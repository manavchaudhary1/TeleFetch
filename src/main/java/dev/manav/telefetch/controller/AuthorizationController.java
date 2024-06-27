package dev.manav.telefetch.controller;

import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(value = "/api/authorization")
public class AuthorizationController {



    private ClientAuthorizationState authorizationState;

    public AuthorizationController(ClientAuthorizationState authorizationState) {
        this.authorizationState = authorizationState;
    }

    record Credential(@NotBlank String value){}

    @PostMapping(value = "/code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateConfirmationCode(@RequestBody @Valid Credential credential) {
        log.info("Received code: {}", credential.value);
        authorizationState.checkAuthenticationCode(credential.value);
    }

    @PostMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updatePassword(@RequestBody @Valid Credential credential) {
        log.info("Received password: {}", credential.value);
        authorizationState.checkAuthenticationPassword(credential.value);
    }

    @GetMapping(value = "/status")
    @ResponseBody
    public String authorizationStatus() {
        boolean authorized = authorizationState.haveAuthorization();
        log.info("Authorization status: {}", authorized ? "AUTHORIZED" : "NOT_AUTHORIZED");
        return authorized ? "AUTHORIZED" : "NOT_AUTHORIZED";
    }
}