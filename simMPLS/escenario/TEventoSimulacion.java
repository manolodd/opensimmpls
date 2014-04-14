//**************************************************************************
// Nombre......: TEventoSimulacion.java
// Proyecto....: Open SimMPLS
// Descripción.: Superclase que implementa un evento de la simulación reali-
// ............: zada. Todos los eventos de simulación deben heredar de él 
// ............: para poder usar el polimorfismo.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import simMPLS.utiles.*;

/**
 * Esta clase es la superclase de todos los eventos de simulación del simulador.
 * Todos los eventos e simulación deben heredar e implementar los métodos
 * abstractos de esta clase.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TEventoSimulacion extends TEventoSimMPLS {

    /**
     * Crea una nueva instancia de TEventoSimulacion
     * @since 1.0
     * @param inst Instante de tiempo en el que se produjo el evento.
     * @param emisor Elemento de la topología que produjo el evento.
     * @param id Identificador unico para el evento.
     */
    public TEventoSimulacion(Object emisor, long id, long inst) {
        super(emisor, id, inst);
    }

    /**
     * Este método permite saber el tipo de evento de que se trata.
     * @return TEventoSimMPLS.SIMULACION, indicando que se trata de un evento de simulación.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return super.SIMULACION;
    }

    /**
     * Este método permite saber el tipo de evento de simulación de que se trata.
     * @return Tipo del evento de simulación. Una de las constantes de la clase.
     * @since 1.0
     */    
    public abstract int obtenerSubtipo();

    /**
     * Este método permite obtener el elemento de la topología que generó el evento.
     * @return Elemento de la topologia que generó el evento.
     * @since 1.0
     */    
    public TElementoTopologia obtenerFuente() {  
        return (TElementoTopologia) super.source;
    }
    
    /**
     * Constante que identifica a un evento de generación de paquete.
     * @since 1.0
     */    
    public static final int PAQUETE_GENERADO =              0;
    /**
     * Constante que identifica a un evento de  envío de paquete.
     * @since 1.0
     */    
    public static final int PAQUETE_ENVIADO =               1;
    /**
     * Constante que identifica a un evento de recepción de paquete.
     * @since 1.0
     */    
    public static final int PAQUETE_RECIBIDO =              2;
    /**
     * Constante que identifica a un evento de conmutación de paquete.
     * @since 1.0
     */    
    public static final int PAQUETE_CONMUTADO =             3;
    /**
     * Constante que identifica a un evento de descarte de paquete.
     * @since 1.0
     */    
    public static final int PAQUETE_DESCARTADO =            4;
    /**
     * Constante que identifica a un evento de paquete en tránsito.
     * @since 1.0
     */    
    public static final int PAQUETE_EN_TRANSITO =           5;
    /**
     * Constante que identifica a un evento de paquete almacenado en la DMGP
     * @since 1.0
     */    
    public static final int PAQUETE_ALMACENADO_DMGP =       6;
    /**
     * Constante que identifica a un evento de paquete eliminado de la DMGP
     * @since 1.0
     */    
    public static final int PAQUETE_ELIMINADO_DMGP =        7;
    /**
     * Constante que identifica a un evento de paquete hallado en la DMGP
     * @since 1.0
     */    
    public static final int PAQUETE_ENCONTRADO_DMGP =       8;
    /**
     * Constante que identifica a un evento de paquete no encontrado en la DMGP
     * @since 1.0
     */    
    public static final int PAQUETE_NO_ENCONTRADO_DMGP =    9;
    /**
     * Constante que identifica a un evento de paquete retransmitido
     * @since 1.0
     */    
    public static final int PAQUETE_RETRANSMITIDO =         10;

    /**
     * Constante que identifica a un evento de enlace sobrecargado
     * @since 1.0
     */    
    public static final int ENLACE_SOBRECARGADO =          11;
    /**
     * Constante que identifica a un evento de enlace caído
     * @since 1.0
     */    
    public static final int ENLACE_CAIDO =                 12;
    /**
     * Constante que identifica a un evento de enlace recuperado
     * @since 1.0
     */    
    public static final int ENLACE_RECUPERADO =            13;
    
    /**
     * Constante que identifica a un evento de enlace congestionado
     * @since 1.0
     */    
    public static final int NODO_CONGESTIONADO =           14;

    /**
     * Constante que identifica a un evento de etiqueta solicitada
     * @since 1.0
     */    
    public static final int ETIQUETA_SOLICITADA =          15;
    /**
     * Constante que identifica a un evento de etiqueta recibida
     * @since 1.0
     */    
    public static final int ETIQUETA_RECIBIDA =            16;
    /**
     * Constante que identifica a un evento de etiqueta asignada
     * @since 1.0
     */    
    public static final int ETIQUETA_ASIGNADA =            17;
    /**
     * Constante que identifica a un evento de etiqueta denegada
     * @since 1.0
     */    
    public static final int ETIQUETA_DENEGADA =            18;
    /**
     * Constante que identifica a un evento de etiqueta eliminada
     * @since 1.0
     */    
    public static final int ETIQUETA_ELIMINADA =           19;

    /**
     * Constante que identifica a un evento de LSP establecido
     * @since 1.0
     */    
    public static final int LSP_ESTABLECIDO =              20;
    /**
     * Constante que identifica a un evento de LSP no establecido
     * @since 1.0
     */    
    public static final int LSP_NO_ESTABLECIDO =           21;
    /**
     * Constante que identifica a un evento de LSP eliminado
     * @since 1.0
     */    
    public static final int LSP_ELIMINADO =                22;
    /**
     * Constante que identifica a un evento de LSP de respaldo establecido
     * @since 1.0
     */    
    public static final int LSP_BACKUP_ESTABLECIDO =       23;
    /**
     * Constante que identifica a un evento de LSP de respaldo no establecido
     * @since 1.0
     */    
    public static final int LSP_BACKUP_NO_ESTABLECIDO =    24;
    /**
     * Constante que identifica a un evento de LSP de respaldo eliminado
     * @since 1.0
     */    
    public static final int LSP_BACKUP_ELIMINADO =         25;
    /**
     * Constante que identifica a un evento de LSP de respaldo activado
     * @since 1.0
     */    
    public static final int LSP_BACKUP_ACTIVADO =          26;

    /**
     * Constante que identifica a un evento de retransmisión solicitada
     * @since 1.0
     */    
    public static final int RETRANSMISION_SOLICITADA =     26;
    /**
     * Constante que identifica a un evento de retransmisión afirmativa
     * @since 1.0
     */    
    public static final int RETRANSMISION_AFIRMATIVA =     27;
    /**
     * Constante que identifica a un evento de retransmisión negativa
     * @since 1.0
     */    
    public static final int RETRANSMISION_NEGATIVA =       28;
    /**
     * Constante que identifica a un evento de retransmisión recibida
     * @since 1.0
     */    
    public static final int RETRANSMISION_RECIBIDA =       29;
    /**
     * Constante que identifica a un evento de paquete encaminado
     * @since 1.0
     */    
    public static final int PAQUETE_ENCAMINADO =           30;
}
