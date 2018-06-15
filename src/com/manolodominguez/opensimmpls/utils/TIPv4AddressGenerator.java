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
package com.manolodominguez.opensimmpls.utils;

/**
 * This class implements a IPv4 address generator. It generates consecutive IPv4
 * addressess from 10.0.0.1 to 10.255.255.254 without repetitions.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TIPv4AddressGenerator {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TIPv4AddressGenerator.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TIPv4AddressGenerator() {
        // FIX: Do not use harcoded values. Use class constants instead.
        this.octect1 = 10;
        this.octect2 = 0;
        this.octect3 = 0;
        this.octect4 = 0;
    }

    /**
     * This method sets the IPv4 address generator new internal value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param newInternalIPv4Address the ID generator new internal value.
     * @since 2.0
     */
    private boolean setIPv4Address(String newInternalIPv4Address) {
        if (isAValidIPv4Address(newInternalIPv4Address)) {
            String[] octects = newInternalIPv4Address.split("[.]");
            this.octect2 = Integer.parseInt(octects[1]);
            this.octect3 = Integer.parseInt(octects[2]);
            this.octect4 = Integer.parseInt(octects[3]);
            return true;
        }
        return false;
    }

    /**
     * This method check whether the IPv4 specified as an argument is a valid
     * IPv4 address (in the context of this TIPV4AddressGenerator) os not. An
     * IPv4 address is valid in the context of this TIPV4AddressGenerator if it
     * is in the range of 10.0.0.1 - 10.255.255.254
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param ipv4Address the IPv4 address to check.
     * @return TRUE, if the specified IPv4 address is valid. Otherwise, FALSE.
     * @since 2.0
     */
    private boolean isAValidIPv4Address(String ipv4Address) {
        if (ipv4Address.matches("[1][0][.][0-2]{0,1}[0-9]{0,1}[0-9][.][0-2]{0,1}[0-9]{0,1}[0-9][.][0-2]{0,1}[0-9]{0,1}[0-9]")) {
            int auxOctect2 = 0;
            int auxOctect3 = 0;
            int auxOctect4 = 0;
            String[] octects = ipv4Address.split("[.]");
            auxOctect2 = Integer.parseInt(octects[1]);
            auxOctect3 = Integer.parseInt(octects[2]);
            auxOctect4 = Integer.parseInt(octects[3]);
            // FIX: Do not use harcoded values. Use class constants instead.
            if ((auxOctect2 == 0) && (auxOctect3 == 0) && (auxOctect4 == 0)) {
                return false;
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            if ((auxOctect2 > 255) || (auxOctect3 > 255) || (auxOctect4 > 255)) {
                return false;
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            if ((auxOctect2 > 255) && (auxOctect3 > 255) && (auxOctect4 > 255)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method resets the IPv4 address generator to its initial internal
     * value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        // FIX: Do not use harcoded values. Use class constants instead.
        this.octect2 = 0;
        this.octect3 = 0;
        this.octect4 = 0;
    }

    /**
     * This method sets the IPv4 address generator internal value to the one
     * specified as an argument, only if the specified IPv4 address is greater
     * than the one the IPv4 address generator has.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param newInternalIPv4Address the new internal IPv4 address to set the
     * IPv4 address generator.
     * @since 2.0
     */
    public void setIPv4AddressIfGreater(String newInternalIPv4Address) {
        if (isAValidIPv4Address(newInternalIPv4Address)) {
            String[] octects = newInternalIPv4Address.split("[.]");
            int auxOctect2 = Integer.parseInt(octects[1]);
            int auxOctect3 = Integer.parseInt(octects[2]);
            int auxOctect4 = Integer.parseInt(octects[3]);
            if (auxOctect2 > this.octect2) {
                this.setIPv4Address(newInternalIPv4Address);
            } else if (auxOctect2 == this.octect2) {
                if (auxOctect3 > this.octect3) {
                    this.setIPv4Address(newInternalIPv4Address);
                } else if (auxOctect3 == this.octect3) {
                    if (auxOctect4 > this.octect4) {
                        this.setIPv4Address(newInternalIPv4Address);
                    }
                }
            }
        }
    }

    /**
     * This method generates a new IPv4 address.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A new IPv4 addres in in the range of 10.0.0.1 - 10.255.255.254.
     * @throws EIPv4AddressGeneratorOverflow when the IPv4 address generator
     * reaches its maximum value (10.255.255.254).
     * @since 2.0
     */
    public String getIPv4Address() throws EIPv4AddressGeneratorOverflow {
        // FIX: Do not use harcoded values. Use class constants instead.
        if (this.octect4 < 255) {
            this.octect4++;
        } else {
            if (this.octect3 < 255) {
                this.octect4 = 0;
                this.octect3++;
            } else {
                if (this.octect2 < 254) {
                    this.octect4 = 0;
                    this.octect3 = 0;
                    this.octect2++;
                } else {
                    throw new EIPv4AddressGeneratorOverflow();
                }
            }
        }
        return (this.octect1 + "." + this.octect2 + "." + this.octect3 + "." + this.octect4);
    }

    private final int octect1;
    private int octect2;
    private int octect3;
    private int octect4;
}
