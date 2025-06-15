package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.request;

public class TweetReactionRequest {
    private Long tweetId;
    private Long reactionId;

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public Long getReactionId() {
        return reactionId;
    }

    public void setReactionId(Long reactionId) {
        this.reactionId = reactionId;
    }
}
