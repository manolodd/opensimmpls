package simMPLS.protocolo;

/** Esta clase implementa el payload de un paquete TLDP, es decir, mensajes para la
 * señalización mediante etiquetas.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TDatosTLDP {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TDatosLDP.
     * @since 1.0
     */
    public TDatosTLDP() {
        mensaje = this.SOLICITUD_ETIQUETA;
        ipDestinoFinal = "";
        label = 16;
        identificadorLDP = 0;
    }
    
    /** Este método devuelve el tamaño, en bytes, de esta instancia del payload TLDP.
     * @return El tamaño en bytes del payload.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return 9;
    }
    
    /** Establece el mensaje de un paquete TLDP puesto que en principio el paquete es
     * TLDP genérico y no especifica ninguna solicitud o respuesta.
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
    
    /** Este método nos permite especificar la IP del destino final del tráfico para el
     * cual estamos buscando un LSP.
     * @param ip La IP del nodo receptor final del tráfico.
     * @since 1.0
     */
    public void ponerIPDestinoFinal(String ip) {
        ipDestinoFinal = ip;
    }
    
    /** Este método nos permite conocer la IP del nodo receptor final para el cual vamos
     * a crear un LSP.
     * @return La IP del nodo destino final del tráfico.
     * @since 1.0
     */
    public String obtenerIPDestinoFinal() {
        return ipDestinoFinal;
    }
    
    /** Este método permite especificar una etiqueta en la instancia de payload TLDP que
     * estamos tratando.
     * @param e El valor de la etiqueta que deseamos transportar vía TLDP.
     * @since 1.0
     */
    public void ponerEtiqueta(int e) {
        label = e;
    }
    
    /** Este método nos permite obtener el valor de la etiqueta quer está siendo
     * transportada vía esta instancia de payload TLDP.
     * @return El valor de la etiqueta.
     * @since 1.0
     */
    public int obtenerEtiqueta() {
        return label;
    }
    
    /** Este método permite especificar dentro de los datos TLDP el identificación de
     * sesión TLDP que estamos usando.
     * @param i El identificador de la sesión TLDP.
     * @since 1.0
     */
    public void ponerIdentificadorLDP(int i) {
        identificadorLDP = i;
    }
    
    /** Este método nos permite conocer el identificador de sesión TLDP que se expresa
     * en los datos TLDP de la instancia actual.
     * @return el identificador único TLDP.
     * @since 1.0
     */
    public int obtenerIdentificadorLDP() {
        return identificadorLDP;
    }
    
    /**
     * Esta constante se usa para solicitar una etiqueta en un dominio MPLS. Asímismo,
     * este valor provoca la apertura de una sesión TLDP.
     * @since 1.0
     */    
    public static final int SOLICITUD_ETIQUETA = -33;
    /**
     * Esta constante se usa para denegar la concesión de una etiqueta en un dominio MPLS.
     * @since 1.0
     */    
    public static final int SOLICITUD_NO = -31;
    /**
     * Esta constante se usa para conceder una etiqueta en un dominio MPLS.
     * @since 1.0
     */    
    public static final int SOLICITUD_OK = -30;
    /**
     * Esta constante se usa para eliminar una etiqueta y desasociar así el LSP
     * asociado.
     * @since 1.0
     */    
    public static final int ELIMINACION_ETIQUETA = -32;
    /**
     * Esta constante se usa para confirmar la eliminación de una etiqueta y la
     * disociación del el LSP asociado.
     * @since 1.0
     */    
    public static final int ELIMINACION_OK = -34;
    
    /** Atributo que es el mensaje concreto que lleva incorporado este paquete TLDP.
     * Será alguno de los valores constantes definidos en esta clase.
     * @since 1.0
     */
    private int mensaje;
    /** Atributo que indica, dentro del paquete TLDP, cuál es el destino final al que se
     * dirige el flujo de información que se va a utilizar. De este modo cada nodo
     * puede elegir el mejor salto posible de acuerdo con el destino del tráfico.
     * @since 1.10
     */
    private String ipDestinoFinal;
    /** Este atributo indica, en lo casos que corresponda, la etiqueta afectada de una eliminación, una
     * concesión, etcétera.
     * @since 1.0
     */
    private int label;
    /** Este atributo es el identificador de una sesión LDP abierta entre dos nodos
     * adyacentes. Se usa para cuando hay que enlazar el destino con el origen a través
     * de un LSR o un LER.
     * @since 1.0
     */
    private int identificadorLDP;
}
