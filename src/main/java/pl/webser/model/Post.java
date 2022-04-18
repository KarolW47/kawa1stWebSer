package pl.webser.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @ManyToOne()
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
