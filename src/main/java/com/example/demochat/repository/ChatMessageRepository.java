package com.example.demochat.repository;

import com.example.demochat.model.ChatMessage;
import com.example.demochat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRecipient(User recipient);
    List<ChatMessage> findByRecipientIsNull(); // Public messages
}
