
package com.example.chat.service;

import org.springframework.stereotype.Service;

import com.example.chat.domain.Conversation;
import com.example.chat.domain.User;
import com.example.chat.repository.ConversationRepository;
import com.example.chat.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository; // ✅ ADD THIS

    public ConversationService(ConversationRepository conversationRepository,
                               UserRepository userRepository) {   // ✅ ADD
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }
    

public Conversation startNewConversationByEmail(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Conversation conversation = new Conversation();
    conversation.setUserEmail(email);
    conversation.setTitle("New Chat");
    conversation.setCreatedAt(LocalDateTime.now());

    return conversationRepository.save(conversation);
}


    public List<Conversation> getUserConversationsByEmail(String email) {
        return conversationRepository.findByUserEmail(email);
    }

    public Conversation saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }
}
