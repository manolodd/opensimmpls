package simMPLS.protocols;

import java.util.*;

/** Esta clase implementa un paquete MPLS, con todos sus campos diferenciados y
 * accesibles.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPDUMPLS extends TPDU {
    
    /** Este m�todo es el constructor de la clase. Crea una instancia nueva de TPDUMPLS
     * de acuerdo a los par�metros especificados.
     * @param id Identificador �nico del paquete.
     * @param ipo Direcci�n IP origen del paquete.
     * @param ipd Direcci�n IP del destino del paquete MPLS.
     * @param tamDatos Tama�o de los datos TCP que incorpora el paquete MPLS en su interior, en bytes.
     * @since 1.0
     */
    public TPDUMPLS(long id, String ipo, String ipd, int tamDatos) {
        super(id, ipo, ipd);
        datosTCP = new TDatosTCP(tamDatos);
        pilaEtiquetas = new TPilaEtiquetasMPLS();
        subtipo = super.MPLS;
    }

    
    /**
     * Este m�todo devuelve un paquete MPLS que es una r�plica exacta a la instancia
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
    
    /** Este m�todo nos devuelve el tama�o completo del paquete MPLS, en bytes.
     * @return El tama�o completeo del paquete, en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        int tam = 0;
        tam += super.obtenerCabecera().obtenerTamanio();
        tam += this.datosTCP.obtenerTamanio();
        tam += (4 * this.pilaEtiquetas.obtenerTamanio());
        return (tam);
    }
    
    /** Este m�todo nos devuelve la constante MPLS, indicando que el paquete es de este
     * tipo.
     * @return La constante MPLS.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.MPLS;
    }
    
    /** Este m�todo nos permite acceder directamente a los datos TCP que se encuentran
     * en el interior del paquete y as� usar sus m�todos.
     * @return Los datos TCP que transporta este paquete MPLS.
     * @since 1.0
     */
    public TDatosTCP obtenerDatosTCP() {
        return datosTCP;
    }
    
    /** Este m�todo nos permite cambiar la carga TCP de este paquete MPLS de una forma
     * r�pida y sencilla.
     * @param d Los nuevos datos TCP que deseamos que tenga el paquete MPLS.
     * @since 1.0
     */
    public void ponerDatosTCP(TDatosTCP d) {
        datosTCP = d;
    }
    
    /** Este m�todo nos permite el acceso directo a la pila de etiquetas MPLS, as�
     * podremos usar sus m�todos directamente.
     * @return La pila de etiquetas del paquete MPLS.
     * @since 1.0
     */
    public TPilaEtiquetasMPLS obtenerPilaEtiquetas() {
        return pilaEtiquetas;
    }
    
    /**
     * Este metodo permite establecer el subtipo de un paquete MPLS.
     * @param st El subtipo. Una constante de la clase TPDU que indica si se trata de un paquete
     * MPLS con garant�a de servicio o no.
     * @since 1.0
     */
    public void ponerSubtipo(int st) {
        subtipo = st;
    }
    
    /**
     * Este m�todo permite obtener el subtipo de un paquete MPLS, es decir, si el
     * paquete MPLS es o no un paquete con GoS.
     * @return El subtipo el paquete MPLS.
     * @since 1.0
     */
    public int obtenerSubTipo() {
        return subtipo;
    }
    
    /** Este atributo representa los datos TCP que est�n incorporados en el paquete.
     * @since 1.0
     */
    private TDatosTCP datosTCP;
    /** Este atributo privado representa la pila de etiquetas MPLS que  acompa�ana a la
     * cabecera de esta instancia del paquete MPLS.
     * @since 1.0
     */
    private TPilaEtiquetasMPLS pilaEtiquetas;
    /**
     * Este atributo almacenar� el subtipo de paquete MPLS, que puede ser: Con o sin
     * GoS.
     * @since 1.0
     */
    private int subtipo;
}
