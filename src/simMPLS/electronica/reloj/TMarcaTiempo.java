//**************************************************************************
// Nombre......: TMarcaTiempo.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa una marca temporar, es decir un ins-
// ............: tante concreto dentro de la simulación. Un tic de reloj 
// ............: vendrá dado por la diferencia entre dos marcas de tiempo.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.reloj;

/** Esta clase implementa una marca de tiempo, un instante, un momento temporal
 * dado. un tic de reloj vendrá dado siempre como un par de marcas de tiempo que
 * delimitrán su duración; cada marca de tiempo es de este tipo.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TMarcaTiempo {

    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TMarcaTiempo.
     * @param ms La componente de la marca de tiempo que expresa los milisegundos.
     * @param ns La componente de la marca de tiempo que expresa los nanosegundos.
     * @since 1.0
     */
    public TMarcaTiempo(long ms, int ns) {
        milisegundo = ms;
        nanosegundo = ns;
    }

    /** Este método compara la instancia actual con otra del tipo TMarcaTiempo. Es
     * requisito por implementar la interfaz comparable.
     * @param o Objeto del tipo TMarcaTiempo con el que deseamos compar la instancia actual.
     * @return MAYOR_EL_PARAMETRO, IGUALES o MENOR_EL_PARAMETRO, en función de si la instancia
     * con al que nos estamos comparando es mayor, igual o menor que la actual, de
     * acuerdo al orden elegido.
     * @since 1.0
     */    
    public int comparar(Object o) {
        TMarcaTiempo parametro = (TMarcaTiempo) o;
        if (milisegundo < parametro.obtenerMilisegundo())
            return this.MAYOR_EL_PARAMETRO;
        if (milisegundo > parametro.obtenerMilisegundo())
            return this.MENOR_EL_PARAMETRO;
        if (milisegundo == parametro.obtenerMilisegundo()) {
            if (nanosegundo < parametro.obtenerNanosegundo())
                return this.MAYOR_EL_PARAMETRO;
            if (nanosegundo > parametro.obtenerNanosegundo())
                return this.MENOR_EL_PARAMETRO;
            if (nanosegundo == parametro.obtenerNanosegundo())
                return this.IGUALES;
        }
        return this.IGUALES;
    }

    /** Este método obtiene la componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @return La componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @since 1.0
     */    
    public int obtenerNanosegundo() {
        return nanosegundo;
    }

    /** Este método permite establecer la componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @param ns La componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @since 1.0
     */    
    public void ponerNanosegundo(int ns) {
        nanosegundo = ns;
    }

    /** Este método permite sumar un número de nanosegundos determinados a la instancia
     * actual.
     * @param ns El número de nanosegundos a sumar a la instancia actual.
     * @since 1.0
     */    
    public void sumarNanosegundo(int ns) {
        nanosegundo += ns;
        long divisionEntera = (nanosegundo / 1000000);
        if (divisionEntera > 0) {
            sumarMilisegundo(divisionEntera);
            nanosegundo %= 1000000;
        }
    }

    /** Este método obtiene la componente de la marca de tiempo que expresa los
     * milisegundos.
     * @return La componente de la marca de tiempo que expresa los
     * milisegundos.
     * @since 1.0
     */    
    public long obtenerMilisegundo() {
        return milisegundo;
    }

    /** Este método permite establecer la componente de la marca de tiempo que expresa los
     * milisegundos.
     * @param ms La componente de la marca de tiempo que expresa los
     * milisegundos.
     * @since 1.0
     */    
    public void ponerMilisegundo(long ms) {
        milisegundo = ms;
    }

    /**
     * Este método configura la instancia de marca actual basándose en otra marca de
     * tiempo ya existente.
     * @param mt Marca de tiempo ya existente con cuyos valores vamos a iniciar la instancia
     * actual.
     * @since 1.0
     */    
    public void ponerMarca(TMarcaTiempo mt) {
        this.milisegundo = mt.obtenerMilisegundo();
        this.nanosegundo = mt.obtenerNanosegundo();
    }
    
    /** Este método permite sumar un número de milisegundos determinados a la instancia
     * actual.
     * @param ms El número de milisegundos a sumar a la instancia actual.
     * @since 1.0
     */    
    public void sumarMilisegundo(long ms) {
        milisegundo += ms;
    }

    /** Este método permite obtener la expresión de la marca de tiempo en nanosegundos.
     * @return la expresión de la marca de tiempo en nanosegundos.
     * @since 1.0
     */    
    public long obtenerTotalEnNanosegundos() {
        return (long) ((this.obtenerMilisegundo()*1000000) + this.obtenerNanosegundo());
    }

    /** Constante para indicar que, al comparar dos marcas, la que se compara con la
     * instancia actual es mayor.
     * @since 1.0
     */    
    public static final int MAYOR_EL_PARAMETRO = -1;
    /** Constante para indicar que, al comparar dos marcas, ambas son iguales.
     * @since 1.0
     */    
    public static final int IGUALES = 0;
    /** Constante para indicar que, al comparar dos marcas, la que se compara con la
     * instancia actual es menor.
     * @since 1.0
     */    
    public static final int MENOR_EL_PARAMETRO = 1;

    /** Este atributo almacenará la componente de la marca de tiempo que expresa los
     * milisegundos.
     * @since 1.0
     */    
    private long milisegundo;
    /** Este atributo almacenará la componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @since 1.0
     */    
    private int nanosegundo;
}
