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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "post_text_message", length = 1000)
    private String postTextMessage;

    @Column(name = "update_date")
    private Date updateDate;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user=" + user +
                ", createDate=" + createDate +
                ", postTextMessage='" + postTextMessage + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}
