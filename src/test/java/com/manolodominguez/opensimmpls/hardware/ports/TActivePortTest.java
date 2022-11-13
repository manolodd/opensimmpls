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
import com.manolodominguez.opensimmpls.gui.simulator.JSimulationPanel;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSLabel;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.scenario.TOuterLink;
import com.manolodominguez.opensimmpls.scenario.TInnerLink;
import com.manolodominguez.opensimmpls.scenario.TActiveLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TLinkConfig;
import com.manolodominguez.opensimmpls.scenario.TScene;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import java.util.NoSuchElementException;
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
public class TActivePortTest {

    public TActivePortTest() {
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
     * Test constructor, of class TActivePort.
     */
    @Test
    public void testConstructor() {
        System.out.println("test Constructor");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
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
     * Test constructor, of class TActivePort.
     */
    @Test
    public void testConstructorWhenPortSetIsNull() {
        System.out.println("test constructor");
        assertThrows(IllegalArgumentException.class, () -> {
            TActivePort instance = new TActivePort(null, 0);  // Should throw an exception
        });
    }

    /**
     * Test constructor, of class TActivePort.
     */
    @Test
    public void testConstructorWhenPortIsNegative() {
        System.out.println("test constructor");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        assertThrows(IllegalArgumentException.class, () -> {
            TActivePort instance = new TActivePort(portSet, -1);  // Should throw an exception
        });
    }

    /**
     * Test constructor, of class TActivePort.
     */
    @Test
    public void testConstructorWhenPortIsOutOfRange() {
        System.out.println("test constructor");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        assertThrows(IllegalArgumentException.class, () -> {
            TActivePort instance = new TActivePort(portSet, 8);  // range is [0,7]. Should throw an exception
        });
    }

    /**
     * Test of setPortSet method, of class TActivePort.
     */
    @Test
    public void testSetPortSetWheNull() {
        System.out.println("test setPortSet");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortSet(null);  // Should throw an exception
        });
    }

    /**
     * Test of setPortSet method, of class TActivePort.
     */
    @Test
    public void testSetPortSet() {
        System.out.println("test setPortSet");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        instance.setPortSet(portSet);
        assertTrue(portSet == instance.getPortSet()); // Comparing reference
    }

    /**
     * Test of getPortSet method, of class TActivePort.
     */
    @Test
    public void testGetPortSet() {
        System.out.println("test getPortSet");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertTrue(portSet == instance.getPortSet()); // Comparing reference
    }

    /**
     * Test of setPortID method, of class TActivePort.
     */
    @Test
    public void testSetPortID() {
        System.out.println("test setPortID");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        instance.setPortID(1);
        assertEquals(1, instance.getPortID());
    }

    /**
     * Test of setPortID method, of class TActivePort.
     */
    @Test
    public void testSetPortIDWhenNegative() {
        System.out.println("test setPortID");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(-1);  // range is [0,7]. Should throw an exception
        });
    }

    /**
     * Test of setPortID method, of class TActivePort.
     */
    @Test
    public void testSetPortIDWhenOutOfRange() {
        System.out.println("test setPortID");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(8);  // range is [0,7]. Should throw an exception
        });
    }

    /**
     * Test of getPortID method, of class TActivePort.
     */
    @Test
    public void testGetPortID() {
        System.out.println("test getPortID");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertEquals(0, instance.getPortID());
    }

    /**
     * Test of getPortID method, of class TActivePort.
     */
    @Test
    public void testGetPortIDWhenNegative() {
        System.out.println("test getPortID");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(-1);  // range is [0,7]. Should throw an exception
            // Anyway, if the previous sentence does not throw an exception because
            // a bug, this one should throw the exception itself.
            instance.getPortID();
        });
    }

    /**
     * Test of getPortID method, of class TActivePort.
     */
    @Test
    public void testGetPortIDWhenOutOfRange() {
        System.out.println("test getPortID");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setPortID(8);  // range is [0,7]. Should throw an exception
            // Anyway, if the previous sentence does not throw an exception because
            // a bug, this one should throw the exception itself.
            instance.getPortID();
        });
    }

    /**
     * Test of isAvailable method, of class TActivePort.
     */
    @Test
    public void testIsAvailable() {
        System.out.println("test isAvailable");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertTrue(instance.isAvailable());
    }

    /**
     * Test of isAvailable method, of class TActivePort.
     */
    @Test
    public void testIsAvailableWhenUnavailable() {
        System.out.println("test isAvailable");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        TOuterLink link = new TOuterLink(1, new TLongIDGenerator(), topology);
        instance.setLink(link);
        assertFalse(instance.isAvailable());
    }

    /**
     * Test of testSetLink method, of class TActivePort.
     */
    @Test
    public void testSetLink() {
        System.out.println("test setLink");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        TOuterLink link = new TOuterLink(1, new TLongIDGenerator(), topology);
        instance.setLink(link);
        assertFalse(instance.isAvailable());
    }

    /**
     * Test of testSetLink method, of class TActivePort.
     */
    @Test
    public void testSetLinkWhenNull() {
        System.out.println("test setLink");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            instance.setLink(null); // Should throw an exception
        });
    }

    /**
     * Test of testGetLink method, of class TActivePort.
     */
    @Test
    public void testGetLink() {
        System.out.println("test getLink");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        TOuterLink link = new TOuterLink(1, new TLongIDGenerator(), topology);
        instance.setLink(link);
        assertTrue(instance.getLink() == link); // Correct. Here both references have to be the same
    }

    /**
     * Test of testGetLink method, of class TActivePort.
     */
    @Test
    public void testGetLinkWhenNull() {
        System.out.println("test getLink");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        assertTrue(instance.getLink() == null); // Correct. Here both references have to be the same
    }

    /**
     * Test of testDisconnectLink method, of class TActivePort.
     */
    @Test
    public void testDisconnectLink() {
        System.out.println("test disconnectLink");
        boolean worksFine = true;
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        TOuterLink link = new TOuterLink(1, new TLongIDGenerator(), topology);
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
     * Test of testDisconnectLink method, of class TActivePort.
     */
    @Test
    public void testDisconnectLinkWhenNotLinked() {
        System.out.println("test disconnectLink");
        TScene scenario = new TScene();
        TTopology topology = new TTopology(scenario);
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology);
        TActivePortSet portSet = new TActivePortSet(8, node);
        TActivePort instance = new TActivePort(portSet, 0);
        instance.disconnectLink();
        assertTrue(instance.getLink() == null);
    }

    /**
     * Test of testPutPacketOnLink method, of class TActivePort.
     */
    @Test
    public void testPutPacketOnLinkWhenPacketIsNull() {
        System.out.println("test putPacketOnLink");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode headEndNode = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology); //Creates a node
        headEndNode.setName("Dummy head end node name");
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        TInnerLink internalLink = new TInnerLink(3, new TLongIDGenerator(), topology);  //Creats a link
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

        // Here we have two nodes linked by a link on the port 0 of each one.
        // Put packet (really, null) on link connected to port 0 of head en node.
        assertThrows(IllegalArgumentException.class, () -> {
            // Should throws an exception because the specified packet is null
            headEndNode.getPorts().getPort(0).putPacketOnLink(null, TLink.TAIL_END_NODE);
        });
    }

    /**
     * Test of testPutPacketOnLink method, of class TActivePort.
     */
    @Test
    public void testPutPacketOnLinkWhenOutOfRange1() {
        System.out.println("test putPacketOnLink");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode headEndNode = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology); //Creates a node
        headEndNode.setName("Dummy head end node name");
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        TInnerLink internalLink = new TInnerLink(3, new TLongIDGenerator(), topology);  //Creats a link
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
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        // Put packet on link connected to port 0 of head en node.
        assertThrows(IllegalArgumentException.class, () -> {
            // Should throws an exception because the specified end node is neither
            // TLink.HEAD_END_NODE (value 1) nor TLink.TAIL_END_NODE (value 2)
            headEndNode.getPorts().getPort(0).putPacketOnLink(mplsPacket, 0);
        });
    }

    /**
     * Test of testPutPacketOnLink method, of class TActivePort.
     */
    @Test
    public void testPutPacketOnLinkWhenOutOfRange2() {
        System.out.println("test putPacketOnLink");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode headEndNode = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology); //Creates a node
        headEndNode.setName("Dummy head end node name");
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        TInnerLink internalLink = new TInnerLink(3, new TLongIDGenerator(), topology);  //Creats a link
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
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        // Put packet on link connected to port 0 of head en node.
        assertThrows(IllegalArgumentException.class, () -> {
            // Should throws an exception because the specified end node is neither
            // TLink.HEAD_END_NODE (value 1) nor TLink.TAIL_END_NODE (value 2)
            headEndNode.getPorts().getPort(0).putPacketOnLink(mplsPacket, 3);
        });
    }

    /**
     * Test of testPutPacketOnLink method, of class TActivePort.
     */
    @Test
    public void testPutPacketOnLink() {
        System.out.println("test putPacketOnLink");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode headEndNode = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology); //Creates a node
        headEndNode.setName("Dummy head end node name");
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        TInnerLink internalLink = new TInnerLink(3, new TLongIDGenerator(), topology);  //Creats a link
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
        headEndNode.getPorts().getPort(0).putPacketOnLink(mplsPacket, TLink.TAIL_END_NODE);
        if (internalLink.getNumberOfPacketInTransit() != 1) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of getNextPacketPriority method, of class TActivePort.
     */
    @Test
    public void testGetNextPacketPriorityWhenNoPacketAvailable() {
        System.out.println("getNextPacketPriority");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        assertEquals(-1, ((TActivePort) tailEndNode.getPorts().getPort(0)).getNextPacketPriority());
    }

    /**
     * Test of getNextPacketPriority method, of class TActivePort.
     */
    @Test
    public void testGetNextPacketPriority() {
        System.out.println("getNextPacketPriority");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).reEnqueuePacket(mplsPacket);
        assertEquals(0, ((TActivePort) tailEndNode.getPorts().getPort(0)).getNextPacketPriority());
    }

    /**
     * Test of setUnlimitedBuffer method, of class TActivePort.
     */
    @Test
    public void testSetUnlimitedBuffer() {
        System.out.println("test setUnlimitedBuffer");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology); //Creates a node
        // All ports of this node are set to not unlimited by default constructor.
        // Here we force them to be unlimited.
        boolean worksFine = true;
        if (node.getPorts().getPort(0).isUnlimitedBuffer()) {
            worksFine &= false;
        }
        node.getPorts().getPort(0).setUnlimitedBuffer(true);
        if (!node.getPorts().getPort(0).isUnlimitedBuffer()) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of isUnlimitedBuffer method, of class TActivePort.
     */
    @Test
    public void testIsUnlimitedBuffer() {
        System.out.println("test isUnlimitedBuffer");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode node = new TActiveLSRNode(1, "10.0.0.1", new TLongIDGenerator(), topology); //Creates a node
        // All ports of this node are set to not unlimited by default constructor.
        // Here we force them to be unlimited.
        boolean worksFine = true;
        if (node.getPorts().getPort(0).isUnlimitedBuffer()) {
            worksFine &= false;
        }
        node.getPorts().getPort(0).setUnlimitedBuffer(true);
        if (!node.getPorts().getPort(0).isUnlimitedBuffer()) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of discardPacket method, of class TActivePort.
     */
    @Test
    public void testDiscardPacket() {
        System.out.println("discardPacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        assertDoesNotThrow(() -> {
            tailEndNode.getPorts().getPort(0).discardPacket(mplsPacket);
        });
    }

    /**
     * Test of discardPacket method, of class TActivePort.
     */
    @Test
    public void testDiscardPacketWhenPacketIsNull() {
        System.out.println("test discardPacket 2");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        assertThrows(IllegalArgumentException.class, () -> {
            tailEndNode.getPorts().getPort(0).discardPacket(null); // This throws an exception
        });
    }

    /**
     * Test of addPacket method, of class TActivePort.
     */
    @Test
    public void testAddPacket() {
        System.out.println("addPacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket);
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 1) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of addPacket method, of class TActivePort.
     */
    @Test
    public void testAddPacketWhenPacketIsNull() {
        System.out.println("test addPacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        assertThrows(IllegalArgumentException.class, () -> {
            tailEndNode.getPorts().getPort(0).addPacket(null);
        });
    }

    /**
     * Test of runEarlyPacketCatchAndDiscard method, of class TActivePort.
     */
    @Test
    public void testRunEarlyPacketCatchAndDiscard() {
        System.out.println("runEarlyPacketCatchAndDiscard");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        worksFine &= ((TActivePort) tailEndNode.getPorts().getPort(0)).runEarlyPacketCatchAndDiscard(mplsPacket);
        assertTrue(worksFine);
    }

    /**
     * Test of runEarlyPacketCatchAndDiscard method, of class TActivePort.
     */
    @Test
    public void testRunEarlyPacketCatchAndDiscardWhenPacketIsNull() {
        System.out.println("runEarlyPacketCatchAndDiscard");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        assertThrows(IllegalArgumentException.class, () -> {
            ((TActivePort) tailEndNode.getPorts().getPort(0)).runEarlyPacketCatchAndDiscard(null);
        });
    }

    /**
     * Test of runEarlyPacketCatchAndDiscard method, of class TActivePort.
     */
    @Test
    public void testRunEarlyPacketCatchAndDiscardWhenBufferIsFull() {
        System.out.println("runEarlyPacketCatchAndDiscard");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        // We create a MPLS packet big enough to make the buffer overflow
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 10240000);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        worksFine &= !((TActivePort) tailEndNode.getPorts().getPort(0)).runEarlyPacketCatchAndDiscard(mplsPacket);
        assertTrue(worksFine);
    }

    /**
     * Test of reEnqueuePacket method, of class TActivePort.
     */
    @Test
    public void testReEnqueuePacket() {
        System.out.println("reEnqueuePacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).reEnqueuePacket(mplsPacket);
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 1) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of reEnqueuePacket method, of class TActivePort.
     */
    @Test
    public void testReEnqueuePacketWhenPacketIsNull() {
        System.out.println("test reEnqueuePacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        assertThrows(IllegalArgumentException.class, () -> {
            tailEndNode.getPorts().getPort(0).reEnqueuePacket(null);
        });
    }

    /**
     * Test of getPacket method, of class TActivePort.
     */
    @Test
    public void testGetPacket() {
        System.out.println("getPacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).reEnqueuePacket(mplsPacket);
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 1) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacketRead = (TMPLSPDU) tailEndNode.getPorts().getPort(0).getPacket();
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of getPacket method, of class TActivePort.
     */
    @Test
    public void testGetPacketWhenNoPacketAvailable() {
        System.out.println("test getPacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Tries to read a packet. Does not exit so, throws an exception.
        assertThrows(NoSuchElementException.class, () -> {
            TMPLSPDU mplsPacketRead = (TMPLSPDU) tailEndNode.getPorts().getPort(0).getPacket();
        });
    }

    /**
     * Test of canSwitchPacket method, of class TActivePort.
     */
    @Test
    public void testCanSwitchPacket() {
        System.out.println("canSwitchPacket");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        // a 1024 octets payload means an active packet with a total size of 
        // 1068 octects
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).reEnqueuePacket(mplsPacket);
        assertTrue(tailEndNode.getPorts().getPort(0).canSwitchPacket(1068));
    }

    /**
     * Test of getCongestionLevel method, of class TActivePort.
     */
    @Test
    public void testGetCongestionLevel() {
        System.out.println("getCongestionLevel");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 65535);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).reEnqueuePacket(mplsPacket);
        assertTrue(tailEndNode.getPorts().getPort(0).getCongestionLevel() > 0L);
    }

    /**
     * Test of getCongestionLevel method, of class TActivePort.
     */
    @Test
    public void testGetCongestionLevelWhenUnlimited() {
        System.out.println("test getCongestionLevel");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TLSRNode tailEndNode = new TLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        tailEndNode.getPorts().setUnlimitedBuffer(true);
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 65535);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).reEnqueuePacket(mplsPacket);
        assertFalse(tailEndNode.getPorts().getPort(0).getCongestionLevel() > 0L);
    }

    /**
     * Test of thereIsAPacketWaiting method, of class TActivePort.
     */
    @Test
    public void testThereIsAPacketWaiting() {
        System.out.println("thereIsAPacketWaiting");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).reEnqueuePacket(mplsPacket);
        assertTrue(tailEndNode.getPorts().getPort(0).thereIsAPacketWaiting());
    }

    /**
     * Test of thereIsAPacketWaiting method, of class TActivePort.
     */
    @Test
    public void testThereIsAPacketWaitingWhenNoPacketAwaiting() {
        System.out.println("test thereIsAPacketWaiting");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //The port has not a packet waiting.
        assertFalse(tailEndNode.getPorts().getPort(0).thereIsAPacketWaiting());
    }

    /**
     * Test of getOccupancy method, of class TActivePort.
     */
    @Test
    public void testGetOccupancy() {
        System.out.println("getOccupancy");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getOccupancy() != 0L) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket);
        if (tailEndNode.getPorts().getPort(0).getOccupancy() == 0L) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of getNumberOfPackets method, of class TActivePort.
     */
    @Test
    public void testGetNumberOfPackets() {
        System.out.println("getNumberOfPackets");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacket = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel = new TMPLSLabel();
        outgoingMPLSLabel.setBoS(true);
        outgoingMPLSLabel.setEXP(0);
        outgoingMPLSLabel.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel.setTTL(mplsPacket.getIPv4Header().getTTL());
        mplsPacket.getLabelStack().pushTop(outgoingMPLSLabel);
        TMPLSPDU mplsPacket2 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel2 = new TMPLSLabel();
        outgoingMPLSLabel2.setBoS(true);
        outgoingMPLSLabel2.setEXP(0);
        outgoingMPLSLabel2.setLabel(50); // A valid and unreserved label
        outgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(outgoingMPLSLabel2);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket2);
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 2) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }

    /**
     * Test of reset method, of class TActivePort.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        TScene scenario = new TScene();  //Creates an scenario
        TTopology topology = new TTopology(scenario); //Creates a topology
        TActiveLSRNode tailEndNode = new TActiveLSRNode(2, "10.0.0.2", new TLongIDGenerator(), topology); //Creates a node
        tailEndNode.setName("Dummy tail end node name");
        topology.addNode(tailEndNode); // Adds tail end node to the topology
        JSimulationPanel simulationPanel = new JSimulationPanel();
        tailEndNode.simulationEventsListener.setSimulationPanel(simulationPanel);
        //Creates a new MPLS packet directed to tail end node.
        boolean worksFine = true;
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        TMPLSPDU mplsPacket1 = new TMPLSPDU(1, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel1 = new TMPLSLabel();
        outgoingMPLSLabel1.setBoS(true);
        outgoingMPLSLabel1.setEXP(TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP);
        outgoingMPLSLabel1.setLabel(50); // A reserved label
        outgoingMPLSLabel1.setTTL(mplsPacket1.getIPv4Header().getTTL());
        mplsPacket1.getLabelStack().pushTop(outgoingMPLSLabel1);

        TMPLSPDU mplsPacket2 = new TMPLSPDU(2, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel2 = new TMPLSLabel();
        outgoingMPLSLabel2.setBoS(true);
        outgoingMPLSLabel2.setEXP(TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP);
        outgoingMPLSLabel2.setLabel(1); // A reserved label
        outgoingMPLSLabel2.setTTL(mplsPacket2.getIPv4Header().getTTL());
        mplsPacket2.getLabelStack().pushTop(outgoingMPLSLabel2);

        TMPLSPDU mplsPacket3 = new TMPLSPDU(3, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel3 = new TMPLSLabel();
        outgoingMPLSLabel3.setBoS(true);
        outgoingMPLSLabel3.setEXP(TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP);
        outgoingMPLSLabel3.setLabel(1); // A reserved label
        outgoingMPLSLabel3.setTTL(mplsPacket3.getIPv4Header().getTTL());
        mplsPacket3.getLabelStack().pushTop(outgoingMPLSLabel3);

        TMPLSPDU mplsPacket4 = new TMPLSPDU(4, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel4 = new TMPLSLabel();
        outgoingMPLSLabel4.setBoS(true);
        outgoingMPLSLabel4.setEXP(TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP);
        outgoingMPLSLabel4.setLabel(1); // A reserved label
        outgoingMPLSLabel4.setTTL(mplsPacket4.getIPv4Header().getTTL());
        mplsPacket4.getLabelStack().pushTop(outgoingMPLSLabel4);

        TMPLSPDU mplsPacket5 = new TMPLSPDU(5, "10.0.0.1", "10.0.0.2", 1024);
        TMPLSLabel outgoingMPLSLabel5 = new TMPLSLabel();
        outgoingMPLSLabel5.setBoS(true);
        outgoingMPLSLabel5.setEXP(TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP);
        outgoingMPLSLabel5.setLabel(1); // A reserved label
        outgoingMPLSLabel5.setTTL(mplsPacket5.getIPv4Header().getTTL());
        mplsPacket5.getLabelStack().pushTop(outgoingMPLSLabel5);

        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket1);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket2);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket3);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket4);
        tailEndNode.getPorts().getPort(0).addPacket(mplsPacket5);
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 5) {
            worksFine &= false;
        }
        tailEndNode.getPorts().getPort(0).reset();
        if (tailEndNode.getPorts().getPort(0).getNumberOfPackets() != 0) {
            worksFine &= false;
        }
        assertTrue(worksFine);
    }
}
