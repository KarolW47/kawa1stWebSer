package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.webser.model.Post;
import pl.webser.model.User;
import pl.webser.repository.PostRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public List<Post> getPosts() {
        log.info("Fetching all posts from db.");
        return postRepository.findAll();
    }

    public List<Post> getUserPosts(Long id) {
        log.info("Fetching all posts of user with id: {}", id);
        return postRepository.findByUserId(id);
    }

    public Post getPost(Long id) {
        log.info("Fetching post with id {}", id);
        return postRepository.getById(id);
    }

    public Post addPost(String emailAddress, String postText) {
        User user = userService.getUserByEmailAddress(emailAddress);
        Post post = new Post();
        post.setUser(user);
        post.setPostTextMessage(postText);
        post.setCreateDate(new Date(System.currentTimeMillis()));
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        log.info("Deleting post with id {}", id);
        postRepository.deleteById(id);
    }

    public void editPost(String postText, Long id) {
        log.info("Saving post with id {}, after edit", id);
        Date updateDate = new Date(System.currentTimeMillis());
        postRepository.updatePostById(postText, updateDate, id);
    }

    public boolean isPostBelongsToUser(User user, Post post) {
        Post postFromDb = postRepository.getById(post.getId());
        return postFromDb.getUser().equals(user);
    }
}
