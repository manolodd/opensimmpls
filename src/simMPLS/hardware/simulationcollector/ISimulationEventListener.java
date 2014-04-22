//**************************************************************************
// Nombre......: ISimulationEventListener.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Interfaz que deben implementar todos los objetos que se 
// ............: seen suscribir para recibir eventos TSimulationEvent.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.simulationcollector;

import simMPLS.scenario.TSimulationEvent;
import java.util.*;

/** Esta interfaz generar� clases que ser�n suscriptores de eventos de simulaci�n.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public interface ISimulationEventListener extends EventListener {
    /** Este m�todo es utilizado por el objeto al que estamos suscrito para enviarnos
     * eventos de simulaci�n cuando sea necesario.
     * @param evt Evento de simulacion que habr� emitido un objeto al que se est�
     * suscrito.
     * @since 1.0
     */    
    public void capturarEventoSimulacion(TSimulationEvent evt);
}
