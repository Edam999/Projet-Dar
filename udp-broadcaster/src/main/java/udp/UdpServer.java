package udp;

import java.net.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UdpServer {

    private static final int UDP_PORT = 9876;
    private DatagramSocket socket;
    private volatile boolean running = true;
    private ScheduledExecutorService scheduler;

    public UdpServer() throws SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        System.out.println("✓ UDP Broadcast Server started on port " + UDP_PORT);

        // Broadcast périodique toutes les 5 secondes
        scheduler.scheduleAtFixedRate(() -> {
            try {
                broadcastMessage("GAME_AVAILABLE TicTacToe");
            } catch (IOException e) {
                System.err.println("UDP broadcast error: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);

        // Écoute des messages UDP entrants
        listenForMessages();
    }

    private void listenForMessages() {
        byte[] buffer = new byte[1024];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("UDP received: " + message + " from " +
                        packet.getAddress().getHostAddress());

                handleUdpMessage(message, packet.getAddress(), packet.getPort());

            } catch (IOException e) {
                if (running) {
                    System.err.println("UDP receive error: " + e.getMessage());
                }
            }
        }
    }

    private void handleUdpMessage(String message, InetAddress address, int port) {
        try {
            if (message.startsWith("DISCOVER")) {
                // Répondre avec les informations du serveur
                String response = "SERVER_INFO GameHub localhost:8081";
                byte[] responseData = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length, address, port
                );
                socket.send(responsePacket);
                System.out.println("Sent server info to " + address.getHostAddress());
            }
        } catch (IOException e) {
            System.err.println("Error sending UDP response: " + e.getMessage());
        }
    }

    private void broadcastMessage(String message) throws IOException {
        byte[] buffer = message.getBytes();

        // Broadcast local
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length,
                InetAddress.getByName("255.255.255.255"), UDP_PORT
        );
        socket.send(packet);

        System.out.println("UDP broadcast: " + message);
    }

    public void stop() {
        running = false;
        scheduler.shutdown();
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("UDP Server stopped");
    }

    public static void main(String[] args) {
        try {
            UdpServer server = new UdpServer();
            server.start();

            // Hook pour arrêt propre
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down UDP server...");
                server.stop();
            }));

        } catch (SocketException e) {
            System.err.println("Failed to start UDP server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}