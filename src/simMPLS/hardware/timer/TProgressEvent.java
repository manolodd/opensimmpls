/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.hardware.timer;

import simMPLS.utils.TOpenSimMPLSEvent;

/**
 * This class implements a progress event that will allow knowing the state of
 * the simulation when it is running.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    @Override
    public int getType() {
        return TOpenSimMPLSEvent.PROGRESS;
    }

    private final int progressPercentage;
}
