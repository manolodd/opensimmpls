//**************************************************************************
// Nombre......: TPuerto.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa un puerto de comunicaciones de un no-
// ............: do de la topologia.
// Fecha.......: 12/03/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.puertos;

import java.util.Iterator;
import java.util.LinkedList;
import simMPLS.escenario.TESPaqueteRecibido;
import simMPLS.escenario.TEstadisticas;
import simMPLS.escenario.TNodoTopologia;
import simMPLS.protocolo.TPDU;


/**
 * Esta clase implementa un puerto de entrada/salida v�lido para cualquiera de
 * los nodos del simulador.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPuertoNormal extends TPuerto {

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TPuertoNormal.
     * @since 1.0
     * @param idp Identificador (n�mero) del puerto.
     * @param cpn Referencia al superconjunto de puertos del que este puerto forma parte.
     */
    public TPuertoNormal(TPuertosNodo cpn, int idp) {
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
    public void ponerBufferIlimitado(boolean bi) {
        this.bufferIlimitado = bi;
    }

    /**
     * Este m�todo deshecha el paquete pasado por parametro.
     * @param paquete El paquete que se desea descartar.
     * @since 1.0
     */    
    @Override
    public void descartarPaquete(TPDU paquete) {
        this.obtenerCjtoPuertos().obtenerNodo().descartarPaquete(paquete);
    }
    
    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto.
     * @param paquete El paquete que queremos que sea recivido en el puerto.
     * @since 1.0
     */    
    @Override
    public void ponerPaquete(TPDU paquete) {
        TPuertosNodoNormal cjtoPuertosAux = (TPuertosNodoNormal) cjtoPuertos;
        cjtoPuertosAux.cerrojoCjtoPuertos.bloquear();
        cerrojo.bloquear();
        TNodoTopologia nt = this.cjtoPuertos.obtenerNodo();
        long idEvt = 0;
        try {
            idEvt = nt.gILargo.obtenerNuevo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tipo = paquete.obtenerSubTipo();
        if (this.bufferIlimitado) {
            buffer.addLast(paquete);
            cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
            TESPaqueteRecibido evt = new TESPaqueteRecibido(nt, idEvt, this.obtenerCjtoPuertos().obtenerNodo().obtenerInstanteDeTiempo(), tipo, paquete.obtenerTamanio());
            nt.suscriptorSimulacion.capturarEventoSimulacion(evt);
            if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(paquete, TEstadisticas.ENTRADA);
            }
        } else {
            if ((cjtoPuertosAux.obtenerTamanioOcupadoCjtoPuertos() + paquete.obtenerTamanio()) <= (cjtoPuertosAux.obtenerTamanioBuffer()*1024*1024)) {
                buffer.addLast(paquete);
                cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
                TESPaqueteRecibido evt = new TESPaqueteRecibido(nt, idEvt, this.obtenerCjtoPuertos().obtenerNodo().obtenerInstanteDeTiempo(), tipo, paquete.obtenerTamanio());
                nt.suscriptorSimulacion.capturarEventoSimulacion(evt);
                if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                    this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(paquete, TEstadisticas.ENTRADA);
                }
            } else {
                this.descartarPaquete(paquete);                
            }
        }
        cerrojo.liberar();
        cjtoPuertosAux.cerrojoCjtoPuertos.liberar();
    }

    /**
     * Este m�todo inserta un paquete en el buffer de recepci�n del puerto. Es igual
     * que el m�todo ponerPaquete(), salvo que no genera eventos y lo hace
     * silenciosamente.
     * @param paquete El paquete que queremos que reciba el puerto.
     * @since 1.0
     */    
    @Override
    public void reencolar(TPDU paquete) {
        TPuertosNodoNormal cjtoPuertosAux = (TPuertosNodoNormal) cjtoPuertos;
        cjtoPuertosAux.cerrojoCjtoPuertos.bloquear();
        cerrojo.bloquear();
        TNodoTopologia nt = this.cjtoPuertos.obtenerNodo();
        long idEvt = 0;
        try {
            idEvt = nt.gILargo.obtenerNuevo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tipo = paquete.obtenerSubTipo();
        if (this.bufferIlimitado) {
            buffer.addLast(paquete);
            cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
        } else {
            if ((cjtoPuertosAux.obtenerTamanioOcupadoCjtoPuertos() + paquete.obtenerTamanio()) <= (cjtoPuertosAux.obtenerTamanioBuffer()*1024*1024)) {
                buffer.addLast(paquete);
                cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
            } else {
                this.descartarPaquete(paquete);                
            }
        }
        cerrojo.liberar();
        cjtoPuertosAux.cerrojoCjtoPuertos.liberar();
    }
    
    /**
     * este m�todo lee un paquete del buffer de recepci�n del puerto. El paquete leido
     * depender� del algoritmo de gesti�n de los b�fferes que implemente el puerto. Por
     * defecto, es un FIFO Droptail.
     * @return El paquete le�do.
     * @since 1.0
     */    
    @Override
    public TPDU obtenerPaquete() {
        TPuertosNodoNormal cjtoPuertosAux = (TPuertosNodoNormal) cjtoPuertos;
        cjtoPuertosAux.cerrojoCjtoPuertos.bloquear();
        cerrojo.bloquear();
        paqueteDevuelto = (TPDU) buffer.removeFirst();
        if (!this.bufferIlimitado) {
                cjtoPuertosAux.decrementarTamanioOcupadoCjtoPuertos(paqueteDevuelto.obtenerTamanio());
        }
        cerrojo.liberar();
        cjtoPuertosAux.cerrojoCjtoPuertos.liberar();
        return paqueteDevuelto;
    }
    
    /**
     * Este m�todo calcula si podemos conmutar el siguiente paquete del nodo, dado el
     * n�mero de octetos que como mucho podemos conmutar en un momento dado.
     * @param octetos El n�mero de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean puedoConmutarPaquete(int octetos) {
        cerrojo.bloquear();
        paqueteDevuelto = (TPDU) buffer.getFirst();
        cerrojo.liberar();
        if (paqueteDevuelto.obtenerTamanio() <= octetos) {
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
    public long obtenerCongestion() {
        if (this.bufferIlimitado) {
            return 0;
        } 
        TPuertosNodoNormal tpn = (TPuertosNodoNormal) cjtoPuertos;
        long cong = (tpn.obtenerTamanioOcupadoCjtoPuertos()*100) / (tpn.obtenerTamanioBuffer()*1024*1024);
        return cong;
    }

    /**
     * Este m�todo comprueba si hay paquetes esperando en el buffer de recepci�n o no.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean hayPaqueteEsperando() {
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
    public long obtenerOcupacion() {
        if (this.bufferIlimitado) {
            this.cerrojo.bloquear();
            int ocup=0;
            TPDU paquete = null;
            Iterator it = this.buffer.iterator();
            while (it.hasNext()) {
                paquete = (TPDU) it.next();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojo.liberar();
            return ocup;
        }
        TPuertosNodoNormal tpn = (TPuertosNodoNormal) cjtoPuertos;
        return tpn.obtenerTamanioOcupadoCjtoPuertos();
    }

    /**
     * Este m�todo calcula el n�mero de paquetes total que hay en el buffer del puerto.
     * @return El n�mero total de paquetes que hay en el puerto.
     * @since 1.0
     */    
    @Override
    public int obtenerNumeroPaquetes() {
        return buffer.size();
    }

    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 1.0
     */    
    @Override
    public void reset() {
        this.cerrojo.bloquear();
        Iterator it = this.buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojo.liberar();
    }
    
    private LinkedList buffer;
    private TPDU paqueteDevuelto;
    private boolean bufferIlimitado;
}
