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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
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
