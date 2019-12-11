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
public class TLongIDGeneratorTest {

    public TLongIDGeneratorTest() {
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
     * Test of constructor, of class TLongIDGenerator.
     */
    @Test
    public void testConstructor() {
        System.out.println("Testing TLongIDGenerator constructor");
        try {
            TLongIDGenerator instance = new TLongIDGenerator();
            assertEquals(1L, instance.getNextIdentifier()); // should return 1L.
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TLongIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of reset method, of class TLongIDGenerator.
     */
    @Test
    public void testReset() {
        System.out.println("Testing reset()");
        try {
            TLongIDGenerator instance = new TLongIDGenerator();
            instance.getNextIdentifier(); // should return 1L. Next 2L
            instance.getNextIdentifier(); // should return 2L. Next 3L
            instance.getNextIdentifier(); // should return 3L. Next 4L
            instance.getNextIdentifier(); // should return 4L. Next 5L
            instance.reset(); //reset to 0L
            assertEquals(1L, instance.getNextIdentifier()); // should return 1L.
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TLongIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of getNextIdentifier method, of class TLongIDGenerator.
     */
    @Test
    public void testGetNextIdentifier() {
        System.out.println("testing getNextIdentifier() without overflow");
        try {
            TLongIDGenerator instance = new TLongIDGenerator();
            instance.getNextIdentifier(); // should return 1L. Next 2L
            instance.getNextIdentifier(); // should return 2L. Next 3L
            instance.getNextIdentifier(); // should return 3L. Next 4L
            instance.getNextIdentifier(); // should return 4L. Next 5L
            assertEquals(5L, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TLongIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of getNextIdentifier method, of class TLongIDGenerator.
     */
    @Test
    public void testGetNextIdentifierOverflow() {
        System.out.println("testing getNextIdentifier() with overflow");
        assertThrows(EIDGeneratorOverflow.class, () -> {
            TLongIDGenerator instance = new TLongIDGenerator();
            instance.setIdentifier(Long.MAX_VALUE - 1);
            instance.getNextIdentifier(); // should return Long.MAX_VALUE
            instance.getNextIdentifier(); // should throw an exception
        });
    }

    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierWhenLower() {
        try {
            System.out.println("testing setIdentifier() when argument is lower than current value");
            TLongIDGenerator instance = new TLongIDGenerator();
            instance.getNextIdentifier(); // should return 1L. Next 2L
            instance.getNextIdentifier(); // should return 2L. Next 3L
            instance.getNextIdentifier(); // should return 3L. Next 4L
            instance.getNextIdentifier(); // should return 4L. Next 5L
            instance.setIdentifier(2); // Sets 2L. Next should be 3L
            assertEquals(3L, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TLongIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
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
            TLongIDGenerator instance = new TLongIDGenerator();
            instance.getNextIdentifier(); // should return 1L. Next 2L
            instance.getNextIdentifier(); // should return 2L. Next 3L
            instance.getNextIdentifier(); // should return 3L. Next 4L
            instance.getNextIdentifier(); // should return 4L. Next 5L
            instance.setIdentifier(10L); // Sets 10L. Next should be 11L
            assertEquals(11L, instance.getNextIdentifier());
        } catch (EIDGeneratorOverflow ex) {
            Logger.getLogger(TLongIDGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Unexpected EIDGeneratorOverflow thrown");
        }
    }

    /**
     * Test of setIdentifier method, of class TIDGenerator.
     */
    @Test
    public void testSetIdentifierWhenNegative() {
        System.out.println("testing setIdentifier() when argument is negative");
        assertThrows(IllegalArgumentException.class, () -> {
            TLongIDGenerator instance = new TLongIDGenerator();
            instance.setIdentifier(-2); // This is lower than DEFAULT_ID and should throws an exception
        });
    }
}
