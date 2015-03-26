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

import simMPLS.protocols.TAbstractPDU;
import simMPLS.protocols.TMPLSPDU;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TFIFOPortSet;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TPortSet;
import simMPLS.utils.TLongIDGenerator;
import java.awt.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase implementa un nodo receptor de tr�fico.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TReceiverNode extends TNode implements ITimerEventListener, Runnable {

    /**
     * Crea una nueva instancia de TNodoReceptor
     * @param identificador Identificador unico para el nodo en la topolog�a.
     * @param d Direcci�n IP del nodo.
     * @param il Generador de identificadores para los eventos que tenga que genrar el nodo.
     * @param t Topologia dentro de la cual se encuentra el nodo.
     * @since 1.0
     */
    public TReceiverNode(int identificador, String d, TLongIDGenerator il, TTopology t) {
        super(identificador, d, il, t);
        this.setPorts(super.NUM_PUERTOS_RECEPTOR);
        this.puertos.setUnlimitedBuffer(true);
        this.estadisticas = new TReceiverStats();
    }

    /**
     * Este m�todo reinicia los atributos de la clase y los deja como recien creadops
     * por el constructor.
     * @since 1.0
     */    
    public void reset() {
        this.puertos.reset();
        this.estadisticas.reset();
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
    }
    
    /**
     * Este m�todo devuelve el tipo del nodo.
     * @return TNode.RECEIVER, indicando que se trata de un nodo receptor.
     * @since 1.0
     */    
    public int getNodeType() {
        return super.RECEIVER;
    }

    /**
     * Este m�todo permite obtener eventos enviados desde el reloj del simulador.
     * @param evt Evento enviado desde el reloj principal del simulador.
     * @since 1.0
     */    
    public void receiveTimerEvent(TTimerEvent evt) {
        this.ponerDuracionTic(evt.getStepDuration());
        this.ponerInstanteDeTiempo(evt.getUpperLimit());
        this.iniciar();
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el hilo independiente del nodo se
     * pone en funcionamiento. En �l se codifica toda la funcionalidad del nodo.
     * @since 1.0
     */    
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        recibirDatos();
        estadisticas.asentarDatos(this.getAvailableTime());
        // Acciones a llevar a cabo durante el tic.
    }

    /**
     * Este m�todo lee mientras puede los paquetes que hay en el buffer de recepci�n.
     * @since 1.0
     */    
    public void recibirDatos() {
        TPort p = this.puertos.getPort(0);
        long idEvt = 0;
        int tipo = 0;
        TAbstractPDU paquete = null;
        TSEPacketReceived evt = null;
        if (p != null) {
            while (p.thereIsAPacketWaiting()) {
                paquete = p.getPacket();
                try {
                    idEvt = this.longIdentifierGenerator.getNextID();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.contabilizarPaquete(paquete, true);
                evt = new TSEPacketReceived(this, idEvt, this.getAvailableTime(), tipo, paquete.getSize());
                this.simulationEventsListener.captureSimulationEvents(evt);
                paquete = null;
            }
        }
    }

    /**
     * Este m�todo contabiliza en las estad�sticas del nodo un paquete le�do.
     * @param paquete Paquete que se quiere contabilizar.
     * @param deEntrada TRUE, si el paquete entra en el nodo. FALSE si el paquete sale del nodo.
     * @since 1.0
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
     * Este m�too permite acceder directamente a los puertos del nodo.
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
     * Este m�todo devuelve el peso del nodo que debe ser tenido en cuenta por los
     * algoritmos de encaminamiento para el c�lculo de rutas.
     * @return En el caso de un nodo receptor, siempre es cero.
     * @since 1.0
     */    
    public long obtenerPeso() {
        return 0;
    }

    /**
     * Devuelve si el nodo est� bien configurado o no.
     * @return TRUE, si el nodo esta bien configurado. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean estaBienConfigurado() {
        return this.bienConfigurado;
    }
    
    /**
     * Este m�todo comprueba la configuraci�n del nodo y devuelve un c�digo expresando
     * dicho estado.
     * @param t Topolog�a donde est� el nodo.
     * @param recfg TRUE, si se est� reconfigurando el nodo. FALSE si el nodo se est� configurando
     * por primera vez.
     * @return CORRECTA, si el nodo est� bien configurado. Un codigo de error en caso
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
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
        return this.CORRECTA;
    }
    
    /**
     * Este m�todo transforma el codigo de error de configuraci�n del nodo en un texto
     * entendible y explicativo.
     * @param e Codigo de error.
     * @return Texto explicativo del codigo de error.
     * @since 1.0
     */    
    public String obtenerMensajeError(int e) {
        switch (e) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigReceptor.FALTA_NOMBRE"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigReceptor.NOMBRE_REPETIDO"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TNodoReceptor.NombreNoSoloEspacios"));
        }
        return ("");
    }

    /**
     * Este m�todo transforma el nodo en una representaci�n textual volcable a disco.
     * @return La representaci�n textual del nodo.
     * @since 1.0
     */    
    public String marshall() {
        String cadena = "#Receptor#";
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
        return cadena;
    }
    
    /**
     * Este m�todo deserializa un nodo serializado pasado por par�metro,
     * reconstruyendolo en memoria en la instancia actual.
     * @param elemento Nodo receptor serializado.
     * @return TRUE, si se ha podido deserializar correctamente. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean unmarshall(String elemento) {
        String valores[] = elemento.split("#");
        if (valores.length != 10) {
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
        return true;
    }
    
    /**
     * Este m�todo permite acceder directamente a las estad�sticas del nodo.
     * @return Las estad�sticas del nodo.
     * @since 1.0
     */    
    public TStats getStats() {
        return this.estadisticas;
    }
    
    /**
     * Este m�todo permite establecer el n�mero de puertos que deseamos para el nodo.
     * @param num El n�mero de puertos del nodo. Como mucho 8.
     * @since 1.0
     */    
    public synchronized void setPorts(int num) {
        puertos = new TFIFOPortSet(num, this);
    }
    
    /**
     * Este m�todo permite descartar nu paquete en el nodo y reflejar este descarte en
     * las estadisticas.
     * @param paquete paquete que deseamos descartar.
     * @since 1.0
     */    
    public void discardPacket(TAbstractPDU paquete) {
        // Un receptor no descarta paquetes, porque tiene un buffer 
        // ilimitado y no analiza el tr�fico. Lo recibe y ya est�.
        paquete = null;
    }

    /**    
    * Este m�todo no hace nada en un Receptor. En un nodo activo permitir�
    * solicitar a un nodo activo la retransmisi�n de un paquete.
    * @param paquete Paquete cuya retransmisi�n se est� solicitando.
    * @param pSalida Puerto por el que se enviar� la solicitud.
    * @since 1.0
    */    
    public void runGoSPDUStoreAndRetransmitProtocol(TMPLSPDU paquete, int pSalida) {
    }
    
    /**
     * Esta constante identifica que la configuraci�n del nodo es correcta.
     * @since 1.0
     */    
    public static final int CORRECTA = 0;
    /**
     * Esta constante identifica que el nodo no tiene nombre.
     * @since 1.0
     */    
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante identifica que el nombre del nodo ya existe con anterioridad.
     * @since 1.0
     */    
    public static final int NOMBRE_YA_EXISTE = 2;
    /**
     * Esta constante identifica que el nombre del nodo est� formado s�lo por espacios.
     * @since 1.0
     */    
    public static final int SOLO_ESPACIOS = 3;
    
    private TReceiverStats estadisticas;
}
