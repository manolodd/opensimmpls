//**************************************************************************
// Nombre......: JSobre.java
// Proyecto....: Open SimMPLS
// Descripción.: Implementación de una ventana de diálogo que muestra la
// ............: información referente al programa: autor, director...
// Fecha.......: 19/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.interfaz.dialogos;

import simMPLS.interfaz.utiles.*;

/** Implementa una ventana con la imagen del programa de fondo.
 * Se usa para mostrar información sobre el programa cuando sea
 * solicitada.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JSobre extends javax.swing.JDialog {

    /**
     * Este método crea una nueva instancia de JSobre
     * @since 1.0
     * @param parent Ventana padre dentro de la cual se mostrará esta ventana de tipo JSobre.
     * @param modal TRUE indica que la ventana impide que se seleccione nada del resto de la
     * interfaz hasta que sea cerrada. FALSE indica que esto no es así.
     * @param di Dispensador de imágenes global de la aplicación.
     */    
    public JSobre(java.awt.Frame parent, boolean modal, TDispensadorDeImagenes di) {
        super(parent, modal);
        dispensadorDeImagenes = di;
        initComponents();
    }

    /** Este método es llamado desde el constructor para inicializar el cuadro de
     * diálogo con los valores correctos.
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
        Imagen.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.SPLASH));
        getContentPane().add(Imagen, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 300));

        pack();

        java.awt.Dimension tamFrame=this.getSize();
        java.awt.Dimension tamPantalla=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((tamPantalla.width-tamFrame.width)/2, (tamPantalla.height-tamFrame.height)/2);

    }//GEN-END:initComponents

    /** Cierra el cuadro de diálogo cuando hace un clic de ratón sobre él. */
    private void cerrarVentana(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cerrarVentana
        setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cerrarVentana


    private TDispensadorDeImagenes dispensadorDeImagenes;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Imagen;
    // End of variables declaration//GEN-END:variables

}
