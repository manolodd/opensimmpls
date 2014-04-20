//**************************************************************************
// Nombre......: JVentanaEmisor.java
// Proyecto....: Open SimMPLS
// Descripción.: Implementación de una ventana que obtendrá los parámetros
// ............: necesarios para la configuración de un nodo emisor.
// Fecha.......: 19/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.interfaz.dialogos;

import simMPLS.interfaz.utiles.*;
import simMPLS.interfaz.dialogos.*;
import simMPLS.interfaz.simulador.*;
import simMPLS.escenario.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * Esta clase implementa una ventana de configuración de un nodo emisor de la
 * topología.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @since 1.0
 */
public class JVentanaEmisor extends javax.swing.JDialog {

    /**
     * Esta instancia crea una nueva instancia de JVentanaEmisor
     * @param t Topología dentro de la cual se encuentra el nodo emisor que queremos configurar.
     * @param pad Panel de diseño dentro del cual estamos diseñando el nodo emisor.
     * @param di Dispensador de imágenes global de la aplicación.
     * @param parent Ventana padre donde se mostrará esta ventana de tipo JVentanaEmisor.
     * @param modal TRUE indica que la ventana impedirá que se pueda seleccionar cualquier parte de
     * la interfaz hasta que se cierre. FALSE indica todo lo contrario.
     * @since 1.0
     */
    public JVentanaEmisor(TTopologia t, JPanelDisenio pad, TDispensadorDeImagenes di, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        ventanaPadre = parent;
        dispensadorDeImagenes = di;
        pd = pad;
        topo = t;
        initComponents();
        initComponents2();
    }

    /**
     * Este método terminará de configurar algunos aspectos que no hayan quedado
     * terminados de configurar en el constructor.
     * @since 1.0
     */    
    public void initComponents2() {
        panelCoordenadas.ponerPanelOrigen(pd);
        java.awt.Dimension tamFrame=this.getSize();
        java.awt.Dimension tamPadre=ventanaPadre.getSize();
        setLocation((tamPadre.width-tamFrame.width)/2, (tamPadre.height-tamFrame.height)/2);
        emisor = null;
        coordenadaX.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.X=_") + panelCoordenadas.obtenerXReal());
        coordenadaY.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Y=_") + panelCoordenadas.obtenerYReal());
        Iterator it = topo.obtenerIteradorNodos();
        selectorDelReceptor.removeAllItems();
        selectorDelReceptor.addItem("");
        TNodoTopologia nt;
        while (it.hasNext()) {
            nt = (TNodoTopologia) it.next();
            if (nt.obtenerTipo() == TNodoTopologia.RECEPTOR) {
                selectorDelReceptor.addItem(nt.obtenerNombre());
            }
        }
        this.selectorDeGoS.removeAllItems();
        this.selectorDeGoS.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.None"));
        this.selectorDeGoS.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Level_1"));
        this.selectorDeGoS.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Level_2"));
        this.selectorDeGoS.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Level_3"));
        this.selectorDeGoS.setSelectedIndex(0);
        this.selectorSencilloTrafico.removeAllItems();
        this.selectorSencilloTrafico.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Personalized"));
        this.selectorSencilloTrafico.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Email"));
        this.selectorSencilloTrafico.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Web"));
        this.selectorSencilloTrafico.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.P2P_file_sharing"));
        this.selectorSencilloTrafico.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Bank_data_transaction"));
        this.selectorSencilloTrafico.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Tele-medical_video"));
        this.selectorSencilloTrafico.addItem(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Bulk_traffic"));
        this.selectorSencilloTrafico.setSelectedIndex(0);
        selectorDelReceptor.setSelectedIndex(0);
        BKUPDestino = "";
        BKUPLSPDeBackup = false;
        BKUPMostrarNombre = true;
        BKUPNivelDeGos = 0;
        BKUPNombre = "";
        BKUPTasaTrafico = 1000;
        BKUPTipoTrafico = TNodoEmisor.CONSTANTE;
        BKUPGenerarEstadisticas = false;
        BKUPTamDatosConstante = 1024;
        BKUPEncapsularEnMPLS = false;
        reconfigurando = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        buttonGroup1 = new javax.swing.ButtonGroup();
        panelPrincipal = new javax.swing.JPanel();
        panelPestanias = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        iconoEmisor = new javax.swing.JLabel();
        etiquetaNombre = new javax.swing.JLabel();
        nombreNodo = new javax.swing.JTextField();
        panelPosicion = new javax.swing.JPanel();
        coordenadaX = new javax.swing.JLabel();
        coordenadaY = new javax.swing.JLabel();
        panelCoordenadas = new simMPLS.interfaz.dialogos.JPanelCoordenadas();
        verNombre = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        selectorDelReceptor = new javax.swing.JComboBox();
        panelRapido = new javax.swing.JPanel();
        iconoEnlace1 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        selectorDeGenerarEstadisticasSencillo = new javax.swing.JCheckBox();
        selectorSencilloTrafico = new javax.swing.JComboBox();
        panelAvanzado = new javax.swing.JPanel();
        iconoEnlace2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        etiquetaTasa = new javax.swing.JLabel();
        selectorDeGenerarEstadisticas = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        selectorDeGoS = new javax.swing.JComboBox();
        selectorLSPDeRespaldo = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        traficoConstante = new javax.swing.JRadioButton();
        traficoVariable = new javax.swing.JRadioButton();
        encapsularSobreMPLS = new javax.swing.JCheckBox();
        selectorDeTasa = new javax.swing.JSlider();
        selectorDeTamPaquete = new javax.swing.JSlider();
        etiquetaOctetos = new javax.swing.JLabel();
        etiquetaTamPaquete = new javax.swing.JLabel();
        panelBotones = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setTitle(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.TituloVentana"));
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        panelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPestanias.setFont(new java.awt.Font("Dialog", 0, 12));
        panelGeneral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iconoEmisor.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.EMISOR));
        iconoEmisor.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.DescripcionNodo"));
        panelGeneral.add(iconoEmisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        etiquetaNombre.setFont(new java.awt.Font("Dialog", 0, 12));
        etiquetaNombre.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Etiqueta.NombreNodo"));
        panelGeneral.add(etiquetaNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 105, 120, -1));

        nombreNodo.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.Nombre"));
        panelGeneral.add(nombreNodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 130, 125, -1));

        panelPosicion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPosicion.setBorder(new javax.swing.border.TitledBorder(null, java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Etiqueta.Posicion"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12)));
        coordenadaX.setFont(new java.awt.Font("Dialog", 0, 12));
        coordenadaX.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.X=_45"));
        panelPosicion.add(coordenadaX, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, -1, -1));

        coordenadaY.setFont(new java.awt.Font("Dialog", 0, 12));
        coordenadaY.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Y=_1024"));
        panelPosicion.add(coordenadaY, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        panelCoordenadas.setBackground(new java.awt.Color(255, 255, 255));
        panelCoordenadas.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.posicion"));
        panelCoordenadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clicEnPanelCoordenadas(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratonEntraEnPanelCoordenadas(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratonSaleDePanelCoordenadas(evt);
            }
        });

        panelPosicion.add(panelCoordenadas, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 25, 130, 70));

        panelGeneral.add(panelPosicion, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 100, 180, 125));

        verNombre.setFont(new java.awt.Font("Dialog", 0, 12));
        verNombre.setSelected(true);
        verNombre.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Etiqueta.VerNombre"));
        verNombre.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.VerNombre"));
        panelGeneral.add(verNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 175, -1, -1));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.DestinoTrafico"));
        panelGeneral.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 245, 170, -1));

        selectorDelReceptor.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorDelReceptor.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.destinodeltrafico"));
        panelGeneral.add(selectorDelReceptor, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 240, -1, -1));

        panelPestanias.addTab(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Tab.General"), panelGeneral);

        panelRapido.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iconoEnlace1.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ASISTENTE));
        iconoEnlace1.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.configuracionRapida"));
        panelRapido.add(iconoEnlace1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.TipoDeTrafico1"));
        panelRapido.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 125, 115, -1));

        selectorDeGenerarEstadisticasSencillo.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorDeGenerarEstadisticasSencillo.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.GenerarEstadisticas1"));
        selectorDeGenerarEstadisticasSencillo.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.GenerarEstadisticas1"));
        selectorDeGenerarEstadisticasSencillo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnGenerarEstadisticasSencillo(evt);
            }
        });

        panelRapido.add(selectorDeGenerarEstadisticasSencillo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 195, -1, -1));

        selectorSencilloTrafico.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorSencilloTrafico.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Personalized", "Email", "Web", "P2P file sharing", "Bank data transaction", "Tele-medical video", "Bulk traffic" }));
        selectorSencilloTrafico.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.TipoDeTrafico1"));
        selectorSencilloTrafico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnSelectorSencilloTrafico(evt);
            }
        });

        panelRapido.add(selectorSencilloTrafico, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 120, -1, -1));

        panelPestanias.addTab(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Tab.Rapida"), panelRapido);

        panelAvanzado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iconoEnlace2.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.AVANZADA));
        iconoEnlace2.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.ConfiguracionAvanzada"));
        panelAvanzado.add(iconoEnlace2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 20, 335, -1));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.TasaDeTrafico"));
        panelAvanzado.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 90, 100, -1));

        etiquetaTasa.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaTasa.setForeground(new java.awt.Color(102, 102, 102));
        etiquetaTasa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        etiquetaTasa.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Kbpsinicial"));
        panelAvanzado.add(etiquetaTasa, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, 70, -1));

        selectorDeGenerarEstadisticas.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorDeGenerarEstadisticas.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.GenerarEstadisticas2"));
        selectorDeGenerarEstadisticas.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.GenerarEstadisticas2"));
        selectorDeGenerarEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnGenerarEstadisticasAvanzado(evt);
            }
        });

        panelAvanzado.add(selectorDeGenerarEstadisticas, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, -1, -1));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.NivelDeGoS"));
        panelAvanzado.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 85, -1));

        selectorDeGoS.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorDeGoS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Level 1", "Level 2", "Level 3" }));
        selectorDeGoS.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.nivelDeGoS"));
        selectorDeGoS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnNivelGoS(evt);
            }
        });

        panelAvanzado.add(selectorDeGoS, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, -1, -1));

        selectorLSPDeRespaldo.setFont(new java.awt.Font("Dialog", 0, 12));
        selectorLSPDeRespaldo.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.CrearLSPBackup"));
        selectorLSPDeRespaldo.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.crearUnLSPdeBackup"));
        selectorLSPDeRespaldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnLSPDeRespaldo(evt);
            }
        });

        panelAvanzado.add(selectorLSPDeRespaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 230, -1, -1));

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.TipoDeTrafico3"));
        panelAvanzado.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 125, 100, -1));

        traficoConstante.setFont(new java.awt.Font("Dialog", 0, 12));
        traficoConstante.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.TraficoConstante"));
        traficoConstante.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.traficoConstante"));
        buttonGroup1.add(traficoConstante);
        traficoConstante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnTraficoConstante(evt);
            }
        });

        panelAvanzado.add(traficoConstante, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 125, -1, 20));

        traficoVariable.setFont(new java.awt.Font("Dialog", 0, 12));
        traficoVariable.setSelected(true);
        traficoVariable.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.TraficoVariable"));
        traficoVariable.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.traficovariable"));
        buttonGroup1.add(traficoVariable);
        traficoVariable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnTraficoVariable(evt);
            }
        });

        panelAvanzado.add(traficoVariable, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 125, -1, 20));

        encapsularSobreMPLS.setFont(new java.awt.Font("Dialog", 0, 12));
        encapsularSobreMPLS.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.EncapsularSobreMPLS"));
        encapsularSobreMPLS.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.encapsularsobrempls"));
        encapsularSobreMPLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnEncapsularSobreMPLS(evt);
            }
        });

        panelAvanzado.add(encapsularSobreMPLS, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, -1, -1));

        selectorDeTasa.setMajorTickSpacing(1000);
        selectorDeTasa.setMaximum(10240);
        selectorDeTasa.setMinimum(1);
        selectorDeTasa.setMinorTickSpacing(100);
        selectorDeTasa.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltipo.CambiarTasa"));
        selectorDeTasa.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cambioEnSelectorDeTasa(evt);
            }
        });

        panelAvanzado.add(selectorDeTasa, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 165, -1));

        selectorDeTamPaquete.setMajorTickSpacing(1000);
        selectorDeTamPaquete.setMaximum(65495);
        selectorDeTamPaquete.setMinorTickSpacing(100);
        selectorDeTamPaquete.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltipo.CambiarTasa"));
        selectorDeTamPaquete.setValue(1024);
        selectorDeTamPaquete.setEnabled(false);
        selectorDeTamPaquete.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                clicEnSelectorDeTamPaquete(evt);
            }
        });

        panelAvanzado.add(selectorDeTamPaquete, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 160, 120, -1));

        etiquetaOctetos.setFont(new java.awt.Font("Dialog", 0, 10));
        etiquetaOctetos.setForeground(new java.awt.Color(102, 102, 102));
        etiquetaOctetos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        etiquetaOctetos.setEnabled(false);
        panelAvanzado.add(etiquetaOctetos, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 160, 70, -1));

        etiquetaTamPaquete.setFont(new java.awt.Font("Dialog", 0, 12));
        etiquetaTamPaquete.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etiquetaTamPaquete.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.TamCargaUtil"));
        etiquetaTamPaquete.setEnabled(false);
        panelAvanzado.add(etiquetaTamPaquete, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 140, -1));

        panelPestanias.addTab(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Tab.Avanzada"), panelAvanzado);

        panelPrincipal.add(panelPestanias, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 370, 330));

        getContentPane().add(panelPrincipal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 350));

        panelBotones.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton2.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.ACEPTAR));
        jButton2.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.botones.Aceptar").charAt(0));
        jButton2.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Boton.Aceptar.Texto"));
        jButton2.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.Aceptar"));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnAceptar(evt);
            }
        });

        panelBotones.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, 110, -1));

        jButton3.setFont(new java.awt.Font("Dialog", 0, 12));
        jButton3.setIcon(dispensadorDeImagenes.obtenerIcono(TDispensadorDeImagenes.CANCELAR));
        jButton3.setMnemonic(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.botones.Cancelar").charAt(0));
        jButton3.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Boton.Cancelar.Texto"));
        jButton3.setToolTipText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.tooltip.Cancelar"));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicEnCancelar(evt);
            }
        });

        panelBotones.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 15, 110, -1));

        getContentPane().add(panelBotones, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 340, 400, 50));

        pack();
    }//GEN-END:initComponents

    private void clicEnSelectorSencilloTrafico(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnSelectorSencilloTrafico
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
    }//GEN-LAST:event_clicEnSelectorSencilloTrafico

    private void clicEnSelectorDeTamPaquete(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_clicEnSelectorDeTamPaquete
    int tamSeleccionado = this.selectorDeTamPaquete.getValue();
    String unidades = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Octetos");
    this.etiquetaOctetos.setText(tamSeleccionado + " " +unidades);
    }//GEN-LAST:event_clicEnSelectorDeTamPaquete

private void clicEnGenerarEstadisticasAvanzado(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnGenerarEstadisticasAvanzado
    this.selectorDeGenerarEstadisticasSencillo.setSelected(this.selectorDeGenerarEstadisticas.isSelected());
}//GEN-LAST:event_clicEnGenerarEstadisticasAvanzado

private void clicEnGenerarEstadisticasSencillo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnGenerarEstadisticasSencillo
    this.selectorDeGenerarEstadisticas.setSelected(this.selectorDeGenerarEstadisticasSencillo.isSelected());
}//GEN-LAST:event_clicEnGenerarEstadisticasSencillo

private void clicEnLSPDeRespaldo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnLSPDeRespaldo
    this.selectorSencilloTrafico.setSelectedIndex(0);
}//GEN-LAST:event_clicEnLSPDeRespaldo

private void clicEnNivelGoS(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnNivelGoS
    this.selectorSencilloTrafico.setSelectedIndex(0);
}//GEN-LAST:event_clicEnNivelGoS

private void clicEnEncapsularSobreMPLS(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnEncapsularSobreMPLS
    this.selectorSencilloTrafico.setSelectedIndex(0);
}//GEN-LAST:event_clicEnEncapsularSobreMPLS

private void clicEnTraficoVariable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnTraficoVariable
    this.selectorSencilloTrafico.setSelectedIndex(0);
    this.selectorDeTamPaquete.setEnabled(false);
    this.etiquetaOctetos.setEnabled(false);
    this.etiquetaTamPaquete.setEnabled(false);
}//GEN-LAST:event_clicEnTraficoVariable

private void clicEnTraficoConstante(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnTraficoConstante
    this.selectorSencilloTrafico.setSelectedIndex(0);
    this.selectorDeTamPaquete.setEnabled(true);
    this.etiquetaOctetos.setEnabled(true);
    this.etiquetaTamPaquete.setEnabled(true);
}//GEN-LAST:event_clicEnTraficoConstante

private void cambioEnSelectorDeTasa(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cambioEnSelectorDeTasa
    this.selectorSencilloTrafico.setSelectedIndex(0);
    int tasaSeleccionada = this.selectorDeTasa.getValue();
    String unidades = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.unidades.kbps");
    this.etiquetaTasa.setText(tasaSeleccionada + " " +unidades);
}//GEN-LAST:event_cambioEnSelectorDeTasa

private void clicEnCancelar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnCancelar
    if (reconfigurando) {
        emisor.ponerDestino(BKUPDestino);
        emisor.ponerLSPDeBackup(BKUPLSPDeBackup);
        emisor.ponerMostrarNombre(BKUPMostrarNombre);
        emisor.ponerNivelDeGoS(BKUPNivelDeGos);
        emisor.ponerNombre(BKUPNombre);
        emisor.ponerTasaTrafico(BKUPTasaTrafico);
        emisor.ponerTipoTrafico(BKUPTipoTrafico);
        emisor.ponerEstadisticas(BKUPGenerarEstadisticas);
        emisor.ponerTamDatosConstante(BKUPTamDatosConstante);
        emisor.ponerBienConfigurado(true);
        emisor.ponerSobreMPLS(BKUPEncapsularEnMPLS);
        reconfigurando = false;
    } else {
        emisor.ponerBienConfigurado(false);
    }
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_clicEnCancelar

private void clicEnAceptar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clicEnAceptar
    emisor.ponerBienConfigurado(true);
    if (!this.reconfigurando){
        emisor.ponerPosicion(new Point(panelCoordenadas.obtenerXReal(),panelCoordenadas.obtenerYReal()));
    }
    emisor.ponerNombre(nombreNodo.getText());
    emisor.ponerMostrarNombre(verNombre.isSelected());
    emisor.ponerEstadisticas(this.selectorDeGenerarEstadisticas.isSelected());
    emisor.ponerTasaTrafico(this.selectorDeTasa.getValue());
    emisor.ponerLSPDeBackup(this.selectorLSPDeRespaldo.isSelected());
    emisor.ponerSobreMPLS(this.encapsularSobreMPLS.isSelected());
    emisor.ponerNivelDeGoS(this.selectorDeGoS.getSelectedIndex());
    emisor.ponerDestino((String) this.selectorDelReceptor.getSelectedItem());
    emisor.ponerTamDatosConstante(this.selectorDeTamPaquete.getValue());
    if (this.traficoConstante.isSelected()) {
        emisor.ponerTipoTrafico(TNodoEmisor.CONSTANTE);
    } else if (this.traficoVariable.isSelected()) {
        emisor.ponerTipoTrafico(TNodoEmisor.VARIABLE);
    }
    int error = emisor.comprobar(topo, this.reconfigurando);
    if (error != TNodoEmisor.CORRECTA) {
        JVentanaAdvertencia va = new JVentanaAdvertencia(ventanaPadre, true, dispensadorDeImagenes);
        va.mostrarMensaje(emisor.obtenerMensajeError(error));
        va.show();
    } else {
        this.reconfigurando = false;
        this.setVisible(false);
        this.dispose();
    }
}//GEN-LAST:event_clicEnAceptar

private void clicEnPanelCoordenadas(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clicEnPanelCoordenadas
    if (evt.getButton() == MouseEvent.BUTTON1) {
        panelCoordenadas.ponerCoordenadasReducidas(evt.getPoint());
        coordenadaX.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.X=_") + panelCoordenadas.obtenerXReal());
        coordenadaY.setText(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("VentanaEmisor.Y=_") + panelCoordenadas.obtenerYReal());
        panelCoordenadas.repaint();
    }
}//GEN-LAST:event_clicEnPanelCoordenadas

private void ratonSaleDePanelCoordenadas(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonSaleDePanelCoordenadas
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_ratonSaleDePanelCoordenadas

private void ratonEntraEnPanelCoordenadas(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratonEntraEnPanelCoordenadas
    this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
}//GEN-LAST:event_ratonEntraEnPanelCoordenadas

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        emisor.ponerBienConfigurado(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    /**
     * Este método permite cargar en la ventana la configuración actual del nodo emisor
     * que estamos configurando.
     * @since 1.0
     * @param recfg TRUE indica que estamos reconfigurando el emisor. FALSE, que el emisor está
     * siendo insertado desde cero.
     * @param tce Nodo emisor que estamos configurando.
     */    
    public void ponerConfiguracion(TNodoEmisor tce, boolean recfg) {
        emisor = tce;
        reconfigurando = recfg;
        if (reconfigurando) {
            this.panelCoordenadas.setEnabled(false);
            this.panelCoordenadas.setToolTipText(null);
            TNodoTopologia nt = this.topo.obtenerNodo(emisor.obtenerDestino());
            if (nt != null) {
                BKUPDestino = nt.obtenerNombre();
            }
            BKUPLSPDeBackup = emisor.obtenerLSPDeBackup();
            BKUPMostrarNombre = emisor.obtenerMostrarNombre();
            BKUPNivelDeGos = emisor.obtenerNivelDeGoS();
            BKUPNombre = emisor.obtenerNombre();
            BKUPTasaTrafico = emisor.obtenerTasaTrafico();
            BKUPTipoTrafico = emisor.obtenerTipoTrafico();
            BKUPGenerarEstadisticas = emisor.obtenerEstadisticas();
            BKUPTamDatosConstante = emisor.obtenerTamDatosConstante();
            this.BKUPEncapsularEnMPLS = emisor.obtenerSobreMPLS();
            
            
            this.encapsularSobreMPLS.setSelected(BKUPEncapsularEnMPLS);
            this.nombreNodo.setText(BKUPNombre);
            if (BKUPTipoTrafico == TNodoEmisor.CONSTANTE) {
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

            if (BKUPTipoTrafico == TNodoEmisor.CONSTANTE) {
                this.selectorDeTamPaquete.setEnabled(true);
                this.etiquetaOctetos.setEnabled(true);
                this.etiquetaTamPaquete.setEnabled(true);
                String unidades = java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("JVentanaEmisor.Octetos");
                this.etiquetaOctetos.setText(this.BKUPTamDatosConstante + " " +unidades);        }
                this.selectorDeTamPaquete.setValue(this.BKUPTamDatosConstante);
            }
    }

    private TDispensadorDeImagenes dispensadorDeImagenes;
    private Frame ventanaPadre;
    private JPanelDisenio pd;
    private TNodoEmisor emisor;
    private TTopologia topo;
    
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel coordenadaX;
    private javax.swing.JLabel coordenadaY;
    private javax.swing.JCheckBox encapsularSobreMPLS;
    private javax.swing.JLabel etiquetaNombre;
    private javax.swing.JLabel etiquetaOctetos;
    private javax.swing.JLabel etiquetaTamPaquete;
    private javax.swing.JLabel etiquetaTasa;
    private javax.swing.JLabel iconoEmisor;
    private javax.swing.JLabel iconoEnlace1;
    private javax.swing.JLabel iconoEnlace2;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField nombreNodo;
    private javax.swing.JPanel panelAvanzado;
    private javax.swing.JPanel panelBotones;
    private simMPLS.interfaz.dialogos.JPanelCoordenadas panelCoordenadas;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JTabbedPane panelPestanias;
    private javax.swing.JPanel panelPosicion;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelRapido;
    private javax.swing.JCheckBox selectorDeGenerarEstadisticas;
    private javax.swing.JCheckBox selectorDeGenerarEstadisticasSencillo;
    private javax.swing.JComboBox selectorDeGoS;
    private javax.swing.JSlider selectorDeTamPaquete;
    private javax.swing.JSlider selectorDeTasa;
    private javax.swing.JComboBox selectorDelReceptor;
    private javax.swing.JCheckBox selectorLSPDeRespaldo;
    private javax.swing.JComboBox selectorSencilloTrafico;
    private javax.swing.JRadioButton traficoConstante;
    private javax.swing.JRadioButton traficoVariable;
    private javax.swing.JCheckBox verNombre;
    // End of variables declaration//GEN-END:variables

}
