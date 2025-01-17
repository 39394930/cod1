package rmi_package;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ImageServer {

    public static void main(String[] args) {
        try {
            // Creăm un registry RMI pe portul 1099
            LocateRegistry.createRegistry(1100);
            System.out.println("Serverul RMI este initializat la portul 1100");

            // Creăm instanța implementării ImageProcessor
            ImageImplement imageImplement = new ImageImplement();

            // Înregistrăm implementarea la serverul RMI
            Naming.rebind("rmi://localhost:1100/ImageProcessor", imageImplement);

            System.out.println("Serverul c05 este acum pornit si asteapta conexiuni...");
        } catch (Exception e) {
            System.err.println("Eroare la înregistrarea serverului RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
