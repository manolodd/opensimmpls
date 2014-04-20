//**************************************************************************
// Nombre......: IEventoProgresionListener.java
// Proyecto....: Open SimMPLS
// Descripción.: Interfaz que deben implementar todos los objetos que se 
// ............: seen suscribir para recibir eventos TEventoProgresion.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.reloj;

import java.util.*;

/** Interfaz para la creación de clases sean suscriptoras de eventos de progresión.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public interface IEventoProgresionListener extends EventListener {
    /** Este método deberá ser implementado. Su función es que los objetos generadores
     * de eventos de progresión a los que nos hemos suscrito puedan enviarnos los
     * eventos cuando se produzcan.
     * @param evt El evento de progresión que habrá emitido un objeto al que se está
     * suscrito.
     * @since 1.0
     */    
    public void capturarEventoProgreso(TEventoProgresion evt);
}
