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

/** Esta clase implementa una excepci�n que se usar� cuando se hayan usado todas las
 * direcciones IP que es capaz de generar el generador autom�tico de direcciones
 * IP.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class EDesbordeDeIP extends Exception {
    
    /** Este es el constructor de la clase. Crea una nueva instancia de EDesbordeDeIP.
     * @since 1.0
     */
    public EDesbordeDeIP() {
    }
    
    /** Devuelve una cadena de texto explicando el motivo de la excepci�n.
     * @return Descripci�n textual del error que ha ocurrido
     * @since 1.0
     */
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("GeneradorIP.LlegoAlLimite"));
    }
}
