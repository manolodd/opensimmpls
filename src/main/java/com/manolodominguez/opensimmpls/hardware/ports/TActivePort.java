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
        this.packetRead = DEFAULT_PACKET_READ;
        this.isUnlimitedBuffer = DEFAULT_IS_UNLIMITED_BUFFER;
        this.rotaryIdentifierGenerator = new TRotaryIDGenerator();
        this.priority0BufferSemaphore = new TSemaphore();
        this.priority1BufferSemaphore = new TSemaphore();
        this.priority2BufferSemaphore = new TSemaphore();
        this.priority3BufferSemaphore = new TSemaphore();
        this.priority4BufferSemaphore = new TSemaphore();
        this.priority5BufferSemaphore = new TSemaphore();
        this.priority6BufferSemaphore = new TSemaphore();
        this.priority7BufferSemaphore = new TSemaphore();
        this.priority8BufferSemaphore = new TSemaphore();
        this.priority9BufferSemaphore = new TSemaphore();
        this.priority10BufferSemaphore = new TSemaphore();
        this.priority0Buffer = new TreeSet<>();
        this.priority1Buffer = new TreeSet<>();
        this.priority2Buffer = new TreeSet<>();
        this.priority3Buffer = new TreeSet<>();
        this.priority4Buffer = new TreeSet<>();
        this.priority5Buffer = new TreeSet<>();
        this.priority6Buffer = new TreeSet<>();
        this.priority7Buffer = new TreeSet<>();
        this.priority8Buffer = new TreeSet<>();
        this.priority9Buffer = new TreeSet<>();
        this.priority10Buffer = new TreeSet<>();
        this.selectedBuffer = DEFAULT_SELECTED_BUFFER;
        this.nextPacketToBeRead = null;
        this.maxReadsOfBuffer = new int[(HIGHEST_PRIORITY + ONE)];
        this.currentReadsOfBuffer = new int[(HIGHEST_PRIORITY + ONE)];
        int i;
        for (i = ZERO; i <= HIGHEST_PRIORITY; i++) {
            this.maxReadsOfBuffer[i] = i + ONE;
            this.currentReadsOfBuffer[i] = ZERO;
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
        Iterator iterator = null;
        TActivePortBufferEntry activePortBufferEntry = null;
        if (this.nextPacketToBeRead == null) {
            while ((!end) && (numberOfEmptyBuffers <= MAX_PRIORITIZED_BUFFERS)) {
                switch (this.selectedBuffer) {
                    case TEN:
                        if (this.priority10Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[TEN] < this.maxReadsOfBuffer[TEN]) {
                                this.priority10BufferSemaphore.setRed();
                                iterator = priority10Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority10BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[TEN]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[TEN] = this.maxReadsOfBuffer[TEN];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case NINE:
                        if (this.priority9Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[NINE] < this.maxReadsOfBuffer[NINE]) {
                                this.priority9BufferSemaphore.setRed();
                                iterator = this.priority9Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority9BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[NINE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[NINE] = this.maxReadsOfBuffer[NINE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case EIGHT:
                        if (this.priority8Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[EIGHT] < this.maxReadsOfBuffer[EIGHT]) {
                                this.priority8BufferSemaphore.setRed();
                                iterator = this.priority8Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority8BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[EIGHT]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[EIGHT] = this.maxReadsOfBuffer[EIGHT];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case SEVEN:
                        if (this.priority7Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[SEVEN] < this.maxReadsOfBuffer[SEVEN]) {
                                this.priority7BufferSemaphore.setRed();
                                iterator = this.priority7Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority7BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[SEVEN]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[SEVEN] = this.maxReadsOfBuffer[SEVEN];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case SIX:
                        if (this.priority6Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[SIX] < this.maxReadsOfBuffer[SIX]) {
                                this.priority6BufferSemaphore.setRed();
                                iterator = this.priority6Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority6BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[SIX]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[SIX] = this.maxReadsOfBuffer[SIX];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case FIVE:
                        if (this.priority5Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[FIVE] < this.maxReadsOfBuffer[FIVE]) {
                                this.priority5BufferSemaphore.setRed();
                                iterator = this.priority5Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority5BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[FIVE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[FIVE] = this.maxReadsOfBuffer[FIVE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case FOUR:
                        if (this.priority4Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[FOUR] < this.maxReadsOfBuffer[FOUR]) {
                                this.priority4BufferSemaphore.setRed();
                                iterator = this.priority4Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority4BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[FOUR]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[FOUR] = this.maxReadsOfBuffer[FOUR];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case THREE:
                        if (this.priority3Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[THREE] < this.maxReadsOfBuffer[THREE]) {
                                this.priority3BufferSemaphore.setRed();
                                iterator = this.priority3Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority3BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[THREE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[THREE] = this.maxReadsOfBuffer[THREE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case TWO:
                        if (this.priority2Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[TWO] < this.maxReadsOfBuffer[TWO]) {
                                this.priority2BufferSemaphore.setRed();
                                iterator = this.priority2Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority2BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[TWO]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[TWO] = this.maxReadsOfBuffer[TWO];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case ONE:
                        if (this.priority1Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[ONE] < this.maxReadsOfBuffer[ONE]) {
                                this.priority1BufferSemaphore.setRed();
                                iterator = this.priority1Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority1BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[ONE]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[ONE] = this.maxReadsOfBuffer[ONE];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    case ZERO:
                        if (this.priority0Buffer.size() > ZERO) {
                            if (this.currentReadsOfBuffer[ZERO] < this.maxReadsOfBuffer[ZERO]) {
                                this.priority0BufferSemaphore.setRed();
                                iterator = this.priority0Buffer.iterator();
                                if (iterator.hasNext()) {
                                    activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                    this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                    iterator.remove();
                                }
                                this.priority0BufferSemaphore.setGreen();
                                this.currentReadsOfBuffer[ZERO]++;
                                end = true;
                            } else {
                                numberOfBuffersAlreadyRead++;
                            }
                        } else {
                            this.currentReadsOfBuffer[ZERO] = this.maxReadsOfBuffer[ZERO];
                            numberOfBuffersAlreadyRead++;
                            numberOfEmptyBuffers++;
                        }
                        break;
                    default:
                        break;
                }
                this.selectedBuffer = ((this.selectedBuffer + ONE) % MAX_PRIORITIZED_BUFFERS);
                if (numberOfBuffersAlreadyRead >= MAX_PRIORITIZED_BUFFERS) {
                    int i;
                    for (i = ZERO; i < MAX_PRIORITIZED_BUFFERS; i++) {
                        this.currentReadsOfBuffer[i] = ZERO;
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
        this.monitor.setRed();
        this.doPrioritizedRoundRobinPacketSelection();
        if (this.nextPacketToBeRead != null) {
            priorityAux = this.loadPacketPriority(this.nextPacketToBeRead);
            this.monitor.setGreen();
            return priorityAux;
        }
        this.monitor.setGreen();
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
     * This method discard the packet passed as an argument from the buffer.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be discarded from the buffer.
     * @since 2.0
     */
    @Override
    public void discardPacket(TAbstractPDU packet) {
        this.getPortSet().getParentNode().discardPacket(packet);
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
        TActivePortSet parentPortSetAux = (TActivePortSet) this.parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        this.monitor.setRed();
        TNode parentNode = this.parentPortSet.getParentNode();
        long eventID = ZERO;
        int packetOrder = ZERO;
        int priority = this.loadPacketPriority(packet);
        try {
            eventID = parentNode.eventIdentifierGenerator.getNextIdentifier();
            packetOrder = this.rotaryIdentifierGenerator.getNextIdentifier();
        } catch (EIDGeneratorOverflow ex) {
            this.logger.error(ex.getMessage(), ex);
        }
        int packetSubtype = packet.getSubtype();
        if (this.isUnlimitedBuffer) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(priority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, this.getPortSet().getParentNode().getCurrentTimeInstant(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (this.getPortSet().getParentNode().getStats() != null) {
                this.getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.INCOMING);
            }
        } else {
            if (!this.runEarlyPacketCatchAndDiscard(packet)) {
                this.discardPacket(packet);
            }
        }
        this.monitor.setGreen();
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
        int packetPriority = this.loadPacketPriority(packet);
        TNode parentNode = this.parentPortSet.getParentNode();
        try {
            eventID = parentNode.eventIdentifierGenerator.getNextIdentifier();
            packetOrder = this.rotaryIdentifierGenerator.getNextIdentifier();
        } catch (EIDGeneratorOverflow ex) {
            this.logger.error(ex.getMessage(), ex);
        }
        int packetSubtype = packet.getSubtype();
        if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= ((parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits()) - EPCD_THRESHOLD)) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, this.getPortSet().getParentNode().getCurrentTimeInstant(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (this.getPortSet().getParentNode().getStats() != null) {
                this.getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.INCOMING);
            }
            return true;
        } else {
            if (packet.getSubtype() == TAbstractPDU.MPLS_GOS) {
                parentPortSetAux.getParentNode().runGPSRP((TMPLSPDU) packet, this.portID);
            }
        }
        return false;
    }

    private void addPrioritizedBufferEntry(TActivePortBufferEntry activePortBufferEntry) {
        int priorityAux = activePortBufferEntry.getPriority();
        switch (priorityAux) {
            case TActivePort.PRIORITY_10:
                this.priority10BufferSemaphore.setRed();
                this.priority10Buffer.add(activePortBufferEntry);
                this.priority10BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_9:
                this.priority9BufferSemaphore.setRed();
                this.priority9Buffer.add(activePortBufferEntry);
                this.priority9BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_8:
                this.priority8BufferSemaphore.setRed();
                this.priority8Buffer.add(activePortBufferEntry);
                this.priority8BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_7:
                this.priority7BufferSemaphore.setRed();
                this.priority7Buffer.add(activePortBufferEntry);
                this.priority7BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_6:
                this.priority6BufferSemaphore.setRed();
                this.priority6Buffer.add(activePortBufferEntry);
                this.priority6BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_5:
                this.priority5BufferSemaphore.setRed();
                this.priority5Buffer.add(activePortBufferEntry);
                this.priority5BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_4:
                this.priority4BufferSemaphore.setRed();
                this.priority4Buffer.add(activePortBufferEntry);
                this.priority4BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_3:
                this.priority3BufferSemaphore.setRed();
                this.priority3Buffer.add(activePortBufferEntry);
                this.priority3BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_2:
                this.priority2BufferSemaphore.setRed();
                this.priority2Buffer.add(activePortBufferEntry);
                this.priority2BufferSemaphore.setGreen();
                break;
            case TActivePort.PRIORITY_1:
                this.priority1BufferSemaphore.setRed();
                this.priority1Buffer.add(activePortBufferEntry);
                this.priority1BufferSemaphore.setGreen();
                break;
            case TActivePort.WITHOUT_PRIORITY:
                this.priority0BufferSemaphore.setRed();
                this.priority0Buffer.add(activePortBufferEntry);
                this.priority0BufferSemaphore.setGreen();
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
        TActivePortSet parentPortSetAux = (TActivePortSet) parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        this.monitor.setRed();
        int packetOrder = ZERO;
        int packetPriority = this.loadPacketPriority(packet);
        packetOrder = this.rotaryIdentifierGenerator.getNextIdentifier();
        if (this.isUnlimitedBuffer) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
        } else {
            if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= (parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits())) {
                TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
                this.addPrioritizedBufferEntry(activePortBufferEntry);
                parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            } else {
                this.discardPacket(packet);
            }
        }
        this.monitor.setGreen();
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
        TActivePortSet parentPortSetAux = (TActivePortSet) this.parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        this.monitor.setRed();
        this.doPrioritizedRoundRobinPacketSelection();
        if (this.nextPacketToBeRead != null) {
            this.packetRead = this.nextPacketToBeRead;
            if (!this.isUnlimitedBuffer) {
                parentPortSetAux.decreasePortSetOccupancySize(this.packetRead.getSize());
            }
            this.nextPacketToBeRead = null;
        }
        this.monitor.setGreen();
        parentPortSetAux.portSetSemaphore.setGreen();
        return this.packetRead;
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
        this.monitor.setRed();
        this.doPrioritizedRoundRobinPacketSelection();
        if (this.nextPacketToBeRead != null) {
            this.packetRead = this.nextPacketToBeRead;
            this.monitor.setGreen();
            if (this.packetRead.getSize() <= octets) {
                return true;
            }
        }
        this.monitor.setGreen();
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
        if (this.isUnlimitedBuffer) {
            return ZERO;
        }
        TActivePortSet parentPortSetAux = (TActivePortSet) this.parentPortSet;
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
        if (this.priority10Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority9Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority8Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority7Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority6Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority5Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority4Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority3Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority2Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority1Buffer.size() > ZERO) {
            return true;
        }
        if (this.priority0Buffer.size() > ZERO) {
            return true;
        }
        return this.nextPacketToBeRead != null;
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
        if (this.isUnlimitedBuffer) {
            this.monitor.setRed();
            int occupancyAux = ZERO;
            TAbstractPDU packet = null;
            TActivePortBufferEntry activePortBufferEntry = null;
            this.priority10BufferSemaphore.setRed();
            Iterator iterator = this.priority10Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority10BufferSemaphore.setGreen();
            this.priority9BufferSemaphore.setRed();
            iterator = this.priority9Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority9BufferSemaphore.setGreen();
            this.priority8BufferSemaphore.setRed();
            iterator = this.priority8Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority8BufferSemaphore.setGreen();
            this.priority7BufferSemaphore.setRed();
            iterator = this.priority7Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority7BufferSemaphore.setGreen();
            this.priority6BufferSemaphore.setRed();
            iterator = this.priority6Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority6BufferSemaphore.setGreen();
            this.priority5BufferSemaphore.setRed();
            iterator = this.priority5Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority5BufferSemaphore.setGreen();
            this.priority4BufferSemaphore.setRed();
            iterator = this.priority4Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority4BufferSemaphore.setGreen();
            this.priority3BufferSemaphore.setRed();
            iterator = this.priority3Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority3BufferSemaphore.setGreen();
            this.priority2BufferSemaphore.setRed();
            iterator = this.priority2Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority2BufferSemaphore.setGreen();
            this.priority1BufferSemaphore.setRed();
            iterator = this.priority1Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority1BufferSemaphore.setGreen();
            this.priority0BufferSemaphore.setRed();
            iterator = this.priority0Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority0BufferSemaphore.setGreen();
            if (this.nextPacketToBeRead != null) {
                occupancyAux += this.nextPacketToBeRead.getSize();
            }
            this.monitor.setGreen();
            return occupancyAux;
        }
        TActivePortSet parentPortSetAux = (TActivePortSet) this.parentPortSet;
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
        numPackets += this.priority10Buffer.size();
        numPackets += this.priority9Buffer.size();
        numPackets += this.priority8Buffer.size();
        numPackets += this.priority7Buffer.size();
        numPackets += this.priority6Buffer.size();
        numPackets += this.priority5Buffer.size();
        numPackets += this.priority4Buffer.size();
        numPackets += this.priority3Buffer.size();
        numPackets += this.priority2Buffer.size();
        numPackets += this.priority1Buffer.size();
        numPackets += this.priority0Buffer.size();
        if (this.nextPacketToBeRead != null) {
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
        this.monitor.setRed();
        this.priority10BufferSemaphore.setRed();
        Iterator iterator = this.priority10Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority10BufferSemaphore.setGreen();
        this.priority9BufferSemaphore.setRed();
        iterator = this.priority9Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority9BufferSemaphore.setGreen();
        this.priority8BufferSemaphore.setRed();
        iterator = this.priority8Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority8BufferSemaphore.setGreen();
        this.priority7BufferSemaphore.setRed();
        iterator = this.priority7Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority7BufferSemaphore.setGreen();
        this.priority6BufferSemaphore.setRed();
        iterator = this.priority6Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority6BufferSemaphore.setGreen();
        this.priority5BufferSemaphore.setRed();
        iterator = this.priority5Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority5BufferSemaphore.setGreen();
        this.priority4BufferSemaphore.setRed();
        iterator = this.priority4Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority4BufferSemaphore.setGreen();
        this.priority3BufferSemaphore.setRed();
        iterator = this.priority3Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority3BufferSemaphore.setGreen();
        this.priority2BufferSemaphore.setRed();
        iterator = this.priority2Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority2BufferSemaphore.setGreen();
        this.priority1BufferSemaphore.setRed();
        iterator = this.priority1Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority1BufferSemaphore.setGreen();
        this.priority0BufferSemaphore.setRed();
        iterator = this.priority0Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority0BufferSemaphore.setGreen();
        this.monitor.setGreen();
        this.packetRead = null;
        this.selectedBuffer = ZERO;
        this.nextPacketToBeRead = null;
        int i;
        for (i = ZERO; i < MAX_PRIORITIZED_BUFFERS; i++) {
            this.currentReadsOfBuffer[i] = ZERO;
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
