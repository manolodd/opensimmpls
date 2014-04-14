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
 * Esta clase implementa un conjunto de puertos de un nodo.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPuertosNodoNormal extends TPuertosNodo {

    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TPuertosNodoNormal.
     * @param num Numero de puertos que contendrá el conjunto e puertos.
     * @param n Referencia al nodo al que pertenece este conjunto de puertos.
     * @since 1.0
     */
    public TPuertosNodoNormal(int num, TNodoTopologia n) {
        super(num, n);
        puertos = new TPuertoNormal[num];
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            puertos[i] = new TPuertoNormal(this, i);
            puertos[i].ponerIdentificador(i);
        }
        puertoLeido = 0;
    }

    /**
     * Este método establece el conjunto de puertos como ideal, sin
     * restricciones de tamaño, ilimitado.
     * @param bi TRUE, indica que el buffer del conjunto de puertos es ilimitado. FALSE, indica que no es
     * ilimitado.
     * @since 1.0
     */    
    public void ponerBufferIlimitado(boolean bi) {
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            puertos[i].ponerBufferIlimitado(bi);
        }
    }
    
    /**
     * Este método devuelve el puerto cuyo identificador coincida con el especificado.
     * @param numPuerto Identificador del puerto que queremos obtener.
     * @return El puerto que deseamos obtener. NULL si el puerto con ese identificador no
     * existe.
     * @since 1.0
     */    
    public TPuerto obtenerPuerto(int numPuerto) {
        if (numPuerto < this.numPuertos)
            return puertos[numPuerto];
        return null;
    }

    /**
     * Este método establece el tamaño que tendrá el buffer del conjunto de puertos. Si
     * el conjunto de puertos está definido como ilimitado, este método no tiene efecto.
     * @param tamEnMB Tamaño en MB del buffer del conjunto de puertos.
     * @since 1.0
     */    
    public void ponerTamanioBuffer(int tamEnMB) {
        this.tamanioBufferCjtoPuertos = tamEnMB;
    }
    
    /**
     * Este método devuelve el tamaño del buffer del conjunto de puertos.
     * @return El tamaño del buffer del conjunto de puertos en MB.
     * @since 1.0
     */    
    public int obtenerTamanioBuffer() {
        return this.tamanioBufferCjtoPuertos;
    }
    
    /**
     * Este método comprueba si un puerto concreto del conjunto de puertos está libre o
     * si por el contrario está conectado.
     * @param p Identificador del puerto que queremos consultar.
     * @return TRUE, indica que el puerto está sin conectar. FALSE indica que el puerto ya está
     * conectado a un enlace.
     * @since 1.0
     */    
    public boolean estaLibre(int p) {
        if (p < this.numPuertos)
            return puertos[p].estaLibre();
        return false;
    }

    /**
     * Este método comprueba si hay algún puerto libre, es decir, sin conectar a un
     * enelace, en el conjunto de puertos.
     * @return TRUE, si al menos uno de los puertos del conjunto de puertos está sin conectar.
     * FALSE si todos están conectado ya a un enlace.
     * @since 1.0
     */    
    public boolean hayPuertosLibres() {
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            if (puertos[i].estaLibre())
                return true;
        }
        return false;
    }

    /**
     * Este método toma un enlace y lo conecta a un puerto concreto del conjunto de
     * puertos.
     * @param e Enlace que queremos conectar.
     * @param p Identificador del puerto del conjunto de puertos al que se conectará el enlace.
     * @since 1.0
     */    
    public void ponerEnlaceEnPuerto(TEnlaceTopologia e, int p) {
        if (p < this.numPuertos)
            if (puertos[p].estaLibre())
                puertos[p].ponerEnlace(e);
    } 

    /**
     * Este método devuelve el enlace al que está conectado un puerto del conjunto de
     * puertos.
     * @param p Identificador de puerto de uno de los puertos del conjunto.
     * @return Enlace al que está conectado el puerto especificado. NULL si el puerto está
     * libre.
     * @since 1.0
     */    
    public TEnlaceTopologia obtenerEnlaceDePuerto(int p) {
        if (p < this.numPuertos)
            if (!puertos[p].estaLibre())
                return puertos[p].obtenerEnlace();
        return null;
    } 

    /**
     * Este método desconecta un enlace de un puerto concreto, dejándolo libre.
     * @param p El identificador del puerto del conjunto de puertos, que queremos desconectar y
     * dejar libre.
     * @since 1.0
     */    
    public void quitarEnlaceDePuerto(int p) {
        if ((p >= 0) && (p < this.numPuertos))
            puertos[p].quitarEnlace();
    } 

    /**
     * Este método lee y devuelve un paquete de uno de lo puertos. El que toque.
     * @return Un paquete leído de uno de los puertos del conjunto de puertos.
     * @since 1.0
     */    
    public TPDU leerPaquete() {
        for (int i=0; i<numPuertos; i++) {
            puertoLeido = (puertoLeido + 1) % numPuertos;
            if (puertos[puertoLeido].hayPaqueteEsperando()) {
                return puertos[puertoLeido].obtenerPaquete();
            }
        }
        return null;
    }

    /**
     * Este método comprueba si hay o no paquetes esperando en el buffer de recepción.
     * @return TRUE, si hay paquetes en el buffer de recepción. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean hayPaquetesQueConmutar() {
        for (int i=0; i<numPuertos; i++) {
            if (puertos[i].hayPaqueteEsperando()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Este método comprueba si hay o no paquetes esperando en el buffer de recepción.
     * @return TRUE, si hay paquetes en el buffer de recepción. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean hayPaquetesQueEncaminar() {
        return hayPaquetesQueConmutar();
    }
    
    /**
     * Este método comprueba si se puede conmutar el siguiente paquete del conjunto de
     * puertos, teniendo como referencia el número máximo de octetos que el nodo puede
     * conmutar en ese instante.
     * @param octetos El número de octetos que el nodo puede conmutar en ese instante.
     * @return TRUE, si puedo conmutar un nuevo paquete. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean puedoConmutarPaquete(int octetos) {
        int puertosSinPaquete = 0;
        while (puertosSinPaquete < this.numPuertos) {
            if (puertos[((puertoLeido + 1) % numPuertos)].hayPaqueteEsperando()) {
                return puertos[((puertoLeido + 1) % numPuertos)].puedoConmutarPaquete(octetos);
            } else {
                puertosSinPaquete++;
                this.saltarPuerto();
            }
        }
        return false;
    }
    
    /**
     * Este método salta un puerto que tocaba ser leído y lee el siguiente.
     * @since 1.0
     */    
    public void saltarPuerto() {
        puertoLeido = (puertoLeido + 1) % numPuertos;
    }
    
    /**
     * Este método devuelve el idetificador del puerto del que se leyó el último
     * paquete.
     * @return El identificador del último puerto leído.
     * @since 1.0
     */    
    public int obtenerPuertoLeido() {
        return puertoLeido;
    }

    /**
     * Este método toma una dirección IP y devuelve el puerto del conjunto de puertos,
     * en cuyo extremo está el nodo con dicha IP.
     * @param ip IP a la que se desea acceder.
     * @return El puerto que conecta al nodo cuya IP es la especificada. NULL en caso de que no
     * hay conexión directa con dicho nodo/IP.
     * @since 1.0
     */    
    public TPuerto obtenerPuertoDestino(String ip) {
        for (int i=0; i<numPuertos; i++) {
            if (!puertos[i].estaLibre()) {
                int destino = puertos[i].obtenerEnlace().obtenerDestinoLocal(nodo);
                if (destino == TEnlaceTopologia.EXTREMO1) {
                    if (puertos[i].obtenerEnlace().obtenerExtremo1().obtenerIP().equals(ip)) {
                        return puertos[i];
                    }
                } else {
                    if (puertos[i].obtenerEnlace().obtenerExtremo2().obtenerIP().equals(ip)) {
                        return puertos[i];
                    }
                }
            }
        }
        return null;
    }

    /**
     * Este método consulta un puerto para obtener la IP del nodo que se encuentra en
     * el otro extremo.
     * @param p Identificado de un puerto del conjunto de puertos.
     * @return IP del nodo destino al que está unido el puerto especificado. NULL si el puerto
     * está libre.
     * @since 1.0
     */    
    public String obtenerIPDestinoDelPuerto(int p) {
        if ((p >= 0) && (p < this.numPuertos)) {
            if (!puertos[p].estaLibre()) {
                String IP2 = puertos[p].obtenerEnlace().obtenerExtremo2().obtenerIP();
                if (puertos[p].obtenerEnlace().obtenerExtremo1().obtenerIP().equals(nodo.obtenerIP()))
                    return puertos[p].obtenerEnlace().obtenerExtremo2().obtenerIP();
                return puertos[p].obtenerEnlace().obtenerExtremo1().obtenerIP();
            }
        }
        return null;
    }

    /**
     * Este método calcula la congestión global del conjunto de puertos, en forma de
     * porcentaje.
     * @return Porcentaje (0%-100%) de congestión del conjunto de puertos.
     * @since 1.0
     */    
    public long obtenerCongestion() {
        long cong = 0;
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            if (puertos[i].obtenerCongestion() > cong)
                cong = puertos[i].obtenerCongestion();
        }
        return cong;
    }
    
    /**
     * Este método reinicia el valor de todos los atributos de la clase, como si
     * acabasen de ser creados en el constructor.
     * @since 1.0
     */    
    public void reset(){
        this.cerrojoCjtoPuertos.liberar();
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            puertos[i].reset();
        }
        this.puertoLeido = 0;
        this.ponerTamanioOcupadoCjtoPuertos(0);
        this.saturadoArtificialmente = false;
        this.verdaderaOcupacion = 0;
        this.cerrojoCjtoPuertos.liberar();
    }
    
    /**
     * Este método permite establecer el nivel de saturación del nodo en el 97% de tal
     * forma que rápidamente comience a descartar paquetes. Lo hace de forma artificial
     * y es capaz de volver al estado original en cualquier momento.
     * @param sa TRUE indica que el puerto se debe saturar artificialmente. FALSE indica lo
     * contrario.
     * @since 1.0
     */    
    public void ponerSaturadoArtificialmente(boolean sa) {
        long calculo97PorCiento = (long) (this.obtenerTamanioBuffer()*1017118.72);
        if (sa) {
            if (!this.saturadoArtificialmente) {
                if (this.obtenerTamanioOcupadoCjtoPuertos() < calculo97PorCiento) {
                    this.saturadoArtificialmente = true;
                    this.verdaderaOcupacion = this.obtenerTamanioOcupadoCjtoPuertos();
                    this.ponerTamanioOcupadoCjtoPuertos(calculo97PorCiento);
                }
            }
        } else {
            if (this.saturadoArtificialmente) {
                this.verdaderaOcupacion += (obtenerTamanioOcupadoCjtoPuertos() - calculo97PorCiento);
                if (this.verdaderaOcupacion < 0)
                    this.verdaderaOcupacion = 0;
                this.ponerTamanioOcupadoCjtoPuertos(this.verdaderaOcupacion);
                this.saturadoArtificialmente = false;
                this.verdaderaOcupacion = 0;
            }
        }
    }
    
    private TPuerto[] puertos;
    private int puertoLeido;
}
