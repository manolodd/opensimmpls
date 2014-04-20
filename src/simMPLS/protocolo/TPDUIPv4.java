package simMPLS.protocolo;

/** Esta clase implementa un paquete IPv4, con sus campos accesibles.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPDUIPv4 extends TPDU {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de TPDUIPv4
     * con los parámetros especificados.
     * @param id El identificador unico de cada paquete generado.
     * @param ipo La dirección IP origen del paquete.
     * @param ipd La dirección IP destino el paquete.
     * @param tamDatos El tamaño de los datos el paquete, en bytes.
     * @since 1.0
     */
    public TPDUIPv4(long id, String ipo, String ipd, int tamDatos) {
        super(id, ipo, ipd);
        datos = new TDatosTCP(tamDatos);
        subtipo = super.IPV4;
    }
    
    /** Este método devuelve el tamaño del paquete, tanto de la carga útil como del
     * overhead, los campos,e tcetera. En bytes.
     * @return El tamaño completo del paquete en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return (super.obtenerCabecera().obtenerTamanio() + datos.obtenerTamanio());
    }
    
    /** Este método devuelve la constante IPV4, indicando que se trata de un paquete
     * IPv4.
     * @return Devuelve la constante IPV4, indicando que se trata de un paquete IPv4.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.IPV4;
    }
    
    /** Este método nos permite acceder directamente a la carga útil del paquete y poder
     * así acceder directamente a sus métodos.
     * @return La carga útil del paquete IPv4.
     * @since 1.0
     */
    public TDatosTCP obtenerDatos() {
        return datos;
    }
    
    /** Este método nos permite cambiar la carga útil del paquete IPv4 de forma rápida y
     * sencilla.
     * @param d La nueva carga útil (datos) que queremos para el paquete IPv4.
     * @since 1.0
     */
    public void ponerDatos(TDatosTCP d) {
        datos = d;
    }
    
    /** Este método nos devuelve la cabecera IPv4 del paquete para poder así acceder a sus métodos
     * de forma directa.
     * @return La cabecera IPv4 del paquete.
     * @since 1.0
     */
    public TCabeceraIPv4 obtenerCabecera() {
        return super.obtenerCabecera();
    }
    
    /**
     * Este método permite obtener el subtipo de paquete IPv4, es decir, si el paquete
     * está o no marcado con GoS.
     * @return El subtipo del paquete IPv4. Una de las cosntantes definidas en la clase TPDU.
     * @since 1.0
     */
    public int obtenerSubTipo() {
        return this.subtipo;
    }
    
    /**
     * Este método poermite establecer el subtipo de paquete IPv4, es decir, si el
     * paquete está o no marcado con GoS.
     * @param st El subtipo del paquete. Una de las constantes definidas en la clase TPDU.
     * @since 1.0
     */
    public void ponerSubtipo(int st) {
        subtipo = st;
    }
    
    /** Este atributo simula la carga del paquete IPv4. Es utilizado para simular
     * paquetes con distinto tamaño.
     * @since 1.0
     */
    private TDatosTCP datos;
    /**
     * Este atributo almacenará el subtipo de paquete IPv4 de que se trata.
     * @since 1.0
     */
    private int subtipo;
}
