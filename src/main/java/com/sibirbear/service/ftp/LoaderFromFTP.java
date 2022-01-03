package com.sibirbear.service.ftp;

import com.sibirbear.config.Config;
import com.sibirbear.service.telegram.TelegramNotification;
import com.sibirbear.service.telegram.TelegramNotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class LoaderFromFTP {

    private final Logger log = LogManager.getLogger(this.getClass());
    private final String locationFiles;
    private final String destinationFiles;
    private final ClientFTP clientFTP;

    private boolean isFileDownloading = true;
    private List<String> listFiles;
    private int count;

    public LoaderFromFTP(final Config config, final ClientFTP clientFTP) {
        this.locationFiles = config.getPathToFtp();
        this.destinationFiles = config.getPathToFiles();
        this.clientFTP = clientFTP;
    }

    /*
     * The method reads files from FTP storage dir and writes it to the temp
     * directory defined in property file
     */
    public void loadFromFTP() {
        log.info("Loading files from FTP storage to the Tem dir...");

        listFiles = clientFTP.getListFilesFromFTP(locationFiles);
        count = 0;

        if (!listFiles.isEmpty()) {
            for (String file : listFiles) {
                downloadFile(clientFTP, locationFiles + file, destinationFiles + file);
            }
            deleteFiles();
        } else {
            log.info("FTP storage is empty.");
        }

        log.info("Count of downloading files from FTP storage: " + count);
    }

    /*
     * The method for uploading file from FTP storage to the temp directory
     * defined in property file
     */
    private void downloadFile(ClientFTP clientFTP, String source, String destination) {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            clientFTP.retrieveFileFromFTP(source, fos);
            count++;
        } catch (IOException e) {
            log.error("ERROR when downloading from FTP: " + source + "to: " + destination
                + " : " + e.getMessage());
            isFileDownloading = false;
        }
    }

    /*
     * The method for deleting files downloaded from FTP storage
     */
    private void deleteFiles() {
        log.info(" Trying to delete downloading files from FTP storage...");

        if (isFileDownloading) {
            try {
                for (String file : listFiles) {
                    clientFTP.deleteFile(locationFiles + file);
                }
            } catch (IOException e) {
                log.error("ERROR on deleting files from FTP storage " + e.getMessage());
            }
            log.info("Files deleted from FTP storage.");
        } else {
            log.info("Files were not deleted from FTP storage " +
                    "due to an error during downloading!");
            TelegramNotification.sendMessage(TelegramNotificationType.ERROR_FTP);
        }
    }

}
