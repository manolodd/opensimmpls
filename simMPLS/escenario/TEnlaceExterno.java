//**************************************************************************
// Nombre......: TEnlaceExterno.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un enlace entre dos nodos de la topo-
// ............: logía. El enlace será Externo cuya única diferencia con
// ............: otros enlaces es el tipo que sierve para que se muestre de
// ............: forma distinta en el simulador.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import java.util.*;
import simMPLS.escenario.*;
import simMPLS.electronica.reloj.*;
import simMPLS.utiles.*;
import simMPLS.protocolo.*;
import org.jfree.chart.*;
import org.jfree.data.*;

/**
 * Esta clase implementa un enlace de la topología que será externo al dominio
 * MPLS.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEnlaceExterno extends TEnlaceTopologia implements IEventoRelojListener, Runnable {

    /**
     * Crea una nueva instancia de TEnlaceExterno
     * @param identificador Identificador único para este elemento en la topología.
     * @param il Generador de identificadores para los eventos que genere este enlace externo.
     * @param t Topologia en la que se encuentra este enlace externo.
     * @since 1.0
     */
    public TEnlaceExterno(int identificador, TIdentificadorLargo il, TTopologia t) {
        super(identificador, il, t);
        paso=0;
    }

    /**
     * Este método devuelve el tipo el enlace.
     * @return TEnlaceTopologia.EXTERNO, indicando que es un nodo externo.
     * @since 1.0
     */    
    public int obtenerTipo() {
        return super.EXTERNO;
    }

    /**
     * Este método recibe eventos de sincronización del reloj del simulador, que lo
     * sincroniza todo.
     * @param evt Evento de sincronización que el reloj del simulador envía a este enlace externo.
     * @since 1.0
     */    
    public void capturarEventoReloj(TEventoReloj evt) {
        this.ponerDuracionTic(evt.obtenerDuracionTic());
        this.ponerInstanteDeTiempo(evt.obtenerLimiteSuperior());
        paso = evt.obtenerDuracionTic();
        this.iniciar();
    }

    /**
     * Este método establece si el enlace se puede considerar como caido o no.
     * @param ec TRUE, indica que queremos que el enlace caiga. FALSE indica que no lo queremos o
     * que queremos que se levante si está caido.
     * @since 1.0
     */    
    public void ponerEnlaceCaido(boolean ec) {
        enlaceCaido = ec;
        if (ec) {
            try {
                this.generarEventoSimulacion(new TESEnlaceCaido(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo()));
                this.cerrojo.bloquear();
                TPDU paquete = null;
                TEntradaBufferEnlace ebe = null;
                Iterator it = this.buffer.iterator();
                while (it.hasNext()) {
                    ebe = (TEntradaBufferEnlace) it.next();
                    paquete = ebe.obtenerPaquete();
                    if (paquete != null) {
                        if (ebe.obtenerDestino() == 1) {
                            this.generarEventoSimulacion(new TESPaqueteDescartado(this.obtenerExtremo2(), this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        } else if (ebe.obtenerDestino() == 2) {
                            this.generarEventoSimulacion(new TESPaqueteDescartado(this.obtenerExtremo1(), this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), paquete.obtenerSubTipo()));
                        }
                    }
                    it.remove();
                }
                this.cerrojo.liberar();
            } catch (EDesbordeDelIdentificador e) {
                e.printStackTrace(); 
            }
        } else {
            try {
                this.generarEventoSimulacion(new TESEnlaceRecuperado(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo()));
            } catch (EDesbordeDelIdentificador e) {
                e.printStackTrace(); 
            }
        }
    }
    
    /**
     * Este método se ejecuta cuando el hilo principal del enlace externo se ponne en
     * funcionamiento. Es el núcleo del enlace externo.
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
     * Este método toma todos los paquetes que en ese momento se encuentren circulando
     * por el enlace externo y los avanza por el mismo hacia su destino.
     * @since 1.0
     */    
    public void actualizarTiemposDeEspera() {
        cerrojo.bloquear();
        Iterator it = buffer.iterator();
        while (it.hasNext()) {
            TEntradaBufferEnlace ebe = (TEntradaBufferEnlace) it.next();
            ebe.restarTiempoPaso(paso);
            long pctj = this.obtenerPorcentajeTransito(ebe.obtener100x100(), ebe.obtenerTiempoEspera());
            if (ebe.obtenerDestino() == 1)
                pctj = 100 - pctj;
            try {
                this.generarEventoSimulacion(new TESPaqueteEnTransito(this, this.gILargo.obtenerNuevo(), this.obtenerInstanteDeTiempo(), ebe.obtenerPaquete().obtenerSubTipo(), pctj));
            } catch (EDesbordeDelIdentificador e) {
                e.printStackTrace(); 
            }
        }
        cerrojo.liberar();
    }

    /**
     * Este método toma todos los paquetes que se encuentren circulando por el enlace
     * externo y detecta todos aquellos que ya han llegado al destino.
     * @since 1.0
     */    
    public void adelantarPaquetesEnTransito() {
        cerrojo.bloquear();
        Iterator it = buffer.iterator();
        while (it.hasNext()) {
            TEntradaBufferEnlace ebe = (TEntradaBufferEnlace) it.next();
            if (ebe.obtenerTiempoEspera() <= 0) {
                this.cerrojoLlegados.bloquear();
                bufferLlegadosADestino.add(ebe);
                this.cerrojoLlegados.liberar();
            }
        }
        it = buffer.iterator();
        while (it.hasNext()) {
            TEntradaBufferEnlace ebe = (TEntradaBufferEnlace) it.next();
            if (ebe.obtenerTiempoEspera() <= 0)
                it.remove();
        }
        cerrojo.liberar();
    }

    /**
     * Este método toma todos los paquetes que han llegado al destino y realiza la
     * insercioón de los mismos en el puerto correspondiente de dicho destino.
     * @since 1.0
     */    
    public void pasarPaquetesADestino() {
        this.cerrojoLlegados.bloquear();
        Iterator it = bufferLlegadosADestino.iterator();
        while (it.hasNext())  {
            TEntradaBufferEnlace ebe = (TEntradaBufferEnlace) it.next();
            if (ebe.obtenerDestino() == TEnlaceTopologia.EXTREMO1) {
                TNodoTopologia nt = this.obtenerExtremo1();
                nt.ponerPaquete(ebe.obtenerPaquete(), this.obtenerPuertoExtremo1());
            } else {
                TNodoTopologia nt = this.obtenerExtremo2();
                nt.ponerPaquete(ebe.obtenerPaquete(), this.obtenerPuertoExtremo2());
            }
            it.remove();
        }
        this.cerrojoLlegados.liberar();
    }
    
    /**
     * Este método obtiene el peso del enlace externos que debe usar el algoritmo de
     * routing para calcular rutas.
     * @return El peso del enlace. En el enlace externo es el retardo.
     * @since 1.0
     */    
    public long obtenerPeso() {
        long peso = this.obtenerDelay();
        return peso; 
    }

    /**
     * Este método devuelve si el enlace externo está bien configurado o no.
     * @return TRUE, si la configuración actual del enlace es correcta. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public boolean estaBienConfigurado() {
        return false;
    }
    
    /**
     * Este método comprueba si el valor de todos los atributos configurables del
     * enlace externo es válido o no.
     * @param t Topología dentro de la cual se encuentra este enlace externo.
     * @return CORRECTA, si la configuración es correcta. Un codigo de error en caso contrario.
     * @since 1.0
     */    
    public int comprobar(TTopologia t) {
        return 0;
    }
    
    /**
     * Este método transforma en un mensaje legible el código de error devuelto por el
     * método <I>comprobar(...)</I>
     * @param e El codigo de error que se quiere transformar.
     * @return El mensaje textual correspondiente a ese mensaje de error.
     * @since 1.0
     */    
    public String obtenerMensajeError(int e) {
        return null;
    }
    
    /**
     * Este método transforma el enlace externo en un representación de texto que se
     * puede almacenar en disco sin problemas.
     * @return El equivalente en texto del enlace externo completo.
     * @since 1.0
     */    
    public String serializar() {
        String cadena = "#EnlaceExterno#";
        cadena += this.obtenerIdentificador();
        cadena += "#";
        cadena += this.obtenerNombre().replace('#', ' ');
        cadena += "#";
        cadena += this.obtenerMostrarNombre();
        cadena += "#";
        cadena += this.obtenerDelay();
        cadena += "#";
        cadena += this.obtenerExtremo1().obtenerIP();
        cadena += "#";
        cadena += this.obtenerPuertoExtremo1();
        cadena += "#";
        cadena += this.obtenerExtremo2().obtenerIP();
        cadena += "#";
        cadena += this.obtenerPuertoExtremo2();
        cadena += "#";
        return cadena;
    }
    
    /**
     * Este método toma la representación textual de un enlace externo completo y
     * configura el objeto con los valores que obtiene.
     * @param elemento Enlace externo en su representación serializada.
     * @return TRUE, si se deserializa correctamente, FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean desSerializar(String elemento) {
        TConfigEnlace configEnlace = new TConfigEnlace();
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
        TNodoTopologia ex1 = this.obtenerTopologia().obtenerNodo(IP1);
        TNodoTopologia ex2 = this.obtenerTopologia().obtenerNodo(IP2);
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
     * Este método reinicia los atributos de la clase, dejando la instancia como si se
     * acabase de crear por el constructor.
     * @since 1.0
     */    
    public void reset() {
        this.cerrojo.bloquear();
        Iterator it = this.buffer.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojo.liberar();
        this.cerrojoLlegados.bloquear();
        it = this.bufferLlegadosADestino.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.cerrojoLlegados.liberar();
        ponerEnlaceCaido(false);
    }
    
    public long obtenerPesoRABAN() {
        return this.obtenerPeso(); 
    }
    
    private long paso;
}
