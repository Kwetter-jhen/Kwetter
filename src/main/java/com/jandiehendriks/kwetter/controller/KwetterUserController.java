package com.jandiehendriks.kwetter.controller;

import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.domain.Tweet;
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

    @RequestMapping(value = "/users/{username}/billingid",
            method = RequestMethod.GET)
    public ResponseEntity getBillingId(
            @PathVariable String username,
            @RequestHeader String token) {
        try {
            KwetterUser authUser = authService.getUserByToken(token);
            KwetterUser user =
                    kwetterUserService.getUserByUsername(username);

            if (!authUser.getUsername().equals(username) &&
                    authUser.getUserType() != UserType.ADMIN)
                throw new KwetterException("You dont have permision to get this information");

            return new ResponseEntity<>(
                    "\"" + user.getBillingId() + "\"",
                    HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/login",
            method = RequestMethod.GET)
    public ResponseEntity loginUser(
            @RequestHeader String username,
            @RequestHeader String password) {
        try {
            String token = kwetterUserService.loginKwetterUser(username,
                    password);
            token = "\"" + token + "\"";

            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (KwetterException | NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/{username}/followers",
            method = RequestMethod.GET)
    public ResponseEntity getFollowers(
            @PathVariable String username) {
        try {
            List<KwetterUser> followers =
                    kwetterUserService
                            .getUserByUsername(username)
                            .getFollowers();

            linkUtil.provideLinksForUser(followers);

            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/{username}/following",
            method = RequestMethod.GET)
    public ResponseEntity getFollowing(
            @PathVariable String username) {
        try {
            List<KwetterUser> followers =
                    kwetterUserService
                            .getUserByUsername(username)
                            .getFollowing();

            linkUtil.provideLinksForUser(followers);

            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/{username}/relevanttweets",
            method = RequestMethod.GET)
    public ResponseEntity getRelevantTweets(
            @PathVariable String username,
            @RequestHeader String token) {
        try {
            List<Tweet> tweets = kwetterUserService.getRelevantTweets(
                    authService.getUserByToken(token), 100);

            linkUtil.provideLinksForTweet(tweets);

            return new ResponseEntity<>(tweets, HttpStatus.OK);
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

    @RequestMapping(value = "/users/follow/{username}",
            method = RequestMethod.PUT)
    public ResponseEntity followUser(
            @PathVariable String username,
            @RequestHeader String token) {
        try {
            KwetterUser user =
                    kwetterUserService.addFollower(
                            authService.getUserByToken(token),
                            username);

            linkUtil.provideLinksForUser(user);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/update",
            method = RequestMethod.PUT)
    public ResponseEntity updateUser(
            @RequestHeader String token,
            @RequestHeader String password,
            @RequestBody KwetterUser kwetterUser) {
        try {
            KwetterUser user =
                    kwetterUserService.updateUser(
                            authService.getUserByToken(token),
                            kwetterUser.getUsername(),
                            kwetterUser.getEmail(),
                            kwetterUser.getWebsite(),
                            kwetterUser.getBio(),
                            password
                    );

            linkUtil.provideLinksForUser(user);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (KwetterException | NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/admin",
            method = RequestMethod.PUT)
    public ResponseEntity makeAdmin(
            @RequestHeader String token,
            @RequestBody KwetterUser kwetterUser) {
        try {
            KwetterUser user =
                    kwetterUserService.makeAdmin(
                            authService.getUserByToken(token),
                            kwetterUser.getUsername());

            linkUtil.provideLinksForUser(user);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
