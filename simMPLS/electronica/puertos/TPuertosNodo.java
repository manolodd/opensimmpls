//**************************************************************************
// Nombre......: TPuertosNodo.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa el conjunto de puertos de un nodo de
// ............: la topología.
// Fecha.......: 06/03/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.puertos;

import simMPLS.escenario.*;
import simMPLS.protocolo.*;
import simMPLS.utiles.*;

/**
 * Esta clase abstracta es la superclase del conjunto de puertos que hay en un nodo.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public abstract class TPuertosNodo {

    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TPuertosNodo.
     * @param num Numero de puertos que contendrá el conjunto e puertos.
     * @param n Referencia al nodo al que pertenece este conjunto de puertos.
     * @since 1.0
     */
    public TPuertosNodo(int num, TNodoTopologia n) {
        this.numPuertos = num;
        nodo = n;
        tamanioBufferCjtoPuertos = 1;
        tamanioOcupadoCjtoPuertos = 0;
        this.cerrojoCjtoPuertos = new TMonitor();
        saturadoArtificialmente = false;
        verdaderaOcupacion = 0;
    }

    /**
     * Este método incrementa la cantidad de buffer que está ocupada.
     * @param tcp Tamaño (en octetos) en que se debe incrementar la ocupación total.
     * @since 1.0
     */    
    public synchronized void incrementarTamanioOcupadoCjtoPuertos(long tcp) {
        this.tamanioOcupadoCjtoPuertos += tcp;
    }
    
    /**
     * Este método decrementa la cantidad de buffer que está ocupada.
     * @param tcp Tamaño (en octetos) en que se debe decrementar la ocupación total.
     * @since 1.0
     */    
    public synchronized void decrementarTamanioOcupadoCjtoPuertos(long tcp) {
        this.tamanioOcupadoCjtoPuertos -= tcp;
    }

    /**
     * Este método establece la cantidad de buffer que está ocupada.
     * @param tcp Tamaño (en octetos) del buffer que está ocupado.
     * @since 1.0
     */    
    public synchronized void ponerTamanioOcupadoCjtoPuertos(long tcp) {
        this.tamanioOcupadoCjtoPuertos = tcp;
    }
    
    /**
     * Este método obtiene el tamaño de buffer que está ocupado actualmente.
     * @return Tamaño (en octetos) del buffer que está ocupado.
     * @since 1.0
     */    
    public synchronized long obtenerTamanioOcupadoCjtoPuertos() {
        return this.tamanioOcupadoCjtoPuertos;
    }
    
    /**
     * Este método obtiene si el nodo está saturado artificialmente o no.
     * @since 1.0
     * @return TRUE, si el nodo está saturado artificialmente. FALSE en caso contrario.
     */    
    public boolean obtenerSaturadoArtificialmente() {
        return this.saturadoArtificialmente;
    }
    
    /**
     * Este método es abstracto. Saturará o no artificialmente el puerto.
     * @since 1.0
     * @param sa TRUE, si el nodo se saturará artificialmente. FALSE en caso contrario.
     */    
    public abstract void ponerSaturadoArtificialmente(boolean sa);

    /**
     * Este método devuelve el número de puerto que tiene el conjunto de puertos.
     * @return El número de puertos del conjunto de puertos.
     * @since 1.0
     */    
    public int obtenerNumeroDePuertos() {
        return numPuertos;
    }

    /**
     * Este método establece qué nodo es el propietario de este conjunto de puertos.
     * @param n El nodo poseedor de este conjunto de puertos.
     * @since 1.0
     */    
    public void ponerNodo(TNodoTopologia n) {
        nodo = n;
    }

    /**
     * Este método devuelve una referencia al nodo que es poseedor de este conjunto de
     * puertos.
     * @return El nodo que posee este conjunto de puertos.
     * @since 1.0
     */    
    public TNodoTopologia obtenerNodo() {
        return nodo;
    }

    /**
     * Este método lee y devuelve un paquete de uno de lo puertos. El que toque.
     * @return Un paquete leído de uno de los puertos del conjunto de puertos.
     * @since 1.0
     */    
    public abstract TPDU leerPaquete();
    /**
     * Este método comprueba si hay o no paquetes esperando en el buffer de recepción.
     * @return TRUE, si hay paquetes en el buffer de recepción. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean hayPaquetesQueConmutar();
    /**
     * Este método comprueba si hay o no paquetes esperando en el buffer de recepción.
     * @return TRUE, si hay paquetes en el buffer de recepción. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean hayPaquetesQueEncaminar();
    /**
     * Este método comprueba si se puede conmutar el siguiente paquete del conjunto de
     * puertos, teniendo como referencia el número máximo de octetos que el nodo puede
     * conmutar en ese instante.
     * @param octetos El número de octetos que el nodo puede conmutar en ese instante.
     * @return TRUE, si puedo conmutar un nuevo paquete. FALSE en caso contrario.
     * @since 1.0
     */    
    public abstract boolean puedoConmutarPaquete(int octetos);
    /**
     * Este método salta un puerto que tocaba ser leído y lee el siguiente.
     * @since 1.0
     */    
    public abstract void saltarPuerto();
    /**
     * Este método devuelve el idetificador del puerto del que se leyó el último
     * paquete.
     * @return El identificador del último puerto leído.
     * @since 1.0
     */    
    public abstract int obtenerPuertoLeido();
    /**
     * Este método toma una dirección IP y devuelve el puerto del conjunto de puertos,
     * en cuyo extremo está el nodo con dicha IP.
     * @param ip IP a la que se desea acceder.
     * @return El puerto que conecta al nodo cuya IP es la especificada. NULL en caso de que no
     * hay conexión directa con dicho nodo/IP.
     * @since 1.0
     */    
    public abstract TPuerto obtenerPuertoDestino(String ip);
    /**
     * Este método consulta un puerto para obtener la IP del nodo que se encuentra en
     * el otro extremo.
     * @param p Identificado de un puerto del conjunto de puertos.
     * @return IP del nodo destino al que está unido el puerto especificado. NULL si el puerto
     * está libre.
     * @since 1.0
     */    
    public abstract String obtenerIPDestinoDelPuerto(int p);
    /**
     * Este método calcula la congestión global del conjunto de puertos, en forma de
     * porcentaje.
     * @return Porcentaje (0%-100%) de congestión del conjunto de puertos.
     * @since 1.0
     */    
    public abstract long obtenerCongestion();
    /**
     * Este método reinicia el valor de todos los atributos de la clase, como si
     * acabasen de ser creados en el constructor.
     * @since 1.0
     */    
    public abstract void reset();
    /**
     * Este método toma un enlace y lo conecta a un puerto concreto del conjunto de
     * puertos.
     * @param e Enlace que queremos conectar.
     * @param p Identificador del puerto del conjunto de puertos al que se conectará el enlace.
     * @since 1.0
     */    
    public abstract void ponerEnlaceEnPuerto(TEnlaceTopologia e, int p);
    /**
     * Este método devuelve el enlace al que está conectado un puerto del conjunto de
     * puertos.
     * @param p Identificador de puerto de uno de los puertos del conjunto.
     * @return Enlace al que está conectado el puerto especificado. NULL si el puerto está
     * libre.
     * @since 1.0
     */    
    public abstract TEnlaceTopologia obtenerEnlaceDePuerto(int p);
    /**
     * Este método desconecta un enlace de un puerto concreto, dejándolo libre.
     * @param p El identificador del puerto del conjunto de puertos, que queremos desconectar y
     * dejar libre.
     * @since 1.0
     */    
    public abstract void quitarEnlaceDePuerto(int p);
    /**
     * Este método establece el conjunto de puertos como ideal, sin
     * restricciones de tamaño, ilimitado.
     * @param bi TRUE, indica que el buffer del conjunto de puertos es ilimitado. FALSE, indica que no es
     * ilimitado.
     * @since 1.0
     */    
    public abstract void ponerBufferIlimitado(boolean bi);
    /**
     * Este método devuelve el puerto cuyo identificador coincida con el especificado.
     * @param numPuerto Identificador del puerto que queremos obtener.
     * @return El puerto que deseamos obtener. NULL si el puerto con ese identificador no
     * existe.
     * @since 1.0
     */    
    public abstract TPuerto obtenerPuerto(int numPuerto);
    /**
     * Este método establece el tamaño que tendrá el buffer del conjunto de puertos. Si
     * el conjunto de puertos está definido como ilimitado, este método no tiene efecto.
     * @param tamEnMB Tamaño en MB del buffer del conjunto de puertos.
     * @since 1.0
     */    
    public abstract void ponerTamanioBuffer(int tamEnMB);
    /**
     * Este método devuelve el tamaño del buffer del conjunto de puertos.
     * @return El tamaño del buffer del conjunto de puertos en MB.
     * @since 1.0
     */    
    public abstract int obtenerTamanioBuffer();
    /**
     * Este método comprueba si un puerto concreto del conjunto de puertos está libre o
     * si por el contrario está conectado.
     * @param p Identificador del puerto que queremos consultar.
     * @return TRUE, indica que el puerto está sin conectar. FALSE indica que el puerto ya está
     * conectado a un enlace.
     * @since 1.0
     */    
    public abstract boolean estaLibre(int p);
    /**
     * Este método comprueba si hay algún puerto libre, es decir, sin conectar a un
     * enelace, en el conjunto de puertos.
     * @return TRUE, si al menos uno de los puertos del conjunto de puertos está sin conectar.
     * FALSE si todos están conectado ya a un enlace.
     * @since 1.0
     */    
    public abstract boolean hayPuertosLibres();
    
    /**
     * Este atributo de la clase especifica el número de puertos que hay en el conjunto
     * de puertos.
     * @since 1.0
     */    
    protected int numPuertos;
    /**
     * Este atributo es una referencia al nodo que es dueño de este conjunto de
     * puertos.
     * @since 1.0
     */    
    protected TNodoTopologia nodo;
    /**
     * Este atributo indica el tamaño en MB del búffer existente para el conjunto de
     * puertos.
     * @since 1.0
     */    
    protected int tamanioBufferCjtoPuertos;
    /**
     * Este atributo almacena el tamaño del buffer del conjunto de puertos que
     * actualmente está ocupado. En octetos.
     * @since 1.0
     */    
    private long tamanioOcupadoCjtoPuertos;
    /**
     * Este atributo es un Monitor genérico que se usa para crear regiones críticas que
     * sirvan para sincronizar accesos concurrentes.
     * @since 1.0
     */    
    public TMonitor cerrojoCjtoPuertos;
    /**
     * Este atributo almacena el estado de saturación artificial o no no saturación
     * artificial del puerto.
     * @since 1.0
     */    
    protected boolean saturadoArtificialmente;
    /**
     * Este atributo almacena la verdadera ocupación del nodo. Así se permite restaurar
     * este valor tras una congestión artificial.
     * @since 1.0
     */    
    protected long verdaderaOcupacion;
}
