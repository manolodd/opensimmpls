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
package com.manolodominguez.opensimmpls.scenario;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.io.File;
import java.util.ResourceBundle;

/**
 * This class implements a complete simulation scenario, with all its
 * components.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TScene {

    /**
     * This is the constructor of the class. It creates a new instance of
     * TScenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TScene() {
        // FIX: Use class constants instead of harcoded values.
        this.topology = new TTopology(this);
        this.simulation = new TSimulation(this);
        this.scenarioFile = null;
        this.alreadySaved = false;
        this.modified = false;
        this.title = "";
        this.author = "";
        this.description = "";
        this.translations = ResourceBundle.getBundle(AvailableBundles.SCENARIO.getPath());
    }

    /**
     * This method restart all attributes of the class a when it was
     * instantiated.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.topology.reset();
        this.simulation.reset();
    }

    /**
     * This method sets the title of the scenario.
     *
     * @param title the title of the scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method gets the title of the scenario.
     *
     * @return the title of the scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * This method sets the author of this scenario.
     *
     * @param author the author of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * This method gets the author of this scenario.
     *
     * @return the author of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * This mthod sets the description of this scenario.
     *
     * @param description the description of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This mthod gets the description of this scenario.
     *
     * @return the description of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * This method serializes the title of this scenario.
     *
     * @return the serialized version of the title of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public String marshallTitle() {
        String serializedTitle = "#Titulo#";
        if (this.getTitle().replace('#', ' ').equals("")) {
            serializedTitle += this.translations.getString("TEscenario.SinDefinir");
        } else {
            serializedTitle += this.getTitle().replace('#', ' ');
        }
        serializedTitle += "#";
        return serializedTitle;
    }

    /**
     * This method serializes the author of this scenario.
     *
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the serialized version of the author of this scenario.
     */
    public String marshallAuthor() {
        String serializedAuthor = "#Autor#";
        if (this.getAuthor().replace('#', ' ').equals("")) {
            serializedAuthor += this.translations.getString("TEscenario.SinDefinir");
        } else {
            serializedAuthor += this.getAuthor().replace('#', ' ');
        }
        serializedAuthor += "#";
        return serializedAuthor;
    }

    /**
     * This method serializes the description of this scenario.
     *
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the serialized version of the description of this scenario.
     */
    public String marshallDescription() {
        String serializedDescription = "#Descripcion#";
        if (this.getDescription().replace('#', ' ').equals("")) {
            serializedDescription += this.translations.getString("TEscenario.SinDefinir");
        } else {
            serializedDescription += this.getDescription().replace('#', ' ');
        }
        serializedDescription += "#";
        return serializedDescription;
    }

    /**
     * This method deserializes the title of this scenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param titulo the serialized version of the title of this scenario.
     * @return TRUE, if the title can be deserialized correctly. Otherwise,
     * FALSE.
     */
    public boolean unmarshallTitle(String titulo) {
        String[] titleFields = titulo.split("#");
        if (titleFields.length != 3) {
            return false;
        }
        if (titleFields[2] != null) {
            this.setTitle(titleFields[2]);
        }
        return true;
    }

    /**
     * This method deserializes the autor of this scenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param autor the serialized version of the autor of this scenario.
     * @return TRUE, if the autor can be deserialized correctly. Otherwise,
     * FALSE.
     */
    public boolean unmarshallAuthor(String autor) {
        String[] authorFields = autor.split("#");
        if (authorFields.length != 3) {
            return false;
        }
        if (authorFields[2] != null) {
            this.setAuthor(authorFields[2]);
        }
        return true;
    }

    /**
     * This method deserializes the descripcion of this scenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param descripcion the serialized version of the descripcion of this
     * scenario.
     * @return TRUE, if the descripcion can be deserialized correctly.
     * Otherwise, FALSE.
     */
    public boolean unmarshallDescription(String descripcion) {
        String[] descriptionFields = descripcion.split("#");
        if (descriptionFields.length != 3) {
            return false;
        }
        if (descriptionFields[2] != null) {
            this.setDescription(descriptionFields[2]);
        }
        return true;
    }

    /**
     * This method sets the topology of this scenario.
     *
     * @param topology the topology of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setTopology(TTopology topology) {
        this.topology = topology;
        this.modified = true;
    }

    /**
     * This method gets the topology of this scenario.
     *
     * @return the topology of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TTopology getTopology() {
        return topology;
    }

    /**
     * This method sets the simulation of this scenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param simulation the simulation of this scenario.
     */
    public void setSimulation(TSimulation simulation) {
        this.simulation = simulation;
        this.modified = true;
    }

    /**
     * This method gets the simulation of this scenario.
     *
     * @return the simulation of this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TSimulation getSimulation() {
        return this.simulation;
    }

    /**
     * This method start the simulation process of this scenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void simulate() {
        if (!this.topology.getTimer().isRunning()) {
            topology.getTimer().reset();
            topology.getTimer().start();
        }
    }

    /**
     * This method sets the file disk associated to this scenario.
     *
     * @param scenarioFile the file disk associated to this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setScenarioFile(File scenarioFile) {
        this.scenarioFile = scenarioFile;
    }

    /**
     * This method gets the file disk associated to this scenario.
     *
     * @return the file disk associated to this scenario.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public File getScenarioFile() {
        return this.scenarioFile;
    }

    /**
     * This method sets whether the in-memory scenario is saved to disk or not.
     *
     * @param saved TRUE, if the scenario has been saved to disk. Otherwise,
     * FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setAlreadySaved(boolean saved) {
        this.alreadySaved = saved;
    }

    /**
     * This method ckecks whether the in-memory scenario has been saved to disk
     * or not.
     *
     * @return TRUE, if the scenario has been saved to disk. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isAlreadySaved() {
        return this.alreadySaved;
    }

    /**
     * This method sets whether the in-memory scenario has changed with respect
     * to its version saved on disk or not.
     *
     * @param modified TRUE, if the in-memory scenario has changed with respect
     * to its version saved on disk. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * This method checks whether the in-memory scenario has changed with
     * respect to its version saved on disk or not.
     *
     * @return TRUE, if the in-memory scenario has changed with respect to its
     * version saved on disk. Otherwise, FALSE.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public boolean isModified() {
        return this.modified;
    }

    private String title;
    private String author;
    private String description;
    private TTopology topology;
    private TSimulation simulation;
    private File scenarioFile;
    private boolean alreadySaved;
    private boolean modified;
    private ResourceBundle translations;
}
