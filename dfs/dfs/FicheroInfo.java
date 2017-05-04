// Esta clase representa información de un fichero.
// El enunciado explica más en detalle el posible uso de esta clase.
// Al ser serializable, puede usarse en las transferencias entre cliente
// y servidor.

package dfs;
import java.io.*;

public class FicheroInfo implements Serializable {
    private DFSFicheroServ dsfFicheroServ;
    private long lastMod;
    private boolean isCachePerm;
    
    public FicheroInfo(DFSFicheroServ dsfFicheroServ, long lastMod, boolean isCachePerm) {
        this.dsfFicheroServ = dsfFicheroServ;
        this.lastMod = lastMod;
        this.isCachePerm = isCachePerm;
    }

    public DFSFicheroServ getDfsFile() {
        return dsfFicheroServ;
    }

    public long getLastModification() {
        return lastMod;
    }

    public boolean isCacheAllowed(){
        return isCachePerm;
    }
}
