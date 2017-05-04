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
        if(ficheroServHashMap.containsKey(nom)){
            System.out.println("DFSServicioImpl: already opened");
            return ficheroServHashMap.get(nom);
        }
        System.out.println("DFSServicioImpl: Creating new one");
        DFSFicheroServ dfsFicheroServ = new DFSFicheroServImpl(nom, modo);
        ficheroServHashMap.put(nom, dfsFicheroServ);
        return dfsFicheroServ;
    }

}
