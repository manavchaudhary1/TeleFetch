package dev.manav.telefetch.controller;

import dev.manav.telefetch.model.GroupInfo;
import dev.manav.telefetch.service.GroupIdService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
public class GroupIdController {

    private final GroupIdService groupIdService;

    public GroupIdController(GroupIdService groupIdService) {
        this.groupIdService = groupIdService;
    }

    @GetMapping("/chatTitles")
    public List<GroupInfo> getMyChats() {
        List<GroupInfo> chats = groupIdService.getMyChats();
        return chats;
    }
}
