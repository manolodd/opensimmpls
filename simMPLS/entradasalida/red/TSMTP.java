package simMPLS.entradasalida.red;

import java.net.*;
import java.io.*;

/**
 * Esta clase contiene los métodos necesarios para poder enviar correos
 * electronicos desde programas Java interactuando directamente con el protocolo
 * SMTP vía sockets.
 * @author <B>Manuel Domínguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSMTP {

    /** Constante de la clase que indica que el envío del email ha sido satisfactorio
     * @since 1.0
     */    
    public static final int CORRECTO = 0;
    /** Constante para indicar que cuando se ha intentado poner en contacto con el
     * servidor remoto, ha sido imposible.
     * @since 1.01
     */    
    public static final int ERROR_CONECTANDO = -1;
    /** Constante para indicar que cuando hemos anunciado nuestro dominio al servidor
     * SMTP remoto, ha devuelto un código de error.
     * @since 1.0
     */    
    public static final int ERROR_DOMINIO = -2;
    /** Constante para indicar que cuando hemos anunciado nuestro email al servidor SMTP
     * remoto, ha contestado con un codigo de error.
     * @since 1.0
     */    
    public static final int ERROR_REMITENTE = -3;
    /** Constante para indicar que cuando hemos anunciado al seridor SMTP remoto la
     * dirección del destinatario del correo, ha contestado con un error.
     * @since 1.0
     */    
    public static final int ERROR_DESTINATARIO = -4;
    /** Constante para indicar que cuando hemos comenzado a transferir los datos del
     * email, ha ocurrido un error.
     * @since 1.0
     */    
    public static final int ERROR_TRANSFIRIENDO = -5;
    /** Constnte para indicar que cuando hemos finalizado los datos y esperamos el envío
     * del email, ha ocurrido un error.
     * @since 1.0
     */    
    public static final int ERROR_ENVIAR = -6;

    /** Dirección de internet del servidor SMTP que transportará el email.
     * @since 1.0
     */    
    private InetAddress HostSMTP;
    /** Dirección de internet local del PC desde donde estamos mandando el correo.
     * @since 1.0
     */    
    private InetAddress Dominio2;
    /** Puerto del servidor SMTP remoto donde estará escuchando el servidor de correo
     * para enviar nuestro mensaje.
     * @since 1.0
     */    
    private int PuertoSMTP;
    /** Dirección de correo electrónicao de la persona que envía el correo electronico.
     * @since 1.0
     */    
    private String RemitenteSMTP;
    /** Dirección de correo electronico de la persona que ha de recibir el correo.
     * @since 1.0
     */    
    private String DestinatarioSMTP;
    /** Dominio local del PC desde el que mandamos el correo electrónico, pero en forma
     * de cadena de texto.
     * @since 1.0
     */    
    private String Dominio;
    
    /** Este método es el constructor de la clase. Incia los atributos de la clase con un valor que no de
     * errores.
     * @since 1.0
     */    
    public TSMTP() {
        PuertoSMTP = 0;
        RemitenteSMTP = "";
        DestinatarioSMTP = "";
        Dominio = "";
    }
    
    /**
     * Inicia los atributos de la clase con valores válidos para enviar un correo
     * electrónico.
     * @param Host Servidor de correo saliente que se va a usar para enviar el correo.
     * @param Puerto Puerto en el que está escuchando el servidor de correo saliente.
     * @param Remitente Dirección de correo electrónico del remitente.
     * @param Destinatario Dirección de correo electrónico del destinatario.
     * @return 0 si la inciación ha sido correcta. -1 si no se ha podido resolver la dirección del servidor SMTP.
     * @since 1.0
     */    
    public int iniciarSMTP(String Host, String Puerto, String Remitente, String Destinatario) {
        try {
            Integer IntAux = new Integer(0);
            boolean bandera= false;
            int posicion=-1;
            int longitud=0;
            HostSMTP = InetAddress.getByName(Host);
            Dominio2 = InetAddress.getLocalHost();
            Dominio = Dominio2.toString();
            longitud=Dominio.length();
            for (int i=0; i<longitud; i++) {
                if (Dominio.toCharArray()[i]=='/') {
                    posicion=i;
                    bandera=true;
                }
            }
            if (bandera) {
                Dominio=Dominio.substring(posicion+1,longitud);
            }
            PuertoSMTP = IntAux.parseInt(Puerto);
            RemitenteSMTP = Remitente;
            DestinatarioSMTP = Destinatario;
            return 0;
        }
        catch (Exception e) {
            return this.ERROR_CONECTANDO;
        }
    }

    /**
     * Inicia los atributos de la clase con valores válidos para enviar un correo
     * electrónico. por defecto prepara la instancia para enviar correos a los autores
     * de Open SimMPLS 1.0
     * @param Host La dirección de internet del servidor SMTP que enviará el correo.
     * @param Remitente La persona que envía el mensaje (su email).
     * @return 0 si la inciación ha sido correcta. -1 si no se ha podido resolver la dirección del servidor SMTP.
     * @since 1.0
     */    
    public int iniciarSMTP(String Host, String Remitente) {
        try {
            Integer IntAux = new Integer(0);
            boolean bandera= false;
            int posicion=-1;
            int longitud=0;
            HostSMTP = InetAddress.getByName(Host);
            Dominio2 = InetAddress.getLocalHost();
            Dominio = Dominio2.toString();
            longitud=Dominio.length();
            for (int i=0; i<longitud; i++) {
                if (Dominio.toCharArray()[i]=='/') {
                    posicion=i;
                    bandera=true;
                }
            }
            if (bandera) {
                Dominio=Dominio.substring(posicion+1,longitud);
            }
            PuertoSMTP = 25;
            RemitenteSMTP = Remitente;
            DestinatarioSMTP = "opensimmpls@patanegra.unex.es";
            return 0;
        }
        catch (Exception e) {
            return this.ERROR_CONECTANDO;
        }
    }

    
    /** Este método envía un mensaje de correo electronico usando la instancia de la clase TSMTP a la que
     * pertenece.
     * @param Notificacion Mensaje que se envía por correo electrónico.
     * @return Alguna de las constantes definidas al comienzo de esta clase, indicando el
     * estado general del envío del correo electronico.
     */    
    public int enviarNotificacion(String Notificacion) {
        String buff = "";
        try {
            Socket SocketSMTP = new Socket(HostSMTP, PuertoSMTP);
            InputStream is = SocketSMTP.getInputStream();
            OutputStream os = SocketSMTP.getOutputStream();
            BufferedReader dis = new BufferedReader(new InputStreamReader(is));
            PrintStream ps = new PrintStream(os);
            buff = dis.readLine();
            ps.println("HELO "+Dominio);
            buff = dis.readLine();
            if (buff.startsWith("250")) {
                ps.println("MAIL FROM:<"+RemitenteSMTP+">");
                buff = dis.readLine();
                if (buff.startsWith("250")) {
                    ps.println("RCPT TO:<"+DestinatarioSMTP+">");
                    buff = dis.readLine();
                    if ((buff.startsWith("250")) || (buff.startsWith("251"))) {
                        ps.println("DATA");
                        buff = dis.readLine();
                        if (buff.startsWith("354")) {
                            ps.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.Subject"));
                            ps.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.XSoftware"));
                            ps.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.Programador"));
                            ps.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.ProgramadorWeb"));
                            ps.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.ProgramadorEmail"));
                            ps.println(Notificacion);
                            ps.println(".");
                            buff = dis.readLine();
                            if (buff.startsWith("250")) {
                                do {
                                    ps.println("QUIT");
                                    buff = dis.readLine();
                                }
                                while (!buff.startsWith("221"));
                                return this.CORRECTO;
                            }
                            else
                                return this.ERROR_ENVIAR;
                        }
                        else
                            return this.ERROR_TRANSFIRIENDO;
                    }
                    else
                        return this.ERROR_DESTINATARIO;
                }
                else
                    return this.ERROR_REMITENTE;
            }
            else
                return this.ERROR_DOMINIO;
        }
        catch (Exception e) {
            return this.ERROR_CONECTANDO;
        }
    }
}