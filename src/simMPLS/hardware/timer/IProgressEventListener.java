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
