package MiniGames;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPClient implements Runnable {

    private int port;

    public UDPClient(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[1024];
            System.out.println("UDP Client listening on port " + port);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received UDP message: " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}