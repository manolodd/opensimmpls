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
import org.jfree.data.AbstractDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

/**
 * This class implements a statistics collector for a traffic generator node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTrafficGeneratorStats extends TStats {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TTrafficGeneratorStats.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTrafficGeneratorStats() {
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
        this.discardedIPv4Packets = new XYSeries(TStats.IPV4);
        this.discardedIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.discardedIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.discardedIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.discardedMPLSPackets = new XYSeries(TStats.MPLS);
        this.discardedMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.discardedMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.discardedMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        // Temporary data to be consolidated -----------
        // FIX: Do use class constants instead of hardcoded values.
        this.outgoingIPv4PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.discardedIPv4PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.discardedMPLSPacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS3PacketsOfThisTimeInstant = 0;
        // ------------------------------------------
    }

    /**
     * This method returns the dataset #1 of the traffic generator node
     * associated to this TTrafficGeneratorStats that can be represented in a
     * GUI or used by any other statistics processor. Dataset #1 contains values
     * related to outgoing packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #1 of this TTrafficGeneratorStats that contains
     * values related to outgoing packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset1() {
        return this.outgoingPackets;
    }

    /**
     * This method returns the dataset #2 of the traffic generator node
     * associated to this TTrafficGeneratorStats that can be represented in a
     * GUI or used by any other statistics processor. Dataset #2 contains values
     * related to discarded packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #2 of this TTrafficGeneratorStats that contains
     * values related to discarded packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset2() {
        return this.discardedPackets;
    }

    /**
     * This method returns the dataset #3 of the traffic generator node
     * associated to this TTrafficGeneratorStats node that can be represented in
     * a GUI or used by any other statistics processor. There are not Dataset #3
     * in a TTrafficGeneratorStats so null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #3 in a TTrafficGeneratorStats so null is
     * returned.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset3() {
        return null;
    }

    /**
     * This method returns the dataset #4 of the traffic generator node
     * associated to this TTrafficGeneratorStats node that can be represented in
     * a GUI or used by any other statistics processor. There are not Dataset #4
     * in a TTrafficGeneratorStats so null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #4 in a TTrafficGeneratorStats so null is
     * returned.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset4() {
        return null;
    }

    /**
     * This method returns the dataset #5 of the traffic generator node
     * associated to this TTrafficGeneratorStats node that can be represented in
     * a GUI or used by any other statistics processor. There are not Dataset #5
     * in a TTrafficGeneratorStats so null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #5 in a TTrafficGeneratorStats so null is
     * returned.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset5() {
        return null;
    }

    /**
     * This method returns the dataset #6 of the traffic generator node
     * associated to this TTrafficGeneratorStats node that can be represented in
     * a GUI or used by any other statistics processor. There are not Dataset #6
     * in a TTrafficGeneratorStats so null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #6 in a TTrafficGeneratorStats so null is
     * returned.
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
     * this TTrafficGeneratorStats.
     * @param entryType INCOMING, OUTGOING o DISCARD, depending on whether the
     * packet is incoming, outgoing or being discarded in the traffic generator
     * node associated to this TTrafficGeneratorStats.
     * @since 2.0
     */
    @Override
    public void addStatEntry(TAbstractPDU packet, int entryType) {
        if (this.statsEnabled) {
            int packetType = packet.getSubtype();
            // FIX: Do use class constants instead of hardcoded values.
            int GOSLevel = 0;
            // FIX: Use switch statement instead of such amount of nested ifs.
            if (packetType == TAbstractPDU.MPLS) {
                if (entryType == TStats.OUTGOING) {
                    this.outgoingMPLSPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.DISCARD) {
                    this.discardedMPLSPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.MPLS_GOS) {
                GOSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                if (entryType == TStats.OUTGOING) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSPacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                } else if (entryType == TStats.DISCARD) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.discardedMPLSPacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                }
            } else if (packetType == TAbstractPDU.IPV4) {
                if (entryType == TStats.OUTGOING) {
                    this.outgoingIPv4PacketsOfThisTimeInstant++;
                } else if (entryType == TStats.DISCARD) {
                    this.discardedIPv4PacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.IPV4_GOS) {
                GOSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
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
                } else if (entryType == TStats.DISCARD) {
                    if ((GOSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.discardedIPv4PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.discardedIPv4GOS1PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.discardedIPv4GOS2PacketsOfThisTimeInstant++;
                    } else if ((GOSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GOSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.discardedIPv4GOS3PacketsOfThisTimeInstant++;
                    }
                }
            }
        }
    }

    /**
     * This method returns the number of datasets that are available in this
     * TTrafficGeneratorStats.
     *
     * @return the number of available datasets in this TTrafficGeneratorStats
     * that are 2.
     * @since 2.0
     */
    @Override
    public int numberOfAvailableDatasets() {
        // FIX: do not use harcoded values. Use class constants instead.
        return 2;
    }

    /**
     * This method reset all the values and attribues of this
     * TTrafficGeneratorStats as in the moment of its instantiation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
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
        this.discardedIPv4Packets = new XYSeries(TStats.IPV4);
        this.discardedIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.discardedIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.discardedIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.discardedMPLSPackets = new XYSeries(TStats.MPLS);
        this.discardedMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.discardedMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.discardedMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        // FIX: do not use harcoded values. Use class constants instead.
        this.outgoingIPv4PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.outgoingIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.discardedIPv4PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.discardedIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.discardedMPLSPacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.discardedMPLSGOS3PacketsOfThisTimeInstant = 0;
    }

    /**
     * This method groups the latests data added to this TTrafficGeneratorStats
     * by the time instant passed as an argument. In this way aggregated
     * statistics are generated and the dataset contains only information that
     * is relevant for the units used in OpenSimMPLS (the tick or time instant).
     * This reduces the dataset size and the time of processing.
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
            if (this.outgoingIPv4PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingIPv4Packets.getItemCount() == 0) {
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
                if (this.outgoingIPv4GOS3Packets.getItemCount() == 0) {
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
        }
    }

    /**
     * This method returns the title of dataset #1 of this
     * TTrafficGeneratorStats. In this case is a descriptive text about
     * "ouutgoing packets".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about "outgoing packets".
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset1() {
        return TStats.OUTGOING_PACKETS;
    }

    /**
     * This method returns the title of dataset #1 of this
     * TTrafficGeneratorStats. In this case is a descriptive text about
     * "discarded packets".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about "discarded packets".
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset2() {
        return TStats.DISCARDED_PACKETS;
    }

    /**
     * This method returns the title of dataset #3 of this
     * TTrafficGeneratorStats. There is not a dataset #3 in a
     * TTrafficGeneratorStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #3 in a TTrafficGeneratorStats so, null is
     * always returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset3() {
        return null;
    }

    /**
     * This method returns the title of dataset #4 of this
     * TTrafficGeneratorStats. There is not a dataset #4 in a
     * TTrafficGeneratorStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #4 in a TTrafficGeneratorStats so, null is
     * always returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset4() {
        return null;
    }

    /**
     * This method returns the title of dataset #5 of this
     * TTrafficGeneratorStats. There is not a dataset #5 in a
     * TTrafficGeneratorStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #5 in a TTrafficGeneratorStats so, null is
     * always returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset5() {
        return null;
    }

    /**
     * This method returns the title of dataset #6 of this
     * TTrafficGeneratorStats. There is not a dataset #6 in a
     * TTrafficGeneratorStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #6 in a TTrafficGeneratorStats so, null is
     * always returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset6() {
        return null;
    }

    private int outgoingIPv4PacketsOfThisTimeInstant;
    private int outgoingIPv4GOS1PacketsOfThisTimeInstant;
    private int outgoingIPv4GOS2PacketsOfThisTimeInstant;
    private int outgoingIPv4GOS3PacketsOfThisTimeInstant;
    private int outgoingMPLSPacketsOfThisTimeInstant;
    private int outgoingMPLSGOS1PacketsOfThisTimeInstant;
    private int outgoingMPLSGOS2PacketsOfThisTimeInstant;
    private int outgoingMPLSGOS3PacketsOfThisTimeInstant;
    private int discardedIPv4PacketsOfThisTimeInstant;
    private int discardedIPv4GOS1PacketsOfThisTimeInstant;
    private int discardedIPv4GOS2PacketsOfThisTimeInstant;
    private int discardedIPv4GOS3PacketsOfThisTimeInstant;
    private int discardedMPLSPacketsOfThisTimeInstant;
    private int discardedMPLSGOS1PacketsOfThisTimeInstant;
    private int discardedMPLSGOS2PacketsOfThisTimeInstant;
    private int discardedMPLSGOS3PacketsOfThisTimeInstant;
    private XYSeriesCollection outgoingPackets;
    private XYSeriesCollection discardedPackets;
    private XYSeries outgoingIPv4Packets;
    private XYSeries outgoingIPv4GOS1Packets;
    private XYSeries outgoingIPv4GOS2Packets;
    private XYSeries outgoingIPv4GOS3Packets;
    private XYSeries outgoingMPLSPackets;
    private XYSeries outgoingMPLSGOS1Packets;
    private XYSeries outgoingMPLSGOS2Packets;
    private XYSeries outgoingMPLSGOS3Packets;
    private XYSeries discardedIPv4Packets;
    private XYSeries discardedIPv4GOS1Packets;
    private XYSeries discardedIPv4GOS2Packets;
    private XYSeries discardedIPv4GOS3Packets;
    private XYSeries discardedMPLSPackets;
    private XYSeries discardedMPLSGOS1Packets;
    private XYSeries discardedMPLSGOS2Packets;
    private XYSeries discardedMPLSGOS3Packets;
}
