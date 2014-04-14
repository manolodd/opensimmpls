package simMPLS.utiles;

import simMPLS.utiles.*;

/**
 * Esta clase implementa un generador de identificadores largos, cíclico (vuelve a
 * empezar). Se utiliza para identificar los paquetes con GoS de un emisor.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TIdentificadorRotativo {
    
    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de
     * TIdentificadorRotativo y lo inicia a cero.
     * @since 1.0
     */
    public TIdentificadorRotativo() {
        identificador = 0;
    }
    
    /**
     * Este método coloca a cero el generador de identificadores. Como si acabase de
     * ser instanciado.
     * @since 1.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /**
     * Este método obtiene un nuevo identificador del generador e incrementa su
     * contador interno para la siguiente llamada.
     * @return Un identificador nuevo y único (en este ciclo del generador).
     * @since 1.0
     */
    synchronized public int obtenerNuevo() {
        if (identificador > 2147483646) {
            identificador = 0;
        } else {
            identificador++;
        }
        return (identificador);
    }
    
    /**
     * Este método permite establecer el valor a partir del cuál el generador de
     * identificadores comenzará a generar.
     * @param i El valor con el que se desea iniciar el generador.
     * @since 1.0
     */
    synchronized public void ponerIdentificador(int i) {
        identificador = i;
    }
    
    private int identificador;
}
