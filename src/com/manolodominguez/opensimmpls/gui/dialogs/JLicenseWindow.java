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
import com.manolodominguez.opensimmpls.resources.license.AvailableLicenseFiles;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.gui.utils.TImageBroker;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * This class implements a window that is used to show information related to
 * the license of OpenSimMPLS.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JLicenseWindow extends JDialog {

    /**
     * This is the constructor of the class and creates a new instance of
     * JLicenseWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param parent Parent window over wich this JLicenseWindow is shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param modal TRUE, if this dialog has to be modal. Otherwise, FALSE.
     * @since 2.0
     */
    public JLicenseWindow(Frame parent, boolean modal, TImageBroker imageBroker) {
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
        this.translations = ResourceBundle.getBundle(AvailableBundles.LICENSE_WINDOW.getPath());
        this.scrollPaneLicense = new JScrollPane();
        this.textPaneLicenseText = new JTextPane();
        this.buttonOK = new JButton();
        this.iconContainerLicense = new JLabel();
        this.textPaneShortLicenseDescription = new JTextPane();
        setTitle(this.translations.getString("Open_SimMPLS_license"));
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleWindowsClosing(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());
        this.scrollPaneLicense.setBorder(null);
        this.scrollPaneLicense.setViewportBorder(BorderFactory.createTitledBorder(""));
        this.scrollPaneLicense.setAutoscrolls(true);
        this.textPaneLicenseText.setEditable(false);
        this.textPaneLicenseText.setBackground(new Color(246, 247, 224));
        try {
            this.textPaneLicenseText.read(this.getClass().getResourceAsStream(AvailableLicenseFiles.LICENSE_FILE.getPath()), this);
        } catch (FileNotFoundException ex) {
            //FIX: This is ugly
            ex.printStackTrace();
        } catch (IOException ex) {
            //FIX: This is ugly
            ex.printStackTrace();
        }
        this.textPaneLicenseText.setCaretPosition(1);
        this.scrollPaneLicense.setViewportView(this.textPaneLicenseText);
        getContentPane().add(this.scrollPaneLicense, new AbsoluteConstraints(15, 130, 535, 210));
        this.buttonOK.setFont(new Font("Dialog", 0, 12));
        this.buttonOK.setIcon(this.imageBroker.getImageIcon(AvailableImages.ACCEPT));
        this.buttonOK.setMnemonic(this.translations.getString("VentanaLicencia.ResaltadoBoton").charAt(0));
        this.buttonOK.setText(this.translations.getString("OK"));
        this.buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOkButton(evt);
            }
        });
        getContentPane().add(this.buttonOK, new AbsoluteConstraints(15, 360, 100, -1));
        this.iconContainerLicense.setIcon(imageBroker.getImageIcon(AvailableImages.LICENSE_LOGO)
        );
        this.iconContainerLicense.setFocusable(false);
        this.iconContainerLicense.setBackground(new Color(0, 0, 0, 0));
        this.iconContainerLicense.setDoubleBuffered(true);
        this.iconContainerLicense.setFocusable(false);
        getContentPane().add(this.iconContainerLicense, new AbsoluteConstraints(15, 15, 120, 100));
        this.textPaneShortLicenseDescription.setEditable(false);
        this.textPaneShortLicenseDescription.setBackground(new Color(0, 0, 0, 0));
        this.textPaneShortLicenseDescription.setDisabledTextColor(new Color(0, 0, 0));
        this.textPaneShortLicenseDescription.setText(this.translations.getString("Open_SimMPLS_is_an_Open_Source_Initiative_Certified_software_free_under_the_terms_of_the_GNU_General_Public_License_as_shown_below."));
        this.textPaneShortLicenseDescription.setAutoscrolls(false);
        this.textPaneShortLicenseDescription.setEnabled(false);
        this.textPaneShortLicenseDescription.setFocusable(false);
        getContentPane().add(this.textPaneShortLicenseDescription, new AbsoluteConstraints(150, 35, 370, 65));
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
        this.setSize(570, 425);
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
    private void handleClickOnOkButton(ActionEvent evt) {
        setVisible(false);
        dispose();
    }

    /**
     * This method is called when the JErrorWindow is closed (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleWindowsClosing(WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    private TImageBroker imageBroker;
    private JButton buttonOK;
    private JTextPane textPaneShortLicenseDescription;
    private JLabel iconContainerLicense;
    private JScrollPane scrollPaneLicense;
    private JTextPane textPaneLicenseText;
    private ResourceBundle translations;
}
