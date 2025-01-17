package rmi_package;

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
