package simMPLS.protocolo;

import java.util.*;
import simMPLS.utiles.*;

/** Esta clase implementa la pila de etiquetas de un paquete MPLS.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPilaEtiquetasMPLS {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TPilaEtiquetasMPLS, vacía.
     * @since 1.0
     */
    public TPilaEtiquetasMPLS() {
        pila = new LinkedList();
        generaID = new TIdentificador();
    }
    
    /** Este método devuelve el tamaño completo de la pila de etiquetas, en bytes.
     * @return Tamaño completo de la pila de etiquetas, en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return pila.size();
    }
    
    /** Este método inserta una nueva etiqueta MPLS en la pila de etiquetas.
     * @param etiqueta Etiqueta que deseamos insertar en la pila de etiquetas MPLS.
     * @since 1.0
     */
    public void ponerEtiqueta(TEtiquetaMPLS etiqueta) {
        try {
            etiqueta.ponerIdentificador(generaID.obtenerNuevo());
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace();
        }
        pila.addLast(etiqueta);
    }
    
    /** Este metodo nos da acceso a la etiqueta de la pila de etiquetas que se encuentra
     * ne la cima de la pila y así poder acceder a sus métodos.
     * @return Etiqueta en la cima de la pila de etiquetas MPLS.
     * @since 1.0
     */
    public TEtiquetaMPLS obtenerEtiqueta() {
        return (TEtiquetaMPLS) pila.getLast();
    }
    
    /** Este método elimina la etiqueta que se encuentra en la cima de la pila de
     * etiquetas.
     * @since 1.0
     */
    public void borrarEtiqueta() {
        pila.removeLast();
    }
    
    /** Este método cambia la etiqueta que se encuentra en la cima de la pila de
     * etiquetas, por la etiqueta pasada por parámetros.
     * @param etiqueta nueva etiqueta que sustituirá a la de la cima de la pila de etiquetas.
     * @since 1.0
     */
    public void cambiarEtiqueta(TEtiquetaMPLS etiqueta) {
        this.borrarEtiqueta();
        try {
            etiqueta.ponerIdentificador(generaID.obtenerNuevo());
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace();
        }
        pila.addLast(etiqueta);
    }
    
    /** Este método elimina por completo la pila de etiquetas.
     * @since 1.0
     */
    public void borrar() {
        Iterator it = pila.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
    
    /** Este atributo es una lista enlazada, configurada como pila, que almacenará todas
     * las etiquetas en orden LIFO.
     * @since 1.0
     */
    private LinkedList pila;
    /** Este atributo es un generador de identificadores, que será usado para asignar un
     * identificador distinto a cada etiqueta y poder ordenarlas así en la pila.
     * @since 1.0
     */
    private TIdentificador generaID;
}
