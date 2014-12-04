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

/**
 * Esta clase implementa el payload de un paquete GPSRP, es decir, mensajes para la
 * recuperaci�n local de paquetes.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TGPSRPPayload {
    
    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TDatosGPSRP.
     * @since 1.0
     */
    public TGPSRPPayload() {
        mensaje = this.SOLICITUD_RETRANSMISION;
        flujo = 0;
        idPaquete = 0;
    }
    
    /**
     * Este m�todo permite establecer el flujo al que pertenece el paquete cuya
     * a cuya retransmisi�n se refiere este mensaje.
     * @param idf El identificador del flujo al que pertenece el paquete solicitado.
     * @since 1.0
     */    
    public void ponerFlujo(int idf) {
        flujo = idf;
    }
    
    /**
     * Este m�todo permite obtener el flujo al que pertenece el paquete cuya
     * a cuya retransmisi�n se refiere este mensaje.
     * @return El identificador del flujo al que pertenece el paquete solicitado.
     * @since 1.0
     */    
    public int obtenerFlujo() {
        return this.flujo;
    }
    
    /**
     * Este m�todo permite establecer el identificador del paquete a cuya
     * retransmisi�n se refiere este mensaje.
     * @param idp Identificador del paquete buscado.
     * @since 1.0
     */    
    public void ponerIdPaquete(int idp) {
        this.idPaquete = idp;
    }
    
    /**
     * Este m�todo permite obtener el identificador del paquete a cuya
     * retransmisi�n se refiere este mensaje.
     * @return Identificador del paquete solicitado.
     * @since 1.0
     */    
    public int obtenerIdPaquete() {
        return this.idPaquete;
    }
    
    /**
     * Este m�todo devuelve el tama�o, en bytes, de esta instancia del payload GPSRP.
     * @return El tama�o en bytes del payload.
     * @since 1.0
     */
    public int setSize() {
        return 9;
    }
    
    /**
     * Establece el mensaje de un paquete GPSRP puesto que en principio el paquete es
     * GPSRP gen�rico y no especifica ninguna solicitud o respuesta.
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
    
    /**
     * Esta constante se usa para solicitar la retransmisi�n de un paquete marcado con
     * GoS.
     * @since 1.0
     */    
    public static final int SOLICITUD_RETRANSMISION = -1;
    /**
     * Esta constante se usa para denegar la retransmisi�n de un paquete marcado con
     * GOS.
     * @since 1.0
     */    
    public static final int RETRANSMISION_NO = -2;
    /**
     * Esta constante se usa para conceder la retransmisi�n de un paquete marcado con
     * GoS.
     * @since 1.0
     */    
    public static final int RETRANSMISION_OK = -3;
    
    /** Atributo que es el mensaje concreto que lleva incorporado este paquete TLDP.
     * Ser� alguno de los valores constantes definidos en esta clase.
     * @since 1.0
     */
    private int mensaje;
    private int flujo;
    private int idPaquete;
}
