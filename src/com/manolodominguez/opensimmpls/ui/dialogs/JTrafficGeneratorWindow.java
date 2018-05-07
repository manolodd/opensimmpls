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
package com.manolodominguez.opensimmpls.ui.dialogs;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.scenario.TTrafficGeneratorNode;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.scenario.TNode;
import com.manolodominguez.opensimmpls.ui.simulator.JDesignPanel;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
 * traffic generator node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JTrafficGeneratorWindow extends JDialog {

    /**
     * This is the constructor of the class and creates a new instance of
     * JTrafficGeneratorWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param designPanel desing panel wich contains the traffic generator node
     * that is configured via this JTrafficGeneratorWindow
     * @param parent Parent window over wich this JTrafficGeneratorWindow is
     * shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param modal TRUE, if this dialog has to be modal. Otherwise, FALSE.
     * @param topology Topology the traffic generator node belongs to.
     * @since 2.0
     */
    public JTrafficGeneratorWindow(TTopology topology, JDesignPanel designPanel, TImageBroker imageBroker, Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.imageBroker = imageBroker;
        this.designPanel = designPanel;
        this.topology = topology;
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
        this.translations = ResourceBundle.getBundle(AvailableBundles.TRAFFIC_GENERATOR_WINDOW.getPath());
        this.buttonGroup = new ButtonGroup();
        this.mainPanel = new JPanel();
        this.panelTabs = new JTabbedPane();
        this.panelGeneralConfiguration = new JPanel();
        this.iconContainerTrafficGenerator = new JLabel();
        this.labelName = new JLabel();
        this.textFieldName = new JTextField();
        this.panelPosition = new JPanel();
        this.labelCoordinateX = new JLabel();
        this.labelCoordinateY = new JLabel();
        this.coordinatesPanel = new com.manolodominguez.opensimmpls.ui.dialogs.JCoordinatesPanel();
        this.checkBoxShowName = new JCheckBox();
        this.labelTargetTrafficSinkNode = new JLabel();
        this.comboBoxTargetTrafficSinkNode = new JComboBox();
        this.panelQuickConfiguration = new JPanel();
        this.labelQuickConfiguration = new JLabel();
        this.labelQuickTrafficType = new JLabel();
        this.checkBoxQuickGenerateStatistics = new JCheckBox();
        this.comboBoxPredefinedOptions = new JComboBox();
        this.panelAdvancedConfiguration = new JPanel();
        this.labelAdvancedConfiguration = new JLabel();
        this.labelTrafficGenerationRate = new JLabel();
        this.labelTrafficGenerationRateMbps = new JLabel();
        this.checkBoxAdvancedGenerateStatistics = new JCheckBox();
        this.labelGoSLevel = new JLabel();
        this.comboBoxGoSLevel = new JComboBox();
        this.checkBoxBackupLSP = new JCheckBox();
        this.labelAdvancedTrafficType = new JLabel();
        this.radioButtonConstantPacketSize = new JRadioButton();
        this.constantTrafficVariablePacketSize = new JRadioButton();
        this.checkBoxEncapsulateOverMPLS = new JCheckBox();
        this.sliderTrafficGenerationRate = new JSlider();
        this.sliderConstantTrafficPayloadSize = new JSlider();
        this.labelConstanTrafficPayloadSize = new JLabel();
        this.labelPayloadSize = new JLabel();
        this.panelButtons = new JPanel();
        this.buttonOK = new JButton();
        this.buttonCancel = new JButton();
        setTitle(this.translations.getString("VentanaEmisor.TituloVentana"));
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
        this.iconContainerTrafficGenerator.setIcon(this.imageBroker.getIcon(TImageBroker.EMISOR));
        this.iconContainerTrafficGenerator.setText(this.translations.getString("VentanaEmisor.DescripcionNodo"));
        this.panelGeneralConfiguration.add(this.iconContainerTrafficGenerator, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelName.setFont(new Font("Dialog", 0, 12));
        this.labelName.setText(this.translations.getString("VentanaEmisor.Etiqueta.NombreNodo"));
        this.panelGeneralConfiguration.add(this.labelName, new AbsoluteConstraints(215, 105, 120, -1));
        this.textFieldName.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.Nombre"));
        this.panelGeneralConfiguration.add(this.textFieldName, new AbsoluteConstraints(215, 130, 125, -1));
        this.panelPosition.setBorder(BorderFactory.createTitledBorder(this.translations.getString("VentanaEmisor.Etiqueta.Posicion")));
        this.panelPosition.setLayout(new AbsoluteLayout());
        this.labelCoordinateX.setFont(new Font("Dialog", 0, 12));
        this.labelCoordinateX.setText(this.translations.getString("VentanaEmisor.X=_45"));
        this.panelPosition.add(this.labelCoordinateX, new AbsoluteConstraints(100, 100, -1, -1));
        this.labelCoordinateY.setFont(new Font("Dialog", 0, 12));
        this.labelCoordinateY.setText(this.translations.getString("VentanaEmisor.Y=_1024"));
        this.panelPosition.add(this.labelCoordinateY, new AbsoluteConstraints(40, 100, -1, -1));
        this.coordinatesPanel.setBackground(new Color(255, 255, 255));
        this.coordinatesPanel.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.posicion"));
        this.coordinatesPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleClickOnCoordinatesPanel(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
                handleMouseEnteringInCoordinatesPanel(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                handleMouseLeavingCoordinatesPanel(evt);
            }
        });
        this.panelPosition.add(this.coordinatesPanel, new AbsoluteConstraints(25, 25, 130, 70));
        this.panelGeneralConfiguration.add(this.panelPosition, new AbsoluteConstraints(15, 100, 180, 125));
        this.checkBoxShowName.setFont(new Font("Dialog", 0, 12));
        this.checkBoxShowName.setSelected(true);
        this.checkBoxShowName.setText(this.translations.getString("VentanaEmisor.Etiqueta.VerNombre"));
        this.checkBoxShowName.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.VerNombre"));
        this.panelGeneralConfiguration.add(this.checkBoxShowName, new AbsoluteConstraints(215, 175, -1, -1));
        this.labelTargetTrafficSinkNode.setFont(new Font("Dialog", 0, 12));
        this.labelTargetTrafficSinkNode.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelTargetTrafficSinkNode.setText(this.translations.getString("VentanaEmisor.DestinoTrafico"));
        this.panelGeneralConfiguration.add(this.labelTargetTrafficSinkNode, new AbsoluteConstraints(20, 245, 170, -1));
        this.comboBoxTargetTrafficSinkNode.setFont(new Font("Dialog", 0, 12));
        this.comboBoxTargetTrafficSinkNode.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.destinodeltrafico"));
        this.panelGeneralConfiguration.add(this.comboBoxTargetTrafficSinkNode, new AbsoluteConstraints(200, 240, -1, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaEmisor.Tab.General"), this.panelGeneralConfiguration);
        this.panelQuickConfiguration.setLayout(new AbsoluteLayout());
        this.labelQuickConfiguration.setIcon(this.imageBroker.getIcon(TImageBroker.ASISTENTE));
        this.labelQuickConfiguration.setText(this.translations.getString("VentanaEmisor.configuracionRapida"));
        this.panelQuickConfiguration.add(this.labelQuickConfiguration, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelQuickTrafficType.setFont(new Font("Dialog", 0, 12));
        this.labelQuickTrafficType.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelQuickTrafficType.setText(this.translations.getString("VentanaEmisor.TipoDeTrafico1"));
        this.panelQuickConfiguration.add(this.labelQuickTrafficType, new AbsoluteConstraints(20, 125, 115, -1));
        this.checkBoxQuickGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        this.checkBoxQuickGenerateStatistics.setText(this.translations.getString("VentanaEmisor.GenerarEstadisticas1"));
        this.checkBoxQuickGenerateStatistics.setToolTipText(this.translations.getString("VentanaEmisor.GenerarEstadisticas1"));
        this.checkBoxQuickGenerateStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnQuickGenerateStatistics(evt);
            }
        });
        this.panelQuickConfiguration.add(this.checkBoxQuickGenerateStatistics, new AbsoluteConstraints(70, 195, -1, -1));
        this.comboBoxPredefinedOptions.setFont(new Font("Dialog", 0, 12));
        this.comboBoxPredefinedOptions.setModel(new DefaultComboBoxModel(new String[]{"Personalized", "Email", "Web", "P2P file sharing", "Bank data transaction", "Tele-medical video", "Bulk traffic"}));
        this.comboBoxPredefinedOptions.setToolTipText(this.translations.getString("VentanaEmisor.TipoDeTrafico1"));
        this.comboBoxPredefinedOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPredefinedOptions(evt);
            }
        });
        this.panelQuickConfiguration.add(this.comboBoxPredefinedOptions, new AbsoluteConstraints(145, 120, -1, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaEmisor.Tab.Rapida"), this.panelQuickConfiguration);
        this.panelAdvancedConfiguration.setLayout(new AbsoluteLayout());
        this.labelAdvancedConfiguration.setIcon(this.imageBroker.getIcon(TImageBroker.AVANZADA));
        this.labelAdvancedConfiguration.setText(this.translations.getString("VentanaEmisor.ConfiguracionAvanzada"));
        this.panelAdvancedConfiguration.add(this.labelAdvancedConfiguration, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelTrafficGenerationRate.setFont(new Font("Dialog", 0, 12));
        this.labelTrafficGenerationRate.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelTrafficGenerationRate.setText(this.translations.getString("VentanaEmisor.TasaDeTrafico"));
        this.panelAdvancedConfiguration.add(this.labelTrafficGenerationRate, new AbsoluteConstraints(15, 90, 100, -1));
        this.labelTrafficGenerationRateMbps.setFont(new Font("Dialog", 0, 10));
        this.labelTrafficGenerationRateMbps.setForeground(new Color(102, 102, 102));
        this.labelTrafficGenerationRateMbps.setHorizontalAlignment(SwingConstants.LEFT);
        this.labelTrafficGenerationRateMbps.setText(this.translations.getString("VentanaEmisor.Kbpsinicial"));
        this.panelAdvancedConfiguration.add(this.labelTrafficGenerationRateMbps, new AbsoluteConstraints(290, 90, 70, -1));
        this.checkBoxAdvancedGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        this.checkBoxAdvancedGenerateStatistics.setText(this.translations.getString("VentanaEmisor.GenerarEstadisticas2"));
        this.checkBoxAdvancedGenerateStatistics.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.GenerarEstadisticas2"));
        this.checkBoxAdvancedGenerateStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnAdvancedGenerateStatistics(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.checkBoxAdvancedGenerateStatistics, new AbsoluteConstraints(70, 270, -1, -1));
        this.labelGoSLevel.setFont(new Font("Dialog", 0, 12));
        this.labelGoSLevel.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelGoSLevel.setText(this.translations.getString("VentanaEmisor.NivelDeGoS"));
        this.panelAdvancedConfiguration.add(this.labelGoSLevel, new AbsoluteConstraints(10, 240, 85, -1));
        this.comboBoxGoSLevel.setFont(new Font("Dialog", 0, 12));
        this.comboBoxGoSLevel.setModel(new DefaultComboBoxModel(new String[]{"None", "Level 1", "Level 2", "Level 3"}));
        this.comboBoxGoSLevel.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.nivelDeGoS"));
        this.comboBoxGoSLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnGoSLevel(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.comboBoxGoSLevel, new AbsoluteConstraints(100, 230, -1, -1));
        this.checkBoxBackupLSP.setFont(new Font("Dialog", 0, 12));
        this.checkBoxBackupLSP.setText(this.translations.getString("VentanaEmisor.CrearLSPBackup"));
        this.checkBoxBackupLSP.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.crearUnLSPdeBackup"));
        this.checkBoxBackupLSP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnBackupLSP(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.checkBoxBackupLSP, new AbsoluteConstraints(200, 230, -1, -1));
        this.labelAdvancedTrafficType.setFont(new Font("Dialog", 0, 12));
        this.labelAdvancedTrafficType.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelAdvancedTrafficType.setText(this.translations.getString("VentanaEmisor.TipoDeTrafico3"));
        this.panelAdvancedConfiguration.add(this.labelAdvancedTrafficType, new AbsoluteConstraints(15, 125, 100, -1));
        this.buttonGroup.add(this.radioButtonConstantPacketSize);
        this.radioButtonConstantPacketSize.setFont(new Font("Dialog", 0, 12));
        this.radioButtonConstantPacketSize.setText(this.translations.getString("VentanaEmisor.TraficoConstante"));
        this.radioButtonConstantPacketSize.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.traficoConstante"));
        this.radioButtonConstantPacketSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnConstantTraffic(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.radioButtonConstantPacketSize, new AbsoluteConstraints(125, 125, -1, 20));
        this.buttonGroup.add(this.constantTrafficVariablePacketSize);
        this.constantTrafficVariablePacketSize.setFont(new Font("Dialog", 0, 12));
        this.constantTrafficVariablePacketSize.setSelected(true);
        this.constantTrafficVariablePacketSize.setText(this.translations.getString("VentanaEmisor.TraficoVariable"));
        this.constantTrafficVariablePacketSize.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.traficovariable"));
        this.constantTrafficVariablePacketSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnVariableTraffic(evt);
            }
        });
        this.panelAdvancedConfiguration.add(constantTrafficVariablePacketSize, new AbsoluteConstraints(225, 125, -1, 20));
        this.checkBoxEncapsulateOverMPLS.setFont(new Font("Dialog", 0, 12));
        this.checkBoxEncapsulateOverMPLS.setText(this.translations.getString("VentanaEmisor.EncapsularSobreMPLS"));
        this.checkBoxEncapsulateOverMPLS.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.encapsularsobrempls"));
        this.checkBoxEncapsulateOverMPLS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnEncapsulateOverMPLS(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.checkBoxEncapsulateOverMPLS, new AbsoluteConstraints(50, 190, -1, -1));
        this.sliderTrafficGenerationRate.setMajorTickSpacing(1000);
        this.sliderTrafficGenerationRate.setMaximum(10240);
        this.sliderTrafficGenerationRate.setMinimum(1);
        this.sliderTrafficGenerationRate.setMinorTickSpacing(100);
        this.sliderTrafficGenerationRate.setToolTipText(this.translations.getString("VentanaEmisor.tooltipo.CambiarTasa"));
        this.sliderTrafficGenerationRate.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnTrafficGenerationRate(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.sliderTrafficGenerationRate, new AbsoluteConstraints(120, 90, 165, -1));
        this.sliderConstantTrafficPayloadSize.setMajorTickSpacing(1000);
        this.sliderConstantTrafficPayloadSize.setMaximum(65495);
        this.sliderConstantTrafficPayloadSize.setMinorTickSpacing(100);
        this.sliderConstantTrafficPayloadSize.setToolTipText(this.translations.getString("VentanaEmisor.tooltipo.CambiarTasa"));
        this.sliderConstantTrafficPayloadSize.setValue(1024);
        this.sliderConstantTrafficPayloadSize.setEnabled(false);
        this.sliderConstantTrafficPayloadSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnConstantTrafficPayloadSize(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.sliderConstantTrafficPayloadSize, new AbsoluteConstraints(160, 160, 120, -1));
        this.labelConstanTrafficPayloadSize.setFont(new Font("Dialog", 0, 10));
        this.labelConstanTrafficPayloadSize.setForeground(new Color(102, 102, 102));
        this.labelConstanTrafficPayloadSize.setHorizontalAlignment(SwingConstants.LEFT);
        this.labelConstanTrafficPayloadSize.setText("null");
        this.labelConstanTrafficPayloadSize.setEnabled(false);
        this.panelAdvancedConfiguration.add(this.labelConstanTrafficPayloadSize, new AbsoluteConstraints(290, 160, 70, -1));
        this.labelPayloadSize.setFont(new Font("Dialog", 0, 12));
        this.labelPayloadSize.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelPayloadSize.setText(this.translations.getString("JVentanaEmisor.TamCargaUtil"));
        this.labelPayloadSize.setEnabled(false);
        this.panelAdvancedConfiguration.add(this.labelPayloadSize, new AbsoluteConstraints(10, 160, 140, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaEmisor.Tab.Avanzada"), panelAdvancedConfiguration);
        this.mainPanel.add(this.panelTabs, new AbsoluteConstraints(15, 15, 370, 330));
        getContentPane().add(this.mainPanel, new AbsoluteConstraints(0, 0, 400, 350));
        this.panelButtons.setLayout(new AbsoluteLayout());
        this.buttonOK.setFont(new Font("Dialog", 0, 12));
        this.buttonOK.setIcon(this.imageBroker.getIcon(TImageBroker.ACEPTAR));
        this.buttonOK.setMnemonic(this.translations.getString("VentanaEmisor.botones.Aceptar").charAt(0));
        this.buttonOK.setText(this.translations.getString("VentanaEmisor.Boton.Aceptar.Texto"));
        this.buttonOK.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.Aceptar"));
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOKButton(evt);
            }
        });
        this.panelButtons.add(this.buttonOK, new AbsoluteConstraints(15, 15, 110, -1));
        this.buttonCancel.setFont(new Font("Dialog", 0, 12));
        this.buttonCancel.setIcon(this.imageBroker.getIcon(TImageBroker.CANCELAR));
        this.buttonCancel.setMnemonic(this.translations.getString("VentanaEmisor.botones.Cancelar").charAt(0));
        this.buttonCancel.setText(this.translations.getString("VentanaEmisor.Boton.Cancelar.Texto"));
        this.buttonCancel.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.Cancelar"));
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnCancelButton(evt);
            }
        });
        this.panelButtons.add(this.buttonCancel, new AbsoluteConstraints(140, 15, 110, -1));
        getContentPane().add(this.panelButtons, new AbsoluteConstraints(0, 340, 400, 50));
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
        this.coordinatesPanel.setDesignPanel(this.designPanel);
        Dimension frameSize = this.getSize();
        Dimension parentSize = this.parent.getSize();
        setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);
        this.trafficGeneratorNode = null;
        this.labelCoordinateX.setText(this.translations.getString("VentanaEmisor.X=_") + this.coordinatesPanel.getRealX());
        this.labelCoordinateY.setText(this.translations.getString("VentanaEmisor.Y=_") + this.coordinatesPanel.getRealY());
        Iterator iterator = this.topology.getNodesIterator();
        this.comboBoxTargetTrafficSinkNode.removeAllItems();
        this.comboBoxTargetTrafficSinkNode.addItem("");
        TNode node;
        while (iterator.hasNext()) {
            node = (TNode) iterator.next();
            if (node.getNodeType() == TNode.TRAFFIC_SINK) {
                this.comboBoxTargetTrafficSinkNode.addItem(node.getName());
            }
        }
        this.comboBoxGoSLevel.removeAllItems();
        this.comboBoxGoSLevel.addItem(this.translations.getString("JVentanaEmisor.None"));
        this.comboBoxGoSLevel.addItem(this.translations.getString("JVentanaEmisor.Level_1"));
        this.comboBoxGoSLevel.addItem(this.translations.getString("JVentanaEmisor.Level_2"));
        this.comboBoxGoSLevel.addItem(this.translations.getString("JVentanaEmisor.Level_3"));
        this.comboBoxGoSLevel.setSelectedIndex(0);
        this.comboBoxPredefinedOptions.removeAllItems();
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaEmisor.Personalized"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaEmisor.Email"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaEmisor.Web"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaEmisor.P2P_file_sharing"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaEmisor.Bank_data_transaction"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaEmisor.Tele-medical_video"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaEmisor.Bulk_traffic"));
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.comboBoxTargetTrafficSinkNode.setSelectedIndex(0);
        this.currentConfigTargetTrafficSinkNode = "";
        this.currentConfigCreateBackupLSP = false;
        this.currentConfigShowName = true;
        this.currentConfigGoSLevel = 0;
        this.currentConfigName = "";
        this.currentConfigTrafficGenerationRate = 1000;
        this.currentConfigTrafficType = TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE;
        this.currentConfigGenerateStatistics = false;
        this.currentConfigConstanTrafficPayloadSize = 1024;
        this.currentConfigEncapsulateOverMPLS = false;
        this.reconfiguration = false;
    }

    /**
     * This method is called when a a predefined option is selected in the UI to
     * configure the traffic generator.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnPredefinedOptions(ActionEvent evt) {
        int selectedOption = this.comboBoxPredefinedOptions.getSelectedIndex();
        if (selectedOption > 0) {
            if (selectedOption == 1) {
                this.checkBoxBackupLSP.setSelected(false);
                this.comboBoxGoSLevel.setSelectedIndex(0);
                this.radioButtonConstantPacketSize.setSelected(false);
                this.constantTrafficVariablePacketSize.setSelected(true);
                this.sliderConstantTrafficPayloadSize.setValue(0);
                this.sliderTrafficGenerationRate.setValue(1);
                this.sliderConstantTrafficPayloadSize.setEnabled(false);
            } else if (selectedOption == 2) {
                this.checkBoxBackupLSP.setSelected(false);
                this.comboBoxGoSLevel.setSelectedIndex(0);
                this.radioButtonConstantPacketSize.setSelected(false);
                this.constantTrafficVariablePacketSize.setSelected(true);
                this.sliderConstantTrafficPayloadSize.setValue(0);
                this.sliderTrafficGenerationRate.setValue(7);
                this.sliderConstantTrafficPayloadSize.setEnabled(false);
            } else if (selectedOption == 3) {
                this.checkBoxBackupLSP.setSelected(false);
                this.comboBoxGoSLevel.setSelectedIndex(0);
                this.radioButtonConstantPacketSize.setSelected(false);
                this.constantTrafficVariablePacketSize.setSelected(true);
                this.sliderConstantTrafficPayloadSize.setValue(0);
                this.sliderTrafficGenerationRate.setValue(3413);
                this.sliderConstantTrafficPayloadSize.setEnabled(false);
            } else if (selectedOption == 4) {
                this.checkBoxBackupLSP.setSelected(true);
                this.comboBoxGoSLevel.setSelectedIndex(0);
                this.radioButtonConstantPacketSize.setSelected(false);
                this.constantTrafficVariablePacketSize.setSelected(true);
                this.sliderConstantTrafficPayloadSize.setValue(0);
                this.sliderTrafficGenerationRate.setValue(10240);
                this.sliderConstantTrafficPayloadSize.setEnabled(false);
            } else if (selectedOption == 5) {
                this.checkBoxBackupLSP.setSelected(true);
                this.comboBoxGoSLevel.setSelectedIndex(2);
                this.radioButtonConstantPacketSize.setSelected(false);
                this.constantTrafficVariablePacketSize.setSelected(true);
                this.sliderConstantTrafficPayloadSize.setValue(0);
                this.sliderTrafficGenerationRate.setValue(341);
                this.sliderConstantTrafficPayloadSize.setEnabled(false);
            } else if (selectedOption == 6) {
                this.checkBoxBackupLSP.setSelected(false);
                this.comboBoxGoSLevel.setSelectedIndex(0);
                this.radioButtonConstantPacketSize.setSelected(false);
                this.constantTrafficVariablePacketSize.setSelected(true);
                this.sliderConstantTrafficPayloadSize.setValue(0);
                this.sliderTrafficGenerationRate.setValue(6827);
                this.sliderConstantTrafficPayloadSize.setEnabled(false);
            }
        }
        this.comboBoxPredefinedOptions.setSelectedIndex(selectedOption);
    }

    /**
     * This method is called when a change is made in payload size (for constant
     * traffic generation), in the UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleChangeOnConstantTrafficPayloadSize(ChangeEvent evt) {
        int selectedPayloadSize = this.sliderConstantTrafficPayloadSize.getValue();
        String units = this.translations.getString("JVentanaEmisor.Octetos");
        this.labelConstanTrafficPayloadSize.setText(selectedPayloadSize + " " + units);
    }

    /**
     * This method is called when a change is made in "generate stistics"
     * checkbox located at "Advanced configuration" tab (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnAdvancedGenerateStatistics(ActionEvent evt) {
        this.checkBoxQuickGenerateStatistics.setSelected(this.checkBoxAdvancedGenerateStatistics.isSelected());
    }

    /**
     * This method is called when a change is made in "generate stistics"
     * checkbox located at "Quick configuration" tab (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnQuickGenerateStatistics(ActionEvent evt) {
        this.checkBoxAdvancedGenerateStatistics.setSelected(this.checkBoxQuickGenerateStatistics.isSelected());
    }

    /**
     * This method is called when a change is made in "Backup LSP", in the UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnBackupLSP(ActionEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
    }

    /**
     * This method is called when a change is made in "GoS Level", in the UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnGoSLevel(ActionEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
    }

    /**
     * This method is called when a change is made in "Encapsulate over MPLS",
     * in the UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnEncapsulateOverMPLS(ActionEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
    }

    /**
     * This method is called when a change is made in "Variable traffic", in the
     * UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnVariableTraffic(ActionEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.sliderConstantTrafficPayloadSize.setEnabled(false);
        this.labelConstanTrafficPayloadSize.setEnabled(false);
        this.labelPayloadSize.setEnabled(false);
    }

    /**
     * This method is called when a change is made in "Constant traffic", in the
     * UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnConstantTraffic(ActionEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.sliderConstantTrafficPayloadSize.setEnabled(true);
        this.labelConstanTrafficPayloadSize.setEnabled(true);
        this.labelPayloadSize.setEnabled(true);
    }

    /**
     * This method is called when a change is made in "Traffic generation rate",
     * in the UI.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleChangeOnTrafficGenerationRate(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        int selectedTrafficGenerationRate = this.sliderTrafficGenerationRate.getValue();
        String units = this.translations.getString("VentanaEmisor.unidades.kbps");
        this.labelTrafficGenerationRateMbps.setText(selectedTrafficGenerationRate + " " + units);
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
            this.trafficGeneratorNode.setTargetNode(this.currentConfigTargetTrafficSinkNode);
            this.trafficGeneratorNode.setRequestBackupLSP(this.currentConfigCreateBackupLSP);
            this.trafficGeneratorNode.setShowName(this.currentConfigShowName);
            this.trafficGeneratorNode.setGoSLevel(this.currentConfigGoSLevel);
            this.trafficGeneratorNode.setName(this.currentConfigName);
            this.trafficGeneratorNode.setTrafficGenerationRate(this.currentConfigTrafficGenerationRate);
            this.trafficGeneratorNode.setTrafficGenerationMode(this.currentConfigTrafficType);
            this.trafficGeneratorNode.setGenerateStats(this.currentConfigGenerateStatistics);
            this.trafficGeneratorNode.setConstantPayloadSizeInBytes(this.currentConfigConstanTrafficPayloadSize);
            this.trafficGeneratorNode.setWellConfigured(true);
            this.trafficGeneratorNode.encapsulateOverMPLS(this.currentConfigEncapsulateOverMPLS);
            this.reconfiguration = false;
        } else {
            this.trafficGeneratorNode.setWellConfigured(false);
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
        this.trafficGeneratorNode.setWellConfigured(true);
        if (!this.reconfiguration) {
            this.trafficGeneratorNode.setScreenPosition(new Point(this.coordinatesPanel.getRealX(), this.coordinatesPanel.getRealY()));
        }
        this.trafficGeneratorNode.setName(this.textFieldName.getText());
        this.trafficGeneratorNode.setShowName(this.checkBoxShowName.isSelected());
        this.trafficGeneratorNode.setGenerateStats(this.checkBoxAdvancedGenerateStatistics.isSelected());
        this.trafficGeneratorNode.setTrafficGenerationRate(this.sliderTrafficGenerationRate.getValue());
        this.trafficGeneratorNode.setRequestBackupLSP(this.checkBoxBackupLSP.isSelected());
        this.trafficGeneratorNode.encapsulateOverMPLS(this.checkBoxEncapsulateOverMPLS.isSelected());
        this.trafficGeneratorNode.setGoSLevel(this.comboBoxGoSLevel.getSelectedIndex());
        this.trafficGeneratorNode.setTargetNode((String) this.comboBoxTargetTrafficSinkNode.getSelectedItem());
        this.trafficGeneratorNode.setConstantPayloadSizeInBytes(this.sliderConstantTrafficPayloadSize.getValue());
        if (this.radioButtonConstantPacketSize.isSelected()) {
            this.trafficGeneratorNode.setTrafficGenerationMode(TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE);
        } else if (this.constantTrafficVariablePacketSize.isSelected()) {
            this.trafficGeneratorNode.setTrafficGenerationMode(TTrafficGeneratorNode.VARIABLE_TRAFFIC_RATE);
        }
        int error = this.trafficGeneratorNode.validateConfig(this.topology, this.reconfiguration);
        if (error != TTrafficGeneratorNode.OK) {
            JWarningWindow warningWindow = new JWarningWindow(this.parent, true, this.imageBroker);
            warningWindow.setWarningMessage(this.trafficGeneratorNode.getErrorMessage(error));
            warningWindow.setVisible(true);
        } else {
            this.reconfiguration = false;
            this.setVisible(false);
            this.dispose();
        }
    }

    /**
     * This method is called when a click is done over the coordinates panel (in
     * the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnCoordinatesPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            this.coordinatesPanel.setCoordinates(evt.getPoint());
            this.labelCoordinateX.setText(this.translations.getString("VentanaEmisor.X=_") + this.coordinatesPanel.getRealX());
            this.labelCoordinateY.setText(this.translations.getString("VentanaEmisor.Y=_") + this.coordinatesPanel.getRealY());
            this.coordinatesPanel.repaint();
        }
    }

    /**
     * This method is called when the mouse exits the coordinates panel (in the
     * UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleMouseLeavingCoordinatesPanel(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the mouse enters the coordinates panel (in the
     * UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleMouseEnteringInCoordinatesPanel(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * This method is called when the JTrafficGeneratorWindow is closed (in the
     * UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleWindowsClosing(WindowEvent evt) {
        setVisible(false);
        this.trafficGeneratorNode.setWellConfigured(false);
        dispose();
    }

    /**
     * This method configures all components of JTrafficGeneratorWindow with
     * present values retrieved from the traffic generator node. It is used to
     * do a reconfiguration.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param trafficGeneratorNode the traffic generator node to be configured
     * through this JTrafficGeneratorWindow
     * @param reconfiguration TRUE if the traffic generator node is being
     * reconfigured. FALSE if it is the first configuration of the traffic
     * generator node after its creation.
     * @since 2.0
     */
    public void setConfiguration(TTrafficGeneratorNode trafficGeneratorNode, boolean reconfiguration) {
        this.trafficGeneratorNode = trafficGeneratorNode;
        this.reconfiguration = reconfiguration;
        if (this.reconfiguration) {
            this.coordinatesPanel.setEnabled(false);
            this.coordinatesPanel.setToolTipText(null);
            TNode node = this.topology.getNode(this.trafficGeneratorNode.getTargetIPv4Address());
            if (node != null) {
                this.currentConfigTargetTrafficSinkNode = node.getName();
            }
            this.currentConfigCreateBackupLSP = this.trafficGeneratorNode.isRequestingBackupLSP();
            this.currentConfigShowName = this.trafficGeneratorNode.getShowName();
            this.currentConfigGoSLevel = this.trafficGeneratorNode.getGoSLevel();
            this.currentConfigName = this.trafficGeneratorNode.getName();
            this.currentConfigTrafficGenerationRate = this.trafficGeneratorNode.getTrafficGenerationRate();
            this.currentConfigTrafficType = this.trafficGeneratorNode.getTrafficGenerationMode();
            this.currentConfigGenerateStatistics = this.trafficGeneratorNode.isGeneratingStats();
            this.currentConfigConstanTrafficPayloadSize = this.trafficGeneratorNode.getConstantPayloadSizeInBytes();
            this.currentConfigEncapsulateOverMPLS = this.trafficGeneratorNode.isEncapsulatingOverMPLS();
            this.checkBoxEncapsulateOverMPLS.setSelected(this.currentConfigEncapsulateOverMPLS);
            this.textFieldName.setText(this.currentConfigName);
            if (this.currentConfigTrafficType == TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE) {
                this.radioButtonConstantPacketSize.setSelected(true);
                this.constantTrafficVariablePacketSize.setSelected(false);
            } else {
                this.radioButtonConstantPacketSize.setSelected(false);
                this.constantTrafficVariablePacketSize.setSelected(true);
            }
            this.checkBoxAdvancedGenerateStatistics.setSelected(this.currentConfigGenerateStatistics);
            this.checkBoxQuickGenerateStatistics.setSelected(this.currentConfigGenerateStatistics);
            this.checkBoxBackupLSP.setSelected(this.currentConfigCreateBackupLSP);
            this.checkBoxShowName.setSelected(this.currentConfigShowName);
            int numTargetTrafficSinkNodes = this.comboBoxTargetTrafficSinkNode.getItemCount();
            int i = 0;
            String auxTargetTrafficSinkNode;
            for (i = 0; i < numTargetTrafficSinkNodes; i++) {
                auxTargetTrafficSinkNode = (String) this.comboBoxTargetTrafficSinkNode.getItemAt(i);
                if (auxTargetTrafficSinkNode.equals(this.currentConfigTargetTrafficSinkNode)) {
                    this.comboBoxTargetTrafficSinkNode.setSelectedIndex(i);
                }
            }
            if (this.comboBoxGoSLevel.getItemCount() >= this.currentConfigGoSLevel) {
                this.comboBoxGoSLevel.setSelectedIndex(this.currentConfigGoSLevel);
            }
            this.comboBoxPredefinedOptions.setSelectedIndex(0);
            this.sliderTrafficGenerationRate.setValue(this.currentConfigTrafficGenerationRate);

            if (this.currentConfigTrafficType == TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE) {
                this.sliderConstantTrafficPayloadSize.setEnabled(true);
                this.labelConstanTrafficPayloadSize.setEnabled(true);
                this.labelPayloadSize.setEnabled(true);
                String units = this.translations.getString("JVentanaEmisor.Octetos");
                this.labelConstanTrafficPayloadSize.setText(this.currentConfigConstanTrafficPayloadSize + " " + units);
            }
            this.sliderConstantTrafficPayloadSize.setValue(this.currentConfigConstanTrafficPayloadSize);
        }
    }

    private TImageBroker imageBroker;
    private Frame parent;
    private JDesignPanel designPanel;
    private TTrafficGeneratorNode trafficGeneratorNode;
    private TTopology topology;
    private String currentConfigTargetTrafficSinkNode;
    private boolean currentConfigCreateBackupLSP;
    private boolean currentConfigShowName;
    private int currentConfigGoSLevel;
    private String currentConfigName;
    private int currentConfigTrafficGenerationRate;
    private int currentConfigTrafficType;
    private boolean currentConfigGenerateStatistics;
    private int currentConfigConstanTrafficPayloadSize;
    private boolean currentConfigEncapsulateOverMPLS;
    private boolean reconfiguration;
    private ButtonGroup buttonGroup;
    private JLabel labelCoordinateX;
    private JLabel labelCoordinateY;
    private JCheckBox checkBoxEncapsulateOverMPLS;
    private JLabel labelName;
    private JLabel labelConstanTrafficPayloadSize;
    private JLabel labelPayloadSize;
    private JLabel labelTrafficGenerationRateMbps;
    private JLabel iconContainerTrafficGenerator;
    private JLabel labelQuickConfiguration;
    private JLabel labelAdvancedConfiguration;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel labelQuickTrafficType;
    private JLabel labelTrafficGenerationRate;
    private JLabel labelGoSLevel;
    private JLabel labelAdvancedTrafficType;
    private JLabel labelTargetTrafficSinkNode;
    private JTextField textFieldName;
    private JPanel panelAdvancedConfiguration;
    private JPanel panelButtons;
    private JCoordinatesPanel coordinatesPanel;
    private JPanel panelGeneralConfiguration;
    private JTabbedPane panelTabs;
    private JPanel panelPosition;
    private JPanel mainPanel;
    private JPanel panelQuickConfiguration;
    private JCheckBox checkBoxAdvancedGenerateStatistics;
    private JCheckBox checkBoxQuickGenerateStatistics;
    private JComboBox comboBoxGoSLevel;
    private JSlider sliderConstantTrafficPayloadSize;
    private JSlider sliderTrafficGenerationRate;
    private JComboBox comboBoxTargetTrafficSinkNode;
    private JCheckBox checkBoxBackupLSP;
    private JComboBox comboBoxPredefinedOptions;
    private JRadioButton radioButtonConstantPacketSize;
    private JRadioButton constantTrafficVariablePacketSize;
    private JCheckBox checkBoxShowName;
    private ResourceBundle translations;
}
