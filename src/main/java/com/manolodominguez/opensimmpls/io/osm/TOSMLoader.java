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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import com.manolodominguez.opensimmpls.scenario.TInternalLink;
import com.manolodominguez.opensimmpls.scenario.TExternalLink;
import com.manolodominguez.opensimmpls.scenario.TScenario;
import com.manolodominguez.opensimmpls.scenario.TLERNode;
import com.manolodominguez.opensimmpls.scenario.TTrafficGeneratorNode;
import com.manolodominguez.opensimmpls.scenario.TTrafficSinkNode;
import com.manolodominguez.opensimmpls.scenario.TActiveLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLSRNode;
import com.manolodominguez.opensimmpls.scenario.TActiveLERNode;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a class that loads a scenario from disk in OSM (Open
 * SimMPLS format).
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TOSMLoader {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TOSMLoader.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TOSMLoader() {
        this.scenario = new TScenario();
        this.inputStream = null;
        this.input = null;
        this.translations = ResourceBundle.getBundle(AvailableBundles.OSM_LOADER.getPath());
    }

    /**
     * This method loads an scenario description from a file formated as OSM.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param inputFile The file where a scenario description is stored.
     * @return true, if the file can be correctly loaded. False on the contrary.
     * @since 2.0
     */
    public boolean load(File inputFile) {
        String stringAux = "";
        int configSection = TOSMLoader.LOOKING_FOR_A_NEW_CONFIG_SECTION;
        this.scenario.setScenarioFile(inputFile);
        try {
            if (inputFile.exists()) {
                this.inputStream = new FileInputStream(inputFile);
                this.input = new BufferedReader(new InputStreamReader(this.inputStream));
                while ((stringAux = this.input.readLine()) != null) { // Read till EOF
                    // This code read lines from the file, sequentially, 
                    // untill it detects tokens that allow to identify 
                    // different sections of the configuration file. 
                    // Do not load blank linkes, comments and lines that
                    // store CRC info (deprecated, but still present in some
                    // scenarios).
                    if ((!stringAux.equals("")) && (!stringAux.startsWith("//")) && (!stringAux.startsWith("@CRC#"))) {
                        switch (configSection) {
                            case TOSMLoader.LOOKING_FOR_A_NEW_CONFIG_SECTION:
                                if (stringAux.startsWith("@?Escenario")) {
                                    configSection = TOSMLoader.SCENARIO;
                                } else if (stringAux.startsWith("@?Topologia")) {
                                    configSection = TOSMLoader.TOPOLOGY;
                                } else if (stringAux.startsWith("@?Simulacion")) {
                                    configSection = TOSMLoader.SIMULATION;
                                } else if (stringAux.startsWith("@?Analisis")) {
                                    configSection = TOSMLoader.ANALISYS;
                                }
                                break;
                            case TOSMLoader.SCENARIO:
                                if (stringAux.startsWith("@!Escenario")) {
                                    configSection = TOSMLoader.LOOKING_FOR_A_NEW_CONFIG_SECTION;
                                } else {
                                    loadScenario(stringAux);
                                }
                                break;
                            case TOSMLoader.TOPOLOGY:
                                if (stringAux.startsWith("@!Topologia")) {
                                    configSection = TOSMLoader.LOOKING_FOR_A_NEW_CONFIG_SECTION;
                                } else {
                                    loadTopology(stringAux);
                                }
                                break;
                            case TOSMLoader.SIMULATION:
                                if (stringAux.startsWith("@!Simulacion")) {
                                    configSection = TOSMLoader.LOOKING_FOR_A_NEW_CONFIG_SECTION;
                                }
                                break;
                            case TOSMLoader.ANALISYS:
                                if (stringAux.startsWith("@!Analisis")) {
                                    configSection = TOSMLoader.LOOKING_FOR_A_NEW_CONFIG_SECTION;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                this.inputStream.close();
                this.input.close();
                this.scenario.setAlreadySaved(true);
                this.scenario.setModified(false);
            }
        } catch (IOException e) {
            this.logger.error(translations.getString("ioexceptionDescription"));
            return false;
        }
        return true;
    }
    
    private void loadTopology(String topologyString) {
        if (topologyString.startsWith("#Receptor#")) {
            TTrafficSinkNode receiver = new TTrafficSinkNode(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, TOSMLoader.DEFAULT_IPV4_ADDRESS, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (receiver.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(receiver);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(receiver.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(receiver.getIPv4Address());
            }
        } else if (topologyString.startsWith("#Emisor#")) {
            TTrafficGeneratorNode sender = new TTrafficGeneratorNode(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, TOSMLoader.DEFAULT_IPV4_ADDRESS, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (sender.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(sender);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(sender.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(sender.getIPv4Address());
            }
        } else if (topologyString.startsWith("#LER#")) {
            TLERNode ler = new TLERNode(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, TOSMLoader.DEFAULT_IPV4_ADDRESS, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (ler.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(ler);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(ler.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(ler.getIPv4Address());
            }
        } else if (topologyString.startsWith("#LERA#")) {
            TActiveLERNode activeLER = new TActiveLERNode(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, TOSMLoader.DEFAULT_IPV4_ADDRESS, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (activeLER.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(activeLER);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(activeLER.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(activeLER.getIPv4Address());
            }
        } else if (topologyString.startsWith("#LSR#")) {
            TLSRNode lsr = new TLSRNode(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, TOSMLoader.DEFAULT_IPV4_ADDRESS, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (lsr.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(lsr);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(lsr.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(lsr.getIPv4Address());
            }
        } else if (topologyString.startsWith("#LSRA#")) {
            TActiveLSRNode activeLSR = new TActiveLSRNode(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, TOSMLoader.DEFAULT_IPV4_ADDRESS, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (activeLSR.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(activeLSR);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(activeLSR.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(activeLSR.getIPv4Address());
            }
        } else if (topologyString.startsWith("#EnlaceExterno#")) {
            TExternalLink externalLink = new TExternalLink(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (externalLink.fromOSMString(topologyString)) {
                this.scenario.getTopology().addLink(externalLink);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(externalLink.getID());
            }
        } else if (topologyString.startsWith("#EnlaceInterno#")) {
            TInternalLink internalLink = new TInternalLink(TOSMLoader.DEFAULT_TOPOLOGY_ELEMENT_ID, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (internalLink.fromOSMString(topologyString)) {
                this.scenario.getTopology().addLink(internalLink);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(internalLink.getID());
            }
        } else {
            throw new IllegalArgumentException("topologyString does not have the correct format");
        }
    }
    
    private void loadScenario(String scenarioString) {
        if (scenarioString.startsWith("#Titulo#")) {
            if (!this.scenario.unmarshallTitle(scenarioString)) {
                this.scenario.setTitle(TOSMLoader.DEFAULT_TITLE);
            }
        } else if (scenarioString.startsWith("#Autor#")) {
            if (!this.scenario.unmarshallAuthor(scenarioString)) {
                this.scenario.setAuthor(TOSMLoader.DEFAULT_AUTHOR);
            }
        } else if (scenarioString.startsWith("#Descripcion#")) {
            if (!this.scenario.unmarshallDescription(scenarioString)) {
                this.scenario.setDescription(TOSMLoader.DEFAULT_DESCRIPTION);
            }
        } else if (scenarioString.startsWith("#Temporizacion#")) {
            if (!this.scenario.getSimulation().unmarshallTimeParameters(scenarioString)) {
                this.scenario.getSimulation().setSimulationLengthInNs(TOSMLoader.DEFAULT_SIMULATION_LENGTH_IN_NS);
                this.scenario.getSimulation().setSimulationTickDurationInNs(TOSMLoader.DEFAULT_SIMULATION_TICK_DURATION_IN_NS);
            }
        } else {
            throw new IllegalArgumentException("scenarioString does not have the correct format");
        }
    }

    /**
     * This method gets the scenario that has been loaded from file.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TScenario, the scenario loaded from a file. Null if no scenario
     * has been loaded.
     * @since 2.0
     */
    public TScenario getScenario() {
        return this.scenario;
    }
    
    private static final int LOOKING_FOR_A_NEW_CONFIG_SECTION = 0;
    private static final int SCENARIO = 1;
    private static final int TOPOLOGY = 2;
    private static final int SIMULATION = 3;
    private static final int ANALISYS = 4;
    
    private static final int DEFAULT_SIMULATION_LENGTH_IN_NS = 500;
    private static final int DEFAULT_SIMULATION_TICK_DURATION_IN_NS = 1;
    private static final String DEFAULT_IPV4_ADDRESS = "10.0.0.1";
    private static final int DEFAULT_TOPOLOGY_ELEMENT_ID = 0;
    private static final String DEFAULT_TITLE = "";
    private static final String DEFAULT_AUTHOR = "";
    private static final String DEFAULT_DESCRIPTION = "";
    
    private TScenario scenario;
    private FileInputStream inputStream;
    private BufferedReader input;
    private final ResourceBundle translations;
    private final Logger logger = LoggerFactory.getLogger(TOSMLoader.class);
}
