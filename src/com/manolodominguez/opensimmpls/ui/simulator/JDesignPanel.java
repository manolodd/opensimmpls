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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 * Esta clase implementa un panel donde se puede dise�ar la topolog�a que luego
 * va a ser representada por el simulador.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JDesignPanel extends JPanel {

    /**
     * Crea una nueva instancia de JPanelDisenio.
     *
     * @since 2.0
     */
    public JDesignPanel() {
        initComponents();
    }

    /**
     * Crea una nueva instancia de JPanelDisenio.
     *
     * @since 2.0
     * @param imageBroker El dispensador de im�genes. De �l tomar� el panel
     * todas las im�genes que tenga que mostrar en la pantalla.
     */
    public JDesignPanel(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
        initComponents();
    }

    /**
     * Este m�todo asocia con el panel de dise�o un dispensador de im�genes del
     * cual el panel tomar� las im�genes que tenga que mostrar en pantalla.
     *
     * @since 2.0
     * @param imageBroker El dispensador de im�genes.
     */
    public void ponerDispensadorDeImagenes(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
    }

    /**
     * @since 2.0
     */
    private void initComponents() {
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.buffer = null;
        this.bufferedImage = null;
        this.bufferedG2D = null;
        this.topology = null;
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxX = 10;
        this.maxY = 10;
    }

    /**
     * Este m�todo permite establecer una topolog�a ya creada en el panel de
     * dise�o.
     *
     * @since 2.0
     * @param topology La topolog�a ya creda que queremos mostrar y modificar en
     * el panel de dise�o.
     */
    public void ponerTopologia(TTopology topology) {
        this.topology = topology;
    }

    /**
     * Este m�todo permite obtener el grosor en p�xeles con que debe mostrarse
     * un enlace en el panel de dise�o a tenor de su retardo real.
     *
     * @param linkDelay El retardo real del enlace.
     * @return El grosor en p�xeles con que se debe mostrar el enlace en la zona
     * de dise�o.
     * @since 2.0
     */
    public double obtenerGrosorEnlace(double linkDelay) {
        // FIX: Do not use harcoded values. Use class constants instead.
        return (16 / Math.log(linkDelay + 100));
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */
    private void prepararImagen(Graphics2D g2Dbuf) {
        g2Dbuf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Dbuf.setColor(Color.WHITE);
        g2Dbuf.fillRect(0, 0, screenSize.width, screenSize.height);
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */
    private void dibujarDominio(Graphics2D g2Dbuf) {
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
            g2Dbuf.setColor(new Color(239, 222, 209));
            g2Dbuf.fillPolygon(polygon);
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.setColor(new Color(232, 212, 197));
            g2Dbuf.drawPolygon(polygon);
        // FIX: Do not use harcoded values. Use class constants instead.
        } else if (vertexes == 2) {
            int x1 = Math.min(polygon.xpoints[0], polygon.xpoints[1]);
            int y1 = Math.min(polygon.ypoints[0], polygon.ypoints[1]);
            int x2 = Math.max(polygon.xpoints[0], polygon.xpoints[1]);
            int y2 = Math.max(polygon.ypoints[0], polygon.ypoints[1]);
            int width = x2 - x1;
            int height = y2 - y1;
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.setColor(new Color(239, 222, 209));
            g2Dbuf.fillRect(x1, y1, width, height);
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.setColor(new Color(232, 212, 197));
            g2Dbuf.drawRect(x1, y1, width, height);
        // FIX: Do not use harcoded values. Use class constants instead.
        } else if (vertexes == 1) {
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.setColor(new Color(239, 222, 209));
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.fillOval(polygon.xpoints[0] - 50, polygon.ypoints[0] - 40, 100, 80);
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.setColor(new Color(232, 212, 197));
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.drawOval(polygon.xpoints[0] - 50, polygon.ypoints[0] - 40, 100, 80);
        }
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */
    private void dibujarEnlaces(Graphics2D g2Dbuf) {
        Iterator linksIterator = this.topology.getLinksIterator();
        while (linksIterator.hasNext()) {
            TLink link = (TLink) linksIterator.next();
            Point headEnd = link.getHeadEndNode().getScreenPosition();
            Point tailEnd = link.getTailEndNode().getScreenPosition();
            int linkDelay = link.getDelay();
            g2Dbuf.setStroke(new BasicStroke((float) obtenerGrosorEnlace(linkDelay)));
            if (link.getLinkType() == TLink.EXTERNAL_LINK) {
                g2Dbuf.setColor(Color.GRAY);
            } else {
                g2Dbuf.setColor(Color.BLUE);
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            g2Dbuf.drawLine(headEnd.x + 24, headEnd.y + 24, tailEnd.x + 24, tailEnd.y + 24);
            g2Dbuf.setStroke(new BasicStroke((float) 1));
            if (link.getShowName()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int textWidth = fm.charsWidth(link.getName().toCharArray(), 0, link.getName().length());
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX1 = link.getHeadEndNode().getScreenPosition().x + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posY1 = link.getHeadEndNode().getScreenPosition().y + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX2 = link.getTailEndNode().getScreenPosition().x + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posY2 = link.getTailEndNode().getScreenPosition().y + 24;
                // FIX: Do not use harcoded values. Use class constants instead.
                int posX = Math.min(posX1, posX2) + ((Math.max(posX1, posX2) - Math.min(posX1, posX2)) / 2) - (textWidth / 2);
                // FIX: Do not use harcoded values. Use class constants instead.
                int posY = Math.min(posY1, posY2) + ((Math.max(posY1, posY2) - Math.min(posY1, posY2)) / 2) + 5;
                // FIX: Do not use harcoded values. Use class constants instead.
                g2Dbuf.setColor(new Color(255, 254, 226));
                // FIX: Do not use harcoded values. Use class constants instead.
                g2Dbuf.fillRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(link.getName(), posX, posY);
                // FIX: Do not use harcoded values. Use class constants instead.
                g2Dbuf.drawRoundRect(posX - 3, posY - 13, textWidth + 5, 17, 10, 10);
            }
        }
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */
    private void dibujarNodos(Graphics2D g2Dbuf) {
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxX = 10;
        // FIX: Do not use harcoded values. Use class constants instead.
        this.maxY = 10;
        Iterator nodesIterator = topology.getNodesIterator();
        while (nodesIterator.hasNext()) {
            TNode node = (TNode) nodesIterator.next();
            Point nodePosition = node.getScreenPosition();
            // FIX: Do not use harcoded values. Use class constants instead.
            if ((nodePosition.x + 48) > maxX) {
            // FIX: Do not use harcoded values. Use class constants instead.
                maxX = nodePosition.x + 48;
            }
            // FIX: Do not use harcoded values. Use class constants instead.
            if ((nodePosition.y + 48) > maxY) {
            // FIX: Do not use harcoded values. Use class constants instead.
                maxY = nodePosition.y + 48;
            }
            this.setPreferredSize(new Dimension(maxX, maxY));
            this.revalidate();
            int nodeType = node.getNodeType();
            switch (nodeType) {
                case TNode.TRAFFIC_GENERATOR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.EMISOR), nodePosition.x, nodePosition.y, null);
                    } else {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.EMISOR_MOVIENDOSE), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.TRAFFIC_SINK: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.RECEPTOR), nodePosition.x, nodePosition.y, null);
                    } else {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.RECEPTOR_MOVIENDOSE), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.LER: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LER), nodePosition.x, nodePosition.y, null);
                    } else {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LER_MOVIENDOSE), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.ACTIVE_LER: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LERA), nodePosition.x, nodePosition.y, null);
                    } else {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LERA_MOVIENDOSE), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.LSR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LSR), nodePosition.x, nodePosition.y, null);
                    } else {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LSR_MOVIENDOSE), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
                case TNode.ACTIVE_LSR: {
                    if (node.isSelected() == TNode.UNSELECTED) {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LSRA), nodePosition.x, nodePosition.y, null);
                    } else {
                        g2Dbuf.drawImage(imageBroker.obtenerImagen(TImageBroker.LSRA_MOVIENDOSE), nodePosition.x, nodePosition.y, null);
                    }
                    break;
                }
            }
            if (node.getShowName()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(node.getName().toCharArray(), 0, node.getName().length());
                int posX = (node.getScreenPosition().x + 24) - ((anchoTexto / 2));
                int posY = node.getScreenPosition().y + 60;
                g2Dbuf.setColor(Color.WHITE);
                g2Dbuf.fillRoundRect(posX - 3, posY - 13, anchoTexto + 5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(node.getName(), posX, posY);
                g2Dbuf.drawRoundRect(posX - 3, posY - 13, anchoTexto + 5, 17, 10, 10);
            }
        }
    }

    /**
     * Esta imagen obtiene una captura del dise�o en un momento dado, en forma
     * de imagen bitmap.
     *
     * @since 2.0
     * @return La imagen capturada del dise�o de la topolog�a en un momento
     * dado.
     */
    public BufferedImage capturaDeDisenio() {
        if (bufferedImage == null) {
            bufferedImage = new BufferedImage(screenSize.width, screenSize.height, BufferedImage.TYPE_4BYTE_ABGR);
            bufferedG2D = bufferedImage.createGraphics();
        }
        prepararImagen(bufferedG2D);
        if (topology != null) {
            dibujarDominio(bufferedG2D);
            dibujarEnlaces(bufferedG2D);
            dibujarNodos(bufferedG2D);
        }
        return bufferedImage;
    }

    /**
     * Este m�todo redibuja la pantalla de dise�o cuando es necesario,
     * autom�ticamente.
     *
     * @since 2.0
     * @param g El lienzo donde se debe redibujar la captura del dise�o de la
     * topolog�a.
     */
    public void paint(java.awt.Graphics g) {
        BufferedImage ima = this.capturaDeDisenio();
        g.drawImage(ima, 0, 0, null);
    }

    private TImageBroker imageBroker;
    private Image buffer;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedG2D;
    private TTopology topology;
    private Dimension screenSize;
    private int maxX;
    private int maxY;
}
