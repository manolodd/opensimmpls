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
import com.manolodominguez.opensimmpls.scenario.TScene;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public TOSMSaver(TScene scenario) {
        if (scenario == null) {
            this.logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException("scenario is null");
        }
        this.scenario = scenario;
        this.outputStream = null;
        this.output = null;
        this.translations = ResourceBundle.getBundle(AvailableBundles.T_OSM_SAVER.getPath());
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
        if (outputFile == null) {
            this.logger.error(translations.getString("badArgument"));
            throw new IllegalArgumentException("outputFile is null");
        }
        try {
            TNode auxNode;
            TLink auxLink;
            Iterator<TNode> nodesIterator;
            Iterator<TLink> linksIterator;
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
            this.output.println();
            this.output.println(this.scenario.marshallTitle());
            this.output.println(this.scenario.marshallAuthor());
            this.output.println(this.scenario.marshallDescription());
            this.output.println(this.scenario.getSimulation().marshallTimeParameters());
            this.output.println();
            this.output.println("@!Escenario");
            this.output.println();
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.DefinicionDeLaTopologiaDelEscenario"));
            this.output.println(this.translations.getString("TAlmacenadorOSM.asteriscos"));
            this.output.println();
            this.output.println("@?Topologia");
            this.output.println();
            // Saving traffic receivers.
            nodesIterator = this.scenario.getTopology().getNodesIterator();
            while (nodesIterator.hasNext()) {
                auxNode = nodesIterator.next();
                if (auxNode != null) {
                    if (auxNode.getNodeType() == TNode.TRAFFIC_SINK) {
                        this.output.println(auxNode.toOSMString());
                    }
                }
            }
            // Saving other nodes.
            nodesIterator = this.scenario.getTopology().getNodesIterator();
            while (nodesIterator.hasNext()) {
                auxNode = nodesIterator.next();
                if (auxNode != null) {
                    if (auxNode.getNodeType() != TNode.TRAFFIC_SINK) {
                        this.output.println(auxNode.toOSMString());
                    }
                }
            }
            // Saving links
            linksIterator = this.scenario.getTopology().getLinksIterator();
            while (linksIterator.hasNext()) {
                auxLink = linksIterator.next();
                if (auxLink != null) {
                    this.output.println(auxLink.toOSMString());
                }
            }
            this.output.println();
            this.output.println("@!Topologia");
            this.outputStream.close();
            this.output.close();
        } catch (IOException e) {
            this.logger.error(translations.getString("ioexceptionDescription"));
            return false;
        }
        return true;
    }

    private TScene scenario;
    private FileOutputStream outputStream;
    private PrintStream output;
    private ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TOSMSaver.class);   
}
