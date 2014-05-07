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
    public void captureSimulationEvents(TSimulationEvent evt);
}
