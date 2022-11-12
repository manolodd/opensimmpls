/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.opensimmpls.protocols;

import java.util.LinkedList;

/**
 * This class implements a MPLS packet.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
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
     * @param payloadSizeInOctets The desired size for the payload of this MPLS packet,
     * in bytes (octects).
     * @since 2.0
     */
    public TMPLSPDU(long id, String originIP, String targetIP, int payloadSizeInOctets) {
        super(id, originIP, targetIP);
        this.tcpPayload = new TTCPPayload(payloadSizeInOctets);
        this.mplsLabelStack = new TMPLSLabelStack();
        this.subType = TAbstractPDU.MPLS;
    }

    /**
     * This method creates a clone of this MPLS packet.
     *
     * @since 2.0
     * @return An exact copy of this MPLS packet.
     */
    public TMPLSPDU getAClon() {
        long auxID = this.getID();
        String auxOriginIP = this.getIPv4Header().getOriginIPv4Address();
        String auxTargetIP = this.getIPv4Header().getTailEndIPAddress();
        // FIX: Define a class constant instead of using this harcoded value
        int auxTCPPayloadSize = this.tcpPayload.getSize() - 20;
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
                        clonedMPLSPDU.getIPv4Header().getOptionsField().setCrossedActiveNode(auxCurrentCrossedActiveNodeTag);
                    }
                }
            }
        }
        TMPLSLabel currentLabel = null;
        TMPLSLabel newLocalLabel = null;
        TMPLSLabel newLabelForClonedPDU = null;
        LinkedList<TMPLSLabel> localLabelStack = new LinkedList<>();
        LinkedList<TMPLSLabel> labelStackForClonedPDU = new LinkedList<>();
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
            if (localLabelStack.isEmpty()) {
                localLabelStack.add(newLocalLabel);
            } else {
                localLabelStack.addFirst(newLocalLabel);
            }
            if (labelStackForClonedPDU.isEmpty()) {
                labelStackForClonedPDU.add(newLabelForClonedPDU);
            } else {
                labelStackForClonedPDU.addFirst(newLabelForClonedPDU);
            }
        }
        while (localLabelStack.size() > 0) {
            this.getLabelStack().pushTop((TMPLSLabel) localLabelStack.removeFirst());
        }
        while (labelStackForClonedPDU.size() > 0) {
            clonedMPLSPDU.getLabelStack().pushTop((TMPLSLabel) labelStackForClonedPDU.removeFirst());
        }
        return clonedMPLSPDU;
    }

    /**
     * This method returns the size of the packet in bytes (octects).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size of this packet in bytes (octects).
     * @since 2.0
     */
    @Override
    public int getSize() {
        //FIX: Avoid using harcoded values. Use class constants instead.
        int auxSize = 0;
        auxSize += super.getIPv4Header().getSize(); // IPv4 header.
        auxSize += this.tcpPayload.getSize(); // TCP payload.
        auxSize += (4 * this.mplsLabelStack.getSize()); // MPLS payload.
        return (auxSize);
    }

    /**
     * This method returns the type of the packet, as defined by constants in
     * TAbstractPDU class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of this packet.
     * @since 2.0
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
     * @since 2.0
     */
    public TTCPPayload getTCPPayload() {
        return this.tcpPayload;
    }

    /**
     * This method set the TCP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param TCPPayload The TCP payload for this packet.
     * @since 2.0
     */
    public void setTCPPayload(TTCPPayload TCPPayload) {
        this.tcpPayload = TCPPayload;
    }

    /**
     * This method returns the label stack of this MPLS packet.
     *
     * @return The label stack of this MPLS packet.
     * @since 2.0
     */
    public TMPLSLabelStack getLabelStack() {
        return this.mplsLabelStack;
    }

    @Override
    public void setSubtype(int subType) {
        this.subType = subType;
    }

    /**
     * This method returns the subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The subtype of this packet. For instances of this class, it
     * returns MPLS, as defined in TAbstractPDU.
     * @since 2.0
     */
    @Override
    public int getSubtype() {
        return this.subType;
    }

    private int subType;
    private TTCPPayload tcpPayload;
    private TMPLSLabelStack mplsLabelStack;
}
