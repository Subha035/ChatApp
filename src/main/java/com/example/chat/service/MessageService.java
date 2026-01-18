package com.example.chat.service;

import com.example.chat.domain.Message;
import com.example.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepo;

    // Fetch all chatbot messages for a user
    public List<Message> getUserChatbotMessages(String email) {
        return messageRepo.findUserChatbotMessages(email);
    }

    // Save a new message (user or bot)
    public Message sendMessage(String senderEmail, String receiverEmail, String messageText) {
        Message message = new Message();
        message.setSenderEmail(senderEmail);
        message.setReceiverEmail(receiverEmail);
        message.setMessageText(messageText);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        return messageRepo.save(message);
    }

    // Alternative: save a fully constructed Message object
    public Message saveMessage(Message message) {
        return messageRepo.save(message);
    }

    // Get unread messages for a user
    public List<Message> getUnreadMessages(String email) {
        return messageRepo.findByReceiverEmailAndIsReadFalse(email);
    }

    // Mark a message as read
    public void markAsRead(Long messageId) {
        messageRepo.findById(messageId).ifPresent(msg -> {
            msg.setRead(true);
            messageRepo.save(msg);
        });
    }

    // Delete a message by its ID
    public void deleteMessageById(Long id) {
        messageRepo.deleteById(id);
    }

    // Get all messages sent by a specific email
    public List<Message> getByEmail(String email) {
        return messageRepo.findBySenderEmailOrderByTimestampAsc(email);
    }

    // Get all messages in a conversation (if you add conversationId field)
    public List<Message> getByConversation(Long conversationId) {
        return messageRepo.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    // Delete a whole conversation (if you add conversationId field)
    public void deleteConversation(Long conversationId) {
        messageRepo.deleteByConversationId(conversationId);
    }
}