package com.jandiehendriks.kwetter.service;
import com.jandiehendriks.kwetter.domain.GifEmote;
import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.domain.Tweet;
import com.jandiehendriks.kwetter.repository.EmoteRepository;
import com.jandiehendriks.kwetter.repository.KwetterUserRepository;
import com.jandiehendriks.kwetter.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 * @author Jandie
 */
@Service
public class EmoteService {

    private EmoteRepository emoteRepository;
    private TweetRepository tweetRepository;

    @Autowired
    public EmoteService(EmoteRepository emoteRepository,
                        TweetRepository tweetRepository) {
        this.emoteRepository = emoteRepository;
        this.tweetRepository = tweetRepository;
    }

    public GifEmote addGifEmote (KwetterUser kwetterUser, String name, String url, Long tweetId)
            throws KwetterException {
        Optional<Tweet> tweet = tweetRepository.findById(tweetId);

        if (!tweet.isPresent())
            throw new KwetterException("Tweet not found");
        
        GifEmote emote = new GifEmote(url, name, kwetterUser);
        tweet.get().getEmotes().add(emote);
        
        return emoteRepository.save(emote);
    }
}
