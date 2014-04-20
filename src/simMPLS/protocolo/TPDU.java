package simMPLS.protocolo;

/** Esta clase implementa los mínimos para un paquete electronico de forma que
 * cualquier definición de un paquete (MPLS, TCP, IPv4...) pueda heredar de esta
 * definición y permitir así el polimorfismo.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TPDU implements Comparable {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de TPDu en
     * base a los valores pasados por parámetro.
     * @param ident Identificador del paquete, único para cada paquete.
     * @param ipo Dirección IP origen del paquete.
     * @param ipd Dirección IP destino del paquete.
     * @since 1.0
     */
    public TPDU(long ident, String ipo, String ipd) {
        identificador = ident;
        cabecera = new TCabeceraIPv4(ipo, ipd);
    }
    
    /** Este método nos permite obtener el valor del atributo privado de la clase,
     * identificador.
     * @return El valor del atributo privado de la clase, identificador.
     * @since 1.0
     */
    public long obtenerIdentificador() {
        return identificador;
    }
    
    /** Este método nos permite modificar el valor del atributo privado de la clase,
     * identificador.
     * @param i El nuevo valor que deseamos para el atributo privado de la clase, identificador.
     * @since 1.0
     */
    public void ponerIdentificador(long i) {
        identificador = i;
    }
    
    /** Este método nos permite acceder directamente a la cabecera IPv4 del paquete.
     * @return La cabecera IPv4 del paquete, para poder utilizar sus métodos de forma directa.
     * @since 1.0
     */
    public TCabeceraIPv4 obtenerCabecera() {
        return cabecera;
    }
    
    /** Este método nos permite poner una cabecera IPv4 distinta al paquete.
     * @param c La nueva cabecera IPv4 que deseamos establecer para este paquete.
     * @since 1.0
     */
    public void ponerCabecera(TCabeceraIPv4 c) {
        cabecera = c;
    }
    
    /** Este método compara dos objetos del tipo TPDU para ver si son mayores, menores o
     * iguales según el tipo de clasificación seleccionado. Es la implementación de la
     * interfaz Comparable.
     * @param o El otro paquete con el que vamos a compararnos.
     * @return -1, 0 o 1 dependiendo de si el paquete pasado por parámetro es mayor, igual o
     * menor que el actual, de acuerdo a la ordenación elegida.
     * @since 1.0
     */
    public int compareTo(Object o) {
        TPDU pdu = (TPDU) o;
        if (this.obtenerIdentificador() > pdu.obtenerIdentificador()) {
            return 1;
        } else if (this.obtenerIdentificador() == pdu.obtenerIdentificador()) {
            return (this.obtenerCabecera().obtenerIPOrigen().compareTo(pdu.obtenerCabecera().obtenerIPOrigen()));
        } else {
            return -1;
        }
    }
    
    /** Este método, que debe ser implementado en las subclases, nos devuelve el tamaño
     * en bytes del paquete. De esta forma se pueden calcular multitud de cosas durante
     * la simulación.
     * @return El tamaño en bytes del paquete.
     * @since 1.0
     */
    public abstract int obtenerTamanio();
    /** Este método, que se debe especificar en las subclases, devuelve el tipo de
     * paquete de que se trata la instancia actual. Será uno de los valores constantes
     * definidos en la clase.
     * @return En las subclases, debe devolver el tipo de PDU de que se trata, que será una de
     * las constantes definidas en esta clase.
     * @since 1.0
     */
    public abstract int obtenerTipo();
    /**
     * Método abstracto que permitirá, cuando sea definido, obtener las distintas
     * subclases de un paquete, si existen subclases del mismo.
     * @return El subtipo del paquete.
     * @since 1.0
     */
    public abstract int obtenerSubTipo();
    /**
     * Método abstracto que permitirá, cuando sea definido, establecer las distintas
     * subclases de un paquete, si existen subclases del mismo.
     * @param st El subtipo del paquete.
     * @since 1.0
     */
    public abstract void ponerSubtipo(int st);
    
    /** Constante de la clase que indica que el paquete se trata de un paquete IPv4.
     * @since 1.0
     */
    public static final int IPV4 = 0;
    /** Constante de la clase que indica que el paquete se trata de un paquete MPLS.
     * @since 1.0
     */
    public static final int MPLS = 1;
    /** Constante que indica que el paquete se trata de un paquete TLDP.
     * @since 1.0
     */
    public static final int TLDP = 2;
    /** Constante de la clase que indica que el paquete es un paquete GPSRP
     * @since 1.0
     */
    public static final int GPSRP = 3;
    /** Constante de la clase que indica que el paquete se trata de un paquete IPv4
     * marcado con algún nivel de Garantía de Servicio (GoS).
     * @since 1.0
     */
    public static final int IPV4_GOS = 4;
    /** Constante de la clase que indica que el paquete se trata de un paquete MPLS
     * marcado con algún nivel de Garantía de Servicio (GoS).
     * @since 1.0
     */
    public static final int MPLS_GOS = 5;
    
    /**
     * Constante de la clase que indica que el paquete se trata de un paquete RLPRP
     * de recuperación local de caminos.
     * @since 1.0
     */    
    public static final int RLPRP = 6;

    /** Atributo privado que conforma un identificador del paquete de tal forma que en
     * base a él se puedan ordenar posteriormente los paquetes generados.
     * @since 1.0
     */
    protected long identificador;
    /**
     * Esta constante indica que nivel de garantía de servicio 0 y no creación de LSP
     * de backup.
     * @since 1.0
     */
    
    public static final int EXP_NIVEL0_SINLSP = 0;
    /**
     * Esta constante indica que nivel de garantía de servicio 1 y no creación de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_NIVEL1_SINLSP = 1;
    /**
     * Esta constante indica que nivel de garantía de servicio 2 y no creación de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_NIVEL2_SINLSP = 2;
    /**
     * Esta constante indica que nivel de garantía de servicio 3 y no creación de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_NIVEL3_SINLSP = 3;
    /**
     * Esta constante indica que nivel de garantía de servicio 0 y creación de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_NIVEL0_CONLSP = 4;
    /**
     * Esta constante indica que nivel de garantía de servicio 1 y creación de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_NIVEL1_CONLSP = 5;
    /**
     * Esta constante indica que nivel de garantía de servicio 2 y creación de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_NIVEL2_CONLSP = 6;
    /**
     * Esta constante indica que nivel de garantía de servicio 3 y creación de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_NIVEL3_CONLSP = 7;
    
    /**
     * En Open SimMPLS 1.0 todos los paquetes transportan son IPv4. Por tanto todos
     * ellos llevarán una cabecera IPv4, que es esta.
     * @since 1.0
     */
    TCabeceraIPv4 cabecera;
}
