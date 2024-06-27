package dev.manav.telefetch.service;

import dev.manav.telefetch.model.GroupInfo;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GroupIdService {


    private final TelegramClient telegramClient;

    public GroupIdService(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public List<GroupInfo> getMyChats() {
        TdApi.Chats chats = telegramClient.sendSync(new TdApi.GetChats(new TdApi.ChatListMain(), 1000));
        return Arrays.stream(chats.chatIds)
                .mapToObj(chatId -> {
                    TdApi.Chat chat = telegramClient.sendSync(new TdApi.GetChat(chatId));
                    return new GroupInfo(chatId, chat.title);
                }).toList();
    }
}

