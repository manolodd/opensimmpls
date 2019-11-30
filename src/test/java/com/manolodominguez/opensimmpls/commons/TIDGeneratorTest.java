/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manolodominguez.opensimmpls.commons;

import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TIDGeneratorTest {
    
    public TIDGeneratorTest() {
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
     * Test of reset method, of class TIDGenerator.
     */
    @Test
    public void testReset() {
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.reset();
            assertEquals(1, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getNextIdentifier method, of class TIDGenerator.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetNextIdentifier() throws Exception {
        TIDGenerator instance = new TIDGenerator();
        instance.getNextIdentifier(); // should return 1. Next 2
        instance.getNextIdentifier(); // should return 2. Next 3
        instance.getNextIdentifier(); // should return 3. Next 4
        instance.getNextIdentifier(); // should return 4. Next 5
        assertEquals(5, instance.getNextIdentifier());
    }

    /**
     * Test of setIdentifierIfGreater method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierIfGreater1() {
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifierIfGreater(2); // Remains 4, next should be 5
            assertEquals(5, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of setIdentifierIfGreater method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierIfGreater2() {
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifierIfGreater(10); // Set 10, next should be 11
            assertEquals(11, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifier() {
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifier(2); // Sets 2. Next should be 3
            assertEquals(3, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
