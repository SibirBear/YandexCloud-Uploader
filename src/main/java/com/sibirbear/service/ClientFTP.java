package com.sibirbear.service;

import com.sibirbear.config.Config;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientFTP {

    private final String server;
    private final int port;
    private final String user;
    private final String pass;
    private FTPClient ftpClient;
    private List<String> listFilesFromFTP;

    private final Logger log = LogManager.getLogger(this.getClass());

    public ClientFTP(final Config config) {
        this.server = config.getServerFtp();
        this.port = config.getPortFtp();
        this.user = config.getUserFtp();
        this.pass = config.getPassFtp();
    }

    /*
     * The method creates a connection to the FTP Server
     */

    public void open() throws IOException {
        this.ftpClient = new FTPClient();

        log.info("Connect to FTP Server...");
        ftpClient.connect(server, port);
        ftpClient.enterLocalPassiveMode();

        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            log.error("Invalid config properties to FTP Server: " + reply);
            TelegramNotification.sendMessage(TelegramNotificationType.ERROR_FTP);
            throw new IOException("Exception in connecting to FTP Server.");
        }

        log.info("Login to FTP Server...");
        boolean logFtpS = ftpClient.login(user, pass);

        reply = ftpClient.getReplyCode();
        if (!logFtpS) {
            log.error("Invalid login/pass to access FTP: " + reply);
            TelegramNotification.sendMessage(TelegramNotificationType.ERROR_FTP);
            throw new IOException("Exception in connecting to FTP Server (user/login).");
        }

        log.info("Connection to the FTP is successful.");

    }

    /*
     * Method close connection to created FTP server
     */
    public  void close() {
        log.info("Trying to disconnect FTP Server...");
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            log.info("Disconnect FTP Server successful!");
        } catch (IOException e) {
            log.error("ERROR! There is no connection to remove. " + e.getMessage());
        }
    }

    /*
     * Method generates a list of files (only them, because the method in the filter
     * was redefined) in the form of a String collection
     */
    public List<String> getListFilesFromFTP(String path) {
        listFilesFromFTP = Collections.synchronizedList(new ArrayList<>());

        try {
            FTPFile[] files = ftpClient.listFiles(path);
            for (FTPFile file : files) {
                if (file.getType() == FTPFile.FILE_TYPE) {
                    String name = file.getName();
                    listFilesFromFTP.add(name);
                }
            }
        } catch (IOException e) {
            log.error("ERROR reading files at FTP server! " + e.getMessage());
        }

        return listFilesFromFTP;

    }

    /*
     * The Method to retrieve file from FTP
     */
    public void retrieveFileFromFTP(String source, FileOutputStream fos) throws IOException {
        ftpClient.retrieveFile(source, fos);
    }

    /*
     * The method to delete file from FTP which downloaded
     */
    public void deleteFile(String source) throws IOException {
        ftpClient.deleteFile(source);
    }

}
