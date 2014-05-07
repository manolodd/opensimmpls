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

/** Esta clase permite crear un objeto que genera direcciones IP consecutivas y sin
 * repetici�n dentro del rango 10.0.0.1 - 10.255.255.254
 * @version 1.0
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 */
public class TGeneradorDeIP {
    
    /** Este m�todo es el constructor de la clase. Permite generar instancias de
     * TGeneradorDeIP.
     * @since 1.0
     */
    public TGeneradorDeIP() {
        octeto2 = 0;
        octeto3 = 0;
        octeto4 = 0;
    }
    
    /** Este m�todo permite iniciar el generador de direcciones IP con una direcci�n
     * que, obligatoriamente debe pertenecer al dominio manejado por el generador, es
     * decir 10.0.0.1 - 10.255.255.254 y que debe venir dada en forma de cadena de
     * texto, por ejemplo "10.23.144.56".
     * @param IPStr La representaci�n textual de la direcci�n IP.
     * @return TRUE, si la direcci�n IP es v�lida y se inicia con ella el generador; FALSE en
     * caso contrario.
     * @since 1.0
     */
    public boolean ponerValor(String IPStr) {
        if (esIPValida(IPStr)) {
            String octetos[] = IPStr.split("[.]");
            octeto2 = Integer.valueOf(octetos[1]).intValue();
            octeto3 = Integer.valueOf(octetos[2]).intValue();
            octeto4 = Integer.valueOf(octetos[3]).intValue();
            return true;
        }
        return false;
    }
    
    /**
     * Este m�todo comprueba si una direcci�n IP pasada por par�metro es una direcci�n
     * v�lida y perteneciente al rango 10.0.0.1 - 10.255.255.254
     * @since 1.0
     * @param IPStr La direcci�n IP a comprobar.
     * @return true, si la direcci�n IP es v�lida y adem�s pertenece al rango 10.0.0.1 -
     * 10.255.255.254. false en caso contrario.
     */
    public boolean esIPValida(String IPStr) {
        if (IPStr.matches("[1][0][.][0-2]{0,1}[0-9]{0,1}[0-9][.][0-2]{0,1}[0-9]{0,1}[0-9][.][0-2]{0,1}[0-9]{0,1}[0-9]")) {
            int oAux2 = 0;
            int oAux3 = 0;
            int oAux4 = 0;
            String octetos[] = IPStr.split("[.]");
            oAux2 = Integer.valueOf(octetos[1]).intValue();
            oAux3 = Integer.valueOf(octetos[2]).intValue();
            oAux4 = Integer.valueOf(octetos[3]).intValue();
            if ((oAux2 == 0) && (oAux3 == 0) && (oAux4 == 0)) {
                return false;
            }
            if ((oAux2 > 255) || (oAux3 > 255) || (oAux4 > 255)) {
                return false;
            }
            if ((oAux2 > 255) && (oAux3 > 255) && (oAux4 > 255)) {
                return false;
            }
        }
        return true;
    }
    
    /** Este m�todo reinicia el generador a su valor inicial "10.0.0.0" de forma que la
     * siguiente direcci�n IP que generar� ser� 10.0.0.1.
     * @since 1.0
     */
    public void reset() {
        octeto2 = 0;
        octeto3 = 0;
        octeto4 = 0;
    }
    
    /**
     * Este m�todo permite establecer la IP a partir de la cual comenzar� a generar
     * direcciones IP el generador. Siempre y cuando la IP que especifiquemos sea mayor
     * que la que el propio generador tenga internamente.
     * @param IPStr Direcci�n IP a partir de la cual queremos que siga generando el generador.
     * @since 1.0
     */
    public void ponerValorSiMayor(String IPStr) {
        if (esIPValida(IPStr)) {
            String octetos[] = IPStr.split("[.]");
            int octetoAux2 = Integer.valueOf(octetos[1]).intValue();
            int octetoAux3 = Integer.valueOf(octetos[2]).intValue();
            int octetoAux4 = Integer.valueOf(octetos[3]).intValue();
            if (octetoAux2 > octeto2) {
                this.ponerValor(IPStr);
            } else if (octetoAux2 == octeto2) {
                if (octetoAux3 > octeto3) {
                    this.ponerValor(IPStr);
                } else if (octetoAux3 == octeto3) {
                    if (octetoAux4 > octeto4) {
                        this.ponerValor(IPStr);
                    }
                }
            }
        }
    }
    
    /** Este m�todo obtiene una direcci�n IP que ser� distinta a todas las ya generadas
     * por el mismo y distinta a todas las que les queda por generar. La IP pertenecer�
     * al rango 10.0.0.1 - 10.255.255.254.
     * @throws EDesbordeDeIP Cuando el generador sobrepasa el rango de direcciones IP para el que est�
     * preparado, se genera un excepci�n EDesbordeDeIP cada vez que se llama a este
     * m�todo, indicando que el generador no puede cumplir sus funci�n.
     * @return Devuelve una representaci�n textual de la direcci�n IP generada, por ejemplo
     * "10.32.125.254".
     * @since 1.0
     */
    public String obtenerIP() throws EDesbordeDeIP {
        if (octeto4 < 255) {
            octeto4++;
        } else {
            if (octeto3 < 255) {
                octeto4 = 0;
                octeto3++;
            } else {
                if (octeto2 < 254) {
                    octeto4 = 0;
                    octeto3 = 0;
                    octeto2++;
                } else {
                    throw new EDesbordeDeIP();
                }
            }
        }
        return (octeto1 + "." + octeto2 + "." + octeto3 + "." + octeto4);
    }
    
    /** Constante "10" que ser� el componente fijo de las direcciones IP generadas.
     * @since 1.0
     */
    private static final int octeto1 = 10;
    /** Segundo octeto de las direcciones IP generadas.
     * @since 1.0
     */
    private int octeto2;
    /** Tercer octeto de las direcciones IP generadas.
     * @since 1.0
     */
    private int octeto3;
    /** Cuartoocteto de las direcciones IP generadas.
     * @since 1.0
     */
    private int octeto4;
}
