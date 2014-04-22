package simMPLS.utils;

/**
 * Esta clase implementa un generador de identificadores largos, c�clico (vuelve a
 * empezar). Se utiliza para identificar los paquetes con GoS de un emisor.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TIdentificadorRotativo {
    
    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TIdentificadorRotativo y lo inicia a cero.
     * @since 1.0
     */
    public TIdentificadorRotativo() {
        identificador = 0;
    }
    
    /**
     * Este m�todo coloca a cero el generador de identificadores. Como si acabase de
     * ser instanciado.
     * @since 1.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /**
     * Este m�todo obtiene un nuevo identificador del generador e incrementa su
     * contador interno para la siguiente llamada.
     * @return Un identificador nuevo y �nico (en este ciclo del generador).
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
     * Este m�todo permite establecer el valor a partir del cu�l el generador de
     * identificadores comenzar� a generar.
     * @param i El valor con el que se desea iniciar el generador.
     * @since 1.0
     */
    synchronized public void ponerIdentificador(int i) {
        identificador = i;
    }
    
    private int identificador;
}
