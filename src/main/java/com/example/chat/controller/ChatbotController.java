package com.example.chat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.chat.domain.Message;
import com.example.chat.service.MessageService;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private MessageService messageService;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    // Return all chatbot messages for a user
    @GetMapping("/history/{email}")
    public ResponseEntity<?> getChatHistory(@PathVariable("email") String email) {
        try {
            List<Message> messages = messageService.getUserChatbotMessages(email);
            return ResponseEntity.ok(messages != null ? messages : List.of());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch chat history", "details", e.getMessage()));
        }
    }

@PostMapping("/message")
public ResponseEntity<?> getBotReply(@RequestBody Map<String, Object> request) {
System.out.println("RAW REQUEST BODY => " + request);
    String userMessage = (String) request.get("messageText");
    String email = (String) request.get("senderEmail");
    // String conversationId = String.valueOf(request.get("conversationId"));

    Object convObj = request.get("conversationId");

if (convObj == null) {
    return ResponseEntity.badRequest()
            .body(Map.of("error", "conversationId is required"));
}

Long conversationId;
try {
    conversationId = Long.valueOf(convObj.toString());
} catch (NumberFormatException e) {
    return ResponseEntity.badRequest()
            .body(Map.of("error", "conversationId must be a number"));
}


    if (userMessage == null || userMessage.trim().isEmpty()
            || email == null || conversationId == null) {
        return ResponseEntity.badRequest()
                .body(Map.of("reply", "messageText, senderEmail and conversationId are required"));
    }

    String botReply = callOpenAIApi(userMessage);

    // Save user message
    Message userMsg = new Message();
    userMsg.setConversationId(conversationId);
    userMsg.setSenderEmail(email);
    userMsg.setReceiverEmail("bot");
    userMsg.setMessageText(userMessage);
    userMsg.setTimestamp(LocalDateTime.now());
    userMsg.setRead(true);
    messageService.saveMessage(userMsg);

    // Save bot reply
    Message botMsg = new Message();
    botMsg.setConversationId(conversationId);
    botMsg.setSenderEmail("bot");
    botMsg.setReceiverEmail(email);
    botMsg.setMessageText(botReply);
    botMsg.setTimestamp(LocalDateTime.now());
    botMsg.setRead(true);
    messageService.saveMessage(botMsg);

    return ResponseEntity.ok(Map.of("reply", botReply));
}


    private String callOpenAIApi(String userMessage) {
        String url = "https://openrouter.ai/api/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-3.5-turbo");
        body.put("messages", List.of(Map.of("role", "user", "content", userMessage)));

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + openRouterApiKey);
        headers.set("Content-Type", "application/json");

        org.springframework.http.HttpEntity<Map<String, Object>> entity =
                new org.springframework.http.HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.get("choices") instanceof List<?> choices && !choices.isEmpty()) {
                Object firstChoice = choices.get(0);
                if (firstChoice instanceof Map<?, ?> choiceMap) {
                    Object messageObj = choiceMap.get("message");
                    if (messageObj instanceof Map<?, ?> messageMap) {
                        Object content = messageMap.get("content");
                        if (content != null) {
                            return content.toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error connecting to OpenRouter API: " + e.getMessage();
        }
        return "No reply from OpenRouter.";
    }

    // Delete all messages for a user
    @DeleteMapping("/history/{email}")
    public ResponseEntity<?> deleteAllConversations(@PathVariable("email") String email) {
        try {
            List<Message> messages = messageService.getUserChatbotMessages(email);
            for (Message m : messages) {
                messageService.deleteMessageById(m.getId());
            }
            return ResponseEntity.ok(Map.of("message", "All conversations deleted"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete conversations", "details", e.getMessage()));
        }
    }
}