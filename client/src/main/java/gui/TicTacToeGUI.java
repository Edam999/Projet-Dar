package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class TicTacToeGUI extends Application {

    private Button[][] buttons = new Button[3][3];
    private char mySymbol = 'X';
    private boolean myTurn = false;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        GridPane gridPane = new GridPane();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button(" ");
                btn.setPrefSize(100, 100);
                final int row = i;
                final int col = j;
                btn.setOnAction(e -> handleClick(row, col));
                buttons[i][j] = btn;
                gridPane.add(btn, j, i);
            }
        }

        root.getChildren().add(gridPane);
        Scene scene = new Scene(root, 320, 350);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic-Tac-Toe");
        primaryStage.show();

        new Thread(this::startClient).start();
    }

    private void handleClick(int row, int col) {
        if (!myTurn || !buttons[row][col].getText().equals(" ")) return;

        buttons[row][col].setText(String.valueOf(mySymbol));
        myTurn = false;
        sendMove(row, col);
    }

    private void startClient() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                switch (parts[0]) {
                    case "SYMBOL":
                        setMySymbol(parts[1].charAt(0));
                        break;
                    case "YOUR_TURN":
                        setMyTurn(true);
                        break;
                    case "MOVE":
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        char symbol = parts[3].charAt(0);
                        updateMove(row, col, symbol);
                        break;
                    case "RESULT":
                        showResult(parts[1]);
                        break;
                    case "INVALID_MOVE":
                        showInvalidMove();
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthodes publiques pour GameClientTCP
    public void setMySymbol(char symbol) {
        this.mySymbol = symbol;
    }

    public void setMyTurn(boolean turn) {
        this.myTurn = turn;
    }

    public void updateMove(int row, int col, char symbol) {
        Platform.runLater(() -> buttons[row][col].setText(String.valueOf(symbol)));
    }

    public void showResult(String result) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Fin de la partie");
            alert.setContentText(result);
            alert.showAndWait();
        });
    }

    public void showInvalidMove() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Coup invalide");
            alert.setContentText("Veuillez choisir une case vide !");
            alert.showAndWait();
        });
    }

    private void sendMove(int row, int col) {
        if (out != null) {
            out.println(row + "," + col);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}