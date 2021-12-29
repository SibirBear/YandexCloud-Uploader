package com.sibirbear.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

/*
 * A class for processing and storing settings for connecting to FTP,
 * YCloud, TG bot, location of files for reading and downloading.
 */

public class Config {

    private final Logger log = LogManager.getLogger(this.getClass());
    private String bucket;
    private String storage;
    private String region;
    private String kaws;
    private String saws;
    private String pathToFtp;
    private String pathToFiles;
    private String serverFtp;
    private int portFtp;
    private String userFtp;
    private String passFtp;
    private String tgBot;
    private String tgChatId;
    private Properties properties;

    public Config(String configPath) {
        try {
            log.info("Reading configuration properties...");
            InputStream is = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(configPath);
            properties = new Properties();
            properties.load(is);
        } catch (Exception e) {
            log.error("ERROR init configuration prperties! " + e);
        }

    }

    private void setConfig(Properties properties) {
        this.bucket = properties.getProperty("BUCKET_NAME");
        this.storage = properties.getProperty("STORAGE");
        this.region = properties.getProperty("STORAGE_REGION");
        this.kaws = properties.getProperty("KAWS");
        this.saws = properties.getProperty("SAWS");
        this.pathToFtp = properties.getProperty("PATH_FILES_FTP");
        this.pathToFiles = properties.getProperty("PATH_FILES_TO_UPLOAD");
        this.serverFtp = properties.getProperty("SERVER_FTP");
        this.portFtp = Integer.parseInt(properties.getProperty("PORT_FTP"));
        this.userFtp = properties.getProperty("USER_FTP");
        this.passFtp = properties.getProperty("PASS_FTP");
        this.tgBot = properties.getProperty("TOKEN");
        this.tgChatId = properties.getProperty("IDCHAT");
    }

    public String getBucket() {
        return bucket;
    }

    public String getStorage() {
        return storage;
    }

    public String getRegion() {
        return region;
    }

    public String getKaws() {
        return kaws;
    }

    public String getSaws() {
        return saws;
    }

    public String getPathToFtp() {
        return pathToFtp;
    }

    public String getPathToFiles() {
        return pathToFiles;
    }

    public String getServerFtp() {
        return serverFtp;
    }

    public int getPortFtp() {
        return portFtp;
    }

    public String getUserFtp() {
        return userFtp;
    }

    public String getPassFtp() {
        return passFtp;
    }

    public String getTgBot() {
        return tgBot;
    }

    public String getTgChatId() {
        return tgChatId;
    }

}
