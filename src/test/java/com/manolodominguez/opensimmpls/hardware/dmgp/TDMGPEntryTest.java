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
public class TDMGPEntryTest {

    public TDMGPEntryTest() {
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
     * Test of getPacketGoSGlobalUniqueIdentifier method, of class TDMGPEntry.
     */
    @Test
    public void testGetPacketGoSGlobalUniqueIdentifier() {
        System.out.println("getPacketGoSGlobalUniqueIdentifier");
        TDMGPEntry instance = null;
        int expResult = 0;
        int result = instance.getPacketGoSGlobalUniqueIdentifier();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPacket method, of class TDMGPEntry.
     */
    @Test
    public void testGetPacket() {
        System.out.println("getPacket");
        TDMGPEntry instance = null;
        TMPLSPDU expResult = null;
        TMPLSPDU result = instance.getPacket();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPacket method, of class TDMGPEntry.
     */
    @Test
    public void testSetPacket() {
        System.out.println("setPacket");
        TMPLSPDU mplsPacket = null;
        TDMGPEntry instance = null;
        instance.setPacket(mplsPacket);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArrivalOrder method, of class TDMGPEntry.
     */
    @Test
    public void testGetArrivalOrder() {
        System.out.println("getArrivalOrder");
        TDMGPEntry instance = null;
        int expResult = 0;
        int result = instance.getArrivalOrder();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class TDMGPEntry.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        TDMGPEntry anotherDMGPEntry = null;
        TDMGPEntry instance = null;
        int expResult = 0;
        int result = instance.compareTo(anotherDMGPEntry);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
  
}
