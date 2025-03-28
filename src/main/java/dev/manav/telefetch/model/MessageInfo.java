package dev.manav.telefetch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageInfo {
    private long messageId;
    private int id;
    private String filename;
    private String mimeType;
    private double size; // size in megabytes
    private String caption;
    @Setter
    private double progress;

    public MessageInfo(long messageId,int id, String filename, String mimeType, long sizeInBytes, String caption) {
        this.messageId = messageId;
        this.id = id;
        this.filename = filename;
        this.mimeType = mimeType;
        this.size = sizeInBytes / (1024.0 * 1024.0); // convert bytes to megabytes
        this.caption = caption;
    }
}