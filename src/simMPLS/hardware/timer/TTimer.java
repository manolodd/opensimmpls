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

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.scenario.TExternalLink;
import simMPLS.scenario.TInternalLink;
import simMPLS.scenario.TLERANode;
import simMPLS.scenario.TLERNode;
import simMPLS.scenario.TLSRANode;
import simMPLS.scenario.TLSRNode;
import simMPLS.scenario.TLink;
import simMPLS.scenario.TNode;
import simMPLS.scenario.TReceiverNode;
import simMPLS.scenario.TSenderNode;
import simMPLS.scenario.TTopologyElement;
import simMPLS.utils.EIdentifierGeneratorOverflow;
import simMPLS.utils.TProgressEventListener;
import simMPLS.utils.TLongIdentifier;

/**
 * This class implements a timer that will govern the operation and
 * synchronization of the simulation. It will send time events to the rest of
 * component that compose the topology.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TTimer implements Runnable {

    /**
     * This method is the constuctor of the class. It will create a new instance
     * of TTimer and will set the initial values for the attributes of the
     * class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TTimer() {
        this.thread = null;
        this.timerEventListenerNodes = new TreeSet();
        this.timerEventListenerLInks = new TreeSet();
        this.progressEventListener = null;
        this.longIdentifierGenerator = new TLongIdentifier();
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
                if (nodeAux.getID() == timerEventListenerAux.getID()) {
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
     * @since 1.0
     */
    public void purgeTimerEventListeners() {
        Iterator linksIterator = this.timerEventListenerLInks.iterator();
        TLink linkAux;
        while (linksIterator.hasNext()) {
            linkAux = (TLink) linksIterator.next();
            if (linkAux.hasToBePurged()) {
                linksIterator.remove();
            }
        }
        Iterator nodesIterator = this.timerEventListenerNodes.iterator();
        TNode nodeAux;
        while (nodesIterator.hasNext()) {
            nodeAux = (TNode) nodesIterator.next();
            if (nodeAux.hasToBePurged()) {
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
     * @since 1.0
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
     * @since 1.0
     */
    public void removeProgressEventListener() {
        this.progressEventListener = null;
    }

    /**
     * This method generates a new timer event an sends it to all topology
     * elements that are subscribed to receive timer events.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
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
                case TNode.SENDER: {
                    nodeAux = (TSenderNode) nodeAux;
                    break;
                }
                case TNode.LER: {
                    nodeAux = (TLERNode) nodeAux;
                    break;
                }
                case TNode.LERA: {
                    nodeAux = (TLERANode) nodeAux;
                    break;
                }
                case TNode.LSR: {
                    nodeAux = (TLSRNode) nodeAux;
                    break;
                }
                case TNode.LSRA: {
                    nodeAux = (TLSRANode) nodeAux;
                    break;
                }
                case TNode.RECEIVER: {
                    nodeAux = (TReceiverNode) nodeAux;
                    break;
                }
            }
            try {
                TTimerEvent timerEvent = new TTimerEvent(this, this.longIdentifierGenerator.getNextID(), startOfSimulationInterval, endOfSimulationInterval);
                nodeAux.receiveTimerEvent(timerEvent);
            } catch (EIdentifierGeneratorOverflow e) {
                e.printStackTrace();
            }
        }
        while (linksIterator.hasNext()) {
            linkAux = (TLink) linksIterator.next();
            switch (linkAux.getLinkType()) {
                case TLink.EXTERNAL: {
                    linkAux = (TExternalLink) linkAux;
                    break;
                }
                case TLink.INTERNAL: {
                    linkAux = (TInternalLink) linkAux;
                    break;
                }
            }
            try {
                linkAux.receiveTimerEvent(new TTimerEvent(this, this.longIdentifierGenerator.getNextID(), startOfSimulationInterval, endOfSimulationInterval));
            } catch (EIdentifierGeneratorOverflow e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method generates a new progress event an sends it to the only one
     * listener that are subscribed to receive it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
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
                this.progressEventListener.receiveProgressEvent(new TProgressEvent(this, this.longIdentifierGenerator.getNextID(), computedProgress));
            }
        } catch (EIdentifierGeneratorOverflow e) {
            e.printStackTrace();
        }
    }

    /**
     * This method restart the timer operation after it was previously paused.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    public synchronized void waitForCompletion() {
        if (this.thread != null) {
            try {
                this.thread.join();
            } catch (Exception e) {
                System.out.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TReloj.ErrorAlEsperarFinalizacionDelReloj") + e.toString());
            };
        }
    }

    /**
     * This method check whether the timer is running (and therefore, generating
     * events) or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the timer is running. Otherwise, return FALSE.
     * @since 1.0
     */
    public boolean isRunning() {
        return this.running;
    }

    private TreeSet timerEventListenerNodes;
    private TreeSet timerEventListenerLInks;
    private TProgressEventListener progressEventListener;
    private TLongIdentifier longIdentifierGenerator;
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
