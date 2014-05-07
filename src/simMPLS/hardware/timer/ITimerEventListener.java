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
