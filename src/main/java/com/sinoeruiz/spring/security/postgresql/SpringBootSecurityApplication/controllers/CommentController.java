package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.controllers;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.*;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response.CommentResponse;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create/{tweetId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createComment(@PathVariable Long tweetId,
                                           @Valid @RequestBody Comment comment,
                                           Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Tweet> tweetOpt = tweetRepository.findById(tweetId);

        if (userOpt.isEmpty() || tweetOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Tweet o Usuario no encontrado");
        }

        comment.setUser(userOpt.get());
        comment.setTweet(tweetOpt.get());

        Comment saved = commentRepository.save(comment);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/tweet/{tweetId}")
public ResponseEntity<List<CommentResponse>> getCommentsByTweet(@PathVariable Long tweetId) {
    List<Comment> comments = commentRepository.findByTweetId(tweetId);

    List<CommentResponse> response = comments.stream()
        .map(c -> new CommentResponse(
            c.getContent(),
            c.getCreatedAt(),
            c.getUser().getUsername()
        ))
        .toList();

    return ResponseEntity.ok(response);
}

@PutMapping("/{commentId}")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                       @RequestBody String newContent,
                                       Authentication authentication) {
    Optional<Comment> commentOpt = commentRepository.findById(commentId);
    if (commentOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Comment comment = commentOpt.get();
    String username = authentication.getName();

    if (!comment.getUser().getUsername().equals(username)) {
        return ResponseEntity.status(403).body("No tienes permiso para editar este comentario.");
    }

    comment.setContent(newContent);
    commentRepository.save(comment);

    return ResponseEntity.ok("Comentario actualizado correctamente ✏️");
}
@DeleteMapping("/{commentId}")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                       Authentication authentication) {
    Optional<Comment> commentOpt = commentRepository.findById(commentId);
    if (commentOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Comment comment = commentOpt.get();
    String username = authentication.getName();

    if (!comment.getUser().getUsername().equals(username)) {
        return ResponseEntity.status(403).body("No tienes permiso para eliminar este comentario.");
    }

    commentRepository.delete(comment);
    return ResponseEntity.ok("Comentario eliminado exitosamente 🗑️");
}

}

