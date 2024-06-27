package dev.manav.telefetch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.voroby.springframework.telegram.client.TdApi;
import dev.voroby.springframework.telegram.client.TelegramClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final TelegramClient telegramClient;
    private final ObjectMapper objectMapper;

    public UserService(TelegramClient telegramClient, ObjectMapper objectMapper) {
        this.telegramClient = telegramClient;
        this.objectMapper = objectMapper;
    }

    public List<String> getActiveUsernames() throws IOException {
        TdApi.User user = fetchUserData();

        // Create a JSON structure that matches the expected format
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode activeUsernamesNode = rootNode.putObject("activeUsernames");
        activeUsernamesNode.put("id", user.id);
        activeUsernamesNode.put("firstName", user.firstName);
        activeUsernamesNode.put("lastName", user.lastName);

        ObjectNode usernamesNode = activeUsernamesNode.putObject("usernames");
        ArrayNode activeUsernamesArray = usernamesNode.putArray("activeUsernames");
        for (String username : user.usernames.activeUsernames) {
            activeUsernamesArray.add(username);
        }

        String jsonString = objectMapper.writeValueAsString(rootNode);
        JsonNode parsedNode = objectMapper.readTree(jsonString);

        List<String> activeUsernames = new ArrayList<>();
        JsonNode usernamesArrayNode = parsedNode.path("activeUsernames").path("usernames").path("activeUsernames");
        if (usernamesArrayNode.isArray()) {
            for (JsonNode usernameNode : usernamesArrayNode) {
                activeUsernames.add(usernameNode.asText());
            }
        }

        return activeUsernames;
    }

    public TdApi.User fetchUserData() {
        return telegramClient.sendSync(new TdApi.GetMe());
    }
}

