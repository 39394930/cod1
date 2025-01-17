package rmi_package;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ImageProcessor extends Remote {
    void processImage(String base64Image) throws RemoteException;
}
