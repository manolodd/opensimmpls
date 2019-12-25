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

/**
 * This enum is used to access unit translations from a centralilzed point. This
 * easies accessing the same unit translations from classes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public enum UnitsTranslations {
    BITS_PER_OCTETS(8),
    OCTETS_PER_KILOBYTE(1024),
    OCTETS_PER_MEGABYTE(1024 * 1024),
    OCTETS_PER_GIGABYTE(1024 * 1024 * 1024);

    // Be careful not to specify an argument greater than Integer.MAX_VALUE that
    // is 2.147.483.647
    private final int units;

    /**
     * This is the constructor of the enum. It will set the default value of
     * each enum item.
     *
     * @param units the number of trasnslated units of the enum's item. Values
     * greater than Integer.MAX_VALUE will be truncated silently.
     * @since 2.0
     */
    private UnitsTranslations(int units) {
        this.units = units;
    }

    /**
     * This method gets the units translation corresponding to the enum's item.
     *
     * @return the units corresponding to the enum's item.
     * @since 2.0
     */
    public int getUnits() {
        return units;
    }
}
