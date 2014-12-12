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
 * This class implements a GPSRP (Guarantee of Service Store and Retransmit
 * Protocol) packet content (payload of GPSRP packet). As defined in the
 * proposal "Guarantee of Servico (GoS) Support over MPLS using Active
 * Techniques".
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TGPSRPPayload {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TGPSRPPayload.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TGPSRPPayload() {
        this.message = TGPSRPPayload.RETRANSMISSION_REQUEST;
        this.flowID = 0;
        this.packetID = 0;
    }

    /**
     * Este m�todo permite establecer el flowID al que pertenece el paquete cuya a
 cuya retransmisi�n se refiere este message.
     *
     * @param flowID El identificador del flowID al que pertenece el paquete
 solicitado.
     * @since 1.0
     */
    public void setFlowID(int flowID) {
        this.flowID = flowID;
    }

    /**
     * Este m�todo permite obtener el flowID al que pertenece el paquete cuya a
 cuya retransmisi�n se refiere este message.
     *
     * @return El identificador del flowID al que pertenece el paquete solicitado.
     * @since 1.0
     */
    public int getFlowID() {
        return this.flowID;
    }

    /**
     * Este m�todo permite establecer el identificador del paquete a cuya
     * retransmisi�n se refiere este message.
     *
     * @param packetID Identificador del paquete buscado.
     * @since 1.0
     */
    public void setPacketID(int packetID) {
        this.packetID = packetID;
    }

    /**
     * Este m�todo permite obtener el identificador del paquete a cuya
     * retransmisi�n se refiere este message.
     *
     * @return Identificador del paquete solicitado.
     * @since 1.0
     */
    public int getPacketID() {
        return this.packetID;
    }

    /**
     * Este m�todo devuelve el tama�o, en bytes, de esta instancia del payload
     * GPSRP.
     *
     * @return El tama�o en bytes del payload.
     * @since 1.0
     */
    public int setSize() {
        return 9;
        // FIX: Create a constant insted of a harcoded value.
    }

    /**
     * Establece el message de un paquete GPSRP puesto que en principio el
     * paquete es GPSRP gen�rico y no especifica ninguna solicitud o respuesta.
     *
     * @param message El message, que ser� una de las constantes definidas en la
     * clase.
     * @since 1.0
     */
    public void setMessage(int message) {
        this.message = message;
    }

    /**
     * Este m�todo nos permite conocer el valor del message del payload.
     *
     * @return El message que lleva impl�cito el paquete.
     * @since 1.0
     */
    public int getMessage() {
        return this.message;
    }

    public static final int RETRANSMISSION_REQUEST = -1;
    public static final int RETRANSMISION_NO = -2;
    public static final int RETRANSMISION_OK = -3;

    private int message;
    private int flowID;
    private int packetID;
}
