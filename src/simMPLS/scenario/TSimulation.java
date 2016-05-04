/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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

import simMPLS.ui.simulator.JSimulationPanel;
import simMPLS.hardware.simulationcollector.TSimulationCollector;

/**
 * Esta clase implementa un objeto que almacena los datos globales de la
 * simulaci�n.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSimulation {

    /**
     * Crea una nueva instancia de TScenario
     * @param e Escenario al que pertenecen los datos de la simulaci�n.
     * @since 2.0
     */
    public TSimulation(TScenario e) {
        escenarioPadre = e;
        recolector = new TSimulationCollector();
        duracion = 500;
        paso = 1;
    }

    /**
     * Este m�todo permite establecer el panel de simulaci�n en el cual se realizar� la
     * simulaci�n.
     * @since 2.0
     * @param ps El panel de simulaci�n.
     */    
    public void ponerPanelSimulacion(JSimulationPanel ps) {
        recolector.setSimulationPanel(ps);
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase dej�ndolos como reci�n iniciados
     * por el constructor.
     * @since 2.0
     */    
    public void reset() {
        recolector.reset();
    }
    
    /**
     * Este m�todo permite establecer la duraci�n total de la simulaci�n.
     * @param d Duraci�n total de la simulaci�n en nanosegundos.
     * @since 2.0
     */    
    public void setDuration(long d) {
        this.duracion = d;
    }
    
    /**
     * Este m�todo permite establecer la duraci�n del paso de simulaci�n.
     * @param p Duraci�n del paso de simulaci�n en nanosegundos.
     * @since 2.0
     */    
    public void setStep(long p) {
        this.paso = p;
    }
    
    /**
     * Este m�todo permite obtener la duraci�n total de la simulaci�n-
     * @return Duraci�n total de la simulaci�n, en nanosegundos.
     * @since 2.0
     */    
    public long obtenerDuracion() {
        return this.duracion;
    }
    
    /**
     * Este m�todo permite obtener la duraci�n del paso de simulaci�n.
     * @return Duraci�n del paso de simulaci�n en nanosegundos.
     * @since 2.0
     */    
    public long obtenerPaso() {
        return this.paso;
    }
    
    /**
     * Este m�todo serializa la instancia, convirti�ndola en un texto que se puede
     * almacenar en disco.
     * @return Un texto que representa a la instancia actual.
     * @since 2.0
     */    
    public String marshallTimeParameters() {
        String serializada = "#Temporizacion#";
        serializada += this.duracion+"#";
        serializada += this.paso+"#";
        return serializada;
    }

    /**
     * Este m�todo deserializa un objeto TSimulation previamente serializado,
 reconstruy�ndolo en memoria.
     * @param pt El objeto de tipo TSimulaci�n serializado.
     * @return TRUE, si se ha conseguido serializar correctamente. FALSE en caso contrario.
     * @since 2.0
     */    
    public boolean unmarshallTimeParameters(String pt) {
        String valores[] = pt.split("#");
        if (valores.length != 4) {
            return false;
        }
        this.duracion = Integer.valueOf(valores[2]).longValue();
        this.paso = Integer.valueOf(valores[3]).longValue();
        return true;
    }
    
    /**
     * Este m�todo permite acceder directamente al recolector de eventos de simulaci�n.
     * @return Recolector de eventos de simulaci�n el escenario.
     * @since 2.0
     */    
    public TSimulationCollector obtenerRecolector() {
        return recolector;
    }

    private long duracion;
    private long paso;
    
    private TScenario escenarioPadre;
    private TSimulationCollector recolector;
}
