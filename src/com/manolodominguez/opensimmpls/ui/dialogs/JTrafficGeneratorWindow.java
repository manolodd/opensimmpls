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


public class JTrafficGeneratorWindow extends JDialog {


    public JTrafficGeneratorWindow(TTopology t, JDesignPanel pad, TImageBroker di, Frame parent, boolean modal) {
        super(parent, modal);
        ventanaPadre = parent;
        dispensadorDeImagenes = di;
        pd = pad;
        topo = t;
        initComponents();
        initComponents2();
    }


    public void initComponents2() {
        panelCoordenadas.setDesignPanel(pd);
        Dimension tamFrame=this.getSize();
        Dimension tamPadre=ventanaPadre.getSize();
        setLocation((tamPadre.width-tamFrame.width)/2, (tamPadre.height-tamFrame.height)/2);
        emisor = null;
        coordenadaX.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaEmisor.X=_") + panelCoordenadas.getRealX());
        coordenadaY.setText(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaEmisor.Y=_") + panelCoordenadas.getRealY());
        Iterator it = topo.getNodesIterator();
        selectorDelReceptor.removeAllItems();
        selectorDelReceptor.addItem("");
        TNode nt;
        while (it.hasNext()) {
            nt = (TNode) it.next();
            if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                selectorDelReceptor.addItem(nt.getName());
            }
        }
        this.selectorDeGoS.removeAllItems();
        this.selectorDeGoS.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.None"));
        this.selectorDeGoS.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Level_1"));
        this.selectorDeGoS.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Level_2"));
        this.selectorDeGoS.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Level_3"));
        this.selectorDeGoS.setSelectedIndex(0);
        this.selectorSencilloTrafico.removeAllItems();
        this.selectorSencilloTrafico.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Personalized"));
        this.selectorSencilloTrafico.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Email"));
        this.selectorSencilloTrafico.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Web"));
        this.selectorSencilloTrafico.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.P2P_file_sharing"));
        this.selectorSencilloTrafico.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Bank_data_transaction"));
        this.selectorSencilloTrafico.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Tele-medical_video"));
        this.selectorSencilloTrafico.addItem(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("JVentanaEmisor.Bulk_traffic"));
        this.selectorSencilloTrafico.setSelectedIndex(0);
        selectorDelReceptor.setSelectedIndex(0);
        BKUPDestino = "";
        BKUPLSPDeBackup = false;
        BKUPMostrarNombre = true;
        BKUPNivelDeGos = 0;
        BKUPNombre = "";
        BKUPTasaTrafico = 1000;
        BKUPTipoTrafico = TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE;
        BKUPGenerarEstadisticas = false;
        BKUPTamDatosConstante = 1024;
        BKUPEncapsularEnMPLS = false;
        reconfigurando = false;
    }

    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.TRAFFIC_GENERATOR_WINDOW.getPath());
        buttonGroup1 = new ButtonGroup();
        panelPrincipal = new JPanel();
        panelPestanias = new JTabbedPane();
        panelGeneral = new JPanel();
        iconoEmisor = new JLabel();
        etiquetaNombre = new JLabel();
        nombreNodo = new JTextField();
        panelPosicion = new JPanel();
        coordenadaX = new JLabel();
        coordenadaY = new JLabel();
        panelCoordenadas = new com.manolodominguez.opensimmpls.ui.dialogs.JCoordinatesPanel();
        verNombre = new JCheckBox();
        jLabel6 = new JLabel();
        selectorDelReceptor = new JComboBox();
        panelRapido = new JPanel();
        iconoEnlace1 = new JLabel();
        jLabel1 = new JLabel();
        selectorDeGenerarEstadisticasSencillo = new JCheckBox();
        selectorSencilloTrafico = new JComboBox();
        panelAvanzado = new JPanel();
        iconoEnlace2 = new JLabel();
        jLabel2 = new JLabel();
        etiquetaTasa = new JLabel();
        selectorDeGenerarEstadisticas = new JCheckBox();
        jLabel4 = new JLabel();
        selectorDeGoS = new JComboBox();
        selectorLSPDeRespaldo = new JCheckBox();
        jLabel5 = new JLabel();
        traficoConstante = new JRadioButton();
        traficoVariable = new JRadioButton();
        encapsularSobreMPLS = new JCheckBox();
        selectorDeTasa = new JSlider();
        selectorDeTamPaquete = new JSlider();
        etiquetaOctetos = new JLabel();
        etiquetaTamPaquete = new JLabel();
        panelBotones = new JPanel();
        jButton2 = new JButton();
        jButton3 = new JButton();

        setTitle(this.translations.getString("VentanaEmisor.TituloVentana")); // NOI18N
        setModal(true);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPestanias.setFont(new Font("Dialog", 0, 12)); // NOI18N

        panelGeneral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iconoEmisor.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.EMISOR));
        iconoEmisor.setText(this.translations.getString("VentanaEmisor.DescripcionNodo")); // NOI18N
        panelGeneral.add(iconoEmisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        etiquetaNombre.setFont(new Font("Dialog", 0, 12)); // NOI18N
        etiquetaNombre.setText(this.translations.getString("VentanaEmisor.Etiqueta.NombreNodo")); // NOI18N
        panelGeneral.add(etiquetaNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 105, 120, -1));

        nombreNodo.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.Nombre")); // NOI18N
        panelGeneral.add(nombreNodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 130, 125, -1));

        panelPosicion.setBorder(BorderFactory.createTitledBorder(this.translations.getString("VentanaEmisor.Etiqueta.Posicion"))); // NOI18N
        panelPosicion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        coordenadaX.setFont(new Font("Dialog", 0, 12)); // NOI18N
        coordenadaX.setText(this.translations.getString("VentanaEmisor.X=_45")); // NOI18N
        panelPosicion.add(coordenadaX, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, -1, -1));

        coordenadaY.setFont(new Font("Dialog", 0, 12)); // NOI18N
        coordenadaY.setText(this.translations.getString("VentanaEmisor.Y=_1024")); // NOI18N
        panelPosicion.add(coordenadaY, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        panelCoordenadas.setBackground(new Color(255, 255, 255));
        panelCoordenadas.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.posicion")); // NOI18N
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
        panelPosicion.add(panelCoordenadas, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 25, 130, 70));

        panelGeneral.add(panelPosicion, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 100, 180, 125));

        verNombre.setFont(new Font("Dialog", 0, 12)); // NOI18N
        verNombre.setSelected(true);
        verNombre.setText(this.translations.getString("VentanaEmisor.Etiqueta.VerNombre")); // NOI18N
        verNombre.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.VerNombre")); // NOI18N
        panelGeneral.add(verNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 175, -1, -1));

        jLabel6.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel6.setText(this.translations.getString("VentanaEmisor.DestinoTrafico")); // NOI18N
        panelGeneral.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 245, 170, -1));

        selectorDelReceptor.setFont(new Font("Dialog", 0, 12)); // NOI18N
        selectorDelReceptor.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.destinodeltrafico")); // NOI18N
        panelGeneral.add(selectorDelReceptor, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 240, -1, -1));

        panelPestanias.addTab(this.translations.getString("VentanaEmisor.Tab.General"), panelGeneral); // NOI18N

        panelRapido.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iconoEnlace1.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ASISTENTE));
        iconoEnlace1.setText(this.translations.getString("VentanaEmisor.configuracionRapida")); // NOI18N
        panelRapido.add(iconoEnlace1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        jLabel1.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText(this.translations.getString("VentanaEmisor.TipoDeTrafico1")); // NOI18N
        panelRapido.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 125, 115, -1));

        selectorDeGenerarEstadisticasSencillo.setFont(new Font("Dialog", 0, 12)); // NOI18N
        selectorDeGenerarEstadisticasSencillo.setText(this.translations.getString("VentanaEmisor.GenerarEstadisticas1")); // NOI18N
        selectorDeGenerarEstadisticasSencillo.setToolTipText(this.translations.getString("VentanaEmisor.GenerarEstadisticas1")); // NOI18N
        selectorDeGenerarEstadisticasSencillo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnGenerarEstadisticasSencillo(evt);
            }
        });
        panelRapido.add(selectorDeGenerarEstadisticasSencillo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 195, -1, -1));

        selectorSencilloTrafico.setFont(new Font("Dialog", 0, 12)); // NOI18N
        selectorSencilloTrafico.setModel(new DefaultComboBoxModel(new String[] { "Personalized", "Email", "Web", "P2P file sharing", "Bank data transaction", "Tele-medical video", "Bulk traffic" }));
        selectorSencilloTrafico.setToolTipText(this.translations.getString("VentanaEmisor.TipoDeTrafico1")); // NOI18N
        selectorSencilloTrafico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnSelectorSencilloTrafico(evt);
            }
        });
        panelRapido.add(selectorSencilloTrafico, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 120, -1, -1));

        panelPestanias.addTab(this.translations.getString("VentanaEmisor.Tab.Rapida"), panelRapido); // NOI18N

        panelAvanzado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iconoEnlace2.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.AVANZADA));
        iconoEnlace2.setText(this.translations.getString("VentanaEmisor.ConfiguracionAvanzada")); // NOI18N
        panelAvanzado.add(iconoEnlace2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        jLabel2.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText(this.translations.getString("VentanaEmisor.TasaDeTrafico")); // NOI18N
        panelAvanzado.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 90, 100, -1));

        etiquetaTasa.setFont(new Font("Dialog", 0, 10)); // NOI18N
        etiquetaTasa.setForeground(new Color(102, 102, 102));
        etiquetaTasa.setHorizontalAlignment(SwingConstants.LEFT);
        etiquetaTasa.setText(this.translations.getString("VentanaEmisor.Kbpsinicial")); // NOI18N
        panelAvanzado.add(etiquetaTasa, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, 70, -1));

        selectorDeGenerarEstadisticas.setFont(new Font("Dialog", 0, 12)); // NOI18N
        selectorDeGenerarEstadisticas.setText(this.translations.getString("VentanaEmisor.GenerarEstadisticas2")); // NOI18N
        selectorDeGenerarEstadisticas.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.GenerarEstadisticas2")); // NOI18N
        selectorDeGenerarEstadisticas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnGenerarEstadisticasAvanzado(evt);
            }
        });
        panelAvanzado.add(selectorDeGenerarEstadisticas, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, -1, -1));

        jLabel4.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel4.setText(this.translations.getString("VentanaEmisor.NivelDeGoS")); // NOI18N
        panelAvanzado.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 85, -1));

        selectorDeGoS.setFont(new Font("Dialog", 0, 12)); // NOI18N
        selectorDeGoS.setModel(new DefaultComboBoxModel(new String[] { "None", "Level 1", "Level 2", "Level 3" }));
        selectorDeGoS.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.nivelDeGoS")); // NOI18N
        selectorDeGoS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnNivelGoS(evt);
            }
        });
        panelAvanzado.add(selectorDeGoS, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, -1, -1));

        selectorLSPDeRespaldo.setFont(new Font("Dialog", 0, 12)); // NOI18N
        selectorLSPDeRespaldo.setText(this.translations.getString("VentanaEmisor.CrearLSPBackup")); // NOI18N
        selectorLSPDeRespaldo.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.crearUnLSPdeBackup")); // NOI18N
        selectorLSPDeRespaldo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnLSPDeRespaldo(evt);
            }
        });
        panelAvanzado.add(selectorLSPDeRespaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 230, -1, -1));

        jLabel5.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel5.setText(this.translations.getString("VentanaEmisor.TipoDeTrafico3")); // NOI18N
        panelAvanzado.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 125, 100, -1));

        buttonGroup1.add(traficoConstante);
        traficoConstante.setFont(new Font("Dialog", 0, 12)); // NOI18N
        traficoConstante.setText(this.translations.getString("VentanaEmisor.TraficoConstante")); // NOI18N
        traficoConstante.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.traficoConstante")); // NOI18N
        traficoConstante.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnTraficoConstante(evt);
            }
        });
        panelAvanzado.add(traficoConstante, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 125, -1, 20));

        buttonGroup1.add(traficoVariable);
        traficoVariable.setFont(new Font("Dialog", 0, 12)); // NOI18N
        traficoVariable.setSelected(true);
        traficoVariable.setText(this.translations.getString("VentanaEmisor.TraficoVariable")); // NOI18N
        traficoVariable.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.traficovariable")); // NOI18N
        traficoVariable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnTraficoVariable(evt);
            }
        });
        panelAvanzado.add(traficoVariable, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 125, -1, 20));

        encapsularSobreMPLS.setFont(new Font("Dialog", 0, 12)); // NOI18N
        encapsularSobreMPLS.setText(this.translations.getString("VentanaEmisor.EncapsularSobreMPLS")); // NOI18N
        encapsularSobreMPLS.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.encapsularsobrempls")); // NOI18N
        encapsularSobreMPLS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnEncapsularSobreMPLS(evt);
            }
        });
        panelAvanzado.add(encapsularSobreMPLS, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, -1, -1));

        selectorDeTasa.setMajorTickSpacing(1000);
        selectorDeTasa.setMaximum(10240);
        selectorDeTasa.setMinimum(1);
        selectorDeTasa.setMinorTickSpacing(100);
        selectorDeTasa.setToolTipText(this.translations.getString("VentanaEmisor.tooltipo.CambiarTasa")); // NOI18N
        selectorDeTasa.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                cambioEnSelectorDeTasa(evt);
            }
        });
        panelAvanzado.add(selectorDeTasa, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 165, -1));

        selectorDeTamPaquete.setMajorTickSpacing(1000);
        selectorDeTamPaquete.setMaximum(65495);
        selectorDeTamPaquete.setMinorTickSpacing(100);
        selectorDeTamPaquete.setToolTipText(this.translations.getString("VentanaEmisor.tooltipo.CambiarTasa")); // NOI18N
        selectorDeTamPaquete.setValue(1024);
        selectorDeTamPaquete.setEnabled(false);
        selectorDeTamPaquete.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                clicEnSelectorDeTamPaquete(evt);
            }
        });
        panelAvanzado.add(selectorDeTamPaquete, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 160, 120, -1));

        etiquetaOctetos.setFont(new Font("Dialog", 0, 10)); // NOI18N
        etiquetaOctetos.setForeground(new Color(102, 102, 102));
        etiquetaOctetos.setHorizontalAlignment(SwingConstants.LEFT);
        etiquetaOctetos.setText("null");
        etiquetaOctetos.setEnabled(false);
        panelAvanzado.add(etiquetaOctetos, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 160, 70, -1));

        etiquetaTamPaquete.setFont(new Font("Dialog", 0, 12)); // NOI18N
        etiquetaTamPaquete.setHorizontalAlignment(SwingConstants.RIGHT);
        etiquetaTamPaquete.setText(this.translations.getString("JVentanaEmisor.TamCargaUtil")); // NOI18N
        etiquetaTamPaquete.setEnabled(false);
        panelAvanzado.add(etiquetaTamPaquete, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 140, -1));

        panelPestanias.addTab(this.translations.getString("VentanaEmisor.Tab.Avanzada"), panelAvanzado); // NOI18N

        panelPrincipal.add(panelPestanias, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 370, 330));

        getContentPane().add(panelPrincipal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 350));

        panelBotones.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jButton2.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ACEPTAR));
        jButton2.setMnemonic(this.translations.getString("VentanaEmisor.botones.Aceptar").charAt(0));
        jButton2.setText(this.translations.getString("VentanaEmisor.Boton.Aceptar.Texto")); // NOI18N
        jButton2.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.Aceptar")); // NOI18N
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnAceptar(evt);
            }
        });
        panelBotones.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 110, -1));

        jButton3.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jButton3.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.CANCELAR));
        jButton3.setMnemonic(this.translations.getString("VentanaEmisor.botones.Cancelar").charAt(0));
        jButton3.setText(this.translations.getString("VentanaEmisor.Boton.Cancelar.Texto")); // NOI18N
        jButton3.setToolTipText(this.translations.getString("VentanaEmisor.tooltip.Cancelar")); // NOI18N
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnCancelar(evt);
            }
        });
        panelBotones.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 15, 110, -1));

        getContentPane().add(panelBotones, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 340, 400, 50));

        pack();
    }

    private void clicEnSelectorSencilloTrafico(ActionEvent evt) {
        int seleccionado = this.selectorSencilloTrafico.getSelectedIndex();
        if (seleccionado > 0) {
            if (seleccionado == 1) {
                this.selectorLSPDeRespaldo.setSelected(false);
                this.selectorDeGoS.setSelectedIndex(0);
                this.traficoConstante.setSelected(false);
                this.traficoVariable.setSelected(true);
                this.selectorDeTamPaquete.setValue(0);
                this.selectorDeTasa.setValue(1);
                this.selectorDeTamPaquete.setEnabled(false);
            } else if (seleccionado == 2) {
                this.selectorLSPDeRespaldo.setSelected(false);
                this.selectorDeGoS.setSelectedIndex(0);
                this.traficoConstante.setSelected(false);
                this.traficoVariable.setSelected(true);
                this.selectorDeTamPaquete.setValue(0);
                this.selectorDeTasa.setValue(7);
                this.selectorDeTamPaquete.setEnabled(false);
            } else if (seleccionado == 3) {
                this.selectorLSPDeRespaldo.setSelected(false);
                this.selectorDeGoS.setSelectedIndex(0);
                this.traficoConstante.setSelected(false);
                this.traficoVariable.setSelected(true);
                this.selectorDeTamPaquete.setValue(0);
                this.selectorDeTasa.setValue(3413);
                this.selectorDeTamPaquete.setEnabled(false);
            } else if (seleccionado == 4) {
                this.selectorLSPDeRespaldo.setSelected(true);
                this.selectorDeGoS.setSelectedIndex(0);
                this.traficoConstante.setSelected(false);
                this.traficoVariable.setSelected(true);
                this.selectorDeTamPaquete.setValue(0);
                this.selectorDeTasa.setValue(10240);
                this.selectorDeTamPaquete.setEnabled(false);
            } else if (seleccionado == 5) {
                this.selectorLSPDeRespaldo.setSelected(true);
                this.selectorDeGoS.setSelectedIndex(2);
                this.traficoConstante.setSelected(false);
                this.traficoVariable.setSelected(true);
                this.selectorDeTamPaquete.setValue(0);
                this.selectorDeTasa.setValue(341);
                this.selectorDeTamPaquete.setEnabled(false);
            } else if (seleccionado == 6) {
                this.selectorLSPDeRespaldo.setSelected(false);
                this.selectorDeGoS.setSelectedIndex(0);
                this.traficoConstante.setSelected(false);
                this.traficoVariable.setSelected(true);
                this.selectorDeTamPaquete.setValue(0);
                this.selectorDeTasa.setValue(6827);
                this.selectorDeTamPaquete.setEnabled(false);
            }
        }
        this.selectorSencilloTrafico.setSelectedIndex(seleccionado);
    }

    private void clicEnSelectorDeTamPaquete(ChangeEvent evt) {
    int tamSeleccionado = this.selectorDeTamPaquete.getValue();
    String unidades = this.translations.getString("JVentanaEmisor.Octetos");
    this.etiquetaOctetos.setText(tamSeleccionado + " " +unidades);
    }

private void clicEnGenerarEstadisticasAvanzado(ActionEvent evt) {
    this.selectorDeGenerarEstadisticasSencillo.setSelected(this.selectorDeGenerarEstadisticas.isSelected());
}

private void clicEnGenerarEstadisticasSencillo(ActionEvent evt) {
    this.selectorDeGenerarEstadisticas.setSelected(this.selectorDeGenerarEstadisticasSencillo.isSelected());
}

private void clicEnLSPDeRespaldo(ActionEvent evt) {
    this.selectorSencilloTrafico.setSelectedIndex(0);
}

private void clicEnNivelGoS(ActionEvent evt) {
    this.selectorSencilloTrafico.setSelectedIndex(0);
}

private void clicEnEncapsularSobreMPLS(ActionEvent evt) {
    this.selectorSencilloTrafico.setSelectedIndex(0);
}

private void clicEnTraficoVariable(ActionEvent evt) {
    this.selectorSencilloTrafico.setSelectedIndex(0);
    this.selectorDeTamPaquete.setEnabled(false);
    this.etiquetaOctetos.setEnabled(false);
    this.etiquetaTamPaquete.setEnabled(false);
}

private void clicEnTraficoConstante(ActionEvent evt) {
    this.selectorSencilloTrafico.setSelectedIndex(0);
    this.selectorDeTamPaquete.setEnabled(true);
    this.etiquetaOctetos.setEnabled(true);
    this.etiquetaTamPaquete.setEnabled(true);
}

private void cambioEnSelectorDeTasa(ChangeEvent evt) {
    this.selectorSencilloTrafico.setSelectedIndex(0);
    int tasaSeleccionada = this.selectorDeTasa.getValue();
    String unidades = this.translations.getString("VentanaEmisor.unidades.kbps");
    this.etiquetaTasa.setText(tasaSeleccionada + " " +unidades);
}

private void clicEnCancelar(ActionEvent evt) {
    if (reconfigurando) {
        emisor.setTargetNode(BKUPDestino);
        emisor.setRequestBackupLSP(BKUPLSPDeBackup);
        emisor.setShowName(BKUPMostrarNombre);
        emisor.setGoSLevel(BKUPNivelDeGos);
        emisor.setName(BKUPNombre);
        emisor.setTrafficGenerationRate(BKUPTasaTrafico);
        emisor.setTrafficGenerationMode(BKUPTipoTrafico);
        emisor.setGenerateStats(BKUPGenerarEstadisticas);
        emisor.setConstantPayloadSizeInBytes(BKUPTamDatosConstante);
        emisor.setWellConfigured(true);
        emisor.encapsulateOverMPLS(BKUPEncapsularEnMPLS);
        reconfigurando = false;
    } else {
        emisor.setWellConfigured(false);
    }
    this.setVisible(false);
    this.dispose();
}

private void clicEnAceptar(ActionEvent evt) {
    emisor.setWellConfigured(true);
    if (!this.reconfigurando){
        emisor.setScreenPosition(new Point(panelCoordenadas.getRealX(),panelCoordenadas.getRealY()));
    }
    emisor.setName(nombreNodo.getText());
    emisor.setShowName(verNombre.isSelected());
    emisor.setGenerateStats(this.selectorDeGenerarEstadisticas.isSelected());
    emisor.setTrafficGenerationRate(this.selectorDeTasa.getValue());
    emisor.setRequestBackupLSP(this.selectorLSPDeRespaldo.isSelected());
    emisor.encapsulateOverMPLS(this.encapsularSobreMPLS.isSelected());
    emisor.setGoSLevel(this.selectorDeGoS.getSelectedIndex());
    emisor.setTargetNode((String) this.selectorDelReceptor.getSelectedItem());
    emisor.setConstantPayloadSizeInBytes(this.selectorDeTamPaquete.getValue());
    if (this.traficoConstante.isSelected()) {
        emisor.setTrafficGenerationMode(TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE);
    } else if (this.traficoVariable.isSelected()) {
        emisor.setTrafficGenerationMode(TTrafficGeneratorNode.VARIABLE_TRAFFIC_RATE);
    }
    int error = emisor.validateConfig(topo, this.reconfigurando);
    if (error != TTrafficGeneratorNode.OK) {
        JWarningWindow va = new JWarningWindow(ventanaPadre, true, dispensadorDeImagenes);
        va.setWarningMessage(emisor.getErrorMessage(error));
        va.show();
    } else {
        this.reconfigurando = false;
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
        emisor.setWellConfigured(false);
        dispose();
    }


    public void ponerConfiguracion(TTrafficGeneratorNode tce, boolean recfg) {
        emisor = tce;
        reconfigurando = recfg;
        if (reconfigurando) {
            this.panelCoordenadas.setEnabled(false);
            this.panelCoordenadas.setToolTipText(null);
            TNode nt = this.topo.getNode(emisor.getTargetIPv4Address());
            if (nt != null) {
                BKUPDestino = nt.getName();
            }
            BKUPLSPDeBackup = emisor.isRequestingBackupLSP();
            BKUPMostrarNombre = emisor.nameMustBeDisplayed();
            BKUPNivelDeGos = emisor.getGoSLevel();
            BKUPNombre = emisor.getName();
            BKUPTasaTrafico = emisor.getTrafficGenerationRate();
            BKUPTipoTrafico = emisor.getTrafficGenerationMode();
            BKUPGenerarEstadisticas = emisor.isGeneratingStats();
            BKUPTamDatosConstante = emisor.getConstantPayloadSizeInBytes();
            this.BKUPEncapsularEnMPLS = emisor.isEncapsulatingOverMPLS();
            
            
            this.encapsularSobreMPLS.setSelected(BKUPEncapsularEnMPLS);
            this.nombreNodo.setText(BKUPNombre);
            if (BKUPTipoTrafico == TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE) {
                this.traficoConstante.setSelected(true);
                this.traficoVariable.setSelected(false);
            } else {
                this.traficoConstante.setSelected(false);
                this.traficoVariable.setSelected(true);
            }
            this.selectorDeGenerarEstadisticas.setSelected(BKUPGenerarEstadisticas);
            this.selectorDeGenerarEstadisticasSencillo.setSelected(BKUPGenerarEstadisticas);
            this.selectorLSPDeRespaldo.setSelected(BKUPLSPDeBackup);
            this.verNombre.setSelected(BKUPMostrarNombre);
            int numDestinos = selectorDelReceptor.getItemCount();
            int i = 0;
            String destinoAux;
            for (i = 0; i<numDestinos; i++) {
                destinoAux = (String) selectorDelReceptor.getItemAt(i);
                if (destinoAux.equals(BKUPDestino)) {
                    selectorDelReceptor.setSelectedIndex(i);
                }
            }
            if (this.selectorDeGoS.getItemCount() >= BKUPNivelDeGos) {
                this.selectorDeGoS.setSelectedIndex(BKUPNivelDeGos);
            }
            this.selectorSencilloTrafico.setSelectedIndex(0);
            this.selectorDeTasa.setValue(BKUPTasaTrafico);

            if (BKUPTipoTrafico == TTrafficGeneratorNode.CONSTANT_TRAFFIC_RATE) {
                this.selectorDeTamPaquete.setEnabled(true);
                this.etiquetaOctetos.setEnabled(true);
                this.etiquetaTamPaquete.setEnabled(true);
                String unidades = this.translations.getString("JVentanaEmisor.Octetos");
                this.etiquetaOctetos.setText(this.BKUPTamDatosConstante + " " +unidades);        }
                this.selectorDeTamPaquete.setValue(this.BKUPTamDatosConstante);
            }
    }

    private TImageBroker dispensadorDeImagenes;
    private Frame ventanaPadre;
    private JDesignPanel pd;
    private TTrafficGeneratorNode emisor;
    private TTopology topo;
    
    private String BKUPDestino;
    private boolean BKUPLSPDeBackup;
    private boolean BKUPMostrarNombre;
    private int BKUPNivelDeGos;
    private String BKUPNombre;
    private int BKUPTasaTrafico;
    private int BKUPTipoTrafico;
    private boolean BKUPGenerarEstadisticas;
    private int BKUPTamDatosConstante;
    private boolean BKUPEncapsularEnMPLS;

    private boolean reconfigurando;
    
    private ButtonGroup buttonGroup1;
    private JLabel coordenadaX;
    private JLabel coordenadaY;
    private JCheckBox encapsularSobreMPLS;
    private JLabel etiquetaNombre;
    private JLabel etiquetaOctetos;
    private JLabel etiquetaTamPaquete;
    private JLabel etiquetaTasa;
    private JLabel iconoEmisor;
    private JLabel iconoEnlace1;
    private JLabel iconoEnlace2;
    private JButton jButton2;
    private JButton jButton3;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JTextField nombreNodo;
    private JPanel panelAvanzado;
    private JPanel panelBotones;
    private JCoordinatesPanel panelCoordenadas;
    private JPanel panelGeneral;
    private JTabbedPane panelPestanias;
    private JPanel panelPosicion;
    private JPanel panelPrincipal;
    private JPanel panelRapido;
    private JCheckBox selectorDeGenerarEstadisticas;
    private JCheckBox selectorDeGenerarEstadisticasSencillo;
    private JComboBox selectorDeGoS;
    private JSlider selectorDeTamPaquete;
    private JSlider selectorDeTasa;
    private JComboBox selectorDelReceptor;
    private JCheckBox selectorLSPDeRespaldo;
    private JComboBox selectorSencilloTrafico;
    private JRadioButton traficoConstante;
    private JRadioButton traficoVariable;
    private JCheckBox verNombre;
    private ResourceBundle translations;
}
