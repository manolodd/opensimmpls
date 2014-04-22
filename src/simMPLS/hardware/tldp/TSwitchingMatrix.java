//**************************************************************************
// Nombre......: TSwitchingMatrix.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa una matriz de conmutaci�n de un nodo 
// ............: LER o LSR o sus variantes, dentro de la topolog�a.
// ............: Equivale a matrices ILM, FTN y NHLFE unificadas en un solo
// ............: tipo de tabla de conmutaci�n.
// Fecha.......: 13/04/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.tldp;

import simMPLS.utils.TLock;
import java.util.*;

/** Esta clase implementa una matriz de conmutaci�n para cualquiera de los
 * encaminadores o conmutadores del simulador.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSwitchingMatrix {

    /** Este m�todo es el constructor de la clase. Crea un anueva instancia de
     * TMatrizConmutacion.
     * @since 1.0
     */
    public TSwitchingMatrix() {
        matriz = new LinkedList();
        cerrojo = new TLock();
    }

    /**
     * Este m�todo devuelve el monitor de la clase, que permitir� realizar operaciones
     * sobre la matriz de conmutaci�n sin peligro de accesos concurrentes mal llevados
     * a cabo.
     * @return El monitor que hace de cerrojo para operar en esta clase.
     * @since 1.0
     */    
    public TLock obtenerCerrojo() {
        return cerrojo;
    }
    
    /** Este m�todo vac�a todas las entradas existentes en la matriz de conmutaci�n,
     * dej�ndola inicializada como antes del proceso de simulaci�n.
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

    /** Este m�todo inserta una nueva entrada en la matriz de conmutaci�n.
     * @param emc La nueva entrada que deseamos insertar en la matriz de conmutaci�n.
     * @since 1.0
     */    
    public void insertar(TSwitchingMatrixEntry emc) {
        cerrojo.bloquear();
        matriz.addLast(emc);
        cerrojo.liberar();
    }

    /** Este m�todo permite acceder a una entrada concreta de la matriz de conmutaci�n.
     * @param p El puerto de la entrada de la matriz que buscamos.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que buscamos.
     * @param t El tipo de entrada (ILM o FTN) que estamos buscando.
     * @return Devuelve la entrada de la matriz de conmutaci�n que concuerda con los parametros
     * de b�squeda, si la encuentra, o null, en caso contrario.
     * @since 1.0
     */    
    public TSwitchingMatrixEntry obtenerEntrada(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
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

    /** Este m�todo permite acceder a una entrada concreta de la matriz de conmutaci�n,
     * que debe tener un identificador de sesi�n TLDP propio ex�ctamente igua al indicado.
     * @param id Identificador TLDP propio que asignamos en su momento a la entrada de la matriz
     * de conmutaci�n que buscamos.
     * @return La entrada de la matriz de conmutaci�n coincidente, en caso de encontrarla, y
     * null en caso contrario.
     * @since 1.0
     */    
    public TSwitchingMatrixEntry obtenerEntradaIDPropio(int id) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc.obtenerIDLDPPropio() == id) {
                cerrojo.liberar();
                return emc;
            }
        }
        cerrojo.liberar();
        return null;
    }

    /** Este m�todo da acceso a una entrada de conmutaci�n que provenga de un nodo
     * antecesor concreto.
     * @param id El identificador de sesi�n TLDP que el nodo antecesor especifico para la entrada que
     * estamos buscando.
     * @param pEntrada El puerto de entrada asociado a la entrada TLDP que estamos buscando. Necesario
     * porque s�lo con el identificador TLDP del antecesor no vale ya que distintos
     * antecesores pueden otorgar el mismo identificador de sesi�n TLDP.
     * @return La entrada de la matriz de conmutaci�n buscada, si la encuentra, o null en caso
     * contrario.
     * @since 1.0
     */    
    public TSwitchingMatrixEntry obtenerEntradaIDAntecesor(int id, int pEntrada) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
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

    /** Este m�todo sirve para ver si hay un acierto al buscar una entrada de la matriz
     * de conmutaci�n concreta o no.
     * @param p El puerto de la entrada de la matriz que buscamos.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que buscamos.
     * @param t El tipo de entrada (ILM o FTN) que estamos buscando.
     * @return Devuelve true si existe una entrada coincidente en la matriz de conmutaci�n y
     * false en caso contrario.
     * @since 1.0
     */    
    public boolean existeEntrada(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
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

    /** Este m�todo elimina una entrada concreta de la matriz de conmutaci�n.
     * @param p El puerto de la entrada de la matriz que queremos borrar.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que queremos borrar.
     * @param t El tipo de entrada (ILM o FTN) de la entrada de la matriz que deseamos borrar.
     * @since 1.0
     */    
    public void borrarEntrada(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
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

    /** Este m�todo elimina una entrada concreta de la matriz de conmutaci�n.
     * @param id El identificador TLDP propio de la entrada de la matriz que deseamos eliminar.
     * @param pEntrada El puerto de entrada de la entrada de la matriz de conmutaci�n que queremos
     * eliminar.
     * @since 1.0
     */    
    public void borrarEntradaIDPropio(int id, int pEntrada) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc.obtenerIDLDPPropio() == id) {
                if (emc.obtenerPuertoEntrada() == pEntrada) {
                    it.remove();
                }
            }
        }
        cerrojo.liberar();
    }

    /** Este m�todo obtiene la operaci�n que hay que realizar sobre la pila de etiquetas
     * de los paquetes de un flujo concreto, seg�n la entrada de la matriz de
     * conmutaci�n asociada a ese flujo.
     * @param p El puerto de la entrada de la matriz que queremos consultar.
     * @param lf El valor de la etiqueta de entrada o del campo FEC de la entrada de la matriz
     * que queremos consultar.
     * @param t El tipo de entrada (ILM o FTN) de la entrada que queremos consultar.
     * @return Devuelve la operaci�n a realizar sobre la pila de etiquetas. Es una de las
 constantes definidas en TSwitchingMatrixEntry.
     * @since 1.0
     */    
    public int obtenerOperacion(int p, int lf, int t) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
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
        return TSwitchingMatrixEntry.SIN_DEFINIR;
    }

    /** Este m�todo obtiene la etiqueta de salida para los paquetes de un flujo concreto,
     * seg�n la entrada de la matriz de conmutaci�n asociada a ese flujo.
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
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
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
        return TSwitchingMatrixEntry.SIN_DEFINIR;
    }

    /** Este m�todo obtiene el puerto de salida de los paquetes de un flujo concreto,
     * seg�n la entrada de la matriz de conmutaci�n asociada a ese flujo.
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
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
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
        return TSwitchingMatrixEntry.SIN_DEFINIR;
    }

    /** Este m�todo comprueba si hay alguna entrada cuya etiqueta de entrada
     * coincide con la epecificada.
     * @param lf Etiqueta que debe tener la entrada de la matriz que estamos buscando.
     * @return true si existe una entrada de la matriz con esa etiqueta de entrada, si no,
     * false.
     * @since 1.0
     */    
    public boolean existeEtiquetaEntrada(int lf) {
        cerrojo.bloquear();
        Iterator it = matriz.iterator();
        TSwitchingMatrixEntry emc = null;
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc.obtenerLabelFEC() == lf) {
                if (emc.obtenerTipo() == TSwitchingMatrixEntry.LABEL) {
                    cerrojo.liberar();
                    return true;
                }
            }
        }
        cerrojo.liberar();
        return false;
    }

    /** Este m�todo devuelve una etiqueta de 20 bits �nica. Generalmente para responder a una
     * petici�n de etiqueta realizada por otro nodo antecesor.
     * @return Una etiqueta para recibir tr�fico, si es posible, o la constante
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
        return TSwitchingMatrixEntry.ETIQUETA_DENEGADA;
    }

    /** Este m�todo obtiene acceso a todas las entradas de la matr�z de conmutaci�n v�a
     * el iterador de la lista enlazada que las contiene.
     * @return El iterador de la lista enlazada que contiene las entradas de la matriz de
     * conmutaci�n.
     * @since 1.0
     */    
    public Iterator obtenerIteradorEntradas() {
        return matriz.iterator();
    }
    
    /** Este m�todo obtiene el n�mero de entradas que hay en la matriz de
     * conmutaci�n.
     * @return N�mero de entradas de la matriz de conmutacion.
     * @since 1.0
     */    
    public int obtenerNumeroEntradas() {
        return matriz.size();
    }

    /**
     * Este m�todo reinicia la matriz de conmutaci�n y la deja como si acabase de ser
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

    /** Lista enlazda que almacenar� las entradas de la matriz de conmutaci�n. Realmente
     * es la tabla de encaminamiento/conmutaci�n de los encaminadores/conmutadores.
     * @since 1.0
     */    
    private LinkedList matriz;
    /** Monitor encargado de preservar una zona cr�tica para el acceso concurrente a la
     * matriz de conmutaci�n.
     * @since 1.0
     */    
    private TLock cerrojo;
}
