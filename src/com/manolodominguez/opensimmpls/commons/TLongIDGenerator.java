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

/**
 * This class implements a ID generator that generates consecutive numeric IDs.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLongIDGenerator {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TLongIDGenerator.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TLongIDGenerator() {
        // FIX: Do not use harcoded values. Use class constants instead.
        this.identifier = 0;
    }

    /**
     * This method resets the ID generator to its initial internal value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void reset() {
        // FIX: Do not use harcoded values. Use class constants instead.
        this.identifier = 0;
    }

    /**
     * This method generates a new ID.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a long value that is unique.
     * @throws EIDGeneratorOverflow when the ID generator reaches its maximum
     * value.
     * @since 2.0
     */
    synchronized public long getNextIdentifier() throws EIDGeneratorOverflow {
        if (this.identifier >= Long.MAX_VALUE) {
            throw new EIDGeneratorOverflow();
        } else {
            this.identifier++;
        }
        return (this.identifier);
    }

    private long identifier;
}
