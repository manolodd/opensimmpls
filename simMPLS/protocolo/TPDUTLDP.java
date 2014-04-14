package simMPLS.protocolo;

/** Esta clase implementa un paquete TLDP (Tiny Label Distribution Protocol).
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TPDUTLDP extends TPDU {
    
    /** Este método es el constructor de la clase. Crea una nueva instancia de un
     * paquete TLDP basándose en lo parámetros pasados.
     * @param id identificador único del paquete.
     * @param ipo Dirección IP origen del paquete.
     * @param ipd Dirección IP destino del paquete.
     * @since 1.0
     */
    public TPDUTLDP(long id, String ipo, String ipd) {
        super(id, ipo, ipd);
        datosTCP = new TDatosTCP(0);
        datosTLDP = new TDatosTLDP();
        tipoLSP = false;
        entradaPaquete = this.ADELANTE;
    }
    
    /** Este método devuelve el tamaño completo del paquete en bytes, para poder
     * realizar cálculos en la simulación.
     * @return El tamaño completo del paquete en bytes.
     * @since 1.0
     */
    public int obtenerTamanio() {
        int tam = 0;
        tam += super.obtenerCabecera().obtenerTamanio(); // Cabecera IPv4
        tam += this.datosTCP.obtenerTamanio(); // Cabecera TCP
        tam += this.datosTLDP.obtenerTamanio(); // Tamanio mensaje LDP
        return (tam);
    }
    
    /** Este método devuelve la constante TLDP, indicando que el paquete es de tipo
     * TLDP.
     * @return La constante TLDP.
     * @since 1.0
     */
    public int obtenerTipo() {
        return super.TLDP;
    }
    
    /** Este método nos permite el acceso a los datos TCP de este paquete, para poder
     * acceder a sus métodos de forma directa.
     * @return Los datos TCP de esta instancia.
     * @since 1.0
     */
    public TDatosTCP obtenerDatosTCP() {
        return datosTCP;
    }
    
    /** Este método nos permite acceder a los datos TLDP del paquete para hacer uso
     * directamente de sus métodos.
     * @return Los datos TLDP de esta instancia.
     * @since 1.0
     */
    public TDatosTLDP obtenerDatosTLDP() {
        return datosTLDP;
    }
    
    /** Este método nos permite acceder a la cabecera IPv4 del paquete y poder hacer uso
     * de sus métodos de forma directa.
     * @return La cabecera IP del paquete.
     * @since 1.0
     */
    public TCabeceraIPv4 obtenerCabecera() {
        return super.obtenerCabecera();
    }
    
    /**
     * Este método permite obtener el subtipo del paquete TLDP. En esta versión el
     * paquete TLDP no tiene subtipos. Implementa este método para dejar de ser una
     * clase abstracta.
     * @return TPDU.TLDP
     * @since 1.0
     */
    public int obtenerSubTipo() {
        return super.TLDP;
    }
    
    /**
     * Este método no hace nada. Existe porque es necesario implementarlo por ser
     * abstracto en una clase superior.
     * @param st No utilizado.
     * @since 1.0
     */
    public void ponerSubtipo(int st) {
        // No se hace nada
    }
    
    /**
     * Este método permite establecer el tipo de LSP al que se refiere el paquete TLDP.
     * Este método y el valor especificado se utilizan exclusivamente para la
     * simulación visual y en ningún caso se puede entender que el valor forma parte
     * del protocolo TLDP.
     * @param tlsp LSP_NORMAL o LSP_BACKUP, dependiendo de si se refiere a un LSP tradicional o a
     * un LSP de respaldo.
     * @since 1.0
     */    
    public void ponerEsParaBackup(boolean tlsp) {
        this.tipoLSP = tlsp;
    }
    
    /**
     * Este método permite obtener el tipo de LSP al que se refiere el paquete TLDP.
     * Este método y el valor devuelto se utilizan exclusivamente para la
     * simulación visual y en ningún caso se puede entender que el valor forma parte
     * del protocolo TLDP.
     * @return LSP_NORMAL o LSP_BACKUP, dependiendo de si se refiere a un LSP tradicional o a
     * un LSP de respaldo.
     * @since 1.0
     */    
    public boolean obtenerEsParaBackup() {
        return this.tipoLSP;
    }
    
    /**
     * Este método permite obtener por dónde ha llegado el paquete TLDP al nodo.
     * @since 1.0
     * @return ENTRADA, SALIDA o SALIDA_BACKUP, según el lugar por donde haya llegado el nodo.
     */    
    public int obtenerEntradaPaquete() {
        if (this.entradaPaquete == this.ADELANTE)
            return this.ENTRADA;
        if (this.entradaPaquete == this.ATRAS)
            return this.SALIDA;
        if (this.entradaPaquete == this.ATRAS_BACKUP)
            return this.SALIDA_BACKUP;
        return this.ENTRADA;
    }
    
    /**
     * Este método permite establecer por donde va a salir del nodo el paquete TLDP.
     * @since 1.0
     * @param ep ADELANTE, ATRAS o ATRAS_BACKUP, dependiendo de por donde salga el paquete del
     * nodo.
     */    
    public void ponerSalidaPaquete(int ep) {
        this.entradaPaquete = ep;
    }
    
    /**
     * Esta constante indica que el paquete TLDP será enviado bien por la salida de
     * Backup o por la salida principal.
     * @since 1.0
     */    
    public static final int ADELANTE = -1;
    /**
     * Esta constante indica que el paquete TLDP será enviado por la entrada pero por
     * un LSP principal.
     * @since 1.0
     */    
    public static final int ATRAS = -2;
    /**
     * Esta constante indica que el paquete TLDP será enviado por la entrada pero por
     * un LSP de backup.
     * @since 1.0
     */    
    public static final int ATRAS_BACKUP = -3;
    /**
     * Esta constante indica que el paquete TLDP ha llegado al nodo por el puerto de
     * entrada.
     * @since 1.0
     */    
    public static final int ENTRADA = -1;
    /**
     * Esta constante indica que el paquete TLDP ha llegado al nodo por el puerto de
     * salida principal.
     * @since 1.0
     */    
    public static final int SALIDA = -2;
    /**
     * Esta constante indica que el paquete TLDP ha llegado al nodo por el puerto de
     * salida de backup.
     * @since 1.0
     */    
    public static final int SALIDA_BACKUP = -3;
    
    private int entradaPaquete;
    
    /** Este atributo privado simula la carga aportada por el tamaño de los datos TCP
     * al paquete.
     * @since 1.0
     */
    private TDatosTCP datosTCP;
    /** Este atributo privado simula los datos del paquete TLDP, de donde se puede
     * obtener los mensajes de señalización necesarios.
     * @since 1.0
     */
    private TDatosTLDP datosTLDP;
    
    private boolean tipoLSP;
}
