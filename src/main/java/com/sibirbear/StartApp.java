package com.sibirbear;

import com.sibirbear.config.Config;
import com.sibirbear.tasks.InitTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Startup
@Singleton(name = "YandexCloudUploader")
public class StartApp {

    private Logger log;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture;
    private Config config;
    private InitTask initTask;

    private final long PERIOD_TIME_TASK = 10;

    @PostConstruct
    public void init() {
        log = LogManager.getLogger(this.getClass());
        config = new Config("config.properties");
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        initTask = new InitTask(config);
        executeRepeatingTask(initTask);
    }

    private void executeRepeatingTask(final InitTask initTask) {
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                () -> {
                    try {
                        log.info("Init app [YandexCloudUploader]...");
                        initTask.startTask();
                    } catch (IOException e) {
                        log.error("ERROR init app! " + e.getMessage(), e);
                        e.printStackTrace();
                    }
                }, 0, PERIOD_TIME_TASK, TimeUnit.MINUTES
        );
    }

    @PreDestroy
    public void stop() {
        log.info("Stop App. Begin stop process...");

        try {
            log.info(" Stop App - Trying to stop task.");
            scheduledFuture.cancel(false);
        } catch (Exception e) {
            log.error(" Stop App - Error when trying to stop task. "
                    + Arrays.toString(e.getStackTrace()));
        }

        boolean wait = true;
        while (wait) {
            try {
                if (scheduledFuture.isDone())
                    wait = false;
            } catch (Exception e) {
                log.error(" Stop App - Error when trying to stop task. "
                        + Arrays.toString(e.getStackTrace()));
                wait = false;
            }
        }

        try {
            scheduledExecutorService.shutdown();
            log.info(" Stop App - Successfully stopped.");
        } catch (Exception e) {
            log.error(" Stop App - error when trying to stop scheduleExecutorService. "
                    + Arrays.toString(e.getStackTrace()));
        }

    }

}
