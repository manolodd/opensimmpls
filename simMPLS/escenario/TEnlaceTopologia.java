//**************************************************************************
// Nombre......: TEnlaceTopologia.java
// Proyecto....: Open SimMPLS
// Descripción.: Superclase que implementa un enlace entre dos nodos de la
// ............: topología.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import java.awt.*;
import java.util.*;
import simMPLS.escenario.*;
import simMPLS.interfaz.dialogos.*;
import simMPLS.electronica.reloj.*;
import simMPLS.protocolo.*;
import simMPLS.electronica.puertos.*;
import simMPLS.utiles.*;
import org.jfree.data.*;
import org.jfree.chart.*;


/**
 * Esta calse es abstracta y representa a un enlace de la topología.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TEnlaceTopologia extends TElementoTopologia implements Comparable, IEventoRelojListener, Runnable {
    
    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de
     * TEnlaceTopologia.
     * @param identificador Identificador único del enlace.
     * @param il Generador de identificadores para los eventos emitidos por el enlace.
     * @param t Topología a la que pertenece el enlace.
     * @since 1.0
     */
    public TEnlaceTopologia(int identificador, TIdentificadorLargo il, TTopologia t) {
        super(TElementoTopologia.ENLACE, il);
        id = identificador;
        extremo1 = null;
        extremo2 = null;
        mostrarNombre = false;
        nombre = "";
        delay = 1;
        puertoExtremo1 = -1;
        puertoExtremo2 = -1;
        buffer = Collections.synchronizedSortedSet(new TreeSet());
        bufferLlegadosADestino = new TreeSet();
        cerrojo = new TMonitor();
        cerrojoLlegados = new TMonitor();
        topologia = t;
        enlaceCaido = false;
    }
    
    /**
     * Este método averigua si el enlace está caido o no.
     * @return TRUE, si el enlace está caido. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean obtenerEnlaceCaido() {
        return enlaceCaido;
    }
    
    /**
     * Este método compara la instancia actual con otro objeto del mismo tipo, para
     * averiguar la posición ordinal de uno con respecto al otro.
     * @param o Enlace de la topología con el que se va a comparar la instancia actual.
     * @return -1, 0 ó 1, dependiendo de si la instancia actual es menor, igual o mayor que la
     * pasada por parámetro, en términos de orden.
     * @since 1.0
     */
    public int compareTo(Object o) {
        TEnlaceTopologia e = (TEnlaceTopologia) o;
        if (obtenerIdentificador() < e.obtenerIdentificador())
            return -1;
        else if (obtenerIdentificador() == e.obtenerIdentificador())
            return 0;
        return 1;
    }
    
    /**
     * Este método calcula qué porcentaje de tránsito le queda a un paquete concreto,
     * sabiendo que ha recorrido ya un porcentaje determinado.
     * @param cienxcien Cien por cien.
     * @param xxcien Equis por ciento.
     * @return Valor calculado.
     * @since 1.0
     */    
    public long obtenerPorcentajeTransito(long cienxcien, long xxcien) {
        return ((cienxcien-xxcien)*100)/cienxcien;
    }
    
    /**
     * Este método calcula las coordenadas donde debe dibujarse un paquete que ha
     * recorrido ya un porcentaje concreto de su tránsito.
     * @since 1.0
     * @param porcentaje Porcentaje recorrido ya por el paquete en el enlace.
     * @return Coordenadas donde dibujar el paquete.
     */    
    public Point obtenerCoordenadasPaquete(long porcentaje) {
        Point coordenadas = new Point(0, 0);
        int x1 = extremo1.obtenerPosicion().x+24;
        int y1 = extremo1.obtenerPosicion().y+24;
        int x2 = extremo2.obtenerPosicion().x+24;
        int y2 = extremo2.obtenerPosicion().y+24;
        coordenadas.x = x1;
        coordenadas.y = y1;
        int distanciaX = (x2-x1);
        int distanciaY = (y2-y1);
        coordenadas.x += (int) ((double)distanciaX*(double)porcentaje/(double)100);
        coordenadas.y += (int) ((double)distanciaY*(double)porcentaje/(double)100);
        return coordenadas;
    }
    
    /**
     * Este método permite establecer la topologia a la que pertenece el enlace.
     * @param t Topología a la que pertenece el enlace.
     * @since 1.0
     */
    public void ponerTopologia(TTopologia t) {
        topologia = t;
    }
    
    /**
     * Este método permite obtener la topologia a la que pertenece el enlace.
     * @return La topología a la que pertenece el enlace.
     * @since 1.0
     */
    public TTopologia obtenerTopologia() {
        return topologia;
    }
    
    /**
     * Este método configura el enlace.
     * @since 1.0
     * @param recfg TRUE, si se está reconfigurando el enlace. FALSE si se está configurando por
     * primera vez.
     * @param tcenlace Objeto de configuración del enlace que contiene una configuración para
     * el mismo.
     * @param topo Topología a la que pertenece ele enlace.
     */
    public void configurar(TConfigEnlace tcenlace, TTopologia topo, boolean recfg) {
        this.ponerNombre(tcenlace.obtenerNombre());
        this.ponerMostrarNombre(tcenlace.obtenerMostrarNombre());
        this.ponerDelay(tcenlace.obtenerDelay());
        if (!recfg) {
            this.ponerExtremo1(topo.obtenerPrimerNodoLlamado(tcenlace.obtenerNombreExtremo1()));
            this.ponerExtremo2(topo.obtenerPrimerNodoLlamado(tcenlace.obtenerNombreExtremo2()));
            this.desconectarDePuertos();
            this.ponerPuertoExtremo1(tcenlace.obtenerPuertoExtremo1());
            TPuertosNodo p1 = extremo1.obtenerPuertos();
            if (p1 != null) {
                p1.ponerEnlaceEnPuerto(this, this.puertoExtremo1);
            }
            this.ponerPuertoExtremo2(tcenlace.obtenerPuertoExtremo2());
            TPuertosNodo p2 = extremo2.obtenerPuertos();
            if (p2 != null) {
                p2.ponerEnlaceEnPuerto(this, this.puertoExtremo2);
            }
        }
    }
    
    /**
     * Este método obtiene un objeto con la configuración completa del enlace.
     * @return Configuración completa del enlace.
     * @since 1.0
     */    
    public TConfigEnlace obtenerConfiguracion() {
        TConfigEnlace tce = new TConfigEnlace();
        tce.ponerNombre(this.obtenerNombre());
        tce.ponerMostrarNombre(this.obtenerMostrarNombre());
        if (this.obtenerExtremo1() != null)
            tce.ponerNombreExtremo1(this.obtenerExtremo1().obtenerNombre());
        if (this.obtenerExtremo2() != null)
            tce.ponerNombreExtremo2(this.obtenerExtremo2().obtenerNombre());
        tce.ponerDelay(this.obtenerDelay());
        tce.ponerPuertoExtremo1(this.obtenerPuertoExtremo1());
        tce.ponerPuertoExtremo2(this.obtenerPuertoExtremo2());
        return tce;
    }
    
    /**
     * Este método libera el enlace, desconectándolo de los posibles nodos a los que
     * esté "enganchado".
     * @since 1.0
     */
    public void desconectarDePuertos() {
        if (extremo1 != null) {
            TPuertosNodo p1 = extremo1.obtenerPuertos();
            if (p1 != null) {
                p1.quitarEnlaceDePuerto(this.puertoExtremo1);
            }
        }
        if (extremo2 != null) {
            TPuertosNodo p2 = extremo2.obtenerPuertos();
            if (p2 != null) {
                p2.quitarEnlaceDePuerto(this.puertoExtremo2);
            }
        }
    }
    
    /**
     * Este método establece el retardo del enlace.
     * @param d Retardo del enlace.
     * @since 1.0
     */
    public void ponerDelay(int d) {
        if (d <= 0) {
            delay = 1;
        } else {
            delay = d;
        }
    }
    
    /**
     * Este método obtiene el retardo del enlace.
     * @return Retardo del enlace.
     * @since 1.0
     */
    public int obtenerDelay() {
        return delay;
    }
    
    /**
     * Este método establece el identificador único del enlace.
     * @param identificador Identificador del enlace.
     * @since 1.0
     */
    public void ponerIdentificador(int identificador) {
        id = identificador;
    }
    
    /**
     * Este método permite obtener el identificador único del enlace.
     * @return Identificador unico del enlace.
     * @since 1.0
     */
    public int obtenerIdentificador() {
        return id;
    }
    
    /**
     * Este método permite establecer el nombre del enlace.
     * @param nom Nombre del enlace.
     * @since 1.0
     */
    public void ponerNombre(String nom) {
        nombre = nom;
    }
    
    /**
     * Este método permite obtener el nombre del enlace.
     * @return Nombre del enlace.
     * @since 1.0
     */
    public String obtenerNombre() {
        return nombre;
    }
    
    /**
     * Este método permite especificar si el nombre del enlace se ha de ver o no.
     * @param m TRUE, si el nombre debe mostrarse. FALSE en caso contrario.
     * @since 1.0
     */
    public void ponerMostrarNombre(boolean m) {
        mostrarNombre = m;
    }
    
    /**
     * Este método permite obtener si el nombre del enlace se ha de ver o no.
     * @return TRUE, si el nombre se esta mostrando. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean obtenerMostrarNombre() {
        return mostrarNombre;
    }
    
    /**
     * Este método obtiene el nodo que está al final del extremo izquierdo del enlace.
     * @return El nodo extremo izquierdo del enlace.
     * @since 1.0
     */
    public TNodoTopologia obtenerExtremo1() {
        return extremo1;
    }
    
    /**
     * Este método permite establecer el nodo que estará conectado al extremo izquierdo
     * del enlace.
     * @param e1 Nodo que estará conectado al extremo izquierdo del enalace.
     * @since 1.0
     */
    public void ponerExtremo1(TNodoTopologia e1) {
        extremo1 = e1;
    }
    
    /**
     * Este método obtiene el nodo que está al final del extremo derecho del enlace.
     * @return El nodo del extremo derecho del enlace.
     * @since 1.0
     */
    public TNodoTopologia obtenerExtremo2() {
        return extremo2;
    }
    
    /**
     * Este método permite establecer el nodo que estará conectado al extremo derecho
     * del enlace.
     * @param e2 Nodo del extremo derecho del enlace.
     * @since 1.0
     */
    public void ponerExtremo2(TNodoTopologia e2) {
        extremo2 = e2;
    }
    
    /**
     * Este método obtiene la posición del extremo izquierdo del enlace.
     * @return Posicioón del extremo izquierdo del enlace.
     * @since 1.0
     */
    public Point obtenerPosicion1() {
        return extremo1.obtenerPosicion();
    }
    
    /**
     * Este método establece el puerto del nodo extremo izquierdo al que está conectado
     * el enlace.
     * @param p Puerto del nodo extremo izquierdo.
     * @since 1.0
     */
    public void ponerPuertoExtremo1(int p) {
        puertoExtremo1 = p;
    }
    
    /**
     * Este método permite obtener el puerto del nodo extremo izquierdo al que está
     * conectado el enlace.
     * @return Puerto del nodo extremo izquierdo.
     * @since 1.0
     */
    public int obtenerPuertoExtremo1() {
        return puertoExtremo1;
    }
    
    /**
     * Este método establece el puerto del nodo extremo derecho al que está conectado
     * el enlace.
     * @param p Puerto del nodo extremo derecho.
     * @since 1.0
     */
    public void ponerPuertoExtremo2(int p) {
        puertoExtremo2 = p;
    }
    
    /**
     * Este método permite obtener el puerto del nodo extremo derecho al que está
     * conectado el enlace.
     * @return Puerto del nodo extremo derecho.
     * @since 1.0
     */
    public int obtenerPuertoExtremo2() {
        return puertoExtremo2;
    }
    
    /**
     * Este método obtiene la posición del nodo extremo derecho del enlace.
     * @return Posición del nodo extremo derecho del enlace.
     * @since 1.0
     */
    public Point obtenerPosicion2() {
        return extremo2.obtenerPosicion();
    }
    
    /**
     * Este método comprueba si el enlace está conectad a un nodo concreto.
     * @param extremo Nodo al que se desea saber si el enlace está conectado o no.
     * @return TRUE, si está conectado. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean conectadoA(TNodoTopologia extremo) {
        if (extremo1.obtenerIdentificador() == extremo.obtenerIdentificador())
            return true;
        if (extremo2.obtenerIdentificador() == extremo.obtenerIdentificador())
            return true;
        return false;
    }
    
    /**
     * Este método comprueba si el enlace está conectad a un nodo concreto.
     * @param idExtremo Identificador del nodo al que se desea saber si el enlace está conectado o no.
     * @return TRUE, si está conectado. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean conectadoA(int idExtremo) {
        if (extremo1.obtenerIdentificador() == idExtremo)
            return true;
        if (extremo2.obtenerIdentificador() == idExtremo)
            return true;
        return false;
    }
    
    /**
     * Este método coloca un paquete desde el enlace al nodo destino.
     * @param paquete Paquete que se desea trasladar.
     * @param destino Nodo destino del paquete en el enlace.
     * @since 1.0
     */
    public void ponerPaquete(TPDU paquete, int destino) {
        cerrojo.bloquear();
        buffer.add(new TEntradaBufferEnlace(paquete, this.obtenerDelay(), destino));
        cerrojo.liberar();
    }
    
    /**
     * Este método comprueba si dada unas coordenadas, el enlace pasa por dicha posición.
     * @param p Posición.
     * @return TRUE, si el enlace pasa por esa posición. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean estaEnPosicion(Point p) {
        int x1 = extremo1.obtenerPosicion().x+24;
        int y1 = extremo1.obtenerPosicion().y+24;
        int x2 = extremo2.obtenerPosicion().x+24;
        int y2 = extremo2.obtenerPosicion().y+24;
        int dx, dy, pasos, k;
        double incrementox, incrementoy, x, y;
        
        if ((x1 == x2) && (y1 == y2))   // Para líneas que son sólo un punto.
        {
            if ((p.x == x1) && (p.y == y1))
                return true;
        }
        else	// Para el resto de líneas.
        {
            dx = x2 - x1;
            dy = y2 - y1;
            if (Math.abs(dx) > Math.abs(dy))
                pasos = Math.abs(dx);
            else
                pasos = Math.abs(dy);
            incrementox = (float) dx / pasos;
            incrementoy = (float) dy / pasos;
            x = x1;
            y = y1;
            
            if ((x >= p.x-3) && (x <= p.x+3) &&
            (y >= p.y-3) && (y <= p.y+3)) {
                return true;
            }
            for (k=1; k<=pasos; k++) {
                x += incrementox;
                y += incrementoy;
                if ((x >= p.x-3) && (x <= p.x+3) &&
                (y >= p.y-3) && (y <= p.y+3)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Este método devuelve el cerrojo del enlace.
     * @return El cerrojo del enlace.
     * @since 1.0
     */
    public TMonitor obtenerCerrojo() {
        return this.cerrojo;
    }
    
    /**
     * Este método comprueba cuál de los dos extremos del enlace es el nodo pasado por
     * parámetro.
     * @param n Nodo que realiza la consulta.
     * @return EXTREMO1, si el nodo es el extremo 1. EXTREMO2 si es el extremo 2.
     * @since 1.0
     */
    public int queExtremoSoyYo(TNodoTopologia n) {
        if (n.obtenerIdentificador() == extremo1.obtenerIdentificador())
            return this.EXTREMO1;
        return this.EXTREMO2;
    }
    
    /**
     * Este método calcula a qué extremo del enlace se debe enviar un paquete dado el
     * nodo quelo envía.
     * @param n Nodo que envía el paquete y que hace la consulta.
     * @return EXTREMO1 si el paquete debe ir al extremo 1. EXTREMO 2 si debe ir al extremo 2.
     * @since 1.0
     */
    public int obtenerDestinoLocal(TNodoTopologia n) {
        if (n.obtenerIdentificador() == extremo1.obtenerIdentificador())
            return this.EXTREMO2;
        return this.EXTREMO1;
    }
    
    /**
     * Este método devuelve el tipo del enlace.
     * @return Será redefinido por las subclases.
     * @since 1.0
     */
    public abstract int obtenerTipo();
    /**
     * Este método captura un evento de reloj lo que pone en funcionamiento al enlace.
     * @param evt Evento de reloj.
     * @since 1.0
     */
    public abstract void capturarEventoReloj(TEventoReloj evt);
    /**
     * Núcleo del enlace. Aquí se codificará (en las subclases) toda la funcionalidad.
     * @since 1.0
     */
    public abstract void run();
    /**
     * Este método obtiene el peso del enlace. Que será usado por los algoritmos de
     * encaminamiento.
     * @return Peso del enlace.
     * @since 1.0
     */
    public abstract long obtenerPeso();
    /**
     * Este método obtiene el peso del enlace. Que será usado por el algoritmos de
     * encaminamiento RABAN. El peso incluye diversos factores y no exclusivamente
     * el retardo del enlace.
     * @return Peso del enlace.
     * @since 1.0
     */
    public abstract long obtenerPesoRABAN();
    /**
     * Este método calcula si está bien configurado el enlace.
     * @return TRUE, si está bien configurado. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean estaBienConfigurado();
    /**
     * Este m´´etodo devolverá un mensaje de error asociado a un codigo de error.
     * @param e Codigo de error.
     * @return Representación textual del código de error.
     * @since 1.0
     */    
    public abstract String obtenerMensajeError(int e);
    /**
     * Este método serializa el enlace; es decir, lo convierte a texto para ser volcado
     * a disco.
     * @return Representación textual del enlace.
     * @since 1.0
     */    
    public abstract String serializar();
    /**
     * Este método deserializa un enlace y lo construye en memoria.
     * @param elemento Enlace serializado.
     * @return TRUE, si todo va bien. FALSE si no se ha podido deserializar el enlace.
     * @since 1.0
     */    
    public abstract boolean desSerializar(String elemento);
    /**
     * Este método establece si el enlace está caío o no.
     * @param ec TRUE, si el enlace debe aparecer como caido. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract void ponerEnlaceCaido(boolean ec);
    /**
     * Este método reinicia los atributos de la clase, dejando el enlace como recien
     * creado por el constructor.
     * @since 1.0
     */    
    public abstract void reset();
    
    /**
     * Esta constante identifica a un enlace interno al dominio MPLS.
     * @since 1.0
     */
    public static final int INTERNO = 0;
    /**
     * Está constante identifica a un enlace externo al dominio MPLS.
     * @since 1.0
     */
    public static final int EXTERNO = 1;
    
    /**
     * Esta constante se usa para identificar el extremo inicial del enlace.
     * @since 1.0
     */
    public static final int EXTREMO1 = 1;
    /**
     * Esta constante se usa para identificar el extremo final del enlace.
     * @since 1.0
     */
    public static final int EXTREMO2 = 2;
    
    private int id;
    private TNodoTopologia extremo1;
    private TNodoTopologia extremo2;
    private int puertoExtremo1;
    private int puertoExtremo2;
    private String nombre;
    private boolean mostrarNombre;
    private int delay;
    /**
     * Este atributo almacena los paquetes en el enlace para simular su recorrido por
     * el mismo.
     * @since 1.0
     */
    protected SortedSet buffer;
    
    /**
     * Este atributo almacena temporalmente los paquetes que han llegado al destinio.
     * @since 1.0
     */    
    protected TreeSet bufferLlegadosADestino;
    
    /**
     * Este atributo es el monitor de la clase que permite sincronizaciones.
     * @since 1.0
     */
    protected TMonitor cerrojo;
    /**
     * Este atributo es el monitor de la clase que permite sincronizaciones en el
     * buffer de paquetes lelgados al destino.
     * @since 1.0
     */    
    protected TMonitor cerrojoLlegados;
    /**
     * Topología a la cual pertenece el enlace.
     * @since 1.0
     */    
    protected TTopologia topologia;
    /**
     * Indica si el enlace está funcionando o está caido.
     * @since 1.0
     */    
    protected boolean enlaceCaido;
    
    /**
     * Esta constante se usa para indicar que la configuración del enlace es correcta.
     * @since 1.0
     */    
    public static final int CORRECTA = 0;
    /**
     * Esta constante se usa para indicar que la configuración del enlace es incorrecta.
     * Falta el nombre.
     * @since 1.0
     */    
    public static final int SIN_NOMBRE = 1;
    /**
     * Esta constante se usa para indicar que la configuración del enlace es incorrecta.
     * El nombre solo está formado por espacios.
     * @since 1.0
     */    
    public static final int SOLO_ESPACIOS = 2;
    /**
     * Esta constante se usa para indicar que la configuración del enlace es incorrecta.
     * Ya hay un elemento con el nombre de este enlace.
     * @since 1.0
     */    
    public static final int NOMBRE_YA_EXISTE = 3;
    /**
     * Esta constante se usa para indicar que la configuración del enlace es incorrecta.
     * no se ha especificado el puerto de origen.
     * @since 1.0
     */    
    public static final int FALTA_PUERTO_1 = 4;
    /**
     * Esta constante se usa para indicar que la configuración del enlace es incorrecta.
     * No se ha especificado el puerto de destino.
     * @since 1.0
     */    
    public static final int FALTA_PUERTO_2 = 5;
    /**
     * Esta constante se usa para indicar que la configuración del enlace es incorrecta.
     * No se ha especificado el nodo origen.
     * @since 1.0
     */    
    public static final int FALTA_EXTREMO_1 = 6;
    /**
     * Esta constante se usa para indicar que la configuración del enlace es incorrecta.
     * No se ha especificado el nodo destino.
     * @since 1.0
     */    
    public static final int FALTA_EXTREMO_2 = 7;
}
