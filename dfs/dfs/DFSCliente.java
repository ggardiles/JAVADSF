// Clase de cliente que proporciona acceso al servicio DFS

package dfs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class DFSCliente {
    private final int tamBloque;
    private final int tamCache;

    private DFSServicio dfsServicio;

    public DFSCliente(int tamBloque, int tamCache) {
        this.tamBloque = tamBloque;
        this.tamCache = tamCache;

        String rmiUrl = "//"+System.getenv("SERVIDOR")+
                ":" + System.getenv("PUERTO") + "/DFS";
        try {
            this.dfsServicio = ((DFSServicio) Naming.lookup(rmiUrl));
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getTamBloque() throws RemoteException{
        return this.tamBloque;
    }

    public DFSServicio getDfsServicio() throws RemoteException{
        return this.dfsServicio;
    }
}

