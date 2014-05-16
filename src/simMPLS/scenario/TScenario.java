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

import simMPLS.ui.simulator.JSimulationPanel;
import simMPLS.hardware.timer.TTimestamp;
import java.io.*;

/**
 * Esta clase implementa un escenario completo de simulaci�n, con todos sus
 * componentes.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TScenario {

    /**
     * Crea una nueva instancia de TEscenario
     * @since 1.0
     */
    public TScenario() {
        topologia = new TTopology(this);
        simulacion = new TSimulation(this);
        File ficheroEscenario = null;
        guardado = false;
        modificado = false;
        titulo = "";
        autor = "";
        descripcion = "";
    }
    
    /**
     * Este m�todo establece el panel de simulaci�n que se usar� para mostrar la
     * simulaci�n visual.
     * @param ps Un panel de simulaci�n existente.
     * @since 1.0
     */    
    public void ponerPanelSimulacion(JSimulationPanel ps) {
        simulacion.ponerPanelSimulacion(ps);
    }
    
    /**
     * Este m�todo reinicia todos los atributos de la clase, dejando a la instancia
     * como si la acabase de crear el constructor.
     * @since 1.0
     */    
    public void reset() {
        topologia.reset();
        simulacion.reset();
        modificado = true;
    }
    
    /**
     * Este m�todo permite establecer el t�tulo del escenario, que aparecer� en la
     * ventana correspondiente cuando sea necesario.
     * @param t T�tulo del escenario
     * @since 1.0
     */    
    public void ponerTitulo(String t) {
        this.titulo = t;
    }

    /**
     * Este m�todo permite obtener el t�tulo del escenario.
     * @return T�tulo del escenario.
     * @since 1.0
     */    
    public String obtenerTitulo() {
        return this.titulo;
    }
    
    /**
     * Este m�todo permite establecer el autor del escenario.
     * @param a Autor del escenario.
     * @since 1.0
     */    
    public void ponerAutor(String a) {
        this.autor = a;
    }
    
    /**
     * Este m�todo permite obtener el autor del escenario.
     * @return Autor del escenario.
     * @since 1.0
     */    
    public String obtenerAutor() {
        return this.autor;
    }
    
    /**
     * Este m�todo permite establecer la descripci�n del escenario.
     * @param d Descripci�n del escenario.
     * @since 1.0
     */    
    public void ponerDescripcion(String d) {
        this.descripcion = d;
    }
    
    /**
     * Este m�todo permite obtener la descripci�n del escenario.
     * @return La descripci�n del escenario.
     * @since 1.0
     */    
    public String obtenerDescripcion() {
        return this.descripcion;
    }
    
    /**
     * Este m�todo permite obtener una caden a que es la serializaci�n del t�tulo para
     * almacenarla en disco.
     * @return La representaci�n serializada del t�tulo.
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
     * Este m�todo permite obtener una caden a que es la serializaci�n del autor para
     * almacenarla en disco.
     * @since 1.0
     * @return La representaci�n serializada del autor
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
     * Este m�todo permite obtener una cadena que es la serializaci�n de la descripci�n
     * del escenario para  almacenarla en disco.
     * @since 1.0
     * @return Representaci�n serializada de la descripci�n.
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
     * Este m�todo permite, apartir de un t�tulo serializado, poner el mismo titulo a
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
     * Este m�todo permite, apartir de un autor serializado, poner el mismo autor a
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
     * Este m�todo permite, apartir de una descripci�n serializada, poner la misma
     * descripci�n a la instancia actual del escenario.
     * @since 1.0
     * @param descripcion Descripci�n serializada.
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
     * Este m�todo permite establecer la topolog�a del escenario a partir de una
     * topolog�a existente.
     * @param t Topolog�a asociada al escenario.
     * @since 1.0
     */    
    public void ponerTopologia(TTopology t) {
        topologia = t;
    }

    /**
     * Este m�todo permite obtener la topolog�a del escenario.
     * @return Topolog�a del escenario.
     * @since 1.0
     */    
    public TTopology obtenerTopologia() {
        return topologia;
    }

    /**
     * Este m�todo permite establecer la simulaci�n del escenario.
     * @since 1.0
     * @param s Simulaci�n asociada al escenario.
     */    
    public void ponerSimulacion(TSimulation s) {
        simulacion = s;
    }

    /**
     * Este m�todo permite obtener la simulaci�n del escenario.
     * @return Simulaci�n del escenario.
     * @since 1.0
     */    
    public TSimulation obtenerSimulacion() {
        return simulacion;
    }

    /**
     * Este m�todo permite saber si en un momento dado la simulaci�n est� funcionando o
     * no.
     * @return TRUE, si la simulaci�n est� en curso. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean simulacionEnFuncionamiento() {
        if (topologia.obtenerReloj().estaEnFuncionamiento()) 
            return true;
        return false;
    }

    /**
     * Este m�todo permite comenzar el proceso de simulaci�n.
     * @since 1.0
     */    
    public void generarSimulacion() {
        if (!this.simulacionEnFuncionamiento()) {
            topologia.obtenerReloj().reset();
            topologia.obtenerReloj().iniciar();
        }
    }

    /**
     * Este m�todo permite establecer el tiempo que debe durar la simulaci�n.
     * @param d Marca de tiempo que expresa la duraci�n en tiempo de simulaci�n, de la
     * simulaci�n.
     * @since 1.0
     */    
    public void ponerDuracionSimulacion(TTimestamp d) {
        if (!this.simulacionEnFuncionamiento()) {
            this.topologia.obtenerReloj().ponerLimite(d);
        }
    }

    /**
     * Este m�todo permite establecer la duraci�n de cada paso de simulaci�n. La
     * simulaci�n cont�nua usa unos recursos muy elevados as� que la simulaci�n se
     * discretiza, haci�ndose por tics.
     * @param p Duraci�n en nanosegundos del tic de relo o, lo que es lo mismo, la duraci�n del
     * paso de simulaci�n.
     * @since 1.0
     */    
    public void ponerPasoSimulacion(int p) {
        if (!this.simulacionEnFuncionamiento()) {
            this.topologia.obtenerReloj().ponerPaso(p);
        }
    }

    /**
     * Este m�todo permite establecer el nombre y la ruta del fichero que almacena en
     * disco el escenario.
     * @param f Fichero de disco que almacena el escenario.
     * @since 1.0
     */    
    public void ponerFichero(File f) {
        ficheroEscenario = f;
    }
    
    /**
     * Este m�todo permite obtener el nombre y ruta del fichero que almacena el
     * escenario en disco.
     * @return Fichero y ruta que almacena el escenario en disco.
     * @since 1.0
     */    
    public File obtenerFichero() {
        return ficheroEscenario;
    }
    
    /**
     * Este m�todo establece si el escenario est� guardado en disco o no.
     * @param g TRUE, si el escenario queremos que aparezca como guardado. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public void ponerGuardado(boolean g) {
        guardado = g;
    }
     
    /**
     * Este m�todo permite obtener si el escenario se encuentra guardado en disco o
     * solo est� en memoria.
     * @return TRUE, si el fichero est� almacenado en disco. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean obtenerGuardado() {
        return guardado;
    }
     
    /**
     * Este m�todo permite establecer si el escenario se ha modificado desde la ultima
     * vez que se guard� en disco o no.
     * @param m TRUE, si queremos que el escenario aparezca como modificado. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public void ponerModificado(boolean m) {
        modificado = m;
    }
    
    /**
     * Este m�todo permite obtener si el escenario se ha modificado despu�s de haberse
     * guardado en disco o no.
     * @return TRUE, si el escenario est� modificado con respecto al disco. FALSE en caso
     * contrario.
     * @since 1.0
     */    
    public boolean obtenerModificado() {
        return modificado;
    }
    
    
    private String titulo;
    private String autor;
    private String descripcion;
    private TTopology topologia;
    private TSimulation simulacion;
    private File ficheroEscenario;
    private boolean guardado;
    private boolean modificado;
}
