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
package simMPLS.protocols;

/** Esta clase implementa el payload de un paquete TCP. Es decir,
 * simular� que el paquete TCP lleva datos del nivel superior en su interior y no
 * est�, por tanto, vac�o.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TTCPPayload {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TDatosTCP.
     * @param t El tama�o que ocupar� el paquete TCP, en bytes.
     * @since 1.0
     */
    public TTCPPayload(int t) {
        tamanio = 20;   // Tama�o de la cabecera TCP
        tamanio += t;
    }
    
    /** Este m�todo nos permite obtener el valor del atributo privado de la clase
     * "tamanio".
     * @return El valor del atributo tamanio.
     * @since 1.0
     */
    public int setSize() {
        return tamanio;
    }
    
    /** Este m�todo nos permite modificar el valor del atributo privado de la clase, tamanio.
     * @param t El nuevo valor del atributo privado tamanio.
     * @since 1.0
     */
    public void ponerTamanio(int t) {
        tamanio = 20;   // Tama�o de la cabecera TCP.
        tamanio += t;
    }
    
    /** Este atributo almacenar� el tama�o de los datos que se supone que van sobre TCP.
     * @since 1.0
     */
    private int tamanio;
}
