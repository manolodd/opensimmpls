/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.protocols;

/** Esta clase implementa los m�nimos para un paquete electronico de forma que
 * cualquier definici�n de un paquete (MPLS, TCP, IPv4...) pueda heredar de esta
 * definici�n y permitir as� el polimorfismo.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TAbstractPDU implements Comparable {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de TPDu en
     * base a los valores pasados por par�metro.
     * @param ident Identificador del paquete, �nico para cada paquete.
     * @param ipo Direcci�n IP origen del paquete.
     * @param ipd Direcci�n IP destino del paquete.
     * @since 1.0
     */
    public TAbstractPDU(long ident, String ipo, String ipd) {
        identificador = ident;
        cabecera = new TIPv4Header(ipo, ipd);
    }
    
    /** Este m�todo nos permite obtener el valor del atributo privado de la clase,
     * identificador.
     * @return El valor del atributo privado de la clase, identificador.
     * @since 1.0
     */
    public long obtenerIdentificador() {
        return identificador;
    }
    
    /** Este m�todo nos permite modificar el valor del atributo privado de la clase,
     * identificador.
     * @param i El nuevo valor que deseamos para el atributo privado de la clase, identificador.
     * @since 1.0
     */
    public void ponerIdentificador(long i) {
        identificador = i;
    }
    
    /** Este m�todo nos permite acceder directamente a la cabecera IPv4 del paquete.
     * @return La cabecera IPv4 del paquete, para poder utilizar sus m�todos de forma directa.
     * @since 1.0
     */
    public TIPv4Header getHeader() {
        return cabecera;
    }
    
    /** Este m�todo nos permite poner una cabecera IPv4 distinta al paquete.
     * @param c La nueva cabecera IPv4 que deseamos establecer para este paquete.
     * @since 1.0
     */
    public void ponerCabecera(TIPv4Header c) {
        cabecera = c;
    }
    
    /** Este m�todo compara dos objetos del tipo TAbstractPDU para ver si son mayores, menores o
 iguales seg�n el tipo de clasificaci�n seleccionado. Es la implementaci�n de la
     * interfaz Comparable.
     * @param o El otro paquete con el que vamos a compararnos.
     * @return -1, 0 o 1 dependiendo de si el paquete pasado por par�metro es mayor, igual o
     * menor que el actual, de acuerdo a la ordenaci�n elegida.
     * @since 1.0
     */
    public int compareTo(Object o) {
        TAbstractPDU pdu = (TAbstractPDU) o;
        if (this.obtenerIdentificador() > pdu.obtenerIdentificador()) {
            return 1;
        } else if (this.obtenerIdentificador() == pdu.obtenerIdentificador()) {
            return (this.getHeader().obtenerIPOrigen().compareTo(pdu.getHeader().obtenerIPOrigen()));
        } else {
            return -1;
        }
    }
    
    /** Este m�todo, que debe ser implementado en las subclases, nos devuelve el tama�o
     * en bytes del paquete. De esta forma se pueden calcular multitud de cosas durante
     * la simulaci�n.
     * @return El tama�o en bytes del paquete.
     * @since 1.0
     */
    public abstract int getSize();
    /** Este m�todo, que se debe especificar en las subclases, devuelve el tipo de
     * paquete de que se trata la instancia actual. Ser� uno de los valores constantes
     * definidos en la clase.
     * @return En las subclases, debe devolver el tipo de PDU de que se trata, que ser� una de
     * las constantes definidas en esta clase.
     * @since 1.0
     */
    public abstract int getType();
    /**
     * M�todo abstracto que permitir�, cuando sea definido, obtener las distintas
     * subclases de un paquete, si existen subclases del mismo.
     * @return El subtipo del paquete.
     * @since 1.0
     */
    public abstract int getSubtype();
    /**
     * M�todo abstracto que permitir�, cuando sea definido, establecer las distintas
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
     * marcado con alg�n nivel de Garant�a de Servicio (GoS).
     * @since 1.0
     */
    public static final int IPV4_GOS = 4;
    /** Constante de la clase que indica que el paquete se trata de un paquete MPLS
     * marcado con alg�n nivel de Garant�a de Servicio (GoS).
     * @since 1.0
     */
    public static final int MPLS_GOS = 5;
    
    /**
     * Constante de la clase que indica que el paquete se trata de un paquete RLPRP
     * de recuperaci�n local de caminos.
     * @since 1.0
     */    
    public static final int RLPRP = 6;

    /** Atributo privado que conforma un identificador del paquete de tal forma que en
     * base a �l se puedan ordenar posteriormente los paquetes generados.
     * @since 1.0
     */
    protected long identificador;
    /**
     * Esta constante indica que nivel de garant�a de servicio 0 y no creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    
    public static final int EXP_LEVEL0_WITHOUT_BACKUP_LSP = 0;
    /**
     * Esta constante indica que nivel de garant�a de servicio 1 y no creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_LEVEL1_WITHOUT_BACKUP_LSP = 1;
    /**
     * Esta constante indica que nivel de garant�a de servicio 2 y no creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_LEVEL2_WITHOUT_BACKUP_LSP = 2;
    /**
     * Esta constante indica que nivel de garant�a de servicio 3 y no creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_LEVEL3_WITHOUT_BACKUP_LSP = 3;
    /**
     * Esta constante indica que nivel de garant�a de servicio 0 y creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_LEVEL0_WITH_BACKUP_LSP = 4;
    /**
     * Esta constante indica que nivel de garant�a de servicio 1 y creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_LEVEL1_WITH_BACKUP_LSP = 5;
    /**
     * Esta constante indica que nivel de garant�a de servicio 2 y creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_LEVEL2_WITH_BACKUP_LSP = 6;
    /**
     * Esta constante indica que nivel de garant�a de servicio 3 y creaci�n de LSP
     * de backup.
     * @since 1.0
     */
    public static final int EXP_LEVEL3_WITH_BACKUP_LSP = 7;
    
    /**
     * En Open SimMPLS 1.0 todos los paquetes transportan son IPv4. Por tanto todos
     * ellos llevar�n una cabecera IPv4, que es esta.
     * @since 1.0
     */
    TIPv4Header cabecera;
}
