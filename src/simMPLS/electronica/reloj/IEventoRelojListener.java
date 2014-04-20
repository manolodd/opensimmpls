//**************************************************************************
// Nombre......: IEventoRelojListener.java
// Proyecto....: Open SimMPLS
// Descripción.: Interfaz que deben implementar todos los objetos que se 
// ............: seen suscribir para recibir eventos TEventoReloj.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.reloj;

import java.util.*;

/** Esta interfaz la implementarán aquellas clases que se quieran convertir en
 * suscriptoras de eventos de reloj.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public interface IEventoRelojListener extends EventListener {
    /** Este método deberá ser implementado. A la clase que lo implemente le servirá
     * para que a través de él le lleguen eventos de reloj del objeto generador de los
     * mismo al que está suscrito.
     * @param evt El evento de reloj emitido por el objeto generador al que se está suscrito.
     * @since 1.0
     */    
    public void capturarEventoReloj(TEventoReloj evt);
}
