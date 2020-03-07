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
package com.manolodominguez.opensimmpls.hardware.ports;

import com.manolodominguez.opensimmpls.commons.TLongIDGenerator;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.scenario.TExternalLink;
import com.manolodominguez.opensimmpls.scenario.TInternalLink;
import com.manolodominguez.opensimmpls.scenario.TLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TLinkConfig;
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
public class TFIFOPortTest {

    public TFIFOPortTest() {
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
     * Test constructor, of class TFIFOPort.
     */
    @Test
    public void testConstructor() {
        System.out.println("test Constructor");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        boolean worksFine = true;
        if (instance.getLink() != null) {
            worksFine &= false;
        }
        if (instance.getPortSet() == null) {
            worksFine &= false;
        }
        if (instance.getPortSet() != portSet) { //comparing reference
            worksFine &= false;
        }
        if (instance.getPortID() != 0) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test constructor, of class TFIFOPort.
     */
    @Test
    public void testConstructorWhenPortSetIsNull() {
        System.out.println("test constructor");
        assertThrows(IllegalArgumentException.class, () -> {
            TFIFOPort instance = new TFIFOPort(null, 0);  // Should throw an exception
        });
    }

    /**
     * Test constructor, of class TFIFOPort.
     */
    @Test
    public void testConstructorWhenPortIsNegative() {
        System.out.println("test constructor");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        assertThrows(IllegalArgumentException.class, () -> {
            TFIFOPort instance = new TFIFOPort(portSet, -1);  // Should throw an exception
        });
    }

    /**
     * Test constructor, of class TFIFOPort.
     */
    @Test
    public void testConstructorWhenPortIsOutOfRange() {
        System.out.println("test constructor");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        assertThrows(IllegalArgumentException.class, () -> {
            TFIFOPort instance = new TFIFOPort(portSet, 8);  // range is [0,7]. Should throw an exception
        });
    }

    /**
     * Test of setPortSet method, of class TFIFOPort.
     */
    @Test
    public void testSetPortSetWheNull() {
        System.out.println("test setPortSet");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortSet(null);  // Should throw an exception
        });
    }

    /**
     * Test of setPortSet method, of class TFIFOPort.
     */
    @Test
    public void testSetPortSet() {
        System.out.println("test setPortSet");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        instance.setPortSet(portSet);
        assertTrue(portSet == instance.getPortSet()); // Comparing reference
    }

    /**
     * Test of getPortSet method, of class TFIFOPort.
     */
    @Test
    public void testGetPortSet() {
        System.out.println("test getPortSet");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertTrue(portSet == instance.getPortSet()); // Comparing reference
    }

    /**
     * Test of setPortID method, of class TFIFOPort.
     */
    @Test
    public void testSetPortID() {
        System.out.println("test setPortID");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        instance.setPortID(1);
        assertEquals(1, instance.getPortID());
    }

    /**
     * Test of setPortID method, of class TFIFOPort.
     */
    @Test
    public void testSetPortIDWhenNegative() {
        System.out.println("test setPortID");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(-1);  // range is [0,7]. Should throw an exception
        });
    }

    /**
     * Test of setPortID method, of class TFIFOPort.
     */
    @Test
    public void testSetPortIDWhenOutOfRange() {
        System.out.println("test setPortID");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(8);  // range is [0,7]. Should throw an exception
        });
    }

    /**
     * Test of getPortID method, of class TFIFOPort.
     */
    @Test
    public void testGetPortID() {
        System.out.println("test getPortID");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertEquals(0, instance.getPortID());
    }

    /**
     * Test of getPortID method, of class TFIFOPort.
     */
    @Test
    public void testGetPortIDWhenNegative() {
        System.out.println("test getPortID");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(-1);  // range is [0,7]. Should throw an exception
            // Anyway, if the previous sentence does not throw an exception because
            // a bug, this one should throw the exception itself.
            instance.getPortID();
        });
    }

    /**
     * Test of getPortID method, of class TFIFOPort.
     */
    @Test
    public void testGetPortIDWhenOutOfRange() {
        System.out.println("test getPortID");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(8);  // range is [0,7]. Should throw an exception
            // Anyway, if the previous sentence does not throw an exception because
            // a bug, this one should throw the exception itself.
            instance.getPortID();
        });
    }

    /**
     * Test of isAvailable method, of class TFIFOPort.
     */
    @Test
    public void testIsAvailable() {
        System.out.println("test isAvailable");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertTrue(instance.isAvailable());
    }

    /**
     * Test of isAvailable method, of class TFIFOPort.
     */
    @Test
    public void testIsAvailableWhenUnavailable() {
        System.out.println("test isAvailable");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        TExternalLink link = new TExternalLink(1, new TLongIDGenerator(), topology);
        instance.setLink(link);
        assertFalse(instance.isAvailable());
    }

    /**
     * Test of testSetLink method, of class TFIFOPort.
     */
    @Test
    public void testSetLink() {
        System.out.println("test setLink");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        TExternalLink link = new TExternalLink(1, new TLongIDGenerator(), topology);
        instance.setLink(link);
        assertFalse(instance.isAvailable());
    }

    /**
     * Test of testSetLink method, of class TFIFOPort.
     */
    @Test
    public void testSetLinkWhenNull() {
        System.out.println("test setLink");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setLink(null); // Should throw an exception
        });
    }

    /**
     * Test of testGetLink method, of class TFIFOPort.
     */
    @Test
    public void testGetLink() {
        System.out.println("test getLink");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        TExternalLink link = new TExternalLink(1, new TLongIDGenerator(), topology);
        instance.setLink(link);
        assertTrue(instance.getLink() == link); // Correct. Here both references have to be the same
    }

    /**
     * Test of testGetLink method, of class TFIFOPort.
     */
    @Test
    public void testGetLinkWhenNull() {
        System.out.println("test getLink");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        assertTrue(instance.getLink() == null); // Correct. Here both references have to be the same
    }

    /**
     * Test of testDisconnectLink method, of class TFIFOPort.
     */
    @Test
    public void testDisconnectLink() {
        System.out.println("test disconnectLink");
        boolean worksFine = true;
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        TExternalLink link = new TExternalLink(1, new TLongIDGenerator(), topology);
        instance.setLink(link);
        if (instance.getLink() != link) {  // Correct. Here both references have to be the same
            worksFine &= false;
        }
        instance.disconnectLink();
        if (instance.getLink() != null) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of testDisconnectLink method, of class TFIFOPort.
     */
    @Test
    public void testDisconnectLinkWhenNotLinked() {
        System.out.println("test disconnectLink");
        TScenario scenario = new TScenario();
        TTopology topology = new TTopology(scenario);
        TLSRNode node = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TFIFOPortSet portSet = new TFIFOPortSet(8, node);
        TFIFOPort instance = new TFIFOPort(portSet, 0);
        instance.disconnectLink();
        assertTrue(instance.getLink() == null);
    }

    /**
     * Test of testPutPacketOnLink method, of class TFIFOPort.
     */
    @Test
    public void testPutPacketOnLink() {
        System.out.println("test putPacketOnLink");
        TScenario scenario = new TScenario();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TLSRNode headEndNode = new TLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology); //Creates a node
        headEndNode.setName("Dummy head end node name");
        TLSRNode tailEndNode = new TLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        TInternalLink internalLink = new TInternalLink(3, new TLongIDGenerator(), topology);  //Creats a link
        topology.addNode(headEndNode); // Adds head end node to the topology
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        topology.addLink(internalLink); //Adds node to
        
        TLinkConfig linkConfig = new TLinkConfig(); // Creates a link configuration object
        linkConfig.setName("Dummy link name");
        linkConfig.setShowName(false);
        linkConfig.setLinkDelay(1); //1 ns
        linkConfig.setHeadEndNodeName("Dummy head end node name");
        linkConfig.setTailEndNodeName("Dummy tail end node name");
        linkConfig.setLinkType(TLink.INTERNAL_LINK);
        linkConfig.setHeadEndNodePortID(0); // port 0 of head end
        linkConfig.setTailEndNodePortID(0); // port 0 of tail end
        
        int error = linkConfig.validateConfig(topology, false); // Check the link config against the topology
        if (error != TLinkConfig.OK) {
            System.out.println("****************** LINK CONFIG ERROR");
            linkConfig.setWellConfigured(false);
            fail("The test case is a prototype.");
        } else {
            linkConfig.setWellConfigured(true);
        }

        internalLink.configure(linkConfig, topology, false);

        // Here we habe two nodes linked by a link on the port 0 of each one.
        
        boolean worksFine = true;
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024); 
        if (internalLink.getNumberOfPacketInTransit() != 0) {
            worksFine &= false;
        }
        // Put packet on link connected to port 0 of head en node.
        headEndNode.getPorts().getPort(0).putPacketOnLink(mplsPacket, 2); 
        if (internalLink.getNumberOfPacketInTransit() != 1) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of setUnlimitedBuffer method, of class TFIFOPort.
     */
    @Test
    public void testSetUnlimitedBuffer() {
        System.out.println("test setUnlimitedBuffer");
        boolean unlimitedBuffer = false;
        TFIFOPort instance = null;
        instance.setUnlimitedBuffer(unlimitedBuffer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of discardPacket method, of class TFIFOPort.
     */
    @Test
    public void testDiscardPacket() {
        System.out.println("test discardPacket");
        TAbstractPDU packet = null;
        TFIFOPort instance = null;
        instance.discardPacket(packet);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPacket method, of class TFIFOPort.
     */
    @Test
    public void testAddPacket() {
        System.out.println("test addPacket");
        TAbstractPDU packet = null;
        TFIFOPort instance = null;
        instance.addPacket(packet);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reEnqueuePacket method, of class TFIFOPort.
     */
    @Test
    public void testReEnqueuePacket() {
        System.out.println("test reEnqueuePacket");
        TAbstractPDU packet = null;
        TFIFOPort instance = null;
        instance.reEnqueuePacket(packet);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPacket method, of class TFIFOPort.
     */
    @Test
    public void testGetPacket() {
        System.out.println("test getPacket");
        TFIFOPort instance = null;
        TAbstractPDU expResult = null;
        TAbstractPDU result = instance.getPacket();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canSwitchPacket method, of class TFIFOPort.
     */
    @Test
    public void testCanSwitchPacket() {
        System.out.println("test canSwitchPacket");
        int switchableOctets = 0;
        TFIFOPort instance = null;
        boolean expResult = false;
        boolean result = instance.canSwitchPacket(switchableOctets);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCongestionLevel method, of class TFIFOPort.
     */
    @Test
    public void testGetCongestionLevel() {
        System.out.println("test getCongestionLevel");
        TFIFOPort instance = null;
        long expResult = 0L;
        long result = instance.getCongestionLevel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of thereIsAPacketWaiting method, of class TFIFOPort.
     */
    @Test
    public void testThereIsAPacketWaiting() {
        System.out.println("test thereIsAPacketWaiting");
        TFIFOPort instance = null;
        boolean expResult = false;
        boolean result = instance.thereIsAPacketWaiting();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOccupancy method, of class TFIFOPort.
     */
    @Test
    public void testGetOccupancy() {
        System.out.println("test getOccupancy");
        TFIFOPort instance = null;
        long expResult = 0L;
        long result = instance.getOccupancy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfPackets method, of class TFIFOPort.
     */
    @Test
    public void testGetNumberOfPackets() {
        System.out.println("test getNumberOfPackets");
        TFIFOPort instance = null;
        int expResult = 0;
        int result = instance.getNumberOfPackets();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reset method, of class TFIFOPort.
     */
    @Test
    public void testReset() {
        System.out.println("test reset");
        TFIFOPort instance = null;
        instance.reset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
