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
package com.manolodominguez.opensimmpls.scenario;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;

/**
 * This class implements a TLinkBufferEntry that allow simulation of the transit
 * delay of a packet through the link.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLinkBufferEntry implements Comparable {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TLinkBufferEntry.
     *
     * @param packet A packet that start its travel through the link towards the
     * corresponding end node.
     * @param totalTransitDelay The total delay the packet will require to reach
     * the target end node. Usually it is the link delay.
     * @param endNode TLink.TAIL_END_NODE or TLink.HEAD_END_NODE, depending on
     * whether the target node is connected to the tail end of the link or to
     * the head end, respectively. Links are full duplex.
     * @since 2.0
     */
    public TLinkBufferEntry(TAbstractPDU packet, long totalTransitDelay, int endNode) {
        this.remainingTransitDelay = totalTransitDelay;
        this.initialTotalTransitDelay = totalTransitDelay;
        this.packet = packet;
        this.endNode = endNode;
    }

    /**
     * This method allows the comparation between the current TLinkBufferEntry
     * and another passed out as an argument. It is used to sort different
     * entries.
     *
     * @param anotherLinkBufferEntry Another instance of TLinkBufferEntry.
     * @return -1, 0 or 1, depending on whether the anotherLinkBufferEntry is
     * lesser, equal or greater than the current entry.
     * @since 2.0
     */
    @Override
    public int compareTo(Object anotherLinkBufferEntry) {
        TLinkBufferEntry linkBufferEntry = (TLinkBufferEntry) anotherLinkBufferEntry;
        TAbstractPDU anotherPacket = linkBufferEntry.getPacket();
        return this.packet.compareTo(anotherPacket);
    }

    /**
     * Este m�todo obtiene el paquete de la entrada del buffer.
     *
     * @return Paquete de la entrada del buffer.
     * @since 2.0
     */
    public TAbstractPDU getPacket() {
        return this.packet;
    }

    /**
     * Este m�todo devuelve el total de tiempo que el paquete debe esperar en el
     * enlace.
     *
     * @return El tiempo que el nodo debe esperar en el enlace desde que entra
     * hasta que llega al destino.
     * @since 2.0
     */
    public long getTotalTransitDelay() {
        return this.initialTotalTransitDelay;
    }

    /**
     * Este m�todo permte poner un paquete en la entrada el buffer.
     *
     * @param packet Paquete que deseamos insertar en la entrada del buffer.
     * @since 2.0
     */
    public void setPacket(TAbstractPDU packet) {
        this.packet = packet;
    }

    /**
     * Este m�todo permite establecer el tiempo que el paquete deber� esperar en
     * el enlace desde que entra hasta que llega al destino.
     *
     * @param totalTransitDelay Tiempo de espera total para el paquete.
     * @since 2.0
     */
    public void setTotalTransitDelay(long totalTransitDelay) {
        this.remainingTransitDelay = totalTransitDelay;
        this.initialTotalTransitDelay = totalTransitDelay;
    }

    /**
     * Este m�todo permite obtener el tiempo que a�n le queda al paquete para
     * llegar al destino.
     *
     * @return Tiempo (en nanosegundos) que le falta al paquete para llegar al
     * destino.
     * @since 2.0
     */
    public long getRemainingTransitDelay() {
        return this.remainingTransitDelay;
    }

    /**
     * Este m�todo resta al tiempo de espera del paquete en la entrada del
     * buffer, el tiempo que va transcurriendo. Al llegar a cero, el paquete
     * habr� llegado al destino.
     *
     * @param step Tiempo que se le resta al tiempo de espera del paquete.
     * @since 2.0
     */
    public void substractStepDelay(long step) {
        this.remainingTransitDelay -= step;
        if (this.remainingTransitDelay < 0) {
            this.remainingTransitDelay = 0;
        }
    }

    /**
     * Este m�todo permite establecer, para esta entrada concreta del buffer,
     * cu�l de los nodos extremos es el destino.
     *
     * @param endNode 1 � 2, dependiendo de si el paquete va dirigido al extremo
     * 1 o al extremo2 el enlace.
     * @since 2.0
     */
    public void setEndNode(int endNode) {
        this.endNode = endNode;
    }

    /**
     * Este m�todo permite obtener el destino del tr�fico, esto es, qu� nodo es
     * el que debe recibir el paquete.
     *
     * @return 1 o 2, dependiendo de si el tr�fico debe salir por el extremo 1 o
     * el extremo 2 del enlace.
     * @since 2.0
     */
    public int getEndNode() {
        return this.endNode;
    }

    private TAbstractPDU packet;
    private int endNode;
    private long remainingTransitDelay;
    private long initialTotalTransitDelay;
}
