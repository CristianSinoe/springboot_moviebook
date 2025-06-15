package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository;


import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Tweet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    @Query("SELECT DISTINCT t FROM Tweet t " +
       "LEFT JOIN FETCH t.comments c " +
       "LEFT JOIN FETCH c.user " +
       "LEFT JOIN FETCH t.postedBy")
List<Tweet> findAllWithComments(); 
}
