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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 */
public class TGPSRPRequestEntryTest {

    public TGPSRPRequestEntryTest() {
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
     * Test of constructor method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testConstructor() {
        System.out.println("Test constructor");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertEquals(arrivalOrder, instance.getArrivalOrder());
    }

    /**
     * Test of constructor method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testConstructorWhenNegative() {
        System.out.println("Test constructor");
        int arrivalOrder = -1;
        assertThrows(IllegalArgumentException.class, () -> {
            TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);  // Should throw an exception
        });
    }

    /**
     * Test of getArrivalOrder method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetArrivalOrder() {
        System.out.println("Test getArrivalOrder()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertEquals(arrivalOrder, instance.getArrivalOrder());
    }

    /**
     * Test of setFlowID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetFlowID() {
        System.out.println("Test setFlowID()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setFlowID(1234);
        assertEquals(1234, instance.getFlowID());
    }

    /**
     * Test of getFlowID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetFlowID() {
        System.out.println("Test getFlowID()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setFlowID(3245);
        assertEquals(3245, instance.getFlowID());
    }

    /**
     * Test of getFlowID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetFlowIDWhenNotInitialized() {
        System.out.println("Test getFlowID()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getFlowID(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setGoSGlobalUniqueIdentifier method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetGoSGlobalUniqueIdentifier() {
        System.out.println("Test testSetGoSGlobalUniqueIdentifier()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setGlobalUniqueIdentifier("AGoSGlobalUniqueIdentifier".hashCode());
        assertEquals("AGoSGlobalUniqueIdentifier".hashCode(), instance.getGlobalUniqueIdentifier());
    }

    /**
     * Test of getGoSGlobalUniqueIdentifier method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetGoSGlobalUniqueIdentifier() {
        System.out.println("Test getGoSGlobalUniqueIdentifier()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setGlobalUniqueIdentifier("AGoSGlobalUniqueIdentifier".hashCode());
        assertEquals("AGoSGlobalUniqueIdentifier".hashCode(), instance.getGlobalUniqueIdentifier());
    }

    /**
     * Test of getGoSGlobalUniqueIdentifier method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetGoSGlobalUniqueIdentifierWhenNotInitialized() {
        System.out.println("Test getGoSGlobalUniqueIdentifier()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getGlobalUniqueIdentifier(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setOutgoingPortID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetOutgoingPortID() {
        System.out.println("Test setOutgoingPortID");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setOutgoingPortID(1);
        assertEquals(1, instance.getOutgoingPortID());
    }

    /**
     * Test of setOutgoingPortID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetOutgoingPortIDWhenNegative() {
        System.out.println("Test setOutgoingPortID");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setOutgoingPortID(-1);  // Should throw an exception
        });
    }

    /**
     * Test of getOutgoingPortID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetOutgoingPortID() {
        System.out.println("Test getOutgoingPortID");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setOutgoingPortID(1);
        assertEquals(1, instance.getOutgoingPortID());
    }

    /**
     * Test of getOutgoingPortID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetOutgoingPortIDWhenNotinitialized() {
        System.out.println("Test getOutgoingPortID");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getOutgoingPortID(); // Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setCrossedNodeIP method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetCrossedNodeIP() {
        System.out.println("Test setCrossedNodeIP");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        assertEquals("10.0.0.1", instance.getNearestCossedActiveNodeIPv4());
    }

    /**
     * Test of setCrossedNodeIP method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetSomeCrossedNodeIP() {
        System.out.println("Test setCrossedNodeIP");
        boolean worksFine = true;
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        instance.setCrossedNodeIP("10.0.0.2");
        if (!instance.getNearestCossedActiveNodeIPv4().equals("10.0.0.2")) {
            worksFine = false;
        }
        if (!instance.getNearestCossedActiveNodeIPv4().equals("10.0.0.1")) {
            worksFine = false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of setCrossedNodeIP method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetCrossedNodeIPWhenEmpty() {
        System.out.println("Test setCrossedNodeIP");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.setCrossedNodeIP("");// Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setCrossedNodeIP method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetCrossedNodeIPWhenNull() {
        System.out.println("Test setCrossedNodeIP");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.setCrossedNodeIP(null);// Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of setCrossedNodeIP method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetCrossedNodeIPWhenIncorrectIP() {
        System.out.println("Test setCrossedNodeIP");
        boolean worksFine = true;
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        try {
            instance.setCrossedNodeIP("This is not a valid IP");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.0.0.0");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.255.255.255");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.256.0.0");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.0.256.0");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.0.0.256");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("12.0.0.1");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.-1.0.0");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.0.-1.0");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        try {
            instance.setCrossedNodeIP("10.0.0.-1");
            worksFine = false;
        } catch (RuntimeException ex) {
            worksFine &= true;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of getNextNearestCrossedNodeIPv4 method, of class
     * TGPSRPRequestEntry.
     */
    @Test
    public void testGetNextNearestCrossedNodeIPv4() {
        System.out.println("Test getNextNearestCrossedNodeIPv4()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        assertTrue(instance.getNearestCossedActiveNodeIPv4().equals("10.0.0.1"));
    }

    /**
     * Test of getNextNearestCrossedNodeIPv4 method, of class
     * TGPSRPRequestEntry.
     */
    @Test
    public void testGetNextNearestCrossedNodeIPv4WhenNotInitialized() {
        System.out.println("Test getNextNearestCrossedNodeIPv4()");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.getNearestCossedActiveNodeIPv4();// Should throw an exception. It's not initialized
        });
    }

    /**
     * Test of decreaseTimeout method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testDecreaseTimeout() {
        System.out.println("Test decreaseTimeout");
        boolean result = true;
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        instance.decreaseTimeout(50000); // Attempts 8, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 7, Timeout = 50.000
        instance.decreaseTimeout(50000); // // Attempts 7, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 6, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 6, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 5, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 5, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 4, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 4, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 3, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 3, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 2, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 2, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 1, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 1, Timeout = 0
        result &= instance.isRetriable(); // True
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 0, Timeout = 50.000
        // The test really starts here, when attempts = 0 and timeout = 50.000
        instance.decreaseTimeout(10000); // Attempts 0, Timeout = 40.000
        instance.decreaseTimeout(10000); // Attempts 0, Timeout = 30.000
        instance.decreaseTimeout(10000); // Attempts 0, Timeout = 20.000
        instance.decreaseTimeout(10000); // Attempts 0, Timeout = 10.000
        instance.decreaseTimeout(500); // Attempts 0, Timeout = 9.500
        instance.decreaseTimeout(1500); // Attempts 0, Timeout = 8.000
        instance.decreaseTimeout(1234); // Attempts 0, Timeout = 6.766
        instance.decreaseTimeout(6766); // Attempts 0, Timeout = 0
        result &= !instance.isRetriable();  // !false --> true
        assertTrue(!instance.isRetriable() && result); // (!false && true) = true
    }

    /**
     * Test of decreaseTimeout method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testDecreaseTimeoutWhenNegative() {
        System.out.println("Test decreaseTimeout");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        assertThrows(RuntimeException.class, () -> {
            instance.decreaseTimeout(-1); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of resetTimeoutAndDecreaseAttempts method, of class
     * TGPSRPRequestEntry.
     */
    @Test
    public void testResetTimeoutAndDecreaseAttempts() {
        System.out.println("Test resetTimeoutAndDecreaseAttempts");
        boolean result = true;
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        instance.decreaseTimeout(50000); // Attempts 8, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 7, Timeout = 50.000
        instance.decreaseTimeout(50000); // // Attempts 7, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 6, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 6, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 5, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 5, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 4, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 4, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 3, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 3, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 2, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 2, Timeout = 0
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 1, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 1, Timeout = 0
        result &= instance.isRetriable();
        instance.resetTimeoutAndDecreaseAttempts(); // Attempts 0, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 0, Timeout = 0
        assertTrue(!instance.isRetriable() && result); // (!false && true) = true
    }

    /**
     * Test of forceTimeoutReset method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testForceTimeoutReset() {
        System.out.println("Test forceTimeoutReset");
        boolean result = true;
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        instance.forceTimeoutReset(); // Attempts 7, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 6, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 5, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 4, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 3, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 2, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 1, Timeout = 50.000
        instance.decreaseTimeout(50000); // Attempts 1, Timeout = 0
        result &= instance.isRetriable(); // true
        instance.forceTimeoutReset(); // Attempts 0, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 0, Timeout = 0
        instance.forceTimeoutReset(); // Still Attempts 0, Timeout = 0
        assertTrue(!instance.isRetriable() && result); // (!false && true) = true
    }

    /**
     * Test of isRetriable method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testIsRetriableWhenNotRetriable() {
        System.out.println("Test isRetriable");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        // At this poing, Attempts = 8 and Timeout = 50.000 so, not retriable
        assertFalse(instance.isRetriable());
    }

    /**
     * Test of isRetriable method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testIsRetriableWhenNotCrossedNodes() {
        System.out.println("Test isRetriable");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.decreaseTimeout(50000);
        // At this poing, Attempts = 8 and Timeout = 0 so, it should be retriable
        // but it is not because there are not crossed nodes.
        assertFalse(instance.isRetriable()); // (!false == true)
    }

    /**
     * Test of isRetriable method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testIsRetriableWhenRetriable() {
        System.out.println("Test isRetriable");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        instance.decreaseTimeout(50000); // Attempts 8, Timeout = 0 --> Retriable
        assertTrue(instance.isRetriable());
    }

    /**
     * Test of canBePurged method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testCanBePurgedWhenNotTimeoutAndNotAttempts() {
        System.out.println("Test canBePurged");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        instance.forceTimeoutReset(); // Attempts 7, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 6, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 5, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 4, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 3, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 2, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 1, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 0, Timeout = 50.000
        instance.forceTimeoutReset(); // Attempts 0, Timeout = 0
        // crossed nodes, but no attempts and no timeout --> can be purged
        assertTrue(instance.canBePurged());
    }

    /**
     * Test of canBePurged method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testCanBePurgedWhenCannot() {
        System.out.println("Test canBePurged");
        boolean result = false;
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        instance.setCrossedNodeIP("10.0.0.1");
        // There are crossed nodes
        result |= instance.canBePurged(); // Attempts 8, Timeout = 50.000 --> Cannot be purged
        instance.decreaseTimeout(50000); // Attempts 8, Timeout = 0 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 7, Timeout = 50.000 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 6, Timeout = 50.000 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 5, Timeout = 50.000 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 4, Timeout = 50.000 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 3, Timeout = 50.000 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 2, Timeout = 50.000 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 1, Timeout = 50.000 --> Cannot be purged
        result |= instance.canBePurged(); // Cannot be purged
        instance.forceTimeoutReset(); // Attempts 0, Timeout = 50.000 --> Cannot be purged
        assertFalse(instance.canBePurged() || result);
    }

    /**
     * Test of canBePurged method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testCanBePurgedWhenNotCrossedNodes() {
        System.out.println("Test canBePurged");
        int arrivalOrder = 345; // It could be any other
        TGPSRPRequestEntry instance = new TGPSRPRequestEntry(arrivalOrder);
        // Not crossed nodes --> can be purged
        assertTrue(instance.canBePurged());
    }

    /**
     * Test of compareTo method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testCompareTo() {
        System.out.println("Test compareTo");
        boolean worksFineInAllCases = true;
        TGPSRPRequestEntry anotherTGPSRPRequestEntry = new TGPSRPRequestEntry(1);
        TGPSRPRequestEntry instance1 = new TGPSRPRequestEntry(2);
        TGPSRPRequestEntry instance2 = new TGPSRPRequestEntry(2);
        if (instance1.compareTo(anotherTGPSRPRequestEntry) != 1) {
            worksFineInAllCases = false;
        }
        if (anotherTGPSRPRequestEntry.compareTo(instance1) != -1) {
            worksFineInAllCases = false;
        }
        if (instance1.compareTo(instance2) != 0) {
            worksFineInAllCases = false;
        }
        assertEquals(true, worksFineInAllCases);
    }
}
