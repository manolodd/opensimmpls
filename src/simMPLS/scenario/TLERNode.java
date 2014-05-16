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

import simMPLS.protocols.TPDUGPSRP;
import simMPLS.protocols.TPDUTLDP;
import simMPLS.protocols.TPDU;
import simMPLS.protocols.TEtiquetaMPLS;
import simMPLS.protocols.TPDUMPLS;
import simMPLS.protocols.TDatosTLDP;
import simMPLS.protocols.TPDUIPv4;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TFIFOPort;
import simMPLS.hardware.tldp.TSwitchingMatrix;
import simMPLS.hardware.tldp.TSwitchingMatrixEntry;
import simMPLS.hardware.ports.TFIFOPortSet;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TPortSet;
import simMPLS.utils.EDesbordeDelIdentificador;
import simMPLS.utils.TIdentificador;
import simMPLS.utils.TIdentificadorLargo;
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
     * identificador unico para cada evento que genere.
     * @param t Referencia a la topolog�a a la que pertenece el LER. Le permite hacer
     * comprobaciones, calcular rutas, etc�tera.
     * @since 1.0
     */
    public TLERNode(int identificador, String d, TIdentificadorLargo il, TTopology t) {
        super(identificador, d, il, t);
        this.ponerPuertos(super.NUM_PUERTOS_LER);
        matrizConmutacion = new TSwitchingMatrix();
        gIdent = new TIdentificadorLargo();
        gIdentLDP = new TIdentificador();
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
    public int obtenerTipo() {
        return super.LER;
    }
    
    /**
     * Este m�todo inicia el hilo de ejecuci�n del LER, para que entre en
     * funcionamiento. Adem�s controla el tiempo de que dispone el LER para conmutar
     * paquetes.
     * @param evt Evento de reloj que sincroniza la ejecuci�n de los elementos de la topologia.
     * @since 1.0
     */
    public void capturarEventoReloj(TTimerEvent evt) {
        this.ponerDuracionTic(evt.obtenerDuracionTic());
        this.ponerInstanteDeTiempo(evt.obtenerLimiteSuperior());
        if (this.obtenerPuertos().isAnyPacketToRoute()) {
            this.nsDisponibles += evt.obtenerDuracionTic();
        } else {
            this.restaurarPasosSinEmitir();
            this.nsDisponibles = evt.obtenerDuracionTic();
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
        matrizConmutacion.obtenerCerrojo().lock();
        Iterator it = matrizConmutacion.obtenerIteradorEntradas();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                idPuerto = emc.obtenerPuertoSalida();
                if ((idPuerto >= 0) && (idPuerto < this.puertos.getNumberOfPorts())) {
                    puertoSalida = this.puertos.getPort(idPuerto);
                    if (puertoSalida != null) {
                        et = puertoSalida.getLink();
                        if (et != null) {
                            if ((et.obtenerEnlaceCaido()) && (emc.obtenerEtiqueta() != TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA)) {
                                puertoEntrada = this.puertos.getPort(emc.obtenerPuertoEntrada());
                                et = puertoEntrada.getLink();
                                if (et.obtenerTipo() == TLink.INTERNO) {
                                    eliminarTLDP(emc, emc.obtenerPuertoEntrada());
                                } else {
                                    eliminar = true;
                                }
                            }
                        }
                    }
                }
                idPuerto = emc.obtenerPuertoEntrada();
                if ((idPuerto >= 0) && (idPuerto < this.puertos.getNumberOfPorts())) {
                    puertoEntrada = this.puertos.getPort(idPuerto);
                    if (puertoEntrada != null) {
                        et = puertoEntrada.getLink();
                        if (et != null) {
                            if ((et.obtenerEnlaceCaido()) && (emc.obtenerEtiqueta() != TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA)) {
                                puertoSalida = this.puertos.getPort(emc.obtenerPuertoSalida());
                                et = puertoSalida.getLink();
                                if (et.obtenerTipo() == TLink.INTERNO) {
                                    eliminarTLDP(emc, emc.obtenerPuertoSalida());
                                } else {
                                    eliminar = false;
                                }
                            }
                        }
                    }
                }
                if ((emc.obtenerPuertoEntrada() >= 0) && ((emc.obtenerPuertoSalida() >= 0))) {
                    et = puertos.getPort(emc.obtenerPuertoEntrada()).getLink();
                    et2 = puertos.getPort(emc.obtenerPuertoSalida()).getLink();
                    if (et.obtenerEnlaceCaido() && et2.obtenerEnlaceCaido()) {
                        eliminar = true;
                    }
                    if (et.obtenerEnlaceCaido() && (et2.obtenerTipo() == TLink.EXTERNO)) {
                        eliminar = true;
                    }
                    if ((et.obtenerTipo() == TLink.EXTERNO) && et2.obtenerEnlaceCaido()) {
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
        matrizConmutacion.obtenerCerrojo().unLock();
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
        TPDU paquete = null;
        int octetosQuePuedoMandar = this.obtenerOctetosTransmitibles();
        while (this.obtenerPuertos().canSwitchPacket(octetosQuePuedoMandar)) {
            conmute = true;
            paquete = this.puertos.getNextPacket();
            puertoLeido = puertos.getReadPort();
            if (paquete != null) {
                if (paquete.getType() == TPDU.IPV4) {
                    conmutarIPv4((TPDUIPv4) paquete, puertoLeido);
                } else if (paquete.getType() == TPDU.TLDP) {
                    conmutarTLDP((TPDUTLDP) paquete, puertoLeido);
                } else if (paquete.getType() == TPDU.MPLS) {
                    conmutarMPLS((TPDUMPLS) paquete, puertoLeido);
                } else if (paquete.getType() == TPDU.GPSRP) {
                    conmutarGPSRP((TPDUGPSRP) paquete, puertoLeido);
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
    public void conmutarGPSRP(TPDUGPSRP paquete, int pEntrada) {
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
                    pSalida.ponerPaqueteEnEnlace(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.GPSRP));
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
    public void conmutarIPv4(TPDUIPv4 paquete, int pEntrada) {
        int valorFEC = clasificarPaquete(paquete);
        String IPDestinoFinal = paquete.getHeader().obtenerIPDestino();
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.obtenerEntrada(pEntrada, valorFEC, TSwitchingMatrixEntry.FEC);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizFEC(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.ponerEtiqueta(TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA);
                    solicitarTLDP(emc);
                }
                this.puertos.getPort(pEntrada).reEnqueuePacket(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TSwitchingMatrixEntry.SIN_DEFINIR) {
                emc.ponerEtiqueta(TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA);
                solicitarTLDP(emc);
                this.puertos.getPort(emc.obtenerPuertoEntrada()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                this.puertos.getPort(emc.obtenerPuertoEntrada()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_DENEGADA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA)) {
                int operacion = emc.obtenerOperacion();
                if (operacion == TSwitchingMatrixEntry.SIN_DEFINIR) {
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PONER_ETIQUETA) {
                        TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                        TPDUMPLS paqueteMPLS = this.crearPaqueteMPLS(paquete, emc);
                        pSalida.ponerPaqueteEnEnlace(paqueteMPLS, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.QUITAR_ETIQUETA) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.CAMBIAR_ETIQUETA) {
                        discardPacket(paquete);
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
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
    public void conmutarTLDP(TPDUTLDP paquete, int pEntrada) {
        if (paquete.obtenerDatosTLDP().obtenerMensaje() == TDatosTLDP.SOLICITUD_ETIQUETA) {
            this.tratarSolicitudTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TDatosTLDP.SOLICITUD_OK) {
            this.tratarSolicitudOkTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TDatosTLDP.SOLICITUD_NO) {
            this.tratarSolicitudNoTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TDatosTLDP.ELIMINACION_ETIQUETA) {
            this.tratarEliminacionTLDP(paquete, pEntrada);
        } else if (paquete.obtenerDatosTLDP().obtenerMensaje() == TDatosTLDP.ELIMINACION_OK) {
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
    public void conmutarMPLS(TPDUMPLS paquete, int pEntrada) {
        TEtiquetaMPLS eMPLS = null;
        TSwitchingMatrixEntry emc = null;
        boolean conEtiqueta1 = false;
        if (paquete.getLabelStack().getTop().getLabelField() == 1) {
            eMPLS = paquete.getLabelStack().getTop();
            paquete.getLabelStack().borrarEtiqueta();
            conEtiqueta1 = true;
        }
        int valorLABEL = paquete.getLabelStack().getTop().getLabelField();
        String IPDestinoFinal = paquete.getHeader().obtenerIPDestino();
        emc = matrizConmutacion.obtenerEntrada(pEntrada, valorLABEL, TSwitchingMatrixEntry.LABEL);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizLABEL(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.ponerEtiqueta(TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA);
                    solicitarTLDP(emc);
                }
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                this.puertos.getPort(pEntrada).reEnqueuePacket(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TSwitchingMatrixEntry.SIN_DEFINIR) {
                emc.ponerEtiqueta(TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA);
                solicitarTLDP(emc);
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                this.puertos.getPort(emc.obtenerPuertoEntrada()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                this.puertos.getPort(emc.obtenerPuertoEntrada()).reEnqueuePacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_DENEGADA) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
                if (conEtiqueta1) {
                    paquete.getLabelStack().ponerEtiqueta(eMPLS);
                }
                discardPacket(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA)) {
                int operacion = emc.obtenerOperacion();
                if (operacion == TSwitchingMatrixEntry.SIN_DEFINIR) {
                    if (conEtiqueta1) {
                        paquete.getLabelStack().ponerEtiqueta(eMPLS);
                    }
                    discardPacket(paquete);
                } else {
                    if (operacion == TSwitchingMatrixEntry.PONER_ETIQUETA) {
                        TEtiquetaMPLS empls = new TEtiquetaMPLS();
                        empls.ponerBoS(false);
                        empls.ponerEXP(0);
                        empls.setLabelField(emc.obtenerEtiqueta());
                        empls.ponerTTL(paquete.getLabelStack().getTop().obtenerTTL()-1);
                        paquete.getLabelStack().ponerEtiqueta(empls);
                        if (conEtiqueta1) {
                            paquete.getLabelStack().ponerEtiqueta(eMPLS);
                            paquete.ponerSubtipo(TPDU.MPLS_GOS);
                        } else {
                            paquete.ponerSubtipo(TPDU.MPLS);
                        }
                        TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.QUITAR_ETIQUETA) {
                        if (paquete.getLabelStack().getTop().obtenerBoS()) {
                            TPDUIPv4 paqueteIPv4 = this.crearPaqueteIPv4(paquete, emc);
                            if (conEtiqueta1) {
                                paqueteIPv4.ponerSubtipo(TPDU.IPV4_GOS);
                            }
                            TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                            pSalida.ponerPaqueteEnEnlace(paqueteIPv4, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        } else {
                            paquete.getLabelStack().borrarEtiqueta();
                            if (conEtiqueta1) {
                                paquete.getLabelStack().ponerEtiqueta(eMPLS);
                            }
                            TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                            pSalida.ponerPaqueteEnEnlace(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        }
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.CAMBIAR_ETIQUETA) {
                        paquete.getLabelStack().getTop().setLabelField(emc.obtenerEtiqueta());
                        if (conEtiqueta1) {
                            paquete.getLabelStack().ponerEtiqueta(eMPLS);
                        }
                        TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketRouted(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TSwitchingMatrixEntry.NOOP) {
                        TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
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
    public void tratarSolicitudTLDP(TPDUTLDP paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.obtenerEntradaIDAntecesor(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        if (emc == null) {
            emc = crearEntradaAPartirDeTLDP(paquete, pEntrada);
        }
        if (emc != null) {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TSwitchingMatrixEntry.SIN_DEFINIR) {
                emc.ponerEtiqueta(TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA);
                this.solicitarTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                // no hago nada. Se est� esperando una etiqueta.);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_DENEGADA) {
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA) {
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
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
    public void tratarEliminacionTLDP(TPDUTLDP paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.obtenerEntradaPaquete() == TPDUTLDP.ENTRADA) {
            emc = matrizConmutacion.obtenerEntradaIDAntecesor(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        } else {
            emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            if (emc.obtenerIDLDPAntecesor() != TSwitchingMatrixEntry.SIN_DEFINIR) {
                int etiquetaActual = emc.obtenerEtiqueta();
                if (etiquetaActual == TSwitchingMatrixEntry.SIN_DEFINIR) {
                    emc.ponerEtiqueta(TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                    emc.ponerEtiqueta(TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_DENEGADA) {
                    emc.ponerEtiqueta(TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    matrizConmutacion.borrarEntrada(emc.obtenerPuertoEntrada(), emc.obtenerLabelFEC(), emc.obtenerTipo());
                } else if (etiquetaActual == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                } else if (etiquetaActual > 15) {
                    emc.ponerEtiqueta(TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else {
                    discardPacket(paquete);
                }
            } else {
                enviarEliminacionOkTLDP(emc, pEntrada);
                matrizConmutacion.borrarEntrada(emc.obtenerPuertoEntrada(), emc.obtenerLabelFEC(), emc.obtenerTipo());
            }
        }
    }
    
    /**
     * Este m�todo trata un paquete TLDP de confirmaci�n de etiqueta.
     * @param paquete Confirmaci�n de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmaci�n de etiquetas.
     * @since 1.0
     */
    public void tratarSolicitudOkTLDP(TPDUTLDP paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TSwitchingMatrixEntry.SIN_DEFINIR) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                emc.ponerEtiqueta(paquete.obtenerDatosTLDP().obtenerEtiqueta());
                if (emc.obtenerLabelFEC() == TSwitchingMatrixEntry.SIN_DEFINIR) {
                    emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                }
                TInternalLink et = (TInternalLink) puertos.getPort(emc.obtenerPuertoSalida()).getLink();
                if (et != null) {
                    if (emc.obtenerEntranteEsLSPDEBackup()) {
                        et.ponerLSPDeBackup();
                    } else {
                        et.ponerLSP();
                    }
                }
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_DENEGADA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
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
    public void tratarSolicitudNoTLDP(TPDUTLDP paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TSwitchingMatrixEntry.SIN_DEFINIR) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                emc.ponerEtiqueta(TSwitchingMatrixEntry.ETIQUETA_DENEGADA);
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_DENEGADA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
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
    public void tratarEliminacionOkTLDP(TPDUTLDP paquete, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        if (paquete.obtenerEntradaPaquete() == TPDUTLDP.ENTRADA) {
            emc = matrizConmutacion.obtenerEntradaIDAntecesor(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        } else {
            emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        }
        if (emc == null) {
            discardPacket(paquete);
        } else {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TSwitchingMatrixEntry.SIN_DEFINIR) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_DENEGADA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA) {
                discardPacket(paquete);
            } else if (etiquetaActual == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
                if (emc.obtenerEtiqueta() != TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA) {
                    TPort pSalida = puertos.getPort(pEntrada);
                    TLink et = pSalida.getLink();
                    if (et.obtenerTipo() == TLink.INTERNO) {
                        TInternalLink ei = (TInternalLink) et;
                        if (emc.obtenerEntranteEsLSPDEBackup()) {
                            ei.quitarLSPDeBackup();
                        } else {
                            ei.quitarLSP();
                        }
                    }
                }
                matrizConmutacion.borrarEntrada(emc.obtenerPuertoEntrada(), emc.obtenerLabelFEC(), emc.obtenerTipo());
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
            if (emc.obtenerIDLDPAntecesor() != TSwitchingMatrixEntry.SIN_DEFINIR) {
                String IPLocal = this.getIPAddress();
                String IPDestino = puertos.getIPOfNodeLinkedTo(emc.obtenerPuertoEntrada());
                if (IPDestino != null) {
                    TPDUTLDP nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TPDUTLDP(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.SOLICITUD_OK);
                        nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.obtenerDestinoFinal());
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPAntecesor());
                        nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(emc.obtenerLabelFEC());
                        if (emc.obtenerEntranteEsLSPDEBackup()) {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS_BACKUP);
                        } else {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS);
                        }
                        TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPDestino);
                        pSalida.ponerPaqueteEnEnlace(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP, nuevoTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP));
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
            if (emc.obtenerIDLDPAntecesor() != TSwitchingMatrixEntry.SIN_DEFINIR) {
                String IPLocal = this.getIPAddress();
                String IPDestino = puertos.getIPOfNodeLinkedTo(emc.obtenerPuertoEntrada());
                if (IPDestino != null) {
                    TPDUTLDP nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TPDUTLDP(gIdent.getNextID(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.SOLICITUD_NO);
                        nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.obtenerDestinoFinal());
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPAntecesor());
                        nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(TSwitchingMatrixEntry.SIN_DEFINIR);
                        if (emc.obtenerEntranteEsLSPDEBackup()) {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS_BACKUP);
                        } else {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS);
                        }
                        TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPDestino);
                        pSalida.ponerPaqueteEnEnlace(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP, nuevoTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP));
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
                TPDUTLDP nuevoTLDP = null;
                try {
                    nuevoTLDP = new TPDUTLDP(gIdent.getNextID(), IPLocal, IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nuevoTLDP != null) {
                    nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.ELIMINACION_OK);
                    nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.obtenerDestinoFinal());
                    nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(TSwitchingMatrixEntry.SIN_DEFINIR);
                    if (emc.obtenerPuertoSalida() == puerto) {
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPPropio());
                        nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ADELANTE);
                    } else if (emc.obtenerPuertoEntrada() == puerto) {
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPAntecesor());
                        if (emc.obtenerEntranteEsLSPDEBackup()) {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS_BACKUP);
                        } else {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS);
                        }
                    }
                    TPort pSalida = puertos.getPort(puerto);
                    pSalida.ponerPaqueteEnEnlace(nuevoTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    try {
                        this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP, nuevoTLDP.getSize()));
                        this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP));
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
        String IPDestinoFinal = emc.obtenerDestinoFinal();
        if (emc.obtenerEtiqueta() != TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA) {
            String IPSalto = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
            if (IPSalto != null) {
                TPDUTLDP paqueteTLDP = null;
                try {
                    paqueteTLDP = new TPDUTLDP(gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(IPDestinoFinal);
                    paqueteTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.SOLICITUD_ETIQUETA);
                    paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPPropio());
                    if (emc.obtenerEntranteEsLSPDEBackup()) {
                        paqueteTLDP.ponerEsParaBackup(true);
                    } else {
                        paqueteTLDP.ponerEsParaBackup(false);
                    }
                    paqueteTLDP.ponerSalidaPaquete(TPDUTLDP.ADELANTE);
                    TPort pSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalto);
                    if (pSalida != null) {
                        pSalida.ponerPaqueteEnEnlace(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP, paqueteTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP));
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
            emc.ponerEtiqueta(TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA);
            String IPLocal = this.getIPAddress();
            String IPDestinoFinal = emc.obtenerDestinoFinal();
            String IPSalto = puertos.getIPOfNodeLinkedTo(puerto);
            if (IPSalto != null) {
                TPDUTLDP paqueteTLDP = null;
                try {
                    paqueteTLDP = new TPDUTLDP(gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(IPDestinoFinal);
                    paqueteTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.ELIMINACION_ETIQUETA);
                    if (emc.obtenerPuertoSalida() == puerto) {
                        paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPPropio());
                        paqueteTLDP.ponerSalidaPaquete(TPDUTLDP.ADELANTE);
                    } else if (emc.obtenerPuertoEntrada() == puerto) {
                        paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPAntecesor());
                        if (emc.obtenerEntranteEsLSPDEBackup()) {
                            paqueteTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS_BACKUP);
                        } else {
                            paqueteTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS);
                        }
                    }
                    TPort pSalida = puertos.getPort(puerto);
                    if (pSalida != null) {
                        pSalida.ponerPaqueteEnEnlace(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP, paqueteTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP));
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
            String IPDestinoFinal = emc.obtenerDestinoFinal();
            String IPSalto = puertos.getIPOfNodeLinkedTo(emc.obtenerPuertoSalida());
            if (IPSalto != null) {
                TPDUTLDP paqueteTLDP = null;
                try {
                    paqueteTLDP = new TPDUTLDP(gIdent.getNextID(), IPLocal, IPSalto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (paqueteTLDP != null) {
                    paqueteTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(IPDestinoFinal);
                    paqueteTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.SOLICITUD_ETIQUETA);
                    paqueteTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPPropio());
                    if (emc.obtenerEntranteEsLSPDEBackup()) {
                        paqueteTLDP.ponerEsParaBackup(true);
                    } else {
                        paqueteTLDP.ponerEsParaBackup(false);
                    }
                    paqueteTLDP.ponerSalidaPaquete(TPDUTLDP.ADELANTE);
                    TPort pSalida = puertos.getPort(emc.obtenerPuertoSalida());
                    if (pSalida != null) {
                        pSalida.ponerPaqueteEnEnlace(paqueteTLDP, pSalida.getLink().getTargetNodeIDOfTrafficSentBy(this));
                        try {
                            this.generarEventoSimulacion(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP, paqueteTLDP.getSize()));
                            this.generarEventoSimulacion(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), TPDU.TLDP));
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
        eliminarTLDP(emc, emc.obtenerPuertoEntrada());
        eliminarTLDP(emc, emc.obtenerPuertoSalida());
    }
    
    /**
     * Este m�todo decrementa los contadores de retransmisi�n existentes para este nodo.
     * @since 1.0
     */
    public void decrementarContadores() {
        TSwitchingMatrixEntry emc = null;
        this.matrizConmutacion.obtenerCerrojo().lock();
        Iterator it = this.matrizConmutacion.obtenerIteradorEntradas();
        while (it.hasNext()) {
            emc = (TSwitchingMatrixEntry) it.next();
            if (emc != null) {
                emc.decrementarTimeOut(this.obtenerDuracionTic());
                if (emc.obtenerEtiqueta() == TSwitchingMatrixEntry.SOLICITANDO_ETIQUETA) {
                    if (emc.hacerPeticionDeNuevo()) {
                        emc.reestablecerTimeOut();
                        emc.decrementarIntentos();
                        solicitarTLDPTrasTimeout(emc);
                    }
                } else if (emc.obtenerEtiqueta() == TSwitchingMatrixEntry.ELIMINANDO_ETIQUETA) {
                    if (emc.hacerPeticionDeNuevo()) {
                        emc.reestablecerTimeOut();
                        emc.decrementarIntentos();
                        eliminarTLDPTrasTimeout(emc);
                    } else {
                        if (!emc.quedanIntentos()) {
                            it.remove();
                        }
                    }
                } else {
                    emc.reestablecerTimeOut();
                    emc.reestablecerIntentos();
                }
            }
        }
        this.matrizConmutacion.obtenerCerrojo().unLock();
    }
    
    /**
     * Este m�todo crea una nueva entrada en la matriz de conmutaci�n con los datos de
     * un paquete TLDP entrante.
     * @param paqueteSolicitud Paquete TLDP entrante, de solicitud de etiqueta.
     * @param pEntrada Puerto de entrada del paquete TLDP.
     * @return La entrada de la matriz de conmutaci�n, ya creada, insertada e inicializada.
     * @since 1.0
     */
    public TSwitchingMatrixEntry crearEntradaAPartirDeTLDP(TPDUTLDP paqueteSolicitud, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.obtenerDatosTLDP().obtenerIdentificadorLDP();
        TPort puertoEntrada = puertos.getPort(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.obtenerDatosTLDP().obtenerIPDestinoFinal();
        String IPSalto = topologia.obtenerIPSalto(this.getIPAddress(), IPDestinoFinal);
        if (IPSalto != null) {
            TPort puertoSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalto);
            int enlaceOrigen = TLink.EXTERNO;
            int enlaceDestino = TLink.INTERNO;
            emc = new TSwitchingMatrixEntry();
            emc.ponerIDLDPAntecesor(IdTLDPAntecesor);
            emc.ponerDestinoFinal(IPDestinoFinal);
            emc.ponerPuertoEntrada(pEntrada);
            emc.ponerEtiqueta(TSwitchingMatrixEntry.SIN_DEFINIR);
            emc.ponerLabelFEC(TSwitchingMatrixEntry.SIN_DEFINIR);
            emc.ponerEntranteEsLSPDEBackup(paqueteSolicitud.obtenerEsParaBackup());
            if (puertoSalida != null) {
                emc.ponerPuertoSalida(puertoSalida.obtenerIdentificador());
            } else {
                emc.ponerPuertoSalida(TSwitchingMatrixEntry.SIN_DEFINIR);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().obtenerTipo();
            }
            if (puertoSalida != null) {
                enlaceDestino = puertoSalida.getLink().obtenerTipo();
            }
            if ((enlaceOrigen == TLink.EXTERNO) && (enlaceDestino == TLink.EXTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.FEC);
                emc.ponerOperacion(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNO) && (enlaceDestino == TLink.INTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.FEC);
                emc.ponerOperacion(TSwitchingMatrixEntry.PONER_ETIQUETA);
            } else if ((enlaceOrigen == TLink.INTERNO) && (enlaceDestino == TLink.EXTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.LABEL);
                emc.ponerOperacion(TSwitchingMatrixEntry.QUITAR_ETIQUETA);
            } else if ((enlaceOrigen == TLink.INTERNO) && (enlaceDestino == TLink.INTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.LABEL);
                emc.ponerOperacion(TSwitchingMatrixEntry.CAMBIAR_ETIQUETA);
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                emc.ponerEtiqueta(TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA);
            }
            try {
                emc.ponerIDLDPPropio(gIdentLDP.obtenerNuevo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            matrizConmutacion.insertar(emc);
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
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizFEC(TPDUIPv4 paqueteIPv4, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteIPv4.getHeader().obtenerIPDestino();
        String IPSalida = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = puertos.getPort(pEntrada);
            TPort puertoSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalida);
            int enlaceOrigen = TLink.EXTERNO;
            int enlaceDestino = TLink.INTERNO;
            emc = new TSwitchingMatrixEntry();
            emc.ponerIDLDPAntecesor(TSwitchingMatrixEntry.SIN_DEFINIR);
            emc.ponerDestinoFinal(IPDestinoFinal);
            emc.ponerPuertoEntrada(pEntrada);
            emc.ponerEtiqueta(TSwitchingMatrixEntry.SIN_DEFINIR);
            emc.ponerLabelFEC(clasificarPaquete(paqueteIPv4));
            emc.ponerEntranteEsLSPDEBackup(false);
            if (puertoSalida != null) {
                emc.ponerPuertoSalida(puertoSalida.obtenerIdentificador());
                enlaceDestino = puertoSalida.getLink().obtenerTipo();
            } else {
                emc.ponerPuertoSalida(TSwitchingMatrixEntry.SIN_DEFINIR);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().obtenerTipo();
            }
            if ((enlaceOrigen == TLink.EXTERNO) && (enlaceDestino == TLink.EXTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.FEC);
                emc.ponerOperacion(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNO) && (enlaceDestino == TLink.INTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.FEC);
                emc.ponerOperacion(TSwitchingMatrixEntry.PONER_ETIQUETA);
            } else if ((enlaceOrigen == TLink.INTERNO) && (enlaceDestino == TLink.EXTERNO)) {
                // No es posible
            } else if ((enlaceOrigen == TLink.INTERNO) && (enlaceDestino == TLink.INTERNO)) {
                // No es posible
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                emc.ponerEtiqueta(TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA);
            }
            try {
                emc.ponerIDLDPPropio(gIdentLDP.obtenerNuevo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            matrizConmutacion.insertar(emc);
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
    public TSwitchingMatrixEntry crearEntradaInicialEnMatrizLABEL(TPDUMPLS paqueteMPLS, int pEntrada) {
        TSwitchingMatrixEntry emc = null;
        String IPLocal = this.getIPAddress();
        String IPDestinoFinal = paqueteMPLS.getHeader().obtenerIPDestino();
        String IPSalida = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPort puertoEntrada = puertos.getPort(pEntrada);
            TPort puertoSalida = puertos.getPortWhereIsConectedANodeHavingIP(IPSalida);
            int enlaceOrigen = TLink.EXTERNO;
            int enlaceDestino = TLink.INTERNO;
            emc = new TSwitchingMatrixEntry();
            emc.ponerIDLDPAntecesor(TSwitchingMatrixEntry.SIN_DEFINIR);
            emc.ponerDestinoFinal(IPDestinoFinal);
            emc.ponerPuertoEntrada(pEntrada);
            emc.ponerEtiqueta(TSwitchingMatrixEntry.SIN_DEFINIR);
            emc.ponerEntranteEsLSPDEBackup(false);
            emc.ponerLabelFEC(paqueteMPLS.getLabelStack().getTop().getLabelField());
            if (puertoSalida != null) {
                emc.ponerPuertoSalida(puertoSalida.obtenerIdentificador());
                enlaceDestino = puertoSalida.getLink().obtenerTipo();
            } else {
                emc.ponerPuertoSalida(TSwitchingMatrixEntry.SIN_DEFINIR);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.getLink().obtenerTipo();
            }
            if ((enlaceOrigen == TLink.EXTERNO) && (enlaceDestino == TLink.EXTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.LABEL);
                emc.ponerOperacion(TSwitchingMatrixEntry.NOOP);
            } else if ((enlaceOrigen == TLink.EXTERNO) && (enlaceDestino == TLink.INTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.LABEL);
                emc.ponerOperacion(TSwitchingMatrixEntry.PONER_ETIQUETA);
            } else if ((enlaceOrigen == TLink.INTERNO) && (enlaceDestino == TLink.EXTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.LABEL);
                emc.ponerOperacion(TSwitchingMatrixEntry.QUITAR_ETIQUETA);
            } else if ((enlaceOrigen == TLink.INTERNO) && (enlaceDestino == TLink.INTERNO)) {
                emc.ponerTipo(TSwitchingMatrixEntry.LABEL);
                emc.ponerOperacion(TSwitchingMatrixEntry.CAMBIAR_ETIQUETA);
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                emc.ponerEtiqueta(TSwitchingMatrixEntry.ETIQUETA_CONCEDIDA);
            }
            try {
                emc.ponerIDLDPPropio(gIdentLDP.obtenerNuevo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            matrizConmutacion.insertar(emc);
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
    public TPDUMPLS crearPaqueteMPLS(TPDUIPv4 paqueteIPv4, TSwitchingMatrixEntry emc) {
        TPDUMPLS paqueteMPLS = null;
        try {
            paqueteMPLS = new TPDUMPLS(gIdent.getNextID(), paqueteIPv4.getHeader().obtenerIPOrigen(), paqueteIPv4.getHeader().obtenerIPDestino(), paqueteIPv4.getSize());
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace();
        }
        paqueteMPLS.ponerCabecera(paqueteIPv4.getHeader());
        paqueteMPLS.ponerDatosTCP(paqueteIPv4.obtenerDatos());
        paqueteMPLS.ponerSubtipo(TPDU.MPLS);
        TEtiquetaMPLS empls = new TEtiquetaMPLS();
        empls.ponerBoS(true);
        empls.ponerEXP(0);
        empls.setLabelField(emc.obtenerEtiqueta());
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
    public TPDUIPv4 crearPaqueteIPv4(TPDUMPLS paqueteMPLS, TSwitchingMatrixEntry emc) {
        TPDUIPv4 paqueteIPv4 = null;
        try {
            paqueteIPv4 = new TPDUIPv4(gIdent.getNextID(), paqueteMPLS.getHeader().obtenerIPOrigen(), paqueteMPLS.getHeader().obtenerIPDestino(), paqueteMPLS.obtenerDatosTCP().obtenerTamanio());
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace();
        }
        paqueteIPv4.ponerCabecera(paqueteMPLS.getHeader());
        paqueteIPv4.ponerDatos(paqueteMPLS.obtenerDatosTCP());
        paqueteIPv4.getHeader().ponerTTL(paqueteMPLS.getLabelStack().getTop().obtenerTTL());
        if (paqueteIPv4.getHeader().getOptionsField().isUsed()) {
            paqueteIPv4.ponerSubtipo(TPDU.IPV4_GOS);
        } else {
            paqueteIPv4.ponerSubtipo(TPDU.IPV4);
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
    public boolean esUnPaqueteExterno(TPDU paquete, int pEntrada) {
        if (paquete.getType() == TPDU.IPV4)
            return true;
        TPort pe = puertos.getPort(pEntrada);
        if (pe.getLink().obtenerTipo() == TLink.EXTERNO)
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
    public void contabilizarPaquete(TPDU paquete, boolean deEntrada) {
    }
    
    /**
     * Este m�todo descarta un paquete en el nodo y refleja dicho descarte en las
     * estad�sticas del nodo.
     * @param paquete Paquete que se quiere descartar.
     * @since 1.0
     */
    public void discardPacket(TPDU paquete) {
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
     * clasifica. Esto significa que determina el FEC al que pertenece el paquete.
     * Este valor se calcula como el c�digo HASH practicado a la concatenaci�n de la IP
     * de origen y la IP de destino. En la pr�ctica esto significa que paquetes con el
     * mismo origen y con el mismo destino pertenecer�n al mismo FEC.
     * @param paquete El paquete que se desea clasificar.
     * @return El FEC al que pertenece el paquete pasado por par�metros.
     * @since 1.0
     */
    public int clasificarPaquete(TPDU paquete) {
        String IPOrigen = paquete.getHeader().obtenerIPOrigen();
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
        long pesoMC = (long) ((10*this.matrizConmutacion.obtenerNumeroEntradas())* (0.3));
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
            if (p.getLink().obtenerTipo() == TLink.EXTERNO)
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
    public String serializar() {
        String cadena = "#LER#";
        cadena += this.obtenerIdentificador();
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
    public boolean desSerializar(String elemento) {
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
    public void runGoSPDUStoreAndRetransmitProtocol(TPDUMPLS paquete, int pSalida) {
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
    private TIdentificadorLargo gIdent;
    private TIdentificador gIdentLDP;
    private int potenciaEnMb;
    private TLERStats estadisticas;
}
