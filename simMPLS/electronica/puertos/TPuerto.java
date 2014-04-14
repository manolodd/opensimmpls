//**************************************************************************
// Nombre......: TPuerto.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un puerto de comunicaciones de un no-
// ............: do de la topologia.
// Fecha.......: 12/03/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.puertos;

import java.util.*;
import simMPLS.protocolo.*;
import simMPLS.escenario.*;
import simMPLS.utiles.*;
import simMPLS.electronica.puertos.*;
import simMPLS.escenario.*;

/**
 * Esta clase abstracta es la superclase de un puerto de entrada/salida válido para
 * cualquiera de los nodos del simulador.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TPuerto {

    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de TPuerto.
     * @since 1.0
     * @param idp Identificador (número) del puerto.
     * @param cpn Referencia al superconjunto de puertos del que este puerto forma parte.
     */
    public TPuerto(TPuertosNodo cpn, int idp) {
        identificador = 0;
        enlace = null;
        cjtoPuertos = cpn;
        cerrojo = new TMonitor();
        idPuerto = idp;
    }
    
    /**
     * Este método establece cuál es el superconjunto de puertos al que pertenece este
     * `puerto concreto.
     * @param cpn El conjunto de puertos al que pertenece este puerto.
     * @since 1.0
     */    
    public void ponerCjtoPuertos(TPuertosNodo cpn) {
        cjtoPuertos = cpn;
    }

    /**
     * Este método obtiene el superconjunto de puertos al que pertenece este puerto.
     * @return El conjunto de puertos al que pertenece este nodo.
     * @since 1.0
     */    
    public TPuertosNodo obtenerCjtoPuertos() {
        return cjtoPuertos;
    }

    /**
     * Este método establece el identificador del puerto.
     * @param id Identificador que queremos asociar a este puerto.
     * @since 1.0
     */    
    public void ponerIdentificador(int id) {
        identificador = id;
    }
    
    /**
     * Este método obtiene el identificador del puerto.
     * @return El identificador del puerto.
     * @since 1.0
     */    
    public int obtenerIdentificador() {
        return identificador;
    }

    /**
     * Este método averigua si el puerto está libre o está conectado a alún enlace de
     * la topología.
     * @return TRUE, si está libre. FALSE, si está conectado a algún enlace de la topología.
     * @since 1.0
     */    
    public boolean estaLibre() {
        if (enlace == null)
            return true;
        return false;
    }

    /**
     * Este método enlaza este puerto con un enlace de la topología, dejando por tanto
     * de estar libre.
     * @param e El enlace al que se desea conectar el puerto.
     * @since 1.0
     */    
    public void ponerEnlace(TEnlaceTopologia e) {
        enlace = e;
    }

    /**
     * Este método obtiene el enlace al que está unido el puerto.
     * @return El enlace al que está unido el puerto, si está unido. NULL en caso contrario.
     * @since 1.0
     */    
    public TEnlaceTopologia obtenerEnlace() {
        return enlace;
    }

    /**
     * Este método libera la conexión que existe entre el puerto y un enlace de la
     * topología, dejándolo libre.
     * @since 1.0
     */    
    public void quitarEnlace() {
        enlace = null;
    }

    /**
     * Este método coloca en el enlace al que está unido el puerto, un paquete de
     * datos.
     * @param p El paquete que se desea transmitir por el enlace.
     * @param destino 1, si el paquete va dirigido al EXTREMO1 del enlace. 2, si va dirigido al
     * EXTREMO2.
     * @since 1.0
     */    
    public void ponerPaqueteEnEnlace(TPDU p, int destino) {
        if (enlace != null) {
            if (!enlace.obtenerEnlaceCaido()) {
                if (enlace.obtenerTipo() == TEnlaceTopologia.INTERNO) {
                    enlace.ponerPaquete(p, destino);
                    if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                        this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(p, TEstadisticas.SALIDA);
                    }
                } else {
                    if ((p.obtenerTipo() != TPDU.GPSRP) && (p.obtenerTipo() != TPDU.TLDP)) {
                        enlace.ponerPaquete(p, destino);
                        if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                            this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(p, TEstadisticas.SALIDA);
                        }
                    }
                }
            } else {
                descartarPaquete(p);
            }
        }
    }

    /**
     * Este método deshecha el paquete pasado por parametro.
     * @param paquete El paquete que se desea descartar.
     * @since 1.0
     */    
    public abstract void descartarPaquete(TPDU paquete);
    /**
     * Este método inserta un paquete en el buffer de recepción del puerto.
     * @param paquete El paquete que queremos que sea recivido en el puerto.
     * @since 1.0
     */    
    public abstract void ponerPaquete(TPDU paquete);
    /**
     * Este método inserta un paquete en el buffer de recepción del puerto. Es igual
     * que el método ponerPaquete(), salvo que no genera eventos y lo hace
     * silenciosamente.
     * @param paquete El paquete que queremos que reciba el puerto.
     * @since 1.0
     */    
    public abstract void reencolar(TPDU paquete);
    /**
     * este método lee un paquete del buffer de recepción del puerto. El paquete leido
     * dependerá del algoritmo de gestión de los búfferes que implemente el puerto. Por
     * defecto, es un FIFO Droptail.
     * @return El paquete leído.
     * @since 1.0
     */    
    public abstract TPDU obtenerPaquete();
    /**
     * Este método calcula si podemos conmutar el siguiente paquete del nodo, dado el
     * número de octetos que como mucho podemos conmutar en un momento dado.
     * @param octetos El número de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso contrario.
     * @since 1.0
     */    
    public abstract boolean puedoConmutarPaquete(int octetos);
    /**
     * Este método obtiene la congestión total el puerto, en porcentaje.
     * @return El porcentaje de ocupación del puerto.
     * @since 1.0
     */    
    public abstract long obtenerCongestion();
    /**
     * Este método comprueba si hay paquetes esperando en el buffer de recepción o no.
     * @return TRUE, si hay paquetes en el buffer de recepción. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean hayPaqueteEsperando();
    /**
     * Este método calcula el total de octetos que suman los paquetes que actualmente
     * hay en el buffer de recepción del puerto.
     * @return El tamaño en octetos del total de paquetes en el buffer de recepción.
     * @since 1.0
     */    
    public abstract long obtenerOcupacion();
    /**
     * Este método calcula el número de paquetes total que hay en el buffer del puerto.
     * @return El número total de paquetes que hay en el puerto.
     * @since 1.0
     */    
    public abstract int obtenerNumeroPaquetes();
    /**
     * Este método reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 1.0
     */    
    public abstract void reset();
    /**
     * Este método permite saltarse las limitaciones de tamaño del buffer y establecer
     * éste como un buffer ideal, con capacidad infinita.
     * @param bi TRUE indica que el buffer se tomará como ilimitado. FALSE indica que el buffer
     * tendrá el tamaño especificado en el resto de métodos.
     * @since 1.0
     */    
    public abstract void ponerBufferIlimitado(boolean bi);
    
    /**
     * Este atributo es el identificador del puerto. Como los nodos tienen más de un
     * puerto, es necesario un identificador para referirse a cada uno de ellos.
     * @since 1.0
     */    
    protected int identificador;
    /**
     * Este atributo es el enlace de la topología al que está unido el puerto. Todo
     * puerto o está libre o está unido a un enlace, que es este.
     * @since 1.0
     */    
    protected TEnlaceTopologia enlace;
    /**
     * Este atributo es una referencia al superconjunto de todos los puertos de un nodo
     * al que pertenece este.
     * @since 1.0
     */    
    protected TPuertosNodo cjtoPuertos;
    /**
     * Este atributo es un monitor que sirve para crear secciones críticas, actuando de
     * barrera, para sincronizar el acceso concurrente a algunas zonas del objeto.
     * @since 1.0
     */    
    protected TMonitor cerrojo;
    /**
     * Este atributo almacenará el identificador que indica el número de puerto que
     * ocupa la instancia actual dentro del conjunto de puertos de un nodo.
     * @since 1.0
     */    
    protected int idPuerto;
    
}
