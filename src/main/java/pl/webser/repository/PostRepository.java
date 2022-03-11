package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.Post;

import java.util.Date;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("UPDATE posts SET postTextMessage = ?1, updateDate = ?2 WHERE id = ?3")
    void updatePostById(String postText, Date updateDate, Long id);
}
