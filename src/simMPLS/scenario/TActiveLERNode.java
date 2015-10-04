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
 referencing switchingMatrixIterator in the topology.
     * @param ipAddress The IPv4 address assigned to this active LER.
     * @param longIDGenerator The idntifier generator that the active LER will
 use to identify unambiguosly each event switchingMatrixIterator generates.
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
     * @param evt Evento de reloj que sincroniza la ejecuci�n de los elementos
     * de la topology.
     * @since 1.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent evt) {
        this.setStepDouration(evt.getStepDuration());
        this.setTimeInstant(evt.getUpperLimit());
        if (this.getPorts().isThereAnyPacketToRoute()) {
            this.availableNs += evt.getStepDuration();
        } else {
            this.resetStepsWithoutEmittingToZero();
            this.availableNs = evt.getStepDuration();
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
                                    this.labelWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getIncomingPortID());
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
                                    this.labelWithdrawal(switchingMatrixEntry, switchingMatrixEntry.getOutgoingPortID());
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
                targetIPv4Address = gpsrpRequestEntry.getCrossedNodeIP();
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
 conmutar y/o encaminar un packet, lo hace, llamando para ello a los
 m�todos correspondiente segun el packet. Si el packet est� mal formado
 o es desconocido, lo descarta.
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
     * @param incomingPort Puerto por el que ha llegado el packet.
     * @since 1.0
     */
    public void handleGPSRPPacket(TGPSRPPDU packet, int incomingPort) {
        if (packet != null) {
            int mensaje = packet.getGPSRPPayload().getGPSRPMessageType();
            int flujo = packet.getGPSRPPayload().getFlowID();
            int idPaquete = packet.getGPSRPPayload().getPacketID();
            String IPDestinoFinal = packet.getIPv4Header().getTargetIPAddress();
            TFIFOPort pSalida = null;
            if (IPDestinoFinal.equals(this.getIPAddress())) {
                if (mensaje == TGPSRPPayload.RETRANSMISSION_REQUEST) {
                    this.atenderPeticionGPSRP(packet, incomingPort);
                } else if (mensaje == TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE) {
                    this.atenderDenegacionGPSRP(packet, incomingPort);
                } else if (mensaje == TGPSRPPayload.RETRANSMISION_OK) {
                    this.atenderAceptacionGPSRP(packet, incomingPort);
                }
            } else {
                String IPSalida = this.topology.obtenerIPSaltoRABAN(this.getIPAddress(), IPDestinoFinal);
                pSalida = (TFIFOPort) this.ports.getPortConnectedToANodeWithIPAddress(IPSalida);
                if (pSalida != null) {
                    pSalida.putPacketOnLink(packet, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
                    } catch (Exception e) {
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
     * @param paquete Paquete GPSRP de petici�n de retransmisi�n.
     * @param pEntrada Puerto por el que ha llegado el packet.
     * @since 1.0
     */
    public void atenderPeticionGPSRP(TGPSRPPDU paquete, int pEntrada) {
        int idFlujo = paquete.getGPSRPPayload().getFlowID();
        int idPaquete = paquete.getGPSRPPayload().getPacketID();
        TMPLSPDU paqueteBuscado = (TMPLSPDU) dmgp.getPacket(idFlujo, idPaquete);
        if (paqueteBuscado != null) {
            this.aceptarGPSRP(paquete, pEntrada);
            TActivePort puertoSalida = (TActivePort) this.ports.getPort(pEntrada);
            if (puertoSalida != null) {
                puertoSalida.putPacketOnLink(paqueteBuscado, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                try {
                    this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteBuscado.getSubtype()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.denegarGPSRP(paquete, pEntrada);
        }
    }

    /**
     * Este m�todo atiende un packet GPSRP de denegaci�n de retransmisi�n.
     *
     * @param paquete Paquete GPSRP.
     * @param pEntrada Puerto por el que ha llegado el packet GPSRP.
     * @since 1.0
     */
    public void atenderDenegacionGPSRP(TGPSRPPDU paquete, int pEntrada) {
        int idf = paquete.getGPSRPPayload().getFlowID();
        int idp = paquete.getGPSRPPayload().getPacketID();
        TGPSRPRequestEntry ep = gpsrpRequests.getEntry(idf, idp);
        if (ep != null) {
            ep.forceTimeoutReset();
            int p = ep.getOutgoingPort();
            if (!ep.isPurgeable()) {
                String IPDestino = ep.getCrossedNodeIP();
                if (IPDestino != null) {
                    requestGPSRP(idf, idp, IPDestino, p);
                } else {
                    gpsrpRequests.removeEntry(idf, idp);
                }
            } else {
                gpsrpRequests.removeEntry(idf, idp);
            }
        }
    }

    /**
     * Este m�todo atiende un packet GPSRP de aceptaci�n de retransmisi�n.
     *
     * @param paquete Paquete GPSRP de aceptaci�n de retransmisi�n.
     * @param pEntrada Puerto por el que ha llegado el packet.
     * @since 1.0
     */
    public void atenderAceptacionGPSRP(TGPSRPPDU paquete, int pEntrada) {
        int idf = paquete.getGPSRPPayload().getFlowID();
        int idp = paquete.getGPSRPPayload().getPacketID();
        gpsrpRequests.removeEntry(idf, idp);
    }

    /**
     * Este m�todo solicita un retransmisi�n GPSRP.
     *
     * @param paquete Paquete MPLS para el cual se solicita la retransmisi�n.
     * @param pSalida Puerto por el cual debe salir la solicitud.
     * @since 1.0
     */
    public void runGoSPDUStoreAndRetransmitProtocol(TMPLSPDU paquete, int pSalida) {
        TGPSRPRequestEntry ep = null;
        ep = this.gpsrpRequests.addEntry(paquete, pSalida);
        if (ep != null) {
            TActivePort puertoSalida = (TActivePort) ports.getPort(pSalida);
            TGPSRPPDU paqueteGPSRP = null;
            String IPDestino = ep.getCrossedNodeIP();
            if (IPDestino != null) {
                try {
                    paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                paqueteGPSRP.getGPSRPPayload().setFlowID(ep.getFlowID());
                paqueteGPSRP.getGPSRPPayload().setPacketID(ep.getPacketID());
                paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
                puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                try {
                    this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                    this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Este m�todo solicita un retransmisi�n GPSRP.
     *
     * @param idFlujo Identificador del flujo del cual se solicita
     * retransmisi�n.
     * @param idPaquete Identificaci�n del packet del flujo del que se desea
 retransmisi�n.
     * @param IPDestino IP del nodo al que se realizar� la solicitud.
     * @param pSalida Puerto de salida por el que se debe encaminar la
     * solicitud.
     * @since 1.0
     */
    public void requestGPSRP(int idFlujo, int idPaquete, String IPDestino, int pSalida) {
        TActivePort puertoSalida = (TActivePort) ports.getPort(pSalida);
        TGPSRPPDU paqueteGPSRP = null;
        if (IPDestino != null) {
            try {
                paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), IPDestino);
            } catch (Exception e) {
                e.printStackTrace();
            }
            paqueteGPSRP.getGPSRPPayload().setFlowID(idFlujo);
            paqueteGPSRP.getGPSRPPayload().setPacketID(idPaquete);
            paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
            puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Este m�todo deniega una retransmisi�n de paquetes.
     *
     * @param paquete Paquete GPSRP de solicitud de retransmisi�n.
     * @param pSalida Puerto por el que se debe enviar la denegaci�n.
     * @since 1.0
     */
    public void denegarGPSRP(TGPSRPPDU paquete, int pSalida) {
        TActivePort puertoSalida = (TActivePort) this.ports.getPort(pSalida);
        if (puertoSalida != null) {
            TGPSRPPDU paqueteGPSRP = null;
            try {
                paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), paquete.getIPv4Header().getOriginIPAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
            paqueteGPSRP.getGPSRPPayload().setFlowID(paquete.getGPSRPPayload().getFlowID());
            paqueteGPSRP.getGPSRPPayload().setPacketID(paquete.getGPSRPPayload().getPacketID());
            paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE);
            puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            discardPacket(paquete);
        }
    }

    /**
     * Este m�todo deniega una retransmisi�n de paquetes.
     *
     * @param paquete Paquete GPSRP de solicitud de retransmisi�n.
     * @param pSalida Puerto por el que se debe enviar la aceptaci�n.
     * @since 1.0
     */
    public void aceptarGPSRP(TGPSRPPDU paquete, int pSalida) {
        TActivePort puertoSalida = (TActivePort) this.ports.getPort(pSalida);
        if (puertoSalida != null) {
            TGPSRPPDU paqueteGPSRP = null;
            try {
                paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), paquete.getIPv4Header().getOriginIPAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
            paqueteGPSRP.getGPSRPPayload().setFlowID(paquete.getGPSRPPayload().getFlowID());
            paqueteGPSRP.getGPSRPPayload().setPacketID(paquete.getGPSRPPayload().getPacketID());
            paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_OK);
            puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            discardPacket(paquete);
        }
    }

    /**
     * Este m�todo comprueba si existe una entrada en la tabla de encaminamiento
 para el packet entrante. Si no es as�, clasifica el packet y, si es
 necesario, reencola el packet y solicita una etiqueta para poder
 enviarlo. Una vez que tiene entrada en la tabla de encaminamiento,
 reenv�a el packet hacia el interior del dominio MPLS o hacia el
 exterior, segun corresponda.
     *
     * @param paquete Paquete IPv4 de entrada.
     * @param pEntrada Puerto por el que ha accedido al nodo el packet.
     * @since 1.0
     */
    public void handleIPv4Packet(TIPv4PDU paquete, int pEntrada) {
        int valorFEC = classifyPacket(paquete);
        String IPDestinoFinal = paquete.getIPv4Header().getTargetIPAddress();
        TSwitchingMatrixEntry emc = null;
        boolean requiereLSPDeRespaldo = false;
        if ((paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)
                || (paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)
                || (paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)
                || (paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel() == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
            requiereLSPDeRespaldo = true;
        }
        emc = switchingMatrix.getEntry(pEntrada, valorFEC, TSwitchingMatrixEntry.FEC_ENTRY);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizFEC(paquete, pEntrada);
            if (emc != null) {
                if (!isExitActiveLER(IPDestinoFinal)) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    solicitarTLDP(emc);
                }
                this.ports.getPort(pEntrada).reEnqueuePacket(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                solicitarTLDP(emc);
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operacion = emc.getLabelStackOperation();
                if (operacion == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PUSH_LABEL) {
                        if (requiereLSPDeRespaldo) {
                            solicitarTLDPDeBackup(emc);
                        }
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        TMPLSPDU paqueteMPLS = this.createMPLSPacket(paquete, emc);
                        if (paquete.getSubtype() == TAbstractPDU.IPV4_GOS) {
                            int EXPAux = paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                            TMPLSLabel etiquetaMPLS1 = new TMPLSLabel();
                            etiquetaMPLS1.setBoS(false);
                            etiquetaMPLS1.setEXP(EXPAux);
                            etiquetaMPLS1.setLabel(1);
                            etiquetaMPLS1.setTTL(paquete.getIPv4Header().getTTL());
                            paqueteMPLS.getLabelStack().pushTop(etiquetaMPLS1);
                            paqueteMPLS.setSubtype(TAbstractPDU.MPLS_GOS);
                            paqueteMPLS.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            dmgp.addPacket(paqueteMPLS);
                        }
                        pSalida.putPacketOnLink(paqueteMPLS, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.POP_LABEL) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.SWAP_LABEL) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                discardPacket(paquete);
            }
        } else {
            discardPacket(paquete);
        }
    }

    /**
     * Este m�todo se llama cuando se recibe un packet TLDP con informaci�n
 sobre las etiquetas a use. El m�todo realiza sobre las matriz de
     * encaminamiento la operaci�n que sea necesario y propaga el cambio al nodo
     * adyacente que corresponda.
     *
     * @param paquete Paquete TLDP recibido.
     * @param pEntrada Puerto por el que se ha recibido el packet TLDP.
     * @since 1.0
     */
    public void handleTLDPPacket(TTLDPPDU paquete, int pEntrada) {
        if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST) {
            this.tratarSolicitudTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST_OK) {
            this.tratarSolicitudOkTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST_DENIED) {
            this.tratarSolicitudNoTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REMOVAL_REQUEST) {
            this.tratarEliminacionTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REVOMAL_REQUEST_OK) {
            this.tratarEliminacionOkTLDP(paquete, pEntrada);
        }
    }

    /**
     * Este m�todo comprueba si existe una entrada en la tabla de encaminamiento
 para el packet entrante. Si no es as�, clasifica el packet y, si es
 necesario, reencola el packet y solicita una etiqueta para poder
 enviarlo. Una vez que tiene entrada en la tabla de encaminamiento,
 reenv�a el packet hacia el siguiente nodo del dominio MPLS o hacia el
 exterior, segun corresponda.
     *
     * @param paquete Paquete MPLS recibido.
     * @param pEntrada Puerto por el que ha llegado el packet MPLS recibido.
     * @since 1.0
     */
    public void handleMPLSPacket(TMPLSPDU paquete, int pEntrada) {
        TMPLSLabel eMPLS = null;
        TSwitchingMatrixEntry emc = null;
        boolean conEtiqueta1 = false;
        boolean requiereLSPDeRespaldo = false;
        if (paquete.getLabelStack().getTop().getLabel() == 1) {
            eMPLS = paquete.getLabelStack().getTop();
            paquete.getLabelStack().popTop();
            conEtiqueta1 = true;
            if ((eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)
                    || (eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)
                    || (eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)
                    || (eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                requiereLSPDeRespaldo = true;
            }
        }
        int valorLABEL = paquete.getLabelStack().getTop().getLabel();
        String IPDestinoFinal = paquete.getIPv4Header().getTargetIPAddress();
        emc = switchingMatrix.getEntry(pEntrada, valorLABEL, TSwitchingMatrixEntry.LABEL_ENTRY);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizLABEL(paquete, pEntrada);
            if (emc != null) {
                if (!isExitActiveLER(IPDestinoFinal)) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    solicitarTLDP(emc);
                }
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                solicitarTLDP(emc);
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operacion = emc.getLabelStackOperation();
                if (operacion == TSwitchingMatrixEntry.UNDEFINED) {
                    if (conEtiqueta1) {
                        paquete.getLabelStack().pushTop(eMPLS);
                    }
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PUSH_LABEL) {
                        TMPLSLabel empls = new TMPLSLabel();
                        empls.setBoS(false);
                        empls.setEXP(0);
                        empls.setLabel(emc.getOutgoingLabel());
                        empls.setTTL(paquete.getLabelStack().getTop().getTTL() - 1);
                        if (requiereLSPDeRespaldo) {
                            solicitarTLDPDeBackup(emc);
                        }
                        paquete.getLabelStack().pushTop(empls);
                        if (conEtiqueta1) {
                            paquete.getLabelStack().pushTop(eMPLS);
                        }
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        if (conEtiqueta1) {
                            paquete.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            dmgp.addPacket(paquete);
                        }
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.POP_LABEL) {
                        if (paquete.getLabelStack().getTop().getBoS()) {
                            TIPv4PDU paqueteIPv4 = this.createIPv4Packet(paquete, emc);
                            TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                            pSalida.putPacketOnLink(paqueteIPv4, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        } else {
                            paquete.getLabelStack().popTop();
                            if (conEtiqueta1) {
                                paquete.getLabelStack().pushTop(eMPLS);
                            }
                            TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                            pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        }
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.SWAP_LABEL) {
                        if (requiereLSPDeRespaldo) {
                            solicitarTLDPDeBackup(emc);
                        }
                        paquete.getLabelStack().getTop().setLabel(emc.getOutgoingLabel());
                        if (conEtiqueta1) {
                            paquete.getLabelStack().pushTop(eMPLS);
                        }
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        if (conEtiqueta1) {
                            paquete.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            dmgp.addPacket(paquete);
                        }
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                discardPacket(paquete);
            }
        } else {
            if (conEtiqueta1) {
                paquete.getLabelStack().pushTop(eMPLS);
            }
            discardPacket(paquete);
        }
    }

    /**
     * Este m�todo trata una petici�n de etiquetas.
     *
     * @param paquete Petici�n de etiquetas recibida de otro nodo.
     * @param pEntrada Puerto de entrada de la petici�n de etiqueta.
     * @since 1.0
     */
    public void tratarSolicitudTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = switchingMatrix.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        if (emc == null) {
            emc = crearEntradaAPartirDeTLDP(paquete, pEntrada);
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                this.solicitarTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                // no hago nada. Se est� esperando una etiqueta.);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                labelWithdrawal(emc, pEntrada);
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
     * Este m�todo trata un packet TLDP de eliminaci�n de etiqueta.
     *
     * @param paquete Eliminaci�n de etiqueta recibida.
     * @param pEntrada Puerto por el que se recibi�n la eliminaci�n de etiqueta.
     * @since 1.0
     */
    public void tratarEliminacionTLDP(TTLDPPDU paquete, int pEntrada) {
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
                if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    int etiquetaActual = emc.getOutgoingLabel();
                    if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                        emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                    } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                        emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                    } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                        emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                    } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                    } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                        enviarEliminacionOkTLDP(emc, pEntrada);
                    } else if (etiquetaActual > 15) {
                        emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                    } else {
                        discardPacket(paquete);
                    }
                    if (emc.backupLSPHasBeenEstablished() || emc.backupLSPShouldBeRemoved()) {
                        int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                        if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                            emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                            labelWithdrawal(emc, emc.getBackupOutgoingPortID());
                        } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                            emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                            labelWithdrawal(emc, emc.getBackupOutgoingPortID());
                        } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                            emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                            labelWithdrawal(emc, emc.getBackupOutgoingPortID());
                        } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                            // No hacemos nada... esperamos.
                        } else if (etiquetaActualBackup > 15) {
                            emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                            labelWithdrawal(emc, emc.getBackupOutgoingPortID());
                        } else {
                            discardPacket(paquete);
                        }
                    }
                } else {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                }
            } else if (emc.getOutgoingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    labelWithdrawal(emc, emc.getIncomingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    labelWithdrawal(emc, emc.getIncomingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    labelWithdrawal(emc, emc.getIncomingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                } else if (etiquetaActual > 15) {
                    if (emc.backupLSPHasBeenEstablished()) {
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        if (emc.getBackupOutgoingPortID() >= 0) {
                            TInternalLink ei = (TInternalLink) ports.getPort(emc.getBackupOutgoingPortID()).getLink();
                            ei.ponerLSP();
                            ei.quitarLSPDeBackup();
                        }
                        emc.switchToBackupLSP();
                    } else {
                        if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                            emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                            enviarEliminacionOkTLDP(emc, pEntrada);
                            labelWithdrawal(emc, emc.getIncomingPortID());
                        } else {
                            enviarEliminacionOkTLDP(emc, pEntrada);
                            switchingMatrix.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                        }
                    }
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    // No hago nada. Espero...
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup > 15) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else {
                    discardPacket(paquete);
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
    public void tratarSolicitudOkTLDP(TTLDPPDU paquete, int pEntrada) {
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
                            et.ponerLSP();
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
                    enviarSolicitudOkTLDP(emc);
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
    public void tratarSolicitudNoTLDP(TTLDPPDU paquete, int pEntrada) {
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
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                    enviarSolicitudNoTLDP(emc);
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
 etiqueta.
     *
     * @param paquete Paquete de confirmaci�n e eliminaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de
     * eliminaci�n de etiqueta.
     * @since 1.0
     */
    public void tratarEliminacionOkTLDP(TTLDPPDU paquete, int pEntrada) {
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
                                        ei.quitarLSPDeBackup();
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
                                        ei.quitarLSPDeBackup();
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
                                    ei.quitarLSPDeBackup();
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
                            ei.quitarLSPDeBackup();
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
                            ei.quitarLSPDeBackup();
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
    public void enviarSolicitudOkTLDP(TSwitchingMatrixEntry emc) {
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
                        TPort pSalida = ports.getPortConnectedToANodeWithIPAddress(IPDestino);
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
    public void enviarSolicitudNoTLDP(TSwitchingMatrixEntry emc) {
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
                        TPort pSalida = ports.getPortConnectedToANodeWithIPAddress(IPDestino);
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
    public void enviarEliminacionOkTLDP(TSwitchingMatrixEntry emc, int puerto) {
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
    public void solicitarTLDP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        if (emc.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
            String IPSalto = topology.obtenerIPSaltoRABAN(IPLocal, IPDestinoFinal);
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
                    TPort pSalida = ports.getPortConnectedToANodeWithIPAddress(IPSalto);
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
    public void solicitarTLDPDeBackup(TSwitchingMatrixEntry emc) {
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
                                    TPort pSalida = ports.getPortConnectedToANodeWithIPAddress(IPSalto);
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
    public void labelWithdrawal(TSwitchingMatrixEntry emc, int puerto) {
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
        labelWithdrawal(emc, puerto);
    }

    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de
     * una entrada de la matriz de conmutaci�n a todos los ports necesarios.
     *
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry emc) {
        labelWithdrawal(emc, emc.getIncomingPortID());
        labelWithdrawal(emc, emc.getOutgoingPortID());
        labelWithdrawal(emc, emc.getBackupOutgoingPortID());
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
 datos de un packet TLDP entrante.
     *
     * @param paqueteSolicitud Paquete TLDP entrante, de solicitud de etiqueta.
     * @param pEntrada Puerto de entrada del packet TLDP.
     * @return La entrada de la matriz de conmutaci�n, ya creada, insertada e
     * inicializada.
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaAPartirDeTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.getTLDPPayload().getTLDPIdentifier();
        TPort puertoEntrada = ports.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.getTLDPPayload().getTargetIPAddress();
        String IPSalto = topology.obtenerIPSaltoRABAN(this.getIPAddress(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = ports.getPortConnectedToANodeWithIPAddress(IPSalto);
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
 en un packet IPv4 recibido.
     *
     * @param paqueteIPv4 Paquete IPv4 recibido.
     * @param pEntrada Puerto por el que ha llegado el packet IPv4.
     * @return La entrada de la matriz de conmutaci�n, creada, insertada e
     * inicializada.
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizFEC(TIPv4PDU paqueteIPv4, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteIPv4.getIPv4Header().getTargetIPAddress();
        String IPSalida = topology.obtenerIPSaltoRABAN(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = ports.getPort(pEntrada);
            TPort puertoSalida = ports.getPortConnectedToANodeWithIPAddress(IPSalida);
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
 en un packet MPLS recibido.
     *
     * @param paqueteMPLS Paquete MPLS recibido.
     * @param pEntrada Puerto por el que ha llegado el packet MPLS.
     * @return La entrada de la matriz de conmutaci�n, creada, insertada e
     * inicializada.
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizLABEL(TMPLSPDU paqueteMPLS, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteMPLS.getIPv4Header().getTargetIPAddress();
        String IPSalida = topology.obtenerIPSaltoRABAN(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = ports.getPort(pEntrada);
            TPort puertoSalida = ports.getPortConnectedToANodeWithIPAddress(IPSalida);
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
 asociada al mismo y crea un packet MPLS etiquetado correctamente que
 contiene dicho packet IPv4 listo para ser transmitido hacia el interior
 del dominio.
     *
     * @param ipv4Packet Paquete IPv4 que se debe etiquetar.
     * @param switchingMatrixEntry Entrada de la matriz de conmutaci�n asociada
 al packet IPv4 que se desea etiquetar.
     * @return El packet IPv4 de entrada, convertido en un packet MPLS
 correctamente etiquetado.
     * @since 1.0
     */
    public TMPLSPDU createMPLSPacket(TIPv4PDU ipv4Packet, TSwitchingMatrixEntry switchingMatrixEntry) {
        TMPLSPDU mplsPacket = null;
        try {
            mplsPacket = new TMPLSPDU(gIdent.getNextID(), ipv4Packet.getIPv4Header().getOriginIPAddress(), ipv4Packet.getIPv4Header().getTargetIPAddress(), ipv4Packet.getSize());
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
 de conmutaci�n asociada. Extrae del packet MPLS el packet IP
 correspondiente y actualiza sus elementFields correctamente.
     *
     * @param MPLSPacket Paquete MPLS cuyo contenido de nivel IPv4 se desea
     * extraer.
     * @param switchingMatrixEntry Entrada de la matriz de conmutaci�n asociada
 al packet MPLS.
     * @return Paquete IPv4 que corresponde al packet MPLS una vez que se ha
 eliminado toda la informaci�n MLPS; que se ha desetiquetado.
     * @since 1.0
     */
    public TIPv4PDU createIPv4Packet(TMPLSPDU MPLSPacket, TSwitchingMatrixEntry switchingMatrixEntry) {
        TIPv4PDU ipv4Packet = null;
        try {
            ipv4Packet = new TIPv4PDU(gIdent.getNextID(), MPLSPacket.getIPv4Header().getOriginIPAddress(), MPLSPacket.getIPv4Header().getTargetIPAddress(), MPLSPacket.getTCPPayload().getSize());
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
        String targetIPAddress = packet.getIPv4Header().getTargetIPAddress();
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
        TPort portAux = ports.getPortConnectedToANodeWithIPAddress(targetIPAddress);
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
     * Este m�todo toma un codigo de error y genera un mensaje textual del
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
