package simMPLS.electronica.dmgp;

import simMPLS.utiles.*;
import simMPLS.protocolo.*;
import java.util.*;

/**
 * Esta clase implementa una entrada de flujo para memoria DMGP.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEntradaFlujoDMGP implements Comparable {
    
    /**
     * Este método es el constructor. Crea una nueva instancia de TEntradaFlujoDMGP.
     * @param ordenLlegada Oden de llegada del flujo a la DMGP.
     * @since 1.0
     */    
    public TEntradaFlujoDMGP(int ordenLlegada) {
        orden = ordenLlegada;
        idFlujo = -1;
        porcentajeAsignado = 0;
        octetosAsignados = 0;
        octetosOcupados = 0;
        entradas = new TreeSet();
        cerrojo = new TMonitor();
        generadorId = new TIdentificadorRotativo();
    }
    
    /**
     * este método establece el identificador del flujo al que representa esta entrada.
     * @param idf Identificador del flujo.
     * @since 1.0
     */    
    public void ponerIdFlujo(int idf) {
        this.idFlujo = idf;
    }
    
    /**
     * Este método devuelve el identificador del flujo al que pertenece esta entrada.
     * @return El identificador del flujo.
     * @since 1.0
     */    
    public int obtenerIdFlujo() {
        return this.idFlujo;
    }
    
    /**
     * Este método establece el porcentaje de DMGP asignado a este flujo.
     * @param pa Porcentaje de DMGP asignado a este flujo.
     * @since 1.0
     */    
    public void ponerPorcentajeAsignado(int pa) {
        this.porcentajeAsignado = pa;
    }
    
    /**
     * Este método obtiene el porcentaje de DMGP asignado a este flujo.
     * @return El porcentaje de DMGP asignado a este flujo.
     * @since 1.0
     */    
    public int obtenerPorcentajeAsignado() {
        return this.porcentajeAsignado;
    }
    
    /**
     * Este método establece el número de octetos de la DMGP que se han asignado a este
     * flujo.
     * @param oa Número de octetos de DMGP asignados al flujo.
     * @since 1.0
     */    
    public void ponerOctetosAsignados(int oa) {
        this.octetosAsignados = oa;
    }
    
    /**
     * Este método obtiene el número de octetos de DMGP que se han asignado a este
     * flujo.
     * @return El número de octetos de DMGO asignados a este flujo.
     * @since 1.0
     */    
    public int obtenerOctetosAsignados() {
        return this.octetosAsignados;
    }
    
    /**
     * Este método establece el número de octetos de DMGP ocupados actualmente por el flujo.
     * @param oo Número de octetos de DMGP actualmente ocupador por el flujo.
     * @since 1.0
     */    
    public void ponerOctetosOcupados(int oo) {
        this.octetosOcupados = oo;
    }
    
    /**
     * Este método obtiene el número de octetos de DMGP que actualmente están ocupados
     * por el flujo.
     * @return Número de octetos de DMGP actualmente ocupados por el flujo.
     * @since 1.0
     */    
    public int obtenerOctetosOcupados() {
        return this.octetosOcupados;
    }
    
    /**
     * Este método obtiene el árbol que contiene los paquetes de este flujo.
     * @return Árbol que contiene los paquetes de este flujo.
     * @since 1.0
     */    
    public TreeSet obtenerEntradas() {
        return this.entradas;
    }
    
    /**
     * Este método obtiene el orden de llegada de esta entrada a la DMGP.
     * @return Orden de llegada.
     * @since 1.0
     */    
    public int obtenerOrden() {
        return this.orden;
    }
    
    /**
     * Este método devuelve el cerrojo del flujo.
     * @since 1.0
     * @return El cerrojo del flujo.
     */    
    public TMonitor obtenerCerrojo() {
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
     * Este método inserta un paquete perteneciente a este flujo, en la lista de
     * paquetes. Si hay sitio, se inserta el paquete, si no hay, se liberan paquetes
     * hasta que cabe; si aun así no cabe, no se inserta.
     * @param paquete Paquete que se desea insertar en la DMGP para este flujo.
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
     * Compara esta entrada de flujo con otra del mismo tipo.
     * @param o La entrada con la que se va a comparar.
     * @return -1, 0 ó 1, dependiendo de si la instancia actual es menor, igual o mayor que la
     * especificada por parámetros. Hablando siempre en términos de ordenación.
     * @since 1.0
     */    
    public int compareTo(Object o) {
        TEntradaFlujoDMGP edmgp = (TEntradaFlujoDMGP) o;
        if (this.orden < edmgp.obtenerOrden())
            return this.ESTE_MENOR;
        if (this.orden > edmgp.obtenerOrden())
            return this.ESTE_MAYOR;
        return this.ESTE_IGUAL;
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
    private TMonitor cerrojo;
    private TIdentificadorRotativo generadorId;
}
