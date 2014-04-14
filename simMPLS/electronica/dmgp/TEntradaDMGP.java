package simMPLS.electronica.dmgp;

import simMPLS.protocolo.*;

/**
 * Esta clase implementa una entrada de la memoria DMGP. Almacenará un paquete
 * marcado con GoS y los datos necesarios para ser retransmitido.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEntradaDMGP implements Comparable {
    
    /**
     * Este método es el constructor. Crea una nueva instancia de TEntradaDMGP e inicia
     * sus atributos.
     * @param ordenLlegada Establece el orden de llegada de la entrada en la DMGP global.
     */    
    public TEntradaDMGP(int ordenLlegada) {
        idFlujo = -1;
        idPaquete = -1;
        paquete = null;
        orden = ordenLlegada;
    }

    /**
     * Este método obtiene el identificador del flujo al que está asociada esta
     * entrada.
     * @return El identificador del flujo al que está asociada esta entrada.
     * @since 1.0
     */    
    public int obtenerIdFlujo() {
        return this.idFlujo;
    }

    /**
     * Este método obtiene el identificador del paquete marcado con GoS que se
     * encuentra almacenado en esta entrada.
     * @return El identificador del paquete.
     * @since 1.0
     */    
    public int obtenerIdPaquete() {
        return this.idPaquete;
    }
    
    /**
     * Este método obtiene el paquete marcado con GoS que se encuentra almacenado en la
     * entrada.
     * @return El paquete almacenado.
     * @since 1.0
     */    
    public TPDUMPLS obtenerPaquete() {
        return this.paquete.obtenerCopia();
    }
    
    /**
     * Este método inserta un paquete marcado con GoS en la entrada.
     * @param p Paquete que se deea insertar en la entrada.
     * @since 1.0
     */    
    public void ponerPaquete(TPDUMPLS p) {
        this.paquete = p.obtenerCopia();
        this.idFlujo = p.obtenerCabecera().obtenerIPOrigen().hashCode();
        this.idPaquete = p.obtenerCabecera().obtenerClavePrimaria();
    }
    
    /**
     * Este método permite establecer el número de orden de la entrada en la memoria
     * DMGP completa.
     * @return El número de orden de la entrada en la DMGP.
     * @since 1.0
     */    
    public int obtenerOrden() {
        return this.orden;
    }
    
    /**
     * Este método ompara esta entrada de la DMGP con otra, para establecer el orden de
     * ordenación.
     * @param o Entrada de la DMGP con la que se va a comparar la actual.
     * @return -1, 0 ó 1, dependiendo de si la entrada actual es menor, igual o mayor que la
     * pasada por parámetros. Hablando en términos de ordenación.
     * @since 1.0
     */    
    public int compareTo(Object o) {
        TEntradaDMGP edmgp = (TEntradaDMGP) o;
        if (this.orden < edmgp.obtenerOrden())
            return this.ESTE_MENOR;
        if (this.orden > edmgp.obtenerOrden())
            return this.ESTE_MAYOR;
        return this.ESTE_IGUAL;
    }
    
    private static final int ESTE_MENOR = -1;
    private static final int ESTE_IGUAL = 0;
    private static final int ESTE_MAYOR = 1;
    
    private int idFlujo;
    private int idPaquete;
    private int orden;
    private TPDUMPLS paquete;
}
