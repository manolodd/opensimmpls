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

/** Esta clase implementa un paquete TLDP (Tiny Label Distribution Protocol).
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TTLDPPDU extends TAbstractPDU {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de un
     * paquete TLDP bas�ndose en lo par�metros pasados.
     * @param id id �nico del paquete.
     * @param ipo Direcci�n IP origen del paquete.
     * @param ipd Direcci�n IP destino del paquete.
     * @since 1.0
     */
    public TTLDPPDU(long id, String ipo, String ipd) {
        super(id, ipo, ipd);
        this.TCPPayload = new TTCPPayload(0);
        this.TLDPPayload = new TTLDPPayload();
        this.LSPType = false;
        this.packetDirection = TTLDPPDU.DIRECTION_FORWARD;
    }
    
    /** Este m�todo devuelve el tama�o completo del paquete en bytes, para poder
     * realizar c�lculos en la simulaci�n.
     * @return El tama�o completo del paquete en bytes.
     * @since 1.0
     */
    public int getSize() {
        int auxSize = 0;
        auxSize += super.getIPv4Header().getSize(); // IPv4 header
        auxSize += this.TCPPayload.getSize(); // TCP header
        auxSize += this.TLDPPayload.getSize(); // LDP payload
        return (auxSize);
    }
    
    /** Este m�todo devuelve la constante TLDP, indicando que el paquete es de tipo
     * TLDP.
     * @return La constante TLDP.
     * @since 1.0
     */
    public int getType() {
        return super.TLDP;
    }
    
    /** Este m�todo nos permite el acceso a los datos TCP de este paquete, para poder
     * acceder a sus m�todos de forma directa.
     * @return Los datos TCP de esta instancia.
     * @since 1.0
     */
    public TTCPPayload getTCPPayload() {
        return this.TCPPayload;
    }
    
    /** Este m�todo nos permite acceder a los datos TLDP del paquete para hacer uso
     * directamente de sus m�todos.
     * @return Los datos TLDP de esta instancia.
     * @since 1.0
     */
    public TTLDPPayload getTLDPPayload() {
        return this.TLDPPayload;
    }
    
    /** Este m�todo nos permite acceder a la header IPv4 del paquete y poder hacer uso
 de sus m�todos de forma directa.
     * @return La header IP del paquete.
     * @since 1.0
     */
    public TIPv4Header getIPv4Header() {
        return super.getIPv4Header();
    }
    
    /**
     * Este m�todo permite obtener el subtipo del paquete TLDP. En esta versi�n el
     * paquete TLDP no tiene subtipos. Implementa este m�todo para dejar de ser una
     * clase abstracta.
     * @return TAbstractPDU.TLDP
     * @since 1.0
     */
    public int getSubtype() {
        return TAbstractPDU.TLDP;
    }
    
    /**
     * Este m�todo no hace nada. Existe porque es necesario implementarlo por ser
     * abstracto en una clase superior.
     * @param st No utilizado.
     * @since 1.0
     */
    public void setSubtype(int st) {
        // No se hace nada
    }
    
    /**
     * Este m�todo permite establecer el tipo de LSP al que se refiere el paquete TLDP.
     * Este m�todo y el valor especificado se utilizan exclusivamente para la
     * simulaci�n visual y en ning�n caso se puede entender que el valor forma parte
     * del protocolo TLDP.
     * @param tlsp LSP_NORMAL o LSP_BACKUP, dependiendo de si se refiere a un LSP tradicional o a
     * un LSP de respaldo.
     * @since 1.0
     */    
    public void setLSPType(boolean tlsp) {
        this.LSPType = tlsp;
    }
    
    /**
     * Este m�todo permite obtener el tipo de LSP al que se refiere el paquete TLDP.
     * Este m�todo y el valor devuelto se utilizan exclusivamente para la
     * simulaci�n visual y en ning�n caso se puede entender que el valor forma parte
     * del protocolo TLDP.
     * @return LSP_NORMAL o LSP_BACKUP, dependiendo de si se refiere a un LSP tradicional o a
     * un LSP de respaldo.
     * @since 1.0
     */    
    public boolean getLSPType() {
        return this.LSPType;
    }
    
    /**
     * Este m�todo permite obtener por d�nde ha llegado el paquete TLDP al nodo.
     * @since 1.0
     * @return CAME_BY_ENTRANCE, CAME_BY_EXIT o CAME_BY_BACKUP_EXIT, seg�n el lugar por donde haya llegado el nodo.
     */    
    public int getLocalOrigin() {
        if (this.packetDirection == TTLDPPDU.DIRECTION_FORWARD)
            return TTLDPPDU.CAME_BY_ENTRANCE;
        if (this.packetDirection == TTLDPPDU.DIRECTION_BACKWARD)
            return TTLDPPDU.CAME_BY_EXIT;
        if (this.packetDirection == TTLDPPDU.DIRECTION_BACKWARD_BACKUP)
            return TTLDPPDU.CAME_BY_BACKUP_EXIT;
        return TTLDPPDU.CAME_BY_ENTRANCE;
    }
    
    /**
     * Este m�todo permite establecer por donde va a salir del nodo el paquete TLDP.
     * @since 1.0
     * @param localTarget DIRECTION_FORWARD, DIRECTION_BACKWARD o DIRECTION_BACKWARD_BACKUP, dependiendo de por donde salga el paquete del
 nodo.
     */    
    public void setLocalTarget(int localTarget) {
        this.packetDirection = localTarget;
    }
    
    public static final int DIRECTION_FORWARD = -1;
    public static final int DIRECTION_BACKWARD = -2;
    public static final int DIRECTION_BACKWARD_BACKUP = -3;
    
    public static final int CAME_BY_ENTRANCE = -1;
    public static final int CAME_BY_EXIT = -2;
    public static final int CAME_BY_BACKUP_EXIT = -3;
    
    private int packetDirection;
    private TTCPPayload TCPPayload;
    private TTLDPPayload TLDPPayload;
    
    private boolean LSPType;
}
