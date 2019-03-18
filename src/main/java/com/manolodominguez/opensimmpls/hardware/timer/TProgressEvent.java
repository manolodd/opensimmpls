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
package com.manolodominguez.opensimmpls.hardware.timer;

import com.manolodominguez.opensimmpls.scenario.simulationevents.TOpenSimMPLSEvent;

/**
 * This class implements a progress event that will allow knowing the state of
 * the simulation when it is running.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TProgressEvent extends TOpenSimMPLSEvent {

    /**
     * This method is the constrctor of the class. It creates a new instance of
     * TProgressEvent.
     *
     * @param eventID The unique event identifier.
     * @param eventGenerator The object that generates the event.
     * @param progressPercentage The progress percentage that the event is
     * carrying out and will be received by the listener.
     * @since 2.0
     */
    public TProgressEvent(Object eventGenerator, long eventID, int progressPercentage) {
        super(eventGenerator, eventID, 0);
        this.progressPercentage = progressPercentage;
    }

    /**
     * This method returns the progress percentage advertised by this event.
     *
     * @return The progress percentage that the event is carrying out and will
     * be received by the listener.
     * @since 2.0
     */
    public int getProgressPercentage() {
        return this.progressPercentage;
    }

    /**
     * This method return the type of this event. It is one of the constants
     * defined in TOpenSimMPLSEvent.
     *
     * @return The type of this event. It is one of the constants defined in
     * TOpenSimMPLSEvent.
     * @since 2.0
     */
    @Override
    public int getType() {
        return TOpenSimMPLSEvent.PROGRESS;
    }

    private final int progressPercentage;
}
