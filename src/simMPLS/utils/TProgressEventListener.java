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
package simMPLS.utils;

import simMPLS.hardware.timer.IProgressEventListener;
import simMPLS.hardware.timer.TProgressEvent;
import javax.swing.*;

/**
 * Esta clase genera instancias capaces de actualizar una barra de progreso cuando
 * recibe un evento de progresi�n.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TProgressEventListener implements IProgressEventListener {
    
    /** Este es el constructor de la clase, que permite crear instancias de
     * TActualizadorDeProgreso.
     * @since 1.0
     * @param bp Barra de progreso swing que debe actualizar este actualizador.
     */
    public TProgressEventListener(JProgressBar bp) {
        barraDeProgreso = bp;
    }
    
    /** Este m�todo captura un evento de progresi�n y seg�n lo que dicho evento indique,
     * actualiza la barra de progreso asociada de una manera u otra.
     * @since 1.0
     * @param evt El evento de progresi�n capturado.
     */
    public void receiveProgressEvent(TProgressEvent evt) {
        barraDeProgreso.setValue(evt.getProgressPercentage());
    }
    
    /** Barra de progreso que debe ir actualizando el actualizador de progreso.
     * @since 1.0
     */
    private JProgressBar barraDeProgreso;
}
