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

/** Esta clase implementa una etiqueta MPLS con sus campos bien diferenciados y
 * accesibles.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TMPLSLabel implements Comparable {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TEtiquetaMPLS usando como identificador el pasado por par�metros.
     * @param id El identificador de la etiqueta, pasado por par�metro.
     * @since 1.0
     */
    public TMPLSLabel(int id) {
        TTL = 256;
        label = 16;
        EXP = 0;
        BoS = true;
        identificador = id;
    }
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TEtiquetaMPLS y le pone por defecto el identificador 0.
     * @since 1.0
     */
    public TMPLSLabel() {
        TTL = 256;
        label = 16;
        EXP = 0;
        BoS = true;
        identificador = 0;
    }
    
    /** Este m�todo nos permite cambiar el identificador de la etiqueta MPLS.
     * @param id El nuevo valor que deseamos poner al identificador de la etiqueta MPLS.
     * @since 1.0
     */
    public void ponerIdentificador(int id) {
        identificador = id;
    }
    
    /** Este m�todo nos permite consultar el valor del identificador de la etiqueta
     * MPLS.
     * @return El valor del identificador de la etiqueta MPLS.
     * @since 1.0
     */
    public int obtenerIdentificador() {
        return identificador;
    }
    
    /** Este m�todo nos permite cambiar el valor del atributo <B>label</B> de la
     * etiqueta MPLS.
     * @param l El nuevo valor que deseamos para el campo label de la etiqueta MPLS.
     * @since 1.0
     */
    public void setLabelField(int l) {
        label = l;
    }
    
    /** Este m�todo nos permite obtener el valor del atributo <B>label</B> de la etiqueta MPLS.
     * @return El valor del atributo privado label, de la etiqueta MPLS.
     * @since 1.0
     */
    public int getLabelField() {
        return label;
    }
    
    /** Este m�todo nos permite cambiar el valor del atributto <B>TTL</B> (Time to Live)
     * de la etiqueta MPLS.
     * @param t El nuevo valor deseado para el atributo TTL.
     * @since 1.0
     */
    public void ponerTTL(int t) {
        TTL = t;
    }
    
    /** Este m�todo nos permite obtener el valor del atributo privado <B>TTL</B> de la
     * etiqueta MPLS.
     * @return El valor del atributo TTL de la etiqueta MPLS.
     * @since 1.0
     */
    public int obtenerTTL() {
        return TTL;
    }
    
    /** Este m�todo nos permite especificar el valor del atributo <B>EXP</B> de la
     * etiqueta MPLS.
     * @param e El nuevo valor deseado para el atributo privado EXP de la etiqueta MPLS.
     * @since 1.0
     */
    public void ponerEXP(int e) {
        EXP = e;
    }
    
    /** Este m�todo nos permite obtener el valor del atributo privado <B>EXP</B> de la
     * etiqueta MPLS.
     * @return El valor del atributo privado EXP de la etiqueta MPLS.
     * @since 1.0
     */
    public int getEXPField() {
        return EXP;
    }
    
    /** Este m�todo nos permite especificar un valor para el atributo privado <B>Bos</B>
     * (Bottom of Stack) de la etiqueta MPLS.
     * @param b El nuevo valor para el campo BoS de la etiqueta MPLS.
     * @since 1.0
     */
    public void ponerBoS(boolean b) {
        BoS = b;
    }
    
    /** Este m�todo nos permite obtener el valor del atributo privado <B>BoS</B> (Bottom
     * of Stack)de la etiqueta MPLS.
     * @return El valor del atributo privado BoS (Bottom of Stack) del a etiqueta MPLS.
     * @since 1.0
     */
    public boolean obtenerBoS() {
        return BoS;
    }
    
    /** Este m�todo compara esta instancia de TMPLSLabel con otra del mismo tipo,
 para aclarar si es mayor, menor o igual, en base al orden que especifica este
 m�todo. Es la implementaci�n de la interfaz Comparable.
     * @param o El objeto del tipo TMPLSLabel con el que se va a comparar.
     * @return -1, 0 � 1, dependiendo de si, de acuerdo con el orden establecido, esta
     * instancia es menor, igual o mayor que la especificada por par�metro.
     * @since 1.0
     */
    public int compareTo(Object o) {
        TMPLSLabel e2= (TMPLSLabel) o;
        if (this.obtenerIdentificador() > e2.obtenerIdentificador()) {
            return 1;
        } else if (this.obtenerIdentificador() == e2.obtenerIdentificador()) {
            return 0;
        } else {
            return -1;
        }
    }
    
    /** Atributo privado. El campo TTL (Time to Live) de la etiqueta MPLS.
     * @since 1.0
     */
    private int TTL;
    /** Atributo privado. El campo Label de la cabecera MPLS.
     * @since 1.0
     */
    private int label;
    /** Atributo privado. Es el campo EXP (Experimental) de la etiqueta MPLS. Lo
     * usaremos en Open SimMPLS para denotar cu�ndo un paquete est� marcado como
     * privilegiado.
     * @since 1.0
     */
    private int EXP;
    /** Atributo privado. Campo BoS (Bottom of Stack) de la etiqueta MPLS.
     * @since 1.0
     */
    private boolean BoS;
    /** Atributo privado. Identificador num�rico �nico para cada etiqueta, de forma que
     * puedan ser ordenadas en la pila.
     * @since 1.0
     */
    private int identificador;
}
