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
 * This class implements a TLDP (Tiny Label Distribution Protocol) packet. TLDP
 * is defined in "Guarantee of Service (goS) Support over MPLS using Active
 * Tehcniques" proposal.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTLDPPDU extends TAbstractPDU {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TTLDPPDU.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id Packet identifier.
     * @param originIP IP addres of this packet's sender.
     * @param targetIP IP addres of this packet's receiver.
     * @since 2.0
     */
    public TTLDPPDU(long id, String originIP, String targetIP) {
        super(id, originIP, targetIP);
        this.tcpPayload = new TTCPPayload(0);
        this.tldpPayload = new TTLDPPayload();
        this.lspType = false;
        this.packetDirection = TTLDPPDU.DIRECTION_FORWARD;
    }

    /**
     * This method returns the size of the packet in bytes (octects).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size of this packet in bytes (octects).
     * @since 2.0
     */
    @Override
    public int getSize() {
        int auxSize = 0;
        auxSize += super.getIPv4Header().getSize(); // IPv4 header.
        auxSize += this.tcpPayload.getSize(); // TCP payload.
        auxSize += this.tldpPayload.getSize(); // TLDP payload.
        return (auxSize);
    }

    /**
     * This method returns the type of the packet, as defined by constants in
     * TAbstractPDU class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of this packet.
     * @since 2.0
     */
    @Override
    public int getType() {
        return TAbstractPDU.TLDP;
    }

    /**
     * This method return the TCP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TCP payload of this packet.
     * @since 2.0
     */
    public TTCPPayload getTCPPayload() {
        return this.tcpPayload;
    }

    /**
     * This method return the TLDP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TLDP payload of this packet.
     * @since 2.0
     */
    public TTLDPPayload getTLDPPayload() {
        return this.tldpPayload;
    }

    /**
     * This method gets the IPv4 header of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The IPv4 header of this packet.
     * @since 2.0
     */
    @Override
    public TIPv4Header getIPv4Header() {
        return super.getIPv4Header();
        // Not necessary. Already implemented in superclass. FIX
    }

    /**
     * This method returns the subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The subtype of this packet. For instances of this class, it
     * returns TLDP, as defined in TAbstractPDU.
     * @since 2.0
     */
    @Override
    public int getSubtype() {
        return TAbstractPDU.TLDP;
    }

    @Override
    public void setSubtype(int st) {
        // Do nothing. FIX (remove).
    }

    /**
     * This metod sets the LSP signaled by this packet as a traditional LSP or a
     * backup LSP as defined in "Guarantee of Service support over MPLS using
     * Active Techniques" proposal. This value is only used with simulation
     * purposes and does not form part of the TLDP protocol at all.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param LSPType LSP_NORMAL o LSP_BACKUP as defined in JSimulationPanel,
     * depending on whether it refers to a traditional LSP or a backup LSP as
     * defined in "Guarantee of Service support over MPLS using Active
     * Techniques" proposal.
     * @since 2.0
     */
    public void setLSPType(boolean LSPType) {
        this.lspType = LSPType;
        // FIX: LSP_NORMAL and LSP_BACKUP constats should be defined in this 
        // class instead of in JSimulationPanel class.
    }

    /**
     * This method gets the type of LSP that is signaled by this packet: a
     * traditional one or a backup LSP as defined in "Guarantee of Service
     * support over MPLS using Active Techniques" proposal. This value is only
     * used with simulation purposes and does not form part of the TLDP protocol
     * at all.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return LSP_NORMAL o LSP_BACKUP as defined in JSimulationPanel, depending
     * on whether it refers to a traditional LSP or a backup LSP as defined in
     * "Guarantee of Service support over MPLS using Active Techniques"
     * proposal.
     * @since 2.0
     */
    public boolean getLSPType() {
        return this.lspType;
        // FIX: LSP_NORMAL and LSP_BACKUP constants should be defined in this 
        // class instead of in JSimulationPanel class.
    }

    /**
     * This method return the way this packet has arrived to a given node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return CAME_BY_ENTRANCE, CAME_BY_EXIT o CAME_BY_BACKUP_EXIT as defined
     * in TTLDPPDU class.
     */
    public int getLocalOrigin() {
        if (this.packetDirection == TTLDPPDU.DIRECTION_FORWARD) {
            return TTLDPPDU.CAME_BY_ENTRANCE;
        }
        if (this.packetDirection == TTLDPPDU.DIRECTION_BACKWARD) {
            return TTLDPPDU.CAME_BY_EXIT;
        }
        if (this.packetDirection == TTLDPPDU.DIRECTION_BACKWARD_BACKUP) {
            return TTLDPPDU.CAME_BY_BACKUP_EXIT;
        }
        return TTLDPPDU.CAME_BY_ENTRANCE;
    }

    /**
     * This method allow establishing the way this packet should follow to exit
     * the node. It's a kind of internal pseudo-switching.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param localTarget DIRECTION_FORWARD, DIRECTION_BACKWARD as defined in
     * TTLDPPDU class.
     */
    public void setLocalTarget(int localTarget) {
        this.packetDirection = localTarget;
    }

    public static final int DIRECTION_FORWARD = -1;
    public static final int DIRECTION_BACKWARD = -2;
    public static final int DIRECTION_BACKWARD_BACKUP = -3;

    public static final int CAME_BY_ENTRANCE = -1;
    public static final int CAME_BY_EXIT = -2;
    public static final int CAME_BY_BACKUP_EXIT = -3;

    private int packetDirection;
    private TTCPPayload tcpPayload;
    private TTLDPPayload tldpPayload;

    private boolean lspType;
}
