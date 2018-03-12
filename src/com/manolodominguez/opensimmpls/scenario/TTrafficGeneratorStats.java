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
     * Creates a new instance of TEstadisticasEmisor
     * @since 2.0
     */
    public TTrafficGeneratorStats() {
    	this.paquetesSalientes = new XYSeriesCollection();
    	this.paquetesDescartados = new XYSeriesCollection();
    	this.salientesIPv4 = new XYSeries(TStats.IPV4);
    	this.salientesIPv4_GOS1 = new XYSeries(TStats.IPV4_GOS1);
    	this.salientesIPv4_GOS2 = new XYSeries(TStats.IPV4_GOS2);
    	this.salientesIPv4_GOS3 = new XYSeries(TStats.IPV4_GOS3);
    	this.salientesMPLS = new XYSeries(TStats.MPLS);
    	this.salientesMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
    	this.salientesMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
    	this.salientesMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	this.descartadosIPv4 = new XYSeries(TStats.IPV4);
    	this.descartadosIPv4_GOS1 = new XYSeries(TStats.IPV4_GOS1);
    	this.descartadosIPv4_GOS2 = new XYSeries(TStats.IPV4_GOS2);
    	this.descartadosIPv4_GOS3 = new XYSeries(TStats.IPV4_GOS3);
	this.descartadosMPLS = new XYSeries(TStats.MPLS);
	this.descartadosMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
	this.descartadosMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
	this.descartadosMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
        this.tSIPV4 = 0;
        this.tSIPV4_GOS1 = 0;
        this.tSIPV4_GOS2 = 0;
        this.tSIPV4_GOS3 = 0;
        this.tSMPLS = 0;
        this.tSMPLS_GOS1 = 0;
        this.tSMPLS_GOS2 = 0;
        this.tSMPLS_GOS3 = 0;
        this.tDIPV4 = 0;
        this.tDIPV4_GOS1 = 0;
        this.tDIPV4_GOS2 = 0;
        this.tDIPV4_GOS3 = 0;
        this.tDMPLS = 0;
        this.tDMPLS_GOS1 = 0;
        this.tDMPLS_GOS2 = 0;
        this.tDMPLS_GOS3 = 0;
    }
    
    /**
     * Este m�todo obtiene los datos que permitiran generar la gr�fica 1.
     * @return Datos dela gr�fica 1.
     * @since 2.0
     */
    @Override
    public AbstractDataset getDataset1() {
        return this.paquetesSalientes;
    }
    
    /**
     * Este m�todo obtiene los datos que permitiran generar la gr�fica 2.
     * @return Datos dela gr�fica 2.
     * @since 2.0
     */@Override
    public AbstractDataset getDataset2() {
        return this.paquetesDescartados;
    }
    
    /**
     * Este m�todo obtiene los datos que permitiran generar la gr�fica 3.
     * @return Datos dela gr�fica 3.
     * @since 2.0
     */@Override
    public AbstractDataset getDataset3() {
        return null;
    }
    
    /**
     * Este m�todo obtiene los datos que permitiran generar la gr�fica 4.
     * @return Datos dela gr�fica 4.
     * @since 2.0
     */@Override
    public AbstractDataset getDataset4() {
        return null;
    }
    
    /**
     * Este m�todo obtiene los datos que permitiran generar la gr�fica 5.
     * @return Datos dela gr�fica 5.
     * @since 2.0
     */@Override
    public AbstractDataset getDataset5() {
        return null;
    }
    
    /**
     * Este m�todo obtiene los datos que permitiran generar la gr�fica 6.
     * @return Datos dela gr�fica 6.
     * @since 2.0
     */@Override
    public AbstractDataset getDataset6() {
        return null;
    }

    /**
     * Este m�todo amplia las estad�sticas, a�adiendo las del nuevo paquete
     * especificado.
     * @param paquete Paquete que se desea contabilizar en las estad�sticas.
     * @param entrada INCOMING, OUTGOING o DISCARD, dependiendo de si el paquete entra en el nodo, sale
 de �l o es descartado.
     * @since 2.0
     */    
    @Override
    public void addStatEntry(TAbstractPDU paquete, int entrada) {
        if (this.statsEnabled) {
            int tipoPaquete = paquete.getSubtype();
            int GoS = 0;
            if (tipoPaquete == TAbstractPDU.MPLS) {
                if (entrada == TStats.OUTGOING) {
                    this.tSMPLS++;
                } else if (entrada == TStats.DISCARD) {
                    this.tDMPLS++;
                }
            } else if (tipoPaquete == TAbstractPDU.MPLS_GOS) {
                GoS = paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                if (entrada == TStats.OUTGOING) {
                    if ((GoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.tSMPLS++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.tSMPLS_GOS1++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.tSMPLS_GOS2++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.tSMPLS_GOS3++;
                    }
                } else if (entrada == TStats.DISCARD) {
                    if ((GoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.tDMPLS++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.tDMPLS_GOS1++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.tDMPLS_GOS2++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.tDMPLS_GOS3++;
                    }
                }
            } else if (tipoPaquete == TAbstractPDU.IPV4) {
                if (entrada == TStats.OUTGOING) {
                    this.tSIPV4++;
                } else if (entrada == TStats.DISCARD) {
                    this.tDIPV4++;
                }
            } else if (tipoPaquete == TAbstractPDU.IPV4_GOS) {
                GoS = paquete.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                if (entrada == TStats.OUTGOING) {
                    if ((GoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.tSIPV4++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.tSIPV4_GOS1++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.tSIPV4_GOS2++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.tSIPV4_GOS3++;
                    }
                } else if (entrada == TStats.DISCARD) {
                    if ((GoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.tDIPV4++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.tDIPV4_GOS1++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.tDIPV4_GOS2++;
                    } else if ((GoS == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.tDIPV4_GOS3++;
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo devuelve el n�mero de gr�fica que tiene el emisor.
     * @return N�mero de gr�ficas del emisor.
     * @since 2.0
     */    
    @Override
    public int numberOfAvailableDatasets() {
        return 2;
    }
    
    /**
     * Este m�todo restaura las estad�sticas del emisor a su valor original com osi
     * acabasen de se creadas por el cosntructor.
     * @since 2.0
     */    
    @Override
    public void reset() {
    	this.paquetesSalientes = new XYSeriesCollection();
    	this.paquetesDescartados = new XYSeriesCollection();
    	this.salientesIPv4 = new XYSeries(TStats.IPV4);
    	this.salientesIPv4_GOS1 = new XYSeries(TStats.IPV4_GOS1);
    	this.salientesIPv4_GOS2 = new XYSeries(TStats.IPV4_GOS2);
    	this.salientesIPv4_GOS3 = new XYSeries(TStats.IPV4_GOS3);
    	this.salientesMPLS = new XYSeries(TStats.MPLS);
    	this.salientesMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
    	this.salientesMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
    	this.salientesMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	this.descartadosIPv4 = new XYSeries(TStats.IPV4);
    	this.descartadosIPv4_GOS1 = new XYSeries(TStats.IPV4_GOS1);
    	this.descartadosIPv4_GOS2 = new XYSeries(TStats.IPV4_GOS2);
    	this.descartadosIPv4_GOS3 = new XYSeries(TStats.IPV4_GOS3);
	this.descartadosMPLS = new XYSeries(TStats.MPLS);
	this.descartadosMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
	this.descartadosMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
	this.descartadosMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
        this.tSIPV4 = 0;
        this.tSIPV4_GOS1 = 0;
        this.tSIPV4_GOS2 = 0;
        this.tSIPV4_GOS3 = 0;
        this.tSMPLS = 0;
        this.tSMPLS_GOS1 = 0;
        this.tSMPLS_GOS2 = 0;
        this.tSMPLS_GOS3 = 0;
        this.tDIPV4 = 0;
        this.tDIPV4_GOS1 = 0;
        this.tDIPV4_GOS2 = 0;
        this.tDIPV4_GOS3 = 0;
        this.tDMPLS = 0;
        this.tDMPLS_GOS1 = 0;
        this.tDMPLS_GOS2 = 0;
        this.tDMPLS_GOS3 = 0;
    }
    
    /**
     * Este m�todo actualiza las estadisticas con los valores nuevos desde la ultima
     * vez que se llam� a este m�todo.
     * @param instante Tic de reloj al que se a�adir�n los �ltimos datos.
     * @since 2.0
     */    
    @Override
    public void groupStatsByTimeInstant(long instante) {
        if (this.statsEnabled) {
            if (this.tSIPV4 > 0) {
		if (salientesIPv4.getItemCount() == 0) {
                    this.salientesIPv4.add(instante-1, 0);
                    this.salientesIPv4.add(instante, this.tSIPV4);
                    this.paquetesSalientes.addSeries(this.salientesIPv4);
                } else {
                    this.salientesIPv4.add(instante, this.tSIPV4);
                }
            } 
            
            if (this.tSIPV4_GOS1 > 0) {
                if (this.salientesIPv4_GOS1.getItemCount() == 0) {
                    this.salientesIPv4_GOS1.add(instante-1, 0);
                    this.salientesIPv4_GOS1.add(instante, this.tSIPV4_GOS1);
                    this.paquetesSalientes.addSeries(this.salientesIPv4_GOS1);
                } else {
                    this.salientesIPv4_GOS1.add(instante, this.tSIPV4_GOS1);
                }
            }
            
            if (this.tSIPV4_GOS2 > 0) {
                if (this.salientesIPv4_GOS2.getItemCount() == 0) {
                    this.salientesIPv4_GOS2.add(instante-1, 0);
                    this.salientesIPv4_GOS2.add(instante, this.tSIPV4_GOS2);
                    this.paquetesSalientes.addSeries(this.salientesIPv4_GOS2);
                } else {
                    this.salientesIPv4_GOS2.add(instante, this.tSIPV4_GOS2);
                }
            }
            
            if (this.tSIPV4_GOS3 > 0) {
                if (this.salientesIPv4_GOS3.getItemCount() == 0) {
                    this.salientesIPv4_GOS3.add(instante-1, 0);
                    this.salientesIPv4_GOS3.add(instante, this.tSIPV4_GOS3);
                    this.paquetesSalientes.addSeries(this.salientesIPv4_GOS3);
                } else {
                    this.salientesIPv4_GOS3.add(instante, this.tSIPV4_GOS3);
                }
            }
            
            if (this.tSMPLS > 0) {
                if (this.salientesMPLS.getItemCount() == 0) {
                    this.salientesMPLS.add(instante-1, 0);
                    this.salientesMPLS.add(instante, this.tSMPLS);
                    this.paquetesSalientes.addSeries(this.salientesMPLS);
                } else {
                    this.salientesMPLS.add(instante, this.tSMPLS);
                }
            }
            
            if (this.tSMPLS_GOS1 > 0) {
                if (this.salientesMPLS_GOS1.getItemCount() == 0) {
                    this.salientesMPLS_GOS1.add(instante-1, 0);
                    this.salientesMPLS_GOS1.add(instante, this.tSMPLS_GOS1);
                    this.paquetesSalientes.addSeries(this.salientesMPLS_GOS1);
                } else {
                    this.salientesMPLS_GOS1.add(instante, this.tSMPLS_GOS1);
                }
            }
            
            if (this.tSMPLS_GOS2 > 0) {
                if (this.salientesMPLS_GOS2.getItemCount() == 0) {
                    this.salientesMPLS_GOS2.add(instante-1, 0);
                    this.salientesMPLS_GOS2.add(instante, this.tSMPLS_GOS2);
                    this.paquetesSalientes.addSeries(this.salientesMPLS_GOS2);
                } else {
                    this.salientesMPLS_GOS2.add(instante, this.tSMPLS_GOS2);
                }
            }
            
            if (this.tSMPLS_GOS3 > 0) {
                if (this.salientesMPLS_GOS3.getItemCount() == 0) {
                    this.salientesMPLS_GOS3.add(instante-1, 0);
                    this.salientesMPLS_GOS3.add(instante, this.tSMPLS_GOS3);
                    this.paquetesSalientes.addSeries(this.salientesMPLS_GOS3);
                } else {
                    this.salientesMPLS_GOS3.add(instante, this.tSMPLS_GOS3);
                }
            }
            
            if (this.tDIPV4 > 0) {
                if (this.descartadosIPv4.getItemCount() == 0) {
                    this.descartadosIPv4.add(instante-1, 0);
                    this.descartadosIPv4.add(instante, this.tDIPV4);
                    this.paquetesDescartados.addSeries(this.descartadosIPv4);
                } else {
                    this.descartadosIPv4.add(instante, this.tDIPV4);
                }
            }
            
            if (this.tDIPV4_GOS1 > 0) {
                if (this.descartadosIPv4_GOS1.getItemCount() == 0) {
                    this.descartadosIPv4_GOS1.add(instante-1, 0);
                    this.descartadosIPv4_GOS1.add(instante, this.tDIPV4_GOS1);
                    this.paquetesDescartados.addSeries(this.descartadosIPv4_GOS1);
                } else {
                    this.descartadosIPv4_GOS1.add(instante, this.tDIPV4_GOS1);
                }
            }

            if (this.tDIPV4_GOS2 > 0) {
                if (this.descartadosIPv4_GOS2.getItemCount() == 0) {
                    this.descartadosIPv4_GOS2.add(instante-1, 0);
                    this.descartadosIPv4_GOS2.add(instante, this.tDIPV4_GOS2);
                    this.paquetesDescartados.addSeries(this.descartadosIPv4_GOS2);
                } else {
                    this.descartadosIPv4_GOS2.add(instante, this.tDIPV4_GOS2);
                }
            }

            if (this.tDIPV4_GOS3 > 0) {
                if (this.descartadosIPv4_GOS3.getItemCount() == 0) {
                    this.descartadosIPv4_GOS3.add(instante-1, 0);
                    this.descartadosIPv4_GOS3.add(instante, this.tDIPV4_GOS3);
                    this.paquetesDescartados.addSeries(this.descartadosIPv4_GOS3);
                } else {
                    this.descartadosIPv4_GOS3.add(instante, this.tDIPV4_GOS3);
                }
            }
            
            if (this.tDMPLS > 0) {
                if (this.descartadosMPLS.getItemCount() == 0) {
                    this.descartadosMPLS.add(instante-1, 0);
                    this.descartadosMPLS.add(instante, this.tDMPLS);
                    this.paquetesDescartados.addSeries(this.descartadosMPLS);
                } else {
                    this.descartadosMPLS.add(instante, this.tDMPLS);
                }
            }
            
            if (this.tDMPLS_GOS1 > 0) {
                if (this.descartadosMPLS_GOS1.getItemCount() == 0) {
                    this.descartadosMPLS_GOS1.add(instante-1, 0);
                    this.descartadosMPLS_GOS1.add(instante, this.tDMPLS_GOS1);
                    this.paquetesDescartados.addSeries(this.descartadosMPLS_GOS1);
                } else {
                    this.descartadosMPLS_GOS1.add(instante, this.tDMPLS_GOS1);
                }
            }
            
            if (this.tDMPLS_GOS2 > 0) {
                if (this.descartadosMPLS_GOS2.getItemCount() == 0) {
                    this.descartadosMPLS_GOS2.add(instante-1, 0);
                    this.descartadosMPLS_GOS2.add(instante, this.tDMPLS_GOS2);
                    this.paquetesDescartados.addSeries(this.descartadosMPLS_GOS2);
                } else {
                    this.descartadosMPLS_GOS2.add(instante, this.tDMPLS_GOS2);
                }
            }
            
            if (this.tDMPLS_GOS3 > 0) {
                if (this.descartadosMPLS_GOS3.getItemCount() == 0) {
                    this.descartadosMPLS_GOS3.add(instante-1, 0);
                    this.descartadosMPLS_GOS3.add(instante, this.tDMPLS_GOS3);
                    this.paquetesDescartados.addSeries(this.descartadosMPLS_GOS3);
                } else {
                    this.descartadosMPLS_GOS3.add(instante, this.tDMPLS_GOS3);
                }
            }
        }
    }    
    
    /**
     * Este m�todo obtiene el t�tulo de la gr�fica 1.
     * @return T�tulo de la gr�fica 1.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset1() {
        return TStats.OUTGOING_PACKETS;
    }
    
    /**
     * Este m�todo obtiene el t�tulo de la gr�fica 2.
     * @return T�tulo de la gr�fica 2.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset2() {
        return TStats.DISCARDED_PACKETS;
    }
    
    /**
     * Este m�todo obtiene el t�tulo de la gr�fica 3.
     * @return T�tulo de la gr�fica 3.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset3() {
        return null;
    }
    
    /**
     * Este m�todo obtiene el t�tulo de la gr�fica 4.
     * @return T�tulo de la gr�fica 4.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset4() {
        return null;
    }
    
    /**
     * Este m�todo obtiene el t�tulo de la gr�fica 5.
     * @return T�tulo de la gr�fica 5.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset5() {
        return null;
    }
    
    /**
     * Este m�todo obtiene el t�tulo de la gr�fica 6.
     * @return T�tulo de la gr�fica 6.
     * @since 2.0
     */    
    @Override
    public String getTitleOfDataset6() {
        return null;
    }
    
    private int tSIPV4;
    private int tSIPV4_GOS1;
    private int tSIPV4_GOS2;
    private int tSIPV4_GOS3;
    private int tSMPLS;
    private int tSMPLS_GOS1;
    private int tSMPLS_GOS2;
    private int tSMPLS_GOS3;
    private int tDIPV4;
    private int tDIPV4_GOS1;
    private int tDIPV4_GOS2;
    private int tDIPV4_GOS3;
    private int tDMPLS;
    private int tDMPLS_GOS1;
    private int tDMPLS_GOS2;
    private int tDMPLS_GOS3;
    private XYSeriesCollection paquetesSalientes;
    private XYSeriesCollection paquetesDescartados;
    private XYSeries salientesIPv4;
    private XYSeries salientesIPv4_GOS1;
    private XYSeries salientesIPv4_GOS2;
    private XYSeries salientesIPv4_GOS3;
    private XYSeries salientesMPLS;
    private XYSeries salientesMPLS_GOS1;
    private XYSeries salientesMPLS_GOS2;
    private XYSeries salientesMPLS_GOS3;
    private XYSeries descartadosIPv4;
    private XYSeries descartadosIPv4_GOS1;
    private XYSeries descartadosIPv4_GOS2;
    private XYSeries descartadosIPv4_GOS3;
    private XYSeries descartadosMPLS;
    private XYSeries descartadosMPLS_GOS1;
    private XYSeries descartadosMPLS_GOS2;
    private XYSeries descartadosMPLS_GOS3;
}
