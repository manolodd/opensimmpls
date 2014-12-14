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
 * This class implements a IPv4 header.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TIPv4Header {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TIPv4Header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param originIP IP addres of this packet's sender.
     * @param targetIP IP addres of this packet's receiver.
     * @since 1.0
     */
    public TIPv4Header(String originIP, String targetIP) {
        this.originIP = originIP;
        this.targetIP = targetIP;
        this.IPv4OptionsField = new TIPv4OptionsField();
        // FIX: create and use a constant instead of this harcoded value.
        this.TTL = 255;
    }

    /**
     * This method gets a global unique identifier that identifies unambiguously
     * this packet from other from the point of view of GoS. As defined in the
     * proposal "Guarantee of Servico (GoS) Support over MPLS using Active
     * Techniques".
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A global unique identifier of this packet within the MPLS domain.
     * @since 1.0
     */
    public int getGoSGlobalUniqueIdentifier() {
        String RawGoSGlobalUniqueIdentifier = "";
        if (this.IPv4OptionsField.isUsed()) {
            RawGoSGlobalUniqueIdentifier = this.originIP + this.IPv4OptionsField.getPacketLocalUniqueIdentifier();
            return RawGoSGlobalUniqueIdentifier.hashCode();
        }
        // FIX: Create and use a constant instead of this harcoded value
        return -1;
    }

    /**
     * This method gets the size in bytes (octects) of this IPv4 header,
     * including the options field if used.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the size in bytes (octects) of this IPv4 header, including the
     * options field if used.
     * @since 1.0
     */
    public int getSize() {
        // FIX: create and use a constant instead of the following harcoded
        // value.
        int size = 20; // IP header size in bytes (octets)
        size += this.IPv4OptionsField.getSize(); // options field size
        return size;
    }

    /**
     * This method gets the IP address of the sender of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the IP address of the sender of this packet.
     * @since 1.0
     */
    public String getOriginIP() {
        return this.originIP;
    }

    /**
     * This method sets the IP address of the sender of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param originIP The IP address of the sender of this packet.
     * @since 1.0
     */
    public void setOriginIP(String originIP) {
        this.originIP = originIP;
    }

    /**
     * This method gets the IP address of the receiver of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the IP address of the receiver of this packet.
     * @since 1.0
     */
    public String getTargetIP() {
        return this.targetIP;
    }

    /**
     * This method sets the IP address of the receiver of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param targetIP The IP address of the receiver of this packet.
     * @since 1.0
     */
    public void setTargetIP(String targetIP) {
        this.targetIP = targetIP;
    }

    /**
     * This method sets the TTL (Time To Live) field of the IPv4 header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param ttl The desired TTL value for this IPv4 header.
     * @since 1.0
     */
    public void setTTL(int ttl) {
        this.TTL = ttl;
    }

    /**
     * This method gets the TTL (Time To Live) field of the IPv4 header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The TTL value of this IPv4 header.
     * @since 1.0
     */
    public int getTTL() {
        return this.TTL;
    }

    /**
     * This method gets the options field of this IPv4 header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The option fields of this IPv4 header.
     * @since 1.0
     */
    public TIPv4OptionsField getOptionsField() {
        return this.IPv4OptionsField;
    }

    private String originIP;
    private String targetIP;
    private int TTL;
    private TIPv4OptionsField IPv4OptionsField;
}
