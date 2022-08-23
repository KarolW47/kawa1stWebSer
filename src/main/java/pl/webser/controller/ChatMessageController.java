package pl.webser.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import pl.webser.model.ChatMessage;
import pl.webser.security.JWTUtil;
import pl.webser.service.ChatMessageService;

import java.util.List;

@Slf4j
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

    @MessageMapping("/chat")
    @SendTo("/user/{chosenUserUsername}")
    public ChatMessage sendMessage(@DestinationVariable String chosenUserUsername, ChatMessage chatMessage) {
        log.info("Sending message to user with id {}, and saving to db.", chatMessage.getIdOfReceiver());
        return chatMessageService.addChatMessage(chatMessage.getMessage(), chatMessage.getIdOfSender(),
                chatMessage.getIdOfReceiver());
    }
}
