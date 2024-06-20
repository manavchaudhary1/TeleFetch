package dev.manav.telefetch.controller;

import dev.manav.telefetch.model.ChatHistoryTemplate;
import dev.manav.telefetch.model.ChatResponse;
import dev.manav.telefetch.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/api/chat")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponse> chatHistory(@RequestBody ChatHistoryTemplate chatId, @RequestBody ChatHistoryTemplate limit) {
        log.info("chatHistory endpoint called with chatId: {}", chatId.getChatId());
        try {
            ChatResponse response = chatHistoryService.getChatHistory(chatId, limit);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error fetching chat history: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

//        @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public TdApi.Messages chatHistory(@RequestBody ChatId chatId) {
//        logger.info("chatHistory endpoint called with chatId: {} and fromMessageId: {}", chatId.getChatId());
//        updateChatList();
//        TdApi.ChatHistoryController request = new TdApi.ChatHistoryController(chatId.getChatId(), 0,0, 25,false);
//        try {
//            TdApi.Messages messages = telegramClient.sendSync(request);
//            return messages;
//        } catch (TelegramClientTdApiException e) {
//            logger.error("Error fetching chat history: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to fetch chat history: " + e.getMessage());
//        }
//    }
}
