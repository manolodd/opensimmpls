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
import com.manolodominguez.opensimmpls.scenario.TTrafficSinkNode;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class JTrafficSinkWindow extends JDialog {

    public JTrafficSinkWindow(TTopology t, JDesignPanel pad, TImageBroker di, Frame parent, boolean modal) {
        super(parent, modal);
        ventanaPadre = parent;
        dispensadorDeImagenes = di;
        pd = pad;
        topo = t;
        initComponents();
        initComponents2();
    }

    private void initComponents2() {
        panelCoordenadas.setDesignPanel(pd);
        Dimension tamFrame=this.getSize();
        Dimension tamPadre=ventanaPadre.getSize();
        setLocation((tamPadre.width-tamFrame.width)/2, (tamPadre.height-tamFrame.height)/2);
        configReceptor = null;
        coordenadaX.setText(this.translations.getString("VentanaEmisor.X=_") + panelCoordenadas.getRealX());
        coordenadaY.setText(this.translations.getString("VentanaEmisor.Y=_") + panelCoordenadas.getRealY());
        BKUPMostrarNombre = true;
        BKUPNombre = "";
        reconfigurando = false;
    }

    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.TRAFFIC_SINK_WINDOW.getPath());
        panelPrincipal = new JPanel();
        panelPestanias = new JTabbedPane();
        panelGeneral = new JPanel();
        iconoReceptor = new JLabel();
        etiquetaNombre = new JLabel();
        nombreNodo = new JTextField();
        panelPosicion = new JPanel();
        coordenadaX = new JLabel();
        coordenadaY = new JLabel();
        panelCoordenadas = new com.manolodominguez.opensimmpls.ui.dialogs.JCoordinatesPanel();
        verNombre = new JCheckBox();
        panelRapido = new JPanel();
        iconoEnlace1 = new JLabel();
        selectorGenerarEstadisticaFacil = new JCheckBox();
        panelAvanzado = new JPanel();
        iconoEnlace2 = new JLabel();
        selectorGenerarEstadisticaAvanzada = new JCheckBox();
        panelBotones = new JPanel();
        jButton2 = new JButton();
        jButton3 = new JButton();
        setTitle(this.translations.getString("VentanaReceptor.titulo"));
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
        iconoReceptor.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.RECEPTOR));
        iconoReceptor.setText(this.translations.getString("VentanaReceptor.descripcion"));
        panelGeneral.add(iconoReceptor, new AbsoluteConstraints(15, 20, 335, -1));
        etiquetaNombre.setFont(new Font("Dialog", 0, 12));
        etiquetaNombre.setText(this.translations.getString("VentanaReceptor.etiquetaNombre"));
        panelGeneral.add(etiquetaNombre, new AbsoluteConstraints(215, 80, 120, -1));
        panelGeneral.add(nombreNodo, new AbsoluteConstraints(215, 105, 125, -1));
        panelPosicion.setBorder(BorderFactory.createTitledBorder(this.translations.getString("VentanaReceptor.titulogrupo")));
        panelPosicion.setLayout(new AbsoluteLayout());
        coordenadaX.setFont(new Font("Dialog", 0, 12));
        coordenadaX.setText(this.translations.getString("VentanaReceptor.X=_"));
        panelPosicion.add(coordenadaX, new AbsoluteConstraints(100, 100, -1, -1));
        coordenadaY.setFont(new Font("Dialog", 0, 12));
        coordenadaY.setText(this.translations.getString("VentanaReceptor.Y=_"));
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
        verNombre.setText(this.translations.getString("VentanaReceptor.verNombre"));
        panelGeneral.add(verNombre, new AbsoluteConstraints(215, 135, -1, -1));
        panelPestanias.addTab(this.translations.getString("VentanaReceptor.tab.General"), panelGeneral);
        panelRapido.setLayout(new AbsoluteLayout());
        iconoEnlace1.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ASISTENTE));
        iconoEnlace1.setText(this.translations.getString("JVentanaReceptor.configuracionSencilla"));
        panelRapido.add(iconoEnlace1, new AbsoluteConstraints(15, 20, 335, -1));
        selectorGenerarEstadisticaFacil.setFont(new Font("Dialog", 0, 12));
        selectorGenerarEstadisticaFacil.setText(this.translations.getString("JVentanaReceptor.generarEstadisticas"));
        selectorGenerarEstadisticaFacil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnGenerarEstadisticaFacil(evt);
            }
        });
        panelRapido.add(selectorGenerarEstadisticaFacil, new AbsoluteConstraints(60, 125, -1, -1));
        panelPestanias.addTab(this.translations.getString("VentanaReceptor.tab.Fast"), panelRapido);
        panelAvanzado.setLayout(new AbsoluteLayout());
        iconoEnlace2.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.AVANZADA));
        iconoEnlace2.setText(this.translations.getString("JVentanaReceptor.configuracionAvanzada"));
        panelAvanzado.add(iconoEnlace2, new AbsoluteConstraints(15, 20, 335, -1));
        selectorGenerarEstadisticaAvanzada.setFont(new Font("Dialog", 0, 12));
        selectorGenerarEstadisticaAvanzada.setText(this.translations.getString("JVentanaReceptor.GenerEstadisticas"));
        selectorGenerarEstadisticaAvanzada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnGenerarEstadisticaAvanzada(evt);
            }
        });
        panelAvanzado.add(selectorGenerarEstadisticaAvanzada, new AbsoluteConstraints(60, 125, -1, -1));
        panelPestanias.addTab(this.translations.getString("VentanaReceptor.tab.Advanced"), panelAvanzado);
        panelPrincipal.add(panelPestanias, new AbsoluteConstraints(15, 15, 370, 240));
        panelBotones.setLayout(new AbsoluteLayout());
        jButton2.setFont(new Font("Dialog", 0, 12));
        jButton2.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ACEPTAR));
        jButton2.setMnemonic(this.translations.getString("VentanaReceptor.botones.mne.Aceptar").charAt(0));
        jButton2.setText(this.translations.getString("VentanaReceptor.boton.Ok"));
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnAceptar(evt);
            }
        });
        panelBotones.add(jButton2, new AbsoluteConstraints(15, 15, 105, -1));
        jButton3.setFont(new Font("Dialog", 0, 12));
        jButton3.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.CANCELAR));
        jButton3.setMnemonic(this.translations.getString("VentanaReceptor.botones.mne.Cancelar").charAt(0));
        jButton3.setText(this.translations.getString("VentanaReceptor.boton.Cancel"));
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnCancelar(evt);
            }
        });
        panelBotones.add(jButton3, new AbsoluteConstraints(135, 15, 105, -1));
        panelPrincipal.add(panelBotones, new AbsoluteConstraints(0, 255, 400, 55));
        getContentPane().add(panelPrincipal, new AbsoluteConstraints(0, 0, -1, 310));
        pack();
    }

private void clicEnGenerarEstadisticaAvanzada(ActionEvent evt) {
    this.selectorGenerarEstadisticaFacil.setSelected(this.selectorGenerarEstadisticaAvanzada.isSelected());
}

private void clicEnGenerarEstadisticaFacil(ActionEvent evt) {
    this.selectorGenerarEstadisticaAvanzada.setSelected(this.selectorGenerarEstadisticaFacil.isSelected());
}

private void clicEnCancelar(ActionEvent evt) {
    if (reconfigurando) {
        configReceptor.setShowName(BKUPMostrarNombre);
        configReceptor.setName(BKUPNombre);
        configReceptor.setWellConfigured(true);
        reconfigurando = false;
    } else {
        configReceptor.setWellConfigured(false);
    }
    this.setVisible(false);
    this.dispose();
}

private void clicEnAceptar(ActionEvent evt) {
    configReceptor.setWellConfigured(true);
    if (!this.reconfigurando){
        configReceptor.setScreenPosition(new Point(panelCoordenadas.getRealX(),panelCoordenadas.getRealY()));
    }
    configReceptor.setName(nombreNodo.getText());
    configReceptor.setShowName(verNombre.isSelected());
    configReceptor.setGenerateStats(this.selectorGenerarEstadisticaAvanzada.isSelected());
    int error = configReceptor.validateConfig(topo, this.reconfigurando);
    if (error != TTrafficSinkNode.OK) {
        JWarningWindow va = new JWarningWindow(ventanaPadre, true, dispensadorDeImagenes);
        va.setWarningMessage(configReceptor.getErrorMessage(error));
        va.setVisible(true);
    } else {
        this.setVisible(false);
        this.dispose();
    }
}

private void clicEnPanelCoordenadas(MouseEvent evt) {
    if (evt.getButton() == MouseEvent.BUTTON1) {
        panelCoordenadas.setCoordinates(evt.getPoint());
        coordenadaX.setText(this.translations.getString("VentanaEmisor.X=_") + panelCoordenadas.getRealX());
        coordenadaY.setText(this.translations.getString("VentanaEmisor.Y=_") + panelCoordenadas.getRealY());
        panelCoordenadas.repaint();
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
        configReceptor.setWellConfigured(false);
        dispose();
    }

    public void ponerConfiguracion(TTrafficSinkNode tnr, boolean recfg) {
        configReceptor = tnr;
        reconfigurando = recfg;
        if (reconfigurando) {
            this.panelCoordenadas.setEnabled(false);
            this.panelCoordenadas.setToolTipText(null);
            BKUPGenerarEstadisticas = tnr.isGeneratingStats();
            BKUPMostrarNombre = tnr.nameMustBeDisplayed();
            BKUPNombre = tnr.getName();
            this.nombreNodo.setText(BKUPNombre);
            this.verNombre.setSelected(BKUPMostrarNombre);
            this.selectorGenerarEstadisticaAvanzada.setSelected(BKUPGenerarEstadisticas);
            this.selectorGenerarEstadisticaFacil.setSelected(BKUPGenerarEstadisticas);
        }
    }

    private TImageBroker dispensadorDeImagenes;
    private Frame ventanaPadre;
    private JDesignPanel pd;
    private TTrafficSinkNode configReceptor;
    private TTopology topo;
    private boolean BKUPMostrarNombre;
    private String BKUPNombre;
    private boolean BKUPGenerarEstadisticas;
    private boolean reconfigurando;
    private JLabel coordenadaX;
    private JLabel coordenadaY;
    private JLabel etiquetaNombre;
    private JLabel iconoEnlace1;
    private JLabel iconoEnlace2;
    private JLabel iconoReceptor;
    private JButton jButton2;
    private JButton jButton3;
    private JTextField nombreNodo;
    private JPanel panelAvanzado;
    private JPanel panelBotones;
    private com.manolodominguez.opensimmpls.ui.dialogs.JCoordinatesPanel panelCoordenadas;
    private JPanel panelGeneral;
    private JTabbedPane panelPestanias;
    private JPanel panelPosicion;
    private JPanel panelPrincipal;
    private JPanel panelRapido;
    private JCheckBox selectorGenerarEstadisticaAvanzada;
    private JCheckBox selectorGenerarEstadisticaFacil;
    private JCheckBox verNombre;
    private ResourceBundle translations;
}
