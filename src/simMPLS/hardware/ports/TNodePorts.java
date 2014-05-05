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
import simMPLS.scenario.TNode;
import simMPLS.protocols.TPDU;
import simMPLS.utils.TMonitor;


/**
 * Esta clase abstracta es la superclase del conjunto de puertos que hay en un parentNode.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TNodePorts {

    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TPuertosNodo.
     * @param num Numero de puertos que contendr� el conjunto e puertos.
     * @param n Referencia al parentNode al que pertenece este conjunto de puertos.
     * @since 1.0
     */
    public TNodePorts(int num, TNode n) {
        this.numberOfPorts = num;
        this.parentNode = n;
        this.portSetBufferSize = 1;
        this.portSetBufferOccupancy = 0;
        this.portSetMonitor = new TMonitor();
        this.artificiallyCongested = false;
        this.occupancy = 0;
    }

    /**
     * Este m�todo incrementa la cantidad de buffer que est� ocupada.
     * @param tcp Tama�o (en octetos) en que se debe incrementar la ocupaci�n total.
     * @since 1.0
     */    
    public synchronized void increasePortSetOccupancy(long tcp) {
        this.portSetBufferOccupancy += tcp;
    }
    
    /**
     * Este m�todo decrementa la cantidad de buffer que est� ocupada.
     * @param tcp Tama�o (en octetos) en que se debe decrementar la ocupaci�n total.
     * @since 1.0
     */    
    public synchronized void decreasePortSetOccupancySize(long tcp) {
        this.portSetBufferOccupancy -= tcp;
    }

    /**
     * Este m�todo establece la cantidad de buffer que est� ocupada.
     * @param tcp Tama�o (en octetos) del buffer que est� ocupado.
     * @since 1.0
     */    
    public synchronized void setPortSetOccupancySize(long tcp) {
        this.portSetBufferOccupancy = tcp;
    }
    
    /**
     * Este m�todo obtiene el tama�o de buffer que est� ocupado actualmente.
     * @return Tama�o (en octetos) del buffer que est� ocupado.
     * @since 1.0
     */    
    public synchronized long getPortSetOccupancy() {
        return this.portSetBufferOccupancy;
    }
    
    /**
     * Este m�todo obtiene si el parentNode est� saturado artificialmente o no.
     * @since 1.0
     * @return TRUE, si el parentNode est� saturado artificialmente. FALSE en caso contrario.
     */    
    public boolean getArtificiallyCongested() {
        return this.artificiallyCongested;
    }
    
    /**
     * Este m�todo es abstracto. Saturar� o no artificialmente el puerto.
     * @since 1.0
     * @param sa TRUE, si el parentNode se saturar� artificialmente. FALSE en caso contrario.
     */    
    public abstract void setArtificiallyCongested(boolean sa);

    /**
     * Este m�todo devuelve el n�mero de puerto que tiene el conjunto de puertos.
     * @return El n�mero de puertos del conjunto de puertos.
     * @since 1.0
     */    
    public int getNumberOfPorts() {
        return numberOfPorts;
    }

    /**
     * Este m�todo establece qu� parentNode es el propietario de este conjunto de puertos.
     * @param n El parentNode poseedor de este conjunto de puertos.
     * @since 1.0
     */    
    public void setNode(TNode n) {
        parentNode = n;
    }

    /**
     * Este m�todo devuelve una referencia al parentNode que es poseedor de este conjunto de
 puertos.
     * @return El parentNode que posee este conjunto de puertos.
     * @since 1.0
     */    
    public TNode getNode() {
        return parentNode;
    }

    /**
     * Este m�todo lee y devuelve un paquete de uno de lo puertos. El que toque.
     * @return Un paquete le�do de uno de los puertos del conjunto de puertos.
     * @since 1.0
     */    
    public abstract TPDU isAnyPacketWaiting();
    /**
     * Este m�todo comprueba si hay o no paquetes esperando en el buffer de recepci�n.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean isAnyPacketToSwitch();
    /**
     * Este m�todo comprueba si hay o no paquetes esperando en el buffer de recepci�n.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean isAnyPacketToRoute();
    /**
     * Este m�todo comprueba si se puede conmutar el siguiente paquete del conjunto de
 puertos, teniendo como referencia el n�mero m�ximo de octetos que el parentNode puede
 conmutar en ese instante.
     * @param octetos El n�mero de octetos que el parentNode puede conmutar en ese instante.
     * @return TRUE, si puedo conmutar un nuevo paquete. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean canSwitchPacket(int octetos);
    /**
     * Este m�todo salta un puerto que tocaba ser le�do y lee el siguiente.
     * @since 1.0
     */    
    public abstract void skipPort();
    /**
     * Este m�todo devuelve el idetificador del puerto del que se ley� el �ltimo
     * paquete.
     * @return El identificador del �ltimo puerto le�do.
     * @since 1.0
     */    
    public abstract int getReadPort();
    /**
     * Este m�todo toma una direcci�n IP y devuelve el puerto del conjunto de puertos,
 en cuyo extremo est� el parentNode con dicha IP.
     * @param ip IP a la que se desea acceder.
     * @return El puerto que conecta al parentNode cuya IP es la especificada. NULL en caso de que no
 hay conexi�n directa con dicho parentNode/IP.
     * @since 1.0
     */    
    public abstract TPort getPortWhereIsConectedANodeHavingIP(String ip);
    /**
     * Este m�todo consulta un puerto para obtener la IP del parentNode que se encuentra en
 el otro extremo.
     * @param p Identificado de un puerto del conjunto de puertos.
     * @return IP del parentNode destino al que est� unido el puerto especificado. NULL si el puerto
 est� libre.
     * @since 1.0
     */    
    public abstract String getIPOfNodeLinkedTo(int p);
    /**
     * Este m�todo calcula la congesti�n global del conjunto de puertos, en forma de
     * porcentaje.
     * @return Porcentaje (0%-100%) de congesti�n del conjunto de puertos.
     * @since 1.0
     */    
    public abstract long getCongestionLevel();
    /**
     * Este m�todo reinicia el valor de todos los atributos de la clase, como si
     * acabasen de ser creados en el constructor.
     * @since 1.0
     */    
    public abstract void reset();
    /**
     * Este m�todo toma un enlace y lo conecta a un puerto concreto del conjunto de
     * puertos.
     * @param e Enlace que queremos conectar.
     * @param p Identificador del puerto del conjunto de puertos al que se conectar� el enlace.
     * @since 1.0
     */    
    public abstract void connectLinkToPort(TTopologyLink e, int p);
    /**
     * Este m�todo devuelve el enlace al que est� conectado un puerto del conjunto de
     * puertos.
     * @param p Identificador de puerto de uno de los puertos del conjunto.
     * @return Enlace al que est� conectado el puerto especificado. NULL si el puerto est�
     * libre.
     * @since 1.0
     */    
    public abstract TTopologyLink getLinkConnectedToPort(int p);
    /**
     * Este m�todo desconecta un enlace de un puerto concreto, dej�ndolo libre.
     * @param p El identificador del puerto del conjunto de puertos, que queremos desconectar y
     * dejar libre.
     * @since 1.0
     */    
    public abstract void disconnectLinkFromPort(int p);
    /**
     * Este m�todo establece el conjunto de puertos como ideal, sin
     * restricciones de tama�o, ilimitado.
     * @param bi TRUE, indica que el buffer del conjunto de puertos es ilimitado. FALSE, indica que no es
     * ilimitado.
     * @since 1.0
     */    
    public abstract void setUnlimitedBuffer(boolean bi);
    /**
     * Este m�todo devuelve el puerto cuyo identificador coincida con el especificado.
     * @param numPuerto Identificador del puerto que queremos obtener.
     * @return El puerto que deseamos obtener. NULL si el puerto con ese identificador no
     * existe.
     * @since 1.0
     */    
    public abstract TPort getPort(int numPuerto);
    /**
     * Este m�todo establece el tama�o que tendr� el buffer del conjunto de puertos. Si
     * el conjunto de puertos est� definido como ilimitado, este m�todo no tiene efecto.
     * @param tamEnMB Tama�o en MB del buffer del conjunto de puertos.
     * @since 1.0
     */    
    public abstract void setBufferSizeInMB(int tamEnMB);
    /**
     * Este m�todo devuelve el tama�o del buffer del conjunto de puertos.
     * @return El tama�o del buffer del conjunto de puertos en MB.
     * @since 1.0
     */    
    public abstract int getBufferSizeInMB();
    /**
     * Este m�todo comprueba si un puerto concreto del conjunto de puertos est� libre o
     * si por el contrario est� conectado.
     * @param p Identificador del puerto que queremos consultar.
     * @return TRUE, indica que el puerto est� sin conectar. FALSE indica que el puerto ya est�
     * conectado a un enlace.
     * @since 1.0
     */    
    public abstract boolean isAvailable(int p);
    /**
     * Este m�todo comprueba si hay alg�n puerto libre, es decir, sin conectar a un
     * enelace, en el conjunto de puertos.
     * @return TRUE, si al menos uno de los puertos del conjunto de puertos est� sin conectar.
     * FALSE si todos est�n conectado ya a un enlace.
     * @since 1.0
     */    
    public abstract boolean isAnyPortAvailable();
    
    /**
     * Este atributo de la clase especifica el n�mero de puertos que hay en el conjunto
     * de puertos.
     * @since 1.0
     */    
    protected int numberOfPorts;
    /**
     * Este atributo es una referencia al parentNode que es due�o de este conjunto de
 puertos.
     * @since 1.0
     */    
    protected TNode parentNode;
    /**
     * Este atributo indica el tama�o en MB del b�ffer existente para el conjunto de
     * puertos.
     * @since 1.0
     */    
    protected int portSetBufferSize;
    /**
     * Este atributo almacena el tama�o del buffer del conjunto de puertos que
     * actualmente est� ocupado. En octetos.
     * @since 1.0
     */    
    private long portSetBufferOccupancy;
    /**
     * Este atributo es un Monitor gen�rico que se usa para crear regiones cr�ticas que
     * sirvan para sincronizar accesos concurrentes.
     * @since 1.0
     */    
    public TMonitor portSetMonitor;
    /**
     * Este atributo almacena el estado de saturaci�n artificial o no no saturaci�n
     * artificial del puerto.
     * @since 1.0
     */    
    protected boolean artificiallyCongested;
    /**
     * Este atributo almacena la verdadera ocupaci�n del parentNode. As� se permite restaurar
     * este valor tras una congesti�n artificial.
     * @since 1.0
     */    
    protected long occupancy;
}
