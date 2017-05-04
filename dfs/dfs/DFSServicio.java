// Interfaz del servicio DFS

package dfs;
import java.io.IOException;
import java.rmi.*;

public interface DFSServicio extends Remote {

    FicheroInfo getOrCreateFicheroInfo(String nom, String modo)
            throws RemoteException, IOException;

    void removeFromHashmap(String nom) throws RemoteException;
}       
