package dev.manav.telefetch.controller;

import dev.manav.telefetch.service.DownloadService;
import dev.voroby.springframework.telegram.client.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/download")
@Slf4j
public class DownloadFileController {

    private final DownloadService downloadService;

    @Autowired
    public DownloadFileController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable int fileId) {
        try {
            TdApi.File file = new TdApi.File();
            file.id = fileId;
            downloadService.downloadFile(file);
            return ResponseEntity.ok("Download initiated for file ID: " + fileId);
        } catch (Exception ex) {
            log.error("Error initiating download for file ID: {}", fileId, ex);
            return ResponseEntity.status(500).body("Failed to initiate download for file: " + ex.getMessage());
        }
    }

    @GetMapping("/progress/{fileId}")
    public ResponseEntity<Map<String, Object>> getFileDownloadProgress(@PathVariable int fileId) {
        Map<String, Object> progressInfo = downloadService.getFileDownloadProgress(fileId);
        if (progressInfo.containsKey("error")) {
            return ResponseEntity.status(404).body(progressInfo);
        }
        return ResponseEntity.ok(progressInfo);
    }
}
