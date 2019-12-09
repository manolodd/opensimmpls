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
package com.manolodominguez.opensimmpls.hardware.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.commons.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.commons.TSemaphore;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a flow entry for the DMGP memory. A flow includes all
 * packets that shares the same origin and target nodes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TDMGPFlowEntry implements Comparable<TDMGPFlowEntry> {

    /**
     * This method is the constructor. It creates a new TDMGPFlowEntry instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param arrivalOrder Incoming arrivalOrder of the flow to the DMGP memory.
     * @since 2.0
     */
    public TDMGPFlowEntry(int arrivalOrder) {
        translations = ResourceBundle.getBundle(AvailableBundles.T_DMGP_FLOW_ENTRY.getPath());
        if (arrivalOrder < 0) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange")); 
        }
        this.arrivalOrder = arrivalOrder;
        flowID = DEFAULT_FLOWID;
        assignedPercentage = DEFAULT_ASSIGNED_PERCENTAGE;
        assignedOctects = DEFAULT_ASSIGNED_OCTECTS;
        usedOctects = DEFAULT_USED_OCTECTS;
        entries = new TreeSet<>();
        semaphore = new TSemaphore();
        idGenerator = new TRotaryIDGenerator();
    }

    /**
     * This method establishes the flow identifier associated to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID The flow identifier. Could be a negative int.
     * @since 2.0
     */
    public void setFlowID(int flowID) {
        this.flowID = flowID;
    }

    /**
     * This method returns the identifier of the flow assigned to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The flow identifier.
     * @since 2.0
     */
    public int getFlowID() {
        return flowID;
    }

    /**
     * This method establishes the percentage of DMGP assigned to this flow.
     *
     * @param assignedPercentage Percentage of DMGP assigned to this flow.
     * @since 2.0
     */
    public void setAssignedPercentage(int assignedPercentage) {
        if (assignedPercentage < 0) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange")); 
        }
        this.assignedPercentage = assignedPercentage;
    }

    /**
     * This method obtains the percentage of DMGP assigned to this flow..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Percentage of DMGP assigned to this flow.
     * @since 2.0
     */
    public int getAssignedPercentage() {
        return assignedPercentage;
    }

    /**
     * This method establishes the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param assignedOctects Number of DMGP octects assigned to this flow.
     * @since 2.0
     */
    public void setAssignedOctects(int assignedOctects) {
        if (assignedOctects < 0) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange")); 
        }
        this.assignedOctects = assignedOctects;
    }

    /**
     * This method obtains the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of DMGP octects assigned to this flow.
     * @since 2.0
     */
    public int getAssignedOctects() {
        return assignedOctects;
    }

    /**
     * This method establishes the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param usedOctects Number of DMGP octects currently used by the flow.
     * @since 2.0
     */
    public void setUsedOctects(int usedOctects) {
        if (usedOctects < 0) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange")); 
        }
        this.usedOctects = usedOctects;
    }

    /**
     * This method obtains the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Number of DMGP octects currently used by the flow.
     * @since 2.0
     */
    public int getUsedOctects() {
        return usedOctects;
    }

    /**
     * This method obtains the tree that contains all the packets of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The tree containing all the packets of this flow.
     * @since 2.0
     */
    public TreeSet<TDMGPEntry> getEntries() {
        return entries;
    }

    /**
     * This method contains the arrivalOrder of incoming to the DMGP.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The arrival order of this flow in relation to others in the
     * global DMGP.
     * @since 2.0
     */
    public int getArrivalOrder() {
        return arrivalOrder;
    }

    /**
     * This method returns the monitor of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return The monitor of this flow.
     */
    public TSemaphore getMonitor() {
        return semaphore;
    }

    private void releaseMemory(int octectsToBeReleased) {
        if (octectsToBeReleased < 0) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange")); 
        }
        int releasedOctects = ZERO;
        Iterator<TDMGPEntry> entriesIterator = entries.iterator();
        TDMGPEntry dmgpEntry = null;
        while ((entriesIterator.hasNext()) && (releasedOctects < octectsToBeReleased)) {
            dmgpEntry = entriesIterator.next();
            releasedOctects += dmgpEntry.getPacket().getSize();
            entriesIterator.remove();
        }
        usedOctects -= releasedOctects;
    }

    /**
     * This method inserts a packet that belongs to this flow, in the tree of
     * packets. If there is available space, the packet is inserted. Otherwise
     * packets are reselased untill there are space. If it is not possible even
     * releasing packets, the packet is not inserted (and the DMGP for this
     * packets remains intact).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsPacket Packet belonging to this flow to be inserted in the
     * DMGP.
     * @since 2.0
     */
    public void addPacket(TMPLSPDU mplsPacket) {
        if (mplsPacket == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument")); 
        }
        semaphore.setRed();
        int availableOctects = assignedOctects - usedOctects;
        if (assignedOctects >= mplsPacket.getSize()) {
            if (availableOctects >= mplsPacket.getSize()) {
                TDMGPEntry dmgpEntry = new TDMGPEntry(idGenerator.getNextIdentifier());
                dmgpEntry.setPacket(mplsPacket);
                usedOctects += mplsPacket.getSize();
                entries.add(dmgpEntry);
            } else {
                releaseMemory(mplsPacket.getSize() - availableOctects);
                TDMGPEntry dmgpEntry = new TDMGPEntry(idGenerator.getNextIdentifier());
                dmgpEntry.setPacket(mplsPacket);
                usedOctects += mplsPacket.getSize();
                entries.add(dmgpEntry);
            }
        } else {
            mplsPacket = null;
        }
        semaphore.setGreen();
    }

    /**
     * This method compares this flow entry with another of the same type.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherDMGPFlowEntry The entry to be compared with.
     * @return -1, 0, 1, depending on whether the current instance is lesser,
     * equal or greater than the instance passed as an argument. In terms of
     * shorting.
     * @since 2.0
     */
    @Override
    public int compareTo(TDMGPFlowEntry anotherDMGPFlowEntry) {
        if (anotherDMGPFlowEntry == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument")); 
        }
        if (arrivalOrder < anotherDMGPFlowEntry.getArrivalOrder()) {
            return TDMGPFlowEntry.THIS_LOWER;
        }
        if (arrivalOrder > anotherDMGPFlowEntry.getArrivalOrder()) {
            return TDMGPFlowEntry.THIS_GREATER;
        }
        return TDMGPFlowEntry.THIS_EQUAL;
    }

    private static final int THIS_LOWER = -1;
    private static final int THIS_EQUAL = 0;
    private static final int THIS_GREATER = 1;

    private static final int ZERO = 0;
    private static final int DEFAULT_FLOWID = -1;
    private static final int DEFAULT_ASSIGNED_PERCENTAGE = 0;
    private static final int DEFAULT_ASSIGNED_OCTECTS = 0;
    private static final int DEFAULT_USED_OCTECTS = 0;

    private final int arrivalOrder;
    private int flowID;
    private int assignedPercentage;
    private int assignedOctects;
    private int usedOctects;
    private final TreeSet<TDMGPEntry> entries;
    private final TSemaphore semaphore;
    private final TRotaryIDGenerator idGenerator;
    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TDMGPFlowEntry.class);
}
