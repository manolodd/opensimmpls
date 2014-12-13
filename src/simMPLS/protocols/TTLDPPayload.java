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
 * This class implements a TLDP (Tiny Label Distribution Protocol) packet
 * content. As defined in the proposal "Guarantee of Servico (GoS) Support over
 * MPLS using Active Techniques".
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TTLDPPayload {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TTLDPPayload.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TTLDPPayload() {
        this.TLDPMessageType = TTLDPPayload.LABEL_REQUEST;
        this.targetIPAddress = "";
        // FIX: create and use constants instead of these harcoded values
        this.label = 16;
        this.TLDPIdentifier = 0;
    }

    /**
     * This method gets the size of this TLDP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Te size for this TLDP packet, in bytes (octects).
     * @since 1.0
     */
    public int getSize() {
        // Fix: create and use a constant insted of this harcoded value.
        return 9;
    }

    /**
     * This method sets the type of TLDP message, giving meaning to this GPSRP
     * payload. As defined in the proposal "Guarantee of Service (GoS) Support
     * over MPLS using Active Techniques".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param TLDPMessageType The type of TLDP message; one of the constants
     * defined in TTLDPPayload class.
     * @since 1.0
     */
    public void setTLDPMessageType(int TLDPMessageType) {
        this.TLDPMessageType = TLDPMessageType;
    }

    /**
     * This method gets the type of TLDP message, giving meaning to this GPSRP
     * payload. As defined in the proposal "Guarantee of Service (GoS) Support
     * over MPLS using Active Techniques".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of TLDP message; one of the constants defined in
     * TTLDPPayload class.
     * @since 1.0
     */
    public int getTLDPMessageType() {
        return this.TLDPMessageType;
    }

    /**
     * This method sets the IP address of the node that should receive this TLDP
     * packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param targetIPAddress the IP address of the node that should receive
     * this TLDP packet.
     * @since 1.0
     */
    public void setTargetIPAddress(String targetIPAddress) {
        this.targetIPAddress = targetIPAddress;
    }

    /**
     * This method gets the IP address of the node that should receive this TLDP
     * packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The IP address of the node that should receive this TLDP packet.
     * @since 1.0
     */
    public String getTargetIPAddress() {
        return this.targetIPAddress;
    }

    /**
     * This method sets the label that is going to travel within this TLDP
     * packet payload. The speciic meaning of this label will depend on the kind
     * of TLDP message.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param label The label that is going to travel within this TLDP packet
     * payload.
     * @since 1.0
     */
    public void setLabel(int label) {
        this.label = label;
    }

    /**
     * This method gets the label that is going to travel within this TLDP
     * packet payload. The speciic meaning of this label will depend on the kind
     * of TLDP message.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The label that travels within this TLDP packet payload.
     * @since 1.0
     */
    public int getLabel() {
        return this.label;
    }

    /**
     * A node can have multiple TLDP sessions concurrently. This method sets the
     * TLDP identifier (TLDP session identifier) for this specific TDLP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param TLDPIdentifier The TLDP session identifier for this TLDP packet.
     * @since 1.0
     */
    public void setTLDPIdentifier(int TLDPIdentifier) {
        this.TLDPIdentifier = TLDPIdentifier;
    }

    /**
     * A node can have multiple TLDP sessions concurrently. This method gets the
     * TLDP identifier (TLDP session identifier) for this specific TDLP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The TLDP session identifier for this TLDP packet.
     * @since 1.0
     */
    public int getTLDPIdentifier() {
        return this.TLDPIdentifier;
    }

    public static final int LABEL_REQUEST = -33;
    public static final int LABEL_REQUEST_DENIED = -31;
    public static final int LABEL_REQUEST_OK = -30;
    public static final int LABEL_REMOVAL_REQUEST = -32;
    public static final int LABEL_REVOMAL_REQUEST_OK = -34;

    private int TLDPMessageType;
    private String targetIPAddress;
    private int label;
    private int TLDPIdentifier;
}
