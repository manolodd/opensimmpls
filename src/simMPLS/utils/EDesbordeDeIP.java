package simMPLS.utils;

/** Esta clase implementa una excepci�n que se usar� cuando se hayan usado todas las
 * direcciones IP que es capaz de generar el generador autom�tico de direcciones
 * IP.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class EDesbordeDeIP extends Exception {
    
    /** Este es el constructor de la clase. Crea una nueva instancia de EDesbordeDeIP.
     * @since 1.0
     */
    public EDesbordeDeIP() {
    }
    
    /** Devuelve una cadena de texto explicando el motivo de la excepci�n.
     * @return Descripci�n textual del error que ha ocurrido
     * @since 1.0
     */
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("GeneradorIP.LlegoAlLimite"));
    }
}
