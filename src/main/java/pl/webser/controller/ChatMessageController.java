package pl.webser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.webser.model.ChatMessage;
import pl.webser.security.JWTUtil;
import pl.webser.service.ChatMessageService;

import java.util.List;

@RestController
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final JWTUtil jwtUtil;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, JWTUtil jwtUtil) {
        this.chatMessageService = chatMessageService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/chat_message/user")
    public ResponseEntity<List<ChatMessage>> getHistoryOfConversation(@RequestParam String username,
                                                                      @RequestHeader String token) {
        return ResponseEntity.ok().body(chatMessageService.getChatMessagesWithChosenUser(
                jwtUtil.getEmailAddressFromJwtToken(token), username));
    }

    @MessageMapping("/chat_message")
    @SendTo("/user")
    public ChatMessage sendMessage(ChatMessage chatMessage, String token) {
        return chatMessageService.addChatMessage(chatMessage.getMessage(), jwtUtil.getEmailAddressFromJwtToken(token),
                chatMessage.getUsernameOfReceiver());
    }
}
