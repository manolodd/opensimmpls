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

import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketReceived;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPortSet;
import com.manolodominguez.opensimmpls.hardware.ports.TPort;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;

/**
 * This class implements a receiver node; a node that only receives traffic.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTrafficSinkNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * This is the constructor of the class and creates a new instance of
     * TReceiverNode.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param nodeID The node identifier that is unique in the topology.
     * @param ipv4Address IPv4 address of the node.
     * @param identifierGenerator An identifier generator that will be used to
     * generate unique identifiers for events.
     * @param topology Topology the node belongs to.
     */
    public TTrafficSinkNode(int nodeID, String ipv4Address, TLongIDGenerator identifierGenerator, TTopology topology) {
        super(nodeID, ipv4Address, identifierGenerator, topology);
        // FIX: This method is overridable. Avoid using this method to update
        // the number of ports or make it final.
        this.setPorts(TNode.DEFAULT_NUM_PORTS_TRAFFIC_SINK);
        // FIX: Use class constants instead of harcoded values.
        this.ports.setUnlimitedBuffer(true);
        this.stats = new TTrafficSinkStats();
    }

    /**
     * Este m�todo reinicia los atributos de la clase y los deja como recien
     * creadops por el constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        this.ports.reset();
        this.stats.reset();
        this.stats.setStatsEnabled(this.isGeneratingStats());
    }

    /**
     * Este m�todo devuelve el tipo del nodo.
     *
     * @return TNode.RECEIVER, indicando que se trata de un nodo receptor.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public int getNodeType() {
        return TNode.TRAFFIC_SINK;
    }

    /**
     * Este m�todo permite obtener eventos enviados desde el reloj del
     * simulador.
     *
     * @param evt Evento enviado desde el reloj principal del simulador.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent evt) {
        this.setTickDurationInNs(evt.getTickDurationInNs());
        this.setCurrentTimeInstant(evt.getUpperLimit());
        this.startOperation();
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el hilo independiente del
     * nodo se pone en funcionamiento. En �l se codifica toda la funcionalidad
     * del nodo.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void run() {
        // Actions to during the duration of the tick.
        receivePackets();
        this.stats.groupStatsByTimeInstant(this.getCurrentTimeInstant());
    }

    /**
     * This method receives, while possible, packets from the receiving buffer.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void receivePackets() {
        // FIX: do not use harcoded values. Use class constants instead in all
        // cases.
        TPort incomingPort = this.ports.getPort(0);
        long eventID = 0;
        int eventType = 0;
        TAbstractPDU incomingPacket = null;
        TSimulationEventPacketReceived packetReceivedEvent = null;
        if (incomingPort != null) {
            while (incomingPort.thereIsAPacketWaiting()) {
                incomingPacket = incomingPort.getPacket();
                try {
                    eventID = this.eventIdentifierGenerator.getNextID();
                } catch (Exception e) {
                    // FIX: This is ugly
                    e.printStackTrace();
                }
                // FIX: The following line has no effects as the method called
                // does nothing. Check whether it is needed or not. If needed, 
                // do not use harcoded values. Use class constants instead.
                this.accountPacket(incomingPacket, true);
                packetReceivedEvent = new TSimulationEventPacketReceived(this, eventID, this.getCurrentTimeInstant(), eventType, incomingPacket.getSize());
                this.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
                incomingPacket = null;
            }
        }
    }

    /**
     * This method register in the stats of this the node a new received packet.
     *
     * @param packet Packet to be registered.
     * @param isIncomingPacket TRUE, if the packet is an incoming packet.
     * Oterwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: This method does nothing. Check whether it is needed or not.
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
     * This method gets the ports set of this node.
     *
     * @return the ports set of this node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public TPortSet getPorts() {
        return this.ports;
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
     * This method gets the weight to be used in routing algorithm. By design,
     * ha receiver node has a weight of 0.
     *
     * @return for this type of node, alsways 0.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public long getRoutingWeight() {
        // FIX: use class constants instead of harcoded values.
        return 0;
    }

    /**
     * This method gets whether the node is well configured or not.
     *
     * @return TRUE, if the node is well configured. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
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
     * @return TReceiverNode.OK if the configuration is correct. Otherwise, an
     * error code is returned. See public constants of error codes in this
     * class.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        // FIX: use class constants instead of harcoded values.
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return TTrafficSinkNode.UNNAMED;
        }
        boolean onlyBlankSpaces = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                onlyBlankSpaces = false;
            }
        }
        if (onlyBlankSpaces) {
            return TTrafficSinkNode.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TNode nodeAux1 = topology.getFirstNodeNamed(this.getName());
            if (nodeAux1 != null) {
                return TTrafficSinkNode.NAME_ALREADY_EXISTS;
            }
        } else {
            TNode nodeAux2 = topology.getFirstNodeNamed(this.getName());
            if (nodeAux2 != null) {
                if (this.topology.isThereMoreThanANodeNamed(this.getName())) {
                    return TTrafficSinkNode.NAME_ALREADY_EXISTS;
                }
            }
        }
        // FIX: use class constants instead of harcoded values.
        this.setWellConfigured(true);
        this.stats.setStatsEnabled(this.isGeneratingStats());
        return TTrafficSinkNode.OK;
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
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigReceptor.FALTA_NOMBRE"));
            case NAME_ALREADY_EXISTS:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigReceptor.NOMBRE_REPETIDO"));
            case ONLY_BLANK_SPACES:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TNodoReceptor.NombreNoSoloEspacios"));
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
        // FIX: all harcoded values should be coded as class constants.
        String serializedElement = "#Receptor#";
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
        return serializedElement;
    }

    /**
     * This method gets as an argument a serialized string that contains the
     * needed parameters to configure an TReceiverNode and configure this node
     * using them.
     *
     * @param serializedReceiverNode A serialized representation of a
     * TReceiverNode.
     * @return true, whether the serialized string is correct and this node has
     * been initialized correctly using the serialized values. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public boolean unMarshall(String serializedReceiverNode) {
        // FIX: All fixed values in this method should be implemented as class
        // constants instead of harcoded values.
        String[] elementFields = serializedReceiverNode.split("#");
        if (elementFields.length != 10) {
            return false;
        }
        this.setNodeID(Integer.parseInt(elementFields[2]));
        this.setName(elementFields[3]);
        this.setIPv4Address(elementFields[4]);
        this.setSelected(Integer.parseInt(elementFields[5]));
        this.setShowName(Boolean.valueOf(elementFields[6]));
        this.setGenerateStats(Boolean.valueOf(elementFields[7]));
        int posX = Integer.parseInt(elementFields[8]);
        int posY = Integer.parseInt(elementFields[9]);
        this.setScreenPosition(new Point(posX + 24, posY + 24));
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
     * @param numPorts Number of ports of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public synchronized void setPorts(int numPorts) {
        this.ports = new TFIFOPortSet(numPorts, this);
    }

    /**
     * This method discard a packet from this node.
     *
     * @param packet packet to be discarded.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void discardPacket(TAbstractPDU packet) {
        // In OpenSimMPLS a receiver node does not discard packets because has 
        // an unlimited buffer and traffic is not analized, only received.
        packet = null;
    }

    /**
     * This method start the GPSRP protocol to request the retransmission of a
     * lost packet part of wich has been recovered before discarding it. It does
     * nothing in a receiver node.
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
    }

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int NAME_ALREADY_EXISTS = 2;
    public static final int ONLY_BLANK_SPACES = 3;

    private TTrafficSinkStats stats;
}
