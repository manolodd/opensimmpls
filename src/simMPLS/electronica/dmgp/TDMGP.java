package simMPLS.electronica.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.protocolo.TPDU;
import simMPLS.protocolo.TPDUMPLS;
import simMPLS.utiles.TIdentificadorRotativo;
import simMPLS.utiles.TLock;



/**
 * This class implements a DMGP memory to save GoS-aware PDUs temporarily.
 *
 * @author Manuel Domínguez Dorado</B - ingeniero@ManoloDominguez.com @version
 * 1.0
 */
public class TDMGP {

    /**
     * This method is the class constructor. It creates a new instance of TDMGP
     * and initialize its attributes.
     *
     * @since 1.0
     */
    public TDMGP() {
        monitor = new TLock();
        generaId = new TIdentificadorRotativo();
        flujos = new TreeSet();
        porcentajeTotalDisponible = 100;
        tamanioTotalDMGPKB = 1;
        octetosTotalesAsignados = 0;
    }

    /**
     * This method establish the global size of DMGP in kilobytes.
     *
     * @since 1.0
     * @param tam Size in kilobytes.
     */
    public void ponerTamanioDMGPEnKB(int tam) {
        this.tamanioTotalDMGPKB = tam;
        this.reset();
    }

    /**
     * This method obtains the globals soze of DMGP in kilobites.
     *
     * @return Size in kilobites.
     * @since 1.0
     */
    public int obtenerTamanioDMGPEnKB() {
        return this.tamanioTotalDMGPKB;
    }

    /**
     * This method look for a packet tagged as GoS within the DMGP memory.
     *
     * @param idf Identifier of the flow the packet belongs to.
     * @param idp Identifier of the packet.
     * @return The packet, if in the DMGP. NULL on the contrary.
     * @since 1.0
     */
    public TPDUMPLS buscarPaquete(int idf, int idp) {
        TPDUMPLS paqueteBuscado = null;
        TEntradaFlujoDMGP efdmgp = obtenerFlujo(idf);
        if (efdmgp != null) {
            this.monitor.bloquear();
            Iterator it = efdmgp.obtenerEntradas().iterator();
            TEntradaDMGP edmgp = null;
            while (it.hasNext()) {
                edmgp = (TEntradaDMGP) it.next();
                if (edmgp.obtenerIdPaquete() == idp) {
                    paqueteBuscado = edmgp.obtenerPaquete();
                    this.monitor.liberar();
                    return paqueteBuscado;
                }
            }
            this.monitor.liberar();
        }
        return null;
    }

    /**
     * This method insert a new GoS packet into the DMGP memory.
     *
     * @param paquete The packet to be inserted into the DMGP memory.
     * @since 1.0
     */
    public void insertarPaquete(TPDUMPLS paquete) {
        TEntradaFlujoDMGP efdmgp = this.obtenerFlujo(paquete);
        if (efdmgp == null) {
            efdmgp = this.crearFlujo(paquete);
        }
        if (efdmgp != null) {
            efdmgp.insertarPaquete(paquete);
        } else {
            paquete = null;
        }
    }

    /**
     * This method restores the value of all attributes as when created by the
     * constructor.
     *
     * @since 1.0
     */
    public void reset() {
        monitor = null;
        generaId = null;
        flujos = null;
        monitor = new TLock();
        generaId = new TIdentificadorRotativo();
        flujos = new TreeSet();
        porcentajeTotalDisponible = 100;
        octetosTotalesAsignados = 0;
    }

    private int obtenerTamanioDMGPEnOctetos() {
        return (this.tamanioTotalDMGPKB * 1024);
    }

    private TEntradaFlujoDMGP obtenerFlujo(TPDU paquete) {
        TEntradaFlujoDMGP efdmgp = null;
        int idf = paquete.obtenerCabecera().obtenerIPOrigen().hashCode();
        efdmgp = obtenerFlujo(idf);
        return efdmgp;
    }

    private TEntradaFlujoDMGP obtenerFlujo(int idf) {
        TEntradaFlujoDMGP efdmgp = null;
        this.monitor.bloquear();
        Iterator ite = flujos.iterator();
        while (ite.hasNext()) {
            efdmgp = (TEntradaFlujoDMGP) ite.next();
            if (efdmgp.obtenerIdFlujo() == idf) {
                this.monitor.liberar();
                return efdmgp;
            }
        }
        this.monitor.liberar();
        return null;
    }

    private TEntradaFlujoDMGP crearFlujo(TPDU paquete) {
        this.monitor.bloquear();
        TEntradaFlujoDMGP efdmgp = null;
        int idf = paquete.obtenerCabecera().obtenerIPOrigen().hashCode();
        int pqsa = 0;
        int oqsa = 0;
        if (this.octetosTotalesAsignados < this.obtenerTamanioDMGPEnOctetos()) {
            pqsa = this.obtenerPorcentajeQueSeAsignara(paquete);
            oqsa = this.obtenerOctetosQueSeAsignaran(paquete);
            if (oqsa > 0) {
                this.octetosTotalesAsignados += oqsa;
                this.porcentajeTotalDisponible -= pqsa;
                efdmgp = new TEntradaFlujoDMGP(this.generaId.obtenerNuevo());
                efdmgp.ponerIdFlujo(idf);
                efdmgp.ponerPorcentajeAsignado(pqsa);
                efdmgp.ponerOctetosAsignados(oqsa);
                flujos.add(efdmgp);
                this.monitor.liberar();
                return efdmgp;
            }
        }
        this.monitor.liberar();
        return null;
    }

    private int obtenerOctetosQueSeAsignaran(TPDU paquete) {
        int pr = obtenerPorcentajeRequerido(paquete);
        int or = 0;
        if (this.porcentajeTotalDisponible > 0) {
            if (this.porcentajeTotalDisponible > pr) {
                or = ((this.obtenerTamanioDMGPEnOctetos() * pr) / 100);
                return or;
            } else {
                or = this.obtenerTamanioDMGPEnOctetos() - this.octetosTotalesAsignados;
                return or;
            }
        }
        return 0;
    }

    private int obtenerPorcentajeQueSeAsignara(TPDU paquete) {
        int pr = obtenerPorcentajeRequerido(paquete);
        if (this.porcentajeTotalDisponible > 0) {
            if (this.porcentajeTotalDisponible > pr) {
                return pr;
            } else {
                return this.porcentajeTotalDisponible;
            }
        }
        return 0;
    }

    private int obtenerPorcentajeRequerido(TPDU paquete) {
        int nivelGoSDelPaquete = 0;
        if (paquete.obtenerCabecera().obtenerCampoOpciones().estaUsado()) {
            nivelGoSDelPaquete = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerNivelGoS();
        } else {
            return 0;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL3_CONLSP) {
            return 12;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL3_SINLSP) {
            return 12;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL2_CONLSP) {
            return 8;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL2_SINLSP) {
            return 8;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL1_CONLSP) {
            return 4;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL1_SINLSP) {
            return 4;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL0_CONLSP) {
            return 0;
        }
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL0_SINLSP) {
            return 0;
        }
        return 1;
    }

    private TLock monitor;
    private TIdentificadorRotativo generaId;
    private TreeSet flujos;
    private int porcentajeTotalDisponible;
    private int tamanioTotalDMGPKB;
    private int octetosTotalesAsignados;
}
