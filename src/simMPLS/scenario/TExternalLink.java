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
package simMPLS.scenario;

import simMPLS.protocols.TPDU;
import simMPLS.hardware.timer.TTimerEvent;
import simMPLS.hardware.timer.ITimerEventListener;
import simMPLS.utils.EIdentifierGeneratorOverflow;
import simMPLS.utils.TLongIdentifier;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase implementa un enlace de la topolog�a que ser� externo al dominio
 * MPLS.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TExternalLink extends TLink implements ITimerEventListener, Runnable {

    /**
     * Crea una nueva instancia de TEnlaceExterno
     * @param identificador Identificador �nico para este elemento en la topolog�a.
     * @param il Generador de identificadores para los eventos que genere este enlace externo.
     * @param t Topologia en la que se encuentra este enlace externo.
     * @since 1.0
     */
    public TExternalLink(int identificador, TLongIdentifier il, TTopology t) {
        super(identificador, il, t);
        paso=0;
    }

    /**
     * Este m�todo devuelve el tipo el enlace.
     * @return TLink.EXTERNAL, indicando que es un nodo externo.
     * @since 1.0
     */    
    public int getLinkType() {
        return super.EXTERNAL;
    }

    /**
     * Este m�todo recibe eventos de sincronizaci�n del reloj del simulador, que lo
     * sincroniza todo.
     * @param evt Evento de sincronizaci�n que el reloj del simulador env�a a este enlace externo.
     * @since 1.0
     */    
    public void receiveTimerEvent(TTimerEvent evt) {
        this.ponerDuracionTic(evt.getStepDuration());
        this.ponerInstanteDeTiempo(evt.getUpperLimit());
        paso = evt.getStepDuration();
        this.iniciar();
    }

    /**
     * Este m�todo establece si el enlace se puede considerar como caido o no.
     * @param ec TRUE, indica que queremos que el enlace caiga. FALSE indica que no lo queremos o
     * que queremos que se levante si est� caido.
     * @since 1.0
     */    
    public void ponerEnlaceCaido(boolean ec) {
        enlaceCaido = ec;
        if (ec) {
            try {
                this.generarEventoSimulacion(new TSELinkBroken(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
                this.cerrojo.lock();
                TPDU paquete = null;
                TLinkBufferEntry ebe = null;
                Iterator it = this.buffer.iterator();
                while (it.hasNext()) {
                    ebe = (TLinkBufferEntry) it.next();
                    paquete = ebe.obtenerPaquete();
                    if (paquete != null) {
                        if (ebe.obtenerDestino() == 1) {
                            this.generarEventoSimulacion(new TSEPacketDiscarded(this.getEnd2(), this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        } else if (ebe.obtenerDestino() == 2) {
                            this.generarEventoSimulacion(new TSEPacketDiscarded(this.getEnd1(), this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), paquete.getSubtype()));
                        }
                    }
                    it.remove();
                }
                this.cerrojo.unLock();
            } catch (EIdentifierGeneratorOverflow e) {
                e.printStackTrace(); 
            }
        } else {
            try {
                this.generarEventoSimulacion(new TSELinkRecovered(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime()));
            } catch (EIdentifierGeneratorOverflow e) {
                e.printStackTrace(); 
            }
        }
    }
    
    /**
     * Este m�todo se ejecuta cuando el hilo principal del enlace externo se ponne en
     * funcionamiento. Es el n�cleo del enlace externo.
     * @since 1.0
     */    
    public void run() {
        // Acciones a llevar a cabo durante el tic.
        this.actualizarTiemposDeEspera();
        this.adelantarPaquetesEnTransito();
        this.pasarPaquetesADestino();
        // Acciones a llevar a cabo durante el tic.
    }

    /**
     * Este m�todo toma todos los paquetes que en ese momento se encuentren circulando
     * por el enlace externo y los avanza por el mismo hacia su destino.
     * @since 1.0
     */    
    public void actualizarTiemposDeEspera() {
        cerrojo.lock();
        Iterator it = buffer.iterator();
        while (it.hasNext()) {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            ebe.restarTiempoPaso(paso);
            long pctj = this.obtenerPorcentajeTransito(ebe.obtener100x100(), ebe.obtenerTiempoEspera());
            if (ebe.obtenerDestino() == 1)
                pctj = 100 - pctj;
            try {
                this.generarEventoSimulacion(new TSEPacketOnFly(this, this.longIdentifierGenerator.getNextID(), this.getAvailableTime(), ebe.obtenerPaquete().getSubtype(), pctj));
            } catch (EIdentifierGeneratorOverflow e) {
                e.printStackTrace(); 
            }
        }
        cerrojo.unLock();
    }

    /**
     * Este m�todo toma todos los paquetes que se encuentren circulando por el enlace
     * externo y detecta todos aquellos que ya han llegado al destino.
     * @since 1.0
     */    
    public void adelantarPaquetesEnTransito() {
        cerrojo.lock();
        Iterator it = buffer.iterator();
        while (it.hasNext()) {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            if (ebe.obtenerTiempoEspera() <= 0) {
                this.cerrojoLlegados.lock();
                bufferLlegadosADestino.add(ebe);
                this.cerrojoLlegados.unLock();
            }
        }
        it = buffer.iterator();
        while (it.hasNext()) {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            if (ebe.obtenerTiempoEspera() <= 0)
                it.remove();
        }
        cerrojo.unLock();
    }

    /**
     * Este m�todo toma todos los paquetes que han llegado al destino y realiza la
     * insercio�n de los mismos en el puerto correspondiente de dicho destino.
     * @since 1.0
     */    
    public void pasarPaquetesADestino() {
        this.cerrojoLlegados.lock();
        Iterator it = bufferLlegadosADestino.iterator();
        while (it.hasNext())  {
            TLinkBufferEntry ebe = (TLinkBufferEntry) it.next();
            if (ebe.obtenerDestino() == TLink.END_NODE_1) {
                TNode nt = this.getEnd1();
                nt.ponerPaquete(ebe.obtenerPaquete(), this.obtenerPuertoExtremo1());
            } else {
                TNode nt = this.getEnd2();
                nt.ponerPaquete(ebe.obtenerPaquete(), this.obtenerPuertoExtremo2());
            }
            it.remove();
        }
        this.cerrojoLlegados.unLock();
    }
    
    /**
     * Este m�todo obtiene el peso del enlace externos que debe usar el algoritmo de
     * routing para calcular rutas.
     * @return El peso del enlace. En el enlace externo es el retardo.
     * @since 1.0
     */    
    public long obtenerPeso() {
        long peso = this.obtenerDelay();
        return peso; 
    }

    /**
     * Este m�todo devuelve si el enlace externo est� bien configurado o no.
     * @return TRUE, si la configuraci�n actual del enlace es correcta. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public boolean estaBienConfigurado() {
        return false;
    }
    
    /**
     * Este m�todo comprueba si el valor de todos los atributos configurables del
     * enlace externo es v�lido o no.
     * @param t Topolog�a dentro de la cual se encuentra este enlace externo.
     * @return CORRECTA, si la configuraci�n es correcta. Un codigo de error en caso contrario.
     * @since 1.0
     */    
    public int comprobar(TTopology t) {
        return 0;
    }
    
    /**
     * Este m�todo transforma en un mensaje legible el c�digo de error devuelto por el
     * m�todo <I>comprobar(...)</I>
     * @param e El codigo de error que se quiere transformar.
     * @return El mensaje textual correspondiente a ese mensaje de error.
     * @since 1.0
     */    
    public String obtenerMensajeError(int e) {
        return null;
    }
    
    /**
     * Este m�todo transforma el enlace externo en un representaci�n de texto que se
     * puede almacenar en disco sin problemas.
     * @return El equivalente en texto del enlace externo completo.
     * @since 1.0
     */    
    public String marshall() {
        String cadena = "#EnlaceExterno#";
        cadena += this.getID();
        cadena += "#";
        cadena += this.obtenerNombre().replace('#', ' ');
        cadena += "#";
        cadena += this.obtenerMostrarNombre();
        cadena += "#";
        cadena += this.obtenerDelay();
        cadena += "#";
        cadena += this.getEnd1().getIPAddress();
        cadena += "#";
        cadena += this.obtenerPuertoExtremo1();
        cadena += "#";
        cadena += this.getEnd2().getIPAddress();
        cadena += "#";
        cadena += this.obtenerPuertoExtremo2();
        cadena += "#";
        return cadena;
    }
    
    /**
     * Este m�todo toma la representaci�n textual de un enlace externo completo y
     * configura el objeto con los valores que obtiene.
     * @param elemento Enlace externo en su representaci�n serializada.
     * @return TRUE, si se deserializa correctamente, FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean unmarshall(String elemento) {
        TLinkConfig configEnlace = new TLinkConfig();
        String valores[] = elemento.split("#");
        if (valores.length != 10) {
            return false;
        }
        this.ponerIdentificador(Integer.valueOf(valores[2]).intValue());
        configEnlace.ponerNombre(valores[3]);
        configEnlace.ponerMostrarNombre(Boolean.valueOf(valores[4]).booleanValue());
        configEnlace.ponerDelay(Integer.valueOf(valores[5]).intValue());
        String IP1 = valores[6];
        String IP2 = valores[8];
        TNode ex1 = this.obtenerTopologia().obtenerNodo(IP1);
        TNode ex2 = this.obtenerTopologia().obtenerNodo(IP2);
        if (!((ex1 == null) || (ex2 == null))) {
            configEnlace.ponerNombreExtremo1(ex1.obtenerNombre());
            configEnlace.ponerNombreExtremo2(ex2.obtenerNombre());
            configEnlace.ponerPuertoExtremo1(Integer.valueOf(valores[7]).intValue());
            configEnlace.ponerPuertoExtremo2(Integer.valueOf(valores[9]).intValue());
            configEnlace.calcularTipo(this.topologia);
        } else {
            return false;
        }
        this.configurar(configEnlace, this.topologia, false);
        return true;
    }
    
    /**
     * Este m�todo reinicia los atributos de la clase, dejando la instancia como si se
     * acabase de crear por el constructor.
     * @since 1.0
     */    
    public void reset() {
        this.cerrojo.lock();
        Iterator it = this.buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojo.unLock();
        this.cerrojoLlegados.lock();
        it = this.bufferLlegadosADestino.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoLlegados.unLock();
        ponerEnlaceCaido(false);
    }
    
    public long obtenerPesoRABAN() {
        return this.obtenerPeso(); 
    }
    
    private long paso;
}
