/*
 * TEstadisticasEmisor.java
 *
 * Created on 10 de noviembre de 2004, 16:46
 */

package simMPLS.escenario;

import org.jfree.chart.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;
import simMPLS.protocolo.*;

/**
 * Esta clase implementa las estadísticas de un nodo emisor.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEstadisticasEmisor extends TEstadisticas {
    
    /**
     * Creates a new instance of TEstadisticasEmisor
     * @since 1.0
     */
    public TEstadisticasEmisor() {
    	paquetesSalientes = new XYSeriesCollection();
    	paquetesDescartados = new XYSeriesCollection();
    	salientesIPv4 = new XYSeries(TEstadisticas.IPV4);
    	salientesIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	salientesIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	salientesIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
    	salientesMPLS = new XYSeries(TEstadisticas.MPLS);
    	salientesMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
    	salientesMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
    	salientesMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	descartadosIPv4 = new XYSeries(TEstadisticas.IPV4);
    	descartadosIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	descartadosIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	descartadosIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
	descartadosMPLS = new XYSeries(TEstadisticas.MPLS);
	descartadosMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
	descartadosMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
	descartadosMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
        tSIPV4 = 0;
        tSIPV4_GOS1 = 0;
        tSIPV4_GOS2 = 0;
        tSIPV4_GOS3 = 0;
        tSMPLS = 0;
        tSMPLS_GOS1 = 0;
        tSMPLS_GOS2 = 0;
        tSMPLS_GOS3 = 0;
        tDIPV4 = 0;
        tDIPV4_GOS1 = 0;
        tDIPV4_GOS2 = 0;
        tDIPV4_GOS3 = 0;
        tDMPLS = 0;
        tDMPLS_GOS1 = 0;
        tDMPLS_GOS2 = 0;
        tDMPLS_GOS3 = 0;
    }
    
    /**
     * Este método obtiene los datos que permitiran generar la gráfica 1.
     * @return Datos dela gráfica 1.
     * @since 1.0
     */
    public org.jfree.data.AbstractDataset obtenerDatosGrafica1() {
        return this.paquetesSalientes;
    }
    
    /**
     * Este método obtiene los datos que permitiran generar la gráfica 2.
     * @return Datos dela gráfica 2.
     * @since 1.0
     */    public org.jfree.data.AbstractDataset obtenerDatosGrafica2() {
        return this.paquetesDescartados;
    }
    
    /**
     * Este método obtiene los datos que permitiran generar la gráfica 3.
     * @return Datos dela gráfica 3.
     * @since 1.0
     */    public org.jfree.data.AbstractDataset obtenerDatosGrafica3() {
        return null;
    }
    
    /**
     * Este método obtiene los datos que permitiran generar la gráfica 4.
     * @return Datos dela gráfica 4.
     * @since 1.0
     */    public org.jfree.data.AbstractDataset obtenerDatosGrafica4() {
        return null;
    }
    
    /**
     * Este método obtiene los datos que permitiran generar la gráfica 5.
     * @return Datos dela gráfica 5.
     * @since 1.0
     */    public org.jfree.data.AbstractDataset obtenerDatosGrafica5() {
        return null;
    }
    
    /**
     * Este método obtiene los datos que permitiran generar la gráfica 6.
     * @return Datos dela gráfica 6.
     * @since 1.0
     */    public org.jfree.data.AbstractDataset obtenerDatosGrafica6() {
        return null;
    }

    /**
     * Este método amplia las estadísticas, añadiendo las del nuevo paquete
     * especificado.
     * @param paquete Paquete que se desea contabilizar en las estadísticas.
     * @param entrada ENTRADA, SALIDA o DESCARTE, dependiendo de si el paquete entra en el nodo, sale
     * de él o es descartado.
     * @since 1.0
     */    
    public void crearEstadistica(TPDU paquete, int entrada) {
        if (this.estadisticasActivas) {
            int tipoPaquete = paquete.obtenerSubTipo();
            int GoS = 0;
            if (tipoPaquete == TPDU.MPLS) {
                if (entrada == TEstadisticas.SALIDA) {
                    this.tSMPLS++;
                } else if (entrada == TEstadisticas.DESCARTE) {
                    this.tDMPLS++;
                }
            } else if (tipoPaquete == TPDU.MPLS_GOS) {
                GoS = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerNivelGoS();
                if (entrada == TEstadisticas.SALIDA) {
                    if ((GoS == TPDU.EXP_NIVEL0_SINLSP) || (GoS == TPDU.EXP_NIVEL0_CONLSP)) {
                        this.tSMPLS++;
                    } else if ((GoS == TPDU.EXP_NIVEL1_SINLSP) || (GoS == TPDU.EXP_NIVEL1_CONLSP)) {
                        this.tSMPLS_GOS1++;
                    } else if ((GoS == TPDU.EXP_NIVEL2_SINLSP) || (GoS == TPDU.EXP_NIVEL2_CONLSP)) {
                        this.tSMPLS_GOS2++;
                    } else if ((GoS == TPDU.EXP_NIVEL3_SINLSP) || (GoS == TPDU.EXP_NIVEL3_CONLSP)) {
                        this.tSMPLS_GOS3++;
                    }
                } else if (entrada == TEstadisticas.DESCARTE) {
                    if ((GoS == TPDU.EXP_NIVEL0_SINLSP) || (GoS == TPDU.EXP_NIVEL0_CONLSP)) {
                        this.tDMPLS++;
                    } else if ((GoS == TPDU.EXP_NIVEL1_SINLSP) || (GoS == TPDU.EXP_NIVEL1_CONLSP)) {
                        this.tDMPLS_GOS1++;
                    } else if ((GoS == TPDU.EXP_NIVEL2_SINLSP) || (GoS == TPDU.EXP_NIVEL2_CONLSP)) {
                        this.tDMPLS_GOS2++;
                    } else if ((GoS == TPDU.EXP_NIVEL3_SINLSP) || (GoS == TPDU.EXP_NIVEL3_CONLSP)) {
                        this.tDMPLS_GOS3++;
                    }
                }
            } else if (tipoPaquete == TPDU.IPV4) {
                if (entrada == TEstadisticas.SALIDA) {
                    this.tSIPV4++;
                } else if (entrada == TEstadisticas.DESCARTE) {
                    this.tDIPV4++;
                }
            } else if (tipoPaquete == TPDU.IPV4_GOS) {
                GoS = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerNivelGoS();
                if (entrada == TEstadisticas.SALIDA) {
                    if ((GoS == TPDU.EXP_NIVEL0_SINLSP) || (GoS == TPDU.EXP_NIVEL0_CONLSP)) {
                        this.tSIPV4++;
                    } else if ((GoS == TPDU.EXP_NIVEL1_SINLSP) || (GoS == TPDU.EXP_NIVEL1_CONLSP)) {
                        this.tSIPV4_GOS1++;
                    } else if ((GoS == TPDU.EXP_NIVEL2_SINLSP) || (GoS == TPDU.EXP_NIVEL2_CONLSP)) {
                        this.tSIPV4_GOS2++;
                    } else if ((GoS == TPDU.EXP_NIVEL3_SINLSP) || (GoS == TPDU.EXP_NIVEL3_CONLSP)) {
                        this.tSIPV4_GOS3++;
                    }
                } else if (entrada == TEstadisticas.DESCARTE) {
                    if ((GoS == TPDU.EXP_NIVEL0_SINLSP) || (GoS == TPDU.EXP_NIVEL0_CONLSP)) {
                        this.tDIPV4++;
                    } else if ((GoS == TPDU.EXP_NIVEL1_SINLSP) || (GoS == TPDU.EXP_NIVEL1_CONLSP)) {
                        this.tDIPV4_GOS1++;
                    } else if ((GoS == TPDU.EXP_NIVEL2_SINLSP) || (GoS == TPDU.EXP_NIVEL2_CONLSP)) {
                        this.tDIPV4_GOS2++;
                    } else if ((GoS == TPDU.EXP_NIVEL3_SINLSP) || (GoS == TPDU.EXP_NIVEL3_CONLSP)) {
                        this.tDIPV4_GOS3++;
                    }
                }
            }
        }
    }
    
    /**
     * Este método devuelve el número de gráfica que tiene el emisor.
     * @return Número de gráficas del emisor.
     * @since 1.0
     */    
    public int obtenerNumeroGraficas() {
        return 2;
    }
    
    /**
     * Este método restaura las estadísticas del emisor a su valor original com osi
     * acabasen de se creadas por el cosntructor.
     * @since 1.0
     */    
    public void reset() {
    	paquetesSalientes = new XYSeriesCollection();
    	paquetesDescartados = new XYSeriesCollection();
    	salientesIPv4 = new XYSeries(TEstadisticas.IPV4);
    	salientesIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	salientesIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	salientesIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
    	salientesMPLS = new XYSeries(TEstadisticas.MPLS);
    	salientesMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
    	salientesMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
    	salientesMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	descartadosIPv4 = new XYSeries(TEstadisticas.IPV4);
    	descartadosIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	descartadosIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	descartadosIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
	descartadosMPLS = new XYSeries(TEstadisticas.MPLS);
	descartadosMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
	descartadosMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
	descartadosMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
        tSIPV4 = 0;
        tSIPV4_GOS1 = 0;
        tSIPV4_GOS2 = 0;
        tSIPV4_GOS3 = 0;
        tSMPLS = 0;
        tSMPLS_GOS1 = 0;
        tSMPLS_GOS2 = 0;
        tSMPLS_GOS3 = 0;
        tDIPV4 = 0;
        tDIPV4_GOS1 = 0;
        tDIPV4_GOS2 = 0;
        tDIPV4_GOS3 = 0;
        tDMPLS = 0;
        tDMPLS_GOS1 = 0;
        tDMPLS_GOS2 = 0;
        tDMPLS_GOS3 = 0;
    }
    
    /**
     * Este método actualiza las estadisticas con los valores nuevos desde la ultima
     * vez que se llamó a este método.
     * @param instante Tic de reloj al que se añadirán los últimos datos.
     * @since 1.0
     */    
    public void asentarDatos(long instante) {
        if (this.estadisticasActivas) {
            if (tSIPV4 > 0) {
		if (salientesIPv4.getItemCount() == 0) {
                    this.salientesIPv4.add(instante-1, 0);
                    this.salientesIPv4.add(instante, tSIPV4);
                    this.paquetesSalientes.addSeries(salientesIPv4);
                } else {
                    this.salientesIPv4.add(instante, tSIPV4);
                }
            } 
            
            if (tSIPV4_GOS1 > 0) {
                if (salientesIPv4_GOS1.getItemCount() == 0) {
                    this.salientesIPv4_GOS1.add(instante-1, 0);
                    this.salientesIPv4_GOS1.add(instante, tSIPV4_GOS1);
                    this.paquetesSalientes.addSeries(salientesIPv4_GOS1);
                } else {
                    this.salientesIPv4_GOS1.add(instante, tSIPV4_GOS1);
                }
            }
            
            if (tSIPV4_GOS2 > 0) {
                if (salientesIPv4_GOS2.getItemCount() == 0) {
                    this.salientesIPv4_GOS2.add(instante-1, 0);
                    this.salientesIPv4_GOS2.add(instante, tSIPV4_GOS2);
                    this.paquetesSalientes.addSeries(salientesIPv4_GOS2);
                } else {
                    this.salientesIPv4_GOS2.add(instante, tSIPV4_GOS2);
                }
            }
            
            if (tSIPV4_GOS3 > 0) {
                if (salientesIPv4_GOS3.getItemCount() == 0) {
                    this.salientesIPv4_GOS3.add(instante-1, 0);
                    this.salientesIPv4_GOS3.add(instante, tSIPV4_GOS3);
                    this.paquetesSalientes.addSeries(salientesIPv4_GOS3);
                } else {
                    this.salientesIPv4_GOS3.add(instante, tSIPV4_GOS3);
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
            
            if (tDIPV4 > 0) {
                if (descartadosIPv4.getItemCount() == 0) {
                    this.descartadosIPv4.add(instante-1, 0);
                    this.descartadosIPv4.add(instante, tDIPV4);
                    this.paquetesDescartados.addSeries(descartadosIPv4);
                } else {
                    this.descartadosIPv4.add(instante, tDIPV4);
                }
            }
            
            if (tDIPV4_GOS1 > 0) {
                if (descartadosIPv4_GOS1.getItemCount() == 0) {
                    this.descartadosIPv4_GOS1.add(instante-1, 0);
                    this.descartadosIPv4_GOS1.add(instante, tDIPV4_GOS1);
                    this.paquetesDescartados.addSeries(descartadosIPv4_GOS1);
                } else {
                    this.descartadosIPv4_GOS1.add(instante, tDIPV4_GOS1);
                }
            }

            if (tDIPV4_GOS2 > 0) {
                if (descartadosIPv4_GOS2.getItemCount() == 0) {
                    this.descartadosIPv4_GOS2.add(instante-1, 0);
                    this.descartadosIPv4_GOS2.add(instante, tDIPV4_GOS2);
                    this.paquetesDescartados.addSeries(descartadosIPv4_GOS2);
                } else {
                    this.descartadosIPv4_GOS2.add(instante, tDIPV4_GOS2);
                }
            }

            if (tDIPV4_GOS3 > 0) {
                if (descartadosIPv4_GOS3.getItemCount() == 0) {
                    this.descartadosIPv4_GOS3.add(instante-1, 0);
                    this.descartadosIPv4_GOS3.add(instante, tDIPV4_GOS3);
                    this.paquetesDescartados.addSeries(descartadosIPv4_GOS3);
                } else {
                    this.descartadosIPv4_GOS3.add(instante, tDIPV4_GOS3);
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
        }
    }    
    
    /**
     * Este método obtiene el título de la gráfica 1.
     * @return Título de la gráfica 1.
     * @since 1.0
     */    
    public String obtenerTitulo1() {
        return TEstadisticas.PAQUETES_SALIENTES;
    }
    
    /**
     * Este método obtiene el título de la gráfica 2.
     * @return Título de la gráfica 2.
     * @since 1.0
     */    
    public String obtenerTitulo2() {
        return TEstadisticas.PAQUETES_DESCARTADOS;
    }
    
    /**
     * Este método obtiene el título de la gráfica 3.
     * @return Título de la gráfica 3.
     * @since 1.0
     */    
    public String obtenerTitulo3() {
        return null;
    }
    
    /**
     * Este método obtiene el título de la gráfica 4.
     * @return Título de la gráfica 4.
     * @since 1.0
     */    
    public String obtenerTitulo4() {
        return null;
    }
    
    /**
     * Este método obtiene el título de la gráfica 5.
     * @return Título de la gráfica 5.
     * @since 1.0
     */    
    public String obtenerTitulo5() {
        return null;
    }
    
    /**
     * Este método obtiene el título de la gráfica 6.
     * @return Título de la gráfica 6.
     * @since 1.0
     */    
    public String obtenerTitulo6() {
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
