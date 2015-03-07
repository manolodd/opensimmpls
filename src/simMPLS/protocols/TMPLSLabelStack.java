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

import simMPLS.utils.EIDGeneratorOverflow;
import simMPLS.utils.TIDGenerator;
import java.util.*;

/** Esta clase implementa la pila de etiquetas de un paquete MPLS.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TMPLSLabelStack {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TPilaEtiquetasMPLS, vac�a.
     * @since 1.0
     */
    public TMPLSLabelStack() {
        pila = new LinkedList();
        generaID = new TIDGenerator();
    }
    
    /** Este m�todo devuelve el tama�o completo de la pila de etiquetas, en bytes.
     * @return Tama�o completo de la pila de etiquetas, en bytes.
     * @since 1.0
     */
    public int getSize() {
        return pila.size();
    }
    
    /** Este m�todo inserta una nueva etiqueta MPLS en la pila de etiquetas.
     * @param etiqueta Etiqueta que deseamos insertar en la pila de etiquetas MPLS.
     * @since 1.0
     */
    public void pushLabel(TMPLSLabel etiqueta) {
        try {
            etiqueta.setID(generaID.obtenerNuevo());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        pila.addLast(etiqueta);
    }
    
    /** Este metodo nos da acceso a la etiqueta de la pila de etiquetas que se encuentra
     * ne la cima de la pila y as� poder acceder a sus m�todos.
     * @return Etiqueta en la cima de la pila de etiquetas MPLS.
     * @since 1.0
     */
    public TMPLSLabel getTop() {
        return (TMPLSLabel) pila.getLast();
    }
    
    /** Este m�todo elimina la etiqueta que se encuentra en la cima de la pila de
     * etiquetas.
     * @since 1.0
     */
    public void popTop() {
        pila.removeLast();
    }
    
    /** Este m�todo cambia la etiqueta que se encuentra en la cima de la pila de
     * etiquetas, por la etiqueta pasada por par�metros.
     * @param etiqueta nueva etiqueta que sustituir� a la de la cima de la pila de etiquetas.
     * @since 1.0
     */
    public void cambiarEtiqueta(TMPLSLabel etiqueta) {
        this.popTop();
        try {
            etiqueta.setID(generaID.obtenerNuevo());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        pila.addLast(etiqueta);
    }
    
    /** Este m�todo elimina por completo la pila de etiquetas.
     * @since 1.0
     */
    public void borrar() {
        Iterator it = pila.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
    
    /** Este atributo es una lista enlazada, configurada como pila, que almacenar� todas
     * las etiquetas en orden LIFO.
     * @since 1.0
     */
    private LinkedList pila;
    /** Este atributo es un generador de identificadores, que ser� usado para asignar un
     * identificador distinto a cada etiqueta y poder ordenarlas as� en la pila.
     * @since 1.0
     */
    private TIDGenerator generaID;
}
