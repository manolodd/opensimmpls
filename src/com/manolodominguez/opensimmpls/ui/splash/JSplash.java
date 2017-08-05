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
package com.manolodominguez.opensimmpls.ui.splash;

/** Esta clase implementa una ventana con la imagen de Open SimMPLS de fondo donde
 * se puede mostrar texto arbitrario.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JSplash extends javax.swing.JFrame {

    /** Este m�todo es el constructor de la clase; crea una nueva instancia de JSplash.
     * @since 2.0
     */
    public JSplash() {
        initComponents();
        setIconImage(new javax.swing.ImageIcon(this.getClass().getResource(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("/imagenes/splash_menu.png"))).getImage());
    }

    /** Este m�todo se llama desde el constructor para dar los valores por defecto a los
     * atributos de la ventana.
     * @since 2.0
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        Texto = new javax.swing.JLabel();
        Imagen = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        setName(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("JSplash"));
        setResizable(false);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        Texto.setFont(new java.awt.Font(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("Arial"), 0, 12));
        Texto.setText(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("Starting..."));
        getContentPane().add(Texto, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 16, 260, 20));

        Imagen.setIcon(new javax.swing.ImageIcon(getClass().getResource(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("/imagenes/splash_inicio.png"))));
        getContentPane().add(Imagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 300));

        pack();

        java.awt.Dimension tamFrame=this.getSize();
        java.awt.Dimension tamPantalla=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((tamPantalla.width-tamFrame.width)/2, (tamPantalla.height-tamFrame.height)/2);

    }//GEN-END:initComponents
    /** Este m�todo cierra la ventana.
     * @param evt Evento indicando que se debe cerrar la ventana.
     * @since 2.0
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        this.dispose();
    }//GEN-LAST:event_exitForm

    /** Este m�todo muestra en la ventana de inicio el texto que se especifique.
     * @param txt Texto que se mostrar� en el recuadro blanco de la ventana.
     * @since 2.0
     */
    public void ponerTexto(String txt) {
        Texto.setText(txt);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Imagen;
    private javax.swing.JLabel Texto;
    // End of variables declaration//GEN-END:variables
}
