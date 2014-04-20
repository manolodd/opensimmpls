//**************************************************************************
// Nombre......: TNodoLER.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un nodo LER de la topología.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import java.awt.*;
import java.util.*;
import simMPLS.escenario.*;
import simMPLS.electronica.reloj.*;
import simMPLS.electronica.puertos.*;
import simMPLS.utiles.*;
import simMPLS.electronica.tldp.*;
import simMPLS.protocolo.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase implementa un Label Edge Router (LER) de entrada/salida del dominio
 * MPLS.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TNodoLER extends TNodoTopologia implements IEventoRelojListener, Runnable {
    
    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de TNodoLER
     * y otorga unos valores iniciales a los atributos.
     * @param identificador Clabve primaria que permite buscar, encontrar y ordenadr dentro de la topología
     * a esta instancia del LER. Identifica el nodo como unico.
     * @param d Dirección IP única que tendrá el nodo.
     * @param il generador de identificadores largos. Se usa para que el LER pueda obtener un
     * identificador unico para cada evento que genere.
     * @param t Referencia a la topología a la que pertenece el LER. Le permite hacer
     * comprobaciones, calcular rutas, etcétera.
     * @since 1.0
     */
    public TNodoLER(int identificador, String d, TIdentificadorLargo il, TTopologia t) {
        super(identificador, d, il, t);
        this.ponerPuertos(super.NUM_PUERTOS_LER);
        matrizConmutacion = new TMatrizConmutacion();
        gIdent = new TIdentificadorLargo();
        gIdentLDP = new TIdentificador();
        potenciaEnMb = 512;
        estadisticas = new TEstadisticasLER();
    }
    
    /**
     * Este método calcula el número de nanosegundos que se necesitan para conmutar un
     * bit. Se basa en la potencia de conmutación configurada para el LER.
     * @return El número de nanosegundos necesarios para conmutar un bit.
     * @since 1.0
     */
    public double obtenerNsPorBit() {
        long tasaEnBitsPorSegundo = (long) (this.potenciaEnMb*1048576L);
        double nsPorCadaBit = (double) ((double)1000000000.0/(long)tasaEnBitsPorSegundo);
        return nsPorCadaBit;
    }
    
    /**
     * Este método calcula el numero de nanosegundos que son necesarios para conmutar
     * un determinado número de octetos.
     * @param octetos El número de octetos que queremos conmutar.
     * @return El número de nanosegundos necesarios para conmutar el número de octetos
     * especificados.
     * @since 1.0
     */
    public double obtenerNsUsadosTotalOctetos(int octetos) {
        double nsPorCadaBit = obtenerNsPorBit();
        long bitsOctetos = (long) ((long)octetos*(long)8);
        return (double)((double)nsPorCadaBit*(long)bitsOctetos);
    }
    
    /**
     * Este método calcula el número de bits que puede conmutar el nodo con el número
     * de nanosegundos de que dispone actualmente.
     * @return El número de bits máximo que puede conmutar el nodo con los nanosegundos de que
     * dispone actualmente.
     * @since 1.0
     */
    public int obtenerLimiteBitsTransmitibles() {
        double nsPorCadaBit = obtenerNsPorBit();
        double maximoBits = (double) ((double)nsDisponibles/(double)nsPorCadaBit);
        return (int) maximoBits;
    }
    
    /**
     * Este método calcula el número de octetos completos que puede transmitir el nodo
     * con el número de nanosegundos de que dispone.
     * @return El número de octetos completos que puede transmitir el nodo en un momento dado.
     * @since 1.0
     */
    public int obtenerOctetosTransmitibles() {
        double maximoBytes = ((double)obtenerLimiteBitsTransmitibles()/(double)8.0);
        return (int) maximoBytes;
    }
    
    /**
     * Este método obtiene la potencia em Mbps con que está configurado el nodo.
     * @return La potencia de conmutación del nodo en Mbps.
     * @since 1.0
     */
    public int obtenerPotenciaEnMb() {
        return this.potenciaEnMb;
    }
    
    /**
     * Este método permite establecer la potencia de conmutación del nodo en Mbps.
     * @param pot Potencia deseada para el nodo en Mbps.
     * @since 1.0
     */
    public void ponerPotenciaEnMb(int pot) {
        this.potenciaEnMb = pot;
    }
    
    /**
     * Este método obtiene el tamaño del buffer del nodo.
     * @return Tamaño del buffer del nodo en MB.
     * @since 1.0
     */
    public int obtenerTamanioBuffer() {
        return this.obtenerPuertos().obtenerTamanioBuffer();
    }
    
    /**
     * Este método permite establecer el tamaño del buffer del nodo.
     * @param tb Tamaño el buffer deseado para el nodo, en MB.
     * @since 1.0
     */
    public void ponerTamanioBuffer(int tb) {
        this.obtenerPuertos().ponerTamanioBuffer(tb);
    }
    
    /**
     * Este método reinicia los atributos de la clase como si acabasen de ser creados
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
     * Este método indica el tipo de nodo de que se trata la instancia actual.
     * @return LER. Indica que el nodo es de este tipo.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.LER;
    }
    
    /**
     * Este método inicia el hilo de ejecución del LER, para que entre en
     * funcionamiento. Además controla el tiempo de que dispone el LER para conmutar
     * paquetes.
     * @param evt Evento de reloj que sincroniza la ejecución de los elementos de la topologia.
     * @since 1.0
     */
    public void capturarEventoReloj(TEventoReloj evt) {
        this.ponerDuracionTic(evt.obtenerDuracionTic());
        this.ponerInstanteDeTiempo(evt.obtenerLimiteSuperior());
        if (this.obtenerPuertos().hayPaquetesQueEncaminar()) {
            this.nsDisponibles += evt.obtenerDuracionTic();
        } else {
            this.restaurarPasosSinEmitir();
            this.nsDisponibles = evt.obtenerDuracionTic();
        }
        this.iniciar();
    }
    
    /**
     * Llama a las acciones que se tienen que ejecutar en el transcurso del tic de
     * reloj que el LER estará en funcionamiento.
     * @since 1.0
     */
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        try {
            this.generarEventoSimulacion(new TESNodoCongestionado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), this.obtenerPuertos().obtenerCongestion()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        comprobarElEstadoDeLasComunicaciones();
        decrementarContadores();
        encaminarPaquetes();
        estadisticas.asentarDatos(this.obtenerInstanteDeTiempo());
        // Acciones a llevar a cabo durante el tic.
    }
    
    /**
     * Este método comprueba que haya conectividad con sus nodos adyacentes, es decir,
     * que no haya caido ningún enlace. Si ha caido algún enlace, entonces genera la
     * correspondiente señalización para notificar este hecho.
     * @since 1.0
     */
    public void comprobarElEstadoDeLasComunicaciones() {
        boolean eliminar = false;
        TEntradaMatrizConmutacion emc = null;
        int idPuerto = 0;
        TPuerto puertoSalida = null;
        TPuerto puertoEntrada = null;
        TEnlaceTopologia et = null;
        TEnlaceTopologia et2 = null;
        matrizConmutacion.obtenerCerrojo().bloquear();
        Iterator it = matrizConmutacion.obtenerIteradorEntradas();
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc != null) {
                idPuerto = emc.obtenerPuertoSalida();
                if ((idPuerto >= 0) && (idPuerto < this.puertos.obtenerNumeroDePuertos())) {
                    puertoSalida = this.puertos.obtenerPuerto(idPuerto);
                    if (puertoSalida != null) {
                        et = puertoSalida.obtenerEnlace();
                        if (et != null) {
                            if ((et.obtenerEnlaceCaido()) && (emc.obtenerEtiqueta() != TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA)) {
                                puertoEntrada = this.puertos.obtenerPuerto(emc.obtenerPuertoEntrada());
                                et = puertoEntrada.obtenerEnlace();
                                if (et.obtenerTipo() == TEnlaceTopologia.INTERNO) {
                                    eliminarTLDP(emc, emc.obtenerPuertoEntrada());
                                } else {
                                    eliminar = true;
                                }
                            }
                        }
                    }
                }
                idPuerto = emc.obtenerPuertoEntrada();
                if ((idPuerto >= 0) && (idPuerto < this.puertos.obtenerNumeroDePuertos())) {
                    puertoEntrada = this.puertos.obtenerPuerto(idPuerto);
                    if (puertoEntrada != null) {
                        et = puertoEntrada.obtenerEnlace();
                        if (et != null) {
                            if ((et.obtenerEnlaceCaido()) && (emc.obtenerEtiqueta() != TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA)) {
                                puertoSalida = this.puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                                et = puertoSalida.obtenerEnlace();
                                if (et.obtenerTipo() == TEnlaceTopologia.INTERNO) {
                                    eliminarTLDP(emc, emc.obtenerPuertoSalida());
                                } else {
                                    eliminar = false;
                                }
                            }
                        }
                    }
                }
                if ((emc.obtenerPuertoEntrada() >= 0) && ((emc.obtenerPuertoSalida() >= 0))) {
                    et = puertos.obtenerPuerto(emc.obtenerPuertoEntrada()).obtenerEnlace();
                    et2 = puertos.obtenerPuerto(emc.obtenerPuertoSalida()).obtenerEnlace();
                    if (et.obtenerEnlaceCaido() && et2.obtenerEnlaceCaido()) {
                        eliminar = true;
                    }
                    if (et.obtenerEnlaceCaido() && (et2.obtenerTipo() == TEnlaceTopologia.EXTERNO)) {
                        eliminar = true;
                    }
                    if ((et.obtenerTipo() == TEnlaceTopologia.EXTERNO) && et2.obtenerEnlaceCaido()) {
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
        matrizConmutacion.obtenerCerrojo().liberar();
    }
    
    /**
     * Este método lee del puerto que corresponda según el turno Round Robin
     * consecutivamente hasta que se termina el crédito. Si tiene posibilidad de
     * conmutar y/o encaminar un paquete, lo hace, llamando para ello a los métodos
     * correspondiente segun el paquete. Si el paquete está mal formado o es
     * desconocido, lo descarta.
     * @since 1.0
     */
    public void encaminarPaquetes() {
        boolean conmute = false;
        int puertoLeido = 0;
        TPDU paquete = null;
        int octetosQuePuedoMandar = this.obtenerOctetosTransmitibles();
        while (this.obtenerPuertos().puedoConmutarPaquete(octetosQuePuedoMandar)) {
            conmute = true;
            paquete = this.puertos.leerPaquete();
            puertoLeido = puertos.obtenerPuertoLeido();
            if (paquete != null) {
                if (paquete.obtenerTipo() == TPDU.IPV4) {
                    conmutarIPv4((TPDUIPv4) paquete, puertoLeido);
                } else if (paquete.obtenerTipo() == TPDU.TLDP) {
                    conmutarTLDP((TPDUTLDP) paquete, puertoLeido);
                } else if (paquete.obtenerTipo() == TPDU.MPLS) {
                    conmutarMPLS((TPDUMPLS) paquete, puertoLeido);
                } else if (paquete.obtenerTipo() == TPDU.GPSRP) {
                    conmutarGPSRP((TPDUGPSRP) paquete, puertoLeido);
                } else {
                    this.nsDisponibles += obtenerNsUsadosTotalOctetos(paquete.obtenerTamanio());
                    descartarPaquete(paquete);
                }
                this.nsDisponibles -= obtenerNsUsadosTotalOctetos(paquete.obtenerTamanio());
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
     * Este método conmuta un paquete GPSRP.
     * @param paquete Paquete GPSRP a conmutar.
     * @param pEntrada Puerto por el que ha entrado el paquete.
     * @since 1.0
     */
    public void conmutarGPSRP(TPDUGPSRP paquete, int pEntrada) {
        if (paquete != null) {
            int mensaje = paquete.obtenerDatosGPSRP().obtenerMensaje();
            int flujo = paquete.obtenerDatosGPSRP().obtenerFlujo();
            int idPaquete = paquete.obtenerDatosGPSRP().obtenerIdPaquete();
            String IPDestinoFinal = paquete.obtenerCabecera().obtenerIPDestino();
            TPuertoNormal pSalida = null;
            if (IPDestinoFinal.equals(this.obtenerIP())) {
                // Un LER no entiende peticiones GPSRP, por tanto no pueder
                // haber mensajes GPSRP dirigidos a él.
                this.descartarPaquete(paquete);
            } else {
                String IPSalida = this.topologia.obtenerIPSalto(this.obtenerIP(), IPDestinoFinal);
                pSalida = (TPuertoNormal) this.puertos.obtenerPuertoDestino(IPSalida);
                if (pSalida != null) {
                    pSalida.ponerPaqueteEnEnlace(paquete, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                    try {
                        this.generarEventoSimulacion(new TESPaqueteEncaminado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.GPSRP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    this.descartarPaquete(paquete);
                }
            }
        }
    }
    
    /**
     * Este método comprueba si existe una entrada en la tabla de encaminamiento para
     * el paquete entrante. Si no es así, clasifica el paquete y, si es necesario,
     * reencola el paquete y solicita una etiqueta para poder enviarlo. Una vez que
     * tiene entrada en la tabla de encaminamiento, reenvía el paquete hacia el
     * interior del dominio MPLS o hacia el exterior, segun corresponda.
     * @param paquete Paquete IPv4 de entrada.
     * @param pEntrada Puerto por el que ha accedido al nodo el paquete.
     * @since 1.0
     */
    public void conmutarIPv4(TPDUIPv4 paquete, int pEntrada) {
        int valorFEC = clasificarPaquete(paquete);
        String IPDestinoFinal = paquete.obtenerCabecera().obtenerIPDestino();
        TEntradaMatrizConmutacion emc = null;
        emc = matrizConmutacion.obtenerEntrada(pEntrada, valorFEC, TEntradaMatrizConmutacion.FEC);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizFEC(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.ponerEtiqueta(TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA);
                    solicitarTLDP(emc);
                }
                this.puertos.obtenerPuerto(pEntrada).reencolar(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                emc.ponerEtiqueta(TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA);
                solicitarTLDP(emc);
                this.puertos.obtenerPuerto(emc.obtenerPuertoEntrada()).reencolar(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                this.puertos.obtenerPuerto(emc.obtenerPuertoEntrada()).reencolar(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_DENEGADA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
                descartarPaquete(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA)) {
                int operacion = emc.obtenerOperacion();
                if (operacion == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                    descartarPaquete(paquete);
                } else {
                    if (operacion == TEntradaMatrizConmutacion.PONER_ETIQUETA) {
                        TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                        TPDUMPLS paqueteMPLS = this.crearPaqueteMPLS(paquete, emc);
                        pSalida.ponerPaqueteEnEnlace(paqueteMPLS, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteEncaminado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TEntradaMatrizConmutacion.QUITAR_ETIQUETA) {
                        descartarPaquete(paquete);
                    } else if (operacion == TEntradaMatrizConmutacion.CAMBIAR_ETIQUETA) {
                        descartarPaquete(paquete);
                    } else if (operacion == TEntradaMatrizConmutacion.NOOP) {
                        TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteEncaminado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                descartarPaquete(paquete);
            }
        } else {
            descartarPaquete(paquete);
        }
    }
    
    /**
     * Este método se llama cuando se recibe un paquete TLDP con información sobre las
     * etiquetas a usar. El método realiza sobre las matriz de encaminamiento la
     * operación que sea necesario y propaga el cambio al nodo adyacente que
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
     * Este método comprueba si existe una entrada en la tabla de encaminamiento para
     * el paquete entrante. Si no es así, clasifica el paquete y, si es necesario,
     * reencola el paquete y solicita una etiqueta para poder enviarlo. Una vez que
     * tiene entrada en la tabla de encaminamiento, reenvía el paquete hacia el
     * siguiente nodo del dominio MPLS o hacia el exterior, segun corresponda.
     * @param paquete Paquete MPLS recibido.
     * @param pEntrada Puerto por el que ha llegado el paquete MPLS recibido.
     * @since 1.0
     */
    public void conmutarMPLS(TPDUMPLS paquete, int pEntrada) {
        TEtiquetaMPLS eMPLS = null;
        TEntradaMatrizConmutacion emc = null;
        boolean conEtiqueta1 = false;
        if (paquete.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerLabel() == 1) {
            eMPLS = paquete.obtenerPilaEtiquetas().obtenerEtiqueta();
            paquete.obtenerPilaEtiquetas().borrarEtiqueta();
            conEtiqueta1 = true;
        }
        int valorLABEL = paquete.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerLabel();
        String IPDestinoFinal = paquete.obtenerCabecera().obtenerIPDestino();
        emc = matrizConmutacion.obtenerEntrada(pEntrada, valorLABEL, TEntradaMatrizConmutacion.LABEL);
        if (emc == null) {
            emc = crearEntradaInicialEnMatrizLABEL(paquete, pEntrada);
            if (emc != null) {
                if (!soyLERDeSalida(IPDestinoFinal)) {
                    emc.ponerEtiqueta(TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA);
                    solicitarTLDP(emc);
                }
                if (conEtiqueta1) {
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                }
                this.puertos.obtenerPuerto(pEntrada).reencolar(paquete);
            }
        }
        if (emc != null) {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                emc.ponerEtiqueta(TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA);
                solicitarTLDP(emc);
                if (conEtiqueta1) {
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                }
                this.puertos.obtenerPuerto(emc.obtenerPuertoEntrada()).reencolar(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                if (conEtiqueta1) {
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                }
                this.puertos.obtenerPuerto(emc.obtenerPuertoEntrada()).reencolar(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_DENEGADA) {
                if (conEtiqueta1) {
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                }
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
                if (conEtiqueta1) {
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                }
                descartarPaquete(paquete);
            } else if ((etiquetaActual > 15) || (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA)) {
                int operacion = emc.obtenerOperacion();
                if (operacion == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                    if (conEtiqueta1) {
                        paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                    }
                    descartarPaquete(paquete);
                } else {
                    if (operacion == TEntradaMatrizConmutacion.PONER_ETIQUETA) {
                        TEtiquetaMPLS empls = new TEtiquetaMPLS();
                        empls.ponerBoS(false);
                        empls.ponerEXP(0);
                        empls.ponerLabel(emc.obtenerEtiqueta());
                        empls.ponerTTL(paquete.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerTTL()-1);
                        paquete.obtenerPilaEtiquetas().ponerEtiqueta(empls);
                        if (conEtiqueta1) {
                            paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                            paquete.ponerSubtipo(TPDU.MPLS_GOS);
                        } else {
                            paquete.ponerSubtipo(TPDU.MPLS);
                        }
                        TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteEncaminado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TEntradaMatrizConmutacion.QUITAR_ETIQUETA) {
                        if (paquete.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerBoS()) {
                            TPDUIPv4 paqueteIPv4 = this.crearPaqueteIPv4(paquete, emc);
                            if (conEtiqueta1) {
                                paqueteIPv4.ponerSubtipo(TPDU.IPV4_GOS);
                            }
                            TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                            pSalida.ponerPaqueteEnEnlace(paqueteIPv4, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        } else {
                            paquete.obtenerPilaEtiquetas().borrarEtiqueta();
                            if (conEtiqueta1) {
                                paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                            }
                            TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                            pSalida.ponerPaqueteEnEnlace(paquete, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        }
                        try {
                            this.generarEventoSimulacion(new TESPaqueteEncaminado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TEntradaMatrizConmutacion.CAMBIAR_ETIQUETA) {
                        paquete.obtenerPilaEtiquetas().obtenerEtiqueta().ponerLabel(emc.obtenerEtiqueta());
                        if (conEtiqueta1) {
                            paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                        }
                        TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteEncaminado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (operacion == TEntradaMatrizConmutacion.NOOP) {
                        TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                        pSalida.ponerPaqueteEnEnlace(paquete, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteEncaminado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (conEtiqueta1) {
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
                }
                descartarPaquete(paquete);
            }
        } else {
            if (conEtiqueta1) {
                paquete.obtenerPilaEtiquetas().ponerEtiqueta(eMPLS);
            }
            descartarPaquete(paquete);
        }
    }
    
    /**
     * Este método trata una petición de etiquetas.
     * @param paquete Petición de etiquetas recibida de otro nodo.
     * @param pEntrada Puerto de entrada de la petición de etiqueta.
     * @since 1.0
     */
    public void tratarSolicitudTLDP(TPDUTLDP paquete, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        emc = matrizConmutacion.obtenerEntradaIDAntecesor(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        if (emc == null) {
            emc = crearEntradaAPartirDeTLDP(paquete, pEntrada);
        }
        if (emc != null) {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                emc.ponerEtiqueta(TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA);
                this.solicitarTLDP(emc);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                // no hago nada. Se está esperando una etiqueta.);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_DENEGADA) {
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA) {
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
                eliminarTLDP(emc, pEntrada);
            } else if (etiquetaActual > 15) {
                enviarSolicitudOkTLDP(emc);
            } else {
                descartarPaquete(paquete);
            }
        } else {
            descartarPaquete(paquete);
        }
    }
    
    /**
     * Este método trata un paquete TLDP de eliminación de etiqueta.
     * @param paquete Eliminación de etiqueta recibida.
     * @param pEntrada Puerto por el que se recibión la eliminación de etiqueta.
     * @since 1.0
     */
    public void tratarEliminacionTLDP(TPDUTLDP paquete, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        if (paquete.obtenerEntradaPaquete() == TPDUTLDP.ENTRADA) {
            emc = matrizConmutacion.obtenerEntradaIDAntecesor(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        } else {
            emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        }
        if (emc == null) {
            descartarPaquete(paquete);
        } else {
            if (emc.obtenerIDLDPAntecesor() != TEntradaMatrizConmutacion.SIN_DEFINIR) {
                int etiquetaActual = emc.obtenerEtiqueta();
                if (etiquetaActual == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                    emc.ponerEtiqueta(TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else if (etiquetaActual == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                    emc.ponerEtiqueta(TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_DENEGADA) {
                    emc.ponerEtiqueta(TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    matrizConmutacion.borrarEntrada(emc.obtenerPuertoEntrada(), emc.obtenerLabelFEC(), emc.obtenerTipo());
                } else if (etiquetaActual == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
                    enviarEliminacionOkTLDP(emc, pEntrada);
                } else if (etiquetaActual > 15) {
                    emc.ponerEtiqueta(TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA);
                    enviarEliminacionOkTLDP(emc, pEntrada);
                    eliminarTLDP(emc, emc.obtenerPuertoOpuesto(pEntrada));
                } else {
                    descartarPaquete(paquete);
                }
            } else {
                enviarEliminacionOkTLDP(emc, pEntrada);
                matrizConmutacion.borrarEntrada(emc.obtenerPuertoEntrada(), emc.obtenerLabelFEC(), emc.obtenerTipo());
            }
        }
    }
    
    /**
     * Este método trata un paquete TLDP de confirmación de etiqueta.
     * @param paquete Confirmación de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmación de etiquetas.
     * @since 1.0
     */
    public void tratarSolicitudOkTLDP(TPDUTLDP paquete, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        if (emc == null) {
            descartarPaquete(paquete);
        } else {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                emc.ponerEtiqueta(paquete.obtenerDatosTLDP().obtenerEtiqueta());
                if (emc.obtenerLabelFEC() == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                    emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                }
                TEnlaceInterno et = (TEnlaceInterno) puertos.obtenerPuerto(emc.obtenerPuertoSalida()).obtenerEnlace();
                if (et != null) {
                    if (emc.obtenerEntranteEsLSPDEBackup()) {
                        et.ponerLSPDeBackup();
                    } else {
                        et.ponerLSP();
                    }
                }
                enviarSolicitudOkTLDP(emc);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_DENEGADA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual > 15) {
                descartarPaquete(paquete);
            } else {
                descartarPaquete(paquete);
            }
        }
    }
    
    /**
     * Este método trata un paquete TLDP de denegación de etiqueta.
     * @param paquete Paquete de denegación de etiquetas recibido.
     * @param pEntrada Puerto por el que se ha recibido la denegación de etiquetas.
     * @since 1.0
     */
    public void tratarSolicitudNoTLDP(TPDUTLDP paquete, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        if (emc == null) {
            descartarPaquete(paquete);
        } else {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                emc.ponerEtiqueta(TEntradaMatrizConmutacion.ETIQUETA_DENEGADA);
                enviarSolicitudNoTLDP(emc);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_DENEGADA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual > 15) {
                descartarPaquete(paquete);
            } else {
                descartarPaquete(paquete);
            }
        }
    }
    
    /**
     * Este método trata un paquete TLDP de confirmación de eliminación de etiqueta.
     * @param paquete Paquete de confirmación e eliminación de etiqueta.
     * @param pEntrada Puerto por el que se ha recibido la confirmación de eliminación de etiqueta.
     * @since 1.0
     */
    public void tratarEliminacionOkTLDP(TPDUTLDP paquete, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        if (paquete.obtenerEntradaPaquete() == TPDUTLDP.ENTRADA) {
            emc = matrizConmutacion.obtenerEntradaIDAntecesor(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP(), pEntrada);
        } else {
            emc = matrizConmutacion.obtenerEntradaIDPropio(paquete.obtenerDatosTLDP().obtenerIdentificadorLDP());
        }
        if (emc == null) {
            descartarPaquete(paquete);
        } else {
            int etiquetaActual = emc.obtenerEtiqueta();
            if (etiquetaActual == TEntradaMatrizConmutacion.SIN_DEFINIR) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_DENEGADA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA) {
                descartarPaquete(paquete);
            } else if (etiquetaActual == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
                if (emc.obtenerEtiqueta() != TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA) {
                    TPuerto pSalida = puertos.obtenerPuerto(pEntrada);
                    TEnlaceTopologia et = pSalida.obtenerEnlace();
                    if (et.obtenerTipo() == TEnlaceTopologia.INTERNO) {
                        TEnlaceInterno ei = (TEnlaceInterno) et;
                        if (emc.obtenerEntranteEsLSPDEBackup()) {
                            ei.quitarLSPDeBackup();
                        } else {
                            ei.quitarLSP();
                        }
                    }
                }
                matrizConmutacion.borrarEntrada(emc.obtenerPuertoEntrada(), emc.obtenerLabelFEC(), emc.obtenerTipo());
             } else if (etiquetaActual > 15) {
                 descartarPaquete(paquete);
             } else {
                 descartarPaquete(paquete);
             }
        }
    }
    
    /**
     * Este método envía una etiqueta al nodo que indique la entrada en la
     * matriz de conmutación especificada.
     * @param emc Entrada de la matriz de conmutación especificada.
     * @since 1.0
     */
    public void enviarSolicitudOkTLDP(TEntradaMatrizConmutacion emc) {
        if (emc != null) {
            if (emc.obtenerIDLDPAntecesor() != TEntradaMatrizConmutacion.SIN_DEFINIR) {
                String IPLocal = this.obtenerIP();
                String IPDestino = puertos.obtenerIPDestinoDelPuerto(emc.obtenerPuertoEntrada());
                if (IPDestino != null) {
                    TPDUTLDP nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TPDUTLDP(gIdent.obtenerNuevo(), IPLocal, IPDestino);
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
                        TPuerto pSalida = puertos.obtenerPuertoDestino(IPDestino);
                        pSalida.ponerPaqueteEnEnlace(nuevoTLDP, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP, nuevoTLDP.obtenerTamanio()));
                            this.generarEventoSimulacion(new TESPaqueteEnviado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este método envía una denegación de etiqueta al nodo que especifique la entrada
     * de la matriz de conmutación correspondiente.
     * @param emc Entrada de la matriz de conmutación correspondiente.
     * @since 1.0
     */
    public void enviarSolicitudNoTLDP(TEntradaMatrizConmutacion emc) {
        if (emc != null) {
            if (emc.obtenerIDLDPAntecesor() != TEntradaMatrizConmutacion.SIN_DEFINIR) {
                String IPLocal = this.obtenerIP();
                String IPDestino = puertos.obtenerIPDestinoDelPuerto(emc.obtenerPuertoEntrada());
                if (IPDestino != null) {
                    TPDUTLDP nuevoTLDP = null;
                    try {
                        nuevoTLDP = new TPDUTLDP(gIdent.obtenerNuevo(), IPLocal, IPDestino);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nuevoTLDP != null) {
                        nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.SOLICITUD_NO);
                        nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.obtenerDestinoFinal());
                        nuevoTLDP.obtenerDatosTLDP().ponerIdentificadorLDP(emc.obtenerIDLDPAntecesor());
                        nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(TEntradaMatrizConmutacion.SIN_DEFINIR);
                        if (emc.obtenerEntranteEsLSPDEBackup()) {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS_BACKUP);
                        } else {
                            nuevoTLDP.ponerSalidaPaquete(TPDUTLDP.ATRAS);
                        }
                        TPuerto pSalida = puertos.obtenerPuertoDestino(IPDestino);
                        pSalida.ponerPaqueteEnEnlace(nuevoTLDP, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP, nuevoTLDP.obtenerTamanio()));
                            this.generarEventoSimulacion(new TESPaqueteEnviado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este método envía una confirmación de eliminación de etiqueta al nodo que
     * especifique la correspondiente entrada en la matriz de conmutación.
     * @since 1.0
     * @param puerto Puierto por el que se debe enviar la confirmación de eliminación.
     * @param emc Entrada de la matriz de conmutación especificada.
     */
    public void enviarEliminacionOkTLDP(TEntradaMatrizConmutacion emc, int puerto) {
        if (emc != null) {
            String IPLocal = this.obtenerIP();
            String IPDestino = puertos.obtenerIPDestinoDelPuerto(puerto);
            if (IPDestino != null) {
                TPDUTLDP nuevoTLDP = null;
                try {
                    nuevoTLDP = new TPDUTLDP(gIdent.obtenerNuevo(), IPLocal, IPDestino);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (nuevoTLDP != null) {
                    nuevoTLDP.obtenerDatosTLDP().ponerMensaje(TDatosTLDP.ELIMINACION_OK);
                    nuevoTLDP.obtenerDatosTLDP().ponerIPDestinoFinal(emc.obtenerDestinoFinal());
                    nuevoTLDP.obtenerDatosTLDP().ponerEtiqueta(TEntradaMatrizConmutacion.SIN_DEFINIR);
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
                    TPuerto pSalida = puertos.obtenerPuerto(puerto);
                    pSalida.ponerPaqueteEnEnlace(nuevoTLDP, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                    try {
                        this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP, nuevoTLDP.obtenerTamanio()));
                        this.generarEventoSimulacion(new TESPaqueteEnviado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * Este método solicita una etiqueta al nodo que se especifica en la entrada de la
     * matriz de conmutación correspondiente.
     * @param emc Entrada en la matriz de conmutación especificada.
     * @since 1.0
     */
    public void solicitarTLDP(TEntradaMatrizConmutacion emc) {
        String IPLocal = this.obtenerIP();
        String IPDestinoFinal = emc.obtenerDestinoFinal();
        if (emc.obtenerEtiqueta() != TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA) {
            String IPSalto = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
            if (IPSalto != null) {
                TPDUTLDP paqueteTLDP = null;
                try {
                    paqueteTLDP = new TPDUTLDP(gIdent.obtenerNuevo(), IPLocal, IPSalto);
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
                    TPuerto pSalida = puertos.obtenerPuertoDestino(IPSalto);
                    if (pSalida != null) {
                        pSalida.ponerPaqueteEnEnlace(paqueteTLDP, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP, paqueteTLDP.obtenerTamanio()));
                            this.generarEventoSimulacion(new TESPaqueteEnviado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este método envía una eliminación de etiqueta al nodo especificado por le
     * entrada de la matriz de conmutación correspondiente.
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la eliminación.
     * @param emc Entrada en la matriz de conmutación especificada.
     */
    public void eliminarTLDP(TEntradaMatrizConmutacion emc, int puerto) {
        if (emc != null) {
            emc.ponerEtiqueta(TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA);
            String IPLocal = this.obtenerIP();
            String IPDestinoFinal = emc.obtenerDestinoFinal();
            String IPSalto = puertos.obtenerIPDestinoDelPuerto(puerto);
            if (IPSalto != null) {
                TPDUTLDP paqueteTLDP = null;
                try {
                    paqueteTLDP = new TPDUTLDP(gIdent.obtenerNuevo(), IPLocal, IPSalto);
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
                    TPuerto pSalida = puertos.obtenerPuerto(puerto);
                    if (pSalida != null) {
                        pSalida.ponerPaqueteEnEnlace(paqueteTLDP, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP, paqueteTLDP.obtenerTamanio()));
                            this.generarEventoSimulacion(new TESPaqueteEnviado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este método reenvía todas las peticiones pendientes de contestación de una
     * entrada de la matriz de conmutación.
     * @param emc Entrada de la matriz de conmutación especificada.
     * @since 1.0
     */
    public void solicitarTLDPTrasTimeout(TEntradaMatrizConmutacion emc) {
        if (emc != null) {
            String IPLocal = this.obtenerIP();
            String IPDestinoFinal = emc.obtenerDestinoFinal();
            String IPSalto = puertos.obtenerIPDestinoDelPuerto(emc.obtenerPuertoSalida());
            if (IPSalto != null) {
                TPDUTLDP paqueteTLDP = null;
                try {
                    paqueteTLDP = new TPDUTLDP(gIdent.obtenerNuevo(), IPLocal, IPSalto);
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
                    TPuerto pSalida = puertos.obtenerPuerto(emc.obtenerPuertoSalida());
                    if (pSalida != null) {
                        pSalida.ponerPaqueteEnEnlace(paqueteTLDP, pSalida.obtenerEnlace().obtenerDestinoLocal(this));
                        try {
                            this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP, paqueteTLDP.obtenerTamanio()));
                            this.generarEventoSimulacion(new TESPaqueteEnviado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), TPDU.TLDP));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Este método reenvía todas las eliminaciones de etiquetas pendientes de una
     * entrada de la matriz de conmutación.
     * @since 1.0
     * @param puerto Puerto por el que se debe enviar la eliminación.
     * @param emc Entrada de la matriz de conmutación especificada.
     */
    public void eliminarTLDPTrasTimeout(TEntradaMatrizConmutacion emc, int puerto){
        eliminarTLDP(emc, puerto);
    }
    
    /**
     * Este método reenvía todas las eliminaciones de etiquetas pendientes de una
     * entrada de la matriz de conmutación a todos los puertos necesarios.
     * @param emc Entrada de la matriz de conmutación especificada.
     * @since 1.0
     */
    public void eliminarTLDPTrasTimeout(TEntradaMatrizConmutacion emc){
        eliminarTLDP(emc, emc.obtenerPuertoEntrada());
        eliminarTLDP(emc, emc.obtenerPuertoSalida());
    }
    
    /**
     * Este método decrementa los contadores de retransmisión existentes para este nodo.
     * @since 1.0
     */
    public void decrementarContadores() {
        TEntradaMatrizConmutacion emc = null;
        this.matrizConmutacion.obtenerCerrojo().bloquear();
        Iterator it = this.matrizConmutacion.obtenerIteradorEntradas();
        while (it.hasNext()) {
            emc = (TEntradaMatrizConmutacion) it.next();
            if (emc != null) {
                emc.decrementarTimeOut(this.obtenerDuracionTic());
                if (emc.obtenerEtiqueta() == TEntradaMatrizConmutacion.SOLICITANDO_ETIQUETA) {
                    if (emc.hacerPeticionDeNuevo()) {
                        emc.reestablecerTimeOut();
                        emc.decrementarIntentos();
                        solicitarTLDPTrasTimeout(emc);
                    }
                } else if (emc.obtenerEtiqueta() == TEntradaMatrizConmutacion.ELIMINANDO_ETIQUETA) {
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
        this.matrizConmutacion.obtenerCerrojo().liberar();
    }
    
    /**
     * Este método crea una nueva entrada en la matriz de conmutación con los datos de
     * un paquete TLDP entrante.
     * @param paqueteSolicitud Paquete TLDP entrante, de solicitud de etiqueta.
     * @param pEntrada Puerto de entrada del paquete TLDP.
     * @return La entrada de la matriz de conmutación, ya creada, insertada e inicializada.
     * @since 1.0
     */
    public TEntradaMatrizConmutacion crearEntradaAPartirDeTLDP(TPDUTLDP paqueteSolicitud, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        int IdTLDPAntecesor = paqueteSolicitud.obtenerDatosTLDP().obtenerIdentificadorLDP();
        TPuerto puertoEntrada = puertos.obtenerPuerto(pEntrada);
        String IPDestinoFinal = paqueteSolicitud.obtenerDatosTLDP().obtenerIPDestinoFinal();
        String IPSalto = topologia.obtenerIPSalto(this.obtenerIP(), IPDestinoFinal);
        if (IPSalto != null) {
            TPuerto puertoSalida = puertos.obtenerPuertoDestino(IPSalto);
            int enlaceOrigen = TEnlaceTopologia.EXTERNO;
            int enlaceDestino = TEnlaceTopologia.INTERNO;
            emc = new TEntradaMatrizConmutacion();
            emc.ponerIDLDPAntecesor(IdTLDPAntecesor);
            emc.ponerDestinoFinal(IPDestinoFinal);
            emc.ponerPuertoEntrada(pEntrada);
            emc.ponerEtiqueta(TEntradaMatrizConmutacion.SIN_DEFINIR);
            emc.ponerLabelFEC(TEntradaMatrizConmutacion.SIN_DEFINIR);
            emc.ponerEntranteEsLSPDEBackup(paqueteSolicitud.obtenerEsParaBackup());
            if (puertoSalida != null) {
                emc.ponerPuertoSalida(puertoSalida.obtenerIdentificador());
            } else {
                emc.ponerPuertoSalida(TEntradaMatrizConmutacion.SIN_DEFINIR);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.obtenerEnlace().obtenerTipo();
            }
            if (puertoSalida != null) {
                enlaceDestino = puertoSalida.obtenerEnlace().obtenerTipo();
            }
            if ((enlaceOrigen == TEnlaceTopologia.EXTERNO) && (enlaceDestino == TEnlaceTopologia.EXTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.FEC);
                emc.ponerOperacion(TEntradaMatrizConmutacion.NOOP);
            } else if ((enlaceOrigen == TEnlaceTopologia.EXTERNO) && (enlaceDestino == TEnlaceTopologia.INTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.FEC);
                emc.ponerOperacion(TEntradaMatrizConmutacion.PONER_ETIQUETA);
            } else if ((enlaceOrigen == TEnlaceTopologia.INTERNO) && (enlaceDestino == TEnlaceTopologia.EXTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.LABEL);
                emc.ponerOperacion(TEntradaMatrizConmutacion.QUITAR_ETIQUETA);
            } else if ((enlaceOrigen == TEnlaceTopologia.INTERNO) && (enlaceDestino == TEnlaceTopologia.INTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.LABEL);
                emc.ponerOperacion(TEntradaMatrizConmutacion.CAMBIAR_ETIQUETA);
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                emc.ponerEtiqueta(TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA);
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
     * Este método crea una nueva entrada en la matriz de conmutación basándose en un
     * paquete IPv4 recibido.
     * @param paqueteIPv4 Paquete IPv4 recibido.
     * @param pEntrada Puerto por el que ha llegado el paquete IPv4.
     * @return La entrada de la matriz de conmutación, creada, insertada e inicializada.
     * @since 1.0
     */
    public TEntradaMatrizConmutacion crearEntradaInicialEnMatrizFEC(TPDUIPv4 paqueteIPv4, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        String IPLocal = this.obtenerIP();
        String IPDestinoFinal = paqueteIPv4.obtenerCabecera().obtenerIPDestino();
        String IPSalida = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPuerto puertoEntrada = puertos.obtenerPuerto(pEntrada);
            TPuerto puertoSalida = puertos.obtenerPuertoDestino(IPSalida);
            int enlaceOrigen = TEnlaceTopologia.EXTERNO;
            int enlaceDestino = TEnlaceTopologia.INTERNO;
            emc = new TEntradaMatrizConmutacion();
            emc.ponerIDLDPAntecesor(TEntradaMatrizConmutacion.SIN_DEFINIR);
            emc.ponerDestinoFinal(IPDestinoFinal);
            emc.ponerPuertoEntrada(pEntrada);
            emc.ponerEtiqueta(TEntradaMatrizConmutacion.SIN_DEFINIR);
            emc.ponerLabelFEC(clasificarPaquete(paqueteIPv4));
            emc.ponerEntranteEsLSPDEBackup(false);
            if (puertoSalida != null) {
                emc.ponerPuertoSalida(puertoSalida.obtenerIdentificador());
                enlaceDestino = puertoSalida.obtenerEnlace().obtenerTipo();
            } else {
                emc.ponerPuertoSalida(TEntradaMatrizConmutacion.SIN_DEFINIR);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.obtenerEnlace().obtenerTipo();
            }
            if ((enlaceOrigen == TEnlaceTopologia.EXTERNO) && (enlaceDestino == TEnlaceTopologia.EXTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.FEC);
                emc.ponerOperacion(TEntradaMatrizConmutacion.NOOP);
            } else if ((enlaceOrigen == TEnlaceTopologia.EXTERNO) && (enlaceDestino == TEnlaceTopologia.INTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.FEC);
                emc.ponerOperacion(TEntradaMatrizConmutacion.PONER_ETIQUETA);
            } else if ((enlaceOrigen == TEnlaceTopologia.INTERNO) && (enlaceDestino == TEnlaceTopologia.EXTERNO)) {
                // No es posible
            } else if ((enlaceOrigen == TEnlaceTopologia.INTERNO) && (enlaceDestino == TEnlaceTopologia.INTERNO)) {
                // No es posible
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                emc.ponerEtiqueta(TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA);
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
     * Este método crea una nueva entrada en la matriz de conmutación basándose en un
     * paquete MPLS recibido.
     * @param paqueteMPLS Paquete MPLS recibido.
     * @param pEntrada Puerto por el que ha llegado el paquete MPLS.
     * @return La entrada de la matriz de conmutación, creada, insertada e inicializada.
     * @since 1.0
     */
    public TEntradaMatrizConmutacion crearEntradaInicialEnMatrizLABEL(TPDUMPLS paqueteMPLS, int pEntrada) {
        TEntradaMatrizConmutacion emc = null;
        String IPLocal = this.obtenerIP();
        String IPDestinoFinal = paqueteMPLS.obtenerCabecera().obtenerIPDestino();
        String IPSalida = topologia.obtenerIPSalto(IPLocal, IPDestinoFinal);
        if (IPSalida != null) {
            TPuerto puertoEntrada = puertos.obtenerPuerto(pEntrada);
            TPuerto puertoSalida = puertos.obtenerPuertoDestino(IPSalida);
            int enlaceOrigen = TEnlaceTopologia.EXTERNO;
            int enlaceDestino = TEnlaceTopologia.INTERNO;
            emc = new TEntradaMatrizConmutacion();
            emc.ponerIDLDPAntecesor(TEntradaMatrizConmutacion.SIN_DEFINIR);
            emc.ponerDestinoFinal(IPDestinoFinal);
            emc.ponerPuertoEntrada(pEntrada);
            emc.ponerEtiqueta(TEntradaMatrizConmutacion.SIN_DEFINIR);
            emc.ponerEntranteEsLSPDEBackup(false);
            emc.ponerLabelFEC(paqueteMPLS.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerLabel());
            if (puertoSalida != null) {
                emc.ponerPuertoSalida(puertoSalida.obtenerIdentificador());
                enlaceDestino = puertoSalida.obtenerEnlace().obtenerTipo();
            } else {
                emc.ponerPuertoSalida(TEntradaMatrizConmutacion.SIN_DEFINIR);
            }
            if (puertoEntrada != null) {
                enlaceOrigen = puertoEntrada.obtenerEnlace().obtenerTipo();
            }
            if ((enlaceOrigen == TEnlaceTopologia.EXTERNO) && (enlaceDestino == TEnlaceTopologia.EXTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.LABEL);
                emc.ponerOperacion(TEntradaMatrizConmutacion.NOOP);
            } else if ((enlaceOrigen == TEnlaceTopologia.EXTERNO) && (enlaceDestino == TEnlaceTopologia.INTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.LABEL);
                emc.ponerOperacion(TEntradaMatrizConmutacion.PONER_ETIQUETA);
            } else if ((enlaceOrigen == TEnlaceTopologia.INTERNO) && (enlaceDestino == TEnlaceTopologia.EXTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.LABEL);
                emc.ponerOperacion(TEntradaMatrizConmutacion.QUITAR_ETIQUETA);
            } else if ((enlaceOrigen == TEnlaceTopologia.INTERNO) && (enlaceDestino == TEnlaceTopologia.INTERNO)) {
                emc.ponerTipo(TEntradaMatrizConmutacion.LABEL);
                emc.ponerOperacion(TEntradaMatrizConmutacion.CAMBIAR_ETIQUETA);
            }
            if (soyLERDeSalida(IPDestinoFinal)) {
                emc.ponerLabelFEC(matrizConmutacion.obtenerEtiqueta());
                emc.ponerEtiqueta(TEntradaMatrizConmutacion.ETIQUETA_CONCEDIDA);
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
     * Este método toma un paquete IPv4 y la entrada de la matriz de conmutación
     * asociada al mismo y crea un paquete MPLS etiquetado correctamente que contiene
     * dicho paquete IPv4 listo para ser transmitido hacia el interior del dominio.
     * @param paqueteIPv4 Paquete IPv4 que se debe etiquetar.
     * @param emc Entrada de la matriz de conmutación asociada al paquete IPv4 que se desea
     * etiquetar.
     * @return El paquete IPv4 de entrada, convertido en un paquete MPLS correctamente
     * etiquetado.
     * @since 1.0
     */
    public TPDUMPLS crearPaqueteMPLS(TPDUIPv4 paqueteIPv4, TEntradaMatrizConmutacion emc) {
        TPDUMPLS paqueteMPLS = null;
        try {
            paqueteMPLS = new TPDUMPLS(gIdent.obtenerNuevo(), paqueteIPv4.obtenerCabecera().obtenerIPOrigen(), paqueteIPv4.obtenerCabecera().obtenerIPDestino(), paqueteIPv4.obtenerTamanio());
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace();
        }
        paqueteMPLS.ponerCabecera(paqueteIPv4.obtenerCabecera());
        paqueteMPLS.ponerDatosTCP(paqueteIPv4.obtenerDatos());
        paqueteMPLS.ponerSubtipo(TPDU.MPLS);
        TEtiquetaMPLS empls = new TEtiquetaMPLS();
        empls.ponerBoS(true);
        empls.ponerEXP(0);
        empls.ponerLabel(emc.obtenerEtiqueta());
        empls.ponerTTL(paqueteIPv4.obtenerCabecera().obtenerTTL()-1);
        paqueteMPLS.obtenerPilaEtiquetas().ponerEtiqueta(empls);
        paqueteIPv4 = null;
        try {
            this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paqueteMPLS.obtenerSubTipo(), paqueteMPLS.obtenerTamanio()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paqueteMPLS;
    }
    
    /**
     * Este método toma como parámetro un paquete MPLS y su entrada en la matriz de
     * conmutación asociada. Extrae del paquete MPLS el paquete IP correspondiente y
     * actualiza sus valores correctamente.
     * @param paqueteMPLS Paquete MPLS cuyo contenido de nivel IPv4 se desea extraer.
     * @param emc Entrada de la matriz de conmutación asociada al paquete MPLS.
     * @return Paquete IPv4 que corresponde al paquete MPLS una vez que se ha eliminado toda la
     * información MLPS; que se ha desetiquetado.
     * @since 1.0
     */
    public TPDUIPv4 crearPaqueteIPv4(TPDUMPLS paqueteMPLS, TEntradaMatrizConmutacion emc) {
        TPDUIPv4 paqueteIPv4 = null;
        try {
            paqueteIPv4 = new TPDUIPv4(gIdent.obtenerNuevo(), paqueteMPLS.obtenerCabecera().obtenerIPOrigen(), paqueteMPLS.obtenerCabecera().obtenerIPDestino(), paqueteMPLS.obtenerDatosTCP().obtenerTamanio());
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace();
        }
        paqueteIPv4.ponerCabecera(paqueteMPLS.obtenerCabecera());
        paqueteIPv4.ponerDatos(paqueteMPLS.obtenerDatosTCP());
        paqueteIPv4.obtenerCabecera().ponerTTL(paqueteMPLS.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerTTL());
        if (paqueteIPv4.obtenerCabecera().obtenerCampoOpciones().estaUsado()) {
            paqueteIPv4.ponerSubtipo(TPDU.IPV4_GOS);
        } else {
            paqueteIPv4.ponerSubtipo(TPDU.IPV4);
        }
        try {
            this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paqueteIPv4.obtenerSubTipo(), paqueteIPv4.obtenerTamanio()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        paqueteMPLS = null;
        return paqueteIPv4;
    }
    
    /**
     * Este método comprueba si un paquete recibido es un paquete del interior del
     * dominio MPLS o es un paquete externo al mismo.
     * @param paquete Paquete que ha llegado al nodo.
     * @param pEntrada Puerto por el que ha llegado el paquete al nodo.
     * @return true, si el paquete es exterior al dominio MPLS. false en caso contrario.
     * @since 1.0
     */
    public boolean esUnPaqueteExterno(TPDU paquete, int pEntrada) {
        if (paquete.obtenerTipo() == TPDU.IPV4)
            return true;
        TPuerto pe = puertos.obtenerPuerto(pEntrada);
        if (pe.obtenerEnlace().obtenerTipo() == TEnlaceTopologia.EXTERNO)
            return true;
        return false;
    }
    
    /**
     * Este método contabiliza un paquete recibido o conmutado en las estadísticas del
     * nodo.
     * @param paquete paquete que se desa contabilizar.
     * @param deEntrada TRUE, si el paquete se ha recibido en el nodo. FALSE so el paquete ha salido del
     * nodo.
     * @since 1.0
     */
    public void contabilizarPaquete(TPDU paquete, boolean deEntrada) {
    }
    
    /**
     * Este método descarta un paquete en el nodo y refleja dicho descarte en las
     * estadísticas del nodo.
     * @param paquete Paquete que se quiere descartar.
     * @since 1.0
     */
    public void descartarPaquete(TPDU paquete) {
        try {
            this.generarEventoSimulacion(new TESPaqueteDescartado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
            this.estadisticas.crearEstadistica(paquete, TEstadisticas.DESCARTE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paquete = null;
    }
    
    /**
     * Este método toma como parametro un paquete, supuestamente sin etiquetar, y lo
     * clasifica. Esto significa que determina el FEC al que pertenece el paquete.
     * Este valor se calcula como el código HASH practicado a la concatenación de la IP
     * de origen y la IP de destino. En la práctica esto significa que paquetes con el
     * mismo origen y con el mismo destino pertenecerán al mismo FEC.
     * @param paquete El paquete que se desea clasificar.
     * @return El FEC al que pertenece el paquete pasado por parámetros.
     * @since 1.0
     */
    public int clasificarPaquete(TPDU paquete) {
        String IPOrigen = paquete.obtenerCabecera().obtenerIPOrigen();
        String IPDestino = paquete.obtenerCabecera().obtenerIPDestino();
        String cadenaFEC = cadenaFEC = IPOrigen + IPDestino;
        return cadenaFEC.hashCode();
    }
    
    /**
     * Este método permite el acceso al conjunto de puertos del nodo.
     * @return El conjunto de puertos del nodo.
     * @since 1.0
     */
    public TPuertosNodo obtenerPuertos() {
        return this.puertos;
    }
    
    /**
     * Este método calcula si el nodo tiene puertos libres o no.
     * @return true, si el nodo tiene puertos libres. false en caso contrario.
     * @since 1.0
     */
    public boolean tienePuertosLibres() {
        return this.puertos.hayPuertosLibres();
    }
    
    /**
     * Este método calcula el peso del nodo. Se utilizará para calcular rutas con costo
     * menor. En el nodo LER el pero será siempre nulo (cero).
     * @return 0. El peso siempre será nulo en un LER.
     * @since 1.0
     */
    public long obtenerPeso() {
        long peso = 0;
        long pesoC = (long) (this.puertos.obtenerCongestion() * (0.7));
        long pesoMC = (long) ((10*this.matrizConmutacion.obtenerNumeroEntradas())* (0.3));
        peso = pesoC + pesoMC;
        return peso;
    }
    
    /**
     * Este método comprueba si la isntancia actual es el LER de salida del dominio
     * MPLS para una IP dada.
     * @param ip IP de destino del tráfico, para la cual queremos averiguar si el LER es nodo de
     * salida.
     * @return true, si el LER es de salida del dominio para tráfico dirigido a esa IP. false
     * en caso contrario.
     * @since 1.0
     */
    public boolean soyLERDeSalida(String ip) {
        TPuerto p = puertos.obtenerPuertoDestino(ip);
        if (p != null)
            if (p.obtenerEnlace().obtenerTipo() == TEnlaceTopologia.EXTERNO)
                return true;
        return false;
    }
    
    /**
     * Este método permite el acceso a la matriz de conmutación de LER.
     * @return La matriz de conmutación del LER.
     * @since 1.0
     */
    public TMatrizConmutacion obtenerMatrizConmutacion() {
        return matrizConmutacion;
    }
    
    /**
     * Este método comprueba que la configuración de LER sea la correcta.
     * @return true, si el LER está bien configurado. false en caso contrario.
     * @since 1.0
     */
    public boolean estaBienConfigurado() {
        return this.bienConfigurado;
    }
    
    /**
     * Este método comprueba que una cierta configuración es válida.
     * @param t Topología a la que pertenece el LER.
     * @param recfg true si se trata de una reconfiguración. false en caso contrario.
     * @return CORRECTA, si la configuración es correcta. Un código de error en caso contrario.
     * @since 1.0
     */
    public int comprobar(TTopologia t, boolean recfg) {
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
            TNodoTopologia tp = t.obtenerPrimerNodoLlamado(this.obtenerNombre());
            if (tp != null)
                return this.NOMBRE_YA_EXISTE;
        } else {
            TNodoTopologia tp = t.obtenerPrimerNodoLlamado(this.obtenerNombre());
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
     * Este método toma un codigo de error y genera un mensaje textual del mismo.
     * @param e El código de error para el cual queremos una explicación textual.
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
     * Este método forma una cadena de texto que representa al LER y toda su
     * configuración. Sirve para almacenar el LER en disco.
     * @return Una cadena de texto que representa un a este LER.
     * @since 1.0
     */
    public String serializar() {
        String cadena = "#LER#";
        cadena += this.obtenerIdentificador();
        cadena += "#";
        cadena += this.obtenerNombre().replace('#', ' ');
        cadena += "#";
        cadena += this.obtenerIP();
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
        cadena += this.obtenerPuertos().obtenerTamanioBuffer();
        cadena += "#";
        return cadena;
    }
    
    /**
     * Este método toma como parámetro una cadena de texto que debe pertencer a un LER
     * serializado y configura esta instancia con los valores de dicha caddena.
     * @param elemento LER serializado.
     * @return true, si no ha habido errores y la instancia actual está bien configurada. false
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
        this.obtenerPuertos().ponerTamanioBuffer(Integer.valueOf(valores[11]).intValue());
        return true;
    }
    
    /**
     * Este método permite acceder directamente a las estadisticas del nodo.
     * @return Las estadísticas del nodo.
     * @since 1.0
     */
    public TEstadisticas accederAEstadisticas() {
        return estadisticas;
    }
    
    /**
     * Este método permite establecer el número de puertos que tendrá el nodo.
     * @param num Número de puertos deseado para el nodo. Como mucho, 8 puertos.
     * @since 1.0
     */
    public synchronized void ponerPuertos(int num) {
        puertos = new TPuertosNodoNormal(num, this);
    }
    
    /**
     * Este método no hace nada en un LSR. En un nodo activoPermitirá solicitar
     * a un nodo activo la retransmisión de un paquete.
     * @param paquete Paquete cuya retransmisión se está solicitando.
     * @param pSalida Puerto por el que se enviará la solicitud.
     * @since 1.0
     */
    public void solicitarGPSRP(TPDUMPLS paquete, int pSalida) {
    }
    
    /**
     * Esta constante indica que la configuración del nodo LER esta correcta, que no
     * contiene errores.
     * @since 1.0
     */
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que el nombre del nodo LER no está definido.
     * @since 1.0
     */
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que el nombre especificado para el LER ya está siendo
     * usado por otro nodo de la topologia.
     * @since 1.0
     */
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que el nombre que se ha definido para el LER contiene sólo
     * constantes.
     * @since 1.0
     */
    public static final int SOLO_ESPACIOS = 3;
    
    private TMatrizConmutacion matrizConmutacion;
    private TIdentificadorLargo gIdent;
    private TIdentificador gIdentLDP;
    private int potenciaEnMb;
    private TEstadisticasLER estadisticas;
}
