//**************************************************************************
// Nombre......: TNodoTopologia.java
// Proyecto....: Open SimMPLS
// Descripción.: Superclase que implementa un nodo de la topología.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import simMPLS.electronica.reloj.*;
import simMPLS.electronica.puertos.*;
import simMPLS.protocolo.*;
import java.awt.*;
import simMPLS.escenario.*;
import simMPLS.utiles.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta es una superclase abstracta de la cual deben heredar todos los nodos
 * axistentes en el simulador.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TNodoTopologia extends TElementoTopologia implements Comparable, IEventoRelojListener, Runnable {

    /**
     * Crea una nueva instancia de TNodoTopologia.
     * @since 1.0
     * @param identificador Identificador unico para el nodo en la topologia.
     * @param d Dirección IP del nodo.
     * @param il Generador de identificadores para los eventos que deba emitir el nodo.
     * @param t Topologia donde se encuentra el nodo incluído.
     */
    public TNodoTopologia(int identificador, String d, TIdentificadorLargo il, TTopologia t) {
        super(TElementoTopologia.NODO, il);
        posicion = new Point(0,0);
        id = identificador;
        nombre = "";
        estado = DESELECCIONADO;
        mostrarNombre = false;
        IP = d;
        puertos = null;
        cerrojo = new TMonitor();
        topologia = t;
        generarEstadisticas = false;
        nsDisponibles = 0;
        nsDelTic = 0;
        pasosSinEmitir = 0;
    }

    /**
     * Este método permite establecer las estadísticas del nodo.
     * @param e Estadísticas para el nodo.
     * @since 1.0
     */    
    public void ponerEstadisticas(boolean e) {
        generarEstadisticas = e;
    }

    /**
     * Este método permite acceder directamente a las estadísticas del nodo.
     * @return las estadísticas del nodo.
     * @since 1.0
     */    
    public boolean obtenerEstadisticas() {
        return generarEstadisticas;
    }


    /**
     * Este método permite comparar para ordenar, este nodo con cualquier otro.
     * @param o Nodo con el que se va a comparar.
     * @return -1, 0 o 1, dependiendo de si ordinalmente el nodo actual es menor, igual o
     * mayor que el pasado por parámetros.
     * @since 1.0
     */    
    public int compareTo(Object o) {
        TNodoTopologia n = (TNodoTopologia) o;
        if (obtenerIdentificador() < n.obtenerIdentificador())
            return -1;
        else if (obtenerIdentificador() == n.obtenerIdentificador())
            return 0;
        return 1;
    }

    /**
     * Eeste método permite establecer la topologia donde se encuentra el nodo.
     * @since 1.0
     * @param t Topología donde se encuentra el nodo.
     */    
    public void ponerTopologia(TTopologia t) {
        topologia = t;
    }

    /**
     * Este método permite obtener la topologia donde se encuentra el nodo.
     * @return Topología donde se encuentra el nodo.
     * @since 1.0
     */    
    public TTopologia obtenerTopologia() {
        return topologia;
    }

    /**
     * Este método permite obtener el nombre del nodo.
     * @return Nombre del nodo.
     * @since 1.0
     */    
    public String obtenerNombre() {
        return nombre;
    }

    /**
     * Este método permite establecer el nombre del nodo.
     * @since 1.0
     * @param n nombre deseado para el nodo.
     */    
    public void ponerNombre(String n) {
        nombre = n;
    }

    /**
     * Este método permite obtener el identificador del nodo.
     * @return El identificador unico del nodo.
     * @since 1.0
     */    
    public int obtenerIdentificador() {
        return id;
    }

    /**
     * Este método permite establecer el identificador del nodo.
     * @param identificador El identificador unico del nodo.
     * @since 1.0
     */    
    public void ponerIdentificador(int identificador) {
        id = identificador;
    }

    /**
     * Este método permite obtener la posición del panel de simulación donde se
     * encuentra el nodo.
     * @return Las coordenadas del panel de simulación donde se encuentra el nodo.
     * @since 1.0
     */    
    public Point obtenerPosicion() {
        return posicion;
    }

    /**
     * Este métopdo permite establecer las coordenadas del panel de simulación donde se
     * mostrará el nodo.
     * @param p Las coordenadas del panel de simulación elegidas para el nodo.
     * @since 1.0
     */    
    public void ponerPosicion(Point p) {
        posicion.x = p.x - (ANCHO_ICONOS/2);
        posicion.y = p.y - (ALTO_ICONOS/2);
    }

    /**
     * Este método permite obtener si el nodo está en una posición dada o no.
     * @param p Coordenadas donde queremos saber si está el nodo o no.
     * @return TRUE, si el nodo está en ess coordenadas. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean estaEnPosicion(Point p) {
        if ((p.x >= posicion.x) && (p.x <= (posicion.x+ANCHO_ICONOS)) &&
            (p.y >= posicion.y) && (p.y <= (posicion.y+ALTO_ICONOS))) 
                return true;
        return false;
    }

    /**
     * Este método permite obtener si el nodo está seleccionado o no.
     * @return SELECCIONADO, si el nodo está seleccionado. DESELECCIONADO en caso contrario.
     * @since 1.0
     */    
    public int obtenerEstado() {
        return estado;
    }

    /**
     * Este método permite seleccionar o deseleccionar el nodo.
     * @param est SELECCIONADO, si queremos que el nodo esté seleccionado. DESELECCIONADO en caso
     * contrario.
     * @since 1.0
     */    
    public void ponerEstado(int est) {
        estado = est;
    }

    /**
     * Este método permite establecer si queremos que se muestre el nombre del nodo en
     * la pantalla o no.
     * @param p TRUE, si queremos ver el nombre del nodo. FALSE en caso contrario.
     * @since 1.0
     */    
    public void ponerMostrarNombre(boolean p) {
        if (nombre.length() == 0)
            mostrarNombre = false;
        else
            mostrarNombre = p;
    }

    /**
     * Este método permite saber si se está mostrando el nombre del nodo en la pantalla
     * o no.
     * @return TRUE, si se está mostrando el nombre del nodo. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean obtenerMostrarNombre() {
        return mostrarNombre;
    }

    /**
     * Este método permie obtener la dirección IP del nodo.
     * @return La dirección IP del nodo.
     * @since 1.0
     */    
    public String obtenerIP() {
        return IP;
    }

    /**
     * este método permite establecer la dirección IP del nodo.
     * @param direccion Dirección IP deseada para el nodo.
     * @since 1.0
     */    
    public void ponerIP(String direccion) {
        IP = direccion;
    }

    /**
     * Este método permite establecer el número de puertos que tendrá el nodo.
     * @param num El número de puertos deseados para el nodo. 8 como mucho.
     * @since 1.0
     */    
    public abstract void ponerPuertos(int num);

    /**
     * Este método permite poner un paquete en el buffer de entrada del nodo.
     * @param paquete Paquete que deseamo poner.
     * @param puerto Puerto del conjunto de puertos en el que deeamos depositar el paquete.
     * @since 1.0
     */    
    public synchronized void ponerPaquete(TPDU paquete, int puerto) {
        cerrojo.bloquear();
        this.puertos.obtenerPuerto(puerto).ponerPaquete(paquete);
        cerrojo.liberar();
    }

    /**
     * Este método incrementa en 1 el número de tics que hace que el nodo no emite un
     * paquete.
     * @since 1.0
     */    
    public void incrementarPasosSinEmitir() {
        pasosSinEmitir++;
    }
    
    /**
     * Este método coloca a cero el número de pasos que el nodo ha estado sin emitir un
     * paquete.
     * @since 1.0
     */    
    public void restaurarPasosSinEmitir() {
        pasosSinEmitir = 0;
    }

    /**
     * Este método obtiene el número pasos que el nodo lleva sin emitir un paquete.
     * @return El número de pasos
     * @since 1.0
     */    
    public int obtenerPasosSinEmitir() {
        return pasosSinEmitir;
    }
    
    /**
     * Este método permite descartar un paquete en el nodo.
     * @param paquete Paquete que deseamos descartar.
     * @since 1.0
     */    
    public abstract void descartarPaquete(TPDU paquete);
    
    /**
     * Este método permite acceder directamente a los puertos del nodo.
     * @return El conjunto de puertos del nodo.
     * @since 1.0
     */    
    public abstract TPuertosNodo obtenerPuertos();
    /**
     * Este método permite obtener eventos de sincronización del reloj principal del
     * simulador.
     * @param evt Evento enviado por el reloj principal del simulador.
     * @since 1.0
     */    
    public abstract void capturarEventoReloj(TEventoReloj evt);
    /**
     * Este método permite obtener el tipo de nodo al que pertenece la instancia
     * actual.
     * @return El tipo del nodo. Una de las constantes definidas en la clase.
     * @since 1.0
     */    
    public abstract int obtenerTipo();

    /**
     * Este método se ponen en funcionamiento cuando el hilo independiente del nodo
     * entra en funcionamiento. En él se codifica toda la funcionalidad del nodo.
     * @since 1.0
     */    
    public abstract void run();

    /**
     * Este método debe ser implementado. Permitirá solicitar a un nodo activo la
     * retransmisión de un paquete.
     * @param paquete Paquete cuya retransmisión se está solicitando.
     * @param pSalida Puerto por el que se enviará la solicitud.
     * @since 1.0
     */    
    public abstract void solicitarGPSRP(TPDUMPLS paquete, int pSalida);
    
    /**
     * Este método averigua si al nodo le quedan puertos libre o no.
     * @return TRUE, si quedan puertos libres al nodo. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean tienePuertosLibres();
    /**
     * Este método devuelve el peso del nodo, que debe ser tenido en cuenta por los
     * algoritmos de encaminamiento.
     * @since 1.0
     * @return El peso del nodo.
     */    
    public abstract long obtenerPeso();

    /**
     * Este método devuelve si el nodo está bien configurado o no.
     * @return TRUE, si el nodo está bien configurado. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean estaBienConfigurado();
    
    /**
     * Este método comprueba la configuración del nodo y devuelve un codigo que expresa
     * esta situación.
     * @param t Topología donde se encuentra el nodo.
     * @param recfg TRUE, si se está reconfigurando el nodod. FALSE si se está configurando por
     * primera vez.
     * @return CORRECTA, si la configuración es correcta. Un código de error en caso contrario.
     * @since 1.0
     */    
    public abstract int comprobar(TTopologia t, boolean recfg);
    
    /**
     * Este método transofmra el código de error de la configuración de un nodo en un
     * texto inteligible.
     * @param e Código de error.
     * @return Un texto explicando el codigo de error.
     * @since 1.0
     */    
    public abstract String obtenerMensajeError(int e);
    
    /**
     * Este método transforma el nodo en una repreentación de texto que se puede volcar
     * a disco.
     * @return La cadena de texto que representa al nodo.
     * @since 1.0
     */    
    public abstract String serializar();
    
    /**
     * Este método toma un nodo serializado y lo deserializa.
     * @param elemento Elemento serializado (texto).
     * @return TRUE, si se consigue deserializar sin problemas. FALSe en caso contrario.
     * @since 1.0
     */    
    public abstract boolean desSerializar(String elemento);
    
    /**
     * Este método permite acceder directamente a las estadísticas del nodo.
     * @return Estadísticas del nodo.
     * @since 1.0
     */    
    public abstract TEstadisticas accederAEstadisticas();
   
    /**
     * Este método reinicia los atributos de la clase, dejándolos como recien iniciados
     * por el constructor.
     * @since 1.0
     */    
    public abstract void reset();
    
    /**
     * Esta constante identifica a un nodo emisor.
     * @since 1.0
     */    
    public static final int EMISOR = 0;
    /**
     * Esta constante identifica a un nodo receptor.
     * @since 1.0
     */    
    public static final int RECEPTOR = 1;
    /**
     * Esta constante identifica a un nodo LER.
     * @since 1.0
     */    
    public static final int LER = 2;
    /**
     * Esta constante identifica a un nodo LER activo.
     * @since 1.0
     */    
    public static final int LERA = 3;
    /**
     * Esta constante identifica a un nodo LSR.
     * @since 1.0
     */    
    public static final int LSR = 4; 
    /**
     * Esta constante identifica a un nodo LSR activo.
     * @since 1.0
     */    
    public static final int LSRA = 5;

    /**
     * Esta constante identifica el ancho de los iconos de los nodos.
     * @since 1.0
     */    
    public static final int ANCHO_ICONOS = 48;
    /**
     * Esta constante identifica el alto de los iconos de los nodos.
     * @since 1.0
     */    
    public static final int ALTO_ICONOS = 48;

    /**
     * Esta cosntante identifica que el nodo está seleccionado.
     * @since 1.0
     */    
    public static final int DESELECCIONADO = 0;
    /**
     * Esta cosntante identifica que el nodo no está seleccionado.
     * @since 1.0
     */    
    public static final int SELECCIONADO = 1;

    /**
     * Esta constante identifica el número de puerto que tiene un emisor.
     * @since 1.0
     */    
    public static final int NUM_PUERTOS_EMISOR = 1;
    /**
     * Esta constante identifica el número de puerto que tiene un receptor.
     * @since 1.0
     */    
    public static final int NUM_PUERTOS_RECEPTOR = 1;
    /**
     * Esta constante identifica el número de puerto que tiene un LER.
     * @since 1.0
     */    
    public static final int NUM_PUERTOS_LER = 8;
    /**
     * Esta constante identifica el número de puerto que tiene un LER activo.
     * @since 1.0
     */    
    public static final int NUM_PUERTOS_LERA = 8;
    /**
     * Esta constante identifica el número de puerto que tiene un LSR.
     * @since 1.0
     */    
    public static final int NUM_PUERTOS_LSR = 8;
    /**
     * Esta constante identifica el número de puerto que tiene un LSR activo.
     * @since 1.0
     */    
    public static final int NUM_PUERTOS_LSRA = 8;
    /**
     * Esta constante identifica el número de pasos máximo que el nodo estará
     * sin emitir un paquete antes de que se muestre un reloj de arena en el
     * simulador.
     * @since 1.0
     */    
    public static final int MAX_PASOS_SIN_EMITIR = 25;
    
    /**
     * @since 1.0
     */    
    private int id;
    /**
     * @since 1.0
     */    
    private int estado;
    /**
     * @since 1.0
     */    
    private String nombre;
    /**
     * @since 1.0
     */    
    private Point posicion;
    /**
     * @since 1.0
     */    
    private boolean mostrarNombre;
    /**
     * @since 1.0
     */    
    private String IP;
    /**
     * Este atributo contiene el conjunto de puertos del nodo.
     * @since 1.0
     */    
    protected TPuertosNodo puertos;
    /**
     * @since 1.0
     */    
    private TMonitor cerrojo;
    /**
     * Este atributo almacena la topologia en la que está incluido el nodo.
     * @since 1.0
     */    
    protected TTopologia topologia;
    /**
     * @since 1.0
     */    
    private boolean generarEstadisticas;
    
    /**
     * Este atributo almacena el número de nanosegundos que tiene un tic de reloj
     * asignado por el reloj principal.
     * @since 1.0
     */    
    protected int nsDelTic;
    
    private int pasosSinEmitir = 0;
}
