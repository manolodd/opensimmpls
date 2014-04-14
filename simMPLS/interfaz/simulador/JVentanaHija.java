//**************************************************************************
// Nombre......: JVentanaHija.java
// Proyecto....: Open SimMPLS
// Descripción.: Implementación de una ventana hija que se creará cada vez
// ............: que se cree un escenario de simulación nuevo. En ella se
// ............: desarrolla toda la actividad en el proceso de creación, si-
// ............: mulación y análisis de resultados de un escenario concreto.
// Fecha.......: 19/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.interfaz.simulador;

import simMPLS.entradasalida.osm.*;
import simMPLS.interfaz.dialogos.*;
import simMPLS.interfaz.simulador.*;
import simMPLS.escenario.*;
import simMPLS.interfaz.utiles.*;
import simMPLS.utiles.*;
import simMPLS.electronica.reloj.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;

/**
 * Esta clase implementa una ventana que almacenará un escenario completo y dará
 * soporte a la simulación, análisis y diseño de la topologia.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class JVentanaHija extends javax.swing.JInternalFrame {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * JVentanaHija.
     * @since 1.0
     * @param padre Ventana padre dentro de la cual se va a ubicar este ventana hija.
     * @param di Dispensador de imágenes de donde se obtendrán todas las imágenes que se
     * necesiten.
     */
    public JVentanaHija(JSimulador padre, TDispensadorDeImagenes di) {
        dispensadorDeImagenes = di;
        VentanaPadre = padre;
        initComponents();
        initComponents2();
    }
    
    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de
     * JVentanaHija.
     * @since 1.0
     * @param titulo Título que deseamos que tenga la ventana hija. Se usará también para almacenar el
     * escenario en disco.
     * @param padre Ventana padre dentro de la cual se va a ubicar este ventana hija.
     * @param di Dispensador de imágenes de donde se obtendrán todas las imágenes que se
     * necesiten.
     */
    public JVentanaHija(JSimulador padre, TDispensadorDeImagenes di, java.lang.String titulo) {
        dispensadorDeImagenes = di;
        VentanaPadre = padre;
        initComponents();
        initComponents2();
        this.setTitle(titulo);
    }
    
    /**
     * Este método es el constructor de la clase. Crea una nueva instancia de
     * JVentanaHija y la inicializa con los valores de un nodo existente.
     * @param padre Ventana padre dentro de la cual se va a ubicar este ventana hija.
     * @param di Dispensador de imágenes de donde se obtendrán todas las imágenes que se
     * necesiten.
     * @param esc Escenario ya creado al que se va a asociar esta ventana hija y que contendrá un
     * escenario y todos sus datos.
     * @since 1.0
     */    
    public JVentanaHija(JSimulador padre, TDispensadorDeImagenes di, TEscenario esc) {
        dispensadorDeImagenes = di;
        VentanaPadre = padre;
        initComponents();
        initComponents2();
        escenario = esc;
    }
    
    /** Este método se encarga de iniciar los atributos de la clase que no hayan sido
     * aun iniciados por NetBeans.
     * @since 1.0
     */
    public void initComponents2() {
        panelDisenio.ponerDispensadorDeImagenes(dispensadorDeImagenes);
        panelSimulacion.ponerDispensadorDeImagenes(dispensadorDeImagenes);
        Dimension tamPantalla= VentanaPadre.getSize();
        this.setSize((tamPantalla.width/2), (tamPantalla.height/2));
        Dimension tamFrame=this.getSize();
        this.setLocation((tamPantalla.width-tamFrame.width)/2, (tamPantalla.height-tamFrame.height)/2);
        escenario = new TEscenario();
        panelDisenio.ponerTopologia(escenario.obtenerTopologia());
        panelSimulacion.ponerTopologia(escenario.obtenerTopologia());
        nodoSeleccionado = null;
        elementoDisenioClicDerecho = null;
        aProgresoGeneracion = new TActualizadorDeProgreso(barraDeProgreso);
        try {
            escenario.obtenerTopologia().obtenerReloj().addListenerProgreso(aProgresoGeneracion);
        } catch (EProgresoUnSoloSuscriptor e) {
            e.printStackTrace();
        }
        this.mlsPorTic.setValue(1);
        this.pasoNs.setMaximum(duracionMs.getValue()*1000000 + this.duracionNs.getValue());
        this.etiquetaMlsPorTic.setText(this.mlsPorTic.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Simulacion.EtiquetaMsTic"));
        this.etiquetaDuracionMs.setText(this.duracionMs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija._ms."));
        this.etiquetaDuracionNs.setText(this.duracionNs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija._ns."));
        this.etiquetaPasoNs.setText(this.pasoNs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija_ns."));
        controlTemporizacionDesactivado = false;
        escenario.ponerPanelSimulacion(this.panelSimulacion);
        panelGrafico1 = null;
        panelGrafico2 = null;
        panelGrafico3 = null;
        panelGrafico4 = null;
        panelGrafico5 = null;
        panelGrafico6 = null;
        grafico1 = null;
        grafico2 = null;
        grafico3 = null;
        grafico4 = null;
        grafico5 = null;
        grafico6 = null;
    }
    
    /** Este método es llamado desde el constructor para actualizar la mayor parte de
     * los atributos de la clase que tienen que ver con la interfaz de usuario. Es un
     * método creado por NetBeans automaticamente.
     * @since 1.0
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        diseElementoPopUp = new javax.swing.JPopupMenu();
        dEliminarMenuItem = new javax.swing.JMenuItem();
        dVerNombreMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        dPropiedadesMenuItem = new javax.swing.JMenuItem();
        diseFondoPopUp = new javax.swing.JPopupMenu();
        dVerNombresNodosMenuItem = new javax.swing.JMenuItem();
        dOcultarNombresNodosMenuItem = new javax.swing.JMenuItem();
        dVerNombresEnlacesMenuItem = new javax.swing.JMenuItem();
        dOcultarNombresEnlacesMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        dEliminarTodoMenuItem = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelDisenioSuperior = new javax.swing.JPanel();
        panelBotonesDisenio = new javax.swing.JPanel();
        iconoEmisor = new javax.swing.JLabel();
        iconoReceptor = new javax.swing.JLabel();
        iconoLER = new javax.swing.JLabel();
        iconoLERA = new javax.swing.JLabel();
        iconoLSR = new javax.swing.JLabel();
        iconoLSRA = new javax.swing.JLabel();
        iconoEnlace = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelDisenio = new simMPLS.interfaz.simulador.JPanelDisenio();
        panelSimulacionSuperior = new javax.swing.JPanel();
        panelBotonesSimulacion = new javax.swing.JPanel();
        iconoComenzar = new javax.swing.JLabel();
        iconoFinalizar = new javax.swing.JLabel();
        iconoReanudar = new javax.swing.JLabel();
        iconoPausar = new javax.swing.JLabel();
        barraDeProgreso = new javax.swing.JProgressBar();
        mlsPorTic = new javax.swing.JSlider();
        etiquetaMlsPorTic = new javax.swing.JLabel();
        crearTraza = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelSimulacion = new simMPLS.interfaz.simulador.JPanelSimulacion();
        panelAnalisisSuperior = new javax.swing.JPanel();
        panelSeleccionElemento = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        selectorElementoEstadisticas = new javax.swing.JComboBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        panelAnalisis = new javax.swing.JPanel();
        panelFijo = new javax.swing.JPanel();
        etiquetaEstadisticasTituloEscenario = new javax.swing.JLabel();
        etiquetaEstadisticasNombreAutor = new javax.swing.JLabel();
        areaEstadisticasDescripcion = new javax.swing.JTextArea();
        etiquetaNombreElementoEstadistica = new javax.swing.JLabel();
        panelOpcionesSuperior = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        panelOpciones = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        nombreEscenario = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        nombreAutor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        descripcionEscenario = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        duracionMs = new javax.swing.JSlider();
        etiquetaDuracionMs = new javax.swing.JLabel();
        duracionNs = new javax.swing.JSlider();
        etiquetaDuracionNs = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pasoNs = new javax.swing.JSlider();
        etiquetaPasoNs = new javax.swing.JLabel();

        diseElementoPopUp.setFont(new java.awt.Font("Dialog", 0, 12));
        dEliminarMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dEliminarMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.PopUpDisenio.mne.Delete").charAt(0));
        dEliminarMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.PopUpDisenio.Delete"));
        dEliminarMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPopUpDisenioEliminar(evt);
            }
        });

        diseElementoPopUp.add(dEliminarMenuItem);

        dVerNombreMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dVerNombreMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.PopUpDisenio.mne.verNombre").charAt(0));
        dVerNombreMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.PopUpDisenio.verNombre"));
        dVerNombreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPopUpDisenioVerNombre(evt);
            }
        });

        diseElementoPopUp.add(dVerNombreMenuItem);

        diseElementoPopUp.add(jSeparator1);

        dPropiedadesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dPropiedadesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.PopUpDisenio.mne.Propiedades").charAt(0));
        dPropiedadesMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.PopUpDisenio.Propiedades"));
        dPropiedadesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPropiedadesPopUpDisenioElemento(evt);
            }
        });

        diseElementoPopUp.add(dPropiedadesMenuItem);

        diseFondoPopUp.setFont(new java.awt.Font("Dialog", 0, 12));
        dVerNombresNodosMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dVerNombresNodosMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.mne.verTodosNodos").charAt(0));
        dVerNombresNodosMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.verTodosNodos"));
        dVerNombresNodosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPopUpDisenioFondoVerNombreNodos(evt);
            }
        });

        diseFondoPopUp.add(dVerNombresNodosMenuItem);

        dOcultarNombresNodosMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dOcultarNombresNodosMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.mne.ocultarTodosNodos").charAt(0));
        dOcultarNombresNodosMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.ocultarTodosNodos"));
        dOcultarNombresNodosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPopUpDisenioFondoOcultarNombreNodos(evt);
            }
        });

        diseFondoPopUp.add(dOcultarNombresNodosMenuItem);

        dVerNombresEnlacesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dVerNombresEnlacesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.mne.verTodosEnlaces").charAt(0));
        dVerNombresEnlacesMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.verTodosEnlaces"));
        dVerNombresEnlacesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPopUpDisenioFondoVerNombreEnlaces(evt);
            }
        });

        diseFondoPopUp.add(dVerNombresEnlacesMenuItem);

        dOcultarNombresEnlacesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dOcultarNombresEnlacesMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.mne.ocultarTodosEnlaces").charAt(0));
        dOcultarNombresEnlacesMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.ocultarTodosEnlaces"));
        dOcultarNombresEnlacesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPopUpDisenioFondoOcultarNombreEnlaces(evt);
            }
        });

        diseFondoPopUp.add(dOcultarNombresEnlacesMenuItem);

        diseFondoPopUp.add(jSeparator2);

        dEliminarTodoMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        dEliminarTodoMenuItem.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.mne.eliminarTodo").charAt(0));
        dEliminarTodoMenuItem.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("popUpDisenioFondo.borrarTodo"));
        dEliminarTodoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnPopUpDisenioFondoEliminar(evt);
            }
        });

        diseFondoPopUp.add(dEliminarTodoMenuItem);

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Titulo"));
        setFont(new java.awt.Font("Dialog", 0, 12));
        setFrameIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ICONO_VENTANA_INTERNA_MENU));
        setNormalBounds(new java.awt.Rectangle(10, 10, 100, 100));
        setPreferredSize(new java.awt.Dimension(100, 100));
        setVisible(true);
        setAutoscrolls(true);
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12));
        panelDisenioSuperior.setLayout(new java.awt.BorderLayout());

        panelBotonesDisenio.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        panelBotonesDisenio.setBorder(new javax.swing.border.EtchedBorder());
        iconoEmisor.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.EMISOR_MENU));
        iconoEmisor.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.Emisor"));
        iconoEmisor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoEmisor(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDeIconoEmisor(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnAniadirEmisorDeTrafico(evt);
            }
        });

        panelBotonesDisenio.add(iconoEmisor);

        iconoReceptor.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.RECEPTOR_MENU));
        iconoReceptor.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.Receptor"));
        iconoReceptor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoReceptor(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDeIconoReceptor(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnAniadirReceptor(evt);
            }
        });

        panelBotonesDisenio.add(iconoReceptor);

        iconoLER.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LER_MENU));
        iconoLER.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.LER"));
        iconoLER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoLER(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDeIconoLER(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnAniadirLER(evt);
            }
        });

        panelBotonesDisenio.add(iconoLER);

        iconoLERA.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LERA_MENU));
        iconoLERA.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.LERActivo"));
        iconoLERA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoLERA(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDeIconoLERA(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnAniadirLERA(evt);
            }
        });

        panelBotonesDisenio.add(iconoLERA);

        iconoLSR.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSR_MENU));
        iconoLSR.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.LSR"));
        iconoLSR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoLSR(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDeIconoLSR(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnAniadirLSR(evt);
            }
        });

        panelBotonesDisenio.add(iconoLSR);

        iconoLSRA.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSRA_MENU));
        iconoLSRA.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.LSRActivo"));
        iconoLSRA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoLSRA(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDeIconoLSRA(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnAniadirLSRA(evt);
            }
        });

        panelBotonesDisenio.add(iconoLSRA);

        iconoEnlace.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ENLACE_MENU));
        iconoEnlace.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.Enlace"));
        iconoEnlace.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clicEnAniadirEnlace(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoEnlace(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDeIconoEnlace(evt);
            }
        });

        panelBotonesDisenio.add(iconoEnlace);

        panelDisenioSuperior.add(panelBotonesDisenio, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelDisenio.setLayout(null);

        panelDisenio.setBackground(java.awt.Color.white);
        panelDisenio.setBorder(new javax.swing.border.EtchedBorder());
        panelDisenio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clicDerechoEnPanelDisenio(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnPanelDisenio(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clicSoltadoEnPanelDisenio(evt);
            }
        });
        panelDisenio.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                arrastrandoEnPanelDisenio(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ratonSobrePanelDisenio(evt);
            }
        });

        jScrollPane1.setViewportView(panelDisenio);

        panelDisenioSuperior.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Tab.Disenio"), dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.DISENIO), panelDisenioSuperior, java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.A_panel_to_design_network_topology"));

        panelSimulacionSuperior.setLayout(new java.awt.BorderLayout());

        panelBotonesSimulacion.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        panelBotonesSimulacion.setBorder(new javax.swing.border.EtchedBorder());
        iconoComenzar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_GENERAR));
        iconoComenzar.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.Generar"));
        iconoComenzar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoComenzar(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDelIconoComenzar(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnComenzar(evt);
            }
        });

        panelBotonesSimulacion.add(iconoComenzar);

        iconoFinalizar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_PARAR));
        iconoFinalizar.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.Finalizar"));
        iconoFinalizar.setEnabled(false);
        iconoFinalizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoFinalizar(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDelIconoFinalizar(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnFinalizar(evt);
            }
        });

        panelBotonesSimulacion.add(iconoFinalizar);

        iconoReanudar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_COMENZAR));
        iconoReanudar.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.Simulacion"));
        iconoReanudar.setEnabled(false);
        iconoReanudar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoReanudar(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDelIconoReanudar(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnReanudar(evt);
            }
        });

        panelBotonesSimulacion.add(iconoReanudar);

        iconoPausar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_PAUSA));
        iconoPausar.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Topic.Detener"));
        iconoPausar.setEnabled(false);
        iconoPausar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnIconoPausar(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDelIconoPausar(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicAlPausar(evt);
            }
        });

        panelBotonesSimulacion.add(iconoPausar);

        barraDeProgreso.setFont(new java.awt.Font("Dialog", 0, 12));
        barraDeProgreso.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.BarraProgreso.tooltip"));
        barraDeProgreso.setStringPainted(true);
        panelBotonesSimulacion.add(barraDeProgreso);

        mlsPorTic.setMajorTickSpacing(10);
        mlsPorTic.setMaximum(500);
        mlsPorTic.setMinimum(1);
        mlsPorTic.setMinorTickSpacing(1);
        mlsPorTic.setSnapToTicks(true);
        mlsPorTic.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Simulacion.SelectorDeVelocidad.tooltip"));
        mlsPorTic.setPreferredSize(new java.awt.Dimension(100, 20));
        mlsPorTic.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mlsPorTicCambiado(evt);
            }
        });

        panelBotonesSimulacion.add(mlsPorTic);

        etiquetaMlsPorTic.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaMlsPorTic.setForeground(new java.awt.Color(102, 102, 102));
        panelBotonesSimulacion.add(etiquetaMlsPorTic);

        crearTraza.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.Create_trace_file"));
        panelBotonesSimulacion.add(crearTraza);

        panelSimulacionSuperior.add(panelBotonesSimulacion, java.awt.BorderLayout.NORTH);

        jScrollPane2.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelSimulacion.setBorder(new javax.swing.border.EtchedBorder());
        panelSimulacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ratonPulsadoYSoltadoEnPanelSimulacion(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clicEnPanelSimulacion(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ratonSoltadoEnPanelSimulacion(evt);
            }
        });
        panelSimulacion.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                ratonArrastradoEnPanelSimulacion(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ratonSobrePanelSimulacion(evt);
            }
        });

        jScrollPane2.setViewportView(panelSimulacion);

        panelSimulacionSuperior.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Tab.Simulacion"), dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.SIMULACION), panelSimulacionSuperior, java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.A_panel_to_generate_and_play_simulation."));

        panelAnalisisSuperior.setLayout(new java.awt.BorderLayout());

        panelSeleccionElemento.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        panelSeleccionElemento.setBorder(new javax.swing.border.EtchedBorder());
        jLabel1.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.SelcUnElemParaVerDatos"));
        panelSeleccionElemento.add(jLabel1);

        selectorElementoEstadisticas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "" }));
        selectorElementoEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnSeleccionalElementoEstadistica(evt);
            }
        });

        panelSeleccionElemento.add(selectorElementoEstadisticas);

        panelAnalisisSuperior.add(panelSeleccionElemento, java.awt.BorderLayout.NORTH);

        jScrollPane4.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelAnalisis.setLayout(new java.awt.GridBagLayout());

        panelAnalisis.setBackground(new java.awt.Color(252, 246, 226));
        panelFijo.setLayout(new java.awt.GridBagLayout());

        panelFijo.setBackground(new java.awt.Color(252, 246, 226));
        etiquetaEstadisticasTituloEscenario.setBackground(new java.awt.Color(252, 246, 226));
        etiquetaEstadisticasTituloEscenario.setFont(new java.awt.Font("Arial", 1, 18));
        etiquetaEstadisticasTituloEscenario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaEstadisticasTituloEscenario.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.TituloDelEscenario"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelFijo.add(etiquetaEstadisticasTituloEscenario, gridBagConstraints);

        etiquetaEstadisticasNombreAutor.setBackground(new java.awt.Color(252, 246, 226));
        etiquetaEstadisticasNombreAutor.setFont(new java.awt.Font("Arial", 1, 14));
        etiquetaEstadisticasNombreAutor.setForeground(new java.awt.Color(102, 0, 51));
        etiquetaEstadisticasNombreAutor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaEstadisticasNombreAutor.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.AutorDelEscenario"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelFijo.add(etiquetaEstadisticasNombreAutor, gridBagConstraints);

        areaEstadisticasDescripcion.setBackground(new java.awt.Color(252, 246, 226));
        areaEstadisticasDescripcion.setEditable(false);
        areaEstadisticasDescripcion.setFont(new java.awt.Font("MonoSpaced", 0, 11));
        areaEstadisticasDescripcion.setLineWrap(true);
        areaEstadisticasDescripcion.setRows(3);
        areaEstadisticasDescripcion.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.DescripcionDelEscenario"));
        areaEstadisticasDescripcion.setWrapStyleWord(true);
        areaEstadisticasDescripcion.setMinimumSize(new java.awt.Dimension(500, 16));
        areaEstadisticasDescripcion.setPreferredSize(new java.awt.Dimension(500, 48));
        areaEstadisticasDescripcion.setAutoscrolls(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelFijo.add(areaEstadisticasDescripcion, gridBagConstraints);

        etiquetaNombreElementoEstadistica.setBackground(new java.awt.Color(252, 246, 226));
        etiquetaNombreElementoEstadistica.setFont(new java.awt.Font("Arial", 1, 14));
        etiquetaNombreElementoEstadistica.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaNombreElementoEstadistica.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.SeleccioneNodoAInspeccionar"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panelFijo.add(etiquetaNombreElementoEstadistica, gridBagConstraints);

        panelAnalisis.add(panelFijo, new java.awt.GridBagConstraints());

        jScrollPane4.setViewportView(panelAnalisis);

        panelAnalisisSuperior.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.Analisis"), dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ANALISIS), panelAnalisisSuperior, java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.Analisis.Tooltip"));

        panelOpcionesSuperior.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setBorder(null);
        panelOpciones.setLayout(new java.awt.GridBagLayout());

        panelOpciones.setPreferredSize(new java.awt.Dimension(380, 230));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(new javax.swing.border.TitledBorder(null, java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.GParameters"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Scene_title"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel5, gridBagConstraints);

        nombreEscenario.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Type_a__title_of_the_scene"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(nombreEscenario, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Scene_author"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel6, gridBagConstraints);

        nombreAutor.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Type_de_name_of_the_author"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(nombreAutor, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Description"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel7, gridBagConstraints);

        descripcionEscenario.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Enter_a_short_description."));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 200.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(descripcionEscenario, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 350.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelOpciones.add(jPanel3, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(new javax.swing.border.TitledBorder(null, java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.TParameters"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Duration"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel3, gridBagConstraints);

        duracionMs.setMajorTickSpacing(2);
        duracionMs.setMaximum(2);
        duracionMs.setMinorTickSpacing(1);
        duracionMs.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Slide_it_to_change_the_ms._component_of_simulation_duration."));
        duracionMs.setValue(0);
        duracionMs.setMaximumSize(new java.awt.Dimension(30, 20));
        duracionMs.setMinimumSize(new java.awt.Dimension(30, 24));
        duracionMs.setPreferredSize(new java.awt.Dimension(30, 20));
        duracionMs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                clicEnDuracionMs(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 150.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(duracionMs, gridBagConstraints);

        etiquetaDuracionMs.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaDuracionMs.setForeground(new java.awt.Color(102, 102, 102));
        etiquetaDuracionMs.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.ms."));
        etiquetaDuracionMs.setMaximumSize(new java.awt.Dimension(30, 14));
        etiquetaDuracionMs.setMinimumSize(new java.awt.Dimension(30, 14));
        etiquetaDuracionMs.setPreferredSize(new java.awt.Dimension(30, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 40.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(etiquetaDuracionMs, gridBagConstraints);

        duracionNs.setMajorTickSpacing(1000);
        duracionNs.setMaximum(999999);
        duracionNs.setMinorTickSpacing(100);
        duracionNs.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Slide_it_to_change_the_ns._component_of_simulation_duration."));
        duracionNs.setValue(100000);
        duracionNs.setMaximumSize(new java.awt.Dimension(32767, 20));
        duracionNs.setMinimumSize(new java.awt.Dimension(36, 20));
        duracionNs.setPreferredSize(new java.awt.Dimension(200, 20));
        duracionNs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                clicEnDuracionNs(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 150.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(duracionNs, gridBagConstraints);

        etiquetaDuracionNs.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaDuracionNs.setForeground(new java.awt.Color(102, 102, 102));
        etiquetaDuracionNs.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.ns."));
        etiquetaDuracionNs.setMaximumSize(new java.awt.Dimension(40, 14));
        etiquetaDuracionNs.setMinimumSize(new java.awt.Dimension(40, 14));
        etiquetaDuracionNs.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(etiquetaDuracionNs, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Step"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel4, gridBagConstraints);

        pasoNs.setMajorTickSpacing(1000);
        pasoNs.setMaximum(999999);
        pasoNs.setMinimum(1);
        pasoNs.setMinorTickSpacing(100);
        pasoNs.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Slide_it_to_change_the_step_duration_(ns).."));
        pasoNs.setValue(10000);
        pasoNs.setMaximumSize(new java.awt.Dimension(32767, 20));
        pasoNs.setPreferredSize(new java.awt.Dimension(100, 20));
        pasoNs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                clicEnPasoNs(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(pasoNs, gridBagConstraints);

        etiquetaPasoNs.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaPasoNs.setForeground(new java.awt.Color(102, 102, 102));
        etiquetaPasoNs.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.ns."));
        etiquetaPasoNs.setMaximumSize(new java.awt.Dimension(40, 14));
        etiquetaPasoNs.setMinimumSize(new java.awt.Dimension(40, 14));
        etiquetaPasoNs.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(etiquetaPasoNs, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 350.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelOpciones.add(jPanel2, gridBagConstraints);

        jScrollPane3.setViewportView(panelOpciones);

        panelOpcionesSuperior.add(jScrollPane3, java.awt.BorderLayout.NORTH);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Options"), dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.OPCIONES), panelOpcionesSuperior, java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Options_about_the_scene"));

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void ratonPulsadoYSoltadoEnPanelSimulacion(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonPulsadoYSoltadoEnPanelSimulacion
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TElementoTopologia et = escenario.obtenerTopologia().obtenerElementoEnPosicion(evt.getPoint());
            if (et != null) {
                if (et.obtenerTipoElemento() == TElementoTopologia.NODO) {
                    TNodoTopologia nt = (TNodoTopologia) et;
                    if (nt.obtenerPuertos().obtenerSaturadoArtificialmente()) {
                        nt.obtenerPuertos().ponerSaturadoArtificialmente(false);
                    } else {
                        nt.obtenerPuertos().ponerSaturadoArtificialmente(true);
                    }
                } else if (et.obtenerTipoElemento() == TElementoTopologia.ENLACE) {
                    TEnlaceTopologia ent = (TEnlaceTopologia) et;
                    if (ent.obtenerEnlaceCaido()) {
                        ent.ponerEnlaceCaido(false);
                    } else {
                        ent.ponerEnlaceCaido(true);
                    }
                }
            } else {
                    if (this.panelSimulacion.obtenerMostrarLeyenda()) {
                        this.panelSimulacion.ponerMostrarLeyenda(false);
                    } else {
                        this.panelSimulacion.ponerMostrarLeyenda(true);
                    }
            }
        } else {
            elementoDisenioClicDerecho = null;
            panelDisenio.repaint();
        }
    }//GEN-LAST:event_ratonPulsadoYSoltadoEnPanelSimulacion

    private void clicEnSeleccionalElementoEstadistica(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnSeleccionalElementoEstadistica
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
            this.etiquetaNombreElementoEstadistica.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.SeleccioneElNodoAInspeccionar"));
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            this.panelFijo.add(this.etiquetaEstadisticasTituloEscenario, gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            this.panelFijo.add(this.etiquetaEstadisticasNombreAutor, gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            this.panelFijo.add(this.areaEstadisticasDescripcion,gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.insets = new Insets(10, 5, 10, 10);
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            this.panelFijo.add(this.etiquetaNombreElementoEstadistica, gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            this.panelAnalisis.add(this.panelFijo, gbc);
            this.panelAnalisis.repaint();
        } else {
            String nombreEltoSeleccionado = (String) this.selectorElementoEstadisticas.getSelectedItem();
            this.crearEInsertarGraficas(nombreEltoSeleccionado);
        }
    }//GEN-LAST:event_clicEnSeleccionalElementoEstadistica

    /**
     * Este método se llama cuando se arrastra el ratón sobre el panel de diseño. Si se
     * hace sobre un elemento que estaba seleccionado, el resultado es que ese elemento
     * se mueve donde vaya el cursor del ratón.
     * @param evt El evento que provoca la llamada.
     * @since 1.0
     */    
    private void ratonArrastradoEnPanelSimulacion(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonArrastradoEnPanelSimulacion
    if (evt.getModifiersEx() == java.awt.event.InputEvent.BUTTON1_DOWN_MASK) {
            if (nodoSeleccionado != null) {
                TTopologia topo = escenario.obtenerTopologia();
                Point p2 = evt.getPoint();
                if (p2.x < 0)
                    p2.x = 0;
                if (p2.x > panelDisenio.getSize().width)
                    p2.x = panelDisenio.getSize().width;
                if (p2.y < 0)
                    p2.y = 0;
                if (p2.y > panelDisenio.getSize().height)
                    p2.y = panelDisenio.getSize().height;
                nodoSeleccionado.ponerPosicion(new Point(p2.x, p2.y));
                panelSimulacion.repaint();
                this.escenario.ponerModificado(true);
            }
        }
    }//GEN-LAST:event_ratonArrastradoEnPanelSimulacion

    /**
     * Este método se llama cuando se libera un botón del ratón estando en el panel de
     * simulación. Si se hace sobre un elemento que estaba seleccionado, deja de
     * estarlo.
     * @param evt El evento que genera la llamada.
     * @since 1.0
     */    
    private void ratonSoltadoEnPanelSimulacion(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSoltadoEnPanelSimulacion
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (nodoSeleccionado != null) {
                nodoSeleccionado.ponerEstado(TNodoTopologia.DESELECCIONADO);
                nodoSeleccionado = null;
                this.escenario.ponerModificado(true);
            }
            panelSimulacion.repaint();
        }
    }//GEN-LAST:event_ratonSoltadoEnPanelSimulacion

    /**
     * Este método se llama cuando se presiona un botón del ratón en el panel de
     * simulación. Si se hace sobre un elemento de la topología, lo marca como
     * seleccionado.
     * @since 1.0
     * @param evt El evento que provoca la llamada.
     */    
    private void clicEnPanelSimulacion(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnPanelSimulacion
        if (evt.getButton() == MouseEvent.BUTTON1) {
            TTopologia topo = escenario.obtenerTopologia();
            TElementoTopologia et = topo.obtenerElementoEnPosicion(evt.getPoint());
            if (et != null) {
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (et.obtenerTipoElemento() == TElementoTopologia.NODO) {
                    TNodoTopologia nt = (TNodoTopologia) et;
                    nodoSeleccionado = nt;
                    if (nodoSeleccionado != null) {
                        nodoSeleccionado.ponerEstado(TNodoTopologia.SELECCIONADO);
                        this.escenario.ponerModificado(true);
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
     * Este método se llama cuando se hace clic derecho sobre un elemento en la ventana
     * de diseño y se selecciona la opción "Propiedades" del menú emergente. Se encarga
     * de mostrar en pantalla la ventana de configuración del elemento en cuestión.
     * @since 1.0
     * @param evt El evento que provoca la llamada.
     */    
    private void clicEnPropiedadesPopUpDisenioElemento(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPropiedadesPopUpDisenioElemento
        if (elementoDisenioClicDerecho != null) {
            if (elementoDisenioClicDerecho.obtenerTipoElemento() == TElementoTopologia.NODO) {
                TNodoTopologia nt = (TNodoTopologia) elementoDisenioClicDerecho;
                if (nt.obtenerTipo() == TNodoTopologia.EMISOR) {
                    JVentanaEmisor ve = new JVentanaEmisor(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
                    ve.ponerConfiguracion((TNodoEmisor) nt, true);
                    ve.show();
                } else if (nt.obtenerTipo() == TNodoTopologia.LER) {
                    JVentanaLER vler = new JVentanaLER(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
                    vler.ponerConfiguracion((TNodoLER) nt, true);
                    vler.show();
                } else if (nt.obtenerTipo() == TNodoTopologia.LERA) {
                    JVentanaLERA vlera = new JVentanaLERA(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
                    vlera.ponerConfiguracion((TNodoLERA) nt, true);
                    vlera.show();
                } else if (nt.obtenerTipo() == TNodoTopologia.LSR) {
                    JVentanaLSR vlsr = new JVentanaLSR(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
                    vlsr.ponerConfiguracion((TNodoLSR) nt, true);
                    vlsr.show();
                } else if (nt.obtenerTipo() == TNodoTopologia.LSRA) {
                    JVentanaLSRA vlsra = new JVentanaLSRA(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
                    vlsra.ponerConfiguracion((TNodoLSRA) nt, true);
                    vlsra.show();
                } else if (nt.obtenerTipo() == TNodoTopologia.RECEPTOR) {
                    JVentanaReceptor vr = new JVentanaReceptor(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
                    vr.ponerConfiguracion((TNodoReceptor) nt, true);
                    vr.show();
                }
                elementoDisenioClicDerecho = null;
                panelDisenio.repaint();
            } else {
                TEnlaceTopologia ent = (TEnlaceTopologia) elementoDisenioClicDerecho;
                TConfigEnlace tceAux = ent.obtenerConfiguracion();
                JVentanaEnlace ve = new JVentanaEnlace(escenario.obtenerTopologia(), dispensadorDeImagenes, VentanaPadre, true);
                ve.ponerConfiguracion(tceAux, true);
                ve.show();
                if (ent.obtenerTipo() == TEnlaceTopologia.EXTERNO) {
                    TEnlaceExterno ext = (TEnlaceExterno) ent;
                    ext.configurar(tceAux, this.escenario.obtenerTopologia(), true);
                } else if (ent.obtenerTipo() == TEnlaceTopologia.INTERNO) {
                    TEnlaceInterno inte = (TEnlaceInterno) ent;
                    inte.configurar(tceAux, this.escenario.obtenerTopologia(), true);
                }
                elementoDisenioClicDerecho = null;
                panelDisenio.repaint();
                int minimoDelay = this.escenario.obtenerTopologia().obtenerMinimoDelay();
                int pasoActual = this.pasoNs.getValue();
                if (pasoActual > minimoDelay) {
                    this.pasoNs.setValue(minimoDelay);
                }
            }
            this.escenario.ponerModificado(true);
        }
    }//GEN-LAST:event_clicEnPropiedadesPopUpDisenioElemento
    
    /** Este método se encarga de controlar que la duración de la simulaciçon y del paso
     * de la misma sea acorde con los delays de los enlaces. Además se encarga de la
     * actualización de la interfaz en esos lugares.
     * @since 1.0
     */
    public void controlarParametrosTemporales() {
        if (!controlTemporizacionDesactivado) {
            if (duracionMs.getValue() == 0) {
                duracionNs.setMinimum(1);
            } else {
                duracionNs.setMinimum(0);
            }
            int duracionTotal = duracionMs.getValue()*1000000 + this.duracionNs.getValue();
            int minDelay = escenario.obtenerTopologia().obtenerMinimoDelay();
            if (minDelay < duracionTotal) {
                pasoNs.setMaximum(minDelay);
            } else {
                pasoNs.setMaximum(duracionTotal);
            }
            this.etiquetaDuracionMs.setText(this.duracionMs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija._ms."));
            this.etiquetaDuracionNs.setText(this.duracionNs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija._ns."));
            this.etiquetaPasoNs.setText(this.pasoNs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija._ns."));
            escenario.obtenerSimulacion().ponerDuracion(new TMarcaTiempo(duracionMs.getValue(), duracionNs.getValue()).obtenerTotalEnNanosegundos());
            escenario.obtenerSimulacion().ponerPaso(pasoNs.getValue());
        }
    }
    
    /** Este método se llama automáticamente cuando se cambia la duración en
     * nanosegundos del paso de simulación.
     * @since 1.0
     * @param evt Evento que hace que el método salte.
     */
private void clicEnPasoNs(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_clicEnPasoNs
    controlarParametrosTemporales();
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnPasoNs

/** Este método se llama automáticamente cuando se cambia la duración de la
 * simulación en nanosegundos.
 * @since 1.0
 * @param evt Evento que hace que se ejecute este método.
 */
private void clicEnDuracionNs(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_clicEnDuracionNs
    controlarParametrosTemporales();
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnDuracionNs

/** Este método se llama automáticamente cuando se cambia la duración de la
 * simulación en milisegundos.
 * @since 1.0
 * @param evt Evento que produce que se ejecute este método.
 */
private void clicEnDuracionMs(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_clicEnDuracionMs
    controlarParametrosTemporales();
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnDuracionMs

/** Este método se llama automáticamente cuando se cambia el tiempo que se detendrá
 * la simulación entre un paso de simulación y otro.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void mlsPorTicCambiado(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mlsPorTicCambiado
    this.etiquetaMlsPorTic.setText(this.mlsPorTic.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Simulacion.etiquetaMsTic"));
    panelSimulacion.ponerMlsPorTic(this.mlsPorTic.getValue());
}//GEN-LAST:event_mlsPorTicCambiado

/** Este método se ejecuta cuando se hace clic en la opción de ocultar el nombre de
 * todos los enlaces, en el menú emergente de la pantalla de Disenio.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnPopUpDisenioFondoOcultarNombreEnlaces(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoOcultarNombreEnlaces
    Iterator it = escenario.obtenerTopologia().obtenerIteradorEnlaces();
    TEnlaceTopologia enlaceAux;
    while (it.hasNext()) {
        enlaceAux = (TEnlaceTopologia) it.next();
        enlaceAux.ponerMostrarNombre(false);
    }
    panelDisenio.repaint();
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnPopUpDisenioFondoOcultarNombreEnlaces

/** Este método se ejecuta cuando se hace clic en la opción de ver el nombre de
 * todos los enlaces, en el menú emergente de la pantalla de Disenio.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnPopUpDisenioFondoVerNombreEnlaces(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoVerNombreEnlaces
    Iterator it = escenario.obtenerTopologia().obtenerIteradorEnlaces();
    TEnlaceTopologia enlaceAux;
    while (it.hasNext()) {
        enlaceAux = (TEnlaceTopologia) it.next();
        enlaceAux.ponerMostrarNombre(true);
    }
    panelDisenio.repaint();
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnPopUpDisenioFondoVerNombreEnlaces

/** Este método se ejecuta cuando se hace clic en la opción de ocultar el nombre de
 * todos los nodos, en el menú emergente de la pantalla de Disenio.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnPopUpDisenioFondoOcultarNombreNodos(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoOcultarNombreNodos
    Iterator it = escenario.obtenerTopologia().obtenerIteradorNodos();
    TNodoTopologia nodoAux;
    while (it.hasNext()) {
        nodoAux = (TNodoTopologia) it.next();
        nodoAux.ponerMostrarNombre(false);
    }
    panelDisenio.repaint();
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnPopUpDisenioFondoOcultarNombreNodos

/** Este método se ejecuta cuando se hace clic en la opción de ver el nombre de
 * todos los nodos, en el menú emergente de la pantalla de Disenio.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnPopUpDisenioFondoVerNombreNodos(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoVerNombreNodos
    Iterator it = escenario.obtenerTopologia().obtenerIteradorNodos();
    TNodoTopologia nodoAux;
    while (it.hasNext()) {
        nodoAux = (TNodoTopologia) it.next();
        nodoAux.ponerMostrarNombre(true);
    }
    panelDisenio.repaint();
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnPopUpDisenioFondoVerNombreNodos

/** Este método se ejecuta cuando se hace clic en la opción de eliminar todo el
 * escenario completo, en el menú emergente de la pantalla de Disenio.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void clicEnPopUpDisenioFondoEliminar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioFondoEliminar
    JVentanaBooleana vb = new JVentanaBooleana(this.VentanaPadre, true, this.dispensadorDeImagenes);
    vb.mostrarPregunta(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.PreguntaBorrarTodo"));
    vb.show();
    boolean respuesta = vb.obtenerRespuesta();
    if (respuesta) {
        escenario.obtenerTopologia().eliminarTodo();
        panelDisenio.repaint();
    }
    this.escenario.ponerModificado(true);
}//GEN-LAST:event_clicEnPopUpDisenioFondoEliminar

/**
 * Este método asigna un escenario ya creado a la ventana hija. A partir de ese
 * momento todo lo que se haga en la ventana tendrá su repercusión en el escenario.
 * @param esc Escenario ya creado al que se va a asociar esta ventana hija y que contendrá un
 * escenario y todos sus datos.
 * @since 1.0
 */
public void ponerEscenario(TEscenario esc) {
    this.controlTemporizacionDesactivado = true;
    long durac = esc.obtenerSimulacion().obtenerDuracion();
    long pas = esc.obtenerSimulacion().obtenerPaso();
    escenario = esc;
    panelDisenio.ponerTopologia(esc.obtenerTopologia());
    panelSimulacion.ponerTopologia(esc.obtenerTopologia());
    nodoSeleccionado = null;
    elementoDisenioClicDerecho = null;
    aProgresoGeneracion = new TActualizadorDeProgreso(barraDeProgreso);
    try {
        esc.obtenerTopologia().obtenerReloj().addListenerProgreso(aProgresoGeneracion);
    } catch (EProgresoUnSoloSuscriptor e) {
        e.printStackTrace();
    }
    this.duracionMs.setValue((int)(durac/1000000));
    this.duracionNs.setValue((int) (durac-(this.duracionMs.getValue()*1000000)));
    this.pasoNs.setMaximum((int) esc.obtenerSimulacion().obtenerDuracion());
    this.pasoNs.setValue((int) pas);
    esc.obtenerSimulacion().ponerDuracion(durac);
    esc.obtenerSimulacion().ponerPaso(pas);
    this.etiquetaMlsPorTic.setText(this.mlsPorTic.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.Simulacion.EtiquetaMsTic"));
    this.etiquetaDuracionMs.setText(this.duracionMs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija._ms."));
    this.etiquetaDuracionNs.setText(this.duracionNs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija._ns."));
    this.etiquetaPasoNs.setText(this.pasoNs.getValue() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija_ns."));
    this.nombreAutor.setText(esc.obtenerAutor());
    this.nombreAutor.setCaretPosition(1);
    this.nombreEscenario.setText(esc.obtenerTitulo());
    this.nombreEscenario.setCaretPosition(1);
    this.descripcionEscenario.setText(esc.obtenerDescripcion());
    this.descripcionEscenario.setCaretPosition(1);
    this.controlTemporizacionDesactivado = false;
    escenario.ponerPanelSimulacion(this.panelSimulacion);
    this.controlarParametrosTemporales();
}

/** Este método se ejecuta cuando se hace clic en la opción de añadir un enlace
 * nuevo en la barra de herramientas de la pantalla de diseño.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void clicEnAniadirEnlace(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirEnlace
    if (escenario.obtenerTopologia().obtenerNumeroDeNodos() < 2) {
        JVentanaAdvertencia va = new JVentanaAdvertencia(VentanaPadre, true, dispensadorDeImagenes);
        va.mostrarMensaje(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.ErrorAlMenosDosNodos"));
        va.show();
    } else {
        TConfigEnlace config = new TConfigEnlace();
        JVentanaEnlace venlace = new JVentanaEnlace(escenario.obtenerTopologia(), dispensadorDeImagenes, VentanaPadre, true);
        venlace.ponerConfiguracion(config, false);
        venlace.cargarNodosPorDefecto();
        venlace.show();
        if (config.obtenerValida()) {
            try {
                if (config.obtenerTipo() == TEnlaceTopologia.INTERNO) {
                    TEnlaceInterno enlaceInterno = new TEnlaceInterno(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
                    enlaceInterno.configurar(config, escenario.obtenerTopologia(), false);
                    escenario.obtenerTopologia().insertarEnlace(enlaceInterno);
                } else {
                    TEnlaceExterno enlaceExterno = new TEnlaceExterno(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
                    enlaceExterno.configurar(config, escenario.obtenerTopologia(), false);
                    escenario.obtenerTopologia().insertarEnlace(enlaceExterno);
                }
                panelDisenio.repaint();
            } catch (Exception e) {
                JVentanaError err;
                err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
                err.mostrarMensaje(e.toString());
                err.show();
            };
            this.escenario.ponerModificado(true);
        } else {
            config = null;
        }
    }
}//GEN-LAST:event_clicEnAniadirEnlace

/** Este método se ejecuta cuando se hace clic en la opción eliminar que aparece en
 * el menú emergente al pulsar con el botón derecho sobre un elemento de la
 * topología. En la pantalla de diseño.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void clicEnPopUpDisenioEliminar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioEliminar
    JVentanaBooleana vb = new JVentanaBooleana(this.VentanaPadre, true, this.dispensadorDeImagenes);
    vb.mostrarPregunta(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.preguntaAlEliminar"));
    vb.show();
    boolean respuesta = vb.obtenerRespuesta();
    if (respuesta) {
        if (elementoDisenioClicDerecho != null) {
            if (elementoDisenioClicDerecho.obtenerTipoElemento() == TElementoTopologia.NODO) {
                TNodoTopologia nt = (TNodoTopologia) elementoDisenioClicDerecho;
                if (nt.obtenerTipo() == TNodoTopologia.RECEPTOR) {
                    if (this.escenario.obtenerTopologia().hayTraficoDirigidoAMi((TNodoReceptor) nt)) {
                        JVentanaAdvertencia va;
                        va = new JVentanaAdvertencia(VentanaPadre, true, dispensadorDeImagenes);
                        va.mostrarMensaje(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.NoPuedoBorrarReceptor"));
                        va.show();
                        elementoDisenioClicDerecho = null;
                    } else {
                        escenario.obtenerTopologia().eliminarNodo(nt);
                        elementoDisenioClicDerecho = null;
                        panelDisenio.repaint();
                    }
                } else {
                    escenario.obtenerTopologia().eliminarNodo(nt);
                    elementoDisenioClicDerecho = null;
                    panelDisenio.repaint();
                }
            } else {
                TEnlaceTopologia ent = (TEnlaceTopologia) elementoDisenioClicDerecho;
                escenario.obtenerTopologia().eliminarEnlace(ent);
                elementoDisenioClicDerecho = null;
                panelDisenio.repaint();
            }
            this.escenario.ponerModificado(true);
        }
    }
    
}//GEN-LAST:event_clicEnPopUpDisenioEliminar

/** Este método se ejecuta cuando se hace clic en la opción de ver/ocultar nombre
 * que aparece en el menú emergente al pulsar con el botón derecho sobre un elemento
 * de la topología. En la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnPopUpDisenioVerNombre(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnPopUpDisenioVerNombre
    if (elementoDisenioClicDerecho != null) {
        if (elementoDisenioClicDerecho.obtenerTipoElemento() == TElementoTopologia.NODO) {
            TNodoTopologia nt = (TNodoTopologia) elementoDisenioClicDerecho;
            nt.ponerMostrarNombre(dVerNombreMenuItem.isSelected());
            elementoDisenioClicDerecho = null;
            panelDisenio.repaint();
        } else {
            TEnlaceTopologia ent = (TEnlaceTopologia) elementoDisenioClicDerecho;
            ent.ponerMostrarNombre(dVerNombreMenuItem.isSelected());
            elementoDisenioClicDerecho = null;
            panelDisenio.repaint();
        }
        this.escenario.ponerModificado(true);
    }
}//GEN-LAST:event_clicEnPopUpDisenioVerNombre

/** Este método se ejecuta cuando se hace clic con el botón derecho en la pantalla
 * de diseño.
 * @since 1.0
 * @param evt Evento que hace que este método se dispare.
 */
private void clicDerechoEnPanelDisenio(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicDerechoEnPanelDisenio
    if (evt.getButton() == MouseEvent.BUTTON3) {
        TElementoTopologia et = escenario.obtenerTopologia().obtenerElementoEnPosicion(evt.getPoint());
        if (et == null) {
            diseFondoPopUp.show(this, evt.getX()+7, evt.getY()-27);
        }
        else {
            if (et.obtenerTipoElemento() == TElementoTopologia.NODO) {
                TNodoTopologia nt = (TNodoTopologia) et;
                dVerNombreMenuItem.setSelected(nt.obtenerMostrarNombre());
                elementoDisenioClicDerecho = et;
                diseElementoPopUp.show(this, evt.getX()+7, evt.getY()+15);
            } else if (et.obtenerTipoElemento() == TElementoTopologia.ENLACE) {
                TEnlaceTopologia ent = (TEnlaceTopologia) et;
                dVerNombreMenuItem.setSelected(ent.obtenerMostrarNombre());
                elementoDisenioClicDerecho = et;
                diseElementoPopUp.show(this, evt.getX()+7, evt.getY()+15);
            }
        }
    } else {
        elementoDisenioClicDerecho = null;
        panelDisenio.repaint();
    }
}//GEN-LAST:event_clicDerechoEnPanelDisenio

/** Este método se ejecuta cuando se hace clic en la opción de añadir un LSRA
 * nuevo en la barra de herramientas de la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnAniadirLSRA(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLSRA
    TNodoLSRA lsra = null;
    try {
        lsra = new TNodoLSRA(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIP().obtenerIP(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
    } catch (Exception e) {
        JVentanaError err;
        err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
        err.mostrarMensaje(e.toString());
        err.show();
    }
    JVentanaLSRA vlsra = new JVentanaLSRA(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
    vlsra.ponerConfiguracion(lsra, false);
    vlsra.show();
    if (lsra.estaBienConfigurado()) {
        try {
            escenario.obtenerTopologia().insertarNodo(lsra);
            panelDisenio.repaint();
        } catch (Exception e) {
            JVentanaError err;
            err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
            err.mostrarMensaje(e.toString());
            err.show();
        };
        this.escenario.ponerModificado(true);
    } else {
        lsra = null;
    }
}//GEN-LAST:event_clicEnAniadirLSRA

/** Este método se ejecuta cuando se hace clic en la opción de añadir un LSR
 * nuevo en la barra de herramientas de la pantalla de diseño.
 * @since 1.0
 * @param evt Evento que hace que este método se dispare.
 */
private void clicEnAniadirLSR(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLSR
    TNodoLSR lsr = null;
    try {
        lsr = new TNodoLSR(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIP().obtenerIP(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
    } catch (Exception e) {
        JVentanaError err;
        err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
        err.mostrarMensaje(e.toString());
        err.show();
    }
    JVentanaLSR vlsr = new JVentanaLSR(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
    vlsr.ponerConfiguracion(lsr, false);
    vlsr.show();
    if (lsr.estaBienConfigurado()) {
        try {
            escenario.obtenerTopologia().insertarNodo(lsr);
            panelDisenio.repaint();
        } catch (Exception e) {
            JVentanaError err;
            err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
            err.mostrarMensaje(e.toString());
            err.show();
        };
        this.escenario.ponerModificado(true);
    } else {
        lsr = null;
    }
}//GEN-LAST:event_clicEnAniadirLSR

/** Este método se ejecuta cuando se hace clic en la opción de añadir un LSRA
 * nuevo en la barra de herramientas de la pantalla de diseño.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void clicEnAniadirLERA(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLERA
    TNodoLERA lera = null;
    try {
        lera = new TNodoLERA(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIP().obtenerIP(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
    } catch (Exception e) {
        JVentanaError err;
        err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
        err.mostrarMensaje(e.toString());
        err.show();
    }
    JVentanaLERA vlera = new JVentanaLERA(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
    vlera.ponerConfiguracion(lera, false);
    vlera.show();
    if (lera.estaBienConfigurado()) {
        try {
            escenario.obtenerTopologia().insertarNodo(lera);
            panelDisenio.repaint();
        } catch (Exception e) {
            JVentanaError err;
            err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
            err.mostrarMensaje(e.toString());
            err.show();
        };
        this.escenario.ponerModificado(true);
    } else {
        lera = null;
    }
}//GEN-LAST:event_clicEnAniadirLERA

/** Este método se ejecuta cuando se mueve el ratón dentro del área de simulación ,
 * en la pantalla de simulación. Entre otras cosas, cambia el cursor del ratón al pasar
 * sobre un elemento, permite mostrar menús emergentes coherentes con el contexto
 * de donde se encuentra el ratón, etcétera.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSobrePanelSimulacion(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSobrePanelSimulacion
    TTopologia topo = escenario.obtenerTopologia();
    TElementoTopologia et = topo.obtenerElementoEnPosicion(evt.getPoint());
    if (et != null) {
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (et.obtenerTipoElemento() == TElementoTopologia.NODO) {
            TNodoTopologia nt = (TNodoTopologia) et;
            if (nt.obtenerPuertos().obtenerSaturadoArtificialmente()) {
                panelSimulacion.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.Congestion") +nt.obtenerPuertos().obtenerCongestion()+ java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.POrcentaje")+java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.paraDejarDeCongestionar"));
            } else {
                panelSimulacion.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.Congestion") +nt.obtenerPuertos().obtenerCongestion()+ java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.POrcentaje")+java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaHija.paraCongestionar"));
            }
        } else if (et.obtenerTipoElemento() == TElementoTopologia.ENLACE) {
            TEnlaceTopologia ent = (TEnlaceTopologia) et;
            if (ent.obtenerEnlaceCaido()) {
                panelSimulacion.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.EnlaceRoto"));
            } else {
                panelSimulacion.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.EnlaceFuncionando"));
            }
        }
    } else {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        this.panelSimulacion.setToolTipText(null); 
        if (!this.panelSimulacion.obtenerMostrarLeyenda()) {
            this.panelSimulacion.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.VerLeyenda")); 
        } else{
            this.panelSimulacion.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.OcultarLeyenda")); 
        }
    }
}//GEN-LAST:event_ratonSobrePanelSimulacion

/** Este método se ejecuta cuando se mueve el ratón dentro del área de diseño,
 * en la pantalla de Diseño. Entre otras cosas, cambia el cursor del ratón al pasar
 * sobre un elemento, permite mostrar menús emergentes coherentes con el contexto
 * de donde se encuentra el ratón, etcétera.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonSobrePanelDisenio(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSobrePanelDisenio
    TTopologia topo = escenario.obtenerTopologia();
    TElementoTopologia et = topo.obtenerElementoEnPosicion(evt.getPoint());
    if (et != null) {
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (et.obtenerTipoElemento() == TElementoTopologia.NODO) {
            TNodoTopologia nt = (TNodoTopologia) et;
            panelDisenio.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.PanelDisenio.IP") + nt.obtenerIP());
        } else if (et.obtenerTipoElemento() == TElementoTopologia.ENLACE) {
            TEnlaceTopologia ent = (TEnlaceTopologia) et;
            panelDisenio.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.panelDisenio.Retardo") + ent.obtenerDelay() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.panelDisenio.ns"));
        }
    } else {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        panelDisenio.setToolTipText(null);
    }
}//GEN-LAST:event_ratonSobrePanelDisenio

/** Este método se llama automáticamente cuando se está arrastrando el ratón en la
 * pantalla de diseño. Se encarga de mover los elementos de un lugar a otro para
 * diseñar la topología.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void arrastrandoEnPanelDisenio(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_arrastrandoEnPanelDisenio
    if (evt.getModifiersEx() == java.awt.event.InputEvent.BUTTON1_DOWN_MASK) {
        if (nodoSeleccionado != null) {
            TTopologia topo = escenario.obtenerTopologia();
            Point p2 = evt.getPoint();
            if (p2.x < 0)
                p2.x = 0;
            if (p2.x > panelDisenio.getSize().width)
                p2.x = panelDisenio.getSize().width;
            if (p2.y < 0)
                p2.y = 0;
            if (p2.y > panelDisenio.getSize().height)
                p2.y = panelDisenio.getSize().height;
            nodoSeleccionado.ponerPosicion(new Point(p2.x, p2.y));
            panelDisenio.repaint();
            this.escenario.ponerModificado(true);
        }
    }
}//GEN-LAST:event_arrastrandoEnPanelDisenio

/** Este método se llama automáticamente cuando soltamos el botón del raton a la
 * rrastrar o al hacer clic. Si el ratón estaba sobre  un elemento de la topologia,
 * se marca éste como no seleccionado.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicSoltadoEnPanelDisenio(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicSoltadoEnPanelDisenio
    if (evt.getButton() == MouseEvent.BUTTON1) {
        if (nodoSeleccionado != null) {
            nodoSeleccionado.ponerEstado(TNodoTopologia.DESELECCIONADO);
            nodoSeleccionado = null;
            this.escenario.ponerModificado(true);
        }
        panelDisenio.repaint();
    }
}//GEN-LAST:event_clicSoltadoEnPanelDisenio

/** Este método se llama automáticamente cuando se hace un clic con el botón
 * izquierdo sobre la pantalla de diseño.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void clicEnPanelDisenio(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnPanelDisenio
    if (evt.getButton() == MouseEvent.BUTTON1) {
        TTopologia topo = escenario.obtenerTopologia();
        nodoSeleccionado = topo.obtenerNodoEnPosicion(evt.getPoint());
        if (nodoSeleccionado != null) {
            nodoSeleccionado.ponerEstado(TNodoTopologia.SELECCIONADO);
            this.escenario.ponerModificado(true);
        }
        panelDisenio.repaint();
    }
}//GEN-LAST:event_clicEnPanelDisenio

/** Este método se llama automáticamente cuando el ratón sale del icono de
 * detener en la pantalla de simulación.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSaleDelIconoPausar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoPausar
    iconoPausar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_PAUSA));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDelIconoPausar

/** Este método se llama automáticamente cuando el ratón pasa por el icono de
 * detener en la pantalla de simulación.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoPausar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoPausar
    iconoPausar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_PAUSA_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoPausar

/** Este método se llama automáticamente cuando el ratón sale del icono de finalizar
 * en la pantalla de simulación.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonSaleDelIconoFinalizar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoFinalizar
    iconoFinalizar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_PARAR));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDelIconoFinalizar

/** Este método se llama automáticamente cuando el ratón pasa por el icono de finalizar
 * en la pantalla de simulación.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoFinalizar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoFinalizar
    iconoFinalizar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_PARAR_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoFinalizar

/** Este método se llama automáticamente cuando el ratón sale del icono de comenzar
 * en la pantalla de simulación.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonSaleDelIconoReanudar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoReanudar
    iconoReanudar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_COMENZAR));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDelIconoReanudar

/** Este método se llama automáticamente cuando el ratón pasa por el icono de
 * comenzar en la pantalla de simulación.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoReanudar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoReanudar
    iconoReanudar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_COMENZAR_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoReanudar

/** Este método se llama automáticamente cuando el ratón sale del icono generar en la
 * pantalla de simulación.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonSaleDelIconoComenzar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDelIconoComenzar
    iconoComenzar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_GENERAR));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDelIconoComenzar

/** Este método se llama automáticamente cuando el ratón pasa por el icono generar en
 * la pantalla de simulación.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoComenzar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoComenzar
    iconoComenzar.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.BOTON_GENERAR_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoComenzar

/** Este método se ejecuta cuando se hace clic en la opción de añadir un LER
 * nuevo en la barra de herramientas de la pantalla de diseño.
 * @since 1.0
 * @param evt Evento que hace que se dispare este método.
 */
private void clicEnAniadirLER(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirLER
    TNodoLER ler = null;
    try {
        ler = new TNodoLER(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIP().obtenerIP(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
    } catch (Exception e) {
        JVentanaError err;
        err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
        err.mostrarMensaje(e.toString());
        err.show();
    }
    JVentanaLER vler = new JVentanaLER(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
    vler.ponerConfiguracion(ler, false);
    vler.show();
    if (ler.estaBienConfigurado()) {
        try {
            escenario.obtenerTopologia().insertarNodo(ler);
            panelDisenio.repaint();
        } catch (Exception e) {
            JVentanaError err;
            err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
            err.mostrarMensaje(e.toString());
            err.show();
        };
        this.escenario.ponerModificado(true);
    } else {
        ler = null;
    }
}//GEN-LAST:event_clicEnAniadirLER

/** Este método se llama automáticamente cuando el ratón sale del icono enlace en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método
 */
private void ratonSaleDeIconoEnlace(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoEnlace
    iconoEnlace.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ENLACE_MENU));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDeIconoEnlace

/** Este método se llama automáticamente cuando el ratón pasa por el icono enlace en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoEnlace(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoEnlace
    iconoEnlace.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ENLACE_MENU_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoEnlace

/** Este método se llama automáticamente cuando el ratón sale del icono LSRA en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSaleDeIconoLSRA(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLSRA
    iconoLSRA.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSRA_MENU));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDeIconoLSRA

/** Este método se llama automáticamente cuando el ratón pasa por el icono LSRA en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoLSRA(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLSRA
    iconoLSRA.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSRA_MENU_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoLSRA

/** Este método se llama automáticamente cuando el ratón sale del icono LSR en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSaleDeIconoLSR(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLSR
    iconoLSR.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSR_MENU));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDeIconoLSR

/** Este método se llama automáticamente cuando el ratón pasa por el icono LSR en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoLSR(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLSR
    iconoLSR.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSR_MENU_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoLSR

/** Este método se llama automáticamente cuando el ratón sale del icono LERA en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSaleDeIconoLERA(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLERA
    iconoLERA.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LERA_MENU));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDeIconoLERA

/** Este método se llama automáticamente cuando el ratón pasa por el icono LERA en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoLERA(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLERA
    iconoLERA.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LERA_MENU_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoLERA

/** Este método se llama automáticamente cuando el ratón sale del icono LER en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSaleDeIconoLER(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoLER
    iconoLER.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LER_MENU));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDeIconoLER

/** Este método se llama automáticamente cuando el ratón pasa por el icono LER en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoLER(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoLER
    iconoLER.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LER_MENU_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoLER

/** Este método se llama automáticamente cuando el ratón sale del icono receptor en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSaleDeIconoReceptor(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoReceptor
    iconoReceptor.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.RECEPTOR_MENU));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDeIconoReceptor

/** Este método se llama automáticamente cuando el ratón pasa por el icono receptor
 * en la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoReceptor(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoReceptor
    iconoReceptor.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.RECEPTOR_MENU_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoReceptor

/** Este método se llama automáticamente cuando el ratón sale del icono emisor en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonSaleDeIconoEmisor(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDeIconoEmisor
    iconoEmisor.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.EMISOR_MENU));
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDeIconoEmisor

/** Este método se llama automáticamente cuando el ratón pasa por el icono emisor en
 * la pantalla de diseño.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void ratonEntraEnIconoEmisor(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnIconoEmisor
    iconoEmisor.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.EMISOR_MENU_BRILLO));
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_ratonEntraEnIconoEmisor

/** Este método se llama automáticamente cuando se hace clic sobre el icono receptor
 * en la ventana de diseño. Añade un receptor nuevo en la topologia.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnAniadirReceptor(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirReceptor
    TNodoReceptor receptor = null;
    try {
        receptor = new TNodoReceptor(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIP().obtenerIP(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
    } catch (Exception e) {
        JVentanaError err;
        err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
        err.mostrarMensaje(e.toString());
        err.show();
    }
    JVentanaReceptor vr = new JVentanaReceptor(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
    vr.ponerConfiguracion(receptor, false);
    vr.show();
    if (receptor.estaBienConfigurado()) {
        try {
            escenario.obtenerTopologia().insertarNodo(receptor);
            panelDisenio.repaint();
        } catch (Exception e) {
            JVentanaError err;
            err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
            err.mostrarMensaje(e.toString());
            err.show();
        };
        this.escenario.ponerModificado(true);
    } else {
        receptor = null;
    }
}//GEN-LAST:event_clicEnAniadirReceptor

/** Este método se llama automáticamente cuando se hace clic sobre el icono emisor
 * en la ventana de diseño. Añade un emisor nuevo en la topologia.
 * @since 1.0
 * @param evt El evento que hace que se dispare este método.
 */
private void clicEnAniadirEmisorDeTrafico(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnAniadirEmisorDeTrafico
    TTopologia t = escenario.obtenerTopologia();
    Iterator it = t.obtenerIteradorNodos();
    TNodoTopologia nt;
    boolean hayDestino = false;
    while (it.hasNext()) {
        nt = (TNodoTopologia) it.next();
        if (nt.obtenerTipo() == TNodoTopologia.RECEPTOR)
            hayDestino = true;
    }
    if (!hayDestino) {
        JVentanaAdvertencia va = new JVentanaAdvertencia(VentanaPadre, true, dispensadorDeImagenes);
        va.mostrarMensaje(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.NecesitaHaberUnReceptor"));
        va.show();
    } else {
        TNodoEmisor emisor = null;
        try {
            emisor = new TNodoEmisor(escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().obtenerNuevo(), escenario.obtenerTopologia().obtenerGeneradorIP().obtenerIP(), escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
        } catch (Exception e) {
            JVentanaError err;
            err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
            err.mostrarMensaje(e.toString());
            err.show();
        }
        JVentanaEmisor ve = new JVentanaEmisor(escenario.obtenerTopologia(), panelDisenio, dispensadorDeImagenes, VentanaPadre, true);
        ve.ponerConfiguracion(emisor, false);
        ve.show();
        if (emisor.estaBienConfigurado()) {
            try {
                escenario.obtenerTopologia().insertarNodo(emisor);
                panelDisenio.repaint();
            } catch (Exception e) {
                JVentanaError err;
                err = new JVentanaError(VentanaPadre, true, dispensadorDeImagenes);
                err.mostrarMensaje(e.toString());
                err.show();
            };
            this.escenario.ponerModificado(true);
        } else {
            emisor = null;
        }
    }
}//GEN-LAST:event_clicEnAniadirEmisorDeTrafico

/** Este método se llama automáticamente cuando se hace clic sobre el icono detener
 * en la ventana de simulación. Detiene la simulación o su generación.
 * @since 1.0
 * @param evt Evento que hace que este método se dispare.
 */
private void clicAlPausar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicAlPausar
        if (iconoPausar.isEnabled()) {
            this.escenario.obtenerTopologia().obtenerReloj().ponerPausa(true);
            activarOpcionesAlDetener();
        }
}//GEN-LAST:event_clicAlPausar
    
    /** Este método se llama automáticamente cuando se hace clic sobre el icono
     * finalizar en la ventana de simulación. Detiene la simulación por completo.
     * @since 1.0
     * @param evt El evento que hace que este método se dispare.
     */
    private void clicEnFinalizar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnFinalizar
        if (iconoFinalizar.isEnabled()) {
            this.escenario.obtenerTopologia().obtenerReloj().reset();
            this.crearTraza.setEnabled(true);
            this.panelSimulacion.ponerFicheroTraza(null);
            activarOpcionesAlFinalizar();
        }
    }//GEN-LAST:event_clicEnFinalizar
    
    /** Este método se llama automáticamente cuando se hace clic sobre el icono comenzar
     * en la ventana de simulación. Inicia la simulación.
     * @since 1.0
     * @param evt El evento que hace que este método se dispare.
     */
    private void clicEnReanudar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnReanudar
        if (iconoReanudar.isEnabled()) {
            activarOpcionesAlComenzar();
            this.escenario.obtenerTopologia().obtenerReloj().ponerPausa(false);
            this.escenario.obtenerTopologia().obtenerReloj().reanudar();
        }
    }//GEN-LAST:event_clicEnReanudar
    
    /** Este método se llama automáticamente cuando se hace clic sobre el icono generar
     * en la ventana de simulación. Crea la simulación.
     * @since 1.0
     * @param evt El evento que hace que este método se dispare.
     */
    private void clicEnComenzar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnComenzar
        if (iconoComenzar.isEnabled()) {
            escenario.reset();
            escenario.ponerDuracionSimulacion(new TMarcaTiempo(duracionMs.getValue(), duracionNs.getValue()));
            escenario.ponerPasoSimulacion(pasoNs.getValue());
            crearListaElementosEstadistica();
            this.escenario.ponerModificado(true);
            this.escenario.obtenerTopologia().obtenerReloj().reset();
            panelSimulacion.reset();
            panelSimulacion.repaint();
            escenario.generarSimulacion();
            int minimoDelay = this.escenario.obtenerTopologia().obtenerMinimoDelay();
            int pasoActual = this.pasoNs.getValue();
            if (pasoActual > minimoDelay) {
                this.pasoNs.setValue(minimoDelay);
            }
            this.crearTraza.setEnabled(false);
            this.panelSimulacion.ponerFicheroTraza(null);
            if (this.crearTraza.isSelected()) {
                if (this.escenario.obtenerFichero() != null) {
                    File fAux = new File(this.escenario.obtenerFichero().getPath()+".txt");
                    this.panelSimulacion.ponerFicheroTraza(fAux);
                } else {
                    this.panelSimulacion.ponerFicheroTraza(new File(this.getTitle()+".txt"));
                }
            } else {
                this.panelSimulacion.ponerFicheroTraza(null);
            }
            activarOpcionesTrasGenerar();
        }
    }//GEN-LAST:event_clicEnComenzar
    
    /**
     * Este método se llama cuando comienza la simulación del escenario. Crea una lista
     * de todos los nodos que tienen activa la generación de estadísticas para
     * posteriormente poder elegir uno de ellos y ver sus gráficas.
     * @since 1.0
     */    
    public void crearListaElementosEstadistica() {
        Iterator it = null;
        TNodoTopologia nt = null;
        TEnlaceTopologia et = null;
        this.selectorElementoEstadisticas.removeAllItems();
        this.selectorElementoEstadisticas.addItem("");
        it = this.escenario.obtenerTopologia().obtenerIteradorNodos();
        while (it.hasNext()) {
            nt = (TNodoTopologia) it.next();
            if (nt.obtenerEstadisticas()) {
                this.selectorElementoEstadisticas.addItem(nt.obtenerNombre());
            }
        }
        this.selectorElementoEstadisticas.setSelectedIndex(0);
    }
    
    /** Este método modifica la interfaz para que las opciones que se muestran sean
     * acordes al momento en que la simulación está detenida.
     * @since 1.0
     */
    private void activarOpcionesAlDetener() {
        iconoComenzar.setEnabled(false);
        iconoReanudar.setEnabled(true);
        iconoFinalizar.setEnabled(true);
        iconoPausar.setEnabled(false);
    }
    
    /** Este método modifica la interfaz para que las opciones que se muestran sean
     * acordes al momento en que la simulación ha finalizado.
     * @since 1.0
     */
    private void activarOpcionesAlFinalizar() {
        iconoComenzar.setEnabled(true);
        iconoReanudar.setEnabled(false);
        iconoFinalizar.setEnabled(false);
        iconoPausar.setEnabled(false);
    }
    
    /** Este método modifica la interfaz para que las opciones que se muestran sean
     * acordes al momento en que la simulación se acaba de generar.
     * @since 1.0
     */
    private void activarOpcionesTrasGenerar() {
        iconoComenzar.setEnabled(false);
        iconoReanudar.setEnabled(false);
        iconoFinalizar.setEnabled(true);
        iconoPausar.setEnabled(true);
    }
    
    /** Este método modifica la interfaz para que las opciones que se muestran sean
     * acordes al momento en que la simulación comienza.
     * @since 1.0
     */
    private void activarOpcionesAlComenzar() {
        iconoComenzar.setEnabled(false);
        iconoReanudar.setEnabled(false);
        iconoFinalizar.setEnabled(true);
        iconoPausar.setEnabled(true);
    }
    
    /** Cierra la ventana hija y pierde o almacena su contenido en función de la
     * elección del usuario.
     * @since 1.0
     */
    public void cerrar() {
        this.setVisible(false);
        this.dispose();
    }
    
    
    /**
     * Este método se encarga de controlar que todo ocurre como debe con respecto al
     * escenario, cuando se pulsa en el menú principal la opción de "Guardar como..."
     * @since 1.0
     */    
    public void gestionarGuardarComo() {
        anotarDatosDeEscenario();
        JFileChooser dialogoGuardar = new JFileChooser();
        dialogoGuardar.setFileFilter(new JFiltroOSM());
        dialogoGuardar.setDialogType(JFileChooser.CUSTOM_DIALOG);
        dialogoGuardar.setApproveButtonMnemonic('A');
        dialogoGuardar.setApproveButtonText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.DialogoGuardar.OK"));
        dialogoGuardar.setDialogTitle(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.DialogoGuardar.Almacenar")+ this.getTitle() +java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("-"));
        dialogoGuardar.setAcceptAllFileFilterUsed(false);
        dialogoGuardar.setSelectedFile(new File(this.getTitle()));
        dialogoGuardar.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int resultado = dialogoGuardar.showSaveDialog(VentanaPadre);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String ext = null;
            String nombreFich = dialogoGuardar.getSelectedFile().getPath();
            int i = nombreFich.lastIndexOf('.');
            if (i > 0 &&  i < nombreFich.length() - 1) {
                ext = nombreFich.substring(i+1).toLowerCase();
            }
            if (ext == null) {
                nombreFich += java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString(".osm");
            } else {
                if (!ext.equals(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("osm"))) {
                    nombreFich += java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString(".osm");
                }
            }
            dialogoGuardar.setSelectedFile(new File(nombreFich));
            escenario.ponerFichero(dialogoGuardar.getSelectedFile());
            this.escenario.ponerGuardado(true);
            this.setTitle(this.escenario.obtenerFichero().getName());
            TAlmacenadorOSM almacenador = new TAlmacenadorOSM(escenario);
            JVentanaBooleana vb = new JVentanaBooleana(this.VentanaPadre, true, this.dispensadorDeImagenes);
            vb.mostrarPregunta(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.PreguntaEmpotrarCRC"));
            vb.show();
            boolean conCRC = vb.obtenerRespuesta();
            boolean correcto = almacenador.almacenar(escenario.obtenerFichero(), conCRC);
            if (correcto) {
                this.escenario.ponerModificado(false);
                this.escenario.ponerGuardado(true);
            }
        }
    }
    
    /**
     * Este método se encarga de controlar que todo ocurre como debe con respecto al
     * escenario, cuando se pulsa en el menú principal la opción de "Cerrar" o "Salir"
     * y el escenario actual no está aún guardado o está modificado.
     * @since 1.0
     */    
    public void gestionarGuardarParaCerrar() {
        boolean guardado = this.escenario.obtenerGuardado();
        boolean modificado = this.escenario.obtenerModificado();
        anotarDatosDeEscenario();
        
        // Detengo la simulación antes de cerrar, si es necesario.
        if (this.escenario.obtenerTopologia().obtenerReloj().estaEnFuncionamiento()) {
            panelSimulacion.reset();
            panelSimulacion.repaint();
            escenario.reset();
            escenario.ponerDuracionSimulacion(new TMarcaTiempo(duracionMs.getValue(), duracionNs.getValue()));
            escenario.ponerPasoSimulacion(pasoNs.getValue());
            this.escenario.obtenerTopologia().obtenerReloj().ponerPausa(false);
            activarOpcionesAlFinalizar();
        }
        
        if (!guardado) {
            JVentanaBooleana vb = new JVentanaBooleana(VentanaPadre, true, dispensadorDeImagenes);
            vb.mostrarPregunta(this.getTitle() + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.DialogoGuardar.GuardarPrimeraVez"));
            vb.show();
            boolean respuesta = vb.obtenerRespuesta();
            vb.dispose();
            if (respuesta) {
                this.gestionarGuardarComo();
            }
        } else if ((guardado) && (!modificado)) {
            // No se hace nada, ya está todo guardado correctamente.
        } else if ((guardado) && (modificado)) {
            JVentanaBooleana vb = new JVentanaBooleana(VentanaPadre, true, dispensadorDeImagenes);
            vb.mostrarPregunta(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.DialogoGuardar.CambiosSinguardar1")+ " " + this.getTitle()+ " " + java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.DialogoGuardar.CambiosSinguardar2"));
            vb.show();
            boolean respuesta = vb.obtenerRespuesta();
            vb.dispose();
            if (respuesta) {
                TAlmacenadorOSM almacenador = new TAlmacenadorOSM(escenario);
                JVentanaBooleana vb2 = new JVentanaBooleana(this.VentanaPadre, true, this.dispensadorDeImagenes);
                vb2.mostrarPregunta(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.PreguntaEmpotrarCRC"));
                vb2.show();
                boolean conCRC = vb2.obtenerRespuesta();
                boolean correcto = almacenador.almacenar(escenario.obtenerFichero(), conCRC);
                if (correcto) {
                    this.escenario.ponerModificado(false);
                    this.escenario.ponerGuardado(true);
                }
            }
        }
    }
    
    /**
     * Este método se encarga de controlar que todo ocurre como debe con respecto al
     * escenario, cuando se pulsa en el menú principal la opción de "Guardar".
     * @since 1.0
     */    
    public void gestionarGuardar() {
        boolean guardado = this.escenario.obtenerGuardado();
        boolean modificado = this.escenario.obtenerModificado();
        anotarDatosDeEscenario();
        if (!guardado) {
            this.gestionarGuardarComo();
        } else {
            TAlmacenadorOSM almacenador = new TAlmacenadorOSM(escenario);
            JVentanaBooleana vb = new JVentanaBooleana(this.VentanaPadre, true, this.dispensadorDeImagenes);
            vb.mostrarPregunta(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaHija.PreguntaEmpotrarCRC"));
            vb.show();
            boolean conCRC = vb.obtenerRespuesta();
            boolean correcto = almacenador.almacenar(escenario.obtenerFichero(), conCRC);
            if (correcto) {
                this.escenario.ponerModificado(false);
                this.escenario.ponerGuardado(true);
            }
            this.escenario.ponerModificado(false);
            this.escenario.ponerGuardado(true);
        }
    }
    
    private void crearEInsertarGraficas(String nombre) {
            GridBagConstraints gbc = null;
            this.panelAnalisis.removeAll();
            this.etiquetaEstadisticasTituloEscenario.setText(this.nombreEscenario.getText());
            this.etiquetaEstadisticasNombreAutor.setText(this.nombreAutor.getText());
            this.areaEstadisticasDescripcion.setText(this.descripcionEscenario.getText());
            this.etiquetaNombreElementoEstadistica.setText(nombre);
            TNodoTopologia nt = this.escenario.obtenerTopologia().obtenerPrimerNodoLlamado(nombre);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            this.panelFijo.add(this.etiquetaEstadisticasTituloEscenario, gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            this.panelFijo.add(this.etiquetaEstadisticasNombreAutor, gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            this.panelFijo.add(this.areaEstadisticasDescripcion, gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.insets = new Insets(10, 5, 10, 5);
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            this.panelFijo.add(this.etiquetaNombreElementoEstadistica, gbc);
            gbc = new java.awt.GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 5);
            gbc.anchor = java.awt.GridBagConstraints.NORTH;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            this.panelAnalisis.add(this.panelFijo, gbc);
            if (nt != null) {
                if (nt.obtenerTipo() == TNodoTopologia.EMISOR) {
                    this.etiquetaNombreElementoEstadistica.setIcon(this.dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.EMISOR));
                } else if (nt.obtenerTipo() == TNodoTopologia.RECEPTOR) {
                    this.etiquetaNombreElementoEstadistica.setIcon(this.dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.RECEPTOR));
                } else if (nt.obtenerTipo() == TNodoTopologia.LER) {
                    this.etiquetaNombreElementoEstadistica.setIcon(this.dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LER));
                } else if (nt.obtenerTipo() == TNodoTopologia.LERA) {
                    this.etiquetaNombreElementoEstadistica.setIcon(this.dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LERA));
                } else if (nt.obtenerTipo() == TNodoTopologia.LSR) {
                    this.etiquetaNombreElementoEstadistica.setIcon(this.dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSR));
                } else if (nt.obtenerTipo() == TNodoTopologia.LSRA) {
                    this.etiquetaNombreElementoEstadistica.setIcon(this.dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.LSRA));
                }

                int numeroGraficos = nt.accederAEstadisticas().obtenerNumeroGraficas();
                
                if (numeroGraficos > 0) {
                    grafico1 = ChartFactory.createXYLineChart(nt.accederAEstadisticas().obtenerTitulo1(), 
                                                             TEstadisticas.TIEMPO,
                                                             TEstadisticas.NUMERO_DE_PAQUETES,
                                                             (XYSeriesCollection) nt.accederAEstadisticas().obtenerDatosGrafica1(),
                                                             PlotOrientation.VERTICAL, 
                                                             true, true, true);

                    grafico1.getPlot().setBackgroundPaint(Color.WHITE);
                    grafico1.getPlot().setForegroundAlpha((float)0.5);
                    grafico1.getPlot().setOutlinePaint(new Color(14, 69, 125));
                    grafico1.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                    grafico1.setBackgroundPaint(new Color(210, 226, 242));
                    grafico1.setBorderPaint(Color.BLACK);
                    grafico1.getTitle().setPaint(new Color(79, 138, 198));
                    this.panelGrafico1 = new ChartPanel(grafico1);
                    panelGrafico1.setBorder(new LineBorder(Color.BLACK));
                    panelGrafico1.setPreferredSize(new Dimension(600, 300));
                    gbc = new java.awt.GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    gbc.insets = new Insets(10, 5, 10, 5);
                    gbc.anchor = java.awt.GridBagConstraints.NORTH;
                    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    this.panelAnalisis.add(panelGrafico1, gbc);
                }
                if (numeroGraficos > 1) {
                    grafico2 = ChartFactory.createXYLineChart(nt.accederAEstadisticas().obtenerTitulo2(), 
                                                             TEstadisticas.TIEMPO,
                                                             TEstadisticas.NUMERO_DE_PAQUETES,
                                                             (XYSeriesCollection) nt.accederAEstadisticas().obtenerDatosGrafica2(),
                                                             PlotOrientation.VERTICAL, 
                                                             true, true, true);
                    grafico2.getPlot().setBackgroundPaint(Color.WHITE);
                    grafico2.getPlot().setForegroundAlpha((float)0.5);
                    grafico2.getPlot().setOutlinePaint(new Color(14, 69, 125));
                    grafico2.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                    grafico2.setBackgroundPaint(new Color(210, 226, 242));
                    grafico2.setBorderPaint(Color.BLACK);
                    grafico2.getTitle().setPaint(new Color(79, 138, 198));
                    this.panelGrafico2 = new ChartPanel(grafico2);
                    panelGrafico2.setPreferredSize(new Dimension(600, 300));
                    panelGrafico2.setBorder(new LineBorder(Color.BLACK));
                    gbc = new java.awt.GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    gbc.insets = new Insets(10, 5, 10, 5);
                    gbc.anchor = java.awt.GridBagConstraints.NORTH;
                    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    this.panelAnalisis.add(panelGrafico2, gbc);
                }
                if (numeroGraficos > 2) {
                    grafico3 = ChartFactory.createXYLineChart(nt.accederAEstadisticas().obtenerTitulo3(), 
                                                             TEstadisticas.TIEMPO,
                                                             TEstadisticas.NUMERO_DE_PAQUETES,
                                                             (XYSeriesCollection) nt.accederAEstadisticas().obtenerDatosGrafica3(),
                                                             PlotOrientation.VERTICAL, 
                                                             true, true, true);
                    grafico3.getPlot().setBackgroundPaint(Color.WHITE);
                    grafico3.getPlot().setForegroundAlpha((float)0.5);
                    grafico3.getPlot().setOutlinePaint(new Color(14, 69, 125));
                    grafico3.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                    grafico3.setBackgroundPaint(new Color(210, 226, 242));
                    grafico3.setBorderPaint(Color.BLACK);
                    grafico3.getTitle().setPaint(new Color(79, 138, 198));
                    this.panelGrafico3 = new ChartPanel(grafico3);
                    panelGrafico3.setBorder(new LineBorder(Color.BLACK));
                    panelGrafico3.setPreferredSize(new Dimension(600, 300));
                    gbc = new java.awt.GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 3;
                    gbc.insets = new Insets(10, 5, 10, 5);
                    gbc.anchor = java.awt.GridBagConstraints.NORTH;
                    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    this.panelAnalisis.add(panelGrafico3, gbc);
                }
                if (numeroGraficos > 3) {
                    grafico4 = ChartFactory.createBarChart(nt.accederAEstadisticas().obtenerTitulo4(), 
                                                             TEstadisticas.DESCRIPCION,
                                                             TEstadisticas.NUMERO,
                                                             (DefaultCategoryDataset) nt.accederAEstadisticas().obtenerDatosGrafica4(),
                                                             PlotOrientation.VERTICAL, 
                                                             true, true, true);
                    grafico4.getPlot().setBackgroundPaint(Color.WHITE);
                    grafico4.getPlot().setForegroundAlpha((float)0.5);
                    grafico4.getPlot().setOutlinePaint(new Color(14, 69, 125));
                    grafico4.setBackgroundPaint(new Color(210, 226, 242));
                    grafico4.setBorderPaint(Color.BLACK);
                    grafico4.getTitle().setPaint(new Color(79, 138, 198));
                    this.panelGrafico4 = new ChartPanel(grafico4);
                    panelGrafico4.setBorder(new LineBorder(Color.BLACK));
                    panelGrafico4.setPreferredSize(new Dimension(600, 300));
                    gbc = new java.awt.GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 4;
                    gbc.insets = new Insets(10, 5, 10, 5);
                    gbc.anchor = java.awt.GridBagConstraints.NORTH;
                    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    this.panelAnalisis.add(panelGrafico4, gbc);
                }
                if (numeroGraficos > 4) {
                    grafico5 = ChartFactory.createBarChart(nt.accederAEstadisticas().obtenerTitulo5(), 
                                                             TEstadisticas.DESCRIPCION,
                                                             TEstadisticas.NUMERO,
                                                             (DefaultCategoryDataset) nt.accederAEstadisticas().obtenerDatosGrafica5(),
                                                             PlotOrientation.VERTICAL, 
                                                             true, true, true);
                    grafico5.getPlot().setBackgroundPaint(Color.WHITE);
                    grafico5.getPlot().setForegroundAlpha((float)0.5);
                    grafico5.getPlot().setOutlinePaint(new Color(14, 69, 125));
                    grafico5.setBackgroundPaint(new Color(210, 226, 242));
                    grafico5.setBorderPaint(Color.BLACK);
                    grafico5.getTitle().setPaint(new Color(79, 138, 198));
                    this.panelGrafico5 = new ChartPanel(grafico5);
                    panelGrafico5.setBorder(new LineBorder(Color.BLACK));
                    panelGrafico5.setPreferredSize(new Dimension(600, 300));
                    gbc = new java.awt.GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 5;
                    gbc.insets = new Insets(10, 5, 10, 5);
                    gbc.anchor = java.awt.GridBagConstraints.NORTH;
                    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    this.panelAnalisis.add(panelGrafico5, gbc);
                }
                if (numeroGraficos > 5) {
                    grafico6 = ChartFactory.createXYLineChart(nt.accederAEstadisticas().obtenerTitulo6(), 
                                                             TEstadisticas.TIEMPO,
                                                             TEstadisticas.NUMERO_DE_PAQUETES,
                                                             (XYSeriesCollection) nt.accederAEstadisticas().obtenerDatosGrafica6(),
                                                             PlotOrientation.VERTICAL, 
                                                             true, true, true);
                    grafico6.getPlot().setBackgroundPaint(Color.WHITE);
                    grafico6.getPlot().setForegroundAlpha((float)0.5);
                    grafico6.getPlot().setOutlinePaint(new Color(14, 69, 125));
                    grafico6.getXYPlot().setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5, 5, 5, 5));
                    grafico6.setBackgroundPaint(new Color(210, 226, 242));
                    grafico6.setBorderPaint(Color.BLACK);
                    grafico6.getTitle().setPaint(new Color(79, 138, 198));
                    this.panelGrafico6 = new ChartPanel(grafico6);
                    panelGrafico6.setBorder(new LineBorder(Color.BLACK));
                    panelGrafico6.setPreferredSize(new Dimension(600, 300));
                    gbc = new java.awt.GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 6;
                    gbc.insets = new Insets(10, 5, 10, 10);
                    gbc.anchor = java.awt.GridBagConstraints.NORTH;
                    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    this.panelAnalisis.add(panelGrafico6, gbc);
                }
            }
            this.panelAnalisis.repaint();
    }
    
    /**
     * Este método se encarga de anotar los datos del escenario desde la interfaz de
     * usuario hasta los correspondientes atributos del objeto que almacena el
     * escenario.
     * @since 1.0
     */    
    private void anotarDatosDeEscenario() {
        this.escenario.ponerTitulo(this.nombreEscenario.getText());
        this.escenario.ponerAutor(this.nombreAutor.getText());
        this.escenario.ponerDescripcion(this.descripcionEscenario.getText());
    }
    
    /** Este atributo es el objeto encargado de actualizar la barra de progreso del
     * escenario que se usa a la hora de generar la simulación y a la hora de
     * ejecutarla.
     * @since 1.0
     */
    private TActualizadorDeProgreso aProgresoGeneracion;
    /** Este atributo contendrá todo el escenario completo de la simulación: topologia,
     * análisis y simulación.
     * @since 1.0
     */
    private TEscenario escenario;
    /** Este atributo contendrá en todo momento una referencia al nodo del escenario que
     * se está arrastrando.
     * @since 1.0
     */
    private TNodoTopologia nodoSeleccionado;
    /** Este atributo contendrá todas las imágenes de Open SimMPLS para poder acceder a
     * ellas de forma más rápida y para no tener que cargar la misma imagen en
     * distintas instancias.
     * @since 1.0
     */
    private TDispensadorDeImagenes dispensadorDeImagenes;
    /** Este atributo es una referencia a la ventana padre que recoge dentro de si a
     * esta ventana hija.
     * @since 1.0
     */
    private JSimulador VentanaPadre;
    /** Este atributo contiene en todo momento un referencia al elemento de la topología
     * (nodo o enlace) sobre el que se está intentando abrir un menú contextual (clic
     * con el botón derecho).
     * @since 1.0
     */
    private TElementoTopologia elementoDisenioClicDerecho;
    
    
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
    private javax.swing.JTextArea areaEstadisticasDescripcion;
    private javax.swing.JProgressBar barraDeProgreso;
    private javax.swing.JCheckBox crearTraza;
    private javax.swing.JMenuItem dEliminarMenuItem;
    private javax.swing.JMenuItem dEliminarTodoMenuItem;
    private javax.swing.JMenuItem dOcultarNombresEnlacesMenuItem;
    private javax.swing.JMenuItem dOcultarNombresNodosMenuItem;
    private javax.swing.JMenuItem dPropiedadesMenuItem;
    private javax.swing.JCheckBoxMenuItem dVerNombreMenuItem;
    private javax.swing.JMenuItem dVerNombresEnlacesMenuItem;
    private javax.swing.JMenuItem dVerNombresNodosMenuItem;
    private javax.swing.JTextField descripcionEscenario;
    private javax.swing.JPopupMenu diseElementoPopUp;
    private javax.swing.JPopupMenu diseFondoPopUp;
    private javax.swing.JSlider duracionMs;
    private javax.swing.JSlider duracionNs;
    private javax.swing.JLabel etiquetaDuracionMs;
    private javax.swing.JLabel etiquetaDuracionNs;
    private javax.swing.JLabel etiquetaEstadisticasNombreAutor;
    private javax.swing.JLabel etiquetaEstadisticasTituloEscenario;
    private javax.swing.JLabel etiquetaMlsPorTic;
    private javax.swing.JLabel etiquetaNombreElementoEstadistica;
    private javax.swing.JLabel etiquetaPasoNs;
    private javax.swing.JLabel iconoComenzar;
    private javax.swing.JLabel iconoEmisor;
    private javax.swing.JLabel iconoEnlace;
    private javax.swing.JLabel iconoFinalizar;
    private javax.swing.JLabel iconoLER;
    private javax.swing.JLabel iconoLERA;
    private javax.swing.JLabel iconoLSR;
    private javax.swing.JLabel iconoLSRA;
    private javax.swing.JLabel iconoPausar;
    private javax.swing.JLabel iconoReanudar;
    private javax.swing.JLabel iconoReceptor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JSlider mlsPorTic;
    private javax.swing.JTextField nombreAutor;
    private javax.swing.JTextField nombreEscenario;
    private javax.swing.JPanel panelAnalisis;
    private javax.swing.JPanel panelAnalisisSuperior;
    private javax.swing.JPanel panelBotonesDisenio;
    private javax.swing.JPanel panelBotonesSimulacion;
    private simMPLS.interfaz.simulador.JPanelDisenio panelDisenio;
    private javax.swing.JPanel panelDisenioSuperior;
    private javax.swing.JPanel panelFijo;
    private javax.swing.JPanel panelOpciones;
    private javax.swing.JPanel panelOpcionesSuperior;
    private javax.swing.JPanel panelSeleccionElemento;
    private simMPLS.interfaz.simulador.JPanelSimulacion panelSimulacion;
    private javax.swing.JPanel panelSimulacionSuperior;
    private javax.swing.JSlider pasoNs;
    private javax.swing.JComboBox selectorElementoEstadisticas;
    // End of variables declaration//GEN-END:variables
}
