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
package com.manolodominguez.opensimmpls.scenario;

/**
 * Esta clase implementa un evento que se usar� para notificar que se ha creado un
 * LSP.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSELSPEstablished extends TSimulationEvent {

    /**
     * Crea una nueva instancia de TESLSPEstablecido
     * @since 2.0
     * @param inst Instante de tiempo en el que se gener� el evento.
     * @param emisor nodo que gener� el evento
     * @param id Identificador unico del evento.
     */
    public TSELSPEstablished(Object emisor, long id, long inst) {
        super(emisor, id, inst);
    }

    /**
     * Este m�todo obtiene el subtipo del evento, si los hubiese.
     * @return El subtipo del evento.
     * @since 2.0
     */    
    public int getSubtype() {
        return super.LSP_ESTABLISHED;
    }

    /**
     * Este m�todo obtiene el nombre del enlace que origin� el evento.
     * @return El nombre del enlace que origin� el evento.
     * @since 2.0
     */    
    public String obtenerNombre() {
        TTopologyElement et = null;
        TLink ent = null;
        TNode nt = null;
        et = super.obtenerFuente();
        if (et.getElementType() == TTopologyElement.LINK) {
            ent = (TLink) et;
            return ent.getName();
        } else if (et.getElementType() == TTopologyElement.NODO) {
            nt = (TNode) et;
            return nt.getName();
        }
        return ("");
    }

    /**
     * Este m�todo obtiene un texto con el tipo de evento.
     * @return Un texto con el tipo de evento.
     * @since 2.0
     */    
    public String obtenerNombreTipo() {
        TTopologyElement et = null;
        et = super.obtenerFuente();
        if (et.getElementType() == TTopologyElement.LINK) {
            return ("Enlace ");
        } else if (et.getElementType() == TTopologyElement.NODO) {
            return ("Nodo ");
        }
        return ("");
    }

    /**
     * Este m�todo explcia el evento en una l�nea de texto.
     * @return El texto explicando el evento.
     * @since 2.0
     */    
    public String toString() {
        String cad = "";
        cad += "[";
        cad += this.obtenerNombreTipo();
        cad += " ";
        cad += this.obtenerNombre();
        cad += "] ha establecido un tramo de LSP";
        return(cad);
    }
}
