package simMPLS.entradasalida.osm;

import java.io.*;
import java.util.zip.*;
import simMPLS.escenario.*;
import java.util.*;

/**
 * Esta clase implementa un cargador de escenarios de openSimMPLS 1.0
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @since 1.0
 */
public class TAlmacenadorOSM {
    
    /**
     * Este método crea una nueva instancia de TAlmacenadorOSM.
     * @param e Escenario que se desea almacenar en disco.
     * @since 1.0
     */
    public TAlmacenadorOSM(TEscenario e) {
        escenario = e;
        flujoDeSalida = null;
        salida = null;
        crc = new CRC32();
    }
    
    /**
     * Este método almacena el escenario en disco, en el fichero especificado.
     * @return TRUE, si se ha almacenado correctamente. FALSE en caso contrario.
     * @since 1.0
     * @param conCRC TRUE indica que se almacene en el fichero un código CRC para comprobar la
     * integridad.
     * @param ficheroSalida El fichero de disco en el que se desea almacenar el escenario.
     */    
    public boolean almacenar(File ficheroSalida, boolean conCRC) {
        try {
            TNodoTopologia nt;
            TEnlaceTopologia et;
            Iterator in;
            flujoDeSalida = new FileOutputStream(ficheroSalida);
            salida = new PrintStream(flujoDeSalida);
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.GeneradoPor"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.blanco"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.NoSeDebeModificarEsteFichero"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.PorqueIncorporaUnCodigoCRCParaQue"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.SimuladorPuedaComprobarSuIntegridad"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.ElSimuladorLoPodriaDetectarComoUn"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.FicheroCorrupto"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            salida.println();
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.DefinicionGlobalDelEscenario"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            salida.println();
            salida.println("@?Escenario");
            crc.update("@?Escenario".getBytes());
            salida.println();
            salida.println(this.escenario.serializarTitulo());
            crc.update(this.escenario.serializarTitulo().getBytes());
            salida.println(this.escenario.serializarAutor());
            crc.update(this.escenario.serializarAutor().getBytes());
            salida.println(this.escenario.serializarDescripcion());
            crc.update(this.escenario.serializarDescripcion().getBytes());
            salida.println(this.escenario.obtenerSimulacion().serializarParametrosTemporales());
            crc.update(this.escenario.obtenerSimulacion().serializarParametrosTemporales().getBytes());
            salida.println();
            salida.println("@!Escenario");
            crc.update("@!Escenario".getBytes());
            salida.println();
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.DefinicionDeLaTopologiaDelEscenario"));
            salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
            salida.println();
            salida.println("@?Topologia");
            salida.println();
            crc.update("@?Topologia".getBytes());
            // Volcamos los receptores.
            in = escenario.obtenerTopologia().obtenerIteradorNodos();
            while (in.hasNext()) {
                nt = (TNodoTopologia) in.next();
                if (nt != null) {
                    if (nt.obtenerTipo() == TNodoTopologia.RECEPTOR) {
                        salida.println(nt.serializar());
                        crc.update(nt.serializar().getBytes());
                    }
                }
            }
            // Volcamos el resto de nodos después
            in = escenario.obtenerTopologia().obtenerIteradorNodos();
            while (in.hasNext()) {
                nt = (TNodoTopologia) in.next();
                if (nt != null) {
                    if (nt.obtenerTipo() != TNodoTopologia.RECEPTOR) {
                        salida.println(nt.serializar());
                        crc.update(nt.serializar().getBytes());
                    }
                }
            }
            // Volcamos al final los enlaces
            in = escenario.obtenerTopologia().obtenerIteradorEnlaces();
            while (in.hasNext()) {
                et = (TEnlaceTopologia) in.next();
                if (et != null) {
                    salida.println(et.serializar());
                    crc.update(et.serializar().getBytes());
                }
            }
            salida.println();
            salida.println("@!Topologia");
            crc.update("@!Topologia".getBytes());
            if (conCRC) {
                String cadenaCrc = Long.toString(crc.getValue());
                salida.println();
                salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
                salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.CodigoCRCParaLaIntegridadDelFichero"));
                salida.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TAlmacenadorOSM.asteriscos"));
                salida.println();
                salida.println("@CRC#" + cadenaCrc);
            }
            flujoDeSalida.close();
            salida.close();
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }
    
    private CRC32 crc;
    private TEscenario escenario;
    private FileOutputStream flujoDeSalida;
    private PrintStream salida;
}
