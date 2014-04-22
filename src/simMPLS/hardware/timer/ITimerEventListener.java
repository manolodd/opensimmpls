//**************************************************************************
// Nombre......: ITimerEventListener.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Interfaz que deben implementar todos los objetos que se 
// ............: seen suscribir para recibir eventos TTimerEvent.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.timer;

import java.util.*;

/** Esta interfaz la implementar�n aquellas clases que se quieran convertir en
 * suscriptoras de eventos de reloj.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public interface ITimerEventListener extends EventListener {
    /** Este m�todo deber� ser implementado. A la clase que lo implemente le servir�
     * para que a trav�s de �l le lleguen eventos de reloj del objeto generador de los
     * mismo al que est� suscrito.
     * @param evt El evento de reloj emitido por el objeto generador al que se est� suscrito.
     * @since 1.0
     */    
    public void capturarEventoReloj(TTimerEvent evt);
}
