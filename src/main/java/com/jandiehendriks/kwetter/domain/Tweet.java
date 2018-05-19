package com.jandiehendriks.kwetter.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author Jandie
 */
@Entity
public class Tweet implements Serializable, Comparable<Tweet> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String text;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date creationDate;
    @ManyToOne
    private KwetterUser kwetterUser;
    private boolean reported;
    @OneToMany
    private List<Emote> emotes;
    @ManyToMany
    private List<KwetterUser> mentions;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Tweet> tweets;
    @ManyToMany
    private List<HashTag> hashTags;
    @ManyToOne
    @JsonBackReference
    private Tweet parent;
    @Transient
    @JsonProperty("_links")
    private List<Link> links;
    
    public Tweet() {
    }

    public Tweet(String text, Date creationDate) {
        this.text = text;
        this.creationDate = creationDate;
        this.kwetterUser = null;
        this.reported = false;
        this.emotes = new ArrayList<>();
        this.mentions = new ArrayList<>();
        this.tweets = new ArrayList<>();
        this.hashTags = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public KwetterUser getKwetterUser() {
        return kwetterUser;
    }

    public void setKwetterUser(KwetterUser kwetterUser) {
        this.kwetterUser = kwetterUser;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public List<Emote> getEmotes() {
        return emotes;
    }

    public void setEmotes(List<Emote> emotes) {
        this.emotes = emotes;
    }

    public List<KwetterUser> getMentions() {
        return mentions;
    }

    public void setMentions(List<KwetterUser> mentions) {
        this.mentions = mentions;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public List<HashTag> getHashTags() {
        return hashTags;
    }

    public void setHashTags(List<HashTag> hashTags) {
        this.hashTags = hashTags;
    }

    public Tweet getParent() {
        return parent;
    }

    public void setParent(Tweet parent) {
        this.parent = parent;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> _links) {
        this.links = _links;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tweet)) {
            return false;
        }
        Tweet other = (Tweet) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.jandiehendriks.kwetter.domain.Tweet[ id=" + id + " ]";
    }

    @Override
    public int compareTo(Tweet other) {
        if (this.getCreationDate().after(other.getCreationDate())) {
            return 1;
        } else if (this.getCreationDate().equals(other.getCreationDate())) {
            return 0;
        }
        
        return -1;
    }
}
