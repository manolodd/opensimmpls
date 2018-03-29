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

import com.manolodominguez.opensimmpls.scenario.simulationevents.ESimulationSingleSubscriber;
import com.manolodominguez.opensimmpls.hardware.timer.TTimer;
import com.manolodominguez.opensimmpls.utils.TIPv4AddressGenerator;
import com.manolodominguez.opensimmpls.utils.TLock;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class implements a network topology: nodes, links and the required
 * algorithms to compute routes using Floyd algorithm and RABAN algorithm. For
 * RABAN algorithm see "Guarantee of Service Support (GoS) over MPLS using
 * Active Techniques" proposal.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTopology {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TTopology.
     *
     * @param parentScenario Scenario to wich the topology belongs to.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTopology(TScenario parentScenario) {
        this.nodes = new TreeSet();
        this.links = new TreeSet();
        this.timer = new TTimer();
        this.parentScenario = parentScenario;
        this.eventIDGenerator = new TLongIDGenerator();
        this.elementsIDGenerator = new TIDGenerator();
        this.ipv4AddressGenerator = new TIPv4AddressGenerator();
        this.floydWarshallAlgorithmLock = new TLock();
        this.rabanAlgorithmLock = new TLock();
    }

    /**
     * This method restart all attributes of the class as when it was
     * instantiated.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.floydWarshallAlgorithmLock.lock();
        Iterator nodesIterator;
        nodesIterator = this.nodes.iterator();
        TTopologyElement topologyElement;
        while (nodesIterator.hasNext()) {
            topologyElement = (TTopologyElement) nodesIterator.next();
            topologyElement.reset();
        }
        nodesIterator = this.links.iterator();
        while (nodesIterator.hasNext()) {
            topologyElement = (TTopologyElement) nodesIterator.next();
            topologyElement.reset();
        }
        this.timer.reset();
        this.eventIDGenerator.reset();
        this.floydWarshallAlgorithmLock.unLock();
        this.rabanAlgorithmLock.unLock();
    }

    /**
     * This method adds a new node to the topology.
     *
     * @param node Node to be added to the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void addNode(TNode node) {
        this.nodes.add(node);
        this.timer.addTimerEventListener(node);
        try {
            node.addSimulationListener(this.parentScenario.getSimulation().getSimulationEventListener());
        } catch (ESimulationSingleSubscriber e) {
            System.out.println(e.toString());
        }
    }

    /**
     * This method removes a node from the topology, but does not disconnect the
     * links connected to it. It's a dirty removal that should only be used to
     * remove nodes that are not connected to others.
     *
     * @param nodeID Node to be added to the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void removeNode(int nodeID) {
        boolean done = false;
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while ((nodesIterator.hasNext()) && (!done)) {
            node = (TNode) nodesIterator.next();
            if (node.getNodeID() == nodeID) {
                node.markForDeletionAsTimerEventListener(true);
                nodesIterator.remove();
                done = true;
            }
        }
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * This method gets a node from the topology corresponding to the identifier
     * specified as an argument.
     *
     * @param nodeID the identifier of the node to be returned.
     * @return a node from the topology corresponding to the identifier
     * specified as an argument.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getNode(int nodeID) {
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.getNodeID() == nodeID) {
                return node;
            }
        }
        return null;
    }

    /**
     * This method gets a node from the topology corresponding to the IPv4
     * address specified as an argument.
     *
     * @param ipv4Address the IPv4 address of the node to be returned.
     * @return a node from the topology corresponding to the IPv4 address
     * specified as an argument.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getNode(String ipv4Address) {
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.getIPv4Address().equals(ipv4Address)) {
                return node;
            }
        }
        return null;
    }

    /**
     * This method gets the first node from the topology that match the node
     * name specified as an argument.
     *
     * @param nodeName node name of the node to be returned.
     * @return the first node from the topology that match the node name
     * specified as an argument, if it exists. Otherwise, returns FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getFirstNodeNamed(String nodeName) {
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }
        return null;
    }

    /**
     * This methods checks whether there are more than a node using the node
     * name specified as an argument.
     *
     * @param nodeName node name to check.
     * @return TRUE, if there are more than a node using the node name specified
     * as an argument. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereMoreThanANodeNamed(String nodeName) {
        int nodesWithSameName = 0;
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.getName().equals(nodeName)) {
                nodesWithSameName++;
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            if (nodesWithSameName > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * This methods checks whether there are more than a link using the link
     * name specified as an argument.
     *
     * @param linkName link name to check.
     * @return TRUE, if there are more than a link using the link name specified
     * as an argument. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereMoreThanALinkNamed(String linkName) {
        int linksWithSameName = 0;
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            if (link.getName().equals(linkName)) {
                linksWithSameName++;
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            if (linksWithSameName > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks whether there is a traffic generator node that is
     * configured to send traffic to the traffic sink node specifies as an
     * argument.
     *
     * @param trafficSinkNode traffic sink node we want to ckeck if is receiving
     * traffic.
     * @return TRUE, there is a traffic generator node that is configured to
     * send traffic to the traffic sink node specifies as an argument.
     * Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereAnyNodeGeneratingTrafficFor(TTrafficSinkNode trafficSinkNode) {
        TNode node = null;
        TTrafficGeneratorNode trafficeGeneratorNode = null;
        Iterator nodesIterator = this.nodes.iterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                trafficeGeneratorNode = (TTrafficGeneratorNode) node;
                if (trafficeGeneratorNode.getTargetIPv4Address().equals(trafficSinkNode.getIPv4Address())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method gets the first link from the topology that match the link
     * name specified as an argument.
     *
     * @param linkName link name of the link to be returned.
     * @return the first link from the topology that match the link name
     * specified as an argument, if it exists. Otherwise, returns FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getFirstLinkNamed(String linkName) {
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            if (link.getName().equals(linkName)) {
                return link;
            }
        }
        return null;
    }

    /**
     * This method gets the node from the topology corresponding to the screen
     * position specified as an argument.
     *
     * @param screenPosition the screen position of the node to be returned.
     * @return a node from the topology corresponding to the screen prosition
     * specified as an argument.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getNodeInScreenPosition(Point screenPosition) {
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.isInScreenPosition(screenPosition)) {
                return node;
            }
        }
        return null;
    }

    /**
     * This method gets all nodes from the topology as an Array of nodes.
     *
     * @return all nodes from the topology as an Array of nodes.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode[] getNodesAsArray() {
        return (TNode[]) this.nodes.toArray();
    }

    /**
     * This method gets a node as an argument, look for the same node (the one
     * having the same node ID) and update its values using the values of the
     * node specified as an argument.
     *
     * @param modifiedNode Node containing updated values.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void modifyNode(TNode modifiedNode) {
        boolean done = false;
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while ((nodesIterator.hasNext()) && (!done)) {
            node = (TNode) nodesIterator.next();
            if (node.getNodeID() == modifiedNode.getNodeID()) {
                if (modifiedNode.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                    TTrafficGeneratorNode trafficGeneratorNode = (TTrafficGeneratorNode) node;
                    trafficGeneratorNode.setName(modifiedNode.getName());
                    trafficGeneratorNode.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.TRAFFIC_SINK) {
                    TTrafficSinkNode trafficSinkNode = (TTrafficSinkNode) node;
                    trafficSinkNode.setName(modifiedNode.getName());
                    trafficSinkNode.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.LER) {
                    TLERNode lerNode = (TLERNode) node;
                    lerNode.setName(modifiedNode.getName());
                    lerNode.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.ACTIVE_LER) {
                    TActiveLERNode activeLERNode = (TActiveLERNode) node;
                    activeLERNode.setName(modifiedNode.getName());
                    activeLERNode.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.LSR) {
                    TLSRNode lsrNode = (TLSRNode) node;
                    lsrNode.setName(modifiedNode.getName());
                    lsrNode.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.ACTIVE_LSR) {
                    TActiveLSRNode activeLSRNode = (TActiveLSRNode) node;
                    activeLSRNode.setName(modifiedNode.getName());
                    activeLSRNode.setScreenPosition(modifiedNode.getScreenPosition());
                }
                done = true;
            }
        }
    }

    /**
     * This method adds a new link to the topology.
     *
     * @param link link to be added to the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void addLink(TLink link) {
        this.links.add(link);
        this.timer.addTimerEventListener(link);
        try {
            link.addSimulationListener(this.parentScenario.getSimulation().getSimulationEventListener());
        } catch (ESimulationSingleSubscriber e) {
            System.out.println(e.toString());
        }
    }

    /**
     * This method removes from the topology the link that match the link ID
     * specified as an argument.
     *
     * @param linkID link ID of the linlk to be removed from the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeLink(int linkID) {
        boolean done = false;
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        while ((linksIterator.hasNext()) && (!done)) {
            link = (TLink) linksIterator.next();
            if (link.getID() == linkID) {
                link.disconnectFromBothNodes();
                // FIX: Do not use harcoded values; use class constants instead.
                link.markForDeletionAsTimerEventListener(true);
                linksIterator.remove();
                done = true;
            }
        }
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * This method removes from the topology the link specified as an argument.
     *
     * @param link link to be removed from the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeLink(TLink link) {
        this.removeLink(link.getID());
    }

    /**
     * This method gets from the topology the link that match the link ID
     * specified as an argument.
     *
     * @param linkID link ID of the linlk to be returned.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getLink(int linkID) {
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            if (link.getID() == linkID) {
                return link;
            }
        }
        return null;
    }

    /**
     * This method gets the link from the topology corresponding to the screen
     * position specified as an argument.
     *
     * @param screenPosition the screen position of the link to be returned.
     * @return a link from the topology corresponding to the screen prosition
     * specified as an argument.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getLinkInScreenPosition(Point screenPosition) {
        TLink links = null;
        Iterator linksIterator = this.links.iterator();
        while (linksIterator.hasNext()) {
            links = (TLink) linksIterator.next();
            if (links.crossesScreenPosition(screenPosition)) {
                return links;
            }
        }
        return null;
    }

    /**
     * This method gets all links from the topology as an Array of links.
     *
     * @return all links from the topology as an Array of links.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink[] getLinksAsArray() {
        return (TLink[]) this.links.toArray();
    }

    /**
     * This method gets a link as an argument, look for the same link (the one
     * having the same link ID) and update its values using the values of the
     * link specified as an argument.
     *
     * @param modifiedLink Link containing updated values.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void modifyLink(TLink modifiedLink) {
        boolean done = false;
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        while ((linksIterator.hasNext()) && (!done)) {
            link = (TLink) linksIterator.next();
            if (link.getID() == modifiedLink.getID()) {
                if (link.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink externalLink = (TExternalLink) link;
                    externalLink.setHeadEndNode(modifiedLink.getHeadEndNode());
                    externalLink.setTailEndNode(modifiedLink.getTailEndNode());
                } else if (modifiedLink.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink internalLink = (TInternalLink) link;
                    internalLink.setHeadEndNode(modifiedLink.getHeadEndNode());
                    internalLink.setTailEndNode(modifiedLink.getTailEndNode());
                }
                done = true;
            }
        }
    }

    /**
     * This method checks wheteher there is a topology element (node or link) in
     * the screen position specified as an argument.
     *
     * @param screenPosition screen position to check if there is a topology
     * element there.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return TRUE, if there is a topology element in the screen position
     * specified as an argument. Otherwise, FALSE.
     */
    private boolean isThereAnElementInScreenPosition(Point screenPosition) {
        if (getNodeInScreenPosition(screenPosition) != null) {
            return true;
        }
        if (getLinkInScreenPosition(screenPosition) != null) {
            return true;
        }
        return false;
    }

    /**
     * This method returns the topology element that is in the screen position
     * specified as an argument.
     *
     * @param screenPosition screen position to get the topology element that is
     * there.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return The topology element that is in the specified screen position or
     * NULL if there is not a topology element there. If a link and a node are
     * overlapped in the same screen position, the node is returned.
     */
    public TTopologyElement getElementInScreenPosition(Point screenPosition) {
        if (isThereAnElementInScreenPosition(screenPosition)) {
            TNode node;
            node = getNodeInScreenPosition(screenPosition);
            if (node != null) {
                return node;
            }

            TLink link;
            link = getLinkInScreenPosition(screenPosition);
            if (link != null) {
                return link;
            }
        }
        return null;
    }

    /**
     * This method disconnect a node that match the node ID specified as an
     * argument from others and once done this job, removes the node itself.
     *
     * @param nodeID Node ID of the node to be disconnected and removed from the
     * topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void disconnectNodeAndRemove(int nodeID) {
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            if (link.isConnectedTo(nodeID)) {
                link.disconnectFromBothNodes();
                link.markForDeletionAsTimerEventListener(true);
                linksIterator.remove();
            }
        }
        TTopology.this.removeNode(nodeID);
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * This method disconnect the node specified as an argument from others and
     * once done this job, removes the node itself.
     *
     * @param node Node to be disconnected and removed from the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void disconnectNodeAndRemove(TNode node) {
        this.disconnectNodeAndRemove(node.getNodeID());
    }

    /**
     * This method gets the iterator of all nodes of the topology.
     *
     * @return the iterator of all nodes of the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public Iterator getNodesIterator() {
        return this.nodes.iterator();
    }

    /**
     * This method gets the iterator of all links of the topology.
     *
     * @return the iterator of all links of the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public Iterator getLinksIterator() {
        return this.links.iterator();
    }

    /**
     * This method removes all elements from the topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeAllElements() {
        Iterator elementsIterator = this.getLinksIterator();
        TNode node;
        TLink link;
        while (elementsIterator.hasNext()) {
            link = (TLink) elementsIterator.next();
            link.disconnectFromBothNodes();
            link.markForDeletionAsTimerEventListener(true);
            elementsIterator.remove();
        }
        elementsIterator = this.getNodesIterator();
        while (elementsIterator.hasNext()) {
            node = (TNode) elementsIterator.next();
            node.markForDeletionAsTimerEventListener(true);
            elementsIterator.remove();
        }
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * This method gets the number of nodes in the topology.
     *
     * @return the number of nodes in the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getNumberOfNodes() {
        return this.nodes.size();
    }

    /**
     * This method sets the timer, that will synchronize the overall operation
     * between elements.
     *
     * @param timer the timer, that will synchronize the overall operation
     * between elements.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTimer(TTimer timer) {
        this.timer = timer;
    }

    /**
     * This method gets the timer, that synchronizes the overall operation
     * between elements.
     *
     * @return the timer, that synchronizes the overall operation between
     * elements.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTimer getTimer() {
        return this.timer;
    }

    /**
     * This method computes the minimum delay of all point-to-point links in the
     * topology.
     *
     * @return the minimum delay of all point-to-point links in the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getMinimumDelay() {
        Iterator linksIterator = this.getLinksIterator();
        TLink link;
        int minimumDelay = 0;
        int tmpDelay = 0;
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            // FIX: Do not use harcoded values. Use class constants instead.
            if (minimumDelay == 0) {
                minimumDelay = link.getDelay();
            } else {
                tmpDelay = link.getDelay();
                if (tmpDelay < minimumDelay) {
                    minimumDelay = tmpDelay;
                }
            }
        }
        // FIX: Do not use harcoded values. Use class constants instead.
        if (minimumDelay == 0) {
            // FIX: Do not use harcoded values. Use class constants instead.
            minimumDelay = 1;
        }
        return minimumDelay;
    }

    /**
     * This method checks whether there is a link that joins two nodes of the
     * topology that correponds to the two node IDs specified as arguments.
     *
     * @param node1ID node ID of the link end 1.
     * @param node2ID node ID of the link end 2.
     * @return TRUE, if there is a link that joins the nodes whose node IDs are
     * specified as arguments. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereAnyLinkThatJoins(int node1ID, int node2ID) {
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        TNode node1;
        TNode node2;
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            node1 = link.getHeadEndNode();
            node2 = link.getTailEndNode();
            if ((node2.getNodeID() == node1ID) && (node1.getNodeID() == node2ID)) {
                return true;
            }
            if ((node2.getNodeID() == node2ID) && (node1.getNodeID() == node1ID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns the link that joins two nodes of the topology that
     * correponds to the two node IDs specified as arguments.
     *
     * @param node1ID node ID of the link end 1.
     * @param node2ID node ID of the link end 2.
     * @return the link that joins two nodes of the topology that correponds to
     * the two node IDs specified as arguments, if it exists. if it does not
     * exist, returns NULL.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getLinkThatJoins(int node1ID, int node2ID) {
        TLink link = null;
        Iterator linksIterator = this.links.iterator();
        TNode node1;
        TNode node2;
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            node1 = link.getHeadEndNode();
            node2 = link.getTailEndNode();
            if ((node2.getNodeID() == node1ID) && (node1.getNodeID() == node2ID)) {
                return link;
            }
            if ((node2.getNodeID() == node2ID) && (node1.getNodeID() == node1ID)) {
                return link;
            }
        }
        return null;
    }

    /**
     * This method gets the event ID generator of this topology.
     *
     * @return the event ID generator of this topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLongIDGenerator getEventIDGenerator() {
        return this.eventIDGenerator;
    }

    /**
     * This method sets the event ID generator of this topology.
     *
     * @param elementsIDGenerator the event ID generator of this topology
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setElementsIDGenerator(TIDGenerator elementsIDGenerator) {
        this.elementsIDGenerator = elementsIDGenerator;
    }

    /**
     * This method gets the element ID generator of this topology.
     *
     * @return the element ID generator of this topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TIDGenerator getElementsIDGenerator() {
        return this.elementsIDGenerator;
    }

    /**
     * This method sets the IPv4 adddress generator of this topology.
     *
     * @param ipv4AddressGenerator the IPv4 adddress generator of this topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setIPv4AddressGenerator(TIPv4AddressGenerator ipv4AddressGenerator) {
        this.ipv4AddressGenerator = ipv4AddressGenerator;
    }

    /**
     * This method gets the IPv4 adddress generator of this topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the IPv4 adddress generator of this topology.
     * @since 2.0
     */
    public TIPv4AddressGenerator getIPv4AddressGenerator() {
        return this.ipv4AddressGenerator;
    }

    /**
     * This method runs Floyd-Warshall algorithm to compute the next hop node ID
     * to reach the target node (whose node ID is specified as an argument) from
     * a given origin node (whose node ID is specified as an argument, too).
     * This algorithm is the "traditional" one where link delay are used to
     * route packets through a network.
     *
     * @param originNodeID node ID of the origin node.
     * @param targetNodeID node ID of the target/destination node.
     * @return node ID of the next hop node, that is an adjacent node of the
     * origin node, to reach the target node with a minimum delay, according to
     * Floyd-Warsall algorithm. If there is not a route to reach the target
     * node, TTopology.TARGET_UNREACHABLE is returned.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized int getFloydWarshallNextHopID(int originNodeID, int targetNodeID) {
        this.floydWarshallAlgorithmLock.lock();
        int currentNumberOfNodes = this.nodes.size();
        int tmpOrigin = 0;
        int tmpDestination = 0;
        // We compute equivalences betweeen indexes and node IDs to be used 
        // when computing the adjacency matrix. This is needed because node IDs 
        // cannot be used as indexes of the adjacency matrix. It's an index 
        // translation.
        int[] equivalenceMatrix = new int[currentNumberOfNodes];
        int i = 0;
        TNode node = null;
        Iterator nodesIterator = this.getNodesIterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            equivalenceMatrix[i] = node.getNodeID();
            if (equivalenceMatrix[i] == originNodeID) {
                tmpOrigin = i;
            } else if (equivalenceMatrix[i] == targetNodeID) {
                tmpDestination = i;
            }
            i++;
        }
        // We compute adjacency matrix
        TLink link = null;
        long[][] adjacencyMatrix = new long[currentNumberOfNodes][currentNumberOfNodes];
        int j = 0;
        for (i = 0; i < currentNumberOfNodes; i++) {
            for (j = 0; j < currentNumberOfNodes; j++) {
                link = getLinkThatJoins(equivalenceMatrix[i], equivalenceMatrix[j]);
                if ((link == null) || ((link != null) && (link.isBroken()))) {
                    // If there is not a link joining node i and j. Or if this
                    // link exists but is down at this moment:
                    if (i == j) {
                        // It's the same node.
                        adjacencyMatrix[i][j] = 0;
                    } else {
                        // It's impossible to reach i from j or viceversa.
                        adjacencyMatrix[i][j] = TTopology.INFINITE_WEIGHT;
                    }
                } else if (link.getLinkType() == TLink.EXTERNAL_LINK) {
                    // We put the link weight in the adjacency matrix
                    TExternalLink externalLink = (TExternalLink) link;
                    adjacencyMatrix[i][j] = externalLink.getWeight();
                } else {
                    // We put the link weight in the adjacency matrix
                    TInternalLink internalLink = (TInternalLink) link;
                    adjacencyMatrix[i][j] = internalLink.getWeight();
                }
            }
        }
        // We compute costs matrix and paths matrix
        long[][] costsMatrix = new long[currentNumberOfNodes][currentNumberOfNodes];
        int[][] pathsMatrix = new int[currentNumberOfNodes][currentNumberOfNodes];
        int k = 0;
        for (i = 0; i < currentNumberOfNodes; i++) {
            for (j = 0; j < currentNumberOfNodes; j++) {
                costsMatrix[i][j] = adjacencyMatrix[i][j];
                pathsMatrix[i][j] = currentNumberOfNodes;
            }
        }
        for (k = 0; k < currentNumberOfNodes; k++) {
            for (i = 0; i < currentNumberOfNodes; i++) {
                for (j = 0; j < currentNumberOfNodes; j++) {
                    if (!((costsMatrix[i][k] == TTopology.INFINITE_WEIGHT) || (costsMatrix[k][j] == TTopology.INFINITE_WEIGHT))) {
                        if ((costsMatrix[i][k] + costsMatrix[k][j]) < costsMatrix[i][j]) {
                            costsMatrix[i][j] = costsMatrix[i][k] + costsMatrix[k][j];
                            pathsMatrix[i][j] = k;
                        }
                    }
                }
            }
        }
        // If there is a route to destination host, this step get the first hop
        // of this route.
        int nextHop = TTopology.TARGET_UNREACHABLE;
        k = pathsMatrix[tmpOrigin][tmpDestination];
        while (k != currentNumberOfNodes) {
            nextHop = k;
            k = pathsMatrix[tmpOrigin][k];
        }
        // We check wether there is not route to host or they are adjacents
        if (nextHop == TTopology.TARGET_UNREACHABLE) {
            TLink linkAux = this.getLinkThatJoins(originNodeID, targetNodeID);
            if (linkAux != null) {
                nextHop = targetNodeID;
            }
        } else {
            nextHop = equivalenceMatrix[nextHop];
        }
        this.floydWarshallAlgorithmLock.unLock();
        return nextHop;
    }

    /**
     * This method runs Floyd-Warshall algorithm to compute the next hop IPv4
     * address to reach the target node (whose IPv4 address is specified as an
     * argument) from a given origin node (whose IPv4 address is specified as an
     * argument, too). This algorithm is the "traditional" one where link delay
     * are used to route packets through a network.
     *
     * @param originNodeIPv4Address IPv4 address of the origin node.
     * @param targetNodeIPv4Address IPv4 address of the target/destination node.
     * @return IPv4 address of the next hop node, that is an adjacent node of
     * the origin node, to reach the target node with a minimum delay, according
     * to Floyd-Warsall algorithm. If there is not a route to reach the target
     * node, NULL is returned.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized String getFloydWarsallNextHopIPv4Address(String originNodeIPv4Address, String targetNodeIPv4Address) {
        int originNodeID = this.getNode(originNodeIPv4Address).getNodeID();
        int destinationID = this.getNode(targetNodeIPv4Address).getNodeID();
        int nextHopID = getFloydWarshallNextHopID(originNodeID, destinationID);
        TNode node = this.getNode(nextHopID);
        if (node != null) {
            return node.getIPv4Address();
        }
        return null;
    }

    /**
     * This method runs RABAN algorithm (a variant of Floyd-Warsall algorithm)
     * to compute the next hop IPv4 address to reach the target node (whose IPv4
     * address is specified as an argument) from a given origin node (whose IPv4
     * address is specified as an argument, too). This algorithm takes into
     * account lots of data instead of only "delay", to balance the traffic
     * through a network. See "Guarentee of Service (GoS) support over MPLS
     * using Active Techniques" proposal so know more of RABAN.
     *
     * @param originNodeIPv4Address IPv4 address of the origin node.
     * @param targetNodeIPv4Address IPv4 address of the target/destination node.
     * @return IPv4 address of the next hop node, that is an adjacent node of
     * the origin node, to reach the target node with a minimum RABAN weight,
     * according to RABAN algorithm. If there is not a route to reach the target
     * node, NULL is returned.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized String getRABANNextHopIPv4Address(String originNodeIPv4Address, String targetNodeIPv4Address) {
        int originNodeID = this.getNode(originNodeIPv4Address).getNodeID();
        int destinationNodeID = this.getNode(targetNodeIPv4Address).getNodeID();
        int nextHopID = TTopology.this.getRABANNextHopID(originNodeID, destinationNodeID);
        TNode node = this.getNode(nextHopID);
        if (node != null) {
            return node.getIPv4Address();
        }
        return null;
    }

    /**
     * This method runs RABAN algorithm (a variant of Floyd-Warsall algorithm)
     * to compute the next hop IPv4 address to reach the target node (whose IPv4
     * address is specified as an argument) from a given origin node (whose IPv4
     * address is specified as an argument, too); also, it avoid choosing the
     * node specified by nodeToAvoidIPv4Address as next hop. This algorithm
     * takes into account lots of data instead of only "delay", to balance the
     * traffic through a network. See "Guarentee of Service (GoS) support over
     * MPLS using Active Techniques" proposal so know more of RABAN.
     *
     * @param originNodeIPv4Address IPv4 address of the origin node.
     * @param targetNodeIPv4Address IPv4 address of the target/destination node.
     * @param nodeToAvoidIPv4Address IPv4 address of the node that has not to be
     * taken into account to compute the next hop. The corresponding node is
     * avoided.
     * @return IPv4 address of the next hop node, that is an adjacent node of
     * the origin node, to reach the target node with a minimum RABAN weight,
     * according to RABAN algorithm. If there is not a route to reach the target
     * node, NULL is returned.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized String getRABANNextHopIPv4Address(String originNodeIPv4Address, String targetNodeIPv4Address, String nodeToAvoidIPv4Address) {
        int originNodeID = this.getNode(originNodeIPv4Address).getNodeID();
        int destinationNodeID = this.getNode(targetNodeIPv4Address).getNodeID();
        int nodeToAvoidID = this.getNode(nodeToAvoidIPv4Address).getNodeID();
        int nextHopID = getNextHopIDUsingRABAN(originNodeID, destinationNodeID, nodeToAvoidID);
        TNode node = this.getNode(nextHopID);
        if (node != null) {
            return node.getIPv4Address();
        }
        return null;
    }

    /**
     * This method runs RABAN algorithm (a variant of Floyd-Warsall algorithm)
     * to compute the next hop node ID to reach the target node (whose node ID
     * is specified as an argument) from a given origin node (whose node ID is
     * specified as an argument, too). This algorithm takes into account lots of
     * data instead of only "delay", to balance the traffic through a network.
     * See "Guarentee of Service (GoS) support over MPLS using Active
     * Techniques" proposal so know more of RABAN.
     *
     * @param originNodeID node ID of the origin node.
     * @param targetNodeID node ID of the target/destination node.
     * @return node ID of the next hop node, that is an adjacent node of the
     * origin node, to reach the target node with a minimum RABAN weight,
     * according to RABAN algorithm. If there is not a route to reach the target
     * node, TTopology.TARGET_UNREACHABLE is returned.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized int getRABANNextHopID(int originNodeID, int targetNodeID) {
        this.rabanAlgorithmLock.lock();
        int currentNumberOfNodes = this.nodes.size();
        int tmpOrigin = 0;
        int tmpDestination = 0;
        // We compute equivalences betweeen indexes and node IDs to be used 
        // when computing the adjacency matrix. This is needed because node IDs 
        // cannot be used as indexes of the adjacency matrix. It's an index 
        // translation.
        int[] equivalenceMatrix = new int[currentNumberOfNodes];
        int i = 0;
        TNode node = null;
        Iterator nodesIterator = this.getNodesIterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            equivalenceMatrix[i] = node.getNodeID();
            if (equivalenceMatrix[i] == originNodeID) {
                tmpOrigin = i;
            } else if (equivalenceMatrix[i] == targetNodeID) {
                tmpDestination = i;
            }
            i++;
        }
        // We compute adjacency matrix
        TLink link = null;
        long[][] adjacencyMatrix = new long[currentNumberOfNodes][currentNumberOfNodes];
        int j = 0;
        for (i = 0; i < currentNumberOfNodes; i++) {
            for (j = 0; j < currentNumberOfNodes; j++) {
                link = getLinkThatJoins(equivalenceMatrix[i], equivalenceMatrix[j]);
                if ((link == null) || ((link != null) && (link.isBroken()))) {
                    if (i == j) {
                        adjacencyMatrix[i][j] = 0;
                    } else {
                        adjacencyMatrix[i][j] = TTopology.INFINITE_WEIGHT;
                    }
                } else if (link.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink externalLink = (TExternalLink) link;
                    adjacencyMatrix[i][j] = externalLink.getRABANWeight();
                } else {
                    TInternalLink internalLink = (TInternalLink) link;
                    adjacencyMatrix[i][j] = internalLink.getRABANWeight();
                }
            }
        }
        // We compute costs matrix and paths matrix
        long[][] costsMatrix = new long[currentNumberOfNodes][currentNumberOfNodes];
        int[][] pathsMatrix = new int[currentNumberOfNodes][currentNumberOfNodes];
        int k = 0;
        for (i = 0; i < currentNumberOfNodes; i++) {
            for (j = 0; j < currentNumberOfNodes; j++) {
                costsMatrix[i][j] = adjacencyMatrix[i][j];
                pathsMatrix[i][j] = currentNumberOfNodes;
            }
        }
        for (k = 0; k < currentNumberOfNodes; k++) {
            for (i = 0; i < currentNumberOfNodes; i++) {
                for (j = 0; j < currentNumberOfNodes; j++) {
                    if (!((costsMatrix[i][k] == TTopology.INFINITE_WEIGHT) || (costsMatrix[k][j] == TTopology.INFINITE_WEIGHT))) {
                        if ((costsMatrix[i][k] + costsMatrix[k][j]) < costsMatrix[i][j]) {
                            costsMatrix[i][j] = costsMatrix[i][k] + costsMatrix[k][j];
                            pathsMatrix[i][j] = k;
                        }
                    }
                }
            }
        }
        // If there is a route to destination host, this step get the first hop
        // of this route.
        int nextHop = TTopology.TARGET_UNREACHABLE;
        k = pathsMatrix[tmpOrigin][tmpDestination];
        while (k != currentNumberOfNodes) {
            nextHop = k;
            k = pathsMatrix[tmpOrigin][k];
        }
        // We check wether there is not route to host or they are adjacents
        if (nextHop == TTopology.TARGET_UNREACHABLE) {
            TLink linkAux = this.getLinkThatJoins(originNodeID, targetNodeID);
            if (linkAux != null) {
                nextHop = targetNodeID;
            }
        } else {
            nextHop = equivalenceMatrix[nextHop];
        }
        this.rabanAlgorithmLock.unLock();
        return nextHop;
    }

    /**
     * This method runs RABAN algorithm (a variant of Floyd-Warsall algorithm)
     * to compute the next hop node ID to reach the target node (whose node ID
     * is specified as an argument) from a given origin node (whose node ID is
     * specified as an argument, too); also, it avoid choosing the node
     * specified by nodeToAvoidID node ID as next hop. This algorithm takes into
     * account lots of data instead of only "delay", to balance the traffic
     * through a network. See "Guarentee of Service (GoS) support over MPLS
     * using Active Techniques" proposal so know more of RABAN.
     *
     * @param originNodeID node ID of the origin node.
     * @param targetNodeID node ID of the target/destination node.
     * @param nodeToAvoidID node ID that has not to be taken into account to
     * compute the next hop. The corresponding node is avoided.
     * @return node ID of the next hop node, that is an adjacent node of the
     * origin node, to reach the target node with a minimum RABAN weight,
     * according to RABAN algorithm. If there is not a route to reach the target
     * node, TTopology.TARGET_UNREACHABLE is returned.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized int getNextHopIDUsingRABAN(int originNodeID, int targetNodeID, int nodeToAvoidID) {
        this.rabanAlgorithmLock.lock();
        int currentNumberOfNodes = this.nodes.size();
        int tmpOrigin = 0;
        int tmpDestination = 0;
        int tmpNodeToAvoidID = 0;
        // We compute equivalences betweeen indexes and node IDs to be used 
        // when computing the adjacency matrix. This is needed because node IDs 
        // cannot be used as indexes of the adjacency matrix. It's an index 
        // translation.
        int[] equivalenceMatrix = new int[currentNumberOfNodes];
        int i = 0;
        TNode node = null;
        Iterator nodesIterator = this.getNodesIterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            equivalenceMatrix[i] = node.getNodeID();
            if (equivalenceMatrix[i] == originNodeID) {
                tmpOrigin = i;
            } else if (equivalenceMatrix[i] == targetNodeID) {
                tmpDestination = i;
            } else if (equivalenceMatrix[i] == nodeToAvoidID) {
                tmpNodeToAvoidID = i;
            }
            i++;
        }
        // We compute adjacency matrix
        TLink link = null;
        long[][] adjacencyMatrix = new long[currentNumberOfNodes][currentNumberOfNodes];
        int j = 0;
        for (i = 0; i < currentNumberOfNodes; i++) {
            for (j = 0; j < currentNumberOfNodes; j++) {
                link = getLinkThatJoins(equivalenceMatrix[i], equivalenceMatrix[j]);
                if ((link == null) || ((link != null) && (link.isBroken()))) {
                    if (i == j) {
                        adjacencyMatrix[i][j] = 0;
                    } else {
                        adjacencyMatrix[i][j] = TTopology.INFINITE_WEIGHT;
                    }
                } else if (link.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink externalLink = (TExternalLink) link;
                    adjacencyMatrix[i][j] = externalLink.getRABANWeight();
                } else {
                    TInternalLink internalLink = (TInternalLink) link;
                    adjacencyMatrix[i][j] = internalLink.getRABANWeight();
                }
                // He we avoid to choose the specified undesired node as next
                // hop. Let's say, we avoid to include the undesired node in
                // the computed route to destination.
                if ((i == tmpOrigin) && (j == tmpNodeToAvoidID)) {
                    adjacencyMatrix[i][j] = TTopology.INFINITE_WEIGHT;
                }
                if ((i == tmpNodeToAvoidID) && (j == tmpOrigin)) {
                    adjacencyMatrix[i][j] = TTopology.INFINITE_WEIGHT;
                }
            }
        }
        // We compute costs matrix and paths matrix
        long[][] costsMatrix = new long[currentNumberOfNodes][currentNumberOfNodes];
        int[][] pathsMatrix = new int[currentNumberOfNodes][currentNumberOfNodes];
        int k = 0;
        for (i = 0; i < currentNumberOfNodes; i++) {
            for (j = 0; j < currentNumberOfNodes; j++) {
                costsMatrix[i][j] = adjacencyMatrix[i][j];
                pathsMatrix[i][j] = currentNumberOfNodes;
            }
        }
        for (k = 0; k < currentNumberOfNodes; k++) {
            for (i = 0; i < currentNumberOfNodes; i++) {
                for (j = 0; j < currentNumberOfNodes; j++) {
                    if (!((costsMatrix[i][k] == TTopology.INFINITE_WEIGHT) || (costsMatrix[k][j] == TTopology.INFINITE_WEIGHT))) {
                        if ((costsMatrix[i][k] + costsMatrix[k][j]) < costsMatrix[i][j]) {
                            costsMatrix[i][j] = costsMatrix[i][k] + costsMatrix[k][j];
                            pathsMatrix[i][j] = k;
                        }
                    }
                }
            }
        }
        // If there is a route to destination host, this step get the first hop
        // of this route.
        int nextHop = TTopology.TARGET_UNREACHABLE;
        k = pathsMatrix[tmpOrigin][tmpDestination];
        while (k != currentNumberOfNodes) {
            nextHop = k;
            k = pathsMatrix[tmpOrigin][k];
        }
        // We check wether there is not route to host or they are adjacents
        if (nextHop == TTopology.TARGET_UNREACHABLE) {
            TLink linkAux = this.getLinkThatJoins(originNodeID, targetNodeID);
            if (linkAux != null) {
                nextHop = targetNodeID;
            }
        } else {
            nextHop = equivalenceMatrix[nextHop];
        }
        this.rabanAlgorithmLock.unLock();
        return nextHop;
    }

    public static final long INFINITE_WEIGHT = 9223372036854775806L;
    public static final long VERY_HIGH_WEIGHT = (long) INFINITE_WEIGHT / 2;
    public static final int TARGET_UNREACHABLE = -1;

    private TreeSet nodes;
    private TreeSet links;
    private TTimer timer;
    private TScenario parentScenario;
    private TLongIDGenerator eventIDGenerator;
    private TIDGenerator elementsIDGenerator;
    private TIPv4AddressGenerator ipv4AddressGenerator;
    private TLock floydWarshallAlgorithmLock;
    private TLock rabanAlgorithmLock;
}
