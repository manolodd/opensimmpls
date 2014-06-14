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
package simMPLS.hardware.tldp;

/**
 * Esta clase implementa una entrada dentro de la matriz de conmutaci�n. Esto
 * comprende un compendio entre la tabla Label Incoming Map (LIP), Next Hop
 * Label Forwarding Entry (NHLFE) y FEC_ENTRY to NHLFE (FTN). Es necesario para
 * la conmutaci�n y el enrutamiento.
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A
 * href="http://www.ManoloDominguez.com"
 * target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSwitchingMatrixEntry {

    /**
     * Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TEntradaMatrizConmutacion.
     *
     * @since 1.0
     */
    public TSwitchingMatrixEntry() {
        this.incomingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.labelOrFEC = TSwitchingMatrixEntry.UNDEFINED;
        this.outgoingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.backupOutgoingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.label = TSwitchingMatrixEntry.UNDEFINED;
        this.backupLabel = TSwitchingMatrixEntry.UNDEFINED;
        this.labelStackOperation = TSwitchingMatrixEntry.UNDEFINED;
        this.entryType = TSwitchingMatrixEntry.LABEL_ENTRY;
        this.tailEndIPAddress = "";
        this.localTLDPSessionID = TSwitchingMatrixEntry.UNDEFINED;
        this.upstreamTLDPSessionID = TSwitchingMatrixEntry.UNDEFINED;
        this.timeout = TSwitchingMatrixEntry.TIMEOUT;
        this.labelRequestAttempts = TSwitchingMatrixEntry.LABEL_REQUEST_ATTEMPTS;
        this.isRequestForBackupLSP = false;
    }

    /**
     * Este m�todo comprueba si segun la informaci�n de la entrada, hay un LSP
     * de backup establecido.
     *
     * @return TRUE, si hay un LSP de backup establecido. FALSE en caso
     * contrario.
     * @since 1.0
     */
    public boolean backupLSPHasBeenEstablished() {
        if (this.backupOutgoingPortID >= 0) {
            if (this.backupLabel > 15) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo comprueba si se debe eliminar el LSP de backup aunque este no
     * est� establecido (porque, por ejemplo, se est� estableciendo).
     *
     * @return TRUE, si se debe iniciar una eliminaci�n del LSP de backup. FALSE
     * en caso contrario.
     * @since 1.0
     */
    public boolean backupLSPShouldBeRemoved() {
        if (backupLSPHasBeenEstablished()) {
            if (this.backupLabel == TSwitchingMatrixEntry.LABEL_REQUESTED) {
                return true;
            }
            if (this.backupLabel == TSwitchingMatrixEntry.REMOVING_LABEL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Este m�todo, usado desde un nodo activo, comprueba si dicho nodo activo
     * es el iniciador del LSP de backup o no.
     *
     * @return TRUE, si el nodo ACTIVO que realiza la consulta es el iniciador
     * del LSP de backup. FALSE en caso contrario.
     * @since 1.0
     */
    public boolean amIABackupLSPHeadEnd() {
        if (this.isRequestForBackupLSP) {
            return false;
        }
        return true;
    }

    /**
     * Este m�todo dado un portID de entrada de un LSP, obtiene el portID
     * opuesto. Si es la entrada, indica la salida. Si es la salida, devuelve la
     * entrada.
     *
     * @param portID Puerto cuyo opuesto se desea averiguar.
     * @return EEl portID opuesto al pasado por par�metros.
     * @since 1.0
     */
    public int getOppositePortID(int portID) {
        if (this.incomingPortID == portID) {
            return this.outgoingPortID;
        }
        if (this.outgoingPortID == portID) {
            return this.incomingPortID;
        }
        if (this.backupOutgoingPortID == portID) {
            return this.incomingPortID;
        }
        return this.incomingPortID;
    }

    /**
     * Este m�todo averigua si para esta entrada, el solicitante que envi� el
     * paquete TLDP va a crear un LSP de respaldo o no. Se usa s�lo con fines de
     * simulaci�n visual.
     *
     * @return TRUE, si la petici�n tiene como finalidad un LSP de respaldo.
     * FALSE en caso contrario.
     * @since 1.0
     */
    public boolean aBackupLSPHasBeenRequested() {
        return this.isRequestForBackupLSP;
    }

    /**
     * Este m�todo establece si para esta entrada, el solicitante que envi� el
     * paquete TLDP va a crear un LSP de respaldo o no. Se usa s�lo con fines de
     * simulaci�n visual. contrario.
     *
     * @since 1.0
     * @param isRequestForBackupLSP TRUE, si la entrada corresponde a un LSP de
     * respaldo. FALSE en caso contrario.
     */
    public void setEntryIsForBackupLSP(boolean isRequestForBackupLSP) {
        this.isRequestForBackupLSP = isRequestForBackupLSP;
    }

    /**
     * Este m�todo reestablece a cero el n�mero de labelRequestAttempts que se
     * llevan realizados de petici�n TLDP.
     *
     * @since 1.0
     */
    public void resetAttempts() {
        this.labelRequestAttempts = TSwitchingMatrixEntry.LABEL_REQUEST_ATTEMPTS;
    }

    /**
     * Este m�todo decrementa el n�mero de labelRequestAttempts que se llevan
     * realizados de petici�n TLDP.
     *
     * @since 1.0
     */
    public void decreaseAttempts() {
        if (this.labelRequestAttempts > 0) {
            this.labelRequestAttempts--;
        }
        if (this.labelRequestAttempts < 0) {
            this.labelRequestAttempts = 0;
        }
    }

    /**
     * Este m�todo comprueba el n�mero de labelRequestAttempts que se llevan
     * realizados de petici�n TLDP, para ver si a�n quedan labelRequestAttempts.
     *
     * @since 1.0
     * @return TRUE, si todav�a se puede reintentar la comunicaci�n TLDP. FALSE
     * en caso contrario.
     */
    public boolean areThereAvailableAttempts() {
        if (this.labelRequestAttempts > 0) {
            return true;
        }
        return false;
    }

    /**
     * Este m�todo reestablece a su valor original el tiempo que falta para que
     * expire el intento de comunicaci�n y haya que reintentarlo de nuevo.
     *
     * @since 1.0
     */
    public void resetTimeOut() {
        this.timeout = TSwitchingMatrixEntry.TIMEOUT;
    }

    /**
     * Este m�todo decrementa el tiempo que falta para que expire el intento de
     * comunicaci�n y haya que reintentarlo de nuevo.
     *
     * @since 1.0
     * @param nanosecondsToDecrease El n�mero de nanosegundos que se va a
     * descontar del tiempo de cr�dito del timeout.
     */
    public void decreaseTimeOut(int nanosecondsToDecrease) {
        if (this.timeout > 0) {
            this.timeout -= nanosecondsToDecrease;
        }
        if (this.timeout < 0) {
            this.timeout = 0;
        }
    }

    /**
     * Este m�todo vuelve a realizar las peticiones pendientes para las cuales
     * queden labelRequestAttempts de petici�n y para las cuales haya vencido el
     * timeout.
     *
     * @since 1.0
     * @return TRUE, si las peticiones se hacen de nuevo correctamente. FALSE,
     * en caso contrario.
     */
    public boolean retryExpiredTLDPRequests() {
        if (areThereAvailableAttempts()) {
            if (timeout == 0) {
                if ((this.label == TSwitchingMatrixEntry.LABEL_REQUESTED) || (this.label == TSwitchingMatrixEntry.REMOVING_LABEL)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Este m�todo permite especificar cual ser� el puerto de entrada en la
     * entrada de la tabla.
     *
     * @param incomingPortID El puerto de entrada deseado.
     * @since 1.0
     */
    public void setIncomingPortID(int incomingPortID) {
        this.incomingPortID = incomingPortID;
    }

    /**
     * Este m�todo permite obtener el puerto de entrada en la entrada de la
     * tabla.
     *
     * @return El puerto de entrada seg�n el valor de los atributos.
     * @since 1.0
     */
    public int getIncomingPortID() {
        return this.incomingPortID;
    }

    /**
     * Este m�todo permite especificar cual ser� el valor de la etiqueta o el
     * FEC_ENTRY de entrada en la entrada de la tabla.
     *
     * @param labelOrFEC El valor de FEC_ENTRY o de la etiqueta de entrada.
     * @since 1.0
     */
    public void setLabelOrFEC(int labelOrFEC) {
        this.labelOrFEC = labelOrFEC;
    }

    /**
     * Este m�todo permite obtener el valor de la etiqueta o el FEC_ENTRY de
     * entrada en la entrada de la tabla.
     *
     * @return El valor de la etiqueta o FEC_ENTRY de entrada.
     * @since 1.0
     */
    public int getLabelOrFEC() {
        return this.labelOrFEC;
    }

    /**
     * Este m�todo permite obtener el valor del puerto de salida en la entrada
     * de la tabla.
     *
     * @param outgoingPortID El puerto de salida deseado.
     * @since 1.0
     */
    public void setOutgoingPortID(int outgoingPortID) {
        this.outgoingPortID = outgoingPortID;
    }

    /**
     * Este m�todo permite obtener el valor del puerto de salida en la entrada
     * de la tabla, para un camino de respaldo.
     *
     * @param backupOutgoingPortID El puerto de salida deseado.
     * @since 1.0
     */
    public void setBackupOutgoingPortID(int backupOutgoingPortID) {
        this.backupOutgoingPortID = backupOutgoingPortID;
    }

    /**
     * Este m�todo permite obtener el valor del puerto de salida en la entrada
     * de la tabla.
     *
     * @return El valor del puerto de salida.
     * @since 1.0
     */
    public int getOutgoingPortID() {
        return this.outgoingPortID;
    }

    /**
     * Este m�todo permite obtener el valor del puerto de salida en la entrada
     * de la tabla, para un LSP de respaldo.
     *
     * @return El valor del puerto de salida.
     * @since 1.0
     */
    public int getBackupOutgoingPortID() {
        return this.backupOutgoingPortID;
    }

    /**
     * Este m�todo establece el LSP de respaldo como LSP principal y anula
     * posteriormente el LSP de respaldo, para que se vuelva a solicitar la
     * creaci�n de uno.
     *
     * @since 1.0
     */
    public void switchToBackupLSP() {
        this.outgoingPortID = this.backupOutgoingPortID;
        this.label = this.backupLabel;
        this.backupOutgoingPortID = TSwitchingMatrixEntry.UNDEFINED;
        this.backupLabel = TSwitchingMatrixEntry.UNDEFINED;
        this.isRequestForBackupLSP = false;
    }

    /**
     * Este m�todo permite poner el valor de la etiqueta de salida que se
     * utilizar�.
     *
     * @param outgoingLabel La etiqueta de salida.
     * @since 1.0
     */
    public void setOutgoingLabel(int outgoingLabel) {
        this.label = outgoingLabel;
    }

    /**
     * Este m�todo permite poner el valor de la etiqueta de salida de backup que
     * se utilizar�.
     *
     * @param backupOutgoingLabel La etiqueta de salida.
     * @since 1.0
     */
    public synchronized void setBackupOutgoingLabel(int backupOutgoingLabel) {
        this.backupLabel = backupOutgoingLabel;
    }

    /**
     * Este m�todo permite obtener el valor de la etiqueta de salida para el LSP
     * principal.
     *
     * @return El valor de la etiqueta de salida.
     * @since 1.0
     */
    public int getOutgoingLabel() {
        return this.label;
    }

    /**
     * Este m�todo permite obtener el valor de la etiqueta de salida para el LSP
     * de backup.
     *
     * @return El valor de la etiqueta de salida de backup.
     * @since 1.0
     */
    public synchronized int getBackupOutgoingLabel() {
        return this.backupLabel;
    }

    /**
     * Este m�todo permite obtener el valor de la operaci�n que se aplicar�
     * sobre la cima de la pila de etiquetas de los paquetes entrantes en la
     * entrada de la tabla.
     *
     * @param labelStackOperation Operaci�n que se aplicar�. una de las
     * constantes definidas en la clase.
     * @since 1.0
     */
    public void setLabelStackOperation(int labelStackOperation) {
        this.labelStackOperation = labelStackOperation;
    }

    /**
     * Este m�todo permite obtener qu� operaci�n, seg�n la entrada de la tabla,
     * se aplicar� a los paquetes conmutados.
     *
     * @return La operaci�n que se aplicar�. Una de las constantes definidas en
     * la clase.
     * @since 1.0
     */
    public int getLabelStackOperation() {
        return this.labelStackOperation;
    }

    /**
     * Este m�todo permite especificar el tipo de entrada de que se trata (ILM o
     * FTN).
     *
     * @param entryType El tipo de entrada, seg�n las constantes definidas en la
     * clase.
     * @since 1.0
     */
    public void setEntryType(int entryType) {
        this.entryType = entryType;
    }

    /**
     * Este m�todo permite obtener de qu� tipo de entrada se trata.
     *
     * @return El tipo de entrada de que se trata la instancia actual. Una de
     * las constantes definidas en la clase.
     * @since 1.0
     */
    public int getEntryType() {
        return this.entryType;
    }

    /**
     * Este m�todo obtiene la IP de destino final del tr�fico conmutado.
     *
     * @return La IP del receptor final del tr�fico.
     * @since 1.0
     */
    public String getTailEndIPAddress() {
        return this.tailEndIPAddress;
    }

    /**
     * Este m�todo permite especificar la IP de destino final del tr�fico
     * conmutado.
     *
     * @param tailEndIPAddress La IP del destino final del tr�fico.
     * @since 1.0
     */
    public void setTailEndIPAddress(String tailEndIPAddress) {
        this.tailEndIPAddress = tailEndIPAddress;
    }

    /**
     * Este m�todo permite especificar el identificador de sesi�n TLDP propio.
     *
     * @param localTLDPSessionID El identificador TLDP deseado.
     * @since 1.0
     */
    public void setLocalTLDPSessionID(int localTLDPSessionID) {
        this.localTLDPSessionID = localTLDPSessionID;
    }

    /**
     * Este m�todo permite consultar cu�l es el identificador TLDP propio que
     * estamos usando.
     *
     * @return El identificador TLDP propio.
     * @since 1.0
     */
    public int getLocalTLDPSessionID() {
        return this.localTLDPSessionID;
    }

    /**
     * Este m�todo permite establecer el identificador TLDp que est� usando
     * nuestro adyadcente. De este modo se puede entender que ambos est�n
     * encadenados por la sesi�n TLDP.
     *
     * @param upstreamTLDPSessionID El identificador TLDP usado por nuestro
     * adyacente antecesor.
     * @since 1.0
     */
    public void setUpstreamTLDPSessionID(int upstreamTLDPSessionID) {
        this.upstreamTLDPSessionID = upstreamTLDPSessionID;
    }

    /**
     * Este m�todo permite obtener el identificador TLDP que est� usando nuestro
     * adyadcente.
     *
     * @return El identificador TLDP de nuestro nodo adyacente antecesor.
     * @since 1.0
     */
    public int getUpstreamTLDPSessionID() {
        return this.upstreamTLDPSessionID;
    }

    /**
     * Este m�todo indica si la entrada es v�lida o si por el contrario tiene
     * alg�n error y no debe ser tenida en consideraci�n. Se puede entender como
     * un validador de la entrada de la matriz de conmutaci�n.
     *
     * @return TRUE si la entrada es v�lida y FALSE en caso contrario.
     * @since 1.0
     */
    public boolean isAValidEntry() {
        if (this.getIncomingPortID() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getLabelOrFEC() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getOutgoingPortID() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getLocalTLDPSessionID() == TSwitchingMatrixEntry.UNDEFINED) {
            return false;
        }
        if (this.getTailEndIPAddress().equals("")) {
            return false;
        }
        if (this.getOutgoingLabel() == TSwitchingMatrixEntry.UNDEFINED) {
            if (this.getLabelStackOperation() == TSwitchingMatrixEntry.SWAP_LABEL) {
                return false;
            }
        }
        if (this.getLabelStackOperation() == TSwitchingMatrixEntry.PUSH_LABEL) {
            return false;
        }
        return true;
    }

    /**
     * Esta constante indica que la entrada de la matriz es de tipo FTN
     * (FEC_ENTRY to NHLFE).
     *
     * @since 1.0
     */
    public static final int FEC_ENTRY = 0;
    /**
     * Esta constante indica que la entrada de la matriz es de tipo ILM
     * (Incoming Label Map).
     *
     * @since 1.0
     */
    public static final int LABEL_ENTRY = 1;

    /**
     * Esta constante indica una entrad de la matriz no especifica a�n nada de
     * c�mo se ha de tratar el paquete entrante.
     *
     * @since 1.0
     */
    public static final int UNDEFINED = -1;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta
     * entrada de la matriz de conmutaci�n, se le debe poner una etiqueta.
     *
     * @since 1.0
     */
    public static final int PUSH_LABEL = -10;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta
     * entrada de la matriz de conmutaci�n, se le debe quitar una etiqueta.
     *
     * @since 1.0
     */
    public static final int POP_LABEL = -12;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta
     * entrada de la matriz de conmutaci�n, se le debe modificar su etiqueta
     * superior.
     *
     * @since 1.0
     */
    public static final int SWAP_LABEL = -11;
    /**
     * Esta constante indica que al paquete entrante que coincida con esta
     * entrada de la matriz de conmutaci�n, se le debe conmutar sin
     * modificaciones.
     *
     * @since 1.0
     */
    public static final int NOOP = -13;

    /**
     * Esta constante indica que a�n no se ha configurado el valor de la
     * etiqueta en una entrada de la matriz, pero se est� en espera de que el
     * nodo adyacente responda a una petici�n.
     *
     * @since 1.0
     */
    public static final int LABEL_REQUESTED = -33;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha denegado la etiqueta para recibir tr�fico.
     *
     * @since 1.0
     */
    public static final int LABEL_DENIED = -31;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha concedido la etiqueta para recibir tr�fico.
     *
     * @since 1.0
     */
    public static final int LABEL_ASSIGNED = -30;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha eliminado una etiqueta que hab�a concedido anteriormente
     * para recibir tr�fico. Se est� en proceso de destrucci�n el LSP.
     *
     * @since 1.0
     */
    public static final int REMOVING_LABEL = -32;
    /**
     * Esta constante indica que para una entrada concreta de la matriz, el nodo
     * adyacente ha eliminado una etiqueta que hab�a concedido anteriormente
     * para recibir tr�fico. Se est� en proceso de destrucci�n el LSP.
     *
     * @since 1.0
     */
    public static final int LABEL_WITHDRAWN = -34;

    /**
     * Esta constante indica que no hay camino al destino para el cual se ha
     * solicitado una etiqueta. El puerto de salida, por tanto, aparecer� con
     * este valor.
     *
     * @since 1.0
     */
    public static final int PATH_UNAVAILABLE = -20;

    /**
     * Este atributo es el puerto de entrada de la tabla de
     * encaminamiento/conmutaci�n.
     *
     * @since 1.0
     */
    private int incomingPortID;
    /**
     * Este atributo es el valor de la etiqueta de entrada (entrada ILM) o del
     * FEC_ENTRY de entrada (entrada FTN) de entrada de la tabla de
     * encaminamiento/conmutaci�n.
     *
     * @since 1.0
     */
    private int labelOrFEC;
    /**
     * Este atributo es el puerto de salida de la tabla de
     * encaminamiento/conmutaci�n.
     *
     * @since 1.0
     */
    private int outgoingPortID;
    /**
     * Este atributo es el puerto de salida de la tabla de
     * encaminamiento/conmutaci�n, para un LSP de respaldo.
     *
     * @since 1.0
     */
    private int backupOutgoingPortID;
    /**
     * Este atributo es la etiqueta de salida de la tabla de
     * encaminamiento/conmutaci�n.
     *
     * @since 1.0
     */
    private int label;
    /**
     * Este atributo es la etiqueta de salida de backup de la tabla de
     * encaminamiento/conmutaci�n.
     *
     * @since 1.0
     */
    private int backupLabel;
    /**
     * Este atributo es quien indica la operaci�n que hay que hacer sobre la
     * cima de la pila de etiquetas.
     *
     * @since 1.0
     */
    private int labelStackOperation;
    /**
     * Este atributo indica si la entrada es de tipo ILM o FTN.
     *
     * @since 1.0
     */
    private int entryType;
    /**
     * Este atributo almacena la IP destino final del flujo que se va a
     * encaminar/conmutar.
     *
     * @since 1.0
     */
    private String tailEndIPAddress;
    /**
     * Este atributo es identificador de sesi�n LDP que tiene esta entrada.
     *
     * @since 1.0
     */
    private int localTLDPSessionID;
    /**
     * Este atributo es identificador de sesi�n LDP que tiene el nodo adyacente
     * antecesor en la comunicaci�n y que servir� para retornar etiquetas sin
     * ambiguedad hacia atr�s.
     *
     * @since 1.0
     */
    private int upstreamTLDPSessionID;
    /**
     * Este atributo almacena si una petici�n entrante se refiere a un LSP de
     * respaldo en el solicitante o no.
     *
     * @since 1.0
     */
    private boolean isRequestForBackupLSP;

    private int timeout;
    private int labelRequestAttempts;

    private static final int TIMEOUT = 50000;
    private static final int LABEL_REQUEST_ATTEMPTS = 3;
}
