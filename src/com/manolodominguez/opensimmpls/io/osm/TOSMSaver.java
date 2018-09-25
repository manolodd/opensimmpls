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
package com.manolodominguez.opensimmpls.io.osm;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.zip.CRC32;
import com.manolodominguez.opensimmpls.scenario.TScenario;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import java.util.ResourceBundle;

/**
 * This class implements a class that stores a scenario to disk in OSM (Open
 * SimMPLS format).
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TOSMSaver {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TOSMSaver.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param scenario The TScenario object to be stored in disk.
     * @since 2.0
     */
    public TOSMSaver(TScenario scenario) {
        this.scenario = scenario;
        this.outputStream = null;
        this.output = null;
        this.scenarioCRC = new CRC32();
        this.translations = ResourceBundle.getBundle(AvailableBundles.OSM_SAVER.getPath());
    }

    /**
     * This method saves a scenario to a disk file.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param outputFile The file where the scenario will be stored.
     * @param createCRC If true, a CRC hash of the file will be computed to
     * assure no manual modifications. If false, no CRC will be computed.
     * @return True, if the scenario can be saved successful. Otherwise, returns
     * false.
     * @since 2.0
     */
    public boolean save(File outputFile, boolean createCRC) {
        try {
            TNode auxNode;
            TLink auxLink;
            Iterator auxIterator;
            this.outputStream = new FileOutputStream(outputFile);
            this.output = new PrintStream(this.outputStream);
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.GeneradoPor"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.blanco"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.NoSeDebeModificarEsteFichero"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.PorqueIncorporaUnCodigoCRCParaQue"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.SimuladorPuedaComprobarSuIntegridad"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.ElSimuladorLoPodriaDetectarComoUn"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.FicheroCorrupto"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println();
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.DefinicionGlobalDelEscenario"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println();
            this.output.println("@?Escenario");
            this.scenarioCRC.update("@?Escenario".getBytes());
            this.output.println();
            this.output.println(this.scenario.marshallTitle());
            this.scenarioCRC.update(this.scenario.marshallTitle().getBytes());
            this.output.println(this.scenario.marshallAuthor());
            this.scenarioCRC.update(this.scenario.marshallAuthor().getBytes());
            this.output.println(this.scenario.marshallDescription());
            this.scenarioCRC.update(this.scenario.marshallDescription().getBytes());
            this.output.println(this.scenario.getSimulation().marshallTimeParameters());
            this.scenarioCRC.update(this.scenario.getSimulation().marshallTimeParameters().getBytes());
            this.output.println();
            this.output.println("@!Escenario");
            this.scenarioCRC.update("@!Escenario".getBytes());
            this.output.println();
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.DefinicionDeLaTopologiaDelEscenario"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println();
            this.output.println("@?Topologia");
            this.output.println();
            this.scenarioCRC.update("@?Topologia".getBytes());
            // Saving traffic receivers.
            auxIterator = this.scenario.getTopology().getNodesIterator();
            while (auxIterator.hasNext()) {
                auxNode = (TNode) auxIterator.next();
                if (auxNode != null) {
                    if (auxNode.getNodeType() == TNode.TRAFFIC_SINK) {
                        this.output.println(auxNode.toOSMString());
                        this.scenarioCRC.update(auxNode.toOSMString().getBytes());
                    }
                }
            }
            // Saving other nodes.
            auxIterator = this.scenario.getTopology().getNodesIterator();
            while (auxIterator.hasNext()) {
                auxNode = (TNode) auxIterator.next();
                if (auxNode != null) {
                    if (auxNode.getNodeType() != TNode.TRAFFIC_SINK) {
                        this.output.println(auxNode.toOSMString());
                        this.scenarioCRC.update(auxNode.toOSMString().getBytes());
                    }
                }
            }
            // Saving links
            auxIterator = this.scenario.getTopology().getLinksIterator();
            while (auxIterator.hasNext()) {
                auxLink = (TLink) auxIterator.next();
                if (auxLink != null) {
                    this.output.println(auxLink.toOSMString());
                    this.scenarioCRC.update(auxLink.toOSMString().getBytes());
                }
            }
            this.output.println();
            this.output.println("@!Topologia");
            this.scenarioCRC.update("@!Topologia".getBytes());
            if (createCRC) {
                String auxCRCHash = Long.toString(this.scenarioCRC.getValue());
                this.output.println();
                this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
                this.output.println(this.translations.getString("TAlmacenadorOSM.CodigoCRCParaLaIntegridadDelFichero"));
                this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
                this.output.println();
                this.output.println("@CRC#" + auxCRCHash);
            }
            this.outputStream.close();
            this.output.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private CRC32 scenarioCRC;
    private TScenario scenario;
    private FileOutputStream outputStream;
    private PrintStream output;
    private ResourceBundle translations;
}
