/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.scenario;

import simMPLS.protocols.TGPSRPPDU;
import simMPLS.protocols.TTLDPPDU;
import simMPLS.protocols.TGPSRPPayload;
import simMPLS.protocols.TAbstractPDU;
import simMPLS.protocols.TMPLSLabel;
import simMPLS.protocols.TMPLSPDU;
import simMPLS.protocols.TTLDPPayload;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TActivePortSet;
import simMPLS.hardware.ports.TActivePort;
import simMPLS.hardware.dmgp.TDMGP;
import simMPLS.hardware.tldp.TSwitchingMatrix;
import simMPLS.hardware.tldp.TSwitchingMatrixEntry;
import simMPLS.hardware.dmgp.TGPSRPRequestsMatrix;
import simMPLS.hardware.dmgp.TGPSRPRequestEntry;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TPortSet;
import simMPLS.utils.TIDGenerator;
import simMPLS.utils.TLongIDGenerator;
import java.awt.*;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase implementa un nodo LSR; un conmutador interno a un dominio MPLS.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TActiveLSRNode extends TNode implements ITimerEventListener, Runnable {
    
    /**
     * Crea una nueva instancia de TNodoLSR
     * @param identificador Identificador unico del nodo en la topolog�a.
     * @param d Direcci�n IP del nodo.
     * @param il Generador de identificadores para los eventos generados por el nodo.
     * @param t Topolog�a dentro de la cual se encuentra el nodo.
     * @since 1.0
     */
    public TActiveLSRNode(int identificador, String d, TLongIDGenerator il, TTopology t) {
        super(identificador, d, il, t);
        this.ponerPuertos(super.NUM_PUERTOS_LSRA);
        matrizConmutacion = new TSwitchingMatrix();
        gIdent = new TLongIDGenerator();
        gIdentLDP = new TIDGenerator();
        potenciaEnMb = 512;
        dmgp = new TDMGP();
        peticionesGPSRP = new TGPSRPRequestsMatrix();
        estadisticas = new TLSRAStats();
    }
    
    /**
     * Este m�todo obtiene el tama�o del a memoria DMGP del nodo.
     * @since 1.0
     * @return Tama�o de la DMGP en KB.
     */
    public int obtenerTamanioDMGPEnKB() {
        return this.dmgp.getDMGPSizeInKB();
    }
    
    /**
     * Este m�todo permite establecer el tama�o de la DMGP del nodo.
     * @param t Tama�o de la DMGP del nodo en KB.
     * @since 1.0
     */
    public void ponerTamanioDMGPEnKB(int t) {
        this.dmgp.setDMGPSizeInKB(t);
    }
    
    /**
     * Este m�todo obtiene el n�mero de nanosegundos que son necesarios para conmutar
     * un bit.
     * @return El n�mero de nanosegundos necesarios para conmutar un bit.
     * @since 1.0
     */
    public double obtenerNsPorBit() {
        long tasaEnBitsPorSegundo = (long) (this.potenciaEnMb*1048576L);
        double nsPorCadaBit = (double) ((double)1000000000.0/(long)tasaEnBitsPorSegundo);
        return nsPorCadaBit;
    }
    
    /**
     * Este m�todo calcula el n�mero de nanosegundos necesarios para conmutar un n�mero
     * determinado de octetos.
     * @param octetos N�mero de octetos que queremos conmutar.
     * @return N�mero de nanosegundos necesarios para conmutar los octetos especificados.
     * @since 1.0
     */
    public double obtenerNsUsadosTotalOctetos(int octetos) {
        double nsPorCadaBit = obtenerNsPorBit();
        long bitsOctetos = (long) ((long)octetos*(long)8);
        return (double)((double)nsPorCadaBit*(long)bitsOctetos);
    }
    
    /**
     * Este m�todo devuelve el n�mero de bits que se pueden conmutar con el n�mero de
     * nanosegundos de los que dispone actualmente el nodo.
     * @return N�mero de bits m�ximos que puede conmutar el nodo en un instante.
     * @since 1.0
     */
    public int obtenerLimiteBitsTransmitibles() {
        double nsPorCadaBit = obtenerNsPorBit();
        double maximoBits = (double) ((double)nsDisponibles/(double)nsPorCadaBit);
        return (int) maximoBits;
    }
    
    /**
     * Este m�todo calcula el n�mero m�ximo de octetos completos que puede conmtuar el
     * nodo.
     * @return El n�mero m�ximo de octetos que puede conmutar el nodo.
     * @since 1.0
     */
    public int obtenerOctetosTransmitibles() {
        double maximoBytes = ((double)obtenerLimiteBitsTransmitibles()/(double)8.0);
        return (int) maximoBytes;
    }
    
    /**
     * Este m�todo devuelve la potencia de conmutaci�n con la que est� configurado el
     * nodo.
     * @return Potencia de conmutaci�n en Mbps.
     * @since 1.0
     */
    public int obtenerPotenciaEnMb() {
        return this.potenciaEnMb;
    }
    
    /**
     * Este m�todo establece la potencia de conmutaci�n para el nodo.
     * @param pot Potencia de conmutaci�n en Mbps deseada para el nodo.
     * @since 1.0
     */
    public void ponerPotenciaEnMb(int pot) {
        this.potenciaEnMb = pot;
    }
    
    /**
     * Este m�todo permite obtener el tamanio el buffer del nodo.
     * @return Tamanio del buffer en MB.
     * @since 1.0
     */
    public int obtenerTamanioBuffer() {
        return this.obtenerPuertos().getBufferSizeInMB();
    }
    
    /**
     * Este m�todo permite establecer el tamanio del buffer del nodo.
     * @param tb Tamanio deseado para el buffer del nodo en MB.
     * @since 1.0
     */
    public void ponerTamanioBuffer(int tb) {
        this.obtenerPuertos().setBufferSizeInMB(tb);
    }
    
    /**
     * Este m�todo reinicia los atributos del nodo hasta dejarlos como si acabasen de
     * ser creados por el Constructor.
     * @since 1.0
     */
    public void reset() {
        this.puertos.reset();
        matrizConmutacion.reset();
        gIdent.reset();
        gIdentLDP.reset();
        estadisticas.reset();
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
        dmgp.reset();
        peticionesGPSRP.reset();
        this.restaurarPasosSinEmitir();
    }
    
    /**
     * Este m�todo permite obtener el tipo del nodo.
     * @return TNode.LSR, indicando que se trata de un nodo LSR.
     * @since 1.0
     */
    public int getNodeType() {
        return super.LSRA;
    }
    
    /**
     * Este m�todo permite obtener eventos de sincronizaci�n del reloj del simulador.
     * @param evt Evento de sincronizaci�n que env�a el reloj del simulador.
     * @since 1.0
     */
    public void receiveTimerEvent(TTimerEvent evt) {
        this.ponerDuracionTic(evt.getStepDuration());
        this.ponerInstanteDeTiempo(evt.getUpperLimit());
        if (this.obtenerPuertos().isAnyPacketToSwitch()) {
            this.nsDisponibles += evt.getStepDuration();
        } else {
            this.restaurarPasosSinEmitir();
            this.nsDisponibles = evt.getStepDuration();
        }
        this.iniciar();
    }
    
    /**
     * Este m�todo se llama cuando se inicia el hilo independiente del nodo y es en el
     * que se implementa toda la funcionalidad.
     * @since 1.0
     */
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        try {
            this.generarEventoSimulacion(new TSENodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), this.obtenerPuertos().getCongestionLevel()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        comprobarElEstadoDeLasComunicaciones();
        decrementarContadores();
        conmutarPaquete();
        estadisticas.asentarDatos(this.getAvailableTime());
        // Acciones a llevar a cabo durante el tic.
    }
    
    /**
     * Este m�todo se encarga de comprobar que los enlaces que unen al nodo con sus
     * adyacentes, funcionan correctamente. Y si no es asi y es necesario, env�a la
     * se�alizaci�n correspondiente para reparar la situaci�n.
     * @since 1.0
     */
    public void comprobarElEstadoDeLasComunicaciones() {
        TSwitchingMatrixEntry emc = null;
        int idPuerto = 0;
        TPort puertoSalida = null;
        TPort puertoSalidaBackup = null;
        TPort puertoEntrada = null;
        TLink et = null;
        matrizConmutacion.getMonitor().lock();
        Iterator it = matrizConmutacion.getEntriesIterator();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                idPuerto = emc.getBackupOutgoingPortID();
                if ((idPuerto >= 0) && (idPuerto < this.puertos.getNumberOfPorts())) {
                    puertoSalidaBackup = this.puertos.getPort(idPuerto);
                    if (puertoSalidaBackup != null) {
                        et = puertoSalidaBackup.getLink();
                        if (et != null) {
                            if ((et.linkIsBroken()) && (emc.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                if (emc.backupLSPHasBeenEstablished() || emc.backupLSPShouldBeRemoved()) {
                                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                                }
                            }
                        }
                    }
                }
                idPuerto = emc.getOutgoingPortID();
                if ((idPuerto >= 0) && (idPuerto < this.puertos.getNumberOfPorts())) {
                    puertoSalida = this.puertos.getPort(idPuerto);
                    if (puertoSalida != null) {
                        et = puertoSalida.getLink();
                        if (et != null) {
                            if ((et.linkIsBroken()) && (emc.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                if (emc.backupLSPHasBeenEstablished()) {
                                    emc.switchToBackupLSP();
                                } else {
                                    eliminarTLDP(emc, emc.getIncomingPortID());
                                }
                            }
                        }
                    }
                }
                idPuerto = emc.getIncomingPortID();
                if ((idPuerto >= 0) && (idPuerto < this.puertos.getNumberOfPorts())) {
                    puertoEntrada = this.puertos.getPort(idPuerto);
                    if (puertoEntrada != null) {
                        et = puertoEntrada.getLink();
                        if (et != null) {
                            if ((et.linkIsBroken()) && (emc.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                eliminarTLDP(emc, emc.getOutgoingPortID());
                                if (emc.backupLSPHasBeenEstablished() || emc.backupLSPShouldBeRemoved()) {
                                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                                    eliminarTLDP(emc, emc.getBackupOutgoingPortID());
                                }
                            }
                        }
                    }
                }
            } else {
                it.remove();
            }
        }
        matrizConmutacion.getMonitor().unLock();
        
        peticionesGPSRP.decreaseTimeout(this.obtenerDuracionTic());
        peticionesGPSRP.updateEntries();
        int numeroPuertos = puertos.getNumberOfPorts();
        int i = 0;
        TActivePort puertoActual = null;
        TLink enlTop = null;
        for (i=0; i<numeroPuertos; i++) {
            puertoActual = (TActivePort) puertos.getPort(i);
            if (puertoActual != null) {
                enlTop = puertoActual.getLink();
                if (enlTop != null) {
                    if (enlTop.linkIsBroken()) {
                        peticionesGPSRP.removeEntriesMatchingOutgoingPort(i);
                    }
                }
            }
        }
        peticionesGPSRP.getMonitor().lock();
        Iterator ite = peticionesGPSRP.getEntriesIterator();
        int idFlujo = 0;
        int idPaquete = 0;
        String IPDestino = null;
        int pSalida = 0;
        TGPSRPRequestEntry epet = null;
        while (ite.hasNext()) {
            epet = (TGPSRPRequestEntry) ite.next();
            if (epet.isRetryable()) {
                idFlujo = epet.getFlowID();
                idPaquete = epet.getPacketID();
                IPDestino = epet.getCrossedNodeIP();
                pSalida = epet.getOutgoingPort();
                this.solicitarGPSRP(idFlujo, idPaquete, IPDestino, pSalida);
            }
            epet.resetTimeout();
        }
        peticionesGPSRP.getMonitor().unLock();
    }
    
    /**
     * Este m�todo conmuta paquetes del buffer de entrada.
     * @since 1.0
     */
    public void conmutarPaquete() {
        boolean conmute = false;
        int puertoLeido = 0;
        TAbstractPDU paquete = null;
        int octetosQuePuedoMandar = this.obtenerOctetosTransmitibles();
        while (this.obtenerPuertos().canSwitchPacket(octetosQuePuedoMandar)) {
            conmute = true;
            paquete = this.puertos.getNextPacket();
            puertoLeido = puertos.getReadPort();
            if (paquete != null) {
                if (paquete.getType() == TAbstractPDU.TLDP) {
                    conmutarTLDP((TTLDPPDU) paquete, puertoLeido);
                } else if (paquete.getType() == TAbstractPDU.MPLS) {
                    conmutarMPLS((TMPLSPDU) paquete, puertoLeido);
                } else if (paquete.getType() == TAbstractPDU.GPSRP) {
                    conmutarGPSRP((TGPSRPPDU) paquete, puertoLeido);
                } else {
                    this.nsDisponibles += obtenerNsUsadosTotalOctetos(paquete.getSize());
                    discardPacket(paquete);
                }
                this.nsDisponibles -= obtenerNsUsadosTotalOctetos(paquete.getSize());
                octetosQuePuedoMandar = this.obtenerOctetosTransmitibles();
            }
        }
        if (conmute) {
            this.restaurarPasosSinEmitir();
        } else {
            this.incrementarPasosSinEmitir();
        }
    }
    
    /**
     * Este m�todo conmuta un paquete GPSRP.
     * @param paquete Paquete GPSRP a conmutar.
     * @param pEntrada Puerto por el que ha llegado el paquete.
     * @since 1.0
     */
    public void conmutarGPSRP(TGPSRPPDU paquete, int pEntrada) {
        if (paquete != null) {
            int mensaje = paquete.getGPSRPPayload().getGPSRPMessageType();
            int flujo = paquete.getGPSRPPayload().getFlowID();
            int idPaquete = paquete.getGPSRPPayload().getPacketID();
            String IPDestinoFinal = paquete.getIPv4Header().getTargetIP();
            TActivePort pSalida = null;
            if (IPDestinoFinal.equals(this.getIPAddress())) {
                if (mensaje == TGPSRPPayload.RETRANSMISSION_REQUEST) {
                    this.atenderPeticionGPSRP(paquete, pEntrada);
                } else if (mensaje == TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE) {
                    this.atenderDenegacionGPSRP(paquete, pEntrada);
                } else if (mensaje == TGPSRPPayload.RETRANSMISION_OK) {
                    this.atenderAceptacionGPSRP(paquete, pEntrada);
                }
            } else {
                String IPSalida = this.topologia.obtenerIPSaltoRABAN(this.getIPAddress(), IPDestinoFinal);
                pSalida = (TActivePort) this.puertos.getPortWhereIsConectedANodeHavingIP(IPSalida);
                if (pSalida != null) {
                    pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    this.discardPacket(paquete);
                }
            }
        }
    }
    
    /**
     * Este m�todo atiende una petici�n de retransmisi�n.
     * @param paquete Paquete GPSRP de solicitud de retransmisi�n.
     * @param pEntrada Puerto por el que ha llegado el paquete.
     * @since 1.0
     */
    public void atenderPeticionGPSRP(TGPSRPPDU paquete, int pEntrada) {
        int idFlujo = paquete.getGPSRPPayload().getFlowID();
        int idPaquete = paquete.getGPSRPPayload().getPacketID();
        TMPLSPDU paqueteBuscado = (TMPLSPDU) dmgp.getPacket(idFlujo, idPaquete);
        if (paqueteBuscado != null) {
            this.aceptarGPSRP(paquete, pEntrada);
            TActivePort puertoSalida = (TActivePort) this.puertos.getPort(pEntrada);
            if (puertoSalida != null) {
                puertoSalida.putPacketOnLink(paqueteBuscado, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                try {
                    this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteBuscado.getSubtype()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.denegarGPSRP(paquete, pEntrada);
        }
    }
    
    /**
     * Este m�todo atiende una denegaci�n de retransmisi�n.
     * @param paquete Paquete GPSRP de denegaci�n de retransmisi�n.
     * @param pEntrada Puerto por el que ha llegado el paquete.
     * @since 1.0
     */
    public void atenderDenegacionGPSRP(TGPSRPPDU paquete, int pEntrada) {
        int idf = paquete.getGPSRPPayload().getFlowID();
        int idp = paquete.getGPSRPPayload().getPacketID();
        TGPSRPRequestEntry ep = peticionesGPSRP.getEntry(idf, idp);
        if (ep != null) {
            ep.forceTimeoutReset();
            int p = ep.getOutgoingPort();
            if (!ep.isPurgeable()) {
                String IPDestino = ep.getCrossedNodeIP();
                if (IPDestino != null) {
                    solicitarGPSRP(idf, idp, IPDestino, p);
                } else {
                    peticionesGPSRP.removeEntry(idf, idp);
                }
            } else {
                peticionesGPSRP.removeEntry(idf, idp);
            }
        }
    }
    
    /**
     * Este m�todo atiende una aceptaci�n de retransmisi�n.
     * @param paquete Paquete GPSRP de aceptaci�n de retransmisi�n.
     * @param pEntrada Puerto por el que ha llegado el paquete.
     * @since 1.0
     */
    public void atenderAceptacionGPSRP(TGPSRPPDU paquete, int pEntrada) {
        int idf = paquete.getGPSRPPayload().getFlowID();
        int idp = paquete.getGPSRPPayload().getPacketID();
        peticionesGPSRP.removeEntry(idf, idp);
    }
    
    /**
     * Este m�todo solicita una retransmisi�n de paquete.
     * @param paquete Paquete MPLS para el que se solicita la retransmisi�n.
     * @param pSalida Puerto por el que se debe encaminar la petici�n.
     * @since 1.0
     */
    public void runGoSPDUStoreAndRetransmitProtocol(TMPLSPDU paquete, int pSalida) {
        TGPSRPRequestEntry ep = null;
        ep = this.peticionesGPSRP.addEntry(paquete, pSalida);
        if (ep != null) {
            TActivePort puertoSalida = (TActivePort) puertos.getPort(pSalida);
            TGPSRPPDU paqueteGPSRP = null;
            String IPDestino = ep.getCrossedNodeIP();
            if (IPDestino != null) {
                try {
                    paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                paqueteGPSRP.getGPSRPPayload().setFlowID(ep.getFlowID());
                paqueteGPSRP.getGPSRPPayload().setPacketID(ep.getPacketID());
                paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
                puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                try {
                    this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                    this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Este m�todo solicita una retransmisi�n de paquete.
     * @param idFlujo Identificador del flujo al que pertenece el paquete solicitado.
     * @param idPaquete Identificador del paquete solicitado.
     * @param IPDestino IP del nodo al que se env�a la solicitud.
     * @param pSalida Puerto de salida por el que se debe encaminar la solicitud.
     * @since 1.0
     */
    public void solicitarGPSRP(int idFlujo, int idPaquete, String IPDestino, int pSalida) {
        TActivePort puertoSalida = (TActivePort) puertos.getPort(pSalida);
        TGPSRPPDU paqueteGPSRP = null;
        if (IPDestino != null) {
            try {
                paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), IPDestino);
            } catch (Exception e) {
                e.printStackTrace();
            }
            paqueteGPSRP.getGPSRPPayload().setFlowID(idFlujo);
            paqueteGPSRP.getGPSRPPayload().setPacketID(idPaquete);
            paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISSION_REQUEST);
            puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Este m�todo deniega la retransmisi�n de un paquete.
     * @param paquete Paquete GPSRP de solicitud de retransmisi�in.
     * @param pSalida PPuerto de salida por el que se debe encaminar la denegaci�n.
     * @since 1.0
     */
    public void denegarGPSRP(TGPSRPPDU paquete, int pSalida) {
        TActivePort puertoSalida = (TActivePort) this.puertos.getPort(pSalida);
        if (puertoSalida != null) {
            TGPSRPPDU paqueteGPSRP = null;
            try {
                paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), paquete.getIPv4Header().getOriginIP());
            } catch (Exception e) {
                e.printStackTrace();
            }
            paqueteGPSRP.getGPSRPPayload().setFlowID(paquete.getGPSRPPayload().getFlowID());
            paqueteGPSRP.getGPSRPPayload().setPacketID(paquete.getGPSRPPayload().getPacketID());
            paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_NOT_POSSIBLE);
            puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            discardPacket(paquete);
        }
    }
    
    /**
     * Este m�todo acepta la retransmisi�n de un paquete.
     * @param paquete Paquete GPSRP de solicitud de retransmisi�n.
     * @param pSalida Puerto por el que se debe encaminar la aceptaci�n.
     * @since 1.0
     */
    public void aceptarGPSRP(TGPSRPPDU paquete, int pSalida) {
        TActivePort puertoSalida = (TActivePort) this.puertos.getPort(pSalida);
        if (puertoSalida != null) {
            TGPSRPPDU paqueteGPSRP = null;
            try {
                paqueteGPSRP = new TGPSRPPDU(gIdent.getNextID(), this.getIPAddress(), paquete.getIPv4Header().getOriginIP());
            } catch (Exception e) {
                e.printStackTrace();
            }
            paqueteGPSRP.getGPSRPPayload().setFlowID(paquete.getGPSRPPayload().getFlowID());
            paqueteGPSRP.getGPSRPPayload().setPacketID(paquete.getGPSRPPayload().getPacketID());
            paqueteGPSRP.getGPSRPPayload().setGPSRPMessageType(TGPSRPPayload.RETRANSMISION_OK);
            puertoSalida.putPacketOnLink(paqueteGPSRP, puertoSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
            try {
                this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP, paqueteGPSRP.getSize()));
                this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            discardPacket(paquete);
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP que ha llegado.
     * @param paquete Paquete TLDP recibido.
     * @param pEntrada Puerto por el que se ha recibido el paquete TLDP.
     * @since 1.0
     */
    public void conmutarTLDP(TTLDPPDU paquete, int pEntrada) {
        if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST) {
            this.tratarSolicitudTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST_OK) {
            this.tratarSolicitudOkTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REQUEST_DENIED) {
            this.tratarSolicitudNoTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REMOVAL_REQUEST) {
            this.tratarEliminacionTLDP(paquete, pEntrada);
        } else if (paquete.getTLDPPayload().getTLDPMessageType() == TTLDPPayload.LABEL_REVOMAL_REQUEST_OK) {
            this.tratarEliminacionOkTLDP(paquete, pEntrada);
        }
    }
    
    /**
     * Este m�todo trata un paquete MPLS que ha llegado.
     * @param paquete Paquete MPLS recibido.
     * @param pEntrada Puerto por el que se ha recibido el paquete MPLS.
     * @since 1.0
     */
    public void conmutarMPLS(TMPLSPDU paquete, int pEntrada) {
        TMPLSLabel eMPLS = null;
        TSwitchingMatrixEntry emc = null;
        boolean conEtiqueta1 = false;
        boolean requiereLSPDeRespaldo = false;
        if (paquete.getLabelStack().getTop().getLabel() == 1) {
            eMPLS = paquete.getLabelStack().getTop();
            paquete.getLabelStack().popTop();
            conEtiqueta1 = true;
            if ((eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP) ||
            (eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP) ||
            (eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP) ||
            (eMPLS.getEXP() == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP)) {
                requiereLSPDeRespaldo = true;
            }
        }
        int valorLABEL = paquete.getLabelStack().getTop().getLabel();
        String IPDestinoFinal = paquete.getIPv4Header().getTargetIP();
        emc = matrizConmutacion.getEntry(pEntrada, valorLABEL, TSwitchingMatrixEntry.LABEL_ENTRY);
        if (emc == null) {
            if (conEtiqueta1) {
                paquete.getLabelStack().pushLabel(eMPLS);
            }
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                solicitarTLDP(emc);
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushLabel(eMPLS);
                }
                this.puertos.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushLabel(eMPLS);
                }
                this.puertos.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushLabel(eMPLS);
                }
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushLabel(eMPLS);
                }
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operacion = emc.getLabelStackOperation();
                if (operacion == TSwitchingMatrixEntry.UNDEFINED) {
                    if (conEtiqueta1) {
                        paquete.getLabelStack().pushLabel(eMPLS);
                    }
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PUSH_LABEL) {
                        TMPLSLabel empls = new TMPLSLabel();
                        empls.setBoS(false);
                        empls.setEXP(0);
                        empls.setLabel(emc.getOutgoingLabel());
                        empls.setTTL(paquete.getLabelStack().getTop().getTTL()-1);
                        paquete.getLabelStack().pushLabel(empls);
                        if (conEtiqueta1) {
                            paquete.getLabelStack().pushLabel(eMPLS);
                            paquete.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            dmgp.addPacket(paquete);
                        }
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.POP_LABEL) {
                        paquete.getLabelStack().popTop();
                        if (conEtiqueta1) {
                            paquete.getLabelStack().pushLabel(eMPLS);
                            paquete.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            dmgp.addPacket(paquete);
                        }
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.SWAP_LABEL) {
                        if (requiereLSPDeRespaldo) {
                            solicitarTLDPDeBackup(emc);
                        }
                        paquete.getLabelStack().getTop().setLabel(emc.getOutgoingLabel());
                        if (conEtiqueta1) {
                            paquete.getLabelStack().pushLabel(eMPLS);
                            paquete.getIPv4Header().getOptionsField().setCrossedActiveNode(this.getIPAddress());
                            dmgp.addPacket(paquete);
                        }
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        if (emc.aBackupLSPHasBeenRequested()) {
                            TInternalLink ei = (TInternalLink) pSalida.getLink();
                            ei.ponerLSP();
                            ei.quitarLSPDeBackup();
                            emc.setEntryIsForBackupLSP(false);
                        }
                        try {
                            this.generarEventoSimulacion(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketSwitched(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushLabel(eMPLS);
                }
                discardPacket(paquete);
            }
        }
    }
    
    /**
     * Este m�todo trata una petici�n de etiquetas.
     * @param paquete Petici�n de etiquetas recibida de otro nodo.
     * @param pEntrada Puerto de entrada de la petici�n de etiqueta.
     * @since 1.0
     */
    public void tratarSolicitudTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        if (emc == null) {
            emc = crearEntradaAPartirDeTLDP(paquete, pEntrada);
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                this.solicitarTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                // no hago nada. Se est� esperando una etiqueta.);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                eliminarTLDP(emc, pEntrada);
            } else if (etiquetaActual > 15) {
                enviarSolicitudOkTLDP(emc);
            } else {
                discardPacket(paquete);
            }
        } else {
            discardPacket(paquete);
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de eliminaci�n de etiqueta.
     * @param paquete Eliminaci�n de etiqueta recibida.
     * @param pEntrada Puerto por el que se recibi�n la eliminaci�n de etiqueta.
     * @since 1.0
     */
    public void tratarEliminacionTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.getLocalOrigin() == TTLDPPDU.CAME_BY_ENTRANCE) {
            emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        } else {
            emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getIncomingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOutgoingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOutgoingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOutgoingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                } else if (etiquetaActual > 15) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOutgoingPortID());
                } else {
                    discardPacket(paquete);
                }
                if (emc.backupLSPHasBeenEstablished() || emc.backupLSPShouldBeRemoved()) {
                    int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                    if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                        emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        eliminarTLDP(emc, emc.getBackupOutgoingPortID());
                    } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                        emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        eliminarTLDP(emc, emc.getBackupOutgoingPortID());
                    } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                        emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        eliminarTLDP(emc, emc.getBackupOutgoingPortID());
                    } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                        // No hacemos nada... esperamos.
                    } else if (etiquetaActualBackup > 15) {
                        emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        eliminarTLDP(emc, emc.getBackupOutgoingPortID());
                    } else {
                        discardPacket(paquete);
                    }
                }
            } else if (emc.getOutgoingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getIncomingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getIncomingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getIncomingPortID());
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                } else if (etiquetaActual > 15) {
                    if (emc.backupLSPHasBeenEstablished()) {
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        if (emc.getBackupOutgoingPortID() >= 0) {
                            TInternalLink ei = (TInternalLink) puertos.getPort(emc.getBackupOutgoingPortID()).getLink();
                            ei.ponerLSP();
                            ei.quitarLSPDeBackup();
                        }
                        emc.switchToBackupLSP();
                    } else {
                        emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                        enviarEliminacionOkTLDP(emc, pEntrada);
                        eliminarTLDP(emc, emc.getIncomingPortID());
                    }
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else if (etiquetaActualBackup > 15) {
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    emc.setBackupOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
                } else {
                    discardPacket(paquete);
                }
            }
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de confirmaci�n de etiqueta.
     * @param paquete Confirmaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de etiquetas.
     * @since 1.0
     */
    public void tratarSolicitudOkTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getOutgoingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(paquete.getTLDPPayload().getLabel());
                    if (emc.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                        emc.setLabelOrFEC(matrizConmutacion.getNewLabel());
                    }
                    TInternalLink et = (TInternalLink) puertos.getPort(pEntrada).getLink();
                    if (et != null) {
                        if (emc.aBackupLSPHasBeenRequested()) {
                            et.ponerLSPDeBackup();
                        } else {
                            et.ponerLSP();
                        }
                    }
                    enviarSolicitudOkTLDP(emc);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    discardPacket(paquete);
                } else if (etiquetaActual > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setBackupOutgoingLabel(paquete.getTLDPPayload().getLabel());
                    if (emc.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                        emc.setLabelOrFEC(matrizConmutacion.getNewLabel());
                    }
                    TInternalLink et = (TInternalLink) puertos.getPort(pEntrada).getLink();
                    if (et != null) {
                        et.ponerLSPDeBackup();
                    }
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            }
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de denegaci�n de etiqueta.
     * @param paquete Paquete de denegaci�n de etiquetas recibido.
     * @param pEntrada Puerto por el que se ha recibido la denegaci�n de etiquetas.
     * @since 1.0
     */
    public void tratarSolicitudNoTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getOutgoingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                    enviarSolicitudNoTLDP(emc);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    discardPacket(paquete);
                } else if (etiquetaActual > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_UNAVAILABLE);
                    enviarSolicitudNoTLDP(emc);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            }
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de confirmaci�n de eliminaci�n de etiqueta.
     * @param paquete Paquete de confirmaci�n e eliminaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de eliminaci�n de etiqueta.
     * @since 1.0
     */
    public void tratarEliminacionOkTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.getLocalOrigin() == TTLDPPDU.CAME_BY_ENTRANCE) {
            emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier(), pEntrada);
        } else {
            emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getIncomingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if ((etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) ||
                (etiquetaActual == TSwitchingMatrixEntry.LABEL_WITHDRAWN)) {
                    if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        if (pSalida != null) {
                            TInternalLink ei = (TInternalLink) pSalida.getLink();
                            if (emc.aBackupLSPHasBeenRequested()) {
                                ei.quitarLSPDeBackup();
                            } else {
                                ei.quitarLSP();
                            }
                        }
                        emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                    }
                    if (emc.getBackupOutgoingLabel() != TSwitchingMatrixEntry.LABEL_WITHDRAWN) {
                        if (emc.getBackupOutgoingPortID() >= 0) {
                            TPort pSalida = puertos.getPort(emc.getBackupOutgoingPortID());
                            if (pSalida != null) {
                                TInternalLink ei = (TInternalLink) pSalida.getLink();
                                ei.quitarLSPDeBackup();
                            }
                            emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                        }
                    }
                    TPort pSalida = puertos.getPort(pEntrada);
                    if (pSalida != null) {
                        TInternalLink ei = (TInternalLink) pSalida.getLink();
                        if (emc.aBackupLSPHasBeenRequested()) {
                            ei.quitarLSPDeBackup();
                        } else {
                            ei.quitarLSP();
                        }
                    }
                    matrizConmutacion.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                } else if (etiquetaActual > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getOutgoingPortID() == pEntrada) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    TPort pSalida = puertos.getPort(pEntrada);
                    TInternalLink ei = (TInternalLink) pSalida.getLink();
                    if (emc.aBackupLSPHasBeenRequested()) {
                        ei.quitarLSPDeBackup();
                    } else {
                        ei.quitarLSP();
                    }
                    if (emc.getBackupOutgoingLabel() == TSwitchingMatrixEntry.LABEL_WITHDRAWN) {
                        matrizConmutacion.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                    }
                } else if (etiquetaActual > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            } else if (emc.getBackupOutgoingPortID() == pEntrada) {
                int etiquetaActualBackup = emc.getBackupOutgoingLabel();
                if (etiquetaActualBackup == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    discardPacket(paquete);
                } else if (etiquetaActualBackup == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    TPort pSalida = puertos.getPort(pEntrada);
                    TInternalLink ei = (TInternalLink) pSalida.getLink();
                    ei.quitarLSPDeBackup();
                    emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_WITHDRAWN);
                    if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_WITHDRAWN) {
                        matrizConmutacion.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                    }
                } else if (etiquetaActualBackup > 15) {
                    discardPacket(paquete);
                } else {
                    discardPacket(paquete);
                }
            }
        }
    }
    
    /**
     * Este m�todo env�a una etiqueta al nodo que indique la entrada en la
     * matriz de conmutaci�n especificada.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void enviarSolicitudOkTLDP(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPAddress();
                String IPDestino = puertos.getIPOfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_OK);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPAddress());
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.getTLDPPayload().setLabel(emc.getLabelOrFEC());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPDestino);
                        pSalida.putPacketOnLink(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, nuevoTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo env�a una denegaci�n de etiqueta al nodo que especifique la entrada
     * de la matriz de conmutaci�n correspondiente.
     * @param emc Entrada de la matriz de conmutaci�n correspondiente.
     * @since 1.0
     */
    public void enviarSolicitudNoTLDP(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPAddress();
                String IPDestino = puertos.getIPOfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_DENIED);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPAddress());
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPDestino);
                        pSalida.putPacketOnLink(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, nuevoTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo env�a una confirmaci�n de eliminaci�n de etiqueta al nodo que
     * especifique la correspondiente entrada en la matriz de conmutaci�n.
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la confirmaci�n de eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void enviarEliminacionOkTLDP(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            String IPLocal = this.getIPAddress();
            String IPDestino = puertos.getIPOfNodeLinkedTo(puerto);
            if (IPDestino != null) {
                TTLDPPDU nuevoTLDP = null;
                try {
                    nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nuevoTLDP != null) {
                    nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REVOMAL_REQUEST_OK);
                    nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPAddress());
                    nuevoTLDP.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                    if (emc.getOutgoingPortID() == puerto) {
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getBackupOutgoingPortID() == puerto) {
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getIncomingPortID() == puerto) {
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort pSalida = puertos.getPort(puerto);
                    pSalida.putPacketOnLink(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, nuevoTLDP.getSize()));
                        this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo solicita una etiqueta al nodo indicado por la correspondiente entrada en
     * la matriz de conmutaci�n.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void solicitarTLDP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        String IPSalto = topologia.obtenerIPSaltoRABAN(IPLocal, IPDestinoFinal);
        if (IPSalto != null) {
            TTLDPPDU paqueteTLDP = null;
            try {
                paqueteTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPSalto);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (paqueteTLDP != null) {
                paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                if (emc.aBackupLSPHasBeenRequested()) {
                    paqueteTLDP.setLSPType(true);
                } else {
                    paqueteTLDP.setLSPType(false);
                }
                paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalto);
                if (pSalida != null) {
                    pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    emc.setOutgoingPortID(pSalida.getPortID());
                    try {
                        this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                        this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo solicita una etiqueta al nodo indicado por la correspondiente entrada en
     * la matriz de conmutaci�n. El camino solicitado ser� de Backup.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void solicitarTLDPDeBackup(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        String IPSaltoPrincipal = puertos.getIPOfNodeLinkedTo(emc.getOutgoingPortID());
        String IPSalto = topologia.obtenerIPSaltoRABAN(IPLocal, IPDestinoFinal, IPSaltoPrincipal);
        if (IPSalto != null) {
            if (emc.getBackupOutgoingPortID() == TSwitchingMatrixEntry.UNDEFINED) {
                if (emc.getBackupOutgoingLabel() == TSwitchingMatrixEntry.UNDEFINED) {
                    if (emc.getOutgoingLabel() > 15) {
                        emc.setBackupOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                        if (IPSalto != null) {
                            TTLDPPDU paqueteTLDP = null;
                            try {
                                paqueteTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPSalto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (paqueteTLDP != null) {
                                paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                                paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                                paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                                paqueteTLDP.setLSPType(true);
                                paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                                TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalto);
                                emc.setBackupOutgoingPortID(pSalida.getPortID());
                                if (pSalida != null) {
                                    pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                                    try {
                                        this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                                        this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo elimina una etiqueta del nodo indicado por la correspondiente entrada en
     * la matriz de conmutaci�n.
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     */
    public void eliminarTLDP(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
            String IPLocal = this.getIPAddress();
            String IPDestinoFinal = emc.getTailEndIPAddress();
            String IPSalto = puertos.getIPOfNodeLinkedTo(puerto);
            if (IPSalto != null) {
                TTLDPPDU paqueteTLDP = null;
                try {
                    paqueteTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                    paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REMOVAL_REQUEST);
                    if (emc.getOutgoingPortID() == puerto) {
                        paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getBackupOutgoingPortID() == puerto) {
                        paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                        paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    } else if (emc.getIncomingPortID() == puerto) {
                        paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort pSalida = puertos.getPort(puerto);
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo reenv�a todas las peticiones pendiente de confirmaci�n al nodo
     * especificadao por la correspondiente entrada en la matriz de conmutaci�n.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void solicitarTLDPTrasTimeout(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            String IPLocal = this.getIPAddress();
            String IPDestinoFinal = emc.getTailEndIPAddress();
            String IPSalto = puertos.getIPOfNodeLinkedTo(emc.getOutgoingPortID());
            if (IPSalto != null) {
                TTLDPPDU paqueteTLDP = null;
                try {
                    paqueteTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.getTLDPPayload().setTargetIPAddress(IPDestinoFinal);
                    paqueteTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST);
                    paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getLocalTLDPSessionID());
                    if (emc.aBackupLSPHasBeenRequested()) {
                        paqueteTLDP.setLSPType(true);
                    } else {
                        paqueteTLDP.setLSPType(false);
                    }
                    paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_FORWARD);
                    TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de una
     * entrada de la matriz de conmutaci�n.
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void eliminarTLDPTrasTimeout(TSwitchingMatrixEntry emc, int puerto){
        eliminarTLDP(emc, puerto);
    }
    
    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de una
     * entrada de la matriz de conmutaci�n a todos los puertos necesarios.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void eliminarTLDPTrasTimeout(TSwitchingMatrixEntry emc){
        eliminarTLDP(emc, emc.getIncomingPortID());
        eliminarTLDP(emc, emc.getOutgoingPortID());
        eliminarTLDP(emc, emc.getBackupOutgoingPortID());
    }
    
    /**
     * Este m�todo decrementa los contadores para la retransmisi�n.
     * @since 1.0
     */
    public void decrementarContadores() {
        TSwitchingMatrixEntry emc = null;
        this.matrizConmutacion.getMonitor().lock();
        Iterator it = this.matrizConmutacion.getEntriesIterator();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                emc.decreaseTimeOut(this.obtenerDuracionTic());
                if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    if (emc.shouldRetryExpiredTLDPRequest()) {
                        emc.resetTimeOut();
                        emc.decreaseAttempts();
                        solicitarTLDPTrasTimeout(emc);
                    }
                } else if (emc.getOutgoingLabel() == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    if (emc.shouldRetryExpiredTLDPRequest()) {
                        emc.resetTimeOut();
                        emc.decreaseAttempts();
                        eliminarTLDPTrasTimeout(emc);
                    } else {
                        if (!emc.areThereAvailableAttempts()) {
                            it.remove();
                        }
                    }
                } else {
                    emc.resetTimeOut();
                    emc.resetAttempts();
                }
            }
        }
        this.matrizConmutacion.getMonitor().unLock();
    }
    
    /**
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n a partir de una
     * solicitud de etiqueta recibida.
     * @param paqueteSolicitud Solicitud de etiqueta recibida.
     * @param pEntrada Puerto por el que se ha recibido la solicitud.
     * @return La nueva entrada en la matriz de conmutaci�n, creda, insertada e inicializada.
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaAPartirDeTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.getTLDPPayload().getTLDPIdentifier();
        TPort puertoEntrada = puertos.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.getTLDPPayload().getTargetIPAddress();
        String IPSalto = topologia.obtenerIPSaltoRABAN(this.getIPAddress(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalto);
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(IdTLDPAntecesor);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setLabelOrFEC(TSwitchingMatrixEntry.UNDEFINED);
            emc.setEntryIsForBackupLSP(paqueteSolicitud.getLSPType());
            if (puertoSalida != null) {
                emc.setOutgoingPortID(puertoSalida.getPortID());
            } else {
                emc.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
            emc.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.obtenerNuevo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            matrizConmutacion.addEntry(emc);
        }
        return emc;
    }
    
    /**
     * Este m�todo descarta un paquete del ndo y refleja este descarte en las
     * estad�sticas del nodo.
     * @param paquete Paquete que queremos descartar.
     * @since 1.0
     */
    public void discardPacket(TAbstractPDU paquete) {
        try {
            this.generarEventoSimulacion(new TSEPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
            this.estadisticas.addStatsEntry(paquete, TStats.DESCARTE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paquete = null;
    }
    
    /**
     * Este m�todo permite acceder a los puertos del nodo directamtne.
     * @return El conjunto de puertos del nodo.
     * @since 1.0
     */
    public TPortSet obtenerPuertos() {
        return this.puertos;
    }
    
    /**
     * Este m�todo devuelve si el nodo tiene puertos libres o no.
     * @return TRUE, si el nodo tiene puertos libres. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean tienePuertosLibres() {
        return this.puertos.isAnyPortAvailable();
    }
    
    /**
     * Este m�todo devuelve el peso del nodo, que debe ser tomado en cuenta por lo
     * algoritmos de encaminamiento para calcular las rutas.
     * @return El peso del LSRA..
     * @since 1.0
     */
    public long obtenerPeso() {
        long peso = 0;
        long pesoC = (long) (this.puertos.getCongestionLevel() * (0.7));
        long pesoMC = (long) ((10*this.matrizConmutacion.getNumberOfEntries())* (0.3));
        peso = pesoC + pesoMC;
        return peso;
    }
    
    /**
     * Este m�todo calcula si el nodo est� bien configurado o no.
     * @return TRUE, si el ndoo est� bien configurado. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean estaBienConfigurado() {
        return this.bienConfigurado;
    }
    /**
     * Este m�todo devuelve si el nodo est� bien configurado y si no, la raz�n.
     * @param t La topolog�a donde est� el nodo incluido.
     * @param recfg TRUE, si se est� reconfigurando el LSR. FALSE si se est� configurando por
     * primera vez.
     * @return CORRECTA, si el nodo est� bien configurado. Un c�digo de error en caso
     * contrario.
     * @since 1.0
     */
    public int comprobar(TTopology t, boolean recfg) {
        this.ponerBienConfigurado(false);
        if (this.obtenerNombre().equals(""))
            return this.SIN_NOMBRE;
        boolean soloEspacios = true;
        for (int i=0; i < this.obtenerNombre().length(); i++){
            if (this.obtenerNombre().charAt(i) != ' ')
                soloEspacios = false;
        }
        if (soloEspacios)
            return this.SOLO_ESPACIOS;
        if (!recfg) {
            TNode tp = t.obtenerPrimerNodoLlamado(this.obtenerNombre());
            if (tp != null)
                return this.NOMBRE_YA_EXISTE;
        } else {
            TNode tp = t.obtenerPrimerNodoLlamado(this.obtenerNombre());
            if (tp != null) {
                if (this.topologia.existeMasDeUnNodoLlamado(this.obtenerNombre())) {
                    return this.NOMBRE_YA_EXISTE;
                }
            }
        }
        this.ponerBienConfigurado(true);
        return this.CORRECTA;
    }
    
    /**
     * Este m�todo transforma el c�digo de error de configuraci�n del nodo en un
     * mensaje aclaratorio.
     * @param e C�digo de error.
     * @return Texto explicativo del c�digo de error.
     * @since 1.0
     */
    public String obtenerMensajeError(int e) {
        switch (e) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLSR.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLSR.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoLSR.NombreNoSoloEspacios"));
        }
        return ("");
    }
    
    /**
     * Este m�todo permite transformar el nodo en una cadena de texto que se puede
     * volcar f�cilmente a disco.
     * @return Una cadena de texto que representa al nodo.
     * @since 1.0
     */
    public String marshall() {
        String cadena = "#LSRA#";
        cadena += this.getID();
        cadena += "#";
        cadena += this.obtenerNombre().replace('#', ' ');
        cadena += "#";
        cadena += this.getIPAddress();
        cadena += "#";
        cadena += this.obtenerEstado();
        cadena += "#";
        cadena += this.obtenerMostrarNombre();
        cadena += "#";
        cadena += this.obtenerEstadisticas();
        cadena += "#";
        cadena += this.obtenerPosicion().x;
        cadena += "#";
        cadena += this.obtenerPosicion().y;
        cadena += "#";
        cadena += this.potenciaEnMb;
        cadena += "#";
        cadena += this.obtenerPuertos().getBufferSizeInMB();
        cadena += "#";
        cadena += this.dmgp.getDMGPSizeInKB();
        cadena += "#";
        return cadena;
    }
    
    /**
     * Este m�todo permite construir sobre la instancia actual, un LSR partiendo de la
     * representaci�n serializada de otro.
     * @param elemento �lemento serializado que se desea deserializar.
     * @return TRUE, si se ha conseguido deserializar correctamente. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean unmarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 13) {
            return false;
        }
        this.ponerIdentificador(Integer.valueOf(valores[2]).intValue());
        this.ponerNombre(valores[3]);
        this.ponerIP(valores[4]);
        this.ponerEstado(Integer.valueOf(valores[5]).intValue());
        this.ponerMostrarNombre(Boolean.valueOf(valores[6]).booleanValue());
        this.ponerEstadisticas(Boolean.valueOf(valores[7]).booleanValue());
        int posX = Integer.valueOf(valores[8]).intValue();
        int posY = Integer.valueOf(valores[9]).intValue();
        this.ponerPosicion(new Point(posX+24, posY+24));
        this.potenciaEnMb = Integer.valueOf(valores[10]).intValue();
        this.obtenerPuertos().setBufferSizeInMB(Integer.valueOf(valores[11]).intValue());
        this.dmgp.setDMGPSizeInKB(Integer.valueOf(valores[12]).intValue());
        return true;
    }
    
    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
     * @return Las estad�sticas del nodo.
     * @since 1.0
     */
    public TStats getStats() {
        return estadisticas;
    }
    
    /**
     * Este m�todo permite establecer el n�mero de puertos que tendr� el nodo.
     * @param num N�mero de puertos del nodo. Como mucho 8.
     * @since 1.0
     */
    public synchronized void ponerPuertos(int num) {
        puertos = new TActivePortSet(num, this);
    }
    
    /**
     * Esta constante indica que la configuraci�n del nodo es correcta.
     * @since 1.0
     */
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que en la configuraci�n del nodo, falta el nombre.
     * @since 1.0
     */
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que, en la configuraci�n del nodo, se ha elegido un nombre
     * que ya est� siendo usado.
     * @since 1.0
     */
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que en la configuraci�n del nodo, el nombre elegido es
     * err�neo porque solo cuenta con espacios.
     * @since 1.0
     */
    public static final int SOLO_ESPACIOS = 3;
    
    private TSwitchingMatrix matrizConmutacion;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int potenciaEnMb;
    private TDMGP dmgp;
    private TGPSRPRequestsMatrix peticionesGPSRP;
    private TLSRAStats estadisticas;
}
