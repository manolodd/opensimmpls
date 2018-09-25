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

import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketSent;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketGenerated;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketDiscarded;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventNodeCongested;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketRouted;
import com.manolodominguez.opensimmpls.protocols.TGPSRPPDU;
import com.manolodominguez.opensimmpls.protocols.TTLDPPDU;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSLabel;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.protocols.TTLDPPayload;
import com.manolodominguez.opensimmpls.protocols.TIPv4PDU;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPort;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrix;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrixEntry;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPortSet;
import com.manolodominguez.opensimmpls.hardware.ports.TPort;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.commons.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.commons.TIDGenerator;
import com.manolodominguez.opensimmpls.commons.TLongIDGenerator;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.awt.Point;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * This class implements an Label Edge Router (LER) node that will allow network
 * traffic to entry/exit to/from the MPLS domain.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLERNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TActiveLERNode.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param identifier the identifier of this active LER node that allow
     * referencing switchingMatrixIterator in the topology.
     * @param ipv4Address The IPv4 address assigned to this active LER.
     * @param longIDGenerator The idntifier generator that the active LER will
     * use to identify unambiguosly each event the switchingMatrixIterator
     * generates.
     * @param topology A reference to the topology this active LER belongs to.
     * @since 2.0
     */
    public TLERNode(int identifier, String ipv4Address, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(identifier, ipv4Address, longIDGenerator, topology);
        // FIX: This is an overridable method call in constructor that should be 
        // avoided.
        this.setPorts(TNode.DEFAULT_NUM_PORTS_LER);
        this.switchingMatrix = new TSwitchingMatrix();
        this.gIdent = new TLongIDGenerator();
        this.gIdentLDP = new TIDGenerator();
        //FIX: replace with class constants.
        this.routingPowerInMbps = 512;
        this.stats = new TLERStats();
        this.translations = ResourceBundle.getBundle(AvailableBundles.LER_NODE.getPath());
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to switch a single bit. This is something that depends on the
     * switching power of this LER.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the number of nanoseconds that are needed to switch a single bit.
     * @since 2.0
     */
    public double getNsPerBit() {
        // FIX: replace al harcoded values for class constants
        long bitsPerSecondRate = (long) (this.routingPowerInMbps * 1048576L);
        double nsPerBit = (double) ((double) 1000000000.0 / (long) bitsPerSecondRate);
        return nsPerBit;
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to switch the specified number of octects. This is something that
     * depends on the switching power of this LER.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param octects the number of octects that wants to be switched.
     * @return the number of nanoseconds that are needed to switch the specified
     * number of octects.
     * @since 2.0
     */
    public double getNsRequiredForAllOctets(int octects) {
        double nsPerBit = this.getNsPerBit();
        // FIX: replace al harcoded values for class constants
        long numberOfBits = (long) ((long) octects * (long) 8);
        return (double) ((double) nsPerBit * (long) numberOfBits);
    }

    /**
     * This method gets the number of bits that this LER can route with the
     * available number of nanoseconds switchingMatrixIterator has. The more
     * switching power the LER has, the more bits switchingMatrixIterator can
     * switch with the same number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the number of bits the node can route with the current available
     * number of nanoseconds.
     * @since 2.0
     */
    public int getMaxRouteableBitsWithCurrentNs() {
        double nsPerBit = this.getNsPerBit();
        double maxNumberOfBits = (double) ((double) this.availableNanoseconds / (double) nsPerBit);
        return (int) maxNumberOfBits;
    }

    /**
     * This method gets the number of octects that this LER can route with the
     * available number of nanoseconds switchingMatrixIterator has. The more
     * switching power the LER has, the more octects switchingMatrixIterator can
     * switch with the same number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the number of octects the node can route with the current
     * available number of nanoseconds.
     * @since 2.0
     */
    public int getMaxRouteableOctectsWithCurrentNs() {
        // FIX: replace al harcoded values for class constants
        double maxNumberOfOctects = ((double) this.getMaxRouteableBitsWithCurrentNs() / (double) 8.0);
        return (int) maxNumberOfOctects;
    }

    /**
     * This method gets the switching power of this LER, in Mbps.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the routing power of the node in Mbps.
     * @since 2.0
     */
    public int getRoutingPowerInMbps() {
        return this.routingPowerInMbps;
    }

    /**
     * This method sets the switching power of this LER, in Mbps.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param routingPowerInMbps the routing power of this LER, in Mbps.
     * @since 2.0
     */
    public void setRoutingPowerInMbps(int routingPowerInMbps) {
        this.routingPowerInMbps = routingPowerInMbps;
    }

    /**
     * This method gets the size of this LER's buffer, in MBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the size of this LER's buffer, in MBytes.
     * @since 2.0
     */
    public int getBufferSizeInMBytes() {
        return this.getPorts().getBufferSizeInMBytes();
    }

    /**
     * This method sets the size of this LER's buffer, in MBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param bufferSizeInMBytes the size of this LER's buffer, in MBytes.
     * @since 2.0
     */
    public void setBufferSizeInMBytes(int bufferSizeInMBytes) {
        this.getPorts().setBufferSizeInMB(bufferSizeInMBytes);
    }

    /**
     * This method restart the attributes of the class as in the creation of the
     * instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        this.ports.reset();
        this.switchingMatrix.reset();
        this.gIdent.reset();
        this.gIdentLDP.reset();
        this.stats.reset();
        this.stats.setStatsEnabled(this.isGeneratingStats());
        this.resetTicksWithoutEmitting();
    }

    /**
     * This method gets the type of this node. In this case,
     * switchingMatrixIterator will return the constant TNode.LER.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the constant TNode.LER.
     * @since 2.0
     */
    @Override
    public int getNodeType() {
        return TNode.LER;
    }

    /**
     * This method receive a timer event from the simulation's global timer. It
     * does some initial tasks and then wake up the LER to start doing its work.
     *
     * @param timerEvent a timer event that is used to synchronize all nodes and
     * that inclues a number of nanoseconds to be used by the LER.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent timerEvent) {
        this.setTickDurationInNs(timerEvent.getTickDurationInNs());
        this.setCurrentTimeInstant(timerEvent.getUpperLimit());
        if (this.getPorts().isThereAnyPacketToRoute()) {
            this.availableNanoseconds += timerEvent.getTickDurationInNs();
        } else {
            this.resetTicksWithoutEmitting();
            this.availableNanoseconds = timerEvent.getTickDurationInNs();
        }
        this.startOperation();
    }

    /**
     * This method starts all tasks that has to be executed during a timer tick.
     * The number of nanoseconds of this tick is the time this LER will work.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void run() {
        // Actions to be done during the timer tick.
        try {
            this.generateSimulationEvent(new TSimulationEventNodeCongested(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), this.getPorts().getCongestionLevel()));
        } catch (Exception e) {
            // FIX: this is not a good practice. Avoid.
            e.printStackTrace();
        }
        this.checkConnectivityStatus();
        this.decreaseCounters();
        this.routePackets();
        this.stats.groupStatsByTimeInstant(this.getCurrentTimeInstant());
    }

    /**
     * This method check wether the connectivity to the neighbors nodes exists.
     * Let's say, this check whether a link of this node is down. If so, this
     * method generates the corresponding event to notify the situation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void checkConnectivityStatus() {
        boolean removeSwitchingMatrixEntry = false;
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        // FIX: Avoid using harcoded values
        int portIDAux = 0;
        TPort outgoingPort = null;
        TPort incomingPort = null;
        TLink linkAux1 = null;
        TLink linkAux2 = null;
        this.switchingMatrix.getMonitor().lock();
        Iterator switchingMatrixIterator = switchingMatrix.getEntriesIterator();
        while (switchingMatrixIterator.hasNext()) {
            switchingMatrixEntry = (TSwitchingMatrixEntry) switchingMatrixIterator.next();
            if (switchingMatrixEntry != null) {
                portIDAux = switchingMatrixEntry.getOutgoingPortID();
                // FIX: Avoid using harcoded values
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    outgoingPort = this.ports.getPort(portIDAux);
                    if (outgoingPort != null) {
                        linkAux1 = outgoingPort.getLink();
                        if (linkAux1 != null) {
                            if ((linkAux1.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                incomingPort = this.ports.getPort(switchingMatrixEntry.getIncomingPortID());
                                linkAux1 = incomingPort.getLink();
                                if (linkAux1.getLinkType() == TLink.INTERNAL_LINK) {
                                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
                                } else {
                                    removeSwitchingMatrixEntry = true;
                                }
                            }
                        }
                    }
                }
                portIDAux = switchingMatrixEntry.getIncomingPortID();
                // FIX: Avoid using harcoded values
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    incomingPort = this.ports.getPort(portIDAux);
                    if (incomingPort != null) {
                        linkAux1 = incomingPort.getLink();
                        if (linkAux1 != null) {
                            if ((linkAux1.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                                linkAux1 = outgoingPort.getLink();
                                if (linkAux1.getLinkType() == TLink.INTERNAL_LINK) {
                                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
                                } else {
                                    removeSwitchingMatrixEntry = false;
                                }
                            }
                        }
                    }
                }
                // FIX: Avoid using harcoded values
                if ((switchingMatrixEntry.getIncomingPortID() >= 0) && ((switchingMatrixEntry.getOutgoingPortID() >= 0))) {
                    linkAux1 = this.ports.getPort(switchingMatrixEntry.getIncomingPortID()).getLink();
                    linkAux2 = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID()).getLink();
                    if (linkAux1.isBroken() && linkAux2.isBroken()) {
                        removeSwitchingMatrixEntry = true;
                    }
                    if (linkAux1.isBroken() && (linkAux2.getLinkType() == TLink.EXTERNAL_LINK)) {
                        removeSwitchingMatrixEntry = true;
                    }
                    if ((linkAux1.getLinkType() == TLink.EXTERNAL_LINK) && linkAux2.isBroken()) {
                        removeSwitchingMatrixEntry = true;
                    }
                } else {
                    removeSwitchingMatrixEntry = true;
                }
                if (removeSwitchingMatrixEntry) {
                    switchingMatrixIterator.remove();
                }
            } else {
                switchingMatrixIterator.remove();
            }
        }
        this.switchingMatrix.getMonitor().unLock();
    }

    /**
     * This method read read the port to which entriesIterator is up following a
     * Round Robin algorithm. It does that until entriesIterator consumes all
     * the available nanoseconds for the current tick/step. If entriesIterator
     * is able to switch or route a given incoming packet, entriesIterator does
     * entriesIterator. If entriesIterator is a martian packet, the packet is
     * discarded.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void routePackets() {
        boolean atLeastOnePacketRouted = false;
        int readPort = 0;
        TAbstractPDU packet = null;
        int routeableOctectsWithCurrentNs = this.getMaxRouteableOctectsWithCurrentNs();
        while (this.getPorts().canSwitchPacket(routeableOctectsWithCurrentNs)) {
            atLeastOnePacketRouted = true;
            packet = this.ports.getNextPacket();
            readPort = this.ports.getReadPort();
            if (packet != null) {
                // FIX: Convert to a switch statement
                if (packet.getType() == TAbstractPDU.IPV4) {
                    this.handleIPv4Packet((TIPv4PDU) packet, readPort);
                } else if (packet.getType() == TAbstractPDU.TLDP) {
                    this.handleTLDPPacket((TTLDPPDU) packet, readPort);
                } else if (packet.getType() == TAbstractPDU.MPLS) {
                    this.handleMPLSPacket((TMPLSPDU) packet, readPort);
                } else if (packet.getType() == TAbstractPDU.GPSRP) {
                    this.handleGPSRPPacket((TGPSRPPDU) packet, readPort);
                } else {
                    this.availableNanoseconds += this.getNsRequiredForAllOctets(packet.getSize());
                    this.discardPacket(packet);
                }
                this.availableNanoseconds -= this.getNsRequiredForAllOctets(packet.getSize());
                routeableOctectsWithCurrentNs = this.getMaxRouteableOctectsWithCurrentNs();
            }
        }
        if (atLeastOnePacketRouted) {
            this.resetTicksWithoutEmitting();
        } else {
            this.increaseTicksWithoutEmitting();
        }
    }

    /**
     * This method switchs an incoming GPDRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet GPSRP packet to route.
     * @param incomingPortID Port of this node where the packet has arrived.
     * @since 2.0
     */
    public void handleGPSRPPacket(TGPSRPPDU packet, int incomingPortID) {
        if (packet != null) {
            // FIX: flowID, messageType and packetID seems not to be used. If 
            // not necessary, remove from the code.
            int messageType = packet.getGPSRPPayload().getGPSRPMessageType();
            int flowID = packet.getGPSRPPayload().getFlowID();
            int packetID = packet.getGPSRPPayload().getPacketID();
            String targetIPv4Address = packet.getIPv4Header().getTailEndIPAddress();
            TFIFOPort outgoingPort = null;
            if (targetIPv4Address.equals(this.getIPv4Address())) {
                // A LER node does not understand GPRS. So no GPRS packets can
                // be sent to this node.
                this.discardPacket(packet);
            } else {
                String nextHopIPv4Address = this.topology.getFloydWarsallNextHopIPv4Address(this.getIPv4Address(), targetIPv4Address);
                outgoingPort = (TFIFOPort) this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
                if (outgoingPort != null) {
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else {
                    this.discardPacket(packet);
                }
            }
        }
    }

    /**
     * This method cheks whether there is an entry for the incoming IPv4 packet
     * in the routing table. If not, this method classifies the packet and, if
     * needed, entriesIterator re-enqueues the packet and requests a label to be
     * able to relay the packet. Once an entry for this packet is in the routing
     * table, entriesIterator sends the packet into the MPLS domain or outward,
     * as appropriate.
     *
     * @param packet Incoming IPv4 packet.
     * @param incomingPortID Port of this node from wich the IPv4 packet has
     * arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleIPv4Packet(TIPv4PDU packet, int incomingPortID) {
        int fec = this.classifyPacket(packet);
        String targetIPv4Address = packet.getIPv4Header().getTailEndIPAddress();
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        switchingMatrixEntry = this.switchingMatrix.getEntry(incomingPortID, fec, TSwitchingMatrixEntry.FEC_ENTRY);
        if (switchingMatrixEntry == null) {
            switchingMatrixEntry = this.createInitialEntryInFECMatrix(packet, incomingPortID);
            if (switchingMatrixEntry != null) {
                if (!this.isExitLER(targetIPv4Address)) {
                    switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    this.requestTLDP(switchingMatrixEntry);
                }
                this.ports.getPort(incomingPortID).reEnqueuePacket(packet);
            }
        }
        if (switchingMatrixEntry != null) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                this.requestTLDP(switchingMatrixEntry);
                this.ports.getPort(switchingMatrixEntry.getIncomingPortID()).reEnqueuePacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                this.ports.getPort(switchingMatrixEntry.getIncomingPortID()).reEnqueuePacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                this.discardPacket(packet);
            } else if ((currentLabel > 15) || (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operation = switchingMatrixEntry.getLabelStackOperation();
                // FIX: Use Switch statement instead of chained ifs
                if (operation == TSwitchingMatrixEntry.UNDEFINED) {
                    this.discardPacket(packet);
                } else if (operation == TSwitchingMatrixEntry.PUSH_LABEL) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    TMPLSPDU mplsPacket = this.createMPLSPacket(packet, switchingMatrixEntry);
                    outgoingPort.putPacketOnLink(mplsPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.POP_LABEL) {
                    this.discardPacket(packet);
                } else if (operation == TSwitchingMatrixEntry.SWAP_LABEL) {
                    this.discardPacket(packet);
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: Avoid this. This is not a good practice.
                        e.printStackTrace();
                    }
                }
            } else {
                this.discardPacket(packet);
            }
        } else {
            this.discardPacket(packet);
        }
    }

    /**
     * This method is called when a TLDP packet is received. It performs the
     * required action in the routing matrix and propagates the changes to the
     * correspondant adjacent node.
     *
     * @param packet TLDP packet received.
     * @param incomingPortID Port of this node from wich the TLDP packet has
     * arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleTLDPPacket(TTLDPPDU packet, int incomingPortID) {
        // FIX: change by a Switch statement
        if (packet.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST) {
            this.handleTLDPRequest(packet, incomingPortID);
        } else if (packet.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST_OK) {
            this.handleTLDPRequestOk(packet, incomingPortID);
        } else if (packet.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST_DENIED) {
            this.handleTLDPRefuseRequest(packet, incomingPortID);
        } else if (packet.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REMOVAL_REQUEST) {
            this.handleTLDPWithdrawal(packet, incomingPortID);
        } else if (packet.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REVOMAL_REQUEST_OK) {
            this.handleTLDPWithdrawalOk(packet, incomingPortID);
        }
    }

    /**
     * This method checks whether there is an entry for the incoming MPLS packet
     * in the routing table. If not, this method classifies the packet and, if
     * needed, entriesIterator re-enqueues the packet and requests a label to be
     * able to relay the packet. Once an entry for this packet is in the routing
     * table, entriesIterator sends the packet into the MPLS domain or outward,
     * as appropriate.
     *
     * @param packet MPLS packet received.
     * @param incomingPortID Port of this node from wich the MPLS packet has
     * arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleMPLSPacket(TMPLSPDU packet, int incomingPortID) {
        TMPLSLabel mplsLabel = null;
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        boolean isLabeled = false;
        // FIX: Do not use harcoded values. Use class constants instead.
        if (packet.getLabelStack().getTop().getLabel() == 1) {
            mplsLabel = packet.getLabelStack().getTop();
            packet.getLabelStack().popTop();
            isLabeled = true;
        }
        int labelValue = packet.getLabelStack().getTop().getLabel();
        String targetIPv4Address = packet.getIPv4Header().getTailEndIPAddress();
        switchingMatrixEntry = this.switchingMatrix.getEntry(incomingPortID, labelValue, TSwitchingMatrixEntry.LABEL_ENTRY);
        if (switchingMatrixEntry == null) {
            switchingMatrixEntry = this.createInitialEntryInILMMatrix(packet, incomingPortID);
            if (switchingMatrixEntry != null) {
                if (!isExitLER(targetIPv4Address)) {
                    switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    this.requestTLDP(switchingMatrixEntry);
                }
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                this.ports.getPort(incomingPortID).reEnqueuePacket(packet);
            }
        }
        if (switchingMatrixEntry != null) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                this.requestTLDP(switchingMatrixEntry);
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                this.ports.getPort(switchingMatrixEntry.getIncomingPortID()).reEnqueuePacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                this.ports.getPort(switchingMatrixEntry.getIncomingPortID()).reEnqueuePacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                this.discardPacket(packet);
                // FIX: Do not use hardcoded values. Use class constants instead.
            } else if ((currentLabel > 15) || (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operation = switchingMatrixEntry.getLabelStackOperation();
                // FIX: Replace conditional by Switch statement
                if (operation == TSwitchingMatrixEntry.UNDEFINED) {
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                    }
                    discardPacket(packet);
                } else if (operation == TSwitchingMatrixEntry.PUSH_LABEL) {
                    TMPLSLabel mplsLabelAux = new TMPLSLabel();
                    mplsLabelAux.setBoS(false);
                    mplsLabelAux.setEXP(0);
                    mplsLabelAux.setLabel(switchingMatrixEntry.getOutgoingLabel());
                    mplsLabelAux.setTTL(packet.getLabelStack().getTop().getTTL() - 1);
                    packet.getLabelStack().pushTop(mplsLabelAux);
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                        packet.setSubtype(TAbstractPDU.MPLS_GOS);
                    } else {
                        packet.setSubtype(TAbstractPDU.MPLS);
                    }
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.POP_LABEL) {
                    if (packet.getLabelStack().getTop().getBoS()) {
                        TIPv4PDU ipv4Packet = this.createIPv4Packet(packet, switchingMatrixEntry);
                        if (isLabeled) {
                            ipv4Packet.setSubtype(TAbstractPDU.IPV4_GOS);
                        }
                        TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                        outgoingPort.putPacketOnLink(ipv4Packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    } else {
                        packet.getLabelStack().popTop();
                        if (isLabeled) {
                            packet.getLabelStack().pushTop(mplsLabel);
                        }
                        TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                        outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    }
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.SWAP_LABEL) {
                    packet.getLabelStack().getTop().setLabel(switchingMatrixEntry.getOutgoingLabel());
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                    }
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                }
            } else {
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                this.discardPacket(packet);
            }
        } else {
            if (isLabeled) {
                packet.getLabelStack().pushTop(mplsLabel);
            }
            this.discardPacket(packet);
        }
    }

    /**
     * This method handles a label request.
     *
     * @param packet Label request received from an adjacent node.
     * @param incomingPortID Port of this node from wich the label request has
     * arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleTLDPRequest(TTLDPPDU packet, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        switchingMatrixEntry = this.switchingMatrix.getEntry(packet.getTLDPPayload().getTLDPIdentifier(), incomingPortID);
        if (switchingMatrixEntry == null) {
            switchingMatrixEntry = this.createEntryFromTLDP(packet, incomingPortID);
        }
        if (switchingMatrixEntry != null) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                this.requestTLDP(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                // Do nothing. The LER is waiting for a label
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                this.sendTLDPRequestRefuse(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                this.sendTLDPRequestOk(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                this.sendTLDPWithdrawal(switchingMatrixEntry, incomingPortID);
                // FIX: Do not use hardcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                this.sendTLDPRequestOk(switchingMatrixEntry);
            } else {
                this.discardPacket(packet);
            }
        } else {
            this.discardPacket(packet);
        }
    }

    /**
     * This method handles a label withdrawal.
     *
     * @param packet Label withdrawal received.
     * @param incomingPortID Port of this node from wich the label withdrawal
     * has arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleTLDPWithdrawal(TTLDPPDU packet, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        if (packet.getLocalOrigin() == TTLDPPDU.CAME_BY_ENTRANCE) {
            switchingMatrixEntry = this.switchingMatrix.getEntry(packet.getTLDPPayload().getTLDPIdentifier(), incomingPortID);
        } else {
            switchingMatrixEntry = this.switchingMatrix.getEntry(packet.getTLDPPayload().getTLDPIdentifier());
        }
        if (switchingMatrixEntry == null) {
            this.discardPacket(packet);
        } else if (switchingMatrixEntry.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                this.sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                this.sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                this.sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                this.sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                this.sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentLabel > 15) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                this.sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else {
                this.discardPacket(packet);
            }
        } else {
            this.sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
            this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
        }
    }

    /**
     * This method handles a label acknowledge.
     *
     * @param packet Label acknowledge received.
     * @param incomingPortID Port of this node from wich the label acknowledge
     * has arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleTLDPRequestOk(TTLDPPDU packet, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        switchingMatrixEntry = this.switchingMatrix.getEntry(packet.getTLDPPayload().getTLDPIdentifier());
        if (switchingMatrixEntry == null) {
            this.discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(packet.getTLDPPayload().getLabel());
                if (switchingMatrixEntry.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                }
                TInternalLink internalLink = (TInternalLink) ports.getPort(switchingMatrixEntry.getOutgoingPortID()).getLink();
                if (internalLink != null) {
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        internalLink.setAsUsedByABackupLSP();
                    } else {
                        internalLink.setAsUsedByALSP();
                    }
                }
                this.sendTLDPRequestOk(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                this.discardPacket(packet);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentLabel > 15) {
                this.discardPacket(packet);
            } else {
                this.discardPacket(packet);
            }
        }
    }

    /**
     * This method handles a received TLDP packet containing a label refusal.
     *
     * @param packet Label refusal received.
     * @param incomingPortID Port of this node from wich the label refusal has
     * arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleTLDPRefuseRequest(TTLDPPDU packet, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        switchingMatrixEntry = this.switchingMatrix.getEntry(packet.getTLDPPayload().getTLDPIdentifier());
        if (switchingMatrixEntry == null) {
            this.discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                this.sendTLDPRequestRefuse(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                this.discardPacket(packet);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentLabel > 15) {
                this.discardPacket(packet);
            } else {
                this.discardPacket(packet);
            }
        }
    }

    /**
     * This method handles a TLDP packet containing a label withdrawal
     * acknowledgement.
     *
     * @param packet Label withdrawal acknowledgement received.
     * @param incomingPortID Port of this node from wich the label withdrawal
     * acknowledgement has arrived.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void handleTLDPWithdrawalOk(TTLDPPDU packet, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        if (packet.getLocalOrigin() == TTLDPPDU.CAME_BY_ENTRANCE) {
            switchingMatrixEntry = this.switchingMatrix.getEntry(packet.getTLDPPayload().getTLDPIdentifier(), incomingPortID);
        } else {
            switchingMatrixEntry = this.switchingMatrix.getEntry(packet.getTLDPPayload().getTLDPIdentifier());
        }
        if (switchingMatrixEntry == null) {
            this.discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                this.discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                if (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    TPort outgoingPort = this.ports.getPort(incomingPortID);
                    TLink link = outgoingPort.getLink();
                    if (link.getLinkType() == TLink.INTERNAL_LINK) {
                        TInternalLink internalLink = (TInternalLink) link;
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            internalLink.unlinkFromABackupLSP();
                        } else {
                            internalLink.unlinkFromALSP();
                        }
                    }
                }
                this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentLabel > 15) {
                this.discardPacket(packet);
            } else {
                this.discardPacket(packet);
            }
        }
    }

    /**
     * This methods sends a label to a node that is specified in the switching
     * matrix entry specified as an argument.
     *
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPRequestOk(TSwitchingMatrixEntry switchingMatrixEntry) {
        if (switchingMatrixEntry != null) {
            if (switchingMatrixEntry.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String localIPv4Address = this.getIPv4Address();
                String targetIPv4Address = this.ports.getIPv4OfNodeLinkedTo(switchingMatrixEntry.getIncomingPortID());
                if (targetIPv4Address != null) {
                    TTLDPPDU tldpPacket = null;
                    try {
                        tldpPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, targetIPv4Address);
                    } catch (Exception e) {
                        // FIX: this is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                    if (tldpPacket != null) {
                        tldpPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_OK);
                        tldpPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPv4Address());
                        tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        tldpPacket.getTLDPPayload().setLabel(switchingMatrixEntry.getLabelOrFEC());
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(targetIPv4Address);
                        outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            // FIX: this is not a good practice. Avoid.
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This methods sends a label refusal to a node that is specified in the
     * switching matrix entry specified as an argument.
     *
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPRequestRefuse(TSwitchingMatrixEntry switchingMatrixEntry) {
        if (switchingMatrixEntry != null) {
            if (switchingMatrixEntry.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String localIPv4Address = this.getIPv4Address();
                String targetIPv4Address = this.ports.getIPv4OfNodeLinkedTo(switchingMatrixEntry.getIncomingPortID());
                if (targetIPv4Address != null) {
                    TTLDPPDU tldpPacket = null;
                    try {
                        tldpPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, targetIPv4Address);
                    } catch (Exception e) {
                        // FIX: this is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                    if (tldpPacket != null) {
                        tldpPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_DENIED);
                        tldpPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPv4Address());
                        tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        tldpPacket.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort outgoingPort = ports.getLocalPortConnectedToANodeWithIPAddress(targetIPv4Address);
                        outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            //FIX: this is not a good practice. Avoid.
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This method sends a label withdrawal acknowledgement to the node
     * specified in the corresponding switching matrix entry.
     *
     * @since 2.0
     * @param portID Port of this node from wich the label withdrawal
     * acknowledgement will be sent.
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPWithdrawalOk(TSwitchingMatrixEntry switchingMatrixEntry, int portID) {
        if (switchingMatrixEntry != null) {
            String localIPv4Address = this.getIPv4Address();
            String targetIPv4Address = this.ports.getIPv4OfNodeLinkedTo(portID);
            if (targetIPv4Address != null) {
                TTLDPPDU tldpPacket = null;
                try {
                    tldpPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, targetIPv4Address);
                } catch (Exception e) {
                    // FIX: this is not a good practice. Avoid.
                    e.printStackTrace();
                }
                if (tldpPacket != null) {
                    tldpPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REVOMAL_REQUEST_OK);
                    tldpPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPv4Address());
                    tldpPacket.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                    if (switchingMatrixEntry.getOutgoingPortID() == portID) {
                        tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                        tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (switchingMatrixEntry.getIncomingPortID() == portID) {
                        tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort outgoingPort = this.ports.getPort(portID);
                    outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        // FIX: this is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method requests a label from the node contained in the switching
     * matrix entry specified as an argument.
     *
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void requestTLDP(TSwitchingMatrixEntry switchingMatrixEntry) {
        String localIPv4Address = this.getIPv4Address();
        String tailEndIPv4Address = switchingMatrixEntry.getTailEndIPv4Address();
        if (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
            String nextHopIPv4Address = this.topology.getFloydWarsallNextHopIPv4Address(localIPv4Address, tailEndIPv4Address);
            if (nextHopIPv4Address != null) {
                TTLDPPDU tldpPacket = null;
                try {
                    tldpPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, nextHopIPv4Address);
                } catch (Exception e) {
                    // FIX: this is not a good practice. Avoid.
                    e.printStackTrace();
                }
                if (tldpPacket != null) {
                    tldpPacket.getTLDPPayload().setTargetIPAddress(tailEndIPv4Address);
                    tldpPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                    tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        // FIX: Use class constants instead of true/false 
                        // harcoded values
                        tldpPacket.setLSPType(true);
                    } else {
                        // FIX: Use class constants instead of true/false 
                        // harcoded values
                        tldpPacket.setLSPType(false);
                    }
                    tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
                    if (outgoingPort != null) {
                        outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            // FIX: this is not a good practice. Avoid.
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This method sends a label withdrawal to the node included in the
     * switching matrix entry specified as an argument.
     *
     * @since 2.0
     * @param portID Port of this node from wich the label withdrawal will be
     * sent.
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPWithdrawal(TSwitchingMatrixEntry switchingMatrixEntry, int portID) {
        if (switchingMatrixEntry != null) {
            switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
            String localIPv4Address = this.getIPv4Address();
            String tailEndIPv4Address = switchingMatrixEntry.getTailEndIPv4Address();
            String nextHopIPv4Address = this.ports.getIPv4OfNodeLinkedTo(portID);
            if (nextHopIPv4Address != null) {
                TTLDPPDU tldpPacket = null;
                try {
                    tldpPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, nextHopIPv4Address);
                } catch (Exception e) {
                    // FIX: this is ugly. Avoid.
                    e.printStackTrace();
                }
                if (tldpPacket != null) {
                    tldpPacket.getTLDPPayload().setTargetIPAddress(tailEndIPv4Address);
                    tldpPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REMOVAL_REQUEST);
                    if (switchingMatrixEntry.getOutgoingPortID() == portID) {
                        tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                        tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (switchingMatrixEntry.getIncomingPortID() == portID) {
                        tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort outgoingPort = this.ports.getPort(portID);
                    if (outgoingPort != null) {
                        outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            // FIX: this is ugly. Avoid.
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This method re-sends all the TLDP requests that are waiting for an
     * answer, related to the specified switching matrix entry, after a timeout
     * expiration.
     *
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void requestTLDPAfterTimeout(TSwitchingMatrixEntry switchingMatrixEntry) {
        if (switchingMatrixEntry != null) {
            String localIPv4Address = this.getIPv4Address();
            String tailEndIPv4Address = switchingMatrixEntry.getTailEndIPv4Address();
            String nextHopIPv4Address = this.ports.getIPv4OfNodeLinkedTo(switchingMatrixEntry.getOutgoingPortID());
            if (nextHopIPv4Address != null) {
                TTLDPPDU tldpPacket = null;
                try {
                    tldpPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, nextHopIPv4Address);
                } catch (Exception e) {
                    // FIX: this is ugly. Avoid.
                    e.printStackTrace();
                }
                if (tldpPacket != null) {
                    tldpPacket.getTLDPPayload().setTargetIPAddress(tailEndIPv4Address);
                    tldpPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                    tldpPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        // FIX: Avoid using harcoded values. Use class constants
                        // instead
                        tldpPacket.setLSPType(true);
                    } else {
                        // FIX: Avoid using harcoded values. Use class constants
                        // instead
                        tldpPacket.setLSPType(false);
                    }
                    tldpPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    if (outgoingPort != null) {
                        outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            // FIX: this is ugly. Avoid.
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This method re-sends all the TLDP withdrawals that are pending, related
     * to the specified switching matrix entry, after a timeout expiration.
     *
     * @since 2.0
     * @param portID Port of this node from wich the label withdrawals will be
     * sent.
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry switchingMatrixEntry, int portID) {
        this.sendTLDPWithdrawal(switchingMatrixEntry, portID);
    }

    /**
     * This method re-sends all the TLDP withdrawals that are pending, related
     * to the specified switching matrix entry, after a timeout expiration. TLDP
     * withdrawals will be sent by each port specified in the switching matrix
     * entry.
     *
     * @since 2.0
     * @param switchingMatrixEntry The switching matrix entry specified.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry switchingMatrixEntry) {
        this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
        this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
    }

    /**
     * This method decreases all retransmission counters for the this node.
     *
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void decreaseCounters() {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        this.switchingMatrix.getMonitor().lock();
        Iterator entriesIterator = this.switchingMatrix.getEntriesIterator();
        while (entriesIterator.hasNext()) {
            switchingMatrixEntry = (TSwitchingMatrixEntry) entriesIterator.next();
            if (switchingMatrixEntry != null) {
                switchingMatrixEntry.decreaseTimeOut(this.getTickDurationInNs());
                // FIX: It is more efficient to use a switch clause instead of
                // nested ifs.
                if (switchingMatrixEntry.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    if (switchingMatrixEntry.shouldRetryExpiredTLDPRequest()) {
                        switchingMatrixEntry.resetTimeOut();
                        switchingMatrixEntry.decreaseAttempts();
                        this.requestTLDPAfterTimeout(switchingMatrixEntry);
                    }
                } else if (switchingMatrixEntry.getOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    if (switchingMatrixEntry.shouldRetryExpiredTLDPRequest()) {
                        switchingMatrixEntry.resetTimeOut();
                        switchingMatrixEntry.decreaseAttempts();
                        this.labelWithdrawalAfterTimeout(switchingMatrixEntry);
                    } else if (!switchingMatrixEntry.areThereAvailableAttempts()) {
                        entriesIterator.remove();
                    }
                } else {
                    switchingMatrixEntry.resetTimeOut();
                    switchingMatrixEntry.resetAttempts();
                }
            }
        }
        this.switchingMatrix.getMonitor().unLock();
    }

    /**
     * This method creates a new entry in the switching matrix using data from
     * an incoming TLDP packet.
     *
     * @param tldpPacket the incoming packet. A label request.
     * @param incomingPortID the port by wich the TLDP packet has arrived.
     * @return A new switching matrix entry, already initialized and inserted in
     * the switching matrix of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public TSwitchingMatrixEntry createEntryFromTLDP(TTLDPPDU tldpPacket, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        int predecessorTLDPID = tldpPacket.getTLDPPayload().getTLDPIdentifier();
        TPort incomingPort = this.ports.getPort(incomingPortID);
        String tailEndIPv4Address = tldpPacket.getTLDPPayload().getTailEndIPAddress();
        String nextHopIPv4Address = this.topology.getFloydWarsallNextHopIPv4Address(this.getIPv4Address(), tailEndIPv4Address);
        if (nextHopIPv4Address != null) {
            TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
            int incomingLink = TLink.EXTERNAL_LINK;
            int outgoingLink = TLink.INTERNAL_LINK;
            switchingMatrixEntry = new TSwitchingMatrixEntry();
            switchingMatrixEntry.setUpstreamTLDPSessionID(predecessorTLDPID);
            switchingMatrixEntry.setTailEndIPAddress(tailEndIPv4Address);
            switchingMatrixEntry.setIncomingPortID(incomingPortID);
            switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setLabelOrFEC(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setEntryAsForBackupLSP(tldpPacket.getLSPType());
            if (outgoingPort != null) {
                switchingMatrixEntry.setOutgoingPortID(outgoingPort.getPortID());
            } else {
                switchingMatrixEntry.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (incomingPort != null) {
                incomingLink = incomingPort.getLink().getLinkType();
            }
            if (outgoingPort != null) {
                outgoingLink = outgoingPort.getLink().getLinkType();
            }
            if ((incomingLink == TLink.EXTERNAL_LINK) && (outgoingLink == TLink.EXTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((incomingLink == TLink.EXTERNAL_LINK) && (outgoingLink == TLink.INTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((incomingLink == TLink.INTERNAL_LINK) && (outgoingLink == TLink.EXTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.POP_LABEL);
            } else if ((incomingLink == TLink.INTERNAL_LINK) && (outgoingLink == TLink.INTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            }
            if (this.isExitLER(tailEndIPv4Address)) {
                switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                switchingMatrixEntry.setLocalTLDPSessionID(this.gIdentLDP.getNextIdentifier());
            } catch (Exception e) {
                // FIX: this is ugly. Avoid.
                e.printStackTrace();
            }
            this.switchingMatrix.addEntry(switchingMatrixEntry);
        }
        return switchingMatrixEntry;
    }

    /**
     * This method creates a new entry in the switching matrix using data from
     * an incoming IPv4 packet.
     *
     * @param ipv4Packet the incoming packet.
     * @param incomingPortID the port by wich the IPv4 packet has arrived.
     * @return A new switching matrix entry, already initialized and inserted in
     * the switching matrix of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public TSwitchingMatrixEntry createInitialEntryInFECMatrix(TIPv4PDU ipv4Packet, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        String localIPv4Address = this.getIPv4Address();
        String tailEndIPv4Address = ipv4Packet.getIPv4Header().getTailEndIPAddress();
        String outgoingPortID = this.topology.getFloydWarsallNextHopIPv4Address(localIPv4Address, tailEndIPv4Address);
        if (outgoingPortID != null) {
            TPort incomingPort = this.ports.getPort(incomingPortID);
            TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(outgoingPortID);
            int incomingLink = TLink.EXTERNAL_LINK;
            int outgoingLink = TLink.INTERNAL_LINK;
            switchingMatrixEntry = new TSwitchingMatrixEntry();
            switchingMatrixEntry.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setTailEndIPAddress(tailEndIPv4Address);
            switchingMatrixEntry.setIncomingPortID(incomingPortID);
            switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setLabelOrFEC(this.classifyPacket(ipv4Packet));
            switchingMatrixEntry.setEntryAsForBackupLSP(false);
            if (outgoingPort != null) {
                switchingMatrixEntry.setOutgoingPortID(outgoingPort.getPortID());
                outgoingLink = outgoingPort.getLink().getLinkType();
            } else {
                switchingMatrixEntry.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (incomingPort != null) {
                incomingLink = incomingPort.getLink().getLinkType();
            }
            if ((incomingLink == TLink.EXTERNAL_LINK) && (outgoingLink == TLink.EXTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((incomingLink == TLink.EXTERNAL_LINK) && (outgoingLink == TLink.INTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((incomingLink == TLink.INTERNAL_LINK) && (outgoingLink == TLink.EXTERNAL_LINK)) {
                // Not possible
            } else if ((incomingLink == TLink.INTERNAL_LINK) && (outgoingLink == TLink.INTERNAL_LINK)) {
                // Not possible
            }
            if (this.isExitLER(tailEndIPv4Address)) {
                switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                switchingMatrixEntry.setLocalTLDPSessionID(gIdentLDP.getNextIdentifier());
            } catch (Exception e) {
                // FIX: this is ugly. Avoid.
                e.printStackTrace();
            }
            this.switchingMatrix.addEntry(switchingMatrixEntry);
        }
        return switchingMatrixEntry;
    }

    /**
     * This method creates a new entry in the switching matrix using data from
     * an incoming MPLS packet.
     *
     * @param mplsPacket the incoming packet.
     * @param incomingPortID the port by wich the MPLS packet has arrived.
     * @return A new switching matrix entry, already initialized and inserted in
     * the switching matrix of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public TSwitchingMatrixEntry createInitialEntryInILMMatrix(TMPLSPDU mplsPacket, int incomingPortID) {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        String localIPv4Address = this.getIPv4Address();
        String tailEndIPv4Address = mplsPacket.getIPv4Header().getTailEndIPAddress();
        String nextHopIPv4Address = this.topology.getFloydWarsallNextHopIPv4Address(localIPv4Address, tailEndIPv4Address);
        if (nextHopIPv4Address != null) {
            TPort incomingPort = this.ports.getPort(incomingPortID);
            TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
            int incomingLink = TLink.EXTERNAL_LINK;
            int outgoingLink = TLink.INTERNAL_LINK;
            switchingMatrixEntry = new TSwitchingMatrixEntry();
            switchingMatrixEntry.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setTailEndIPAddress(tailEndIPv4Address);
            switchingMatrixEntry.setIncomingPortID(incomingPortID);
            switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setEntryAsForBackupLSP(false);
            switchingMatrixEntry.setLabelOrFEC(mplsPacket.getLabelStack().getTop().getLabel());
            if (outgoingPort != null) {
                switchingMatrixEntry.setOutgoingPortID(outgoingPort.getPortID());
                outgoingLink = outgoingPort.getLink().getLinkType();
            } else {
                switchingMatrixEntry.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (incomingPort != null) {
                incomingLink = incomingPort.getLink().getLinkType();
            }
            if ((incomingLink == TLink.EXTERNAL_LINK) && (outgoingLink == TLink.EXTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((incomingLink == TLink.EXTERNAL_LINK) && (outgoingLink == TLink.INTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((incomingLink == TLink.INTERNAL_LINK) && (outgoingLink == TLink.EXTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.POP_LABEL);
            } else if ((incomingLink == TLink.INTERNAL_LINK) && (outgoingLink == TLink.INTERNAL_LINK)) {
                switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            }
            if (this.isExitLER(tailEndIPv4Address)) {
                switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                switchingMatrixEntry.setLocalTLDPSessionID(this.gIdentLDP.getNextIdentifier());
            } catch (Exception e) {
                // FIX: this is ugly. Avoid.
                e.printStackTrace();
            }
            this.switchingMatrix.addEntry(switchingMatrixEntry);
        }
        return switchingMatrixEntry;
    }

    /**
     * This method take an incoming IPv4 packet and the corresponding entry in
     * the switching matrix and creates a new MPLS packet containing the IPv4
     * one and labeled correctly to be sent to the interior of the MPLS domain.
     *
     * @param ipv4Packet the IPv4 packet to be labeled as an MPLS packet.
     * @param switchingMatrixEntry entry of the switching matrix corresponding
     * to the incoming IPv4 packet.
     * @return an MPLS packet that is the incoming IPv4 packet labeled as an
     * MPLS packet.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public TMPLSPDU createMPLSPacket(TIPv4PDU ipv4Packet, TSwitchingMatrixEntry switchingMatrixEntry) {
        TMPLSPDU mplsPacket = null;
        try {
            mplsPacket = new TMPLSPDU(this.gIdent.getNextIdentifier(), ipv4Packet.getIPv4Header().getOriginIPv4Address(), ipv4Packet.getIPv4Header().getTailEndIPAddress(), ipv4Packet.getSize());
        } catch (EIDGeneratorOverflow e) {
            // FIX: this is ugly. Avoid.
            e.printStackTrace();
        }
        // FIX: At this point, mplsPacket could be null and the next line would
        // throw an exception. To be corrected.
        mplsPacket.setHeader(ipv4Packet.getIPv4Header());
        mplsPacket.setTCPPayload(ipv4Packet.getTCPPayload());
        mplsPacket.setSubtype(TAbstractPDU.MPLS);
        TMPLSLabel mplsLabel = new TMPLSLabel();
        // FIX: all harcoded values should be changed by class constants.
        mplsLabel.setBoS(true);
        mplsLabel.setEXP(0);
        mplsLabel.setLabel(switchingMatrixEntry.getOutgoingLabel());
        mplsLabel.setTTL(ipv4Packet.getIPv4Header().getTTL() - 1);
        mplsPacket.getLabelStack().pushTop(mplsLabel);
        ipv4Packet = null;
        try {
            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), mplsPacket.getSubtype(), mplsPacket.getSize()));
        } catch (Exception e) {
            // FIX: this is ugly. Avoid.
            e.printStackTrace();
        }
        return mplsPacket;
    }

    /**
     * This method gets an MPLS packet and its associated entry in the switching
     * matrix and extracts the original IPv4 from it.
     *
     * @param MPLSPacket the MPLS packet whose IPv4 content is going to be
     * extracted.
     * @param switchingMatrixEntry entry of the switching matrix corresponding
     * to the incoming MPLS packet.
     * @return the IPv4 packet that is contained in the MPLS specified as an
     * argumenty.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public TIPv4PDU createIPv4Packet(TMPLSPDU MPLSPacket, TSwitchingMatrixEntry switchingMatrixEntry) {
        TIPv4PDU ipv4Packet = null;
        try {
            ipv4Packet = new TIPv4PDU(this.gIdent.getNextIdentifier(), MPLSPacket.getIPv4Header().getOriginIPv4Address(), MPLSPacket.getIPv4Header().getTailEndIPAddress(), MPLSPacket.getTCPPayload().getSize());
        } catch (EIDGeneratorOverflow e) {
            // FIX: this is ugly. Avoid.
            e.printStackTrace();
        }
        // FIX: At this point, ipv4Packet could be null and the next line would
        // throw an exception. To be corrected.
        ipv4Packet.setHeader(MPLSPacket.getIPv4Header());
        ipv4Packet.setTCPPayload(MPLSPacket.getTCPPayload());
        ipv4Packet.getIPv4Header().setTTL(MPLSPacket.getLabelStack().getTop().getTTL());
        if (ipv4Packet.getIPv4Header().getOptionsField().isUsed()) {
            ipv4Packet.setSubtype(TAbstractPDU.IPV4_GOS);
        } else {
            ipv4Packet.setSubtype(TAbstractPDU.IPV4);
        }
        try {
            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), ipv4Packet.getSubtype(), ipv4Packet.getSize()));
        } catch (Exception e) {
            // FIX: this is ugly. Avoid.
            e.printStackTrace();
        }
        MPLSPacket = null;
        return ipv4Packet;
    }

    /**
     * This method checks whether a given packet comes from inside the MPLS
     * domain or from outside the MPLS domain.
     *
     * @param packet incoming packet.
     * @param incomingPortID por from wich the packet has arrived to this node.
     * @return true, if the packet comes from outside the MPLS domain.
     * Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public boolean isAnExternalPacket(TAbstractPDU packet, int incomingPortID) {
        if (packet.getType() == TAbstractPDU.IPV4) {
            return true;
        }
        TPort incomingPort = this.ports.getPort(incomingPortID);
        return incomingPort.getLink().getLinkType() == TLink.EXTERNAL_LINK;
    }

    /**
     * This method discards a packet and update the corresponding stats.
     *
     * @param packet packet to be discarded.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public void discardPacket(TAbstractPDU packet) {
        try {
            this.generateSimulationEvent(new TSimulationEventPacketDiscarded(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
            this.stats.addStatEntry(packet, TStats.BEING_DISCARDED);
        } catch (Exception e) {
            // FIX: this is ugly. Avoid.
            e.printStackTrace();
        }
        packet = null;
    }

    /**
     * This method gets an incoming packet as a parameter and classifies it.
     * This means that the node determines the FEC_ENTRY to wich the packet has
     * to be associated. This values is computed as a hash of the concatenation
     * of the origin and the target IP address. In practice, this means that
     * packets having the same origin and target IP addresses have the same
     * FEC_ENTRY.
     *
     * @param incomingPacket the incoming packet to be classified.
     * @return The computed FEC_ENTRY to wich de incoming packet has to be
     * associated.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public int classifyPacket(TAbstractPDU incomingPacket) {
        String originIPv4Address = incomingPacket.getIPv4Header().getOriginIPv4Address();
        String tailEndIPv4Address = incomingPacket.getIPv4Header().getTailEndIPAddress();
        String FECString = originIPv4Address + tailEndIPv4Address;
        // FIX: According to Java documentation, hashCode() does not have a 
        // constistent behaviour between different executions; should be changed
        // and use a persistent mechanism.
        return FECString.hashCode();
    }

    /**
     * This gets the ports set of this node.
     *
     * @return the ports set of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public TPortSet getPorts() {
        return this.ports;
    }

    /**
     * This method checks whether the node has available ports or not.
     *
     * @return true, if the node has available ports. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }

    /**
     * This method computes the routing weight of this node. This has to do with
     * the "Guarente of Service Support (GoS) over MPLS using Active Techniques"
     * proposal. It allows the RABAN routing algorithm to balance the traffic
     * over the network depending on some factors like the congestion level or
     * the number of flows that are crossing the node at the moment.
     *
     * @return The routing weight of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public long getRoutingWeight() {
        // FIX: All harcoded values should be defined as class constants. They 
        // are weights of the GoS proposal, but anyway, they should be 
        // configured as class constans.
        long congestionWeightComponent = (long) (this.ports.getCongestionLevel() * (0.7));
        long switchingMatrixWeightComponent = (long) ((10 * this.switchingMatrix.getNumberOfEntries()) * (0.3));
        long routingWeight = congestionWeightComponent + switchingMatrixWeightComponent;
        return routingWeight;
    }

    /**
     * This method checks whether this node is an exit point from the MPLS
     * domain to reach a given target IP address.
     *
     * @param targetIPAddress the IP address that has to be reached.
     * @return true, if this node is an exit point from the MPLS domain that
     * allow reaching the specified target IP address. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public boolean isExitLER(String targetIPAddress) {
        TPort portAux = this.ports.getLocalPortConnectedToANodeWithIPAddress(targetIPAddress);
        if (portAux != null) {
            if (portAux.getLink().getLinkType() == TLink.EXTERNAL_LINK) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method gets the switching matrix of the node.
     *
     * @return the switching matrix of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public TSwitchingMatrix getSwitchingMatrix() {
        return this.switchingMatrix;
    }

    /**
     * This method returns the configuration status of this node.
     *
     * @return true, if the node is configured correctly. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }

    /**
     * This method checks whether this node is configured correctly or not.
     *
     * @param topology Topology to wich this node belongs to.
     * @param reconfiguration true, if the node is being re-configured.
     * Otherwise, false.
     * @return TLERNode.OK if the configuration is correct. Otherwise, an error
     * code is returned. See public constants of error codes in this class.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TLERNode.UNNAMED;
        }
        boolean onlyBlankSpaces = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                onlyBlankSpaces = false;
            }
        }
        if (onlyBlankSpaces) {
            return TLERNode.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TNode nodeAux = topology.getFirstNodeNamed(this.getName());
            if (nodeAux != null) {
                return TLERNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode nodeAux2 = topology.getFirstNodeNamed(this.getName());
            if (nodeAux2 != null) {
                if (this.topology.isThereMoreThanANodeNamed(this.getName())) {
                    return TLERNode.NAME_ALREADY_EXISTS;
                }
            }
        }
        this.setWellConfigured(true);
        return TLERNode.OK;
    }

    /**
     * This method generates an human-readable error message from a given error
     * code specified as an argument.
     *
     * @param errorCode the error code to witch the text message has to be
     * generated. One of the public constants defined in this class.
     * @return an String explaining the error.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case UNNAMED:
                return (this.translations.getString("TConfigLER.FALTA_NOMBRE"));
            case NAME_ALREADY_EXISTS:
                return (this.translations.getString("TConfigLER.NOMBRE_REPETIDO"));
            case ONLY_BLANK_SPACES:
                return (this.translations.getString("TNodoLER.NombreNoSoloEspacios"));
        }
        return ("");
    }

    /**
     * This method serializes the configuration parameters of this node into an
     * string that can be saved into disk.
     *
     * @return an String containing all the configuration parameters of this
     * node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public String toOSMString() {
        String serializedElement = "#LER#";
        serializedElement += this.getNodeID();
        serializedElement += "#";
        serializedElement += this.getName().replace('#', ' ');
        serializedElement += "#";
        serializedElement += this.getIPv4Address();
        serializedElement += "#";
        serializedElement += this.isSelected();
        serializedElement += "#";
        serializedElement += this.getShowName();
        serializedElement += "#";
        serializedElement += this.isGeneratingStats();
        serializedElement += "#";
        serializedElement += this.getScreenPosition().x;
        serializedElement += "#";
        serializedElement += this.getScreenPosition().y;
        serializedElement += "#";
        serializedElement += this.routingPowerInMbps;
        serializedElement += "#";
        serializedElement += this.getPorts().getBufferSizeInMBytes();
        serializedElement += "#";
        return serializedElement;
    }

    /**
     * This method gets as an argument a serialized string that contains the
     * needed parameters to configure an TLERNode and configure this node using
     * them.
     *
     * @param serializedLER A serialized representation of a TLERNode.
     * @return true, whether the serialized string is correct and this node has
     * been initialized correctly using the serialized values. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public boolean fromOSMString(String serializedLER) {
        // FIX: All fixed values in this method should be implemented as class
        // constants instead of harcoded values.
        String[] elementFields = serializedLER.split("#");
        if (elementFields.length != 12) {
            return false;
        }
        this.setNodeID(Integer.parseInt(elementFields[2]));
        this.setName(elementFields[3]);
        this.setIPv4Address(elementFields[4]);
        this.setSelected(Integer.parseInt(elementFields[5]));
        this.setShowName(Boolean.parseBoolean(elementFields[6]));
        this.setGenerateStats(Boolean.parseBoolean(elementFields[7]));
        int posX = Integer.parseInt(elementFields[8]);
        int posY = Integer.parseInt(elementFields[9]);
        this.setScreenPosition(new Point(posX + 24, posY + 24));
        this.routingPowerInMbps = Integer.parseInt(elementFields[10]);
        this.getPorts().setBufferSizeInMB(Integer.parseInt(elementFields[11]));
        return true;
    }

    /**
     * This node gets the stat of this node.
     *
     * @return The stats of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public TStats getStats() {
        return this.stats;
    }

    /**
     * This method sets the number of ports of this node.
     *
     * @param numPorts Number of ports of this node. The maximum allowed is 8.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public synchronized void setPorts(int numPorts) {
        this.ports = new TFIFOPortSet(numPorts, this);
    }

    /**
     * In an active node, this method start the GPSRP protocol to request the
     * retransmission of a lost packet part of wich has been recovered before
     * discarding it. TLERNode is not an active node so, this method does
     * nothing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsPacket incomplete MPLS packet that want to be recovered
     * locally.
     * @param outgoingPortID Port of this node through wich the GPSRP
     * retransmission request is going to be sent.
     * @since 2.0
     */
    @Override
    public void runGPSRP(TMPLSPDU mplsPacket, int outgoingPortID) {
        // FIX: This does nothing in a non-active node.
    }

    // FIX: This values are used to check that the active LER node is correctly
    // configured through the UI. It should not be here but in another place.
    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int NAME_ALREADY_EXISTS = 2;
    public static final int ONLY_BLANK_SPACES = 3;

    private TSwitchingMatrix switchingMatrix;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int routingPowerInMbps;
    private TLERStats stats;
    private ResourceBundle translations;
}
