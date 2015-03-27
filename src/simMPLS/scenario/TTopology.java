/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package simMPLS.scenario;

import simMPLS.hardware.timer.TTimer;
import simMPLS.utils.TIPGenerator;
import simMPLS.utils.TMonitor;
import simMPLS.utils.TIDGenerator;
import simMPLS.utils.TLongIDGenerator;
import java.awt.*;
import java.util.*;

/**
 * Esta clase implementa una topolog�a de rede completa.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TTopology {

    /**
     * Crea una nueva instancia de TTopologia
     * @param e Escenario padre al que pertence la topologia.
     * @since 1.0
     */
    public TTopology(TScenario e) {
        conjuntoNodos = new TreeSet();
        conjuntoEnlaces = new TreeSet();
        relojTopologia = new TTimer();
        escenarioPadre = e;
        IDEvento = new TLongIDGenerator();
        generaIdentificador = new TIDGenerator();
        generadorIP = new TIPGenerator();
        cerrojoFloyd = new TMonitor();
        cerrojoRABAN = new TMonitor();
    }

    /**
     * Este m�todo reinicia los atributos de la clase y los deja como si acabasen de
     * ser iniciador por el constructor.
     * @since 1.0
     */    
    public void reset() {
        this.cerrojoFloyd.lock();
        Iterator it;
        it = conjuntoNodos.iterator();
        TTopologyElement et;
        while (it.hasNext()) {
            et = (TTopologyElement) it.next();
            et.reset();
        }
        it = conjuntoEnlaces.iterator();
        while (it.hasNext()) {
            et = (TTopologyElement) it.next();
            et.reset();
        }
        relojTopologia.reset();
        IDEvento.reset();
        this.cerrojoFloyd.unLock();
        this.cerrojoRABAN.unLock();
    }
    
    /**
     * Este m�todo inserta un nuevo nodo en la topologia.
     * @param nodo Nodo que queremos insertar.
     * @since 1.0
     */    
    public void addNode(TNode nodo) {
        conjuntoNodos.add(nodo);
        relojTopologia.addTimerEventListener(nodo);
        try {
            nodo.addListenerSimulacion(escenarioPadre.getSimulation().obtenerRecolector());
        } catch (ESimulationSingleSubscriber e) {System.out.println(e.toString());}
    }
    
    /**
     * @param identificador
     */    
    private void eliminarSoloNodo(int identificador) {
        boolean fin = false;
        TNode nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            nodo = (TNode) iterador.next();
            if (nodo.getID() == identificador) {
                nodo.ponerPurgar(true);
                iterador.remove();
                fin = true;
            }
        }
        this.relojTopologia.purgeTimerEventListeners();
    }

    /**
     * @param n
     */    
    private void eliminarSoloNodo(TNode n) {
        eliminarSoloNodo(n.getID());
    }

    /**
     * Este m�todo obtiene un nodo de la topologia seg�n si identificador.
     * @param identificador Identificador del nodo que deseamos obtener.
     * @return Nodo que busc�bamos.NULL si no existe.
     * @since 1.0
     */    
    public TNode obtenerNodo(int identificador) {
        TNode nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getID() == identificador)
                return nodo;
        }
        return null;
    }

    /**
     * Este m�todo obtiene un nodo de la topologia por su direcci�n IP.
     * @param ip IP del nodo que deseamos obtener.
     * @return Nodo que busc�bamos.NULL si no existe.
     * @since 1.0
     */    
    public TNode obtenerNodo(String ip) {
        TNode nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getIPAddress().equals(ip))
                return nodo;
        }
        return null;
    }

    /**
     * Este m�todo obtiene el primer nodo de la topolog�a que encuentre, cuyo nombre
     * sea el mismo que el especificado como par�metro.
     * @param nom Nombre del nodo que deseamos obtener.
     * @return Nodo que busc�bamos. NULL si no existe.
     * @since 1.0
     */    
    public TNode obtenerPrimerNodoLlamado(String nom) {
        TNode nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.obtenerNombre().equals(nom))
                return nodo;
        }
        return null;
    }

    /**
     * Este m�todo comprueba si existe m�s de un nodo con el mismo nombre.
     * @param nom Nombre del nodo.
     * @return TRUE, si existe m�s de un nodo con el mismo nombre. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean existeMasDeUnNodoLlamado(String nom) {
        int cuantos = 0;
        TNode nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.obtenerNombre().equals(nom))
                cuantos++;
                if (cuantos > 1)
                    return true;
        }
        return false;
    }

    /**
     * Este m�todo comprueba si existe m�s de un enlace con el mismo nombre.
     * @param nom Nombre del enlace.
     * @return TRUE si existe m�s de un enlace con el mismo nombre. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean existeMasDeUnEnlaceLlamado(String nom) {
        int cuantos = 0;
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.obtenerNombre().equals(nom))
                cuantos++;
                if (cuantos > 1)
                    return true;
        }
        return false;
    }
    
    /**
     * Este m�todo comprueba si en la topoliog�a hay algun emisor de tr�fico que dirija
     * su tr�fico al receptor especificado por parametros.
     * @param nr Nodo receptor.
     * @return TRUE, si algun exmisor env�a tr�fico al receptor. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean hayTraficoDirigidoAMi(TReceiverNode nr) {
        TNode nodo = null;
        TSenderNode emisor = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.getNodeType() == TNode.SENDER) {
                emisor = (TSenderNode) nodo;
                if (emisor.obtenerDestino().equals(nr.getIPAddress()))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Este m�todo obtiene el primer enlace de la topolog�a con el nombre igual al
     * especificado como par�metro.
     * @param nom Nombre del enlace buscado.
     * @return El enlace buscado. NULL si no existe.
     * @since 1.0
     */    
    public TLink obtenerPrimerEnlaceLlamado(String nom) {
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.obtenerNombre().equals(nom))
                return enlace;
        }
        return null;
    }

    /**
     * Este m�todo obtiene el nodo cuya posici�n en el panel de dise�o coincida con la
     * especificada como par�metro.
     * @param p Coordenadas del nodo que deseamos buscar.
     * @return El nodo buscado. NULL si no existe.
     * @since 1.0
     */    
    public TNode obtenerNodoEnPosicion(Point p) {
        TNode nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNode) iterador.next();
            if (nodo.estaEnPosicion(p))
                return nodo;
        }
        return null;
    }

    /**
     * Este m�todo devuelve un vector con todos los nodos de la topologia.
     * @return Un vector con todos los nodos de la topologia.
     * @since 1.0
     */    
    public TNode[] obtenerNodos() {
        return (TNode[]) conjuntoNodos.toArray();
    }

    /**
     * Este m�todo toma un nodo por parametro y actualiza el mismo en la topologia, con
     * los valores pasados.
     * @param nodo Nodo que queremos actualizar, con los nuevos valores.
     * @since 1.0
     */    
    public void modificarNodo(TNode nodo) {
        boolean fin = false;
        TNode nodoBuscado = null;
        Iterator iterador = conjuntoNodos.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            nodoBuscado = (TNode) iterador.next();
            if (nodoBuscado.getID() == nodo.getID()) {
                if (nodo.getNodeType() == TNode.SENDER) {
                    TSenderNode nodoTrasCast = (TSenderNode) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.getNodeType() == TNode.RECEIVER) {
                    TSenderNode nodoTrasCast = (TSenderNode) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.getNodeType() == TNode.RECEIVER) {
                    TReceiverNode nodoTrasCast = (TReceiverNode) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.getNodeType() == TNode.LER) {
                    TLERNode nodoTrasCast = (TLERNode) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.getNodeType() == TNode.LERA) {
                    TActiveLERNode nodoTrasCast = (TActiveLERNode) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.getNodeType() == TNode.LSR) {
                    TLSRNode nodoTrasCast = (TLSRNode) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.getNodeType() == TNode.LSRA) {
                    TActiveLSRNode nodoTrasCast = (TActiveLSRNode) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                fin = true;
            }
        }
    }

    /**
     * Este m�todo inserta un nuevo enlace en la topologia.
     * @param enlace Enlace que deseamos insertar.
     * @since 1.0
     */    
    public void addLink(TLink enlace) {
        conjuntoEnlaces.add(enlace);
        relojTopologia.addTimerEventListener(enlace);
        try {
            enlace.addListenerSimulacion(escenarioPadre.getSimulation().obtenerRecolector());
        } catch (ESimulationSingleSubscriber e) {System.out.println(e.toString());}
    }

    /**
     * Este m�todo elimina de la topologia el enlace cuyo identificador es el
     * especificado por par�metros.
     * @param identificador Identificador del enlace que deseamos eliminar.
     * @since 1.0
     */    
    public void eliminarEnlace(int identificador) {
        boolean fin = false;
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            enlace = (TLink) iterador.next();
            if (enlace.getID() == identificador) {
                enlace.desconectarDePuertos();
                enlace.ponerPurgar(true);
                iterador.remove();
                fin = true;
            }
        }
        this.relojTopologia.purgeTimerEventListeners();
    }

    /**
     * Este m�todo elimina de la topolog�a el enlace pasado por par�metro.
     * @param e El enlace que deseamos eliminar.
     * @since 1.0
     */    
    public void eliminarEnlace(TLink e) {
        eliminarEnlace(e.getID());
    }

    /**
     * Este m�todo obtiene un enlace de la topologia, cuyo identificador coincide con
     * el especificado por parametros.
     * @param identificador Identificador del enlace que deseamos obtener.
     * @return El enlace que dese�bamos obtener. NULL si no existe.
     * @since 1.0
     */    
    public TLink obtenerEnlace(int identificador) {
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.getID() == identificador)
                return enlace;
        }
        return null;
    }

    /**
     * Este m�todo permite obtener un enlace cuyas coordenadas en la ventana de
     * simulaci�n coincidan con las pasadas por parametro.
     * @param p Coordenadas del enlace buscado.
     * @return El enlace buscado. NULL si no existe.
     * @since 1.0
     */    
    public TLink obtenerEnlaceEnPosicion(Point p) {
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.estaEnPosicion(p))
                return enlace;
        }
        return null;
    }

    /**
     * Este m�todo devuelve un vector con todos los enlaces de la topologia.
     * @return Un vector con todos los enlaces de la topolog�a.
     * @since 1.0
     */    
    public TLink[] obtenerEnlaces() {
        return (TLink[]) conjuntoEnlaces.toArray();
    }

    /**
     * Este m�todo actualiza un enlace de la topolog�a con otro pasado por par�metro.
     * @param enlace Enlace que queremos actualizar, con los valores nuevos.
     * @since 1.0
     */    
    public void modificarEnlace(TLink enlace) {
        boolean fin = false;
        TLink enlaceBuscado = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            enlaceBuscado = (TLink) iterador.next();
            if (enlaceBuscado.getID() == enlace.getID()) {
                if (enlaceBuscado.getLinkType() == TLink.EXTERNAL) {
                    TExternalLink enlaceTrasCast = (TExternalLink) enlaceBuscado;
                    enlaceTrasCast.ponerExtremo1(enlace.getEnd1());
                    enlaceTrasCast.ponerExtremo2(enlace.getEnd2());
                }
                else if (enlace.getLinkType() == TLink.INTERNAL) {
                    TInternalLink enlaceTrasCast = (TInternalLink) enlaceBuscado;
                    enlaceTrasCast.ponerExtremo1(enlace.getEnd1());
                    enlaceTrasCast.ponerExtremo2(enlace.getEnd2());
                }
                fin = true;
            }
        }
    }

    /**
     * @param p
     * @return
     */    
    private boolean hayElementoEnPosicion(Point p) {
        if (obtenerNodoEnPosicion(p) != null)
            return true;
        if (obtenerEnlaceEnPosicion(p) != null)
            return true;
        return false;
    }

    /**
     * Este m�todo permite obtener un elemento de la topolog�a cuyas coordenadas en la
     * ventana de simulaci�n coincidan con las pasadas por parametro.
     * @param p Coordenadas del elemento buscado.
     * @return El elemento buscado. NULL si no existe.
     * @since 1.0
     */    
    public TTopologyElement obtenerElementoEnPosicion(Point p) {
        if (hayElementoEnPosicion(p)) {
            TNode n;
            n = obtenerNodoEnPosicion(p);
            if (n != null)
                return n;

            TLink e;
            e = obtenerEnlaceEnPosicion(p);
            if (e != null)
                return e;
        }
        return null;
    }

    /**
     * Este m�todo elimina de la topologia un nodo cuyo identificador coincida con el
     * especificado por par�metros.
     * @param identificador identificador del nodo a borrar.
     * @since 1.0
     */    
    public void eliminarNodo(int identificador) {
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            if (enlace.conectadoA(identificador)) {
                enlace.desconectarDePuertos();
                enlace.ponerPurgar(true);
                iterador.remove();
            }
        }
        eliminarSoloNodo(identificador);
        this.relojTopologia.purgeTimerEventListeners();
    }

    /**
     * Este m�todo elimina de la topolog�a el nodo especificado por parametros.
     * @param n Nodo que deseamos eliminar.
     * @since 1.0
     */    
    public void eliminarNodo(TNode n) {
        eliminarNodo(n.getID());
    }

    /**
     * Este m�todo devuelve un iterador que permite navegar por los nodos de forma
     * sencilla.
     * @return El iterador de los nodos de la topologia.
     * @since 1.0
     */    
    public Iterator getNodesIterator() {
        return conjuntoNodos.iterator();
    }

    /**
     * Este m�todo devuelve un iterador que permite navegar por los enlaces de forma
     * sencilla.
     * @return El iterador de los enlaces de la topolog�a.
     * @since 1.0
     */    
    public Iterator getLinksIterator() {
        return conjuntoEnlaces.iterator();
    }

    /**
     * Este m�todo limpia la topologia, eliminando todos los enlaces y nodos
     * existentes.
     * @since 1.0
     */    
    public void eliminarTodo() {
        Iterator it = this.getLinksIterator();
        TNode n;
        TLink e;
        while (it.hasNext()) {
            e = (TLink) it.next();
            e.desconectarDePuertos();
            e.ponerPurgar(true);
            it.remove();
        }
        it = this.getNodesIterator();
        while (it.hasNext()) {
            n = (TNode) it.next();
            n.ponerPurgar(true);
            it.remove();
        }
        this.relojTopologia.purgeTimerEventListeners();
    }

    /**
     * Este m�todo devuelve el n�mero de nodos que hay en la topolog�a.
     * @return N�mero de nodos de la topolog�a.
     * @since 1.0
     */    
    public int obtenerNumeroDeNodos() {
        return conjuntoNodos.size();
    }

    /**
     * Este m�todo permite establecer el reloj principal que controlar� la topologia.
     * @param r El reloj principal de la topolog�a.
     * @since 1.0
     */    
    public void ponerReloj(TTimer r) {
        relojTopologia = r;
    }

    /**
     * Este m�todo permite obtener el reloj principal de la topologia.
     * @return El reloj principal de la topolog�a.
     * @since 1.0
     */    
    public TTimer obtenerReloj() {
        return relojTopologia;
    }

    /**
     * Este m�todo permite obtener el retardo menor de todos los enlaces de la topologia.
     * @return El retardo menor de todos los enlaces de la topolog�a.
     * @since 1.0
     */    
    public int obtenerMinimoDelay() {
        Iterator it = this.getLinksIterator();
        TLink e;
        int minimoDelay = 0;
        int delayAux=0;
        while (it.hasNext()) {
            e = (TLink) it.next();
            if (minimoDelay == 0) {
                minimoDelay = e.obtenerDelay();
            } else {
                delayAux = e.obtenerDelay();
                if (delayAux < minimoDelay) {
                    minimoDelay = delayAux;
                }
            }
        }
        if (minimoDelay == 0)
            minimoDelay = 1;
        return minimoDelay;
    }

    /**
     * Este m�todo permite averiguar si existe un enlace entre dos nodos con
     * identificadores los pasados por parametros.
     * @param extremo1 Identificador del nodo extremo 1.
     * @param extremo2 Identificador del nodo extremo 2.
     * @return TRUE, si existe un enlace entre extremo1 y extremo 2. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean existeEnlace(int extremo1, int extremo2) {
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        TNode izquierdo;
        TNode derecho;
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            izquierdo = enlace.getEnd1();
            derecho = enlace.getEnd2();
            if ((derecho.getID() == extremo1) && (izquierdo.getID() == extremo2))
                return true;
            if ((derecho.getID() == extremo2) && (izquierdo.getID() == extremo1))
                return true;
        }
        return false;
    }

    /**
     * Este m�todo permite obtener el enlace entre dos nodos con
     * identificadores los pasados por parametros.
     * @param extremo1 Identificador del nodo extremo 1.
     * @param extremo2 Identificador del nodo extremo 2.
     * @return El enlace entre extremo 1 y extremo 2. NULL si no existe.
     * @since 1.0
     */    
    public TLink obtenerEnlace(int extremo1, int extremo2) {
        TLink enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        TNode izquierdo;
        TNode derecho;
        while (iterador.hasNext()) {
            enlace = (TLink) iterador.next();
            izquierdo = enlace.getEnd1();
            derecho = enlace.getEnd2();
            if ((derecho.getID() == extremo1) && (izquierdo.getID() == extremo2))
                return enlace;
            if ((derecho.getID() == extremo2) && (izquierdo.getID() == extremo1))
                return enlace;
        }
        return null;
    }

    /**
     * Este m�todo permite acceder directamente al generador de identificadores para
     * eventos de la topologia.
     * @return El generador de identificadores para eventos de la topologia.
     * @since 1.0
     */    
    public TLongIDGenerator getEventIDGenerator() {
        return this.IDEvento;
    }

    /**
     * Este m�todo permite establecer el generador de identificadores para elementos de
     * la topolog�a.
     * @param gie El generador de identificadores de elementos.
     * @since 1.0
     */    
    public void ponerGeneradorIdentificadorElmto(TIDGenerator gie) {
        generaIdentificador = gie;
    }

    /**
     * Este m�todo permite obtener el generador de identificadores para elementos de
     * la topolog�a.
     * @return El generador de identificadores de elementos.
     * @since 1.0
     */    
    public TIDGenerator getItemIdentifierGenerator() {
        return generaIdentificador;
    }

    /**
     * Este m�todo permite establecer el generador de direcciones IP de la topolog�a.
     * @param gip Generador de direcciones IP de la topolog�a.
     * @since 1.0
     */    
    public void ponerGeneradorIP(TIPGenerator gip) {
        generadorIP = gip;
    }

    /**
     * Este m�todo permite obtener el generador de direcciones IP de la topolog�a.
     * @return El generador de direcciones IP de la topolog�a.
     * @since 1.0
     */    
    public TIPGenerator getIPAddressGenerator() {
        return generadorIP;
    }

    /**
     * Dados dos nodos como par�metros, uno de origen y otro de destino, este m�todo
     * obtiene el identificador de un nodo adyacente al origen, por el que hay que ir
     * para llegar al destino.
     * @param origen Identificador del nodo origen.
     * @param destino Identificador del nodo destino.
     * @return Identificador del nod que es siguiente salto para llegar del origen al destino.
     * @since 1.0
     */    
    public synchronized int obtenerSalto(int origen, int destino) {
        cerrojoFloyd.lock();
        int numNodosActual = this.conjuntoNodos.size();
        int origen2 = 0;
        int destino2 = 0;
        // Hayamos equivalencias entre �ndices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i=0;
        TNode nt = null;
        Iterator it = this.getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            equivalencia[i] = nt.getID();
            if (equivalencia[i] == origen)
                origen2 = i;
            else if (equivalencia[i] == destino)
                destino2 = i;
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TLink en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                en = obtenerEnlace(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.linkIsBroken()))) {
                    if (i==j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                    }
                } else {
                    if (en.getLinkType() == TLink.EXTERNAL) {
                        TExternalLink ee = (TExternalLink) en;
                        matrizAdyacencia[i][j] = ee.obtenerPeso();
                    } else {
                        TInternalLink ei = (TInternalLink) en;
                        matrizAdyacencia[i][j] = ei.obtenerPeso();
                    }
                }
            }
        }
        // Calculamos la matriz de costes y de caminos
        long matrizCostes[][] = new long[numNodosActual][numNodosActual];
        int matrizCaminos[][] = new int[numNodosActual][numNodosActual];
        int k=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                matrizCostes[i][j] = matrizAdyacencia[i][j];
                matrizCaminos[i][j] = numNodosActual;
            }
        }
        for (k=0; k<numNodosActual; k++) {
            for (i=0; i<numNodosActual; i++) {
                for (j=0; j<numNodosActual; j++) {
                    if (!((matrizCostes[i][k] == this.PESO_INFINITO) || (matrizCostes[k][j] == this.PESO_INFINITO))) {
                        if ((matrizCostes[i][k] + matrizCostes[k][j]) < matrizCostes[i][j]) {
                            matrizCostes[i][j] = matrizCostes[i][k] + matrizCostes[k][j];
                            matrizCaminos[i][j] = k;
                        }
                    }
                }
            }
        }
        // Obtiene el primer nodo del camino, si hay camino.
        int nodoSiguiente = this.SIN_CAMINO;
        k = matrizCaminos[origen2][destino2];
        while (k != numNodosActual) {
            nodoSiguiente = k;
            k = matrizCaminos[origen2][k];
        }
        // Comprobamos si no hay camino o es que son adyacentes
        if (nodoSiguiente == this.SIN_CAMINO) {
            TLink enlt = this.obtenerEnlace(origen, destino);
            if (enlt != null)
                nodoSiguiente = destino;
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        cerrojoFloyd.unLock();
        return nodoSiguiente;
      }

    /**
     * Dados dos nodos como par�metros, uno de origen y otro de destino, este m�todo
     * obtiene la IP de un nodo adyacente al origen, por el que hay que ir
     * para llegar al destino.
     * @param IPorigen IP del nodo origen
     * @param IPdestino IP del nodo destino.
     * @return IP del nodo que es siguiente salto para llegar del origen al destino.
     * @since 1.0
     */    
    public synchronized String obtenerIPSalto(String IPorigen, String IPdestino) {
        int origen = this.obtenerNodo(IPorigen).getID();
        int destino = this.obtenerNodo(IPdestino).getID();
        int siguienteSalto = obtenerSalto(origen, destino);
        TNode nt = this.obtenerNodo(siguienteSalto);
        if (nt != null)
            return nt.getIPAddress();
        return null;
    }

    /**
     * Este m�todo calcula la IP del nodo al que hay que dirigirse, cuyo camino es el
     * para avanzar hacia el destino seg� el protocolo RABAN.
     * @param IPorigen Direcci�n IP del nodo desde el que se calcula el salto.
     * @param IPdestino Direcci�n IP del nodo al que se quiere llegar.
     * @return La direcci�n IP del nodo adyacente al origen al que hay que dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @since 1.0
     */    
    public synchronized String obtenerIPSaltoRABAN(String IPorigen, String IPdestino) {
        int origen = this.obtenerNodo(IPorigen).getID();
        int destino = this.obtenerNodo(IPdestino).getID();
        int siguienteSalto = obtenerSaltoRABAN(origen, destino);
        TNode nt = this.obtenerNodo(siguienteSalto);
        if (nt != null)
            return nt.getIPAddress();
        return null;
    }

    /**
     * Este m�todo calcula la IP del nodo al que hay que dirigirse, cuyo camino es el
     * para avanzar hacia el destino seg� el protocolo RABAN. Adem�s lo calcula
     * evitando pasar por el enlace que se especifica mediante el par IPOrigen-IPNodoAEvitar.
     * @return La direcci�n IP del nodo adyacente al origen al que hay que dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @since 1.0
     * @param IPNodoAEvitar IP del nodo adyacente al nodo origen. Por el enlace que une a ambos, no se desea
     * pasar.
     * @param IPorigen Direcci�n IP del nodo desde el que se calcula el salto.
     * @param IPdestino Direcci�n IP del nodo al que se quiere llegar.
     */    
    public synchronized String obtenerIPSaltoRABAN(String IPorigen, String IPdestino, String IPNodoAEvitar) {
        int origen = this.obtenerNodo(IPorigen).getID();
        int destino = this.obtenerNodo(IPdestino).getID();
        int nodoAEvitar = this.obtenerNodo(IPNodoAEvitar).getID();
        int siguienteSalto = obtenerSaltoRABAN(origen, destino, nodoAEvitar);
        TNode nt = this.obtenerNodo(siguienteSalto);
        if (nt != null)
            return nt.getIPAddress();
        return null;
    }

     /**
     * Dados dos nodos como par�metros, uno de origen y otro de destino, este m�todo
     * obtiene el identificador de un nodo adyacente al origen, por el que hay que ir
     * para llegar al destino, siguiendo el protocolo de encaminamiento RABAN.
     * @param origen Identificador del nodo origen.
     * @param destino Identificador del nodo destino.
     * @return Identificador del nod que es siguiente salto para llegar del origen al destino.
     * @since 1.0
     */    
    public synchronized int obtenerSaltoRABAN(int origen, int destino) {
        cerrojoRABAN.lock();
        int numNodosActual = this.conjuntoNodos.size();
        int origen2 = 0;
        int destino2 = 0;
        // Hayamos equivalencias entre �ndices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i=0;
        TNode nt = null;
        Iterator it = this.getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            equivalencia[i] = nt.getID();
            if (equivalencia[i] == origen)
                origen2 = i;
            else if (equivalencia[i] == destino)
                destino2 = i;
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TLink en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                en = obtenerEnlace(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.linkIsBroken()))) {
                    if (i==j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                    }
                } else {
                    if (en.getLinkType() == TLink.EXTERNAL) {
                        TExternalLink ee = (TExternalLink) en;
                        matrizAdyacencia[i][j] = ee.obtenerPesoRABAN();
                    } else {
                        TInternalLink ei = (TInternalLink) en;
                        matrizAdyacencia[i][j] = ei.obtenerPesoRABAN();
                    }
                }
            }
        }
        // Calculamos la matriz de costes y de caminos
        long matrizCostes[][] = new long[numNodosActual][numNodosActual];
        int matrizCaminos[][] = new int[numNodosActual][numNodosActual];
        int k=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                matrizCostes[i][j] = matrizAdyacencia[i][j];
                matrizCaminos[i][j] = numNodosActual;
            }
        }
        for (k=0; k<numNodosActual; k++) {
            for (i=0; i<numNodosActual; i++) {
                for (j=0; j<numNodosActual; j++) {
                    if (!((matrizCostes[i][k] == this.PESO_INFINITO) || (matrizCostes[k][j] == this.PESO_INFINITO))) {
                        if ((matrizCostes[i][k] + matrizCostes[k][j]) < matrizCostes[i][j]) {
                            matrizCostes[i][j] = matrizCostes[i][k] + matrizCostes[k][j];
                            matrizCaminos[i][j] = k;
                        }
                    }
                }
            }
        }
        // Obtiene el primer nodo del camino, si hay camino.
        int nodoSiguiente = this.SIN_CAMINO;
        k = matrizCaminos[origen2][destino2];
        while (k != numNodosActual) {
            nodoSiguiente = k;
            k = matrizCaminos[origen2][k];
        }
        // Comprobamos si no hay camino o es que son adyacentes
        if (nodoSiguiente == this.SIN_CAMINO) {
            TLink enlt = this.obtenerEnlace(origen, destino);
            if (enlt != null)
                nodoSiguiente = destino;
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        cerrojoRABAN.unLock();
        return nodoSiguiente;
      }

    /**
     * Este m�todo calcula el iodentificador del nodo al que hay que dirigirse, cuyo
     * camino es el mejor para avanzar hacia el destino seg�n el protocolo RABAN. Adem�s
     * lo calcula evitando pasar por el enlace que se especifica mediante el par
     * origen-nodoAEvitar.
     * @return El identificador del nodo adyacente al origen al que hay que dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @since 1.0
     * @param origen El identyificador del nodo que realiza la petici�n de c�lculo.
     * @param destino El identificador del nodo al que se desea llegar.
     * @param nodoAEvitar Identificador del nodo adyacente a origen. El enlace que une a ambos se desea
     * evitar.
     */    
    public synchronized int obtenerSaltoRABAN(int origen, int destino, int nodoAEvitar) {
        cerrojoRABAN.lock();
        int numNodosActual = this.conjuntoNodos.size();
        int origen2 = 0;
        int destino2 = 0;
        int nodoAEvitar2 = 0;
        // Hayamos equivalencias entre �ndices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i=0;
        TNode nt = null;
        Iterator it = this.getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            equivalencia[i] = nt.getID();
            if (equivalencia[i] == origen)
                origen2 = i;
            else if (equivalencia[i] == destino)
                destino2 = i;
            else if (equivalencia[i] == nodoAEvitar)
                nodoAEvitar2 = i;
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TLink en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                en = obtenerEnlace(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.linkIsBroken()))) {
                    if (i==j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                    }
                } else {
                    if (en.getLinkType() == TLink.EXTERNAL) {
                        TExternalLink ee = (TExternalLink) en;
                        matrizAdyacencia[i][j] = ee.obtenerPesoRABAN();
                    } else {
                        TInternalLink ei = (TInternalLink) en;
                        matrizAdyacencia[i][j] = ei.obtenerPesoRABAN();
                    }
                }
                // Aqu� se evita calcular un camino que pase por el enlace que
                // deseamos evitar, el que une origen con nodoAEvitar.
                if ((i == origen2) && (j == nodoAEvitar2)) {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                }
                if ((i == nodoAEvitar2) && (j == origen2)) {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                }
            }
        }
        // Calculamos la matriz de costes y de caminos
        long matrizCostes[][] = new long[numNodosActual][numNodosActual];
        int matrizCaminos[][] = new int[numNodosActual][numNodosActual];
        int k=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                matrizCostes[i][j] = matrizAdyacencia[i][j];
                matrizCaminos[i][j] = numNodosActual;
            }
        }
        for (k=0; k<numNodosActual; k++) {
            for (i=0; i<numNodosActual; i++) {
                for (j=0; j<numNodosActual; j++) {
                    if (!((matrizCostes[i][k] == this.PESO_INFINITO) || (matrizCostes[k][j] == this.PESO_INFINITO))) {
                        if ((matrizCostes[i][k] + matrizCostes[k][j]) < matrizCostes[i][j]) {
                            matrizCostes[i][j] = matrizCostes[i][k] + matrizCostes[k][j];
                            matrizCaminos[i][j] = k;
                        }
                    }
                }
            }
        }
        // Obtiene el primer nodo del camino, si hay camino.
        int nodoSiguiente = this.SIN_CAMINO;
        k = matrizCaminos[origen2][destino2];
        while (k != numNodosActual) {
            nodoSiguiente = k;
            k = matrizCaminos[origen2][k];
        }
        // Comprobamos si no hay camino o es que son adyacentes
        if (nodoSiguiente == this.SIN_CAMINO) {
            TLink enlt = this.obtenerEnlace(origen, destino);
            if (enlt != null)
                nodoSiguiente = destino;
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        cerrojoRABAN.unLock();
        return nodoSiguiente;
      }

    /**
     * Esta constante identifica un peso infinito.
     * @since 1.0
     */    
    public static final long PESO_INFINITO = 9223372036854775806L;
    /**
     * Esta constante implementa un peso muy alto.
     * @since 1.0
     */    
    public static final long PESO_MUY_ALTO = (long) PESO_INFINITO / 2;
    /**
     * Esta constante indica que no hay camino entre un nodo y otro.
     * @since 1.0
     */    
    public static final int SIN_CAMINO = -1;

    private TreeSet conjuntoNodos;
    private TreeSet conjuntoEnlaces;
    private TTimer relojTopologia;
    private TScenario escenarioPadre;
    private TLongIDGenerator IDEvento;
    private TIDGenerator generaIdentificador;
    private TIPGenerator generadorIP;
    private TMonitor cerrojoFloyd;
    private TMonitor cerrojoRABAN;
}
