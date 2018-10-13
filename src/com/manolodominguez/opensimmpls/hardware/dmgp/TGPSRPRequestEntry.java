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
package com.manolodominguez.opensimmpls.hardware.dmgp;

import java.util.LinkedList;

/**
 * This class implements an entry that will store data related to a
 * retransmission requested by a node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TGPSRPRequestEntry implements Comparable<TGPSRPRequestEntry> {

    /**
     * This is the class constructor. Implements a new instance of
     * TGPSRPRequestsEntry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param arrivalOrder Arrival order. This is a number that must be used to
     * make this entry shorted in a collection in a coherent way.
     * @since 2.0
     */
    public TGPSRPRequestEntry(int arrivalOrder) {
        this.timeout = AvailableGPSRPConfigValues.GPSRP_TIMEOUT_NANOSECONDS.getValue();
        this.attempts = AvailableGPSRPConfigValues.GPSRP_ATTEMPTS.getValue();
        this.flowID = DEFAULT_FLOWID;
        this.packetID = DEFAULT_PACKETID;
        this.outgoingPortID = DEFAULT_OUTGOING_PORTID;
        this.crossedNodes = new LinkedList<>();
        this.arrivalOrder = arrivalOrder;
    }

    /**
     * This method obtains the arrival order to the entry in order to be shorted
     * in a collection.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Arrival order to the entry.
     * @since 2.0
     */
    public int getArrivalOrder() {
        return this.arrivalOrder;
    }

    /**
     * This method establishes the flow ID of the flow the entry belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID The flow ID of the flow the entry belongs to.
     * @since 2.0
     */
    public void setFlowID(int flowID) {
        this.flowID = flowID;
    }

    /**
     * This method obtains the flow ID of the flow the entry belongs to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The flow ID of the flow the entry belongs to.
     * @since 2.0
     */
    public int getFlowID() {
        return this.flowID;
    }

    /**
     * This method establishes the identifier of the packet this entry refers
     * to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packetID The packet identifier.
     * @since 2.0
     */
    public void setPacketID(int packetID) {
        this.packetID = packetID;
    }

    /**
     * This method obtains the identifier of the packet this entry refers to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The packet identifier.
     * @since 2.0
     */
    public int getPacketID() {
        return this.packetID;
    }

    /**
     * This method establishes the outgoing port ID of the port by where the
     * retransmission request has been sent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param outgoingPortID Outgoing port ID.
     * @since 2.0
     */
    public void setOutgoingPort(int outgoingPortID) {
        this.outgoingPortID = outgoingPortID;
    }

    /**
     * This method obtains the outgoing port ID of the port by where the
     * retransmission request has been sent.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Outgoing port ID.
     * @since 2.0
     */
    public int getOutgoingPort() {
        return this.outgoingPortID;
    }

    /**
     * This method sets the IP address of an active node that will be requested
     * for a packet retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param crossedNodeIP IP address of a node to be requested for a packet
     * retransmission.
     * @since 2.0
     */
    public void setCrossedNodeIP(String crossedNodeIP) {
        this.crossedNodes.addFirst(crossedNodeIP);
    }

    /**
     * This method obtains the IP address of the next active node that will be
     * requested for a packet retransmission.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return IP address of the next active node to be requested for a packet
     * retransmission. If there is not a node to be requested, this method
     * return NULL.
     * @since 2.0
     */
    public String getCrossedNodeIPv4() {
        if (this.crossedNodes.size() > ZERO) {
            return this.crossedNodes.removeFirst();
        }
        return null;
    }

    /**
     * This method decreases the retransmission TimeOut.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param nanoseconds Number of nanoseconds to decrease from the timeout.
     * @since 2.0
     */
    public void decreaseTimeout(int nanoseconds) {
        this.timeout -= nanoseconds;
        if (this.timeout < ZERO) {
            this.timeout = ZERO;
        }
    }

    /**
     * This method restores the retransmission TimeOut to its original value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void resetTimeout() {
        if (this.timeout == ZERO) {
            if (this.attempts > ZERO) {
                this.timeout = AvailableGPSRPConfigValues.GPSRP_TIMEOUT_NANOSECONDS.getValue();
                this.attempts--;
            }
        }
    }

    /**
     * This method forces the TimeOut restoration to its original value and also
     * increases the number of expired retransmission attempts.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void forceTimeoutReset() {
        this.timeout = AvailableGPSRPConfigValues.GPSRP_TIMEOUT_NANOSECONDS.getValue();
        this.attempts--;
        if (this.attempts < ZERO) {
            attempts = ZERO;
            timeout = ZERO;
        }
    }

    /**
     * This method ckeck whether the retransmission request must be retried
     * again or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the retransmission must be retried. Otherwise, FALSE.
     * @since 2.0
     */
    public boolean isRetryable() {
        if (this.attempts > ZERO) {
            if (this.timeout == ZERO) {
                if (this.crossedNodes.size() > ZERO) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method check whether the entry must be removed from the table
     * (because retransmission is not going to be retried) or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the entry must be removed. Otherwise, FALSE.
     * @since 2.0
     */
    public boolean isPurgeable() {
        if (this.crossedNodes.size() == ZERO) {
            return true;
        }
        if (this.attempts == ZERO) {
            if (this.timeout == ZERO) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method compares the current instance with another of the same type
     * passed as an argument to know the order to be inserted in a collection.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherTGPSRPRequestEntry Instancia con la que se va a comparar la
     * actual.
     * @return -1, 0, 1, depending on wheter the curren instance is lower,
     * equal, or greater than the one passed as an argument. In terms of
     * shorting.
     * @since 2.0
     */
    @Override
    public int compareTo(TGPSRPRequestEntry anotherTGPSRPRequestEntry) {
        if (this.arrivalOrder < anotherTGPSRPRequestEntry.getArrivalOrder()) {
            return TGPSRPRequestEntry.THIS_LOWER;
        }
        if (this.arrivalOrder > anotherTGPSRPRequestEntry.getArrivalOrder()) {
            return TGPSRPRequestEntry.THIS_GREATER;
        }
        return TGPSRPRequestEntry.THIS_EQUAL;
    }

    private static final int THIS_LOWER = -1;
    private static final int THIS_EQUAL = 0;
    private static final int THIS_GREATER = 1;

    private static final int DEFAULT_FLOWID = -1;
    private static final int DEFAULT_PACKETID = -1;
    private static final int DEFAULT_OUTGOING_PORTID = -1;

    private static final int ZERO = 0;

    private int timeout;
    private int flowID;
    private int packetID;
    private int outgoingPortID;
    private final LinkedList<String> crossedNodes;
    private final int arrivalOrder;
    private int attempts;
}
