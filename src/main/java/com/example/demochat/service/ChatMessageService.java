package com.example.demochat.service;

import com.example.demochat.model.ChatMessage;
import com.example.demochat.model.User;
import com.example.demochat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage sendMessage(User sender, User recipient, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setRecipient(recipient);
        chatMessage.setContent(content);
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessages(User recipient) {
        if (recipient == null) {
            return chatMessageRepository.findByRecipientIsNull();
        } else {
            return chatMessageRepository.findByRecipient(recipient);
        }
    }
}
