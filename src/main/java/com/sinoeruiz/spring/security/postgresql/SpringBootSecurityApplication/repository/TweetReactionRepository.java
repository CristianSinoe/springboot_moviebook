package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.TweetReaction;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Tweet;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TweetReactionRepository extends JpaRepository<TweetReaction, Long> {
    Optional<TweetReaction> findByTweetAndUser(Tweet tweet, User user);
    List<TweetReaction> findByTweetId(Long tweetId);
}
