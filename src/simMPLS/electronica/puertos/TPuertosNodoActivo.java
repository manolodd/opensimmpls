//**************************************************************************
// Nombre......: TPuertosNodo.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa el conjunto de puertos de un nodo de
// ............: la topolog�a.
// Fecha.......: 06/03/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.puertos;

import simMPLS.escenario.TEnlaceTopologia;
import simMPLS.escenario.TNodoTopologia;
import simMPLS.protocolo.TPDU;


/**
 * Esta clase implementa un conjunto de puertos activos de un nodo.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPuertosNodoActivo extends TPuertosNodo {

    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TPuertosNodoActivo.
     * @param num Numero de puertos que contendr� el conjunto e puertos.
     * @param n Referencia al nodo al que pertenece este conjunto de puertos.
     * @since 1.0
     */
    public TPuertosNodoActivo(int num, TNodoTopologia n) {
        super(num, n);
        puertos = new TPuertoActivo[num];
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            puertos[i] = new TPuertoActivo(this, i);
            puertos[i].ponerIdentificador(i);
        }
        puertoLeido = 0;
        siguientePaqueteALeer = null;
        ratioPorPrioridad = new int[11];
        actualPorPrioridad = new int[11];
        for (i=0; i<11; i++) {
            ratioPorPrioridad[i] = i+1;
            actualPorPrioridad[i] = 0;
        }
        prioridadActual = 0;
    }
    
    private void obtenerSiguientePaquete() {
        if (this.siguientePaqueteALeer == null) {
            int contadorPrioridad = 0;
            int contadorPuertos = 0;
            boolean fin = false;
            int prioridadAux = -1;
            int prioridadActualAux = 0;
            int puertoLeidoAux = 0;
            while ((contadorPrioridad < 11) && (!fin)) {
                prioridadActualAux = (prioridadActual+contadorPrioridad) % 11;
                if (actualPorPrioridad[prioridadActualAux] < ratioPorPrioridad[prioridadActualAux]) {
                    while ((contadorPuertos < numPuertos) && (!fin)) {
                        puertoLeidoAux = (puertoLeido+contadorPuertos) % numPuertos;
                        if (puertos[puertoLeidoAux].hayPaqueteEsperando()) {
                            prioridadAux = ((TPuertoActivo)puertos[puertoLeidoAux]).obtenerPrioridadSiguientePaquete();
                            if (prioridadAux == prioridadActualAux) {
                                puertoLeido = puertoLeidoAux;
                                prioridadActual = prioridadActualAux;
                                siguientePaqueteALeer = puertos[puertoLeidoAux].obtenerPaquete();
                                fin = true;
                                actualPorPrioridad[prioridadActualAux]++;
                            }
                        }
                        contadorPuertos++;
                    }
                    if (!fin) {
                        actualPorPrioridad[prioridadActualAux] = ratioPorPrioridad[prioridadActualAux];
                    }
                }
                if (!fin) {
                    contadorPrioridad++;
                }
                contadorPuertos = 0;
            }
            limpiarPrioridades();
        }
    }

    private void limpiarPrioridades() {
        int i=0;
        boolean limpiar = true;
        for (i=0; i<11; i++) {
            if (actualPorPrioridad[i] < ratioPorPrioridad[i]) {
                limpiar = false;
            }
        }
        if (limpiar) {
            for (i=0; i<11; i++) {
                actualPorPrioridad[i] = 0;
            }
        }
    }
    
    /**
     * Este m�todo establece el conjunto de puertos como ideal, sin
     * restricciones de tama�o, ilimitado.
     * @param bi TRUE, indica que el buffer del conjunto de puertos es ilimitado. FALSE, indica que no es
     * ilimitado.
     * @since 1.0
     */    
    @Override
    public void ponerBufferIlimitado(boolean bi) {
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            puertos[i].ponerBufferIlimitado(bi);
        }
    }
    
    /**
     * Este m�todo devuelve el puerto cuyo identificador coincida con el especificado.
     * @param numPuerto Identificador del puerto que queremos obtener.
     * @return El puerto que deseamos obtener. NULL si el puerto con ese identificador no
     * existe.
     * @since 1.0
     */    
    @Override
    public TPuerto obtenerPuerto(int numPuerto) {
        if (numPuerto < this.numPuertos)
            return puertos[numPuerto];
        return null;
    }

    /**
     * Este m�todo establece el tama�o que tendr� el buffer del conjunto de puertos. Si
     * el conjunto de puertos est� definido como ilimitado, este m�todo no tiene efecto.
     * @param tamEnMB Tama�o en MB del buffer del conjunto de puertos.
     * @since 1.0
     */    
    @Override
    public void ponerTamanioBuffer(int tamEnMB) {
        this.tamanioBufferCjtoPuertos = tamEnMB;
    }
    
    /**
     * Este m�todo devuelve el tama�o del buffer del conjunto de puertos.
     * @return El tama�o del buffer del conjunto de puertos en MB.
     * @since 1.0
     */    
    @Override
    public int obtenerTamanioBuffer() {
        return this.tamanioBufferCjtoPuertos;
    }
    
    /**
     * Este m�todo comprueba si un puerto concreto del conjunto de puertos est� libre o
     * si por el contrario est� conectado.
     * @param p Identificador del puerto que queremos consultar.
     * @return TRUE, indica que el puerto est� sin conectar. FALSE indica que el puerto ya est�
     * conectado a un enlace.
     * @since 1.0
     */    
    @Override
    public boolean estaLibre(int p) {
        if (p < this.numPuertos)
            return puertos[p].estaLibre();
        return false;
    }

    /**
     * Este m�todo comprueba si hay alg�n puerto libre, es decir, sin conectar a un
     * enelace, en el conjunto de puertos.
     * @return TRUE, si al menos uno de los puertos del conjunto de puertos est� sin conectar.
     * FALSE si todos est�n conectado ya a un enlace.
     * @since 1.0
     */    
    @Override
    public boolean hayPuertosLibres() {
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            if (puertos[i].estaLibre())
                return true;
        }
        return false;
    }

    /**
     * Este m�todo toma un enlace y lo conecta a un puerto concreto del conjunto de
     * puertos.
     * @param e Enlace que queremos conectar.
     * @param p Identificador del puerto del conjunto de puertos al que se conectar� el enlace.
     * @since 1.0
     */    
    @Override
    public void ponerEnlaceEnPuerto(TEnlaceTopologia e, int p) {
        if (p < this.numPuertos)
            if (puertos[p].estaLibre())
                puertos[p].ponerEnlace(e);
    } 

    /**
     * Este m�todo devuelve el enlace al que est� conectado un puerto del conjunto de
     * puertos.
     * @param p Identificador de puerto de uno de los puertos del conjunto.
     * @return Enlace al que est� conectado el puerto especificado. NULL si el puerto est�
     * libre.
     * @since 1.0
     */    
    @Override
    public TEnlaceTopologia obtenerEnlaceDePuerto(int p) {
        if (p < this.numPuertos)
            if (!puertos[p].estaLibre())
                return puertos[p].obtenerEnlace();
        return null;
    } 

    /**
     * Este m�todo desconecta un enlace de un puerto concreto, dej�ndolo libre.
     * @param p El identificador del puerto del conjunto de puertos, que queremos desconectar y
     * dejar libre.
     * @since 1.0
     */    
    @Override
    public void quitarEnlaceDePuerto(int p) {
        if ((p >= 0) && (p < this.numPuertos))
            puertos[p].quitarEnlace();
    } 

    /**
     * Este m�todo lee y devuelve un paquete de uno de lo puertos. El que toque.
     * @return Un paquete le�do de uno de los puertos del conjunto de puertos.
     * @since 1.0
     */    
    @Override
    public TPDU leerPaquete() {
/*
        for (int i=0; i<numPuertos; i++) {
            puertoLeido = (puertoLeido + 1) % numPuertos;
            if (puertos[puertoLeido].hayPaqueteEsperando()) {
                return puertos[puertoLeido].obtenerPaquete();
            }
        }
        return null;
 */
        TPDU paqueteAux = null;
        this.obtenerSiguientePaquete();
        paqueteAux = this.siguientePaqueteALeer;
        this.siguientePaqueteALeer = null;
        return paqueteAux;
    }

    /**
     * Este m�todo comprueba si hay o no paquetes esperando en el buffer de recepci�n.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean hayPaquetesQueConmutar() {
        for (int i=0; i<numPuertos; i++) {
            if (puertos[i].hayPaqueteEsperando()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Este m�todo comprueba si hay o no paquetes esperando en el buffer de recepci�n.
     * @return TRUE, si hay paquetes en el buffer de recepci�n. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean hayPaquetesQueEncaminar() {
        return hayPaquetesQueConmutar();
    }
    
    /**
     * Este m�todo comprueba si se puede conmutar el siguiente paquete del conjunto de
     * puertos, teniendo como referencia el n�mero m�ximo de octetos que el nodo puede
     * conmutar en ese instante.
     * @param octetos El n�mero de octetos que el nodo puede conmutar en ese instante.
     * @return TRUE, si puedo conmutar un nuevo paquete. FALSE en caso contrario.
     * @since 1.0
     */    
    @Override
    public boolean puedoConmutarPaquete(int octetos) {
        TPDU paqueteAux = null;
        this.obtenerSiguientePaquete();
        paqueteAux = this.siguientePaqueteALeer;
        if (paqueteAux != null) {
            if (paqueteAux.obtenerTamanio() <= octetos) {
                return true;
            }
        }
        return false;
/*
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
*/
 }
    
    /**
     * Este m�todo salta un puerto que tocaba ser le�do y lee el siguiente.
     * @since 1.0
     */    
    @Override
    public void saltarPuerto() {
        puertoLeido = (puertoLeido + 1) % numPuertos;
    }
    
    /**
     * Este m�todo devuelve el idetificador del puerto del que se ley� el �ltimo
     * paquete.
     * @return El identificador del �ltimo puerto le�do.
     * @since 1.0
     */    
    @Override
    public int obtenerPuertoLeido() {
        return puertoLeido;
    }

    /**
     * Este m�todo toma una direcci�n IP y devuelve el puerto del conjunto de puertos,
     * en cuyo extremo est� el nodo con dicha IP.
     * @param ip IP a la que se desea acceder.
     * @return El puerto que conecta al nodo cuya IP es la especificada. NULL en caso de que no
     * hay conexi�n directa con dicho nodo/IP.
     * @since 1.0
     */    
    @Override
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
     * Este m�todo consulta un puerto para obtener la IP del nodo que se encuentra en
     * el otro extremo.
     * @param p Identificado de un puerto del conjunto de puertos.
     * @return IP del nodo destino al que est� unido el puerto especificado. NULL si el puerto
     * est� libre.
     * @since 1.0
     */    
    @Override
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
     * Este m�todo calcula la congesti�n global del conjunto de puertos, en forma de
     * porcentaje.
     * @return Porcentaje (0%-100%) de congesti�n del conjunto de puertos.
     * @since 1.0
     */    
    @Override
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
     * Este m�todo reinicia el valor de todos los atributos de la clase, como si
     * acabasen de ser creados en el constructor.
     * @since 1.0
     */    
    @Override
    public void reset(){
        this.cerrojoCjtoPuertos.liberar();
        int i=0;
        for (i=0; i<this.numPuertos; i++) {
            puertos[i].reset();
        }
        this.puertoLeido = 0;
        this.ponerTamanioOcupadoCjtoPuertos(0);
        siguientePaqueteALeer = null;
        for (i=0; i<11; i++) {
            actualPorPrioridad[i] = 0;
        }
        this.saturadoArtificialmente = false;
        this.verdaderaOcupacion = 0;
        this.cerrojoCjtoPuertos.liberar();
    }
    
    /**
     * Este m�todo permite establecer el nivel de saturaci�n del nodo en el 97% de tal
     * forma que r�pidamente comience a descartar paquetes. Lo hace de forma artificial
     * y es capaz de volver al estado original en cualquier momento.
     * @param sa TRUE indica que el puerto se debe saturar. FALSE indica lo contrario.
     * @since 1.0
     */    
    @Override
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
    private int prioridadActual;
    private TPDU siguientePaqueteALeer;
    private int ratioPorPrioridad[];
    private int actualPorPrioridad[];
}
