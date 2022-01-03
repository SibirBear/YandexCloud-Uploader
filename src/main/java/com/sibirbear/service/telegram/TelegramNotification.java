package com.sibirbear.service.telegram;

import com.sibirbear.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/*
 * The class for sending message to Telegram bot if caused trouble with
 * connection, loading to FTP or CLOUD.
 */

public class TelegramNotification {

    private static final Logger log = LogManager.getLogger(TelegramNotification.class);
    private static HttpURLConnection con;
    private static String TgBot;
    private static String TgChatID;

    public static void initConfig(final Config config) {
        TelegramNotification.TgBot = config.getTgBot();
        TelegramNotification.TgChatID = config.getTgChatId();

        if (TgBot == null || TgChatID == null) {
            log.error("[TelegramNotification] - Error! There aren parameters for connecting to the Telegram API.");
        }
    }

    public static void sendMessage(final TelegramNotificationType type) {

        String urlBot = "https://api.telegram.org/bot" + TgBot + "/sendMessage";
        String urlParameters = "chat_id=" + TgChatID + "&text="
                + type.getErrorMessage();

        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        try {
            URL url = new URL(urlBot);
            con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestMethod("POST");

            try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
                dos.write(postData);
                log.info("[TelegramNotification] - Notification send. " + Arrays.toString(postData));
            } catch (IOException e) {
                log.error("[TelegramNotification] - Error sending notification! " + e.getMessage());
                e.printStackTrace();
            }

            StringBuilder response;

            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String line;
                response = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                    response.append(System.lineSeparator());
                }
                log.info("[TelegramNotification] - response: " + response);
            } catch (IOException e) {
                log.error("[TelegramNotification] - error when receiving response. " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            log.error("[TelegramNotification] - ERROR connecting or sending message! " + e.getMessage());
            e.printStackTrace();
        }

        finally {
            con.disconnect();
        }

    }

}
