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
package com.manolodominguez.opensimmpls.ui.utils;

import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.filechooser.FileFilter;

/**
 * This class implements a filter to be used togueteher with an open/save dialog
 * in order to see only files that match this filter. This filter is for
 * OpenSimMPLS files in OSM format.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class JOSMFilter extends FileFilter {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of JOSMFilter.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public JOSMFilter() {
        this.translations = ResourceBundle.getBundle(AvailableBundles.OSMFILTER.getPath());
    }

    /**
     * This method accepts a file that has to be analyzed to know if a given
     * open/save dialog should show it or not.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param file The file sent by a open/save dialog.
     * @return TRUE, if the file should be shown in the dialog. Otherwise,
     * FALSE.
     * @since 2.0
     */
    @Override
    public boolean accept(File file) {
        if (!file.isDirectory()) {
            String extension = this.getExtension(file);
            if (extension != null) {
                return extension.equals(JOSMFilter.OSM_EXTENSION);
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * This method gets the extension of the file specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param file The file whose extension is going to be returned.
     * @return the extension of the file specified as an argument. Null, if the
     * file des not have extension.
     * @since 2.0
     */
    private String getExtension(File file) {
        String extension = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            extension = s.substring(i + 1).toLowerCase();
        }
        return extension;
    }

    /**
     * This method gets a descriptions for files that match this filter.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return A description of files that mach this filter, to be shwon in
     * open/save dialogs.
     * @since 2.0
     */
    @Override
    public String getDescription() {
        return this.translations.getString("JSelectorFicheros.DescripcionOSM");
    }

    private ResourceBundle translations;

    public static final String OSM_EXTENSION = "osm";
}
