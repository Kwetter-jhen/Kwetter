package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.Tweet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TweetRepository extends CrudRepository<Tweet, Long> {
    List<Tweet> findByReported(boolean reported);
}
