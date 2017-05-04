// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.*;
import java.rmi.*;
import java.util.List;


public class DFSFicheroCliente  {
    private final DFSCliente dfsCliente;
    private final DFSFicheroServ dfsFicheroServ;
    private final DFSServicio dfsServicio;
    private final String nom;
    private final String modo;
    private long pos;
    private boolean isOpen;

    public DFSFicheroCliente(DFSCliente dfs, String nom, String modo)
      throws RemoteException, IOException, FileNotFoundException {
        this.dfsCliente = dfs;
        this.nom = nom;
        this.modo = modo;
        this.setOpen(true);
        this.pos = 0L;
        //this.callback = new DFSFicheroCallbackImpl(this);

        // Crear cache si no existe
        dfs.createCacheOrIgnore(nom, modo);

        this.dfsServicio = dfsCliente.getDfsServicio();
        this.dfsFicheroServ = this.dfsServicio.getOrCreateDSFFicheroServ(nom, modo);
    }
    public int read(byte[] b) throws RemoteException, IOException {
        if(!isOpen()) {
            throw new IOException("File has not been opened yet");
        }

        int nleidos = 0;
        int tamBloque = dfsCliente.getTamBloque();

        for (int i = 0; i * tamBloque < b.length; i++) {
            byte[] cacheRead = new byte[tamBloque];

            if (dfsCliente.isInCache(nom, tamBloque * i)){ // En cache
                cacheRead = dfsCliente.getFromCache(nom, tamBloque * i);
            }else{ // No en cache -> Leer de fichero y guardar en cache
                cacheRead = this.dfsFicheroServ.read(cacheRead, pos+tamBloque*i);
                List<Bloque> bloquesPendientes =
                        dfsCliente.saveInCache(nom, tamBloque * i, cacheRead, false);
                for(Bloque bloque: bloquesPendientes){
                    dfsFicheroServ.write(bloque.obtenerContenido(), bloque.obtenerId());
                }
            }

            if(cacheRead == null) { // EOF
                System.out.println("READ EOF");
                break;
            }
            nleidos += tamBloque;
            System.arraycopy(cacheRead, 0, b, tamBloque * i, cacheRead.length);
        }
        pos += nleidos;
        System.out.println("READ: Bytes leidos: "+nleidos+ " pos="+pos);
        return (nleidos>0)?nleidos:-1;
    }


    public void write(byte[] b) throws RemoteException, IOException {
        if(!this.isOpen || !modo.equals("rw")){
            throw new IOException("File not opened or incorrect mode");
        }
        
        int tamBloque = dfsCliente.getTamBloque();
        
        for(int i = 0 ; i * tamBloque < b.length; i++){
            byte[] buffer = new byte[tamBloque];
            System.arraycopy(b, tamBloque * i , buffer, 0, tamBloque);
            List<Bloque> bloquesPendientes =
                    dfsCliente.saveInCache(nom, tamBloque * i, buffer, true);
            for(Bloque bloque: bloquesPendientes){
                dfsFicheroServ.write(bloque.obtenerContenido(), bloque.obtenerId());
            }
            //dfsFicheroServ.write(b, pos);
        }
        pos += b.length;
        System.out.println("WRITE: Bytes escritos: "+b.length+ " pos="+pos);
    }

    public void seek(long p) throws RemoteException, IOException {
        if(!this.isOpen) {
            throw new IOException("The file has not been opened");
        }

        dfsFicheroServ.seek(p);
        pos = p;
        System.out.println("SEEK: Nueva posicion: "+p);

    }
    public void close() throws RemoteException, IOException {
        if(!this.isOpen) {
            throw new IOException("The file has not been opened");
        }
        List<Bloque> modified = dfsCliente.removeAllModified(nom);
        for(Bloque bloque : modified){
            dfsFicheroServ.write(bloque.obtenerContenido(),bloque.obtenerId());
        }
        dfsServicio.removeFromHashmap(nom+modo);
        dfsFicheroServ.close();
        setOpen(false);
        dfsCliente.updateCacheDate(nom, dfsFicheroServ.getLastModDate());
        System.out.println("CLOSE: DFSFicheroServ Closed");
    }

    private boolean isOpen() {
        return isOpen;
    }

    private void setOpen(boolean open) {
        isOpen = open;
    }
}
