/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute iterator and/or modify
 * iterator under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that iterator will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.hardware.tldp;

import java.util.Iterator;
import java.util.LinkedList;
import simMPLS.utils.TMonitor;

/**
 * This class implements a switching matrix to be used within each node of the
 * topology.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TSwitchingMatrix {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TSwitchingMatrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TSwitchingMatrix() {
        this.switchingMatrix = new LinkedList();
        this.monitor = new TMonitor();
    }

    /**
     * Este m�todo devuelve el monitor de la clase, que permitir� realizar
 operaciones sobre la switchingMatrix de conmutaci�n sin peligro de accesos
 concurrentes mal llevados a cabo.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return El monitor que hace de monitor para operar en esta clase.
     * @since 1.0
     */
    public TMonitor getMonitor() {
        return this.monitor;
    }

    /**
     * Este m�todo vac�a todas las entradas existentes en la switchingMatrix de
 conmutaci�n, dej�ndola inicializada como antes del proceso de simulaci�n.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void clear() {
        this.monitor.lock();
        Iterator it = this.switchingMatrix.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.monitor.unLock();
    }

    /**
     * Este m�todo inserta una nueva entrada en la switchingMatrix de conmutaci�n.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param switchingMatrixEntry La nueva entrada que deseamos addEntry en la switchingMatrix de
 conmutaci�n.
     * @since 1.0
     */
    public void addEntry(TSwitchingMatrixEntry switchingMatrixEntry) {
        this.monitor.lock();
        this.switchingMatrix.addLast(switchingMatrixEntry);
        this.monitor.unLock();
    }

    /**
     * Este m�todo permite acceder a una entrada concreta de la switchingMatrix de
 conmutaci�n.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID El puerto de la entrada de la switchingMatrix que buscamos.
     * @param labelOrFEC El valor de la labelAux de entrada o del campo FEC_ENTRY de la
 entrada de la switchingMatrix que buscamos.
     * @param entryType El tipo de entrada (ILM o FTN) que estamos buscando.
     * @return Devuelve la entrada de la switchingMatrix de conmutaci�n que concuerda con
 los parametros de b�squeda, si la encuentra, o null, en caso contrario.
     * @since 1.0
     */
    public TSwitchingMatrixEntry getEntry(int incomingPortID, int labelOrFEC, int entryType) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.monitor.unLock();
                        return switchingMatrixEntryAux;
                    }
                }
            }
        }
        this.monitor.unLock();
        return null;
    }

    /**
     * Este m�todo permite acceder a una entrada concreta de la switchingMatrix de
 conmutaci�n, que debe tener un identificador de sesi�n TLDP propio
 ex�ctamente igua al indicado.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param localTLDPSessionID Identificador TLDP propio que asignamos en su momento a la
 entrada de la switchingMatrix de conmutaci�n que buscamos.
     * @return La entrada de la switchingMatrix de conmutaci�n coincidente, en caso de
 encontrarla, y null en caso contrario.
     * @since 1.0
     */
    public TSwitchingMatrixEntry getEntryHavingLocalTLDPSessionID(int localTLDPSessionID) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLocalTLDPSessionID() == localTLDPSessionID) {
                this.monitor.unLock();
                return switchingMatrixEntryAux;
            }
        }
        this.monitor.unLock();
        return null;
    }

    /**
     * Este m�todo da acceso a una entrada de conmutaci�n que provenga de un
     * nodo antecesor concreto.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param upstreamTLDPSessionID El identificador de sesi�n TLDP que el nodo antecesor
     * especifico para la entrada que estamos buscando.
     * @param incomingPortID El puerto de entrada asociado a la entrada TLDP que
     * estamos buscando. Necesario porque s�lo con el identificador TLDP del
     * antecesor no vale ya que distintos antecesores pueden otorgar el mismo
     * identificador de sesi�n TLDP.
     * @return La entrada de la switchingMatrix de conmutaci�n buscada, si la encuentra,
 o null en caso contrario.
     * @since 1.0
     */
    public TSwitchingMatrixEntry getEntryHavinUpstreamTLDPSessionID(int upstreamTLDPSessionID, int incomingPortID) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getUpstreamTLDPSessionID() == upstreamTLDPSessionID) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    this.monitor.unLock();
                    return switchingMatrixEntryAux;
                }
            }
        }
        this.monitor.unLock();
        return null;
    }

    /**
     * Este m�todo sirve para ver si hay un acierto al buscar una entrada de la
 switchingMatrix de conmutaci�n concreta o no.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID El puerto de la entrada de la switchingMatrix que buscamos.
     * @param labelOrFEC El valor de la labelAux de entrada o del campo FEC_ENTRY de la
 entrada de la switchingMatrix que buscamos.
     * @param entryType El tipo de entrada (ILM o FTN) que estamos buscando.
     * @return Devuelve true si existe una entrada coincidente en la switchingMatrix de
 conmutaci�n y false en caso contrario.
     * @since 1.0
     */
    public boolean existsEntry(int incomingPortID, int labelOrFEC, int entryType) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.monitor.unLock();
                        return true;
                    }
                }
            }
        }
        this.monitor.unLock();
        return false;
    }

    /**
     * Este m�todo elimina una entrada concreta de la switchingMatrix de conmutaci�n.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID El puerto de la entrada de la switchingMatrix que queremos borrar.
     * @param labelOrFEC El valor de la labelAux de entrada o del campo FEC_ENTRY de la
 entrada de la switchingMatrix que queremos borrar.
     * @param entryType El tipo de entrada (ILM o FTN) de la entrada de la switchingMatrix que
 deseamos borrar.
     * @since 1.0
     */
    public void removeEntry(int incomingPortID, int labelOrFEC, int entryType) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        iterator.remove();
                    }
                }
            }
        }
        this.monitor.unLock();
    }

    /**
     * Este m�todo elimina una entrada concreta de la switchingMatrix de conmutaci�n.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param localTLDPSessionID El identificador TLDP propio de la entrada de la switchingMatrix que
 deseamos eliminar.
     * @param incomingPortID El puerto de entrada de la entrada de la switchingMatrix de
 conmutaci�n que queremos eliminar.
     * @since 1.0
     */
    public void removeEntryHavingLocalTLDPSessionID(int localTLDPSessionID, int incomingPortID) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLocalTLDPSessionID() == localTLDPSessionID) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    iterator.remove();
                }
            }
        }
        this.monitor.unLock();
    }

    /**
     * Este m�todo obtiene la operaci�n que hay que realizar sobre la pila de
 etiquetas de los paquetes de un flujo concreto, seg�n la entrada de la
 switchingMatrix de conmutaci�n asociada a ese flujo.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID El puerto de la entrada de la switchingMatrix que queremos consultar.
     * @param labelOrFEC El valor de la labelAux de entrada o del campo FEC_ENTRY de la
 entrada de la switchingMatrix que queremos consultar.
     * @param entryType El tipo de entrada (ILM o FTN) de la entrada que queremos
     * consultar.
     * @return Devuelve la operaci�n a realizar sobre la pila de etiquetas. Es
 una de las constantes definidas en TSwitchingMatrixEntry.
     * @since 1.0
     */
    public int getLabelStackOperation(int incomingPortID, int labelOrFEC, int entryType) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.monitor.unLock();
                        return switchingMatrixEntryAux.getLabelStackOperation();
                    }
                }
            }
        }
        this.monitor.unLock();
        return TSwitchingMatrixEntry.UNDEFINED;
    }

    /**
     * Este m�todo obtiene la labelAux de salida para los paquetes de un flujo
 concreto, seg�n la entrada de la switchingMatrix de conmutaci�n asociada a ese
 flujo.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID El puerto de la entrada de la switchingMatrix que queremos consultar.
     * @param labelOrFEC El valor de la labelAux de entrada o del campo FEC_ENTRY de la
 entrada de la switchingMatrix que queremos consultar.
     * @param entryType El tipo de entrada (ILM o FTN) de la entrada de la switchingMatrix que
 queremos consultar.
     * @return La labelAux de salida que hay que poner a los paquetes del flujo
 asociado a la entrada de la switchingMatrix que hemos especificado.
     * @since 1.0
     */
    public int getOutgoingLabel(int incomingPortID, int labelOrFEC, int entryType) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.monitor.unLock();
                        return switchingMatrixEntryAux.getOutgoingLabel();
                    }
                }
            }
        }
        this.monitor.unLock();
        return TSwitchingMatrixEntry.UNDEFINED;
    }

    /**
     * Este m�todo obtiene el puerto de salida de los paquetes de un flujo
 concreto, seg�n la entrada de la switchingMatrix de conmutaci�n asociada a ese
 flujo.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID El puerto de la entrada de la switchingMatrix que quermos consultar.
     * @param labelOrFEC El valor de la labelAux de entrada o del campo FEC_ENTRY de la
 entrada de la switchingMatrix que queremos consultar.
     * @param entryType El tipo de entrada (ILM o FTN) de la entrada de la switchingMatrix que
 queremos consultar.
     * @return El puerto de salida para los paquetes del flujo asociado a la
 entrada de la switchingMatrix especificada.
     * @since 1.0
     */
    public int getOutgoingPortID(int incomingPortID, int labelOrFEC, int entryType) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.monitor.unLock();
                        return switchingMatrixEntryAux.getOutgoingPortID();
                    }
                }
            }
        }
        this.monitor.unLock();
        return TSwitchingMatrixEntry.UNDEFINED;
    }

    /**
     * Este m�todo comprueba si hay alguna entrada cuya labelAux de entrada
 coincide con la epecificada.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param label Etiqueta que debe tener la entrada de la switchingMatrix que estamos
 buscando.
     * @return true si existe una entrada de la switchingMatrix con esa labelAux de
 entrada, si no, false.
     * @since 1.0
     */
    public boolean existsEntryHavingLabel(int label) {
        this.monitor.lock();
        Iterator iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = (TSwitchingMatrixEntry) iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == label) {
                if (switchingMatrixEntryAux.getEntryType() == TSwitchingMatrixEntry.LABEL_ENTRY) {
                    this.monitor.unLock();
                    return true;
                }
            }
        }
        this.monitor.unLock();
        return false;
    }

    /**
     * Este m�todo devuelve una labelAux de 20 bits �nica. Generalmente para
 responder a una petici�n de labelAux realizada por otro nodo antecesor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Una labelAux para recibir tr�fico, si es posible, o la constante
 ETIQUETA_NO_DISPONIBLE, en caso de que se haya agotado el espacio de
 etiquetas del nodo.
     * @since 1.0
     */
    public int getNewLabel() {
        int labelAux = 16;
        while (labelAux <= 1048575) {
            if (!existsEntryHavingLabel(labelAux)) {
                return labelAux;
            } else {
                labelAux++;
            }
        }
        return TSwitchingMatrixEntry.LABEL_DENIED;
    }

    /**
     * Este m�todo obtiene acceso a todas las entradas de la matr�z de
     * conmutaci�n v�a el iterador de la lista enlazada que las contiene.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return El iterador de la lista enlazada que contiene las entradas de la
 switchingMatrix de conmutaci�n.
     * @since 1.0
     */
    public Iterator getEntriesIterator() {
        return this.switchingMatrix.iterator();
    }

    /**
     * Este m�todo obtiene el n�mero de entradas que hay en la switchingMatrix de
 conmutaci�n.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return N�mero de entradas de la switchingMatrix de conmutacion.
     * @since 1.0
     */
    public int getNumberOfEntries() {
        return this.switchingMatrix.size();
    }

    /**
     * Este m�todo reinicia la switchingMatrix de conmutaci�n y la deja como si acabase
 de ser creada por el constructor de la clase.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void reset() {
        this.monitor.lock();
        Iterator it = this.switchingMatrix.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.monitor.unLock();
    }

    private LinkedList switchingMatrix;
    private TMonitor monitor;
}
