// Interfaz del servicio DFS

package dfs;
import java.io.IOException;
import java.rmi.*;

public interface DFSServicio extends Remote {

    FicheroInfo generarFichero(String nom, String modo)
            throws RemoteException, IOException;

    DFSFicheroServ getOrCreateDSFFicheroServ(String nom, String modo)
            throws RemoteException, IOException;

}       
