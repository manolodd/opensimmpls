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
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class JErrorWindow extends JDialog {

    /**
     * Crea una nueva ventana de error.
     *
     * @param parent Ventana padre donde se ubicar� esta ventana de tipo
     * JVentanaError.
     * @param modal TRUE indica que la ventana impedir� que se pueda seleccionar
     * nada de la interfaz de usuario hasta que se cierre. FALSE indica que esto
     * no es as�.
     * @param imageBroker Dispensador de im�genes global de la aplicaci�n.
     * @since 2.0
     */
    public JErrorWindow(Frame parent, boolean modal, TImageBroker imageBroker) {
        super(parent, modal);
        this.imageBroker = imageBroker;
        initComponents();
        initComponents2();
    }

    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.ERROR_WINDOW.getPath());
        this.iconContainerError = new JLabel();
        this.buttonOk = new JButton();
        this.textPaneErrorMessage = new JTextPane();
        this.mainPanel = new JPanel();
        setTitle(this.translations.getString("VentanaError.titulo"));
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleWindowsClosing(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());
        this.iconContainerError.setIcon(this.imageBroker.getIcon(TImageBroker.ERROR));
        getContentPane().add(this.iconContainerError, new AbsoluteConstraints(10, 15, -1, -1));
        this.buttonOk.setFont(new Font("Dialog", 0, 12));
        this.buttonOk.setIcon(this.imageBroker.getIcon(TImageBroker.ACEPTAR));
        this.buttonOk.setMnemonic(this.translations.getString("VentanaError.ResaltadoBoton").charAt(0));
        this.buttonOk.setText(this.translations.getString("OK"));
        this.buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnOkButton(evt);
            }
        });
        getContentPane().add(this.buttonOk, new AbsoluteConstraints(125, 80, 105, -1));
        this.textPaneErrorMessage.setBackground(new Color(0, 0, 0, 0));
        this.textPaneErrorMessage.setEditable(false);
        this.textPaneErrorMessage.setDisabledTextColor(new Color(0, 0, 0));
        this.textPaneErrorMessage.setFocusCycleRoot(false);
        this.textPaneErrorMessage.setFocusable(false);
        this.textPaneErrorMessage.setEnabled(false);
        getContentPane().add(this.textPaneErrorMessage, new AbsoluteConstraints(55, 15, 285, 50));
        this.mainPanel.setLayout(null);
        getContentPane().add(this.mainPanel, new AbsoluteConstraints(0, 0, 350, 120));
        pack();
    }

    private void initComponents2() {
        Dimension frameSize = this.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

    private void handleClickOnOkButton(ActionEvent evt) {
        setVisible(false);
        dispose();
    }

    private void handleWindowsClosing(WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    public void setErrorMessage(String errorMessage) {
        Toolkit.getDefaultToolkit().beep();
        this.textPaneErrorMessage.setText(errorMessage);
    }

    private TImageBroker imageBroker;
    private JButton buttonOk;
    private JLabel iconContainerError;
    private JPanel mainPanel;
    private JTextPane textPaneErrorMessage;
    private ResourceBundle translations;
}
