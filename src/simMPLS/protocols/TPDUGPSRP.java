package simMPLS.protocols;

/** Esta clase implementa un paquete GPSRP (GoS PDU Store and Retransmit Protocol).
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPDUGPSRP extends TPDU {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de un
     * paquete GPSRP bas�ndose en lo par�metros pasados.
     * @param id identificador �nico del paquete.
     * @param ipo Direcci�n IP origen del paquete.
     * @param ipd Direcci�n IP destino del paquete.
     * @since 1.0
     */
    public TPDUGPSRP(long id, String ipo, String ipd) {
        super(id, ipo, ipd);
        datosTCP = new TDatosTCP(0);
        datosGPSRP = new TDatosGPSRP();
    }
    
    /** Este m�todo devuelve el tama�o completo del paquete en bytes, para poder
     * realizar c�lculos en la simulaci�n.
     * @return El tama�o completo del paquete en bytes.
     * @since 1.0
     */
    public int getSize() {
        int tam = 0;
        tam += super.getHeader().obtenerTamanio(); // Cabecera IPv4
        tam += this.datosTCP.obtenerTamanio(); // Cabecera TCP
        tam += this.datosGPSRP.obtenerTamanio(); // Tamanio mensaje GPSRP
        return (tam);
    }
    
    /** Este m�todo devuelve la constante GPSRP, indicando que el paquete es de tipo
     * GPSRP.
     * @return La constante GPSRP.
     * @since 1.0
     */
    public int getType() {
        return super.GPSRP;
    }
    
    /** Este m�todo nos permite el acceso a los datos TCP de este paquete, para poder
     * acceder a sus m�todos de forma directa.
     * @return Los datos TCP de esta instancia.
     * @since 1.0
     */
    public TDatosTCP obtenerDatosTCP() {
        return datosTCP;
    }
    
    /** Este m�todo nos permite acceder a los datos GPSRP del paquete para hacer uso
     * directamente de sus m�todos.
     * @return Los datos GPSRP de esta instancia.
     * @since 1.0
     */
    public TDatosGPSRP obtenerDatosGPSRP() {
        return datosGPSRP;
    }
    
    /** Este m�todo nos permite acceder a la cabecera IPv4 del paquete y poder hacer uso
     * de sus m�todos de forma directa.
     * @return La cabecera IP del paquete.
     * @since 1.0
     */
    public TCabeceraIPv4 getHeader() {
        return super.getHeader();
    }
    
    /**
     * Este m�todo permite obtener el subtipo del paquete GPSRP. En esta versi�n el
     * paquete GPSRP no tiene subtipos. Implementa este m�todo para dejar de ser una
     * clase abstracta.
     * @return TPDU.TLDP
     * @since 1.0
     */
    public int getSubtype() {
        return super.GPSRP;
    }
    
    /**
     * Este m�todo no hace nada. Existe porque es necesario implementarlo por ser
     * abstracto en una clase superior.
     * @param st No utilizado.
     * @since 1.0
     */
    public void ponerSubtipo(int st) {
        // No se hace nada
    }
    
    /** Este atributo privado simula la carga aportada por el tama�o de los datos TCP
     * al paquete.
     * @since 1.0
     */
    private TDatosTCP datosTCP;
    /** Este atributo privado simula los datos del paquete GPSRP, de donde se puede
     * obtener los mensajes de retransmisi�n necesarios.
     * @since 1.0
     */
    private TDatosGPSRP datosGPSRP;
}
