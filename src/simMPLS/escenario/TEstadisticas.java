/*
 * TEstadisticas.java
 *
 * Created on 5 de octubre de 2004, 20:11
 */

package simMPLS.escenario;

import org.jfree.chart.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;
import simMPLS.protocolo.*;

/**
 * Esta clase es superclase abstracta. Todas las estadísticas de los nodos de la
 * topología deben heredar e implementar los métodos abstractos.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TEstadisticas {
    
    /**
     * Crea una nueva instancia de TEstadisticas
     * @since 1.0
     */
    public TEstadisticas() {
        estadisticasActivas = false;
    }

    /**
     * Este método activa o desactiva las estadísticas.
     * @since 1.0
     * @param a TRUE, activa las estadísticas. FALSE las desactiva.
     */    
    public void activarEstadisticas(boolean a) {
        this.estadisticasActivas = a;
    }

    /**
     * Este método obtiene los datos de la gráfica 1.
     * @since 1.0
     * @return Datos de la gráfica 1.
     */    
    public abstract AbstractDataset obtenerDatosGrafica1();
    /**
     * Este método obtiene los datos de la gráfica 2.
     * @since 1.0
     * @return Datos de la gráfica 2.
     */    
    public abstract AbstractDataset obtenerDatosGrafica2();
    /**
     * Este método obtiene los datos de la gráfica 3.
     * @since 1.0
     * @return Datos de la gráfica 3.
     */    
    public abstract AbstractDataset obtenerDatosGrafica3();
    /**
     * Este método obtiene los datos de la gráfica 4.
     * @since 1.0
     * @return Datos de la gráfica 4.
     */    
    public abstract AbstractDataset obtenerDatosGrafica4();
    /**
     * Este método obtiene los datos de la gráfica 5.
     * @since 1.0
     * @return Datos de la gráfica 5.
     */    
    public abstract AbstractDataset obtenerDatosGrafica5();
    /**
     * Este método obtiene los datos de la gráfica 6.
     * @since 1.0
     * @return Datos de la gráfica 6.
     */    
    public abstract AbstractDataset obtenerDatosGrafica6();
    /**
     * Este método obtiene el título de la gráfica 1.
     * @return El título de la gráfica 1
     * @since 1.0
     */
    public abstract String obtenerTitulo1();
    /**
     * Este método obtiene el título de la gráfica 2.
     * @return El título de la gráfica 2
     * @since 1.0
     */
    public abstract String obtenerTitulo2();
    /**
     * Este método obtiene el título de la gráfica 3.
     * @return El título de la gráfica 3
     * @since 1.0
     */
    public abstract String obtenerTitulo3();
    /**
     * Este método obtiene el título de la gráfica 4.
     * @return El título de la gráfica 4
     * @since 1.0
     */
    public abstract String obtenerTitulo4();
    /**
     * Este método obtiene el título de la gráfica 5.
     * @return El título de la gráfica 5
     * @since 1.0
     */
    public abstract String obtenerTitulo5();
    /**
     * Este método obtiene el título de la gráfica 6.
     * @return El título de la gráfica 6
     * @since 1.0
     */
    public abstract String obtenerTitulo6();
    /**
     * Este método modifica las estadísticas, añadiendo las necesarias para el paquete
     * especificado.
     * @param paquete Paquete que se desea anotar en las estadísticas.
     * @param entrada ENTRADA, SALIDA, DESCARTE dependiendo si el paquete ha entrado del nodo, sallido
     * o ha sido descartado.
     * @since 1.0
     */    
    public abstract void crearEstadistica(TPDU paquete, int entrada);
    /**
     * Este método añade los datos modificados desde la última vez que se llamó a este
     * método, en las estadísticas.
     * @param instante Instante al que se asignarán los últimos datos.
     * @since 1.0
     */    
    public abstract void asentarDatos(long instante);
    /**
     * Devuelve el número de gráficas que contiene la instancia.
     * @return Número de gráficas.
     * @since 1.0
     */    
    public abstract int obtenerNumeroGraficas();
    /**
     * Reinicia las estadísticas y las deja como si acabasen de ser creadas por el
     * constructor.
     * @since 1.0
     */    
    public abstract void reset();
    
    /**
     * Este atributo almacenará si las estadísticas están activada o no.
     * @since 1.0
     */    
    protected boolean estadisticasActivas;
    
    /**
     * Esta constante es un texto que representa a paquetes de tipo IPv4
     * @since 1.0
     */    
    public static final String IPV4 = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.IPv4");
    /**
     * Esta constante es un texto que representa a paquetes IPv4 con GoS 1
     * @since 1.0
     */    
    public static final String IPV4_GOS1 = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.IPv4_GoS1");
    /**
     * Esta constante es un texto que representa a paquetes IPv4 con GoS 2
     * @since 1.0
     */    
    public static final String IPV4_GOS2 = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.IPv4_GoS2");
    /**
     * Esta constante es un texto que representa a paquetes IPv4 con GoS 3
     * @since 1.0
     */    
    public static final String IPV4_GOS3 = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.IPv4_Gos3");
    /**
     * Esta constante es un texto que representa a paquetes MPLS
     * @since 1.0
     */    
    public static final String MPLS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.MPLS");
    /**
     * Esta constante es un texto que representa a paquetes MPLS con GoS 1
     * @since 1.0
     */    
    public static final String MPLS_GOS1 = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.MPLS_GoS1");
    /**
     * Esta constante es un texto que representa a paquetes MPLS con GoS 2
     * @since 1.0
     */    
    public static final String MPLS_GOS2 = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.MPLS_GoS2");
    /**
     * Esta constante es un texto que representa a paquetes MPLS con GoS 3
     * @since 1.0
     */    
    public static final String MPLS_GOS3 = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.MPLS_GoS3");
    /**
     * Esta constante es un texto que representa a paquetes TLDP
     * @since 1.0
     */    
    public static final String TLDP = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.TLDP");
    /**
     * Esta constante es un texto que representa a paquetes GPSRP
     * @since 1.0
     */    
    public static final String GPSRP = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.GPSRP");
    
    /**
     * Esta constante es un texto que representa al tiempo
     * @since 1.0
     */    
    public static final String TIEMPO = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Tiempo_ns");
    /**
     * Esta constante es un texto que representa al número de paquetes
     * @since 1.0
     */    
    public static final String NUMERO_DE_PAQUETES = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Numero_de_paquetes");
    /**
     * Esta constante es un texto que representa al número
     * @since 1.0
     */    
    public static final String NUMERO = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Numero");
    /**
     * Esta constante es un texto que representa al tamaño de la DMGP
     * @since 1.0
     */    
    public static final String TAMANIO_DMGP = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Tamanio_DMGP_Kilobytes");
    
    /**
     * Esta constante es un texto que representa a los paquetes salientes
     * @since 1.0
     */    
    public static final String PAQUETES_SALIENTES = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_salientes");
    /**
     * Esta constante es un texto que representa a los paquetes entrantes.
     * @since 1.0
     */    
    public static final String PAQUETES_ENTRANTES = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_entrantes");
    /**
     * Esta constante es un texto que representa a los paquetes descartados.
     * @since 1.0
     */    
    public static final String PAQUETES_DESCARTADOS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_descartados");
    /**
     * Esta constante es un texto que representa a los paquetes recuperados.
     * @since 1.0
     */    
    public static final String PAQUETES_RECUPERADOS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_recuperados");
    /**
     * Esta constante es un texto que representa a los paquetes no recuperados.
     * @since 1.0
     */    
    public static final String PAQUETES_NO_RECUPERADOS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_no_recuperados");
    /**
     * Esta constante es un texto que representa a las recuperaciones locales
     * @since 1.0
     */    
    public static final String RECUPERACIONES_LOCALES = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Recuperaciones_locales_de_paquetes_con_GoS");
    /**
     * Esta constante es un texto que representa a las retransmisiones atendidas
     * @since 1.0
     */    
    public static final String RETRANSMISIONES_ATENDIDAS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Retransmisiones_de_paquetes_con_GoS_atendidas");
    /**
     * Esta constante es un texto que representa a las solicitudes atendidas.
     * @since 1.0
     */    
    public static final String SOLICITUDES_ATENDIDAS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Solicitudes_atendidas");
    /**
     * Esta constante es un texto que representa a las retransmisiones realizadas
     * @since 1.0
     */    
    public static final String RETRANSMISIONES_REALIZADAS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Retransmisiones_realizadas");
    /**
     * Esta constante es un texto que representa a las retransmisiones no realizadas.
     * @since 1.0
     */    
    public static final String RETRANSMISIONES_NO_REALIZADAS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Retransmisiones_no_realizadas");
    /**
     * Esta constante es un texto que representa a las solicitudes recibidas
     * @since 1.0
     */    
    public static final String SOLICITUDES_RECIBIDAS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Solicitudes_recibidas");
    /**
     * Esta constante es un texto que representa a los paquetes con GoS perdidos
     * @since 1.0
     */    
    public static final String PAQUETES_GOS_PERDIDOS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_GoS_perdidos");
    /**
     * Esta constante es un texto que representa a las solicitudes emitidas.
     * @since 1.0
     */    
    public static final String SOLICITUDES_EMITIDAS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Solicitudes_emitidas");
    /**
     * Esta constante es un texto que representa a las solicitudes sin respuestas.
     * @since 1.0
     */    
    public static final String SOLICITUDES_SIN_RESPUESTA_AUN = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Solicitudes_sin_respuesta_aun");
    /**
     * Esta constante es un texto que representa a los paquetes con GoS recuperados
     * @since 1.0
     */    
    public static final String PAQUETES_GOS_RECUPERADOS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_GoS_recuperados");
    /**
     * Esta constante es un texto que representa a los paquetes con GoS no recuperados.
     * @since 1.0
     */    
    public static final String PAQUETES_GOS_NO_RECUPERADOS = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Paquetes_GoS_no_recuperados");
    /**
     * Esta constante es un texto que representa a la descripción de categorías.
     * @since 1.0
     */    
    public static final String DESCRIPCION = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEstadisticas.Descripcion");
    
    /**
     * Esta constante indica un paquete entrante en el nodo.
     * @since 1.0
     */    
    public static final int ENTRADA = -1;
    /**
     * Esta constante indica un paquete saliente del nodo.
     * @since 1.0
     */    
    public static final int SALIDA = -2;
    /**
     * Esta constante indica un paquete descartado en el nodo.
     * @since 1.0
     */    
    public static final int DESCARTE = -3;
}
