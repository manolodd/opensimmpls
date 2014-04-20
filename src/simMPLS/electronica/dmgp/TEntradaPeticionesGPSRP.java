package simMPLS.electronica.dmgp;

import java.util.LinkedList;


/**
 * Esta clase implementa un entrada que almacenar� los datos de una petici�n de
 * retransmisi�n realizada por un nodo.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEntradaPeticionesGPSRP implements Comparable {
    
    /**
     * Constructor de la clase. Implementa una nueva instancia de
     * TEntradaPeticionesGPSRP.
     * @param oll Orden de llegada. Es un n�mero que debe servir para que esta entrada pueda ser
     * ordenada en una colecci�n de forma coherente.
     * @since 1.0
     */    
    public TEntradaPeticionesGPSRP(int oll) {
        timeout = TMatrizPeticionesGPSRP.TIMEOUT;
        intentos = TMatrizPeticionesGPSRP.INTENTOS;
        idFlujo = -1;
        idPaquete = -1;
        pSalida = -1;
        nodosAtravesados = new LinkedList();
        orden = oll;
    }
    
    /**
     * Este m�todo obtiene el orden de llegada e esa entrda, para su ordenaci�n en una
     * colecci�n.
     * @return Orden de llgada de la entrada.
     * @since 1.0
     */    
    public int obtenerOrden() {
        return orden;
    }
    
    /**
     * Este m�todo establece el flujo al que pertenece la entrada.
     * @param idf Flujo al que pertenece la entrada.
     * @since 1.0
     */    
    public void ponerIdFlujo(int idf) {
        this.idFlujo = idf;
    }
    
    /**
     * Este m�todo obtiene el flujo al que pertenece la entrada.
     * @return Flujo al que pertenece la entrada.
     * @since 1.0
     */    
    public int obtenerIdFlujo() {
        return this.idFlujo;
    }
    
    /**
     * Este m�todo establece el identificador del paquete al que se refiere esta
     * entrada.
     * @param idp Identificador del paquete.
     * @since 1.0
     */    
    public void ponerIdPaquete(int idp) {
        this.idPaquete = idp;
    }
    
    /**
     * Este m�todo obtiene el identificador del paquete al que se refiere esta entrada.
     * @return Identificador del paquete.
     * @since 1.0
     */    
    public int obtenerIdPaquete() {
        return this.idPaquete;
    }
    
    /**
     * Este m�todo establece el puerto de salida por donde se ha enviado la solicitud
     * de retransmisi�n.
     * @param ps Puerto de salida.
     * @since 1.0
     */    
    public void ponerPuertoSalida(int ps) {
        this.pSalida = ps;
    }
    
    /**
     * Este m�todo obtiene el puerto de salida por el que se ha enviado la solicitud de
     * retransmisi�n.
     * @return Puerto de salida.
     * @since 1.0
     */    
    public int obtenerPuertoSalida() {
        return this.pSalida;
    }
    
    /**
     * Este m�todo establece la IP de un nodo activo al que se ha de solicitar la
     * retransmision del paquete.
     * @param na IP del nodo activo al que solicitar la retransmisi�n.
     * @since 1.0
     */    
    public void ponerIPNodoAtravesado(String na) {
        this.nodosAtravesados.addFirst(na);
    }
    
    /**
     * Este m�todo obtiene la IP del siguiente nodo activo al que se ha de solicitar
     * la retransmisi�n del paquete.
     * @return IP del siguiente nodo al que se debe pedir la retransmisi�n. NULL en caso de no
     * existir tal nodo.
     * @since 1.0
     */    
    public String obtenerIPNodoAtravesado() {
        if (this.nodosAtravesados.size() > 0)
            return ((String) this.nodosAtravesados.removeFirst());
        return null;
    }
    
    /**
     * Este m�todo decrementa el TimeOut de la retransmisi�n.
     * @param d N�mero de nanosegundos en que se ha de decrementar el timeout.
     * @since 1.0
     */    
    public void decrementarTimeOut(int d) {
        this.timeout -= d;
        if (this.timeout < 0)
            this.timeout = 0;
    }
    
    /**
     * Este m�todo restaura el TimeOut a su valor original.
     * @since 1.0
     */    
    public void restaurarTimeOut() {
        if (this.timeout == 0) {
            if (this.intentos > 0) {
                this.timeout = TMatrizPeticionesGPSRP.TIMEOUT;
                this.intentos--;
            }
        }
    }
    
    /**
     * Este m�todo restaura forzosamente el TimeOut a su valor original y adem�s
     * incrementa el n�mero de intentos agotados de retransmisi�n.
     * @since 1.0
     */    
    public void restaurarTimeOutForzosamente() {
        this.timeout = TMatrizPeticionesGPSRP.TIMEOUT;
        this.intentos--;
        if (this.intentos < 0) {
            intentos = 0;
            timeout = 0;
        }
    }

    /**
     * Este m�todo comprueba si debe reintentarse la solicitud de retransmisi�n de
     * nuevo.
     * @return TRUE, si se debe reintentar la solicitud. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean debeReintentarse() {
        if (this.intentos > 0) {
            if (this.timeout == 0) {
                if (this.nodosAtravesados.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Este m�todo comprueba si la entrada debe eliminarse de la tabla porque no se va
     * a reintentar m�s la solicitud.
     * @return TRU, indica que la entrada debe eliminarse. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean debePurgarse() {
        if (this.nodosAtravesados.size() == 0) {
            return true;
        }
        if (this.intentos == 0) {
            if (this.timeout == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo compara la instancia actual con otra del mismo tipo para averiguar
     * el orden que deben llevar en una colecci�n.
     * @param o Instancia con la que se va a comparar la actual.
     * @return -1, 0 � 1, dependiendo si la instancia actual es menor, igual o mayor que la
     * pasada por par�metro, en t�rminos de orden.
     * @since 1.0
     */    
    @Override
    public int compareTo(Object o) {
        TEntradaPeticionesGPSRP e = (TEntradaPeticionesGPSRP) o;
        if (this.orden < e.obtenerOrden())
            return TEntradaPeticionesGPSRP.ESTE_MENOR;
        if (this.orden > e.obtenerOrden())
            return TEntradaPeticionesGPSRP.ESTE_MAYOR;
        return TEntradaPeticionesGPSRP.ESTE_IGUAL;
    }
    
    private static final int ESTE_MENOR = -1;
    private static final int ESTE_IGUAL = 0;
    private static final int ESTE_MAYOR = 1;
    
    private int timeout;
    private int idFlujo;
    private int idPaquete;
    private int pSalida;
    private LinkedList nodosAtravesados;
    private int orden;
    private int intentos;
}
