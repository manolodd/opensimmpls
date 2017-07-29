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
package simMPLS.scenario;

import org.jfree.data.AbstractDataset;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import simMPLS.protocols.TGPSRPPDU;
import simMPLS.protocols.TGPSRPPayload;
import simMPLS.protocols.TAbstractPDU;

/**
 * This class implements a statistics collector for an active LER.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TActiveLERStats extends TStats {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TActiveLERStats.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TActiveLERStats() {
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
        this.ourgoingGPSRPPackets = new XYSeries(TStats.GPSRP);
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
        // FIX: Do use class constants instead of hardcoded values.
        this.retransmissionsManaged = new DefaultCategoryDataset();
        this.retransmissionRequestsReceived = 0;
        this.retransmissionsRealized = 0;
        this.retransmisionsUnrealized = 0;
        // FIX: Do use class constants instead of hardcoded values.
        this.localRecoveriesManaged = new DefaultCategoryDataset();
        this.GOSPacketsLost = 0;
        this.retransmissionRequestsSent = 0;
        this.GOSPacketsRecovered = 0;
        this.GOSPacketsUnrecovered = 0;
    }

    /**
     * This method returns the dataset #1 of the Active LER node associated to
     * this TActiveLERStats that can be represented in a GUI or used by any
     * other statistics processor. Dataset #1 contains values related to
     * incoming packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #1 of this TActiveLERStatsthat contains values
     * related to incoming packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset1() {
        return this.incomingPackets;
    }

    /**
     * This method returns the dataset #2 of the Active LER node associated to
     * this TActiveLERStats that can be represented in a GUI or used by any
     * other statistics processor. Dataset #2 contains values related to
     * outgoing packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #2 of this TActiveLERStats that contains values
     * related to outgoing packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset2() {
        return this.outgoingPackets;
    }

    /**
     * This method returns the dataset #3 of the Active LER node associated to
     * this TActiveLERStats that can be represented in a GUI or used by any
     * other statistics processor. Dataset #3 contains values related to
     * discarded packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #3 of this TActiveLERStats that contains values
     * related to discarded packets.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset3() {
        return this.discardedPackets;
    }

    /**
     * This method returns the dataset #4 of the Active LER node associated to
     * this TActiveLERStats node that can be represented in a GUI or used by any
     * other statistics processor. Dataset #4 contains values related to the
     * number of retransmissions that have been managed.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #4 of this TActiveLERStats that contains values
     * related to the number of retransmissions that have been managed.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset4() {
        return this.retransmissionsManaged;
    }

    /**
     * This method returns the dataset #5 of the Active LER node associated to
     * this TActiveLERStats node that can be represented in a GUI or used by any
     * other statistics processor. Dataset #5 contains values related to the
     * number of local recoveries that have been managed.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #5 of this TActiveLERStats that contains values
     * related to the number of local recoveries that have been managed.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset5() {
        return this.localRecoveriesManaged;
    }

    /**
     * This method returns the dataset #6 of the Active LER node associated to
     * this TActiveLERStats node that can be represented in a GUI or used by any
     * other statistics processor. There are not Dataset #6 in a TActiveLERStats
     * so null is always returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There are not Dataset #6 in a TActiveLERStats so null is
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
     * this TActiveLERStats.
     * @param entryType INCOMING, OUTGOING o DISCARD, dependiendo on whether the
     * packet is incoming, outgoing or being discarded in the Active LER
     * associated to this TActiveLERStats.
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
                if (entryType == TStats.OUTGOING) {
                    this.outgoingTLDPPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.DISCARD) {
                    this.discardedTLDPPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.INCOMING) {
                    this.incomingTLDPPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.GPSRP) {
                TGPSRPPDU GPSRPPacket = (TGPSRPPDU) packet;
                int messageType = GPSRPPacket.getGPSRPPayload().getGPSRPMessageType();
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (messageType == TGPSRPPayload.RETRANSMISSION_REQUEST) {
                    // Use switch statement instead of such amount of nested ifs.
                    if (entryType == TStats.OUTGOING) {
                        this.outgoingGPSRPPacketsOfThisTimeInstant++;
                        this.retransmissionRequestsSent++;
                    } else if (entryType == TStats.DISCARD) {
                        this.discardedGPSRPPacketsOfThisTimeInstant++;
                    } else if (entryType == TStats.INCOMING) {
                        this.incomingGPSRPPacketsOfThisTimeInstant++;
                        this.retransmissionRequestsReceived++;
                    }
                } else if (messageType == TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE) {
                    // FIX: Use switch statement instead of such amount of nested ifs.
                    if (entryType == TStats.OUTGOING) {
                        this.outgoingGPSRPPacketsOfThisTimeInstant++;
                        this.retransmisionsUnrealized++;
                    } else if (entryType == TStats.DISCARD) {
                        this.discardedGPSRPPacketsOfThisTimeInstant++;
                    } else if (entryType == TStats.INCOMING) {
                        this.incomingGPSRPPacketsOfThisTimeInstant++;
                        this.GOSPacketsUnrecovered++;
                    }
                } else if (messageType == TGPSRPPayload.RETRANSMISION_OK) {
                    // FIX: Use switch statement instead of such amount of nested ifs.
                    if (entryType == TStats.OUTGOING) {
                        this.outgoingGPSRPPacketsOfThisTimeInstant++;
                        this.retransmissionsRealized++;
                    } else if (entryType == TStats.DISCARD) {
                        this.discardedGPSRPPacketsOfThisTimeInstant++;
                    } else if (entryType == TStats.INCOMING) {
                        this.incomingGPSRPPacketsOfThisTimeInstant++;
                        this.GOSPacketsRecovered++;
                    }
                }
            } else if (packetType == TAbstractPDU.MPLS) {
                // FIX: Use switch statement instead of such amount of nested ifs.
                if (entryType == TStats.OUTGOING) {
                    this.outgoingMPLSPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.DISCARD) {
                    this.discardedMPLSPacketsOfThisTimeInstant++;
                } else if (entryType == TStats.INCOMING) {
                    this.incomingMPLSPacketsOfThisTimeInstant++;
                }
            } else if (packetType == TAbstractPDU.MPLS_GOS) {
                GOSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                // FIX: Use switch statement instead of such amount of nested ifs.
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
                    this.GOSPacketsLost++;
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
                } else if (entryType == TStats.DISCARD) {
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
     * TActiveLERStats.
     *
     * @return the number of available datasets in this TActiveLERStats that are
     * 5.
     * @since 2.0
     */
    @Override
    public int numberOfAvailableDatasets() {
        // FIX: do not use harcoded values. Use class constants instead.
        return 5;
    }

    /**
     * This method reset all the values and attribues of this TActiveLERStats as
     * in the moment of its instantiation.
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
        this.ourgoingGPSRPPackets = new XYSeries(TStats.GPSRP);
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
        this.retransmissionsManaged = new DefaultCategoryDataset();
        // FIX: do not use harcoded values. Use class constants instead.
        this.retransmissionRequestsReceived = 0;
        this.retransmissionsRealized = 0;
        this.retransmisionsUnrealized = 0;
        this.localRecoveriesManaged = new DefaultCategoryDataset();
        // FIX: do not use harcoded values. Use class constants instead.
        this.GOSPacketsLost = 0;
        this.retransmissionRequestsSent = 0;
        this.GOSPacketsRecovered = 0;
        this.GOSPacketsUnrecovered = 0;
    }

    /**
     * This method groups the latests data added to this TActiveLERStats by the
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
    public void consolidateData(long timeInstant) {
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
            if (incomingTLDPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingTLDPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingTLDPPackets.add(timeInstant - 1, 0);
                    this.incomingTLDPPackets.add(timeInstant, incomingTLDPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(incomingTLDPPackets);
                } else {
                    this.incomingTLDPPackets.add(timeInstant, incomingTLDPPacketsOfThisTimeInstant);
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

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingIPv4PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingIPv4Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4Packets.add(timeInstant, outgoingIPv4PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingIPv4Packets);
                } else {
                    this.outgoingIPv4Packets.add(timeInstant, outgoingIPv4PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingIPv4GOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingIPv4GOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4GOS1Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4GOS1Packets.add(timeInstant, outgoingIPv4GOS1PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingIPv4GOS1Packets);
                } else {
                    this.outgoingIPv4GOS1Packets.add(timeInstant, outgoingIPv4GOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingIPv4GOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingIPv4GOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4GOS2Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4GOS2Packets.add(timeInstant, outgoingIPv4GOS2PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingIPv4GOS2Packets);
                } else {
                    this.outgoingIPv4GOS2Packets.add(timeInstant, outgoingIPv4GOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingIPv4GOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingIPv4GOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingIPv4GOS3Packets.add(timeInstant - 1, 0);
                    this.outgoingIPv4GOS3Packets.add(timeInstant, outgoingIPv4GOS3PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingIPv4GOS3Packets);
                } else {
                    this.outgoingIPv4GOS3Packets.add(timeInstant, outgoingIPv4GOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSPackets.add(timeInstant - 1, 0);
                    this.outgoingMPLSPackets.add(timeInstant, outgoingMPLSPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingMPLSPackets);
                } else {
                    this.outgoingMPLSPackets.add(timeInstant, outgoingMPLSPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingMPLSGOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingMPLSGOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSGOS1Packets.add(timeInstant - 1, 0);
                    this.outgoingMPLSGOS1Packets.add(timeInstant, outgoingMPLSGOS1PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingMPLSGOS1Packets);
                } else {
                    this.outgoingMPLSGOS1Packets.add(timeInstant, outgoingMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingMPLSGOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingMPLSGOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSGOS2Packets.add(timeInstant - 1, 0);
                    this.outgoingMPLSGOS2Packets.add(timeInstant, outgoingMPLSGOS2PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingMPLSGOS2Packets);
                } else {
                    this.outgoingMPLSGOS2Packets.add(timeInstant, outgoingMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingMPLSGOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingMPLSGOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSGOS3Packets.add(timeInstant - 1, 0);
                    this.outgoingMPLSGOS3Packets.add(timeInstant, outgoingMPLSGOS3PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingMPLSGOS3Packets);
                } else {
                    this.outgoingMPLSGOS3Packets.add(timeInstant, outgoingMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingTLDPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (outgoingTLDPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingTLDPPackets.add(timeInstant - 1, 0);
                    this.outgoingTLDPPackets.add(timeInstant, outgoingTLDPPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(outgoingTLDPPackets);
                } else {
                    this.outgoingTLDPPackets.add(timeInstant, outgoingTLDPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (outgoingGPSRPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (ourgoingGPSRPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.ourgoingGPSRPPackets.add(timeInstant - 1, 0);
                    this.ourgoingGPSRPPackets.add(timeInstant, outgoingGPSRPPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(ourgoingGPSRPPackets);
                } else {
                    this.ourgoingGPSRPPackets.add(timeInstant, outgoingGPSRPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedIPv4PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedIPv4Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4Packets.add(timeInstant, discardedIPv4PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedIPv4Packets);
                } else {
                    this.discardedIPv4Packets.add(timeInstant, discardedIPv4PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedIPv4GOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedIPv4GOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4GOS1Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4GOS1Packets.add(timeInstant, discardedIPv4GOS1PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedIPv4GOS1Packets);
                } else {
                    this.discardedIPv4GOS1Packets.add(timeInstant, discardedIPv4GOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedIPv4GOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedIPv4GOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4GOS2Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4GOS2Packets.add(timeInstant, discardedIPv4GOS2PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedIPv4GOS2Packets);
                } else {
                    this.discardedIPv4GOS2Packets.add(timeInstant, discardedIPv4GOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedIPv4GOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedIPv4GOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedIPv4GOS3Packets.add(timeInstant - 1, 0);
                    this.discardedIPv4GOS3Packets.add(timeInstant, discardedIPv4GOS3PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedIPv4GOS3Packets);
                } else {
                    this.discardedIPv4GOS3Packets.add(timeInstant, discardedIPv4GOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSPackets.add(timeInstant - 1, 0);
                    this.discardedMPLSPackets.add(timeInstant, discardedMPLSPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedMPLSPackets);
                } else {
                    this.discardedMPLSPackets.add(timeInstant, discardedMPLSPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedMPLSGOS1PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedMPLSGOS1Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSGOS1Packets.add(timeInstant - 1, 0);
                    this.discardedMPLSGOS1Packets.add(timeInstant, discardedMPLSGOS1PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedMPLSGOS1Packets);
                } else {
                    this.discardedMPLSGOS1Packets.add(timeInstant, discardedMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedMPLSGOS2PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedMPLSGOS2Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSGOS2Packets.add(timeInstant - 1, 0);
                    this.discardedMPLSGOS2Packets.add(timeInstant, discardedMPLSGOS2PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedMPLSGOS2Packets);
                } else {
                    this.discardedMPLSGOS2Packets.add(timeInstant, discardedMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedMPLSGOS3PacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedMPLSGOS3Packets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSGOS3Packets.add(timeInstant - 1, 0);
                    this.discardedMPLSGOS3Packets.add(timeInstant, discardedMPLSGOS3PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedMPLSGOS3Packets);
                } else {
                    this.discardedMPLSGOS3Packets.add(timeInstant, discardedMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedTLDPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedTLDPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedTLDPPackets.add(timeInstant - 1, 0);
                    this.discardedTLDPPackets.add(timeInstant, discardedTLDPPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedTLDPPackets);
                } else {
                    this.discardedTLDPPackets.add(timeInstant, discardedTLDPPacketsOfThisTimeInstant);
                }
            }

            // FIX: do not use harcoded values. Use class constants instead.
            if (discardedGPSRPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (discardedGPSRPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedGPSRPPackets.add(timeInstant - 1, 0);
                    this.discardedGPSRPPackets.add(timeInstant, discardedGPSRPPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(discardedGPSRPPackets);
                } else {
                    this.discardedGPSRPPackets.add(timeInstant, discardedGPSRPPacketsOfThisTimeInstant);
                }
            }

            this.retransmissionsManaged.addValue(this.retransmissionRequestsReceived, TStats.RETRANSMISSION_REQUESTS_RECEIVED, "");
            this.retransmissionsManaged.addValue(this.retransmissionsRealized, TStats.RETRANSMISSIONS_REALIZED, "");
            this.retransmissionsManaged.addValue(this.retransmisionsUnrealized, TStats.RETRANSMISSIONS_UNREALIZED, "");
            this.localRecoveriesManaged.addValue(this.GOSPacketsLost, TStats.GOS_PACKETS_LOST, "");
            this.localRecoveriesManaged.addValue(this.retransmissionRequestsSent, TStats.RETRANSMISSION_REQUESTS_SENT, "");
            this.localRecoveriesManaged.addValue(this.GOSPacketsRecovered, TStats.GOS_PACKETS_RECOVERED, "");
            this.localRecoveriesManaged.addValue(this.GOSPacketsUnrecovered, TStats.GOS_PACKETS_UNRECOVERED, "");
            int retransmissionRequestsStillUnanswered = (retransmissionRequestsSent - GOSPacketsRecovered - GOSPacketsUnrecovered);
            // FIX: do not use harcoded values. Use class constants instead.
            if (retransmissionRequestsStillUnanswered < 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                retransmissionRequestsStillUnanswered = 0;
            }
            this.localRecoveriesManaged.addValue(retransmissionRequestsStillUnanswered, TStats.RETRANSMISSION_REQUESTS_STILL_UNANSWERED, "");
        }
    }

    /**
     * This method returns the title of dataset #1 of this TActiveLERStats node.
     * In this case is a descriptive text about "incoming packets".
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
     * This method returns the title of dataset #2 of this TActiveLERStats node.
     * In this case is a descriptive text about "outgoing packets".
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
     * This method returns the title of dataset #3 of this TActiveLERStats node.
     * In this case is a descriptive text about "discarded packets".
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
     * This method returns the title of dataset #4 of this TActiveLERStats node.
     * In this case is a descriptive text about "retransmissions managed".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about "retransmissions managed".
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset4() {
        return TStats.RETRANSMISSIONS_MANAGED;
    }

    /**
     * This method returns the title of dataset #5 of this TActiveLERStats node.
     * In this case is a descriptive text about "local recoveries".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about "local recoveries".
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset5() {
        return TStats.LOCAL_RECOVERIES_MANAGED;
    }

    /**
     * This method returns the title of dataset #6 of this TActiveLERStats node.
     * There is not a dataset #6 in a TActiveLERStats so, null is always
     * returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return There is not a dataset #6 in a TActiveLERStats so, null is always
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
    private XYSeries ourgoingGPSRPPackets;
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
    private DefaultCategoryDataset retransmissionsManaged;
    private int retransmissionRequestsReceived;
    private int retransmissionsRealized;
    private int retransmisionsUnrealized;
    private DefaultCategoryDataset localRecoveriesManaged;
    private int GOSPacketsLost;
    private int retransmissionRequestsSent;
    private int GOSPacketsRecovered;
    private int GOSPacketsUnrecovered;
}
