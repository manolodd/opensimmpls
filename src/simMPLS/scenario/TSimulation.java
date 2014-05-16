/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
     * @since 1.0
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
     * @since 1.0
     * @param ps El panel de simulaci�n.
     */    
    public void ponerPanelSimulacion(JSimulationPanel ps) {
        recolector.setSimulationPanel(ps);
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase dej�ndolos como reci�n iniciados
     * por el constructor.
     * @since 1.0
     */    
    public void reset() {
        recolector.reset();
    }
    
    /**
     * Este m�todo permite establecer la duraci�n total de la simulaci�n.
     * @param d Duraci�n total de la simulaci�n en nanosegundos.
     * @since 1.0
     */    
    public void ponerDuracion(long d) {
        this.duracion = d;
    }
    
    /**
     * Este m�todo permite establecer la duraci�n del paso de simulaci�n.
     * @param p Duraci�n del paso de simulaci�n en nanosegundos.
     * @since 1.0
     */    
    public void ponerPaso(long p) {
        this.paso = p;
    }
    
    /**
     * Este m�todo permite obtener la duraci�n total de la simulaci�n-
     * @return Duraci�n total de la simulaci�n, en nanosegundos.
     * @since 1.0
     */    
    public long obtenerDuracion() {
        return this.duracion;
    }
    
    /**
     * Este m�todo permite obtener la duraci�n del paso de simulaci�n.
     * @return Duraci�n del paso de simulaci�n en nanosegundos.
     * @since 1.0
     */    
    public long obtenerPaso() {
        return this.paso;
    }
    
    /**
     * Este m�todo serializa la instancia, convirti�ndola en un texto que se puede
     * almacenar en disco.
     * @return Un texto que representa a la instancia actual.
     * @since 1.0
     */    
    public String serializarParametrosTemporales() {
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
     * @since 1.0
     */    
    public boolean deserializarParametrosTemporales(String pt) {
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
     * @since 1.0
     */    
    public TSimulationCollector obtenerRecolector() {
        return recolector;
    }

    private long duracion;
    private long paso;
    
    private TScenario escenarioPadre;
    private TSimulationCollector recolector;
}
