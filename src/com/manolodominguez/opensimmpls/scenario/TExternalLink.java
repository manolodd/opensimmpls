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

import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventLinkBroken;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketDiscarded;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventLinkRecovered;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketOnFly;
import java.util.Iterator;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.utils.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;

/**
 * This class implements an external link of the topology (a link that is
 * outside the MPLS domain).
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TExternalLink extends TLink implements ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TExternalLink.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param linkID Unique identifier that identifies this link in the overall
     * topology.
     * @param longIDGenerator ID generator, to be used by this link to generate
     * distinguisible simulation events.
     * @param topology Topology this link belongs to.
     * @since 2.0
     */
    public TExternalLink(int linkID, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(linkID, longIDGenerator, topology);
        //FIX: Use class constants instead of harcoded values
        this.stepLength = 0;
    }

    /**
     * This metod return the type of this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TLink.EXTERNAL, that means that this is an external link.
     * @since 2.0
     */
    @Override
    public int getLinkType() {
        return TLink.EXTERNAL_LINK;
    }

    /**
     * This event receives a synchronization event from the simulation clock
     * that coordinates the global operation. This event allow the link to do
     * things during a short period of time.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param timerEvent Synchronization event received from the simulation
     * clock.
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent timerEvent) {
        this.setTickDuration(timerEvent.getStepDuration());
        this.setCurrentInstant(timerEvent.getUpperLimit());
        this.stepLength = timerEvent.getStepDuration();
        this.startOperation();
    }

    /**
     * This method establishes whether the links should be considered as a
     * broken one or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param linkIsBroken TRUE, means that the link is considered as broken.
     * FALSE means that it should be considered as up.
     * @since 2.0
     */
    @Override
    public void setAsBrokenLink(boolean linkIsBroken) {
        this.linkIsBroken = linkIsBroken;
        if (this.linkIsBroken) {
            try {
                this.generateSimulationEvent(new TSimulationEventLinkBroken(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant()));
                this.packetsInTransitEntriesLock.lock();
                TAbstractPDU packet = null;
                TLinkBufferEntry bufferedPacketEntry = null;
                Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
                while (bufferedPacketEntriesIterator.hasNext()) {
                    bufferedPacketEntry = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
                    packet = bufferedPacketEntry.getPacket();
                    if (packet != null) {
                        // FIX: do not use harcoded values. Use constants class
                        // instead
                        if (bufferedPacketEntry.getPacketEnd() == 1) {
                            this.generateSimulationEvent(new TSimulationEventPacketDiscarded(this.getTailEndNode(), this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packet.getSubtype()));
                            // FIX: do not use harcoded values. Use constants class
                            // instead
                        } else if (bufferedPacketEntry.getPacketEnd() == 2) {
                            this.generateSimulationEvent(new TSimulationEventPacketDiscarded(this.getHeadEndNode(), this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packet.getSubtype()));
                        }
                    }
                    bufferedPacketEntriesIterator.remove();
                }
                this.packetsInTransitEntriesLock.unLock();
            } catch (EIDGeneratorOverflow e) {
                // FIX: this is not a good practice
                e.printStackTrace();
            }
        } else {
            try {
                this.generateSimulationEvent(new TSimulationEventLinkRecovered(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant()));
            } catch (EIDGeneratorOverflow e) {
                // FIX: this is not a good practice
                e.printStackTrace();
            }
        }
    }

    /**
     * This method runs in it own thread and is started after a synchronization
     * event is received and only during the time specified in that
     * syncronization event. This is what the link does while running.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void run() {
        this.updateTransitDelay();
        this.advancePacketInTransit();
        this.deliverPacketsToDestination();
    }

    /**
     * This method pick up all packets in transit through this link and updates
     * their remaining transit delay to the destination node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void updateTransitDelay() {
        this.packetsInTransitEntriesLock.lock();
        Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry bufferedPacketEntry = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
            bufferedPacketEntry.substractStepFromRemainingDelay(this.stepLength);
            long transitPercentage = this.getCurrentTransitPercentage(bufferedPacketEntry.getTotalTransitDelay(), bufferedPacketEntry.getRemainingTransitDelay());
            // FIX: do not use harcoded values. Use constants class instead.
            if (bufferedPacketEntry.getPacketEnd() == 1) {
                // FIX: do not use harcoded values. Use constants class instead.
                transitPercentage = 100 - transitPercentage;
            }
            try {
                this.generateSimulationEvent(new TSimulationEventPacketOnFly(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), bufferedPacketEntry.getPacket().getSubtype(), transitPercentage));
            } catch (EIDGeneratorOverflow e) {
                // FIX: This is not a good practice.
                e.printStackTrace();
            }
        }
        this.packetsInTransitEntriesLock.unLock();
    }

    /**
     * This method pick up all packets in transit through this link and advances
     * them to the destination node. Also, it detects those packets that have
     * already reached the target node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void advancePacketInTransit() {
        this.packetsInTransitEntriesLock.lock();
        Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry bufferedPacketEntry = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
            // FIX: Do not use harcoded values. Use constants class instead.
            if (bufferedPacketEntry.getRemainingTransitDelay() <= 0) {
                this.deliveredPacketEntriesLock.lock();
                this.deliveredPacketsBuffer.add(bufferedPacketEntry);
                this.deliveredPacketEntriesLock.unLock();
            }
        }
        bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry bufferedPacketEntry = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
            // FIX: Do not use harcoded values. Use constants class instead.
            if (bufferedPacketEntry.getRemainingTransitDelay() <= 0) {
                bufferedPacketEntriesIterator.remove();
            }
        }
        this.packetsInTransitEntriesLock.unLock();
    }

    /**
     * This method pick up all packets that have already reached the target node
     * and deposits them in the corresponding port of that node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void deliverPacketsToDestination() {
        this.deliveredPacketEntriesLock.lock();
        Iterator deliveredPacketEntriesIterator = this.deliveredPacketsBuffer.iterator();
        while (deliveredPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry deliveredBufferedPacketEntry = (TLinkBufferEntry) deliveredPacketEntriesIterator.next();
            if (deliveredBufferedPacketEntry.getPacketEnd() == TLink.HEAD_END_NODE) {
                TNode nodeAux = this.getHeadEndNode();
                nodeAux.putPacket(deliveredBufferedPacketEntry.getPacket(), this.getHeadEndNodePortID());
            } else {
                TNode nodeAux = this.getTailEndNode();
                nodeAux.putPacket(deliveredBufferedPacketEntry.getPacket(), this.getTailEndNodePortID());
            }
            deliveredPacketEntriesIterator.remove();
        }
        this.deliveredPacketEntriesLock.unLock();
    }

    /**
     * This method gets the weight of this link to be used in the global routing
     * algoritm.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The weight measurement for this link. For an TExternalLink it is
     * the link delay.
     * @since 2.0
     */
    @Override
    public long getWeight() {
        long weight = this.getDelay();
        return weight;
    }

    /**
     * This method checks wheter this external link is well configured or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the configuration of this link is OK. Otherwise, returns
     * FALSE.
     * @since 2.0
     */
    @Override
    public boolean isWellConfigured() {
        // FIX: This method should be implemented correclty. In fact this does 
        // not do anything.
        return false;
    }

    /**
     * This method returns a human-readable message corresponding to a given
     * error code.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param errorCode El codigo de error que se quiere transformar.
     * @return El mensaje textual correspondiente a ese mensaje de error.
     * @since 2.0
     */
    @Override
    public String getErrorMessage(int errorCode) {
        // FIX: Review this method. In fact this is doing nothing and I cannot 
        // remember the reason. It could be a mistake. Perhaps it is an 
        // obligation because it's an abstract method in the superclass.
        return null;
    }

    /**
     * This method serializes the configuration parameters of this external link
     * into an string that can be saved into disk.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return an String containing all the configuration parameters of this
     * external link.
     * @since 2.0
     */
    @Override
    public String marshall() {
        String serializedElement = "#EnlaceExterno#";
        serializedElement += this.getID();
        serializedElement += "#";
        serializedElement += this.getName().replace('#', ' ');
        serializedElement += "#";
        serializedElement += this.getShowName();
        serializedElement += "#";
        serializedElement += this.getDelay();
        serializedElement += "#";
        serializedElement += this.getHeadEndNode().getIPv4Address();
        serializedElement += "#";
        serializedElement += this.getHeadEndNodePortID();
        serializedElement += "#";
        serializedElement += this.getTailEndNode().getIPv4Address();
        serializedElement += "#";
        serializedElement += this.getTailEndNodePortID();
        serializedElement += "#";
        return serializedElement;
    }

    /**
     * This method gets as an argument a serialized string that contains the
     * needed parameters to configure an TExternalLink and configure this
     * external link using them.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param serializedLink A serialized representation of a TExternalLink.
     * @return true, whether the serialized string is correct and this external
     * link has been initialized correctly using the serialized values.
     * Otherwise, false.
     * @since 2.0
     */
    @Override
    public boolean unMarshall(String serializedLink) {
        TLinkConfig linkConfig = new TLinkConfig();
        String[] elementFields = serializedLink.split("#");
        // FIX: Do not use harcoded values. This affect to the entire method. 
        // Use class constants instead.
        if (elementFields.length != 10) {
            return false;
        }
        this.getID(Integer.valueOf(elementFields[2]));
        linkConfig.setName(elementFields[3]);
        linkConfig.setShowName(Boolean.parseBoolean(elementFields[4]));
        linkConfig.setLinkDelay(Integer.parseInt(elementFields[5]));
        String ipv4AddressOfNodeAtEND1 = elementFields[6];
        String ipv4AddressOfNodeAtEND2 = elementFields[8];
        TNode nodeAtEnd1 = this.getTopology().getNode(ipv4AddressOfNodeAtEND1);
        TNode nodeAtEnd2 = this.getTopology().getNode(ipv4AddressOfNodeAtEND2);
        if (!((nodeAtEnd1 == null) || (nodeAtEnd2 == null))) {
            linkConfig.setHeadEndNodeName(nodeAtEnd1.getName());
            linkConfig.setTailEndNodeName(nodeAtEnd2.getName());
            linkConfig.setHeadEndNodePortID(Integer.parseInt(elementFields[7]));
            linkConfig.setTailEndNodePortID(Integer.parseInt(elementFields[9]));
            linkConfig.discoverLinkType(this.topology);
        } else {
            return false;
        }
        this.configure(linkConfig, this.topology, false);
        return true;
    }

    /**
     * This method reset all the values and attribues of this external link as
     * in the moment of its instantiation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        this.packetsInTransitEntriesLock.lock();
        Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            bufferedPacketEntriesIterator.next();
            bufferedPacketEntriesIterator.remove();
        }
        this.packetsInTransitEntriesLock.unLock();
        this.deliveredPacketEntriesLock.lock();
        Iterator deliveredPacketEntriesIterator = this.deliveredPacketsBuffer.iterator();
        while (deliveredPacketEntriesIterator.hasNext()) {
            deliveredPacketEntriesIterator.next();
            deliveredPacketEntriesIterator.remove();
        }
        this.deliveredPacketEntriesLock.unLock();
        this.setAsBrokenLink(false);
    }

    /**
     * This method gets the weight of this link to be used in the RABAN routing
     * algorithm. As RABAN does not operates in external links, this methohd
     * returns the same than the method getWeight().
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The RABAN weight measurement for this link. For an TExternalLink
     * it is the link delay.
     * @since 2.0
     */
    @Override
    public long getRABANWeight() {
        // There is no RABAN weight for external links, so, the usual weight is
        // returned.
        return this.getWeight();
    }

    private long stepLength;
}
