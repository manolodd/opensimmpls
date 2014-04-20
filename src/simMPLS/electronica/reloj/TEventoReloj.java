//**************************************************************************
// Nombre......: TEventoReloj.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un evento que será difuncido por el
// ............: reloj de la topología hacia todos los elementos que esten
// ............: suscritos a ellos.
// Fecha.......: 19/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.reloj;

import java.util.*;
import simMPLS.electronica.reloj.*;    
import simMPLS.utiles.*;

/** Esta clase implementa un evento que se utilizará cuando se necesite notificar
 * que un contador de progreso ha cambiado de valor.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEventoReloj extends TEventoSimMPLS {

    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TEventoReloj.
     * @param emisor Objeto que generó el evento.
     * @param id Identificador unico del evento.
     * @param i Límite inferior del intervalo temporal que expresa este evento.
     * @param s Límite superior del intervalo temporal que expresa este evento.
     * @since 1.0
     */    
    public TEventoReloj(Object emisor, long id, TMarcaTiempo i, TMarcaTiempo s) {
        super(emisor, id, s.obtenerTotalEnNanosegundos());
        marcaInferior = i;
        marcaSuperior = s;
    }

    /** Este método devuelve la duración del tic, o sea, la diferencia existente entre
     * el límite superior y el límite inferior del intervalo expresado por el evento.
     * @return La duración del tic de reloj expresada en este evento.
     * @since 1.0
     */    
    public int obtenerDuracionTic() {
        return (int) ((marcaSuperior.obtenerTotalEnNanosegundos()) - (marcaInferior.obtenerTotalEnNanosegundos()));
    }

    /** Este método obtiene el límite superior del intervalo temporal que expresa este
     * evento.
     * @return El limite superior del intervalo de tiempo expresado por este evento.
     * @since 1.0
     */    
    public long obtenerLimiteSuperior() {
        return (long) (marcaSuperior.obtenerTotalEnNanosegundos());
    }

    /** Este método devuelve el tipo de evento de que se trata la instancia actual.
     * @return El tipo de evento de que se trata la instancia actual, según las constantes
     * definidas en TEventoSimMPLS.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return super.RELOJ;
    }

    /** Este atributo marca el extremo inferior del intervalo temporal que es lo que
     * significa este evento.
     * @since 1.0
     */    
    private TMarcaTiempo marcaInferior;
    /** Este atributo marca el extremo superior del intervalo temporal que es lo que
     * significa este evento.
     * @since 1.0
     */    
    private TMarcaTiempo marcaSuperior;
}





