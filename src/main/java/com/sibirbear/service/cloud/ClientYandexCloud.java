package com.sibirbear.service.cloud;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.sibirbear.config.Config;
import com.sibirbear.service.telegram.TelegramNotification;
import com.sibirbear.service.telegram.TelegramNotificationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * The class builds a client using the S3 protocol according
 * to the parameters obtained from class Config
 */

public class ClientYandexCloud {

    private final Logger log = LogManager.getLogger(this.getClass());
    private final String storage;
    private final String region;
    private final String kaws;
    private final String saws;

    private AmazonS3 s3client;

    public ClientYandexCloud(final Config config) {
        this.storage = config.getStorage();
        this.region = config.getRegion();
        this.kaws = config.getKaws();
        this.saws = config.getSaws();
    }

    public synchronized AmazonS3 connect() {
        try {
            AWSCredentials cr = new BasicAWSCredentials(kaws, saws);
            AmazonS3ClientBuilder client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(cr))
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(storage, region)
                    );
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setProtocol(Protocol.HTTP);
            client.setClientConfiguration(clientConfiguration);
            this.s3client = client.build();
            log.info("Connect to Yandex.Cloud successful.");
        } catch (Exception e) {
            log.error("ERROR connect to Yandex.Cloud: " + e.getMessage());
            TelegramNotification.sendMessage(TelegramNotificationType.ERROR_CLOUD);
            e.printStackTrace();
        }

        return s3client;
    }

}
