/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package simMPLS.ui.dialogs;

import java.awt.*;
import java.awt.Toolkit.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import simMPLS.ui.simulator.JPanelDisenio;

/**
 * Esa clase implementa una panel en miniatura que representar� al panel de dise�o
 * principal y que sirve para colocar, de forma visual y en el momento de creaci�n,
 * los nodos que se van insertando.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JPanelCoordenadas extends javax.swing.JPanel {

    /**
     * Crea una nueva instancia de JPanelDisenio
     * @since 2.0
     */
    public JPanelCoordenadas() {
        panelOrigen = null;
        initComponents();
    }

    /**
     * Crea una nueva instancia de JPanelDisenio
     * @since 2.0
     * @param pd Panel de dise�o cuay representaci�n en miniatura mostrar� el panel de
     * coordenadas.
     */    
    public JPanelCoordenadas(JPanelDisenio pd) {
        panelOrigen = pd;
        initComponents();
    }

    /**
     * Este m�todo establece el panel de dise�o que se mostrar� en miniatura en el
     * panel de coordenadas.
     * @since 2.0
     * @param pd El panel de dise�o.
     */    
    public void ponerPanelOrigen(JPanelDisenio pd) {
        panelOrigen = pd;
        coordenadas.x = getSize().width/2;
        coordenadas.y = getSize().height/2;
    }

    private void initComponents () {
        coordenadas = new Point(0,0);
	imagenbuf = null;
    }

    /**
     * Dado un clic sobre el panel de coordenadas, este m�todo devuelve correspondiente
     * la coordenada X real en el panel de dise�o.
     * @since 2.0
     * @return La coordenada X real en el panel de dise�o.
     */    
    public int obtenerXReal() {
        int anchoReal = panelOrigen.getSize().width;
        int anchoReducido = this.getSize().width;
        return ((anchoReal * coordenadas.x) / anchoReducido);
    }

    /**
     * Dado un clic sobre el panel de coordenadas, este m�todo devuelve correspondiente
     * la coordenada Y real en el panel de dise�o.
     * @since 2.0
     * @return La coordenada Y real en el panel de dise�o.
     */    
    public int obtenerYReal() {
        int altoReal = panelOrigen.getSize().height;
        int altoReducido = this.getSize().height;
        return ((altoReal * coordenadas.y) / altoReducido);
    }

    /**
     * Este m�todo dibuja un punto rojo indicativo en el panel de coordenadas, dado un
     * punto real del panel de dise�o.
     * @since 2.0
     * @param p Punto (x,y) real del panel de dise�o.
     */    
    public void ponerCoordenadasReducidas(Point p) {
        if (this.isEnabled()) {
            coordenadas.x = p.x;
            coordenadas.y = p.y;
            repaint();
        }
    }

    /**
     * Este m�todo refresca el panel de coordenadas cuando sea necesario.
     * @since 2.0
     * @param g Lienzo sobre el cual se va a redibujar el panel de coordenadas.
     */    
    public void paint(java.awt.Graphics g) {
        if (this.isEnabled()) {
            Graphics2D g2D = (Graphics2D) g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (panelOrigen != null) {
                BufferedImage ima = panelOrigen.capturaDeDisenio();
                BufferedImage ima2 = ima.getSubimage(0,0,panelOrigen.getSize().width,panelOrigen.getSize().height);
                g2D.drawImage(ima2.getScaledInstance(getSize().width, getSize().height, BufferedImage.SCALE_FAST), 0, 0, null);
            }
            else {
                g2D.setColor(Color.WHITE);
                g2D.fillRect(0,0,getSize().width,getSize().height);
            }
            g2D.setColor(Color.BLACK);
            g2D.drawRect(0,0,getSize().width-1,getSize().height-1);
            g2D.setColor(Color.RED);
            g2D.fillOval(coordenadas.x-2, coordenadas.y-2, 5, 5);
        } else {
            Graphics2D g2D = (Graphics2D) g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setColor(Color.WHITE);
            g2D.fillRect(0, 0, this.getWidth()-1, this.getHeight()-1);
            g2D.setStroke(new BasicStroke((float) 2));
            g2D.setColor(Color.RED);
            g2D.drawLine(0, 0, this.getWidth(), this.getHeight());
            g2D.drawLine(this.getWidth(), 0, 0, this.getHeight());
            g2D.setColor(Color.GRAY);
            g2D.setStroke(new BasicStroke((float) 1));
            g2D.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
        }
    }  

    private JPanelDisenio panelOrigen;
    private BufferedImage imagenbuf;
    private Point coordenadas;
}
