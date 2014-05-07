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
package simMPLS.protocols;

/** Esta clase implementa una codificaci�n del campo opciones de IPv4 acorde a lo
 * que necesitamos para la propuesta de GoS sobre MPLS.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TCampoOpcionesIPv4 {
    
    /** Este m�todo es el constructor de la clase. Crea una nueva instancia de
     * TCampoOpcionesIPv4.
     * @since 1.0
     */
    public TCampoOpcionesIPv4() {
        nivelGoS = 0;
        nodosPasados = new String[this.MAX_IPS];
        int i = 0;
        for (i=0; i< this.MAX_IPS; i++) {
            nodosPasados[i] = null;
        }
        campoUsado = false;
        nodosOcupados = 0;
        idUnico = 0;
        hayMarcasDePaso = false;
    }
    
    /** Este m�todo devuelve el tama�o del campo opciones debido a que �ste puede ser
     * variable en palabras de 4 bytes. El resultado lo devuelve en bytes.
     * @return El tama�o en bytes del campo opciones de IPv4.
     * @since 1.0
     */
    public int obtenerTamanio() {
        int tam=0;
        int contador = 0;
        if (this.campoUsado) {
            tam += 1;   // Del campo GoS
            tam += 4;   // Del identificador �nico
            tam += (4*this.nodosOcupados);
            while (contador < tam) {
                contador += 4;
            }
            tam = contador;
        }
        return tam;
    }
    
    /**
     * Este m�todo permite establecer el nivel de garant�a de servicio que se ha
     * elegido para el paquete.
     * @param ngos Nivel de garant�a de servicio; Una de las constantes definidas en la clase TPDU.
     * @since 1.0
     */
    public void ponerNivelGoS(int ngos) {
        this.nivelGoS = ngos;
        this.campoUsado = true;
    }
    
    /**
     * Este m�todo permite establecer como usado el campo opciones.
     * @since 1.0
     */
    public void usar() {
        this.campoUsado = true;
    }
    
    /**
     * Este m�todo permite consultar si el campo opciones est� siendo usado o no.
     * @return true, si el campo opciones est� siendo usado. false en caso contrario.
     * @since 1.0
     */
    public boolean isUsed() {
        return this.campoUsado;
    }
    
    /**
     * Este m�todo poermite obtener el nivel de garant�a de servicio que tiene el
     * paquete.
     * @return El nivel de garant�a de servicio del paquete. una de las constantes definidas en
     * la clase TPDU.
     * @since 1.0
     */
    public int getEncodedGoSLevel() {
        return this.nivelGoS;
    }
    
    /**
     * Este m�todo permite establecer el identificador del paquete.
     * @param id El identificador del paquete.
     * @since 1.0
     */
    public void ponerIDPaqueteGoS(int id) {
        this.idUnico = id;
        this.campoUsado = true;
    }
    
    /**
     * Este m�todo permite obtener el identificador del paquete.
     * @return El identificador del paquete.
     * @since 1.0
     */
    public int obtenerIDPaqueteGoS() {
        return this.idUnico;
    }
    
    /**
     * Este m�todo permite insertar en el campo opciones una direcci�n IP de un nodo
     * activo atravesado.
     * @param IPNodo La IP del nodo activo atravesado.
     * @since 1.0
     */
    public void ponerNodoAtravesado(String IPNodo) {
        this.hayMarcasDePaso = true;
        this.campoUsado = true;
        if (this.nodosOcupados < MAX_IPS) {
            this.nodosPasados[this.nodosOcupados] = IPNodo;
            this.nodosOcupados++;
        } else {
            int contador = 1;
            for (contador = 1; contador < MAX_IPS; contador++) {
                nodosPasados[contador-1] = nodosPasados[contador];
            }
            nodosPasados[MAX_IPS-1] = IPNodo;
        }
    }
    
    /**
     * Este m�todo permite averiguar si en el campo opciones hay anotadas direcciones
     * IP de los nodos activos que se han pasado o no.
     * @return true, si hay direcciones IP almacenadas en el campo opciones. false, en caso
     * contrario,
     * @since 1.0
     */
    public boolean tieneMarcasDePaso() {
        return this.hayMarcasDePaso;
    }
    
    /**
     * Este m�todo comprueba el n�mero de IP de nodos activos atravesados por el
     * paquete que est�n actualmente marcadas en el campo opciones.
     * @return N�mero de IP de nodos activos atravesados que contiene el campo opciones.
     * @since 1.0
     */    
    public int obtenerNumeroDeNodosActivosAtravesados() {
        return this.nodosOcupados;
    }
    
    /**
     * Este m�todo devuelve la IP del nodo activo que el paquete atraves� hace <I>naa</I>
     * nodos activos.
     * @param naa N�mero de nodos activos atravesados antes del que queremos saber la IP.
     * @return IP del nodo activo deseado.
     * @since 1.0
     */    
    public String obtenerActivoNodoAtravesado(int naa) {
        if (naa < this.MAX_IPS) {
            return this.nodosPasados[naa];
        }
        return null;
    }
    
    /**
     * Esta constante indica el n�mero m�ximo de direcciones IP de nodos activos
     * atravesados que pueden ser almacenadas en el campo opciones.
     * @since 1.0
     */
    private static final int MAX_IPS = 8;
    
    /**
     * Este atributo almacena los datos del nivel de garant�a de servicio y la creaci�n
     * de LSp de respaldo que se han elegido para el paquete.
     * @since 1.0
     */
    private int nivelGoS;
    /**
     * Este atributo est� configurado como una cola rotatoria que almacena las
     * direcciones IP (la 8 �ltimas como mucho9 de los nodos activos que ha ido
     * atravesando el paquete.
     * @since 1.0
     */
    private String nodosPasados[];
    /**
     * Este atributo indica si el campo opciones se ha usado o no, para de este modo
     * contarlo en el c�mputo del tama�o del paquete o no.
     * @since 1.0
     */
    private boolean campoUsado;
    /**
     * Este m�todo indica cu�ntas direcciones IP's de nodos activos atravesados hay
     * almacenadas en el campo opciones.
     * @since 1.0
     */
    private int nodosOcupados;
    /**
     * Este m�todo almacena el identificador unico que, junto con la IP de origen del
     * paquete, forman la clave primaria que identifica al paquete.
     * @since 1.0
     */
    private int idUnico;
    /**
     * Este atributo indica si se ha anotado alguna direcci�n IP de algun nodo activo
     * en el campo opciones.
     * @since 1.0
     */
    private boolean hayMarcasDePaso;
}
