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
package simMPLS.protocols;

/**
 * This class implements a specifica format of the options field of the IPv4
 * header that is needed to implement the "Guarantee of Service (GoS) support
 * over MPLS using active techniques".
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TIPv4OptionsField {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TIPv4OptionsField.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TIPv4OptionsField() {
        // FIX: create and use class constants instead of harcoded values.
        this.requestedGoSLevel = 0;
        this.registerOfActiveNodesCrossed = new String[TIPv4OptionsField.MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES];
        for (int i = 0; i < TIPv4OptionsField.MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES; i++) {
            this.registerOfActiveNodesCrossed[i] = null;
        }
        this.optionFieldIsUsed = false;
        this.numberOfActiveNodesRegistered = 0;
        this.packetLocalUniqueIdentifier = 0;
        this.hasCrossedActiveNodesIPs = false;
    }

    /**
     * This method gets the size of the options field. This is needed because
     * its size can vary in 4-bytes word.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The size of the options field, in bytes (octects).
     * @since 2.0
     */
    public int getSize() {
        // FIX: Recode. Size variable is not necessary.
        int size = 0;
        int sizeAux = 0;
        if (this.optionFieldIsUsed) {
            // FIX: Create and use class constants instead of harcoded values.
            size += 1;   // GoS field.
            size += 4;   // Unique local identifier.
            size += (4 * this.numberOfActiveNodesRegistered); //4 bytes each one.
            while (sizeAux < size) {
                sizeAux += 4;
            }
            size = sizeAux;
        }
        return size;
    }

    /**
     * This method allow establishing the GoS level that has been selected fot
     * the packet. This is part of the "Guarantee of Service (GoS) support over
     * MPLS using active techniques" proposal.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param requestedGoSLevel GoS level selected for the packet, as defined in
     * TAbstractPDU class.
     * @since 2.0
     */
    public void setRequestedGoSLevel(int requestedGoSLevel) {
        this.requestedGoSLevel = requestedGoSLevel;
        this.optionFieldIsUsed = true;
    }

    /**
     * This method set the options field as "in use".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void use() {
        this.optionFieldIsUsed = true;
    }

    /**
     * This method gets whether the options field is being used or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return true, if the options field is being used. Otherwise, false.
     * @since 2.0
     */
    public boolean isUsed() {
        return this.optionFieldIsUsed;
    }

    /**
     * This method gets the requested GoS level encoded in this packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return GoS level selected for the packet, as defined in TAbstractPDU
     * class.
     * @since 2.0
     */
    public int getRequestedGoSLevel() {
        return this.requestedGoSLevel;
    }

    /**
     * This method set a unique packet identifier to this packet. This is part
     * of the "Guarantee of Service (GoS) support over MPLS using active
     * techniques" proposal. It is used to identify a given packet in the whole
     * MPLS domain.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packetLocalUniqueIdentifier The global unique identifier for this
     * packet.
     * @since 2.0
     */
    public void setPacketLocalUniqueIdentifier(int packetLocalUniqueIdentifier) {
        this.packetLocalUniqueIdentifier = packetLocalUniqueIdentifier;
        this.optionFieldIsUsed = true;
    }

    /**
     * This method get the unique packet identifier of this packet. This is part
     * of the "Guarantee of Service (GoS) support over MPLS using active
     * techniques" proposal. It is used to identify a given packet in the whole
     * MPLS domain.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The global unique identifier of this packet.
     * @since 2.0
     */
    public int getPacketLocalUniqueIdentifier() {
        return this.packetLocalUniqueIdentifier;
    }

    /**
     * This method register in the options field the IP address of a new crossed
     * active node. This is part of the "Guarantee of Service (GoS) support over
     * MPLS using active techniques" proposal.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param crossedActiveNodeIP The IP address of a new crossed active node.
     * @since 2.0
     */
    public void setCrossedActiveNode(String crossedActiveNodeIP) {
        this.hasCrossedActiveNodesIPs = true;
        this.optionFieldIsUsed = true;
        if (this.numberOfActiveNodesRegistered < MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES) {
            this.registerOfActiveNodesCrossed[this.numberOfActiveNodesRegistered] = crossedActiveNodeIP;
            this.numberOfActiveNodesRegistered++;
        } else {
            // Drop the first registered crossed active node IP address, because
            // the option field is already full.
            for (int index = 1; index < MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES; index++) {
                this.registerOfActiveNodesCrossed[index - 1] = this.registerOfActiveNodesCrossed[index];
            }
            this.registerOfActiveNodesCrossed[MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES - 1] = crossedActiveNodeIP;
        }
    }

    /**
     * This method allow knowing whether the options field contains IP addresses
     * of crossed active nodes, or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return true, if the options field contains IP addresses of active nodes
     * that has been crossed. Oterwise, false.
     * @since 2.0
     */
    public boolean hasCrossedActiveNodes() {
        return this.hasCrossedActiveNodesIPs;
    }

    /**
     * This method gets the number of IP addresses included of active nodes that
     * are registered in the options field.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return number of IP addresses included of active nodes that are
     * registered in the options field
     * @since 2.0
     */
    public int getNumberOfCrossedActiveNodes() {
        return this.numberOfActiveNodesRegistered;
    }

    /**
     * This method returns the IP address of the active node that this packet
     * crossed registeredActiveNodeIndex hops ago.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param registeredActiveNodeIndex Number of active nodes that this packet
     * crossed before the one whose IP address is wanted.
     * @return IP of the desired active node.
     * @since 2.0
     */
    public String getCrossedActiveNode(int registeredActiveNodeIndex) {
        if (registeredActiveNodeIndex < TIPv4OptionsField.MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES) {
            return this.registerOfActiveNodesCrossed[registeredActiveNodeIndex];
        }
        return null;
    }

    private static final int MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES = 8;

    private int requestedGoSLevel;
    private String[] registerOfActiveNodesCrossed;
    private boolean optionFieldIsUsed;
    private int numberOfActiveNodesRegistered;
    private int packetLocalUniqueIdentifier;
    private boolean hasCrossedActiveNodesIPs;
}
