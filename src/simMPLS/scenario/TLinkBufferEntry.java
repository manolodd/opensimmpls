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
package simMPLS.scenario;

import simMPLS.protocols.TAbstractPDU;

/**
 * Esta clase implementa una entrada dentro del buffer de un enlace. El buffer del
 * enlace existe �nicamente a efectos de simulaci�n visual, no existe en la
 * realidad.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TLinkBufferEntry implements Comparable {

    /**
     * Crea una nueva instancia de TEntradaBufferEnlace
     * @param p Paquete que entra en el enlace.
     * @param tiempoEspera Tiempo que debe esperar el paquete en el enlace. Generalmente corresponde al
     * retardo del enlace.
     * @param dest Especifica cu�l de los nodos que une el enlace es destino del tr�fico. Los
     * enlaces son full duplex.
     * @since 2.0
     */
    public TLinkBufferEntry(TAbstractPDU p, long tiempoEspera, int dest) {
        te = tiempoEspera;
        teInicial = tiempoEspera;
        paquete = p;
        destino = dest;
    }
    
    /**
     * Este m�todo permite comparar para su ordenaci�n dos instancias de
 TLinkBufferEntry.
     * @param o Instancia de tipo TLinkBufferEntry.
     * @return -1, 0 � 1, dependiendo de si la entrada del buffer con la que se compara es
     * ordinalmente menos, igual o mayor.
     * @since 2.0
     */    
    public int compareTo(Object o) {
        TLinkBufferEntry ebe = (TLinkBufferEntry) o;
        TAbstractPDU pdu = ebe.getPacket();
        return this.paquete.compareTo(pdu);
    }

    /**
     * Este m�todo obtiene el paquete de la entrada del buffer.
     * @return Paquete de la entrada del buffer.
     * @since 2.0
     */    
    public TAbstractPDU getPacket() {
        return paquete;
    }

    /**
     * Este m�todo devuelve el total de tiempo que el paquete debe esperar en el
     * enlace.
     * @return El tiempo que el nodo debe esperar en el enlace desde que entra hasta que llega
     * al destino.
     * @since 2.0
     */    
    public long getTotalTransitDelay() {
        return this.teInicial;
    }

    /**
     * Este m�todo permte poner un paquete en la entrada el buffer.
     * @param p Paquete que deseamos insertar en la entrada del buffer.
     * @since 2.0
     */    
    public void ponerPaquete(TAbstractPDU p) {
        paquete = p;
    }

    /**
     * Este m�todo permite establecer el tiempo que el paquete deber� esperar en el
     * enlace desde que entra hasta que llega al destino.
     * @param t Tiempo de espera total para el paquete.
     * @since 2.0
     */    
    public void ponerTiempoEspera(long t) {
        te = t;
        teInicial = t;
    }

    /**
     * Este m�todo permite obtener el tiempo que a�n le queda al paquete para llegar al
     * destino.
     * @return Tiempo (en nanosegundos) que le falta al paquete para llegar al destino.
     * @since 2.0
     */    
    public long getRemainingTransitDelay() {
        return te;
    }

    /**
     * Este m�todo resta al tiempo de espera del paquete en la entrada del buffer, el
     * tiempo que va transcurriendo. Al llegar a cero, el paquete habr� llegado al
     * destino.
     * @param paso Tiempo que se le resta al tiempo de espera del paquete.
     * @since 2.0
     */    
    public void substractStepLength(long paso) {
        te -= paso;
        if (te < 0) 
            te = 0;
    }

    /**
     * Este m�todo permite establecer, para esta entrada concreta del buffer, cu�l de
     * los nodos extremos es el destino.
     * @param dest 1 � 2, dependiendo de si el paquete va dirigido al extremo 1 o al extremo2 el
     * enlace.
     * @since 2.0
     */    
    public void ponerDestino(int dest) {
        destino = dest;
    }
    
    /**
     * Este m�todo permite obtener el destino del tr�fico, esto es, qu� nodo es el que
     * debe recibir el paquete.
     * @return 1 o 2, dependiendo de si el tr�fico debe salir por el extremo 1 o el extremo 2
     * del enlace.
     * @since 2.0
     */    
    public int getTargetEnd() {
        return destino;
    }
    
    private TAbstractPDU paquete;
    private int destino;
    private long te;
    private long teInicial;
}
