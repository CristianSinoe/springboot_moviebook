package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response;

import java.util.List;
import java.util.Map;

public class TweetResponse {
    private String tweet;
    private String imageUrl;
    private String username;
    private List<CommentResponse> comments;
    private Map<String, Integer> reactions;

    public TweetResponse(String tweet, String imageUrl, String username,
                         List<CommentResponse> comments,
                         Map<String, Integer> reactions) {
        this.tweet = tweet;
        this.imageUrl = imageUrl;
        this.username = username;
        this.comments = comments;
        this.reactions = reactions;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }

    public Map<String, Integer> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, Integer> reactions) {
        this.reactions = reactions;
    }
}
