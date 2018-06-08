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
package com.manolodominguez.opensimmpls.scenario;

import com.manolodominguez.opensimmpls.ui.simulator.JSimulationPanel;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventListener;

/**
 * This class implements a container of simulation events that also link the
 * parent scenario (source of these events) and the corresponding visual
 * representation of the simulation.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSimulation {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TSimulation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param scenario the parent scenario. The one from wich all simulation
     * events will come from.
     * @since 2.0
     */
    public TSimulation(TScenario scenario) {
        this.parentScenario = scenario;
        this.simulationEventListener = new TSimulationEventListener();
        // FIX: Use class constants instead of harcoded values.
        this.simulationLengthInNs = 500;
        this.simulationTickDurationInNs = 1;
    }

    /**
     * This method sets the simulation panel that will be the place where visual
     * representation of simulation events will happen.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param simulationPanel the simulation panel that will be the place where
     * visual representation of simulation events will happen.
     * @since 2.0
     */
    public void setSimulationPanel(JSimulationPanel simulationPanel) {
        this.simulationEventListener.setSimulationPanel(simulationPanel);
    }

    /**
     * This method will reset the attributes of the instance as in the moment of
     * its creation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.simulationEventListener.reset();
    }

    /**
     * This method sets the total simulation length in nanoseconds.
     *
     * @param simulationLengthInNs the total simulation length in nanoseconds.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setSimulationLengthInNs(long simulationLengthInNs) {
        this.simulationLengthInNs = simulationLengthInNs;
    }

    /**
     * This method sets the simulation step length in nanoseconds.
     *
     * @param simulationStepLengthInNs the simulation step length in
     * nanoseconds.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setSimulationTickDurationInNs(long simulationStepLengthInNs) {
        this.simulationTickDurationInNs = simulationStepLengthInNs;
    }

    /**
     * This method gets the simulation total length in nanoseconds.
     *
     * @return the simulation total length in nanoseconds.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public long getSimulationLengthInNs() {
        return this.simulationLengthInNs;
    }

    /**
     * This method gets the simulation step total length in nanoseconds.
     *
     * @return the simulation step total length in nanoseconds.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public long getSimulationTickDurationInNs() {
        return this.simulationTickDurationInNs;
    }

    /**
     * This method creates a serialized string containing the configuration
     * values of every attributes of the instance.
     *
     * @return a serialized string containing the configuration values of every
     * attributes of the instance.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String marshallTimeParameters() {
        String serializedTimeParameters = "#Temporizacion#";
        serializedTimeParameters += this.simulationLengthInNs + "#";
        serializedTimeParameters += this.simulationTickDurationInNs + "#";
        return serializedTimeParameters;
    }

    /**
     * This method configure this instance from the configuration values
     * contained in a serialized string.
     *
     * @param serializedTimeParameters The string containing the configuration
     * values for this instance.
     * @return TRUE, if the serialized string is correct. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean unmarshallTimeParameters(String serializedTimeParameters) {
        String[] timeParametersFields = serializedTimeParameters.split("#");
        // FIX: Do not use harcoded values. Use class constants instead.
        if (timeParametersFields.length != 4) {
            return false;
        }
        this.simulationLengthInNs = Integer.valueOf(timeParametersFields[2]).longValue();
        this.simulationTickDurationInNs = Integer.valueOf(timeParametersFields[3]).longValue();
        return true;
    }

    /**
     * This methods gets the simulation event collector that contains the
     * simulation events tha have happened.
     *
     * @return the simulation event collector that contains the simulation
     * events tha have happened.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TSimulationEventListener getSimulationEventListener() {
        return this.simulationEventListener;
    }

    private long simulationLengthInNs;
    private long simulationTickDurationInNs;

    private TScenario parentScenario;
    private TSimulationEventListener simulationEventListener;
}
