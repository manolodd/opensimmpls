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
 * Protocol) packet. As defined in the proposal "Guarantee of Servico (GoS)
 * Support over MPLS using Active Techniques".
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TGPSRPPDU extends TAbstractPDU {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TGPSRPPDU.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id Packet identifier.
     * @param originIP IP addres of this packet's sender.
     * @param targetIP IP addres of this packet's receiver.
     * @since 1.0
     */
    public TGPSRPPDU(long id, String originIP, String targetIP) {
        super(id, originIP, targetIP);
        this.TCPPayload = new TTCPPayload(0);
        this.GPSRPPayload = new TGPSRPPayload();
    }

    /**
     * This method returns the size of the packet in bytes (octects).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size of this packet in bytes (octects).
     * @since 1.0
     */
    @Override
    public int getSize() {
        int auxSize = 0;
        auxSize += super.getIPv4Header().getSize(); // IPv4 header
        auxSize += this.TCPPayload.getSize(); // TCP header
        auxSize += this.GPSRPPayload.getSize(); // GPSRP packet size
        return (auxSize);
    }

    /**
     * This method returns the type of the packet, as defined by constants in
     * TAbstractPDU class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of this packet.
     * @since 1.0
     */
    @Override
    public int getType() {
        return TAbstractPDU.GPSRP;
    }

    /**
     * This method return the TCP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TCP payload of this packet.
     * @since 1.0
     */
    public TTCPPayload getTCPPayload() {
        return this.TCPPayload;
    }

    /**
     * This method return the GPSRP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return GPSRP payload (header) of this packet.
     * @since 1.0
     */
    public TGPSRPPayload getGPSRPPayload() {
        return this.GPSRPPayload;
    }

    /**
     * This method gets the IPv4 header of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The IPv4 header of this packet.
     * @since 1.0
     */
    @Override
    public TIPv4Header getIPv4Header() {
        return super.getIPv4Header();
        // FIX: this is unnecessary as it is already defined in superclass.
    }

    /**
     * This method returns the subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The subtype of this packet. For instances of this class, it
     * returns GPSRP, as defined in TAbstractPDU.
     * @since 1.0
     */
    @Override
    public int getSubtype() {
        return TAbstractPDU.GPSRP;
    }

    @Override
    public void setSubtype(int subtype) {
        // Do nothing. FIX (remove).
    }

    private TTCPPayload TCPPayload;
    private TGPSRPPayload GPSRPPayload;
}
