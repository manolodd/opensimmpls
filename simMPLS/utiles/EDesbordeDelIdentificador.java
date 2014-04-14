package simMPLS.utiles;

/** Esta clase implementa una excepción que se lanzará cuando un contador ascendente
 * llegue a su mayor valor posible.
 * @version 1.0
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class EDesbordeDelIdentificador extends Exception {
    
    /** Es el constructor de la clase. Crea una nueva instancia de
     * EDesbordeDelidentificador.
     * @since 1.0
     */
    public EDesbordeDelIdentificador() {
    }
    
    /** Devuelve una descripción textual de por qué se ha producido la excepción.
     * @return Una cadena de texto explicando el motivo de la excepción.
     * @since 1.0
     */
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("EDesbordeDelIdentificador.texto"));
    }
    
}
