//**************************************************************************
// Nombre......: ESimulationSingleSubscriber.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Excepci�n para cuando un elemento de la red recibe mas pe-
// ............: ticiones de suscripci�n de las debidas, que en este caso es
// ............: de una sola.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: manolodd@eresmas.com
// ............: http://manolodd.virtualave.net
//**************************************************************************

package simMPLS.scenario;

/**
 * Esta clase implementa una excepci�n que se utilizar� cuando un contador de
 * progreso intente a�adir un nuevo suscriptor, teniendo ya uno previo.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class ESimulationSingleSubscriber extends Exception {
    
    /** Crea una nueva instancia de EProgresoUnSoloSuscriptor.
     * @since 1.0
     */    
    public ESimulationSingleSubscriber() {
    }
    
    /** Este m�todo devuelve un texo explicativo del motivo por el que se ha producido
     * la excepci�n cuando es invocado.
     * @return Devuele una cadena de texto (String) que es un mensaje explicativo del motivo de
     * la excepci�n.
     * @since 1.0
     */    
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("ESimulacionUnSoloSuscriptor.toString"));
    }
    
}
