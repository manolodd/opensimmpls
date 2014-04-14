//**************************************************************************
// Nombre......: TEventoProgresion.java
// Proyecto....: Open SimMPLS
// Descripción.: Implementación de un evento para indicar el grado de pro-
// ............: gresión, generalmente de tareas. Está indicado principal-
// ............: mente para hacer llegar el nuevo valor a las barras de pro-
// ............: greso en interfaces Swing.
// Fecha.......: 27/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.reloj;

import java.util.*;
import simMPLS.utiles.*;

/** Esta clase implementa un evento que se utilizará cuando se necesite notificar
 * que un contador de progreso ha cambiado de valor.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEventoProgresion extends TEventoSimMPLS {

    /** Este atributo contendrá el porcentaje de progreso indicado por el contador de
     * progreso que emitió este evento de progreso.
     * @since 1.0
     */    
    private int porcentaje;
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TEventoprogresion.
     * @param id El identificador único del evento.
     * @param emisor Objeto que ha generado el evento.
     * @param porcentaje Porcentaje de progresión que se desea que porte el evento hasta llegar al
     * suscriptor que lo debe recibir.
     * @since 1.0
     */    
    public TEventoProgresion(Object emisor, long id, int porcentaje) {
        super(emisor, id, 0);
        this.porcentaje=porcentaje;
    }

    /** Este método devuelve el valor interno del porcentaje de progresión que lleva
     * incorporado el evento.
     * @return El valor del porcentaje de progreso que debe ser leido por el suscriptor.
     * @since 1.0
     */    
    public int obtenerPorcentaje() {
        return this.porcentaje;
    }

    /** Este método devuelve el tipo de evento de que se trata la instancia actual. Es
     * una de las constantes descritas en TEventoSimMPLS.
     * @return El tipo de evento de que se trata. Una de las constantes descritas en
     * TEventoSimMPLS.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return super.PROGRESION;
    }

}





