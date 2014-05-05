//**************************************************************************
// Nombre......: TSEPacketSent.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa un evento que se emitir� cuando un nodo
// ............: ponga en un puerto un paquete.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.scenario;

import simMPLS.protocols.TPDU;

/**
 * Esta clase implementa un evento que ser� usado para notificar que un paquete ha
 * salido de un nodo.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSEPacketSent extends TSimulationEvent {

    /**
     * Crea una nueva instancia de TESPaqueteEnviado
     * @since 1.0
     * @param inst Instante de tiempo en el que se produjo el evento.
     * @param emisor Nodo que gener� el evento.
     * @param id Identificador �nico del evento.
     * @param tipoPaquete Tipo e paquete al que se refiere el evento.
     */
    public TSEPacketSent(Object emisor, long id, long inst, int tipoPaquete) {
        super(emisor, id, inst);
        tipoP = tipoPaquete;
    }

    /**
     * ste m�todo obtiene el tipo del paquete al que se refiere el evento.
     * @return El tipo de paquete al que se refiere el evento.
     * @since 1.0
     */    
    public int obtenerTipoPaquete() {
        return tipoP;
    }

    /**
     * Este m�todo obtiene el subtipo del evento, si los hubiese.
     * @return El subtipo del evento.
     * @since 1.0
     */    
    public int obtenerSubtipo() {
        return super.PAQUETE_ENVIADO;
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
     * Este m�todo obtiene una representaci�no textual del tipo del paquete al que se
     * refiere el evento.
     * @return El tipo el paquete al que se refiere el evento, expresado como texto.
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
        cad += "ha enviado un paquete ";
        cad += this.obtenerNombreTipoPaquete();
        return(cad);
    }

    private int tipoP;
}
