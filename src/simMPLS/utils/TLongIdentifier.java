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

/** Esta es la implementaci�n de un generador de identificadores num�ricos que no se
 * repiten y que se van incrementando hasta llegar a su l�mite m�ximo.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TLongIdentifier {
    
    /** Atributo que almacena el valor interno del generador de identificadores
     * num�ticos largos.
     * @since 1.0
     */
    private long identificador;
    
    /** Crea un nuevo generador de identificadores con el valor inicial 0.
     * @since 1.0
     */
    public TLongIdentifier() {
        identificador = 0;
    }
    
    /**
     * Este m�todo reinicia el generador de identificadores a su valor original, como
     * si acabase de ser instanciado.
     * @since 1.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /** M�todo que devuelve un identificador generado por el generador. Adem�s se
     * modifica el contador interno para que el siguiente generador sea distinto. El
     * m�todo est� <B>sincronizado</B>.
     * @return Un n�mero enterio que ser� un identificador largo: un entero largo �nico.
     * @throws EIdentifierGeneratorOverflow Esta excepci�n se lanza cuando el contador interno del generador de
     * identificadores se desborda. Es alto por lo que generalmente no ocurrir�, pero
     * hay que capturar la excepci�n por si acaso.
     * @since 1.0
     */
    synchronized public long getNextID() throws EIdentifierGeneratorOverflow {
        if (identificador > 9223372036854775806L) {
            throw new EIdentifierGeneratorOverflow();
        } else {
            identificador++;
        }
        return (identificador);
    }
    
}
