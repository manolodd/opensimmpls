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

import java.awt.*;

/**
 * Esta clase implementa un objeto que almacenar� la configuraci�n completa de un enlace.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TLinkConfig {

    /**
     * Crea una nueva instancia de TConfigEnlace
     */
    public TLinkConfig() {
        nombre = "";
        nombreExtremo1 = "";
        nombreExtremo2 = "";
        delay = 1;
        valida = false;
        ponerNombre = false;
        tipoEnlace = TLink.INTERNAL;
        puertoExtremo1 = -1;
        puertoExtremo2 = -1;
    }

    /**
     * Este m�todo establece el retardo para el enlace.
     * @param d Retardo deseado para el enlace
     * @since 2.0
     */    
    public void setDelay(int d) {
        delay = d;
    }
    
    /**
     * Este m�todo devuelve el retardo del enlace.
     * @return El retardo del enlace.
     * @since 2.0
     */    
    public int getDelay() {
        return delay;
    }
    
    /**
     * Este m�todo establece la configuraci�n del enlace como v�lida.
     * @since 2.0
     * @param v TRUE, indica que la configuraci�n es v�lida. FALSE, lo contrario.
     */    
    public void ponerValida(boolean v) {
        valida = v;
    }
    
    /**
     * Este m�todo averigua si la configuraci�n de enlace es v�lida o no.
     * @since 2.0
     * @return TRUE, si la configuraci�n es v�lida. FALSE en caso contrario.
     */    
    public boolean obtenerValida() {
        return valida;
    }
    
    /**
     * Este m�todo establece si el enlace es externo o interno al dominio MPLS.
     * @since 2.0
     * @param t EXTERO, si se desea que el enlace sea externo. INTERNAL, si se desea que sea
 interno.
     */    
    public void ponerTipo(int t) {
        tipoEnlace = t;
    }

    /**
     * Este m�todo averigua el tipo del enlace.
     * @since 2.0
     * @return EXTERNAL, si el enlace es externo. INTERNAL si el enlace es interno.
     */    
    public int obtenerTipo() {
        return tipoEnlace;
    }

    /**
     * Este m�todo establece el nombre del enlace.
     * @since 2.0
     * @param n El nombre del enlace.
     */    
    public void setName(String n) {
        nombre = n;
    }

    /**
     * Este m�todo obtiene el nombre del enlace.
     * @since 2.0
     * @return El nombre del enlace.
     */    
    public String getName() {
        return nombre;
    }

    /**
     * Este m�todo establece si el nombre del enlace se debe mostrar o no.
     * @since 2.0
     * @param p TRUE, si queremos que el nombre se muestre. FALSE en caso contrario.
     */    
    public void setShowName(boolean p) {
        ponerNombre = p;
    }

    /**
     * Este m�todo establece el nombre del nodo que ser� extremo izquierdo del enlace.
     * @since 2.0
     * @param n Nombre del ndo que ser� extremo izquierdo del enlace.
     */    
    public void setHeadEndNodeName(String n) {
        nombreExtremo1 = n;
    }

    /**
     * Este m�todo obtiene el nombre del nodo que es extremo izquierdo del enlace.
     * @since 2.0
     * @return El nombre del nodo que es extremo izquierdo del enlace.
     */    
    public String getHeadEndNodeName() {
        return nombreExtremo1;
    }

    /**
     * Este m�todo establece el nombre del nodo que ser� extremo derecho del enlace.
     * @since 2.0
     * @param n El nombre del nodo que es extremo derecho del enlace.
     */    
    public void setTailEndNodeName(String n) {
        nombreExtremo2 = n;
    }

    /**
     * Este m�todo obtiene el nombre del nodo que es extremo derecho del enlace.
     * @since 2.0
     * @return El nombre del nodo que es extremo derecho del enlace.
     */    
    public String getTailEndNodeName() {
        return nombreExtremo2;
    }

    /**
     * Este m�todo averigua si actualmente el enlace est� mostrando el nombre.
     * @since 2.0
     * @return TRUE, si el nombre se est� mostrando. FALSE en caso contrario.
     */    
    public boolean getShowName() {
        return ponerNombre;
    }

    /**
     * Este m�todo establece a qu� puerto del nodo extremo izquierdo del enlace se va a
     * conectar �ste.
     * @since 2.0
     * @param p Puerto del nodo izquierdo al que se conecta el enlace.
     */    
    public void setHeadEndNodePortID(int p) {  
        puertoExtremo1 = p;
    }

    /**
     * Este m�todo obtiene el puerto del nodo izquierdo del enlace al que este est�
     * conectado.
     * @since 2.0
     * @return El puerto del ndo izquierdo del enlace al que �ste est� conectado.
     */    
    public int getHeadEndNodePortID() {  
        return puertoExtremo1;
    }

    /**
     * Este m�todo establece a qu� puerto del nodo extremo derecho del enlace se va a
     * conectar �ste.
     * @since 2.0
     * @param p Puerto del ndo derecho del enlace al que se conectar� �ste.
     */    
    public void setTailEndNodePortID(int p) {  
        puertoExtremo2 = p;
    }

    /**
     * Este m�todo obtiene el puerto del nodo derecho del enlace al que �ste est�
     * conectado.
     * @since 2.0
     * @return El puerto del nodo derecho del enlace al que este est� conectado.
     */    
    public int getTailEndNodePortID() {  
        return puertoExtremo2;
    }

    /**
     * Este m�todo comprueba que la configuraci�n del enlace sea correcta.
     * @since 2.0
     * @param topo Topolog�a donde est� insertado el enlace.
     * @param recfg TRUE, si se est� reconfigurando el enlace. FALSE si se est� insertando nuevo.
     * @return CORRECTA, si la configuraci�n es correcta. Un codigo de error correspondiente a
     * una de las constantes de la clase en caso contrario.
     */    
    public int comprobar(TTopology topo, boolean recfg) {
        if (nombre.equals(""))
            return this.SIN_NOMBRE;
        boolean soloEspacios = true;
        for (int i=0; i < nombre.length(); i++){
            if (nombre.charAt(i) != ' ')
                soloEspacios = false;
        }
        if (soloEspacios)
            return this.SOLO_ESPACIOS;
        if (!recfg) {
            TLink e = topo.obtenerPrimerEnlaceLlamado(nombre);
            if (e != null)
                return this.NOMBRE_YA_EXISTE;
        } else {
            TLink e = topo.obtenerPrimerEnlaceLlamado(nombre);
            if (e != null) {
                if (topo.existeMasDeUnEnlaceLlamado(nombre)) {
                    return this.NOMBRE_YA_EXISTE;
                } else {
                    if (e.getHeadEndNode().getName().equals(this.getHeadEndNodeName())) {
                        if (!e.getTailEndNode().getName().equals(this.getTailEndNodeName())) {
                            return this.NOMBRE_YA_EXISTE;
                        }
                    } else {
                        return this.NOMBRE_YA_EXISTE;
                    }
                }
            }
        }
        if ((this.nombreExtremo1.equals("")) || (this.nombreExtremo1 == null))
            return this.FALTA_EXTREMO_1;
        if ((this.nombreExtremo2.equals("")) || (this.nombreExtremo2 == null))
            return this.FALTA_EXTREMO_2;
        if (puertoExtremo1 == -1)
            return this.FALTA_PUERTO_1;
        if (puertoExtremo2 == -1)
            return this.FALTA_PUERTO_2;
        return this.CORRECTA;
    }

    /**
     * Este m�todo transforma el c�digo de error de las constantes de la clase, en una
     * explicaci�n textual inteligible.
     * @since 2.0
     * @param error El codigo de error que se debe transformar
     * @return La descripci�n textual del error
     */    
    public String obtenerMensajeError(int error) {
        switch (error) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.FALTA_NOMBRE"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.NoSoloEspacios"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.NombreYaUsado"));
            case FALTA_PUERTO_1: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionrPuertoIzquierdo"));
            case FALTA_PUERTO_2: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionarPuertoDerecho"));
            case FALTA_EXTREMO_1: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionarExtremoIzquierdo"));
            case FALTA_EXTREMO_2: return (java.util.ResourceBundle.getBundle("com/manolodominguez/opensimmpls/resources/translations/translations").getString("TConfigEnlace.SeleccionarExtremoDerecho"));
        }
        return ("");
    }

    /**
     * Este m�todo calcula y configura el tipo del enlace bas�ndose en el tipo de nodos
     * que interconecta.
     * @since 2.0
     * @param topo Topolog�a donde se encuentra el enlace.
     */    
    public void discoverLinkType(TTopology topo) {
        TNode e1, e2;
        e1 = topo.getFirstNodeNamed(nombreExtremo1);
        e2 = topo.getFirstNodeNamed(nombreExtremo2);
        if ((e1 != null) && (e2 != null)) {
            int tipo1 = e1.getNodeType();
            int tipo2 = e2.getNodeType();
            if ((tipo1 == TNode.SENDER) || (tipo1 == TNode.RECEIVER) ||
                (tipo2 == TNode.SENDER) || (tipo2 == TNode.RECEIVER))
                this.ponerTipo(TLink.EXTERNAL);
            else
                this.ponerTipo(TLink.INTERNAL);
        }
    }

    /**
     * Constante que indica que la configuraci�n del enlace es correcta.
     * @since 2.0
     */    
    public static final int CORRECTA = 0;
    /**
     * Constante que indica falta el nombre en la configuraci�n del enlace.
     * @since 2.0
     */    
    public static final int SIN_NOMBRE = 1;
    /**
     * Constante que indica que el nombre del enlace solo esta compuesta por espacios.
     * @since 2.0
     */    
    public static final int SOLO_ESPACIOS = 2;
    /**
     * Constante que indica que ya hay otro enlace en la topolog�a con ese nombre.
     * @since 2.0
     */    
    public static final int NOMBRE_YA_EXISTE = 3;
    /**
     * Constnte que indica que no se ha seleccionado el puerto para el extremo
     * izquierdo del enlace.
     * @since 2.0
     */    
    public static final int FALTA_PUERTO_1 = 4;
    /**
     * Constnte que indica que no se ha seleccionado el puerto para el extremo
     * derecho del enlace.
     * @since 2.0
     */    
    public static final int FALTA_PUERTO_2 = 5;
    /**
     * Constnte que indica que no se ha seleccionado el extremo
     * izquierdo del enlace.
     * @since 2.0
     */    
    public static final int FALTA_EXTREMO_1 = 6;
    /**
     * Constnte que indica que no se ha seleccionado el extremo
     * derecho del enlace.
     * @since 2.0
     */    
    public static final int FALTA_EXTREMO_2 = 7;

    private int tipoEnlace;
    private String nombre;
    private String nombreExtremo1;
    private String nombreExtremo2;
    private int puertoExtremo1;
    private int puertoExtremo2;
    /**
     * Atributo de la clase que almacena el retardo del enlace.
     * @since 2.0
     */    
    public int delay;
    private boolean ponerNombre;
    private boolean valida;
}
