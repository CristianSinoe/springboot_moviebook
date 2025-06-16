package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.controllers;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.EReaction;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Tweet;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.User;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response.CommentResponse;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response.TweetResponse;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response.UserMinResponse;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.TweetRepository;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.UserRepository;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response.UserMinResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tweets")
public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    private final Path uploadDir = Paths.get("uploads");

@GetMapping("/all")
public ResponseEntity<List<TweetResponse>> getAllTweets() {
    List<Tweet> tweets = tweetRepository.findAllWithComments();

    List<TweetResponse> response = new ArrayList<>();

    for (Tweet tweet : tweets) {
        // Convertir comentarios
        List<CommentResponse> commentDTOs = tweet.getComments().stream().map(comment ->
            new CommentResponse(
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getUsername()
            )
        ).toList();

        // Contar reacciones
        Map<String, Long> rawCount = tweet.getReactions().stream()
            .filter(r -> r.getReaction() != null && r.getReaction().getName() != null)
            .collect(Collectors.groupingBy(
                r -> r.getReaction().getName().name(),
                Collectors.counting()
            ));

        Map<String, Integer> reactionCount = new LinkedHashMap<>();
        for (EReaction er : EReaction.values()) {
            long count = rawCount.getOrDefault(er.name(), 0L);
            reactionCount.put(er.name(), (int) count);
        }

        reactionCount = reactionCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));

        // Construir respuesta
        TweetResponse tweetDTO = new TweetResponse(
    tweet.getId(), // ✅ agregar el ID
    tweet.getTweet(),
    tweet.getImageUrl(),
    new UserMinResponse(
        tweet.getPostedBy().getId(),
        tweet.getPostedBy().getUsername()
    ),
    commentDTOs,
    reactionCount
);

        response.add(tweetDTO);
    }

    return ResponseEntity.ok(response);
}


    @PostMapping("/create")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> createTweet(
        @RequestParam("tweet") String tweetText,
        @RequestParam(value = "image", required = false) MultipartFile imageFile,
        Authentication authentication) {

    try {
        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        User user = userOpt.get();
        Tweet tweet = new Tweet(tweetText);
        tweet.setPostedBy(user);

        if (imageFile != null && !imageFile.isEmpty()) {
            String url = saveImage(imageFile);
            if (url == null) {
                return ResponseEntity.badRequest().body("Formato de imagen no permitido. Solo JPG y PNG.");
            }
            tweet.setImageUrl(url);
        }

        Tweet saved = tweetRepository.save(tweet);

        // Solo devolver lo necesario
        TweetResponse response = new TweetResponse(
    saved.getId(), // ✅ agregar el ID del tweet guardado
    saved.getTweet(),
    saved.getImageUrl(),
    new UserMinResponse(user.getId(), user.getUsername()),
    null, // comentarios vacíos por ahora
    Map.of() // sin reacciones aún
);

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
}

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String url = saveImage(file);
            if (url == null) {
                return ResponseEntity.badRequest().body("Formato inválido. Usa .jpg, .jpeg o .png");
            }
            return ResponseEntity.ok(Map.of("imageUrl", url));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir imagen: " + e.getMessage());
        }
    }

    // 🔐 Método utilitario privado
    private String saveImage(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        if (originalName == null) return null;

        String extension = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
        if (!List.of("jpg", "jpeg", "png").contains(extension)) {
            return null;
        }

        Files.createDirectories(uploadDir);
        String filename = UUID.randomUUID() + "_" + originalName;
        Path destination = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + filename;
    }
}

