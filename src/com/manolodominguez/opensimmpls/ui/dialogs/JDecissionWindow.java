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
import javax.swing.WindowConstants;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * This class implements a window that is used to get a Yes/No answer from the
 * user.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JDecissionWindow extends JDialog {

    /**
     * This is the constructor of the class and creates a new instance of
     * JDecissionWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param parent Parent window over wich this JDecissionWindow is shown.
     * @param imageBroker An object that supply the needed images to be inserted
     * in the UI.
     * @param modal TRUE, if this dialog has to be modal. Otherwise, FALSE.
     * @since 2.0
     */
    public JDecissionWindow(Frame parent, boolean modal, TImageBroker imageBroker) {
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
        this.translations = ResourceBundle.getBundle(AvailableBundles.DECISSION_WINDOW.getPath());
        this.iconContainerQuestionMark = new JLabel();
        this.buttonYes = new JButton();
        this.buttonNo = new JButton();
        this.textPaneQuestion = new JTextPane();
        this.mainPanel = new JPanel();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(this.translations.getString("VentanaAdvertencia.titulo"));
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleWindowsClosing(evt);
            }
        });
        getContentPane().setLayout(new AbsoluteLayout());
        this.iconContainerQuestionMark.setIcon(this.imageBroker.getIcon(TImageBroker.INTERROGACION));
        getContentPane().add(this.iconContainerQuestionMark, new AbsoluteConstraints(10, 15, -1, -1));
        this.buttonYes.setFont(new Font("Dialog", 0, 12));
        this.buttonYes.setIcon(this.imageBroker.getIcon(TImageBroker.ACEPTAR));
        this.buttonYes.setMnemonic(this.translations.getString("JVentanaBooleana.mnemonico.si").charAt(0));
        this.buttonYes.setText(this.translations.getString("JVentanaBooleana.Si"));
        this.buttonYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnYesButton(evt);
            }
        });
        getContentPane().add(buttonYes, new AbsoluteConstraints(60, 80, 105, -1));
        this.buttonNo.setFont(new Font("Dialog", 0, 12));
        this.buttonNo.setIcon(imageBroker.getIcon(TImageBroker.CANCELAR));
        this.buttonNo.setMnemonic(this.translations.getString("JVentanaBooleana.mnemonico.no").charAt(0));
        this.buttonNo.setText(this.translations.getString("JVentanaBooleana.No"));
        this.buttonNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleClickOnNoButton(evt);
            }
        });
        getContentPane().add(buttonNo, new AbsoluteConstraints(180, 80, 105, -1));
        this.textPaneQuestion.setBackground(UIManager.getDefaults().getColor("Button.background"));
        this.textPaneQuestion.setEditable(false);
        this.textPaneQuestion.setDisabledTextColor(new Color(0, 0, 0));
        this.textPaneQuestion.setFocusCycleRoot(false);
        this.textPaneQuestion.setFocusable(false);
        this.textPaneQuestion.setEnabled(false);
        getContentPane().add(textPaneQuestion, new AbsoluteConstraints(55, 15, 285, 50));
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
        Dimension parentSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);
        this.userAnswer = false;
    }

    /**
     * This method is called when a click is done on "No" button (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnNoButton(ActionEvent evt) {
        setVisible(false);
        this.userAnswer = false;
    }

    /**
     * This method is called when a click is done on "Yes" button (in the UI).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void handleClickOnYesButton(ActionEvent evt) {
        setVisible(false);
        this.userAnswer = true;
    }

    /**
     * This method returns the user asnwer to the question of this
     * JDecissionWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE if the user answered Yes. Otherwise, FALSE.
     * @since 2.0
     */
    public boolean getUserAnswer() {
        return this.userAnswer;
    }

    /**
     * This method is called when the JCommentWindow is closed (in the UI).
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
     * This method sets the question the user has to answer; the question will
     * be displayed in the JDecissionWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param question the question the user has to answer.
     * @since 2.0
     */
    public void setQuestion(String question) {
        Toolkit.getDefaultToolkit().beep();
        this.textPaneQuestion.setText(question);
    }

    private boolean userAnswer;
    private TImageBroker imageBroker;
    private JButton buttonYes;
    private JButton buttonNo;
    private JLabel iconContainerQuestionMark;
    private JPanel mainPanel;
    private JTextPane textPaneQuestion;
    private ResourceBundle translations;
}
