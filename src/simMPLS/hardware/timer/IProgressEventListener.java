//**************************************************************************
// Nombre......: IProgressEventListener.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Interfaz que deben implementar todos los objetos que se 
// ............: seen suscribir para recibir eventos TProgressEvent.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.timer;

import java.util.*;

/** Interfaz para la creaci�n de clases sean suscriptoras de eventos de progresi�n.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public interface IProgressEventListener extends EventListener {
    /** Este m�todo deber� ser implementado. Su funci�n es que los objetos generadores
     * de eventos de progresi�n a los que nos hemos suscrito puedan enviarnos los
     * eventos cuando se produzcan.
     * @param evt El evento de progresi�n que habr� emitido un objeto al que se est�
     * suscrito.
     * @since 1.0
     */    
    public void capturarEventoProgreso(TProgressEvent evt);
}
