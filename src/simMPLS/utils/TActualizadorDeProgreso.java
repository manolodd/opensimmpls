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
public class TActualizadorDeProgreso implements IProgressEventListener {
    
    /** Este es el constructor de la clase, que permite crear instancias de
     * TActualizadorDeProgreso.
     * @since 1.0
     * @param bp Barra de progreso swing que debe actualizar este actualizador.
     */
    public TActualizadorDeProgreso(JProgressBar bp) {
        barraDeProgreso = bp;
    }
    
    /** Este m�todo captura un evento de progresi�n y seg�n lo que dicho evento indique,
     * actualiza la barra de progreso asociada de una manera u otra.
     * @since 1.0
     * @param evt El evento de progresi�n capturado.
     */
    public void capturarEventoProgreso(TProgressEvent evt) {
        barraDeProgreso.setValue(evt.obtenerPorcentaje());
    }
    
    /** Barra de progreso que debe ir actualizando el actualizador de progreso.
     * @since 1.0
     */
    private JProgressBar barraDeProgreso;
}
