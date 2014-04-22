//**************************************************************************
// Nombre......: TPort.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa un puerto de comunicaciones de un no-
// ............: do de la topologia.
// Fecha.......: 12/03/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.ports;

import simMPLS.scenario.TTopologyLink;
import simMPLS.scenario.TStats;
import simMPLS.protocols.TPDU;
import simMPLS.utils.TLock;


/**
 * Esta clase abstracta es la superclase de un puerto de entrada/salida v�lido para
 * cualquiera de los nodos del simulador.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TPort {

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de TPuerto.
     * @since 1.0
     * @param idp Identificador (n�mero) del puerto.
     * @param cpn Referencia al superconjunto de puertos del que este puerto forma parte.
     */
    public TPort(TNodePorts cpn, int idp) {
        identificador = 0;
        enlace = null;
        cjtoPuertos = cpn;
        cerrojo = new TLock();
        idPuerto = idp;
    }
    
    /**
     * Este m�todo establece cu�l es el superconjunto de puertos al que pertenece este
     * `puerto concreto.
     * @param cpn El conjunto de puertos al que pertenece este puerto.
     * @since 1.0
     */    
    public void ponerCjtoPuertos(TNodePorts cpn) {
        cjtoPuertos = cpn;
    }

    /**
     * Este m�todo obtiene el superconjunto de puertos al que pertenece este puerto.
     * @return El conjunto de puertos al que pertenece este nodo.
     * @since 1.0
     */    
    public TNodePorts obtenerCjtoPuertos() {
        return cjtoPuertos;
    }

    /**
     * Este m�todo establece el identificador del puerto.
     * @param id Identificador que queremos asociar a este puerto.
     * @since 1.0
     */    
    public void ponerIdentificador(int id) {
        identificador = id;
    }
    
    /**
     * Este m�todo obtiene el identificador del puerto.
     * @return El identificador del puerto.
     * @since 1.0
     */    
    public int obtenerIdentificador() {
        return identificador;
    }

    /**
     * Este m�todo averigua si el puerto est� libre o est� conectado a al�n enlace de
     * la topolog�a.
     * @return TRUE, si est� libre. FALSE, si est� conectado a alg�n enlace de la topolog�a.
     * @since 1.0
     */    
    public boolean estaLibre() {
        if (enlace == null)
            return true;
        return false;
    }

    /**
     * Este m�todo enlaza este puerto con un enlace de la topolog�a, dejando por tanto
     * de estar libre.
     * @param e El enlace al que se desea conectar el puerto.
     * @since 1.0
     */    
    public void ponerEnlace(TTopologyLink e) {
        enlace = e;
    }

    /**
     * Este m�todo obtiene el enlace al que est� unido el puerto.
     * @return El enlace al que est� unido el puerto, si est� unido. NULL en caso contrario.
     * @since 1.0
     */    
    public TTopologyLink obtenerEnlace() {
        return enlace;
    }

    /**
     * Este m�todo libera la conexi�n que existe entre el puerto y un enlace de la
     * topolog�a, dej�ndolo libre.
     * @since 1.0
     */    
    public void quitarEnlace() {
        enlace = null;
    }

    /**
     * Este m�todo coloca en el enlace al que est� unido el puerto, un paquete de
     * datos.
     * @param p El paquete que se desea transmitir por el enlace.
     * @param destino 1, si el paquete va dirigido al EXTREMO1 del enlace. 2, si va dirigido al
     * EXTREMO2.
     * @since 1.0
     */    
    public void ponerPaqueteEnEnlace(TPDU p, int destino) {
        if (enlace != null) {
            if (!enlace.obtenerEnlaceCaido()) {
                if (enlace.obtenerTipo() == TTopologyLink.INTERNO) {
                    enlace.ponerPaquete(p, destino);
                    if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                        this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(p, TStats.SALIDA);
                    }
                } else {
                    if ((p.obtenerTipo() != TPDU.GPSRP) && (p.obtenerTipo() != TPDU.TLDP)) {
                        enlace.ponerPaquete(p, destino);
                        if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                            this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(p, TStats.SALIDA);
                        }
                    }
                }
            } else {
                descartarPaquete(p);
            }
        }
    }

    /**
     * Este m�todo deshecha el paquete pasado por parametro.
     * @param paquete El paquete que se desea descartar.
     * @since 1.0
     */    
    public abstract void descartarPaquete(TPDU paquete);
    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto.
     * @param paquete El paquete que queremos que sea recivido en el puerto.
     * @since 1.0
     */    
    public abstract void ponerPaquete(TPDU paquete);
    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto. Es igual
     * que el m�todo ponerPaquete(), salvo que no genera eventos y lo hace
     * silenciosamente.
     * @param paquete El paquete que queremos que reciba el puerto.
     * @since 1.0
     */    
    public abstract void reencolar(TPDU paquete);
    /**
     * este m�todo lee un paquete del buffer de recepci�n del puerto. El paquete leido
     * depender� del algoritmo de gesti�n de los b�fferes que implemente el puerto. Por
     * defecto, es un FIFO Droptail.
     * @return El paquete le�do.
     * @since 1.0
     */    
    public abstract TPDU obtenerPaquete();
    /**
     * Este m�todo calcula si podemos conmutar el siguiente paquete del nodo, dado el
     * n�mero de octetos que como mucho podemos conmutar en un momento dado.
     * @param octetos El n�mero de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso contrario.
     * @since 1.0
     */    
    public abstract boolean puedoConmutarPaquete(int octetos);
    /**
     * Este m�todo obtiene la congesti�n total el puerto, en porcentaje.
     * @return El porcentaje de ocupaci�n del puerto.
     * @since 1.0
     */    
    public abstract long obtenerCongestion();
    /**
     * Este m�todo comprueba si hay paquetes esperando en el buffer de recepci�n o no.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean hayPaqueteEsperando();
    /**
     * Este m�todo calcula el total de octetos que suman los paquetes que actualmente
     * hay en el buffer de recepci�n del puerto.
     * @return El tama�o en octetos del total de paquetes en el buffer de recepci�n.
     * @since 1.0
     */    
    public abstract long obtenerOcupacion();
    /**
     * Este m�todo calcula el n�mero de paquetes total que hay en el buffer del puerto.
     * @return El n�mero total de paquetes que hay en el puerto.
     * @since 1.0
     */    
    public abstract int obtenerNumeroPaquetes();
    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 1.0
     */    
    public abstract void reset();
    /**
     * Este m�todo permite saltarse las limitaciones de tama�o del buffer y establecer
     * �ste como un buffer ideal, con capacidad infinita.
     * @param bi TRUE indica que el buffer se tomar� como ilimitado. FALSE indica que el buffer
     * tendr� el tama�o especificado en el resto de m�todos.
     * @since 1.0
     */    
    public abstract void ponerBufferIlimitado(boolean bi);
    
    /**
     * Este atributo es el identificador del puerto. Como los nodos tienen m�s de un
     * puerto, es necesario un identificador para referirse a cada uno de ellos.
     * @since 1.0
     */    
    protected int identificador;
    /**
     * Este atributo es el enlace de la topolog�a al que est� unido el puerto. Todo
     * puerto o est� libre o est� unido a un enlace, que es este.
     * @since 1.0
     */    
    protected TTopologyLink enlace;
    /**
     * Este atributo es una referencia al superconjunto de todos los puertos de un nodo
     * al que pertenece este.
     * @since 1.0
     */    
    protected TNodePorts cjtoPuertos;
    /**
     * Este atributo es un monitor que sirve para crear secciones cr�ticas, actuando de
     * barrera, para sincronizar el acceso concurrente a algunas zonas del objeto.
     * @since 1.0
     */    
    protected TLock cerrojo;
    /**
     * Este atributo almacenar� el identificador que indica el n�mero de puerto que
     * ocupa la instancia actual dentro del conjunto de puertos de un nodo.
     * @since 1.0
     */    
    protected int idPuerto;
    
}
