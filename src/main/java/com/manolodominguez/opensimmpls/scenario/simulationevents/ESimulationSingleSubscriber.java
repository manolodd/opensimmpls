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
package com.manolodominguez.opensimmpls.scenario.simulationevents;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.ResourceBundle;

/**
 * This class implements an exception that will be thrown when more than a
 * single listener is subscribed to the progress event generator.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class ESimulationSingleSubscriber extends Exception {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of ESimulationSingleSubscriber.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public ESimulationSingleSubscriber() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.E_SIMULATION_SINGLE_SUBSCRIBER.getPath());
    }

    /**
     * This method return a localized message explaining the cause of this
     * exception.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a localized message explaining the cause of this exception.
     * @since 2.0
     */
    @Override
    public String toString() {
        return (this.translations.getString("ESimulacionUnSoloSuscriptor.toString"));
    }

    private ResourceBundle translations;
   
}
