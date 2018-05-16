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

import com.manolodominguez.opensimmpls.io.osm.TOSMLoader;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.scenario.TScenario;
import com.manolodominguez.opensimmpls.ui.dialogs.JLicenseWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JAboutWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JWarningWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JDecissionWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JErrorWindow;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import com.manolodominguez.opensimmpls.ui.utils.JOSMFilter;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * Esta clase implementa un simulador completo para simular, valga la
 * redundancia, escenarios MPLS y escenarios MPLS con Garant�a de Servicio.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 * @since 2.0
 */
public class JOpenSimMPLS extends JFrame {

    /**
     * Este m�todo crea una nueva instancia de la clase, esto es un nuevo
     * simulador.
     *
     * @since 2.0
     * @param di El dispensador de im�genes que se encargar� de precargar las
     * im�genes necesarias en el simulador, ahorrando tiempo y mejorando el
     * rendimiento de la aplicaci�n.
     */
    public JOpenSimMPLS(TImageBroker di) {
        this.dispensadorDeImagenes = di;
        initComponents();
        initComponents2();
    }

    /**
     * Este m�todo se llama desde el constructor de la clase y lo que hace es
     * dar unos valores iniciales a los atributos.
     *
     * @since 2.0
     */
    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.OPENSIMMPLS_WINDOW.getPath());
        this.Escritorio = new JDesktopPane();
        this.menu = new JMenuBar();
        this.submenuEscenario = new JMenu();
        this.nuevoMenuItem = new JMenuItem();
        this.abrirMenuItem = new JMenuItem();
        this.cerrarMenuItem = new JMenuItem();
        this.guardarMenuItem = new JMenuItem();
        this.guardarComoMenuItem = new JMenuItem();
        this.jSeparator1 = new JSeparator();
        this.salirMenuItem = new JMenuItem();
        this.submenuVista = new JMenu();
        this.mosaicoHorizontalMenuItem = new JMenuItem();
        this.mosaicoVerticalMenuItem = new JMenuItem();
        this.cascadaMenuItem = new JMenuItem();
        this.iconosMenuItem = new JMenuItem();
        this.submenuAyuda = new JMenu();
        this.tutorialMenuItem = new JMenuItem();
        this.jSeparator2 = new JSeparator();
        this.licenciaMenuItem = new JMenuItem();
        this.comentarioMenuItem1 = new JMenuItem();
        this.contribuyeMenuItem1 = new JMenuItem();
        this.sobreMenuItem = new JMenuItem();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(this.translations.getString("Open_SimMPLS"));
        setIconImage(this.dispensadorDeImagenes.obtenerImagen(TImageBroker.SPLASH_MENU));
        setName(this.translations.getString("simulator"));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });
        getContentPane().add(this.Escritorio, BorderLayout.CENTER);
        this.menu.setDoubleBuffered(true);
        this.submenuEscenario.setMnemonic(this.translations.getString("LetraEscenario").charAt(0));
        this.submenuEscenario.setText(this.translations.getString("Scene"));
        this.submenuEscenario.setToolTipText(this.translations.getString("Scene"));
        this.submenuEscenario.setActionCommand(this.translations.getString("File"));
        this.submenuEscenario.setDoubleBuffered(true);
        this.submenuEscenario.setFont(new Font("Dialog", 0, 12));
        this.nuevoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        this.nuevoMenuItem.setFont(new Font("Dialog", 0, 12));
        this.nuevoMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.NUEVO_MENU));
        this.nuevoMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Nuevo").charAt(0));
        this.nuevoMenuItem.setText(this.translations.getString("New"));
        this.nuevoMenuItem.setDoubleBuffered(true);
        this.nuevoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnNuevo(evt);
            }
        });
        this.submenuEscenario.add(this.nuevoMenuItem);
        this.abrirMenuItem.setFont(new Font("Dialog", 0, 12));
        this.abrirMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.ABRIR_MENU));
        this.abrirMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Abrir").charAt(0));
        this.abrirMenuItem.setText(this.translations.getString("Open"));
        this.abrirMenuItem.setDoubleBuffered(true);
        this.abrirMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnAbrir(evt);
            }
        });
        this.submenuEscenario.add(this.abrirMenuItem);
        this.cerrarMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        this.cerrarMenuItem.setFont(new Font("Dialog", 0, 12));
        this.cerrarMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.CERRAR_MENU));
        this.cerrarMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Cerrar").charAt(0));
        this.cerrarMenuItem.setText(this.translations.getString("Close"));
        this.cerrarMenuItem.setDoubleBuffered(true);
        this.cerrarMenuItem.setEnabled(false);
        this.cerrarMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnCerrar(evt);
            }
        });
        this.submenuEscenario.add(this.cerrarMenuItem);
        this.guardarMenuItem.setFont(new Font("Dialog", 0, 12));
        this.guardarMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.GUARDAR_MENU));
        this.guardarMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Guardar").charAt(0));
        this.guardarMenuItem.setText(this.translations.getString("Save"));
        this.guardarMenuItem.setDoubleBuffered(true);
        this.guardarMenuItem.setEnabled(false);
        this.guardarMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnGuardar(evt);
            }
        });
        this.submenuEscenario.add(guardarMenuItem);
        this.guardarComoMenuItem.setFont(new Font("Dialog", 0, 12));
        this.guardarComoMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.GUARDAR_COMO_MENU));
        this.guardarComoMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Guardar_como").charAt(0));
        this.guardarComoMenuItem.setText(this.translations.getString("Save_as..."));
        this.guardarComoMenuItem.setDoubleBuffered(true);
        this.guardarComoMenuItem.setEnabled(false);
        this.guardarComoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnGuardarComo(evt);
            }
        });
        this.submenuEscenario.add(this.guardarComoMenuItem);
        this.submenuEscenario.add(this.jSeparator1);
        this.salirMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        this.salirMenuItem.setFont(new Font("Dialog", 0, 12));
        this.salirMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.SALIR_MENU));
        this.salirMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Salir").charAt(0));
        this.salirMenuItem.setText(this.translations.getString("Exit"));
        this.salirMenuItem.setDoubleBuffered(true);
        this.salirMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnSalir(evt);
            }
        });
        this.submenuEscenario.add(this.salirMenuItem);
        this.menu.add(this.submenuEscenario);
        this.submenuVista.setMnemonic(this.translations.getString("MenuPrincipal.Vista.Resaltado").charAt(0));
        this.submenuVista.setText(this.translations.getString("MenuPrincipal.Vista"));
        this.submenuVista.setDoubleBuffered(true);
        this.submenuVista.setFont(new Font("Dialog", 0, 12));
        this.mosaicoHorizontalMenuItem.setFont(new Font("Dialog", 0, 12));
        this.mosaicoHorizontalMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.VISTA_HORIZONTAL));
        this.mosaicoHorizontalMenuItem.setMnemonic(this.translations.getString("SubmenuVista.MosaicoHorixontal.Resaltado").charAt(0));
        this.mosaicoHorizontalMenuItem.setText(this.translations.getString("SubmenuVista.MosaicoHorizontal"));
        this.mosaicoHorizontalMenuItem.setDoubleBuffered(true);
        this.mosaicoHorizontalMenuItem.setEnabled(false);
        this.mosaicoHorizontalMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnVistaMosaicoHorizontal(evt);
            }
        });
        this.submenuVista.add(this.mosaicoHorizontalMenuItem);
        this.mosaicoVerticalMenuItem.setFont(new Font("Dialog", 0, 12));
        this.mosaicoVerticalMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.VISTA_VERTICAL));
        this.mosaicoVerticalMenuItem.setMnemonic(this.translations.getString("SubmenuVista.MosaicoVertical.Resaltado").charAt(0));
        this.mosaicoVerticalMenuItem.setText(this.translations.getString("SubmenuVista.MosaicoVertical"));
        this.mosaicoVerticalMenuItem.setDoubleBuffered(true);
        this.mosaicoVerticalMenuItem.setEnabled(false);
        this.mosaicoVerticalMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnVistaMosaicoVertical(evt);
            }
        });
        this.submenuVista.add(this.mosaicoVerticalMenuItem);
        this.cascadaMenuItem.setFont(new Font("Dialog", 0, 12));
        this.cascadaMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.VISTA_CASCADA));
        this.cascadaMenuItem.setMnemonic(this.translations.getString("SubmenuVista.Cascadal.Resaltado").charAt(0));
        this.cascadaMenuItem.setText(this.translations.getString("SubmenuVista.Cascada"));
        this.cascadaMenuItem.setDoubleBuffered(true);
        this.cascadaMenuItem.setEnabled(false);
        this.cascadaMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnVistaCascada(evt);
            }
        });
        this.submenuVista.add(this.cascadaMenuItem);
        this.iconosMenuItem.setFont(new Font("Dialog", 0, 12));
        this.iconosMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.VISTA_ICONOS));
        this.iconosMenuItem.setMnemonic(this.translations.getString("SubmenuVista.Iconos.Resaltado").charAt(0));
        this.iconosMenuItem.setText(this.translations.getString("SubmenuVista.Iconos"));
        this.iconosMenuItem.setDoubleBuffered(true);
        this.iconosMenuItem.setEnabled(false);
        this.iconosMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnVistaIconos(evt);
            }
        });
        this.submenuVista.add(this.iconosMenuItem);
        this.menu.add(this.submenuVista);
        this.submenuAyuda.setMnemonic(this.translations.getString("LetraAyuda").charAt(0));
        this.submenuAyuda.setText(this.translations.getString("Help"));
        this.submenuAyuda.setDoubleBuffered(true);
        this.submenuAyuda.setFont(new Font("Dialog", 0, 12));
        this.tutorialMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        this.tutorialMenuItem.setFont(new Font("Dialog", 0, 12));
        this.tutorialMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.TUTORIAL_MENU));
        this.tutorialMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Contenidos").charAt(0));
        this.tutorialMenuItem.setText(this.translations.getString("Contents"));
        this.tutorialMenuItem.setDoubleBuffered(true);
        this.tutorialMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnTutorial(evt);
            }
        });
        this.submenuAyuda.add(this.tutorialMenuItem);
        this.submenuAyuda.add(this.jSeparator2);
        this.licenciaMenuItem.setFont(new Font("Dialog", 0, 12));
        this.licenciaMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.LICENCIA_MENU));
        this.licenciaMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Licencia").charAt(0));
        this.licenciaMenuItem.setText(this.translations.getString("License"));
        this.licenciaMenuItem.setToolTipText(this.translations.getString("License"));
        this.licenciaMenuItem.setDoubleBuffered(true);
        this.licenciaMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnLicencia(evt);
            }
        });
        this.submenuAyuda.add(this.licenciaMenuItem);
        this.comentarioMenuItem1.setFont(new Font("Dialog", 0, 12));
        this.comentarioMenuItem1.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.COMENTARIO_MENU));
        this.comentarioMenuItem1.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Comentario").charAt(0));
        this.comentarioMenuItem1.setText(this.translations.getString("Contact_the_authors"));
        this.comentarioMenuItem1.setToolTipText(this.translations.getString("License"));
        this.comentarioMenuItem1.setDoubleBuffered(true);
        this.comentarioMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnEnviarComentario(evt);
            }
        });
        this.submenuAyuda.add(this.comentarioMenuItem1);
        this.contribuyeMenuItem1.setFont(new Font("Dialog", 0, 12));
        this.contribuyeMenuItem1.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.COMENTARIO_MENU));
        this.contribuyeMenuItem1.setMnemonic(this.translations.getString("Menu.LetraResaltada.Contribuye").charAt(0));
        this.contribuyeMenuItem1.setText(this.translations.getString("Contribute"));
        this.contribuyeMenuItem1.setToolTipText(this.translations.getString("Contribute"));
        this.contribuyeMenuItem1.setDoubleBuffered(true);
        this.contribuyeMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnContribuye(evt);
            }
        });
        this.submenuAyuda.add(this.contribuyeMenuItem1);
        this.sobreMenuItem.setFont(new Font("Dialog", 0, 12));
        this.sobreMenuItem.setIcon(this.dispensadorDeImagenes.getIcon(TImageBroker.SPLASH_MENU));
        this.sobreMenuItem.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Sobre").charAt(0));
        this.sobreMenuItem.setText(this.translations.getString("About"));
        this.sobreMenuItem.setDoubleBuffered(true);
        this.sobreMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnSobre(evt);
            }
        });
        this.submenuAyuda.add(this.sobreMenuItem);
        this.menu.add(this.submenuAyuda);
        setJMenuBar(this.menu);
        pack();
    }

    /**
     * Este m�todo inicia los componentes que no han sido suficientemente
     * definidos por el constructor.
     *
     * @since 2.0
     */
    private void initComponents2() {
        this.GeneradorId = new TIDGenerator();
        this.numVentanasAbiertas = 0;
    }

    private void clicEnTutorial(ActionEvent evt) {
        if (Desktop.isDesktopSupported()) {
            try {
                URL url = this.getClass().getResource(this.translations.getString("JSimulator.GuidePath"));
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException ex) {
                Logger.getLogger(JOpenSimMPLS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(JOpenSimMPLS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void clicEnAbrir(ActionEvent evt) {
        boolean finAbrir = false;
        JFileChooser dialogoAbrir = new JFileChooser();
        dialogoAbrir.setFileFilter(new JOSMFilter());
        dialogoAbrir.setDialogType(JFileChooser.CUSTOM_DIALOG);
        dialogoAbrir.setApproveButtonMnemonic('A');
        dialogoAbrir.setApproveButtonText(this.translations.getString("JSimulador.DialogoAbrir.OK"));
        dialogoAbrir.setDialogTitle(this.translations.getString("JSimulador.DialogoAbrir.CargarUnEscenario"));
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
                            this.numVentanasAbiertas++;
                            activarOpciones();
                            JScenarioWindow nuevoEscenario = new JScenarioWindow(this, this.dispensadorDeImagenes, tit);
                            nuevoEscenario.ponerEscenario(esc);
                            this.Escritorio.add(nuevoEscenario, JLayeredPane.FRAME_CONTENT_LAYER);
                            try {
                                nuevoEscenario.setSelected(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            getContentPane().add(this.Escritorio, BorderLayout.CENTER);
                        } catch (Exception e) {
                            JErrorWindow ve;
                            ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
                            ve.setErrorMessage(e.toString());
                            ve.show();
                        }
                    } else {
                        JWarningWindow va;
                        va = new JWarningWindow(this, true, this.dispensadorDeImagenes);
                        va.setWarningMessage(this.translations.getString("JSimulador.DialogoAbrir.FicheroCorrupto"));
                        va.show();
                    }
                    dialogoAbrir = null;
                    finAbrir = true;
                } else {
                    JWarningWindow va;
                    va = new JWarningWindow(this, true, this.dispensadorDeImagenes);
                    va.setWarningMessage(this.translations.getString("JSimulador.DialogoAbrir.FicheroDebeExistir"));
                    va.show();
                }
            } else {
                finAbrir = true;
            }
        }
    }

    private void clicEnGuardarComo(ActionEvent evt) {
        JScenarioWindow vActiva = (JScenarioWindow) Escritorio.getSelectedFrame();
        if (vActiva != null) {
            vActiva.gestionarGuardarComo();
        }
    }

    private void clicEnGuardar(ActionEvent evt) {
        JScenarioWindow vActiva = (JScenarioWindow) Escritorio.getSelectedFrame();
        if (vActiva != null) {
            vActiva.gestionarGuardar();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnEnviarComentario(ActionEvent evt) {
        JWarningWindow warningWindow = new JWarningWindow(this, true, this.dispensadorDeImagenes);
        warningWindow.setWarningMessage("Not developed yet");
        warningWindow.setVisible(true);
        //JCommentWindow vc = new JCommentWindow(dispensadorDeImagenes, this, true);
        //vc.show();
    }

    private void clicEnContribuye(ActionEvent evt) {
        JWarningWindow warningWindow = new JWarningWindow(this, true, this.dispensadorDeImagenes);
        warningWindow.setWarningMessage("Not developed yet");
        warningWindow.setVisible(true);
        //JCommentWindow vc = new JCommentWindow(dispensadorDeImagenes, this, true);
        //vc.show();
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnVistaMosaicoHorizontal(ActionEvent evt) {
        JInternalFrame ventanas[] = this.Escritorio.getAllFrames();
        Dimension tam = this.Escritorio.getSize();
        int incremento = tam.width / ventanas.length;
        Point p = new Point(0, 0);
        tam.width = incremento;
        try {
            for (int i = ventanas.length - 1; i >= 0; i--) {
                ventanas[i].setVisible(false);
                ventanas[i].setIcon(false);
                ventanas[i].setSize(tam);
                ventanas[i].setLocation(p);
                p.x += incremento;
            }
            for (int j = ventanas.length - 1; j >= 0; j--) {
                ventanas[j].setVisible(true);
            }
        } catch (Exception e) {
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnVistaMosaicoVertical(ActionEvent evt) {
        JInternalFrame ventanas[] = this.Escritorio.getAllFrames();
        Dimension tam = this.Escritorio.getSize();
        int incremento = tam.height / ventanas.length;
        Point p = new Point(0, 0);
        tam.height = incremento;
        try {
            for (int i = ventanas.length - 1; i >= 0; i--) {
                ventanas[i].setVisible(false);
                ventanas[i].setIcon(false);
                ventanas[i].setSize(tam);
                ventanas[i].setLocation(p);
                p.y += incremento;
            }
            for (int j = ventanas.length - 1; j >= 0; j--) {
                ventanas[j].setVisible(true);
            }
        } catch (Exception e) {
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnVistaCascada(ActionEvent evt) {
        int incremento = 20;
        Dimension d = this.Escritorio.getSize();
        d.height = d.height - incremento;
        d.width = d.width - incremento;
        Point p = new Point(incremento, incremento);
        Dimension tam = this.Escritorio.getSize();
        tam.height = tam.height / 2;
        tam.width = tam.width / 2;
        JInternalFrame ventanas[] = this.Escritorio.getAllFrames();
        try {
            for (int i = ventanas.length - 1; i >= 0; i--) {
                ventanas[i].setVisible(false);
                ventanas[i].setIcon(false);
                ventanas[i].setSize(tam);
                ventanas[i].setLocation(p);
                p.x += incremento;
                p.y += incremento;
                if ((p.x + tam.width) >= d.width) {
                    p.x = incremento;
                }
                if ((p.y + tam.height) >= d.height) {
                    p.y = incremento;
                }
            }
            for (int j = ventanas.length - 1; j >= 0; j--) {
                ventanas[j].setVisible(true);
            }
        } catch (Exception e) {
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnVistaIconos(ActionEvent evt) {
        JInternalFrame ventanas[] = this.Escritorio.getAllFrames();
        try {
            for (int i = 0; i < ventanas.length; i++) {
                ventanas[i].setIcon(true);
            }
            ventanas[0].setSelected(true);
        } catch (Exception e) {
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnCerrar(ActionEvent evt) {
        JScenarioWindow vActiva = (JScenarioWindow) Escritorio.getSelectedFrame();
        if (vActiva != null) {
            vActiva.gestionarGuardarParaCerrar();
            vActiva.cerrar();
            this.numVentanasAbiertas--;
        }
        if (this.numVentanasAbiertas == 0) {
            desactivarOpciones();
        } else {
            if (this.numVentanasAbiertas == 1) {
                desactivarOpcionesVista();
            }
            JInternalFrame ventanas[] = this.Escritorio.getAllFrames();
            try {
                if (ventanas[0].isIcon()) {
                    ventanas[0].setIcon(false);
                }
                ventanas[0].setSelected(true);
            } catch (Exception e) {
                JErrorWindow ve;
                ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
                ve.setErrorMessage(e.toString());
                ve.show();
            }
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnNuevo(ActionEvent evt) {
        try {
            int id = this.GeneradorId.getNew();
            String tit = this.translations.getString("TextoUntitled-") + id;
            this.numVentanasAbiertas++;
            activarOpciones();
            JScenarioWindow nuevoEscenario = new JScenarioWindow(this, this.dispensadorDeImagenes, tit);
            this.Escritorio.add(nuevoEscenario, JLayeredPane.FRAME_CONTENT_LAYER);
            try {
                nuevoEscenario.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getContentPane().add(this.Escritorio, BorderLayout.CENTER);
        } catch (Exception e) {
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnLicencia(ActionEvent evt) {
        JLicenseWindow ventanaLicencia;
        ventanaLicencia = new JLicenseWindow(this, true, this.dispensadorDeImagenes);
        ventanaLicencia.show();
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void clicEnSobre(ActionEvent evt) {
        JAboutWindow nuevoSobre;
        nuevoSobre = new JAboutWindow(this, true, this.dispensadorDeImagenes);
        nuevoSobre.show();
    }

    /**
     * Este m�todo cierra todas las ventanas hijas que haya abiertas en ese
     * momento.
     *
     * @since 2.0
     */
    public void cerrarTodo() {
        while (this.numVentanasAbiertas > 0) {
            JScenarioWindow vActiva = (JScenarioWindow) this.Escritorio.getSelectedFrame();
            if (vActiva != null) {
                vActiva.gestionarGuardarParaCerrar();
                vActiva.cerrar();
                this.numVentanasAbiertas--;
            }
            if (this.numVentanasAbiertas > 0) {
                JInternalFrame ventanas[] = this.Escritorio.getAllFrames();
                if (ventanas.length > 0) {
                    try {
                        if (ventanas[0].isIcon()) {
                            ventanas[0].setIcon(false);
                        }
                        ventanas[0].setSelected(true);
                    } catch (Exception e) {
                        JErrorWindow ve;
                        ve = new JErrorWindow(this, true, this.dispensadorDeImagenes);
                        ve.setErrorMessage(e.toString());
                        ve.show();
                    }
                } else {
                    this.numVentanasAbiertas = 0;
                }
            }
        }
    }

    /**
     * Sale del simulador cuando se elige desde el men� ARCHIVO
     *
     * @param evt
     * @since 2.0
     */
    private void clicEnSalir(ActionEvent evt) {
        clicEnCualquierSalir();
    }

    /**
     * @since 2.0
     */
    private void clicEnCualquierSalir() {
        JDecissionWindow vb = new JDecissionWindow(this, true, this.dispensadorDeImagenes);
        vb.setQuestion(this.translations.getString("JSimulador.PreguntaSalirDelSimulador"));
        vb.show();
        boolean respuesta = vb.getUserAnswer();
        vb.dispose();
        if (respuesta) {
            cerrarTodo();
            this.dispose();
        }
    }

    /**
     * Sale del simulador
     *
     * @param evt
     * @since 2.0
     */
    private void exitForm(WindowEvent evt) {
        clicEnCualquierSalir();
    }

    /**
     * @since 2.0
     */
    private void desactivarOpciones() {
        this.cerrarMenuItem.setEnabled(false);
        this.guardarComoMenuItem.setEnabled(false);
        this.guardarMenuItem.setEnabled(false);
        desactivarOpcionesVista();
    }

    /**
     * @since 2.0
     */
    private void activarOpciones() {
        this.cerrarMenuItem.setEnabled(true);
        this.guardarComoMenuItem.setEnabled(true);
        this.guardarMenuItem.setEnabled(true);
        activarOpcionesVista();
    }

    /**
     * @since 2.0
     */
    private void desactivarOpcionesVista() {
        if (this.numVentanasAbiertas == 0) {
            this.mosaicoHorizontalMenuItem.setEnabled(false);
            this.mosaicoVerticalMenuItem.setEnabled(false);
            this.cascadaMenuItem.setEnabled(false);
            this.iconosMenuItem.setEnabled(false);
        } else if (this.numVentanasAbiertas == 1) {
            this.mosaicoHorizontalMenuItem.setEnabled(false);
            this.mosaicoVerticalMenuItem.setEnabled(false);
            this.cascadaMenuItem.setEnabled(false);
            this.iconosMenuItem.setEnabled(true);
        }
    }

    /**
     * @since 2.0
     */
    private void activarOpcionesVista() {
        if (this.numVentanasAbiertas == 1) {
            this.mosaicoHorizontalMenuItem.setEnabled(false);
            this.mosaicoVerticalMenuItem.setEnabled(false);
            this.cascadaMenuItem.setEnabled(false);
            this.iconosMenuItem.setEnabled(true);
        } else if (this.numVentanasAbiertas > 1) {
            this.mosaicoHorizontalMenuItem.setEnabled(true);
            this.mosaicoVerticalMenuItem.setEnabled(true);
            this.cascadaMenuItem.setEnabled(true);
            this.iconosMenuItem.setEnabled(true);
        }
    }

    private TImageBroker dispensadorDeImagenes;
    private TIDGenerator GeneradorId;
    private int numVentanasAbiertas;
    private JDesktopPane Escritorio;
    private JMenuItem abrirMenuItem;
    private JMenuItem cascadaMenuItem;
    private JMenuItem cerrarMenuItem;
    private JMenuItem comentarioMenuItem1;
    private JMenuItem contribuyeMenuItem1;
    private JMenuItem guardarComoMenuItem;
    private JMenuItem guardarMenuItem;
    private JMenuItem iconosMenuItem;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JMenuItem licenciaMenuItem;
    private JMenuBar menu;
    private JMenuItem mosaicoHorizontalMenuItem;
    private JMenuItem mosaicoVerticalMenuItem;
    private JMenuItem nuevoMenuItem;
    private JMenuItem salirMenuItem;
    private JMenuItem sobreMenuItem;
    private JMenu submenuAyuda;
    private JMenu submenuEscenario;
    private JMenu submenuVista;
    private JMenuItem tutorialMenuItem;
    private ResourceBundle translations;
}
