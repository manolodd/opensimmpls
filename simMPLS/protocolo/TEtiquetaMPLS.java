package simMPLS.protocolo;

/** Esta clase implementa una etiqueta MPLS con sus campos bien diferenciados y
 * accesibles.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEtiquetaMPLS implements Comparable {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TEtiquetaMPLS usando como identificador el pasado por parámetros.
     * @param id El identificador de la etiqueta, pasado por parámetro.
     * @since 1.0
     */
    public TEtiquetaMPLS(int id) {
        TTL = 256;
        label = 16;
        EXP = 0;
        BoS = true;
        identificador = id;
    }
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TEtiquetaMPLS y le pone por defecto el identificador 0.
     * @since 1.0
     */
    public TEtiquetaMPLS() {
        TTL = 256;
        label = 16;
        EXP = 0;
        BoS = true;
        identificador = 0;
    }
    
    /** Este método nos permite cambiar el identificador de la etiqueta MPLS.
     * @param id El nuevo valor que deseamos poner al identificador de la etiqueta MPLS.
     * @since 1.0
     */
    public void ponerIdentificador(int id) {
        identificador = id;
    }
    
    /** Este método nos permite consultar el valor del identificador de la etiqueta
     * MPLS.
     * @return El valor del identificador de la etiqueta MPLS.
     * @since 1.0
     */
    public int obtenerIdentificador() {
        return identificador;
    }
    
    /** Este método nos permite cambiar el valor del atributo <B>label</B> de la
     * etiqueta MPLS.
     * @param l El nuevo valor que deseamos para el campo label de la etiqueta MPLS.
     * @since 1.0
     */
    public void ponerLabel(int l) {
        label = l;
    }
    
    /** Este método nos permite obtener el valor del atributo <B>label</B> de la etiqueta MPLS.
     * @return El valor del atributo privado label, de la etiqueta MPLS.
     * @since 1.0
     */
    public int obtenerLabel() {
        return label;
    }
    
    /** Este método nos permite cambiar el valor del atributto <B>TTL</B> (Time to Live)
     * de la etiqueta MPLS.
     * @param t El nuevo valor deseado para el atributo TTL.
     * @since 1.0
     */
    public void ponerTTL(int t) {
        TTL = t;
    }
    
    /** Este método nos permite obtener el valor del atributo privado <B>TTL</B> de la
     * etiqueta MPLS.
     * @return El valor del atributo TTL de la etiqueta MPLS.
     * @since 1.0
     */
    public int obtenerTTL() {
        return TTL;
    }
    
    /** Este método nos permite especificar el valor del atributo <B>EXP</B> de la
     * etiqueta MPLS.
     * @param e El nuevo valor deseado para el atributo privado EXP de la etiqueta MPLS.
     * @since 1.0
     */
    public void ponerEXP(int e) {
        EXP = e;
    }
    
    /** Este método nos permite obtener el valor del atributo privado <B>EXP</B> de la
     * etiqueta MPLS.
     * @return El valor del atributo privado EXP de la etiqueta MPLS.
     * @since 1.0
     */
    public int obtenerEXP() {
        return EXP;
    }
    
    /** Este método nos permite especificar un valor para el atributo privado <B>Bos</B>
     * (Bottom of Stack) de la etiqueta MPLS.
     * @param b El nuevo valor para el campo BoS de la etiqueta MPLS.
     * @since 1.0
     */
    public void ponerBoS(boolean b) {
        BoS = b;
    }
    
    /** Este método nos permite obtener el valor del atributo privado <B>BoS</B> (Bottom
     * of Stack)de la etiqueta MPLS.
     * @return El valor del atributo privado BoS (Bottom of Stack) del a etiqueta MPLS.
     * @since 1.0
     */
    public boolean obtenerBoS() {
        return BoS;
    }
    
    /** Este método compara esta instancia de TEtiquetaMPLS con otra del mismo tipo,
     * para aclarar si es mayor, menor o igual, en base al orden que especifica este
     * método. Es la implementación de la interfaz Comparable.
     * @param o El objeto del tipo TEtiquetaMPLS con el que se va a comparar.
     * @return -1, 0 ó 1, dependiendo de si, de acuerdo con el orden establecido, esta
     * instancia es menor, igual o mayor que la especificada por parámetro.
     * @since 1.0
     */
    public int compareTo(Object o) {
        TEtiquetaMPLS e2= (TEtiquetaMPLS) o;
        if (this.obtenerIdentificador() > e2.obtenerIdentificador()) {
            return 1;
        } else if (this.obtenerIdentificador() == e2.obtenerIdentificador()) {
            return 0;
        } else {
            return -1;
        }
    }
    
    /** Atributo privado. El campo TTL (Time to Live) de la etiqueta MPLS.
     * @since 1.0
     */
    private int TTL;
    /** Atributo privado. El campo Label de la cabecera MPLS.
     * @since 1.0
     */
    private int label;
    /** Atributo privado. Es el campo EXP (Experimental) de la etiqueta MPLS. Lo
     * usaremos en Open SimMPLS para denotar cuándo un paquete está marcado como
     * privilegiado.
     * @since 1.0
     */
    private int EXP;
    /** Atributo privado. Campo BoS (Bottom of Stack) de la etiqueta MPLS.
     * @since 1.0
     */
    private boolean BoS;
    /** Atributo privado. Identificador numérico único para cada etiqueta, de forma que
     * puedan ser ordenadas en la pila.
     * @since 1.0
     */
    private int identificador;
}
