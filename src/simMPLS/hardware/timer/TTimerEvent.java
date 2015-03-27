/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package simMPLS.hardware.timer;

import simMPLS.utils.TOpenSimMPLSEvent;

/**
 * This class implements a timer event that will be used to govern and
 * synchronize all elements that compose the simulation.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TTimerEvent extends TOpenSimMPLSEvent {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TTimerEvent.
     *
     * @param eventID The unique event identifier.
     * @param eventGenerator The object that generates the event.
     * @param lowerEndOfInterval The start of the period of time (simulation
     * step) carried out by this event.
     * @param upperEndOfInterval The end of the period of time (simulation step)
     * carried out by this event.
     * @since 1.0
     */
    public TTimerEvent(Object eventGenerator, long eventID, TTimestamp lowerEndOfInterval, TTimestamp upperEndOfInterval) {
        super(eventGenerator, eventID, upperEndOfInterval.getTotalAsNanoseconds());
        this.lowerEndOfInterval = lowerEndOfInterval;
        this.upperEndOfInterval = upperEndOfInterval;
    }

    /**
     * This method return the duration of the simulation step in nanoseconds.
     * That is, the difference between the end of the interval and the start of
     * the interval carried out by the event.
     *
     * @return The duration of the simulation step in nanoseconds, according to
     * the values included in this event.
     * @since 1.0
     */
    public int getStepDuration() {
        return (int) ((this.upperEndOfInterval.getTotalAsNanoseconds()) - (this.lowerEndOfInterval.getTotalAsNanoseconds()));
    }

    /**
     * This method get the end of the interval according to the values included
     * in the event, in nanoseconds.
     *
     * @return The end of the interval according to the values included in the
     * event, in nanoseconds.
     * @since 1.0
     */
    public long getUpperLimit() {
        return (long) (this.upperEndOfInterval.getTotalAsNanoseconds());
    }

    /**
     * This method return the type of this event. It is one of the constants
     * defined in TOpenSimMPLSEvent.
     *
     * @return The type of this event. It is one of the constants defined in
     * TOpenSimMPLSEvent.
     * @since 1.0
     */
    @Override
    public int getType() {
        return TOpenSimMPLSEvent.TIMER;
    }

    private final TTimestamp lowerEndOfInterval;
    private final TTimestamp upperEndOfInterval;
}
