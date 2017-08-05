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

import com.manolodominguez.opensimmpls.hardware.timer.TTimerEvent;
import com.manolodominguez.opensimmpls.hardware.timer.ITimerEventListener;
import com.manolodominguez.opensimmpls.hardware.simulationcollector.TSimulationCollector;
import com.manolodominguez.opensimmpls.utils.TLongIDGenerator;
import java.awt.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase abstracta es la superclase de la que derivan todos los elementos que
 * se pueden insertar en una topolog�a.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TTopologyElement implements ITimerEventListener, Runnable {

    /**
     * Crea una nueva instancia de TElementoTopologia
     * @param tipo Indica el tipo de elemento de que se trata. Una de las constantes de la clase.
     * @param il generador de identificadores de eventos.
     * @since 2.0
     */
    public TTopologyElement(int tipo, TLongIDGenerator il) {
        tipoElemento = tipo;
        eliminarDelReloj = false;
        hilo = null;
        simulationEventsListener = null;
        this.longIdentifierGenerator = il;
        this.availableNs = 0;
        alive = true;
        wellConfigured = false;
        instanteDeTiempo = 0;
        duracionTic = 0;
    }

    /**
     * Este m�todo devuelve el instante de tiempo en que se encuentra el elemento. Se
     * usa para asignar dicho instante a los eventos generados.
     * @return El instante de tiempo en que se encuentra el elemento.
     * @since 2.0
     */    
    public long getAvailableTime() {
        return this.instanteDeTiempo;
    }
    
    /**
     * Este m�todo establece el instante de tiempo en que se encuentra el elemento.
     * @param i Instante de tiempo.
     * @since 2.0
     */    
    public void setTimeInstant(long i) {
        this.instanteDeTiempo = i;
    }

    /**
     * Este m�todo obtiene la duraci�n del tic del que dispone el elemento para operar.
     * @return La duraci�n del tic, en nanosegundos.
     * @since 2.0
     */    
    public int getTickDuration() {
        return this.duracionTic;
    }
    
    /**
     * Este m�todo establece la duraci�n del tic del que dispone el elemento.
     * @param dc Duraci�n del tic en nanosegundos.
     * @since 2.0
     */    
    public void setStepDuration(int dc) {
        this.duracionTic = dc;
    }
    
    /**
     * Este m�todo permite establecer si se quiere dejar de recibir eventos de reloj o
     * no.
     * @param p TRUE, indica que se dejen de recibir eventos de reloj cuanto antes. FALSE indica
     * lo contrario.
     * @since 2.0
     */    
    public void ponerPurgar(boolean p) {
        eliminarDelReloj = p;
    }

    /**
     * Este m�todo devuelve si se ha decidido no recibir eventos del reloj o no.
     * @return TRUE, indica que se ha decidido dejar de recibir eventos del reloj. FALSE indica
     * lo contrario.
     * @since 2.0
     */    
    public boolean hasToBePurged() {
        return eliminarDelReloj;
    }

    /**
     * Este m�todo devuelve el tipo de elemento de que se trata.
     * @return El tipo del elemento. Una de las constantes de la clase.
     * @since 2.0
     */    
    public int getElementType() {
        return tipoElemento;
    }

    /**
     * Este m�todo permite establecer el n�mero de nanosegundos de que dispone el
     * elemento para operar.
     * @param ns Nanosegundos de que dispone el elemento.
     * @since 2.0
     */    
    public void ponerNsDisponibles(long ns) {
        availableNs = ns;
    }

    /**
     * Este m�todo permite obtener el n�mero de nanosegundos de que dispone el elemento
     * para operar.
     * @return El n�mero de nanosegundos de que dispone el elemento.
     * @since 2.0
     */    
    public double obtenerNsDisponibles() {
        return availableNs;
    }

    /**
     * Este m�todo a�ade nanosegundos al n�mero de nanosegundos disponibles para el
     * elemento.
     * @param ns Nanosegundos que queremos a�adir al disponible para el elemento.
     * @since 2.0
     */    
    public void sumarNsADisponibles(long ns) {
        availableNs += ns;
    }

    /**
     * Este m�todo resta nanosegundos al n�mero de nanosegundos disponibles para el
     * elemento.
     * @param ns nanosegundos que queremos descontar del disponible para el elemento.
     * @since 2.0
     */    
    public void restarNsADisponibles(long ns) {
        if (availableNs < ns)
            availableNs = 0;
        else
            availableNs -= ns;
    }

    /**
     * Este m�todo pone en funcionamiento el hilo independiente que maneja al elemento.
     * @since 2.0
     */    
    public synchronized void startOperation() {
        if (hilo == null) {
            hilo = new Thread(this);
            this.hilo.start();
        } else {
            if (!hilo.isAlive()) {
                hilo = new Thread(this);
                this.hilo.start();
            }
        }
    }
    
    /**
     * Este m�todo se usa para sincronizar el hilo de este elemento con el de todos los
     * dem�s y con el hilo principal. Este m�todo es llamado por el reloj del
     * simulador, que sincroniza toda la simulaci�n.
     * @since 2.0
     */    
    public synchronized void waitForCompletion() {
        if (hilo != null) {
            try {
                this.hilo.join();
            } catch (Exception e) {
                System.out.println(java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/lenguajes/lenguajes").getString("TElementoTopologia.ErrorFinReloj") + e.toString());
            };
        }
    }

    /**
     * Este m�todo establece cu�l ser� el recolector de eventos de simulaci�n al que se
     * le deben enviar los eventos que genere este elemento.
     * @param rs Recolector de simulaci�n elegido.
     * @throws ESimulationSingleSubscriber Esta excepci�n salta si se intenta establecer un recolector de simulaci�n y ya
     * hay otro establecido.
     * @since 2.0
     */    
    public void addListenerSimulacion(TSimulationCollector rs) throws ESimulationSingleSubscriber {
        if ( this.simulationEventsListener == null) {
            this.simulationEventsListener = rs;
        } else {
            throw new ESimulationSingleSubscriber();        
        }
    }

    /**
     * Este m�todo elimina el v�nculo con el recolector de eventos de simulaci�n al
     * que se le deben enviar los eventos que genere este elemento.
     * @since 2.0
     */    
    public void removeListenerSimulacion() {
        this.simulationEventsListener = null;
    }

    /**
     * Este m�todo env�a un evento de simulaci�n al recolector de simulaci�n al que
     * est� vinculado el elemento.
     * @param evt Evento que se quiere enviar al recolector.
     * @since 2.0
     */    
    public void generateSimulationEvent(TSimulationEvent evt) {
        if (simulationEventsListener != null) {
            simulationEventsListener.captureSimulationEvents(evt);
        }
    }

    /**
     * Este m�todo comprueba si el nodo est� "vivo" o no.
     * @return TRUE, si el nodo est� "vivo". FALSE en caso contrario.
     * @since 2.0
     */    
    public boolean estaVivo() {
        return alive;
    }

    /**
     * Este m�todo establece si el elemento est� bien configurado o no.
     * @param bc TRUE, si el elemento est� bien configurado. FALSE en caso contrario.
     * @since 2.0
     */    
    public void setWellConfigured(boolean bc) {
        this.wellConfigured = bc;
    }
    
    /**
     * Este m�todo recibe eventos de sincronizaci�n del reloj principal del simulador, que sincroniza
     * todo.
     * @param evt Evento que el reloj principal env�a al elemento.
     * @since 2.0
     */    
    public abstract void receiveTimerEvent(TTimerEvent evt);

    /**
     * Este m�todo es el que se ejecuta cuando se pone el hilo independiente del
     * elemento en funcionamiento.
     * @since 2.0
     */    
    public abstract void run();
    /**
     * Este m�todo calcula si el elemento est� bien configurado o no.
     * @return TRUE, si el elemento est� bien configurado. FALSE en otro caso.
     * @since 2.0
     */    
    public abstract boolean isWellConfigured();
    /**
     * Este m�todo genera un mensaje legible correspondiente al c�digo de error
     * especificado.
     * @param e C�digo de error que debe transformar el m�todo.
     * @return Mensaje de texto explicativo del c�digo de error.
     * @since 2.0
     */    
    public abstract String getErrorMessage(int e);
    /**
     * Este m�todo convierte el elemento completo en una cadena de texto almacenable en
     * disco.
     * @return La euivalencia en texto del elemento.
     * @since 2.0
     */    
    public abstract String marshall();
    /**
     * Este m�todo toma una cadena de texto que representa un elemento serializado, y
     * con ella configura correctamente el elemento.
     * @param elemento Representaci�n serializada (texto) de un elemento.
     * @return TRUE, si se ha podido deserializar correctamente el elemento textual. FALSE en
     * caso contrario.
     * @since 2.0
     */    
    public abstract boolean unMarshall(String elemento);
    /**
     * Este m�todo reinicia los atributos del elemento como si acabase de ser creado en
     * el constructor.
     * @since 2.0
     */    
    public abstract void reset();

    /**
     * Esta constante indica que el elemento es un enlace.
     * @since 2.0
     */    
    public static final int LINK = 0;
    /**
     * Esta constante indica que el elemento es un nodo.
     * @since 2.0
     */    
    public static final int NODO = 1;

    private int tipoElemento;
    private boolean eliminarDelReloj;
    private Thread hilo;
    /**
     * Este atributo es el recolector de eventos de simulaci�n al que se deben mandar
     * los eventos de simulaci�n que el elemento vaya generando.
     * @since 2.0
     */    
    public TSimulationCollector simulationEventsListener;
    /**
     * Este atributo es el generador de identificadores que debe usar el elemento para
     * asignar un identificador unico a cada evento que genere.
     * @since 2.0
     */    
    public TLongIDGenerator longIdentifierGenerator;
    /**
     * Este atributo almacena en cada momento los nanosegundos de los que dispone el
     * elemento para realizar las operaciones que necesite.
     * @since 2.0
     */    
    protected double availableNs;
    private boolean alive;
    /**
     * Este atributo almacenar� en todo momento si el elemento est� bien configurado o
     * no.
     * @since 2.0
     */    
    protected boolean wellConfigured;
    private long instanteDeTiempo;
    private int duracionTic;
}
