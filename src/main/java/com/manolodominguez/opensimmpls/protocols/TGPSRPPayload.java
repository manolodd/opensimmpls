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
package com.manolodominguez.opensimmpls.protocols;

/**
 * This class implements a GPSRP (Guarantee of Service Store and Retransmit
 * Protocol) packet content (payload of GPSRP packet). As defined in the
 * proposal "Guarantee of Servico (GoS) Support over MPLS using Active
 * Techniques".
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TGPSRPPayload {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TGPSRPPayload.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TGPSRPPayload() {
        this.GPSRPMessageType = TGPSRPPayload.RETRANSMISSION_REQUEST;
        this.globalFlowID = 0;
        this.packetGoSGlobalUniqueID = 0;
    }

    /**
     * This method sets the flow of the packet whose retransmission is being
     * requested by this GPSRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param globalFlowID The flow ID of the packet whose retransmission is being
     * requested.
     * @since 2.0
     */
    public void setFlowID(int globalFlowID) {
        this.globalFlowID = globalFlowID;
    }

    /**
     * This method gets the flow of the packet whose retransmission is being
     * requested by this GPSRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The flow ID of the packet whose retransmission is being
     * requested.
     * @since 2.0
     */
    public int getFlowID() {
        return this.globalFlowID;
    }

    /**
     * This method sets the packet ID of the packet whose retransmission is
     * being requested by this GPSRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packetGoSGlobalUniqueID Packet ID of the packet whose retransmission is being
     * requested.
     * @since 2.0
     */
    public void setPacketGoSGlobalUniqueID(int packetGoSGlobalUniqueID) {
        this.packetGoSGlobalUniqueID = packetGoSGlobalUniqueID;
    }

    /**
     * This method gets the packet ID of the packet whose retransmission is
     * being requested by this GPSRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Packet ID of the packet whose retransmission is being requested.
     * @since 2.0
     */
    public int getPacketGoSGlobalUniqueID() {
        return this.packetGoSGlobalUniqueID;
    }

    /**
     * This method returns the size of this GPSRP payload in bytes (octects). As
     * defined in the proposal "Guarantee of Service (GoS) Support over MPLS
     * using Active Techniques".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size of this GPSRP payload in bytes (octects).
     * @since 2.0
     */
    public int getSize() {
        return 9;
        // FIX: Create a constant insted of a harcoded value.
    }

    /**
     * This method sets the type of GPSRP message, giving meaning to this GPSRP
     * payload. As defined in the proposal "Guarantee of Service (GoS) Support
     * over MPLS using Active Techniques".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param GPSRPMessageType The type of GPSRP message; one of the constants
     * defined in TGPSRPPayload class.
     * @since 2.0
     */
    public void setGPSRPMessageType(int GPSRPMessageType) {
        this.GPSRPMessageType = GPSRPMessageType;
    }

    /**
     * This method gets the type of GPSRP message, giving meaning to this GPSRP
     * payload. As defined in the proposal "Guarantee of Service (GoS) Support
     * over MPLS using Active Techniques".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of GPSRP message; one of the constants defined in
     * TGPSRPPayload class.
     * @since 2.0
     */
    public int getGPSRPMessageType() {
        return this.GPSRPMessageType;
    }

    // Types of GPSRP messages
    public static final int RETRANSMISSION_REQUEST = -1;
    public static final int RETRANSMISION_NOT_POSSIBLE = -2;
    public static final int RETRANSMISION_OK = -3;

    private int GPSRPMessageType;
    private int globalFlowID;
    private int packetGoSGlobalUniqueID;
}
