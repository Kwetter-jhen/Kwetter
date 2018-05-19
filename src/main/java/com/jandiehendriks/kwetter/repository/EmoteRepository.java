package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.Emote;
import org.springframework.data.repository.CrudRepository;

public interface EmoteRepository extends CrudRepository<Emote, Long> {
}
