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

/** Esta clase implementa la cabecera de un paquete IPv4 con las opciones que
 * interesan para este proyecto final de carrera.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TIPv4Header {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de TCabeceraIPv4.
     * @param ipo Direcci�n IP origen.
     * @param ipd Direcci�n IP destino.
     * @since 1.0
     */
    public TIPv4Header(String ipo, String ipd) {
        IPOrigen = ipo;
        IPDestino = ipd;
        opciones = new TIPv4OptionsField();
        TTL = 255;
    }
    
    /**
     * Este m�todo devuelve una cadena de caracteres que identifica �nequ�vocamente a
     * un paquete marcado con GoS dentro de la topolog�a. Est� formada por una
     * concatenaci�n de las representaciones textuales de la IP del emisor y del
     * identificador �nico del paquete.
     * @return Una cadena de caracteres �nica en todo el dominio MPLS para ese paquete.
     * @since 1.0
     */
    public int obtenerClavePrimaria() {
        String clave = "";
        if (opciones.isUsed()) {
            clave = this.IPOrigen + opciones.getGoSPacketID();
            return clave.hashCode();
        }
        return -1;
    }
    
    /** Este m�todo devuelve el tama�o de la cabecera IPv4 en un momento dado. Puede ser
     * que su campo opcinoes haya variad y por tanto el tama�o de la cabecera no sea
     * constante.
     * @return El tama�o de la cabecera en bytes.
     * @since 1.0
     */
    public int getSize() {
        return (20 + opciones.obtenerTamanio());
    }
    
    /** Este m�todo devuelve la IP de origen de la cabecera IPv4.
     * @return La direcci�n IP de origen.
     * @since 1.0
     */
    public String getOriginIP() {
        return IPOrigen;
    }
    
    /** Este m�todo sirve para modificar el campo de direcci�n IP origen de la cabecera
     * IPv4.
     * @param ipo El nuevo valor de la IP de origen.
     * @since 1.0
     */
    public void ponerIPOrigen(String ipo) {
        IPOrigen = ipo;
    }
    
    /** Este m�todo devuelve la IP de destino de la cabecera IPv4.
     * @return La direcci�n IP de destino.
     * @since 1.0
     */
    public String getTargetIP() {
        return IPDestino;
    }
    
    /** Este m�todo sirve para modificar el campo de direcci�n destino de la cabecera
     * IPv4.
     * @param ipd El nuevo valor para la IP de destino.
     * @since 1.0
     */
    public void ponerIPDestino(String ipd) {
        IPDestino = ipd;
    }
    
    /** Este m�todo modifica el valor del campo TTL de la cabecera IPv4.
     * @param t El nuevo valor para el campo TTL.
     * @since 1.0
     */
    public void ponerTTL(int t) {
        TTL = t;
    }
    
    /** Este m�todo obtiene el valor del campo TTL de la cabecera IPv4.
     * @return El valor del campo TTL.
     * @since 1.0
     */
    public int obtenerTTL() {
        return TTL;
    }
    
    /** Este m�todo obtiene el campo opciones al completo, de la cabecera IPv4.
     * @return El campo opciones de la cabecera, completo.
     * @since 1.0
     */
    public TIPv4OptionsField getOptionsField() {
        return opciones;
    }
    
    /** Atributo que ser� la direcci�n fuente del paquete IPv4 que llevar� esta cabecera.
     * @since 1.0
     */
    private String IPOrigen;
    /** Atributo que ser� la direcci�n destino del paquete IPv4 que llevar� esta cabecera.
     * @since 1.0
     */
    private String IPDestino;
    /** Atributo que ser� el campo "Time to Live" de la cabecera IPv4.
     * @since 1.0
     */
    private int TTL;
    /** Atributo que implementa una codificaci�n del campo opciones de IPv4.
     * @since 1.0
     */
    private TIPv4OptionsField opciones;
}
