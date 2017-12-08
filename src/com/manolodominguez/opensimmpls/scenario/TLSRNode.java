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
        this.setPorts(TNode.NUM_LSR_PORTS);
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
    public void ponerPotenciaEnMb(int switchingPowerInMbps) {
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
    public void ponerTamanioBuffer(int bufferSizeInMBytes) {
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
     * The number of nanoseconds of this tick is the time this LSR will work.
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
                    }
                    TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
                    outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    if (switchingMatrixEntry.aBackupLSPHasBeenRequested()) {
                        TInternalLink internalLinkAux = (TInternalLink) outgoingPort.getLink();
                        internalLinkAux.setAsUsedByALSP();
                        internalLinkAux.unlinkFromABackupLSP();
                        switchingMatrixEntry.setEntryAsForBackupLSP(false);
                    }
                    try {
                        this.generateSimulationEvent(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                    } catch (Exception e) {
                        // FIX: This is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                } else if (operation == TSwitchingMatrixEntry.NOOP) {
                    TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
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
     * Este m�todo trata una petici�n de etiquetas.
     *
     * @param paquete Petici�n de etiquetas recibida de otro nodo.
     * @param pEntrada Puerto de entrada de la petici�n de etiqueta.
     * @since 2.0
     */
    public void handleTLDPRequest(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = this.switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        if (emc == null) {
            emc = crearEntradaAPartirDeTLDP(paquete, pEntrada);
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                this.requestTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                // no hago nada. Se est� esperando una etiqueta.);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                sendTLDPWithdrawal(emc, pEntrada);
            } else if (etiquetaActual > 15) {
                enviarSolicitudOkTLDP(emc);
            } else {
                discardPacket(paquete);
            }
        } else {
            discardPacket(paquete);
        }
    }

    /**
     * Este m�todo trata un paquete TLDP de eliminaci�n de etiqueta.
     *
     * @param paquete Eliminaci�n de etiqueta recibida.
     * @param pEntrada Puerto por el que se recibi�n la eliminaci�n de etiqueta.
     * @since 2.0
     */
    public void handleTLDPWithdrawal(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.getLocalOrigin() == TTLDPPDU.CAME_BY_ENTRANCE) {
            emc = this.switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        } else {
            emc = this.switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                enviarEliminacionOkTLDP(emc, pEntrada);
                sendTLDPWithdrawal(emc, emc.getOppositePortID(pEntrada));
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                enviarEliminacionOkTLDP(emc, pEntrada);
                sendTLDPWithdrawal(emc, emc.getOppositePortID(pEntrada));
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                enviarEliminacionOkTLDP(emc, pEntrada);
                sendTLDPWithdrawal(emc, emc.getOppositePortID(pEntrada));
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                enviarEliminacionOkTLDP(emc, pEntrada);
            } else if (etiquetaActual > 15) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                enviarEliminacionOkTLDP(emc, pEntrada);
                sendTLDPWithdrawal(emc, emc.getOppositePortID(pEntrada));
            } else {
                discardPacket(paquete);
            }
        }
    }

    /**
     * Este m�todo trata un paquete TLDP de confirmaci�n de etiqueta.
     *
     * @param paquete Confirmaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de
     * etiquetas.
     * @since 2.0
     */
    public void handleTLDPRequestOk(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = this.switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                emc.setOutgoingLabel(paquete.getTLDPPayload().getLabel());
                if (emc.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setLabelOrFEC(this.switchingMatrix.getNewLabel());
                }
                TInternalLink et = (TInternalLink) ports.getPort(pEntrada).getLink();
                if (et != null) {
                    if (emc.aBackupLSPHasBeenRequested()) {
                        et.setAsUsedByABackupLSP();
                    } else {
                        et.setAsUsedByALSP();
                    }
                }
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(paquete);
            } else if (etiquetaActual > 15) {
                discardPacket(paquete);
            } else {
                discardPacket(paquete);
            }
        }
    }

    /**
     * Este m�todo trata un paquete TLDP de denegaci�n de etiqueta.
     *
     * @param paquete Paquete de denegaci�n de etiquetas recibido.
     * @param pEntrada Puerto por el que se ha recibido la denegaci�n de
     * etiquetas.
     * @since 2.0
     */
    public void handleTLDPRefuseRequest(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = this.switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(paquete);
            } else if (etiquetaActual > 15) {
                discardPacket(paquete);
            } else {
                discardPacket(paquete);
            }
        }
    }

    /**
     * Este m�todo trata un paquete TLDP de confirmaci�n de eliminaci�n de
     * etiqueta.
     *
     * @param paquete Paquete de confirmaci�n e eliminaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de
     * eliminaci�n de etiqueta.
     * @since 2.0
     */
    public void handleTLDPWithdrawalOk(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.getLocalOrigin() == TTLDPPDU.CAME_BY_ENTRANCE) {
            emc = this.switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        } else {
            emc = this.switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                TPort pSalida = ports.getPort(pEntrada);
                TInternalLink ei = (TInternalLink) pSalida.getLink();
                if (emc.aBackupLSPHasBeenRequested()) {
                    ei.unlinkFromABackupLSP();
                } else {
                    ei.unlinkFromALSP();
                }
                this.switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
            } else if (etiquetaActual > 15) {
                discardPacket(paquete);
            } else {
                discardPacket(paquete);
            }
        }
    }

    /**
     * Este m�todo env�a una etiqueta al nodo que indique la entrada en la
     * matriz de conmutaci�n especificada.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void enviarSolicitudOkTLDP(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPv4Address();
                String IPDestino = this.ports.getIPv4OfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(this.gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_OK);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPv4Address());
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.getTLDPPayload().setLabel(emc.getLabelOrFEC());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort pSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPDestino);
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
    }

    /**
     * Este m�todo env�a una denegaci�n de etiqueta al nodo que especifique la
     * entrada de la matriz de conmutaci�n correspondiente.
     *
     * @param emc Entrada de la matriz de conmutaci�n correspondiente.
     * @since 2.0
     */
    public void enviarSolicitudNoTLDP(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPv4Address();
                String IPDestino = this.ports.getIPv4OfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(this.gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_DENIED);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPv4Address());
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort pSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPDestino);
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
    }

    /**
     * Este m�todo env�a una confirmaci�n de eliminaci�n de etiqueta al nodo que
     * especifique la correspondiente entrada en la matriz de conmutaci�n.
     *
     * @since 2.0
     * @param puerto Puerto por el que se debe enviar la confirmaci�n de
     * eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void enviarEliminacionOkTLDP(TSwitchingMatrixEntry emc, int puerto) {
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
                    TPort pSalida = ports.getPort(puerto);
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
        String IPSalto = this.topology.getNextHopIPv4Address(IPLocal, IPDestinoFinal);
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
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
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
                        TPort pSalida = ports.getPort(puerto);
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
    public TSwitchingMatrixEntry crearEntradaAPartirDeTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.getTLDPPayload().getTLDPIdentifier();
        TPort puertoEntrada = this.ports.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.getTLDPPayload().getTailEndIPAddress();
        String IPSalto = this.topology.getNextHopIPv4Address(this.getIPv4Address(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(IdTLDPAntecesor);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setLabelOrFEC(TSwitchingMatrixEntry.UNDEFINED);
            emc.setEntryAsForBackupLSP(paqueteSolicitud.getLSPType());
            if (puertoSalida != null) {
                emc.setOutgoingPortID(puertoSalida.getPortID());
            } else {
                emc.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
            emc.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
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
     * Este m�todo descarta un paquete del ndo y refleja este descarte en las
     * estad�sticas del nodo.
     *
     * @param paquete Paquete que queremos descartar.
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
     * @return El peso del LSR.
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
     * @return CORRECTA, si el nodo est� bien configurado. Un c�digo de error en
     * caso contrario.
     * @since 2.0
     */
    @Override
    public int validateConfig(TTopology t, boolean recfg) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TLSRNode.SIN_NOMBRE;
        }
        boolean soloEspacios = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                soloEspacios = false;
            }
        }
        if (soloEspacios) {
            return TLSRNode.SOLO_ESPACIOS;
        }
        if (!recfg) {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null) {
                return TLSRNode.NOMBRE_YA_EXISTE;
            }
        } else {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return TLSRNode.NOMBRE_YA_EXISTE;
                }
            }
        }
        this.setWellConfigured(true);
        return TLSRNode.CORRECTA;
    }

    /**
     * Este m�todo transforma el c�digo de error de configuraci�n del nodo en un
     * mensaje aclaratorio.
     *
     * @param e C�digo de error.
     * @return Texto explicativo del c�digo de error.
     * @since 2.0
     */
    @Override
    public String getErrorMessage(int e) {
        switch (e) {
            case SIN_NOMBRE:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigLSR.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigLSR.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TNodoLSR.NombreNoSoloEspacios"));
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
        String cadena = "#LSR#";
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
        cadena += this.getScreenPosition().x;
        cadena += "#";
        cadena += this.getScreenPosition().y;
        cadena += "#";
        cadena += this.switchingPowerInMbps;
        cadena += "#";
        cadena += this.getPorts().getBufferSizeInMBytes();
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
        this.setScreenPosition(new Point(posX + 24, posY + 24));
        this.switchingPowerInMbps = Integer.parseInt(valores[10]);
        this.getPorts().setBufferSizeInMB(Integer.parseInt(valores[11]));
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
        this.ports = new TFIFOPortSet(num, this);
    }

    /**
     * Este m�todo no hace nada en un LSR. En un nodo activoPermitir� solicitar
     * a un nodo activo la retransmisi�n de un paquete.
     *
     * @param paquete Paquete cuya retransmisi�n se est� solicitando.
     * @param pSalida Puerto por el que se enviar� la solicitud.
     * @since 2.0
     */
    @Override
    public void runGPSRP(TMPLSPDU paquete, int pSalida) {
    }

    /**
     * Esta constante indica que la configuraci�n del nodo es correcta.
     *
     * @since 2.0
     */
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que en la configuraci�n del nodo, falta el nombre.
     *
     * @since 2.0
     */
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que, en la configuraci�n del nodo, se ha elegido un
     * nombre que ya est� siendo usado.
     *
     * @since 2.0
     */
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que en la configuraci�n del nodo, el nombre elegido
     * es err�neo porque solo cuenta con espacios.
     *
     * @since 2.0
     */
    public static final int SOLO_ESPACIOS = 3;

    private TSwitchingMatrix switchingMatrix;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int switchingPowerInMbps;
    private TLSRStats stats;
}
