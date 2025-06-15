package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.controllers;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.*;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.request.TweetReactionRequest;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reactions")
public class TweetReactionController {

    @Autowired
    private TweetReactionRepository tweetReactionRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @PostMapping("/react")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> reactToTweet(@RequestBody TweetReactionRequest request, Authentication auth) {
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<Tweet> tweetOpt = tweetRepository.findById(request.getTweetId());
        Optional<Reaction> reactionOpt = reactionRepository.findById(request.getReactionId());

        if (userOpt.isEmpty() || tweetOpt.isEmpty() || reactionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Datos inválidos");
        }

        User user = userOpt.get();
        Tweet tweet = tweetOpt.get();
        Reaction reaction = reactionOpt.get();

        Optional<TweetReaction> existingReaction = tweetReactionRepository.findByTweetAndUser(tweet, user);

        TweetReaction tweetReaction = existingReaction.orElse(new TweetReaction());
        tweetReaction.setTweet(tweet);
        tweetReaction.setUser(user);
        tweetReaction.setReaction(reaction);

        TweetReaction saved = tweetReactionRepository.save(tweetReaction);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<?> getReactionsByTweet(@PathVariable Long tweetId) {
        List<TweetReaction> reactions = tweetReactionRepository.findByTweetId(tweetId);
        Map<String, Long> countByType = new HashMap<>();

        for (TweetReaction tr : reactions) {
            String type = tr.getReaction().getDescription().name();
            countByType.put(type, countByType.getOrDefault(type, 0L) + 1);
        }

        return ResponseEntity.ok(countByType);
    }
}
