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

import com.manolodominguez.opensimmpls.scenario.simulationevents.ESimulationSingleSubscriber;
import com.manolodominguez.opensimmpls.hardware.timer.TTimer;
import com.manolodominguez.opensimmpls.utils.TIPv4AddressGenerator;
import com.manolodominguez.opensimmpls.utils.TLock;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class implements a network topology: nodes, links and the required
 * algorithms to compute routes using Floyd algorithm and RABAN algorithm. For
 * RABAN algorithm see "Guarantee of Service Support (GoS) over MPLS using
 * Active Techniques" proposal.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTopology {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TTopology.
     *
     * @param parentScenario Scenario to wich the topology belongs to.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTopology(TScenario parentScenario) {
        this.nodes = new TreeSet();
        this.links = new TreeSet();
        this.timer = new TTimer();
        this.parentScenario = parentScenario;
        this.eventIDGenerator = new TLongIDGenerator();
        this.elementsIDGenerator = new TIDGenerator();
        this.ipv4AddressGenerator = new TIPv4AddressGenerator();
        this.floydAlgorithmLock = new TLock();
        this.rabanAlgorithmLock = new TLock();
    }

    /**
     * This method restart all attributes of the class as when it was
     * instantiated.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.floydAlgorithmLock.lock();
        Iterator nodesIterator;
        nodesIterator = this.nodes.iterator();
        TTopologyElement topologyElement;
        while (nodesIterator.hasNext()) {
            topologyElement = (TTopologyElement) nodesIterator.next();
            topologyElement.reset();
        }
        nodesIterator = this.links.iterator();
        while (nodesIterator.hasNext()) {
            topologyElement = (TTopologyElement) nodesIterator.next();
            topologyElement.reset();
        }
        this.timer.reset();
        this.eventIDGenerator.reset();
        this.floydAlgorithmLock.unLock();
        this.rabanAlgorithmLock.unLock();
    }

    /**
     * This method adds a new node to the topology.
     *
     * @param node Node to be added to the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void addNode(TNode node) {
        this.nodes.add(node);
        this.timer.addTimerEventListener(node);
        try {
            node.addSimulationListener(this.parentScenario.getSimulation().getSimulationEventListener());
        } catch (ESimulationSingleSubscriber e) {
            System.out.println(e.toString());
        }
    }

    /**
     * This method adds a new node to the topology.
     *
     * @param nodeID Node to be added to the topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void removeNode(int nodeID) {
        boolean done = false;
        TNode node = null;
        Iterator nodesIterator = this.nodes.iterator();
        while ((nodesIterator.hasNext()) && (!done)) {
            node = (TNode) nodesIterator.next();
            if (node.getNodeID() == nodeID) {
                node.markForDeletionAsTimerEventListener(true);
                nodesIterator.remove();
                done = true;
            }
        }
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * Este m�todo obtiene un nodo de la topology seg�n si identificador.
     *
     * @param nodeID Identificador del nodo que deseamos obtener.
     * @return Nodo que busc�bamos.NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getNode(int nodeID) {
        TNode nodo = null;
        Iterator iterador = this.nodes.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getNodeID() == nodeID) {
                return nodo;
            }
        }
        return null;
    }

    /**
     * Este m�todo obtiene un nodo de la topology por su direcci�n IP.
     *
     * @param ipv4Address IP del nodo que deseamos obtener.
     * @return Nodo que busc�bamos.NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getNode(String ipv4Address) {
        TNode nodo = null;
        Iterator iterador = this.nodes.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getIPv4Address().equals(ipv4Address)) {
                return nodo;
            }
        }
        return null;
    }

    /**
     * Este m�todo obtiene el primer nodo de la topolog�a que encuentre, cuyo
     * nombre sea el mismo que el especificado como par�metro.
     *
     * @param nodeName Nombre del nodo que deseamos obtener.
     * @return Nodo que busc�bamos. NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getFirstNodeNamed(String nodeName) {
        TNode nodo = null;
        Iterator iterador = this.nodes.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getName().equals(nodeName)) {
                return nodo;
            }
        }
        return null;
    }

    /**
     * Este m�todo comprueba si existe m�s de un nodo con el mismo nombre.
     *
     * @param nodeName Nombre del nodo.
     * @return TRUE, si existe m�s de un nodo con el mismo nombre. FALSE en caso
     * contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereMoreThanANodeNamed(String nodeName) {
        int cuantos = 0;
        TNode nodo = null;
        Iterator iterador = this.nodes.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getName().equals(nodeName)) {
                cuantos++;
            }
            if (cuantos > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo comprueba si existe m�s de un enlace con el mismo nombre.
     *
     * @param linkName Nombre del enlace.
     * @return TRUE si existe m�s de un enlace con el mismo nombre. FALSE en
     * caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereMoreThanALinkNamed(String linkName) {
        int cuantos = 0;
        TLink enlace = null;
        Iterator iterador = this.links.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.getName().equals(linkName)) {
                cuantos++;
            }
            if (cuantos > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo comprueba si en la topoliog�a hay algun emisor de tr�fico que
     * dirija su tr�fico al receptor especificado por parametros.
     *
     * @param trafficSinkNode Nodo receptor.
     * @return TRUE, si algun exmisor env�a tr�fico al receptor. FALSE en caso
     * contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereAnyNodeGeneratingTrafficFor(TTrafficSinkNode trafficSinkNode) {
        TNode nodo = null;
        TTrafficGeneratorNode emisor = null;
        Iterator iterador = this.nodes.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getNodeType() == TNode.SENDER) {
                emisor = (TTrafficGeneratorNode) nodo;
                if (emisor.getTargetIPv4Address().equals(trafficSinkNode.getIPv4Address())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Este m�todo obtiene el primer enlace de la topolog�a con el nombre igual
     * al especificado como par�metro.
     *
     * @param linkName Nombre del enlace buscado.
     * @return El enlace buscado. NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getFirstLinkNamed(String linkName) {
        TLink enlace = null;
        Iterator iterador = this.links.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.getName().equals(linkName)) {
                return enlace;
            }
        }
        return null;
    }

    /**
     * Este m�todo obtiene el nodo cuya posici�n en el panel de dise�o coincida
     * con la especificada como par�metro.
     *
     * @param screenPosition Coordenadas del nodo que deseamos buscar.
     * @return El nodo buscado. NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode getNodeInScreenPosition(Point screenPosition) {
        TNode nodo = null;
        Iterator iterador = this.nodes.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.isInScreenPosition(screenPosition)) {
                return nodo;
            }
        }
        return null;
    }

    /**
     * Este m�todo devuelve un vector con todos los nodos de la topology.
     *
     * @return Un vector con todos los nodos de la topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TNode[] getNodesAsArray() {
        return (TNode[]) this.nodes.toArray();
    }

    /**
     * Este m�todo toma un nodo por parametro y actualiza el mismo en la
     * topology, con los valores pasados.
     *
     * @param modifiedNode Nodo que queremos actualizar, con los nuevos valores.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void modifyNode(TNode modifiedNode) {
        boolean fin = false;
        TNode nodoBuscado = null;
        Iterator iterador = this.nodes.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            nodoBuscado = (TNode) iterador.next();
            if (nodoBuscado.getNodeID() == modifiedNode.getNodeID()) {
                if (modifiedNode.getNodeType() == TNode.SENDER) {
                    TTrafficGeneratorNode nodoTrasCast = (TTrafficGeneratorNode) nodoBuscado;
                    nodoTrasCast.setName(modifiedNode.getName());
                    nodoTrasCast.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.RECEIVER) {
                    TTrafficGeneratorNode nodoTrasCast = (TTrafficGeneratorNode) nodoBuscado;
                    nodoTrasCast.setName(modifiedNode.getName());
                    nodoTrasCast.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.RECEIVER) {
                    TTrafficSinkNode nodoTrasCast = (TTrafficSinkNode) nodoBuscado;
                    nodoTrasCast.setName(modifiedNode.getName());
                    nodoTrasCast.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.LER) {
                    TLERNode nodoTrasCast = (TLERNode) nodoBuscado;
                    nodoTrasCast.setName(modifiedNode.getName());
                    nodoTrasCast.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.ACTIVE_LER) {
                    TActiveLERNode nodoTrasCast = (TActiveLERNode) nodoBuscado;
                    nodoTrasCast.setName(modifiedNode.getName());
                    nodoTrasCast.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.LSR) {
                    TLSRNode nodoTrasCast = (TLSRNode) nodoBuscado;
                    nodoTrasCast.setName(modifiedNode.getName());
                    nodoTrasCast.setScreenPosition(modifiedNode.getScreenPosition());
                } else if (modifiedNode.getNodeType() == TNode.ACTIVE_LSR) {
                    TActiveLSRNode nodoTrasCast = (TActiveLSRNode) nodoBuscado;
                    nodoTrasCast.setName(modifiedNode.getName());
                    nodoTrasCast.setScreenPosition(modifiedNode.getScreenPosition());
                }
                fin = true;
            }
        }
    }

    /**
     * Este m�todo inserta un nuevo enlace en la topology.
     *
     * @param link Enlace que deseamos insertar.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void addLink(TLink link) {
        this.links.add(link);
        this.timer.addTimerEventListener(link);
        try {
            link.addSimulationListener(this.parentScenario.getSimulation().getSimulationEventListener());
        } catch (ESimulationSingleSubscriber e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Este m�todo elimina de la topology el enlace cuyo identificador es el
     * especificado por par�metros.
     *
     * @param linkID Identificador del enlace que deseamos eliminar.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeLink(int linkID) {
        boolean fin = false;
        TLink enlace = null;
        Iterator iterador = this.links.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            enlace = (TLink) iterador.next();
            if (enlace.getID() == linkID) {
                enlace.disconnectFromBothNodes();
                enlace.markForDeletionAsTimerEventListener(true);
                iterador.remove();
                fin = true;
            }
        }
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * Este m�todo elimina de la topolog�a el enlace pasado por par�metro.
     *
     * @param link El enlace que deseamos eliminar.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeLink(TLink link) {
        TTopology.this.removeLink(link.getID());
    }

    /**
     * Este m�todo obtiene un enlace de la topology, cuyo identificador coincide
     * con el especificado por parametros.
     *
     * @param linkID Identificador del enlace que deseamos obtener.
     * @return El enlace que dese�bamos obtener. NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getLink(int linkID) {
        TLink enlace = null;
        Iterator iterador = this.links.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.getID() == linkID) {
                return enlace;
            }
        }
        return null;
    }

    /**
     * Este m�todo permite obtener un enlace cuyas coordenadas en la ventana de
     * simulaci�n coincidan con las pasadas por parametro.
     *
     * @param screenPosition Coordenadas del enlace buscado.
     * @return El enlace buscado. NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getLinkInScreenPosition(Point screenPosition) {
        TLink enlace = null;
        Iterator iterador = this.links.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.crossesScreenPosition(screenPosition)) {
                return enlace;
            }
        }
        return null;
    }

    /**
     * Este m�todo devuelve un vector con todos los enlaces de la topology.
     *
     * @return Un vector con todos los enlaces de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink[] getLinksAsArray() {
        return (TLink[]) this.links.toArray();
    }

    /**
     * Este m�todo actualiza un enlace de la topolog�a con otro pasado por
     * par�metro.
     *
     * @param modifiedLink Enlace que queremos actualizar, con los valores
     * nuevos.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void modifyLink(TLink modifiedLink) {
        boolean fin = false;
        TLink enlaceBuscado = null;
        Iterator iterador = this.links.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            enlaceBuscado = (TLink) iterador.next();
            if (enlaceBuscado.getID() == modifiedLink.getID()) {
                if (enlaceBuscado.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink enlaceTrasCast = (TExternalLink) enlaceBuscado;
                    enlaceTrasCast.setHeadEndNode(modifiedLink.getHeadEndNode());
                    enlaceTrasCast.setTailEndNode(modifiedLink.getTailEndNode());
                } else if (modifiedLink.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink enlaceTrasCast = (TInternalLink) enlaceBuscado;
                    enlaceTrasCast.setHeadEndNode(modifiedLink.getHeadEndNode());
                    enlaceTrasCast.setTailEndNode(modifiedLink.getTailEndNode());
                }
                fin = true;
            }
        }
    }

    /**
     * @param screenPosition
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return
     */
    private boolean isThereAnElementInScreenPosition(Point screenPosition) {
        if (getNodeInScreenPosition(screenPosition) != null) {
            return true;
        }
        if (getLinkInScreenPosition(screenPosition) != null) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo permite obtener un elemento de la topolog�a cuyas coordenadas
     * en la ventana de simulaci�n coincidan con las pasadas por parametro.
     *
     * @param screenPosition Coordenadas del elemento buscado.
     * @return El elemento buscado. NULL si no existe.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public TTopologyElement getElementInScreenPosition(Point screenPosition) {
        if (isThereAnElementInScreenPosition(screenPosition)) {
            TNode n;
            n = getNodeInScreenPosition(screenPosition);
            if (n != null) {
                return n;
            }

            TLink e;
            e = getLinkInScreenPosition(screenPosition);
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    /**
     * Este m�todo elimina de la topology un nodo cuyo identificador coincida
     * con el especificado por par�metros.
     *
     * @param nodeID identificador del nodo a borrar.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeNodeAndPropagate(int nodeID) {
        TLink enlace = null;
        Iterator iterador = this.links.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.isConnectedTo(nodeID)) {
                enlace.disconnectFromBothNodes();
                enlace.markForDeletionAsTimerEventListener(true);
                iterador.remove();
            }
        }
        TTopology.this.removeNode(nodeID);
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * Este m�todo elimina de la topolog�a el nodo especificado por parametros.
     *
     * @param node Nodo que deseamos eliminar.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeNode(TNode node) {
        removeNodeAndPropagate(node.getNodeID());
    }

    /**
     * Este m�todo devuelve un iterador que permite navegar por los nodos de
     * forma sencilla.
     *
     * @return El iterador de los nodos de la topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public Iterator getNodesIterator() {
        return this.nodes.iterator();
    }

    /**
     * Este m�todo devuelve un iterador que permite navegar por los enlaces de
     * forma sencilla.
     *
     * @return El iterador de los enlaces de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public Iterator getLinksIterator() {
        return this.links.iterator();
    }

    /**
     * Este m�todo limpia la topology, eliminando todos los enlaces y nodos
     * existentes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeAllElements() {
        Iterator it = this.getLinksIterator();
        TNode n;
        TLink e;
        while (it.hasNext()) {
            e = (TLink) it.next();
            e.disconnectFromBothNodes();
            e.markForDeletionAsTimerEventListener(true);
            it.remove();
        }
        it = this.getNodesIterator();
        while (it.hasNext()) {
            n = (TNode) it.next();
            n.markForDeletionAsTimerEventListener(true);
            it.remove();
        }
        this.timer.purgeTimerEventListenersMarkedForDeletion();
    }

    /**
     * Este m�todo devuelve el n�mero de nodos que hay en la topolog�a.
     *
     * @return N�mero de nodos de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getNumberOfNodes() {
        return this.nodes.size();
    }

    /**
     * Este m�todo permite establecer el reloj principal que controlar� la
     * topology.
     *
     * @param timer El reloj principal de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTimer(TTimer timer) {
        this.timer = timer;
    }

    /**
     * Este m�todo permite obtener el reloj principal de la topology.
     *
     * @return El reloj principal de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTimer getTimer() {
        return this.timer;
    }

    /**
     * Este m�todo permite obtener el retardo menor de todos los enlaces de la
     * topology.
     *
     * @return El retardo menor de todos los enlaces de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getMinimumDelay() {
        Iterator it = this.getLinksIterator();
        TLink e;
        int minimoDelay = 0;
        int delayAux = 0;
        while (it.hasNext()) {
            e = (TLink) it.next();
            if (minimoDelay == 0) {
                minimoDelay = e.getDelay();
            } else {
                delayAux = e.getDelay();
                if (delayAux < minimoDelay) {
                    minimoDelay = delayAux;
                }
            }
        }
        if (minimoDelay == 0) {
            minimoDelay = 1;
        }
        return minimoDelay;
    }

    /**
     * Este m�todo permite averiguar si existe un enlace entre dos nodos con
     * identificadores los pasados por parametros.
     *
     * @param node1ID Identificador del nodo extremo 1.
     * @param node2ID Identificador del nodo extremo 2.
     * @return TRUE, si existe un enlace entre extremo1 y extremo 2. FALSE en
     * caso contrario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isThereAnyLinkThatJoins(int node1ID, int node2ID) {
        TLink enlace = null;
        Iterator iterador = links.iterator();
        TNode izquierdo;
        TNode derecho;
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            izquierdo = enlace.getHeadEndNode();
            derecho = enlace.getTailEndNode();
            if ((derecho.getNodeID() == node1ID) && (izquierdo.getNodeID() == node2ID)) {
                return true;
            }
            if ((derecho.getNodeID() == node2ID) && (izquierdo.getNodeID() == node1ID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo permite obtener el enlace entre dos nodos con identificadores
     * los pasados por parametros.
     *
     * @param node1ID Identificador del nodo extremo 1.
     * @param node2ID Identificador del nodo extremo 2.
     * @return El enlace entre extremo 1 y extremo 2. NULL si no existe.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLink getLinkThatJoins(int node1ID, int node2ID) {
        TLink enlace = null;
        Iterator iterador = this.links.iterator();
        TNode izquierdo;
        TNode derecho;
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            izquierdo = enlace.getHeadEndNode();
            derecho = enlace.getTailEndNode();
            if ((derecho.getNodeID() == node1ID) && (izquierdo.getNodeID() == node2ID)) {
                return enlace;
            }
            if ((derecho.getNodeID() == node2ID) && (izquierdo.getNodeID() == node1ID)) {
                return enlace;
            }
        }
        return null;
    }

    /**
     * Este m�todo permite acceder directamente al generador de identificadores
     * para eventos de la topology.
     *
     * @return El generador de identificadores para eventos de la topology.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLongIDGenerator getEventIDGenerator() {
        return this.eventIDGenerator;
    }

    /**
     * Este m�todo permite establecer el generador de identificadores para
     * elementos de la topolog�a.
     *
     * @param elementsIDGenerator El generador de identificadores de elementos.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setElementsIDGenerator(TIDGenerator elementsIDGenerator) {
        this.elementsIDGenerator = elementsIDGenerator;
    }

    /**
     * Este m�todo permite obtener el generador de identificadores para
     * elementos de la topolog�a.
     *
     * @return El generador de identificadores de elementos.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TIDGenerator getElementsIDGenerator() {
        return this.elementsIDGenerator;
    }

    /**
     * Este m�todo permite establecer el generador de direcciones IP de la
     * topolog�a.
     *
     * @param ipv4AddressGenerator Generador de direcciones IP de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setIPv4AddressGenerator(TIPv4AddressGenerator ipv4AddressGenerator) {
        this.ipv4AddressGenerator = ipv4AddressGenerator;
    }

    /**
     * Este m�todo permite obtener el generador de direcciones IP de la
     * topolog�a.
     *
     * @return El generador de direcciones IP de la topolog�a.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TIPv4AddressGenerator getIPv4AddressGenerator() {
        return this.ipv4AddressGenerator;
    }

    /**
     * Dados dos nodos como par�metros, uno de origen y otro de destino, este
     * m�todo obtiene el identificador de un nodo adyacente al origen, por el
     * que hay que ir para llegar al destino.
     *
     * @param originNodeID Identificador del nodo origen.
     * @param targetNodeID Identificador del nodo destino.
     * @return Identificador del nod que es siguiente salto para llegar del
     * origen al destino.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized int getNextHopID(int originNodeID, int targetNodeID) {
        this.floydAlgorithmLock.lock();
        int numNodosActual = this.nodes.size();
        int origen2 = 0;
        int destino2 = 0;
        // Hayamos equivalencias entre �ndices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i = 0;
        TNode nt = null;
        Iterator it = this.getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            equivalencia[i] = nt.getNodeID();
            if (equivalencia[i] == originNodeID) {
                origen2 = i;
            } else if (equivalencia[i] == targetNodeID) {
                destino2 = i;
            }
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TLink en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j = 0;
        for (i = 0; i < numNodosActual; i++) {
            for (j = 0; j < numNodosActual; j++) {
                en = getLinkThatJoins(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.isBroken()))) {
                    if (i == j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = TTopology.INFINITE_WEIGHT;
                    }
                } else if (en.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink ee = (TExternalLink) en;
                    matrizAdyacencia[i][j] = ee.getWeight();
                } else {
                    TInternalLink ei = (TInternalLink) en;
                    matrizAdyacencia[i][j] = ei.getWeight();
                }
            }
        }
        // Calculamos la matriz de costes y de caminos
        long matrizCostes[][] = new long[numNodosActual][numNodosActual];
        int matrizCaminos[][] = new int[numNodosActual][numNodosActual];
        int k = 0;
        for (i = 0; i < numNodosActual; i++) {
            for (j = 0; j < numNodosActual; j++) {
                matrizCostes[i][j] = matrizAdyacencia[i][j];
                matrizCaminos[i][j] = numNodosActual;
            }
        }
        for (k = 0; k < numNodosActual; k++) {
            for (i = 0; i < numNodosActual; i++) {
                for (j = 0; j < numNodosActual; j++) {
                    if (!((matrizCostes[i][k] == TTopology.INFINITE_WEIGHT) || (matrizCostes[k][j] == TTopology.INFINITE_WEIGHT))) {
                        if ((matrizCostes[i][k] + matrizCostes[k][j]) < matrizCostes[i][j]) {
                            matrizCostes[i][j] = matrizCostes[i][k] + matrizCostes[k][j];
                            matrizCaminos[i][j] = k;
                        }
                    }
                }
            }
        }
        // Obtiene el primer nodo del camino, si hay camino.
        int nodoSiguiente = TTopology.TARGET_UNREACHABLE;
        k = matrizCaminos[origen2][destino2];
        while (k != numNodosActual) {
            nodoSiguiente = k;
            k = matrizCaminos[origen2][k];
        }
        // Comprobamos si no hay camino o es que son adyacentes
        if (nodoSiguiente == TTopology.TARGET_UNREACHABLE) {
            TLink enlt = this.getLinkThatJoins(originNodeID, targetNodeID);
            if (enlt != null) {
                nodoSiguiente = targetNodeID;
            }
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        this.floydAlgorithmLock.unLock();
        return nodoSiguiente;
    }

    /**
     * Dados dos nodos como par�metros, uno de origen y otro de destino, este
     * m�todo obtiene la IP de un nodo adyacente al origen, por el que hay que
     * ir para llegar al destino.
     *
     * @param originNodeIPv4Address IP del nodo origen
     * @param targetNodeIPv4Address IP del nodo destino.
     * @return IP del nodo que es siguiente salto para llegar del origen al
     * destino.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized String getNextHopIPv4Address(String originNodeIPv4Address, String targetNodeIPv4Address) {
        int origen = this.getNode(originNodeIPv4Address).getNodeID();
        int destino = this.getNode(targetNodeIPv4Address).getNodeID();
        int siguienteSalto = getNextHopID(origen, destino);
        TNode nt = this.getNode(siguienteSalto);
        if (nt != null) {
            return nt.getIPv4Address();
        }
        return null;
    }

    /**
     * Este m�todo calcula la IP del nodo al que hay que dirigirse, cuyo camino
     * es el para avanzar hacia el destino seg� el protocolo RABAN.
     *
     * @param originNodeIPv4Address Direcci�n IP del nodo desde el que se
     * calcula el salto.
     * @param targetNodeIPv4Address Direcci�n IP del nodo al que se quiere
     * llegar.
     * @return La direcci�n IP del nodo adyacente al origen al que hay que
     * dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized String getNextHopIPv4AddressUsingRABAN(String originNodeIPv4Address, String targetNodeIPv4Address) {
        int origen = this.getNode(originNodeIPv4Address).getNodeID();
        int destino = this.getNode(targetNodeIPv4Address).getNodeID();
        int siguienteSalto = TTopology.this.getNextHopIDUsingRABAN(origen, destino);
        TNode nt = this.getNode(siguienteSalto);
        if (nt != null) {
            return nt.getIPv4Address();
        }
        return null;
    }

    /**
     * Este m�todo calcula la IP del nodo al que hay que dirigirse, cuyo camino
     * es el para avanzar hacia el destino seg� el protocolo RABAN. Adem�s lo
     * calcula evitando pasar por el enlace que se especifica mediante el par
     * IPOrigen-IPNodoAEvitar.
     *
     * @return La direcci�n IP del nodo adyacente al origen al que hay que
     * dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param nodeToAvoidIPv4Address IP del nodo adyacente al nodo origen. Por
     * el enlace que une a ambos, no se desea pasar.
     * @param originNodeIPv4Address Direcci�n IP del nodo desde el que se
     * calcula el salto.
     * @param targetNodeIPv4Address Direcci�n IP del nodo al que se quiere
     * llegar.
     */
    public synchronized String getNextHopIPv4AddressUsingRABAN(String originNodeIPv4Address, String targetNodeIPv4Address, String nodeToAvoidIPv4Address) {
        int origen = this.getNode(originNodeIPv4Address).getNodeID();
        int destino = this.getNode(targetNodeIPv4Address).getNodeID();
        int nodoAEvitar = this.getNode(nodeToAvoidIPv4Address).getNodeID();
        int siguienteSalto = getNextHopIDUsingRABAN(origen, destino, nodoAEvitar);
        TNode nt = this.getNode(siguienteSalto);
        if (nt != null) {
            return nt.getIPv4Address();
        }
        return null;
    }

    /**
     * Dados dos nodos como par�metros, uno de origen y otro de destino, este
     * m�todo obtiene el identificador de un nodo adyacente al origen, por el
     * que hay que ir para llegar al destino, siguiendo el protocolo de
     * encaminamiento RABAN.
     *
     * @param originNodeID Identificador del nodo origen.
     * @param targetNodeID Identificador del nodo destino.
     * @return Identificador del nod que es siguiente salto para llegar del
     * origen al destino.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized int getNextHopIDUsingRABAN(int originNodeID, int targetNodeID) {
        this.rabanAlgorithmLock.lock();
        int numNodosActual = this.nodes.size();
        int origen2 = 0;
        int destino2 = 0;
        // Hayamos equivalencias entre �ndices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i = 0;
        TNode nt = null;
        Iterator it = this.getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            equivalencia[i] = nt.getNodeID();
            if (equivalencia[i] == originNodeID) {
                origen2 = i;
            } else if (equivalencia[i] == targetNodeID) {
                destino2 = i;
            }
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TLink en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j = 0;
        for (i = 0; i < numNodosActual; i++) {
            for (j = 0; j < numNodosActual; j++) {
                en = getLinkThatJoins(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.isBroken()))) {
                    if (i == j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = this.INFINITE_WEIGHT;
                    }
                } else if (en.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink ee = (TExternalLink) en;
                    matrizAdyacencia[i][j] = ee.getRABANWeight();
                } else {
                    TInternalLink ei = (TInternalLink) en;
                    matrizAdyacencia[i][j] = ei.getRABANWeight();
                }
            }
        }
        // Calculamos la matriz de costes y de caminos
        long matrizCostes[][] = new long[numNodosActual][numNodosActual];
        int matrizCaminos[][] = new int[numNodosActual][numNodosActual];
        int k = 0;
        for (i = 0; i < numNodosActual; i++) {
            for (j = 0; j < numNodosActual; j++) {
                matrizCostes[i][j] = matrizAdyacencia[i][j];
                matrizCaminos[i][j] = numNodosActual;
            }
        }
        for (k = 0; k < numNodosActual; k++) {
            for (i = 0; i < numNodosActual; i++) {
                for (j = 0; j < numNodosActual; j++) {
                    if (!((matrizCostes[i][k] == this.INFINITE_WEIGHT) || (matrizCostes[k][j] == this.INFINITE_WEIGHT))) {
                        if ((matrizCostes[i][k] + matrizCostes[k][j]) < matrizCostes[i][j]) {
                            matrizCostes[i][j] = matrizCostes[i][k] + matrizCostes[k][j];
                            matrizCaminos[i][j] = k;
                        }
                    }
                }
            }
        }
        // Obtiene el primer nodo del camino, si hay camino.
        int nodoSiguiente = this.TARGET_UNREACHABLE;
        k = matrizCaminos[origen2][destino2];
        while (k != numNodosActual) {
            nodoSiguiente = k;
            k = matrizCaminos[origen2][k];
        }
        // Comprobamos si no hay camino o es que son adyacentes
        if (nodoSiguiente == this.TARGET_UNREACHABLE) {
            TLink enlt = this.getLinkThatJoins(originNodeID, targetNodeID);
            if (enlt != null) {
                nodoSiguiente = targetNodeID;
            }
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        this.rabanAlgorithmLock.unLock();
        return nodoSiguiente;
    }

    /**
     * Este m�todo calcula el iodentificador del nodo al que hay que dirigirse,
     * cuyo camino es el mejor para avanzar hacia el destino seg�n el protocolo
     * RABAN. Adem�s lo calcula evitando pasar por el enlace que se especifica
     * mediante el par origen-nodoAEvitar.
     *
     * @return El identificador del nodo adyacente al origen al que hay que
     * dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param originNodeID El identyificador del nodo que realiza la petici�n de
     * c�lculo.
     * @param targetNodeID El identificador del nodo al que se desea llegar.
     * @param nodeToAvoidID Identificador del nodo adyacente a origen. El enlace
     * que une a ambos se desea evitar.
     */
    public synchronized int getNextHopIDUsingRABAN(int originNodeID, int targetNodeID, int nodeToAvoidID) {
        this.rabanAlgorithmLock.lock();
        int numNodosActual = this.nodes.size();
        int origen2 = 0;
        int destino2 = 0;
        int nodoAEvitar2 = 0;
        // Hayamos equivalencias entre �ndices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i = 0;
        TNode nt = null;
        Iterator it = this.getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            equivalencia[i] = nt.getNodeID();
            if (equivalencia[i] == originNodeID) {
                origen2 = i;
            } else if (equivalencia[i] == targetNodeID) {
                destino2 = i;
            } else if (equivalencia[i] == nodeToAvoidID) {
                nodoAEvitar2 = i;
            }
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TLink en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j = 0;
        for (i = 0; i < numNodosActual; i++) {
            for (j = 0; j < numNodosActual; j++) {
                en = getLinkThatJoins(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.isBroken()))) {
                    if (i == j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = TTopology.INFINITE_WEIGHT;
                    }
                } else if (en.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink ee = (TExternalLink) en;
                    matrizAdyacencia[i][j] = ee.getRABANWeight();
                } else {
                    TInternalLink ei = (TInternalLink) en;
                    matrizAdyacencia[i][j] = ei.getRABANWeight();
                }
                // Aqu� se evita calcular un camino que pase por el enlace que
                // deseamos evitar, el que une origen con nodoAEvitar.
                if ((i == origen2) && (j == nodoAEvitar2)) {
                    matrizAdyacencia[i][j] = TTopology.INFINITE_WEIGHT;
                }
                if ((i == nodoAEvitar2) && (j == origen2)) {
                    matrizAdyacencia[i][j] = TTopology.INFINITE_WEIGHT;
                }
            }
        }
        // Calculamos la matriz de costes y de caminos
        long matrizCostes[][] = new long[numNodosActual][numNodosActual];
        int matrizCaminos[][] = new int[numNodosActual][numNodosActual];
        int k = 0;
        for (i = 0; i < numNodosActual; i++) {
            for (j = 0; j < numNodosActual; j++) {
                matrizCostes[i][j] = matrizAdyacencia[i][j];
                matrizCaminos[i][j] = numNodosActual;
            }
        }
        for (k = 0; k < numNodosActual; k++) {
            for (i = 0; i < numNodosActual; i++) {
                for (j = 0; j < numNodosActual; j++) {
                    if (!((matrizCostes[i][k] == TTopology.INFINITE_WEIGHT) || (matrizCostes[k][j] == TTopology.INFINITE_WEIGHT))) {
                        if ((matrizCostes[i][k] + matrizCostes[k][j]) < matrizCostes[i][j]) {
                            matrizCostes[i][j] = matrizCostes[i][k] + matrizCostes[k][j];
                            matrizCaminos[i][j] = k;
                        }
                    }
                }
            }
        }
        // Obtiene el primer nodo del camino, si hay camino.
        int nodoSiguiente = TTopology.TARGET_UNREACHABLE;
        k = matrizCaminos[origen2][destino2];
        while (k != numNodosActual) {
            nodoSiguiente = k;
            k = matrizCaminos[origen2][k];
        }
        // Comprobamos si no hay camino o es que son adyacentes
        if (nodoSiguiente == TTopology.TARGET_UNREACHABLE) {
            TLink enlt = this.getLinkThatJoins(originNodeID, targetNodeID);
            if (enlt != null) {
                nodoSiguiente = targetNodeID;
            }
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        this.rabanAlgorithmLock.unLock();
        return nodoSiguiente;
    }

    public static final long INFINITE_WEIGHT = 9223372036854775806L;
    public static final long VERY_HIGH_WEIGHT = (long) INFINITE_WEIGHT / 2;
    public static final int TARGET_UNREACHABLE = -1;

    private TreeSet nodes;
    private TreeSet links;
    private TTimer timer;
    private TScenario parentScenario;
    private TLongIDGenerator eventIDGenerator;
    private TIDGenerator elementsIDGenerator;
    private TIPv4AddressGenerator ipv4AddressGenerator;
    private TLock floydAlgorithmLock;
    private TLock rabanAlgorithmLock;
}
