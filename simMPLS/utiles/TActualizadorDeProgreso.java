package simMPLS.utiles;

import simMPLS.electronica.reloj.*;
import javax.swing.*;

/**
 * Esta clase genera instancias capaces de actualizar una barra de progreso cuando
 * recibe un evento de progresión.
 * @version 1.0
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TActualizadorDeProgreso implements IEventoProgresionListener {
    
    /** Este es el constructor de la clase, que permite crear instancias de
     * TActualizadorDeProgreso.
     * @since 1.0
     * @param bp Barra de progreso swing que debe actualizar este actualizador.
     */
    public TActualizadorDeProgreso(JProgressBar bp) {
        barraDeProgreso = bp;
    }
    
    /** Este método captura un evento de progresión y según lo que dicho evento indique,
     * actualiza la barra de progreso asociada de una manera u otra.
     * @since 1.0
     * @param evt El evento de progresión capturado.
     */
    public void capturarEventoProgreso(TEventoProgresion evt) {
        barraDeProgreso.setValue(evt.obtenerPorcentaje());
    }
    
    /** Barra de progreso que debe ir actualizando el actualizador de progreso.
     * @since 1.0
     */
    private JProgressBar barraDeProgreso;
}
