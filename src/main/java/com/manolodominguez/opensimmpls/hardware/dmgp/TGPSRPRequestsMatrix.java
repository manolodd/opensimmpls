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

import java.util.Iterator;
import java.util.TreeSet;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.commons.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.commons.TSemaphore;

/**
 * This class implements a table where received requests for retrnasmission will
 * be stored while they wait to be managed.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TGPSRPRequestsMatrix {

    /**
     * This is the class constructor. It creates a new instance of
     * TGPSRPRequestsMatrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TGPSRPRequestsMatrix() {
        entries = new TreeSet<>();
        idGenerator = new TRotaryIDGenerator();
        semaphore = new TSemaphore();
    }

    /**
     * This method reset all attributes od the class to its original values, as
     * when created by the constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        entries = new TreeSet<>();
        idGenerator = new TRotaryIDGenerator();
        semaphore = new TSemaphore();
    }

    /**
     * This method update the outgoing port for all the matching entries. It
     * will be changed for a new outgoing port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param currentOutgoingPortID The port ID of the port that is going to be
     * replaced.
     * @param newOutgoingPortID The new outgoing port ID of the port for
     * matching entries.
     * @since 2.0
     */
    public void updateOutgoingPort(int currentOutgoingPortID, int newOutgoingPortID) {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            if (gpsrpRequestEntry.getOutgoingPortID() == currentOutgoingPortID) {
                gpsrpRequestEntry.setOutgoingPortID(newOutgoingPortID);
            }
        }
        semaphore.setGreen();
    }

    /**
     * This method removes from the table all entries that have the outgoing
     * port ID equal than the one specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param oldOutgoingPortID Port ID of the port that must match the outgoing
     * port of entries to be removed.
     * @since 2.0
     */
    public void removeEntriesMatchingOutgoingPort(int oldOutgoingPortID) {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            if (gpsrpRequestEntry.getOutgoingPortID() == oldOutgoingPortID) {
                iterator.remove();
            }
        }
        semaphore.setGreen();
    }

    /**
     * This method insert a new entry in the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsPacket Packet for wich the retransmission is going to be
     * requested.
     * @param incomingPortID Port ID of the incoming port of the packet. It will
     * be the outgoing port ID for the retransmission request.
     * @return The new created an inserted entry. Otherwise, NULL.
     * @since 2.0
     */
    public TGPSRPRequestEntry addEntry(TMPLSPDU mplsPacket, int incomingPortID) {
        semaphore.setRed();
        TGPSRPRequestEntry gpsrpRequestEntry = new TGPSRPRequestEntry(idGenerator.getNextIdentifier());
        gpsrpRequestEntry.setOutgoingPortID(incomingPortID);
        gpsrpRequestEntry.setFlowID(mplsPacket.getIPv4Header().getOriginIPv4Address().hashCode());
        gpsrpRequestEntry.setPacketID(mplsPacket.getIPv4Header().getGoSGlobalUniqueIdentifier());
        int numberOfCrossedNodes = mplsPacket.getIPv4Header().getOptionsField().getNumberOfCrossedActiveNodes();
        int i = ZERO;
        String nextIPv4 = EMPTY_STRING;
        for (i = ZERO; i < numberOfCrossedNodes; i++) {
            nextIPv4 = mplsPacket.getIPv4Header().getOptionsField().getCrossedActiveNode(i);
            if (nextIPv4 != null) {
                gpsrpRequestEntry.setCrossedNodeIP(nextIPv4);
            }
        }
        entries.add(gpsrpRequestEntry);
        semaphore.setGreen();
        return gpsrpRequestEntry;
    }

    /**
     * This method removes a entry from the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID Flow ID of the flow the entry refers to.
     * @param packetID Packet ID the table refers to.
     * @since 2.0
     */
    public void removeEntry(int flowID, int packetID) {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            if (gpsrpRequestEntry.getFlowID() == flowID) {
                if (gpsrpRequestEntry.getPacketID() == packetID) {
                    iterator.remove();
                }
            }
        }
        semaphore.setGreen();
    }

    /**
     * This method obtains a specific entry from the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID Flow ID of the flow identifier the entry refers to.
     * @param packetID Packet ID the entry refers to.
     * @return Entry matching the specified arguments. Otherwise, NULL.
     * @since 2.0
     */
    public TGPSRPRequestEntry getEntry(int flowID, int packetID) {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            if (gpsrpRequestEntry.getFlowID() == flowID) {
                if (gpsrpRequestEntry.getPacketID() == packetID) {
                    semaphore.setGreen();
                    return gpsrpRequestEntry;
                }
            }
        }
        semaphore.setGreen();
        return null;
    }

    /**
     * This method updates the table. It removes all entries for which no
     * retransmission attemps are available and their timeouts have expired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void updateEntries() {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            if (gpsrpRequestEntry.isPurgeable()) {
                iterator.remove();
            } else {
                gpsrpRequestEntry.resetTimeout();
            }
        }
        semaphore.setGreen();
    }

    /**
     * This method drecreases the timeout for all entries of the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param nanoseconds Number of nanoseconds to be decreased from all entries
     * timeouts.
     * @since 2.0
     */
    public void decreaseTimeout(int nanoseconds) {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            gpsrpRequestEntry.decreaseTimeout(nanoseconds);
        }
        semaphore.setGreen();
    }

    /**
     * This method obtains the outgoing port ID of a specific entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID Flow ID of the flow the entry refers to.
     * @param packetID Packet ID the entry refers to.
     * @return Outgoing port of the entry maching the specified arguments.
     * @since 2.0
     */
    public int getOutgoingPort(int flowID, int packetID) {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            if (gpsrpRequestEntry.getFlowID() == flowID) {
                if (gpsrpRequestEntry.getPacketID() == packetID) {
                    semaphore.setGreen();
                    return gpsrpRequestEntry.getOutgoingPortID();
                }
            }
        }
        semaphore.setGreen();
        return -1;
    }

    /**
     * Thism method obtains the IP address of the following node that should be
     * requested for a packet retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID Flow ID of the flow of the desired entry.
     * @param packetID Packet ID of the desired entry.
     * @return IP address of the following node to be requested for a packet
     * retransmission. Otherwise, NULL.
     * @since 2.0
     */
    public String getActiveNodeIP(int flowID, int packetID) {
        semaphore.setRed();
        Iterator<TGPSRPRequestEntry> iterator = entries.iterator();
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (iterator.hasNext()) {
            gpsrpRequestEntry = iterator.next();
            if (gpsrpRequestEntry.getFlowID() == flowID) {
                if (gpsrpRequestEntry.getPacketID() == packetID) {
                    semaphore.setGreen();
                    return gpsrpRequestEntry.getCrossedNodeIPv4();
                }
            }
        }
        semaphore.setGreen();
        return null;
    }

    /**
     * This method obtains the iterator of all entries of the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Iterator of all entries in the table.
     * @since 2.0
     */
    public Iterator<TGPSRPRequestEntry> getEntriesIterator() {
        return entries.iterator();
    }

    /**
     * This method allow accesing the sync monitor of this table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Sync monitor of the table.
     * @since 2.0
     */
    public TSemaphore getMonitor() {
        return semaphore;
    }

    private static final int ZERO = 0;
    private static final String EMPTY_STRING = "";

    private TreeSet<TGPSRPRequestEntry> entries;
    private TRotaryIDGenerator idGenerator;
    private TSemaphore semaphore;
}
