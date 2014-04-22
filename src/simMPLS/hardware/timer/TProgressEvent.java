//**************************************************************************
// Nombre......: TProgressEvent.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Implementaci�n de un evento para indicar el grado de pro-
// ............: gresi�n, generalmente de tareas. Est� indicado principal-
// ............: mente para hacer llegar el nuevo valor a las barras de pro-
// ............: greso en interfaces Swing.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.timer;

import simMPLS.utils.TEventoSimMPLS;
import java.util.*;

/** Esta clase implementa un evento que se utilizar� cuando se necesite notificar
 * que un contador de progreso ha cambiado de valor.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TProgressEvent extends TEventoSimMPLS {

    /** Este atributo contendr� el porcentaje de progreso indicado por el contador de
     * progreso que emiti� este evento de progreso.
     * @since 1.0
     */    
    private int porcentaje;
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TEventoprogresion.
     * @param id El identificador �nico del evento.
     * @param emisor Objeto que ha generado el evento.
     * @param porcentaje Porcentaje de progresi�n que se desea que porte el evento hasta llegar al
     * suscriptor que lo debe recibir.
     * @since 1.0
     */    
    public TProgressEvent(Object emisor, long id, int porcentaje) {
        super(emisor, id, 0);
        this.porcentaje=porcentaje;
    }

    /** Este m�todo devuelve el valor interno del porcentaje de progresi�n que lleva
     * incorporado el evento.
     * @return El valor del porcentaje de progreso que debe ser leido por el suscriptor.
     * @since 1.0
     */    
    public int obtenerPorcentaje() {
        return this.porcentaje;
    }

    /** Este m�todo devuelve el tipo de evento de que se trata la instancia actual. Es
     * una de las constantes descritas en TEventoSimMPLS.
     * @return El tipo de evento de que se trata. Una de las constantes descritas en
     * TEventoSimMPLS.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return super.PROGRESION;
    }

}





