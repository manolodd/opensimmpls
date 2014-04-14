//**************************************************************************
// Nombre......: JVentanaAdvertencia.java
// Proyecto....: Open SimMPLS
// Descripción.: Implementación de una ventana de diálogo que muestra un 
// ............: texo y un icono de advertencia.
// Fecha.......: 26/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.interfaz.dialogos;

import simMPLS.interfaz.utiles.*;

/** Esta clase implementa una ventana que muestra una pregunta al usuario y espera
 * una respuesta de stipo SI/NO (true/false). Es polivalente.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JVentanaAdvertencia extends javax.swing.JDialog {
    
    /**
     * Crea una nueva ventana de Advertencia.
     * @param parent Ventana padre dentro de la cual se mostrará esta ventana de tipo
     * JVentanaAdvertencia.
     * @param modal TRUE indica que la ventana impedirá que se seleccione nada del resto de la
     * interfaz hasta que sea cerrada. FALSe indica que esto no es asi.
     * @param di Disepnsador de imágenes global de la aplicación.
     * @since 1.0
     */
    public JVentanaAdvertencia(java.awt.Frame parent, boolean modal, TDispensadorDeImagenes di) {
        super(parent, modal);
        dispensadorDeImagenes = di;
        initComponents();
        java.awt.Dimension tamFrame=this.getSize();
        java.awt.Dimension tamPantalla=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((tamPantalla.width-tamFrame.width)/2, (tamPantalla.height-tamFrame.height)/2);
    }
    
    /** Este método se llama desde el constructor para dar los valores iniciales válidos
     * a los atributos de la clase.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setTitle(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaAdvertencia.titulo"));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jLabel1.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ADVERTENCIA));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, -1, -1));

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton1.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ACEPTAR));
        jButton1.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaAdvertencia.ResaltadoBoton").charAt(0));
        jButton1.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("OK"));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnBoton(evt);
            }
        });

        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 80, 105, -1));

        jTextPane1.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Button.background"));
        jTextPane1.setEditable(false);
        jTextPane1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextPane1.setFocusCycleRoot(false);
        jTextPane1.setFocusable(false);
        jTextPane1.setEnabled(false);
        getContentPane().add(jTextPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 15, 285, 50));

        jPanel1.setLayout(null);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 120));

        pack();
    }//GEN-END:initComponents

    /** Este método captura un evento de ratón sobre el boton de la ventana. Al
     * recibirlo, cierra la ventana y la elimina.
     */    
    private void clicEnBoton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnBoton
        setVisible(false);
        dispose();
    }//GEN-LAST:event_clicEnBoton
    
    /** Este método captura un evento de ratón sobre el boton [X] de la ventana. Al
     * recibirlo, cierra la ventana y la elimina.
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /** Este método recibe por parámetro un texto y hace que aparezca en la ventana.
     * @param texto El texto que se desea mostrar en la ventana.
     * @since 1.0
     */    
    public void mostrarMensaje(java.lang.String texto) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        jTextPane1.setText(texto);
    }    

    /** Este objeto es una referencia la dispensador de imágenes global del simulador.
     * Se encarga de tener precargadas las imágenes, acelerando el proceso de
     * mostrarlas.
     * @since 1.0
     */    
    private TDispensadorDeImagenes dispensadorDeImagenes;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
    
}
