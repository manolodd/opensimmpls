/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.opensimmpls.scenario;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPortSet;
import com.manolodominguez.opensimmpls.hardware.ports.TPort;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;

/**
 * This class implements a receiver node; a node that only receives traffic.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TReceiverNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * This is the constructor of the class and creates a new instance of
     * TReceiverNode.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param nodeID The node identifier that is unique in the topology.
     * @param ipv4Address IPv4 address of the node.
     * @param identifierGenerator An identifier generator that will be used to
     * generate unique identifiers for events.
     * @param topology Topology the node belongs to.
     */
    public TReceiverNode(int nodeID, String ipv4Address, TLongIDGenerator identifierGenerator, TTopology topology) {
        super(nodeID, ipv4Address, identifierGenerator, topology);
        // FIX: This method is overridable. Avoid using this method to update
        // the number of ports or make it final.
        this.setPorts(TNode.DEFAULT_NUM_PORTS_RECEIVER);
        // FIX: Use class constants instead of harcoded values.
        this.ports.setUnlimitedBuffer(true);
        this.stats = new TReceiverStats();
    }

    /**
     * Este m�todo reinicia los atributos de la clase y los deja como recien
     * creadops por el constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        this.ports.reset();
        this.stats.reset();
        this.stats.activateStats(this.isGeneratingStats());
    }

    /**
     * Este m�todo devuelve el tipo del nodo.
     *
     * @return TNode.RECEIVER, indicando que se trata de un nodo receptor.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public int getNodeType() {
        return TNode.RECEIVER;
    }

    /**
     * Este m�todo permite obtener eventos enviados desde el reloj del
     * simulador.
     *
     * @param evt Evento enviado desde el reloj principal del simulador.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent evt) {
        this.setTickDuration(evt.getStepDuration());
        this.setCurrentInstant(evt.getUpperLimit());
        this.startOperation();
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el hilo independiente del
     * nodo se pone en funcionamiento. En �l se codifica toda la funcionalidad
     * del nodo.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        recibirDatos();
        this.stats.consolidateData(this.getCurrentInstant());
        // Acciones a llevar a cabo durante el tic.
    }

    /**
     * Este m�todo lee mientras puede los paquetes que hay en el buffer de
     * recepci�n.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void recibirDatos() {
        TPort p = this.ports.getPort(0);
        long idEvt = 0;
        int tipo = 0;
        TAbstractPDU paquete = null;
        TSEPacketReceived evt = null;
        if (p != null) {
            while (p.thereIsAPacketWaiting()) {
                paquete = p.getPacket();
                try {
                    idEvt = this.longIdentifierGenerator.getNextID();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.contabilizarPaquete(paquete, true);
                evt = new TSEPacketReceived(this, idEvt, this.getCurrentInstant(), tipo, paquete.getSize());
                this.simulationEventsListener.captureSimulationEvents(evt);
                paquete = null;
            }
        }
    }

    /**
     * Este m�todo contabiliza en las estad�sticas del nodo un paquete le�do.
     *
     * @param paquete Paquete que se quiere contabilizar.
     * @param deEntrada TRUE, si el paquete entra en el nodo. FALSE si el
     * paquete sale del nodo.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void contabilizarPaquete(TAbstractPDU paquete, boolean deEntrada) {
        if (deEntrada) {
            if (paquete.getSubtype() == TAbstractPDU.MPLS) {
            } else if (paquete.getSubtype() == TAbstractPDU.MPLS_GOS) {
            } else if (paquete.getSubtype() == TAbstractPDU.IPV4) {
            } else if (paquete.getSubtype() == TAbstractPDU.IPV4_GOS) {
            }
        } else if (paquete.getSubtype() == TAbstractPDU.MPLS) {
        } else if (paquete.getSubtype() == TAbstractPDU.MPLS_GOS) {
        } else if (paquete.getSubtype() == TAbstractPDU.IPV4) {
        } else if (paquete.getSubtype() == TAbstractPDU.IPV4_GOS) {
        }
    }

    /**
     * Este m�too permite acceder directamente a los ports del nodo.
     *
     * @return El conjunto de ports del nodo.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public TPortSet getPorts() {
        return this.ports;
    }

    /**
     * Este m�todo devuelve si el nodo tiene ports libres o no.
     *
     * @return TRUE, si el nodo tiene ports libres. FALSE en caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }

    /**
     * Este m�todo devuelve el peso del nodo que debe ser tenido en cuenta por
     * los algoritmos de encaminamiento para el c�lculo de rutas.
     *
     * @return En el caso de un nodo receptor, siempre es cero.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public long getRoutingWeight() {
        return 0;
    }

    /**
     * Devuelve si el nodo est� bien configurado o no.
     *
     * @return TRUE, si el nodo esta bien configurado. FALSE en caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }

    /**
     * Este m�todo comprueba la configuraci�n del nodo y devuelve un c�digo
     * expresando dicho estado.
     *
     * @param t Topolog�a donde est� el nodo.
     * @param recfg TRUE, si se est� reconfigurando el nodo. FALSE si el nodo se
     * est� configurando por primera vez.
     * @return CORRECTA, si el nodo est� bien configurado. Un codigo de error en
     * caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public int validateConfig(TTopology t, boolean recfg) {
        this.setWellConfigured(false);
        if (this.getName().equals("")) {
            return this.SIN_NOMBRE;
        }
        boolean soloEspacios = true;
        for (int i = 0; i < this.getName().length(); i++) {
            if (this.getName().charAt(i) != ' ') {
                soloEspacios = false;
            }
        }
        if (soloEspacios) {
            return this.SOLO_ESPACIOS;
        }
        if (!recfg) {
            TNode tp = t.getFirstNodeNamed(this.getName());
            if (tp != null) {
                return this.NOMBRE_YA_EXISTE;
            }
        } else {
            TNode tp = t.getFirstNodeNamed(this.getName());
            if (tp != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return this.NOMBRE_YA_EXISTE;
                }
            }
        }
        this.setWellConfigured(true);
        this.stats.activateStats(this.isGeneratingStats());
        return this.CORRECTA;
    }

    /**
     * Este m�todo transforma el codigo de error de configuraci�n del nodo en un
     * texto entendible y explicativo.
     *
     * @param e Codigo de error.
     * @return Texto explicativo del codigo de error.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public String getErrorMessage(int e) {
        switch (e) {
            case SIN_NOMBRE:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigReceptor.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigReceptor.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TNodoReceptor.NombreNoSoloEspacios"));
        }
        return ("");
    }

    /**
     * Este m�todo transforma el nodo en una representaci�n textual volcable a
     * disco.
     *
     * @return La representaci�n textual del nodo.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public String marshall() {
        String cadena = "#Receptor#";
        cadena += this.getNodeID();
        cadena += "#";
        cadena += this.getName().replace('#', ' ');
        cadena += "#";
        cadena += this.getIPv4Address();
        cadena += "#";
        cadena += this.isSelected();
        cadena += "#";
        cadena += this.nameMustBeDisplayed();
        cadena += "#";
        cadena += this.isGeneratingStats();
        cadena += "#";
        cadena += this.getScreenPosition().x;
        cadena += "#";
        cadena += this.getScreenPosition().y;
        cadena += "#";
        return cadena;
    }

    /**
     * Este m�todo deserializa un nodo serializado pasado por par�metro,
     * reconstruyendolo en memoria en la instancia actual.
     *
     * @param elemento Nodo receptor serializado.
     * @return TRUE, si se ha podido deserializar correctamente. FALSE en caso
     * contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public boolean unMarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 10) {
            return false;
        }
        this.setNodeID(Integer.parseInt(valores[2]));
        this.setName(valores[3]);
        this.setIPv4Address(valores[4]);
        this.setSelected(Integer.parseInt(valores[5]));
        this.setShowName(Boolean.valueOf(valores[6]));
        this.setGenerateStats(Boolean.valueOf(valores[7]));
        int posX = Integer.parseInt(valores[8]);
        int posY = Integer.parseInt(valores[9]);
        this.setScreenPosition(new Point(posX + 24, posY + 24));
        return true;
    }

    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
     *
     * @return Las estad�sticas del nodo.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public TStats getStats() {
        return this.stats;
    }

    /**
     * Este m�todo permite establecer el n�mero de ports que deseamos para el
     * nodo.
     *
     * @param num El n�mero de ports del nodo. Como mucho 8.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public synchronized void setPorts(int num) {
        ports = new TFIFOPortSet(num, this);
    }

    /**
     * Este m�todo permite descartar nu paquete en el nodo y reflejar este
     * descarte en las estadisticas.
     *
     * @param paquete paquete que deseamos descartar.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void discardPacket(TAbstractPDU paquete) {
        // Un receptor no descarta paquetes, porque tiene un buffer 
        // ilimitado y no analiza el tr�fico. Lo recibe y ya est�.
        paquete = null;
    }

    /**
     * Este m�todo no hace nada en un Receptor. En un nodo activo permitir�
     * solicitar a un nodo activo la retransmisi�n de un paquete.
     *
     * @param paquete Paquete cuya retransmisi�n se est� solicitando.
     * @param pSalida Puerto por el que se enviar� la solicitud.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void runGPSRP(TMPLSPDU paquete, int pSalida) {
    }

    public static final int CORRECTA = 0;
    public static final int SIN_NOMBRE = 1;
    public static final int NOMBRE_YA_EXISTE = 2;
    public static final int SOLO_ESPACIOS = 3;

    private TReceiverStats stats;
}
