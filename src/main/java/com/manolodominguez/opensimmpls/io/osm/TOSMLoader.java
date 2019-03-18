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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.CRC32;
import com.manolodominguez.opensimmpls.scenario.TInternalLink;
import com.manolodominguez.opensimmpls.scenario.TExternalLink;
import com.manolodominguez.opensimmpls.scenario.TScenario;
import com.manolodominguez.opensimmpls.scenario.TLERNode;
import com.manolodominguez.opensimmpls.scenario.TTrafficGeneratorNode;
import com.manolodominguez.opensimmpls.scenario.TTrafficSinkNode;
import com.manolodominguez.opensimmpls.scenario.TActiveLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLSRNode;
import com.manolodominguez.opensimmpls.scenario.TActiveLERNode;

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
        this.scenarioCRC = new CRC32();
        this.section = TOSMLoader.NONE;
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
        if (this.fileIsValid(inputFile)) {
            String stringAux = "";
            this.scenario.setScenarioFile(inputFile);
            try {
                if (inputFile.exists()) {
                    this.inputStream = new FileInputStream(inputFile);
                    this.input = new BufferedReader(new InputStreamReader(this.inputStream));
                    while ((stringAux = this.input.readLine()) != null) {
                        if ((!stringAux.equals("")) && (!stringAux.startsWith("//")) && (!stringAux.startsWith("@CRC#"))) {
                            if (this.section == TOSMLoader.NONE) {
                                if (stringAux.startsWith("@?Escenario")) {
                                    this.section = TOSMLoader.SCENARIO;
                                } else if (stringAux.startsWith("@?Topologia")) {
                                    this.section = TOSMLoader.TOPOLOGY;
                                } else if (stringAux.startsWith("@?Simulacion")) {
                                    this.section = TOSMLoader.SIMULATION;
                                } else if (stringAux.startsWith("@?Analisis")) {
                                    this.section = TOSMLoader.ANALISYS;
                                }
                            } else if (section == TOSMLoader.SCENARIO) {
                                loadScenario(stringAux);
                            } else if (section == TOSMLoader.TOPOLOGY) {
                                loadTopology(stringAux);
                            } else if (section == TOSMLoader.SIMULATION) {
                                if (stringAux.startsWith("@!Simulacion")) {
                                    this.section = TOSMLoader.NONE;
                                }
                            } else if (section == TOSMLoader.ANALISYS) {
                                if (stringAux.startsWith("@!Analisis")) {
                                    this.section = TOSMLoader.NONE;
                                }
                            }
                        }
                    }
                    this.inputStream.close();
                    this.input.close();
                    this.scenario.setAlreadySaved(true);
                    this.scenario.setModified(false);
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void loadTopology(String topologyString) {
        if (topologyString.startsWith("@!Topologia")) {
            this.section = TOSMLoader.NONE;
        } else if (topologyString.startsWith("#Receptor#")) {
            TTrafficSinkNode receiver = new TTrafficSinkNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (receiver.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(receiver);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(receiver.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(receiver.getIPv4Address());
            }
            receiver = null;
        } else if (topologyString.startsWith("#Emisor#")) {
            TTrafficGeneratorNode sender = new TTrafficGeneratorNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (sender.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(sender);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(sender.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(sender.getIPv4Address());
            }
            sender = null;
        } else if (topologyString.startsWith("#LER#")) {
            TLERNode ler = new TLERNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (ler.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(ler);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(ler.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(ler.getIPv4Address());
            }
            ler = null;
        } else if (topologyString.startsWith("#LERA#")) {
            TActiveLERNode activeLER = new TActiveLERNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (activeLER.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(activeLER);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(activeLER.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(activeLER.getIPv4Address());
            }
            activeLER = null;
        } else if (topologyString.startsWith("#LSR#")) {
            TLSRNode lsr = new TLSRNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (lsr.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(lsr);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(lsr.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(lsr.getIPv4Address());
            }
            lsr = null;
        } else if (topologyString.startsWith("#LSRA#")) {
            TActiveLSRNode activeLSR = new TActiveLSRNode(0, "10.0.0.1", this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (activeLSR.fromOSMString(topologyString)) {
                this.scenario.getTopology().addNode(activeLSR);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(activeLSR.getNodeID());
                this.scenario.getTopology().getIPv4AddressGenerator().setIPv4AddressIfGreater(activeLSR.getIPv4Address());
            }
            activeLSR = null;
        } else if (topologyString.startsWith("#EnlaceExterno#")) {
            TExternalLink externalLink = new TExternalLink(0, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (externalLink.fromOSMString(topologyString)) {
                this.scenario.getTopology().addLink(externalLink);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(externalLink.getID());
            }
            externalLink = null;
        } else if (topologyString.startsWith("#EnlaceInterno#")) {
            TInternalLink internalLink = new TInternalLink(0, this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            if (internalLink.fromOSMString(topologyString)) {
                this.scenario.getTopology().addLink(internalLink);
                this.scenario.getTopology().getElementsIDGenerator().setIdentifierIfGreater(internalLink.getID());
            }
            internalLink = null;
        }
    }

    private void loadScenario(String scenarioString) {
        if (scenarioString.startsWith("@!Escenario")) {
            this.section = TOSMLoader.NONE;
        } else if (scenarioString.startsWith("#Titulo#")) {
            if (!this.scenario.unmarshallTitle(scenarioString)) {
                this.scenario.setTitle("");
            }
        } else if (scenarioString.startsWith("#Autor#")) {
            if (!this.scenario.unmarshallAuthor(scenarioString)) {
                this.scenario.setAuthor("");
            }
        } else if (scenarioString.startsWith("#Descripcion#")) {
            if (!this.scenario.unmarshallDescription(scenarioString)) {
                this.scenario.setDescription("");
            }
        } else if (scenarioString.startsWith("#Temporizacion#")) {
            if (!this.scenario.getSimulation().unmarshallTimeParameters(scenarioString)) {
                //FIX: Avoid using harcoded values. Use class constants instead.
                this.scenario.getSimulation().setSimulationLengthInNs(500);
                this.scenario.getSimulation().setSimulationTickDurationInNs(1);
            }
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

    private boolean fileIsValid(File f) {
        String inputFileCRC = "";
        String computedFileCRC = "@CRC#";
        this.scenarioCRC.reset();
        String auxString = "";
        try {
            if (f.exists()) {
                FileInputStream inputFile = new FileInputStream(f);
                BufferedReader ent = new BufferedReader(new InputStreamReader(inputFile));
                while ((auxString = ent.readLine()) != null) {
                    if ((!auxString.equals("")) && (!auxString.startsWith("//"))) {
                        if (auxString.startsWith("@CRC#")) {
                            inputFileCRC = auxString;
                        } else {
                            this.scenarioCRC.update(auxString.getBytes());
                        }
                    }
                }
                inputFile.close();
                ent.close();
                if (inputFileCRC.equals("")) {
                    return true;
                } else {
                    computedFileCRC += Long.toString(this.scenarioCRC.getValue());
                    if (computedFileCRC.equals(inputFileCRC)) {
                        return true;
                    }
                    return false;
                }
            }
        } catch (IOException e) {
            this.scenarioCRC.reset();
            return false;
        }
        this.scenarioCRC.reset();
        return false;
    }

    private static final int NONE = 0;
    private static final int SCENARIO = 1;
    private static final int TOPOLOGY = 2;
    private static final int SIMULATION = 3;
    private static final int ANALISYS = 4;

    private int section;
    private CRC32 scenarioCRC;
    private TScenario scenario;
    private FileInputStream inputStream;
    private BufferedReader input;
}
