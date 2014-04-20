//**************************************************************************
// Nombre......: EProgresoUnSoloSuscriptor.java
// Proyecto....: Open SimMPLS
// Descripción.: Excepción para cuando un TContadorDeProgreso recibe mas pe-
// ............: ticiones de suscripción de las debidas, que en este caso es
// ............: de una sola.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.reloj;

/** Esta clase implementa una excepción que se utilizará cuando un contador de
 * progreso intente añadir un nuevo suscriptor, teniendo ya uno previamente
 * suscrito.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class EProgresoUnSoloSuscriptor extends Exception {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * EProgresoUnSoloSuscriptor.
     * @since 1.0
     */    
    public EProgresoUnSoloSuscriptor() {
    }
    
    /** Este método devuelve un texo explicativo del motivo por el que se ha producido
     * la excepción.
     * @return Una cadena de texto que es un mensaje explicativo del motivo de
     * la excepción.
     */    
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("EProgresoUnSoloSuscriptor.texto"));
    }
    
}
