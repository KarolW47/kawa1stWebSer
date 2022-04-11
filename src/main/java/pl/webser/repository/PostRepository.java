package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.Post;

import java.util.Date;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Query("UPDATE Post p SET p.postTextMessage = ?1, p.updateDate = ?2 WHERE p.id = ?3")
    void updatePostById(String postText, Date updateDate, Long id);
}
