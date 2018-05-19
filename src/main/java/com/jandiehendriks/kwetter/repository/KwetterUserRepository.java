package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.KwetterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KwetterUserRepository extends JpaRepository<KwetterUser, Long> {
    Optional<KwetterUser> findByToken(String token);
    Optional<KwetterUser> findByUsername(String username);
}
