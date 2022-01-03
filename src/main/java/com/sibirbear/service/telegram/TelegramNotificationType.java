package com.sibirbear.service.telegram;

public enum TelegramNotificationType {
    ERROR_FTP("[YandexCloud-Uploader] FTP : Error connecting, " +
            "downloading or deleting files from FTP. Look at the app logs."),
    ERROR_CLOUD("[YandexCloud-Uploader] CLOUD : Error connecting, " +
                      "uploading or deleting files to CLOUD. Look at the app logs.");

    private final String type;

    TelegramNotificationType(String type) {
        this.type = type;
    }

    public String getErrorMessage() {
        return type;
    }

}
