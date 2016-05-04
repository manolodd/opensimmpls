/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package simMPLS.protocols;

/**
 * This class implements a TCP packet content (payload of TCP packet). It is
 * used to simulate packets of different sizes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTCPPayload {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TTCPPayload having the size passed in as a parameter. TCP header size
     * will be added to the specified value, so specify only payload size.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param size The desired size for this TCP packet payload, in bytes
     * (octects).
     * @since 2.0
     */
    public TTCPPayload(int size) {
        // FIX: create a constant insted of this harcoded value.
        this.size = 20;   // TCP header size in bytes (octects)
        this.size += size;
    }

    /**
     * This method gets the size of this TCP packet. TCP header size is included
     * in the returned value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Te size for this TCP packet, including TCP header, in bytes
     * (octects).
     * @since 2.0
     */
    public int getSize() {
        return this.size;
    }

    /**
     * This method sets the size of this TCP packet payload. TCP header size
     * will be added to the specified value, so specify only payload size.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param size The desired size for this TCP packet payload, in bytes
     * (octects).
     * @since 2.0
     */
    public void setSize(int size) {
        //FIX: create a constant instead of this harcoded value
        this.size = 20;   // TCP header size in byts (octects).
        this.size += size;
    }

    private int size;
}
