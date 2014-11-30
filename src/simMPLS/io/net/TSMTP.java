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
package simMPLS.io.net;

import java.net.*;
import java.io.*;

/**
 * Esta clase contiene los m�todos necesarios para poder enviar correos
 * electronicos desde programas Java interactuando directamente con el protocolo
 * SMTP v�a sockets.
 * @author <B>Manuel Dom�nguez Dorado</B><br><A
 * href="mailto:ingeniero@ManoloDominguez.com">ingeniero@ManoloDominguez.com</A><br><A href="http://www.ManoloDominguez.com" target="_blank">http://www.ManoloDominguez.com</A>
 * @version 1.0
 */
public class TSMTP {

    /** Constante de la clase que indica que el env�o del email ha sido satisfactorio
     * @since 1.0
     */    
    public static final int CORRECTO = 0;
    /** Constante para indicar que cuando se ha intentado poner en contacto con el
     * servidor remoto, ha sido imposible.
     * @since 1.01
     */    
    public static final int ERROR_CONECTANDO = -1;
    /** Constante para indicar que cuando hemos anunciado nuestro dominio al servidor
     * SMTP remoto, ha devuelto un c�digo de error.
     * @since 1.0
     */    
    public static final int ERROR_DOMINIO = -2;
    /** Constante para indicar que cuando hemos anunciado nuestro email al servidor SMTP
     * remoto, ha contestado con un codigo de error.
     * @since 1.0
     */    
    public static final int ERROR_REMITENTE = -3;
    /** Constante para indicar que cuando hemos anunciado al seridor SMTP remoto la
     * direcci�n del destinatario del correo, ha contestado con un error.
     * @since 1.0
     */    
    public static final int ERROR_DESTINATARIO = -4;
    /** Constante para indicar que cuando hemos comenzado a transferir los datos del
     * email, ha ocurrido un error.
     * @since 1.0
     */    
    public static final int ERROR_TRANSFIRIENDO = -5;
    /** Constnte para indicar que cuando hemos finalizado los datos y esperamos el env�o
     * del email, ha ocurrido un error.
     * @since 1.0
     */    
    public static final int ERROR_ENVIAR = -6;

    /** Direcci�n de internet del servidor SMTP que transportar� el email.
     * @since 1.0
     */    
    private InetAddress HostSMTP;
    /** Direcci�n de internet local del PC desde donde estamos mandando el correo.
     * @since 1.0
     */    
    private InetAddress Dominio2;
    /** Puerto del servidor SMTP remoto donde estar� escuchando el servidor de correo
     * para enviar nuestro mensaje.
     * @since 1.0
     */    
    private int PuertoSMTP;
    /** Direcci�n de correo electr�nicao de la persona que env�a el correo electronico.
     * @since 1.0
     */    
    private String RemitenteSMTP;
    /** Direcci�n de correo electronico de la persona que ha de recibir el correo.
     * @since 1.0
     */    
    private String DestinatarioSMTP;
    /** Dominio local del PC desde el que mandamos el correo electr�nico, pero en forma
     * de cadena de texto.
     * @since 1.0
     */    
    private String Dominio;
    
    /** Este m�todo es el constructor de la clase. Incia los atributos de la clase con un valor que no de
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
     * Inicia los atributos de la clase con valores v�lidos para enviar un correo
     * electr�nico.
     * @param Host Servidor de correo saliente que se va a usar para enviar el correo.
     * @param Puerto Puerto en el que est� escuchando el servidor de correo saliente.
     * @param Remitente Direcci�n de correo electr�nico del remitente.
     * @param Destinatario Direcci�n de correo electr�nico del destinatario.
     * @return 0 si la inciaci�n ha sido correcta. -1 si no se ha podido resolver la direcci�n del servidor SMTP.
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
     * Inicia los atributos de la clase con valores v�lidos para enviar un correo
     * electr�nico. por defecto prepara la instancia para enviar correos a los autores
     * de Open SimMPLS 1.0
     * @param Host La direcci�n de internet del servidor SMTP que enviar� el correo.
     * @param Remitente La persona que env�a el mensaje (su email).
     * @return 0 si la inciaci�n ha sido correcta. -1 si no se ha podido resolver la direcci�n del servidor SMTP.
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
            DestinatarioSMTP = "opensimmpls@manolodominguez.com";
            return 0;
        }
        catch (Exception e) {
            return this.ERROR_CONECTANDO;
        }
    }

    
    /** Este m�todo env�a un mensaje de correo electronico usando la instancia de la clase TSMTP a la que
     * pertenece.
     * @param Notificacion Mensaje que se env�a por correo electr�nico.
     * @return Alguna de las constantes definidas al comienzo de esta clase, indicando el
     * estado general del env�o del correo electronico.
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