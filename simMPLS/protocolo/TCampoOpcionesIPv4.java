package simMPLS.protocolo;

/** Esta clase implementa una codificación del campo opciones de IPv4 acorde a lo
 * que necesitamos para la propuesta de GoS sobre MPLS.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TCampoOpcionesIPv4 {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de
     * TCampoOpcionesIPv4.
     * @since 1.0
     */
    public TCampoOpcionesIPv4() {
        nivelGoS = 0;
        nodosPasados = new String[this.MAX_IPS];
        int i = 0;
        for (i=0; i< this.MAX_IPS; i++) {
            nodosPasados[i] = null;
        }
        campoUsado = false;
        nodosOcupados = 0;
        idUnico = 0;
        hayMarcasDePaso = false;
    }
    
    /** Este método devuelve el tamaño del campo opciones debido a que éste puede ser
     * variable en palabras de 4 bytes. El resultado lo devuelve en bytes.
     * @return El tamaño en bytes del campo opciones de IPv4.
     * @since 1.0
     */
    public int obtenerTamanio() {
        int tam=0;
        int contador = 0;
        if (this.campoUsado) {
            tam += 1;   // Del campo GoS
            tam += 4;   // Del identificador único
            tam += (4*this.nodosOcupados);
            while (contador < tam) {
                contador += 4;
            }
            tam = contador;
        }
        return tam;
    }
    
    /**
     * Este método permite establecer el nivel de garantía de servicio que se ha
     * elegido para el paquete.
     * @param ngos Nivel de garantía de servicio; Una de las constantes definidas en la clase TPDU.
     * @since 1.0
     */
    public void ponerNivelGoS(int ngos) {
        this.nivelGoS = ngos;
        this.campoUsado = true;
    }
    
    /**
     * Este método permite establecer como usado el campo opciones.
     * @since 1.0
     */
    public void usar() {
        this.campoUsado = true;
    }
    
    /**
     * Este método permite consultar si el campo opciones está siendo usado o no.
     * @return true, si el campo opciones está siendo usado. false en caso contrario.
     * @since 1.0
     */
    public boolean estaUsado() {
        return this.campoUsado;
    }
    
    /**
     * Este método poermite obtener el nivel de garantía de servicio que tiene el
     * paquete.
     * @return El nivel de garantía de servicio del paquete. una de las constantes definidas en
     * la clase TPDU.
     * @since 1.0
     */
    public int obtenerNivelGoS() {
        return this.nivelGoS;
    }
    
    /**
     * Este método permite establecer el identificador del paquete.
     * @param id El identificador del paquete.
     * @since 1.0
     */
    public void ponerIDPaqueteGoS(int id) {
        this.idUnico = id;
        this.campoUsado = true;
    }
    
    /**
     * Este método permite obtener el identificador del paquete.
     * @return El identificador del paquete.
     * @since 1.0
     */
    public int obtenerIDPaqueteGoS() {
        return this.idUnico;
    }
    
    /**
     * Este método permite insertar en el campo opciones una dirección IP de un nodo
     * activo atravesado.
     * @param IPNodo La IP del nodo activo atravesado.
     * @since 1.0
     */
    public void ponerNodoAtravesado(String IPNodo) {
        this.hayMarcasDePaso = true;
        this.campoUsado = true;
        if (this.nodosOcupados < MAX_IPS) {
            this.nodosPasados[this.nodosOcupados] = IPNodo;
            this.nodosOcupados++;
        } else {
            int contador = 1;
            for (contador = 1; contador < MAX_IPS; contador++) {
                nodosPasados[contador-1] = nodosPasados[contador];
            }
            nodosPasados[MAX_IPS-1] = IPNodo;
        }
    }
    
    /**
     * Este método permite averiguar si en el campo opciones hay anotadas direcciones
     * IP de los nodos activos que se han pasado o no.
     * @return true, si hay direcciones IP almacenadas en el campo opciones. false, en caso
     * contrario,
     * @since 1.0
     */
    public boolean tieneMarcasDePaso() {
        return this.hayMarcasDePaso;
    }
    
    /**
     * Este método comprueba el número de IP de nodos activos atravesados por el
     * paquete que están actualmente marcadas en el campo opciones.
     * @return Número de IP de nodos activos atravesados que contiene el campo opciones.
     * @since 1.0
     */    
    public int obtenerNumeroDeNodosActivosAtravesados() {
        return this.nodosOcupados;
    }
    
    /**
     * Este método devuelve la IP del nodo activo que el paquete atravesó hace <I>naa</I>
     * nodos activos.
     * @param naa Número de nodos activos atravesados antes del que queremos saber la IP.
     * @return IP del nodo activo deseado.
     * @since 1.0
     */    
    public String obtenerActivoNodoAtravesado(int naa) {
        if (naa < this.MAX_IPS) {
            return this.nodosPasados[naa];
        }
        return null;
    }
    
    /**
     * Esta constante indica el número máximo de direcciones IP de nodos activos
     * atravesados que pueden ser almacenadas en el campo opciones.
     * @since 1.0
     */
    private static final int MAX_IPS = 8;
    
    /**
     * Este atributo almacena los datos del nivel de garantía de servicio y la creación
     * de LSp de respaldo que se han elegido para el paquete.
     * @since 1.0
     */
    private int nivelGoS;
    /**
     * Este atributo está configurado como una cola rotatoria que almacena las
     * direcciones IP (la 8 últimas como mucho9 de los nodos activos que ha ido
     * atravesando el paquete.
     * @since 1.0
     */
    private String nodosPasados[];
    /**
     * Este atributo indica si el campo opciones se ha usado o no, para de este modo
     * contarlo en el cómputo del tamaño del paquete o no.
     * @since 1.0
     */
    private boolean campoUsado;
    /**
     * Este método indica cuántas direcciones IP's de nodos activos atravesados hay
     * almacenadas en el campo opciones.
     * @since 1.0
     */
    private int nodosOcupados;
    /**
     * Este método almacena el identificador unico que, junto con la IP de origen del
     * paquete, forman la clave primaria que identifica al paquete.
     * @since 1.0
     */
    private int idUnico;
    /**
     * Este atributo indica si se ha anotado alguna dirección IP de algun nodo activo
     * en el campo opciones.
     * @since 1.0
     */
    private boolean hayMarcasDePaso;
}
