/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package simMPLS.scenario;

import simMPLS.protocols.TAbstractPDU;

/**
 * Esta clase implementa un evento que ser� usado para indecar que un paquete est�
 * circulando por un enlace.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSEPacketOnFly extends TSimulationEvent {

    /**
     * Crea una nueva instancia de TESPaqueteEnTransito
     * @since 2.0
     * @param inst Instante de tiempo en el que se produjo el evento.
     * @param pt Porcentaje de reccorido del enlace que lleva recorrido el paquete por el cual se
     * genero el evento.
     * @param emisor Enlace que gener� el evento.
     * @param id Identificador unico del evento.
     * @param tipoPaquete Tipo del paquete (de qu� trafico es) al que se refiere el evento.
     */
    public TSEPacketOnFly(Object emisor, long id, long inst, int tipoPaquete, long pt) {
        super(emisor, id, inst);
        tipoP = tipoPaquete;
        porcentajeTransito = pt;
    }

    /**
     * Este m�todo obtiene el porcentaje del enalce que ha recorrido ya el paquete al
     * que se refiete el evento.
     * @since 2.0
     * @return El porcentaje de recorrido del paquete.
     */    
    public long obtenerPorcentajeTransito() {
        return this.porcentajeTransito;
    }
    
    /**
     * Este m�todo obtiene el tipo de paquete al que se refiere el evento.
     * @return El tipo el paquete.
     * @since 2.0
     */    
    public int obtenerTipoPaquete() {
        return tipoP;
    }

    /**
     * Este m�todo obtiene el subtipo del evento, si los hubiese.
     * @return El subtipo del evento.
     * @since 2.0
     */    
    public int getSubtype() {
        return super.PACKET_ON_FLY;
    }

    /**
     * Este m�todo obtiene el nombre del enlace que origin� el evento.
     * @return El nombre del enlace que origin� el evento.
     * @since 2.0
     */    
    public String obtenerNombre() {
        TTopologyElement et = null;
        TLink ent = null;
        TNode nt = null;
        et = super.obtenerFuente();
        if (et.getElementType() == TTopologyElement.LINK) {
            ent = (TLink) et;
            return ent.getName();
        } else if (et.getElementType() == TTopologyElement.NODO) {
            nt = (TNode) et;
            return nt.getName();
        }
        return ("");
    }

    /**
     * Este m�todo obtiene un texto con el tipo de evento.
     * @return Un texto con el tipo de evento.
     * @since 2.0
     */    
    public String obtenerNombreTipo() {
        TTopologyElement et = null;
        et = super.obtenerFuente();
        if (et.getElementType() == TTopologyElement.LINK) {
            return ("Enlace ");
        } else if (et.getElementType() == TTopologyElement.NODO) {
            return ("Nodo ");
        }
        return ("");
    }

    /**
     * Este m�todo obtiene una representaci�n textual del tipo de paquete.
     * @return El tipo de paquete, en texto.
     * @since 2.0
     */    
    public String obtenerNombreTipoPaquete() {
        String strTipo = "";
        switch (tipoP) {
            case TAbstractPDU.IPV4: {
                strTipo = "IPv4";
                break;
            }
            case TAbstractPDU.IPV4_GOS: {
                strTipo = "IPv4 con GoS";
                break;
            }
            case TAbstractPDU.MPLS: {
                strTipo = "MPLS";
                break;
            }
            case TAbstractPDU.MPLS_GOS: {
                strTipo = "MPLS con GoS";
                break;
            }
            case TAbstractPDU.TLDP: {
                strTipo = "LDP";
                break;
            }
            case TAbstractPDU.GPSRP: {
                strTipo = "GPSRP";
                break;
            }
        }
        return(strTipo);
    }
    

    /**
     * Este m�todo explcia el evento en una l�nea de texto.
     * @return El texto explicando el evento.
     * @since 2.0
     */    
    public String toString() {
        String cad = "";
        cad += "[";
        cad += this.obtenerNombreTipo();
        cad += " ";
        cad += this.obtenerNombre();
        cad += "] ";
        cad += "transporta un paquete ";
        cad += this.obtenerNombreTipoPaquete();
        return(cad);
    }

    private long porcentajeTransito;
    private int tipoP;
}
