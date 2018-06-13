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
 * limitations under the license.
 */
package com.manolodominguez.opensimmpls.ui.simulator;

import com.manolodominguez.opensimmpls.hardware.timer.EProgressEventGeneratorOnlyAllowASingleListener;
import com.manolodominguez.opensimmpls.hardware.timer.TTimestamp;
import com.manolodominguez.opensimmpls.io.osm.TOSMSaver;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.scenario.TExternalLink;
import com.manolodominguez.opensimmpls.scenario.TInternalLink;
import com.manolodominguez.opensimmpls.scenario.TActiveLERNode;
import com.manolodominguez.opensimmpls.scenario.TLERNode;
import com.manolodominguez.opensimmpls.scenario.TActiveLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLSRNode;
import com.manolodominguez.opensimmpls.scenario.TLinkConfig;
import com.manolodominguez.opensimmpls.scenario.TTrafficSinkNode;
import com.manolodominguez.opensimmpls.scenario.TScenario;
import com.manolodominguez.opensimmpls.scenario.TTrafficGeneratorNode;
import com.manolodominguez.opensimmpls.scenario.TStats;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.scenario.TTopologyElement;
import com.manolodominguez.opensimmpls.scenario.TLink;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.ui.dialogs.JWarningWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JDecissionWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JTrafficGeneratorWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JLinkWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JErrorWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JLERWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JActiveLERWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JLSRWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JActiveLSRWindow;
import com.manolodominguez.opensimmpls.ui.dialogs.JTrafficSinkWindow;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import com.manolodominguez.opensimmpls.ui.utils.JOSMFilter;
import com.manolodominguez.opensimmpls.ui.utils.TProgressEventListener;
import com.manolodominguez.opensimmpls.ui.utils.JScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class implements a window that contains all needed to design, simulate
 * an analyse a simulation scenario.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JScenarioWindow extends JInternalFrame {

    /**
     * This is the constructor of the class and creates a new instance of
     * JScenarioWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param parent Parent window over wich this JScenarioWindow is shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param title The initial title of the window
     * @since 2.0
     */
    public JScenarioWindow(JOpenSimMPLS parent, TImageBroker imageBroker, String title) {
        this.imageBroker = imageBroker;
        this.parent = parent;
        initComponents();
        initComponents2();
        this.title = title;
    }

    /**
     * This method is called from within the constructor to initialize the
     * window components.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.SCENARIO_WINDOW.getPath());
        GridBagConstraints gridBagConstraints;
        this.popupMenuTopologyElement = new JPopupMenu();
        this.menuItemDeleteElement = new JMenuItem();
        this.chekBoxMenuItemShowElementName = new JCheckBoxMenuItem();
        this.separatorPopupMenuTopologyElement = new JSeparator();
        this.menuItemItemElementProperties = new JMenuItem();
        this.popupMenuBackgroundDesignPanel = new JPopupMenu();
        this.menuItemShowNodesNames = new JMenuItem();
        this.menuItemHideNodesNames = new JMenuItem();
        this.menuItemShowLinksNames = new JMenuItem();
        this.menuItemHideLinksNames = new JMenuItem();
        this.separatorPopupMenuBackgroundDesignPanel = new JSeparator();
        this.menuItemDeleteAllItems = new JMenuItem();
        this.tabsPanel = new JTabbedPane();
        this.designMainContainerPanel = new JPanel();
        this.designToolbarPanel = new JPanel();
        this.iconContainerTrafficGeneratorNode = new JLabel();
        this.iconContainerTrafficSink = new JLabel();
        this.iconContainerLER = new JLabel();
        this.iconContainerActiveLER = new JLabel();
        this.iconContainerLSR = new JLabel();
        this.iconContainerActiveLSR = new JLabel();
        this.iconContainerLink = new JLabel();
        this.scrollPaneDesign = new JScrollPane();
        this.designPanel = new JDesignPanel();
        this.simulationMainContainerPanel = new JPanel();
        this.simulationToolbarPanel = new JPanel();
        this.iconContainterStartSimulation = new JLabel();
        this.iconContainerStopSimulation = new JLabel();
        this.iconContainerResumeSimulation = new JLabel();
        this.iconContainerPauseSimulation = new JLabel();
        this.progressBarSimulation = new JProgressBar();
        this.sliderSimulationSpeedInMsPerTick = new JSlider();
        this.labelSimulationSpeedFaster = new JLabel();
        this.labelSimulationSpeedSlower = new JLabel();
        this.scrollPaneSimulation = new JScrollPane();
        this.simulationPanel = new JSimulationPanel();
        this.analysisMainContainerPanel = new JPanel();
        this.analysisToolbarPanel = new JPanel();
        this.labelSelectANodeToAnalyze = new JLabel();
        this.comboBoxNodeToAnalize = new JComboBox();
        this.scrollPaneAnalysis = new JScrollPane();
        this.analysisPanel = new JScrollablePanel();
        this.labelScenarioTitle = new JLabel();
        this.labelScenarioAuthorName = new JLabel();
        this.textAreaScenarioDescription = new JTextArea();
        this.labelElementToAnalize = new JLabel();
        this.optionsPrimaryMainContainerPanel = new JPanel();
        this.optionsSecondaryMainContainerPanel = new JPanel();
        this.optionsScenarioInformationPanel = new JPanel();
        this.labelOptionsScenarioTitle = new JLabel();
        this.textFieldOptionsScenarioTitle = new JTextField();
        this.labelOptionsScenarioAuthorName = new JLabel();
        this.textFieldOptionsScenarioAuthorName = new JTextField();
        this.labelOptionsScenarioDescription = new JLabel();
        this.textAreaOptionsScenarioDescription = new JTextArea();
        this.optionsSimulationTimingPanel = new JPanel();
        this.labelOptionsSimulationLength = new JLabel();
        this.sliderOptionsSimulationLengthMs = new JSlider();
        this.labelOptionsSimulationLengthMs = new JLabel();
        this.sliderOptionsSimulationLengthNs = new JSlider();
        this.labelOptionsSimulationLengthNs = new JLabel();
        this.labelOptionsTickLengthInNs = new JLabel();
        this.sliderOptionsTickDurationInNs = new JSlider();
        this.labelOptionsNsTick = new JLabel();
        // Definition of popup menu showed when righ-click on an element in 
        // design panel
        // FIX: Do not use harcoded values. Use class constants instead
        this.popupMenuTopologyElement.setFont(new Font("Dialog", 0, 12));
        // FIX: Do not use harcoded values. Use class constants instead
        this.menuItemDeleteElement.setFont(new Font("Dialog", 0, 12));
        this.menuItemDeleteElement.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.Delete").charAt(0));
        this.menuItemDeleteElement.setText(this.translations.getString("VentanaHija.PopUpDisenio.Delete"));
        this.menuItemDeleteElement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPupupDesignPanelRemove(evt);
            }
        });
        this.popupMenuTopologyElement.add(this.menuItemDeleteElement);
        // FIX: Do not use harcoded values. Use class constants instead
        this.chekBoxMenuItemShowElementName.setFont(new Font("Dialog", 0, 12));
        this.chekBoxMenuItemShowElementName.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.verNombre").charAt(0));
        this.chekBoxMenuItemShowElementName.setText(this.translations.getString("VentanaHija.PopUpDisenio.verNombre"));
        this.chekBoxMenuItemShowElementName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPupupDesignPanelShowName(evt);
            }
        });
        this.popupMenuTopologyElement.add(this.chekBoxMenuItemShowElementName);
        this.popupMenuTopologyElement.add(this.separatorPopupMenuTopologyElement);
        // FIX: Do not use harcoded values. Use class constants instead
        this.menuItemItemElementProperties.setFont(new Font("Dialog", 0, 12));
        this.menuItemItemElementProperties.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.Propiedades").charAt(0));
        this.menuItemItemElementProperties.setText(this.translations.getString("VentanaHija.PopUpDisenio.Propiedades"));
        this.menuItemItemElementProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPupupElementProperties(evt);
            }
        });
        this.popupMenuTopologyElement.add(this.menuItemItemElementProperties);
        // Definition of opup menu showed when righ-click on background in 
        // design panel
        // FIX: Do not use harcoded values. Use class constants instead
        this.popupMenuBackgroundDesignPanel.setFont(new Font("Dialog", 0, 12));
        // FIX: Do not use harcoded values. Use class constants instead
        this.menuItemShowNodesNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemShowNodesNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.verTodosNodos").charAt(0));
        this.menuItemShowNodesNames.setText(this.translations.getString("popUpDisenioFondo.verTodosNodos"));
        this.menuItemShowNodesNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPupupDesignPanelShowNodeNames(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemShowNodesNames);
        // FIX: Do not use harcoded values. Use class constants instead
        this.menuItemHideNodesNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemHideNodesNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.ocultarTodosNodos").charAt(0));
        this.menuItemHideNodesNames.setText(this.translations.getString("popUpDisenioFondo.ocultarTodosNodos"));
        this.menuItemHideNodesNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPupupDesignPanelHideNodeNames(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemHideNodesNames);
        // FIX: Do not use harcoded values. Use class constants instead
        this.menuItemShowLinksNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemShowLinksNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.verTodosEnlaces").charAt(0));
        this.menuItemShowLinksNames.setText(this.translations.getString("popUpDisenioFondo.verTodosEnlaces"));
        this.menuItemShowLinksNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPupupDesignPanelShowLinksNames(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemShowLinksNames);
        // FIX: Do not use harcoded values. Use class constants instead
        this.menuItemHideLinksNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemHideLinksNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.ocultarTodosEnlaces").charAt(0));
        this.menuItemHideLinksNames.setText(this.translations.getString("popUpDisenioFondo.ocultarTodosEnlaces"));
        this.menuItemHideLinksNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPupupDesignPanelHideLinksNames(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemHideLinksNames);
        this.popupMenuBackgroundDesignPanel.add(this.separatorPopupMenuBackgroundDesignPanel);
        // FIX: Do not use harcoded values. Use class constants instead
        this.menuItemDeleteAllItems.setFont(new Font("Dialog", 0, 12));
        this.menuItemDeleteAllItems.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.eliminarTodo").charAt(0));
        this.menuItemDeleteAllItems.setText(this.translations.getString("popUpDisenioFondo.borrarTodo"));
        this.menuItemDeleteAllItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnBackgroundDesignPanelRemoveAll(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemDeleteAllItems);
        // Scenario window properties
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(this.translations.getString("VentanaHija.Titulo"));
        // FIX: Do not use harcoded values. Use class constants instead
        setFont(new Font("Dialog", 0, 12));
        setFrameIcon(this.imageBroker.getImageIcon(TImageBroker.SCENARIO_WINDOW_ICON_MENU));
        // FIX: Do not use harcoded values. Use class constants instead
        setNormalBounds(new Rectangle(10, 10, 100, 100));
        // FIX: Do not use harcoded values. Use class constants instead
        setPreferredSize(new Dimension(100, 100));
        setVisible(true);
        setAutoscrolls(true);
        this.tabsPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // FIX: Do not use harcoded values. Use class constants instead
        this.tabsPanel.setFont(new Font("Dialog", 0, 12));
        // Definition of design tab content
        this.designMainContainerPanel.setLayout(new BorderLayout());
        this.designToolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.designToolbarPanel.setBorder(new EtchedBorder());
        this.iconContainerTrafficGeneratorNode.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_GENERATOR_MENU_COLOR));
        this.iconContainerTrafficGeneratorNode.setToolTipText(this.translations.getString("VentanaHija.Topic.Emisor"));
        this.iconContainerTrafficGeneratorNode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringTrafficGeneratorNodeIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingTrafficGeneratorNodeIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnTrafficGeneratorNodeIcon(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerTrafficGeneratorNode);
        this.iconContainerTrafficSink.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_SINK_MENU_COLOR));
        this.iconContainerTrafficSink.setToolTipText(this.translations.getString("VentanaHija.Topic.Receptor"));
        this.iconContainerTrafficSink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringTrafficSinkNodeIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingTrafficSinkNodeIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnTrafficSinkNodeIcon(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerTrafficSink);
        this.iconContainerLER.setIcon(this.imageBroker.getImageIcon(TImageBroker.LER_MENU_COLOR));
        this.iconContainerLER.setToolTipText(this.translations.getString("VentanaHija.Topic.LER"));
        this.iconContainerLER.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringLERNodeIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingLERNodeIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnLERNodeIcon(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerLER);
        this.iconContainerActiveLER.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LER_MENU_COLOR));
        this.iconContainerActiveLER.setToolTipText(this.translations.getString("VentanaHija.Topic.LERActivo"));
        this.iconContainerActiveLER.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringActiveLERNodeIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingActiveLERNodeIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnActiveLERNodeIcon(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerActiveLER);
        this.iconContainerLSR.setIcon(this.imageBroker.getImageIcon(TImageBroker.LSR_MENU_COLOR));
        this.iconContainerLSR.setToolTipText(this.translations.getString("VentanaHija.Topic.LSR"));
        this.iconContainerLSR.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringLSRNodeIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingLSRNodeIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnLSRNodeIcon(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerLSR);
        this.iconContainerActiveLSR.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LSR_MENU_COLOR));
        this.iconContainerActiveLSR.setToolTipText(this.translations.getString("VentanaHija.Topic.LSRActivo"));
        this.iconContainerActiveLSR.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringActiveLSRNodeIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingActiveLSRNodeIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnActiveLSRNodeIcon(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerActiveLSR);
        this.iconContainerLink.setIcon(this.imageBroker.getImageIcon(TImageBroker.LINK_MENU_COLOR));
        this.iconContainerLink.setToolTipText(this.translations.getString("VentanaHija.Topic.Enlace"));
        this.iconContainerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleClickOnLinkIcon(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringLinkIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingLinkIcon(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerLink);
        this.designMainContainerPanel.add(this.designToolbarPanel, BorderLayout.NORTH);
        this.scrollPaneDesign.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.designPanel.setLayout(null);
        this.designPanel.setBackground(Color.white);
        this.designPanel.setBorder(new EtchedBorder());
        this.designPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleRightClickOnDesignPanel(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleMousePressedOnDesignPanel(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                handleMouseReleasedOnDesignPanel(evt);
            }
        });
        this.designPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                handleDragOnDesignPanel(evt);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                handleMouseMovedOnDesignPanel(evt);
            }
        });
        this.scrollPaneDesign.setViewportView(this.designPanel);
        this.designMainContainerPanel.add(this.scrollPaneDesign, BorderLayout.CENTER);
        this.tabsPanel.addTab(this.translations.getString("VentanaHija.Tab.Disenio"), this.imageBroker.getImageIcon(TImageBroker.DESIGN), this.designMainContainerPanel, this.translations.getString("VentanaHija.A_panel_to_design_network_topology"));
        // Definition of options panel content
        this.optionsPrimaryMainContainerPanel.setLayout(new BorderLayout());
        this.optionsPrimaryMainContainerPanel.setBorder(new EtchedBorder());
        this.optionsSecondaryMainContainerPanel.setLayout(new GridBagLayout());
        this.optionsScenarioInformationPanel.setLayout(new GridBagLayout());
        this.optionsScenarioInformationPanel.setBorder(new TitledBorder(null, this.translations.getString("VentanaHija.GParameters"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsScenarioTitle.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsScenarioTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsScenarioTitle.setText(this.translations.getString("VentanaHija.Scene_title"));
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridwidth = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridheight = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 0.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.labelOptionsScenarioTitle, gridBagConstraints);
        this.textFieldOptionsScenarioTitle.setToolTipText(this.translations.getString("VentanaHija.Type_a__title_of_the_scene"));
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 1.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.textFieldOptionsScenarioTitle, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsScenarioAuthorName.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsScenarioAuthorName.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsScenarioAuthorName.setText(this.translations.getString("VentanaHija.Scene_author"));
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridwidth = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridheight = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 0.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.labelOptionsScenarioAuthorName, gridBagConstraints);
        this.textFieldOptionsScenarioAuthorName.setToolTipText(this.translations.getString("VentanaHija.Type_de_name_of_the_author"));
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 1.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.textFieldOptionsScenarioAuthorName, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsScenarioDescription.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsScenarioDescription.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsScenarioDescription.setText(this.translations.getString("VentanaHija.Description"));
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 2;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridwidth = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridheight = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.labelOptionsScenarioDescription, gridBagConstraints);
        this.textAreaOptionsScenarioDescription.setToolTipText(this.translations.getString("VentanaHija.Enter_a_short_description."));
        this.textAreaOptionsScenarioDescription.setLineWrap(true);
        this.textAreaOptionsScenarioDescription.setWrapStyleWord(true);
        // FIX: Do not use harcoded values. Use class constants instead
        this.textAreaOptionsScenarioDescription.setRows(3);
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 1.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.textAreaOptionsScenarioDescription, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSecondaryMainContainerPanel.add(this.optionsScenarioInformationPanel, gridBagConstraints);
        this.optionsSimulationTimingPanel.setLayout(new GridBagLayout());
        this.optionsSimulationTimingPanel.setBorder(new TitledBorder(null, this.translations.getString("VentanaHija.TParameters"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLength.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsSimulationLength.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsSimulationLength.setText(this.translations.getString("VentanaHija.Duration"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 100.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsSimulationLength, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setMajorTickSpacing(2);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setMaximum(2);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setMinorTickSpacing(1);
        this.sliderOptionsSimulationLengthMs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_ms._component_of_simulation_duration."));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setValue(0);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setMaximumSize(new Dimension(30, 20));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setMinimumSize(new Dimension(30, 24));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setPreferredSize(new Dimension(30, 20));
        this.sliderOptionsSimulationLengthMs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnSimulationLengthMs(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 150.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.sliderOptionsSimulationLengthMs, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthMs.setFont(new Font("Dialog", 0, 10));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthMs.setForeground(new Color(102, 102, 102));
        this.labelOptionsSimulationLengthMs.setText(this.translations.getString("VentanaHija.ms."));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthMs.setMaximumSize(new Dimension(30, 14));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthMs.setMinimumSize(new Dimension(30, 14));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthMs.setPreferredSize(new Dimension(30, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 40.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsSimulationLengthMs, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setMajorTickSpacing(1000);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setMaximum(999999);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setMinorTickSpacing(100);
        this.sliderOptionsSimulationLengthNs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_ns._component_of_simulation_duration."));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setValue(100000);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setMaximumSize(new Dimension(32767, 20));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setMinimumSize(new Dimension(36, 20));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setPreferredSize(new Dimension(200, 20));
        this.sliderOptionsSimulationLengthNs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnSimulationLengthNs(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 150.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.sliderOptionsSimulationLengthNs, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthNs.setFont(new Font("Dialog", 0, 10));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthNs.setForeground(new Color(102, 102, 102));
        this.labelOptionsSimulationLengthNs.setText(this.translations.getString("VentanaHija.ns."));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthNs.setMaximumSize(new Dimension(40, 14));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthNs.setMinimumSize(new Dimension(40, 14));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsSimulationLengthNs.setPreferredSize(new Dimension(40, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 100.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsSimulationLengthNs, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsTickLengthInNs.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsTickLengthInNs.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsTickLengthInNs.setText(this.translations.getString("VentanaHija.Step"));
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 100.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsTickLengthInNs, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setMajorTickSpacing(1000);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setMaximum(999999);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setMinimum(1);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setMinorTickSpacing(100);
        this.sliderOptionsTickDurationInNs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_step_duration_(ns).."));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setValue(10000);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setMaximumSize(new Dimension(32767, 20));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setPreferredSize(new Dimension(100, 20));
        this.sliderOptionsTickDurationInNs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnTickDurationInNs(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 1;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.sliderOptionsTickDurationInNs, gridBagConstraints);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsNsTick.setFont(new Font("Dialog", 0, 10));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsNsTick.setForeground(new Color(102, 102, 102));
        this.labelOptionsNsTick.setText(this.translations.getString("VentanaHija.ns."));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsNsTick.setMaximumSize(new Dimension(40, 14));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsNsTick.setMinimumSize(new Dimension(40, 14));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelOptionsNsTick.setPreferredSize(new Dimension(40, 14));
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 4;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 1.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsNsTick, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridx = 0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.weightx = 1.0;
        // FIX: Do not use harcoded values. Use class constants instead
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSecondaryMainContainerPanel.add(this.optionsSimulationTimingPanel, gridBagConstraints);
        this.optionsPrimaryMainContainerPanel.add(this.optionsSecondaryMainContainerPanel, BorderLayout.NORTH);
        this.tabsPanel.addTab(this.translations.getString("VentanaHija.Options"), imageBroker.getImageIcon(TImageBroker.OPTIONS), this.optionsPrimaryMainContainerPanel, this.translations.getString("VentanaHija.Options_about_the_scene"));
        // Definition of simulation panel content
        this.simulationMainContainerPanel.setLayout(new BorderLayout());
        this.simulationToolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.simulationToolbarPanel.setBorder(new EtchedBorder());
        this.iconContainterStartSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.GENERATE_SIMULATION_COLOR));
        this.iconContainterStartSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Generar"));
        this.iconContainterStartSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringStartIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingStartIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnStartIcon(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainterStartSimulation);
        this.iconContainerStopSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.STOP_SIMULATION_COLOR));
        this.iconContainerStopSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Finalizar"));
        this.iconContainerStopSimulation.setEnabled(false);
        this.iconContainerStopSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringStopIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingStopIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnStopIcon(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainerStopSimulation);
        this.iconContainerResumeSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.START_SIMULATION_COLOR));
        this.iconContainerResumeSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Simulacion"));
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringResumeIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingResumeIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnResumeIcon(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainerResumeSimulation);
        this.iconContainerPauseSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.PAUSE_SIMULATION_COLOR));
        this.iconContainerPauseSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Detener"));
        this.iconContainerPauseSimulation.setEnabled(false);
        this.iconContainerPauseSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringPauseIcon(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseExitingPauseIcon(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleClickOnPauseIcon(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainerPauseSimulation);
        // FIX: Do not use harcoded values. Use class constants instead
        this.progressBarSimulation.setFont(new Font("Dialog", 0, 12));
        this.progressBarSimulation.setToolTipText(this.translations.getString("VentanaHija.BarraProgreso.tooltip"));
        this.progressBarSimulation.setStringPainted(true);
        this.simulationToolbarPanel.add(this.progressBarSimulation);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelSimulationSpeedFaster.setFont(new Font("Dialog", 0, 10));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelSimulationSpeedFaster.setForeground(new Color(102, 102, 102));
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.simulationToolbarPanel.add(this.labelSimulationSpeedFaster);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderSimulationSpeedInMsPerTick.setMajorTickSpacing(10);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderSimulationSpeedInMsPerTick.setMaximum(300);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderSimulationSpeedInMsPerTick.setMinimum(1);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderSimulationSpeedInMsPerTick.setMinorTickSpacing(1);
        this.sliderSimulationSpeedInMsPerTick.setSnapToTicks(true);
        this.sliderSimulationSpeedInMsPerTick.setToolTipText(this.translations.getString("VentanaHija.Simulacion.SelectorDeVelocidad.tooltip"));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderSimulationSpeedInMsPerTick.setPreferredSize(new Dimension(100, 20));
        this.sliderSimulationSpeedInMsPerTick.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handelChangeInSimulationSpeedInMsPerTick(evt);
            }
        });
        this.simulationToolbarPanel.add(this.sliderSimulationSpeedInMsPerTick);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelSimulationSpeedSlower.setFont(new Font("Dialog", 0, 10));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelSimulationSpeedSlower.setForeground(new Color(102, 102, 102));
        this.labelSimulationSpeedSlower.setText(this.translations.getString("VentanaHija.Simulacion.slower"));
        this.simulationToolbarPanel.add(this.labelSimulationSpeedSlower);
        this.simulationMainContainerPanel.add(this.simulationToolbarPanel, BorderLayout.NORTH);
        this.scrollPaneSimulation.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.simulationPanel.setBorder(new EtchedBorder());
        this.simulationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleClickOnSimulationPanel(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                handleMousePressedOnSimulationPanel(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                handleMouseReleasedOnSimulationPanel(evt);
            }
        });
        this.simulationPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                handleDragOnSimulationPanel(evt);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                handleMouseMovedOnSimulationPanel(evt);
            }
        });
        this.scrollPaneSimulation.setViewportView(this.simulationPanel);
        this.simulationMainContainerPanel.add(this.scrollPaneSimulation, BorderLayout.CENTER);
        this.tabsPanel.addTab(this.translations.getString("VentanaHija.Tab.Simulacion"), this.imageBroker.getImageIcon(TImageBroker.SIMULATION), this.simulationMainContainerPanel, this.translations.getString("VentanaHija.A_panel_to_generate_and_play_simulation."));
        // Definition of analysis panel content
        this.analysisMainContainerPanel.setLayout(new BorderLayout());
        this.analysisMainContainerPanel.setBackground(Color.WHITE);
        this.analysisToolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.analysisToolbarPanel.setBorder(new EtchedBorder());
        this.labelSelectANodeToAnalyze.setText(this.translations.getString("JVentanaHija.SelcUnElemParaVerDatos"));
        this.analysisToolbarPanel.add(this.labelSelectANodeToAnalyze);
        this.comboBoxNodeToAnalize.setModel(new DefaultComboBoxModel(new String[]{""}));
        this.comboBoxNodeToAnalize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnNodeToBeAnalized(evt);
            }
        });
        this.analysisToolbarPanel.add(this.comboBoxNodeToAnalize);
        this.analysisMainContainerPanel.add(this.analysisToolbarPanel, BorderLayout.PAGE_START);
        this.scrollPaneAnalysis.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.analysisPanel.setBorder(new EtchedBorder());
        this.analysisPanel.setLayout(new MigLayout());
        this.analysisPanel.setBackground(Color.WHITE);
        this.labelScenarioTitle.setBackground(Color.WHITE);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelScenarioTitle.setFont(new Font("Serif", 1, 18));
        this.labelScenarioTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelScenarioTitle.setText(this.translations.getString("JVentanaHija.TituloDelEscenario"));
        this.analysisPanel.add(this.labelScenarioTitle, "span 2, grow, wrap");
        this.labelScenarioAuthorName.setBackground(Color.WHITE);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelScenarioAuthorName.setFont(new Font("Serif", 1, 14));
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelScenarioAuthorName.setForeground(new Color(102, 0, 51));
        this.labelScenarioAuthorName.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelScenarioAuthorName.setText(this.translations.getString("JVentanaHija.AutorDelEscenario"));
        this.analysisPanel.add(this.labelScenarioAuthorName, "span 2, grow, wrap");
        this.textAreaScenarioDescription.setBackground(Color.WHITE);
        this.textAreaScenarioDescription.setEditable(false);
        // FIX: Do not use harcoded values. Use class constants instead
        this.textAreaScenarioDescription.setFont(new Font("MonoSpaced", 0, 11));
        this.textAreaScenarioDescription.setLineWrap(true);
        this.textAreaScenarioDescription.setText(this.translations.getString("JVentanaHija.DescripcionDelEscenario"));
        this.textAreaScenarioDescription.setWrapStyleWord(true);
        this.textAreaScenarioDescription.setBorder(null);
        this.textAreaScenarioDescription.setAutoscrolls(false);
        this.analysisPanel.add(this.textAreaScenarioDescription, "span 2, grow, wrap");
        this.labelElementToAnalize.setBackground(Color.WHITE);
        // FIX: Do not use harcoded values. Use class constants instead
        this.labelElementToAnalize.setFont(new Font("Serif", 1, 14));
        this.labelElementToAnalize.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelElementToAnalize.setText(this.translations.getString("JVentanaHija.SeleccioneNodoAInspeccionar"));
        this.analysisPanel.add(this.labelElementToAnalize, "span 2, grow, wrap");
        this.scrollPaneAnalysis.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPaneAnalysis.setViewportView(this.analysisPanel);
        this.analysisMainContainerPanel.add(this.scrollPaneAnalysis, BorderLayout.CENTER);
        this.tabsPanel.addTab(this.translations.getString("JVentanaHija.Analisis"), this.imageBroker.getImageIcon(TImageBroker.ANALYSIS), this.analysisMainContainerPanel, this.translations.getString("JVentanaHija.Analisis.Tooltip"));
        this.tabsPanel.setBackground(Color.WHITE);
        getContentPane().add(this.tabsPanel, BorderLayout.CENTER);
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
        this.designPanel.setImageBroker(this.imageBroker);
        this.simulationPanel.setImageBroker(this.imageBroker);
        Dimension parentSize = this.parent.getSize();
        // FIX: Do not use harcoded values. Use class constants instead
        this.setSize((parentSize.width * 8 / 10), (parentSize.height * 8 / 10));
        Dimension frameSize = this.getSize();
        this.setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);
        this.scenario = new TScenario();
        this.designPanel.setTopology(this.scenario.getTopology());
        this.simulationPanel.setTopology(this.scenario.getTopology());
        this.selectedNode = null;
        this.rightClickedElementInDesignPanel = null;
        this.progressEventListener = new TProgressEventListener(this.progressBarSimulation);
        try {
            this.scenario.getTopology().getTimer().addProgressEventListener(this.progressEventListener);
        } catch (EProgressEventGeneratorOnlyAllowASingleListener e) {
            // FIX: This is ugly
            e.printStackTrace();
        }
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderSimulationSpeedInMsPerTick.setValue(150);
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsTickDurationInNs.setMaximum(this.sliderOptionsSimulationLengthMs.getValue() * 1000000 + this.sliderOptionsSimulationLengthNs.getValue());
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.labelOptionsSimulationLengthMs.setText(this.sliderOptionsSimulationLengthMs.getValue() + this.translations.getString("VentanaHija._ms."));
        this.labelOptionsSimulationLengthNs.setText(this.sliderOptionsSimulationLengthNs.getValue() + this.translations.getString("VentanaHija._ns."));
        this.labelOptionsNsTick.setText(this.sliderOptionsTickDurationInNs.getValue() + this.translations.getString("VentanaHija_ns."));
        this.timingControlDisabled = false;
        this.scenario.getSimulation().setSimulationPanel(this.simulationPanel);
        this.xyChart1 = null;
        this.xyChart2 = null;
        this.xyChart3 = null;
        this.barChart1 = null;
        this.barChart2 = null;
        this.fillAnalysisInformation();
    }

    /**
     * This method is called when the user clic on the simulation panel.
     * Everything that can be done with a clic on simulation panel is here.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnSimulationPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopologyElement topologyElement = this.scenario.getTopology().getElementInScreenPosition(evt.getPoint());
            if (topologyElement != null) {
                if (topologyElement.getElementType() == TTopologyElement.NODE) {
                    TNode node = (TNode) topologyElement;
                    if (node.getPorts().isCongestedArtificially()) {
                        node.getPorts().setArtificiallyCongested(false);
                    } else {
                        node.getPorts().setArtificiallyCongested(true);
                    }
                } else if (topologyElement.getElementType() == TTopologyElement.LINK) {
                    TLink link = (TLink) topologyElement;
                    if (link.isBroken()) {
                        link.setAsBrokenLink(false);
                    } else {
                        link.setAsBrokenLink(true);
                    }
                }
            } else if (this.simulationPanel.getShowLegend()) {
                this.simulationPanel.setShowLegend(false);
            } else {
                this.simulationPanel.setShowLegend(true);
            }
        } else {
            this.rightClickedElementInDesignPanel = null;
            this.designPanel.repaint();
        }
    }

    /**
     * This method is called when the user clic on the list of elements to be
     * analyzed, in the analysis panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnNodeToBeAnalized(ActionEvent evt) {
        if (this.comboBoxNodeToAnalize.getSelectedIndex() == 0) {
            this.fillAnalysisInformation();
        } else {
            String nameOfSelectedNode = (String) this.comboBoxNodeToAnalize.getSelectedItem();
            this.fillAnalysisInformation(nameOfSelectedNode);
        }
    }

    /**
     * This method is called when the user drag the mouse on the simulation
     * panel. Everything that can be done dragging the mouse in the simulation
     * panel is here.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleDragOnSimulationPanel(MouseEvent evt) {
        if (evt.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (this.selectedNode != null) {
                Point mousePosition = evt.getPoint();
                if (mousePosition.x < 0) {
                    // FIX: Do not use harcoded values. Use class constants instead
                    mousePosition.x = 0;
                }
                if (mousePosition.x > this.designPanel.getSize().width) {
                    mousePosition.x = this.designPanel.getSize().width;
                }
                if (mousePosition.y < 0) {
                    // FIX: Do not use harcoded values. Use class constants instead
                    mousePosition.y = 0;
                }
                if (mousePosition.y > this.designPanel.getSize().height) {
                    mousePosition.y = this.designPanel.getSize().height;
                }
                this.selectedNode.setScreenPosition(new Point(mousePosition.x, mousePosition.y));
                this.simulationPanel.repaint();
                this.scenario.setModified(true);
            }
        }
    }

    /**
     * This method is called when a mouse button is released on the simulation
     * panel. Everything that can be done releasing a mouse button in the
     * simulation panel is here (usually, the en of a drag action).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseReleasedOnSimulationPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (this.selectedNode != null) {
                this.selectedNode.setSelected(TNode.UNSELECTED);
                this.selectedNode = null;
                this.scenario.setModified(true);
            }
            this.simulationPanel.repaint();
        }
    }

    /**
     * This method is called when a mouse button is pressed on the simulation
     * panel. Everything that can be done pressing a mouse button in the
     * simulation panel is here (pressing, without releasing).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMousePressedOnSimulationPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopology topology = this.scenario.getTopology();
            TTopologyElement topologyElement = topology.getElementInScreenPosition(evt.getPoint());
            if (topologyElement != null) {
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (topologyElement.getElementType() == TTopologyElement.NODE) {
                    TNode node = (TNode) topologyElement;
                    this.selectedNode = node;
                    if (this.selectedNode != null) {
                        this.selectedNode.setSelected(TNode.SELECTED);
                        this.scenario.setModified(true);
                    }
                }
            } else {
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                this.setToolTipText(null);
            }
            this.simulationPanel.repaint();
        }
    }

    /**
     * This method is called when the user does click on "Properties" option
     * that is shown when a right click on a element is done in design panel.
     * This opens the window to configure the topology element.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPupupElementProperties(ActionEvent evt) {
        if (this.rightClickedElementInDesignPanel != null) {
            if (this.rightClickedElementInDesignPanel.getElementType() == TTopologyElement.NODE) {
                TNode node = (TNode) this.rightClickedElementInDesignPanel;
                if (node.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                    JTrafficGeneratorWindow trafficGeneratorWindow = new JTrafficGeneratorWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    trafficGeneratorWindow.setConfiguration((TTrafficGeneratorNode) node, true);
                    trafficGeneratorWindow.setVisible(true);
                } else if (node.getNodeType() == TNode.LER) {
                    JLERWindow lerWindow = new JLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    lerWindow.setConfiguration((TLERNode) node, true);
                    lerWindow.setVisible(true);
                } else if (node.getNodeType() == TNode.ACTIVE_LER) {
                    JActiveLERWindow activeLERWindow = new JActiveLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    activeLERWindow.setConfiguration((TActiveLERNode) node, true);
                    activeLERWindow.setVisible(true);
                } else if (node.getNodeType() == TNode.LSR) {
                    JLSRWindow lsrWindow = new JLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    lsrWindow.setConfiguration((TLSRNode) node, true);
                    lsrWindow.setVisible(true);
                } else if (node.getNodeType() == TNode.ACTIVE_LSR) {
                    JActiveLSRWindow activeLSRWindow = new JActiveLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    activeLSRWindow.setConfiguration((TActiveLSRNode) node, true);
                    activeLSRWindow.setVisible(true);
                } else if (node.getNodeType() == TNode.TRAFFIC_SINK) {
                    JTrafficSinkWindow trafficSinkWindow = new JTrafficSinkWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    trafficSinkWindow.setConfiguration((TTrafficSinkNode) node, true);
                    trafficSinkWindow.setVisible(true);
                }
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
            } else {
                TLink link = (TLink) this.rightClickedElementInDesignPanel;
                TLinkConfig linkConfig = link.getConfig();
                JLinkWindow linkWindow = new JLinkWindow(this.scenario.getTopology(), this.imageBroker, this.parent, true);
                linkWindow.setConfiguration(linkConfig, true);
                linkWindow.setVisible(true);
                if (link.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink externalLink = (TExternalLink) link;
                    externalLink.configure(linkConfig, this.scenario.getTopology(), true);
                } else if (link.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink internalLink = (TInternalLink) link;
                    internalLink.configure(linkConfig, this.scenario.getTopology(), true);
                }
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
                int minDelay = this.scenario.getTopology().getMinimumDelay();
                int tickDurationInNs = this.sliderOptionsTickDurationInNs.getValue();
                if (tickDurationInNs > minDelay) {
                    this.sliderOptionsTickDurationInNs.setValue(minDelay);
                }
            }
            this.scenario.setModified(true);
        }
    }

    /**
     * This method takes cares of timming options. By default the minimum tick
     * duration has to be equal or greater thant the minimum delay of all links
     * in the topology. As the link delay can be changed after establishing the
     * tick duration, this method adjust the tick duration to assure this fact.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void controlTimingOptions() {
        if (!this.timingControlDisabled) {
            if (this.sliderOptionsSimulationLengthMs.getValue() == 0) {
                this.sliderOptionsSimulationLengthNs.setMinimum(1);
            } else {
                this.sliderOptionsSimulationLengthNs.setMinimum(0);
            }
            // FIX: Do not use harcoded values. Use class constants instead
            int totalSimulationLength = this.sliderOptionsSimulationLengthMs.getValue() * 1000000 + this.sliderOptionsSimulationLengthNs.getValue();
            int minDelay = this.scenario.getTopology().getMinimumDelay();
            if (minDelay < totalSimulationLength) {
                this.sliderOptionsTickDurationInNs.setMaximum(minDelay);
                // FIX: Change also current value of this slider
            } else {
                this.sliderOptionsTickDurationInNs.setMaximum(totalSimulationLength);
                // FIX: Change also current value of this slider
            }
            this.labelOptionsSimulationLengthMs.setText(this.sliderOptionsSimulationLengthMs.getValue() + this.translations.getString("VentanaHija._ms."));
            this.labelOptionsSimulationLengthNs.setText(this.sliderOptionsSimulationLengthNs.getValue() + this.translations.getString("VentanaHija._ns."));
            this.labelOptionsNsTick.setText(this.sliderOptionsTickDurationInNs.getValue() + this.translations.getString("VentanaHija._ns."));
            this.scenario.getSimulation().setSimulationLengthInNs(new TTimestamp(this.sliderOptionsSimulationLengthMs.getValue(), this.sliderOptionsSimulationLengthNs.getValue()).getTotalAsNanoseconds());
            this.scenario.getSimulation().setSimulationTickDurationInNs(this.sliderOptionsTickDurationInNs.getValue());
        }
    }

    /**
     * This method is called when the user changes the tick duration of the
     * simulation in the timming options panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleChangeOnTickDurationInNs(ChangeEvent evt) {
        controlTimingOptions();
        this.scenario.setModified(true);
    }

    /**
     * This method is called when the user changes the Ns components of the
     * simulation length in the timming options panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleChangeOnSimulationLengthNs(ChangeEvent evt) {
        controlTimingOptions();
        this.scenario.setModified(true);
    }

    /**
     * This method is called when the user changes the Ms components of the
     * simulation length in the timming options panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleChangeOnSimulationLengthMs(ChangeEvent evt) {
        controlTimingOptions();
        this.scenario.setModified(true);
    }

    /**
     * This method is called when the user changes the simulation speed in the
     * simulation panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handelChangeInSimulationSpeedInMsPerTick(ChangeEvent evt) {
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.etiquetaMsTic"));
        this.simulationPanel.setSimulationSpeedInMsPerTick(this.sliderSimulationSpeedInMsPerTick.getValue());
    }

    /**
     * This method is called when the user does click on "Hide link names"
     * option that is shown when a right click on the background of design panel
     * is done. This hides the name of all links.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPupupDesignPanelHideLinksNames(ActionEvent evt) {
        Iterator linksIterator = this.scenario.getTopology().getLinksIterator();
        TLink link;
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            link.setShowName(false);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * This method is called when the user does click on "Show link names"
     * option that is shown when a right click on the background of design panel
     * is done. This shows the name of all links.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPupupDesignPanelShowLinksNames(ActionEvent evt) {
        Iterator linksIterator = this.scenario.getTopology().getLinksIterator();
        TLink link;
        while (linksIterator.hasNext()) {
            link = (TLink) linksIterator.next();
            link.setShowName(true);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * This method is called when the user does click on "Hide nodes names"
     * option that is shown when a right click on the background of design panel
     * is done. This hides the name of all nodes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPupupDesignPanelHideNodeNames(ActionEvent evt) {
        Iterator nodesIterator = this.scenario.getTopology().getNodesIterator();
        TNode node;
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            node.setShowName(false);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * This method is called when the user does click on "Show nodes names"
     * option that is shown when a right click on the background of design panel
     * is done. This shows the name of all nodes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPupupDesignPanelShowNodeNames(ActionEvent evt) {
        Iterator nodesIterator = this.scenario.getTopology().getNodesIterator();
        TNode node;
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            node.setShowName(true);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * This method is called when the user does click on "Remove all" option
     * that is shown when a right click on the background of design panel is
     * done. This removes all nodes and links of the topology.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnBackgroundDesignPanelRemoveAll(ActionEvent evt) {
        JDecissionWindow decissionWindow = new JDecissionWindow(this.parent, true, this.imageBroker);
        decissionWindow.setQuestion(this.translations.getString("JVentanaHija.PreguntaBorrarTodo"));
        decissionWindow.setVisible(true);
        boolean userAnswer = decissionWindow.getUserAnswer();
        if (userAnswer) {
            this.scenario.getTopology().removeAllElements();
            this.designPanel.repaint();
        }
        this.scenario.setModified(true);
    }

    /**
     * This method sets the scenario of this JScenarioWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param scenario The scenario
     * @since 2.0
     */
    public void setScenario(TScenario scenario) {
        this.timingControlDisabled = true;
        long simulationLength = scenario.getSimulation().getSimulationLengthInNs();
        long simulationTickDurationInNs = scenario.getSimulation().getSimulationTickDurationInNs();
        this.scenario = scenario;
        this.designPanel.setTopology(scenario.getTopology());
        this.simulationPanel.setTopology(scenario.getTopology());
        this.selectedNode = null;
        this.rightClickedElementInDesignPanel = null;
        this.progressEventListener = new TProgressEventListener(this.progressBarSimulation);
        try {
            scenario.getTopology().getTimer().addProgressEventListener(this.progressEventListener);
        } catch (EProgressEventGeneratorOnlyAllowASingleListener e) {
            // FIX: this is ugly
            e.printStackTrace();
        }
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthMs.setValue((int) (simulationLength / 1000000));
        // FIX: Do not use harcoded values. Use class constants instead
        this.sliderOptionsSimulationLengthNs.setValue((int) (simulationLength - (this.sliderOptionsSimulationLengthMs.getValue() * 1000000)));
        this.sliderOptionsTickDurationInNs.setMaximum((int) scenario.getSimulation().getSimulationLengthInNs());
        this.sliderOptionsTickDurationInNs.setValue((int) simulationTickDurationInNs);
        scenario.getSimulation().setSimulationLengthInNs(simulationLength);
        scenario.getSimulation().setSimulationTickDurationInNs(simulationTickDurationInNs);
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.labelOptionsSimulationLengthMs.setText(this.sliderOptionsSimulationLengthMs.getValue() + this.translations.getString("VentanaHija._ms."));
        this.labelOptionsSimulationLengthNs.setText(this.sliderOptionsSimulationLengthNs.getValue() + this.translations.getString("VentanaHija._ns."));
        this.labelOptionsNsTick.setText(this.sliderOptionsTickDurationInNs.getValue() + this.translations.getString("VentanaHija_ns."));
        this.textFieldOptionsScenarioAuthorName.setText(scenario.getAuthor());
        this.textFieldOptionsScenarioAuthorName.setCaretPosition(1);
        this.textFieldOptionsScenarioTitle.setText(scenario.getTitle());
        this.textFieldOptionsScenarioTitle.setCaretPosition(1);
        this.textAreaOptionsScenarioDescription.setText(scenario.getDescription());
        this.textAreaOptionsScenarioDescription.setCaretPosition(1);
        this.timingControlDisabled = false;
        this.scenario.getSimulation().setSimulationPanel(this.simulationPanel);
        this.controlTimingOptions();
    }

    /**
     * This method is called when the user does click on "link icon" in the
     * design panel. It's used to adda new link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnLinkIcon(MouseEvent evt) {
        if (this.scenario.getTopology().getNumberOfNodes() < 2) {
            JWarningWindow warningWindow = new JWarningWindow(this.parent, true, this.imageBroker);
            warningWindow.setWarningMessage(this.translations.getString("VentanaHija.ErrorAlMenosDosNodos"));
            warningWindow.setVisible(true);
        } else {
            TLinkConfig linkConfig = new TLinkConfig();
            JLinkWindow linkWindow = new JLinkWindow(this.scenario.getTopology(), this.imageBroker, this.parent, true);
            linkWindow.setConfiguration(linkConfig, false);
            linkWindow.setVisible(true);
            if (linkConfig.isWellConfigured()) {
                try {
                    if (linkConfig.getLinkType() == TLink.INTERNAL_LINK) {
                        TInternalLink internalLink = new TInternalLink(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
                        internalLink.configure(linkConfig, scenario.getTopology(), false);
                        this.scenario.getTopology().addLink(internalLink);
                    } else {
                        TExternalLink externalLink = new TExternalLink(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
                        externalLink.configure(linkConfig, this.scenario.getTopology(), false);
                        this.scenario.getTopology().addLink(externalLink);
                    }
                    this.designPanel.repaint();
                } catch (Exception e) {
                    JErrorWindow errorWindow;
                    errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                    errorWindow.setErrorMessage(e.toString());
                    errorWindow.setVisible(true);
                }
                this.scenario.setModified(true);
            } else {
                linkConfig = null;
            }
        }
    }

    /**
     * This method is called when the user does click on "Remove" option that is
     * shown when a right click on an element (node or link) is the done in the
     * design panel. This removes the selected element.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPupupDesignPanelRemove(ActionEvent evt) {
        JDecissionWindow decissionWindow = new JDecissionWindow(this.parent, true, this.imageBroker);
        decissionWindow.setQuestion(this.translations.getString("JVentanaHija.preguntaAlEliminar"));
        decissionWindow.setVisible(true);
        boolean userAnswer = decissionWindow.getUserAnswer();
        if (userAnswer) {
            if (this.rightClickedElementInDesignPanel != null) {
                if (this.rightClickedElementInDesignPanel.getElementType() == TTopologyElement.NODE) {
                    TNode node = (TNode) this.rightClickedElementInDesignPanel;
                    if (node.getNodeType() == TNode.TRAFFIC_SINK) {
                        if (this.scenario.getTopology().isThereAnyNodeGeneratingTrafficFor((TTrafficSinkNode) node)) {
                            JWarningWindow warningWindow;
                            warningWindow = new JWarningWindow(this.parent, true, this.imageBroker);
                            warningWindow.setWarningMessage(this.translations.getString("JVentanaHija.NoPuedoBorrarReceptor"));
                            warningWindow.setVisible(true);
                            this.rightClickedElementInDesignPanel = null;
                        } else {
                            this.scenario.getTopology().disconnectNodeAndRemove(node);
                            this.rightClickedElementInDesignPanel = null;
                            this.designPanel.repaint();
                        }
                    } else {
                        this.scenario.getTopology().disconnectNodeAndRemove(node);
                        this.rightClickedElementInDesignPanel = null;
                        this.designPanel.repaint();
                    }
                } else {
                    TLink link = (TLink) this.rightClickedElementInDesignPanel;
                    this.scenario.getTopology().removeLink(link);
                    this.rightClickedElementInDesignPanel = null;
                    this.designPanel.repaint();
                }
                this.scenario.setModified(true);
            }
        }

    }

    /**
     * This method is called when the user does click on "Show name" option that
     * is shown when a right click on an element (node or link) is the done in
     * the design panel. This makes visible the name of this element.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPupupDesignPanelShowName(ActionEvent evt) {
        if (this.rightClickedElementInDesignPanel != null) {
            if (this.rightClickedElementInDesignPanel.getElementType() == TTopologyElement.NODE) {
                TNode node = (TNode) this.rightClickedElementInDesignPanel;
                node.setShowName(this.chekBoxMenuItemShowElementName.isSelected());
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
            } else {
                TLink link = (TLink) this.rightClickedElementInDesignPanel;
                link.setShowName(this.chekBoxMenuItemShowElementName.isSelected());
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
            }
            this.scenario.setModified(true);
        }
    }

    /**
     * This method is called, at a first stage, when the user does right click
     * on the background of the design panel. This makes visible the
     * corresponding popup menu.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleRightClickOnDesignPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            TTopologyElement topologyElement = this.scenario.getTopology().getElementInScreenPosition(evt.getPoint());
            if (topologyElement == null) {
        // FIX: Do not use harcoded values. Use class constants instead
                this.popupMenuBackgroundDesignPanel.show(this, evt.getX() + 7, evt.getY() - 27);
            } else if (topologyElement.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) topologyElement;
                this.chekBoxMenuItemShowElementName.setSelected(nt.getShowName());
                this.rightClickedElementInDesignPanel = topologyElement;
        // FIX: Do not use harcoded values. Use class constants instead
                this.popupMenuTopologyElement.show(this, evt.getX() + 7, evt.getY() + 15);
            } else if (topologyElement.getElementType() == TTopologyElement.LINK) {
                TLink ent = (TLink) topologyElement;
                this.chekBoxMenuItemShowElementName.setSelected(ent.getShowName());
                this.rightClickedElementInDesignPanel = topologyElement;
        // FIX: Do not use harcoded values. Use class constants instead
                this.popupMenuTopologyElement.show(this, evt.getX() + 7, evt.getY() + 15);
            }
        } else {
            this.rightClickedElementInDesignPanel = null;
            this.designPanel.repaint();
        }
    }

    /**
     * This method is called when the user does click on "Active LSR node icon"
     * in the design panel. It's used to add a new Active LSR node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnActiveLSRNodeIcon(MouseEvent evt) {
        TActiveLSRNode activeLSRNode = null;
        try {
            activeLSRNode = new TActiveLSRNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
        JActiveLSRWindow activeLSRWindow = new JActiveLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        activeLSRWindow.setConfiguration(activeLSRNode, false);
        activeLSRWindow.setVisible(true);
        // FIX: the node could not be initilized yet
        if (activeLSRNode.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(activeLSRNode);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow errorWindow;
                errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                errorWindow.setErrorMessage(e.toString());
                errorWindow.setVisible(true);
            };
            this.scenario.setModified(true);
        } else {
            activeLSRNode = null;
        }
    }

    /**
     * This method is called when the user does click on "LSR node icon" in the
     * design panel. It's used to add a new LSR node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnLSRNodeIcon(MouseEvent evt) {
        TLSRNode lsrNode = null;
        try {
            lsrNode = new TLSRNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
        JLSRWindow lsrWindow = new JLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        lsrWindow.setConfiguration(lsrNode, false);
        lsrWindow.setVisible(true);
        // FIX: the node could not be initilized yet
        if (lsrNode.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(lsrNode);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow errorWindow;
                errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                errorWindow.setErrorMessage(e.toString());
                errorWindow.setVisible(true);
            }
            this.scenario.setModified(true);
        } else {
            lsrNode = null;
        }
    }

    /**
     * This method is called when the user does click on "Active LER node icon"
     * in the design panel. It's used to add a new Active LER node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnActiveLERNodeIcon(MouseEvent evt) {
        TActiveLERNode activeLERNode = null;
        try {
            activeLERNode = new TActiveLERNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
        JActiveLERWindow activeLERWindow = new JActiveLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        activeLERWindow.setConfiguration(activeLERNode, false);
        activeLERWindow.setVisible(true);
        // FIX: the node could not be initilized yet
        if (activeLERNode.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(activeLERNode);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow errorWindow;
                errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                errorWindow.setErrorMessage(e.toString());
                errorWindow.setVisible(true);
            }
            this.scenario.setModified(true);
        } else {
            activeLERNode = null;
        }
    }

    /**
     * This method is called when the user moves the mouse over the simulation
     * panel. It is used to show different mouse cursor depending on the element
     * the mouse is on and also to see information about the existing elements.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseMovedOnSimulationPanel(MouseEvent evt) {
        TTopology topology = this.scenario.getTopology();
        TTopologyElement topologyElement = topology.getElementInScreenPosition(evt.getPoint());
        if (topologyElement != null) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (topologyElement.getElementType() == TTopologyElement.NODE) {
                TNode node = (TNode) topologyElement;
                if (node.getPorts().isCongestedArtificially()) {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.Congestion") + node.getPorts().getCongestionLevel() + this.translations.getString("JVentanaHija.POrcentaje") + this.translations.getString("VentanaHija.paraDejarDeCongestionar"));
                } else {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.Congestion") + node.getPorts().getCongestionLevel() + this.translations.getString("JVentanaHija.POrcentaje") + this.translations.getString("VentanaHija.paraCongestionar"));
                }
            } else if (topologyElement.getElementType() == TTopologyElement.LINK) {
                TLink link = (TLink) topologyElement;
                if (link.isBroken()) {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.EnlaceRoto"));
                } else {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.EnlaceFuncionando"));
                }
            }
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.simulationPanel.setToolTipText(null);
            if (!this.simulationPanel.getShowLegend()) {
                this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.VerLeyenda"));
            } else {
                this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.OcultarLeyenda"));
            }
        }
    }

    /**
     * This method is called when the user moves the mouse over the design
     * panel. It is used to show different mouse cursor depending on the element
     * the mouse is on and also to see information about the existing elements.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseMovedOnDesignPanel(MouseEvent evt) {
        TTopology topology = this.scenario.getTopology();
        TTopologyElement topologyElement = topology.getElementInScreenPosition(evt.getPoint());
        if (topologyElement != null) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (topologyElement.getElementType() == TTopologyElement.NODE) {
                TNode node = (TNode) topologyElement;
                this.designPanel.setToolTipText(this.translations.getString("JVentanaHija.PanelDisenio.IP") + node.getIPv4Address());
            } else if (topologyElement.getElementType() == TTopologyElement.LINK) {
                TLink link = (TLink) topologyElement;
                this.designPanel.setToolTipText(this.translations.getString("JVentanaHija.panelDisenio.Retardo") + link.getDelay() + this.translations.getString("JVentanaHija.panelDisenio.ns"));
            }
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.designPanel.setToolTipText(null);
        }
    }

    /**
     * This method is called when the user drag the mouse on the design panel.
     * Everything that can be done dragging the mouse in the design panel is
     * here.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleDragOnDesignPanel(MouseEvent evt) {
        if (evt.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (this.selectedNode != null) {
                Point p2 = evt.getPoint();
                if (p2.x < 0) {
                    // FIX: Do not use harcoded values. Use class constants instead
                    p2.x = 0;
                }
                if (p2.x > this.designPanel.getSize().width) {
                    p2.x = this.designPanel.getSize().width;
                }
                if (p2.y < 0) {
                    // FIX: Do not use harcoded values. Use class constants instead
                    p2.y = 0;
                }
                if (p2.y > this.designPanel.getSize().height) {
                    p2.y = this.designPanel.getSize().height;
                }
                this.selectedNode.setScreenPosition(new Point(p2.x, p2.y));
                this.designPanel.repaint();
                this.scenario.setModified(true);
            }
        }
    }

    /**
     * This method is called when a mouse button is released on the design
     * panel. Everything that can be done releasing a mouse button in the design
     * panel is here (usually, the en of a drag action).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseReleasedOnDesignPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (this.selectedNode != null) {
                this.selectedNode.setSelected(TNode.UNSELECTED);
                this.selectedNode = null;
                this.scenario.setModified(true);
            }
            this.designPanel.repaint();
        }
    }

    /**
     * This method is called when a mouse button is pressed on the design panel.
     * Everything that can be done pressing a mouse button in the design panel
     * is here (pressing, without releasing).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMousePressedOnDesignPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopology topology = this.scenario.getTopology();
            this.selectedNode = topology.getNodeInScreenPosition(evt.getPoint());
            if (this.selectedNode != null) {
                this.selectedNode.setSelected(TNode.SELECTED);
                this.scenario.setModified(true);
            }
            this.designPanel.repaint();
        }
    }

    /**
     * This method is called when the mouse exits the "Pause" icon in the
     * simulation panel. It is used to restore the default pause icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingPauseIcon(MouseEvent evt) {
        this.iconContainerPauseSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.PAUSE_SIMULATION_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "Pause" icon in the
     * simulation panel. It is used to show a highlighted pause icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringPauseIcon(MouseEvent evt) {
        this.iconContainerPauseSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.PAUSE_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "Stop" icon in the
     * simulation panel. It is used to restore the default stop icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingStopIcon(MouseEvent evt) {
        this.iconContainerStopSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.STOP_SIMULATION_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "Stop" icon in the
     * simulation panel. It is used to show a highlighted stop icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringStopIcon(MouseEvent evt) {
        this.iconContainerStopSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.STOP_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "Resume" icon in the
     * simulation panel. It is used to restore the default resume icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingResumeIcon(MouseEvent evt) {
        this.iconContainerResumeSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.START_SIMULATION_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "Resume" icon in the
     * simulation panel. It is used to show a highlighted resume icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringResumeIcon(MouseEvent evt) {
        this.iconContainerResumeSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.START_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "Start" icon in the
     * simulation panel. It is used to restore the default start icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingStartIcon(MouseEvent evt) {
        this.iconContainterStartSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.GENERATE_SIMULATION_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "Start" icon in the
     * simulation panel. It is used to show a highlighted start icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringStartIcon(MouseEvent evt) {
        this.iconContainterStartSimulation.setIcon(this.imageBroker.getImageIcon(TImageBroker.GENERATE_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the user does click on "LER node icon" in the
     * design panel. It's used to add a new LER node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnLERNodeIcon(MouseEvent evt) {
        TLERNode lerNode = null;
        try {
            lerNode = new TLERNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
        JLERWindow lerWindow = new JLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        lerWindow.setConfiguration(lerNode, false);
        lerWindow.setVisible(true);
        // FIX: the node could not be initilized yet
        if (lerNode.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(lerNode);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow errorWindow;
                errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                errorWindow.setErrorMessage(e.toString());
                errorWindow.setVisible(true);
            }
            this.scenario.setModified(true);
        } else {
            lerNode = null;
        }
    }

    /**
     * This method is called when the mouse exits the "Link" icon in the design
     * panel. It is used to restore the default link icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingLinkIcon(MouseEvent evt) {
        this.iconContainerLink.setIcon(this.imageBroker.getImageIcon(TImageBroker.LINK_MENU_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "Link" icon in the design
     * panel. It is used to show a highlighted link icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringLinkIcon(MouseEvent evt) {
        this.iconContainerLink.setIcon(this.imageBroker.getImageIcon(TImageBroker.LINK_MENU_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "Active LSR node" icon in
     * the design panel. It is used to restore the default Active LSR node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingActiveLSRNodeIcon(MouseEvent evt) {
        this.iconContainerActiveLSR.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LSR_MENU_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "Active LSR node" icon in
     * the design panel. It is used to show a highlighted Active LSR node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringActiveLSRNodeIcon(MouseEvent evt) {
        this.iconContainerActiveLSR.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LSR_MENU_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "LSR node" icon in the
     * design panel. It is used to restore the default LSR node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingLSRNodeIcon(MouseEvent evt) {
        this.iconContainerLSR.setIcon(this.imageBroker.getImageIcon(TImageBroker.LSR_MENU_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "LSR node" icon in the
     * design panel. It is used to show a highlighted LSR node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringLSRNodeIcon(MouseEvent evt) {
        this.iconContainerLSR.setIcon(this.imageBroker.getImageIcon(TImageBroker.LSR_MENU_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "Active LER node" icon in
     * the design panel. It is used to restore the default active LER node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingActiveLERNodeIcon(MouseEvent evt) {
        this.iconContainerActiveLER.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LER_MENU_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "Active LER node" icon in
     * the design panel. It is used to show a highlighted active LER node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringActiveLERNodeIcon(MouseEvent evt) {
        this.iconContainerActiveLER.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LER_MENU_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "LER node" icon in the
     * design panel. It is used to restore the default LER node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingLERNodeIcon(MouseEvent evt) {
        this.iconContainerLER.setIcon(this.imageBroker.getImageIcon(TImageBroker.LER_MENU_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "LER node" icon in the
     * design panel. It is used to show a highlighted LER node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringLERNodeIcon(MouseEvent evt) {
        this.iconContainerLER.setIcon(this.imageBroker.getImageIcon(TImageBroker.LER_MENU_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "Traffic sink node" icon
     * in the design panel. It is used to restore the default traffic sink node
     * icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingTrafficSinkNodeIcon(MouseEvent evt) {
        this.iconContainerTrafficSink.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_SINK_MENU_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "traffic sink node" icon
     * in the design panel. It is used to show a highlighted traffic sink node
     * icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringTrafficSinkNodeIcon(MouseEvent evt) {
        this.iconContainerTrafficSink.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_SINK_MENU_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the mouse exits the "Traffic generator node"
     * icon in the design panel. It is used to restore the default traffic
     * generator node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseExitingTrafficGeneratorNodeIcon(MouseEvent evt) {
        this.iconContainerTrafficGeneratorNode.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_GENERATOR_MENU_COLOR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the "traffic generator node"
     * icon in the design panel. It is used to show a highlighted traffic
     * generator node icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleMouseEnteringTrafficGeneratorNodeIcon(MouseEvent evt) {
        this.iconContainerTrafficGeneratorNode.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_GENERATOR_MENU_HIGHLIGHTED));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * This method is called when the user does click on the "traffic sink node"
     * icon in the design panel. It is used to add a new traffic sink node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnTrafficSinkNodeIcon(MouseEvent evt) {
        TTrafficSinkNode trafficSinkNode = null;
        try {
            trafficSinkNode = new TTrafficSinkNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow errorWindow;
            errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
            errorWindow.setErrorMessage(e.toString());
            errorWindow.setVisible(true);
        }
        JTrafficSinkWindow trafficSinkWindow = new JTrafficSinkWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        trafficSinkWindow.setConfiguration(trafficSinkNode, false);
        trafficSinkWindow.setVisible(true);
        // FIX: the node could not be initilized yet
        if (trafficSinkNode.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(trafficSinkNode);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow errorWindow;
                errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                errorWindow.setErrorMessage(e.toString());
                errorWindow.setVisible(true);
            }
            this.scenario.setModified(true);
        } else {
            trafficSinkNode = null;
        }
    }

    /**
     * This method is called when the user does click on the "traffic generator
     * node" icon in the design panel. It is used to add a new traffic generator
     * node.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnTrafficGeneratorNodeIcon(MouseEvent evt) {
        TTopology topology = this.scenario.getTopology();
        Iterator nodesIterator = topology.getNodesIterator();
        TNode node;
        boolean isThereTrafficSinks = false;
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.getNodeType() == TNode.TRAFFIC_SINK) {
                isThereTrafficSinks = true;
            }
        }
        if (!isThereTrafficSinks) {
            JWarningWindow warningWindow = new JWarningWindow(this.parent, true, this.imageBroker);
            warningWindow.setWarningMessage(this.translations.getString("JVentanaHija.NecesitaHaberUnReceptor"));
            warningWindow.setVisible(true);
        } else {
            TTrafficGeneratorNode trafficGeneratorNode = null;
            try {
                trafficGeneratorNode = new TTrafficGeneratorNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            } catch (Exception e) {
                JErrorWindow errorWindow;
                errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                errorWindow.setErrorMessage(e.toString());
                errorWindow.setVisible(true);
            }
            JTrafficGeneratorWindow trafficGeneratorWindow = new JTrafficGeneratorWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
            trafficGeneratorWindow.setConfiguration(trafficGeneratorNode, false);
            trafficGeneratorWindow.setVisible(true);
            // FIX: the node could not be initilized yet
            if (trafficGeneratorNode.isWellConfigured()) {
                try {
                    this.scenario.getTopology().addNode(trafficGeneratorNode);
                    this.designPanel.repaint();
                } catch (Exception e) {
                    JErrorWindow errorWindow;
                    errorWindow = new JErrorWindow(this.parent, true, this.imageBroker);
                    errorWindow.setErrorMessage(e.toString());
                    errorWindow.setVisible(true);
                }
                this.scenario.setModified(true);
            } else {
                trafficGeneratorNode = null;
            }
        }
    }

    /**
     * This method is called when the user does click on the "Pause" icon in the
     * simulation panel. It pauses the simulation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnPauseIcon(MouseEvent evt) {
        if (this.iconContainerPauseSimulation.isEnabled()) {
            this.scenario.getTopology().getTimer().setPaused(true);
            activeOptionsAfterPause();
        }
    }

    /**
     * This method is called when the user does click on the "Stop" icon in the
     * simulation panel. It stops the simulation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnStopIcon(MouseEvent evt) {
        if (this.iconContainerStopSimulation.isEnabled()) {
            this.scenario.getTopology().getTimer().reset();
            activeOptionsAfterStop();
        }
    }

    /**
     * This method is called when the user does click on the "Resume" icon in
     * the simulation panel. It resume the simulation after a pause.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnResumeIcon(MouseEvent evt) {
        if (this.iconContainerResumeSimulation.isEnabled()) {
            activeOptionsAfterResume();
            this.scenario.getTopology().getTimer().setPaused(false);
            this.scenario.getTopology().getTimer().restart();
        }
    }

    /**
     * This method is called when the user does click on the "Start" icon in the
     * simulation panel. It starts the simulation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return evt The event that triggers this method
     * @since 2.0
     */
    private void handleClickOnStartIcon(MouseEvent evt) {
        if (this.iconContainterStartSimulation.isEnabled()) {
            this.scenario.reset();
            if (!this.scenario.getTopology().getTimer().isRunning()) {
                this.scenario.getTopology().getTimer().setFinishTimestamp(new TTimestamp(this.sliderOptionsSimulationLengthMs.getValue(), this.sliderOptionsSimulationLengthNs.getValue()));
            }
            this.scenario.getTopology().getTimer().setTick(this.sliderOptionsTickDurationInNs.getValue());
            createListOfElementsToAnalize();
            this.scenario.setModified(true);
            this.scenario.getTopology().getTimer().reset();
            this.simulationPanel.reset();
            this.simulationPanel.repaint();
            this.scenario.simulate();
            int minimumDelay = this.scenario.getTopology().getMinimumDelay();
            int currentTickDurationInNs = this.sliderOptionsTickDurationInNs.getValue();
            if (currentTickDurationInNs > minimumDelay) {
                this.sliderOptionsTickDurationInNs.setValue(minimumDelay);
            }
            activeOptionsAfterStart();
        }
    }

    /**
     * This method creates a list of all elements that are configured to
     * generate statistics. It will be shown in the combo box of the analysis
     * panel to be selected by the user for further analysis.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void createListOfElementsToAnalize() {
        Iterator nodesIterator = null;
        TNode node = null;
        this.comboBoxNodeToAnalize.removeAllItems();
        this.comboBoxNodeToAnalize.addItem("");
        nodesIterator = this.scenario.getTopology().getNodesIterator();
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.isGeneratingStats()) {
                this.comboBoxNodeToAnalize.addItem(node.getName());
            }
        }
        this.comboBoxNodeToAnalize.setSelectedIndex(0);
    }

    /**
     * This method enables/disables some icons in the simulation panel after the
     * user does click in the Pause icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void activeOptionsAfterPause() {
        this.iconContainterStartSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.setEnabled(true);
        this.iconContainerStopSimulation.setEnabled(true);
        this.iconContainerPauseSimulation.setEnabled(false);
    }

    /**
     * This method enables/disables some icons in the simulation panel after the
     * user does click in the Stop icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void activeOptionsAfterStop() {
        this.iconContainterStartSimulation.setEnabled(true);
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerStopSimulation.setEnabled(false);
        this.iconContainerPauseSimulation.setEnabled(false);
    }

    /**
     * This method enables/disables some icons in the simulation panel after the
     * user does click in the Start icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void activeOptionsAfterStart() {
        this.iconContainterStartSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerStopSimulation.setEnabled(true);
        this.iconContainerPauseSimulation.setEnabled(true);
    }

    /**
     * This method enables/disables some icons in the simulation panel after the
     * user does click in the Resume icon.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void activeOptionsAfterResume() {
        this.iconContainterStartSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerStopSimulation.setEnabled(true);
        this.iconContainerPauseSimulation.setEnabled(true);
    }

    /**
     * This method closes the JScenarioWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void close() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method is called when the user does click on "Save as" option in the
     * scenario menu of this JScenarioWindow. Is allow the user to save the
     * scenario to a file using a specific file name.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void handleSaveAs() {
        updateScenarioInformation();
        JFileChooser saveAsDialog = new JFileChooser();
        saveAsDialog.setFileFilter(new JOSMFilter());
        saveAsDialog.setDialogType(JFileChooser.CUSTOM_DIALOG);
        // FIX: i18N required
        saveAsDialog.setApproveButtonMnemonic('A');
        saveAsDialog.setApproveButtonText(this.translations.getString("JVentanaHija.DialogoGuardar.OK"));
        saveAsDialog.setDialogTitle(this.translations.getString("JVentanaHija.DialogoGuardar.Almacenar") + this.getTitle() + this.translations.getString("-"));
        saveAsDialog.setAcceptAllFileFilterUsed(false);
        saveAsDialog.setSelectedFile(new File(this.getTitle()));
        saveAsDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = saveAsDialog.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            String extension = null;
            String fileName = saveAsDialog.getSelectedFile().getPath();
            int i = fileName.lastIndexOf('.');
            if (i > 0 && i < fileName.length() - 1) {
                extension = fileName.substring(i + 1).toLowerCase();
            }
            if (extension == null) {
                fileName += this.translations.getString(".osm");
            } else if (!extension.equals(this.translations.getString("osm"))) {
                fileName += this.translations.getString(".osm");
            }
            saveAsDialog.setSelectedFile(new File(fileName));
            this.scenario.setScenarioFile(saveAsDialog.getSelectedFile());
            this.scenario.setSaved(true);
            this.setTitle(this.scenario.getScenarioFile().getName());
            TOSMSaver osmSaver = new TOSMSaver(this.scenario);
            JDecissionWindow decissionWindow = new JDecissionWindow(this.parent, true, this.imageBroker);
            decissionWindow.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
            decissionWindow.setVisible(true);
            boolean addCRCToFile = decissionWindow.getUserAnswer();
            boolean savedCorrectly = osmSaver.save(this.scenario.getScenarioFile(), addCRCToFile);
            if (savedCorrectly) {
                this.scenario.setModified(false);
                this.scenario.setSaved(true);
            }
        }
    }

    /**
     * This method is called when the user closes a given scenario or exits the
     * simulator and the scenario is not saved. Is allow the user to save the
     * scenario to a file before closing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void saveBeforeClosing() {
        boolean isAlreadySaved = this.scenario.isSaved();
        boolean isModified = this.scenario.isModified();
        updateScenarioInformation();

        // Stop simulation before close, if necessary.
        if (this.scenario.getTopology().getTimer().isRunning()) {
            this.simulationPanel.reset();
            this.simulationPanel.repaint();
            this.scenario.reset();
            if (!this.scenario.getTopology().getTimer().isRunning()) {
                this.scenario.getTopology().getTimer().setFinishTimestamp(new TTimestamp(this.sliderOptionsSimulationLengthMs.getValue(), this.sliderOptionsSimulationLengthNs.getValue()));
            }
            this.scenario.getTopology().getTimer().setTick(this.sliderOptionsTickDurationInNs.getValue());
            this.scenario.getTopology().getTimer().setPaused(false);
            activeOptionsAfterStop();
        }

        if (!isAlreadySaved) {
            JDecissionWindow decissionWindow = new JDecissionWindow(this.parent, true, this.imageBroker);
            decissionWindow.setQuestion(this.getTitle() + this.translations.getString("JVentanaHija.DialogoGuardar.GuardarPrimeraVez"));
            decissionWindow.setVisible(true);
            boolean userAnswer = decissionWindow.getUserAnswer();
            decissionWindow.dispose();
            if (userAnswer) {
                this.handleSaveAs();
            }
        } else if ((isAlreadySaved) && (!isModified)) {
            // Nothing to do. All is already saved.
        } else if ((isAlreadySaved) && (isModified)) {
            JDecissionWindow decissionWindow = new JDecissionWindow(this.parent, true, this.imageBroker);
            decissionWindow.setQuestion(this.translations.getString("JVentanaHija.DialogoGuardar.CambiosSinguardar1") + " " + this.getTitle() + " " + this.translations.getString("JVentanaHija.DialogoGuardar.CambiosSinguardar2"));
            decissionWindow.setVisible(true);
            boolean userAnswer = decissionWindow.getUserAnswer();
            decissionWindow.dispose();
            if (userAnswer) {
                TOSMSaver osmSaver = new TOSMSaver(this.scenario);
                JDecissionWindow decissionWindow2 = new JDecissionWindow(this.parent, true, this.imageBroker);
                decissionWindow2.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
                decissionWindow2.setVisible(true);
                boolean addCRCToFile = decissionWindow2.getUserAnswer();
                boolean savedCorrectly = osmSaver.save(this.scenario.getScenarioFile(), addCRCToFile);
                if (savedCorrectly) {
                    this.scenario.setModified(false);
                    this.scenario.setSaved(true);
                }
            }
        }
    }

    /**
     * This method is called when the user does click on "Save" option in the
     * scenario menu of this JScenarioWindow. Is allow the user to save the
     * scenario to a file using a specific file name (if never saved before) or
     * the current one (on the contrary).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void handleSave() {
        boolean isAlreadySaved = this.scenario.isSaved();
        // FIX: Use next line so "Save" dialog is nos always used.
        boolean isModified = this.scenario.isModified();
        updateScenarioInformation();
        if (!isAlreadySaved) {
            this.handleSaveAs();
        } else {
            TOSMSaver osmSaver = new TOSMSaver(this.scenario);
            JDecissionWindow decissionWindow = new JDecissionWindow(this.parent, true, this.imageBroker);
            decissionWindow.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
            decissionWindow.setVisible(true);
            boolean addCRCToFile = decissionWindow.getUserAnswer();
            boolean savedCorrectly = osmSaver.save(this.scenario.getScenarioFile(), addCRCToFile);
            if (savedCorrectly) {
                this.scenario.setModified(false);
                this.scenario.setSaved(true);
            }
            this.scenario.setModified(false);
            this.scenario.setSaved(true);
        }
    }

    /**
     * This method is a wrapper of fillAnalysisInformation(String) to be used
     * the first time that ther ins not information to show.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void fillAnalysisInformation() {
        this.fillAnalysisInformation("");
    }

    /**
     * This method fill the analysis panel with statistics information and some
     * charts related to the node whose name is specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void fillAnalysisInformation(String nodeName) {
        this.analysisPanel.removeAll();
        this.analysisPanel.setLayout(new MigLayout("align center, fillx"));
        JScrollablePanel scenarioTitle = new JScrollablePanel();
        JScrollablePanel author = new JScrollablePanel();
        JScrollablePanel description = new JScrollablePanel();
        JScrollablePanel element = new JScrollablePanel();
        scenarioTitle.setMinimumSize(new Dimension());
        author.setMinimumSize(new Dimension());
        description.setMinimumSize(new Dimension());
        element.setMinimumSize(new Dimension());
        scenarioTitle.add(labelScenarioTitle);
        labelScenarioAuthorName.setMinimumSize(new Dimension());
        textAreaScenarioDescription.setMinimumSize(new Dimension());
        labelElementToAnalize.setMinimumSize(new Dimension());
        labelScenarioTitle.setMinimumSize(new Dimension());
        author.add(labelScenarioAuthorName);
        description.add(textAreaScenarioDescription);
        element.add(labelElementToAnalize);
        scenarioTitle.setBackground(Color.WHITE);
        author.setBackground(Color.WHITE);
        description.setBackground(Color.WHITE);
        element.setBackground(Color.WHITE);
        this.analysisPanel.add(scenarioTitle, "span 2, grow, wrap");
        this.analysisPanel.add(author, "span 2, grow, wrap");
        this.analysisPanel.add(textAreaScenarioDescription, "span 2, grow, gapleft 10%, gapright 10%, wrap");
        this.analysisPanel.add(element, "span 2, grow, wrap");
        this.labelScenarioTitle.setText(this.textFieldOptionsScenarioTitle.getText());
        this.labelScenarioAuthorName.setText(this.textFieldOptionsScenarioAuthorName.getText());
        this.textAreaScenarioDescription.setText(this.textAreaOptionsScenarioDescription.getText());
        this.labelElementToAnalize.setText(nodeName);
        this.labelElementToAnalize.setIcon(null);
        TNode nt = this.scenario.getTopology().getFirstNodeNamed(nodeName);
        if (nt != null) {
            if (nt.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_GENERATOR));
            } else if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getImageIcon(TImageBroker.TRAFFIC_SINK));
            } else if (nt.getNodeType() == TNode.LER) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getImageIcon(TImageBroker.LER));
            } else if (nt.getNodeType() == TNode.ACTIVE_LER) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LER));
            } else if (nt.getNodeType() == TNode.LSR) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getImageIcon(TImageBroker.LSR));
            } else if (nt.getNodeType() == TNode.ACTIVE_LSR) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getImageIcon(TImageBroker.ACTIVE_LSR));
            }
            int numeroGraficos = nt.getStats().getNumberOfAvailableDatasets();

            // FIX: Do not use harcoded values. Use class constants instead
            if (numeroGraficos > 0) {
                this.xyChart1 = new JXYChart(nt.getStats().getTitleOfDataset1(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset1());
                if (numeroGraficos == 1) {
                    this.analysisPanel.add(this.xyChart1.getChartPanel());
                } else {
                    this.analysisPanel.add(this.xyChart1.getChartPanel(), "grow");
                }
            }
            // FIX: Do not use harcoded values. Use class constants instead
            if (numeroGraficos > 1) {

                this.xyChart2 = new JXYChart(nt.getStats().getTitleOfDataset2(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset2());
                this.analysisPanel.add(this.xyChart2.getChartPanel(), "grow, wrap");
            }
            // FIX: Do not use harcoded values. Use class constants instead
            if (numeroGraficos > 2) {
                this.xyChart3 = new JXYChart(nt.getStats().getTitleOfDataset3(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset3());
                this.analysisPanel.add(this.xyChart3.getChartPanel(), "grow");
            }
            // FIX: Do not use harcoded values. Use class constants instead
            if (numeroGraficos > 3) {
                this.barChart1 = new JBarChart(nt.getStats().getTitleOfDataset4(), TStats.DESCRIPTION, TStats.NUMBER, (DefaultCategoryDataset) nt.getStats().getDataset4());
                this.analysisPanel.add(this.barChart1.getChartPanel(), "grow, wrap");
            }
            if (numeroGraficos > 4) {
                this.barChart2 = new JBarChart(nt.getStats().getTitleOfDataset5(), TStats.DESCRIPTION, TStats.NUMBER, (DefaultCategoryDataset) nt.getStats().getDataset5());
                this.analysisPanel.add(this.barChart2.getChartPanel(), "grow");
            }
        }

        AnalysisPanelComponentAdapter componentAdapter = new AnalysisPanelComponentAdapter();
        if (this.xyChart1 != null) {
            componentAdapter.addChartPanel(this.xyChart1.getChartPanel());
        }
        if (this.xyChart2 != null) {
            componentAdapter.addChartPanel(this.xyChart2.getChartPanel());
        }
        if (this.xyChart3 != null) {
            componentAdapter.addChartPanel(this.xyChart3.getChartPanel());
        }
        if (this.barChart1 != null) {
            componentAdapter.addChartPanel(this.barChart1.getChartPanel());
        }
        if (this.barChart2 != null) {
            componentAdapter.addChartPanel(this.barChart2.getChartPanel());
        }
        this.analysisPanel.addComponentListener(componentAdapter);
        this.analysisPanel.revalidate();
        componentAdapter.setInitialChartsSize(analysisPanel.getSize());
        this.analysisPanel.repaint();
    }

    /**
     * This method saves the latest information about the simulation to the
     * associated scenario object.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void updateScenarioInformation() {
        this.scenario.setTitle(this.textFieldOptionsScenarioTitle.getText());
        this.scenario.setAuthor(this.textFieldOptionsScenarioAuthorName.getText());
        this.scenario.setDescription(this.textAreaOptionsScenarioDescription.getText());
    }

    private TProgressEventListener progressEventListener;
    private TScenario scenario;
    private TNode selectedNode;
    private TImageBroker imageBroker;
    private JOpenSimMPLS parent;
    private TTopologyElement rightClickedElementInDesignPanel;
    private boolean timingControlDisabled;
    private JXYChart xyChart1;
    private JXYChart xyChart2;
    private JXYChart xyChart3;
    private JBarChart barChart1;
    private JBarChart barChart2;
    private JTextArea textAreaScenarioDescription;
    private JProgressBar progressBarSimulation;
    private JMenuItem menuItemDeleteElement;
    private JMenuItem menuItemDeleteAllItems;
    private JMenuItem menuItemHideLinksNames;
    private JMenuItem menuItemHideNodesNames;
    private JMenuItem menuItemItemElementProperties;
    private JCheckBoxMenuItem chekBoxMenuItemShowElementName;
    private JMenuItem menuItemShowLinksNames;
    private JMenuItem menuItemShowNodesNames;
    private JTextArea textAreaOptionsScenarioDescription;
    private JPopupMenu popupMenuTopologyElement;
    private JPopupMenu popupMenuBackgroundDesignPanel;
    private JSlider sliderOptionsSimulationLengthMs;
    private JSlider sliderOptionsSimulationLengthNs;
    private JLabel labelOptionsSimulationLengthMs;
    private JLabel labelOptionsSimulationLengthNs;
    private JLabel labelScenarioAuthorName;
    private JLabel labelScenarioTitle;
    private JLabel labelSimulationSpeedFaster;
    private JLabel labelSimulationSpeedSlower;
    private JLabel labelElementToAnalize;
    private JLabel labelOptionsNsTick;
    private JLabel iconContainterStartSimulation;
    private JLabel iconContainerTrafficGeneratorNode;
    private JLabel iconContainerLink;
    private JLabel iconContainerStopSimulation;
    private JLabel iconContainerLER;
    private JLabel iconContainerActiveLER;
    private JLabel iconContainerLSR;
    private JLabel iconContainerActiveLSR;
    private JLabel iconContainerPauseSimulation;
    private JLabel iconContainerResumeSimulation;
    private JLabel iconContainerTrafficSink;
    private JLabel labelSelectANodeToAnalyze;
    private JLabel labelOptionsSimulationLength;
    private JLabel labelOptionsTickLengthInNs;
    private JLabel labelOptionsScenarioTitle;
    private JLabel labelOptionsScenarioAuthorName;
    private JLabel labelOptionsScenarioDescription;
    private JPanel optionsSimulationTimingPanel;
    private JPanel optionsScenarioInformationPanel;
    private JScrollPane scrollPaneDesign;
    private JScrollPane scrollPaneSimulation;
    private JScrollPane scrollPaneAnalysis;
    private JSeparator separatorPopupMenuTopologyElement;
    private JSeparator separatorPopupMenuBackgroundDesignPanel;
    private JTabbedPane tabsPanel;
    private JSlider sliderSimulationSpeedInMsPerTick;
    private JTextField textFieldOptionsScenarioAuthorName;
    private JTextField textFieldOptionsScenarioTitle;
    private JScrollablePanel analysisPanel;
    private JPanel analysisMainContainerPanel;
    private JPanel designToolbarPanel;
    private JPanel simulationToolbarPanel;
    private JDesignPanel designPanel;
    private JPanel designMainContainerPanel;
    private JPanel optionsSecondaryMainContainerPanel;
    private JPanel optionsPrimaryMainContainerPanel;
    private JPanel analysisToolbarPanel;
    private JSimulationPanel simulationPanel;
    private JPanel simulationMainContainerPanel;
    private JSlider sliderOptionsTickDurationInNs;
    private JComboBox comboBoxNodeToAnalize;
    private ResourceBundle translations;
}
