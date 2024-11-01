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
package com.manolodominguez.opensimmpls.hardware.tldp;

import java.util.Iterator;
import java.util.LinkedList;
import com.manolodominguez.opensimmpls.commons.TSemaphore;

/**
 * This class implements a switching matrix to be used within each node of the
 * topology.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSwitchingMatrix {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TSwitchingMatrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TSwitchingMatrix() {
        this.switchingMatrix = new LinkedList<>();
        this.semaphore = new TSemaphore();
    }

    /**
     * This method returns the semaphore of the class, that will allow
     * operations on the switching matrix avoiding the risk of concurrent
     * accesses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The semaphore of the class.
     * @since 2.0
     */
    public TSemaphore getSemaphore() {
        return this.semaphore;
    }

    /**
     * This method add a new switching entry to the switching matrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param switchingMatrixEntry The entry to be added to the switching
     * matrix.
     * @since 2.0
     */
    public void addEntry(TSwitchingMatrixEntry switchingMatrixEntry) {
        this.semaphore.setRed();
        this.switchingMatrix.addLast(switchingMatrixEntry);
        this.semaphore.setGreen();
    }

    /**
     * This method gives access to a specific switching entry of the switching
     * matrix using the values specified as arguments to do that.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID The incoming port of field of the wanted switching
     * entry.
     * @param labelOrFEC The labelOrFEC field of the wanted switching entry.
     * @param entryType The entry type (ILM or FTN) field of the wanted
     * switching entry.
     * @return The switching entry corresponding to the values specified as
     * arguments (if exist) or NULL on the contrary.
     * @since 2.0
     */
    public TSwitchingMatrixEntry getEntry(int incomingPortID, int labelOrFEC, int entryType) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.semaphore.setGreen();
                        return switchingMatrixEntryAux;
                    }
                }
            }
        }
        this.semaphore.setGreen();
        return null;
    }

    /**
     * This method gives access to a specific switching entry of the switching
     * matrix using the value specified as an argument to do that.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param localTLDPSessionID Local TLDP session ID field of the wanted
     * switching entry.
     * @return The switching entry corresponding to the value specified as an
     * argument (if exist) or NULL on the contrary.
     * @since 2.0
     */
    public TSwitchingMatrixEntry getEntry(int localTLDPSessionID) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLocalTLDPSessionID() == localTLDPSessionID) {
                this.semaphore.setGreen();
                return switchingMatrixEntryAux;
            }
        }
        this.semaphore.setGreen();
        return null;
    }

    /**
     * This method gives access to a specific switching entry of the switching
     * matrix using the values specified as arguments to do that.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param upstreamTLDPSessionID Upstream TLDP session ID field of the wanted
     * switching entry.
     * @param incomingPortID The incoming port of field of the wanted switching
     * entry.
     * @return The switching entry corresponding to the values specified as
     * arguments (if exist) or NULL on the contrary.
     * @since 2.0
     */
    public TSwitchingMatrixEntry getEntry(int upstreamTLDPSessionID, int incomingPortID) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getUpstreamTLDPSessionID() == upstreamTLDPSessionID) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    this.semaphore.setGreen();
                    return switchingMatrixEntryAux;
                }
            }
        }
        this.semaphore.setGreen();
        return null;
    }

    /**
     * This method check wheter a specific sitching entry (identified by the
     * values passed as arguments) exist in the switching matrix or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID The incoming port of field of the wanted switching
     * entry.
     * @param labelOrFEC The labelOrFEC field of the wanted switching entry.
     * @param entryType The entry type (ILM or FTN) field of the wanted
     * switching entry.
     * @return TRUE, if the wanted switching entry exist in the switching
     * matrix. Otherwise, returns FALSE.
     * @since 2.0
     */
    public boolean existsEntry(int incomingPortID, int labelOrFEC, int entryType) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.semaphore.setGreen();
                        return true;
                    }
                }
            }
        }
        this.semaphore.setGreen();
        return false;
    }

    /**
     * This method removes a specific switching entry of the switching matrix
     * using the value specified as an argument to do that.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID The incoming port of field of the switching entry
     * to be removed.
     * @param labelOrFEC The labelOrFEC field of the switching entry to be
     * removed.
     * @param entryType The entry type (ILM or FTN) field of the switching entry
     * to be removed.
     * @since 2.0
     */
    public void removeEntry(int incomingPortID, int labelOrFEC, int entryType) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        iterator.remove();
                    }
                }
            }
        }
        this.semaphore.setGreen();
    }

    /**
     * This method removes a specific switching entry of the switching matrix
     * using the value specified as an argument to do that.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param localTLDPSessionID Local TLDP session ID field of the switching
     * entry to be removed.
     * @param incomingPortID The incoming port of field of the switching entry
     * to be removed.
     * @since 2.0
     */
    public void removeEntry(int localTLDPSessionID, int incomingPortID) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLocalTLDPSessionID() == localTLDPSessionID) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    iterator.remove();
                }
            }
        }
        this.semaphore.setGreen();
    }

    /**
     * This method gets the operation that has to be done over the top of the
     * label stack as stored in the switching entry identified by the values
     * specified as arguments.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID The incoming port of field of the switching entry
     * whose label stack operation is wanted.
     * @param labelOrFEC The labelOrFEC field of the switching entry whose label
     * stack operation is wanted.
     * @param entryType The entry type (ILM or FTN) field of the switching entry
     * whose label stack operation is wanted.
     * @return the operation to be done over the top of the label stack as
     * defined in the switching entry identified by the values specified as
     * arguments. Operations are defined in TSwitchingMatrixEntry class
     * (TSwitchingMatrixEntry.PUSH_LABEL, TSwitchingMatrixEntry.POP_LABEL,
     * TSwitchingMatrixEntry.SWAP_LABEL and TSwitchingMatrixEntry.NOOP).
     * @since 2.0
     */
    public int getLabelStackOperation(int incomingPortID, int labelOrFEC, int entryType) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.semaphore.setGreen();
                        return switchingMatrixEntryAux.getLabelStackOperation();
                    }
                }
            }
        }
        this.semaphore.setGreen();
        return TSwitchingMatrixEntry.UNDEFINED;
    }

    /**
     * This method gets the outgoing label that has to be used when forwarding a
     * packet as stored in the switching entry identified by the values
     * specified as arguments.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID The incoming port of field of the switching entry
     * whose label stack operation is wanted.
     * @param labelOrFEC The labelOrFEC field of the switching entry whose label
     * stack operation is wanted.
     * @param entryType The entry type (ILM or FTN) field of the switching entry
     * whose label stack operation is wanted.
     * @return the outgoing label that has to be used when forwarding a packet
     * as stored in the switching entry identified by the values specified as
     * arguments.
     * @since 2.0
     */
    public int getOutgoingLabel(int incomingPortID, int labelOrFEC, int entryType) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.semaphore.setGreen();
                        return switchingMatrixEntryAux.getOutgoingLabel();
                    }
                }
            }
        }
        this.semaphore.setGreen();
        return TSwitchingMatrixEntry.UNDEFINED;
    }

    /**
     * This method gets the outgoing port that has to be used when forwarding a
     * packet as stored in the switching entry identified by the values
     * specified as arguments.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param incomingPortID The incoming port of field of the switching entry
     * whose label stack operation is wanted.
     * @param labelOrFEC The labelOrFEC field of the switching entry whose label
     * stack operation is wanted.
     * @param entryType The entry type (ILM or FTN) field of the switching entry
     * whose label stack operation is wanted.
     * @return the outgoing port that has to be used when forwarding a packet as
     * stored in the switching entry identified by the values specified as
     * arguments.
     * @since 2.0
     */
    public int getOutgoingPortID(int incomingPortID, int labelOrFEC, int entryType) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux = null;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == labelOrFEC) {
                if (switchingMatrixEntryAux.getIncomingPortID() == incomingPortID) {
                    if (switchingMatrixEntryAux.getEntryType() == entryType) {
                        this.semaphore.setGreen();
                        return switchingMatrixEntryAux.getOutgoingPortID();
                    }
                }
            }
        }
        this.semaphore.setGreen();
        return TSwitchingMatrixEntry.UNDEFINED;
    }

    /**
     * This method check wheter a given label is already used by any switching
     * entry in the switching matrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param label The label we want to check.
     * @return TRUE if any switching entry in the switching matrix is using the
     * specified label. Otherwise returns FALSE.
     * @since 2.0
     */
    public boolean labelIsAlreadyUsed(int label) {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        TSwitchingMatrixEntry switchingMatrixEntryAux;
        while (iterator.hasNext()) {
            switchingMatrixEntryAux = iterator.next();
            if (switchingMatrixEntryAux.getLabelOrFEC() == label) {
                if (switchingMatrixEntryAux.getEntryType() == TSwitchingMatrixEntry.LABEL_ENTRY) {
                    this.semaphore.setGreen();
                    return true;
                }
            }
        }
        this.semaphore.setGreen();
        return false;
    }

    /**
     * This method generates and returns a new 20-bits label tha is not used by
     * any other switching entry in the switching matrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a new 20-bits label that is not used by any other switching entry
     * in the switching matrix, if possible. If the label space is completely
     * used and is not possible to return a new label, this returns
     * TSwitchingMatrixEntry.LABEL_UNAVAILABLE.
     * @since 2.0
     */
    public int getNewLabel() {
        int labelAux = TSwitchingMatrixEntry.FIRST_UNRESERVED_LABEL;
        while (labelAux <= TSwitchingMatrixEntry.LABEL_SPACE) {
            if (!labelIsAlreadyUsed(labelAux)) {
                return labelAux;
            } else {
                labelAux++;
            }
        }
        return TSwitchingMatrixEntry.LABEL_UNAVAILABLE;
    }

    /**
     * This method returns the switching entries iterator for this switching
     * matrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the switching entries iterator for this switching matrix.
     * @since 2.0
     */
    public Iterator<TSwitchingMatrixEntry> getEntriesIterator() {
        return this.switchingMatrix.iterator();
    }

    /**
     * This method returns the number of switching entries currently stored in
     * this switching matrix.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the number of switching entries currently stored in this
     * switching matrix.
     * @since 2.0
     */
    public int getNumberOfEntries() {
        return this.switchingMatrix.size();
    }

    /**
     * This method clear all switching entries in stored in the switching
     * matrix, as when created by the constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.semaphore.setRed();
        Iterator<TSwitchingMatrixEntry> iterator = this.switchingMatrix.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.semaphore.setGreen();
    }

    private LinkedList<TSwitchingMatrixEntry> switchingMatrix;
    private TSemaphore semaphore;
}
