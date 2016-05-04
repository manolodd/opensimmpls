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

import simMPLS.protocols.TAbstractPDU;
import simMPLS.protocols.TMPLSLabel;
import simMPLS.protocols.TMPLSPDU;
import simMPLS.protocols.TIPv4PDU;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TFIFOPortSet;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TPortSet;
import simMPLS.utils.EIDGeneratorOverflow;
import simMPLS.utils.TLongIDGenerator;
import simMPLS.utils.TRotaryIDGenerator;
import java.awt.*;
import java.util.*;

/**
 * Esta clase implementa un nodo emisor de tr�fico.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSenderNode extends TNode implements ITimerEventListener, Runnable {
    
    /**
     * Crea una nueva instanci de TNodoEmisor
     * @param identificador Identificar �nico del nodo en la topolog�a.
     * @param d Direcci�n IP del nodo.
     * @param il Generador de identigficadores para los eventos generados por el nodo.
     * @param t Topolog�a dentro de la cual se encuentra el nodo.
     * @since 2.0
     */
    public TSenderNode(int identificador, String d, TLongIDGenerator il, TTopology t) {
        super(identificador, d, il, t);
        this.setPorts(super.NUM_PUERTOS_EMISOR);
        gIdent = new TLongIDGenerator();
        gIdGoS = new TRotaryIDGenerator();
        String IPDestino = "";
        tasaTransferencia = 10;
        tipoTrafico = TSenderNode.CONSTANTE;
        encapsularSobreMPLS = false;
        nivelDeGoS = 0;
        LSPDeBackup = false;
        generadorDeAleatorios = new Random();
        etiquetaDeEmision = (16 + generadorDeAleatorios.nextInt(1000000));
        tamDatosConstante = 0;
        tamDatosVariable = 0;
        estadisticas = new TSenderStats();
        estadisticas.activateStats(this.isGeneratingStats());
    }
    
    /**
     * Este m�todo obtiene el tamanio de la carga �til los paquetes de datos constantes
     * que debe generar el nodo si est� configurado para tr�fico constante.
     * @return El tama�o de la carga �til de los paquetes constantes.
     * @since 2.0
     */    
    public int obtenerTamDatosConstante() {
        return this.tamDatosConstante;
    }
    
    /**
     * Este m�todo permite establecer el tama�o de la carga util de los paquetes constantes que debe
     * generar el nodo.
     * @param tdc Tamanio de la carga util de los paquetes para tr�fico constante.
     * @since 2.0
     */    
    public void ponerTamDatosConstante(int tdc) {
        this.tamDatosConstante = tdc;
    }
    
    /**
     * Este m�todo permite establecer el nodo destino del tr�fico generado.
     * @param d IP del nodo destino del tr�fico.
     * @since 2.0
     */
    public void ponerDestino(String d) {
        if (!d.equals("")) {
            TNode nt = this.topology.setFirstNodeNamed(d);
            if (nt != null) {
                IPDestino = nt.getIPAddress();
            }
        }
    }
    
    /**
     * Este m�todo permite obtener la IP del nodo destino del tr�fico generado.
     * @return La IP del nodo destino del tr�fico generado.
     * @since 2.0
     */
    public String obtenerDestino() {
        return IPDestino;
    }
    
    /**
     * Este m�todo permite establecer la tasa de generaci�n de tr�fico del nodo.
     * @param t Tasa de generaci�n de tr�fico elegida para el nodo. En Mbps.
     * @since 2.0
     */
    public void ponerTasaTrafico(int t) {
        tasaTransferencia = t;
    }
    
    /**
     * Este m�todo permite obtener la tasa de generaci�n de tr�fico del nodo.
     * @return Tasa de generaci�n de tr�fico el nodo. En Mbps.
     * @since 2.0
     */
    public int obtenerTasaTrafico() {
        return tasaTransferencia;
    }
    
    private int obtenerCodificacionEXP() {
        if ((this.nivelDeGoS == 0) && (this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP;
        } else if ((this.nivelDeGoS == 1) && (this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP;
        } else if ((this.nivelDeGoS == 2) && (this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP;
        } else if ((this.nivelDeGoS == 3) && (this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP;
        } else if ((this.nivelDeGoS == 0) && (!this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP;
        } else if ((this.nivelDeGoS == 1) && (!this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP;
        } else if ((this.nivelDeGoS == 2) && (!this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP;
        } else if ((this.nivelDeGoS == 3) && (!this.LSPDeBackup)) {
            return TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP;
        }
        return TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP;
    }
    
    /**
     * Este m�todo permite establecer qu� tipo de tr�fico generar� el nodo.
     * @param t Tipo de tr�fico generado por el nodo. Una de las constantes definidas en esta
     * clase.
     * @since v
     */
    public void ponerTipoTrafico(int t) {
        tipoTrafico = t;
    }
    
    /**
     * Este m�todo permite obtener el tipo de tr�fico que est� generando el nodo.
     * @return Tipo de tr�fico generado por el nodo. Una de las constantes de esta clase.
     * @since 2.0
     */
    public int obtenerTipoTrafico() {
        return tipoTrafico;
    }
    
    /**
     * Este m�todo permite establecer si el tr�fico generado est� ya estiquetado en
     * MPLS o no. Lo que es lo mismo, si el tr�fico proviene de otro dominio MPLS o no.
     * @param mpls TRUE, si el nodo debe generar tr�fico MPLS. FALSE en caso contrario.
     * @since 2.0
     */
    public void ponerSobreMPLS(boolean mpls) {
        encapsularSobreMPLS = mpls;
    }
    
    /**
     * Este m�todo permite obtener si el tr�fico que est� generando el nodo es ya
     * tr�fico MPLS o no.
     * @return TRUE, si el tr�fico que est� generando el nodo es MPLS. FALSE en caso contrario.
     * @since 2.0
     */
    public boolean obtenerSobreMPLS() {
        return encapsularSobreMPLS;
    }
    
    /**
     * Este m�todo permite establecer el nivel de garant�a de servicio con el que el
     * nodo debe marcar los paquetes generados.
     * @param gos Nivel de garant�a de servicio.
     * @since 2.0
     */
    public void ponerNivelDeGoS(int gos) {
        nivelDeGoS = gos;
    }
    
    /**
     * Este m�todo permite obtener el nivel de garant�a de servicio con el que el nodo
     * est� marcando los paquetes generados.
     * @return El nivel de garant�a de servicio con el que el nodo est� marcando los paquetes
     * generados.
     * @since 2.0
     */
    public int obtenerNivelDeGoS() {
        return nivelDeGoS;
    }
    
    /**
     * Este m�todo permite establecer si los paquetes generados ser�n marcdos para
     * requerir un LSP de respaldo en el dominio MPLS o no.
     * @param l TRUE si los paqutes requerir�n LSP de respaldo. FALSE en caso contrario.
     * @since 2.0
     */
    public void ponerLSPDeBackup(boolean l) {
        LSPDeBackup = l;
    }
    
    /**
     * Este m�todo permite saber si los paquetes generados est�n siendo marcados para
     * requerir un LSP de respaldo o no.
     * @return TRUE, si los paquetes es�n siendo marcados para requerir un LSP de respaldo.
     * FALSE en caso contrario.
     * @since 2.0
     */
    public boolean obtenerLSPDeBackup() {
        return LSPDeBackup;
    }
    
    /**
     * Este m�todo permite obtener el tipo de nodo del que se trata esta instancia.
     * @return TNode.SENDER, indicando que es un generador y emisor de tr�fico.
     * @since 2.0
     */
    public int getNodeType() {
        return super.SENDER;
    }
    
    /**
     * Este m�todo permite recibir eventos de sincronizaci�n del reloj principal del
     * simulador, que es quien sincroniza todo.
     * @param evt Evento de sincronizaci�n enviado por el reloj principal.
     * @since 2.0
     */
    public void receiveTimerEvent(TTimerEvent evt) {
        this.setStepDuration(evt.getStepDuration());
        this.setTimeInstant(evt.getUpperLimit());
        this.availableNs += evt.getStepDuration();
        this.startOperation();
    }
    
    /**
     * Este m�todo se llama cuando el hilo independiente del nodo se pone en
     * funcionamiento. Es el n�cleo del nodo.
     * @since 2.0
     */
    public void run() {
        try {
            this.generateSimulationEvent(new TSENodeCongested(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), 0));
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        TAbstractPDU paqueteTmp = crearPaquete();
        boolean emito = false;
        while (obtenerOctetosTransmitibles() > obtenerTamanioSiguientePaquete(paqueteTmp)) {
            emito = true;
            generarTrafico();
        }
        paqueteTmp = null;
        if (emito) {
            this.resetStepsWithoutEmittingToZero();
        } else {
            this.increaseStepsWithoutEmitting();
        }
        this.estadisticas.consolidateData(this.getAvailableTime());
    }
    
    /**
     * Este m�todo obtiene el tama�o que tendr� la carga util del siguiente paquete
     * generado, independientemente de que se est� tratando con tr�fico constante o
     * variable.
     * @return El tama�o de la carga �til del siguiente paquete que generar� el emisor.
     * @since 2.0
     */    
    public int obtenerTamanioDatosSiguientePaquete() {
        if (this.tipoTrafico == TSenderNode.CONSTANTE) {
            return this.tamDatosConstante;
        }
        return this.tamDatosVariable;
    }
    
    /**
     * Este m�todo obtiene el tama�o de la header del sigueinte paquete que se
 generar�, independientemente del tipo de tr�fico del que se trate y de los
 valores de garant�a de Servicio con los que peuda estar marcado.
     * @param paquete Paquete de cuya header queremos conocer el tama�o.
     * @return El tama�o de la header.
     * @since 2.0
     */    
    public int obtenerTamanioCabeceraSiguientePaquete(TAbstractPDU paquete) {
        TMPLSPDU paqueteMPLS = null;
        TIPv4PDU paqueteIPv4 = null;
        if (paquete.getType() == TAbstractPDU.MPLS) {
            paqueteMPLS = (TMPLSPDU) paquete;
            return paqueteMPLS.getSize();
        } 
        paqueteIPv4 = (TIPv4PDU) paquete;
        return paqueteIPv4.getSize();
    }
    
    /**
     * Este m�todo calcula, el tama�o del siguiente paquete a generar,
     * independientemente de que se de tipo constante o variable, o de cualquier
     * protocolo de los soportados, e incluso de que nivel de GoS tenga asignado.
     * @param paquete paquete cuyo tamanio se desea calcular.
     * @return El tama�o total del paquete especificado por par�metros.
     * @since 2.0
     */    
    public int obtenerTamanioSiguientePaquete(TAbstractPDU paquete) {
        int tamanioDatos = 0;
        int tamanioCabecera = 0;
        int tamanioFinal = 0;
        tamanioDatos = obtenerTamanioDatosSiguientePaquete();
        tamanioCabecera = obtenerTamanioCabeceraSiguientePaquete(paquete);
        tamanioFinal = tamanioDatos + tamanioCabecera;
        return tamanioFinal;
    }
    
    /**
     * Este m�todo crea paquetes de tr�fico acorde a la configuraci�n el emisor de
     * tr�fico y los env�a al receptor destino del tr�fico.
     * @since 2.0
     */    
    public void generarTrafico() {
        TAbstractPDU paquete=null;
        TAbstractPDU paqueteConTamanio=null;
        TPort pt = ports.getPort(0);
        if (pt != null) {
            if (!pt.isAvailable()) {
                paquete = crearPaquete();
                paqueteConTamanio = this.ponerTamanio(paquete);
                if (paqueteConTamanio != null) {
                    try {
                        int tipo = 0;
                        if (paqueteConTamanio.getType() == TAbstractPDU.MPLS) {
                            TMPLSPDU paqueteMPLS = (TMPLSPDU) paqueteConTamanio;
                            tipo = paqueteMPLS.getSubtype();
                        } else if (paqueteConTamanio.getType() == TAbstractPDU.IPV4) {
                            TIPv4PDU paqueteIPv4 = (TIPv4PDU) paqueteConTamanio;
                            tipo = paqueteIPv4.getSubtype();
                        }
                        this.generateSimulationEvent(new TSEPacketGenerated(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), tipo, paqueteConTamanio.getSize()));
                        this.generateSimulationEvent(new TSEPacketSent(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), tipo));
                    } catch (Exception e) {
                        e.printStackTrace(); 
                    }
                    if (this.topology.obtenerIPSalto(this.getIPAddress(), this.obtenerDestino()) != null) {
                        pt.putPacketOnLink(paqueteConTamanio, pt.getLink().getTargetNodeIDOfTrafficSentBy(this));
                    } else {
                        discardPacket(paqueteConTamanio);
                    }
                } 
            }
        }
    }

    /**
     * Este m�todo contabiliza un paquete y su tama�o asociado, en las estad�sticas del
     * nodo emisor, y sus gr�ficas.
     * @param paquete Paquete que se desea contabilizar.
     * @param deEntrada TRUE indica que se trata de un paquete que ha entrado en el nodo. FALSE indica
     * que se trata de un paquete que ha salido del nodo.
     * @since 2.0
     */    
    public void contabilizarPaquete(TAbstractPDU paquete, boolean deEntrada) {
        if (deEntrada) {
            if (paquete.getSubtype() == TAbstractPDU.MPLS) {
            } else if (paquete.getSubtype() == TAbstractPDU.MPLS_GOS) {
            } else if (paquete.getSubtype() == TAbstractPDU.IPV4) {
            } else if (paquete.getSubtype() == TAbstractPDU.IPV4_GOS) {
            }
        } else {
            if (paquete.getSubtype() == TAbstractPDU.MPLS) {
            } else if (paquete.getSubtype() == TAbstractPDU.MPLS_GOS) {
            } else if (paquete.getSubtype() == TAbstractPDU.IPV4) {
            } else if (paquete.getSubtype() == TAbstractPDU.IPV4_GOS) {
            }
        }
    }
    
    /**
     * Este m�todo calcula cu�ntos nanosegundos necesita el nodo emisor para generar y
     * transmitir un bit. Se basa para ello en la tasa de generaci�n de tr�fico del
     * nodo.
     * @return El n�mero de nanosegundos necesarios generar y transmitir un bit.
     * @since 2.0
     */    
    public double obtenerNsPorBit() {
        long tasaEnBitsPorSegundo = (long) (this.tasaTransferencia*1048576L);
        double nsPorCadaBit = (double) ((double)1000000000.0/(long)tasaEnBitsPorSegundo);
        return nsPorCadaBit;
    }
    
    /**
     * Este m�todo calcula el n�mero de nanosegundos necesarios para poder generar y
     * enviar un determinado n�mero de octetos.
     * @param octetos N�mero de octetos que deseamos generar y transmitir.
     * @return N�mero de nanosegundos que necesitamos para los octetos especificados.
     * @since 2.0
     */    
    public double obtenerNsUsadosTotalOctetos(int octetos) {
        double nsPorCadaBit = obtenerNsPorBit();
        long bitsOctetos = (long) ((long)octetos*(long)8);
        return (double)((double)nsPorCadaBit*(long)bitsOctetos);
    }
    
    /**
     * Este m�todo calcula el n�mero de bits que puede generar y transmitir con el
     * n�mero de nanosegundos con los que cuenta.
     * @return N�mero de bits que puede generar y transmitir.
     * @since 2.0
     */    
    public int obtenerLimiteBitsTransmitibles() {
        double nsPorCadaBit = obtenerNsPorBit();
        double maximoBits = (double) ((double)availableNs/(double)nsPorCadaBit);
        return (int) maximoBits;
    }
    
    /**
     * Este metodo calcula el n�mero de octetos completos que puede generar y
     * transmitir el emisor teniendo en cuenta el n�mero de nanosegundos con los que
     * cuenta.
     * @return El n�mero de octetos completos que se pueden generar y transmitir.
     * @since 2.0
     */    
    public int obtenerOctetosTransmitibles() {
        double maximoBytes = ((double)obtenerLimiteBitsTransmitibles()/(double)8.0);
        return (int) maximoBytes;
    }
    
    /**
     * Este m�todo calcula autom�ticamente el tama�o de la carga util del siguiente
     * paquete a generar. Si el tr�fico es constante, devolver� el tama�o de paquete
     * con el que se configur� el emisor. Si el tama�o es variable, generar� tr�fico
     * siguiendo una funci�n de probabilidad en la cual se sigue la siguiente
     * distribuci�n de tama�os:
     *
     * Tama�oPaquete < 100 octetos ---------------------> 47%
     * Tama�oPaquete >= 100 octetos y < 1400 octetos ---> 24%
     * Tama�oPaquete >= 1400 octetos y < 1500 octetos --> 18%
     * Tama�oPaquete >= 1500 octetos -------------------> 1%
     *
     * Esta distribuci�n est� extra�da de las estad�sticas de la red Abilene, que son
     * p�blicas y se pueden observar en http://netflow.internet2.edu/weekly.
     * @return El tama�o que debe tener el siguiente paquete.
     * @since 2.0
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
     * Este m�todo toma como par�metro un paquete vacio y devuelve un paquete con datos
     * insertados. Los datos ser�n del tama�o que se haya estimado en los distintos
     * m�todos de la clase,pero ser� el correcto.
     * @param paquete Paquete al que se quiere a�adir datos.
     * @return Paquete con datos insertados del tama�o correcto seg�n el tipo de gr�fico.
     * @since 2.0
     */    
    public TAbstractPDU ponerTamanio(TAbstractPDU paquete) {
        TMPLSPDU paqueteMPLS = null;
        TIPv4PDU paqueteIPv4 = null;
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
            if (paquete.getType() == TAbstractPDU.MPLS) {
                paqueteMPLS = (TMPLSPDU) paquete;
                paqueteMPLS.getTCPPayload().setSize((int) tamanioDatos);
                nsUsados = this.obtenerNsUsadosTotalOctetos(tamanioTotal);
                this.availableNs -= nsUsados;
                if (this.availableNs < 0)
                    this.availableNs = 0;
                if (this.tipoTrafico == this.VARIABLE) {
                    this.tamDatosVariable = this.generarTamanioSiguientePaquete();
                }
                return paqueteMPLS;
            } else if (paquete.getType() == TAbstractPDU.IPV4) {
                paqueteIPv4 = (TIPv4PDU) paquete;
                paqueteIPv4.getTCPPayload().setSize(tamanioDatos);
                nsUsados = this.obtenerNsUsadosTotalOctetos(tamanioTotal);
                this.availableNs -= nsUsados;
                if (this.availableNs < 0)
                    this.availableNs = 0;
                if (this.tipoTrafico == this.VARIABLE) {
                    this.tamDatosVariable = this.generarTamanioSiguientePaquete();
                }
                return paqueteIPv4;
            }
        }
        return null;
    }
    
    /**
     * Este m�todo devuelve un paquete vac�o (sin datos) del tipo correcto para el que
     * esta configurado el nodo emisor.
     * @return El paquete creado.
     * @since 2.0
     */    
    public TAbstractPDU crearPaquete() {
        int valorGoS = this.obtenerCodificacionEXP();
        try {
            if (this.encapsularSobreMPLS) {
                if (valorGoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    TMPLSPDU paquete = new TMPLSPDU(gIdent.getNextID(), getIPAddress(), this.IPDestino, 0);
                    TMPLSLabel etiquetaMPLSDeEmision = new TMPLSLabel();
                    etiquetaMPLSDeEmision.setBoS(true);
                    etiquetaMPLSDeEmision.setEXP(0);
                    etiquetaMPLSDeEmision.setLabel(etiquetaDeEmision);
                    etiquetaMPLSDeEmision.setTTL(paquete.getIPv4Header().getTTL());
                    paquete.getLabelStack().pushTop(etiquetaMPLSDeEmision);
                    return paquete;
                } else {
                    TMPLSPDU paquete = new TMPLSPDU(gIdent.getNextID(), getIPAddress(), this.IPDestino, 0);
                    paquete.setSubtype(TAbstractPDU.MPLS_GOS);
                    paquete.getIPv4Header().getOptionsField().setRequestedGoSLevel(valorGoS);
                    paquete.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(this.gIdGoS.getNextID());
                    TMPLSLabel etiquetaMPLSDeEmision = new TMPLSLabel();
                    etiquetaMPLSDeEmision.setBoS(true);
                    etiquetaMPLSDeEmision.setEXP(0);
                    etiquetaMPLSDeEmision.setLabel(etiquetaDeEmision);
                    etiquetaMPLSDeEmision.setTTL(paquete.getIPv4Header().getTTL());
                    TMPLSLabel etiquetaMPLS1 = new TMPLSLabel();
                    etiquetaMPLS1.setBoS(false);
                    etiquetaMPLS1.setEXP(valorGoS);
                    etiquetaMPLS1.setLabel(1);
                    etiquetaMPLS1.setTTL(paquete.getIPv4Header().getTTL());
                    paquete.getLabelStack().pushTop(etiquetaMPLSDeEmision);
                    paquete.getLabelStack().pushTop(etiquetaMPLS1);
                    return paquete;
                }
            } else {
                if (valorGoS == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
                    TIPv4PDU paquete = new TIPv4PDU(gIdent.getNextID(), getIPAddress(), this.IPDestino, 0);
                    return paquete;
                } else {
                    TIPv4PDU paquete = new TIPv4PDU(gIdent.getNextID(), getIPAddress(), this.IPDestino, 0);
                    paquete.setSubtype(TAbstractPDU.IPV4_GOS);
                    paquete.getIPv4Header().getOptionsField().setRequestedGoSLevel(valorGoS);
                    paquete.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(this.gIdGoS.getNextID());
                    return paquete;
                }
            }
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace(); 
        }
        return null;
    }
    
    /**
     * Este m�todo descarta un paquete de cualquier tipo. Adem�s anota los datos
     * relativos en ese descarte en las estad�sticas del nodo.
     * @param paquete Paquete que se quiere descartar.
     * @since 2.0
     */    
    public void discardPacket(TAbstractPDU paquete) {
        try {
            this.generateSimulationEvent(new TSEPacketDiscarded(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
            this.estadisticas.addStatsEntry(paquete, TStats.DISCARD);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        paquete = null;
    }
    
    /**
     * Este m�todo investiga si al nodo le quedan ports libres.
     * @return TRUE, si al nodo le quedan ports libres. FALSE en caso contrario.
     * @since 2.0
     */
    public boolean hasAvailablePorts() {
        return this.ports.hasAvailablePorts();
    }
    
    /**
     * Este m�todo permite acceder a los ports del nodo directamente.
     * @return El conjunto de ports del nodo.
     * @since 2.0
     */
    public TPortSet getPorts() {
        return this.ports;
    }
    
    /**
     * Este m�todo devuelve el peso del nodo, que debe ser tenido en cuenta por el
     * algoritmo e encaminamiento para el c�lculo de rutas.
     * @return En el nodo emisor, siempre es cero.
     * @since 2.0
     */
    public long getRoutingWeight() {
        return 0;
    }
    
    /**
     * Este m�todo devuelve si el nodo est� bien configurado o no.
     * @return TRUE, si el nodo est� bien configurado. FALSE en caso contrario.
     * @since 2.0
     */    
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }
    
    /**
     * Este m�todo calcula si el nodo est� bien configurado o no, actualizando el
     * atributo que indicar� posteriormente este hecho.
     * @param t Topolog�a dentro de la cual est� incluido el nodo emisor.
     * @param recfg TRUE si se est� reconfigurando el nodo emisor. FALSE si est� configurando por
     * primera vez.
     * @return CORRECTA, si el nodo est� bien configurado. Un codigo de error en caso contrario.
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
        
        if (this.obtenerDestino() == null)
            return this.SIN_DESTINO;
        if (this.obtenerDestino().equals(""))
            return this.SIN_DESTINO;
        this.setWellConfigured(true);
        return this.CORRECTA;
    }
    
    /**
     * Este m�todo transforma un codigo de error en un mensaje con similar significado,
     * pero legible por el usuario.
     * @param e C�digo de error cuyo mensaje se desea obtener.
     * @return El mensaje equivalente al codigo de error, pero legible.
     * @since 2.0
     */    
    public String getErrorMessage(int e) {
        switch (e) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEmisor.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEmisor.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoEmisor.NoSoloEspacios"));
            case SIN_DESTINO: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoEmisor.DestinoParaElTrafico"));
        }
        return ("");
    }
    
    /**
     * Este m�todo transforma el nodo emisor en una cadena de caracterres que se puede
     * volcar a disco.
     * @return TRUE, si se ha realizado la resializaci�n correctamente. FALSE en caso
     * contrario.
     * @since 2.0
     */    
    public String marshall() {
        String cadena = "#Emisor#";
        cadena += this.getID();
        cadena += "#";
        cadena += this.getName().replace('#', ' ');
        cadena += "#";
        cadena += this.getIPAddress();
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
     * Este m�todo toma una cadena de texto que representa a un emisor serializado y
     * construye, en base a ella, el emisor en memoria sobre la instancia actual.
     * @param elemento El nodo emisor serializado.
     * @return TRUE, si se consigue deserializar correctamente. FALSE en caso contrario.
     * @since 2.0
     */    
    public boolean unMarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 17) {
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
     * Este m�todo reinicia los atributos de la clase como si acabasen de ser creador
     * por el constructor.
     * @since 2.0
     */    
    public void reset() {
        gIdent.reset();
        gIdGoS.reset();
        this.ports.reset();
        this.estadisticas.reset();
        estadisticas.activateStats(this.isGeneratingStats());
        this.resetStepsWithoutEmittingToZero();
    }
    
    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
     * @return Las estad�sticas del nodo.
     * @since 2.0
     */    
    public TStats getStats() {
        return estadisticas;
    }    
    
    /**
     * Este m�todo inicia el conjunto de ports del nodo, con el n�mero de ports
 especificado.
     * @param num N�mero de ports que tendr� el nodo. Como m�ximo est� configurado para 8.
     * @since 2.0
     */    
    public synchronized void setPorts(int num) {
        ports = new TFIFOPortSet(num, this);
    }
    
	/**    
    * Este m�todo no hace nada en un Emisor. En un nodo activo permitir�
    * solicitar a un nodo activo la retransmisi�n de un paquete.
    * @param paquete Paquete cuya retransmisi�n se est� solicitando.
    * @param pSalida Puerto por el que se enviar� la solicitud.
    * @since 2.0
    */    
    public void runGoSPDUStoreAndRetransmitProtocol(TMPLSPDU paquete, int pSalida) {
    }
    
    private String IPDestino;
    private int tasaTransferencia;
    private int tipoTrafico;
    private boolean encapsularSobreMPLS;
    private int nivelDeGoS;
    private boolean LSPDeBackup;
    
    private Random generadorDeAleatorios;
    private int etiquetaDeEmision;
    private TRotaryIDGenerator gIdGoS;
    private int tamDatosConstante;
    private int tamDatosVariable;

    private TLongIDGenerator gIdent;
    
    /**
     * Este atributo almacenar� las estad�sticas del nodo.
     * @since 2.0
     */    
    public TSenderStats estadisticas;
    
    /**
     * Esta constante identifica que el tr�fico generado ser� constante.
     * @since 2.0
     */
    public static final int CONSTANTE = 0;
    /**
     * Esta constante identifica que el tr�fico generado ser� variable.
     * @since 2.0
     */
    public static final int VARIABLE = 1;
    
    /**
     * Esta constante indica que la configuraci�n del nodo es correcta.
     * @since 2.0
     */    
    public static final int CORRECTA = 0;
    /**
     * Esta constante indica que falta el nombre del nodo.
     * @since v
     */    
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante indica que el nombre del nodo ya existe.
     * @since 2.0
     */    
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante indica que el nombre del nodo est� formado s�lo por espacios.
     * @since 2.0
     */    
    public static final int SOLO_ESPACIOS = 3;
    /**
     * Esta constante indica que no ha seleccionado un destino para el tr�fico generado
     * por el nodo.
     * @since 2.0
     */    
    public static final int SIN_DESTINO = 4;
    
}
