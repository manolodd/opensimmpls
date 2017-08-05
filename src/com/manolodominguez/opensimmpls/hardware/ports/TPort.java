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
package com.manolodominguez.opensimmpls.hardware.ports;

import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TStats;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.utils.TMonitor;

/**
 * This abstract class will be implemented to have an I/O port of a port set.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TPort {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TPort.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param portID the id of the port. This is the unique id
 ti distinguish the port within the parent port set.
     * @param parentPortSet A reference to the parent port set this port belongs
     * to.
     */
    public TPort(TPortSet parentPortSet, int portID) {
        this.link = null;
        this.parentPortSet = parentPortSet;
        this.monitor = new TMonitor();
        this.portID = portID;
    }

    /**
     * This method establish which is the port set this port belongs to.
     *
     * @param parentPortSet The port set this port belongs to.
     * @since 2.0
     */
    public void setPortSet(TPortSet parentPortSet) {
        this.parentPortSet = parentPortSet;
    }

    /**
     * This method get the port set this port belongs to.
     *
     * @return The port set this port belongs to.
     * @since 2.0
     */
    public TPortSet getPortSet() {
        return this.parentPortSet;
    }

    /**
     * This method establishes the port number for this port.
     *
     * @param portID the port number for this port.
     * @since 2.0
     */
    public void setPortID(int portID) {
        this.portID = portID;
    }

    /**
     * This method gets the port number for this port.
     *
     * @return The port number for this port.
     * @since 2.0
     */
    public int getPortID() {
        return this.portID;
    }

    /**
     * This method check whether the port is available (not connected to another
     * adjacent node) or no (if it is connected to another adjacent node).
     *
     * @return TRUE, if it is available. Otherwise, FALSE.
     * @since 2.0
     */
    public boolean isAvailable() {
        if (this.link == null) {
            return true;
        }
        return false;
    }

    /**
     * This method attach a link to this port, making it unavailable for new
     * link attachments.
     *
     * @param link the link to be attached to this port.
     * @since 2.0
     */
    public void setLink(TLink link) {
        this.link = link;
    }

    /**
     * This method return the link attached to this port.
     *
     * @return The link attached to this port, if the port is connected.
     * Otherwise, NULL.
     * @since 2.0
     */
    public TLink getLink() {
        return this.link;
    }

    /**
     * This method dettach a link from this port, making it available for new
     * links attachments.
     *
     * @since 2.0
     */
    public void disconnectLink() {
        this.link = null;
    }

    /**
     * This method put a packet in the link connected to it, to be delivered to
     * the other end of the link. Links are full-duplex, so it is necessary to
     * identify to where the packet is going to.
     *
     * @param packet The packet to be delivered through the link.
     * @param endID 1, if the packet has to be delivered to the end 1. 2, if the
     * packet has to be delivered to the end 2.
     * @since 2.0
     */
    public void putPacketOnLink(TAbstractPDU packet, int endID) {
        if (this.link != null) {
            if (!this.link.isBroken()) {
                if (this.link.getLinkType() == TLink.INTERNAL) {
                    this.link.carryPacket(packet, endID);
                    if (this.getPortSet().getParentNode().getStats() != null) {
                        this.getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.OUTGOING);
                    }
                } else {
                    if ((packet.getType() != TAbstractPDU.GPSRP) && (packet.getType() != TAbstractPDU.TLDP)) {
                        this.link.carryPacket(packet, endID);
                        if (this.getPortSet().getParentNode().getStats() != null) {
                            this.getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.OUTGOING);
                        }
                    }
                }
            } else {
                this.discardPacket(packet);
            }
        }
    }

    /**
     * This method, when implemente, will discard the packet passed as an
     * argument from the buffer.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be discarded from the buffer.
     * @since 2.0
     */
    public abstract void discardPacket(TAbstractPDU packet);

    /**
     * This method, when implemented, will put a new packet in the buffer of the
     * port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet Packet to be inserted in the buffer of the port.
     * @since 2.0
     */
    public abstract void addPacket(TAbstractPDU packet);

    /**
     * This method, when implemented, will put a new packet in the buffer of the
     * port. In fact, this will do the same than addPacket(p) method, but will
     * not generate simulation events but do it silently.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be inserted in the buffer of the port.
     * @since 2.0
     */
    public abstract void reEnqueuePacket(TAbstractPDU packet);

    /**
     * This method, when implemented, wil read and return the next packet of the
     * buffer according to the port management policy.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The read packet
     * @since 2.0
     */
    public abstract TAbstractPDU getPacket();

    /**
     * This method, when implemented, will compute whether it is possible or not
     * to switch the next packet in the buffer having the number of octets
     * (specified as an argument) that the port can switch in the current
     * moment.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param octets The number of octets that the port can switch in this
     * moment.
     * @return TRUE, if we can switch the next packet of the buffer at this
     * moment. Otherwise, FALSE.
     * @since 2.0
     */
    public abstract boolean canSwitchPacket(int octetos);

    /**
     * This method, when implemented, will compute the congestion level of the
     * port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A number, without decimals, between 0 and 100, which will be the
     * congestion level as a percentage.
     * @since 2.0
     */
    public abstract long getCongestionLevel();

    /**
     * This method, when implemented, will check whether there is a packet in
     * the buffer waiting to be switched/routed, or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is a packet waiting to be switched/routed.
     * Otherwise, FALSE.
     * @since 2.0
     */
    public abstract boolean thereIsAPacketWaiting();

    /**
     * This method, when implemented, will compute and return the number of
     * octets that are currently used by packets in the buffer of the port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size, in octects, used by packets in the buffer of the port.
     * @since 2.0
     */
    public abstract long getOccupancy();

    /**
     * This method, when implemented, will compute and return the number of
     * packets stored in the buffer of the port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The total number of packets stored in the buffer of the port.
     * @since 2.0
     */
    public abstract int getNumberOfPackets();

    /**
     * This method, when implemented, will reset attributes of the class as when
     * created by the constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract void reset();

    /**
     * This method, when implemented, will allow to skip size limitation of the
     * buffer and, hence, configure the port as an ideal port, with unlimited
     * space.
     *
     * @param unlimitedBuffer TRUE if the port is going to be defined as an
     * ideal one (unlimited space on it). FALSE, on the contrary.
     * @since 2.0
     */
    public abstract void setUnlimitedBuffer(boolean unlimitedBuffer);

    protected TLink link;
    protected TPortSet parentPortSet;
    protected TMonitor monitor;
    protected int portID;
}
