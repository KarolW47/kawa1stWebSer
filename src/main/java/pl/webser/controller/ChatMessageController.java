package pl.webser.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.webser.model.ChatMessage;
import pl.webser.security.JWTUtil;
import pl.webser.service.ChatMessageService;

import java.util.List;

import static pl.webser.security.filter.CustomAuthorizationFilter.ACCESS_TOKEN_HEADER;

@RestController
@Slf4j
@RequestMapping("/chat_messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final JWTUtil jwtUtil;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, JWTUtil jwtUtil) {
        this.chatMessageService = chatMessageService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/user")
    public ResponseEntity<List<ChatMessage>> getHistoryOfConversation(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                                                      @RequestParam(name = "username") String username) {
        return ResponseEntity.ok(chatMessageService.getChatMessagesWithChosenUser(
                jwtUtil.getEmailAddressFromJwtToken(token), username));
    }

    @MessageMapping("/chat/{chosenUserId}/{chosenUserUsername}")
    @SendTo("/user/{chosenUserId}/{chosenUserUsername}")
    public ChatMessage sendMessage(@DestinationVariable String chosenUserUsername,
                                   @DestinationVariable Long chosenUserId,
                                   ChatMessage chatMessage) {
        log.info("Sending message to user with id {}, and saving to db.", chatMessage.getIdOfReceiver());
        return chatMessageService.addChatMessage(chatMessage);
    }
}
