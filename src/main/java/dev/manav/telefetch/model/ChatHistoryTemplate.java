package dev.manav.telefetch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatHistoryTemplate {
    private long chatId;
    private int fromMessageId;
    private int limit;
}
