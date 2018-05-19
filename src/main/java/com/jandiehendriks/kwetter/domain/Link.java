package com.jandiehendriks.kwetter.domain;

import java.io.Serializable;

/**
 *
 * @author Jandie
 */
public class Link implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String url;
    private String rel;
    
    public Link () {}

    public Link(String url, String rel) {
        this.url = url;
        this.rel = rel;
    }

    public String getUrl() {
        return url;
    }

    public String getRel() {
        return rel;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }
}
