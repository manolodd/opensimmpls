/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @since 2.0
     * @param bp Barra de progreso swing que debe actualizar este actualizador.
     */
    public TProgressEventListener(JProgressBar bp) {
        barraDeProgreso = bp;
    }
    
    /** Este m�todo captura un evento de progresi�n y seg�n lo que dicho evento indique,
     * actualiza la barra de progreso asociada de una manera u otra.
     * @since 2.0
     * @param evt El evento de progresi�n capturado.
     */
    public void receiveProgressEvent(TProgressEvent evt) {
        barraDeProgreso.setValue(evt.getProgressPercentage());
    }
    
    /** Barra de progreso que debe ir actualizando el actualizador de progreso.
     * @since 2.0
     */
    private JProgressBar barraDeProgreso;
}
