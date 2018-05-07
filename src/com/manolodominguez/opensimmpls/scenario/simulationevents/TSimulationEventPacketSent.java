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

/**
 * This class implements a simulation event that is generated when a packet is
 * sent.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSimulationEventPacketSent extends TSimulationEvent {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TSimulationEventPacketSent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param source The object that generates the event.
     * @param eventID The event unique identifier.
     * @param timeInstant The time instant when the event was generated.
     * @param packetType the size of the packet thas the source node has
     * generated.
     * @since 2.0
     */
    public TSimulationEventPacketSent(Object source, long eventID, long timeInstant, int packetType) {
        super(source, eventID, timeInstant);
        this.packetType = packetType;
    }

    /**
     * This method gets the type of the packet sent by the source node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the type of the packet sent by the source node. One of the
     * constants defined in TAbstractPDU.
     * @since 2.0
     */
    public int getPacketType() {
        return this.packetType;
    }

    /**
     * This method gets the subtype of this simulation event. One of the
     * constants of TSimulationEvent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the subtype of this simulation event.
     * TSimulationEvent.PACKET_SENT.
     * @since 2.0
     */
    @Override
    public int getSubtype() {
        return TSimulationEvent.PACKET_SENT;
    }

    /**
     * This method gets the type of the packet routed by the source node, as
     * String.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the type of the packet routed by the source node, as String.
     * @since 2.0
     */
    public String getPacketTypeAsString() {
        String strPacketType = "";
        switch (this.packetType) {
            case TAbstractPDU.IPV4: {
                // FIX: i18N required
                strPacketType = "IPv4";
                break;
            }
            case TAbstractPDU.IPV4_GOS: {
                // FIX: i18N required
                strPacketType = "IPv4 con GoS";
                break;
            }
            case TAbstractPDU.MPLS: {
                // FIX: i18N required
                strPacketType = "MPLS";
                break;
            }
            case TAbstractPDU.MPLS_GOS: {
                // FIX: i18N required
                strPacketType = "MPLS con GoS";
                break;
            }
            case TAbstractPDU.TLDP: {
                // FIX: i18N required
                strPacketType = "LDP";
                break;
            }
            case TAbstractPDU.GPSRP: {
                // FIX: i18N required
                strPacketType = "GPSRP";
                break;
            }
        }
        return (strPacketType);
    }

    /**
     * This method gets a human readable explanation of this event.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a human readable explanation of this event.
     * @since 2.0
     */
    @Override
    public String toString() {
        String string = "";
        string += "[";
        string += this.getSourceTypeAsString();
        string += " ";
        string += this.getSourceName();
        string += "] ";
        // FIX: i18N needed
        string += "ha enviado un paquete ";
        string += this.getPacketTypeAsString();
        return (string);
    }

    private int packetType;
}
