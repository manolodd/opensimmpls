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
public class TIPv4AddressGeneratorTest {

    public TIPv4AddressGeneratorTest() {
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
     * Test of constructor, of class TIPv4AddressGenerator.
     */
    @Test
    public void testConstructor() {
        try {
            System.out.println("Test constructor of TIPv4AddressGenerator");
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            assertEquals("10.0.0.1", instance.getNextIPv4Address()); // First IPv4 addres returned is 10.0.0.1
        } catch (EIPv4AddressGeneratorOverflow ex) {
            Logger.getLogger(TIPv4AddressGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIPv4AddressGeneratorOverflow thrown");
        }
    }

    /**
     * Test of reset method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testReset() {
        try {
            System.out.println("Test reset()");
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.getNextIPv4Address(); // returns 10.0.0.1. Next 10.0.0.2
            instance.getNextIPv4Address(); // returns 10.0.0.2. Next 10.0.0.3
            instance.getNextIPv4Address(); // returns 10.0.0.3. Next 10.0.0.4
            instance.getNextIPv4Address(); // returns 10.0.0.4. Next 10.0.0.5
            instance.reset();
            assertEquals("10.0.0.1", instance.getNextIPv4Address()); // First IPv4 addres returned is 10.0.0.1
        } catch (EIPv4AddressGeneratorOverflow ex) {
            Logger.getLogger(TIPv4AddressGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIPv4AddressGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenGreater() {
        try {
            System.out.println("Test setIPv4AddressIfGreater() when greater");
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.getNextIPv4Address(); // returns 10.0.0.1. Next 10.0.0.2
            instance.getNextIPv4Address(); // returns 10.0.0.2. Next 10.0.0.3
            instance.getNextIPv4Address(); // returns 10.0.0.3. Next 10.0.0.4
            instance.getNextIPv4Address(); // returns 10.0.0.4. Next 10.0.0.5
            instance.setIPv4AddressIfGreater("10.0.0.6");
            assertEquals("10.0.0.7", instance.getNextIPv4Address()); // Next IPv4 addres returned is 10.0.0.7
        } catch (EIPv4AddressGeneratorOverflow ex) {
            Logger.getLogger(TIPv4AddressGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIPv4AddressGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenLower() {
        try {
            System.out.println("Test setIPv4AddressIfGreater() when lower");
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.getNextIPv4Address(); // returns 10.0.0.1. Next 10.0.0.2
            instance.getNextIPv4Address(); // returns 10.0.0.2. Next 10.0.0.3
            instance.getNextIPv4Address(); // returns 10.0.0.3. Next 10.0.0.4
            instance.getNextIPv4Address(); // returns 10.0.0.4. Next 10.0.0.5
            instance.setIPv4AddressIfGreater("10.0.0.2");
            assertEquals("10.0.0.5", instance.getNextIPv4Address()); // Next IPv4 addres returned is 10.0.0.5
        } catch (EIPv4AddressGeneratorOverflow ex) {
            Logger.getLogger(TIPv4AddressGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIPv4AddressGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenNull() {
        System.out.println("Test setIPv4AddressIfGreater() when null");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater(null); // This causes a IllegalArgumentException exception. 
        });
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address1() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 1");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("This is not an IPv4 address"); // This causes a IllegalArgumentException exception. 
        });
    }
    
    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address2() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 2");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.0.0.0"); // This IPva address is reserved. 
        });
    }
    
    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address3() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 3");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.255.255.255"); // This IPva address is reserved.  
        });
    }
    
    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address4() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 4");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.256.0.0"); // This is nor a valid IPv4 address. 
        });
    }
    
    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address5() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 5");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.0.256.0"); // This is nor a valid IPv4 address. 
        });
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address6() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 6");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.0.0.256"); // This is nor a valid IPv4 address. 
        });
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address7() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 7");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("12.0.0.1"); // Not a valid 10.0.0.0/8 IPv4 address
        });
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address8() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 8");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.-1.0.1"); // This is nor a valid IPv4 address. 
        });
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address9() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 9");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.0.-1.1"); // This is nor a valid IPv4 address. 
        });
    }

    /**
     * Test of setIPv4AddressIfGreater method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testSetIPv4AddressIfGreaterWhenInvalidIPv4Address10() {
        System.out.println("Test setIPv4AddressIfGreater() when invalid IPv4 address 10");
        assertThrows(IllegalArgumentException.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.0.0.-1"); // This is nor a valid IPv4 address. 
        });
    }

    /**
     * Test of getNextIPv4Address method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testGetNextIPv4Address() {
        try {
            System.out.println("Test getNextIPv4Address() without overflow");
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.getNextIPv4Address(); // returns 10.0.0.1. Next 10.0.0.2
            instance.getNextIPv4Address(); // returns 10.0.0.2. Next 10.0.0.3
            instance.getNextIPv4Address(); // returns 10.0.0.3. Next 10.0.0.4
            instance.getNextIPv4Address(); // returns 10.0.0.4. Next 10.0.0.5
            assertEquals("10.0.0.5", instance.getNextIPv4Address()); // Next IPv4 addres returned is 10.0.0.5
        } catch (EIPv4AddressGeneratorOverflow ex) {
            Logger.getLogger(TIPv4AddressGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIPv4AddressGeneratorOverflow thrown");
        }
    }

    /**
     * Test of getNextIPv4Address method, of class TIPv4AddressGenerator.
     */
    @Test
    public void testGetNextIPv4AddressOverflow() {
        System.out.println("Test testGetNextIPv4Address() with overflow");
        assertThrows(EIPv4AddressGeneratorOverflow.class, () -> {
            TIPv4AddressGenerator instance = new TIPv4AddressGenerator();
            instance.setIPv4AddressIfGreater("10.255.255.254"); // This is the max value of the generator.
            instance.getNextIPv4Address(); // Will throw a EIPv4AddressGeneratorOverflow exception
        });
    }
}
