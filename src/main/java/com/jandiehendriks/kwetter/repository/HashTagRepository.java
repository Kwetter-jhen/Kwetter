package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.HashTag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface HashTagRepository extends CrudRepository<HashTag, Long> {
    Optional<HashTag> findByText(String text);
}
