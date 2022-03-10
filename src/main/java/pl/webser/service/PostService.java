package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.webser.model.Post;
import pl.webser.repository.PostRepository;
import pl.webser.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Post> getPosts() {
        log.info("Fetching all posts from db.");
        return postRepository.findAll();
    }



}
