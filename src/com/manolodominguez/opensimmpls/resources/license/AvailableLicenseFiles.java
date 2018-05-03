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
package com.manolodominguez.opensimmpls.resources.license;

/**
 * This enum is used to access license files from a centralilzed point. This
 * easies accessing from classes.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public enum AvailableLicenseFiles {
    LICENSE_FILE("/com/manolodominguez/opensimmpls/resources/license/license_text.txt");

    private final String filePath;

    /**
     * This is the constructor of the enum. It will set the default value of
     * each enum item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param filePath the path of the enum's item.
     * @since 2.0
     */
    private AvailableLicenseFiles(String filePath) {
        this.filePath = filePath;
    }

    /**
     * This method gets the path corresponding to the enum's item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the path corresponding to the enum's item.
     * @since 2.0
     */
    public String getPath() {
        return this.filePath;
    }
}
