package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "tweet_reactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tweet_id", "user_id"})
})
public class TweetReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id")
    @JsonIgnoreProperties({"reactions", "comments", "postedBy", "hibernateLazyInitializer", "handler"})
    private Tweet tweet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"tweets", "comments", "roles", "hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Reaction reaction;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }
}