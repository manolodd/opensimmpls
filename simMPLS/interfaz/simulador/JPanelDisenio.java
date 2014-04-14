//**************************************************************************
// Nombre......: JPanelDisenio.java
// Proyecto....: Open SimMPLS
// Descripción.: Implementación de un panel de dibujo que responderá a los
// ............: eventos del ratón y que se utilizará como base para diseñar
// ............: la topología de la red.
// Fecha.......: 19/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.interfaz.simulador;

import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.Toolkit.*;
import java.awt.geom.*;
import java.util.*;
import simMPLS.escenario.*;
import simMPLS.interfaz.utiles.*;

/**
 * Esta clase implementa un panel donde se puede diseñar la topología que luego va
 * a ser representada por el simulador.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JPanelDisenio extends javax.swing.JPanel {

    /**
     * Crea una nueva instancia de JPanelDisenio.
     * @since 1.0
     */
    public JPanelDisenio() {
        initComponents();
    }

    /**
     * Crea una nueva instancia de JPanelDisenio.
     * @since 1.0
     * @param di El dispensador de imágenes. De él tomará el panel todas las imágenes que tenga
     * que mostrar en la pantalla.
     */    
    public JPanelDisenio(TDispensadorDeImagenes di) {
        dispensadorDeImagenes = di;
        initComponents();
    }

    /**
     * Este método asocia con el panel de diseño un dispensador de imágenes del cual el
     * panel tomará las imágenes que tenga que mostrar en pantalla.
     * @since 1.0
     * @param di El dispensador de imágenes.
     */    
    public void ponerDispensadorDeImagenes(TDispensadorDeImagenes di) {
        dispensadorDeImagenes = di;
    }

    /**
     * @since 1.0
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
     * Este método permite establecer una topología ya creada en el panel de diseño.
     * @since 1.0
     * @param t La topología ya creda que queremos mostrar y modificar en el panel de diseño.
     */    
    public void ponerTopologia(TTopologia t) {
        topologia = t;
    }

    /**
     * Este método permite obtener el grosor en píxeles con que debe mostrarse un
     * enlace en el panel de diseño a tenor de su retardo real.
     * @param delay El retardo real del enlace.
     * @return El grosor en píxeles con que se debe mostrar el enlace en la zona de diseño.
     * @since 1.0
     */    
    public double obtenerGrosorEnlace(double delay) {
        return (16/Math.log(delay+100));
    }
    
    /**
     * @param g2Dbuf
     * @since 1.0
     */    
    private void prepararImagen(Graphics2D g2Dbuf) {
        g2Dbuf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Dbuf.setColor(Color.WHITE);
        g2Dbuf.fillRect(0,0, tamPantalla.width, tamPantalla.height);
    }

    /**
     * @param g2Dbuf
     * @since 1.0
     */    
    private void dibujarDominio(Graphics2D g2Dbuf) {
        Iterator itd = topologia.obtenerIteradorNodos();
        TNodoTopologia nd;
        Polygon pol = new Polygon();
        int vertices = 0;
        while (itd.hasNext()) {
            nd = (TNodoTopologia) itd.next();
            if ((nd.obtenerTipo() == TNodoTopologia.LER) ||
               (nd.obtenerTipo() == TNodoTopologia.LERA)) {
                   pol.addPoint(nd.obtenerPosicion().x+24, nd.obtenerPosicion().y+24);
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
     * @since 1.0
     */    
    private void dibujarEnlaces(Graphics2D g2Dbuf) {
        Iterator ite = topologia.obtenerIteradorEnlaces();
        while (ite.hasNext()) {
            TEnlaceTopologia enlace = (TEnlaceTopologia) ite.next();
            Point inicio = enlace.obtenerExtremo1().obtenerPosicion();
            Point fin = enlace.obtenerExtremo2().obtenerPosicion();
            int del = enlace.obtenerDelay();
            g2Dbuf.setStroke(new BasicStroke((float) obtenerGrosorEnlace(del)));
            if (enlace.obtenerTipo() == TEnlaceTopologia.EXTERNO) {
                g2Dbuf.setColor(Color.GRAY);
            } else {
                g2Dbuf.setColor(Color.BLUE);
            }
            g2Dbuf.drawLine(inicio.x+24, inicio.y+24, fin.x+24, fin.y+24);
            g2Dbuf.setStroke(new BasicStroke((float) 1));
            if (enlace.obtenerMostrarNombre()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(enlace.obtenerNombre().toCharArray(), 0, enlace.obtenerNombre().length());
                int posX1 = enlace.obtenerExtremo1().obtenerPosicion().x+24;
                int posY1 = enlace.obtenerExtremo1().obtenerPosicion().y+24;
                int posX2 = enlace.obtenerExtremo2().obtenerPosicion().x+24;
                int posY2 = enlace.obtenerExtremo2().obtenerPosicion().y+24;
                int posX = Math.min(posX1, posX2) + ((Math.max(posX1, posX2) - Math.min(posX1, posX2)) / 2) - (anchoTexto / 2);
                int posY = Math.min(posY1, posY2) + ((Math.max(posY1, posY2) - Math.min(posY1, posY2)) / 2) + 5;
                g2Dbuf.setColor(new Color(255, 254, 226));
                g2Dbuf.fillRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(enlace.obtenerNombre(), posX, posY);
                g2Dbuf.drawRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
            }
        }
    }

    /**
     * @param g2Dbuf
     * @since 1.0
     */    
    private void dibujarNodos(Graphics2D g2Dbuf) {
        maxX = 10;
        maxY = 10;
        Iterator ite = topologia.obtenerIteradorNodos();
        while (ite.hasNext()) {
            TNodoTopologia nodo = (TNodoTopologia) ite.next();
            Point posicion = nodo.obtenerPosicion();

            if ((posicion.x+48) > maxX)
                maxX = posicion.x+48;
            if ((posicion.y+48) > maxY)
                maxY = posicion.y+48;
            this.setPreferredSize(new Dimension(maxX, maxY));
            this.revalidate();

            int tipo = nodo.obtenerTipo();
            switch (tipo) {
                case TNodoTopologia.EMISOR: {
                    if (nodo.obtenerEstado() == TNodoTopologia.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.EMISOR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.EMISOR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNodoTopologia.RECEPTOR: {
                    if (nodo.obtenerEstado() == TNodoTopologia.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.RECEPTOR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.RECEPTOR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNodoTopologia.LER: {
                    if (nodo.obtenerEstado() == TNodoTopologia.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LER), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LER_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNodoTopologia.LERA: {
                    if (nodo.obtenerEstado() == TNodoTopologia.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LERA), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LERA_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNodoTopologia.LSR: {
                    if (nodo.obtenerEstado() == TNodoTopologia.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LSR), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LSR_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
                case TNodoTopologia.LSRA: {
                    if (nodo.obtenerEstado() == TNodoTopologia.DESELECCIONADO)
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LSRA), posicion.x, posicion.y, null);
                    else
                        g2Dbuf.drawImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.LSRA_MOVIENDOSE), posicion.x, posicion.y, null);
                    break;
                }
            }
            if (nodo.obtenerMostrarNombre()) {
                FontMetrics fm = this.getFontMetrics(this.getFont());
                int anchoTexto = fm.charsWidth(nodo.obtenerNombre().toCharArray(), 0, nodo.obtenerNombre().length());
                int posX = (nodo.obtenerPosicion().x + 24) - ((anchoTexto/2));
                int posY = nodo.obtenerPosicion().y+60;
                g2Dbuf.setColor(Color.WHITE);
                g2Dbuf.fillRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
                g2Dbuf.setColor(Color.GRAY);
                g2Dbuf.drawString(nodo.obtenerNombre(), posX, posY);
                g2Dbuf.drawRoundRect(posX-3, posY-13, anchoTexto+5, 17, 10, 10);
            }
        }
    }

    /**
     * Esta imagen obtiene una captura del diseño en un momento dado, en forma de
     * imagen bitmap.
     * @since 1.0
     * @return La imagen capturada del diseño de la topología en un momento dado.
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
     * Este método redibuja la pantalla de diseño cuando es necesario, automáticamente.
     * @since 1.0
     * @param g El lienzo donde se debe redibujar la captura del diseño de la topología.
     */    
    public void paint(java.awt.Graphics g) {
        BufferedImage ima = this.capturaDeDisenio();
        g.drawImage(ima, 0, 0, null);
    }  

    /**
     * @since 1.0
     */    
    private TDispensadorDeImagenes dispensadorDeImagenes;
    /**
     * @since 1.0
     */    
    private Image buffer;
    /**
     * @since 1.0
     */    
    private BufferedImage imagenbuf;
    /**
     * @since 1.0
     */    
    private Graphics2D g2Dbuf;
    /**
     * @since 1.0
     */    
    private TTopologia topologia;
    /**
     * @since 1.0
     */    
    private Dimension tamPantalla;
    /**
     * @since 1.0
     */    
    private int maxX;
    /**
     * @since 1.0
     */    
    private int maxY;
}
