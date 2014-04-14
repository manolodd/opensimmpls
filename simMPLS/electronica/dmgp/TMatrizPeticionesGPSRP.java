package simMPLS.electronica.dmgp;

import java.util.*;
import simMPLS.utiles.*;
import simMPLS.protocolo.*;

/**
 * Este método implementa una tabla donde se almacenarán las peticiones de
 * retransmisión realizadas por un nodo y aún no contestadas.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TMatrizPeticionesGPSRP {
    
    /**
     * Constructo de la clase. Crea una nueva instancia de TMatrizPeticionesGPSRP.
     * @since 1.0
     */    
    public TMatrizPeticionesGPSRP() {
        entradas = new TreeSet();
        generaId = new TIdentificadorRotativo();
        cerrojo = new TMonitor();
    }

    /**
     * Este método reinicia el valro de los atributos de la clase, dejándolos como si
     * acabasen de ser creados por el constructor.
     * @since 1.0
     */    
    public void reset() {
        entradas = null;
        generaId = null;
        cerrojo = null;
        entradas = new TreeSet();
        generaId = new TIdentificadorRotativo();
        cerrojo = new TMonitor();
    }
    
    /**
     * Este método actualiza el puerto de salida de todas las entradas coincidentes,
     * por un nuevo puerto de salida.
     * @param pAnterior Puerto que se desea susituir en las entradas.
     * @param pNuevo Nuevo valor para el puerto de salida de las entradas.
     * @since 1.0
     */    
    public void actualizarPuertoSalida(int pAnterior, int pNuevo) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerPuertoSalida() == pAnterior) {
                ep.ponerPuertoSalida(pNuevo);
            }
        }
        this.cerrojo.liberar();
    }

    /**
     * Este método elimina de la tabla todas las entradas que tengan como puerto de
     * salida el puerto especificado por parámetro.
     * @param pAnterior Puerto que determinará si la entrada se elimina o no.
     * @since 1.0
     */    
    public void eliminarEntradasConPuertoSalida(int pAnterior) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerPuertoSalida() == pAnterior) {
                ite.remove();
            }
        }
        this.cerrojo.liberar();
    }
    
    /**
     * Este método inserta una nueva entrada en la tabla.
     * @since 1.0
     * @param paquete Paquete para el cual se va a solicitar retransmisión.
     * @param pEntrada Puerto de entrada de dicho paquete. Será la salida de la retransmisión.
     * @return La entrada creada que se acaba de insertar. Null en caso contrario.
     */    
    public TEntradaPeticionesGPSRP insertarEntrada(TPDUMPLS paquete, int pEntrada) {
        this.cerrojo.bloquear();
        TEntradaPeticionesGPSRP ep = new TEntradaPeticionesGPSRP(this.generaId.obtenerNuevo());
        ep.ponerPuertoSalida(pEntrada);
        ep.ponerIdFlujo(paquete.obtenerCabecera().obtenerIPOrigen().hashCode());
        ep.ponerIdPaquete(paquete.obtenerCabecera().obtenerClavePrimaria());
        int numIPs = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerNumeroDeNodosActivosAtravesados();
        int i=0;
        String siguienteIP = "";
        for (i=0; i<numIPs; i++) {
            siguienteIP = paquete.obtenerCabecera().obtenerCampoOpciones().obtenerActivoNodoAtravesado(i);
            if (siguienteIP != null) {
                ep.ponerIPNodoAtravesado(siguienteIP);
            }
        }
        entradas.add(ep);
        this.cerrojo.liberar();
        return ep;
    }
    
    /**
     * Este método elimina una entrada de la tabla.
     * @param idf Flujo al que hace referencia la entrada.
     * @param idp Paquete al que hace referencia la tabla.
     * @since 1.0
     */    
    public void eliminarEntrada(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    ite.remove();
                }
            }
        }
        this.cerrojo.liberar();
    }
    
    
    /**
     * Este método obtiene una entrada concreta de la tabla.
     * @param idf Identificador del flujo al que hace referencia la entrada.
     * @param idp identificador del paquete al que hace referencia la entrada.
     * @return Entrada buscada. NULL si no se encuentra.
     * @since 1.0
     */    
    public TEntradaPeticionesGPSRP obtenerEntrada(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    this.cerrojo.liberar();
                    return ep;
                }
            }
        }
        this.cerrojo.liberar();
        return null;
    }
    
    /**
     * Este método actualiza la tabla. Básicamente consiste en eliminar todas aquellas
     * entradas para las cuales ya no quedan intentos de petición y se les han agotado
     * los temporizadores.
     * @since 1.0
     */    
    public void actualizarEntradas() {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.debePurgarse()) {
                ite.remove();
            } else {
                ep.restaurarTimeOut();
            }
        }
        this.cerrojo.liberar();
    }
    
    /**
     * Este método decrementa el timeout para todas las entradas de la tabla.
     * @param d Número de nanosegundos en los que se debe decrementar el timeout de las
     * entradas.
     * @since 1.0
     */    
    public void decrementarTimeOut(int d) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            ep.decrementarTimeOut(d);
        }
        this.cerrojo.liberar();
    }

    /**
     * Este método obtiene el puerto de salida de una entrada concreta.
     * @param idf Identificador del flujo al que hace referencia la entrada.
     * @param idp Identificador del paquete al que hace referencia la entrada.
     * @return Puerto de salida de la entrada.
     * @since 1.0
     */    
    public int obtenerPuertoSalida(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    this.cerrojo.liberar();
                    return ep.obtenerPuertoSalida();
                }
            }
        }
        this.cerrojo.liberar();
        return -1;
    }
    
    /**
     * Este método obtiene la IP del siguiente nodo al que se debe solicitar la
     * retransmisión de un paquete.
     * @param idf Identificador de flujo de la entrada deseada.
     * @param idp Identificador de paquete de la entrada deseada.
     * @return IP del siguiente nodo al que se le tiene que solicitar la retransmisión. NULL si
     * no existe tal nodo.
     * @since 1.0
     */    
    public String obtenerIPNodoActivo(int idf, int idp) {
        this.cerrojo.bloquear();
        Iterator ite = this.entradas.iterator();
        TEntradaPeticionesGPSRP ep = null;
        while (ite.hasNext()) {
            ep = (TEntradaPeticionesGPSRP) ite.next();
            if (ep.obtenerIdFlujo() == idf) {
                if (ep.obtenerIdPaquete() == idp) {
                    this.cerrojo.liberar();
                    return ep.obtenerIPNodoAtravesado();
                }
            }
        }
        this.cerrojo.liberar();
        return null;
    }
    
    /**
     * Este método obtiene el iterador de las entradas de la tabla.
     * @return iterador de las entradas de la tabla.
     * @since 1.0
     */    
    public Iterator obtenerIterador() {
        return entradas.iterator();
    }
    
    /**
     * Este método permite el acceso al cerrojo que permite la sincronización de la
     * tabla.
     * @return Cerrojo de la tabla.
     * @since 1.0
     */    
    public TMonitor obtenerCerrojo() {
        return this.cerrojo;
    }
    
    /**
     * Temporizador para ver cuándo se ha de repetir la solicitud.
     * @since 1.0
     */    
    public static final int TIMEOUT = 50000;
    /**
     * Número de veces que se ha de repetir la solicitud.
     * @since 1.0
     */    
    public static final int INTENTOS = 8;
    
    private TreeSet entradas;
    private TIdentificadorRotativo generaId;
    private TMonitor cerrojo;
}
