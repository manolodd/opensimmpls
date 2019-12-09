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
    T_TRAFFIC_SINK_NODE("translations"),
    T_TRAFFIC_GENERATOR_NODE("translations"),
    T_TOPOLOGY_ELEMENT("translations"),
    SCENARIO("translations"),
    T_LINK_CONFIG("translations"),
    T_LSR_NODE("translations"),
    T_LER_NODE("translations"),
    T_ACTIVE_LSR_NODE("translations"),
    T_ACTIVE_LER_NODE("translations"),
    MAIN_OPENSIMMPLS("translations"),
    T_TIMER("translations"),
    E_SIMULATION_SINGLE_SUBSCRIBER("translations"),
    E_PROGRESS_EVENT_GENERATOR_ONLY_ALLOW_A_SINGLE_LISTENER("translations"),
    LER_WINDOW("translations"),
    LSR_WINDOW("translations"),
    ACTIVE_LER_WINDOW("translations"),
    LICENSE_WINDOW("translations"),
    LINK_WINDOW("translations"),
    TRAFFIC_GENERATOR_WINDOW("translations"),
    TRAFFIC_SINK_WINDOW("translations"),
    ACTIVE_LSR_WINDOW("translations"),
    WARNING_WINDOW("translations"),
    DECISSION_WINDOW("translations"),
    ERROR_WINDOW("translations"),
    ABOUT_WINDOW("translations"),
    COMMENT_WINDOW("translations"),
    OPENSIMMPLS_WINDOW("translations"),
    SIMULATION_PANEL("translations"),
    SCENARIO_WINDOW("translations"),
    SPLASH("translations"),
    T_OSM_FILTER("translations"),
    T_IMAGE_BROKER("translations"),
    E_ID_GENERATOR_OVERFLOW("eidgeneratoroverflow"),
    E_IPV4_ADDRESS_GENERATOR_OVERFLOW("eipv4addressgeneratoroverflow"),
    T_OSM_SAVER("osmsaver"),
    T_ID_GENERATOR("tidgenerator"),
    T_DMGP_FLOW_ENTRI("tdmgpflowentry"),
    T_ROTARY_ID_GENERATOR("trotaryidgenerator"),
    T_LONG_ID_GENERATOR("tlongidgenerator"),
    T_IPV4_ADDRESS_GENERATOR("tipv4addressgenerator"),
    UNITS_TRANSLATIONS("unitstranslations"),
    T_OSM_LOADER("osmloader");

    private final String bundlePath;
    private static final String BASE_PATH = "com/manolodominguez/opensimmpls/resources/translations/";

    /**
     * This is the constructor of the enum. It will set the default value of
     * each enum item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param bundle the path of the enum's item.
     * @since 2.0
     */
    private AvailableBundles(String bundle) {
        this.bundlePath = BASE_PATH+bundle;
    }

    /**
     * This method gets the path corresponding to the enum's item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the path corresponding to the enum's item.
     * @since 2.0
     */
    public String getPath() {
        return bundlePath;
    }
}
