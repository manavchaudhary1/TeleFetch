package dev.manav.telefetch.service;

import org.drinkless.tdlib.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.updates.UpdateNotificationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DownloadService {

    private final TelegramClient telegramClient;

    public DownloadService(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public record FileDownloadProgress(
            long fileId,
            long expectedSize,
            long downloadedSize,
            boolean isCompleted
    ) {}

    private static final Map<Integer, FileDownloadProgress> fileIdToFileDownloadProgress = new ConcurrentHashMap<>();

    public void downloadFile(TdApi.File file) {
        log.info("Entering downloadFile method for file ID: {}", file.id);
        telegramClient.sendAsync(new TdApi.DownloadFile(file.id, 32, 0, 0, false))
                .thenAccept(fileResponse -> {
                    log.info("Received response for file ID: {}", file.id);
                    if (fileResponse.error() != null) {
                        log.error("Error initiating download for file ID {}: {}", file.id, fileResponse.error());
                    } else {
                        log.info("Download initiated successfully for file ID: {}", file.id);
                        try {
                            long approximateFileSize = getApproximateFileSize(file);
                            long downloadedSize = (file.local != null) ? file.local.downloadedSize : 0;
                            var downloadProgress = new FileDownloadProgress(file.id, approximateFileSize, downloadedSize, false);
                            fileIdToFileDownloadProgress.put(file.id, downloadProgress);
                            log.info("Initial progress added to map for file ID: {}", file.id);
                        } catch (Exception e) {
                            log.error("Error while adding initial progress to map for file ID {}: {}", file.id, e.getMessage(), e);
                        }
                    }
                })
                .exceptionally(ex -> {
                    log.error("Exception occurred while downloading file ID {}: {}", file.id, ex.getMessage(), ex);
                    return null;
                });
        log.info("Exiting downloadFile method for file ID: {}", file.id);
    }

    public Map<String, Object> getAllDownloadProgress() {
        Map<String, Object> allProgress = new HashMap<>();
        for (Map.Entry<Integer, FileDownloadProgress> entry : fileIdToFileDownloadProgress.entrySet()) {
            allProgress.put(String.valueOf(entry.getKey()), getFileDownloadProgress(entry.getKey()));
        }
        return allProgress;
    }

    public Map<String, Object> getFileDownloadProgress(int fileId) {
        log.info("Checking progress for file ID: {}", fileId);

        FileDownloadProgress progress = fileIdToFileDownloadProgress.get(fileId);
        if (progress != null) {
            Map<String, Object> progressInfo = new HashMap<>();
            progressInfo.put("fileId", progress.fileId());
            progressInfo.put("expectedSize", progress.expectedSize());
            progressInfo.put("downloadedSize", progress.downloadedSize());

            if (progress.isCompleted()) {
                progressInfo.put("status", "Completed");
                progressInfo.put("progress", 100);
            } else if (progress.expectedSize() == 0) {
                progressInfo.put("status", "Unknown");
                progressInfo.put("progress", 0);
                log.warn("Expected size is zero for file ID: {}", fileId);
            } else {
                progressInfo.put("status", "In Progress");
                progressInfo.put("progress", (progress.downloadedSize() * 100) / progress.expectedSize());
            }

            return progressInfo;
        } else {
            log.warn("No progress found for file ID: {}", fileId);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", "File not found or download not initiated");
            return errorInfo;
        }
    }

    private static long getApproximateFileSize(TdApi.File file) {
        return file.size == 0 ? file.expectedSize : file.size;
    }

    @Component
    static class UpdateFile implements UpdateNotificationListener<TdApi.UpdateFile> {

        @Override
        public void handleNotification(TdApi.UpdateFile updateFile) {
            TdApi.File file = updateFile.file;

            if (fileIdToFileDownloadProgress.containsKey(file.id)) {
                long approximateFileSize = getApproximateFileSize(file);
                long downloadedSize = (file.local != null) ? file.local.downloadedSize : 0;
                boolean isCompleted = (file.local != null && file.local.isDownloadingCompleted);

                var newDownloadProgress = new FileDownloadProgress(file.id, approximateFileSize, downloadedSize, isCompleted);
                fileIdToFileDownloadProgress.put(file.id, newDownloadProgress);

                if (isCompleted) {
                    log.info("File download completed: [fileId: {}]", file.id);
                }
            } else {
                log.warn("Received update for file ID {} but it's not in the progress map", file.id);
            }
        }

        @Override
        public Class<TdApi.UpdateFile> notificationType() {
            return TdApi.UpdateFile.class;
        }
    }
}
