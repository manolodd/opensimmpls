/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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

/** Esta clase implementa una excepci�n que se lanzar� cuando un contador ascendente
 * llegue a su mayor valor posible.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class EIDGeneratorOverflow extends Exception {
    
    /** Es el constructor de la clase. Crea una nueva instancia de
     * EDesbordeDelidentificador.
     * @since 1.0
     */
    public EIDGeneratorOverflow() {
    }
    
    /** Devuelve una descripci�n textual de por qu� se ha producido la excepci�n.
     * @return Una cadena de texto explicando el motivo de la excepci�n.
     * @since 1.0
     */
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("EDesbordeDelIdentificador.texto"));
    }
    
}
