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
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class implements a statistics collector for a LER.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLERStats extends TStats {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TLERStats.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLERStats() {
        this.incomingPackets = new XYSeriesCollection();
        this.outgoingPackets = new XYSeriesCollection();
        this.discardedPackets = new XYSeriesCollection();
        this.outgoingIPv4Packets = new XYSeries(TStats.IPV4);
        this.outgoingIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.outgoingIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.outgoingIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.outgoingMPLSPackets = new XYSeries(TStats.MPLS);
        this.outgoingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.outgoingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.outgoingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.outgoingTLDPPackets = new XYSeries(TStats.TLDP);
        this.outgoingGPSRPPackets = new XYSeries(TStats.GPSRP);
        this.incomingIPv4Packets = new XYSeries(TStats.IPV4);
        this.incomingIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.incomingIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.incomingIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.incomingMPLSPackets = new XYSeries(TStats.MPLS);
        this.incomingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.incomingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.incomingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.incomingTLDPPackets = new XYSeries(TStats.TLDP);
        this.incomingGPSRPPackets = new XYSeries(TStats.GPSRP);
        this.discardedIPv4Packets = new XYSeries(TStats.IPV4);
        this.discardedIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.discardedIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.discardedIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.discardedMPLSPackets = new XYSeries(TStats.MPLS);
        this.discardedMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.discardedMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.discardedMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.discardedTLDPPackets = new XYSeries(TStats.TLDP);
        this.discardedGPSRPPackets = new XYSeries(TStats.GPSRP);
        // Temporary data to be consolidated -----------
        // FIX: Do use class constants instead of hardcoded values.
        this.incomingIPv4PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.incomingMPLSPacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.incomingTLDPPacketsOfThisTimeInstant = 0;
        this.incomingGPSRPPacketsOfThisTimeInstant = 0;
        this.outgoingIPv4PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.outgoingTLDPPacketsOfThisTimeInstant = 0;
        this.outgoingGPSRPPacketsOfThisTimeInstant = 0;
        this.discardedIPv4PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.discardedMPLSPacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.discardedTLDPPacketsOfThisTimeInstant = 0;
        this.discardedGPSRPPacketsOfThisTimeInstant = 0;
        // ------------------------------------------
    }

    /**
     * This method returns the dataset #1 of the LER node associated to this
     * TLERStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #1 contains values related to incoming
     * packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #1 of this TLERStats that contains values related to
     * incoming packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset1() {
        return this.incomingPackets;
    }

    /**
     * This method returns the dataset #2 of the LER node associated to this
     * TLERStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #2 contains values related to outgoing
     * packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #2 of this TLERStats that contains values related to
     * outgoing packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset2() {
        return this.outgoingPackets;
    }

    /**
     * This method returns the dataset #3 of the LER node associated to this
     * TLERStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #3 contains values related to discarded
     * packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #3 of this TLERStats that contains values related to
     * discarded packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset3() {
        return this.discardedPackets;
    }

    /**
     * This method returns the dataset #4 of the LER node associated to this
     * TLERStats node that can be represented in a GUI or used by any other
     * statistics processor. There are not Dataset #4 in a TLERStats so null is
     * always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #4 in a TLERStats so null is returned.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset4() {
        return null;
    }

    /**
     * This method returns the dataset #5 of the LER node associated to this
     * TLERStats node that can be represented in a GUI or used by any other
     * statistics processor. There are not Dataset #5 in a TLERStats so null is
     * always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #5 in a TLERStats so null is returned.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset5() {
        return null;
    }

    /**
     * This method returns the dataset #6 of the LER node associated to this
     * TLERStats node that can be represented in a GUI or used by any other
     * statistics processor. There are not Dataset #6 in a TLERStats so null is
     * always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #6 in a TLERStats so null is returned.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset6() {
        return null;
    }

    /**
     * This method takes into account the packet and type of entry passed as a
     * parameter, updating the stats information to include this new
     * information.
     *
     * @param packet new packet as a source of information to update stats of
     * this TLERStats.
     * @param entryType INCOMING, OUTGOING o DISCARD, depending on whether the
     * packet is incoming, outgoing or being discarded in the LER associated to
     * this TLERStats.
     * @since 2.0
     */
    @Override
    public void addStatEntry(TAbstractPDU packet, int entryType) {
        if (this.statsEnabled) {
            int packetType = packet.getSubtype();
            // FIX: Do use class constants instead of hardcoded values.
            int GOSLevel = 0;
            // FIX: Use switch statement instead of such amount of nested ifs.
            if (packetType == TAbstractPDU.TLDP) {
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (entryType == TStats.OUTGOING) {
                    this.outgoingTLDPPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.BEING_DISCARDED) {
                    this.discardedTLDPPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.INCOMING) {
                    this.incomingTLDPPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.GPSRP) {
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (entryType == TStats.OUTGOING) {
                    this.outgoingGPSRPPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.BEING_DISCARDED) {
                    this.discardedGPSRPPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.INCOMING) {
                    this.incomingGPSRPPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.MPLS) {
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (entryType == TStats.OUTGOING) {
                    this.outgoingMPLSPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.BEING_DISCARDED) {
                    this.discardedMPLSPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.INCOMING) {
                    this.incomingMPLSPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.MPLS_GOS) {
                GOSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (entryType == TStats.OUTGOING) {
                    // FIX: Use switch statement instead of such amount of nested ifs.
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSPacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                } else if (entryType == TStats.BEING_DISCARDED) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.discardedMPLSPacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                } else if (entryType == TStats.INCOMING) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.incomingMPLSPacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.incomingMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.incomingMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.incomingMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                }
            } else if (packetType == TAbstractPDU.IPV4) {
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (entryType == TStats.OUTGOING) {
                    this.outgoingIPv4PacketsOfThisTimeInstant++;
                } else if (entryType == TStats.BEING_DISCARDED) {
                    this.discardedIPv4PacketsOfThisTimeInstant++;
                } else if (entryType == TStats.INCOMING) {
                    this.incomingIPv4PacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.IPV4_GOS) {
                GOSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (entryType == TStats.OUTGOING) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.outgoingIPv4PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.outgoingIPv4GOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.outgoingIPv4GOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.outgoingIPv4GOS3PacketsOfThisTimeInstant++;
                    }
                } else if (entryType == TStats.BEING_DISCARDED) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.discardedIPv4PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.discardedIPv4GOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.discardedIPv4GOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.discardedIPv4GOS3PacketsOfThisTimeInstant++;
                    }
                } else if (entryType == TStats.INCOMING) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.incomingIPv4PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.incomingIPv4GOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.incomingIPv4GOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.incomingIPv4GOS3PacketsOfThisTimeInstant++;
                    }
                }
            }
        }
    }

    /**
     * This method returns the number of datasets that are available in this
     * TLERStats.
     *
     * @return the number of available datasets in this TLERStats that are 3.
     * @since 2.0
     */
    @Override
    public int getNumberOfAvailableDatasets() {
        // FIX: do not use harcoded values. Use class constants instead.
        return 3;
    }

    /**
     * This method reset all the values and attribues of this TLERStats as in
     * the moment of its instantiation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        this.incomingPackets = new XYSeriesCollection();
        this.outgoingPackets = new XYSeriesCollection();
        this.discardedPackets = new XYSeriesCollection();
        this.outgoingIPv4Packets = new XYSeries(TStats.IPV4);
        this.outgoingIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.outgoingIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.outgoingIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.outgoingMPLSPackets = new XYSeries(TStats.MPLS);
        this.outgoingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.outgoingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.outgoingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.outgoingTLDPPackets = new XYSeries(TStats.TLDP);
        this.outgoingGPSRPPackets = new XYSeries(TStats.GPSRP);
        this.incomingIPv4Packets = new XYSeries(TStats.IPV4);
        this.incomingIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.incomingIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.incomingIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.incomingMPLSPackets = new XYSeries(TStats.MPLS);
        this.incomingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.incomingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.incomingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.incomingTLDPPackets = new XYSeries(TStats.TLDP);
        this.incomingGPSRPPackets = new XYSeries(TStats.GPSRP);
        this.discardedIPv4Packets = new XYSeries(TStats.IPV4);
        this.discardedIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.discardedIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.discardedIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.discardedMPLSPackets = new XYSeries(TStats.MPLS);
        this.discardedMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.discardedMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.discardedMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.discardedTLDPPackets = new XYSeries(TStats.TLDP);
        this.discardedGPSRPPackets = new XYSeries(TStats.GPSRP);
        // FIX: do not use harcoded values. Use class constants instead.
        this.incomingIPv4PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.incomingMPLSPacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.incomingTLDPPacketsOfThisTimeInstant = 0;
        this.incomingGPSRPPacketsOfThisTimeInstant = 0;
        this.outgoingIPv4PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.outgoingTLDPPacketsOfThisTimeInstant = 0;
        this.outgoingGPSRPPacketsOfThisTimeInstant = 0;
        this.discardedIPv4PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.discardedMPLSPacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.discardedTLDPPacketsOfThisTimeInstant = 0;
        this.discardedGPSRPPacketsOfThisTimeInstant = 0;
    }

    /**
     * This method groups the latests data added to this TLERStats by the time
     * instant passed as an argument. In this way aggregated statistics are
     * generated and the dataset contains only information that is relevant for
     * the units used in OpenSimMPLS (the tick or time instant). This reduces
     * the dataset size and the time of processing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param timeInstant the time instant (in simulation terms) by wich the
     * latest data contained in the datases will be grouped/aggregated.
     * @since 2.0
     */
    @Override
    public void groupStatsByTimeInstant(long timeInstant) {
        if (this.statsEnabled) {
            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingIPv4PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingIPv4Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4Packets.add(timeInstant, this.incomingIPv4PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingIPv4Packets);
                } else {
                    this.incomingIPv4Packets.add(timeInstant, incomingIPv4PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingIPv4GOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingIPv4GOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4GOS1Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4GOS1Packets.add(timeInstant, this.incomingIPv4GOS1PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingIPv4GOS1Packets);
                } else {
                    this.incomingIPv4GOS1Packets.add(timeInstant, this.incomingIPv4GOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingIPv4GOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingIPv4GOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4GOS2Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4GOS2Packets.add(timeInstant, this.incomingIPv4GOS2PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingIPv4GOS2Packets);
                } else {
                    this.incomingIPv4GOS2Packets.add(timeInstant, this.incomingIPv4GOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingIPv4GOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingIPv4GOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4GOS3Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4GOS3Packets.add(timeInstant, this.incomingIPv4GOS3PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingIPv4GOS3Packets);
                } else {
                    this.incomingIPv4GOS3Packets.add(timeInstant, this.incomingIPv4GOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSPackets.add(timeInstant - 1, 0);
                    this.incomingMPLSPackets.add(timeInstant, this.incomingMPLSPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSPackets);
                } else {
                    this.incomingMPLSPackets.add(timeInstant, this.incomingMPLSPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingMPLSGOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingMPLSGOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSGOS1Packets.add(timeInstant - 1, 0);
                    this.incomingMPLSGOS1Packets.add(timeInstant, this.incomingMPLSGOS1PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSGOS1Packets);
                } else {
                    this.incomingMPLSGOS1Packets.add(timeInstant, this.incomingMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingMPLSGOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingMPLSGOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSGOS2Packets.add(timeInstant - 1, 0);
                    this.incomingMPLSGOS2Packets.add(timeInstant, this.incomingMPLSGOS2PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSGOS2Packets);
                } else {
                    this.incomingMPLSGOS2Packets.add(timeInstant, this.incomingMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingMPLSGOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingMPLSGOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSGOS3Packets.add(timeInstant - 1, 0);
                    this.incomingMPLSGOS3Packets.add(timeInstant, this.incomingMPLSGOS3PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSGOS3Packets);
                } else {
                    this.incomingMPLSGOS3Packets.add(timeInstant, this.incomingMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingTLDPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingTLDPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingTLDPPackets.add(timeInstant - 1, 0);
                    this.incomingTLDPPackets.add(timeInstant, this.incomingTLDPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingTLDPPackets);
                } else {
                    this.incomingTLDPPackets.add(timeInstant, this.incomingTLDPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingGPSRPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.incomingGPSRPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingGPSRPPackets.add(timeInstant - 1, 0);
                    this.incomingGPSRPPackets.add(timeInstant, this.incomingGPSRPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingGPSRPPackets);
                } else {
                    this.incomingGPSRPPackets.add(timeInstant, this.incomingGPSRPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingIPv4PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingIPv4Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4Packets.add(timeInstant, this.outgoingIPv4PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingIPv4Packets);
                } else {
                    this.outgoingIPv4Packets.add(timeInstant, this.outgoingIPv4PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingIPv4GOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingIPv4GOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4GOS1Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4GOS1Packets.add(timeInstant, this.outgoingIPv4GOS1PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingIPv4GOS1Packets);
                } else {
                    this.outgoingIPv4GOS1Packets.add(timeInstant, this.outgoingIPv4GOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingIPv4GOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingIPv4GOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4GOS2Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4GOS2Packets.add(timeInstant, this.outgoingIPv4GOS2PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingIPv4GOS2Packets);
                } else {
                    this.outgoingIPv4GOS2Packets.add(timeInstant, this.outgoingIPv4GOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingIPv4GOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingIPv4GOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4GOS3Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4GOS3Packets.add(timeInstant, this.outgoingIPv4GOS3PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingIPv4GOS3Packets);
                } else {
                    this.outgoingIPv4GOS3Packets.add(timeInstant, this.outgoingIPv4GOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSPackets.add(timeInstant - 1, 0);
                    this.outgoingMPLSPackets.add(timeInstant, this.outgoingMPLSPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSPackets);
                } else {
                    this.outgoingMPLSPackets.add(timeInstant, this.outgoingMPLSPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingMPLSGOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingMPLSGOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSGOS1Packets.add(timeInstant - 1, 0);
                    this.outgoingMPLSGOS1Packets.add(timeInstant, this.outgoingMPLSGOS1PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSGOS1Packets);
                } else {
                    this.outgoingMPLSGOS1Packets.add(timeInstant, this.outgoingMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingMPLSGOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingMPLSGOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSGOS2Packets.add(timeInstant - 1, 0);
                    this.outgoingMPLSGOS2Packets.add(timeInstant, this.outgoingMPLSGOS2PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSGOS2Packets);
                } else {
                    this.outgoingMPLSGOS2Packets.add(timeInstant, this.outgoingMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingMPLSGOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingMPLSGOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSGOS3Packets.add(timeInstant - 1, 0);
                    this.outgoingMPLSGOS3Packets.add(timeInstant, this.outgoingMPLSGOS3PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSGOS3Packets);
                } else {
                    this.outgoingMPLSGOS3Packets.add(timeInstant, this.outgoingMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingTLDPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingTLDPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingTLDPPackets.add(timeInstant - 1, 0);
                    this.outgoingTLDPPackets.add(timeInstant, this.outgoingTLDPPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingTLDPPackets);
                } else {
                    this.outgoingTLDPPackets.add(timeInstant, this.outgoingTLDPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingGPSRPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingGPSRPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingGPSRPPackets.add(timeInstant - 1, 0);
                    this.outgoingGPSRPPackets.add(timeInstant, this.outgoingGPSRPPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingGPSRPPackets);
                } else {
                    this.outgoingGPSRPPackets.add(timeInstant, this.outgoingGPSRPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedIPv4PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedIPv4Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4Packets.add(timeInstant, this.discardedIPv4PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedIPv4Packets);
                } else {
                    this.discardedIPv4Packets.add(timeInstant, this.discardedIPv4PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedIPv4GOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedIPv4GOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4GOS1Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4GOS1Packets.add(timeInstant, this.discardedIPv4GOS1PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedIPv4GOS1Packets);
                } else {
                    this.discardedIPv4GOS1Packets.add(timeInstant, this.discardedIPv4GOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedIPv4GOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedIPv4GOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4GOS2Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4GOS2Packets.add(timeInstant, this.discardedIPv4GOS2PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedIPv4GOS2Packets);
                } else {
                    this.discardedIPv4GOS2Packets.add(timeInstant, this.discardedIPv4GOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedIPv4GOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedIPv4GOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4GOS3Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4GOS3Packets.add(timeInstant, this.discardedIPv4GOS3PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedIPv4GOS3Packets);
                } else {
                    this.discardedIPv4GOS3Packets.add(timeInstant, this.discardedIPv4GOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSPackets.add(timeInstant - 1, 0);
                    this.discardedMPLSPackets.add(timeInstant, this.discardedMPLSPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSPackets);
                } else {
                    this.discardedMPLSPackets.add(timeInstant, this.discardedMPLSPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedMPLSGOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedMPLSGOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSGOS1Packets.add(timeInstant - 1, 0);
                    this.discardedMPLSGOS1Packets.add(timeInstant, this.discardedMPLSGOS1PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSGOS1Packets);
                } else {
                    this.discardedMPLSGOS1Packets.add(timeInstant, this.discardedMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedMPLSGOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedMPLSGOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSGOS2Packets.add(timeInstant - 1, 0);
                    this.discardedMPLSGOS2Packets.add(timeInstant, this.discardedMPLSGOS2PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSGOS2Packets);
                } else {
                    this.discardedMPLSGOS2Packets.add(timeInstant, this.discardedMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedMPLSGOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedMPLSGOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSGOS3Packets.add(timeInstant - 1, 0);
                    this.discardedMPLSGOS3Packets.add(timeInstant, this.discardedMPLSGOS3PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSGOS3Packets);
                } else {
                    this.discardedMPLSGOS3Packets.add(timeInstant, this.discardedMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedTLDPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedTLDPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedTLDPPackets.add(timeInstant - 1, 0);
                    this.discardedTLDPPackets.add(timeInstant, this.discardedTLDPPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedTLDPPackets);
                } else {
                    this.discardedTLDPPackets.add(timeInstant, this.discardedTLDPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedGPSRPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedGPSRPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedGPSRPPackets.add(timeInstant - 1, 0);
                    this.discardedGPSRPPackets.add(timeInstant, this.discardedGPSRPPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedGPSRPPackets);
                } else {
                    this.discardedGPSRPPackets.add(timeInstant, this.discardedGPSRPPacketsOfThisTimeInstant);
                }
            }
        }
    }

    /**
     * This method returns the title of dataset #1 of this TLERStats node. In
     * this case is a descriptive text about "incoming packets".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about "incoming packets".
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset1() {
        return TStats.INCOMING_PACKETS;
    }

    /**
     * This method returns the title of dataset #2 of this TLERStats node. In
     * this case is a descriptive text about "outgoing packets".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about "outgoing packets".
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset2() {
        return TStats.OUTGOING_PACKETS;
    }

    /**
     * This method returns the title of dataset #3 of this TLERStats node. In
     * this case is a descriptive text about "discarded packets".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about "discarded packets".
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset3() {
        return TStats.DISCARDED_PACKETS;
    }

    /**
     * This method returns the title of dataset #4 of this TLERStats node. There
     * is not a dataset #4 in a TLERStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #4 in a TLERStats so, null is always
     * returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset4() {
        return null;
    }

    /**
     * This method returns the title of dataset #5 of this TLERStats node. There
     * is not a dataset #5 in a TLERStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #5 in a TLERStats so, null is always
     * returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset5() {
        return null;
    }

    /**
     * This method returns the title of dataset #6 of this TLERStats node. There
     * is not a dataset #6 in a TLERStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #6 in a TLERStats so, null is always
     * returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset6() {
        return null;
    }

    private int incomingIPv4PacketsOfThisTimeInstant;
    private int incomingIPv4GOS1PacketsOfThisTimeInstant;
    private int incomingIPv4GOS2PacketsOfThisTimeInstant;
    private int incomingIPv4GOS3PacketsOfThisTimeInstant;
    private int incomingMPLSPacketsOfThisTimeInstant;
    private int incomingMPLSGOS1PacketsOfThisTimeInstant;
    private int incomingMPLSGOS2PacketsOfThisTimeInstant;
    private int incomingMPLSGOS3PacketsOfThisTimeInstant;
    private int incomingTLDPPacketsOfThisTimeInstant;
    private int incomingGPSRPPacketsOfThisTimeInstant;
    private int outgoingIPv4PacketsOfThisTimeInstant;
    private int outgoingIPv4GOS1PacketsOfThisTimeInstant;
    private int outgoingIPv4GOS2PacketsOfThisTimeInstant;
    private int outgoingIPv4GOS3PacketsOfThisTimeInstant;
    private int outgoingMPLSPacketsOfThisTimeInstant;
    private int outgoingMPLSGOS1PacketsOfThisTimeInstant;
    private int outgoingMPLSGOS2PacketsOfThisTimeInstant;
    private int outgoingMPLSGOS3PacketsOfThisTimeInstant;
    private int outgoingTLDPPacketsOfThisTimeInstant;
    private int outgoingGPSRPPacketsOfThisTimeInstant;
    private int discardedIPv4PacketsOfThisTimeInstant;
    private int discardedIPv4GOS1PacketsOfThisTimeInstant;
    private int discardedIPv4GOS2PacketsOfThisTimeInstant;
    private int discardedIPv4GOS3PacketsOfThisTimeInstant;
    private int discardedMPLSPacketsOfThisTimeInstant;
    private int discardedMPLSGOS1PacketsOfThisTimeInstant;
    private int discardedMPLSGOS2PacketsOfThisTimeInstant;
    private int discardedMPLSGOS3PacketsOfThisTimeInstant;
    private int discardedTLDPPacketsOfThisTimeInstant;
    private int discardedGPSRPPacketsOfThisTimeInstant;
    private XYSeriesCollection incomingPackets;
    private XYSeriesCollection outgoingPackets;
    private XYSeriesCollection discardedPackets;
    private XYSeries incomingIPv4Packets;
    private XYSeries incomingIPv4GOS1Packets;
    private XYSeries incomingIPv4GOS2Packets;
    private XYSeries incomingIPv4GOS3Packets;
    private XYSeries incomingMPLSPackets;
    private XYSeries incomingMPLSGOS1Packets;
    private XYSeries incomingMPLSGOS2Packets;
    private XYSeries incomingMPLSGOS3Packets;
    private XYSeries incomingTLDPPackets;
    private XYSeries incomingGPSRPPackets;
    private XYSeries outgoingIPv4Packets;
    private XYSeries outgoingIPv4GOS1Packets;
    private XYSeries outgoingIPv4GOS2Packets;
    private XYSeries outgoingIPv4GOS3Packets;
    private XYSeries outgoingMPLSPackets;
    private XYSeries outgoingMPLSGOS1Packets;
    private XYSeries outgoingMPLSGOS2Packets;
    private XYSeries outgoingMPLSGOS3Packets;
    private XYSeries outgoingTLDPPackets;
    private XYSeries outgoingGPSRPPackets;
    private XYSeries discardedIPv4Packets;
    private XYSeries discardedIPv4GOS1Packets;
    private XYSeries discardedIPv4GOS2Packets;
    private XYSeries discardedIPv4GOS3Packets;
    private XYSeries discardedMPLSPackets;
    private XYSeries discardedMPLSGOS1Packets;
    private XYSeries discardedMPLSGOS2Packets;
    private XYSeries discardedMPLSGOS3Packets;
    private XYSeries discardedTLDPPackets;
    private XYSeries discardedGPSRPPackets;
}
