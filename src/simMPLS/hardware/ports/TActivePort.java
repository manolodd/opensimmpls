package simMPLS.hardware.ports;

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.scenario.TSEPacketReceived;
import simMPLS.scenario.TStats;
import simMPLS.scenario.TNode;
import simMPLS.protocols.TPDU;
import simMPLS.protocols.TPDUMPLS;
import simMPLS.utils.TRotaryIDGenerator;
import simMPLS.utils.TMonitor;

/**
 * This class implements an active port. Active ports will be available in
 * active nodes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TActivePort extends TPort {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TActivePort.
     *
     * @since 1.0
     * @param portNumber port number (the port identifier used to distinguish
     * this port of other in the same active parentNode).
     * @param parentSetOfActivePorts A reference to the parent set of active
     * ports this active port belongs to.
     */
    public TActivePort(TNodePorts parentSetOfActivePorts, int portNumber) {
        super(parentSetOfActivePorts, portNumber);
        this.packetRead = null;
        this.unlimitedBuffer = false;
        this.incomingOrder = new TRotaryIDGenerator();
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
     * Read the proposal "Guarantee of Service (gosLevel) support over MPLS
     * using active techniques" to know more about these 11 types of traffic.
     * Summarizing, an active port acts as a single port, but maintains 11
     * buffers with increased priority from 0 to 10. The port executes a
     * prioritized Round Robin algorithm. Y goes on buffer by buffer avoiding
     * undefined relegation of packets but assuring that more prioritized
     * packets (those in higher buffers) will be handled before less prioritized
     * ones. In fact, due to some studies done before designing this proposal,
     * if there are available packets, the port will read 10 packets from
     * prioritized buffer 10, 9 from prioritized buffer 9, 8 from prioritized
     * buffer 8, and so on. So, imagine that there are enough packets in each
     * buffer, then in a complete Prioritized Round Robin cicle the ports should
     * read: 11 packets from buffer of priority 10; 10 packets from buffer of
     * priority 9; 9 packets from buffer of priority 8; 8 packets from buffer of
     * priority 7; 7 packets from buffer of priority 6; 6 packets from buffer of
     * priority 5; 5 packets from buffer of priority 4; 4 packets from buffer of
     * priority 3; 3 packets from buffer of priority 2; 2 packets from buffer of
     * priority 1; 1 packets from buffer of priority 0.
     *
     * This method prepares the next read that follow this scheme. And, in case
     * of existing only best effort traffic without gosLevel requirements, the
     * port works as a traditional one, dispatching one packet per cicle
     * followin a FIFO paradigm.
     *
     * @since 1.0
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
     * return its priority according to the "Guarante of Service (gosLevel)
     * support over MPLS using active techniques". This information is embedded
     * in the packet itself. Read the proposal to know more about prioritized
     * packets.
     *
     * @since 1.0
     * @return -1, if there are not packet to read (the port is empty).
     * Otherwise, it return a value between 0 and 10 (inclusive) which is the
     * priority of the next packet to be read from the port.
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
     * ideal one (unlimited space on it). FALSE, on the contrary.
     * @since 1.0
     */
    @Override
    public void setUnlimitedBuffer(boolean unlimitedBuffer) {
        this.unlimitedBuffer = unlimitedBuffer;
    }

    /**
     * This method discard the packet passed as an argument from the buffer.
     *
     * @param packet The packet to be discarded from the buffer.
     * @since 1.0
     */
    @Override
    public void discardPacket(TPDU packet) {
        this.getPortSet().getNode().discardPacket(packet);
    }

    /**
     * This method put a new packet in the buffer of the active port.
     *
     * @param packet Packet to be inserted in the buffer of the active port.
     * @since 1.0
     */
    @Override
    public void addPacket(TPDU packet) {
        TActiveNodePorts parentPortSetAux = (TActiveNodePorts) this.parentPortSet;
        parentPortSetAux.portSetMonitor.lock();
        monitor.lock();
        TNode parentNode = this.parentPortSet.getNode();
        long eventID = 0;
        int packetOrder = 0;
        int priority = this.loadPacketPriority(packet);
        try {
            eventID = parentNode.gILargo.getNextID();
            packetOrder = this.incomingOrder.getNextID();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int packetSubtype = packet.getSubtype();
        if (this.unlimitedBuffer) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(priority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSEPacketReceived packetReceivedEvent = new TSEPacketReceived(parentNode, eventID, this.getPortSet().getNode().getAvailableTime(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (this.getPortSet().getNode().getStats() != null) {
                this.getPortSet().getNode().getStats().addStatsEntry(packet, TStats.ENTRADA);
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
     * Service Support (gosLevel) over MPLS using Active Techniques". Read the
     * proposal to know more about EPCD algorithm.
     *
     * Summarizing, EPCD maintains a fixed thresshold free in the buffer of the
     * active port. If the packet is going to be discarded, this small space
     * allow the algorithm to retain some bytes of the packet header that are
     * codified as expressed in the gosLevel proposal. Using these bytes, this
     * method request the parent node to request retransmission of the packet,
     * if needed, to other active nodes.
     *
     * @param packet The packet that is going to be inserted in the buffer
     * through EPCD algoritm.
     * @return TRUE, if the packet can be inserted in the buffer. If this is the
     * case, the packet is inserted in the same operation. If the packet should
     * be discarded because a buffer overflow, the method returns FALSE and the
     * EPCD algorithm request the parent node to request the packet
     * retransmission.
     */
    public boolean runEarlyPacketCatchAndDiscard(TPDU packet) {
        TActiveNodePorts parentPortSetAux = (TActiveNodePorts) parentPortSet;
        long eventID = 0;
        int packetOrder = 0;
        int packetPriority = this.loadPacketPriority(packet);
        TNode parentNode = this.parentPortSet.getNode();
        try {
            eventID = parentNode.gILargo.getNextID();
            packetOrder = this.incomingOrder.getNextID();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int packetSubtype = packet.getSubtype();
        if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= ((parentPortSetAux.getBufferSizeInMB() * 1024 * 1024) - EPCD_THRESHOLD)) {
            TActivePortBufferEntry activePortBufferEntry = new TActivePortBufferEntry(packetPriority, packetOrder, packet);
            this.addPrioritizedBufferEntry(activePortBufferEntry);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSEPacketReceived packetReceivedEvent = new TSEPacketReceived(parentNode, eventID, this.getPortSet().getNode().getAvailableTime(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (this.getPortSet().getNode().getStats() != null) {
                this.getPortSet().getNode().getStats().addStatsEntry(packet, TStats.ENTRADA);
            }
            return true;
        } else {
            if (packet.getSubtype() == TPDU.MPLS_GOS) {
                parentPortSetAux.getNode().runGoSPDUStoreAndRetransmitProtocol((TPDUMPLS) packet, this.portID);
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

    private int loadPacketPriority(TPDU packet) {
        if (packet.getType() == TPDU.TLDP) {
            return TActivePort.PRIORITY_10;
        }
        if (packet.getType() == TPDU.GPSRP) {
            return TActivePort.PRIORITY_9;
        }
        if (packet.getType() == TPDU.RLPRP) {
            return TActivePort.PRIORITY_8;
        }
        if (packet.getType() == TPDU.MPLS) {
            TPDUMPLS mplsPacket = (TPDUMPLS) packet;
            if (mplsPacket.getLabelStack().getTop().getLabelField() == 1) {
                int EXP = mplsPacket.getLabelStack().getTop().getEXPField();
                if (EXP == TPDU.EXP_LEVEL3_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_7;
                }
                if (EXP == TPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_6;
                }
                if (EXP == TPDU.EXP_LEVEL2_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_5;
                }
                if (EXP == TPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_4;
                }
                if (EXP == TPDU.EXP_LEVEL1_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_3;
                }
                if (EXP == TPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_2;
                }
                if (EXP == TPDU.EXP_LEVEL0_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_1;
                }
                if (EXP == TPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    return TActivePort.WITHOUT_PRIORITY;
                }
            } else {
                return TActivePort.WITHOUT_PRIORITY;
            }
        }
        if (packet.getType() == TPDU.IPV4) {
            if (packet.getHeader().getOptionsField().isUsed()) {
                int gosLevel = packet.getHeader().getOptionsField().getEncodedGoSLevel();
                if (gosLevel == TPDU.EXP_LEVEL3_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_7;
                }
                if (gosLevel == TPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_6;
                }
                if (gosLevel == TPDU.EXP_LEVEL2_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_5;
                }
                if (gosLevel == TPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_4;
                }
                if (gosLevel == TPDU.EXP_LEVEL1_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_3;
                }
                if (gosLevel == TPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) {
                    return TActivePort.PRIORITY_2;
                }
                if (gosLevel == TPDU.EXP_LEVEL0_WITH_BACKUP_LSP) {
                    return TActivePort.PRIORITY_1;
                }
                if (gosLevel == TPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    return TActivePort.WITHOUT_PRIORITY;
                }
            } else {
                return TActivePort.WITHOUT_PRIORITY;
            }
        }
        return TActivePort.WITHOUT_PRIORITY;
    }

    /**
     * Este m�todo inserta un packet en el buffer de recepci�n del puerto. Es
     * igual que el m�todo addPacket(), salvo que no genera eventos y lo hace
     * silenciosamente.
     *
     * @param packet El packet que queremos que reciba el puerto.
     * @since 1.0
     */
    @Override
    public void reEnqueuePacket(TPDU packet) {
        TActiveNodePorts cjtoPuertosAux = (TActiveNodePorts) parentPortSet;
        cjtoPuertosAux.portSetMonitor.lock();
        monitor.lock();
        TNode nt = this.parentPortSet.getNode();
        long idEvt = 0;
        int idOrden = 0;
        int prior = this.loadPacketPriority(packet);
        try {
            idEvt = nt.gILargo.getNextID();
            idOrden = this.incomingOrder.getNextID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tipo = packet.getSubtype();
        if (this.unlimitedBuffer) {
            TActivePortBufferEntry epa = new TActivePortBufferEntry(prior, idOrden, packet);
            addPrioritizedBufferEntry(epa);
            cjtoPuertosAux.increasePortSetOccupancy(packet.getSize());
        } else {
            if ((cjtoPuertosAux.getPortSetOccupancy() + packet.getSize()) <= (cjtoPuertosAux.getBufferSizeInMB() * 1024 * 1024)) {
                TActivePortBufferEntry epa = new TActivePortBufferEntry(prior, idOrden, packet);
                addPrioritizedBufferEntry(epa);
                cjtoPuertosAux.increasePortSetOccupancy(packet.getSize());
            } else {
                this.discardPacket(packet);
            }
        }
        monitor.unLock();
        cjtoPuertosAux.portSetMonitor.unLock();
    }

    /**
     * este m�todo lee un paquete del buffer de recepci�n del puerto. El paquete
     * leido depender� del algoritmo de gesti�n de los b�fferes que implemente
     * el puerto. Por defecto, es un FIFO con prioridad por packetSubtype de
     * paquetes.
     *
     * @return El paquete le�do.
     * @since 1.0
     */
    @Override
    public TPDU getPacket() {
        TActiveNodePorts cjtoPuertosAux = (TActiveNodePorts) parentPortSet;
        cjtoPuertosAux.portSetMonitor.lock();
        monitor.lock();
        doPrioritizedRoundRobinPacketSelection();
        if (nextPacketToBeRead != null) {
            packetRead = nextPacketToBeRead;
            if (!this.unlimitedBuffer) {
                cjtoPuertosAux.decreasePortSetOccupancySize(packetRead.getSize());
            }
            nextPacketToBeRead = null;
        }
        monitor.unLock();
        cjtoPuertosAux.portSetMonitor.unLock();
        return packetRead;
    }

    /**
     * Este m�todo calcula si podemos conmutar el siguiente paquete del
     * parentNode, dado el n�mero de octetos que como mucho podemos conmutar en
     * un momento dado.
     *
     * @param octetos El n�mero de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso
     * contrario.
     * @since 1.0
     */
    @Override
    public boolean canSwitchPacket(int octetos) {
        monitor.lock();
        doPrioritizedRoundRobinPacketSelection();
        if (nextPacketToBeRead != null) {
            packetRead = nextPacketToBeRead;
            monitor.unLock();
            if (packetRead.getSize() <= octetos) {
                return true;
            }
        }
        monitor.unLock();
        return false;
    }

    /**
     * Este m�todo obtiene la congesti�n total el puerto, en porcentaje.
     *
     * @return El porcentaje de ocupaci�n del puerto.
     * @since 1.0
     */
    @Override
    public long getCongestionLevel() {
        if (this.unlimitedBuffer) {
            return 0;
        }
        TActiveNodePorts tpn = (TActiveNodePorts) parentPortSet;
        long cong = (tpn.getPortSetOccupancy() * 100) / (tpn.getBufferSizeInMB() * 1024 * 1024);
        return cong;
    }

    /**
     * Este m�todo comprueba si hay paquetes esperando en el buffer de recepci�n
     * o no.
     *
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso
     * contrario.
     * @since 1.0
     */
    @Override
    public boolean thereIsAPacketWaiting() {
        if (priority10Buffer.size() > 0) {
            return true;
        }
        if (priority9Buffer.size() > 0) {
            return true;
        }
        if (priority8Buffer.size() > 0) {
            return true;
        }
        if (priority7Buffer.size() > 0) {
            return true;
        }
        if (priority6Buffer.size() > 0) {
            return true;
        }
        if (priority5Buffer.size() > 0) {
            return true;
        }
        if (priority4Buffer.size() > 0) {
            return true;
        }
        if (priority3Buffer.size() > 0) {
            return true;
        }
        if (priority2Buffer.size() > 0) {
            return true;
        }
        if (priority1Buffer.size() > 0) {
            return true;
        }
        if (priority0Buffer.size() > 0) {
            return true;
        }
        if (nextPacketToBeRead != null) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo calcula el total de octetos que suman los paquetes que
     * actualmente hay en el buffer de recepci�n del puerto.
     *
     * @return El tama�o en octetos del total de paquetes en el buffer de
     * recepci�n.
     * @since 1.0
     */
    @Override
    public long getOccupancy() {
        if (this.unlimitedBuffer) {
            this.monitor.lock();
            int ocup = 0;
            TPDU paquete = null;
            TActivePortBufferEntry epa = null;

            this.priority10Monitor.lock();
            Iterator it = this.priority10Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority10Monitor.unLock();

            this.priority9Monitor.lock();
            it = this.priority9Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority9Monitor.unLock();

            this.priority8Monitor.lock();
            it = this.priority8Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority8Monitor.unLock();

            this.priority7Monitor.lock();
            it = this.priority7Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority7Monitor.unLock();

            this.priority6Monitor.lock();
            it = this.priority6Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority6Monitor.unLock();

            this.priority5Monitor.lock();
            it = this.priority5Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority5Monitor.unLock();

            this.priority4Monitor.lock();
            it = this.priority4Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority4Monitor.unLock();

            this.priority3Monitor.lock();
            it = this.priority3Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority3Monitor.unLock();

            this.priority2Monitor.lock();
            it = this.priority2Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority2Monitor.unLock();

            this.priority1Monitor.lock();
            it = this.priority1Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority1Monitor.unLock();

            this.priority0Monitor.lock();
            it = this.priority0Buffer.iterator();
            while (it.hasNext()) {
                epa = (TActivePortBufferEntry) it.next();
                paquete = epa.getPacket();
                if (paquete != null) {
                    ocup += paquete.getSize();
                }
            }
            this.priority0Monitor.unLock();

            if (nextPacketToBeRead != null) {
                ocup += nextPacketToBeRead.getSize();
            }
            this.monitor.unLock();
            return ocup;
        }
        TActiveNodePorts tpn = (TActiveNodePorts) parentPortSet;
        return tpn.getPortSetOccupancy();
    }

    /**
     * Este m�todo calcula el n�mero de paquetes total que hay en el buffer del
     * puerto.
     *
     * @return El n�mero total de paquetes que hay en el puerto.
     * @since 1.0
     */
    @Override
    public int getNumberOfPackets() {
        int numP = 0;
        numP += priority10Buffer.size();
        numP += priority9Buffer.size();
        numP += priority8Buffer.size();
        numP += priority7Buffer.size();
        numP += priority6Buffer.size();
        numP += priority5Buffer.size();
        numP += priority4Buffer.size();
        numP += priority3Buffer.size();
        numP += priority2Buffer.size();
        numP += priority1Buffer.size();
        numP += priority0Buffer.size();
        if (nextPacketToBeRead != null) {
            numP++;
        }
        return numP;
    }

    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser
     * creados por el constructor.
     *
     * @since 1.0
     */
    @Override
    public void reset() {
        this.monitor.lock();

        this.priority10Monitor.lock();
        Iterator it = this.priority10Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority10Monitor.unLock();

        this.priority9Monitor.lock();
        it = this.priority9Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority9Monitor.unLock();

        this.priority8Monitor.lock();
        it = this.priority8Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority8Monitor.unLock();

        this.priority7Monitor.lock();
        it = this.priority7Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority7Monitor.unLock();

        this.priority6Monitor.lock();
        it = this.priority6Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority6Monitor.unLock();

        this.priority5Monitor.lock();
        it = this.priority5Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority5Monitor.unLock();

        this.priority4Monitor.lock();
        it = this.priority4Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority4Monitor.unLock();

        this.priority3Monitor.lock();
        it = this.priority3Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority3Monitor.unLock();

        this.priority2Monitor.lock();
        it = this.priority2Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority2Monitor.unLock();

        this.priority1Monitor.lock();
        it = this.priority1Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority1Monitor.unLock();

        this.priority0Monitor.lock();
        it = this.priority0Buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.priority0Monitor.unLock();

        this.monitor.unLock();
        packetRead = null;
        selectedBuffer = 0;
        nextPacketToBeRead = null;
        int i;
        for (i = 0; i < 11; i++) {
            currentReadsOfBuffer[i] = 0;
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
    private TPDU packetRead;
    private boolean unlimitedBuffer;
    private TRotaryIDGenerator incomingOrder;
    private int[] maxReadsOfBuffer;
    private int[] currentReadsOfBuffer;
    private TPDU nextPacketToBeRead;
}
