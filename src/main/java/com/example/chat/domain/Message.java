package com.example.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Conversation grouping
    @Column(name = "conversation_id", nullable = false)
    private long conversationId;

    // Who sent and received the message
    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "receiver_email", nullable = false)
    private String receiverEmail;

    // Whether the receiver has read the message
    @Column(name = "is_read")
    private boolean isRead;

    // Message type: "text", "image", "file", etc.
    @Column(name = "message_type")
    private String messageType;

    @Column(name = "message_text", length = 5000)
    private String messageText;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
