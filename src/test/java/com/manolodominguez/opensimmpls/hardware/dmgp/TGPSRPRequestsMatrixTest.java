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
package com.manolodominguez.opensimmpls.hardware.dmgp;

import com.manolodominguez.opensimmpls.commons.TSemaphore;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSLabel;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author manolodd
 */
public class TGPSRPRequestsMatrixTest {

    public TGPSRPRequestsMatrixTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of constructor method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testConstructor() {
        System.out.println("Test constructor");
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();
        assertFalse(instance.getEntriesIterator().hasNext());
    }

    /**
     * Test of reset method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testReset() {
        System.out.println("Test reset");
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        instance.addEntry(mplsPacket, 0);
        instance.reset();
        assertFalse(instance.getEntriesIterator().hasNext());
    }

    /**
     * Test of updateOutgoingPort method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testUpdateOutgoingPort() {
        System.out.println("Test updateOutgoingPort");
        boolean worksFine = true;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 1024);
        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket1, 1);
        instance.addEntry(mplsPacket1, 2);
        TGPSRPRequestEntry entry1 = instance.getEntry("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        TGPSRPRequestEntry entry2 = instance.getEntry("10.0.0.1".hashCode(), mplsPacket2.getIPv4Header().getGoSGlobalUniqueIdentifier());
        TGPSRPRequestEntry entry3 = instance.getEntry("10.0.0.1".hashCode(), mplsPacket3.getIPv4Header().getGoSGlobalUniqueIdentifier());
        if ((entry1 != null) && (entry2 != null) && (entry3 != null)) {
            instance.updateOutgoingPort(0, 2);
            instance.updateOutgoingPort(1, 2);
            worksFine &= (entry1.getOutgoingPortID() == 2);
            worksFine &= (entry2.getOutgoingPortID() == 2);
            worksFine &= (entry3.getOutgoingPortID() == 2);
        } else {
            worksFine = false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of updateOutgoingPort method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testUpdateOutgoingPortWhenOutOfRange1() {
        System.out.println("Test updateOutgoingPort");
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        instance.addEntry(mplsPacket1, 0);
        assertThrows(RuntimeException.class, () -> {
            instance.updateOutgoingPort(-1, 2); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of updateOutgoingPort method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testUpdateOutgoingPortWhenOutOfRange2() {
        System.out.println("Test updateOutgoingPort");
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        instance.addEntry(mplsPacket1, 0);
        assertThrows(RuntimeException.class, () -> {
            instance.updateOutgoingPort(0, -1); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of removeEntriesMatchingOutgoingPort method, of class
     * TGPSRPRequestsMatrix.
     */
    @Test
    public void testRemoveEntriesMatchingOutgoingPort() {
        System.out.println("Test removeEntriesMatchingOutgoingPort");

        int numberOfEntries = 0;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket2.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket2.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket2.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(2);
        TMPLSLabel bottomOutgoingMPLSLabel2 = new TMPLSLabel();
        bottomOutgoingMPLSLabel2.setBoS(true);
        bottomOutgoingMPLSLabel2.setEXP(0);
        bottomOutgoingMPLSLabel2.setLabel(123);
        bottomOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel2 = new TMPLSLabel();
        upperOutgoingMPLSLabel2.setBoS(false);
        upperOutgoingMPLSLabel2.setEXP(mplsPacket2.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel2.setLabel(1);
        upperOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(bottomOutgoingMPLSLabel2);
        mplsPacket2.getLabelStack().pushTop(upperOutgoingMPLSLabel2);

        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket3.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket3.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket3.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(3);
        TMPLSLabel bottomOutgoingMPLSLabel3 = new TMPLSLabel();
        bottomOutgoingMPLSLabel3.setBoS(true);
        bottomOutgoingMPLSLabel3.setEXP(0);
        bottomOutgoingMPLSLabel3.setLabel(123);
        bottomOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel3 = new TMPLSLabel();
        upperOutgoingMPLSLabel3.setBoS(false);
        upperOutgoingMPLSLabel3.setEXP(mplsPacket3.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel3.setLabel(1);
        upperOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        mplsPacket3.getLabelStack().pushTop(bottomOutgoingMPLSLabel3);
        mplsPacket3.getLabelStack().pushTop(upperOutgoingMPLSLabel3);

        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket2, 2);
        instance.addEntry(mplsPacket3, 2);

        instance.removeEntriesMatchingOutgoingPort(2);

        Iterator<TGPSRPRequestEntry> iterator = instance.getEntriesIterator();
        while (iterator.hasNext()) {
            iterator.next();
            numberOfEntries++;
        }
        assertEquals(1, numberOfEntries);
    }

    /**
     * Test of removeEntriesMatchingOutgoingPort method, of class
     * TGPSRPRequestsMatrix.
     */
    @Test
    public void testRemoveEntriesMatchingOutgoingPortWhenOutOfRange() {
        System.out.println("Test removeEntriesMatchingOutgoingPort");

        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        instance.addEntry(mplsPacket1, 0);

        assertThrows(RuntimeException.class, () -> {
            instance.removeEntriesMatchingOutgoingPort(-1); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of addEntry method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testAddEntry() {
        System.out.println("Test addEntry");
        boolean worksFine = true;
        int numberOfEntries = 0;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket2.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket2.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket2.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(2);
        TMPLSLabel bottomOutgoingMPLSLabel2 = new TMPLSLabel();
        bottomOutgoingMPLSLabel2.setBoS(true);
        bottomOutgoingMPLSLabel2.setEXP(0);
        bottomOutgoingMPLSLabel2.setLabel(123);
        bottomOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel2 = new TMPLSLabel();
        upperOutgoingMPLSLabel2.setBoS(false);
        upperOutgoingMPLSLabel2.setEXP(mplsPacket2.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel2.setLabel(1);
        upperOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(bottomOutgoingMPLSLabel2);
        mplsPacket2.getLabelStack().pushTop(upperOutgoingMPLSLabel2);

        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket2, 2);

        Iterator<TGPSRPRequestEntry> iterator = instance.getEntriesIterator();
        while (iterator.hasNext()) {
            iterator.next();
            numberOfEntries++;
        }

        if (numberOfEntries != 2) {
            worksFine = false;
        }

        TGPSRPRequestEntry auxEntry1 = instance.getEntry("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        TGPSRPRequestEntry auxEntry2 = instance.getEntry("10.0.0.1".hashCode(), mplsPacket2.getIPv4Header().getGoSGlobalUniqueIdentifier());

        if ((auxEntry1 == null) || (auxEntry2 == null)) {
            worksFine = false;
        }

        assertTrue(worksFine);
    }

    /**
     * Test of addEntry method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testAddEntryWhenPacketIsNull() {
        System.out.println("Test addEntry");
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();
        assertThrows(RuntimeException.class, () -> {
            instance.addEntry(null, 0); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of addEntry method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testAddEntryWhenPortIsOutOfRange() {
        System.out.println("Test addEntry");
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        assertThrows(RuntimeException.class, () -> {
            instance.addEntry(mplsPacket1, -1); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of removeEntry method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testRemoveEntry() {
        System.out.println("Test removeEntry");

        int numberOfEntries = 0;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        instance.addEntry(mplsPacket1, 0);

        instance.removeEntry("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());

        Iterator<TGPSRPRequestEntry> iterator = instance.getEntriesIterator();
        while (iterator.hasNext()) {
            iterator.next();
            numberOfEntries++;
        }
        assertEquals(0, numberOfEntries);
    }

    /**
     * Test of getEntry method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetEntry() {
        System.out.println("getEntry");

        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket2.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket2.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket2.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(2);
        TMPLSLabel bottomOutgoingMPLSLabel2 = new TMPLSLabel();
        bottomOutgoingMPLSLabel2.setBoS(true);
        bottomOutgoingMPLSLabel2.setEXP(0);
        bottomOutgoingMPLSLabel2.setLabel(123);
        bottomOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel2 = new TMPLSLabel();
        upperOutgoingMPLSLabel2.setBoS(false);
        upperOutgoingMPLSLabel2.setEXP(mplsPacket2.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel2.setLabel(1);
        upperOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(bottomOutgoingMPLSLabel2);
        mplsPacket2.getLabelStack().pushTop(upperOutgoingMPLSLabel2);

        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket3.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket3.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket3.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(3);
        TMPLSLabel bottomOutgoingMPLSLabel3 = new TMPLSLabel();
        bottomOutgoingMPLSLabel3.setBoS(true);
        bottomOutgoingMPLSLabel3.setEXP(0);
        bottomOutgoingMPLSLabel3.setLabel(123);
        bottomOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel3 = new TMPLSLabel();
        upperOutgoingMPLSLabel3.setBoS(false);
        upperOutgoingMPLSLabel3.setEXP(mplsPacket3.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel3.setLabel(1);
        upperOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        mplsPacket3.getLabelStack().pushTop(bottomOutgoingMPLSLabel3);
        mplsPacket3.getLabelStack().pushTop(upperOutgoingMPLSLabel3);

        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket2, 2);
        instance.addEntry(mplsPacket3, 3);

        TGPSRPRequestEntry auxEntry = instance.getEntry("10.0.0.1".hashCode(), mplsPacket2.getIPv4Header().getGoSGlobalUniqueIdentifier());

        assertEquals(2, auxEntry.getOutgoingPortID());
    }

    /**
     * Test of getEntry method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetEntryWhenNoEntryFound() {
        System.out.println("getEntry");

        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        instance.addEntry(mplsPacket1, 0);

        TGPSRPRequestEntry auxEntry = instance.getEntry("10.0.0.2".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());

        assertEquals(null, auxEntry);
    }

    /**
     * Test of updateEntries method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testUpdateEntries() {
        System.out.println("Test updateEntries");

        int numberOfEntries = 0;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);
        mplsPacket1.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.3");

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket2.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket2.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket2.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(2);
        TMPLSLabel bottomOutgoingMPLSLabel2 = new TMPLSLabel();
        bottomOutgoingMPLSLabel2.setBoS(true);
        bottomOutgoingMPLSLabel2.setEXP(0);
        bottomOutgoingMPLSLabel2.setLabel(123);
        bottomOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel2 = new TMPLSLabel();
        upperOutgoingMPLSLabel2.setBoS(false);
        upperOutgoingMPLSLabel2.setEXP(mplsPacket2.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel2.setLabel(1);
        upperOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(bottomOutgoingMPLSLabel2);
        mplsPacket2.getLabelStack().pushTop(upperOutgoingMPLSLabel2);
        mplsPacket2.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.3");

        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket3.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket3.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket3.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(3);
        TMPLSLabel bottomOutgoingMPLSLabel3 = new TMPLSLabel();
        bottomOutgoingMPLSLabel3.setBoS(true);
        bottomOutgoingMPLSLabel3.setEXP(0);
        bottomOutgoingMPLSLabel3.setLabel(123);
        bottomOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel3 = new TMPLSLabel();
        upperOutgoingMPLSLabel3.setBoS(false);
        upperOutgoingMPLSLabel3.setEXP(mplsPacket3.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel3.setLabel(1);
        upperOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        mplsPacket3.getLabelStack().pushTop(bottomOutgoingMPLSLabel3);
        mplsPacket3.getLabelStack().pushTop(upperOutgoingMPLSLabel3);
        mplsPacket3.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.3");

        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket2, 1);

        instance.decreaseTimeout(50000); // Attempts 8, Timeout = 0
        instance.updateEntries(); // Attempts 7, Timeout = 50.000
        instance.decreaseTimeout(50000); // // Attempts 7, Timeout = 0
        instance.updateEntries(); // Attempts 6, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 6, Timeout = 0
        instance.updateEntries(); // Attempts 5, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 5, Timeout = 0
        instance.updateEntries(); // Attempts 4, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 4, Timeout = 0
        instance.updateEntries(); // Attempts 3, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 3, Timeout = 0
        instance.updateEntries(); // Attempts 2, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 2, Timeout = 0
        instance.updateEntries(); // Attempts 1, Timeout = 50.000
        instance.addEntry(mplsPacket3, 2); // Added a new entry
        instance.decreaseTimeout(50000); // Attempts 1, Timeout = 0
        instance.updateEntries(); // Attempts 0, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 0, Timeout = 0        
        instance.updateEntries(); // Entry of mplsPacket 1 and 2 are now removed
        // entry for mplaPacket3 is still in the matrix
        Iterator<TGPSRPRequestEntry> iterator = instance.getEntriesIterator();
        while (iterator.hasNext()) {
            iterator.next();
            numberOfEntries++;
        }
        assertEquals(1, numberOfEntries);
    }

    /**
     * Test of decreaseTimeout method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testDecreaseTimeout() {
        System.out.println("Test decreaseTimeout");

        boolean worksFine = true;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);
        mplsPacket1.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.3");

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket2.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket2.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket2.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(2);
        TMPLSLabel bottomOutgoingMPLSLabel2 = new TMPLSLabel();
        bottomOutgoingMPLSLabel2.setBoS(true);
        bottomOutgoingMPLSLabel2.setEXP(0);
        bottomOutgoingMPLSLabel2.setLabel(123);
        bottomOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel2 = new TMPLSLabel();
        upperOutgoingMPLSLabel2.setBoS(false);
        upperOutgoingMPLSLabel2.setEXP(mplsPacket2.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel2.setLabel(1);
        upperOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(bottomOutgoingMPLSLabel2);
        mplsPacket2.getLabelStack().pushTop(upperOutgoingMPLSLabel2);
        mplsPacket2.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.3");

        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket3.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket3.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket3.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(3);
        TMPLSLabel bottomOutgoingMPLSLabel3 = new TMPLSLabel();
        bottomOutgoingMPLSLabel3.setBoS(true);
        bottomOutgoingMPLSLabel3.setEXP(0);
        bottomOutgoingMPLSLabel3.setLabel(123);
        bottomOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel3 = new TMPLSLabel();
        upperOutgoingMPLSLabel3.setBoS(false);
        upperOutgoingMPLSLabel3.setEXP(mplsPacket3.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel3.setLabel(1);
        upperOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        mplsPacket3.getLabelStack().pushTop(bottomOutgoingMPLSLabel3);
        mplsPacket3.getLabelStack().pushTop(upperOutgoingMPLSLabel3);
        mplsPacket3.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.3");

        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket2, 1);
        instance.addEntry(mplsPacket3, 2);

        instance.decreaseTimeout(45000); // Now, Timeout = 5.000 for all entries

        Iterator<TGPSRPRequestEntry> iterator1 = instance.getEntriesIterator();
        TGPSRPRequestEntry auxEntry = null;
        while (iterator1.hasNext()) {
            auxEntry = iterator1.next();
            auxEntry.decreaseTimeout(5000); // 45.000 has been decreased previously.
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 7, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // // Attempts 7, Timeout = 0
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 6, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // Attempts 6, Timeout = 0
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 5, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // Attempts 5, Timeout = 0
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 4, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // Attempts 4, Timeout = 0
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 3, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // Attempts 3, Timeout = 0
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 2, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // Attempts 2, Timeout = 0
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 1, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // Attempts 1, Timeout = 0
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Attempts 0, Timeout = 50.000
            auxEntry.decreaseTimeout(50000); // Attempts 0, Timeout = 0        
            auxEntry.resetTimeoutAndDecreaseAttempts(); // Entry is not retriable anymore.
            if (auxEntry.isRetriable()) {
                worksFine = false;
            }
        }
        assertTrue(worksFine);
    }

    /**
     * Test of decreaseTimeout method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testDecreaseTimeoutWhenOutOfRange() {
        System.out.println("Test decreaseTimeout");

        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);
        mplsPacket1.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.3");

        instance.addEntry(mplsPacket1, 0);

        assertThrows(RuntimeException.class, () -> {
            instance.decreaseTimeout(-1); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of getOutgoingPort method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetOutgoingPort() {
        System.out.println("getOutgoingPort");

        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket2.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket2.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket2.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(2);
        TMPLSLabel bottomOutgoingMPLSLabel2 = new TMPLSLabel();
        bottomOutgoingMPLSLabel2.setBoS(true);
        bottomOutgoingMPLSLabel2.setEXP(0);
        bottomOutgoingMPLSLabel2.setLabel(123);
        bottomOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel2 = new TMPLSLabel();
        upperOutgoingMPLSLabel2.setBoS(false);
        upperOutgoingMPLSLabel2.setEXP(mplsPacket2.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel2.setLabel(1);
        upperOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(bottomOutgoingMPLSLabel2);
        mplsPacket2.getLabelStack().pushTop(upperOutgoingMPLSLabel2);

        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket3.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket3.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket3.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(3);
        TMPLSLabel bottomOutgoingMPLSLabel3 = new TMPLSLabel();
        bottomOutgoingMPLSLabel3.setBoS(true);
        bottomOutgoingMPLSLabel3.setEXP(0);
        bottomOutgoingMPLSLabel3.setLabel(123);
        bottomOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel3 = new TMPLSLabel();
        upperOutgoingMPLSLabel3.setBoS(false);
        upperOutgoingMPLSLabel3.setEXP(mplsPacket3.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel3.setLabel(1);
        upperOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        mplsPacket3.getLabelStack().pushTop(bottomOutgoingMPLSLabel3);
        mplsPacket3.getLabelStack().pushTop(upperOutgoingMPLSLabel3);

        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket2, 1);
        instance.addEntry(mplsPacket3, 2);

        int packet1GoSGlobalID = mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier();
        int packet2GoSGlobalID = mplsPacket2.getIPv4Header().getGoSGlobalUniqueIdentifier();
        int packet3GoSGlobalID = mplsPacket3.getIPv4Header().getGoSGlobalUniqueIdentifier();
        
        int outgoingPortID1 = instance.getOutgoingPort("10.0.0.1".hashCode(), packet1GoSGlobalID);
        int outgoingPortID2 = instance.getOutgoingPort("10.0.0.1".hashCode(), packet2GoSGlobalID);
        int outgoingPortID3 = instance.getOutgoingPort("10.0.0.1".hashCode(), packet3GoSGlobalID);
        
        
        assertTrue((outgoingPortID1 == 0) || (outgoingPortID2 == 1) || (outgoingPortID3 == 2));
    }

    /**
     * Test of getOutgoingPort method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetOutgoingPortWhenEntryNotFound() {
        System.out.println("getOutgoingPort");

        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        instance.addEntry(mplsPacket1, 0);

        assertEquals(-1, instance.getOutgoingPort("10.0.0.10".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier()));
    }

    /**
     * Test of getNearestCossedActiveNodeIPv4 method, of class
     * TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetNearestCossedActiveNodeIPv4() {
        System.out.println("Test getNearestCossedActiveNodeIPv4");

        boolean worksFine = true;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.10", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);
        mplsPacket1.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.2");
        mplsPacket1.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.4");
        mplsPacket1.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.6");

        instance.addEntry(mplsPacket1, 0);

        String auxIPv4Address = null;

        auxIPv4Address = instance.getNextNearestCrossedActiveNodeIPv4("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        if (auxIPv4Address == null) {
            worksFine = false;
        } else {
            if (!auxIPv4Address.equals("10.0.0.6")) {
                worksFine = false;
            } else {
                worksFine &= true;
            }
        }
        auxIPv4Address = instance.getNextNearestCrossedActiveNodeIPv4("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        if (auxIPv4Address == null) {
            worksFine = false;
        } else {
            if (!auxIPv4Address.equals("10.0.0.4")) {
                worksFine = false;
            } else {
                worksFine &= true;
            }
        }
        auxIPv4Address = instance.getNextNearestCrossedActiveNodeIPv4("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        if (auxIPv4Address == null) {
            worksFine = false;
        } else {
            if (!auxIPv4Address.equals("10.0.0.2")) {
                worksFine = false;
            } else {
                worksFine &= true;
            }
        }
        assertTrue(worksFine);
    }

    /**
     * Test of getNearestCossedActiveNodeIPv4 method, of class
     * TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetNearestCossedActiveNodeIPv4WhenEntryNotFound() {
        System.out.println("Test getNearestCossedActiveNodeIPv4");

        boolean worksFine = true;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.10", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);
        mplsPacket1.getIPv4Header().getOptionsField().setCrossedActiveNode("10.0.0.2");

        instance.addEntry(mplsPacket1, 0);

        assertEquals(null, instance.getNextNearestCrossedActiveNodeIPv4("10.0.0.50".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier()));
    }

    /**
     * Test of getEntriesIterator method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetEntriesIterator() {
        System.out.println("Test getEntriesIterator");

        boolean worksFine = true;
        int numberOfEntries = 0;
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();

        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket1.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket1.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket1.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(1);
        TMPLSLabel bottomOutgoingMPLSLabel1 = new TMPLSLabel();
        bottomOutgoingMPLSLabel1.setBoS(true);
        bottomOutgoingMPLSLabel1.setEXP(0);
        bottomOutgoingMPLSLabel1.setLabel(123);
        bottomOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel1 = new TMPLSLabel();
        upperOutgoingMPLSLabel1.setBoS(false);
        upperOutgoingMPLSLabel1.setEXP(mplsPacket1.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel1.setLabel(1);
        upperOutgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(bottomOutgoingMPLSLabel1);
        mplsPacket1.getLabelStack().pushTop(upperOutgoingMPLSLabel1);

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket2.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket2.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket2.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(2);
        TMPLSLabel bottomOutgoingMPLSLabel2 = new TMPLSLabel();
        bottomOutgoingMPLSLabel2.setBoS(true);
        bottomOutgoingMPLSLabel2.setEXP(0);
        bottomOutgoingMPLSLabel2.setLabel(123);
        bottomOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel2 = new TMPLSLabel();
        upperOutgoingMPLSLabel2.setBoS(false);
        upperOutgoingMPLSLabel2.setEXP(mplsPacket2.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel2.setLabel(1);
        upperOutgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(bottomOutgoingMPLSLabel2);
        mplsPacket2.getLabelStack().pushTop(upperOutgoingMPLSLabel2);

        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 100);
        mplsPacket3.setSubtype(TAbstractPDU.MPLS_GOS);
        mplsPacket3.getIPv4Header().getOptionsField().setRequestedGoSLevel(7);
        mplsPacket3.getIPv4Header().getOptionsField().setPacketLocalUniqueIdentifier(3);
        TMPLSLabel bottomOutgoingMPLSLabel3 = new TMPLSLabel();
        bottomOutgoingMPLSLabel3.setBoS(true);
        bottomOutgoingMPLSLabel3.setEXP(0);
        bottomOutgoingMPLSLabel3.setLabel(123);
        bottomOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        TMPLSLabel upperOutgoingMPLSLabel3 = new TMPLSLabel();
        upperOutgoingMPLSLabel3.setBoS(false);
        upperOutgoingMPLSLabel3.setEXP(mplsPacket3.getIPv4Header().getOptionsField().getRequestedGoSLevel());
        upperOutgoingMPLSLabel3.setLabel(1);
        upperOutgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        mplsPacket3.getLabelStack().pushTop(bottomOutgoingMPLSLabel3);
        mplsPacket3.getLabelStack().pushTop(upperOutgoingMPLSLabel3);

        instance.addEntry(mplsPacket1, 0);
        instance.addEntry(mplsPacket2, 1);
        instance.addEntry(mplsPacket3, 2);

        Iterator<TGPSRPRequestEntry> iterator1 = instance.getEntriesIterator();
        TGPSRPRequestEntry auxEntry = null;
        while (iterator1.hasNext()) {
            auxEntry = iterator1.next();
            if (auxEntry != null) {
                numberOfEntries++;
            } else {
                worksFine &= false;
            }
        }

        assertTrue((numberOfEntries == 3) && (worksFine));
    }

    /**
     * Test of getMonitor method, of class TGPSRPRequestsMatrix.
     */
    @Test
    public void testGetMonitor() {
        System.out.println("getMonitor");
        TGPSRPRequestsMatrix instance = new TGPSRPRequestsMatrix();
        TSemaphore semaphoreAux = instance.getMonitor();
        assertTrue(semaphoreAux instanceof TSemaphore);
    }

}
