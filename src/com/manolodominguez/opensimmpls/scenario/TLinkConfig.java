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

/**
 * This class implements an object that will contain the needed values to
 * configure a link.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TLinkConfig {

    /**
     * This methods is the constructor of the class. It creates a new instance
     * of TLinkConfig.
     */
    public TLinkConfig() {
        // FIX: Do not use harcoded values. Use class constants in all cases.
        this.name = "";
        this.headEndNodeName = "";
        this.tailEndNodeName = "";
        this.linkDelay = 1;
        this.wellConfigured = false;
        this.showName = false;
        this.linkType = TLink.INTERNAL_LINK;
        this.headEndNodePortID = -1;
        this.tailEndNodePortID = -1;
    }

    /**
     * This method sets the link delay (in nanoseconds).
     *
     * @param linkDelay the link delay (in nanoseconds).
     * @since 2.0
     */
    public void setLinkDelay(int linkDelay) {
        this.linkDelay = linkDelay;
    }

    /**
     * This method gets the link delay (in nanoseconds).
     *
     * @return the link delay (in nanoseconds).
     * @since 2.0
     */
    public int getLinkDelay() {
        return this.linkDelay;
    }

    /**
     * This method sets the link config as a valid or not valid.
     *
     * @since 2.0
     * @param wellConfigured TRUE, if the link config is valid. Otherwise,
     * FALSE.
     */
    public void setWellConfigured(boolean wellConfigured) {
        this.wellConfigured = wellConfigured;
    }

    /**
     * This method gets whether the link config is valid or not valid.
     *
     * @since 2.0
     * @return TRUE, if the link config is valid. Otherwise, FALSE.
     */
    public boolean isWellConfigured() {
        return this.wellConfigured;
    }

    /**
     * This method establish the type of the link (enternal or internal).
     *
     * @since 2.0
     * @param linkType TLink.EXTERNAL_LINK, if it is a external link.
     * TLink.INTERNAL_LINK if the link is internal.
     */
    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    /**
     * This method gets the type of the link (enternal or internal).
     *
     * @since 2.0
     * @return TLink.EXTERNAL_LINK, if it is a external link.
     * TLink.INTERNAL_LINK if the link is internal.
     */
    public int getLinkType() {
        return this.linkType;
    }

    /**
     * This method sets the name of the link.
     *
     * @since 2.0
     * @param name the name of the link.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method gets the name of the link.
     *
     * @since 2.0
     * @return the name of the link.
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method sets whether the name of the link has to be displayed in the
     * simulator or not.
     *
     * @since 2.0
     * @param showName TRUE, if the name has to be displayed. Otherwise, FALSE.
     */
    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    /**
     * This method sets the name of the node connected to the head end of the
     * link.
     *
     * @since 2.0
     * @param headEndNodeName the name of the node connected to the head end of
     * the link.
     */
    public void setHeadEndNodeName(String headEndNodeName) {
        this.headEndNodeName = headEndNodeName;
    }

    /**
     * This method gets the name of the node connected to the head end of the
     * link.
     *
     * @since 2.0
     * @return the name of the node connected to the head end of the link.
     */
    public String getHeadEndNodeName() {
        return this.headEndNodeName;
    }

    /**
     * This method sets the name of the node connected to the tail end of the
     * link.
     *
     * @since 2.0
     * @param tailEndNodeName the name of the node connected to the tail end of
     * the link.
     */
    public void setTailEndNodeName(String tailEndNodeName) {
        this.tailEndNodeName = tailEndNodeName;
    }

    /**
     * This method gets the name of the node connected to the tail end of the
     * link.
     *
     * @since 2.0
     * @return the name of the node connected to the tail end of the link.
     */
    public String getTailEndNodeName() {
        return this.tailEndNodeName;
    }

    /**
     * This method gets whether the name of the link has to be displayed in the
     * simulator or not.
     *
     * @since 2.0
     * @return TRUE, if the name has to be displayed. Otherwise, FALSE.
     */
    public boolean nameMustBeDisplayed() {
        return this.showName;
    }

    /**
     * This method sets the port ID of the node to wich the head end of the link
     * is connected.
     *
     * @since 2.0
     * @param headEndNodePortID the port ID of the node to wich the head end of
     * the link is connected.
     */
    public void setHeadEndNodePortID(int headEndNodePortID) {
        this.headEndNodePortID = headEndNodePortID;
    }

    /**
     * This method gets the port ID of the node to wich the head end of the link
     * is connected.
     *
     * @since 2.0
     * @return the port ID of the node to wich the head end of the link is
     * connected.
     */
    public int getHeadEndNodePortID() {
        return this.headEndNodePortID;
    }

    /**
     * This method sets the port ID of the node to wich the tail end of the link
     * is connected.
     *
     * @since 2.0
     * @param tailEndNodePortID the port ID of the node to wich the tail end of
     * the link is connected.
     */
    public void setTailEndNodePortID(int tailEndNodePortID) {
        this.tailEndNodePortID = tailEndNodePortID;
    }

    /**
     * This method gets the port ID of the node to wich the tail end of the link
     * is connected.
     *
     * @since 2.0
     * @return the port ID of the node to wich the tail end of the link is
     * connected.
     */
    public int getTailEndNodePortID() {
        return this.tailEndNodePortID;
    }

    /**
     * This method checks whether the link is configured correctly or not taking
     * into account the information of this TLinkConfig.
     *
     * @param topology Topology the link belongs to.
     * @param reconfiguration TRUE, if the link is being re-configured.
     * Otherwise, FALSE.
     * @return TLinkConfig.OK if the configuration is correct. Otherwise, an
     * error code is returned. See public constants of error codes in this
     * class.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public int validateConfig(TTopology topology, boolean reconfiguration) {
        // FIX: It seems like a call to setWellConfigured(false) is missing 
        // here. Check
        if (this.name.equals("")) {
            return TLinkConfig.UNNAMED;
        }
        boolean onlyBlankSpaces = true;
        for (int i = 0; i < this.name.length(); i++) {
            if (this.name.charAt(i) != ' ') {
                onlyBlankSpaces = false;
            }
        }
        if (onlyBlankSpaces) {
            return TLinkConfig.ONLY_BLANK_SPACES;
        }
        if (!reconfiguration) {
            TLink linkAux = topology.getFirstLinkNamed(this.name);
            if (linkAux != null) {
                return TLinkConfig.NAME_ALREADY_EXISTS;
            }
        } else {
            TLink linkAux2 = topology.getFirstLinkNamed(this.name);
            if (linkAux2 != null) {
                if (topology.isThereMoreThanALinkNamed(this.name)) {
                    return TLinkConfig.NAME_ALREADY_EXISTS;
                } else if (linkAux2.getHeadEndNode().getName().equals(this.getHeadEndNodeName())) {
                    if (!linkAux2.getTailEndNode().getName().equals(this.getTailEndNodeName())) {
                        return TLinkConfig.NAME_ALREADY_EXISTS;
                    }
                } else {
                    return TLinkConfig.NAME_ALREADY_EXISTS;
                }
            }
        }
        if ((this.headEndNodeName.equals("")) || (this.headEndNodeName == null)) {
            return TLinkConfig.MISSING_HEAD_END_NODE_NAME;
        }
        if ((this.tailEndNodeName.equals("")) || (this.tailEndNodeName == null)) {
            return TLinkConfig.MISSING_TAIL_END_NODE_NAME;
        }
        // FIX: use class constants instead of harcoded values
        if (this.headEndNodePortID == -1) {
            return TLinkConfig.MISSING_HEAD_END_NODE_PORT_ID;
        }
        // FIX: use class constants instead of harcoded values
        if (this.tailEndNodePortID == -1) {
            return TLinkConfig.MISSING_TAIL_END_NODE_PORT_ID;
        }
        // FIX: It seems like a call to setWellConfigured(true) is missing here.
        // Check
        return TLinkConfig.OK;
    }

    /**
     * This method generates an human-readable error message from a given error
     * code specified as an argument.
     *
     * @param errorCode the error code to witch the text message has to be
     * generated. One of the public constants defined in this class.
     * @return an String explaining the error.
     * @since 2.0
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     */
    public String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case UNNAMED:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.FALTA_NOMBRE"));
            case ONLY_BLANK_SPACES:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.NoSoloEspacios"));
            case NAME_ALREADY_EXISTS:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.NombreYaUsado"));
            case MISSING_HEAD_END_NODE_PORT_ID:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionrPuertoIzquierdo"));
            case MISSING_TAIL_END_NODE_PORT_ID:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionarPuertoDerecho"));
            case MISSING_HEAD_END_NODE_NAME:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionarExtremoIzquierdo"));
            case MISSING_TAIL_END_NODE_NAME:
                return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionarExtremoDerecho"));
        }
        return ("");
    }

    /**
     * This method discovers automatically the type of the link depending on the
     * type of nodes it connects.
     *
     * @since 2.0
     * @param topology Topology the link belongs to.
     */
    public void discoverLinkType(TTopology topology) {
        TNode headEndNode, tailEndNode;
        headEndNode = topology.getFirstNodeNamed(this.headEndNodeName);
        tailEndNode = topology.getFirstNodeNamed(this.tailEndNodeName);
        if ((headEndNode != null) && (tailEndNode != null)) {
            int headEndNodeType = headEndNode.getNodeType();
            int tailEndNodeType = tailEndNode.getNodeType();
            if ((headEndNodeType == TNode.TRAFFIC_GENERATOR) || (headEndNodeType == TNode.TRAFFIC_SINK)
                    || (tailEndNodeType == TNode.TRAFFIC_GENERATOR) || (tailEndNodeType == TNode.TRAFFIC_SINK)) {
                this.setLinkType(TLink.EXTERNAL_LINK);
            } else {
                this.setLinkType(TLink.INTERNAL_LINK);
            }
        }
    }

    public static final int OK = 0;
    public static final int UNNAMED = 1;
    public static final int ONLY_BLANK_SPACES = 2;
    public static final int NAME_ALREADY_EXISTS = 3;
    public static final int MISSING_HEAD_END_NODE_PORT_ID = 4;
    public static final int MISSING_TAIL_END_NODE_PORT_ID = 5;
    public static final int MISSING_HEAD_END_NODE_NAME = 6;
    public static final int MISSING_TAIL_END_NODE_NAME = 7;

    private int linkType;
    private String name;
    private String headEndNodeName;
    private String tailEndNodeName;
    private int headEndNodePortID;
    private int tailEndNodePortID;
    public int linkDelay;
    private boolean showName;
    private boolean wellConfigured;
}
