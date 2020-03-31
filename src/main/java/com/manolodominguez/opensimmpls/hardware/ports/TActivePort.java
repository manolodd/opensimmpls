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
package com.manolodominguez.opensimmpls.hardware.ports;

import com.manolodominguez.opensimmpls.commons.EIDGeneratorOverflow;
import java.util.Iterator;
import java.util.TreeSet;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketReceived;
import com.manolodominguez.opensimmpls.scenario.TStats;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.commons.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.commons.TSemaphore;
import static com.manolodominguez.opensimmpls.commons.UnitsTranslations.OCTETS_PER_MEGABYTE;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an active port. Active ports will be available in
 * active nodes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TActivePort extends TPort {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TActivePort.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param portID port number (the port id used to distinguish this port of
     * other in the same active parentNode).
     * @param parentSetOfActivePorts A reference to the parent set of active
     * ports this active port belongs to.
     */
    public TActivePort(TPortSet parentSetOfActivePorts, int portID) {
        super(parentSetOfActivePorts, portID);
        translations = ResourceBundle.getBundle(AvailableBundles.T_ACTIVE_PORT.getPath());
        packetRead = DEFAULT_PACKET_READ;
        isUnlimitedBuffer = DEFAULT_IS_UNLIMITED_BUFFER;
        rotaryIdentifierGenerator = new TRotaryIDGenerator();
        priority0BufferSemaphore = new TSemaphore();
        priority1BufferSemaphore = new TSemaphore();
        priority2BufferSemaphore = new TSemaphore();
        priority3BufferSemaphore = new TSemaphore();
        priority4BufferSemaphore = new TSemaphore();
        priority5BufferSemaphore = new TSemaphore();
        priority6BufferSemaphore = new TSemaphore();
        priority7BufferSemaphore = new TSemaphore();
        priority8BufferSemaphore = new TSemaphore();
        priority9BufferSemaphore = new TSemaphore();
        priority10BufferSemaphore = new TSemaphore();
        priority0Buffer = new TreeSet<>();
        priority1Buffer = new TreeSet<>();
        priority2Buffer = new TreeSet<>();
        priority3Buffer = new TreeSet<>();
        priority4Buffer = new TreeSet<>();
        priority5Buffer = new TreeSet<>();
        priority6Buffer = new TreeSet<>();
        priority7Buffer = new TreeSet<>();
        priority8Buffer = new TreeSet<>();
        priority9Buffer = new TreeSet<>();
        priority10Buffer = new TreeSet<>();
        selectedBuffer = DEFAULT_SELECTED_BUFFER;
        nextPacketToBeRead = null;
        maxReadsOfBuffer = new int[(HIGHEST_PRIORITY + ONE)];
        currentReadsOfBuffer = new int[(HIGHEST_PRIORITY + ONE)];
        int i;
        for (i = ZERO; i <= HIGHEST_PRIORITY; i++) {
            maxReadsOfBuffer[i] = i + ONE;
            currentReadsOfBuffer[i] = ZERO;
        }
    }

    /**
     * This method computes and set the next packet to be read from the port.
     * This is an active port that differs from the normal one because packets
     * are not read from the buffer following a FIFO paradigm. Instead, the
     * active port has 11 different prioritized buffers (10 to 0) depending on
     * the requirements of each packet which is embedded in the packet itself.
     * Read the proposal "Guarantee of Service (GoS) support over MPLS using
     * active techniques" to know more about these 11 types of traffic.
     * Summarizing, an active port acts as a single port, but maintains 11
     * buffers with increased packetPriority from 0 to 10. The port executes a
     * prioritized Round Robin algorithm. Y goes on buffer by buffer avoiding
     * undefined relegation of packets but assuring that more prioritized
     * packets (those in higher buffers) will be handled before less prioritized
     * ones. In fact, due to some studies done before designing this proposal,
     * if there are available packets, the port will read 10 packets from
     * prioritized buffer 10, 9 from prioritized buffer 9, 8 from prioritized
     * buffer 8, and so on. So, imagine that there are enough packets in each
     * buffer, then in a complete Prioritized Round Robin cicle the ports should
     * read: 11 packets from buffer of packetPriority 10; 10 packets from buffer
     * of packetPriority 9; 9 packets from buffer of packetPriority 8; 8 packets
     * from buffer of packetPriority 7; 7 packets from buffer of packetPriority
     * 6; 6 packets from buffer of packetPriority 5; 5 packets from buffer of
     * packetPriority 4; 4 packets from buffer of packetPriority 3; 3 packets
     * from buffer of packetPriority 2; 2 packets from buffer of packetPriority
     * 1; 1 packets from buffer of packetPriority 0.
     *
     * This method prepares the next read that follow this scheme. And, in case
     * of existing only best effort traffic without gosLevel requirements, the
     * port works as a traditional one, dispatching one packet per cicle
     * followin a FIFO paradigm.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void doPrioritizedRoundRobinPacketSelection() {
        boolean end = false;
        int numberOfEmptyBuffers = ZERO;
        int numberOfBuffersAlreadyRead = ZERO;
        Iterator<TActivePortBufferEntry> iterator = null;
        TActivePortBufferEntry activePortBufferEntry = null;
        if (nextPacketToBeRead == null) {
            while ((!end) && (numberOfEmptyBuffers <= MAX_PRIORITIZED_BUFFERS)) {
                switch (selectedBuffer) {
                    case TEN:
                        if (priority10Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[TEN] < maxReadsOfBuffer[TEN]) {
                                priority10BufferSemaphore.setRed();
                                iterator = priority10Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority10BufferSemaphore.setGreen();
                                currentReadsOfBuffer[TEN]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[TEN] = maxReadsOfBuffer[TEN];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case NINE:
                        if (priority9Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[NINE] < maxReadsOfBuffer[NINE]) {
                                priority9BufferSemaphore.setRed();
                                iterator = priority9Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority9BufferSemaphore.setGreen();
                                currentReadsOfBuffer[NINE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[NINE] = maxReadsOfBuffer[NINE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case EIGHT:
                        if (priority8Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[EIGHT] < maxReadsOfBuffer[EIGHT]) {
                                priority8BufferSemaphore.setRed();
                                iterator = priority8Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority8BufferSemaphore.setGreen();
                                currentReadsOfBuffer[EIGHT]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[EIGHT] = maxReadsOfBuffer[EIGHT];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case SEVEN:
                        if (priority7Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[SEVEN] < maxReadsOfBuffer[SEVEN]) {
                                priority7BufferSemaphore.setRed();
                                iterator = priority7Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority7BufferSemaphore.setGreen();
                                currentReadsOfBuffer[SEVEN]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[SEVEN] = maxReadsOfBuffer[SEVEN];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case SIX:
                        if (priority6Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[SIX] < maxReadsOfBuffer[SIX]) {
                                priority6BufferSemaphore.setRed();
                                iterator = priority6Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority6BufferSemaphore.setGreen();
                                currentReadsOfBuffer[SIX]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[SIX] = maxReadsOfBuffer[SIX];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case FIVE:
                        if (priority5Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[FIVE] < maxReadsOfBuffer[FIVE]) {
                                priority5BufferSemaphore.setRed();
                                iterator = priority5Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority5BufferSemaphore.setGreen();
                                currentReadsOfBuffer[FIVE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[FIVE] = maxReadsOfBuffer[FIVE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case FOUR:
                        if (priority4Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[FOUR] < maxReadsOfBuffer[FOUR]) {
                                priority4BufferSemaphore.setRed();
                                iterator = priority4Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority4BufferSemaphore.setGreen();
                                currentReadsOfBuffer[FOUR]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[FOUR] = maxReadsOfBuffer[FOUR];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case THREE:
                        if (priority3Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[THREE] < maxReadsOfBuffer[THREE]) {
                                priority3BufferSemaphore.setRed();
                                iterator = priority3Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority3BufferSemaphore.setGreen();
                                currentReadsOfBuffer[THREE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[THREE] = maxReadsOfBuffer[THREE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case TWO:
                        if (priority2Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[TWO] < maxReadsOfBuffer[TWO]) {
                                priority2BufferSemaphore.setRed();
                                iterator = priority2Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority2BufferSemaphore.setGreen();
                                currentReadsOfBuffer[TWO]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[TWO] = maxReadsOfBuffer[TWO];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case ONE:
                        if (priority1Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[ONE] < maxReadsOfBuffer[ONE]) {
                                priority1BufferSemaphore.setRed();
                                iterator = priority1Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                priority1BufferSemaphore.setGreen();
                                currentReadsOfBuffer[ONE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[ONE] = maxReadsOfBuffer[ONE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case ZERO:
                        if (priority0Buffer.size() > ZERO) {
                            if (currentReadsOfBuffer[ZERO] < maxReadsOfBuffer[ZERO]) {
                                priority0BufferSemaphore.setRed();
                                iterator = priority0Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = iterator.next();
                                    nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                } 
                                priority0BufferSemaphore.setGreen();
                                currentReadsOfBuffer[ZERO]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            currentReadsOfBuffer[ZERO] = maxReadsOfBuffer[ZERO];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    default:
                        break;
                }
                selectedBuffer = ((selectedBuffer + ONE) % MAX_PRIORITIZED_BUFFERS);
                if (numberOfBuffersAlreadyRead >= MAX_PRIORITIZED_BUFFERS) {
                    int i;
                    for (i = ZERO; i < MAX_PRIORITIZED_BUFFERS; i++) {
                        currentReadsOfBuffer[i] = ZERO;
                    }
                }
            }
        }
    }

    /**
     * This method selects next packet to be read from the active port and
     * return its packetPriority according to the "Guarante of Service (GoS)
     * support over MPLS using active techniques". This information is embedded
     * in the packet itself. Read the proposal to know more about prioritized
     * packets.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return -1, if there are not packet to read (the port is empty).
     * Otherwise, iterator return a value between 0 and 10 (inclusive) which is
     * the packetPriority of the next packet to be read from the port.
     */
    public int getNextPacketPriority() {
        int priorityAux;
        semaphore.setRed();
        doPrioritizedRoundRobinPacketSelection();
        if (nextPacketToBeRead != null) {
            priorityAux = loadPacketPriority(nextPacketToBeRead);
            semaphore.setGreen();
            return priorityAux;
        }
        semaphore.setGreen();
        return -1;
    }

    /**
     * This method allow to skip size limitation of the buffer and, hence,
     * configure the port as an ideal port, with unlimited space.
     *
     * @param unlimitedBuffer TRUE if the port is going to be defined as an
     * ideal one (unlimited space on iterator). FALSE, on the contrary.
     * @since 2.0
     */
    @Override
    public void setUnlimitedBuffer(boolean unlimitedBuffer) {
        this.isUnlimitedBuffer = unlimitedBuffer;
    }

    /**
     * This method, when implemented, will allow to known wheter a port is
     * configured as unlimited (as an ideal port), or not.
     *
     * @return TRUE if the port is defined as an ideal one (unlimited space on
     * it). FALSE, on the contrary.
     * @since 2.0
     */
    @Override
    public boolean isUnlimitedBuffer() {
        return isUnlimitedBuffer;
    }

    /**
     * This method discard the packet passed as an argument from the buffer.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be discarded from the buffer.
     * @since 2.0
     */
    @Override
    public void discardPacket(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        getPortSet().getParentNode().discardPacket(packet);
    }

    /**
     * This method put a new packet in the buffer of the active port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet Packet to be inserted in the buffer of the active port.
     * @since 2.0
     */
    @Override
    public void addPacket(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        TActivePortSet parentPortSetAux = (TActivePortSet) parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        semaphore.setRed();
        TNode parentNode = parentPortSet.getParentNode();
        long eventID = ZERO;
        int packetOrder = ZERO;
        int priority = loadPacketPriority(packet);
        try {
            eventID = parentNode.eventIdentifierGenerator.getNextIdentifier();
            packetOrder = rotaryIdentifierGenerator.getNextIdentifier();
        } catch (EIDGeneratorOverflow ex) {
            logger.error(ex.getMessage(), ex);
        }
        int packetSubtype = packet.getSubtype();
        if (isUnlimitedBuffer) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(priority, packetOrder, packet);
            addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, getPortSet().getParentNode().getCurrentTimeInstant(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (getPortSet().getParentNode().getStats() != null) {
                getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.INCOMING);
            }
        } else {
            if (!runEarlyPacketCatchAndDiscard(packet)) {
                discardPacket(packet);
            }
        }
        semaphore.setGreen();
        parentPortSetAux.portSetSemaphore.setGreen();
    }

    /**
     * This method implements Early Packet Catch and Discard algorithm for
     * active buffer management, as defined in the proposal "Guarentee of
     * Service Support (GoS) over MPLS using Active Techniques". Read the
     * proposal to know more about EPCD algorithm.
     *
     * Summarizing, EPCD maintains a fixed thresshold free in the buffer of the
     * active port. If the packet is going to be discarded, this small space
     * allow the algorithm to retain some bytes of the packet header that are
     * codified as expressed in the GoS proposal. Using these bytes, this method
     * request the parent parentNode to request retransmission of the packet, if
     * needed, to other active nodes.
     *
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet that is going to be inserted in the buffer
     * through EPCD algoritm.
     * @return TRUE, if the packet can be inserted in the buffer. If this is the
     * case, the packet is inserted in the same operation. If the packet should
     * be discarded because a buffer overflow, the method returns FALSE and the
     * EPCD algorithm request the parent parentNode to request the packet
     * retransmission.
     */
    public boolean runEarlyPacketCatchAndDiscard(TAbstractPDU packet) {
        TActivePortSet parentPortSetAux = (TActivePortSet) parentPortSet;
        long eventID = 0;
        int packetOrder = 0;
        int packetPriority = loadPacketPriority(packet);
        TNode parentNode = parentPortSet.getParentNode();
        try {
            eventID = parentNode.eventIdentifierGenerator.getNextIdentifier();
            packetOrder = rotaryIdentifierGenerator.getNextIdentifier();
        } catch (EIDGeneratorOverflow ex) {
            logger.error(ex.getMessage(), ex);
        }
        int packetSubtype = packet.getSubtype();
        if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= ((parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits()) - EPCD_THRESHOLD)) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
            addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, getPortSet().getParentNode().getCurrentTimeInstant(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (getPortSet().getParentNode().getStats() != null) {
                getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.INCOMING);
            }
            return true;
        } else {
            if (packet.getSubtype() == TAbstractPDU.MPLS_GOS) {
                parentPortSetAux.getParentNode().runGPSRP((TMPLSPDU) packet, portID);
            }
        }
        return false;
    }

    private void addPrioritizedBufferEntry(TActivePortBufferEntry activePortBufferEntry) {
        int priorityAux = activePortBufferEntry.getPriority();
        switch (priorityAux) {
            case TActivePort.PRIORITY_10:
                priority10BufferSemaphore.setRed();
                priority10Buffer.add(activePortBufferEntry);
                priority10BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_9:
                priority9BufferSemaphore.setRed();
                priority9Buffer.add(activePortBufferEntry);
                priority9BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_8:
                priority8BufferSemaphore.setRed();
                priority8Buffer.add(activePortBufferEntry);
                priority8BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_7:
                priority7BufferSemaphore.setRed();
                priority7Buffer.add(activePortBufferEntry);
                priority7BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_6:
                priority6BufferSemaphore.setRed();
                priority6Buffer.add(activePortBufferEntry);
                priority6BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_5:
                priority5BufferSemaphore.setRed();
                priority5Buffer.add(activePortBufferEntry);
                priority5BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_4:
                priority4BufferSemaphore.setRed();
                priority4Buffer.add(activePortBufferEntry);
                priority4BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_3:
                priority3BufferSemaphore.setRed();
                priority3Buffer.add(activePortBufferEntry);
                priority3BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_2:
                priority2BufferSemaphore.setRed();
                priority2Buffer.add(activePortBufferEntry);
                priority2BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_1:
                priority1BufferSemaphore.setRed();
                priority1Buffer.add(activePortBufferEntry);
                priority1BufferSemaphore.setGreen();
                break;
            case TActivePort.WITHOUT_PRIORITY:
                priority0BufferSemaphore.setRed();
                priority0Buffer.add(activePortBufferEntry);
                priority0BufferSemaphore.setGreen();
                break;
            default:
                break;
        }
    }

    private int loadPacketPriority(TAbstractPDU packet) {
        if (packet.getType() == TAbstractPDU.TLDP) {
            return TActivePort.PRIORITY_10;
        }
        if (packet.getType() == TAbstractPDU.GPSRP) {
            return TActivePort.PRIORITY_9;
        }
        if (packet.getType() == TAbstractPDU.RLPRP) {
            return TActivePort.PRIORITY_8;
        }
        if (packet.getType() == TAbstractPDU.MPLS) {
            TMPLSPDU mplsPacket = (TMPLSPDU) packet;
            if (mplsPacket.getLabelStack().getTop().getLabel() == 1) {
                int EXP = mplsPacket.getLabelStack().getTop().getEXP();
                if (EXP == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_7;
                }
                if (EXP == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_6;
                }
                if (EXP == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_5;
                }
                if (EXP == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_4;
                }
                if (EXP == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_3;
                }
                if (EXP == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_2;
                }
                if (EXP == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_1;
                }
                if (EXP == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    return TActivePort.WITHOUT_PRIORITY;
                }
            } else {
                return TActivePort.WITHOUT_PRIORITY;
            }
        }
        if (packet.getType() == TAbstractPDU.IPV4) {
            if (packet.getIPv4Header().getOptionsField().isUsed()) {
                int gosLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
                if (gosLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_7;
                }
                if (gosLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_6;
                }
                if (gosLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_5;
                }
                if (gosLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_4;
                }
                if (gosLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_3;
                }
                if (gosLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_2;
                }
                if (gosLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_1;
                }
                if (gosLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    return TActivePort.WITHOUT_PRIORITY;
                }
            } else {
                return TActivePort.WITHOUT_PRIORITY;
            }
        }
        return TActivePort.WITHOUT_PRIORITY;
    }

    /**
     * This method put a new packet in the buffer of the active port. In fact,
     * this do the same than addPacket(p) method, but does not generates
     * simulation events but do iterator silently.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be inserted in the buffer of the active port.
     * @since 2.0
     */
    @Override
    public void reEnqueuePacket(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        TActivePortSet parentPortSetAux = (TActivePortSet) parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        semaphore.setRed();
        int packetOrder = ZERO;
        int packetPriority = loadPacketPriority(packet);
        packetOrder = rotaryIdentifierGenerator.getNextIdentifier();
        if (isUnlimitedBuffer) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
            addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
        } else {
            if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= (parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits())) {
                TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
                addPrioritizedBufferEntry(activePortBufferEntry);
                parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            } else {
                discardPacket(packet);
            }
        }
        semaphore.setGreen();
        parentPortSetAux.portSetSemaphore.setGreen();
    }

    /**
     * This method reads an returns the next packet of the buffer according to
     * the buffer management policy of an active port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The read packet
     * @since 2.0
     */
    @Override
    public TAbstractPDU getPacket() {
        TActivePortSet parentPortSetAux = (TActivePortSet) parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        semaphore.setRed();
        doPrioritizedRoundRobinPacketSelection();
        if (nextPacketToBeRead != null) {
            packetRead = nextPacketToBeRead;
            if (!isUnlimitedBuffer) {
                parentPortSetAux.decreasePortSetOccupancySize(packetRead.getSize());
            }
            nextPacketToBeRead = null;
        }
        semaphore.setGreen();
        parentPortSetAux.portSetSemaphore.setGreen();
        // Added to be 100% compatible to the same method of TFIFOPort
        if (packetRead == null) {
            throw new NoSuchElementException();
        }
        //**************
        return packetRead;
    }

    /**
     * This method compute whether iterator is possible or not to switch the
     * next packet in the buffer having the number of octects (specified as an
     * argument) that the port can switch in the current moment.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param octets The number of octects that the active port can switch in
     * this moment.
     * @return TRUE, if we can switch the next packet of the buffer at this
     * moment. Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public boolean canSwitchPacket(int octets) {
        semaphore.setRed();
        doPrioritizedRoundRobinPacketSelection();
        if (nextPacketToBeRead != null) {
            packetRead = nextPacketToBeRead;
            semaphore.setGreen();
            if (packetRead.getSize() <= octets) {
                return true;
            }
        }
        semaphore.setGreen();
        return false;
    }

    /**
     * This method computes the congestion level of the active port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A number, without decimals, between 0 and 100, which will be the
     * congestion level as a percentage.
     * @since 2.0
     */
    @Override
    public long getCongestionLevel() {
        if (isUnlimitedBuffer) {
            return ZERO;
        }
        TActivePortSet parentPortSetAux = (TActivePortSet) parentPortSet;
        long congestion = (parentPortSetAux.getPortSetOccupancy() * ONE_HUNDRED) / (parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits());
        return congestion;
    }

    /**
     * This method checks whether there is a packet in te buffer waiting to be
     * switched/routed, or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is a packet waiting to be switched/routed.
     * Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public boolean thereIsAPacketWaiting() {
        if (priority10Buffer.size() > ZERO) {
            return true;
        }
        if (priority9Buffer.size() > ZERO) {
            return true;
        }
        if (priority8Buffer.size() > ZERO) {
            return true;
        }
        if (priority7Buffer.size() > ZERO) {
            return true;
        }
        if (priority6Buffer.size() > ZERO) {
            return true;
        }
        if (priority5Buffer.size() > ZERO) {
            return true;
        }
        if (priority4Buffer.size() > ZERO) {
            return true;
        }
        if (priority3Buffer.size() > ZERO) {
            return true;
        }
        if (priority2Buffer.size() > ZERO) {
            return true;
        }
        if (priority1Buffer.size() > ZERO) {
            return true;
        }
        if (priority0Buffer.size() > ZERO) {
            return true;
        }
        return nextPacketToBeRead != null;
    }

    /**
     * This method computes and returns the number of octets that are currently
     * used by packets in the buffer of the active port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size, in octects, used by packets in the buffer of the active
     * port.
     * @since 2.0
     */
    @Override
    public long getOccupancy() {
        if (isUnlimitedBuffer) {
            semaphore.setRed();
            int occupancyAux = ZERO;
            TAbstractPDU packet = null;
            TActivePortBufferEntry activePortBufferEntry = null;
            priority10BufferSemaphore.setRed();
            Iterator<TActivePortBufferEntry> iterator = priority10Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority10BufferSemaphore.setGreen();
            priority9BufferSemaphore.setRed();
            iterator = priority9Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority9BufferSemaphore.setGreen();
            priority8BufferSemaphore.setRed();
            iterator = priority8Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority8BufferSemaphore.setGreen();
            priority7BufferSemaphore.setRed();
            iterator = priority7Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority7BufferSemaphore.setGreen();
            priority6BufferSemaphore.setRed();
            iterator = priority6Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority6BufferSemaphore.setGreen();
            priority5BufferSemaphore.setRed();
            iterator = priority5Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority5BufferSemaphore.setGreen();
            priority4BufferSemaphore.setRed();
            iterator = priority4Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority4BufferSemaphore.setGreen();
            priority3BufferSemaphore.setRed();
            iterator = priority3Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority3BufferSemaphore.setGreen();
            priority2BufferSemaphore.setRed();
            iterator = priority2Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority2BufferSemaphore.setGreen();
            priority1BufferSemaphore.setRed();
            iterator = priority1Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority1BufferSemaphore.setGreen();
            priority0BufferSemaphore.setRed();
            iterator = priority0Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            priority0BufferSemaphore.setGreen();
            if (nextPacketToBeRead != null) {
                occupancyAux += nextPacketToBeRead.getSize();
            }
            semaphore.setGreen();
            return occupancyAux;
        }
        TActivePortSet parentPortSetAux = (TActivePortSet) parentPortSet;
        return parentPortSetAux.getPortSetOccupancy();
    }

    /**
     * This method computes and returns the number of packets stored in the
     * buffer of the active port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The total number of packets stored in the buffer of the active
     * port.
     * @since 2.0
     */
    @Override
    public int getNumberOfPackets() {
        int numPackets = ZERO;
        numPackets += priority10Buffer.size();
        numPackets += priority9Buffer.size();
        numPackets += priority8Buffer.size();
        numPackets += priority7Buffer.size();
        numPackets += priority6Buffer.size();
        numPackets += priority5Buffer.size();
        numPackets += priority4Buffer.size();
        numPackets += priority3Buffer.size();
        numPackets += priority2Buffer.size();
        numPackets += priority1Buffer.size();
        numPackets += priority0Buffer.size();
        if (nextPacketToBeRead != null) {
            numPackets++;
        }
        return numPackets;
    }

    /**
     * This method reset attributes of the class as when created by the
     * constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    @Override
    public void reset() {
        semaphore.setRed();
        priority10BufferSemaphore.setRed();
        Iterator<TActivePortBufferEntry> iterator = priority10Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority10BufferSemaphore.setGreen();
        priority9BufferSemaphore.setRed();
        iterator = priority9Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority9BufferSemaphore.setGreen();
        priority8BufferSemaphore.setRed();
        iterator = priority8Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority8BufferSemaphore.setGreen();
        priority7BufferSemaphore.setRed();
        iterator = priority7Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority7BufferSemaphore.setGreen();
        priority6BufferSemaphore.setRed();
        iterator = priority6Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority6BufferSemaphore.setGreen();
        priority5BufferSemaphore.setRed();
        iterator = priority5Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority5BufferSemaphore.setGreen();
        priority4BufferSemaphore.setRed();
        iterator = priority4Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority4BufferSemaphore.setGreen();
        priority3BufferSemaphore.setRed();
        iterator = priority3Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority3BufferSemaphore.setGreen();
        priority2BufferSemaphore.setRed();
        iterator = priority2Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority2BufferSemaphore.setGreen();
        priority1BufferSemaphore.setRed();
        iterator = priority1Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority1BufferSemaphore.setGreen();
        priority0BufferSemaphore.setRed();
        iterator = priority0Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        priority0BufferSemaphore.setGreen();
        semaphore.setGreen();
        packetRead = null;
        selectedBuffer = ZERO;
        nextPacketToBeRead = null;
        int i;
        for (i = ZERO; i < MAX_PRIORITIZED_BUFFERS; i++) {
            currentReadsOfBuffer[i] = ZERO;
        }
    }

    private final TSemaphore priority0BufferSemaphore;
    private final TSemaphore priority1BufferSemaphore;
    private final TSemaphore priority2BufferSemaphore;
    private final TSemaphore priority3BufferSemaphore;
    private final TSemaphore priority4BufferSemaphore;
    private final TSemaphore priority5BufferSemaphore;
    private final TSemaphore priority6BufferSemaphore;
    private final TSemaphore priority7BufferSemaphore;
    private final TSemaphore priority8BufferSemaphore;
    private final TSemaphore priority9BufferSemaphore;
    private final TSemaphore priority10BufferSemaphore;
    private final TreeSet<TActivePortBufferEntry> priority0Buffer;
    private final TreeSet<TActivePortBufferEntry> priority1Buffer;
    private final TreeSet<TActivePortBufferEntry> priority2Buffer;
    private final TreeSet<TActivePortBufferEntry> priority3Buffer;
    private final TreeSet<TActivePortBufferEntry> priority4Buffer;
    private final TreeSet<TActivePortBufferEntry> priority5Buffer;
    private final TreeSet<TActivePortBufferEntry> priority6Buffer;
    private final TreeSet<TActivePortBufferEntry> priority7Buffer;
    private final TreeSet<TActivePortBufferEntry> priority8Buffer;
    private final TreeSet<TActivePortBufferEntry> priority9Buffer;
    private final TreeSet<TActivePortBufferEntry> priority10Buffer;

    private int selectedBuffer;
    private TAbstractPDU packetRead;
    private boolean isUnlimitedBuffer;
    private final TRotaryIDGenerator rotaryIdentifierGenerator;
    private final int[] maxReadsOfBuffer;
    private final int[] currentReadsOfBuffer;
    private TAbstractPDU nextPacketToBeRead;
    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TActivePort.class);

    private static final int HIGHEST_PRIORITY = 10;
    private static final int PRIORITY_10 = 10;
    private static final int PRIORITY_9 = 9;
    private static final int PRIORITY_8 = 8;
    private static final int PRIORITY_7 = 7;
    private static final int PRIORITY_6 = 6;
    private static final int PRIORITY_5 = 5;
    private static final int PRIORITY_4 = 4;
    private static final int PRIORITY_3 = 3;
    private static final int PRIORITY_2 = 2;
    private static final int PRIORITY_1 = 1;
    private static final int WITHOUT_PRIORITY = 0;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int SIX = 6;
    private static final int SEVEN = 7;
    private static final int EIGHT = 8;
    private static final int NINE = 9;
    private static final int TEN = 10;
    private static final int ELEVEN = 11;
    private static final int MAX_PRIORITIZED_BUFFERS = ELEVEN;
    private static final int EPCD_THRESHOLD = 100;
    private static final int ONE_HUNDRED = 100;
    private static final int DEFAULT_SELECTED_BUFFER = 0;
    private static final TAbstractPDU DEFAULT_PACKET_READ = null;
    private static final boolean DEFAULT_IS_UNLIMITED_BUFFER = false;
}
