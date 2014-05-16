/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.scenario;

import simMPLS.protocols.TPDU;

/**
 * Esta clase implementa un evento que ser� usado para notificar que un paquete ha
 * sido conmutado.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSEPacketSwitched extends TSimulationEvent {

    /**
     * Crea una nueva instancia de TESPaqueteConmutado
     * @since 1.0
     * @param inst Instante de tiempo en el que se gener� el paquete.
     * @param emisor Nodo que gener� el evento.
     * @param id Identificador unico del evento.
     * @param tipoPaquete Tipo de paquete que se ha conmutado (de qu� tr�fico es).
     */
    public TSEPacketSwitched(Object emisor, long id, long inst, int tipoPaquete) {
        super(emisor, id, inst);
        tipoP = tipoPaquete;
    }

    /**
     * Este m�todo obtiene el tipo el paquete que conmut� el nodo que gener� el evento.
     * @return Tipo de paquete conmutado.
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
    public int getSubtype() {
        return super.PACKET_SWITCHED;
    }

    /**
     * Este m�todo obtiene el nombre del enlace que origin� el evento.
     * @return El nombre del enlace que origin� el evento.
     * @since 1.0
     */    
    public String obtenerNombre() {
        TTopologyElement et = null;
        TLink ent = null;
        TNode nt = null;
        et = super.obtenerFuente();
        if (et.obtenerTipoElemento() == TTopologyElement.ENLACE) {
            ent = (TLink) et;
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
     * Este m�todo devuelve una representaci�n textual del tipo de paquete conmutado.
     * @return Nombre en texto claro, del tipo de paquete conmutado.
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
        cad += "ha conmutado un paquete ";
        cad += this.obtenerNombreTipoPaquete();
        return(cad);
    }

    private int tipoP;
}
