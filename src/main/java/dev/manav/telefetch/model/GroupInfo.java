package dev.manav.telefetch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInfo {
    private Long chatId;
    private String chatTitle;

    public GroupInfo(Long chatId, String chatTitle) {
        this.chatId = chatId;
        this.chatTitle = chatTitle;
    }
}
