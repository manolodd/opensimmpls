//**************************************************************************
// Nombre......: TNodePorts.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa el conjunto de puertos de un parentNode de
// ............: la topolog�a.
// Fecha.......: 06/03/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.ports;

import simMPLS.scenario.TTopologyLink;
import simMPLS.scenario.TTopologyNode;
import simMPLS.protocols.TPDU;


/**
 * Esta clase implementa un conjunto de puertos de un parentNode.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TNormalNodePorts extends TNodePorts {

    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TPuertosNodoNormal.
     * @param num Numero de puertos que contendr� el conjunto e puertos.
     * @param n Referencia al parentNode al que pertenece este conjunto de puertos.
     * @since 1.0
     */
    public TNormalNodePorts(int num, TTopologyNode n) {
        super(num, n);
        puertos = new TNormalPort[num];
        int i=0;
        for (i=0; i<this.numberOfPorts; i++) {
            puertos[i] = new TNormalPort(this, i);
            puertos[i].ponerIdentificador(i);
        }
        puertoLeido = 0;
    }

    /**
     * Este m�todo establece el conjunto de puertos como ideal, sin
     * restricciones de tama�o, ilimitado.
     * @param bi TRUE, indica que el buffer del conjunto de puertos es ilimitado. FALSE, indica que no es
     * ilimitado.
     * @since 1.0
     */    
    @Override
    public void setUnlimitedBuffer(boolean bi) {
        int i=0;
        for (i=0; i<this.numberOfPorts; i++) {
            puertos[i].setUnlimitedBuffer(bi);
        }
    }
    
    /**
     * Este m�todo devuelve el puerto cuyo identificador coincida con el especificado.
     * @param numPuerto Identificador del puerto que queremos obtener.
     * @return El puerto que deseamos obtener. NULL si el puerto con ese identificador no
     * existe.
     * @since 1.0
     */    
    @Override
    public TPort getPort(int numPuerto) {
        if (numPuerto < this.numberOfPorts)
            return puertos[numPuerto];
        return null;
    }

    /**
     * Este m�todo establece el tama�o que tendr� el buffer del conjunto de puertos. Si
     * el conjunto de puertos est� definido como ilimitado, este m�todo no tiene efecto.
     * @param tamEnMB Tama�o en MB del buffer del conjunto de puertos.
     * @since 1.0
     */    
    @Override
    public void setBufferSizeInMB(int tamEnMB) {
        this.portSetBufferSize = tamEnMB;
    }
    
    /**
     * Este m�todo devuelve el tama�o del buffer del conjunto de puertos.
     * @return El tama�o del buffer del conjunto de puertos en MB.
     * @since 1.0
     */    
    @Override
    public int getBufferSizeInMB() {
        return this.portSetBufferSize;
    }
    
    /**
     * Este m�todo comprueba si un puerto concreto del conjunto de puertos est� libre o
     * si por el contrario est� conectado.
     * @param p Identificador del puerto que queremos consultar.
     * @return TRUE, indica que el puerto est� sin conectar. FALSE indica que el puerto ya est�
     * conectado a un enlace.
     * @since 1.0
     */    
    @Override
    public boolean isAvailable(int p) {
        if (p < this.numberOfPorts)
            return puertos[p].isAvailable();
        return false;
    }

    /**
     * Este m�todo comprueba si hay alg�n puerto libre, es decir, sin conectar a un
     * enelace, en el conjunto de puertos.
     * @return TRUE, si al menos uno de los puertos del conjunto de puertos est� sin conectar.
     * FALSE si todos est�n conectado ya a un enlace.
     * @since 1.0
     */    
    @Override
    public boolean isAnyPortAvailable() {
        int i=0;
        for (i=0; i<this.numberOfPorts; i++) {
            if (puertos[i].isAvailable())
                return true;
        }
        return false;
    }

    /**
     * Este m�todo toma un enlace y lo conecta a un puerto concreto del conjunto de
     * puertos.
     * @param e Enlace que queremos conectar.
     * @param p Identificador del puerto del conjunto de puertos al que se conectar� el enlace.
     * @since 1.0
     */    
    @Override
    public void connectLinkToPort(TTopologyLink e, int p) {
        if (p < this.numberOfPorts)
            if (puertos[p].isAvailable())
                puertos[p].setLink(e);
    } 

    /**
     * Este m�todo devuelve el enlace al que est� conectado un puerto del conjunto de
     * puertos.
     * @param p Identificador de puerto de uno de los puertos del conjunto.
     * @return Enlace al que est� conectado el puerto especificado. NULL si el puerto est�
     * libre.
     * @since 1.0
     */    
    @Override
    public TTopologyLink getLinkConnectedToPort(int p) {
        if (p < this.numberOfPorts)
            if (!puertos[p].isAvailable())
                return puertos[p].getLink();
        return null;
    } 

    /**
     * Este m�todo desconecta un enlace de un puerto concreto, dej�ndolo libre.
     * @param p El identificador del puerto del conjunto de puertos, que queremos desconectar y
     * dejar libre.
     * @since 1.0
     */    
    @Override
    public void disconnectLinkFromPort(int p) {
        if ((p >= 0) && (p < this.numberOfPorts))
            puertos[p].disconnectLink();
    } 

    /**
     * Este m�todo lee y devuelve un paquete de uno de lo puertos. El que toque.
     * @return Un paquete le�do de uno de los puertos del conjunto de puertos.
     * @since 1.0
     */    
    @Override
    public TPDU isAnyPacketWaiting() {
        for (int i=0; i<numberOfPorts; i++) {
            puertoLeido = (puertoLeido + 1) % numberOfPorts;
            if (puertos[puertoLeido].thereIsAPacketWaiting()) {
                return puertos[puertoLeido].getPacket();
            }
        }
        return null;
    }

    /**
     * Este m�todo comprueba si hay o no paquetes esperando en el buffer de recepci�n.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean isAnyPacketToSwitch() {
        for (int i=0; i<numberOfPorts; i++) {
            if (puertos[i].thereIsAPacketWaiting()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Este m�todo comprueba si hay o no paquetes esperando en el buffer de recepci�n.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean isAnyPacketToRoute() {
        return isAnyPacketToSwitch();
    }
    
    /**
     * Este m�todo comprueba si se puede conmutar el siguiente paquete del conjunto de
 puertos, teniendo como referencia el n�mero m�ximo de octetos que el parentNode puede
 conmutar en ese instante.
     * @param octetos El n�mero de octetos que el parentNode puede conmutar en ese instante.
     * @return TRUE, si puedo conmutar un nuevo paquete. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean canSwitchPacket(int octetos) {
        int puertosSinPaquete = 0;
        while (puertosSinPaquete < this.numberOfPorts) {
            if (puertos[((puertoLeido + 1) % numberOfPorts)].thereIsAPacketWaiting()) {
                return puertos[((puertoLeido + 1) % numberOfPorts)].canSwitchPacket(octetos);
            } else {
                puertosSinPaquete++;
                this.skipPort();
            }
        }
        return false;
    }
    
    /**
     * Este m�todo salta un puerto que tocaba ser le�do y lee el siguiente.
     * @since 1.0
     */    
    @Override
    public void skipPort() {
        puertoLeido = (puertoLeido + 1) % numberOfPorts;
    }
    
    /**
     * Este m�todo devuelve el idetificador del puerto del que se ley� el �ltimo
     * paquete.
     * @return El identificador del �ltimo puerto le�do.
     * @since 1.0
     */    
    @Override
    public int getReadPort() {
        return puertoLeido;
    }

    /**
     * Este m�todo toma una direcci�n IP y devuelve el puerto del conjunto de puertos,
 en cuyo extremo est� el parentNode con dicha IP.
     * @param ip IP a la que se desea acceder.
     * @return El puerto que conecta al parentNode cuya IP es la especificada. NULL en caso de que no
 hay conexi�n directa con dicho parentNode/IP.
     * @since 1.0
     */    
    @Override
    public TPort getPortWhereIsConectedANodeHavingIP(String ip) {
        for (int i=0; i<numberOfPorts; i++) {
            if (!puertos[i].isAvailable()) {
                int destino = puertos[i].getLink().getTargetNodeIDOfTrafficSentBy(parentNode);
                if (destino == TTopologyLink.END_NODE_1) {
                    if (puertos[i].getLink().obtenerExtremo1().getIPAddress().equals(ip)) {
                        return puertos[i];
                    }
                } else {
                    if (puertos[i].getLink().obtenerExtremo2().getIPAddress().equals(ip)) {
                        return puertos[i];
                    }
                }
            }
        }
        return null;
    }

    /**
     * Este m�todo consulta un puerto para obtener la IP del parentNode que se encuentra en
 el otro extremo.
     * @param p Identificado de un puerto del conjunto de puertos.
     * @return IP del parentNode destino al que est� unido el puerto especificado. NULL si el puerto
 est� libre.
     * @since 1.0
     */    
    @Override
    public String getIPOfNodeLinkedTo(int p) {
        if ((p >= 0) && (p < this.numberOfPorts)) {
            if (!puertos[p].isAvailable()) {
                String IP2 = puertos[p].getLink().obtenerExtremo2().getIPAddress();
                if (puertos[p].getLink().obtenerExtremo1().getIPAddress().equals(parentNode.getIPAddress()))
                    return puertos[p].getLink().obtenerExtremo2().getIPAddress();
                return puertos[p].getLink().obtenerExtremo1().getIPAddress();
            }
        }
        return null;
    }

    /**
     * Este m�todo calcula la congesti�n global del conjunto de puertos, en forma de
     * porcentaje.
     * @return Porcentaje (0%-100%) de congesti�n del conjunto de puertos.
     * @since 1.0
     */    
    @Override
    public long getCongestionLevel() {
        long cong = 0;
        int i=0;
        for (i=0; i<this.numberOfPorts; i++) {
            if (puertos[i].getCongestionLevel() > cong)
                cong = puertos[i].getCongestionLevel();
        }
        return cong;
    }
    
    /**
     * Este m�todo reinicia el valor de todos los atributos de la clase, como si
     * acabasen de ser creados en el constructor.
     * @since 1.0
     */    
    @Override
    public void reset(){
        this.portSetMonitor.unLock();
        int i=0;
        for (i=0; i<this.numberOfPorts; i++) {
            puertos[i].reset();
        }
        this.puertoLeido = 0;
        this.setPortSetOccupancySize(0);
        this.artificiallyCongested = false;
        this.occupancy = 0;
        this.portSetMonitor.unLock();
    }
    
    /**
     * Este m�todo permite establecer el nivel de saturaci�n del parentNode en el 97% de tal
 forma que r�pidamente comience a descartar paquetes. Lo hace de forma artificial
     * y es capaz de volver al estado original en cualquier momento.
     * @param sa TRUE indica que el puerto se debe saturar artificialmente. FALSE indica lo
     * contrario.
     * @since 1.0
     */    
    @Override
    public void setArtificiallyCongested(boolean sa) {
        long calculo97PorCiento = (long) (this.getBufferSizeInMB()*1017118.72);
        if (sa) {
            if (!this.artificiallyCongested) {
                if (this.getPortSetOccupancySize() < calculo97PorCiento) {
                    this.artificiallyCongested = true;
                    this.occupancy = this.getPortSetOccupancySize();
                    this.setPortSetOccupancySize(calculo97PorCiento);
                }
            }
        } else {
            if (this.artificiallyCongested) {
                this.occupancy += (getPortSetOccupancySize() - calculo97PorCiento);
                if (this.occupancy < 0)
                    this.occupancy = 0;
                this.setPortSetOccupancySize(this.occupancy);
                this.artificiallyCongested = false;
                this.occupancy = 0;
            }
        }
    }
    
    private TPort[] puertos;
    private int puertoLeido;
}
