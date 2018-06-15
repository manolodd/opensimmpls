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

import com.manolodominguez.opensimmpls.resources.images.AvailableImages;
import com.manolodominguez.opensimmpls.resources.translations.AvailableBundles;
import java.awt.Image;
import java.util.EnumMap;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

/**
 * This class implements a image broker that preloads all images needed by
 * OpenSimMPLS to give each GUI component the required image faster.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TImageBroker {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TImageBroker.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private TImageBroker() {
        this.imageIcons = new EnumMap<>(AvailableImages.class);
        this.translations = ResourceBundle.getBundle(AvailableBundles.IMAGE_BROKER.getPath());
        try {
            for (AvailableImages availableImage : AvailableImages.values()) {
                this.imageIcons.put(availableImage, new ImageIcon(getClass().getResource(availableImage.getPath())));
            }
        } catch (Exception e) {
            System.out.println(this.translations.getString("TDispensadorDeImagenes.error"));
        }
    }

    /**
     * This method returns a instance of this class. As this class implements
     * the singleton pattern, this checks whether a new instance has to be
     * created or the existing one has to be returned.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return An instance of TImageBroker
     * @since 2.0
     */
    public static TImageBroker getInstance() {
        TImageBroker localInstance = TImageBroker.instance;
        if (localInstance == null) {
            synchronized (TImageBroker.class) {
                localInstance = TImageBroker.instance;
                if (localInstance == null) {
                    TImageBroker.instance = localInstance = new TImageBroker();
                }
            }
        }
        return localInstance;
    }

    /**
     * This method request a given image from the ImageBroker.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param imageID The image ID the is requested.
     * @return The requested image or a default one if the requested image is
     * not found.
     * @since 2.0
     */
    public Image getImage(AvailableImages imageID) {
        ImageIcon imageIcon = this.imageIcons.get(imageID);
        if (imageIcon == null) {
            imageIcon = this.imageIcons.get(AvailableImages.IMAGE_NOT_FOUND);
        }
        return imageIcon.getImage();
    }

    /**
     * This method request a given image icon from the ImageBroker.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param imageID The image ID the is requested.
     * @return The requested image icon or a default one if the requested image
     * icon is not found.
     * @since 2.0
     */
    public ImageIcon getImageIcon(AvailableImages imageID) {
        ImageIcon imageIcon = this.imageIcons.get(imageID);
        if (imageIcon == null) {
            imageIcon = this.imageIcons.get(AvailableImages.IMAGE_NOT_FOUND);
        }
        return imageIcon;
    }

    private ResourceBundle translations;
    private static volatile TImageBroker instance;
    private final EnumMap<AvailableImages, ImageIcon> imageIcons;
}
