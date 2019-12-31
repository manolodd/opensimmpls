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

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSLabel;
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
     * Test of constructor of class TDMGP.
     */
    @Test
    public void testConstructor() {
        System.out.println("Test constructor");
        boolean worksFine = true;
        TDMGP instance = new TDMGP();
        if (instance.getDMGPSizeInKB() != 1) {
            worksFine &= false;
        }
        if (instance.getPacket(-5684, 14856) != null) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of setDMGPSizeInKB method, of class TDMGP.
     */
    @Test
    public void testSetDMGPSizeInKB() {
        System.out.println("Test setDMGPSizeInKB");
        TDMGP instance = new TDMGP();
        instance.setDMGPSizeInKB(120);
        assertEquals(120, instance.getDMGPSizeInKB());
    }

    /**
     * Test of setDMGPSizeInKB method, of class TDMGP.
     */
    @Test
    public void testSetDMGPSizeInKBWhenNegative() {
        System.out.println("Test setDMGPSizeInKB");
        TDMGP instance = new TDMGP();
        assertThrows(RuntimeException.class, () -> {
            instance.setDMGPSizeInKB(-120); // Not possible. Should throws an exception
        });
    }

    /**
     * Test of getDMGPSizeInKB method, of class TDMGP.
     */
    @Test
    public void testGetDMGPSizeInKB() {
        System.out.println("Test getDMGPSizeInKB");
        TDMGP instance = new TDMGP();
        assertEquals(1, instance.getDMGPSizeInKB());
    }

    /**
     * Test of getPacket method, of class TDMGP.
     */
    @Test
    public void testGetPacket() {
        System.out.println("Test getPacket");

        TDMGP instance = new TDMGP();
        instance.setDMGPSizeInKB(10);
               
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

        instance.addPacket(mplsPacket1);
        
        TMPLSPDU result = instance.getPacket("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        assertTrue(result instanceof TMPLSPDU);
    }

    /**
     * Test of getPacket method, of class TDMGP.
     */
    @Test
    public void testGetPacketWhenNotFound() {
        System.out.println("Test getPacket");

        TDMGP instance = new TDMGP();
        instance.setDMGPSizeInKB(10);
               
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

        instance.addPacket(mplsPacket1);
        
        TMPLSPDU result = instance.getPacket("10.0.0.45".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        assertEquals(null, result);
    }

    /**
     * Test of addPacket method, of class TDMGP.
     */
    @Test
    public void testAddPacket() {

        TDMGP instance = new TDMGP();
        instance.setDMGPSizeInKB(10);
               
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

        instance.addPacket(mplsPacket1);
        
        TMPLSPDU result = instance.getPacket("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        assertTrue(result instanceof TMPLSPDU);
    }

    /**
     * Test of addPacket method, of class TDMGP.
     */
    @Test
    public void testAddPacketWhenNull() {

        TDMGP instance = new TDMGP();
        assertThrows(RuntimeException.class, () -> {
            instance.addPacket(null); // Not possible. Should throws an exception
        });
    }


    /**
     * Test of addPacket method, of class TDMGP.
     */
    @Test
    public void testAddPacketWhenNotEnoughMemoryInDMGP() {

        TDMGP instance = new TDMGP();
        instance.setDMGPSizeInKB(1);
               
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

        instance.addPacket(mplsPacket1);
        
        TMPLSPDU result = instance.getPacket("10.0.0.1".hashCode(), mplsPacket1.getIPv4Header().getGoSGlobalUniqueIdentifier());
        assertEquals(null, result);
    }

    /**
     * Test of reset method, of class TDMGP.
     */
    @Test
    public void testReset() {
        System.out.println("Test reset");
        boolean worksFine = true;
        TDMGP instance = new TDMGP();
        instance.setDMGPSizeInKB(1250);
        instance.reset();
        if (instance.getDMGPSizeInKB() != 1250) {
            worksFine &= false;
        }
        if (instance.getPacket(-5684, 14856) != null) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

}
