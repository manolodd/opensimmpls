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
import java.awt.Image;
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
    public TImageBroker() {
        this.imageIcons = new ImageIcon[NUMERO_DE_IMAGENES];
        this.translations = ResourceBundle.getBundle(AvailableBundles.IMAGE_BROKER.getPath());
        try {
            preloadImages();
        } catch (Exception e) {
            System.out.println(this.translations.getString("TDispensadorDeImagenes.error"));
        }
    }

    /**
     * Este m�todo carga en memoria todas las im�genes que se van a utilizar en
     * la aplicaci�n.
     *
     * @since 2.0
     */
    private void preloadImages() {
        imageIcons[OPEN] = new ImageIcon(getClass().getResource(IMAGES_PATH + "abrir.png"));
        imageIcons[OPEN_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "abrir_menu_gris.png"));
        imageIcons[OPEN_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "abrir_menu.png"));
        imageIcons[WARNING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "advertencia.png"));
        imageIcons[WIZARD] = new ImageIcon(getClass().getResource(IMAGES_PATH + "asistente.png"));
        imageIcons[START_SIMULATION_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_comenzar.png"));
        imageIcons[START_SIMULATION_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_comenzar_gris.png"));
        imageIcons[GENERATE_SIMULATION_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_generar.png"));
        imageIcons[GENERATE_SIMULATION_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_generar_gris.png"));
        imageIcons[STOP_SIMULATION_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_parar.png"));
        imageIcons[STOP_SIMULATION_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_parar_gris.png"));
        imageIcons[PAUSE_SIMULATION_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_pausa.png"));
        imageIcons[PAUSE_SIMULATION_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_pausa_gris.png"));
        imageIcons[CLOSE] = new ImageIcon(getClass().getResource(IMAGES_PATH + "cerrar.png"));
        imageIcons[CLOSE_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "cerrar_menu_gris.png"));
        imageIcons[CLOSE_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "cerrar_menu.png"));
        imageIcons[COMMENT] = new ImageIcon(getClass().getResource(IMAGES_PATH + "comentario.png"));
        imageIcons[COMMENT_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "comentario_menu_gris.png"));
        imageIcons[COMMENT_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "comentario_menu.png"));
        imageIcons[TRAFFIC_GENERATOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor.png"));
        imageIcons[TRAFFIC_GENERATOR_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor_menu_gris.png"));
        imageIcons[TRAFFIC_GENERATOR_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor_menu.png"));
        imageIcons[TRAFFIC_GENERATOR_MOVING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor_moviendose.png"));
        imageIcons[LINK] = new ImageIcon(getClass().getResource(IMAGES_PATH + "enlace.png"));
        imageIcons[LINK_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "enlace_menu_gris.png"));
        imageIcons[LINK_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "enlace_menu.png"));
        imageIcons[ERROR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "error.png"));
        imageIcons[SAVE] = new ImageIcon(getClass().getResource(IMAGES_PATH + "guardar.png"));
        imageIcons[SAVE_AS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "guardar_como.png"));
        imageIcons[SAVE_AS_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "guardar_como_menu_gris.png"));
        imageIcons[SAVE_AS_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "guardar_como_menu.png"));
        imageIcons[SAVE_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "guardar_menu_gris.png"));
        imageIcons[SAVE_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "guardar_menu.png"));
        imageIcons[SCENARIO_WINDOW_ICON] = new ImageIcon(getClass().getResource(IMAGES_PATH + "icono_ventana_interna.png"));
        imageIcons[SCENARIO_WINDOW_ICON_MENU] = new ImageIcon(getClass().getResource(IMAGES_PATH + "icono_ventana_interna_menu.png"));
        imageIcons[OSM_ICON] = new ImageIcon(getClass().getResource(IMAGES_PATH + "iconosimMPLS.png"));
        imageIcons[PRINT] = new ImageIcon(getClass().getResource(IMAGES_PATH + "imprimir.png"));
        imageIcons[PRINT_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "imprimir_menu_gris.png"));
        imageIcons[PRINT_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "imprimir_menu.png"));
        imageIcons[LER] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler.png"));
        imageIcons[LER_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler_menu_gris.png"));
        imageIcons[LER_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler_menu.png"));
        imageIcons[LER_MOVING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler_moviendose.png"));
        imageIcons[ACTIVE_LER] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera.png"));
        imageIcons[ACTIVE_LER_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera_menu_gris.png"));
        imageIcons[ACTIVE_LER_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera_menu.png"));
        imageIcons[ACTIVE_LER_MOVING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera_moviendose.png"));
        imageIcons[LICENSE] = new ImageIcon(getClass().getResource(IMAGES_PATH + "licencia.png"));
        imageIcons[LICENSE_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "licencia_menu_gris.png"));
        imageIcons[LICENSE_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "licencia_menu.png"));
        imageIcons[LSR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr.png"));
        imageIcons[LSR_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr_menu_gris.png"));
        imageIcons[LSR_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr_menu.png"));
        imageIcons[LSR_MOVING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr_moviendose.png"));
        imageIcons[ACTIVE_LSR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra.png"));
        imageIcons[ACTIVE_LSR_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra_menu_gris.png"));
        imageIcons[ACTIVE_LSR_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra_menu.png"));
        imageIcons[ACTIVE_LSR_MOVING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra_moviendose.png"));
        imageIcons[CLOUD] = new ImageIcon(getClass().getResource(IMAGES_PATH + "nube.png"));
        imageIcons[NEW] = new ImageIcon(getClass().getResource(IMAGES_PATH + "nuevo.png"));
        imageIcons[NEW_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "nuevo_menu_gris.png"));
        imageIcons[NEW_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "nuevo_menu.png"));
        imageIcons[LICENSE_LOGO] = new ImageIcon(getClass().getResource(IMAGES_PATH + "osi_certified.png"));
        imageIcons[TRAFFIC_SINK] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor.png"));
        imageIcons[TRAFFIC_SINK_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor_menu_gris.png"));
        imageIcons[TRAFFIC_SINK_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor_menu.png"));
        imageIcons[TRAFFIC_SINK_MOVING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor_moviendose.png"));
        imageIcons[EXIT] = new ImageIcon(getClass().getResource(IMAGES_PATH + "salir.png"));
        imageIcons[EXIT_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "salir_menu.png"));
        imageIcons[SPLASH_ABOUT] = new ImageIcon(getClass().getResource(IMAGES_PATH + "splash.png"));
        imageIcons[SPLASH] = new ImageIcon(getClass().getResource(IMAGES_PATH + "splash_inicio.png"));
        imageIcons[ABOUT_MENU_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "splash_menu_gris.png"));
        imageIcons[ABOUT_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "splash_menu.png"));
        imageIcons[TUTORIAL] = new ImageIcon(getClass().getResource(IMAGES_PATH + "tutorial.png"));
        imageIcons[TUTORIAL_MENU_GRIS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "tutorial_menu_gris.png"));
        imageIcons[TUTORIAL_MENU_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "tutorial_menu.png"));
        imageIcons[IMAGE_NOT_FOUND] = new ImageIcon(getClass().getResource(IMAGES_PATH + "imagen_no_encontrada.png"));
        imageIcons[TRAFFIC_GENERATOR_MENU_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor_menu_brillo.png"));
        imageIcons[TRAFFIC_SINK_MENU_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor_menu_brillo.png"));
        imageIcons[LER_MENU_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler_menu_brillo.png"));
        imageIcons[LSR_MENU_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr_menu_brillo.png"));
        imageIcons[ACTIVE_LER_MENU_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera_menu_brillo.png"));
        imageIcons[ACTIVE_LSR_MENU_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra_menu_brillo.png"));
        imageIcons[LINK_MENU_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "enlace_menu_brillo.png"));
        imageIcons[START_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_comenzar_brillo.png"));
        imageIcons[GENERATE_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_generar_brillo.png"));
        imageIcons[STOP_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_parar_brillo.png"));
        imageIcons[PAUSE_HIGHLIGHTED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "boton_pausa_brillo.png"));
        imageIcons[ICONS_VIEW_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "vista_iconos_menu.png"));
        imageIcons[CASCADE_VIEW_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "vista_cascada_menu.png"));
        imageIcons[HORIZONTAL_MOSAIC_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "vista_horizontal_menu.png"));
        imageIcons[VERTICAL_MOSAIC_COLOR] = new ImageIcon(getClass().getResource(IMAGES_PATH + "vista_vertical_menu.png"));
        imageIcons[PDU_LDP] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_ldp.png"));
        imageIcons[PDU_GOS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_gos.png"));
        imageIcons[PDU_IPV4] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_ipv4.png"));
        imageIcons[PDU_MPLS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_mpls.png"));
        imageIcons[TRAFFIC_GENERATOR_CONGESTED3] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor_congestionado.png"));
        imageIcons[TRAFFIC_SINK_CONGESTED3] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor_congestionado.png"));
        imageIcons[LER_CONGESTED3] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler_congestionado.png"));
        imageIcons[ACTIVE_LER_CONGESTED3] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera_congestionado.png"));
        imageIcons[LSR_CONGESTED3] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr_congestionado.png"));
        imageIcons[ACTIVE_LSR_CONGESTED3] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra_congestionado.png"));
        imageIcons[PDU_IPV4_GOS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_ipv4_gos.png"));
        imageIcons[PDU_MPLS_GOS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_mpls_gos.png"));
        imageIcons[SIMULATION] = new ImageIcon(getClass().getResource(IMAGES_PATH + "simulacion.png"));
        imageIcons[OPTIONS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "opciones.png"));
        imageIcons[DESIGN] = new ImageIcon(getClass().getResource(IMAGES_PATH + "disenio.png"));
        imageIcons[ANALYSIS] = new ImageIcon(getClass().getResource(IMAGES_PATH + "analisis.png"));
        imageIcons[EMAIL] = new ImageIcon(getClass().getResource(IMAGES_PATH + "sobre_email.png"));
        imageIcons[ACCEPT] = new ImageIcon(getClass().getResource(IMAGES_PATH + "aceptar.png"));
        imageIcons[CANCEL] = new ImageIcon(getClass().getResource(IMAGES_PATH + "cancelar.png"));
        imageIcons[ADVANCED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "avanzada.png"));
        imageIcons[BULLET_GRAY] = new ImageIcon(getClass().getResource(IMAGES_PATH + "topo_gris.png"));
        imageIcons[BULLET_GREEN] = new ImageIcon(getClass().getResource(IMAGES_PATH + "topo_verde.png"));
        imageIcons[QUESTION_MARK] = new ImageIcon(getClass().getResource(IMAGES_PATH + "interrogacion.png"));
        imageIcons[OSM_MIME] = new ImageIcon(getClass().getResource(IMAGES_PATH + "mime_osm.png"));
        imageIcons[PDU_IPV4_GOS_DISCARDED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_ipv4_gos_cae.png"));
        imageIcons[PDU_MPLS_GOS_DISCARDED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_mpls_gos_cae.png"));
        imageIcons[PDU_LDP_DISCARDED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_ldp_cae.png"));
        imageIcons[PDU_GOS_DISCARDED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_gos_cae.png"));
        imageIcons[PDU_IPV4_DISCARDED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_ipv4_cae.png"));
        imageIcons[PDU_MPLS_DISCARDED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "pdu_mpls_cae.png"));
        imageIcons[PACKET_GENERATED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "paquete_generado.png"));
        imageIcons[PACKET_SENT] = new ImageIcon(getClass().getResource(IMAGES_PATH + "paquete_emitido.png"));
        imageIcons[PACKET_RECEIVED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "paquete_recibido.png"));
        imageIcons[PACKET_SWITCHED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "paquete_conmutado.png"));
        imageIcons[TRAFFIC_GENERATOR_CONGESTED1] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor_congestionado_20.png"));
        imageIcons[TRAFFIC_SINK_CONGESTED1] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor_congestionado_20.png"));
        imageIcons[LER_CONGESTED1] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler_congestionado_20.png"));
        imageIcons[ACTIVE_LER_CONGESTED1] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera_congestionado_20.png"));
        imageIcons[LSR_CONGESTED1] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr_congestionado_20.png"));
        imageIcons[ACTIVE_LSR_CONGESTED1] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra_congestionado_20.png"));
        imageIcons[TRAFFIC_GENERATOR_CONGESTED2] = new ImageIcon(getClass().getResource(IMAGES_PATH + "emisor_congestionado_60.png"));
        imageIcons[TRAFFIC_SINK_CONGESTED2] = new ImageIcon(getClass().getResource(IMAGES_PATH + "receptor_congestionado_60.png"));
        imageIcons[LER_CONGESTED2] = new ImageIcon(getClass().getResource(IMAGES_PATH + "ler_congestionado_60.png"));
        imageIcons[ACTIVE_LER_CONGESTED2] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lera_congestionado_60.png"));
        imageIcons[LSR_CONGESTED2] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsr_congestionado_60.png"));
        imageIcons[ACTIVE_LSR_CONGESTED2] = new ImageIcon(getClass().getResource(IMAGES_PATH + "lsra_congestionado_60.png"));
        imageIcons[LINK_BROKEN] = new ImageIcon(getClass().getResource(IMAGES_PATH + "enlace_caido.png"));
        imageIcons[LINK_RECOVERED] = new ImageIcon(getClass().getResource(IMAGES_PATH + "enlace_recuperado.png"));
        imageIcons[WORKING] = new ImageIcon(getClass().getResource(IMAGES_PATH + "reloj_arena.png"));
        imageIcons[WEB_SCREENSHOT] = new ImageIcon(getClass().getResource(IMAGES_PATH + "capturaweb.png"));
    }

    /**
     * Obtiene una de las im�genes del dispensador de im�genes como un onjeto
     * Image.
     *
     * @param queImagen Constante que identifica la imagen que se desea obtener.
     * Es una de las constantes definidas en la clase.
     * @return Un objeto Image con la imagen que se ha solicitado.
     * @since 2.0
     */
    public Image obtenerImagen(int queImagen) {
        ImageIcon imagenBuscada = imageIcons[queImagen];
        if (imagenBuscada == null) {
            imagenBuscada = imageIcons[IMAGE_NOT_FOUND];
        }
        return imagenBuscada.getImage();
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
    public ImageIcon getImageIcon(int imageID) {
        ImageIcon desiredImage = this.imageIcons[imageID];
        if (desiredImage == null) {
            desiredImage = this.imageIcons[IMAGE_NOT_FOUND];
        }
        return desiredImage;
    }

    private ImageIcon[] imageIcons;
    private ResourceBundle translations;

    private static final String IMAGES_PATH = "/com/manolodominguez/opensimmpls/resources/images/";

    public static final int OPEN = 0;
    public static final int OPEN_MENU_GRAY = 1;
    public static final int OPEN_MENU_COLOR = 2;
    public static final int WARNING = 3;
    public static final int WIZARD = 4;
    public static final int START_SIMULATION_COLOR = 5;
    public static final int START_SIMULATION_GRAY = 6;
    public static final int GENERATE_SIMULATION_COLOR = 7;
    public static final int GENERATE_SIMULATION_GRAY = 8;
    public static final int STOP_SIMULATION_COLOR = 9;
    public static final int STOP_SIMULATION_GRAY = 10;
    public static final int PAUSE_SIMULATION_COLOR = 11;
    public static final int PAUSE_SIMULATION_GRAY = 12;
    public static final int CLOSE = 13;
    public static final int CLOSE_MENU_GRAY = 14;
    public static final int CLOSE_MENU_COLOR = 15;
    public static final int COMMENT = 16;
    public static final int COMMENT_MENU_GRAY = 17;
    public static final int COMMENT_MENU_COLOR = 18;
    public static final int TRAFFIC_GENERATOR = 19;
    public static final int TRAFFIC_GENERATOR_MENU_GRAY = 20;
    public static final int TRAFFIC_GENERATOR_MENU_COLOR = 21;
    public static final int TRAFFIC_GENERATOR_MOVING = 22;
    public static final int LINK = 23;
    public static final int LINK_MENU_GRAY = 24;
    public static final int LINK_MENU_COLOR = 25;
    public static final int ERROR = 26;
    public static final int SAVE = 27;
    public static final int SAVE_AS = 28;
    public static final int SAVE_AS_MENU_GRAY = 29;
    public static final int SAVE_AS_MENU_COLOR = 30;
    public static final int SAVE_MENU_GRAY = 31;
    public static final int SAVE_MENU_COLOR = 32;
    public static final int SCENARIO_WINDOW_ICON = 33;
    public static final int SCENARIO_WINDOW_ICON_MENU = 34;
    public static final int OSM_ICON = 35;
    public static final int PRINT = 36;
    public static final int PRINT_MENU_GRAY = 37;
    public static final int PRINT_MENU_COLOR = 38;
    public static final int LER = 39;
    public static final int LER_MENU_GRAY = 40;
    public static final int LER_MENU_COLOR = 41;
    public static final int LER_MOVING = 42;
    public static final int ACTIVE_LER = 43;
    public static final int ACTIVE_LER_MENU_GRAY = 44;
    public static final int ACTIVE_LER_MENU_COLOR = 45;
    public static final int ACTIVE_LER_MOVING = 46;
    public static final int LICENSE = 47;
    public static final int LICENSE_MENU_GRAY = 48;
    public static final int LICENSE_MENU_COLOR = 49;
    public static final int LSR = 50;
    public static final int LSR_MENU_GRAY = 51;
    public static final int LSR_MENU_COLOR = 52;
    public static final int LSR_MOVING = 53;
    public static final int ACTIVE_LSR = 54;
    public static final int ACTIVE_LSR_MENU_GRAY = 55;
    public static final int ACTIVE_LSR_MENU_COLOR = 56;
    public static final int ACTIVE_LSR_MOVING = 57;
    public static final int CLOUD = 58;
    public static final int NEW = 59;
    public static final int NEW_MENU_GRAY = 60;
    public static final int NEW_MENU_COLOR = 61;
    public static final int LICENSE_LOGO = 62;
    public static final int TRAFFIC_SINK = 63;
    public static final int TRAFFIC_SINK_MENU_GRAY = 64;
    public static final int TRAFFIC_SINK_MENU_COLOR = 65;
    public static final int TRAFFIC_SINK_MOVING = 66;
    public static final int EXIT = 67;
    public static final int EXIT_MENU_COLOR = 68;
    public static final int SPLASH_ABOUT = 69;
    public static final int SPLASH = 70;
    public static final int ABOUT_MENU_GRAY = 71;
    public static final int ABOUT_MENU_COLOR = 72;
    public static final int TUTORIAL = 73;
    public static final int TUTORIAL_MENU_GRIS = 74;
    public static final int TUTORIAL_MENU_COLOR = 75;
    private static final int IMAGE_NOT_FOUND = 76;
    public static final int TRAFFIC_GENERATOR_MENU_HIGHLIGHTED = 77;
    public static final int TRAFFIC_SINK_MENU_HIGHLIGHTED = 78;
    public static final int LER_MENU_HIGHLIGHTED = 79;
    public static final int LSR_MENU_HIGHLIGHTED = 80;
    public static final int ACTIVE_LER_MENU_HIGHLIGHTED = 81;
    public static final int ACTIVE_LSR_MENU_HIGHLIGHTED = 82;
    public static final int LINK_MENU_HIGHLIGHTED = 83;
    public static final int START_HIGHLIGHTED = 84;
    public static final int GENERATE_HIGHLIGHTED = 85;
    public static final int STOP_HIGHLIGHTED = 86;
    public static final int PAUSE_HIGHLIGHTED = 87;
    public static final int ICONS_VIEW_COLOR = 88;
    public static final int CASCADE_VIEW_COLOR = 89;
    public static final int HORIZONTAL_MOSAIC_COLOR = 90;
    public static final int VERTICAL_MOSAIC_COLOR = 91;
    public static final int PDU_LDP = 92;
    public static final int PDU_GOS = 93;
    public static final int PDU_IPV4 = 94;
    public static final int PDU_MPLS = 95;
    public static final int TRAFFIC_GENERATOR_CONGESTED3 = 96;
    public static final int TRAFFIC_SINK_CONGESTED3 = 97;
    public static final int LER_CONGESTED3 = 98;
    public static final int ACTIVE_LER_CONGESTED3 = 99;
    public static final int LSR_CONGESTED3 = 100;
    public static final int ACTIVE_LSR_CONGESTED3 = 101;
    public static final int PDU_IPV4_GOS = 102;
    public static final int PDU_MPLS_GOS = 103;
    public static final int SIMULATION = 104;
    public static final int OPTIONS = 105;
    public static final int DESIGN = 106;
    public static final int ANALYSIS = 107;
    public static final int EMAIL = 108;
    public static final int ACCEPT = 109;
    public static final int CANCEL = 110;
    public static final int ADVANCED = 111;
    public static final int BULLET_GRAY = 112;
    public static final int BULLET_GREEN = 113;
    public static final int QUESTION_MARK = 114;
    public static final int OSM_MIME = 115;
    public static final int PDU_LDP_DISCARDED = 116;
    public static final int PDU_GOS_DISCARDED = 117;
    public static final int PDU_IPV4_DISCARDED = 118;
    public static final int PDU_IPV4_GOS_DISCARDED = 119;
    public static final int PDU_MPLS_GOS_DISCARDED = 120;
    public static final int PDU_MPLS_DISCARDED = 121;
    public static final int PACKET_GENERATED = 122;
    public static final int PACKET_SENT = 123;
    public static final int PACKET_RECEIVED = 124;
    public static final int PACKET_SWITCHED = 125;
    public static final int TRAFFIC_GENERATOR_CONGESTED1 = 126;
    public static final int TRAFFIC_SINK_CONGESTED1 = 127;
    public static final int LER_CONGESTED1 = 128;
    public static final int ACTIVE_LER_CONGESTED1 = 129;
    public static final int LSR_CONGESTED1 = 130;
    public static final int ACTIVE_LSR_CONGESTED1 = 131;
    public static final int TRAFFIC_GENERATOR_CONGESTED2 = 132;
    public static final int TRAFFIC_SINK_CONGESTED2 = 133;
    public static final int LER_CONGESTED2 = 134;
    public static final int ACTIVE_LER_CONGESTED2 = 135;
    public static final int LSR_CONGESTED2 = 136;
    public static final int ACTIVE_LSR_CONGESTED2 = 137;
    public static final int LINK_BROKEN = 138;
    public static final int LINK_RECOVERED = 139;
    public static final int WORKING = 140;
    public static final int WEB_SCREENSHOT = 141;

    private static final int NUMERO_DE_IMAGENES = 142;    
}
