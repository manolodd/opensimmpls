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

import java.util.LinkedList;

/**
 * This class implements a MPLS packet.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TMPLSPDU extends TAbstractPDU {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TMPLSPDU.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id Packet identifier.
     * @param originIP IP addres of this packet's sender.
     * @param targetIP IP addres of this packet's receiver.
     * @param payloadSize The desired size for the payload of this MPLS packet,
     * in bytes (octects).
     * @since 1.0
     */
    public TMPLSPDU(long id, String originIP, String targetIP, int payloadSize) {
        super(id, originIP, targetIP);
        this.TCPPayload = new TTCPPayload(payloadSize);
        this.MPLSLabelStack = new TMPLSLabelStack();
        this.subType = TAbstractPDU.MPLS;
    }

    /**
     * This method creates a clone of this MPLS packet.
     *
     * @since 1.0
     * @return An exact copy of this MPLS packet.
     */
    public TMPLSPDU getAClon() {
        long auxID = this.getID();
        String auxOriginIP = this.getIPv4Header().getOriginIP();
        String auxTargetIP = this.getIPv4Header().getTargetIP();
        int auxTCPPayloadSize = this.TCPPayload.getSize() - 20;
        TMPLSPDU clonedMPLSPDU = new TMPLSPDU(auxID, auxOriginIP, auxTargetIP, auxTCPPayloadSize);
        // "Guarentee of Service Support over MPLS using Active Techniques" 
        // proposal redefines the IPv4 Options field to track crossed active 
        // nodes. Therefore we inspect if this field is being use this way and 
        // clone it if necessary.
        if (this.getIPv4Header().getOptionsField().isUsed()) {
            int auxGoSLevel = this.getIPv4Header().getOptionsField().getRequestedGoSLevel();
            clonedMPLSPDU.getIPv4Header().getOptionsField().setRequestedGoSLevel(auxGoSLevel);
            int auxGoSID = this.getIPv4Header().getOptionsField().getPacketLocalUniqueIdentifier();
            clonedMPLSPDU.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(auxGoSID);
            if (this.getIPv4Header().getOptionsField().hasCrossedActiveNodes()) {
                int auxNumberOfCrossedActiveNodes = this.getIPv4Header().getOptionsField().getNumberOfCrossedActiveNodes();
                int i = 0;
                String auxCurrentCrossedActiveNodeTag = null;
                for (i = 0; i < auxNumberOfCrossedActiveNodes; i++) {
                    auxCurrentCrossedActiveNodeTag = this.getIPv4Header().getOptionsField().getCrossedActiveNode(i);
                    if (auxCurrentCrossedActiveNodeTag != null) {
                        clonedMPLSPDU.getIPv4Header().getOptionsField().setCrossedActiveNode(new String(auxCurrentCrossedActiveNodeTag));
                    }
                }
            }
        }
        TMPLSLabel currentLabel = null;
        TMPLSLabel newLocalLabel = null;
        TMPLSLabel newLabelForClonedPDU = null;
        LinkedList localLabelStack = new LinkedList();
        LinkedList labelStackForClonedPDU = new LinkedList();
        while (this.getLabelStack().getSize() > 0) {
            currentLabel = this.getLabelStack().getTop();
            this.getLabelStack().popTop();
            int auxLabelID = currentLabel.getID();
            newLocalLabel = new TMPLSLabel(auxLabelID);
            newLocalLabel.setBoS(currentLabel.getBoS());
            newLocalLabel.setEXP(currentLabel.getEXP());
            newLocalLabel.setLabel(currentLabel.getLabel());
            newLocalLabel.setTTL(currentLabel.getTTL());
            newLabelForClonedPDU = new TMPLSLabel(auxLabelID);
            newLabelForClonedPDU.setBoS(currentLabel.getBoS());
            newLabelForClonedPDU.setEXP(currentLabel.getEXP());
            newLabelForClonedPDU.setLabel(currentLabel.getLabel());
            newLabelForClonedPDU.setTTL(currentLabel.getTTL());
            if (localLabelStack.size() == 0) {
                localLabelStack.add(newLocalLabel);
            } else {
                localLabelStack.addFirst(newLocalLabel);
            }
            if (labelStackForClonedPDU.size() == 0) {
                labelStackForClonedPDU.add(newLabelForClonedPDU);
            } else {
                labelStackForClonedPDU.addFirst(newLabelForClonedPDU);
            }
        }
        while (localLabelStack.size() > 0) {
            this.getLabelStack().pushLabel((TMPLSLabel) localLabelStack.removeFirst());
        }
        while (labelStackForClonedPDU.size() > 0) {
            clonedMPLSPDU.getLabelStack().pushLabel((TMPLSLabel) labelStackForClonedPDU.removeFirst());
        }
        return clonedMPLSPDU;
    }

    /**
     * This method returns the size of the packet in bytes (octects).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size of this packet in bytes (octects).
     * @since 1.0
     */
    @Override
    public int getSize() {
        int auxSize = 0;
        auxSize += super.getIPv4Header().getSize(); // IPv4 header.
        auxSize += this.TCPPayload.getSize(); // TCP payload.
        auxSize += (4 * this.MPLSLabelStack.getSize()); // MPLS payload.
        return (auxSize);
    }

    /**
     * This method returns the type of the packet, as defined by constants in
     * TAbstractPDU class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of this packet.
     * @since 1.0
     */
    @Override
    public int getType() {
        return TAbstractPDU.MPLS;
    }

    /**
     * This method return the TCP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TCP payload of this packet.
     * @since 1.0
     */
    public TTCPPayload getTCPPayload() {
        return this.TCPPayload;
    }

    /**
     * This method set the TCP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param TCPPayload The TCP payload for this packet.
     * @since 1.0
     */
    public void setTCPPayload(TTCPPayload TCPPayload) {
        this.TCPPayload = TCPPayload;
    }

    /**
     * This method returns the label stack of this MPLS packet.
     *
     * @return The label stack of this MPLS packet.
     * @since 1.0
     */
    public TMPLSLabelStack getLabelStack() {
        return this.MPLSLabelStack;
    }

    @Override
    public void setSubtype(int st) {
        this.subType = st;
    }

    /**
     * This method returns the subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The subtype of this packet. For instances of this class, it
     * returns MPLS, as defined in TAbstractPDU.
     * @since 1.0
     */
    @Override
    public int getSubtype() {
        return this.subType;
    }

    private int subType;
    private TTCPPayload TCPPayload;
    private TMPLSLabelStack MPLSLabelStack;
}
