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
import com.manolodominguez.opensimmpls.gui.utils.TImageBroker;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * This class implements a dialog that is used to show information about
 * OpenSimMPLS.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
@SuppressWarnings("serial")
public class JAboutWindow extends JDialog {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of JAboutWindow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param parent Parent window in wich this dialog will be shown.
     * @param modal this defines whether the dialog should be modal (if true) or
     * not (if false).
     * @param imageBroker this is a class that will supply the needed prealoaded
     * images to be inserted in the dialog.
     * @since 2.0
     */
    public JAboutWindow(Frame parent, boolean modal, TImageBroker imageBroker) {
        super(parent, modal);
        this.imageBroker = imageBroker;
        initComponents();
    }

    /**
     * This method is called by the constructor to initialize attributes value.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void initComponents() {
        this.iconContainer = new JLabel();
        getContentPane().setLayout(new AbsoluteLayout());
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setModal(true);
        setResizable(false);
        setUndecorated(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                closeDialog(evt);
            }
        });

        this.iconContainer.setHorizontalAlignment(SwingConstants.CENTER);
        this.iconContainer.setIcon(this.imageBroker.getImageIcon(AvailableImages.SPLASH_ABOUT));
        getContentPane().add(this.iconContainer, new AbsoluteConstraints(0, 0, -1, 300));
        pack();
        Dimension frameSize = this.getSize();
        Dimension parentSize = this.getParent().getSize();
        setLocation((parentSize.width - frameSize.width) / 2, (parentSize.height - frameSize.height) / 2);

    }

    /**
     * This method closes the dialog once the user clic on it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private void closeDialog(MouseEvent evt) {
        setVisible(false);
        this.dispose();
    }

    private TImageBroker imageBroker;
    private JLabel iconContainer;
}
