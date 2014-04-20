package simMPLS.electronica.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import simMPLS.protocolo.TPDUMPLS;
import simMPLS.utiles.TIdentificadorRotativo;
import simMPLS.utiles.TLock;


/**
 * Este m�todo implementa una tabla donde se almacenar�n las peticiones de
 * retransmisi�n realizadas por un nodo y a�n no contestadas.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
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
        cerrojo = new TLock();
    }

    /**
     * Este m�todo reinicia el valro de los atributos de la clase, dej�ndolos como si
     * acabasen de ser creados por el constructor.
     * @since 1.0
     */    
    public void reset() {
        entradas = null;
        generaId = null;
        cerrojo = null;
        entradas = new TreeSet();
        generaId = new TIdentificadorRotativo();
        cerrojo = new TLock();
    }
    
    /**
     * Este m�todo actualiza el puerto de salida de todas las entradas coincidentes,
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
     * Este m�todo elimina de la tabla todas las entradas que tengan como puerto de
     * salida el puerto especificado por par�metro.
     * @param pAnterior Puerto que determinar� si la entrada se elimina o no.
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
     * Este m�todo inserta una nueva entrada en la tabla.
     * @since 1.0
     * @param paquete Paquete para el cual se va a solicitar retransmisi�n.
     * @param pEntrada Puerto de entrada de dicho paquete. Ser� la salida de la retransmisi�n.
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
     * Este m�todo elimina una entrada de la tabla.
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
     * Este m�todo obtiene una entrada concreta de la tabla.
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
     * Este m�todo actualiza la tabla. B�sicamente consiste en eliminar todas aquellas
     * entradas para las cuales ya no quedan intentos de petici�n y se les han agotado
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
     * Este m�todo decrementa el timeout para todas las entradas de la tabla.
     * @param d N�mero de nanosegundos en los que se debe decrementar el timeout de las
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
     * Este m�todo obtiene el puerto de salida de una entrada concreta.
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
     * Este m�todo obtiene la IP del siguiente nodo al que se debe solicitar la
     * retransmisi�n de un paquete.
     * @param idf Identificador de flujo de la entrada deseada.
     * @param idp Identificador de paquete de la entrada deseada.
     * @return IP del siguiente nodo al que se le tiene que solicitar la retransmisi�n. NULL si
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
     * Este m�todo obtiene el iterador de las entradas de la tabla.
     * @return iterador de las entradas de la tabla.
     * @since 1.0
     */    
    public Iterator obtenerIterador() {
        return entradas.iterator();
    }
    
    /**
     * Este m�todo permite el acceso al cerrojo que permite la sincronizaci�n de la
     * tabla.
     * @return Cerrojo de la tabla.
     * @since 1.0
     */    
    public TLock obtenerCerrojo() {
        return this.cerrojo;
    }
    
    /**
     * Temporizador para ver cu�ndo se ha de repetir la solicitud.
     * @since 1.0
     */    
    public static final int TIMEOUT = 50000;
    /**
     * N�mero de veces que se ha de repetir la solicitud.
     * @since 1.0
     */    
    public static final int INTENTOS = 8;
    
    private TreeSet entradas;
    private TIdentificadorRotativo generaId;
    private TLock cerrojo;
}
