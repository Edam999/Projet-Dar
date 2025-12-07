package gameserver;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {

    public static void main(String[] args) {
        String host = "localhost"; // serveur TCP
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);

                if (line.contains("À vous de jouer")) {
                    String move = scanner.nextLine();
                    out.println(move);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}