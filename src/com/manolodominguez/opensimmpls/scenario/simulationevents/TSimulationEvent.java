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
package com.manolodominguez.opensimmpls.scenario.simulationevents;

import com.manolodominguez.opensimmpls.scenario.TTopologyElement;

/**
 * Esta clase es la superclase de todos los eventos de simulaci�n del simulador.
 * Todos los eventos e simulaci�n deben heredar e implementar los m�todos
 * abstractos de esta clase.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TSimulationEvent extends TOpenSimMPLSEvent {

    /**
     * Crea una nueva instancia de TEventoSimulacion
     * @since 2.0
     * @param inst Instante de tiempo en el que se produjo el evento.
     * @param emisor Elemento de la topolog�a que produjo el evento.
     * @param id Identificador unico para el evento.
     */
    public TSimulationEvent(Object emisor, long id, long inst) {
        super(emisor, id, inst);
    }

    /**
     * Este m�todo permite saber el tipo de evento de que se trata.
     * @return TOpenSimMPLSEvent.SIMULACION, indicando que se trata de un evento de simulaci�n.
     * @since 2.0
     */    
    public int getType() {
        return super.SIMULATION;
    }

    /**
     * Este m�todo permite saber el tipo de evento de simulaci�n de que se trata.
     * @return Tipo del evento de simulaci�n. Una de las constantes de la clase.
     * @since 2.0
     */    
    public abstract int getSubtype();

    /**
     * Este m�todo permite obtener el elemento de la topolog�a que gener� el evento.
     * @return Elemento de la topologia que gener� el evento.
     * @since 2.0
     */    
    public TTopologyElement obtenerFuente() {  
        return (TTopologyElement) super.source;
    }
    
    /**
     * Constante que identifica a un evento de generaci�n de paquete.
     * @since 2.0
     */    
    public static final int PACKET_GENERATED =              0;
    /**
     * Constante que identifica a un evento de  env�o de paquete.
     * @since 2.0
     */    
    public static final int PACKET_SENT =               1;
    /**
     * Constante que identifica a un evento de recepci�n de paquete.
     * @since 2.0
     */    
    public static final int PACKET_RECEIVED =              2;
    /**
     * Constante que identifica a un evento de conmutaci�n de paquete.
     * @since 2.0
     */    
    public static final int PACKET_SWITCHED =             3;
    /**
     * Constante que identifica a un evento de descarte de paquete.
     * @since 2.0
     */    
    public static final int PACKET_DISCARDED =            4;
    /**
     * Constante que identifica a un evento de paquete en tr�nsito.
     * @since 2.0
     */    
    public static final int PACKET_ON_FLY =           5;
    /**
     * Constante que identifica a un evento de paquete almacenado en la DMGP
     * @since 2.0
     */    
    public static final int PAQUETE_ALMACENADO_DMGP =       6;
    /**
     * Constante que identifica a un evento de paquete eliminado de la DMGP
     * @since 2.0
     */    
    public static final int PAQUETE_ELIMINADO_DMGP =        7;
    /**
     * Constante que identifica a un evento de paquete hallado en la DMGP
     * @since 2.0
     */    
    public static final int PAQUETE_ENCONTRADO_DMGP =       8;
    /**
     * Constante que identifica a un evento de paquete no encontrado en la DMGP
     * @since 2.0
     */    
    public static final int PAQUETE_NO_ENCONTRADO_DMGP =    9;
    /**
     * Constante que identifica a un evento de paquete retransmitido
     * @since 2.0
     */    
    public static final int PAQUETE_RETRANSMITIDO =         10;

    /**
     * Constante que identifica a un evento de enlace sobrecargado
     * @since 2.0
     */    
    public static final int ENLACE_SOBRECARGADO =          11;
    /**
     * Constante que identifica a un evento de enlace ca�do
     * @since 2.0
     */    
    public static final int LINK_BROKEN =                 12;
    /**
     * Constante que identifica a un evento de enlace recuperado
     * @since 2.0
     */    
    public static final int LINK_RECOVERED =            13;
    
    /**
     * Constante que identifica a un evento de enlace congestionado
     * @since 2.0
     */    
    public static final int NODE_CONGESTED =           14;

    /**
     * Constante que identifica a un evento de etiqueta solicitada
     * @since 2.0
     */    
    public static final int LABEL_REQUESTED =          15;
    /**
     * Constante que identifica a un evento de etiqueta recibida
     * @since 2.0
     */    
    public static final int LABEL_RECEIVED =            16;
    /**
     * Constante que identifica a un evento de etiqueta asignada
     * @since 2.0
     */    
    public static final int LABEL_ASSIGNED =            17;
    /**
     * Constante que identifica a un evento de etiqueta denegada
     * @since 2.0
     */    
    public static final int LABEL_DENIED =            18;
    /**
     * Constante que identifica a un evento de etiqueta eliminada
     * @since 2.0
     */    
    public static final int LABEL_REMOVED =           19;

    /**
     * Constante que identifica a un evento de LSP establecido
     * @since 2.0
     */    
    public static final int LSP_ESTABLISHED =              20;
    /**
     * Constante que identifica a un evento de LSP no establecido
     * @since 2.0
     */    
    public static final int LSP_NO_ESTABLECIDO =           21;
    /**
     * Constante que identifica a un evento de LSP eliminado
     * @since 2.0
     */    
    public static final int LSP_REMOVED =                22;
    /**
     * Constante que identifica a un evento de LSP de respaldo establecido
     * @since 2.0
     */    
    public static final int LSP_BACKUP_ESTABLECIDO =       23;
    /**
     * Constante que identifica a un evento de LSP de respaldo no establecido
     * @since 2.0
     */    
    public static final int LSP_BACKUP_NO_ESTABLECIDO =    24;
    /**
     * Constante que identifica a un evento de LSP de respaldo eliminado
     * @since 2.0
     */    
    public static final int LSP_BACKUP_ELIMINADO =         25;
    /**
     * Constante que identifica a un evento de LSP de respaldo activado
     * @since 2.0
     */    
    public static final int LSP_BACKUP_ACTIVADO =          26;

    /**
     * Constante que identifica a un evento de retransmisi�n solicitada
     * @since 2.0
     */    
    public static final int RETRANSMISION_SOLICITADA =     26;
    /**
     * Constante que identifica a un evento de retransmisi�n afirmativa
     * @since 2.0
     */    
    public static final int RETRANSMISION_AFIRMATIVA =     27;
    /**
     * Constante que identifica a un evento de retransmisi�n negativa
     * @since 2.0
     */    
    public static final int RETRANSMISION_NEGATIVA =       28;
    /**
     * Constante que identifica a un evento de retransmisi�n recibida
     * @since 2.0
     */    
    public static final int RETRANSMISION_RECIBIDA =       29;
    /**
     * Constante que identifica a un evento de paquete encaminado
     * @since 2.0
     */    
    public static final int PACKET_ROUTED =           30;
}
