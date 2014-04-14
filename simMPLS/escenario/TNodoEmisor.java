//**************************************************************************
// Nombre......: TNodoEmisor.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un nodo emisor de la topología.
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
import simMPLS.protocolo.*;

/**
 * Esta clase implementa un nodo emisor de tráfico.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TNodoEmisor extends TNodoTopologia implements IEventoRelojListener, Runnable {
    
    /**
     * Crea una nueva instanci de TNodoEmisor
     * @param identificador Identificar único del nodo en la topología.
     * @param d Dirección IP del nodo.
     * @param il Generador de identigficadores para los eventos generados por el nodo.
     * @param t Topología dentro de la cual se encuentra el nodo.
     * @since 1.0
     */
    public TNodoEmisor(int identificador, String d, TIdentificadorLargo il, TTopologia t) {
        super(identificador, d, il, t);
        this.ponerPuertos(super.NUM_PUERTOS_EMISOR);
        gIdent = new TIdentificadorLargo();
        gIdGoS = new TIdentificadorRotativo();
        String IPDestino = "";
        tasaTransferencia = 10;
        tipoTrafico = TNodoEmisor.CONSTANTE;
        encapsularSobreMPLS = false;
        nivelDeGoS = 0;
        LSPDeBackup = false;
        generadorDeAleatorios = new Random();
        etiquetaDeEmision = (16 + generadorDeAleatorios.nextInt(1000000));
        tamDatosConstante = 0;
        tamDatosVariable = 0;
        estadisticas = new TEstadisticasEmisor();
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
    }
    
    /**
     * Este método obtiene el tamanio de la carga útil los paquetes de datos constantes
     * que debe generar el nodo si está configurado para tráfico constante.
     * @return El tamaño de la carga útil de los paquetes constantes.
     * @since 1.0
     */    
    public int obtenerTamDatosConstante() {
        return this.tamDatosConstante;
    }
    
    /**
     * Este método permite establecer el tamaño de la carga util de los paquetes constantes que debe
     * generar el nodo.
     * @param tdc Tamanio de la carga util de los paquetes para tráfico constante.
     * @since 1.0
     */    
    public void ponerTamDatosConstante(int tdc) {
        this.tamDatosConstante = tdc;
    }
    
    /**
     * Este método permite establecer el nodo destino del tráfico generado.
     * @param d IP del nodo destino del tráfico.
     * @since 1.0
     */
    public void ponerDestino(String d) {
        if (!d.equals("")) {
            TNodoTopologia nt = this.topologia.obtenerPrimerNodoLlamado(d);
            if (nt != null) {
                IPDestino = nt.obtenerIP();
            }
        }
    }
    
    /**
     * Este método permite obtener la IP del nodo destino del tráfico generado.
     * @return La IP del nodo destino del tráfico generado.
     * @since 1.0
     */
    public String obtenerDestino() {
        return IPDestino;
    }
    
    /**
     * Este método permite establecer la tasa de generación de tráfico del nodo.
     * @param t Tasa de generación de tráfico elegida para el nodo. En Mbps.
     * @since 1.0
     */
    public void ponerTasaTrafico(int t) {
        tasaTransferencia = t;
    }
    
    /**
     * Este método permite obtener la tasa de generación de tráfico del nodo.
     * @return Tasa de generación de tráfico el nodo. En Mbps.
     * @since 1.0
     */
    public int obtenerTasaTrafico() {
        return tasaTransferencia;
    }
    
    private int obtenerCodificacionEXP() {
        if ((this.nivelDeGoS == 0) && (this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL0_CONLSP;
        } else if ((this.nivelDeGoS == 1) && (this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL1_CONLSP;
        } else if ((this.nivelDeGoS == 2) && (this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL2_CONLSP;
        } else if ((this.nivelDeGoS == 3) && (this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL3_CONLSP;
        } else if ((this.nivelDeGoS == 0) && (!this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL0_SINLSP;
        } else if ((this.nivelDeGoS == 1) && (!this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL1_SINLSP;
        } else if ((this.nivelDeGoS == 2) && (!this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL2_SINLSP;
        } else if ((this.nivelDeGoS == 3) && (!this.LSPDeBackup)) {
            return TPDU.EXP_NIVEL3_SINLSP;
        }
        return TPDU.EXP_NIVEL0_SINLSP;
    }
    
    /**
     * Este método permite establecer qué tipo de tráfico generará el nodo.
     * @param t Tipo de tráfico generado por el nodo. Una de las constantes definidas en esta
     * clase.
     * @since v
     */
    public void ponerTipoTrafico(int t) {
        tipoTrafico = t;
    }
    
    /**
     * Este método permite obtener el tipo de tráfico que está generando el nodo.
     * @return Tipo de tráfico generado por el nodo. Una de las constantes de esta clase.
     * @since 1.0
     */
    public int obtenerTipoTrafico() {
        return tipoTrafico;
    }
    
    /**
     * Este método permite establecer si el tráfico generado está ya estiquetado en
     * MPLS o no. Lo que es lo mismo, si el tráfico proviene de otro dominio MPLS o no.
     * @param mpls TRUE, si el nodo debe generar tráfico MPLS. FALSE en caso contrario.
     * @since 1.0
     */
    public void ponerSobreMPLS(boolean mpls) {
        encapsularSobreMPLS = mpls;
    }
    
    /**
     * Este método permite obtener si el tráfico que está generando el nodo es ya
     * tráfico MPLS o no.
     * @return TRUE, si el tráfico que está generando el nodo es MPLS. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean obtenerSobreMPLS() {
        return encapsularSobreMPLS;
    }
    
    /**
     * Este método permite establecer el nivel de garantía de servicio con el que el
     * nodo debe marcar los paquetes generados.
     * @param gos Nivel de garantía de servicio.
     * @since 1.0
     */
    public void ponerNivelDeGoS(int gos) {
        nivelDeGoS = gos;
    }
    
    /**
     * Este método permite obtener el nivel de garantía de servicio con el que el nodo
     * está marcando los paquetes generados.
     * @return El nivel de garantía de servicio con el que el nodo está marcando los paquetes
     * generados.
     * @since 1.0
     */
    public int obtenerNivelDeGoS() {
        return nivelDeGoS;
    }
    
    /**
     * Este método permite establecer si los paquetes generados serán marcdos para
     * requerir un LSP de respaldo en el dominio MPLS o no.
     * @param l TRUE si los paqutes requerirán LSP de respaldo. FALSE en caso contrario.
     * @since 1.0
     */
    public void ponerLSPDeBackup(boolean l) {
        LSPDeBackup = l;
    }
    
    /**
     * Este método permite saber si los paquetes generados están siendo marcados para
     * requerir un LSP de respaldo o no.
     * @return TRUE, si los paquetes esán siendo marcados para requerir un LSP de respaldo.
     * FALSE en caso contrario.
     * @since 1.0
     */
    public boolean obtenerLSPDeBackup() {
        return LSPDeBackup;
    }
    
    /**
     * Este método permite obtener el tipo de nodo del que se trata esta instancia.
     * @return TNodoTopologia.EMISOR, indicando que es un generador y emisor de tráfico.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.EMISOR;
    }
    
    /**
     * Este método permite recibir eventos de sincronización del reloj principal del
     * simulador, que es quien sincroniza todo.
     * @param evt Evento de sincronización enviado por el reloj principal.
     * @since 1.0
     */
    public void capturarEventoReloj(TEventoReloj evt) {
        this.ponerDuracionTic(evt.obtenerDuracionTic());
        this.ponerInstanteDeTiempo(evt.obtenerLimiteSuperior());
        this.nsDisponibles += evt.obtenerDuracionTic();
        this.iniciar();
    }
    
    /**
     * Este método se llama cuando el hilo independiente del nodo se pone en
     * funcionamiento. Es el núcleo del nodo.
     * @since 1.0
     */
    public void run() {
        try {
            this.generarEventoSimulacion(new TESNodoCongestionado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), 0));
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        TPDU paqueteTmp = crearPaquete();
        boolean emito = false;
        while (obtenerOctetosTransmitibles() > obtenerTamanioSiguientePaquete(paqueteTmp)) {
            emito = true;
            generarTrafico();
        }
        paqueteTmp = null;
        if (emito) {
            this.restaurarPasosSinEmitir();
        } else {
            this.incrementarPasosSinEmitir();
        }
        this.estadisticas.asentarDatos(this.obtenerInstanteDeTiempo());
    }
    
    /**
     * Este método obtiene el tamaño que tendrá la carga util del siguiente paquete
     * generado, independientemente de que se esté tratando con tráfico constante o
     * variable.
     * @return El tamaño de la carga útil del siguiente paquete que generará el emisor.
     * @since 1.0
     */    
    public int obtenerTamanioDatosSiguientePaquete() {
        if (this.tipoTrafico == TNodoEmisor.CONSTANTE) {
            return this.tamDatosConstante;
        }
        return this.tamDatosVariable;
    }
    
    /**
     * Este método obtiene el tamaño de la cabecera del sigueinte paquete que se
     * generará, independientemente del tipo de tráfico del que se trate y de los
     * valores de garantía de Servicio con los que peuda estar marcado.
     * @param paquete Paquete de cuya cabecera queremos conocer el tamaño.
     * @return El tamaño de la cabecera.
     * @since 1.0
     */    
    public int obtenerTamanioCabeceraSiguientePaquete(TPDU paquete) {
        TPDUMPLS paqueteMPLS = null;
        TPDUIPv4 paqueteIPv4 = null;
        if (paquete.obtenerTipo() == TPDU.MPLS) {
            paqueteMPLS = (TPDUMPLS) paquete;
            return paqueteMPLS.obtenerTamanio();
        } 
        paqueteIPv4 = (TPDUIPv4) paquete;
        return paqueteIPv4.obtenerTamanio();
    }
    
    /**
     * Este método calcula, el tamaño del siguiente paquete a generar,
     * independientemente de que se de tipo constante o variable, o de cualquier
     * protocolo de los soportados, e incluso de que nivel de GoS tenga asignado.
     * @param paquete paquete cuyo tamanio se desea calcular.
     * @return El tamaño total del paquete especificado por parámetros.
     * @since 1.0
     */    
    public int obtenerTamanioSiguientePaquete(TPDU paquete) {
        int tamanioDatos = 0;
        int tamanioCabecera = 0;
        int tamanioFinal = 0;
        tamanioDatos = obtenerTamanioDatosSiguientePaquete();
        tamanioCabecera = obtenerTamanioCabeceraSiguientePaquete(paquete);
        tamanioFinal = tamanioDatos + tamanioCabecera;
        return tamanioFinal;
    }
    
    /**
     * Este método crea paquetes de tráfico acorde a la configuración el emisor de
     * tráfico y los envía al receptor destino del tráfico.
     * @since 1.0
     */    
    public void generarTrafico() {
        TPDU paquete=null;
        TPDU paqueteConTamanio=null;
        TPuerto pt = puertos.obtenerPuerto(0);
        if (pt != null) {
            if (!pt.estaLibre()) {
                paquete = crearPaquete();
                paqueteConTamanio = this.ponerTamanio(paquete);
                if (paqueteConTamanio != null) {
                    try {
                        int tipo = 0;
                        if (paqueteConTamanio.obtenerTipo() == TPDU.MPLS) {
                            TPDUMPLS paqueteMPLS = (TPDUMPLS) paqueteConTamanio;
                            tipo = paqueteMPLS.obtenerSubTipo();
                        } else if (paqueteConTamanio.obtenerTipo() == TPDU.IPV4) {
                            TPDUIPv4 paqueteIPv4 = (TPDUIPv4) paqueteConTamanio;
                            tipo = paqueteIPv4.obtenerSubTipo();
                        }
                        this.generarEventoSimulacion(new TESPaqueteGenerado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), tipo, paqueteConTamanio.obtenerTamanio()));
                        this.generarEventoSimulacion(new TESPaqueteEnviado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), tipo));
                    } catch (Exception e) {
                        e.printStackTrace(); 
                    }
                    if (this.topologia.obtenerIPSalto(this.obtenerIP(), this.obtenerDestino()) != null) {
                        pt.ponerPaqueteEnEnlace(paqueteConTamanio, pt.obtenerEnlace().obtenerDestinoLocal(this));
                    } else {
                        descartarPaquete(paqueteConTamanio);
                    }
                } 
            }
        }
    }

    /**
     * Este método contabiliza un paquete y su tamaño asociado, en las estadísticas del
     * nodo emisor, y sus gráficas.
     * @param paquete Paquete que se desea contabilizar.
     * @param deEntrada TRUE indica que se trata de un paquete que ha entrado en el nodo. FALSE indica
     * que se trata de un paquete que ha salido del nodo.
     * @since 1.0
     */    
    public void contabilizarPaquete(TPDU paquete, boolean deEntrada) {
        if (deEntrada) {
            if (paquete.obtenerSubTipo() == TPDU.MPLS) {
            } else if (paquete.obtenerSubTipo() == TPDU.MPLS_GOS) {
            } else if (paquete.obtenerSubTipo() == TPDU.IPV4) {
            } else if (paquete.obtenerSubTipo() == TPDU.IPV4_GOS) {
            }
        } else {
            if (paquete.obtenerSubTipo() == TPDU.MPLS) {
            } else if (paquete.obtenerSubTipo() == TPDU.MPLS_GOS) {
            } else if (paquete.obtenerSubTipo() == TPDU.IPV4) {
            } else if (paquete.obtenerSubTipo() == TPDU.IPV4_GOS) {
            }
        }
    }
    
    /**
     * Este método calcula cuántos nanosegundos necesita el nodo emisor para generar y
     * transmitir un bit. Se basa para ello en la tasa de generación de tráfico del
     * nodo.
     * @return El número de nanosegundos necesarios generar y transmitir un bit.
     * @since 1.0
     */    
    public double obtenerNsPorBit() {
        long tasaEnBitsPorSegundo = (long) (this.tasaTransferencia*1048576L);
        double nsPorCadaBit = (double) ((double)1000000000.0/(long)tasaEnBitsPorSegundo);
        return nsPorCadaBit;
    }
    
    /**
     * Este método calcula el número de nanosegundos necesarios para poder generar y
     * enviar un determinado número de octetos.
     * @param octetos Número de octetos que deseamos generar y transmitir.
     * @return Número de nanosegundos que necesitamos para los octetos especificados.
     * @since 1.0
     */    
    public double obtenerNsUsadosTotalOctetos(int octetos) {
        double nsPorCadaBit = obtenerNsPorBit();
        long bitsOctetos = (long) ((long)octetos*(long)8);
        return (double)((double)nsPorCadaBit*(long)bitsOctetos);
    }
    
    /**
     * Este método calcula el número de bits que puede generar y transmitir con el
     * número de nanosegundos con los que cuenta.
     * @return Número de bits que puede generar y transmitir.
     * @since 1.0
     */    
    public int obtenerLimiteBitsTransmitibles() {
        double nsPorCadaBit = obtenerNsPorBit();
        double maximoBits = (double) ((double)nsDisponibles/(double)nsPorCadaBit);
        return (int) maximoBits;
    }
    
    /**
     * Este metodo calcula el número de octetos completos que puede generar y
     * transmitir el emisor teniendo en cuenta el número de nanosegundos con los que
     * cuenta.
     * @return El número de octetos completos que se pueden generar y transmitir.
     * @since 1.0
     */    
    public int obtenerOctetosTransmitibles() {
        double maximoBytes = ((double)obtenerLimiteBitsTransmitibles()/(double)8.0);
        return (int) maximoBytes;
    }
    
    /**
     * Este método calcula automáticamente el tamaño de la carga util del siguiente
     * paquete a generar. Si el tráfico es constante, devolverá el tamaño de paquete
     * con el que se configuró el emisor. Si el tamaño es variable, generará tráfico
     * siguiendo una función de probabilidad en la cual se sigue la siguiente
     * distribución de tamaños:
     *
     * TamañoPaquete < 100 octetos ---------------------> 47%
     * TamañoPaquete >= 100 octetos y < 1400 octetos ---> 24%
     * TamañoPaquete >= 1400 octetos y < 1500 octetos --> 18%
     * TamañoPaquete >= 1500 octetos -------------------> 1%
     *
     * Esta distribución está extraída de las estadísticas de la red Abilene, que son
     * públicas y se pueden observar en http://netflow.internet2.edu/weekly.
     * @return El tamaño que debe tener el siguiente paquete.
     * @since 1.0
     */    
    public int generarTamanioSiguientePaquete() {
        int probabilidad = this.generadorDeAleatorios.nextInt(100);
        int tamanioGenerado = 0;
        if (probabilidad < 47) {
            tamanioGenerado = this.generadorDeAleatorios.nextInt(100);
            tamanioGenerado -= 40;
        } else if ((probabilidad >= 47) && (probabilidad < 71)) {
            tamanioGenerado = (this.generadorDeAleatorios.nextInt(1300) + 100);
            tamanioGenerado -= 40;
        } else if ((probabilidad >= 71) && (probabilidad < 99)) {
            tamanioGenerado = (this.generadorDeAleatorios.nextInt(100) + 1400);
            tamanioGenerado -= 40;
        } else if (probabilidad >= 99) {
            tamanioGenerado = (this.generadorDeAleatorios.nextInt(64035) + 1500);
            tamanioGenerado -= 40;
        }
        return tamanioGenerado;
    }
    
    /**
     * Este método toma como parámetro un paquete vacio y devuelve un paquete con datos
     * insertados. Los datos serán del tamaño que se haya estimado en los distintos
     * métodos de la clase,pero será el correcto.
     * @param paquete Paquete al que se quiere añadir datos.
     * @return Paquete con datos insertados del tamaño correcto según el tipo de gráfico.
     * @since 1.0
     */    
    public TPDU ponerTamanio(TPDU paquete) {
        TPDUMPLS paqueteMPLS = null;
        TPDUIPv4 paqueteIPv4 = null;
        int bitsMaximos = obtenerLimiteBitsTransmitibles();
        int tamanioCabecera = 0;
        int tamanioDatos = 0;
        int tamanioTotal = 0;
        double nsUsados = 0;
        tamanioTotal = obtenerTamanioSiguientePaquete(paquete);
        tamanioCabecera = obtenerTamanioCabeceraSiguientePaquete(paquete);
        tamanioDatos = obtenerTamanioDatosSiguientePaquete();
        if (tamanioTotal > obtenerOctetosTransmitibles()) {
            paquete = null;
            return null;
        } else {
            if (paquete.obtenerTipo() == TPDU.MPLS) {
                paqueteMPLS = (TPDUMPLS) paquete;
                paqueteMPLS.obtenerDatosTCP().ponerTamanio((int) tamanioDatos);
                nsUsados = this.obtenerNsUsadosTotalOctetos(tamanioTotal);
                this.nsDisponibles -= nsUsados;
                if (this.nsDisponibles < 0)
                    this.nsDisponibles = 0;
                if (this.tipoTrafico == this.VARIABLE) {
                    this.tamDatosVariable = this.generarTamanioSiguientePaquete();
                }
                return paqueteMPLS;
            } else if (paquete.obtenerTipo() == TPDU.IPV4) {
                paqueteIPv4 = (TPDUIPv4) paquete;
                paqueteIPv4.obtenerDatos().ponerTamanio(tamanioDatos);
                nsUsados = this.obtenerNsUsadosTotalOctetos(tamanioTotal);
                this.nsDisponibles -= nsUsados;
                if (this.nsDisponibles < 0)
                    this.nsDisponibles = 0;
                if (this.tipoTrafico == this.VARIABLE) {
                    this.tamDatosVariable = this.generarTamanioSiguientePaquete();
                }
                return paqueteIPv4;
            }
        }
        return null;
    }
    
    /**
     * Este método devuelve un paquete vacío (sin datos) del tipo correcto para el que
     * esta configurado el nodo emisor.
     * @return El paquete creado.
     * @since 1.0
     */    
    public TPDU crearPaquete() {
        int valorGoS = this.obtenerCodificacionEXP();
        try {
            if (this.encapsularSobreMPLS) {
                if (valorGoS == TPDU.EXP_NIVEL0_SINLSP) {
                    TPDUMPLS paquete = new TPDUMPLS(gIdent.obtenerNuevo(), obtenerIP(), this.IPDestino, 0);
                    TEtiquetaMPLS etiquetaMPLSDeEmision = new TEtiquetaMPLS();
                    etiquetaMPLSDeEmision.ponerBoS(true);
                    etiquetaMPLSDeEmision.ponerEXP(0);
                    etiquetaMPLSDeEmision.ponerLabel(etiquetaDeEmision);
                    etiquetaMPLSDeEmision.ponerTTL(paquete.obtenerCabecera().obtenerTTL());
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(etiquetaMPLSDeEmision);
                    return paquete;
                } else {
                    TPDUMPLS paquete = new TPDUMPLS(gIdent.obtenerNuevo(), obtenerIP(), this.IPDestino, 0);
                    paquete.ponerSubtipo(TPDU.MPLS_GOS);
                    paquete.obtenerCabecera().obtenerCampoOpciones().ponerNivelGoS(valorGoS);
                    paquete.obtenerCabecera().obtenerCampoOpciones().ponerIDPaqueteGoS(this.gIdGoS.obtenerNuevo());
                    TEtiquetaMPLS etiquetaMPLSDeEmision = new TEtiquetaMPLS();
                    etiquetaMPLSDeEmision.ponerBoS(true);
                    etiquetaMPLSDeEmision.ponerEXP(0);
                    etiquetaMPLSDeEmision.ponerLabel(etiquetaDeEmision);
                    etiquetaMPLSDeEmision.ponerTTL(paquete.obtenerCabecera().obtenerTTL());
                    TEtiquetaMPLS etiquetaMPLS1 = new TEtiquetaMPLS();
                    etiquetaMPLS1.ponerBoS(false);
                    etiquetaMPLS1.ponerEXP(valorGoS);
                    etiquetaMPLS1.ponerLabel(1);
                    etiquetaMPLS1.ponerTTL(paquete.obtenerCabecera().obtenerTTL());
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(etiquetaMPLSDeEmision);
                    paquete.obtenerPilaEtiquetas().ponerEtiqueta(etiquetaMPLS1);
                    return paquete;
                }
            } else {
                if (valorGoS == TPDU.EXP_NIVEL0_SINLSP) {
                    TPDUIPv4 paquete = new TPDUIPv4(gIdent.obtenerNuevo(), obtenerIP(), this.IPDestino, 0);
                    return paquete;
                } else {
                    TPDUIPv4 paquete = new TPDUIPv4(gIdent.obtenerNuevo(), obtenerIP(), this.IPDestino, 0);
                    paquete.ponerSubtipo(TPDU.IPV4_GOS);
                    paquete.obtenerCabecera().obtenerCampoOpciones().ponerNivelGoS(valorGoS);
                    paquete.obtenerCabecera().obtenerCampoOpciones().ponerIDPaqueteGoS(this.gIdGoS.obtenerNuevo());
                    return paquete;
                }
            }
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace(); 
        }
        return null;
    }
    
    /**
     * Este método descarta un paquete de cualquier tipo. Además anota los datos
     * relativos en ese descarte en las estadísticas del nodo.
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
     * Este método investiga si al nodo le quedan puertos libres.
     * @return TRUE, si al nodo le quedan puertos libres. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean tienePuertosLibres() {
        return this.puertos.hayPuertosLibres();
    }
    
    /**
     * Este método permite acceder a los puertos del nodo directamente.
     * @return El conjunto de puertos del nodo.
     * @since 1.0
     */
    public TPuertosNodo obtenerPuertos() {
        return this.puertos;
    }
    
    /**
     * Este método devuelve el peso del nodo, que debe ser tenido en cuenta por el
     * algoritmo e encaminamiento para el cálculo de rutas.
     * @return En el nodo emisor, siempre es cero.
     * @since 1.0
     */
    public long obtenerPeso() {
        return 0;
    }
    
    /**
     * Este método devuelve si el nodo está bien configurado o no.
     * @return TRUE, si el nodo está bien configurado. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean estaBienConfigurado() {
        return this.bienConfigurado;
    }
    
    /**
     * Este método calcula si el nodo está bien configurado o no, actualizando el
     * atributo que indicará posteriormente este hecho.
     * @param t Topología dentro de la cual está incluido el nodo emisor.
     * @param recfg TRUE si se está reconfigurando el nodo emisor. FALSE si está configurando por
     * primera vez.
     * @return CORRECTA, si el nodo está bien configurado. Un codigo de error en caso contrario.
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
        
        if (this.obtenerDestino() == null)
            return this.SIN_DESTINO;
        if (this.obtenerDestino().equals(""))
            return this.SIN_DESTINO;
        this.ponerBienConfigurado(true);
        return this.CORRECTA;
    }
    
    /**
     * Este método transforma un codigo de error en un mensaje con similar significado,
     * pero legible por el usuario.
     * @param e Código de error cuyo mensaje se desea obtener.
     * @return El mensaje equivalente al codigo de error, pero legible.
     * @since 1.0
     */    
    public String obtenerMensajeError(int e) {
        switch (e) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEmisor.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEmisor.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoEmisor.NoSoloEspacios"));
            case SIN_DESTINO: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoEmisor.DestinoParaElTrafico"));
        }
        return ("");
    }
    
    /**
     * Este método transforma el nodo emisor en una cadena de caracterres que se puede
     * volcar a disco.
     * @return TRUE, si se ha realizado la resialización correctamente. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public String serializar() {
        String cadena = "#Emisor#";
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
        cadena += this.obtenerDestino();
        cadena += "#";
        cadena += this.obtenerLSPDeBackup();
        cadena += "#";
        cadena += this.obtenerNivelDeGoS();
        cadena += "#";
        cadena += this.obtenerSobreMPLS();
        cadena += "#";
        cadena += this.obtenerTasaTrafico();
        cadena += "#";
        cadena += this.obtenerTipoTrafico();
        cadena += "#";
        cadena += this.obtenerTamDatosConstante();
        cadena += "#";
        return cadena;
    }
    
    /**
     * Este método toma una cadena de texto que representa a un emisor serializado y
     * construye, en base a ella, el emisor en memoria sobre la instancia actual.
     * @param elemento El nodo emisor serializado.
     * @return TRUE, si se consigue deserializar correctamente. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean desSerializar(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 17) {
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
        this.IPDestino = valores[10];
        this.ponerLSPDeBackup(Boolean.valueOf(valores[11]).booleanValue());
        this.ponerNivelDeGoS(Integer.valueOf(valores[12]).intValue());
        this.ponerSobreMPLS(Boolean.valueOf(valores[13]).booleanValue());
        this.ponerTasaTrafico(Integer.valueOf(valores[14]).intValue());
        this.ponerTipoTrafico(Integer.valueOf(valores[15]).intValue());
        this.ponerTamDatosConstante(Integer.valueOf(valores[16]).intValue());
        return true;
    }
    
    /**
     * Este método reinicia los atributos de la clase como si acabasen de ser creador
     * por el constructor.
     * @since 1.0
     */    
    public void reset() {
        gIdent.reset();
        gIdGoS.reset();
        this.puertos.reset();
        this.estadisticas.reset();
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
        this.restaurarPasosSinEmitir();
    }
    
    /**
     * Este método permite acceder directamente a las estadísticas del nodo.
     * @return Las estadísticas del nodo.
     * @since 1.0
     */    
    public TEstadisticas accederAEstadisticas() {
        return estadisticas;
    }    
    
    /**
     * Este método inicia el conjunto de puertos del nodo, con el número de puertos
     * especificado.
     * @param num Número de puertos que tendrá el nodo. Como máximo está configurado para 8.
     * @since 1.0
     */    
    public synchronized void ponerPuertos(int num) {
        puertos = new TPuertosNodoNormal(num, this);
    }
    
	/**    
    * Este método no hace nada en un Emisor. En un nodo activo permitirá
    * solicitar a un nodo activo la retransmisión de un paquete.
    * @param paquete Paquete cuya retransmisión se está solicitando.
    * @param pSalida Puerto por el que se enviará la solicitud.
    * @since 1.0
    */    
    public void solicitarGPSRP(TPDUMPLS paquete, int pSalida) {
    }
    
    private String IPDestino;
    private int tasaTransferencia;
    private int tipoTrafico;
    private boolean encapsularSobreMPLS;
    private int nivelDeGoS;
    private boolean LSPDeBackup;
    
    private Random generadorDeAleatorios;
    private int etiquetaDeEmision;
    private TIdentificadorRotativo gIdGoS;
    private int tamDatosConstante;
    private int tamDatosVariable;

    private TIdentificadorLargo gIdent;
    
    /**
     * Este atributo almacenará las estadísticas del nodo.
     * @since 1.0
     */    
    public TEstadisticasEmisor estadisticas;
    
    /**
     * Esta constante identifica que el tráfico generado será constante.
     * @since 1.0
     */
    public static final int CONSTANTE = 0;
    /**
     * Esta constante identifica que el tráfico generado será variable.
     * @since 1.0
     */
    public static final int VARIABLE = 1;
    
    /**
     * Esta constante indica que la configuración del nodo es correcta.
     * @since 1.0
     */    
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que falta el nombre del nodo.
     * @since v
     */    
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que el nombre del nodo ya existe.
     * @since 1.0
     */    
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que el nombre del nodo está formado sólo por espacios.
     * @since 1.0
     */    
    public static final int SOLO_ESPACIOS = 3;
    /**
     * Esta constante indica que no ha seleccionado un destino para el tráfico generado
     * por el nodo.
     * @since 1.0
     */    
    public static final int SIN_DESTINO = 4;
    
}
