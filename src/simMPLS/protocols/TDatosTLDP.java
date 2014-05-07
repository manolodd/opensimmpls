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

/** Esta clase implementa el payload de un paquete TLDP, es decir, mensajes para la
 * se�alizaci�n mediante etiquetas.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TDatosTLDP {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TDatosLDP.
     * @since 1.0
     */
    public TDatosTLDP() {
        mensaje = this.SOLICITUD_ETIQUETA;
        ipDestinoFinal = "";
        label = 16;
        identificadorLDP = 0;
    }
    
    /** Este m�todo devuelve el tama�o, en bytes, de esta instancia del payload TLDP.
     * @return El tama�o en bytes del payload.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return 9;
    }
    
    /** Establece el mensaje de un paquete TLDP puesto que en principio el paquete es
     * TLDP gen�rico y no especifica ninguna solicitud o respuesta.
     * @param m El mensaje, que ser� una de las constantes definidas en la clase.
     * @since 1.0
     */
    public void ponerMensaje(int m) {
        mensaje = m;
    }
    
    /** Este m�todo nos permite conocer el valor del mensaje del payload.
     * @return El mensaje que lleva impl�cito el paquete.
     * @since 1.0
     */
    public int obtenerMensaje() {
        return mensaje;
    }
    
    /** Este m�todo nos permite especificar la IP del destino final del tr�fico para el
     * cual estamos buscando un LSP.
     * @param ip La IP del nodo receptor final del tr�fico.
     * @since 1.0
     */
    public void ponerIPDestinoFinal(String ip) {
        ipDestinoFinal = ip;
    }
    
    /** Este m�todo nos permite conocer la IP del nodo receptor final para el cual vamos
     * a crear un LSP.
     * @return La IP del nodo destino final del tr�fico.
     * @since 1.0
     */
    public String obtenerIPDestinoFinal() {
        return ipDestinoFinal;
    }
    
    /** Este m�todo permite especificar una etiqueta en la instancia de payload TLDP que
     * estamos tratando.
     * @param e El valor de la etiqueta que deseamos transportar v�a TLDP.
     * @since 1.0
     */
    public void ponerEtiqueta(int e) {
        label = e;
    }
    
    /** Este m�todo nos permite obtener el valor de la etiqueta quer est� siendo
     * transportada v�a esta instancia de payload TLDP.
     * @return El valor de la etiqueta.
     * @since 1.0
     */
    public int obtenerEtiqueta() {
        return label;
    }
    
    /** Este m�todo permite especificar dentro de los datos TLDP el identificaci�n de
     * sesi�n TLDP que estamos usando.
     * @param i El identificador de la sesi�n TLDP.
     * @since 1.0
     */
    public void ponerIdentificadorLDP(int i) {
        identificadorLDP = i;
    }
    
    /** Este m�todo nos permite conocer el identificador de sesi�n TLDP que se expresa
     * en los datos TLDP de la instancia actual.
     * @return el identificador �nico TLDP.
     * @since 1.0
     */
    public int obtenerIdentificadorLDP() {
        return identificadorLDP;
    }
    
    /**
     * Esta constante se usa para solicitar una etiqueta en un dominio MPLS. As�mismo,
     * este valor provoca la apertura de una sesi�n TLDP.
     * @since 1.0
     */    
    public static final int SOLICITUD_ETIQUETA = -33;
    /**
     * Esta constante se usa para denegar la concesi�n de una etiqueta en un dominio MPLS.
     * @since 1.0
     */    
    public static final int SOLICITUD_NO = -31;
    /**
     * Esta constante se usa para conceder una etiqueta en un dominio MPLS.
     * @since 1.0
     */    
    public static final int SOLICITUD_OK = -30;
    /**
     * Esta constante se usa para eliminar una etiqueta y desasociar as� el LSP
     * asociado.
     * @since 1.0
     */    
    public static final int ELIMINACION_ETIQUETA = -32;
    /**
     * Esta constante se usa para confirmar la eliminaci�n de una etiqueta y la
     * disociaci�n del el LSP asociado.
     * @since 1.0
     */    
    public static final int ELIMINACION_OK = -34;
    
    /** Atributo que es el mensaje concreto que lleva incorporado este paquete TLDP.
     * Ser� alguno de los valores constantes definidos en esta clase.
     * @since 1.0
     */
    private int mensaje;
    /** Atributo que indica, dentro del paquete TLDP, cu�l es el destino final al que se
     * dirige el flujo de informaci�n que se va a utilizar. De este modo cada nodo
     * puede elegir el mejor salto posible de acuerdo con el destino del tr�fico.
     * @since 1.10
     */
    private String ipDestinoFinal;
    /** Este atributo indica, en lo casos que corresponda, la etiqueta afectada de una eliminaci�n, una
     * concesi�n, etc�tera.
     * @since 1.0
     */
    private int label;
    /** Este atributo es el identificador de una sesi�n LDP abierta entre dos nodos
     * adyacentes. Se usa para cuando hay que enlazar el destino con el origen a trav�s
     * de un LSR o un LER.
     * @since 1.0
     */
    private int identificadorLDP;
}
