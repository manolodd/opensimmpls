package simMPLS.utiles;

/**
 * Esta clase es un monitor genérico que se utilizará para sincronizar hilos en el
 * acceso concurrente a datos. Se utilizará frecuentemente para crear regiones
 * crítitcas.
 * @version 1.0
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TMonitor {
    
    /** Crea una nueva instancia de TMonitor para sincronizar hilos.
     * @since 1.0
     */
    public TMonitor() {
        bloqueado = false;
    }
    
    /** Cuando un hilo llama a este método del monitor queda bloqueado en él si hay otro
     * hilo que aún no lo ha liberado y si no, es él el que bloquea este monitor para
     * parar a otros hilos. El método está <B>sincronizado</B>.
     * @since 1.0
     */
    public synchronized void bloquear() {
        while (bloqueado) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        bloqueado = true;
    }
    
    /** Cuando un hilo llama a este método, hace que uno de los hilos que estarán
     * esperando que se libere el monitor, pueda acceder al mismo. El método está
     * <B>sincronizado</B>.
     * @since 1.0
     */
    public synchronized void liberar() {
        bloqueado = false;
        notify();
    }
    
    /** Atributo interno que especifica en qué estado se encontrará el hilo que llame a
     * este monitor.
     * @since 1.0
     */
    private boolean bloqueado;
}
