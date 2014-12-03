/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.protocols;

import java.util.*;

/** Esta clase implementa un paquete MPLS, con todos sus campos diferenciados y
 * accesibles.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TMPLSPDU extends TAbstractPDU {
    
    /** Este m�todo es el constructor de la clase. Crea una instancia nueva de TPDUMPLS
     * de acuerdo a los par�metros especificados.
     * @param id Identificador �nico del paquete.
     * @param ipo Direcci�n IP origen del paquete.
     * @param ipd Direcci�n IP del destino del paquete MPLS.
     * @param tamDatos Tama�o de los datos TCP que incorpora el paquete MPLS en su interior, en bytes.
     * @since 1.0
     */
    public TMPLSPDU(long id, String ipo, String ipd, int tamDatos) {
        super(id, ipo, ipd);
        datosTCP = new TTCPPayload(tamDatos);
        pilaEtiquetas = new TMPLSLabelStack();
        subtipo = super.MPLS;
    }

    
    /**
     * Este m�todo devuelve un paquete MPLS que es una r�plica exacta a la instancia
     * actual.
     * @since 1.0
     * @return Una copia exacta de la instancia actual.
     */    
    public TMPLSPDU obtenerCopia() {
        long id = this.obtenerIdentificador();
        String IPo = new String(this.getHeader().obtenerIPOrigen());
        String IPd = new String(this.getHeader().obtenerIPDestino());
        int tamD = this.datosTCP.obtenerTamanio() - 20;
        TMPLSPDU clon = new TMPLSPDU(id, IPo, IPd, tamD);
        if (this.getHeader().getOptionsField().isUsed()) {
            int nivelGoS = this.getHeader().getOptionsField().getEncodedGoSLevel();
            clon.getHeader().getOptionsField().ponerNivelGoS(nivelGoS);
            int idGoS = this.getHeader().getOptionsField().obtenerIDPaqueteGoS();
            clon.getHeader().getOptionsField().ponerIDPaqueteGoS(idGoS);
            if (this.getHeader().getOptionsField().tieneMarcasDePaso()) {
                int numMarcas = this.getHeader().getOptionsField().obtenerNumeroDeNodosActivosAtravesados();
		int i=0;
                String marcaActual = null;
		for (i=0; i< numMarcas; i++) {
                    marcaActual = this.getHeader().getOptionsField().obtenerActivoNodoAtravesado(i);
                    if (marcaActual != null) {
                        clon.getHeader().getOptionsField().ponerNodoAtravesado(new String(marcaActual));
                    }
                }
            }
        }
        TMPLSLabel etiquetaActual = null;
        TMPLSLabel etiquetaNuevaLocal = null;
        TMPLSLabel etiquetaNuevaClon = null;
        LinkedList pilaEtiquetasLocal = new LinkedList();
        LinkedList pilaEtiquetasClon = new LinkedList();
        while (this.getLabelStack().obtenerTamanio() > 0) {
            etiquetaActual = this.getLabelStack().getTop();
            this.getLabelStack().borrarEtiqueta();
            int idEtiq = etiquetaActual.obtenerIdentificador();
            etiquetaNuevaLocal = new TMPLSLabel(idEtiq);
            etiquetaNuevaLocal.ponerBoS(etiquetaActual.obtenerBoS());
            etiquetaNuevaLocal.ponerEXP(etiquetaActual.getEXPField());
            etiquetaNuevaLocal.setLabelField(etiquetaActual.getLabelField());
            etiquetaNuevaLocal.ponerTTL(etiquetaActual.obtenerTTL());
            etiquetaNuevaClon = new TMPLSLabel(idEtiq);
            etiquetaNuevaClon.ponerBoS(etiquetaActual.obtenerBoS());
            etiquetaNuevaClon.ponerEXP(etiquetaActual.getEXPField());
            etiquetaNuevaClon.setLabelField(etiquetaActual.getLabelField());
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
            this.getLabelStack().ponerEtiqueta((TMPLSLabel) pilaEtiquetasLocal.removeFirst());
        }
        while (pilaEtiquetasClon.size() > 0) {
            clon.getLabelStack().ponerEtiqueta((TMPLSLabel) pilaEtiquetasClon.removeFirst());
        }
        return clon;
    }
    
    /** Este m�todo nos devuelve el tama�o completo del paquete MPLS, en bytes.
     * @return El tama�o completeo del paquete, en bytes.
     * @since 1.0
     */
    public int getSize() {
        int tam = 0;
        tam += super.getHeader().obtenerTamanio();
        tam += this.datosTCP.obtenerTamanio();
        tam += (4 * this.pilaEtiquetas.obtenerTamanio());
        return (tam);
    }
    
    /** Este m�todo nos devuelve la constante MPLS, indicando que el paquete es de este
     * tipo.
     * @return La constante MPLS.
     * @since 1.0
     */
    public int getType() {
        return super.MPLS;
    }
    
    /** Este m�todo nos permite acceder directamente a los datos TCP que se encuentran
     * en el interior del paquete y as� usar sus m�todos.
     * @return Los datos TCP que transporta este paquete MPLS.
     * @since 1.0
     */
    public TTCPPayload obtenerDatosTCP() {
        return datosTCP;
    }
    
    /** Este m�todo nos permite cambiar la carga TCP de este paquete MPLS de una forma
     * r�pida y sencilla.
     * @param d Los nuevos datos TCP que deseamos que tenga el paquete MPLS.
     * @since 1.0
     */
    public void ponerDatosTCP(TTCPPayload d) {
        datosTCP = d;
    }
    
    /** Este m�todo nos permite el acceso directo a la pila de etiquetas MPLS, as�
     * podremos usar sus m�todos directamente.
     * @return La pila de etiquetas del paquete MPLS.
     * @since 1.0
     */
    public TMPLSLabelStack getLabelStack() {
        return pilaEtiquetas;
    }
    
    /**
     * Este metodo permite establecer el subtipo de un paquete MPLS.
     * @param st El subtipo. Una constante de la clase TAbstractPDU que indica si se trata de un paquete
 MPLS con garant�a de servicio o no.
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
    public int getSubtype() {
        return subtipo;
    }
    
    /** Este atributo representa los datos TCP que est�n incorporados en el paquete.
     * @since 1.0
     */
    private TTCPPayload datosTCP;
    /** Este atributo privado representa la pila de etiquetas MPLS que  acompa�ana a la
     * cabecera de esta instancia del paquete MPLS.
     * @since 1.0
     */
    private TMPLSLabelStack pilaEtiquetas;
    /**
     * Este atributo almacenar� el subtipo de paquete MPLS, que puede ser: Con o sin
     * GoS.
     * @since 1.0
     */
    private int subtipo;
}
