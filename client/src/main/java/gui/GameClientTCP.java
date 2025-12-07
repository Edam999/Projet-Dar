package gui;

import java.io.*;
import java.net.Socket;

public class GameClientTCP implements Runnable {

    private String host;
    private int port;
    private TicTacToeGUI gui;
    private PrintWriter out;

    public GameClientTCP(String host, int port, TicTacToeGUI gui) {
        this.host = host;
        this.port = port;
        this.gui = gui;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("SYMBOL")) {
                    gui.setMySymbol(parts[1].charAt(0));
                } else if (parts[0].equals("YOUR_TURN")) {
                    gui.setMyTurn(true);
                } else if (parts[0].equals("MOVE")) {
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    char symbol = parts[3].charAt(0);
                    javafx.application.Platform.runLater(() -> gui.updateMove(row, col, symbol));
                } else if (parts[0].equals("RESULT")) {
                    javafx.application.Platform.runLater(() -> gui.showResult(parts[1]));
                } else if (parts[0].equals("INVALID_MOVE")) {
                    javafx.application.Platform.runLater(() -> gui.showInvalidMove());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int row, int col, char symbol) {
        if (out != null) {
            out.println(row + "," + col);
        }
    }
}