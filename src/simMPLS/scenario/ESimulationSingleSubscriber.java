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
package simMPLS.scenario;

/**
 * Esta clase implementa una excepci�n que se utilizar� cuando un contador de
 * progreso intente a�adir un nuevo suscriptor, teniendo ya uno previo.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
/**
 * This class implements an exception that will be thrown when more than a
 * single listener is subscribed to the progress event generator.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class ESimulationSingleSubscriber extends Exception {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of ESimulationSingleSubscriber.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public ESimulationSingleSubscriber() {
    }

    /**
     * This method return a localized message explaining the cause of this
     * exception.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a localized message explaining the cause of this exception.
     * @since 1.0
     */
    @Override
    public String toString() {
        return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("ESimulacionUnSoloSuscriptor.toString"));
    }

}
