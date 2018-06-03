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
 * Esta clase implementa una ventana que save� un escenario completo y dar�
 * soporte a la simulaci�n, an�lisis y dise�o de la topology.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JScenarioWindow extends JInternalFrame {

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * JVentanaHija.
     *
     * @since 2.0
     * @param parent Ventana padre dentro de la cual se va a ubicar este ventana
     * hija.
     * @param imageBroker Dispensador de im�genes de donde se obtendr�n todas
     * las im�genes que se necesiten.
     */
    public JScenarioWindow(JOpenSimMPLS parent, TImageBroker imageBroker) {
        this.imageBroker = imageBroker;
        this.parent = parent;
        initComponents();
        initComponents2();
    }

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * JVentanaHija.
     *
     * @since 2.0
     * @param title T�tulo que deseamos que tenga la ventana hija. Se usar�
     * tambi�n para save el escenario en disco.
     * @param parent Ventana padre dentro de la cual se va a ubicar este ventana
     * hija.
     * @param imageBroker Dispensador de im�genes de donde se obtendr�n todas
     * las im�genes que se necesiten.
     */
    public JScenarioWindow(JOpenSimMPLS parent, TImageBroker imageBroker, String title) {
        this.imageBroker = imageBroker;
        this.parent = parent;
        initComponents();
        initComponents2();
        this.title = title;
    }

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * JVentanaHija y la inicializa con los valores de un nodo existente.
     *
     * @param parent Ventana padre dentro de la cual se va a ubicar este ventana
     * hija.
     * @param imageBroker Dispensador de im�genes de donde se obtendr�n todas
     * las im�genes que se necesiten.
     * @param scenario Escenario ya creado al que se va a asociar esta ventana
     * hija y que contendr� un escenario y todos sus datos.
     * @since 2.0
     */
    public JScenarioWindow(JOpenSimMPLS parent, TImageBroker imageBroker, TScenario scenario) {
        this.imageBroker = imageBroker;
        this.parent = parent;
        initComponents();
        initComponents2();
        this.scenario = scenario;
    }

    /**
     * Este m�todo es llamado desde el constructor para actualizar la mayor
     * parte de los atributos de la clase que tienen que ver con la interfaz de
     * usuario. Es un m�todo creado por NetBeans automaticamente.
     *
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
        this.jSeparator2 = new JSeparator();
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
        this.iconContainerFinishSimulation = new JLabel();
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
        this.popupMenuTopologyElement.setFont(new Font("Dialog", 0, 12));
        this.menuItemDeleteElement.setFont(new Font("Dialog", 0, 12));
        this.menuItemDeleteElement.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.Delete").charAt(0));
        this.menuItemDeleteElement.setText(this.translations.getString("VentanaHija.PopUpDisenio.Delete"));
        this.menuItemDeleteElement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioEliminar(evt);
            }
        });
        this.popupMenuTopologyElement.add(this.menuItemDeleteElement);
        this.chekBoxMenuItemShowElementName.setFont(new Font("Dialog", 0, 12));
        this.chekBoxMenuItemShowElementName.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.verNombre").charAt(0));
        this.chekBoxMenuItemShowElementName.setText(this.translations.getString("VentanaHija.PopUpDisenio.verNombre"));
        this.chekBoxMenuItemShowElementName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioVerNombre(evt);
            }
        });
        this.popupMenuTopologyElement.add(this.chekBoxMenuItemShowElementName);
        this.popupMenuTopologyElement.add(this.separatorPopupMenuTopologyElement);
        this.menuItemItemElementProperties.setFont(new Font("Dialog", 0, 12));
        this.menuItemItemElementProperties.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.Propiedades").charAt(0));
        this.menuItemItemElementProperties.setText(this.translations.getString("VentanaHija.PopUpDisenio.Propiedades"));
        this.menuItemItemElementProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPropiedadesPopUpDisenioElemento(evt);
            }
        });
        this.popupMenuTopologyElement.add(this.menuItemItemElementProperties);
        // Definition of opup menu showed when righ-click on background in 
        // design panel
        this.popupMenuBackgroundDesignPanel.setFont(new Font("Dialog", 0, 12));
        this.menuItemShowNodesNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemShowNodesNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.verTodosNodos").charAt(0));
        this.menuItemShowNodesNames.setText(this.translations.getString("popUpDisenioFondo.verTodosNodos"));
        this.menuItemShowNodesNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoVerNombreNodos(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemShowNodesNames);
        this.menuItemHideNodesNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemHideNodesNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.ocultarTodosNodos").charAt(0));
        this.menuItemHideNodesNames.setText(this.translations.getString("popUpDisenioFondo.ocultarTodosNodos"));
        this.menuItemHideNodesNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoOcultarNombreNodos(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemHideNodesNames);
        this.menuItemShowLinksNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemShowLinksNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.verTodosEnlaces").charAt(0));
        this.menuItemShowLinksNames.setText(this.translations.getString("popUpDisenioFondo.verTodosEnlaces"));
        this.menuItemShowLinksNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoVerNombreEnlaces(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemShowLinksNames);
        this.menuItemHideLinksNames.setFont(new Font("Dialog", 0, 12));
        this.menuItemHideLinksNames.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.ocultarTodosEnlaces").charAt(0));
        this.menuItemHideLinksNames.setText(this.translations.getString("popUpDisenioFondo.ocultarTodosEnlaces"));
        this.menuItemHideLinksNames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoOcultarNombreEnlaces(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemHideLinksNames);
        this.popupMenuBackgroundDesignPanel.add(this.jSeparator2);
        this.menuItemDeleteAllItems.setFont(new Font("Dialog", 0, 12));
        this.menuItemDeleteAllItems.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.eliminarTodo").charAt(0));
        this.menuItemDeleteAllItems.setText(this.translations.getString("popUpDisenioFondo.borrarTodo"));
        this.menuItemDeleteAllItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoEliminar(evt);
            }
        });
        this.popupMenuBackgroundDesignPanel.add(this.menuItemDeleteAllItems);
        // Scenario window properties
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(this.translations.getString("VentanaHija.Titulo"));
        setFont(new Font("Dialog", 0, 12));
        setFrameIcon(this.imageBroker.getIcon(TImageBroker.ICONO_VENTANA_INTERNA_MENU));
        setNormalBounds(new Rectangle(10, 10, 100, 100));
        setPreferredSize(new Dimension(100, 100));
        setVisible(true);
        setAutoscrolls(true);
        this.tabsPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.tabsPanel.setFont(new Font("Dialog", 0, 12));
        // Definition of design tab content
        this.designMainContainerPanel.setLayout(new BorderLayout());
        this.designToolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.designToolbarPanel.setBorder(new EtchedBorder());
        this.iconContainerTrafficGeneratorNode.setIcon(this.imageBroker.getIcon(TImageBroker.EMISOR_MENU));
        this.iconContainerTrafficGeneratorNode.setToolTipText(this.translations.getString("VentanaHija.Topic.Emisor"));
        this.iconContainerTrafficGeneratorNode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoEmisor(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDeIconoEmisor(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnAniadirEmisorDeTrafico(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerTrafficGeneratorNode);
        this.iconContainerTrafficSink.setIcon(this.imageBroker.getIcon(TImageBroker.RECEPTOR_MENU));
        this.iconContainerTrafficSink.setToolTipText(this.translations.getString("VentanaHija.Topic.Receptor"));
        this.iconContainerTrafficSink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoReceptor(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDeIconoReceptor(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnAniadirReceptor(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerTrafficSink);
        this.iconContainerLER.setIcon(this.imageBroker.getIcon(TImageBroker.LER_MENU));
        this.iconContainerLER.setToolTipText(this.translations.getString("VentanaHija.Topic.LER"));
        this.iconContainerLER.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoLER(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDeIconoLER(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnAniadirLER(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerLER);
        this.iconContainerActiveLER.setIcon(this.imageBroker.getIcon(TImageBroker.LERA_MENU));
        this.iconContainerActiveLER.setToolTipText(this.translations.getString("VentanaHija.Topic.LERActivo"));
        this.iconContainerActiveLER.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoLERA(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDeIconoLERA(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnAniadirLERA(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerActiveLER);
        this.iconContainerLSR.setIcon(this.imageBroker.getIcon(TImageBroker.LSR_MENU));
        this.iconContainerLSR.setToolTipText(this.translations.getString("VentanaHija.Topic.LSR"));
        this.iconContainerLSR.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoLSR(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDeIconoLSR(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnAniadirLSR(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerLSR);
        this.iconContainerActiveLSR.setIcon(this.imageBroker.getIcon(TImageBroker.LSRA_MENU));
        this.iconContainerActiveLSR.setToolTipText(this.translations.getString("VentanaHija.Topic.LSRActivo"));
        this.iconContainerActiveLSR.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoLSRA(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDeIconoLSRA(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnAniadirLSRA(evt);
            }
        });
        this.designToolbarPanel.add(this.iconContainerActiveLSR);
        this.iconContainerLink.setIcon(this.imageBroker.getIcon(TImageBroker.ENLACE_MENU));
        this.iconContainerLink.setToolTipText(this.translations.getString("VentanaHija.Topic.Enlace"));
        this.iconContainerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                clicEnAniadirEnlace(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoEnlace(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDeIconoEnlace(evt);
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
                clicDerechoEnPanelDisenio(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnPanelDisenio(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                clicSoltadoEnPanelDisenio(evt);
            }
        });
        this.designPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                arrastrandoEnPanelDisenio(evt);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                ratonSobrePanelDisenio(evt);
            }
        });
        this.scrollPaneDesign.setViewportView(this.designPanel);
        this.designMainContainerPanel.add(this.scrollPaneDesign, BorderLayout.CENTER);
        this.tabsPanel.addTab(this.translations.getString("VentanaHija.Tab.Disenio"), this.imageBroker.getIcon(TImageBroker.DISENIO), this.designMainContainerPanel, this.translations.getString("VentanaHija.A_panel_to_design_network_topology"));
        // Definition of options panel content
        this.optionsPrimaryMainContainerPanel.setLayout(new BorderLayout());
        this.optionsPrimaryMainContainerPanel.setBorder(new EtchedBorder());
        this.optionsSecondaryMainContainerPanel.setLayout(new GridBagLayout());
        this.optionsScenarioInformationPanel.setLayout(new GridBagLayout());
        this.optionsScenarioInformationPanel.setBorder(new TitledBorder(null, this.translations.getString("VentanaHija.GParameters"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
        this.labelOptionsScenarioTitle.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsScenarioTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsScenarioTitle.setText(this.translations.getString("VentanaHija.Scene_title"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.labelOptionsScenarioTitle, gridBagConstraints);
        this.textFieldOptionsScenarioTitle.setToolTipText(this.translations.getString("VentanaHija.Type_a__title_of_the_scene"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.textFieldOptionsScenarioTitle, gridBagConstraints);
        this.labelOptionsScenarioAuthorName.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsScenarioAuthorName.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsScenarioAuthorName.setText(this.translations.getString("VentanaHija.Scene_author"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.labelOptionsScenarioAuthorName, gridBagConstraints);
        this.textFieldOptionsScenarioAuthorName.setToolTipText(this.translations.getString("VentanaHija.Type_de_name_of_the_author"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.textFieldOptionsScenarioAuthorName, gridBagConstraints);
        this.labelOptionsScenarioDescription.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsScenarioDescription.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsScenarioDescription.setText(this.translations.getString("VentanaHija.Description"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.labelOptionsScenarioDescription, gridBagConstraints);
        this.textAreaOptionsScenarioDescription.setToolTipText(this.translations.getString("VentanaHija.Enter_a_short_description."));
        this.textAreaOptionsScenarioDescription.setLineWrap(true);
        this.textAreaOptionsScenarioDescription.setWrapStyleWord(true);
        this.textAreaOptionsScenarioDescription.setRows(3);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsScenarioInformationPanel.add(this.textAreaOptionsScenarioDescription, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSecondaryMainContainerPanel.add(this.optionsScenarioInformationPanel, gridBagConstraints);
        this.optionsSimulationTimingPanel.setLayout(new GridBagLayout());
        this.optionsSimulationTimingPanel.setBorder(new TitledBorder(null, this.translations.getString("VentanaHija.TParameters"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
        this.labelOptionsSimulationLength.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsSimulationLength.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsSimulationLength.setText(this.translations.getString("VentanaHija.Duration"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsSimulationLength, gridBagConstraints);
        this.sliderOptionsSimulationLengthMs.setMajorTickSpacing(2);
        this.sliderOptionsSimulationLengthMs.setMaximum(2);
        this.sliderOptionsSimulationLengthMs.setMinorTickSpacing(1);
        this.sliderOptionsSimulationLengthMs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_ms._component_of_simulation_duration."));
        this.sliderOptionsSimulationLengthMs.setValue(0);
        this.sliderOptionsSimulationLengthMs.setMaximumSize(new Dimension(30, 20));
        this.sliderOptionsSimulationLengthMs.setMinimumSize(new Dimension(30, 24));
        this.sliderOptionsSimulationLengthMs.setPreferredSize(new Dimension(30, 20));
        this.sliderOptionsSimulationLengthMs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                clicEnDuracionMs(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 150.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.sliderOptionsSimulationLengthMs, gridBagConstraints);
        this.labelOptionsSimulationLengthMs.setFont(new Font("Dialog", 0, 10));
        this.labelOptionsSimulationLengthMs.setForeground(new Color(102, 102, 102));
        this.labelOptionsSimulationLengthMs.setText(this.translations.getString("VentanaHija.ms."));
        this.labelOptionsSimulationLengthMs.setMaximumSize(new Dimension(30, 14));
        this.labelOptionsSimulationLengthMs.setMinimumSize(new Dimension(30, 14));
        this.labelOptionsSimulationLengthMs.setPreferredSize(new Dimension(30, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 40.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsSimulationLengthMs, gridBagConstraints);
        this.sliderOptionsSimulationLengthNs.setMajorTickSpacing(1000);
        this.sliderOptionsSimulationLengthNs.setMaximum(999999);
        this.sliderOptionsSimulationLengthNs.setMinorTickSpacing(100);
        this.sliderOptionsSimulationLengthNs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_ns._component_of_simulation_duration."));
        this.sliderOptionsSimulationLengthNs.setValue(100000);
        this.sliderOptionsSimulationLengthNs.setMaximumSize(new Dimension(32767, 20));
        this.sliderOptionsSimulationLengthNs.setMinimumSize(new Dimension(36, 20));
        this.sliderOptionsSimulationLengthNs.setPreferredSize(new Dimension(200, 20));
        this.sliderOptionsSimulationLengthNs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                clicEnDuracionNs(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 150.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.sliderOptionsSimulationLengthNs, gridBagConstraints);
        this.labelOptionsSimulationLengthNs.setFont(new Font("Dialog", 0, 10));
        this.labelOptionsSimulationLengthNs.setForeground(new Color(102, 102, 102));
        this.labelOptionsSimulationLengthNs.setText(this.translations.getString("VentanaHija.ns."));
        this.labelOptionsSimulationLengthNs.setMaximumSize(new Dimension(40, 14));
        this.labelOptionsSimulationLengthNs.setMinimumSize(new Dimension(40, 14));
        this.labelOptionsSimulationLengthNs.setPreferredSize(new Dimension(40, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsSimulationLengthNs, gridBagConstraints);
        this.labelOptionsTickLengthInNs.setFont(new Font("Dialog", 0, 12));
        this.labelOptionsTickLengthInNs.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelOptionsTickLengthInNs.setText(this.translations.getString("VentanaHija.Step"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsTickLengthInNs, gridBagConstraints);
        this.sliderOptionsTickDurationInNs.setMajorTickSpacing(1000);
        this.sliderOptionsTickDurationInNs.setMaximum(999999);
        this.sliderOptionsTickDurationInNs.setMinimum(1);
        this.sliderOptionsTickDurationInNs.setMinorTickSpacing(100);
        this.sliderOptionsTickDurationInNs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_step_duration_(ns).."));
        this.sliderOptionsTickDurationInNs.setValue(10000);
        this.sliderOptionsTickDurationInNs.setMaximumSize(new Dimension(32767, 20));
        this.sliderOptionsTickDurationInNs.setPreferredSize(new Dimension(100, 20));
        this.sliderOptionsTickDurationInNs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                clicEnPasoNs(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.sliderOptionsTickDurationInNs, gridBagConstraints);
        this.labelOptionsNsTick.setFont(new Font("Dialog", 0, 10));
        this.labelOptionsNsTick.setForeground(new Color(102, 102, 102));
        this.labelOptionsNsTick.setText(this.translations.getString("VentanaHija.ns."));
        this.labelOptionsNsTick.setMaximumSize(new Dimension(40, 14));
        this.labelOptionsNsTick.setMinimumSize(new Dimension(40, 14));
        this.labelOptionsNsTick.setPreferredSize(new Dimension(40, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSimulationTimingPanel.add(this.labelOptionsNsTick, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.optionsSecondaryMainContainerPanel.add(this.optionsSimulationTimingPanel, gridBagConstraints);
        this.optionsPrimaryMainContainerPanel.add(this.optionsSecondaryMainContainerPanel, BorderLayout.NORTH);
        this.tabsPanel.addTab(this.translations.getString("VentanaHija.Options"), imageBroker.getIcon(TImageBroker.OPCIONES), this.optionsPrimaryMainContainerPanel, this.translations.getString("VentanaHija.Options_about_the_scene"));
        // Definition of simulation panel content
        this.simulationMainContainerPanel.setLayout(new BorderLayout());
        this.simulationToolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.simulationToolbarPanel.setBorder(new EtchedBorder());
        this.iconContainterStartSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_GENERAR));
        this.iconContainterStartSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Generar"));
        this.iconContainterStartSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoComenzar(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDelIconoComenzar(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnComenzar(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainterStartSimulation);
        this.iconContainerFinishSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PARAR));
        this.iconContainerFinishSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Finalizar"));
        this.iconContainerFinishSimulation.setEnabled(false);
        this.iconContainerFinishSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoFinalizar(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDelIconoFinalizar(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnFinalizar(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainerFinishSimulation);
        this.iconContainerResumeSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_COMENZAR));
        this.iconContainerResumeSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Simulacion"));
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoReanudar(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDelIconoReanudar(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnReanudar(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainerResumeSimulation);
        this.iconContainerPauseSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PAUSA));
        this.iconContainerPauseSimulation.setToolTipText(this.translations.getString("VentanaHija.Topic.Detener"));
        this.iconContainerPauseSimulation.setEnabled(false);
        this.iconContainerPauseSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnIconoPausar(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDelIconoPausar(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicAlPausar(evt);
            }
        });
        this.simulationToolbarPanel.add(this.iconContainerPauseSimulation);
        this.progressBarSimulation.setFont(new Font("Dialog", 0, 12));
        this.progressBarSimulation.setToolTipText(this.translations.getString("VentanaHija.BarraProgreso.tooltip"));
        this.progressBarSimulation.setStringPainted(true);
        this.simulationToolbarPanel.add(this.progressBarSimulation);
        this.labelSimulationSpeedFaster.setFont(new Font("Dialog", 0, 10));
        this.labelSimulationSpeedFaster.setForeground(new Color(102, 102, 102));
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.simulationToolbarPanel.add(this.labelSimulationSpeedFaster);
        this.sliderSimulationSpeedInMsPerTick.setMajorTickSpacing(10);
        this.sliderSimulationSpeedInMsPerTick.setMaximum(300);
        this.sliderSimulationSpeedInMsPerTick.setMinimum(1);
        this.sliderSimulationSpeedInMsPerTick.setMinorTickSpacing(1);
        this.sliderSimulationSpeedInMsPerTick.setSnapToTicks(true);
        this.sliderSimulationSpeedInMsPerTick.setToolTipText(this.translations.getString("VentanaHija.Simulacion.SelectorDeVelocidad.tooltip"));
        this.sliderSimulationSpeedInMsPerTick.setPreferredSize(new Dimension(100, 20));
        this.sliderSimulationSpeedInMsPerTick.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                mlsPorTicCambiado(evt);
            }
        });
        this.simulationToolbarPanel.add(this.sliderSimulationSpeedInMsPerTick);
        this.labelSimulationSpeedSlower.setFont(new Font("Dialog", 0, 10));
        this.labelSimulationSpeedSlower.setForeground(new Color(102, 102, 102));
        this.labelSimulationSpeedSlower.setText(this.translations.getString("VentanaHija.Simulacion.slower"));
        this.simulationToolbarPanel.add(this.labelSimulationSpeedSlower);
        this.simulationMainContainerPanel.add(this.simulationToolbarPanel, BorderLayout.NORTH);
        this.scrollPaneSimulation.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.simulationPanel.setBorder(new EtchedBorder());
        this.simulationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                ratonPulsadoYSoltadoEnPanelSimulacion(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                clicEnPanelSimulacion(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                ratonSoltadoEnPanelSimulacion(evt);
            }
        });
        this.simulationPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                ratonArrastradoEnPanelSimulacion(evt);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                ratonSobrePanelSimulacion(evt);
            }
        });
        this.scrollPaneSimulation.setViewportView(this.simulationPanel);
        this.simulationMainContainerPanel.add(this.scrollPaneSimulation, BorderLayout.CENTER);
        this.tabsPanel.addTab(this.translations.getString("VentanaHija.Tab.Simulacion"), this.imageBroker.getIcon(TImageBroker.SIMULACION), this.simulationMainContainerPanel, this.translations.getString("VentanaHija.A_panel_to_generate_and_play_simulation."));
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
                clicEnSeleccionalElementoEstadistica(evt);
            }
        });
        this.analysisToolbarPanel.add(this.comboBoxNodeToAnalize);
        this.analysisMainContainerPanel.add(this.analysisToolbarPanel, BorderLayout.NORTH);
        this.scrollPaneAnalysis.setBorder(new BevelBorder(BevelBorder.LOWERED));
        /*        
        this.analysisPanel.setLayout(new GridBagLayout());
        this.analysisPanel.setBackground(Color.WHITE);
        this.analysisScenarioInformationPanel.setLayout(new GridLayout(4,1));
        this.analysisScenarioInformationPanel.setBackground(Color.WHITE);
        this.labelScenarioTitle.setBackground(Color.WHITE);
        this.labelScenarioTitle.setFont(new Font("Serif", 1, 18));
        this.labelScenarioTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelScenarioTitle.setText(this.translations.getString("JVentanaHija.TituloDelEscenario"));
        this.analysisScenarioInformationPanel.add(this.labelScenarioTitle);
        this.labelScenarioAuthorName.setBackground(Color.WHITE);
        this.labelScenarioAuthorName.setFont(new Font("Serif", 1, 14));
        this.labelScenarioAuthorName.setForeground(new Color(102, 0, 51));
        this.labelScenarioAuthorName.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelScenarioAuthorName.setText(this.translations.getString("JVentanaHija.AutorDelEscenario"));
        this.analysisScenarioInformationPanel.add(this.labelScenarioAuthorName);
        this.textAreaScenarioDescription.setBackground(Color.WHITE);
        this.textAreaScenarioDescription.setEditable(false);
        this.textAreaScenarioDescription.setFont(new Font("MonoSpaced", 0, 11));
        this.textAreaScenarioDescription.setLineWrap(true);
        this.textAreaScenarioDescription.setText(this.translations.getString("JVentanaHija.DescripcionDelEscenario"));
        this.textAreaScenarioDescription.setWrapStyleWord(true);
        this.textAreaScenarioDescription.setBorder(null);
        this.textAreaScenarioDescription.setAutoscrolls(false);
        this.analysisScenarioInformationPanel.add(this.textAreaScenarioDescription);
        this.labelElementToAnalize.setBackground(Color.WHITE);
        this.labelElementToAnalize.setFont(new Font("Serif", 1, 14));
        this.labelElementToAnalize.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelElementToAnalize.setText(this.translations.getString("JVentanaHija.SeleccioneNodoAInspeccionar"));
        this.analysisScenarioInformationPanel.add(this.labelElementToAnalize);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = GridBagConstraints.CENTER;
        this.analysisPanel.add(this.analysisScenarioInformationPanel, gridBagConstraints);
        this.analysisChartsPanel.setLayout(new GridLayout(3,2));
        this.analysisChartsPanel.setBackground(Color.WHITE);
        this.analysisChartsPanel.setBorder(new EtchedBorder());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = GridBagConstraints.CENTER;
        this.analysisPanel.add(this.analysisChartsPanel, gridBagConstraints);
         */
////////////////
        this.analysisPanel.setBorder(new EtchedBorder());
        this.analysisPanel.setLayout(new MigLayout());
        this.analysisPanel.setBackground(Color.WHITE);
        this.labelScenarioTitle.setBackground(Color.WHITE);
        this.labelScenarioTitle.setFont(new Font("Serif", 1, 18));
        this.labelScenarioTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelScenarioTitle.setText(this.translations.getString("JVentanaHija.TituloDelEscenario"));
        this.analysisPanel.add(this.labelScenarioTitle, "span 2, grow, wrap");
        this.labelScenarioAuthorName.setBackground(Color.WHITE);
        this.labelScenarioAuthorName.setFont(new Font("Serif", 1, 14));
        this.labelScenarioAuthorName.setForeground(new Color(102, 0, 51));
        this.labelScenarioAuthorName.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelScenarioAuthorName.setText(this.translations.getString("JVentanaHija.AutorDelEscenario"));
        this.analysisPanel.add(this.labelScenarioAuthorName, "span 2, grow, wrap");
        this.textAreaScenarioDescription.setBackground(Color.WHITE);
        this.textAreaScenarioDescription.setEditable(false);
        this.textAreaScenarioDescription.setFont(new Font("MonoSpaced", 0, 11));
        this.textAreaScenarioDescription.setLineWrap(true);
        this.textAreaScenarioDescription.setText(this.translations.getString("JVentanaHija.DescripcionDelEscenario"));
        this.textAreaScenarioDescription.setWrapStyleWord(true);
        this.textAreaScenarioDescription.setBorder(null);
        this.textAreaScenarioDescription.setAutoscrolls(false);
        this.analysisPanel.add(this.textAreaScenarioDescription, "span 2, grow, wrap");
        this.labelElementToAnalize.setBackground(Color.WHITE);
        this.labelElementToAnalize.setFont(new Font("Serif", 1, 14));
        this.labelElementToAnalize.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelElementToAnalize.setText(this.translations.getString("JVentanaHija.SeleccioneNodoAInspeccionar"));
        this.analysisPanel.add(this.labelElementToAnalize, "span 2, grow, wrap");
////////////////

        this.scrollPaneAnalysis.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollPaneAnalysis.setViewportView(this.analysisPanel);
        this.analysisMainContainerPanel.add(this.scrollPaneAnalysis, BorderLayout.CENTER);
        this.tabsPanel.addTab(this.translations.getString("JVentanaHija.Analisis"), this.imageBroker.getIcon(TImageBroker.ANALISIS), this.analysisMainContainerPanel, this.translations.getString("JVentanaHija.Analisis.Tooltip"));
        this.tabsPanel.setBackground(Color.WHITE);
        getContentPane().add(this.tabsPanel, BorderLayout.CENTER);
        pack();
    }

    /**
     * Este m�todo se encarga de start los atributos de la clase que no hayan
     * sido aun iniciados por NetBeans.
     *
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
        this.simulationPanel.ponerTopologia(this.scenario.getTopology());
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
        this.sliderOptionsTickDurationInNs.setMaximum(this.sliderOptionsSimulationLengthMs.getValue() * 1000000 + this.sliderOptionsSimulationLengthNs.getValue());
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.labelOptionsSimulationLengthMs.setText(this.sliderOptionsSimulationLengthMs.getValue() + this.translations.getString("VentanaHija._ms."));
        this.labelOptionsSimulationLengthNs.setText(this.sliderOptionsSimulationLengthNs.getValue() + this.translations.getString("VentanaHija._ns."));
        this.labelOptionsNsTick.setText(this.sliderOptionsTickDurationInNs.getValue() + this.translations.getString("VentanaHija_ns."));
        this.controlTemporizacionDesactivado = false;
        this.scenario.getSimulation().setSimulationPanel(this.simulationPanel);
        this.xyChart1 = null;
        this.xyChart2 = null;
        this.xyChart3 = null;
        this.barChart1 = null;
        this.barChart2 = null;
    }

    private void ratonPulsadoYSoltadoEnPanelSimulacion(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopologyElement et = this.scenario.getTopology().getElementInScreenPosition(evt.getPoint());
            if (et != null) {
                if (et.getElementType() == TTopologyElement.NODE) {
                    TNode nt = (TNode) et;
                    if (nt.getPorts().isArtificiallyCongested()) {
                        nt.getPorts().setArtificiallyCongested(false);
                    } else {
                        nt.getPorts().setArtificiallyCongested(true);
                    }
                } else if (et.getElementType() == TTopologyElement.LINK) {
                    TLink ent = (TLink) et;
                    if (ent.isBroken()) {
                        ent.setAsBrokenLink(false);
                    } else {
                        ent.setAsBrokenLink(true);
                    }
                }
            } else if (this.simulationPanel.obtenerMostrarLeyenda()) {
                this.simulationPanel.ponerMostrarLeyenda(false);
            } else {
                this.simulationPanel.ponerMostrarLeyenda(true);
            }
        } else {
            rightClickedElementInDesignPanel = null;
            designPanel.repaint();
        }
    }

    private void clicEnSeleccionalElementoEstadistica(ActionEvent evt) {
        GridBagConstraints gbc = null;
        if (this.comboBoxNodeToAnalize.getSelectedIndex() == 0) {
            //this.analysisPanel.removeAll();
            this.xyChart1 = null;
            this.xyChart2 = null;
            this.xyChart3 = null;
            this.barChart1 = null;
            this.barChart2 = null;
            this.labelScenarioTitle.setText(this.textFieldOptionsScenarioTitle.getText());
            this.labelScenarioAuthorName.setText(this.textFieldOptionsScenarioAuthorName.getText());
            this.textAreaScenarioDescription.setText(this.textAreaOptionsScenarioDescription.getText());
            this.labelElementToAnalize.setIcon(null);
            this.labelElementToAnalize.setText(this.translations.getString("JVentanaHija.SeleccioneElNodoAInspeccionar"));
            /*
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.analysisScenarioInformationPanel.add(this.labelScenarioTitle, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.analysisScenarioInformationPanel.add(this.labelScenarioAuthorName, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.analysisScenarioInformationPanel.add(this.textAreaScenarioDescription, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.insets = new Insets(10, 5, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.analysisScenarioInformationPanel.add(this.labelElementToAnalize, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            this.analysisPanel.add(this.analysisScenarioInformationPanel, gbc);
             */
            this.analysisPanel.repaint();
        } else {
            String nombreEltoSeleccionado = (String) this.comboBoxNodeToAnalize.getSelectedItem();
            this.crearEInsertarGraficas(nombreEltoSeleccionado);
        }
    }

    /**
     * Este m�todo se llama cuando se arrastra el rat�n sobre el panel de
     * dise�o. Si se hace sobre un elemento que estaba seleccionado, el
     * resultado es que ese elemento se mueve donde vaya el cursor del rat�n.
     *
     * @param evt El evento que provoca la llamada.
     * @since 2.0
     */
    private void ratonArrastradoEnPanelSimulacion(MouseEvent evt) {
        if (evt.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (this.selectedNode != null) {
                TTopology topo = this.scenario.getTopology();
                Point p2 = evt.getPoint();
                if (p2.x < 0) {
                    p2.x = 0;
                }
                if (p2.x > designPanel.getSize().width) {
                    p2.x = designPanel.getSize().width;
                }
                if (p2.y < 0) {
                    p2.y = 0;
                }
                if (p2.y > designPanel.getSize().height) {
                    p2.y = designPanel.getSize().height;
                }
                this.selectedNode.setScreenPosition(new Point(p2.x, p2.y));
                this.simulationPanel.repaint();
                this.scenario.setModified(true);
            }
        }
    }

    /**
     * Este m�todo se llama cuando se libera un bot�n del rat�n estando en el
     * panel de simulaci�n. Si se hace sobre un elemento que estaba
     * seleccionado, deja de estarlo.
     *
     * @param evt El evento que genera la llamada.
     * @since 2.0
     */
    private void ratonSoltadoEnPanelSimulacion(MouseEvent evt) {
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
     * Este m�todo se llama cuando se presiona un bot�n del rat�n en el panel de
     * simulaci�n. Si se hace sobre un elemento de la topolog�a, lo marca como
     * seleccionado.
     *
     * @since 2.0
     * @param evt El evento que provoca la llamada.
     */
    private void clicEnPanelSimulacion(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopology topo = this.scenario.getTopology();
            TTopologyElement et = topo.getElementInScreenPosition(evt.getPoint());
            if (et != null) {
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (et.getElementType() == TTopologyElement.NODE) {
                    TNode nt = (TNode) et;
                    this.selectedNode = nt;
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
     * Este m�todo se llama cuando se hace clic derecho sobre un elemento en la
     * ventana de dise�o y se selecciona la opci�n "Propiedades" del men�
     * emergente. Se encarga de mostrar en pantalla la ventana de configuraci�n
     * del elemento en cuesti�n.
     *
     * @since 2.0
     * @param evt El evento que provoca la llamada.
     */
    private void clicEnPropiedadesPopUpDisenioElemento(ActionEvent evt) {
        if (this.rightClickedElementInDesignPanel != null) {
            if (this.rightClickedElementInDesignPanel.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) this.rightClickedElementInDesignPanel;
                if (nt.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                    JTrafficGeneratorWindow ve = new JTrafficGeneratorWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    ve.setConfiguration((TTrafficGeneratorNode) nt, true);
                    ve.show();
                } else if (nt.getNodeType() == TNode.LER) {
                    JLERWindow vler = new JLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    vler.setConfiguration((TLERNode) nt, true);
                    vler.show();
                } else if (nt.getNodeType() == TNode.ACTIVE_LER) {
                    JActiveLERWindow vlera = new JActiveLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    vlera.setConfiguration((TActiveLERNode) nt, true);
                    vlera.show();
                } else if (nt.getNodeType() == TNode.LSR) {
                    JLSRWindow vlsr = new JLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    vlsr.setConfiguration((TLSRNode) nt, true);
                    vlsr.show();
                } else if (nt.getNodeType() == TNode.ACTIVE_LSR) {
                    JActiveLSRWindow vlsra = new JActiveLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    vlsra.setConfiguration((TActiveLSRNode) nt, true);
                    vlsra.show();
                } else if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                    JTrafficSinkWindow vr = new JTrafficSinkWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
                    vr.setConfiguration((TTrafficSinkNode) nt, true);
                    vr.show();
                }
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
            } else {
                TLink ent = (TLink) this.rightClickedElementInDesignPanel;
                TLinkConfig tceAux = ent.getConfig();
                JLinkWindow ve = new JLinkWindow(this.scenario.getTopology(), this.imageBroker, this.parent, true);
                ve.setConfiguration(tceAux, true);
                ve.show();
                if (ent.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink ext = (TExternalLink) ent;
                    ext.configure(tceAux, this.scenario.getTopology(), true);
                } else if (ent.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink inte = (TInternalLink) ent;
                    inte.configure(tceAux, this.scenario.getTopology(), true);
                }
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
                int minimoDelay = this.scenario.getTopology().getMinimumDelay();
                int pasoActual = this.sliderOptionsTickDurationInNs.getValue();
                if (pasoActual > minimoDelay) {
                    this.sliderOptionsTickDurationInNs.setValue(minimoDelay);
                }
            }
            this.scenario.setModified(true);
        }
    }

    /**
     * Este m�todo se encarga de controlar que la duraci�n de la simulaci�on y
     * del paso de la misma sea acorde con los delays de los enlaces. Adem�s se
     * encarga de la actualizaci�n de la interfaz en esos lugares.
     *
     * @since 2.0
     */
    public void controlarParametrosTemporales() {
        if (!this.controlTemporizacionDesactivado) {
            if (this.sliderOptionsSimulationLengthMs.getValue() == 0) {
                this.sliderOptionsSimulationLengthNs.setMinimum(1);
            } else {
                this.sliderOptionsSimulationLengthNs.setMinimum(0);
            }
            int duracionTotal = this.sliderOptionsSimulationLengthMs.getValue() * 1000000 + this.sliderOptionsSimulationLengthNs.getValue();
            int minDelay = this.scenario.getTopology().getMinimumDelay();
            if (minDelay < duracionTotal) {
                this.sliderOptionsTickDurationInNs.setMaximum(minDelay);
                // FIX: Change also current value of this slider
            } else {
                this.sliderOptionsTickDurationInNs.setMaximum(duracionTotal);
                // FIX: Change also current value of this slider
            }
            this.labelOptionsSimulationLengthMs.setText(this.sliderOptionsSimulationLengthMs.getValue() + this.translations.getString("VentanaHija._ms."));
            this.labelOptionsSimulationLengthNs.setText(this.sliderOptionsSimulationLengthNs.getValue() + this.translations.getString("VentanaHija._ns."));
            this.labelOptionsNsTick.setText(this.sliderOptionsTickDurationInNs.getValue() + this.translations.getString("VentanaHija._ns."));
            this.scenario.getSimulation().setSimulationLengthInNs(new TTimestamp(this.sliderOptionsSimulationLengthMs.getValue(), this.sliderOptionsSimulationLengthNs.getValue()).getTotalAsNanoseconds());
            this.scenario.getSimulation().setSimulationStepLengthInNs(this.sliderOptionsTickDurationInNs.getValue());
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia la duraci�n en
     * nanosegundos del paso de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que el m�todo salte.
     */
    private void clicEnPasoNs(ChangeEvent evt) {
        controlarParametrosTemporales();
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia la duraci�n de la
     * simulaci�n en nanosegundos.
     *
     * @since 2.0
     * @param evt Evento que hace que se ejecute este m�todo.
     */
    private void clicEnDuracionNs(ChangeEvent evt) {
        controlarParametrosTemporales();
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia la duraci�n de la
     * simulaci�n en milisegundos.
     *
     * @since 2.0
     * @param evt Evento que produce que se ejecute este m�todo.
     */
    private void clicEnDuracionMs(ChangeEvent evt) {
        controlarParametrosTemporales();
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia el tiempo que se
     * detendr� la simulaci�n entre un paso de simulaci�n y otro.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void mlsPorTicCambiado(ChangeEvent evt) {
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.etiquetaMsTic"));
        this.simulationPanel.ponerMlsPorTic(this.sliderSimulationSpeedInMsPerTick.getValue());
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ocultar el
     * nombre de todos los enlaces, en el men� emergente de la pantalla de
     * Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoOcultarNombreEnlaces(ActionEvent evt) {
        Iterator it = this.scenario.getTopology().getLinksIterator();
        TLink enlaceAux;
        while (it.hasNext()) {
            enlaceAux = (TLink) it.next();
            enlaceAux.setShowName(false);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ver el nombre
     * de todos los enlaces, en el men� emergente de la pantalla de Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoVerNombreEnlaces(ActionEvent evt) {
        Iterator it = this.scenario.getTopology().getLinksIterator();
        TLink enlaceAux;
        while (it.hasNext()) {
            enlaceAux = (TLink) it.next();
            enlaceAux.setShowName(true);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ocultar el
     * nombre de todos los nodos, en el men� emergente de la pantalla de
     * Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoOcultarNombreNodos(ActionEvent evt) {
        Iterator it = this.scenario.getTopology().getNodesIterator();
        TNode nodoAux;
        while (it.hasNext()) {
            nodoAux = (TNode) it.next();
            nodoAux.setShowName(false);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ver el nombre
     * de todos los nodos, en el men� emergente de la pantalla de Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoVerNombreNodos(ActionEvent evt) {
        Iterator it = this.scenario.getTopology().getNodesIterator();
        TNode nodoAux;
        while (it.hasNext()) {
            nodoAux = (TNode) it.next();
            nodoAux.setShowName(true);
        }
        this.designPanel.repaint();
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de eliminar todo
     * el escenario completo, en el men� emergente de la pantalla de Disenio.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoEliminar(ActionEvent evt) {
        JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
        vb.setQuestion(this.translations.getString("JVentanaHija.PreguntaBorrarTodo"));
        vb.show();
        boolean respuesta = vb.getUserAnswer();
        if (respuesta) {
            this.scenario.getTopology().removeAllElements();
            this.designPanel.repaint();
        }
        this.scenario.setModified(true);
    }

    /**
     * Este m�todo asigna un escenario ya creado a la ventana hija. A partir de
     * ese momento todo lo que se haga en la ventana tendr� su repercusi�n en el
     * escenario.
     *
     * @param esc Escenario ya creado al que se va a asociar esta ventana hija y
     * que contendr� un escenario y todos sus datos.
     * @since 2.0
     */
    public void setScenario(TScenario esc) {
        this.controlTemporizacionDesactivado = true;
        long durac = esc.getSimulation().getSimulationLengthInNs();
        long pas = esc.getSimulation().getSimulationStepLengthInNs();
        this.scenario = esc;
        this.designPanel.setTopology(esc.getTopology());
        this.simulationPanel.ponerTopologia(esc.getTopology());
        this.selectedNode = null;
        this.rightClickedElementInDesignPanel = null;
        this.progressEventListener = new TProgressEventListener(this.progressBarSimulation);
        try {
            esc.getTopology().getTimer().addProgressEventListener(this.progressEventListener);
        } catch (EProgressEventGeneratorOnlyAllowASingleListener e) {
            e.printStackTrace();
        }
        this.sliderOptionsSimulationLengthMs.setValue((int) (durac / 1000000));
        this.sliderOptionsSimulationLengthNs.setValue((int) (durac - (this.sliderOptionsSimulationLengthMs.getValue() * 1000000)));
        this.sliderOptionsTickDurationInNs.setMaximum((int) esc.getSimulation().getSimulationLengthInNs());
        this.sliderOptionsTickDurationInNs.setValue((int) pas);
        esc.getSimulation().setSimulationLengthInNs(durac);
        esc.getSimulation().setSimulationStepLengthInNs(pas);
        this.labelSimulationSpeedFaster.setText(this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.labelOptionsSimulationLengthMs.setText(this.sliderOptionsSimulationLengthMs.getValue() + this.translations.getString("VentanaHija._ms."));
        this.labelOptionsSimulationLengthNs.setText(this.sliderOptionsSimulationLengthNs.getValue() + this.translations.getString("VentanaHija._ns."));
        this.labelOptionsNsTick.setText(this.sliderOptionsTickDurationInNs.getValue() + this.translations.getString("VentanaHija_ns."));
        this.textFieldOptionsScenarioAuthorName.setText(esc.getAuthor());
        this.textFieldOptionsScenarioAuthorName.setCaretPosition(1);
        this.textFieldOptionsScenarioTitle.setText(esc.getTitle());
        this.textFieldOptionsScenarioTitle.setCaretPosition(1);
        this.textAreaOptionsScenarioDescription.setText(esc.getDescription());
        this.textAreaOptionsScenarioDescription.setCaretPosition(1);
        this.controlTemporizacionDesactivado = false;
        this.scenario.getSimulation().setSimulationPanel(this.simulationPanel);
        this.controlarParametrosTemporales();
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un
     * enlace nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirEnlace(MouseEvent evt) {
        if (this.scenario.getTopology().getNumberOfNodes() < 2) {
            JWarningWindow va = new JWarningWindow(this.parent, true, this.imageBroker);
            va.setWarningMessage(this.translations.getString("VentanaHija.ErrorAlMenosDosNodos"));
            va.show();
        } else {
            TLinkConfig config = new TLinkConfig();
            JLinkWindow venlace = new JLinkWindow(this.scenario.getTopology(), this.imageBroker, this.parent, true);
            venlace.setConfiguration(config, false);
            //venlace.loadAllNodesThatHaveAvailablePorts();
            venlace.show();
            if (config.isWellConfigured()) {
                try {
                    if (config.getLinkType() == TLink.INTERNAL_LINK) {
                        TInternalLink enlaceInterno = new TInternalLink(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
                        enlaceInterno.configure(config, scenario.getTopology(), false);
                        this.scenario.getTopology().addLink(enlaceInterno);
                    } else {
                        TExternalLink enlaceExterno = new TExternalLink(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
                        enlaceExterno.configure(config, this.scenario.getTopology(), false);
                        this.scenario.getTopology().addLink(enlaceExterno);
                    }
                    this.designPanel.repaint();
                } catch (Exception e) {
                    JErrorWindow err;
                    err = new JErrorWindow(this.parent, true, this.imageBroker);
                    err.setErrorMessage(e.toString());
                    err.show();
                }
                this.scenario.setModified(true);
            } else {
                config = null;
            }
        }
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n eliminar que
     * aparece en el men� emergente al pulsar con el bot�n derecho sobre un
     * elemento de la topolog�a. En la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioEliminar(ActionEvent evt) {
        JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
        vb.setQuestion(this.translations.getString("JVentanaHija.preguntaAlEliminar"));
        vb.show();
        boolean respuesta = vb.getUserAnswer();
        if (respuesta) {
            if (this.rightClickedElementInDesignPanel != null) {
                if (this.rightClickedElementInDesignPanel.getElementType() == TTopologyElement.NODE) {
                    TNode nt = (TNode) this.rightClickedElementInDesignPanel;
                    if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                        if (this.scenario.getTopology().isThereAnyNodeGeneratingTrafficFor((TTrafficSinkNode) nt)) {
                            JWarningWindow va;
                            va = new JWarningWindow(this.parent, true, this.imageBroker);
                            va.setWarningMessage(this.translations.getString("JVentanaHija.NoPuedoBorrarReceptor"));
                            va.show();
                            this.rightClickedElementInDesignPanel = null;
                        } else {
                            this.scenario.getTopology().disconnectNodeAndRemove(nt);
                            this.rightClickedElementInDesignPanel = null;
                            this.designPanel.repaint();
                        }
                    } else {
                        this.scenario.getTopology().disconnectNodeAndRemove(nt);
                        this.rightClickedElementInDesignPanel = null;
                        this.designPanel.repaint();
                    }
                } else {
                    TLink ent = (TLink) this.rightClickedElementInDesignPanel;
                    this.scenario.getTopology().removeLink(ent);
                    this.rightClickedElementInDesignPanel = null;
                    this.designPanel.repaint();
                }
                this.scenario.setModified(true);
            }
        }

    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ver/ocultar
     * nombre que aparece en el men� emergente al pulsar con el bot�n derecho
     * sobre un elemento de la topolog�a. En la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioVerNombre(ActionEvent evt) {
        if (this.rightClickedElementInDesignPanel != null) {
            if (this.rightClickedElementInDesignPanel.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) this.rightClickedElementInDesignPanel;
                nt.setShowName(this.chekBoxMenuItemShowElementName.isSelected());
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
            } else {
                TLink ent = (TLink) this.rightClickedElementInDesignPanel;
                ent.setShowName(this.chekBoxMenuItemShowElementName.isSelected());
                this.rightClickedElementInDesignPanel = null;
                this.designPanel.repaint();
            }
            this.scenario.setModified(true);
        }
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic con el bot�n derecho en la
     * pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que este m�todo se dispare.
     */
    private void clicDerechoEnPanelDisenio(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            TTopologyElement et = this.scenario.getTopology().getElementInScreenPosition(evt.getPoint());
            if (et == null) {
                this.popupMenuBackgroundDesignPanel.show(this, evt.getX() + 7, evt.getY() - 27);
            } else if (et.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) et;
                this.chekBoxMenuItemShowElementName.setSelected(nt.getShowName());
                this.rightClickedElementInDesignPanel = et;
                this.popupMenuTopologyElement.show(this, evt.getX() + 7, evt.getY() + 15);
            } else if (et.getElementType() == TTopologyElement.LINK) {
                TLink ent = (TLink) et;
                this.chekBoxMenuItemShowElementName.setSelected(ent.getShowName());
                this.rightClickedElementInDesignPanel = et;
                this.popupMenuTopologyElement.show(this, evt.getX() + 7, evt.getY() + 15);
            }
        } else {
            this.rightClickedElementInDesignPanel = null;
            this.designPanel.repaint();
        }
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LSRA
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirLSRA(MouseEvent evt) {
        TActiveLSRNode lsra = null;
        try {
            lsra = new TActiveLSRNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(this.parent, true, this.imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JActiveLSRWindow vlsra = new JActiveLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        vlsra.setConfiguration(lsra, false);
        vlsra.show();
        if (lsra.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(lsra);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(this.parent, true, this.imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            };
            this.scenario.setModified(true);
        } else {
            lsra = null;
        }
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LSR
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que este m�todo se dispare.
     */
    private void clicEnAniadirLSR(MouseEvent evt) {
        TLSRNode lsr = null;
        try {
            lsr = new TLSRNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(this.parent, true, this.imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JLSRWindow vlsr = new JLSRWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        vlsr.setConfiguration(lsr, false);
        vlsr.show();
        if (lsr.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(lsr);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(this.parent, true, this.imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            }
            this.scenario.setModified(true);
        } else {
            lsr = null;
        }
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LSRA
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirLERA(MouseEvent evt) {
        TActiveLERNode lera = null;
        try {
            lera = new TActiveLERNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(this.parent, true, this.imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JActiveLERWindow vlera = new JActiveLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        vlera.setConfiguration(lera, false);
        vlera.show();
        if (lera.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(lera);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(this.parent, true, this.imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            }
            this.scenario.setModified(true);
        } else {
            lera = null;
        }
    }

    /**
     * Este m�todo se ejecuta cuando se mueve el rat�n dentro del �rea de
     * simulaci�n , en la pantalla de simulaci�n. Entre otras cosas, cambia el
     * cursor del rat�n al pasar sobre un elemento, permite mostrar men�s
     * emergentes coherentes con el contexto de donde se encuentra el rat�n,
     * etc�tera.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSobrePanelSimulacion(MouseEvent evt) {
        TTopology topo = this.scenario.getTopology();
        TTopologyElement et = topo.getElementInScreenPosition(evt.getPoint());
        if (et != null) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (et.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) et;
                if (nt.getPorts().isArtificiallyCongested()) {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.Congestion") + nt.getPorts().getCongestionLevel() + this.translations.getString("JVentanaHija.POrcentaje") + this.translations.getString("VentanaHija.paraDejarDeCongestionar"));
                } else {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.Congestion") + nt.getPorts().getCongestionLevel() + this.translations.getString("JVentanaHija.POrcentaje") + this.translations.getString("VentanaHija.paraCongestionar"));
                }
            } else if (et.getElementType() == TTopologyElement.LINK) {
                TLink ent = (TLink) et;
                if (ent.isBroken()) {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.EnlaceRoto"));
                } else {
                    this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.EnlaceFuncionando"));
                }
            }
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.simulationPanel.setToolTipText(null);
            if (!this.simulationPanel.obtenerMostrarLeyenda()) {
                this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.VerLeyenda"));
            } else {
                this.simulationPanel.setToolTipText(this.translations.getString("JVentanaHija.OcultarLeyenda"));
            }
        }
    }

    /**
     * Este m�todo se ejecuta cuando se mueve el rat�n dentro del �rea de
     * dise�o, en la pantalla de Dise�o. Entre otras cosas, cambia el cursor del
     * rat�n al pasar sobre un elemento, permite mostrar men�s emergentes
     * coherentes con el contexto de donde se encuentra el rat�n, etc�tera.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSobrePanelDisenio(MouseEvent evt) {
        TTopology topo = this.scenario.getTopology();
        TTopologyElement et = topo.getElementInScreenPosition(evt.getPoint());
        if (et != null) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (et.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) et;
                this.designPanel.setToolTipText(this.translations.getString("JVentanaHija.PanelDisenio.IP") + nt.getIPv4Address());
            } else if (et.getElementType() == TTopologyElement.LINK) {
                TLink ent = (TLink) et;
                this.designPanel.setToolTipText(this.translations.getString("JVentanaHija.panelDisenio.Retardo") + ent.getDelay() + this.translations.getString("JVentanaHija.panelDisenio.ns"));
            }
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.designPanel.setToolTipText(null);
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se est� arrastrando el rat�n
     * en la pantalla de dise�o. Se encarga de mover los elementos de un lugar a
     * otro para dise�ar la topolog�a.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void arrastrandoEnPanelDisenio(MouseEvent evt) {
        if (evt.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (this.selectedNode != null) {
                TTopology topo = scenario.getTopology();
                Point p2 = evt.getPoint();
                if (p2.x < 0) {
                    p2.x = 0;
                }
                if (p2.x > this.designPanel.getSize().width) {
                    p2.x = this.designPanel.getSize().width;
                }
                if (p2.y < 0) {
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
     * Este m�todo se llama autom�ticamente cuando soltamos el bot�n del raton a
     * la rrastrar o al hacer clic. Si el rat�n estaba sobre un elemento de la
     * topology, se marca �ste como no seleccionado.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicSoltadoEnPanelDisenio(MouseEvent evt) {
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
     * Este m�todo se llama autom�ticamente cuando se hace un clic con el bot�n
     * izquierdo sobre la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnPanelDisenio(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopology topo = this.scenario.getTopology();
            this.selectedNode = topo.getNodeInScreenPosition(evt.getPoint());
            if (this.selectedNode != null) {
                this.selectedNode.setSelected(TNode.SELECTED);
                this.scenario.setModified(true);
            }
            this.designPanel.repaint();
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono de
     * detener en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoPausar(MouseEvent evt) {
        this.iconContainerPauseSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PAUSA));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono de
     * detener en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoPausar(MouseEvent evt) {
        this.iconContainerPauseSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PAUSA_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono de
     * finalizar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoFinalizar(MouseEvent evt) {
        this.iconContainerFinishSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PARAR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono de
     * finalizar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoFinalizar(MouseEvent evt) {
        this.iconContainerFinishSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PARAR_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono de
     * comenzar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoReanudar(MouseEvent evt) {
        this.iconContainerResumeSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_COMENZAR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono de
     * comenzar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoReanudar(MouseEvent evt) {
        this.iconContainerResumeSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_COMENZAR_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * generar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoComenzar(MouseEvent evt) {
        this.iconContainterStartSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_GENERAR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * generar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoComenzar(MouseEvent evt) {
        this.iconContainterStartSimulation.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_GENERAR_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LER
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirLER(MouseEvent evt) {
        TLERNode ler = null;
        try {
            ler = new TLERNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(this.parent, true, this.imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JLERWindow vler = new JLERWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        vler.setConfiguration(ler, false);
        vler.show();
        if (ler.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(ler);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(this.parent, true, this.imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            }
            this.scenario.setModified(true);
        } else {
            ler = null;
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * enlace en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo
     */
    private void ratonSaleDeIconoEnlace(MouseEvent evt) {
        this.iconContainerLink.setIcon(this.imageBroker.getIcon(TImageBroker.ENLACE_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * enlace en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoEnlace(MouseEvent evt) {
        this.iconContainerLink.setIcon(this.imageBroker.getIcon(TImageBroker.ENLACE_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LSRA
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLSRA(MouseEvent evt) {
        this.iconContainerActiveLSR.setIcon(this.imageBroker.getIcon(TImageBroker.LSRA_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LSRA en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLSRA(MouseEvent evt) {
        this.iconContainerActiveLSR.setIcon(this.imageBroker.getIcon(TImageBroker.LSRA_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LSR
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLSR(MouseEvent evt) {
        this.iconContainerLSR.setIcon(this.imageBroker.getIcon(TImageBroker.LSR_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LSR en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLSR(MouseEvent evt) {
        this.iconContainerLSR.setIcon(this.imageBroker.getIcon(TImageBroker.LSR_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LERA
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLERA(MouseEvent evt) {
        this.iconContainerActiveLER.setIcon(this.imageBroker.getIcon(TImageBroker.LERA_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LERA en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLERA(MouseEvent evt) {
        this.iconContainerActiveLER.setIcon(this.imageBroker.getIcon(TImageBroker.LERA_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LER
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLER(MouseEvent evt) {
        this.iconContainerLER.setIcon(this.imageBroker.getIcon(TImageBroker.LER_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LER en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLER(MouseEvent evt) {
        this.iconContainerLER.setIcon(this.imageBroker.getIcon(TImageBroker.LER_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * receptor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoReceptor(MouseEvent evt) {
        this.iconContainerTrafficSink.setIcon(this.imageBroker.getIcon(TImageBroker.RECEPTOR_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * receptor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoReceptor(MouseEvent evt) {
        this.iconContainerTrafficSink.setIcon(this.imageBroker.getIcon(TImageBroker.RECEPTOR_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * emisor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoEmisor(MouseEvent evt) {
        this.iconContainerTrafficGeneratorNode.setIcon(this.imageBroker.getIcon(TImageBroker.EMISOR_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * emisor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoEmisor(MouseEvent evt) {
        this.iconContainerTrafficGeneratorNode.setIcon(this.imageBroker.getIcon(TImageBroker.EMISOR_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * receptor en la ventana de dise�o. A�ade un receptor nuevo en la topology.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirReceptor(MouseEvent evt) {
        TTrafficSinkNode receptor = null;
        try {
            receptor = new TTrafficSinkNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(this.parent, true, this.imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JTrafficSinkWindow vr = new JTrafficSinkWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
        vr.setConfiguration(receptor, false);
        vr.show();
        if (receptor.isWellConfigured()) {
            try {
                this.scenario.getTopology().addNode(receptor);
                this.designPanel.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(this.parent, true, this.imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            }
            this.scenario.setModified(true);
        } else {
            receptor = null;
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * emisor en la ventana de dise�o. A�ade un emisor nuevo en la topology.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirEmisorDeTrafico(MouseEvent evt) {
        TTopology t = this.scenario.getTopology();
        Iterator it = t.getNodesIterator();
        TNode nt;
        boolean hayDestino = false;
        while (it.hasNext()) {
            nt = (TNode) it.next();
            if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                hayDestino = true;
            }
        }
        if (!hayDestino) {
            JWarningWindow va = new JWarningWindow(this.parent, true, this.imageBroker);
            va.setWarningMessage(this.translations.getString("JVentanaHija.NecesitaHaberUnReceptor"));
            va.show();
        } else {
            TTrafficGeneratorNode emisor = null;
            try {
                emisor = new TTrafficGeneratorNode(this.scenario.getTopology().getElementsIDGenerator().getNew(), this.scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), this.scenario.getTopology().getEventIDGenerator(), this.scenario.getTopology());
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(this.parent, true, this.imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            }
            JTrafficGeneratorWindow ve = new JTrafficGeneratorWindow(this.scenario.getTopology(), this.designPanel, this.imageBroker, this.parent, true);
            ve.setConfiguration(emisor, false);
            ve.show();
            if (emisor.isWellConfigured()) {
                try {
                    this.scenario.getTopology().addNode(emisor);
                    this.designPanel.repaint();
                } catch (Exception e) {
                    JErrorWindow err;
                    err = new JErrorWindow(this.parent, true, this.imageBroker);
                    err.setErrorMessage(e.toString());
                    err.show();
                }
                this.scenario.setModified(true);
            } else {
                emisor = null;
            }
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * detener en la ventana de simulaci�n. Detiene la simulaci�n o su
     * generaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que este m�todo se dispare.
     */
    private void clicAlPausar(MouseEvent evt) {
        if (this.iconContainerPauseSimulation.isEnabled()) {
            this.scenario.getTopology().getTimer().setPaused(true);
            activarOpcionesAlDetener();
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * finalizar en la ventana de simulaci�n. Detiene la simulaci�n por
     * completo.
     *
     * @since 2.0
     * @param evt El evento que hace que este m�todo se dispare.
     */
    private void clicEnFinalizar(MouseEvent evt) {
        if (this.iconContainerFinishSimulation.isEnabled()) {
            this.scenario.getTopology().getTimer().reset();
            this.simulationPanel.ponerFicheroTraza(null);
            activarOpcionesAlFinalizar();
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * comenzar en la ventana de simulaci�n. Inicia la simulaci�n.
     *
     * @since 2.0
     * @param evt El evento que hace que este m�todo se dispare.
     */
    private void clicEnReanudar(MouseEvent evt) {
        if (this.iconContainerResumeSimulation.isEnabled()) {
            activarOpcionesAlComenzar();
            this.scenario.getTopology().getTimer().setPaused(false);
            this.scenario.getTopology().getTimer().restart();
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * generar en la ventana de simulaci�n. Crea la simulaci�n.
     *
     * @since 2.0
     * @param evt El evento que hace que este m�todo se dispare.
     */
    private void clicEnComenzar(MouseEvent evt) {
        if (this.iconContainterStartSimulation.isEnabled()) {
            this.scenario.reset();
            if (!this.scenario.getTopology().getTimer().isRunning()) {
                this.scenario.getTopology().getTimer().setFinishTimestamp(new TTimestamp(this.sliderOptionsSimulationLengthMs.getValue(), this.sliderOptionsSimulationLengthNs.getValue()));
            }
            this.scenario.getTopology().getTimer().setTick(this.sliderOptionsTickDurationInNs.getValue());
            crearListaElementosEstadistica();
            this.scenario.setModified(true);
            this.scenario.getTopology().getTimer().reset();
            this.simulationPanel.reset();
            this.simulationPanel.repaint();
            this.scenario.simulate();
            int minimoDelay = this.scenario.getTopology().getMinimumDelay();
            int pasoActual = this.sliderOptionsTickDurationInNs.getValue();
            if (pasoActual > minimoDelay) {
                this.sliderOptionsTickDurationInNs.setValue(minimoDelay);
            }
            activarOpcionesTrasGenerar();
        }
    }

    /**
     * Este m�todo se llama cuando comienza la simulaci�n del escenario. Crea
     * una lista de todos los nodos que tienen activa la generaci�n de
     * estad�sticas para posteriormente poder elegir uno de ellos y ver sus
     * gr�ficas.
     *
     * @since 2.0
     */
    public void crearListaElementosEstadistica() {
        Iterator it = null;
        TNode nt = null;
        TLink et = null;
        this.comboBoxNodeToAnalize.removeAllItems();
        this.comboBoxNodeToAnalize.addItem("");
        it = this.scenario.getTopology().getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            if (nt.isGeneratingStats()) {
                this.comboBoxNodeToAnalize.addItem(nt.getName());
            }
        }
        this.comboBoxNodeToAnalize.setSelectedIndex(0);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n est� detenida.
     *
     * @since 2.0
     */
    private void activarOpcionesAlDetener() {
        this.iconContainterStartSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.setEnabled(true);
        this.iconContainerFinishSimulation.setEnabled(true);
        this.iconContainerPauseSimulation.setEnabled(false);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n ha finalizado.
     *
     * @since 2.0
     */
    private void activarOpcionesAlFinalizar() {
        this.iconContainterStartSimulation.setEnabled(true);
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerFinishSimulation.setEnabled(false);
        this.iconContainerPauseSimulation.setEnabled(false);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n se acaba de generar.
     *
     * @since 2.0
     */
    private void activarOpcionesTrasGenerar() {
        this.iconContainterStartSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerFinishSimulation.setEnabled(true);
        this.iconContainerPauseSimulation.setEnabled(true);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n comienza.
     *
     * @since 2.0
     */
    private void activarOpcionesAlComenzar() {
        this.iconContainterStartSimulation.setEnabled(false);
        this.iconContainerResumeSimulation.setEnabled(false);
        this.iconContainerFinishSimulation.setEnabled(true);
        this.iconContainerPauseSimulation.setEnabled(true);
    }

    /**
     * Cierra la ventana hija y pierde o almacena su contenido en funci�n de la
     * elecci�n del usuario.
     *
     * @since 2.0
     */
    public void cerrar() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * Este m�todo se encarga de controlar que todo ocurre como debe con
     * respecto al escenario, cuando se pulsa en el men� principal la opci�n de
     * "Guardar como..."
     *
     * @since 2.0
     */
    public void handleSaveAs() {
        anotarDatosDeEscenario();
        JFileChooser dialogoGuardar = new JFileChooser();
        dialogoGuardar.setFileFilter(new JOSMFilter());
        dialogoGuardar.setDialogType(JFileChooser.CUSTOM_DIALOG);
        // FIX: i18N required
        dialogoGuardar.setApproveButtonMnemonic('A');
        dialogoGuardar.setApproveButtonText(this.translations.getString("JVentanaHija.DialogoGuardar.OK"));
        dialogoGuardar.setDialogTitle(this.translations.getString("JVentanaHija.DialogoGuardar.Almacenar") + this.getTitle() + this.translations.getString("-"));
        dialogoGuardar.setAcceptAllFileFilterUsed(false);
        dialogoGuardar.setSelectedFile(new File(this.getTitle()));
        dialogoGuardar.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int resultado = dialogoGuardar.showSaveDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String ext = null;
            String nombreFich = dialogoGuardar.getSelectedFile().getPath();
            int i = nombreFich.lastIndexOf('.');
            if (i > 0 && i < nombreFich.length() - 1) {
                ext = nombreFich.substring(i + 1).toLowerCase();
            }
            if (ext == null) {
                nombreFich += this.translations.getString(".osm");
            } else if (!ext.equals(this.translations.getString("osm"))) {
                nombreFich += this.translations.getString(".osm");
            }
            dialogoGuardar.setSelectedFile(new File(nombreFich));
            this.scenario.setScenarioFile(dialogoGuardar.getSelectedFile());
            this.scenario.setSaved(true);
            this.setTitle(this.scenario.getScenarioFile().getName());
            TOSMSaver almacenador = new TOSMSaver(this.scenario);
            JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
            vb.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
            vb.show();
            boolean conCRC = vb.getUserAnswer();
            boolean correcto = almacenador.save(this.scenario.getScenarioFile(), conCRC);
            if (correcto) {
                this.scenario.setModified(false);
                this.scenario.setSaved(true);
            }
        }
    }

    /**
     * Este m�todo se encarga de controlar que todo ocurre como debe con
     * respecto al escenario, cuando se pulsa en el men� principal la opci�n de
     * "Cerrar" o "Salir" y el escenario actual no est� a�n guardado o est�
     * modificado.
     *
     * @since 2.0
     */
    public void gestionarGuardarParaCerrar() {
        boolean guardado = this.scenario.isSaved();
        boolean modificado = this.scenario.isModified();
        anotarDatosDeEscenario();

        // Detengo la simulaci�n antes de cerrar, si es necesario.
        if (this.scenario.getTopology().getTimer().isRunning()) {
            this.simulationPanel.reset();
            this.simulationPanel.repaint();
            this.scenario.reset();
            if (!this.scenario.getTopology().getTimer().isRunning()) {
                this.scenario.getTopology().getTimer().setFinishTimestamp(new TTimestamp(this.sliderOptionsSimulationLengthMs.getValue(), this.sliderOptionsSimulationLengthNs.getValue()));
            }
            this.scenario.getTopology().getTimer().setTick(this.sliderOptionsTickDurationInNs.getValue());
            this.scenario.getTopology().getTimer().setPaused(false);
            activarOpcionesAlFinalizar();
        }

        if (!guardado) {
            JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
            vb.setQuestion(this.getTitle() + this.translations.getString("JVentanaHija.DialogoGuardar.GuardarPrimeraVez"));
            vb.show();
            boolean respuesta = vb.getUserAnswer();
            vb.dispose();
            if (respuesta) {
                this.handleSaveAs();
            }
        } else if ((guardado) && (!modificado)) {
            // No se hace nada, ya est� todo guardado correctamente.
        } else if ((guardado) && (modificado)) {
            JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
            vb.setQuestion(this.translations.getString("JVentanaHija.DialogoGuardar.CambiosSinguardar1") + " " + this.getTitle() + " " + this.translations.getString("JVentanaHija.DialogoGuardar.CambiosSinguardar2"));
            vb.show();
            boolean respuesta = vb.getUserAnswer();
            vb.dispose();
            if (respuesta) {
                TOSMSaver almacenador = new TOSMSaver(this.scenario);
                JDecissionWindow vb2 = new JDecissionWindow(this.parent, true, this.imageBroker);
                vb2.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
                vb2.show();
                boolean conCRC = vb2.getUserAnswer();
                boolean correcto = almacenador.save(this.scenario.getScenarioFile(), conCRC);
                if (correcto) {
                    this.scenario.setModified(false);
                    this.scenario.setSaved(true);
                }
            }
        }
    }

    /**
     * Este m�todo se encarga de controlar que todo ocurre como debe con
     * respecto al escenario, cuando se pulsa en el men� principal la opci�n de
     * "Guardar".
     *
     * @since 2.0
     */
    public void handleSave() {
        boolean guardado = this.scenario.isSaved();
        boolean modificado = this.scenario.isModified();
        anotarDatosDeEscenario();
        if (!guardado) {
            this.handleSaveAs();
        } else {
            TOSMSaver almacenador = new TOSMSaver(this.scenario);
            JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
            vb.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
            vb.show();
            boolean conCRC = vb.getUserAnswer();
            boolean correcto = almacenador.save(this.scenario.getScenarioFile(), conCRC);
            if (correcto) {
                this.scenario.setModified(false);
                this.scenario.setSaved(true);
            }
            this.scenario.setModified(false);
            this.scenario.setSaved(true);
        }
    }

    private void crearEInsertarGraficas(String nombre) {
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
        this.labelElementToAnalize.setText(nombre);
        TNode nt = this.scenario.getTopology().getFirstNodeNamed(nombre);
        if (nt != null) {
            if (nt.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getIcon(TImageBroker.EMISOR));
            } else if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getIcon(TImageBroker.RECEPTOR));
            } else if (nt.getNodeType() == TNode.LER) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getIcon(TImageBroker.LER));
            } else if (nt.getNodeType() == TNode.ACTIVE_LER) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getIcon(TImageBroker.LERA));
            } else if (nt.getNodeType() == TNode.LSR) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getIcon(TImageBroker.LSR));
            } else if (nt.getNodeType() == TNode.ACTIVE_LSR) {
                this.labelElementToAnalize.setIcon(this.imageBroker.getIcon(TImageBroker.LSRA));
            }
            int numeroGraficos = nt.getStats().getNumberOfAvailableDatasets();

            if (numeroGraficos > 0) {
                this.xyChart1 = new JXYChart(nt.getStats().getTitleOfDataset1(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset1());
                this.analysisPanel.add(this.xyChart1.getChartPanel(), "grow");
            }
            if (numeroGraficos > 1) {

                this.xyChart2 = new JXYChart(nt.getStats().getTitleOfDataset2(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset2());
                this.analysisPanel.add(this.xyChart2.getChartPanel(), "grow, wrap");
            }
            if (numeroGraficos > 2) {
                this.xyChart3 = new JXYChart(nt.getStats().getTitleOfDataset3(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset3());
                this.analysisPanel.add(this.xyChart3.getChartPanel(), "grow");
            }
            if (numeroGraficos > 3) {
                this.barChart1 = new JBarChart(nt.getStats().getTitleOfDataset4(), TStats.DESCRIPTION, TStats.NUMBER, (DefaultCategoryDataset) nt.getStats().getDataset4());
                this.analysisPanel.add(this.barChart1.getChartPanel(), "grow, wrap");
            }
            if (numeroGraficos > 4) {
                this.barChart2 = new JBarChart(nt.getStats().getTitleOfDataset5(), TStats.DESCRIPTION, TStats.NUMBER, (DefaultCategoryDataset) nt.getStats().getDataset5());
                this.analysisPanel.add(this.barChart2.getChartPanel(), "grow");
            }
            if (numeroGraficos > 5) {
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
     * Este m�todo se encarga de anotar los datos del escenario desde la
     * interfaz de usuario hasta los correspondientes atributos del objeto que
     * almacena el escenario.
     *
     * @since 2.0
     */
    private void anotarDatosDeEscenario() {
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
    private boolean controlTemporizacionDesactivado;
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
    private JLabel iconContainerFinishSimulation;
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
    private JSeparator jSeparator2;
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
