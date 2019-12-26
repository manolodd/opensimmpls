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
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketSwitched;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventNodeCongested;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketRouted;
import java.awt.Point;
import java.util.Iterator;
import com.manolodominguez.opensimmpls.protocols.TGPSRPPDU;
import com.manolodominguez.opensimmpls.protocols.TTLDPPDU;
import com.manolodominguez.opensimmpls.protocols.TGPSRPPayload;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSLabel;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.protocols.TTLDPPayload;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TActivePortSet;
import com.manolodominguez.opensimmpls.hardware.ports.TActivePort;
import com.manolodominguez.opensimmpls.hardware.dmgp.TDMGP;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrix;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrixEntry;
import com.manolodominguez.opensimmpls.hardware.dmgp.TGPSRPRequestsMatrix;
import com.manolodominguez.opensimmpls.hardware.dmgp.TGPSRPRequestEntry;
import com.manolodominguez.opensimmpls.hardware.ports.TPort;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.commons.TIDGenerator;
import com.manolodominguez.opensimmpls.commons.TLongIDGenerator;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.ResourceBundle;

/**
 * This class implements an active Label Switch Router (LSR) that will operate
 * inside a MPLS domain.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TActiveLSRNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TActiveLSRNode.
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
    public TActiveLSRNode(int identifier, String ipAddress, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(identifier, ipAddress, longIDGenerator, topology);
        // FIX: This is an overridable method call in constructor that should be 
        // avoided.
        this.setPorts(TNode.DEFAULT_NUM_PORTS_ACTIVE_LSR);
        this.switchingMatrix = new TSwitchingMatrix();
        this.gIdent = new TLongIDGenerator();
        this.gIdentLDP = new TIDGenerator();
        //FIX: replace with class constants.
        this.switchingPowerInMbps = 512;
        this.dmgp = new TDMGP();
        this.gpsrpRequests = new TGPSRPRequestsMatrix();
        this.stats = new TActiveLSRStats();
        this.translations = ResourceBundle.getBundle(AvailableBundles.T_ACTIVE_LSR_NODE.getPath());
    }

    /**
     * This method returns the size of the local DMGP (see the "Guarantee Of
     * Service Support Over MPLS Using Active Techniques" proposal) in KBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the current size of DMGP in KBytes.
     * @since 2.0
     */
    public int getDMGPSizeInKB() {
        return this.dmgp.getDMGPSizeInKB();
    }

    /**
     * This method sets the size of the local DMGP (see the "Guarantee Of
     * Service Support Over MPLS Using Active Techniques" proposal) in KBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param sizeInKB the desired size of DMGP in KBytes.
     * @since 2.0
     */
    public void setDMGPSizeInKB(int sizeInKB) {
        this.dmgp.setDMGPSizeInKB(sizeInKB);
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to switch a single bit. This is something that depends on the
     * switching power of this active LSR.
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
     * depends on the switching power of this active LSR.
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
     * This method gets the number of bits that this LSRA can switch with the
     * available number of nanoseconds it has. The more switching power the LSRA
     * has, the more bits it can switch with the same number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of bits that can be switched.
     * @since 2.0
     */
    public int getMaxSwitchableBitsWithCurrentNs() {
        double nsPerBit = getNsPerBit();
        double maxNumberOfBits = (double) ((double) this.availableNanoseconds / (double) nsPerBit);
        return (int) maxNumberOfBits;
    }

    /**
     * This method gets the number of octects that this LSRA can switch with the
     * available number of nanoseconds it has. The more switching power the LERA
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
     * This method gets the switching power of this LSRA, in Mbps.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the switching power of the node in Mbps.
     * @since 2.0
     */
    public int getSwitchingPowerInMbps() {
        return this.switchingPowerInMbps;
    }

    /**
     * This method sets the switching power of this LSRA, in Mbps.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param switchingPowerInMbps the switching power of this LSRA, in Mbps.
     * @since 2.0
     */
    public void setSwitchingPowerInMbps(int switchingPowerInMbps) {
        this.switchingPowerInMbps = switchingPowerInMbps;
    }

    /**
     * This method gets the size of this LSRA's buffer, in MBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the size of this LSRA's buffer, in MBytes.
     * @since 2.0
     */
    public int getBufferSizeInMBytes() {
        return this.getPorts().getBufferSizeInMBytes();
    }

    /**
     * This method sets the size of this LSRA's buffer, in MBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param bufferSizeInMBytes the size of this LSRA's buffer, in MBytes.
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
        this.dmgp.reset();
        this.gpsrpRequests.reset();
        this.resetTicksWithoutEmitting();
    }

    /**
     * This method gets the type of this node. In this case, it will return the
     * constant TNode.LSRA.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the constant TNode.LSRA.
     * @since 2.0
     */
    @Override
    public int getNodeType() {
        return TNode.ACTIVE_LSR;
    }

    /**
     * This method receive a timer event from the simulation's global timer. It
     * does some initial tasks and then wake up the LSRA to start doing its
     * work.
     *
     * @param timerEvent a timer event that is used to synchronize all nodes and
     * that inclues a number of nanoseconds to be used by the LSRA.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent timerEvent) {
        this.setTickDurationInNs(timerEvent.getTickDurationInNs());
        this.setCurrentTimeInstant(timerEvent.getUpperLimit());
        if (this.getPorts().isThereAnyPacketToSwitch()) {
            this.availableNanoseconds += timerEvent.getTickDurationInNs();
        } else {
            this.resetTicksWithoutEmitting();
            this.availableNanoseconds = timerEvent.getTickDurationInNs();
        }
        this.startOperation();
    }

    /**
     * This method starts all tasks that has to be executed during a timer tick.
     * The number of nanoseconds of this tick is the time this LSRA will work.
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
        this.switchPackets();
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
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        // FIX: Avoid using harcoded values
        int portIDAux = 0;
        TPort outgoingPort = null;
        TPort outgoingBackupPort = null;
        TPort incomingPort = null;
        TLink linkAux = null;
        this.switchingMatrix.getSemaphore().setRed();
        Iterator switchingMatrixIterator = this.switchingMatrix.getEntriesIterator();
        while (switchingMatrixIterator.hasNext()) {
            switchingMatrixEntry = (TSwitchingMatrixEntry) switchingMatrixIterator.next();
            if (switchingMatrixEntry != null) {
                portIDAux = switchingMatrixEntry.getBackupOutgoingPortID();
                // FIX: Avoid using harcoded values
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    outgoingBackupPort = this.ports.getPort(portIDAux);
                    if (outgoingBackupPort != null) {
                        linkAux = outgoingBackupPort.getLink();
                        if (linkAux != null) {
                            if ((linkAux.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                if (switchingMatrixEntry.backupLSPHasBeenEstablished() || switchingMatrixEntry.backupLSPShouldBeRemoved()) {
                                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                                    switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                                }
                            }
                        }
                    }
                }
                portIDAux = switchingMatrixEntry.getOutgoingPortID();
                // FIX: Avoid using harcoded values
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    outgoingPort = this.ports.getPort(portIDAux);
                    if (outgoingPort != null) {
                        linkAux = outgoingPort.getLink();
                        if (linkAux != null) {
                            if ((linkAux.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                if (switchingMatrixEntry.backupLSPHasBeenEstablished()) {
                                    switchingMatrixEntry.switchToBackupLSP();
                                } else {
                                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
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
                        linkAux = incomingPort.getLink();
                        if (linkAux != null) {
                            if ((linkAux.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
                                if (switchingMatrixEntry.backupLSPHasBeenEstablished() || switchingMatrixEntry.backupLSPShouldBeRemoved()) {
                                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                                    this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getBackupOutgoingPortID());
                                }
                            }
                        }
                    }
                }
            } else {
                switchingMatrixIterator.remove();
            }
        }
        this.switchingMatrix.getSemaphore().setGreen();
        this.gpsrpRequests.decreaseTimeout(this.getTickDurationInNs());
        this.gpsrpRequests.updateEntries();
        int numberOfPorts = this.ports.getNumberOfPorts();
        int i = 0;
        TActivePort currentPort = null;
        TLink linkOfPort = null;
        for (i = 0; i < numberOfPorts; i++) {
            currentPort = (TActivePort) this.ports.getPort(i);
            if (currentPort != null) {
                linkOfPort = currentPort.getLink();
                if (linkOfPort != null) {
                    if (linkOfPort.isBroken()) {
                        this.gpsrpRequests.removeEntriesMatchingOutgoingPort(i);
                    }
                }
            }
        }
        this.gpsrpRequests.getMonitor().setRed();
        Iterator gpsrpRequestsIterator = this.gpsrpRequests.getEntriesIterator();
        int flowID = 0;
        int packetGlobalUniqueID = 0;
        String targetIPv4Address = null;
        int outgoingPortAux = 0;
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (gpsrpRequestsIterator.hasNext()) {
            gpsrpRequestEntry = (TGPSRPRequestEntry) gpsrpRequestsIterator.next();
            if (gpsrpRequestEntry.isRetriable()) {
                flowID = gpsrpRequestEntry.getFlowID();
                packetGlobalUniqueID = gpsrpRequestEntry.getGlobalUniqueIdentifier();
                targetIPv4Address = gpsrpRequestEntry.getNearestCossedActiveNodeIPv4();
                outgoingPortAux = gpsrpRequestEntry.getOutgoingPortID();
                this.requestGPSRP(flowID, packetGlobalUniqueID, targetIPv4Address, outgoingPortAux);
            }
            gpsrpRequestEntry.resetTimeoutAndDecreaseAttempts();
        }
        this.gpsrpRequests.getMonitor().setGreen();
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
                    this.availableNanoseconds += getNsRequiredForAllOctets(packet.getSize());
                    discardPacket(packet);
                }
                this.availableNanoseconds -= getNsRequiredForAllOctets(packet.getSize());
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
            int messageType = packet.getGPSRPPayload().getGPSRPMessageType();
            // FIX: flowID and packetGlobalUniqueID seems not to be used. If not necessary,
            // remove from the code.
            int flowID = packet.getGPSRPPayload().getFlowID();
            int packetGlobalUniqueID = packet.getGPSRPPayload().getPacketID();
            String targetIPv4Address = packet.getIPv4Header().getTailEndIPAddress();
            TActivePort outgoingPort = null;
            if (targetIPv4Address.equals(this.getIPv4Address())) {
                // FIX: Convert to a switch statement
                if (messageType == TGPSRPPayload.RETRANSMISSION_REQUEST) {
                    this.handleGPSRPRetransmissionRequest(packet, incomingPortID);
                } else if (messageType == TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE) {
                    this.handleGPSRPRetransmissionNotPossible(packet, incomingPortID);
                } else if (messageType == TGPSRPPayload.RETRANSMISION_OK) {
                    this.handleGPSRPRetransmissionOk(packet, incomingPortID);
                }
            } else {
                String nextHopIPv4Address = this.topology.getRABANNextHopIPv4Address(this.getIPv4Address(), targetIPv4Address);
                outgoingPort = (TActivePort) this.ports.getLocalPortConnectedToANodeWithIPv4Address(nextHopIPv4Address);
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
     * This method handles a GPSRP request of retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet GPSRP packet to handle.
     * @param incomingPortID Port of this node where the packet has arrived.
     * @since 2.0
     */
    public void handleGPSRPRetransmissionRequest(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetGlobalUniqueID = packet.getGPSRPPayload().getPacketID();
        TMPLSPDU wantedPacket = (TMPLSPDU) this.dmgp.getPacket(flowID, packetGlobalUniqueID);
        if (wantedPacket != null) {
            this.acceptGPSRP(packet, incomingPortID);
            TActivePort outgoingPort = (TActivePort) this.ports.getPort(incomingPortID);
            if (outgoingPort != null) {
                outgoingPort.putPacketOnLink(wantedPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                try {
                    this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), wantedPacket.getSubtype()));
                } catch (Exception e) {
                    // FIX: this is not a good practice. Avoid.
                    e.printStackTrace();
                }
            }
        } else {
            this.rejectGPSRP(packet, incomingPortID);
        }
    }

    /**
     * This method handles a GPSRP denial of retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet GPSRP packet to handle.
     * @param incomingPortID Port of this node where the packet has arrived.
     * @since 2.0
     */
    public void handleGPSRPRetransmissionNotPossible(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetGlobalUniqueID = packet.getGPSRPPayload().getPacketID();
        TGPSRPRequestEntry gpsrpRequestEntry = this.gpsrpRequests.getEntry(flowID, packetGlobalUniqueID);
        if (gpsrpRequestEntry != null) {
            gpsrpRequestEntry.forceTimeoutReset();
            int p = gpsrpRequestEntry.getOutgoingPortID();
            if (!gpsrpRequestEntry.canBePurged()) {
                String targetIPv4Address = gpsrpRequestEntry.getNearestCossedActiveNodeIPv4();
                if (targetIPv4Address != null) {
                    requestGPSRP(flowID, packetGlobalUniqueID, targetIPv4Address, p);
                } else {
                    this.gpsrpRequests.removeEntry(flowID, packetGlobalUniqueID);
                }
            } else {
                this.gpsrpRequests.removeEntry(flowID, packetGlobalUniqueID);
            }
        }
    }

    /**
     * This method handles a GPSRP acceptance of retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet GPSRP packet to handle.
     * @param incomingPortID Port of this node where the packet has arrived.
     * @since 2.0
     */
    public void handleGPSRPRetransmissionOk(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetGlobalUniqueID = packet.getGPSRPPayload().getPacketID();
        this.gpsrpRequests.removeEntry(flowID, packetGlobalUniqueID);
    }

    /**
     * This method start the GPSRP protocol to request the retransmission of a
     * lost packet part of wich has been recovered before discarding it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet incomplete MPLS packet that want to be recovered locally.
     * @param outgoingPortID Port of this node through wich the GPSRP
     * retransmission request is going to be sent.
     * @since 2.0
     */
    @Override
    public void runGPSRP(TMPLSPDU packet, int outgoingPortID) {
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        gpsrpRequestEntry = this.gpsrpRequests.addEntry(packet, outgoingPortID);
        if (gpsrpRequestEntry != null) {
            TActivePort outgoingPort = (TActivePort) ports.getPort(outgoingPortID);
            TGPSRPPDU gpsrpPacket = null;
            String targetIPv4Address = gpsrpRequestEntry.getNearestCossedActiveNodeIPv4();
            if (targetIPv4Address != null) {
                try {
                    gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextIdentifier(), this.getIPv4Address(), targetIPv4Address);
                } catch (Exception e) {
                    //FIX: This is not a good practice. Avoid.
                    e.printStackTrace();
                }
                // FIX: gpsrPacket could be null if the previous try generates 
                // an exception. 
                gpsrpPacket.getGPSRPPayload().setFlowID(gpsrpRequestEntry.getFlowID());
                gpsrpPacket.getGPSRPPayload().setPacketID(gpsrpRequestEntry.getGlobalUniqueIdentifier());
                gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
                outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                try {
                    this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                    this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP));
                } catch (Exception e) {
                    //FIX: This is not a good practice. Avoid.
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method start the GPSRP protocol to request the retransmission of a
     * lost packet part of whose data has been recovered before discarding it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID flow ID for wich the retransmision is requested.
     * @param outgoingPortID Port of this node through wich the GPSRP
     * retransmission request is going to be sent.
     * @param packetGlobalUniqueID packet, of the specified flow, for wich the
     * retransmission is requested.
     * @param targetIPv4Address IP address of the node to wich the
     * retransmission request is sent. Hopefuly, the one that could retransmit
     * the lost packet.
     * @since 2.0
     */
    public void requestGPSRP(int flowID, int packetGlobalUniqueID, String targetIPv4Address, int outgoingPortID) {
        TActivePort outgoingPort = (TActivePort) this.ports.getPort(outgoingPortID);
        TGPSRPPDU gpsrpPacket = null;
        if (targetIPv4Address != null) {
            try {
                gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextIdentifier(), this.getIPv4Address(), targetIPv4Address);
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
            // FIX: gpsrPacket could be null if the previous try generates an 
            // exception. 
            gpsrpPacket.getGPSRPPayload().setFlowID(flowID);
            gpsrpPacket.getGPSRPPayload().setPacketID(packetGlobalUniqueID);
            gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
            outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
        }
    }

    /**
     * This method deny a packet retransmission.
     *
     * @param packet GPSRP packet requesting a packet retransmission.
     * @param outgoingPortID Outgoing port to be used to send the denial.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void rejectGPSRP(TGPSRPPDU packet, int outgoingPortID) {
        TActivePort outgoingPort = (TActivePort) this.ports.getPort(outgoingPortID);
        if (outgoingPort != null) {
            TGPSRPPDU gpsrpPacket = null;
            try {
                gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextIdentifier(), this.getIPv4Address(), packet.getIPv4Header().getOriginIPv4Address());
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
            // FIX: gpsrPacket could be null if the previous try generates an 
            // exception. 
            gpsrpPacket.getGPSRPPayload().setFlowID(packet.getGPSRPPayload().getFlowID());
            gpsrpPacket.getGPSRPPayload().setPacketID(packet.getGPSRPPayload().getPacketID());
            gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE);
            outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
        } else {
            discardPacket(packet);
        }
    }

    /**
     * This method accepts/confirms a packet retransmission.
     *
     * @param packet GPSRP packet requesting a packet retransmission.
     * @param outgoingPortID Outgoing port to be used to send the acceptance.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void acceptGPSRP(TGPSRPPDU packet, int outgoingPortID) {
        TActivePort outgoingPort = (TActivePort) this.ports.getPort(outgoingPortID);
        if (outgoingPort != null) {
            TGPSRPPDU gpsrpPacket = null;
            try {
                gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextIdentifier(), this.getIPv4Address(), packet.getIPv4Header().getOriginIPv4Address());
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
            // FIX: gpsrPacket could be null if the previous try generates an 
            // exception. 
            gpsrpPacket.getGPSRPPayload().setFlowID(packet.getGPSRPPayload().getFlowID());
            gpsrpPacket.getGPSRPPayload().setPacketID(packet.getGPSRPPayload().getPacketID());
            gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_OK);
            outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
        } else {
            discardPacket(packet);
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
        boolean requireBackupLSP = false;
        // FIX: Do not use harcoded values. Use class constants instead.
        if (packet.getLabelStack().getTop().getLabel() == 1) {
            mplsLabel = packet.getLabelStack().getTop();
            packet.getLabelStack().popTop();
            isLabeled = true;
            if ((mplsLabel.getEXP() == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)
                    || (mplsLabel.getEXP() == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)
                    || (mplsLabel.getEXP() == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)
                    || (mplsLabel.getEXP() == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                requireBackupLSP = true;
            }
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
                    // FIX: Do not use hardcoded values. Use class constants 
                    // instead.
                    mplsLabelAux.setBoS(false);
                    mplsLabelAux.setEXP(0);
                    mplsLabelAux.setLabel(switchingMatrixEntry.getOutgoingLabel());
                    mplsLabelAux.setTTL(packet.getLabelStack().getTop().getTTL() - 1);
                    packet.getLabelStack().pushTop(mplsLabelAux);
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                        packet.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPv4Address());
                        this.dmgp.addPacket(packet);
                    }
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.POP_LABEL) {
                    packet.getLabelStack().popTop();
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                        packet.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPv4Address());
                        this.dmgp.addPacket(packet);
                    }
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.SWAP_LABEL) {
                    if (requireBackupLSP) {
                        requestTLDPForBackupLSP(switchingMatrixEntry);
                    }
                    packet.getLabelStack().getTop().setLabel(switchingMatrixEntry.getOutgoingLabel());
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                        packet.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPv4Address());
                        this.dmgp.addPacket(packet);
                    }
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        TInternalLink internalLinkAux = (TInternalLink) outgoingPort.getLink();
                        internalLinkAux.setAsUsedByALSP();
                        internalLinkAux.unlinkFromABackupLSP();
                        switchingMatrixEntry.setEntryAsForBackupLSP(false);
                    }
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketSwitched(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packet.getSubtype()));
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
                // Do nothing. The Active LSR is waiting for a label
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
        } else if (switchingMatrixEntry.getIncomingPortID() == incomingPortID) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
            // FIX: Avoid using harcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
            } else {
                discardPacket(packet);
            }
            if (switchingMatrixEntry.backupLSPHasBeenEstablished() || switchingMatrixEntry.backupLSPShouldBeRemoved()) {
                int currentBackupLabel = switchingMatrixEntry.getBackupOutgoingLabel();
                if (currentBackupLabel == TSwitchingMatrixEntry.UNDEFINED) {
                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getBackupOutgoingPortID());
                } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getBackupOutgoingPortID());
                } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getBackupOutgoingPortID());
                } else if (currentBackupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    // Do nothing. Waiting for label removal...
                    // FIX: Avoid using harcoded values. Use class constants 
                    // instead.
                } else if (currentBackupLabel > 15) {
                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getBackupOutgoingPortID());
                } else {
                    discardPacket(packet);
                }
            }
        } else if (switchingMatrixEntry.getOutgoingPortID() == incomingPortID) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentLabel > 15) {
                if (switchingMatrixEntry.backupLSPHasBeenEstablished()) {
                    sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                    if (switchingMatrixEntry.getBackupOutgoingPortID() >= 0) {
                        TInternalLink internalLinkAux = (TInternalLink) this.ports.getPort(switchingMatrixEntry.getBackupOutgoingPortID()).getLink();
                        internalLinkAux.setAsUsedByALSP();
                        internalLinkAux.unlinkFromABackupLSP();
                    }
                    switchingMatrixEntry.switchToBackupLSP();
                } else {
                    switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                    sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
                }
            } else {
                discardPacket(packet);
            }
        } else if (switchingMatrixEntry.getBackupOutgoingPortID() == incomingPortID) {
            int currentBackupLabel = switchingMatrixEntry.getBackupOutgoingLabel();
            if (currentBackupLabel == TSwitchingMatrixEntry.UNDEFINED) {
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentBackupLabel > 15) {
                switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
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
        } else if (switchingMatrixEntry.getOutgoingPortID() == incomingPortID) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setOutgoingLabel(packet.getTLDPPayload().getLabel());
                if (switchingMatrixEntry.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                }
                TInternalLink internalLink = (TInternalLink) this.ports.getPort(incomingPortID).getLink();
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
        } else if (switchingMatrixEntry.getBackupOutgoingPortID() == incomingPortID) {
            int currentBackupLabel = switchingMatrixEntry.getBackupOutgoingLabel();
            if (currentBackupLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setBackupOutgoingLabel(packet.getTLDPPayload().getLabel());
                if (switchingMatrixEntry.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                }
                TInternalLink et = (TInternalLink) this.ports.getPort(incomingPortID).getLink();
                if (et != null) {
                    et.setAsUsedByABackupLSP();
                }
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(packet);
            // FIX: Avoid using harcoded values. Use class constants instead.
            } else if (currentBackupLabel > 15) {
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
        } else if (switchingMatrixEntry.getOutgoingPortID() == incomingPortID) {
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
        } else if (switchingMatrixEntry.getBackupOutgoingPortID() == incomingPortID) {
            int currentBackupLabel = switchingMatrixEntry.getBackupOutgoingLabel();
            if (currentBackupLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                sendTLDPRequestRefuse(switchingMatrixEntry);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(packet);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentBackupLabel > 15) {
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
        } else if (switchingMatrixEntry.getIncomingPortID() == incomingPortID) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if ((currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL)
                    || (currentLabel == TSwitchingMatrixEntry.LABEL_WITHDRAWN)) {
                if (switchingMatrixEntry.getOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    if (outgoingPort != null) {
                        TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            internalLink.unlinkFromABackupLSP();
                        } else {
                            internalLink.unlinkFromALSP();
                        }
                    }
                    switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                }
                if (switchingMatrixEntry.getBackupOutgoingLabel() != TSwitchingMatrixEntry.LABEL_WITHDRAWN) {
                    if (switchingMatrixEntry.getBackupOutgoingPortID() >= 0) {
                        TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getBackupOutgoingPortID());
                        if (outgoingPort != null) {
                            TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                            internalLink.unlinkFromABackupLSP();
                        }
                        switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                    }
                }
                TPort outgoingPort = this.ports.getPort(incomingPortID);
                if (outgoingPort != null) {
                    TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        internalLink.unlinkFromABackupLSP();
                    } else {
                        internalLink.unlinkFromALSP();
                    }
                }
                this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
            // FIX: Avoid using harcoded values. Use class constants instead.
            } else if (currentLabel > 15) {
                discardPacket(packet);
            } else {
                discardPacket(packet);
            }
        } else if (switchingMatrixEntry.getOutgoingPortID() == incomingPortID) {
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
                TPort outgoingPort = this.ports.getPort(incomingPortID);
                TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                    internalLink.unlinkFromABackupLSP();
                } else {
                    internalLink.unlinkFromALSP();
                }
                if (switchingMatrixEntry.getBackupOutgoingLabel() == TSwitchingMatrixEntry.LABEL_WITHDRAWN) {
                    this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                }
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentLabel > 15) {
                discardPacket(packet);
            } else {
                discardPacket(packet);
            }
        } else if (switchingMatrixEntry.getBackupOutgoingPortID() == incomingPortID) {
            int currentBackupLabel = switchingMatrixEntry.getBackupOutgoingLabel();
            if (currentBackupLabel == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if (currentBackupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                TPort outgoingPort = this.ports.getPort(incomingPortID);
                TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                internalLink.unlinkFromABackupLSP();
                switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                if (switchingMatrixEntry.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_WITHDRAWN) {
                    this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                }
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
            } else if (currentBackupLabel > 15) {
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
                        newTLDPPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, targetIPv4Address);
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
                        TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPv4Address(targetIPv4Address);
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
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
                        newTLDPPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, targetIPv4Address);
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
                        TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPv4Address(targetIPv4Address);
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
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
                    newTLDPPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, targetIPv4Address);
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
                    } else if (switchingMatrixEntry.getBackupOutgoingPortID() == portID) {
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
                    TPort outgoingPort = this.ports.getPort(portID);
                    outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
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
        String nextHopIPAddress = this.topology.getRABANNextHopIPv4Address(localIPv4Address, targetIPV4Address);
        if (nextHopIPAddress != null) {
            TTLDPPDU newTLDPPacket = null;
            try {
                newTLDPPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, nextHopIPAddress);
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
                TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPv4Address(nextHopIPAddress);
                if (outgoingPort != null) {
                    outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                    switchingMatrixEntry.setOutgoingPortID(outgoingPort.getPortID());
                    try {
                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        // FIX: This is not a good practice
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method sends a TLDP packet containing a label request to a TLDP peer
     * to compute a backup LSP.
     *
     * @param switchingMatrixEntry that contains the needed data to contact to
     * the TLP peer to request a label to compute a backup LSP.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void requestTLDPForBackupLSP(TSwitchingMatrixEntry switchingMatrixEntry) {
        String localIPv4Address = this.getIPv4Address();
        String targetIPv4Address = switchingMatrixEntry.getTailEndIPv4Address();
        String currentNextHopIPv4Address = this.ports.getIPv4OfNodeLinkedTo(switchingMatrixEntry.getOutgoingPortID());
        String backupNextHopIPv4Address = this.topology.getRABANNextHopIPv4Address(localIPv4Address, targetIPv4Address, currentNextHopIPv4Address);
        if (backupNextHopIPv4Address != null) {
            if (switchingMatrixEntry.getBackupOutgoingPortID() == TSwitchingMatrixEntry.UNDEFINED) {
                if (switchingMatrixEntry.getBackupOutgoingLabel() == TSwitchingMatrixEntry.UNDEFINED) {
                    // FIX: Do not use harcoded values. Use class constants 
                    // instead.
                    if (switchingMatrixEntry.getOutgoingLabel() > 15) {
                        switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                        // FIX: The following check is unnecessary. 
                        // backupNextHopIPv4Address is never null.
                        if (backupNextHopIPv4Address != null) {
                            TTLDPPDU newTLDPPacket = null;
                            try {
                                newTLDPPacket = new TTLDPPDU(gIdent.getNextIdentifier(), localIPv4Address, backupNextHopIPv4Address);
                            } catch (Exception e) {
                                // FIX: This is not a good practice
                                e.printStackTrace();
                            }
                            if (newTLDPPacket != null) {
                                newTLDPPacket.getTLDPPayload().setTargetIPAddress(targetIPv4Address);
                                newTLDPPacket.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                                newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getLocalTLDPSessionID());
                                newTLDPPacket.setLSPType(true);
                                newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                                TPort outgoingBackupPort = this.ports.getLocalPortConnectedToANodeWithIPv4Address(backupNextHopIPv4Address);
                                switchingMatrixEntry.setBackupOutgoingPortID(outgoingBackupPort.getPortID());
                                // FIX: The following check is unnecessary. 
                                // outgoingBackupPort is never null.
                                if (outgoingBackupPort != null) {
                                    outgoingBackupPort.putPacketOnLink(newTLDPPacket, outgoingBackupPort.getLink().getDestinationOfTrafficSentBy(this));
                                    try {
                                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
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
            switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
            String localIPv4Address = this.getIPv4Address();
            String targetIPv4Address = switchingMatrixEntry.getTailEndIPv4Address();
            String nextHopIPv4Address = this.ports.getIPv4OfNodeLinkedTo(portID);
            if (nextHopIPv4Address != null) {
                TTLDPPDU newTLDPPacket = null;
                try {
                    newTLDPPacket = new TTLDPPDU(gIdent.getNextIdentifier(), localIPv4Address, nextHopIPv4Address);
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
                    } else if (switchingMatrixEntry.getBackupOutgoingPortID() == portID) {
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
                    TPort outgoingPort = this.ports.getPort(portID);
                    if (outgoingPort != null) {
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getDestinationOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
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
                    newTLDPPacket = new TTLDPPDU(this.gIdent.getNextIdentifier(), localIPv4Address, nextHopIPv4Address);
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
                            this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), TAbstractPDU.TLDP));
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
        sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getBackupOutgoingPortID());
    }

    /**
     * This method decreases all retransmission counters for the this node.
     *
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void decreaseCounters() {
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        this.switchingMatrix.getSemaphore().setRed();
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
        this.switchingMatrix.getSemaphore().setGreen();
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
        String nextHopIPv4Address = this.topology.getRABANNextHopIPv4Address(this.getIPv4Address(), targetIPv4Address);
        if (nextHopIPv4Address != null) {
            TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPv4Address(nextHopIPv4Address);
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
     * @return TActiveLSRNode.OK if the configuration is correct. Otherwise, an
     * error code is returned. See public constants of error codes in this
     * class.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TActiveLSRNode.UNNAMED;
        }
        boolean onlyBlankSpaces = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                onlyBlankSpaces = false;
            }
        }
        if (onlyBlankSpaces) {
            return TActiveLSRNode.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TNode nodeAux = topology.getFirstNodeNamed(this.getName());
            if (nodeAux != null) {
                return TActiveLSRNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode nodeAux2 = topology.getFirstNodeNamed(this.getName());
            if (nodeAux2 != null) {
                if (this.topology.isThereMoreThanANodeNamed(this.getName())) {
                    return TActiveLSRNode.NAME_ALREADY_EXISTS;
                }
            }
        }
        this.setWellConfigured(true);
        return TActiveLSRNode.OK;
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
            case TActiveLSRNode.UNNAMED:
                return (this.translations.getString("TConfigLSR.FALTA_NOMBRE"));
            case TActiveLSRNode.NAME_ALREADY_EXISTS:
                return (this.translations.getString("TConfigLSR.NOMBRE_REPETIDO"));
            case TActiveLSRNode.ONLY_BLANK_SPACES:
                return (this.translations.getString("TNodoLSR.NombreNoSoloEspacios"));
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
        String serializedElement = "#LSRA#";
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
        serializedElement += this.switchingPowerInMbps;
        serializedElement += "#";
        serializedElement += this.getPorts().getBufferSizeInMBytes();
        serializedElement += "#";
        serializedElement += this.dmgp.getDMGPSizeInKB();
        serializedElement += "#";
        return serializedElement;
    }

    /**
     * This method gets as an argument a serialized string that contains the
     * needed parameters to configure an TActiveLSRNode and configure this node
     * using them.
     *
     * @param serializedLSRA A serialized representation of a TActiveLSRNode.
     * @return true, whether the serialized string is correct and this node has
     * been initialized correctly using the serialized values. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public boolean fromOSMString(String serializedLSRA) {
        // FIX: All fixed values in this method should be implemented as class
        // constants instead of harcoded values.
        String[] elementFields = serializedLSRA.split("#");
        if (elementFields.length != 13) {
            return false;
        }
        this.setNodeID(Integer.parseInt(elementFields[2]));
        this.setName(elementFields[3]);
        this.setIPv4Address(elementFields[4]);
        this.setSelected(Integer.parseInt(elementFields[5]));
        this.setShowName(Boolean.parseBoolean(elementFields[6]));
        this.setGenerateStats(Boolean.parseBoolean(elementFields[7]));
        int coordX = Integer.parseInt(elementFields[8]);
        int coordY = Integer.parseInt(elementFields[9]);
        this.setScreenPosition(new Point(coordX + 24, coordY + 24));
        this.switchingPowerInMbps = Integer.parseInt(elementFields[10]);
        this.getPorts().setBufferSizeInMB(Integer.parseInt(elementFields[11]));
        this.dmgp.setDMGPSizeInKB(Integer.parseInt(elementFields[12]));
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
     * This method sets the number of ports of this node.
     *
     * @param numPorts Number of ports of this node. The maximum allowed is 8.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public synchronized void setPorts(int numPorts) {
        this.ports = new TActivePortSet(numPorts, this);
    }

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int NAME_ALREADY_EXISTS = 2;
    public static final int ONLY_BLANK_SPACES = 3;

    private TSwitchingMatrix switchingMatrix;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int switchingPowerInMbps;
    private TDMGP dmgp;
    private TGPSRPRequestsMatrix gpsrpRequests;
    private TActiveLSRStats stats;
    private ResourceBundle translations;
}
