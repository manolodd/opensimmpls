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
package com.manolodominguez.opensimmpls.ui.dialogs;

import com.manolodominguez.opensimmpls.ui.simulator.JDesignPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * This class implements a mini-panel that is a scaled version of the main
 * design panel; it is used to let the user choose the place where a new network
 * element should be placed
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JCoordinatesPanel extends JPanel {

    /**
     * Crea una nueva instancia de JPanelDisenio
     *
     * @since 2.0
     */
    public JCoordinatesPanel() {
        this.designPanel = null;
        initComponents();
    }

    /**
     * This is the constructor of the class and creates a new instance of
     * JCoordinatesPanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param designPanel the panel that this JCoordinatesPanel is going to show
     * scaled.
     * @since 2.0
     */
    public JCoordinatesPanel(JDesignPanel designPanel) {
        this.designPanel = designPanel;
        initComponents();
    }

    /**
     * This method sets the main design panel this JCoordinatesPanel is going to
     * show scaled
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param designPanel the panel that this JCoordinatesPanel is going to show
     * scaled.
     * @since 2.0
     */
    public void setDesignPanel(JDesignPanel designPanel) {
        this.designPanel = designPanel;
        this.coordinates.x = getSize().width / 2;
        this.coordinates.y = getSize().height / 2;
    }

    /**
     * This method is called from within the constructor to initialize the panel
     * attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void initComponents() {
        this.coordinates = new Point(0, 0);
    }

    /**
     * This method gets the real X component of the coordinate currently
     * "active" in the coordinates panel (the one marked with a red point in the
     * UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return X component of the coordinate currently "active" in the
     * coordinates panel (the one marked with a red pint in the UI).
     * @since 2.0
     */
    public int getRealX() {
        int realWidth = this.designPanel.getSize().width;
        int scaledWidth = this.getSize().width;
        return ((realWidth * this.coordinates.x) / scaledWidth);
    }

    /**
     * This method gets the real Y component of the coordinate currently
     * "active" in the coordinates panel (the one marked with a red point in the
     * UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return X component of the coordinate currently "active" in the
     * coordinates panel (the one marked with a red pint in the UI).
     * @since 2.0
     */
    public int getRealY() {
        int realHeight = this.designPanel.getSize().height;
        int scaledHeight = this.getSize().height;
        return ((realHeight * this.coordinates.y) / scaledHeight);
    }

    /**
     * This method sets the coordinate of a point of the coordinates panel (to
     * be used after a click on it) and repaint the coordinates panel to paint a
     * red point in that place.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param coordinates the coordinates of a point of the coordinates panel.
     * @since 2.0
     */
    public void setCoordinates(Point coordinates) {
        if (this.isEnabled()) {
            this.coordinates.x = coordinates.x;
            this.coordinates.y = coordinates.y;
            repaint();
        }
    }

    /**
     * This method paints the content of the coordinates panel; this draw a
     * scaled version of the design panel and a red point wherever corresponds.
     *
     * @since 2.0
     * @param graphics A canvas to wich the content of the coordinates panel
     * will be painted.
     */
    @Override
    public void paint(Graphics graphics) {
        if (this.isEnabled()) {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (this.designPanel != null) {
                BufferedImage image = this.designPanel.getDesignScreenshot();
                BufferedImage image2 = image.getSubimage(0, 0, this.designPanel.getSize().width, this.designPanel.getSize().height);
                graphics2D.drawImage(image2.getScaledInstance(getSize().width, getSize().height, BufferedImage.SCALE_FAST), 0, 0, null);
            } else {
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(0, 0, getSize().width, getSize().height);
            }
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
            graphics2D.setColor(Color.RED);
            graphics2D.fillOval(this.coordinates.x - 2, this.coordinates.y - 2, 5, 5);
        } else {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
            graphics2D.setStroke(new BasicStroke((float) 2));
            graphics2D.setColor(Color.RED);
            graphics2D.drawLine(0, 0, this.getWidth(), this.getHeight());
            graphics2D.drawLine(this.getWidth(), 0, 0, this.getHeight());
            graphics2D.setColor(Color.GRAY);
            graphics2D.setStroke(new BasicStroke((float) 1));
            graphics2D.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        }
    }

    private JDesignPanel designPanel;
    private Point coordinates;
}
