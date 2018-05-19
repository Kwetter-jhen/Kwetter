package com.jandiehendriks.kwetter.repository;

import com.jandiehendriks.kwetter.domain.KwetterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KwetterUserRepository extends JpaRepository<KwetterUser, Long> {
    Optional<KwetterUser> findByToken(String token);
    Optional<KwetterUser> findByUsername(String username);
}
