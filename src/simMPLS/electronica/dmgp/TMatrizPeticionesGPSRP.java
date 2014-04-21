package simMPLS.electronica.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.protocolo.TPDUMPLS;
import simMPLS.utiles.TIdentificadorRotativo;
import simMPLS.utiles.TLock;

/**
 * This class implements a table where received requests for retrnasmission will
 * be stored while they wait to be managed.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TMatrizPeticionesGPSRP {

    /**
     * This is the class constructor. It creates a new instance of
     * TMatrizPeticionesGPSRP.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TMatrizPeticionesGPSRP() {
        entradas = new TreeSet();
        generaId = new TIdentificadorRotativo();
        cerrojo = new TLock();
    }

    /**
     * This method reset all attributes od the class to its original values, as
     * when created by the constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void reset() {
        entradas = null;
        generaId = null;
        cerrojo = null;
        entradas = new TreeSet();
        generaId = new TIdentificadorRotativo();
        cerrojo = new TLock();
    }

    /**
     * This method update the outgoing port of for all the matching entries. It
     * will be changed for a new outgoing port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param pAnterior The port that is goint to be replaced.
     * @param pNuevo The new outgoing port for matching entries.
     * @since 1.0
     */
    public void actualizarPuertoSalida(int pAnterior, int pNuevo) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerPuertoSalida() == pAnterior) {
                ep.ponerPuertoSalida(pNuevo);
            }
        }
        this.cerrojo.liberar();
    }

    /**
     * This method removes from the table all entries that have the outgoing
     * port equal than the one specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param pAnterior Port that must match the outgoing port of entries to be
     * removed.
     * @since 1.0
     */
    public void eliminarEntradasConPuertoSalida(int pAnterior) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerPuertoSalida() == pAnterior) {
                ite.remove();
            }
        }
        this.cerrojo.liberar();
    }

    /**
     * This method insert a new entry in the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param paquete Packet for wich the retransmission is going to be
     * requested.
     * @param pEntrada Incoming port of the packet. It will be the outgoing port
     * for the retransmission request.
     * @return The new created an inserted entry. Otherwise, NULL.
     * @since 1.0
     */
    public TEntradaPeticionesGPSRP insertarEntrada(TPDUMPLS paquete, int pEntrada) {
        this.cerrojo.bloquear();
        TEntradaPeticionesGPSRP ep = new TEntradaPeticionesGPSRP(this.generaId.obtenerNuevo());
        ep.ponerPuertoSalida(pEntrada);
        ep.ponerIdFlujo(paquete.obtenerCabecera().obtenerIPOrigen().hashCode());
        ep.ponerIdPaquete(paquete.obtenerCabecera().obtenerClavePrimaria());
        int numIPs = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerNumeroDeNodosActivosAtravesados();
        int i = 0;
        String siguienteIP = "";
        for (i = 0; i < numIPs; i++) {
            siguienteIP = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerActivoNodoAtravesado(i);
            if (siguienteIP != null) {
                ep.ponerIPNodoAtravesado(siguienteIP);
            }
        }
        entradas.add(ep);
        this.cerrojo.liberar();
        return ep;
    }

    /**
     * This method removes a entry from the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param idf Flow the entry refers to.
     * @param idp Packet the table refers to.
     * @since 1.0
     */
    public void eliminarEntrada(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    ite.remove();
                }
            }
        }
        this.cerrojo.liberar();
    }

    /**
     * This method obtains a specific entry from the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param idf Flow identifier the entry refers to.
     * @param idp Packet identifier the entry refers to.
     * @return Entry matching the specified arguments. Otherwise, NULL.
     * @since 1.0
     */
    public TEntradaPeticionesGPSRP obtenerEntrada(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    this.cerrojo.liberar();
                    return ep;
                }
            }
        }
        this.cerrojo.liberar();
        return null;
    }

    /**
     * This method updates the table. It removes all entries for which no
     * retransmission attemps are available and their timeouts have expired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void actualizarEntradas() {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.debePurgarse()) {
                ite.remove();
            } else {
                ep.restaurarTimeOut();
            }
        }
        this.cerrojo.liberar();
    }

    /**
     * This method drecreases the timeout for all entries of the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param d Number of nanoseconds to be decreased from all entries timeouts.
     * @since 1.0
     */
    public void decrementarTimeOut(int d) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            ep.decrementarTimeOut(d);
        }
        this.cerrojo.liberar();
    }

    /**
     * This method obtains the outgoing port of a specific entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param idf Flow identifier the entry refers to.
     * @param idp Packet identifier the entry refers to.
     * @return Outgoing port of the entry maching the specified arguments.
     * @since 1.0
     */
    public int obtenerPuertoSalida(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    this.cerrojo.liberar();
                    return ep.obtenerPuertoSalida();
                }
            }
        }
        this.cerrojo.liberar();
        return -1;
    }

    /**
     * Thism method obtains the IP address of the following node that should be
     * requested for a packet retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param idf Flow identifier of the desired entry.
     * @param idp Packet identifier of the desired entry.
     * @return IP address of the following node to be requested for a packet
     * retransmission. Otherwise, NULL.
     * @since 1.0
     */
    public String obtenerIPNodoActivo(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    this.cerrojo.liberar();
                    return ep.obtenerIPNodoAtravesado();
                }
            }
        }
        this.cerrojo.liberar();
        return null;
    }

    /**
     * This method obtains the interator of all entries of the table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Iterator of all entries in the table.
     * @since 1.0
     */
    public Iterator obtenerIterador() {
        return entradas.iterator();
    }

    /**
     * This method allow accesing the sync monitor of this table.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Sync monitor of the table.
     * @since 1.0
     */
    public TLock obtenerCerrojo() {
        return this.cerrojo;
    }

    /**
     * Timer used to know when a retransmission request should be retried.
     *
     * @since 1.0
     */
    public static final int TIMEOUT = 50000;
    /**
     * Number of times tha the rentransmission request should be retried.
     *
     * @since 1.0
     */
    public static final int INTENTOS = 8;

    private TreeSet entradas;
    private TIdentificadorRotativo generaId;
    private TLock cerrojo;
}
