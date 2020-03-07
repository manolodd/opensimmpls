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
import java.util.LinkedList;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketReceived;
import com.manolodominguez.opensimmpls.scenario.TStats;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import static com.manolodominguez.opensimmpls.commons.UnitsTranslations.OCTETS_PER_MEGABYTE;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a I/O port that follow a FIFO scheme to dispatch
 * packets.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TFIFOPort extends TPort {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TFIFOPort.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param portID the identifier of the port. This is the unique identifier
     * ti distinguish the port within the parent port set.
     * @param parentPortSet A reference to the parent port set this port belongs
     * to.
     */
    public TFIFOPort(TPortSet parentPortSet, int portID) {
        super(parentPortSet, portID);
        translations = ResourceBundle.getBundle(AvailableBundles.T_FIFO_PORT.getPath());
        if (portID < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        if (parentPortSet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        } else {
            if (portID >= parentPortSet.getNumberOfPorts()) {
                logger.error(translations.getString("argumentOutOfRange"));
                throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
            }
        }
        buffer = new LinkedList<>();
        packetRead = null;
        isUnlimitedBuffer = false;
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
        isUnlimitedBuffer = unlimitedBuffer;
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
     * This method put a new packet in the buffer of the port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet Packet to be inserted in the buffer of the port.
     * @since 2.0
     */
    @Override
    public void addPacket(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        TFIFOPortSet parentPortSetAux = (TFIFOPortSet) parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        semaphore.setRed();
        TNode parentNode = parentPortSet.getParentNode();
        long eventID = ZERO;
        try {
            eventID = parentNode.eventIdentifierGenerator.getNextIdentifier();
        } catch (EIDGeneratorOverflow ex) {
            logger.error(ex.getMessage(), ex);
        }
        int packetSubtype = packet.getSubtype();
        if (isUnlimitedBuffer) {
            buffer.addLast(packet);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
            TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, getPortSet().getParentNode().getCurrentTimeInstant(), packetSubtype, packet.getSize());
            parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
            if (getPortSet().getParentNode().getStats() != null) {
                getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.INCOMING);
            }
        } else {
            if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= (parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits())) {
                buffer.addLast(packet);
                parentPortSetAux.increasePortSetOccupancy(packet.getSize());
                TSimulationEventPacketReceived packetReceivedEvent = new TSimulationEventPacketReceived(parentNode, eventID, getPortSet().getParentNode().getCurrentTimeInstant(), packetSubtype, packet.getSize());
                parentNode.simulationEventsListener.captureSimulationEvents(packetReceivedEvent);
                if (getPortSet().getParentNode().getStats() != null) {
                    getPortSet().getParentNode().getStats().addStatEntry(packet, TStats.INCOMING);
                }
            } else {
                discardPacket(packet);
            }
        }
        semaphore.setGreen();
        parentPortSetAux.portSetSemaphore.setGreen();
    }

    /**
     * This method put a new packet in the buffer of the port. In fact, this do
     * the same than addPacket(p) method, but does not generates simulation
     * events but do it silently.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be inserted in the buffer of the port.
     * @since 2.0
     */
    @Override
    public void reEnqueuePacket(TAbstractPDU packet) {
        if (packet == null) {
            logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException(translations.getString("badArgument"));
        }
        TFIFOPortSet parentPortSetAux = (TFIFOPortSet) parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        semaphore.setRed();
        if (isUnlimitedBuffer) {
            buffer.addLast(packet);
            parentPortSetAux.increasePortSetOccupancy(packet.getSize());
        } else {
            if ((parentPortSetAux.getPortSetOccupancy() + packet.getSize()) <= (parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits())) {
                buffer.addLast(packet);
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
     * FIFO policy. If there is not a packet to be read from the buffer, then a
     * NoSuchElementException is thrown.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The read packet
     * @since 2.0
     */
    @Override
    public TAbstractPDU getPacket() {
        TFIFOPortSet parentPortSetAux = (TFIFOPortSet) parentPortSet;
        parentPortSetAux.portSetSemaphore.setRed();
        semaphore.setRed();
        try {
            packetRead = buffer.removeFirst();
            if (!isUnlimitedBuffer) {
                parentPortSetAux.decreasePortSetOccupancySize(packetRead.getSize());
            }
        } catch (NoSuchElementException e) {
            logger.error(translations.getString("elementDoesNotExist"));
            throw e;
        }
        semaphore.setGreen();
        parentPortSetAux.portSetSemaphore.setGreen();
        return packetRead;
    }

    /**
     * This method compute whether it is possible or not to switch the next
     * packet in the buffer having the number of octets (specified as an
     * argument) that the port can switch in the current moment.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param switchableOctets The number of octets that the port can switch in
     * this moment.
     * @return TRUE, if we can switch the next packet of the buffer at this
     * moment. Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public boolean canSwitchPacket(int switchableOctets) {
        if (switchableOctets < ZERO) {
            logger.error(translations.getString("argumentOutOfRange"));
            throw new IllegalArgumentException(translations.getString("argumentOutOfRange"));
        }
        semaphore.setRed();
        packetRead = buffer.getFirst();
        semaphore.setGreen();
        return packetRead.getSize() <= switchableOctets;
    }

    /**
     * This method computes the congestion level of the port.
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
        TFIFOPortSet parentPortSetAux = (TFIFOPortSet) parentPortSet;
        long congestion = (parentPortSetAux.getPortSetOccupancy() * ONE_HUNDRED) / (parentPortSetAux.getBufferSizeInMBytes() * OCTETS_PER_MEGABYTE.getUnits());
        return congestion;
    }

    /**
     * This method checks whether there is a packet in the buffer waiting to be
     * switched/routed, or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if there is a packet waiting to be switched/routed.
     * Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public boolean thereIsAPacketWaiting() {
        return buffer.size() > ZERO;
    }

    /**
     * This method computes and returns the number of octets that are currently
     * used by packets in the buffer of the port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size, in octects, used by packets in the buffer of the port.
     * @since 2.0
     */
    @Override
    public long getOccupancy() {
        if (isUnlimitedBuffer) {
            semaphore.setRed();
            int occupancy = ZERO;
            TAbstractPDU packet = null;
            Iterator<TAbstractPDU> iterator = buffer.iterator();
            while (iterator.hasNext()) {
                packet = iterator.next();
                if (packet != null) {
                    occupancy += packet.getSize();
                }
            }
            semaphore.setGreen();
            return occupancy;
        }
        TFIFOPortSet parentPortSetAux = (TFIFOPortSet) parentPortSet;
        return parentPortSetAux.getPortSetOccupancy();
    }

    /**
     * This method computes and returns the number of packets stored in the
     * buffer of the port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The total number of packets stored in the buffer of the port.
     * @since 2.0
     */
    @Override
    public int getNumberOfPackets() {
        return buffer.size();
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
        Iterator<TAbstractPDU> iterator = buffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        semaphore.setGreen();
    }

    private LinkedList<TAbstractPDU> buffer;
    private TAbstractPDU packetRead;
    private boolean isUnlimitedBuffer;
    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TFIFOPort.class);

    private static final int ZERO = 0;
    private static final int ONE_HUNDRED = 100;
}
