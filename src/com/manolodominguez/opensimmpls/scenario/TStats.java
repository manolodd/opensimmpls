/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
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
package com.manolodominguez.opensimmpls.scenario;

import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import org.jfree.data.general.AbstractDataset;

/**
 * This class is abstract. It will be implemented by subclasses to allow
 * collecting stats about the simulation
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public abstract class TStats {

    /**
     * This is the constructor of the class. As this class is abstract it will
     * be called only from the constructor of subclasses.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TStats() {
        this.statsEnabled = false;
    }

    /**
     * This method sets whether the stats are enabled or not..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param statsEnabled TRUE, if the class are enabled. Otherwise, FALSE.
     * @since 2.0
     */
    public void setStatsEnabled(boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
    }

    /**
     * This method is abstract; once implemented will return the dataset #1 of
     * the TStats that can be represented in a GUI or used by any other
     * statistics processor. Each subclass can use dataset #1 as required.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #1 of this TStats.
     * @since 2.0
     */
    public abstract AbstractDataset getDataset1();

    /**
     * This method is abstract; once implemented will return the dataset #2 of
     * the TStats that can be represented in a GUI or used by any other
     * statistics processor. Each subclass can use dataset #2 as required.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #2 of this TStats.
     * @since 2.0
     */
    public abstract AbstractDataset getDataset2();

    /**
     * This method is abstract; once implemented will return the dataset #3 of
     * the TStats that can be represented in a GUI or used by any other
     * statistics processor. Each subclass can use dataset #3 as required.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #3 of this TStats.
     * @since 2.0
     */
    public abstract AbstractDataset getDataset3();

    /**
     * This method is abstract; once implemented will return the dataset #4 of
     * the TStats that can be represented in a GUI or used by any other
     * statistics processor. Each subclass can use dataset #4 as required.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #4 of this TStats.
     * @since 2.0
     */
    public abstract AbstractDataset getDataset4();

    /**
     * This method is abstract; once implemented will return the dataset #5 of
     * the TStats that can be represented in a GUI or used by any other
     * statistics processor. Each subclass can use dataset #5 as required.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #5 of this TStats.
     * @since 2.0
     */
    public abstract AbstractDataset getDataset5();

    /**
     * This method is abstract; once implemented will return the dataset #6 of
     * the TStats that can be represented in a GUI or used by any other
     * statistics processor. Each subclass can use dataset #6 as required.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the dataset #6 of this TStats.
     * @since 2.0
     */
    public abstract AbstractDataset getDataset6();

    /**
     * This abstract method once implemented will return the title of dataset #1
     * of this TSstat. Each subclass can set Dataset #1 name as desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about Dataset #1.
     * @since 2.0
     */
    public abstract String getTitleOfDataset1();

    /**
     * This abstract method once implemented will return the title of dataset #2
     * of this TSstat. Each subclass can set Dataset #2 name as desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about Dataset #2.
     * @since 2.0
     */
    public abstract String getTitleOfDataset2();

    /**
     * This abstract method once implemented will return the title of dataset #3
     * of this TSstat. Each subclass can set Dataset #3 name as desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about Dataset #3.
     * @since 2.0
     */
    public abstract String getTitleOfDataset3();

    /**
     * This abstract method once implemented will return the title of dataset #4
     * of this TSstat. Each subclass can set Dataset #4 name as desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about Dataset #4.
     * @since 2.0
     */
    public abstract String getTitleOfDataset4();

    /**
     * This abstract method once implemented will return the title of dataset #5
     * of this TSstat. Each subclass can set Dataset #5 name as desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about Dataset #5.
     * @since 2.0
     */
    public abstract String getTitleOfDataset5();

    /**
     * This abstract method once implemented will return the title of dataset #6
     * of this TSstat. Each subclass can set Dataset #6 name as desired.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return a descriptive text about Dataset #6.
     * @since 2.0
     */
    public abstract String getTitleOfDataset6();

    /**
     * This abstract method once implemented will take into account the packet
     * and type of entry passed as a parameter, updating the stats information
     * to include this new information.
     *
     * @param packet new packet as a source of information to update stats.
     * @param entryType INCOMING, OUTGOING or BEING_DISCARDED, dependiendo on
     * whether the packet is incoming, outgoing or being discarded, as defined
     * on TStat class.
     * @since 2.0
     */
    public abstract void addStatEntry(TAbstractPDU packet, int entryType);

    /**
     * This abstract method once implemented will group the latests data added
     * by the time instant passed as an argument. In this way aggregated
     * statistics are generated and the dataset contains only information that
     * is relevant for the units used in OpenSimMPLS (the tick or time instant).
     * This reduces the dataset size and the time of processing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param timeInstant the time instant (in simulation terms) by wich the
     * latest data contained in the datases will be grouped/aggregated.
     * @since 2.0
     */
    public abstract void groupStatsByTimeInstant(long timeInstant);

    /**
     * This abstract method, once implemented will get the number of datasets
     * that are available.
     *
     * @return the number of available datasets.
     * @since 2.0
     */
    public abstract int getNumberOfAvailableDatasets();

    /**
     * This abstract method once implemented will reset all the values and
     * attribues as in the moment of its instantiation.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public abstract void reset();

    protected boolean statsEnabled;

    // Strings related to stats.
    public static final String IPV4 = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.IPv4");
    public static final String IPV4_GOS1 = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.IPv4_GoS1");
    public static final String IPV4_GOS2 = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.IPv4_GoS2");
    public static final String IPV4_GOS3 = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.IPv4_Gos3");
    public static final String MPLS = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.MPLS");
    public static final String MPLS_GOS1 = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.MPLS_GoS1");
    public static final String MPLS_GOS2 = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.MPLS_GoS2");
    public static final String MPLS_GOS3 = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.MPLS_GoS3");
    public static final String TLDP = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.TLDP");
    public static final String GPSRP = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.GPSRP");
    public static final String TIME = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Tiempo_ns");
    public static final String NUMBER_OF_PACKETS = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Numero_de_paquetes");
    public static final String NUMBER = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Numero");
    public static final String DMGP_SIZE = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Tamanio_DMGP_Kilobytes");
    public static final String OUTGOING_PACKETS = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_salientes");
    public static final String INCOMING_PACKETS = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_entrantes");
    public static final String DISCARDED_PACKETS = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_descartados");
    public static final String RECOVERED_PACKETS = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_recuperados");
    public static final String UNRECOVERED_PACKETS = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_no_recuperados");
    public static final String LOCAL_RECOVERIES_MANAGED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Recuperaciones_locales_de_paquetes_con_GoS");
    public static final String RETRANSMISSIONS_MANAGED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Retransmisiones_de_paquetes_con_GoS_atendidas");
    public static final String REQUESTS_ANSWERED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Solicitudes_atendidas");
    public static final String RETRANSMISSIONS_REALIZED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Retransmisiones_realizadas");
    public static final String RETRANSMISSIONS_UNREALIZED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Retransmisiones_no_realizadas");
    public static final String RETRANSMISSION_REQUESTS_RECEIVED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Solicitudes_recibidas");
    public static final String GOS_PACKETS_LOST = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_GoS_perdidos");
    public static final String RETRANSMISSION_REQUESTS_SENT = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Solicitudes_emitidas");
    public static final String RETRANSMISSION_REQUESTS_STILL_UNANSWERED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Solicitudes_sin_respuesta_aun");
    public static final String GOS_PACKETS_RECOVERED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_GoS_recuperados");
    public static final String GOS_PACKETS_UNRECOVERED = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Paquetes_GoS_no_recuperados");
    public static final String DESCRIPTION = java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TEstadisticas.Descripcion");

    // Constants related to packet status
    public static final int INCOMING = -1;
    public static final int OUTGOING = -2;
    public static final int BEING_DISCARDED = -3;
}
