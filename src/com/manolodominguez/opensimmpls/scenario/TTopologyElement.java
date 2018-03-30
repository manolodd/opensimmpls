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

import com.manolodominguez.opensimmpls.scenario.simulationevents.ESimulationSingleSubscriber;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEvent;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventListener;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;

/**
 * This is an abstract class that is the super class of all elements of a
 * topology.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TTopologyElement implements ITimerEventListener, Runnable {

    /**
     * This is the constructor of the class. It will be called by subclasses
     * when creating a new instance.
     *
     * @param elementType The type of the element. One of the contants defined
     * in this class.
     * @param eventIdentifierGenerator The event identifier generator.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTopologyElement(int elementType, TLongIDGenerator eventIdentifierGenerator) {
        this.elementType = elementType;
        this.markForDeletionAsTimerEventListener = false;
        this.elementThread = null;
        this.simulationEventsListener = null;
        this.eventIdentifierGenerator = eventIdentifierGenerator;
        this.availableNanoseconds = 0;
        this.alive = true;
        this.wellConfigured = false;
        this.currentTimeInstant = 0;
        this.tickDurationInNs = 0;
    }

    /**
     * This method gets the current time instant. It is a representation of a
     * specific moment in the simulation. Mostly used to be included in each
     * event generated.
     *
     * @return the current time instant.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public long getCurrentTimeInstant() {
        return this.currentTimeInstant;
    }

    /**
     * This method sets the current time instant. It is a representation of a
     * specific moment in the simulation. Mostly used to be included in each
     * event generated.
     *
     * @param currentInstant the current time instant
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setCurrentTimeInstant(long currentInstant) {
        this.currentTimeInstant = currentInstant;
    }

    /**
     * This method gets the tick duration in nanoseconds.
     *
     * @return the tick duration in nanoseconds.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: Clarify the difference between tickDurationInNs and the available 
    // number of nanoseconds. 
    public int getTickDurationInNs() {
        return this.tickDurationInNs;
    }

    /**
     * This method sets the tick duration in nanoseconds.
     *
     * @param tickDurationInNs the tick duration in nanoseconds.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: Clarify the difference between tickDurationInNs and the available 
    // number of nanoseconds. 
    public void setTickDurationInNs(int tickDurationInNs) {
        this.tickDurationInNs = tickDurationInNs;
    }

    /**
     * This method marks the topology element to stop receiving timer event or
     * not.
     *
     * @param markForDeletionAsTimerEventListener TRUE, if the topology node has
     * to stop receiving timer events. On the contrary, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void markForDeletionAsTimerEventListener(boolean markForDeletionAsTimerEventListener) {
        this.markForDeletionAsTimerEventListener = markForDeletionAsTimerEventListener;
    }

    /**
     * This method checks whether the topology element has been marked to stop
     * receiving timer events or not.
     *
     * @return TRUE, if the topology element has been marked to stop receiving
     * timer events. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isMarkedForDeletionAsTimerEventListener() {
        return this.markForDeletionAsTimerEventListener;
    }

    /**
     * This method gets the element type of the topology element.
     *
     * @return the element type of the topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public int getElementType() {
        return this.elementType;
    }

    /**
     * This method sets the available number of nanoseconds of the topology
     * element (those nanoseconds that the topology element uses to work).
     *
     * @param availableNanoseconds the available number of nanoseconds of the
     * topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: this is not being used. Subclassess are accesing directly to the 
    // attribute.
    public void setAvailableNanoseconds(long availableNanoseconds) {
        this.availableNanoseconds = availableNanoseconds;
    }

    /**
     * This method gets the available number of nanoseconds of the topology
     * element (those nanoseconds that the topology element uses to work).
     *
     * @return the available number of nanoseconds of the topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: this is not being used. Subclassess are accesing directly to the 
    // attribute.
    public double getAvailableNanoseconds() {
        return this.availableNanoseconds;
    }

    /**
     * This method adds the specified number of nanoseconds to the avaliable
     * number of nanoseconds of the topology element.
     *
     * @param nanosecondsToAdd number of nanoseconds to add to the avaliable
     * number of nanoseconds of the topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: this is not being used. Subclassess are accesing directly to the 
    // attribute.
    public void addToAvailableNanoseconds(long nanosecondsToAdd) {
        this.availableNanoseconds += nanosecondsToAdd;
    }

    /**
     * This method subtracts the specified number of nanoseconds from the
     * avaliable number of nanoseconds of the topology element.
     *
     * @param nanosecondsToSubtract number of nanoseconds to subtract from the
     * avaliable number of nanoseconds of the topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: this is not being used. Subclassess are accesing directly to the 
    // attribute.
    public void subtractFromAvailableNanoseconds(long nanosecondsToSubtract) {
        if (this.availableNanoseconds < nanosecondsToSubtract) {
            this.availableNanoseconds = 0;
        } else {
            this.availableNanoseconds -= nanosecondsToSubtract;
        }
    }

    /**
     * This method starts the independent thread that governs the topology
     * element.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void startOperation() {
        if (elementThread == null) {
            elementThread = new Thread(this);
            this.elementThread.start();
        } else if (!elementThread.isAlive()) {
            elementThread = new Thread(this);
            this.elementThread.start();
        }
    }

    /**
     * This method is used to syncronize the thread of the topology element with
     * those of the rest of elements and with the main thread so that the
     * simulation can flow synchronously. It is called by the simulator's timer
     * that synchronizes the simulation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void waitForCompletion() {
        if (elementThread != null) {
            try {
                this.elementThread.join();
            } catch (Exception e) {
                System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TElementoTopologia.ErrorFinReloj") + e.toString());
            };
        }
    }

    /**
     * This methods sets the simulation events listener of this element. This
     * listener will collect the simulation of thi topology element.
     *
     * @param simulationEventsListener the simulation event listener that will
     * collect wimulation events generated by the topology element.
     * @throws ESimulationSingleSubscriber this exception is thrown when a
     * listener is being defined but there is already a listener for this
     * element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void addSimulationListener(TSimulationEventListener simulationEventsListener) throws ESimulationSingleSubscriber {
        if (this.simulationEventsListener == null) {
            this.simulationEventsListener = simulationEventsListener;
        } else {
            throw new ESimulationSingleSubscriber();
        }
    }

    /**
     * This method removes, if exists, the simulation event listener of this
     * topology element.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeSimulationEventListener() {
        this.simulationEventsListener = null;
    }

    /**
     * This method send a simulation event to the simulation event listener of
     * this topology element.
     *
     * @param simulationEvent simulation event to be sent to the simulation
     * event listener of this topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void generateSimulationEvent(TSimulationEvent simulationEvent) {
        if (this.simulationEventsListener != null) {
            this.simulationEventsListener.captureSimulationEvents(simulationEvent);
        }
    }

    /**
     * This is an abstract method that once implemented will check whether the
     * topology element is alive or not.
     *
     * @return TRUE, if the topology element is alive. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isAlive() {
        return this.alive;
    }

    /**
     * This is an abstract method that once implemented will set whether the
     * topology element is well configured or not.
     *
     * @param wellConfigured TRUE, if the element is well configured. Otherwise,
     * FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setWellConfigured(boolean wellConfigured) {
        this.wellConfigured = wellConfigured;
    }

    /**
     * This is an abstract method that once implemented will receive a timer
     * event from the global timer.
     *
     * @param timerEvent a timer event from the global timer.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public abstract void receiveTimerEvent(TTimerEvent timerEvent);

    /**
     * This is an abstract method that once implemented will start the thread of
     * the topology element.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public abstract void run();

    /**
     * This is an abstract method that once implemented will check whether the
     * topology element is well configured or not.
     *
     * @return TRUE, if the topology element is well configured. Otherwise,
     * FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract boolean isWellConfigured();

    /**
     * This is an abstract method that once implemented will generate a error
     * string message corresponding to the error code specified as an argument.
     *
     * @param errorCode error code that will be used to generate the error
     * string.
     * @return Human readable error string corresponding to the error code
     * specified.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract String getErrorMessage(int errorCode);

    /**
     * This is an abstract method that once implemented will generate a
     * serialized string that represent all configuration values of this
     * topology element.
     *
     * @return a serialized string that represent all configuration values of
     * this topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract String marshall();

    /**
     * This is an abstract method that once implemented will configure the
     * topology decoding the serialized string specified as an argument that
     * will contain the required values.
     *
     * @param serializedElement serialized string that will contain the required
     * values to configure the topology element.
     * @return TRUE, if the serialized string is correct and has the needed
     * values to configure the topology element.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract boolean unMarshall(String serializedElement);

    /**
     * This is an abstract method that once implemented will reset all the
     * values of class attributes as if the instance was recently created.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract void reset();

    public static final int LINK = 0;
    public static final int NODE = 1;

    private int elementType;
    private boolean markForDeletionAsTimerEventListener;
    private Thread elementThread;
    public TSimulationEventListener simulationEventsListener;
    public TLongIDGenerator eventIdentifierGenerator;
    // FIX: put this attribute as private, not protected.
    protected double availableNanoseconds;
    private boolean alive;
    protected boolean wellConfigured;
    private long currentTimeInstant;
    private int tickDurationInNs;
}
