/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package com.manolodominguez.opensimmpls.scenario.simulationevents;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.scenario.TTopologyElement;

/**
 * Esta clase implementa un evento que ser� usado para notificar que un nodo ha
 * recibido un paquete.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSimulationEventPacketReceived extends TSimulationEvent {

    /**
     * Crea una nueva instancia de TESpaqueteRecibido
     * @since 2.0
     * @param inst Instante de tiempo en el que se produce el evento.
     * @param tam Tama�o del paquete  al que se refiere el evento.
     * @param emisor Elemento de la topologia que gener� el evento.
     * @param id id �nico del evento.
     * @param tipoPaquete Tipo del paquete que se ha recibido.
     */
    public TSimulationEventPacketReceived(Object emisor, long id, long inst, int tipoPaquete, int tam) {
        super(emisor, id, inst);
        tipoP = tipoPaquete;
        tamanio = tam;
    }

    /**
     * Este m�todo obtiene el tipo del paquete al que se refiere el evento.
     * @return El tipo del paquete al que se refiere el evento.
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
        return super.PACKET_RECEIVED;
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
        et = super.getSource();
        if (et.getElementType() == TTopologyElement.LINK) {
            ent = (TLink) et;
            return ent.getName();
        } else if (et.getElementType() == TTopologyElement.NODE) {
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
        et = super.getSource();
        if (et.getElementType() == TTopologyElement.LINK) {
            return ("Enlace ");
        } else if (et.getElementType() == TTopologyElement.NODE) {
            return ("Nodo ");
        }
        return ("");
    }

    /**
     * Este m�todo obtiene una representaci�n textual del tipo del paquete al que se
     * refiere el evento.
     * @return El tipo del paquete al que se refiere el evento, expresado en  texto.
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
        cad += "ha recibido un paquete ";
        cad += this.obtenerNombreTipoPaquete();
        cad += " de tamanio ";
        cad += this.tamanio;
        cad += " octetos.";
        return(cad);
    }

    private int tipoP;
    private int tamanio;
}
