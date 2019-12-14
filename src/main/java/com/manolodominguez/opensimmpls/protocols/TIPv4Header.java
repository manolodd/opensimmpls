/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package com.manolodominguez.opensimmpls.protocols;

/**
 * This class implements a IPv4 header.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TIPv4Header {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TIPv4Header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param originIP IP addres of this packet's sender.
     * @param targetIP IP addres of this packet's receiver.
     * @since 2.0
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
     * @since 2.0
     */
    public int getGoSGlobalUniqueIdentifier() {
        String RawGoSGlobalUniqueIdentifier = "";
        if (this.IPv4OptionsField.isUsed()) {
            // this.targetIP was included 12/12/19
            RawGoSGlobalUniqueIdentifier = this.originIP + this.IPv4OptionsField.getPacketLocalUniqueIdentifier() + this.targetIP;
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
     * @since 2.0
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
     * @since 2.0
     */
    public String getOriginIPv4Address() {
        return this.originIP;
    }

    /**
     * This method sets the IP address of the sender of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param originIP The IP address of the sender of this packet.
     * @since 2.0
     */
    public void setOriginIP(String originIP) {
        this.originIP = originIP;
    }

    /**
     * This method gets the IP address of the receiver of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the IP address of the receiver of this packet.
     * @since 2.0
     */
    public String getTailEndIPAddress() {
        return this.targetIP;
    }

    /**
     * This method sets the IP address of the receiver of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param targetIP The IP address of the receiver of this packet.
     * @since 2.0
     */
    public void setTargetIP(String targetIP) {
        this.targetIP = targetIP;
    }

    /**
     * This method sets the TTL (Time To Live) field of the IPv4 header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param ttl The desired TTL value for this IPv4 header.
     * @since 2.0
     */
    public void setTTL(int ttl) {
        this.TTL = ttl;
    }

    /**
     * This method gets the TTL (Time To Live) field of the IPv4 header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The TTL value of this IPv4 header.
     * @since 2.0
     */
    public int getTTL() {
        return this.TTL;
    }

    /**
     * This method gets the options field of this IPv4 header.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The option fields of this IPv4 header.
     * @since 2.0
     */
    public TIPv4OptionsField getOptionsField() {
        return this.IPv4OptionsField;
    }

    private String originIP;
    private String targetIP;
    private int TTL;
    private TIPv4OptionsField IPv4OptionsField;
}
