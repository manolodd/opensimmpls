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
package com.manolodominguez.opensimmpls.resources.images;

/**
 *
 * @author manolodd
 */
// FIX: Change TImageBroker to use this enum items.
public enum AvailableImages {
    DUMMY("pathdeprueba");

    private final String imagePath;

    private AvailableImages(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPath() {
        return this.imagePath;
    }
}
