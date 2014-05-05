package simMPLS.utils;

/** Esta es la implementaci�n de un generador de identificadores num�ricos que no se
 * repiten y que se van incrementando hasta llegar a su l�mite m�ximo.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TIdentificadorLargo {
    
    /** Atributo que almacena el valor interno del generador de identificadores
     * num�ticos largos.
     * @since 1.0
     */
    private long identificador;
    
    /** Crea un nuevo generador de identificadores con el valor inicial 0.
     * @since 1.0
     */
    public TIdentificadorLargo() {
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
     * @return Un n�mero enterio que ser� un identificador largo: un entero largo �nico.
     * @throws EDesbordeDelIdentificador Esta excepci�n se lanza cuando el contador interno del generador de
     * identificadores se desborda. Es alto por lo que generalmente no ocurrir�, pero
     * hay que capturar la excepci�n por si acaso.
     * @since 1.0
     */
    synchronized public long getNextID() throws EDesbordeDelIdentificador {
        if (identificador > 9223372036854775806L) {
            throw new EDesbordeDelIdentificador();
        } else {
            identificador++;
        }
        return (identificador);
    }
    
}
