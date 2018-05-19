package com.jandiehendriks.kwetter.service;

import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.domain.UserType;
import com.jandiehendriks.kwetter.repository.KwetterUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private KwetterUserRepository kwetterUserRepository;

    @Autowired
    public AuthService(KwetterUserRepository kwetterUserRepository) {
        this.kwetterUserRepository = kwetterUserRepository;
    }

    public KwetterUser getUserByToken(String token) throws KwetterException {
        Optional<KwetterUser> user = kwetterUserRepository.findByToken(token);

        if (!user.isPresent())
            throw new KwetterException("Token " + token + " is not valid");

        return user.get();
    }

    public KwetterUser checkUserToken(String token, UserType userType) throws KwetterException {
        KwetterUser user = getUserByToken(token);

        if (user.getUserType() != userType) {
            throw new KwetterException("You don't have enough rights to perform this action.");
        }

        return user;
    }
}
