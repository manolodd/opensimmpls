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

/**
 * This class implements a simulation event that is generated when a MPLS label
 * is received.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSimulationEventLabelReceived extends TSimulationEvent {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TSimulationEventLabelDenied.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param source The object that generates the event.
     * @param eventID The event unique identifier.
     * @param timeInstant The time instant when the event was generated.
     * @since 2.0
     */
    public TSimulationEventLabelReceived(Object source, long eventID, long timeInstant) {
        super(source, eventID, timeInstant);
    }

    /**
     * This method gets the subtype of this simulation event. One of the
     * constants of TSimulationEvent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the subtype of this simulation event.
     * TSimulationEvent.LABEL_RECEIVED.
     * @since 2.0
     */ 
    @Override
    public int getSubtype() {
        return TSimulationEvent.LABEL_RECEIVED;
    }

    /**
     * This method gets a human readable explanation of this event.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a human readable explanation of this event.
     * @since 2.0
     */
    @Override
    public String toString() {
        String string = "";
        string += "[";
        string += this.getSourceTypeAsString();
        string += " ";
        string += this.getSourceName();
        string += "] ";
        // FIX: i18N needed
        string += "ha recibido una etiqueta";
        return(string);
    }
}
