package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.controllers;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response.CommentResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Comment;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Tweet;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.User;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.request.CommentCreateRequest;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.request.CommentUpdateRequest;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.CommentRepository;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.TweetRepository;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    // ✅ Crear comentario
    @PostMapping("/create/{tweetId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createComment(@PathVariable Long tweetId,
                                           @Valid @RequestBody CommentCreateRequest request,
                                           Authentication authentication) {

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Tweet> tweetOpt = tweetRepository.findById(tweetId);

        if (userOpt.isEmpty() || tweetOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tweet o Usuario no encontrado"));
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setTweet(tweetOpt.get());

        Comment saved = commentRepository.save(comment);
        return ResponseEntity.ok(Map.of("message", "Comentario creado correctamente 💬", "id", saved.getId()));
    }

    // ✅ Obtener comentarios por tweet
    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTweet(@PathVariable Long tweetId) {
        List<Comment> comments = commentRepository.findByTweetId(tweetId);

        List<CommentResponse> response = comments.stream()
            .map(c -> new CommentResponse(
                c.getId(),
                c.getContent(),
                c.getCreatedAt(),
                c.getUser().getUsername()
            )).toList();

        return ResponseEntity.ok(response);
    }

    // ✅ Editar comentario
    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @Valid @RequestBody CommentUpdateRequest request,
                                           Authentication authentication) {

        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = commentOpt.get();
        String username = authentication.getName();

        if (!comment.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "No tienes permiso para editar este comentario."));
        }

        comment.setContent(request.getContent());
        commentRepository.save(comment);

        return ResponseEntity.ok(Map.of("message", "Comentario actualizado correctamente ✏️"));
    }

    // ✅ Eliminar comentario
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           Authentication authentication) {

        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = commentOpt.get();
        String username = authentication.getName();

        boolean isOwner = comment.getUser().getUsername().equals(username);
        boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "No tienes permiso para eliminar este comentario."));
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok(Map.of("message", "Comentario eliminado exitosamente 🗑️"));
    }
}
