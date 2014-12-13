/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.protocols;

/**
 * This class implements a TCP packet content (payload of TCP packet). It is
 * used to simulate packets of different sizes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
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
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    public void setSize(int size) {
        //FIX: create a constant instead of this harcoded value
        this.size = 20;   // TCP header size in byts (octects).
        this.size += size;
    }

    private int size;
}
