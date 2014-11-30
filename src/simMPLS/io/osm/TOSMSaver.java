/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed auxIterator the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.io.osm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.zip.CRC32;
import simMPLS.scenario.TScenario;
import simMPLS.scenario.TLink;
import simMPLS.scenario.TNode;

/**
 * This class implements a class that stores a scenario to disk in OSM (Open
 * SimMPLS format).
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TOSMSaver {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TOSMSaver.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param scenario The TScenario object to be stored in disk.
     * @since 1.0
     */
    public TOSMSaver(TScenario scenario) {
        this.scenario = scenario;
        this.outputStream = null;
        this.output = null;
        this.scenarioCRC = new CRC32();
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
     * @since 1.0
     */
    public boolean save(File outputFile, boolean createCRC) {
        try {
            TNode auxNode;
            TLink auxLink;
            Iterator auxIterator;
            this.outputStream = new FileOutputStream(outputFile);
            this.output = new PrintStream(this.outputStream);
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.GeneradoPor"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.blanco"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.NoSeDebeModificarEsteFichero"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.PorqueIncorporaUnCodigoCRCParaQue"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.SimuladorPuedaComprobarSuIntegridad"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.ElSimuladorLoPodriaDetectarComoUn"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.FicheroCorrupto"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            this.output.println();
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.DefinicionGlobalDelEscenario"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
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
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.DefinicionDeLaTopologiaDelEscenario"));
            this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            this.output.println();
            this.output.println("@?Topologia");
            this.output.println();
            this.scenarioCRC.update("@?Topologia".getBytes());
            // Saving traffic receivers.
            auxIterator = this.scenario.getTopology().getNodesIterator();
            while (auxIterator.hasNext()) {
                auxNode = (TNode) auxIterator.next();
                if (auxNode != null) {
                    if (auxNode.getNodeType() == TNode.RECEIVER) {
                        this.output.println(auxNode.marshall());
                        this.scenarioCRC.update(auxNode.marshall().getBytes());
                    }
                }
            }
            // Saving other nodes.
            auxIterator = this.scenario.getTopology().getNodesIterator();
            while (auxIterator.hasNext()) {
                auxNode = (TNode) auxIterator.next();
                if (auxNode != null) {
                    if (auxNode.getNodeType() != TNode.RECEIVER) {
                        this.output.println(auxNode.marshall());
                        this.scenarioCRC.update(auxNode.marshall().getBytes());
                    }
                }
            }
            // Saving links
            auxIterator = this.scenario.getTopology().getLinksIterator();
            while (auxIterator.hasNext()) {
                auxLink = (TLink) auxIterator.next();
                if (auxLink != null) {
                    this.output.println(auxLink.marshall());
                    this.scenarioCRC.update(auxLink.marshall().getBytes());
                }
            }
            this.output.println();
            this.output.println("@!Topologia");
            this.scenarioCRC.update("@!Topologia".getBytes());
            if (createCRC) {
                String auxCRCHash = Long.toString(this.scenarioCRC.getValue());
                this.output.println();
                this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
                this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.CodigoCRCParaLaIntegridadDelFichero"));
                this.output.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
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
}
