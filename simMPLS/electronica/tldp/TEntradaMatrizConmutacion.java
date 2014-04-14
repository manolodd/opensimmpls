//**************************************************************************
// Nombre......: TEntradaMatrizConmutacion.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa una entrada para la matriz de conmuta-
// ............: ción de un nodo LER o LSR o sus variantes, dentro de la to-
// ............: pología. Equivale a entradas ILM, FTN y NHLFE unificadas en
// ............: un solo tipo de entrada.
// Fecha.......: 13/04/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.electronica.tldp;

import simMPLS.protocolo.*;

/**
 * Esta clase implementa una entrada dentro de la matriz de conmutación. Esto
 * comprende un compendio entre la tabla Label Incoming Map (LIP), Next Hop Label
 * Forwarding Entry (NHLFE) y FEC to NHLFE (FTN). Es necesario para la conmutación
 * y el enrutamiento.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEntradaMatrizConmutacion {

    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TEntradaMatrizConmutacion.
     * @since 1.0
     */
    public TEntradaMatrizConmutacion() {
        pEntrada = this.SIN_DEFINIR;
        labelFEC = this.SIN_DEFINIR;
        pSalida = this.SIN_DEFINIR;
        pSalidaBackup = this.SIN_DEFINIR;
        label = this.SIN_DEFINIR;
        labelBackup = this.SIN_DEFINIR;
        operacion = this.SIN_DEFINIR;
        tipoEntrada = this.LABEL;
        String IPDestino = "";
        idLDPPropio = this.SIN_DEFINIR;
        idLDPAntecesor = this.SIN_DEFINIR;
        timeout = this.TIMEOUT;
        intentos = this.INTENTOS;
        esDebackup = false;
    }

    /**
     * Este método comprueba si segun la información de la entrada, hay un LSP de
     * backup establecido.
     * @return TRUE, si hay un LSP de backup establecido. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean hayLSPDeBackup() {
        if (this.pSalidaBackup >= 0)
            if (this.labelBackup > 15)
                return true;
        return false;
    }
    
    /**
     * Este método comprueba si se debe eliminar el LSP de backup aunque este no esté
     * establecido (porque, por ejemplo, se esté estableciendo).
     * @return TRUE, si se debe iniciar una eliminación del LSP de backup. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public boolean seDebeEliminarLSPDeBackup() {
        if (hayLSPDeBackup()) {
            if (this.labelBackup == this.SOLICITANDO_ETIQUETA)
                return true;
            if (this.labelBackup == this.ELIMINANDO_ETIQUETA)
                return true;
        }
        return false;
    }
    
    /**
     * Este método, usado desde un nodo activo, comprueba si dicho nodo activo es el
     * iniciador del LSP de backup o no.
     * @return TRUE, si el nodo ACTIVO que realiza la consulta es el iniciador del LSP de
     * backup. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean soyInicioLSPBackup() {
        if (this.esDebackup) 
            return false;
        return true;
    }
    
    /**
     * Este método dado un puerto de entrada de un LSP, obtiene el puerto opuesto. Si
     * es la entrada, indica la salida. Si es la salida, devuelve la entrada.
     * @param puerto Puerto cuyo opuesto se desea averiguar.
     * @return EEl puerto opuesto al pasado por parámetros.
     * @since 1.0
     */    
    public int obtenerPuertoOpuesto(int puerto) {
        if (this.pEntrada == puerto)
            return this.pSalida;
        if (this.pSalida == puerto)
            return this.pEntrada;
        if (this.pSalidaBackup == puerto)
            return this.pEntrada;
        return this.pEntrada;
    }
    
    /**
     * Este método averigua si para esta entrada, el solicitante que envió el paquete
     * TLDP va a crear un LSP de respaldo o no. Se usa sólo con fines de simulación
     * visual.
     * @return TRUE, si la petición tiene como finalidad un LSP de respaldo. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public boolean obtenerEntranteEsLSPDEBackup() {
        return this.esDebackup;
    }
    
    /**
     * Este método establece si para esta entrada, el solicitante que envió el paquete
     * TLDP va a crear un LSP de respaldo o no. Se usa sólo con fines de simulación
     * visual.
     * contrario.
     * @since 1.0
     * @param lspr TRUE, si la entrada corresponde a un LSP de respaldo. FALSE en caso contrario.
     */    
    public void ponerEntranteEsLSPDEBackup(boolean lspr) {
        this.esDebackup = lspr;
    }
    
    /**
     * Este método reestablece a cero el número de intentos que se llevan realizados de
     * petición TLDP.
     * @since 1.0
     */    
    public void reestablecerIntentos() {
        intentos = INTENTOS;
    }
    
    /**
     * Este método decrementa el número de intentos que se llevan realizados de
     * petición TLDP.
     * @since 1.0
     */    
    public void decrementarIntentos() {
        if (intentos > 0)
            intentos--;
        if (intentos < 0)
            intentos = 0;
    }
    
    /**
     * Este método comprueba el número de intentos que se llevan realizados de
     * petición TLDP, para ver si aún quedan intentos.
     * @since 1.0
     * @return TRUE, si todavía se puede reintentar la comunicación TLDP. FALSE en caso
     * contrario.
     */    
    public boolean quedanIntentos() {
        if (intentos > 0)
            return true;
        return false;
    }
    
    /**
     * Este método reestablece a su valor original el tiempo que falta para que expire
     * el intento de comunicación y haya que reintentarlo de nuevo.
     * @since 1.0
     */    
    public void reestablecerTimeOut() {
        timeout = TIMEOUT;
    }

    /**
     * Este método decrementa el tiempo que falta para que expire el intento de
     * comunicación y haya que reintentarlo de nuevo.
     * @since 1.0
     * @param dec El número de nanosegundos que se va a descontar del tiempo de crédito del
     * timeout.
     */    
    public void decrementarTimeOut(int dec) {
        if (timeout > 0)
            timeout -= dec;
        if (timeout < 0)
            timeout = 0;
    }
    
    /**
     * Este método vuelve a realizar las peticiones pendientes para las cuales queden
     * intentos de petición y para las cuales haya vencido el timeout.
     * @since 1.0
     * @return TRUE, si las peticiones se hacen de nuevo correctamente. FALSE, en caso
     * contrario.
     */    
    public boolean hacerPeticionDeNuevo() {
        if (quedanIntentos()) {
            if (timeout == 0)
                if ((this.label == this.SOLICITANDO_ETIQUETA) || (this.label == this.ELIMINANDO_ETIQUETA))
                    return true;
            return false;
        }
        return false;
    }
    
    /** Este método permite especificar cual será el puerto de entrada en la entrada de
     * la tabla.
     * @param p El puerto de entrada deseado.
     * @since 1.0
     */    
    public void ponerPuertoEntrada(int p) {
        pEntrada = p;
    }

    /** Este método permite obtener el puerto de entrada en la entrada de
     * la tabla.
     * @return El puerto de entrada según el valor de los atributos.
     * @since 1.0
     */    
    public int obtenerPuertoEntrada() {
        return pEntrada;
    }

    /** Este método permite especificar cual será el valor de la etiqueta o el FEC de
     * entrada en la entrada de la tabla.
     * @param lf El valor de FEC o de la etiqueta de entrada.
     * @since 1.0
     */    
    public void ponerLabelFEC(int lf) {
        labelFEC = lf;
    }

    /** Este método permite obtener el valor de la etiqueta o el FEC de entrada en la entrada de la tabla.
     * @return El valor de la etiqueta o FEC de entrada.
     * @since 1.0
     */    
    public int obtenerLabelFEC() {
        return labelFEC;
    }

    /** Este método permite obtener el valor del puerto de salida en la entrada de
     * la tabla.
     * @param p El puerto de salida deseado.
     * @since 1.0
     */    
    public void ponerPuertoSalida(int p) {
        pSalida = p;
    }

    /** Este método permite obtener el valor del puerto de salida en la entrada de
     * la tabla, para un camino de respaldo.
     * @param p El puerto de salida deseado.
     * @since 1.0
     */    
    public void ponerPuertoSalidaBackup(int p) {
        pSalidaBackup = p;
    }

    /** Este método permite obtener el valor del puerto de salida en la entrada de
     * la tabla.
     * @return El valor del puerto de salida.
     * @since 1.0
     */    
    public int obtenerPuertoSalida() {
        return pSalida;
    }

    /** Este método permite obtener el valor del puerto de salida en la entrada de
     * la tabla, para un LSP de respaldo.
     * @return El valor del puerto de salida.
     * @since 1.0
     */    
    public int obtenerPuertoSalidaBackup() {
        return pSalidaBackup;
    }

    /**
     * Este método establece el LSP de respaldo como LSP principal y anula
     * posteriormente el LSP de respaldo, para que se vuelva a solicitar la creación de
     * uno.
     * @since 1.0
     */    
    public void conmutarLSPPrincipalALSPBackup() {
        this.pSalida = this.pSalidaBackup;
        this.label = this.labelBackup;
        this.pSalidaBackup = this.SIN_DEFINIR;
        this.labelBackup = this.SIN_DEFINIR;
        this.esDebackup = false;
    }
    
    /** Este método permite poner el valor de la etiqueta de salida que se utilizará.
     * @param l La etiqueta de salida.
     * @since 1.0
     */    
    public void ponerEtiqueta(int l) {
        label = l;
    }

    /** Este método permite poner el valor de la etiqueta de salida de backup 
     * que se utilizará.
     * @param l La etiqueta de salida.
     * @since 1.0
     */    
    public synchronized void ponerEtiquetaBackup(int l) {
        labelBackup = l;
    }

    /** Este método permite obtener el valor de la etiqueta de salida para el
     * LSP principal.
     * @return El valor de la etiqueta de salida.
     * @since 1.0
     */    
    public int obtenerEtiqueta() {
        return label;
    }

    /** Este método permite obtener el valor de la etiqueta de salida para el
     * LSP de backup.
     * @return El valor de la etiqueta de salida de backup.
     * @since 1.0
     */    
    public synchronized int obtenerEtiquetaBackup() {
        return labelBackup;
    }
    
    /** Este método permite obtener el valor de la operación que se aplicará sobre la
     * cima de la pila de etiquetas de los paquetes entrantes en la entrada de
     * la tabla.
     * @param op Operación que se aplicará. una de las constantes definidas en la clase.
     * @since 1.0
     */    
    public void ponerOperacion(int op) {
        operacion = op;
    }

    /** Este método permite obtener qué operación, según la entrada de la tabla, se
     * aplicará a los paquetes conmutados.
     * @return La operación que se aplicará. Una de las constantes definidas en la clase.
     * @since 1.0
     */    
    public int obtenerOperacion() {
        return operacion;
    }

    /** Este método permite especificar el tipo de entrada de que se trata (ILM o FTN).
     * @param t El tipo de entrada, según las constantes definidas en la clase.
     * @since 1.0
     */    
    public void ponerTipo(int t) {
        tipoEntrada = t;
    }

    /** Este método permite obtener de qué tipo de entrada se trata.
     * @return El tipo de entrada de que se trata la instancia actual. Una de las constantes
     * definidas en la clase.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return tipoEntrada;
    }

    /** Este método obtiene la IP de destino final del tráfico conmutado.
     * @return La IP del receptor final del tráfico.
     * @since 1.0
     */    
    public String obtenerDestinoFinal() {
        return ipDestino;
    }

    /** Este método permite especificar la IP de destino final del tráfico conmutado.
     * @param ip La IP del destino final del tráfico.
     * @since 1.0
     */    
    public void ponerDestinoFinal(String ip) {
        ipDestino = ip;
    }

    /** Este método permite especificar el identificador de sesión TLDP propio.
     * @param i El identificador TLDP deseado.
     * @since 1.0
     */    
    public void ponerIDLDPPropio(int i) {
        this.idLDPPropio = i;
    }

    /** Este método permite consultar cuál es el identificador TLDP propio que estamos usando.
     * @return El identificador TLDP propio.
     * @since 1.0
     */    
    public int obtenerIDLDPPropio() {
        return this.idLDPPropio;
    }

    /** Este método permite establecer el identificador TLDp que está usando nuestro
     * adyadcente. De este modo se puede entender que ambos están encadenados por la
     * sesión TLDP.
     * @param id El identificador TLDP usado por nuestro adyacente antecesor.
     * @since 1.0
     */    
    public void ponerIDLDPAntecesor(int id) {
        this.idLDPAntecesor = id;
    }

    /** Este método permite obtener el identificador TLDP que está usando nuestro
     * adyadcente.
     * @return El identificador TLDP de nuestro nodo adyacente antecesor.
     * @since 1.0
     */    
    public int obtenerIDLDPAntecesor() {
        return this.idLDPAntecesor;
    }

    /** Este método indica si la entrada es válida o si por el contrario tiene algún
     * error y no debe ser tenida en consideración. Se puede entender como un validador
     * de la entrada de la matriz de conmutación.
     * @return TRUE si la entrada es válida y FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean esUsable() {
        if (this.obtenerPuertoEntrada() == this.SIN_DEFINIR)
            return false;
        if (this.obtenerLabelFEC() == this.SIN_DEFINIR)
            return false;
        if (this.obtenerPuertoSalida() == this.SIN_DEFINIR)
            return false;
        if (this.obtenerIDLDPPropio() == this.SIN_DEFINIR)
            return false;
        if (this.obtenerDestinoFinal().equals(""))
            return false;
        if (this.obtenerEtiqueta() == this.SIN_DEFINIR)
            if (this.obtenerOperacion() == this.CAMBIAR_ETIQUETA)
                return false;
            if (this.obtenerOperacion() == this.PONER_ETIQUETA)
                return false;
        return true;
    }

    /**
     * Esta constante indica que la entrada de la matriz es de tipo FTN (FEC to NHLFE).
     * @since 1.0
     */    
    public static final int FEC = 0;
    /**
     * Esta constante indica que la entrada de la matriz es de tipo ILM (Incoming
     * Label Map).
     * @since 1.0
     */    
    public static final int LABEL = 1;

    /**
     * Esta constante indica una entrad de la matriz no especifica aún nada de cómo se
     * ha de tratar el paquete entrante.
     * @since 1.0
     */    
    public static final int SIN_DEFINIR = -1;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta entrada de
     * la matriz de conmutación, se le debe poner una etiqueta.
     * @since 1.0
     */    
    public static final int PONER_ETIQUETA = -10;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta entrada de
     * la matriz de conmutación, se le debe quitar una etiqueta.
     * @since 1.0
     */    
    public static final int QUITAR_ETIQUETA = -12;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta entrada de
     * la matriz de conmutación, se le debe modificar su etiqueta superior.
     * @since 1.0
     */    
    public static final int CAMBIAR_ETIQUETA = -11;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta entrada de
     * la matriz de conmutación, se le debe conmutar sin modificaciones.
     * @since 1.0
     */    
    public static final int NOOP = -13;
    
    /**
     * Esta constante indica que aún no se ha configurado el valor de la etiqueta en una
     * entrada de la matriz, pero se está en espera de que el nodo adyacente responda a
     * una petición.
     * @since 1.0
     */    
    public static final int SOLICITANDO_ETIQUETA = -33;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha denegado la etiqueta para recibir tráfico.
     * @since 1.0
     */    
    public static final int ETIQUETA_DENEGADA = -31;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha concedido la etiqueta para recibir tráfico.
     * @since 1.0
     */    
    public static final int ETIQUETA_CONCEDIDA = -30;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha eliminado una etiqueta que había concedido anteriormente para recibir tráfico.
     * Se está en proceso de destrucción el LSP.
     * @since 1.0
     */    
    public static final int ELIMINANDO_ETIQUETA = -32;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha eliminado una etiqueta que había concedido anteriormente para recibir tráfico.
     * Se está en proceso de destrucción el LSP.
     * @since 1.0
     */    
    public static final int ETIQUETA_ELIMINADA = -34;

    /**
     * Esta constante indica que no hay camino al destino para el cual se ha 
     * solicitado una etiqueta. El puerto de salida, por tanto, aparecerá con
     * este valor.
     * @since 1.0
     */    
    public static final int SIN_CAMINO = -20;
    
    /** Este atributo es el puerto de entrada de la tabla de encaminamiento/conmutación.
     * @since 1.0
     */    
    private int pEntrada;
    /** Este atributo es el valor de la etiqueta de entrada (entrada ILM) o del FEC de
     * entrada (entrada FTN) de entrada de la tabla de encaminamiento/conmutación.
     * @since 1.0
     */    
    private int labelFEC;
    /** Este atributo es el puerto de salida de la tabla de encaminamiento/conmutación.
     * @since 1.0
     */    
    private int pSalida;
    /** Este atributo es el puerto de salida de la tabla de encaminamiento/conmutación,
     * para un LSP de respaldo.
     * @since 1.0
     */    
    private int pSalidaBackup;
    /** Este atributo es la etiqueta de salida de la tabla de encaminamiento/conmutación.
     * @since 1.0
     */    
    private int label;
    /** Este atributo es la etiqueta de salida de backup de la tabla de 
     * encaminamiento/conmutación.
     * @since 1.0
     */    
    private int labelBackup;
    /** Este atributo es quien indica la operación que hay que hacer sobre la cima de la
     * pila de etiquetas.
     * @since 1.0
     */    
    private int operacion;
    /** Este atributo indica si la entrada es de tipo ILM o FTN.
     * @since 1.0
     */    
    private int tipoEntrada;
    /** Este atributo almacena la IP destino final del flujo que se va a
     * encaminar/conmutar.
     * @since 1.0
     */    
    private String ipDestino;
    /** Este atributo es identificador de sesión LDP que tiene esta entrada.
     * @since 1.0
     */    
    private int idLDPPropio;
    /** Este atributo es identificador de sesión LDP que tiene el nodo adyacente
     * antecesor en la comunicación y que servirá para retornar etiquetas sin
     * ambiguedad hacia atrás.
     * @since 1.0
     */    
    private int idLDPAntecesor;
    /** Este atributo almacena si una petición entrante se refiere a un LSP de
     * respaldo en el solicitante o no.
     * @since 1.0
     */    
    private boolean esDebackup;
    
    private int timeout;
    private int intentos;
    
    private static final int TIMEOUT = 50000;
    private static final int INTENTOS = 3;
}
