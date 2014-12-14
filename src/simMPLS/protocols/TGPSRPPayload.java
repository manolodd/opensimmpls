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
package simMPLS.protocols;

/**
 * This class implements a GPSRP (Guarantee of Service Store and Retransmit
 * Protocol) packet content (payload of GPSRP packet). As defined in the
 * proposal "Guarantee of Servico (GoS) Support over MPLS using Active
 * Techniques".
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TGPSRPPayload {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TGPSRPPayload.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
