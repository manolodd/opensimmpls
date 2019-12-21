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
 * @author manolodd
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
     * Test of getArrivalOrder method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetArrivalOrder() {
        System.out.println("getArrivalOrder");
        TGPSRPRequestEntry instance = null;
        int expResult = 0;
        int result = instance.getArrivalOrder();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFlowID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetFlowID() {
        System.out.println("setFlowID");
        int flowID = 0;
        TGPSRPRequestEntry instance = null;
        instance.setFlowID(flowID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFlowID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetFlowID() {
        System.out.println("getFlowID");
        TGPSRPRequestEntry instance = null;
        int expResult = 0;
        int result = instance.getFlowID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setGoSGlobalUniqueIdentifier method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetGoSGlobalUniqueIdentifier() {
        System.out.println("setGoSGlobalUniqueIdentifier");
        int gosGlobalUniqueIdentifier = 0;
        TGPSRPRequestEntry instance = null;
        instance.setGoSGlobalUniqueIdentifier(gosGlobalUniqueIdentifier);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGoSGlobalUniqueIdentifier method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetGoSGlobalUniqueIdentifier() {
        System.out.println("getGoSGlobalUniqueIdentifier");
        TGPSRPRequestEntry instance = null;
        int expResult = 0;
        int result = instance.getGoSGlobalUniqueIdentifier();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setOutgoingPortID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetOutgoingPortID() {
        System.out.println("setOutgoingPortID");
        int outgoingPortID = 0;
        TGPSRPRequestEntry instance = null;
        instance.setOutgoingPortID(outgoingPortID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOutgoingPortID method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetOutgoingPortID() {
        System.out.println("getOutgoingPortID");
        TGPSRPRequestEntry instance = null;
        int expResult = 0;
        int result = instance.getOutgoingPortID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCrossedNodeIP method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testSetCrossedNodeIP() {
        System.out.println("setCrossedNodeIP");
        String crossedNodeIP = "";
        TGPSRPRequestEntry instance = null;
        instance.setCrossedNodeIP(crossedNodeIP);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextNearestCrossedNodeIPv4 method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testGetNextNearestCrossedNodeIPv4() {
        System.out.println("getNextNearestCrossedNodeIPv4");
        TGPSRPRequestEntry instance = null;
        String expResult = "";
        String result = instance.getNextNearestCrossedNodeIPv4();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decreaseTimeout method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testDecreaseTimeout() {
        System.out.println("decreaseTimeout");
        int nanosecondsToDecrease = 0;
        TGPSRPRequestEntry instance = null;
        instance.decreaseTimeout(nanosecondsToDecrease);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetTimeoutAndDecreaseAttempts method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testResetTimeoutAndDecreaseAttempts() {
        System.out.println("resetTimeoutAndDecreaseAttempts");
        TGPSRPRequestEntry instance = null;
        instance.resetTimeoutAndDecreaseAttempts();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of forceTimeoutReset method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testForceTimeoutReset() {
        System.out.println("forceTimeoutReset");
        TGPSRPRequestEntry instance = null;
        instance.forceTimeoutReset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isRetryable method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testIsRetryable() {
        System.out.println("isRetryable");
        TGPSRPRequestEntry instance = null;
        boolean expResult = false;
        boolean result = instance.isRetryable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canBePurged method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testCanBePurged() {
        System.out.println("canBePurged");
        TGPSRPRequestEntry instance = null;
        boolean expResult = false;
        boolean result = instance.canBePurged();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class TGPSRPRequestEntry.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        TGPSRPRequestEntry anotherTGPSRPRequestEntry = null;
        TGPSRPRequestEntry instance = null;
        int expResult = 0;
        int result = instance.compareTo(anotherTGPSRPRequestEntry);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
