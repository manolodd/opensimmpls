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
     * @since 2.0
     */
    public TRotaryIDGenerator() {
        identificador = 0;
    }
    
    /**
     * Este m�todo coloca a cero el generador de identificadores. Como si acabase de
     * ser instanciado.
     * @since 2.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /**
     * Este m�todo obtiene un nuevo identificador del generador e incrementa su
     * contador interno para la siguiente llamada.
     * @return Un identificador nuevo y �nico (en este ciclo del generador).
     * @since 2.0
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
     * @since 2.0
     */
    synchronized public void ponerIdentificador(int i) {
        identificador = i;
    }
    
    private int identificador;
}
