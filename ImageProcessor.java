package rmi_package;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Base64;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageImplement extends UnicastRemoteObject implements ImageProcessor {

    protected ImageImplement() throws RemoteException {
        super();
    }

    // Implementarea metodei pentru prelucrarea imaginii
    @Override
    public void processImage(String base64Image) throws RemoteException {
        try {
            // Decodificăm imaginea din Base64
            byte[] imageData = Base64.getDecoder().decode(base64Image);

            // Salvăm imaginea ca fișier pe server
            try (FileOutputStream fos = new FileOutputStream("received_image_part1.jpg")) {
                fos.write(imageData);
                System.out.println("Prima jumătate a imaginii a fost procesată și salvată!");
            }
        } catch (IOException e) {
            System.err.println("Eroare la procesarea imaginii: " + e.getMessage());
        }
    }
}

public interface ImageProcessor extends Remote {
    void processImage(String base64Image) throws RemoteException;
}

public class ImageImplement extends UnicastRemoteObject implements ImageProcessor {

    protected ImageImplement() throws RemoteException {
        super();
    }

    // Implementarea metodei pentru prelucrarea imaginii
    @Override
    public void processImage(String base64Image) throws RemoteException {
        try {
            // Decodificăm imaginea din Base64
            byte[] imageData = Base64.getDecoder().decode(base64Image);

            // Salvăm imaginea ca fișier pe server
            try (FileOutputStream fos = new FileOutputStream("received_image_part1.jpg")) {
                fos.write(imageData);
                System.out.println("Prima jumătate a imaginii a fost procesată și salvată!");
            }
        } catch (IOException e) {
            System.err.println("Eroare la procesarea imaginii: " + e.getMessage());
        }
    }
}
public class ImageServer {

    public static void main(String[] args) {
        try {
            // Creăm un registry RMI pe portul 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("Serverul RMI este initializat la portul 1099");

            // Creăm instanța implementării ImageProcessor
            ImageImplement imageImplement = new ImageImplement();

            // Înregistrăm implementarea la serverul RMI
            Naming.rebind("rmi://localhost:1099/ImageProcessor", imageImplement);

            System.out.println("Serverul c04 este acum disponibil si asteapta conexiuni...");
        } catch (Exception e) {
            System.err.println("Eroare la înregistrarea serverului RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
