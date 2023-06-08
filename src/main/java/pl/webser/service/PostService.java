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

    final int LIMIT_NUMBER_OF_FETCHED_POSTS = 10;

    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public List<Post> getTenPostsStartingFromCreateDate(Date createDate) {
        log.info("Fetching 10 posts from db which createDate starts from: {}", createDate);
        return postRepository.findTenStartingFromCreateDate(createDate, LIMIT_NUMBER_OF_FETCHED_POSTS);
    }

    public List<Post> getUserPosts(Long userId, Date createDate) {
        log.info("Fetching 10 posts of user with id: {}, which createDate starts from: {}", userId, createDate);
        return postRepository.findTenByUserIdStartingFromCreateDate(userId, createDate, LIMIT_NUMBER_OF_FETCHED_POSTS);
    }

    public Post getPost(Long postId) {
        log.info("Fetching post with id {}", postId);
        return postRepository.getById(postId);
    }

    public void addPost(String emailAddress, String postText) {
        User user = userService.getUserByEmailAddress(emailAddress);
        Post post = new Post();
        post.setUser(user);
        post.setPostTextMessage(postText);
        post.setCreateDate(new Date(System.currentTimeMillis()));
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        log.info("Deleting post with id {}", postId);
        postRepository.deleteById(postId);
    }

    public void editPost(String postText, Long postId) {
        log.info("Saving post with id {}, after edit", postId);
        Date updateDate = new Date(System.currentTimeMillis());
        postRepository.updatePostById(postText, updateDate, postId);
    }

    public boolean isPostBelongsToUser(User user, Post post) {
        Post postFromDb = postRepository.getById(post.getId());
        return postFromDb.getUser().equals(user);
    }
}
