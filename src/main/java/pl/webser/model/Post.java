package pl.webser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NonNull
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @NonNull
    @Column(name = "create_date")
    private Date createDate;

    @NonNull
    @Column(name = "post_text_message")
    private String postTextMessage;

    @Column(name = "update_date")
    private Date updateDate;
}
