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
package com.manolodominguez.opensimmpls.commons;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a IPv4 address generator. It generates consecutive IPv4
 * addressess from 10.0.0.1 to 10.255.255.254 without repetitions.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
@SuppressWarnings("serial")
public class TIPv4AddressGenerator {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TIPv4AddressGenerator.
     *
     * @since 2.0
     */
    public TIPv4AddressGenerator() {
        octet1 = DEFAULT_OCTECT1;
        octet2 = DEFAULT_OCTECT2;
        octet3 = DEFAULT_OCTECT3;
        octet4 = DEFAULT_OCTECT4;
        translations = ResourceBundle.getBundle(AvailableBundles.T_IPV4_ADDRESS_GENERATOR.getPath());
    }

    /**
     * This method sets the IPv4 address generator new internal value.
     *
     * @param newInternalIPv4Address the ID generator new internal value.
     * @return TRUE, if the specified IPv4 address is assigned. Otherwise,
     * FALSE.
     * @since 2.0
     */
    private boolean setIPv4Address(String newInternalIPv4Address) {
        if (newInternalIPv4Address == null) {
            logger.error(translations.getString("argumentIsNull"));
            logger.error(translations.getString("notAValidIPv4Address"));
        } else {
            if (isAValidIPv4Address(newInternalIPv4Address)) {
                String[] octects = newInternalIPv4Address.split(IPV4_SEPARATOR_REGEX);
                octet2 = Integer.parseInt(octects[1]);
                octet3 = Integer.parseInt(octects[2]);
                octet4 = Integer.parseInt(octects[3]);
                return true;
            } else {
                logger.error(translations.getString("notAValidIPv4Address"));
            }
        }
        return false;
    }

    /**
     * This method check whether the IPv4 specified as an argument is a valid
     * IPv4 address (in the context of this TIPV4AddressGenerator) os not. An
     * IPv4 address is valid in the context of this TIPV4AddressGenerator if it
     * is in the range of 10.0.0.1 - 10.255.255.254
     *
     * @param ipv4Address the IPv4 address to check.
     * @return TRUE, if the specified IPv4 address is valid. Otherwise, FALSE.
     * @since 2.0
     */
    private boolean isAValidIPv4Address(String ipv4Address) {
        if (ipv4Address == null) {
            logger.error(translations.getString("argumentIsNull"));
            logger.error(translations.getString("notAValidIPv4Address"));
            return false;
        } else {
            if (ipv4Address.matches(IPV4_REGEX)) {
                int auxOctect1 = MIN_OCTECT_VALUE;
                int auxOctect2 = MIN_OCTECT_VALUE;
                int auxOctect3 = MIN_OCTECT_VALUE;
                int auxOctect4 = MIN_OCTECT_VALUE;
                String[] octects = ipv4Address.split(IPV4_SEPARATOR_REGEX);
                auxOctect1 = Integer.parseInt(octects[0]);
                auxOctect2 = Integer.parseInt(octects[1]);
                auxOctect3 = Integer.parseInt(octects[2]);
                auxOctect4 = Integer.parseInt(octects[3]);
                if (auxOctect1 != 10) {
                    logger.error(translations.getString("notAValidIPv4Address"));
                    return false; // X!=10.0.0.0 --> IPv4 addresses in opensimmpls are from 10.0.0.0/8
                }
                if ((auxOctect2 == MIN_OCTECT_VALUE) && (auxOctect3 == MIN_OCTECT_VALUE) && (auxOctect4 == MIN_OCTECT_VALUE)) {
                    logger.error(translations.getString("notAValidIPv4Address"));
                    return false; // 10.0.0.0 --> Reserved for network address
                }
                if ((auxOctect2 > MAX_OCTECT_VALUE) || (auxOctect3 > MAX_OCTECT_VALUE) || (auxOctect4 > MAX_OCTECT_VALUE)) {
                    logger.error(translations.getString("notAValidIPv4Address"));
                    return false; // Any octet greater than 255
                }
                if ((auxOctect2 < MIN_OCTECT_VALUE) || (auxOctect3 < MIN_OCTECT_VALUE) || (auxOctect4 < MIN_OCTECT_VALUE)) {
                    logger.error(translations.getString("notAValidIPv4Address"));
                    return false; // Any octet lower than 0
                }
                if ((auxOctect2 == MAX_OCTECT_VALUE) && (auxOctect3 == MAX_OCTECT_VALUE) && (auxOctect4 == MAX_OCTECT_VALUE)) {
                    logger.error(translations.getString("notAValidIPv4Address"));
                    return false; // 10.255.255.255 --> Reserved for broadcast address
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * This method resets the IPv4 address generator to its initial internal
     * value.
     *
     * @since 2.0
     */
    public void reset() {
        octet2 = DEFAULT_OCTECT2;
        octet3 = DEFAULT_OCTECT3;
        octet4 = DEFAULT_OCTECT4;
    }

    /**
     * This method sets the IPv4 address generator internal value to the one
     * specified as an argument, only if the specified IPv4 address is greater
     * than the one the IPv4 address generator has.
     *
     * @param newInternalIPv4Address the new internal IPv4 address to set the
     * IPv4 address generator.
     * @since 2.0
     */
    public void setIPv4AddressIfGreater(String newInternalIPv4Address) {
        if (newInternalIPv4Address == null) {
            logger.error(translations.getString("argumentIsNull"));
            throw new IllegalArgumentException(translations.getString("argumentIsNull"));
        } else {
            if (isAValidIPv4Address(newInternalIPv4Address)) {
                String[] octects = newInternalIPv4Address.split(IPV4_SEPARATOR_REGEX);
                int auxOctect2 = Integer.parseInt(octects[1]);
                int auxOctect3 = Integer.parseInt(octects[2]);
                int auxOctect4 = Integer.parseInt(octects[3]);
                if (auxOctect2 > octet2) {
                    setIPv4Address(newInternalIPv4Address);
                } else if (auxOctect2 == octet2) {
                    if (auxOctect3 > octet3) {
                        setIPv4Address(newInternalIPv4Address);
                    } else if (auxOctect3 == octet3) {
                        if (auxOctect4 > octet4) {
                            setIPv4Address(newInternalIPv4Address);
                        }
                    }
                }
            } else {
                logger.error(translations.getString("notAValidIPv4Address"));
                throw new IllegalArgumentException(translations.getString("notAValidIPv4Address"));
            }
        }
    }

    /**
     * This method generates a new IPv4 address.
     *
     * @return A new IPv4 addres in in the range of 10.0.0.1 - 10.255.255.254.
     * @throws EIPv4AddressGeneratorOverflow when the IPv4 address generator
     * reaches its maximum value (10.255.255.254).
     * @since 2.0
     */
    public String getNextIPv4Address() throws EIPv4AddressGeneratorOverflow {
        // Analysis of current IPv4 value
        if (octet2 >= MAX_OCTECT_VALUE) { //10.255.X:X
            if (octet3 >= MAX_OCTECT_VALUE) { //10.255.255.X
                if (octet4 >= (MAX_OCTECT_VALUE - 1)) { //10.255.255.254
                    throw new EIPv4AddressGeneratorOverflow();
                } else { //10.255.255.<254
                    octet4++;
                }
            } else { //10.255.<255.255
                if (octet4 >= (MAX_OCTECT_VALUE)) { //10.255.<255.255
                    octet3++;
                    octet4 = MIN_OCTECT_VALUE;
                } else {  //10.255.<255.<255
                    octet4++;
                }
            }
        } else { //10.<255.X.X
            if (octet3 >= MAX_OCTECT_VALUE) { //10.<255.255.X
                if (octet4 >= (MAX_OCTECT_VALUE)) { //10.<255.255.255
                    octet2++;
                    octet3 = MIN_OCTECT_VALUE;
                    octet4 = MIN_OCTECT_VALUE;
                } else { //10.<255.255.<255
                    octet4++;
                }
            } else { //10.<255.<255.X
                if (octet4 >= MAX_OCTECT_VALUE) { //10.<255.<255.255 
                    octet3++;
                    octet4 = MIN_OCTECT_VALUE;
                } else { //10.<255.<255.<255 
                    octet4++;
                }
            }
        }
        // Returns the new IPv4 value
        return (this.octet1 + IPV4_SEPARATOR + this.octet2 + IPV4_SEPARATOR + this.octet3 + IPV4_SEPARATOR + this.octet4);
    }

    private final int octet1;
    private int octet2;
    private int octet3;
    private int octet4;

    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TIPv4AddressGenerator.class);

    private static final int DEFAULT_OCTECT1 = 10;
    private static final int DEFAULT_OCTECT2 = 0;
    private static final int DEFAULT_OCTECT3 = 0;
    private static final int DEFAULT_OCTECT4 = 0;
    private static final int MIN_OCTECT_VALUE = 0;
    private static final int MAX_OCTECT_VALUE = 255;
    private static final String IPV4_REGEX = "[1][0][.][0-2]{0,1}[0-9]{0,1}[0-9][.][0-2]{0,1}[0-9]{0,1}[0-9][.][0-2]{0,1}[0-9]{0,1}[0-9]";
    private static final String IPV4_SEPARATOR_REGEX = "[.]";
    private static final String IPV4_SEPARATOR = ".";
}
