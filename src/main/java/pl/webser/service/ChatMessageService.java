package pl.webser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.webser.model.ChatMessage;
import pl.webser.repository.ChatMessageRepository;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
    }

    public void addChatMessage(String message, String fromUserWithUsername, String toUserWithUsername) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setSentDate(new Date(System.currentTimeMillis()));
        chatMessage.setChatMessageFromUser(userService.getUserByUsername(fromUserWithUsername));
        chatMessage.setChatMessageToUser(userService.getUserByUsername(toUserWithUsername));
        chatMessageRepository.save(chatMessage);
    }

}
