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

import java.util.TreeSet;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.commons.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.commons.TSemaphore;
import com.manolodominguez.opensimmpls.commons.UnitsTranslations;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a DMGP memory to save GoS-aware PDUs temporarily.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TDMGP {

    /**
     * This method is the class constructor. It creates a new instance of TDMGP
     * and initialize its attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TDMGP() {
        translations = ResourceBundle.getBundle(AvailableBundles.T_DMGP.getPath());
        semaphore = new TSemaphore();
        idGenerator = new TRotaryIDGenerator();
        flows = new TreeSet<>();
        totalAvailablePercentage = DEFAULT_TOTAL_AVAILABLE_PERCENTAGE;
        totalDMGPSizeInKB = DEFAULT_TOTAL_DMGP_SIZE_IN_KB;
        totalAssignedOctects = DEFAULT_TOTAL_ASSIGNED_OCTECTS;
    }

    /**
     * This method establish the global size of DMGP in kilobytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param totalDMGPSizeInKB Size in kilobytes.
     */
    public void setDMGPSizeInKB(int totalDMGPSizeInKB) {
        if (totalDMGPSizeInKB < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        this.totalDMGPSizeInKB = totalDMGPSizeInKB;
        reset();
    }

    /**
     * This method obtains the globals soze of DMGP in kilobites.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size in kilobytes.
     * @since 2.0
     */
    public int getDMGPSizeInKB() {
        return totalDMGPSizeInKB;
    }

    /**
     * This method look for a packet tagged as GoS within the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param globalFlowID Identifier of the flow the packet belongs to.
     * @param packetGlobalUniqueID Identifier of the packet.
     * @return The packet, if in the DMGP. NULL on the contrary.
     * @since 2.0
     */
    public TMPLSPDU getPacket(int globalFlowID, int packetGlobalUniqueID) {
        TMPLSPDU wantedPacket = null;
        TDMGPFlowEntry requestedDMGPFlowEntry = getFlow(globalFlowID);
        // If the requested globalFlowID is already created...
        if (requestedDMGPFlowEntry != null) {
            semaphore.setRed();
            for (TDMGPEntry dmgpEntry : requestedDMGPFlowEntry.getEntries()) {
                if (dmgpEntry.getPacketGoSGlobalUniqueIdentifier() == packetGlobalUniqueID) {
                    wantedPacket = dmgpEntry.getPacketClone();
                    semaphore.setGreen();
                    return wantedPacket;
                }
            }
            semaphore.setGreen();
        }
        return null;
    }

    /**
     * This method insert a new GoS packet into the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be inserted into the DMGP memory.
     * @since 2.0
     */
    public void addPacket(TMPLSPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        TDMGPFlowEntry dmgpFlowEntry = getFlow(packet);
        if (dmgpFlowEntry == null) { // The correponding flow, dows not exists
            dmgpFlowEntry = createFlow(packet);
        }
        if (dmgpFlowEntry != null) { // The corresponding flow already exists
            dmgpFlowEntry.addPacket(packet);
        } else {
            packet = null;
        }
    }

    /**
     * This method restores the value of all attributes as when created by the
     * constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        semaphore = null;
        idGenerator = null;
        flows = null;
        semaphore = new TSemaphore();
        idGenerator = new TRotaryIDGenerator();
        flows = new TreeSet<>();
        totalAvailablePercentage = DEFAULT_TOTAL_AVAILABLE_PERCENTAGE;
        totalAssignedOctects = DEFAULT_TOTAL_ASSIGNED_OCTECTS;
    }

    private int getDMGPSizeInOctects() {
        return (totalDMGPSizeInKB * UnitsTranslations.OCTETS_PER_KILOBYTE.getUnits());
    }

    private TDMGPFlowEntry getFlow(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        TDMGPFlowEntry dmgpFlowEntry = null;
        int globalFlowID = packet.getIPv4Header().getOriginIPv4Address().hashCode();
        dmgpFlowEntry = getFlow(globalFlowID);
        return dmgpFlowEntry;
    }

    private TDMGPFlowEntry getFlow(int globalFlowID) {
        semaphore.setRed();
        for (TDMGPFlowEntry dmgpFlowEntry : flows) {
            if (dmgpFlowEntry.getFlowID() == globalFlowID) {
                semaphore.setGreen();
                return dmgpFlowEntry;
            }
        }
        semaphore.setGreen();
        return null;
    }

    private TDMGPFlowEntry createFlow(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        semaphore.setRed();
        TDMGPFlowEntry dmgpFlowEntry = null;
        int globalFlowID = packet.getIPv4Header().getOriginIPv4Address().hashCode();
        int percentageToBeAssigned = ZERO;
        int octectsToBeAssigned = ZERO;
        if (totalAssignedOctects < getDMGPSizeInOctects()) {
            percentageToBeAssigned = getPercentageToBeAssigned(packet);
            octectsToBeAssigned = getOctectsToBeAssigned(packet);
            if (octectsToBeAssigned > ZERO) {
                totalAssignedOctects += octectsToBeAssigned;
                totalAvailablePercentage -= percentageToBeAssigned;
                dmgpFlowEntry = new TDMGPFlowEntry(idGenerator.getNextIdentifier());
                dmgpFlowEntry.setFlowID(globalFlowID);
                dmgpFlowEntry.setAssignedPercentage(percentageToBeAssigned);
                dmgpFlowEntry.setAssignedOctets(octectsToBeAssigned);
                flows.add(dmgpFlowEntry);
                semaphore.setGreen();
                return dmgpFlowEntry;
            }
        }
        semaphore.setGreen();
        return null;
    }

    private int getOctectsToBeAssigned(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        int reservedPercentage = getRequestedPercentage(packet);
        int reservedOctects = ZERO;
        if (totalAvailablePercentage > ZERO) {
            if (totalAvailablePercentage > reservedPercentage) {
                reservedOctects = ((this.getDMGPSizeInOctects() * reservedPercentage) / ONE_HUNDRED);
                return reservedOctects;
            } else {
                reservedOctects = getDMGPSizeInOctects() - totalAssignedOctects;
                return reservedOctects;
            }
        }
        return ZERO;
    }

    private int getPercentageToBeAssigned(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        int reservedPercentage = getRequestedPercentage(packet);
        if (totalAvailablePercentage > ZERO) {
            if (totalAvailablePercentage > reservedPercentage) {
                return reservedPercentage;
            } else {
                return totalAvailablePercentage;
            }
        }
        return ZERO;
    }

    private int getRequestedPercentage(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        int packetGoSLevel = ZERO;
        if (packet.getIPv4Header().getOptionsField().isUsed()) {
            packetGoSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
            // The following values have been defined by design. See "Guarantee
            // of Service (GoS) support over MPLS using Active Techniques"
            // proposal. It determines the number of GoS flows that can be 
            // using the DMGP in a given moment concurrently
            switch (packetGoSLevel) {
                case TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS3;
                case TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS3;
                case TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS2;
                case TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS2;
                case TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS1;
                case TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS1;
                case TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
                case TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
                default:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
            }
        } else {
            return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
        }
    }

    private TSemaphore semaphore;
    private TRotaryIDGenerator idGenerator;
    private TreeSet<TDMGPFlowEntry> flows;
    private int totalAvailablePercentage;
    private int totalDMGPSizeInKB;
    private int totalAssignedOctects;

    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TDMGP.class);

    private static final int DEFAULT_TOTAL_AVAILABLE_PERCENTAGE = 100;
    private static final int DEFAULT_TOTAL_DMGP_SIZE_IN_KB = 1;
    private static final int DEFAULT_TOTAL_ASSIGNED_OCTECTS = 0;
    private static final int ZERO = 0;
    private static final int ONE_HUNDRED = 100;
    // The following proportion is defined in the "Guarentee of Service (GoS) 
    // support over MPLS using Active Techniques" proposal.
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0 = 0;
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS1 = 4;
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS2 = 8;
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS3 = 12;
}
