//**************************************************************************
// Nombre......: TESPaqueteConmutado.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa un evento que se emitir� cuando un pa-
// ............: quete sea conmutado por un nodo de la red.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.scenario;

/**
 * Esta clase implementa un evento usado para notificar la recuperaci�n de un
 * enlace caido.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSELinkRecovered extends TSimulationEvent {

    /**
     * Crea una nueva instancia de TESEnlaceRecuperado
     * @since 1.0
     * @param inst Instante de tiempo en que se produjo el evento.
     * @param emisor Enlace que gener� el evento.
     * @param id Identificador unico del evento.
     */
    public TSELinkRecovered(Object emisor, long id, long inst) {
        super(emisor, id, inst);
    }

 
    /**
     * Este m�todo obtiene el subtipo del evento, si los hubiese.
     * @return El subtipo del evento.
     * @since 1.0
     */    
    public int obtenerSubtipo() {
        return super.ENLACE_RECUPERADO;
    }

    /**
     * Este m�todo obtiene el nombre del enlace que origin� el evento.
     * @return El nombre del enlace que origin� el evento.
     * @since 1.0
     */    
    public String obtenerNombre() {
        TTopologyElement et = null;
        TTopologyLink ent = null;
        TNode nt = null;
        et = super.obtenerFuente();
        if (et.obtenerTipoElemento() == TTopologyElement.ENLACE) {
            ent = (TTopologyLink) et;
            return ent.obtenerNombre();
        } else if (et.obtenerTipoElemento() == TTopologyElement.NODO) {
            nt = (TNode) et;
            return nt.obtenerNombre();
        }
        return ("");
    }

    /**
     * Este m�todo obtiene un texto con el tipo de evento.
     * @return Un texto con el tipo de evento.
     * @since 1.0
     */    
    public String obtenerNombreTipo() {
        TTopologyElement et = null;
        et = super.obtenerFuente();
        if (et.obtenerTipoElemento() == TTopologyElement.ENLACE) {
            return ("Enlace ");
        } else if (et.obtenerTipoElemento() == TTopologyElement.NODO) {
            return ("Nodo ");
        }
        return ("");
    }

    /**
     * Este m�todo explcia el evento en una l�nea de texto.
     * @return El texto explicando el evento.
     * @since 1.0
     */    
    public String toString() {
        String cad = "";
        cad += "[";
        cad += this.obtenerNombreTipo();
        cad += " ";
        cad += this.obtenerNombre();
        cad += "] ";
        cad += "se ha recuperado.";
        return(cad);
    }
}
