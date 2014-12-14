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

/**
 * Esta clase implementa una codificaci�n del campo opciones de IPv4 acorde a lo
 * que necesitamos para la propuesta de GoS sobre MPLS.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TIPv4OptionsField {

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TCampoOpcionesIPv4.
     *
     * @since 1.0
     */
    public TIPv4OptionsField() {
        // FIX: create and use class constants instead of harcoded values.
        this.requestedGoSLevel = 0;
        this.registerOfActiveNodesCrossed = new String[TIPv4OptionsField.MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES];
        for (int i = 0; i < TIPv4OptionsField.MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES; i++) {
            this.registerOfActiveNodesCrossed[i] = null;
        }
        this.optionFieldIsUsed = false;
        this.numberOfActiveNodesRegistered = 0;
        this.packetLocalUniqueIdentifier = 0;
        this.hasCrossedActiveNodesIPs = false;
    }

    /**
     * Este m�todo devuelve el tama�o del campo opciones debido a que �ste puede
     * ser variable en palabras de 4 bytes. El resultado lo devuelve en bytes.
     *
     * @return El tama�o en bytes del campo opciones de IPv4.
     * @since 1.0
     */
    public int getSize() {
        // FIX: Recode. Size variable is not necessary.
        int size = 0;
        int sizeAux = 0;
        if (this.optionFieldIsUsed) {
            // FIX: Create and use class constants instead of harcoded values.
            size += 1;   // GoS field.
            size += 4;   // Unique local identifier.
            size += (4 * this.numberOfActiveNodesRegistered);
            while (sizeAux < size) {
                sizeAux += 4;
            }
            size = sizeAux;
        }
        return size;
    }

    /**
     * Este m�todo permite establecer el nivel de garant�a de servicio que se ha
     * elegido para el paquete.
     *
     * @param requestedGoSLevel Nivel de garant�a de servicio; Una de las
     * constantes definidas en la clase TPDU.
     * @since 1.0
     */
    public void setRequestedGoSLevel(int requestedGoSLevel) {
        this.requestedGoSLevel = requestedGoSLevel;
        this.optionFieldIsUsed = true;
    }

    /**
     * Este m�todo permite establecer como usado el campo opciones.
     *
     * @since 1.0
     */
    public void use() {
        this.optionFieldIsUsed = true;
    }

    /**
     * Este m�todo permite consultar si el campo opciones est� siendo usado o
     * no.
     *
     * @return true, si el campo opciones est� siendo usado. false en caso
     * contrario.
     * @since 1.0
     */
    public boolean isUsed() {
        return this.optionFieldIsUsed;
    }

    /**
     * Este m�todo poermite obtener el nivel de garant�a de servicio que tiene
     * el paquete.
     *
     * @return El nivel de garant�a de servicio del paquete. una de las
     * constantes definidas en la clase TPDU.
     * @since 1.0
     */
    public int getRequestedGoSLevel() {
        return this.requestedGoSLevel;
    }

    /**
     * Este m�todo permite establecer el identificador del paquete.
     *
     * @param packetLocalUniqueIdentifier El identificador del paquete.
     * @since 1.0
     */
    public void setPacketLocalUniqueIdentifier(int packetLocalUniqueIdentifier) {
        this.packetLocalUniqueIdentifier = packetLocalUniqueIdentifier;
        this.optionFieldIsUsed = true;
    }

    /**
     * Este m�todo permite obtener el identificador del paquete.
     *
     * @return El identificador del paquete.
     * @since 1.0
     */
    public int getPacketLocalUniqueIdentifier() {
        return this.packetLocalUniqueIdentifier;
    }

    /**
     * Este m�todo permite insertar en el campo opciones una direcci�n IP de un
     * nodo activo atravesado.
     *
     * @param crossedActiveNodeIP La IP del nodo activo atravesado.
     * @since 1.0
     */
    public void setCrossedActiveNode(String crossedActiveNodeIP) {
        this.hasCrossedActiveNodesIPs = true;
        this.optionFieldIsUsed = true;
        if (this.numberOfActiveNodesRegistered < MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES) {
            this.registerOfActiveNodesCrossed[this.numberOfActiveNodesRegistered] = crossedActiveNodeIP;
            this.numberOfActiveNodesRegistered++;
        } else {
            // Drop the first registered crossed active node IP addres, because the option field
            // is already full.
            for (int index = 1; index < MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES; index++) {
                this.registerOfActiveNodesCrossed[index - 1] = this.registerOfActiveNodesCrossed[index];
            }
            this.registerOfActiveNodesCrossed[MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES - 1] = crossedActiveNodeIP;
        }
    }

    /**
     * Este m�todo permite averiguar si en el campo opciones hay anotadas
     * direcciones IP de los nodos activos que se han pasado o no.
     *
     * @return true, si hay direcciones IP almacenadas en el campo opciones.
     * false, en caso contrario,
     * @since 1.0
     */
    public boolean hasCrossedActiveNodes() {
        return this.hasCrossedActiveNodesIPs;
    }

    /**
     * Este m�todo comprueba el n�mero de IP de nodos activos atravesados por el
     * paquete que est�n actualmente marcadas en el campo opciones.
     *
     * @return N�mero de IP de nodos activos atravesados que contiene el campo
     * opciones.
     * @since 1.0
     */
    public int getNumberOfCrossedActiveNodes() {
        return this.numberOfActiveNodesRegistered;
    }

    /**
     * Este m�todo devuelve la IP del nodo activo que el paquete atraves� hace
     * <I>registeredActiveNodeIndex</I>
     * nodos activos.
     *
     * @param registeredActiveNodeIndex N�mero de nodos activos atravesados antes del que queremos
     * saber la IP.
     * @return IP del nodo activo deseado.
     * @since 1.0
     */
    public String getCrossedActiveNode(int registeredActiveNodeIndex) {
        if (registeredActiveNodeIndex < TIPv4OptionsField.MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES) {
            return this.registerOfActiveNodesCrossed[registeredActiveNodeIndex];
        }
        return null;
    }

    private static final int MAX_REGISTERED_ACTIVE_NODE_IP_ADDRESSES = 8;

    private int requestedGoSLevel;
    private String[] registerOfActiveNodesCrossed;
    private boolean optionFieldIsUsed;
    private int numberOfActiveNodesRegistered;
    private int packetLocalUniqueIdentifier;
    private boolean hasCrossedActiveNodesIPs;
}
