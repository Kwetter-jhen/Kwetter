package com.jandiehendriks.kwetter.controller;

import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.domain.Tweet;
import com.jandiehendriks.kwetter.domain.UserType;
import com.jandiehendriks.kwetter.service.AuthService;
import com.jandiehendriks.kwetter.service.KwetterUserService;
import com.jandiehendriks.kwetter.service.TweetService;
import com.jandiehendriks.kwetter.util.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TweetController {
    private static final Logger log =
            LoggerFactory.getLogger(TweetController.class);

    private TweetService tweetService;
    private KwetterUserService kwetterUserService;
    private AuthService authService;
    private LinkUtil linkUtil;

    @Autowired
    public TweetController(TweetService tweetService,
                           KwetterUserService kwetterUserService,
                           AuthService authService) {
        this.tweetService = tweetService;
        this.kwetterUserService = kwetterUserService;
        this.authService = authService;
        this.linkUtil = new LinkUtil();
    }

    @RequestMapping(value = "/tweets",
            method = RequestMethod.GET)
    public ResponseEntity getTweets(
            @RequestHeader String token,
            @RequestParam(value = "username",
                    defaultValue = "") String userNameFilter,
            @RequestParam(value = "reported",
                    defaultValue = "") String reportedFilter,
            @RequestParam(value = "token",
                    defaultValue = "") String tokenFilter,
            @RequestParam(value = "hashtag",
                    defaultValue = "") String hashTagFilter
    ) {
        try {
            List<Tweet> foundTweets = new ArrayList<>();

            if (!userNameFilter.isEmpty())
                foundTweets.addAll(
                        kwetterUserService.getUserByUsername(userNameFilter)
                                .getTweets());

            else if (reportedFilter.equals("true")){
                authService.getUserByToken(token);
                foundTweets.addAll(tweetService
                        .getReportedTweets());
            }
            else if (!tokenFilter.isEmpty())
                foundTweets.addAll(kwetterUserService
                        .getRelevantTweets(
                                authService.getUserByToken(tokenFilter),
                                100));

            else if (!hashTagFilter.isEmpty())
                foundTweets.addAll(tweetService
                        .getTweetsByHashTag(hashTagFilter));

            linkUtil.provideLinksForTweet(foundTweets);

            return new ResponseEntity<>(foundTweets, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/tweets",
            method = RequestMethod.POST)
    public ResponseEntity addTweet(
            @RequestHeader String token,
            @RequestBody Tweet tweet
    ) {
        try {
            Tweet newTweet =
                    tweetService.addTweet(
                            authService.getUserByToken(token),
                            tweet.getText());

            linkUtil.provideLinksForTweet(newTweet);

            return new ResponseEntity<>(newTweet, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/tweets",
            method = RequestMethod.PUT)
    public ResponseEntity reportTweet(
            @RequestBody Tweet tweet
    ) {
        try {
            Tweet reportedTweet =
                    tweetService.reportTweet(tweet.getId());

            linkUtil.provideLinksForTweet(reportedTweet);

            return new ResponseEntity<>(reportedTweet, HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/tweets/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity deleteTweet(
            @RequestHeader String token,
            @PathVariable Long id
    ) {
        try {
            tweetService.deleteTweet(
                    authService.checkUserToken(token, UserType.ADMIN),
                    id);

            return new ResponseEntity(HttpStatus.OK);
        } catch (KwetterException ex) {
            log.error(ex.getMessage());

            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
