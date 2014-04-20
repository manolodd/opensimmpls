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
 * Esta clase implementa las estadísticas para un nodo LERA.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEstadisticasLERA extends TEstadisticas {
    
    /**
     * Crea una nueva instancia de TEstadisticasLERA
     * @since 1.0
     */
    public TEstadisticasLERA() {
    	paquetesEntrantes = new XYSeriesCollection();
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
    	salientesTLDP = new XYSeries(TEstadisticas.TLDP);
    	salientesGPSRP = new XYSeries(TEstadisticas.GPSRP);
    	entrantesIPv4 = new XYSeries(TEstadisticas.IPV4);
    	entrantesIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	entrantesIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	entrantesIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
    	entrantesMPLS = new XYSeries(TEstadisticas.MPLS);
    	entrantesMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
    	entrantesMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
    	entrantesMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	entrantesTLDP = new XYSeries(TEstadisticas.TLDP);
    	entrantesGPSRP = new XYSeries(TEstadisticas.GPSRP);
        descartadosIPv4 = new XYSeries(TEstadisticas.IPV4);
    	descartadosIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	descartadosIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	descartadosIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
	descartadosMPLS = new XYSeries(TEstadisticas.MPLS);
	descartadosMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
	descartadosMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
	descartadosMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	descartadosTLDP = new XYSeries(TEstadisticas.TLDP);
    	descartadosGPSRP = new XYSeries(TEstadisticas.GPSRP);
        tEIPV4 = 0;
        tEIPV4_GOS1 = 0;
        tEIPV4_GOS2 = 0;
        tEIPV4_GOS3 = 0;
        tEMPLS = 0;
        tEMPLS_GOS1 = 0;
        tEMPLS_GOS2 = 0;
        tEMPLS_GOS3 = 0;
        tETLDP = 0;
        tEGPSRP = 0;
        tSIPV4 = 0;
        tSIPV4_GOS1 = 0;
        tSIPV4_GOS2 = 0;
        tSIPV4_GOS3 = 0;
        tSMPLS = 0;
        tSMPLS_GOS1 = 0;
        tSMPLS_GOS2 = 0;
        tSMPLS_GOS3 = 0;
        tSTLDP = 0;
        tSGPSRP = 0;
        tDIPV4 = 0;
        tDIPV4_GOS1 = 0;
        tDIPV4_GOS2 = 0;
        tDIPV4_GOS3 = 0;
        tDMPLS = 0;
        tDMPLS_GOS1 = 0;
        tDMPLS_GOS2 = 0;
        tDMPLS_GOS3 = 0;
        tDTLDP = 0;
        tDGPSRP = 0;
        retransmisionesAtendidas = new DefaultCategoryDataset();
        solicitudesRecibidas = 0;
        retransmisionesRealizadas = 0;
        retransmisionesNoRealizadas = 0;
        recuperacionesLocales = new DefaultCategoryDataset();
        paquetesGoSPerdido = 0;
        solicitudesEmitidas = 0;
        paquetesGoSRecuperados = 0;
        paquetesGoSNoRecuperados = 0;
    }
    
    /**
     * Este método permite obtener los datos necesario para generar la gráfica 1.
     * @return Datos para la gráfica 1.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica1() {
        return this.paquetesEntrantes;
    }
    
    /**
     * Este método permite obtener los datos necesario para generar la gráfica 2.
     * @return Datos para la gráfica 2.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica2() {
        return this.paquetesSalientes;
    }
    
    /**
     * Este método permite obtener los datos necesario para generar la gráfica 3.
     * @return Datos para la gráfica 3.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica3() {
        return this.paquetesDescartados;
    }
    
    /**
     * Este método permite obtener los datos necesario para generar la gráfica 4.
     * @return Datos para la gráfica 4.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica4() {
        return this.retransmisionesAtendidas;
    }
    
    /**
     * Este método permite obtener los datos necesario para generar la gráfica 5.
     * @return Datos para la gráfica 5.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica5() {
        return this.recuperacionesLocales;
    }
    
    /**
     * Este método permite obtener los datos necesario para generar la gráfica 6.
     * @return Datos para la gráfica 6.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica6() {
        return null;
    }

    /**
     * Este metodo permite aumentar las estadísticas añadiendo para ello las del
     * paquete especificado.
     * @param paquete Paquete a contabilizar.
     * @param entrada ENTRADA, SALIDA o DESCARTE, dependiendo de si el paquete entra en el nodod, sale
     * de el o es descartado.
     * @since 1.0
     */    
    public void crearEstadistica(TPDU paquete, int entrada) {
        if (this.estadisticasActivas) {
            int tipoPaquete = paquete.obtenerSubTipo();
            int GoS = 0;
            if (tipoPaquete == TPDU.TLDP) {
                if (entrada == TEstadisticas.SALIDA) {
                    this.tSTLDP++;
                } else if (entrada == TEstadisticas.DESCARTE) {
                    this.tDTLDP++;
                } else if (entrada == TEstadisticas.ENTRADA) {
                    this.tETLDP++;
                }
            } else if (tipoPaquete == TPDU.GPSRP) {
                TPDUGPSRP pGPSRP = (TPDUGPSRP) paquete;
                int mensaje = pGPSRP.obtenerDatosGPSRP().obtenerMensaje();
                if (mensaje == TDatosGPSRP.SOLICITUD_RETRANSMISION) {
                    if (entrada == TEstadisticas.SALIDA) {
                        this.tSGPSRP++;
                        solicitudesEmitidas++;
                    } else if (entrada == TEstadisticas.DESCARTE) {
                        this.tDGPSRP++;
                    } else if (entrada == TEstadisticas.ENTRADA) {
                        this.tEGPSRP++;
                        solicitudesRecibidas++;
                    }
                } else if (mensaje == TDatosGPSRP.RETRANSMISION_NO) {
                    if (entrada == TEstadisticas.SALIDA) {
                        this.tSGPSRP++;
                        retransmisionesNoRealizadas++;
                    } else if (entrada == TEstadisticas.DESCARTE) {
                        this.tDGPSRP++;
                    } else if (entrada == TEstadisticas.ENTRADA) {
                        this.tEGPSRP++;
                        paquetesGoSNoRecuperados++;
                    }
                } else if (mensaje == TDatosGPSRP.RETRANSMISION_OK) {
                    if (entrada == TEstadisticas.SALIDA) {
                        this.tSGPSRP++;
                        retransmisionesRealizadas++;
                    } else if (entrada == TEstadisticas.DESCARTE) {
                        this.tDGPSRP++;
                    } else if (entrada == TEstadisticas.ENTRADA) {
                        this.tEGPSRP++;
                        paquetesGoSRecuperados++;
                    }
                }
            } else if (tipoPaquete == TPDU.MPLS) {
                if (entrada == TEstadisticas.SALIDA) {
                    this.tSMPLS++;
                } else if (entrada == TEstadisticas.DESCARTE) {
                    this.tDMPLS++;
                } else if (entrada == TEstadisticas.ENTRADA) {
                    this.tEMPLS++;
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
                    paquetesGoSPerdido++;
                    if ((GoS == TPDU.EXP_NIVEL0_SINLSP) || (GoS == TPDU.EXP_NIVEL0_CONLSP)) {
                        this.tDMPLS++;
                    } else if ((GoS == TPDU.EXP_NIVEL1_SINLSP) || (GoS == TPDU.EXP_NIVEL1_CONLSP)) {
                        this.tDMPLS_GOS1++;
                    } else if ((GoS == TPDU.EXP_NIVEL2_SINLSP) || (GoS == TPDU.EXP_NIVEL2_CONLSP)) {
                        this.tDMPLS_GOS2++;
                    } else if ((GoS == TPDU.EXP_NIVEL3_SINLSP) || (GoS == TPDU.EXP_NIVEL3_CONLSP)) {
                        this.tDMPLS_GOS3++;
                    }
                } else if (entrada == TEstadisticas.ENTRADA) {
                    if ((GoS == TPDU.EXP_NIVEL0_SINLSP) || (GoS == TPDU.EXP_NIVEL0_CONLSP)) {
                        this.tEMPLS++;
                    } else if ((GoS == TPDU.EXP_NIVEL1_SINLSP) || (GoS == TPDU.EXP_NIVEL1_CONLSP)) {
                        this.tEMPLS_GOS1++;
                    } else if ((GoS == TPDU.EXP_NIVEL2_SINLSP) || (GoS == TPDU.EXP_NIVEL2_CONLSP)) {
                        this.tEMPLS_GOS2++;
                    } else if ((GoS == TPDU.EXP_NIVEL3_SINLSP) || (GoS == TPDU.EXP_NIVEL3_CONLSP)) {
                        this.tEMPLS_GOS3++;
                    }
                }
            } else if (tipoPaquete == TPDU.IPV4) {
                if (entrada == TEstadisticas.SALIDA) {
                    this.tSIPV4++;
                } else if (entrada == TEstadisticas.DESCARTE) {
                    this.tDIPV4++;
                } else if (entrada == TEstadisticas.ENTRADA) {
                    this.tEIPV4++;
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
                } else if (entrada == TEstadisticas.ENTRADA) {
                    if ((GoS == TPDU.EXP_NIVEL0_SINLSP) || (GoS == TPDU.EXP_NIVEL0_CONLSP)) {
                        this.tEIPV4++;
                    } else if ((GoS == TPDU.EXP_NIVEL1_SINLSP) || (GoS == TPDU.EXP_NIVEL1_CONLSP)) {
                        this.tEIPV4_GOS1++;
                    } else if ((GoS == TPDU.EXP_NIVEL2_SINLSP) || (GoS == TPDU.EXP_NIVEL2_CONLSP)) {
                        this.tEIPV4_GOS2++;
                    } else if ((GoS == TPDU.EXP_NIVEL3_SINLSP) || (GoS == TPDU.EXP_NIVEL3_CONLSP)) {
                        this.tEIPV4_GOS3++;
                    }
                }
            }
        }
    }
    
    /**
     * Este método permite obtener el número de gráficas que genera el nodo LERA.
     * @return El número de gráficas que genera el LERA.
     * @since 1.0
     */    
    public int obtenerNumeroGraficas() {
        return 5;
    }
    
    /**
     * Este método reinicia los valores de los atributos de la clase, dejando la
     * instancia como si acabase de ser creada por el constructor.
     * @since 1.0
     */    
    public void reset() {
    	paquetesEntrantes = new XYSeriesCollection();
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
    	salientesTLDP = new XYSeries(TEstadisticas.TLDP);
    	salientesGPSRP = new XYSeries(TEstadisticas.GPSRP);
    	entrantesIPv4 = new XYSeries(TEstadisticas.IPV4);
    	entrantesIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	entrantesIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	entrantesIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
    	entrantesMPLS = new XYSeries(TEstadisticas.MPLS);
    	entrantesMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
    	entrantesMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
    	entrantesMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	entrantesTLDP = new XYSeries(TEstadisticas.TLDP);
    	entrantesGPSRP = new XYSeries(TEstadisticas.GPSRP);
        descartadosIPv4 = new XYSeries(TEstadisticas.IPV4);
    	descartadosIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	descartadosIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	descartadosIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
	descartadosMPLS = new XYSeries(TEstadisticas.MPLS);
	descartadosMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
	descartadosMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
	descartadosMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	descartadosTLDP = new XYSeries(TEstadisticas.TLDP);
    	descartadosGPSRP = new XYSeries(TEstadisticas.GPSRP);
        tEIPV4 = 0;
        tEIPV4_GOS1 = 0;
        tEIPV4_GOS2 = 0;
        tEIPV4_GOS3 = 0;
        tEMPLS = 0;
        tEMPLS_GOS1 = 0;
        tEMPLS_GOS2 = 0;
        tEMPLS_GOS3 = 0;
        tETLDP = 0;
        tEGPSRP = 0;
        tSIPV4 = 0;
        tSIPV4_GOS1 = 0;
        tSIPV4_GOS2 = 0;
        tSIPV4_GOS3 = 0;
        tSMPLS = 0;
        tSMPLS_GOS1 = 0;
        tSMPLS_GOS2 = 0;
        tSMPLS_GOS3 = 0;
        tSTLDP = 0;
        tSGPSRP = 0;
        tDIPV4 = 0;
        tDIPV4_GOS1 = 0;
        tDIPV4_GOS2 = 0;
        tDIPV4_GOS3 = 0;
        tDMPLS = 0;
        tDMPLS_GOS1 = 0;
        tDMPLS_GOS2 = 0;
        tDMPLS_GOS3 = 0;
        tDTLDP = 0;
        tDGPSRP = 0;
        retransmisionesAtendidas = new DefaultCategoryDataset();
        solicitudesRecibidas = 0;
        retransmisionesRealizadas = 0;
        retransmisionesNoRealizadas = 0;
        recuperacionesLocales = new DefaultCategoryDataset();
        paquetesGoSPerdido = 0;
        solicitudesEmitidas = 0;
        paquetesGoSRecuperados = 0;
        paquetesGoSNoRecuperados = 0;
    }
    
    /**
     * Este método actualiza las estadísticas con los últimos datos estadísticos
     * existentes desde la última vez que se llamó a este método.
     * @param instante Instante de tiempo al que se atribuirán los ultimos datos existentes.
     * @since 1.0
     */    
    public void asentarDatos(long instante) {
        if (this.estadisticasActivas) {
            if (tEIPV4 > 0) {
                if (entrantesIPv4.getItemCount() == 0) {
                    this.entrantesIPv4.add(instante-1, 0 );
                    this.entrantesIPv4.add(instante, tEIPV4);
                    this.paquetesEntrantes.addSeries(entrantesIPv4);
                } else {
                    this.entrantesIPv4.add(instante, tEIPV4);
                }
            }

            if (tEIPV4_GOS1 > 0) {
                if (entrantesIPv4_GOS1.getItemCount() == 0) {
                    this.entrantesIPv4_GOS1.add(instante-1, 0 );
                    this.entrantesIPv4_GOS1.add(instante, tEIPV4_GOS1);
                    this.paquetesEntrantes.addSeries(entrantesIPv4_GOS1);
                } else {
                    this.entrantesIPv4_GOS1.add(instante, tEIPV4_GOS1);
                }
            }

            if (tEIPV4_GOS2 > 0) {
                if (entrantesIPv4_GOS2.getItemCount() == 0) {
                    this.entrantesIPv4_GOS2.add(instante-1, 0 );
                    this.entrantesIPv4_GOS2.add(instante, tEIPV4_GOS2);
                    this.paquetesEntrantes.addSeries(entrantesIPv4_GOS2);
                } else {
                    this.entrantesIPv4_GOS2.add(instante, tEIPV4_GOS2);
                }
            }

            if (tEIPV4_GOS3 > 0) {
                if (entrantesIPv4_GOS3.getItemCount() == 0) {
                    this.entrantesIPv4_GOS3.add(instante-1, 0 );
                    this.entrantesIPv4_GOS3.add(instante, tEIPV4_GOS3);
                    this.paquetesEntrantes.addSeries(entrantesIPv4_GOS3);
                } else {
                    this.entrantesIPv4_GOS3.add(instante, tEIPV4_GOS3);
                }
            }

            if (tEMPLS > 0) {
                if (entrantesMPLS.getItemCount() == 0) {
                    this.entrantesMPLS.add(instante-1, 0 );
                    this.entrantesMPLS.add(instante, tEMPLS);
                    this.paquetesEntrantes.addSeries(entrantesMPLS);
                } else {
                    this.entrantesMPLS.add(instante, tEMPLS);
                }
            }

            if (tEMPLS_GOS1 > 0) {
                if (entrantesMPLS_GOS1.getItemCount() == 0) {
                    this.entrantesMPLS_GOS1.add(instante-1, 0 );
                    this.entrantesMPLS_GOS1.add(instante, tEMPLS_GOS1);
                    this.paquetesEntrantes.addSeries(entrantesMPLS_GOS1);
                } else {
                    this.entrantesMPLS_GOS1.add(instante, tEMPLS_GOS1);
                }
            }

            if (tEMPLS_GOS2 > 0) {
                if (entrantesMPLS_GOS2.getItemCount() == 0) {
                    this.entrantesMPLS_GOS2.add(instante-1, 0 );
                    this.entrantesMPLS_GOS2.add(instante, tEMPLS_GOS2);
                    this.paquetesEntrantes.addSeries(entrantesMPLS_GOS2);
                } else {
                    this.entrantesMPLS_GOS2.add(instante, tEMPLS_GOS2);
                }
            }

            if (tEMPLS_GOS3 > 0) {
                if (entrantesMPLS_GOS3.getItemCount() == 0) {
                    this.entrantesMPLS_GOS3.add(instante-1, 0 );
                    this.entrantesMPLS_GOS3.add(instante, tEMPLS_GOS3);
                    this.paquetesEntrantes.addSeries(entrantesMPLS_GOS3);
                } else {
                    this.entrantesMPLS_GOS3.add(instante, tEMPLS_GOS3);
                }
            }

            if (tETLDP > 0) {
                if (entrantesTLDP.getItemCount() == 0) {
                    this.entrantesTLDP.add(instante-1, 0 );
                    this.entrantesTLDP.add(instante, tETLDP);
                    this.paquetesEntrantes.addSeries(entrantesTLDP);
                } else {
                    this.entrantesTLDP.add(instante, tETLDP);
                }
            }

            if (tEGPSRP > 0) {
                if (entrantesGPSRP.getItemCount() == 0) {
                    this.entrantesGPSRP.add(instante-1, 0 );
                    this.entrantesGPSRP.add(instante, tEGPSRP);
                    this.paquetesEntrantes.addSeries(entrantesGPSRP);
                } else {
                    this.entrantesGPSRP.add(instante, tEGPSRP);
                }
            }

            if (tSIPV4 > 0) {
                if (salientesIPv4.getItemCount() == 0) {
                    this.salientesIPv4.add(instante-1, 0 );
                    this.salientesIPv4.add(instante, tSIPV4);
                    this.paquetesSalientes.addSeries(salientesIPv4);
                } else {
                    this.salientesIPv4.add(instante, tSIPV4);
                }
            }
            
            if (tSIPV4_GOS1 > 0) {
                if (salientesIPv4_GOS1.getItemCount() == 0) {
                    this.salientesIPv4_GOS1.add(instante-1, 0 );
                    this.salientesIPv4_GOS1.add(instante, tSIPV4_GOS1);
                    this.paquetesSalientes.addSeries(salientesIPv4_GOS1);
                } else {
                    this.salientesIPv4_GOS1.add(instante, tSIPV4_GOS1);
                }
            }
            
            if (tSIPV4_GOS2 > 0) {
                if (salientesIPv4_GOS2.getItemCount() == 0) {
                    this.salientesIPv4_GOS2.add(instante-1, 0 );
                    this.salientesIPv4_GOS2.add(instante, tSIPV4_GOS2);
                    this.paquetesSalientes.addSeries(salientesIPv4_GOS2);
                } else {
                    this.salientesIPv4_GOS2.add(instante, tSIPV4_GOS2);
                }
            }
            
            if (tSIPV4_GOS3 > 0) {
                if (salientesIPv4_GOS3.getItemCount() == 0) {
                    this.salientesIPv4_GOS3.add(instante-1, 0 );
                    this.salientesIPv4_GOS3.add(instante, tSIPV4_GOS3);
                    this.paquetesSalientes.addSeries(salientesIPv4_GOS3);
                } else {
                    this.salientesIPv4_GOS3.add(instante, tSIPV4_GOS3);
                }
            }
            
            if (tSMPLS > 0) {
                if (salientesMPLS.getItemCount() == 0) {
                    this.salientesMPLS.add(instante-1, 0 );
                    this.salientesMPLS.add(instante, tSMPLS);
                    this.paquetesSalientes.addSeries(salientesMPLS);
                } else {
                    this.salientesMPLS.add(instante, tSMPLS);
                }
            }
            
            if (tSMPLS_GOS1 > 0) {
                if (salientesMPLS_GOS1.getItemCount() == 0) {
                    this.salientesMPLS_GOS1.add(instante-1, 0 );
                    this.salientesMPLS_GOS1.add(instante, tSMPLS_GOS1);
                    this.paquetesSalientes.addSeries(salientesMPLS_GOS1);
                } else {
                    this.salientesMPLS_GOS1.add(instante, tSMPLS_GOS1);
                }
            }
            
            if (tSMPLS_GOS2 > 0) {
                if (salientesMPLS_GOS2.getItemCount() == 0) {
                    this.salientesMPLS_GOS2.add(instante-1, 0 );
                    this.salientesMPLS_GOS2.add(instante, tSMPLS_GOS2);
                    this.paquetesSalientes.addSeries(salientesMPLS_GOS2);
                } else {
                    this.salientesMPLS_GOS2.add(instante, tSMPLS_GOS2);
                }
            }
            
            if (tSMPLS_GOS3 > 0) {
                if (salientesMPLS_GOS3.getItemCount() == 0) {
                    this.salientesMPLS_GOS3.add(instante-1, 0 );
                    this.salientesMPLS_GOS3.add(instante, tSMPLS_GOS3);
                    this.paquetesSalientes.addSeries(salientesMPLS_GOS3);
                } else {
                    this.salientesMPLS_GOS3.add(instante, tSMPLS_GOS3);
                }
            }
            
            if (tSTLDP > 0) {
                if (salientesTLDP.getItemCount() == 0) {
                    this.salientesTLDP.add(instante-1, 0 );
                    this.salientesTLDP.add(instante, tSTLDP);
                    this.paquetesSalientes.addSeries(salientesTLDP);
                } else {
                    this.salientesTLDP.add(instante, tSTLDP);
                }
            }
            
            if (tSGPSRP > 0) {
                if (salientesGPSRP.getItemCount() == 0) {
                    this.salientesGPSRP.add(instante-1, 0 );
                    this.salientesGPSRP.add(instante, tSGPSRP);
                    this.paquetesSalientes.addSeries(salientesGPSRP);
                } else {
                    this.salientesGPSRP.add(instante, tSGPSRP);
                }
            }
                    
            if (tDIPV4 > 0) {
                if (descartadosIPv4.getItemCount() == 0) {
                    this.descartadosIPv4.add(instante-1, 0 );
                    this.descartadosIPv4.add(instante, tDIPV4);
                    this.paquetesDescartados.addSeries(descartadosIPv4);
                } else {
                    this.descartadosIPv4.add(instante, tDIPV4);
                }
            }
                    
            if (tDIPV4_GOS1 > 0) {
                if (descartadosIPv4_GOS1.getItemCount() == 0) {
                    this.descartadosIPv4_GOS1.add(instante-1, 0 );
                    this.descartadosIPv4_GOS1.add(instante, tDIPV4_GOS1);
                    this.paquetesDescartados.addSeries(descartadosIPv4_GOS1);
                } else {
                    this.descartadosIPv4_GOS1.add(instante, tDIPV4_GOS1);
                }
            }

            if (tDIPV4_GOS2 > 0) {
                if (descartadosIPv4_GOS2.getItemCount() == 0) {
                    this.descartadosIPv4_GOS2.add(instante-1, 0 );
                    this.descartadosIPv4_GOS2.add(instante, tDIPV4_GOS2);
                    this.paquetesDescartados.addSeries(descartadosIPv4_GOS2);
                } else {
                    this.descartadosIPv4_GOS2.add(instante, tDIPV4_GOS2);
                }
            }

            if (tDIPV4_GOS3 > 0) {
                if (descartadosIPv4_GOS3.getItemCount() == 0) {
                    this.descartadosIPv4_GOS3.add(instante-1, 0 );
                    this.descartadosIPv4_GOS3.add(instante, tDIPV4_GOS3);
                    this.paquetesDescartados.addSeries(descartadosIPv4_GOS3);
                } else {
                    this.descartadosIPv4_GOS3.add(instante, tDIPV4_GOS3);
                }
            }

            if (tDMPLS > 0) {
                if (descartadosMPLS.getItemCount() == 0) {
                    this.descartadosMPLS.add(instante-1, 0 );
                    this.descartadosMPLS.add(instante, tDMPLS);
                    this.paquetesDescartados.addSeries(descartadosMPLS);
                } else {
                    this.descartadosMPLS.add(instante, tDMPLS);
                }
            }

            if (tDMPLS_GOS1 > 0) {
                if (descartadosMPLS_GOS1.getItemCount() == 0) {
                    this.descartadosMPLS_GOS1.add(instante-1, 0 );
                    this.descartadosMPLS_GOS1.add(instante, tDMPLS_GOS1);
                    this.paquetesDescartados.addSeries(descartadosMPLS_GOS1);
                } else {
                    this.descartadosMPLS_GOS1.add(instante, tDMPLS_GOS1);
                }
            }

            if (tDMPLS_GOS2 > 0) {
                if (descartadosMPLS_GOS2.getItemCount() == 0) {
                    this.descartadosMPLS_GOS2.add(instante-1, 0 );
                    this.descartadosMPLS_GOS2.add(instante, tDMPLS_GOS2);
                    this.paquetesDescartados.addSeries(descartadosMPLS_GOS2);
                } else {
                    this.descartadosMPLS_GOS2.add(instante, tDMPLS_GOS2);
                }
            }

            if (tDMPLS_GOS3 > 0) {
                if (descartadosMPLS_GOS3.getItemCount() == 0) {
                    this.descartadosMPLS_GOS3.add(instante-1, 0 );
                    this.descartadosMPLS_GOS3.add(instante, tDMPLS_GOS3);
                    this.paquetesDescartados.addSeries(descartadosMPLS_GOS3);
                } else {
                    this.descartadosMPLS_GOS3.add(instante, tDMPLS_GOS3);
                }
            }

            if (tDTLDP > 0) {
                if (descartadosTLDP.getItemCount() == 0) {
                    this.descartadosTLDP.add(instante-1, 0 );
                    this.descartadosTLDP.add(instante, tDTLDP);
                    this.paquetesDescartados.addSeries(descartadosTLDP);
                } else {
                    this.descartadosTLDP.add(instante, tDTLDP);
                }
            }

            if (tDGPSRP > 0) {
                if (descartadosGPSRP.getItemCount() == 0) {
                    this.descartadosGPSRP.add(instante-1, 0 );
                    this.descartadosGPSRP.add(instante, tDGPSRP);
                    this.paquetesDescartados.addSeries(descartadosGPSRP);
                } else {
                    this.descartadosGPSRP.add(instante, tDGPSRP);
                }
            }
            
            this.retransmisionesAtendidas.addValue(this.solicitudesRecibidas, TEstadisticas.SOLICITUDES_RECIBIDAS, "");
            this.retransmisionesAtendidas.addValue(this.retransmisionesRealizadas, TEstadisticas.RETRANSMISIONES_REALIZADAS, "");
            this.retransmisionesAtendidas.addValue(this.retransmisionesNoRealizadas, TEstadisticas.RETRANSMISIONES_NO_REALIZADAS, "");
            this.recuperacionesLocales.addValue(this.paquetesGoSPerdido, TEstadisticas.PAQUETES_GOS_PERDIDOS, "");
            this.recuperacionesLocales.addValue(this.solicitudesEmitidas, TEstadisticas.SOLICITUDES_EMITIDAS, "");
            this.recuperacionesLocales.addValue(this.paquetesGoSRecuperados, TEstadisticas.PAQUETES_GOS_RECUPERADOS, "");
            this.recuperacionesLocales.addValue(this.paquetesGoSNoRecuperados, TEstadisticas.PAQUETES_GOS_NO_RECUPERADOS, "");
            int sinRespuesta = (solicitudesEmitidas - paquetesGoSRecuperados - paquetesGoSNoRecuperados);
            if (sinRespuesta < 0) {
                sinRespuesta = 0;
            }
            this.recuperacionesLocales.addValue(sinRespuesta, TEstadisticas.SOLICITUDES_SIN_RESPUESTA_AUN, "");
        }
    }    
    
    /**
     * Este método permite obtener el título de la gráfica 1.
     * @return Título de la gráfica 1.
     * @since 1.0
     */    
    public String obtenerTitulo1() {
        return TEstadisticas.PAQUETES_ENTRANTES;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 2.
     * @return Título de la gráfica 2.
     * @since 1.0
     */    
    public String obtenerTitulo2() {
        return TEstadisticas.PAQUETES_SALIENTES;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 3.
     * @return Título de la gráfica 3.
     * @since 1.0
     */    
    public String obtenerTitulo3() {
        return TEstadisticas.PAQUETES_DESCARTADOS;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 4.
     * @return Título de la gráfica 4.
     * @since 1.0
     */    
    public String obtenerTitulo4() {
        return TEstadisticas.RETRANSMISIONES_ATENDIDAS;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 5.
     * @return Título de la gráfica 5.
     * @since 1.0
     */    
    public String obtenerTitulo5() {
        return TEstadisticas.RECUPERACIONES_LOCALES;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 6.
     * @return Título de la gráfica 6.
     * @since 1.0
     */    
    public String obtenerTitulo6() {
        return null;
    }
    
    private int tEIPV4;
    private int tEIPV4_GOS1;
    private int tEIPV4_GOS2;
    private int tEIPV4_GOS3;
    private int tEMPLS;
    private int tEMPLS_GOS1;
    private int tEMPLS_GOS2;
    private int tEMPLS_GOS3;
    private int tETLDP;
    private int tEGPSRP;
    private int tSIPV4;
    private int tSIPV4_GOS1;
    private int tSIPV4_GOS2;
    private int tSIPV4_GOS3;
    private int tSMPLS;
    private int tSMPLS_GOS1;
    private int tSMPLS_GOS2;
    private int tSMPLS_GOS3;
    private int tSTLDP;
    private int tSGPSRP;
    private int tDIPV4;
    private int tDIPV4_GOS1;
    private int tDIPV4_GOS2;
    private int tDIPV4_GOS3;
    private int tDMPLS;
    private int tDMPLS_GOS1;
    private int tDMPLS_GOS2;
    private int tDMPLS_GOS3;
    private int tDTLDP;
    private int tDGPSRP;
    private XYSeriesCollection paquetesEntrantes;
    private XYSeriesCollection paquetesSalientes;
    private XYSeriesCollection paquetesDescartados;
    private XYSeries entrantesIPv4;
    private XYSeries entrantesIPv4_GOS1;
    private XYSeries entrantesIPv4_GOS2;
    private XYSeries entrantesIPv4_GOS3;
    private XYSeries entrantesMPLS;
    private XYSeries entrantesMPLS_GOS1;
    private XYSeries entrantesMPLS_GOS2;
    private XYSeries entrantesMPLS_GOS3;
    private XYSeries entrantesTLDP;
    private XYSeries entrantesGPSRP;
    private XYSeries salientesIPv4;
    private XYSeries salientesIPv4_GOS1;
    private XYSeries salientesIPv4_GOS2;
    private XYSeries salientesIPv4_GOS3;
    private XYSeries salientesMPLS;
    private XYSeries salientesMPLS_GOS1;
    private XYSeries salientesMPLS_GOS2;
    private XYSeries salientesMPLS_GOS3;
    private XYSeries salientesTLDP;
    private XYSeries salientesGPSRP;
    private XYSeries descartadosIPv4;
    private XYSeries descartadosIPv4_GOS1;
    private XYSeries descartadosIPv4_GOS2;
    private XYSeries descartadosIPv4_GOS3;
    private XYSeries descartadosMPLS;
    private XYSeries descartadosMPLS_GOS1;
    private XYSeries descartadosMPLS_GOS2;
    private XYSeries descartadosMPLS_GOS3;
    private XYSeries descartadosTLDP;
    private XYSeries descartadosGPSRP;
    private DefaultCategoryDataset retransmisionesAtendidas;
    private int solicitudesRecibidas;
    private int retransmisionesRealizadas;
    private int retransmisionesNoRealizadas;
    private DefaultCategoryDataset recuperacionesLocales;
    private int paquetesGoSPerdido;
    private int solicitudesEmitidas;
    private int paquetesGoSRecuperados;
    private int paquetesGoSNoRecuperados;
}
