package simMPLS.electronica.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.protocolo.TPDUMPLS;
import simMPLS.utiles.TIdentificadorRotativo;
import simMPLS.utiles.TLock;

/**
 * This calss implements a flow entry for the DMGP memory.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TEntradaFlujoDMGP implements Comparable {

    /**
     * This method is the constructor. It creates a new TEntradaFlujoDMGP
     * instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param ordenLlegada Incoming order of the flow to the DMGP memory.
     * @since 1.0
     */
    public TEntradaFlujoDMGP(int ordenLlegada) {
        orden = ordenLlegada;
        idFlujo = -1;
        porcentajeAsignado = 0;
        octetosAsignados = 0;
        octetosOcupados = 0;
        entradas = new TreeSet();
        cerrojo = new TLock();
        generadorId = new TIdentificadorRotativo();
    }

    /**
     * This method establishes the flow identifier associated to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param idf The flow identifier.
     * @since 1.0
     */
    public void ponerIdFlujo(int idf) {
        this.idFlujo = idf;
    }

    /**
     * This method returns the identifier of the flow assigned to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The flow identifier.
     * @since 1.0
     */
    public int obtenerIdFlujo() {
        return this.idFlujo;
    }

    /**
     * This method establishes the percentage of DMGP assigned to this flow.
     *
     * @param pa Percentage of DMGP assigned to this flow.
     * @since 1.0
     */
    public void ponerPorcentajeAsignado(int pa) {
        this.porcentajeAsignado = pa;
    }

    /**
     * This method obtains the percentage of DMGP assigned to this flow..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Percentage of DMGP assigned to this flow.
     * @since 1.0
     */
    public int obtenerPorcentajeAsignado() {
        return this.porcentajeAsignado;
    }

    /**
     * This method establishes the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param oa Number of DMGP octects assigned to this flow.
     * @since 1.0
     */
    public void ponerOctetosAsignados(int oa) {
        this.octetosAsignados = oa;
    }

    /**
     * This method obtains the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of DMGP octects assigned to this flow.
     * @since 1.0
     */
    public int obtenerOctetosAsignados() {
        return this.octetosAsignados;
    }

    /**
     * This method establishes the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param oo Number of DMGP octects currently used by the flow.
     * @since 1.0
     */
    public void ponerOctetosOcupados(int oo) {
        this.octetosOcupados = oo;
    }

    /**
     * This method obtains the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Number of DMGP octects currently used by the flow.
     * @since 1.0
     */
    public int obtenerOctetosOcupados() {
        return this.octetosOcupados;
    }

    /**
     * This method obtains the tree that contains all the packets of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The tree containing all the packets of this flow.
     * @since 1.0
     */
    public TreeSet obtenerEntradas() {
        return this.entradas;
    }

    /**
     * This method contains the order of incoming to the DMGP.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The incoming order.
     * @since 1.0
     */
    public int obtenerOrden() {
        return this.orden;
    }

    /**
     * This method returns the monitor of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     * @return The monitor of this flow.
     */
    public TLock obtenerCerrojo() {
        return this.cerrojo;
    }

    private void liberarEspacio(int octetosALiberar) {
        int octetosLiberados = 0;
        Iterator it = entradas.iterator();
        TEntradaDMGP edmgp = null;
        while ((it.hasNext()) && (octetosLiberados < octetosALiberar)) {
            edmgp = (TEntradaDMGP) it.next();
            octetosLiberados += edmgp.obtenerPaquete().obtenerTamanio();
            it.remove();
        }
        this.octetosOcupados -= octetosLiberados;
    }

    /**
     * This method inserts a packet that belongs to this flow, in the tree of
     * packets. If there is available space, the packet is inserted. Otherwise
     * packets are reselased untill there are space. If after this release there
     * are no enough space, the packet is not inserted.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param paquete Packet of this flow to be inserted in the DMGP.
     * @since 1.0
     */
    public void insertarPaquete(TPDUMPLS paquete) {
        this.cerrojo.bloquear();
        int octetosDisponibles = this.octetosAsignados - this.octetosOcupados;
        if (octetosDisponibles >= paquete.obtenerTamanio()) {
            TEntradaDMGP edmgp = new TEntradaDMGP(generadorId.obtenerNuevo());
            edmgp.ponerPaquete(paquete);
            this.octetosOcupados += paquete.obtenerTamanio();
            this.entradas.add(edmgp);
        } else {
            if (octetosOcupados >= paquete.obtenerTamanio()) {
                liberarEspacio(paquete.obtenerTamanio());
                TEntradaDMGP edmgp = new TEntradaDMGP(generadorId.obtenerNuevo());
                edmgp.ponerPaquete(paquete);
                this.octetosOcupados += paquete.obtenerTamanio();
                this.entradas.add(edmgp);
            } else {
                paquete = null;
            }
        }
        this.cerrojo.liberar();
    }

    /**
     * This method compares this flow entry with another of the same type.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param o The entry to be compared with.
     * @return -1, 0, 1, depending on whether the current instance is lesser,
     * equal or greater than the instance passed as an argument. In terms of
     * shorting.
     * @since 1.0
     */
    @Override
    public int compareTo(Object o) {
        TEntradaFlujoDMGP edmgp = (TEntradaFlujoDMGP) o;
        if (this.orden < edmgp.obtenerOrden()) {
            return TEntradaFlujoDMGP.ESTE_MENOR;
        }
        if (this.orden > edmgp.obtenerOrden()) {
            return TEntradaFlujoDMGP.ESTE_MAYOR;
        }
        return TEntradaFlujoDMGP.ESTE_IGUAL;
    }

    private static final int ESTE_MENOR = -1;
    private static final int ESTE_IGUAL = 0;
    private static final int ESTE_MAYOR = 1;

    private int orden;
    private int idFlujo;
    private int porcentajeAsignado;
    private int octetosAsignados;
    private int octetosOcupados;
    private TreeSet entradas;
    private TLock cerrojo;
    private TIdentificadorRotativo generadorId;
}
