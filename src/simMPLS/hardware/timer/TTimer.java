//**************************************************************************
// Nombre......: TTimer.java
// Proyecto....: Open SimMPLS
// Descripci�n.: Clase que implementa un reloj que emite eventos de sincro-
// ............: nizaci�n que son usados por todos los elementos de la to-
// ............: polog�a para llevar a cabo la simulaci�n.
// Fecha.......: 19/01/2004
// Autor/es....: Manuel Dom�nguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.hardware.timer;

import simMPLS.utils.EDesbordeDelIdentificador;
import simMPLS.utils.TActualizadorDeProgreso;
import simMPLS.utils.TIdentificadorLargo;
import simMPLS.scenario.TTopologyElement;
import simMPLS.scenario.TInternalLink;
import simMPLS.scenario.TExternalLink;
import simMPLS.scenario.TTopologyLink;
import simMPLS.scenario.TLERNode;
import simMPLS.scenario.TSenderNode;
import simMPLS.scenario.TReceiverNode;
import simMPLS.scenario.TLSRANode;
import simMPLS.scenario.TLSRNode;
import simMPLS.scenario.TLERANode;
import simMPLS.scenario.TTopologyNode;
import java.util.*;
import javax.swing.*;

/** Esta clase implementa un reloj que sincronizar� toda la simulaci�n desde el
 * comienzo hasta el final.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TTimer implements Runnable {

    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de TReloj.
     * @since 1.0
     */
    public TTimer() {
        hilo = null;
        nodosSuscriptores = new TreeSet();
        enlacesSuscriptores = new TreeSet();
        suscriptorProgreso = null;
        generadorIdentificadorLargo = new TIdentificadorLargo();
        tActual = new TTimestamp(0, 0);
        tAnterior = new TTimestamp(0, 0);
        tLimite = new TTimestamp(0, 100000);
        tActualAux = new TTimestamp(0, 0);
        tAnteriorAux = new TTimestamp(0, 0);
        tLimiteAux = new TTimestamp(0, 100000);
        paso = 1000;
        enFuncionamiento = false;
        fin = true;
        pausa = false;
    }
    
    /**
     * Este m�todo reinicia el reloj y lo deja como si acabase de ser creado por el
     * constructor de la clase.
     * @since 1.0
     */    
    public void reset() {
        tActual = new TTimestamp(0, 0);
        tAnterior = new TTimestamp(0, 0);
        enFuncionamiento = false;
        generadorIdentificadorLargo.reset();
        fin = true;
        pausa = false;
        lanzarEventoProgreso();        
    }

    /** Este m�todo permite establecer el l�mite temporal a partir del cual el reloj se
     * parar� y dejar� de emitir eventos.
     * @param l Marca de tiempo indicando el l�mite donde el reloj (y por tanto la simulacion) se parar�.
     * @since 1.0
     */    
    public void ponerLimite(TTimestamp l) {
        tLimite.ponerMilisegundo(l.obtenerMilisegundo());
        tLimite.ponerNanosegundo(l.obtenerNanosegundo());
    }

    /** Este m�todo permite establecer cada cuanto tiempo debe generar eventos el reloj.
     * @since 1.0
     * @param p El intervalo de tiempo que deseamos que pase entre que el reloj genera un evento
     * y el siguiente.
     */    
    public void ponerPaso(int p) {
        paso = p;
    }

    /** Este evento permite que se pueda suscribir para recibir eventos de reloj
     * cualquier elemento de la topolog�a.
     * @param suscriptor Elemento de la topolog�a que queremos suscribir para recibir eventos de reloj.
     * @since 1.0
     */    
    public void addListenerReloj(TTopologyElement suscriptor) {
        if (suscriptor.obtenerTipoElemento() == TTopologyElement.ENLACE)
            enlacesSuscriptores.add(suscriptor);
        else
            nodosSuscriptores.add(suscriptor);
    }

    /** Este evento permite eliminar la suscripci�n uno de los elementos de la topolog�a
     * que se encuentre suscrito para recibir eventos de reloj.
     * @param suscriptor El elemento de la topolog�a cuya suscripci�n para recibir eventos de reloj
     * deseamos eliminar.
     * @since 1.0
     */    
    public void removeListenerReloj(TTopologyElement suscriptor) {
        if (suscriptor.obtenerTipoElemento() == TTopologyElement.ENLACE) {
            Iterator ite = enlacesSuscriptores.iterator();
            TTopologyLink e;
            TTopologyLink parametro = (TTopologyLink) suscriptor;
            while (ite.hasNext()) {
                e = (TTopologyLink) ite.next();
                if (e.obtenerIdentificador() == parametro.obtenerIdentificador()) {
                    ite.remove();
                }
            }
        }
        else {
            Iterator itn = nodosSuscriptores.iterator();
            TTopologyNode n;
            TTopologyNode parametro = (TTopologyNode) suscriptor;
            while (itn.hasNext()) {
                n = (TTopologyNode) itn.next();
                if (n.obtenerIdentificador() == parametro.obtenerIdentificador()) {
                    itn.remove();
                }
            }
        }
    }

    /** Este m�todo permite eliminar la suscripci�n de uno ovarios elementos de la
     * topolog�a que se hayan marcado para desuscribirse desde cualquier otra parte del
     * programa. As� se evita una excepci�n de acceso concurrente.
     * @since 1.0
     */    
    public void purgarListenerReloj() {
        Iterator ite = enlacesSuscriptores.iterator();
        TTopologyLink e;
        while (ite.hasNext()) {
            e = (TTopologyLink) ite.next();
            if (e.obtenerPurgar()) {
                ite.remove();
            }
        }
        Iterator itn = nodosSuscriptores.iterator();
        TTopologyNode n;
        while (itn.hasNext()) {
            n = (TTopologyNode) itn.next();
            if (n.obtenerPurgar()) {
                itn.remove();
            }
        }
    }

    /** Este elemento permite a un actualizador de progreso suscribirse para recibir
     * eventos de progresi�n.
     * @param ap El actualizador de progreso que deseamos susccribir.
     * @throws EProgressSingleSubscriber Excepci�n que se lanza cuando se intenta suscribir nu actualizador de progreso y
     * ya hay uno suscrito. S�lo se permite un actualizador de progreso suscrito en un
     * momento dado.
     * @since 1.0
     */    
    public void addListenerProgreso(TActualizadorDeProgreso ap) throws EProgressSingleSubscriber {
        if (suscriptorProgreso == null) {
            suscriptorProgreso = ap;
        } else {
            throw new EProgressSingleSubscriber();
        }
    }

    /** Este m�todo permite anular la suscripci�n de un actualizador de progreso para
     * recibir eventos de progresi�n.
     * @since 1.0
     */    
    public void removeListenerProgreso() {
        suscriptorProgreso = null;
    }

    /** Este m�todo genera y remite un evento de reloj a todos los suscriptores que lo
     * est�n esperando.
     * @since 1.0
     */    
    private void lanzarEventoReloj() {
        Iterator ite = enlacesSuscriptores.iterator();
        Iterator itn = nodosSuscriptores.iterator();
        TTopologyNode nodoAux;
        TTopologyLink enlaceAux;
        TTimestamp i = new TTimestamp(tAnterior.obtenerMilisegundo(), tAnterior.obtenerNanosegundo());
        TTimestamp s = new TTimestamp(tActual.obtenerMilisegundo(), tActual.obtenerNanosegundo());
        while (itn.hasNext()) {
            nodoAux = (TTopologyNode) itn.next();
            switch (nodoAux.obtenerTipo()) {
                case TTopologyNode.EMISOR: {
                    nodoAux = (TSenderNode) nodoAux;
                    break;
                }
                case TTopologyNode.LER: {
                    nodoAux = (TLERNode) nodoAux;
                    break;
                }
                case TTopologyNode.LERA: {
                    nodoAux = (TLERANode) nodoAux;
                    break;
                }
                case TTopologyNode.LSR: {
                    nodoAux = (TLSRNode) nodoAux;
                    break;
                }
                case TTopologyNode.LSRA: {
                    nodoAux = (TLSRANode) nodoAux;
                    break;
                }
                case TTopologyNode.RECEPTOR: {
                    nodoAux = (TReceiverNode) nodoAux;
                    break;
                }
            }
            try {
                TTimerEvent evtReloj = new TTimerEvent(this, generadorIdentificadorLargo.obtenerNuevo(), i, s);
                nodoAux.capturarEventoReloj(evtReloj);
            } catch (EDesbordeDelIdentificador e) {
                e.printStackTrace(); 
            }
        }
        while (ite.hasNext()) {
            enlaceAux = (TTopologyLink) ite.next();
            switch (enlaceAux.obtenerTipo()) {
                case TTopologyLink.EXTERNO: {
                    enlaceAux = (TExternalLink) enlaceAux;
                    break;
                }
                case TTopologyLink.INTERNO: {
                    enlaceAux = (TInternalLink) enlaceAux;
                    break;
                }
            }
            try {
                enlaceAux.capturarEventoReloj(new TTimerEvent(this, generadorIdentificadorLargo.obtenerNuevo(), i, s));
            } catch (EDesbordeDelIdentificador e) {
                e.printStackTrace(); 
            }
        }
    }

    /** Este m�todo genera y remite un evento de progresi�n al suscriptor (solo uno) que
     * lo est� esperando.
     * @since 1.0
     */    
    public void lanzarEventoProgreso() {
        int p=0;
        long total = tLimite.obtenerTotalEnNanosegundos();
        long actual = tActual.obtenerTotalEnNanosegundos();
        if (total != 0)
            p = (int) Math.round((actual*100) / total);
        try {
            if (suscriptorProgreso != null) {
                suscriptorProgreso.capturarEventoProgreso(new TProgressEvent(this, generadorIdentificadorLargo.obtenerNuevo(), p));
            }
        } catch (EDesbordeDelIdentificador e) {
            e.printStackTrace(); 
        }
    }

    /**
     * Este m�todo <B>sincronizado</B> reanuda la ejecuci�n del reloj una vez que ha sido
     * detenida.
     * @since 1.0
     */    
    public synchronized void reanudar() {
        if (hilo == null) {
            hilo = new Thread(this);
            this.hilo.start();
        } else {
            if (!hilo.isAlive()) {
                hilo = new Thread(this);
                this.hilo.start();
            }
        }
    }
    
    /** Este m�todo inicia la ejecuci�n del hilo del reloj. Se debe llamar para comenzar
     * la cuenta y generaci�n de eventos. El m�todo est� <B>sincronizado</B>.
     * @since 1.0
     */    
    public synchronized void iniciar() {
        if (hilo == null) {
            reset();
            hilo = new Thread(this);
            this.hilo.start();
        } else {
            if (!hilo.isAlive()) {
                reset();
                hilo = new Thread(this);
                this.hilo.start();
            }
        }
    }

    /**
     * Este m�todo detiene la ejecuci�n del reloj y por tanto de la simulaci�n.
     * @param p TRUE, indica que el reloj se  tiene que parar. FALSE indica que el reloj tiene
     * que reanudar su ejecuci�n.
     * @since 1.0
     */    
    public void ponerPausa(boolean p) {
        if ((pausa) && (p)) {
            // No hago nada. Ya estoy en pausa.
        } else if ((pausa) && (!p)) {
            tActual.ponerMarca(tActualAux);
            tAnterior.ponerMarca(tAnteriorAux);
            tLimite.ponerMarca(tLimiteAux);
            pausa = p;
            this.reanudar();
        } else if ((!pausa) && (p)) {
            tActualAux.ponerMarca(tActual);
            tAnteriorAux.ponerMarca(tAnterior);
            tLimiteAux.ponerMarca(tLimite);
            pausa = p;
            this.fin = true;
        } else if ((!pausa) && (!p)) {
            // No hago nada. Ya estoy funcionando.
        }
    }
    
    /**
     * Este m�todo comprueba si actualmente el reloj est� en pausa o no.
     * @since 1.0
     * @return TRUE, indica que el reloj est� en pausa. FALSE indica lo contrario.
     */    
    public boolean obtenerPausa() {
        return pausa;
    }
    
    /** Si el hilo del reloj est� activo y funcionando, este m�todo es el que se ejecuta
     * ciclicamente. B�sicamente se van contando el tiempo en pasos hasta llegar al
     * l�mite impuesto y en cada uno de los pasos se generan los eventos de reloj y de
     * progresi�n que son necesarios.
     * @since 1.0
     */    
    public void run() {
        enFuncionamiento = true;
        long totalActual;
        long totalAnterior;
        long totalLimite;
        boolean ultimoPasoDado = false;
        fin = false;
        tActual.sumarNanosegundo(paso);
        totalActual = tActual.obtenerTotalEnNanosegundos();
        totalLimite = tLimite.obtenerTotalEnNanosegundos();
        if (totalActual == totalLimite) {
            ultimoPasoDado = true;
        }
        while ((tActual.comparar(tLimite) != TTimestamp.MENOR_EL_PARAMETRO) && (!fin)) {
            // Acciones a llevar a cabo
            lanzarEventoProgreso();
            lanzarEventoReloj();
            // Acciones a llevar a cabo
            tAnterior.ponerMilisegundo(tActual.obtenerMilisegundo());
            tAnterior.ponerNanosegundo(tActual.obtenerNanosegundo());
            totalActual = tActual.obtenerTotalEnNanosegundos();
            totalLimite = tLimite.obtenerTotalEnNanosegundos();
            if (totalActual+paso > totalLimite) {
                if (!ultimoPasoDado) {
                    tActual.ponerMilisegundo(tLimite.obtenerMilisegundo());
                    tActual.ponerNanosegundo(tLimite.obtenerNanosegundo());
                    ultimoPasoDado = true;
                } else {
                    tActual.sumarNanosegundo(paso);
                }
            } else {
                tActual.sumarNanosegundo(paso);
            }
            totalActual = tActual.obtenerTotalEnNanosegundos();
            totalAnterior = tAnterior.obtenerTotalEnNanosegundos();
            if (totalAnterior == totalActual) {
                fin = true;
            }
            esperarSuscriptoresReloj();
        }
        enFuncionamiento = false;
    }

    /** Este m�todo espera a que todos los suscriptores de eventos de reloj a los que se
     * ha remitido un evento lo hayan consumido. As� se puede sincronizar y no enviar
     * m�s eventos hasta que hayan finalizado con el anterior. El m�todo est�
     * <B>sincronizado</B>.
     * @since 1.0
     */    
    private synchronized void esperarSuscriptoresReloj() {
        Iterator itn = nodosSuscriptores.iterator();
        Iterator ite = enlacesSuscriptores.iterator();
        TTopologyNode nodo;
        TTopologyLink enlace;
        while (itn.hasNext()) {
            nodo = (TTopologyNode) itn.next();
            nodo.esperarFinalizacion();
        }
        while (ite.hasNext()) {
            enlace = (TTopologyLink) ite.next();
            enlace.esperarFinalizacion();
        }
    }

    /** Este m�todo permite a cualquier objeto del simulador detectar y esperar a que el
     * reloj haya finalizado su ejecuci�n antes de hacer una operaci�n. El m�todo est�
     * <B>sincronizado</B>.
     * @since 1.0
     */    
    public synchronized void esperarFinalizacion() {
        if (hilo != null) {
            try {
                this.hilo.join();
            } catch (Exception e) {
                System.out.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TReloj.ErrorAlEsperarFinalizacionDelReloj") + e.toString());
            };
        }
    }

    /** Este m�todo permite saber si en un momento dado el reloj est� activo, esto es,
     * contando y generando eventos de reloj y simulaci�n, o por el contrario est�
     * detenido.
     * @return TRUE, si el reloj est� funcionando. FALSE si el reloj est� parado.
     * @since 1.0
     */    
    public boolean estaEnFuncionamiento() {
        return enFuncionamiento;
    }

    /** Este atributo contiene el conjunto de nodos que est�n suscritos para recibir los
     * eventos de reloj que se vayan produciendo.
     * @since 1.0
     */    
    private TreeSet nodosSuscriptores;
    /** Este atributo contiene el conjunto de enlaces que est�n suscritos para recibir los
     * eventos de reloj que se vayan produciendo.
     * @since 1.0
     */    
    private TreeSet enlacesSuscriptores;
    /** Este atributo es el actualizador de progreso que est� a la espera de los eventos
     * de progresi�n que genere esta instancia.
     * @since 1.0
     */    
    private TActualizadorDeProgreso suscriptorProgreso;
    /** Este atributo es el generador de identificadores largos que se usar� para
     * asignar a cada evento generado un identificador distinto.
     * @since 1.0
     */    
    private TIdentificadorLargo generadorIdentificadorLargo;
    /** Este atributo indica cada cuanto tiempo se debe generar un evento de reloj y de
     * progresi�n.
     */    
    private int paso;
    /** Este atributo es el hilo sobre el que correr� la instancia de TTimer.
     * @since 1.0
     */    
    private Thread hilo;    
    /** Este atributo almacena el l�mite superior que se usar� en el siguiente evento
     * de reloj que se genere.
     * @since 1.0
     */    
    private TTimestamp tActual;
    /** Este atributo almacena el l�mite inferior que se usar� en el siguiente evento
     * de reloj que se genere.
     * @since 1.0
     */    
    private TTimestamp tAnterior;
    /** Este atributo almacena el l�mite superior de la simulaci�n. Todas las
     * simulaciones comienzan en tiempo t=0 y acaban en el instante que indica este
     * atributo.
     * @since 1.0
     */    
    private TTimestamp tLimite;
    /** Este atributo indicar� si el reloj (esta instancia) est� en proceso de ejecuci�n
     * y, por tanto, generando eventos, o si por el contrario est� parado.
     * @since 1.0
     */    
    private boolean enFuncionamiento;

    private boolean fin;
    private boolean pausa;
    
    private TTimestamp tActualAux;
    private TTimestamp tAnteriorAux;
    private TTimestamp tLimiteAux;
}
