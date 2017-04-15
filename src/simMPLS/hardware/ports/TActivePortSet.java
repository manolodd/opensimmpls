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
package simMPLS.hardware.ports;

import simMPLS.scenario.TLink;
import simMPLS.scenario.TNode;
import simMPLS.protocols.TAbstractPDU;

/**
 * This class implements a set of active ports for a node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TActivePortSet extends TPortSet {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TActivePortSet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param numberOfPorts Num of ports that the set of active ports will
     * contain.
     * @param activeNode Reference to the parentNode the set of active ports
     * belongs to.
     * @since 2.0
     */
    public TActivePortSet(int numberOfPorts, TNode activeNode) {
        super(numberOfPorts, activeNode);
        this.ports = new TActivePort[numberOfPorts];
        int i = 0;
        for (i = 0; i < this.numberOfPorts; i++) {
            this.ports[i] = new TActivePort(this, i);
            this.ports[i].setPortID(i);
        }
        this.readPort = 0;
        this.nextPacketToBeRead = null;
        this.ratioByPriority = new int[11];
        this.currentByPriority = new int[11];
        for (i = 0; i < 11; i++) {
            this.ratioByPriority[i] = i + 1;
            this.currentByPriority[i] = 0;
        }
        this.currentPriority = 0;
    }

    private void runPriorityBasedNextPacketSelection() {
        if (this.nextPacketToBeRead == null) {
            int priorityCounter = 0;
            int portsCounter = 0;
            boolean end = false;
            int auxPriority = -1;
            int auxCurrentPriority = 0;
            int auxReadPort = 0;
            while ((priorityCounter < 11) && (!end)) {
                auxCurrentPriority = (this.currentPriority + priorityCounter) % 11;
                if (this.currentByPriority[auxCurrentPriority] < this.ratioByPriority[auxCurrentPriority]) {
                    while ((portsCounter < this.numberOfPorts) && (!end)) {
                        auxReadPort = (this.readPort + portsCounter) % this.numberOfPorts;
                        if (this.ports[auxReadPort].thereIsAPacketWaiting()) {
                            auxPriority = ((TActivePort) this.ports[auxReadPort]).getNextPacketPriority();
                            if (auxPriority == auxCurrentPriority) {
                                this.readPort = auxReadPort;
                                this.currentPriority = auxCurrentPriority;
                                this.nextPacketToBeRead = this.ports[auxReadPort].getPacket();
                                end = true;
                                this.currentByPriority[auxCurrentPriority]++;
                            }
                        }
                        portsCounter++;
                    }
                    if (!end) {
                        this.currentByPriority[auxCurrentPriority] = this.ratioByPriority[auxCurrentPriority];
                    }
                }
                if (!end) {
                    priorityCounter++;
                }
                portsCounter = 0;
            }
            resetPriorities();
        }
    }

    private void resetPriorities() {
        int i = 0;
        boolean reset = true;
        for (i = 0; i < 11; i++) {
            if (this.currentByPriority[i] < this.ratioByPriority[i]) {
                reset = false;
            }
        }
        if (reset) {
            for (i = 0; i < 11; i++) {
                this.currentByPriority[i] = 0;
            }
        }
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
        int i = 0;
        for (i = 0; i < this.numberOfPorts; i++) {
            this.ports[i].setUnlimitedBuffer(unlimitedBuffer);
        }
    }

    /**
     * This method returns a port whose port number match the one specified as
     * an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID port number of the port to be obtained.
     * @return The port matching the port number specified as an argument. If
     * the port does not exist, returns NULL.
     * @since 2.0
     */
    @Override
    public TPort getPort(int portID) {
        if (portID < this.numberOfPorts) {
            return this.ports[portID];
        }
        return null;
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
        this.portSetBufferSize = sizeInMB;
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
        return this.portSetBufferSize;
    }

    /**
     * This method check whether a given port is connected to a link or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID portID number of the portID to be checked.
     * @return TRUE, if the portID is not connected to a link (available). FALSE
     * if the portID is connected to a link (not available).
     * @since 2.0
     */
    @Override
    public boolean isAvailable(int portID) {
        if (portID < this.numberOfPorts) {
            return this.ports[portID].isAvailable();
        }
        return false;
    }

    /**
     * This method check whether there is at lest a port of the port set that is
     * not connected to a link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if at least one port of the port set is not connected to a
     * link. Otherwise, returns false.
     * @since 2.0
     */
    @Override
    public boolean hasAvailablePorts() {
        int i = 0;
        for (i = 0; i < this.numberOfPorts; i++) {
            if (this.ports[i].isAvailable()) {
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
        if (portID < this.numberOfPorts) {
            if (this.ports[portID].isAvailable()) {
                this.ports[portID].setLink(link);
            }
        }
    }

    /**
     * This method returns the link that is connected to the port having the
     * specified port number.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID The port number of the port where the desired linkg is
     * connected.
     * @return Link connected to the specified port. If the port is not
     * connected to a link, returns NULL.
     * @since 2.0
     */
    @Override
    public TLink getLinkConnectedToPort(int portID) {
        if (portID < this.numberOfPorts) {
            if (!this.ports[portID].isAvailable()) {
                return this.ports[portID].getLink();
            }
        }
        return null;
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
        if ((portID >= 0) && (portID < this.numberOfPorts)) {
            this.ports[portID].disconnectLink();
        }
    }

    /**
     * This method reads and returns packet from one port of the port set. This
     * port will be selected automatically depending on the priority-based
     * algorithm running in the active port set.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a new packet thas was waiting in one port of the port set.
     * @since 2.0
     */
    @Override
    public TAbstractPDU getNextPacket() {
        TAbstractPDU packetAux = null;
        // This modifies de value of this.nextPacketToBeRead
        // It also changes this.readPort and this.currentPriority
        this.runPriorityBasedNextPacketSelection();
        // End of packet selection based on priorities
        packetAux = this.nextPacketToBeRead;
        this.nextPacketToBeRead = null;
        return packetAux;
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
    public boolean isAnyPacketToSwitch() {
        for (int i = 0; i < numberOfPorts; i++) {
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
        return this.isAnyPacketToSwitch();
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
        TAbstractPDU auxPacket = null;
        // This modifies de value of this.nextPacketToBeRead
        // It also changes this.readPort and this.currentPriority
        this.runPriorityBasedNextPacketSelection();
        // End of packet selection based on priorities
        auxPacket = this.nextPacketToBeRead;
        if (auxPacket != null) {
            if (auxPacket.getSize() <= maxSwitchableOctects) {
                return true;
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
        this.readPort = (this.readPort + 1) % this.numberOfPorts;
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
        return this.readPort;
    }

    /**
     * This method look for a port that is directly connected (through a link)
     * to a parentNode having the IP address specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param adjacentNodeIP IP address of the parentNode connected to the port
     * we are looking for.
     * @return The port to wich the parentNode having the specified IP address
     * is connected to. If the parentNode having the specified IP address is not
     * connected to this port set, returns NULL.
     * @since 2.0
     */
    @Override
    public TPort getLocalPortConnectedToANodeWithIPAddress(String adjacentNodeIP) {
        for (int i = 0; i < this.numberOfPorts; i++) {
            if (!this.ports[i].isAvailable()) {
                int targetNodeID = this.ports[i].getLink().getTargetNodeIDOfTrafficSentBy(this.parentNode);
                if (targetNodeID == TLink.END_NODE_1) {
                    if (this.ports[i].getLink().getEnd1().getIPv4Address().equals(adjacentNodeIP)) {
                        return this.ports[i];
                    }
                } else {
                    if (this.ports[i].getLink().getEnd2().getIPv4Address().equals(adjacentNodeIP)) {
                        return this.ports[i];
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
        if ((portID >= 0) && (portID < this.numberOfPorts)) {
            if (!this.ports[portID].isAvailable()) {
                String IPv42 = this.ports[portID].getLink().getEnd2().getIPv4Address();
                if (this.ports[portID].getLink().getEnd1().getIPv4Address().equals(this.parentNode.getIPv4Address())) {
                    return this.ports[portID].getLink().getEnd2().getIPv4Address();
                }
                return this.ports[portID].getLink().getEnd1().getIPv4Address();
            }
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
        long computedCongestion = 0;
        int i = 0;
        for (i = 0; i < this.numberOfPorts; i++) {
            if (this.ports[i].getCongestionLevel() > computedCongestion) {
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
        this.portSetMonitor.unLock();
        int i = 0;
        for (i = 0; i < this.numberOfPorts; i++) {
            ports[i].reset();
        }
        this.readPort = 0;
        this.setPortSetOccupancySize(0);
        nextPacketToBeRead = null;
        for (i = 0; i < 11; i++) {
            currentByPriority[i] = 0;
        }
        this.artificiallyCongested = false;
        this.occupancy = 0;
        this.portSetMonitor.unLock();
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
        long computationOf97Percent = (long) (this.getBufferSizeInMBytes() * 1017118.72);
        if (congestArtificially) {
            if (!this.artificiallyCongested) {
                if (this.getPortSetOccupancy() < computationOf97Percent) {
                    this.artificiallyCongested = true;
                    this.occupancy = this.getPortSetOccupancy();
                    this.setPortSetOccupancySize(computationOf97Percent);
                }
            }
        } else {
            if (this.artificiallyCongested) {
                this.occupancy += (getPortSetOccupancy() - computationOf97Percent);
                if (this.occupancy < 0) {
                    this.occupancy = 0;
                }
                this.setPortSetOccupancySize(this.occupancy);
                this.artificiallyCongested = false;
                this.occupancy = 0;
            }
        }
    }

    private TPort[] ports;
    private int readPort;

    private int currentPriority;
    // The following attributes are used in a very dirty way to retain the next
    // packet to be managed. This is because of the special priority-based
    // algorithm used by active nodes to prioritize packets depending on
    // several aspects. It need a revision to be optimized.
    private TAbstractPDU nextPacketToBeRead;
    private int[] ratioByPriority;
    private int[] currentByPriority;
    // end of comment
}
