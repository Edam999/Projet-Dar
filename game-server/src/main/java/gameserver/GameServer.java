package gameserver;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GameServer {

    private static final int SERVER_PORT = 12345;
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("Game TCP Server started on port " + SERVER_PORT);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket player1 = serverSocket.accept();
                System.out.println("Player 1 connected, waiting for Player 2...");
                Socket player2 = serverSocket.accept();
                System.out.println("Player 2 connected. Starting game session.");

                pool.execute(new GameSession(player1, player2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class GameSession implements Runnable {
        private Socket p1, p2;
        private char[][] board = new char[3][3];

        public GameSession(Socket p1, Socket p2) {
            this.p1 = p1;
            this.p2 = p2;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    board[i][j] = ' ';
        }

        @Override
        public void run() {
            try (BufferedReader in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                 PrintWriter out1 = new PrintWriter(p1.getOutputStream(), true);
                 BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                 PrintWriter out2 = new PrintWriter(p2.getOutputStream(), true)) {

                out1.println("SYMBOL,X");
                out2.println("SYMBOL,O");

                boolean xTurn = true;
                boolean gameOver = false;

                while (!gameOver) {
                    if (xTurn) {
                        out1.println("YOUR_TURN");
                        String move = in1.readLine();
                        if (processMove(move, 'X')) {
                            out2.println("MOVE," + move + ",X");
                            if (checkVictory('X')) {
                                out1.println("RESULT,WIN");
                                out2.println("RESULT,LOSE");
                                gameOver = true;
                            } else if (isDraw()) {
                                out1.println("RESULT,DRAW");
                                out2.println("RESULT,DRAW");
                                gameOver = true;
                            }
                        } else {
                            out1.println("INVALID_MOVE");
                            continue;
                        }
                    } else {
                        out2.println("YOUR_TURN");
                        String move = in2.readLine();
                        if (processMove(move, 'O')) {
                            out1.println("MOVE," + move + ",O");
                            if (checkVictory('O')) {
                                out2.println("RESULT,WIN");
                                out1.println("RESULT,LOSE");
                                gameOver = true;
                            } else if (isDraw()) {
                                out1.println("RESULT,DRAW");
                                out2.println("RESULT,DRAW");
                                gameOver = true;
                            }
                        } else {
                            out2.println("INVALID_MOVE");
                            continue;
                        }
                    }
                    xTurn = !xTurn;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { p1.close(); p2.close(); } catch (IOException ignored) {}
            }
        }

        private boolean processMove(String move, char symbol) {
            try {
                String[] parts = move.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != ' ') {
                    return false;
                }
                board[row][col] = symbol;
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean checkVictory(char symbol) {
            for (int i = 0; i < 3; i++) {
                if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) return true;
                if (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol) return true;
            }
            if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) return true;
            if (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol) return true;
            return false;
        }

        private boolean isDraw() {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (board[i][j] == ' ') return false;
            return true;
        }
    }
}