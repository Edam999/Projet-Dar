package MiniGames;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

public class CorbaClient {

    public static void main(String[] args) {
        try {
            int udpPort = 9876;
            Thread udpThread = new Thread(new UDPClient(udpPort));
            udpThread.start();
            // Initialisation ORB avec le même host/port que le serveur
            ORB orb = ORB.init(new String[] {
                    "-ORBInitialHost", "localhost",
                    "-ORBInitialPort", "1050"
            }, null);

            // Récupération du Naming Service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Recherche de notre serveur CORBA
            PlayerManager manager = PlayerManagerHelper.narrow(ncRef.resolve_str("PlayerManager"));

            // ---- TEST 1 : Enregistrer un joueur ----
            String playerName = "Ali";
            boolean ok = manager.registerPlayer(playerName);
            System.out.println("Register player '" + playerName + "' : " + ok);

            // ---- TEST 2 : Récupérer la liste des joueurs ----
            String online = manager.getOnlinePlayers();
            System.out.println("Online players: " + online);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}