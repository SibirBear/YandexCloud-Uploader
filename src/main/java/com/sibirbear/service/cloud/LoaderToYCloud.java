package com.sibirbear.service.cloud;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.sibirbear.config.Config;
import com.sibirbear.service.telegram.TelegramNotification;
import com.sibirbear.service.telegram.TelegramNotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;

public class LoaderToYCloud {

    private final Logger log = LogManager.getLogger(this.getClass());
    private boolean isUploadSuccess;
    private File[] listFilesTempDir;

    public void uploadToCloud(Config config) {
        File dir = new File(config.getPathToFiles());
        listFilesTempDir = dir.listFiles(File::isFile);

        if (listFilesTempDir == null || listFilesTempDir.length == 0) {
            log.info("There ane no files in temp directory to downloading.");
        } else {
            uploadStart(config);
            if (isUploadSuccess) {
                log.info("Files upload is complete.");
            } else {
                log.error("ERROR uploading to cloud! See log message above.");
                TelegramNotification.sendMessage(TelegramNotificationType.ERROR_CLOUD);
            }
        }
    }

    private synchronized void uploadStart(Config config){
        String bucket = config.getBucket();

        log.info("Connecting Yandex.Cloud and trying to upload files...");

        ClientYandexCloud clientYandexCloud = new ClientYandexCloud(config);
        final AmazonS3 s3 = clientYandexCloud.connect();

        if (s3 != null) {
            for (File file : listFilesTempDir) {
                if (file.isFile()) {
                    String path = file.toString();
                    String keyName = Paths.get(path).getFileName().toString();

                    try {
                        s3.putObject(bucket, keyName, new File(path));
                        deleteUploadFile(file);
                    } catch (AmazonS3Exception e) {
                        log.error("ERROR ahile uploading file " + path + " to "
                            + bucket + " (Yandex.Cloud)! " + e.getMessage());
                        isUploadSuccess = false;
                    }
                }
            }
        }
    }

    private  void deleteUploadFile(File file) {
        boolean success = file.delete();
        if (!success) {
            log.error(" ERROR when deleting file: " + file);
        }
    }

}
