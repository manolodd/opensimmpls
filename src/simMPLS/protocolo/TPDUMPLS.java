package simMPLS.protocolo;

import java.util.*;

/** Esta clase implementa un paquete MPLS, con todos sus campos diferenciados y
 * accesibles.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPDUMPLS extends TPDU {
    
    /** Este método es el constructor de la clase. Crea una instancia nueva de TPDUMPLS
     * de acuerdo a los parámetros especificados.
     * @param id Identificador único del paquete.
     * @param ipo Dirección IP origen del paquete.
     * @param ipd Dirección IP del destino del paquete MPLS.
     * @param tamDatos Tamaño de los datos TCP que incorpora el paquete MPLS en su interior, en bytes.
     * @since 1.0
     */
    public TPDUMPLS(long id, String ipo, String ipd, int tamDatos) {
        super(id, ipo, ipd);
        datosTCP = new TDatosTCP(tamDatos);
        pilaEtiquetas = new TPilaEtiquetasMPLS();
        subtipo = super.MPLS;
    }

    
    /**
     * Este método devuelve un paquete MPLS que es una réplica exacta a la instancia
     * actual.
     * @since 1.0
     * @return Una copia exacta de la instancia actual.
     */    
    public TPDUMPLS obtenerCopia() {
        long id = this.obtenerIdentificador();
        String IPo = new String(this.obtenerCabecera().obtenerIPOrigen());
        String IPd = new String(this.obtenerCabecera().obtenerIPDestino());
        int tamD = this.datosTCP.obtenerTamanio() - 20;
        TPDUMPLS clon = new TPDUMPLS(id, IPo, IPd, tamD);
        if (this.obtenerCabecera().obtenerCampoOpciones().estaUsado()) {
            int nivelGoS = this.obtenerCabecera().obtenerCampoOpciones().obtenerNivelGoS();
            clon.obtenerCabecera().obtenerCampoOpciones().ponerNivelGoS(nivelGoS);
            int idGoS = this.obtenerCabecera().obtenerCampoOpciones().obtenerIDPaqueteGoS();
            clon.obtenerCabecera().obtenerCampoOpciones().ponerIDPaqueteGoS(idGoS);
            if (this.obtenerCabecera().obtenerCampoOpciones().tieneMarcasDePaso()) {
                int numMarcas = this.obtenerCabecera().obtenerCampoOpciones().obtenerNumeroDeNodosActivosAtravesados();
		int i=0;
                String marcaActual = null;
		for (i=0; i< numMarcas; i++) {
                    marcaActual = this.obtenerCabecera().obtenerCampoOpciones().obtenerActivoNodoAtravesado(i);
                    if (marcaActual != null) {
                        clon.obtenerCabecera().obtenerCampoOpciones().ponerNodoAtravesado(new String(marcaActual));
                    }
                }
            }
        }
        TEtiquetaMPLS etiquetaActual = null;
        TEtiquetaMPLS etiquetaNuevaLocal = null;
        TEtiquetaMPLS etiquetaNuevaClon = null;
        LinkedList pilaEtiquetasLocal = new LinkedList();
        LinkedList pilaEtiquetasClon = new LinkedList();
        while (this.obtenerPilaEtiquetas().obtenerTamanio() > 0) {
            etiquetaActual = this.obtenerPilaEtiquetas().obtenerEtiqueta();
            this.obtenerPilaEtiquetas().borrarEtiqueta();
            int idEtiq = etiquetaActual.obtenerIdentificador();
            etiquetaNuevaLocal = new TEtiquetaMPLS(idEtiq);
            etiquetaNuevaLocal.ponerBoS(etiquetaActual.obtenerBoS());
            etiquetaNuevaLocal.ponerEXP(etiquetaActual.obtenerEXP());
            etiquetaNuevaLocal.ponerLabel(etiquetaActual.obtenerLabel());
            etiquetaNuevaLocal.ponerTTL(etiquetaActual.obtenerTTL());
            etiquetaNuevaClon = new TEtiquetaMPLS(idEtiq);
            etiquetaNuevaClon.ponerBoS(etiquetaActual.obtenerBoS());
            etiquetaNuevaClon.ponerEXP(etiquetaActual.obtenerEXP());
            etiquetaNuevaClon.ponerLabel(etiquetaActual.obtenerLabel());
            etiquetaNuevaClon.ponerTTL(etiquetaActual.obtenerTTL());
            if (pilaEtiquetasLocal.size() == 0) {
                pilaEtiquetasLocal.add(etiquetaNuevaLocal);
            } else {
                pilaEtiquetasLocal.addFirst(etiquetaNuevaLocal);
            }
            if (pilaEtiquetasClon.size() == 0) {
                pilaEtiquetasClon.add(etiquetaNuevaClon);
            } else {
                pilaEtiquetasClon.addFirst(etiquetaNuevaClon);
            }
        }
        while (pilaEtiquetasLocal.size() > 0) {
            this.obtenerPilaEtiquetas().ponerEtiqueta((TEtiquetaMPLS) pilaEtiquetasLocal.removeFirst());
        }
        while (pilaEtiquetasClon.size() > 0) {
            clon.obtenerPilaEtiquetas().ponerEtiqueta((TEtiquetaMPLS) pilaEtiquetasClon.removeFirst());
        }
        return clon;
    }
    
    /** Este método nos devuelve el tamaño completo del paquete MPLS, en bytes.
     * @return El tamaño completeo del paquete, en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        int tam = 0;
        tam += super.obtenerCabecera().obtenerTamanio();
        tam += this.datosTCP.obtenerTamanio();
        tam += (4 * this.pilaEtiquetas.obtenerTamanio());
        return (tam);
    }
    
    /** Este método nos devuelve la constante MPLS, indicando que el paquete es de este
     * tipo.
     * @return La constante MPLS.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.MPLS;
    }
    
    /** Este método nos permite acceder directamente a los datos TCP que se encuentran
     * en el interior del paquete y así usar sus métodos.
     * @return Los datos TCP que transporta este paquete MPLS.
     * @since 1.0
     */
    public TDatosTCP obtenerDatosTCP() {
        return datosTCP;
    }
    
    /** Este método nos permite cambiar la carga TCP de este paquete MPLS de una forma
     * rápida y sencilla.
     * @param d Los nuevos datos TCP que deseamos que tenga el paquete MPLS.
     * @since 1.0
     */
    public void ponerDatosTCP(TDatosTCP d) {
        datosTCP = d;
    }
    
    /** Este método nos permite el acceso directo a la pila de etiquetas MPLS, así
     * podremos usar sus métodos directamente.
     * @return La pila de etiquetas del paquete MPLS.
     * @since 1.0
     */
    public TPilaEtiquetasMPLS obtenerPilaEtiquetas() {
        return pilaEtiquetas;
    }
    
    /**
     * Este metodo permite establecer el subtipo de un paquete MPLS.
     * @param st El subtipo. Una constante de la clase TPDU que indica si se trata de un paquete
     * MPLS con garantía de servicio o no.
     * @since 1.0
     */
    public void ponerSubtipo(int st) {
        subtipo = st;
    }
    
    /**
     * Este método permite obtener el subtipo de un paquete MPLS, es decir, si el
     * paquete MPLS es o no un paquete con GoS.
     * @return El subtipo el paquete MPLS.
     * @since 1.0
     */
    public int obtenerSubTipo() {
        return subtipo;
    }
    
    /** Este atributo representa los datos TCP que están incorporados en el paquete.
     * @since 1.0
     */
    private TDatosTCP datosTCP;
    /** Este atributo privado representa la pila de etiquetas MPLS que  acompañana a la
     * cabecera de esta instancia del paquete MPLS.
     * @since 1.0
     */
    private TPilaEtiquetasMPLS pilaEtiquetas;
    /**
     * Este atributo almacenará el subtipo de paquete MPLS, que puede ser: Con o sin
     * GoS.
     * @since 1.0
     */
    private int subtipo;
}
