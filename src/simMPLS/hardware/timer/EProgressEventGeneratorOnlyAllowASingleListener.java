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
package simMPLS.hardware.timer;

/**
 * This class implements an exception that will be thrown in those situations
 * when a single IProgressEventListener is allowed and there is an attempt of
 * subscribing an aditional one.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class EProgressEventGeneratorOnlyAllowASingleListener extends Exception {

    /**
     * This is the constructor of the class. It creates a new instance of
     * EProgressEventGeneratorOnlyAllowASingleListener.
     *
     * @since 1.0
     */
    public EProgressEventGeneratorOnlyAllowASingleListener() {
    }

    /**
     * This method returns a text showing the cause of the exception.
     *
     * @return A string explaining the cause of the exception.
     */
    public String toString() {
        return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("EProgresoUnSoloSuscriptor.texto"));
    }

}
