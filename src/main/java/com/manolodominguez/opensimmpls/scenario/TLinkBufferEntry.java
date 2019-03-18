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
package com.manolodominguez.opensimmpls.scenario;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;

/**
 * This class implements a TLinkBufferEntry that allow simulation of the transit
 * delay of a packet through the link.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLinkBufferEntry implements Comparable {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TLinkBufferEntry.
     *
     * @param packet A packet that start its travel through the link towards the
     * corresponding end node.
     * @param totalTransitDelay The total delay the packet will require to reach
     * the target end node (in nanoseconds). Usually it is the link delay.
     * @param packetEnd TLink.TAIL_END_NODE or TLink.HEAD_END_NODE, depending on
     * whether the target node is connected to the tail end of the link or to
     * the head end, respectively. Links are full duplex.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLinkBufferEntry(TAbstractPDU packet, long totalTransitDelay, int packetEnd) {
        this.remainingTransitDelay = totalTransitDelay;
        this.initialTotalTransitDelay = totalTransitDelay;
        this.packet = packet;
        this.packetEnd = packetEnd;
    }

    /**
     * This method allows the comparation between the current TLinkBufferEntry
     * and another passed out as an argument. It is used to sort different
     * entries.
     *
     * @param anotherLinkBufferEntry Another instance of TLinkBufferEntry.
     * @return -1, 0 or 1, depending on whether the anotherLinkBufferEntry is
     * lesser, equal or greater than the current entry.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public int compareTo(Object anotherLinkBufferEntry) {
        TLinkBufferEntry linkBufferEntry = (TLinkBufferEntry) anotherLinkBufferEntry;
        TAbstractPDU anotherPacket = linkBufferEntry.getPacket();
        return this.packet.compareTo(anotherPacket);
    }

    /**
     * This method gets the packet included in this link buffer entry.
     *
     * @return the packet included in this link buffer entry..
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TAbstractPDU getPacket() {
        return this.packet;
    }

    /**
     * This method gets the total transit delay the packet has to be in the link
     * before being delivered to the target node (in nanoseconds).
     *
     * @return the total transit delay the packet has to be in the link before
     * being delivered to the target node (in nanoseconds).
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public long getTotalTransitDelay() {
        return this.initialTotalTransitDelay;
    }

    /**
     * This method sets the packet that has to be included in this link buffer
     * entry.
     *
     * @param packet the packet that has to be included in this link buffer
     * entry.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setPacket(TAbstractPDU packet) {
        this.packet = packet;
    }

    /**
     * This method sets the total transit delay the packet has to be in the link
     * before being delivered to the target node (in nanoseconds).
     *
     * @param totalTransitDelay the total transit delay the packet has to be in
     * the link before being delivered to the target node (in nanoseconds).
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTotalTransitDelay(long totalTransitDelay) {
        this.remainingTransitDelay = totalTransitDelay;
        this.initialTotalTransitDelay = totalTransitDelay;
    }

    /**
     * This method gets the remaining transit delay the packet has to be in the
     * link before being delivered to the target node (in nanoseconds).
     *
     * @return the remaining transit delay the packet has to be in the link
     * before being delivered to the target node (in nanoseconds).
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public long getRemainingTransitDelay() {
        return this.remainingTransitDelay;
    }

    /**
     * This method decreases "step" nanoseconds from the remaining transis delay
     * of this packet. Once the remaning transit delay is zero, the packet can
     * be delivered to the target node.
     *
     * @param step A given number of nanoseconds to be substracted from the
     * remaining transit delay.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void substractStepFromRemainingDelay(long step) {
        this.remainingTransitDelay -= step;
        // FIX: use class constant instead of harcoded values
        if (this.remainingTransitDelay < 0) {
            this.remainingTransitDelay = 0;
        }
    }

    /**
     * This method sets the node connected to the end of the link to wich the
     * packet inside this link buffer entry will be delivered. As links are
     * full-duplex, it is necessary to know whether the packet should advance to
     * the tail end or to the head end of the link.
     *
     * @param packetEnd TLink.TAIL_END_NODE or TLink.HEAD_END_NODE, depending on
     * whether the target node is connected to the tail end of the link or to
     * the head end, respectively. Links are full duplex.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setPacketEnd(int packetEnd) {
        this.packetEnd = packetEnd;
    }

    /**
     * This method identifies the node connected to the end of the link to wich
     * the packet inside this link buffer entry will be delivered. As links are
     * full-duplex, it is necessary to know whether the packet should advance to
     * the tail end or to the head end of the link.
     *
     * @return TLink.TAIL_END_NODE or TLink.HEAD_END_NODE, depending on whether
     * the target node is connected to the tail end of the link or to the head
     * end, respectively. Links are full duplex.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getPacketEnd() {
        return this.packetEnd;
    }

    private TAbstractPDU packet;
    private int packetEnd;
    private long remainingTransitDelay;
    private long initialTotalTransitDelay;
}
