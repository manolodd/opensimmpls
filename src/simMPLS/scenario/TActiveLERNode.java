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
import simMPLS.protocols.TIPv4PDU;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TActivePortSet;
import simMPLS.hardware.ports.TActivePort;
import simMPLS.hardware.ports.TFIFOPort;
import simMPLS.hardware.dmgp.TDMGP;
import simMPLS.hardware.tldp.TSwitchingMatrix;
import simMPLS.hardware.tldp.TSwitchingMatrixEntry;
import simMPLS.hardware.dmgp.TGPSRPRequestsMatrix;
import simMPLS.hardware.dmgp.TGPSRPRequestEntry;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TPortSet;
import simMPLS.utils.EIDGeneratorOverflow;
import simMPLS.utils.TIDGenerator;
import simMPLS.utils.TLongIDGenerator;

/**
 * This class implements an active Label Edge Router (LER) node that will allow
 * network traffic to entry/exit to/from the MPLS domain.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TActiveLERNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TActiveLERNode.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param identifier the identifier of this active LER node that allow
     * referencing switchingMatrixIterator in the topology.
     * @param ipAddress The IPv4 address assigned to this active LER.
     * @param longIDGenerator The idntifier generator that the active LER will
     * use to identify unambiguosly each event switchingMatrixIterator
     * generates.
     * @param topology A reference to the topology this active LER belongs to.
     * @since 1.0
     */
    public TActiveLERNode(int identifier, String ipAddress, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(identifier, ipAddress, longIDGenerator, topology);
        // FIX: This is an overridable method call in constructor that should be 
        // avoided.
        this.setPorts(super.NUM_LERA_PORTS);
        this.switchingMatrix = new TSwitchingMatrix();
        this.gIdent = new TLongIDGenerator();
        this.gIdentLDP = new TIDGenerator();
        //FIX: replace with class constants.
        this.switchingPowerInMbps = 512;
        this.dmgp = new TDMGP();
        this.gpsrpRequests = new TGPSRPRequestsMatrix();
        this.stats = new TLERAStats();
    }

    /**
     * This method returns the size of the local DMGP (see the "Guarantee Of
     * Service Support Over MPLS Using Active Techniques" proposal) in KBytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the current size of DMGP in KBytes.
     * @since 1.0
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
     * @since 1.0
     */
    public void setDMGPSizeInKB(int sizeInKB) {
        this.dmgp.setDMGPSizeInKB(sizeInKB);
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to switch a single bit. This is something that depends on the
     * switching power of this active LER.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the number of nanoseconds that are needed to switch a single bit.
     * @since 1.0
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
     * depends on the switching power of this active LER.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param octects the number of octects that wants to be switched.
     * @return the number of nanoseconds that are needed to switch the specified
     * number of octects.
     * @since 1.0
     */
    public double getNsRequiredForAllOctets(int octects) {
        // FIX: replace al harcoded values for class constants
        double nsPerBit = this.getNsPerBit();
        long numberOfBits = (long) ((long) octects * (long) 8);
        return (double) ((double) nsPerBit * (long) numberOfBits);
    }

    /**
     * Este m�todo calcula el n�mero de bits que puede conmutar el nodo con el
     * n�mero de nanosegundos de que dispone actualmente.
     *
     * @return El n�mero de bits m�ximo que puede conmutar el nodo con los
     * nanosegundos de que dispone actualmente.
     * @since 1.0
     */
    public int getMaxSwitchableBitsWithCurrentNs() {
        double nsPerBit = getNsPerBit();
        double maxNumberOfBits = (double) ((double) availableNs / (double) nsPerBit);
        return (int) maxNumberOfBits;
    }

    /**
     * Este m�todo calcula el n�mero de octetos completos que puede transmitir
     * el nodo con el n�mero de nanosegundos de que dispone.
     *
     * @return El n�mero de octetos completos que puede transmitir el nodo en un
     * momento dado.
     * @since 1.0
     */
    public int getMaxSwitchableOctectsWithCurrentNs() {
        // FIX: replace al harcoded values for class constants
        double maxNumberOfOctects = ((double) getMaxSwitchableBitsWithCurrentNs() / (double) 8.0);
        return (int) maxNumberOfOctects;
    }

    /**
     * Este m�todo obtiene la potencia em Mbps con que est� configurado el nodo.
     *
     * @return La potencia de conmutaci�n del nodo en Mbps.
     * @since 1.0
     */
    public int getSwitchingPowerInMbps() {
        return this.switchingPowerInMbps;
    }

    /**
     * Este m�todo permite establecer la potencia de conmutaci�n del nodo en
     * Mbps.
     *
     * @param switchingPowerInMbps Potencia deseada para el nodo en Mbps.
     * @since 1.0
     */
    public void setSwitchingPowerInMbps(int switchingPowerInMbps) {
        this.switchingPowerInMbps = switchingPowerInMbps;
    }

    /**
     * Este m�todo obtiene el tama�o del buffer del nodo.
     *
     * @return Tama�o del buffer del nodo en MB.
     * @since 1.0
     */
    public int getBufferSizeInMBytes() {
        return this.getPorts().getBufferSizeInMB();
    }

    /**
     * Este m�todo permite establecer el tama�o del buffer del nodo.
     *
     * @param bufferSizeInMBytes Tama�o el buffer deseado para el nodo, en MB.
     * @since 1.0
     */
    public void setBufferSizeInMBytes(int bufferSizeInMBytes) {
        this.getPorts().setBufferSizeInMB(bufferSizeInMBytes);
    }

    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser
     * creados por el constructor.
     *
     * @since 1.0
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
        this.resetStepsWithoutEmittingToZero();
    }

    /**
     * Este m�todo indica el tipo de nodo de que se trata la instancia actual.
     *
     * @return LER. Indica que el nodo es de este tipo.
     * @since 1.0
     */
    @Override
    public int getNodeType() {
        return TNode.LERA;
    }

    /**
     * Este m�todo inicia el hilo de ejecuci�n del LER, para que entre en
     * funcionamiento. Adem�s controla el tiempo de que dispone el LER para
     * conmutar paquetes.
     *
     * @param timerEvent Evento de reloj que sincroniza la ejecuci�n de los
     * elementos de la topology.
     * @since 1.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent timerEvent) {
        this.setStepDouration(timerEvent.getStepDuration());
        this.setTimeInstant(timerEvent.getUpperLimit());
        if (this.getPorts().isThereAnyPacketToRoute()) {
            this.availableNs += timerEvent.getStepDuration();
        } else {
            this.resetStepsWithoutEmittingToZero();
            this.availableNs = timerEvent.getStepDuration();
        }
        this.startOperation();
    }

    /**
     * Llama a las acciones que se tienen que ejecutar en el transcurso del tic
     * de reloj que el LER estar� en funcionamiento.
     *
     * @since 1.0
     */
    @Override
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        try {
            this.generateSimulationEvent(new TSENodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), this.getPorts().getCongestionLevel()));
        } catch (Exception e) {
            // FIX: this is not a good practice. Avoid.
            e.printStackTrace();
        }
        checkConnectivityStatus();
        decreaseCounters();
        routePackets();
        this.stats.consolidateData(this.getAvailableTime());
    }

    /**
     * Este m�todo comprueba que haya conectividad con sus nodos adyacentes, es
     * decir, que no haya caido ning�n enlace. Si ha caido alg�n enlace,
     * entonces genera la correspondiente se�alizaci�n para notificar este
     * hecho.
     *
     * @since 1.0
     */
    public void checkConnectivityStatus() {
        boolean removeSwitchingMatrixEntry = false;
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        // FIX: Avoid using harcoded values
        int portIDAux = 0;
        TPort outgoingPort = null;
        TPort outgoingBackupPort = null;
        TPort incomingPort = null;
        TLink linkAux1 = null;
        TLink linkAux2 = null;
        this.switchingMatrix.getMonitor().lock();
        Iterator switchingMatrixIterator = this.switchingMatrix.getEntriesIterator();
        while (switchingMatrixIterator.hasNext()) {
            switchingMatrixEntry = (TSwitchingMatrixEntry) switchingMatrixIterator.next();
            if (switchingMatrixEntry != null) {
                portIDAux = switchingMatrixEntry.getBackupOutgoingPortID();
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    outgoingBackupPort = this.ports.getPort(portIDAux);
                    if (outgoingBackupPort != null) {
                        linkAux1 = outgoingBackupPort.getLink();
                        if (linkAux1 != null) {
                            if ((linkAux1.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                if (switchingMatrixEntry.backupLSPHasBeenEstablished() || switchingMatrixEntry.backupLSPShouldBeRemoved()) {
                                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                                    switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                                }
                            }
                        }
                    }
                }
                portIDAux = switchingMatrixEntry.getOutgoingPortID();
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    outgoingPort = this.ports.getPort(portIDAux);
                    if (outgoingPort != null) {
                        linkAux1 = outgoingPort.getLink();
                        if (linkAux1 != null) {
                            if ((linkAux1.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                incomingPort = this.ports.getPort(switchingMatrixEntry.getIncomingPortID());
                                linkAux1 = incomingPort.getLink();
                                if (linkAux1.getLinkType() == TLink.INTERNAL) {
                                    this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
                                } else {
                                    removeSwitchingMatrixEntry = true;
                                }
                            }
                        }
                    }
                }
                portIDAux = switchingMatrixEntry.getIncomingPortID();
                if ((portIDAux >= 0) && (portIDAux < this.ports.getNumberOfPorts())) {
                    incomingPort = this.ports.getPort(portIDAux);
                    if (incomingPort != null) {
                        linkAux1 = incomingPort.getLink();
                        if (linkAux1 != null) {
                            if ((linkAux1.isBroken()) && (switchingMatrixEntry.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                                linkAux1 = outgoingPort.getLink();
                                if (linkAux1.getLinkType() == TLink.INTERNAL) {
                                    this.sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
                                } else {
                                    removeSwitchingMatrixEntry = false;
                                }
                            }
                        }
                    }
                }
                if ((switchingMatrixEntry.getIncomingPortID() >= 0) && ((switchingMatrixEntry.getOutgoingPortID() >= 0))) {
                    linkAux1 = ports.getPort(switchingMatrixEntry.getIncomingPortID()).getLink();
                    linkAux2 = ports.getPort(switchingMatrixEntry.getOutgoingPortID()).getLink();
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
        this.gpsrpRequests.decreaseTimeout(this.obtenerDuracionTic());
        this.gpsrpRequests.updateEntries();
        int numberOfPorts = ports.getNumberOfPorts();
        int i = 0;
        TActivePort currentPort = null;
        TLink linkOfPort = null;
        for (i = 0; i < numberOfPorts; i++) {
            currentPort = (TActivePort) ports.getPort(i);
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
     * Este m�todo lee del puerto que corresponda seg�n el turno Round Robin
     * consecutivamente hasta que se termina el cr�dito. Si tiene posibilidad de
     * conmutar y/o encaminar un packet, lo hace, llamando para ello a los
     * m�todos correspondiente segun el packet. Si el packet est� mal formado o
     * es desconocido, lo descarta.
     *
     * @since 1.0
     */
    public void routePackets() {
        boolean atLeastOnePacketRouted = false;
        int readPort = 0;
        TAbstractPDU packet = null;
        int switchableOctectsWithCurrentNs = this.getMaxSwitchableOctectsWithCurrentNs();
        while (this.getPorts().canSwitchPacket(switchableOctectsWithCurrentNs)) {
            atLeastOnePacketRouted = true;
            packet = this.ports.getNextPacket();
            readPort = ports.getReadPort();
            if (packet != null) {
                if (packet.getType() == TAbstractPDU.IPV4) {
                    handleIPv4Packet((TIPv4PDU) packet, readPort);
                } else if (packet.getType() == TAbstractPDU.TLDP) {
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
        if (atLeastOnePacketRouted) {
            this.resetStepsWithoutEmittingToZero();
        } else {
            this.increaseStepsWithoutEmitting();
        }
    }

    /**
     * Este m�todo conmuta un packet GPSRP.
     *
     * @param packet Paquete GPSRP que conmutar.
     * @param incomingPortID Puerto por el que ha llegado el packet.
     * @since 1.0
     */
    public void handleGPSRPPacket(TGPSRPPDU packet, int incomingPortID) {
        if (packet != null) {
            int messageType = packet.getGPSRPPayload().getGPSRPMessageType();
            // FIX: flowID and packetID seems not to be used. If not necessary,
            // remove from the code.
            int flowID = packet.getGPSRPPayload().getFlowID();
            int packetID = packet.getGPSRPPayload().getPacketID();
            String targetIPv4Address = packet.getIPv4Header().getTargetIPv4Address();
            TFIFOPort outgoingPort = null;
            if (targetIPv4Address.equals(this.getIPAddress())) {
                if (messageType == TGPSRPPayload.RETRANSMISSION_REQUEST) {
                    this.handleGPSRPRetransmissionRequest(packet, incomingPortID);
                } else if (messageType == TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE) {
                    this.handleGPSRPRetransmissionNotPossible(packet, incomingPortID);
                } else if (messageType == TGPSRPPayload.RETRANSMISION_OK) {
                    this.handleGPSRPRetransmissionOk(packet, incomingPortID);
                }
            } else {
                String nextHopIPv4Address = this.topology.getNextHopRABANIPv4Address(this.getIPAddress(), targetIPv4Address);
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
     * Este m�todo atiende una solicitud GPSRP de retransmisi�n.
     *
     * @param packet Paquete GPSRP de petici�n de retransmisi�n.
     * @param incomingPortID Puerto por el que ha llegado el packet.
     * @since 1.0
     */
    public void handleGPSRPRetransmissionRequest(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetID = packet.getGPSRPPayload().getPacketID();
        TMPLSPDU wantedPacket = (TMPLSPDU) dmgp.getPacket(flowID, packetID);
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
     * Este m�todo atiende un packet GPSRP de denegaci�n de retransmisi�n.
     *
     * @param packet Paquete GPSRP.
     * @param incomingPort Puerto por el que ha llegado el packet GPSRP.
     * @since 1.0
     */
    public void handleGPSRPRetransmissionNotPossible(TGPSRPPDU packet, int incomingPort) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetID = packet.getGPSRPPayload().getPacketID();
        TGPSRPRequestEntry gpsrpRequestEntry = gpsrpRequests.getEntry(flowID, packetID);
        if (gpsrpRequestEntry != null) {
            gpsrpRequestEntry.forceTimeoutReset();
            int outgoingPortAux = gpsrpRequestEntry.getOutgoingPort();
            if (!gpsrpRequestEntry.isPurgeable()) {
                String targetIPv4Address = gpsrpRequestEntry.getCrossedNodeIPv4();
                if (targetIPv4Address != null) {
                    requestGPSRP(flowID, packetID, targetIPv4Address, outgoingPortAux);
                } else {
                    gpsrpRequests.removeEntry(flowID, packetID);
                }
            } else {
                gpsrpRequests.removeEntry(flowID, packetID);
            }
        }
    }

    /**
     * Este m�todo atiende un packet GPSRP de aceptaci�n de retransmisi�n.
     *
     * @param packet Paquete GPSRP de aceptaci�n de retransmisi�n.
     * @param incomingPortID Puerto por el que ha llegado el packet.
     * @since 1.0
     */
    public void handleGPSRPRetransmissionOk(TGPSRPPDU packet, int incomingPortID) {
        int flowID = packet.getGPSRPPayload().getFlowID();
        int packetID = packet.getGPSRPPayload().getPacketID();
        gpsrpRequests.removeEntry(flowID, packetID);
    }

    /**
     * Este m�todo solicita un retransmisi�n GPSRP.
     *
     * @param packet Paquete MPLS para el cual se solicita la retransmisi�n.
     * @param outgoingPortID Puerto por el cual debe salir la solicitud.
     * @since 1.0
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
                    gpsrpPacket = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), targetIPv4Address);
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
     * Este m�todo solicita un retransmisi�n GPSRP.
     *
     * @param flowID Identificador del flowID del cual se solicita
     * retransmisi�n.
     * @param packetID Identificaci�n del packet del flowID del que se desea
     * retransmisi�n.
     * @param targetIPv4Address IP del nodo al que se realizar� la solicitud.
     * @param outgoingPortID Puerto de salida por el que se debe encaminar la
     * solicitud.
     * @since 1.0
     */
    public void requestGPSRP(int flowID, int packetID, String targetIPv4Address, int outgoingPortID) {
        TActivePort outgoingPort = (TActivePort) ports.getPort(outgoingPortID);
        TGPSRPPDU gpsrpPacket = null;
        if (targetIPv4Address != null) {
            try {
                gpsrpPacket = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), targetIPv4Address);
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
     * Este m�todo deniega una retransmisi�n de paquetes.
     *
     * @param packet Paquete GPSRP de solicitud de retransmisi�n.
     * @param outgoingPortID Puerto por el que se debe enviar la denegaci�n.
     * @since 1.0
     */
    public void rejectGPSRP(TGPSRPPDU packet, int outgoingPortID) {
        TActivePort outgoingPort = (TActivePort) this.ports.getPort(outgoingPortID);
        if (outgoingPort != null) {
            TGPSRPPDU gpsrpPacket = null;
            try {
                gpsrpPacket = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), packet.getIPv4Header().getOriginIPAddress());
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
     * Este m�todo deniega una retransmisi�n de paquetes.
     *
     * @param packet Paquete GPSRP de solicitud de retransmisi�n.
     * @param outgoingPortID Puerto por el que se debe enviar la aceptaci�n.
     * @since 1.0
     */
    public void acceptGPSRP(TGPSRPPDU packet, int outgoingPortID) {
        TActivePort outgoingPort = (TActivePort) this.ports.getPort(outgoingPortID);
        if (outgoingPort != null) {
            TGPSRPPDU gpsrpPacket = null;
            try {
                gpsrpPacket = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), packet.getIPv4Header().getOriginIPAddress());
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
     * Este m�todo comprueba si existe una entrada en la tabla de encaminamiento
     * para el packet entrante. Si no es as�, clasifica el packet y, si es
     * necesario, reencola el packet y solicita una etiqueta para poder
     * enviarlo. Una vez que tiene entrada en la tabla de encaminamiento,
     * reenv�a el packet hacia el interior del dominio MPLS o hacia el exterior,
     * segun corresponda.
     *
     * @param packet Paquete IPv4 de entrada.
     * @param incomingPortID Puerto por el que ha accedido al nodo el packet.
     * @since 1.0
     */
    public void handleIPv4Packet(TIPv4PDU packet, int incomingPortID) {
        int fec = classifyPacket(packet);
        String targetIPv4Address = packet.getIPv4Header().getTargetIPv4Address();
        TSwitchingMatrixEntry switchingMatrixEntry = null;
        boolean requireBackupLSP = false;
        if ((packet.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)
                || (packet.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)
                || (packet.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)
                || (packet.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
            requireBackupLSP = true;
        }
        switchingMatrixEntry = this.switchingMatrix.getEntry(incomingPortID, fec, TSwitchingMatrixEntry.FEC_ENTRY);
        if (switchingMatrixEntry == null) {
            switchingMatrixEntry = createInitialEntryInFECMatrix(packet, incomingPortID);
            if (switchingMatrixEntry != null) {
                if (!isExitActiveLER(targetIPv4Address)) {
                    switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    requestTLDP(switchingMatrixEntry);
                }
                this.ports.getPort(incomingPortID).reEnqueuePacket(packet);
            }
        }
        if (switchingMatrixEntry != null) {
            int currentLabel = switchingMatrixEntry.getOutgoingLabel();
            if (currentLabel == TSwitchingMatrixEntry.UNDEFINED) {
                switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                requestTLDP(switchingMatrixEntry);
                this.ports.getPort(switchingMatrixEntry.getIncomingPortID()).reEnqueuePacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                this.ports.getPort(switchingMatrixEntry.getIncomingPortID()).reEnqueuePacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(packet);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(packet);
            // FIX: Avoid using hardcoded values. Use class constants instead.
            } else if ((currentLabel > 15) || (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operation = switchingMatrixEntry.getLabelStackOperation();
                if (operation == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(packet);
                } else {
                    if (operation == TSwitchingMatrixEntry.PUSH_LABEL) {
                        if (requireBackupLSP) {
                            requestTLDPForBackupLSP(switchingMatrixEntry);
                        }
                        TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                        TMPLSPDU mplsPacket = this.createMPLSPacket(packet, switchingMatrixEntry);
                        if (packet.getSubtype() == TAbstractPDU.IPV4_GOS) {
                            int expFieldAux = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                            TMPLSLabel mplsLabelAux = new TMPLSLabel();
                            // FIX: Avoid using hardcoded values. Use class 
                            // constants instead.
                            mplsLabelAux.setBoS(false);
                            mplsLabelAux.setEXP(expFieldAux);
                            mplsLabelAux.setLabel(1);
                            mplsLabelAux.setTTL(packet.getIPv4Header().getTTL());
                            mplsPacket.getLabelStack().pushTop(mplsLabelAux);
                            mplsPacket.setSubtype(TAbstractPDU.MPLS_GOS);
                            mplsPacket.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            this.dmgp.addPacket(mplsPacket);
                        }
                        outgoingPort.putPacketOnLink(mplsPacket, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                        } catch (Exception e) {
                            // FIX: Avoid this. This is not a good practice.
                            e.printStackTrace();
                        }
                    } else if (operation == TSwitchingMatrixEntry.POP_LABEL) {
                        discardPacket(packet);
                    } else if (operation == TSwitchingMatrixEntry.SWAP_LABEL) {
                        discardPacket(packet);
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
                }
            } else {
                discardPacket(packet);
            }
        } else {
            discardPacket(packet);
        }
    }

    /**
     * Este m�todo se llama cuando se recibe un packet TLDP con informaci�n
     * sobre las etiquetas a use. El m�todo realiza sobre las matriz de
     * encaminamiento la operaci�n que sea necesario y propaga el cambio al nodo
     * adyacente que corresponda.
     *
     * @param packet Paquete TLDP recibido.
     * @param incomingPortID Puerto por el que se ha recibido el packet TLDP.
     * @since 1.0
     */
    public void handleTLDPPacket(TTLDPPDU packet, int incomingPortID) {
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
     * Este m�todo comprueba si existe una entrada en la tabla de encaminamiento
     * para el packet entrante. Si no es as�, clasifica el packet y, si es
     * necesario, reencola el packet y solicita una etiqueta para poder
     * enviarlo. Una vez que tiene entrada en la tabla de encaminamiento,
     * reenv�a el packet hacia el siguiente nodo del dominio MPLS o hacia el
     * exterior, segun corresponda.
     *
     * @param packet Paquete MPLS recibido.
     * @param incomingPortID Puerto por el que ha llegado el packet MPLS recibido.
     * @since 1.0
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
        String targetIPv4Address = packet.getIPv4Header().getTargetIPv4Address();
        switchingMatrixEntry = this.switchingMatrix.getEntry(incomingPortID, labelValue, TSwitchingMatrixEntry.LABEL_ENTRY);
        if (switchingMatrixEntry == null) {
            switchingMatrixEntry = createInitialEntryInILMMatrix(packet, incomingPortID);
            if (switchingMatrixEntry != null) {
                if (!isExitActiveLER(targetIPv4Address)) {
                    switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    requestTLDP(switchingMatrixEntry);
                }
            }
        }
        if (switchingMatrixEntry != null) {
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
                if (operation == TSwitchingMatrixEntry.UNDEFINED) {
                    if (isLabeled) {
                        packet.getLabelStack().pushTop(mplsLabel);
                    }
                    discardPacket(packet);
                } else {
                    if (operation == TSwitchingMatrixEntry.PUSH_LABEL) {
                        TMPLSLabel mplsLabelAux = new TMPLSLabel();
                        // FIX: Do not use hardcoded values. Use class constants 
                        // instead.
                        mplsLabelAux.setBoS(false);
                        mplsLabelAux.setEXP(0);
                        mplsLabelAux.setLabel(switchingMatrixEntry.getOutgoingLabel());
                        mplsLabelAux.setTTL(packet.getLabelStack().getTop().getTTL() - 1);
                        if (requireBackupLSP) {
                            requestTLDPForBackupLSP(switchingMatrixEntry);
                        }
                        packet.getLabelStack().pushTop(mplsLabelAux);
                        if (isLabeled) {
                            packet.getLabelStack().pushTop(mplsLabel);
                        }
                        TPort outgoingPort = this.ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                        if (isLabeled) {
                            packet.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            this.dmgp.addPacket(packet);
                        }
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
                            TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                            outgoingPort.putPacketOnLink(ipv4Packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        } else {
                            packet.getLabelStack().popTop();
                            if (isLabeled) {
                                packet.getLabelStack().pushTop(mplsLabel);
                            }
                            TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                            outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        }
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
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
                        }
                        TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                        if (isLabeled) {
                            packet.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            dmgp.addPacket(packet);
                        }
                        outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                        } catch (Exception e) {
                            // FIX: This is not a good practice. Avoid.
                            e.printStackTrace();
                        }
                    } else if (operation == TSwitchingMatrixEntry.NOOP) {
                        TPort outgoingPort = ports.getPort(switchingMatrixEntry.getOutgoingPortID());
                        outgoingPort.putPacketOnLink(packet, outgoingPort.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                        } catch (Exception e) {
                            // FIX: This is not a good practice. Avoid.
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (isLabeled) {
                    packet.getLabelStack().pushTop(mplsLabel);
                }
                discardPacket(packet);
            }
        } else {
            if (isLabeled) {
                packet.getLabelStack().pushTop(mplsLabel);
            }
            discardPacket(packet);
        }
    }

    /**
     * Este m�todo trata una petici�n de etiquetas.
     *
     * @param packet Petici�n de etiquetas recibida de otro nodo.
     * @param incomingPortID Puerto de entrada de la petici�n de etiqueta.
     * @since 1.0
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
                // Do nothing. The Active LER is waiting for a label
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                sendTLDPRequestRefuse(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                sendTLDPRequestOk(switchingMatrixEntry);
            } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                sendTLDPWithdrawal(switchingMatrixEntry, incomingPortID);
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
     * Este m�todo trata un packet TLDP de eliminaci�n de etiqueta.
     *
     * @param packet Eliminaci�n de etiqueta recibida.
     * @param incomingPortID Puerto por el que se recibi�n la eliminaci�n de etiqueta.
     * @since 1.0
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
            if (switchingMatrixEntry.getIncomingPortID() == incomingPortID) {
                if (switchingMatrixEntry.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
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
                    } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                        sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                        this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                    } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                        sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                    // FIX: Avoid using harcoded values. Use class constants 
                    // instead.
                    } else if (currentLabel > 15) {
                        switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                        sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOppositePortID(incomingPortID));
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
                } else {
                    sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                    this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
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
                } else if (currentLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                    this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                } else if (currentLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
                } else if (currentLabel > 15) {
                    if (switchingMatrixEntry.backupLSPHasBeenEstablished()) {
                        sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                        // FIX: Avoid using harcoded values. Use class constants 
                        // instead.
                        if (switchingMatrixEntry.getBackupOutgoingPortID() >= 0) {
                            TInternalLink internalLinkAux = (TInternalLink) ports.getPort(switchingMatrixEntry.getBackupOutgoingPortID()).getLink();
                            internalLinkAux.setLSPUp();
                            internalLinkAux.setBackupLSPDown();
                        }
                        switchingMatrixEntry.switchToBackupLSP();
                    } else {
                        if (switchingMatrixEntry.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                            switchingMatrixEntry.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                            sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                            sendTLDPWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
                        } else {
                            sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                            this.switchingMatrix.removeEntry(switchingMatrixEntry.getIncomingPortID(), switchingMatrixEntry.getLabelOrFEC(), switchingMatrixEntry.getEntryType());
                        }
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
                } else if (currentBackupLabel == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    // Do nothing.
                } else if (currentBackupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                // FIX: Avoid using harcoded values. Use class constants 
                // instead.
                } else if (currentBackupLabel > 15) {
                    sendTLDPWithdrawalOk(switchingMatrixEntry, incomingPortID);
                    switchingMatrixEntry.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    switchingMatrixEntry.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else {
                    discardPacket(packet);
                }
            }
        }
    }

    /**
     * Este m�todo trata un packet TLDP de confirmaci�n de etiqueta.
     *
     * @param paquete Confirmaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de
     * etiquetas.
     * @since 1.0
     */
    public void handleTLDPRequestOk(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getOutgoingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(paquete.getTLDPPayload().getLabel());
                    if (emc.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                        emc.setLabelOrFEC(switchingMatrix.getNewLabel());
                    }
                    TInternalLink et = (TInternalLink) ports.getPort(pEntrada).getLink();
                    if (et != null) {
                        if (emc.aBackupLSPHasBeenRequested()) {
                            et.ponerLSPDeBackup();
                        } else {
                            et.setLSPUp();
                        }
                    }
                    sendTLDPRequestOk(emc);
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
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setBackupOutgoingLabel(paquete.getTLDPPayload().getLabel());
                    if (emc.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                        emc.setLabelOrFEC(switchingMatrix.getNewLabel());
                    }
                    TInternalLink et = (TInternalLink) ports.getPort(pEntrada).getLink();
                    et.ponerLSPDeBackup();
                    sendTLDPRequestOk(emc);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            }
        }
    }

    /**
     * Este m�todo trata un packet TLDP de denegaci�n de etiqueta.
     *
     * @param paquete Paquete de denegaci�n de etiquetas recibido.
     * @param pEntrada Puerto por el que se ha recibido la denegaci�n de
     * etiquetas.
     * @since 1.0
     */
    public void handleTLDPRefuseRequest(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getOutgoingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                    sendTLDPRequestRefuse(emc);
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
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                    sendTLDPRequestRefuse(emc);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            }
        }
    }

    /**
     * Este m�todo trata un packet TLDP de confirmaci�n de eliminaci�n de
     * etiqueta.
     *
     * @param paquete Paquete de confirmaci�n e eliminaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de
     * eliminaci�n de etiqueta.
     * @since 1.0
     */
    public void handleTLDPWithdrawalOk(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.getLocalOrigin() == TTLDPPDU.CAME_BY_ENTRANCE) {
            emc = switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        } else {
            emc = switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getIncomingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if ((etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL)
                        || (etiquetaActual == TSwitchingMatrixEntry.LABEL_WITHDRAWN)) {
                    if (emc.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                        if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                            TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                            if (pSalida != null) {
                                TLink et = pSalida.getLink();
                                if (et.getLinkType() == TLink.INTERNAL) {
                                    TInternalLink ei = (TInternalLink) et;
                                    if (emc.aBackupLSPHasBeenRequested()) {
                                        ei.setBackupLSPDown();
                                    } else {
                                        ei.quitarLSP();
                                    }
                                }
                            }
                            emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                        }
                    }
                    switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                    if (emc.getBackupOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                        if (emc.getBackupOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                            if (emc.getBackupOutgoingPortID() >= 0) {
                                TPort pSalida = ports.getPort(emc.getBackupOutgoingPortID());
                                if (pSalida != null) {
                                    TLink et = pSalida.getLink();
                                    if (et.getLinkType() == TLink.INTERNAL) {
                                        TInternalLink ei = (TInternalLink) et;
                                        ei.setBackupLSPDown();
                                    }
                                }
                            }
                            emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                        }
                    }
                    if (emc.getIncomingPortID() != TSwitchingMatrixEntry.UNDEFINED) {
                        TPort pSalida = ports.getPort(pEntrada);
                        if (pSalida != null) {
                            TLink et = pSalida.getLink();
                            if (et.getLinkType() == TLink.INTERNAL) {
                                TInternalLink ei = (TInternalLink) et;
                                if (emc.aBackupLSPHasBeenRequested()) {
                                    ei.setBackupLSPDown();
                                } else {
                                    ei.quitarLSP();
                                }
                            }
                        }
                    }
                    switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                } else if (etiquetaActual > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getOutgoingPortID() == pEntrada) {
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
                    TLink et = pSalida.getLink();
                    if (et.getLinkType() == TLink.INTERNAL) {
                        TInternalLink ei = (TInternalLink) et;
                        if (emc.aBackupLSPHasBeenRequested()) {
                            ei.setBackupLSPDown();
                        } else {
                            ei.quitarLSP();
                        }
                    }
                    if ((emc.getBackupOutgoingLabel() == TSwitchingMatrixEntry.LABEL_WITHDRAWN)
                            || (emc.getBackupOutgoingLabel() == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                        switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                    }
                    switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                } else if (etiquetaActual > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
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
                    TLink et = pSalida.getLink();
                    if (et.getLinkType() == TLink.INTERNAL) {
                        TInternalLink ei = (TInternalLink) et;
                        if (emc.aBackupLSPHasBeenRequested()) {
                            ei.setBackupLSPDown();
                        } else {
                            ei.quitarLSP();
                        }
                    }
                    if ((emc.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_WITHDRAWN)
                            || (emc.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                        switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                    }
                    switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                } else if (etiquetaActual > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            }
        }
    }

    /**
     * Este m�todo env�a una etiqueta al nodo que indique la entrada en la
     * matriz de conmutaci�n especificada.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void sendTLDPRequestOk(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPAddress();
                String IPDestino = ports.getIPOfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_OK);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPAddress());
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
     * @since 1.0
     */
    public void sendTLDPRequestRefuse(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPAddress();
                String IPDestino = ports.getIPOfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_DENIED);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPAddress());
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
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la confirmaci�n de
     * eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void sendTLDPWithdrawalOk(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            String IPLocal = this.getIPAddress();
            String IPDestino = ports.getIPOfNodeLinkedTo(puerto);
            if (IPDestino != null) {
                TTLDPPDU nuevoTLDP = null;
                try {
                    nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nuevoTLDP != null) {
                    nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REVOMAL_REQUEST_OK);
                    nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPAddress());
                    nuevoTLDP.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                    if (emc.getOutgoingPortID() == puerto) {
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getBackupOutgoingPortID() == puerto) {
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
     * @since 1.0
     */
    public void requestTLDP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        if (emc.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
            String IPSalto = topology.getNextHopRABANIPv4Address(IPLocal, IPDestinoFinal);
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
                    if (emc.aBackupLSPHasBeenRequested()) {
                        paqueteTLDP.setLSPType(true);
                    } else {
                        paqueteTLDP.setLSPType(false);
                    }
                    paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort pSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
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
     * Este m�todo solicita una etiqueta al nodo que se especifica en la entrada
     * de la matriz de conmutaci�n correspondiente. La solicitud ir� destinada a
     * crear un LSP de backup.
     *
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void requestTLDPForBackupLSP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        String IPSaltoPrincipal = ports.getIPOfNodeLinkedTo(emc.getOutgoingPortID());
        if (IPSaltoPrincipal != null) {
            String IPSalto = topology.obtenerIPSaltoRABAN(IPLocal, IPDestinoFinal, IPSaltoPrincipal);
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
                                    TPort pSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
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
    }

    /**
     * Este m�todo env�a una eliminaci�n de etiqueta al nodo especificado por le
     * entrada de la matriz de conmutaci�n correspondiente.
     *
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     */
    public void sendTLDPWithdrawal(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
            String IPLocal = this.getIPAddress();
            String IPDestinoFinal = emc.getTailEndIPAddress();
            String IPSalto = ports.getIPOfNodeLinkedTo(puerto);
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

    /**
     * Este m�todo reenv�a todas las peticiones pendientes de contestaci�n de
     * una entrada de la matriz de conmutaci�n.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void solicitarTLDPTrasTimeout(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            String IPLocal = this.getIPAddress();
            String IPDestinoFinal = emc.getTailEndIPAddress();
            String IPSalto = ports.getIPOfNodeLinkedTo(emc.getOutgoingPortID());
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
                    if (emc.aBackupLSPHasBeenRequested()) {
                        paqueteTLDP.setLSPType(true);
                    } else {
                        paqueteTLDP.setLSPType(false);
                    }
                    paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort pSalida = ports.getPort(emc.getOutgoingPortID());
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
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry emc, int puerto) {
        sendTLDPWithdrawal(emc, puerto);
    }

    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de
     * una entrada de la matriz de conmutaci�n a todos los ports necesarios.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry emc) {
        sendTLDPWithdrawal(emc, emc.getIncomingPortID());
        sendTLDPWithdrawal(emc, emc.getOutgoingPortID());
        sendTLDPWithdrawal(emc, emc.getBackupOutgoingPortID());
    }

    /**
     * Este m�todo decrementa los contadores de retransmisi�n existentes para
     * este nodo.
     *
     * @since 1.0
     */
    public void decreaseCounters() {
        TSwitchingMatrixEntry emc = null;
        this.switchingMatrix.getMonitor().lock();
        Iterator it = this.switchingMatrix.getEntriesIterator();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                emc.decreaseTimeOut(this.obtenerDuracionTic());
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
                    } else {
                        if (!emc.areThereAvailableAttempts()) {
                            it.remove();
                        }
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
     * @since 1.0
     */
    public TSwitchingMatrixEntry createEntryFromTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.getTLDPPayload().getTLDPIdentifier();
        TPort puertoEntrada = ports.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.getTLDPPayload().getTargetIPAddress();
        String IPSalto = topology.getNextHopRABANIPv4Address(this.getIPAddress(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
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
            if (isExitActiveLER(IPDestinoFinal)) {
                emc.setLabelOrFEC(switchingMatrix.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
                emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            switchingMatrix.addEntry(emc);
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
     * @since 1.0
     */
    public TSwitchingMatrixEntry createInitialEntryInFECMatrix(TIPv4PDU paqueteIPv4, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteIPv4.getIPv4Header().getTargetIPv4Address();
        String IPSalida = topology.getNextHopRABANIPv4Address(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = ports.getPort(pEntrada);
            TPort puertoSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalida);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setLabelOrFEC(classifyPacket(paqueteIPv4));
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
            if (isExitActiveLER(IPDestinoFinal)) {
                emc.setLabelOrFEC(switchingMatrix.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
                emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            switchingMatrix.addEntry(emc);
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
     * @since 1.0
     */
    public TSwitchingMatrixEntry createInitialEntryInILMMatrix(TMPLSPDU paqueteMPLS, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteMPLS.getIPv4Header().getTargetIPv4Address();
        String IPSalida = topology.getNextHopRABANIPv4Address(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = ports.getPort(pEntrada);
            TPort puertoSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalida);
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
            if (isExitActiveLER(IPDestinoFinal)) {
                emc.setLabelOrFEC(switchingMatrix.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
                emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            switchingMatrix.addEntry(emc);
        }
        return emc;
    }

    /**
     * Este m�todo toma un packet IPv4 y la entrada de la matriz de conmutaci�n
     * asociada al mismo y crea un packet MPLS etiquetado correctamente que
     * contiene dicho packet IPv4 listo para ser transmitido hacia el interior
     * del dominio.
     *
     * @param ipv4Packet Paquete IPv4 que se debe etiquetar.
     * @param switchingMatrixEntry Entrada de la matriz de conmutaci�n asociada
     * al packet IPv4 que se desea etiquetar.
     * @return El packet IPv4 de entrada, convertido en un packet MPLS
     * correctamente etiquetado.
     * @since 1.0
     */
    public TMPLSPDU createMPLSPacket(TIPv4PDU ipv4Packet, TSwitchingMatrixEntry switchingMatrixEntry) {
        TMPLSPDU mplsPacket = null;
        try {
            mplsPacket = new TMPLSPDU(gIdent.getNextID(), ipv4Packet.getIPv4Header().getOriginIPAddress(), ipv4Packet.getIPv4Header().getTargetIPv4Address(), ipv4Packet.getSize());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        mplsPacket.setHeader(ipv4Packet.getIPv4Header());
        mplsPacket.setTCPPayload(ipv4Packet.getTCPPayload());
        if (ipv4Packet.getSubtype() == TAbstractPDU.IPV4) {
            mplsPacket.setSubtype(TAbstractPDU.MPLS);
        } else if (ipv4Packet.getSubtype() == TAbstractPDU.IPV4_GOS) {
            mplsPacket.setSubtype(TAbstractPDU.MPLS_GOS);
        }
        TMPLSLabel mplsLabel = new TMPLSLabel();
        // FIX: all harcoded values should be changed by class constants.
        mplsLabel.setBoS(true);
        mplsLabel.setEXP(0);
        mplsLabel.setLabel(switchingMatrixEntry.getOutgoingLabel());
        mplsLabel.setTTL(ipv4Packet.getIPv4Header().getTTL() - 1);
        mplsPacket.getLabelStack().pushTop(mplsLabel);
        ipv4Packet = null;
        try {
            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), mplsPacket.getSubtype(), mplsPacket.getSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mplsPacket;
    }

    /**
     * Este m�todo toma como par�metro un packet MPLS y su entrada en la matriz
     * de conmutaci�n asociada. Extrae del packet MPLS el packet IP
     * correspondiente y actualiza sus elementFields correctamente.
     *
     * @param MPLSPacket Paquete MPLS cuyo contenido de nivel IPv4 se desea
     * extraer.
     * @param switchingMatrixEntry Entrada de la matriz de conmutaci�n asociada
     * al packet MPLS.
     * @return Paquete IPv4 que corresponde al packet MPLS una vez que se ha
     * eliminado toda la informaci�n MLPS; que se ha desetiquetado.
     * @since 1.0
     */
    public TIPv4PDU createIPv4Packet(TMPLSPDU MPLSPacket, TSwitchingMatrixEntry switchingMatrixEntry) {
        TIPv4PDU ipv4Packet = null;
        try {
            ipv4Packet = new TIPv4PDU(gIdent.getNextID(), MPLSPacket.getIPv4Header().getOriginIPAddress(), MPLSPacket.getIPv4Header().getTargetIPv4Address(), MPLSPacket.getTCPPayload().getSize());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        ipv4Packet.setHeader(MPLSPacket.getIPv4Header());
        ipv4Packet.setTCPPayload(MPLSPacket.getTCPPayload());
        ipv4Packet.getIPv4Header().setTTL(MPLSPacket.getLabelStack().getTop().getTTL());
        if (MPLSPacket.getSubtype() == TAbstractPDU.MPLS) {
            ipv4Packet.setSubtype(TAbstractPDU.IPV4);
        } else if (MPLSPacket.getSubtype() == TAbstractPDU.MPLS_GOS) {
            ipv4Packet.setSubtype(TAbstractPDU.IPV4_GOS);
        }
        try {
            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), ipv4Packet.getSubtype(), ipv4Packet.getSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MPLSPacket = null;
        return ipv4Packet;
    }

    /**
     * Este m�todo comprueba si un packet recibido es un packet del interior del
     * dominio MPLS o es un packet externo al mismo.
     *
     * @param packet Paquete que ha llegado al nodo.
     * @param entryPortID Puerto por el que ha llegado el packet al nodo.
     * @return true, si el packet es exterior al dominio MPLS. false en caso
     * contrario.
     * @since 1.0
     */
    public boolean isAnExternalPacket(TAbstractPDU packet, int entryPortID) {
        if (packet.getType() == TAbstractPDU.IPV4) {
            return true;
        }
        TPort entryPort = ports.getPort(entryPortID);
        return entryPort.getLink().getLinkType() == TLink.EXTERNAL;
    }

    /**
     * Este m�todo descarta un packet en el nodo y refleja dicho descarte en las
     * estad�sticas del nodo.
     *
     * @param packet Paquete que se quiere descartar.
     * @since 1.0
     */
    @Override
    public void discardPacket(TAbstractPDU packet) {
        try {
            this.generateSimulationEvent(new TSEPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
            this.stats.addStatsEntry(packet, TStats.DISCARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        packet = null;
    }

    /**
     * Este m�todo toma como parametro un packet, supuestamente sin etiquetar, y
     * lo clasifica. Esto significa que determina el FEC_ENTRY al que pertenece
     * el packet. Este valor se calcula como el c�digo HASH practicado a la
     * concatenaci�n de la IP de origen y la IP de destino. En la pr�ctica esto
     * significa que paquetes con el mismo origen y con el mismo destino
     * pertenecer�n al mismo FEC_ENTRY.
     *
     * @param packet El packet que se desea clasificar.
     * @return El FEC_ENTRY al que pertenece el packet pasado por par�metros.
     * @since 1.0
     */
    public int classifyPacket(TAbstractPDU packet) {
        String originIPAddress = packet.getIPv4Header().getOriginIPAddress();
        String targetIPAddress = packet.getIPv4Header().getTargetIPv4Address();
        String FECString = originIPAddress + targetIPAddress;
        // FIX: hashCode() does not have a constistent behaviour between
        // different executions; should be changed and use a persistent 
        // mechanism.
        return FECString.hashCode();
    }

    /**
     * Este m�todo permite el acceso al conjunto de ports del nodo.
     *
     * @return El conjunto de ports del nodo.
     * @since 1.0
     */
    @Override
    public TPortSet getPorts() {
        return this.ports;
    }

    /**
     * Este m�todo calcula si el nodo tiene ports libres o no.
     *
     * @return true, si el nodo tiene ports libres. false en caso contrario.
     * @since 1.0
     */
    @Override
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }

    /**
     * Este m�todo calcula el routingWeight del nodo. Se utilizar� para calcular
     * rutas con costo menor. En el nodo LER el pero ser� siempre nulo (cero).
     *
     * @return 0. El routingWeight del LERA.
     * @since 1.0
     */
    @Override
    public long getRoutingWeight() {
        // FIX: All harcoded values should be defined as class constants.
        long congestionWeightComponent = (long) (this.ports.getCongestionLevel() * (0.7));
        long switchingMatrixWeightComponent = (long) ((10 * this.switchingMatrix.getNumberOfEntries()) * (0.3));
        long routingWeight = congestionWeightComponent + switchingMatrixWeightComponent;
        return routingWeight;
    }

    /**
     * Este m�todo comprueba si la isntancia actual es el LER de salida del
     * dominio MPLS para una IP dada.
     *
     * @param targetIPAddress IP de destino del tr�fico, para la cual queremos
     * averiguar si el LER es nodo de salida.
     * @return true, si el LER es de salida del dominio para tr�fico dirigido a
     * esa IP. false en caso contrario.
     * @since 1.0
     */
    public boolean isExitActiveLER(String targetIPAddress) {
        TPort portAux = ports.getLocalPortConnectedToANodeWithIPAddress(targetIPAddress);
        if (portAux != null) {
            if (portAux.getLink().getLinkType() == TLink.EXTERNAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo permite el acceso a la matriz de conmutaci�n de LER.
     *
     * @return La matriz de conmutaci�n del LER.
     * @since 1.0
     */
    public TSwitchingMatrix getSwitchingMatrix() {
        return switchingMatrix;
    }

    /**
     * Este m�todo comprueba que la configuraci�n de LER sea la correcta.
     *
     * @return true, si el LER est� bien configurado. false en caso contrario.
     * @since 1.0
     */
    @Override
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }

    /**
     * Este m�todo comprueba que una cierta configuraci�n es v�lida.
     *
     * @param topology Topolog�a a la que pertenece el LER.
     * @param reconfiguration true si se trata de una reconfiguraci�n. false en
     * caso contrario.
     * @return CORRECTA, si la configuraci�n es correcta. Un c�digo de error en
     * caso contrario.
     * @since 1.0
     */
    @Override
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TActiveLERNode.UNNAMED;
        }
        boolean onlyBlankSpaces = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                onlyBlankSpaces = false;
            }
        }
        if (onlyBlankSpaces) {
            return TActiveLERNode.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TNode nodeAux = topology.setFirstNodeNamed(this.getName());
            if (nodeAux != null) {
                return TActiveLERNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode nodeAux = topology.setFirstNodeNamed(this.getName());
            if (nodeAux != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return TActiveLERNode.NAME_ALREADY_EXISTS;
                }
            }
        }
        this.setWellConfigured(true);
        return TActiveLERNode.OK;
    }

    /**
     * Este m�todo toma un codigo de error y genera un messageType textual del
     * mismo.
     *
     * @param errorCode El c�digo de error para el cual queremos una explicaci�n
     * textual.
     * @return Cadena de texto explicando el error.
     * @since 1.0
     */
    @Override
    public String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case TActiveLERNode.UNNAMED:
                return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLER.FALTA_NOMBRE"));
            case TActiveLERNode.NAME_ALREADY_EXISTS:
                return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLER.NOMBRE_REPETIDO"));
            case TActiveLERNode.ONLY_BLANK_SPACES:
                return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoLER.NombreNoSoloEspacios"));
        }
        return ("");
    }

    /**
     * Este m�todo forma una serializedElement de texto que representa al LER y
     * toda su configuraci�n. Sirve para almacenar el LER en disco.
     *
     * @return Una serializedElement de texto que representa un a este LER.
     * @since 1.0
     */
    @Override
    public String marshall() {
        // FIX: all harcoded values should be coded as class constants.
        String serializedElement = "#LERA#";
        serializedElement += this.getID();
        serializedElement += "#";
        serializedElement += this.getName().replace('#', ' ');
        serializedElement += "#";
        serializedElement += this.getIPAddress();
        serializedElement += "#";
        serializedElement += this.getStatus();
        serializedElement += "#";
        serializedElement += this.getShowName();
        serializedElement += "#";
        serializedElement += this.isGeneratingStats();
        serializedElement += "#";
        serializedElement += this.obtenerPosicion().x;
        serializedElement += "#";
        serializedElement += this.obtenerPosicion().y;
        serializedElement += "#";
        serializedElement += this.switchingPowerInMbps;
        serializedElement += "#";
        serializedElement += this.getPorts().getBufferSizeInMB();
        serializedElement += "#";
        serializedElement += this.dmgp.getDMGPSizeInKB();
        serializedElement += "#";
        return serializedElement;
    }

    /**
     * Este m�todo toma como par�metro una serializedElement de texto que debe
     * pertencer a un LER serializado y configura esta instancia con los
     * elementFields de dicha caddena.
     *
     * @param serializedElement LER serializado.
     * @return true, si no ha habido errores y la instancia actual est� bien
     * configurada. false en caso contrario.
     * @since 1.0
     */
    @Override
    public boolean unMarshall(String serializedElement) {
        // FIX: All numeric values in this method should be implemented as class
        // constants instead of harcodeed values.
        String[] elementFields = serializedElement.split("#");
        if (elementFields.length != 13) {
            return false;
        }
        this.setID(Integer.parseInt(elementFields[2]));
        this.setName(elementFields[3]);
        this.setIPAddress(elementFields[4]);
        this.setStatus(Integer.parseInt(elementFields[5]));
        this.setShowName(Boolean.parseBoolean(elementFields[6]));
        this.setGenerateStats(Boolean.parseBoolean(elementFields[7]));
        int coordX = Integer.parseInt(elementFields[8]);
        int coordY = Integer.parseInt(elementFields[9]);
        this.setPosition(new Point(coordX + 24, coordY + 24));
        this.switchingPowerInMbps = Integer.parseInt(elementFields[10]);
        this.getPorts().setBufferSizeInMB(Integer.parseInt(elementFields[11]));
        this.dmgp.setDMGPSizeInKB(Integer.parseInt(elementFields[12]));
        return true;
    }

    /**
     * Este m�todo permite acceder directamente a las estadisticas del nodo.
     *
     * @return Las estad�sticas del nodo.
     * @since 1.0
     */
    @Override
    public TStats getStats() {
        return this.stats;
    }

    /**
     * Este m�todo permite establecer el n�mero de ports que tendr� el nodo.
     *
     * @param numPorts N�mero de ports deseado para el nodo. Como mucho, 8
     * ports.
     * @since 1.0
     */
    @Override
    public synchronized void setPorts(int numPorts) {
        this.ports = new TActivePortSet(numPorts, this);
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
    private int switchingPowerInMbps;
    private TDMGP dmgp;
    private TGPSRPRequestsMatrix gpsrpRequests;
    private TLERAStats stats;
}
