package pl.webser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.webser.model.ChatMessage;
import pl.webser.model.User;
import pl.webser.repository.ChatMessageRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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

    public ChatMessage addChatMessage(String message, Long idOfSender, String usernameOfReceiver) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setSentDate(new Date(System.currentTimeMillis()));
        chatMessage.setEmailOfSender(userService.getUserById(idOfSender).map(User::getEmailAddress).orElse(null));
        chatMessage.setEmailOfReceiver(userService.getUserByUsername(usernameOfReceiver).getEmailAddress());
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> getChatMessagesWithChosenUser(String emailOfUser,
                                                           String usernameOfChosenUserToChat) {
        String usernameOfUser = userService.getUserByEmailAddress(emailOfUser).getUsername();
        String emailOfChosenUserToChat = userService.getUserByUsername(usernameOfChosenUserToChat).getEmailAddress();

        List<ChatMessage> chatMessagesSentToChosenUser =
                chatMessageRepository.findAllFromSenderToReceiverByEmail(emailOfUser,
                        emailOfChosenUserToChat);
        chatMessagesSentToChosenUser
                .forEach(cm -> {
                    cm.setUsernameOfSender(usernameOfUser);
                    cm.setUsernameOfReceiver(usernameOfChosenUserToChat);
                });

        List<ChatMessage> chatMessagesReceivedFromChosenUser =
                chatMessageRepository.findAllFromSenderToReceiverByEmail(emailOfChosenUserToChat,
                        emailOfUser);
        chatMessagesReceivedFromChosenUser
                .forEach(cm -> {
                    cm.setUsernameOfSender(usernameOfChosenUserToChat);
                    cm.setUsernameOfReceiver(usernameOfUser);
                });

        return Stream
                .concat(chatMessagesSentToChosenUser.stream(), chatMessagesReceivedFromChosenUser.stream())
                .toList();
    }
}
