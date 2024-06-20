package dev.manav.telefetch.controller;

import dev.manav.telefetch.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/download")
public class DownloadFileController {

    @Autowired
    private DownloadService downloadService;

    @GetMapping("/{fileId}")
    public CompletableFuture<ResponseEntity<String>> downloadFile(@PathVariable int fileId) {
        return downloadService.downloadFile(fileId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).body("Download failed: " + ex.getMessage()));
    }
}
