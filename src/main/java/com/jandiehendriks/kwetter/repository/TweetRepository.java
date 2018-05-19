package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByReported(boolean reported);
}
