/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package simMPLS.scenario;

import java.util.Iterator;
import simMPLS.protocols.TAbstractPDU;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.utils.EIDGeneratorOverflow;
import simMPLS.utils.TLongIDGenerator;

/**
 * This class implements an exterlan link of the topology (a link that is
 * outside the MPLS domain.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TExternalLink extends TLink implements ITimerEventListener, Runnable {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TExternalLink.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param linkID Unique identifier that identifies this link in the overall
     * topology.
     * @param longIDGenerator ID generator, to be used by this link to generate
     * distinguisible simulation events.
     * @param topology Topology this link belongs to.
     * @since 2.0
     */
    public TExternalLink(int linkID, TLongIDGenerator longIDGenerator, TTopology topology) {
        super(linkID, longIDGenerator, topology);
        this.stepLength = 0;
    }

    /**
     * Este m�todo devuelve el tipo el enlace.
     *
     * @return TLink.EXTERNAL, indicando que es un nodo externo.
     * @since 2.0
     */
    @Override
    public int getLinkType() {
        return TLink.EXTERNAL;
    }

    /**
     * Este m�todo recibe eventos de sincronizaci�n del reloj del simulador, que
     * lo sincroniza todo.
     *
     * @param timerEvent Evento de sincronizaci�n que el reloj del simulador
     * env�a a este enlace externo.
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent timerEvent) {
        this.setStepDuration(timerEvent.getStepDuration());
        this.setTimeInstant(timerEvent.getUpperLimit());
        this.stepLength = timerEvent.getStepDuration();
        this.startOperation();
    }

    /**
     * Este m�todo establece si el enlace se puede considerar como caido o no.
     *
     * @param linkIsBroken TRUE, indica que queremos que el enlace caiga. FALSE
     * indica que no lo queremos o que queremos que se levante si est� caido.
     * @since 2.0
     */
    @Override
    public void setAsBrokenLink(boolean linkIsBroken) {
        this.linkIsBroken = linkIsBroken;
        if (linkIsBroken) {
            try {
                this.generateSimulationEvent(new TSEBrokenLink(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
                this.packetsInTransitEntriesLock.lock();
                TAbstractPDU packet = null;
                TLinkBufferEntry bufferedPacketEntry = null;
                Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
                while (bufferedPacketEntriesIterator.hasNext()) {
                    bufferedPacketEntry = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
                    packet = bufferedPacketEntry.getPacket();
                    if (packet != null) {
                        // FIX: do not use harcoded values. Use constants class
                        // instead
                        if (bufferedPacketEntry.getTargetEnd() == 1) {
                            this.generateSimulationEvent(new TSEPacketDiscarded(this.getNodeAtEnd2(), this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                            // FIX: do not use harcoded values. Use constants class
                            // instead
                        } else if (bufferedPacketEntry.getTargetEnd() == 2) {
                            this.generateSimulationEvent(new TSEPacketDiscarded(this.getNodeAtEnd1(), this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), packet.getSubtype()));
                        }
                    }
                    bufferedPacketEntriesIterator.remove();
                }
                this.packetsInTransitEntriesLock.unLock();
            } catch (EIDGeneratorOverflow e) {
                // FIX: this is not a good practice
                e.printStackTrace();
            }
        } else {
            try {
                this.generateSimulationEvent(new TSELinkRecovered(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
            } catch (EIDGeneratorOverflow e) {
                // FIX: this is not a good practice
                e.printStackTrace();
            }
        }
    }

    /**
     * Este m�todo se ejecuta cuando el hilo principal del enlace externo se
     * ponne en funcionamiento. Es el n�cleo del enlace externo.
     *
     * @since 2.0
     */
    @Override
    public void run() {
        this.updateTransitDelay();
        this.advancePacketInTransit();
        this.deliverPacketsToDestination();
    }

    /**
     * Este m�todo toma todos los paquetes que en ese momento se encuentren
     * circulando por el enlace externo y los avanza por el mismo hacia su
     * destino.
     *
     * @since 2.0
     */
    public void updateTransitDelay() {
        this.packetsInTransitEntriesLock.lock();
        Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry bufferedPacketEntry = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
            bufferedPacketEntry.substractStepLength(this.stepLength);
            long transitPercentage = this.getTransitPercentage(bufferedPacketEntry.getTotalTransitDelay(), bufferedPacketEntry.getRemainingTransitDelay());
            // FIX: do not use harcoded values. Use constants class instead.
            if (bufferedPacketEntry.getTargetEnd() == 1) {
                // FIX: do not use harcoded values. Use constants class instead.
                transitPercentage = 100 - transitPercentage;
            }
            try {
                this.generateSimulationEvent(new TSEPacketOnFly(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), bufferedPacketEntry.getPacket().getSubtype(), transitPercentage));
            } catch (EIDGeneratorOverflow e) {
                // FIX: This is not a good practice.
                e.printStackTrace();
            }
        }
        this.packetsInTransitEntriesLock.unLock();
    }

    /**
     * Este m�todo toma todos los paquetes que se encuentren circulando por el
     * enlace externo y detecta todos aquellos que ya han llegado al destino.
     *
     * @since 2.0
     */
    public void advancePacketInTransit() {
        this.packetsInTransitEntriesLock.lock();
        Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry bufferedPacketEntry = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
            // FIX: Do not use harcoded values. Use constants class instead.
            if (bufferedPacketEntry.getRemainingTransitDelay() <= 0) {
                this.deliveredPacketEntriesLock.lock();
                this.deliveredPacketsBuffer.add(bufferedPacketEntry);
                this.deliveredPacketEntriesLock.unLock();
            }
        }
        bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry bufferedPacket = (TLinkBufferEntry) bufferedPacketEntriesIterator.next();
            // FIX: Do not use harcoded values. Use constants class instead.
            if (bufferedPacket.getRemainingTransitDelay() <= 0) {
                bufferedPacketEntriesIterator.remove();
            }
        }
        this.packetsInTransitEntriesLock.unLock();
    }

    /**
     * Este m�todo toma todos los paquetes que han llegado al destino y realiza
     * la insercio�n de los mismos en el puerto correspondiente de dicho
     * destino.
     *
     * @since 2.0
     */
    public void deliverPacketsToDestination() {
        this.deliveredPacketEntriesLock.lock();
        Iterator deliveredPacketEntriesIterator = this.deliveredPacketsBuffer.iterator();
        while (deliveredPacketEntriesIterator.hasNext()) {
            TLinkBufferEntry deliveredPacketEntry = (TLinkBufferEntry) deliveredPacketEntriesIterator.next();
            if (deliveredPacketEntry.getTargetEnd() == TLink.END_NODE_1) {
                TNode nodeAux = this.getNodeAtEnd1();
                nodeAux.putPacket(deliveredPacketEntry.getPacket(), this.getPortOfNodeAtEnd1());
            } else {
                TNode nodeAux = this.getNodeAtEnd2();
                nodeAux.putPacket(deliveredPacketEntry.getPacket(), this.getPortOfNodeAtEnd2());
            }
            deliveredPacketEntriesIterator.remove();
        }
        this.deliveredPacketEntriesLock.unLock();
    }

    /**
     * Este m�todo obtiene el weight del enlace externos que debe usar el
     * algoritmo de routing para calcular rutas.
     *
     * @return El weight del enlace. En el enlace externo es el retardo.
     * @since 2.0
     */
    @Override
    public long getWeight() {
        long weight = this.getDelay();
        return weight;
    }

    /**
     * Este m�todo devuelve si el enlace externo est� bien configurado o no.
     *
     * @return TRUE, si la configuraci�n actual del enlace es correcta. FALSE en
     * caso contrario.
     * @since 2.0
     */
    @Override
    public boolean isWellConfigured() {
        // FIX: This method should be used correclty. In fact this does not do
        // anything. Seems to be a mistake.
        return false;
    }

    /**
     * Este m�todo comprueba si el valor de todos los atributos configurables
     * del enlace externo es v�lido o no.
     *
     * @param topology Topolog�a dentro de la cual se encuentra este enlace
     * externo.
     * @return CORRECTA, si la configuraci�n es correcta. Un codigo de error en
     * caso contrario.
     * @since 2.0
     */
    public int isWellConfigured(TTopology topology) {
        // FIX: This method should be used correclty. In fact this does not do
        // anything. Seems to be a mistake.
        return 0;
    }

    /**
     * Este m�todo transforma en un mensaje legible el c�digo de error devuelto
     * por el m�todo <I>validateConfig(...)</I>
     *
     * @param errorCode El codigo de error que se quiere transformar.
     * @return El mensaje textual correspondiente a ese mensaje de error.
     * @since 2.0
     */
    @Override
    public String getErrorMessage(int errorCode) {
        return null;
    }

    /**
     * Este m�todo transforma el enlace externo en un representaci�n de texto
     * que se puede almacenar en disco sin problemas.
     *
     * @return El equivalente en texto del enlace externo completo.
     * @since 2.0
     */
    @Override
    public String marshall() {
        String serializedElement = "#EnlaceExterno#";
        serializedElement += this.getID();
        serializedElement += "#";
        serializedElement += this.getName().replace('#', ' ');
        serializedElement += "#";
        serializedElement += this.getShowName();
        serializedElement += "#";
        serializedElement += this.getDelay();
        serializedElement += "#";
        serializedElement += this.getNodeAtEnd1().getIPv4Address();
        serializedElement += "#";
        serializedElement += this.getPortOfNodeAtEnd1();
        serializedElement += "#";
        serializedElement += this.getNodeAtEnd2().getIPv4Address();
        serializedElement += "#";
        serializedElement += this.getPortOfNodeAtEnd2();
        serializedElement += "#";
        return serializedElement;
    }

    /**
     * Este m�todo toma la representaci�n textual de un enlace externo completo
     * y configura el objeto con los elementFields que obtiene.
     *
     * @param serializedLink Enlace externo en su representaci�n serializada.
     * @return TRUE, si se deserializa correctamente, FALSE en caso contrario.
     * @since 2.0
     */
    @Override
    public boolean unMarshall(String serializedLink) {
        TLinkConfig linkConfig = new TLinkConfig();
        String[] elementFields = serializedLink.split("#");
        // FIX: Do not use harcoded values. This affect to the entire method. 
        // Use class constants instead.
        if (elementFields.length != 10) {
            return false;
        }
        this.setLinkID(Integer.valueOf(elementFields[2]));
        linkConfig.setName(elementFields[3]);
        linkConfig.setShowName(Boolean.parseBoolean(elementFields[4]));
        linkConfig.setDelay(Integer.parseInt(elementFields[5]));
        String ipv4AddressOfNodeAtEND1 = elementFields[6];
        String ipv4AddressOfNodeAtEND2 = elementFields[8];
        TNode nodeAtEnd1 = this.getTopology().getNode(ipv4AddressOfNodeAtEND1);
        TNode nodeAtEnd2 = this.getTopology().getNode(ipv4AddressOfNodeAtEND2);
        if (!((nodeAtEnd1 == null) || (nodeAtEnd2 == null))) {
            linkConfig.setNameOfNodeAtEnd1(nodeAtEnd1.getName());
            linkConfig.setNameOfNodeAtEnd2(nodeAtEnd2.getName());
            linkConfig.setPortOfNodeAtEnd1(Integer.parseInt(elementFields[7]));
            linkConfig.setPortOfNodeAtEnd2(Integer.parseInt(elementFields[9]));
            linkConfig.discoverLinkType(this.topology);
        } else {
            return false;
        }
        this.configure(linkConfig, this.topology, false);
        return true;
    }

    /**
     * Este m�todo reinicia los atributos de la clase, dejando la instancia como
     * si se acabase de crear por el constructor.
     *
     * @since 2.0
     */
    @Override
    public void reset() {
        this.packetsInTransitEntriesLock.lock();
        Iterator bufferedPacketEntriesIterator = this.buffer.iterator();
        while (bufferedPacketEntriesIterator.hasNext()) {
            bufferedPacketEntriesIterator.next();
            bufferedPacketEntriesIterator.remove();
        }
        this.packetsInTransitEntriesLock.unLock();
        this.deliveredPacketEntriesLock.lock();
        Iterator deliveredPacketEntriesIterator = this.deliveredPacketsBuffer.iterator();
        while (deliveredPacketEntriesIterator.hasNext()) {
            deliveredPacketEntriesIterator.next();
            deliveredPacketEntriesIterator.remove();
        }
        this.deliveredPacketEntriesLock.unLock();
        this.setAsBrokenLink(false);
    }

    @Override
    public long getRABANWeight() {
        // There is no RABAN weight for external links, so, the usual weight is
        // returned.
        return this.getWeight();
    }

    private long stepLength;
}
