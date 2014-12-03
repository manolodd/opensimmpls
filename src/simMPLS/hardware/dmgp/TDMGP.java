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
package simMPLS.hardware.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.protocols.TAbstractPDU;
import simMPLS.protocols.TMPLSPDU;
import simMPLS.utils.TRotaryIDGenerator;
import simMPLS.utils.TMonitor;

/**
 * This class implements a DMGP memory to save GoS-aware PDUs temporarily.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TDMGP {

    /**
     * This method is the class constructor. It creates a new instance of TDMGP
     * and initialize its attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TDMGP() {
        this.monitor = new TMonitor();
        this.idGenerator = new TRotaryIDGenerator();
        this.flows = new TreeSet();
        this.totalAvailablePercentage = 100;
        this.totalDMGPSizeInKB = 1;
        this.totalAssignedOctects = 0;
    }

    /**
     * This method establish the global size of DMGP in kilobytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     * @param size Size in kilobytes.
     */
    public void setDMGPSizeInKB(int size) {
        this.totalDMGPSizeInKB = size;
        this.reset();
    }

    /**
     * This method obtains the globals soze of DMGP in kilobites.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size in kilobites.
     * @since 1.0
     */
    public int getDMGPSizeInKB() {
        return this.totalDMGPSizeInKB;
    }

    /**
     * This method look for a packet tagged as GoS within the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID Identifier of the flow the packet belongs to.
     * @param packetID Identifier of the packet.
     * @return The packet, if in the DMGP. NULL on the contrary.
     * @since 1.0
     */
    public TMPLSPDU getPacket(int flowID, int packetID) {
        TMPLSPDU wantedPacket = null;
        TDMGPFlowEntry dmgpFlowEntry = this.getFlow(flowID);
        if (dmgpFlowEntry != null) {
            this.monitor.lock();
            Iterator it = dmgpFlowEntry.getEntries().iterator();
            TDMGPEntry dmgpEntry = null;
            while (it.hasNext()) {
                dmgpEntry = (TDMGPEntry) it.next();
                if (dmgpEntry.getPacketID() == packetID) {
                    wantedPacket = dmgpEntry.getPacket();
                    this.monitor.unLock();
                    return wantedPacket;
                }
            }
            this.monitor.unLock();
        }
        return null;
    }

    /**
     * This method insert a new GoS packet into the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be inserted into the DMGP memory.
     * @since 1.0
     */
    public void addPacket(TMPLSPDU packet) {
        TDMGPFlowEntry dmgpFlowEntry = this.getFlow(packet);
        if (dmgpFlowEntry == null) {
            dmgpFlowEntry = this.createFlow(packet);
        }
        if (dmgpFlowEntry != null) {
            dmgpFlowEntry.addPacket(packet);
        } else {
            packet = null;
        }
    }

    /**
     * This method restores the value of all attributes as when created by the
     * constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void reset() {
        this.monitor = null;
        this.idGenerator = null;
        this.flows = null;
        this.monitor = new TMonitor();
        this.idGenerator = new TRotaryIDGenerator();
        this.flows = new TreeSet();
        this.totalAvailablePercentage = 100;
        this.totalAssignedOctects = 0;
    }

    private int getDMGPSizeInOctects() {
        return (this.totalDMGPSizeInKB * 1024);
    }

    private TDMGPFlowEntry getFlow(TAbstractPDU packet) {
        TDMGPFlowEntry dmgpFlowEntry = null;
        int flowID = packet.getHeader().getOriginIP().hashCode();
        dmgpFlowEntry = getFlow(flowID);
        return dmgpFlowEntry;
    }

    private TDMGPFlowEntry getFlow(int idf) {
        TDMGPFlowEntry dmgpFlowEntry = null;
        this.monitor.lock();
        Iterator ite = flows.iterator();
        while (ite.hasNext()) {
            dmgpFlowEntry = (TDMGPFlowEntry) ite.next();
            if (dmgpFlowEntry.getFlowID() == idf) {
                this.monitor.unLock();
                return dmgpFlowEntry;
            }
        }
        this.monitor.unLock();
        return null;
    }

    private TDMGPFlowEntry createFlow(TAbstractPDU packet) {
        this.monitor.lock();
        TDMGPFlowEntry dmgpFlowEntry = null;
        int flowID = packet.getHeader().getOriginIP().hashCode();
        int percentageToBeAssigned = 0;
        int octectsToBeAssigned = 0;
        if (this.totalAssignedOctects < this.getDMGPSizeInOctects()) {
            percentageToBeAssigned = this.getPercentageToBeAssigned(packet);
            octectsToBeAssigned = this.getOctectsToBeAssigned(packet);
            if (octectsToBeAssigned > 0) {
                this.totalAssignedOctects += octectsToBeAssigned;
                this.totalAvailablePercentage -= percentageToBeAssigned;
                dmgpFlowEntry = new TDMGPFlowEntry(this.idGenerator.getNextID());
                dmgpFlowEntry.setFlowID(flowID);
                dmgpFlowEntry.setAssignedPercentage(percentageToBeAssigned);
                dmgpFlowEntry.setAssignedOctects(octectsToBeAssigned);
                flows.add(dmgpFlowEntry);
                this.monitor.unLock();
                return dmgpFlowEntry;
            }
        }
        this.monitor.unLock();
        return null;
    }

    private int getOctectsToBeAssigned(TAbstractPDU packet) {
        int reservedPercentage = getRequestedPercentage(packet);
        int reservedOctects = 0;
        if (this.totalAvailablePercentage > 0) {
            if (this.totalAvailablePercentage > reservedPercentage) {
                reservedOctects = ((this.getDMGPSizeInOctects() * reservedPercentage) / 100);
                return reservedOctects;
            } else {
                reservedOctects = this.getDMGPSizeInOctects() - this.totalAssignedOctects;
                return reservedOctects;
            }
        }
        return 0;
    }

    private int getPercentageToBeAssigned(TAbstractPDU packet) {
        int reservedPercentage = getRequestedPercentage(packet);
        if (this.totalAvailablePercentage > 0) {
            if (this.totalAvailablePercentage > reservedPercentage) {
                return reservedPercentage;
            } else {
                return this.totalAvailablePercentage;
            }
        }
        return 0;
    }

    private int getRequestedPercentage(TAbstractPDU packet) {
        int packetGoSLevel = 0;
        if (packet.getHeader().getOptionsField().isUsed()) {
            packetGoSLevel = packet.getHeader().getOptionsField().getEncodedGoSLevel();
        } else {
            return 0;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP) {
            return 12;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP) {
            return 12;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP) {
            return 8;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP) {
            return 8;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP) {
            return 4;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP) {
            return 4;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP) {
            return 0;
        }
        if (packetGoSLevel == TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP) {
            return 0;
        }
        return 1;
    }

    private TMonitor monitor;
    private TRotaryIDGenerator idGenerator;
    private TreeSet flows;
    private int totalAvailablePercentage;
    private int totalDMGPSizeInKB;
    private int totalAssignedOctects;
}
