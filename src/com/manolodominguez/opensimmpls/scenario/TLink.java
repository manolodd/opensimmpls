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
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.TMonitor;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is an abstract class that will be implemented by subclasses. It is
 * a generic link within the network topology.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TLink extends TTopologyElement implements Comparable, ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It should be called by
     * subclasses to create a new instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param identifier
     * @param longIDGenerator
     * @param topology
     * @since 2.0
     */
    public TLink(int identifier, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(TTopologyElement.LINK, longIDGenerator);
        this.identifier = identifier;
        this.headEndNode = null;
        this.tailEndNode = null;
        this.showName = false;
        this.name = "";
        this.delay = 1;
        this.headEndNodePortID = -1;
        this.tailEndNodePortID = -1;
        this.buffer = Collections.synchronizedSortedSet(new TreeSet());
        this.deliveredPacketsBuffer = new TreeSet();
        this.packetsInTransitEntriesLock = new TMonitor();
        this.deliveredPacketEntriesLock = new TMonitor();
        this.topology = topology;
        this.linkIsBroken = false;
    }

    /**
     * Este m�todo averigua si el enlace est� caido o no.
     *
     * @return TRUE, si el enlace est� caido. FALSE en caso contrario.
     * @since 2.0
     */
    public boolean isBroken() {
        return this.linkIsBroken;
    }

    /**
     * Este m�todo compara la instancia actual con otro objeto del mismo tipo,
     * para averiguar la posici�n ordinal de uno con respecto al otro.
     *
     * @param anotherLink Enlace de la topolog�a con el que se va a comparar la
     * instancia actual.
     * @return -1, 0 � 1, dependiendo de si la instancia actual es menor, igual
     * o mayor que la pasada por par�metro, en t�rminos de orden.
     * @since 2.0
     */
    @Override
    public int compareTo(Object anotherLink) {
        TLink linkAux = (TLink) anotherLink;
        if (TLink.this.getID() < linkAux.getID()) {
            return -1;
        } else if (TLink.this.getID() == linkAux.getID()) {
            return 0;
        }
        return 1;
    }

    /**
     * Este m�todo calcula qu� porcentaje de tr�nsito que lleva recorrido un
     * paquete concreto, sabiendo que ha recorrido ya un porcentaje determinado.
     *
     * @param totalTransitDelay Cien por cien.
     * @param remainingDelay Equis por ciento.
     * @return Valor calculado.
     * @since 2.0
     */
    public long getCurrentTransitPercentage(long totalTransitDelay, long remainingDelay) {
        return ((totalTransitDelay - remainingDelay) * 100) / totalTransitDelay;
    }

    /**
     * Este m�todo calcula las coordenadas donde debe dibujarse un paquete que
     * ha recorrido ya un porcentaje concreto de su tr�nsito.
     *
     * @since 2.0
     * @param transitPercentage Porcentaje recorrido ya por el paquete en el
     * enlace.
     * @return Coordenadas donde dibujar el paquete.
     */
    public Point getScreenPacketPosition(long transitPercentage) {
        Point screenPacketPosition = new Point(0, 0);
        int x1 = this.headEndNode.getScreenPosition().x + 24;
        int y1 = this.headEndNode.getScreenPosition().y + 24;
        int x2 = this.tailEndNode.getScreenPosition().x + 24;
        int y2 = this.tailEndNode.getScreenPosition().y + 24;
        screenPacketPosition.x = x1;
        screenPacketPosition.y = y1;
        int distanceX = (x2 - x1);
        int distanceY = (y2 - y1);
        screenPacketPosition.x += (int) ((double) distanceX * (double) transitPercentage / (double) 100);
        screenPacketPosition.y += (int) ((double) distanceY * (double) transitPercentage / (double) 100);
        return screenPacketPosition;
    }

    /**
     * Este m�todo permite establecer la topology a la que pertenece el enlace.
     *
     * @param topology Topolog�a a la que pertenece el enlace.
     * @since 2.0
     */
    public void setTopology(TTopology topology) {
        this.topology = topology;
    }

    /**
     * Este m�todo permite obtener la topology a la que pertenece el enlace.
     *
     * @return La topolog�a a la que pertenece el enlace.
     * @since 2.0
     */
    public TTopology getTopology() {
        return this.topology;
    }

    /**
     * Este m�todo configura el enlace.
     *
     * @since 2.0
     * @param isAReconfiguration TRUE, si se est� reconfigurando el enlace.
     * FALSE si se est� configurando por primera vez.
     * @param linkConfig Objeto de configuraci�n del enlace que contiene una
     * configuraci�n para el mismo.
     * @param topology Topolog�a a la que pertenece ele enlace.
     */
    public void configure(TLinkConfig linkConfig, TTopology topology, boolean isAReconfiguration) {
        this.setName(linkConfig.getName());
        this.setShowName(linkConfig.getShowName());
        this.setDelay(linkConfig.getDelay());
        if (!isAReconfiguration) {
            this.setHeadEndNode(topology.getFirstNodeNamed(linkConfig.getHeadEndNodeName()));
            this.setTailEndNode(topology.getFirstNodeNamed(linkConfig.getTailEndNodeName()));
            this.disconnectFromPorts();
            this.setHeadEndNodePortID(linkConfig.getHeadEndNodePortID());
            TPortSet portSetAux1 = this.headEndNode.getPorts();
            if (portSetAux1 != null) {
                portSetAux1.connectLinkToPort(this, this.headEndNodePortID);
            }
            this.setTailEndNodePortID(linkConfig.getTailEndNodePortID());
            TPortSet portSetAux2 = this.tailEndNode.getPorts();
            if (portSetAux2 != null) {
                portSetAux2.connectLinkToPort(this, this.tailEndNodePortID);
            }
        }
    }

    /**
     * Este m�todo obtiene un objeto con la configuraci�n completa del enlace.
     *
     * @return Configuraci�n completa del enlace.
     * @since 2.0
     */
    public TLinkConfig getConfig() {
        TLinkConfig linkConfig = new TLinkConfig();
        linkConfig.setName(this.getName());
        linkConfig.setShowName(this.getShowName());
        if (this.getHeadEndNode() != null) {
            linkConfig.setHeadEndNodeName(this.getHeadEndNode().getName());
        }
        if (this.getTailEndNode() != null) {
            linkConfig.setTailEndNodeName(this.getTailEndNode().getName());
        }
        linkConfig.setDelay(this.getDelay());
        linkConfig.setHeadEndNodePortID(this.getHeadEndNodePortID());
        linkConfig.setTailEndNodePortID(this.getTailEndNodePortID());
        return linkConfig;
    }

    /**
     * Este m�todo libera el enlace, desconect�ndolo de los posibles nodos a los
     * que est� "enganchado".
     *
     * @since 2.0
     */
    public void disconnectFromPorts() {
        if (this.headEndNode != null) {
            TPortSet portSetAux1 = this.headEndNode.getPorts();
            if (portSetAux1 != null) {
                portSetAux1.disconnectLinkFromPort(this.headEndNodePortID);
            }
        }
        if (this.tailEndNode != null) {
            TPortSet portSetAux2 = this.tailEndNode.getPorts();
            if (portSetAux2 != null) {
                portSetAux2.disconnectLinkFromPort(this.tailEndNodePortID);
            }
        }
    }

    /**
     * Este m�todo establece el retardo del enlace.
     *
     * @param delay Retardo del enlace.
     * @since 2.0
     */
    public void setDelay(int delay) {
        if (delay <= 0) {
            this.delay = 1;
        } else {
            this.delay = delay;
        }
    }

    /**
     * Este m�todo obtiene el retardo del enlace.
     *
     * @return Retardo del enlace.
     * @since 2.0
     */
    public int getDelay() {
        return this.delay;
    }

    /**
     * Este m�todo establece el identificador �nico del enlace.
     *
     * @param identifier Identificador del enlace.
     * @since 2.0
     */
    public void getID(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Este m�todo permite obtener el identificador �nico del enlace.
     *
     * @return Identificador unico del enlace.
     * @since 2.0
     */
    public int getID() {
        return this.identifier;
    }

    /**
     * Este m�todo permite establecer el nombre del enlace.
     *
     * @param name Nombre del enlace.
     * @since 2.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Este m�todo permite obtener el nombre del enlace.
     *
     * @return Nombre del enlace.
     * @since 2.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * Este m�todo permite especificar si el nombre del enlace se ha de ver o
     * no.
     *
     * @param showName TRUE, si el nombre debe mostrarse. FALSE en caso
     * contrario.
     * @since 2.0
     */
    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    /**
     * Este m�todo permite obtener si el nombre del enlace se ha de ver o no.
     *
     * @return TRUE, si el nombre se esta mostrando. FALSE en caso contrario.
     * @since 2.0
     */
    public boolean getShowName() {
        return this.showName;
    }

    /**
     * Este m�todo obtiene el nodo que est� al final del extremo izquierdo del
     * enlace.
     *
     * @return El nodo extremo izquierdo del enlace.
     * @since 2.0
     */
    public TNode getHeadEndNode() {
        return this.headEndNode;
    }

    /**
     * Este m�todo permite establecer el nodo que estar� conectado al extremo
     * izquierdo del enlace.
     *
     * @param headEndNode Nodo que estar� conectado al extremo izquierdo del
     * enalace.
     * @since 2.0
     */
    public void setHeadEndNode(TNode headEndNode) {
        this.headEndNode = headEndNode;
    }

    /**
     * Este m�todo obtiene el nodo que est� al final del extremo derecho del
     * enlace.
     *
     * @return El nodo del extremo derecho del enlace.
     * @since 2.0
     */
    public TNode getTailEndNode() {
        return this.tailEndNode;
    }

    /**
     * Este m�todo permite establecer el nodo que estar� conectado al extremo
     * derecho del enlace.
     *
     * @param tailEndNode Nodo del extremo derecho del enlace.
     * @since 2.0
     */
    public void setTailEndNode(TNode tailEndNode) {
        this.tailEndNode = tailEndNode;
    }

    /**
     * Este m�todo obtiene la posici�n del extremo izquierdo del enlace.
     *
     * @return Posicio�n del extremo izquierdo del enlace.
     * @since 2.0
     */
    public Point getHeadEndScreenPosition() {
        return this.headEndNode.getScreenPosition();
    }

    /**
     * Este m�todo establece el puerto del nodo extremo izquierdo al que est�
     * conectado el enlace.
     *
     * @param portID Puerto del nodo extremo izquierdo.
     * @since 2.0
     */
    public void setHeadEndNodePortID(int portID) {
        this.headEndNodePortID = portID;
    }

    /**
     * Este m�todo permite obtener el puerto del nodo extremo izquierdo al que
     * est� conectado el enlace.
     *
     * @return Puerto del nodo extremo izquierdo.
     * @since 2.0
     */
    public int getHeadEndNodePortID() {
        return this.headEndNodePortID;
    }

    /**
     * Este m�todo establece el puerto del nodo extremo derecho al que est�
     * conectado el enlace.
     *
     * @param portID Puerto del nodo extremo derecho.
     * @since 2.0
     */
    public void setTailEndNodePortID(int portID) {
        this.tailEndNodePortID = portID;
    }

    /**
     * Este m�todo permite obtener el puerto del nodo extremo derecho al que
     * est� conectado el enlace.
     *
     * @return Puerto del nodo extremo derecho.
     * @since 2.0
     */
    public int getTailEndNodePortID() {
        return this.tailEndNodePortID;
    }

    /**
     * Este m�todo obtiene la posici�n del nodo extremo derecho del enlace.
     *
     * @return Posici�n del nodo extremo derecho del enlace.
     * @since 2.0
     */
    public Point getTailEndScreenPosition() {
        return this.tailEndNode.getScreenPosition();
    }

    /**
     * Este m�todo comprueba si el enlace est� conectad a un nodo concreto.
     *
     * @param node Nodo al que se desea saber si el enlace est� conectado o no.
     * @return TRUE, si est� conectado. FALSE en caso contrario.
     * @since 2.0
     */
    public boolean isConnectedTo(TNode node) {
        if (this.headEndNode.getID() == node.getID()) {
            return true;
        }
        if (this.tailEndNode.getID() == node.getID()) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo comprueba si el enlace est� conectad a un nodo concreto.
     *
     * @param nodeID Identificador del nodo al que se desea saber si el enlace
     * est� conectado o no.
     * @return TRUE, si est� conectado. FALSE en caso contrario.
     * @since 2.0
     */
    public boolean isConnectedTo(int nodeID) {
        if (this.headEndNode.getID() == nodeID) {
            return true;
        }
        if (this.tailEndNode.getID() == nodeID) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo coloca un paquete desde el enlace al nodo destino.
     *
     * @param packet Paquete que se desea trasladar.
     * @param nodeID Nodo destino del paquete en el enlace.
     * @since 2.0
     */
    public void deliverPacketToNode(TAbstractPDU packet, int nodeID) {
        this.packetsInTransitEntriesLock.lock();
        this.buffer.add(new TLinkBufferEntry(packet, this.getDelay(), nodeID));
        this.packetsInTransitEntriesLock.unLock();
    }

    /**
     * Este m�todo comprueba si dada unas coordenadas, el enlace pasa por dicha
     * posici�n.
     *
     * @param screenPosition Posici�n.
     * @return TRUE, si el enlace pasa por esa posici�n. FALSE en caso
     * contrario.
     * @since 2.0
     */
    public boolean crossesScreenPosition(Point screenPosition) {
        int x1 = this.headEndNode.getScreenPosition().x + 24;
        int y1 = this.headEndNode.getScreenPosition().y + 24;
        int x2 = this.tailEndNode.getScreenPosition().x + 24;
        int y2 = this.tailEndNode.getScreenPosition().y + 24;
        int dx, dy, steps, k;
        double incrementX, incrementY, x, y;

        if ((x1 == x2) && (y1 == y2)) // Para l�neas que son s�lo un punto.
        {
            if ((screenPosition.x == x1) && (screenPosition.y == y1)) {
                return true;
            }
        } else // Para el resto de l�neas.
        {
            dx = x2 - x1;
            dy = y2 - y1;
            if (Math.abs(dx) > Math.abs(dy)) {
                steps = Math.abs(dx);
            } else {
                steps = Math.abs(dy);
            }
            incrementX = (float) dx / steps;
            incrementY = (float) dy / steps;
            x = x1;
            y = y1;

            if ((x >= screenPosition.x - 3) && (x <= screenPosition.x + 3)
                    && (y >= screenPosition.y - 3) && (y <= screenPosition.y + 3)) {
                return true;
            }
            for (k = 1; k <= steps; k++) {
                x += incrementX;
                y += incrementY;
                if ((x >= screenPosition.x - 3) && (x <= screenPosition.x + 3)
                        && (y >= screenPosition.y - 3) && (y <= screenPosition.y + 3)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Este m�todo devuelve el packetsInTransitEntriesLock del enlace.
     *
     * @return El packetsInTransitEntriesLock del enlace.
     * @since 2.0
     */
    public TMonitor getLock() {
        return this.packetsInTransitEntriesLock;
    }

    /**
     * Este m�todo comprueba cu�l de los dos extremos del enlace es el nodo
     * pasado por par�metro.
     *
     * @param node Nodo que realiza la consulta.
     * @return END_NODE_1, si el nodo es el extremo 1. END_NODE_2 si es el
     * extremo 2.
     * @since 2.0
     */
    public int whichEndIs(TNode node) {
        if (node.getID() == this.headEndNode.getID()) {
            return TLink.HEAD_END_NODE;
        }
        return TLink.TAIL_END_NODE;
    }

    /**
     * Este m�todo calcula a qu� extremo del enlace se debe enviar un paquete
     * dado el nodo quelo env�a.
     *
     * @param node Nodo que env�a el paquete y que hace la consulta.
     * @return END_NODE_1 si el paquete debe ir al extremo 1. EXTREMO 2 si debe
     * ir al extremo 2.
     * @since 2.0
     */
    public int getDestinationOfTrafficSentBy(TNode node) {
        if (node.getID() == this.headEndNode.getID()) {
            return TLink.TAIL_END_NODE;
        }
        return TLink.HEAD_END_NODE;
    }

    public abstract int getLinkType();

    @Override
    public abstract void receiveTimerEvent(TTimerEvent evt);

    @Override
    public abstract void run();

    public abstract long getWeight();

    public abstract long getRABANWeight();

    @Override
    public abstract boolean isWellConfigured();

    @Override
    public abstract String getErrorMessage(int error);

    @Override
    public abstract String marshall();

    @Override
    public abstract boolean unMarshall(String serializedLink);

    public abstract void setAsBrokenLink(boolean isBroken);

    @Override
    public abstract void reset();

    public static final int INTERNAL = 0;
    public static final int EXTERNAL = 1;
    public static final int HEAD_END_NODE = 1;
    public static final int TAIL_END_NODE = 2;

    private int identifier;
    private TNode headEndNode;
    private TNode tailEndNode;
    private int headEndNodePortID;
    private int tailEndNodePortID;
    private String name;
    private boolean showName;
    private int delay;

    protected SortedSet buffer;
    protected TreeSet deliveredPacketsBuffer;
    protected TMonitor packetsInTransitEntriesLock;
    protected TMonitor deliveredPacketEntriesLock;
    protected TTopology topology;
    protected boolean linkIsBroken;

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int ONLY_BLANK_SPACES = 2;
    public static final int NAME_ALREADY_EXISTS = 3;
    public static final int HEAD_END_NODE_PORT_MISSING = 4;
    public static final int TAIL_END_NODE_PORT_MISSING = 5;
    public static final int HEAD_END_NODE_MISSING = 6;
    public static final int TAIL_END_NODE_MISSING = 7;
}
