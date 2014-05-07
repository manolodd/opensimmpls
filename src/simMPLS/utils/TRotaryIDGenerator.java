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

/**
 * Esta clase implementa un generador de identificadores largos, c�clico (vuelve a
 * empezar). Se utiliza para identificar los paquetes con GoS de un emisor.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TRotaryIDGenerator {
    
    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TIdentificadorRotativo y lo inicia a cero.
     * @since 1.0
     */
    public TRotaryIDGenerator() {
        identificador = 0;
    }
    
    /**
     * Este m�todo coloca a cero el generador de identificadores. Como si acabase de
     * ser instanciado.
     * @since 1.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /**
     * Este m�todo obtiene un nuevo identificador del generador e incrementa su
     * contador interno para la siguiente llamada.
     * @return Un identificador nuevo y �nico (en este ciclo del generador).
     * @since 1.0
     */
    synchronized public int getNextID() {
        if (identificador > 2147483646) {
            identificador = 0;
        } else {
            identificador++;
        }
        return (identificador);
    }
    
    /**
     * Este m�todo permite establecer el valor a partir del cu�l el generador de
     * identificadores comenzar� a generar.
     * @param i El valor con el que se desea iniciar el generador.
     * @since 1.0
     */
    synchronized public void ponerIdentificador(int i) {
        identificador = i;
    }
    
    private int identificador;
}
