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

import com.manolodominguez.opensimmpls.protocols.TGPSRPPDU;
import com.manolodominguez.opensimmpls.protocols.TTLDPPDU;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSLabel;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.protocols.TTLDPPayload;
import com.manolodominguez.opensimmpls.protocols.TIPv4PDU;
import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPort;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrix;
import com.manolodominguez.opensimmpls.hardware.tldp.TSwitchingMatrixEntry;
import com.manolodominguez.opensimmpls.hardware.ports.TFIFOPortSet;
import com.manolodominguez.opensimmpls.hardware.ports.TPort;
import com.manolodominguez.opensimmpls.hardware.ports.TPortSet;
import com.manolodominguez.opensimmpls.utils.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.Point;
import java.util.Iterator;


/**
 * Esta clase implementa un Label Edge Router (LER) de entrada/salida del dominio
 * MPLS.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TLERNode extends TNode implements ITimerEventListener, Runnable {
    
    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de TNodoLER
     * y otorga unos valores iniciales a los atributos.
     * @param identificador Clabve primaria que permite buscar, encontrar y ordenadr dentro de la topolog�a
     * a esta instancia del LER. Identifica el nodo como unico.
     * @param d Direcci�n IP �nica que tendr� el nodo.
     * @param il generador de identificadores largos. Se usa para que el LER pueda obtener un
 id unico para cada evento que genere.
     * @param t Referencia a la topolog�a a la que pertenece el LER. Le permite hacer
     * comprobaciones, calcular rutas, etc�tera.
     * @since 2.0
     */
    public TLERNode(int identificador, String d, TLongIDGenerator il, TTopology t) {
        super(identificador, d, il, t);
        this.setPorts(TNode.NUM_PUERTOS_LER);
        matrizConmutacion = new TSwitchingMatrix();
        gIdent = new TLongIDGenerator();
        gIdentLDP = new TIDGenerator();
        potenciaEnMb = 512;
        estadisticas = new TLERStats();
    }
    
    /**
     * Este m�todo calcula el n�mero de nanosegundos que se necesitan para conmutar un
     * bit. Se basa en la potencia de conmutaci�n configurada para el LER.
     * @return El n�mero de nanosegundos necesarios para conmutar un bit.
     * @since 2.0
     */
    public double obtenerNsPorBit() {
        long tasaEnBitsPorSegundo = (long) (this.potenciaEnMb*1048576L);
        double nsPorCadaBit = (double) ((double)1000000000.0/(long)tasaEnBitsPorSegundo);
        return nsPorCadaBit;
    }
    
    /**
     * Este m�todo calcula el numero de nanosegundos que son necesarios para conmutar
     * un determinado n�mero de octetos.
     * @param octetos El n�mero de octetos que queremos conmutar.
     * @return El n�mero de nanosegundos necesarios para conmutar el n�mero de octetos
     * especificados.
     * @since 2.0
     */
    public double obtenerNsUsadosTotalOctetos(int octetos) {
        double nsPorCadaBit = obtenerNsPorBit();
        long bitsOctetos = (long) ((long)octetos*(long)8);
        return (double)((double)nsPorCadaBit*(long)bitsOctetos);
    }
    
    /**
     * Este m�todo calcula el n�mero de bits que puede conmutar el nodo con el n�mero
     * de nanosegundos de que dispone actualmente.
     * @return El n�mero de bits m�ximo que puede conmutar el nodo con los nanosegundos de que
     * dispone actualmente.
     * @since 2.0
     */
    public int obtenerLimiteBitsTransmitibles() {
        double nsPorCadaBit = obtenerNsPorBit();
        double maximoBits = (double) ((double)availableNs/(double)nsPorCadaBit);
        return (int) maximoBits;
    }
    
    /**
     * Este m�todo calcula el n�mero de octetos completos que puede transmitir el nodo
     * con el n�mero de nanosegundos de que dispone.
     * @return El n�mero de octetos completos que puede transmitir el nodo en un momento dado.
     * @since 2.0
     */
    public int obtenerOctetosTransmitibles() {
        double maximoBytes = ((double)obtenerLimiteBitsTransmitibles()/(double)8.0);
        return (int) maximoBytes;
    }
    
    /**
     * Este m�todo obtiene la potencia em Mbps con que est� configurado el nodo.
     * @return La potencia de conmutaci�n del nodo en Mbps.
     * @since 2.0
     */
    public int obtenerPotenciaEnMb() {
        return this.potenciaEnMb;
    }
    
    /**
     * Este m�todo permite establecer la potencia de conmutaci�n del nodo en Mbps.
     * @param pot Potencia deseada para el nodo en Mbps.
     * @since 2.0
     */
    public void ponerPotenciaEnMb(int pot) {
        this.potenciaEnMb = pot;
    }
    
    /**
     * Este m�todo obtiene el tama�o del buffer del nodo.
     * @return Tama�o del buffer del nodo en MB.
     * @since 2.0
     */
    public int obtenerTamanioBuffer() {
        return this.getPorts().getBufferSizeInMBytes();
    }
    
    /**
     * Este m�todo permite establecer el tama�o del buffer del nodo.
     * @param tb Tama�o el buffer deseado para el nodo, en MB.
     * @since 2.0
     */
    public void ponerTamanioBuffer(int tb) {
        this.getPorts().setBufferSizeInMB(tb);
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 2.0
     */
    @Override
    public void reset() {
        this.ports.reset();
        matrizConmutacion.reset();
        gIdent.reset();
        gIdentLDP.reset();
        estadisticas.reset();
        estadisticas.activateStats(this.isGeneratingStats());
        this.handleGPSRPPacket();
    }
    
    /**
     * Este m�todo indica el tipo de nodo de que se trata la instancia actual.
     * @return LER. Indica que el nodo es de este tipo.
     * @since 2.0
     */
    @Override
    public int getNodeType() {
        return TNode.LER;
    }
    
    /**
     * Este m�todo inicia el hilo de ejecuci�n del LER, para que entre en
     * funcionamiento. Adem�s controla el tiempo de que dispone el LER para conmutar
     * paquetes.
     * @param evt Evento de reloj que sincroniza la ejecuci�n de los elementos de la topology.
     * @since 2.0
     */
    @Override
    public void receiveTimerEvent(TTimerEvent evt) {
        this.setStepDuration(evt.getStepDuration());
        this.setTimeInstant(evt.getUpperLimit());
        if (this.getPorts().isThereAnyPacketToRoute()) {
            this.availableNs += evt.getStepDuration();
        } else {
            this.handleGPSRPPacket();
            this.availableNs = evt.getStepDuration();
        }
        this.startOperation();
    }
    
    /**
     * Llama a las acciones que se tienen que ejecutar en el transcurso del tic de
     * reloj que el LER estar� en funcionamiento.
     * @since 2.0
     */
    @Override
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        try {
            this.generateSimulationEvent(new TSENodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), this.getPorts().getCongestionLevel()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        comprobarElEstadoDeLasComunicaciones();
        decrementarContadores();
        encaminarPaquetes();
        estadisticas.consolidateData(this.getAvailableTime());
        // Acciones a llevar a cabo durante el tic.
    }
    
    /**
     * Este m�todo comprueba que haya conectividad con sus nodos adyacentes, es decir,
     * que no haya caido ning�n enlace. Si ha caido alg�n enlace, entonces genera la
     * correspondiente se�alizaci�n para notificar este hecho.
     * @since 2.0
     */
    public void comprobarElEstadoDeLasComunicaciones() {
        boolean eliminar = false;
        TSwitchingMatrixEntry emc = null;
        int idPuerto = 0;
        TPort puertoSalida = null;
        TPort puertoEntrada = null;
        TLink et = null;
        TLink et2 = null;
        matrizConmutacion.getMonitor().lock();
        Iterator it = matrizConmutacion.getEntriesIterator();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                idPuerto = emc.getOutgoingPortID();
                if ((idPuerto >= 0) && (idPuerto < this.ports.getNumberOfPorts())) {
                    puertoSalida = this.ports.getPort(idPuerto);
                    if (puertoSalida != null) {
                        et = puertoSalida.getLink();
                        if (et != null) {
                            if ((et.isBroken()) && (emc.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                puertoEntrada = this.ports.getPort(emc.getIncomingPortID());
                                et = puertoEntrada.getLink();
                                if (et.getLinkType() == TLink.INTERNAL) {
                                    labelWithdrawal(emc, emc.getIncomingPortID());
                                } else {
                                    eliminar = true;
                                }
                            }
                        }
                    }
                }
                idPuerto = emc.getIncomingPortID();
                if ((idPuerto >= 0) && (idPuerto < this.ports.getNumberOfPorts())) {
                    puertoEntrada = this.ports.getPort(idPuerto);
                    if (puertoEntrada != null) {
                        et = puertoEntrada.getLink();
                        if (et != null) {
                            if ((et.isBroken()) && (emc.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                puertoSalida = this.ports.getPort(emc.getOutgoingPortID());
                                et = puertoSalida.getLink();
                                if (et.getLinkType() == TLink.INTERNAL) {
                                    labelWithdrawal(emc, emc.getOutgoingPortID());
                                } else {
                                    eliminar = false;
                                }
                            }
                        }
                    }
                }
                if ((emc.getIncomingPortID() >= 0) && ((emc.getOutgoingPortID() >= 0))) {
                    et = ports.getPort(emc.getIncomingPortID()).getLink();
                    et2 = ports.getPort(emc.getOutgoingPortID()).getLink();
                    if (et.isBroken() && et2.isBroken()) {
                        eliminar = true;
                    }
                    if (et.isBroken() && (et2.getLinkType() == TLink.EXTERNAL)) {
                        eliminar = true;
                    }
                    if ((et.getLinkType() == TLink.EXTERNAL) && et2.isBroken()) {
                        eliminar = true;
                    }
                } else {
                    eliminar = true;
                }
                if (eliminar) {
                    it.remove();
                }
            } else {
                it.remove();
            }
        }
        matrizConmutacion.getMonitor().unLock();
    }
    
    /**
     * Este m�todo lee del puerto que corresponda seg�n el turno Round Robin
     * consecutivamente hasta que se termina el cr�dito. Si tiene posibilidad de
     * conmutar y/o encaminar un paquete, lo hace, llamando para ello a los m�todos
     * correspondiente segun el paquete. Si el paquete est� mal formado o es
     * desconocido, lo descarta.
     * @since 2.0
     */
    public void encaminarPaquetes() {
        boolean conmute = false;
        int puertoLeido = 0;
        TAbstractPDU paquete = null;
        int octetosQuePuedoMandar = this.obtenerOctetosTransmitibles();
        while (this.getPorts().canSwitchPacket(octetosQuePuedoMandar)) {
            conmute = true;
            paquete = this.ports.getNextPacket();
            puertoLeido = ports.getReadPort();
            if (paquete != null) {
                if (paquete.getType() == TAbstractPDU.IPV4) {
                    conmutarIPv4((TIPv4PDU) paquete, puertoLeido);
                } else if (paquete.getType() == TAbstractPDU.TLDP) {
                    conmutarTLDP((TTLDPPDU) paquete, puertoLeido);
                } else if (paquete.getType() == TAbstractPDU.MPLS) {
                    conmutarMPLS((TMPLSPDU) paquete, puertoLeido);
                } else if (paquete.getType() == TAbstractPDU.GPSRP) {
                    conmutarGPSRP((TGPSRPPDU) paquete, puertoLeido);
                } else {
                    this.availableNs += obtenerNsUsadosTotalOctetos(paquete.getSize());
                    discardPacket(paquete);
                }
                this.availableNs -= obtenerNsUsadosTotalOctetos(paquete.getSize());
                octetosQuePuedoMandar = this.obtenerOctetosTransmitibles();
            }
        }
        if (conmute) {
            this.handleGPSRPPacket();
        } else {
            this.increaseStepsWithoutEmitting();
        }
    }
    
    /**
     * Este m�todo conmuta un paquete GPSRP.
     * @param paquete Paquete GPSRP a conmutar.
     * @param pEntrada Puerto por el que ha entrado el paquete.
     * @since 2.0
     */
    public void conmutarGPSRP(TGPSRPPDU paquete, int pEntrada) {
        if (paquete != null) {
            int mensaje = paquete.getGPSRPPayload().getGPSRPMessageType();
            int flujo = paquete.getGPSRPPayload().getFlowID();
            int idPaquete = paquete.getGPSRPPayload().getPacketID();
            String IPDestinoFinal = paquete.getIPv4Header().getTailEndIPAddress();
            TFIFOPort pSalida = null;
            if (IPDestinoFinal.equals(this.getIPv4Address())) {
                // Un LER no entiende peticiones GPSRP, por tanto no pueder
                // haber mensajes GPSRP dirigidos a �l.
                this.discardPacket(paquete);
            } else {
                String IPSalida = this.topology.obtenerIPSalto(this.getIPv4Address(), IPDestinoFinal);
                pSalida = (TFIFOPort) this.ports.getLocalPortConnectedToANodeWithIPAddress(IPSalida);
                if (pSalida != null) {
                    pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.GPSRP));
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
     * Este m�todo comprueba si existe una entrada en la tabla de encaminamiento para
     * el paquete entrante. Si no es as�, clasifica el paquete y, si es necesario,
     * reencola el paquete y solicita una etiqueta para poder enviarlo. Una vez que
     * tiene entrada en la tabla de encaminamiento, reenv�a el paquete hacia el
     * interior del dominio MPLS o hacia el exterior, segun corresponda.
     * @param paquete Paquete IPv4 de entrada.
     * @param pEntrada Puerto por el que ha accedido al nodo el paquete.
     * @since 2.0
     */
    public void conmutarIPv4(TIPv4PDU paquete, int pEntrada) {
        int valorFEC = clasificarPaquete(paquete);
        String IPDestinoFinal = paquete.getIPv4Header().getTailEndIPAddress();
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(pEntrada, valorFEC, TSwitchingMatrixEntry.FEC_ENTRY);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizFEC(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    solicitarTLDP(emc);
                }
                this.ports.getPort(pEntrada).reEnqueuePacket(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                solicitarTLDP(emc);
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operacion = emc.getLabelStackOperation();
                if (operacion == TSwitchingMatrixEntry.UNDEFINED) {
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PUSH_LABEL) {
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        TMPLSPDU paqueteMPLS = this.crearPaqueteMPLS(paquete, emc);
                        pSalida.putPacketOnLink(paqueteMPLS, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.POP_LABEL) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.SWAP_LABEL) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                discardPacket(paquete);
            }
        } else {
            discardPacket(paquete);
        }
    }
    
    /**
     * Este m�todo se llama cuando se recibe un paquete TLDP con informaci�n sobre las
 etiquetas a use. El m�todo realiza sobre las matriz de encaminamiento la
     * operaci�n que sea necesario y propaga el cambio al nodo adyacente que
     * corresponda.
     * @param paquete Paquete TLDP recibido.
     * @param pEntrada Puerto por el que se ha recibido el paquete TLDP.
     * @since 2.0
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
     * Este m�todo comprueba si existe una entrada en la tabla de encaminamiento para
     * el paquete entrante. Si no es as�, clasifica el paquete y, si es necesario,
     * reencola el paquete y solicita una etiqueta para poder enviarlo. Una vez que
     * tiene entrada en la tabla de encaminamiento, reenv�a el paquete hacia el
     * siguiente nodo del dominio MPLS o hacia el exterior, segun corresponda.
     * @param paquete Paquete MPLS recibido.
     * @param pEntrada Puerto por el que ha llegado el paquete MPLS recibido.
     * @since 2.0
     */
    public void conmutarMPLS(TMPLSPDU paquete, int pEntrada) {
        TMPLSLabel eMPLS = null;
        TSwitchingMatrixEntry emc = null;
        boolean conEtiqueta1 = false;
        if (paquete.getLabelStack().getTop().getLabel() == 1) {
            eMPLS = paquete.getLabelStack().getTop();
            paquete.getLabelStack().popTop();
            conEtiqueta1 = true;
        }
        int valorLABEL = paquete.getLabelStack().getTop().getLabel();
        String IPDestinoFinal = paquete.getIPv4Header().getTailEndIPAddress();
        emc = matrizConmutacion.getEntry(pEntrada, valorLABEL, TSwitchingMatrixEntry.LABEL_ENTRY);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizLABEL(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    solicitarTLDP(emc);
                }
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                this.ports.getPort(pEntrada).reEnqueuePacket(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                solicitarTLDP(emc);
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                this.ports.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operacion = emc.getLabelStackOperation();
                if (operacion == TSwitchingMatrixEntry.UNDEFINED) {
                    if (conEtiqueta1) {
                        paquete.getLabelStack().pushTop(eMPLS);
                    }
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PUSH_LABEL) {
                        TMPLSLabel empls = new TMPLSLabel();
                        empls.setBoS(false);
                        empls.setEXP(0);
                        empls.setLabel(emc.getOutgoingLabel());
                        empls.setTTL(paquete.getLabelStack().getTop().getTTL()-1);
                        paquete.getLabelStack().pushTop(empls);
                        if (conEtiqueta1) {
                            paquete.getLabelStack().pushTop(eMPLS);
                            paquete.setSubtype(TAbstractPDU.MPLS_GOS);
                        } else {
                            paquete.setSubtype(TAbstractPDU.MPLS);
                        }
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.POP_LABEL) {
                        if (paquete.getLabelStack().getTop().getBoS()) {
                            TIPv4PDU paqueteIPv4 = this.crearPaqueteIPv4(paquete, emc);
                            if (conEtiqueta1) {
                                paqueteIPv4.setSubtype(TAbstractPDU.IPV4_GOS);
                            }
                            TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                            pSalida.putPacketOnLink(paqueteIPv4, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        } else {
                            paquete.getLabelStack().popTop();
                            if (conEtiqueta1) {
                                paquete.getLabelStack().pushTop(eMPLS);
                            }
                            TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                            pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        }
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.SWAP_LABEL) {
                        paquete.getLabelStack().getTop().setLabel(emc.getOutgoingLabel());
                        if (conEtiqueta1) {
                            paquete.getLabelStack().pushTop(eMPLS);
                        }
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (conEtiqueta1) {
                    paquete.getLabelStack().pushTop(eMPLS);
                }
                discardPacket(paquete);
            }
        } else {
            if (conEtiqueta1) {
                paquete.getLabelStack().pushTop(eMPLS);
            }
            discardPacket(paquete);
        }
    }
    
    /**
     * Este m�todo trata una petici�n de etiquetas.
     * @param paquete Petici�n de etiquetas recibida de otro nodo.
     * @param pEntrada Puerto de entrada de la petici�n de etiqueta.
     * @since 2.0
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
                labelWithdrawal(emc, pEntrada);
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
     * @since 2.0
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
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    matrizConmutacion.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                } else if (etiquetaActual > 15) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    labelWithdrawal(emc, emc.getOppositePortID(pEntrada));
                } else {
                    discardPacket(paquete);
                }
            } else {
                enviarEliminacionOkTLDP(emc, pEntrada);
                matrizConmutacion.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
            }
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de confirmaci�n de etiqueta.
     * @param paquete Confirmaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de etiquetas.
     * @since 2.0
     */
    public void tratarSolicitudOkTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                emc.setOutgoingLabel(paquete.getTLDPPayload().getLabel());
                if (emc.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setLabelOrFEC(matrizConmutacion.getNewLabel());
                }
                TInternalLink et = (TInternalLink) ports.getPort(emc.getOutgoingPortID()).getLink();
                if (et != null) {
                    if (emc.aBackupLSPHasBeenRequested()) {
                        et.setAsUsedByABackupLSP();
                    } else {
                        et.setAsUsedByALSP();
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
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de denegaci�n de etiqueta.
     * @param paquete Paquete de denegaci�n de etiquetas recibido.
     * @param pEntrada Puerto por el que se ha recibido la denegaci�n de etiquetas.
     * @since 2.0
     */
    public void tratarSolicitudNoTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(paquete.getTLDPPayload().getTLDPIdentifier());
        if (emc == null) {
            discardPacket(paquete);
        } else {
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
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de confirmaci�n de eliminaci�n de etiqueta.
     * @param paquete Paquete de confirmaci�n e eliminaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de eliminaci�n de etiqueta.
     * @since 2.0
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
                if (emc.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    TPort pSalida = ports.getPort(pEntrada);
                    TLink et = pSalida.getLink();
                    if (et.getLinkType() == TLink.INTERNAL) {
                        TInternalLink ei = (TInternalLink) et;
                        if (emc.aBackupLSPHasBeenRequested()) {
                            ei.unlinkFromABackupLSP();
                        } else {
                            ei.unlinkFromALSP();
                        }
                    }
                }
                matrizConmutacion.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
             } else if (etiquetaActual > 15) {
                 discardPacket(paquete);
             } else {
                 discardPacket(paquete);
             }
        }
    }
    
    /**
     * Este m�todo env�a una etiqueta al nodo que indique la entrada en la
     * matriz de conmutaci�n especificada.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void enviarSolicitudOkTLDP(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPv4Address();
                String IPDestino = ports.getIPv4OfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_OK);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPv4Address());
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.getTLDPPayload().setLabel(emc.getLabelOrFEC());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort pSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPDestino);
                        pSalida.putPacketOnLink(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, nuevoTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
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
     * @since 2.0
     */
    public void enviarSolicitudNoTLDP(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                String IPLocal = this.getIPv4Address();
                String IPDestino = ports.getIPv4OfNodeLinkedTo(emc.getIncomingPortID());
                if (IPDestino != null) {
                    TTLDPPDU nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REQUEST_DENIED);
                        nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPv4Address());
                        nuevoTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            nuevoTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                        TPort pSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPDestino);
                        pSalida.putPacketOnLink(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, nuevoTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
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
     * @since 2.0
     * @param puerto Puierto por el que se debe enviar la confirmaci�n de eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void enviarEliminacionOkTLDP(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            String IPLocal = this.getIPv4Address();
            String IPDestino = ports.getIPv4OfNodeLinkedTo(puerto);
            if (IPDestino != null) {
                TTLDPPDU nuevoTLDP = null;
                try {
                    nuevoTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nuevoTLDP != null) {
                    nuevoTLDP.getTLDPPayload().setTLDPMessageType(TTLDPPayload.LABEL_REVOMAL_REQUEST_OK);
                    nuevoTLDP.getTLDPPayload().setTargetIPAddress(emc.getTailEndIPv4Address());
                    nuevoTLDP.getTLDPPayload().setLabel(TSwitchingMatrixEntry.UNDEFINED);
                    if (emc.getOutgoingPortID() == puerto) {
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
                    TPort pSalida = ports.getPort(puerto);
                    pSalida.putPacketOnLink(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, nuevoTLDP.getSize()));
                        this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo solicita una etiqueta al nodo que se especifica en la entrada de la
     * matriz de conmutaci�n correspondiente.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void solicitarTLDP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = emc.getTailEndIPv4Address();
        if (emc.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
            String IPSalto = topology.obtenerIPSalto(IPLocal, IPDestinoFinal);
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
                    TPort pSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo env�a una eliminaci�n de etiqueta al nodo especificado por le
     * entrada de la matriz de conmutaci�n correspondiente.
     * @since 2.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     */
    public void labelWithdrawal(TSwitchingMatrixEntry emc, int puerto) {
        if (emc != null) {
            emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
            String IPLocal = this.getIPv4Address();
            String IPDestinoFinal = emc.getTailEndIPv4Address();
            String IPSalto = ports.getIPv4OfNodeLinkedTo(puerto);
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
                    } else if (emc.getIncomingPortID() == puerto) {
                        paqueteTLDP.getTLDPPayload().setTLDPIdentifier(emc.getUpstreamTLDPSessionID());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD_BACKUP);
                        } else {
                            paqueteTLDP.setLocalTarget(TTLDPPDU.DIRECTION_BACKWARD);
                        }
                    }
                    TPort pSalida = ports.getPort(puerto);
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este m�todo reenv�a todas las peticiones pendientes de contestaci�n de una
     * entrada de la matriz de conmutaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void solicitarTLDPTrasTimeout(TSwitchingMatrixEntry emc) {
        if (emc != null) {
            String IPLocal = this.getIPv4Address();
            String IPDestinoFinal = emc.getTailEndIPv4Address();
            String IPSalto = ports.getIPv4OfNodeLinkedTo(emc.getOutgoingPortID());
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
                    TPort pSalida = ports.getPort(emc.getOutgoingPortID());
                    if (pSalida != null) {
                        pSalida.putPacketOnLink(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP, paqueteTLDP.getSize()));
                            this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TAbstractPDU.TLDP));
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
     * @since 2.0
     * @param puerto Puerto por el que se debe enviar la eliminaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry emc, int puerto){
        labelWithdrawal(emc, puerto);
    }
    
    /**
     * Este m�todo reenv�a todas las eliminaciones de etiquetas pendientes de una
 entrada de la matriz de conmutaci�n a todos los ports necesarios.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
     * @since 2.0
     */
    public void labelWithdrawalAfterTimeout(TSwitchingMatrixEntry emc){
        labelWithdrawal(emc, emc.getIncomingPortID());
        labelWithdrawal(emc, emc.getOutgoingPortID());
    }
    
    /**
     * Este m�todo decrementa los contadores de retransmisi�n existentes para este nodo.
     * @since 2.0
     */
    public void decrementarContadores() {
        TSwitchingMatrixEntry emc = null;
        this.matrizConmutacion.getMonitor().lock();
        Iterator it = this.matrizConmutacion.getEntriesIterator();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                emc.decreaseTimeOut(this.getTickDuration());
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
                        labelWithdrawalAfterTimeout(emc);
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
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n con los datos de
     * un paquete TLDP entrante.
     * @param paqueteSolicitud Paquete TLDP entrante, de solicitud de etiqueta.
     * @param pEntrada Puerto de entrada del paquete TLDP.
     * @return La entrada de la matriz de conmutaci�n, ya creada, insertada e inicializada.
     * @since 2.0
     */
    public TSwitchingMatrixEntry crearEntradaAPartirDeTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.getTLDPPayload().getTLDPIdentifier();
        TPort puertoEntrada = ports.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.getTLDPPayload().getTailEndIPAddress();
        String IPSalto = topology.obtenerIPSalto(this.getIPv4Address(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalto);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
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
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().getLinkType();
            }
            if (puertoSalida != null) {
                enlaceDestino = puertoSalida.getLink().getLinkType();
            }
            if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.POP_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.setLabelOrFEC(matrizConmutacion.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            matrizConmutacion.addEntry(emc);
        }
        return emc;
    }
    
    /**
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n bas�ndose en un
     * paquete IPv4 recibido.
     * @param paqueteIPv4 Paquete IPv4 recibido.
     * @param pEntrada Puerto por el que ha llegado el paquete IPv4.
     * @return La entrada de la matriz de conmutaci�n, creada, insertada e inicializada.
     * @since 2.0
     */
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizFEC(TIPv4PDU paqueteIPv4, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = paqueteIPv4.getIPv4Header().getTailEndIPAddress();
        String IPSalida = topology.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = ports.getPort(pEntrada);
            TPort puertoSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalida);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setLabelOrFEC(clasificarPaquete(paqueteIPv4));
            emc.setEntryIsForBackupLSP(false);
            if (puertoSalida != null) {
                emc.setOutgoingPortID(puertoSalida.getPortID());
                enlaceDestino = puertoSalida.getLink().getLinkType();
            } else {
                emc.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().getLinkType();
            }
            if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.FEC_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                // No es posible
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                // No es posible
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.setLabelOrFEC(matrizConmutacion.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            matrizConmutacion.addEntry(emc);
        }
        return emc;
    }
    
    /**
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n bas�ndose en un
     * paquete MPLS recibido.
     * @param paqueteMPLS Paquete MPLS recibido.
     * @param pEntrada Puerto por el que ha llegado el paquete MPLS.
     * @return La entrada de la matriz de conmutaci�n, creada, insertada e inicializada.
     * @since 2.0
     */
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizLABEL(TMPLSPDU paqueteMPLS, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPv4Address();
        String IPDestinoFinal = paqueteMPLS.getIPv4Header().getTailEndIPAddress();
        String IPSalida = topology.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = ports.getPort(pEntrada);
            TPort puertoSalida = ports.getLocalPortConnectedToANodeWithIPAddress(IPSalida);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setEntryIsForBackupLSP(false);
            emc.setLabelOrFEC(paqueteMPLS.getLabelStack().getTop().getLabel());
            if (puertoSalida != null) {
                emc.setOutgoingPortID(puertoSalida.getPortID());
                enlaceDestino = puertoSalida.getLink().getLinkType();
            } else {
                emc.setOutgoingPortID(TSwitchingMatrixEntry.UNDEFINED);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().getLinkType();
            }
            if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.PUSH_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.EXTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.POP_LABEL);
            } else if ((enlaceOrigen == TLink.INTERNAL) && (enlaceDestino == TLink.INTERNAL)) {
                emc.setEntryType(TSwitchingMatrixEntry.LABEL_ENTRY);
                emc.setLabelStackOperation(TSwitchingMatrixEntry.SWAP_LABEL);
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.setLabelOrFEC(matrizConmutacion.getNewLabel());
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_ASSIGNED);
            }
            try {
                emc.setLocalTLDPSessionID(gIdentLDP.getNew());
            } catch (Exception e) {
                e.printStackTrace();
            }
            matrizConmutacion.addEntry(emc);
        }
        return emc;
    }
    
    /**
     * Este m�todo toma un paquete IPv4 y la entrada de la matriz de conmutaci�n
     * asociada al mismo y crea un paquete MPLS etiquetado correctamente que contiene
     * dicho paquete IPv4 listo para ser transmitido hacia el interior del dominio.
     * @param paqueteIPv4 Paquete IPv4 que se debe etiquetar.
     * @param emc Entrada de la matriz de conmutaci�n asociada al paquete IPv4 que se desea
     * etiquetar.
     * @return El paquete IPv4 de entrada, convertido en un paquete MPLS correctamente
     * etiquetado.
     * @since 2.0
     */
    public TMPLSPDU crearPaqueteMPLS(TIPv4PDU paqueteIPv4, TSwitchingMatrixEntry emc) {
        TMPLSPDU paqueteMPLS = null;
        try {
            paqueteMPLS = new TMPLSPDU(gIdent.getNextID(), paqueteIPv4.getIPv4Header().getOriginIPv4Address(), paqueteIPv4.getIPv4Header().getTailEndIPAddress(), paqueteIPv4.getSize());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        paqueteMPLS.setHeader(paqueteIPv4.getIPv4Header());
        paqueteMPLS.setTCPPayload(paqueteIPv4.getTCPPayload());
        paqueteMPLS.setSubtype(TAbstractPDU.MPLS);
        TMPLSLabel empls = new TMPLSLabel();
        empls.setBoS(true);
        empls.setEXP(0);
        empls.setLabel(emc.getOutgoingLabel());
        empls.setTTL(paqueteIPv4.getIPv4Header().getTTL()-1);
        paqueteMPLS.getLabelStack().pushTop(empls);
        paqueteIPv4 = null;
        try {
            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteMPLS.getSubtype(), paqueteMPLS.getSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paqueteMPLS;
    }
    
    /**
     * Este m�todo toma como par�metro un paquete MPLS y su entrada en la matriz de
     * conmutaci�n asociada. Extrae del paquete MPLS el paquete IP correspondiente y
     * actualiza sus valores correctamente.
     * @param paqueteMPLS Paquete MPLS cuyo contenido de nivel IPv4 se desea extraer.
     * @param emc Entrada de la matriz de conmutaci�n asociada al paquete MPLS.
     * @return Paquete IPv4 que corresponde al paquete MPLS una vez que se ha eliminado toda la
     * informaci�n MLPS; que se ha desetiquetado.
     * @since 2.0
     */
    public TIPv4PDU crearPaqueteIPv4(TMPLSPDU paqueteMPLS, TSwitchingMatrixEntry emc) {
        TIPv4PDU paqueteIPv4 = null;
        try {
            paqueteIPv4 = new TIPv4PDU(gIdent.getNextID(), paqueteMPLS.getIPv4Header().getOriginIPv4Address(), paqueteMPLS.getIPv4Header().getTailEndIPAddress(), paqueteMPLS.getTCPPayload().getSize());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        paqueteIPv4.setHeader(paqueteMPLS.getIPv4Header());
        paqueteIPv4.setTCPPayload(paqueteMPLS.getTCPPayload());
        paqueteIPv4.getIPv4Header().setTTL(paqueteMPLS.getLabelStack().getTop().getTTL());
        if (paqueteIPv4.getIPv4Header().getOptionsField().isUsed()) {
            paqueteIPv4.setSubtype(TAbstractPDU.IPV4_GOS);
        } else {
            paqueteIPv4.setSubtype(TAbstractPDU.IPV4);
        }
        try {
            this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteIPv4.getSubtype(), paqueteIPv4.getSize()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        paqueteMPLS = null;
        return paqueteIPv4;
    }
    
    /**
     * Este m�todo comprueba si un paquete recibido es un paquete del interior del
     * dominio MPLS o es un paquete externo al mismo.
     * @param paquete Paquete que ha llegado al nodo.
     * @param pEntrada Puerto por el que ha llegado el paquete al nodo.
     * @return true, si el paquete es exterior al dominio MPLS. false en caso contrario.
     * @since 2.0
     */
    public boolean esUnPaqueteExterno(TAbstractPDU paquete, int pEntrada) {
        if (paquete.getType() == TAbstractPDU.IPV4)
            return true;
        TPort pe = ports.getPort(pEntrada);
        if (pe.getLink().getLinkType() == TLink.EXTERNAL)
            return true;
        return false;
    }
    
    /**
     * Este m�todo contabiliza un paquete recibido o conmutado en las estad�sticas del
     * nodo.
     * @param paquete paquete que se desa contabilizar.
     * @param deEntrada TRUE, si el paquete se ha recibido en el nodo. FALSE so el paquete ha salido del
     * nodo.
     * @since 2.0
     */
    public void contabilizarPaquete(TAbstractPDU paquete, boolean deEntrada) {
    }
    
    /**
     * Este m�todo descarta un paquete en el nodo y refleja dicho descarte en las
     * estad�sticas del nodo.
     * @param paquete Paquete que se quiere descartar.
     * @since 2.0
     */
    public void discardPacket(TAbstractPDU paquete) {
        try {
            this.generateSimulationEvent(new TSEPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
            this.estadisticas.addStatEntry(paquete, TStats.DISCARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paquete = null;
    }
    
    /**
     * Este m�todo toma como parametro un paquete, supuestamente sin etiquetar, y lo
     * clasifica. Esto significa que determina el FEC_ENTRY al que pertenece el paquete.
 Este valor se calcula como el c�digo HASH practicado a la concatenaci�n de la IP
 de origen y la IP de destino. En la pr�ctica esto significa que paquetes con el
 mismo origen y con el mismo destino pertenecer�n al mismo FEC_ENTRY.
     * @param paquete El paquete que se desea clasificar.
     * @return El FEC_ENTRY al que pertenece el paquete pasado por par�metros.
     * @since 2.0
     */
    public int clasificarPaquete(TAbstractPDU paquete) {
        String IPOrigen = paquete.getIPv4Header().getOriginIPv4Address();
        String IPDestino = paquete.getIPv4Header().getTailEndIPAddress();
        String cadenaFEC = cadenaFEC = IPOrigen + IPDestino;
        return cadenaFEC.hashCode();
    }
    
    /**
     * Este m�todo permite el acceso al conjunto de ports del nodo.
     * @return El conjunto de ports del nodo.
     * @since 2.0
     */
    public TPortSet getPorts() {
        return this.ports;
    }
    
    /**
     * Este m�todo calcula si el nodo tiene ports libres o no.
     * @return true, si el nodo tiene ports libres. false en caso contrario.
     * @since 2.0
     */
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }
    
    /**
     * Este m�todo calcula el peso del nodo. Se utilizar� para calcular rutas con costo
     * menor. En el nodo LER el pero ser� siempre nulo (cero).
     * @return 0. El peso siempre ser� nulo en un LER.
     * @since 2.0
     */
    public long getRoutingWeight() {
        long peso = 0;
        long pesoC = (long) (this.ports.getCongestionLevel() * (0.7));
        long pesoMC = (long) ((10*this.matrizConmutacion.getNumberOfEntries())* (0.3));
        peso = pesoC + pesoMC;
        return peso;
    }
    
    /**
     * Este m�todo comprueba si la isntancia actual es el LER de salida del dominio
     * MPLS para una IP dada.
     * @param ip IP de destino del tr�fico, para la cual queremos averiguar si el LER es nodo de
     * salida.
     * @return true, si el LER es de salida del dominio para tr�fico dirigido a esa IP. false
     * en caso contrario.
     * @since 2.0
     */
    public boolean soyLERDeSalida(String ip) {
        TPort p = ports.getLocalPortConnectedToANodeWithIPAddress(ip);
        if (p != null)
            if (p.getLink().getLinkType() == TLink.EXTERNAL)
                return true;
        return false;
    }
    
    /**
     * Este m�todo permite el acceso a la matriz de conmutaci�n de LER.
     * @return La matriz de conmutaci�n del LER.
     * @since 2.0
     */
    public TSwitchingMatrix obtenerMatrizConmutacion() {
        return matrizConmutacion;
    }
    
    /**
     * Este m�todo comprueba que la configuraci�n de LER sea la correcta.
     * @return true, si el LER est� bien configurado. false en caso contrario.
     * @since 2.0
     */
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }
    
    /**
     * Este m�todo comprueba que una cierta configuraci�n es v�lida.
     * @param t Topolog�a a la que pertenece el LER.
     * @param recfg true si se trata de una reconfiguraci�n. false en caso contrario.
     * @return CORRECTA, si la configuraci�n es correcta. Un c�digo de error en caso contrario.
     * @since 2.0
     */
    public int validateConfig(TTopology t, boolean recfg) {
        this.setWellConfigured(false);
        if (this.getName().equals(""))
            return this.SIN_NOMBRE;
        boolean soloEspacios = true;
        for (int i=0; i < this.getName().length(); i++){
            if (this.getName().charAt(i) != ' ')
                soloEspacios = false;
        }
        if (soloEspacios)
            return this.SOLO_ESPACIOS;
        if (!recfg) {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null)
                return this.NOMBRE_YA_EXISTE;
        } else {
            TNode tp = t.setFirstNodeNamed(this.getName());
            if (tp != null) {
                if (this.topology.thereIsMoreThanANodeNamed(this.getName())) {
                    return this.NOMBRE_YA_EXISTE;
                }
            }
        }
        this.setWellConfigured(true);
        return this.CORRECTA;
    }
    
    /**
     * Este m�todo toma un codigo de error y genera un mensaje textual del mismo.
     * @param e El c�digo de error para el cual queremos una explicaci�n textual.
     * @return Cadena de texto explicando el error.
     * @since 2.0
     */
    public String getErrorMessage(int e) {
        switch (e) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("TConfigLER.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("TConfigLER.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("TNodoLER.NombreNoSoloEspacios"));
        }
        return ("");
    }
    
    /**
     * Este m�todo forma una cadena de texto que representa al LER y toda su
     * configuraci�n. Sirve para almacenar el LER en disco.
     * @return Una cadena de texto que representa un a este LER.
     * @since 2.0
     */
    public String marshall() {
        String cadena = "#LER#";
        cadena += this.getID();
        cadena += "#";
        cadena += this.getName().replace('#', ' ');
        cadena += "#";
        cadena += this.getIPv4Address();
        cadena += "#";
        cadena += this.getStatus();
        cadena += "#";
        cadena += this.getShowName();
        cadena += "#";
        cadena += this.isGeneratingStats();
        cadena += "#";
        cadena += this.obtenerPosicion().x;
        cadena += "#";
        cadena += this.obtenerPosicion().y;
        cadena += "#";
        cadena += this.potenciaEnMb;
        cadena += "#";
        cadena += this.getPorts().getBufferSizeInMBytes();
        cadena += "#";
        return cadena;
    }
    
    /**
     * Este m�todo toma como par�metro una cadena de texto que debe pertencer a un LER
     * serializado y configura esta instancia con los valores de dicha caddena.
     * @param elemento LER serializado.
     * @return true, si no ha habido errores y la instancia actual est� bien configurada. false
     * en caso contrario.
     * @since 2.0
     */
    public boolean unMarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 12) {
            return false;
        }
        this.setID(Integer.valueOf(valores[2]).intValue());
        this.setName(valores[3]);
        this.setIPAddress(valores[4]);
        this.setStatus(Integer.valueOf(valores[5]).intValue());
        this.setShowName(Boolean.valueOf(valores[6]).booleanValue());
        this.setGenerateStats(Boolean.valueOf(valores[7]).booleanValue());
        int posX = Integer.valueOf(valores[8]).intValue();
        int posY = Integer.valueOf(valores[9]).intValue();
        this.setPosition(new Point(posX+24, posY+24));
        this.potenciaEnMb = Integer.valueOf(valores[10]).intValue();
        this.getPorts().setBufferSizeInMB(Integer.valueOf(valores[11]).intValue());
        return true;
    }
    
    /**
     * Este m�todo permite acceder directamente a las estadisticas del nodo.
     * @return Las estad�sticas del nodo.
     * @since 2.0
     */
    public TStats getStats() {
        return estadisticas;
    }
    
    /**
     * Este m�todo permite establecer el n�mero de ports que tendr� el nodo.
     * @param num N�mero de ports deseado para el nodo. Como mucho, 8 ports.
     * @since 2.0
     */
    public synchronized void setPorts(int num) {
        ports = new TFIFOPortSet(num, this);
    }
    
    /**
     * Este m�todo no hace nada en un LSR. En un nodo activoPermitir� solicitar
     * a un nodo activo la retransmisi�n de un paquete.
     * @param paquete Paquete cuya retransmisi�n se est� solicitando.
     * @param pSalida Puerto por el que se enviar� la solicitud.
     * @since 2.0
     */
    public void runGPSRP(TMPLSPDU paquete, int pSalida) {
    }
    
    /**
     * Esta constante indica que la configuraci�n del nodo LER esta correcta, que no
     * contiene errores.
     * @since 2.0
     */
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que el nombre del nodo LER no est� definido.
     * @since 2.0
     */
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que el nombre especificado para el LER ya est� siendo
 usado por otro nodo de la topology.
     * @since 2.0
     */
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que el nombre que se ha definido para el LER contiene s�lo
     * constantes.
     * @since 2.0
     */
    public static final int SOLO_ESPACIOS = 3;
    
    private TSwitchingMatrix matrizConmutacion;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int potenciaEnMb;
    private TLERStats estadisticas;
}
