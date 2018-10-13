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
package com.manolodominguez.opensimmpls.hardware.dmgp;

import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;

/**
 * This class implements an entry of the DMGP memory. It stores a GoS packet and
 * all neccesary data to be forwarded.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TDMGPEntry implements Comparable<TDMGPEntry> {

    /**
     * This method is the constructor of thte class. It creates a new instance
     * of TDMGPEntry and initialize its attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param arrivalOrder Establish the arrival of this TDMGPEntry in relation
     * to itrs corresponding flow.
     */
    public TDMGPEntry(int arrivalOrder) {
        this.flowID = DEFAULT_FLOWID;
        this.packetID = DEFAULT_PACKETID;
        this.packet = null;
        this.arrivalOrder = arrivalOrder;
    }

    /**
     * This method obtains the identifier of the flow associated to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The identifier of the flow associated to this entry.
     * @since 2.0
     */
    public int getFlowID() {
        return this.flowID;
    }

    /**
     * This method obtains the identifier of the GoS packet stored in this
     * entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The identifier of the GoS packet.
     * @since 2.0
     */
    public int getPacketID() {
        return this.packetID;
    }

    /**
     * This method obtains the GoS packet that is stored in this entry of the
     * DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The stored GoS packet.
     * @since 2.0
     */
    public TMPLSPDU getPacket() {
        if (this.packet != null) {
            return this.packet.getAClon();
        }
        return null;
    }

    /**
     * This method insert the GoS packet in this entry of the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsPacket The packet to be inserted in this entry.
     * @since 2.0
     */
    public void setPacket(TMPLSPDU mplsPacket) {
        this.packet = mplsPacket.getAClon();
        this.flowID = mplsPacket.getIPv4Header().getOriginIPv4Address().hashCode();
        this.packetID = mplsPacket.getIPv4Header().getGoSGlobalUniqueIdentifier();
    }

    /**
     * This method allow estabishing the order number in the complete DMGP
     * memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The order number of this DMGP entry.
     * @since 2.0
     */
    public int getArrivalOrder() {
        return this.arrivalOrder;
    }

    /**
     * This method compares this DMGP entry with another to establish the oder.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherDMGPEntry DMGP entry to be compared with this one.
     * @return -1, 0, 1, depending on whether the current entry is lower, equal
     * or greater than the entry specified as argument. In terms of shorting.
     * @since 2.0
     */
    @Override
    public int compareTo(TDMGPEntry anotherDMGPEntry) {
        if (this.arrivalOrder < anotherDMGPEntry.getArrivalOrder()) {
            return TDMGPEntry.THIS_LOWER;
        }
        if (this.arrivalOrder > anotherDMGPEntry.getArrivalOrder()) {
            return TDMGPEntry.THIS_GREATER;
        }
        return TDMGPEntry.THIS_EQUAL;
    }

    private static final int THIS_LOWER = -1;
    private static final int THIS_EQUAL = 0;
    private static final int THIS_GREATER = 1;

    private int flowID;
    private int packetID;
    private final int arrivalOrder;
    private TMPLSPDU packet;

    private static final int DEFAULT_FLOWID = -1;
    private static final int DEFAULT_PACKETID = -1;
}
