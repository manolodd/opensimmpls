//**************************************************************************
// Nombre......: EProgressSingleSubscriber.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Excepci�n para cuando un TContadorDeProgreso recibe mas pe-
// ............: ticiones de suscripci�n de las debidas, que en este caso es
// ............: de una sola.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.timer;

/** Esta clase implementa una excepci�n que se utilizar� cuando un contador de
 * progreso intente a�adir un nuevo suscriptor, teniendo ya uno previamente
 * suscrito.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class EProgressSingleSubscriber extends Exception {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * EProgresoUnSoloSuscriptor.
     * @since 1.0
     */    
    public EProgressSingleSubscriber() {
    }
    
    /** Este m�todo devuelve un texo explicativo del motivo por el que se ha producido
     * la excepci�n.
     * @return Una cadena de texto que es un mensaje explicativo del motivo de
     * la excepci�n.
     */    
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("EProgresoUnSoloSuscriptor.texto"));
    }
    
}
