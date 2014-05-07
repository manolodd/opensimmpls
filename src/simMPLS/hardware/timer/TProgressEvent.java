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





