package pl.webser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "sent_date")
    private Date sentDate;

    @Transient
    private String usernameOfSender;

    @Transient
    private String usernameOfReceiver;

    @Column(name = "id_of_sender")
    private Long idOfSender;

    @Column(name = "id_od_receiver")
    private Long idOfReceiver;

}
