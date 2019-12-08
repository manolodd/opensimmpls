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
import static org.junit.jupiter.api.Assertions.fail;
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
     * Test of constructor, of class TIDGenerator.
     */
    @Test
    public void testConstructor() {
        System.out.println("Testing TIDGenerator constructor");
        try {
            TIDGenerator instance = new TIDGenerator();
            assertEquals(1, instance.getNextIdentifier()); // should return 1.
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }    
    
    /**
     * Test of reset method, of class TIDGenerator.
     */
    @Test
    public void testReset() {
        System.out.println("Testing reset()");
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.reset(); //reset to 0
            assertEquals(1, instance.getNextIdentifier()); // should return 1.
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of getNextIdentifier method, of class TIDGenerator.
     *
     */
    @Test
    public void testGetNextIdentifier() {
        System.out.println("testing getNextIdentifier() without overflow");
        try {
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            assertEquals(5, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of getNextIdentifier method, of class TIDGenerator.
     *
     */
    @Test
    public void testGetNextIdentifierOverflow() {
        System.out.println("testing getNextIdentifier() with overflow");
        assertThrows(EIDGeneratorOverflow.class, () -> {
            TIDGenerator instance = new TIDGenerator();
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
            System.out.println("testing getNextIdentifier() when argument is positive and not greater");
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifierIfGreater(2); // Remains 4, next should be 5
            assertEquals(5, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIdentifierIfGreater method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierIfGreaterWhenNegative() {
        try {
            System.out.println("testing getNextIdentifier() when argument is negative");
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifierIfGreater(-2); // Remains 4, next should be 5
            assertEquals(5, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIdentifierIfGreater method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierIfGreaterWhenGreater() {
        try {
            System.out.println("testing getNextIdentifier() when argument is positive and greater");
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifierIfGreater(10); // Set 10, next should be 11
            assertEquals(11, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierWhenLower() {
        try {
            System.out.println("testing setIdentifier() when argument is lower than current value");
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifier(2); // Sets 2. Next should be 3
            assertEquals(3, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierWhenGreater() {
        try {
            System.out.println("testing setIdentifier() when argument is greater than current value");
            TIDGenerator instance = new TIDGenerator();
            instance.getNextIdentifier(); // should return 1. Next 2
            instance.getNextIdentifier(); // should return 2. Next 3
            instance.getNextIdentifier(); // should return 3. Next 4
            instance.getNextIdentifier(); // should return 4. Next 5
            instance.setIdentifier(10); // Sets 10. Next should be 11
            assertEquals(11, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            TIDGenerator instance = new TIDGenerator();
            instance.setIdentifier(-2); // This is lower than DEFAULT_ID and should throws an exception
        });
    }
}
