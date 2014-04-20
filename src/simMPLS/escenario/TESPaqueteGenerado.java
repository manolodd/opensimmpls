//**************************************************************************
// Nombre......: TESPaqueteGenerado.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un evento que se emitirá cuando un no-
// ............: do genere un paquete nuevo.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import simMPLS.protocolo.*;

/**
 * Esta clase implementa un evento que será usado para notificar que se ha generado
 * un paquete en un nodo.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TESPaqueteGenerado extends TEventoSimulacion {

    /**
     * Crea una nueva instancia de TESPaqueteGenerado
     * @since 1.0
     * @param inst Instante de tiempo en el que se ha producido el evento.
     * @param tam Tamaño del paquete al que se refiere el evento.
     * @param emisor Nodo que generó el evento.
     * @param id Identificador único del evento.
     * @param tipoPaquete Tipo del paquete al que se refiere el evento.
     */
    public TESPaqueteGenerado(Object emisor, long id, long inst, int tipoPaquete, int tam) {
        super(emisor, id, inst);
        tipoP = tipoPaquete;
        tamanio = tam;
    }

    /**
     * Este método obtiene el tipo del paquete al que se refiere el evento.
     * @return Tipo del paquete al que se refiere el evento.
     * @since 1.0
     */    
    public int obtenerTipoPaquete() {
        return tipoP;
    }

    /**
     * Este método obtiene el subtipo del evento, si los hubiese.
     * @return El subtipo del evento.
     * @since 1.0
     */    
    public int obtenerSubtipo() {
        return super.PAQUETE_GENERADO;
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
     * Este método obtiene una representación textual del tipo del paquete al que se
     * refiere el evento.
     * @return El tipo del paquete al que se refiere el evento, expresado en texto.
     * @since 1.0
     */    
    public String obtenerNombreTipoPaquete() {
        String strTipo = "";
        switch (tipoP) {
            case TPDU.IPV4: {
                strTipo = "IPv4";
                break;
            }
            case TPDU.IPV4_GOS: {
                strTipo = "IPv4 con GoS";
                break;
            }
            case TPDU.MPLS: {
                strTipo = "MPLS";
                break;
            }
            case TPDU.MPLS_GOS: {
                strTipo = "MPLS con GoS";
                break;
            }
            case TPDU.TLDP: {
                strTipo = "LDP";
                break;
            }
            case TPDU.GPSRP: {
                strTipo = "GPSRP";
                break;
            }
        }
        return(strTipo);
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
        cad += "ha generado un paquete ";
        cad += this.obtenerNombreTipoPaquete();
        cad += " de tamaño ";
        cad += this.tamanio;
        cad += " octetos";
        return(cad);
    }

    private int tipoP;
    private int tamanio;
}
