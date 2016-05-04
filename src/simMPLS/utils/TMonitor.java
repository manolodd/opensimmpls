/* 
 * Copyright 2015 (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @since 2.0
     */
    public TMonitor() {
        bloqueado = false;
    }
    
    /** Cuando un hilo llama a este m�todo del monitor queda bloqueado en �l si hay otro
     * hilo que a�n no lo ha liberado y si no, es �l el que bloquea este monitor para
     * parar a otros hilos. El m�todo est� <B>sincronizado</B>.
     * @since 2.0
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
     * @since 2.0
     */
    public synchronized void unLock() {
        bloqueado = false;
        notify();
    }
    
    /** Atributo interno que especifica en qu� estado se encontrar� el hilo que llame a
     * este monitor.
     * @since 2.0
     */
    private boolean bloqueado;
}
