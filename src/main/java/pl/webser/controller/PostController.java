package pl.webser.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.webser.model.Post;
import pl.webser.model.User;
import pl.webser.security.JWTUtil;
import pl.webser.service.PostService;
import pl.webser.service.UserService;

import java.net.URI;
import java.util.List;

import static pl.webser.security.filter.CustomAuthorizationFilter.ACCESS_TOKEN_HEADER;

@RestController
@Slf4j
@RequestMapping("/post")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Autowired
    public PostController(PostService postService, UserService userService, JWTUtil jwtUtil) {
        this.postService = postService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addPost(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token, @RequestBody Post post) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        User user = userService.getUserByUsername(username);
        log.info("Successfully added post of user {}.", username);
        URI uri =
                URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/register").toUriString());
        return ResponseEntity.created(uri).body(postService.addPost(user.getUsername(), post.getPostTextMessage()));
    }

    @GetMapping(path = "/posts")
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok().body(postService.getPosts());
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> deletePost(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                        @RequestBody Post post) {
        User userFromDb = userService.getUserByUsername(jwtUtil.getUserNameFromJwtToken(token));
        if (postService.isPostBelongsToUser(userFromDb, post)) {
            postService.deletePost(post.getId());
            return ResponseEntity.status(HttpStatus.OK).body("Post deleted successfully.");
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Post do not belongs to user.");
    }

    @PatchMapping(path = "/edit")
    public ResponseEntity<?> editPost(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token, Post post) {
        User userFromDb = userService.getUserByUsername(jwtUtil.getUserNameFromJwtToken(token));
        if (postService.isPostBelongsToUser(userFromDb, post)) {
            return ResponseEntity.status(HttpStatus.OK).body(postService.editPost(post.getPostTextMessage(),
                    post.getId()));
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Post do not belongs to user.");
    }
}
