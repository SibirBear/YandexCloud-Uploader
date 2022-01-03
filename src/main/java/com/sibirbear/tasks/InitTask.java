package com.sibirbear.tasks;

import com.sibirbear.config.Config;
import com.sibirbear.service.ftp.ClientFTP;
import com.sibirbear.service.ftp.LoaderFromFTP;
import com.sibirbear.service.cloud.LoaderToYCloud;
import com.sibirbear.service.telegram.TelegramNotification;

import java.io.IOException;

public class InitTask {

    private final Config config;
    private ClientFTP clientFTP;
    private LoaderFromFTP loaderFromFTP;
    private LoaderToYCloud loaderToYCloud;

    public InitTask(Config config) {
        this.config = config;
    }

    public void startTask() throws IOException {
        TelegramNotification.initConfig(config);

        clientFTP = new ClientFTP(config);
        loaderFromFTP = new LoaderFromFTP(config, clientFTP);
        loaderToYCloud = new LoaderToYCloud();

        clientFTP.open();
        loaderFromFTP.loadFromFTP();
        clientFTP.close();
        loaderToYCloud.uploadToCloud(config);

    }

}
