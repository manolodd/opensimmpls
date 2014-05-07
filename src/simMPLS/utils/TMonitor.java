/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.utils;

/**
 * Esta clase es un monitor gen�rico que se utilizar� para sincronizar hilos en el
 * acceso concurrente a datos. Se utilizar� frecuentemente para crear regiones
 * cr�titcas.
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TMonitor {
    
    /** Crea una nueva instancia de TMonitor para sincronizar hilos.
     * @since 1.0
     */
    public TMonitor() {
        bloqueado = false;
    }
    
    /** Cuando un hilo llama a este m�todo del monitor queda bloqueado en �l si hay otro
     * hilo que a�n no lo ha liberado y si no, es �l el que bloquea este monitor para
     * parar a otros hilos. El m�todo est� <B>sincronizado</B>.
     * @since 1.0
     */
    public synchronized void lock() {
        while (bloqueado) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        bloqueado = true;
    }
    
    /** Cuando un hilo llama a este m�todo, hace que uno de los hilos que estar�n
     * esperando que se libere el monitor, pueda acceder al mismo. El m�todo est�
     * <B>sincronizado</B>.
     * @since 1.0
     */
    public synchronized void unLock() {
        bloqueado = false;
        notify();
    }
    
    /** Atributo interno que especifica en qu� estado se encontrar� el hilo que llame a
     * este monitor.
     * @since 1.0
     */
    private boolean bloqueado;
}
