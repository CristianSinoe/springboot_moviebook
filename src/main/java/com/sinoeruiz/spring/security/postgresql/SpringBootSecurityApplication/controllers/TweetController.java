package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Tweet;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.User;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.TweetRepository;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tweets")
public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    public Page<Tweet> getTweet(Pageable pageable) {
        return tweetRepository.findAll(pageable);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createTweet(
            @RequestParam("tweet") String tweetText,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = userOpt.get();

            Tweet tweet = new Tweet(tweetText);
            tweet.setPostedBy(user);

            // Subida de imagen si existe
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

                if (!fileExtension.equals("jpg") && !fileExtension.equals("jpeg") && !fileExtension.equals("png")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solo se permiten imágenes JPG o PNG");
                }

                String filename = UUID.randomUUID() + "_" + originalFilename;
                Path uploadPath = Paths.get("uploads");
                Files.createDirectories(uploadPath);

                Path imagePath = uploadPath.resolve(filename);
                Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                tweet.setImageUrl("/uploads/" + filename);
            }

            Tweet saved = tweetRepository.save(tweet);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear tweet: " + e.getMessage());
        }
    }
}
