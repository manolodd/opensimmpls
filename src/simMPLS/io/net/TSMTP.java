/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package simMPLS.io.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class implements a class that uses SMTP to send email messages.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSMTP {

    /**
     * This method inputStream the constructor of the class. It creates a new
     * instance of TSMTP.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TSMTP() {
        this.remoteSMTPPort = 0;
        this.remoteSMTPUser = "";
        this.receiverEmailAddress = "";
        this.localDomain = "";
        this.wellConfigured = false;
    }

    /**
     * This method configures the current TSMTP instance with correct values to
     * send email messages.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param remoteSMTPHost remote SMTP hostname.
     * @param remoteSMTPPort remote SMTP port.
     * @param remoteSMTPUser remote SMTP user.
     * @param receiverEmailAddress Receiver email address.
     * @return 0 if the TSMTP instance inputStream weel configured. Otherwise,
     * returns TSMTP.CONNECTION_ERROR.
     * @since 2.0
     */
    public int configure(String remoteSMTPHost, String remoteSMTPPort, String remoteSMTPUser, String receiverEmailAddress) {
        try {
            boolean slashFoundInLocalDomain = false;
            int localDomainSlashPosition = -1;
            int localDomainLength = 0;
            this.remoteSMTPHost = InetAddress.getByName(remoteSMTPHost);
            this.localIPv4Address = InetAddress.getLocalHost();
            this.localDomain = this.localIPv4Address.toString();
            localDomainLength = this.localDomain.length();
            for (int i = 0; i < localDomainLength; i++) {
                if (this.localDomain.toCharArray()[i] == '/') {
                    localDomainSlashPosition = i;
                    slashFoundInLocalDomain = true;
                }
            }
            if (slashFoundInLocalDomain) {
                this.localDomain = this.localDomain.substring(localDomainSlashPosition + 1, localDomainLength);
            }
            this.remoteSMTPPort = Integer.parseInt(remoteSMTPPort);
            this.remoteSMTPUser = remoteSMTPUser;
            this.receiverEmailAddress = receiverEmailAddress;
            this.wellConfigured = true;
            return 0;
        } catch (UnknownHostException | NumberFormatException e) {
            this.wellConfigured = false;
            return TSMTP.CONNECTION_ERROR;
        }
    }

    /**
     * This method configures the current TSMTP instance with correct values to
     * send email messages. Use standardized values for SMTP port and configure
     * the TSMTP instance to send the notification to Open SimMPLS authors.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param remoteSMTPHost remote SMTP hostname.
     * @param receiverEmailAddress Receiver email address.
     * @return 0 if the TSMTP instance inputStream weel configured. Otherwise,
     * returns TSMTP.CONNECTION_ERROR.
     * @since 2.0
     */
    public int configure(String remoteSMTPHost, String receiverEmailAddress) {
        try {
            boolean slashFoundInLocalDomain = false;
            int localDomainSlashPosition = -1;
            int localDomainLength = 0;
            this.remoteSMTPHost = InetAddress.getByName(remoteSMTPHost);
            this.localIPv4Address = InetAddress.getLocalHost();
            this.localDomain = this.localIPv4Address.toString();
            localDomainLength = this.localDomain.length();
            for (int i = 0; i < localDomainLength; i++) {
                if (this.localDomain.toCharArray()[i] == '/') {
                    localDomainSlashPosition = i;
                    slashFoundInLocalDomain = true;
                }
            }
            if (slashFoundInLocalDomain) {
                this.localDomain = this.localDomain.substring(localDomainSlashPosition + 1, localDomainLength);
            }
            this.remoteSMTPPort = 25;
            this.remoteSMTPUser = receiverEmailAddress;
            this.receiverEmailAddress = "opensimmpls@manolodominguez.com";
            this.wellConfigured = true;
            return 0;
        } catch (UnknownHostException e) {
            this.wellConfigured = false;
            return TSMTP.CONNECTION_ERROR;
        }
    }

    /**
     * This method send an email message using the current configuration of this
     * TSMTP instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param messageBody The message to be sent.
     * @return one of the constants defined in TSMTP.
     */
    public int sendNotification(String messageBody) {
        if (this.wellConfigured) {
            String remoteSMTPHostResponse = "";
            try {
                Socket SocketSMTP = new Socket(this.remoteSMTPHost, this.remoteSMTPPort);
                InputStream inputStream = SocketSMTP.getInputStream();
                OutputStream outputStream = SocketSMTP.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                PrintStream printStream = new PrintStream(outputStream);
                bufferedReader.readLine();
                printStream.println("HELO " + this.localDomain);
                remoteSMTPHostResponse = bufferedReader.readLine();
                if (remoteSMTPHostResponse.startsWith("250")) {
                    printStream.println("MAIL FROM:<" + this.remoteSMTPUser + ">");
                    remoteSMTPHostResponse = bufferedReader.readLine();
                    if (remoteSMTPHostResponse.startsWith("250")) {
                        printStream.println("RCPT TO:<" + this.receiverEmailAddress + ">");
                        remoteSMTPHostResponse = bufferedReader.readLine();
                        if ((remoteSMTPHostResponse.startsWith("250")) || (remoteSMTPHostResponse.startsWith("251"))) {
                            printStream.println("DATA");
                            remoteSMTPHostResponse = bufferedReader.readLine();
                            if (remoteSMTPHostResponse.startsWith("354")) {
                                printStream.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.Subject"));
                                printStream.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.XSoftware"));
                                printStream.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.Programador"));
                                printStream.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.ProgramadorWeb"));
                                printStream.println(java.util.ResourceBundle.getBundle("simMPLS/lenguajes/lenguajes").getString("TSMTP.ProgramadorEmail"));
                                printStream.println(messageBody);
                                printStream.println(".");
                                remoteSMTPHostResponse = bufferedReader.readLine();
                                if (remoteSMTPHostResponse.startsWith("250")) {
                                    do {
                                        printStream.println("QUIT");
                                        remoteSMTPHostResponse = bufferedReader.readLine();
                                    } while (!remoteSMTPHostResponse.startsWith("221"));
                                    return TSMTP.SUCCESSFUL;
                                } else {
                                    return TSMTP.EMAIL_SENDING_ERROR;
                                }
                            } else {
                                return TSMTP.DATA_TRANSFER_ERROR;
                            }
                        } else {
                            return TSMTP.RECEIVER_RECOGNITION_ERROR;
                        }
                    } else {
                        return TSMTP.SENDER_RECOGNITION_ERROR;
                    }
                } else {
                    return TSMTP.DOMAIN_RECOGNITION_ERROR;
                }
            } catch (IOException e) {
                return TSMTP.CONNECTION_ERROR;
            }
        } else {
            return TSMTP.MISCONFIGURATION_ERROR;
        }
    }

    public static final int SUCCESSFUL = 0;
    public static final int CONNECTION_ERROR = -1;
    public static final int DOMAIN_RECOGNITION_ERROR = -2;
    public static final int SENDER_RECOGNITION_ERROR = -3;
    public static final int RECEIVER_RECOGNITION_ERROR = -4;
    public static final int DATA_TRANSFER_ERROR = -5;
    public static final int EMAIL_SENDING_ERROR = -6;
    public static final int MISCONFIGURATION_ERROR = -7;

    private InetAddress remoteSMTPHost;
    private InetAddress localIPv4Address;
    private int remoteSMTPPort;
    private String remoteSMTPUser;
    private String receiverEmailAddress;
    private String localDomain;
    private boolean wellConfigured;
}
