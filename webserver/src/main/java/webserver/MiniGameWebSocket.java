package webserver;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.IOException;

public class MiniGameWebSocket extends NanoWSD.WebSocket {

    public MiniGameWebSocket(NanoHTTPD.IHTTPSession handshakeRequest) {
        super(handshakeRequest);
    }

    @Override
    protected void onOpen() {
        System.out.println("WebSocket ouvert: " + this);
        try {
            send("Bienvenue sur GameHub!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame message) {
        System.out.println("Message reçu: " + message.getTextPayload());
        try {
            send("Reçu: " + message.getTextPayload());
            // Ici tu peux appeler ton backend CORBA/TCP/UDP
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        System.out.println("WebSocket fermé: " + reason);
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pong) { }

    @Override
    protected void onException(IOException exception) {
        exception.printStackTrace();
    }
}