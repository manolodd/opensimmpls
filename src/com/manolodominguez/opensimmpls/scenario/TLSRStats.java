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
 * This class implements a statistics collector for a LSR.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLSRStats extends TStats {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TLSRStats.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLSRStats() {
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
    }

    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 1.
     *
     * @return Datos necesarios para la gr�fica 1.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset1() {
        return this.incomingPackets;
    }

    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 2.
     *
     * @return Datos necesarios para la gr�fica 2.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset2() {
        return this.outgoingPackets;
    }

    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 3.
     *
     * @return Datos necesarios para la gr�fica 3.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset3() {
        return this.discardedPackets;
    }

    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 4.
     *
     * @return Datos necesarios para la gr�fica 4.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset4() {
        return null;
    }

    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 5.
     *
     * @return Datos necesarios para la gr�fica 5.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset5() {
        return null;
    }

    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 6.
     *
     * @return Datos necesarios para la gr�fica 6.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset6() {
        return null;
    }

    /**
     * Este m�todo permite aumentar las estad�sticas, a�adiendo las del paquete
     * especificado.
     *
     * @param paquete Paquete que se desea contabilizar.
     * @param entrada INCOMING, OUTGOING o DISCARD, dependiendo de si el paquete
     * entra en el nodo, sale de �l o es descartado.
     * @since 2.0
     */
    @Override
    public void addStatEntry(TAbstractPDU paquete, int entrada) {
        if (this.statsEnabled) {
            int tipoPaquete = paquete.getSubtype();
            int GoS = 0;
            if (tipoPaquete == TAbstractPDU.TLDP) {
                if (entrada == TStats.OUTGOING) {
                    this.outgoingTLDPPacketsOfThisTimeInstant++;
                } else if (entrada == TStats.DISCARD) {
                    this.discardedTLDPPacketsOfThisTimeInstant++;
                } else if (entrada == TStats.INCOMING) {
                    this.incomingTLDPPacketsOfThisTimeInstant++;
                }
            } else if (tipoPaquete == TAbstractPDU.GPSRP) {
                if (entrada == TStats.OUTGOING) {
                    this.outgoingGPSRPPacketsOfThisTimeInstant++;
                } else if (entrada == TStats.DISCARD) {
                    this.discardedGPSRPPacketsOfThisTimeInstant++;
                } else if (entrada == TStats.INCOMING) {
                    this.incomingGPSRPPacketsOfThisTimeInstant++;
                }
            } else if (tipoPaquete == TAbstractPDU.MPLS) {
                if (entrada == TStats.OUTGOING) {
                    this.outgoingMPLSPacketsOfThisTimeInstant++;
                } else if (entrada == TStats.DISCARD) {
                    this.discardedMPLSPacketsOfThisTimeInstant++;
                } else if (entrada == TStats.INCOMING) {
                    this.incomingMPLSPacketsOfThisTimeInstant++;
                }
            } else if (tipoPaquete == TAbstractPDU.MPLS_GOS) {
                GoS = paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                if (entrada == TStats.OUTGOING) {
                    if ((GoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSPacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.outgoingMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                } else if (entrada == TStats.DISCARD) {
                    if ((GoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.discardedMPLSPacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.discardedMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                } else if (entrada == TStats.INCOMING) {
                    if ((GoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.incomingMPLSPacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.incomingMPLSGOS1PacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.incomingMPLSGOS2PacketsOfThisTimeInstant++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.incomingMPLSGOS3PacketsOfThisTimeInstant++;
                    }
                }
            }
        }
    }

    /**
     * Este m�todo devuelve el n�mero de gr�ficas que genera el nodo LSR.
     *
     * @return El n�mero de graficas del nodo LSR.
     * @since 2.0
     */
    @Override
    public int numberOfAvailableDatasets() {
        return 3;
    }

    /**
     * Este m�todo reinicia los atributos de la clase, dejando la instancia como
     * si acabase de ser creada por el constructor.
     *
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
    }

    /**
     * Este m�todo actualiza las estadisticas con los �ltimos adtos recopilados
     * desde la �ltima vez que se llam� a este m�todo.
     *
     * @param instante Instante de tiempo al que se atribuiran los �ltimos datos
     * recolectados.
     * @since 2.0
     */
    @Override
    public void consolidateData(long instante) {
        if (this.statsEnabled) {
            if (this.incomingMPLSPacketsOfThisTimeInstant > 0) {
                if (this.incomingMPLSPackets.getItemCount() == 0) {
                    this.incomingMPLSPackets.add(instante - 1, 0);
                    this.incomingMPLSPackets.add(instante, this.incomingMPLSPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSPackets);
                } else {
                    this.incomingMPLSPackets.add(instante, this.incomingMPLSPacketsOfThisTimeInstant);
                }
            }

            if (this.incomingMPLSGOS1PacketsOfThisTimeInstant > 0) {
                if (this.incomingMPLSGOS1Packets.getItemCount() == 0) {
                    this.incomingMPLSGOS1Packets.add(instante - 1, 0);
                    this.incomingMPLSGOS1Packets.add(instante, this.incomingMPLSGOS1PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSGOS1Packets);
                } else {
                    this.incomingMPLSGOS1Packets.add(instante, this.incomingMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            if (this.incomingMPLSGOS2PacketsOfThisTimeInstant > 0) {
                if (this.incomingMPLSGOS2Packets.getItemCount() == 0) {
                    this.incomingMPLSGOS2Packets.add(instante - 1, 0);
                    this.incomingMPLSGOS2Packets.add(instante, this.incomingMPLSGOS2PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSGOS2Packets);
                } else {
                    this.incomingMPLSGOS2Packets.add(instante, this.incomingMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            if (this.incomingMPLSGOS3PacketsOfThisTimeInstant > 0) {
                if (this.incomingMPLSGOS3Packets.getItemCount() == 0) {
                    this.incomingMPLSGOS3Packets.add(instante - 1, 0);
                    this.incomingMPLSGOS3Packets.add(instante, this.incomingMPLSGOS3PacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingMPLSGOS3Packets);
                } else {
                    this.incomingMPLSGOS3Packets.add(instante, this.incomingMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            if (this.incomingTLDPPacketsOfThisTimeInstant > 0) {
                if (this.incomingTLDPPackets.getItemCount() == 0) {
                    this.incomingTLDPPackets.add(instante - 1, 0);
                    this.incomingTLDPPackets.add(instante, this.incomingTLDPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingTLDPPackets);
                } else {
                    this.incomingTLDPPackets.add(instante, this.incomingTLDPPacketsOfThisTimeInstant);
                }
            }

            if (this.incomingGPSRPPacketsOfThisTimeInstant > 0) {
                if (this.incomingGPSRPPackets.getItemCount() == 0) {
                    this.incomingGPSRPPackets.add(instante - 1, 0);
                    this.incomingGPSRPPackets.add(instante, this.incomingGPSRPPacketsOfThisTimeInstant);
                    this.incomingPackets.addSeries(this.incomingGPSRPPackets);
                } else {
                    this.incomingGPSRPPackets.add(instante, this.incomingGPSRPPacketsOfThisTimeInstant);
                }
            }

            if (this.outgoingMPLSPacketsOfThisTimeInstant > 0) {
                if (this.outgoingMPLSPackets.getItemCount() == 0) {
                    this.outgoingMPLSPackets.add(instante - 1, 0);
                    this.outgoingMPLSPackets.add(instante, this.outgoingMPLSPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSPackets);
                } else {
                    this.outgoingMPLSPackets.add(instante, this.outgoingMPLSPacketsOfThisTimeInstant);
                }
            }

            if (this.outgoingMPLSGOS1PacketsOfThisTimeInstant > 0) {
                if (this.outgoingMPLSGOS1Packets.getItemCount() == 0) {
                    this.outgoingMPLSGOS1Packets.add(instante - 1, 0);
                    this.outgoingMPLSGOS1Packets.add(instante, this.outgoingMPLSGOS1PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSGOS1Packets);
                } else {
                    this.outgoingMPLSGOS1Packets.add(instante, this.outgoingMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            if (this.outgoingMPLSGOS2PacketsOfThisTimeInstant > 0) {
                if (this.outgoingMPLSGOS2Packets.getItemCount() == 0) {
                    this.outgoingMPLSGOS2Packets.add(instante - 1, 0);
                    this.outgoingMPLSGOS2Packets.add(instante, this.outgoingMPLSGOS2PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSGOS2Packets);
                } else {
                    this.outgoingMPLSGOS2Packets.add(instante, this.outgoingMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            if (this.outgoingMPLSGOS3PacketsOfThisTimeInstant > 0) {
                if (this.outgoingMPLSGOS3Packets.getItemCount() == 0) {
                    this.outgoingMPLSGOS3Packets.add(instante - 1, 0);
                    this.outgoingMPLSGOS3Packets.add(instante, this.outgoingMPLSGOS3PacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingMPLSGOS3Packets);
                } else {
                    this.outgoingMPLSGOS3Packets.add(instante, this.outgoingMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            if (this.outgoingTLDPPacketsOfThisTimeInstant > 0) {
                if (this.outgoingTLDPPackets.getItemCount() == 0) {
                    this.outgoingTLDPPackets.add(instante - 1, 0);
                    this.outgoingTLDPPackets.add(instante, this.outgoingTLDPPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingTLDPPackets);
                } else {
                    this.outgoingTLDPPackets.add(instante, this.outgoingTLDPPacketsOfThisTimeInstant);
                }
            }

            if (this.outgoingGPSRPPacketsOfThisTimeInstant > 0) {
                if (this.outgoingGPSRPPackets.getItemCount() == 0) {
                    this.outgoingGPSRPPackets.add(instante - 1, 0);
                    this.outgoingGPSRPPackets.add(instante, this.outgoingGPSRPPacketsOfThisTimeInstant);
                    this.outgoingPackets.addSeries(this.outgoingGPSRPPackets);
                } else {
                    this.outgoingGPSRPPackets.add(instante, this.outgoingGPSRPPacketsOfThisTimeInstant);
                }
            }

            if (this.discardedMPLSPacketsOfThisTimeInstant > 0) {
                if (this.discardedMPLSPackets.getItemCount() == 0) {
                    this.discardedMPLSPackets.add(instante - 1, 0);
                    this.discardedMPLSPackets.add(instante, this.discardedMPLSPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSPackets);
                } else {
                    this.discardedMPLSPackets.add(instante, this.discardedMPLSPacketsOfThisTimeInstant);
                }
            }

            if (this.discardedMPLSGOS1PacketsOfThisTimeInstant > 0) {
                if (this.discardedMPLSGOS1Packets.getItemCount() == 0) {
                    this.discardedMPLSGOS1Packets.add(instante - 1, 0);
                    this.discardedMPLSGOS1Packets.add(instante, this.discardedMPLSGOS1PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSGOS1Packets);
                } else {
                    this.discardedMPLSGOS1Packets.add(instante, this.discardedMPLSGOS1PacketsOfThisTimeInstant);
                }
            }

            if (this.discardedMPLSGOS2PacketsOfThisTimeInstant > 0) {
                if (this.discardedMPLSGOS2Packets.getItemCount() == 0) {
                    this.discardedMPLSGOS2Packets.add(instante - 1, 0);
                    this.discardedMPLSGOS2Packets.add(instante, this.discardedMPLSGOS2PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSGOS2Packets);
                } else {
                    this.discardedMPLSGOS2Packets.add(instante, this.discardedMPLSGOS2PacketsOfThisTimeInstant);
                }
            }

            if (this.discardedMPLSGOS3PacketsOfThisTimeInstant > 0) {
                if (discardedMPLSGOS3Packets.getItemCount() == 0) {
                    this.discardedMPLSGOS3Packets.add(instante - 1, 0);
                    this.discardedMPLSGOS3Packets.add(instante, this.discardedMPLSGOS3PacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedMPLSGOS3Packets);
                } else {
                    this.discardedMPLSGOS3Packets.add(instante, this.discardedMPLSGOS3PacketsOfThisTimeInstant);
                }
            }

            if (this.discardedTLDPPacketsOfThisTimeInstant > 0) {
                if (this.discardedTLDPPackets.getItemCount() == 0) {
                    this.discardedTLDPPackets.add(instante - 1, 0);
                    this.discardedTLDPPackets.add(instante, this.discardedTLDPPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedTLDPPackets);
                } else {
                    this.discardedTLDPPackets.add(instante, this.discardedTLDPPacketsOfThisTimeInstant);
                }
            }

            if (this.discardedGPSRPPacketsOfThisTimeInstant > 0) {
                if (this.discardedGPSRPPackets.getItemCount() == 0) {
                    this.discardedGPSRPPackets.add(instante - 1, 0);
                    this.discardedGPSRPPackets.add(instante, this.discardedGPSRPPacketsOfThisTimeInstant);
                    this.discardedPackets.addSeries(this.discardedGPSRPPackets);
                } else {
                    this.discardedGPSRPPackets.add(instante, this.discardedGPSRPPacketsOfThisTimeInstant);
                }
            }
        }
    }

    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 1.
     *
     * @return El t�tulo de la gr�fica 1.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset1() {
        return TStats.INCOMING_PACKETS;
    }

    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 2.
     *
     * @return El t�tulo de la gr�fica 2.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset2() {
        return TStats.OUTGOING_PACKETS;
    }

    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 3.
     *
     * @return El t�tulo de la gr�fica 3.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset3() {
        return TStats.DISCARDED_PACKETS;
    }

    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 4.
     *
     * @return El t�tulo de la gr�fica 4.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset4() {
        return null;
    }

    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 5.
     *
     * @return El t�tulo de la gr�fica 5.
     * @since 2.0
     */
    @Override
    public String getTitleOfDataset5() {
        return null;
    }

    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 6.
     *
     * @return El t�tulo de la gr�fica 6.
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
}
