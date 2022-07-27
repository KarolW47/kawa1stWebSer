package pl.webser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User chatMessageFromUser;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User chatMessageToUser;

}
