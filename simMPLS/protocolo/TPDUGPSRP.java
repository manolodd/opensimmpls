package simMPLS.protocolo;

/** Esta clase implementa un paquete GPSRP (GoS PDU Store and Retransmit Protocol).
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPDUGPSRP extends TPDU {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de un
     * paquete GPSRP basándose en lo parámetros pasados.
     * @param id identificador único del paquete.
     * @param ipo Dirección IP origen del paquete.
     * @param ipd Dirección IP destino del paquete.
     * @since 1.0
     */
    public TPDUGPSRP(long id, String ipo, String ipd) {
        super(id, ipo, ipd);
        datosTCP = new TDatosTCP(0);
        datosGPSRP = new TDatosGPSRP();
    }
    
    /** Este método devuelve el tamaño completo del paquete en bytes, para poder
     * realizar cálculos en la simulación.
     * @return El tamaño completo del paquete en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        int tam = 0;
        tam += super.obtenerCabecera().obtenerTamanio(); // Cabecera IPv4
        tam += this.datosTCP.obtenerTamanio(); // Cabecera TCP
        tam += this.datosGPSRP.obtenerTamanio(); // Tamanio mensaje GPSRP
        return (tam);
    }
    
    /** Este método devuelve la constante GPSRP, indicando que el paquete es de tipo
     * GPSRP.
     * @return La constante GPSRP.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.GPSRP;
    }
    
    /** Este método nos permite el acceso a los datos TCP de este paquete, para poder
     * acceder a sus métodos de forma directa.
     * @return Los datos TCP de esta instancia.
     * @since 1.0
     */
    public TDatosTCP obtenerDatosTCP() {
        return datosTCP;
    }
    
    /** Este método nos permite acceder a los datos GPSRP del paquete para hacer uso
     * directamente de sus métodos.
     * @return Los datos GPSRP de esta instancia.
     * @since 1.0
     */
    public TDatosGPSRP obtenerDatosGPSRP() {
        return datosGPSRP;
    }
    
    /** Este método nos permite acceder a la cabecera IPv4 del paquete y poder hacer uso
     * de sus métodos de forma directa.
     * @return La cabecera IP del paquete.
     * @since 1.0
     */
    public TCabeceraIPv4 obtenerCabecera() {
        return super.obtenerCabecera();
    }
    
    /**
     * Este método permite obtener el subtipo del paquete GPSRP. En esta versión el
     * paquete GPSRP no tiene subtipos. Implementa este método para dejar de ser una
     * clase abstracta.
     * @return TPDU.TLDP
     * @since 1.0
     */
    public int obtenerSubTipo() {
        return super.GPSRP;
    }
    
    /**
     * Este método no hace nada. Existe porque es necesario implementarlo por ser
     * abstracto en una clase superior.
     * @param st No utilizado.
     * @since 1.0
     */
    public void ponerSubtipo(int st) {
        // No se hace nada
    }
    
    /** Este atributo privado simula la carga aportada por el tamaño de los datos TCP
     * al paquete.
     * @since 1.0
     */
    private TDatosTCP datosTCP;
    /** Este atributo privado simula los datos del paquete GPSRP, de donde se puede
     * obtener los mensajes de retransmisión necesarios.
     * @since 1.0
     */
    private TDatosGPSRP datosGPSRP;
}
