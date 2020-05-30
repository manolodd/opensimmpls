/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manolodominguez.opensimmpls.hardware.ports;

import com.manolodominguez.opensimmpls.commons.TLongIDGenerator;
import com.manolodominguez.opensimmpls.commons.TSemaphore;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.scenario.TLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TScenario;
import com.manolodominguez.opensimmpls.scenario.TTopology;
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
        System.out.println("test constructor");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        // We set 8 ports for the ports set and also give the created node as 
        // the parent node.
        TFIFOPortSet instance = new TFIFOPortSet(8, node);
        boolean worksFine = true;
        if (instance.getParentNode() != node) { // Web compare the node reference
            worksFine &= false;
        }
        if (instance.getNumberOfPorts() != 8) { // and the specified numer of ports
            worksFine &= false;
        }
        // And the rest of values the constructor sets
        if (instance.getBufferSizeInMBytes() != 1) {
            worksFine &= false;
        }
        if (instance.getPortSetOccupancy() != 0) {
            worksFine &= false;
        }
        if (instance.isCongestedArtificially()) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of constructor of class TFIFOPortSet.
     */
    @Test
    public void testConstructorWhenNumberOfPortsNegative() {
        System.out.println("test constructor");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        // We set -1 ports for the ports set and this should throws an exception
        assertThrows(IllegalArgumentException.class, () -> {
            TFIFOPortSet instance = new TFIFOPortSet(-1, node);
        });
    }

    /**
     * Test of constructor of class TFIFOPortSet.
     */

    @Test
    public void testConstructorWhenParentNodeIsNull() {
        System.out.println("test constructor");
        // We set null for the parent node and this should throws an exception
        assertThrows(IllegalArgumentException.class, () -> {
            TFIFOPortSet instance = new TFIFOPortSet(8, null);
        });
    }

// Test of TPortSet superclass
    /**
     * Test of increasePortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testIncreasePortSetOccupancy() {
        System.out.println("increasePortSetOccupancy");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        instance.increasePortSetOccupancy(123); // increase port set occupancy in 123 octets
        assertEquals(123, instance.getPortSetOccupancy());
    }

    /**
     * Test of increasePortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testIncreasePortSetOccupancyWhenNegativeIncrement() {
        System.out.println("increasePortSetOccupancy");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        assertThrows(IllegalArgumentException.class, () -> {
            instance.increasePortSetOccupancy(-1); // This should thrown an exception
        });
    }

    /**
     * Test of decreasePortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testDecreasePortSetOccupancy() {
        System.out.println("test decreasePortSetOccupancy");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        instance.increasePortSetOccupancy(123); // increase port set occupancy in 123 octets
        instance.decreasePortSetOccupancySize(23); // decrease port set occupancy in 23 octets
        assertEquals(100, instance.getPortSetOccupancy());
    }

    /**
     * Test of decreasePortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testDecreasePortSetOccupancyWhenNegativeDecrease() {
        System.out.println("test decreasePortSetOccupancy");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        instance.increasePortSetOccupancy(123); // increase port set occupancy in 123 octets
        assertThrows(IllegalArgumentException.class, () -> {
            instance.decreasePortSetOccupancySize(-1); // This should thrown an exception
        });
    }
    
    /**
     * Test of setPortSetOccupancySize method, of class TFIFOPortSet.
     */
    @Test
    public void testSetPortSetOccupancySize() {
        System.out.println("test setPortSetOccupancySize");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        instance.setPortSetOccupancySize(123); // sets the port set occupancy in 123 octets
        assertEquals(123, instance.getPortSetOccupancy());
    }

    /**
     * Test of setPortSetOccupancySize method, of class TFIFOPortSet.
     */
    @Test
    public void testSetPortSetOccupancySizeWhenNegative() {
        System.out.println("test setPortSetOccupancySize");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortSetOccupancySize(-1); // This should thrown an exception
        });
    }

    /**
     * Test of getPortSetOccupancy method, of class TFIFOPortSet.
     */
    @Test
    public void testGetPortSetOccupancy() {
        System.out.println("test getPortSetOccupancy");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        instance.setPortSetOccupancySize(100); //sets occupancy to 100 octets
        assertEquals(100, instance.getPortSetOccupancy());
    }

    /**
     * Test of isCongestedArtificially method, of class TFIFOPortSet.
     */
    @Test
    public void testIsCongestedArtificially() {
        System.out.println("test isCongestedArtificially");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node); // Occupancy is 0 here
        instance.setPortSetOccupancySize(100); //sets occupancy to 100 octets
        boolean worksFine = true;
        if (instance.isCongestedArtificially()) { // By default is not congested
            worksFine &= false;
        }
        instance.setArtificiallyCongested(true); // sets artificially congested
        if (!instance.isCongestedArtificially()) { // Should be congested artificially
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of getNumberOfPorts method, of class TFIFOPortSet.
     */
    @Test
    public void testGetNumberOfPorts() {
        System.out.println("test getNumberOfPorts");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(5, node); // sets 5 ports
        assertEquals(5, instance.getNumberOfPorts());
    }

    /**
     * Test of getParentNode method, of class TFIFOPortSet.
     */
    @Test
    public void testGetParentNode() {
        System.out.println("test getParentNode");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet instance = new TFIFOPortSet(8, node);
        assertTrue(node == instance.getParentNode()); // Both references should be the same
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

// Test of TFIFOPortSet subclass
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
