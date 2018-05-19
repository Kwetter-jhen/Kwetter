package com.jandiehendriks.kwetter.service;

import com.jandiehendriks.kwetter.domain.KwetterException;
import com.jandiehendriks.kwetter.domain.KwetterUser;
import com.jandiehendriks.kwetter.domain.Tweet;
import com.jandiehendriks.kwetter.domain.UserType;
import com.jandiehendriks.kwetter.repository.KwetterUserRepository;
import com.jandiehendriks.kwetter.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Jandie
 */
@Service
public class KwetterUserService {

    private KwetterUserRepository kwetterUserRepository;

    @Autowired
    public KwetterUserService(KwetterUserRepository kwetterUserRepository) {
        this.kwetterUserRepository = kwetterUserRepository;
    }

    public List<KwetterUser> getAllUsers() {
        List<KwetterUser> users = new ArrayList<>();
        kwetterUserRepository.findAll().forEach(users::add);

        return users;
    }

    public KwetterUser addFollower(KwetterUser user, String usernameToFollow) throws KwetterException {
        KwetterUser userToFollow = getUserByUsername(usernameToFollow);

        if (user.hasFollowing(usernameToFollow)
                || user.getUsername().equals(usernameToFollow))
            throw new KwetterException("User is already being followed.");

        user.getFollowing().add(userToFollow);
        userToFollow.getFollowers().add(user);

        kwetterUserRepository.save(user);
        kwetterUserRepository.save(userToFollow);

        return userToFollow;
    }

    public KwetterUser getUserByUsername(String username) throws KwetterException {
        Optional<KwetterUser> user = kwetterUserRepository.findByUsername(username);

        if (!user.isPresent())
            throw new KwetterException("Username does not exsist!");

        return user.get();
    }

    public String loginKwetterUser(String username, String password) throws KwetterException, NoSuchAlgorithmException {
        KwetterUser user = getUserByUsername(username);

        if (!new HashUtil().hashPassword(password, user.getSalt(),
                "SHA-256", "UTF-8").equals(user.getHashedPassword())) {
            throw new KwetterException("Wrong username or password!");
        }

        return changeUserToken(user);
    }

    public List<Tweet> getRelevantTweets(KwetterUser user, int maxAmount) {
        List<Tweet> tweets = new ArrayList<>();

        tweets.addAll(user.getAmountOfTweets(maxAmount));

        for (KwetterUser kwetterUser : user.getFollowing()) {
            tweets.addAll(kwetterUser.getAmountOfTweets(maxAmount));
        }

        return tweets;
    }

    public KwetterUser registerKwetterUser(String username, String email, String website,
            String bio, String password) throws NoSuchAlgorithmException, KwetterException {
        HashUtil hashUtil = new HashUtil();

        checkUserParameters(username, email, website, bio, password);

        String salt = hashUtil.generateSalt();
        String hashedPassword = hashUtil.hashPassword(password, salt, "SHA-256", "UTF-8");
        String token = hashUtil.generateSalt();
        KwetterUser user = new KwetterUser(username, email, website, bio, hashedPassword, salt, token);

        return kwetterUserRepository.save(user);
    }

    public KwetterUser updateUser(KwetterUser authUser, String username, String email,
            String website, String bio, String password) throws KwetterException, NoSuchAlgorithmException {
        HashUtil hashUtil = new HashUtil();

        checkUserParameters(username, email, website, bio, password);

        KwetterUser userToUpdate = getUserByUsername(username);

        if (authUser.getUserType() != UserType.ADMIN
                && !authUser.getUsername().equals(userToUpdate.getUsername())) {
            throw new KwetterException("You must have admin rights to get all users.");
        }

        String salt = hashUtil.generateSalt();
        String hashedPassword = hashUtil.hashPassword(password, salt, "SHA-256", "UTF-8");

        userToUpdate.setEmail(email);
        userToUpdate.setWebsite(website);
        userToUpdate.setBio(bio);
        userToUpdate.setHashedPassword(hashedPassword);
        userToUpdate.setSalt(salt);

        return kwetterUserRepository.save(userToUpdate);
    }

    public KwetterUser makeAdmin(KwetterUser authUser, String username) throws KwetterException {
        return changeUserType(authUser, username, UserType.ADMIN);
    }
    
    public KwetterUser demoteUser(KwetterUser authUser, String username) throws KwetterException {
        return changeUserType(authUser, username, UserType.NORMAL);
    }
    
    private KwetterUser changeUserType(KwetterUser authUser, String username, UserType userType)
            throws KwetterException {

        if (authUser.getUserType() != UserType.ADMIN) {
            throw new KwetterException("You must have admin rights to demote a user.");
        }

        KwetterUser userToUpdate = getUserByUsername(username);
        userToUpdate.setUserType(userType);

        kwetterUserRepository.save(userToUpdate);

        return userToUpdate;
    }

    public void init() throws NoSuchAlgorithmException, KwetterException {
        KwetterUser kwetterUser = registerKwetterUser(
            "henk",
            "jandie@pm.me",
            "www.henk.nl",
            "ik ben henk",
            "testpwd1");
        
        kwetterUser.setUserType(UserType.ADMIN);
        
        kwetterUserRepository.save(kwetterUser);
    }

    private void checkUserParameters(String username, String email, String website,
            String bio, String password) throws KwetterException {
        if (username == null || username.trim().isEmpty()) {
            throw new KwetterException("Invalid username");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new KwetterException("Invalid email");
        }

        if (website == null || website.trim().isEmpty()) {
            throw new KwetterException("Invalid website");
        }

        if (bio == null || bio.trim().isEmpty()) {
            throw new KwetterException("Invalid bio");
        }

        if (bio.length() > 160) {
            throw new KwetterException("Bio is too long, maximum amount of characters is 160.");
        }

        if (password == null || password.length() < 8) {
            throw new KwetterException("Password must be atleast 8 characters");
        }
    }
    
    private String changeUserToken(KwetterUser user) throws NoSuchAlgorithmException, KwetterException {
        user.setToken(new HashUtil().generateSalt());
        
        kwetterUserRepository.save(user);
        
        return user.getToken();
    }
}
