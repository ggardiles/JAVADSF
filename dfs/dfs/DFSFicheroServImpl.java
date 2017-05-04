// Clase de servidor que implementa el API de acceso remoto a un fichero

package dfs;
import java.rmi.*;
import java.rmi.server.*;

import java.io.*;

public class DFSFicheroServImpl extends UnicastRemoteObject implements DFSFicheroServ {
    private static final String DFSDir = "DFSDir/";
    private RandomAccessFile randomAccessFile;
    private File file;

    public DFSFicheroServImpl(String nom, String modo)
            throws IOException {

        // Opening File
        File file = new File(DFSDir + nom);
        if (file.exists()) {
            System.out.println("DFSFicheroServImpl: already exists " + nom +" opening");
            this.file = file;
            this.randomAccessFile = new RandomAccessFile(file, "rw");
            return;
        }

        // File does not exist
        if(modo.equals("r")) { // Si lectura
            throw new IOException("File does not exist. Cannot read it.");
        }
        if (!file.createNewFile()){ // Si escritura -> crearlo
            throw new IOException("Could not create file");
        }
        System.out.println("DFSFicheroServImpl: created file " + nom);
        this.file = file;
        this.randomAccessFile = new RandomAccessFile(file, "rw");
    }

    @Override
    public byte[] read(byte[] b, long pos) throws IOException, RemoteException {
        seek(pos);
        int nleidos = randomAccessFile.read(b);
        System.out.println("READ: Bytes leidos: " + nleidos + " pos="+pos);
        return nleidos > 0 ? b:null;
    }

    @Override
    public void write(byte[] b, long pos) throws IOException, RemoteException {
        seek(pos);
        randomAccessFile.write(b);
        System.out.println("WRITE: Bytes Escritos: " + b.length+ " pos="+pos);
    }

    @Override
    public void seek(long p) throws RemoteException, IOException {
        randomAccessFile.seek(p);
        System.out.println("SEEK: Nueva Posicion: " + p);
    }

    @Override
    public void close() throws IOException, RemoteException {
        randomAccessFile.close();
        System.out.println("CLOSE: Fichero Cerrado");
    }

    @Override
    public long getLastModDate() throws IOException, RemoteException {
        return this.file.lastModified();
    }
}
