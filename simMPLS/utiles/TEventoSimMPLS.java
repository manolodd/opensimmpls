package simMPLS.utiles;

import java.util.*;

/** Esta clase es la superclase de la que partiran todos los eventos del simulador
 * openSimMPLS. Está creada para permitir el polimorfismo.
 * @version 1.0
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public abstract class TEventoSimMPLS extends EventObject implements Comparable {
    
    /**
     * Este el el constructor de la clase, que permitirá crear instancias de tipo
     * TEventoSimMPLS. En realidad no se puede porque es una clase abstracta, pero debe
     * ser inicializada por las superclases y por ello se proporciona este constructor.
     * @since 1.0
     * @param inst Instante de tiempo. Todos los eventos de Open SimMPLS están marcados con el
     * instante en que se han generado.
     * @param emisor El objeto que genera el evento.
     * @param id El identificador único del evento, para que el recolector de eventos pueda
     * ordenarlo.
     */
    public TEventoSimMPLS(Object emisor, long id, long inst) {
        super(emisor);
        identificador = id;
        instante = inst;
    }
    
    /**
     * Este método permite obtener el instante de tiempo en que se generó el evento.
     * @return El instante de tiempo en que se generó el evento.
     * @since 1.0
     */
    public long obtenerInstante() {
        return this.instante;
    }
    
    /** Este método obtiene el identificador del evento.
     * @return El identificador del evento.
     * @since 1.0
     */
    public long obtenerIdentificador() {
        return identificador;
    }
    
    /** Este método permite cambiar el identificador del objeto colocado en el momento
     * de su instanciación.
     * @since 1.0
     * @param id El nuevo identificador del objeto.
     */
    public void ponerIdentificador(long id) {
        identificador = id;
    }
    
    /** Este método compara este evento con otro de la misma clase para determinar el
     * orden de cada uno y poder insertarlo de forma correcta en un arbol binario
     * ordenado.
     * @param o El otro evento con el que se compara.
     * @return -1, 0 ó 1, dependiendo de si los eventos son distintos, mayor o menor en cuanto
     * a orden.
     * @since 1.0
     */
    public int compareTo(Object o) {
        TEventoSimMPLS e = (TEventoSimMPLS) o;
        if (this.instante < e.obtenerInstante())
            return -1;
        else if (this.instante > e.obtenerInstante())
            return 1;
        else {
            if (obtenerIdentificador() < e.obtenerIdentificador())
                return -1;
            else if (obtenerIdentificador() == e.obtenerIdentificador()) {
                return 0;
            }
            return 1;
        }
    }
    
    /** Este método obtiene el tipo del evento, que será una de las constantes definidas
     * en esta clase.
     * @return La constante que indica de qué tipo es el evento.
     * @since 1.0
     */
    public abstract int obtenerTipo();
    
    /** Constante que indica que el evento es de tipo ESTADISTICA.
     * @since 1.0
     */
    public static final int ESTADISTICA = 0;
    /** Constante que indica que el evento es de tipo SIMULACION.
     * @since 1.0
     */
    public static final int SIMULACION = 1;
    /** Constante que indica que el evento es de tipo RELOJ.
     * @since 1.0
     */
    public static final int RELOJ = 2;
    /** Constante que indica que el evento es de tipo PROGRESION.
     * @since 1.0
     */
    public static final int PROGRESION = 3;
    
    /** Atributo que contiene el identificador del evento.
     * @since 1.0
     */
    private long identificador;
    private long instante;
}
