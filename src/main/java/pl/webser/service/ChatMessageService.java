package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.webser.model.ChatMessage;
import pl.webser.model.User;
import pl.webser.repository.ChatMessageRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
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

    public ChatMessage addChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> getChatMessagesWithChosenUser(String emailOfUser,
                                                           String usernameOfChosenUserToChat) {
        User currentUser = userService.getUserByEmailAddress(emailOfUser);
        Long chosenUserId = userService.getUserByUsername(usernameOfChosenUserToChat).getId();

        log.info("Fetching conversation between users with id {} and {}", currentUser.getId(), chosenUserId);

        List<ChatMessage> chatMessagesSentToChosenUser =
                chatMessageRepository.findAllFromSenderToReceiverByUsersId(currentUser.getId(), chosenUserId);
        chatMessagesSentToChosenUser
                .forEach(cm -> {
                    cm.setUsernameOfSender(currentUser.getUsername());
                    cm.setUsernameOfReceiver(usernameOfChosenUserToChat);
                });

        List<ChatMessage> chatMessagesReceivedFromChosenUser =
                chatMessageRepository.findAllFromSenderToReceiverByUsersId(chosenUserId, currentUser.getId());
        chatMessagesReceivedFromChosenUser
                .forEach(cm -> {
                    cm.setUsernameOfSender(usernameOfChosenUserToChat);
                    cm.setUsernameOfReceiver(currentUser.getUsername());
                });

        return Stream
                .concat(chatMessagesSentToChosenUser.stream(), chatMessagesReceivedFromChosenUser.stream())
                .toList();
    }
}
