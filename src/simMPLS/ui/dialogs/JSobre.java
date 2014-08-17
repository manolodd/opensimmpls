/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.ui.dialogs;

import simMPLS.ui.utils.TImagesBroker;

/** Implementa una ventana con la imagen del programa de fondo.
 * Se usa para mostrar informaci�n sobre el programa cuando sea
 * solicitada.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JSobre extends javax.swing.JDialog {

    /**
     * Este m�todo crea una nueva instancia de JSobre
     * @since 1.0
     * @param parent Ventana padre dentro de la cual se mostrar� esta ventana de tipo JSobre.
     * @param modal TRUE indica que la ventana impide que se seleccione nada del resto de la
     * interfaz hasta que sea cerrada. FALSE indica que esto no es as�.
     * @param di Dispensador de im�genes global de la aplicaci�n.
     */    
    public JSobre(java.awt.Frame parent, boolean modal, TImagesBroker di) {
        super(parent, modal);
        dispensadorDeImagenes = di;
        initComponents();
    }

    /** Este m�todo es llamado desde el constructor para inicializar el cuadro de
     * di�logo con los valores correctos.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        Imagen = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setModal(true);
        setResizable(false);
        setUndecorated(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cerrarVentana(evt);
            }
        });

        Imagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Imagen.setIcon(dispensadorDeImagenes.obtenerIcono(TImagesBroker.SPLASH));
        getContentPane().add(Imagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 300));

        pack();

        java.awt.Dimension tamFrame=this.getSize();
        java.awt.Dimension tamPantalla=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((tamPantalla.width-tamFrame.width)/2, (tamPantalla.height-tamFrame.height)/2);

    }//GEN-END:initComponents

    /** Cierra el cuadro de di�logo cuando hace un clic de rat�n sobre �l. */
    private void cerrarVentana(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrarVentana
        setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cerrarVentana


    private TImagesBroker dispensadorDeImagenes;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Imagen;
    // End of variables declaration//GEN-END:variables

}
