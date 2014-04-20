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

/**
 * Esta clase implementa una ventana que muestra un mensaje de advertencia al
 * usuario y que muestra además un icono de advertencia.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JVentanaBooleana extends javax.swing.JDialog {
    
    /**
     * Crea una nueva ventana de Advertencia.
     * @param parent Ventana padre dentro de la cual se mostrará esta ventana de tipo JVentanaBooleana
     * @param modal TRUE indica que la ventana impedirá que se pueda seleccionar nada del resto de
     * la interfaz hasta que sea cerrada. FALSE indica que esto no es así.
     * @param di Dispensador de imágenes global de la aplicación.
     * @since 1.0
     */
    public JVentanaBooleana(java.awt.Frame parent, boolean modal, TDispensadorDeImagenes di) {
        super(parent, modal);
        dispensadorDeImagenes = di;
        initComponents();
        java.awt.Dimension tamFrame=this.getSize();
        java.awt.Dimension tamPantalla=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((tamPantalla.width-tamFrame.width)/2, (tamPantalla.height-tamFrame.height)/2);
        respuesta = false;
    }
    
    /** Este método se llama desde el constructor para dar los valores iniciales válidos
     * a los atributos de la clase.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaAdvertencia.titulo"));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jLabel1.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.INTERROGACION));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, -1, -1));

        jButton1.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton1.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ACEPTAR));
        jButton1.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaBooleana.mnemonico.si").charAt(0));
        jButton1.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaBooleana.Si"));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnBotonSi(evt);
            }
        });

        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 105, -1));

        jButton2.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton2.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.CANCELAR));
        jButton2.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaBooleana.mnemonico.no").charAt(0));
        jButton2.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaBooleana.No"));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnBotonNo(evt);
            }
        });

        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, 105, -1));

        jTextPane1.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
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

    private void clicEnBotonNo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnBotonNo
        setVisible(false);
        respuesta = false;
    }//GEN-LAST:event_clicEnBotonNo

    /** Este método captura un evento de ratón sobre el boton de la ventana. Al
     * recibirlo, cierra la ventana y la elimina.
     */    
    private void clicEnBotonSi(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnBotonSi
        setVisible(false);
        respuesta = true;
    }//GEN-LAST:event_clicEnBotonSi
    
    /**
     * Este método obtiene la respuesta elegida por el usuario. La ventana booleana
     * siempre presenta una pregunta de tipo SI/NO que el usuario debe elegir.
     * @since 1.0
     * @return TRUE, si el usuario respondió afirmativamente a la pregunta. FALSE en caso
     * contrario.
     */    
    public boolean obtenerRespuesta() {
        return respuesta;
    }
    
    /** Este método captura un evento de ratón sobre el boton [X] de la ventana. Al
     * recibirlo, cierra la ventana y la elimina.
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
//        setVisible(false);
//        dispose();
    }//GEN-LAST:event_closeDialog
    
    /** Este método recibe por parámetro un texto y hace que aparezca en la ventana.
     * @param texto El texto que se desea mostrar en la ventana.
     * @since 1.0
     */    
    public void mostrarPregunta(java.lang.String texto) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        jTextPane1.setText(texto);
    }    

    private boolean respuesta;
    private TDispensadorDeImagenes dispensadorDeImagenes;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
    
}
