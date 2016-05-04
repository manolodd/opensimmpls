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
package simMPLS.protocols;

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
        this.flowID = 0;
        this.packetID = 0;
    }

    /**
     * This method sets the flow of the packet whose retransmission is being
     * requested by this GPSRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID The flow ID of the packet whose retransmission is being
     * requested.
     * @since 2.0
     */
    public void setFlowID(int flowID) {
        this.flowID = flowID;
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
        return this.flowID;
    }

    /**
     * This method sets the packet ID of the packet whose retransmission is
     * being requested by this GPSRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packetID Packet ID of the packet whose retransmission is being
     * requested.
     * @since 2.0
     */
    public void setPacketID(int packetID) {
        this.packetID = packetID;
    }

    /**
     * This method gets the packet ID of the packet whose retransmission is
     * being requested by this GPSRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Packet ID of the packet whose retransmission is being requested.
     * @since 2.0
     */
    public int getPacketID() {
        return this.packetID;
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
    private int flowID;
    private int packetID;
}
