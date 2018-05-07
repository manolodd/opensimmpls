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

import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.scenario.TTopologyElement;

/**
 * This class implements a simulation event that is generated when a new LSP is
 * removed.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSimulationEventLSPRemoved extends TSimulationEvent {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TSimulationEventLSPERemoved.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param source The object that generates the event.
     * @param eventID The event unique identifier.
     * @param timeInstant The time instant when the event was generated.
     * @since 2.0
     */
    public TSimulationEventLSPRemoved(Object source, long eventID, long timeInstant) {
        super(source, eventID, timeInstant);
    }

    /**
     * This method gets the subtype of this simulation event. One of the
     * constants of TSimulationEvent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the subtype of this simulation event.
     * TSimulationEvent.LSP_REMOVED.
     * @since 2.0
     */
    @Override
    public int getSubtype() {
        return TSimulationEvent.LSP_REMOVED;
    }

    /**
     * This method gets the name of the topology element that generated the
     * event.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the name of the topology element that generated the event.
     * @since 2.0
     */
    public String getSourceName() {
        TTopologyElement topologyElement = null;
        TLink link = null;
        TNode node = null;
        topologyElement = super.getSource();
        if (topologyElement.getElementType() == TTopologyElement.LINK) {
            link = (TLink) topologyElement;
            return link.getName();
        } else if (topologyElement.getElementType() == TTopologyElement.NODE) {
            node = (TNode) topologyElement;
            return node.getName();
        }
        return ("");
    }

    /**
     * This method gets the type of the topology element that generated the
     * event, as a String.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the type of the topology element that generated the event, as a
     * String.
     * @since 2.0
     */
    public String getSourceTypeAsString() {
        TTopologyElement topologyElement = null;
        topologyElement = super.getSource();
        if (topologyElement.getElementType() == TTopologyElement.LINK) {
            // FIX: i18N needed
            return ("Enlace ");
        } else if (topologyElement.getElementType() == TTopologyElement.NODE) {
            // FIX: i18N needed
            return ("Nodo ");
        }
        return ("");
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
        string += "ha desactivado un LSP";
        return (string);
    }
}
