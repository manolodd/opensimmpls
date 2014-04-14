//**************************************************************************
// Nombre......: TMatrizConmutacion.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa una matriz de conmutación de un nodo 
// ............: LER o LSR o sus variantes, dentro de la topología.
// ............: Equivale a matrices ILM, FTN y NHLFE unificadas en un solo
// ............: tipo de tabla de conmutación.
// Fecha.......: 13/04/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.tldp;

import java.util.*;
import simMPLS.utiles.*;

/** Esta clase implementa una matriz de conmutación para cualquiera de los
 * encaminadores o conmutadores del simulador.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TMatrizConmutacion {

    /** Este método es el constructor de la clase. Crea un anueva instancia de
     * TMatrizConmutacion.
     * @since 1.0
     */
    public TMatrizConmutacion() {
        matriz = new LinkedList();
        cerrojo = new TMonitor();
    }

    /**
     * Este método devuelve el monitor de la clase, que permitirá realizar operaciones
     * sobre la matriz de conmutación sin peligro de accesos concurrentes mal llevados
     * a cabo.
     * @return El monitor que hace de cerrojo para operar en esta clase.
     * @since 1.0
     */    
    public TMonitor obtenerCerrojo() {
        return cerrojo;
    }
    
    /** Este método vacía todas las entradas existentes en la matriz de conmutación,
     * dejándola inicializada como antes del proceso de simulación.
     * @since 1.0
     */    
    public void borrarTodo() {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        cerrojo.liberar();
    }

    /** Este método inserta una nueva entrada en la matriz de conmutación.
     * @param emc La nueva entrada que deseamos insertar en la matriz de conmutación.
     * @since 1.0
     */    
    public void insertar(TEntradaMatrizConmutacion emc) {
        cerrojo.bloquear();
        matriz.addLast(emc);
        cerrojo.liberar();
    }

    /** Este método permite acceder a una entrada concreta de la matriz de conmutación.
     * @param p El puerto de la entrada de la matriz que buscamos.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que buscamos.
     * @param t El tipo de entrada (ILM o FTN) que estamos buscando.
     * @return Devuelve la entrada de la matriz de conmutación que concuerda con los parametros
     * de búsqueda, si la encuentra, o null, en caso contrario.
     * @since 1.0
     */    
    public TEntradaMatrizConmutacion obtenerEntrada(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerPuertoEntrada() == p) {
                    if (emc.obtenerTipo() == t) {
                        cerrojo.liberar();
                        return emc;
                    }
                }
            }
        }
        cerrojo.liberar();
        return null;
    }

    /** Este método permite acceder a una entrada concreta de la matriz de conmutación,
     * que debe tener un identificador de sesión TLDP propio exáctamente igua al indicado.
     * @param id Identificador TLDP propio que asignamos en su momento a la entrada de la matriz
     * de conmutación que buscamos.
     * @return La entrada de la matriz de conmutación coincidente, en caso de encontrarla, y
     * null en caso contrario.
     * @since 1.0
     */    
    public TEntradaMatrizConmutacion obtenerEntradaIDPropio(int id) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerIDLDPPropio() == id) {
                cerrojo.liberar();
                return emc;
            }
        }
        cerrojo.liberar();
        return null;
    }

    /** Este método da acceso a una entrada de conmutación que provenga de un nodo
     * antecesor concreto.
     * @param id El identificador de sesión TLDP que el nodo antecesor especifico para la entrada que
     * estamos buscando.
     * @param pEntrada El puerto de entrada asociado a la entrada TLDP que estamos buscando. Necesario
     * porque sólo con el identificador TLDP del antecesor no vale ya que distintos
     * antecesores pueden otorgar el mismo identificador de sesión TLDP.
     * @return La entrada de la matriz de conmutación buscada, si la encuentra, o null en caso
     * contrario.
     * @since 1.0
     */    
    public TEntradaMatrizConmutacion obtenerEntradaIDAntecesor(int id, int pEntrada) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerIDLDPAntecesor() == id) {
                if (emc.obtenerPuertoEntrada() == pEntrada) {
                    cerrojo.liberar();
                    return emc;
                }
            }
        }
        cerrojo.liberar();
        return null;
    }

    /** Este método sirve para ver si hay un acierto al buscar una entrada de la matriz
     * de conmutación concreta o no.
     * @param p El puerto de la entrada de la matriz que buscamos.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que buscamos.
     * @param t El tipo de entrada (ILM o FTN) que estamos buscando.
     * @return Devuelve true si existe una entrada coincidente en la matriz de conmutación y
     * false en caso contrario.
     * @since 1.0
     */    
    public boolean existeEntrada(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerPuertoEntrada() == p) {
                    if (emc.obtenerTipo() == t) {
                        cerrojo.liberar();
                        return true;
                    }
                }
            }
        }
        cerrojo.liberar();
        return false;
    }

    /** Este método elimina una entrada concreta de la matriz de conmutación.
     * @param p El puerto de la entrada de la matriz que queremos borrar.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que queremos borrar.
     * @param t El tipo de entrada (ILM o FTN) de la entrada de la matriz que deseamos borrar.
     * @since 1.0
     */    
    public void borrarEntrada(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerPuertoEntrada() == p) {
                    if (emc.obtenerTipo() == t) {
                        it.remove();
                    }
                }
            }
        }
        cerrojo.liberar();
    }

    /** Este método elimina una entrada concreta de la matriz de conmutación.
     * @param id El identificador TLDP propio de la entrada de la matriz que deseamos eliminar.
     * @param pEntrada El puerto de entrada de la entrada de la matriz de conmutación que queremos
     * eliminar.
     * @since 1.0
     */    
    public void borrarEntradaIDPropio(int id, int pEntrada) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerIDLDPPropio() == id) {
                if (emc.obtenerPuertoEntrada() == pEntrada) {
                    it.remove();
                }
            }
        }
        cerrojo.liberar();
    }

    /** Este método obtiene la operación que hay que realizar sobre la pila de etiquetas
     * de los paquetes de un flujo concreto, según la entrada de la matriz de
     * conmutación asociada a ese flujo.
     * @param p El puerto de la entrada de la matriz que queremos consultar.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que queremos consultar.
     * @param t El tipo de entrada (ILM o FTN) de la entrada que queremos consultar.
     * @return Devuelve la operación a realizar sobre la pila de etiquetas. Es una de las
     * constantes definidas en TEntradaMatrizConmutacion.
     * @since 1.0
     */    
    public int obtenerOperacion(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerPuertoEntrada() == p) {
                    if (emc.obtenerTipo() == t) {
                        cerrojo.liberar();
                        return emc.obtenerOperacion();
                    }
                }
            }
        }
        cerrojo.liberar();
        return TEntradaMatrizConmutacion.SIN_DEFINIR;
    }

    /** Este método obtiene la etiqueta de salida para los paquetes de un flujo concreto,
     * según la entrada de la matriz de conmutación asociada a ese flujo.
     * @param p El puerto de la entrada de la matriz que queremos consultar.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que queremos consultar.
     * @param t El tipo de entrada (ILM o FTN) de la entrada de la matriz que queremos consultar.
     * @return La etiqueta de salida que hay que poner a los paquetes del flujo asociado a la
     * entrada de la matriz que hemos especificado.
     * @since 1.0
     */    
    public int obtenerEtiquetaSalida(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerPuertoEntrada() == p) {
                    if (emc.obtenerTipo() == t) {
                        cerrojo.liberar();
                        return emc.obtenerEtiqueta();
                    }
                }
            }
        }
        cerrojo.liberar();
        return TEntradaMatrizConmutacion.SIN_DEFINIR;
    }

    /** Este método obtiene el puerto de salida de los paquetes de un flujo concreto,
     * según la entrada de la matriz de conmutación asociada a ese flujo.
     * @param p El puerto de la entrada de la matriz que quermos consultar.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que queremos consultar.
     * @param t El tipo de entrada (ILM o FTN) de la entrada de la matriz que queremos consultar.
     * @return El puerto de salida para los paquetes del flujo asociado a la entrada de la
     * matriz especificada.
     * @since 1.0
     */    
    public int obtenerPuertoSalida(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerPuertoEntrada() == p) {
                    if (emc.obtenerTipo() == t) {
                        cerrojo.liberar();
                        return emc.obtenerPuertoSalida();
                    }
                }
            }
        }
        cerrojo.liberar();
        return TEntradaMatrizConmutacion.SIN_DEFINIR;
    }

    /** Este método comprueba si hay alguna entrada cuya etiqueta de entrada
     * coincide con la epecificada.
     * @param lf Etiqueta que debe tener la entrada de la matriz que estamos buscando.
     * @return true si existe una entrada de la matriz con esa etiqueta de entrada, si no,
     * false.
     * @since 1.0
     */    
    public boolean existeEtiquetaEntrada(int lf) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TEntradaMatrizConmutacion emc = null;
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerTipo() == TEntradaMatrizConmutacion.LABEL) {
                    cerrojo.liberar();
                    return true;
                }
            }
        }
        cerrojo.liberar();
        return false;
    }

    /** Este método devuelve una etiqueta de 20 bits única. Generalmente para responder a una
     * petición de etiqueta realizada por otro nodo antecesor.
     * @return Una etiqueta para recibir tráfico, si es posible, o la constante
     * ETIQUETA_NO_DISPONIBLE, en caso de que se haya agotado el espacio de etiquetas
     * del nodo.
     * @since 1.0
     */    
    public int obtenerEtiqueta() {
        int etiqueta = 16;
        while (etiqueta <= 1048575) {
            if (!existeEtiquetaEntrada(etiqueta))
                return etiqueta;
            else
                etiqueta++;
        }
        return TEntradaMatrizConmutacion.ETIQUETA_DENEGADA;
    }

    /** Este método obtiene acceso a todas las entradas de la matríz de conmutación vía
     * el iterador de la lista enlazada que las contiene.
     * @return El iterador de la lista enlazada que contiene las entradas de la matriz de
     * conmutación.
     * @since 1.0
     */    
    public Iterator obtenerIteradorEntradas() {
        return matriz.iterator();
    }
    
    /** Este método obtiene el número de entradas que hay en la matriz de
     * conmutación.
     * @return Número de entradas de la matriz de conmutacion.
     * @since 1.0
     */    
    public int obtenerNumeroEntradas() {
        return matriz.size();
    }

    /**
     * Este método reinicia la matriz de conmutación y la deja como si acabase de ser
     * creada por el constructor de la clase.
     * @since 1.0
     */    
    public void reset() {
        this.cerrojo.bloquear();
        Iterator it = this.matriz.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojo.liberar();
    }

    /** Lista enlazda que almacenará las entradas de la matriz de conmutación. Realmente
     * es la tabla de encaminamiento/conmutación de los encaminadores/conmutadores.
     * @since 1.0
     */    
    private LinkedList matriz;
    /** Monitor encargado de preservar una zona crítica para el acceso concurrente a la
     * matriz de conmutación.
     * @since 1.0
     */    
    private TMonitor cerrojo;
}
