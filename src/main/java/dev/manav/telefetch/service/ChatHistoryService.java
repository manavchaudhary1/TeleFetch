package dev.manav.telefetch.service;

import dev.manav.telefetch.model.ChatHistoryTemplate;
import dev.manav.telefetch.model.ChatResponse;
import dev.manav.telefetch.model.MessageInfo;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.exception.TelegramClientTdApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatHistoryService {

    private final TelegramClient telegramClient;

    public ChatHistoryService(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public ChatResponse getChatHistory(ChatHistoryTemplate chatHistoryTemplate) throws RuntimeException {
        log.info("Fetching chat history for chatId: {}", chatHistoryTemplate.getChatId());
        updateChatList();

        TdApi.GetChatHistory request = new TdApi.GetChatHistory(chatHistoryTemplate.getChatId(), chatHistoryTemplate.getFromMessageId(), 0, chatHistoryTemplate.getLimit(), false);
        try {
            TdApi.Messages messages = telegramClient.sendSync(request);
            List<MessageInfo> filteredMessages = filterMessages(messages);
            int totalCount = messages.totalCount;
            return new ChatResponse(totalCount, filteredMessages);
        } catch (TelegramClientTdApiException e) {
            log.error("Error fetching chat history: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching chat history", e);
        }
    }

    private List<MessageInfo> filterMessages(TdApi.Messages messages) {
        List<MessageInfo> filteredMessages = new ArrayList<>();

        for (TdApi.Message message : messages.messages) {
            long messageId = message.id;
            if (message.content instanceof TdApi.MessageDocument documentContent) {
                TdApi.Document document = documentContent.document;

                int documentId = document.document.id;
                String filename = document.fileName.isEmpty() ? "No filename" : document.fileName;
                String mimeType = document.mimeType;
                long size = document.document.size;
                String caption = documentContent.caption.text;

                filteredMessages.add(new MessageInfo(messageId, documentId, filename, mimeType, size, caption));
            } else if (message.content instanceof TdApi.MessageVideo videoContent) {
                TdApi.Video video = videoContent.video;

                int videoId = video.video.id;
                String filename = video.fileName.isEmpty() ? "No videoName" : video.fileName;
                String mimeType = video.mimeType;
                long size = video.video.size;
                String caption = videoContent.caption.text;

                filteredMessages.add(new MessageInfo(messageId, videoId, filename, mimeType, size, caption));
            }
        }

        return filteredMessages;
    }


    public void updateChatList() {
        try {
            TdApi.Chats chats = telegramClient.sendSync(new TdApi.GetChats(new TdApi.ChatListMain(), 500));
            log.info("Chat list updated with {} chats", chats.totalCount);
        } catch (TelegramClientTdApiException e) {
            log.error("Error updating chat list: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update chat list: " + e.getMessage());
        }
    }

}
