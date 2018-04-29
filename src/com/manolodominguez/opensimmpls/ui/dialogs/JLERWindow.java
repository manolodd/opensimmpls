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
import com.manolodominguez.opensimmpls.scenario.TLERNode;
import com.manolodominguez.opensimmpls.scenario.TTopology;
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

public class JLERWindow extends JDialog {

    public JLERWindow(TTopology t, JDesignPanel pad, TImageBroker di, Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        imageBroker = di;
        designPanel = pad;
        topology = t;
        initComponents();
        initComponents2();
    }

    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.LER_WINDOW.getPath());
        panelPrincipal = new JPanel();
        panelTabs = new JTabbedPane();
        mainPanel = new JPanel();
        iconContainerLER = new JLabel();
        labelName = new JLabel();
        textFieldName = new JTextField();
        panelPosition = new JPanel();
        labelCoordinateX = new JLabel();
        labelCoordinateY = new JLabel();
        coordinatesPanel = new JCoordinatesPanel();
        checkBoxShowName = new JCheckBox();
        panelQuickConfiguration = new JPanel();
        checkBoxQuickGenerateStatistics = new JCheckBox();
        iconContainerEnd1 = new JLabel();
        labelLERFeatures = new JLabel();
        comboBoxPredefinedOptions = new JComboBox();
        panelAdvancedConfiguration = new JPanel();
        checkBoxAdvancedGenerateStatistics = new JCheckBox();
        iconContainerEnd2 = new JLabel();
        labelRoutingPower = new JLabel();
        selectorRoutingPower = new JSlider();
        labelRoutingPowerMbps = new JLabel();
        labelBufferSize = new JLabel();
        selectorBufferSize = new JSlider();
        labelBufferSizeMB = new JLabel();
        panelButtons = new JPanel();
        buttonOK = new JButton();
        buttonCancel = new JButton();
        setTitle(this.translations.getString("VentanaLER.titulo"));
        setModal(true);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());
        panelPrincipal.setLayout(new AbsoluteLayout());
        panelTabs.setFont(new Font("Dialog", 0, 12));
        mainPanel.setLayout(new AbsoluteLayout());
        iconContainerLER.setIcon(imageBroker.getIcon(TImageBroker.LER));
        iconContainerLER.setText(this.translations.getString("VentanaLER.descripcion"));
        mainPanel.add(iconContainerLER, new AbsoluteConstraints(15, 20, 335, -1));
        labelName.setFont(new Font("Dialog", 0, 12));
        labelName.setText(this.translations.getString("VentanaLER.etiquetaNombre"));
        mainPanel.add(labelName, new AbsoluteConstraints(215, 80, 120, -1));
        mainPanel.add(textFieldName, new AbsoluteConstraints(215, 105, 125, -1));
        panelPosition.setBorder(BorderFactory.createTitledBorder(this.translations.getString("VentanaLER.etiquetaGrupo")));
        panelPosition.setLayout(new AbsoluteLayout());
        labelCoordinateX.setFont(new Font("Dialog", 0, 12));
        labelCoordinateX.setText(this.translations.getString("VentanaLER.X="));
        panelPosition.add(labelCoordinateX, new AbsoluteConstraints(100, 100, -1, -1));
        labelCoordinateY.setFont(new Font("Dialog", 0, 12));
        labelCoordinateY.setText(this.translations.getString("VentanaLER.Y="));
        panelPosition.add(labelCoordinateY, new AbsoluteConstraints(40, 100, -1, -1));
        coordinatesPanel.setBackground(new Color(255, 255, 255));
        coordinatesPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                clicEnPanelCoordenadas(evt);
            }

            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnPanelCoordenadas(evt);
            }

            public void mouseExited(MouseEvent evt) {
                ratonSaleDePanelCoordenadas(evt);
            }
        });
        panelPosition.add(coordinatesPanel, new AbsoluteConstraints(25, 25, 130, 70));
        mainPanel.add(panelPosition, new AbsoluteConstraints(15, 75, 180, 125));
        checkBoxShowName.setFont(new Font("Dialog", 0, 12));
        checkBoxShowName.setSelected(true);
        checkBoxShowName.setText(this.translations.getString("VentanaLER.verNombre"));
        mainPanel.add(checkBoxShowName, new AbsoluteConstraints(215, 135, -1, -1));
        panelTabs.addTab(this.translations.getString("VentanaLER.tabs.General"), mainPanel);
        panelQuickConfiguration.setLayout(new AbsoluteLayout());
        checkBoxQuickGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        checkBoxQuickGenerateStatistics.setText(this.translations.getString("VentanaLER.GenerarEstadisticas"));
        checkBoxQuickGenerateStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnGenerarEstadisticasSencillo(evt);
            }
        });
        panelQuickConfiguration.add(checkBoxQuickGenerateStatistics, new AbsoluteConstraints(70, 160, -1, -1));
        iconContainerEnd1.setIcon(imageBroker.getIcon(TImageBroker.ASISTENTE));
        iconContainerEnd1.setText(this.translations.getString("VentanaLER.ConfiguracionRapida"));
        panelQuickConfiguration.add(iconContainerEnd1, new AbsoluteConstraints(15, 20, 335, -1));
        labelLERFeatures.setFont(new Font("Dialog", 0, 12));
        labelLERFeatures.setHorizontalAlignment(SwingConstants.RIGHT);
        labelLERFeatures.setText(this.translations.getString("VentanaLER.CaracteristicasDelLER"));
        panelQuickConfiguration.add(labelLERFeatures, new AbsoluteConstraints(20, 110, 160, -1));
        comboBoxPredefinedOptions.setFont(new Font("Dialog", 0, 12));
        comboBoxPredefinedOptions.setModel(new DefaultComboBoxModel(new String[]{"Personalized", "Very low cost LER", "Low cost LER", "Medium cost LER", "Expensive LER", "Very expensive LER"}));
        comboBoxPredefinedOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                cliEnSelectorSencilloCaracteristicas(evt);
            }
        });
        panelQuickConfiguration.add(comboBoxPredefinedOptions, new AbsoluteConstraints(190, 110, -1, -1));
        panelTabs.addTab(this.translations.getString("VentanaLER.tabs.Fast"), panelQuickConfiguration);
        panelAdvancedConfiguration.setLayout(new AbsoluteLayout());
        checkBoxAdvancedGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        checkBoxAdvancedGenerateStatistics.setText(this.translations.getString("VentanaLER.GenerarEstadisticas"));
        checkBoxAdvancedGenerateStatistics.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clicEnGenerarEstadisticasAvanzada(evt);
            }
        });
        panelAdvancedConfiguration.add(checkBoxAdvancedGenerateStatistics, new AbsoluteConstraints(70, 160, -1, -1));
        iconContainerEnd2.setIcon(imageBroker.getIcon(TImageBroker.AVANZADA));
        iconContainerEnd2.setText(this.translations.getString("VentanaLER.ConfiguracionAvanzada"));
        panelAdvancedConfiguration.add(iconContainerEnd2, new AbsoluteConstraints(15, 20, 335, -1));
        labelRoutingPower.setFont(new Font("Dialog", 0, 12));
        labelRoutingPower.setHorizontalAlignment(SwingConstants.RIGHT);
        labelRoutingPower.setText(this.translations.getString("VentanaLER.PotenciaDeConmutacion"));
        panelAdvancedConfiguration.add(labelRoutingPower, new AbsoluteConstraints(10, 90, 140, -1));
        selectorRoutingPower.setMajorTickSpacing(1000);
        selectorRoutingPower.setMaximum(10240);
        selectorRoutingPower.setMinimum(1);
        selectorRoutingPower.setMinorTickSpacing(100);
        selectorRoutingPower.setValue(1);
        selectorRoutingPower.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                selectorDePotenciadeConmutacionCambiado(evt);
            }
        });
        panelAdvancedConfiguration.add(selectorRoutingPower, new AbsoluteConstraints(155, 90, 130, 20));
        labelRoutingPowerMbps.setFont(new Font("Dialog", 0, 10));
        labelRoutingPowerMbps.setForeground(new Color(102, 102, 102));
        labelRoutingPowerMbps.setHorizontalAlignment(SwingConstants.LEFT);
        labelRoutingPowerMbps.setText(this.translations.getString("VentanaLER.1_Mbps"));
        panelAdvancedConfiguration.add(labelRoutingPowerMbps, new AbsoluteConstraints(290, 90, 70, 20));
        labelBufferSize.setFont(new Font("Dialog", 0, 12));
        labelBufferSize.setHorizontalAlignment(SwingConstants.RIGHT);
        labelBufferSize.setText(this.translations.getString("VentanaLER.TamanioDelBufferDeEntrada"));
        panelAdvancedConfiguration.add(labelBufferSize, new AbsoluteConstraints(10, 120, 180, -1));
        selectorBufferSize.setMajorTickSpacing(50);
        selectorBufferSize.setMaximum(1024);
        selectorBufferSize.setMinimum(1);
        selectorBufferSize.setMinorTickSpacing(100);
        selectorBufferSize.setValue(1);
        selectorBufferSize.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                selectorDeTamanioBufferCambiado(evt);
            }
        });
        panelAdvancedConfiguration.add(selectorBufferSize, new AbsoluteConstraints(200, 120, 100, 20));
        labelBufferSizeMB.setFont(new Font("Dialog", 0, 10));
        labelBufferSizeMB.setForeground(new Color(102, 102, 102));
        labelBufferSizeMB.setHorizontalAlignment(SwingConstants.LEFT);
        labelBufferSizeMB.setText(this.translations.getString("VentanaLER.1_MB"));
        panelAdvancedConfiguration.add(labelBufferSizeMB, new AbsoluteConstraints(300, 120, 60, 20));
        panelTabs.addTab(this.translations.getString("VentanaLER.tabs.Advanced"), panelAdvancedConfiguration);
        panelPrincipal.add(panelTabs, new AbsoluteConstraints(15, 15, 370, 240));
        panelButtons.setLayout(new AbsoluteLayout());
        buttonOK.setFont(new Font("Dialog", 0, 12));
        buttonOK.setIcon(imageBroker.getIcon(TImageBroker.ACEPTAR));
        buttonOK.setMnemonic(this.translations.getString("VentanaLER.botones.mne.Aceptar").charAt(0));
        buttonOK.setText(this.translations.getString("VentanaLER.boton.Ok"));
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clicEnAceptar(evt);
            }
        });
        panelButtons.add(buttonOK, new AbsoluteConstraints(15, 15, 115, -1));
        buttonCancel.setFont(new Font("Dialog", 0, 12));
        buttonCancel.setIcon(imageBroker.getIcon(TImageBroker.CANCELAR));
        buttonCancel.setMnemonic(this.translations.getString("VentanaLER.botones.mne.Cancelar").charAt(0));
        buttonCancel.setText(this.translations.getString("VentanaLER.boton.Cancel"));
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clicEnCancelar(evt);
            }
        });
        panelButtons.add(buttonCancel, new AbsoluteConstraints(140, 15, 115, -1));
        panelPrincipal.add(panelButtons, new AbsoluteConstraints(0, 255, 400, 55));
        getContentPane().add(panelPrincipal, new AbsoluteConstraints(0, 0, -1, 310));
        pack();
    }

    private void initComponents2() {
        coordinatesPanel.setDesignPanel(designPanel);
        Dimension frameSize = this.getSize();
        Dimension parentSize = parent.getSize();
        setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);
        lerNode = null;
        labelCoordinateX.setText(this.translations.getString("JVentanaLER.X=") + coordinatesPanel.getRealX());
        labelCoordinateY.setText(this.translations.getString("JVentanaLER.Y=") + coordinatesPanel.getRealY());
        currentConfigShowName = true;
        currentConfigName = "";
        currentConfigRoutingPower = 0;
        currentConfigBufferSize = 0;
        reconguration = false;
        currentConfigGenerateStatistics = false;
        this.comboBoxPredefinedOptions.removeAllItems();
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Personalized_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Very_low_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Low_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Medium_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("High_range_LER"));
        this.comboBoxPredefinedOptions.addItem(this.translations.getString("JVentanaLER.Very_high_range_LER"));
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
    }

    private void cliEnSelectorSencilloCaracteristicas(ActionEvent evt) {
        int opcionSeleccionada = this.comboBoxPredefinedOptions.getSelectedIndex();
        if (opcionSeleccionada == 0) {
            // No se hace nada
            this.comboBoxPredefinedOptions.setSelectedIndex(0);
        } else if (opcionSeleccionada == 1) {
            this.selectorRoutingPower.setValue(1);
            this.selectorBufferSize.setValue(1);
            this.comboBoxPredefinedOptions.setSelectedIndex(1);
        } else if (opcionSeleccionada == 2) {
            this.selectorRoutingPower.setValue(2560);
            this.selectorBufferSize.setValue(256);
            this.comboBoxPredefinedOptions.setSelectedIndex(2);
        } else if (opcionSeleccionada == 3) {
            this.selectorRoutingPower.setValue(5120);
            this.selectorBufferSize.setValue(512);
            this.comboBoxPredefinedOptions.setSelectedIndex(3);
        } else if (opcionSeleccionada == 4) {
            this.selectorRoutingPower.setValue(7680);
            this.selectorBufferSize.setValue(768);
            this.comboBoxPredefinedOptions.setSelectedIndex(4);
        } else if (opcionSeleccionada == 5) {
            this.selectorRoutingPower.setValue(10240);
            this.selectorBufferSize.setValue(1024);
            this.comboBoxPredefinedOptions.setSelectedIndex(5);
        }
    }

    private void selectorDeTamanioBufferCambiado(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.labelBufferSizeMB.setText(this.selectorBufferSize.getValue() + " " + this.translations.getString("VentanaLER.MB"));
    }

    private void selectorDePotenciadeConmutacionCambiado(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.labelRoutingPowerMbps.setText(this.selectorRoutingPower.getValue() + " " + this.translations.getString("VentanaLER.Mbps."));
    }

    private void clicEnGenerarEstadisticasAvanzada(ActionEvent evt) {
        this.checkBoxQuickGenerateStatistics.setSelected(this.checkBoxAdvancedGenerateStatistics.isSelected());
    }

    private void clicEnGenerarEstadisticasSencillo(ActionEvent evt) {
        this.checkBoxAdvancedGenerateStatistics.setSelected(this.checkBoxQuickGenerateStatistics.isSelected());
    }

    private void clicEnCancelar(ActionEvent evt) {
        if (reconguration) {
            lerNode.setShowName(currentConfigShowName);
            lerNode.setName(currentConfigName);
            lerNode.setWellConfigured(true);
            lerNode.setBufferSizeInMBytes(currentConfigBufferSize);
            lerNode.setRoutingPowerInMbps(currentConfigRoutingPower);
            lerNode.setGenerateStats(currentConfigGenerateStatistics);
            reconguration = false;
        } else {
            lerNode.setWellConfigured(false);
        }
        this.setVisible(false);
        this.dispose();
    }

    private void clicEnAceptar(ActionEvent evt) {
        lerNode.setWellConfigured(true);
        if (!this.reconguration) {
            lerNode.setScreenPosition(new Point(coordinatesPanel.getRealX(), coordinatesPanel.getRealY()));
        }
        lerNode.setBufferSizeInMBytes(this.selectorBufferSize.getValue());
        lerNode.setRoutingPowerInMbps(this.selectorRoutingPower.getValue());
        lerNode.setGenerateStats(this.checkBoxQuickGenerateStatistics.isSelected());
        lerNode.setName(textFieldName.getText());
        lerNode.setShowName(checkBoxShowName.isSelected());
        lerNode.setGenerateStats(this.checkBoxQuickGenerateStatistics.isSelected());
        int error = lerNode.validateConfig(topology, this.reconguration);
        if (error != TLERNode.OK) {
            JWarningWindow va = new JWarningWindow(parent, true, imageBroker);
            va.setWarningMessage(lerNode.getErrorMessage(error));
            va.show();
        } else {
            this.setVisible(false);
            this.dispose();
        }
    }

    private void clicEnPanelCoordenadas(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            coordinatesPanel.setCoordinates(evt.getPoint());
            labelCoordinateX.setText(this.translations.getString("VentanaconfigLER.X=_") + coordinatesPanel.getRealX());
            labelCoordinateY.setText(this.translations.getString("VentanaconfigLER.Y=_") + coordinatesPanel.getRealY());
            coordinatesPanel.repaint();
        }
    }

    private void ratonSaleDePanelCoordenadas(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void ratonEntraEnPanelCoordenadas(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void closeDialog(WindowEvent evt) {
        setVisible(false);
        lerNode.setWellConfigured(false);
        dispose();
    }

    public void ponerConfiguracion(TLERNode tnler, boolean recfg) {
        lerNode = tnler;
        reconguration = recfg;
        if (reconguration) {
            this.coordinatesPanel.setEnabled(false);
            this.coordinatesPanel.setToolTipText(null);
            currentConfigGenerateStatistics = tnler.isGeneratingStats();
            currentConfigShowName = tnler.nameMustBeDisplayed();
            currentConfigName = tnler.getName();
            currentConfigRoutingPower = tnler.getRoutingPowerInMbps();
            currentConfigBufferSize = tnler.getBufferSizeInMBytes();
            this.checkBoxAdvancedGenerateStatistics.setSelected(currentConfigGenerateStatistics);
            this.checkBoxQuickGenerateStatistics.setSelected(currentConfigGenerateStatistics);
            this.selectorRoutingPower.setValue(currentConfigRoutingPower);
            this.selectorBufferSize.setValue(currentConfigBufferSize);
            this.textFieldName.setText(currentConfigName);
            this.checkBoxShowName.setSelected(currentConfigShowName);
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
    private JLabel iconContainerEnd1;
    private JLabel iconContainerEnd2;
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
    private JSlider selectorRoutingPower;
    private JSlider selectorBufferSize;
    private JComboBox comboBoxPredefinedOptions;
    private JCheckBox checkBoxShowName;
    private ResourceBundle translations;
}
