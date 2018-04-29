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

import com.manolodominguez.opensimmpls.resources.license.AvailableLicenseFiles;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
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

public class JLicenseWindow extends JDialog {

    public JLicenseWindow(Frame parent, boolean modal, TImageBroker di) {
        super(parent, modal);
        dispensadorDeImagenes = di;
        initComponents();
        this.setSize(570, 425);
        Dimension tamFrame = this.getSize();
        Dimension tamPantalla = this.getParent().getSize();
        setLocation((tamPantalla.width - tamFrame.width) / 2, (tamPantalla.height - tamFrame.height) / 2);
    }

    private void initComponents() {

        scrollPaneLicense = new JScrollPane();
        textPaneLicense = new JTextPane();
        buttonOK = new JButton();
        licenseIcon = new JLabel();
        jTextPane2 = new JTextPane();

        ResourceBundle bundle = ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations");
        setTitle(bundle.getString("Open_SimMPLS_license"));
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());

        scrollPaneLicense.setBorder(null);
        scrollPaneLicense.setViewportBorder(BorderFactory.createTitledBorder(""));
        scrollPaneLicense.setAutoscrolls(true);

        textPaneLicense.setEditable(false);
        textPaneLicense.setBackground(new Color(246, 247, 224));
        try {
            textPaneLicense.read(this.getClass().getResourceAsStream(AvailableLicenseFiles.LICENSE_FILE.getPath()), this);
        } catch (FileNotFoundException ex) {
            //FIX: This is ugly
            ex.printStackTrace();
        } catch (IOException ex) {
            //FIX: This is ugly
            ex.printStackTrace();
        }
        textPaneLicense.setCaretPosition(1);
        scrollPaneLicense.setViewportView(textPaneLicense);

        getContentPane().add(scrollPaneLicense, new AbsoluteConstraints(15, 130, 535, 210));

        buttonOK.setFont(new Font("Dialog", 0, 12));
        buttonOK.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.ACEPTAR));
        buttonOK.setMnemonic(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("VentanaLicencia.ResaltadoBoton").charAt(0));
        buttonOK.setText(bundle.getString("OK"));
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ClicEnElBoton(evt);
            }
        });
        getContentPane().add(buttonOK, new AbsoluteConstraints(15, 360, 100, -1));

        licenseIcon.setIcon(dispensadorDeImagenes.getIcon(TImageBroker.OSI_CERTIFIED)
        );
        licenseIcon.setFocusable(false);
        licenseIcon.setBackground(new Color(0, 0, 0, 0));
        licenseIcon.setDoubleBuffered(true);
        licenseIcon.setFocusable(false);
        getContentPane().add(licenseIcon, new AbsoluteConstraints(15, 15, 120, 100));

        jTextPane2.setEditable(false);
        jTextPane2.setBackground(new Color(0, 0, 0, 0));
        jTextPane2.setDisabledTextColor(new Color(0, 0, 0));
        jTextPane2.setText(bundle.getString("Open_SimMPLS_is_an_Open_Source_Initiative_Certified_software_free_under_the_terms_of_the_GNU_General_Public_License_as_shown_below."));
        jTextPane2.setAutoscrolls(false);
        jTextPane2.setEnabled(false);
        jTextPane2.setFocusable(false);
        getContentPane().add(jTextPane2, new AbsoluteConstraints(150, 35, 370, 65));

        pack();
    }

    private void ClicEnElBoton(java.awt.event.ActionEvent evt) {
        setVisible(false);
        dispose();
    }

    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    private TImageBroker dispensadorDeImagenes;
    private javax.swing.JButton buttonOK;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JLabel licenseIcon;
    private javax.swing.JScrollPane scrollPaneLicense;
    private javax.swing.JTextPane textPaneLicense;
}
