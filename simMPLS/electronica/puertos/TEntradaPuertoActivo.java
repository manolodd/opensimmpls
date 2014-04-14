package simMPLS.electronica.puertos;

import simMPLS.protocolo.*;

/**
 * Esta clase implementa una entrada en el buffer de un puerto activo. Es necesario
 * para poder priorizar unos paquetes mas que otros en el buffer de forma
 * sencilla.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEntradaPuertoActivo implements Comparable {
    
    /**
     * Crea una nueva instancia de TEntradaPuertoActivo
     * @since 1.0
     * @param prior Prioridad de la entrada del buffer.
     * @param orden Orden de llegada al buffer.
     * @param paquet Paquete que ha llegado al buffer y que está contenido en la entrada.
     */
    public TEntradaPuertoActivo(int prior, int orden, TPDU paquet) {
        this.prioridad = prior;
        this.ordenDeLlegada = orden;
        this.paquete = paquet;
    }
    
    /**
     * Este método sirve para comparar dos instancias de TEntradaPuertoActivo.
     * @param o Otra entrada de un puerto activo con la que se quiere comparar la instancia
     * actual.
     * @return -1, 0 ó 1, dependiendo de si la instancia actual es menor, mayo o igual que la
     * especificada por parámetro, hablando de orden.
     * @since 1.0
     */    
    public int compareTo(Object o) {
        TEntradaPuertoActivo parametro = (TEntradaPuertoActivo) o;
        if (this.ordenDeLlegada < parametro.obtenerOrdenDeLlegada()) {
            return this.PRIMERO_EL_ACTUAL;
        }
        if (this.ordenDeLlegada > parametro.obtenerOrdenDeLlegada()) {
            return this.PRIMERO_EL_PARAMETRO;
        }
        return this.IGUALES;
    }
    
    /**
     * Este método obtiene la prioridad de la entrada.
     * @since 1.0
     * @return La prioridad de la entrada, de 0 a 10, inclusives.
     */    
    public int obtenerPrioridad() {
        return this.prioridad;
    }
    
    /**
     * Este método devuelve el orden de llegada de la entrada al puerto.
     * @since 1.0
     * @return El orden de llegada de la entrada al puerto.
     */    
    public int obtenerOrdenDeLlegada() {
        return this.ordenDeLlegada;
    }
    
    /**
     * Este método devuelve el paquete que llegó a la entrada del puerto.
     * @return Paquete que está almacenado en el buffer.
     * @since 1.0
     */    
    public TPDU obtenerPaquete() {
        return this.paquete;
    }
    
    private static final int PRIMERO_EL_ACTUAL = -1;
    private static final int PRIMERO_EL_PARAMETRO = 1;
    private static final int IGUALES = 0;

    private int prioridad;
    private int ordenDeLlegada;
    private TPDU paquete;
}
