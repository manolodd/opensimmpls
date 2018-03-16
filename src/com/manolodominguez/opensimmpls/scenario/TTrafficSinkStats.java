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
 * This class implements a statistics collector for a receiver node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTrafficSinkStats extends TStats {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TReceiverStats.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTrafficSinkStats() {
        this.incomingPackets = new XYSeriesCollection();
        this.incomingIPv4Packets = new XYSeries(TStats.IPV4);
        this.incomingIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.incomingIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.incomingIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.incomingMPLSPackets = new XYSeries(TStats.MPLS);
        this.incomingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.incomingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.incomingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.incomingGPSRPPackets = new XYSeries(TStats.GPSRP);
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
        this.incomingGPSRPPacketsOfThisTimeInstant = 0;
        // ------------------------------------------
    }

    /**
     * This method returns the dataset #1 of the receiver nodeassociated to this
     * TReceiverStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #1 contains values related to incoming
     * packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #1 of this TReceiverStats contains values related to
     * incoming packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset1() {
        return this.incomingPackets;
    }

    /**
     * This method returns the dataset #2 of the receiver nodeassociated to this
     * TReceiverStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #2 for receiver nodes is always null.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return for receiver nodes is always null.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset2() {
        return null;
    }

    /**
     * This method returns the dataset #3 of the receiver nodeassociated to this
     * TReceiverStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #3 for receiver nodes is always null.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return for receiver nodes is always null.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset3() {
        return null;
    }

    /**
     * This method returns the dataset #4 of the receiver nodeassociated to this
     * TReceiverStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #4 for receiver nodes is always null.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return for receiver nodes is always null.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset4() {
        return null;
    }

    /**
     * This method returns the dataset #5 of the receiver nodeassociated to this
     * TReceiverStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #5 for receiver nodes is always null.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return for receiver nodes is always null.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset5() {
        return null;
    }

    /**
     * This method returns the dataset #6 of the receiver nodeassociated to this
     * TReceiverStats that can be represented in a GUI or used by any other
     * statistics processor. Dataset #6 for receiver nodes is always null.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return for receiver nodes is always null.
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
     * this TReceiverStats.
     * @param entryType INCOMING, OUTGOING o DISCARD, depending on whether the
     * packet is incoming, outgoing or being discarded in the receiver node
     * associated to this TReceiverStats.
     * @since 2.0
     */
    @Override
    public void addStatEntry(TAbstractPDU packet, int entryType) {
        if (this.statsEnabled) {
            int packetType = packet.getSubtype();
            // FIX: Do use class constants instead of hardcoded values.
            int GOSLevel = 0;
            // FIX: Use switch statement instead of such amount of nested ifs.
            if (packetType == TAbstractPDU.GPSRP) {
                if (entryType == TStats.INCOMING) {
                    this.incomingGPSRPPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.MPLS) {
                if (entryType == TStats.INCOMING) {
                    this.incomingMPLSPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.MPLS_GOS) {
                GOSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                if (entryType == TStats.INCOMING) {
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
                if (entryType == TStats.INCOMING) {
                    this.incomingIPv4PacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.IPV4_GOS) {
                GOSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                if (entryType == TStats.INCOMING) {
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
     * TReceiverStats.
     *
     * @return the number of available datasets in this TReceiverStats that are
     * 1.
     * @since 2.0
     */
    @Override
    public int getNumberOfAvailableDatasets() {
        // FIX: do not use harcoded values. Use class constants instead.
        return 1;
    }

    /**
     * This method reset all the values and attribues of this TReceiverStats as
     * in the moment of its instantiation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        this.incomingPackets = new XYSeriesCollection();
        this.incomingIPv4Packets = new XYSeries(TStats.IPV4);
        this.incomingIPv4GOS1Packets = new XYSeries(TStats.IPV4_GOS1);
        this.incomingIPv4GOS2Packets = new XYSeries(TStats.IPV4_GOS2);
        this.incomingIPv4GOS3Packets = new XYSeries(TStats.IPV4_GOS3);
        this.incomingMPLSPackets = new XYSeries(TStats.MPLS);
        this.incomingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
        this.incomingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
        this.incomingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
        this.incomingGPSRPPackets = new XYSeries(TStats.GPSRP);
        // FIX: do not use harcoded values. Use class constants instead.
        this.incomingIPv4PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS1PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS2PacketsOfThisTimeInstant = 0;
        this.incomingIPv4GOS3PacketsOfThisTimeInstant = 0;
        this.incomingMPLSPacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.incomingGPSRPPacketsOfThisTimeInstant = 0;
    }

    /**
     * This method groups the latests data added to this TReceiverStats by the
     * time instant passed as an argument. In this way aggregated statistics are
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
            if (incomingIPv4PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingIPv4Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4Packets.add(timeInstant, incomingIPv4PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingIPv4Packets);
                } else {
                    this.incomingIPv4Packets.add(timeInstant, incomingIPv4PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingIPv4GOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingIPv4GOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4GOS1Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4GOS1Packets.add(timeInstant, incomingIPv4GOS1PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingIPv4GOS1Packets);
                } else {
                    this.incomingIPv4GOS1Packets.add(timeInstant, incomingIPv4GOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingIPv4GOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingIPv4GOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4GOS2Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4GOS2Packets.add(timeInstant, incomingIPv4GOS2PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingIPv4GOS2Packets);
                } else {
                    this.incomingIPv4GOS2Packets.add(timeInstant, incomingIPv4GOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingIPv4GOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingIPv4GOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingIPv4GOS3Packets.add(timeInstant - 1, 0);
                    this.incomingIPv4GOS3Packets.add(timeInstant, incomingIPv4GOS3PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingIPv4GOS3Packets);
                } else {
                    this.incomingIPv4GOS3Packets.add(timeInstant, incomingIPv4GOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSPackets.add(timeInstant - 1, 0);
                    this.incomingMPLSPackets.add(timeInstant, incomingMPLSPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingMPLSPackets);
                } else {
                    this.incomingMPLSPackets.add(timeInstant, incomingMPLSPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingMPLSGOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingMPLSGOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSGOS1Packets.add(timeInstant - 1, 0);
                    this.incomingMPLSGOS1Packets.add(timeInstant, incomingMPLSGOS1PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingMPLSGOS1Packets);
                } else {
                    this.incomingMPLSGOS1Packets.add(timeInstant, incomingMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingMPLSGOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingMPLSGOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSGOS2Packets.add(timeInstant - 1, 0);
                    this.incomingMPLSGOS2Packets.add(timeInstant, incomingMPLSGOS2PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingMPLSGOS2Packets);
                } else {
                    this.incomingMPLSGOS2Packets.add(timeInstant, incomingMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingMPLSGOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingMPLSGOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSGOS3Packets.add(timeInstant - 1, 0);
                    this.incomingMPLSGOS3Packets.add(timeInstant, incomingMPLSGOS3PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingMPLSGOS3Packets);
                } else {
                    this.incomingMPLSGOS3Packets.add(timeInstant, incomingMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (incomingGPSRPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingGPSRPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingGPSRPPackets.add(timeInstant - 1, 0);
                    this.incomingGPSRPPackets.add(timeInstant, incomingGPSRPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingGPSRPPackets);
                } else {
                    this.incomingGPSRPPackets.add(timeInstant, incomingGPSRPPacketsOfThisTimeInstant);
                }
            }
        }
    }

    /**
     * This method returns the title of dataset #1 of this TReceiverStats. In
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
     * This method returns the title of dataset #2 of this TReceiverStats. There
     * is not a dataset #2 in a TReceiverStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #2 in a TReceiverStats so, null is always
     * returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset2() {
        return null;
    }

    /**
     * This method returns the title of dataset #3 of this TReceiverStats. There
     * is not a dataset #3 in a TReceiverStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #3 in a TReceiverStats so, null is always
     * returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset3() {
        return null;
    }

    /**
     * This method returns the title of dataset #4 of this TReceiverStats. There
     * is not a dataset #4 in a TReceiverStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #4 in a TReceiverStats so, null is always
     * returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset4() {
        return null;
    }

    /**
     * This method returns the title of dataset #5 of this TReceiverStats. There
     * is not a dataset #5 in a TReceiverStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #5 in a TReceiverStats so, null is always
     * returned.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset5() {
        return null;
    }

    /**
     * This method returns the title of dataset #6 of this TReceiverStats. There
     * is not a dataset #6 in a TReceiverStats so, null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #6 in a TReceiverStats so, null is always
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
    private int incomingGPSRPPacketsOfThisTimeInstant;
    private XYSeriesCollection incomingPackets;
    private XYSeries incomingIPv4Packets;
    private XYSeries incomingIPv4GOS1Packets;
    private XYSeries incomingIPv4GOS2Packets;
    private XYSeries incomingIPv4GOS3Packets;
    private XYSeries incomingMPLSPackets;
    private XYSeries incomingMPLSGOS1Packets;
    private XYSeries incomingMPLSGOS2Packets;
    private XYSeries incomingMPLSGOS3Packets;
    private XYSeries incomingGPSRPPackets;
}
