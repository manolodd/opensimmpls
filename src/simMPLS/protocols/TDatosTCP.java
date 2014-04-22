package simMPLS.protocols;

/** Esta clase implementa el payload de un paquete TCP. Es decir,
 * simular� que el paquete TCP lleva datos del nivel superior en su interior y no
 * est�, por tanto, vac�o.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TDatosTCP {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TDatosTCP.
     * @param t El tama�o que ocupar� el paquete TCP, en bytes.
     * @since 1.0
     */
    public TDatosTCP(int t) {
        tamanio = 20;   // Tama�o de la cabecera TCP
        tamanio += t;
    }
    
    /** Este m�todo nos permite obtener el valor del atributo privado de la clase
     * "tamanio".
     * @return El valor del atributo tamanio.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return tamanio;
    }
    
    /** Este m�todo nos permite modificar el valor del atributo privado de la clase, tamanio.
     * @param t El nuevo valor del atributo privado tamanio.
     * @since 1.0
     */
    public void ponerTamanio(int t) {
        tamanio = 20;   // Tama�o de la cabecera TCP.
        tamanio += t;
    }
    
    /** Este atributo almacenar� el tama�o de los datos que se supone que van sobre TCP.
     * @since 1.0
     */
    private int tamanio;
}
