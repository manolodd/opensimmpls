package simMPLS.hardware.dmgp;

import java.util.LinkedList;

/**
 * This class implements an entry that will store data related to a
 * retransmission requested by a node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TGPSRPRequestsEntry implements Comparable {

    /**
     * This is the class constructor. Implements a new instance of
     * TEntradaPeticionesGPSRP.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param oll Incoming order. This is a number that must be used to make
     * this entry shorted in a collection in a coherent way.
     * @since 1.0
     */
    public TGPSRPRequestsEntry(int oll) {
        timeout = TGPSRPRequestsMatrix.TIMEOUT;
        intentos = TGPSRPRequestsMatrix.INTENTOS;
        idFlujo = -1;
        idPaquete = -1;
        pSalida = -1;
        nodosAtravesados = new LinkedList();
        orden = oll;
    }

    /**
     * This method obtains the incoming order to the entry in order to be
     * shorted in a collection.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Incoming order to the entry.
     * @since 1.0
     */
    public int obtenerOrden() {
        return orden;
    }

    /**
     * This method establishes the flow the entry belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param idf The flow the entry belongs to.
     * @since 1.0
     */
    public void ponerIdFlujo(int idf) {
        this.idFlujo = idf;
    }

    /**
     * This method obtains the flow the entry belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The flow the entry belongs to.
     * @since 1.0
     */
    public int obtenerIdFlujo() {
        return this.idFlujo;
    }

    /**
     * This method establishes the identifier of the packet this entry refers
     * to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param idp The packet identifier.
     * @since 1.0
     */
    public void ponerIdPaquete(int idp) {
        this.idPaquete = idp;
    }

    /**
     * This method obtains the identifier of the packet this entry refers to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The packet identifier.
     * @since 1.0
     */
    public int obtenerIdPaquete() {
        return this.idPaquete;
    }

    /**
     * This method establishes the outgoing port by where the retransmission
     * request has been sent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param ps Outgoing port.
     * @since 1.0
     */
    public void ponerPuertoSalida(int ps) {
        this.pSalida = ps;
    }

    /**
     * This method obtains the outgoing port by where the retransmission request
     * has been sent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Outgoing port.
     * @since 1.0
     */
    public int obtenerPuertoSalida() {
        return this.pSalida;
    }

    /**
     * This method establishes the IP address of an active node that will be
     * requested for a packet retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param na IP address of the node to be requested for a packet
     * retransmission.
     * @since 1.0
     */
    public void ponerIPNodoAtravesado(String na) {
        this.nodosAtravesados.addFirst(na);
    }

    /**
     * This method obtains the IP address of the next active node that will be
     * requested for a packet retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return IP address of the next active node to be requested for a packet
     * retransmission. If there is not a node to be requested, this method
     * return NULL.
     * @since 1.0
     */
    public String obtenerIPNodoAtravesado() {
        if (this.nodosAtravesados.size() > 0) {
            return ((String) this.nodosAtravesados.removeFirst());
        }
        return null;
    }

    /**
     * This method decreases the retransmission TimeOut.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param d Number of nanoseconds to decrease from the timeout.
     * @since 1.0
     */
    public void decrementarTimeOut(int d) {
        this.timeout -= d;
        if (this.timeout < 0) {
            this.timeout = 0;
        }
    }

    /**
     * This method restores the retransmission TimeOut to its original value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void restaurarTimeOut() {
        if (this.timeout == 0) {
            if (this.intentos > 0) {
                this.timeout = TGPSRPRequestsMatrix.TIMEOUT;
                this.intentos--;
            }
        }
    }

    /**
     * This method forces the TimeOut restoration to its original value and also
     * increases the number of expired retransmission attempts.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void restaurarTimeOutForzosamente() {
        this.timeout = TGPSRPRequestsMatrix.TIMEOUT;
        this.intentos--;
        if (this.intentos < 0) {
            intentos = 0;
            timeout = 0;
        }
    }

    /**
     * This method ckeck whether the retransmission request must be retried
     * again or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the retransmission must be retried. Otherwise, FALSE.
     * @since 1.0
     */
    public boolean debeReintentarse() {
        if (this.intentos > 0) {
            if (this.timeout == 0) {
                if (this.nodosAtravesados.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method check whether the entry must be removed from the table
     * (because retransmission is not going to be retried) or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the entry must be removed. Otherwise, FALSE.
     * @since 1.0
     */
    public boolean debePurgarse() {
        if (this.nodosAtravesados.size() == 0) {
            return true;
        }
        if (this.intentos == 0) {
            if (this.timeout == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method compares the current instance with another of the same type
     * passed as an argument to know the order to be inserted in a collection.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param o Instancia con la que se va a comparar la actual.
     * @return -1, 0, 1, depending on wheter the curren instance is lower,
     * equal, or greater than the one passed as an argument. In terms of
     * shorting.
     * @since 1.0
     */
    @Override
    public int compareTo(Object o) {
        TGPSRPRequestsEntry e = (TGPSRPRequestsEntry) o;
        if (this.orden < e.obtenerOrden()) {
            return TGPSRPRequestsEntry.ESTE_MENOR;
        }
        if (this.orden > e.obtenerOrden()) {
            return TGPSRPRequestsEntry.ESTE_MAYOR;
        }
        return TGPSRPRequestsEntry.ESTE_IGUAL;
    }

    private static final int ESTE_MENOR = -1;
    private static final int ESTE_IGUAL = 0;
    private static final int ESTE_MAYOR = 1;

    private int timeout;
    private int idFlujo;
    private int idPaquete;
    private int pSalida;
    private LinkedList nodosAtravesados;
    private int orden;
    private int intentos;
}
