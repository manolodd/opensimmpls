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
package simMPLS.hardware.timer;

/**
 * This class implements an exception that will be thrown in those situations
 * when a single IProgressEventListener is allowed and there is an attempt of
 * subscribing an aditional one.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class EProgressEventGeneratorOnlyAllowASingleListener extends Exception {

    /**
     * This is the constructor of the class. It creates a new instance of
     * EProgressEventGeneratorOnlyAllowASingleListener.
     *
     * @since 2.0
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
