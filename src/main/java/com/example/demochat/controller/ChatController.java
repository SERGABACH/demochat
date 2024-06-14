package com.example.demochat.controller;

import com.example.demochat.model.ChatMessage;
import com.example.demochat.model.User;
import com.example.demochat.service.ChatMessageService;
import com.example.demochat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(Principal principal, @RequestBody ChatMessage chatMessage) {
        User sender = userService.getUserByUsername(principal.getName());
        User recipient = chatMessage.getRecipient() != null ?
                userService.getUserByUsername(chatMessage.getRecipient().getUsername()) : null;
        return chatMessageService.sendMessage(sender, recipient, chatMessage.getContent());
    }

    @RestController
    @RequestMapping("/api")
    public static class ChatRestController {

        @Autowired
        private ChatMessageService chatMessageService;

        @Autowired
        private UserService userService;

        @GetMapping("/messages")
        public List<ChatMessage> getMessages(@RequestParam(required = false) String recipientUsername) {
            User recipient = recipientUsername != null ? userService.getUserByUsername(recipientUsername) : null;
            return chatMessageService.getMessages(recipient);
        }

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@RequestBody User user) {
            try {
                User registeredUser = userService.registerUser(user.getUsername());
                return ResponseEntity.ok(registeredUser);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
        }

        @PostMapping("/login")
        public ResponseEntity<?> loginUser(@RequestBody User user) {
            User existingUser = userService.getUserByUsername(user.getUsername());
            if (existingUser != null) {
                return ResponseEntity.ok(existingUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
        }
        @MessageMapping("/chat.sendPublicMessage")
        @SendTo("/topic/public")
        public ChatMessage sendPublicMessage(@RequestBody ChatMessage chatMessage) {
            User sender = userService.getUserByUsername(chatMessage.getSender().getUsername());
            return chatMessageService.sendMessage(sender, null, chatMessage.getContent());
        }

        @GetMapping("/users")
        public List<User> getAllUsers() {
            // Возвращаем всех пользователей
            return userService.getAllUsers();
        }
    }
}
