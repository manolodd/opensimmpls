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
package com.manolodominguez.opensimmpls.ui.splash;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/**
 * This class implements a splash window that is shown before the simulator is
 * completely loaded and generated.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JSplash extends javax.swing.JFrame {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of JSplash.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public JSplash() {
        this.message = new JLabel();
        this.splashImageContainer = new JLabel();
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the
     * window components.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void initComponents() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.SPLASH.getPath());
        getContentPane().setLayout(new MigLayout("fillx, filly"));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        setName(this.translations.getString("JSplash"));
        setResizable(false);
        setUndecorated(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });
        this.splashImageContainer.setIcon(new ImageIcon(getClass().getResource(this.translations.getString("/imagenes/splash_inicio.png"))));
        getContentPane().add(this.splashImageContainer, "wrap");
        this.message.setFont(new Font("Arial", 0, 12));
        this.message.setText(this.translations.getString("Starting..."));
        getContentPane().add(this.message, "align center");
        getContentPane().setBackground(Color.WHITE);
        pack();
        Dimension frameSize = this.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        setIconImage(new ImageIcon(this.getClass().getResource(this.translations.getString("/imagenes/splash_menu.png"))).getImage());
    }

    /**
     * This method is called when the splash window is closed.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param evt The event that triggers this method.
     * @since 2.0
     */
    private void exitForm(WindowEvent evt) {
        this.dispose();
    }

    /**
     * This method set the text message that is going to be shown in the splash
     * window.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param message the text message that is going to be shown in the splash
     * window.
     * @since 2.0
     */
    public void setMessage(String message) {
        this.message.setText(message);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(JSplash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JLabel splashImageContainer;
    private JLabel message;
    private ResourceBundle translations;
}
