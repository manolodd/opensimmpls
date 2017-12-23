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
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.TMonitor;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is an abstract class that will be implemented by subclasses. It is
 * a generic link within the network topology.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TLink extends TTopologyElement implements Comparable, ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It should be called by
     * subclasses to create a new instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param identifier
     * @param longIDGenerator
     * @param topology
     * @since 2.0
     */
    public TLink(int identifier, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(TTopologyElement.LINK, longIDGenerator);
        this.identifier = identifier;
        this.headEndNode = null;
        this.tailEndNode = null;
        this.showName = false;
        this.name = "";
        this.delay = 1;
        this.headEndNodePortID = -1;
        this.tailEndNodePortID = -1;
        this.buffer = Collections.synchronizedSortedSet(new TreeSet());
        this.deliveredPacketsBuffer = new TreeSet();
        this.packetsInTransitEntriesLock = new TMonitor();
        this.deliveredPacketEntriesLock = new TMonitor();
        this.topology = topology;
        this.linkIsBroken = false;
    }

    /**
     * This method check whether the link is broken or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the link is broken. Otherwise, returns FALSE.
     * @since 2.0
     */
    public boolean isBroken() {
        return this.linkIsBroken;
    }

    /**
     * This method compares the current instance to another instance of TLink to
     * know the ordinal position of one to respect the other.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherLink a TLink instance to be compared to the current one.
     * @return -1, 0 or 1 depending on whether the current instance is lesser,
     * equal or greater than the one specified as an argument.
     * @since 2.0
     */
    @Override
    public int compareTo(Object anotherLink) {
        TLink linkAux = (TLink) anotherLink;
        if (TLink.this.getID() < linkAux.getID()) {
            return -1;
        } else if (TLink.this.getID() == linkAux.getID()) {
            return 0;
        }
        return 1;
    }

    /**
     * This method return the percentage of the total transit delay that a given
     * packet has already been "waiting" before reaching the tail end node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param totalTransitDelay The total transit delay required to go over the
     * link.
     * @param remainingDelay The part of totalTransitDelay that has not yet been
     * covered.
     * @return The percentage of the totalTransitDdelay that has been already
     * covered.
     * @since 2.0
     */
    public long getCurrentTransitPercentage(long totalTransitDelay, long remainingDelay) {
        return ((totalTransitDelay - remainingDelay) * 100) / totalTransitDelay;
    }

    /**
     * This method computes the coordinates of the screen where a given packet
     * has to be displayed knowing the percentaje of the total transit delay
     * that it has been going through the link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param transitPercentage Percentage of the total transit delay that the
     * packet has been going through the link.
     * @return Coordinates of the screen where the packet has to be displayed.
     */
    public Point getScreenPacketPosition(long transitPercentage) {
        Point screenPacketPosition = new Point(0, 0);
        int x1 = this.headEndNode.getScreenPosition().x + 24;
        int y1 = this.headEndNode.getScreenPosition().y + 24;
        int x2 = this.tailEndNode.getScreenPosition().x + 24;
        int y2 = this.tailEndNode.getScreenPosition().y + 24;
        screenPacketPosition.x = x1;
        screenPacketPosition.y = y1;
        int distanceX = (x2 - x1);
        int distanceY = (y2 - y1);
        screenPacketPosition.x += (int) ((double) distanceX * (double) transitPercentage / (double) 100);
        screenPacketPosition.y += (int) ((double) distanceY * (double) transitPercentage / (double) 100);
        return screenPacketPosition;
    }

    /**
     * This method allow setting the topology to wich this link belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param topology topology to wich this link belongs to.
     * @since 2.0
     */
    public void setTopology(TTopology topology) {
        this.topology = topology;
    }

    /**
     * This method gets the topology to wich this link belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The topology to wich this link belongs to.
     * @since 2.0
     */
    public TTopology getTopology() {
        return this.topology;
    }

    /**
     * This method establishes the configuration of the link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param isAReconfiguration TRUE, if the link is being reconfigured. FALSE
     * if the link is being configured for the first time.
     * @param linkConfig The configuration object for this link.
     * @param topology Topology the link belongs to.
     */
    public void configure(TLinkConfig linkConfig, TTopology topology, boolean isAReconfiguration) {
        this.setName(linkConfig.getName());
        this.setShowName(linkConfig.getShowName());
        this.setDelay(linkConfig.getDelay());
        if (!isAReconfiguration) {
            this.setHeadEndNode(topology.getFirstNodeNamed(linkConfig.getHeadEndNodeName()));
            this.setTailEndNode(topology.getFirstNodeNamed(linkConfig.getTailEndNodeName()));
            this.disconnectFromPorts();
            this.setHeadEndNodePortID(linkConfig.getHeadEndNodePortID());
            TPortSet portSetAux1 = this.headEndNode.getPorts();
            if (portSetAux1 != null) {
                portSetAux1.connectLinkToPort(this, this.headEndNodePortID);
            }
            this.setTailEndNodePortID(linkConfig.getTailEndNodePortID());
            TPortSet portSetAux2 = this.tailEndNode.getPorts();
            if (portSetAux2 != null) {
                portSetAux2.connectLinkToPort(this, this.tailEndNodePortID);
            }
        }
    }

    /**
     * This method gets the configuration of this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The configuration object of this link.
     * @since 2.0
     */
    public TLinkConfig getConfig() {
        TLinkConfig linkConfig = new TLinkConfig();
        linkConfig.setName(this.getName());
        linkConfig.setShowName(this.getShowName());
        if (this.getHeadEndNode() != null) {
            linkConfig.setHeadEndNodeName(this.getHeadEndNode().getName());
        }
        if (this.getTailEndNode() != null) {
            linkConfig.setTailEndNodeName(this.getTailEndNode().getName());
        }
        linkConfig.setDelay(this.getDelay());
        linkConfig.setHeadEndNodePortID(this.getHeadEndNodePortID());
        linkConfig.setTailEndNodePortID(this.getTailEndNodePortID());
        return linkConfig;
    }

    /**
     * This method disconnect the link from all ports.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void disconnectFromPorts() {
        if (this.headEndNode != null) {
            TPortSet portSetAux1 = this.headEndNode.getPorts();
            if (portSetAux1 != null) {
                portSetAux1.disconnectLinkFromPort(this.headEndNodePortID);
            }
        }
        if (this.tailEndNode != null) {
            TPortSet portSetAux2 = this.tailEndNode.getPorts();
            if (portSetAux2 != null) {
                portSetAux2.disconnectLinkFromPort(this.tailEndNodePortID);
            }
        }
    }

    /**
     * This method sets the delay of this link
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param delay the delay of this link.
     * @since 2.0
     */
    public void setDelay(int delay) {
        if (delay <= 0) {
            this.delay = 1;
        } else {
            this.delay = delay;
        }
    }

    /**
     * This method gets the delay of this link
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the delay of this link.
     * @since 2.0
     */
    public int getDelay() {
        return this.delay;
    }

    /**
     * This method established the link unique identifier.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param identifier the unique identifier for this link in the topology.
     * @since 2.0
     */
    public void getID(int identifier) {
        this.identifier = identifier;
    }

    /**
     * This method gets the link unique identifier.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the unique identifier for this link in the topology.
     * @since 2.0
     */
    public int getID() {
        return this.identifier;
    }

    /**
     * This method sets the name of the link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param name The name of the link.
     * @since 2.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method gets the name of the link
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The name of the link.
     * @since 2.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method allows setting whether the name of the link should be
     * displayed in the simulator or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param showName TRUE, if the name of the link should be visible.
     * Otherwise, FALSE.
     * @since 2.0
     */
    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    /**
     * This method allows getting whether the name of the link should be
     * displayed in the simulator or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the name of the link is currently visible. Otherwise,
     * FALSE.
     * @since 2.0
     */
    public boolean getShowName() {
        return this.showName;
    }

    /**
     * This method gets the node connected to the head end of this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the node connected to the head end of this link.
     * @since 2.0
     */
    public TNode getHeadEndNode() {
        return this.headEndNode;
    }

    /**
     * This method sets the node to be connected to the head end of this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param headEndNode the node to be connected to the head end of this link.
     * @since 2.0
     */
    public void setHeadEndNode(TNode headEndNode) {
        this.headEndNode = headEndNode;
    }

    /**
     * This method gets the node connected to the head end of this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the node connected to the tail end of this link.
     * @since 2.0
     */
    public TNode getTailEndNode() {
        return this.tailEndNode;
    }

    /**
     * This method sets the node to be connected to the head end of this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param tailEndNode the node to be connected to the tail end of this link.
     * @since 2.0
     */
    public void setTailEndNode(TNode tailEndNode) {
        this.tailEndNode = tailEndNode;
    }

    /**
     * This method get the position of the head end node in the screen.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the position of the head end node in the screen.
     * @since 2.0
     */
    public Point getHeadEndScreenPosition() {
        return this.headEndNode.getScreenPosition();
    }

    /**
     * This method defines the port ID of the node at the head end of the link
     * where the link is connected.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID the port ID of the node at the head end of the link where
     * the link is connected.
     * @since 2.0
     */
    public void setHeadEndNodePortID(int portID) {
        this.headEndNodePortID = portID;
    }

    /**
     * This method gets the port ID of the node at the head end of the link
     * where the link is connected.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the port ID of the node at the head end of the link where the
     * link is connected.
     * @since 2.0
     */
    public int getHeadEndNodePortID() {
        return this.headEndNodePortID;
    }

    /**
     * This method defines the port ID of the node at the tail end of the link
     * where the link is connected.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID the port ID of the node at the tail end of the link where
     * the link is connected.
     * @since 2.0
     */
    public void setTailEndNodePortID(int portID) {
        this.tailEndNodePortID = portID;
    }

    /**
     * This method gets the port ID of the node at the tail end of the link
     * where the link is connected.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the port ID of the node at the tail end of the link where the
     * link is connected.
     * @since 2.0
     */
    public int getTailEndNodePortID() {
        return this.tailEndNodePortID;
    }

    /**
     * This method get the position of the tail end node in the screen.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the position of the tail end node in the screen.
     * @since 2.0
     */
    public Point getTailEndScreenPosition() {
        return this.tailEndNode.getScreenPosition();
    }

    /**
     * This method checks whether the link is connected to the node specified as
     * an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param node the node we want to check.
     * @return TRUE, if the link is connected to the node specified. Otherwise,
     * FALSE.
     * @since 2.0
     */
    public boolean isConnectedTo(TNode node) {
        if (this.headEndNode.getID() == node.getID()) {
            return true;
        }
        if (this.tailEndNode.getID() == node.getID()) {
            return true;
        }
        return false;
    }

    /**
     * This method checks whether the link is connected to the node specified as
     * an argument through its node ID.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param nodeID the node ID of the node we want to check.
     * @return TRUE, if the link is connected to the node specified by the node
     * ID specified. Otherwise, FALSE.
     * @since 2.0
     */
    public boolean isConnectedTo(int nodeID) {
        if (this.headEndNode.getID() == nodeID) {
            return true;
        }
        if (this.tailEndNode.getID() == nodeID) {
            return true;
        }
        return false;
    }

    /**
     * This method removes a packet from the link and delivers it to the
     * corresponding node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet packet that has reached the target node.
     * @param nodeID node ID of the node that has to receive the packet.
     * @since 2.0
     */
    public void deliverPacketToNode(TAbstractPDU packet, int nodeID) {
        this.packetsInTransitEntriesLock.lock();
        this.buffer.add(new TLinkBufferEntry(packet, this.getDelay(), nodeID));
        this.packetsInTransitEntriesLock.unLock();
    }

    /**
     * This method checks if, given some screen coordinates, these coordinates
     * correspond to this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param screenPosition Screen coordinates.
     * @return TRUE, if these coordinates correspond to the link. Otherwise,
     * FALSE.
     * @since 2.0
     */
    public boolean crossesScreenPosition(Point screenPosition) {
        int x1 = this.headEndNode.getScreenPosition().x + 24;
        int y1 = this.headEndNode.getScreenPosition().y + 24;
        int x2 = this.tailEndNode.getScreenPosition().x + 24;
        int y2 = this.tailEndNode.getScreenPosition().y + 24;
        int dx, dy, steps, k;
        double incrementX, incrementY, x, y;

        if ((x1 == x2) && (y1 == y2)) // Lines that are a single point.
        {
            if ((screenPosition.x == x1) && (screenPosition.y == y1)) {
                return true;
            }
        } else // Rest of lines.
        {
            dx = x2 - x1;
            dy = y2 - y1;
            if (Math.abs(dx) > Math.abs(dy)) {
                steps = Math.abs(dx);
            } else {
                steps = Math.abs(dy);
            }
            incrementX = (float) dx / steps;
            incrementY = (float) dy / steps;
            x = x1;
            y = y1;

            if ((x >= screenPosition.x - 3) && (x <= screenPosition.x + 3)
                    && (y >= screenPosition.y - 3) && (y <= screenPosition.y + 3)) {
                return true;
            }
            for (k = 1; k <= steps; k++) {
                x += incrementX;
                y += incrementY;
                if ((x >= screenPosition.x - 3) && (x <= screenPosition.x + 3)
                        && (y >= screenPosition.y - 3) && (y <= screenPosition.y + 3)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method gets the monitor used to the packets in transit of this link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the monitor used to the packets in transit of this link.
     * @since 2.0
     */
    public TMonitor getLock() {
        return this.packetsInTransitEntriesLock;
    }

    /**
     * This method checks whether the node specified as an argument is the one
     * connected to he head end of the link or to the tail end of the link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param node node that is going to be checked.
     * @return TLink.HEAD_END_NODE if the node specified as an argument is
     * connected to the head end of the link. Otherwise, TLink.TAIL_END_NODE
     * @since 2.0
     */
    public int whichEndIs(TNode node) {
        if (node.getID() == this.headEndNode.getID()) {
            return TLink.HEAD_END_NODE;
        }
        return TLink.TAIL_END_NODE;
    }

    /**
     * This node gets the node ID of the node connected to the link that has to
     * receive a given packet taking into account that the node that sent the
     * packet is the one specified as an argument
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param node The node that sent a given packet.
     * @return TLink.TAIL_END_NODE if the node specified as an argument is
     * connected to the head end of the link. Otherwise, TLink.HEAD_END_NODE.
     * @since 2.0
     */
    public int getDestinationOfTrafficSentBy(TNode node) {
        if (node.getID() == this.headEndNode.getID()) {
            return TLink.TAIL_END_NODE;
        }
        return TLink.HEAD_END_NODE;
    }

    /**
     * This method gets the link type. It should be implemented by subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A link type that should be defined as a constant in TLink.
     * @since 2.0
     */
    public abstract int getLinkType();

    /**
     * This method receives a timer event to do things. It should be implemented
     * by subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The timer event that triggers the link operation.
     * @since 2.0
     */
    @Override
    public abstract void receiveTimerEvent(TTimerEvent evt);

    /**
     * This method is called once the link receivers a timer event and has to do
     * things in it own thread. To be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public abstract void run();

    /**
     * This method gets the weight of this link to be used by the standard
     * routing algorithm. To be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the weight of this link.
     * @since 2.0
     */
    public abstract long getWeight();

    /**
     * This method gets the weight of this link to be used by the RABAN routing
     * algorithm (See "Guarantee of Service Support (GoS) over MPLS using Active
     * Techniques"). To be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the RABAN weight of this link.
     * @since 2.0
     */
    public abstract long getRABANWeight();

    /**
     * This method checks whether the link is well configured or not. To be
     * implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the link is well configured. Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public abstract boolean isWellConfigured();

    /**
     * This method returns a human-readable error message that corresponds to
     * the error code specified as an argument. To be implemented by all
     * subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param errorCode a numeric error code.
     * @return A human-readable error message corresponding to the error code
     * specified as an argument.
     * @since 2.0
     */
    @Override
    public abstract String getErrorMessage(int errorCode);

    /**
     * This method serializes the link as a OSM (Open SimMpls) string. To be
     * implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return This link as a serialized string.
     * @since 2.0
     */
    @Override
    public abstract String marshall();

    /**
     * This method deserializes the link from an OSM (Open SimMpls) string to a
     * TLink (or subclass) instance. To be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param serializedLink The serialized version of a link
     * @return TRUE, if the serialized string is a correct link definition and a
     * TLink (or subclass) has been configured from it. Oterwhise, FALSE.
     * @since 2.0
     */
    @Override
    public abstract boolean unMarshall(String serializedLink);

    /**
     * This method sets this links as a broken one. To be implemented by all
     * subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param isBroken TRUE if the link has to be set as broken. Otherwise,
     * FALSE.
     * @since 2.0
     */
    public abstract void setAsBrokenLink(boolean isBroken);

    /**
     * This method sets default values for all attributes as if the instances is
     * created new. To be implemented by all subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public abstract void reset();

    public static final int INTERNAL_LINK = 0;
    public static final int EXTERNAL_LINK = 1;
    public static final int HEAD_END_NODE = 1;
    public static final int TAIL_END_NODE = 2;

    private int identifier;
    private TNode headEndNode;
    private TNode tailEndNode;
    private int headEndNodePortID;
    private int tailEndNodePortID;
    private String name;
    private boolean showName;
    private int delay;

    protected SortedSet buffer;
    protected TreeSet deliveredPacketsBuffer;
    protected TMonitor packetsInTransitEntriesLock;
    protected TMonitor deliveredPacketEntriesLock;
    protected TTopology topology;
    protected boolean linkIsBroken;

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int ONLY_BLANK_SPACES = 2;
    public static final int NAME_ALREADY_EXISTS = 3;
    public static final int HEAD_END_NODE_PORT_MISSING = 4;
    public static final int TAIL_END_NODE_PORT_MISSING = 5;
    public static final int HEAD_END_NODE_MISSING = 6;
    public static final int TAIL_END_NODE_MISSING = 7;
}
