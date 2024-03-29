// Clase de servidor que implementa el servicio DFS

package dfs;

import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;

public class DFSServicioImpl extends UnicastRemoteObject implements DFSServicio {
    private HashMap<String, DFSFicheroServ> ficheroServHashMap;

    public DFSServicioImpl() throws RemoteException {
        this.ficheroServHashMap = new HashMap<String, DFSFicheroServ>();
    }

    public FicheroInfo generarFichero(String nom, String modo)
            throws RemoteException, IOException {

        DFSFicheroServ dfsFicheroServ = getOrCreateDSFFicheroServ(nom, modo);

        return null;
    }

    public DFSFicheroServ getOrCreateDSFFicheroServ(String nom, String modo)
            throws RemoteException, IOException {
        if(ficheroServHashMap.containsKey(nom+modo)){
            System.out.println("DFSServicioImpl: already opened DSFFicheroServ");
            return ficheroServHashMap.get(nom+modo);
        }
        System.out.println("DFSServicioImpl: Creating new DSFFicheroServ");
        DFSFicheroServ dfsFicheroServ = new DFSFicheroServImpl(nom, modo);
        ficheroServHashMap.put(nom+modo, dfsFicheroServ);
        return dfsFicheroServ;
    }

    public void removeFromHashmap(String nom) throws RemoteException{
        this.ficheroServHashMap.remove(nom);
    }

}
