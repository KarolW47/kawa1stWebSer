package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.Post;

import java.util.Date;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.createDate < p.createDate = ?1 ORDER BY p.createDate DESC limit limitNumber " +
            "= ?2")
    List<Post> findTenStartingFromCreateDate(Date createDate, int limitNumber);

    @Modifying
    @Query("UPDATE Post p SET p.postTextMessage = ?1, p.updateDate = ?2 WHERE p.id = ?3")
    void updatePostById(String postText, Date updateDate, Long id);

    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 AND p.createDate < p.createDate = ?2 ORDER BY p.createDate DESC " +
            "limit limitNumber = ?3")
    List<Post> findTenByUserIdStartingFromCreateDate(Long id, Date createDate, int limitNumber);
}
