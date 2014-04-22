package simMPLS.protocols;

/** Esta clase implementa un paquete IPv4, con sus campos accesibles.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPDUIPv4 extends TPDU {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de TPDUIPv4
     * con los par�metros especificados.
     * @param id El identificador unico de cada paquete generado.
     * @param ipo La direcci�n IP origen del paquete.
     * @param ipd La direcci�n IP destino el paquete.
     * @param tamDatos El tama�o de los datos el paquete, en bytes.
     * @since 1.0
     */
    public TPDUIPv4(long id, String ipo, String ipd, int tamDatos) {
        super(id, ipo, ipd);
        datos = new TDatosTCP(tamDatos);
        subtipo = super.IPV4;
    }
    
    /** Este m�todo devuelve el tama�o del paquete, tanto de la carga �til como del
     * overhead, los campos,e tcetera. En bytes.
     * @return El tama�o completo del paquete en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return (super.obtenerCabecera().obtenerTamanio() + datos.obtenerTamanio());
    }
    
    /** Este m�todo devuelve la constante IPV4, indicando que se trata de un paquete
     * IPv4.
     * @return Devuelve la constante IPV4, indicando que se trata de un paquete IPv4.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.IPV4;
    }
    
    /** Este m�todo nos permite acceder directamente a la carga �til del paquete y poder
     * as� acceder directamente a sus m�todos.
     * @return La carga �til del paquete IPv4.
     * @since 1.0
     */
    public TDatosTCP obtenerDatos() {
        return datos;
    }
    
    /** Este m�todo nos permite cambiar la carga �til del paquete IPv4 de forma r�pida y
     * sencilla.
     * @param d La nueva carga �til (datos) que queremos para el paquete IPv4.
     * @since 1.0
     */
    public void ponerDatos(TDatosTCP d) {
        datos = d;
    }
    
    /** Este m�todo nos devuelve la cabecera IPv4 del paquete para poder as� acceder a sus m�todos
     * de forma directa.
     * @return La cabecera IPv4 del paquete.
     * @since 1.0
     */
    public TCabeceraIPv4 obtenerCabecera() {
        return super.obtenerCabecera();
    }
    
    /**
     * Este m�todo permite obtener el subtipo de paquete IPv4, es decir, si el paquete
     * est� o no marcado con GoS.
     * @return El subtipo del paquete IPv4. Una de las cosntantes definidas en la clase TPDU.
     * @since 1.0
     */
    public int obtenerSubTipo() {
        return this.subtipo;
    }
    
    /**
     * Este m�todo poermite establecer el subtipo de paquete IPv4, es decir, si el
     * paquete est� o no marcado con GoS.
     * @param st El subtipo del paquete. Una de las constantes definidas en la clase TPDU.
     * @since 1.0
     */
    public void ponerSubtipo(int st) {
        subtipo = st;
    }
    
    /** Este atributo simula la carga del paquete IPv4. Es utilizado para simular
     * paquetes con distinto tama�o.
     * @since 1.0
     */
    private TDatosTCP datos;
    /**
     * Este atributo almacenar� el subtipo de paquete IPv4 de que se trata.
     * @since 1.0
     */
    private int subtipo;
}
