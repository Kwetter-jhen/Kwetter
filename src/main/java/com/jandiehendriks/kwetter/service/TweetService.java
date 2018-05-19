package com.jandiehendriks.kwetter.service;

import com.jandiehendriks.kwetter.domain.HashTag;
import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.domain.Tweet;
import com.jandiehendriks.kwetter.domain.UserType;
import com.jandiehendriks.kwetter.repository.HashTagRepository;
import com.jandiehendriks.kwetter.repository.KwetterUserRepository;
import com.jandiehendriks.kwetter.repository.TweetRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jandie
 */
@Service
public class TweetService {
    
    private TweetRepository tweetRepository;
    private KwetterUserRepository kwetterUserRepository;
    private HashTagRepository hashTagRepository;

    public TweetService(TweetRepository tweetRepository,
                        KwetterUserRepository kwetterUserRepository,
                        HashTagRepository hashTagRepository) {
        this.tweetRepository = tweetRepository;
        this.kwetterUserRepository = kwetterUserRepository;
        this.hashTagRepository = hashTagRepository;
    }

    public Tweet addTweet(KwetterUser user, String text) throws KwetterException {
        checkTweetLength(text);

        List<KwetterUser> mentionedUsers = convertMentionsToUsers(parseText(text, "@"));
        List<HashTag> hashTags = convertHashTags(parseText(text, "#"));

        Tweet tweet = new Tweet(text, new Date());
        tweetRepository.save(tweet);
        
        tweet.setMentions(mentionedUsers);
        tweet.setHashTags(hashTags);
        
        user.getTweets().add(tweet);
        tweet.setKwetterUser(user);
        
        kwetterUserRepository.save(user);
        tweetRepository.save(tweet);
        
        for(HashTag tag : tweet.getHashTags()) {
            tag.getTweets().add(tweet);
            hashTagRepository.save(tag);
        }
        
        return tweet;
    }
    
    public Tweet addReaction(KwetterUser user, String text, Long id)
            throws KwetterException  {
        checkTweetLength(text);

        List<KwetterUser> mentionedUsers = convertMentionsToUsers(parseText(text, "@"));
        List<HashTag> hashTags = convertHashTags(parseText(text, "#"));
                    
        Tweet tweet = new Tweet(text, new Date());
        tweetRepository.save(tweet);
        
        tweet.setMentions(mentionedUsers);
        tweet.setHashTags(hashTags);

        Optional<Tweet> parentTweet = tweetRepository.findById(id);

        if (!parentTweet.isPresent())
            throw new KwetterException("Invalid parent tweet");

        
        parentTweet.get().getTweets().add(tweet);
        user.getTweets().add(tweet);
        tweet.setParent(parentTweet.get());
        tweet.setKwetterUser(user);
        
        tweetRepository.save(parentTweet.get());
        kwetterUserRepository.save(user);
        tweetRepository.save(tweet);
        
        for(HashTag tag : tweet.getHashTags()) {
            tag.getTweets().add(tweet);
            hashTagRepository.save(tag);
        }
        
        return tweet;
    }
    
    public List<Tweet> getTweetsByHashTag (String hashTag) {
        Optional<HashTag> tag = hashTagRepository.findByText(hashTag);
        
        if (!tag.isPresent()) return new ArrayList<>();
        
        return tag.get().getTweets();
    }
    
    public Tweet reportTweet(Long id) throws KwetterException {
        Optional<Tweet> tweet = tweetRepository.findById(id);

        if (!tweet.isPresent())
            throw new KwetterException("The tweet you are trying to report doesn't exist");
        
        tweet.get().setReported(true);
        
        tweetRepository.save(tweet.get());
        
        return tweet.get();
    }
    
    public List<Tweet> getReportedTweets() throws KwetterException {
        return tweetRepository.findByReported(true);
    }
    
    public void deleteTweet (KwetterUser authUser, Long id) throws KwetterException {

        if (authUser.getUserType() != UserType.ADMIN)
            throw new KwetterException("You must have admin rights to remove tweets.");
        
        Optional<Tweet> tweetToRemove = tweetRepository.findById(id);

        if (!tweetToRemove.isPresent())
            throw new KwetterException("The tweet you are trying to remove does not exist");

        tweetToRemove.get().getKwetterUser().getTweets().remove(tweetToRemove.get());
        
        tweetRepository.delete(tweetToRemove.get());
        kwetterUserRepository.save(tweetToRemove.get().getKwetterUser());
    }

    private List<KwetterUser> convertMentionsToUsers(List<String> mentionedUsernames) {
        List<KwetterUser> users = new ArrayList<>();

        for(String username : mentionedUsernames) {
            Optional<KwetterUser> user =
                    kwetterUserRepository.findByUsername(
                            username.substring(1));

            user.ifPresent(users::add);
        }

        return users;
    }

    private List<HashTag> convertHashTags(List<String> hashTagNames) throws KwetterException {
        List<HashTag> hashTags = new ArrayList<>();

        for (String text : hashTagNames) {
            HashTag hashTag;
            Optional<HashTag> tag = hashTagRepository.findByText(text);

            if (!tag.isPresent()) {
                hashTag = new HashTag(text);
                hashTagRepository.save(hashTag);
            }
            else {
                hashTag = tag.get();
            }

            hashTags.add(hashTag);
        }

        return hashTags;
    }

    private List<String> parseText(String text, String tag) {
        List<String> mentionedUsers = new ArrayList<>();

        Pattern pattern = Pattern.compile(tag + "\\w+");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find())
        {
            mentionedUsers.add(matcher.group());
        }

        return mentionedUsers;
    }
    
    private void checkTweetLength(String text) throws KwetterException {
        if (text.length() > 140) 
            throw new KwetterException(
                    "The tweet is longer than 140 characters!");
    }
}
