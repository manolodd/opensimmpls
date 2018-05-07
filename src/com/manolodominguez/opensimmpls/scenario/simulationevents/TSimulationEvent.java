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

import com.manolodominguez.opensimmpls.scenario.TTopologyElement;

/**
 * This class implements a simulation event.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TSimulationEvent extends TOpenSimMPLSEvent {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TSimulationEvent once implemented and called by subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param source The object that generates the event.
     * @param eventID The event unique identifier.
     * @param timeInstant The time instant when the event was generated.
     * @since 2.0
     */
    public TSimulationEvent(Object source, long eventID, long timeInstant) {
        super(source, eventID, timeInstant);
    }

    /**
     * This method gets the type of this OpenSimMPLS event. Once of the
     * constants of TOpenSimMPLSEvent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the type of this simulation event. Once of the constants of
     * TOpenSimMPLSEvent.
     * @since 2.0
     */
    @Override
    public int getType() {
        return TOpenSimMPLSEvent.SIMULATION;
    }

    /**
     * This method, once implemented, will get the subtype of this simulation
     * event. Once of the constants of this class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the subtype of this simulation event. Once of the constants of
     * this class.
     * @since 2.0
     */
    public abstract int getSubtype();

    /**
     * This method, returns the topology element that generates the event.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the topology element that generates the event.
     * @since 2.0
     */
    @Override
    public TTopologyElement getSource() {
        return (TTopologyElement) super.source;
    }

    public static final int PACKET_GENERATED = 0;
    public static final int PACKET_SENT = 1;
    public static final int PACKET_RECEIVED = 2;
    public static final int PACKET_SWITCHED = 3;
    public static final int PACKET_DISCARDED = 4;
    public static final int PACKET_ON_FLY = 5;
    public static final int PACKET_STORED_IN_DMGPALMACENADO_DMGP = 6;
    public static final int PACKET_REMOVED_FROM_DMGP = 7;
    public static final int PACKET_FOUND_IN_DMGP = 8;
    public static final int PACKET_NOT_FOUND_IN_DMGP = 9;
    public static final int PACKET_RETRANSMITTED = 10;
    public static final int LINK_OVERLOADED = 11;
    public static final int LINK_BROKEN = 12;
    public static final int LINK_RECOVERED = 13;
    public static final int NODE_CONGESTED = 14;
    public static final int LABEL_REQUESTED = 15;
    public static final int LABEL_RECEIVED = 16;
    public static final int LABEL_ASSIGNED = 17;
    public static final int LABEL_DENIED = 18;
    public static final int LABEL_REMOVED = 19;
    public static final int LSP_ESTABLISHED = 20;
    public static final int LSP_NOT_ESTABLISHED = 21;
    public static final int LSP_REMOVED = 22;
    public static final int BACKUP_LSP_ESTABLISHED = 23;
    public static final int BACKUP_LSP_NOT_ESTABLILSHED = 24;
    public static final int BACKUP_LSP_REMOVED = 25;
    public static final int BACKUP_LSP_ACTIVATED = 26;
    public static final int RETRANSMISSION_REQUESTED = 26;
    public static final int RETRANSMISION_RESPONSED_OK = 27;
    public static final int RETRANSMISSION_RESPONSED_DENIED = 28;
    public static final int RETRANSMISSION_RECEIVED = 29;
    public static final int PACKET_ROUTED = 30;
}
