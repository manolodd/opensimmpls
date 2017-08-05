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
package com.manolodominguez.opensimmpls.utils;

/** Esta es la implementaci�n de un generador de identificadores num�ricos que no se
 * repiten y que se van incrementando hasta llegar a su l�mite m�ximo.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TIDGenerator {
    
    /** Atributo que contendr� el valor interno del generador de identificadores
     * num�ticos.
     */
    private int identificador;
    
    /** Crea un nuevo generador de identificadores con el valor inicial 0.
     * @since 2.0
     */
    public TIDGenerator() {
        identificador = 0;
    }
    
    /**
     * Este m�todo reinicia el generador de identificadores a su valor original, como
     * si acabase de ser instanciado.
     * @since 2.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /** M�todo que devuelve un identificador generado por el generador. Adem�s se
     * modifica el contador interno para que el siguiente generador sea distinto. El
     * m�todo est� <B>sincronizado</B>.
     * @return Un numero entero que ser� un identificador: no estar� repetido.
     * @throws EIDGeneratorOverflow Esta excepci�n se lanza cuando el contador interno del generador de
     * identificadores se desborda. Es alto por lo que ngeneralmente no ocurrir�, pero
     * hay que capturar la excepci�n por si acaso.
     * @since 2.0
     */
    synchronized public int getNew() throws EIDGeneratorOverflow {
        if (identificador > 2147483646) {
            throw new EIDGeneratorOverflow();
        } else {
            identificador++;
        }
        return (identificador);
    }
    
    /**
     * Este m�todo establece el valor de partida del generador de identificadores,
     * siempre que el valor que deseamos sea mayor que el que ya tiene el propio
     * generador.
     * @since 2.0
     * @param i El valor de partida con el cual deseamos iniciar el contador.
     */
    synchronized public void setIDIfGreater(int i) {
        if (i > identificador)
            identificador = i;
    }
    
    /**
     * Este m�todo permite poner el valor de partida del generador de identificadores.
     * @since 2.0
     * @param i El valor que deseamos poner como valor de partida para el generador.
     */
    synchronized public void ponerIdentificador(int i) {
        identificador = i;
    }
}
