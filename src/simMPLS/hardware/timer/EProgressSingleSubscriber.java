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

/** Esta clase implementa una excepci�n que se utilizar� cuando un contador de
 * progreso intente a�adir un nuevo suscriptor, teniendo ya uno previamente
 * suscrito.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class EProgressSingleSubscriber extends Exception {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * EProgresoUnSoloSuscriptor.
     * @since 1.0
     */    
    public EProgressSingleSubscriber() {
    }
    
    /** Este m�todo devuelve un texo explicativo del motivo por el que se ha producido
     * la excepci�n.
     * @return Una cadena de texto que es un mensaje explicativo del motivo de
     * la excepci�n.
     */    
    public String toString() {
        return(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("EProgresoUnSoloSuscriptor.texto"));
    }
    
}
