//**************************************************************************
// Nombre......: TConfigEnlace.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un objeto que contendrá la configura-
// ............: ción necesaria para crear un enlace.
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import java.awt.*;
import simMPLS.escenario.*;

/**
 * Esta clase implementa un objeto que almacenará la configuración completa de un enlace.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TConfigEnlace {

    /**
     * Crea una nueva instancia de TConfigEnlace
     */
    public TConfigEnlace() {
        nombre = "";
        nombreExtremo1 = "";
        nombreExtremo2 = "";
        delay = 1;
        valida = false;
        ponerNombre = false;
        tipoEnlace = TEnlaceTopologia.INTERNO;
        puertoExtremo1 = -1;
        puertoExtremo2 = -1;
    }

    /**
     * Este método establece el retardo para el enlace.
     * @param d Retardo deseado para el enlace
     * @since 1.0
     */    
    public void ponerDelay(int d) {
        delay = d;
    }
    
    /**
     * Este método devuelve el retardo del enlace.
     * @return El retardo del enlace.
     * @since 1.0
     */    
    public int obtenerDelay() {
        return delay;
    }
    
    /**
     * Este método establece la configuración del enlace como válida.
     * @since 1.0
     * @param v TRUE, indica que la configuración es válida. FALSE, lo contrario.
     */    
    public void ponerValida(boolean v) {
        valida = v;
    }
    
    /**
     * Este método averigua si la configuración de enlace es válida o no.
     * @since 1.0
     * @return TRUE, si la configuración es válida. FALSE en caso contrario.
     */    
    public boolean obtenerValida() {
        return valida;
    }
    
    /**
     * Este método establece si el enlace es externo o interno al dominio MPLS.
     * @since 1.0
     * @param t EXTERO, si se desea que el enlace sea externo. INTERNO, si se desea que sea
     * interno.
     */    
    public void ponerTipo(int t) {
        tipoEnlace = t;
    }

    /**
     * Este método averigua el tipo del enlace.
     * @since 1.0
     * @return EXTERNO, si el enlace es externo. INTERNO si el enlace es interno.
     */    
    public int obtenerTipo() {
        return tipoEnlace;
    }

    /**
     * Este método establece el nombre del enlace.
     * @since 1.0
     * @param n El nombre del enlace.
     */    
    public void ponerNombre(String n) {
        nombre = n;
    }

    /**
     * Este método obtiene el nombre del enlace.
     * @since 1.0
     * @return El nombre del enlace.
     */    
    public String obtenerNombre() {
        return nombre;
    }

    /**
     * Este método establece si el nombre del enlace se debe mostrar o no.
     * @since 1.0
     * @param p TRUE, si queremos que el nombre se muestre. FALSE en caso contrario.
     */    
    public void ponerMostrarNombre(boolean p) {
        ponerNombre = p;
    }

    /**
     * Este método establece el nombre del nodo que será extremo izquierdo del enlace.
     * @since 1.0
     * @param n Nombre del ndo que será extremo izquierdo del enlace.
     */    
    public void ponerNombreExtremo1(String n) {
        nombreExtremo1 = n;
    }

    /**
     * Este método obtiene el nombre del nodo que es extremo izquierdo del enlace.
     * @since 1.0
     * @return El nombre del nodo que es extremo izquierdo del enlace.
     */    
    public String obtenerNombreExtremo1() {
        return nombreExtremo1;
    }

    /**
     * Este método establece el nombre del nodo que será extremo derecho del enlace.
     * @since 1.0
     * @param n El nombre del nodo que es extremo derecho del enlace.
     */    
    public void ponerNombreExtremo2(String n) {
        nombreExtremo2 = n;
    }

    /**
     * Este método obtiene el nombre del nodo que es extremo derecho del enlace.
     * @since 1.0
     * @return El nombre del nodo que es extremo derecho del enlace.
     */    
    public String obtenerNombreExtremo2() {
        return nombreExtremo2;
    }

    /**
     * Este método averigua si actualmente el enlace está mostrando el nombre.
     * @since 1.0
     * @return TRUE, si el nombre se está mostrando. FALSE en caso contrario.
     */    
    public boolean obtenerMostrarNombre() {
        return ponerNombre;
    }

    /**
     * Este método establece a qué puerto del nodo extremo izquierdo del enlace se va a
     * conectar éste.
     * @since 1.0
     * @param p Puerto del nodo izquierdo al que se conecta el enlace.
     */    
    public void ponerPuertoExtremo1(int p) {  
        puertoExtremo1 = p;
    }

    /**
     * Este método obtiene el puerto del nodo izquierdo del enlace al que este está
     * conectado.
     * @since 1.0
     * @return El puerto del ndo izquierdo del enlace al que éste está conectado.
     */    
    public int obtenerPuertoExtremo1() {  
        return puertoExtremo1;
    }

    /**
     * Este método establece a qué puerto del nodo extremo derecho del enlace se va a
     * conectar éste.
     * @since 1.0
     * @param p Puerto del ndo derecho del enlace al que se conectará éste.
     */    
    public void ponerPuertoExtremo2(int p) {  
        puertoExtremo2 = p;
    }

    /**
     * Este método obtiene el puerto del nodo derecho del enlace al que éste está
     * conectado.
     * @since 1.0
     * @return El puerto del nodo derecho del enlace al que este está conectado.
     */    
    public int obtenerPuertoExtremo2() {  
        return puertoExtremo2;
    }

    /**
     * Este método comprueba que la configuración del enlace sea correcta.
     * @since 1.0
     * @param topo Topología donde está insertado el enlace.
     * @param recfg TRUE, si se está reconfigurando el enlace. FALSE si se está insertando nuevo.
     * @return CORRECTA, si la configuración es correcta. Un codigo de error correspondiente a
     * una de las constantes de la clase en caso contrario.
     */    
    public int comprobar(TTopologia topo, boolean recfg) {
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
            TEnlaceTopologia e = topo.obtenerPrimerEnlaceLlamado(nombre);
            if (e != null)
                return this.NOMBRE_YA_EXISTE;
        } else {
            TEnlaceTopologia e = topo.obtenerPrimerEnlaceLlamado(nombre);
            if (e != null) {
                if (topo.existeMasDeUnEnlaceLlamado(nombre)) {
                    return this.NOMBRE_YA_EXISTE;
                } else {
                    if (e.obtenerExtremo1().obtenerNombre().equals(this.obtenerNombreExtremo1())) {
                        if (!e.obtenerExtremo2().obtenerNombre().equals(this.obtenerNombreExtremo2())) {
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
     * Este método transforma el código de error de las constantes de la clase, en una
     * explicación textual inteligible.
     * @since 1.0
     * @param error El codigo de error que se debe transformar
     * @return La descripción textual del error
     */    
    public String obtenerMensajeError(int error) {
        switch (error) {
            case SIN_NOMBRE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEnlace.FALTA_NOMBRE"));
            case SOLO_ESPACIOS: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEnlace.NoSoloEspacios"));
            case NOMBRE_YA_EXISTE: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEnlace.NombreYaUsado"));
            case FALTA_PUERTO_1: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEnlace.SeleccionrPuertoIzquierdo"));
            case FALTA_PUERTO_2: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEnlace.SeleccionarPuertoDerecho"));
            case FALTA_EXTREMO_1: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEnlace.SeleccionarExtremoIzquierdo"));
            case FALTA_EXTREMO_2: return (java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TConfigEnlace.SeleccionarExtremoDerecho"));
        }
        return ("");
    }

    /**
     * Este método calcula y configura el tipo del enlace basándose en el tipo de nodos
     * que interconecta.
     * @since 1.0
     * @param topo Topología donde se encuentra el enlace.
     */    
    public void calcularTipo(TTopologia topo) {
        TNodoTopologia e1, e2;
        e1 = topo.obtenerPrimerNodoLlamado(nombreExtremo1);
        e2 = topo.obtenerPrimerNodoLlamado(nombreExtremo2);
        if ((e1 != null) && (e2 != null)) {
            int tipo1 = e1.obtenerTipo();
            int tipo2 = e2.obtenerTipo();
            if ((tipo1 == TNodoTopologia.EMISOR) || (tipo1 == TNodoTopologia.RECEPTOR) ||
                (tipo2 == TNodoTopologia.EMISOR) || (tipo2 == TNodoTopologia.RECEPTOR))
                this.ponerTipo(TEnlaceTopologia.EXTERNO);
            else
                this.ponerTipo(TEnlaceTopologia.INTERNO);
        }
    }

    /**
     * Constante que indica que la configuración del enlace es correcta.
     * @since 1.0
     */    
    public static final int CORRECTA = 0;
    /**
     * Constante que indica falta el nombre en la configuración del enlace.
     * @since 1.0
     */    
    public static final int SIN_NOMBRE = 1;
    /**
     * Constante que indica que el nombre del enlace solo esta compuesta por espacios.
     * @since 1.0
     */    
    public static final int SOLO_ESPACIOS = 2;
    /**
     * Constante que indica que ya hay otro enlace en la topología con ese nombre.
     * @since 1.0
     */    
    public static final int NOMBRE_YA_EXISTE = 3;
    /**
     * Constnte que indica que no se ha seleccionado el puerto para el extremo
     * izquierdo del enlace.
     * @since 1.0
     */    
    public static final int FALTA_PUERTO_1 = 4;
    /**
     * Constnte que indica que no se ha seleccionado el puerto para el extremo
     * derecho del enlace.
     * @since 1.0
     */    
    public static final int FALTA_PUERTO_2 = 5;
    /**
     * Constnte que indica que no se ha seleccionado el extremo
     * izquierdo del enlace.
     * @since 1.0
     */    
    public static final int FALTA_EXTREMO_1 = 6;
    /**
     * Constnte que indica que no se ha seleccionado el extremo
     * derecho del enlace.
     * @since 1.0
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
     * @since 1.0
     */    
    public int delay;
    private boolean ponerNombre;
    private boolean valida;
}
