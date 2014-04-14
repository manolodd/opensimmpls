//**************************************************************************
// Nombre......: TSimulacion.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un objeto que contiene todos los datos
// ............: referentes a la simulación del escenario actual.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import simMPLS.escenario.*;
import simMPLS.interfaz.simulador.*;
import simMPLS.electronica.reloj.*;
import simMPLS.electronica.recolectorsimulacion.*;

/**
 * Esta clase implementa un objeto que almacena los datos globales de la
 * simulación.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSimulacion {

    /**
     * Crea una nueva instancia de TEscenario
     * @param e Escenario al que pertenecen los datos de la simulación.
     * @since 1.0
     */
    public TSimulacion(TEscenario e) {
        escenarioPadre = e;
        recolector = new TRecolectorSimulacion();
        duracion = 500;
        paso = 1;
    }

    /**
     * Este método permite establecer el panel de simulación en el cual se realizará la
     * simulación.
     * @since 1.0
     * @param ps El panel de simulación.
     */    
    public void ponerPanelSimulacion(JPanelSimulacion ps) {
        recolector.ponerPanelSimulacion(ps);
    }
    
    /**
     * Este método reinicia los atributos de la clase dejándolos como recién iniciados
     * por el constructor.
     * @since 1.0
     */    
    public void reset() {
        recolector.reset();
    }
    
    /**
     * Este método permite establecer la duración total de la simulación.
     * @param d Duración total de la simulación en nanosegundos.
     * @since 1.0
     */    
    public void ponerDuracion(long d) {
        this.duracion = d;
    }
    
    /**
     * Este método permite establecer la duración del paso de simulación.
     * @param p Duración del paso de simulación en nanosegundos.
     * @since 1.0
     */    
    public void ponerPaso(long p) {
        this.paso = p;
    }
    
    /**
     * Este método permite obtener la duración total de la simulación-
     * @return Duración total de la simulación, en nanosegundos.
     * @since 1.0
     */    
    public long obtenerDuracion() {
        return this.duracion;
    }
    
    /**
     * Este método permite obtener la duración del paso de simulación.
     * @return Duración del paso de simulación en nanosegundos.
     * @since 1.0
     */    
    public long obtenerPaso() {
        return this.paso;
    }
    
    /**
     * Este método serializa la instancia, convirtiéndola en un texto que se puede
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
     * Este método deserializa un objeto TSimulacion previamente serializado,
     * reconstruyéndolo en memoria.
     * @param pt El objeto de tipo TSimulación serializado.
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
     * Este método permite acceder directamente al recolector de eventos de simulación.
     * @return Recolector de eventos de simulación el escenario.
     * @since 1.0
     */    
    public TRecolectorSimulacion obtenerRecolector() {
        return recolector;
    }

    private long duracion;
    private long paso;
    
    private TEscenario escenarioPadre;
    private TRecolectorSimulacion recolector;
}
