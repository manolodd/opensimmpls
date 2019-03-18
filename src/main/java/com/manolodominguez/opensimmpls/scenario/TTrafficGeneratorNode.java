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
import com.manolodominguez.opensimmpls.commons.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.commons.TLongIDGenerator;
import com.manolodominguez.opensimmpls.commons.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.awt.Point;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * This class implements a sender node; a node that only generates traffic.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTrafficGeneratorNode extends TNode implements ITimerEventListener, Runnable {

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
    public TTrafficGeneratorNode(int nodeID, String ipv4Address, TLongIDGenerator identifierGenerator, TTopology topology) {
        super(nodeID, ipv4Address, identifierGenerator, topology);
        // FIX: This method is overridable. Avoid using this method to update
        // the number of ports or make it final.
        this.setPorts(super.DEFAULT_NUM_PORTS_TRAFFIC_GENERATOR);
        this.packetIdentifierGenerator = new TLongIDGenerator();
        this.packetGoSdentifierGenerator = new TRotaryIDGenerator();
        this.targetIPv4Address = "";
        // FIX: Use class constants instead of harcoded values for all cases.
        this.trafficGenerationRate = 10;
        this.trafficGenerationMode = TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE;
        this.encapsulateOverMPLS = false;
        this.gosLevel = 0;
        this.requestBackupLSP = false;
        this.randomNumberGenerator = new Random();
        this.sendingLabel = (16 + randomNumberGenerator.nextInt(1000000));
        this.constantPayloadSizeInBytes = 0;
        this.variablePayloadSizeInBytes = 0;
        this.stats = new TTrafficGeneratorStats();
        // FIX: This method is overridable. Avoid using this method to update
        // the number of ports or make it final.
        this.stats.setStatsEnabled(this.isGeneratingStats());
        this.translations = ResourceBundle.getBundle(AvailableBundles.TRAFFIC_GENERATOR_NODE.getPath());
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
        return TNode.TRAFFIC_GENERATOR;
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
        this.setTickDurationInNs(timerEvent.getTickDurationInNs());
        this.setCurrentTimeInstant(timerEvent.getUpperLimit());
        this.availableNanoseconds += timerEvent.getTickDurationInNs();
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
            this.generateSimulationEvent(new TSimulationEventNodeCongested(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), 0));
        } catch (Exception e) {
            // FIX: this is not a good practice. Avoid.
            e.printStackTrace();
        }
        TAbstractPDU packetAux = createEmptyPacket();
        boolean aPacketWasGenerated = false;
        while (getMaxTransmittableOctetsWithCurrentAvailableNs() > getNextPacketTotalSizeInBytes(packetAux)) {
            aPacketWasGenerated = true;
            generateAndSendPacket();
        }
        packetAux = null;
        if (aPacketWasGenerated) {
            this.resetTicksWithoutEmitting();
        } else {
            this.increaseTicksWithoutEmitting();
        }
        this.stats.groupStatsByTimeInstant(this.getCurrentTimeInstant());
    }

    /**
     * This method gets the payload size of the next packet, according to the
     * generation mode selected for this sender.
     *
     * @return Payload size size in octects.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getNextPacketPayloadSizeInBytes() {
        if (this.trafficGenerationMode == TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE) {
            return this.constantPayloadSizeInBytes;
        }
        return this.variablePayloadSizeInBytes;
    }

    /**
     * This method gets the header size of the next packet. The packet is
     * specified as an argument.
     *
     * @param packet Packet whose header size has to be computed.
     * @return Header size in octects.
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
     * This method computes the total size of the next packet. It takes into
     * account the headers of the packets that is passed out as an argument and
     * also the payload size of the next packet that the sender has already
     * computed.
     *
     * @param packet Packet whose total size wants to be computed to be the next
     * packet.
     * @return The total size of the next packet, in octets..
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
     * This method generate and send a packet taking int account the
     * configuration of this sender: mode of traffic generation, packet size,
     * etc.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void generateAndSendPacket() {
        TAbstractPDU emptyPacket = null;
        TAbstractPDU packetWithPayload = null;
        // FIX: avoid using harcoded values. Use class constants instead.
        TPort port = this.ports.getPort(0);
        if (port != null) {
            if (!port.isAvailable()) {
                emptyPacket = createEmptyPacket();
                packetWithPayload = this.addDataToEmptyPacket(emptyPacket);
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
                        this.generateSimulationEvent(new TSimulationEventPacketGenerated(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packetType, packetWithPayload.getSize()));
                        this.generateSimulationEvent(new TSimulationEventPacketSent(this, this.eventIdentifierGenerator.getNextIdentifier(), this.getCurrentTimeInstant(), packetType));
                    } catch (Exception e) {
                        // FIX: this is not a good practice. Avoid.
                        e.printStackTrace();
                    }
                    if (this.topology.getFloydWarsallNextHopIPv4Address(this.getIPv4Address(), this.getTargetIPv4Address()) != null) {
                        port.putPacketOnLink(packetWithPayload, port.getLink().getDestinationOfTrafficSentBy(this));
                    } else {
                        discardPacket(packetWithPayload);
                    }
                }
            }
        }
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to generate a single bit. This is something that depends on the
     * generation rate the sender is configured for.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the number of nanoseconds that are needed to generate a single
     * bit.
     * @since 2.0
     */
    public double getRequiredNsPerBit() {
        // FIX: Do not use harcoded values. Use class constants instead.
        long traficcRateInBps = (long) (this.trafficGenerationRate * 1048576L);
        // FIX: Do not use harcoded values. Use class constants instead.
        double requiredNsPerBit = (double) ((double) 1000000000.0 / (long) traficcRateInBps);
        return requiredNsPerBit;
    }

    /**
     * This method computes and returns the number of nanoseconds that are
     * needed to generate the specified number of octects. This is something
     * that depends on the generation rate the sender is configured for.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param octects the number of octects that wants to be generated.
     * @return the number of nanoseconds that are needed to generate the
     * specified number of octects.
     * @since 2.0
     */
    public double getNsRequiredForAllOctets(int octects) {
        double requiredNsPerBit = getRequiredNsPerBit();
        // FIX: Do not use harcoded values. Use class constants instead.
        long numberOfBitsToBeSent = (long) ((long) octects * (long) 8);
        return (double) ((double) requiredNsPerBit * (long) numberOfBitsToBeSent);
    }

    /**
     * This method gets the number of bits that this sender can switch with the
     * available number of nanoseconds it has. The higher the generation
     * ratepower the sender has, the more bits it can generate with the same
     * number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of bits that can be generated.
     * @since 2.0
     */
    public int getMaxTransmittableBitsWithCurrentAvailableNs() {
        double requiredNsPerBit = getRequiredNsPerBit();
        double maxTransmittableBitsWithCurrentAvailableNs = (double) ((double) this.availableNanoseconds / (double) requiredNsPerBit);
        return (int) maxTransmittableBitsWithCurrentAvailableNs;
    }

    /**
     * This method gets the number of octects that this sender can generate with
     * the available number of nanoseconds it has. The higher the generation
     * ratepower the sender has, the more octects it can generate with the same
     * number of nanoseconds.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of octects that can be switched.
     * @since 2.0
     */
    public int getMaxTransmittableOctetsWithCurrentAvailableNs() {
        // FIX: Do not use harcoded values. Use class constants instead.
        double maxTransmittableOctetsWithCurrentAvailableNs = ((double) getMaxTransmittableBitsWithCurrentAvailableNs() / (double) 8.0);
        return (int) maxTransmittableOctetsWithCurrentAvailableNs;
    }

    /**
     * This method computes the payload size of the next packet to be generated.
     * If the sender node is configure to generate constant traffic, then the
     * computed payload will be the one for wich the sender was configured. I
     * the sender is configured to generate variable traffic, then the payload
     * size of the next packet to be generated is computed through a probability
     * distribution function as follows:
     *
     * Payload lesser than 100 octects = probability 47%. Payload between 100
     * octects and 1400 octects = probability 24%. Payload between 1400 octects
     * and 1500 octects = probability 18%. Payload greater or equal to 1500
     * octects = probability 1%.
     *
     * This distribution has been extracted from ststistics of Abilene Network,
     * publicly available at
     *
     * http://netflow.internet2.edu/weekly.
     *
     * To know more, read the "Guarentee of Service (GoS) Support over MPLS
     * using active techiques".
     *
     * @return The correct payload size (in octecs) of the next packet to be
     * generated, according to the configuration of this sender node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int computeNextPacketPayloadSize() {
        // FIX: Do not use harcoded values. Use class constants instead. Do it 
        // for all cases in this method.
        int probability = this.randomNumberGenerator.nextInt(100);
        int payloadSize = 0;
        if (probability < 47) {
            payloadSize = this.randomNumberGenerator.nextInt(100);
            payloadSize -= 40;
        } else if ((probability >= 47) && (probability < 71)) {
            payloadSize = (this.randomNumberGenerator.nextInt(1300) + 100);
            payloadSize -= 40;
        } else if ((probability >= 71) && (probability < 99)) {
            payloadSize = (this.randomNumberGenerator.nextInt(100) + 1400);
            payloadSize -= 40;
        } else if (probability >= 99) {
            payloadSize = (this.randomNumberGenerator.nextInt(64035) + 1500);
            payloadSize -= 40;
        }
        return payloadSize;
    }

    /**
     * This method gets an empty packet and insert a payload in it. It is used
     * to generate packets that match the size the sender node is configured
     * for.
     *
     * @param packet An empty packet to embed a payload in it.
     * @return A new packet including the corresponding headers and the correct
     * payload size.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TAbstractPDU addDataToEmptyPacket(TAbstractPDU packet) {
        TMPLSPDU mplsPacket = null;
        TIPv4PDU ipv4Packet = null;
        // FIX: The two following variables seem not to be used. Check and remove if it is safe.
        int maxTransmittableBits = getMaxTransmittableBitsWithCurrentAvailableNs();
        int nextPacketHeaderSizeInBytes = 0;
        int NextPacketPayloadSizeInBytes = 0;
        int nextPacketTotalSizeInBytes = 0;
        double requiredNs = 0;
        nextPacketTotalSizeInBytes = getNextPacketTotalSizeInBytes(packet);
        nextPacketHeaderSizeInBytes = getNextPacketHeaderSizeInBytes(packet);
        NextPacketPayloadSizeInBytes = getNextPacketPayloadSizeInBytes();
        if (nextPacketTotalSizeInBytes > getMaxTransmittableOctetsWithCurrentAvailableNs()) {
            packet = null;
            return null;
        } else if (packet.getType() == TAbstractPDU.MPLS) {
            mplsPacket = (TMPLSPDU) packet;
            mplsPacket.getTCPPayload().setSize((int) NextPacketPayloadSizeInBytes);
            requiredNs = this.getNsRequiredForAllOctets(nextPacketTotalSizeInBytes);
            this.availableNanoseconds -= requiredNs;
            // FIX: Do not use harcoded values. Use class constants instead.
            if (this.availableNanoseconds < 0) {
                // FIX: Do not use harcoded values. Use class constants instead.
                this.availableNanoseconds = 0;
            }
            if (this.trafficGenerationMode == TTrafficGeneratorNode.VARIABLE_TRAFFIC_RATE) {
                this.variablePayloadSizeInBytes = this.computeNextPacketPayloadSize();
            }
            return mplsPacket;
        } else if (packet.getType() == TAbstractPDU.IPV4) {
            ipv4Packet = (TIPv4PDU) packet;
            ipv4Packet.getTCPPayload().setSize(NextPacketPayloadSizeInBytes);
            requiredNs = this.getNsRequiredForAllOctets(nextPacketTotalSizeInBytes);
            this.availableNanoseconds -= requiredNs;
            // FIX: Do not use harcoded values. Use class constants instead.
            if (this.availableNanoseconds < 0) {
                // FIX: Do not use harcoded values. Use class constants instead.
                this.availableNanoseconds = 0;
            }
            if (this.trafficGenerationMode == TTrafficGeneratorNode.VARIABLE_TRAFFIC_RATE) {
                this.variablePayloadSizeInBytes = this.computeNextPacketPayloadSize();
            }
            return ipv4Packet;
        }
        return null;
    }

    /**
     * This method generates and returns an empty packet that match exactly the
     * type of packets expected due to the configuration of this sender node.
     * The payload of this packet is 0.
     *
     * @return A new empty packet, ready to be asigned a given payload.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TAbstractPDU createEmptyPacket() {
        int requiredEXPValue = this.getRequiredEXPValue();
        try {
            if (this.encapsulateOverMPLS) {
                if (requiredEXPValue == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    TMPLSPDU mplsPacket = new TMPLSPDU(this.packetIdentifierGenerator.getNextIdentifier(), getIPv4Address(), this.targetIPv4Address, 0);
                    TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
                    // FIX: Use class constants instead of harcoded values
                    outgoingMPLSLabel.setBoS(true);
                    // FIX: Use class constants instead of harcoded values
                    outgoingMPLSLabel.setEXP(0);
                    outgoingMPLSLabel.setLabel(this.sendingLabel);
                    outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
                    mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
                    return mplsPacket;
                } else {
                    TMPLSPDU mplsPacket = new TMPLSPDU(packetIdentifierGenerator.getNextIdentifier(), getIPv4Address(), this.targetIPv4Address, 0);
                    mplsPacket.setSubtype(TAbstractPDU.MPLS_GOS);
                    mplsPacket.getIPv4Header().getOptionsField().setRequestedGoSLevel(requiredEXPValue);
                    mplsPacket.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(this.packetGoSdentifierGenerator.getNextIdentifier());
                    TMPLSLabel bottomOutgoingMPLSLabel = new TMPLSLabel();
                    // FIX: Use class constants instead of harcoded values
                    bottomOutgoingMPLSLabel.setBoS(true);
                    // FIX: Use class constants instead of harcoded values
                    bottomOutgoingMPLSLabel.setEXP(0);
                    bottomOutgoingMPLSLabel.setLabel(this.sendingLabel);
                    bottomOutgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
                    TMPLSLabel upperOutgoingMPLSLabel = new TMPLSLabel();
                    // FIX: Use class constants instead of harcoded values
                    upperOutgoingMPLSLabel.setBoS(false);
                    upperOutgoingMPLSLabel.setEXP(requiredEXPValue);
                    // FIX: Use class constants instead of harcoded values
                    upperOutgoingMPLSLabel.setLabel(1);
                    upperOutgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
                    mplsPacket.getLabelStack().pushTop(bottomOutgoingMPLSLabel);
                    mplsPacket.getLabelStack().pushTop(upperOutgoingMPLSLabel);
                    return mplsPacket;
                }
            } else if (requiredEXPValue == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                TIPv4PDU ipv4Packet = new TIPv4PDU(packetIdentifierGenerator.getNextIdentifier(), getIPv4Address(), this.targetIPv4Address, 0);
                return ipv4Packet;
            } else {
                TIPv4PDU ipv4Packet = new TIPv4PDU(packetIdentifierGenerator.getNextIdentifier(), getIPv4Address(), this.targetIPv4Address, 0);
                ipv4Packet.setSubtype(TAbstractPDU.IPV4_GOS);
                ipv4Packet.getIPv4Header().getOptionsField().setRequestedGoSLevel(requiredEXPValue);
                ipv4Packet.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(this.packetGoSdentifierGenerator.getNextIdentifier());
                return ipv4Packet;
            }
        } catch (EIDGeneratorOverflow e) {
            // FIX: This is ugly. Avoid.
            e.printStackTrace();
        }
        return null;
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
            // FIX: This is ugly. Avoid.
            e.printStackTrace();
        }
        packet = null;
    }

    /**
     * This method checks whether the node has still free ports.
     *
     * @return TRUE, if the node has still free ports. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
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
     * This method gets the weight to be used in routing algorithm. By design,
     * ha sender node has a weight of 0.
     *
     * @return for this type of node, alsways 0.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public long getRoutingWeight() {
        // FIX: Use class constants instead of harcoded values
        return 0;
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
     * @return TSenderNode.OK if the configuration is correct. Otherwise, an
     * error code is returned. See public constants of error codes in this
     * class.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        // FIX: Do not use harcoded values. Use class constants instead.
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TTrafficGeneratorNode.UNNAMED;
        }
        boolean onlyBlankSpaces = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                onlyBlankSpaces = false;
            }
        }
        if (onlyBlankSpaces) {
            return TTrafficGeneratorNode.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TNode nodeAux = topology.getFirstNodeNamed(this.getName());
            if (nodeAux != null) {
                return TTrafficGeneratorNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode nodeAux = topology.getFirstNodeNamed(this.getName());
            if (nodeAux != null) {
                if (this.topology.isThereMoreThanANodeNamed(this.getName())) {
                    return TTrafficGeneratorNode.NAME_ALREADY_EXISTS;
                }
            }
        }

        if (this.getTargetIPv4Address() == null) {
            return TTrafficGeneratorNode.TARGET_UNREACHABLE;
        }
        if (this.getTargetIPv4Address().equals("")) {
            return TTrafficGeneratorNode.TARGET_UNREACHABLE;
        }
        this.setWellConfigured(true);
        return TTrafficGeneratorNode.OK;
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
                return (this.translations.getString("TConfigEmisor.FALTA_NOMBRE"));
            case NAME_ALREADY_EXISTS:
                return (this.translations.getString("TConfigEmisor.NOMBRE_REPETIDO"));
            case ONLY_BLANK_SPACES:
                return (this.translations.getString("TNodoEmisor.NoSoloEspacios"));
            case TARGET_UNREACHABLE:
                return (this.translations.getString("TNodoEmisor.DestinoParaElTrafico"));
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
        String serializedElement = "#Emisor#";
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
        serializedElement += this.getTargetIPv4Address();
        serializedElement += "#";
        serializedElement += this.isRequestingBackupLSP();
        serializedElement += "#";
        serializedElement += this.getGoSLevel();
        serializedElement += "#";
        serializedElement += this.isEncapsulatingOverMPLS();
        serializedElement += "#";
        serializedElement += this.getTrafficGenerationRate();
        serializedElement += "#";
        serializedElement += this.getTrafficGenerationMode();
        serializedElement += "#";
        serializedElement += this.getConstantPayloadSizeInBytes();
        serializedElement += "#";
        return serializedElement;
    }

    /**
     * This method gets as an argument a serialized string that contains the
     * needed parameters to configure an TSenderNode and configure this node
     * using them.
     *
     * @param serializedSender A serialized representation of a TSenderNode.
     * @return true, whether the serialized string is correct and this node has
     * been initialized correctly using the serialized values. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public boolean fromOSMString(String serializedSender) {
        // FIX: All fixed values in this method should be implemented as class
        // constants instead of harcoded values.
        String[] elementFields = serializedSender.split("#");
        if (elementFields.length != 17) {
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
        this.targetIPv4Address = elementFields[10];
        this.setRequestBackupLSP(Boolean.parseBoolean(elementFields[11]));
        this.setGoSLevel(Integer.parseInt(elementFields[12]));
        this.encapsulateOverMPLS(Boolean.parseBoolean(elementFields[13]));
        this.setTrafficGenerationRate(Integer.parseInt(elementFields[14]));
        this.setTrafficGenerationMode(Integer.parseInt(elementFields[15]));
        this.setConstantPayloadSizeInBytes(Integer.parseInt(elementFields[16]));
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
        this.stats.setStatsEnabled(this.isGeneratingStats());
        this.resetTicksWithoutEmitting();
    }

    /**
     * This node gets the stats of this node.
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
     * @param numPorts Number of ports of this node. The maximum allowed for a
     * sender node is 1.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public synchronized void setPorts(int numPorts) {
        this.ports = new TFIFOPortSet(numPorts, this);
    }

    /**
     * This method does nothing in an sender node.
     *
     * @param mplsPacket Packet whose retransmission is being requested.
     * @param outgoingPortID outgoing port ID to wich the packet will be
     * delivered.
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

    public TTrafficGeneratorStats stats;

    public static final int CONSTANT_TRAFFIC_RATE = 0;
    public static final int VARIABLE_TRAFFIC_RATE = 1;

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int NAME_ALREADY_EXISTS = 2;
    public static final int ONLY_BLANK_SPACES = 3;
    public static final int TARGET_UNREACHABLE = 4;

    private ResourceBundle translations;
}
