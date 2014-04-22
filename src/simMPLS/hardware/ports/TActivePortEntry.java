package simMPLS.hardware.ports;

import simMPLS.protocols.TPDU;


/**
 * Esta clase implementa una entrada en el buffer de un puerto activo. Es necesario
 * para poder priorizar unos paquetes mas que otros en el buffer de forma
 * sencilla.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TActivePortEntry implements Comparable {
    
    /**
     * Crea una nueva instancia de TEntradaPuertoActivo
     * @since 1.0
     * @param prior Prioridad de la entrada del buffer.
     * @param orden Orden de llegada al buffer.
     * @param paquet Paquete que ha llegado al buffer y que est� contenido en la entrada.
     */
    public TActivePortEntry(int prior, int orden, TPDU paquet) {
        this.prioridad = prior;
        this.ordenDeLlegada = orden;
        this.paquete = paquet;
    }
    
    /**
     * Este m�todo sirve para comparar dos instancias de TActivePortEntry.
     * @param o Otra entrada de un puerto activo con la que se quiere comparar la instancia
     * actual.
     * @return -1, 0 � 1, dependiendo de si la instancia actual es menor, mayo o igual que la
     * especificada por par�metro, hablando de orden.
     * @since 1.0
     */    
    @Override
    public int compareTo(Object o) {
        TActivePortEntry parametro = (TActivePortEntry) o;
        if (this.ordenDeLlegada < parametro.obtenerOrdenDeLlegada()) {
            return TActivePortEntry.PRIMERO_EL_ACTUAL;
        }
        if (this.ordenDeLlegada > parametro.obtenerOrdenDeLlegada()) {
            return TActivePortEntry.PRIMERO_EL_PARAMETRO;
        }
        return TActivePortEntry.IGUALES;
    }
    
    /**
     * Este m�todo obtiene la prioridad de la entrada.
     * @since 1.0
     * @return La prioridad de la entrada, de 0 a 10, inclusives.
     */    
    public int obtenerPrioridad() {
        return this.prioridad;
    }
    
    /**
     * Este m�todo devuelve el orden de llegada de la entrada al puerto.
     * @since 1.0
     * @return El orden de llegada de la entrada al puerto.
     */    
    public int obtenerOrdenDeLlegada() {
        return this.ordenDeLlegada;
    }
    
    /**
     * Este m�todo devuelve el paquete que lleg� a la entrada del puerto.
     * @return Paquete que est� almacenado en el buffer.
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
