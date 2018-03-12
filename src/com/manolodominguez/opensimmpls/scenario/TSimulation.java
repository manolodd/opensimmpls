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
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventCollector;

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
        this.simulationEventCollector = new TSimulationEventCollector();
        // FIX: Use class constants instead of harcoded values.
        this.simulationLength = 500;
        this.simulationStepLength = 1;
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
        this.simulationEventCollector.setSimulationPanel(simulationPanel);
    }

    /**
     * This method will reset the attributes of the instance as in the moment of
     * its creation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.simulationEventCollector.reset();
    }

    /**
     * Este m�todo permite establecer la duraci�n total de la simulaci�n.
     *
     * @param simulationLength Duraci�n total de la simulaci�n en nanosegundos.
     * @since 2.0
     */
    public void setSimulationLength(long simulationLength) {
        this.simulationLength = simulationLength;
    }

    /**
     * Este m�todo permite establecer la duraci�n del paso de simulaci�n.
     *
     * @param simulationStepLength Duraci�n del paso de simulaci�n en
     * nanosegundos.
     * @since 2.0
     */
    public void setSimulationStepLength(long simulationStepLength) {
        this.simulationStepLength = simulationStepLength;
    }

    /**
     * Este m�todo permite obtener la duraci�n total de la simulaci�n-
     *
     * @return Duraci�n total de la simulaci�n, en nanosegundos.
     * @since 2.0
     */
    public long getSimulationLength() {
        return this.simulationLength;
    }

    /**
     * Este m�todo permite obtener la duraci�n del paso de simulaci�n.
     *
     * @return Duraci�n del paso de simulaci�n en nanosegundos.
     * @since 2.0
     */
    public long getSimulationStepLength() {
        return this.simulationStepLength;
    }

    /**
     * Este m�todo serializa la instancia, convirti�ndola en un texto que se
     * puede almacenar en disco.
     *
     * @return Un texto que representa a la instancia actual.
     * @since 2.0
     */
    public String marshallTimeParameters() {
        String serializedTimeParameters = "#Temporizacion#";
        serializedTimeParameters += this.simulationLength + "#";
        serializedTimeParameters += this.simulationStepLength + "#";
        return serializedTimeParameters;
    }

    /**
     * Este m�todo deserializa un objeto TSimulation previamente serializado,
     * reconstruy�ndolo en memoria.
     *
     * @param serializedTimeParameters El objeto de tipo TSimulaci�n
     * serializado.
     * @return TRUE, si se ha conseguido serializar correctamente. FALSE en caso
     * contrario.
     * @since 2.0
     */
    public boolean unmarshallTimeParameters(String serializedTimeParameters) {
        String[] timeParametersFields = serializedTimeParameters.split("#");
        if (timeParametersFields.length != 4) {
            return false;
        }
        this.simulationLength = Integer.valueOf(timeParametersFields[2]).longValue();
        this.simulationStepLength = Integer.valueOf(timeParametersFields[3]).longValue();
        return true;
    }

    /**
     * Este m�todo permite acceder directamente al recolector de eventos de
     * simulaci�n.
     *
     * @return Recolector de eventos de simulaci�n el escenario.
     * @since 2.0
     */
    public TSimulationEventCollector simulationEventCollector() {
        return this.simulationEventCollector;
    }

    private long simulationLength;
    private long simulationStepLength;

    private TScenario parentScenario;
    private TSimulationEventCollector simulationEventCollector;
}
