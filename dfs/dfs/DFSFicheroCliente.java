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
    private final FicheroInfo ficheroInfo;
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
        this.ficheroInfo = this.dfsServicio.getOrCreateFicheroInfo(nom, modo);
        this.dfsFicheroServ = this.ficheroInfo.getDfsFile();

        dfsCliente.invalidateCacheIfInvalid(nom, this.ficheroInfo.getLastModification());
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
                System.out.println("FICHERO CLIENTE: Getting from cache pos="+tamBloque*i);
                cacheRead = dfsCliente.getFromCache(nom, tamBloque * i);
            }else{ // No en cache -> Leer de fichero y guardar en cache
                System.out.println("FICHERO CLIENTE: Getting from FILE pos="+tamBloque*i);
                cacheRead = this.dfsFicheroServ.read(cacheRead, tamBloque*i);
                Bloque bloquePendiente =
                        dfsCliente.saveInCache(nom, tamBloque * i, cacheRead, false);

                // Si hay bloques modificados hay que lelvarlos a fichero
                if (bloquePendiente != null){
                    dfsFicheroServ.write(bloquePendiente.obtenerContenido(), bloquePendiente.obtenerId());
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
            Bloque bloquePendiente =
                    dfsCliente.saveInCache(nom, tamBloque * i, buffer, true);
            if(bloquePendiente != null){
                dfsFicheroServ.write(bloquePendiente.obtenerContenido(),
                        bloquePendiente.obtenerId());
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
            System.out.println("CLOSE: Escrito a fichero bloque en pos="+bloque.obtenerId());
        }
        dfsServicio.removeFromHashmap(nom+modo);
        dfsCliente.updateCacheDate(nom, dfsFicheroServ.getLastModDate());
        dfsFicheroServ.close();
        setOpen(false);
        System.out.println("CLOSE: DFSFicheroServ Closed");
    }

    private boolean isOpen() {
        return isOpen;
    }

    private void setOpen(boolean open) {
        isOpen = open;
    }
}
