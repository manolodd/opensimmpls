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

/** Esta clase implementa una marca de tiempo, un instante, un momento temporal
 * dado. un tic de reloj vendr� dado siempre como un par de marcas de tiempo que
 * delimitr�n su duraci�n; cada marca de tiempo es de este tipo.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TTimestamp {

    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TMarcaTiempo.
     * @param ms La componente de la marca de tiempo que expresa los milisegundos.
     * @param ns La componente de la marca de tiempo que expresa los nanosegundos.
     * @since 1.0
     */
    public TTimestamp(long ms, int ns) {
        milisegundo = ms;
        nanosegundo = ns;
    }

    /** Este m�todo compara la instancia actual con otra del tipo TTimestamp. Es
     * requisito por implementar la interfaz comparable.
     * @param anotherTimestamp Objeto del tipo TTimestamp con el que deseamos compar la instancia actual.
     * @return ARGUMENT_IS_GREATER, BOTH_ARE_EQUAL anotherTimestamp ARGUMENT_IS_LOWER, en funci�n de si la instancia
 con al que nos estamos comparando es mayor, igual anotherTimestamp menor que la actual, de
 acuerdo al orden elegido.
     * @since 1.0
     */    
    public int comparar(Object anotherTimestamp) {
        TTimestamp argument = (TTimestamp) anotherTimestamp;
        if (milisegundo < argument.getMillisecond())
            return this.ARGUMENT_IS_GREATER;
        if (milisegundo > argument.getMillisecond())
            return this.ARGUMENT_IS_LOWER;
        if (milisegundo == argument.getMillisecond()) {
            if (nanosegundo < argument.getNanosecond())
                return this.ARGUMENT_IS_GREATER;
            if (nanosegundo > argument.getNanosecond())
                return this.ARGUMENT_IS_LOWER;
            if (nanosegundo == argument.getNanosecond())
                return this.BOTH_ARE_EQUAL;
        }
        return this.BOTH_ARE_EQUAL;
    }

    /** Este m�todo obtiene la componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @return La componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @since 1.0
     */    
    public int getNanosecond() {
        return nanosegundo;
    }

    /** Este m�todo permite establecer la componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @param ns La componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @since 1.0
     */    
    public void ponerNanosegundo(int ns) {
        nanosegundo = ns;
    }

    /** Este m�todo permite sumar un n�mero de nanosegundos determinados a la instancia
     * actual.
     * @param ns El n�mero de nanosegundos a sumar a la instancia actual.
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

    /** Este m�todo obtiene la componente de la marca de tiempo que expresa los
     * milisegundos.
     * @return La componente de la marca de tiempo que expresa los
     * milisegundos.
     * @since 1.0
     */    
    public long getMillisecond() {
        return milisegundo;
    }

    /** Este m�todo permite establecer la componente de la marca de tiempo que expresa los
     * milisegundos.
     * @param ms La componente de la marca de tiempo que expresa los
     * milisegundos.
     * @since 1.0
     */    
    public void ponerMilisegundo(long ms) {
        milisegundo = ms;
    }

    /**
     * Este m�todo configura la instancia de marca actual bas�ndose en otra marca de
     * tiempo ya existente.
     * @param mt Marca de tiempo ya existente con cuyos valores vamos a iniciar la instancia
     * actual.
     * @since 1.0
     */    
    public void ponerMarca(TTimestamp mt) {
        this.milisegundo = mt.getMillisecond();
        this.nanosegundo = mt.getNanosecond();
    }
    
    /** Este m�todo permite sumar un n�mero de milisegundos determinados a la instancia
     * actual.
     * @param ms El n�mero de milisegundos a sumar a la instancia actual.
     * @since 1.0
     */    
    public void sumarMilisegundo(long ms) {
        milisegundo += ms;
    }

    /** Este m�todo permite obtener la expresi�n de la marca de tiempo en nanosegundos.
     * @return la expresi�n de la marca de tiempo en nanosegundos.
     * @since 1.0
     */    
    public long getNanoseconds() {
        return (long) ((this.getMillisecond()*1000000) + this.getNanosecond());
    }

    /** Constante para indicar que, al comparar dos marcas, la que se compara con la
     * instancia actual es mayor.
     * @since 1.0
     */    
    public static final int ARGUMENT_IS_GREATER = -1;
    /** Constante para indicar que, al comparar dos marcas, ambas son iguales.
     * @since 1.0
     */    
    public static final int BOTH_ARE_EQUAL = 0;
    /** Constante para indicar que, al comparar dos marcas, la que se compara con la
     * instancia actual es menor.
     * @since 1.0
     */    
    public static final int ARGUMENT_IS_LOWER = 1;

    /** Este atributo almacenar� la componente de la marca de tiempo que expresa los
     * milisegundos.
     * @since 1.0
     */    
    private long milisegundo;
    /** Este atributo almacenar� la componente de la marca de tiempo que expresa los
     * nanosegundos.
     * @since 1.0
     */    
    private int nanosegundo;
}
