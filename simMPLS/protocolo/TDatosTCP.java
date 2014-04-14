package simMPLS.protocolo;

/** Esta clase implementa el payload de un paquete TCP. Es decir,
 * simulará que el paquete TCP lleva datos del nivel superior en su interior y no
 * está, por tanto, vacío.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TDatosTCP {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TDatosTCP.
     * @param t El tamaño que ocupará el paquete TCP, en bytes.
     * @since 1.0
     */
    public TDatosTCP(int t) {
        tamanio = 20;   // Tamaño de la cabecera TCP
        tamanio += t;
    }
    
    /** Este método nos permite obtener el valor del atributo privado de la clase
     * "tamanio".
     * @return El valor del atributo tamanio.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return tamanio;
    }
    
    /** Este método nos permite modificar el valor del atributo privado de la clase, tamanio.
     * @param t El nuevo valor del atributo privado tamanio.
     * @since 1.0
     */
    public void ponerTamanio(int t) {
        tamanio = 20;   // Tamaño de la cabecera TCP.
        tamanio += t;
    }
    
    /** Este atributo almacenará el tamaño de los datos que se supone que van sobre TCP.
     * @since 1.0
     */
    private int tamanio;
}
