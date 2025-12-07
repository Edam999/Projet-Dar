package webserver;

import fi.iki.elonen.NanoWSD;

import java.io.IOException;

public class WebSocketServer extends NanoWSD {

    public WebSocketServer(int port) throws IOException {
        super(port);
        start();
        System.out.println("WebSocket server running at ws://localhost:" + port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new MiniGameWebSocket(handshake);
    }

    public static void main(String[] args) {
        try {
            new WebSocketServer(8081); // Port WebSocket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}