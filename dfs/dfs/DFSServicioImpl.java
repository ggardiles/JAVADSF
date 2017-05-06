// Clase de servidor que implementa el servicio DFS

package dfs;

import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;

public class DFSServicioImpl extends UnicastRemoteObject implements DFSServicio {
    private HashMap<String, FicheroInfo> ficheroInfoHashMap;

    public DFSServicioImpl() throws RemoteException {
        this.ficheroInfoHashMap = new HashMap<String, FicheroInfo>();
    }

    public FicheroInfo getOrCreateFicheroInfo(String nom, String modo)
            throws RemoteException, IOException {
        /*
        if(ficheroInfoHashMap.containsKey(nom+modo)){
            System.out.println("DFSServicioImpl: already opened FicheroInfo");
            return ficheroInfoHashMap.get(nom+modo);
        }
        */
        System.out.println("DFSServicioImpl: Creating new FicheroInfo");
        DFSFicheroServ dfsFicheroServ = new DFSFicheroServImpl(nom, modo);
        FicheroInfo ficheroInfo = new FicheroInfo(dfsFicheroServ, dfsFicheroServ.getLastModDate(), true);
        ficheroInfoHashMap.put(nom+modo, ficheroInfo);
        return ficheroInfo;
    }

    public void removeFromHashmap(String nom) throws RemoteException{
        this.ficheroInfoHashMap.remove(nom);
    }

}
