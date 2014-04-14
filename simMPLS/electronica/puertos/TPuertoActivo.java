//**************************************************************************
// Nombre......: TPuerto.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un puerto de comunicaciones de un no-
// ............: do de la topologia.
// Fecha.......: 12/03/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.puertos;

import java.util.*;
import simMPLS.protocolo.*;
import simMPLS.escenario.*;
import simMPLS.utiles.*;
import simMPLS.electronica.puertos.*;

/**
 * Esta clase implementa un puerto de entrada/salida válido para cualquiera de
 * los nodos del simulador que sean activos. El puerto es activo.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPuertoActivo extends TPuerto {

    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de
     * TPuertoActivo.
     * @since 1.0
     * @param idp Identificador (número) del puerto.
     * @param cpn Referencia al superconjunto de puertos del que este puerto forma parte.
     */
    public TPuertoActivo(TPuertosNodo cpn, int idp) {
        super(cpn, idp);
        paqueteDevuelto = null;
        bufferIlimitado = false;
        ordenLlegada = new TIdentificadorRotativo();
        cerrojoPrioridad0 = new TMonitor();
        cerrojoPrioridad1 = new TMonitor();
        cerrojoPrioridad2 = new TMonitor();
        cerrojoPrioridad3 = new TMonitor();
        cerrojoPrioridad4 = new TMonitor();
        cerrojoPrioridad5 = new TMonitor();
        cerrojoPrioridad6 = new TMonitor();
        cerrojoPrioridad7 = new TMonitor();
        cerrojoPrioridad8 = new TMonitor();
        cerrojoPrioridad9 = new TMonitor();
        cerrojoPrioridad10 = new TMonitor();

        bufferPrioridad0 = new TreeSet();
        bufferPrioridad1 = new TreeSet();
        bufferPrioridad2 = new TreeSet();
        bufferPrioridad3 = new TreeSet();
        bufferPrioridad4 = new TreeSet();
        bufferPrioridad5 = new TreeSet();
        bufferPrioridad6 = new TreeSet();
        bufferPrioridad7 = new TreeSet();
        bufferPrioridad8 = new TreeSet();
        bufferPrioridad9 = new TreeSet();
        bufferPrioridad10 = new TreeSet();
        
        bufferSeleccionado = 0;
        siguientePaqueteALeer = null;
        ratioPorBuffer = new int[11];
        actualPorBuffer = new int[11];
        int i;
        for (i=0; i<11; i++) {
            ratioPorBuffer[i] = i+1;
            actualPorBuffer[i] = 0;
        }
    }
    
    private void obtenerSiguientePaquete() {
        boolean fin = false;
        int bufferesSinPaquetes = 0;
        int bufferesCompletos = 0;
        Iterator ite = null;
        TEntradaPuertoActivo epa = null;
        if (siguientePaqueteALeer == null) {
            while ((!fin) && (bufferesSinPaquetes < 12)) {
                if (bufferSeleccionado == 10) {
                    if (bufferPrioridad10.size() > 0) {
                        if (actualPorBuffer[10] < ratioPorBuffer[10]) {
                            this.cerrojoPrioridad10.bloquear();
                            ite = bufferPrioridad10.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad10.liberar();
                            actualPorBuffer[10]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[10] = ratioPorBuffer[10];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 9) {
                    if (bufferPrioridad9.size() > 0) {
                        if (actualPorBuffer[9] < ratioPorBuffer[9]) {
                            this.cerrojoPrioridad9.bloquear();
                            ite = bufferPrioridad9.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad9.liberar();
                            actualPorBuffer[9]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[9] = ratioPorBuffer[9];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 8) {
                    if (bufferPrioridad8.size() > 0) {
                        if (actualPorBuffer[8] < ratioPorBuffer[8]) {
                            this.cerrojoPrioridad8.bloquear();
                            ite = bufferPrioridad8.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad8.liberar();
                            actualPorBuffer[8]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[8] = ratioPorBuffer[8];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 7) {
                    if (bufferPrioridad7.size() > 0) {
                        if (actualPorBuffer[7] < ratioPorBuffer[7]) {
                            this.cerrojoPrioridad7.bloquear();
                            ite = bufferPrioridad7.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad7.liberar();
                            actualPorBuffer[7]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[7] = ratioPorBuffer[7];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 6) {
                    if (bufferPrioridad6.size() > 0) {
                        if (actualPorBuffer[6] < ratioPorBuffer[6]) {
                            this.cerrojoPrioridad6.bloquear();
                            ite = bufferPrioridad6.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad6.liberar();
                            actualPorBuffer[6]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[6] = ratioPorBuffer[6];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 5) {
                    if (bufferPrioridad5.size() > 0) {
                        if (actualPorBuffer[5] < ratioPorBuffer[5]) {
                            this.cerrojoPrioridad5.bloquear();
                            ite = bufferPrioridad5.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad5.liberar();
                            actualPorBuffer[5]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[5] = ratioPorBuffer[5];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 4) {
                    if (bufferPrioridad4.size() > 0) {
                        if (actualPorBuffer[4] < ratioPorBuffer[4]) {
                            this.cerrojoPrioridad4.bloquear();
                            ite = bufferPrioridad4.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad4.liberar();
                            actualPorBuffer[4]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[4] = ratioPorBuffer[4];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 3) {
                    if (bufferPrioridad3.size() > 0) {
                        if (actualPorBuffer[3] < ratioPorBuffer[3]) {
                            this.cerrojoPrioridad3.bloquear();
                            ite = bufferPrioridad3.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad3.liberar();
                            actualPorBuffer[3]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[3] = ratioPorBuffer[3];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 2) {
                    if (bufferPrioridad2.size() > 0) {
                        if (actualPorBuffer[2] < ratioPorBuffer[2]) {
                            this.cerrojoPrioridad2.bloquear();
                            ite = bufferPrioridad2.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad2.liberar();
                            actualPorBuffer[2]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[2] = ratioPorBuffer[2];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 1) {
                    if (bufferPrioridad1.size() > 0) {
                        if (actualPorBuffer[1] < ratioPorBuffer[1]) {
                            this.cerrojoPrioridad1.bloquear();
                            ite = bufferPrioridad1.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad1.liberar();
                            actualPorBuffer[1]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[1] = ratioPorBuffer[1];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                } else if (bufferSeleccionado == 0) {
                    if (bufferPrioridad0.size() > 0) {
                        if (actualPorBuffer[0] < ratioPorBuffer[0]) {
                            this.cerrojoPrioridad0.bloquear();
                            ite = bufferPrioridad0.iterator();
                            if (ite.hasNext()) {
                                epa = (TEntradaPuertoActivo) ite.next();
                                siguientePaqueteALeer = epa.obtenerPaquete();
                                ite.remove();
                            }
                            this.cerrojoPrioridad0.liberar();
                            actualPorBuffer[0]++;
                            fin = true;
                        } else {
                            bufferesCompletos++;
                        }
                    } else {
                        actualPorBuffer[0] = ratioPorBuffer[0];
                        bufferesCompletos++;
                        bufferesSinPaquetes++;
                    }
                }
                bufferSeleccionado = ((bufferSeleccionado+1) % 11);
                if (bufferesCompletos >= 11) {
                    int i;
                    for (i=0; i<11; i++) {
                        actualPorBuffer[i] = 0;
                    }
                }
            }
        }
    }
    
    /**
     * Este método selecciona el siguiente calcula el siguiente paquete que se leerá del
     * puerto y devuelve su prioridad que depende directamente del tipo de tráfico y la
     * marca de GoS que lleve.
     * @since 1.0
     * @return -1, si no hay paquete que leer. un valor de 0 a 10 (inclusives) dependiendo de
     * la prioridad del paquete.
     */    
    public int obtenerPrioridadSiguientePaquete() {
        int p;
        cerrojo.bloquear();
        obtenerSiguientePaquete();
        if (this.siguientePaqueteALeer != null) {
            p = this.calcularPrioridadPaquete(this.siguientePaqueteALeer);
            cerrojo.liberar();
            return p;
        }
        cerrojo.liberar();
        return -1;
    }
    
    /**
     * Este método permite saltarse las limitaciones de tamaño del buffer y establecer
     * éste como un buffer ideal, con capacidad infinita.
     * @param bi TRUE indica que el buffer se tomará como ilimitado. FALSE indica que el buffer
     * tendrá el tamaño especificado en el resto de métodos.
     * @since 1.0
     */    
    public void ponerBufferIlimitado(boolean bi) {
        this.bufferIlimitado = bi;
    }

    /**
     * Este método deshecha el paquete pasado por parametro.
     * @param paquete El paquete que se desea descartar.
     * @since 1.0
     */    
    public void descartarPaquete(TPDU paquete) {
        this.obtenerCjtoPuertos().obtenerNodo().descartarPaquete(paquete);
    }
    
    /**
     * Este método deposita en el buffer del puerto un paquete.
     * @param paquete Paquete que se desea depositar en el buffer del puerto.
     * @since 1.0
     */    
    public void ponerPaquete(TPDU paquete) {
        TPuertosNodoActivo cjtoPuertosAux = (TPuertosNodoActivo) cjtoPuertos;
        cjtoPuertosAux.cerrojoCjtoPuertos.bloquear();
        cerrojo.bloquear();
        
        TNodoTopologia nt = this.cjtoPuertos.obtenerNodo();
        long idEvt = 0;
        int idOrden = 0;
        int prior = this.calcularPrioridadPaquete(paquete);
        try {
            idEvt = nt.gILargo.obtenerNuevo();
            idOrden = this.ordenLlegada.obtenerNuevo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int tipo = paquete.obtenerSubTipo();
        if (this.bufferIlimitado) {
            TEntradaPuertoActivo epa = new TEntradaPuertoActivo(prior, idOrden, paquete);
            insertarEntradaPriorizada(epa);
            cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
            TESPaqueteRecibido evt = new TESPaqueteRecibido(nt, idEvt, this.obtenerCjtoPuertos().obtenerNodo().obtenerInstanteDeTiempo(), tipo, paquete.obtenerTamanio());
            nt.suscriptorSimulacion.capturarEventoSimulacion(evt);
            if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(paquete, TEstadisticas.ENTRADA);
            }
        } else {
            if (!protocoloEPCD(paquete)) {
                this.descartarPaquete(paquete);
            }
        }
        cerrojo.liberar();
        cjtoPuertosAux.cerrojoCjtoPuertos.liberar();
    }
    
    /**
     * Este método es la implementación del protocolo EPCD (Early Packet Catch and
     * Discard), que mantiene siempre un umbral fijo libre en el buffer del puerto que
     * le permita capturar la cabecera de un paquete antes de descartarlo e informar al
     * protocolo GPSRP si se trata de un paquete GoS, para que proceda a solicitar su
     * retransmisión.
     * @param paquete Paquete que se desea insertar en el buffer y al que se le desea aplicar EPD
     * antes.
     * @return TRUE, si el paquete se inserta correctamente en el buffer. FALSE, en caso de que
     * se descarte el paquete.
     */    
    public boolean protocoloEPCD(TPDU paquete) {
        TPuertosNodoActivo cjtoPuertosAux = (TPuertosNodoActivo) cjtoPuertos;
        long idEvt = 0;
        int idOrden = 0;
        int prior = this.calcularPrioridadPaquete(paquete);
        TNodoTopologia nt = this.cjtoPuertos.obtenerNodo();
        try {
            idEvt = nt.gILargo.obtenerNuevo();
            idOrden = this.ordenLlegada.obtenerNuevo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int tipo = paquete.obtenerSubTipo();
        if ((cjtoPuertosAux.obtenerTamanioOcupadoCjtoPuertos() + paquete.obtenerTamanio()) <= ((cjtoPuertosAux.obtenerTamanioBuffer()*1024*1024) - UMBRAL)) {
                TEntradaPuertoActivo epa = new TEntradaPuertoActivo(prior, idOrden, paquete);
                insertarEntradaPriorizada(epa);
                cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
                TESPaqueteRecibido evt = new TESPaqueteRecibido(nt, idEvt, this.obtenerCjtoPuertos().obtenerNodo().obtenerInstanteDeTiempo(), tipo, paquete.obtenerTamanio());
                nt.suscriptorSimulacion.capturarEventoSimulacion(evt);
                if (this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas() != null) {
                    this.obtenerCjtoPuertos().obtenerNodo().accederAEstadisticas().crearEstadistica(paquete, TEstadisticas.ENTRADA);
                }
                return true;
        } else {
            if (paquete.obtenerSubTipo() == TPDU.MPLS_GOS) {
                cjtoPuertosAux.obtenerNodo().solicitarGPSRP((TPDUMPLS) paquete, this.idPuerto);
            }
        }
        return false;
    }
    
    private void insertarEntradaPriorizada(TEntradaPuertoActivo ep) {
        int prioridadAux = ep.obtenerPrioridad();
        if (prioridadAux == this.PRIORIDAD_10) {
            this.cerrojoPrioridad10.bloquear();
            this.bufferPrioridad10.add(ep);
            this.cerrojoPrioridad10.liberar();
        } else if (prioridadAux == this.PRIORIDAD_9) {
            this.cerrojoPrioridad9.bloquear();
            this.bufferPrioridad9.add(ep);
            this.cerrojoPrioridad9.liberar();
        } else if (prioridadAux == this.PRIORIDAD_8) {
            this.cerrojoPrioridad8.bloquear();
            this.bufferPrioridad8.add(ep);
            this.cerrojoPrioridad8.liberar();
        } else if (prioridadAux == this.PRIORIDAD_7) {
            this.cerrojoPrioridad7.bloquear();
            this.bufferPrioridad7.add(ep);
            this.cerrojoPrioridad7.liberar();
        } else if (prioridadAux == this.PRIORIDAD_6) {
            this.cerrojoPrioridad6.bloquear();
            this.bufferPrioridad6.add(ep);
            this.cerrojoPrioridad6.liberar();
        } else if (prioridadAux == this.PRIORIDAD_5) {
            this.cerrojoPrioridad5.bloquear();
            this.bufferPrioridad5.add(ep);
            this.cerrojoPrioridad5.liberar();
        } else if (prioridadAux == this.PRIORIDAD_4) {
            this.cerrojoPrioridad4.bloquear();
            this.bufferPrioridad4.add(ep);
            this.cerrojoPrioridad4.liberar();
        } else if (prioridadAux == this.PRIORIDAD_3) {
            this.cerrojoPrioridad3.bloquear();
            this.bufferPrioridad3.add(ep);
            this.cerrojoPrioridad3.liberar();
        } else if (prioridadAux == this.PRIORIDAD_2) {
            this.cerrojoPrioridad2.bloquear();
            this.bufferPrioridad2.add(ep);
            this.cerrojoPrioridad2.liberar();
        } else if (prioridadAux == this.PRIORIDAD_1) {
            this.cerrojoPrioridad1.bloquear();
            this.bufferPrioridad1.add(ep);
            this.cerrojoPrioridad1.liberar();
        } else if (prioridadAux == this.SIN_PRIORIDAD) {
            this.cerrojoPrioridad0.bloquear();
            this.bufferPrioridad0.add(ep);
            this.cerrojoPrioridad0.liberar();
        }
    }
    
    private int calcularPrioridadPaquete(TPDU p) {
        if (p.obtenerTipo() == TPDU.TLDP) {
            return this.PRIORIDAD_10;
        }
        if (p.obtenerTipo() == TPDU.GPSRP) {
            return this.PRIORIDAD_9;
        }
        if (p.obtenerTipo() == TPDU.RLPRP) {
            return this.PRIORIDAD_8;
        }
        if (p.obtenerTipo() == TPDU.MPLS) {
            TPDUMPLS pMPLS = (TPDUMPLS) p;
            if (pMPLS.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerLabel() == 1) {
                int EXP = pMPLS.obtenerPilaEtiquetas().obtenerEtiqueta().obtenerEXP();
                if (EXP == TPDU.EXP_NIVEL3_CONLSP) {
                    return this.PRIORIDAD_7;
                }
                if (EXP == TPDU.EXP_NIVEL3_SINLSP) {
                    return this.PRIORIDAD_6;
                }
                if (EXP == TPDU.EXP_NIVEL2_CONLSP) {
                    return this.PRIORIDAD_5;
                }
                if (EXP == TPDU.EXP_NIVEL2_SINLSP) {
                    return this.PRIORIDAD_4;
                }
                if (EXP == TPDU.EXP_NIVEL1_CONLSP) {
                    return this.PRIORIDAD_3;
                }
                if (EXP == TPDU.EXP_NIVEL1_SINLSP) {
                    return this.PRIORIDAD_2;
                }
                if (EXP == TPDU.EXP_NIVEL0_CONLSP) {
                    return this.PRIORIDAD_1;
                }
                if (EXP == TPDU.EXP_NIVEL0_SINLSP) {
                    return this.SIN_PRIORIDAD;
                }
            } else {
                return this.SIN_PRIORIDAD;
            }
        }
        if (p.obtenerTipo() == TPDU.IPV4) {
            if (p.obtenerCabecera().obtenerCampoOpciones().estaUsado()) {
                int GoS = p.obtenerCabecera().obtenerCampoOpciones().obtenerNivelGoS();
                if (GoS == TPDU.EXP_NIVEL3_CONLSP) {
                    return this.PRIORIDAD_7;
                }
                if (GoS == TPDU.EXP_NIVEL3_SINLSP) {
                    return this.PRIORIDAD_6;
                }
                if (GoS == TPDU.EXP_NIVEL2_CONLSP) {
                    return this.PRIORIDAD_5;
                }
                if (GoS == TPDU.EXP_NIVEL2_SINLSP) {
                    return this.PRIORIDAD_4;
                }
                if (GoS == TPDU.EXP_NIVEL1_CONLSP) {
                    return this.PRIORIDAD_3;
                }
                if (GoS == TPDU.EXP_NIVEL1_SINLSP) {
                    return this.PRIORIDAD_2;
                }
                if (GoS == TPDU.EXP_NIVEL0_CONLSP) {
                    return this.PRIORIDAD_1;
                }
                if (GoS == TPDU.EXP_NIVEL0_SINLSP) {
                    return this.SIN_PRIORIDAD;
                }
            } else {
                return this.SIN_PRIORIDAD;
            }
        }
        return this.SIN_PRIORIDAD;
    }

    /**
     * Este método inserta un paquete en el buffer de recepción del puerto. Es igual
     * que el método ponerPaquete(), salvo que no genera eventos y lo hace
     * silenciosamente.
     * @param paquete El paquete que queremos que reciba el puerto.
     * @since 1.0
     */    
    public void reencolar(TPDU paquete) {
        TPuertosNodoActivo cjtoPuertosAux = (TPuertosNodoActivo) cjtoPuertos;
        cjtoPuertosAux.cerrojoCjtoPuertos.bloquear();
        cerrojo.bloquear();
        TNodoTopologia nt = this.cjtoPuertos.obtenerNodo();
        long idEvt = 0;
        int idOrden = 0;
        int prior = this.calcularPrioridadPaquete(paquete);
        try {
            idEvt = nt.gILargo.obtenerNuevo();
            idOrden = this.ordenLlegada.obtenerNuevo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tipo = paquete.obtenerSubTipo();
        if (this.bufferIlimitado) {
            TEntradaPuertoActivo epa = new TEntradaPuertoActivo(prior, idOrden, paquete);
            insertarEntradaPriorizada(epa);
            cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
        } else {
            if ((cjtoPuertosAux.obtenerTamanioOcupadoCjtoPuertos() + paquete.obtenerTamanio()) <= (cjtoPuertosAux.obtenerTamanioBuffer()*1024*1024)) {
                TEntradaPuertoActivo epa = new TEntradaPuertoActivo(prior, idOrden, paquete);
                insertarEntradaPriorizada(epa);
                cjtoPuertosAux.incrementarTamanioOcupadoCjtoPuertos(paquete.obtenerTamanio());
            } else {
                this.descartarPaquete(paquete);                
            }
        }
        cerrojo.liberar();
        cjtoPuertosAux.cerrojoCjtoPuertos.liberar();
    }
    
    /**
     * este método lee un paquete del buffer de recepción del puerto. El paquete leido
     * dependerá del algoritmo de gestión de los búfferes que implemente el puerto. Por
     * defecto, es un FIFO con prioridad por tipo de paquetes.
     * @return El paquete leído.
     * @since 1.0
     */    
    public TPDU obtenerPaquete() {
        TPuertosNodoActivo cjtoPuertosAux = (TPuertosNodoActivo) cjtoPuertos;
        cjtoPuertosAux.cerrojoCjtoPuertos.bloquear();
        cerrojo.bloquear();
        obtenerSiguientePaquete();
        if (siguientePaqueteALeer != null) {
            paqueteDevuelto = siguientePaqueteALeer;
            if (!this.bufferIlimitado) {
                cjtoPuertosAux.decrementarTamanioOcupadoCjtoPuertos(paqueteDevuelto.obtenerTamanio());
            }
            siguientePaqueteALeer = null;
        }
        cerrojo.liberar();
        cjtoPuertosAux.cerrojoCjtoPuertos.liberar();
        return paqueteDevuelto;
    }
    
    /**
     * Este método calcula si podemos conmutar el siguiente paquete del nodo, dado el
     * número de octetos que como mucho podemos conmutar en un momento dado.
     * @param octetos El número de octetos que podemos conmutar.
     * @return TRUE, si podemos conmutar el siguiente paquete. FALSE, en caso contrario.
     * @since 1.0
     */    
    public boolean puedoConmutarPaquete(int octetos) {
        cerrojo.bloquear();
        obtenerSiguientePaquete();
        if (siguientePaqueteALeer != null) {
            paqueteDevuelto = siguientePaqueteALeer;
            cerrojo.liberar();
            if (paqueteDevuelto.obtenerTamanio() <= octetos) {
                return true;
            }
        }
        cerrojo.liberar();
        return false;
    }

    /**
     * Este método obtiene la congestión total el puerto, en porcentaje.
     * @return El porcentaje de ocupación del puerto.
     * @since 1.0
     */    
    public long obtenerCongestion() {
        if (this.bufferIlimitado) {
            return 0;
        } 
        TPuertosNodoActivo tpn = (TPuertosNodoActivo) cjtoPuertos;
        long cong = (tpn.obtenerTamanioOcupadoCjtoPuertos()*100) / (tpn.obtenerTamanioBuffer()*1024*1024);
        return cong;
    }

    /**
     * Este método comprueba si hay paquetes esperando en el buffer de recepción o no.
     * @return TRUE, si hay paquetes en el buffer de recepción. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean hayPaqueteEsperando() {
        if (bufferPrioridad10.size() > 0)
            return true;
        if (bufferPrioridad9.size() > 0)
            return true;
        if (bufferPrioridad8.size() > 0)
            return true;
        if (bufferPrioridad7.size() > 0)
            return true;
        if (bufferPrioridad6.size() > 0)
            return true;
        if (bufferPrioridad5.size() > 0)
            return true;
        if (bufferPrioridad4.size() > 0)
            return true;
        if (bufferPrioridad3.size() > 0)
            return true;
        if (bufferPrioridad2.size() > 0)
            return true;
        if (bufferPrioridad1.size() > 0)
            return true;
        if (bufferPrioridad0.size() > 0)
            return true;
        if (siguientePaqueteALeer != null)
            return true;
        return false;
    }

    /**
     * Este método calcula el total de octetos que suman los paquetes que actualmente
     * hay en el buffer de recepción del puerto.
     * @return El tamaño en octetos del total de paquetes en el buffer de recepción.
     * @since 1.0
     */    
    public long obtenerOcupacion() {
        if (this.bufferIlimitado) {
            this.cerrojo.bloquear();
            int ocup=0;
            TPDU paquete = null;
            TEntradaPuertoActivo epa = null;

            this.cerrojoPrioridad10.bloquear();
            Iterator it = this.bufferPrioridad10.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad10.liberar();

            this.cerrojoPrioridad9.bloquear();
            it = this.bufferPrioridad9.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad9.liberar();

            this.cerrojoPrioridad8.bloquear();
            it = this.bufferPrioridad8.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad8.liberar();

            this.cerrojoPrioridad7.bloquear();
            it = this.bufferPrioridad7.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad7.liberar();

            this.cerrojoPrioridad6.bloquear();
            it = this.bufferPrioridad6.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad6.liberar();

            this.cerrojoPrioridad5.bloquear();
            it = this.bufferPrioridad5.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad5.liberar();

            this.cerrojoPrioridad4.bloquear();
            it = this.bufferPrioridad4.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad4.liberar();

            this.cerrojoPrioridad3.bloquear();
            it = this.bufferPrioridad3.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad3.liberar();

            this.cerrojoPrioridad2.bloquear();
            it = this.bufferPrioridad2.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad2.liberar();

            this.cerrojoPrioridad1.bloquear();
            it = this.bufferPrioridad1.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad1.liberar();

            this.cerrojoPrioridad0.bloquear();
            it = this.bufferPrioridad0.iterator();
            while (it.hasNext()) {
                epa = (TEntradaPuertoActivo) it.next();
                paquete = epa.obtenerPaquete();
                if (paquete != null)
                    ocup += paquete.obtenerTamanio();
            }
            this.cerrojoPrioridad0.liberar();

            if (siguientePaqueteALeer != null)
                ocup += siguientePaqueteALeer.obtenerTamanio();
            this.cerrojo.liberar();
            return ocup;
        }
        TPuertosNodoActivo tpn = (TPuertosNodoActivo) cjtoPuertos;
        return tpn.obtenerTamanioOcupadoCjtoPuertos();
    }

    /**
     * Este método calcula el número de paquetes total que hay en el buffer del puerto.
     * @return El número total de paquetes que hay en el puerto.
     * @since 1.0
     */    
    public int obtenerNumeroPaquetes() {
        int numP = 0;
        numP += bufferPrioridad10.size();
        numP += bufferPrioridad9.size();
        numP += bufferPrioridad8.size();
        numP += bufferPrioridad7.size();
        numP += bufferPrioridad6.size();
        numP += bufferPrioridad5.size();
        numP += bufferPrioridad4.size();
        numP += bufferPrioridad3.size();
        numP += bufferPrioridad2.size();
        numP += bufferPrioridad1.size();
        numP += bufferPrioridad0.size();
        if (siguientePaqueteALeer != null)
            numP++;
        return numP;
    }

    /**
     * Este método reinicia los atributos de la clase como si acabasen de ser creados
     * por el constructor.
     * @since 1.0
     */    
    public void reset() {
        this.cerrojo.bloquear();
        
        this.cerrojoPrioridad10.bloquear();
        Iterator it = this.bufferPrioridad10.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad10.liberar();
        
        this.cerrojoPrioridad9.bloquear();
        it = this.bufferPrioridad9.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad9.liberar();

        this.cerrojoPrioridad8.bloquear();
        it = this.bufferPrioridad8.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad8.liberar();

        this.cerrojoPrioridad7.bloquear();
        it = this.bufferPrioridad7.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad7.liberar();

        this.cerrojoPrioridad6.bloquear();
        it = this.bufferPrioridad6.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad6.liberar();

        this.cerrojoPrioridad5.bloquear();
        it = this.bufferPrioridad5.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad5.liberar();

        this.cerrojoPrioridad4.bloquear();
        it = this.bufferPrioridad4.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad4.liberar();

        this.cerrojoPrioridad3.bloquear();
        it = this.bufferPrioridad3.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad3.liberar();

        this.cerrojoPrioridad2.bloquear();
        it = this.bufferPrioridad2.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad2.liberar();

        this.cerrojoPrioridad1.bloquear();
        it = this.bufferPrioridad1.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad1.liberar();

        this.cerrojoPrioridad0.bloquear();
        it = this.bufferPrioridad0.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoPrioridad0.liberar();

        this.cerrojo.liberar();
        paqueteDevuelto = null;
        bufferSeleccionado = 0;
        siguientePaqueteALeer = null;
        int i;
        for (i=0; i<11; i++) {
            actualPorBuffer[i] = 0;
        }
    }
    
    private static final int PRIORIDAD_10  = 10;
    private static final int PRIORIDAD_9   = 9;
    private static final int PRIORIDAD_8   = 8;
    private static final int PRIORIDAD_7   = 7;
    private static final int PRIORIDAD_6   = 6;
    private static final int PRIORIDAD_5   = 5;
    private static final int PRIORIDAD_4   = 4;
    private static final int PRIORIDAD_3   = 3;
    private static final int PRIORIDAD_2   = 2;
    private static final int PRIORIDAD_1   = 1;
    private static final int SIN_PRIORIDAD = 0;

    private static final int UMBRAL = 100;
    
    private TMonitor cerrojoPrioridad0;
    private TMonitor cerrojoPrioridad1;
    private TMonitor cerrojoPrioridad2;
    private TMonitor cerrojoPrioridad3;
    private TMonitor cerrojoPrioridad4;
    private TMonitor cerrojoPrioridad5;
    private TMonitor cerrojoPrioridad6;
    private TMonitor cerrojoPrioridad7;
    private TMonitor cerrojoPrioridad8;
    private TMonitor cerrojoPrioridad9;
    private TMonitor cerrojoPrioridad10;
    private TreeSet bufferPrioridad0;
    private TreeSet bufferPrioridad1;
    private TreeSet bufferPrioridad2;
    private TreeSet bufferPrioridad3;
    private TreeSet bufferPrioridad4;
    private TreeSet bufferPrioridad5;
    private TreeSet bufferPrioridad6;
    private TreeSet bufferPrioridad7;
    private TreeSet bufferPrioridad8;
    private TreeSet bufferPrioridad9;
    private TreeSet bufferPrioridad10;

    private int bufferSeleccionado;
    private TPDU paqueteDevuelto;
    private boolean bufferIlimitado;
    private TIdentificadorRotativo ordenLlegada;
    private int ratioPorBuffer[];
    private int actualPorBuffer[];
    private TPDU siguientePaqueteALeer;
}
