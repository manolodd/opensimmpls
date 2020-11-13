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

import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 */
public class TDMGPEntryTest {

    public TDMGPEntryTest() {
    }

    public static void setUpClass() throws Exception {
    }

    public static void tearDownClass() throws Exception {
    }

    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    /**
     * Test of constructor method, of class TDMGPEntry.
     */
    @Test
    public void testConstructor() {
        System.out.println("Test constructor");
        int arrivalOrder = 345; // It could be any other
        TDMGPEntry instance = new TDMGPEntry(arrivalOrder);
        assertEquals(arrivalOrder, instance.getArrivalOrder());
    }

    /**
     * Test of constructor method, of class TDMGPEntry.
     */
    @Test
    public void testConstructorWhenNegative() {
        System.out.println("Test constructor");
        int arrivalOrder = -1;
        assertThrows(IllegalArgumentException.class, () -> {
            TDMGPEntry instance = new TDMGPEntry(arrivalOrder);  // Should throw an exception
        });
    }

    /**
     * Test of getPacketGoSGlobalUniqueIdentifier method, of class TDMGPEntry.
     */
    @Test
    public void testGetPacketGoSGlobalUniqueIdentifier() {
        System.out.println("Test getPacketGoSGlobalUniqueIdentifier()");
        TDMGPEntry instance = new TDMGPEntry(345); // It could be any other
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        instance.setPacket(mplsPacket1);
        int result = instance.getPacketGoSGlobalUniqueIdentifier();
        int expectedGoSGlobalUniqueIdentifier = mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier();
        assertEquals(expectedGoSGlobalUniqueIdentifier, result);
    }

    /**
     * Test of getPacketGoSGlobalUniqueIdentifier method, of class TDMGPEntry.
     */
    @Test
    public void testGetPacketGoSGlobalUniqueIdentifierWhenNotInitialized() {
        System.out.println("Test getPacketGoSGlobalUniqueIdentifier()");
        TDMGPEntry instance = new TDMGPEntry(345); // It could be any other
        assertThrows(RuntimeException.class, () -> {
            instance.getPacketGoSGlobalUniqueIdentifier(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of getPacketGoSGlobalUniqueIdentifier method, of class TDMGPEntry.
     */
    @Test
    public void testGetPacketClone() {
        System.out.println("Test getPacketClone()");
        TDMGPEntry instance = new TDMGPEntry(345); // It could be any other
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        instance.setPacket(mplsPacket1);
        TMPLSPDU returnedPacket = instance.getPacketClone();
        boolean worksFine = true;
        if (returnedPacket == null) {
            worksFine = false;
        } else if (!returnedPacket.getIPv4Header().getOriginIPv4Address().equals("10.0.0.1")) {
            worksFine = false;
        } else if (!returnedPacket.getIPv4Header().getTailEndIPAddress().equals("10.0.0.2")) {
            worksFine = false;
        } else if (returnedPacket.getTCPPayload().getSize() != 1044) { // Inludes TPC header
            worksFine = false;
        } else if (returnedPacket.getID() != 1) {
            worksFine = false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of getPacket method, of class TDMGPEntry.
     */
    @Test
    public void testGetPacketCloneWhenNotInitialized() {
        System.out.println("Test getPacketClone()");
        TDMGPEntry instance = new TDMGPEntry(345); // It could be any other
        assertThrows(RuntimeException.class, () -> {
            instance.getPacketClone(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setPacket method, of class TDMGPEntry.
     */
    @Test
    public void testSetPacket() {
        System.out.println("Test setPacket()");
        TDMGPEntry instance = new TDMGPEntry(345); // It could be any other
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        instance.setPacket(mplsPacket1);
        TMPLSPDU returnedPacket = instance.getPacketClone();
        boolean worksFine = true;
        if (returnedPacket == null) {
            worksFine = false;
        } else if (!returnedPacket.getIPv4Header().getOriginIPv4Address().equals("10.0.0.1")) {
            worksFine = false;
        } else if (!returnedPacket.getIPv4Header().getTailEndIPAddress().equals("10.0.0.2")) {
            worksFine = false;
        } else if (returnedPacket.getTCPPayload().getSize() != 1044) { // Inludes TPC header
            worksFine = false;
        } else if (returnedPacket.getID() != 1) {
            worksFine = false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of setPacket method, of class TDMGPEntry.
     */
    @Test
    public void testSetPacketWhenNull() {
        System.out.println("Test setPacket()");
        int arrivalOrder = 345; // It could be any other
        TDMGPEntry instance = new TDMGPEntry(arrivalOrder);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPacket(null); // Should throw an exception
        });
    }

    /**
     * Test of getArrivalOrder method, of class TDMGPEntry.
     */
    @Test
    public void testGetArrivalOrder() {
        int arrivalOrder = 345; // It could be any other
        TDMGPEntry instance = new TDMGPEntry(arrivalOrder);
        assertEquals(arrivalOrder, instance.getArrivalOrder());
    }

    /**
     * Test of compareTo method, of class TDMGPEntry.
     */
    @Test
    public void testCompareTo() {
        System.out.println("Test compareTo()");
        boolean worksFineInAllCases = true;
        TDMGPEntry anotherDMGPEntry = new TDMGPEntry(1);
        TDMGPEntry instance1 = new TDMGPEntry(2);
        TDMGPEntry instance2 = new TDMGPEntry(2);
        if (instance1.compareTo(anotherDMGPEntry) != 1) {
            worksFineInAllCases = false;
        }
        if (anotherDMGPEntry.compareTo(instance1) != -1) {
            worksFineInAllCases = false;
        }
        if (instance1.compareTo(instance2) != 0) {
            worksFineInAllCases = false;
        }
        assertEquals(true, worksFineInAllCases);
    }

    /**
     * Test of compareTo method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testCompareToWhenNull() {
        System.out.println("Test compareTo");
        TDMGPEntry instance = new TDMGPEntry(1);
        assertThrows(RuntimeException.class, () -> {
            instance.compareTo(null); // Not possible. Should throws an exception
        });
    }

}
