//**************************************************************************
// Nombre......: JSimulador.java
// Proyecto....: Open SimMPLS
// Descripción.: Implementación de la interfaz gráfica del simulador Open
// ............: SimMPLS. A través de esta interfaz tiene funcionamiento
// ............: todo el programa.
// Fecha.......: 19/01/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.interfaz.simulador;

import simMPLS.utiles.*;
import simMPLS.entradasalida.osm.*;
import simMPLS.interfaz.splash.*;
import simMPLS.interfaz.dialogos.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import simMPLS.interfaz.utiles.*;
import simMPLS.escenario.*;

/**
 * Esta clase implementa un simulador completo para simular, valga la redundancia,
 * escenarios MPLS y escenarios MPLS con Garantía de Servicio.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 * @since 1.0
 */
public class JSimulador extends javax.swing.JFrame {
    
    /**
     * Este método crea una nueva instancia de la clase, esto es un nuevo simulador.
     * @since 1.0
     * @param di El dispensador de imágenes que se encargará de precargar las imágenes necesarias
     * en el simulador, ahorrando tiempo y mejorando el rendimiento de la aplicación.
     */
    public JSimulador(TDispensadorDeImagenes di) {
        dispensadorDeImagenes = di;
        initComponents();
        initComponents2();
    }
    
    /**
     * Este método inicia los componentes que no han sido suficientemente definidos por
     * el constructor.
     * @since 1.0
     */    
    public void initComponents2() {
        GeneradorId = new TIdentificador();
        numVentanasAbiertas = 0;
    }
    
    /** Este método se llama desde el constructor de la clase y lo que hace es dar unos
     * valores iniciales a los atributos.
     * @since 1.0
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        Escritorio = new javax.swing.JDesktopPane();
        menu = new javax.swing.JMenuBar();
        submenuEscenario = new javax.swing.JMenu();
        nuevoMenuItem = new javax.swing.JMenuItem();
        abrirMenuItem = new javax.swing.JMenuItem();
        cerrarMenuItem = new javax.swing.JMenuItem();
        guardarMenuItem = new javax.swing.JMenuItem();
        guardarComoMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        salirMenuItem = new javax.swing.JMenuItem();
        submenuVista = new javax.swing.JMenu();
        mosaicoHorizontalMenuItem = new javax.swing.JMenuItem();
        mosaicoVerticalMenuItem = new javax.swing.JMenuItem();
        cascadaMenuItem = new javax.swing.JMenuItem();
        iconosMenuItem = new javax.swing.JMenuItem();
        submenuAyuda = new javax.swing.JMenu();
        tutorialMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        licenciaMenuItem = new javax.swing.JMenuItem();
        comentarioMenuItem1 = new javax.swing.JMenuItem();
        sobreMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Open_SimMPLS"));
        setIconImage(dispensadorDeImagenes.obtenerImagen(TDispensadorDeImagenes.SPLASH_MENU));
        setName(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("simulator"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        getContentPane().add(Escritorio, java.awt.BorderLayout.CENTER);

        menu.setDoubleBuffered(true);
        submenuEscenario.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("LetraEscenario").charAt(0));
        submenuEscenario.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Scene"));
        submenuEscenario.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Scene"));
        submenuEscenario.setActionCommand(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("File"));
        submenuEscenario.setDoubleBuffered(true);
        submenuEscenario.setFont(new java.awt.Font("Dialog", 0, 12));
        nuevoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        nuevoMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        nuevoMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.NUEVO_MENU));
        nuevoMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Nuevo").charAt(0));
        nuevoMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("New"));
        nuevoMenuItem.setDoubleBuffered(true);
        nuevoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnNuevo(evt);
            }
        });

        submenuEscenario.add(nuevoMenuItem);

        abrirMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        abrirMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ABRIR_MENU));
        abrirMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Abrir").charAt(0));
        abrirMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Open"));
        abrirMenuItem.setDoubleBuffered(true);
        abrirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnAbrir(evt);
            }
        });

        submenuEscenario.add(abrirMenuItem);

        cerrarMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        cerrarMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        cerrarMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.CERRAR_MENU));
        cerrarMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Cerrar").charAt(0));
        cerrarMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Close"));
        cerrarMenuItem.setDoubleBuffered(true);
        cerrarMenuItem.setEnabled(false);
        cerrarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnCerrar(evt);
            }
        });

        submenuEscenario.add(cerrarMenuItem);

        guardarMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        guardarMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.GUARDAR_MENU));
        guardarMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Guardar").charAt(0));
        guardarMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Save"));
        guardarMenuItem.setDoubleBuffered(true);
        guardarMenuItem.setEnabled(false);
        guardarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnGuardar(evt);
            }
        });

        submenuEscenario.add(guardarMenuItem);

        guardarComoMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        guardarComoMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.GUARDAR_COMO_MENU));
        guardarComoMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Guardar_como").charAt(0));
        guardarComoMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Save_as..."));
        guardarComoMenuItem.setDoubleBuffered(true);
        guardarComoMenuItem.setEnabled(false);
        guardarComoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnGuardarComo(evt);
            }
        });

        submenuEscenario.add(guardarComoMenuItem);

        submenuEscenario.add(jSeparator1);

        salirMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        salirMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        salirMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.SALIR_MENU));
        salirMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Salir").charAt(0));
        salirMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Exit"));
        salirMenuItem.setDoubleBuffered(true);
        salirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnSalir(evt);
            }
        });

        submenuEscenario.add(salirMenuItem);

        menu.add(submenuEscenario);

        submenuVista.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("MenuPrincipal.Vista.Resaltado").charAt(0));
        submenuVista.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("MenuPrincipal.Vista"));
        submenuVista.setDoubleBuffered(true);
        submenuVista.setFont(new java.awt.Font("Dialog", 0, 12));
        mosaicoHorizontalMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        mosaicoHorizontalMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.VISTA_HORIZONTAL));
        mosaicoHorizontalMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.MosaicoHorixontal.Resaltado").charAt(0));
        mosaicoHorizontalMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.MosaicoHorizontal"));
        mosaicoHorizontalMenuItem.setDoubleBuffered(true);
        mosaicoHorizontalMenuItem.setEnabled(false);
        mosaicoHorizontalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaMosaicoHorizontal(evt);
            }
        });

        submenuVista.add(mosaicoHorizontalMenuItem);

        mosaicoVerticalMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        mosaicoVerticalMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.VISTA_VERTICAL));
        mosaicoVerticalMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.MosaicoVertical.Resaltado").charAt(0));
        mosaicoVerticalMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.MosaicoVertical"));
        mosaicoVerticalMenuItem.setDoubleBuffered(true);
        mosaicoVerticalMenuItem.setEnabled(false);
        mosaicoVerticalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaMosaicoVertical(evt);
            }
        });

        submenuVista.add(mosaicoVerticalMenuItem);

        cascadaMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        cascadaMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.VISTA_CASCADA));
        cascadaMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.Cascadal.Resaltado").charAt(0));
        cascadaMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.Cascada"));
        cascadaMenuItem.setDoubleBuffered(true);
        cascadaMenuItem.setEnabled(false);
        cascadaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaCascada(evt);
            }
        });

        submenuVista.add(cascadaMenuItem);

        iconosMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        iconosMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.VISTA_ICONOS));
        iconosMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.Iconos.Resaltado").charAt(0));
        iconosMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("SubmenuVista.Iconos"));
        iconosMenuItem.setDoubleBuffered(true);
        iconosMenuItem.setEnabled(false);
        iconosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaIconos(evt);
            }
        });

        submenuVista.add(iconosMenuItem);

        menu.add(submenuVista);

        submenuAyuda.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("LetraAyuda").charAt(0));
        submenuAyuda.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Help"));
        submenuAyuda.setDoubleBuffered(true);
        submenuAyuda.setFont(new java.awt.Font("Dialog", 0, 12));
        tutorialMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        tutorialMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        tutorialMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.TUTORIAL_MENU));
        tutorialMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Contenidos").charAt(0));
        tutorialMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Contents"));
        tutorialMenuItem.setDoubleBuffered(true);
        tutorialMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnTutorial(evt);
            }
        });

        submenuAyuda.add(tutorialMenuItem);

        submenuAyuda.add(jSeparator2);

        licenciaMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        licenciaMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LICENCIA_MENU));
        licenciaMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Licencia").charAt(0));
        licenciaMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("License"));
        licenciaMenuItem.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("License"));
        licenciaMenuItem.setDoubleBuffered(true);
        licenciaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnLicencia(evt);
            }
        });

        submenuAyuda.add(licenciaMenuItem);

        comentarioMenuItem1.setFont(new java.awt.Font("Dialog", 0, 12));
        comentarioMenuItem1.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.COMENTARIO_MENU));
        comentarioMenuItem1.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Comentario").charAt(0));
        comentarioMenuItem1.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Contact_the_authors"));
        comentarioMenuItem1.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("License"));
        comentarioMenuItem1.setDoubleBuffered(true);
        comentarioMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnEnviarComentario(evt);
            }
        });

        submenuAyuda.add(comentarioMenuItem1);

        sobreMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        sobreMenuItem.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.SPLASH_MENU));
        sobreMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("Menu.Letra_resaltada.Sobre").charAt(0));
        sobreMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("About"));
        sobreMenuItem.setDoubleBuffered(true);
        sobreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnSobre(evt);
            }
        });

        submenuAyuda.add(sobreMenuItem);

        menu.add(submenuAyuda);

        setJMenuBar(menu);

        pack();
    }//GEN-END:initComponents

    private void clicEnTutorial(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnTutorial
        JVentanaAyuda va = new JVentanaAyuda(this, true, this.dispensadorDeImagenes);
        va.show();
    }//GEN-LAST:event_clicEnTutorial

    private void clicEnAbrir(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnAbrir
        boolean finAbrir = false;
        JFileChooser dialogoAbrir = new JFileChooser();
        dialogoAbrir.setFileFilter(new JFiltroOSM());
        dialogoAbrir.setDialogType(JFileChooser.CUSTOM_DIALOG);
        dialogoAbrir.setApproveButtonMnemonic('A');
        dialogoAbrir.setApproveButtonText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JSimulador.DialogoAbrir.OK"));
        dialogoAbrir.setDialogTitle(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JSimulador.DialogoAbrir.CargarUnEscenario"));
        dialogoAbrir.setAcceptAllFileFilterUsed(false);
        dialogoAbrir.setFileSelectionMode(JFileChooser.FILES_ONLY);
        while (!finAbrir) {
            int resultado = dialogoAbrir.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                if (dialogoAbrir.getSelectedFile().exists()) {
                    TCargadorOSM cargador = new TCargadorOSM();
                    boolean cargado = cargador.cargar(dialogoAbrir.getSelectedFile());
                    if (cargado) {
                        try {
                            TEscenario esc = new TEscenario();
                            esc = cargador.obtenerEscenario();
                            String tit = esc.obtenerFichero().getName();
                            numVentanasAbiertas++;
                            activarOpciones();
                            JVentanaHija nuevoEscenario = new JVentanaHija(this, dispensadorDeImagenes, tit);
                            nuevoEscenario.ponerEscenario(esc);
                            Escritorio.add(nuevoEscenario, javax.swing.JLayeredPane.FRAME_CONTENT_LAYER);
                            try {
                                nuevoEscenario.setSelected(true);
                            } catch (Exception e) {
                                e.printStackTrace(); 
                            }
                            getContentPane().add(Escritorio, java.awt.BorderLayout.CENTER);
                        }
                        catch (Exception e) {
                            JVentanaError ve;
                            ve = new JVentanaError(this, true, dispensadorDeImagenes);
                            ve.mostrarMensaje(e.toString());
                            ve.show();
                        }
                    } else {
                        JVentanaAdvertencia va;
                        va = new JVentanaAdvertencia(this, true, dispensadorDeImagenes);
                        va.mostrarMensaje(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JSimulador.DialogoAbrir.FicheroCorrupto"));
                        va.show();
                    }
                    dialogoAbrir = null;
                    finAbrir = true;
                } else {
                    JVentanaAdvertencia va;
                    va = new JVentanaAdvertencia(this, true, dispensadorDeImagenes);
                    va.mostrarMensaje(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JSimulador.DialogoAbrir.FicheroDebeExistir"));
                    va.show();
                }
            } else {
                finAbrir = true;
            }
        }
    }//GEN-LAST:event_clicEnAbrir

    private void clicEnGuardarComo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnGuardarComo
        JVentanaHija vActiva= (JVentanaHija) Escritorio.getSelectedFrame();
        if (vActiva != null) {
            vActiva.gestionarGuardarComo();
        }
    }//GEN-LAST:event_clicEnGuardarComo

    private void clicEnGuardar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnGuardar
        JVentanaHija vActiva= (JVentanaHija) Escritorio.getSelectedFrame();
        if (vActiva != null) {
            vActiva.gestionarGuardar();
        }
    }//GEN-LAST:event_clicEnGuardar

    /**
     * @param evt
     * @since 1.0
     */    
private void clicEnEnviarComentario(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnEnviarComentario
    JVentanaComentario vc = new JVentanaComentario(dispensadorDeImagenes, this, true);
    vc.show();
}//GEN-LAST:event_clicEnEnviarComentario

/**
 * @param evt
 * @since 1.0
 */
/**
 * @param evt
 * @since 1.0
 */
private void clicEnVistaMosaicoHorizontal(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnVistaMosaicoHorizontal
    javax.swing.JInternalFrame ventanas[] = Escritorio.getAllFrames();
    Dimension tam = Escritorio.getSize();
    int incremento = tam.width/ventanas.length;
    Point p = new Point(0, 0);
    tam.width = incremento;
    try {
        for (int i=ventanas.length-1; i >= 0; i--) {
            ventanas[i].setVisible(false);
            ventanas[i].setIcon(false);
            ventanas[i].setSize(tam);
            ventanas[i].setLocation(p);
            p.x += incremento;
        }
        for (int j=ventanas.length-1; j >= 0; j--)
            ventanas[j].setVisible(true);
    } catch (Exception e) {
        JVentanaError ve;
        ve = new JVentanaError(this, true, dispensadorDeImagenes);
        ve.mostrarMensaje(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaMosaicoHorizontal

/**
 * @param evt
 * @since 1.0
 */
private void clicEnVistaMosaicoVertical(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnVistaMosaicoVertical
    javax.swing.JInternalFrame ventanas[] = Escritorio.getAllFrames();
    Dimension tam = Escritorio.getSize();
    int incremento = tam.height/ventanas.length;
    Point p = new Point(0, 0);
    tam.height = incremento;
    try {
        for (int i=ventanas.length-1; i >= 0; i--) {
            ventanas[i].setVisible(false);
            ventanas[i].setIcon(false);
            ventanas[i].setSize(tam);
            ventanas[i].setLocation(p);
            p.y += incremento;
        }
        for (int j=ventanas.length-1; j >= 0; j--)
            ventanas[j].setVisible(true);
    } catch (Exception e) {
        JVentanaError ve;
        ve = new JVentanaError(this, true, dispensadorDeImagenes);
        ve.mostrarMensaje(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaMosaicoVertical

/**
 * @param evt
 * @since 1.0
 */
private void clicEnVistaCascada(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnVistaCascada
    int incremento=20;
    Dimension d = Escritorio.getSize();
    d.height = d.height-incremento;
    d.width = d.width-incremento;
    Point p = new Point(incremento, incremento);
    Dimension tam = Escritorio.getSize();
    tam.height = tam.height/2;
    tam.width = tam.width/2;
    javax.swing.JInternalFrame ventanas[] = Escritorio.getAllFrames();
    try {
        for (int i=ventanas.length-1; i >= 0; i--) {
            ventanas[i].setVisible(false);
            ventanas[i].setIcon(false);
            ventanas[i].setSize(tam);
            ventanas[i].setLocation(p);
            p.x += incremento;
            p.y += incremento;
            if ((p.x+tam.width) >= d.width)
                p.x = incremento;
            if ((p.y+tam.height) >= d.height)
                p.y = incremento;
        }
        for (int j=ventanas.length-1; j >= 0; j--)
            ventanas[j].setVisible(true);
    } catch (Exception e) {
        JVentanaError ve;
        ve = new JVentanaError(this, true, dispensadorDeImagenes);
        ve.mostrarMensaje(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaCascada

/**
 * @param evt
 * @since 1.0
 */
private void clicEnVistaIconos(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnVistaIconos
    javax.swing.JInternalFrame ventanas[] = Escritorio.getAllFrames();
    try {
        for (int i=0; i < ventanas.length; i++) {
            ventanas[i].setIcon(true);
        }
        ventanas[0].setSelected(true);
    } catch (Exception e) {
        JVentanaError ve;
        ve = new JVentanaError(this, true, dispensadorDeImagenes);
        ve.mostrarMensaje(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaIconos
    
/**
 * @param evt
 * @since 1.0
 */
    private void clicEnCerrar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnCerrar
        JVentanaHija vActiva= (JVentanaHija) Escritorio.getSelectedFrame();
        if (vActiva != null) {
            vActiva.gestionarGuardarParaCerrar();
            vActiva.cerrar();
            numVentanasAbiertas--;
        }
        if (numVentanasAbiertas==0){
            desactivarOpciones();
        } else {
            if (numVentanasAbiertas == 1)
                desactivarOpcionesVista();
            javax.swing.JInternalFrame ventanas[] = Escritorio.getAllFrames();
            try {
                if (ventanas[0].isIcon()) {
                    ventanas[0].setIcon(false);
                }
                ventanas[0].setSelected(true);
            } catch (Exception e) {
                JVentanaError ve;
                ve = new JVentanaError(this, true, dispensadorDeImagenes);
                ve.mostrarMensaje(e.toString());
                ve.show();
            }
        }
    }//GEN-LAST:event_clicEnCerrar
    
    /**
     * @param evt
     * @since 1.0
     */    
    private void clicEnNuevo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnNuevo
        try {
            int id = GeneradorId.obtenerNuevo();
            String tit = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TextoUntitled-")+id;
            numVentanasAbiertas++;
            activarOpciones();
            JVentanaHija nuevoEscenario = new JVentanaHija(this, dispensadorDeImagenes, tit);
            Escritorio.add(nuevoEscenario, javax.swing.JLayeredPane.FRAME_CONTENT_LAYER);
            try {
                nuevoEscenario.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace(); 
            }
            getContentPane().add(Escritorio, java.awt.BorderLayout.CENTER);
        }
        catch (Exception e) {
            JVentanaError ve;
            ve = new JVentanaError(this, true, dispensadorDeImagenes);
            ve.mostrarMensaje(e.toString());
            ve.show();
        }
    }//GEN-LAST:event_clicEnNuevo
    
    /**
     * @param evt
     * @since 1.0
     */    
    private void clicEnLicencia(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnLicencia
        JLicencia ventanaLicencia;
        ventanaLicencia = new JLicencia(this, true, dispensadorDeImagenes);
        ventanaLicencia.show();
    }//GEN-LAST:event_clicEnLicencia
    
    /**
     * @param evt
     * @since 1.0
     */    
    private void clicEnSobre(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnSobre
        JSobre nuevoSobre;
        nuevoSobre = new JSobre(this, true, dispensadorDeImagenes);
        nuevoSobre.show();
    }//GEN-LAST:event_clicEnSobre
    
    /**
     * Este método cierra todas las ventanas hijas que haya abiertas en ese momento.
     * @since 1.0
     */    
    public void cerrarTodo() {
        while (numVentanasAbiertas > 0) {
            JVentanaHija vActiva= (JVentanaHija) Escritorio.getSelectedFrame();
            if (vActiva != null) {
                vActiva.gestionarGuardarParaCerrar();
                vActiva.cerrar();
                numVentanasAbiertas--;
            }
            if (numVentanasAbiertas > 0) {
                javax.swing.JInternalFrame ventanas[] = Escritorio.getAllFrames();
                if (ventanas.length > 0) {
                    try {
                        if (ventanas[0].isIcon()) {
                            ventanas[0].setIcon(false);
                        }
                        ventanas[0].setSelected(true);
                    } catch (Exception e) {
                        JVentanaError ve;
                        ve = new JVentanaError(this, true, dispensadorDeImagenes);
                        ve.mostrarMensaje(e.toString());
                        ve.show();
                    }
                } else {
                    numVentanasAbiertas = 0;
                }
            }
        }
    }
    
    /** Muestra la ventana Splash mientras que se cargan todos los valores para el
     * correct funcionamiento de la aplicación.
     */
    /** Sale del simulador cuando se elige desde el menú ARCHIVO
     * @param evt
     * @since 1.0
     */
    private void clicEnSalir(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnSalir
        clicEnCualquierSalir();
    }//GEN-LAST:event_clicEnSalir
    
    /**
     * @since 1.0
     */    
    private void clicEnCualquierSalir() {
        JVentanaBooleana vb = new JVentanaBooleana(this, true, dispensadorDeImagenes);
        vb.mostrarPregunta(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JSimulador.PreguntaSalirDelSimulador"));
        vb.show();
        boolean respuesta = vb.obtenerRespuesta();
        vb.dispose();
        if (respuesta) {
            cerrarTodo();
            this.dispose();
        }
    }

    /** Sale del simulador
     * @param evt
     * @since 1.0
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        clicEnCualquierSalir();
    }//GEN-LAST:event_exitForm
    
    /**
     * @since 1.0
     */    
    private void desactivarOpciones() {
        cerrarMenuItem.setEnabled(false);
        guardarComoMenuItem.setEnabled(false);
        guardarMenuItem.setEnabled(false);
        desactivarOpcionesVista();
    }
    
    /**
     * @since 1.0
     */    
    private void activarOpciones() {
        cerrarMenuItem.setEnabled(true);
        guardarComoMenuItem.setEnabled(true);
        guardarMenuItem.setEnabled(true);
        activarOpcionesVista();
    }
    
    /**
     * @since 1.0
     */    
    private void desactivarOpcionesVista() {
        if (numVentanasAbiertas == 0) {
            mosaicoHorizontalMenuItem.setEnabled(false);
            mosaicoVerticalMenuItem.setEnabled(false);
            cascadaMenuItem.setEnabled(false);
            iconosMenuItem.setEnabled(false);
        }
        else if (numVentanasAbiertas == 1) {
            mosaicoHorizontalMenuItem.setEnabled(false);
            mosaicoVerticalMenuItem.setEnabled(false);
            cascadaMenuItem.setEnabled(false);
            iconosMenuItem.setEnabled(true);
        }
    }

    /**
     * @since 1.0
     */    
    private void activarOpcionesVista() {
        if (numVentanasAbiertas == 1) {
            mosaicoHorizontalMenuItem.setEnabled(false);
            mosaicoVerticalMenuItem.setEnabled(false);
            cascadaMenuItem.setEnabled(false);
            iconosMenuItem.setEnabled(true);
        }
        else if (numVentanasAbiertas > 1) {
            mosaicoHorizontalMenuItem.setEnabled(true);
            mosaicoVerticalMenuItem.setEnabled(true);
            cascadaMenuItem.setEnabled(true);
            iconosMenuItem.setEnabled(true);
        }
    }

    /**
     * @since 1.0
     */    
    private TDispensadorDeImagenes dispensadorDeImagenes;
    /**
     * @since 1.0
     */    
    private TIdentificador GeneradorId;
    /**
     * @since 1.0
     */    
    private int numVentanasAbiertas;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane Escritorio;
    private javax.swing.JMenuItem abrirMenuItem;
    private javax.swing.JMenuItem cascadaMenuItem;
    private javax.swing.JMenuItem cerrarMenuItem;
    private javax.swing.JMenuItem comentarioMenuItem1;
    private javax.swing.JMenuItem guardarComoMenuItem;
    private javax.swing.JMenuItem guardarMenuItem;
    private javax.swing.JMenuItem iconosMenuItem;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenuItem licenciaMenuItem;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenuItem mosaicoHorizontalMenuItem;
    private javax.swing.JMenuItem mosaicoVerticalMenuItem;
    private javax.swing.JMenuItem nuevoMenuItem;
    private javax.swing.JMenuItem salirMenuItem;
    private javax.swing.JMenuItem sobreMenuItem;
    private javax.swing.JMenu submenuAyuda;
    private javax.swing.JMenu submenuEscenario;
    private javax.swing.JMenu submenuVista;
    private javax.swing.JMenuItem tutorialMenuItem;
    // End of variables declaration//GEN-END:variables
}
