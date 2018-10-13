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
package com.manolodominguez.opensimmpls.hardware.dmgp;

/**
 * This enum is used to access some GPSRP config values from a centralilzed
 * point. This easies accessing that values from classes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public enum AvailableGPSRPConfigValues {
    GPSRP_TIMEOUT_NANOSECONDS(50000),
    GPSRP_ATTEMPTS(8);

    private final int value;

    /**
     * This is the constructor of the enum. It will set the default value of
     * each enum item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param value the value of this GPSRP config value.
     * @since 2.0
     */
    private AvailableGPSRPConfigValues(int value) {
        this.value = value;
    }

    /**
     * This method gets the value corresponding to the enum's item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the value corresponding to the enum's item.
     * @since 2.0
     */
    public int getValue() {
        return this.value;
    }
}
