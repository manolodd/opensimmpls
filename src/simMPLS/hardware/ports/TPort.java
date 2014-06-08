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
package simMPLS.hardware.ports;

import simMPLS.scenario.TLink;
import simMPLS.scenario.TStats;
import simMPLS.protocols.TPDU;
import simMPLS.utils.TMonitor;

/**
 * This abstract class will be implemented to have an I/O port of a port set.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public abstract class TPort {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TPort.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     * @param portID the identifier of the port. This is the unique identifier
     * ti distinguish the port within the parent port set.
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
     * @since 1.0
     */
    public void setPortSet(TPortSet parentPortSet) {
        this.parentPortSet = parentPortSet;
    }

    /**
     * This method get the port set this port belongs to.
     *
     * @return The port set this port belongs to.
     * @since 1.0
     */
    public TPortSet getPortSet() {
        return this.parentPortSet;
    }

    /**
     * This method establishes the port number for this port.
     *
     * @param portID the port number for this port.
     * @since 1.0
     */
    public void setPortID(int portID) {
        this.portID = portID;
    }

    /**
     * This method gets the port number for this port.
     *
     * @return The port number for this port.
     * @since 1.0
     */
    public int getPortID() {
        return this.portID;
    }

    /**
     * This method check whether the port is available (not connected to another
     * adjacent node) or no (if it is connected to another adjacent node).
     *
     * @return TRUE, if it is available. Otherwise, FALSE.
     * @since 1.0
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
     * @since 1.0
     */
    public void setLink(TLink link) {
        this.link = link;
    }

    /**
     * This method return the link attached to this port.
     *
     * @return The link attached to this port, if the port is connected.
     * Otherwise, NULL.
     * @since 1.0
     */
    public TLink getLink() {
        return this.link;
    }

    /**
     * This method dettach a link from this port, making it available for new
     * links attachments.
     *
     * @since 1.0
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
     * @since 1.0
     */
    public void putPacketOnLink(TPDU packet, int endID) {
        if (this.link != null) {
            if (!this.link.linkIsBroken()) {
                if (this.link.getLinkType() == TLink.INTERNAL) {
                    this.link.carryPacket(packet, endID);
                    if (this.getPortSet().getParentNode().getStats() != null) {
                        this.getPortSet().getParentNode().getStats().addStatsEntry(packet, TStats.SALIDA);
                    }
                } else {
                    if ((packet.getType() != TPDU.GPSRP) && (packet.getType() != TPDU.TLDP)) {
                        this.link.carryPacket(packet, endID);
                        if (this.getPortSet().getParentNode().getStats() != null) {
                            this.getPortSet().getParentNode().getStats().addStatsEntry(packet, TStats.SALIDA);
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
     * @since 1.0
     */
    public abstract void discardPacket(TPDU packet);

    /**
     * This method, when implemented, will put a new packet in the buffer of the
     * port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet Packet to be inserted in the buffer of the port.
     * @since 1.0
     */
    public abstract void addPacket(TPDU packet);

    /**
     * This method, when implemented, will put a new packet in the buffer of the
     * port. In fact, this will do the same than addPacket(p) method, but will
     * not generate simulation events but do it silently.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be inserted in the buffer of the port.
     * @since 1.0
     */
    public abstract void reEnqueuePacket(TPDU packet);

    /**
     * This method, when implemented, wil read and return the next packet of the
     * buffer according to the port management policy.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The read packet
     * @since 1.0
     */
    public abstract TPDU getPacket();

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
     * @since 1.0
     */
    public abstract boolean canSwitchPacket(int octetos);

    /**
     * This method, when implemented, will compute the congestion level of the
     * port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A number, without decimals, between 0 and 100, which will be the
     * congestion level as a percentage.
     * @since 1.0
     */
    public abstract long getCongestionLevel();

    /**
     * This method, when implemented, will check whether there is a packet in
     * the buffer waiting to be switched/routed, or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is a packet waiting to be switched/routed.
     * Otherwise, FALSE.
     * @since 1.0
     */
    public abstract boolean thereIsAPacketWaiting();

    /**
     * This method, when implemented, will compute and return the number of
     * octets that are currently used by packets in the buffer of the port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size, in octects, used by packets in the buffer of the port.
     * @since 1.0
     */
    public abstract long getOccupancy();

    /**
     * This method, when implemented, will compute and return the number of
     * packets stored in the buffer of the port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The total number of packets stored in the buffer of the port.
     * @since 1.0
     */
    public abstract int getNumberOfPackets();

    /**
     * This method, when implemented, will reset attributes of the class as when
     * created by the constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public abstract void reset();

    /**
     * This method, when implemented, will allow to skip size limitation of the
     * buffer and, hence, configure the port as an ideal port, with unlimited
     * space.
     *
     * @param unlimitedBuffer TRUE if the port is going to be defined as an
     * ideal one (unlimited space on it). FALSE, on the contrary.
     * @since 1.0
     */
    public abstract void setUnlimitedBuffer(boolean unlimitedBuffer);

    protected TLink link;
    protected TPortSet parentPortSet;
    protected TMonitor monitor;
    protected int portID;
}
