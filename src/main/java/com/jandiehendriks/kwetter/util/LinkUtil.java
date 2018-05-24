/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jandiehendriks.kwetter.util;

import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.domain.Link;
import com.jandiehendriks.kwetter.domain.Tweet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jandie
 */
public class LinkUtil {
    private String mainUrl = "https://kwetter-jhen-restless-bilby.cfapps.io";
    //private String mainUrl = "http://localhost:8080";

    public KwetterUser provideLinksForUser(KwetterUser user) {
        List<Link> links = new ArrayList<>();
        
        links.add(new Link(mainUrl + "/users/" + user.getUsername(),
                "self"));        
        links.add(new Link(mainUrl + "/users/" + user.getUsername() + 
                "/following", "following"));
        links.add(new Link(mainUrl + "/users/" + user.getUsername() + 
                "/followers", "followers"));
        links.add(new Link(mainUrl + "/users/" + user.getUsername() + 
                "/relevanttweets", "relevanttweets"));
        links.add(new Link(mainUrl + "/users/admin", "makeadmin"));
        
        user.setLinks(links);
        
        return user;
    }
    
    public List<KwetterUser> provideLinksForUser(List<KwetterUser> users) {
        users.forEach((user) -> {
            provideLinksForUser(user);
        });
        
        return users;
    }
    
    public Tweet provideLinksForTweet(Tweet tweet) {
        List<Link> links = new ArrayList<>();
        
        links.add(new Link(mainUrl + "/tweets/" + tweet.getId(), "self"));
        links.add(new Link(mainUrl + "/tweets/" + tweet.getId(), "delete"));
        links.add(new Link(mainUrl + "/tweets", "change"));

        tweet.setLinks(links);
        
        return tweet;
    }
    
    public List<Tweet> provideLinksForTweet(List<Tweet> tweets) {
        tweets.forEach((tweet) -> {
            provideLinksForTweet(tweet);
        });
        
        return tweets;
    }
}
