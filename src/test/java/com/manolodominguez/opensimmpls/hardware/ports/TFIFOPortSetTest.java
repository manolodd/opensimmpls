/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manolodominguez.opensimmpls.hardware.ports;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.scenario.TLink;
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
public class TFIFOPortSetTest {

    public TFIFOPortSetTest() {
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
     * Test of constructor of class TFIFOPortSet.
     */
    @Test
    public void testConstructor() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of increasePortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testIncreasePortSetOccupancy() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decreasePortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testDecreasePortSetOccupancy() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPortSetOccupancySize method, of class TFIFOPortSet.
     */
    @Test
    public void testSetPortSetOccupancySize() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testGetPortSetOccupancy() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isCongestedArtificially method, of class TFIFOPortSet.
     */
    @Test
    public void testIsCongestedArtificially() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfPorts method, of class TFIFOPortSet.
     */
    @Test
    public void testGetNumberOfPorts() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setParentNode method, of class TFIFOPortSet.
     */
    @Test
    public void testSetParentNode() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentNode method, of class TFIFOPortSet.
     */
    @Test
    public void testGetParentNode() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUnlimitedBuffer method, of class TFIFOPortSet.
     */
    @Test
    public void testSetUnlimitedBuffer() {
        System.out.println("setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPortSet instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPort method, of class TFIFOPortSet.
     */
    @Test
    public void testGetPort() {
        System.out.println("getPort");
        int portID = 0;
        TFIFOPortSet instance = null;
        TPort expResult = null;
        TPort result = instance.getPort(portID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBufferSizeInMB method, of class TFIFOPortSet.
     */
    @Test
    public void testSetBufferSizeInMB() {
        System.out.println("setBufferSizeInMB");
        int sizeInMB = 0;
        TFIFOPortSet instance = null;
        instance.setBufferSizeInMB(sizeInMB);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBufferSizeInMBytes method, of class TFIFOPortSet.
     */
    @Test
    public void testGetBufferSizeInMBytes() {
        System.out.println("getBufferSizeInMBytes");
        TFIFOPortSet instance = null;
        int expResult = 0;
        int result = instance.getBufferSizeInMBytes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAvailable method, of class TFIFOPortSet.
     */
    @Test
    public void testIsAvailable() {
        System.out.println("isAvailable");
        int portID = 0;
        TFIFOPortSet instance = null;
        boolean expResult = false;
        boolean result = instance.isAvailable(portID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasAvailablePorts method, of class TFIFOPortSet.
     */
    @Test
    public void testHasAvailablePorts() {
        System.out.println("hasAvailablePorts");
        TFIFOPortSet instance = null;
        boolean expResult = false;
        boolean result = instance.hasAvailablePorts();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of connectLinkToPort method, of class TFIFOPortSet.
     */
    @Test
    public void testConnectLinkToPort() {
        System.out.println("connectLinkToPort");
        TLink link = null;
        int portID = 0;
        TFIFOPortSet instance = null;
        instance.connectLinkToPort(link, portID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLinkConnectedToPort method, of class TFIFOPortSet.
     */
    @Test
    public void testGetLinkConnectedToPort() {
        System.out.println("getLinkConnectedToPort");
        int portID = 0;
        TFIFOPortSet instance = null;
        TLink expResult = null;
        TLink result = instance.getLinkConnectedToPort(portID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of disconnectLinkFromPort method, of class TFIFOPortSet.
     */
    @Test
    public void testDisconnectLinkFromPort() {
        System.out.println("disconnectLinkFromPort");
        int portID = 0;
        TFIFOPortSet instance = null;
        instance.disconnectLinkFromPort(portID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextPacket method, of class TFIFOPortSet.
     */
    @Test
    public void testGetNextPacket() {
        System.out.println("getNextPacket");
        TFIFOPortSet instance = null;
        TAbstractPDU expResult = null;
        TAbstractPDU result = instance.getNextPacket();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isThereAnyPacketToSwitch method, of class TFIFOPortSet.
     */
    @Test
    public void testIsThereAnyPacketToSwitch() {
        System.out.println("isThereAnyPacketToSwitch");
        TFIFOPortSet instance = null;
        boolean expResult = false;
        boolean result = instance.isThereAnyPacketToSwitch();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isThereAnyPacketToRoute method, of class TFIFOPortSet.
     */
    @Test
    public void testIsThereAnyPacketToRoute() {
        System.out.println("isThereAnyPacketToRoute");
        TFIFOPortSet instance = null;
        boolean expResult = false;
        boolean result = instance.isThereAnyPacketToRoute();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canSwitchPacket method, of class TFIFOPortSet.
     */
    @Test
    public void testCanSwitchPacket() {
        System.out.println("canSwitchPacket");
        int maxSwitchableOctects = 0;
        TFIFOPortSet instance = null;
        boolean expResult = false;
        boolean result = instance.canSwitchPacket(maxSwitchableOctects);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of skipPort method, of class TFIFOPortSet.
     */
    @Test
    public void testSkipPort() {
        System.out.println("skipPort");
        TFIFOPortSet instance = null;
        instance.skipPort();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReadPort method, of class TFIFOPortSet.
     */
    @Test
    public void testGetReadPort() {
        System.out.println("getReadPort");
        TFIFOPortSet instance = null;
        int expResult = 0;
        int result = instance.getReadPort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLocalPortConnectedToANodeWithIPv4Address method, of class
     * TFIFOPortSet.
     */
    @Test
    public void testGetLocalPortConnectedToANodeWithIPv4Address() {
        System.out.println("getLocalPortConnectedToANodeWithIPv4Address");
        String adjacentNodeIPv4Address = "";
        TFIFOPortSet instance = null;
        TPort expResult = null;
        TPort result = instance.getLocalPortConnectedToANodeWithIPv4Address(adjacentNodeIPv4Address);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIPv4OfNodeLinkedTo method, of class TFIFOPortSet.
     */
    @Test
    public void testGetIPv4OfNodeLinkedTo() {
        System.out.println("getIPv4OfNodeLinkedTo");
        int portID = 0;
        TFIFOPortSet instance = null;
        String expResult = "";
        String result = instance.getIPv4OfNodeLinkedTo(portID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCongestionLevel method, of class TFIFOPortSet.
     */
    @Test
    public void testGetCongestionLevel() {
        System.out.println("getCongestionLevel");
        TFIFOPortSet instance = null;
        long expResult = 0L;
        long result = instance.getCongestionLevel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reset method, of class TFIFOPortSet.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        TFIFOPortSet instance = null;
        instance.reset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setArtificiallyCongested method, of class TFIFOPortSet.
     */
    @Test
    public void testSetArtificiallyCongested() {
        System.out.println("setArtificiallyCongested");
        boolean congestArtificially = false;
        TFIFOPortSet instance = null;
        instance.setArtificiallyCongested(congestArtificially);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
