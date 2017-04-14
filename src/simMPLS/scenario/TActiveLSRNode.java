/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package simMPLS.scenario;

import java.awt.Point;
import java.util.Iterator;
import simMPLS.protocols.TGPSRPPDU;
import simMPLS.protocols.TTLDPPDU;
import simMPLS.protocols.TGPSRPPayload;
import simMPLS.protocols.TAbstractPDU;
import simMPLS.protocols.TMPLSLabel;
import simMPLS.protocols.TMPLSPDU;
import simMPLS.protocols.TTLDPPayload;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TActivePortSet;
import simMPLS.hardware.ports.TActivePort;
import simMPLS.hardware.dmgp.TDMGP;
import simMPLS.hardware.tldp.TSwitchingMatrix;
import simMPLS.hardware.tldp.TSwitchingMatrixEntry;
import simMPLS.hardware.dmgp.TGPSRPRequestsMatrix;
import simMPLS.hardware.dmgp.TGPSRPRequestEntry;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TPortSet;
import simMPLS.utils.TIDGenerator;
import simMPLS.utils.TLongIDGenerator;

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
        this.setPorts(TNode.NUM_LSRA_PORTS);
        this.switchingMatrix = new TSwitchingMatrix();
        this.gIdent = new TLongIDGenerator();
        this.gIdentLDP = new TIDGenerator();
        //FIX: replace with class constants.
        this.switchingPowerInMbps = 512;
        this.dmgp = new TDMGP();
        this.gpsrpRequests = new TGPSRPRequestsMatrix();
        this.stats = new TLSRAStats();
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
     * @return the current size of DMGP in KBytes.
     * @since 2.0
     */
    public int getMaxSwitchableBitsWithCurrentNs() {
        double nsPerBit = getNsPerBit();
        double maxNumberOfBits = (double) ((double) this.availableNs / (double) nsPerBit);
        return (int) maxNumberOfBits;
    }

    /**
     * This method gets the number of octects that this LSRA can switch with the
     * available number of nanoseconds it has. The more switching power the LERA
     * has, the more octects it can switch with the same number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the current size of DMGP in KBytes.
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
        this.stats.activateStats(this.isGeneratingStats());
        this.dmgp.reset();
        this.gpsrpRequests.reset();
        this.handleGPSRPPacket();
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
        return TNode.LSRA;
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
        this.setStepDuration(timerEvent.getStepDuration());
        this.setTimeInstant(timerEvent.getUpperLimit());
        if (this.getPorts().isAnyPacketToSwitch()) {
            this.availableNs += timerEvent.getStepDuration();
        } else {
            this.handleGPSRPPacket();
            this.availableNs = timerEvent.getStepDuration();
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
            this.generateSimulationEvent(new TSENodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), this.getPorts().getCongestionLevel()));
        } catch (Exception e) {
            // FIX: this is not a good practice. Avoid.
            e.printStackTrace();
        }
        this.checkConnectivityStatus();
        this.decreaseCounters();
        this.switchPackets();
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
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        // FIX: Avoid using harcoded values
        int portIDAux = 0;
        TPort outgoingPort = null;
        TPort outgoingBackupPort = null;
        TPort incomingPort = null;
        TLink linkAux = null;
        this.switchingMatrix.getMonitor().lock();
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
        this.switchingMatrix.getMonitor().unLock();
        this.gpsrpRequests.decreaseTimeout(this.getTickDuration());
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
        this.gpsrpRequests.getMonitor().lock();
        Iterator gpsrpRequestsIterator = this.gpsrpRequests.getEntriesIterator();
        int flowID = 0;
        int packetID = 0;
        String targetIPv4Address = null;
        int outgoingPortAux = 0;
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        while (gpsrpRequestsIterator.hasNext()) {
            gpsrpRequestEntry = (TGPSRPRequestEntry) gpsrpRequestsIterator.next();
            if (gpsrpRequestEntry.isRetryable()) {
                flowID = gpsrpRequestEntry.getFlowID();
                packetID = gpsrpRequestEntry.getPacketID();
                targetIPv4Address = gpsrpRequestEntry.getCrossedNodeIPv4();
                outgoingPortAux = gpsrpRequestEntry.getOutgoingPort();
                this.requestGPSRP(flowID, packetID, targetIPv4Address, outgoingPortAux);
            }
            gpsrpRequestEntry.resetTimeout();
        }
        this.gpsrpRequests.getMonitor().unLock();
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
                    conmutarGPSRP((TGPSRPPDU) packet, readPort);
                } else {
                    this.availableNs += getNsRequiredForAllOctets(packet.getSize());
                    discardPacket(packet);
                }
                this.availableNs -= getNsRequiredForAllOctets(packet.getSize());
                switchableOctectsWithCurrentNs = this.getMaxSwitchableOctectsWithCurrentNs();
            }
        }
        if (atLeastOnePacketSwitched) {
            this.handleGPSRPPacket();
        } else {
            this.increaseStepsWithoutEmitting();
        }
    }

    /**
     * This method switchs an incoming GPDRP packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet GPSRP packet to switch.
     * @param incomingPortID Port of this node where the packet has arrived.
     * @since 2.0
     */
    public void conmutarGPSRP(TGPSRPPDU packet, int incomingPortID) {
        if (packet != null) {
            int messageType = packet.getGPSRPPayload().getGPSRPMessageType();
            // FIX: flowID and packetID seems not to be used. If not necessary,
            // remove from the code.
            int flowID = packet.getGPSRPPayload().getFlowID();
            int packetID = packet.getGPSRPPayload().getPacketID();
            String targetIPv4Address = packet.getIPv4Header().getTailEndIPAddress();
            TActivePort outgoingPort = null;
            if (targetIPv4Address.equals(this.getIPv4Address())) {
                // FIX: Convert to a switch statement
                if (messageType == TGPSRPPayload.RETRANSMISSION_REQUEST) {
                    this.atenderPeticionGPSRP(packet, incomingPortID);
                } else if (messageType == TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE) {
                    this.atenderDenegacionGPSRP(packet, incomingPortID);
                } else if (messageType == TGPSRPPayload.RETRANSMISION_OK) {
                    this.atenderAceptacionGPSRP(packet, incomingPortID);
                }
            } else {
                String nextHopIPv4Address = this.topology.getNextHopRABANIPv4Address(this.getIPv4Address(), targetIPv4Address);
                outgoingPort = (TActivePort) this.ports.getLocalPortConnectedToANodeWithIPAddress(nextHopIPv4Address);
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
     * This method handles a GPSRP request of retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet GPSRP packet to handle.
     * @param incomingPortID Port of this node where the packet has arrived.
     * @since 2.0
     */
    public void atenderPeticionGPSRP(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetID = packet.getGPSRPPayload().getPacketID();
        TMPLSPDU wantedPacket = (TMPLSPDU) this.dmgp.getPacket(flowID, packetID);
        if (wantedPacket != null) {
            this.acceptGPSRP(packet, incomingPortID);
            TActivePort outgoingPort = (TActivePort) this.ports.getPort(incomingPortID);
            if (outgoingPort != null) {
                outgoingPort.putPacketOnLink(wantedPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                try {
                    this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), wantedPacket.getSubtype()));
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
    public void atenderDenegacionGPSRP(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetID = packet.getGPSRPPayload().getPacketID();
        TGPSRPRequestEntry gpsrpRequestEntry = this.gpsrpRequests.getEntry(flowID, packetID);
        if (gpsrpRequestEntry != null) {
            gpsrpRequestEntry.forceTimeoutReset();
            int p = gpsrpRequestEntry.getOutgoingPort();
            if (!gpsrpRequestEntry.isPurgeable()) {
                String targetIPv4Address = gpsrpRequestEntry.getCrossedNodeIPv4();
                if (targetIPv4Address != null) {
                    requestGPSRP(flowID, packetID, targetIPv4Address, p);
                } else {
                    this.gpsrpRequests.removeEntry(flowID, packetID);
                }
            } else {
                this.gpsrpRequests.removeEntry(flowID, packetID);
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
    public void atenderAceptacionGPSRP(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetID = packet.getGPSRPPayload().getPacketID();
        this.gpsrpRequests.removeEntry(flowID, packetID);
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
    public void runGoSPDUStoreAndRetransmitProtocol(TMPLSPDU packet, int outgoingPortID) {
        TGPSRPRequestEntry gpsrpRequestEntry = null;
        gpsrpRequestEntry = this.gpsrpRequests.addEntry(packet, outgoingPortID);
        if (gpsrpRequestEntry != null) {
            TActivePort outgoingPort = (TActivePort) ports.getPort(outgoingPortID);
            TGPSRPPDU gpsrpPacket = null;
            String targetIPv4Address = gpsrpRequestEntry.getCrossedNodeIPv4();
            if (targetIPv4Address != null) {
                try {
                    gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextID(), this.getIPv4Address(), targetIPv4Address);
                } catch (Exception e) {
                    //FIX: This is not a good practice. Avoid.
                    e.printStackTrace();
                }
                // FIX: gpsrPacket could be null if the previous try generates 
                // an exception. 
                gpsrpPacket.getGPSRPPayload().setFlowID(gpsrpRequestEntry.getFlowID());
                gpsrpPacket.getGPSRPPayload().setPacketID(gpsrpRequestEntry.getPacketID());
                gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
                outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                try {
                    this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                    this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
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
     * @param packetID packet, of the specified flow, for wich the
     * retransmission is requested.
     * @param targetIPv4Address IP address of the node to wich the
     * retransmission request is sent. Hopefuly, the one that could retransmit
     * the lost packet.
     * @since 2.0
     */
    public void requestGPSRP(int flowID, int packetID, String targetIPv4Address, int outgoingPortID) {
        TActivePort outgoingPort = (TActivePort) this.ports.getPort(outgoingPortID);
        TGPSRPPDU gpsrpPacket = null;
        if (targetIPv4Address != null) {
            try {
                gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextID(), this.getIPv4Address(), targetIPv4Address);
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
            // FIX: gpsrPacket could be null if the previous try generates an 
            // exception. 
            gpsrpPacket.getGPSRPPayload().setFlowID(flowID);
            gpsrpPacket.getGPSRPPayload().setPacketID(packetID);
            gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
            outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
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
                gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextID(), this.getIPv4Address(), packet.getIPv4Header().getOriginIPAddress());
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
            // FIX: gpsrPacket could be null if the previous try generates an 
            // exception. 
            gpsrpPacket.getGPSRPPayload().setFlowID(packet.getGPSRPPayload().getFlowID());
            gpsrpPacket.getGPSRPPayload().setPacketID(packet.getGPSRPPayload().getPacketID());
            gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE);
            outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
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
                gpsrpPacket = new TGPSRPPDU(this.gIdent.getNextID(), this.getIPv4Address(), packet.getIPv4Header().getOriginIPAddress());
            } catch (Exception e) {
                //FIX: This is not a good practice. Avoid.
                e.printStackTrace();
            }
            // FIX: gpsrPacket could be null if the previous try generates an 
            // exception. 
            gpsrpPacket.getGPSRPPayload().setFlowID(packet.getGPSRPPayload().getFlowID());
            gpsrpPacket.getGPSRPPayload().setPacketID(packet.getGPSRPPayload().getPacketID());
            gpsrpPacket.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_OK);
            outgoingPort.putPacketOnLink(gpsrpPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, gpsrpPacket.getSize()));
                this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
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
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                    } catch (Exception e) {
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
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        TInternalLink ei = (TInternalLink) outgoingPort.getLink();
                        ei.setLSPUp();
                        ei.setBackupLSPDown();
                        switchingMatrixEntry.setEntryIsForBackupLSP(false);
                    }
                    try {
                        this.generateSimulationEvent(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
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
                        internalLinkAux.setLSPUp();
                        internalLinkAux.setBackupLSPDown();
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
                        internalLink.setBackupLSP();
                    } else {
                        internalLink.setLSPUp();
                    }
                }
                sendTLDPRequestOk(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(packet);
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
                switchingMatrixEntry.setBackupOutgoingLabel(packet.getTLDPPayload().getLabel());
                if (switchingMatrixEntry.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    switchingMatrixEntry.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                }
                TInternalLink et = (TInternalLink) this.ports.getPort(incomingPortID).getLink();
                if (et != null) {
                    et.setBackupLSP();
                }
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
                            internalLink.setBackupLSPDown();
                        } else {
                            internalLink.removeLSP();
                        }
                    }
                    switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                }
                if (switchingMatrixEntry.getBackupOutgoingLabel() != TSwitchingMatrixEntry.LABEL_WITHDRAWN) {
                    if (switchingMatrixEntry.getBackupOutgoingPortID() >= 0) {
                        TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getBackupOutgoingPortID());
                        if (outgoingPort != null) {
                            TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                            internalLink.setBackupLSPDown();
                        }
                        switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                    }
                }
                TPort outgoingPort = this.ports.getPort(incomingPortID);
                if (outgoingPort != null) {
                    TInternalLink internalLink = (TInternalLink) outgoingPort.getLink();
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        internalLink.setBackupLSPDown();
                    } else {
                        internalLink.removeLSP();
                    }
                }
                this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
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
                    internalLink.setBackupLSPDown();
                } else {
                    internalLink.removeLSP();
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
                internalLink.setBackupLSPDown();
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
                String targetIPv4Address = this.ports.getIPOfNodeLinkedTo(switchingMatrixEntry.getIncomingPortID());
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
                        newTLDPPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPAddress());
                        newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        newTLDPPacket.getTLDPPayload().setLabel(switchingMatrixEntry.getLabelOrFEC());
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(targetIPv4Address);
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
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
                String targetIPv4Address = this.ports.getIPOfNodeLinkedTo(switchingMatrixEntry.getIncomingPortID());
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
                        newTLDPPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPAddress());
                        newTLDPPacket.getTLDPPayload().setTLDPIdentifier(switchingMatrixEntry.getUpstreamTLDPSessionID());
                        newTLDPPacket.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                        if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            newTLDPPacket.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort outgoingPort = this.ports.getLocalPortConnectedToANodeWithIPAddress(targetIPv4Address);
                        outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
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
            String targetIPv4Address = this.ports.getIPOfNodeLinkedTo(portID);
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
                    newTLDPPacket.getTLDPPayload().setTargetIPAddress(switchingMatrixEntry.getTailEndIPAddress());
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
                    outgoingPort.putPacketOnLink(newTLDPPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, newTLDPPacket.getSize()));
                        this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        // FIX: This is not a good practice
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Este m�todo solicita una etiqueta al nodo indicado por la correspondiente
     * entrada en la matriz de conmutaci�n.
     *
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void requestTLDP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        String IPSalto = this.topology.getNextHopRABANIPv4Address(IPLocal, IPDestinoFinal);
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
                    emc.setOutgoingPortID(pSalida.getPortID());
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

    /**
     * Este m�todo solicita una etiqueta al nodo indicado por la correspondiente
     * entrada en la matriz de conmutaci�n. El camino solicitado ser� de Backup.
     *
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void requestTLDPForBackupLSP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        String IPSaltoPrincipal = this.ports.getIPOfNodeLinkedTo(emc.getOutgoingPortID());
        String IPSalto = this.topology.getNextHopRABANIPv4Address(IPLocal, IPDestinoFinal, IPSaltoPrincipal);
        if (IPSalto != null) {
            if (emc.getBackupOutgoingPortID() == TSwitchingMatrixEntry.UNDEFINED) {
                if (emc.getBackupOutgoingLabel() == TSwitchingMatrixEntry.UNDEFINED) {
                    if (emc.getOutgoingLabel() > 15) {
                        emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                        if (IPSalto != null) {
                            TTLDPPDU paqueteTLDP = null;
                            try {
                                paqueteTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPSalto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (paqueteTLDP != null) {
                                paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                                paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                                paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                                paqueteTLDP.setLSPType(true);
                                paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                                TPort pSalida = this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
                                emc.setBackupOutgoingPortID(pSalida.getPortID());
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
            }
        }
    }

    /**
     * Este m�todo elimina una etiqueta del nodo indicado por la correspondiente
     * entrada en la matriz de conmutaci�n.
     *
     * @since 2.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     */
    public void sendTLDPWithdrawal(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
            String IPLocal = this.getIPv4Address();
            String IPDestinoFinal = emc.getTailEndIPAddress();
            String IPSalto = this.ports.getIPOfNodeLinkedTo(puerto);
            if (IPSalto != null) {
                TTLDPPDU paqueteTLDP = null;
                try {
                    paqueteTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                    paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REMOVAL_REQUEST);
                    if (emc.getOutgoingPortID() == puerto) {
                        paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getBackupOutgoingPortID() == puerto) {
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
     * Este m�todo reenv�a todas las peticiones pendiente de confirmaci�n al
     * nodo especificadao por la correspondiente entrada en la matriz de
     * conmutaci�n.
     *
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void solicitarTLDPTrasTimeout(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            String IPLocal = this.getIPv4Address();
            String IPDestinoFinal = emc.getTailEndIPAddress();
            String IPSalto = this.ports.getIPOfNodeLinkedTo(emc.getOutgoingPortID());
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
    public void eliminarTLDPTrasTimeout(TSwitchingMatrixEntry emc, int puerto) {
        sendTLDPWithdrawal(emc, puerto);
    }

    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de
     * una entrada de la matriz de conmutaci�n a todos los ports necesarios.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void eliminarTLDPTrasTimeout(TSwitchingMatrixEntry emc) {
        sendTLDPWithdrawal(emc, emc.getIncomingPortID());
        sendTLDPWithdrawal(emc, emc.getOutgoingPortID());
        sendTLDPWithdrawal(emc, emc.getBackupOutgoingPortID());
    }

    /**
     * Este m�todo decrementa los contadores para la retransmisi�n.
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
                        eliminarTLDPTrasTimeout(emc);
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
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n a partir
     * de una solicitud de etiqueta recibida.
     *
     * @param paqueteSolicitud Solicitud de etiqueta recibida.
     * @param pEntrada Puerto por el que se ha recibido la solicitud.
     * @return La nueva entrada en la matriz de conmutaci�n, creda, insertada e
     * inicializada.
     * @since 2.0
     */
    public TSwitchingMatrixEntry createEntryFromTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.getTLDPPayload().getTLDPIdentifier();
        TPort puertoEntrada = this.ports.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.getTLDPPayload().getTailEndIPAddress();
        String IPSalto = this.topology.getNextHopRABANIPv4Address(this.getIPv4Address(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
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
            emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
            emc.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
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
     * Este m�todo descarta un packet del ndo y refleja este descarte en las
     * estad�sticas del nodo.
     *
     * @param paquete Paquete que queremos descartar.
     * @since 2.0
     */
    @Override
    public void discardPacket(TAbstractPDU paquete) {
        try {
            this.generateSimulationEvent(new TSEPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
            this.stats.addStatsEntry(paquete, TStats.DISCARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paquete = null;
    }

    /**
     * Este m�todo permite acceder a los ports del nodo directamtne.
     *
     * @return El conjunto de ports del nodo.
     * @since 2.0
     */
    @Override
    public TPortSet getPorts() {
        return this.ports;
    }

    /**
     * Este m�todo devuelve si el nodo tiene ports libres o no.
     *
     * @return TRUE, si el nodo tiene ports libres. FALSE en caso contrario.
     * @since 2.0
     */
    @Override
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }

    /**
     * Este m�todo devuelve el peso del nodo, que debe ser tomado en cuenta por
     * lo algoritmos de encaminamiento para calcular las rutas.
     *
     * @return El peso del LSRA..
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
     * Este m�todo calcula si el nodo est� bien configurado o no.
     *
     * @return TRUE, si el ndoo est� bien configurado. FALSE en caso contrario.
     * @since 2.0
     */
    @Override
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }

    /**
     * Este m�todo devuelve si el nodo est� bien configurado y si no, la raz�n.
     *
     * @param t La topolog�a donde est� el nodo incluido.
     * @param recfg TRUE, si se est� reconfigurando el LSR. FALSE si se est�
     * configurando por primera vez.
     * @return OK, si el nodo est� bien configurado. Un c�digo de error en caso
     * contrario.
     * @since 2.0
     */
    @Override
    public int validateConfig(TTopology t, boolean recfg) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TActiveLSRNode.UNNAMED;
        }
        boolean soloEspacios = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                soloEspacios = false;
            }
        }
        if (soloEspacios) {
            return TActiveLSRNode.ONLY_BLANK_SPACES;
        }
        if (!recfg) {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null) {
                return TActiveLSRNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return TActiveLSRNode.NAME_ALREADY_EXISTS;
                }
            }
        }
        this.setWellConfigured(true);
        return TActiveLSRNode.OK;
    }

    /**
     * Este m�todo transforma el c�digo de error de configuraci�n del nodo en un
     * messageType aclaratorio.
     *
     * @param e C�digo de error.
     * @return Texto explicativo del c�digo de error.
     * @since 2.0
     */
    @Override
    public String getErrorMessage(int e) {
        switch (e) {
            case TActiveLSRNode.UNNAMED:
                return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLSR.FALTA_NOMBRE"));
            case TActiveLSRNode.NAME_ALREADY_EXISTS:
                return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLSR.NOMBRE_REPETIDO"));
            case TActiveLSRNode.ONLY_BLANK_SPACES:
                return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoLSR.NombreNoSoloEspacios"));
        }
        return ("");
    }

    /**
     * Este m�todo permite transformar el nodo en una cadena de texto que se
     * puede volcar f�cilmente a disco.
     *
     * @return Una cadena de texto que representa al nodo.
     * @since 2.0
     */
    @Override
    public String marshall() {
        String cadena = "#LSRA#";
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
        cadena += this.switchingPowerInMbps;
        cadena += "#";
        cadena += this.getPorts().getBufferSizeInMBytes();
        cadena += "#";
        cadena += this.dmgp.getDMGPSizeInKB();
        cadena += "#";
        return cadena;
    }

    /**
     * Este m�todo permite construir sobre la instancia actual, un LSR partiendo
     * de la representaci�n serializada de otro.
     *
     * @param elemento �lemento serializado que se desea deserializar.
     * @return TRUE, si se ha conseguido deserializar correctamente. FALSE en
     * caso contrario.
     * @since 2.0
     */
    @Override
    public boolean unMarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 13) {
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
        this.switchingPowerInMbps = Integer.parseInt(valores[10]);
        this.getPorts().setBufferSizeInMB(Integer.parseInt(valores[11]));
        this.dmgp.setDMGPSizeInKB(Integer.parseInt(valores[12]));
        return true;
    }

    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
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
     * @param num N�mero de ports del nodo. Como mucho 8.
     * @since 2.0
     */
    @Override
    public synchronized void setPorts(int num) {
        this.ports = new TActivePortSet(num, this);
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
    private TLSRAStats stats;
}
