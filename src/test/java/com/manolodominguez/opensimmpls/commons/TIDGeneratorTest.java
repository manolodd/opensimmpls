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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
     *
     */
    @Test
    public void testGetNextIdentifier() {
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            assertEquals(5, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getNextIdentifier method, of class TIDGenerator.
     *
     */
    @Test
    public void testGetNextIdentifierOverflow() {
        assertThrows(EIDGeneratorOverflow.class, () -> {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.setIdentifier(Integer.MAX_VALUE); // Now, next should exceed Integer range.
            instance.getNextIdentifier(); // Integer range exceeded.
        });
    }

    /**
     * Test of setIdentifierIfGreater method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierIfGreaterWhenNotGreater() {
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
    public void testSetIdentifierIfGreaterWhenGreater() {
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
    public void testSetIdentifierWhenLower() {
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

    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierWhenGreater() {
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifier(10); // Sets 10. Next should be 11
            assertEquals(11, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierWhenIllegarArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            TIDGenerator instance = new TIDGenerator();
            instance.setIdentifier(instance.getNextIdentifier()-2); // This is DEFAULT_ID-1 that should throws an exception
        });
    }    
}
