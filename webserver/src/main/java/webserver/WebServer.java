package webserver;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebServer extends NanoHTTPD {

    public WebServer() throws IOException {
        super(8080); // Serveur HTTP sur port 8080
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Web server running at: http://localhost:8080");
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            String uri = session.getUri();
            System.out.println("GET " + uri);

            if (uri.equals("/")) uri = "/index.html";

            // Charger fichier static depuis src/main/resources/static
            String path = "webserver/src/main/resources/static" + uri;

            if (!Files.exists(Paths.get(path))) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
            }

            String content = new String(Files.readAllBytes(Paths.get(path)));
            return newFixedLengthResponse(Response.Status.OK, getMimeType(uri), content);

        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse("Internal server error");
        }
    }

    private String getMimeType(String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".js")) return "application/javascript";
        return "text/plain";
    }

    public static void main(String[] args) {
        try {
            new WebServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}