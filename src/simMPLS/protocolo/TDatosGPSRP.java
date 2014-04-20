package simMPLS.protocolo;

/**
 * Esta clase implementa el payload de un paquete GPSRP, es decir, mensajes para la
 * recuperación local de paquetes.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TDatosGPSRP {
    
    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de
     * TDatosGPSRP.
     * @since 1.0
     */
    public TDatosGPSRP() {
        mensaje = this.SOLICITUD_RETRANSMISION;
        flujo = 0;
        idPaquete = 0;
    }
    
    /**
     * Este método permite establecer el flujo al que pertenece el paquete cuya
     * a cuya retransmisión se refiere este mensaje.
     * @param idf El identificador del flujo al que pertenece el paquete solicitado.
     * @since 1.0
     */    
    public void ponerFlujo(int idf) {
        flujo = idf;
    }
    
    /**
     * Este método permite obtener el flujo al que pertenece el paquete cuya
     * a cuya retransmisión se refiere este mensaje.
     * @return El identificador del flujo al que pertenece el paquete solicitado.
     * @since 1.0
     */    
    public int obtenerFlujo() {
        return this.flujo;
    }
    
    /**
     * Este método permite establecer el identificador del paquete a cuya
     * retransmisión se refiere este mensaje.
     * @param idp Identificador del paquete buscado.
     * @since 1.0
     */    
    public void ponerIdPaquete(int idp) {
        this.idPaquete = idp;
    }
    
    /**
     * Este método permite obtener el identificador del paquete a cuya
     * retransmisión se refiere este mensaje.
     * @return Identificador del paquete solicitado.
     * @since 1.0
     */    
    public int obtenerIdPaquete() {
        return this.idPaquete;
    }
    
    /**
     * Este método devuelve el tamaño, en bytes, de esta instancia del payload GPSRP.
     * @return El tamaño en bytes del payload.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return 9;
    }
    
    /**
     * Establece el mensaje de un paquete GPSRP puesto que en principio el paquete es
     * GPSRP genérico y no especifica ninguna solicitud o respuesta.
     * @param m El mensaje, que será una de las constantes definidas en la clase.
     * @since 1.0
     */
    public void ponerMensaje(int m) {
        mensaje = m;
    }
    
    /** Este método nos permite conocer el valor del mensaje del payload.
     * @return El mensaje que lleva implícito el paquete.
     * @since 1.0
     */
    public int obtenerMensaje() {
        return mensaje;
    }
    
    /**
     * Esta constante se usa para solicitar la retransmisión de un paquete marcado con
     * GoS.
     * @since 1.0
     */    
    public static final int SOLICITUD_RETRANSMISION = -1;
    /**
     * Esta constante se usa para denegar la retransmisión de un paquete marcado con
     * GOS.
     * @since 1.0
     */    
    public static final int RETRANSMISION_NO = -2;
    /**
     * Esta constante se usa para conceder la retransmisión de un paquete marcado con
     * GoS.
     * @since 1.0
     */    
    public static final int RETRANSMISION_OK = -3;
    
    /** Atributo que es el mensaje concreto que lleva incorporado este paquete TLDP.
     * Será alguno de los valores constantes definidos en esta clase.
     * @since 1.0
     */
    private int mensaje;
    private int flujo;
    private int idPaquete;
}
