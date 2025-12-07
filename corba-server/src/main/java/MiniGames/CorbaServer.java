package MiniGames;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.CosNaming.*;

public class CorbaServer {

    public static void main(String[] args) {
        try {
            // Initialisation ORB
            ORB orb = ORB.init(new String[] {
                    "-ORBInitialHost", "localhost",
                    "-ORBInitialPort", "1050"
            }, null);

            // Récupération du RootPOA
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // Instanciation de notre implémentation
            PlayerManagerImpl managerImpl = new PlayerManagerImpl();

            // Création de la référence CORBA
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(managerImpl);
            PlayerManager href = PlayerManagerHelper.narrow(ref);

            // Enregistrement dans le Naming Service
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name("PlayerManager");
            ncRef.rebind(path, href);

            System.out.println("CORBA Server ready...");
            orb.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}