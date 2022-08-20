package pl.webser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "email_of_sender")
    private String emailOfSender;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "email_of_receiver")
    private String emailOfReceiver;

    @Transient
    private String usernameOfSender;

    @Transient
    private String usernameOfReceiver;

    @Transient
    private Long idOfSender;

    @Transient
    private Long idOfReceiver;


    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", sentDate=" + sentDate +
                ", emailOfSender='" + emailOfSender + '\'' +
                ", emailOfReceiver='" + emailOfReceiver + '\'' +
                ", usernameOfSender='" + usernameOfSender + '\'' +
                ", usernameOfReceiver='" + usernameOfReceiver + '\'' +
                ", idOfSender='" + idOfSender + '\'' +
                '}';
    }
}
