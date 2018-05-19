package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.Emote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmoteRepository extends JpaRepository<Emote, Long> {
}
