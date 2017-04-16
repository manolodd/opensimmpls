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
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.utils.EIDGeneratorOverflow;
import simMPLS.utils.TLongIDGenerator;


/**
 * Esta clase implementa un enlace de la topolog�a que ser� interno al dominio
 * MPLS.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TInternalLink extends TLink implements ITimerEventListener, Runnable {

    /**
     * Crea una nueva instancia de TEnlaceInterno
     * @param identificador Identificador �nico para este elemento en la topolog�a.
     * @param il Generador de identificadores para los eventos que genere este enlace externo.
     * @param t Topologia en la que se encuentra este enlace interno.
     * @since 2.0
     */
    public TInternalLink(int identificador, TLongIDGenerator il, TTopology t) {
        super(identificador, il, t);
        numberOfLSPs = 0;
        numberOfBackupLSPs = 0;
        stepLength = 0;
    }

    /**
     * Este m�todo devuelve el tipo el enlace.
     * @return TLink.INTERNAL, indicando que es un nodo interno.
     * @since 2.0
     */    
    @Override
    public int getLinkType() {
        return super.INTERNAL;
    }

    /**
     * Este m�todo recibe eventos de sincronizaci�n del reloj del simulador, que lo
     * sincroniza todo.
     * @param evt Evento de sincronizaci�n que el reloj del simulador env�a a este enlace interno.
     * @since 2.0
     */    
    @Override
    public void receiveTimerEvent(simMPLS.hardware.timer.TTimerEvent evt) {
        this.setStepDuration(evt.getStepDuration());
        this.setTimeInstant(evt.getUpperLimit());
        stepLength = evt.getStepDuration();
        this.startOperation();
    }

    /**
     * Este m�todo establece si el enlace se puede considerar como caido o no.
     * @param ec TRUE, indica que queremos que el enlace caiga. FALSE indica que no lo queremos o
     * que queremos que se levante si est� caido.
     * @since 2.0
     */    
    @Override
    public void setAsBrokenLink(boolean ec) {
        linkIsBroken = ec;
        if (ec) {
            try {
                this.numberOfLSPs = 0;
                this.numberOfBackupLSPs = 0;
                this.generateSimulationEvent(new TSEBrokenLink(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
                this.packetsInTransitEntriesLock.lock();
                TAbstractPDU paquete = null;
                TLinkBufferEntry ebe = null;
                Iterator it = this.buffer.iterator();
                while (it.hasNext()) {
                    ebe = (TLinkBufferEntry) it.next();
                    paquete = ebe.getPacket();
                    if (paquete != null) {
                        if (ebe.getTargetEnd() == 1) {
                            this.generateSimulationEvent(new TSEPacketDiscarded(this.getNodeAtEnd2(), this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } else if (ebe.getTargetEnd() == 2) {
                            this.generateSimulationEvent(new TSEPacketDiscarded(this.getNodeAtEnd1(), this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        }
                    }
                    it.remove();
                }
                this.packetsInTransitEntriesLock.unLock();
            } catch (EIDGeneratorOverflow e) {
                e.printStackTrace(); 
            }
        } else {
            try {
                this.generateSimulationEvent(new TSELinkRecovered(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
            } catch (EIDGeneratorOverflow e) {
                e.printStackTrace(); 
            }
        }
    }
    
    /**
     * Este m�todo se ejecuta cuando el hilo principal del enlace externo se ponne en
     * funcionamiento. Es el n�cleo del enlace interno.
     * @since 2.0
     */    
    @Override
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        this.actualizarTiemposDeEspera();
        this.adelantarPaquetesEnTransito();
        this.pasarPaquetesADestino();
        // Acciones a llevar a cabo durante el tic.
    }

    /**
     * Este m�todo comprueba si sobre este enlace se ha establecido alg�n LSP.
     * @return TRUE, si se ha establecido alg�n LSP. FALSE en caso contrario.
     * @since 2.0
     */    
    public boolean tieneLSPs() {
        if (numberOfLSPs > 0)
            return true;
        return false;
    }

    /**
     * Este m�todo a�ade un LSP sobre este enlace.
     * @since 2.0
     */    
    public void setLSPUp() {
        numberOfLSPs++;
        try {
            this.generateSimulationEvent(new TSELSPEstablished(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    /**
     * Este m�todo quita un LSP establecido sobre este enlace.
     * @since 2.0
     */    
    public void removeLSP() {
        if (numberOfLSPs > 0) {
            numberOfLSPs--;
            try {
                this.generateSimulationEvent(new TSELSPRemoved(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }
    }

    /**
     * Este m�todo comprueba si sobre este enlace se ha establecido alg�n LSP de
     * respaldo.
     * @return TRUE, si se ha establecido alg�n LSP de respaldo. FALSE en caso contrario.
     * @since 2.0
     */    
    public boolean tieneLSPsDeBackup() {
        if (numberOfBackupLSPs > 0)
            return true;
        return false;
    }

    /**
     * Este m�todo a�ade un LSP de respaldo sobre este enlace.
     * @since 2.0
     */    
    public void setBackupLSP() {
        numberOfBackupLSPs++;
    }

    /**
     * Este m�todo quita un LSP de respaldo establecido sobre este enlace.
     * @since 2.0
     */    
    public void setBackupLSPDown() {
        if (numberOfBackupLSPs > 0)
            numberOfBackupLSPs--;
    }

    /**
     * Este m�todo toma todos los paquetes que en ese momento se encuentren circulando
     * por el enlace interno y los avanza por el mismo hacia su destino.
     * @since 2.0
     */    
    public void actualizarTiemposDeEspera() {
        packetsInTransitEntriesLock.lock();
        Iterator it = buffer.iterator();
        while (it.hasNext()) {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            ebe.substractStepLength(stepLength);
            long pctj = this.getTransitPercentage(ebe.getTotalTransitDelay(), ebe.getRemainingTransitDelay());
            if (ebe.getTargetEnd() == 1)
                pctj = 100 - pctj;
            try {
                if (ebe.getPacket().getType() == TAbstractPDU.TLDP) {
                    this.generateSimulationEvent(new TSEPacketOnFly(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, pctj));
                } else if (ebe.getPacket().getType() == TAbstractPDU.MPLS) {
                    this.generateSimulationEvent(new TSEPacketOnFly(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), ebe.getPacket().getSubtype(), pctj));
                } else if (ebe.getPacket().getType() == TAbstractPDU.GPSRP) {
                    this.generateSimulationEvent(new TSEPacketOnFly(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, pctj));
                }
            } catch (EIDGeneratorOverflow e) {
                e.printStackTrace(); 
            }
        }
        packetsInTransitEntriesLock.unLock();
    }

    /**
     * Este m�todo toma todos los paquetes que se encuentren circulando por el enlace
     * interno y detecta todos aquellos que ya han llegado al destino.
     * @since 2.0
     */    
    public void adelantarPaquetesEnTransito() {
        packetsInTransitEntriesLock.lock();
        Iterator it = buffer.iterator();
        while (it.hasNext()) {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            if (ebe.getRemainingTransitDelay() <= 0) {
                this.deliveredPacketEntriesLock.lock();
                deliveredPacketsBuffer.add(ebe);
                this.deliveredPacketEntriesLock.unLock();
            }
        }
        it = buffer.iterator();
        while (it.hasNext()) {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            if (ebe.getRemainingTransitDelay() <= 0)
                it.remove();
        }
        packetsInTransitEntriesLock.unLock();
    }

    /**
     * Este m�todo toma todos los paquetes que han llegado al destino y realiza la
     * insercio�n de los mismos en el puerto correspondiente de dicho destino.
     * @since 2.0
     */    
    public void pasarPaquetesADestino() {
        this.deliveredPacketEntriesLock.lock();
        Iterator it = deliveredPacketsBuffer.iterator();
        while (it.hasNext())  {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            if (ebe.getTargetEnd() == TLink.END_NODE_1) {
                TNode nt = this.getNodeAtEnd1();
                nt.putPacket(ebe.getPacket(), this.getPortOfNodeAtEnd1());
            } else {
                TNode nt = this.getNodeAtEnd2();
                nt.putPacket(ebe.getPacket(), this.getPortOfNodeAtEnd2());
            }
            it.remove();
        }
        this.deliveredPacketEntriesLock.unLock();
    }
    
    /**
     * Este m�todo obtiene el rabanWeight del enlace interno que debe usar el algoritmo de
 routing para calcular rutas.
     * @return El rabanWeight del enlace.
     * @since 2.0
     */    
    @Override
    public long getWeight() {
        long peso = this.getDelay();
        return peso; 
    }

    /**
     * Este m�todo devuelve si el enlace interno est� bien configurado o no.
     * @return TRUE, si la configuraci�n actual del enlace es correcta. FALSE en caso
     * contrario.
     * @since 2.0
     */    
    @Override
    public boolean isWellConfigured() {
        return false;
    }
    
    /**
     * Este m�todo comprueba si el valor de todos los atributos configurables del
     * enlace interno es v�lido o no.
     * @param t Topolog�a dentro de la cual se encuentra este enlace interno.
     * @return CORRECTA, si la configuraci�n es correcta. Un codigo de error en caso contrario.
     * @since 2.0
     */    
    public int comprobar(TTopology t) {
        return 0;
    }
    
    /**
     * Este m�todo transforma en un mensaje legible el c�digo de error devuelto por el
     * m�todo <I>validateConfig(...)</I>
     * @param e El codigo de error que se quiere transformar.
     * @return El mensaje textual correspondiente a ese mensaje de error.
     * @since 2.0
     */    
    @Override
    public String getErrorMessage(int e) {
        return null;
    }
    
    /**
     * Este m�todo transforma el enlace interno en un representaci�n de texto que se
     * puede almacenar en disco sin problemas.
     * @return El equivalente en texto del enlace interno completo.
     * @since 2.0
     */    
    @Override
    public String marshall() {
        String cadena = "#EnlaceInterno#";
        cadena += this.getID();
        cadena += "#";
        cadena += this.getName().replace('#', ' ');
        cadena += "#";
        cadena += this.getShowName();
        cadena += "#";
        cadena += this.getDelay();
        cadena += "#";
        cadena += this.getNodeAtEnd1().getIPv4Address();
        cadena += "#";
        cadena += this.getPortOfNodeAtEnd1();
        cadena += "#";
        cadena += this.getNodeAtEnd2().getIPv4Address();
        cadena += "#";
        cadena += this.getPortOfNodeAtEnd2();
        cadena += "#";
        return cadena;
    }
    
    /**
     * Este m�todo toma la representaci�n textual de un enlace interno completo y
     * configura el objeto con los valores que obtiene.
     * @param elemento Enlace interno en su representaci�n serializada.
     * @return TRUE, si se deserializa correctamente, FALSE en caso contrario.
     * @since 2.0
     */    
    @Override
    public boolean unMarshall(String elemento) {
        TLinkConfig configEnlace = new TLinkConfig();
        String valores[] = elemento.split("#");
        if (valores.length != 10) {
            return false;
        }
        this.setLinkID(Integer.valueOf(valores[2]).intValue());
        configEnlace.setName(valores[3]);
        configEnlace.setShowName(Boolean.valueOf(valores[4]).booleanValue());
        configEnlace.setDelay(Integer.valueOf(valores[5]).intValue());
        String IP1 = valores[6];
        String IP2 = valores[8];
        TNode ex1 = this.getTopology().getNode(IP1);
        TNode ex2 = this.getTopology().getNode(IP2);
        if (!((ex1 == null) || (ex2 == null))) {
            configEnlace.setNameOfNodeAtEnd1(ex1.getName());
            configEnlace.setNameOfNodeAtEnd2(ex2.getName());
            configEnlace.setPortOfNodeAtEnd1(Integer.valueOf(valores[7]).intValue());
            configEnlace.setPortOfNodeAtEnd2(Integer.valueOf(valores[9]).intValue());
            configEnlace.discoverLinkType(this.topology);
        } else {
            return false;
        }
        this.configure(configEnlace, this.topology, false);
        return true;
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase, dejando la instancia como si se
     * acabase de crear por el constructor.
     * @since 2.0
     */    
    @Override
    public void reset() {
        this.packetsInTransitEntriesLock.lock();
        Iterator it = this.buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.packetsInTransitEntriesLock.unLock();
        this.deliveredPacketEntriesLock.lock();
        it = this.deliveredPacketsBuffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.deliveredPacketEntriesLock.unLock();
        numberOfLSPs = 0;
        numberOfBackupLSPs = 0;
        setAsBrokenLink(false);
    }
    
    @Override
    public long getRABANWeight() {
        long rabanWeight = 0;
        long delayWeight = this.getDelay();
        long routingWeightOfNodeAtEnd1 = (long) ((double) (delayWeight*0.10)) * this.getNodeAtEnd1().getRoutingWeight();
        long routingWeightOfNodeAtEnd2 = (long) ((double) (delayWeight*0.10)) * this.getNodeAtEnd2().getRoutingWeight();
        long numberOfLSPsWeight = (long) ((double) (delayWeight*0.05)) * this.numberOfLSPs;
        long numberOfBackupLSPsWeight = (long) ((double) (delayWeight*0.05)) * this.numberOfBackupLSPs;
        long packetsInTransitWeight = (long) ((double) (delayWeight*0.10)) * this.buffer.size();
        long subWeight = (long) (routingWeightOfNodeAtEnd1 + routingWeightOfNodeAtEnd2 + numberOfLSPsWeight + numberOfBackupLSPsWeight + packetsInTransitWeight);
        rabanWeight = (long) ((delayWeight*0.5) + (subWeight*0.5));
        return rabanWeight; 
    }
    
    private int numberOfLSPs;
    private int numberOfBackupLSPs;
    private long stepLength;
}
