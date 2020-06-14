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

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;

/**
 * This class implement an active port buffer entry. It is needed to prioritize
 * some packets, because of their embedded priority, within the buffer.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TActivePortBufferEntry implements Comparable<TActivePortBufferEntry> {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TActivePortBufferEntry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param priority packet priority. Embedded on it as defined by "Guarante
     * of Service (GoS)Support over MPLS using Active Techniques". Read this
     * proposal to know more about GoS priorities.
     * @param incomingOrder The incoming ordet to the buffer. To be used when
     * following a FIFO packet dispatching.
     * @param packet The packet itself.
     */
    public TActivePortBufferEntry(int priority, int incomingOrder, TAbstractPDU packet) {
        this.priority = priority;
        this.incomingOrder = incomingOrder;
        this.packet = packet;
    }

    /**
     * This method compares the current instance with other specified as an
     * argument. It is used to store TActivePortBufferEntry objects in a
     * collection.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherActivePortBufferEntry Otra entrada de un puerto activo con
     * la que se quiere comparar la instancia actual.
     * @return -1, 0 or 1, depending on whether the current instance incoming
     * order is lower, equal or greater than the one specified as an argument.
     * @since 2.0
     */
    @Override
    public int compareTo(TActivePortBufferEntry anotherActivePortBufferEntry) {
        if (this.incomingOrder < anotherActivePortBufferEntry.getIncomingOrder()) {
            return TActivePortBufferEntry.THIS_LOWER;
        }
        if (this.incomingOrder > anotherActivePortBufferEntry.getIncomingOrder()) {
            return TActivePortBufferEntry.THIS_GREATER;
        }
        return TActivePortBufferEntry.THIS_EQUAL;
    }

    /**
     * This method returns the priority of the packets stored in this active
     * port buffer entry. This priority has to be interpreted as defined by
     * "Guarante of Service (GoS)Support over MPLS using Active Techniques".
     * Read this proposal to know more about GoS priorities.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return Priority of the packet embedded in this active port buffer entry.
     * A number between 0 (no priority) and 10 (maximum priority).
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * This method returns the incoming order of the packet embedded in this
     * active port buffer entry. It is the incoming order of the packet in the
     * parent buffer.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return The incoming order of the embedded packet to the active port
     * parent buffer.
     */
    public int getIncomingOrder() {
        return this.incomingOrder;
    }

    /**
     * This method returns the packet embedded in this active port buffer entry.
     *
     * @return The packet itself.
     * @since 2.0
     */
    public TAbstractPDU getPacket() {
        return this.packet;
    }

    private static final int THIS_LOWER = -1;
    private static final int THIS_GREATER = 1;
    private static final int THIS_EQUAL = 0;

    private int priority;
    private int incomingOrder;
    private TAbstractPDU packet;
}
