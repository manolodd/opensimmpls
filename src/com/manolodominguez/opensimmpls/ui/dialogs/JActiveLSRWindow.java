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

import com.manolodominguez.opensimmpls.scenario.TActiveLSRNode;
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
 * Esta clase implementa una ventana que permite configurar un nodo LSR.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JActiveLSRWindow extends javax.swing.JDialog {

    /**
     * Crea una nueva instancia de JVentanaLSR
     *
     * @param t Topolog�a dentro de la cual se encuentra el nodo LSR que
     * queremos configurar.
     * @param pad Panel de dise�o donde estamos dise�ando el LSR que queremos
     * configurar.
     * @param di Dispensador de im�genes global del a aplicaci�n.
     * @param parent Ventana padre dentro de la cual se ubicar� esta ventana de
     * tipo JVentanaLSR.
     * @param modal TRUE indica que esta ventana impedira que se pueda
     * seleccionar cualquier otra zona de la interfaz hasta que se haya cerrado.
     * FALSE significa que esto no es as�.
     * @since 2.0
     */
    public JActiveLSRWindow(TTopology t, JDesignPanel pad, TImagesBroker di, Frame parent, boolean modal) {
        super(parent, modal);
        ventanaPadre = parent;
        dispensadorDeImagenes = di;
        pd = pad;
        topo = t;
        initComponents();
        initComponents2();
    }

    /**
     * Este m�todo configura aspectos de la calse que no han podido ser
     * configurados por el constructor.
     *
     * @since 2.0
     */
    private void initComponents2() {
        panelCoordenadas.setDesignPanel(pd);
        Dimension tamFrame = this.getSize();
        Dimension tamPadre = ventanaPadre.getSize();
        setLocation((tamPadre.width - tamFrame.width) / 2, (tamPadre.height - tamFrame.height) / 2);
        configLSRA = null;
        coordenadaX.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaEmisor.X=_") + panelCoordenadas.getRealX());
        coordenadaY.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaEmisor.Y=_") + panelCoordenadas.getRealY());
        BKUPMostrarNombre = true;
        BKUPNombre = "";
        BKUPPotencia = 0;
        BKUPTamBuffer = 0;
        BKUPGenerarEstadisticas = false;
        reconfigurando = false;
        this.selectorSencilloCaracteristicas.removeAllItems();
        this.selectorSencilloCaracteristicas.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLSRA.Personalized_LSRA"));
        this.selectorSencilloCaracteristicas.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLSRA.Very_low_range_LSRA"));
        this.selectorSencilloCaracteristicas.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLSRA.Low_range_LSRA"));
        this.selectorSencilloCaracteristicas.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLSRA.Medium_range_LSRA"));
        this.selectorSencilloCaracteristicas.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLSRA.High_range_LSRA"));
        this.selectorSencilloCaracteristicas.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLSRA.Very_high_range_LSRA"));
        this.selectorSencilloCaracteristicas.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new JPanel();
        panelPestanias = new JTabbedPane();
        panelGeneral = new JPanel();
        iconoReceptor = new JLabel();
        etiquetaNombre = new JLabel();
        nombreNodo = new JTextField();
        panelPosicion = new JPanel();
        coordenadaX = new JLabel();
        coordenadaY = new JLabel();
        panelCoordenadas = new JCoordinatesPanel();
        verNombre = new JCheckBox();
        panelRapido = new JPanel();
        iconoEnlace1 = new JLabel();
        selectorDeGenerarEstadisticasSencillo = new JCheckBox();
        jLabel1 = new JLabel();
        selectorSencilloCaracteristicas = new JComboBox();
        panelAvanzado = new JPanel();
        iconoEnlace2 = new JLabel();
        selectorDeGenerarEstadisticasAvanzado = new JCheckBox();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        selectorDePotenciaDeConmutacion = new JSlider();
        selectorDeTamanioBuffer = new JSlider();
        etiquetaPotencia = new JLabel();
        etiquetaMemoriaBuffer = new JLabel();
        jLabel4 = new JLabel();
        selectorDeTamanioDMGP = new JSlider();
        etiquetaMemoriaDMGP = new JLabel();
        panelBotones = new JPanel();
        jButton2 = new JButton();
        jButton3 = new JButton();
        ResourceBundle bundle = ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations");
        setTitle(bundle.getString("VentanaLSRA.titulo"));
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
        panelPestanias.setFont(new Font("Dialog", 0, 12));
        panelGeneral.setLayout(new AbsoluteLayout());
        iconoReceptor.setIcon(dispensadorDeImagenes.getIcon(TImagesBroker.LSRA));
        iconoReceptor.setText(bundle.getString("VentanaLSRA.descripcion"));
        panelGeneral.add(iconoReceptor, new AbsoluteConstraints(15, 20, 335, -1));
        etiquetaNombre.setFont(new Font("Dialog", 0, 12));
        etiquetaNombre.setText(bundle.getString("VentanaLSRA.etiquetaNombre"));
        panelGeneral.add(etiquetaNombre, new AbsoluteConstraints(215, 80, 120, -1));
        panelGeneral.add(nombreNodo, new AbsoluteConstraints(215, 105, 125, -1));
        panelPosicion.setBorder(BorderFactory.createTitledBorder(bundle.getString("VentanaLSR.titulogrupo")));
        panelPosicion.setLayout(new AbsoluteLayout());
        coordenadaX.setFont(new Font("Dialog", 0, 12));
        coordenadaX.setText(bundle.getString("VentanaLSR.X="));
        panelPosicion.add(coordenadaX, new AbsoluteConstraints(100, 100, -1, -1));
        coordenadaY.setFont(new Font("Dialog", 0, 12));
        coordenadaY.setText(bundle.getString("VentanaLSR.Y="));
        panelPosicion.add(coordenadaY, new AbsoluteConstraints(40, 100, -1, -1));
        panelCoordenadas.setBackground(new Color(255, 255, 255));
        panelCoordenadas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                clicEnPanelCoordenadas(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
                ratonEntraEnPanelCoordenadas(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                ratonSaleDePanelCoordenadas(evt);
            }
        });
        panelPosicion.add(panelCoordenadas, new AbsoluteConstraints(25, 25, 130, 70));
        panelGeneral.add(panelPosicion, new AbsoluteConstraints(15, 75, 180, 125));
        verNombre.setFont(new Font("Dialog", 0, 12));
        verNombre.setSelected(true);
        verNombre.setText(bundle.getString("VentanaLSR.verNombre"));
        panelGeneral.add(verNombre, new AbsoluteConstraints(215, 135, -1, -1));
        panelPestanias.addTab(bundle.getString("VentanaLSR.tabs.General"), panelGeneral);
        panelRapido.setLayout(new AbsoluteLayout());
        iconoEnlace1.setIcon(dispensadorDeImagenes.getIcon(TImagesBroker.ASISTENTE));
        iconoEnlace1.setText(bundle.getString("VentanaLSRA.ConfiguracionSencilla"));
        panelRapido.add(iconoEnlace1, new AbsoluteConstraints(15, 20, 335, -1));
        selectorDeGenerarEstadisticasSencillo.setFont(new Font("Dialog", 0, 12));
        selectorDeGenerarEstadisticasSencillo.setText(bundle.getString("VentanaLSRA.GenerarEstadisticas"));
        selectorDeGenerarEstadisticasSencillo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnSelectorDeGenerarEstadisticasSencillo(evt);
            }
        });
        panelRapido.add(selectorDeGenerarEstadisticasSencillo, new AbsoluteConstraints(70, 160, -1, -1));
        jLabel1.setFont(new Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText(bundle.getString("VentanaLSRA.Caracteristicas"));
        panelRapido.add(jLabel1, new AbsoluteConstraints(20, 110, 160, -1));
        selectorSencilloCaracteristicas.setFont(new Font("Dialog", 0, 12));
        //FIX: Apply I18N in the next line.
        selectorSencilloCaracteristicas.setModel(new DefaultComboBoxModel(new String[]{"Personalized", "Very low cost LSR", "Low cost LSR", "Medium cost LSR", "Expensive LSR", "Very expensive LSR"}));
        selectorSencilloCaracteristicas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnSelectorSencilloCaracteristicas(evt);
            }
        });
        panelRapido.add(selectorSencilloCaracteristicas, new AbsoluteConstraints(190, 110, -1, -1));
        panelPestanias.addTab(bundle.getString("VentanaLSR.tabs.Fast"), panelRapido);
        panelAvanzado.setLayout(new AbsoluteLayout());
        iconoEnlace2.setIcon(dispensadorDeImagenes.getIcon(TImagesBroker.AVANZADA));
        iconoEnlace2.setText(bundle.getString("VentanaLSRA.ConfiguracionAvanzada"));
        panelAvanzado.add(iconoEnlace2, new AbsoluteConstraints(15, 20, 335, -1));
        selectorDeGenerarEstadisticasAvanzado.setFont(new Font("Dialog", 0, 12));
        selectorDeGenerarEstadisticasAvanzado.setText(bundle.getString("VentanaLSRA.GenerarEstadisticas"));
        selectorDeGenerarEstadisticasAvanzado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnSelectorDeGenerarEstadisticasAvanzado(evt);
            }
        });
        panelAvanzado.add(selectorDeGenerarEstadisticasAvanzado, new AbsoluteConstraints(70, 180, -1, -1));
        jLabel2.setFont(new Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText(bundle.getString("VentanaLSR.PotenciaConmutacion"));
        panelAvanzado.add(jLabel2, new AbsoluteConstraints(10, 90, 140, -1));
        jLabel3.setFont(new Font("Dialog", 0, 12));
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel3.setText(bundle.getString("VentanaLSR.TamanioBufferEntrada"));
        panelAvanzado.add(jLabel3, new AbsoluteConstraints(10, 120, 180, -1));
        selectorDePotenciaDeConmutacion.setMajorTickSpacing(1000);
        selectorDePotenciaDeConmutacion.setMaximum(10240);
        selectorDePotenciaDeConmutacion.setMinimum(1);
        selectorDePotenciaDeConmutacion.setMinorTickSpacing(100);
        selectorDePotenciaDeConmutacion.setValue(1);
        selectorDePotenciaDeConmutacion.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                selectorDePotenciaDeConmutacionCambiado(evt);
            }
        });
        panelAvanzado.add(selectorDePotenciaDeConmutacion, new AbsoluteConstraints(155, 90, 130, 20));
        selectorDeTamanioBuffer.setMajorTickSpacing(50);
        selectorDeTamanioBuffer.setMaximum(1024);
        selectorDeTamanioBuffer.setMinimum(1);
        selectorDeTamanioBuffer.setMinorTickSpacing(100);
        selectorDeTamanioBuffer.setValue(1);
        selectorDeTamanioBuffer.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                selectorDeTamanioBufferCambiado(evt);
            }
        });
        panelAvanzado.add(selectorDeTamanioBuffer, new AbsoluteConstraints(200, 120, 100, 20));
        etiquetaPotencia.setFont(new Font("Dialog", 0, 10));
        etiquetaPotencia.setForeground(new Color(102, 102, 102));
        etiquetaPotencia.setHorizontalAlignment(SwingConstants.LEFT);
        etiquetaPotencia.setText(bundle.getString("VentanaLSR.1_Mbps"));
        panelAvanzado.add(etiquetaPotencia, new AbsoluteConstraints(290, 90, 70, 20));
        etiquetaMemoriaBuffer.setFont(new Font("Dialog", 0, 10));
        etiquetaMemoriaBuffer.setForeground(new Color(102, 102, 102));
        etiquetaMemoriaBuffer.setHorizontalAlignment(SwingConstants.LEFT);
        etiquetaMemoriaBuffer.setText(bundle.getString("VentanaLSR.1_MB"));
        panelAvanzado.add(etiquetaMemoriaBuffer, new AbsoluteConstraints(300, 120, 60, 20));
        jLabel4.setFont(new Font("Dialog", 0, 12));
        jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel4.setText(bundle.getString("JVentanaLSRA.DMGP_size"));
        panelAvanzado.add(jLabel4, new AbsoluteConstraints(10, 150, 160, -1));
        selectorDeTamanioDMGP.setMajorTickSpacing(50);
        selectorDeTamanioDMGP.setMaximum(102400);
        selectorDeTamanioDMGP.setMinimum(1);
        selectorDeTamanioDMGP.setMinorTickSpacing(100);
        selectorDeTamanioDMGP.setValue(1);
        selectorDeTamanioDMGP.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                selectorDeTamanioDMGPCambiado(evt);
            }
        });
        panelAvanzado.add(selectorDeTamanioDMGP, new AbsoluteConstraints(180, 150, 120, 20));
        etiquetaMemoriaDMGP.setFont(new Font("Dialog", 0, 10));
        etiquetaMemoriaDMGP.setForeground(new Color(102, 102, 102));
        etiquetaMemoriaDMGP.setHorizontalAlignment(SwingConstants.LEFT);
        etiquetaMemoriaDMGP.setText(bundle.getString("JVentanaLSRA.1_KB"));
        panelAvanzado.add(etiquetaMemoriaDMGP, new AbsoluteConstraints(300, 150, 60, 20));
        panelPestanias.addTab(bundle.getString("VentanaLSR.tabs.Advanced"), panelAvanzado);
        panelPrincipal.add(panelPestanias, new AbsoluteConstraints(15, 15, 370, 240));
        panelBotones.setLayout(new AbsoluteLayout());
        jButton2.setFont(new Font("Dialog", 0, 12));
        jButton2.setIcon(dispensadorDeImagenes.getIcon(TImagesBroker.ACEPTAR));
        jButton2.setMnemonic(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLSR.botones.mne.Aceptar").charAt(0));
        jButton2.setText(bundle.getString("VentanaLSR.boton.Ok"));
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnAceptar(evt);
            }
        });
        panelBotones.add(jButton2, new AbsoluteConstraints(20, 10, 105, -1));
        jButton3.setFont(new Font("Dialog", 0, 12));
        jButton3.setIcon(dispensadorDeImagenes.getIcon(TImagesBroker.CANCELAR));
        jButton3.setMnemonic(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLSR.botones.mne.Cancelar").charAt(0));
        jButton3.setText(bundle.getString("VentanaLSR.boton.Cancel"));
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnCancelar(evt);
            }
        });
        panelBotones.add(jButton3, new AbsoluteConstraints(140, 10, 105, -1));
        panelPrincipal.add(panelBotones, new AbsoluteConstraints(0, 260, 400, 60));
        getContentPane().add(panelPrincipal, new AbsoluteConstraints(0, 0, -1, 310));
        pack();
    }

    private void selectorDeTamanioDMGPCambiado(ChangeEvent evt) {
        this.etiquetaMemoriaDMGP.setText("" + this.selectorDeTamanioDMGP.getValue() + " " + ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaLSRA._MB."));
    }

    private void clicEnSelectorSencilloCaracteristicas(ActionEvent evt) {
        int opcionSeleccionada = this.selectorSencilloCaracteristicas.getSelectedIndex();
        if (opcionSeleccionada == 0) {
        } else if (opcionSeleccionada == 0) {
            this.selectorSencilloCaracteristicas.setSelectedIndex(0);
        } else if (opcionSeleccionada == 1) {
            this.selectorDePotenciaDeConmutacion.setValue(1);
            this.selectorDeTamanioBuffer.setValue(1);
            this.selectorDeTamanioDMGP.setValue(1);
            this.selectorSencilloCaracteristicas.setSelectedIndex(1);
        } else if (opcionSeleccionada == 2) {
            this.selectorDePotenciaDeConmutacion.setValue(2560);
            this.selectorDeTamanioBuffer.setValue(256);
            this.selectorDeTamanioDMGP.setValue(2);
            this.selectorSencilloCaracteristicas.setSelectedIndex(2);
        } else if (opcionSeleccionada == 3) {
            this.selectorDePotenciaDeConmutacion.setValue(5120);
            this.selectorDeTamanioBuffer.setValue(512);
            this.selectorDeTamanioDMGP.setValue(3);
            this.selectorSencilloCaracteristicas.setSelectedIndex(3);
        } else if (opcionSeleccionada == 4) {
            this.selectorDePotenciaDeConmutacion.setValue(7680);
            this.selectorDeTamanioDMGP.setValue(4);
            this.selectorDeTamanioBuffer.setValue(768);
            this.selectorSencilloCaracteristicas.setSelectedIndex(4);
        } else if (opcionSeleccionada == 5) {
            this.selectorDePotenciaDeConmutacion.setValue(10240);
            this.selectorDeTamanioBuffer.setValue(1024);
            this.selectorDeTamanioDMGP.setValue(5);
            this.selectorSencilloCaracteristicas.setSelectedIndex(5);
        }
    }

    private void selectorDeTamanioBufferCambiado(ChangeEvent evt) {
        this.selectorSencilloCaracteristicas.setSelectedIndex(0);
        this.etiquetaMemoriaBuffer.setText(this.selectorDeTamanioBuffer.getValue() + " " + ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLSR.MB"));
    }

    private void selectorDePotenciaDeConmutacionCambiado(ChangeEvent evt) {
        this.selectorSencilloCaracteristicas.setSelectedIndex(0);
        this.etiquetaPotencia.setText(this.selectorDePotenciaDeConmutacion.getValue() + " " + ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLSR.Mbps"));
    }

    private void clicEnSelectorDeGenerarEstadisticasSencillo(ActionEvent evt) {
        this.selectorDeGenerarEstadisticasAvanzado.setSelected(this.selectorDeGenerarEstadisticasSencillo.isSelected());
    }

    private void clicEnSelectorDeGenerarEstadisticasAvanzado(ActionEvent evt) {
        this.selectorDeGenerarEstadisticasSencillo.setSelected(this.selectorDeGenerarEstadisticasAvanzado.isSelected());
    }

    private void clicEnCancelar(ActionEvent evt) {
        if (reconfigurando) {
            configLSRA.setShowName(BKUPMostrarNombre);
            configLSRA.setName(BKUPNombre);
            configLSRA.setWellConfigured(true);
            configLSRA.setBufferSizeInMBytes(BKUPTamBuffer);
            configLSRA.setGenerateStats(BKUPGenerarEstadisticas);
            configLSRA.setSwitchingPowerInMbps(BKUPPotencia);
            configLSRA.setDMGPSizeInKB(BKUPTamanioDMGP);
            reconfigurando = false;
        } else {
            configLSRA.setWellConfigured(false);
        }
        this.setVisible(false);
        this.dispose();
    }

    private void clicEnAceptar(ActionEvent evt) {
        configLSRA.setWellConfigured(true);
        if (!this.reconfigurando) {
            configLSRA.setScreenPosition(new Point(panelCoordenadas.getRealX(), panelCoordenadas.getRealY()));
        }
        configLSRA.setDMGPSizeInKB(this.selectorDeTamanioDMGP.getValue());
        configLSRA.setBufferSizeInMBytes(this.selectorDeTamanioBuffer.getValue());
        configLSRA.setSwitchingPowerInMbps(this.selectorDePotenciaDeConmutacion.getValue());
        configLSRA.setName(nombreNodo.getText());
        configLSRA.setGenerateStats(this.selectorDeGenerarEstadisticasSencillo.isSelected());
        configLSRA.setShowName(verNombre.isSelected());
        int error = configLSRA.validateConfig(topo, this.reconfigurando);
        if (error != TActiveLSRNode.OK) {
            JWarningWindow va = new JWarningWindow(ventanaPadre, true, dispensadorDeImagenes);
            va.mostrarMensaje(configLSRA.getErrorMessage(error));
            va.setVisible(true);
        } else {
            this.setVisible(false);
            this.dispose();
        }
    }

    private void clicEnPanelCoordenadas(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            panelCoordenadas.setCoordinates(evt.getPoint());
            coordenadaX.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaEmisor.X=_") + panelCoordenadas.getRealX());
            coordenadaY.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaEmisor.Y=_") + panelCoordenadas.getRealY());
            panelCoordenadas.repaint();
        }
    }

    private void ratonSaleDePanelCoordenadas(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void ratonEntraEnPanelCoordenadas(MouseEvent evt) {
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * Closes the dialog
     */
    private void closeDialog(WindowEvent evt) {
        setVisible(false);
        configLSRA.setWellConfigured(false);
        dispose();
    }

    /**
     * Este m�todo permite cargar en la ventana la configuraci�n actual del nodo
     * LSR.
     *
     * @since 2.0
     * @param tnlsra Nodo LSR que queremos configurar.
     * @param recfg TRUE indica que vamos a reconfigurar el nodo LSR. FALSE
     * indica que el nodo LSR est� siendo insertado nuevo en la topolog�a.
     */
    public void ponerConfiguracion(TActiveLSRNode tnlsra, boolean recfg) {
        configLSRA = tnlsra;
        reconfigurando = recfg;
        if (reconfigurando) {
            this.panelCoordenadas.setEnabled(false);
            this.panelCoordenadas.setToolTipText(null);
            BKUPGenerarEstadisticas = tnlsra.isGeneratingStats();
            BKUPMostrarNombre = tnlsra.nameMustBeDisplayed();
            BKUPNombre = tnlsra.getName();
            BKUPPotencia = tnlsra.getSwitchingPowerInMbps();
            BKUPTamBuffer = tnlsra.getBufferSizeInMBytes();
            BKUPTamanioDMGP = tnlsra.getDMGPSizeInKB();
            this.selectorDeTamanioDMGP.setValue(this.BKUPTamanioDMGP);
            this.selectorDeGenerarEstadisticasAvanzado.setSelected(BKUPGenerarEstadisticas);
            this.selectorDeGenerarEstadisticasSencillo.setSelected(BKUPGenerarEstadisticas);
            this.selectorDePotenciaDeConmutacion.setValue(BKUPPotencia);
            this.selectorDeTamanioBuffer.setValue(BKUPTamBuffer);
            this.nombreNodo.setText(BKUPNombre);
            this.verNombre.setSelected(BKUPMostrarNombre);
        }
    }

    private TImagesBroker dispensadorDeImagenes;
    private Frame ventanaPadre;
    private JDesignPanel pd;
    private TActiveLSRNode configLSRA;
    private TTopology topo;
    private boolean BKUPMostrarNombre;
    private String BKUPNombre;
    private int BKUPPotencia;
    private int BKUPTamBuffer;
    private boolean BKUPGenerarEstadisticas;
    private int BKUPTamanioDMGP;
    private boolean reconfigurando;
    private JLabel coordenadaX;
    private JLabel coordenadaY;
    private JLabel etiquetaMemoriaBuffer;
    private JLabel etiquetaMemoriaDMGP;
    private JLabel etiquetaNombre;
    private JLabel etiquetaPotencia;
    private JLabel iconoEnlace1;
    private JLabel iconoEnlace2;
    private JLabel iconoReceptor;
    private JButton jButton2;
    private JButton jButton3;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JTextField nombreNodo;
    private JPanel panelAvanzado;
    private JPanel panelBotones;
    private JCoordinatesPanel panelCoordenadas;
    private JPanel panelGeneral;
    private JTabbedPane panelPestanias;
    private JPanel panelPosicion;
    private JPanel panelPrincipal;
    private JPanel panelRapido;
    private JCheckBox selectorDeGenerarEstadisticasAvanzado;
    private JCheckBox selectorDeGenerarEstadisticasSencillo;
    private JSlider selectorDePotenciaDeConmutacion;
    private JSlider selectorDeTamanioBuffer;
    private JSlider selectorDeTamanioDMGP;
    private JComboBox selectorSencilloCaracteristicas;
    private JCheckBox verNombre;
}
