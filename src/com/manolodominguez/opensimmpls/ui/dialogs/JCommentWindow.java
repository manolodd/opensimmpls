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

import com.manolodominguez.opensimmpls.io.net.TBasicEmailSender;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import com.manolodominguez.opensimmpls.ui.utils.TImageBroker;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class JCommentWindow extends JDialog {

    public JCommentWindow(TImageBroker imageBroker, Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.imageBroker = imageBroker;
        this.basicEmailSender = new TBasicEmailSender();
        initComponents();
        initComponents2();
    }

    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.COMMENT_WINDOW.getPath());
        this.mainPanel = new JPanel();
        this.panelButtons = new JPanel();
        this.buttonSend = new JButton();
        this.buttonCancel = new JButton();
        this.iconContainerComment = new JLabel();
        this.scrollPanelComment = new JScrollPane();
        this.textAreaComment = new JTextArea();
        this.labelSMTPServer = new JLabel();
        this.textFieldSMTPAddress = new JTextField();
        this.labelEmail = new JLabel();
        this.textFieldSender = new JTextField();
        this.labelComment = new JLabel();
        setTitle(this.translations.getString("JVentanaComentario.ContactarAutores"));
        setModal(true);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleWindowsClosing(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());
        this.mainPanel.setLayout(new AbsoluteLayout());
        this.panelButtons.setLayout(new AbsoluteLayout());
        this.buttonSend.setFont(new Font("Dialog", 0, 12));
        this.buttonSend.setIcon(this.imageBroker.getIcon(TImageBroker.SOBRE_EMAIL));
        this.buttonSend.setMnemonic(this.translations.getString("JVentanaComentario.mnemonico.enviar").charAt(0));
        this.buttonSend.setText(this.translations.getString("JVentanaComentario.Send"));
        this.buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnSendButton(evt);
            }
        });
        this.panelButtons.add(this.buttonSend, new AbsoluteConstraints(15, 15, 110, -1));
        this.buttonCancel.setFont(new Font("Dialog", 0, 12));
        this.buttonCancel.setIcon(imageBroker.getIcon(TImageBroker.CANCELAR));
        this.buttonCancel.setMnemonic(this.translations.getString("JVentanaComentario.mnemonico.cancelar").charAt(0));
        this.buttonCancel.setText(this.translations.getString("JVentanaComentario.Cancel"));
        this.buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnCancelButton(evt);
            }
        });
        this.panelButtons.add(this.buttonCancel, new AbsoluteConstraints(140, 15, 110, -1));
        this.mainPanel.add(this.panelButtons, new AbsoluteConstraints(0, 255, 400, 55));
        this.iconContainerComment.setIcon(this.imageBroker.getIcon(TImageBroker.COMENTARIO));
        this.iconContainerComment.setText(this.translations.getString("JVentanaComentario.tooltip.send"));
        this.mainPanel.add(this.iconContainerComment, new AbsoluteConstraints(15, 15, -1, -1));
        this.textAreaComment.setLineWrap(true);
        this.scrollPanelComment.setViewportView(this.textAreaComment);
        this.mainPanel.add(this.scrollPanelComment, new AbsoluteConstraints(15, 165, 370, 90));
        this.labelSMTPServer.setFont(new Font("Dialog", 0, 12));
        this.labelSMTPServer.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelSMTPServer.setText(this.translations.getString("JVentanaComentario.tooltip.SMTPServer"));
        this.mainPanel.add(this.labelSMTPServer, new AbsoluteConstraints(15, 75, 170, -1));
        this.mainPanel.add(this.textFieldSMTPAddress, new AbsoluteConstraints(195, 75, 190, -1));
        this.labelEmail.setFont(new Font("Dialog", 0, 12));
        this.labelEmail.setHorizontalAlignment(SwingConstants.RIGHT);
        this.labelEmail.setText(this.translations.getString("JVentanaComentario.email"));
        this.mainPanel.add(this.labelEmail, new AbsoluteConstraints(15, 105, 145, -1));
        this.mainPanel.add(this.textFieldSender, new AbsoluteConstraints(175, 105, 210, -1));
        this.labelComment.setFont(new Font("Dialog", 0, 12));
        this.labelComment.setText(this.translations.getString("JVentanaComentario.Comentario"));
        this.mainPanel.add(this.labelComment, new AbsoluteConstraints(15, 145, 365, -1));
        getContentPane().add(this.mainPanel, new AbsoluteConstraints(0, 0, -1, 310));
        pack();
    }

    private void initComponents2() {
        Dimension tamFrame = this.getSize();
        Dimension tamPadre = this.parent.getSize();
        setLocation((tamPadre.width - tamFrame.width) / 2, (tamPadre.height - tamFrame.height) / 2);
    }

    private void handleClickOnCancelButton(ActionEvent evt) {
        this.setVisible(false);
        this.dispose();
    }

    private void handleClickOnSendButton(ActionEvent evt) {
        int estado = checkData();
        if (estado == OK) {
            int estadoConexion = this.basicEmailSender.configure(this.textFieldSMTPAddress.getText(), this.textFieldSender.getText());
            if (estadoConexion != TBasicEmailSender.CONNECTION_ERROR) {
                int estadoEnvio = this.basicEmailSender.sendNotification(this.textAreaComment.getText());
                if (estadoEnvio != 0) {
                    JWarningWindow va = new JWarningWindow(this.parent, true, this.imageBroker);
                    va.mostrarMensaje(this.translations.getString("VentanaComentario.ErrorEnviando"));
                    va.setVisible(true);
                } else {
                    this.setVisible(false);
                    this.dispose();
                }
            } else {
                JWarningWindow va = new JWarningWindow(this.parent, true, this.imageBroker);
                va.mostrarMensaje(this.translations.getString("JVentanaComentario.ErrorAlConectar"));
                va.setVisible(true);
            }
        } else {
            JWarningWindow va = new JWarningWindow(this.parent, true, this.imageBroker);
            switch (estado) {
                case JCommentWindow.MISSING_SMTP_SERVER: {
                    va.mostrarMensaje(this.translations.getString("JVentanaComentario.debePonerSMTP"));
                    break;
                }
                case JCommentWindow.MISSING_SENDER: {
                    va.mostrarMensaje(this.translations.getString("JVentanaComentario.DebePonerSuEmail"));
                    break;
                }
                case JCommentWindow.MISSING_COMMENT: {
                    va.mostrarMensaje(this.translations.getString("JVentanaComentario.DebePonerComentario"));
                    break;
                }
            }
            va.setVisible(true);
        }
    }

    public int checkData() {
        if (this.textFieldSMTPAddress.getText().equals("")) {
            return JCommentWindow.MISSING_SMTP_SERVER;
        }
        if (this.textFieldSender.getText().equals("")) {
            return JCommentWindow.MISSING_SENDER;
        }
        if (this.textAreaComment.getText().equals("")) {
            return JCommentWindow.MISSING_COMMENT;
        }
        return JCommentWindow.OK;
    }

    private void handleWindowsClosing(WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    private static final int OK = 0;
    private static final int MISSING_SMTP_SERVER = 1;
    private static final int MISSING_COMMENT = 2;
    private static final int MISSING_SENDER = 3;

    private TImageBroker imageBroker;
    private Frame parent;
    private TBasicEmailSender basicEmailSender;
    private JTextArea textAreaComment;
    private JButton buttonSend;
    private JButton buttonCancel;
    private JLabel iconContainerComment;
    private JLabel labelSMTPServer;
    private JLabel labelEmail;
    private JLabel labelComment;
    private JScrollPane scrollPanelComment;
    private JPanel panelButtons;
    private JPanel mainPanel;
    private JTextField textFieldSender;
    private JTextField textFieldSMTPAddress;
    private ResourceBundle translations;
}
