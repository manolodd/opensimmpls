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
package com.manolodominguez.opensimmpls.main;

import com.manolodominguez.opensimmpls.gui.utils.TImageBroker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import com.manolodominguez.opensimmpls.gui.simulator.JOpenSimMPLS;
import com.manolodominguez.opensimmpls.gui.splash.JSplash;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

/**
 * This class implements a new OpenSimMPLS network simulator. This is the main
 * class, that should be executed to run the simulator.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class openSimMPLS {

    /**
     * This method shows licensing information of OpenSimMPLS on console.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    // FIX: Remo this and replace by Apache 2.0 software license
    public static void mostrarGPL() {
        try {
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Open_SimMPLS_1.0_"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Guarantee_of_Service_(GoS)_support_over_MPLS_using_active_techniques."));
            System.out.println();
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("(C)_Copyright_2004"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Manuel_Dom�nguez_Dorado"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("manolodd@eresmas.com"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("http://manolodd.virtualave.net"));
            System.out.println();
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("This_program_is_free_software;_you_can_redistribute_it_and/or_modify"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("it_under_the_terms_of_the_GNU_General_Public_License_as_published_by"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("the_Free_Software_Foundation;_either_version_2_of_the_License,_or"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("(at_your_option)_any_later_version."));
            System.out.println();
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("This_program_is_distributed_in_the_hope_that_it_will_be_useful,"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("but_WITHOUT_ANY_WARRANTY;_without_even_the_implied_warranty_of"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("MERCHANTABILITY_or_FITNESS_FOR_A_PARTICULAR_PURPOSE.__See_the"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("GNU_General_Public_License_for_more_details."));
            System.out.println();
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("You_should_have_received_a_copy_of_the_GNU_General_Public_License"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("along_with_this_program;_if_not,_write_to_the_Free_Software"));
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Foundation,_Inc.,_59_Temple_Place,_Suite_330,_Boston,_MA__02111-1307__USA"));
            System.exit(0);
        } catch (Exception e) {
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Error_trying_to_translate."));
        }
    }

    /**
     * This method start OpenSimMPLS.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param args Arguments specified at command line.
     * @since 2.0
     */
    public static void main(String args[]) {
        if (args.length > 0) {
            mostrarGPL();
        }

        try {
            boolean nimbusSet = false;
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusSet = true;
                    break;
                }
            }
            if (!nimbusSet) {
                UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("An error happened when starting OpenSimMPLS");
        }
        splash = new JSplash();
        SwingUtilities.invokeLater(() -> {
            splash.setVisible(true);
        });
        splash.setMessage(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("Loading_icons..."));
        imagesBroker = TImageBroker.getInstance();
        splash.setMessage(ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("openSimMPLS.generandoInterfaz"));
        simulator = new JOpenSimMPLS(imagesBroker);
        Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        simulator.setBounds(0, 0, tamPantalla.width, tamPantalla.height);
        simulator.setVisible(true);
        splash.dispose();
    }

    // Variables declaration - do not modify
    private static TImageBroker imagesBroker;
    private static JSplash splash;
    private static JOpenSimMPLS simulator;
}
