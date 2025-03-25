package dev.manav.telefetch.controller;

import dev.manav.telefetch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/active-usernames")
    public ResponseEntity<?> getActiveUsernames() throws IOException {
        return ResponseEntity.ok(Collections.singletonMap("activeUsernames", userService.getActiveUsernames()));

    }
}

