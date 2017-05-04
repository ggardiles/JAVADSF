// Clase de cliente que proporciona el API del servicio DFS

package dfs;

import java.io.*;
import java.rmi.*;


public class DFSFicheroCliente  {
    private final DFSCliente dfsCliente;
    private final DFSFicheroServ dfsFicheroServ;
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
        //this.fileInfo = dfs.generateFile(this.callback, nom, modo);

        DFSServicio dfsServicio = dfsCliente.getDfsServicio();
        this.dfsFicheroServ = dfsServicio.getOrCreateDSFFicheroServ(nom, modo);
    }
    public int read(byte[] b) throws RemoteException, IOException {
        if(!isOpen()) {
            throw new IOException("File has not been opened yet");
        }

        int nleidos = 0;
        int tamBloque = dfsCliente.getTamBloque();

        for (int i = 0; i * tamBloque < b.length; i++) {
            byte[] cacheRead = new byte[tamBloque];
            cacheRead = this.dfsFicheroServ.read(cacheRead, pos+tamBloque*i);
            if(cacheRead == null) { // EOF
                break;
            }
            nleidos += tamBloque;
            System.arraycopy(cacheRead, 0, b, tamBloque * i, cacheRead.length);
        }
        System.out.println("Bytes leidos: " + nleidos);
        pos += nleidos;
        return (nleidos>0)?nleidos:-1;
    }
    public void write(byte[] b) throws RemoteException, IOException {
        if(!this.isOpen || !modo.equals("rw")){
            throw new IOException("File not opened or incorrect mode");
        }
        
        int tamBloque = dfsCliente.getTamBloque();
        
        for(int i = 0 ; i * tamBloque < b.length; i++){
            byte[] buffer = new byte[tamBloque];
            dfsFicheroServ.write(b, pos);
        }
        System.out.println("Bytes escritos: "+b.length);
        pos += b.length;
    }
    public void seek(long p) throws RemoteException, IOException {
        dfsFicheroServ.seek(p);
        pos = p;
        System.out.println("Nueva posicion: "+p);

    }
    public void close() throws RemoteException, IOException {
        if(!this.isOpen) {
            throw new IOException("The file has not been opened");
        }
        dfsFicheroServ.close();
        setOpen(false);
        System.out.println("File Closed");
    }

    private boolean isOpen() {
        return isOpen;
    }

    private void setOpen(boolean open) {
        isOpen = open;
    }
}
