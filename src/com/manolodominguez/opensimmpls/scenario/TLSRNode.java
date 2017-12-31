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
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPort;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrix;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrixEntry;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPortSet;
import com.manolodominguez.opensimmpls.hardware.ports.TPort;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;
import java.util.Iterator;

/**
 * This class implements a Label Switch Router (LSR) that will operate inside a
 * MPLS domain.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLSRNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TLSRNode.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param identifier the identifier of this active LSR node that allow
     * referencing switchingMatrixIterator in the topology.
     * @param ipAddress The IPv4 address assigned to this active LSR.
     * @param longIDGenerator The idntifier generator that the active LSR will
     * use to identify unambiguosly each event the switchingMatrixIterator
     * generates.
     * @param topology A reference to the topology this active LSR belongs to.
     * @since 2.0
     */
    public TLSRNode(int identifier, String ipAddress, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(identifier, ipAddress, longIDGenerator, topology);
        // FIX: This is an overridable method call in constructor that should be 
        // avoided.
        this.setPorts(TNode.DEFAULT_NUM_PORTS_LSR);
        this.switchingMatrix = new TSwitchingMatrix();
        this.gIdent = new TLongIDGenerator();
        this.gIdentLDP = new TIDGenerator();
        //FIX: replace with class constants.
        this.switchingPowerInMbps = 512;
        this.stats = new TLSRStats();
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to switch a single bit. This is something that depends on the
     * switching power of this LSR.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the number of nanoseconds that are needed to switch a single bit.
     * @since 2.0
     */
    public double getNsPerBit() {
        // FIX: replace al harcoded values for class constants
        long bitsPerSecondRate = (long) (this.switchingPowerInMbps * 1048576L);
        double nsPerBit = (double) ((double) 1000000000.0 / (long) bitsPerSecondRate);
        return nsPerBit;
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to switch the specified number of octects. This is something that
     * depends on the switching power of this LSR.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param octects the number of octects that wants to be switched.
     * @return the number of nanoseconds that are needed to switch the specified
     * number of octects.
     * @since 2.0
     */
    public double getNsRequiredForAllOctets(int octects) {
        // FIX: replace al harcoded values for class constants
        double nsPerBit = getNsPerBit();
        long numberOfBits = (long) ((long) octects * (long) 8);
        return (double) ((double) nsPerBit * (long) numberOfBits);
    }

    /**
     * This method gets the number of bits that this LSR can switch with the
     * available number of nanoseconds it has. The more switching power the LSR
     * has, the more bits it can switch with the same number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of bits that can be switched.
     * @since 2.0
     */
    public int getMaxSwitchableBitsWithCurrentNs() {
        double nsPerBit = getNsPerBit();
        double maxNumberOfBits = (double) ((double) this.availableNs / (double) nsPerBit);
        return (int) maxNumberOfBits;
    }

    /**
     * This method gets the number of octects that this LSR can switch with the
     * available number of nanoseconds it has. The more switching power the LER
     * has, the more octects it can switch with the same number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of octects that can be switched.
     * @since 2.0
     */
    public int getMaxSwitchableOctectsWithCurrentNs() {
        // FIX: replace al harcoded values for class constants
        double maxNumberOfOctects = ((double) getMaxSwitchableBitsWithCurrentNs() / (double) 8.0);
        return (int) maxNumberOfOctects;
    }

    /**
     * This method gets the switching power of this LSR, in Mbps.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the switching power of the node in Mbps.
     * @since 2.0
     */
    public int getSwitchingPowerInMbps() {
        return this.switchingPowerInMbps;
    }

    /**
     * This method sets the switching power of this LSR, in Mbps.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param switchingPowerInMbps the switching power of this LSRA, in Mbps.
     * @since 2.0
     */
    public void setSwitchingPowerInMbps(int switchingPowerInMbps) {
        this.switchingPowerInMbps = switchingPowerInMbps;
    }

    /**
     * This method gets the size of this LSR's buffer, in MBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the size of this LSR's buffer, in MBytes.
     * @since 2.0
     */
    public int getBufferSizeInMBytes() {
        return this.getPorts().getBufferSizeInMBytes();
    }

    /**
     * This method sets the size of this LSR's buffer, in MBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param bufferSizeInMBytes the size of this LSR's buffer, in MBytes.
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
        this.resetTicksWithoutEmitting();
    }

    /**
     * This method gets the type of this node. In this case, it will return the
     * constant TNode.LSR.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the constant TNode.LSR.
     * @since 2.0
     */
    @Override
    public int getNodeType() {
        return TNode.LSR;
    }

    /**
     * This method receive a timer event from the simulation's global timer. It
     * does some initial tasks and then wake up the LSR to start doing its work.
     *
     * @param timerEvent a timer event that is used to synchronize all nodes and
     * that inclues a number of nanoseconds to be used by the LSR.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent timerEvent) {
        this.setTickDuration(timerEvent.getStepDuration());
        this.setCurrentInstant(timerEvent.getUpperLimit());
        if (this.getPorts().isAnyPacketToSwitch()) {
            this.availableNs += timerEvent.getStepDuration();
        } else {
            this.resetTicksWithoutEmitting();
            this.availableNs = timerEvent.getStepDuration();
        }
        this.startOperation();
    }

    /**
     * This method starts all tasks that has to be executed during a timer tick.
     * The number of nanoseconds of this tick is the time this LSR will work.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void run() {
        // Actions to be done during the timer tick.
        try {
            this.generateSimulationEvent(new TSimulationEventNodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), this.getPorts().getCongestionLevel()));
        } catch (Exception e) {
            // FIX: this is not a good practice. Avoid.
            e.printStackTrace();
        }
        this.checkConnectivityStatus();
        this.decreaseCounters();
        this.switchPackets();
        this.stats.groupStatsByTimeInstant(this.getCurrentInstant());
        // Acciones a llevar a cabo durante el tic.
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
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        // FIX: Avoid using harcoded values
        int portIDAux = 0;
        TPort outgoingPort = null;
        TPort incomingPort = null;
        TLink linkAux = null;
        this.switchingMatrix.getMonitor().lock();
        Iterator switchingMatrixIterator = this.switchingMatrix.getEntriesIterator();
        while (switchingMatrixIterator.hasNext()) {
            switchingMatrixEntry = (TSwitchingMatrixEntry) switchingMatrixIterator.next();
            if (switchingMatrixEntry != null) {
                portIDAux = switchingMatrixEntry.getOutgoingPortID();
                // FIX: Avoid using harcoded values
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    outgoingPort = this.ports.getPort(portIDAux);
                    if (outgoingPort != null) {
                        linkAux = outgoingPort.getLink();
                        if (linkAux != null) {
                            if ((linkAux.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
                            }
                        }
                    }
                }
                portIDAux = switchingMatrixEntry.getIncomingPortID();
                // FIX: Avoid using harcoded values
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    incomingPort = this.ports.getPort(portIDAux);
                    if (incomingPort != null) {
                        linkAux = incomingPort.getLink();
                        if (linkAux != null) {
                            if ((linkAux.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
                            }
                        }
                    }
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
    public void switchPackets() {
        boolean atLeastOnePacketSwitched = false;
        // FIX: use class constant instead of hardcoded values
        int readPort = 0;
        TAbstractPDU packet = null;
        int switchableOctectsWithCurrentNs = this.getMaxSwitchableOctectsWithCurrentNs();
        while (this.getPorts().canSwitchPacket(switchableOctectsWithCurrentNs)) {
            atLeastOnePacketSwitched = true;
            packet = this.ports.getNextPacket();
            readPort = this.ports.getReadPort();
            if (packet != null) {
                // FIX: Convert to a switch statement
                if (packet.getType() == TAbstractPDU.TLDP) {
                    handleTLDPPacket((TTLDPPDU) packet, readPort);
                } else if (packet.getType() == TAbstractPDU.MPLS) {
                    handleMPLSPacket((TMPLSPDU) packet, readPort);
                } else if (packet.getType() == TAbstractPDU.GPSRP) {
                    handleGPSRPPacket((TGPSRPPDU) packet, readPort);
                } else {
                    this.availableNs += getNsRequiredForAllOctets(packet.getSize());
                    discardPacket(packet);
                }
                this.availableNs -= getNsRequiredForAllOctets(packet.getSize());
                switchableOctectsWithCurrentNs = this.getMaxSwitchableOctectsWithCurrentNs();
            }
        }
        if (atLeastOnePacketSwitched) {
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
            // FIX: , messageType, flowID and packetID seems not to be used. If 
            // not necessary, remove from the code.
            int messageType = packet.getGPSRPPayload().getGPSRPMessageType();
            int flowID = packet.getGPSRPPayload().getFlowID();
            int packetID = packet.getGPSRPPayload().getPacketID();
            String targetIPv4Address = packet.getIPv4Header().getTailEndIPAddress();
            TFIFOPort outgoingPort = null;
            if (targetIPv4Address.equals(this.getIPv4Address())) {
                // A LSR is unable to handle GPSRP packets, so if one is 
                // received, it has to be discarded.
                this.discardPacket(packet);
            } else {
                String nextHopIPv4Address = this.topology.getNextHopIPv4Address(this.getIPv4Address(), targetIPv4Address);
                outgoingPort = (TFIFOPort) this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
                if (outgoingPort != null) {
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.GPSRP));
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
     * This method switch a MPLS packet according to the information contained
     * in the corresponding entry in the switching table.
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
        // FIX: targetIPv4Address seems to be unused. Check and remove if 
        // needed
        String targetIPv4Address = packet.getIPv4Header().getTailEndIPAddress();
        switchingMatrixEntry = this.switchingMatrix.getEntry(incomingPortID, labelValue, TSwitchingMatrixEntry.LABEL_ENTRY);
        if (switchingMatrixEntry == null) {
            if (isLabeled) {
                packet.getLabelStack().pushTop(mplsLabel);
            }
            discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                requestTLDP(switchingMatrixEntry);
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
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                discardPacket(packet);
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
                    }
                    TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.POP_LABEL) {
                    packet.getLabelStack().popTop();
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                    }
                    TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.SWAP_LABEL) {
                    packet.getLabelStack().getTop().setLabel(switchingMatrixEntry.getOutgoingLabel());
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                    }
                    TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        TInternalLink internalLinkAux = (TInternalLink) outgoingPort.getLink();
                        internalLinkAux.setAsUsedByALSP();
                        internalLinkAux.unlinkFromABackupLSP();
                        switchingMatrixEntry.setEntryAsForBackupLSP(false);
                    }
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                }
            } else {
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                discardPacket(packet);
            }
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
            switchingMatrixEntry = createEntryFromTLDP(packet, incomingPortID);
        }
        if (switchingMatrixEntry != null) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                this.requestTLDP(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                // Do nothing. The LSR is waiting for a label
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                sendTLDPRequestRefuse(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                sendTLDPRequestOk(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                sendTLDPWithdrawal(switchingMatrixEntry, incomingPortID);
                // FIX: Do not use hardcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                sendTLDPRequestOk(switchingMatrixEntry);
            } else {
                discardPacket(packet);
            }
        } else {
            discardPacket(packet);
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
            discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                // FIX: Avoid using harcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
            } else {
                discardPacket(packet);
            }
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
            discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(packet.getTLDPPayload().getLabel());
                if (switchingMatrixEntry.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                }
                TInternalLink internalLink = (TInternalLink) ports.getPort(incomingPortID).getLink();
                if (internalLink != null) {
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        internalLink.setAsUsedByABackupLSP();
                    } else {
                        internalLink.setAsUsedByALSP();
                    }
                }
                sendTLDPRequestOk(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(packet);
                // FIX: Avoid using harcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                discardPacket(packet);
            } else {
                discardPacket(packet);
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
            discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                sendTLDPRequestRefuse(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(packet);
                // FIX: Avoid using harcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                discardPacket(packet);
            } else {
                discardPacket(packet);
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
            discardPacket(packet);
        } else {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                TPort outgoingPort = ports.getPort(incomingPortID);
                TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                    internalLink.unlinkFromABackupLSP();
                } else {
                    internalLink.unlinkFromALSP();
                }
                this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                // FIX: Avoid using harcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                discardPacket(packet);
            } else {
                discardPacket(packet);
            }
        }
    }

    /**
     * This method sends a TLDP packet containing a label request
     * acknowledgement to a TLDP peer.
     *
     * @param switchingMatrixEntry that contains the needed data to contact to
     * the TLP peer that requested a label.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPRequestOk(TSwitchingMatrixEntry switchingMatrixEntry) {
        if (switchingMatrixEntry != null) {
            if (switchingMatrixEntry.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String localIPv4Address = this.getIPv4Address();
                String targetIPv4Address = this.ports.getIPv4OfNodeLinkedTo(switchingMatrixEntry.getIncomingPortID());
                if (targetIPv4Address != null) {
                    TTLDPPDU newTLDPPacket = null;
                    try {
                        newTLDPPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, targetIPv4Address);
                    } catch (Exception e) {
                        // FIX: this is not a good practice
                        e.printStackTrace();
                    }
                    if (newTLDPPacket != null) {
                        newTLDPPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_OK);
                        newTLDPPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPv4Address());
                        newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        newTLDPPacket.getTLDPPayload().setLabel(switchingMatrixEntry.getLabelOrFEC());
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort outgoingPort = ports.getLocalPortConnectedToANodeWithIPAddress(targetIPv4Address);
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            // FIX: this is not a good practice
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This method sends a TLDP packet containing a label refusal to a TLDP
     * peer.
     *
     * @param switchingMatrixEntry that contains the needed data to contact to
     * the TLP peer that requested a label.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPRequestRefuse(TSwitchingMatrixEntry switchingMatrixEntry) {
        if (switchingMatrixEntry != null) {
            if (switchingMatrixEntry.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String localIPv4Address = this.getIPv4Address();
                String targetIPv4Address = this.ports.getIPv4OfNodeLinkedTo(switchingMatrixEntry.getIncomingPortID());
                if (targetIPv4Address != null) {
                    TTLDPPDU newTLDPPacket = null;
                    try {
                        newTLDPPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, targetIPv4Address);
                    } catch (Exception e) {
                        // FIX: This is not a good practice
                        e.printStackTrace();
                    }
                    if (newTLDPPacket != null) {
                        newTLDPPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_DENIED);
                        newTLDPPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPv4Address());
                        newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        newTLDPPacket.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort outgoingPort = ports.getLocalPortConnectedToANodeWithIPAddress(targetIPv4Address);
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            // FIX: This is not a good practice
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * This method sends a TLDP packet containing a label refusal to a TLDP
     * peer.
     *
     * @param switchingMatrixEntry that contains the needed data to contact to
     * the TLP peer that requested a label.
     * @param portID the ID of the port through wich the TLDP packet has to be
     * sent.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPWithdrawalOk(TSwitchingMatrixEntry switchingMatrixEntry, int portID) {
        if (switchingMatrixEntry != null) {
            String localIPv4Address = this.getIPv4Address();
            String targetIPv4Address = this.ports.getIPv4OfNodeLinkedTo(portID);
            if (targetIPv4Address != null) {
                TTLDPPDU newTLDPPacket = null;
                try {
                    newTLDPPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, targetIPv4Address);
                } catch (Exception e) {
                    // FIX: This is not a good practice
                    e.printStackTrace();
                }
                if (newTLDPPacket != null) {
                    newTLDPPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REVOMAL_REQUEST_OK);
                    newTLDPPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPv4Address());
                    newTLDPPacket.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                    if (switchingMatrixEntry.getOutgoingPortID() == portID) {
                        newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                        newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (switchingMatrixEntry.getIncomingPortID() == portID) {
                        newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort outgoingPort = ports.getPort(portID);
                    outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        // FIX: This is not a good practice
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method sends a TLDP packet containing a label request to a TLDP
     * peer.
     *
     * @param switchingMatrixEntry that contains the needed data to contact to
     * the TLP peer to request a label.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void requestTLDP(TSwitchingMatrixEntry switchingMatrixEntry) {
        String localIPv4Address = this.getIPv4Address();
        String targetIPV4Address = switchingMatrixEntry.getTailEndIPv4Address();
        String nextHopIPAddress = this.topology.getNextHopIPv4Address(localIPv4Address, targetIPV4Address);
        if (nextHopIPAddress != null) {
            TTLDPPDU newTLDPPacket = null;
            try {
                newTLDPPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, nextHopIPAddress);
            } catch (Exception e) {
                // FIX: This is not a good practice
                e.printStackTrace();
            }
            if (newTLDPPacket != null) {
                newTLDPPacket.getTLDPPayload().setTargetIPAddress(targetIPV4Address);
                newTLDPPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                    newTLDPPacket.setLSPType(true);
                } else {
                    newTLDPPacket.setLSPType(false);
                }
                newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPAddress);
                if (outgoingPort != null) {
                    outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        // FIX: This is not a good practice
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method sends a TLDP packet containing a label withdrawal to a TLDP
     * peer.
     *
     * @param switchingMatrixEntry that contains the needed data to contact to
     * the TLP peer to request a label to compute a backup LSP.
     * @param portID the ID of the port through wich the TLDP withdrawal has to
     * be sent.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void sendTLDPWithdrawal(TSwitchingMatrixEntry switchingMatrixEntry, int portID) {
        if (switchingMatrixEntry != null) {
            if (switchingMatrixEntry.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                String localIPv4Address = this.getIPv4Address();
                String targetIPv4Address = switchingMatrixEntry.getTailEndIPv4Address();
                String nextHopIPv4Address = this.ports.getIPv4OfNodeLinkedTo(portID);
                if (nextHopIPv4Address != null) {
                    TTLDPPDU newTLDPPacket = null;
                    try {
                        newTLDPPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, nextHopIPv4Address);
                    } catch (Exception e) {
                        // FIX: This is not a good practice
                        e.printStackTrace();
                    }
                    if (newTLDPPacket != null) {
                        newTLDPPacket.getTLDPPayload().setTargetIPAddress(targetIPv4Address);
                        newTLDPPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REMOVAL_REQUEST);
                        if (switchingMatrixEntry.getOutgoingPortID() == portID) {
                            newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                        } else if (switchingMatrixEntry.getIncomingPortID() == portID) {
                            newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                            if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                                newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                            } else {
                                newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                            }
                        }
                        TPort outgoingPort = ports.getPort(portID);
                        if (outgoingPort != null) {
                            outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                            try {
                                this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                                this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP));
                            } catch (Exception e) {
                                // FIX: This is not a good practice
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method re-sends all request that are waiting for a acknowledgement,
     * after a timeout, to a TLDP peer.
     *
     * @param switchingMatrixEntry that contains the needed data to contact to
     * the TLP peer to re-send all unconfirmed TLDP requests.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void requestTLDPAfterTimeout(TSwitchingMatrixEntry switchingMatrixEntry) {
        if (switchingMatrixEntry != null) {
            String localIPv4Address = this.getIPv4Address();
            String targetIPv4Address = switchingMatrixEntry.getTailEndIPv4Address();
            String nextHopIPv4Address = this.ports.getIPv4OfNodeLinkedTo(switchingMatrixEntry.getOutgoingPortID());
            if (nextHopIPv4Address != null) {
                TTLDPPDU newTLDPPacket = null;
                try {
                    newTLDPPacket = new TTLDPPDU(this.gIdent.getNextID(), localIPv4Address, nextHopIPv4Address);
                } catch (Exception e) {
                    // FIX: This is not a good practice
                    e.printStackTrace();
                }
                if (newTLDPPacket != null) {
                    newTLDPPacket.getTLDPPayload().setTargetIPAddress(targetIPv4Address);
                    newTLDPPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                    newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        // FIX: Do not use harcoded boolean values. Use class
                        // constants instead.
                        newTLDPPacket.setLSPType(true);
                    } else {
                        // FIX: Do not use harcoded boolean values. Use class
                        // constants instead.
                        newTLDPPacket.setLSPType(false);
                    }
                    newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    if (outgoingPort != null) {
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            // FIX: This is not a good practice
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
        sendTLDPWithdrawal(switchingMatrixEntry, portID);
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
        sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
        sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
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
                switchingMatrixEntry.decreaseTimeOut(this.getTickDuration());
                // FIX: It is more efficient to use a switch clause instead of
                // nested ifs.
                if (switchingMatrixEntry.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    if (switchingMatrixEntry.shouldRetryExpiredTLDPRequest()) {
                        switchingMatrixEntry.resetTimeOut();
                        switchingMatrixEntry.decreaseAttempts();
                        requestTLDPAfterTimeout(switchingMatrixEntry);
                    }
                } else if (switchingMatrixEntry.getOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    if (switchingMatrixEntry.shouldRetryExpiredTLDPRequest()) {
                        switchingMatrixEntry.resetTimeOut();
                        switchingMatrixEntry.decreaseAttempts();
                        labelWithdrawalAfterTimeout(switchingMatrixEntry);
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
        // FIX: review the reason why this variable is nor used.
        TPort incomingPort = this.ports.getPort(incomingPortID);
        String targetIPv4Address = tldpPacket.getTLDPPayload().getTailEndIPAddress();
        String nextHopIPv4Address = this.topology.getNextHopIPv4Address(this.getIPv4Address(), targetIPv4Address);
        if (nextHopIPv4Address != null) {
            TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
            switchingMatrixEntry = new TSwitchingMatrixEntry();
            switchingMatrixEntry.setUpstreamTLDPSessionID(predecessorTLDPID);
            switchingMatrixEntry.setTailEndIPAddress(targetIPv4Address);
            switchingMatrixEntry.setIncomingPortID(incomingPortID);
            switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setLabelOrFEC(TSwitchingMatrixEntry.UNDEFINED);
            switchingMatrixEntry.setEntryAsForBackupLSP(tldpPacket.getLSPType());
            if (outgoingPort != null) {
                switchingMatrixEntry.setOutgoingPortID(outgoingPort.getPortID());
            } else {
                switchingMatrixEntry.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            switchingMatrixEntry.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
            switchingMatrixEntry.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            try {
                switchingMatrixEntry.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                // FIX: this is ugly. Avoid.
                e.printStackTrace();
            }
            this.switchingMatrix.addEntry(switchingMatrixEntry);
        }
        return switchingMatrixEntry;
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
            this.generateSimulationEvent(new TSimulationEventPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packet.getSubtype()));
            this.stats.addStatEntry(packet, TStats.DISCARD);
        } catch (Exception e) {
            // FIX: this is ugly. Avoid.
            e.printStackTrace();
        }
        packet = null;
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
        long weight = 0;
        long congestionWeight = (long) (this.ports.getCongestionLevel() * (0.7));
        long switchingMatrixWeight = (long) ((10 * this.switchingMatrix.getNumberOfEntries()) * (0.3));
        weight = congestionWeight + switchingMatrixWeight;
        return weight;
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
     * @return TLSRNode.OK if the configuration is correct. Otherwise, an error
     * code is returned. See public constants of error codes in this class.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TLSRNode.UNNAMED;
        }
        boolean onlyBlankSpaces = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                onlyBlankSpaces = false;
            }
        }
        if (onlyBlankSpaces) {
            return TLSRNode.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TNode nodeAux = topology.getFirstNodeNamed(this.getName());
            if (nodeAux != null) {
                return TLSRNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode nodeAux2 = topology.getFirstNodeNamed(this.getName());
            if (nodeAux2 != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return TLSRNode.NAME_ALREADY_EXISTS;
                }
            }
        }
        this.setWellConfigured(true);
        return TLSRNode.OK;
    }

    /**
     * This method generates an human-readable error message from a given error
     * code specified as ana argument.
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
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigLSR.FALTA_NOMBRE"));
            case NAME_ALREADY_EXISTS:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigLSR.NOMBRE_REPETIDO"));
            case ONLY_BLANK_SPACES:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TNodoLSR.NombreNoSoloEspacios"));
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
    public String marshall() {
        String serializedElement = "#LSR#";
        serializedElement += this.getNodeID();
        serializedElement += "#";
        serializedElement += this.getName().replace('#', ' ');
        serializedElement += "#";
        serializedElement += this.getIPv4Address();
        serializedElement += "#";
        serializedElement += this.isSelected();
        serializedElement += "#";
        serializedElement += this.nameMustBeDisplayed();
        serializedElement += "#";
        serializedElement += this.isGeneratingStats();
        serializedElement += "#";
        serializedElement += this.getScreenPosition().x;
        serializedElement += "#";
        serializedElement += this.getScreenPosition().y;
        serializedElement += "#";
        serializedElement += this.switchingPowerInMbps;
        serializedElement += "#";
        serializedElement += this.getPorts().getBufferSizeInMBytes();
        serializedElement += "#";
        return serializedElement;
    }

    /**
     * This method gets as an argument a serialized string that contains the
     * needed parameters to configure an TLSRNode and configure this node using
     * them.
     *
     * @param serializedLSR A serialized representation of a TActiveLSRNode.
     * @return true, whether the serialized string is correct and this node has
     * been initialized correctly using the serialized values. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public boolean unMarshall(String serializedLSR) {
        // FIX: All fixed values in this method should be implemented as class
        // constants instead of harcoded values.
        String[] elementFields = serializedLSR.split("#");
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
        this.switchingPowerInMbps = Integer.parseInt(elementFields[10]);
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
     * This method does nothing in an LSR node.
     *
     * @param packet Paquete cuya retransmisi�n se est� solicitando.
     * @param outgoingPortID Puerto por el que se enviar� la solicitud.
     * @since 2.0
     */
    @Override
    public void runGPSRP(TMPLSPDU packet, int outgoingPortID) {
        // FIX: This does nothing in a non-active node.
    }

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int NAME_ALREADY_EXISTS = 2;
    public static final int ONLY_BLANK_SPACES = 3;

    private TSwitchingMatrix switchingMatrix;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int switchingPowerInMbps;
    private TLSRStats stats;
}
