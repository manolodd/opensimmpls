/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manolodominguez.opensimmpls.commons;

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
public class UnitsTranslationsTest {
    
    public UnitsTranslationsTest() {
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
     * Test of values method, of class UnitsTranslations.
     */
    @Test
    public void testValues() {
        System.out.println("Test values()");
        UnitsTranslations[] enumValues = UnitsTranslations.values();
        assertEquals(4, enumValues.length);
    }

    /**
     * Test of valuesOf method, of class UnitsTranslations.
     */
    @Test
    public void testValuesOf() {
        System.out.println("Test valueOf()");
        boolean result = true;
        UnitsTranslations[] enumValues = UnitsTranslations.values();
        for (UnitsTranslations singleValue: enumValues) {
            if (singleValue.getUnits() < 0) {
                result = false;
            }
        }
        assertTrue(result);
    }    
    
    /**
     * Test of getUnits method, of class UnitsTranslations.
     */
    @Test
    public void testGetUnits() {
        System.out.println("Test getUnits()");
        UnitsTranslations instance = UnitsTranslations.BITS_PER_OCTETS;
        assertEquals(8, instance.getUnits());
    }
    
}
