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
import simMPLS.protocols.TAbstractPDU;
import simMPLS.protocols.TMPLSLabel;
import simMPLS.protocols.TMPLSPDU;
import simMPLS.protocols.TTLDPPayload;
import simMPLS.protocols.TIPv4PDU;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TFIFOPort;
import simMPLS.hardware.tldp.TSwitchingMatrix;
import simMPLS.hardware.tldp.TSwitchingMatrixEntry;
import simMPLS.hardware.ports.TFIFOPortSet;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TPortSet;
import simMPLS.utils.EIDGeneratorOverflow;
import simMPLS.utils.TIDGenerator;
import simMPLS.utils.TLongIDGenerator;
import java.awt.*;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.data.*;

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
     * @since 1.0
     */
    public TLERNode(int identificador, String d, TLongIDGenerator il, TTopology t) {
        super(identificador, d, il, t);
        this.ponerPuertos(super.NUM_PUERTOS_LER);
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    public int obtenerLimiteBitsTransmitibles() {
        double nsPorCadaBit = obtenerNsPorBit();
        double maximoBits = (double) ((double)nsDisponibles/(double)nsPorCadaBit);
        return (int) maximoBits;
    }
    
    /**
     * Este m�todo calcula el n�mero de octetos completos que puede transmitir el nodo
     * con el n�mero de nanosegundos de que dispone.
     * @return El n�mero de octetos completos que puede transmitir el nodo en un momento dado.
     * @since 1.0
     */
    public int obtenerOctetosTransmitibles() {
        double maximoBytes = ((double)obtenerLimiteBitsTransmitibles()/(double)8.0);
        return (int) maximoBytes;
    }
    
    /**
     * Este m�todo obtiene la potencia em Mbps con que est� configurado el nodo.
     * @return La potencia de conmutaci�n del nodo en Mbps.
     * @since 1.0
     */
    public int obtenerPotenciaEnMb() {
        return this.potenciaEnMb;
    }
    
    /**
     * Este m�todo permite establecer la potencia de conmutaci�n del nodo en Mbps.
     * @param pot Potencia deseada para el nodo en Mbps.
     * @since 1.0
     */
    public void ponerPotenciaEnMb(int pot) {
        this.potenciaEnMb = pot;
    }
    
    /**
     * Este m�todo obtiene el tama�o del buffer del nodo.
     * @return Tama�o del buffer del nodo en MB.
     * @since 1.0
     */
    public int obtenerTamanioBuffer() {
        return this.obtenerPuertos().getBufferSizeInMB();
    }
    
    /**
     * Este m�todo permite establecer el tama�o del buffer del nodo.
     * @param tb Tama�o el buffer deseado para el nodo, en MB.
     * @since 1.0
     */
    public void ponerTamanioBuffer(int tb) {
        this.obtenerPuertos().setBufferSizeInMB(tb);
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 1.0
     */
    public void reset() {
        this.puertos.reset();
        matrizConmutacion.reset();
        gIdent.reset();
        gIdentLDP.reset();
        estadisticas.reset();
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
        this.restaurarPasosSinEmitir();
    }
    
    /**
     * Este m�todo indica el tipo de nodo de que se trata la instancia actual.
     * @return LER. Indica que el nodo es de este tipo.
     * @since 1.0
     */
    public int getNodeType() {
        return super.LER;
    }
    
    /**
     * Este m�todo inicia el hilo de ejecuci�n del LER, para que entre en
     * funcionamiento. Adem�s controla el tiempo de que dispone el LER para conmutar
     * paquetes.
     * @param evt Evento de reloj que sincroniza la ejecuci�n de los elementos de la topologia.
     * @since 1.0
     */
    public void receiveTimerEvent(TTimerEvent evt) {
        this.ponerDuracionTic(evt.getStepDuration());
        this.ponerInstanteDeTiempo(evt.getUpperLimit());
        if (this.obtenerPuertos().isAnyPacketToRoute()) {
            this.nsDisponibles += evt.getStepDuration();
        } else {
            this.restaurarPasosSinEmitir();
            this.nsDisponibles = evt.getStepDuration();
        }
        this.iniciar();
    }
    
    /**
     * Llama a las acciones que se tienen que ejecutar en el transcurso del tic de
     * reloj que el LER estar� en funcionamiento.
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
        encaminarPaquetes();
        estadisticas.asentarDatos(this.getAvailableTime());
        // Acciones a llevar a cabo durante el tic.
    }
    
    /**
     * Este m�todo comprueba que haya conectividad con sus nodos adyacentes, es decir,
     * que no haya caido ning�n enlace. Si ha caido alg�n enlace, entonces genera la
     * correspondiente se�alizaci�n para notificar este hecho.
     * @since 1.0
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
                if ((idPuerto >= 0) && (idPuerto < this.puertos.getNumberOfPorts())) {
                    puertoSalida = this.puertos.getPort(idPuerto);
                    if (puertoSalida != null) {
                        et = puertoSalida.getLink();
                        if (et != null) {
                            if ((et.linkIsBroken()) && (emc.getOutgoingLabel() != TSwitchingMatrixEntry.REMOVING_LABEL)) {
                                puertoEntrada = this.puertos.getPort(emc.getIncomingPortID());
                                et = puertoEntrada.getLink();
                                if (et.getLinkType() == TLink.INTERNAL) {
                                    eliminarTLDP(emc, emc.getIncomingPortID());
                                } else {
                                    eliminar = true;
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
                                puertoSalida = this.puertos.getPort(emc.getOutgoingPortID());
                                et = puertoSalida.getLink();
                                if (et.getLinkType() == TLink.INTERNAL) {
                                    eliminarTLDP(emc, emc.getOutgoingPortID());
                                } else {
                                    eliminar = false;
                                }
                            }
                        }
                    }
                }
                if ((emc.getIncomingPortID() >= 0) && ((emc.getOutgoingPortID() >= 0))) {
                    et = puertos.getPort(emc.getIncomingPortID()).getLink();
                    et2 = puertos.getPort(emc.getOutgoingPortID()).getLink();
                    if (et.linkIsBroken() && et2.linkIsBroken()) {
                        eliminar = true;
                    }
                    if (et.linkIsBroken() && (et2.getLinkType() == TLink.EXTERNAL)) {
                        eliminar = true;
                    }
                    if ((et.getLinkType() == TLink.EXTERNAL) && et2.linkIsBroken()) {
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
     * @since 1.0
     */
    public void encaminarPaquetes() {
        boolean conmute = false;
        int puertoLeido = 0;
        TAbstractPDU paquete = null;
        int octetosQuePuedoMandar = this.obtenerOctetosTransmitibles();
        while (this.obtenerPuertos().canSwitchPacket(octetosQuePuedoMandar)) {
            conmute = true;
            paquete = this.puertos.getNextPacket();
            puertoLeido = puertos.getReadPort();
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
     * @param pEntrada Puerto por el que ha entrado el paquete.
     * @since 1.0
     */
    public void conmutarGPSRP(TGPSRPPDU paquete, int pEntrada) {
        if (paquete != null) {
            int mensaje = paquete.obtenerDatosGPSRP().obtenerMensaje();
            int flujo = paquete.obtenerDatosGPSRP().obtenerFlujo();
            int idPaquete = paquete.obtenerDatosGPSRP().obtenerIdPaquete();
            String IPDestinoFinal = paquete.getHeader().obtenerIPDestino();
            TFIFOPort pSalida = null;
            if (IPDestinoFinal.equals(this.getIPAddress())) {
                // Un LER no entiende peticiones GPSRP, por tanto no pueder
                // haber mensajes GPSRP dirigidos a �l.
                this.discardPacket(paquete);
            } else {
                String IPSalida = this.topologia.obtenerIPSalto(this.getIPAddress(), IPDestinoFinal);
                pSalida = (TFIFOPort) this.puertos.getPortWhereIsConectedANodeHavingIP(IPSalida);
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
     * Este m�todo comprueba si existe una entrada en la tabla de encaminamiento para
     * el paquete entrante. Si no es as�, clasifica el paquete y, si es necesario,
     * reencola el paquete y solicita una etiqueta para poder enviarlo. Una vez que
     * tiene entrada en la tabla de encaminamiento, reenv�a el paquete hacia el
     * interior del dominio MPLS o hacia el exterior, segun corresponda.
     * @param paquete Paquete IPv4 de entrada.
     * @param pEntrada Puerto por el que ha accedido al nodo el paquete.
     * @since 1.0
     */
    public void conmutarIPv4(TIPv4PDU paquete, int pEntrada) {
        int valorFEC = clasificarPaquete(paquete);
        String IPDestinoFinal = paquete.getHeader().obtenerIPDestino();
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(pEntrada, valorFEC, TSwitchingMatrixEntry.FEC_ENTRY);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizFEC(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    solicitarTLDP(emc);
                }
                this.puertos.getPort(pEntrada).reEnqueuePacket(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                solicitarTLDP(emc);
                this.puertos.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                this.puertos.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
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
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        TMPLSPDU paqueteMPLS = this.crearPaqueteMPLS(paquete, emc);
                        pSalida.putPacketOnLink(paqueteMPLS, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.POP_LABEL) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.SWAP_LABEL) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
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
     * etiquetas a usar. El m�todo realiza sobre las matriz de encaminamiento la
     * operaci�n que sea necesario y propaga el cambio al nodo adyacente que
     * corresponda.
     * @param paquete Paquete TLDP recibido.
     * @param pEntrada Puerto por el que se ha recibido el paquete TLDP.
     * @since 1.0
     */
    public void conmutarTLDP(TTLDPPDU paquete, int pEntrada) {
        if (paquete.obtenerDatosTLDP().obtenerMensaje() == TTLDPPayload.SOLICITUD_ETIQUETA) {
            this.tratarSolicitudTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TTLDPPayload.SOLICITUD_OK) {
            this.tratarSolicitudOkTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TTLDPPayload.SOLICITUD_NO) {
            this.tratarSolicitudNoTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TTLDPPayload.ELIMINACION_ETIQUETA) {
            this.tratarEliminacionTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TTLDPPayload.ELIMINACION_OK) {
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
     * @since 1.0
     */
    public void conmutarMPLS(TMPLSPDU paquete, int pEntrada) {
        TMPLSLabel eMPLS = null;
        TSwitchingMatrixEntry emc = null;
        boolean conEtiqueta1 = false;
        if (paquete.getLabelStack().getTop().getLabelField() == 1) {
            eMPLS = paquete.getLabelStack().getTop();
            paquete.getLabelStack().borrarEtiqueta();
            conEtiqueta1 = true;
        }
        int valorLABEL = paquete.getLabelStack().getTop().getLabelField();
        String IPDestinoFinal = paquete.getHeader().obtenerIPDestino();
        emc = matrizConmutacion.getEntry(pEntrada, valorLABEL, TSwitchingMatrixEntry.LABEL_ENTRY);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizLABEL(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                    solicitarTLDP(emc);
                }
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                this.puertos.getPort(pEntrada).reEnqueuePacket(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                emc.setOutgoingLabel(TSwitchingMatrixEntry.LABEL_REQUESTED);
                solicitarTLDP(emc);
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                this.puertos.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                this.puertos.getPort(emc.getIncomingPortID()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED)) {
                int operacion = emc.getLabelStackOperation();
                if (operacion == TSwitchingMatrixEntry.UNDEFINED) {
                    if (conEtiqueta1) {
                        paquete.getLabelStack().ponerEtiqueta(eMPLS);
                    }
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PUSH_LABEL) {
                        TMPLSLabel empls = new TMPLSLabel();
                        empls.ponerBoS(false);
                        empls.ponerEXP(0);
                        empls.setLabelField(emc.getOutgoingLabel());
                        empls.ponerTTL(paquete.getLabelStack().getTop().obtenerTTL()-1);
                        paquete.getLabelStack().ponerEtiqueta(empls);
                        if (conEtiqueta1) {
                            paquete.getLabelStack().ponerEtiqueta(eMPLS);
                            paquete.setSubtype(TAbstractPDU.MPLS_GOS);
                        } else {
                            paquete.setSubtype(TAbstractPDU.MPLS);
                        }
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.POP_LABEL) {
                        if (paquete.getLabelStack().getTop().obtenerBoS()) {
                            TIPv4PDU paqueteIPv4 = this.crearPaqueteIPv4(paquete, emc);
                            if (conEtiqueta1) {
                                paqueteIPv4.setSubtype(TAbstractPDU.IPV4_GOS);
                            }
                            TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                            pSalida.putPacketOnLink(paqueteIPv4, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        } else {
                            paquete.getLabelStack().borrarEtiqueta();
                            if (conEtiqueta1) {
                                paquete.getLabelStack().ponerEtiqueta(eMPLS);
                            }
                            TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                            pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        }
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.SWAP_LABEL) {
                        paquete.getLabelStack().getTop().setLabelField(emc.getOutgoingLabel());
                        if (conEtiqueta1) {
                            paquete.getLabelStack().ponerEtiqueta(eMPLS);
                        }
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = puertos.getPort(emc.getOutgoingPortID());
                        pSalida.putPacketOnLink(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                discardPacket(paquete);
            }
        } else {
            if (conEtiqueta1) {
                paquete.getLabelStack().ponerEtiqueta(eMPLS);
            }
            discardPacket(paquete);
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
        emc = matrizConmutacion.getEntry(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
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
        if (paquete.obtenerEntradaPaquete() == TTLDPPDU.ENTRADA) {
            emc = matrizConmutacion.getEntry(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        } else {
            emc = matrizConmutacion.getEntry(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.getUpstreamTLDPSessionID() != TSwitchingMatrixEntry.UNDEFINED) {
                int etiquetaActual = emc.getOutgoingLabel();
                if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOppositePortID(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOppositePortID(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_UNAVAILABLE) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOppositePortID(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_ASSIGNED) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    matrizConmutacion.removeEntry(emc.getIncomingPortID(), emc.getLabelOrFEC(), emc.getEntryType());
                } else if (etiquetaActual == TSwitchingMatrixEntry.REMOVING_LABEL) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                } else if (etiquetaActual > 15) {
                    emc.setOutgoingLabel(TSwitchingMatrixEntry.REMOVING_LABEL);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.getOppositePortID(pEntrada));
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
     * @since 1.0
     */
    public void tratarSolicitudOkTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.getEntry(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.getOutgoingLabel();
            if (etiquetaActual == TSwitchingMatrixEntry.UNDEFINED) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                emc.setOutgoingLabel(paquete.obtenerDatosTLDP().obtenerEtiqueta());
                if (emc.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
                    emc.setLabelOrFEC(matrizConmutacion.getNewLabel());
                }
                TInternalLink et = (TInternalLink) puertos.getPort(emc.getOutgoingPortID()).getLink();
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
        emc = matrizConmutacion.getEntry(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
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
     * @since 1.0
     */
    public void tratarEliminacionOkTLDP(TTLDPPDU paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.obtenerEntradaPaquete() == TTLDPPDU.ENTRADA) {
            emc = matrizConmutacion.getEntry(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        } else {
            emc = matrizConmutacion.getEntry(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
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
                    TPort pSalida = puertos.getPort(pEntrada);
                    TLink et = pSalida.getLink();
                    if (et.getLinkType() == TLink.INTERNAL) {
                        TInternalLink ei = (TInternalLink) et;
                        if (emc.aBackupLSPHasBeenRequested()) {
                            ei.quitarLSPDeBackup();
                        } else {
                            ei.quitarLSP();
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
                        nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TTLDPPayload.SOLICITUD_OK);
                        nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.getTailEndIPAddress());
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(emc.getLabelOrFEC());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS_BACKUP);
                        } else {
                            nuevoTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS);
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
                        nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TTLDPPayload.SOLICITUD_NO);
                        nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.getTailEndIPAddress());
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getUpstreamTLDPSessionID());
                        nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(TSwitchingMatrixEntry.UNDEFINED);
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS_BACKUP);
                        } else {
                            nuevoTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS);
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
     * @param puerto Puierto por el que se debe enviar la confirmaci�n de eliminaci�n.
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
                    nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TTLDPPayload.ELIMINACION_OK);
                    nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.getTailEndIPAddress());
                    nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(TSwitchingMatrixEntry.UNDEFINED);
                    if (emc.getOutgoingPortID() == puerto) {
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getLocalTLDPSessionID());
                        nuevoTLDP.ponerSalidaPaquete(TTLDPPDU.ADELANTE);
                    } else if (emc.getIncomingPortID() == puerto) {
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getUpstreamTLDPSessionID());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            nuevoTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS_BACKUP);
                        } else {
                            nuevoTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS);
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
     * Este m�todo solicita una etiqueta al nodo que se especifica en la entrada de la
     * matriz de conmutaci�n correspondiente.
     * @param emc Entrada en la matriz de conmutaci�n especificada.
     * @since 1.0
     */
    public void solicitarTLDP(TSwitchingMatrixEntry emc) {
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = emc.getTailEndIPAddress();
        if (emc.getOutgoingLabel() != TSwitchingMatrixEntry.LABEL_ASSIGNED) {
            String IPSalto = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
            if (IPSalto != null) {
                TTLDPPDU paqueteTLDP = null;
                try {
                    paqueteTLDP = new TTLDPPDU(gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(IPDestinoFinal);
                    paqueteTLDP.obtenerDatosTLDP().ponerMensaje(TTLDPPayload.SOLICITUD_ETIQUETA);
                    paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getLocalTLDPSessionID());
                    if (emc.aBackupLSPHasBeenRequested()) {
                        paqueteTLDP.ponerEsParaBackup(true);
                    } else {
                        paqueteTLDP.ponerEsParaBackup(false);
                    }
                    paqueteTLDP.ponerSalidaPaquete(TTLDPPDU.ADELANTE);
                    TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalto);
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
     * Este m�todo env�a una eliminaci�n de etiqueta al nodo especificado por le
     * entrada de la matriz de conmutaci�n correspondiente.
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
                    paqueteTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(IPDestinoFinal);
                    paqueteTLDP.obtenerDatosTLDP().ponerMensaje(TTLDPPayload.ELIMINACION_ETIQUETA);
                    if (emc.getOutgoingPortID() == puerto) {
                        paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getLocalTLDPSessionID());
                        paqueteTLDP.ponerSalidaPaquete(TTLDPPDU.ADELANTE);
                    } else if (emc.getIncomingPortID() == puerto) {
                        paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getUpstreamTLDPSessionID());
                        if (emc.aBackupLSPHasBeenRequested()) {
                            paqueteTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS_BACKUP);
                        } else {
                            paqueteTLDP.ponerSalidaPaquete(TTLDPPDU.ATRAS);
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
     * Este m�todo reenv�a todas las peticiones pendientes de contestaci�n de una
     * entrada de la matriz de conmutaci�n.
     * @param emc Entrada de la matriz de conmutaci�n especificada.
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
                    paqueteTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(IPDestinoFinal);
                    paqueteTLDP.obtenerDatosTLDP().ponerMensaje(TTLDPPayload.SOLICITUD_ETIQUETA);
                    paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.getLocalTLDPSessionID());
                    if (emc.aBackupLSPHasBeenRequested()) {
                        paqueteTLDP.ponerEsParaBackup(true);
                    } else {
                        paqueteTLDP.ponerEsParaBackup(false);
                    }
                    paqueteTLDP.ponerSalidaPaquete(TTLDPPDU.ADELANTE);
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
    }
    
    /**
     * Este m�todo decrementa los contadores de retransmisi�n existentes para este nodo.
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
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n con los datos de
     * un paquete TLDP entrante.
     * @param paqueteSolicitud Paquete TLDP entrante, de solicitud de etiqueta.
     * @param pEntrada Puerto de entrada del paquete TLDP.
     * @return La entrada de la matriz de conmutaci�n, ya creada, insertada e inicializada.
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaAPartirDeTLDP(TTLDPPDU paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.obtenerDatosTLDP().obtenerIdentificadorLDP();
        TPort puertoEntrada = puertos.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.obtenerDatosTLDP().obtenerIPDestinoFinal();
        String IPSalto = topologia.obtenerIPSalto(this.getIPAddress(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalto);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(IdTLDPAntecesor);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setLabelOrFEC(TSwitchingMatrixEntry.UNDEFINED);
            emc.setEntryIsForBackupLSP(paqueteSolicitud.obtenerEsParaBackup());
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
                emc.setLocalTLDPSessionID(gIdentLDP.obtenerNuevo());
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
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizFEC(TIPv4PDU paqueteIPv4, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteIPv4.getHeader().obtenerIPDestino();
        String IPSalida = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = puertos.getPort(pEntrada);
            TPort puertoSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalida);
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
                emc.setLocalTLDPSessionID(gIdentLDP.obtenerNuevo());
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
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizLABEL(TMPLSPDU paqueteMPLS, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteMPLS.getHeader().obtenerIPDestino();
        String IPSalida = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = puertos.getPort(pEntrada);
            TPort puertoSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalida);
            int enlaceOrigen = TLink.EXTERNAL;
            int enlaceDestino = TLink.INTERNAL;
            emc = new TSwitchingMatrixEntry();
            emc.setUpstreamTLDPSessionID(TSwitchingMatrixEntry.UNDEFINED);
            emc.setTailEndIPAddress(IPDestinoFinal);
            emc.setIncomingPortID(pEntrada);
            emc.setOutgoingLabel(TSwitchingMatrixEntry.UNDEFINED);
            emc.setEntryIsForBackupLSP(false);
            emc.setLabelOrFEC(paqueteMPLS.getLabelStack().getTop().getLabelField());
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
                emc.setLocalTLDPSessionID(gIdentLDP.obtenerNuevo());
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
     * @since 1.0
     */
    public TMPLSPDU crearPaqueteMPLS(TIPv4PDU paqueteIPv4, TSwitchingMatrixEntry emc) {
        TMPLSPDU paqueteMPLS = null;
        try {
            paqueteMPLS = new TMPLSPDU(gIdent.getNextID(), paqueteIPv4.getHeader().getOriginIP(), paqueteIPv4.getHeader().obtenerIPDestino(), paqueteIPv4.getSize());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        paqueteMPLS.setHeader(paqueteIPv4.getHeader());
        paqueteMPLS.ponerDatosTCP(paqueteIPv4.obtenerDatos());
        paqueteMPLS.setSubtype(TAbstractPDU.MPLS);
        TMPLSLabel empls = new TMPLSLabel();
        empls.ponerBoS(true);
        empls.ponerEXP(0);
        empls.setLabelField(emc.getOutgoingLabel());
        empls.ponerTTL(paqueteIPv4.getHeader().obtenerTTL()-1);
        paqueteMPLS.getLabelStack().ponerEtiqueta(empls);
        paqueteIPv4 = null;
        try {
            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteMPLS.getSubtype(), paqueteMPLS.getSize()));
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
     * @since 1.0
     */
    public TIPv4PDU crearPaqueteIPv4(TMPLSPDU paqueteMPLS, TSwitchingMatrixEntry emc) {
        TIPv4PDU paqueteIPv4 = null;
        try {
            paqueteIPv4 = new TIPv4PDU(gIdent.getNextID(), paqueteMPLS.getHeader().getOriginIP(), paqueteMPLS.getHeader().obtenerIPDestino(), paqueteMPLS.obtenerDatosTCP().obtenerTamanio());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        paqueteIPv4.setHeader(paqueteMPLS.getHeader());
        paqueteIPv4.ponerDatos(paqueteMPLS.obtenerDatosTCP());
        paqueteIPv4.getHeader().ponerTTL(paqueteMPLS.getLabelStack().getTop().obtenerTTL());
        if (paqueteIPv4.getHeader().getOptionsField().isUsed()) {
            paqueteIPv4.setSubtype(TAbstractPDU.IPV4_GOS);
        } else {
            paqueteIPv4.setSubtype(TAbstractPDU.IPV4);
        }
        try {
            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paqueteIPv4.getSubtype(), paqueteIPv4.getSize()));
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
     * @since 1.0
     */
    public boolean esUnPaqueteExterno(TAbstractPDU paquete, int pEntrada) {
        if (paquete.getType() == TAbstractPDU.IPV4)
            return true;
        TPort pe = puertos.getPort(pEntrada);
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
     * @since 1.0
     */
    public void contabilizarPaquete(TAbstractPDU paquete, boolean deEntrada) {
    }
    
    /**
     * Este m�todo descarta un paquete en el nodo y refleja dicho descarte en las
     * estad�sticas del nodo.
     * @param paquete Paquete que se quiere descartar.
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
     * Este m�todo toma como parametro un paquete, supuestamente sin etiquetar, y lo
     * clasifica. Esto significa que determina el FEC_ENTRY al que pertenece el paquete.
 Este valor se calcula como el c�digo HASH practicado a la concatenaci�n de la IP
 de origen y la IP de destino. En la pr�ctica esto significa que paquetes con el
 mismo origen y con el mismo destino pertenecer�n al mismo FEC_ENTRY.
     * @param paquete El paquete que se desea clasificar.
     * @return El FEC_ENTRY al que pertenece el paquete pasado por par�metros.
     * @since 1.0
     */
    public int clasificarPaquete(TAbstractPDU paquete) {
        String IPOrigen = paquete.getHeader().getOriginIP();
        String IPDestino = paquete.getHeader().obtenerIPDestino();
        String cadenaFEC = cadenaFEC = IPOrigen + IPDestino;
        return cadenaFEC.hashCode();
    }
    
    /**
     * Este m�todo permite el acceso al conjunto de puertos del nodo.
     * @return El conjunto de puertos del nodo.
     * @since 1.0
     */
    public TPortSet obtenerPuertos() {
        return this.puertos;
    }
    
    /**
     * Este m�todo calcula si el nodo tiene puertos libres o no.
     * @return true, si el nodo tiene puertos libres. false en caso contrario.
     * @since 1.0
     */
    public boolean tienePuertosLibres() {
        return this.puertos.isAnyPortAvailable();
    }
    
    /**
     * Este m�todo calcula el peso del nodo. Se utilizar� para calcular rutas con costo
     * menor. En el nodo LER el pero ser� siempre nulo (cero).
     * @return 0. El peso siempre ser� nulo en un LER.
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
     * Este m�todo comprueba si la isntancia actual es el LER de salida del dominio
     * MPLS para una IP dada.
     * @param ip IP de destino del tr�fico, para la cual queremos averiguar si el LER es nodo de
     * salida.
     * @return true, si el LER es de salida del dominio para tr�fico dirigido a esa IP. false
     * en caso contrario.
     * @since 1.0
     */
    public boolean soyLERDeSalida(String ip) {
        TPort p = puertos.getPortWhereIsConectedANodeHavingIP(ip);
        if (p != null)
            if (p.getLink().getLinkType() == TLink.EXTERNAL)
                return true;
        return false;
    }
    
    /**
     * Este m�todo permite el acceso a la matriz de conmutaci�n de LER.
     * @return La matriz de conmutaci�n del LER.
     * @since 1.0
     */
    public TSwitchingMatrix obtenerMatrizConmutacion() {
        return matrizConmutacion;
    }
    
    /**
     * Este m�todo comprueba que la configuraci�n de LER sea la correcta.
     * @return true, si el LER est� bien configurado. false en caso contrario.
     * @since 1.0
     */
    public boolean estaBienConfigurado() {
        return this.bienConfigurado;
    }
    
    /**
     * Este m�todo comprueba que una cierta configuraci�n es v�lida.
     * @param t Topolog�a a la que pertenece el LER.
     * @param recfg true si se trata de una reconfiguraci�n. false en caso contrario.
     * @return CORRECTA, si la configuraci�n es correcta. Un c�digo de error en caso contrario.
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
     * Este m�todo toma un codigo de error y genera un mensaje textual del mismo.
     * @param e El c�digo de error para el cual queremos una explicaci�n textual.
     * @return Cadena de texto explicando el error.
     * @since 1.0
     */
    public String obtenerMensajeError(int e) {
        switch (e) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLER.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigLER.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoLER.NombreNoSoloEspacios"));
        }
        return ("");
    }
    
    /**
     * Este m�todo forma una cadena de texto que representa al LER y toda su
     * configuraci�n. Sirve para almacenar el LER en disco.
     * @return Una cadena de texto que representa un a este LER.
     * @since 1.0
     */
    public String marshall() {
        String cadena = "#LER#";
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
        return cadena;
    }
    
    /**
     * Este m�todo toma como par�metro una cadena de texto que debe pertencer a un LER
     * serializado y configura esta instancia con los valores de dicha caddena.
     * @param elemento LER serializado.
     * @return true, si no ha habido errores y la instancia actual est� bien configurada. false
     * en caso contrario.
     * @since 1.0
     */
    public boolean unmarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 12) {
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
        return true;
    }
    
    /**
     * Este m�todo permite acceder directamente a las estadisticas del nodo.
     * @return Las estad�sticas del nodo.
     * @since 1.0
     */
    public TStats getStats() {
        return estadisticas;
    }
    
    /**
     * Este m�todo permite establecer el n�mero de puertos que tendr� el nodo.
     * @param num N�mero de puertos deseado para el nodo. Como mucho, 8 puertos.
     * @since 1.0
     */
    public synchronized void ponerPuertos(int num) {
        puertos = new TFIFOPortSet(num, this);
    }
    
    /**
     * Este m�todo no hace nada en un LSR. En un nodo activoPermitir� solicitar
     * a un nodo activo la retransmisi�n de un paquete.
     * @param paquete Paquete cuya retransmisi�n se est� solicitando.
     * @param pSalida Puerto por el que se enviar� la solicitud.
     * @since 1.0
     */
    public void runGoSPDUStoreAndRetransmitProtocol(TMPLSPDU paquete, int pSalida) {
    }
    
    /**
     * Esta constante indica que la configuraci�n del nodo LER esta correcta, que no
     * contiene errores.
     * @since 1.0
     */
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que el nombre del nodo LER no est� definido.
     * @since 1.0
     */
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que el nombre especificado para el LER ya est� siendo
     * usado por otro nodo de la topologia.
     * @since 1.0
     */
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que el nombre que se ha definido para el LER contiene s�lo
     * constantes.
     * @since 1.0
     */
    public static final int SOLO_ESPACIOS = 3;
    
    private TSwitchingMatrix matrizConmutacion;
    private TLongIDGenerator gIdent;
    private TIDGenerator gIdentLDP;
    private int potenciaEnMb;
    private TLERStats estadisticas;
}
