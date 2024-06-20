package dev.manav.telefetch.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatResponse {
    private int totalCount;
    private List<MessageInfo> messages;

    public ChatResponse(int totalCount, List<MessageInfo> messages) {
        this.totalCount = totalCount;
        this.messages = messages;
    }
}