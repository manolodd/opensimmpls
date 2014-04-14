package simMPLS.utiles;

import simMPLS.utiles.*;

/** Esta es la implementación de un generador de identificadores numéricos que no se
 * repiten y que se van incrementando hasta llegar a su límite máximo.
 * @version 1.0
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TIdentificadorLargo {
    
    /** Atributo que almacena el valor interno del generador de identificadores
     * numéticos largos.
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
     * Este método reinicia el generador de identificadores a su valor original, como
     * si acabase de ser instanciado.
     * @since 1.0
     */
    public synchronized void reset() {
        identificador = 0;
    }
    
    /** Método que devuelve un identificador generado por el generador. Además se
     * modifica el contador interno para que el siguiente generador sea distinto. El
     * método está <B>sincronizado</B>.
     * @return Un número enterio que será un identificador largo: un entero largo único.
     * @throws EDesbordeDelIdentificador Esta excepción se lanza cuando el contador interno del generador de
     * identificadores se desborda. Es alto por lo que generalmente no ocurrirá, pero
     * hay que capturar la excepción por si acaso.
     * @since 1.0
     */
    synchronized public long obtenerNuevo() throws EDesbordeDelIdentificador {
        if (identificador > 9223372036854775806L) {
            throw new EDesbordeDelIdentificador();
        } else {
            identificador++;
        }
        return (identificador);
    }
    
}
