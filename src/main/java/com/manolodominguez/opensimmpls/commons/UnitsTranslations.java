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
 * This enum is used to access unit translations from a centralilzed point. 
 * This easies accessing the same unit translations from classes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public enum UnitsTranslations {
    BITS_PER_OCTECTS(8),
    OCTETS_PER_KILOBYTE(1024),
    OCTETS_PER_MEGABYTE(1024*1024),
    OCTETS_PER_GIGABYTE(1024*1024*1024);
    
    private final int units;
    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(UnitsTranslations.class);
    /**
     * This is the constructor of the enum. It will set the default value of
     * each enum item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param units the number of trasnslated units of the enum's item.
     * @since 2.0
     */
    private UnitsTranslations(int units) {
        translations = ResourceBundle.getBundle(AvailableBundles.UNITS_TRANSLATIONS.getPath());
        if (units >= Integer.MAX_VALUE) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        this.units = units;
    }

    /**
     * This method gets the units translation corresponding to the enum's item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the units corresponding to the enum's item.
     * @since 2.0
     */
    public int getUnits() {
        return units;
    }
}
