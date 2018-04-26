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
import java.util.*;
import javax.swing.*;
import com.manolodominguez.opensimmpls.io.osm.TOSMLoader;
import com.manolodominguez.opensimmpls.scenario.TScenario;
import com.manolodominguez.opensimmpls.ui.dialogs.JLicenseWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JAboutWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JWarningWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JHelpWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JDecissionWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JCommentWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JErrorWindow;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import com.manolodominguez.opensimmpls.ui.utils.JOSMFilter;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;

/**
 * Esta clase implementa un simulador completo para simular, valga la redundancia,
 * escenarios MPLS y escenarios MPLS con Garant�a de Servicio.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 * @since 2.0
 */
public class JSimulador extends javax.swing.JFrame {
    
    /**
     * Este m�todo crea una nueva instancia de la clase, esto es un nuevo simulador.
     * @since 2.0
     * @param di El dispensador de im�genes que se encargar� de precargar las im�genes necesarias
     * en el simulador, ahorrando tiempo y mejorando el rendimiento de la aplicaci�n.
     */
    public JSimulador(TImageBroker di) {
        dispensadorDeImagenes = di;
        initComponents();
        initComponents2();
    }
    
    /**
     * Este m�todo inicia los componentes que no han sido suficientemente definidos por
     * el constructor.
     * @since 2.0
     */    
    public void initComponents2() {
        GeneradorId = new TIDGenerator();
        numVentanasAbiertas = 0;
    }
    
    /** Este m�todo se llama desde el constructor de la clase y lo que hace es dar unos
     * valores iniciales a los atributos.
     * @since 2.0
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations"); // NOI18N
        setTitle(bundle.getString("Open_SimMPLS")); // NOI18N
        setIconImage(dispensadorDeImagenes.obtenerImagen(TImageBroker.SPLASH_MENU));
        setName(bundle.getString("simulator")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        getContentPane().add(Escritorio, java.awt.BorderLayout.CENTER);

        menu.setDoubleBuffered(true);

        submenuEscenario.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("LetraEscenario").charAt(0));
        submenuEscenario.setText(bundle.getString("Scene")); // NOI18N
        submenuEscenario.setToolTipText(bundle.getString("Scene")); // NOI18N
        submenuEscenario.setActionCommand(bundle.getString("File")); // NOI18N
        submenuEscenario.setDoubleBuffered(true);
        submenuEscenario.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        nuevoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        nuevoMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        nuevoMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.NUEVO_MENU));
        nuevoMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Nuevo").charAt(0));
        nuevoMenuItem.setText(bundle.getString("New")); // NOI18N
        nuevoMenuItem.setDoubleBuffered(true);
        nuevoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnNuevo(evt);
            }
        });
        submenuEscenario.add(nuevoMenuItem);

        abrirMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        abrirMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ABRIR_MENU));
        abrirMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Abrir").charAt(0));
        abrirMenuItem.setText(bundle.getString("Open")); // NOI18N
        abrirMenuItem.setDoubleBuffered(true);
        abrirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnAbrir(evt);
            }
        });
        submenuEscenario.add(abrirMenuItem);

        cerrarMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        cerrarMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cerrarMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.CERRAR_MENU));
        cerrarMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Cerrar").charAt(0));
        cerrarMenuItem.setText(bundle.getString("Close")); // NOI18N
        cerrarMenuItem.setDoubleBuffered(true);
        cerrarMenuItem.setEnabled(false);
        cerrarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnCerrar(evt);
            }
        });
        submenuEscenario.add(cerrarMenuItem);

        guardarMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        guardarMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.GUARDAR_MENU));
        guardarMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Guardar").charAt(0));
        guardarMenuItem.setText(bundle.getString("Save")); // NOI18N
        guardarMenuItem.setDoubleBuffered(true);
        guardarMenuItem.setEnabled(false);
        guardarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnGuardar(evt);
            }
        });
        submenuEscenario.add(guardarMenuItem);

        guardarComoMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        guardarComoMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.GUARDAR_COMO_MENU));
        guardarComoMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Guardar_como").charAt(0));
        guardarComoMenuItem.setText(bundle.getString("Save_as...")); // NOI18N
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
        salirMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        salirMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.SALIR_MENU));
        salirMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Salir").charAt(0));
        salirMenuItem.setText(bundle.getString("Exit")); // NOI18N
        salirMenuItem.setDoubleBuffered(true);
        salirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnSalir(evt);
            }
        });
        submenuEscenario.add(salirMenuItem);

        menu.add(submenuEscenario);

        submenuVista.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("MenuPrincipal.Vista.Resaltado").charAt(0));
        submenuVista.setText(bundle.getString("MenuPrincipal.Vista")); // NOI18N
        submenuVista.setDoubleBuffered(true);
        submenuVista.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        mosaicoHorizontalMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mosaicoHorizontalMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.VISTA_HORIZONTAL));
        mosaicoHorizontalMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("SubmenuVista.MosaicoHorixontal.Resaltado").charAt(0));
        mosaicoHorizontalMenuItem.setText(bundle.getString("SubmenuVista.MosaicoHorizontal")); // NOI18N
        mosaicoHorizontalMenuItem.setDoubleBuffered(true);
        mosaicoHorizontalMenuItem.setEnabled(false);
        mosaicoHorizontalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaMosaicoHorizontal(evt);
            }
        });
        submenuVista.add(mosaicoHorizontalMenuItem);

        mosaicoVerticalMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        mosaicoVerticalMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.VISTA_VERTICAL));
        mosaicoVerticalMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("SubmenuVista.MosaicoVertical.Resaltado").charAt(0));
        mosaicoVerticalMenuItem.setText(bundle.getString("SubmenuVista.MosaicoVertical")); // NOI18N
        mosaicoVerticalMenuItem.setDoubleBuffered(true);
        mosaicoVerticalMenuItem.setEnabled(false);
        mosaicoVerticalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaMosaicoVertical(evt);
            }
        });
        submenuVista.add(mosaicoVerticalMenuItem);

        cascadaMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cascadaMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.VISTA_CASCADA));
        cascadaMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("SubmenuVista.Cascadal.Resaltado").charAt(0));
        cascadaMenuItem.setText(bundle.getString("SubmenuVista.Cascada")); // NOI18N
        cascadaMenuItem.setDoubleBuffered(true);
        cascadaMenuItem.setEnabled(false);
        cascadaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaCascada(evt);
            }
        });
        submenuVista.add(cascadaMenuItem);

        iconosMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        iconosMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.VISTA_ICONOS));
        iconosMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("SubmenuVista.Iconos.Resaltado").charAt(0));
        iconosMenuItem.setText(bundle.getString("SubmenuVista.Iconos")); // NOI18N
        iconosMenuItem.setDoubleBuffered(true);
        iconosMenuItem.setEnabled(false);
        iconosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnVistaIconos(evt);
            }
        });
        submenuVista.add(iconosMenuItem);

        menu.add(submenuVista);

        submenuAyuda.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("LetraAyuda").charAt(0));
        submenuAyuda.setText(bundle.getString("Help")); // NOI18N
        submenuAyuda.setDoubleBuffered(true);
        submenuAyuda.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        tutorialMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        tutorialMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tutorialMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.TUTORIAL_MENU));
        tutorialMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Contenidos").charAt(0));
        tutorialMenuItem.setText(bundle.getString("Contents")); // NOI18N
        tutorialMenuItem.setDoubleBuffered(true);
        tutorialMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnTutorial(evt);
            }
        });
        submenuAyuda.add(tutorialMenuItem);
        submenuAyuda.add(jSeparator2);

        licenciaMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        licenciaMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.LICENCIA_MENU));
        licenciaMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Licencia").charAt(0));
        licenciaMenuItem.setText(bundle.getString("License")); // NOI18N
        licenciaMenuItem.setToolTipText(bundle.getString("License")); // NOI18N
        licenciaMenuItem.setDoubleBuffered(true);
        licenciaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnLicencia(evt);
            }
        });
        submenuAyuda.add(licenciaMenuItem);

        comentarioMenuItem1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        comentarioMenuItem1.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.COMENTARIO_MENU));
        comentarioMenuItem1.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Comentario").charAt(0));
        comentarioMenuItem1.setText(bundle.getString("Contact_the_authors")); // NOI18N
        comentarioMenuItem1.setToolTipText(bundle.getString("License")); // NOI18N
        comentarioMenuItem1.setDoubleBuffered(true);
        comentarioMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnEnviarComentario(evt);
            }
        });
        submenuAyuda.add(comentarioMenuItem1);

        sobreMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        sobreMenuItem.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.SPLASH_MENU));
        sobreMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Menu.Letra_resaltada.Sobre").charAt(0));
        sobreMenuItem.setText(bundle.getString("About")); // NOI18N
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
    }// </editor-fold>//GEN-END:initComponents

    private void clicEnTutorial(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnTutorial
        JHelpWindow va = new JHelpWindow(this, true, this.dispensadorDeImagenes);
        va.show();
    }//GEN-LAST:event_clicEnTutorial

    private void clicEnAbrir(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnAbrir
        boolean finAbrir = false;
        JFileChooser dialogoAbrir = new JFileChooser();
        dialogoAbrir.setFileFilter(new JOSMFilter());
        dialogoAbrir.setDialogType(JFileChooser.CUSTOM_DIALOG);
        dialogoAbrir.setApproveButtonMnemonic('A');
        dialogoAbrir.setApproveButtonText(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JSimulador.DialogoAbrir.OK"));
        dialogoAbrir.setDialogTitle(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JSimulador.DialogoAbrir.CargarUnEscenario"));
        dialogoAbrir.setAcceptAllFileFilterUsed(false);
        dialogoAbrir.setFileSelectionMode(JFileChooser.FILES_ONLY);
        while (!finAbrir) {
            int resultado = dialogoAbrir.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                if (dialogoAbrir.getSelectedFile().exists()) {
                    TOSMLoader cargador = new TOSMLoader();
                    boolean cargado = cargador.cargar(dialogoAbrir.getSelectedFile());
                    if (cargado) {
                        try {
                            TScenario esc = new TScenario();
                            esc = cargador.getScenario();
                            String tit = esc.getScenarioFile().getName();
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
                            JErrorWindow ve;
                            ve = new JErrorWindow(this, true, dispensadorDeImagenes);
                            ve.setErrorMessage(e.toString());
                            ve.show();
                        }
                    } else {
                        JWarningWindow va;
                        va = new JWarningWindow(this, true, dispensadorDeImagenes);
                        va.mostrarMensaje(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JSimulador.DialogoAbrir.FicheroCorrupto"));
                        va.show();
                    }
                    dialogoAbrir = null;
                    finAbrir = true;
                } else {
                    JWarningWindow va;
                    va = new JWarningWindow(this, true, dispensadorDeImagenes);
                    va.mostrarMensaje(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JSimulador.DialogoAbrir.FicheroDebeExistir"));
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
     * @since 2.0
     */    
private void clicEnEnviarComentario(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnEnviarComentario
    JCommentWindow vc = new JCommentWindow(dispensadorDeImagenes, this, true);
    vc.show();
}//GEN-LAST:event_clicEnEnviarComentario

/**
 * @param evt
 * @since 2.0
 */
/**
 * @param evt
 * @since 2.0
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
        JErrorWindow ve;
        ve = new JErrorWindow(this, true, dispensadorDeImagenes);
        ve.setErrorMessage(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaMosaicoHorizontal

/**
 * @param evt
 * @since 2.0
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
        JErrorWindow ve;
        ve = new JErrorWindow(this, true, dispensadorDeImagenes);
        ve.setErrorMessage(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaMosaicoVertical

/**
 * @param evt
 * @since 2.0
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
        JErrorWindow ve;
        ve = new JErrorWindow(this, true, dispensadorDeImagenes);
        ve.setErrorMessage(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaCascada

/**
 * @param evt
 * @since 2.0
 */
private void clicEnVistaIconos(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnVistaIconos
    javax.swing.JInternalFrame ventanas[] = Escritorio.getAllFrames();
    try {
        for (int i=0; i < ventanas.length; i++) {
            ventanas[i].setIcon(true);
        }
        ventanas[0].setSelected(true);
    } catch (Exception e) {
        JErrorWindow ve;
        ve = new JErrorWindow(this, true, dispensadorDeImagenes);
        ve.setErrorMessage(e.toString());
        ve.show();
    }
}//GEN-LAST:event_clicEnVistaIconos
    
/**
 * @param evt
 * @since 2.0
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
                JErrorWindow ve;
                ve = new JErrorWindow(this, true, dispensadorDeImagenes);
                ve.setErrorMessage(e.toString());
                ve.show();
            }
        }
    }//GEN-LAST:event_clicEnCerrar
    
    /**
     * @param evt
     * @since 2.0
     */    
    private void clicEnNuevo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnNuevo
        try {
            int id = GeneradorId.getNew();
            String tit = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TextoUntitled-")+id;
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
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, dispensadorDeImagenes);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }//GEN-LAST:event_clicEnNuevo
    
    /**
     * @param evt
     * @since 2.0
     */    
    private void clicEnLicencia(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnLicencia
        JLicenseWindow ventanaLicencia;
        ventanaLicencia = new JLicenseWindow(this, true, dispensadorDeImagenes);
        ventanaLicencia.show();
    }//GEN-LAST:event_clicEnLicencia
    
    /**
     * @param evt
     * @since 2.0
     */    
    private void clicEnSobre(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnSobre
        JAboutWindow nuevoSobre;
        nuevoSobre = new JAboutWindow(this, true, dispensadorDeImagenes);
        nuevoSobre.show();
    }//GEN-LAST:event_clicEnSobre
    
    /**
     * Este m�todo cierra todas las ventanas hijas que haya abiertas en ese momento.
     * @since 2.0
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
                        JErrorWindow ve;
                        ve = new JErrorWindow(this, true, dispensadorDeImagenes);
                        ve.setErrorMessage(e.toString());
                        ve.show();
                    }
                } else {
                    numVentanasAbiertas = 0;
                }
            }
        }
    }
    
    /** Muestra la ventana Splash mientras que se cargan todos los valores para el
     * correct funcionamiento de la aplicaci�n.
     */
    /** Sale del simulador cuando se elige desde el men� ARCHIVO
     * @param evt
     * @since 2.0
     */
    private void clicEnSalir(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnSalir
        clicEnCualquierSalir();
    }//GEN-LAST:event_clicEnSalir
    
    /**
     * @since 2.0
     */    
    private void clicEnCualquierSalir() {
        JDecissionWindow vb = new JDecissionWindow(this, true, dispensadorDeImagenes);
        vb.setQuestion(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JSimulador.PreguntaSalirDelSimulador"));
        vb.show();
        boolean respuesta = vb.getUserAnswer();
        vb.dispose();
        if (respuesta) {
            cerrarTodo();
            this.dispose();
        }
    }

    /** Sale del simulador
     * @param evt
     * @since 2.0
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        clicEnCualquierSalir();
    }//GEN-LAST:event_exitForm
    
    /**
     * @since 2.0
     */    
    private void desactivarOpciones() {
        cerrarMenuItem.setEnabled(false);
        guardarComoMenuItem.setEnabled(false);
        guardarMenuItem.setEnabled(false);
        desactivarOpcionesVista();
    }
    
    /**
     * @since 2.0
     */    
    private void activarOpciones() {
        cerrarMenuItem.setEnabled(true);
        guardarComoMenuItem.setEnabled(true);
        guardarMenuItem.setEnabled(true);
        activarOpcionesVista();
    }
    
    /**
     * @since 2.0
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
     * @since 2.0
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
     * @since 2.0
     */    
    private TImageBroker dispensadorDeImagenes;
    /**
     * @since 2.0
     */    
    private TIDGenerator GeneradorId;
    /**
     * @since 2.0
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
