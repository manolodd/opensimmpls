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

import java.awt.*;
import java.awt.Toolkit.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.ui.utils.TImagesBroker;

/**
 * Esta clase implementa un panel donde se puede dise�ar la topolog�a que luego va
 * a ser representada por el simulador.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JPanelDisenio extends javax.swing.JPanel {

    /**
     * Crea una nueva instancia de JPanelDisenio.
     * @since 2.0
     */
    public JPanelDisenio() {
        initComponents();
    }

    /**
     * Crea una nueva instancia de JPanelDisenio.
     * @since 2.0
     * @param di El dispensador de im�genes. De �l tomar� el panel todas las im�genes que tenga
     * que mostrar en la pantalla.
     */    
    public JPanelDisenio(TImagesBroker di) {
        dispensadorDeImagenes = di;
        initComponents();
    }

    /**
     * Este m�todo asocia con el panel de dise�o un dispensador de im�genes del cual el
     * panel tomar� las im�genes que tenga que mostrar en pantalla.
     * @since 2.0
     * @param di El dispensador de im�genes.
     */    
    public void ponerDispensadorDeImagenes(TImagesBroker di) {
        dispensadorDeImagenes = di;
    }

    /**
     * @since 2.0
     */    
    private void initComponents () {
        tamPantalla=Toolkit.getDefaultToolkit().getScreenSize();
	buffer = null;
	imagenbuf = null;
        g2Dbuf = null;
        topologia=null;
        maxX = 10;
        maxY = 10;
    }

    /**
     * Este m�todo permite establecer una topolog�a ya creada en el panel de dise�o.
     * @since 2.0
     * @param t La topolog�a ya creda que queremos mostrar y modificar en el panel de dise�o.
     */    
    public void ponerTopologia(TTopology t) {
        topologia = t;
    }

    /**
     * Este m�todo permite obtener el grosor en p�xeles con que debe mostrarse un
     * enlace en el panel de dise�o a tenor de su retardo real.
     * @param delay El retardo real del enlace.
     * @return El grosor en p�xeles con que se debe mostrar el enlace en la zona de dise�o.
     * @since 2.0
     */    
    public double obtenerGrosorEnlace(double delay) {
        return (16/Math.log(delay+100));
    }
    
    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void prepararImagen(Graphics2D g2Dbuf) {
        g2Dbuf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Dbuf.setColor(Color.WHITE);
        g2Dbuf.fillRect(0,0, tamPantalla.width, tamPantalla.height);
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void dibujarDominio(Graphics2D g2Dbuf) {
        Iterator itd = topologia.getNodesIterator();
        TNode nd;
        Polygon pol = new Polygon();
        int vertices = 0;
        while (itd.hasNext()) {
            nd = (TNode) itd.next();
            if ((nd.getNodeType() == TNode.LER) ||
               (nd.getNodeType() == TNode.LERA)) {
                   pol.addPoint(nd.getScreenPosition().x+24, nd.getScreenPosition().y+24);
                   vertices ++;
               }
        };
        if (vertices > 2) {
            g2Dbuf.setColor(new Color(239, 222, 209));
            g2Dbuf.fillPolygon(pol);
            g2Dbuf.setColor(new Color(232, 212, 197));
            g2Dbuf.drawPolygon(pol);
        } else if (vertices == 2) {
            int x1 = Math.min(pol.xpoints[0], pol.xpoints[1]);
            int y1 = Math.min(pol.ypoints[0], pol.ypoints[1]);
            int x2 = Math.max(pol.xpoints[0], pol.xpoints[1]);
            int y2 = Math.max(pol.ypoints[0], pol.ypoints[1]);
            int ancho = x2-x1;
            int alto = y2-y1;
            g2Dbuf.setColor(new Color(239, 222, 209));
            g2Dbuf.fillRect(x1, y1, ancho, alto);
            g2Dbuf.setColor(new Color(232, 212, 197));
            g2Dbuf.drawRect(x1, y1, ancho, alto);
        } else if (vertices == 1) {
            g2Dbuf.setColor(new Color(239, 222, 209));
            g2Dbuf.fillOval(pol.xpoints[0]-50, pol.ypoints[0]-40, 100, 80);
            g2Dbuf.setColor(new Color(232, 212, 197));
            g2Dbuf.drawOval(pol.xpoints[0]-50, pol.ypoints[0]-40, 100, 80);
        }
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void dibujarEnlaces(Graphics2D g2Dbuf) {
        Iterator ite = topologia.getLinksIterator();
        while (ite.hasNext()) {
            TLink enlace = (TLink) ite.next();
            Point inicio = enlace.getHeadEndNode().getScreenPosition();
            Point fin = enlace.getTailEndNode().getScreenPosition();
            int del = enlace.getDelay();
            g2Dbuf.setStroke(new BasicStroke((float) obtenerGrosorEnlace(del)));
            if (enlace.getLinkType() == TLink.EXTERNAL) {
                g2Dbuf.setColor(Color.GRAY);
            } else {
                g2Dbuf.setColor(Color.BLUE);
            }
            g2Dbuf.drawLine(inicio.x+24, inicio.y+24, fin.x+24, fin.y+24);
            g2Dbuf.setStroke(new BasicStroke((float) 1));
            if (enlace.getShowName()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(enlace.getName().toCharArray(), 0, enlace.getName().length());
                int posX1 = enlace.getHeadEndNode().getScreenPosition().x+24;
                int posY1 = enlace.getHeadEndNode().getScreenPosition().y+24;
                int posX2 = enlace.getTailEndNode().getScreenPosition().x+24;
                int posY2 = enlace.getTailEndNode().getScreenPosition().y+24;
                int posX = Math.min(posX1, posX2) + ((Math.max(posX1, posX2) - Math.min(posX1, posX2)) / 2) - (anchoTexto / 2);
                int posY = Math.min(posY1, posY2) + ((Math.max(posY1, posY2) - Math.min(posY1, posY2)) / 2) + 5;
                g2Dbuf.setColor(new Color(255, 254, 226));
                g2Dbuf.fillRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(enlace.getName(), posX, posY);
                g2Dbuf.drawRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
            }
        }
    }

    /**
     * @param g2Dbuf
     * @since 2.0
     */    
    private void dibujarNodos(Graphics2D g2Dbuf) {
        maxX = 10;
        maxY = 10;
        Iterator ite = topologia.getNodesIterator();
        while (ite.hasNext()) {
            TNode nodo = (TNode) ite.next();
            Point posicion = nodo.getScreenPosition();

            if ((posicion.x+48) > maxX)
                maxX = posicion.x+48;
            if ((posicion.y+48) > maxY)
                maxY = posicion.y+48;
            this.setPreferredSize(new Dimension(maxX, maxY));
            this.revalidate();

            int tipo = nodo.getNodeType();
            switch (tipo) {
                case TNode.SENDER: {
                    if (nodo.getStatus() == TNode.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.EMISOR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.EMISOR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.RECEIVER: {
                    if (nodo.getStatus() == TNode.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.RECEPTOR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.RECEPTOR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.LER: {
                    if (nodo.getStatus() == TNode.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LER), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LER_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.LERA: {
                    if (nodo.getStatus() == TNode.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LERA), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LERA_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.LSR: {
                    if (nodo.getStatus() == TNode.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LSR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LSR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNode.LSRA: {
                    if (nodo.getStatus() == TNode.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LSRA), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TImagesBroker.LSRA_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
            }
            if (nodo.getShowName()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(nodo.getName().toCharArray(), 0, nodo.getName().length());
                int posX = (nodo.getScreenPosition().x + 24) - ((anchoTexto/2));
                int posY = nodo.getScreenPosition().y+60;
                g2Dbuf.setColor(Color.WHITE);
                g2Dbuf.fillRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(nodo.getName(), posX, posY);
                g2Dbuf.drawRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
            }
        }
    }

    /**
     * Esta imagen obtiene una captura del dise�o en un momento dado, en forma de
     * imagen bitmap.
     * @since 2.0
     * @return La imagen capturada del dise�o de la topolog�a en un momento dado.
     */    
    public BufferedImage capturaDeDisenio() {
        if (imagenbuf == null) {
            imagenbuf = new BufferedImage(tamPantalla.width, tamPantalla.height, BufferedImage.TYPE_4BYTE_ABGR);
            g2Dbuf = imagenbuf.createGraphics();
        }
        prepararImagen(g2Dbuf);
        if (topologia != null) {
            dibujarDominio(g2Dbuf);
            dibujarEnlaces(g2Dbuf);
            dibujarNodos(g2Dbuf);
        }
        return imagenbuf;
    }

    /**
     * Este m�todo redibuja la pantalla de dise�o cuando es necesario, autom�ticamente.
     * @since 2.0
     * @param g El lienzo donde se debe redibujar la captura del dise�o de la topolog�a.
     */    
    public void paint(java.awt.Graphics g) {
        BufferedImage ima = this.capturaDeDisenio();
        g.drawImage(ima, 0, 0, null);
    }  

    /**
     * @since 2.0
     */    
    private TImagesBroker dispensadorDeImagenes;
    /**
     * @since 2.0
     */    
    private Image buffer;
    /**
     * @since 2.0
     */    
    private BufferedImage imagenbuf;
    /**
     * @since 2.0
     */    
    private Graphics2D g2Dbuf;
    /**
     * @since 2.0
     */    
    private TTopology topologia;
    /**
     * @since 2.0
     */    
    private Dimension tamPantalla;
    /**
     * @since 2.0
     */    
    private int maxX;
    /**
     * @since 2.0
     */    
    private int maxY;
}
