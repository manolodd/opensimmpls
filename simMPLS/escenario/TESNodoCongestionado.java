//**************************************************************************
// Nombre......: TESPaqueteEnTránsito.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un evento que se emitirá cuando un pa-
// ............: quete circule por el interior de un enlace.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import simMPLS.protocolo.*;

/**
 * Esta clase implemetna un evento que será usado para notificar el nivel de
 * congestión de un nodo.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TESNodoCongestionado extends TEventoSimulacion {

    /**
     * Crea una nueva instancia de TESNodoCongestionado
     * @since 1.0
     * @param inst Instante de tiempo en el que se generó el evento.
     * @param pc Porcentaje de congestión del nodo (0%,..,100%)
     * @param emisor Nodo que generó el evento.
     * @param id identificador unico del evento.
     */
    public TESNodoCongestionado(Object emisor, long id, long inst, long pc) {
        super(emisor, id, inst);
        porcentajeCongestion = pc;
    }

    /**
     * Este método devuelve el porcentaje de congestión que indica el evento.
     * @return Porcentaje de congestión del nodo que generó el evento.
     * @since 1.0
     */    
    public long obtenerPorcentajeCongestion() {
        return this.porcentajeCongestion;
    }
    
    /**
     * Este método obtiene el subtipo del evento, si los hubiese.
     * @return El subtipo del evento.
     * @since 1.0
     */    
    public int obtenerSubtipo() {
        return super.NODO_CONGESTIONADO;
    }

    /**
     * Este método obtiene el nombre del enlace que originó el evento.
     * @return El nombre del enlace que originó el evento.
     * @since 1.0
     */    
    public String obtenerNombre() {
        TElementoTopologia et = null;
        TEnlaceTopologia ent = null;
        TNodoTopologia nt = null;
        et = super.obtenerFuente();
        if (et.obtenerTipoElemento() == TElementoTopologia.ENLACE) {
            ent = (TEnlaceTopologia) et;
            return ent.obtenerNombre();
        } else if (et.obtenerTipoElemento() == TElementoTopologia.NODO) {
            nt = (TNodoTopologia) et;
            return nt.obtenerNombre();
        }
        return ("");
    }

    /**
     * Este método obtiene un texto con el tipo de evento.
     * @return Un texto con el tipo de evento.
     * @since 1.0
     */    
    public String obtenerNombreTipo() {
        TElementoTopologia et = null;
        et = super.obtenerFuente();
        if (et.obtenerTipoElemento() == TElementoTopologia.ENLACE) {
            return ("Enlace ");
        } else if (et.obtenerTipoElemento() == TElementoTopologia.NODO) {
            return ("Nodo ");
        }
        return ("");
    }

    /**
     * Este método explcia el evento en una línea de texto.
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
        cad += "está congestionado al ";
        cad += this.porcentajeCongestion + "%";
        return(cad);
    }

    private long porcentajeCongestion;
}
