/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.hardware.ports;

import simMPLS.scenario.TLink;
import simMPLS.scenario.TStats;
import simMPLS.protocols.TPDU;
import simMPLS.utils.TMonitor;

/**
 * This abstract class will be implemented to have an I/O port of a port set.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public abstract class TPort {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TPort.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     * @param portID the identifier of the port. This is the unique identifier
     * ti distinguish the port within the parent port set.
     * @param parentPortSet A reference to the parent port set this port belongs
     * to.
     */
    public TPort(TPortSet parentPortSet, int portID) {
        this.link = null;
        this.parentPortSet = parentPortSet;
        this.monitor = new TMonitor();
        this.portID = portID;
    }

    /**
     * This method establish which is the port set this port belongs to.
     *
     * @param parentPortSet The port set this port belongs to.
     * @since 1.0
     */
    public void setPortSet(TPortSet parentPortSet) {
        this.parentPortSet = parentPortSet;
    }

    /**
     * This method get the port set this port belongs to.
     *
     * @return The port set this port belongs to.
     * @since 1.0
     */
    public TPortSet getPortSet() {
        return this.parentPortSet;
    }

    /**
     * This method establishes the port number for this port.
     *
     * @param portID the port number for this port.
     * @since 1.0
     */
    public void setPortID(int portID) {
        this.portID = portID;
    }

    /**
     * Este m�todo obtiene el identifier del puerto.
     *
     * @return El identifier del puerto.
     * @since 1.0
     */
    public int obtenerIdentificador() {
        return this.portID;
    }

    /**
     * Este m�todo averigua si el puerto est� libre o est� conectado a al�n link
     * de la topolog�a.
     *
     * @return TRUE, si est� libre. FALSE, si est� conectado a alg�n link de la
     * topolog�a.
     * @since 1.0
     */
    public boolean isAvailable() {
        if (link == null) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo enlaza este puerto con un link de la topolog�a, dejando por
     * tanto de estar libre.
     *
     * @param e El link al que se desea conectar el puerto.
     * @since 1.0
     */
    public void setLink(TLink e) {
        link = e;
    }

    /**
     * Este m�todo obtiene el link al que est� unido el puerto.
     *
     * @return El link al que est� unido el puerto, si est� unido. NULL en caso
     * contrario.
     * @since 1.0
     */
    public TLink getLink() {
        return link;
    }

    /**
     * Este m�todo libera la conexi�n que existe entre el puerto y un link de la
     * topolog�a, dej�ndolo libre.
     *
     * @since 1.0
     */
    public void disconnectLink() {
        link = null;
    }

    /**
     * Este m�todo coloca en el link al que est� unido el puerto, un paquete de
     * datos.
     *
     * @param p El paquete que se desea transmitir por el link.
     * @param destino 1, si el paquete va dirigido al END_NODE_1 del link. 2, si
     * va dirigido al END_NODE_2.
     * @since 1.0
     */
    public void ponerPaqueteEnEnlace(TPDU p, int destino) {
        if (link != null) {
            if (!link.obtenerEnlaceCaido()) {
                if (link.obtenerTipo() == TLink.INTERNO) {
                    link.ponerPaquete(p, destino);
                    if (this.getPortSet().getNode().getStats() != null) {
                        this.getPortSet().getNode().getStats().addStatsEntry(p, TStats.SALIDA);
                    }
                } else {
                    if ((p.getType() != TPDU.GPSRP) && (p.getType() != TPDU.TLDP)) {
                        link.ponerPaquete(p, destino);
                        if (this.getPortSet().getNode().getStats() != null) {
                            this.getPortSet().getNode().getStats().addStatsEntry(p, TStats.SALIDA);
                        }
                    }
                }
            } else {
                discardPacket(p);
            }
        }
    }

    /**
     * Este m�todo deshecha el paquete pasado por parametro.
     *
     * @param paquete El paquete que se desea descartar.
     * @since 1.0
     */
    public abstract void discardPacket(TPDU paquete);

    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto.
     *
     * @param paquete El paquete que queremos que sea recivido en el puerto.
     * @since 1.0
     */
    public abstract void addPacket(TPDU paquete);

    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto. Es
     * igual que el m�todo addPacket(), salvo que no genera eventos y lo hace
     * silenciosamente.
     *
     * @param paquete El paquete que queremos que reciba el puerto.
     * @since 1.0
     */
    public abstract void reEnqueuePacket(TPDU paquete);

    /**
     * este m�todo lee un paquete del buffer de recepci�n del puerto. El paquete
     * leido depender� del algoritmo de gesti�n de los b�fferes que implemente
     * el puerto. Por defecto, es un FIFO Droptail.
     *
     * @return El paquete le�do.
     * @since 1.0
     */
    public abstract TPDU getPacket();

    /**
     * Este m�todo calcula si podemos conmutar el siguiente paquete del nodo,
     * dado el n�mero de octetos que como mucho podemos conmutar en un momento
     * dado.
     *
     * @param octetos El n�mero de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso
     * contrario.
     * @since 1.0
     */
    public abstract boolean canSwitchPacket(int octetos);

    /**
     * Este m�todo obtiene la congesti�n total el puerto, en porcentaje.
     *
     * @return El porcentaje de ocupaci�n del puerto.
     * @since 1.0
     */
    public abstract long getCongestionLevel();

    /**
     * Este m�todo comprueba si hay paquetes esperando en el buffer de recepci�n
     * o no.
     *
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso
     * contrario.
     * @since 1.0
     */
    public abstract boolean thereIsAPacketWaiting();

    /**
     * Este m�todo calcula el total de octetos que suman los paquetes que
     * actualmente hay en el buffer de recepci�n del puerto.
     *
     * @return El tama�o en octetos del total de paquetes en el buffer de
     * recepci�n.
     * @since 1.0
     */
    public abstract long getOccupancy();

    /**
     * Este m�todo calcula el n�mero de paquetes total que hay en el buffer del
     * puerto.
     *
     * @return El n�mero total de paquetes que hay en el puerto.
     * @since 1.0
     */
    public abstract int getNumberOfPackets();

    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser
     * creados por el constructor.
     *
     * @since 1.0
     */
    public abstract void reset();

    /**
     * Este m�todo permite saltarse las limitaciones de tama�o del buffer y
     * establecer �ste como un buffer ideal, con capacidad infinita.
     *
     * @param bi TRUE indica que el buffer se tomar� como ilimitado. FALSE
     * indica que el buffer tendr� el tama�o especificado en el resto de
     * m�todos.
     * @since 1.0
     */
    public abstract void setUnlimitedBuffer(boolean bi);

    /**
     * Este atributo es el identifier del puerto. Como los nodos tienen m�s de
     * un puerto, es necesario un identifier para referirse a cada uno de ellos.
     *
     * @since 1.0
     */
    //protected int identifier;
    /**
     * Este atributo es el link de la topolog�a al que est� unido el puerto.
     * Todo puerto o est� libre o est� unido a un link, que es este.
     *
     * @since 1.0
     */
    protected TLink link;
    /**
     * Este atributo es una referencia al superconjunto de todos los puertos de
     * un nodo al que pertenece este.
     *
     * @since 1.0
     */
    protected TPortSet parentPortSet;
    /**
     * Este atributo es un monitor que sirve para crear secciones cr�ticas,
     * actuando de barrera, para sincronizar el acceso concurrente a algunas
     * zonas del objeto.
     *
     * @since 1.0
     */
    protected TMonitor monitor;
    /**
     * Este atributo almacenar� el identifier que indica el n�mero de puerto que
     * ocupa la instancia actual dentro del conjunto de puertos de un nodo.
     *
     * @since 1.0
     */
    protected int portID;

}
