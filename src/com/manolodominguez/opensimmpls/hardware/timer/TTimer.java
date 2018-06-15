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

import java.util.Iterator;
import java.util.TreeSet;
import com.manolodominguez.opensimmpls.scenario.TExternalLink;
import com.manolodominguez.opensimmpls.scenario.TInternalLink;
import com.manolodominguez.opensimmpls.scenario.TActiveLERNode;
import com.manolodominguez.opensimmpls.scenario.TLERNode;
import com.manolodominguez.opensimmpls.scenario.TActiveLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.scenario.TTrafficSinkNode;
import com.manolodominguez.opensimmpls.scenario.TTrafficGeneratorNode;
import com.manolodominguez.opensimmpls.scenario.TTopologyElement;
import com.manolodominguez.opensimmpls.utils.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.ui.utils.TProgressEventListener;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;

/**
 * This class implements a timer that will govern the operation and
 * synchronization of the simulation. It will send time events to the rest of
 * component that compose the topology.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTimer implements Runnable {

    /**
     * This method is the constuctor of the class. It will create a new instance
     * of TTimer and will set the initial values for the attributes of the
     * class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTimer() {
        this.thread = null;
        this.timerEventListenerNodes = new TreeSet();
        this.timerEventListenerLInks = new TreeSet();
        this.progressEventListener = null;
        this.longIdentifierGenerator = new TLongIDGenerator();
        this.currentTimestamp = new TTimestamp(0, 0);
        this.previousTimestamp = new TTimestamp(0, 0);
        this.finishTimestamp = new TTimestamp(0, 100000);
        this.currentTimestampAux = new TTimestamp(0, 0);
        this.previousTimestampAux = new TTimestamp(0, 0);
        this.finishTimestampAux = new TTimestamp(0, 100000);
        this.tick = 1000;
        this.running = false;
        this.isFinished = true;
        this.paused = false;
    }

    /**
     * This method reset the attributes of the class as when created by the
     * constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.currentTimestamp = new TTimestamp(0, 0);
        this.previousTimestamp = new TTimestamp(0, 0);
        this.running = false;
        this.longIdentifierGenerator.reset();
        this.isFinished = true;
        this.paused = false;
        generateProgressEvent();
    }

    /**
     * This method allows establishing the end of the simulation. When the timer
     * reaches this limit the simulation stops and no more events are generated.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param finishTimestamp is the time limit for the simulation. In fact it
     * it the timestamp the timer has to reach to finish the simulation.
     * @since 2.0
     */
    public void setFinishTimestamp(TTimestamp finishTimestamp) {
        this.finishTimestamp.setMillisecond(finishTimestamp.getMillisecond());
        this.finishTimestamp.setNanosecond(finishTimestamp.getNanosecond());
    }

    /**
     * This method establishes the granularity of the simulation, that is, it
     * tells the timmer how often it has to generate a timer event to let the
     * topology componets advance the simulation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param tick the period the timer has to wait between generating a timer
     * event and the next one.
     */
    public void setTick(int tick) {
        this.tick = tick;
    }

    /**
     * This method let a topology element (nodes and/or links) to subscribe the
     * timer to receive timer events.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param timerEventListener the element that wants to receive timer events.
     * @since 2.0
     */
    public void addTimerEventListener(TTopologyElement timerEventListener) {
        if (timerEventListener.getElementType() == TTopologyElement.LINK) {
            this.timerEventListenerLInks.add(timerEventListener);
        } else {
            this.timerEventListenerNodes.add(timerEventListener);
        }
    }

    /**
     * This method removes a timer event listener from the list of timer event
     * listeners. In this way, the listener will stop receiving timer events..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param timerEventListener The topology element that are going to stop
     * receiving timer events from this timer.
     * @since 2.0
     */
    public void removeTimerEventListener(TTopologyElement timerEventListener) {
        if (timerEventListener.getElementType() == TTopologyElement.LINK) {
            Iterator iterator = this.timerEventListenerLInks.iterator();
            TLink linkAux;
            TLink timerEventListenerAux = (TLink) timerEventListener;
            while (iterator.hasNext()) {
                linkAux = (TLink) iterator.next();
                if (linkAux.getID() == timerEventListenerAux.getID()) {
                    iterator.remove();
                }
            }
        } else {
            Iterator iterator = this.timerEventListenerNodes.iterator();
            TNode nodeAux;
            TNode timerEventListenerAux = (TNode) timerEventListener;
            while (iterator.hasNext()) {
                nodeAux = (TNode) iterator.next();
                if (nodeAux.getNodeID() == timerEventListenerAux.getNodeID()) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * This method removes from the list of time event listeners those that are
     * checked off to be removed as timer event listeners.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void purgeTimerEventListenersMarkedForDeletion() {
        Iterator linksIterator = this.timerEventListenerLInks.iterator();
        TLink linkAux;
        while (linksIterator.hasNext()) {
            linkAux = (TLink) linksIterator.next();
            if (linkAux.isMarkedForDeletionAsTimerEventListener()) {
                linksIterator.remove();
            }
        }
        Iterator nodesIterator = this.timerEventListenerNodes.iterator();
        TNode nodeAux;
        while (nodesIterator.hasNext()) {
            nodeAux = (TNode) nodesIterator.next();
            if (nodeAux.isMarkedForDeletionAsTimerEventListener()) {
                nodesIterator.remove();
            }
        }
    }

    /**
     * This method subscribe a progress event listener to thi timer. In this
     * way, the overall simulation progess will be known by this listener.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param progressEventListener The progress event listener that are going
     * to receive progress events from this timer.
     * @throws EProgressEventGeneratorOnlyAllowASingleListener It is possible to
     * subscribe only one progress event listener at the same time. So, if you
     * try to subscribe another one whe n there is a previous progress event
     * listener subscribed to the timer, this ixception is thrown.
     * @since 2.0
     */
    public void addProgressEventListener(TProgressEventListener progressEventListener) throws EProgressEventGeneratorOnlyAllowASingleListener {
        if (this.progressEventListener == null) {
            this.progressEventListener = progressEventListener;
        } else {
            throw new EProgressEventGeneratorOnlyAllowASingleListener();
        }
    }

    /**
     * This method unsubscribe the progress event listener from the timer. So,
     * this progress event listener will stop receiving progress events.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void removeProgressEventListener() {
        this.progressEventListener = null;
    }

    /**
     * This method generates a new timer event an sends it to all topology
     * elements that are subscribed to receive timer events.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void generateTimerEvent() {
        Iterator linksIterator = this.timerEventListenerLInks.iterator();
        Iterator nodesIterator = this.timerEventListenerNodes.iterator();
        TNode nodeAux;
        TLink linkAux;
        TTimestamp startOfSimulationInterval = new TTimestamp(this.previousTimestamp.getMillisecond(), this.previousTimestamp.getNanosecond());
        TTimestamp endOfSimulationInterval = new TTimestamp(this.currentTimestamp.getMillisecond(), this.currentTimestamp.getNanosecond());
        while (nodesIterator.hasNext()) {
            nodeAux = (TNode) nodesIterator.next();
            switch (nodeAux.getNodeType()) {
                case TNode.TRAFFIC_GENERATOR: {
                    nodeAux = (TTrafficGeneratorNode) nodeAux;
                    break;
                }
                case TNode.LER: {
                    nodeAux = (TLERNode) nodeAux;
                    break;
                }
                case TNode.ACTIVE_LER: {
                    nodeAux = (TActiveLERNode) nodeAux;
                    break;
                }
                case TNode.LSR: {
                    nodeAux = (TLSRNode) nodeAux;
                    break;
                }
                case TNode.ACTIVE_LSR: {
                    nodeAux = (TActiveLSRNode) nodeAux;
                    break;
                }
                case TNode.TRAFFIC_SINK: {
                    nodeAux = (TTrafficSinkNode) nodeAux;
                    break;
                }
            }
            try {
                TTimerEvent timerEvent = new TTimerEvent(this, this.longIdentifierGenerator.getNextIdentifier(), startOfSimulationInterval, endOfSimulationInterval);
                nodeAux.receiveTimerEvent(timerEvent);
            } catch (EIDGeneratorOverflow e) {
                e.printStackTrace();
            }
        }
        while (linksIterator.hasNext()) {
            linkAux = (TLink) linksIterator.next();
            switch (linkAux.getLinkType()) {
                case TLink.EXTERNAL_LINK: {
                    linkAux = (TExternalLink) linkAux;
                    break;
                }
                case TLink.INTERNAL_LINK: {
                    linkAux = (TInternalLink) linkAux;
                    break;
                }
            }
            try {
                linkAux.receiveTimerEvent(new TTimerEvent(this, this.longIdentifierGenerator.getNextIdentifier(), startOfSimulationInterval, endOfSimulationInterval));
            } catch (EIDGeneratorOverflow e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method generates a new progress event an sends it to the only one
     * listener that are subscribed to receive it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void generateProgressEvent() {
        int computedProgress = 0;
        long simulationDuration = this.finishTimestamp.getTotalAsNanoseconds();
        long currentTime = this.currentTimestamp.getTotalAsNanoseconds();
        if (simulationDuration != 0) {
            computedProgress = (int) Math.round((currentTime * 100) / simulationDuration);
        }
        try {
            if (this.progressEventListener != null) {
                this.progressEventListener.receiveProgressEvent(new TProgressEvent(this, this.longIdentifierGenerator.getNextIdentifier(), computedProgress));
            }
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
    }

    /**
     * This method restart the timer operation after it was previously paused.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void restart() {
        if (this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
        } else {
            if (!this.thread.isAlive()) {
                this.thread = new Thread(this);
                this.thread.start();
            }
        }
    }

    /**
     * This method starts the timer operation and, therefore, the planned
     * simulation will start. This method is a synchronized one.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void start() {
        if (this.thread == null) {
            reset();
            this.thread = new Thread(this);
            this.thread.start();
        } else {
            if (!this.thread.isAlive()) {
                reset();
                this.thread = new Thread(this);
                this.thread.start();
            }
        }
    }

    /**
     * This method will pause or continue/restart the operation of the timer
     * and, therefore, the simulation itself.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param pause TRUE, if the timer has to be paused. FALSE if the timer has
     * to be restarted.
     * @since 2.0
     */
    public void setPaused(boolean pause) {
        if ((this.paused) && (pause)) {
            // Do nothing. The timer is already paused.
        } else if ((this.paused) && (!pause)) {
            this.currentTimestamp.setTimestamp(this.currentTimestampAux);
            this.previousTimestamp.setTimestamp(this.previousTimestampAux);
            this.finishTimestamp.setTimestamp(this.finishTimestampAux);
            this.paused = pause;
            restart();
        } else if ((!this.paused) && (pause)) {
            this.currentTimestampAux.setTimestamp(this.currentTimestamp);
            this.previousTimestampAux.setTimestamp(this.previousTimestamp);
            this.finishTimestampAux.setTimestamp(this.finishTimestamp);
            this.paused = pause;
            this.isFinished = true;
        } else if ((!this.paused) && (!pause)) {
            // Do nothing. The timer is already running.
        }
    }

    /**
     * This method checks whether the timer is currently running or paused.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return TRUE, if the timmer is currently paused. Otherwise, return FALSE.
     */
    public boolean isPaused() {
        return this.paused;
    }

    /**
     * If the thread of this timer is alive and operative, this method will
     * simulate and synchronize all topology elements continuously. This method
     * orchestrate everything by sending timer events to al elements an wait
     * until they finishing using this event to simulate whatever.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void run() {
        this.running = true;
        long currentSimulatedTime;
        long previousSimulatedTime;
        long simulationDuration;
        boolean simulationFinished = false;
        this.isFinished = false;
        this.currentTimestamp.increaseNanoseconds(this.tick);
        currentSimulatedTime = this.currentTimestamp.getTotalAsNanoseconds();
        simulationDuration = this.finishTimestamp.getTotalAsNanoseconds();
        if (currentSimulatedTime == simulationDuration) {
            simulationFinished = true;
        }
        while ((this.currentTimestamp.compareTo(this.finishTimestamp) != TTimestamp.ARGUMENT_IS_LOWER) && (!this.isFinished)) {
            // Let's simulate
            generateProgressEvent();
            generateTimerEvent();
            // ------------------
            this.previousTimestamp.setMillisecond(this.currentTimestamp.getMillisecond());
            this.previousTimestamp.setNanosecond(this.currentTimestamp.getNanosecond());
            currentSimulatedTime = this.currentTimestamp.getTotalAsNanoseconds();
            simulationDuration = this.finishTimestamp.getTotalAsNanoseconds();
            if (currentSimulatedTime + this.tick > simulationDuration) {
                if (!simulationFinished) {
                    currentTimestamp.setMillisecond(this.finishTimestamp.getMillisecond());
                    currentTimestamp.setNanosecond(this.finishTimestamp.getNanosecond());
                    simulationFinished = true;
                } else {
                    this.currentTimestamp.increaseNanoseconds(this.tick);
                }
            } else {
                this.currentTimestamp.increaseNanoseconds(this.tick);
            }
            currentSimulatedTime = this.currentTimestamp.getTotalAsNanoseconds();
            previousSimulatedTime = this.previousTimestamp.getTotalAsNanoseconds();
            if (previousSimulatedTime == currentSimulatedTime) {
                this.isFinished = true;
            }
            waitUntilTimerEventListenersFinishTheirWork();
        }
        this.running = false;
    }

    /**
     * This method wait until all element that received a timer event have
     * consumed them. This is the way to synchronize al topology elements
     * between timer events. This method is a synchronized one.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private synchronized void waitUntilTimerEventListenersFinishTheirWork() {
        Iterator nodesIterator = this.timerEventListenerNodes.iterator();
        Iterator linksIterator = this.timerEventListenerLInks.iterator();
        TNode nodeAux;
        TLink linkAux;
        while (nodesIterator.hasNext()) {
            nodeAux = (TNode) nodesIterator.next();
            nodeAux.waitForCompletion();
        }
        while (linksIterator.hasNext()) {
            linkAux = (TLink) linksIterator.next();
            linkAux.waitForCompletion();
        }
    }

    /**
     * This method is used by any simulator object to wait for the timer finish
     * before doing a new operation. This method is a synchronized one.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void waitForCompletion() {
        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (Exception e) {
                System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TReloj.ErrorAlEsperarFinalizacionDelReloj") + e.toString());
            };
        }
    }

    /**
     * This method check whether the timer is running (and therefore, generating
     * events) or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the timer is running. Otherwise, return FALSE.
     * @since 2.0
     */
    public boolean isRunning() {
        return this.running;
    }

    private TreeSet timerEventListenerNodes;
    private TreeSet timerEventListenerLInks;
    private TProgressEventListener progressEventListener;
    private TLongIDGenerator longIdentifierGenerator;
    private int tick;
    private Thread thread;
    private TTimestamp currentTimestamp;
    private TTimestamp previousTimestamp;
    private TTimestamp finishTimestamp;
    private boolean running;
    private boolean isFinished;
    private boolean paused;
    private TTimestamp currentTimestampAux;
    private TTimestamp previousTimestampAux;
    private TTimestamp finishTimestampAux;
}
