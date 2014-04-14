package simMPLS.protocolo;

/** Esta clase implementa la cabecera de un paquete IPv4 con las opciones que
 * interesan para este proyecto final de carrera.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TCabeceraIPv4 {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de TCabeceraIPv4.
     * @param ipo Dirección IP origen.
     * @param ipd Dirección IP destino.
     * @since 1.0
     */
    public TCabeceraIPv4(String ipo, String ipd) {
        IPOrigen = ipo;
        IPDestino = ipd;
        opciones = new TCampoOpcionesIPv4();
        TTL = 255;
    }
    
    /**
     * Este método devuelve una cadena de caracteres que identifica ínequívocamente a
     * un paquete marcado con GoS dentro de la topología. Está formada por una
     * concatenación de las representaciones textuales de la IP del emisor y del
     * identificador único del paquete.
     * @return Una cadena de caracteres única en todo el dominio MPLS para ese paquete.
     * @since 1.0
     */
    public int obtenerClavePrimaria() {
        String clave = "";
        if (opciones.estaUsado()) {
            clave = this.IPOrigen + opciones.obtenerIDPaqueteGoS();
            return clave.hashCode();
        }
        return -1;
    }
    
    /** Este método devuelve el tamaño de la cabecera IPv4 en un momento dado. Puede ser
     * que su campo opcinoes haya variad y por tanto el tamaño de la cabecera no sea
     * constante.
     * @return El tamaño de la cabecera en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        return (20 + opciones.obtenerTamanio());
    }
    
    /** Este método devuelve la IP de origen de la cabecera IPv4.
     * @return La dirección IP de origen.
     * @since 1.0
     */
    public String obtenerIPOrigen() {
        return IPOrigen;
    }
    
    /** Este método sirve para modificar el campo de dirección IP origen de la cabecera
     * IPv4.
     * @param ipo El nuevo valor de la IP de origen.
     * @since 1.0
     */
    public void ponerIPOrigen(String ipo) {
        IPOrigen = ipo;
    }
    
    /** Este método devuelve la IP de destino de la cabecera IPv4.
     * @return La dirección IP de destino.
     * @since 1.0
     */
    public String obtenerIPDestino() {
        return IPDestino;
    }
    
    /** Este método sirve para modificar el campo de dirección destino de la cabecera
     * IPv4.
     * @param ipd El nuevo valor para la IP de destino.
     * @since 1.0
     */
    public void ponerIPDestino(String ipd) {
        IPDestino = ipd;
    }
    
    /** Este método modifica el valor del campo TTL de la cabecera IPv4.
     * @param t El nuevo valor para el campo TTL.
     * @since 1.0
     */
    public void ponerTTL(int t) {
        TTL = t;
    }
    
    /** Este método obtiene el valor del campo TTL de la cabecera IPv4.
     * @return El valor del campo TTL.
     * @since 1.0
     */
    public int obtenerTTL() {
        return TTL;
    }
    
    /** Este método obtiene el campo opciones al completo, de la cabecera IPv4.
     * @return El campo opciones de la cabecera, completo.
     * @since 1.0
     */
    public TCampoOpcionesIPv4 obtenerCampoOpciones() {
        return opciones;
    }
    
    /** Atributo que será la dirección fuente del paquete IPv4 que llevará esta cabecera.
     * @since 1.0
     */
    private String IPOrigen;
    /** Atributo que será la dirección destino del paquete IPv4 que llevará esta cabecera.
     * @since 1.0
     */
    private String IPDestino;
    /** Atributo que será el campo "Time to Live" de la cabecera IPv4.
     * @since 1.0
     */
    private int TTL;
    /** Atributo que implementa una codificación del campo opciones de IPv4.
     * @since 1.0
     */
    private TCampoOpcionesIPv4 opciones;
}
