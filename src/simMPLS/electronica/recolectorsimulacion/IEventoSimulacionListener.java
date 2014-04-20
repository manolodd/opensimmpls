//**************************************************************************
// Nombre......: IEventoSimulacionListener.java
// Proyecto....: Open SimMPLS
// Descripción.: Interfaz que deben implementar todos los objetos que se 
// ............: seen suscribir para recibir eventos TEventoSimulacion.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.recolectorsimulacion;

import java.util.*;
import simMPLS.escenario.*;

/** Esta interfaz generará clases que serán suscriptores de eventos de simulación.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public interface IEventoSimulacionListener extends EventListener {
    /** Este método es utilizado por el objeto al que estamos suscrito para enviarnos
     * eventos de simulación cuando sea necesario.
     * @param evt Evento de simulacion que habrá emitido un objeto al que se está
     * suscrito.
     * @since 1.0
     */    
    public void capturarEventoSimulacion(TEventoSimulacion evt);
}
