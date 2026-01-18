package com.example.chat.repository;

import com.example.chat.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Fetch all messages between two users
    List<Message> findBySenderEmailAndReceiverEmail(String senderEmail, String receiverEmail);

    // Fetch unread messages for a user
    List<Message> findByReceiverEmailAndIsReadFalse(String receiverEmail);

    // Fetch all messages in a conversation, ordered by timestamp
    List<Message> findByConversationIdOrderByTimestampAsc(Long conversationId);

    // Delete all messages in a conversation
    void deleteByConversationId(Long conversationId);

    // Fetch all messages sent by a specific user, ordered by timestamp
    List<Message> findBySenderEmailOrderByTimestampAsc(String email);

    // Fetch all messages between user and bot, ordered by timestamp
    @Query("SELECT m FROM Message m " +
           "WHERE ((LOWER(m.senderEmail) = LOWER(:email) AND LOWER(m.receiverEmail) = 'bot') " +
           "   OR (LOWER(m.receiverEmail) = LOWER(:email) AND LOWER(m.senderEmail) = 'bot')) " +
           "ORDER BY m.timestamp ASC")
    List<Message> findUserChatbotMessages(@Param("email") String email);

    // Fetch all messages between two users (conversation style)
    @Query("SELECT m FROM Message m " +
           "WHERE (m.senderEmail = :user1 AND m.receiverEmail = :user2) " +
           "   OR (m.senderEmail = :user2 AND m.receiverEmail = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<Message> findConversation(@Param("user1") String user1, @Param("user2") String user2);

    // Find all conversation partners for a given user
    @Query("SELECT DISTINCT CASE WHEN m.senderEmail = :email THEN m.receiverEmail ELSE m.senderEmail END " +
           "FROM Message m WHERE m.senderEmail = :email OR m.receiverEmail = :email")
    List<String> findConversationPartners(@Param("email") String email);
}