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
import com.manolodominguez.opensimmpls.utils.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;
import java.util.Iterator;

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
        this.setPorts(TNode.NUM_LER_PORTS);
        this.switchingMatrix = new TSwitchingMatrix();
        this.gIdent = new TLongIDGenerator();
        this.gIdentLDP = new TIDGenerator();
        //FIX: replace with class constants.
        this.routingPowerInMbps = 512;
        this.stats = new TLERStats();
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
        double maxNumberOfBits = (double) ((double) this.availableNs / (double) nsPerBit);
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
        this.stats.activateStats(this.isGeneratingStats());
        this.handleGPSRPPacket();
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
        this.setStepDuration(timerEvent.getStepDuration());
        this.setTimeInstant(timerEvent.getUpperLimit());
        if (this.getPorts().isThereAnyPacketToRoute()) {
            this.availableNs += timerEvent.getStepDuration();
        } else {
            this.handleGPSRPPacket();
            this.availableNs = timerEvent.getStepDuration();
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
            this.generateSimulationEvent(new TSENodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), this.getPorts().getCongestionLevel()));
        } catch (Exception e) {
            // FIX: this is not a good practice. Avoid.
            e.printStackTrace();
        }
        this.checkConnectivityStatus();
        this.decreaseCounters();
        this.routePackets();
        this.stats.consolidateData(this.getAvailableTime());
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
                                if (linkAux1.getLinkType() == TLink.INTERNAL) {
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
                                if (linkAux1.getLinkType() == TLink.INTERNAL) {
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
                    if (linkAux1.isBroken() && (linkAux2.getLinkType() == TLink.EXTERNAL)) {
                        removeSwitchingMatrixEntry = true;
                    }
                    if ((linkAux1.getLinkType() == TLink.EXTERNAL) && linkAux2.isBroken()) {
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
     * This method read read the port to which it is up following a Round Robin
     * algorithm. It does that until it consumes all the available nanoseconds
     * for the current tick/step. If it is able to switch or route a given
     * incoming packet, it does it. If it is a martian packet, the packet is
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
                    this.availableNs += this.getNsRequiredForAllOctets(packet.getSize());
                    this.discardPacket(packet);
                }
                this.availableNs -= this.getNsRequiredForAllOctets(packet.getSize());
                routeableOctectsWithCurrentNs = this.getMaxRouteableOctectsWithCurrentNs();
            }
        }
        if (atLeastOnePacketRouted) {
            this.handleGPSRPPacket();
        } else {
            this.increaseStepsWithoutEmitting();
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
                String nextHopIPv4Address = this.topology.obtenerIPSalto(this.getIPv4Address(), targetIPv4Address);
                outgoingPort = (TFIFOPort) this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
                if (outgoingPort != null) {
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
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
     * needed, it re-enqueues the packet and requests a label to be able to
     * relay the packet. Once an entry for this packet is in the routing table,
     * it sends the packet into the MPLS domain or outward, as appropriate.
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
                    outgoingPort.putPacketOnLink(mplsPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.POP_LABEL) {
                    this.discardPacket(packet);
                } else if (operation == TSwitchingMatrixEntry.SWAP_LABEL) {
                    this.discardPacket(packet);
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
     * needed, it re-enqueues the packet and requests a label to be able to
     * relay the packet. Once an entry for this packet is in the routing table,
     * it sends the packet into the MPLS domain or outward, as appropriate.
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
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
                        outgoingPort.putPacketOnLink(ipv4Packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    } else {
                        packet.getLabelStack().popTop();
                        if (isLabeled) {
                            packet.getLabelStack().pushTop(mplsLabel);
                        }
                        TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                        outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    }
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
                    if (link.getLinkType() == TLink.INTERNAL) {
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
                        tldpPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, targetIPv4Address);
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
                        outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
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
                        tldpPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, targetIPv4Address);
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
                        outgoingPort.putPacketOnLink(tldpPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, tldpPacket.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
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
     * Este m�todo env�a una confirmaci�n de eliminaci�n de etiqueta al nodo que
     * especifique la correspondiente entrada en la matriz de conmutaci�n.
     *
     * @since 2.0
     * @param puerto Puierto por el que se debe enviar la confirmaci�n de
     * eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void sendTLDPWithdrawalOk(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            String IPLocal = this.getIPv4Address();
            String IPDestino = this.ports.getIPv4OfNodeLinkedTo(puerto);
            if (IPDestino != null) {
                TTLDPPDU nuevoTLDP = null;
                try {
                    nuevoTLDP = new TTLDPPDU(this.gIdent.getNextID(), IPLocal, IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nuevoTLDP != null) {
                    nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REVOMAL_REQUEST_OK);
                    nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPv4Address());
                    nuevoTLDP.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                    if (emc.getOutgoingPortID() == puerto) {
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getIncomingPortID() == puerto) {
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort pSalida = this.ports.getPort(puerto);
                    pSalida.putPacketOnLink(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, nuevoTLDP.getSize()));
                        this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Este m�todo solicita una etiqueta al nodo que se especifica en la entrada
     * de la matriz de conmutaci�n correspondiente.
     *
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void requestTLDP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = emc.getTailEndIPv4Address();
        if (emc.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
            String IPSalto = this.topology.obtenerIPSalto(IPLocal, IPDestinoFinal);
            if (IPSalto != null) {
                TTLDPPDU paqueteTLDP = null;
                try {
                    paqueteTLDP = new TTLDPPDU(this.gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                    paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                    paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                    if (emc.aBackupLSPHasBeenRequested()) {
                        paqueteTLDP.setLSPType(true);
                    } else {
                        paqueteTLDP.setLSPType(false);
                    }
                    paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort pSalida = this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Este m�todo env�a una eliminaci�n de etiqueta al nodo especificado por le
     * entrada de la matriz de conmutaci�n correspondiente.
     *
     * @since 2.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     */
    public void sendTLDPWithdrawal(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
            String IPLocal = this.getIPv4Address();
            String IPDestinoFinal = emc.getTailEndIPv4Address();
            String IPSalto = this.ports.getIPv4OfNodeLinkedTo(puerto);
            if (IPSalto != null) {
                TTLDPPDU paqueteTLDP = null;
                try {
                    paqueteTLDP = new TTLDPPDU(this.gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                    paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REMOVAL_REQUEST);
                    if (emc.getOutgoingPortID() == puerto) {
                        paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getIncomingPortID() == puerto) {
                        paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort pSalida = this.ports.getPort(puerto);
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Este m�todo reenv�a todas las peticiones pendientes de contestaci�n de
     * una entrada de la matriz de conmutaci�n.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void solicitarTLDPTrasTimeout(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            String IPLocal = this.getIPv4Address();
            String IPDestinoFinal = emc.getTailEndIPv4Address();
            String IPSalto = this.ports.getIPv4OfNodeLinkedTo(emc.getOutgoingPortID());
            if (IPSalto != null) {
                TTLDPPDU paqueteTLDP = null;
                try {
                    paqueteTLDP = new TTLDPPDU(this.gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                    paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                    paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                    if (emc.aBackupLSPHasBeenRequested()) {
                        paqueteTLDP.setLSPType(true);
                    } else {
                        paqueteTLDP.setLSPType(false);
                    }
                    paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort pSalida = this.ports.getPort(emc.getOutgoingPortID());
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de
     * una entrada de la matriz de conmutaci�n.
     *
     * @since 2.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry emc, int puerto) {
        this.sendTLDPWithdrawal(emc, puerto);
    }

    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de
     * una entrada de la matriz de conmutaci�n a todos los ports necesarios.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry emc) {
        this.sendTLDPWithdrawal(emc, emc.getIncomingPortID());
        this.sendTLDPWithdrawal(emc, emc.getOutgoingPortID());
    }

    /**
     * Este m�todo decrementa los contadores de retransmisi�n existentes para
     * este nodo.
     *
     * @since 2.0
     */
    public void decreaseCounters() {
        TSwitchingMatrixEntry emc = null;
        this.switchingMatrix.getMonitor().lock();
        Iterator it = this.switchingMatrix.getEntriesIterator();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                emc.decreaseTimeOut(this.getTickDuration());
                if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    if (emc.shouldRetryExpiredTLDPRequest()) {
                        emc.resetTimeOut();
                        emc.decreaseAttempts();
                        solicitarTLDPTrasTimeout(emc);
                    }
                } else if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    if (emc.shouldRetryExpiredTLDPRequest()) {
                        emc.resetTimeOut();
                        emc.decreaseAttempts();
                        labelWithdrawalAfterTimeout(emc);
                    } else if (!emc.areThereAvailableAttempts()) {
                        it.remove();
                    }
                } else {
                    emc.resetTimeOut();
                    emc.resetAttempts();
                }
            }
        }
        this.switchingMatrix.getMonitor().unLock();
    }

    /**
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n con los
     * datos de un packet TLDP entrante.
     *
     * @param paqueteSolicitud Paquete TLDP entrante, de solicitud de etiqueta.
     * @param pEntrada Puerto de entrada del packet TLDP.
     * @return La entrada de la matriz de conmutaci�n, ya creada, insertada e
     * inicializada.
     * @since 2.0
     */
    public TSwitchingMatrixEntry createEntryFromTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.getTLDPPayload().getTLDPIdentifier();
        TPort puertoEntrada = this.ports.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.getTLDPPayload().getTailEndIPAddress();
        String IPSalto = this.topology.obtenerIPSalto(this.getIPv4Address(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(IdTLDPAntecesor);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setLabelOrFEC(TSwitchingMatrixEntry.UNDEFINED);
            emc.setEntryIsForBackupLSP(paqueteSolicitud.getLSPType());
            if (puertoSalida != null) {
                emc.setOutgoingPortID(puertoSalida.getPortID());
            } else {
                emc.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().getLinkType();
            }
            if (puertoSalida != null) {
                enlaceDestino = puertoSalida.getLink().getLinkType();
            }
            if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.POP_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            }
            if (isExitLER(IPDestinoFinal)) {
                emc.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(this.gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.switchingMatrix.addEntry(emc);
        }
        return emc;
    }

    /**
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n bas�ndose
     * en un packet IPv4 recibido.
     *
     * @param paqueteIPv4 Paquete IPv4 recibido.
     * @param pEntrada Puerto por el que ha llegado el packet IPv4.
     * @return La entrada de la matriz de conmutaci�n, creada, insertada e
     * inicializada.
     * @since 2.0
     */
    public TSwitchingMatrixEntry createInitialEntryInFECMatrix(TIPv4PDU paqueteIPv4, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = paqueteIPv4.getIPv4Header().getTailEndIPAddress();
        String IPSalida = this.topology.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = this.ports.getPort(pEntrada);
            TPort puertoSalida = this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalida);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setLabelOrFEC(this.classifyPacket(paqueteIPv4));
            emc.setEntryIsForBackupLSP(false);
            if (puertoSalida != null) {
                emc.setOutgoingPortID(puertoSalida.getPortID());
                enlaceDestino = puertoSalida.getLink().getLinkType();
            } else {
                emc.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().getLinkType();
            }
            if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                // No es posible
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                // No es posible
            }
            if (this.isExitLER(IPDestinoFinal)) {
                emc.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.switchingMatrix.addEntry(emc);
        }
        return emc;
    }

    /**
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n bas�ndose
     * en un packet MPLS recibido.
     *
     * @param paqueteMPLS Paquete MPLS recibido.
     * @param pEntrada Puerto por el que ha llegado el packet MPLS.
     * @return La entrada de la matriz de conmutaci�n, creada, insertada e
     * inicializada.
     * @since 2.0
     */
    public TSwitchingMatrixEntry createInitialEntryInILMMatrix(TMPLSPDU paqueteMPLS, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = paqueteMPLS.getIPv4Header().getTailEndIPAddress();
        String IPSalida = this.topology.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = this.ports.getPort(pEntrada);
            TPort puertoSalida = this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalida);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setEntryIsForBackupLSP(false);
            emc.setLabelOrFEC(paqueteMPLS.getLabelStack().getTop().getLabel());
            if (puertoSalida != null) {
                emc.setOutgoingPortID(puertoSalida.getPortID());
                enlaceDestino = puertoSalida.getLink().getLinkType();
            } else {
                emc.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().getLinkType();
            }
            if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.POP_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            }
            if (isExitLER(IPDestinoFinal)) {
                emc.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(this.gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.switchingMatrix.addEntry(emc);
        }
        return emc;
    }

    /**
     * Este m�todo toma un packet IPv4 y la entrada de la matriz de conmutaci�n
     * asociada al mismo y crea un packet MPLS etiquetado correctamente que
     * contiene dicho packet IPv4 listo para ser transmitido hacia el interior
     * del dominio.
     *
     * @param paqueteIPv4 Paquete IPv4 que se debe etiquetar.
     * @param emc Entrada de la matriz de conmutaci�n asociada al packet IPv4
     * que se desea etiquetar.
     * @return El packet IPv4 de entrada, convertido en un packet MPLS
     * correctamente etiquetado.
     * @since 2.0
     */
    public TMPLSPDU createMPLSPacket(TIPv4PDU paqueteIPv4, TSwitchingMatrixEntry emc) {
        TMPLSPDU paqueteMPLS = null;
        try {
            paqueteMPLS = new TMPLSPDU(this.gIdent.getNextID(), paqueteIPv4.getIPv4Header().getOriginIPv4Address(), paqueteIPv4.getIPv4Header().getTailEndIPAddress(), paqueteIPv4.getSize());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        paqueteMPLS.setHeader(paqueteIPv4.getIPv4Header());
        paqueteMPLS.setTCPPayload(paqueteIPv4.getTCPPayload());
        paqueteMPLS.setSubtype(TAbstractPDU.MPLS);
        TMPLSLabel empls = new TMPLSLabel();
        empls.setBoS(true);
        empls.setEXP(0);
        empls.setLabel(emc.getOutgoingLabel());
        empls.setTTL(paqueteIPv4.getIPv4Header().getTTL() - 1);
        paqueteMPLS.getLabelStack().pushTop(empls);
        paqueteIPv4 = null;
        try {
            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteMPLS.getSubtype(), paqueteMPLS.getSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paqueteMPLS;
    }

    /**
     * Este m�todo toma como par�metro un packet MPLS y su entrada en la matriz
     * de conmutaci�n asociada. Extrae del packet MPLS el packet IP
     * correspondiente y actualiza sus valores correctamente.
     *
     * @param paqueteMPLS Paquete MPLS cuyo contenido de nivel IPv4 se desea
     * extraer.
     * @param emc Entrada de la matriz de conmutaci�n asociada al packet MPLS.
     * @return Paquete IPv4 que corresponde al packet MPLS una vez que se ha
     * eliminado toda la informaci�n MLPS; que se ha desetiquetado.
     * @since 2.0
     */
    public TIPv4PDU createIPv4Packet(TMPLSPDU paqueteMPLS, TSwitchingMatrixEntry emc) {
        TIPv4PDU paqueteIPv4 = null;
        try {
            paqueteIPv4 = new TIPv4PDU(this.gIdent.getNextID(), paqueteMPLS.getIPv4Header().getOriginIPv4Address(), paqueteMPLS.getIPv4Header().getTailEndIPAddress(), paqueteMPLS.getTCPPayload().getSize());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        paqueteIPv4.setHeader(paqueteMPLS.getIPv4Header());
        paqueteIPv4.setTCPPayload(paqueteMPLS.getTCPPayload());
        paqueteIPv4.getIPv4Header().setTTL(paqueteMPLS.getLabelStack().getTop().getTTL());
        if (paqueteIPv4.getIPv4Header().getOptionsField().isUsed()) {
            paqueteIPv4.setSubtype(TAbstractPDU.IPV4_GOS);
        } else {
            paqueteIPv4.setSubtype(TAbstractPDU.IPV4);
        }
        try {
            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteIPv4.getSubtype(), paqueteIPv4.getSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        paqueteMPLS = null;
        return paqueteIPv4;
    }

    /**
     * Este m�todo comprueba si un packet recibido es un packet del interior del
     * dominio MPLS o es un packet externo al mismo.
     *
     * @param paquete Paquete que ha llegado al nodo.
     * @param pEntrada Puerto por el que ha llegado el packet al nodo.
     * @return true, si el packet es exterior al dominio MPLS. false en caso
     * contrario.
     * @since 2.0
     */
    public boolean esUnPaqueteExterno(TAbstractPDU paquete, int pEntrada) {
        if (paquete.getType() == TAbstractPDU.IPV4) {
            return true;
        }
        TPort pe = this.ports.getPort(pEntrada);
        if (pe.getLink().getLinkType() == TLink.EXTERNAL) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo contabiliza un packet recibido o conmutado en las
     * estad�sticas del nodo.
     *
     * @param paquete packet que se desa contabilizar.
     * @param deEntrada TRUE, si el packet se ha recibido en el nodo. FALSE so
     * el packet ha salido del nodo.
     * @since 2.0
     */
    public void contabilizarPaquete(TAbstractPDU paquete, boolean deEntrada) {
    }

    /**
     * Este m�todo descarta un packet en el nodo y refleja dicho descarte en las
     * estad�sticas del nodo.
     *
     * @param paquete Paquete que se quiere descartar.
     * @since 2.0
     */
    @Override
    public void discardPacket(TAbstractPDU paquete) {
        try {
            this.generateSimulationEvent(new TSEPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
            this.stats.addStatEntry(paquete, TStats.DISCARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paquete = null;
    }

    /**
     * Este m�todo toma como parametro un packet, supuestamente sin etiquetar, y
     * lo clasifica. Esto significa que determina el FEC_ENTRY al que pertenece
     * el packet. Este valor se calcula como el c�digo HASH practicado a la
     * concatenaci�n de la IP de origen y la IP de destino. En la pr�ctica esto
     * significa que paquetes con el mismo origen y con el mismo destino
     * pertenecer�n al mismo FEC_ENTRY.
     *
     * @param paquete El packet que se desea clasificar.
     * @return El FEC_ENTRY al que pertenece el packet pasado por par�metros.
     * @since 2.0
     */
    public int classifyPacket(TAbstractPDU paquete) {
        String IPOrigen = paquete.getIPv4Header().getOriginIPv4Address();
        String IPDestino = paquete.getIPv4Header().getTailEndIPAddress();
        String cadenaFEC = cadenaFEC = IPOrigen + IPDestino;
        return cadenaFEC.hashCode();
    }

    /**
     * Este m�todo permite el acceso al conjunto de ports del nodo.
     *
     * @return El conjunto de ports del nodo.
     * @since 2.0
     */
    @Override
    public TPortSet getPorts() {
        return this.ports;
    }

    /**
     * Este m�todo calcula si el nodo tiene ports libres o no.
     *
     * @return true, si el nodo tiene ports libres. false en caso contrario.
     * @since 2.0
     */
    @Override
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }

    /**
     * Este m�todo calcula el peso del nodo. Se utilizar� para calcular rutas
     * con costo menor. En el nodo LER el pero ser� siempre nulo (cero).
     *
     * @return 0. El peso siempre ser� nulo en un LER.
     * @since 2.0
     */
    @Override
    public long getRoutingWeight() {
        long peso = 0;
        long pesoC = (long) (this.ports.getCongestionLevel() * (0.7));
        long pesoMC = (long) ((10 * this.switchingMatrix.getNumberOfEntries()) * (0.3));
        peso = pesoC + pesoMC;
        return peso;
    }

    /**
     * Este m�todo comprueba si la isntancia actual es el LER de salida del
     * dominio MPLS para una IP dada.
     *
     * @param ip IP de destino del tr�fico, para la cual queremos averiguar si
     * el LER es nodo de salida.
     * @return true, si el LER es de salida del dominio para tr�fico dirigido a
     * esa IP. false en caso contrario.
     * @since 2.0
     */
    public boolean isExitLER(String ip) {
        TPort p = this.ports.getLocalPortConnectedToANodeWithIPAddress(ip);
        if (p != null) {
            if (p.getLink().getLinkType() == TLink.EXTERNAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo permite el acceso a la matriz de conmutaci�n de LER.
     *
     * @return La matriz de conmutaci�n del LER.
     * @since 2.0
     */
    public TSwitchingMatrix obtenerMatrizConmutacion() {
        return this.switchingMatrix;
    }

    /**
     * Este m�todo comprueba que la configuraci�n de LER sea la correcta.
     *
     * @return true, si el LER est� bien configurado. false en caso contrario.
     * @since 2.0
     */
    @Override
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }

    /**
     * Este m�todo comprueba que una cierta configuraci�n es v�lida.
     *
     * @param t Topolog�a a la que pertenece el LER.
     * @param recfg true si se trata de una reconfiguraci�n. false en caso
     * contrario.
     * @return CORRECTA, si la configuraci�n es correcta. Un c�digo de error en
     * caso contrario.
     * @since 2.0
     */
    @Override
    public int validateConfig(TTopology t, boolean recfg) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TLERNode.SIN_NOMBRE;
        }
        boolean soloEspacios = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                soloEspacios = false;
            }
        }
        if (soloEspacios) {
            return TLERNode.SOLO_ESPACIOS;
        }
        if (!recfg) {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null) {
                return TLERNode.NOMBRE_YA_EXISTE;
            }
        } else {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return TLERNode.NOMBRE_YA_EXISTE;
                }
            }
        }
        this.setWellConfigured(true);
        return TLERNode.CORRECTA;
    }

    /**
     * Este m�todo toma un codigo de error y genera un messageType textual del
     * mismo.
     *
     * @param e El c�digo de error para el cual queremos una explicaci�n
     * textual.
     * @return Cadena de texto explicando el error.
     * @since 2.0
     */
    @Override
    public String getErrorMessage(int e) {
        switch (e) {
            case SIN_NOMBRE:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigLER.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigLER.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TNodoLER.NombreNoSoloEspacios"));
        }
        return ("");
    }

    /**
     * Este m�todo forma una cadena de texto que representa al LER y toda su
     * configuraci�n. Sirve para almacenar el LER en disco.
     *
     * @return Una cadena de texto que representa un a este LER.
     * @since 2.0
     */
    @Override
    public String marshall() {
        String cadena = "#LER#";
        cadena += this.getID();
        cadena += "#";
        cadena += this.getName().replace('#', ' ');
        cadena += "#";
        cadena += this.getIPv4Address();
        cadena += "#";
        cadena += this.getStatus();
        cadena += "#";
        cadena += this.getShowName();
        cadena += "#";
        cadena += this.isGeneratingStats();
        cadena += "#";
        cadena += this.obtenerPosicion().x;
        cadena += "#";
        cadena += this.obtenerPosicion().y;
        cadena += "#";
        cadena += this.routingPowerInMbps;
        cadena += "#";
        cadena += this.getPorts().getBufferSizeInMBytes();
        cadena += "#";
        return cadena;
    }

    /**
     * Este m�todo toma como par�metro una cadena de texto que debe pertencer a
     * un LER serializado y configura esta instancia con los valores de dicha
     * caddena.
     *
     * @param elemento LER serializado.
     * @return true, si no ha habido errores y la instancia actual est� bien
     * configurada. false en caso contrario.
     * @since 2.0
     */
    @Override
    public boolean unMarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 12) {
            return false;
        }
        this.setID(Integer.parseInt(valores[2]));
        this.setName(valores[3]);
        this.setIPAddress(valores[4]);
        this.setStatus(Integer.parseInt(valores[5]));
        this.setShowName(Boolean.parseBoolean(valores[6]));
        this.setGenerateStats(Boolean.parseBoolean(valores[7]));
        int posX = Integer.parseInt(valores[8]);
        int posY = Integer.parseInt(valores[9]);
        this.setPosition(new Point(posX + 24, posY + 24));
        this.routingPowerInMbps = Integer.parseInt(valores[10]);
        this.getPorts().setBufferSizeInMB(Integer.parseInt(valores[11]));
        return true;
    }

    /**
     * Este m�todo permite acceder directamente a las stats del nodo.
     *
     * @return Las estad�sticas del nodo.
     * @since 2.0
     */
    @Override
    public TStats getStats() {
        return this.stats;
    }

    /**
     * Este m�todo permite establecer el n�mero de ports que tendr� el nodo.
     *
     * @param num N�mero de ports deseado para el nodo. Como mucho, 8 ports.
     * @since 2.0
     */
    @Override
    public synchronized void setPorts(int num) {
        this.ports = new TFIFOPortSet(num, this);
    }

    /**
     * Este m�todo no hace nada en un LSR. En un nodo activoPermitir� solicitar
     * a un nodo activo la retransmisi�n de un packet.
     *
     * @param paquete Paquete cuya retransmisi�n se est� solicitando.
     * @param pSalida Puerto por el que se enviar� la solicitud.
     * @since 2.0
     */
    @Override
    public void runGPSRP(TMPLSPDU paquete, int pSalida) {
    }

    /**
     * Esta constante indica que la configuraci�n del nodo LER esta correcta,
     * que no contiene errores.
     *
     * @since 2.0
     */
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que el nombre del nodo LER no est� definido.
     *
     * @since 2.0
     */
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que el nombre especificado para el LER ya est�
     * siendo usado por otro nodo de la topology.
     *
     * @since 2.0
     */
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que el nombre que se ha definido para el LER
     * contiene s�lo constantes.
     *
     * @since 2.0
     */
    public static final int SOLO_ESPACIOS = 3;

    private TSwitchingMatrix switchingMatrix;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int routingPowerInMbps;
    private TLERStats stats;
}
