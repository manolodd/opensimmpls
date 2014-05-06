package simMPLS.hardware.ports;

import java.util.Iterator;
import java.util.LinkedList;
import simMPLS.scenario.TSEPacketReceived;
import simMPLS.scenario.TStats;
import simMPLS.scenario.TNode;
import simMPLS.protocols.TPDU;


/**
 * Esta clase implementa un puerto de entrada/salida v�lido para cualquiera de
 * los nodos del simulador.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TNormalPort extends TPort {

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TPuertoNormal.
     * @since 1.0
     * @param idp Identificador (n�mero) del puerto.
     * @param cpn Referencia al superconjunto de puertos del que este puerto forma parte.
     */
    public TNormalPort(TPortSet cpn, int idp) {
        super(cpn, idp);
        buffer = new LinkedList();
        paqueteDevuelto = null;
        bufferIlimitado = false;
    }

    /**
     * Este m�todo permite saltarse las limitaciones de tama�o del buffer y establecer
     * �ste como un buffer ideal, con capacidad infinita.
     * @param bi TRUE indica que el buffer se tomar� como ilimitado. FALSE indica que el buffer
     * tendr� el tama�o especificado en el resto de m�todos.
     * @since 1.0
     */    
    @Override
    public void setUnlimitedBuffer(boolean bi) {
        this.bufferIlimitado = bi;
    }

    /**
     * Este m�todo deshecha el paquete pasado por parametro.
     * @param paquete El paquete que se desea descartar.
     * @since 1.0
     */    
    @Override
    public void discardPacket(TPDU paquete) {
        this.getPortSet().getNode().discardPacket(paquete);
    }
    
    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto.
     * @param paquete El paquete que queremos que sea recivido en el puerto.
     * @since 1.0
     */    
    @Override
    public void addPacket(TPDU paquete) {
        TNormalPortSet cjtoPuertosAux = (TNormalPortSet) parentPortSet;
        cjtoPuertosAux.portSetMonitor.lock();
        monitor.lock();
        TNode nt = this.parentPortSet.getNode();
        long idEvt = 0;
        try {
            idEvt = nt.longIdentifierGenerator.getNextID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tipo = paquete.getSubtype();
        if (this.bufferIlimitado) {
            buffer.addLast(paquete);
            cjtoPuertosAux.increasePortSetOccupancy(paquete.getSize());
            TSEPacketReceived evt = new TSEPacketReceived(nt, idEvt, this.getPortSet().getNode().getAvailableTime(), tipo, paquete.getSize());
            nt.simulationEventsListener.captureSimulationEvents(evt);
            if (this.getPortSet().getNode().getStats() != null) {
                this.getPortSet().getNode().getStats().addStatsEntry(paquete, TStats.ENTRADA);
            }
        } else {
            if ((cjtoPuertosAux.getPortSetOccupancy() + paquete.getSize()) <= (cjtoPuertosAux.getBufferSizeInMB()*1024*1024)) {
                buffer.addLast(paquete);
                cjtoPuertosAux.increasePortSetOccupancy(paquete.getSize());
                TSEPacketReceived evt = new TSEPacketReceived(nt, idEvt, this.getPortSet().getNode().getAvailableTime(), tipo, paquete.getSize());
                nt.simulationEventsListener.captureSimulationEvents(evt);
                if (this.getPortSet().getNode().getStats() != null) {
                    this.getPortSet().getNode().getStats().addStatsEntry(paquete, TStats.ENTRADA);
                }
            } else {
                this.discardPacket(paquete);                
            }
        }
        monitor.unLock();
        cjtoPuertosAux.portSetMonitor.unLock();
    }

    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto. Es igual
 que el m�todo addPacket(), salvo que no genera eventos y lo hace
 silenciosamente.
     * @param paquete El paquete que queremos que reciba el puerto.
     * @since 1.0
     */    
    @Override
    public void reEnqueuePacket(TPDU paquete) {
        TNormalPortSet cjtoPuertosAux = (TNormalPortSet) parentPortSet;
        cjtoPuertosAux.portSetMonitor.lock();
        monitor.lock();
        TNode nt = this.parentPortSet.getNode();
        long idEvt = 0;
        try {
            idEvt = nt.longIdentifierGenerator.getNextID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tipo = paquete.getSubtype();
        if (this.bufferIlimitado) {
            buffer.addLast(paquete);
            cjtoPuertosAux.increasePortSetOccupancy(paquete.getSize());
        } else {
            if ((cjtoPuertosAux.getPortSetOccupancy() + paquete.getSize()) <= (cjtoPuertosAux.getBufferSizeInMB()*1024*1024)) {
                buffer.addLast(paquete);
                cjtoPuertosAux.increasePortSetOccupancy(paquete.getSize());
            } else {
                this.discardPacket(paquete);                
            }
        }
        monitor.unLock();
        cjtoPuertosAux.portSetMonitor.unLock();
    }
    
    /**
     * este m�todo lee un paquete del buffer de recepci�n del puerto. El paquete leido
     * depender� del algoritmo de gesti�n de los b�fferes que implemente el puerto. Por
     * defecto, es un FIFO Droptail.
     * @return El paquete le�do.
     * @since 1.0
     */    
    @Override
    public TPDU getPacket() {
        TNormalPortSet cjtoPuertosAux = (TNormalPortSet) parentPortSet;
        cjtoPuertosAux.portSetMonitor.lock();
        monitor.lock();
        paqueteDevuelto = (TPDU) buffer.removeFirst();
        if (!this.bufferIlimitado) {
                cjtoPuertosAux.decreasePortSetOccupancySize(paqueteDevuelto.getSize());
        }
        monitor.unLock();
        cjtoPuertosAux.portSetMonitor.unLock();
        return paqueteDevuelto;
    }
    
    /**
     * Este m�todo calcula si podemos conmutar el siguiente paquete del parentNode, dado el
 n�mero de octetos que como mucho podemos conmutar en un momento dado.
     * @param octetos El n�mero de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean canSwitchPacket(int octetos) {
        monitor.lock();
        paqueteDevuelto = (TPDU) buffer.getFirst();
        monitor.unLock();
        if (paqueteDevuelto.getSize() <= octetos) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo obtiene la congesti�n total el puerto, en porcentaje.
     * @return El porcentaje de ocupaci�n del puerto.
     * @since 1.0
     */    
    @Override
    public long getCongestionLevel() {
        if (this.bufferIlimitado) {
            return 0;
        } 
        TNormalPortSet tpn = (TNormalPortSet) parentPortSet;
        long cong = (tpn.getPortSetOccupancy()*100) / (tpn.getBufferSizeInMB()*1024*1024);
        return cong;
    }

    /**
     * Este m�todo comprueba si hay paquetes esperando en el buffer de recepci�n o no.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean thereIsAPacketWaiting() {
        if (buffer.size() > 0)
            return true;
        return false;
    }

    /**
     * Este m�todo calcula el total de octetos que suman los paquetes que actualmente
     * hay en el buffer de recepci�n del puerto.
     * @return El tama�o en octetos del total de paquetes en el buffer de recepci�n.
     * @since 1.0
     */    
    @Override
    public long getOccupancy() {
        if (this.bufferIlimitado) {
            this.monitor.lock();
            int ocup=0;
            TPDU paquete = null;
            Iterator it = this.buffer.iterator();
            while (it.hasNext()) {
                paquete = (TPDU) it.next();
                if (paquete != null)
                    ocup += paquete.getSize();
            }
            this.monitor.unLock();
            return ocup;
        }
        TNormalPortSet tpn = (TNormalPortSet) parentPortSet;
        return tpn.getPortSetOccupancy();
    }

    /**
     * Este m�todo calcula el n�mero de paquetes total que hay en el buffer del puerto.
     * @return El n�mero total de paquetes que hay en el puerto.
     * @since 1.0
     */    
    @Override
    public int getNumberOfPackets() {
        return buffer.size();
    }

    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 1.0
     */    
    @Override
    public void reset() {
        this.monitor.lock();
        Iterator it = this.buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.monitor.unLock();
    }
    
    private LinkedList buffer;
    private TPDU paqueteDevuelto;
    private boolean bufferIlimitado;
}
