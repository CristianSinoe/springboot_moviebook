package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTweetId(Long tweetId);
}
