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
 * This class implements the main OpenSimMPLS GUI and start OpenSimMPLS.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JOpenSimMPLS extends JFrame {

    /**
     * This is the constructor of the class and creates a new instance of
     * JOpenSimMPLS.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @since 2.0
     */
    public JOpenSimMPLS(TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
        initComponents();
        initComponents2();
    }

    /**
     * This method is called from within the constructor to initialize the
     * window components.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
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
        setIconImage(this.imageBroker.getImage(TImageBroker.ABOUT_MENU_COLOR));
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
        this.menuItemNew.setIcon(this.imageBroker.getImageIcon(TImageBroker.NEW_MENU_COLOR));
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
        this.menuItemOpen.setIcon(this.imageBroker.getImageIcon(TImageBroker.OPEN_MENU_COLOR));
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
        this.menuItemClose.setIcon(this.imageBroker.getImageIcon(TImageBroker.CLOSE_MENU_COLOR));
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
        this.menuItemSave.setIcon(this.imageBroker.getImageIcon(TImageBroker.SAVE_MENU_COLOR));
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
        this.menuItemSaveAs.setIcon(this.imageBroker.getImageIcon(TImageBroker.SAVE_AS_MENU_COLOR));
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
        this.menuItemExit.setIcon(this.imageBroker.getImageIcon(TImageBroker.EXIT_MENU_COLOR));
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
        this.menuItemHorizontalMosaic.setIcon(this.imageBroker.getImageIcon(TImageBroker.HORIZONTAL_MOSAIC_COLOR));
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
        this.menuItemVerticalMosaic.setIcon(this.imageBroker.getImageIcon(TImageBroker.VERTICAL_MOSAIC_COLOR));
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
        this.menuItemCascade.setIcon(this.imageBroker.getImageIcon(TImageBroker.CASCADE_VIEW_COLOR));
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
        this.menuItemIcons.setIcon(this.imageBroker.getImageIcon(TImageBroker.ICONS_VIEW_COLOR));
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
        this.menuItemQuickGuide.setIcon(this.imageBroker.getImageIcon(TImageBroker.TUTORIAL_MENU_COLOR));
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
        this.menuItemLicense.setIcon(this.imageBroker.getImageIcon(TImageBroker.LICENSE_MENU_COLOR));
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
        this.menuItemAskTheCommunity.setIcon(this.imageBroker.getImageIcon(TImageBroker.COMMENT_MENU_COLOR));
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
        this.menuItemContribute.setIcon(this.imageBroker.getImageIcon(TImageBroker.COMMENT_MENU_COLOR));
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
        this.menuItemAbout.setIcon(this.imageBroker.getImageIcon(TImageBroker.ABOUT_MENU_COLOR));
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
     * This method is called from within the constructor to do additional
     * configurations of window components.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void initComponents2() {
        this.scenarioWindowIDGenerator = new TIDGenerator();
        // FIX: do not use harcoded values. Use class constants instead.
        this.numOpenScenarios = 0;
    }

    /**
     * This method opens the OpenSImMPLS Quick User Guide using the PDF reader
     * configured in the operating system.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
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

    /**
     * This method opens a dialog to choose an existing OpenSimMPLS scenario and
     * opens it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
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
                            enableMenuOptions();
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

    /**
     * This method opens a dialog to choose name for the current active scenario
     * and saves it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnSaveAs(ActionEvent evt) {
        JScenarioWindow activeScenario = (JScenarioWindow) this.desktopPane.getSelectedFrame();
        if (activeScenario != null) {
            activeScenario.handleSaveAs();
        }
    }

    /**
     * This method saves the current active scenario using its current name.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnSave(ActionEvent evt) {
        JScenarioWindow activeScenario = (JScenarioWindow) this.desktopPane.getSelectedFrame();
        if (activeScenario != null) {
            activeScenario.handleSave();
        }
    }

    /**
     * This method opens the GitHub URL where OpenSimMPLS users can ask whatever
     * they want to the OpenSimMPLS community.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnAskTheCommunity(ActionEvent evt) {
        if (Desktop.isDesktopSupported()) {
            try {
                URL url = new URL("https://github.com/manolodd/opensimmpls/issues/new?labels=From%20OpenSimMPLS");
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException ex) {
                JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
                // FIX: i18N needed
                errorWindow.setErrorMessage("Cannot open the community URL");
                errorWindow.setVisible(true);
            } catch (URISyntaxException ex) {
                JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
                // FIX: i18N needed
                errorWindow.setErrorMessage("Cannot open the community URL");
                errorWindow.setVisible(true);
            }
        } else {
            JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
            // FIX: i18N needed
            errorWindow.setErrorMessage("Cannot open the community URL");
            errorWindow.setVisible(true);
        }
    }

    /**
     * This method opens the OpenSimMPLS GitHub URL.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnContribute(ActionEvent evt) {
        if (Desktop.isDesktopSupported()) {
            try {
                URL url = new URL("https://github.com/manolodd/opensimmpls");
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException ex) {
                JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
                // FIX: i18N needed
                errorWindow.setErrorMessage("Cannot open OpenSimMPLS site in GitHub");
                errorWindow.setVisible(true);
            } catch (URISyntaxException ex) {
                JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
                // FIX: i18N needed
                errorWindow.setErrorMessage("Cannot open OpenSimMPLS site in GitHub");
                errorWindow.setVisible(true);
            }
        } else {
            JErrorWindow errorWindow = new JErrorWindow(this, true, this.imageBroker);
            // FIX: i18N needed
            errorWindow.setErrorMessage("Cannot open OpenSimMPLS site in GitHub");
            errorWindow.setVisible(true);
        }
    }

    /**
     * This method places all open scenarios in horizontal mosaic form.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
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
     * This method places all open scenarios in vertical mosaic form.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
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
     * This method places all open scenarios in cascade form.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
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
     * This method places all open scenarios in icons form.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnIcons(ActionEvent evt) {
        JInternalFrame[] openScenarios = this.desktopPane.getAllFrames();
        try {
            for (int i = 0; i < openScenarios.length; i++) {
                openScenarios[i].setIcon(true);
            }
            openScenarios[0].setSelected(true);
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
    }

    /**
     * This method closes the currently active scenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnCloseScenario(ActionEvent evt) {
        JScenarioWindow activeScenario = (JScenarioWindow) desktopPane.getSelectedFrame();
        if (activeScenario != null) {
            activeScenario.saveBeforeClosing();
            activeScenario.close();
            this.numOpenScenarios--;
        }
        if (this.numOpenScenarios == 0) {
            disableMenuOptions();
        } else {
            if (this.numOpenScenarios == 1) {
                disableViewOptions();
            }
            JInternalFrame[] openScenarios = this.desktopPane.getAllFrames();
            try {
                if (openScenarios[0].isIcon()) {
                    openScenarios[0].setIcon(false);
                }
                openScenarios[0].setSelected(true);
            } catch (Exception e) {
                JErrorWindow errorWindow;
                errorWindow = new JErrorWindow(this, true, this.imageBroker);
                errorWindow.setErrorMessage(e.toString());
                errorWindow.setVisible(true);
            }
        }
    }

    /**
     * This method creates a new blank scenario.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnNew(ActionEvent evt) {
        try {
            int scenarioWindowID = this.scenarioWindowIDGenerator.getNew();
            String scenarioWindowTitle = this.translations.getString("TextoUntitled-") + scenarioWindowID;
            this.numOpenScenarios++;
            enableMenuOptions();
            JScenarioWindow newScenario = new JScenarioWindow(this, this.imageBroker, scenarioWindowTitle);
            this.desktopPane.add(newScenario, JLayeredPane.FRAME_CONTENT_LAYER);
            try {
                newScenario.setSelected(true);
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
    }

    /**
     * This method opens a new dialog that shows the OpenSimMPLS license.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnLicense(ActionEvent evt) {
        JLicenseWindow licenseWindow;
        licenseWindow = new JLicenseWindow(this, true, this.imageBroker);
        licenseWindow.setVisible(true);
    }

    /**
     * This method opens a new dialog that shows information about OpenSimMPLS:
     * version, name, status, author...
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnAbout(ActionEvent evt) {
        JAboutWindow aboutWindow;
        aboutWindow = new JAboutWindow(this, true, this.imageBroker);
        aboutWindow.setVisible(true);
    }

    /**
     * This method closes all open scenarios.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void closeAll() {
        while (this.numOpenScenarios > 0) {
            JScenarioWindow activeScenario = (JScenarioWindow) this.desktopPane.getSelectedFrame();
            if (activeScenario != null) {
                activeScenario.saveBeforeClosing();
                activeScenario.close();
                this.numOpenScenarios--;
            }
            if (this.numOpenScenarios > 0) {
                JInternalFrame[] openScenarios = this.desktopPane.getAllFrames();
                if (openScenarios.length > 0) {
                    try {
                        if (openScenarios[0].isIcon()) {
                            openScenarios[0].setIcon(false);
                        }
                        openScenarios[0].setSelected(true);
                    } catch (Exception e) {
                        JErrorWindow errorWindow;
                        errorWindow = new JErrorWindow(this, true, this.imageBroker);
                        errorWindow.setErrorMessage(e.toString());
                        errorWindow.setVisible(true);
                    }
                } else {
                    this.numOpenScenarios = 0;
                }
            }
        }
    }

    /**
     * This method exit OpenSimMPLS when the "Exit" option are selected.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnExit(ActionEvent evt) {
        clickOnAnyExitOption();
    }

    /**
     * This method exit OpenSimMPLS independently on whether the user closed the
     * windows or chose the "Exit" option.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void clickOnAnyExitOption() {
        JDecissionWindow decissionWindow = new JDecissionWindow(this, true, this.imageBroker);
        decissionWindow.setQuestion(this.translations.getString("JSimulador.PreguntaSalirDelSimulador"));
        decissionWindow.setVisible(true);
        boolean userAnswer = decissionWindow.getUserAnswer();
        decissionWindow.dispose();
        if (userAnswer) {
            closeAll();
            this.dispose();
        }
    }

    /**
     * This method exit OpenSimMPLS when the OpenSimMPLS window is closed.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleWindowsClosing(WindowEvent evt) {
        clickOnAnyExitOption();
    }

    /**
     * This method disables some menu options that are not needed under certain
     * circumstances.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void disableMenuOptions() {
        this.menuItemClose.setEnabled(false);
        this.menuItemSaveAs.setEnabled(false);
        this.menuItemSave.setEnabled(false);
        disableViewOptions();
    }

    /**
     * This method enables some menu options that are needed under certain
     * circumstances.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void enableMenuOptions() {
        this.menuItemClose.setEnabled(true);
        this.menuItemSaveAs.setEnabled(true);
        this.menuItemSave.setEnabled(true);
        enableViewOptions();
    }

    /**
     * This method disables some view options that are not needed under certain
     * circumstances.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void disableViewOptions() {
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
     * This method enables some view options that are not needed under certain
     * circumstances.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void enableViewOptions() {
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
    private TIDGenerator scenarioWindowIDGenerator;
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
