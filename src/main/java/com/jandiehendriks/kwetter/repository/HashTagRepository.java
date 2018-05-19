package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    Optional<HashTag> findByText(String text);
}
