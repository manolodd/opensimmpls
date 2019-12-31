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
public class TDMGPTest {
    
    public TDMGPTest() {
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
     * Test of setDMGPSizeInKB method, of class TDMGP.
     */
    @Test
    public void testSetDMGPSizeInKB() {
        System.out.println("setDMGPSizeInKB");
        int totalDMGPSizeInKB = 0;
        TDMGP instance = new TDMGP();
        instance.setDMGPSizeInKB(totalDMGPSizeInKB);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDMGPSizeInKB method, of class TDMGP.
     */
    @Test
    public void testGetDMGPSizeInKB() {
        System.out.println("getDMGPSizeInKB");
        TDMGP instance = new TDMGP();
        int expResult = 0;
        int result = instance.getDMGPSizeInKB();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPacket method, of class TDMGP.
     */
    @Test
    public void testGetPacket() {
        System.out.println("getPacket");
        int globalFlowID = 0;
        int packetGlobalUniqueID = 0;
        TDMGP instance = new TDMGP();
        TMPLSPDU expResult = null;
        TMPLSPDU result = instance.getPacket(globalFlowID, packetGlobalUniqueID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPacket method, of class TDMGP.
     */
    @Test
    public void testAddPacket() {
        System.out.println("addPacket");
        TMPLSPDU packet = null;
        TDMGP instance = new TDMGP();
        instance.addPacket(packet);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reset method, of class TDMGP.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        TDMGP instance = new TDMGP();
        instance.reset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
