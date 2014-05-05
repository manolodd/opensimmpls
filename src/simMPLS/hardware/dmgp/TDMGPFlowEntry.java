package simMPLS.hardware.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.protocols.TPDUMPLS;
import simMPLS.utils.TRotaryIDGenerator;
import simMPLS.utils.TMonitor;

/**
 * This class implements a flow entry for the DMGP memory.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TDMGPFlowEntry implements Comparable {

    /**
     * This method is the constructor. It creates a new TDMGPFlowEntry
     * instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingOrder Incoming order of the flow to the DMGP memory.
     * @since 1.0
     */
    public TDMGPFlowEntry(int incomingOrder) {
        this.order = incomingOrder;
        this.flowID = -1;
        this.assignedPercentage = 0;
        this.assignedOctects = 0;
        this.usedOctects = 0;
        this.entries = new TreeSet();
        this.monitor = new TMonitor();
        this.idGenerator = new TRotaryIDGenerator();
    }

    /**
     * This method establishes the flow identifier associated to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID The flow identifier.
     * @since 1.0
     */
    public void setFlowID(int flowID) {
        this.flowID = flowID;
    }

    /**
     * This method returns the identifier of the flow assigned to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The flow identifier.
     * @since 1.0
     */
    public int getFlowID() {
        return this.flowID;
    }

    /**
     * This method establishes the percentage of DMGP assigned to this flow.
     *
     * @param assignedPercentage Percentage of DMGP assigned to this flow.
     * @since 1.0
     */
    public void setAssignedPercentage(int assignedPercentage) {
        this.assignedPercentage = assignedPercentage;
    }

    /**
     * This method obtains the percentage of DMGP assigned to this flow..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Percentage of DMGP assigned to this flow.
     * @since 1.0
     */
    public int getAssignedPercentage() {
        return this.assignedPercentage;
    }

    /**
     * This method establishes the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param assignedOctects Number of DMGP octects assigned to this flow.
     * @since 1.0
     */
    public void setAssignedOctects(int assignedOctects) {
        this.assignedOctects = assignedOctects;
    }

    /**
     * This method obtains the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of DMGP octects assigned to this flow.
     * @since 1.0
     */
    public int getAssignedOctects() {
        return this.assignedOctects;
    }

    /**
     * This method establishes the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param usedOctects Number of DMGP octects currently used by the flow.
     * @since 1.0
     */
    public void setUsedOctects(int usedOctects) {
        this.usedOctects = usedOctects;
    }

    /**
     * This method obtains the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Number of DMGP octects currently used by the flow.
     * @since 1.0
     */
    public int getUsedOctects() {
        return this.usedOctects;
    }

    /**
     * This method obtains the tree that contains all the packets of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The tree containing all the packets of this flow.
     * @since 1.0
     */
    public TreeSet getEntries() {
        return this.entries;
    }

    /**
     * This method contains the order of incoming to the DMGP.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The incoming order.
     * @since 1.0
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * This method returns the monitor of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     * @return The monitor of this flow.
     */
    public TMonitor getMonitor() {
        return this.monitor;
    }

    private void releaseMemory(int octectsToBeReleased) {
        int releasedOctects = 0;
        Iterator it = entries.iterator();
        TDMGPEntry dmgpEntry = null;
        while ((it.hasNext()) && (releasedOctects < octectsToBeReleased)) {
            dmgpEntry = (TDMGPEntry) it.next();
            releasedOctects += dmgpEntry.getPacket().getSize();
            it.remove();
        }
        this.usedOctects -= releasedOctects;
    }

    /**
     * This method inserts a packet that belongs to this flow, in the tree of
     * packets. If there is available space, the packet is inserted. Otherwise
     * packets are reselased untill there are space. If after this release there
     * are no enough space, the packet is not inserted.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet Packet of this flow to be inserted in the DMGP.
     * @since 1.0
     */
    public void addPacket(TPDUMPLS packet) {
        this.monitor.lock();
        int availableOctects = this.assignedOctects - this.usedOctects;
        if (availableOctects >= packet.getSize()) {
            TDMGPEntry dmgpEntry = new TDMGPEntry(idGenerator.getNextID());
            dmgpEntry.setPacket(packet);
            this.usedOctects += packet.getSize();
            this.entries.add(dmgpEntry);
        } else {
            if (usedOctects >= packet.getSize()) {
                releaseMemory(packet.getSize());
                TDMGPEntry dmgpEntry = new TDMGPEntry(idGenerator.getNextID());
                dmgpEntry.setPacket(packet);
                this.usedOctects += packet.getSize();
                this.entries.add(dmgpEntry);
            } else {
                packet = null;
            }
        }
        this.monitor.unLock();
    }

    /**
     * This method compares this flow entry with another of the same type.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param o The entry to be compared with.
     * @return -1, 0, 1, depending on whether the current instance is lesser,
     * equal or greater than the instance passed as an argument. In terms of
     * shorting.
     * @since 1.0
     */
    @Override
    public int compareTo(Object o) {
        TDMGPFlowEntry edmgp = (TDMGPFlowEntry) o;
        if (this.order < edmgp.getOrder()) {
            return TDMGPFlowEntry.THIS_LOWER;
        }
        if (this.order > edmgp.getOrder()) {
            return TDMGPFlowEntry.THIS_GREATER;
        }
        return TDMGPFlowEntry.THIS_EQUAL;
    }

    private static final int THIS_LOWER = -1;
    private static final int THIS_EQUAL = 0;
    private static final int THIS_GREATER = 1;

    private int order;
    private int flowID;
    private int assignedPercentage;
    private int assignedOctects;
    private int usedOctects;
    private TreeSet entries;
    private TMonitor monitor;
    private TRotaryIDGenerator idGenerator;
}
