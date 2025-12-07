package webserver;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import MiniGames.PlayerManager;
import MiniGames.PlayerManagerHelper;

import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.*;

public class WebSocketBridge extends WebSocketServer {

    private static ORB orb;
    private static PlayerManager playerManager;

    // Gestion des sessions de jeu
    private static ConcurrentHashMap<WebSocket, PlayerSession> sessions = new ConcurrentHashMap<>();
    private static ConcurrentLinkedQueue<WebSocket> waitingPlayers = new ConcurrentLinkedQueue<>();
    private static ConcurrentHashMap<String, GameRoom> activeGames = new ConcurrentHashMap<>();

    public WebSocketBridge() {
        super(new InetSocketAddress(8081));
        initCorba();
    }

    private void initCorba() {
        try {
            orb = ORB.init(new String[] {
                    "-ORBInitialHost", "localhost",
                    "-ORBInitialPort", "1050"
            }, null);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            playerManager = PlayerManagerHelper.narrow(ncRef.resolve_str("PlayerManager"));

            System.out.println("✓ CORBA connection established");
        } catch (Exception e) {
            System.err.println("⚠ CORBA connection failed: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connected: " + conn.getRemoteSocketAddress());
        conn.send("CONNECTED");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("WS Received: " + message);
        String[] parts = message.split(" ");
        String command = parts[0];

        try {
            switch (command) {
                case "REGISTER":
                    handleRegister(conn, parts);
                    break;
                case "GET_PLAYERS":
                    handleGetPlayers(conn);
                    break;
                case "FIND_GAME":
                    handleFindGame(conn);
                    break;
                case "PLAY":
                    handlePlay(conn, parts);
                    break;
                case "LEAVE_GAME":
                    handleLeaveGame(conn);
                    break;
                default:
                    conn.send("ERROR Unknown command: " + command);
            }
        } catch (Exception e) {
            conn.send("ERROR " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRegister(WebSocket conn, String[] parts) {
        if (parts.length < 2) {
            conn.send("ERROR Missing player name");
            return;
        }

        String playerName = parts[1];

        // Enregistrement via CORBA
        if (playerManager != null) {
            boolean registered = playerManager.registerPlayer(playerName);
            if (registered) {
                sessions.put(conn, new PlayerSession(playerName));
                conn.send("REGISTERED " + playerName);
                broadcastPlayerCount();
                System.out.println("✓ Player registered via CORBA: " + playerName);
            } else {
                conn.send("ERROR Registration failed");
            }
        } else {
            conn.send("ERROR CORBA not available");
        }
    }

    private void handleGetPlayers(WebSocket conn) {
        if (playerManager != null) {
            String players = playerManager.getOnlinePlayers();
            conn.send("PLAYERS_LIST " + players);
        } else {
            conn.send("PLAYERS_LIST ");
        }
    }

    private void handleFindGame(WebSocket conn) {
        PlayerSession session = sessions.get(conn);
        if (session == null) {
            conn.send("ERROR Not registered");
            return;
        }

        // Chercher un adversaire en attente
        WebSocket opponent = waitingPlayers.poll();

        if (opponent != null && opponent.isOpen()) {
            // Créer une nouvelle partie
            String gameId = UUID.randomUUID().toString();
            PlayerSession opponentSession = sessions.get(opponent);

            GameRoom room = new GameRoom(gameId, conn, opponent, session.playerName, opponentSession.playerName);
            activeGames.put(gameId, room);

            session.currentGameId = gameId;
            session.symbol = 'X';
            opponentSession.currentGameId = gameId;
            opponentSession.symbol = 'O';

            // Notifier les deux joueurs
            conn.send("START X YES");
            opponent.send("START O NO");

            System.out.println("✓ Game started: " + session.playerName + " vs " + opponentSession.playerName);
        } else {
            // Mettre en file d'attente
            waitingPlayers.add(conn);
            conn.send("WAITING");
            System.out.println("⏳ Player waiting: " + session.playerName);
        }
    }

    private void handlePlay(WebSocket conn, String[] parts) {
        if (parts.length < 3) {
            conn.send("ERROR Invalid move format");
            return;
        }

        PlayerSession session = sessions.get(conn);
        if (session == null || session.currentGameId == null) {
            conn.send("ERROR Not in game");
            return;
        }

        GameRoom room = activeGames.get(session.currentGameId);
        if (room == null) {
            conn.send("ERROR Game not found");
            return;
        }

        try {
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);

            String result = room.makeMove(conn, row, col, session.symbol);

            if (result.startsWith("VALID")) {
                // Mouvement valide, notifier les deux joueurs
                WebSocket opponent = (conn == room.player1) ? room.player2 : room.player1;

                conn.send("MOVE " + row + " " + col + " " + session.symbol);
                opponent.send("MOVE " + row + " " + col + " " + session.symbol);

                // Vérifier la victoire
                char winner = room.checkWinner();
                if (winner != ' ') {
                    if (winner == session.symbol) {
                        conn.send("END WIN");
                        opponent.send("END LOSE");
                    } else {
                        conn.send("END LOSE");
                        opponent.send("END WIN");
                    }
                    activeGames.remove(session.currentGameId);
                    cleanupSession(conn);
                    cleanupSession(opponent);
                } else if (room.isFull()) {
                    conn.send("END DRAW");
                    opponent.send("END DRAW");
                    activeGames.remove(session.currentGameId);
                    cleanupSession(conn);
                    cleanupSession(opponent);
                } else {
                    // Changer de tour
                    room.currentTurn = (room.currentTurn == 'X') ? 'O' : 'X';
                }
            } else {
                conn.send("ERROR Invalid move");
            }
        } catch (NumberFormatException e) {
            conn.send("ERROR Invalid coordinates");
        }
    }

    private void handleLeaveGame(WebSocket conn) {
        PlayerSession session = sessions.get(conn);
        if (session != null && session.currentGameId != null) {
            GameRoom room = activeGames.get(session.currentGameId);
            if (room != null) {
                WebSocket opponent = (conn == room.player1) ? room.player2 : room.player1;
                opponent.send("END OPPONENT_LEFT");
                activeGames.remove(session.currentGameId);
            }
            cleanupSession(conn);
        }
    }

    private void cleanupSession(WebSocket conn) {
        PlayerSession session = sessions.get(conn);
        if (session != null) {
            session.currentGameId = null;
            session.symbol = ' ';
        }
        waitingPlayers.remove(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client disconnected: " + conn.getRemoteSocketAddress());
        handleLeaveGame(conn);
        sessions.remove(conn);
        waitingPlayers.remove(conn);
        broadcastPlayerCount();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("✓ WebSocket Bridge ready on ws://localhost:8081");
    }

    private void broadcastPlayerCount() {
        String message = "PLAYER_COUNT " + sessions.size();
        for (WebSocket conn : sessions.keySet()) {
            conn.send(message);
        }
    }

    // Classes internes
    static class PlayerSession {
        String playerName;
        char symbol = ' ';
        String currentGameId = null;

        PlayerSession(String name) {
            this.playerName = name;
        }
    }

    static class GameRoom {
        String gameId;
        WebSocket player1, player2;
        String player1Name, player2Name;
        char[][] board = new char[3][3];
        char currentTurn = 'X';

        GameRoom(String id, WebSocket p1, WebSocket p2, String p1Name, String p2Name) {
            this.gameId = id;
            this.player1 = p1;
            this.player2 = p2;
            this.player1Name = p1Name;
            this.player2Name = p2Name;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = ' ';
                }
            }
        }

        String makeMove(WebSocket player, int row, int col, char symbol) {
            if (row < 0 || row > 2 || col < 0 || col > 2) {
                return "INVALID Out of bounds";
            }
            if (board[row][col] != ' ') {
                return "INVALID Cell occupied";
            }
            if (currentTurn != symbol) {
                return "INVALID Not your turn";
            }

            board[row][col] = symbol;
            return "VALID";
        }

        char checkWinner() {
            // Lignes
            for (int i = 0; i < 3; i++) {
                if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                    return board[i][0];
                }
            }

            // Colonnes
            for (int i = 0; i < 3; i++) {
                if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                    return board[0][i];
                }
            }

            // Diagonales
            if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
                return board[0][0];
            }
            if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
                return board[0][2];
            }

            return ' ';
        }

        boolean isFull() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        new WebSocketBridge().start();
    }
}