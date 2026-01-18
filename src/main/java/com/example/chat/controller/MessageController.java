package com.example.chat.controller;

import com.example.chat.domain.Message;
import com.example.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> request) {
        String senderEmail = request.get("senderEmail");
        String receiverEmail = request.get("receiverEmail");
        String messageText = request.get("messageText");

        if (senderEmail == null || receiverEmail == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sender and receiver email are required"));
        }

        if (messageText == null || messageText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }

        Message message = messageService.sendMessage(senderEmail, receiverEmail, messageText);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/unread/{email}")
    public ResponseEntity<?> getUnreadMessages(@PathVariable String email) {
        List<Message> unreadMessages = messageService.getUnreadMessages(email);
        return ResponseEntity.ok(Map.of(
                "count", unreadMessages.size(),
                "messages", unreadMessages
        ));
    }

    // @PutMapping("/read/{messageId}")
    // public ResponseEntity<?> markAsRead(@PathVariable Long messageId) {
    //     boolean updated = messageService.markAsRead(messageId);
    //     if (!updated) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(Map.of("error", "Message not found"));
    //     }
    //     return ResponseEntity.ok(Map.of("message", "Message marked as read"));
    // }

    // @DeleteMapping("/{messageId}")
    // public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
    //     boolean deleted = messageService.deleteMessageById(messageId);
    //     if (!deleted) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(Map.of("error", "Message not found"));
    //     }
    //     return ResponseEntity.ok(Map.of("message", "Message deleted successfully"));
    // }
}