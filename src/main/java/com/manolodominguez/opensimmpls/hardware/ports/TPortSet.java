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
package com.manolodominguez.opensimmpls.hardware.ports;

import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.commons.TSemaphore;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a set of ports for a node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TPortSet {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TPortSet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param numberOfPorts Num of ports that the set of ports will contain.
     * @param parentNode Reference to the parentNode the set of active ports
     * belongs to.
     * @since 2.0
     */
    public TPortSet(int numberOfPorts, TNode parentNode) {
        translations = ResourceBundle.getBundle(AvailableBundles.T_PORT_SET.getPath());
        if (numberOfPorts < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        if (parentNode == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        this.numberOfPorts = numberOfPorts;
        this.parentNode = parentNode;
        portSetBufferSize = ONE;
        portSetBufferOccupancy = ZERO;
        portSetSemaphore = new TSemaphore();
        artificiallyCongested = false;
        occupancy = ZERO;
    }

    /**
     * This method increases the amount of buffer memory that is occuped.
     *
     * @param occupancyIncrement Size (in octets) that should be added to the
     * current occupancy.
     * @since 2.0
     */
    public synchronized void increasePortSetOccupancy(long occupancyIncrement) {
        if (occupancyIncrement < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        portSetBufferOccupancy += occupancyIncrement;
    }

    /**
     * This method decreases the amount of buffer memory that is occuped.
     *
     * @param occupancyDecrement Size (in octets) that should be substracted
     * from the current occupancy.
     * @since 2.0
     */
    public synchronized void decreasePortSetOccupancySize(long occupancyDecrement) {
        if (occupancyDecrement < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        portSetBufferOccupancy -= occupancyDecrement;
    }

    /**
     * This method set the amount of buffer memory that is occuped.
     *
     * @param portSetBufferOccupancy The amount of buffer memory that is occuped
     * (in octects). that is occuped.
     * @since 2.0
     */
    public synchronized void setPortSetOccupancySize(long portSetBufferOccupancy) {
        if (portSetBufferOccupancy < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        this.portSetBufferOccupancy = portSetBufferOccupancy;
    }

    /**
     * This method get the amount of buffer memory that is occuped.
     *
     * @return The amount of buffer memory that is occuped(in octects).
     * @since 2.0
     */
    public synchronized long getPortSetOccupancy() {
        return portSetBufferOccupancy;
    }

    /**
     * This method checks whether the parent node is congested artificially or
     * not.
     *
     * @since 2.0
     * @return TRUE, if the parent node is congested artificially. Otherwise,
     * FALSE.
     */
    public boolean isCongestedArtificially() {
        return artificiallyCongested;
    }

    /**
     * This method, when implemented, will allow establishing the congestion
     * level of the port set to a value near 100% so that the parent node will
     * start qickly to discard packets. It will be a trick created to simulate
     * this situation, and the port set wil be able to get back to the real
     * state when desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param congestArtificially TRUE if port set is going to be congested
     * artificially. Otherwise, FALSE.
     * @since 2.0
     */
    public abstract void setArtificiallyCongested(boolean congestArtificially);

    /**
     * This method gets the number of ports that the current port set contains.
     *
     * @return The number of ports that the current port set contains.
     * @since 2.0
     */
    public int getNumberOfPorts() {
        return numberOfPorts;
    }

    /**
     * This method sets the parent node of this port set.
     *
     * @param parentNode The parent node of this port set.
     * @since 2.0
     */
    public void setParentNode(TNode parentNode) {
        if (parentNode == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        this.parentNode = parentNode;
    }

    /**
     * This method gets the parent node of this port set.
     *
     * @return The parent node of this port set.
     * @since 2.0
     */
    public TNode getParentNode() {
        if (parentNode == null) {
            logger.error(translations.getString("attributeNotInitialized"));
            throw new IllegalArgumentException(translations.getString("attributeNotInitialized"));
        }
        return parentNode;
    }

    /**
     * This method, when implemented, will read and return a packet from one
     * port of the port set. This port will be selected automatically depending
     * on the priority-based algorithm running in the port set.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a new packet thas was waiting in one port of the port set.
     * @since 2.0
     */
    public abstract TAbstractPDU getNextPacket();

    /**
     * This method, when implemented, will check whether there are packets
     * waiting in the incoming buffer to be switched or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is at least one packet in one port buffer waiting
     * to be switched/routed. Otherwise, returns FALSE.
     * @since 2.0
     */
    public abstract boolean isThereAnyPacketToSwitch();

    /**
     * This method, when implemented, will check whether there are packets
     * waiting in the incoming buffer to be routed or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is at least one packet in one port buffer waiting
     * to be switched/routed. Otherwise, returns FALSE.
     * @since 2.0
     */
    public abstract boolean isThereAnyPacketToRoute();

    /**
     * This method, when implemented, will check if the next packet can be
     * switched, taking as a reference the number of octects that the parent
     * parentNode can switch at this momment (passed as an argument)
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param maxSwitchableOctects the max. number of octects the parent node is
     * able to switch a this moment.
     * @return TRUE, if next packet can be switched. Otherwise, return false.
     * @since 2.0
     */
    public abstract boolean canSwitchPacket(int maxSwitchableOctects);

    /**
     * This method, when implemented, will skip the port that should be read and
     * read the next one instead.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract void skipPort();

    /**
     * This method, when implemented, will return the port number of the port
     * from where the latest packet was read.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of the port from where the latest packet was read.
     * @since 2.0
     */
    public abstract int getReadPort();

    /**
     * This method, when implemented, will look for a port that is directly
     * connected (through a link) to a node having the IP address specified as
     * an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param adjacentNodeIP IP address of the node connected to the port we are
     * looking for.
     * @return The port to wich the node having the specified IP address is
     * connected to. If the node having the specified IP address is not
     * connected to this port set, returns NULL.
     * @since 2.0
     */
    public abstract TPort getLocalPortConnectedToANodeWithIPv4Address(String adjacentNodeIP);

    /**
     * This method, when implemented, will query a given port to obtain the IP
     * of the node that is connected to this port (through a link).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID The port number of the port to be queried.
     * @return IP address of the node that is connected to the specified port by
     * a link. If the port is not connected (is available), returns NULL.
     * @since 2.0
     */
    public abstract String getIPv4OfNodeLinkedTo(int portID);

    /**
     * This method, when implemented, will compute the global congestion level
     * of the port set as a percentage.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Congestion level of the port set as a percentage (0%-100%).
     * @since 2.0
     */
    public abstract long getCongestionLevel();

    /**
     * This method, when implemented, will reset the value of all attributes as
     * when created by the constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract void reset();

    /**
     * This method, when implemented, will connect a link to a given port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param link The link to be connected.
     * @param portID The port number of the port to be coonecte to the link.
     * @since 2.0
     */
    public abstract void connectLinkToPort(TLink link, int portID);

    /**
     * This method returns the link to wich the specified port of the port set
     * is connected.
     *
     * @param portID the port ID of the por whose link is being obtainded.
     * @return the link to wich the specified port of the port set is connected.
     * @since 2.0
     */
    public abstract TLink getLinkConnectedToPort(int portID);

    /**
     * This method, when implemented, will disconnect a link from a given port,
     * making this port available.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID the port number of the port whose link is going to be
     * disconnected from it.
     * @since 2.0
     */
    public abstract void disconnectLinkFromPort(int portID);

    /**
     * This method, when implemented, will establish the ser of ports as ideal
     * ones, without size restrictions; unlimited.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param unlimitedBuffer TRUE, if the set of ports will be defined as
     * unlimited ones; otherwise, FALSE.
     * @since 2.0
     */
    public abstract void setUnlimitedBuffer(boolean unlimitedBuffer);

    /**
     * This method, when implemented, will return a port whose port number match
     * the one specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID port number of the port to be obtained.
     * @return The port matching the port number specified as an argument. If
     * the port does not exist, returns NULL.
     * @since 2.0
     */
    public abstract TPort getPort(int portID);

    /**
     * This method, when implemented, will establish the set of ports buffer
     * size. If the set of ports is defined as unlimited, this method do
     * nothing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param sizeInMB Size, in MB, for the set of ports buffer.
     * @since 2.0
     */
    public abstract void setBufferSizeInMB(int sizeInMB);

    /**
     * This method, when implemented, will return the size of the port set
     * buffer.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The size of the port set buffer in MB.
     * @since 2.0
     */
    public abstract int getBufferSizeInMBytes();

    /**
     * This method, when implemented, will check whether a given port is
     * connected to a link or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID port number of the port set to be checked.
     * @return TRUE, if the portID is not connected to a link (available). FALSE
     * if the port is connected to a link (not available).
     * @since 2.0
     */
    public abstract boolean isAvailable(int portID);

    /**
     * This method, when implemented, will check whether there is at least a
     * port of the port set that is not connected to a link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if at least one port of the port set is not connected to a
     * link. Otherwise, returns false.
     * @since 2.0
     */
    public abstract boolean hasAvailablePorts();

    protected int numberOfPorts;
    protected TNode parentNode;
    protected int portSetBufferSize;
    private long portSetBufferOccupancy;
    public TSemaphore portSetSemaphore;
    protected boolean artificiallyCongested;
    protected long occupancy;
    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TPortSet.class);

    private static final int ZERO = 0;
    private static final int ONE = 1;
}
