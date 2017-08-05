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

import org.jfree.data.AbstractDataset;
import com.manolodominguez.opensimmpls.protocols.TGPSRPPDU;
import com.manolodominguez.opensimmpls.protocols.TGPSRPPayload;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

/**
 * This class implements a statistics collector for an active LSR.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TActiveLSRStats extends TStats {
    
    /**
     * This method is the constructor of the class. It creates a new instance of
     * TActiveLSRStats.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TActiveLSRStats() {
    	this.incomingPackets = new XYSeriesCollection();
    	this.outgoingPackets = new XYSeriesCollection();
    	this.discardedPackets = new XYSeriesCollection();
    	this.outgoingMPLSPackets = new XYSeries(TStats.MPLS);
    	this.outgoingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
    	this.outgoingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
    	this.outgoingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
    	this.outgoingTLDPPackets = new XYSeries(TStats.TLDP);
    	this.outgoingGPSRPPackets = new XYSeries(TStats.GPSRP);
    	this.incomingMPLSPackets = new XYSeries(TStats.MPLS);
    	this.incomingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
    	this.incomingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
    	this.incomingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
    	this.incomingTLDPPackets = new XYSeries(TStats.TLDP);
    	this.incomingGPSRPPackets = new XYSeries(TStats.GPSRP);
	this.discardedMPLSPackets = new XYSeries(TStats.MPLS);
	this.discardedMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
	this.discardedMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
	this.discardedMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
    	this.discardedTLDPPackets = new XYSeries(TStats.TLDP);
    	this.discardedGPSRPPackets = new XYSeries(TStats.GPSRP);
        // Temporary data to be consolidated -----------
        // FIX: Do use class constants instead of hardcoded values.
        this.incomingMPLSPacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.incomingTLDPPacketsOfThisTimeInstant = 0;
        this.incomingGPSRPPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.outgoingTLDPPacketsOfThisTimeInstant = 0;
        this.outgoingGPSRPPacketsOfThisTimeInstant = 0;
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
     * Este m�todo permite obtener los datos necesario para generar la gr�fica 1.
     * @return Datos de la gr�fica 1.
     * @since 2.0
     */    
    @Override
    public AbstractDataset getDataset1() {
        return this.incomingPackets;
    }
    
    /**
     * Este m�todo permite obtener los datos necesario para generar la gr�fica 2.
     * @return Datos de la gr�fica 2.
     * @since 2.0
     */    
    @Override
    public AbstractDataset getDataset2() {
        return this.outgoingPackets;
    }
    
    /**
     * Este m�todo permite obtener los datos necesario para generar la gr�fica 3.
     * @return Datos de la gr�fica 3.
     * @since 2.0
     */    
    @Override
    public AbstractDataset getDataset3() {
        return this.discardedPackets;
    }
    
    /**
     * Este m�todo permite obtener los datos necesario para generar la gr�fica 4.
     * @return Datos de la gr�fica 4.
     * @since 2.0
     */    
    @Override
    public AbstractDataset getDataset4() {
        return this.retransmissionsManaged;
    }
    
    /**
     * Este m�todo permite obtener los datos necesario para generar la gr�fica 5.
     * @return Datos de la gr�fica 5.
     * @since 2.0
     */    
    @Override
    public AbstractDataset getDataset5() {
        return this.localRecoveriesManaged;
    }
    
    /**
     * Este m�todo permite obtener los datos necesario para generar la gr�fica 6.
     * @return Datos de la gr�fica 6.
     * @since 2.0
     */    
    @Override
    public AbstractDataset getDataset6() {
        return null;
    }

    /**
     * Este m�todo amplia las estad�sticas a�adiendo las correspondientes al packet
 especificado.
     * @param packet Paquete que se desea contabilizar.
     * @param entryType INCOMING, OUTGOING o DISCARD, dependiendo de si el packet entra en el nodo, sale
 de �l o es descartado.
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
                    // FIX: Use switch statement instead of such amount of nested ifs.
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
            }
        }
    }
    
    /**
     * Este m�todo permite obtener el n�mero de graficas del LSRA.
     * @return El n�mero de gr�ficas del LSRA.
     * @since 2.0
     */    
    @Override
    public int numberOfAvailableDatasets() {
        // FIX: do not use harcoded values. Use class constants instead.
        return 5;
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase, dejando las instancia como si
     * acabase de ser creada por el constructor.
     * @since 2.0
     */    
    @Override
    public void reset() {
    	this.incomingPackets = new XYSeriesCollection();
    	this.outgoingPackets = new XYSeriesCollection();
    	this.discardedPackets = new XYSeriesCollection();
    	this.outgoingMPLSPackets = new XYSeries(TStats.MPLS);
    	this.outgoingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
    	this.outgoingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
    	this.outgoingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
    	this.outgoingTLDPPackets = new XYSeries(TStats.TLDP);
    	this.outgoingGPSRPPackets = new XYSeries(TStats.GPSRP);
    	this.incomingMPLSPackets = new XYSeries(TStats.MPLS);
    	this.incomingMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
    	this.incomingMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
    	this.incomingMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
    	this.incomingTLDPPackets = new XYSeries(TStats.TLDP);
    	this.incomingGPSRPPackets = new XYSeries(TStats.GPSRP);
	this.discardedMPLSPackets = new XYSeries(TStats.MPLS);
	this.discardedMPLSGOS1Packets = new XYSeries(TStats.MPLS_GOS1);
	this.discardedMPLSGOS2Packets = new XYSeries(TStats.MPLS_GOS2);
	this.discardedMPLSGOS3Packets = new XYSeries(TStats.MPLS_GOS3);
    	this.discardedTLDPPackets = new XYSeries(TStats.TLDP);
    	this.discardedGPSRPPackets = new XYSeries(TStats.GPSRP);
        // FIX: do not use harcoded values. Use class constants instead.
        this.incomingMPLSPacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.incomingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.incomingTLDPPacketsOfThisTimeInstant = 0;
        this.incomingGPSRPPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSPacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS1PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS2PacketsOfThisTimeInstant = 0;
        this.outgoingMPLSGOS3PacketsOfThisTimeInstant = 0;
        this.outgoingTLDPPacketsOfThisTimeInstant = 0;
        this.outgoingGPSRPPacketsOfThisTimeInstant = 0;
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
     * Este m�todo actualiza las estad�sticas con los ultimos datos recogidos desde la
     * �ltima llamada a este m�todo.
     * @param timeInstant Instante de tiempo al que se atribuir�n los ultimso datos.
     * @since 2.0
     */    
    @Override
    public void consolidateData(long timeInstant) {
        if (this.statsEnabled) {
            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingMPLSPackets.add(timeInstant-1, 0);
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
                    this.incomingMPLSGOS1Packets.add(timeInstant-1, 0);
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
                    this.incomingMPLSGOS2Packets.add(timeInstant-1, 0);
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
                    this.incomingMPLSGOS3Packets.add(timeInstant-1, 0);
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
                    this.incomingTLDPPackets.add(timeInstant-1, 0);
                    this.incomingTLDPPackets.add(timeInstant, this.incomingTLDPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingTLDPPackets);
                } else {
                    this.incomingTLDPPackets.add(timeInstant, this.incomingTLDPPacketsOfThisTimeInstant);
                }
            }
            
            // FIX: do not use harcoded values. Use class constants instead.
            if (this.incomingGPSRPPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (incomingGPSRPPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.incomingGPSRPPackets.add(timeInstant-1, 0);
                    this.incomingGPSRPPackets.add(timeInstant, this.incomingGPSRPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingGPSRPPackets);
                } else {
                    this.incomingGPSRPPackets.add(timeInstant, this.incomingGPSRPPacketsOfThisTimeInstant);
                }
            }
                    
            // FIX: do not use harcoded values. Use class constants instead.
            if (this.outgoingMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.outgoingMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.outgoingMPLSPackets.add(timeInstant-1, 0);
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
                    this.outgoingMPLSGOS1Packets.add(timeInstant-1, 0);
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
                    this.outgoingMPLSGOS2Packets.add(timeInstant-1, 0);
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
                    this.outgoingMPLSGOS3Packets.add(timeInstant-1, 0);
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
                    this.outgoingTLDPPackets.add(timeInstant-1, 0);
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
                    this.outgoingGPSRPPackets.add(timeInstant-1, 0);
                    this.outgoingGPSRPPackets.add(timeInstant, this.outgoingGPSRPPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingGPSRPPackets);
                } else {
                    this.outgoingGPSRPPackets.add(timeInstant, this.outgoingGPSRPPacketsOfThisTimeInstant);
                }
            }
                    
            // FIX: do not use harcoded values. Use class constants instead.
            if (this.discardedMPLSPacketsOfThisTimeInstant > 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                if (this.discardedMPLSPackets.getItemCount() == 0) {
                    // FIX: do not use harcoded values. Use class constants instead.
                    this.discardedMPLSPackets.add(timeInstant-1, 0);
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
                    this.discardedMPLSGOS1Packets.add(timeInstant-1, 0);
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
                    this.discardedMPLSGOS2Packets.add(timeInstant-1, 0);
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
                    this.discardedMPLSGOS3Packets.add(timeInstant-1, 0);
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
                    this.discardedTLDPPackets.add(timeInstant-1, 0);
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
                    this.discardedGPSRPPackets.add(timeInstant-1, 0);
                    this.discardedGPSRPPackets.add(timeInstant, this.discardedGPSRPPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedGPSRPPackets);
                } else {
                    this.discardedGPSRPPackets.add(timeInstant, this.discardedGPSRPPacketsOfThisTimeInstant);
                }
            }
            
            this.retransmissionsManaged.addValue(this.retransmissionRequestsReceived, TStats.RETRANSMISSION_REQUESTS_RECEIVED, "");
            this.retransmissionsManaged.addValue(this.retransmissionsRealized, TStats.RETRANSMISSIONS_REALIZED, "");
            this.retransmissionsManaged.addValue(this.retransmisionsUnrealized, TStats.RETRANSMISSIONS_UNREALIZED, "");
            this.localRecoveriesManaged.addValue(this.GOSPacketsLost, TStats.GOS_PACKETS_LOST, "");
            this.localRecoveriesManaged.addValue(this.retransmissionRequestsSent, TStats.RETRANSMISSION_REQUESTS_SENT, "");
            this.localRecoveriesManaged.addValue(this.GOSPacketsRecovered, TStats.GOS_PACKETS_RECOVERED, "");
            this.localRecoveriesManaged.addValue(this.GOSPacketsUnrecovered, TStats.GOS_PACKETS_UNRECOVERED, "");
            int retransmissionRequestsStillUnanswered = (this.retransmissionRequestsSent - this.GOSPacketsRecovered - this.GOSPacketsUnrecovered);
            // FIX: do not use harcoded values. Use class constants instead.
            if (retransmissionRequestsStillUnanswered < 0) {
                // FIX: do not use harcoded values. Use class constants instead.
                retransmissionRequestsStillUnanswered = 0;
            }
            this.localRecoveriesManaged.addValue(retransmissionRequestsStillUnanswered, TStats.RETRANSMISSION_REQUESTS_STILL_UNANSWERED, "");
        }
    }    
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 1.
     * @return T�tulo de la gr�fica 1.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset1() {
        return TStats.INCOMING_PACKETS;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 2.
     * @return T�tulo de la gr�fica 2.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset2() {
        return TStats.OUTGOING_PACKETS;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 3.
     * @return T�tulo de la gr�fica 3.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset3() {
        return TStats.DISCARDED_PACKETS;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 4.
     * @return T�tulo de la gr�fica 4.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset4() {
        return TStats.RETRANSMISSIONS_MANAGED;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 5.
     * @return T�tulo de la gr�fica 5.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset5() {
        return TStats.LOCAL_RECOVERIES_MANAGED;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 6.
     * @return T�tulo de la gr�fica 6.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset6() {
        return null;
    }
    
    private int incomingMPLSPacketsOfThisTimeInstant;
    private int incomingMPLSGOS1PacketsOfThisTimeInstant;
    private int incomingMPLSGOS2PacketsOfThisTimeInstant;
    private int incomingMPLSGOS3PacketsOfThisTimeInstant;
    private int incomingTLDPPacketsOfThisTimeInstant;
    private int incomingGPSRPPacketsOfThisTimeInstant;
    private int outgoingMPLSPacketsOfThisTimeInstant;
    private int outgoingMPLSGOS1PacketsOfThisTimeInstant;
    private int outgoingMPLSGOS2PacketsOfThisTimeInstant;
    private int outgoingMPLSGOS3PacketsOfThisTimeInstant;
    private int outgoingTLDPPacketsOfThisTimeInstant;
    private int outgoingGPSRPPacketsOfThisTimeInstant;
    private int discardedMPLSPacketsOfThisTimeInstant;
    private int discardedMPLSGOS1PacketsOfThisTimeInstant;
    private int discardedMPLSGOS2PacketsOfThisTimeInstant;
    private int discardedMPLSGOS3PacketsOfThisTimeInstant;
    private int discardedTLDPPacketsOfThisTimeInstant;
    private int discardedGPSRPPacketsOfThisTimeInstant;
    private XYSeriesCollection incomingPackets;
    private XYSeriesCollection outgoingPackets;
    private XYSeriesCollection discardedPackets;
    private XYSeries incomingMPLSPackets;
    private XYSeries incomingMPLSGOS1Packets;
    private XYSeries incomingMPLSGOS2Packets;
    private XYSeries incomingMPLSGOS3Packets;
    private XYSeries incomingTLDPPackets;
    private XYSeries incomingGPSRPPackets;
    private XYSeries outgoingMPLSPackets;
    private XYSeries outgoingMPLSGOS1Packets;
    private XYSeries outgoingMPLSGOS2Packets;
    private XYSeries outgoingMPLSGOS3Packets;
    private XYSeries outgoingTLDPPackets;
    private XYSeries outgoingGPSRPPackets;
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
