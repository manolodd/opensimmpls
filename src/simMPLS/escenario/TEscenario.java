//**************************************************************************
// Nombre......: TEscenario.java
// Proyecto....: Open SimMPLS
// Descripción.: Clase que implementa un objeto que contiene toda la infor-
// ............: mación sobre un escenario, concerniente a la simulacioón, 
// ............: al diseño, al análisis... absolutamente todo. 
// Fecha.......: 27/02/2004
// Autor/es....: Manuel Domínguez Dorado
// ............: ingeniero@ManoloDominguez.com
// ............: http://www.ManoloDominguez.com
//**************************************************************************

package simMPLS.escenario;

import java.io.*;
import simMPLS.escenario.*;
import simMPLS.electronica.reloj.*;
import simMPLS.interfaz.simulador.*;

/**
 * Esta clase implementa un escenario completo de simulación, con todos sus
 * componentes.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TEscenario {

    /**
     * Crea una nueva instancia de TEscenario
     * @since 1.0
     */
    public TEscenario() {
        topologia = new TTopologia(this);
        simulacion = new TSimulacion(this);
        File ficheroEscenario = null;
        guardado = false;
        modificado = false;
        titulo = "";
        autor = "";
        descripcion = "";
    }
    
    /**
     * Este método establece el panel de simulación que se usará para mostrar la
     * simulación visual.
     * @param ps Un panel de simulación existente.
     * @since 1.0
     */    
    public void ponerPanelSimulacion(JPanelSimulacion ps) {
        simulacion.ponerPanelSimulacion(ps);
    }
    
    /**
     * Este método reinicia todos los atributos de la clase, dejando a la instancia
     * como si la acabase de crear el constructor.
     * @since 1.0
     */    
    public void reset() {
        topologia.reset();
        simulacion.reset();
        modificado = true;
    }
    
    /**
     * Este método permite establecer el título del escenario, que aparecerá en la
     * ventana correspondiente cuando sea necesario.
     * @param t Título del escenario
     * @since 1.0
     */    
    public void ponerTitulo(String t) {
        this.titulo = t;
    }

    /**
     * Este método permite obtener el título del escenario.
     * @return Título del escenario.
     * @since 1.0
     */    
    public String obtenerTitulo() {
        return this.titulo;
    }
    
    /**
     * Este método permite establecer el autor del escenario.
     * @param a Autor del escenario.
     * @since 1.0
     */    
    public void ponerAutor(String a) {
        this.autor = a;
    }
    
    /**
     * Este método permite obtener el autor del escenario.
     * @return Autor del escenario.
     * @since 1.0
     */    
    public String obtenerAutor() {
        return this.autor;
    }
    
    /**
     * Este método permite establecer la descripción del escenario.
     * @param d Descripción del escenario.
     * @since 1.0
     */    
    public void ponerDescripcion(String d) {
        this.descripcion = d;
    }
    
    /**
     * Este método permite obtener la descripción del escenario.
     * @return La descripción del escenario.
     * @since 1.0
     */    
    public String obtenerDescripcion() {
        return this.descripcion;
    }
    
    /**
     * Este método permite obtener una caden a que es la serialización del título para
     * almacenarla en disco.
     * @return La representación serializada del título.
     * @since 1.0
     */    
    public String serializarTitulo() {
        String cadena = "#Titulo#";
        if (this.obtenerTitulo().replace('#', ' ').equals("")) {
            cadena += java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEscenario.SinDefinir");
        } else {
            cadena += this.obtenerTitulo().replace('#', ' ');
        }
        cadena += "#";
        return cadena;
    }

    /**
     * Este método permite obtener una caden a que es la serialización del autor para
     * almacenarla en disco.
     * @since 1.0
     * @return La representación serializada del autor
     */    
    public String serializarAutor() {
        String cadena = "#Autor#";
        if (this.obtenerAutor().replace('#', ' ').equals("")) {
            cadena += java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEscenario.SinDefinir");
        } else {
            cadena += this.obtenerAutor().replace('#', ' ');
        }
        cadena += "#";
        return cadena;
    }

    /**
     * Este método permite obtener una cadena que es la serialización de la descripción
     * del escenario para  almacenarla en disco.
     * @since 1.0
     * @return Representación serializada de la descripción.
     */    
    public String serializarDescripcion() {
        String cadena = "#Descripcion#";
        if (this.obtenerDescripcion().replace('#', ' ').equals("")) {
            cadena += java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TEscenario.SinDefinir");
        } else {
            cadena += this.obtenerDescripcion().replace('#', ' ');
        }
        cadena += "#";
        return cadena;
    }

    /**
     * Este método permite, apartir de un título serializado, poner el mismo titulo a
     * la instancia actual del escenario.
     * @since 1.0
     * @param titulo Titulo serializado.
     * @return TRUE, si se ha deserializado sin problemas. FALSE, en caso contrario.
     */    
    public boolean deserializarTitulo(String titulo) {
        String valores[] = titulo.split("#");
        if (valores.length != 3) {
            return false;
        }
        if (valores[2] != null) {
            this.ponerTitulo(valores[2]);
        }
        return true;
    }

    /**
     * Este método permite, apartir de un autor serializado, poner el mismo autor a
     * la instancia actual del escenario.
     * @since 1.0
     * @param autor Autor serializado.
     * @return TRUE, si se ha deserializado correctamente. FALSE en caso contrario.
     */    
    public boolean deserializarAutor(String autor) {
        String valores[] = autor.split("#");
        if (valores.length != 3) {
            return false;
        }
        if (valores[2] != null) {
            this.ponerAutor(valores[2]);
        }
        return true;
    }

    /**
     * Este método permite, apartir de una descripción serializada, poner la misma
     * descripción a la instancia actual del escenario.
     * @since 1.0
     * @param descripcion Descripción serializada.
     * @return TRUE, si se ha deserializado correctamente. FALSE en caso contrario.
     */    
    public boolean deserializarDescripcion(String descripcion) {
        String valores[] = descripcion.split("#");
        if (valores.length != 3) {
            return false;
        }
        if (valores[2] != null) {
            this.ponerDescripcion(valores[2]);
        } 
        return true;
    }
    
    /**
     * Este método permite establecer la topología del escenario a partir de una
     * topología existente.
     * @param t Topología asociada al escenario.
     * @since 1.0
     */    
    public void ponerTopologia(TTopologia t) {
        topologia = t;
    }

    /**
     * Este método permite obtener la topología del escenario.
     * @return Topología del escenario.
     * @since 1.0
     */    
    public TTopologia obtenerTopologia() {
        return topologia;
    }

    /**
     * Este método permite establecer la simulación del escenario.
     * @since 1.0
     * @param s Simulación asociada al escenario.
     */    
    public void ponerSimulacion(TSimulacion s) {
        simulacion = s;
    }

    /**
     * Este método permite obtener la simulación del escenario.
     * @return Simulación del escenario.
     * @since 1.0
     */    
    public TSimulacion obtenerSimulacion() {
        return simulacion;
    }

    /**
     * Este método permite saber si en un momento dado la simulación está funcionando o
     * no.
     * @return TRUE, si la simulación está en curso. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean simulacionEnFuncionamiento() {
        if (topologia.obtenerReloj().estaEnFuncionamiento()) 
            return true;
        return false;
    }

    /**
     * Este método permite comenzar el proceso de simulación.
     * @since 1.0
     */    
    public void generarSimulacion() {
        if (!this.simulacionEnFuncionamiento()) {
            topologia.obtenerReloj().reset();
            topologia.obtenerReloj().iniciar();
        }
    }

    /**
     * Este método permite establecer el tiempo que debe durar la simulación.
     * @param d Marca de tiempo que expresa la duración en tiempo de simulación, de la
     * simulación.
     * @since 1.0
     */    
    public void ponerDuracionSimulacion(TMarcaTiempo d) {
        if (!this.simulacionEnFuncionamiento()) {
            this.topologia.obtenerReloj().ponerLimite(d);
        }
    }

    /**
     * Este método permite establecer la duración de cada paso de simulación. La
     * simulación contínua usa unos recursos muy elevados así que la simulación se
     * discretiza, haciéndose por tics.
     * @param p Duración en nanosegundos del tic de relo o, lo que es lo mismo, la duración del
     * paso de simulación.
     * @since 1.0
     */    
    public void ponerPasoSimulacion(int p) {
        if (!this.simulacionEnFuncionamiento()) {
            this.topologia.obtenerReloj().ponerPaso(p);
        }
    }

    /**
     * Este método permite establecer el nombre y la ruta del fichero que almacena en
     * disco el escenario.
     * @param f Fichero de disco que almacena el escenario.
     * @since 1.0
     */    
    public void ponerFichero(File f) {
        ficheroEscenario = f;
    }
    
    /**
     * Este método permite obtener el nombre y ruta del fichero que almacena el
     * escenario en disco.
     * @return Fichero y ruta que almacena el escenario en disco.
     * @since 1.0
     */    
    public File obtenerFichero() {
        return ficheroEscenario;
    }
    
    /**
     * Este método establece si el escenario está guardado en disco o no.
     * @param g TRUE, si el escenario queremos que aparezca como guardado. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public void ponerGuardado(boolean g) {
        guardado = g;
    }
     
    /**
     * Este método permite obtener si el escenario se encuentra guardado en disco o
     * solo está en memoria.
     * @return TRUE, si el fichero está almacenado en disco. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean obtenerGuardado() {
        return guardado;
    }
     
    /**
     * Este método permite establecer si el escenario se ha modificado desde la ultima
     * vez que se guardó en disco o no.
     * @param m TRUE, si queremos que el escenario aparezca como modificado. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public void ponerModificado(boolean m) {
        modificado = m;
    }
    
    /**
     * Este método permite obtener si el escenario se ha modificado después de haberse
     * guardado en disco o no.
     * @return TRUE, si el escenario está modificado con respecto al disco. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public boolean obtenerModificado() {
        return modificado;
    }
    
    
    private String titulo;
    private String autor;
    private String descripcion;
    private TTopologia topologia;
    private TSimulacion simulacion;
    private File ficheroEscenario;
    private boolean guardado;
    private boolean modificado;
}
