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
        for (UnitsTranslations singleValue : enumValues) {
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

    /**
     * Test values, of class UnitsTranslations.
     */
    @Test
    public void testValuesContent() {
        System.out.println("Test values content");
        boolean worksFine = true;
        if (UnitsTranslations.BITS_PER_OCTETS.getUnits() != 8) {
            worksFine &= false;
        }
        if (UnitsTranslations.OCTETS_PER_KILOBYTE.getUnits() != 1024) {
            worksFine &= false;
        }
        if (UnitsTranslations.OCTETS_PER_MEGABYTE.getUnits() != (1024*1024)) {
            worksFine &= false;
        }
        if (UnitsTranslations.OCTETS_PER_GIGABYTE.getUnits() != (1024*1024*1024)) {
            worksFine &= false;
        }
        
        assertTrue(worksFine);
    }


}
