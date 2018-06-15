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

import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
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
import javax.swing.JPanel;

/**
 * This class implements a panel that paints a topology. It is used mainly to
 * let the user show the topology he/she is designing.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JDesignPanel extends JPanel {

    /**
     * This is the constructor of the class and creates a new instance of
     * JDesignPanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public JDesignPanel() {
        initComponents();
    }

    /**
     * This is the constructor of the class and creates a new instance of
     * JDesignPanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param imageBroker The image broker that will provide the needed images
     * to paint the topology.
     * @since 2.0
     */
    public JDesignPanel(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
        initComponents();
    }

    /**
     * This method sets the image broker that will provide the needed images to
     * paint the topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param imageBroker The image broker that will provide the needed images
     * to paint the topology.
     * @since 2.0
     */
    public void setImageBroker(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
    }

    /**
     * This method initialize some attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void initComponents() {
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.bufferedImage = null;
        this.bufferedG2D = null;
        this.topology = null;
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxX = 10;
        this.maxY = 10;
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
     * This method gets the thickness that has to be used when painting the
     * link whose delay is specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param linkDelay the thickness that has to be used when painting the
     * link whose delay is specified as an argument.
     * @since 2.0
     */
    private double getLinkThickness(double linkDelay) {
        // FIX: Do not use harcoded values. Use class constants instead.
        return (16 / Math.log(linkDelay + 100));
    }

    /**
     * This method prepares the place where the topology is going to be painted
     * setting attributes as the size, antialias, and so on..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics2D the place where the topology is going to be painted.
     * @since 2.0
     */
    private void prepareImage(Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.WHITE);
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
            if ((node.getNodeType() == TNode.LER) || (node.getNodeType() == TNode.ACTIVE_LER)) {
                // FIX: Do not use harcoded values. Use class constants instead.
                polygon.addPoint(node.getScreenPosition().x + 24, node.getScreenPosition().y + 24);
                vertexes++;
            }
        };
        // FIX: Do not use harcoded values. Use class constants instead.
        if (vertexes > 2) {
            // FIX: Do not use harcoded values. Use class constants instead.
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
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.setColor(DOMAIN_BACKGROUND_COLOR);
            graphics2D.fillRect(x1, y1, width, height);
            graphics2D.setColor(DOMAIN_BORDER_COLOR);
            graphics2D.drawRect(x1, y1, width, height);
            // FIX: Do not use harcoded values. Use class constants instead.
        } else if (vertexes == 1) {
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.setColor(DOMAIN_BACKGROUND_COLOR);
            graphics2D.fillOval(polygon.xpoints[0] - 50, polygon.ypoints[0] - 40, 100, 80);
            graphics2D.setColor(DOMAIN_BORDER_COLOR);
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
            // FIX: Do not use harcoded values. Use class constants instead.
            graphics2D.drawLine(headEnd.x + 24, headEnd.y + 24, tailEnd.x + 24, tailEnd.y + 24);
            graphics2D.setStroke(new BasicStroke((float) 1));
            if (link.getShowName()) {
                FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                int textWidth = fontMetrics.charsWidth(link.getName().toCharArray(), 0, link.getName().length());
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX1 = link.getHeadEndNode().getScreenPosition().x + 24;
                int posY1 = link.getHeadEndNode().getScreenPosition().y + 24;
                int posX2 = link.getTailEndNode().getScreenPosition().x + 24;
                int posY2 = link.getTailEndNode().getScreenPosition().y + 24;
                int posX = Math.min(posX1, posX2) + ((Math.max(posX1, posX2) - Math.min(posX1, posX2)) / 2) - (textWidth / 2);
                int posY = Math.min(posY1, posY2) + ((Math.max(posY1, posY2) - Math.min(posY1, posY2)) / 2) + 5;
                graphics2D.setColor(LINK_NAME_COLOR);
                graphics2D.fillRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
                graphics2D.setColor(Color.GRAY);
                graphics2D.drawString(link.getName(), posX, posY);
                graphics2D.drawRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
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
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.TRAFFIC_GENERATOR), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.TRAFFIC_GENERATOR_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.TRAFFIC_SINK: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.TRAFFIC_SINK), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.TRAFFIC_SINK_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.LER: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.LER), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.LER_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.ACTIVE_LER: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.ACTIVE_LER), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.ACTIVE_LER_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.LSR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.LSR), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.LSR_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.ACTIVE_LSR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.ACTIVE_LSR), nodePosition.x, nodePosition.y, null);
                    } else {
                        graphics2D.drawImage(this.imageBroker.getImage(TImageBroker.ACTIVE_LSR_MOVING), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
            }
            if (node.getShowName()) {
                FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
                int textWidth = fontMetrics.charsWidth(node.getName().toCharArray(), 0, node.getName().length());
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX = (node.getScreenPosition().x + 24) - ((textWidth / 2));
                int posY = node.getScreenPosition().y + 60;
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
                graphics2D.setColor(Color.GRAY);
                graphics2D.drawString(node.getName(), posX, posY);
                graphics2D.drawRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
            }
        }
    }

    /**
     * This method gets a screeenshot corresponding to the current topology
     * design.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a screeenshot corresponding to the current topology design.
     * @since 2.0
     */
    public BufferedImage getDesignScreenshot() {
        if (this.bufferedImage == null) {
            this.bufferedImage = new BufferedImage(this.screenSize.width, this.screenSize.height, BufferedImage.TYPE_4BYTE_ABGR);
            this.bufferedG2D = this.bufferedImage.createGraphics();
        }
        prepareImage(this.bufferedG2D);
        if (this.topology != null) {
            paintDomain(this.bufferedG2D);
            paintLinks(this.bufferedG2D);
            paintNodes(this.bufferedG2D);
        }
        return bufferedImage;
    }

    /**
     * This method paints the topology design whenever necessary, automatically.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param graphics the place where the topology design has to be painted.
     * @since 2.0
     */
    @Override
    public void paint(Graphics graphics) {
        BufferedImage bufferedImageAux = this.getDesignScreenshot();
        graphics.drawImage(bufferedImageAux, 0, 0, null);
    }

    private static Color LINK_NAME_COLOR = new Color(255, 255, 230);
    private static Color DOMAIN_BORDER_COLOR = new Color(128, 193, 255);
    private static Color DOMAIN_BACKGROUND_COLOR = new Color(204, 230, 255);
    private static Color EXTERNAL_LINK_COLOR = Color.GRAY;
    private static Color INTERNAL_LINK_COLOR = Color.BLUE;

    
    private TImageBroker imageBroker;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedG2D;
    private TTopology topology;
    private Dimension screenSize;
    private int maxX;
    private int maxY;
}
