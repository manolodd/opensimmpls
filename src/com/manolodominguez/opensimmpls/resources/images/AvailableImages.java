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
 * limitations under the license.
 */
package com.manolodominguez.opensimmpls.resources.images;

/**
 * This is an enum implementation to identify all available images that can be
 * used in OpenSimMPLS.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public enum AvailableImages {
    OPEN_MENU_COLOR("abrir_menu.png"),
    WARNING("advertencia.png"),
    WIZARD("asistente.png"),
    START_SIMULATION_COLOR("boton_comenzar.png"),
    GENERATE_SIMULATION_COLOR("boton_generar.png"),
    STOP_SIMULATION_COLOR("boton_parar.png"),
    PAUSE_SIMULATION_COLOR("boton_pausa.png"),
    CLOSE_MENU_COLOR("cerrar_menu.png"),
    TRAFFIC_GENERATOR("emisor.png"),
    TRAFFIC_GENERATOR_MENU_COLOR("emisor_menu.png"),
    TRAFFIC_GENERATOR_MOVING("emisor_moviendose.png"),
    LINK("enlace.png"),
    LINK_MENU_COLOR("enlace_menu.png"),
    ERROR("error.png"),
    SAVE_AS_MENU_COLOR("guardar_como_menu.png"),
    SAVE_MENU_COLOR("guardar_menu.png"),
    SCENARIO_WINDOW_ICON_MENU("icono_ventana_interna_menu.png"),
    LER("ler.png"),
    LER_MENU_COLOR("ler_menu.png"),
    LER_MOVING("ler_moviendose.png"),
    ACTIVE_LER("lera.png"),
    ACTIVE_LER_MENU_COLOR("lera_menu.png"),
    ACTIVE_LER_MOVING("lera_moviendose.png"),
    LICENSE_MENU_COLOR("licencia_menu.png"),
    LSR("lsr.png"),
    LSR_MENU_COLOR("lsr_menu.png"),
    LSR_MOVING("lsr_moviendose.png"),
    ACTIVE_LSR("lsra.png"),
    ACTIVE_LSR_MENU_COLOR("lsra_menu.png"),
    ACTIVE_LSR_MOVING("lsra_moviendose.png"),
    NEW_MENU_COLOR("nuevo_menu.png"),
    LICENSE_LOGO("osi_certified.png"),
    TRAFFIC_SINK("receptor.png"),
    TRAFFIC_SINK_MENU_COLOR("receptor_menu.png"),
    TRAFFIC_SINK_MOVING("receptor_moviendose.png"),
    EXIT_MENU_COLOR("salir_menu.png"),
    SPLASH_ABOUT("splash.png"),
    SPLASH_LOADING("splash_inicio.png"),
    ABOUT_MENU_COLOR("splash_menu.png"),
    TUTORIAL_MENU_COLOR("tutorial_menu.png"),
    IMAGE_NOT_FOUND("imagen_no_encontrada.png"),
    TRAFFIC_GENERATOR_MENU_HIGHLIGHTED("emisor_menu_brillo.png"),
    TRAFFIC_SINK_MENU_HIGHLIGHTED("receptor_menu_brillo.png"),
    LER_MENU_HIGHLIGHTED("ler_menu_brillo.png"),
    LSR_MENU_HIGHLIGHTED("lsr_menu_brillo.png"),
    ACTIVE_LER_MENU_HIGHLIGHTED("lera_menu_brillo.png"),
    ACTIVE_LSR_MENU_HIGHLIGHTED("lsra_menu_brillo.png"),
    LINK_MENU_HIGHLIGHTED("enlace_menu_brillo.png"),
    START_HIGHLIGHTED("boton_comenzar_brillo.png"),
    GENERATE_HIGHLIGHTED("boton_generar_brillo.png"),
    STOP_HIGHLIGHTED("boton_parar_brillo.png"),
    PAUSE_HIGHLIGHTED("boton_pausa_brillo.png"),
    ICONS_VIEW_COLOR("vista_iconos_menu.png"),
    CASCADE_VIEW_COLOR("vista_cascada_menu.png"),
    HORIZONTAL_MOSAIC_COLOR("vista_horizontal_menu.png"),
    VERTICAL_MOSAIC_COLOR("vista_vertical_menu.png"),
    PDU_LDP("pdu_ldp.png"),
    PDU_GOS("pdu_gos.png"),
    PDU_IPV4("pdu_ipv4.png"),
    PDU_MPLS("pdu_mpls.png"),
    TRAFFIC_GENERATOR_CONGESTED3("emisor_congestionado.png"),
    TRAFFIC_SINK_CONGESTED3("receptor_congestionado.png"),
    LER_CONGESTED3("ler_congestionado.png"),
    ACTIVE_LER_CONGESTED3("lera_congestionado.png"),
    LSR_CONGESTED3("lsr_congestionado.png"),
    ACTIVE_LSR_CONGESTED3("lsra_congestionado.png"),
    PDU_IPV4_GOS("pdu_ipv4_gos.png"),
    PDU_MPLS_GOS("pdu_mpls_gos.png"),
    SIMULATION("simulacion.png"),
    OPTIONS("opciones.png"),
    DESIGN("disenio.png"),
    ANALYSIS("analisis.png"),
    ACCEPT("aceptar.png"),
    CANCEL("cancelar.png"),
    ADVANCED("avanzada.png"),
    QUESTION_MARK("interrogacion.png"),
    PDU_IPV4_GOS_DISCARDED("pdu_ipv4_gos_cae.png"),
    PDU_MPLS_GOS_DISCARDED("pdu_mpls_gos_cae.png"),
    PDU_LDP_DISCARDED("pdu_ldp_cae.png"),
    PDU_GOS_DISCARDED("pdu_gos_cae.png"),
    PDU_IPV4_DISCARDED("pdu_ipv4_cae.png"),
    PDU_MPLS_DISCARDED("pdu_mpls_cae.png"),
    PACKET_GENERATED("paquete_generado.png"),
    PACKET_SENT("paquete_emitido.png"),
    PACKET_RECEIVED("paquete_recibido.png"),
    PACKET_SWITCHED("paquete_conmutado.png"),
    TRAFFIC_GENERATOR_CONGESTED1("emisor_congestionado_20.png"),
    TRAFFIC_SINK_CONGESTED1("receptor_congestionado_20.png"),
    LER_CONGESTED1("ler_congestionado_20.png"),
    ACTIVE_LER_CONGESTED1("lera_congestionado_20.png"),
    LSR_CONGESTED1("lsr_congestionado_20.png"),
    ACTIVE_LSR_CONGESTED1("lsra_congestionado_20.png"),
    TRAFFIC_GENERATOR_CONGESTED2("emisor_congestionado_60.png"),
    TRAFFIC_SINK_CONGESTED2("receptor_congestionado_60.png"),
    LER_CONGESTED2("ler_congestionado_60.png"),
    ACTIVE_LER_CONGESTED2("lera_congestionado_60.png"),
    LSR_CONGESTED2("lsr_congestionado_60.png"),
    ACTIVE_LSR_CONGESTED2("lsra_congestionado_60.png"),
    LINK_BROKEN("enlace_caido.png"),
    LINK_RECOVERED("enlace_recuperado.png"),
    ASK_MENU_COLOR("ask_menu.png"),
    CONTRIBUTE_MENU_COLOR("contribute_menu.png"),
    WORKING("reloj_arena.png"),
    OPENSIMMPLS_APP_ICON("opensimmpls_app_icon.png");
    
    private final String imageFileName;

    /**
     * This is the constructor of the enum. It creates a new enum item and
     * associates a filename to it.
     *
     * @param imageFilename the filename that corresponds to the new available
     * image created.
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    private AvailableImages(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    /**
     * This methods gets the complete file path to the image associated to the
     * enum item.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the complete file path to the image associated to the enum item.
     * @since 2.0
     */
    public String getPath() {
        return AvailableImages.IMAGES_PATH + this.imageFileName;
    }

    private static final String IMAGES_PATH = "/com/manolodominguez/opensimmpls/resources/images/";
}
