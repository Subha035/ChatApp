package com.example.chat.controller;

import com.example.chat.domain.Conversation;
import com.example.chat.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping("/new")
    public ResponseEntity<Conversation> startNewConversation(
            @RequestParam("email") String email) {

        Conversation conversation =
                conversationService.startNewConversationByEmail(email);

        return new ResponseEntity<>(conversation, HttpStatus.CREATED);
    }
}
