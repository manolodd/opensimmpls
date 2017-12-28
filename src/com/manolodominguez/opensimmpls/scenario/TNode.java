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
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.TMonitor;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;

/**
 * Esta es una superclase abstracta de la cual deben heredar todos los nodos
 * axistentes en el simulador.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TNode extends TTopologyElement implements Comparable, ITimerEventListener, Runnable {

    /**
     * Crea una nueva instancia de TNodoTopologia.
     *
     * @since 2.0
     * @param nodeID Identificador unico para el nodo en la topology.
     * @param ipv4Address Direcci�n IP del nodo.
     * @param identifierGenerator Generador de identificadores para los eventos
     * que deba emitir el nodo.
     * @param topology Topologia donde se encuentra el nodo inclu�do.
     */
    public TNode(int nodeID, String ipv4Address, TLongIDGenerator identifierGenerator, TTopology topology) {
        super(TTopologyElement.NODO, identifierGenerator);
        // FIX: Do not use harcoded values. Use class constants in all cases.
        this.screenPosition = new Point(0, 0);
        this.nodeID = nodeID;
        this.name = "";
        this.selected = UNSELECTED;
        this.showName = false;
        this.ipv4Address = ipv4Address;
        this.ports = null;
        this.lock = new TMonitor();
        this.topology = topology;
        this.generateStatistics = false;
        this.availableNs = 0;
        this.tickNanoseconds = 0;
        this.ticksWithoutEmittingBeforeAlert = 0;
    }

    /**
     * Este m�todo permite establecer las estad�sticas del nodo.
     *
     * @param generateStatistics Estad�sticas para el nodo.
     * @since 2.0
     */
    public void setGenerateStats(boolean generateStatistics) {
        this.generateStatistics = generateStatistics;
    }

    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
     *
     * @return las estad�sticas del nodo.
     * @since 2.0
     */
    public boolean isGeneratingStats() {
        return this.generateStatistics;
    }

    /**
     * Este m�todo permite comparar para ordenar, este nodo con cualquier otro.
     *
     * @param anotherNode Nodo con el que se va a comparar.
     * @return -1, 0 o 1, dependiendo de si ordinalmente el nodo actual es
     * menor, igual o mayor que el pasado por par�metros.
     * @since 2.0
     */
    public int compareTo(Object anotherNode) {
        TNode n = (TNode) anotherNode;
        if (getID() < n.getID()) {
            return -1;
        } else if (getID() == n.getID()) {
            return 0;
        }
        return 1;
    }

    /**
     * Eeste m�todo permite establecer la topology donde se encuentra el nodo.
     *
     * @since 2.0
     * @param topology Topolog�a donde se encuentra el nodo.
     */
    public void ponerTopologia(TTopology topology) {
        this.topology = topology;
    }

    /**
     * Este m�todo permite obtener la topology donde se encuentra el nodo.
     *
     * @return Topolog�a donde se encuentra el nodo.
     * @since 2.0
     */
    public TTopology obtenerTopologia() {
        return this.topology;
    }

    /**
     * Este m�todo permite obtener el nombre del nodo.
     *
     * @return Nombre del nodo.
     * @since 2.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * Este m�todo permite establecer el nombre del nodo.
     *
     * @since 2.0
     * @param name nombre deseado para el nodo.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Este m�todo permite obtener el identificador del nodo.
     *
     * @return El identificador unico del nodo.
     * @since 2.0
     */
    public int getID() {
        return this.nodeID;
    }

    /**
     * Este m�todo permite establecer el identificador del nodo.
     *
     * @param nodeID El identificador unico del nodo.
     * @since 2.0
     */
    public void setID(int nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * Este m�todo permite obtener la posici�n del panel de simulaci�n donde se
     * encuentra el nodo.
     *
     * @return Las coordenadas del panel de simulaci�n donde se encuentra el
     * nodo.
     * @since 2.0
     */
    public Point getScreenPosition() {
        return this.screenPosition;
    }

    /**
     * Este m�topdo permite establecer las coordenadas del panel de simulaci�n
     * donde se mostrar� el nodo.
     *
     * @param screenPosition Las coordenadas del panel de simulaci�n elegidas
     * para el nodo.
     * @since 2.0
     */
    public void setScreenPosition(Point screenPosition) {
        this.screenPosition.x = screenPosition.x - (TNode.ICONS_WIDTH / 2);
        this.screenPosition.y = screenPosition.y - (TNode.ICONS_HEIGHT / 2);
    }

    /**
     * Este m�todo permite obtener si el nodo est� en una posici�n dada o no.
     *
     * @param p Coordenadas donde queremos saber si est� el nodo o no.
     * @return TRUE, si el nodo est� en ess coordenadas. FALSE en caso
     * contrario.
     * @since 2.0
     */
    public boolean estaEnPosicion(Point p) {
        if ((p.x >= this.screenPosition.x) && (p.x <= (this.screenPosition.x + TNode.ICONS_WIDTH))
                && (p.y >= this.screenPosition.y) && (p.y <= (this.screenPosition.y + TNode.ICONS_HEIGHT))) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo permite obtener si el nodo est� seleccionado o no.
     *
     * @return SELECCIONADO, si el nodo est� seleccionado. DESELECCIONADO en
     * caso contrario.
     * @since 2.0
     */
    public int isSelected() {
        return this.selected;
    }

    /**
     * Este m�todo permite seleccionar o deseleccionar el nodo.
     *
     * @param selected SELECCIONADO, si queremos que el nodo est� seleccionado.
     * DESELECCIONADO en caso contrario.
     * @since 2.0
     */
    public void setSelected(int selected) {
        this.selected = selected;
    }

    /**
     * Este m�todo permite establecer si queremos que se muestre el nombre del
     * nodo en la pantalla o no.
     *
     * @param showName TRUE, si queremos ver el nombre del nodo. FALSE en caso
     * contrario.
     * @since 2.0
     */
    public void setShowName(boolean showName) {
        if (name.length() == 0) {
            this.showName = false;
        } else {
            this.showName = showName;
        }
    }

    /**
     * Este m�todo permite saber si se est� mostrando el nombre del nodo en la
     * pantalla o no.
     *
     * @return TRUE, si se est� mostrando el nombre del nodo. FALSE en caso
     * contrario.
     * @since 2.0
     */
    public boolean nameMustBeDisplayed() {
        return this.showName;
    }

    /**
     * Este m�todo permie obtener la direcci�n IP del nodo.
     *
     * @return La direcci�n IP del nodo.
     * @since 2.0
     */
    public String getIPv4Address() {
        return this.ipv4Address;
    }

    /**
     * este m�todo permite establecer la direcci�n IP del nodo.
     *
     * @param ipv4Address Direcci�n IP deseada para el nodo.
     * @since 2.0
     */
    public void setIPAddress(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    /**
     * Este m�todo permite establecer el n�mero de ports que tendr� el nodo.
     *
     * @param numPorts El n�mero de ports deseados para el nodo. 8 como mucho.
     * @since 2.0
     */
    public abstract void setPorts(int numPorts);

    /**
     * Este m�todo permite poner un paquete en el buffer de entrada del nodo.
     *
     * @param packet Paquete que deseamo poner.
     * @param portID Puerto del conjunto de ports en el que deeamos depositar el
     * paquete.
     * @since 2.0
     */
    public synchronized void putPacket(TAbstractPDU packet, int portID) {
        lock.lock();
        this.ports.getPort(portID).addPacket(packet);
        lock.unLock();
    }

    /**
     * Este m�todo incrementa en 1 el n�mero de tics que hace que el nodo no
     * emite un paquete.
     *
     * @since 2.0
     */
    public void increaseTicksWithoutEmitting() {
        this.ticksWithoutEmittingBeforeAlert++;
    }

    /**
     * Este m�todo coloca a cero el n�mero de pasos que el nodo ha estado sin
     * emitir un paquete.
     *
     * @since 2.0
     */
    public void resetTicksWithoutEmitting() {
        this.ticksWithoutEmittingBeforeAlert = 0;
    }

    /**
     * Este m�todo obtiene el n�mero pasos que el nodo lleva sin emitir un
     * paquete.
     *
     * @return El n�mero de pasos
     * @since 2.0
     */
    public int getTicksWithoutEmitting() {
        return this.ticksWithoutEmittingBeforeAlert;
    }

    /**
     * Este m�todo permite descartar un paquete en el nodo.
     *
     * @param packet Paquete que deseamos descartar.
     * @since 2.0
     */
    public abstract void discardPacket(TAbstractPDU packet);

    /**
     * Este m�todo permite acceder directamente a los ports del nodo.
     *
     * @return El conjunto de ports del nodo.
     * @since 2.0
     */
    public abstract TPortSet getPorts();

    /**
     * Este m�todo permite obtener eventos de sincronizaci�n del reloj principal
     * del simulador.
     *
     * @param timerEvent Evento enviado por el reloj principal del simulador.
     * @since 2.0
     */
    @Override
    public abstract void receiveTimerEvent(TTimerEvent timerEvent);

    /**
     * Este m�todo permite obtener el tipo de nodo al que pertenece la instancia
     * actual.
     *
     * @return El tipo del nodo. Una de las constantes definidas en la clase.
     * @since 2.0
     */
    public abstract int getNodeType();

    /**
     * Este m�todo se ponen en funcionamiento cuando el hilo independiente del
     * nodo entra en funcionamiento. En �l se codifica toda la funcionalidad del
     * nodo.
     *
     * @since 2.0
     */
    @Override
    public abstract void run();

    /**
     * Este m�todo debe ser implementado. Permitir� solicitar a un nodo activo
     * la retransmisi�n de un paquete.
     *
     * @param packet Paquete cuya retransmisi�n se est� solicitando.
     * @param outgoingPortID Puerto por el que se enviar� la solicitud.
     * @since 2.0
     */
    // FIX: This should be an abstract method of a new abstract class called
    // TActiveNode. TLERNode and TLSRNode should inherit from TNode and 
    // TActiveLERNode and TActiveLSRNode should inherit from this new 
    // TActiveNode class
    public abstract void runGPSRP(TMPLSPDU packet, int outgoingPortID);

    /**
     * Este m�todo averigua si al nodo le quedan ports libre o no.
     *
     * @return TRUE, si quedan ports libres al nodo. FALSE en caso contrario.
     * @since 2.0
     */
    public abstract boolean hasAvailablePorts();

    /**
     * Este m�todo devuelve el peso del nodo, que debe ser tenido en cuenta por
     * los algoritmos de encaminamiento.
     *
     * @since 2.0
     * @return El peso del nodo.
     */
    public abstract long getRoutingWeight();

    /**
     * Este m�todo devuelve si el nodo est� bien configurado o no.
     *
     * @return TRUE, si el nodo est� bien configurado. FALSE en caso contrario.
     * @since 2.0
     */
    @Override
    public abstract boolean isWellConfigured();

    /**
     * Este m�todo comprueba la configuraci�n del nodo y devuelve un codigo que
     * expresa esta situaci�n.
     *
     * @param topology Topolog�a donde se encuentra el nodo.
     * @param reconfiguration TRUE, si se est� reconfigurando el nodod. FALSE si
     * se est� configurando por primera vez.
     * @return CORRECTA, si la configuraci�n es correcta. Un c�digo de error en
     * caso contrario.
     * @since 2.0
     */
    public abstract int validateConfig(TTopology topology, boolean reconfiguration);

    /**
     * Este m�todo transofmra el c�digo de error de la configuraci�n de un nodo
     * en un texto inteligible.
     *
     * @param errorCode C�digo de error.
     * @return Un texto explicando el codigo de error.
     * @since 2.0
     */
    @Override
    public abstract String getErrorMessage(int errorCode);

    /**
     * Este m�todo transforma el nodo en una repreentaci�n de texto que se puede
     * volcar a disco.
     *
     * @return La cadena de texto que representa al nodo.
     * @since 2.0
     */
    @Override
    public abstract String marshall();

    /**
     * Este m�todo toma un nodo serializado y lo deserializa.
     *
     * @param serializedElement Elemento serializado (texto).
     * @return TRUE, si se consigue deserializar sin problemas. FALSe en caso
     * contrario.
     * @since 2.0
     */
    @Override
    public abstract boolean unMarshall(String serializedElement);

    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
     *
     * @return Estad�sticas del nodo.
     * @since 2.0
     */
    public abstract TStats getStats();

    /**
     * Este m�todo reinicia los atributos de la clase, dej�ndolos como recien
     * iniciados por el constructor.
     *
     * @since 2.0
     */
    @Override
    public abstract void reset();

    private static final int ICONS_WIDTH = 48;
    private static final int ICONS_HEIGHT = 48;

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;
    public static final int LER = 2;
    public static final int ACTIVE_LER = 3;
    public static final int LSR = 4;
    public static final int ACTIVE_LSR = 5;

    public static final int UNSELECTED = 0;
    public static final int SELECTED = 1;

    public static final int DEFAULT_NUM_PORTS_SENDER = 1;
    public static final int DEFAULT_NUM_PORTS_RECEIVER = 1;
    public static final int DEFAULT_NUM_PORTS_LER = 8;
    public static final int DEFAULT_NUM_PORTS_ACTIVE_LER = 8;
    public static final int DEFAULT_NUM_PORTS_LSR = 8;
    public static final int DEFAULT_NUM_PORTS_ACTIVE_LSR = 8;

    public static final int MAX_STEP_WITHOUT_EMITTING_BEFORE_ALERT = 25;

    private int nodeID;
    private int selected;
    private String name;
    private Point screenPosition;
    private boolean showName;
    private String ipv4Address;
    private TMonitor lock;
    private boolean generateStatistics;
    private int ticksWithoutEmittingBeforeAlert = 0;

    protected TPortSet ports;
    protected TTopology topology;
    protected int tickNanoseconds;
}
