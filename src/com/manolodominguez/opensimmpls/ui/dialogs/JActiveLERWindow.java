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

import com.manolodominguez.opensimmpls.scenario.TActiveLERNode;
import com.manolodominguez.opensimmpls.scenario.TTopology;
import com.manolodominguez.opensimmpls.ui.simulator.JDesignPanel;
import com.manolodominguez.opensimmpls.ui.utils.TImagesBroker;
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
 * Esta clase implementa una ventana que nos permite configurar un nodo LER
 *
 * @author Administrador<B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JActiveLERWindow extends JDialog {

    /**
     * Este m�todo crea una nueva instancia de JVentanaconfigLERA
     *
     * @param topology Topolog�a dentro del a cual se encuentra insertado el LER
     * que queremos configurar.
     * @param designPanel Panel de dise�o dentro del cual estamos dise�ando el
     * LER que queremos configurar.
     * @param imageBroker Dispensador de im�genes global de la aplicaci�n.
     * @param parent Ventana padre dentro de la cual se ubicar� esta
     * ventana de tipo JVentanaLER
     * @param modal TRUE indica que la ventana impedir� que se pueda seleccionar
     * nada de la interfaz de usuario hasta que se cierre. FALSE indica que esto
     * no es asi.
     * @since 2.0
     */
    public JActiveLERWindow(TTopology topology, JDesignPanel designPanel, TImagesBroker imageBroker, Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.imageBroker = imageBroker;
        this.designPanel = designPanel;
        this.topology = topology;
        initComponents();
        initComponents2();
    }

    /**
     * Este m�todo terminar� de configurar aspectos de la clase que no han
     * podido ser definidos en el constructor.
     *
     * @since 2.0
     */
    private void initComponents2() {
        this.coordinatesPanel.setDesignPanel(this.designPanel);
        Dimension frameSize = this.getSize();
        Dimension parentSize = this.parent.getSize();
        setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);
        this.activeLERNode = null;
        this.labelCoordinateX.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.X=") + coordinatesPanel.getRealX());
        this.labelCoordinateY.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLER.Y=") + coordinatesPanel.getRealY());
        this.currentConfigShowName = true;
        this.currentConfigName = "";
        this.currentConfigRoutingPower = 0;
        this.currentConfigBufferSize = 0;
        this.reconguration = false;
        this.currentConfigGenerateStatistics = false;
        this.comboBoxPredefinedOptions.removeAllItems();
        this.comboBoxPredefinedOptions.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLERA.Personalized_LERA"));
        this.comboBoxPredefinedOptions.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLERA.Very_low_range_LERA"));
        this.comboBoxPredefinedOptions.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLERA.Low_range_LERA"));
        this.comboBoxPredefinedOptions.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLERA.Medium_range_LERA"));
        this.comboBoxPredefinedOptions.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLERA.High_range_LERA"));
        this.comboBoxPredefinedOptions.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLERA.Very_high_range_LERA"));
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {
        this.mainPanel = new JPanel();
        this.panelTabs = new JTabbedPane();
        this.panelGeneralConfiguration = new JPanel();
        this.iconContainerActiveLER = new JLabel();
        this.labelName = new JLabel();
        this.textFieldName = new JTextField();
        this.panelPosition = new JPanel();
        this.labelCoordinateX = new JLabel();
        this.labelCoordinateY = new JLabel();
        this.coordinatesPanel = new JCoordinatesPanel();
        this.checkBoxShowName = new JCheckBox();
        this.panelQuickConfiguration = new JPanel();
        this.checkBoxQuickGenerateStatistics = new JCheckBox();
        this.iconContainerEnd1 = new JLabel();
        this.labelActiveLERFeatures = new JLabel();
        this.comboBoxPredefinedOptions = new JComboBox();
        this.panelAdvancedConfiguration = new JPanel();
        this.checkBoxAdvancedGenerateStatistics = new JCheckBox();
        this.iconContainerEnd2 = new JLabel();
        this.labelRoutingPower = new JLabel();
        this.selectorRoutingPower = new JSlider();
        this.labelRoutingPowerMbps = new JLabel();
        this.labelBufferSize = new JLabel();
        this.selectorBufferSize = new JSlider();
        this.labelBufferSizeMB = new JLabel();
        this.selectorDMGPSize = new JSlider();
        this.labelDMGPSizeKB = new JLabel();
        this.labelDMGPSize = new JLabel();
        this.panelButtons = new JPanel();
        this.buttonOK = new JButton();
        this.buttonCancel = new JButton();
        ResourceBundle bundle = ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations");
        setTitle(bundle.getString("VentanaLERA.titulo"));
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
        this.iconContainerActiveLER.setIcon(this.imageBroker.getIcon(TImagesBroker.LERA));
        this.iconContainerActiveLER.setText(bundle.getString("LERA.Descripcion"));
        this.panelGeneralConfiguration.add(this.iconContainerActiveLER, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelName.setFont(new Font("Dialog", 0, 12));
        this.labelName.setText(bundle.getString("VentanaLERA.etiquetaNombre"));
        this.panelGeneralConfiguration.add(this.labelName, new AbsoluteConstraints(215, 80, 120, -1));
        this.panelGeneralConfiguration.add(this.textFieldName, new AbsoluteConstraints(215, 105, 125, -1));
        this.panelPosition.setBorder(BorderFactory.createTitledBorder(bundle.getString("VentanaLER.etiquetaGrupo")));
        this.panelPosition.setLayout(new AbsoluteLayout());
        this.labelCoordinateX.setFont(new Font("Dialog", 0, 12));
        this.labelCoordinateX.setText(bundle.getString("VentanaLER.X="));
        this.panelPosition.add(this.labelCoordinateX, new AbsoluteConstraints(100, 100, -1, -1));
        this.labelCoordinateY.setFont(new Font("Dialog", 0, 12));
        this.labelCoordinateY.setText(bundle.getString("VentanaLER.Y="));
        this.panelPosition.add(this.labelCoordinateY, new AbsoluteConstraints(40, 100, -1, -1));
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
        this.panelGeneralConfiguration.add(this.panelPosition, new AbsoluteConstraints(15, 75, 180, 125));
        this.checkBoxShowName.setFont(new Font("Dialog", 0, 12));
        this.checkBoxShowName.setSelected(true);
        this.checkBoxShowName.setText(bundle.getString("VentanaLER.verNombre"));
        this.panelGeneralConfiguration.add(this.checkBoxShowName, new AbsoluteConstraints(215, 135, -1, -1));
        this.panelTabs.addTab(bundle.getString("VentanaLER.tabs.General"), this.panelGeneralConfiguration);
        this.panelQuickConfiguration.setLayout(new AbsoluteLayout());
        this.checkBoxQuickGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        this.checkBoxQuickGenerateStatistics.setText(bundle.getString("VentanaLERA.GenerarEstadisticas"));
        this.checkBoxQuickGenerateStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnQuickGenerateStatistics(evt);
            }
        });
        this.panelQuickConfiguration.add(this.checkBoxQuickGenerateStatistics, new AbsoluteConstraints(70, 160, -1, -1));
        this.iconContainerEnd1.setIcon(this.imageBroker.getIcon(TImagesBroker.ASISTENTE));
        this.iconContainerEnd1.setText(bundle.getString("VentanaLERA.ConfiguracionRapida"));
        this.panelQuickConfiguration.add(this.iconContainerEnd1, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelActiveLERFeatures.setFont(new Font("Dialog", 0, 12));
        this.labelActiveLERFeatures.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelActiveLERFeatures.setText(bundle.getString("VentanaLERA.CaracteristicasDelLER"));
        this.panelQuickConfiguration.add(this.labelActiveLERFeatures, new AbsoluteConstraints(20, 110, 160, -1));
        this.comboBoxPredefinedOptions.setFont(new Font("Dialog", 0, 12));
        this.comboBoxPredefinedOptions.setModel(new DefaultComboBoxModel(new String[]{"Personalized", "Very low cost LER", "Low cost LER", "Medium cost LER", "Expensive LER", "Very expensive LER"}));
        this.comboBoxPredefinedOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnPredefinedOptions(evt);
            }
        });
        this.panelQuickConfiguration.add(this.comboBoxPredefinedOptions, new AbsoluteConstraints(190, 110, -1, -1));
        this.panelTabs.addTab(bundle.getString("VentanaLER.tabs.Fast"), this.panelQuickConfiguration);
        this.panelAdvancedConfiguration.setLayout(new AbsoluteLayout());
        this.checkBoxAdvancedGenerateStatistics.setFont(new Font("Dialog", 0, 12));
        this.checkBoxAdvancedGenerateStatistics.setText(bundle.getString("VentanaLERA.GenerarEstadisticas"));
        this.checkBoxAdvancedGenerateStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnAdvancedGenerateStatistics(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.checkBoxAdvancedGenerateStatistics, new AbsoluteConstraints(70, 180, -1, -1));
        this.iconContainerEnd2.setIcon(this.imageBroker.getIcon(TImagesBroker.AVANZADA));
        this.iconContainerEnd2.setText(bundle.getString("VentanaLERA.ConfiguracionAvanzada"));
        this.panelAdvancedConfiguration.add(this.iconContainerEnd2, new AbsoluteConstraints(15, 20, 335, -1));
        this.labelRoutingPower.setFont(new Font("Dialog", 0, 12));
        this.labelRoutingPower.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelRoutingPower.setText(bundle.getString("VentanaLER.PotenciaDeConmutacion"));
        this.panelAdvancedConfiguration.add(this.labelRoutingPower, new AbsoluteConstraints(10, 90, 140, -1));
        this.selectorRoutingPower.setMajorTickSpacing(1000);
        this.selectorRoutingPower.setMaximum(10240);
        this.selectorRoutingPower.setMinimum(1);
        this.selectorRoutingPower.setMinorTickSpacing(100);
        this.selectorRoutingPower.setValue(1);
        this.selectorRoutingPower.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnRoutingPower(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.selectorRoutingPower, new AbsoluteConstraints(155, 90, 130, 20));
        this.labelRoutingPowerMbps.setFont(new Font("Dialog", 0, 10));
        this.labelRoutingPowerMbps.setForeground(new Color(102, 102, 102));
        this.labelRoutingPowerMbps.setHorizontalAlignment(SwingConstants.LEFT);
        this.labelRoutingPowerMbps.setText(bundle.getString("VentanaLER.1_Mbps"));
        this.panelAdvancedConfiguration.add(this.labelRoutingPowerMbps, new AbsoluteConstraints(290, 90, 70, 20));
        this.labelBufferSize.setFont(new java.awt.Font("Dialog", 0, 12));
        this.labelBufferSize.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelBufferSize.setText(bundle.getString("VentanaLER.TamanioDelBufferDeEntrada"));
        this.panelAdvancedConfiguration.add(this.labelBufferSize, new AbsoluteConstraints(10, 120, 180, -1));
        this.selectorBufferSize.setMajorTickSpacing(50);
        this.selectorBufferSize.setMaximum(1024);
        this.selectorBufferSize.setMinimum(1);
        this.selectorBufferSize.setMinorTickSpacing(100);
        this.selectorBufferSize.setValue(1);
        this.selectorBufferSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnBufferSize(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.selectorBufferSize, new AbsoluteConstraints(200, 120, 100, 20));
        this.labelBufferSizeMB.setFont(new Font("Dialog", 0, 10));
        this.labelBufferSizeMB.setForeground(new Color(102, 102, 102));
        this.labelBufferSizeMB.setHorizontalAlignment(SwingConstants.LEFT);
        this.labelBufferSizeMB.setText(bundle.getString("VentanaLER.1_MB"));
        this.panelAdvancedConfiguration.add(this.labelBufferSizeMB, new AbsoluteConstraints(300, 120, 60, 20));
        this.selectorDMGPSize.setMajorTickSpacing(50);
        this.selectorDMGPSize.setMaximum(102400);
        this.selectorDMGPSize.setMinimum(1);
        this.selectorDMGPSize.setMinorTickSpacing(100);
        this.selectorDMGPSize.setValue(1);
        this.selectorDMGPSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                handleChangeOnDMGPSize(evt);
            }
        });
        this.panelAdvancedConfiguration.add(this.selectorDMGPSize, new AbsoluteConstraints(170, 150, 120, 20));
        this.labelDMGPSizeKB.setFont(new Font("Dialog", 0, 10));
        this.labelDMGPSizeKB.setForeground(new Color(102, 102, 102));
        this.labelDMGPSizeKB.setHorizontalAlignment(SwingConstants.LEFT);
        this.labelDMGPSizeKB.setText(bundle.getString("JVentanaLERA.1KB"));
        this.panelAdvancedConfiguration.add(this.labelDMGPSizeKB, new AbsoluteConstraints(290, 150, 70, 20));
        this.labelDMGPSize.setFont(new Font("Dialog", 0, 12));
        this.labelDMGPSize.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelDMGPSize.setText(bundle.getString("JVentanaLERA.DMGP_size"));
        this.panelAdvancedConfiguration.add(this.labelDMGPSize, new AbsoluteConstraints(10, 150, 150, -1));
        this.panelTabs.addTab(bundle.getString("VentanaLER.tabs.Advanced"), this.panelAdvancedConfiguration);
        this.mainPanel.add(this.panelTabs, new AbsoluteConstraints(15, 15, 370, 240));
        this.panelButtons.setLayout(new AbsoluteLayout());
        this.buttonOK.setFont(new Font("Dialog", 0, 12));
        this.buttonOK.setIcon(this.imageBroker.getIcon(TImagesBroker.ACEPTAR));
        this.buttonOK.setMnemonic(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.botones.mne.Aceptar").charAt(0));
        this.buttonOK.setText(bundle.getString("VentanaLER.boton.Ok"));
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOKButton(evt);
            }
        });
        this.panelButtons.add(this.buttonOK, new AbsoluteConstraints(15, 15, 115, -1));
        this.buttonCancel.setFont(new Font("Dialog", 0, 12));
        this.buttonCancel.setIcon(this.imageBroker.getIcon(TImagesBroker.CANCELAR));
        this.buttonCancel.setMnemonic(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.botones.mne.Cancelar").charAt(0));
        this.buttonCancel.setText(bundle.getString("VentanaLER.boton.Cancel"));
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnCancelButton(evt);
            }
        });
        this.panelButtons.add(this.buttonCancel, new AbsoluteConstraints(140, 15, 115, -1));
        this.mainPanel.add(this.panelButtons, new AbsoluteConstraints(0, 260, 400, 50));
        getContentPane().add(this.mainPanel, new AbsoluteConstraints(0, 0, -1, 310));
        pack();
    }

    private void handleChangeOnDMGPSize(ChangeEvent evt) {
        this.labelDMGPSizeKB.setText("" + this.selectorDMGPSize.getValue() + " " + ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLERA._MB."));
    }

    private void handleClickOnPredefinedOptions(ActionEvent evt) {
        int selectedOption = this.comboBoxPredefinedOptions.getSelectedIndex();
        if (selectedOption == 0) {
            // Do nothing
            this.comboBoxPredefinedOptions.setSelectedIndex(0);
        } else if (selectedOption == 1) {
            this.selectorRoutingPower.setValue(1);
            this.selectorBufferSize.setValue(1);
            this.selectorDMGPSize.setValue(1);
            this.comboBoxPredefinedOptions.setSelectedIndex(1);
        } else if (selectedOption == 2) {
            this.selectorRoutingPower.setValue(2560);
            this.selectorBufferSize.setValue(256);
            this.selectorDMGPSize.setValue(2);
            this.comboBoxPredefinedOptions.setSelectedIndex(2);
        } else if (selectedOption == 3) {
            this.selectorRoutingPower.setValue(5120);
            this.selectorBufferSize.setValue(512);
            this.selectorDMGPSize.setValue(3);
            this.comboBoxPredefinedOptions.setSelectedIndex(3);
        } else if (selectedOption == 4) {
            this.selectorRoutingPower.setValue(7680);
            this.selectorBufferSize.setValue(768);
            this.selectorDMGPSize.setValue(4);
            this.comboBoxPredefinedOptions.setSelectedIndex(4);
        } else if (selectedOption == 5) {
            this.selectorRoutingPower.setValue(10240);
            this.selectorBufferSize.setValue(1024);
            this.selectorDMGPSize.setValue(5);
            this.comboBoxPredefinedOptions.setSelectedIndex(5);
        }
    }

    private void handleChangeOnBufferSize(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.labelBufferSizeMB.setText(this.selectorBufferSize.getValue() + " " + ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.MB"));
    }

    private void handleChangeOnRoutingPower(ChangeEvent evt) {
        this.comboBoxPredefinedOptions.setSelectedIndex(0);
        this.labelRoutingPowerMbps.setText(this.selectorRoutingPower.getValue() + " " + ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLER.Mbps."));
    }

    private void handleClickOnAdvancedGenerateStatistics(ActionEvent evt) {
        this.checkBoxQuickGenerateStatistics.setSelected(this.checkBoxAdvancedGenerateStatistics.isSelected());
    }

    private void handleClickOnQuickGenerateStatistics(ActionEvent evt) {
        this.checkBoxAdvancedGenerateStatistics.setSelected(this.checkBoxQuickGenerateStatistics.isSelected());
    }

    private void handleClickOnCancelButton(ActionEvent evt) {
        if (this.reconguration) {
            this.activeLERNode.setShowName(this.currentConfigShowName);
            this.activeLERNode.setName(this.currentConfigName);
            this.activeLERNode.setWellConfigured(true);
            this.activeLERNode.setBufferSizeInMBytes(this.currentConfigBufferSize);
            this.activeLERNode.setRoutingPowerInMbps(this.currentConfigRoutingPower);
            this.activeLERNode.setGenerateStats(this.currentConfigGenerateStatistics);
            this.activeLERNode.setDMGPSizeInKB(this.currentConfigDMGPSize);
            this.reconguration = false;
        } else {
            this.activeLERNode.setWellConfigured(false);
        }
        this.setVisible(false);
        this.dispose();
    }

    private void handleClickOnOKButton(ActionEvent evt) {
        this.activeLERNode.setWellConfigured(true);
        if (!this.reconguration) {
            this.activeLERNode.setScreenPosition(new Point(this.coordinatesPanel.getRealX(), this.coordinatesPanel.getRealY()));
        }
        this.activeLERNode.setDMGPSizeInKB(this.selectorDMGPSize.getValue());
        this.activeLERNode.setBufferSizeInMBytes(this.selectorBufferSize.getValue());
        this.activeLERNode.setRoutingPowerInMbps(this.selectorRoutingPower.getValue());
        this.activeLERNode.setGenerateStats(this.checkBoxQuickGenerateStatistics.isSelected());
        this.activeLERNode.setName(this.textFieldName.getText());
        this.activeLERNode.setShowName(this.checkBoxShowName.isSelected());
        this.activeLERNode.setGenerateStats(this.checkBoxQuickGenerateStatistics.isSelected());
        int error = this.activeLERNode.validateConfig(this.topology, this.reconguration);
        if (error != TActiveLERNode.OK) {
            JWarningWindow va = new JWarningWindow(this.parent, true, this.imageBroker);
            va.mostrarMensaje(this.activeLERNode.getErrorMessage(error));
            va.show();
        } else {
            this.setVisible(false);
            this.dispose();
        }
    }

    private void handleClickOnCoordinatesPanel(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            this.coordinatesPanel.setCoordinates(evt.getPoint());
            this.labelCoordinateX.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaconfigLERA.X=_") + coordinatesPanel.getRealX());
            this.labelCoordinateY.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaconfigLERA.Y=_") + coordinatesPanel.getRealY());
            this.coordinatesPanel.repaint();
        }
    }

    private void handleMouseLeavingCoordinatesPanel(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void handleMouseEnteringInCoordinatesPanel(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * Closes the dialog
     */
    private void handleWindowsClosing(WindowEvent evt) {
        setVisible(false);
        this.activeLERNode.setWellConfigured(false);
        dispose();
    }

    /**
     * Este m�todo permite cargar en la ventana la configuraci�n actual del LER.
     *
     * @since 2.0
     * @param activeLERNode El nodo LER que queremos configurar.
     * @param reconfiguration TRUE indica que estamos reconfigurando el LER.
     * FALSE indica que el LER se ha insertado nuevo en la topolog�a.
     */
    public void setConfiguration(TActiveLERNode activeLERNode, boolean reconfiguration) {
        this.activeLERNode = activeLERNode;
        this.reconguration = reconfiguration;
        if (this.reconguration) {
            this.coordinatesPanel.setEnabled(false);
            this.coordinatesPanel.setToolTipText(null);
            this.currentConfigGenerateStatistics = activeLERNode.isGeneratingStats();
            this.currentConfigShowName = activeLERNode.nameMustBeDisplayed();
            this.currentConfigName = activeLERNode.getName();
            this.currentConfigRoutingPower = activeLERNode.getRoutingPowerInMbps();
            this.currentConfigBufferSize = activeLERNode.getBufferSizeInMBytes();
            this.currentConfigDMGPSize = activeLERNode.getDMGPSizeInKB();
            this.selectorDMGPSize.setValue(this.currentConfigDMGPSize);
            this.checkBoxAdvancedGenerateStatistics.setSelected(this.currentConfigGenerateStatistics);
            this.checkBoxQuickGenerateStatistics.setSelected(this.currentConfigGenerateStatistics);
            this.selectorRoutingPower.setValue(this.currentConfigRoutingPower);
            this.selectorBufferSize.setValue(this.currentConfigBufferSize);
            this.textFieldName.setText(this.currentConfigName);
            this.checkBoxShowName.setSelected(this.currentConfigShowName);
        }
    }

    private TImagesBroker imageBroker;
    private Frame parent;
    private JDesignPanel designPanel;
    private TActiveLERNode activeLERNode;
    private TTopology topology;
    private boolean currentConfigShowName;
    private String currentConfigName;
    private int currentConfigRoutingPower;
    private int currentConfigBufferSize;
    private boolean currentConfigGenerateStatistics;
    private int currentConfigDMGPSize;
    private boolean reconguration;
    private JLabel labelCoordinateX;
    private JLabel labelCoordinateY;
    private JLabel labelBufferSizeMB;
    private JLabel labelDMGPSizeKB;
    private JLabel labelName;
    private JLabel labelRoutingPowerMbps;
    private JLabel iconContainerEnd1;
    private JLabel iconContainerEnd2;
    private JLabel iconContainerActiveLER;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel labelActiveLERFeatures;
    private JLabel labelRoutingPower;
    private JLabel labelBufferSize;
    private JLabel labelDMGPSize;
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
    private JSlider selectorRoutingPower;
    private JSlider selectorBufferSize;
    private JSlider selectorDMGPSize;
    private JComboBox comboBoxPredefinedOptions;
    private JCheckBox checkBoxShowName;
}
