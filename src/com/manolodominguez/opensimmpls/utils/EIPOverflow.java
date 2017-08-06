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

/** Esta clase implementa una excepci�n que se usar� cuando se hayan usado todas las
 * direcciones IP que es capaz de generar el generador autom�tico de direcciones
 * IP.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class EIPOverflow extends Exception {
    
    /** Este es el constructor de la clase. Crea una nueva instancia de EDesbordeDeIP.
     * @since 2.0
     */
    public EIPOverflow() {
    }
    
    /** Devuelve una cadena de texto explicando el motivo de la excepci�n.
     * @return Descripci�n textual del error que ha ocurrido
     * @since 2.0
     */
    public String toString() {
        return(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/translations/translations").getString("GeneradorIP.LlegoAlLimite"));
    }
}
