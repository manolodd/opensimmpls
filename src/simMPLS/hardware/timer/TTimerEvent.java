/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.hardware.timer;

import simMPLS.utils.TEventoSimMPLS;
import java.util.*;

/** Esta clase implementa un evento que se utilizar� cuando se necesite notificar
 * que un contador de progreso ha cambiado de valor.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TTimerEvent extends TEventoSimMPLS {

    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TEventoReloj.
     * @param emisor Objeto que gener� el evento.
     * @param id Identificador unico del evento.
     * @param i L�mite inferior del intervalo temporal que expresa este evento.
     * @param s L�mite superior del intervalo temporal que expresa este evento.
     * @since 1.0
     */    
    public TTimerEvent(Object emisor, long id, TTimestamp i, TTimestamp s) {
        super(emisor, id, s.obtenerTotalEnNanosegundos());
        marcaInferior = i;
        marcaSuperior = s;
    }

    /** Este m�todo devuelve la duraci�n del tic, o sea, la diferencia existente entre
     * el l�mite superior y el l�mite inferior del intervalo expresado por el evento.
     * @return La duraci�n del tic de reloj expresada en este evento.
     * @since 1.0
     */    
    public int obtenerDuracionTic() {
        return (int) ((marcaSuperior.obtenerTotalEnNanosegundos()) - (marcaInferior.obtenerTotalEnNanosegundos()));
    }

    /** Este m�todo obtiene el l�mite superior del intervalo temporal que expresa este
     * evento.
     * @return El limite superior del intervalo de tiempo expresado por este evento.
     * @since 1.0
     */    
    public long obtenerLimiteSuperior() {
        return (long) (marcaSuperior.obtenerTotalEnNanosegundos());
    }

    /** Este m�todo devuelve el tipo de evento de que se trata la instancia actual.
     * @return El tipo de evento de que se trata la instancia actual, seg�n las constantes
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
    private TTimestamp marcaInferior;
    /** Este atributo marca el extremo superior del intervalo temporal que es lo que
     * significa este evento.
     * @since 1.0
     */    
    private TTimestamp marcaSuperior;
}





