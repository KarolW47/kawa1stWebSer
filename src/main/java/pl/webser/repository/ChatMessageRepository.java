package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.ChatMessage;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.idOfSender = ?1 AND m.idOfReceiver = ?2")
    List<ChatMessage> findAllFromSenderToReceiverByUsersId(Long idOfSender, Long idOfReceiver);

}
