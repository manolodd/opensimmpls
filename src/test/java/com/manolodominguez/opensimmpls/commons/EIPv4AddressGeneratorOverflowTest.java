/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manolodominguez.opensimmpls.commons;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.util.ResourceBundle;
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
public class EIPv4AddressGeneratorOverflowTest {
    
    public EIPv4AddressGeneratorOverflowTest() {
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
     * Test of toString method, of class EIPv4AddressGeneratorOverflow.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        EIPv4AddressGeneratorOverflow instance = new EIPv4AddressGeneratorOverflow();
        ResourceBundle translations = ResourceBundle.getBundle(AvailableBundles.E_IPV4_ADDRESS_GENERATOR_OVERFLOW.getPath());
        assertEquals(translations.getString("exceptionDescription"), instance.toString());
    }
    
}
