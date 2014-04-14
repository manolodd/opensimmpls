//**************************************************************************
// Nombre......: TNodoReceptor.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un nodo receptor de la topología.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import java.awt.*;
import simMPLS.escenario.*;
import simMPLS.electronica.reloj.*;
import simMPLS.electronica.puertos.*;
import simMPLS.protocolo.*;
import simMPLS.utiles.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase implementa un nodo receptor de tráfico.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TNodoReceptor extends TNodoTopologia implements IEventoRelojListener, Runnable {

    /**
     * Crea una nueva instancia de TNodoReceptor
     * @param identificador Identificador unico para el nodo en la topología.
     * @param d Dirección IP del nodo.
     * @param il Generador de identificadores para los eventos que tenga que genrar el nodo.
     * @param t Topologia dentro de la cual se encuentra el nodo.
     * @since 1.0
     */
    public TNodoReceptor(int identificador, String d, TIdentificadorLargo il, TTopologia t) {
        super(identificador, d, il, t);
        this.ponerPuertos(super.NUM_PUERTOS_RECEPTOR);
        this.puertos.ponerBufferIlimitado(true);
        this.estadisticas = new TEstadisticasReceptor();
    }

    /**
     * Este método reinicia los atributos de la clase y los deja como recien creadops
     * por el constructor.
     * @since 1.0
     */    
    public void reset() {
        this.puertos.reset();
        this.estadisticas.reset();
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
    }
    
    /**
     * Este método devuelve el tipo del nodo.
     * @return TNodoTopologia.RECEPTOR, indicando que se trata de un nodo receptor.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return super.RECEPTOR;
    }

    /**
     * Este método permite obtener eventos enviados desde el reloj del simulador.
     * @param evt Evento enviado desde el reloj principal del simulador.
     * @since 1.0
     */    
    public void capturarEventoReloj(TEventoReloj evt) {
        this.ponerDuracionTic(evt.obtenerDuracionTic());
        this.ponerInstanteDeTiempo(evt.obtenerLimiteSuperior());
        this.iniciar();
    }

    /**
     * Este método se llama automáticamente cuando el hilo independiente del nodo se
     * pone en funcionamiento. En él se codifica toda la funcionalidad del nodo.
     * @since 1.0
     */    
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        recibirDatos();
        estadisticas.asentarDatos(this.obtenerInstanteDeTiempo());
        // Acciones a llevar a cabo durante el tic.
    }

    /**
     * Este método lee mientras puede los paquetes que hay en el buffer de recepción.
     * @since 1.0
     */    
    public void recibirDatos() {
        TPuerto p = this.puertos.obtenerPuerto(0);
        long idEvt = 0;
        int tipo = 0;
        TPDU paquete = null;
        TESPaqueteRecibido evt = null;
        if (p != null) {
            while (p.hayPaqueteEsperando()) {
                paquete = p.obtenerPaquete();
                try {
                    idEvt = this.gILargo.obtenerNuevo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.contabilizarPaquete(paquete, true);
                evt = new TESPaqueteRecibido(this, idEvt, this.obtenerInstanteDeTiempo(), tipo, paquete.obtenerTamanio());
                this.suscriptorSimulacion.capturarEventoSimulacion(evt);
                paquete = null;
            }
        }
    }

    /**
     * Este método contabiliza en las estadísticas del nodo un paquete leído.
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
     * Este métoo permite acceder directamente a los puertos del nodo.
     * @return El conjunto de puertos del nodo.
     * @since 1.0
     */    
    public TPuertosNodo obtenerPuertos() {
        return this.puertos;
    }

    /**
     * Este método devuelve si el nodo tiene puertos libres o no.
     * @return TRUE, si el nodo tiene puertos libres. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean tienePuertosLibres() {
        return this.puertos.hayPuertosLibres();
    }

    /**
     * Este método devuelve el peso del nodo que debe ser tenido en cuenta por los
     * algoritmos de encaminamiento para el cálculo de rutas.
     * @return En el caso de un nodo receptor, siempre es cero.
     * @since 1.0
     */    
    public long obtenerPeso() {
        return 0;
    }

    /**
     * Devuelve si el nodo está bien configurado o no.
     * @return TRUE, si el nodo esta bien configurado. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean estaBienConfigurado() {
        return this.bienConfigurado;
    }
    
    /**
     * Este método comprueba la configuración del nodo y devuelve un código expresando
     * dicho estado.
     * @param t Topología donde está el nodo.
     * @param recfg TRUE, si se está reconfigurando el nodo. FALSE si el nodo se está configurando
     * por primera vez.
     * @return CORRECTA, si el nodo está bien configurado. Un codigo de error en caso
     * contrario.
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
        estadisticas.activarEstadisticas(this.obtenerEstadisticas());
        return this.CORRECTA;
    }
    
    /**
     * Este método transforma el codigo de error de configuración del nodo en un texto
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
     * Este método transforma el nodo en una representación textual volcable a disco.
     * @return La representación textual del nodo.
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
     * Este método deserializa un nodo serializado pasado por parámetro,
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
     * Este método permite acceder directamente a las estadísticas del nodo.
     * @return Las estadísticas del nodo.
     * @since 1.0
     */    
    public TEstadisticas accederAEstadisticas() {
        return this.estadisticas;
    }
    
    /**
     * Este método permite establecer el número de puertos que deseamos para el nodo.
     * @param num El número de puertos del nodo. Como mucho 8.
     * @since 1.0
     */    
    public synchronized void ponerPuertos(int num) {
        puertos = new TPuertosNodoNormal(num, this);
    }
    
    /**
     * Este método permite descartar nu paquete en el nodo y reflejar este descarte en
     * las estadisticas.
     * @param paquete paquete que deseamos descartar.
     * @since 1.0
     */    
    public void descartarPaquete(TPDU paquete) {
        // Un receptor no descarta paquetes, porque tiene un buffer 
        // ilimitado y no analiza el tráfico. Lo recibe y ya está.
        paquete = null;
    }

    /**    
    * Este método no hace nada en un Receptor. En un nodo activo permitirá
    * solicitar a un nodo activo la retransmisión de un paquete.
    * @param paquete Paquete cuya retransmisión se está solicitando.
    * @param pSalida Puerto por el que se enviará la solicitud.
    * @since 1.0
    */    
    public void solicitarGPSRP(TPDUMPLS paquete, int pSalida) {
    }
    
    /**
     * Esta constante identifica que la configuración del nodo es correcta.
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
     * Esta constante identifica que el nombre del nodo está formado sólo por espacios.
     * @since 1.0
     */    
    public static final int SOLO_ESPACIOS = 3;
    
    private TEstadisticasReceptor estadisticas;
}
