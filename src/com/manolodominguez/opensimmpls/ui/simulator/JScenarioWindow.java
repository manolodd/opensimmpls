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
import javax.swing.JCheckBox;
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
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Spacer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.XYSeriesCollection;

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
     * Este m�todo se encarga de start los atributos de la clase que no hayan
     * sido aun iniciados por NetBeans.
     *
     * @since 2.0
     */
    private void initComponents2() {
        this.panelDisenio.setImageBroker(this.imageBroker);
        this.panelSimulacion.ponerDispensadorDeImagenes(this.imageBroker);
        Dimension tamPantalla = this.parent.getSize();
        this.setSize((tamPantalla.width * 9 / 10), (tamPantalla.height * 9 / 10));
        Dimension tamFrame = this.getSize();
        this.setLocation((tamPantalla.width - tamFrame.width) / 2, (tamPantalla.height - tamFrame.height) / 2);
        this.scenario = new TScenario();
        this.panelDisenio.setTopology(this.scenario.getTopology());
        this.panelSimulacion.ponerTopologia(this.scenario.getTopology());
        this.nodoSeleccionado = null;
        this.elementoDisenioClicDerecho = null;
        this.aProgresoGeneracion = new TProgressEventListener(this.barraDeProgreso);
        try {
            this.scenario.getTopology().getTimer().addProgressEventListener(this.aProgresoGeneracion);
        } catch (EProgressEventGeneratorOnlyAllowASingleListener e) {
            e.printStackTrace();
        }
        this.mlsPorTic.setValue(1);
        this.pasoNs.setMaximum(this.duracionMs.getValue() * 1000000 + this.duracionNs.getValue());
        this.etiquetaMlsPorTic.setText(this.mlsPorTic.getValue() + this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.etiquetaDuracionMs.setText(this.duracionMs.getValue() + this.translations.getString("VentanaHija._ms."));
        this.etiquetaDuracionNs.setText(this.duracionNs.getValue() + this.translations.getString("VentanaHija._ns."));
        this.etiquetaPasoNs.setText(this.pasoNs.getValue() + this.translations.getString("VentanaHija_ns."));
        this.controlTemporizacionDesactivado = false;
        this.scenario.getSimulation().setSimulationPanel(this.panelSimulacion);
        this.panelGrafico1 = null;
        this.panelGrafico2 = null;
        this.panelGrafico3 = null;
        this.panelGrafico4 = null;
        this.panelGrafico5 = null;
        this.panelGrafico6 = null;
        this.grafico1 = null;
        this.grafico2 = null;
        this.grafico3 = null;
        this.grafico4 = null;
        this.grafico5 = null;
        this.grafico6 = null;
    }

    /**
     * Este m�todo es llamado desde el constructor para actualizar la mayor
     * parte de los atributos de la clase que tienen que ver con la interfaz de
     * usuario. Es un m�todo creado por NetBeans automaticamente.
     *
     * @since 2.0
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        this.translations = ResourceBundle.getBundle(AvailableBundles.SCENARIO_WINDOW.getPath());
        GridBagConstraints gridBagConstraints;
        this.diseElementoPopUp = new JPopupMenu();
        this.dEliminarMenuItem = new JMenuItem();
        this.dVerNombreMenuItem = new JCheckBoxMenuItem();
        this.jSeparator1 = new JSeparator();
        this.dPropiedadesMenuItem = new JMenuItem();
        this.diseFondoPopUp = new JPopupMenu();
        this.dVerNombresNodosMenuItem = new JMenuItem();
        this.dOcultarNombresNodosMenuItem = new JMenuItem();
        this.dVerNombresEnlacesMenuItem = new JMenuItem();
        this.dOcultarNombresEnlacesMenuItem = new JMenuItem();
        this.jSeparator2 = new JSeparator();
        this.dEliminarTodoMenuItem = new JMenuItem();
        this.jTabbedPane1 = new JTabbedPane();
        this.panelDisenioSuperior = new JPanel();
        this.panelBotonesDisenio = new JPanel();
        this.iconoEmisor = new JLabel();
        this.iconoReceptor = new JLabel();
        this.iconoLER = new JLabel();
        this.iconoLERA = new JLabel();
        this.iconoLSR = new JLabel();
        this.iconoLSRA = new JLabel();
        this.iconoEnlace = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.panelDisenio = new JDesignPanel();
        this.panelSimulacionSuperior = new JPanel();
        this.panelBotonesSimulacion = new JPanel();
        this.iconoComenzar = new JLabel();
        this.iconoFinalizar = new JLabel();
        this.iconoReanudar = new JLabel();
        this.iconoPausar = new JLabel();
        this.barraDeProgreso = new JProgressBar();
        this.mlsPorTic = new JSlider();
        this.etiquetaMlsPorTic = new JLabel();
        this.crearTraza = new JCheckBox();
        this.jScrollPane2 = new JScrollPane();
        this.panelSimulacion = new JSimulationPanel();
        this.panelAnalisisSuperior = new JPanel();
        this.panelSeleccionElemento = new JPanel();
        this.jLabel1 = new JLabel();
        this.selectorElementoEstadisticas = new JComboBox();
        this.jScrollPane4 = new JScrollPane();
        this.panelAnalisis = new JPanel();
        this.panelFijo = new JPanel();
        this.etiquetaEstadisticasTituloEscenario = new JLabel();
        this.etiquetaEstadisticasNombreAutor = new JLabel();
        this.areaEstadisticasDescripcion = new JTextArea();
        this.etiquetaNombreElementoEstadistica = new JLabel();
        this.panelOpcionesSuperior = new JPanel();
        this.jScrollPane3 = new JScrollPane();
        this.panelOpciones = new JPanel();
        this.jPanel3 = new JPanel();
        this.jLabel5 = new JLabel();
        this.nombreEscenario = new JTextField();
        this.jLabel6 = new JLabel();
        this.nombreAutor = new JTextField();
        this.jLabel7 = new JLabel();
        this.descripcionEscenario = new JTextField();
        this.jPanel2 = new JPanel();
        this.jLabel3 = new JLabel();
        this.duracionMs = new JSlider();
        this.etiquetaDuracionMs = new JLabel();
        this.duracionNs = new JSlider();
        this.etiquetaDuracionNs = new JLabel();
        this.jLabel4 = new JLabel();
        this.pasoNs = new JSlider();
        this.etiquetaPasoNs = new JLabel();

        this.diseElementoPopUp.setFont(new Font("Dialog", 0, 12));
        this.dEliminarMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dEliminarMenuItem.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.Delete").charAt(0));
        this.dEliminarMenuItem.setText(this.translations.getString("VentanaHija.PopUpDisenio.Delete"));
        this.dEliminarMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioEliminar(evt);
            }
        });

        this.diseElementoPopUp.add(this.dEliminarMenuItem);

        this.dVerNombreMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dVerNombreMenuItem.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.verNombre").charAt(0));
        this.dVerNombreMenuItem.setText(this.translations.getString("VentanaHija.PopUpDisenio.verNombre"));
        this.dVerNombreMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioVerNombre(evt);
            }
        });

        this.diseElementoPopUp.add(this.dVerNombreMenuItem);

        this.diseElementoPopUp.add(this.jSeparator1);

        this.dPropiedadesMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dPropiedadesMenuItem.setMnemonic(this.translations.getString("VentanaHija.PopUpDisenio.mne.Propiedades").charAt(0));
        this.dPropiedadesMenuItem.setText(this.translations.getString("VentanaHija.PopUpDisenio.Propiedades"));
        this.dPropiedadesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPropiedadesPopUpDisenioElemento(evt);
            }
        });

        this.diseElementoPopUp.add(this.dPropiedadesMenuItem);

        this.diseFondoPopUp.setFont(new Font("Dialog", 0, 12));
        this.dVerNombresNodosMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dVerNombresNodosMenuItem.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.verTodosNodos").charAt(0));
        this.dVerNombresNodosMenuItem.setText(this.translations.getString("popUpDisenioFondo.verTodosNodos"));
        this.dVerNombresNodosMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoVerNombreNodos(evt);
            }
        });

        this.diseFondoPopUp.add(this.dVerNombresNodosMenuItem);

        this.dOcultarNombresNodosMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dOcultarNombresNodosMenuItem.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.ocultarTodosNodos").charAt(0));
        this.dOcultarNombresNodosMenuItem.setText(this.translations.getString("popUpDisenioFondo.ocultarTodosNodos"));
        this.dOcultarNombresNodosMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoOcultarNombreNodos(evt);
            }
        });

        this.diseFondoPopUp.add(this.dOcultarNombresNodosMenuItem);

        this.dVerNombresEnlacesMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dVerNombresEnlacesMenuItem.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.verTodosEnlaces").charAt(0));
        this.dVerNombresEnlacesMenuItem.setText(this.translations.getString("popUpDisenioFondo.verTodosEnlaces"));
        this.dVerNombresEnlacesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoVerNombreEnlaces(evt);
            }
        });

        this.diseFondoPopUp.add(this.dVerNombresEnlacesMenuItem);

        this.dOcultarNombresEnlacesMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dOcultarNombresEnlacesMenuItem.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.ocultarTodosEnlaces").charAt(0));
        this.dOcultarNombresEnlacesMenuItem.setText(this.translations.getString("popUpDisenioFondo.ocultarTodosEnlaces"));
        this.dOcultarNombresEnlacesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoOcultarNombreEnlaces(evt);
            }
        });

        this.diseFondoPopUp.add(this.dOcultarNombresEnlacesMenuItem);

        this.diseFondoPopUp.add(this.jSeparator2);

        this.dEliminarTodoMenuItem.setFont(new Font("Dialog", 0, 12));
        this.dEliminarTodoMenuItem.setMnemonic(this.translations.getString("popUpDisenioFondo.mne.eliminarTodo").charAt(0));
        this.dEliminarTodoMenuItem.setText(this.translations.getString("popUpDisenioFondo.borrarTodo"));
        this.dEliminarTodoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnPopUpDisenioFondoEliminar(evt);
            }
        });

        this.diseFondoPopUp.add(this.dEliminarTodoMenuItem);

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
        this.jTabbedPane1.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.jTabbedPane1.setFont(new Font("Dialog", 0, 12));
        this.panelDisenioSuperior.setLayout(new BorderLayout());

        this.panelBotonesDisenio.setLayout(new FlowLayout(FlowLayout.LEFT));

        this.panelBotonesDisenio.setBorder(new EtchedBorder());
        this.iconoEmisor.setIcon(this.imageBroker.getIcon(TImageBroker.EMISOR_MENU));
        this.iconoEmisor.setToolTipText(this.translations.getString("VentanaHija.Topic.Emisor"));
        this.iconoEmisor.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesDisenio.add(this.iconoEmisor);

        this.iconoReceptor.setIcon(this.imageBroker.getIcon(TImageBroker.RECEPTOR_MENU));
        this.iconoReceptor.setToolTipText(this.translations.getString("VentanaHija.Topic.Receptor"));
        this.iconoReceptor.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesDisenio.add(this.iconoReceptor);

        this.iconoLER.setIcon(this.imageBroker.getIcon(TImageBroker.LER_MENU));
        this.iconoLER.setToolTipText(this.translations.getString("VentanaHija.Topic.LER"));
        this.iconoLER.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesDisenio.add(this.iconoLER);

        this.iconoLERA.setIcon(this.imageBroker.getIcon(TImageBroker.LERA_MENU));
        this.iconoLERA.setToolTipText(this.translations.getString("VentanaHija.Topic.LERActivo"));
        this.iconoLERA.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesDisenio.add(this.iconoLERA);

        this.iconoLSR.setIcon(this.imageBroker.getIcon(TImageBroker.LSR_MENU));
        this.iconoLSR.setToolTipText(this.translations.getString("VentanaHija.Topic.LSR"));
        this.iconoLSR.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesDisenio.add(iconoLSR);

        this.iconoLSRA.setIcon(this.imageBroker.getIcon(TImageBroker.LSRA_MENU));
        this.iconoLSRA.setToolTipText(this.translations.getString("VentanaHija.Topic.LSRActivo"));
        this.iconoLSRA.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesDisenio.add(this.iconoLSRA);

        this.iconoEnlace.setIcon(this.imageBroker.getIcon(TImageBroker.ENLACE_MENU));
        this.iconoEnlace.setToolTipText(this.translations.getString("VentanaHija.Topic.Enlace"));
        this.iconoEnlace.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesDisenio.add(this.iconoEnlace);

        this.panelDisenioSuperior.add(this.panelBotonesDisenio, BorderLayout.NORTH);

        this.jScrollPane1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.panelDisenio.setLayout(null);

        this.panelDisenio.setBackground(Color.white);
        this.panelDisenio.setBorder(new EtchedBorder());
        this.panelDisenio.addMouseListener(new MouseAdapter() {
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
        this.panelDisenio.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                arrastrandoEnPanelDisenio(evt);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                ratonSobrePanelDisenio(evt);
            }
        });

        this.jScrollPane1.setViewportView(this.panelDisenio);

        this.panelDisenioSuperior.add(this.jScrollPane1, BorderLayout.CENTER);

        this.jTabbedPane1.addTab(this.translations.getString("VentanaHija.Tab.Disenio"), this.imageBroker.getIcon(TImageBroker.DISENIO), this.panelDisenioSuperior, this.translations.getString("VentanaHija.A_panel_to_design_network_topology"));

        this.panelSimulacionSuperior.setLayout(new BorderLayout());

        this.panelBotonesSimulacion.setLayout(new FlowLayout(FlowLayout.LEFT));

        this.panelBotonesSimulacion.setBorder(new EtchedBorder());
        this.iconoComenzar.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_GENERAR));
        this.iconoComenzar.setToolTipText(this.translations.getString("VentanaHija.Topic.Generar"));
        this.iconoComenzar.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesSimulacion.add(this.iconoComenzar);

        this.iconoFinalizar.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PARAR));
        this.iconoFinalizar.setToolTipText(this.translations.getString("VentanaHija.Topic.Finalizar"));
        this.iconoFinalizar.setEnabled(false);
        this.iconoFinalizar.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesSimulacion.add(this.iconoFinalizar);

        this.iconoReanudar.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_COMENZAR));
        this.iconoReanudar.setToolTipText(this.translations.getString("VentanaHija.Topic.Simulacion"));
        this.iconoReanudar.setEnabled(false);
        this.iconoReanudar.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesSimulacion.add(this.iconoReanudar);

        this.iconoPausar.setIcon(this.imageBroker.getIcon(TImageBroker.BOTON_PAUSA));
        this.iconoPausar.setToolTipText(this.translations.getString("VentanaHija.Topic.Detener"));
        this.iconoPausar.setEnabled(false);
        this.iconoPausar.addMouseListener(new MouseAdapter() {
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

        this.panelBotonesSimulacion.add(this.iconoPausar);

        this.barraDeProgreso.setFont(new Font("Dialog", 0, 12));
        this.barraDeProgreso.setToolTipText(this.translations.getString("VentanaHija.BarraProgreso.tooltip"));
        this.barraDeProgreso.setStringPainted(true);
        this.panelBotonesSimulacion.add(this.barraDeProgreso);

        this.mlsPorTic.setMajorTickSpacing(10);
        this.mlsPorTic.setMaximum(500);
        this.mlsPorTic.setMinimum(1);
        this.mlsPorTic.setMinorTickSpacing(1);
        this.mlsPorTic.setSnapToTicks(true);
        this.mlsPorTic.setToolTipText(this.translations.getString("VentanaHija.Simulacion.SelectorDeVelocidad.tooltip"));
        this.mlsPorTic.setPreferredSize(new Dimension(100, 20));
        this.mlsPorTic.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                mlsPorTicCambiado(evt);
            }
        });

        this.panelBotonesSimulacion.add(this.mlsPorTic);

        this.etiquetaMlsPorTic.setFont(new Font("Dialog", 0, 10));
        this.etiquetaMlsPorTic.setForeground(new Color(102, 102, 102));
        this.panelBotonesSimulacion.add(this.etiquetaMlsPorTic);

        this.crearTraza.setText(this.translations.getString("JVentanaHija.Create_trace_file"));
        this.panelBotonesSimulacion.add(this.crearTraza);

        this.panelSimulacionSuperior.add(this.panelBotonesSimulacion, BorderLayout.NORTH);

        this.jScrollPane2.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.panelSimulacion.setBorder(new EtchedBorder());
        this.panelSimulacion.addMouseListener(new MouseAdapter() {
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
        this.panelSimulacion.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                ratonArrastradoEnPanelSimulacion(evt);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                ratonSobrePanelSimulacion(evt);
            }
        });

        this.jScrollPane2.setViewportView(this.panelSimulacion);

        this.panelSimulacionSuperior.add(this.jScrollPane2, BorderLayout.CENTER);

        this.jTabbedPane1.addTab(this.translations.getString("VentanaHija.Tab.Simulacion"), this.imageBroker.getIcon(TImageBroker.SIMULACION), this.panelSimulacionSuperior, this.translations.getString("VentanaHija.A_panel_to_generate_and_play_simulation."));

        this.panelAnalisisSuperior.setLayout(new BorderLayout());

        this.panelSeleccionElemento.setLayout(new FlowLayout(FlowLayout.LEFT));

        this.panelSeleccionElemento.setBorder(new EtchedBorder());
        this.jLabel1.setText(this.translations.getString("JVentanaHija.SelcUnElemParaVerDatos"));
        this.panelSeleccionElemento.add(this.jLabel1);

        this.selectorElementoEstadisticas.setModel(new DefaultComboBoxModel(new String[]{""}));
        this.selectorElementoEstadisticas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                clicEnSeleccionalElementoEstadistica(evt);
            }
        });

        this.panelSeleccionElemento.add(this.selectorElementoEstadisticas);

        this.panelAnalisisSuperior.add(this.panelSeleccionElemento, BorderLayout.NORTH);

        this.jScrollPane4.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.panelAnalisis.setLayout(new GridBagLayout());

        this.panelAnalisis.setBackground(new Color(252, 246, 226));
        this.panelFijo.setLayout(new GridBagLayout());

        this.panelFijo.setBackground(new Color(252, 246, 226));
        this.etiquetaEstadisticasTituloEscenario.setBackground(new Color(252, 246, 226));
        this.etiquetaEstadisticasTituloEscenario.setFont(new Font("Arial", 1, 18));
        this.etiquetaEstadisticasTituloEscenario.setHorizontalAlignment(SwingConstants.CENTER);
        this.etiquetaEstadisticasTituloEscenario.setText(this.translations.getString("JVentanaHija.TituloDelEscenario"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.panelFijo.add(this.etiquetaEstadisticasTituloEscenario, gridBagConstraints);

        this.etiquetaEstadisticasNombreAutor.setBackground(new Color(252, 246, 226));
        this.etiquetaEstadisticasNombreAutor.setFont(new Font("Arial", 1, 14));
        this.etiquetaEstadisticasNombreAutor.setForeground(new Color(102, 0, 51));
        this.etiquetaEstadisticasNombreAutor.setHorizontalAlignment(SwingConstants.CENTER);
        this.etiquetaEstadisticasNombreAutor.setText(this.translations.getString("JVentanaHija.AutorDelEscenario"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.panelFijo.add(this.etiquetaEstadisticasNombreAutor, gridBagConstraints);

        this.areaEstadisticasDescripcion.setBackground(new Color(252, 246, 226));
        this.areaEstadisticasDescripcion.setEditable(false);
        this.areaEstadisticasDescripcion.setFont(new Font("MonoSpaced", 0, 11));
        this.areaEstadisticasDescripcion.setLineWrap(true);
        this.areaEstadisticasDescripcion.setRows(3);
        this.areaEstadisticasDescripcion.setText(this.translations.getString("JVentanaHija.DescripcionDelEscenario"));
        this.areaEstadisticasDescripcion.setWrapStyleWord(true);
        this.areaEstadisticasDescripcion.setMinimumSize(new Dimension(500, 16));
        this.areaEstadisticasDescripcion.setPreferredSize(new Dimension(500, 48));
        this.areaEstadisticasDescripcion.setAutoscrolls(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.panelFijo.add(this.areaEstadisticasDescripcion, gridBagConstraints);

        this.etiquetaNombreElementoEstadistica.setBackground(new Color(252, 246, 226));
        this.etiquetaNombreElementoEstadistica.setFont(new Font("Arial", 1, 14));
        this.etiquetaNombreElementoEstadistica.setHorizontalAlignment(SwingConstants.CENTER);
        this.etiquetaNombreElementoEstadistica.setText(this.translations.getString("JVentanaHija.SeleccioneNodoAInspeccionar"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        this.panelFijo.add(this.etiquetaNombreElementoEstadistica, gridBagConstraints);

        this.panelAnalisis.add(this.panelFijo, new GridBagConstraints());

        this.jScrollPane4.setViewportView(this.panelAnalisis);

        this.panelAnalisisSuperior.add(this.jScrollPane4, BorderLayout.CENTER);

        this.jTabbedPane1.addTab(this.translations.getString("JVentanaHija.Analisis"), this.imageBroker.getIcon(TImageBroker.ANALISIS), this.panelAnalisisSuperior, this.translations.getString("JVentanaHija.Analisis.Tooltip"));

        this.panelOpcionesSuperior.setLayout(new BorderLayout());

        this.jScrollPane3.setBorder(null);
        this.panelOpciones.setLayout(new GridBagLayout());

        this.panelOpciones.setPreferredSize(new Dimension(380, 230));
        this.jPanel3.setLayout(new GridBagLayout());

        this.jPanel3.setBorder(new TitledBorder(null, this.translations.getString("VentanaHija.GParameters"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
        this.jLabel5.setFont(new Font("Dialog", 0, 12));
        this.jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
        this.jLabel5.setText(this.translations.getString("VentanaHija.Scene_title"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel3.add(this.jLabel5, gridBagConstraints);

        this.nombreEscenario.setToolTipText(this.translations.getString("VentanaHija.Type_a__title_of_the_scene"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel3.add(this.nombreEscenario, gridBagConstraints);

        this.jLabel6.setFont(new Font("Dialog", 0, 12));
        this.jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
        this.jLabel6.setText(this.translations.getString("VentanaHija.Scene_author"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel3.add(this.jLabel6, gridBagConstraints);

        this.nombreAutor.setToolTipText(this.translations.getString("VentanaHija.Type_de_name_of_the_author"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel3.add(this.nombreAutor, gridBagConstraints);

        this.jLabel7.setFont(new Font("Dialog", 0, 12));
        this.jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
        this.jLabel7.setText(this.translations.getString("VentanaHija.Description"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel3.add(this.jLabel7, gridBagConstraints);

        this.descripcionEscenario.setToolTipText(this.translations.getString("VentanaHija.Enter_a_short_description."));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel3.add(this.descripcionEscenario, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 350.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.panelOpciones.add(this.jPanel3, gridBagConstraints);

        this.jPanel2.setLayout(new GridBagLayout());

        this.jPanel2.setBorder(new TitledBorder(null, this.translations.getString("VentanaHija.TParameters"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
        this.jLabel3.setFont(new Font("Dialog", 0, 12));
        this.jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        this.jLabel3.setText(this.translations.getString("VentanaHija.Duration"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel2.add(this.jLabel3, gridBagConstraints);

        this.duracionMs.setMajorTickSpacing(2);
        this.duracionMs.setMaximum(2);
        this.duracionMs.setMinorTickSpacing(1);
        this.duracionMs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_ms._component_of_simulation_duration."));
        this.duracionMs.setValue(0);
        this.duracionMs.setMaximumSize(new Dimension(30, 20));
        this.duracionMs.setMinimumSize(new Dimension(30, 24));
        this.duracionMs.setPreferredSize(new Dimension(30, 20));
        this.duracionMs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                clicEnDuracionMs(evt);
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 150.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel2.add(this.duracionMs, gridBagConstraints);

        this.etiquetaDuracionMs.setFont(new Font("Dialog", 0, 10));
        this.etiquetaDuracionMs.setForeground(new Color(102, 102, 102));
        this.etiquetaDuracionMs.setText(this.translations.getString("VentanaHija.ms."));
        this.etiquetaDuracionMs.setMaximumSize(new Dimension(30, 14));
        this.etiquetaDuracionMs.setMinimumSize(new Dimension(30, 14));
        this.etiquetaDuracionMs.setPreferredSize(new Dimension(30, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 40.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel2.add(this.etiquetaDuracionMs, gridBagConstraints);

        this.duracionNs.setMajorTickSpacing(1000);
        this.duracionNs.setMaximum(999999);
        this.duracionNs.setMinorTickSpacing(100);
        this.duracionNs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_ns._component_of_simulation_duration."));
        this.duracionNs.setValue(100000);
        this.duracionNs.setMaximumSize(new Dimension(32767, 20));
        this.duracionNs.setMinimumSize(new Dimension(36, 20));
        this.duracionNs.setPreferredSize(new Dimension(200, 20));
        this.duracionNs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                clicEnDuracionNs(evt);
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 150.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel2.add(this.duracionNs, gridBagConstraints);

        this.etiquetaDuracionNs.setFont(new Font("Dialog", 0, 10));
        this.etiquetaDuracionNs.setForeground(new Color(102, 102, 102));
        this.etiquetaDuracionNs.setText(this.translations.getString("VentanaHija.ns."));
        this.etiquetaDuracionNs.setMaximumSize(new Dimension(40, 14));
        this.etiquetaDuracionNs.setMinimumSize(new Dimension(40, 14));
        this.etiquetaDuracionNs.setPreferredSize(new Dimension(40, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel2.add(this.etiquetaDuracionNs, gridBagConstraints);

        this.jLabel4.setFont(new Font("Dialog", 0, 12));
        this.jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
        this.jLabel4.setText(this.translations.getString("VentanaHija.Step"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel2.add(this.jLabel4, gridBagConstraints);

        this.pasoNs.setMajorTickSpacing(1000);
        this.pasoNs.setMaximum(999999);
        this.pasoNs.setMinimum(1);
        this.pasoNs.setMinorTickSpacing(100);
        this.pasoNs.setToolTipText(this.translations.getString("VentanaHija.Slide_it_to_change_the_step_duration_(ns).."));
        this.pasoNs.setValue(10000);
        this.pasoNs.setMaximumSize(new Dimension(32767, 20));
        this.pasoNs.setPreferredSize(new Dimension(100, 20));
        this.pasoNs.addChangeListener(new ChangeListener() {
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
        this.jPanel2.add(this.pasoNs, gridBagConstraints);

        this.etiquetaPasoNs.setFont(new Font("Dialog", 0, 10));
        this.etiquetaPasoNs.setForeground(new Color(102, 102, 102));
        this.etiquetaPasoNs.setText(this.translations.getString("VentanaHija.ns."));
        this.etiquetaPasoNs.setMaximumSize(new Dimension(40, 14));
        this.etiquetaPasoNs.setMinimumSize(new Dimension(40, 14));
        this.etiquetaPasoNs.setPreferredSize(new Dimension(40, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.jPanel2.add(this.etiquetaPasoNs, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 350.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.panelOpciones.add(this.jPanel2, gridBagConstraints);

        this.jScrollPane3.setViewportView(this.panelOpciones);

        this.panelOpcionesSuperior.add(this.jScrollPane3, BorderLayout.NORTH);

        this.jTabbedPane1.addTab(this.translations.getString("VentanaHija.Options"), imageBroker.getIcon(TImageBroker.OPCIONES), this.panelOpcionesSuperior, this.translations.getString("VentanaHija.Options_about_the_scene"));

        getContentPane().add(this.jTabbedPane1, BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void ratonPulsadoYSoltadoEnPanelSimulacion(MouseEvent evt) {//GEN-FIRST:event_ratonPulsadoYSoltadoEnPanelSimulacion
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopologyElement et = scenario.getTopology().getElementInScreenPosition(evt.getPoint());
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
            } else if (this.panelSimulacion.obtenerMostrarLeyenda()) {
                this.panelSimulacion.ponerMostrarLeyenda(false);
            } else {
                this.panelSimulacion.ponerMostrarLeyenda(true);
            }
        } else {
            elementoDisenioClicDerecho = null;
            panelDisenio.repaint();
        }
    }//GEN-LAST:event_ratonPulsadoYSoltadoEnPanelSimulacion

    private void clicEnSeleccionalElementoEstadistica(ActionEvent evt) {//GEN-FIRST:event_clicEnSeleccionalElementoEstadistica
        GridBagConstraints gbc = null;
        if (this.selectorElementoEstadisticas.getSelectedIndex() == 0) {
            this.panelAnalisis.removeAll();
            grafico1 = null;
            grafico2 = null;
            grafico3 = null;
            grafico4 = null;
            grafico5 = null;
            grafico6 = null;
            panelGrafico1 = null;
            panelGrafico2 = null;
            panelGrafico3 = null;
            panelGrafico4 = null;
            panelGrafico5 = null;
            panelGrafico6 = null;
            this.etiquetaEstadisticasTituloEscenario.setText(this.nombreEscenario.getText());
            this.etiquetaEstadisticasNombreAutor.setText(this.nombreAutor.getText());
            this.areaEstadisticasDescripcion.setText(this.descripcionEscenario.getText());
            this.etiquetaNombreElementoEstadistica.setIcon(null);
            this.etiquetaNombreElementoEstadistica.setText(this.translations.getString("JVentanaHija.SeleccioneElNodoAInspeccionar"));
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.panelFijo.add(this.etiquetaEstadisticasTituloEscenario, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.panelFijo.add(this.etiquetaEstadisticasNombreAutor, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.panelFijo.add(this.areaEstadisticasDescripcion, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.insets = new Insets(10, 5, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            this.panelFijo.add(this.etiquetaNombreElementoEstadistica, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            this.panelAnalisis.add(this.panelFijo, gbc);
            this.panelAnalisis.repaint();
        } else {
            String nombreEltoSeleccionado = (String) this.selectorElementoEstadisticas.getSelectedItem();
            this.crearEInsertarGraficas(nombreEltoSeleccionado);
        }
    }//GEN-LAST:event_clicEnSeleccionalElementoEstadistica

    /**
     * Este m�todo se llama cuando se arrastra el rat�n sobre el panel de
     * dise�o. Si se hace sobre un elemento que estaba seleccionado, el
     * resultado es que ese elemento se mueve donde vaya el cursor del rat�n.
     *
     * @param evt El evento que provoca la llamada.
     * @since 2.0
     */
    private void ratonArrastradoEnPanelSimulacion(MouseEvent evt) {//GEN-FIRST:event_ratonArrastradoEnPanelSimulacion
        if (evt.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (nodoSeleccionado != null) {
                TTopology topo = scenario.getTopology();
                Point p2 = evt.getPoint();
                if (p2.x < 0) {
                    p2.x = 0;
                }
                if (p2.x > panelDisenio.getSize().width) {
                    p2.x = panelDisenio.getSize().width;
                }
                if (p2.y < 0) {
                    p2.y = 0;
                }
                if (p2.y > panelDisenio.getSize().height) {
                    p2.y = panelDisenio.getSize().height;
                }
                nodoSeleccionado.setScreenPosition(new Point(p2.x, p2.y));
                panelSimulacion.repaint();
                this.scenario.setModified(true);
            }
        }
    }//GEN-LAST:event_ratonArrastradoEnPanelSimulacion

    /**
     * Este m�todo se llama cuando se libera un bot�n del rat�n estando en el
     * panel de simulaci�n. Si se hace sobre un elemento que estaba
     * seleccionado, deja de estarlo.
     *
     * @param evt El evento que genera la llamada.
     * @since 2.0
     */
    private void ratonSoltadoEnPanelSimulacion(MouseEvent evt) {//GEN-FIRST:event_ratonSoltadoEnPanelSimulacion
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (nodoSeleccionado != null) {
                nodoSeleccionado.setSelected(TNode.UNSELECTED);
                nodoSeleccionado = null;
                this.scenario.setModified(true);
            }
            panelSimulacion.repaint();
        }
    }//GEN-LAST:event_ratonSoltadoEnPanelSimulacion

    /**
     * Este m�todo se llama cuando se presiona un bot�n del rat�n en el panel de
     * simulaci�n. Si se hace sobre un elemento de la topolog�a, lo marca como
     * seleccionado.
     *
     * @since 2.0
     * @param evt El evento que provoca la llamada.
     */
    private void clicEnPanelSimulacion(MouseEvent evt) {//GEN-FIRST:event_clicEnPanelSimulacion
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopology topo = scenario.getTopology();
            TTopologyElement et = topo.getElementInScreenPosition(evt.getPoint());
            if (et != null) {
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (et.getElementType() == TTopologyElement.NODE) {
                    TNode nt = (TNode) et;
                    nodoSeleccionado = nt;
                    if (nodoSeleccionado != null) {
                        nodoSeleccionado.setSelected(TNode.SELECTED);
                        this.scenario.setModified(true);
                    }
                }
            } else {
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                this.setToolTipText(null);
            }
            panelSimulacion.repaint();
        }
    }//GEN-LAST:event_clicEnPanelSimulacion

    /**
     * Este m�todo se llama cuando se hace clic derecho sobre un elemento en la
     * ventana de dise�o y se selecciona la opci�n "Propiedades" del men�
     * emergente. Se encarga de mostrar en pantalla la ventana de configuraci�n
     * del elemento en cuesti�n.
     *
     * @since 2.0
     * @param evt El evento que provoca la llamada.
     */
    private void clicEnPropiedadesPopUpDisenioElemento(ActionEvent evt) {//GEN-FIRST:event_clicEnPropiedadesPopUpDisenioElemento
        if (elementoDisenioClicDerecho != null) {
            if (elementoDisenioClicDerecho.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) elementoDisenioClicDerecho;
                if (nt.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                    JTrafficGeneratorWindow ve = new JTrafficGeneratorWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
                    ve.setConfiguration((TTrafficGeneratorNode) nt, true);
                    ve.show();
                } else if (nt.getNodeType() == TNode.LER) {
                    JLERWindow vler = new JLERWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
                    vler.setConfiguration((TLERNode) nt, true);
                    vler.show();
                } else if (nt.getNodeType() == TNode.ACTIVE_LER) {
                    JActiveLERWindow vlera = new JActiveLERWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
                    vlera.setConfiguration((TActiveLERNode) nt, true);
                    vlera.show();
                } else if (nt.getNodeType() == TNode.LSR) {
                    JLSRWindow vlsr = new JLSRWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
                    vlsr.setConfiguration((TLSRNode) nt, true);
                    vlsr.show();
                } else if (nt.getNodeType() == TNode.ACTIVE_LSR) {
                    JActiveLSRWindow vlsra = new JActiveLSRWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
                    vlsra.setConfiguration((TActiveLSRNode) nt, true);
                    vlsra.show();
                } else if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                    JTrafficSinkWindow vr = new JTrafficSinkWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
                    vr.setConfiguration((TTrafficSinkNode) nt, true);
                    vr.show();
                }
                elementoDisenioClicDerecho = null;
                panelDisenio.repaint();
            } else {
                TLink ent = (TLink) elementoDisenioClicDerecho;
                TLinkConfig tceAux = ent.getConfig();
                JLinkWindow ve = new JLinkWindow(scenario.getTopology(), imageBroker, parent, true);
                ve.setConfiguration(tceAux, true);
                ve.show();
                if (ent.getLinkType() == TLink.EXTERNAL_LINK) {
                    TExternalLink ext = (TExternalLink) ent;
                    ext.configure(tceAux, this.scenario.getTopology(), true);
                } else if (ent.getLinkType() == TLink.INTERNAL_LINK) {
                    TInternalLink inte = (TInternalLink) ent;
                    inte.configure(tceAux, this.scenario.getTopology(), true);
                }
                elementoDisenioClicDerecho = null;
                panelDisenio.repaint();
                int minimoDelay = this.scenario.getTopology().getMinimumDelay();
                int pasoActual = this.pasoNs.getValue();
                if (pasoActual > minimoDelay) {
                    this.pasoNs.setValue(minimoDelay);
                }
            }
            this.scenario.setModified(true);
        }
    }//GEN-LAST:event_clicEnPropiedadesPopUpDisenioElemento

    /**
     * Este m�todo se encarga de controlar que la duraci�n de la simulaci�on y
     * del paso de la misma sea acorde con los delays de los enlaces. Adem�s se
     * encarga de la actualizaci�n de la interfaz en esos lugares.
     *
     * @since 2.0
     */
    public void controlarParametrosTemporales() {
        if (!controlTemporizacionDesactivado) {
            if (duracionMs.getValue() == 0) {
                duracionNs.setMinimum(1);
            } else {
                duracionNs.setMinimum(0);
            }
            int duracionTotal = duracionMs.getValue() * 1000000 + this.duracionNs.getValue();
            int minDelay = scenario.getTopology().getMinimumDelay();
            if (minDelay < duracionTotal) {
                pasoNs.setMaximum(minDelay);
            } else {
                pasoNs.setMaximum(duracionTotal);
            }
            this.etiquetaDuracionMs.setText(this.duracionMs.getValue() + this.translations.getString("VentanaHija._ms."));
            this.etiquetaDuracionNs.setText(this.duracionNs.getValue() + this.translations.getString("VentanaHija._ns."));
            this.etiquetaPasoNs.setText(this.pasoNs.getValue() + this.translations.getString("VentanaHija._ns."));
            scenario.getSimulation().setSimulationLengthInNs(new TTimestamp(duracionMs.getValue(), duracionNs.getValue()).getTotalAsNanoseconds());
            scenario.getSimulation().setSimulationStepLengthInNs(pasoNs.getValue());
        }
    }

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia la duraci�n en
     * nanosegundos del paso de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que el m�todo salte.
     */
    private void clicEnPasoNs(ChangeEvent evt) {//GEN-FIRST:event_clicEnPasoNs
        controlarParametrosTemporales();
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnPasoNs

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia la duraci�n de la
     * simulaci�n en nanosegundos.
     *
     * @since 2.0
     * @param evt Evento que hace que se ejecute este m�todo.
     */
    private void clicEnDuracionNs(ChangeEvent evt) {//GEN-FIRST:event_clicEnDuracionNs
        controlarParametrosTemporales();
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnDuracionNs

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia la duraci�n de la
     * simulaci�n en milisegundos.
     *
     * @since 2.0
     * @param evt Evento que produce que se ejecute este m�todo.
     */
    private void clicEnDuracionMs(ChangeEvent evt) {//GEN-FIRST:event_clicEnDuracionMs
        controlarParametrosTemporales();
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnDuracionMs

    /**
     * Este m�todo se llama autom�ticamente cuando se cambia el tiempo que se
     * detendr� la simulaci�n entre un paso de simulaci�n y otro.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void mlsPorTicCambiado(ChangeEvent evt) {//GEN-FIRST:event_mlsPorTicCambiado
        this.etiquetaMlsPorTic.setText(this.mlsPorTic.getValue() + this.translations.getString("VentanaHija.Simulacion.etiquetaMsTic"));
        panelSimulacion.ponerMlsPorTic(this.mlsPorTic.getValue());
    }//GEN-LAST:event_mlsPorTicCambiado

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ocultar el
     * nombre de todos los enlaces, en el men� emergente de la pantalla de
     * Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoOcultarNombreEnlaces(ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoOcultarNombreEnlaces
        Iterator it = scenario.getTopology().getLinksIterator();
        TLink enlaceAux;
        while (it.hasNext()) {
            enlaceAux = (TLink) it.next();
            enlaceAux.setShowName(false);
        }
        panelDisenio.repaint();
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnPopUpDisenioFondoOcultarNombreEnlaces

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ver el nombre
     * de todos los enlaces, en el men� emergente de la pantalla de Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoVerNombreEnlaces(ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoVerNombreEnlaces
        Iterator it = scenario.getTopology().getLinksIterator();
        TLink enlaceAux;
        while (it.hasNext()) {
            enlaceAux = (TLink) it.next();
            enlaceAux.setShowName(true);
        }
        panelDisenio.repaint();
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnPopUpDisenioFondoVerNombreEnlaces

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ocultar el
     * nombre de todos los nodos, en el men� emergente de la pantalla de
     * Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoOcultarNombreNodos(ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoOcultarNombreNodos
        Iterator it = scenario.getTopology().getNodesIterator();
        TNode nodoAux;
        while (it.hasNext()) {
            nodoAux = (TNode) it.next();
            nodoAux.setShowName(false);
        }
        panelDisenio.repaint();
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnPopUpDisenioFondoOcultarNombreNodos

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ver el nombre
     * de todos los nodos, en el men� emergente de la pantalla de Disenio.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoVerNombreNodos(ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoVerNombreNodos
        Iterator it = scenario.getTopology().getNodesIterator();
        TNode nodoAux;
        while (it.hasNext()) {
            nodoAux = (TNode) it.next();
            nodoAux.setShowName(true);
        }
        panelDisenio.repaint();
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnPopUpDisenioFondoVerNombreNodos

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de eliminar todo
     * el escenario completo, en el men� emergente de la pantalla de Disenio.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioFondoEliminar(ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoEliminar
        JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
        vb.setQuestion(this.translations.getString("JVentanaHija.PreguntaBorrarTodo"));
        vb.show();
        boolean respuesta = vb.getUserAnswer();
        if (respuesta) {
            scenario.getTopology().removeAllElements();
            panelDisenio.repaint();
        }
        this.scenario.setModified(true);
    }//GEN-LAST:event_clicEnPopUpDisenioFondoEliminar

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
        scenario = esc;
        panelDisenio.setTopology(esc.getTopology());
        panelSimulacion.ponerTopologia(esc.getTopology());
        nodoSeleccionado = null;
        elementoDisenioClicDerecho = null;
        aProgresoGeneracion = new TProgressEventListener(barraDeProgreso);
        try {
            esc.getTopology().getTimer().addProgressEventListener(aProgresoGeneracion);
        } catch (EProgressEventGeneratorOnlyAllowASingleListener e) {
            e.printStackTrace();
        }
        this.duracionMs.setValue((int) (durac / 1000000));
        this.duracionNs.setValue((int) (durac - (this.duracionMs.getValue() * 1000000)));
        this.pasoNs.setMaximum((int) esc.getSimulation().getSimulationLengthInNs());
        this.pasoNs.setValue((int) pas);
        esc.getSimulation().setSimulationLengthInNs(durac);
        esc.getSimulation().setSimulationStepLengthInNs(pas);
        this.etiquetaMlsPorTic.setText(this.mlsPorTic.getValue() + this.translations.getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.etiquetaDuracionMs.setText(this.duracionMs.getValue() + this.translations.getString("VentanaHija._ms."));
        this.etiquetaDuracionNs.setText(this.duracionNs.getValue() + this.translations.getString("VentanaHija._ns."));
        this.etiquetaPasoNs.setText(this.pasoNs.getValue() + this.translations.getString("VentanaHija_ns."));
        this.nombreAutor.setText(esc.getAuthor());
        this.nombreAutor.setCaretPosition(1);
        this.nombreEscenario.setText(esc.getTitle());
        this.nombreEscenario.setCaretPosition(1);
        this.descripcionEscenario.setText(esc.getDescription());
        this.descripcionEscenario.setCaretPosition(1);
        this.controlTemporizacionDesactivado = false;
        scenario.getSimulation().setSimulationPanel(this.panelSimulacion);
        this.controlarParametrosTemporales();
    }

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un
     * enlace nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirEnlace(MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirEnlace
        if (scenario.getTopology().getNumberOfNodes() < 2) {
            JWarningWindow va = new JWarningWindow(parent, true, imageBroker);
            va.setWarningMessage(this.translations.getString("VentanaHija.ErrorAlMenosDosNodos"));
            va.show();
        } else {
            TLinkConfig config = new TLinkConfig();
            JLinkWindow venlace = new JLinkWindow(scenario.getTopology(), imageBroker, parent, true);
            venlace.setConfiguration(config, false);
            //venlace.loadAllNodesThatHaveAvailablePorts();
            venlace.show();
            if (config.isWellConfigured()) {
                try {
                    if (config.getLinkType() == TLink.INTERNAL_LINK) {
                        TInternalLink enlaceInterno = new TInternalLink(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
                        enlaceInterno.configure(config, scenario.getTopology(), false);
                        scenario.getTopology().addLink(enlaceInterno);
                    } else {
                        TExternalLink enlaceExterno = new TExternalLink(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
                        enlaceExterno.configure(config, scenario.getTopology(), false);
                        scenario.getTopology().addLink(enlaceExterno);
                    }
                    panelDisenio.repaint();
                } catch (Exception e) {
                    JErrorWindow err;
                    err = new JErrorWindow(parent, true, imageBroker);
                    err.setErrorMessage(e.toString());
                    err.show();
                };
                this.scenario.setModified(true);
            } else {
                config = null;
            }
        }
    }//GEN-LAST:event_clicEnAniadirEnlace

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n eliminar que
     * aparece en el men� emergente al pulsar con el bot�n derecho sobre un
     * elemento de la topolog�a. En la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioEliminar(ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioEliminar
        JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
        vb.setQuestion(this.translations.getString("JVentanaHija.preguntaAlEliminar"));
        vb.show();
        boolean respuesta = vb.getUserAnswer();
        if (respuesta) {
            if (elementoDisenioClicDerecho != null) {
                if (elementoDisenioClicDerecho.getElementType() == TTopologyElement.NODE) {
                    TNode nt = (TNode) elementoDisenioClicDerecho;
                    if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                        if (this.scenario.getTopology().isThereAnyNodeGeneratingTrafficFor((TTrafficSinkNode) nt)) {
                            JWarningWindow va;
                            va = new JWarningWindow(parent, true, imageBroker);
                            va.setWarningMessage(this.translations.getString("JVentanaHija.NoPuedoBorrarReceptor"));
                            va.show();
                            elementoDisenioClicDerecho = null;
                        } else {
                            scenario.getTopology().disconnectNodeAndRemove(nt);
                            elementoDisenioClicDerecho = null;
                            panelDisenio.repaint();
                        }
                    } else {
                        scenario.getTopology().disconnectNodeAndRemove(nt);
                        elementoDisenioClicDerecho = null;
                        panelDisenio.repaint();
                    }
                } else {
                    TLink ent = (TLink) elementoDisenioClicDerecho;
                    scenario.getTopology().removeLink(ent);
                    elementoDisenioClicDerecho = null;
                    panelDisenio.repaint();
                }
                this.scenario.setModified(true);
            }
        }

    }//GEN-LAST:event_clicEnPopUpDisenioEliminar

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de ver/ocultar
     * nombre que aparece en el men� emergente al pulsar con el bot�n derecho
     * sobre un elemento de la topolog�a. En la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnPopUpDisenioVerNombre(ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioVerNombre
        if (elementoDisenioClicDerecho != null) {
            if (elementoDisenioClicDerecho.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) elementoDisenioClicDerecho;
                nt.setShowName(dVerNombreMenuItem.isSelected());
                elementoDisenioClicDerecho = null;
                panelDisenio.repaint();
            } else {
                TLink ent = (TLink) elementoDisenioClicDerecho;
                ent.setShowName(dVerNombreMenuItem.isSelected());
                elementoDisenioClicDerecho = null;
                panelDisenio.repaint();
            }
            this.scenario.setModified(true);
        }
    }//GEN-LAST:event_clicEnPopUpDisenioVerNombre

    /**
     * Este m�todo se ejecuta cuando se hace clic con el bot�n derecho en la
     * pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que este m�todo se dispare.
     */
    private void clicDerechoEnPanelDisenio(MouseEvent evt) {//GEN-FIRST:event_clicDerechoEnPanelDisenio
        if (evt.getButton() == MouseEvent.BUTTON3) {
            TTopologyElement et = scenario.getTopology().getElementInScreenPosition(evt.getPoint());
            if (et == null) {
                diseFondoPopUp.show(this, evt.getX() + 7, evt.getY() - 27);
            } else if (et.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) et;
                dVerNombreMenuItem.setSelected(nt.getShowName());
                elementoDisenioClicDerecho = et;
                diseElementoPopUp.show(this, evt.getX() + 7, evt.getY() + 15);
            } else if (et.getElementType() == TTopologyElement.LINK) {
                TLink ent = (TLink) et;
                dVerNombreMenuItem.setSelected(ent.getShowName());
                elementoDisenioClicDerecho = et;
                diseElementoPopUp.show(this, evt.getX() + 7, evt.getY() + 15);
            }
        } else {
            elementoDisenioClicDerecho = null;
            panelDisenio.repaint();
        }
    }//GEN-LAST:event_clicDerechoEnPanelDisenio

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LSRA
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirLSRA(MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLSRA
        TActiveLSRNode lsra = null;
        try {
            lsra = new TActiveLSRNode(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(parent, true, imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JActiveLSRWindow vlsra = new JActiveLSRWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
        vlsra.setConfiguration(lsra, false);
        vlsra.show();
        if (lsra.isWellConfigured()) {
            try {
                scenario.getTopology().addNode(lsra);
                panelDisenio.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(parent, true, imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            };
            this.scenario.setModified(true);
        } else {
            lsra = null;
        }
    }//GEN-LAST:event_clicEnAniadirLSRA

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LSR
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que este m�todo se dispare.
     */
    private void clicEnAniadirLSR(MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLSR
        TLSRNode lsr = null;
        try {
            lsr = new TLSRNode(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(parent, true, imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JLSRWindow vlsr = new JLSRWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
        vlsr.setConfiguration(lsr, false);
        vlsr.show();
        if (lsr.isWellConfigured()) {
            try {
                scenario.getTopology().addNode(lsr);
                panelDisenio.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(parent, true, imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            };
            this.scenario.setModified(true);
        } else {
            lsr = null;
        }
    }//GEN-LAST:event_clicEnAniadirLSR

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LSRA
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirLERA(MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLERA
        TActiveLERNode lera = null;
        try {
            lera = new TActiveLERNode(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(parent, true, imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JActiveLERWindow vlera = new JActiveLERWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
        vlera.setConfiguration(lera, false);
        vlera.show();
        if (lera.isWellConfigured()) {
            try {
                scenario.getTopology().addNode(lera);
                panelDisenio.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(parent, true, imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            };
            this.scenario.setModified(true);
        } else {
            lera = null;
        }
    }//GEN-LAST:event_clicEnAniadirLERA

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
    private void ratonSobrePanelSimulacion(MouseEvent evt) {//GEN-FIRST:event_ratonSobrePanelSimulacion
        TTopology topo = scenario.getTopology();
        TTopologyElement et = topo.getElementInScreenPosition(evt.getPoint());
        if (et != null) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (et.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) et;
                if (nt.getPorts().isArtificiallyCongested()) {
                    panelSimulacion.setToolTipText(this.translations.getString("JVentanaHija.Congestion") + nt.getPorts().getCongestionLevel() + this.translations.getString("JVentanaHija.POrcentaje") + this.translations.getString("VentanaHija.paraDejarDeCongestionar"));
                } else {
                    panelSimulacion.setToolTipText(this.translations.getString("JVentanaHija.Congestion") + nt.getPorts().getCongestionLevel() + this.translations.getString("JVentanaHija.POrcentaje") + this.translations.getString("VentanaHija.paraCongestionar"));
                }
            } else if (et.getElementType() == TTopologyElement.LINK) {
                TLink ent = (TLink) et;
                if (ent.isBroken()) {
                    panelSimulacion.setToolTipText(this.translations.getString("JVentanaHija.EnlaceRoto"));
                } else {
                    panelSimulacion.setToolTipText(this.translations.getString("JVentanaHija.EnlaceFuncionando"));
                }
            }
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            this.panelSimulacion.setToolTipText(null);
            if (!this.panelSimulacion.obtenerMostrarLeyenda()) {
                this.panelSimulacion.setToolTipText(this.translations.getString("JVentanaHija.VerLeyenda"));
            } else {
                this.panelSimulacion.setToolTipText(this.translations.getString("JVentanaHija.OcultarLeyenda"));
            }
        }
    }//GEN-LAST:event_ratonSobrePanelSimulacion

    /**
     * Este m�todo se ejecuta cuando se mueve el rat�n dentro del �rea de
     * dise�o, en la pantalla de Dise�o. Entre otras cosas, cambia el cursor del
     * rat�n al pasar sobre un elemento, permite mostrar men�s emergentes
     * coherentes con el contexto de donde se encuentra el rat�n, etc�tera.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSobrePanelDisenio(MouseEvent evt) {//GEN-FIRST:event_ratonSobrePanelDisenio
        TTopology topo = scenario.getTopology();
        TTopologyElement et = topo.getElementInScreenPosition(evt.getPoint());
        if (et != null) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (et.getElementType() == TTopologyElement.NODE) {
                TNode nt = (TNode) et;
                panelDisenio.setToolTipText(this.translations.getString("JVentanaHija.PanelDisenio.IP") + nt.getIPv4Address());
            } else if (et.getElementType() == TTopologyElement.LINK) {
                TLink ent = (TLink) et;
                panelDisenio.setToolTipText(this.translations.getString("JVentanaHija.panelDisenio.Retardo") + ent.getDelay() + this.translations.getString("JVentanaHija.panelDisenio.ns"));
            }
        } else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            panelDisenio.setToolTipText(null);
        }
    }//GEN-LAST:event_ratonSobrePanelDisenio

    /**
     * Este m�todo se llama autom�ticamente cuando se est� arrastrando el rat�n
     * en la pantalla de dise�o. Se encarga de mover los elementos de un lugar a
     * otro para dise�ar la topolog�a.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void arrastrandoEnPanelDisenio(MouseEvent evt) {//GEN-FIRST:event_arrastrandoEnPanelDisenio
        if (evt.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            if (nodoSeleccionado != null) {
                TTopology topo = scenario.getTopology();
                Point p2 = evt.getPoint();
                if (p2.x < 0) {
                    p2.x = 0;
                }
                if (p2.x > panelDisenio.getSize().width) {
                    p2.x = panelDisenio.getSize().width;
                }
                if (p2.y < 0) {
                    p2.y = 0;
                }
                if (p2.y > panelDisenio.getSize().height) {
                    p2.y = panelDisenio.getSize().height;
                }
                nodoSeleccionado.setScreenPosition(new Point(p2.x, p2.y));
                panelDisenio.repaint();
                this.scenario.setModified(true);
            }
        }
    }//GEN-LAST:event_arrastrandoEnPanelDisenio

    /**
     * Este m�todo se llama autom�ticamente cuando soltamos el bot�n del raton a
     * la rrastrar o al hacer clic. Si el rat�n estaba sobre un elemento de la
     * topology, se marca �ste como no seleccionado.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicSoltadoEnPanelDisenio(MouseEvent evt) {//GEN-FIRST:event_clicSoltadoEnPanelDisenio
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (nodoSeleccionado != null) {
                nodoSeleccionado.setSelected(TNode.UNSELECTED);
                nodoSeleccionado = null;
                this.scenario.setModified(true);
            }
            panelDisenio.repaint();
        }
    }//GEN-LAST:event_clicSoltadoEnPanelDisenio

    /**
     * Este m�todo se llama autom�ticamente cuando se hace un clic con el bot�n
     * izquierdo sobre la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnPanelDisenio(MouseEvent evt) {//GEN-FIRST:event_clicEnPanelDisenio
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopology topo = scenario.getTopology();
            nodoSeleccionado = topo.getNodeInScreenPosition(evt.getPoint());
            if (nodoSeleccionado != null) {
                nodoSeleccionado.setSelected(TNode.SELECTED);
                this.scenario.setModified(true);
            }
            panelDisenio.repaint();
        }
    }//GEN-LAST:event_clicEnPanelDisenio

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono de
     * detener en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoPausar(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoPausar
        iconoPausar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_PAUSA));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDelIconoPausar

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono de
     * detener en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoPausar(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoPausar
        iconoPausar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_PAUSA_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoPausar

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono de
     * finalizar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoFinalizar(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoFinalizar
        iconoFinalizar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_PARAR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDelIconoFinalizar

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono de
     * finalizar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoFinalizar(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoFinalizar
        iconoFinalizar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_PARAR_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoFinalizar

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono de
     * comenzar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoReanudar(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoReanudar
        iconoReanudar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_COMENZAR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDelIconoReanudar

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono de
     * comenzar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoReanudar(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoReanudar
        iconoReanudar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_COMENZAR_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoReanudar

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * generar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDelIconoComenzar(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoComenzar
        iconoComenzar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_GENERAR));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDelIconoComenzar

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * generar en la pantalla de simulaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoComenzar(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoComenzar
        iconoComenzar.setIcon(imageBroker.getIcon(TImageBroker.BOTON_GENERAR_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoComenzar

    /**
     * Este m�todo se ejecuta cuando se hace clic en la opci�n de a�adir un LER
     * nuevo en la barra de herramientas de la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt Evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirLER(MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLER
        TLERNode ler = null;
        try {
            ler = new TLERNode(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(parent, true, imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JLERWindow vler = new JLERWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
        vler.setConfiguration(ler, false);
        vler.show();
        if (ler.isWellConfigured()) {
            try {
                scenario.getTopology().addNode(ler);
                panelDisenio.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(parent, true, imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            };
            this.scenario.setModified(true);
        } else {
            ler = null;
        }
    }//GEN-LAST:event_clicEnAniadirLER

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * enlace en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo
     */
    private void ratonSaleDeIconoEnlace(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoEnlace
        iconoEnlace.setIcon(imageBroker.getIcon(TImageBroker.ENLACE_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDeIconoEnlace

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * enlace en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoEnlace(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoEnlace
        iconoEnlace.setIcon(imageBroker.getIcon(TImageBroker.ENLACE_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoEnlace

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LSRA
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLSRA(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLSRA
        iconoLSRA.setIcon(imageBroker.getIcon(TImageBroker.LSRA_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDeIconoLSRA

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LSRA en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLSRA(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLSRA
        iconoLSRA.setIcon(imageBroker.getIcon(TImageBroker.LSRA_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoLSRA

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LSR
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLSR(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLSR
        iconoLSR.setIcon(imageBroker.getIcon(TImageBroker.LSR_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDeIconoLSR

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LSR en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLSR(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLSR
        iconoLSR.setIcon(imageBroker.getIcon(TImageBroker.LSR_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoLSR

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LERA
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLERA(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLERA
        iconoLERA.setIcon(imageBroker.getIcon(TImageBroker.LERA_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDeIconoLERA

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LERA en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLERA(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLERA
        iconoLERA.setIcon(imageBroker.getIcon(TImageBroker.LERA_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoLERA

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono LER
     * en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoLER(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLER
        iconoLER.setIcon(imageBroker.getIcon(TImageBroker.LER_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDeIconoLER

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * LER en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoLER(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLER
        iconoLER.setIcon(imageBroker.getIcon(TImageBroker.LER_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoLER

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * receptor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoReceptor(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoReceptor
        iconoReceptor.setIcon(imageBroker.getIcon(TImageBroker.RECEPTOR_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDeIconoReceptor

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * receptor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoReceptor(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoReceptor
        iconoReceptor.setIcon(imageBroker.getIcon(TImageBroker.RECEPTOR_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoReceptor

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n sale del icono
     * emisor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonSaleDeIconoEmisor(MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoEmisor
        iconoEmisor.setIcon(imageBroker.getIcon(TImageBroker.EMISOR_MENU));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratonSaleDeIconoEmisor

    /**
     * Este m�todo se llama autom�ticamente cuando el rat�n pasa por el icono
     * emisor en la pantalla de dise�o.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void ratonEntraEnIconoEmisor(MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoEmisor
        iconoEmisor.setIcon(imageBroker.getIcon(TImageBroker.EMISOR_MENU_BRILLO));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratonEntraEnIconoEmisor

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * receptor en la ventana de dise�o. A�ade un receptor nuevo en la topology.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirReceptor(MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirReceptor
        TTrafficSinkNode receptor = null;
        try {
            receptor = new TTrafficSinkNode(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
        } catch (Exception e) {
            JErrorWindow err;
            err = new JErrorWindow(parent, true, imageBroker);
            err.setErrorMessage(e.toString());
            err.show();
        }
        JTrafficSinkWindow vr = new JTrafficSinkWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
        vr.setConfiguration(receptor, false);
        vr.show();
        if (receptor.isWellConfigured()) {
            try {
                scenario.getTopology().addNode(receptor);
                panelDisenio.repaint();
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(parent, true, imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            };
            this.scenario.setModified(true);
        } else {
            receptor = null;
        }
    }//GEN-LAST:event_clicEnAniadirReceptor

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * emisor en la ventana de dise�o. A�ade un emisor nuevo en la topology.
     *
     * @since 2.0
     * @param evt El evento que hace que se dispare este m�todo.
     */
    private void clicEnAniadirEmisorDeTrafico(MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirEmisorDeTrafico
        TTopology t = scenario.getTopology();
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
            JWarningWindow va = new JWarningWindow(parent, true, imageBroker);
            va.setWarningMessage(this.translations.getString("JVentanaHija.NecesitaHaberUnReceptor"));
            va.show();
        } else {
            TTrafficGeneratorNode emisor = null;
            try {
                emisor = new TTrafficGeneratorNode(scenario.getTopology().getElementsIDGenerator().getNew(), scenario.getTopology().getIPv4AddressGenerator().obtenerIP(), scenario.getTopology().getEventIDGenerator(), scenario.getTopology());
            } catch (Exception e) {
                JErrorWindow err;
                err = new JErrorWindow(parent, true, imageBroker);
                err.setErrorMessage(e.toString());
                err.show();
            }
            JTrafficGeneratorWindow ve = new JTrafficGeneratorWindow(scenario.getTopology(), panelDisenio, imageBroker, parent, true);
            ve.setConfiguration(emisor, false);
            ve.show();
            if (emisor.isWellConfigured()) {
                try {
                    scenario.getTopology().addNode(emisor);
                    panelDisenio.repaint();
                } catch (Exception e) {
                    JErrorWindow err;
                    err = new JErrorWindow(parent, true, imageBroker);
                    err.setErrorMessage(e.toString());
                    err.show();
                };
                this.scenario.setModified(true);
            } else {
                emisor = null;
            }
        }
    }//GEN-LAST:event_clicEnAniadirEmisorDeTrafico

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * detener en la ventana de simulaci�n. Detiene la simulaci�n o su
     * generaci�n.
     *
     * @since 2.0
     * @param evt Evento que hace que este m�todo se dispare.
     */
    private void clicAlPausar(MouseEvent evt) {//GEN-FIRST:event_clicAlPausar
        if (iconoPausar.isEnabled()) {
            this.scenario.getTopology().getTimer().setPaused(true);
            activarOpcionesAlDetener();
        }
    }//GEN-LAST:event_clicAlPausar

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * finalizar en la ventana de simulaci�n. Detiene la simulaci�n por
     * completo.
     *
     * @since 2.0
     * @param evt El evento que hace que este m�todo se dispare.
     */
    private void clicEnFinalizar(MouseEvent evt) {//GEN-FIRST:event_clicEnFinalizar
        if (iconoFinalizar.isEnabled()) {
            this.scenario.getTopology().getTimer().reset();
            this.crearTraza.setEnabled(true);
            this.panelSimulacion.ponerFicheroTraza(null);
            activarOpcionesAlFinalizar();
        }
    }//GEN-LAST:event_clicEnFinalizar

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * comenzar en la ventana de simulaci�n. Inicia la simulaci�n.
     *
     * @since 2.0
     * @param evt El evento que hace que este m�todo se dispare.
     */
    private void clicEnReanudar(MouseEvent evt) {//GEN-FIRST:event_clicEnReanudar
        if (iconoReanudar.isEnabled()) {
            activarOpcionesAlComenzar();
            this.scenario.getTopology().getTimer().setPaused(false);
            this.scenario.getTopology().getTimer().restart();
        }
    }//GEN-LAST:event_clicEnReanudar

    /**
     * Este m�todo se llama autom�ticamente cuando se hace clic sobre el icono
     * generar en la ventana de simulaci�n. Crea la simulaci�n.
     *
     * @since 2.0
     * @param evt El evento que hace que este m�todo se dispare.
     */
    private void clicEnComenzar(MouseEvent evt) {//GEN-FIRST:event_clicEnComenzar
        if (iconoComenzar.isEnabled()) {
            scenario.reset();
            if (!scenario.getTopology().getTimer().isRunning()) {
                scenario.getTopology().getTimer().setFinishTimestamp(new TTimestamp(duracionMs.getValue(), duracionNs.getValue()));
            }
            scenario.getTopology().getTimer().setTick(pasoNs.getValue());
            crearListaElementosEstadistica();
            this.scenario.setModified(true);
            this.scenario.getTopology().getTimer().reset();
            panelSimulacion.reset();
            panelSimulacion.repaint();
            scenario.simulate();
            int minimoDelay = this.scenario.getTopology().getMinimumDelay();
            int pasoActual = this.pasoNs.getValue();
            if (pasoActual > minimoDelay) {
                this.pasoNs.setValue(minimoDelay);
            }
            this.crearTraza.setEnabled(false);
            this.panelSimulacion.ponerFicheroTraza(null);
            if (this.crearTraza.isSelected()) {
                if (this.scenario.getScenarioFile() != null) {
                    File fAux = new File(this.scenario.getScenarioFile().getPath() + ".txt");
                    this.panelSimulacion.ponerFicheroTraza(fAux);
                } else {
                    this.panelSimulacion.ponerFicheroTraza(new File(this.getTitle() + ".txt"));
                }
            } else {
                this.panelSimulacion.ponerFicheroTraza(null);
            }
            activarOpcionesTrasGenerar();
        }
    }//GEN-LAST:event_clicEnComenzar

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
        this.selectorElementoEstadisticas.removeAllItems();
        this.selectorElementoEstadisticas.addItem("");
        it = this.scenario.getTopology().getNodesIterator();
        while (it.hasNext()) {
            nt = (TNode) it.next();
            if (nt.isGeneratingStats()) {
                this.selectorElementoEstadisticas.addItem(nt.getName());
            }
        }
        this.selectorElementoEstadisticas.setSelectedIndex(0);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n est� detenida.
     *
     * @since 2.0
     */
    private void activarOpcionesAlDetener() {
        iconoComenzar.setEnabled(false);
        iconoReanudar.setEnabled(true);
        iconoFinalizar.setEnabled(true);
        iconoPausar.setEnabled(false);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n ha finalizado.
     *
     * @since 2.0
     */
    private void activarOpcionesAlFinalizar() {
        iconoComenzar.setEnabled(true);
        iconoReanudar.setEnabled(false);
        iconoFinalizar.setEnabled(false);
        iconoPausar.setEnabled(false);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n se acaba de generar.
     *
     * @since 2.0
     */
    private void activarOpcionesTrasGenerar() {
        iconoComenzar.setEnabled(false);
        iconoReanudar.setEnabled(false);
        iconoFinalizar.setEnabled(true);
        iconoPausar.setEnabled(true);
    }

    /**
     * Este m�todo modifica la interfaz para que las opciones que se muestran
     * sean acordes al momento en que la simulaci�n comienza.
     *
     * @since 2.0
     */
    private void activarOpcionesAlComenzar() {
        iconoComenzar.setEnabled(false);
        iconoReanudar.setEnabled(false);
        iconoFinalizar.setEnabled(true);
        iconoPausar.setEnabled(true);
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
            scenario.setScenarioFile(dialogoGuardar.getSelectedFile());
            this.scenario.setSaved(true);
            this.setTitle(this.scenario.getScenarioFile().getName());
            TOSMSaver almacenador = new TOSMSaver(scenario);
            JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
            vb.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
            vb.show();
            boolean conCRC = vb.getUserAnswer();
            boolean correcto = almacenador.save(scenario.getScenarioFile(), conCRC);
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
            panelSimulacion.reset();
            panelSimulacion.repaint();
            scenario.reset();
            if (!scenario.getTopology().getTimer().isRunning()) {
                scenario.getTopology().getTimer().setFinishTimestamp(new TTimestamp(duracionMs.getValue(), duracionNs.getValue()));
            }
            scenario.getTopology().getTimer().setTick(pasoNs.getValue());
            this.scenario.getTopology().getTimer().setPaused(false);
            activarOpcionesAlFinalizar();
        }

        if (!guardado) {
            JDecissionWindow vb = new JDecissionWindow(parent, true, imageBroker);
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
            JDecissionWindow vb = new JDecissionWindow(parent, true, imageBroker);
            vb.setQuestion(this.translations.getString("JVentanaHija.DialogoGuardar.CambiosSinguardar1") + " " + this.getTitle() + " " + this.translations.getString("JVentanaHija.DialogoGuardar.CambiosSinguardar2"));
            vb.show();
            boolean respuesta = vb.getUserAnswer();
            vb.dispose();
            if (respuesta) {
                TOSMSaver almacenador = new TOSMSaver(scenario);
                JDecissionWindow vb2 = new JDecissionWindow(this.parent, true, this.imageBroker);
                vb2.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
                vb2.show();
                boolean conCRC = vb2.getUserAnswer();
                boolean correcto = almacenador.save(scenario.getScenarioFile(), conCRC);
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
            TOSMSaver almacenador = new TOSMSaver(scenario);
            JDecissionWindow vb = new JDecissionWindow(this.parent, true, this.imageBroker);
            vb.setQuestion(this.translations.getString("JVentanaHija.PreguntaEmpotrarCRC"));
            vb.show();
            boolean conCRC = vb.getUserAnswer();
            boolean correcto = almacenador.save(scenario.getScenarioFile(), conCRC);
            if (correcto) {
                this.scenario.setModified(false);
                this.scenario.setSaved(true);
            }
            this.scenario.setModified(false);
            this.scenario.setSaved(true);
        }
    }

    private void crearEInsertarGraficas(String nombre) {
        GridBagConstraints gbc = null;
        this.panelAnalisis.removeAll();
        this.etiquetaEstadisticasTituloEscenario.setText(this.nombreEscenario.getText());
        this.etiquetaEstadisticasNombreAutor.setText(this.nombreAutor.getText());
        this.areaEstadisticasDescripcion.setText(this.descripcionEscenario.getText());
        this.etiquetaNombreElementoEstadistica.setText(nombre);
        TNode nt = this.scenario.getTopology().getFirstNodeNamed(nombre);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 5);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.panelFijo.add(this.etiquetaEstadisticasTituloEscenario, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.panelFijo.add(this.etiquetaEstadisticasNombreAutor, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.panelFijo.add(this.areaEstadisticasDescripcion, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.panelFijo.add(this.etiquetaNombreElementoEstadistica, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 5);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.panelAnalisis.add(this.panelFijo, gbc);
        if (nt != null) {
            if (nt.getNodeType() == TNode.TRAFFIC_GENERATOR) {
                this.etiquetaNombreElementoEstadistica.setIcon(this.imageBroker.getIcon(TImageBroker.EMISOR));
            } else if (nt.getNodeType() == TNode.TRAFFIC_SINK) {
                this.etiquetaNombreElementoEstadistica.setIcon(this.imageBroker.getIcon(TImageBroker.RECEPTOR));
            } else if (nt.getNodeType() == TNode.LER) {
                this.etiquetaNombreElementoEstadistica.setIcon(this.imageBroker.getIcon(TImageBroker.LER));
            } else if (nt.getNodeType() == TNode.ACTIVE_LER) {
                this.etiquetaNombreElementoEstadistica.setIcon(this.imageBroker.getIcon(TImageBroker.LERA));
            } else if (nt.getNodeType() == TNode.LSR) {
                this.etiquetaNombreElementoEstadistica.setIcon(this.imageBroker.getIcon(TImageBroker.LSR));
            } else if (nt.getNodeType() == TNode.ACTIVE_LSR) {
                this.etiquetaNombreElementoEstadistica.setIcon(this.imageBroker.getIcon(TImageBroker.LSRA));
            }

            int numeroGraficos = nt.getStats().getNumberOfAvailableDatasets();

            if (numeroGraficos > 0) {
                grafico1 = ChartFactory.createXYLineChart(nt.getStats().getTitleOfDataset1(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset1(),
                        PlotOrientation.VERTICAL,
                        true, true, true);
                grafico1.getPlot().setBackgroundPaint(Color.WHITE);
                grafico1.getPlot().setForegroundAlpha((float) 0.5);
                grafico1.getPlot().setOutlinePaint(new Color(14, 69, 125));
                grafico1.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                grafico1.setBackgroundPaint(new Color(210, 226, 242));
                grafico1.setBorderPaint(Color.BLACK);
                grafico1.getTitle().setPaint(new Color(79, 138, 198));
                this.panelGrafico1 = new ChartPanel(grafico1);
                panelGrafico1.setBorder(new LineBorder(Color.BLACK));
                panelGrafico1.setPreferredSize(new Dimension(600, 300));
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.insets = new Insets(10, 5, 10, 5);
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                this.panelAnalisis.add(panelGrafico1, gbc);
            }
            if (numeroGraficos > 1) {
                grafico2 = ChartFactory.createXYLineChart(nt.getStats().getTitleOfDataset2(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset2(),
                        PlotOrientation.VERTICAL,
                        true, true, true);
                grafico2.getPlot().setBackgroundPaint(Color.WHITE);
                grafico2.getPlot().setForegroundAlpha((float) 0.5);
                grafico2.getPlot().setOutlinePaint(new Color(14, 69, 125));
                grafico2.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                grafico2.setBackgroundPaint(new Color(210, 226, 242));
                grafico2.setBorderPaint(Color.BLACK);
                grafico2.getTitle().setPaint(new Color(79, 138, 198));
                this.panelGrafico2 = new ChartPanel(grafico2);
                panelGrafico2.setPreferredSize(new Dimension(600, 300));
                panelGrafico2.setBorder(new LineBorder(Color.BLACK));
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.insets = new Insets(10, 5, 10, 5);
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                this.panelAnalisis.add(panelGrafico2, gbc);
            }
            if (numeroGraficos > 2) {
                grafico3 = ChartFactory.createXYLineChart(nt.getStats().getTitleOfDataset3(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset3(),
                        PlotOrientation.VERTICAL,
                        true, true, true);
                grafico3.getPlot().setBackgroundPaint(Color.WHITE);
                grafico3.getPlot().setForegroundAlpha((float) 0.5);
                grafico3.getPlot().setOutlinePaint(new Color(14, 69, 125));
                grafico3.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                grafico3.setBackgroundPaint(new Color(210, 226, 242));
                grafico3.setBorderPaint(Color.BLACK);
                grafico3.getTitle().setPaint(new Color(79, 138, 198));
                this.panelGrafico3 = new ChartPanel(grafico3);
                panelGrafico3.setBorder(new LineBorder(Color.BLACK));
                panelGrafico3.setPreferredSize(new Dimension(600, 300));
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.insets = new Insets(10, 5, 10, 5);
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                this.panelAnalisis.add(panelGrafico3, gbc);
            }
            if (numeroGraficos > 3) {
                grafico4 = ChartFactory.createBarChart(nt.getStats().getTitleOfDataset4(),
                        TStats.DESCRIPTION,
                        TStats.NUMBER,
                        (DefaultCategoryDataset) nt.getStats().getDataset4(),
                        PlotOrientation.VERTICAL,
                        true, true, true);
                grafico4.getPlot().setBackgroundPaint(Color.WHITE);
                grafico4.getPlot().setForegroundAlpha((float) 0.5);
                grafico4.getPlot().setOutlinePaint(new Color(14, 69, 125));
                grafico4.setBackgroundPaint(new Color(210, 226, 242));
                grafico4.setBorderPaint(Color.BLACK);
                grafico4.getTitle().setPaint(new Color(79, 138, 198));
                this.panelGrafico4 = new ChartPanel(grafico4);
                panelGrafico4.setBorder(new LineBorder(Color.BLACK));
                panelGrafico4.setPreferredSize(new Dimension(600, 300));
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 4;
                gbc.insets = new Insets(10, 5, 10, 5);
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                this.panelAnalisis.add(panelGrafico4, gbc);
            }
            if (numeroGraficos > 4) {
                grafico5 = ChartFactory.createBarChart(nt.getStats().getTitleOfDataset5(),
                        TStats.DESCRIPTION,
                        TStats.NUMBER,
                        (DefaultCategoryDataset) nt.getStats().getDataset5(),
                        PlotOrientation.VERTICAL,
                        true, true, true);
                grafico5.getPlot().setBackgroundPaint(Color.WHITE);
                grafico5.getPlot().setForegroundAlpha((float) 0.5);
                grafico5.getPlot().setOutlinePaint(new Color(14, 69, 125));
                grafico5.setBackgroundPaint(new Color(210, 226, 242));
                grafico5.setBorderPaint(Color.BLACK);
                grafico5.getTitle().setPaint(new Color(79, 138, 198));
                this.panelGrafico5 = new ChartPanel(grafico5);
                panelGrafico5.setBorder(new LineBorder(Color.BLACK));
                panelGrafico5.setPreferredSize(new Dimension(600, 300));
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 5;
                gbc.insets = new Insets(10, 5, 10, 5);
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                this.panelAnalisis.add(panelGrafico5, gbc);
            }
            if (numeroGraficos > 5) {
                grafico6 = ChartFactory.createXYLineChart(nt.getStats().getTitleOfDataset6(),
                        TStats.TIME,
                        TStats.NUMBER_OF_PACKETS,
                        (XYSeriesCollection) nt.getStats().getDataset6(),
                        PlotOrientation.VERTICAL,
                        true, true, true);
                grafico6.getPlot().setBackgroundPaint(Color.WHITE);
                grafico6.getPlot().setForegroundAlpha((float) 0.5);
                grafico6.getPlot().setOutlinePaint(new Color(14, 69, 125));
                grafico6.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                grafico6.setBackgroundPaint(new Color(210, 226, 242));
                grafico6.setBorderPaint(Color.BLACK);
                grafico6.getTitle().setPaint(new Color(79, 138, 198));
                this.panelGrafico6 = new ChartPanel(grafico6);
                panelGrafico6.setBorder(new LineBorder(Color.BLACK));
                panelGrafico6.setPreferredSize(new Dimension(600, 300));
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 6;
                gbc.insets = new Insets(10, 5, 10, 10);
                gbc.anchor = GridBagConstraints.NORTH;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                this.panelAnalisis.add(panelGrafico6, gbc);
            }
        }
        this.panelAnalisis.repaint();
    }

    /**
     * Este m�todo se encarga de anotar los datos del escenario desde la
     * interfaz de usuario hasta los correspondientes atributos del objeto que
     * almacena el escenario.
     *
     * @since 2.0
     */
    private void anotarDatosDeEscenario() {
        this.scenario.setTitle(this.nombreEscenario.getText());
        this.scenario.setAuthor(this.nombreAutor.getText());
        this.scenario.setDescription(this.descripcionEscenario.getText());
    }

    /**
     * Este atributo es el objeto encargado de actualizar la barra de progreso
     * del escenario que se usa a la hora de generar la simulaci�n y a la hora
     * de ejecutarla.
     *
     * @since 2.0
     */
    private TProgressEventListener aProgresoGeneracion;
    /**
     * Este atributo contendr� todo el escenario completo de la simulaci�n:
     * topology, an�lisis y simulaci�n.
     *
     * @since 2.0
     */
    private TScenario scenario;
    /**
     * Este atributo contendr� en todo momento una referencia al nodo del
     * escenario que se est� arrastrando.
     *
     * @since 2.0
     */
    private TNode nodoSeleccionado;
    /**
     * Este atributo contendr� todas las im�genes de Open SimMPLS para poder
     * acceder a ellas de forma m�s r�pida y para no tener que cargar la misma
     * imagen en distintas instancias.
     *
     * @since 2.0
     */
    private TImageBroker imageBroker;
    /**
     * Este atributo es una referencia a la ventana padre que recoge dentro de
     * si a esta ventana hija.
     *
     * @since 2.0
     */
    private JOpenSimMPLS parent;
    /**
     * Este atributo contiene en todo momento un referencia al elemento de la
     * topolog�a (nodo o enlace) sobre el que se est� intentando abrir un men�
     * contextual (clic con el bot�n derecho).
     *
     * @since 2.0
     */
    private TTopologyElement elementoDisenioClicDerecho;

    private boolean controlTemporizacionDesactivado;

    private ChartPanel panelGrafico1;
    private ChartPanel panelGrafico2;
    private ChartPanel panelGrafico3;
    private ChartPanel panelGrafico4;
    private ChartPanel panelGrafico5;
    private ChartPanel panelGrafico6;
    private JFreeChart grafico1;
    private JFreeChart grafico2;
    private JFreeChart grafico3;
    private JFreeChart grafico4;
    private JFreeChart grafico5;
    private JFreeChart grafico6;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextArea areaEstadisticasDescripcion;
    private JProgressBar barraDeProgreso;
    private JCheckBox crearTraza;
    private JMenuItem dEliminarMenuItem;
    private JMenuItem dEliminarTodoMenuItem;
    private JMenuItem dOcultarNombresEnlacesMenuItem;
    private JMenuItem dOcultarNombresNodosMenuItem;
    private JMenuItem dPropiedadesMenuItem;
    private JCheckBoxMenuItem dVerNombreMenuItem;
    private JMenuItem dVerNombresEnlacesMenuItem;
    private JMenuItem dVerNombresNodosMenuItem;
    private JTextField descripcionEscenario;
    private JPopupMenu diseElementoPopUp;
    private JPopupMenu diseFondoPopUp;
    private JSlider duracionMs;
    private JSlider duracionNs;
    private JLabel etiquetaDuracionMs;
    private JLabel etiquetaDuracionNs;
    private JLabel etiquetaEstadisticasNombreAutor;
    private JLabel etiquetaEstadisticasTituloEscenario;
    private JLabel etiquetaMlsPorTic;
    private JLabel etiquetaNombreElementoEstadistica;
    private JLabel etiquetaPasoNs;
    private JLabel iconoComenzar;
    private JLabel iconoEmisor;
    private JLabel iconoEnlace;
    private JLabel iconoFinalizar;
    private JLabel iconoLER;
    private JLabel iconoLERA;
    private JLabel iconoLSR;
    private JLabel iconoLSRA;
    private JLabel iconoPausar;
    private JLabel iconoReanudar;
    private JLabel iconoReceptor;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JScrollPane jScrollPane4;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JTabbedPane jTabbedPane1;
    private JSlider mlsPorTic;
    private JTextField nombreAutor;
    private JTextField nombreEscenario;
    private JPanel panelAnalisis;
    private JPanel panelAnalisisSuperior;
    private JPanel panelBotonesDisenio;
    private JPanel panelBotonesSimulacion;
    private JDesignPanel panelDisenio;
    private JPanel panelDisenioSuperior;
    private JPanel panelFijo;
    private JPanel panelOpciones;
    private JPanel panelOpcionesSuperior;
    private JPanel panelSeleccionElemento;
    private JSimulationPanel panelSimulacion;
    private JPanel panelSimulacionSuperior;
    private JSlider pasoNs;
    private JComboBox selectorElementoEstadisticas;
    private ResourceBundle translations;
}
