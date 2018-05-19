package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.KwetterUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface KwetterUserRepository extends CrudRepository<KwetterUser, Long> {
    Optional<KwetterUser> findByToken(String token);
    Optional<KwetterUser> findByUsername(String username);
}
