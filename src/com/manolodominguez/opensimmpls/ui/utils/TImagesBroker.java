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

import java.awt.*;
import javax.swing.*;

/** Esta clase se encarga de cargar en memoria todas las im�genes que se usar�n en
 * la aplicaci�n y posteriormente devolver referencias a las mismas, con lo que la
 * carga de elementos gr�ficos se realiza mucho m�s r�pido y no hace falta cargar
 * m�s de una vez una misma imagen.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TImagesBroker {

    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un s�mbolo de "abrir".
     * @since 2.0
     */    
    public static final int ABRIR =                          0;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un s�mbolo de "abrir" peque�o y gris.
     * @since 2.0
     */    
    public static final int ABRIR_MENU_GRIS =                1;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un s�mbolo de "abrir" peque�o.
     * @since 2.0
     */    
    public static final int ABRIR_MENU =                     2;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un s�mbolo de "advertencia".
     * @since 2.0
     */    
    public static final int ADVERTENCIA =                    3;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un mago.
     * @since 2.0
     */    
    public static final int ASISTENTE =                      4;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * boton de "play".
     * @since 2.0
     */    
    public static final int BOTON_COMENZAR =                 5;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * boton de "play" en gris.
     * @since 2.0
     */    
    public static final int BOTON_COMENZAR_GRIS =            6;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * engranaje.
     * @since 2.0
     */    
    public static final int BOTON_GENERAR =                  7;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * engranaje en gris.
     * @since 2.0
     */    
    public static final int BOTON_GENERAR_GRIS =             8;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * boton de "stop".
     * @since 2.0
     */    
    public static final int BOTON_PARAR =                    9;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * boton de "stop" en gris.
     * @since 2.0
     */    
    public static final int BOTON_PARAR_GRIS =              10;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * boton de "pausa".
     * @since 2.0
     */    
    public static final int BOTON_PAUSA =                   11;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * boton de "pausa" en gris.
     * @since 2.0
     */    
    public static final int BOTON_PAUSA_GRIS =              12;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "cerrar".
     * @since 2.0
     */    
    public static final int CERRAR =                        13;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "cerrar" peque�a y gris.
     * @since 2.0
     */    
    public static final int CERRAR_MENU_GRIS=               14;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "cerrar" peque�a.
     * @since 2.0
     */    
    public static final int CERRAR_MENU =                   15;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de un mensaje con un globo planetario.
     * @since 2.0
     */    
    public static final int COMENTARIO =                    16;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de un mensaje con un globo planetario peque�o y gris.
     * @since 2.0
     */    
    public static final int COMENTARIO_MENU_GRIS =          17;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de un mensaje con un globo planetario peque�o.
     * @since 2.0
     */    
    public static final int COMENTARIO_MENU =               18;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * nodo emisor.
     * @since 2.0
     */    
    public static final int EMISOR =                        19;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * nodo emisor peque�o y gris.
     * @since 2.0
     */    
    public static final int EMISOR_MENU_GRIS =              20;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * nodo emisor peque�o.
     * @since 2.0
     */    
    public static final int EMISOR_MENU =                   21;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * nodo emisor cuando se est� arrastrando.
     * @since 2.0
     */    
    public static final int EMISOR_MOVIENDOSE =             22;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * enlace.
     * @since 2.0
     */    
    public static final int ENLACE =                        23;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * enlace peque�o y gris.
     * @since 2.0
     */    
    public static final int ENLACE_MENU_GRIS =              24;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * enlace peque�o.
     * @since 2.0
     */    
    public static final int ENLACE_MENU =                   25;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen que indica error (direcci�n prohibida).
     * @since 2.0
     */    
    public static final int ERROR =                         26;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "guardar".
     * @since 2.0
     */    
    public static final int GUARDAR =                       27;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "guardar" con nombre.
     * @since 2.0
     */    
    public static final int GUARDAR_COMO =                  28;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "guardar" con nombre, peque�o y gris.
     * @since 2.0
     */    
    public static final int GUARDAR_COMO_MENU_GRIS =        29;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "guardar" con nombre, peque�o.
     * @since 2.0
     */    
    public static final int GUARDAR_COMO_MENU =             30;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "guardar" peque�o y gris.
     * @since 2.0
     */    
    public static final int GUARDAR_MENU_GRIS =             31;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen de "guardar" peque�o.
     * @since 2.0
     */    
    public static final int GUARDAR_MENU =                  32;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una rede en miniatura.
     * @since 2.0
     */    
    public static final int ICONO_VENTANA_INTERNA =         33;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una rede en miniatura, peque�a.
     * @since 2.0
     */    
    public static final int ICONO_VENTANA_INTERNA_MENU =    34;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa el
     * logo de Open SimMPLS.
     * @since 2.0
     */    
    public static final int ICONO_SIMMPLS =                 35;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una impresora.
     * @since 2.0
     */    
    public static final int IMPRIMIR =                      36;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una impresora peque�a y gris.
     * @since 2.0
     */    
    public static final int IMPRIMIR_MENU_GRIS =            37;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una impresora peque�a.
     * @since 2.0
     */    
    public static final int IMPRIMIR_MENU =                 38;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router (LER).
     * @since 2.0
     */    
    public static final int LER =                           39;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router (LER) peque�o y gris.
     * @since 2.0
     */    
    public static final int LER_MENU_GRIS =                 40;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router (LER) peque�o.
     * @since 2.0
     */    
    public static final int LER_MENU =                      41;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router (LER) cuando est� siendo arrastrado.
     * @since 2.0
     */    
    public static final int LER_MOVIENDOSE =                42;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router Activo (LERA).
     * @since 2.0
     */    
    public static final int LERA =                          43;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router Activo (LERA), peque�o y gris.
     * @since 2.0
     */    
    public static final int LERA_MENU_GRIS =                44;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router Activo (LERA), peque�o.
     * @since 2.0
     */    
    public static final int LERA_MENU =                     45;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router Activo (LERA), cuando est� siendo arrastrado.
     * @since 2.0
     */    
    public static final int LERA_MOVIENDOSE =               46;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * documento escrito.
     * @since 2.0
     */    
    public static final int LICENCIA =                      47;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * documento escrito, peque�o y gris.
     * @since 2.0
     */    
    public static final int LICENCIA_MENU_GRIS =            48;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa un
     * documento escrito peque�o.
     * @since 2.0
     */    
    public static final int LICENCIA_MENU =                 49;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router (LSR).
     * @since 2.0
     */    
    public static final int LSR =                           50;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router (LSR), peque�o y gris.
     * @since 2.0
     */    
    public static final int LSR_MENU_GRIS =                 51;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router (LSR), peque�o.
     * @since 2.0
     */    
    public static final int LSR_MENU =                      52;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router (LSR) cuando est� siendo arrastrado.
     * @since 2.0
     */    
    public static final int LSR_MOVIENDOSE =                53;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router Activo (LSRA).
     * @since 2.0
     */    
    public static final int LSRA =                          54;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router Activo (LSRA), peque�o y gris.
     * @since 2.0
     */    
    public static final int LSRA_MENU_GRIS =                55;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router Activo (LSRA), peque�o.
     * @since 2.0
     */    
    public static final int LSRA_MENU =                     56;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router Activo (LSRA), cuando est� siendo arrastrado.
     * @since 2.0
     */    
    public static final int LSRA_MOVIENDOSE =               57;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una nube.
     * @since 2.0
     */    
    public static final int NUBE =                          58;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un documento en blanco.
     * @since 2.0
     */    
    public static final int NUEVO =                         59;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un documento en blanco, peque�o y gris
     * @since 2.0
     */    
    public static final int NUEVO_MENU_GRIS =               60;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un documento en blanco, peque�o.
     * @since 2.0
     */    
    public static final int NUEVO_MENU =                    61;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * el logo de la certificaci�n OSI.
     * @since 2.0
     */    
    public static final int OSI_CERTIFIED =                 62;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo receptor.
     * @since 2.0
     */    
    public static final int RECEPTOR =                      63;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo receptor, peque�o y gris.
     * @since 2.0
     */    
    public static final int RECEPTOR_MENU_GRIS =            64;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo receptor, peque�o.
     * @since 2.0
     */    
    public static final int RECEPTOR_MENU =                 65;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo receptor, cuando est� siendo arrastrado.
     * @since 2.0
     */    
    public static final int RECEPTOR_MOVIENDOSE =           66;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un bot�n como el de apagar los aparatos el�ctricos.
     * @since 2.0
     */    
    public static final int SALIR =                         67;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un bot�n como el de apagar los aparatos el�ctricos, pqeue�o.
     * @since 2.0
     */    
    public static final int SALIR_MENU =                    68;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * la imagen inicial del programa.
     * @since 2.0
     */    
    public static final int SPLASH =                        69;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * la imagen inicial del programa, con espacio para texto.
     * @since 2.0
     */    
    public static final int SPLASH_INICIO =                 70;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * la imagen inicial del programa, peque�o y gris.
     * @since 2.0
     */    
    public static final int SPLASH_MENU_GRIS =              71;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * la imagen inicial del programa, peque�o.
     * @since 2.0
     */    
    public static final int SPLASH_MENU =                   72;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * la imagen de una manual.
     * @since 2.0
     */    
    public static final int TUTORIAL =                      73;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * la imagen de una manual, peque�o y gris.
     * @since 2.0
     */    
    public static final int TUTORIAL_MENU_GRIS =            74;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * la imagen de una manual, peque�o.
     * @since 2.0
     */    
    public static final int TUTORIAL_MENU =                 75;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una aspa indicando que la imagen no existe. Es un atributo privado.
     * @since 2.0
     */    
    private static final int IMAGEN_NO_ENCONTRADA =         76;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo emisor, resaltado.
     * @since 2.0
     */    
    public static final int EMISOR_MENU_BRILLO =            77;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo receptor, resaltado.
     * @since 2.0
     */    
    public static final int RECEPTOR_MENU_BRILLO =          78;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router (LER), resaltado.
     * @since 2.0
     */    
    public static final int LER_MENU_BRILLO =               79;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router (LSR), resaltado.
     * @since 2.0
     */    
    public static final int LSR_MENU_BRILLO =               80;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router Activo (LERA), resaltado.
     * @since 2.0
     */    
    public static final int LERA_MENU_BRILLO =              81;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un LAbel Switch Router Activo (LSRA), resaltado.
     * @since 2.0
     */    
    public static final int LSRA_MENU_BRILLO =              82;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un enlace, resaltado.
     * @since 2.0
     */    
    public static final int ENLACE_MENU_BRILLO =            83;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un bot�n del "Play", resaltado.
     * @since 2.0
     */    
    public static final int BOTON_COMENZAR_BRILLO =         84;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un engranaje, resaltado.
     * @since 2.0
     */    
    public static final int BOTON_GENERAR_BRILLO =          85;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un bot�n del "Stop", resaltado.
     * @since 2.0
     */    
    public static final int BOTON_PARAR_BRILLO =            86;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un bot�n del "Pausa", resaltado.
     * @since 2.0
     */    
    public static final int BOTON_PAUSA_BRILLO =            87;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen indicando disposici�n en forma de iconos.
     * @since 2.0
     */    
    public static final int VISTA_ICONOS =                  88;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen indicando disposici�n de ventanas en cascada.
     * @since 2.0
     */    
    public static final int VISTA_CASCADA =                 89;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen indicando disposici�n de ventanas en mosaico horizontal.
     * @since 2.0
     */    
    public static final int VISTA_HORIZONTAL =              90;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una imagen indicando disposici�n de ventanas en mosaico vertical.
     * @since 2.0
     */    
    public static final int VISTA_VERTICAL =                91;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una topo indicando una PDU de tipo TLDP.
     * @since 2.0
     */    
    public static final int PDU_LDP =                       92;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una topo indicando una PDU de tipo GoS.
     * @since 2.0
     */    
    public static final int PDU_GOS =                       93;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una topo indicando una PDU de tipo IPV4.
     * @since 2.0
     */    
    public static final int PDU_IPV4 =                      94;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una topo indicando una PDU de tipo MPLS.
     * @since 2.0
     */    
    public static final int PDU_MPLS =                      95;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo emisor congestionado.
     * @since 2.0
     */    
    public static final int EMISOR_CONGESTIONADO =          96;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un nodo receptor congestionado.
     * @since 2.0
     */    
    public static final int RECEPTOR_CONGESTIONADO =        97;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router (LER) congestionado.
     * @since 2.0
     */    
    public static final int LER_CONGESTIONADO =             98;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Edge Router Activo (LERA) congestionado.
     * @since 2.0
     */    
    public static final int LERA_CONGESTIONADO =            99;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router (LSR) congestionado.
     * @since 2.0
     */    
    public static final int LSR_CONGESTIONADO =            100;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un Label Switch Router Activo (LSRA) congestionado.
     * @since 2.0
     */    
    public static final int LSRA_CONGESTIONADO =           101;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un topo indicando una PDU de tipo IPV4 marcada con GoS.
     * @since 2.0
     */    
    public static final int PDU_IPV4_GOS =                 102;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un topo indicando una PDU de tipo MPLS marcada con GoS.
     * @since 2.0
     */    
    public static final int PDU_MPLS_GOS =                 103;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un reloj.
     * @since 2.0
     */    
    public static final int SIMULACION =                   104;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un PC.
     * @since 2.0
     */    
    public static final int OPCIONES =                     105;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una paleta de pintor.
     * @since 2.0
     */    
    public static final int DISENIO =                      106;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una gr�fica de queso.
     * @since 2.0
     */    
    public static final int ANALISIS =                     107;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un sobre a enviar por email.
     * @since 2.0
     */    
    public static final int SOBRE_EMAIL =                  108;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una varca de validaci�n, verde.
     * @since 2.0
     */    
    public static final int ACEPTAR =                      109;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un aspa roja de denegaci�n.
     * @since 2.0
     */    
    public static final int CANCELAR =                     110;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * una caja de herramientas.
     * @since 2.0
     */    
    public static final int AVANZADA =                     111;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un topo gris.
     * @since 2.0
     */    
    public static final int TOPO_GRIS =                    112;
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * un topo verde.
     * @since 2.0
     */    
    public static final int TOPO_VERDE =                   113;
    
    /** Esta constante indica al dispensador que le devuelva la imagen que representa
     * uan interrogaci�n circunscrita en cun c�rculo amarillo.
     * @since 2.0
     */    
    public static final int INTERROGACION =                114;

    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * el icono MIME para los ficheros *.OSM de escenarios de Open SimMPLS 1.0.
     * @since 2.0
     */    
    public static final int MIME_OSM =                     115;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete TLDP siendo descartado.
     * @since 2.0
     */    
    public static final int PDU_LDP_CAE =                  116;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete GPSRP siendo descartado.
     * @since 2.0
     */    
    public static final int PDU_GOS_CAE =                  117;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete IPv4 siendo descartado.
     * @since 2.0
     */    
    public static final int PDU_IPV4_CAE =                 118;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete IPv4, marcado con Garant�a de Servicio, siendo descartado.
     * @since 2.0
     */    
    public static final int PDU_IPV4_GOS_CAE =             119;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete MPLS, marcado con Garant�a de Servicio, siendo descartado.
     * @since 2.0
     */    
    public static final int PDU_MPLS_GOS_CAE =             120;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete MPLS siendo descartado.
     * @since 2.0
     */    
    public static final int PDU_MPLS_CAE =                 121;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete que est� siendfo generado.
     * @since 2.0
     */    
    public static final int PAQUETE_GENERADO =             122;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete que est� siendo emitido.
     * @since 2.0
     */    
    public static final int PAQUETE_EMITIDO =              123;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete que est� siendo recibido.
     * @since 2.0
     */    
    public static final int PAQUETE_RECIBIDO =             124;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un paquete que est� siendo conmutado.
     * @since 2.0
     */    
    public static final int PAQUETE_CONMUTADO =            125;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo emisor congestionado al 50%.
     * @since 2.0
     */    
    public static final int EMISOR_CONGESTIONADO_20 =      126;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo receptor congestionado al 50%.
     * @since 2.0
     */    
    public static final int RECEPTOR_CONGESTIONADO_20 =    127;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LER congestionado al 50%.
     * @since 2.0
     */    
    public static final int LER_CONGESTIONADO_20 =         128;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LERA congestionado al 50%.
     * @since 2.0
     */    
    public static final int LERA_CONGESTIONADO_20 =        129;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LSR congestionado al 50%.
     * @since 2.0
     */    
    public static final int LSR_CONGESTIONADO_20 =         130;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LSRA congestionado al 50%.
     * @since 2.0
     */    
    public static final int LSRA_CONGESTIONADO_20 =        131;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo emisor congestionado al 75%.
     * @since 2.0
     */    
    public static final int EMISOR_CONGESTIONADO_60 =      132;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo receptor congestionado al 75%.
     * @since 2.0
     */    
    public static final int RECEPTOR_CONGESTIONADO_60 =    133;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LER congestionado al 75%.
     * @since 2.0
     */    
    public static final int LER_CONGESTIONADO_60 =         134;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LERA congestionado al 75%.
     * @since 2.0
     */    
    public static final int LERA_CONGESTIONADO_60 =        135;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LSR congestionado al 75%.
     * @since 2.0
     */    
    public static final int LSR_CONGESTIONADO_60 =         136;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * a un nodo LSRA congestionado al 75%.
     * @since 2.0
     */    
    public static final int LSRA_CONGESTIONADO_60 =        137;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * un enlace de comunicaciones que cae.
     * @since 2.0
     */    
    public static final int ENLACE_CAIDO =                 138;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * un enlace de comunicaciones que se recupera tras una ca�da.
     * @since 2.0
     */    
    public static final int ENLACE_RECUPERADO =            139;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * un reloj de arena.
     * @since 2.0
     */    
    public static final int TRABAJANDO =                   140;
    /**
     * Esta constante indica al dispensador que le devuelva la imagen que representa
     * una cptura de la web del proyecto.
     * @since 2.0
     */    
    public static final int CAPTURAWEB =                   141;
    
    /** Esta constante indica cuantas im�genes debe cargar en memoria el dispensador de
     * im�genes.
     * @since 2.0
     */    
    private static final int NUMERO_DE_IMAGENES =          142;

    /** Este m�todo es el constructor de la clase; crea una nueva instancia de
     * TDispensadorDeImagenes.
     * @since 2.0
     */
    public TImagesBroker() {
        imagen = new ImageIcon[NUMERO_DE_IMAGENES];
        try {
            cargarImagenes();
        } catch (Exception e) {
            System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/translations/translations").getString("TDispensadorDeImagenes.error"));
        }
    }

    /** Este m�todo carga en memoria todas las im�genes que se van a utilizar en la
     * aplicaci�n.
     * @since 2.0
     */    
    private void cargarImagenes() {
        imagen[ABRIR] = new javax.swing.ImageIcon(getClass().getResource("/images/abrir.png"));
        imagen[ABRIR_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/abrir_menu_gris.png"));
        imagen[ABRIR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/abrir_menu.png"));
        imagen[ADVERTENCIA] = new javax.swing.ImageIcon(getClass().getResource("/images/advertencia.png"));
        imagen[ASISTENTE] = new javax.swing.ImageIcon(getClass().getResource("/images/asistente.png"));
        imagen[BOTON_COMENZAR] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_comenzar.png"));
        imagen[BOTON_COMENZAR_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_comenzar_gris.png"));
        imagen[BOTON_GENERAR] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_generar.png"));
        imagen[BOTON_GENERAR_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_generar_gris.png"));
        imagen[BOTON_PARAR] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_parar.png"));
        imagen[BOTON_PARAR_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_parar_gris.png"));
        imagen[BOTON_PAUSA] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_pausa.png"));
        imagen[BOTON_PAUSA_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_pausa_gris.png"));
        imagen[CERRAR] = new javax.swing.ImageIcon(getClass().getResource("/images/cerrar.png"));
        imagen[CERRAR_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/cerrar_menu_gris.png"));
        imagen[CERRAR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/cerrar_menu.png"));
        imagen[COMENTARIO] = new javax.swing.ImageIcon(getClass().getResource("/images/comentario.png"));
        imagen[COMENTARIO_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/comentario_menu_gris.png"));
        imagen[COMENTARIO_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/comentario_menu.png"));
        imagen[EMISOR] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor.png"));
        imagen[EMISOR_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor_menu_gris.png"));
        imagen[EMISOR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor_menu.png"));
        imagen[EMISOR_MOVIENDOSE] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor_moviendose.png"));
        imagen[ENLACE] = new javax.swing.ImageIcon(getClass().getResource("/images/enlace.png"));
        imagen[ENLACE_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/enlace_menu_gris.png"));
        imagen[ENLACE_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/enlace_menu.png"));
        imagen[ERROR] = new javax.swing.ImageIcon(getClass().getResource("/images/error.png"));
        imagen[GUARDAR] = new javax.swing.ImageIcon(getClass().getResource("/images/guardar.png"));
        imagen[GUARDAR_COMO] = new javax.swing.ImageIcon(getClass().getResource("/images/guardar_como.png"));
        imagen[GUARDAR_COMO_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/guardar_como_menu_gris.png"));
        imagen[GUARDAR_COMO_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/guardar_como_menu.png"));
        imagen[GUARDAR_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/guardar_menu_gris.png"));
        imagen[GUARDAR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/guardar_menu.png"));
        imagen[ICONO_VENTANA_INTERNA] = new javax.swing.ImageIcon(getClass().getResource("/images/icono_ventana_interna.png"));
        imagen[ICONO_VENTANA_INTERNA_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/icono_ventana_interna_menu.png"));
        imagen[ICONO_SIMMPLS] = new javax.swing.ImageIcon(getClass().getResource("/images/iconosimMPLS.png"));
        imagen[IMPRIMIR] = new javax.swing.ImageIcon(getClass().getResource("/images/imprimir.png"));
        imagen[IMPRIMIR_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/imprimir_menu_gris.png"));
        imagen[IMPRIMIR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/imprimir_menu.png"));
        imagen[LER] = new javax.swing.ImageIcon(getClass().getResource("/images/ler.png"));
        imagen[LER_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/ler_menu_gris.png"));
        imagen[LER_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/ler_menu.png"));
        imagen[LER_MOVIENDOSE] = new javax.swing.ImageIcon(getClass().getResource("/images/ler_moviendose.png"));
        imagen[LERA] = new javax.swing.ImageIcon(getClass().getResource("/images/lera.png"));
        imagen[LERA_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/lera_menu_gris.png"));
        imagen[LERA_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/lera_menu.png"));
        imagen[LERA_MOVIENDOSE] = new javax.swing.ImageIcon(getClass().getResource("/images/lera_moviendose.png"));
        imagen[LICENCIA] = new javax.swing.ImageIcon(getClass().getResource("/images/licencia.png"));
        imagen[LICENCIA_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/licencia_menu_gris.png"));
        imagen[LICENCIA_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/licencia_menu.png"));
        imagen[LSR] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr.png"));
        imagen[LSR_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr_menu_gris.png"));
        imagen[LSR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr_menu.png"));
        imagen[LSR_MOVIENDOSE] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr_moviendose.png"));
        imagen[LSRA] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra.png"));
        imagen[LSRA_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra_menu_gris.png"));
        imagen[LSRA_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra_menu.png"));
        imagen[LSRA_MOVIENDOSE] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra_moviendose.png"));
        imagen[NUBE] = new javax.swing.ImageIcon(getClass().getResource("/images/nube.png"));
        imagen[NUEVO] = new javax.swing.ImageIcon(getClass().getResource("/images/nuevo.png"));
        imagen[NUEVO_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/nuevo_menu_gris.png"));
        imagen[NUEVO_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/nuevo_menu.png"));
        imagen[OSI_CERTIFIED] = new javax.swing.ImageIcon(getClass().getResource("/images/osi_certified.png"));
        imagen[RECEPTOR] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor.png"));
        imagen[RECEPTOR_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor_menu_gris.png"));
        imagen[RECEPTOR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor_menu.png"));
        imagen[RECEPTOR_MOVIENDOSE] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor_moviendose.png"));
        imagen[SALIR] = new javax.swing.ImageIcon(getClass().getResource("/images/salir.png"));
        imagen[SALIR_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/salir_menu.png"));
        imagen[SPLASH] = new javax.swing.ImageIcon(getClass().getResource("/images/splash.png"));
        imagen[SPLASH_INICIO] = new javax.swing.ImageIcon(getClass().getResource("/images/splash_inicio.png"));
        imagen[SPLASH_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/splash_menu_gris.png"));
        imagen[SPLASH_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/splash_menu.png"));
        imagen[TUTORIAL] = new javax.swing.ImageIcon(getClass().getResource("/images/tutorial.png"));
        imagen[TUTORIAL_MENU_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/tutorial_menu_gris.png"));
        imagen[TUTORIAL_MENU] = new javax.swing.ImageIcon(getClass().getResource("/images/tutorial_menu.png"));
        imagen[IMAGEN_NO_ENCONTRADA] = new javax.swing.ImageIcon(getClass().getResource("/images/imagen_no_encontrada.png"));
        imagen[EMISOR_MENU_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor_menu_brillo.png"));
        imagen[RECEPTOR_MENU_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor_menu_brillo.png"));
        imagen[LER_MENU_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/ler_menu_brillo.png"));
        imagen[LSR_MENU_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr_menu_brillo.png"));
        imagen[LERA_MENU_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/lera_menu_brillo.png"));
        imagen[LSRA_MENU_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra_menu_brillo.png"));
        imagen[ENLACE_MENU_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/enlace_menu_brillo.png"));
        imagen[BOTON_COMENZAR_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_comenzar_brillo.png"));
        imagen[BOTON_GENERAR_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_generar_brillo.png"));
        imagen[BOTON_PARAR_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_parar_brillo.png"));
        imagen[BOTON_PAUSA_BRILLO] = new javax.swing.ImageIcon(getClass().getResource("/images/boton_pausa_brillo.png"));
        imagen[VISTA_ICONOS] = new javax.swing.ImageIcon(getClass().getResource("/images/vista_iconos_menu.png"));
        imagen[VISTA_CASCADA] = new javax.swing.ImageIcon(getClass().getResource("/images/vista_cascada_menu.png"));
        imagen[VISTA_HORIZONTAL] = new javax.swing.ImageIcon(getClass().getResource("/images/vista_horizontal_menu.png"));
        imagen[VISTA_VERTICAL] = new javax.swing.ImageIcon(getClass().getResource("/images/vista_vertical_menu.png"));
        imagen[PDU_LDP] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_ldp.png"));
        imagen[PDU_GOS] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_gos.png"));
        imagen[PDU_IPV4] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_ipv4.png"));
        imagen[PDU_MPLS] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_mpls.png"));
        imagen[EMISOR_CONGESTIONADO] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor_congestionado.png"));
        imagen[RECEPTOR_CONGESTIONADO] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor_congestionado.png"));
        imagen[LER_CONGESTIONADO] = new javax.swing.ImageIcon(getClass().getResource("/images/ler_congestionado.png"));
        imagen[LERA_CONGESTIONADO] = new javax.swing.ImageIcon(getClass().getResource("/images/lera_congestionado.png"));
        imagen[LSR_CONGESTIONADO] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr_congestionado.png"));
        imagen[LSRA_CONGESTIONADO] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra_congestionado.png"));
        imagen[PDU_IPV4_GOS] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_ipv4_gos.png"));
        imagen[PDU_MPLS_GOS] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_mpls_gos.png"));
        imagen[SIMULACION] = new javax.swing.ImageIcon(getClass().getResource("/images/simulacion.png"));
        imagen[OPCIONES] = new javax.swing.ImageIcon(getClass().getResource("/images/opciones.png"));
        imagen[DISENIO] = new javax.swing.ImageIcon(getClass().getResource("/images/disenio.png"));
        imagen[ANALISIS] = new javax.swing.ImageIcon(getClass().getResource("/images/analisis.png"));
        imagen[SOBRE_EMAIL] = new javax.swing.ImageIcon(getClass().getResource("/images/sobre_email.png"));
        imagen[ACEPTAR] = new javax.swing.ImageIcon(getClass().getResource("/images/aceptar.png"));
        imagen[CANCELAR] = new javax.swing.ImageIcon(getClass().getResource("/images/cancelar.png"));
        imagen[AVANZADA] = new javax.swing.ImageIcon(getClass().getResource("/images/avanzada.png"));
        imagen[TOPO_GRIS] = new javax.swing.ImageIcon(getClass().getResource("/images/topo_gris.png"));
        imagen[TOPO_VERDE] = new javax.swing.ImageIcon(getClass().getResource("/images/topo_verde.png"));
        imagen[INTERROGACION] = new javax.swing.ImageIcon(getClass().getResource("/images/interrogacion.png"));
        imagen[MIME_OSM] = new javax.swing.ImageIcon(getClass().getResource("/images/mime_osm.png"));
        imagen[PDU_IPV4_GOS_CAE] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_ipv4_gos_cae.png"));
        imagen[PDU_MPLS_GOS_CAE] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_mpls_gos_cae.png"));
        imagen[PDU_LDP_CAE] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_ldp_cae.png"));
        imagen[PDU_GOS_CAE] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_gos_cae.png"));
        imagen[PDU_IPV4_CAE] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_ipv4_cae.png"));
        imagen[PDU_MPLS_CAE] = new javax.swing.ImageIcon(getClass().getResource("/images/pdu_mpls_cae.png"));
        imagen[PAQUETE_GENERADO] = new javax.swing.ImageIcon(getClass().getResource("/images/paquete_generado.png"));
        imagen[PAQUETE_EMITIDO] = new javax.swing.ImageIcon(getClass().getResource("/images/paquete_emitido.png"));
        imagen[PAQUETE_RECIBIDO] = new javax.swing.ImageIcon(getClass().getResource("/images/paquete_recibido.png"));
        imagen[PAQUETE_CONMUTADO] = new javax.swing.ImageIcon(getClass().getResource("/images/paquete_conmutado.png"));
        imagen[EMISOR_CONGESTIONADO_20] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor_congestionado_20.png"));
        imagen[RECEPTOR_CONGESTIONADO_20] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor_congestionado_20.png"));
        imagen[LER_CONGESTIONADO_20] = new javax.swing.ImageIcon(getClass().getResource("/images/ler_congestionado_20.png"));
        imagen[LERA_CONGESTIONADO_20] = new javax.swing.ImageIcon(getClass().getResource("/images/lera_congestionado_20.png"));
        imagen[LSR_CONGESTIONADO_20] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr_congestionado_20.png"));
        imagen[LSRA_CONGESTIONADO_20] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra_congestionado_20.png"));
        imagen[EMISOR_CONGESTIONADO_60] = new javax.swing.ImageIcon(getClass().getResource("/images/emisor_congestionado_60.png"));
        imagen[RECEPTOR_CONGESTIONADO_60] = new javax.swing.ImageIcon(getClass().getResource("/images/receptor_congestionado_60.png"));
        imagen[LER_CONGESTIONADO_60] = new javax.swing.ImageIcon(getClass().getResource("/images/ler_congestionado_60.png"));
        imagen[LERA_CONGESTIONADO_60] = new javax.swing.ImageIcon(getClass().getResource("/images/lera_congestionado_60.png"));
        imagen[LSR_CONGESTIONADO_60] = new javax.swing.ImageIcon(getClass().getResource("/images/lsr_congestionado_60.png"));
        imagen[LSRA_CONGESTIONADO_60] = new javax.swing.ImageIcon(getClass().getResource("/images/lsra_congestionado_60.png"));
        imagen[ENLACE_CAIDO] = new javax.swing.ImageIcon(getClass().getResource("/images/enlace_caido.png"));
        imagen[ENLACE_RECUPERADO] = new javax.swing.ImageIcon(getClass().getResource("/images/enlace_recuperado.png"));
        imagen[TRABAJANDO] = new javax.swing.ImageIcon(getClass().getResource("/images/reloj_arena.png"));
        imagen[CAPTURAWEB] = new javax.swing.ImageIcon(getClass().getResource("/images/capturaweb.png"));
    }

    /** Obtiene una de las im�genes del dispensador de im�genes como un onjeto Image.
     * @param queImagen Constante que identifica la imagen que se desea obtener. Es una de las
     * constantes definidas en la clase.
     * @return Un objeto Image con la imagen que se ha solicitado.
     * @since 2.0
     */    
    public Image obtenerImagen(int queImagen) {
        ImageIcon imagenBuscada = imagen[queImagen];
        if (imagenBuscada == null) {
            imagenBuscada = imagen[IMAGEN_NO_ENCONTRADA];
        }
        return imagenBuscada.getImage();
    }

    /** Obtiene una de las im�genes del dispensador de im�genes como un onjeto ImageIcon.
     * @param queImagen Constante que identifica la imagen que se desea obtener. Es una de las
     * constantes definidas en la clase.
     * @return Un objeto ImageIcon con la imagen que se ha solicitado.
     * @since 2.0
     */    
    public ImageIcon obtenerIcono(int queImagen) {
        ImageIcon imagenBuscada = imagen[queImagen];
        if (imagenBuscada == null) {
            imagenBuscada = imagen[IMAGEN_NO_ENCONTRADA];
        }
        return imagenBuscada;
    }

    /** Este atributo es el array din�mico de im�genes que almacenar� las referencias a
     * las im�genes cargadas.
     * @since 2.0
     */    
    private ImageIcon imagen[];
}
