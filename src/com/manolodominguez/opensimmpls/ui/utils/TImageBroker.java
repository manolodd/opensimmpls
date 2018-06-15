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
 * Esta clase se encarga de cargar en memoria todas las im�genes que se usar�n
 * en la aplicaci�n y posteriormente devolver referencias a las mismas, con lo
 * que la carga de elementos gr�ficos se realiza mucho m�s r�pido y no hace
 * falta cargar m�s de una vez una misma imagen.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TImageBroker {

    /**
     * Este m�todo es el constructor de la clase; crea una nueva instancia de
     * TDispensadorDeImagenes.
     *
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

    // Singleton
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
     * Obtiene una de las im�genes del dispensador de im�genes como un onjeto
     * Image.
     *
     * @param imageID Constante que identifica la imagen que se desea obtener.
     * Es una de las constantes definidas en la clase.
     * @return Un objeto Image con la imagen que se ha solicitado.
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
     * Obtiene una de las im�genes del dispensador de im�genes como un onjeto
     * ImageIcon.
     *
     * @param imageID Constante que identifica la imagen que se desea obtener.
     * Es una de las constantes definidas en la clase.
     * @return Un objeto ImageIcon con la imagen que se ha solicitado.
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
