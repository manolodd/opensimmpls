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
import com.manolodominguez.opensimmpls.scenario.TLERNode;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.gui.simulator.JDesignPanel;
import com.manolodominguez.opensimmpls.gui.utils.TImageBroker;
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
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
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
 * LER node.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
@SuppressWarnings("serial")
public class JLERWindow extends JDialog {

    /**
     * This is the constructor of the class and creates a new instance of
     * JLERWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param designPanel desing panel wich contains the LER node that is
     * configured via this JLERWindow
     * @param parent Parent window over wich this JLERWindow is shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param modal TRUE, if this dialog has to be modal. Otherwise, FALSE.
     * @param topology Topology the LER node belongs to.
     * @since 2.0
     */
    public JLERWindow(TTopology topology, JDesignPanel designPanel, TImageBroker imageBroker, Frame parent, boolean modal) {
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
        this.translations = ResourceBundle.getBundle(AvailableBundles.LER_WINDOW.getPath());
        this.panelPrincipal = new JPanel();
        this.panelTabs = new JTabbedPane();
        this.mainPanel = new JPanel();
        this.iconContainerLER = new JLabel();
        this.labelName = new JLabel();
        this.textFieldName = new JTextField();
        this.panelPosition = new JPanel();
        this.labelCoordinateX = new JLabel();
        this.labelCoordinateY = new JLabel();
        this.coordinatesPanel = new JCoordinatesPanel();
        this.checkBoxShowName = new JCheckBox();
        this.panelQuickConfiguration = new JPanel();
        this.checkBoxQuickGenerateStatistics = new JCheckBox();
        this.labelQuickConfiguration = new JLabel();
        this.labelLERFeatures = new JLabel();
        this.comboBoxPredefinedOptions = new JComboBox<>();
        this.panelAdvancedConfiguration = new JPanel();
        this.checkBoxAdvancedGenerateStatistics = new JCheckBox();
        this.labelAdvancedConfiguration = new JLabel();
        this.labelRoutingPower = new JLabel();
        this.sliderRoutingPower = new JSlider();
        this.labelRoutingPowerMbps = new JLabel();
        this.labelBufferSize = new JLabel();
        this.sliderBufferSize = new JSlider();
        this.labelBufferSizeMB = new JLabel();
        this.panelButtons = new JPanel();
        this.buttonOK = new JButton();
        this.buttonCancel = new JButton();
        setTitle(this.translations.getString("VentanaLER.titulo"));
        setModal(true);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleWindowsClosing(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());
        this.panelPrincipal.setLayout(new AbsoluteLayout());
        this.panelTabs.setFont(new Font("Dialog", 0, 12));
        this.mainPanel.setLayout(new AbsoluteLayout());
        this.iconContainerLER.setIcon(this.imageBroker.getImageIcon(AvailableImages.LER));
        this.iconContainerLER.setText(this.translations.getString("VentanaLER.descripcion"));
        this.mainPanel.add(iconContainerLER, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelName.setFont(new Font("Dialog", 0, 12));
        this.labelName.setText(this.translations.getString("VentanaLER.etiquetaNombre"));
        this.mainPanel.add(this.labelName, new AbsoluteConstraints(215, 80, 120, -1));
        this.mainPanel.add(textFieldName, new AbsoluteConstraints(215, 105, 125, -1));
        this.panelPosition.setBorder(BorderFactory.createTitledBorder(this.translations.getString("VentanaLER.etiquetaGrupo")));
        this.panelPosition.setLayout(new AbsoluteLayout());
        this.labelCoordinateX.setFont(new Font("Dialog", 0, 12));
        this.labelCoordinateX.setText(this.translations.getString("VentanaLER.X="));
        this.panelPosition.add(this.labelCoordinateX, new AbsoluteConstraints(100, 100, -1, -1));
        this.labelCoordinateY.setFont(new Font("Dialog", 0, 12));
        this.labelCoordinateY.setText(this.translations.getString("VentanaLER.Y="));
        this.panelPosition.add(labelCoordinateY, new AbsoluteConstraints(40, 100, -1, -1));
        this.coordinatesPanel.setBackground(new Color(255, 255, 255));
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
        this.mainPanel.add(this.panelPosition, new AbsoluteConstraints(15, 75, 180, 125));
        this.checkBoxShowName.setFont(new Font("Dialog", 0, 12));
        this.checkBoxShowName.setSelected(true);
        this.checkBoxShowName.setText(this.translations.getString("VentanaLER.verNombre"));
        this.mainPanel.add(this.checkBoxShowName, new AbsoluteConstraints(215, 135, -1, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaLER.tabs.General"), this.mainPanel);
        this.panelQuickConfiguration.setLayout(new AbsoluteLayout());
        this.checkBoxQuickGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        this.checkBoxQuickGenerateStatistics.setText(this.translations.getString("VentanaLER.GenerarEstadisticas"));
        this.checkBoxQuickGenerateStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnQuickGenerateStatistics(evt);
            }
        });
        this.panelQuickConfiguration.add(this.checkBoxQuickGenerateStatistics, new AbsoluteConstraints(70, 160, -1, -1));
        this.labelQuickConfiguration.setIcon(this.imageBroker.getImageIcon(AvailableImages.WIZARD));
        this.labelQuickConfiguration.setText(this.translations.getString("VentanaLER.ConfiguracionRapida"));
        this.panelQuickConfiguration.add(labelQuickConfiguration, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelLERFeatures.setFont(new Font("Dialog", 0, 12));
        this.labelLERFeatures.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelLERFeatures.setText(this.translations.getString("VentanaLER.CaracteristicasDelLER"));
        this.panelQuickConfiguration.add(this.labelLERFeatures, new AbsoluteConstraints(20, 110, 160, -1));
        this.comboBoxPredefinedOptions.setFont(new Font("Dialog", 0, 12));
        this.comboBoxPredefinedOptions.setModel(new DefaultComboBoxModel<>(new String[]{"Personalized", "Very low cost LER", "Low cost LER", "Medium cost LER", "Expensive LER", "Very expensive LER"}));
        this.comboBoxPredefinedOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPredefinedOptions(evt);
            }
        });
        this.panelQuickConfiguration.add(this.comboBoxPredefinedOptions, new AbsoluteConstraints(190, 110, -1, -1));
        this.panelTabs.addTab(this.translations.getString("VentanaLER.tabs.Fast"), this.panelQuickConfiguration);
        this.panelAdvancedConfiguration.setLayout(new AbsoluteLayout());
        this.checkBoxAdvancedGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        this.checkBoxAdvancedGenerateStatistics.setText(this.translations.getString("VentanaLER.GenerarEstadisticas"));
        this.checkBoxAdvancedGenerateStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnAdvancedGenerateStatistics(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.checkBoxAdvancedGenerateStatistics, new AbsoluteConstraints(70, 160, -1, -1));
        this.labelAdvancedConfiguration.setIcon(this.imageBroker.getImageIcon(AvailableImages.ADVANCED));
        this.labelAdvancedConfiguration.setText(this.translations.getString("VentanaLER.ConfiguracionAvanzada"));
        this.panelAdvancedConfiguration.add(labelAdvancedConfiguration, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelRoutingPower.setFont(new Font("Dialog", 0, 12));
        this.labelRoutingPower.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelRoutingPower.setText(this.translations.getString("VentanaLER.PotenciaDeConmutacion"));
        this.panelAdvancedConfiguration.add(this.labelRoutingPower, new AbsoluteConstraints(10, 90, 140, -1));
        this.sliderRoutingPower.setMajorTickSpacing(1000);
        this.sliderRoutingPower.setMaximum(10240);
        this.sliderRoutingPower.setMinimum(1);
        this.sliderRoutingPower.setMinorTickSpacing(100);
        this.sliderRoutingPower.setValue(1);
        this.sliderRoutingPower.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnRoutingPower(evt);
            }
        });
        this.panelAdvancedConfiguration.add(sliderRoutingPower, new AbsoluteConstraints(155, 90, 130, 20));
        this.labelRoutingPowerMbps.setFont(new Font("Dialog", 0, 10));
        this.labelRoutingPowerMbps.setForeground(new Color(102, 102, 102));
        this.labelRoutingPowerMbps.setHorizontalAlignment(SwingConstants.LEFT);
        this.labelRoutingPowerMbps.setText(this.translations.getString("VentanaLER.1_Mbps"));
        this.panelAdvancedConfiguration.add(labelRoutingPowerMbps, new AbsoluteConstraints(290, 90, 70, 20));
        this.labelBufferSize.setFont(new Font("Dialog", 0, 12));
        this.labelBufferSize.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelBufferSize.setText(this.translations.getString("VentanaLER.TamanioDelBufferDeEntrada"));
        this.panelAdvancedConfiguration.add(labelBufferSize, new AbsoluteConstraints(10, 120, 180, -1));
        this.sliderBufferSize.setMajorTickSpacing(50);
        this.sliderBufferSize.setMaximum(1024);
        this.sliderBufferSize.setMinimum(1);
        this.sliderBufferSize.setMinorTickSpacing(100);
        this.sliderBufferSize.setValue(1);
        this.sliderBufferSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnBufferSize(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.sliderBufferSize, new AbsoluteConstraints(200, 120, 100, 20));
        this.labelBufferSizeMB.setFont(new Font("Dialog", 0, 10));
        this.labelBufferSizeMB.setForeground(new Color(102, 102, 102));
        this.labelBufferSizeMB.setHorizontalAlignment(SwingConstants.LEFT);
        this.labelBufferSizeMB.setText(this.translations.getString("VentanaLER.1_MB"));
        this.panelAdvancedConfiguration.add(this.labelBufferSizeMB, new AbsoluteConstraints(300, 120, 60, 20));
        this.panelTabs.addTab(this.translations.getString("VentanaLER.tabs.Advanced"), this.panelAdvancedConfiguration);
        this.panelPrincipal.add(panelTabs, new AbsoluteConstraints(15, 15, 370, 240));
        this.panelButtons.setLayout(new AbsoluteLayout());
        this.buttonOK.setFont(new Font("Dialog", 0, 12));
        this.buttonOK.setIcon(imageBroker.getImageIcon(AvailableImages.ACCEPT));
        this.buttonOK.setMnemonic(this.translations.getString("VentanaLER.botones.mne.Aceptar").charAt(0));
        this.buttonOK.setText(this.translations.getString("VentanaLER.boton.Ok"));
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOKButton(evt);
            }
        });
        this.panelButtons.add(this.buttonOK, new AbsoluteConstraints(15, 15, 115, -1));
        this.buttonCancel.setFont(new Font("Dialog", 0, 12));
        this.buttonCancel.setIcon(imageBroker.getImageIcon(AvailableImages.CANCEL));
        this.buttonCancel.setMnemonic(this.translations.getString("VentanaLER.botones.mne.Cancelar").charAt(0));
        this.buttonCancel.setText(this.translations.getString("VentanaLER.boton.Cancel"));
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnCancelButton(evt);
            }
        });
        this.panelButtons.add(this.buttonCancel, new AbsoluteConstraints(140, 15, 115, -1));
        this.panelPrincipal.add(this.panelButtons, new AbsoluteConstraints(0, 255, 400, 55));
        getContentPane().add(this.panelPrincipal, new AbsoluteConstraints(0, 0, -1, 310));
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
        this.lerNode = null;
        this.labelCoordinateX.setText(this.translations.getString("JVentanaLER.X=") + this.coordinatesPanel.getRealX());
        this.labelCoordinateY.setText(this.translations.getString("JVentanaLER.Y=") + this.coordinatesPanel.getRealY());
        this.currentConfigShowName = true;
        this.currentConfigName = "";
        this.currentConfigRoutingPower = 0;
        this.currentConfigBufferSize = 0;
        this.reconguration = false;
        this.currentConfigGenerateStatistics = false;
        this.comboBoxPredefinedOptions.removeAllItems();
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Personalized_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Very_low_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Low_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Medium_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("High_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Very_high_range_LER"));
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
    }

    /**
     * This method is called when a a predefined option is selected in the UI to
     * configure the LER.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnPredefinedOptions(ActionEvent evt) {
        int selectedOption = this.comboBoxPredefinedOptions.getSelectedIndex();
        if (selectedOption == 0) {
            // Do nothing
            this.comboBoxPredefinedOptions.setSelectedIndex(0);
        } else if (selectedOption == 1) {
            this.sliderRoutingPower.setValue(1);
            this.sliderBufferSize.setValue(1);
            this.comboBoxPredefinedOptions.setSelectedIndex(1);
        } else if (selectedOption == 2) {
            this.sliderRoutingPower.setValue(2560);
            this.sliderBufferSize.setValue(256);
            this.comboBoxPredefinedOptions.setSelectedIndex(2);
        } else if (selectedOption == 3) {
            this.sliderRoutingPower.setValue(5120);
            this.sliderBufferSize.setValue(512);
            this.comboBoxPredefinedOptions.setSelectedIndex(3);
        } else if (selectedOption == 4) {
            this.sliderRoutingPower.setValue(7680);
            this.sliderBufferSize.setValue(768);
            this.comboBoxPredefinedOptions.setSelectedIndex(4);
        } else if (selectedOption == 5) {
            this.sliderRoutingPower.setValue(10240);
            this.sliderBufferSize.setValue(1024);
            this.comboBoxPredefinedOptions.setSelectedIndex(5);
        }
    }

    /**
     * This method is called when a change is made in the buffer size (in the
     * UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleChangeOnBufferSize(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.labelBufferSizeMB.setText(this.sliderBufferSize.getValue() + " " + this.translations.getString("VentanaLER.MB"));
    }

    /**
     * This method is called when a change is made in the routing power (in the
     * UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleChangeOnRoutingPower(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.labelRoutingPowerMbps.setText(this.sliderRoutingPower.getValue() + " " + this.translations.getString("VentanaLER.Mbps."));
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
     * This method is called when a click is done "Cancel" button (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnCancelButton(ActionEvent evt) {
        if (this.reconguration) {
            this.lerNode.setShowName(this.currentConfigShowName);
            this.lerNode.setName(this.currentConfigName);
            this.lerNode.setWellConfigured(true);
            this.lerNode.setBufferSizeInMBytes(this.currentConfigBufferSize);
            this.lerNode.setRoutingPowerInMbps(this.currentConfigRoutingPower);
            this.lerNode.setGenerateStats(this.currentConfigGenerateStatistics);
            this.reconguration = false;
        } else {
            this.lerNode.setWellConfigured(false);
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
        this.lerNode.setWellConfigured(true);
        if (!this.reconguration) {
            this.lerNode.setScreenPosition(new Point(this.coordinatesPanel.getRealX(), this.coordinatesPanel.getRealY()));
        }
        this.lerNode.setBufferSizeInMBytes(this.sliderBufferSize.getValue());
        this.lerNode.setRoutingPowerInMbps(this.sliderRoutingPower.getValue());
        this.lerNode.setGenerateStats(this.checkBoxQuickGenerateStatistics.isSelected());
        this.lerNode.setName(this.textFieldName.getText());
        this.lerNode.setShowName(this.checkBoxShowName.isSelected());
        this.lerNode.setGenerateStats(this.checkBoxQuickGenerateStatistics.isSelected());
        int error = this.lerNode.validateConfig(this.topology, this.reconguration);
        if (error != TLERNode.OK) {
            JWarningWindow warningWindow = new JWarningWindow(this.parent, true, this.imageBroker);
            warningWindow.setWarningMessage(this.lerNode.getErrorMessage(error));
            warningWindow.setVisible(true);
        } else {
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
            this.labelCoordinateX.setText(this.translations.getString("VentanaconfigLER.X=_") + this.coordinatesPanel.getRealX());
            this.labelCoordinateY.setText(this.translations.getString("VentanaconfigLER.Y=_") + this.coordinatesPanel.getRealY());
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
     * This method is called when the JLERWindow is closed (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleWindowsClosing(WindowEvent evt) {
        setVisible(false);
        this.lerNode.setWellConfigured(false);
        dispose();
    }

    /**
     * This method configures all components of JLERWindow with present values
     * retrieved from the LER. It is used to do a reconfiguration.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param lerNode the LER node to be configured through this JLERWindow
     * @param reconfiguration TRUE if the LER is being reconfigured. FALSE if it
     * is the first configuration of the LER after its creation.
     * @since 2.0
     */
    public void setConfiguration(TLERNode lerNode, boolean reconfiguration) {
        this.lerNode = lerNode;
        this.reconguration = reconfiguration;
        if (this.reconguration) {
            this.coordinatesPanel.setEnabled(false);
            this.coordinatesPanel.setToolTipText(null);
            this.currentConfigGenerateStatistics = lerNode.isGeneratingStats();
            this.currentConfigShowName = lerNode.getShowName();
            this.currentConfigName = lerNode.getName();
            this.currentConfigRoutingPower = lerNode.getRoutingPowerInMbps();
            this.currentConfigBufferSize = lerNode.getBufferSizeInMBytes();
            this.checkBoxAdvancedGenerateStatistics.setSelected(this.currentConfigGenerateStatistics);
            this.checkBoxQuickGenerateStatistics.setSelected(this.currentConfigGenerateStatistics);
            this.sliderRoutingPower.setValue(this.currentConfigRoutingPower);
            this.sliderBufferSize.setValue(this.currentConfigBufferSize);
            this.textFieldName.setText(this.currentConfigName);
            this.checkBoxShowName.setSelected(this.currentConfigShowName);
        }
    }

    private TImageBroker imageBroker;
    private Frame parent;
    private JDesignPanel designPanel;
    private TLERNode lerNode;
    private TTopology topology;
    private boolean currentConfigShowName;
    private String currentConfigName;
    private int currentConfigRoutingPower;
    private int currentConfigBufferSize;
    private boolean currentConfigGenerateStatistics;
    private boolean reconguration;
    private JLabel labelCoordinateX;
    private JLabel labelCoordinateY;
    private JLabel labelBufferSizeMB;
    private JLabel labelName;
    private JLabel labelRoutingPowerMbps;
    private JLabel labelQuickConfiguration;
    private JLabel labelAdvancedConfiguration;
    private JLabel iconContainerLER;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel labelLERFeatures;
    private JLabel labelRoutingPower;
    private JLabel labelBufferSize;
    private JTextField textFieldName;
    private JPanel panelAdvancedConfiguration;
    private JPanel panelButtons;
    private JCoordinatesPanel coordinatesPanel;
    private JPanel mainPanel;
    private JTabbedPane panelTabs;
    private JPanel panelPosition;
    private JPanel panelPrincipal;
    private JPanel panelQuickConfiguration;
    private JCheckBox checkBoxAdvancedGenerateStatistics;
    private JCheckBox checkBoxQuickGenerateStatistics;
    private JSlider sliderRoutingPower;
    private JSlider sliderBufferSize;
    private JComboBox<String> comboBoxPredefinedOptions;
    private JCheckBox checkBoxShowName;
    private ResourceBundle translations;
}
