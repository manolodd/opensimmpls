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
 * Esa clase implementa una panel en miniatura que representar� al panel de dise�o
 * principal y que sirve para colocar, de forma visual y en el momento de creaci�n,
 * los nodos que se van insertando.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JCoordinatesPanel extends JPanel {

    /**
     * Crea una nueva instancia de JPanelDisenio
     * @since 2.0
     */
    public JCoordinatesPanel() {
        this.designPanel = null;
        initComponents();
    }

    /**
     * Crea una nueva instancia de JPanelDisenio
     * @since 2.0
     * @param designPanel Panel de dise�o cuay representaci�n en miniatura mostrar� el panel de
     * coordenadas.
     */    
    public JCoordinatesPanel(JDesignPanel designPanel) {
        this.designPanel = designPanel;
        initComponents();
    }

    /**
     * Este m�todo establece el panel de dise�o que se mostrar� en miniatura en el
     * panel de coordenadas.
     * @since 2.0
     * @param designPanel El panel de dise�o.
     */    
    public void setDesignPanel(JDesignPanel designPanel) {
        this.designPanel = designPanel;
        this.coordinates.x = getSize().width/2;
        this.coordinates.y = getSize().height/2;
    }

    private void initComponents () {
        this.coordinates = new Point(0,0);
    }

    /**
     * Dado un clic sobre el panel de coordenadas, este m�todo devuelve correspondiente
     * la coordenada X real en el panel de dise�o.
     * @since 2.0
     * @return La coordenada X real en el panel de dise�o.
     */    
    public int getRealX() {
        int realWidth = this.designPanel.getSize().width;
        int scaledWidth = this.getSize().width;
        return ((realWidth * this.coordinates.x) / scaledWidth);
    }

    /**
     * Dado un clic sobre el panel de coordenadas, este m�todo devuelve correspondiente
     * la coordenada Y real en el panel de dise�o.
     * @since 2.0
     * @return La coordenada Y real en el panel de dise�o.
     */    
    public int getRealY() {
        int realHeight = this.designPanel.getSize().height;
        int scaledHeight = this.getSize().height;
        return ((realHeight * this.coordinates.y) / scaledHeight);
    }

    /**
     * Este m�todo dibuja un punto rojo indicativo en el panel de coordenadas, dado un
     * punto real del panel de dise�o.
     * @since 2.0
     * @param coordinates Punto (x,y) real del panel de dise�o.
     */    
    public void setCoordinates(Point coordinates) {
        if (this.isEnabled()) {
            this.coordinates.x = coordinates.x;
            this.coordinates.y = coordinates.y;
            repaint();
        }
    }

    /**
     * Este m�todo refresca el panel de coordenadas cuando sea necesario.
     * @since 2.0
     * @param graphics Lienzo sobre el cual se va a redibujar el panel de coordenadas.
     */    
    @Override
    public void paint(Graphics graphics) {
        if (this.isEnabled()) {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (this.designPanel != null) {
                BufferedImage image = this.designPanel.capturaDeDisenio();
                BufferedImage image2 = image.getSubimage(0,0,this.designPanel.getSize().width,this.designPanel.getSize().height);
                graphics2D.drawImage(image2.getScaledInstance(getSize().width, getSize().height, BufferedImage.SCALE_FAST), 0, 0, null);
            }
            else {
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(0,0,getSize().width,getSize().height);
            }
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawRect(0,0,getSize().width-1,getSize().height-1);
            graphics2D.setColor(Color.RED);
            graphics2D.fillOval(this.coordinates.x-2, this.coordinates.y-2, 5, 5);
        } else {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, this.getWidth()-1, this.getHeight()-1);
            graphics2D.setStroke(new BasicStroke((float) 2));
            graphics2D.setColor(Color.RED);
            graphics2D.drawLine(0, 0, this.getWidth(), this.getHeight());
            graphics2D.drawLine(this.getWidth(), 0, 0, this.getHeight());
            graphics2D.setColor(Color.GRAY);
            graphics2D.setStroke(new BasicStroke((float) 1));
            graphics2D.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
        }
    }  

    private JDesignPanel designPanel;
    private Point coordinates;
}
