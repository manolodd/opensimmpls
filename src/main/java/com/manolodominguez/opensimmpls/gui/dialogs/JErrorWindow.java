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
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.gui.utils.TImageBroker;
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
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * This class implements a window that is used to show error messages.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
@SuppressWarnings("serial")
public class JErrorWindow extends JDialog {

    /**
     * This is the constructor of the class and creates a new instance of
     * JErrorWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param parent Parent window over wich this JErrorWindow is shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param modal TRUE, if this dialog has to be modal. Otherwise, FALSE.
     * @since 2.0
     */
    public JErrorWindow(Frame parent, boolean modal, TImageBroker imageBroker) {
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
        this.iconContainerError.setIcon(this.imageBroker.getImageIcon(AvailableImages.ERROR));
        getContentPane().add(this.iconContainerError, new AbsoluteConstraints(10, 15, -1, -1));
        this.buttonOk.setFont(new Font("Dialog", 0, 12));
        this.buttonOk.setIcon(this.imageBroker.getImageIcon(AvailableImages.ACCEPT));
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

    /**
     * This method sets the error message to alert of a given error; the error
     * message will be displayed in the JErrorWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param errorMessage the error message to alert of a given error.
     * @since 2.0
     */
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
