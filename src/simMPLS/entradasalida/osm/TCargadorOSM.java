/*
 * TCargadorOSM.java
 *
 * Created on 27 de julio de 2004, 20:29
 */

package simMPLS.entradasalida.osm;

import java.io.*;
import java.util.zip.*;
import simMPLS.escenario.*;
import simMPLS.interfaz.dialogos.*;

/**
 * Esta clase implementa un cargador de ficheros *.OSM, de Open SimMPLS 1.0
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @since 1.0
 */
public class TCargadorOSM {
    
    /**
     * Crea una nueva instancia de TCargadorOSM
     * @since 1.0
     */
    public TCargadorOSM() {
        escenario = new TEscenario();
        flujoDeEntrada = null;
        entrada = null;
        crc = new CRC32();
        posicion = this.NINGUNO;
}
    
    /**
     * Este método carga desde el disco, del fichero especificado, un escenario.
     * @param ficheroEntrada El fichero de disco que el cargador debe leer para crear en memoria el
     * escenario.
     * @return TRUE, si el escenario se ha cargado correctamente. FALSE en caso contrario.
     * @since 1.0
     */    
    public boolean cargar(File ficheroEntrada) {
        if (this.ficheroEsCorrecto(ficheroEntrada)) {
            String cadena = "";
            escenario.ponerFichero(ficheroEntrada);
            escenario.ponerGuardado(true);
            escenario.ponerModificado(false);
            try {
                if(ficheroEntrada.exists()) {
                    flujoDeEntrada = new FileInputStream(ficheroEntrada);
                    entrada = new BufferedReader(new InputStreamReader(flujoDeEntrada));
                    while ((cadena = entrada.readLine()) != null) {
                        if ((!cadena.equals("")) && (!cadena.startsWith("//")) && (!cadena.startsWith("@CRC#"))) {
                            if (posicion == this.NINGUNO) {
                                if (cadena.startsWith("@?Escenario")) {
                                    this.posicion = this.ESCENARIO;
                                } else if (cadena.startsWith("@?Topologia")) {
                                    this.posicion = this.TOPOLOGIA;
                                } else if (cadena.startsWith("@?Simulacion")) {
                                    this.posicion = this.SIMULACION;
                                } else if (cadena.startsWith("@?Analisis")) {
                                    this.posicion = this.ANALISIS;
                                } 
                            } else if (posicion == this.ESCENARIO) {
                                cargarEscenario(cadena);
                            } else if (posicion == this.TOPOLOGIA) {
                                cargarTopologia(cadena);
                            } else if (posicion == this.SIMULACION) {
                                if (cadena.startsWith("@!Simulacion")) {
                                    this.posicion = this.NINGUNO;
                                }
                            } else if (posicion == this.ANALISIS) {
                                if (cadena.startsWith("@!Analisis")) {
                                    this.posicion = this.NINGUNO;
                                } 
                            }
                        }
                    }
                    flujoDeEntrada.close();
                    entrada.close();
                }
            }
            catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    private void cargarTopologia(String cadena) {
        if (cadena.startsWith("@!Topologia")) {
            this.posicion = this.NINGUNO;
        } else if (cadena.startsWith("#Receptor#")) {
            TNodoReceptor receptor = new TNodoReceptor(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (receptor.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(receptor);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(receptor.obtenerIdentificador());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(receptor.obtenerIP());
            }
            receptor = null;
        } else if (cadena.startsWith("#Emisor#")) {
            TNodoEmisor emisor = new TNodoEmisor(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (emisor.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(emisor);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(emisor.obtenerIdentificador());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(emisor.obtenerIP());
            }
            emisor = null;
        } else if (cadena.startsWith("#LER#")) {
            TNodoLER ler = new TNodoLER(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (ler.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(ler);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(ler.obtenerIdentificador());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(ler.obtenerIP());
            }
            ler = null;
        } else if (cadena.startsWith("#LERA#")) {
            TNodoLERA lera = new TNodoLERA(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (lera.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(lera);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(lera.obtenerIdentificador());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(lera.obtenerIP());
            }
            lera = null;
        } else if (cadena.startsWith("#LSR#")) {
            TNodoLSR lsr = new TNodoLSR(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (lsr.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(lsr);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(lsr.obtenerIdentificador());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(lsr.obtenerIP());
            }
            lsr = null;
        } else if (cadena.startsWith("#LSRA#")) {
            TNodoLSRA lsra = new TNodoLSRA(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (lsra.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(lsra);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(lsra.obtenerIdentificador());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(lsra.obtenerIP());
            }
            lsra = null;
        } else if (cadena.startsWith("#EnlaceExterno#")) {
            TEnlaceExterno externo = new TEnlaceExterno(0, escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (externo.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarEnlace(externo);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(externo.obtenerIdentificador());
            }
            externo = null;
        } else if (cadena.startsWith("#EnlaceInterno#")) {
            TEnlaceInterno interno = new TEnlaceInterno(0, escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (interno.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarEnlace(interno);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(interno.obtenerIdentificador());
            }
            interno = null;
        }
    }

    private void cargarEscenario(String cadena) {
        if (cadena.startsWith("@!Escenario")) {
            this.posicion = this.NINGUNO;
        } else if (cadena.startsWith("#Titulo#")) {
            if (!this.escenario.deserializarTitulo(cadena)) {
                this.escenario.ponerTitulo("");
            }
        } else if (cadena.startsWith("#Autor#")) {
            if (!this.escenario.deserializarAutor(cadena)) {
                this.escenario.ponerAutor("");
            }
        } else if (cadena.startsWith("#Descripcion#")) {
            if (!this.escenario.deserializarDescripcion(cadena)) {
                this.escenario.ponerDescripcion("");
            }
        } else if (cadena.startsWith("#Temporizacion#")) {
            if (!this.escenario.obtenerSimulacion().deserializarParametrosTemporales(cadena)) {
                this.escenario.obtenerSimulacion().ponerDuracion(500);
                this.escenario.obtenerSimulacion().ponerPaso(1);
            }
        }
    }
    
    /**
     * Este método devuelve el escenario que el cargador ha creado en memoria tras leer
     * elfichero asociado en disco.
     * @return El escenario correctamente creado en memoria.
     * @since 1.0
     */    
    public TEscenario obtenerEscenario() {
        return escenario;
    }
    
    private boolean ficheroEsCorrecto(File f) {
        String CRCDelFichero = "";
        String CRCCalculado = "@CRC#";
        crc.reset();
        String cadena = "";
        try {
            if(f.exists()) {
                FileInputStream fEntrada = new FileInputStream(f);
                BufferedReader ent = new BufferedReader(new InputStreamReader(fEntrada));
                while ((cadena = ent.readLine()) != null) {
                    if ((!cadena.equals("")) && (!cadena.startsWith("//"))) {
                        if (cadena.startsWith("@CRC#")) {
                            CRCDelFichero = cadena;
                        } else {
                            crc.update(cadena.getBytes());
                        }
                    }
                }
                fEntrada.close();
                ent.close();
                if (CRCDelFichero.equals("")) {
                    return true;
                } else {
                    CRCCalculado += Long.toString(crc.getValue());
                    if (CRCCalculado.equals(CRCDelFichero)) {
                        return true;
                    }
                    return false;
                }
            }
        }
        catch (IOException e) {
            crc.reset();
            return false;
        }
        crc.reset();
        return false;
    }
    
    private static final int NINGUNO = 0;
    private static final int ESCENARIO = 1;
    private static final int TOPOLOGIA = 2;
    private static final int SIMULACION = 3;
    private static final int ANALISIS = 4;

    private int posicion;
    private CRC32 crc;
    private TEscenario escenario;
    private FileInputStream flujoDeEntrada;
    private BufferedReader entrada;
}
