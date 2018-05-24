package com.jandiehendriks.kwetter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
/**
 *
 * @author Jandie
 */
@Entity
public class KwetterUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique = true)
    private String username;
    private String website;
    private String bio;
    @Column(unique = true)
    private String email;
    @OneToMany
    @JoinTable(name = "following")
    @JsonIgnore
    private List<KwetterUser> following;
    @OneToMany
    @JoinTable(name = "followers")
    @JsonIgnore
    private List<KwetterUser> followers;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private String hashedPassword;
    @JsonIgnore
    private String salt;
    @ManyToMany(mappedBy = "mentions")
    @JsonIgnore
    private List<Tweet> mentionedTweets;
    @OneToMany(mappedBy = "kwetterUser", 
            cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Tweet> tweets;
    @Transient
    @JsonProperty("_links")
    private List<Link> links;
    @JsonIgnore
    private String billingId;
    
    public KwetterUser() {
    }

    public KwetterUser(String username, String email, String website, String bio, 
            String hashedPassword, String salt, String token) {
        this.username = username;
        this.email = email;
        this.website = website;
        this.bio = bio;
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.userType = UserType.NORMAL;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.token = token;
        this.tweets = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<KwetterUser> getFollowing() {
        return following;
    }

    public void setFollowing(List<KwetterUser> following) {
        this.following = following;
    }

    public List<KwetterUser> getFollowers() {
        return followers;
    }

    public void setFollowers(List<KwetterUser> followers) {
        this.followers = followers;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Tweet> getMentionedTweets() {
        return mentionedTweets;
    }

    public void setMentionedTweets(List<Tweet> mentionedTweets) {
        this.mentionedTweets = mentionedTweets;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getBillingId() {
        return billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.jandiehendriks.kwetter.domain.KwetterUser[ id=" + id + " ]";
    }
    
    public boolean hasFollowing(String username) {
        
        for(KwetterUser user : following) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        
        return false;
    }
    
    public List<Tweet> getAmountOfTweets(int amount) {
        List<Tweet> foundTweets = new ArrayList<>();
        
        if (tweets.size() < amount) {
            foundTweets.addAll(tweets);
            Collections.reverse(foundTweets);
            
            return foundTweets;
        }
        
        for(int i = 0; i < amount; i++) {
            foundTweets.add(this.tweets.get(tweets.size() - i - 1));
        }
        
        return foundTweets;
    }
}
