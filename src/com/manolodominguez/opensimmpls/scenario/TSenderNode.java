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
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSLabel;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.protocols.TIPv4PDU;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPortSet;
import com.manolodominguez.opensimmpls.hardware.ports.TPort;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import com.manolodominguez.opensimmpls.utils.TRotaryIDGenerator;
import java.awt.Point;
import java.util.Random;

/**
 * This class implements a sender node; a node that only generates traffic.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSenderNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * This is the constructor of the class and creates a new instance of
     * TSenderNode.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param nodeID The node identifier that is unique in the topology.
     * @param ipv4Address IPv4 address of the node.
     * @param identifierGenerator An identifier generator that will be used to
     * generate unique identifiers for events.
     * @param topology Topology the node belongs to.
     */
    public TSenderNode(int nodeID, String ipv4Address, TLongIDGenerator identifierGenerator, TTopology topology) {
        super(nodeID, ipv4Address, identifierGenerator, topology);
        // FIX: This method is overridable. Avoid using this method to update
        // the number of ports or make it final.
        this.setPorts(super.DEFAULT_NUM_PORTS_SENDER);
        this.packetIdentifierGenerator = new TLongIDGenerator();
        this.packetGoSdentifierGenerator = new TRotaryIDGenerator();
        this.targetIPv4Address = "";
        // FIX: Use class constants instead of harcoded values for all cases.
        this.trafficGenerationRate = 10;
        this.trafficGenerationMode = TSenderNode.CONSTANT_TRAFFIC_RATE;
        this.encapsulateOverMPLS = false;
        this.gosLevel = 0;
        this.requestBackupLSP = false;
        this.randomNumberGenerator = new Random();
        this.sendingLabel = (16 + randomNumberGenerator.nextInt(1000000));
        this.constantPayloadSizeInBytes = 0;
        this.variablePayloadSizeInBytes = 0;
        this.stats = new TSenderStats();
        // FIX: This method is overridable. Avoid using this method to update
        // the number of ports or make it final.
        this.stats.activateStats(this.isGeneratingStats());
    }

    /**
     * This method gets the payload size (in octects) that has been defined to
     * be used when constant traffic rate is configured.
     *
     * @return The payload size (in octects) that has been defined to be used
     * when constant traffic rate is configured.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getConstantPayloadSizeInBytes() {
        return this.constantPayloadSizeInBytes;
    }

    /**
     * This method sets the payload size (in octects) that is desired to be used
     * when constant traffic rate is configured.
     *
     * @param constantPayloadSizeInBytes the payload size (in octects) that is
     * desired to be used when constant traffic rate is configured.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setConstantPayloadSizeInBytes(int constantPayloadSizeInBytes) {
        this.constantPayloadSizeInBytes = constantPayloadSizeInBytes;
    }

    /**
     * This method sets the target node, it is the node that will receive the
     * traffice generated by this TSenderNode.
     *
     * @param targetNodeName Name of the node that will receive the traffice
     * generated by this TSenderNode.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTargetNode(String targetNodeName) {
        if (!targetNodeName.equals("")) {
            TNode targetNodeAux = this.topology.getFirstNodeNamed(targetNodeName);
            if (targetNodeAux != null) {
                this.targetIPv4Address = targetNodeAux.getIPv4Address();
            }
            // FIX: this should throws an exception if the node does not exist.
        }
    }

    /**
     * This method gets the IPv4 address of the target node, it is the node that
     * will receive the traffice generated by this TSenderNode.
     *
     * @return IPv4 address of the node that will receive the traffice generated
     * by this TSenderNode.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String getTargetIPv4Address() {
        return this.targetIPv4Address;
    }

    /**
     * This method sets the traffic generation rate of this node, in Mbps.
     *
     * @param trafficGenerationRate The traffic generation rate of this node in
     * Mbps.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTrafficGenerationRate(int trafficGenerationRate) {
        this.trafficGenerationRate = trafficGenerationRate;
    }

    /**
     * This method gets the traffic generation rate of this node, in Mbps.
     *
     * @return The traffic generation rate of this node in Mbps.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getTrafficGenerationRate() {
        return this.trafficGenerationRate;
    }

    /**
     * This is a private method that determines the value of EXP field for all
     * packets that have to be generated, depending on some factors as, for
     * instance, the required GoS level or the need of establishing a backup
     * LSP. Read the "Guarentee of Service (GoS) support over MPLS using active
     * techniques" to know more about how EXP field is used.
     *
     * @return the value of EXP field for all packets that have to be generated.
     * One of the constants of TAbstractPDu that starts by "EXP".
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private int getRequiredEXPValue() {
        //FIX: replace harcoded values for class constants in all cases.
        if ((this.gosLevel == 0) && (this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP;
        } else if ((this.gosLevel == 1) && (this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP;
        } else if ((this.gosLevel == 2) && (this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP;
        } else if ((this.gosLevel == 3) && (this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP;
        } else if ((this.gosLevel == 0) && (!this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP;
        } else if ((this.gosLevel == 1) && (!this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP;
        } else if ((this.gosLevel == 2) && (!this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP;
        } else if ((this.gosLevel == 3) && (!this.requestBackupLSP)) {
            return TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP;
        }
        return TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP;
    }

    /**
     * This method sets the traffic generation mode of this node.
     *
     * @param trafficGenerationMode the traffic generation mode of this node.
     * One of the *_TRAFFIC_RATE constants in this class.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTrafficGenerationMode(int trafficGenerationMode) {
        this.trafficGenerationMode = trafficGenerationMode;
    }

    /**
     * This method gets the traffic generation mode of this node.
     *
     * @return the traffic generation mode of this node. One of the
     * *_TRAFFIC_RATE constants in this class.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getTrafficGenerationMode() {
        return this.trafficGenerationMode;
    }

    /**
     * This method sets wheter the traffic generated is native TCP or it is TCP
     * encapsulated over MPLS.
     *
     * @param encapsulateOverMPLS TRUE, if the traffic will be encapsulated over
     * MPLS. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void encapsulateOverMPLS(boolean encapsulateOverMPLS) {
        this.encapsulateOverMPLS = encapsulateOverMPLS;
    }

    /**
     * This method gets wheter the traffic generated is native TCP or it is TCP
     * encapsulated over MPLS.
     *
     * @return TRUE, if the node is generating traffic encapsulated over MPLS.
     * Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isEncapsulatingOverMPLS() {
        return this.encapsulateOverMPLS;
    }

    /**
     * This sets the GoS level that will be embedded in each packet generated.
     * This comes from the "Guarantee of Service (GoS) support over MPLS using
     * active techniques" proposal. Read the proposal so know more.
     *
     * @param gosLevel GoS level. One of the EXP_* constants defined in
     * TAbstractPDU class.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setGoSLevel(int gosLevel) {
        this.gosLevel = gosLevel;
    }

    /**
     * This gets the GoS level that is being embedded in each packet generated.
     * This comes from the "Guarantee of Service (GoS) support over MPLS using
     * active techniques" proposal. Read the proposal so know more.
     *
     * @return GoS level. One of the EXP_* constants defined in TAbstractPDU
     * class.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getGoSLevel() {
        return this.gosLevel;
    }

    /**
     * This method sets whether packets generated will request a backup LSP from
     * active nodes or not.
     *
     * @param requestBackupLSP TRUE if packets generated have to request a
     * backup LSP from active nodes. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setRequestBackupLSP(boolean requestBackupLSP) {
        this.requestBackupLSP = requestBackupLSP;
    }

    /**
     * This method gets whether packets generated are requesting a backup LSP
     * from active nodes or not.
     *
     * @return TRUE if packets generated are requesting a backup LSP from active
     * nodes. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isRequestingBackupLSP() {
        return this.requestBackupLSP;
    }

    /**
     * This method gets the type of this node.
     *
     * @return TNode.SENDER, as this is a TSenderNode.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public int getNodeType() {
        return TNode.SENDER;
    }

    /**
     * This method receive a timer event from the simulation's global timer. It
     * does some initial tasks and then wake up this sender node to start doing
     * its work.
     *
     * @param timerEvent a timer event that is used to synchronize all nodes and
     * that inclues a number of nanoseconds to be used by this sender node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent timerEvent) {
        this.setTickDuration(timerEvent.getStepDuration());
        this.setCurrentInstant(timerEvent.getUpperLimit());
        this.availableNs += timerEvent.getStepDuration();
        this.startOperation();
    }

    /**
     * This method starts all tasks that has to be executed during a timer tick.
     * The number of nanoseconds of this tick is the time this sender node will
     * work.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void run() {
        // Actions to be done during the timer tick.
        try {
            this.generateSimulationEvent(new TSimulationEventNodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), 0));
        } catch (Exception e) {
            // FIX: this is not a good practice. Avoid.
            e.printStackTrace();
        }
        TAbstractPDU packetAux = generatePacket();
        boolean aPacketWasGenerated = false;
        while (getMaxTransmittableOctets() > getNextPacketTotalSizeInBytes(packetAux)) {
            aPacketWasGenerated = true;
            generateTraffic();
        }
        packetAux = null;
        if (aPacketWasGenerated) {
            this.resetTicksWithoutEmitting();
        } else {
            this.increaseTicksWithoutEmitting();
        }
        this.stats.groupStatsByTimeInstant(this.getCurrentInstant());
    }

    /**
     * Este m�todo obtiene el tama�o que tendr� la carga util del siguiente
     * paquete generado, independientemente de que se est� tratando con tr�fico
     * constante o variable.
     *
     * @return El tama�o de la carga �til del siguiente paquete que generar� el
     * emisor.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getNextPacketPayloadSizeInBytes() {
        if (this.trafficGenerationMode == TSenderNode.CONSTANT_TRAFFIC_RATE) {
            return this.constantPayloadSizeInBytes;
        }
        return this.variablePayloadSizeInBytes;
    }

    /**
     * Este m�todo obtiene el tama�o de la header del sigueinte paquete que se
     * generar�, independientemente del tipo de tr�fico del que se trate y de
     * los valores de garant�a de Servicio con los que peuda estar marcado.
     *
     * @param packet Paquete de cuya header queremos conocer el tama�o.
     * @return El tama�o de la header.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getNextPacketHeaderSizeInBytes(TAbstractPDU packet) {
        TMPLSPDU mplsPacket = null;
        TIPv4PDU ipv4Packet = null;
        if (packet.getType() == TAbstractPDU.MPLS) {
            mplsPacket = (TMPLSPDU) packet;
            return mplsPacket.getSize();
        }
        ipv4Packet = (TIPv4PDU) packet;
        return ipv4Packet.getSize();
    }

    /**
     * Este m�todo calcula, el tama�o del siguiente paquete a generar,
     * independientemente de que se de tipo constante o variable, o de cualquier
     * protocolo de los soportados, e incluso de que nivel de GoS tenga
     * asignado.
     *
     * @param packet paquete cuyo tamanio se desea calcular.
     * @return El tama�o total del paquete especificado por par�metros.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getNextPacketTotalSizeInBytes(TAbstractPDU packet) {
        int payloadSizeInBytes = 0;
        int headerSizeInBytes = 0;
        int packetSizeInBytes = 0;
        payloadSizeInBytes = getNextPacketPayloadSizeInBytes();
        headerSizeInBytes = getNextPacketHeaderSizeInBytes(packet);
        packetSizeInBytes = payloadSizeInBytes + headerSizeInBytes;
        return packetSizeInBytes;
    }

    /**
     * Este m�todo crea paquetes de tr�fico acorde a la configuraci�n el emisor
     * de tr�fico y los env�a al receptor destino del tr�fico.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void generateTraffic() {
        TAbstractPDU emptyPacket = null;
        TAbstractPDU packetWithPayload = null;
        // FIX: avoid using harcoded values. Use class constants instead.
        TPort port = this.ports.getPort(0);
        if (port != null) {
            if (!port.isAvailable()) {
                emptyPacket = generatePacket();
                packetWithPayload = this.addDataToPacket(emptyPacket);
                if (packetWithPayload != null) {
                    try {
                        // FIX: avoid using harcoded values. Use class constants instead.
                        int packetType = 0;
                        if (packetWithPayload.getType() == TAbstractPDU.MPLS) {
                            TMPLSPDU mplsPacket = (TMPLSPDU) packetWithPayload;
                            packetType = mplsPacket.getSubtype();
                        } else if (packetWithPayload.getType() == TAbstractPDU.IPV4) {
                            TIPv4PDU ipv4Packet = (TIPv4PDU) packetWithPayload;
                            packetType = ipv4Packet.getSubtype();
                        }
                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packetType, packetWithPayload.getSize()));
                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), packetType));
                    } catch (Exception e) {
                        // FIX: this is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                    if (this.topology.getNextHopIPv4Address(this.getIPv4Address(), this.getTargetIPv4Address()) != null) {
                        port.putPacketOnLink(packetWithPayload, port.getLink().getDestinationOfTrafficSentBy(this));
                    } else {
                        discardPacket(packetWithPayload);
                    }
                }
            }
        }
    }

    /**
     * Este m�todo contabiliza un paquete y su tama�o asociado, en las
     * estad�sticas del nodo emisor, y sus gr�ficas.
     *
     * @param packet Paquete que se desea contabilizar.
     * @param isIncomingPacket TRUE indica que se trata de un paquete que ha
     * entrado en el nodo. FALSE indica que se trata de un paquete que ha salido
     * del nodo.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void accountPacket(TAbstractPDU packet, boolean isIncomingPacket) {
        if (isIncomingPacket) {
            if (packet.getSubtype() == TAbstractPDU.MPLS) {
            } else if (packet.getSubtype() == TAbstractPDU.MPLS_GOS) {
            } else if (packet.getSubtype() == TAbstractPDU.IPV4) {
            } else if (packet.getSubtype() == TAbstractPDU.IPV4_GOS) {
            }
        } else if (packet.getSubtype() == TAbstractPDU.MPLS) {
        } else if (packet.getSubtype() == TAbstractPDU.MPLS_GOS) {
        } else if (packet.getSubtype() == TAbstractPDU.IPV4) {
        } else if (packet.getSubtype() == TAbstractPDU.IPV4_GOS) {
        }
    }

    /**
     * Este m�todo calcula cu�ntos nanosegundos necesita el nodo emisor para
     * generar y transmitir un bit. Se basa para ello en la tasa de generaci�n
     * de tr�fico del nodo.
     *
     * @return El n�mero de nanosegundos necesarios generar y transmitir un bit.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public double getNsPerBit() {
        long tasaEnBitsPorSegundo = (long) (this.trafficGenerationRate * 1048576L);
        double nsPorCadaBit = (double) ((double) 1000000000.0 / (long) tasaEnBitsPorSegundo);
        return nsPorCadaBit;
    }

    /**
     * Este m�todo calcula el n�mero de nanosegundos necesarios para poder
     * generar y enviar un determinado n�mero de octetos.
     *
     * @param octects N�mero de octetos que deseamos generar y transmitir.
     * @return N�mero de nanosegundos que necesitamos para los octetos
     * especificados.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public double getNsUsedByOctects(int octects) {
        double nsPorCadaBit = getNsPerBit();
        long bitsOctetos = (long) ((long) octects * (long) 8);
        return (double) ((double) nsPorCadaBit * (long) bitsOctetos);
    }

    /**
     * Este m�todo calcula el n�mero de bits que puede generar y transmitir con
     * el n�mero de nanosegundos con los que cuenta.
     *
     * @return N�mero de bits que puede generar y transmitir.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getMaxTransmittableBits() {
        double nsPorCadaBit = getNsPerBit();
        double maximoBits = (double) ((double) this.availableNs / (double) nsPorCadaBit);
        return (int) maximoBits;
    }

    /**
     * Este metodo calcula el n�mero de octetos completos que puede generar y
     * transmitir el emisor teniendo en cuenta el n�mero de nanosegundos con los
     * que cuenta.
     *
     * @return El n�mero de octetos completos que se pueden generar y
     * transmitir.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getMaxTransmittableOctets() {
        double maximoBytes = ((double) getMaxTransmittableBits() / (double) 8.0);
        return (int) maximoBytes;
    }

    /**
     * Este m�todo calcula autom�ticamente el tama�o de la carga util del
     * siguiente paquete a generar. Si el tr�fico es constante, devolver� el
     * tama�o de paquete con el que se configur� el emisor. Si el tama�o es
     * variable, generar� tr�fico siguiendo una funci�n de probabilidad en la
     * cual se sigue la siguiente distribuci�n de tama�os:
     *
     * Tama�oPaquete < 100 octetos ---------------------> 47% Tama�oPaquete >=
     * 100 octetos y < 1400 octetos ---> 24% Tama�oPaquete >= 1400 octetos y
     * < 1500 octetos --> 18% Tama�oPaquete >= 1500 octetos ------------------->
     * 1%
     *
     * Esta distribuci�n est� extra�da de las estad�sticas de la red Abilene,
     * que son p�blicas y se pueden observar en
     * http://netflow.internet2.edu/weekly.
     *
     * @return El tama�o que debe tener el siguiente paquete.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int computeNextPacketPayloadSize() {
        int probabilidad = this.randomNumberGenerator.nextInt(100);
        int tamanioGenerado = 0;
        if (probabilidad < 47) {
            tamanioGenerado = this.randomNumberGenerator.nextInt(100);
            tamanioGenerado -= 40;
        } else if ((probabilidad >= 47) && (probabilidad < 71)) {
            tamanioGenerado = (this.randomNumberGenerator.nextInt(1300) + 100);
            tamanioGenerado -= 40;
        } else if ((probabilidad >= 71) && (probabilidad < 99)) {
            tamanioGenerado = (this.randomNumberGenerator.nextInt(100) + 1400);
            tamanioGenerado -= 40;
        } else if (probabilidad >= 99) {
            tamanioGenerado = (this.randomNumberGenerator.nextInt(64035) + 1500);
            tamanioGenerado -= 40;
        }
        return tamanioGenerado;
    }

    /**
     * Este m�todo toma como par�metro un paquete vacio y devuelve un paquete
     * con datos insertados. Los datos ser�n del tama�o que se haya estimado en
     * los distintos m�todos de la clase,pero ser� el correcto.
     *
     * @param paquete Paquete al que se quiere a�adir datos.
     * @return Paquete con datos insertados del tama�o correcto seg�n el tipo de
     * gr�fico.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TAbstractPDU addDataToPacket(TAbstractPDU paquete) {
        TMPLSPDU paqueteMPLS = null;
        TIPv4PDU paqueteIPv4 = null;
        int bitsMaximos = getMaxTransmittableBits();
        int tamanioCabecera = 0;
        int tamanioDatos = 0;
        int tamanioTotal = 0;
        double nsUsados = 0;
        tamanioTotal = getNextPacketTotalSizeInBytes(paquete);
        tamanioCabecera = getNextPacketHeaderSizeInBytes(paquete);
        tamanioDatos = getNextPacketPayloadSizeInBytes();
        if (tamanioTotal > getMaxTransmittableOctets()) {
            paquete = null;
            return null;
        } else if (paquete.getType() == TAbstractPDU.MPLS) {
            paqueteMPLS = (TMPLSPDU) paquete;
            paqueteMPLS.getTCPPayload().setSize((int) tamanioDatos);
            nsUsados = this.getNsUsedByOctects(tamanioTotal);
            this.availableNs -= nsUsados;
            if (this.availableNs < 0) {
                this.availableNs = 0;
            }
            if (this.trafficGenerationMode == TSenderNode.VARIABLE_TRAFFIC_RATE) {
                this.variablePayloadSizeInBytes = this.computeNextPacketPayloadSize();
            }
            return paqueteMPLS;
        } else if (paquete.getType() == TAbstractPDU.IPV4) {
            paqueteIPv4 = (TIPv4PDU) paquete;
            paqueteIPv4.getTCPPayload().setSize(tamanioDatos);
            nsUsados = this.getNsUsedByOctects(tamanioTotal);
            this.availableNs -= nsUsados;
            if (this.availableNs < 0) {
                this.availableNs = 0;
            }
            if (this.trafficGenerationMode == TSenderNode.VARIABLE_TRAFFIC_RATE) {
                this.variablePayloadSizeInBytes = this.computeNextPacketPayloadSize();
            }
            return paqueteIPv4;
        }
        return null;
    }

    /**
     * Este m�todo devuelve un paquete vac�o (sin datos) del tipo correcto para
     * el que esta configurado el nodo emisor.
     *
     * @return El paquete creado.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TAbstractPDU generatePacket() {
        int valorGoS = this.getRequiredEXPValue();
        try {
            if (this.encapsulateOverMPLS) {
                if (valorGoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    TMPLSPDU paquete = new TMPLSPDU(packetIdentifierGenerator.getNextID(), getIPv4Address(), this.targetIPv4Address, 0);
                    TMPLSLabel etiquetaMPLSDeEmision = new TMPLSLabel();
                    etiquetaMPLSDeEmision.setBoS(true);
                    etiquetaMPLSDeEmision.setEXP(0);
                    etiquetaMPLSDeEmision.setLabel(sendingLabel);
                    etiquetaMPLSDeEmision.setTTL(paquete.getIPv4Header().getTTL());
                    paquete.getLabelStack().pushTop(etiquetaMPLSDeEmision);
                    return paquete;
                } else {
                    TMPLSPDU paquete = new TMPLSPDU(packetIdentifierGenerator.getNextID(), getIPv4Address(), this.targetIPv4Address, 0);
                    paquete.setSubtype(TAbstractPDU.MPLS_GOS);
                    paquete.getIPv4Header().getOptionsField().setRequestedGoSLevel(valorGoS);
                    paquete.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(this.packetGoSdentifierGenerator.getNextID());
                    TMPLSLabel etiquetaMPLSDeEmision = new TMPLSLabel();
                    etiquetaMPLSDeEmision.setBoS(true);
                    etiquetaMPLSDeEmision.setEXP(0);
                    etiquetaMPLSDeEmision.setLabel(sendingLabel);
                    etiquetaMPLSDeEmision.setTTL(paquete.getIPv4Header().getTTL());
                    TMPLSLabel etiquetaMPLS1 = new TMPLSLabel();
                    etiquetaMPLS1.setBoS(false);
                    etiquetaMPLS1.setEXP(valorGoS);
                    etiquetaMPLS1.setLabel(1);
                    etiquetaMPLS1.setTTL(paquete.getIPv4Header().getTTL());
                    paquete.getLabelStack().pushTop(etiquetaMPLSDeEmision);
                    paquete.getLabelStack().pushTop(etiquetaMPLS1);
                    return paquete;
                }
            } else if (valorGoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                TIPv4PDU paquete = new TIPv4PDU(packetIdentifierGenerator.getNextID(), getIPv4Address(), this.targetIPv4Address, 0);
                return paquete;
            } else {
                TIPv4PDU paquete = new TIPv4PDU(packetIdentifierGenerator.getNextID(), getIPv4Address(), this.targetIPv4Address, 0);
                paquete.setSubtype(TAbstractPDU.IPV4_GOS);
                paquete.getIPv4Header().getOptionsField().setRequestedGoSLevel(valorGoS);
                paquete.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(this.packetGoSdentifierGenerator.getNextID());
                return paquete;
            }
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Este m�todo descarta un paquete de cualquier tipo. Adem�s anota los datos
     * relativos en ese descarte en las estad�sticas del nodo.
     *
     * @param paquete Paquete que se quiere descartar.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void discardPacket(TAbstractPDU paquete) {
        try {
            this.generateSimulationEvent(new TSimulationEventPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getCurrentInstant(), paquete.getSubtype()));
            this.stats.addStatEntry(paquete, TStats.DISCARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paquete = null;
    }

    /**
     * Este m�todo investiga si al nodo le quedan ports libres.
     *
     * @return TRUE, si al nodo le quedan ports libres. FALSE en caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }

    /**
     * Este m�todo permite acceder a los ports del nodo directamente.
     *
     * @return El conjunto de ports del nodo.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public TPortSet getPorts() {
        return this.ports;
    }

    /**
     * Este m�todo devuelve el peso del nodo, que debe ser tenido en cuenta por
     * el algoritmo e encaminamiento para el c�lculo de rutas.
     *
     * @return En el nodo emisor, siempre es cero.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public long getRoutingWeight() {
        return 0;
    }

    /**
     * Este m�todo devuelve si el nodo est� bien configurado o no.
     *
     * @return TRUE, si el nodo est� bien configurado. FALSE en caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }

    /**
     * Este m�todo calcula si el nodo est� bien configurado o no, actualizando
     * el atributo que indicar� posteriormente este hecho.
     *
     * @param topology Topolog�a dentro de la cual est� incluido el nodo emisor.
     * @param reconfiguration TRUE si se est� reconfigurando el nodo emisor.
     * FALSE si est� configurando por primera vez.
     * @return CORRECTA, si el nodo est� bien configurado. Un codigo de error en
     * caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TSenderNode.UNNAMED;
        }
        boolean soloEspacios = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                soloEspacios = false;
            }
        }
        if (soloEspacios) {
            return TSenderNode.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TNode tp = topology.getFirstNodeNamed(this.getName());
            if (tp != null) {
                return TSenderNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode tp = topology.getFirstNodeNamed(this.getName());
            if (tp != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return TSenderNode.NAME_ALREADY_EXISTS;
                }
            }
        }

        if (this.getTargetIPv4Address() == null) {
            return TSenderNode.TARGET_UNREACHABLE;
        }
        if (this.getTargetIPv4Address().equals("")) {
            return TSenderNode.TARGET_UNREACHABLE;
        }
        this.setWellConfigured(true);
        return TSenderNode.OK;
    }

    /**
     * Este m�todo transforma un codigo de error en un mensaje con similar
     * significado, pero legible por el usuario.
     *
     * @param errorCode C�digo de error cuyo mensaje se desea obtener.
     * @return El mensaje equivalente al codigo de error, pero legible.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case UNNAMED:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEmisor.FALTA_NOMBRE"));
            case NAME_ALREADY_EXISTS:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEmisor.NOMBRE_REPETIDO"));
            case ONLY_BLANK_SPACES:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TNodoEmisor.NoSoloEspacios"));
            case TARGET_UNREACHABLE:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TNodoEmisor.DestinoParaElTrafico"));
        }
        return ("");
    }

    /**
     * Este m�todo transforma el nodo emisor en una cadena de caracterres que se
     * puede volcar a disco.
     *
     * @return TRUE, si se ha realizado la resializaci�n correctamente. FALSE en
     * caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public String marshall() {
        String cadena = "#Emisor#";
        cadena += this.getNodeID();
        cadena += "#";
        cadena += this.getName().replace('#', ' ');
        cadena += "#";
        cadena += this.getIPv4Address();
        cadena += "#";
        cadena += this.isSelected();
        cadena += "#";
        cadena += this.nameMustBeDisplayed();
        cadena += "#";
        cadena += this.isGeneratingStats();
        cadena += "#";
        cadena += this.getScreenPosition().x;
        cadena += "#";
        cadena += this.getScreenPosition().y;
        cadena += "#";
        cadena += this.getTargetIPv4Address();
        cadena += "#";
        cadena += this.isRequestingBackupLSP();
        cadena += "#";
        cadena += this.getGoSLevel();
        cadena += "#";
        cadena += this.isEncapsulatingOverMPLS();
        cadena += "#";
        cadena += this.getTrafficGenerationRate();
        cadena += "#";
        cadena += this.getTrafficGenerationMode();
        cadena += "#";
        cadena += this.getConstantPayloadSizeInBytes();
        cadena += "#";
        return cadena;
    }

    /**
     * Este m�todo toma una cadena de texto que representa a un emisor
     * serializado y construye, en base a ella, el emisor en memoria sobre la
     * instancia actual.
     *
     * @param serializedSender El nodo emisor serializado.
     * @return TRUE, si se consigue deserializar correctamente. FALSE en caso
     * contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public boolean unMarshall(String serializedSender) {
        String valores[] = serializedSender.split("#");
        if (valores.length != 17) {
            return false;
        }
        this.setNodeID(Integer.parseInt(valores[2]));
        this.setName(valores[3]);
        this.setIPv4Address(valores[4]);
        this.setSelected(Integer.parseInt(valores[5]));
        this.setShowName(Boolean.parseBoolean(valores[6]));
        this.setGenerateStats(Boolean.parseBoolean(valores[7]));
        int posX = Integer.parseInt(valores[8]);
        int posY = Integer.parseInt(valores[9]);
        this.setScreenPosition(new Point(posX + 24, posY + 24));
        this.targetIPv4Address = valores[10];
        this.setRequestBackupLSP(Boolean.parseBoolean(valores[11]));
        this.setGoSLevel(Integer.parseInt(valores[12]));
        this.encapsulateOverMPLS(Boolean.parseBoolean(valores[13]));
        this.setTrafficGenerationRate(Integer.parseInt(valores[14]));
        this.setTrafficGenerationMode(Integer.parseInt(valores[15]));
        this.setConstantPayloadSizeInBytes(Integer.parseInt(valores[16]));
        return true;
    }

    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser
     * creador por el constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        this.packetIdentifierGenerator.reset();
        this.packetGoSdentifierGenerator.reset();
        this.ports.reset();
        this.stats.reset();
        this.stats.activateStats(this.isGeneratingStats());
        this.resetTicksWithoutEmitting();
    }

    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
     *
     * @return Las estad�sticas del nodo.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public TStats getStats() {
        return this.stats;
    }

    /**
     * Este m�todo inicia el conjunto de ports del nodo, con el n�mero de ports
     * especificado.
     *
     * @param numPorts N�mero de ports que tendr� el nodo. Como m�ximo est�
     * configurado para 8.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public synchronized void setPorts(int numPorts) {
        this.ports = new TFIFOPortSet(numPorts, this);
    }

    /**
     * Este m�todo no hace nada en un Emisor. En un nodo activo permitir�
     * solicitar a un nodo activo la retransmisi�n de un paquete.
     *
     * @param mplsPacket Paquete cuya retransmisi�n se est� solicitando.
     * @param outgoingPortID Puerto por el que se enviar� la solicitud.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void runGPSRP(TMPLSPDU mplsPacket, int outgoingPortID) {
    }

    private String targetIPv4Address;
    private int trafficGenerationRate;
    private int trafficGenerationMode;
    private boolean encapsulateOverMPLS;
    private int gosLevel;
    private boolean requestBackupLSP;
    private Random randomNumberGenerator;
    private int sendingLabel;
    private TRotaryIDGenerator packetGoSdentifierGenerator;
    private int constantPayloadSizeInBytes;
    private int variablePayloadSizeInBytes;
    private TLongIDGenerator packetIdentifierGenerator;

    public TSenderStats stats;

    public static final int CONSTANT_TRAFFIC_RATE = 0;
    public static final int VARIABLE_TRAFFIC_RATE = 1;

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int NAME_ALREADY_EXISTS = 2;
    public static final int ONLY_BLANK_SPACES = 3;
    public static final int TARGET_UNREACHABLE = 4;
}
