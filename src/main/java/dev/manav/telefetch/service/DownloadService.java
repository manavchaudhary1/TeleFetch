package dev.manav.telefetch.service;

import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DownloadService {

    private final TelegramClient telegramClient;

    public DownloadService(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public CompletableFuture<String> downloadFile(int fileId) {
        log.info("Downloading file {}", fileId);
        TdApi.DownloadFile request = new TdApi.DownloadFile(fileId, 1, 0, 0, true);

        CompletableFuture<String> downloadFuture = new CompletableFuture<>();

        telegramClient.sendWithCallback(request, (file, error) -> {
            if (error != null) {
                log.error("Error while downloading file {}: {}", fileId, error.message);
                downloadFuture.completeExceptionally(new RuntimeException("Download failed: " + error.message));
            } else if (file.local.isDownloadingCompleted) {
                String filePath = file.local.path;
                log.info("Downloading file {} completed at {}", fileId, filePath);
                downloadFuture.complete(filePath);
            } else {
                downloadFuture.completeExceptionally(new RuntimeException("Download failed: " + fileId));
            }
        });

        return downloadFuture;
    }
}
