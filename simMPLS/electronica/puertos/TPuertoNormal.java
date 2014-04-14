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

/**
 * Esta clase implementa un puerto de entrada/salida válido para cualquiera de
 * los nodos del simulador.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPuertoNormal extends TPuerto {

    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de
     * TPuertoNormal.
     * @since 1.0
     * @param idp Identificador (número) del puerto.
     * @param cpn Referencia al superconjunto de puertos del que este puerto forma parte.
     */
    public TPuertoNormal(TPuertosNodo cpn, int idp) {
        super(cpn, idp);
        buffer = new LinkedList();
        paqueteDevuelto = null;
        bufferIlimitado = false;
    }

    /**
     * Este método permite saltarse las limitaciones de tamaño del buffer y establecer
     * éste como un buffer ideal, con capacidad infinita.
     * @param bi TRUE indica que el buffer se tomará como ilimitado. FALSE indica que el buffer
     * tendrá el tamaño especificado en el resto de métodos.
     * @since 1.0
     */    
    public void ponerBufferIlimitado(boolean bi) {
        this.bufferIlimitado = bi;
    }

    /**
     * Este método deshecha el paquete pasado por parametro.
     * @param paquete El paquete que se desea descartar.
     * @since 1.0
     */    
    public void descartarPaquete(TPDU paquete) {
        this.obtenerCjtoPuertos().obtenerNodo().descartarPaquete(paquete);
    }
    
    /**
     * Este método inserta un paquete en el buffer de recepción del puerto.
     * @param paquete El paquete que queremos que sea recivido en el puerto.
     * @since 1.0
     */    
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
     * Este método inserta un paquete en el buffer de recepción del puerto. Es igual
     * que el método ponerPaquete(), salvo que no genera eventos y lo hace
     * silenciosamente.
     * @param paquete El paquete que queremos que reciba el puerto.
     * @since 1.0
     */    
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
     * este método lee un paquete del buffer de recepción del puerto. El paquete leido
     * dependerá del algoritmo de gestión de los búfferes que implemente el puerto. Por
     * defecto, es un FIFO Droptail.
     * @return El paquete leído.
     * @since 1.0
     */    
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
     * Este método calcula si podemos conmutar el siguiente paquete del nodo, dado el
     * número de octetos que como mucho podemos conmutar en un momento dado.
     * @param octetos El número de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso contrario.
     * @since 1.0
     */    
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
     * Este método obtiene la congestión total el puerto, en porcentaje.
     * @return El porcentaje de ocupación del puerto.
     * @since 1.0
     */    
    public long obtenerCongestion() {
        if (this.bufferIlimitado) {
            return 0;
        } 
        TPuertosNodoNormal tpn = (TPuertosNodoNormal) cjtoPuertos;
        long cong = (tpn.obtenerTamanioOcupadoCjtoPuertos()*100) / (tpn.obtenerTamanioBuffer()*1024*1024);
        return cong;
    }

    /**
     * Este método comprueba si hay paquetes esperando en el buffer de recepción o no.
     * @return TRUE, si hay paquetes en el buffer de recepción. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean hayPaqueteEsperando() {
        if (buffer.size() > 0)
            return true;
        return false;
    }

    /**
     * Este método calcula el total de octetos que suman los paquetes que actualmente
     * hay en el buffer de recepción del puerto.
     * @return El tamaño en octetos del total de paquetes en el buffer de recepción.
     * @since 1.0
     */    
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
     * Este método calcula el número de paquetes total que hay en el buffer del puerto.
     * @return El número total de paquetes que hay en el puerto.
     * @since 1.0
     */    
    public int obtenerNumeroPaquetes() {
        return buffer.size();
    }

    /**
     * Este método reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 1.0
     */    
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
