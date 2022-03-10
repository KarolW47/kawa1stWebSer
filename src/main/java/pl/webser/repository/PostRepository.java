package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.webser.model.Post;

import java.util.ArrayList;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
