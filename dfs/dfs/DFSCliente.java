// Clase de cliente que proporciona acceso al servicio DFS

package dfs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DFSCliente {
    private final int tamBloque;
    private final int tamCache;

    private DFSServicio dfsServicio;
    private HashMap<String, Cache> caches;

    public DFSCliente(int tamBloque, int tamCache) {
        // Set up global variables
        this.tamBloque = tamBloque;
        this.tamCache = tamCache;
        this.caches = new HashMap<String, Cache>();

        // Build up Java RMI URL
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

    public void createCacheOrIgnore(String nom, String modo)
            throws RemoteException, IOException {

        DFSFicheroServ dfsFicheroServ =
                this.dfsServicio.getOrCreateDSFFicheroServ(nom, modo);

        // Cache available
        if(caches.containsKey(nom)){
            System.out.println("Cache ya existente para "+nom);
            Cache cache = caches.get(nom);
            FicheroInfo ficheroInfo = new FicheroInfo(dfsFicheroServ, )
            return;
        }

        System.out.println("Cache no encontrada para "+nom+" -> se crea.");
        Cache cache = new Cache(tamCache);
        cache.fijarFecha(System.currentTimeMillis());
        caches.put(nom, cache);
    }

    public boolean isInCache(String nom, long pos){
        Cache cache = caches.get(nom);
        if (cache == null) {
            return false;
        }
        return cache.getBloque(pos) != null;
    }

    public byte[] getFromCache(String nom, long pos) {
        if (!isInCache(nom, pos)) {
            System.out.println("Cache no contiene " + nom + " pos="+pos);
            return null;
        }

        System.out.print("Cache contiene " + nom + " pos="+pos);
        Cache cache = caches.get(nom);
        Bloque bloque = cache.getBloque(pos);
        return bloque.obtenerContenido();
    }

    public List<Bloque> saveInCache (String nom, long pos, byte[] b, boolean mod){
        System.out.println("SAVE in cache "+nom+" bloque="+pos+" mod="+((mod)?"true":"false"));


        Cache cache = caches.get(nom);
        Bloque bloque = new Bloque(pos,b);
        if (mod){
            cache.activarMod(bloque);
        }

        Bloque savedBloque = cache.putBloque(bloque);
        List<Bloque> bloqueList = new ArrayList<Bloque>();

        while (savedBloque != null
                && cache.preguntarYDesactivarMod(savedBloque)){
            bloqueList.add(savedBloque);
            savedBloque= cache.putBloque(savedBloque);
        }

        return bloqueList;

    }

    public List<Bloque> removeAllModified(String nom) {
        Cache cache = caches.get(nom);
        List<Bloque> bloqueList = new ArrayList<Bloque>(cache.listaMod());
        cache.vaciarListaMod();
        System.out.println("Elementos modificados: "+bloqueList.size());
        return bloqueList;
    }

    public void updateCacheDate(String nom, long ultimaModificacion) {
        Cache cache = caches.get(nom);
        cache.fijarFecha(ultimaModificacion);
        System.out.println("Nueva fecha cache " + cache.obtenerFecha()+" del fichero: "+nom);
    }

    public int getTamBloque() throws RemoteException{
        return this.tamBloque;
    }

    public DFSServicio getDfsServicio() throws RemoteException{
        return this.dfsServicio;
    }
}

