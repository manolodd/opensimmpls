/*
 * TEstadisticasEmisor.java
 *
 * Created on 10 de noviembre de 2004, 16:46
 */

package simMPLS.scenario;

import simMPLS.protocols.TPDU;
import org.jfree.chart.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;

/**
 * Esta clase implementa las estad�sticas para un nodo LSR.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TLSRStats extends TStats {
    
    /**
     * Crea una nueva instancia de TEstadisticasLSR
     * @since 1.0
     */
    public TLSRStats() {
    	paquetesEntrantes = new XYSeriesCollection();
    	paquetesSalientes = new XYSeriesCollection();
    	paquetesDescartados = new XYSeriesCollection();
    	salientesMPLS = new XYSeries(TStats.MPLS);
    	salientesMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
    	salientesMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
    	salientesMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	salientesTLDP = new XYSeries(TStats.TLDP);
    	salientesGPSRP = new XYSeries(TStats.GPSRP);
    	entrantesMPLS = new XYSeries(TStats.MPLS);
    	entrantesMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
    	entrantesMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
    	entrantesMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	entrantesTLDP = new XYSeries(TStats.TLDP);
    	entrantesGPSRP = new XYSeries(TStats.GPSRP);
	descartadosMPLS = new XYSeries(TStats.MPLS);
	descartadosMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
	descartadosMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
	descartadosMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	descartadosTLDP = new XYSeries(TStats.TLDP);
    	descartadosGPSRP = new XYSeries(TStats.GPSRP);
        tEMPLS = 0;
        tEMPLS_GOS1 = 0;
        tEMPLS_GOS2 = 0;
        tEMPLS_GOS3 = 0;
        tETLDP = 0;
        tEGPSRP = 0;
        tSMPLS = 0;
        tSMPLS_GOS1 = 0;
        tSMPLS_GOS2 = 0;
        tSMPLS_GOS3 = 0;
        tSTLDP = 0;
        tSGPSRP = 0;
        tDMPLS = 0;
        tDMPLS_GOS1 = 0;
        tDMPLS_GOS2 = 0;
        tDMPLS_GOS3 = 0;
        tDTLDP = 0;
        tDGPSRP = 0;
    }
    
    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 1.
     * @return Datos necesarios para la gr�fica 1.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica1() {
        return this.paquetesEntrantes;
    }
    
    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 2.
     * @return Datos necesarios para la gr�fica 2.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica2() {
        return this.paquetesSalientes;
    }
    
    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 3.
     * @return Datos necesarios para la gr�fica 3.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica3() {
        return this.paquetesDescartados;
    }
    
    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 4.
     * @return Datos necesarios para la gr�fica 4.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica4() {
        return null;
    }
    
    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 5.
     * @return Datos necesarios para la gr�fica 5.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica5() {
        return null;
    }
    
    /**
     * Este m�todo permite obtener los datos necesrio para generar la gr�fica 6.
     * @return Datos necesarios para la gr�fica 6.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica6() {
        return null;
    }

    /**
     * Este m�todo permite aumentar las estad�sticas, a�adiendo las del paquete
     * especificado.
     * @param paquete Paquete que se desea contabilizar.
     * @param entrada ENTRADA, SALIDA o DESCARTE, dependiendo de si el paquete entra en el nodo, sale
     * de �l o es descartado.
     * @since 1.0
     */    
    public void addStatsEntry(TPDU paquete, int entrada) {
        if (this.estadisticasActivas) {
            int tipoPaquete = paquete.getSubtype();
            int GoS = 0;
            if (tipoPaquete == TPDU.TLDP) {
                if (entrada == TStats.SALIDA) {
                    this.tSTLDP++;
                } else if (entrada == TStats.DESCARTE) {
                    this.tDTLDP++;
                } else if (entrada == TStats.ENTRADA) {
                    this.tETLDP++;
                }
            } else if (tipoPaquete == TPDU.GPSRP) {
                if (entrada == TStats.SALIDA) {
                    this.tSGPSRP++;
                } else if (entrada == TStats.DESCARTE) {
                    this.tDGPSRP++;
                } else if (entrada == TStats.ENTRADA) {
                    this.tEGPSRP++;
                }
            } else if (tipoPaquete == TPDU.MPLS) {
                if (entrada == TStats.SALIDA) {
                    this.tSMPLS++;
                } else if (entrada == TStats.DESCARTE) {
                    this.tDMPLS++;
                } else if (entrada == TStats.ENTRADA) {
                    this.tEMPLS++;
                }
            } else if (tipoPaquete == TPDU.MPLS_GOS) {
                GoS = paquete.getHeader().getOptionsField().getEncodedGoSLevel();
                if (entrada == TStats.SALIDA) {
                    if ((GoS == TPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.tSMPLS++;
                    } else if ((GoS == TPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.tSMPLS_GOS1++;
                    } else if ((GoS == TPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.tSMPLS_GOS2++;
                    } else if ((GoS == TPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.tSMPLS_GOS3++;
                    }
                } else if (entrada == TStats.DESCARTE) {
                    if ((GoS == TPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.tDMPLS++;
                    } else if ((GoS == TPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.tDMPLS_GOS1++;
                    } else if ((GoS == TPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.tDMPLS_GOS2++;
                    } else if ((GoS == TPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.tDMPLS_GOS3++;
                    }
                } else if (entrada == TStats.ENTRADA) {
                    if ((GoS == TPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL0_WITH_BACKUP_LSP)) {
                        this.tEMPLS++;
                    } else if ((GoS == TPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL1_WITH_BACKUP_LSP)) {
                        this.tEMPLS_GOS1++;
                    } else if ((GoS == TPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL2_WITH_BACKUP_LSP)) {
                        this.tEMPLS_GOS2++;
                    } else if ((GoS == TPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) || (GoS == TPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                        this.tEMPLS_GOS3++;
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo devuelve el n�mero de gr�ficas que genera el nodo LSR.
     * @return El n�mero de graficas del nodo LSR.
     * @since 1.0
     */    
    public int obtenerNumeroGraficas() {
        return 3;
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase, dejando la instancia como si
     * acabase de ser creada por el constructor.
     * @since 1.0
     */    
    public void reset() {
    	paquetesEntrantes = new XYSeriesCollection();
    	paquetesSalientes = new XYSeriesCollection();
    	paquetesDescartados = new XYSeriesCollection();
    	salientesMPLS = new XYSeries(TStats.MPLS);
    	salientesMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
    	salientesMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
    	salientesMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	salientesTLDP = new XYSeries(TStats.TLDP);
    	salientesGPSRP = new XYSeries(TStats.GPSRP);
    	entrantesMPLS = new XYSeries(TStats.MPLS);
    	entrantesMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
    	entrantesMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
    	entrantesMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	entrantesTLDP = new XYSeries(TStats.TLDP);
    	entrantesGPSRP = new XYSeries(TStats.GPSRP);
	descartadosMPLS = new XYSeries(TStats.MPLS);
	descartadosMPLS_GOS1 = new XYSeries(TStats.MPLS_GOS1);
	descartadosMPLS_GOS2 = new XYSeries(TStats.MPLS_GOS2);
	descartadosMPLS_GOS3 = new XYSeries(TStats.MPLS_GOS3);
    	descartadosTLDP = new XYSeries(TStats.TLDP);
    	descartadosGPSRP = new XYSeries(TStats.GPSRP);
        tEMPLS = 0;
        tEMPLS_GOS1 = 0;
        tEMPLS_GOS2 = 0;
        tEMPLS_GOS3 = 0;
        tETLDP = 0;
        tEGPSRP = 0;
        tSMPLS = 0;
        tSMPLS_GOS1 = 0;
        tSMPLS_GOS2 = 0;
        tSMPLS_GOS3 = 0;
        tSTLDP = 0;
        tSGPSRP = 0;
        tDMPLS = 0;
        tDMPLS_GOS1 = 0;
        tDMPLS_GOS2 = 0;
        tDMPLS_GOS3 = 0;
        tDTLDP = 0;
        tDGPSRP = 0;
    }
    
    /**
     * Este m�todo actualiza las estadisticas con los �ltimos adtos recopilados desde
     * la �ltima vez que se llam� a este m�todo.
     * @param instante Instante de tiempo al que se atribuiran los �ltimos datos recolectados.
     * @since 1.0
     */    
    public void asentarDatos(long instante) {
        if (this.estadisticasActivas) {
            if (tEMPLS > 0) {
                if (entrantesMPLS.getItemCount() == 0) {
                    this.entrantesMPLS.add(instante-1, 0);
                    this.entrantesMPLS.add(instante, tEMPLS);
                    this.paquetesEntrantes.addSeries(entrantesMPLS);
                } else {
                    this.entrantesMPLS.add(instante, tEMPLS);
                }
            }
            
            if (tEMPLS_GOS1 > 0) {
                if (entrantesMPLS_GOS1.getItemCount() == 0) {
                    this.entrantesMPLS_GOS1.add(instante-1, 0);
                    this.entrantesMPLS_GOS1.add(instante, tEMPLS_GOS1);
                    this.paquetesEntrantes.addSeries(entrantesMPLS_GOS1);
                } else {
                    this.entrantesMPLS_GOS1.add(instante, tEMPLS_GOS1);
                }
            }
            
            if (tEMPLS_GOS2 > 0) {
                if (entrantesMPLS_GOS2.getItemCount() == 0) {
                    this.entrantesMPLS_GOS2.add(instante-1, 0);
                    this.entrantesMPLS_GOS2.add(instante, tEMPLS_GOS2);
                    this.paquetesEntrantes.addSeries(entrantesMPLS_GOS2);
                } else {
                    this.entrantesMPLS_GOS2.add(instante, tEMPLS_GOS2);
                }
            }
            
            if (tEMPLS_GOS3 > 0) {
                if (entrantesMPLS_GOS3.getItemCount() == 0) {
                    this.entrantesMPLS_GOS3.add(instante-1, 0);
                    this.entrantesMPLS_GOS3.add(instante, tEMPLS_GOS3);
                    this.paquetesEntrantes.addSeries(entrantesMPLS_GOS3);
                } else {
                    this.entrantesMPLS_GOS3.add(instante, tEMPLS_GOS3);
                }
            }
            
            if (tETLDP > 0) {
                if (entrantesTLDP.getItemCount() == 0) {
                    this.entrantesTLDP.add(instante-1, 0);
                    this.entrantesTLDP.add(instante, tETLDP);
                    this.paquetesEntrantes.addSeries(entrantesTLDP);
                } else {
                    this.entrantesTLDP.add(instante, tETLDP);
                }
            }
            
            if (tEGPSRP > 0) {
                if (entrantesGPSRP.getItemCount() == 0) {
                    this.entrantesGPSRP.add(instante-1, 0);
                    this.entrantesGPSRP.add(instante, tEGPSRP);
                    this.paquetesEntrantes.addSeries(entrantesGPSRP);
                } else {
                    this.entrantesGPSRP.add(instante, tEGPSRP);
                }
            }
                    
            if (tSMPLS > 0) {
                if (salientesMPLS.getItemCount() == 0) {
                    this.salientesMPLS.add(instante-1, 0);
                    this.salientesMPLS.add(instante, tSMPLS);
                    this.paquetesSalientes.addSeries(salientesMPLS);
                } else {
                    this.salientesMPLS.add(instante, tSMPLS);
                }
            }
                    
            if (tSMPLS_GOS1 > 0) {
                if (salientesMPLS_GOS1.getItemCount() == 0) {
                    this.salientesMPLS_GOS1.add(instante-1, 0);
                    this.salientesMPLS_GOS1.add(instante, tSMPLS_GOS1);
                    this.paquetesSalientes.addSeries(salientesMPLS_GOS1);
                } else {
                    this.salientesMPLS_GOS1.add(instante, tSMPLS_GOS1);
                }
            }
                    
            if (tSMPLS_GOS2 > 0) {
                if (salientesMPLS_GOS2.getItemCount() == 0) {
                    this.salientesMPLS_GOS2.add(instante-1, 0);
                    this.salientesMPLS_GOS2.add(instante, tSMPLS_GOS2);
                    this.paquetesSalientes.addSeries(salientesMPLS_GOS2);
                } else {
                    this.salientesMPLS_GOS2.add(instante, tSMPLS_GOS2);
                }
            }
                    
            if (tSMPLS_GOS3 > 0) {
                if (salientesMPLS_GOS3.getItemCount() == 0) {
                    this.salientesMPLS_GOS3.add(instante-1, 0);
                    this.salientesMPLS_GOS3.add(instante, tSMPLS_GOS3);
                    this.paquetesSalientes.addSeries(salientesMPLS_GOS3);
                } else {
                    this.salientesMPLS_GOS3.add(instante, tSMPLS_GOS3);
                }
            }
                    
            if (tSTLDP > 0) {
                if (salientesTLDP.getItemCount() == 0) {
                    this.salientesTLDP.add(instante-1, 0);
                    this.salientesTLDP.add(instante, tSTLDP);
                    this.paquetesSalientes.addSeries(salientesTLDP);
                } else {
                    this.salientesTLDP.add(instante, tSTLDP);
                }
            }
                    
            if (tSGPSRP > 0) {
                if (salientesGPSRP.getItemCount() == 0) {
                    this.salientesGPSRP.add(instante-1, 0);
                    this.salientesGPSRP.add(instante, tSGPSRP);
                    this.paquetesSalientes.addSeries(salientesGPSRP);
                } else {
                    this.salientesGPSRP.add(instante, tSGPSRP);
                }
            }
                    
            if (tDMPLS > 0) {
                if (descartadosMPLS.getItemCount() == 0) {
                    this.descartadosMPLS.add(instante-1, 0);
                    this.descartadosMPLS.add(instante, tDMPLS);
                    this.paquetesDescartados.addSeries(descartadosMPLS);
                } else {
                    this.descartadosMPLS.add(instante, tDMPLS);
                }
            }
                    
            if (tDMPLS_GOS1 > 0) {
                if (descartadosMPLS_GOS1.getItemCount() == 0) {
                    this.descartadosMPLS_GOS1.add(instante-1, 0);
                    this.descartadosMPLS_GOS1.add(instante, tDMPLS_GOS1);
                    this.paquetesDescartados.addSeries(descartadosMPLS_GOS1);
                } else {
                    this.descartadosMPLS_GOS1.add(instante, tDMPLS_GOS1);
                }
            }
                    
            if (tDMPLS_GOS2 > 0) {
                if (descartadosMPLS_GOS2.getItemCount() == 0) {
                    this.descartadosMPLS_GOS2.add(instante-1, 0);
                    this.descartadosMPLS_GOS2.add(instante, tDMPLS_GOS2);
                    this.paquetesDescartados.addSeries(descartadosMPLS_GOS2);
                } else {
                    this.descartadosMPLS_GOS2.add(instante, tDMPLS_GOS2);
                }
            }
                    
            if (tDMPLS_GOS3 > 0) {
                if (descartadosMPLS_GOS3.getItemCount() == 0) {
                    this.descartadosMPLS_GOS3.add(instante-1, 0);
                    this.descartadosMPLS_GOS3.add(instante, tDMPLS_GOS3);
                    this.paquetesDescartados.addSeries(descartadosMPLS_GOS3);
                } else {
                    this.descartadosMPLS_GOS3.add(instante, tDMPLS_GOS3);
                }
            }
                    
            if (tDTLDP > 0) {
                if (descartadosTLDP.getItemCount() == 0) {
                    this.descartadosTLDP.add(instante-1, 0);
                    this.descartadosTLDP.add(instante, tDTLDP);
                    this.paquetesDescartados.addSeries(descartadosTLDP);
                } else {
                    this.descartadosTLDP.add(instante, tDTLDP);
                }
            }
                    
            if (tDGPSRP > 0) {
                if (descartadosGPSRP.getItemCount() == 0) {
                    this.descartadosGPSRP.add(instante-1, 0);
                    this.descartadosGPSRP.add(instante, tDGPSRP);
                    this.paquetesDescartados.addSeries(descartadosGPSRP);
                } else {
                    this.descartadosGPSRP.add(instante, tDGPSRP);
                }
            }
        }
    }    
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 1.
     * @return El t�tulo de la gr�fica 1.
     * @since 1.0
     */    
    public String obtenerTitulo1() {
        return TStats.PAQUETES_ENTRANTES;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 2.
     * @return El t�tulo de la gr�fica 2.
     * @since 1.0
     */    
    public String obtenerTitulo2() {
        return TStats.PAQUETES_SALIENTES;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 3.
     * @return El t�tulo de la gr�fica 3.
     * @since 1.0
     */    
    public String obtenerTitulo3() {
        return TStats.PAQUETES_DESCARTADOS;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 4.
     * @return El t�tulo de la gr�fica 4.
     * @since 1.0
     */    
    public String obtenerTitulo4() {
        return null;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 5.
     * @return El t�tulo de la gr�fica 5.
     * @since 1.0
     */    
    public String obtenerTitulo5() {
        return null;
    }
    
    /**
     * Este m�todo permite obtener el t�tulo de la gr�fica 6.
     * @return El t�tulo de la gr�fica 6.
     * @since 1.0
     */    
    public String obtenerTitulo6() {
        return null;
    }
    
    private int tEMPLS;
    private int tEMPLS_GOS1;
    private int tEMPLS_GOS2;
    private int tEMPLS_GOS3;
    private int tETLDP;
    private int tEGPSRP;
    private int tSMPLS;
    private int tSMPLS_GOS1;
    private int tSMPLS_GOS2;
    private int tSMPLS_GOS3;
    private int tSTLDP;
    private int tSGPSRP;
    private int tDMPLS;
    private int tDMPLS_GOS1;
    private int tDMPLS_GOS2;
    private int tDMPLS_GOS3;
    private int tDTLDP;
    private int tDGPSRP;
    private XYSeriesCollection paquetesEntrantes;
    private XYSeriesCollection paquetesSalientes;
    private XYSeriesCollection paquetesDescartados;
    private XYSeries entrantesMPLS;
    private XYSeries entrantesMPLS_GOS1;
    private XYSeries entrantesMPLS_GOS2;
    private XYSeries entrantesMPLS_GOS3;
    private XYSeries entrantesTLDP;
    private XYSeries entrantesGPSRP;
    private XYSeries salientesMPLS;
    private XYSeries salientesMPLS_GOS1;
    private XYSeries salientesMPLS_GOS2;
    private XYSeries salientesMPLS_GOS3;
    private XYSeries salientesTLDP;
    private XYSeries salientesGPSRP;
    private XYSeries descartadosMPLS;
    private XYSeries descartadosMPLS_GOS1;
    private XYSeries descartadosMPLS_GOS2;
    private XYSeries descartadosMPLS_GOS3;
    private XYSeries descartadosTLDP;
    private XYSeries descartadosGPSRP;
}
