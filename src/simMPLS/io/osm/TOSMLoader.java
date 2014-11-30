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
package simMPLS.io.osm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.CRC32;
import simMPLS.scenario.TExternalLink;
import simMPLS.scenario.TInternalLink;
import simMPLS.scenario.TLERANode;
import simMPLS.scenario.TLERNode;
import simMPLS.scenario.TLSRANode;
import simMPLS.scenario.TLSRNode;
import simMPLS.scenario.TReceiverNode;
import simMPLS.scenario.TScenario;
import simMPLS.scenario.TSenderNode;

/**
 * Esta clase implementa un cargador de ficheros *.OSM, de Open SimMPLS 1.0
 *
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A
 * href="http://www.ManoloDominguez.com"
 * target="_blank">http://www.ManoloDominguez.com</A>
 * @since 1.0
 */
public class TOSMLoader {

    /**
     * Crea una nueva instancia de TCargadorOSM
     *
     * @since 1.0
     */
    public TOSMLoader() {
        escenario = new TScenario();
        flujoDeEntrada = null;
        entrada = null;
        crc = new CRC32();
        posicion = TOSMLoader.NINGUNO;
    }

    /**
     * Este m�todo carga desde el disco, del fichero especificado, un escenario.
     *
     * @param ficheroEntrada El fichero de disco que el cargador debe leer para
     * crear en memoria el escenario.
     * @return TRUE, si el escenario se ha cargado correctamente. FALSE en caso
     * contrario.
     * @since 1.0
     */
    public boolean cargar(File ficheroEntrada) {
        if (this.ficheroEsCorrecto(ficheroEntrada)) {
            String cadena = "";
            escenario.ponerFichero(ficheroEntrada);
            escenario.ponerGuardado(true);
            escenario.ponerModificado(false);
            try {
                if (ficheroEntrada.exists()) {
                    flujoDeEntrada = new FileInputStream(ficheroEntrada);
                    entrada = new BufferedReader(new InputStreamReader(flujoDeEntrada));
                    while ((cadena = entrada.readLine()) != null) {
                        if ((!cadena.equals("")) && (!cadena.startsWith("//")) && (!cadena.startsWith("@CRC#"))) {
                            if (posicion == TOSMLoader.NINGUNO) {
                                if (cadena.startsWith("@?Escenario")) {
                                    this.posicion = TOSMLoader.ESCENARIO;
                                } else if (cadena.startsWith("@?Topologia")) {
                                    this.posicion = TOSMLoader.TOPOLOGIA;
                                } else if (cadena.startsWith("@?Simulacion")) {
                                    this.posicion = TOSMLoader.SIMULACION;
                                } else if (cadena.startsWith("@?Analisis")) {
                                    this.posicion = TOSMLoader.ANALISIS;
                                }
                            } else if (posicion == TOSMLoader.ESCENARIO) {
                                cargarEscenario(cadena);
                            } else if (posicion == TOSMLoader.TOPOLOGIA) {
                                cargarTopologia(cadena);
                            } else if (posicion == TOSMLoader.SIMULACION) {
                                if (cadena.startsWith("@!Simulacion")) {
                                    this.posicion = TOSMLoader.NINGUNO;
                                }
                            } else if (posicion == TOSMLoader.ANALISIS) {
                                if (cadena.startsWith("@!Analisis")) {
                                    this.posicion = TOSMLoader.NINGUNO;
                                }
                            }
                        }
                    }
                    flujoDeEntrada.close();
                    entrada.close();
                }
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void cargarTopologia(String cadena) {
        if (cadena.startsWith("@!Topologia")) {
            this.posicion = TOSMLoader.NINGUNO;
        } else if (cadena.startsWith("#Receptor#")) {
            TReceiverNode receptor = new TReceiverNode(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (receptor.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(receptor);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(receptor.getID());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(receptor.getIPAddress());
            }
            receptor = null;
        } else if (cadena.startsWith("#Emisor#")) {
            TSenderNode emisor = new TSenderNode(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (emisor.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(emisor);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(emisor.getID());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(emisor.getIPAddress());
            }
            emisor = null;
        } else if (cadena.startsWith("#LER#")) {
            TLERNode ler = new TLERNode(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (ler.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(ler);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(ler.getID());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(ler.getIPAddress());
            }
            ler = null;
        } else if (cadena.startsWith("#LERA#")) {
            TLERANode lera = new TLERANode(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (lera.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(lera);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(lera.getID());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(lera.getIPAddress());
            }
            lera = null;
        } else if (cadena.startsWith("#LSR#")) {
            TLSRNode lsr = new TLSRNode(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (lsr.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(lsr);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(lsr.getID());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(lsr.getIPAddress());
            }
            lsr = null;
        } else if (cadena.startsWith("#LSRA#")) {
            TLSRANode lsra = new TLSRANode(0, "10.0.0.1", escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (lsra.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarNodo(lsra);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(lsra.getID());
                escenario.obtenerTopologia().obtenerGeneradorIP().ponerValorSiMayor(lsra.getIPAddress());
            }
            lsra = null;
        } else if (cadena.startsWith("#EnlaceExterno#")) {
            TExternalLink externo = new TExternalLink(0, escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (externo.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarEnlace(externo);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(externo.getID());
            }
            externo = null;
        } else if (cadena.startsWith("#EnlaceInterno#")) {
            TInternalLink interno = new TInternalLink(0, escenario.obtenerTopologia().obtenerGeneradorIDEvento(), escenario.obtenerTopologia());
            if (interno.desSerializar(cadena)) {
                escenario.obtenerTopologia().insertarEnlace(interno);
                escenario.obtenerTopologia().obtenerGeneradorIdentificadorElmto().ponerIdentificadorSiMayor(interno.getID());
            }
            interno = null;
        }
    }

    private void cargarEscenario(String cadena) {
        if (cadena.startsWith("@!Escenario")) {
            this.posicion = TOSMLoader.NINGUNO;
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
     * Este m�todo devuelve el escenario que el cargador ha creado en memoria
     * tras leer elfichero asociado en disco.
     *
     * @return El escenario correctamente creado en memoria.
     * @since 1.0
     */
    public TScenario obtenerEscenario() {
        return escenario;
    }

    private boolean ficheroEsCorrecto(File f) {
        String CRCDelFichero = "";
        String CRCCalculado = "@CRC#";
        crc.reset();
        String cadena = "";
        try {
            if (f.exists()) {
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
        } catch (IOException e) {
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
    private TScenario escenario;
    private FileInputStream flujoDeEntrada;
    private BufferedReader entrada;
}
