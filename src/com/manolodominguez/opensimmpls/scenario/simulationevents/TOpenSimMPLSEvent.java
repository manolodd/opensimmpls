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

import java.util.EventObject;

/**
 * This class is the superclass of all events generated in OpenSimMPLS. It is an
 * abstract class that has to be implemented by all subclassess.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TOpenSimMPLSEvent extends EventObject implements Comparable {

    /**
     * This is the constructor of the class that will be called by all
     * subclasses to create a new event in OpenSimMPLS.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param instant Every events includes the moment of their generation, in
     * nanoseconds. It allow syncronizing everything that is happening during a
     * simulation.
     * @param sender The object that generates the event.
     * @param eventID The unique event identifier.
     */
    public TOpenSimMPLSEvent(Object sender, long eventID, long instant) {
        super(sender);
        this.eventID = eventID;
        this.instant = instant;
    }

    /**
     * This method gets the instant in wich the event was generated.
     *
     * @return the instant in wich the event was generated.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public long getInstant() {
        return this.instant;
    }

    /**
     * This method gets the event unique identifier.
     *
     * @return the event unique identifier.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public long getEventID() {
        return this.eventID;
    }

    /**
     * This method sets the event unique identifier.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param eventID the event unique identifier.
     */
    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    /**
     * This method compares the current instance to another instance of
     * TOpenSimMPLSEvent to know the ordinal position of one to respect the
     * other.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherEvent a TOpenSimMPLSEvent instance to be compared to the
     * current one.
     * @return -1, 0 or 1 depending on whether the current instance is lesser,
     * equal or greater than the one specified as an argument.
     * @since 2.0
     */
    @Override
    public int compareTo(Object anotherEvent) {
        TOpenSimMPLSEvent eventAux = (TOpenSimMPLSEvent) anotherEvent;
        if (this.instant < eventAux.getInstant()) {
            return -1;
        } else if (this.instant > eventAux.getInstant()) {
            return 1;
        } else {
            if (getEventID() < eventAux.getEventID()) {
                return -1;
            } else if (getEventID() == eventAux.getEventID()) {
                return 0;
            }
            return 1;
        }
    }

    /**
     * This method gets the event type of this event. It will be one of the
     * constants defined in this class.
     *
     * @return the event type of this event.It will be one of the constants
     * defined in this class.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract int getType();

    public static final int STATISTICS = 0;
    public static final int SIMULATION = 1;
    public static final int TIMER = 2;
    public static final int PROGRESS = 3;

    private long eventID;
    private long instant;
}
