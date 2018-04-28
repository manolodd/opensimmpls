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
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * This class implements a window that is used to show different help ways to
 * the user.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JHelpWindow extends JDialog {

    /**
     * This is the constructor of the class and creates a new instance of
     * JHelpWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param parent Parent window over wich this JHelpWindow is shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param modal TRUE, if this dialog has to be modal. Otherwise, FALSE.
     * @since 2.0
     */
    public JHelpWindow(Frame parent, boolean modal, TImageBroker imageBroker) {
        super(parent, modal);
        this.imageBroker = imageBroker;
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
        this.translations = ResourceBundle.getBundle(AvailableBundles.HELP_WINDOW.getPath());
        this.mainPanel = new JPanel();
        this.labelTextLine4 = new JLabel();
        this.labelTextLine3 = new JLabel();
        this.labelTextLine2 = new JLabel();
        this.labelTextLine1 = new JLabel();
        this.iconContainerWebCapture = new JLabel();
        this.labelHelpTitle = new JLabel();
        this.labelOpenSimMPLSWeb = new JLabel();
        this.buttonPanel = new JPanel();
        this.buttonOK = new JButton();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(this.translations.getString("JVentanaAyuda.Contents"));
        setResizable(false);
        getContentPane().setLayout(new FlowLayout());
        this.mainPanel.setBorder(BorderFactory.createEtchedBorder());
        this.mainPanel.setLayout(new AbsoluteLayout());
        this.labelTextLine4.setFont(new Font("Dialog", 0, 12));
        this.labelTextLine4.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelTextLine4.setText(this.translations.getString("JVentanaAyuda.-_We_apologize_for_the_inconvenence_-"));
        this.mainPanel.add(this.labelTextLine4, new AbsoluteConstraints(0, 150, 420, -1));
        this.labelTextLine3.setFont(new Font("Dialog", 0, 12));
        this.labelTextLine3.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelTextLine3.setText(this.translations.getString("JVentanaAyuda.Open_SimMPLS_1.0_web_site."));
        this.mainPanel.add(this.labelTextLine3, new AbsoluteConstraints(0, 120, 420, -1));
        this.labelTextLine2.setFont(new Font("Dialog", 0, 12));
        this.labelTextLine2.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelTextLine2.setText(this.translations.getString("JVentanaAyuda.is_availabe_only_for_download_in_PDF_format_from_the_official"));
        this.mainPanel.add(labelTextLine2, new AbsoluteConstraints(0, 100, 420, -1));
        this.labelTextLine1.setFont(new Font("Dialog", 0, 12));
        this.labelTextLine1.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelTextLine1.setText(this.translations.getString("JVentanaAyuda.At_the_present_time,_Open_SimMPLS_1.0_help_documentation"));
        this.mainPanel.add(this.labelTextLine1, new AbsoluteConstraints(0, 80, 420, -1));
        this.iconContainerWebCapture.setIcon(imageBroker.getIcon(TImageBroker.CAPTURAWEB));
        this.mainPanel.add(this.iconContainerWebCapture, new AbsoluteConstraints(110, 190, -1, -1));
        this.labelHelpTitle.setFont(new Font("Dialog", 1, 12));
        this.labelHelpTitle.setIcon(imageBroker.getIcon(TImageBroker.TUTORIAL));
        this.labelHelpTitle.setText(this.translations.getString("JVentanaAyuda.Open_SimMPLS_1.0_Help"));
        this.mainPanel.add(this.labelHelpTitle, new AbsoluteConstraints(20, 20, 360, -1));
        this.labelOpenSimMPLSWeb.setFont(new Font("Monospaced", 0, 12));
        this.labelOpenSimMPLSWeb.setForeground(new Color(0, 0, 255));
        this.labelOpenSimMPLSWeb.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelOpenSimMPLSWeb.setText(this.translations.getString("JVentanaAyuda.WebPatanegra"));
        this.mainPanel.add(this.labelOpenSimMPLSWeb, new AbsoluteConstraints(4, 370, 420, 20));
        this.buttonPanel.setOpaque(false);
        this.buttonOK.setIcon(this.imageBroker.getIcon(TImageBroker.ACEPTAR));
        this.buttonOK.setMnemonic(this.translations.getString("VentanaAyuda.OK.Mnemonico").charAt(0));
        this.buttonOK.setText(this.translations.getString("VentanaAyuda.OK"));
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOKButton(evt);
            }
        });
        this.buttonPanel.add(this.buttonOK);
        this.mainPanel.add(this.buttonPanel, new AbsoluteConstraints(0, 400, 420, 50));
        getContentPane().add(this.mainPanel);
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
        Dimension frameSize = this.getSize();
        Dimension parentSize = this.getParent().getSize();
        setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);
    }

    /**
     * This method is called when a click is done on the displayed button (in
     * the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnOKButton(ActionEvent evt) {
        setVisible(false);
        this.dispose();
    }

    private JButton buttonOK;
    private JLabel labelHelpTitle;
    private JLabel iconContainerWebCapture;
    private JLabel labelTextLine1;
    private JLabel labelTextLine2;
    private JLabel labelTextLine3;
    private JLabel labelTextLine4;
    private JLabel labelOpenSimMPLSWeb;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private TImageBroker imageBroker;
    private ResourceBundle translations;
}
