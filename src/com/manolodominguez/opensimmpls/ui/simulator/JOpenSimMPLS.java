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
     * @param imageBroker El dispensador de im�genes que se encargar� de
     * precargar las im�genes necesarias en el simulador, ahorrando tiempo y
     * mejorando el rendimiento de la aplicaci�n.
     */
    public JOpenSimMPLS(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
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
        this.desktopPane = new JDesktopPane();
        this.menuBar = new JMenuBar();
        this.menuScenario = new JMenu();
        this.menuItemNew = new JMenuItem();
        this.menuItemOpen = new JMenuItem();
        this.menuItemClose = new JMenuItem();
        this.menuItemSave = new JMenuItem();
        this.menuItemSaveAs = new JMenuItem();
        this.separatorMenuScenario = new JSeparator();
        this.menuItemExit = new JMenuItem();
        this.menuView = new JMenu();
        this.menuItemHorizontalMosaic = new JMenuItem();
        this.menuItemVerticalMosaic = new JMenuItem();
        this.menuItemCascade = new JMenuItem();
        this.menuItemIcons = new JMenuItem();
        this.menuHelp = new JMenu();
        this.menuItemQuickGuide = new JMenuItem();
        this.separatorMenuHelp = new JSeparator();
        this.menuItemLicense = new JMenuItem();
        this.menuItemAskTheCommunity = new JMenuItem();
        this.menuItemContribute = new JMenuItem();
        this.menuItemAbout = new JMenuItem();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(this.translations.getString("Open_SimMPLS"));
        setIconImage(this.imageBroker.obtenerImagen(TImageBroker.SPLASH_MENU));
        setName(this.translations.getString("simulator"));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleWindowsClosing(evt);
            }
        });
        getContentPane().add(this.desktopPane, BorderLayout.CENTER);
        this.menuBar.setDoubleBuffered(true);
        this.menuScenario.setMnemonic(this.translations.getString("LetraEscenario").charAt(0));
        this.menuScenario.setText(this.translations.getString("Scene"));
        this.menuScenario.setToolTipText(this.translations.getString("Scene"));
        this.menuScenario.setActionCommand(this.translations.getString("File"));
        this.menuScenario.setDoubleBuffered(true);
        this.menuScenario.setFont(new Font("Dialog", 0, 12));
        this.menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        this.menuItemNew.setFont(new Font("Dialog", 0, 12));
        this.menuItemNew.setIcon(this.imageBroker.getIcon(TImageBroker.NUEVO_MENU));
        this.menuItemNew.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Nuevo").charAt(0));
        this.menuItemNew.setText(this.translations.getString("New"));
        this.menuItemNew.setDoubleBuffered(true);
        this.menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnNew(evt);
            }
        });
        this.menuScenario.add(this.menuItemNew);
        this.menuItemOpen.setFont(new Font("Dialog", 0, 12));
        this.menuItemOpen.setIcon(this.imageBroker.getIcon(TImageBroker.ABRIR_MENU));
        this.menuItemOpen.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Abrir").charAt(0));
        this.menuItemOpen.setText(this.translations.getString("Open"));
        this.menuItemOpen.setDoubleBuffered(true);
        this.menuItemOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOpen(evt);
            }
        });
        this.menuScenario.add(this.menuItemOpen);
        this.menuItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        this.menuItemClose.setFont(new Font("Dialog", 0, 12));
        this.menuItemClose.setIcon(this.imageBroker.getIcon(TImageBroker.CERRAR_MENU));
        this.menuItemClose.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Cerrar").charAt(0));
        this.menuItemClose.setText(this.translations.getString("Close"));
        this.menuItemClose.setDoubleBuffered(true);
        this.menuItemClose.setEnabled(false);
        this.menuItemClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnCloseScenario(evt);
            }
        });
        this.menuScenario.add(this.menuItemClose);
        this.menuItemSave.setFont(new Font("Dialog", 0, 12));
        this.menuItemSave.setIcon(this.imageBroker.getIcon(TImageBroker.GUARDAR_MENU));
        this.menuItemSave.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Guardar").charAt(0));
        this.menuItemSave.setText(this.translations.getString("Save"));
        this.menuItemSave.setDoubleBuffered(true);
        this.menuItemSave.setEnabled(false);
        this.menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnSave(evt);
            }
        });
        this.menuScenario.add(menuItemSave);
        this.menuItemSaveAs.setFont(new Font("Dialog", 0, 12));
        this.menuItemSaveAs.setIcon(this.imageBroker.getIcon(TImageBroker.GUARDAR_COMO_MENU));
        this.menuItemSaveAs.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Guardar_como").charAt(0));
        this.menuItemSaveAs.setText(this.translations.getString("Save_as..."));
        this.menuItemSaveAs.setDoubleBuffered(true);
        this.menuItemSaveAs.setEnabled(false);
        this.menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnSaveAs(evt);
            }
        });
        this.menuScenario.add(this.menuItemSaveAs);
        this.menuScenario.add(this.separatorMenuScenario);
        this.menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        this.menuItemExit.setFont(new Font("Dialog", 0, 12));
        this.menuItemExit.setIcon(this.imageBroker.getIcon(TImageBroker.SALIR_MENU));
        this.menuItemExit.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Salir").charAt(0));
        this.menuItemExit.setText(this.translations.getString("Exit"));
        this.menuItemExit.setDoubleBuffered(true);
        this.menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnExit(evt);
            }
        });
        this.menuScenario.add(this.menuItemExit);
        this.menuBar.add(this.menuScenario);
        this.menuView.setMnemonic(this.translations.getString("MenuPrincipal.Vista.Resaltado").charAt(0));
        this.menuView.setText(this.translations.getString("MenuPrincipal.Vista"));
        this.menuView.setDoubleBuffered(true);
        this.menuView.setFont(new Font("Dialog", 0, 12));
        this.menuItemHorizontalMosaic.setFont(new Font("Dialog", 0, 12));
        this.menuItemHorizontalMosaic.setIcon(this.imageBroker.getIcon(TImageBroker.VISTA_HORIZONTAL));
        this.menuItemHorizontalMosaic.setMnemonic(this.translations.getString("SubmenuVista.MosaicoHorixontal.Resaltado").charAt(0));
        this.menuItemHorizontalMosaic.setText(this.translations.getString("SubmenuVista.MosaicoHorizontal"));
        this.menuItemHorizontalMosaic.setDoubleBuffered(true);
        this.menuItemHorizontalMosaic.setEnabled(false);
        this.menuItemHorizontalMosaic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnHorizontalMosaic(evt);
            }
        });
        this.menuView.add(this.menuItemHorizontalMosaic);
        this.menuItemVerticalMosaic.setFont(new Font("Dialog", 0, 12));
        this.menuItemVerticalMosaic.setIcon(this.imageBroker.getIcon(TImageBroker.VISTA_VERTICAL));
        this.menuItemVerticalMosaic.setMnemonic(this.translations.getString("SubmenuVista.MosaicoVertical.Resaltado").charAt(0));
        this.menuItemVerticalMosaic.setText(this.translations.getString("SubmenuVista.MosaicoVertical"));
        this.menuItemVerticalMosaic.setDoubleBuffered(true);
        this.menuItemVerticalMosaic.setEnabled(false);
        this.menuItemVerticalMosaic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnVerticalMosaic(evt);
            }
        });
        this.menuView.add(this.menuItemVerticalMosaic);
        this.menuItemCascade.setFont(new Font("Dialog", 0, 12));
        this.menuItemCascade.setIcon(this.imageBroker.getIcon(TImageBroker.VISTA_CASCADA));
        this.menuItemCascade.setMnemonic(this.translations.getString("SubmenuVista.Cascadal.Resaltado").charAt(0));
        this.menuItemCascade.setText(this.translations.getString("SubmenuVista.Cascada"));
        this.menuItemCascade.setDoubleBuffered(true);
        this.menuItemCascade.setEnabled(false);
        this.menuItemCascade.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnCascade(evt);
            }
        });
        this.menuView.add(this.menuItemCascade);
        this.menuItemIcons.setFont(new Font("Dialog", 0, 12));
        this.menuItemIcons.setIcon(this.imageBroker.getIcon(TImageBroker.VISTA_ICONOS));
        this.menuItemIcons.setMnemonic(this.translations.getString("SubmenuVista.Iconos.Resaltado").charAt(0));
        this.menuItemIcons.setText(this.translations.getString("SubmenuVista.Iconos"));
        this.menuItemIcons.setDoubleBuffered(true);
        this.menuItemIcons.setEnabled(false);
        this.menuItemIcons.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnIcons(evt);
            }
        });
        this.menuView.add(this.menuItemIcons);
        this.menuBar.add(this.menuView);
        this.menuHelp.setMnemonic(this.translations.getString("LetraAyuda").charAt(0));
        this.menuHelp.setText(this.translations.getString("Help"));
        this.menuHelp.setDoubleBuffered(true);
        this.menuHelp.setFont(new Font("Dialog", 0, 12));
        this.menuItemQuickGuide.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        this.menuItemQuickGuide.setFont(new Font("Dialog", 0, 12));
        this.menuItemQuickGuide.setIcon(this.imageBroker.getIcon(TImageBroker.TUTORIAL_MENU));
        this.menuItemQuickGuide.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Contenidos").charAt(0));
        this.menuItemQuickGuide.setText(this.translations.getString("Contents"));
        this.menuItemQuickGuide.setDoubleBuffered(true);
        this.menuItemQuickGuide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnQickGuide(evt);
            }
        });
        this.menuHelp.add(this.menuItemQuickGuide);
        this.menuHelp.add(this.separatorMenuHelp);
        this.menuItemLicense.setFont(new Font("Dialog", 0, 12));
        this.menuItemLicense.setIcon(this.imageBroker.getIcon(TImageBroker.LICENCIA_MENU));
        this.menuItemLicense.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Licencia").charAt(0));
        this.menuItemLicense.setText(this.translations.getString("License"));
        this.menuItemLicense.setToolTipText(this.translations.getString("License"));
        this.menuItemLicense.setDoubleBuffered(true);
        this.menuItemLicense.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnLicense(evt);
            }
        });
        this.menuHelp.add(this.menuItemLicense);
        this.menuItemAskTheCommunity.setFont(new Font("Dialog", 0, 12));
        this.menuItemAskTheCommunity.setIcon(this.imageBroker.getIcon(TImageBroker.COMENTARIO_MENU));
        this.menuItemAskTheCommunity.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Comentario").charAt(0));
        this.menuItemAskTheCommunity.setText(this.translations.getString("Contact_the_authors"));
        this.menuItemAskTheCommunity.setToolTipText(this.translations.getString("License"));
        this.menuItemAskTheCommunity.setDoubleBuffered(true);
        this.menuItemAskTheCommunity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnAskTheCommunity(evt);
            }
        });
        this.menuHelp.add(this.menuItemAskTheCommunity);
        this.menuItemContribute.setFont(new Font("Dialog", 0, 12));
        this.menuItemContribute.setIcon(this.imageBroker.getIcon(TImageBroker.COMENTARIO_MENU));
        this.menuItemContribute.setMnemonic(this.translations.getString("Menu.LetraResaltada.Contribuye").charAt(0));
        this.menuItemContribute.setText(this.translations.getString("Contribute"));
        this.menuItemContribute.setToolTipText(this.translations.getString("Contribute"));
        this.menuItemContribute.setDoubleBuffered(true);
        this.menuItemContribute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnContribute(evt);
            }
        });
        this.menuHelp.add(this.menuItemContribute);
        this.menuItemAbout.setFont(new Font("Dialog", 0, 12));
        this.menuItemAbout.setIcon(this.imageBroker.getIcon(TImageBroker.SPLASH_MENU));
        this.menuItemAbout.setMnemonic(this.translations.getString("Menu.Letra_resaltada.Sobre").charAt(0));
        this.menuItemAbout.setText(this.translations.getString("About"));
        this.menuItemAbout.setDoubleBuffered(true);
        this.menuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnAbout(evt);
            }
        });
        this.menuHelp.add(this.menuItemAbout);
        this.menuBar.add(this.menuHelp);
        setJMenuBar(this.menuBar);
        pack();
    }

    /**
     * Este m�todo inicia los componentes que no han sido suficientemente
     * definidos por el constructor.
     *
     * @since 2.0
     */
    private void initComponents2() {
        this.openScenariosIDGenerator = new TIDGenerator();
        // FIX: do not use harcoded values. Use class constants instead.
        this.numOpenScenarios = 0;
    }

    private void handleClickOnQickGuide(ActionEvent evt) {
        if (Desktop.isDesktopSupported()) {
            try {
                URL url = this.getClass().getResource(this.translations.getString("JSimulator.GuidePath"));
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException ex) {
                JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
                // FIX: i18N needed
                errorWindow.setErrorMessage("Cannot open Quick Guide");
                errorWindow.setVisible(true);
            } catch (URISyntaxException ex) {
                JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
                // FIX: i18N needed
                errorWindow.setErrorMessage("Cannot open Quick Guide");
                errorWindow.setVisible(true);
            }
        } else {
            JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
            // FIX: i18N needed
            errorWindow.setErrorMessage("Cannot open Quick Guide");
            errorWindow.setVisible(true);
        }
    }

    private void handleClickOnOpen(ActionEvent evt) {
        boolean openProcessFinished = false;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new JOSMFilter());
        fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
        fileChooser.setApproveButtonMnemonic('A');
        fileChooser.setApproveButtonText(this.translations.getString("JSimulador.DialogoAbrir.OK"));
        fileChooser.setDialogTitle(this.translations.getString("JSimulador.DialogoAbrir.CargarUnEscenario"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        while (!openProcessFinished) {
            int fileChoosingResult = fileChooser.showOpenDialog(this);
            if (fileChoosingResult == JFileChooser.APPROVE_OPTION) {
                if (fileChooser.getSelectedFile().exists()) {
                    TOSMLoader osmLoader = new TOSMLoader();
                    boolean isLoaded = osmLoader.cargar(fileChooser.getSelectedFile());
                    if (isLoaded) {
                        try {
                            TScenario scenario = new TScenario();
                            scenario = osmLoader.getScenario();
                            String title = scenario.getScenarioFile().getName();
                            this.numOpenScenarios++;
                            activateOptions();
                            JScenarioWindow newScenarioWindow = new JScenarioWindow(this, this.imageBroker, title);
                            newScenarioWindow.setScenario(scenario);
                            this.desktopPane.add(newScenarioWindow, JLayeredPane.FRAME_CONTENT_LAYER);
                            try {
                                newScenarioWindow.setSelected(true);
                            } catch (Exception e) {
                                // FIX: This is ugly
                                e.printStackTrace();
                            }
                            getContentPane().add(this.desktopPane, BorderLayout.CENTER);
                        } catch (Exception e) {
                            JErrorWindow errorWindow;
                            errorWindow = new JErrorWindow(this, true, this.imageBroker);
                            errorWindow.setErrorMessage(e.toString());
                            errorWindow.setVisible(true);
                        }
                    } else {
                        JWarningWindow warningWindow;
                        warningWindow = new JWarningWindow(this, true, this.imageBroker);
                        warningWindow.setWarningMessage(this.translations.getString("JSimulador.DialogoAbrir.FicheroCorrupto"));
                        warningWindow.setVisible(true);
                    }
                    fileChooser = null;
                    openProcessFinished = true;
                } else {
                    JWarningWindow warningWindow;
                    warningWindow = new JWarningWindow(this, true, this.imageBroker);
                    warningWindow.setWarningMessage(this.translations.getString("JSimulador.DialogoAbrir.FicheroDebeExistir"));
                    warningWindow.setVisible(true);
                }
            } else {
                openProcessFinished = true;
            }
        }
    }

    private void handleClickOnSaveAs(ActionEvent evt) {
        JScenarioWindow activeScenario = (JScenarioWindow) this.desktopPane.getSelectedFrame();
        if (activeScenario != null) {
            activeScenario.handleSaveAs();
        }
    }

    private void handleClickOnSave(ActionEvent evt) {
        JScenarioWindow activeScenario = (JScenarioWindow) this.desktopPane.getSelectedFrame();
        if (activeScenario != null) {
            activeScenario.handleSave();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnAskTheCommunity(ActionEvent evt) {
        JWarningWindow warningWindow = new JWarningWindow(this, true, this.imageBroker);
        // FIX: i18N needed
        warningWindow.setWarningMessage("Not developed yet");
        warningWindow.setVisible(true);
    }

    private void handleClickOnContribute(ActionEvent evt) {
        JWarningWindow warningWindow = new JWarningWindow(this, true, this.imageBroker);
        // FIX: i18N needed
        warningWindow.setWarningMessage("Not developed yet");
        warningWindow.setVisible(true);
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnHorizontalMosaic(ActionEvent evt) {
        JInternalFrame[] openScenarios = this.desktopPane.getAllFrames();
        Dimension parentSize = this.desktopPane.getSize();
        int width = parentSize.width / openScenarios.length;
        Point point = new Point(0, 0);
        parentSize.width = width;
        try {
            for (int i = openScenarios.length - 1; i >= 0; i--) {
                openScenarios[i].setVisible(false);
                openScenarios[i].setIcon(false);
                openScenarios[i].setSize(parentSize);
                openScenarios[i].setLocation(point);
                point.x += width;
            }
            for (int j = openScenarios.length - 1; j >= 0; j--) {
                openScenarios[j].setVisible(true);
            }
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnVerticalMosaic(ActionEvent evt) {
        JInternalFrame[] openScenarios = this.desktopPane.getAllFrames();
        Dimension parentSize = this.desktopPane.getSize();
        int height = parentSize.height / openScenarios.length;
        Point point = new Point(0, 0);
        parentSize.height = height;
        try {
            for (int i = openScenarios.length - 1; i >= 0; i--) {
                openScenarios[i].setVisible(false);
                openScenarios[i].setIcon(false);
                openScenarios[i].setSize(parentSize);
                openScenarios[i].setLocation(point);
                point.y += height;
            }
            for (int j = openScenarios.length - 1; j >= 0; j--) {
                openScenarios[j].setVisible(true);
            }
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(rootPaneCheckingEnabled);
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnCascade(ActionEvent evt) {
        int displacement = 20;
        Dimension dimension = this.desktopPane.getSize();
        dimension.height = dimension.height - displacement;
        dimension.width = dimension.width - displacement;
        Point point = new Point(displacement, displacement);
        Dimension scenarioWindowSize = this.desktopPane.getSize();
        scenarioWindowSize.height = scenarioWindowSize.height / 2;
        scenarioWindowSize.width = scenarioWindowSize.width / 2;
        JInternalFrame[] openScenarios = this.desktopPane.getAllFrames();
        try {
            for (int i = openScenarios.length - 1; i >= 0; i--) {
                openScenarios[i].setVisible(false);
                openScenarios[i].setIcon(false);
                openScenarios[i].setSize(scenarioWindowSize);
                openScenarios[i].setLocation(point);
                point.x += displacement;
                point.y += displacement;
                if ((point.x + scenarioWindowSize.width) >= dimension.width) {
                    point.x = displacement;
                }
                if ((point.y + scenarioWindowSize.height) >= dimension.height) {
                    point.y = displacement;
                }
            }
            for (int j = openScenarios.length - 1; j >= 0; j--) {
                openScenarios[j].setVisible(true);
            }
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnIcons(ActionEvent evt) {
        JInternalFrame ventanas[] = this.desktopPane.getAllFrames();
        try {
            for (int i = 0; i < ventanas.length; i++) {
                ventanas[i].setIcon(true);
            }
            ventanas[0].setSelected(true);
        } catch (Exception e) {
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, this.imageBroker);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnCloseScenario(ActionEvent evt) {
        JScenarioWindow vActiva = (JScenarioWindow) desktopPane.getSelectedFrame();
        if (vActiva != null) {
            vActiva.gestionarGuardarParaCerrar();
            vActiva.cerrar();
            this.numOpenScenarios--;
        }
        if (this.numOpenScenarios == 0) {
            desactivarOpciones();
        } else {
            if (this.numOpenScenarios == 1) {
                desactivarOpcionesVista();
            }
            JInternalFrame ventanas[] = this.desktopPane.getAllFrames();
            try {
                if (ventanas[0].isIcon()) {
                    ventanas[0].setIcon(false);
                }
                ventanas[0].setSelected(true);
            } catch (Exception e) {
                JErrorWindow ve;
                ve = new JErrorWindow(this, true, this.imageBroker);
                ve.setErrorMessage(e.toString());
                ve.show();
            }
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnNew(ActionEvent evt) {
        try {
            int id = this.openScenariosIDGenerator.getNew();
            String tit = this.translations.getString("TextoUntitled-") + id;
            this.numOpenScenarios++;
            activateOptions();
            JScenarioWindow nuevoEscenario = new JScenarioWindow(this, this.imageBroker, tit);
            this.desktopPane.add(nuevoEscenario, JLayeredPane.FRAME_CONTENT_LAYER);
            try {
                nuevoEscenario.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getContentPane().add(this.desktopPane, BorderLayout.CENTER);
        } catch (Exception e) {
            JErrorWindow ve;
            ve = new JErrorWindow(this, true, this.imageBroker);
            ve.setErrorMessage(e.toString());
            ve.show();
        }
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnLicense(ActionEvent evt) {
        JLicenseWindow ventanaLicencia;
        ventanaLicencia = new JLicenseWindow(this, true, this.imageBroker);
        ventanaLicencia.show();
    }

    /**
     * @param evt
     * @since 2.0
     */
    private void handleClickOnAbout(ActionEvent evt) {
        JAboutWindow nuevoSobre;
        nuevoSobre = new JAboutWindow(this, true, this.imageBroker);
        nuevoSobre.show();
    }

    /**
     * Este m�todo cierra todas las ventanas hijas que haya abiertas en ese
     * momento.
     *
     * @since 2.0
     */
    public void cerrarTodo() {
        while (this.numOpenScenarios > 0) {
            JScenarioWindow vActiva = (JScenarioWindow) this.desktopPane.getSelectedFrame();
            if (vActiva != null) {
                vActiva.gestionarGuardarParaCerrar();
                vActiva.cerrar();
                this.numOpenScenarios--;
            }
            if (this.numOpenScenarios > 0) {
                JInternalFrame ventanas[] = this.desktopPane.getAllFrames();
                if (ventanas.length > 0) {
                    try {
                        if (ventanas[0].isIcon()) {
                            ventanas[0].setIcon(false);
                        }
                        ventanas[0].setSelected(true);
                    } catch (Exception e) {
                        JErrorWindow ve;
                        ve = new JErrorWindow(this, true, this.imageBroker);
                        ve.setErrorMessage(e.toString());
                        ve.show();
                    }
                } else {
                    this.numOpenScenarios = 0;
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
    private void handleClickOnExit(ActionEvent evt) {
        clicEnCualquierSalir();
    }

    /**
     * @since 2.0
     */
    private void clicEnCualquierSalir() {
        JDecissionWindow vb = new JDecissionWindow(this, true, this.imageBroker);
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
    private void handleWindowsClosing(WindowEvent evt) {
        clicEnCualquierSalir();
    }

    /**
     * @since 2.0
     */
    private void desactivarOpciones() {
        this.menuItemClose.setEnabled(false);
        this.menuItemSaveAs.setEnabled(false);
        this.menuItemSave.setEnabled(false);
        desactivarOpcionesVista();
    }

    /**
     * @since 2.0
     */
    private void activateOptions() {
        this.menuItemClose.setEnabled(true);
        this.menuItemSaveAs.setEnabled(true);
        this.menuItemSave.setEnabled(true);
        activarOpcionesVista();
    }

    /**
     * @since 2.0
     */
    private void desactivarOpcionesVista() {
        if (this.numOpenScenarios == 0) {
            this.menuItemHorizontalMosaic.setEnabled(false);
            this.menuItemVerticalMosaic.setEnabled(false);
            this.menuItemCascade.setEnabled(false);
            this.menuItemIcons.setEnabled(false);
        } else if (this.numOpenScenarios == 1) {
            this.menuItemHorizontalMosaic.setEnabled(false);
            this.menuItemVerticalMosaic.setEnabled(false);
            this.menuItemCascade.setEnabled(false);
            this.menuItemIcons.setEnabled(true);
        }
    }

    /**
     * @since 2.0
     */
    private void activarOpcionesVista() {
        if (this.numOpenScenarios == 1) {
            this.menuItemHorizontalMosaic.setEnabled(false);
            this.menuItemVerticalMosaic.setEnabled(false);
            this.menuItemCascade.setEnabled(false);
            this.menuItemIcons.setEnabled(true);
        } else if (this.numOpenScenarios > 1) {
            this.menuItemHorizontalMosaic.setEnabled(true);
            this.menuItemVerticalMosaic.setEnabled(true);
            this.menuItemCascade.setEnabled(true);
            this.menuItemIcons.setEnabled(true);
        }
    }

    private TImageBroker imageBroker;
    private TIDGenerator openScenariosIDGenerator;
    private int numOpenScenarios;
    private JDesktopPane desktopPane;
    private JMenuItem menuItemOpen;
    private JMenuItem menuItemCascade;
    private JMenuItem menuItemClose;
    private JMenuItem menuItemAskTheCommunity;
    private JMenuItem menuItemContribute;
    private JMenuItem menuItemSaveAs;
    private JMenuItem menuItemSave;
    private JMenuItem menuItemIcons;
    private JSeparator separatorMenuScenario;
    private JSeparator separatorMenuHelp;
    private JMenuItem menuItemLicense;
    private JMenuBar menuBar;
    private JMenuItem menuItemHorizontalMosaic;
    private JMenuItem menuItemVerticalMosaic;
    private JMenuItem menuItemNew;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemAbout;
    private JMenu menuHelp;
    private JMenu menuScenario;
    private JMenu menuView;
    private JMenuItem menuItemQuickGuide;
    private ResourceBundle translations;
}
