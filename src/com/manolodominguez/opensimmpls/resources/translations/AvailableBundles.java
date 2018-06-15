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
package com.manolodominguez.opensimmpls.resources.translations;

/**
 * This enum is used to access bundles from a centralilzed point. This easies
 * accessing the bundles from classes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public enum AvailableBundles {
    E_SIMULATION_SINGLE_SUBSCRIBER("com/manolodominguez/opensimmpls/resources/translations/translations"),
    LER_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    LSR_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    ACTIVE_LER_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    LICENSE_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    LINK_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    TRAFFIC_GENERATOR_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    TRAFFIC_SINK_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    ACTIVE_LSR_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    WARNING_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    DECISSION_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    ERROR_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    ABOUT_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    COMMENT_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    OPENSIMMPLS_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    SIMULATION_PANEL("com/manolodominguez/opensimmpls/resources/translations/translations"),
    SCENARIO_WINDOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    SPLASH("com/manolodominguez/opensimmpls/resources/translations/translations"),
    OSMFILTER("com/manolodominguez/opensimmpls/resources/translations/translations"),
    IMAGE_BROKER("com/manolodominguez/opensimmpls/resources/translations/translations"),
    E_ID_GENERATOR_OVERFLOW("com/manolodominguez/opensimmpls/resources/translations/translations"),
    E_IPV4_ADDRESS_GENERATOR_OVERFLOW("com/manolodominguez/opensimmpls/resources/translations/translations");

    private final String bundlePath;

    /**
     * This is the constructor of the enum. It will set the default value of
     * each enum item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param bundlePath the path of the enum's item.
     * @since 2.0
     */
    private AvailableBundles(String bundlePath) {
        this.bundlePath = bundlePath;
    }

    /**
     * This method gets the path corresponding to the enum's item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the path corresponding to the enum's item.
     * @since 2.0
     */
    public String getPath() {
        return this.bundlePath;
    }
}
