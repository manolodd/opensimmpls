//**************************************************************************
// Nombre......: TTopologia.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un objeto que contiene todos los datos
// ............: referentes a la topologia del escenario: datos de los nodos
// ............: y datos de los enlaces, básicamente.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import java.awt.*;
import java.util.*;
import simMPLS.escenario.*;
import simMPLS.electronica.reloj.*;
import simMPLS.utiles.*;

/**
 * Esta clase implementa una topología de rede completa.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TTopologia {

    /**
     * Crea una nueva instancia de TTopologia
     * @param e Escenario padre al que pertence la topologia.
     * @since 1.0
     */
    public TTopologia(TEscenario e) {
        conjuntoNodos = new TreeSet();
        conjuntoEnlaces = new TreeSet();
        relojTopologia = new TReloj();
        escenarioPadre = e;
        IDEvento = new TIdentificadorLargo();
        generaIdentificador = new TIdentificador();
        generadorIP = new TGeneradorDeIP();
        cerrojoFloyd = new TMonitor();
        cerrojoRABAN = new TMonitor();
    }

    /**
     * Este método reinicia los atributos de la clase y los deja como si acabasen de
     * ser iniciador por el constructor.
     * @since 1.0
     */    
    public void reset() {
        this.cerrojoFloyd.bloquear();
        Iterator it;
        it = conjuntoNodos.iterator();
        TElementoTopologia et;
        while (it.hasNext()) {
            et = (TElementoTopologia) it.next();
            et.reset();
        }
        it = conjuntoEnlaces.iterator();
        while (it.hasNext()) {
            et = (TElementoTopologia) it.next();
            et.reset();
        }
        relojTopologia.reset();
        IDEvento.reset();
        this.cerrojoFloyd.liberar();
        this.cerrojoRABAN.liberar();
    }
    
    /**
     * Este método inserta un nuevo nodo en la topologia.
     * @param nodo Nodo que queremos insertar.
     * @since 1.0
     */    
    public void insertarNodo(TNodoTopologia nodo) {
        conjuntoNodos.add(nodo);
        relojTopologia.addListenerReloj(nodo);
        try {
            nodo.addListenerSimulacion(escenarioPadre.obtenerSimulacion().obtenerRecolector());
        } catch (ESimulacionUnSoloSuscriptor e) {System.out.println(e.toString());}
    }
    
    /**
     * @param identificador
     */    
    private void eliminarSoloNodo(int identificador) {
        boolean fin = false;
        TNodoTopologia nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            nodo = (TNodoTopologia) iterador.next();
            if (nodo.obtenerIdentificador() == identificador) {
                nodo.ponerPurgar(true);
                iterador.remove();
                fin = true;
            }
        }
        this.relojTopologia.purgarListenerReloj();
    }

    /**
     * @param n
     */    
    private void eliminarSoloNodo(TNodoTopologia n) {
        eliminarSoloNodo(n.obtenerIdentificador());
    }

    /**
     * Este método obtiene un nodo de la topologia según si identificador.
     * @param identificador Identificador del nodo que deseamos obtener.
     * @return Nodo que buscábamos.NULL si no existe.
     * @since 1.0
     */    
    public TNodoTopologia obtenerNodo(int identificador) {
        TNodoTopologia nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNodoTopologia) iterador.next();
            if (nodo.obtenerIdentificador() == identificador)
                return nodo;
        }
        return null;
    }

    /**
     * Este método obtiene un nodo de la topologia por su dirección IP.
     * @param ip IP del nodo que deseamos obtener.
     * @return Nodo que buscábamos.NULL si no existe.
     * @since 1.0
     */    
    public TNodoTopologia obtenerNodo(String ip) {
        TNodoTopologia nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNodoTopologia) iterador.next();
            if (nodo.obtenerIP().equals(ip))
                return nodo;
        }
        return null;
    }

    /**
     * Este método obtiene el primer nodo de la topología que encuentre, cuyo nombre
     * sea el mismo que el especificado como parámetro.
     * @param nom Nombre del nodo que deseamos obtener.
     * @return Nodo que buscábamos. NULL si no existe.
     * @since 1.0
     */    
    public TNodoTopologia obtenerPrimerNodoLlamado(String nom) {
        TNodoTopologia nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNodoTopologia) iterador.next();
            if (nodo.obtenerNombre().equals(nom))
                return nodo;
        }
        return null;
    }

    /**
     * Este método comprueba si existe más de un nodo con el mismo nombre.
     * @param nom Nombre del nodo.
     * @return TRUE, si existe más de un nodo con el mismo nombre. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean existeMasDeUnNodoLlamado(String nom) {
        int cuantos = 0;
        TNodoTopologia nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNodoTopologia) iterador.next();
            if (nodo.obtenerNombre().equals(nom))
                cuantos++;
                if (cuantos > 1)
                    return true;
        }
        return false;
    }

    /**
     * Este método comprueba si existe más de un enlace con el mismo nombre.
     * @param nom Nombre del enlace.
     * @return TRUE si existe más de un enlace con el mismo nombre. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean existeMasDeUnEnlaceLlamado(String nom) {
        int cuantos = 0;
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TEnlaceTopologia) iterador.next();
            if (enlace.obtenerNombre().equals(nom))
                cuantos++;
                if (cuantos > 1)
                    return true;
        }
        return false;
    }
    
    /**
     * Este método comprueba si en la topoliogía hay algun emisor de tráfico que dirija
     * su tráfico al receptor especificado por parametros.
     * @param nr Nodo receptor.
     * @return TRUE, si algun exmisor envía tráfico al receptor. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean hayTraficoDirigidoAMi(TNodoReceptor nr) {
        TNodoTopologia nodo = null;
        TNodoEmisor emisor = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNodoTopologia) iterador.next();
            if (nodo.obtenerTipo() == TNodoTopologia.EMISOR) {
                emisor = (TNodoEmisor) nodo;
                if (emisor.obtenerDestino().equals(nr.obtenerIP()))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Este método obtiene el primer enlace de la topología con el nombre igual al
     * especificado como parámetro.
     * @param nom Nombre del enlace buscado.
     * @return El enlace buscado. NULL si no existe.
     * @since 1.0
     */    
    public TEnlaceTopologia obtenerPrimerEnlaceLlamado(String nom) {
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TEnlaceTopologia) iterador.next();
            if (enlace.obtenerNombre().equals(nom))
                return enlace;
        }
        return null;
    }

    /**
     * Este método obtiene el nodo cuya posición en el panel de diseño coincida con la
     * especificada como parámetro.
     * @param p Coordenadas del nodo que deseamos buscar.
     * @return El nodo buscado. NULL si no existe.
     * @since 1.0
     */    
    public TNodoTopologia obtenerNodoEnPosicion(Point p) {
        TNodoTopologia nodo = null;
        Iterator iterador = conjuntoNodos.iterator();
        while (iterador.hasNext()) {
            nodo = (TNodoTopologia) iterador.next();
            if (nodo.estaEnPosicion(p))
                return nodo;
        }
        return null;
    }

    /**
     * Este método devuelve un vector con todos los nodos de la topologia.
     * @return Un vector con todos los nodos de la topologia.
     * @since 1.0
     */    
    public TNodoTopologia[] obtenerNodos() {
        return (TNodoTopologia[]) conjuntoNodos.toArray();
    }

    /**
     * Este método toma un nodo por parametro y actualiza el mismo en la topologia, con
     * los valores pasados.
     * @param nodo Nodo que queremos actualizar, con los nuevos valores.
     * @since 1.0
     */    
    public void modificarNodo(TNodoTopologia nodo) {
        boolean fin = false;
        TNodoTopologia nodoBuscado = null;
        Iterator iterador = conjuntoNodos.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            nodoBuscado = (TNodoTopologia) iterador.next();
            if (nodoBuscado.obtenerIdentificador() == nodo.obtenerIdentificador()) {
                if (nodo.obtenerTipo() == TNodoTopologia.EMISOR) {
                    TNodoEmisor nodoTrasCast = (TNodoEmisor) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.obtenerTipo() == TNodoTopologia.RECEPTOR) {
                    TNodoEmisor nodoTrasCast = (TNodoEmisor) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.obtenerTipo() == TNodoTopologia.RECEPTOR) {
                    TNodoReceptor nodoTrasCast = (TNodoReceptor) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.obtenerTipo() == TNodoTopologia.LER) {
                    TNodoLER nodoTrasCast = (TNodoLER) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.obtenerTipo() == TNodoTopologia.LERA) {
                    TNodoLERA nodoTrasCast = (TNodoLERA) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.obtenerTipo() == TNodoTopologia.LSR) {
                    TNodoLSR nodoTrasCast = (TNodoLSR) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                else if (nodo.obtenerTipo() == TNodoTopologia.LSRA) {
                    TNodoLSRA nodoTrasCast = (TNodoLSRA) nodoBuscado;
                    nodoTrasCast.ponerNombre(nodo.obtenerNombre());
                    nodoTrasCast.ponerPosicion(nodo.obtenerPosicion());
                }
                fin = true;
            }
        }
    }

    /**
     * Este método inserta un nuevo enlace en la topologia.
     * @param enlace Enlace que deseamos insertar.
     * @since 1.0
     */    
    public void insertarEnlace(TEnlaceTopologia enlace) {
        conjuntoEnlaces.add(enlace);
        relojTopologia.addListenerReloj(enlace);
        try {
            enlace.addListenerSimulacion(escenarioPadre.obtenerSimulacion().obtenerRecolector());
        } catch (ESimulacionUnSoloSuscriptor e) {System.out.println(e.toString());}
    }

    /**
     * Este método elimina de la topologia el enlace cuyo identificador es el
     * especificado por parámetros.
     * @param identificador Identificador del enlace que deseamos eliminar.
     * @since 1.0
     */    
    public void eliminarEnlace(int identificador) {
        boolean fin = false;
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            enlace = (TEnlaceTopologia) iterador.next();
            if (enlace.obtenerIdentificador() == identificador) {
                enlace.desconectarDePuertos();
                enlace.ponerPurgar(true);
                iterador.remove();
                fin = true;
            }
        }
        this.relojTopologia.purgarListenerReloj();
    }

    /**
     * Este método elimina de la topología el enlace pasado por parámetro.
     * @param e El enlace que deseamos eliminar.
     * @since 1.0
     */    
    public void eliminarEnlace(TEnlaceTopologia e) {
        eliminarEnlace(e.obtenerIdentificador());
    }

    /**
     * Este método obtiene un enlace de la topologia, cuyo identificador coincide con
     * el especificado por parametros.
     * @param identificador Identificador del enlace que deseamos obtener.
     * @return El enlace que deseábamos obtener. NULL si no existe.
     * @since 1.0
     */    
    public TEnlaceTopologia obtenerEnlace(int identificador) {
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TEnlaceTopologia) iterador.next();
            if (enlace.obtenerIdentificador() == identificador)
                return enlace;
        }
        return null;
    }

    /**
     * Este método permite obtener un enlace cuyas coordenadas en la ventana de
     * simulación coincidan con las pasadas por parametro.
     * @param p Coordenadas del enlace buscado.
     * @return El enlace buscado. NULL si no existe.
     * @since 1.0
     */    
    public TEnlaceTopologia obtenerEnlaceEnPosicion(Point p) {
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TEnlaceTopologia) iterador.next();
            if (enlace.estaEnPosicion(p))
                return enlace;
        }
        return null;
    }

    /**
     * Este método devuelve un vector con todos los enlaces de la topologia.
     * @return Un vector con todos los enlaces de la topología.
     * @since 1.0
     */    
    public TEnlaceTopologia[] obtenerEnlaces() {
        return (TEnlaceTopologia[]) conjuntoEnlaces.toArray();
    }

    /**
     * Este método actualiza un enlace de la topología con otro pasado por parámetro.
     * @param enlace Enlace que queremos actualizar, con los valores nuevos.
     * @since 1.0
     */    
    public void modificarEnlace(TEnlaceTopologia enlace) {
        boolean fin = false;
        TEnlaceTopologia enlaceBuscado = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while ((iterador.hasNext()) && (!fin)) {
            enlaceBuscado = (TEnlaceTopologia) iterador.next();
            if (enlaceBuscado.obtenerIdentificador() == enlace.obtenerIdentificador()) {
                if (enlaceBuscado.obtenerTipo() == TEnlaceTopologia.EXTERNO) {
                    TEnlaceExterno enlaceTrasCast = (TEnlaceExterno) enlaceBuscado;
                    enlaceTrasCast.ponerExtremo1(enlace.obtenerExtremo1());
                    enlaceTrasCast.ponerExtremo2(enlace.obtenerExtremo2());
                }
                else if (enlace.obtenerTipo() == TEnlaceTopologia.INTERNO) {
                    TEnlaceInterno enlaceTrasCast = (TEnlaceInterno) enlaceBuscado;
                    enlaceTrasCast.ponerExtremo1(enlace.obtenerExtremo1());
                    enlaceTrasCast.ponerExtremo2(enlace.obtenerExtremo2());
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
     * Este método permite obtener un elemento de la topología cuyas coordenadas en la
     * ventana de simulación coincidan con las pasadas por parametro.
     * @param p Coordenadas del elemento buscado.
     * @return El elemento buscado. NULL si no existe.
     * @since 1.0
     */    
    public TElementoTopologia obtenerElementoEnPosicion(Point p) {
        if (hayElementoEnPosicion(p)) {
            TNodoTopologia n;
            n = obtenerNodoEnPosicion(p);
            if (n != null)
                return n;

            TEnlaceTopologia e;
            e = obtenerEnlaceEnPosicion(p);
            if (e != null)
                return e;
        }
        return null;
    }

    /**
     * Este método elimina de la topologia un nodo cuyo identificador coincida con el
     * especificado por parámetros.
     * @param identificador identificador del nodo a borrar.
     * @since 1.0
     */    
    public void eliminarNodo(int identificador) {
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        while (iterador.hasNext()) {
            enlace = (TEnlaceTopologia) iterador.next();
            if (enlace.conectadoA(identificador)) {
                enlace.desconectarDePuertos();
                enlace.ponerPurgar(true);
                iterador.remove();
            }
        }
        eliminarSoloNodo(identificador);
        this.relojTopologia.purgarListenerReloj();
    }

    /**
     * Este método elimina de la topología el nodo especificado por parametros.
     * @param n Nodo que deseamos eliminar.
     * @since 1.0
     */    
    public void eliminarNodo(TNodoTopologia n) {
        eliminarNodo(n.obtenerIdentificador());
    }

    /**
     * Este método devuelve un iterador que permite navegar por los nodos de forma
     * sencilla.
     * @return El iterador de los nodos de la topologia.
     * @since 1.0
     */    
    public Iterator obtenerIteradorNodos() {
        return conjuntoNodos.iterator();
    }

    /**
     * Este método devuelve un iterador que permite navegar por los enlaces de forma
     * sencilla.
     * @return El iterador de los enlaces de la topología.
     * @since 1.0
     */    
    public Iterator obtenerIteradorEnlaces() {
        return conjuntoEnlaces.iterator();
    }

    /**
     * Este método limpia la topologia, eliminando todos los enlaces y nodos
     * existentes.
     * @since 1.0
     */    
    public void eliminarTodo() {
        Iterator it = this.obtenerIteradorEnlaces();
        TNodoTopologia n;
        TEnlaceTopologia e;
        while (it.hasNext()) {
            e = (TEnlaceTopologia) it.next();
            e.desconectarDePuertos();
            e.ponerPurgar(true);
            it.remove();
        }
        it = this.obtenerIteradorNodos();
        while (it.hasNext()) {
            n = (TNodoTopologia) it.next();
            n.ponerPurgar(true);
            it.remove();
        }
        this.relojTopologia.purgarListenerReloj();
    }

    /**
     * Este método devuelve el número de nodos que hay en la topología.
     * @return Número de nodos de la topología.
     * @since 1.0
     */    
    public int obtenerNumeroDeNodos() {
        return conjuntoNodos.size();
    }

    /**
     * Este método permite establecer el reloj principal que controlará la topologia.
     * @param r El reloj principal de la topología.
     * @since 1.0
     */    
    public void ponerReloj(TReloj r) {
        relojTopologia = r;
    }

    /**
     * Este método permite obtener el reloj principal de la topologia.
     * @return El reloj principal de la topología.
     * @since 1.0
     */    
    public TReloj obtenerReloj() {
        return relojTopologia;
    }

    /**
     * Este método permite obtener el retardo menor de todos los enlaces de la topologia.
     * @return El retardo menor de todos los enlaces de la topología.
     * @since 1.0
     */    
    public int obtenerMinimoDelay() {
        Iterator it = this.obtenerIteradorEnlaces();
        TEnlaceTopologia e;
        int minimoDelay = 0;
        int delayAux=0;
        while (it.hasNext()) {
            e = (TEnlaceTopologia) it.next();
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
     * Este método permite averiguar si existe un enlace entre dos nodos con
     * identificadores los pasados por parametros.
     * @param extremo1 Identificador del nodo extremo 1.
     * @param extremo2 Identificador del nodo extremo 2.
     * @return TRUE, si existe un enlace entre extremo1 y extremo 2. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean existeEnlace(int extremo1, int extremo2) {
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        TNodoTopologia izquierdo;
        TNodoTopologia derecho;
        while (iterador.hasNext()) {
            enlace = (TEnlaceTopologia) iterador.next();
            izquierdo = enlace.obtenerExtremo1();
            derecho = enlace.obtenerExtremo2();
            if ((derecho.obtenerIdentificador() == extremo1) && (izquierdo.obtenerIdentificador() == extremo2))
                return true;
            if ((derecho.obtenerIdentificador() == extremo2) && (izquierdo.obtenerIdentificador() == extremo1))
                return true;
        }
        return false;
    }

    /**
     * Este método permite obtener el enlace entre dos nodos con
     * identificadores los pasados por parametros.
     * @param extremo1 Identificador del nodo extremo 1.
     * @param extremo2 Identificador del nodo extremo 2.
     * @return El enlace entre extremo 1 y extremo 2. NULL si no existe.
     * @since 1.0
     */    
    public TEnlaceTopologia obtenerEnlace(int extremo1, int extremo2) {
        TEnlaceTopologia enlace = null;
        Iterator iterador = conjuntoEnlaces.iterator();
        TNodoTopologia izquierdo;
        TNodoTopologia derecho;
        while (iterador.hasNext()) {
            enlace = (TEnlaceTopologia) iterador.next();
            izquierdo = enlace.obtenerExtremo1();
            derecho = enlace.obtenerExtremo2();
            if ((derecho.obtenerIdentificador() == extremo1) && (izquierdo.obtenerIdentificador() == extremo2))
                return enlace;
            if ((derecho.obtenerIdentificador() == extremo2) && (izquierdo.obtenerIdentificador() == extremo1))
                return enlace;
        }
        return null;
    }

    /**
     * Este método permite acceder directamente al generador de identificadores para
     * eventos de la topologia.
     * @return El generador de identificadores para eventos de la topologia.
     * @since 1.0
     */    
    public TIdentificadorLargo obtenerGeneradorIDEvento() {
        return this.IDEvento;
    }

    /**
     * Este método permite establecer el generador de identificadores para elementos de
     * la topología.
     * @param gie El generador de identificadores de elementos.
     * @since 1.0
     */    
    public void ponerGeneradorIdentificadorElmto(TIdentificador gie) {
        generaIdentificador = gie;
    }

    /**
     * Este método permite obtener el generador de identificadores para elementos de
     * la topología.
     * @return El generador de identificadores de elementos.
     * @since 1.0
     */    
    public TIdentificador obtenerGeneradorIdentificadorElmto() {
        return generaIdentificador;
    }

    /**
     * Este método permite establecer el generador de direcciones IP de la topología.
     * @param gip Generador de direcciones IP de la topología.
     * @since 1.0
     */    
    public void ponerGeneradorIP(TGeneradorDeIP gip) {
        generadorIP = gip;
    }

    /**
     * Este método permite obtener el generador de direcciones IP de la topología.
     * @return El generador de direcciones IP de la topología.
     * @since 1.0
     */    
    public TGeneradorDeIP obtenerGeneradorIP() {
        return generadorIP;
    }

    /**
     * Dados dos nodos como parámetros, uno de origen y otro de destino, este método
     * obtiene el identificador de un nodo adyacente al origen, por el que hay que ir
     * para llegar al destino.
     * @param origen Identificador del nodo origen.
     * @param destino Identificador del nodo destino.
     * @return Identificador del nod que es siguiente salto para llegar del origen al destino.
     * @since 1.0
     */    
    public synchronized int obtenerSalto(int origen, int destino) {
        cerrojoFloyd.bloquear();
        int numNodosActual = this.conjuntoNodos.size();
        int origen2 = 0;
        int destino2 = 0;
        // Hayamos equivalencias entre índices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i=0;
        TNodoTopologia nt = null;
        Iterator it = this.obtenerIteradorNodos();
        while (it.hasNext()) {
            nt = (TNodoTopologia) it.next();
            equivalencia[i] = nt.obtenerIdentificador();
            if (equivalencia[i] == origen)
                origen2 = i;
            else if (equivalencia[i] == destino)
                destino2 = i;
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TEnlaceTopologia en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                en = obtenerEnlace(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.obtenerEnlaceCaido()))) {
                    if (i==j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                    }
                } else {
                    if (en.obtenerTipo() == TEnlaceTopologia.EXTERNO) {
                        TEnlaceExterno ee = (TEnlaceExterno) en;
                        matrizAdyacencia[i][j] = ee.obtenerPeso();
                    } else {
                        TEnlaceInterno ei = (TEnlaceInterno) en;
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
            TEnlaceTopologia enlt = this.obtenerEnlace(origen, destino);
            if (enlt != null)
                nodoSiguiente = destino;
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        cerrojoFloyd.liberar();
        return nodoSiguiente;
      }

    /**
     * Dados dos nodos como parámetros, uno de origen y otro de destino, este método
     * obtiene la IP de un nodo adyacente al origen, por el que hay que ir
     * para llegar al destino.
     * @param IPorigen IP del nodo origen
     * @param IPdestino IP del nodo destino.
     * @return IP del nodo que es siguiente salto para llegar del origen al destino.
     * @since 1.0
     */    
    public synchronized String obtenerIPSalto(String IPorigen, String IPdestino) {
        int origen = this.obtenerNodo(IPorigen).obtenerIdentificador();
        int destino = this.obtenerNodo(IPdestino).obtenerIdentificador();
        int siguienteSalto = obtenerSalto(origen, destino);
        TNodoTopologia nt = this.obtenerNodo(siguienteSalto);
        if (nt != null)
            return nt.obtenerIP();
        return null;
    }

    /**
     * Este método calcula la IP del nodo al que hay que dirigirse, cuyo camino es el
     * para avanzar hacia el destino segú el protocolo RABAN.
     * @param IPorigen Dirección IP del nodo desde el que se calcula el salto.
     * @param IPdestino Dirección IP del nodo al que se quiere llegar.
     * @return La dirección IP del nodo adyacente al origen al que hay que dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @since 1.0
     */    
    public synchronized String obtenerIPSaltoRABAN(String IPorigen, String IPdestino) {
        int origen = this.obtenerNodo(IPorigen).obtenerIdentificador();
        int destino = this.obtenerNodo(IPdestino).obtenerIdentificador();
        int siguienteSalto = obtenerSaltoRABAN(origen, destino);
        TNodoTopologia nt = this.obtenerNodo(siguienteSalto);
        if (nt != null)
            return nt.obtenerIP();
        return null;
    }

    /**
     * Este método calcula la IP del nodo al que hay que dirigirse, cuyo camino es el
     * para avanzar hacia el destino segú el protocolo RABAN. Además lo calcula
     * evitando pasar por el enlace que se especifica mediante el par IPOrigen-IPNodoAEvitar.
     * @return La dirección IP del nodo adyacente al origen al que hay que dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @since 1.0
     * @param IPNodoAEvitar IP del nodo adyacente al nodo origen. Por el enlace que une a ambos, no se desea
     * pasar.
     * @param IPorigen Dirección IP del nodo desde el que se calcula el salto.
     * @param IPdestino Dirección IP del nodo al que se quiere llegar.
     */    
    public synchronized String obtenerIPSaltoRABAN(String IPorigen, String IPdestino, String IPNodoAEvitar) {
        int origen = this.obtenerNodo(IPorigen).obtenerIdentificador();
        int destino = this.obtenerNodo(IPdestino).obtenerIdentificador();
        int nodoAEvitar = this.obtenerNodo(IPNodoAEvitar).obtenerIdentificador();
        int siguienteSalto = obtenerSaltoRABAN(origen, destino, nodoAEvitar);
        TNodoTopologia nt = this.obtenerNodo(siguienteSalto);
        if (nt != null)
            return nt.obtenerIP();
        return null;
    }

     /**
     * Dados dos nodos como parámetros, uno de origen y otro de destino, este método
     * obtiene el identificador de un nodo adyacente al origen, por el que hay que ir
     * para llegar al destino, siguiendo el protocolo de encaminamiento RABAN.
     * @param origen Identificador del nodo origen.
     * @param destino Identificador del nodo destino.
     * @return Identificador del nod que es siguiente salto para llegar del origen al destino.
     * @since 1.0
     */    
    public synchronized int obtenerSaltoRABAN(int origen, int destino) {
        cerrojoRABAN.bloquear();
        int numNodosActual = this.conjuntoNodos.size();
        int origen2 = 0;
        int destino2 = 0;
        // Hayamos equivalencias entre índices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i=0;
        TNodoTopologia nt = null;
        Iterator it = this.obtenerIteradorNodos();
        while (it.hasNext()) {
            nt = (TNodoTopologia) it.next();
            equivalencia[i] = nt.obtenerIdentificador();
            if (equivalencia[i] == origen)
                origen2 = i;
            else if (equivalencia[i] == destino)
                destino2 = i;
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TEnlaceTopologia en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                en = obtenerEnlace(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.obtenerEnlaceCaido()))) {
                    if (i==j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                    }
                } else {
                    if (en.obtenerTipo() == TEnlaceTopologia.EXTERNO) {
                        TEnlaceExterno ee = (TEnlaceExterno) en;
                        matrizAdyacencia[i][j] = ee.obtenerPesoRABAN();
                    } else {
                        TEnlaceInterno ei = (TEnlaceInterno) en;
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
            TEnlaceTopologia enlt = this.obtenerEnlace(origen, destino);
            if (enlt != null)
                nodoSiguiente = destino;
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        cerrojoRABAN.liberar();
        return nodoSiguiente;
      }

    /**
     * Este método calcula el iodentificador del nodo al que hay que dirigirse, cuyo
     * camino es el mejor para avanzar hacia el destino según el protocolo RABAN. Además
     * lo calcula evitando pasar por el enlace que se especifica mediante el par
     * origen-nodoAEvitar.
     * @return El identificador del nodo adyacente al origen al que hay que dirigirse. NULL, si no hay camino entre el origen y el destino.
     * @since 1.0
     * @param origen El identyificador del nodo que realiza la petición de cálculo.
     * @param destino El identificador del nodo al que se desea llegar.
     * @param nodoAEvitar Identificador del nodo adyacente a origen. El enlace que une a ambos se desea
     * evitar.
     */    
    public synchronized int obtenerSaltoRABAN(int origen, int destino, int nodoAEvitar) {
        cerrojoRABAN.bloquear();
        int numNodosActual = this.conjuntoNodos.size();
        int origen2 = 0;
        int destino2 = 0;
        int nodoAEvitar2 = 0;
        // Hayamos equivalencias entre índices e identificadores de nodo
        int equivalencia[] = new int[numNodosActual];
        int i=0;
        TNodoTopologia nt = null;
        Iterator it = this.obtenerIteradorNodos();
        while (it.hasNext()) {
            nt = (TNodoTopologia) it.next();
            equivalencia[i] = nt.obtenerIdentificador();
            if (equivalencia[i] == origen)
                origen2 = i;
            else if (equivalencia[i] == destino)
                destino2 = i;
            else if (equivalencia[i] == nodoAEvitar)
                nodoAEvitar2 = i;
            i++;
        }
        // Averiguamos la matriz de adyacencia
        TEnlaceTopologia en = null;
        long matrizAdyacencia[][] = new long[numNodosActual][numNodosActual];
        int j=0;
        for (i=0; i<numNodosActual; i++) {
            for (j=0; j<numNodosActual; j++) {
                en = obtenerEnlace(equivalencia[i], equivalencia[j]);
                if ((en == null) || ((en != null) && (en.obtenerEnlaceCaido()))) {
                    if (i==j) {
                        matrizAdyacencia[i][j] = 0;
                    } else {
                        matrizAdyacencia[i][j] = this.PESO_INFINITO;
                    }
                } else {
                    if (en.obtenerTipo() == TEnlaceTopologia.EXTERNO) {
                        TEnlaceExterno ee = (TEnlaceExterno) en;
                        matrizAdyacencia[i][j] = ee.obtenerPesoRABAN();
                    } else {
                        TEnlaceInterno ei = (TEnlaceInterno) en;
                        matrizAdyacencia[i][j] = ei.obtenerPesoRABAN();
                    }
                }
                // Aquí se evita calcular un camino que pase por el enlace que
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
            TEnlaceTopologia enlt = this.obtenerEnlace(origen, destino);
            if (enlt != null)
                nodoSiguiente = destino;
        } else {
            nodoSiguiente = equivalencia[nodoSiguiente];
        }
        cerrojoRABAN.liberar();
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
    private TReloj relojTopologia;
    private TEscenario escenarioPadre;
    private TIdentificadorLargo IDEvento;
    private TIdentificador generaIdentificador;
    private TGeneradorDeIP generadorIP;
    private TMonitor cerrojoFloyd;
    private TMonitor cerrojoRABAN;
}
