//**************************************************************************
// Nombre......: TEntradaBufferEnlace.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa una entrada del buffer de un enlace.
// ............: Esta entrada sirve para propagar correctamente en el simula-
// ............: dor los paquetes de un lado a otro del cable.
// Fecha.......: 14/03/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import simMPLS.protocolo.*;

/**
 * Esta clase implementa una entrada dentro del buffer de un enlace. El buffer del
 * enlace existe únicamente a efectos de simulación visual, no existe en la
 * realidad.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEntradaBufferEnlace implements Comparable {

    /**
     * Crea una nueva instancia de TEntradaBufferEnlace
     * @param p Paquete que entra en el enlace.
     * @param tiempoEspera Tiempo que debe esperar el paquete en el enlace. Generalmente corresponde al
     * retardo del enlace.
     * @param dest Especifica cuál de los nodos que une el enlace es destino del tráfico. Los
     * enlaces son full duplex.
     * @since 1.0
     */
    public TEntradaBufferEnlace(TPDU p, long tiempoEspera, int dest) {
        te = tiempoEspera;
        teInicial = tiempoEspera;
        paquete = p;
        destino = dest;
    }
    
    /**
     * Este método permite comparar para su ordenación dos instancias de
     * TEntradaBufferEnlace.
     * @param o Instancia de tipo TEntradaBufferEnlace.
     * @return -1, 0 ó 1, dependiendo de si la entrada del buffer con la que se compara es
     * ordinalmente menos, igual o mayor.
     * @since 1.0
     */    
    public int compareTo(Object o) {
        TEntradaBufferEnlace ebe = (TEntradaBufferEnlace) o;
        TPDU pdu = ebe.obtenerPaquete();
        return this.paquete.compareTo(pdu);
    }

    /**
     * Este método obtiene el paquete de la entrada del buffer.
     * @return Paquete de la entrada del buffer.
     * @since 1.0
     */    
    public TPDU obtenerPaquete() {
        return paquete;
    }

    /**
     * Este método devuelve el total de tiempo que el paquete debe esperar en el
     * enlace.
     * @return El tiempo que el nodo debe esperar en el enlace desde que entra hasta que llega
     * al destino.
     * @since 1.0
     */    
    public long obtener100x100() {
        return this.teInicial;
    }

    /**
     * Este método permte poner un paquete en la entrada el buffer.
     * @param p Paquete que deseamos insertar en la entrada del buffer.
     * @since 1.0
     */    
    public void ponerPaquete(TPDU p) {
        paquete = p;
    }

    /**
     * Este método permite establecer el tiempo que el paquete deberá esperar en el
     * enlace desde que entra hasta que llega al destino.
     * @param t Tiempo de espera total para el paquete.
     * @since 1.0
     */    
    public void ponerTiempoEspera(long t) {
        te = t;
        teInicial = t;
    }

    /**
     * Este método permite obtener el tiempo que aún le queda al paquete para llegar al
     * destino.
     * @return Tiempo (en nanosegundos) que le falta al paquete para llegar al destino.
     * @since 1.0
     */    
    public long obtenerTiempoEspera() {
        return te;
    }

    /**
     * Este método resta al tiempo de espera del paquete en la entrada del buffer, el
     * tiempo que va transcurriendo. Al llegar a cero, el paquete habrá llegado al
     * destino.
     * @param paso Tiempo que se le resta al tiempo de espera del paquete.
     * @since 1.0
     */    
    public void restarTiempoPaso(long paso) {
        te -= paso;
        if (te < 0) 
            te = 0;
    }

    /**
     * Este método permite establecer, para esta entrada concreta del buffer, cuál de
     * los nodos extremos es el destino.
     * @param dest 1 ó 2, dependiendo de si el paquete va dirigido al extremo 1 o al extremo2 el
     * enlace.
     * @since 1.0
     */    
    public void ponerDestino(int dest) {
        destino = dest;
    }
    
    /**
     * Este método permite obtener el destino del tráfico, esto es, qué nodo es el que
     * debe recibir el paquete.
     * @return 1 o 2, dependiendo de si el tráfico debe salir por el extremo 1 o el extremo 2
     * del enlace.
     * @since 1.0
     */    
    public int obtenerDestino() {
        return destino;
    }
    
    private TPDU paquete;
    private int destino;
    private long te;
    private long teInicial;
}
