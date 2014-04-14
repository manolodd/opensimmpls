package simMPLS.electronica.dmgp;

import simMPLS.utiles.*;
import simMPLS.protocolo.*;
import simMPLS.escenario.*;
import java.util.*;

/**
 * Esta clase implementa una memoria DMGP para el almacenamiento temporal de PDUs
 * con requisitos de GoS.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TDMGP {
    
    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de TDMGP e
     * inicia sus atributos.
     * @since 1.0
     */    
    public TDMGP() {
        cerrojo = new TMonitor();
        generaId = new TIdentificadorRotativo();
        flujos = new TreeSet();
        porcentajeTotalDisponible = 100;
        tamanioTotalDMGPKB = 1;
        octetosTotalesAsignados = 0;
    }
    
    /**
     * Este método establece el tamaño global de la memoria DMGP en kilobytes.
     * @since 1.0
     * @param tam Tamaño en kilobytes.
     */    
    public void ponerTamanioDMGPEnKB(int tam) {
        this.tamanioTotalDMGPKB = tam;
        this.reset();
    }
    
    /**
     * Este método obtiene el tamaño global de la memoria DMGP en kilobytes.
     * @return Tamaño en kilobytes.
     * @since 1.0
     */    
    public int obtenerTamanioDMGPEnKB() {
        return this.tamanioTotalDMGPKB;
    }

    /**
     * Este método busca un paquete marcado con GoS en lamemoria DMGP.
     * @param idf Identificador del flujo al que pertenece el paquete buscado.
     * @param idp Identificador del paquete buscado.
     * @return El paquete buscado, si está en la DMGP. NULL en caso contrario.
     * @since 1.0
     */    
    public TPDUMPLS buscarPaquete(int idf, int idp) {
        TPDUMPLS paqueteBuscado = null;
        TEntradaFlujoDMGP efdmgp = obtenerFlujo(idf);
        if (efdmgp != null) {
            this.cerrojo.bloquear();
            Iterator it = efdmgp.obtenerEntradas().iterator();
            TEntradaDMGP edmgp = null;
            while (it.hasNext()) {
                edmgp = (TEntradaDMGP) it.next();
                if (edmgp.obtenerIdPaquete() == idp) {
                    paqueteBuscado = edmgp.obtenerPaquete();
                    this.cerrojo.liberar();
                    return paqueteBuscado;
                }
            }
            this.cerrojo.liberar();
        }
        return null;
    }
    
    /**
     * Esdte método inserta un nuevo paquete marcado con GoS en la memoria DMGP.
     * @param paquete Paquete que se desea insertar en la DMGP.
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
     * Este método restaura el valor de todos los atributos a su momento inicial, como
     * si acabasen de ser creados por el constructor.
     * @since 1.0
     */    
    public void reset() {
        cerrojo = null;
        generaId = null;
        flujos = null;
        cerrojo = new TMonitor();
        generaId = new TIdentificadorRotativo();
        flujos = new TreeSet();
        porcentajeTotalDisponible = 100;
        octetosTotalesAsignados = 0;
    }
    
    private int obtenerTamanioDMGPEnOctetos() {
        return (this.tamanioTotalDMGPKB*1024);
    }

    private TEntradaFlujoDMGP obtenerFlujo(TPDU paquete) {
        TEntradaFlujoDMGP efdmgp = null;
        int idf = paquete.obtenerCabecera().obtenerIPOrigen().hashCode();
        efdmgp = obtenerFlujo(idf);
        return efdmgp;
    }

    private TEntradaFlujoDMGP obtenerFlujo(int idf) {
        TEntradaFlujoDMGP efdmgp = null;
        this.cerrojo.bloquear();
        Iterator ite = flujos.iterator();
        while (ite.hasNext()) {
            efdmgp = (TEntradaFlujoDMGP) ite.next();
            if (efdmgp.obtenerIdFlujo() == idf) {
                this.cerrojo.liberar();
                return efdmgp;
            }
        }
        this.cerrojo.liberar();
        return null;
    }

    private TEntradaFlujoDMGP crearFlujo(TPDU paquete) {
        this.cerrojo.bloquear();
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
                this.cerrojo.liberar();
                return efdmgp;
            }
        }
        this.cerrojo.liberar();
        return null;
    }
    
    private int obtenerOctetosQueSeAsignaran(TPDU paquete) {
        int pr = obtenerPorcentajeRequerido(paquete);
        int or = 0;
        if (this.porcentajeTotalDisponible > 0) {
            if (this.porcentajeTotalDisponible > pr) {
                or = ((this.obtenerTamanioDMGPEnOctetos()*pr)/100);
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
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL3_CONLSP)
            return 12;
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL3_SINLSP)
            return 12;
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL2_CONLSP)
            return 8;
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL2_SINLSP)
            return 8;
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL1_CONLSP)
            return 4;
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL1_SINLSP)
            return 4;
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL0_CONLSP)
            return 0;
        if (nivelGoSDelPaquete == TPDU.EXP_NIVEL0_SINLSP)
            return 0;
        return 1;
    }
    
    private TMonitor cerrojo;
    private TIdentificadorRotativo generaId;
    private TreeSet flujos;
    private int porcentajeTotalDisponible;
    private int tamanioTotalDMGPKB;
    private int octetosTotalesAsignados;
}
