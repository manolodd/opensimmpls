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
 * Esta lase implementa las estadísticas para un nodo receptor.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEstadisticasReceptor extends TEstadisticas {
    
    /**
     * Este método crea una nueva instancia de TEstadisticasReceptor.
     * @since 1.0
     */
    public TEstadisticasReceptor() {
    	paquetesEntrantes = new XYSeriesCollection();
    	entrantesIPv4 = new XYSeries(TEstadisticas.IPV4);
    	entrantesIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	entrantesIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	entrantesIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
    	entrantesMPLS = new XYSeries(TEstadisticas.MPLS);
    	entrantesMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
    	entrantesMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
    	entrantesMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	entrantesGPSRP = new XYSeries(TEstadisticas.GPSRP);
        tEIPV4 = 0;
        tEIPV4_GOS1 = 0;
        tEIPV4_GOS2 = 0;
        tEIPV4_GOS3 = 0;
        tEMPLS = 0;
        tEMPLS_GOS1 = 0;
        tEMPLS_GOS2 = 0;
        tEMPLS_GOS3 = 0;
        tEGPSRP = 0;
    }
    
    /**
     * Este método permite obtener los datos necesarios para poder generar la gráfica
     * 1.
     * @return Datos de la gráfica 1.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica1() {
        return this.paquetesEntrantes;
    }
    
    /**
     * Este método permite obtener los datos necesarios para poder generar la gráfica
     * 2.
     * @return Datos de la gráfica 2.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica2() {
        return null;
    }
    
    /**
     * Este método permite obtener los datos necesarios para poder generar la gráfica
     * 3.
     * @return Datos de la gráfica 3.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica3() {
        return null;
    }
    
    /**
     * Este método permite obtener los datos necesarios para poder generar la gráfica
     * 4.
     * @return Datos de la gráfica 4.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica4() {
        return null;
    }
    
    /**
     * Este método permite obtener los datos necesarios para poder generar la gráfica
     * 5.
     * @return Datos de la gráfica 5.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica5() {
        return null;
    }
    
    /**
     * Este método permite obtener los datos necesarios para poder generar la gráfica
     * 6.
     * @return Datos de la gráfica 6.
     * @since 1.0
     */    
    public org.jfree.data.AbstractDataset obtenerDatosGrafica6() {
        return null;
    }

    /**
     * Este método permite ampliar las estadísticas añadiendo las correspondientes para
     * el paquete especificado.
     * @param paquete Paquete que se desea contabilizar.
     * @param entrada ENTRADA, SALIDA o DESCARTE, dependiendo de si el paquete entra en el nodo, sale
     * de él o es descartado.
     * @since 1.0
     */    
    public void crearEstadistica(TPDU paquete, int entrada) {
        if (this.estadisticasActivas) {
            int tipoPaquete = paquete.obtenerSubTipo();
            int GoS = 0;
            if (tipoPaquete == TPDU.GPSRP) {
                if (entrada == TEstadisticas.ENTRADA) {
                    this.tEGPSRP++;
                }
            } else if (tipoPaquete == TPDU.MPLS) {
                if (entrada == TEstadisticas.ENTRADA) {
                    this.tEMPLS++;
                }
            } else if (tipoPaquete == TPDU.MPLS_GOS) {
                GoS = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerNivelGoS();
                if (entrada == TEstadisticas.ENTRADA) {
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
                if (entrada == TEstadisticas.ENTRADA) {
                    this.tEIPV4++;
                }
            } else if (tipoPaquete == TPDU.IPV4_GOS) {
                GoS = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerNivelGoS();
                if (entrada == TEstadisticas.ENTRADA) {
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
     * Este método permite obtener el número de graficas del nodo receptor.
     * @return Número de gráficas del nodo receptor.
     * @since 1.0
     */    
    public int obtenerNumeroGraficas() {
        return 1;
    }
    
    /**
     * Este método reinicia los atributos de la clase, dejando las instancia como si
     * acabase de ser creada por el constructor.
     * @since 1.0
     */    
    public void reset() {
    	paquetesEntrantes = new XYSeriesCollection();
    	entrantesIPv4 = new XYSeries(TEstadisticas.IPV4);
    	entrantesIPv4_GOS1 = new XYSeries(TEstadisticas.IPV4_GOS1);
    	entrantesIPv4_GOS2 = new XYSeries(TEstadisticas.IPV4_GOS2);
    	entrantesIPv4_GOS3 = new XYSeries(TEstadisticas.IPV4_GOS3);
    	entrantesMPLS = new XYSeries(TEstadisticas.MPLS);
    	entrantesMPLS_GOS1 = new XYSeries(TEstadisticas.MPLS_GOS1);
    	entrantesMPLS_GOS2 = new XYSeries(TEstadisticas.MPLS_GOS2);
    	entrantesMPLS_GOS3 = new XYSeries(TEstadisticas.MPLS_GOS3);
    	entrantesGPSRP = new XYSeries(TEstadisticas.GPSRP);
        tEIPV4 = 0;
        tEIPV4_GOS1 = 0;
        tEIPV4_GOS2 = 0;
        tEIPV4_GOS3 = 0;
        tEMPLS = 0;
        tEMPLS_GOS1 = 0;
        tEMPLS_GOS2 = 0;
        tEMPLS_GOS3 = 0;
        tEGPSRP = 0;
    }
    
    /**
     * Este método permite ampliar las estadísticas con los ultimos datos especificados
     * desde la ultima vez que se llamó a este método.
     * @param instante Instante de tiempo al que se atribuirán los ultimos datos recolectados.
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
            
            if (tEGPSRP > 0) {
                if (entrantesGPSRP.getItemCount() == 0) {
                    this.entrantesGPSRP.add(instante-1, 0 );
                    this.entrantesGPSRP.add(instante, tEGPSRP);
                    this.paquetesEntrantes.addSeries(entrantesGPSRP);
                } else {
                    this.entrantesGPSRP.add(instante, tEGPSRP);
                }
            }
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
        return null;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 3.
     * @return Título de la gráfica 3.
     * @since 1.0
     */    
    public String obtenerTitulo3() {
        return null;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 4.
     * @return Título de la gráfica 4.
     * @since 1.0
     */    
    public String obtenerTitulo4() {
        return null;
    }
    
    /**
     * Este método permite obtener el título de la gráfica 5.
     * @return Título de la gráfica 5.
     * @since 1.0
     */    
    public String obtenerTitulo5() {
        return null;
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
    private int tEGPSRP;
    private XYSeriesCollection paquetesEntrantes;
    private XYSeries entrantesIPv4;
    private XYSeries entrantesIPv4_GOS1;
    private XYSeries entrantesIPv4_GOS2;
    private XYSeries entrantesIPv4_GOS3;
    private XYSeries entrantesMPLS;
    private XYSeries entrantesMPLS_GOS1;
    private XYSeries entrantesMPLS_GOS2;
    private XYSeries entrantesMPLS_GOS3;
    private XYSeries entrantesGPSRP;
}
