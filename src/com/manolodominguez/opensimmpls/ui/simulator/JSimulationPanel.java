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
package com.manolodominguez.opensimmpls.ui.simulator;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.scenario.TInternalLink;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventNodeCongested;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketDiscarded;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketGenerated;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketOnFly;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketReceived;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketRouted;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketSent;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEventPacketSwitched;
import com.manolodominguez.opensimmpls.scenario.simulationevents.TSimulationEvent;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import com.manolodominguez.opensimmpls.utils.TLock;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javax.swing.JPanel;

/**
 * This class implements a panel that shows the simulation of a given scenario.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JSimulationPanel extends JPanel {

    /**
     * This is the constructor of the class and creates a new instance of
     * JSimulationPanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public JSimulationPanel() {
        initComponents();
    }

    /**
     * This is the constructor of the class and creates a new instance of
     * JSimulationPanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param imageBroker The image broker that will provide the needed images
     * to paint the topology.
     * @since 2.0
     */
    public JSimulationPanel(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
        initComponents();
    }

    /**
     * This method initialize some attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.SIMULATION_PANEL.getPath());
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.bufferedImage = null;
        this.bufferedG2D = null;
        this.topology = null;
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxX = 10;
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxY = 10;
        this.eventsBuffer = new TreeSet();
        this.simulationBuffer = new TreeSet();
        // FIX: Do not use harcoded values. Use class constants instead.
        this.currentTick = 0;
        // FIX: Do not use harcoded values. Use class constants instead.
        this.simulationSpeedInMsPerTick = 0;
        this.showLegend = false;
        this.eventsBuffersLock = new TLock();
    }

    /**
     * This method reset all attributes as in the moment of the instantiation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.eventsBuffersLock.lock();
        Iterator eventsIterator = null;
        eventsIterator = this.eventsBuffer.iterator();
        while (eventsIterator.hasNext()) {
            eventsIterator.next();
            eventsIterator.remove();
        }
        eventsIterator = this.simulationBuffer.iterator();
        while (eventsIterator.hasNext()) {
            eventsIterator.next();
            eventsIterator.remove();
        }
        this.showLegend = false;
        this.eventsBuffersLock.unLock();
        // FIX: Do not use harcoded values. Use class constants instead.
        this.currentTick = 0;
    }

    /**
     * This method sets the topology that has to be painted in the panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param topology the topology that has to be painted in the panel.
     * @since 2.0
     */
    public void setTopology(TTopology topology) {
        this.topology = topology;
    }

    /**
     * This method sets the image broker that will provide the needed images to
     * paint the topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param imageBroker The image broker that will provide the needed images
     * to paint the topology and its simulation.
     * @since 2.0
     */
    public void setImageBroker(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
    }

    /**
     * This method gets the thickness that has to be used when painting the link
     * whose delay is specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param linkDelay the thickness that has to be used when painting the link
     * whose delay is specified as an argument.
     * @since 2.0
     */
    private double getLinkThickness(double linkDelay) {
        // FIX: Do not use harcoded values. Use class constants instead.
        return (16 / Math.log(linkDelay + 100));
    }

    /**
     * This method prepares the place where the simulation is going to be
     * painted setting attributes as the size, antialias, and so on..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void prepareImage(Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.WHITE);
        // FIX: Do not use harcoded values. Use class constants instead.
        graphics2D.fillRect(0, 0, this.screenSize.width, this.screenSize.height);
    }

    /**
     * This method paints the MPLS domain corresponding to the topology being
     * painted.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintDomain(Graphics2D graphics2D) {
        Iterator nodesIterator = this.topology.getNodesIterator();
        TNode node;
        Polygon polygon = new Polygon();
        int vertexes = 0;
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if ((node.getNodeType() == TNode.LER)
                    || (node.getNodeType() == TNode.ACTIVE_LER)) {
                // FIX: Do not use harcoded values. Use class constants instead.
                polygon.addPoint(node.getScreenPosition().x + 24, node.getScreenPosition().y + 24);
                vertexes++;
            }
        }
        // FIX: Do not use harcoded values. Use class constants instead.
        if (vertexes > 2) {
            graphics2D.setColor(DOMAIN_BACKGROUND_COLOR);
            graphics2D.fillPolygon(polygon);
            graphics2D.setColor(DOMAIN_BORDER_COLOR);
            graphics2D.drawPolygon(polygon);
            // FIX: Do not use harcoded values. Use class constants instead.
        } else if (vertexes == 2) {
            int x1 = Math.min(polygon.xpoints[0], polygon.xpoints[1]);
            int y1 = Math.min(polygon.ypoints[0], polygon.ypoints[1]);
            int x2 = Math.max(polygon.xpoints[0], polygon.xpoints[1]);
            int y2 = Math.max(polygon.ypoints[0], polygon.ypoints[1]);
            int width = x2 - x1;
            int height = y2 - y1;
            graphics2D.setColor(DOMAIN_BACKGROUND_COLOR);
            graphics2D.fillRect(x1, y1, width, height);
            graphics2D.setColor(DOMAIN_BORDER_COLOR);
            graphics2D.drawRect(x1, y1, width, height);
            // FIX: Do not use harcoded values. Use class constants instead.
        } else if (vertexes == 1) {
            graphics2D.setColor(DOMAIN_BACKGROUND_COLOR);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.fillOval(polygon.xpoints[0] - 50, polygon.ypoints[0] - 40, 100, 80);
            graphics2D.setColor(DOMAIN_BORDER_COLOR);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawOval(polygon.xpoints[0] - 50, polygon.ypoints[0] - 40, 100, 80);
        }
    }

    /**
     * This method paints links corresponding to the topology being painted.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintLinks(Graphics2D graphics2D) {
        Iterator linksIterator = this.topology.getLinksIterator();
        while (linksIterator.hasNext()) {
            TLink link = (TLink) linksIterator.next();
            Point headEnd = link.getHeadEndNode().getScreenPosition();
            Point tailEnd = link.getTailEndNode().getScreenPosition();
            int linkDelay = link.getDelay();
            graphics2D.setStroke(new BasicStroke((float) getLinkThickness(linkDelay)));
            if (link.getLinkType() == TLink.EXTERNAL_LINK) {
                graphics2D.setColor(EXTERNAL_LINK_COLOR);
            } else {
                graphics2D.setColor(INTERNAL_LINK_COLOR);
            }
            if (link.isBroken()) {
                float dash1[] = {5.0f};
                // FIX: Do not use harcoded values. Use class constants instead.
                BasicStroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
                graphics2D.setColor(BROKEN_LINK_COLOR);
                graphics2D.setStroke(dashed);
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawLine(headEnd.x + 24, headEnd.y + 24, tailEnd.x + 24, tailEnd.y + 24);
            graphics2D.setStroke(new BasicStroke((float) 1));

            if (!link.isBroken()) {
                if (link.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink internalLink = (TInternalLink) link;
                    if (internalLink.isBeingUsedByAnyLSP()) {
                        float dash1[] = {5.0f};
                        // FIX: Do not use harcoded values. Use class constants instead.
                        BasicStroke dashed = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
                        graphics2D.setColor(this.LSP_COLOR);
                        graphics2D.setStroke(dashed);
                        if (headEnd.x == tailEnd.x) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 20, headEnd.y + 24, tailEnd.x + 20, tailEnd.y + 24);
                        } else if (headEnd.y == tailEnd.y) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 24, headEnd.y + 20, tailEnd.x + 24, tailEnd.y + 20);
                        } else if (((headEnd.x < tailEnd.x) && (headEnd.y > tailEnd.y)) || ((headEnd.x > tailEnd.x) && (headEnd.y < tailEnd.y))) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 20, headEnd.y + 20, tailEnd.x + 20, tailEnd.y + 20);
                        } else if (((headEnd.x < tailEnd.x) && (headEnd.y < tailEnd.y)) || ((headEnd.x > tailEnd.x) && (headEnd.y > tailEnd.y))) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 28, headEnd.y + 20, tailEnd.x + 28, tailEnd.y + 20);
                        }
                        graphics2D.setStroke(new BasicStroke(1));
                    }
                }
            }
            if (!link.isBroken()) {
                if (link.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink internalLink = (TInternalLink) link;
                    if (internalLink.isBeingUsedByAnyBackupLSP()) {
                        // FIX: Do not use harcoded values. Use class constants instead.
                        float dash1[] = {10.0f, 5.0f, 0.2f, 5.0f};
                        // FIX: Do not use harcoded values. Use class constants instead.
                        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
                        graphics2D.setColor(LSP_COLOR);
                        graphics2D.setStroke(dashed);
                        if (headEnd.x == tailEnd.x) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 28, headEnd.y + 24, tailEnd.x + 28, tailEnd.y + 24);
                        } else if (headEnd.y == tailEnd.y) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 24, headEnd.y + 28, tailEnd.x + 24, tailEnd.y + 28);
                        } else if (((headEnd.x < tailEnd.x) && (headEnd.y > tailEnd.y)) || ((headEnd.x > tailEnd.x) && (headEnd.y < tailEnd.y))) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 28, headEnd.y + 28, tailEnd.x + 28, tailEnd.y + 28);
                        } else if (((headEnd.x < tailEnd.x) && (headEnd.y < tailEnd.y)) || ((headEnd.x > tailEnd.x) && (headEnd.y > tailEnd.y))) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawLine(headEnd.x + 20, headEnd.y + 28, tailEnd.x + 20, tailEnd.y + 28);
                        }
                        graphics2D.setStroke(new BasicStroke(1));
                    }
                }
            }
            if (link.getShowName()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(link.getName().toCharArray(), 0, link.getName().length());
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX1 = link.getHeadEndNode().getScreenPosition().x + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posY1 = link.getHeadEndNode().getScreenPosition().y + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX2 = link.getTailEndNode().getScreenPosition().x + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posY2 = link.getTailEndNode().getScreenPosition().y + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX = Math.min(posX1, posX2) + ((Math.max(posX1, posX2) - Math.min(posX1, posX2)) / 2) - (anchoTexto / 2);
                // FIX: Do not use harcoded values. Use class constants instead.
                int posY = Math.min(posY1, posY2) + ((Math.max(posY1, posY2) - Math.min(posY1, posY2)) / 2) + 5;
                graphics2D.setColor(LINK_NAME_COLOR);
                // FIX: Do not use harcoded values. Use class constants instead.
                graphics2D.fillRoundRect(posX - 3, posY - 13, anchoTexto + 5, 17, 10, 10);
                graphics2D.setColor(Color.GRAY);
                graphics2D.drawString(link.getName(), posX, posY);
                // FIX: Do not use harcoded values. Use class constants instead.
                graphics2D.drawRoundRect(posX - 3, posY - 13, anchoTexto + 5, 17, 10, 10);
            }
        }
    }

    /**
     * This method paints nodes corresponding to the topology being painted.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintNodes(Graphics2D graphics2D) {
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxX = 10;
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxY = 10;
        Iterator nodesIterator = this.topology.getNodesIterator();
        while (nodesIterator.hasNext()) {
            TNode node = (TNode) nodesIterator.next();
            Point nodePosition = node.getScreenPosition();

            // FIX: Do not use harcoded values. Use class constants instead.
            if ((nodePosition.x + 48) > this.maxX) {
                // FIX: Do not use harcoded values. Use class constants instead.
                this.maxX = nodePosition.x + 48;
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            if ((nodePosition.y + 48) > this.maxY) {
                // FIX: Do not use harcoded values. Use class constants instead.
                this.maxY = nodePosition.y + 48;
            }
            this.setPreferredSize(new Dimension(this.maxX, this.maxY));
            this.revalidate();

            int nodeType = node.getNodeType();
            switch (nodeType) {
                case TNode.TRAFFIC_GENERATOR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_GENERATOR), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_GENERATOR_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.TRAFFIC_SINK: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_SINK), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_SINK_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.LER: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LER), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LER_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.ACTIVE_LER: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LER), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LER_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.LSR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LSR), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LSR_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.ACTIVE_LSR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LSR), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LSR_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
            }
            if (node.getShowName()) {
                FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                int textWidth = fontMetrics.charsWidth(node.getName().toCharArray(), 0, node.getName().length());
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX = (node.getScreenPosition().x + 24) - ((textWidth / 2));
                // FIX: Do not use harcoded values. Use class constants instead.
                int posY = node.getScreenPosition().y + 60;
                graphics2D.setColor(Color.WHITE);
                // FIX: Do not use harcoded values. Use class constants instead.
                graphics2D.fillRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
                graphics2D.setColor(Color.GRAY);
                graphics2D.drawString(node.getName(), posX, posY);
                // FIX: Do not use harcoded values. Use class constants instead.
                graphics2D.drawRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
            }
        }
    }

    /**
     * This method set the number of Ms between subsequents repaints of the
     * simulation. In fact this allow the user to set the simulation speed that,
     * by default is too fast as to see something clearly in the simulation
     * panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param simulationSpeedInMsPerTick The number of Ms that the simulation
     * will wait until next tick is painted in the panel.
     * @since 2.0
     */
    public void setSimulationSpeedInMsPerTick(int simulationSpeedInMsPerTick) {
        this.simulationSpeedInMsPerTick = simulationSpeedInMsPerTick;
    }

    /**
     * This method queues a new simulation event to be painted in the simulation
     * panel when needed.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param simulationEvent a new simulation event to be painted in the
     * simulation panel when needed.
     * @since 2.0
     */
    public void addEvent(TSimulationEvent simulationEvent) {
        this.eventsBuffersLock.lock();
        if (simulationEvent.getInstant() <= this.currentTick) {
            this.eventsBuffer.add(simulationEvent);
            this.eventsBuffersLock.unLock();
        } else {
            this.currentTick = simulationEvent.getInstant();
            Iterator simulationEventsBufferIterator = this.simulationBuffer.iterator();
            TSimulationEvent evento = null;
            while (simulationEventsBufferIterator.hasNext()) {
                simulationEventsBufferIterator.next();
                simulationEventsBufferIterator.remove();
            }
            simulationEventsBufferIterator = this.eventsBuffer.iterator();
            evento = null;
            while (simulationEventsBufferIterator.hasNext()) {
                evento = (TSimulationEvent) simulationEventsBufferIterator.next();
                this.simulationBuffer.add(evento);
                simulationEventsBufferIterator.remove();
            }
            this.eventsBuffersLock.unLock();
            repaint();
            this.eventsBuffer.add(simulationEvent);
            try {
                // FIX: Do not use static access to sleep() method
                Thread.currentThread().sleep(this.simulationSpeedInMsPerTick);
            } catch (Exception e) {
                // FIX: This is ugly.
                e.printStackTrace();
            }
        }
    }

    /**
     * This method paints events related to the packets circulating through the
     * topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintPacketsEvents(Graphics2D graphics2D) {
        this.eventsBuffersLock.lock();
        try {
            Iterator simulationEventsIterator = this.simulationBuffer.iterator();
            TSimulationEvent event = null;
            while (simulationEventsIterator.hasNext()) {
                event = (TSimulationEvent) simulationEventsIterator.next();
                if (event != null) {
                    if (event.getSubtype() == TSimulationEvent.PACKET_ON_FLY) {
                        TSimulationEventPacketOnFly simulationEventPacketOnFly = (TSimulationEventPacketOnFly) event;
                        TLink link = (TLink) simulationEventPacketOnFly.getSource();
                        Point packetPosition = link.getScreenPacketPosition(simulationEventPacketOnFly.getTransitPercentage());
                        if (simulationEventPacketOnFly.getPacketType() == TAbstractPDU.GPSRP) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_GOS), packetPosition.x - 14, packetPosition.y - 14, null);
                        } else if (simulationEventPacketOnFly.getPacketType() == TAbstractPDU.TLDP) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_LDP), packetPosition.x - 8, packetPosition.y - 8, null);
                        } else if (simulationEventPacketOnFly.getPacketType() == TAbstractPDU.IPV4) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_IPV4), packetPosition.x - 8, packetPosition.y - 8, null);
                        } else if (simulationEventPacketOnFly.getPacketType() == TAbstractPDU.IPV4_GOS) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_IPV4_GOS), packetPosition.x - 8, packetPosition.y - 8, null);
                        } else if (simulationEventPacketOnFly.getPacketType() == TAbstractPDU.MPLS) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_MPLS), packetPosition.x - 8, packetPosition.y - 8, null);
                        } else if (simulationEventPacketOnFly.getPacketType() == TAbstractPDU.MPLS_GOS) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_MPLS_GOS), packetPosition.x - 8, packetPosition.y - 8, null);
                        }
                    } else if (event.getSubtype() == TSimulationEvent.PACKET_DISCARDED) {
                        TSimulationEventPacketDiscarded simulationEventPacketDiscarded = (TSimulationEventPacketDiscarded) event;
                        TNode node = (TNode) simulationEventPacketDiscarded.getSource();
                        Point nodePosition = node.getScreenPosition();
                        if (simulationEventPacketDiscarded.getPacketType() == TAbstractPDU.GPSRP) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_GOS_DISCARDED), nodePosition.x, nodePosition.y + 24, null);
                        } else if (simulationEventPacketDiscarded.getPacketType() == TAbstractPDU.TLDP) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_LDP_DISCARDED), nodePosition.x, nodePosition.y + 24, null);
                        } else if (simulationEventPacketDiscarded.getPacketType() == TAbstractPDU.IPV4) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_IPV4_DISCARDED), nodePosition.x, nodePosition.y + 24, null);
                        } else if (simulationEventPacketDiscarded.getPacketType() == TAbstractPDU.IPV4_GOS) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_IPV4_GOS_DISCARDED), nodePosition.x, nodePosition.y + 24, null);
                        } else if (simulationEventPacketDiscarded.getPacketType() == TAbstractPDU.MPLS) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_MPLS_DISCARDED), nodePosition.x, nodePosition.y + 24, null);
                        } else if (simulationEventPacketDiscarded.getPacketType() == TAbstractPDU.MPLS_GOS) {
                            // FIX: Do not use harcoded values. Use class constants instead.
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_MPLS_GOS_DISCARDED), nodePosition.x, nodePosition.y + 24, null);
                        }
                    } else if (event.getSubtype() == TSimulationEvent.LSP_ESTABLISHED) {
                        // I've to think in something to show when this happens :-)
                    } else if (event.getSubtype() == TSimulationEvent.PACKET_GENERATED) {
                        TSimulationEventPacketGenerated simulationEventPacketGenerated = (TSimulationEventPacketGenerated) event;
                        TNode node = (TNode) simulationEventPacketGenerated.getSource();
                        Point nodePosition = node.getScreenPosition();
                        // FIX: Do not use harcoded values. Use class constants instead.
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_GENERATED), nodePosition.x + 8, nodePosition.y - 16, null);
                    } else if (event.getSubtype() == TSimulationEvent.PACKET_SENT) {
                        TSimulationEventPacketSent simulationEventPacketSent = (TSimulationEventPacketSent) event;
                        TNode node = (TNode) simulationEventPacketSent.getSource();
                        Point nodePosition = node.getScreenPosition();
                        // FIX: Do not use harcoded values. Use class constants instead.
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_SENT), nodePosition.x + 24, nodePosition.y - 16, null);
                    } else if (event.getSubtype() == TSimulationEvent.PACKET_RECEIVED) {
                        TSimulationEventPacketReceived simulationEventPacketReceived = (TSimulationEventPacketReceived) event;
                        TNode node = (TNode) simulationEventPacketReceived.getSource();
                        Point nodePosition = node.getScreenPosition();
                        // FIX: Do not use harcoded values. Use class constants instead.
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_RECEIVED), nodePosition.x - 8, nodePosition.y - 16, null);
                    } else if (event.getSubtype() == TSimulationEvent.PACKET_SWITCHED) {
                        TSimulationEventPacketSwitched simulationEventPacketSwitched = (TSimulationEventPacketSwitched) event;
                        TNode node = (TNode) simulationEventPacketSwitched.getSource();
                        Point nodePosition = node.getScreenPosition();
                        // FIX: Do not use harcoded values. Use class constants instead.
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_SWITCHED), nodePosition.x + 40, nodePosition.y - 16, null);
                    } else if (event.getSubtype() == TSimulationEvent.PACKET_ROUTED) {
                        TSimulationEventPacketRouted simulationEventPacketRouted = (TSimulationEventPacketRouted) event;
                        TNode node = (TNode) simulationEventPacketRouted.getSource();
                        Point nodePosition = node.getScreenPosition();
                        // FIX: Do not use harcoded values. Use class constants instead.
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_SWITCHED), nodePosition.x + 40, nodePosition.y - 16, null);
                    }
                }
            }
        } catch (Exception e) {
            // FIX: This is ugly.
            e.printStackTrace();
        }
        this.eventsBuffersLock.unLock();
    }

    /**
     * This method paints events related to the nodes of the topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintNodesEvents(Graphics2D graphics2D) {
        this.eventsBuffersLock.lock();
        try {
            TSimulationEvent event = null;
            Iterator simulationEventsIterator = this.simulationBuffer.iterator();
            while (simulationEventsIterator.hasNext()) {
                event = (TSimulationEvent) simulationEventsIterator.next();
                if (event != null) {
                    if (event.getSubtype() == TSimulationEvent.NODE_CONGESTED) {
                        TSimulationEventNodeCongested simulationEventNodeCongested = (TSimulationEventNodeCongested) event;
                        TNode node = (TNode) simulationEventNodeCongested.getSource();
                        Point nodePosition = node.getScreenPosition();
                        int nodeType = node.getNodeType();
                        long congestionLevel = simulationEventNodeCongested.getCongestionLevel();
                        // FIX: Do not use harcoded values. Use class constants instead.
                        if ((congestionLevel >= 50) && (congestionLevel < 75)) {
                            if (nodeType == TNode.TRAFFIC_GENERATOR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_GENERATOR_CONGESTED1), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.TRAFFIC_SINK) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_SINK_CONGESTED1), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.LER) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LER_CONGESTED1), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.ACTIVE_LER) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LER_CONGESTED1), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.LSR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LSR_CONGESTED1), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.ACTIVE_LSR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LSR_CONGESTED1), nodePosition.x, nodePosition.y, null);
                            }
                            // FIX: Do not use harcoded values. Use class constants instead.
                        } else if ((congestionLevel >= 75) && (congestionLevel < 95)) {
                            if (nodeType == TNode.TRAFFIC_GENERATOR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_GENERATOR_CONGESTED2), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.TRAFFIC_SINK) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_SINK_CONGESTED2), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.LER) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LER_CONGESTED2), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.ACTIVE_LER) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LER_CONGESTED2), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.LSR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LSR_CONGESTED2), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.ACTIVE_LSR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LSR_CONGESTED2), nodePosition.x, nodePosition.y, null);
                            }
                            // FIX: Do not use harcoded values. Use class constants instead.
                        } else if (congestionLevel >= 95) {
                            if (nodeType == TNode.TRAFFIC_GENERATOR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_GENERATOR_CONGESTED3), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.TRAFFIC_SINK) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.TRAFFIC_SINK_CONGESTED3), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.LER) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LER_CONGESTED3), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.ACTIVE_LER) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LER_CONGESTED3), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.LSR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LSR_CONGESTED3), nodePosition.x, nodePosition.y, null);
                            } else if (nodeType == TNode.ACTIVE_LSR) {
                                graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.ACTIVE_LSR_CONGESTED3), nodePosition.x, nodePosition.y, null);
                            }
                        }
                        if (node.getTicksWithoutEmitting() > TNode.MAX_TICKS_WITHOUT_EMITTING_BEFORE_ALERTING) {
                            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.WORKING), nodePosition.x, nodePosition.y, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // FIX: This is ugly.
            e.printStackTrace();
        }
        this.eventsBuffersLock.unLock();
    }

    /**
     * This method paints events related to the links of the topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintLinksEvents(Graphics2D graphics2D) {
        this.eventsBuffersLock.lock();
        try {
            TSimulationEvent event = null;
            Iterator simulationEventsIterator = this.simulationBuffer.iterator();
            while (simulationEventsIterator.hasNext()) {
                event = (TSimulationEvent) simulationEventsIterator.next();
                if (event != null) {
                    if (event.getSubtype() == TSimulationEvent.LINK_BROKEN) {
                        TLink link = (TLink) event.getSource();
                        Point packetPosition = link.getScreenPacketPosition(50);
                        // FIX: Do not use harcoded values. Use class constants instead.
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LINK_BROKEN), packetPosition.x - 41, packetPosition.y - 41, null);
                    } else if (event.getSubtype() == TSimulationEvent.LINK_RECOVERED) {
                        TLink link = (TLink) event.getSource();
                        Point packetPosition = link.getScreenPacketPosition(50);
                        // FIX: Do not use harcoded values. Use class constants instead.
                        graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.LINK_RECOVERED), packetPosition.x - 41, packetPosition.y - 41, null);
                    }
                }
            }
        } catch (Exception e) {
            // FIX: This is ugly.
            e.printStackTrace();
        }
        this.eventsBuffersLock.unLock();
    }

    /**
     * This method paints the tick number whose events are being painted.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintCurrentTick(Graphics2D graphics2D) {
        // FIX: Do not use harcoded values. Use class constants instead.
        int xPosition = 8;
        // FIX: Do not use harcoded values. Use class constants instead.
        int yPosition = 18;
        String tickText = this.currentTick + " " + translations.getString("JPanelSimulacion.Ns");
        FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
        int textWidth = fontMetrics.charsWidth(tickText.toCharArray(), 0, tickText.length());
        this.bufferedG2D.setColor(Color.LIGHT_GRAY);
        // FIX: Do not use harcoded values. Use class constants instead.
        this.bufferedG2D.fillRect(xPosition - 2, yPosition - 12, textWidth + 6, 18);
        this.bufferedG2D.setColor(Color.WHITE);
        // FIX: Do not use harcoded values. Use class constants instead.
        this.bufferedG2D.fillRect(xPosition - 3, yPosition - 13, textWidth + 5, 17);
        this.bufferedG2D.setColor(Color.BLACK);
        // FIX: Do not use harcoded values. Use class constants instead.
        this.bufferedG2D.drawString(tickText, xPosition, yPosition);
        // FIX: Do not use harcoded values. Use class constants instead.
        this.bufferedG2D.drawRect(xPosition - 3, yPosition - 13, textWidth + 5, 17);
    }

    /**
     * This method paints the legend of the topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void paintLegend(Graphics2D graphics2D) {
        if (this.showLegend) {
            int width = 0;
            int totalWidth = 0;
            int height = 0;
            int upperLeftX = 0;
            int upperLeftY = 0;
            FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_TLDP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_TLDP").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_TLDP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_TLDP").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_GPSRP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_GPSRP").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_GPSRP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_GPSRP").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.LSP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.LSP").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.LSP_de_respaldo").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP_de_respaldo").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.LSP_de_respaldo").toCharArray(), 0, this.translations.getString("JPanelSimulacion.LSP_de_respaldo").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("Paquete_recibido").toCharArray(), 0, this.translations.getString("Paquete_recibido").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("Paquete_recibido").toCharArray(), 0, this.translations.getString("Paquete_recibido").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_enviado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_enviado").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_enviado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_enviado").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_conmutado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_conmutado").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_conmutado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_conmutado").length());
            }
            if ((fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_generado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_generado").length())) > width) {
                width = fontMetrics.charsWidth(this.translations.getString("JPanelSimulacion.Paquete_generado").toCharArray(), 0, this.translations.getString("JPanelSimulacion.Paquete_generado").length());
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            totalWidth = 5 + 13 + 5 + width + 20 + 13 + 5 + width + 5;
            // FIX: Do not use harcoded values. Use class constants instead.
            height = 113;
            // FIX: Do not use harcoded values. Use class constants instead.
            upperLeftX = this.getWidth() - totalWidth - 6;
            // FIX: Do not use harcoded values. Use class constants instead.
            upperLeftY = this.getHeight() - height - 6;
            graphics2D.setColor(Color.LIGHT_GRAY);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.fillRect(upperLeftX + 2, upperLeftY + 2, totalWidth, height);
            graphics2D.setColor(LEGEND_BACKGROUND_COLOR);
            graphics2D.fillRect(upperLeftX, upperLeftY, totalWidth, height);
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawRect(upperLeftX, upperLeftY, totalWidth, height);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_IPV4), upperLeftX + 5, upperLeftY + 5, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_IPv4"), upperLeftX + 23, upperLeftY + 18);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_IPV4_GOS), upperLeftX + 5, upperLeftY + 23, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_IPv4_GOS"), upperLeftX + 23, upperLeftY + 36);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_MPLS), upperLeftX + 5, upperLeftY + 41, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_MPLS"), upperLeftX + 23, upperLeftY + 54);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_MPLS_GOS), upperLeftX + 5, upperLeftY + 59, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_MPLS_GOS"), upperLeftX + 23, upperLeftY + 72);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PDU_LDP), upperLeftX + 5, upperLeftY + 77, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_TLDP"), upperLeftX + 23, upperLeftY + 90);
            graphics2D.setColor(Color.LIGHT_GRAY);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawOval(upperLeftX + 5, upperLeftY + 95, 13, 13);
            graphics2D.setColor(Color.BLACK);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.fillOval(upperLeftX + 8, upperLeftY + 98, 7, 7);
            graphics2D.setColor(Color.RED);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.fillOval(upperLeftX + 9, upperLeftY + 99, 5, 5);
            graphics2D.setColor(Color.YELLOW);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.fillOval(upperLeftX + 10, upperLeftY + 100, 3, 3);
            graphics2D.setColor(Color.BLACK);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.fillOval(upperLeftX + 11, upperLeftY + 101, 1, 1);
            graphics2D.setColor(Color.BLACK);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_GPSRP"), upperLeftX + 23, upperLeftY + 108);
            // FIX: Do not use harcoded values. Use class constants instead.
            upperLeftX = upperLeftX + 5 + 13 + 5 + width + 20 - 5;
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_RECEIVED), upperLeftX + 5, upperLeftY + 5, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("Paquete_recibido"), upperLeftX + 23, upperLeftY + 18);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_GENERATED), upperLeftX + 5, upperLeftY + 23, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_generado"), upperLeftX + 23, upperLeftY + 36);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_SENT), upperLeftX + 5, upperLeftY + 41, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_enviado"), upperLeftX + 23, upperLeftY + 54);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawImage(this.imageBroker.obtenerImagen(TImageBroker.PACKET_SWITCHED), upperLeftX + 5, upperLeftY + 59, null);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.Paquete_conmutado"), upperLeftX + 23, upperLeftY + 72);
            // FIX: Do not use harcoded values. Use class constants instead.
            float dash1[] = {5.0f};
            // FIX: Do not use harcoded values. Use class constants instead.
            BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);
            this.bufferedG2D.setColor(this.LSP_COLOR);
            this.bufferedG2D.setStroke(dashed);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawLine(upperLeftX - 5, upperLeftY + 84, upperLeftX - 5 + 30, upperLeftY + 84);
            this.bufferedG2D.setColor(Color.BLACK);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.LSP"), upperLeftX + 35, upperLeftY + 90);
            // FIX: Do not use harcoded values. Use class constants instead.
            float dash2[] = {10.0f, 5.0f, 0.2f, 5.0f};
            // FIX: Do not use harcoded values. Use class constants instead.
            BasicStroke dashed2 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash2, 0.0f);
            this.bufferedG2D.setColor(Color.BLACK);
            this.bufferedG2D.setStroke(dashed2);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawLine(upperLeftX - 5, upperLeftY + 102, upperLeftX - 5 + 30, upperLeftY + 102);
            this.bufferedG2D.setColor(Color.BLACK);
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawString(this.translations.getString("JPanelSimulacion.LSP_de_respaldo"), upperLeftX + 35, upperLeftY + 108);
            this.bufferedG2D.setStroke(new BasicStroke(1.0f));
        }
    }

    /**
     * This method gets a screeenshot corresponding to the current topology
     * simulation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a screeenshot corresponding to the current topology simulation.
     * @since 2.0
     */
    private BufferedImage getSimulationScreenshot() {
        if (this.bufferedImage == null) {
            this.bufferedImage = new BufferedImage(this.screenSize.width, this.screenSize.height, BufferedImage.TYPE_4BYTE_ABGR);
            this.bufferedG2D = this.bufferedImage.createGraphics();
        }
        prepareImage(this.bufferedG2D);
        if (this.topology != null) {
            paintDomain(this.bufferedG2D);
            paintLinks(this.bufferedG2D);
            paintPacketsEvents(this.bufferedG2D);
            paintNodes(this.bufferedG2D);
            paintNodesEvents(this.bufferedG2D);
            paintLinksEvents(this.bufferedG2D);
            paintCurrentTick(this.bufferedG2D);
            paintLegend(this.bufferedG2D);
        }
        return this.bufferedImage;
    }

    /**
     * This method paints the topology simulation whenever necessary,
     * automatically.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics the place where the topology simulation has to be
     * painted.
     * @since 2.0
     */
    @Override
    public void paint(Graphics graphics) {
        BufferedImage bufferedImageAux = this.getSimulationScreenshot();
        // FIX: Do not use harcoded values. Use class constants instead.
        graphics.drawImage(bufferedImageAux, 0, 0, null);
    }

    /**
     * This gets wheteher the legend is being shown in the simulation panel or
     * not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE if the topology is being shown in the simulation panel.
     * Otherwise, FALSE.
     * @since 2.0
     */
    public boolean getShowLegend() {
        return this.showLegend;
    }

    /**
     * This methods sets/hides the legend in the simulation panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param showLegend TRUE, to show the legend in the simulation panel.
     * FALSE, to hide it.
     * @since 2.0
     */
    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    private TImageBroker imageBroker;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedG2D;
    private TTopology topology;
    private Dimension screenSize;
    private int maxX;
    private int maxY;
    private TreeSet eventsBuffer;
    private TreeSet simulationBuffer;
    private long currentTick;
    private TLock eventsBuffersLock;
    private int simulationSpeedInMsPerTick;
    private boolean showLegend;
    private ResourceBundle translations;

    private static Color LEGEND_BACKGROUND_COLOR = new Color(255, 255, 255);
    private static Color LINK_NAME_COLOR = new Color(255, 255, 230);
    private static Color DOMAIN_BORDER_COLOR = new Color(128, 193, 255);
    private static Color DOMAIN_BACKGROUND_COLOR = new Color(204, 230, 255);
    private static Color LSP_COLOR = new Color(0, 0, 200);
    private static Color EXTERNAL_LINK_COLOR = Color.GRAY;
    private static Color INTERNAL_LINK_COLOR = Color.BLUE;
    private static Color BROKEN_LINK_COLOR = Color.RED;
}
