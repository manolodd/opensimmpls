package simMPLS.electronica.dmgp;

import simMPLS.protocolo.TPDUMPLS;


/**
 * This class implements an entry of the DMGP memory. It stores a GoS packet and
 * all neccesary data to be forwarded.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.0
 */
public class TEntradaDMGP implements Comparable {

    /**
     * This method is the constructor. It creates a new instance of TEntradaDMGP
     * and initialize its attributes.
     *
     * @param ordenLlegada Establish the incoming order in the global DMGP 
     * memory.
     */
    public TEntradaDMGP(int ordenLlegada) {
        idFlujo = -1;
        idPaquete = -1;
        paquete = null;
        orden = ordenLlegada;
    }

    /**
     * This method obtains the identifier of the flow associated to this entry.
     *
     * @return The identifier of the flow associated to this entry.
     * @since 1.0
     */
    public int obtenerIdFlujo() {
        return this.idFlujo;
    }

    /**
     * This method obtains the identifier of the GoS packet stored in this 
     * entry.
     *
     * @return The identifier of the GoS packet.
     * @since 1.0
     */
    public int obtenerIdPaquete() {
        return this.idPaquete;
    }

    /**
     * This method obtains the GoS packet that is stored in this entry of the 
     * DMGP memory.
     *
     * @return The stored GoS packet.
     * @since 1.0
     */
    public TPDUMPLS obtenerPaquete() {
        return this.paquete.obtenerCopia();
    }

    /**
     * This method insert the GoS packet in this entry of the DMGP memory.
     *
     * @param p The packet to be inserted in this entry.
     * @since 1.0
     */
    public void ponerPaquete(TPDUMPLS p) {
        this.paquete = p.obtenerCopia();
        this.idFlujo = p.obtenerCabecera().obtenerIPOrigen().hashCode();
        this.idPaquete = p.obtenerCabecera().obtenerClavePrimaria();
    }

    /**
     * This method allow estabishing the order number in the complete DMGP 
     * memory.
     *
     * @return El n�mero de orden de la entrada en la DMGP.
     * @since 1.0
     */
    public int obtenerOrden() {
        return this.orden;
    }

    /**
     * This method compares this DMGP entry with another to establish the 
     * oder.
     *
     * @param o Entrada de la DMGP con la que se va a comparar la actual.
     * @return -1, 0 � 1, dependiendo de si la entrada actual es menor, igual o
     * mayor que la pasada por par�metros. Hablando en t�rminos de ordenaci�n.
     * @since 1.0
     */
    @Override
    public int compareTo(Object o) {
        TEntradaDMGP edmgp = (TEntradaDMGP) o;
        if (this.orden < edmgp.obtenerOrden()) {
            return TEntradaDMGP.ESTE_MENOR;
        }
        if (this.orden > edmgp.obtenerOrden()) {
            return TEntradaDMGP.ESTE_MAYOR;
        }
        return TEntradaDMGP.ESTE_IGUAL;
    }

    private static final int ESTE_MENOR = -1;
    private static final int ESTE_IGUAL = 0;
    private static final int ESTE_MAYOR = 1;

    private int idFlujo;
    private int idPaquete;
    private int orden;
    private TPDUMPLS paquete;
}
