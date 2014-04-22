package simMPLS.utils;

/** Esta es la implementaci�n de un generador de identificadores num�ricos que no se
 * repiten y que se van incrementando hasta llegar a su l�mite m�ximo.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TIdentificador {
    
    /** Atributo que contendr� el valor interno del generador de identificadores
     * num�ticos.
     */
    private int identificador;
    
    /** Crea un nuevo generador de identificadores con el valor inicial 0.
     * @since 1.0
     */
    public TIdentificador() {
        identificador = 0;
    }
    
    /**
     * Este m�todo reinicia el generador de identificadores a su valor original, como
     * si acabase de ser instanciado.
     * @since 1.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /** M�todo que devuelve un identificador generado por el generador. Adem�s se
     * modifica el contador interno para que el siguiente generador sea distinto. El
     * m�todo est� <B>sincronizado</B>.
     * @return Un numero entero que ser� un identificador: no estar� repetido.
     * @throws EDesbordeDelIdentificador Esta excepci�n se lanza cuando el contador interno del generador de
     * identificadores se desborda. Es alto por lo que ngeneralmente no ocurrir�, pero
     * hay que capturar la excepci�n por si acaso.
     * @since 1.0
     */
    synchronized public int obtenerNuevo() throws EDesbordeDelIdentificador {
        if (identificador > 2147483646) {
            throw new EDesbordeDelIdentificador();
        } else {
            identificador++;
        }
        return (identificador);
    }
    
    /**
     * Este m�todo establece el valor de partida del generador de identificadores,
     * siempre que el valor que deseamos sea mayor que el que ya tiene el propio
     * generador.
     * @since 1.0
     * @param i El valor de partida con el cual deseamos iniciar el contador.
     */
    synchronized public void ponerIdentificadorSiMayor(int i) {
        if (i > identificador)
            identificador = i;
    }
    
    /**
     * Este m�todo permite poner el valor de partida del generador de identificadores.
     * @since 1.0
     * @param i El valor que deseamos poner como valor de partida para el generador.
     */
    synchronized public void ponerIdentificador(int i) {
        identificador = i;
    }
}
