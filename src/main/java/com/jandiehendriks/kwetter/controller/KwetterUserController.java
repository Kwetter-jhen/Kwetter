package com.jandiehendriks.kwetter.controller;

import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.domain.UserType;
import com.jandiehendriks.kwetter.service.AuthService;
import com.jandiehendriks.kwetter.service.KwetterUserService;
import com.jandiehendriks.kwetter.util.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
public class KwetterUserController {

    private static final Logger log =
            LoggerFactory.getLogger(KwetterUserController.class);

    private KwetterUserService kwetterUserService;
    private AuthService authService;
    private LinkUtil linkUtil;

    @Autowired
    public KwetterUserController(KwetterUserService kwetterUserService,
                                 AuthService authService) {
        this.kwetterUserService = kwetterUserService;
        this.authService = authService;
        this.linkUtil = new LinkUtil();
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity allUsers(
            @RequestHeader String token,
            @RequestParam(name = "token", defaultValue = "")
                    String tokenFilter) {
        try {
            List<KwetterUser> foundUsers;

            if (tokenFilter != null && !tokenFilter.isEmpty()) {
                KwetterUser foundUser =
                        authService.getUserByToken(tokenFilter);

                new LinkUtil().provideLinksForUser(foundUser);

                return new ResponseEntity<>(foundUser, HttpStatus.OK);
            }
            else {
                authService.checkUserToken(token, UserType.ADMIN);

                foundUsers = kwetterUserService.getAllUsers();
            }

            linkUtil.provideLinksForUser(foundUsers);

            return new ResponseEntity<>(foundUsers, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/{username}",
            method = RequestMethod.GET)
    public ResponseEntity getUser(
            @PathVariable String username
    ) {
        try {
            KwetterUser user =
                    kwetterUserService.getUserByUsername(username);

            linkUtil.provideLinksForUser(user);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users",
            method = RequestMethod.POST)
    public ResponseEntity registerUser(@RequestBody KwetterUser user,
                                       @RequestHeader String password) {
        try {
            KwetterUser newUser = kwetterUserService.registerKwetterUser(user.getUsername(),
                    user.getEmail(),
                    user.getWebsite(),
                    user.getBio(),
                    password);

            linkUtil.provideLinksForUser(newUser);

            return new ResponseEntity<>(newUser, HttpStatus.ACCEPTED);
        } catch (NoSuchAlgorithmException | KwetterException e) {
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        }
    }
}
