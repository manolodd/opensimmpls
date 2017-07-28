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
package simMPLS.hardware.tldp;

/**
 * This class implements a switching matrix entry needed to manage traffic
 * forwarding.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSwitchingMatrixEntry {

    /**
     * This is the constructor of the class. It creates a newe instance of
     * TSwitchingMatrixEntry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TSwitchingMatrixEntry() {
        this.incomingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.labelOrFEC = TSwitchingMatrixEntry.UNDEFINED;
        this.outgoingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.backupOutgoingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.label = TSwitchingMatrixEntry.UNDEFINED;
        this.backupLabel = TSwitchingMatrixEntry.UNDEFINED;
        this.labelStackOperation = TSwitchingMatrixEntry.UNDEFINED;
        this.entryType = TSwitchingMatrixEntry.LABEL_ENTRY;
        this.tailEndIPv4Address = "";
        this.localTLDPSessionID = TSwitchingMatrixEntry.UNDEFINED;
        this.upstreamTLDPSessionID = TSwitchingMatrixEntry.UNDEFINED;
        this.timeout = TSwitchingMatrixEntry.TIMEOUT;
        this.labelRequestAttempts = TSwitchingMatrixEntry.LABEL_REQUEST_ATTEMPTS;
        this.isRequestForBackupLSP = false;
    }

    /**
     * This method check if, according to the existing routing information, a
     * backup LSP has been established.
     *
     * @return TRUE, if a backup LSP has been established. Otherwise, returns
     * FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean backupLSPHasBeenEstablished() {
        if (this.backupOutgoingPortID >= 0) {
            if (this.backupLabel > 15) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method check if the backup LSP should be removed even if not
     * established (for example, because it is being still established).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if backup LSP removal should be started. Otherwise returns
     * FALSE.
     * @since 2.0
     */
    public boolean backupLSPShouldBeRemoved() {
        if (backupLSPHasBeenEstablished()) {
            if (this.backupLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                return true;
            }
            if (this.backupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used by an active node and checks whether the parent
     * active node is which started the backup LSP or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the current active node started the backup LSP.
     * Otherwise, returns FALSE..
     * @since 2.0
     */
    public boolean amIABackupLSPHeadEnd() {
        if (this.isRequestForBackupLSP) {
            return false;
        }
        return true;
    }

    /**
     * This method uses a given portID to return the opposite (entry/exit)
     * portID from the switching entry. If hte specified portID is the entry
     * portID, returns the outgoing portID and vice-versa.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param portID portID whose opposite portID should be returned.
     * @return The opposite portID taking into account the one specified as an
     * argument.
     * @since 2.0
     */
    public int getOppositePortID(int portID) {
        if (this.incomingPortID == portID) {
            return this.outgoingPortID;
        }
        if (this.outgoingPortID == portID) {
            return this.incomingPortID;
        }
        if (this.backupOutgoingPortID == portID) {
            return this.incomingPortID;
        }
        return this.incomingPortID;
    }

    /**
     * This method check whether for the current entry a backup LSP has been
     * requested (if the TLDP packet that creates the entry requested a backup
     * LSP computation), or not. It is used only for visual simulation purpose.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if the initiating TLDP packet requested a backup LSP.
     * Otherwise, returns FALSE.
     * @since 2.0
     */
    public boolean aBackupLSPHasBeenRequested() {
        return this.isRequestForBackupLSP;
    }

    /**
     * This method set wheter the received TLDP packet has requested a backup
     * LSP or not. It is used only for visual simulation purpose.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param isRequestForBackupLSP TRUE, if the entry has a backup LSP
     * requests. Otherwise, returns false.
     */
    public void setEntryIsForBackupLSP(boolean isRequestForBackupLSP) {
        this.isRequestForBackupLSP = isRequestForBackupLSP;
    }

    /**
     * This method reset to zero the number of label request attempts that has
     * been used for this entry/TLDP request.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void resetAttempts() {
        this.labelRequestAttempts = TSwitchingMatrixEntry.LABEL_REQUEST_ATTEMPTS;
    }

    /**
     * This method decreases the number of remaining label requests attempts for
     * this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void decreaseAttempts() {
        if (this.labelRequestAttempts > 0) {
            this.labelRequestAttempts--;
        }
        if (this.labelRequestAttempts < 0) {
            this.labelRequestAttempts = 0;
        }
    }

    /**
     * This method checks whether there is remaining label request attempts for
     * this entry or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return TRUE, if it is still possible to retry the label request.
     * Otherwise, returns false..
     */
    public boolean areThereAvailableAttempts() {
        if (this.labelRequestAttempts > 0) {
            return true;
        }
        return false;
    }

    /**
     * This method reset the communication timeout for this entry to its default
     * value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void resetTimeOut() {
        this.timeout = TSwitchingMatrixEntry.TIMEOUT;
    }

    /**
     * This method decreases the communication timeout.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param nanosecondsToDecrease The number of nanoseconds that are going to
     * be decreased from the available timeout credit.
     */
    public void decreaseTimeOut(int nanosecondsToDecrease) {
        if (this.timeout > 0) {
            this.timeout -= nanosecondsToDecrease;
        }
        if (this.timeout < 0) {
            this.timeout = 0;
        }
    }

    /**
     * This method check if the TLDP operation of this entry shoudl be retried.
     * To do that, both, timeout expiration and attempts availability are
     * reviewed. It checks operation type to know if retry is possible.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return TRUE, if the request of this entry should be retried. Otherwise
     * returns FALSE..
     */
    public boolean shouldRetryExpiredTLDPRequest() {
        if (areThereAvailableAttempts()) {
            if (timeout == 0) {
                if ((this.label == TSwitchingMatrixEntry.LABEL_REQUESTED) || (this.label == TSwitchingMatrixEntry.REMOVING_LABEL)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * This method set the incoming portID for this entry..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID The desired incoming portID.
     * @since 2.0
     */
    public void setIncomingPortID(int incomingPortID) {
        this.incomingPortID = incomingPortID;
    }

    /**
     * This method gets the incoming portID of this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The incoming portID of this entry.
     * @since 2.0
     */
    public int getIncomingPortID() {
        return this.incomingPortID;
    }

    /**
     * This method sets the LABEL or FEC value for this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param labelOrFEC The LABEL or FEC value for this entry.
     * @since 2.0
     */
    public void setLabelOrFEC(int labelOrFEC) {
        this.labelOrFEC = labelOrFEC;
    }

    /**
     * This method gets the LABEl or FEC value of this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The LABEl or FEC value of this entry.
     * @since 2.0
     */
    public int getLabelOrFEC() {
        return this.labelOrFEC;
    }

    /**
     * This method sets the outgoing portID for this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param outgoingPortID The outgoing portID for this entry.
     * @since 2.0
     */
    public void setOutgoingPortID(int outgoingPortID) {
        this.outgoingPortID = outgoingPortID;
    }

    /**
     * This method sets the outgoing portID of the backup LSP for this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param backupOutgoingPortID the outgoing portID of the backup LSP for
     * this entry.
     * @since 2.0
     */
    public void setBackupOutgoingPortID(int backupOutgoingPortID) {
        this.backupOutgoingPortID = backupOutgoingPortID;
    }

    /**
     * This method gets the outgoing portID of this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the outgoing portID of this entry.
     * @since 2.0
     */
    public int getOutgoingPortID() {
        return this.outgoingPortID;
    }

    /**
     * This method gets the outgoing portID of the backup LSP of this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the outgoing portID of the backup LSP of this entry.
     * @since 2.0
     */
    public int getBackupOutgoingPortID() {
        return this.backupOutgoingPortID;
    }

    /**
     * This method switch the main LSP to the backup LSP and sets the conditions
     * for a new backup LSP computation (because the existing one is now the
     * main one and, hence, a backup LSP is now neccesary).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void switchToBackupLSP() {
        this.outgoingPortID = this.backupOutgoingPortID;
        this.label = this.backupLabel;
        this.backupOutgoingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.backupLabel = TSwitchingMatrixEntry.UNDEFINED;
        this.isRequestForBackupLSP = false;
    }

    /**
     * This method sets the outgoing LABEL value for this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param outgoingLabel the outgoing LABEL value for this entry.
     * @since 2.0
     */
    public void setOutgoingLabel(int outgoingLabel) {
        this.label = outgoingLabel;
    }

    /**
     * This method sets the outgoing LABEL for the backup LSP for this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param backupOutgoingLabel the outgoing LABEL for the backup LSP for this
     * entry.
     * @since 2.0
     */
    public synchronized void setBackupOutgoingLabel(int backupOutgoingLabel) {
        this.backupLabel = backupOutgoingLabel;
    }

    /**
     * This method gets the outgoing LABEL of this entry
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the outgoing LABEL of this entry.
     * @since 2.0
     */
    public int getOutgoingLabel() {
        return this.label;
    }

    /**
     * This method gets the outgoing LABEL for the backup LSP of this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the outgoing LABEL for the backup LSP of this entry.
     * @since 2.0
     */
    public synchronized int getBackupOutgoingLabel() {
        return this.backupLabel;
    }

    /**
     * This method sets the operation to be performed to the top of the label
     * stack for this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param labelStackOperation the operation to be performed to the top of
     * the label stack.
     * @since 2.0
     */
    public void setLabelStackOperation(int labelStackOperation) {
        this.labelStackOperation = labelStackOperation;
    }

    /**
     * This method gets the operation to be performed to the top of the label
     * stack of this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the operation to be performed to the top of the label stack.
     * @since 2.0
     */
    public int getLabelStackOperation() {
        return this.labelStackOperation;
    }

    /**
     * This method sets the type of entry for the this entry (ILM - Incoming
     * Label Map or FTN - FEC to Next Hop Label Forwarding Entry).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param entryType the type of entry for this entry, as defined in
     * TSwitchingMatrixEntry.
     * @since 2.0
     */
    public void setEntryType(int entryType) {
        this.entryType = entryType;
    }

    /**
     * This method gets the type of entry of the this entry (ILM - Incoming
     * Label Map or FTN - FEC to Next Hop Label Forwarding Entry).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the type of entry of this entry, as defined in
     * TSwitchingMatrixEntry.
     * @since 2.0
     */
    public int getEntryType() {
        return this.entryType;
    }

    /**
     * This method gets the IP address of the tail end node the traffic affected
     * by this entry is going to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the IP address of the tail end node the traffic affected by this
     * entry is going to.
     * @since 2.0
     */
    public String getTailEndIPv4Address() {
        return this.tailEndIPv4Address;
    }

    /**
     * This method sets the IP address of the tail end node the traffic affected
     * by this entry is going to.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param tailEndIPAddress the IP address of the tail end node the traffic
     * affected by this entry is going to.
     * @since 2.0
     */
    public void setTailEndIPAddress(String tailEndIPAddress) {
        this.tailEndIPv4Address = tailEndIPAddress;
    }

    /**
     * This method sets the local TLDP session ID for this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param localTLDPSessionID the local TLDP session ID for this entry.
     * @since 2.0
     */
    public void setLocalTLDPSessionID(int localTLDPSessionID) {
        this.localTLDPSessionID = localTLDPSessionID;
    }

    /**
     * This method gets the local TLDP session ID of this entry. estamos usando.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the local TLDP session ID of this entry.
     * @since 2.0
     */
    public int getLocalTLDPSessionID() {
        return this.localTLDPSessionID;
    }

    /**
     * This method sets the TLDP session ID used by the upstream adjacent node.
     * It could be considered that the local and the upstream nodes are chained
     * by TLDP session IDs.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param upstreamTLDPSessionID the TLDP session ID used by the upstream
     * adjacent node.
     * @since 2.0
     */
    public void setUpstreamTLDPSessionID(int upstreamTLDPSessionID) {
        this.upstreamTLDPSessionID = upstreamTLDPSessionID;
    }

    /**
     * This method gets the TLDP session ID used by the upstream adjacent node.
     * It could be considered that the local and the upstream nodes are chained
     * by TLDP session IDs.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the TLDP session ID used by the upstream adjacent node.
     * @since 2.0
     */
    public int getUpstreamTLDPSessionID() {
        return this.upstreamTLDPSessionID;
    }

    /**
     * This method checks whether the current entry is configured correctly and
     * is valid, or, on the contratry, the entry is misconfiured and should not
     * be taken into account.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE if the entry is valid. Otherwise returns FALSE.
     * @since 2.0
     */
    public boolean isAValidEntry() {
        if (this.getIncomingPortID() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getOutgoingPortID() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getLocalTLDPSessionID() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getTailEndIPv4Address().equals("")) {
            return false;
        }
        if (this.getOutgoingLabel() == TSwitchingMatrixEntry.UNDEFINED) {
            if (this.getLabelStackOperation() == TSwitchingMatrixEntry.SWAP_LABEL) {
                return false;
            }
        }
        if (this.getLabelStackOperation() == TSwitchingMatrixEntry.PUSH_LABEL) {
            return false;
        }
        return true;
    }

    // Entry types
    public static final int FEC_ENTRY = 0;
    public static final int LABEL_ENTRY = 1;
    public static final int UNDEFINED = -1;

    // Label stack operations
    public static final int PUSH_LABEL = -10;
    public static final int POP_LABEL = -12;
    public static final int SWAP_LABEL = -11;
    public static final int NOOP = -13;

    // Label space
    public static final int LABEL_SPACE = 1048575;
    public static final int FIRST_UNRESERVED_LABEL = 16;

    // TLDP messages and status
    public static final int LABEL_REQUESTED = -33;
    public static final int LABEL_UNAVAILABLE = -31;
    public static final int LABEL_ASSIGNED = -30;
    public static final int REMOVING_LABEL = -32;
    public static final int LABEL_WITHDRAWN = -34;
    public static final int PATH_UNAVAILABLE = -20;

    // TLDP timeout and attempts number
    private static final int TIMEOUT = 50000;
    private static final int LABEL_REQUEST_ATTEMPTS = 3;

    private int incomingPortID;
    private int labelOrFEC;
    private int outgoingPortID;
    private int backupOutgoingPortID;
    private int label;
    private int backupLabel;
    private int labelStackOperation;
    private int entryType;
    private String tailEndIPv4Address;
    private int localTLDPSessionID;
    private int upstreamTLDPSessionID;
    private boolean isRequestForBackupLSP;
    private int timeout;
    private int labelRequestAttempts;
}
