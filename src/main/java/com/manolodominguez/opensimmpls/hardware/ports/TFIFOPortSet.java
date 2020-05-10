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

import com.manolodominguez.opensimmpls.commons.TIPv4AddressGenerator;
import static com.manolodominguez.opensimmpls.commons.UnitsTranslations.OCTETS_PER_MEGABYTE;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
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
public class TFIFOPortSet extends TPortSet {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TFIFOPortSet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param numberOfPorts Num of ports that the set of ports will contain.
     * @param parentNode Reference to the parentNode the set of active ports
     * belongs to.
     * @since 2.0
     */
    public TFIFOPortSet(int numberOfPorts, TNode parentNode) {
        super(numberOfPorts, parentNode);
        translations = ResourceBundle.getBundle(AvailableBundles.T_FIFO_PORT_SET.getPath());
        if (numberOfPorts < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        if (parentNode == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        ports = new TFIFOPort[numberOfPorts];
        for (int i = ZERO; i < numberOfPorts; i++) {
            ports[i] = new TFIFOPort(this, i);
            ports[i].setPortID(i);
        }
        readPort = ZERO;
    }

    /**
     * This method establishes the ser of ports as ideal ones, without size
     * restrictions; unlimited.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param unlimitedBuffer TRUE, if the set of ports will be defined as
     * unlimited ones; otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public void setUnlimitedBuffer(boolean unlimitedBuffer) {
        for (int i = ZERO; i < numberOfPorts; i++) {
            ports[i].setUnlimitedBuffer(unlimitedBuffer);
        }
    }

    /**
     * This method returns a port whose port number match the one specified as
     * an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID port number of the port to be obtained.
     * @return The port matching the port number specified as an argument.
     * @since 2.0
     */
    @Override
    public TPort getPort(int portID) {
        if ((portID < ZERO) || (portID >= numberOfPorts)) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        return ports[portID];
    }

    /**
     * This method establishes the set of ports buffer size. If the set of ports
     * is defined as unlimited, this method do nothing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param sizeInMB Size, in MB, for the set of ports buffer.
     * @since 2.0
     */
    @Override
    public void setBufferSizeInMB(int sizeInMB) {
        portSetBufferSize = sizeInMB;
    }

    /**
     * This method returns the size of the port set buffer.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The size of the port set buffer in MB.
     * @since 2.0
     */
    @Override
    public int getBufferSizeInMBytes() {
        return portSetBufferSize;
    }

    /**
     * This method check whether a given port is connected to a link or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID port number of the port set to be checked.
     * @return TRUE, if the portID is not connected to a link (available). FALSE
     * if the port is connected to a link (not available).
     * @since 2.0
     */
    @Override
    public boolean isAvailable(int portID) {
        if ((portID < ZERO) || (portID >= numberOfPorts)) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        return ports[portID].isAvailable();
    }

    /**
     * This method check whether there is at least a port of the port set that
     * is not connected to a link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if at least one port of the port set is not connected to a
     * link. Otherwise, returns false.
     * @since 2.0
     */
    @Override
    public boolean hasAvailablePorts() {
        for (int i = ZERO; i < numberOfPorts; i++) {
            if (ports[i].isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method connect a link to a given port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param link The link to be connected.
     * @param portID The port number of the port to be coonecte to the link.
     * @since 2.0
     */
    @Override
    public void connectLinkToPort(TLink link, int portID) {
        if ((portID < ZERO) || (portID >= numberOfPorts)) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        if (link == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        if (ports[portID].isAvailable()) {
            ports[portID].setLink(link);
        }
    }

    /**
     * This method returns the link that is connected to the port having the
     * specified port number.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID The port number of the port where the desired link is
     * connected.
     * @return Link connected to the specified port. If the port is not
     * connected to a link, returns NULL.
     * @since 2.0
     */
    @Override
    public TLink getLinkConnectedToPort(int portID) {
        if ((portID < ZERO) || (portID >= numberOfPorts)) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        return ports[portID].getLink();
    }

    /**
     * This method disconnect a link from a given port, making this port
     * available.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID the port number of the port whose link is going to be
     * disconnected from it.
     * @since 2.0
     */
    @Override
    public void disconnectLinkFromPort(int portID) {
        if ((portID < ZERO) || (portID >= numberOfPorts)) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        ports[portID].disconnectLink();
    }

    /**
     * This method reads and returns packet from one port of the port set. This
     * port will be selected automatically depending on the priority-based
     * algorithm running in the port set.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a new packet thas was waiting in one port of the port set.
     * @since 2.0
     */
    @Override
    public TAbstractPDU getNextPacket() {
        for (int i = ZERO; i < numberOfPorts; i++) {
            readPort = (readPort + ONE) % numberOfPorts;
            if (ports[readPort].thereIsAPacketWaiting()) {
                return ports[readPort].getPacket();
            }
        }
        return null;
    }

    /**
     * This method check whether there are packets waiting in the incoming
     * buffer to be switched or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is at least one packet in one port buffer waiting
     * to be switched/routed. Otherwise, returns FALSE.
     * @since 2.0
     */
    @Override
    public boolean isThereAnyPacketToSwitch() {
        for (int i = ZERO; i < numberOfPorts; i++) {
            if (ports[i].thereIsAPacketWaiting()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method check whether there are packets waiting in the incoming
     * buffer to be routed or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is at least one packet in one port buffer waiting
     * to be switched/routed. Otherwise, returns FALSE.
     * @since 2.0
     */
    @Override
    public boolean isThereAnyPacketToRoute() {
        return isThereAnyPacketToSwitch();
    }

    /**
     * This method check if the next packet can be switched, taking as a
     * reference the number of octects that the parent parentNode can switch at
     * this momment (passed as an argument)
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param maxSwitchableOctects the max. number of octects the parent
     * parentNode is able to switch a this moment.
     * @return TRUE, if next packet can be switched. Otherwise, return false.
     * @since 2.0
     */
    @Override
    public boolean canSwitchPacket(int maxSwitchableOctects) {
        if (maxSwitchableOctects < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        int numberOfEmptyPorts = ZERO;
        while (numberOfEmptyPorts < numberOfPorts) {
            if (ports[((readPort + ONE) % numberOfPorts)].thereIsAPacketWaiting()) {
                return ports[((readPort + ONE) % numberOfPorts)].canSwitchPacket(maxSwitchableOctects);
            } else {
                numberOfEmptyPorts++;
                skipPort();
            }
        }
        return false;
    }

    /**
     * This method skip the port that should be read and read the next one
     * instead.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void skipPort() {
        readPort = (readPort + ONE) % numberOfPorts;
    }

    /**
     * This method returns the port number of the port from where the latest
     * packet was read.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of the port from where the latest packet was read.
     * @since 2.0
     */
    @Override
    public int getReadPort() {
        return readPort;
    }

    /**
     * This method look for a port that is directly connected (through a link)
     * to a node having the IP address specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param adjacentNodeIPv4Address IP address of the node connected to the
     * port we are looking for.
     * @return The port to wich the node having the specified IP address is
     * connected to. If the node having the specified IP address is not
     * connected to this port set, returns NULL.
     * @since 2.0
     */
    @Override
    public TPort getLocalPortConnectedToANodeWithIPv4Address(String adjacentNodeIPv4Address) {
        TIPv4AddressGenerator ipv4AddressGenerator = new TIPv4AddressGenerator();
        if (!ipv4AddressGenerator.isAValidIPv4Address(adjacentNodeIPv4Address)) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        for (int i = ZERO; i < numberOfPorts; i++) {
            if (!ports[i].isAvailable()) {
                int targetNodeID = ports[i].getLink().getDestinationOfTrafficSentBy(parentNode);
                if (targetNodeID == TLink.HEAD_END_NODE) {
                    if (ports[i].getLink().getHeadEndNode().getIPv4Address().equals(adjacentNodeIPv4Address)) {
                        return ports[i];
                    }
                } else {
                    if (ports[i].getLink().getTailEndNode().getIPv4Address().equals(adjacentNodeIPv4Address)) {
                        return ports[i];
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method query a given port to obtain the IP of the node that is
     * connected to this port (through a link).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID The port number of the port to be queried.
     * @return IP address of the node that is connected to the specified port by
     * a link. If the port is not connected (is available), returns NULL.
     * @since 2.0
     */
    @Override
    public String getIPv4OfNodeLinkedTo(int portID) {
        if ((portID < ZERO) || (portID >= numberOfPorts)) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        if (!ports[portID].isAvailable()) {
            if (ports[portID].getLink().getHeadEndNode().getIPv4Address().equals(parentNode.getIPv4Address())) {
                return ports[portID].getLink().getTailEndNode().getIPv4Address();
            }
            return ports[portID].getLink().getHeadEndNode().getIPv4Address();
        }
        return null;
    }

    /**
     * This method computes the global congestion level of the port set as a
     * percentage.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Congestion level of the port set as a percentage (0%-100%).
     * @since 2.0
     */
    @Override
    public long getCongestionLevel() {
        long computedCongestion = ZERO;
        for (int i = ZERO; i < numberOfPorts; i++) {
            if (ports[i].getCongestionLevel() > computedCongestion) {
                computedCongestion = ports[i].getCongestionLevel();
            }
        }
        return computedCongestion;
    }

    /**
     * This method reset the value of all attributes as when created by the
     * constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        portSetSemaphore.setGreen();
        for (int i = ZERO; i < numberOfPorts; i++) {
            ports[i].reset();
        }
        readPort = ZERO;
        setPortSetOccupancySize(ZERO);
        artificiallyCongested = false;
        occupancy = ZERO;
        portSetSemaphore.setGreen();
    }

    /**
     * This method allow establishing the congestion level of the port set to
     * 97% so that the parent node will start qickly to discard packets. It is a
     * trick created to simulate this situation, and the port set is able to get
     * back to the real state when desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param congestArtificially TRUE if port set is going to be congested
     * artificially. Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public void setArtificiallyCongested(boolean congestArtificially) {
        long computedCongestionLevel = (long) (getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits() * CONGESTION_FACTOR);
        if (congestArtificially) {
            if (!artificiallyCongested) {
                if (getPortSetOccupancy() < computedCongestionLevel) {
                    artificiallyCongested = true;
                    occupancy = getPortSetOccupancy();
                    setPortSetOccupancySize(computedCongestionLevel);
                }
            }
        } else {
            if (artificiallyCongested) {
                occupancy += (getPortSetOccupancy() - computedCongestionLevel);
                if (occupancy < ZERO) {
                    occupancy = ZERO;
                }
                setPortSetOccupancySize(occupancy);
                artificiallyCongested = false;
                occupancy = ZERO;
            }
        }
    }

    private TPort[] ports;
    private int readPort;
    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TFIFOPortSet.class);

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final float CONGESTION_FACTOR = 0.97f;
}
