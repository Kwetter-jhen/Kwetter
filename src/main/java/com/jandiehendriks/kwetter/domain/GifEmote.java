package com.jandiehendriks.kwetter.domain;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author Jandie
 */
@Entity
@DiscriminatorValue("GIF")
public class GifEmote extends Emote implements Serializable {

    private static final long serialVersionUID = 1L;
    private String url;

    public GifEmote() {
        super();
    }

    public GifEmote(String url, String name, KwetterUser kwetterUser) {
        super(name, kwetterUser);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
