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
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
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
public class TDMGPFlowEntryTest {

    public TDMGPFlowEntryTest() {
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
     * Test of constructor method, of class TDMGPFlowEntry.
     */
    @Test
    public void testConstructor() {
        System.out.println("Test constructor");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertEquals(arrivalOrder, instance.getArrivalOrder());
    }

    /**
     * Test of constructor method, of class TDMGPFlowEntry.
     */
    @Test
    public void testConstructorWhenNegative() {
        System.out.println("Test constructor");
        int arrivalOrder = -1;
        assertThrows(IllegalArgumentException.class, () -> {
            TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);  // Should throw an exception
        });
    }

    /**
     * Test of setFlowID method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetFlowID() {
        System.out.println("Test setFlowID()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setFlowID(1234);
        assertEquals(1234, instance.getFlowID());
    }

    /**
     * Test of getFlowID method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetFlowID() {
        System.out.println("Test getFlowID()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setFlowID(3245);
        assertEquals(3245, instance.getFlowID());
    }

    /**
     * Test of getFlowID method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetFlowIDWhenNotInitialized() {
        System.out.println("Test setFlowID()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getFlowID(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setAssignedPercentage method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetAssignedPercentage() {
        System.out.println("Test setAssignedPercentaje()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setAssignedPercentage(0);
        assertEquals(0, instance.getAssignedPercentage());
    }

    /**
     * Test of setAssignedPercentage method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetAssignedPercentageWhenNegative() {
        System.out.println("Test setAssignedPercentaje()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setAssignedPercentage(-1); // Should throw an exception
        });
    }

    /**
     * Test of setAssignedPercentage method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetAssignedPercentageWhenGreaterThan100() {
        System.out.println("Test setAssignedPercentaje()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setAssignedPercentage(101); // Should throw an exception. Max allowed is 100
        });
    }

    /**
     * Test of getAssignedPercentage method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetAssignedPercentage() {
        System.out.println("Test setAssignedPercentaje()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setAssignedPercentage(0);
        assertEquals(0, instance.getAssignedPercentage());
    }

    /**
     * Test of getAssignedPercentage method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetAssignedPercentageWhenNotInitialized() {
        System.out.println("Test setAssignedPercentaje()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getAssignedPercentage(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setAssignedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetAssignedOctects() {
        System.out.println("Test setAssignedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setAssignedOctets(0);
        assertEquals(0, instance.getAssignedOctets());
    }

    /**
     * Test of setAssignedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetAssignedOctectsWhenNegative() {
        System.out.println("Test setAssignedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setAssignedOctets(-1); // Should throw an exception
        });
    }

    /**
     * Test of getAssignedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetAssignedOctects() {
        System.out.println("Test getAssignedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setAssignedOctets(0);
        assertEquals(0, instance.getAssignedOctets());
    }

    /**
     * Test of getAssignedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetAssignedOctectsWhenNotInitialized() {
        System.out.println("Test getAssignedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getAssignedOctets(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setUsedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetUsedOctects() {
        System.out.println("Test setUsedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setUsedOctets(0);
        assertEquals(0, instance.getUsedOctets());
    }

    /**
     * Test of setUsedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testSetUsedOctectsWhenNegative() {
        System.out.println("Test setUsedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setUsedOctets(-1); // Should throw an exception
        });
    }

    /**
     * Test of getUsedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetUsedOctects() {
        System.out.println("Test getUsedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        instance.setUsedOctets(0);
        assertEquals(0, instance.getUsedOctets());
    }

    /**
     * Test of getUsedOctects method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetUsedOctectsWhenNotInitialized() {
        System.out.println("Test getUsedOctects()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getUsedOctets(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of getEntries method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetEntries() {
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSPDU mplsPacket4 = new TMPLSPDU(4, "10.0.0.1", "10.0.0.2", 1024);
        TDMGPFlowEntry instance = new TDMGPFlowEntry(465);
        instance.setAssignedOctets(1024 * 1024);
        instance.setAssignedPercentage(100);
        instance.setUsedOctets(0);
        instance.setFlowID("10.0.0.1".hashCode());
        instance.addPacket(mplsPacket1);
        instance.addPacket(mplsPacket2);
        instance.addPacket(mplsPacket3);
        instance.addPacket(mplsPacket4);
        assertTrue(instance.getEntries().size() == 4);
    }

    /**
     * Test of getEntries method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetEntriesWhenEmpty() {
        TDMGPFlowEntry instance = new TDMGPFlowEntry(465);
        assertTrue(instance.getEntries().isEmpty());
    }

    /**
     * Test of getArrivalOrder method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetArrivalOrder() {
        System.out.println("Test getArrivalOrder");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertEquals(arrivalOrder, instance.getArrivalOrder());

    }

    /**
     * Test of getMonitor method, of class TDMGPFlowEntry.
     */
    @Test
    public void testGetSemaphore() {
        System.out.println("Test getSemaphore()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertTrue(instance.getSemaphore() instanceof TSemaphore);
    }

    /**
     * Test of addPacket method, of class TDMGPFlowEntry.
     */
    @Test
    public void testAddPacket() {
        System.out.println("Test addPacket()");
        boolean worksFine = true;
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSPDU auxMPLSPacket;
        TDMGPFlowEntry instance = new TDMGPFlowEntry(465);
        instance.setAssignedOctets(1024 * 1024);
        instance.setAssignedPercentage(100);
        instance.setUsedOctets(0);
        instance.setFlowID("10.0.0.1".hashCode());
        instance.addPacket(mplsPacket1);
        if (instance.getEntries().size() != 1) {
            worksFine = false;
        }
        for (TDMGPEntry entry : instance.getEntries()) {
            auxMPLSPacket = entry.getPacket();
            if (auxMPLSPacket.getID() == 1) {
                if (!auxMPLSPacket.getIPv4Header().getOriginIPv4Address().equals("10.0.0.1")) {
                    worksFine = false;
                }
                if (!auxMPLSPacket.getIPv4Header().getTailEndIPAddress().equals("10.0.0.2")) {
                    worksFine = false;
                }
                if (auxMPLSPacket.getSize() != 1064) { // TCP Payload + IPv4 header + MPLS label stack
                    worksFine = false;
                }
                if (instance.getUsedOctets() != 1064) { // TCP Payload + IPv4 header + MPLS label stack
                    worksFine = false;
                }
                if (instance.getAssignedOctets() != (1024 * 1024)) {
                    worksFine = false;
                }
                if (instance.getAssignedPercentage() != 100) {
                    worksFine = false;
                }
                if (instance.getArrivalOrder() != 465) {
                    worksFine = false;
                }
                if (instance.getFlowID() != "10.0.0.1".hashCode()) {
                    worksFine = false;
                }
            } else {
                worksFine = false;
            }
        }
        assertTrue(worksFine);
    }

    /**
     * Test of addPacket method, of class TDMGPFlowEntry.
     */
    @Test
    public void testAddPacketWhenMemoryFull() {
        System.out.println("Test addPacket()");
        boolean worksFine = true;
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1100);
        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 1100);
        TMPLSPDU auxMPLSPacket;
        TDMGPFlowEntry instance = new TDMGPFlowEntry(465);
        instance.setAssignedOctets(1024 * 2);
        instance.setAssignedPercentage(100);
        instance.setUsedOctets(0);
        instance.setFlowID("10.0.0.1".hashCode());
        instance.addPacket(mplsPacket1); // This packet can be added
        instance.addPacket(mplsPacket2); // No enough memory to store this packet. mplsPacket 1 is removed before.
        // At this point, only mplsPacket2 should be in the TDMGPFlowEntry
        if (instance.getEntries().size() != 1) {
            worksFine = false;
        }
        for (TDMGPEntry entry : instance.getEntries()) {
            auxMPLSPacket = entry.getPacket();
            if (auxMPLSPacket.getID() == 2) {
                if (!auxMPLSPacket.getIPv4Header().getOriginIPv4Address().equals("10.0.0.1")) {
                    worksFine = false;
                }
                if (!auxMPLSPacket.getIPv4Header().getTailEndIPAddress().equals("10.0.0.2")) {
                    worksFine = false;
                }
                if (auxMPLSPacket.getSize() != 1140) { // TCP Payload + IPv4 header + MPLS label stack
                    worksFine = false;
                }
                if (instance.getUsedOctets() != 1140) { // TCP Payload + IPv4 header + MPLS label stack
                    worksFine = false;
                }
                if (instance.getAssignedOctets() != (1024 * 2)) {
                    worksFine = false;
                }
                if (instance.getAssignedPercentage() != 100) {
                    worksFine = false;
                }
                if (instance.getArrivalOrder() != 465) {
                    worksFine = false;
                }
                if (instance.getFlowID() != "10.0.0.1".hashCode()) {
                    worksFine = false;
                }
            } else {
                worksFine = false;
            }
        }
        assertTrue(worksFine);
    }

    /**
     * Test of addPacket method, of class TDMGPFlowEntry.
     */
    @Test
    public void testAddPacketWhenNull() {
        System.out.println("Test addPacket()");
        int arrivalOrder = 345; // It could be any other
        TDMGPFlowEntry instance = new TDMGPFlowEntry(arrivalOrder);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.addPacket(null); // Should throw an exception
        });
    }

    /**
     * Test of compareTo method, of class TDMGPFlowEntry.
     */
    @Test
    public void testCompareTo() {
        System.out.println("Test compareTo");
        boolean worksFineInAllCases = true;
        TDMGPFlowEntry anotherDMGPFlowEntry = new TDMGPFlowEntry(1);
        TDMGPFlowEntry instance1 = new TDMGPFlowEntry(2);
        TDMGPFlowEntry instance2 = new TDMGPFlowEntry(2);
        if (instance1.compareTo(anotherDMGPFlowEntry) != 1) {
            worksFineInAllCases = false;
        }
        if (anotherDMGPFlowEntry.compareTo(instance1) != -1) {
            worksFineInAllCases = false;
        }
        if (instance1.compareTo(instance2) != 0) {
            worksFineInAllCases = false;
        }
        assertEquals(true, worksFineInAllCases);
    }

}
