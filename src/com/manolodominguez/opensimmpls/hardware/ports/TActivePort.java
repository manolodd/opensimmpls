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

import java.util.Iterator;
import java.util.TreeSet;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketReceived;
import com.manolodominguez.opensimmpls.scenario.TStats;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.utils.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.utils.TMonitor;

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
     * @param portID port number (the port id used to distinguish
 this port of other in the same active parentNode).
     * @param parentSetOfActivePorts A reference to the parent set of active
     * ports this active port belongs to.
     */
    public TActivePort(TPortSet parentSetOfActivePorts, int portID) {
        super(parentSetOfActivePorts, portID);
        this.packetRead = null;
        this.isUnlimitedBuffer = false;
        this.rotaryIdentifierGenerator = new TRotaryIDGenerator();
        this.priority0Monitor = new TMonitor();
        this.priority1Monitor = new TMonitor();
        this.priority2Monitor = new TMonitor();
        this.priority3Monitor = new TMonitor();
        this.priority4Monitor = new TMonitor();
        this.priority5Monitor = new TMonitor();
        this.priority6Monitor = new TMonitor();
        this.priority7Monitor = new TMonitor();
        this.priority8Monitor = new TMonitor();
        this.priority9Monitor = new TMonitor();
        this.priority10Monitor = new TMonitor();

        this.priority0Buffer = new TreeSet();
        this.priority1Buffer = new TreeSet();
        this.priority2Buffer = new TreeSet();
        this.priority3Buffer = new TreeSet();
        this.priority4Buffer = new TreeSet();
        this.priority5Buffer = new TreeSet();
        this.priority6Buffer = new TreeSet();
        this.priority7Buffer = new TreeSet();
        this.priority8Buffer = new TreeSet();
        this.priority9Buffer = new TreeSet();
        this.priority10Buffer = new TreeSet();

        this.selectedBuffer = 0;
        this.nextPacketToBeRead = null;
        this.maxReadsOfBuffer = new int[11];
        this.currentReadsOfBuffer = new int[11];
        int i;
        for (i = 0; i < 11; i++) {
            this.maxReadsOfBuffer[i] = i + 1;
            this.currentReadsOfBuffer[i] = 0;
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
        int numberOfEmptyBuffers = 0;
        int numberOfBuffersAlreadyRead = 0;
        Iterator iterator = null;
        TActivePortBufferEntry activePortBufferEntry = null;
        if (this.nextPacketToBeRead == null) {
            while ((!end) && (numberOfEmptyBuffers < 12)) {
                if (this.selectedBuffer == 10) {
                    if (this.priority10Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[10] < this.maxReadsOfBuffer[10]) {
                            this.priority10Monitor.lock();
                            iterator = priority10Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority10Monitor.unLock();
                            this.currentReadsOfBuffer[10]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[10] = this.maxReadsOfBuffer[10];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 9) {
                    if (this.priority9Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[9] < this.maxReadsOfBuffer[9]) {
                            this.priority9Monitor.lock();
                            iterator = this.priority9Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority9Monitor.unLock();
                            this.currentReadsOfBuffer[9]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[9] = this.maxReadsOfBuffer[9];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 8) {
                    if (this.priority8Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[8] < this.maxReadsOfBuffer[8]) {
                            this.priority8Monitor.lock();
                            iterator = this.priority8Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority8Monitor.unLock();
                            this.currentReadsOfBuffer[8]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[8] = this.maxReadsOfBuffer[8];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 7) {
                    if (this.priority7Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[7] < this.maxReadsOfBuffer[7]) {
                            this.priority7Monitor.lock();
                            iterator = this.priority7Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority7Monitor.unLock();
                            this.currentReadsOfBuffer[7]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[7] = this.maxReadsOfBuffer[7];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 6) {
                    if (this.priority6Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[6] < this.maxReadsOfBuffer[6]) {
                            this.priority6Monitor.lock();
                            iterator = this.priority6Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority6Monitor.unLock();
                            this.currentReadsOfBuffer[6]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[6] = this.maxReadsOfBuffer[6];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 5) {
                    if (this.priority5Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[5] < this.maxReadsOfBuffer[5]) {
                            this.priority5Monitor.lock();
                            iterator = this.priority5Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority5Monitor.unLock();
                            this.currentReadsOfBuffer[5]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[5] = this.maxReadsOfBuffer[5];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 4) {
                    if (this.priority4Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[4] < this.maxReadsOfBuffer[4]) {
                            this.priority4Monitor.lock();
                            iterator = this.priority4Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority4Monitor.unLock();
                            this.currentReadsOfBuffer[4]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[4] = this.maxReadsOfBuffer[4];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 3) {
                    if (this.priority3Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[3] < this.maxReadsOfBuffer[3]) {
                            this.priority3Monitor.lock();
                            iterator = this.priority3Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority3Monitor.unLock();
                            this.currentReadsOfBuffer[3]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[3] = this.maxReadsOfBuffer[3];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 2) {
                    if (this.priority2Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[2] < this.maxReadsOfBuffer[2]) {
                            this.priority2Monitor.lock();
                            iterator = this.priority2Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority2Monitor.unLock();
                            this.currentReadsOfBuffer[2]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[2] = this.maxReadsOfBuffer[2];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 1) {
                    if (this.priority1Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[1] < this.maxReadsOfBuffer[1]) {
                            this.priority1Monitor.lock();
                            iterator = this.priority1Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority1Monitor.unLock();
                            this.currentReadsOfBuffer[1]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[1] = this.maxReadsOfBuffer[1];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                } else if (this.selectedBuffer == 0) {
                    if (this.priority0Buffer.size() > 0) {
                        if (this.currentReadsOfBuffer[0] < this.maxReadsOfBuffer[0]) {
                            this.priority0Monitor.lock();
                            iterator = this.priority0Buffer.iterator();
                            if (iterator.hasNext()) {
                                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                                this.nextPacketToBeRead = activePortBufferEntry.getPacket();
                                iterator.remove();
                            }
                            this.priority0Monitor.unLock();
                            this.currentReadsOfBuffer[0]++;
                            end = true;
                        } else {
                            numberOfBuffersAlreadyRead++;
                        }
                    } else {
                        this.currentReadsOfBuffer[0] = this.maxReadsOfBuffer[0];
                        numberOfBuffersAlreadyRead++;
                        numberOfEmptyBuffers++;
                    }
                }
                this.selectedBuffer = ((this.selectedBuffer + 1) % 11);
                if (numberOfBuffersAlreadyRead >= 11) {
                    int i;
                    for (i = 0; i < 11; i++) {
                        this.currentReadsOfBuffer[i] = 0;
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
        this.monitor.lock();
        this.doPrioritizedRoundRobinPacketSelection();
        if (this.nextPacketToBeRead != null) {
            priorityAux = this.loadPacketPriority(this.nextPacketToBeRead);
            this.monitor.unLock();
            return priorityAux;
        }
        this.monitor.unLock();
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
        parentPortSetAux.portSetMonitor.lock();
        monitor.lock();
        TNode parentNode = this.parentPortSet.getParentNode();
        long eventID = 0;
        int packetOrder = 0;
        int priority = this.loadPacketPriority(packet);
        try {
            eventID = parentNode.longIdentifierGenerator.getNextID();
            packetOrder = this.rotaryIdentifierGenerator.getNextID();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int packetSubtype = packet.getSubtype();
        if (this.isUnlimitedBuffer) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(priority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, this.getPortSet().getParentNode().getCurrentInstant(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (this.getPortSet().getParentNode().getStats() != null) {
                this.getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.INCOMING);
            }
        } else {
            if (!this.runEarlyPacketCatchAndDiscard(packet)) {
                this.discardPacket(packet);
            }
        }
        monitor.unLock();
        parentPortSetAux.portSetMonitor.unLock();
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
            eventID = parentNode.longIdentifierGenerator.getNextID();
            packetOrder = this.rotaryIdentifierGenerator.getNextID();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int packetSubtype = packet.getSubtype();
        if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= ((parentPortSetAux.getBufferSizeInMBytes() * 1024 * 1024) - EPCD_THRESHOLD)) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, this.getPortSet().getParentNode().getCurrentInstant(), packetSubtype, packet.getSize());
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
        if (priorityAux == TActivePort.PRIORITY_10) {
            this.priority10Monitor.lock();
            this.priority10Buffer.add(activePortBufferEntry);
            this.priority10Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_9) {
            this.priority9Monitor.lock();
            this.priority9Buffer.add(activePortBufferEntry);
            this.priority9Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_8) {
            this.priority8Monitor.lock();
            this.priority8Buffer.add(activePortBufferEntry);
            this.priority8Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_7) {
            this.priority7Monitor.lock();
            this.priority7Buffer.add(activePortBufferEntry);
            this.priority7Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_6) {
            this.priority6Monitor.lock();
            this.priority6Buffer.add(activePortBufferEntry);
            this.priority6Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_5) {
            this.priority5Monitor.lock();
            this.priority5Buffer.add(activePortBufferEntry);
            this.priority5Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_4) {
            this.priority4Monitor.lock();
            this.priority4Buffer.add(activePortBufferEntry);
            this.priority4Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_3) {
            this.priority3Monitor.lock();
            this.priority3Buffer.add(activePortBufferEntry);
            this.priority3Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_2) {
            this.priority2Monitor.lock();
            this.priority2Buffer.add(activePortBufferEntry);
            this.priority2Monitor.unLock();
        } else if (priorityAux == TActivePort.PRIORITY_1) {
            this.priority1Monitor.lock();
            this.priority1Buffer.add(activePortBufferEntry);
            this.priority1Monitor.unLock();
        } else if (priorityAux == TActivePort.WITHOUT_PRIORITY) {
            this.priority0Monitor.lock();
            this.priority0Buffer.add(activePortBufferEntry);
            this.priority0Monitor.unLock();
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
        parentPortSetAux.portSetMonitor.lock();
        this.monitor.lock();
        TNode parentNode = this.parentPortSet.getParentNode();
        long eventID = 0;
        int packetOrder = 0;
        int packetPriority = this.loadPacketPriority(packet);
        try {
            eventID = parentNode.longIdentifierGenerator.getNextID();
            packetOrder = this.rotaryIdentifierGenerator.getNextID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int packetSubtype = packet.getSubtype();
        if (this.isUnlimitedBuffer) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
        } else {
            if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= (parentPortSetAux.getBufferSizeInMBytes() * 1024 * 1024)) {
                TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
                this.addPrioritizedBufferEntry(activePortBufferEntry);
                parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            } else {
                this.discardPacket(packet);
            }
        }
        this.monitor.unLock();
        parentPortSetAux.portSetMonitor.unLock();
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
        parentPortSetAux.portSetMonitor.lock();
        this.monitor.lock();
        this.doPrioritizedRoundRobinPacketSelection();
        if (this.nextPacketToBeRead != null) {
            this.packetRead = this.nextPacketToBeRead;
            if (!this.isUnlimitedBuffer) {
                parentPortSetAux.decreasePortSetOccupancySize(packetRead.getSize());
            }
            this.nextPacketToBeRead = null;
        }
        this.monitor.unLock();
        parentPortSetAux.portSetMonitor.unLock();
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
        this.monitor.lock();
        this.doPrioritizedRoundRobinPacketSelection();
        if (this.nextPacketToBeRead != null) {
            this.packetRead = this.nextPacketToBeRead;
            this.monitor.unLock();
            if (this.packetRead.getSize() <= octets) {
                return true;
            }
        }
        monitor.unLock();
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
            return 0;
        }
        TActivePortSet parentPortSetAux = (TActivePortSet) this.parentPortSet;
        long congestion = (parentPortSetAux.getPortSetOccupancy() * 100) / (parentPortSetAux.getBufferSizeInMBytes() * 1024 * 1024);
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
        if (this.priority10Buffer.size() > 0) {
            return true;
        }
        if (this.priority9Buffer.size() > 0) {
            return true;
        }
        if (this.priority8Buffer.size() > 0) {
            return true;
        }
        if (this.priority7Buffer.size() > 0) {
            return true;
        }
        if (this.priority6Buffer.size() > 0) {
            return true;
        }
        if (this.priority5Buffer.size() > 0) {
            return true;
        }
        if (this.priority4Buffer.size() > 0) {
            return true;
        }
        if (this.priority3Buffer.size() > 0) {
            return true;
        }
        if (this.priority2Buffer.size() > 0) {
            return true;
        }
        if (this.priority1Buffer.size() > 0) {
            return true;
        }
        if (this.priority0Buffer.size() > 0) {
            return true;
        }
        if (this.nextPacketToBeRead != null) {
            return true;
        }
        return false;
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
            this.monitor.lock();
            int occupancyAux = 0;
            TAbstractPDU packet = null;
            TActivePortBufferEntry activePortBufferEntry = null;

            this.priority10Monitor.lock();
            Iterator iterator = this.priority10Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority10Monitor.unLock();
            this.priority9Monitor.lock();
            iterator = this.priority9Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority9Monitor.unLock();
            this.priority8Monitor.lock();
            iterator = this.priority8Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority8Monitor.unLock();
            this.priority7Monitor.lock();
            iterator = this.priority7Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority7Monitor.unLock();
            this.priority6Monitor.lock();
            iterator = this.priority6Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority6Monitor.unLock();
            this.priority5Monitor.lock();
            iterator = this.priority5Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority5Monitor.unLock();
            this.priority4Monitor.lock();
            iterator = this.priority4Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority4Monitor.unLock();
            this.priority3Monitor.lock();
            iterator = this.priority3Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority3Monitor.unLock();
            this.priority2Monitor.lock();
            iterator = this.priority2Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority2Monitor.unLock();
            this.priority1Monitor.lock();
            iterator = this.priority1Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority1Monitor.unLock();
            this.priority0Monitor.lock();
            iterator = this.priority0Buffer.iterator();
            while (iterator.hasNext()) {
                activePortBufferEntry = (TActivePortBufferEntry) iterator.next();
                packet = activePortBufferEntry.getPacket();
                if (packet != null) {
                    occupancyAux += packet.getSize();
                }
            }
            this.priority0Monitor.unLock();
            if (this.nextPacketToBeRead != null) {
                occupancyAux += this.nextPacketToBeRead.getSize();
            }
            this.monitor.unLock();
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
        int numPackets = 0;
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
        this.monitor.lock();

        this.priority10Monitor.lock();
        Iterator iterator = this.priority10Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority10Monitor.unLock();
        this.priority9Monitor.lock();
        iterator = this.priority9Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority9Monitor.unLock();
        this.priority8Monitor.lock();
        iterator = this.priority8Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority8Monitor.unLock();
        this.priority7Monitor.lock();
        iterator = this.priority7Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority7Monitor.unLock();
        this.priority6Monitor.lock();
        iterator = this.priority6Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority6Monitor.unLock();
        this.priority5Monitor.lock();
        iterator = this.priority5Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority5Monitor.unLock();
        this.priority4Monitor.lock();
        iterator = this.priority4Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority4Monitor.unLock();
        this.priority3Monitor.lock();
        iterator = this.priority3Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority3Monitor.unLock();
        this.priority2Monitor.lock();
        iterator = this.priority2Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority2Monitor.unLock();
        this.priority1Monitor.lock();
        iterator = this.priority1Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority1Monitor.unLock();
        this.priority0Monitor.lock();
        iterator = this.priority0Buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.priority0Monitor.unLock();
        this.monitor.unLock();
        this.packetRead = null;
        this.selectedBuffer = 0;
        this.nextPacketToBeRead = null;
        int i;
        for (i = 0; i < 11; i++) {
            this.currentReadsOfBuffer[i] = 0;
        }
    }

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

    private static final int EPCD_THRESHOLD = 100;

    private TMonitor priority0Monitor;
    private TMonitor priority1Monitor;
    private TMonitor priority2Monitor;
    private TMonitor priority3Monitor;
    private TMonitor priority4Monitor;
    private TMonitor priority5Monitor;
    private TMonitor priority6Monitor;
    private TMonitor priority7Monitor;
    private TMonitor priority8Monitor;
    private TMonitor priority9Monitor;
    private TMonitor priority10Monitor;
    private TreeSet priority0Buffer;
    private TreeSet priority1Buffer;
    private TreeSet priority2Buffer;
    private TreeSet priority3Buffer;
    private TreeSet priority4Buffer;
    private TreeSet priority5Buffer;
    private TreeSet priority6Buffer;
    private TreeSet priority7Buffer;
    private TreeSet priority8Buffer;
    private TreeSet priority9Buffer;
    private TreeSet priority10Buffer;

    private int selectedBuffer;
    private TAbstractPDU packetRead;
    private boolean isUnlimitedBuffer;
    private TRotaryIDGenerator rotaryIdentifierGenerator;
    private int[] maxReadsOfBuffer;
    private int[] currentReadsOfBuffer;
    private TAbstractPDU nextPacketToBeRead;
}
