package simMPLS.utils;

/** Esta clase implementa una excepci�n que se lanzar� cuando un contador ascendente
 * llegue a su mayor valor posible.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class EDesbordeDelIdentificador extends Exception {
    
    /** Es el constructor de la clase. Crea una nueva instancia de
     * EDesbordeDelidentificador.
     * @since 1.0
     */
    public EDesbordeDelIdentificador() {
    }
    
    /** Devuelve una descripci�n textual de por qu� se ha producido la excepci�n.
     * @return Una cadena de texto explicando el motivo de la excepci�n.
     * @since 1.0
     */
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("EDesbordeDelIdentificador.texto"));
    }
    
}
