/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package simMPLS.protocols;

/**
 * This class implements an abstract PDU containing the minimum set of data that
 * is common for all PDU. Cannot be directly instantiated.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TAbstractPDU implements Comparable {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TAbstractPDU.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id Packet identifier.
     * @param originIP IP addres of this packet's sender.
     * @param targetIP IP addres of this packet's receiver.
     * @since 2.0
     */
    public TAbstractPDU(long id, String originIP, String targetIP) {
        this.id = id;
        this.IPv4Header = new TIPv4Header(originIP, targetIP);
    }

    /**
     * This method gets the identifier of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return This packet's identifier.
     * @since 2.0
     */
    public long getID() {
        return this.id;
    }

    /**
     * This method sets the identifier of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id An identifier for this packet.
     * @since 2.0
     */
    public void setID(long id) {
        this.id = id;
    }

    /**
     * This method gets the IPv4 header of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The IPv4 header of this packet.
     * @since 2.0
     */
    public TIPv4Header getIPv4Header() {
        return this.IPv4Header;
    }

    /**
     * This method sets the IPv4 header for this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param IPv4Header The IPv4 header for this packet.
     * @since 2.0
     */
    public void setHeader(TIPv4Header IPv4Header) {
        this.IPv4Header = IPv4Header;
    }

    /**
     * This method compares the current packet and the packet specified as
     * parameter to know if it is lower, greater or equual (their identifiers).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param abstractPDU The packet to compaare with this one.
     * @return -1, 0 or 1 depending on wheteher the packet specified as
     * parameter is greater, equal or lower than the current one.
     * @since 2.0
     */
    @Override
    public int compareTo(Object abstractPDU) {
        TAbstractPDU pdu = (TAbstractPDU) abstractPDU;
        if (this.getID() > pdu.getID()) {
            return 1;
        } else if (this.getID() == pdu.getID()) {
            return (this.getIPv4Header().getOriginIPv4Address().compareTo(pdu.getIPv4Header().getOriginIPv4Address()));
        } else {
            return -1;
        }
    }

    /**
     * This method has to be implemented by any subclasses. It has to return the
     * size of the packet in bytes (octects).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size of this packet in bytes (octects).
     * @since 2.0
     */
    public abstract int getSize();

    /**
     * This method has to be implemented by any subclasses. It has to return the
     * type of the packet, as defined by constants in TAbstractPDU class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of this packet.
     * @since 2.0
     */
    public abstract int getType();

    /**
     * This method has to be implemented by any subclasses. It has to return the
     * subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The subtype of this packet.
     * @since 2.0
     */
    public abstract int getSubtype();

    /**
     * This method has to be implemented by any subclasses. It has to allow
     * setting the subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packetSubtype The subtype of the packet.
     * @since 2.0
     */
    public abstract void setSubtype(int packetSubtype);

    // Packet's types
    public static final int IPV4 = 0;
    public static final int MPLS = 1;
    public static final int TLDP = 2;
    public static final int GPSRP = 3;
    public static final int IPV4_GOS = 4;
    public static final int MPLS_GOS = 5;
    public static final int RLPRP = 6;

    // Packet's requirements of Guarantee of Service (GoS).
    public static final int EXP_LEVEL0_WITHOUT_BACKUP_LSP = 0;
    public static final int EXP_LEVEL1_WITHOUT_BACKUP_LSP = 1;
    public static final int EXP_LEVEL2_WITHOUT_BACKUP_LSP = 2;
    public static final int EXP_LEVEL3_WITHOUT_BACKUP_LSP = 3;
    public static final int EXP_LEVEL0_WITH_BACKUP_LSP = 4;
    public static final int EXP_LEVEL1_WITH_BACKUP_LSP = 5;
    public static final int EXP_LEVEL2_WITH_BACKUP_LSP = 6;
    public static final int EXP_LEVEL3_WITH_BACKUP_LSP = 7;

    protected long id;
    private TIPv4Header IPv4Header;
}
