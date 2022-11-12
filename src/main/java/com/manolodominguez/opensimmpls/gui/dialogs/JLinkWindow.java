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
package com.manolodominguez.opensimmpls.gui.dialogs;

import com.manolodominguez.opensimmpls.resources.images.AvailableImages;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.scenario.TLinkConfig;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.gui.utils.TImageBroker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * This class implements a window that is used to configure and reconfigure a
 * link.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
@SuppressWarnings("serial")
public class JLinkWindow extends JDialog {

    /**
     * This is the constructor of the class and creates a new instance of
     * JLinkWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param parent Parent window over wich this JLinkWindow is shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param modal TRUE, if this dialog has to be modal. Otherwise, FALSE.
     * @param topology Topology the link node belongs to.
     * @since 2.0
     */
    public JLinkWindow(TTopology topology, TImageBroker imageBroker, Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.imageBroker = imageBroker;
        this.topology = topology;
        this.linkConfig = null;
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
        this.translations = ResourceBundle.getBundle(AvailableBundles.LINK_WINDOW.getPath());
        this.mainPanel = new JPanel();
        this.panelTabs = new JTabbedPane();
        this.panelGeneralConfiguration = new JPanel();
        this.labelLink = new JLabel();
        this.labelName = new JLabel();
        this.textFieldName = new JTextField();
        this.checkBoxShowName = new JCheckBox();
        this.iconContainerLink = new JLabel();
        this.labelHeadEnd = new JLabel();
        this.labelTailEnd = new JLabel();
        this.comboBoxTailEndNode = new JComboBox<>();
        this.comboBoxHeadEndNode = new JComboBox<>();
        this.comboBoxTailEndNodePort = new JComboBox<>();
        this.comboBoxHeadEndNodePort = new JComboBox<>();
        this.labelHeadEndColons = new JLabel();
        this.labelTailEndColons = new JLabel();
        this.panelQuickConfiguration = new JPanel();
        this.iconContainerWizard = new JLabel();
        this.labelSpeed = new JLabel();
        this.comboBoxPredefinedOptions = new JComboBox<>();
        this.panelAdvancedConfiguration = new JPanel();
        this.iconContainerToolbox = new JLabel();
        this.labelDelay = new JLabel();
        this.sliderDelay = new JSlider();
        this.labelNs = new JLabel();
        this.panelButtons = new JPanel();
        this.buttonOK = new JButton();
        this.buttonCancel = new JButton();
        setTitle(this.translations.getString("VentanaEnlace.titulo"));
        setModal(true);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleWindowsClosing(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());
        this.mainPanel.setLayout(new AbsoluteLayout());
        this.panelTabs.setFont(new Font("Dialog", 0, 12));
        this.panelGeneralConfiguration.setLayout(new AbsoluteLayout());
        this.labelLink.setIcon(this.imageBroker.getImageIcon(AvailableImages.LINK));
        this.labelLink.setText(this.translations.getString("VentanaEnlace.descripcion"));
        this.panelGeneralConfiguration.add(this.labelLink, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelName.setFont(new Font("Dialog", 0, 12));
        this.labelName.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelName.setText(this.translations.getString("VentanaEnlace.etiquetaNombre"));
        this.panelGeneralConfiguration.add(this.labelName, new AbsoluteConstraints(10, 80, 110, -1));
        this.textFieldName.setToolTipText(this.translations.getString("JVentanaEnlace.tooltip.NombreEnlace"));
        this.panelGeneralConfiguration.add(this.textFieldName, new AbsoluteConstraints(135, 80, 110, -1));
        this.checkBoxShowName.setFont(new Font("Dialog", 0, 12));
        this.checkBoxShowName.setText(this.translations.getString("VentanaEnlace.verNombre"));
        this.checkBoxShowName.setToolTipText(this.translations.getString("JVentanaEnlace.tooltip.VerNombre"));
        this.panelGeneralConfiguration.add(this.checkBoxShowName, new AbsoluteConstraints(255, 80, -1, -1));
        this.iconContainerLink.setIcon(this.imageBroker.getImageIcon(AvailableImages.LINK));
        this.panelGeneralConfiguration.add(this.iconContainerLink, new AbsoluteConstraints(158, 120, 50, -1));
        this.labelHeadEnd.setFont(new Font("Dialog", 0, 12));
        this.labelHeadEnd.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelHeadEnd.setText(this.translations.getString("VentanaEnlace.etiquetaExtremoIzquierdo"));
        this.panelGeneralConfiguration.add(this.labelHeadEnd, new AbsoluteConstraints(15, 135, 130, -1));
        this.labelTailEnd.setFont(new Font("Dialog", 0, 12));
        this.labelTailEnd.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelTailEnd.setText(this.translations.getString("VentanaEnlace.etiquetaExtremoDerecho"));
        this.panelGeneralConfiguration.add(this.labelTailEnd, new AbsoluteConstraints(220, 135, 130, -1));
        this.comboBoxTailEndNode.setFont(new Font("Dialog", 0, 12));
        this.comboBoxTailEndNode.setModel(new DefaultComboBoxModel<>(new String[]{""}));
        this.comboBoxTailEndNode.setToolTipText(this.translations.getString("JVentanaEnlace.tooltip.ExtremoIzquierdo"));
        this.comboBoxTailEndNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleChangeInTailEndNode(evt);
            }
        });
        this.panelGeneralConfiguration.add(this.comboBoxTailEndNode, new AbsoluteConstraints(220, 160, 90, -1));
        this.comboBoxHeadEndNode.setFont(new Font("Dialog", 0, 12));
        this.comboBoxHeadEndNode.setModel(new DefaultComboBoxModel<>(new String[]{""}));
        this.comboBoxHeadEndNode.setToolTipText(this.translations.getString("JVentanaEnlace.tooltip.extremoDerecho"));
        this.comboBoxHeadEndNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleChangeInHeadEndNode(evt);
            }
        });
        this.panelGeneralConfiguration.add(this.comboBoxHeadEndNode, new AbsoluteConstraints(15, 160, 90, -1));
        this.comboBoxTailEndNodePort.setFont(new Font("Dialog", 0, 12));
        this.comboBoxTailEndNodePort.setToolTipText(this.translations.getString("JVentanaEnlace.tooltip.puertoEntrada"));
        this.panelGeneralConfiguration.add(comboBoxTailEndNodePort, new AbsoluteConstraints(320, 160, -1, -1));
        this.comboBoxHeadEndNodePort.setFont(new Font("Dialog", 0, 12));
        this.comboBoxHeadEndNodePort.setToolTipText(this.translations.getString("JVentanaEnlace.tooltip.puertosalida"));
        this.panelGeneralConfiguration.add(this.comboBoxHeadEndNodePort, new AbsoluteConstraints(115, 160, -1, -1));
        this.labelHeadEndColons.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelHeadEndColons.setText(this.translations.getString("JVentanaEnlace.:"));
        this.panelGeneralConfiguration.add(this.labelHeadEndColons, new AbsoluteConstraints(105, 165, 10, -1));
        this.labelTailEndColons.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelTailEndColons.setText(this.translations.getString("JVentanaEnlace.:"));
        this.panelGeneralConfiguration.add(this.labelTailEndColons, new AbsoluteConstraints(310, 165, 10, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaEnlace.tabs.General"), panelGeneralConfiguration);
        this.panelQuickConfiguration.setLayout(new AbsoluteLayout());
        this.iconContainerWizard.setIcon(this.imageBroker.getImageIcon(AvailableImages.WIZARD));
        this.iconContainerWizard.setText(this.translations.getString("JVentanaEnlace.Rapida.Descripcion"));
        this.panelQuickConfiguration.add(this.iconContainerWizard, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelSpeed.setFont(new Font("Dialog", 0, 12));
        this.labelSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelSpeed.setText(this.translations.getString("JVentanaEnlace.Link_speed"));
        this.panelQuickConfiguration.add(this.labelSpeed, new AbsoluteConstraints(20, 105, 100, -1));
        this.comboBoxPredefinedOptions.setFont(new Font("Dialog", 0, 12));
        this.comboBoxPredefinedOptions.setMaximumRowCount(6);
        this.comboBoxPredefinedOptions.setModel(new DefaultComboBoxModel<>(new String[]{"Personalizado", "Very high", "High", "Normal", "Low", "Very low"}));
        this.comboBoxPredefinedOptions.setSelectedIndex(3);
        this.comboBoxPredefinedOptions.setToolTipText(this.translations.getString("JVentanaEnlace.Select_the_link_speed"));
        this.comboBoxPredefinedOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPredefinedOptions(evt);
            }
        });
        this.panelQuickConfiguration.add(this.comboBoxPredefinedOptions, new AbsoluteConstraints(140, 100, 180, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaEnlace.tabs.Fast"), this.panelQuickConfiguration);
        this.panelAdvancedConfiguration.setLayout(new AbsoluteLayout());
        this.iconContainerToolbox.setIcon(this.imageBroker.getImageIcon(AvailableImages.ADVANCED));
        this.iconContainerToolbox.setText(this.translations.getString("JVentanaEnlace.Advanced_and_complete_link_configuration."));
        this.panelAdvancedConfiguration.add(this.iconContainerToolbox, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelDelay.setFont(new Font("Dialog", 0, 12));
        this.labelDelay.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelDelay.setText(this.translations.getString("JVentanaEnlace.Link_delay"));
        this.panelAdvancedConfiguration.add(this.labelDelay, new AbsoluteConstraints(20, 105, 100, -1));
        this.sliderDelay.setMajorTickSpacing(1000);
        this.sliderDelay.setMaximum(500000);
        this.sliderDelay.setMinimum(1);
        this.sliderDelay.setMinorTickSpacing(1000);
        this.sliderDelay.setToolTipText(this.translations.getString("JVentanaEnlace.Slide_it_to_set_the_link_delay."));
        this.sliderDelay.setValue(125000);
        this.sliderDelay.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnDelay(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.sliderDelay, new AbsoluteConstraints(125, 105, 150, -1));
        this.labelNs.setFont(new Font("Dialog", 0, 10));
        this.labelNs.setForeground(new Color(102, 102, 102));
        this.labelNs.setText(this.translations.getString("JVentanaEnlace.500_ns."));
        this.panelAdvancedConfiguration.add(this.labelNs, new AbsoluteConstraints(280, 105, 70, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaEnlace.tabs.Advanced"), this.panelAdvancedConfiguration);
        this.mainPanel.add(this.panelTabs, new AbsoluteConstraints(15, 15, 370, 240));
        this.panelButtons.setLayout(new AbsoluteLayout());
        this.buttonOK.setFont(new Font("Dialog", 0, 12));
        this.buttonOK.setIcon(this.imageBroker.getImageIcon(AvailableImages.ACCEPT));
        this.buttonOK.setMnemonic(this.translations.getString("VentanaEnlace.botones.mne.Aceptar").charAt(0));
        this.buttonOK.setText(this.translations.getString("VentanaEnlace.boton.Ok"));
        this.buttonOK.setToolTipText(this.translations.getString("JVentanaEnlace.Adds_the_link_to_the_topology."));
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOKButton(evt);
            }
        });
        this.panelButtons.add(this.buttonOK, new AbsoluteConstraints(15, 15, 115, -1));
        this.buttonCancel.setFont(new Font("Dialog", 0, 12));
        this.buttonCancel.setIcon(this.imageBroker.getImageIcon(AvailableImages.CANCEL));
        this.buttonCancel.setMnemonic(this.translations.getString("VentanaEnlace.botones.mne.Cancelar").charAt(0));
        this.buttonCancel.setText(this.translations.getString("VentanaEnlace.boton.Cancel"));
        this.buttonCancel.setToolTipText(this.translations.getString("JVentanaEnlace.Cancel_the_operation"));
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnCancelButton(evt);
            }
        });
        this.panelButtons.add(this.buttonCancel, new AbsoluteConstraints(145, 15, 115, -1));
        this.mainPanel.add(this.panelButtons, new AbsoluteConstraints(0, 255, 400, 55));
        getContentPane().add(this.mainPanel, new AbsoluteConstraints(0, 0, -1, 310));
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
        Dimension frameSize = this.getSize();
        Dimension parentSize = this.parent.getSize();
        setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);
        this.currentConfigName = null;
        this.currentConfigShowName = false;
        this.currentConfigDelay = 1000;
        this.comboBoxPredefinedOptions.removeAllItems();
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaHija.Personalized"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaHija.Too_fast"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaHija.Fast"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaHija.Normal"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaHija.Low"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaHija.Too_low"));
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        loadAllNodesThatHaveAvailablePorts();
    }

    /**
     * This method is called when a new tail end node is selected in the UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleChangeInTailEndNode(ActionEvent evt) {
        this.comboBoxTailEndNodePort.removeAllItems();
        this.comboBoxTailEndNodePort.addItem("");
        this.comboBoxTailEndNodePort.setSelectedIndex(0);
        if (this.comboBoxTailEndNode.getSelectedIndex() != 0) {
            TNode selectedTailEndNode = this.topology.getFirstNodeNamed((String) this.comboBoxTailEndNode.getSelectedItem());
            // FIX: two next variables seem not to be used. Clean.
            Iterator it = this.topology.getNodesIterator();
            TNode nt;
            if (selectedTailEndNode != null) {
                // Update available ports for that node
                int i = 0;
                for (i = 0; i < selectedTailEndNode.getPorts().getNumberOfPorts(); i++) {
                    if (selectedTailEndNode.getPorts().getPort(i).isAvailable()) {
                        this.comboBoxTailEndNodePort.addItem("" + i);
                    }
                }
            }
        }
    }

    /**
     * This method is called when a new head end node is selected in the UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleChangeInHeadEndNode(ActionEvent evt) {
        this.comboBoxTailEndNode.removeAllItems();
        this.comboBoxTailEndNode.addItem("");
        this.comboBoxTailEndNode.setSelectedIndex(0);
        this.comboBoxHeadEndNodePort.removeAllItems();
        this.comboBoxHeadEndNodePort.addItem("");
        this.comboBoxHeadEndNodePort.setSelectedIndex(0);
        this.comboBoxTailEndNodePort.removeAllItems();
        this.comboBoxTailEndNodePort.addItem("");
        this.comboBoxTailEndNodePort.setSelectedIndex(0);
        if (this.comboBoxHeadEndNode.getSelectedIndex() != 0) {
            TNode selectedHeadEndNode = this.topology.getFirstNodeNamed((String) this.comboBoxHeadEndNode.getSelectedItem());
            Iterator it = this.topology.getNodesIterator();
            TNode nt;
            if (selectedHeadEndNode != null) {
                // Update available ports for that node
                int i = 0;
                for (i = 0; i < selectedHeadEndNode.getPorts().getNumberOfPorts(); i++) {
                    if (selectedHeadEndNode.getPorts().getPort(i).isAvailable()) {
                        this.comboBoxHeadEndNodePort.addItem("" + i);
                    }
                }
                // Once selected the head end node, this computes those nodes 
                // that are candidates for tail end nodes. Not all nodes can be
                // linked.
                while (it.hasNext()) {
                    nt = (TNode) it.next();
                    if (!nt.getName().equals(selectedHeadEndNode.getName())) {
                        if (nt.hasAvailablePorts()) {
                            if (!this.topology.isThereAnyLinkThatJoins(nt.getNodeID(), selectedHeadEndNode.getNodeID())) {
                                switch (selectedHeadEndNode.getNodeType()) {
                                    case TNode.TRAFFIC_GENERATOR: {
                                        if ((nt.getNodeType() == TNode.LER) || (nt.getNodeType() == TNode.ACTIVE_LER)) {
                                            this.comboBoxTailEndNode.addItem(nt.getName());
                                        }
                                        break;
                                    }
                                    case TNode.TRAFFIC_SINK: {
                                        if ((nt.getNodeType() == TNode.LER) || (nt.getNodeType() == TNode.ACTIVE_LER)) {
                                            this.comboBoxTailEndNode.addItem(nt.getName());
                                        }
                                        break;
                                    }
                                    case TNode.LER: {
                                        this.comboBoxTailEndNode.addItem(nt.getName());
                                        break;
                                    }
                                    case TNode.ACTIVE_LER: {
                                        this.comboBoxTailEndNode.addItem(nt.getName());
                                        break;
                                    }
                                    case TNode.LSR: {
                                        if ((nt.getNodeType() == TNode.LER) || (nt.getNodeType() == TNode.ACTIVE_LER)
                                                || (nt.getNodeType() == TNode.LSR) || (nt.getNodeType() == TNode.ACTIVE_LSR)) {
                                            this.comboBoxTailEndNode.addItem(nt.getName());
                                        }
                                        break;
                                    }
                                    case TNode.ACTIVE_LSR: {
                                        if ((nt.getNodeType() == TNode.LER) || (nt.getNodeType() == TNode.ACTIVE_LER)
                                                || (nt.getNodeType() == TNode.LSR) || (nt.getNodeType() == TNode.ACTIVE_LSR)) {
                                            this.comboBoxTailEndNode.addItem(nt.getName());
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method loads the first list of nodes that can be head end nodes. It
     * will contain all nodes that has at leas an available port.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void loadAllNodesThatHaveAvailablePorts() {
        Iterator nodesIterator = this.topology.getNodesIterator();
        TNode node;
        while (nodesIterator.hasNext()) {
            node = (TNode) nodesIterator.next();
            if (node.hasAvailablePorts()) {
                this.comboBoxHeadEndNode.addItem(node.getName());
                this.comboBoxHeadEndNode.setSelectedIndex(0);
            }
        }
    }

    /**
     * This method is called when a a predefined option is selected in the UI to
     * configure the link.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnPredefinedOptions(ActionEvent evt) {
        switch (this.comboBoxPredefinedOptions.getSelectedIndex()) {
            case 0: {
                // Do nothing
                this.comboBoxPredefinedOptions.setSelectedIndex(0);
                break;
            }
            case 1: {
                this.sliderDelay.setValue(1000);
                this.comboBoxPredefinedOptions.setSelectedIndex(1);
                break;
            }
            case 2: {
                this.sliderDelay.setValue(62500);
                this.comboBoxPredefinedOptions.setSelectedIndex(2);
                break;
            }
            case 3: {
                this.sliderDelay.setValue(125000);
                this.comboBoxPredefinedOptions.setSelectedIndex(3);
                break;
            }
            case 4: {
                this.sliderDelay.setValue(187500);
                this.comboBoxPredefinedOptions.setSelectedIndex(4);
                break;
            }
            case 5: {
                this.sliderDelay.setValue(250000);
                this.comboBoxPredefinedOptions.setSelectedIndex(5);
                break;
            }
        }
        this.labelNs.setText(this.sliderDelay.getValue() + this.translations.getString("JVentanaEnlace._ns."));
    }

    /**
     * This method is called when a change is made in the Delay (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleChangeOnDelay(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.labelNs.setText(this.sliderDelay.getValue() + this.translations.getString("JVentanaEnlace._ns."));
    }

    /**
     * This method is called when a click is done "Cancel" button (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnCancelButton(ActionEvent evt) {
        if (this.reconfiguration) {
            this.linkConfig.setName(this.currentConfigName);
            this.linkConfig.setShowName(this.currentConfigShowName);
            this.linkConfig.setLinkDelay(this.currentConfigDelay);
            this.reconfiguration = false;
            this.linkConfig.setWellConfigured(true);
        } else {
            this.linkConfig.setWellConfigured(false);
        }
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method is called when a click is done "Ok" button (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnOKButton(ActionEvent evt) {
        this.linkConfig.setWellConfigured(true);
        this.linkConfig.setName(this.textFieldName.getText());
        this.linkConfig.setShowName(this.checkBoxShowName.isSelected());
        this.linkConfig.setLinkDelay(this.sliderDelay.getValue());
        if (!this.reconfiguration) {
            this.linkConfig.setHeadEndNodeName((String) this.comboBoxHeadEndNode.getSelectedItem());
            this.linkConfig.setTailEndNodeName((String) this.comboBoxTailEndNode.getSelectedItem());
            this.linkConfig.discoverLinkType(this.topology);
            if (((String) this.comboBoxHeadEndNodePort.getSelectedItem()).equals("")) {
                this.linkConfig.setHeadEndNodePortID(-1);
            } else {
                String aux = (String) this.comboBoxHeadEndNodePort.getSelectedItem();
                int aux2 = Integer.parseInt(aux);
                this.linkConfig.setHeadEndNodePortID(aux2);
            }
            if (((String) this.comboBoxTailEndNodePort.getSelectedItem()).equals("")) {
                this.linkConfig.setTailEndNodePortID(-1);
            } else {
                String aux = (String) this.comboBoxTailEndNodePort.getSelectedItem();
                int aux2 = Integer.parseInt(aux);
                this.linkConfig.setTailEndNodePortID(aux2);
            }
        }
        int error = this.linkConfig.validateConfig(this.topology, this.reconfiguration);
        if (error != TLinkConfig.OK) {
            JWarningWindow warningWindow = new JWarningWindow(this.parent, true, this.imageBroker);
            warningWindow.setWarningMessage(this.linkConfig.getErrorMessage(error));
            warningWindow.setVisible(true);
        } else {
            this.setVisible(false);
            this.dispose();
        }
    }

    /**
     * This method is called when the JLinkWindow is closed (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleWindowsClosing(WindowEvent evt) {
        setVisible(false);
        this.linkConfig.setWellConfigured(false);
        dispose();
    }

    /**
     * This method configures all components of JLinkWindow with present values
     * retrieved from the link configuration object specified. It is used to do
     * a reconfiguration.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param linkConfig the configuration object for a link, that will be
     * configured through this JLinkWindow
     * @param reconfiguration TRUE if the link is being reconfigured. FALSE if
     * it is the first configuration of the link after its creation.
     * @since 2.0
     */
    public void setConfiguration(TLinkConfig linkConfig, boolean reconfiguration) {
        this.linkConfig = linkConfig;
        if (reconfiguration) {
            this.reconfiguration = reconfiguration;
            this.currentConfigName = linkConfig.getName();
            this.currentConfigShowName = linkConfig.nameMustBeDisplayed();
            this.currentConfigDelay = linkConfig.getLinkDelay();
            this.textFieldName.setText(linkConfig.getName());
            this.checkBoxShowName.setSelected(linkConfig.nameMustBeDisplayed());
            this.comboBoxPredefinedOptions.setSelectedIndex(0);
            this.sliderDelay.setValue(linkConfig.getLinkDelay());
            this.comboBoxHeadEndNode.setEnabled(false);
            this.comboBoxHeadEndNodePort.setEnabled(false);
            this.comboBoxTailEndNode.setEnabled(false);
            this.comboBoxTailEndNodePort.setEnabled(false);
            this.comboBoxHeadEndNode.removeAllItems();
            this.comboBoxHeadEndNode.addItem(linkConfig.getHeadEndNodeName());
            this.comboBoxHeadEndNode.setSelectedIndex(0);
            this.comboBoxHeadEndNodePort.removeAllItems();
            this.comboBoxHeadEndNodePort.addItem("" + linkConfig.getHeadEndNodePortID());
            this.comboBoxHeadEndNodePort.setSelectedIndex(0);
            this.comboBoxTailEndNode.removeAllItems();
            this.comboBoxTailEndNode.addItem(linkConfig.getTailEndNodeName());
            this.comboBoxTailEndNode.setSelectedIndex(0);
            this.comboBoxTailEndNodePort.removeAllItems();
            this.comboBoxTailEndNodePort.addItem("" + linkConfig.getTailEndNodePortID());
            this.comboBoxTailEndNodePort.setSelectedIndex(0);
        }
    }

    private boolean reconfiguration;
    private String currentConfigName;
    private boolean currentConfigShowName;
    private int currentConfigDelay;
    private TImageBroker imageBroker;
    private Frame parent;
    private TTopology topology;
    private TLinkConfig linkConfig;
    private JSlider sliderDelay;
    private JComboBox<String> comboBoxPredefinedOptions;
    private JLabel labelNs;
    private JLabel labelName;
    private JLabel labelLink;
    private JLabel iconContainerWizard;
    private JLabel iconContainerToolbox;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel iconContainerLink;
    private JLabel labelHeadEnd;
    private JLabel labelTailEnd;
    private JLabel labelHeadEndColons;
    private JLabel labelTailEndColons;
    private JLabel labelSpeed;
    private JLabel labelDelay;
    private JTextField textFieldName;
    private JPanel panelAdvancedConfiguration;
    private JPanel panelButtons;
    private JPanel panelGeneralConfiguration;
    private JTabbedPane panelTabs;
    private JPanel mainPanel;
    private JPanel panelQuickConfiguration;
    private JComboBox<String> comboBoxTailEndNode;
    private JComboBox<String> comboBoxHeadEndNode;
    private JComboBox<String> comboBoxTailEndNodePort;
    private JComboBox<String> comboBoxHeadEndNodePort;
    private JCheckBox checkBoxShowName;
    private ResourceBundle translations;
}
