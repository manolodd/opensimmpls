package simMPLS.hardware.ports;

import simMPLS.scenario.TLink;
import simMPLS.scenario.TNode;
import simMPLS.protocols.TPDU;

/**
 * This class implements a set of active ports for a parentNode.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TActivePortSet extends TPortSet {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TActiveNodePorts.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param numberOfPorts Num of ports that the set of active ports will
     * contain.
     * @param activeNode Reference to the parentNode the set of active ports
     * belongs to.
     * @since 1.0
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
     * @since 1.0
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
     * @param portNumber port number of the port to be obtained.
     * @return The port matching the port number specified as an argument. If
     * the port does not exist, returns NULL.
     * @since 1.0
     */
    @Override
    public TPort getPort(int portNumber) {
        if (portNumber < this.numberOfPorts) {
            return this.ports[portNumber];
        }
        return null;
    }

    /**
     * This method establishes the set of ports buffer size. If the set of ports
     * is defined as unlimited, this method do nothing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param sizeInMB Size, in MB, for the set of ports buffer.
     * @since 1.0
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
     * @since 1.0
     */
    @Override
    public int getBufferSizeInMB() {
        return this.portSetBufferSize;
    }

    /**
     * This method check whether a given port is connected to a link or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portNumber portNumber number of the portNumber to be checked.
     * @return TRUE, if the portNumber is not connected to a link (available).
     * FALSE if the portNumber is connected to a link (not available).
     * @since 1.0
     */
    @Override
    public boolean isAvailable(int portNumber) {
        if (portNumber < this.numberOfPorts) {
            return this.ports[portNumber].isAvailable();
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
     * @since 1.0
     */
    @Override
    public boolean isAnyPortAvailable() {
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
     * @param portNumber The port number of the port to be coonecte to the link.
     * @since 1.0
     */
    @Override
    public void connectLinkToPort(TLink link, int portNumber) {
        if (portNumber < this.numberOfPorts) {
            if (this.ports[portNumber].isAvailable()) {
                this.ports[portNumber].setLink(link);
            }
        }
    }

    /**
     * This method returns the link that is connected to the port having the
     * specified port number.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portNumber The port number of the port where the desired linkg is
     * connected.
     * @return Link connected to the specified port. If the port is not
     * connected to a link, returns NULL.
     * @since 1.0
     */
    @Override
    public TLink getLinkConnectedToPort(int portNumber) {
        if (portNumber < this.numberOfPorts) {
            if (!this.ports[portNumber].isAvailable()) {
                return this.ports[portNumber].getLink();
            }
        }
        return null;
    }

    /**
     * This method disconnect a link from a given port, making this port
     * available.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portNumber the port number of the port whose link is going to be
     * disconnected from it.
     * @since 1.0
     */
    @Override
    public void disconnectLinkFromPort(int portNumber) {
        if ((portNumber >= 0) && (portNumber < this.numberOfPorts)) {
            this.ports[portNumber].disconnectLink();
        }
    }

    /**
     * This method reads and returns packet from one port of the port set. This
     * port will be selected automatically depending on the priority-based
     * algorithm running in the active port set.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a new packet thas was waiting in one port of the port set.
     * @since 1.0
     */
    @Override
    public TPDU isAnyPacketWaiting() {
        TPDU paqueteAux = null;
        // This modifies de value of this.nextPacketToBeRead
        // It also changes this.readPort and this.currentPriority
        this.runPriorityBasedNextPacketSelection();
        // End of packet selection based on priorities
        paqueteAux = this.nextPacketToBeRead;
        this.nextPacketToBeRead = null;
        return paqueteAux;
    }

    /**
     * This method check whether there are packets waiting in the incoming
     * buffer to be switched or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is at least one packet in one port buffer waiting
     * to be switched/routed. Otherwise, returns FALSE.
     * @since 1.0
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
     * buffer to be switched or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is at least one packet in one port buffer waiting
     * to be switched/routed. Otherwise, returns FALSE.
     * @since 1.0
     */
    @Override
    public boolean isAnyPacketToRoute() {
        return isAnyPacketToSwitch();
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
     * @since 1.0
     */
    @Override
    public boolean canSwitchPacket(int maxSwitchableOctects) {
        TPDU auxPacket = null;
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    @Override
    public TPort getPortWhereIsConectedANodeHavingIP(String adjacentNodeIP) {
        for (int i = 0; i < this.numberOfPorts; i++) {
            if (!this.ports[i].isAvailable()) {
                int targetNodeID = this.ports[i].getLink().getTargetNodeIDOfTrafficSentBy(this.parentNode);
                if (targetNodeID == TLink.END_NODE_1) {
                    if (this.ports[i].getLink().obtenerExtremo1().getIPAddress().equals(adjacentNodeIP)) {
                        return this.ports[i];
                    }
                } else {
                    if (this.ports[i].getLink().obtenerExtremo2().getIPAddress().equals(adjacentNodeIP)) {
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
     * @param portNumber The port number of the port to be queried.
     * @return IP address of the node that is connected to the specified port by
     * a link. If the port is not connected (is available), returns NULL.
     * @since 1.0
     */
    @Override
    public String getIPOfNodeLinkedTo(int portNumber) {
        if ((portNumber >= 0) && (portNumber < this.numberOfPorts)) {
            if (!this.ports[portNumber].isAvailable()) {
                String IP2 = this.ports[portNumber].getLink().obtenerExtremo2().getIPAddress();
                if (this.ports[portNumber].getLink().obtenerExtremo1().getIPAddress().equals(this.parentNode.getIPAddress())) {
                    return this.ports[portNumber].getLink().obtenerExtremo2().getIPAddress();
                }
                return this.ports[portNumber].getLink().obtenerExtremo1().getIPAddress();
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    @Override
    public void setArtificiallyCongested(boolean congestArtificially) {
        long computationOf97Percent = (long) (this.getBufferSizeInMB() * 1017118.72);
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
    private TPDU nextPacketToBeRead;
    private int[] ratioByPriority;
    private int[] currentByPriority;
    // end of comment
}
