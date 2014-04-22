//**************************************************************************
// Nombre......: TReceiverNode.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa un nodo receptor de la topolog�a.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.scenario;

import simMPLS.protocols.TPDU;
import simMPLS.protocols.TPDUMPLS;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.hardware.ports.TNormalNodePorts;
import simMPLS.hardware.ports.TPort;
import simMPLS.hardware.ports.TNodePorts;
import simMPLS.utils.TIdentificadorLargo;
import java.awt.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase implementa un nodo receptor de tr�fico.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TReceiverNode extends TTopologyNode implements ITimerEventListener, Runnable {

    /**
     * Crea una nueva instancia de TNodoReceptor
     * @param identificador Identificador unico para el nodo en la topolog�a.
     * @param d Direcci�n IP del nodo.
     * @param il Generador de identificadores para los eventos que tenga que genrar el nodo.
     * @param t Topologia dentro de la cual se encuentra el nodo.
     * @since 1.0
     */
    public TReceiverNode(int identificador, String d, TIdentificadorLargo il, TTopology t) {
        super(identificador, d, il, t);
        this.ponerPuertos(super.NUM_PUERTOS_RECEPTOR);
        this.puertos.ponerBufferIlimitado(true);
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
     * @return TTopologyNode.RECEPTOR, indicando que se trata de un nodo receptor.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return super.RECEPTOR;
    }

    /**
     * Este m�todo permite obtener eventos enviados desde el reloj del simulador.
     * @param evt Evento enviado desde el reloj principal del simulador.
     * @since 1.0
     */    
    public void capturarEventoReloj(TTimerEvent evt) {
        this.ponerDuracionTic(evt.obtenerDuracionTic());
        this.ponerInstanteDeTiempo(evt.obtenerLimiteSuperior());
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
        estadisticas.asentarDatos(this.obtenerInstanteDeTiempo());
        // Acciones a llevar a cabo durante el tic.
    }

    /**
     * Este m�todo lee mientras puede los paquetes que hay en el buffer de recepci�n.
     * @since 1.0
     */    
    public void recibirDatos() {
        TPort p = this.puertos.obtenerPuerto(0);
        long idEvt = 0;
        int tipo = 0;
        TPDU paquete = null;
        TSEPacketReceived evt = null;
        if (p != null) {
            while (p.hayPaqueteEsperando()) {
                paquete = p.obtenerPaquete();
                try {
                    idEvt = this.gILargo.obtenerNuevo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.contabilizarPaquete(paquete, true);
                evt = new TSEPacketReceived(this, idEvt, this.obtenerInstanteDeTiempo(), tipo, paquete.obtenerTamanio());
                this.suscriptorSimulacion.capturarEventoSimulacion(evt);
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
     * Este m�too permite acceder directamente a los puertos del nodo.
     * @return El conjunto de puertos del nodo.
     * @since 1.0
     */    
    public TNodePorts obtenerPuertos() {
        return this.puertos;
    }

    /**
     * Este m�todo devuelve si el nodo tiene puertos libres o no.
     * @return TRUE, si el nodo tiene puertos libres. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean tienePuertosLibres() {
        return this.puertos.hayPuertosLibres();
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
            TTopologyNode tp = t.obtenerPrimerNodoLlamado(this.obtenerNombre());
            if (tp != null)
                return this.NOMBRE_YA_EXISTE;
        } else {
            TTopologyNode tp = t.obtenerPrimerNodoLlamado(this.obtenerNombre());
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
    public String serializar() {
        String cadena = "#Receptor#";
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
        return cadena;
    }
    
    /**
     * Este m�todo deserializa un nodo serializado pasado por par�metro,
     * reconstruyendolo en memoria en la instancia actual.
     * @param elemento Nodo receptor serializado.
     * @return TRUE, si se ha podido deserializar correctamente. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean desSerializar(String elemento) {
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
    public TStats accederAEstadisticas() {
        return this.estadisticas;
    }
    
    /**
     * Este m�todo permite establecer el n�mero de puertos que deseamos para el nodo.
     * @param num El n�mero de puertos del nodo. Como mucho 8.
     * @since 1.0
     */    
    public synchronized void ponerPuertos(int num) {
        puertos = new TNormalNodePorts(num, this);
    }
    
    /**
     * Este m�todo permite descartar nu paquete en el nodo y reflejar este descarte en
     * las estadisticas.
     * @param paquete paquete que deseamos descartar.
     * @since 1.0
     */    
    public void descartarPaquete(TPDU paquete) {
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
    public void solicitarGPSRP(TPDUMPLS paquete, int pSalida) {
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
