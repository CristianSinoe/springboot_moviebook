package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Tweet;



@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

}
