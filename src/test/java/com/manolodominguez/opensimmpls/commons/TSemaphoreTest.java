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
public class TSemaphoreTest {
    
    public TSemaphoreTest() {
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
     * Test of setRed method, of class TSemaphore.
     */
    @Test
    public void testSetRed() {
        System.out.println("Test setRed()");
        TSemaphore instance = new TSemaphore();
        instance.setRed();
        assertTrue(instance.isRed());
    }

    /**
     * Test of setGreen method, of class TSemaphore.
     */
    @Test
    public void testSetGreen() {
        System.out.println("Test setGreen()");
        TSemaphore instance = new TSemaphore();
        instance.setRed();
        instance.setGreen();
        assertTrue(!instance.isRed());
    }

    /**
     * Test of isRed method, of class TSemaphore.
     */
    @Test
    public void testIsRedWhenGreen() {
        System.out.println("Test isRed()");
        TSemaphore instance = new TSemaphore();
        assertTrue(!instance.isRed());
    }
    
    /**
     * Test of isRed method, of class TSemaphore.
     */
    @Test
    public void testIsRedWhenRed() {
        System.out.println("Test isRed()");
        TSemaphore instance = new TSemaphore();
        instance.setRed();
        assertTrue(instance.isRed());
    }
}
