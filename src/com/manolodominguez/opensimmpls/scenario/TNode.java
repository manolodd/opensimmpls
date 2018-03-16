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

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.TLock;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;

/**
 * This class implements a node of the topology. It is an abstract class that
 * has to be implemented by all subclassess.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TNode extends TTopologyElement implements Comparable, ITimerEventListener, Runnable {

    /**
     * This is the constructor of the class. It will be called from all
     * subclassess to create a new instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param nodeID The node identifier that is unique in the topology.
     * @param ipv4Address IPv4 address of the node.
     * @param identifierGenerator An identifier generator that will be used to
     * generate unique identifiers for events.
     * @param topology Topology the node belongs to.
     */
    public TNode(int nodeID, String ipv4Address, TLongIDGenerator identifierGenerator, TTopology topology) {
        super(TTopologyElement.NODE, identifierGenerator);
        // FIX: Do not use harcoded values. Use class constants in all cases.
        this.screenPosition = new Point(0, 0);
        this.nodeID = nodeID;
        this.name = "";
        this.selected = UNSELECTED;
        this.showName = false;
        this.ipv4Address = ipv4Address;
        this.ports = null;
        this.lock = new TLock();
        this.topology = topology;
        this.generateStatistics = false;
        this.availableNs = 0;
        this.tickNanoseconds = 0;
        this.ticksWithoutEmittingBeforeAlert = 0;
    }

    /**
     * This method sets whether the node has to generate statistics or not.
     *
     * @param generateStatistics TRUE, if the node has to generate statistics.
     * Otherwise, FALSE.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public void setGenerateStats(boolean generateStatistics) {
        this.generateStatistics = generateStatistics;
    }

    /**
     * This method gets whether the node has to generate statistics or not.
     *
     * @return TRUE, if the node has to generate statistics. Otherwise, FALSE.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public boolean isGeneratingStats() {
        return this.generateStatistics;
    }

    /**
     * This method compares the current instance to another instance of TNode to
     * know the ordinal position of one to respect the other.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherNode a TNode instance to be compared to the current one.
     * @return -1, 0 or 1 depending on whether the current instance is lesser,
     * equal or greater than the one specified as an argument.
     * @since 2.0
     */
    @Override
    public int compareTo(Object anotherNode) {
        TNode nodeAux = (TNode) anotherNode;
        if (getNodeID() < nodeAux.getNodeID()) {
            return -1;
        } else if (getNodeID() == nodeAux.getNodeID()) {
            return 0;
        }
        return 1;
    }

    /**
     * This method allow setting the topology to wich this node belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param topology topology to wich this node belongs to.
     * @since 2.0
     */
    public void setTopology(TTopology topology) {
        this.topology = topology;
    }

    /**
     * This method allow getting the topology to wich this node belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return topology to wich this node belongs to.
     * @since 2.0
     */
    public TTopology getTopology() {
        return this.topology;
    }

    /**
     * This method gets the name of the node.
     *
     * @return the name of the node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method sets the name of the node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param name the name of the node.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method gets the unique identifier of the node.
     *
     * @return the unique identifier of the node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getNodeID() {
        return this.nodeID;
    }

    /**
     * This method sets the unique identifier of the node.
     *
     * @param nodeID the unique identifier of the node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * This method gets the position of this node in the simulator screen.
     *
     * @return the position of this node in the simulator screen.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public Point getScreenPosition() {
        return this.screenPosition;
    }

    /**
     * This method sets the position of this node in the simulator screen.
     *
     * @param screenPosition the position of this node in the simulator screen.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setScreenPosition(Point screenPosition) {
        this.screenPosition.x = screenPosition.x - (TNode.ICONS_WIDTH / 2);
        this.screenPosition.y = screenPosition.y - (TNode.ICONS_HEIGHT / 2);
    }

    /**
     * This method allow knowing whether a given screen position match the
     * position of the node in the simulator screen or not.
     *
     * @param screenPosition screen position.
     * @return TRUE, if the screen position match the position of the node in
     * the simulator screen. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isInScreenPosition(Point screenPosition) {
        if ((screenPosition.x >= this.screenPosition.x) && (screenPosition.x <= (this.screenPosition.x + TNode.ICONS_WIDTH))
                && (screenPosition.y >= this.screenPosition.y) && (screenPosition.y <= (this.screenPosition.y + TNode.ICONS_HEIGHT))) {
            return true;
        }
        return false;
    }

    /**
     * This method allow knowing whether the node is currently selected or not.
     *
     * @return TNode.SELECTED, if the node is selected in the simulator.
     * Otherwise, TNode.UNSELECTED.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int isSelected() {
        return this.selected;
    }

    /**
     * This method sets whether the node is currently selected or not.
     *
     * @param selected TNode.SELECTED, if the node is selected in the simulator.
     * Otherwise, TNode.UNSELECTED.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setSelected(int selected) {
        this.selected = selected;
    }

    /**
     * This method sets whether the name of the node has to be shown in the
     * simulator screen or not.
     *
     * @param showName TRUE, if the name of the node has to be displayed on the
     * simulator screen. Oterwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setShowName(boolean showName) {
        if (name.length() == 0) {
            this.showName = false;
        } else {
            this.showName = showName;
        }
    }

    /**
     * This method allow knowing whether the name of the node has to be shown in
     * the simulator screen or not.
     *
     * @return TRUE, if the name of the node has to be displayed on the
     * simulator screen. Oterwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean nameMustBeDisplayed() {
        return this.showName;
    }

    /**
     * This method sets the IPv4 address of the node.
     *
     * @return the IPv4 address of the node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String getIPv4Address() {
        return this.ipv4Address;
    }

    /**
     * This method gets the IPv4 address of the node.
     *
     * @param ipv4Address the IPv4 address of the node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setIPv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    /**
     * This method sets the number of ports of this node.
     *
     * @param numPorts the number of ports of this node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract void setPorts(int numPorts);

    /**
     * This method put a packet in the incoming port of the node.
     *
     * @param packet Packet that arrives the node.
     * @param portID Port ID of port of this node where the packet is put.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void putPacket(TAbstractPDU packet, int portID) {
        lock.lock();
        this.ports.getPort(portID).addPacket(packet);
        lock.unLock();
    }

    /**
     * This method increases in 1 the number of ticks that the node has been
     * without emitting a packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void increaseTicksWithoutEmitting() {
        this.ticksWithoutEmittingBeforeAlert++;
    }

    /**
     * This method sets to 0 the number of ticks that the node has been without
     * emitting a packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void resetTicksWithoutEmitting() {
        this.ticksWithoutEmittingBeforeAlert = 0;
    }

    /**
     * This method gets the number of ticks that the node has been without
     * emitting a packet.
     *
     * @return the number of ticks that the node has been without emitting a
     * packet
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getTicksWithoutEmitting() {
        return this.ticksWithoutEmittingBeforeAlert;
    }

    /**
     * This method discard a packet from this node.
     *
     * @param packet packet to be discarded.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract void discardPacket(TAbstractPDU packet);

    /**
     * This method gets the ports set of this node.
     *
     * @return the ports set of this node.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract TPortSet getPorts();

    /**
     * This method receives a timer event to do things. It should be implemented
     * by subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param timerEvent The timer event that triggers the link operation.
     * @since 2.0
     */
    @Override
    public abstract void receiveTimerEvent(TTimerEvent timerEvent);

    /**
     * This method gets the node type. It should be implemented by subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A node type that should be defined as a constant in this class.
     * @since 2.0
     */
    public abstract int getNodeType();

    /**
     * This method is called once the node receives a timer event and has to do
     * things in its own thread. To be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public abstract void run();

    /**
     * This method start the GPSRP protocol to request the retransmission of a
     * lost packet part of wich has been recovered before discarding it. It has
     * to be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsPacket incomplete MPLS packet that want to be recovered
     * locally.
     * @param outgoingPortID Port of this node through wich the GPSRP
     * retransmission request is going to be sent.
     * @since 2.0
     */
    // FIX: This should be an abstract method of a new abstract class called
    // TActiveNode. TLERNode and TLSRNode should inherit from TNode and 
    // TActiveLERNode and TActiveLSRNode should inherit from this new 
    // TActiveNode class
    public abstract void runGPSRP(TMPLSPDU mplsPacket, int outgoingPortID);

    /**
     * This method checks whether the node has available ports or not. It should
     * be implemented by all subclasses.
     *
     * @return true, if the node has available ports. Otherwise, false.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public abstract boolean hasAvailablePorts();

    /**
     * This method computes the routing weight of this node, to be used by
     * routing algorithms. This has to be implemented by all subclasses.
     *
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the routing weight of this node.
     */
    public abstract long getRoutingWeight();

    /**
     * This method checks whether the node is well configured or not. To be
     * implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the node is well configured. Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public abstract boolean isWellConfigured();

    /**
     * This method checks whether the node is configured correctly or not. It
     * has to be implemented by all subclasses.
     *
     * @param topology Topology the node belongs to.
     * @param reconfiguration TRUE, if the node is being re-configured.
     * Otherwise, FALSE.
     * @return 0 if the configuration is correct. Otherwise, an error code is
     * returned. See public constants of error codes in the implementing
     * subclasses.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    // FIX: TNode.OK with value 0 should be created in this class. The rest of
    // error codes can be defined in subclasses. 
    public abstract int validateConfig(TTopology topology, boolean reconfiguration);

    /**
     * This method generates an human-readable error message from a given error
     * code specified as an argument. To be implemented by all subclassess.
     *
     * @param errorCode the error code to witch the text message has to be
     * generated. One of the public constants defined in the corresponding
     * implementing subclasses.
     * @return an String explaining the error.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public abstract String getErrorMessage(int errorCode);

    /**
     * This method serializes the configuration parameters of this node into an
     * string that can be saved into disk. To be implemented by all subclassess.
     *
     * @return an String containing all the configuration parameters of this
     * node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public abstract String marshall();

    /**
     * This method gets as an argument a serialized string that contains the
     * needed parameters to configure the node. It will be used by subclasses to
     * configure this node using them.
     *
     * @param serializedElement A serialized representation of a node.
     * @return TRUE, whether the serialized string is correct and this node has
     * been initialized correctly using the serialized values. Otherwise, FALSE.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    @Override
    public abstract boolean unMarshall(String serializedElement);

    /**
     * This node gets the stat of this node. It has to be implemented by all
     * subclasses.
     *
     * @return The stats of this node.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public abstract TStats getStats();

    /**
     * This method restart the attributes of the class as in the creation of the
     * instance. It has to be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public abstract void reset();

    private static final int ICONS_WIDTH = 48;
    private static final int ICONS_HEIGHT = 48;

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;
    public static final int LER = 2;
    public static final int ACTIVE_LER = 3;
    public static final int LSR = 4;
    public static final int ACTIVE_LSR = 5;

    public static final int UNSELECTED = 0;
    public static final int SELECTED = 1;

    public static final int DEFAULT_NUM_PORTS_SENDER = 1;
    public static final int DEFAULT_NUM_PORTS_RECEIVER = 1;
    public static final int DEFAULT_NUM_PORTS_LER = 8;
    public static final int DEFAULT_NUM_PORTS_ACTIVE_LER = 8;
    public static final int DEFAULT_NUM_PORTS_LSR = 8;
    public static final int DEFAULT_NUM_PORTS_ACTIVE_LSR = 8;

    public static final int MAX_STEP_WITHOUT_EMITTING_BEFORE_ALERT = 25;

    private int nodeID;
    private int selected;
    private String name;
    private Point screenPosition;
    private boolean showName;
    private String ipv4Address;
    private TLock lock;
    private boolean generateStatistics;
    // FIX: Do not use harcoded values. Use class constant.
    private int ticksWithoutEmittingBeforeAlert = 0;

    protected TPortSet ports;
    protected TTopology topology;
    protected int tickNanoseconds;
}
