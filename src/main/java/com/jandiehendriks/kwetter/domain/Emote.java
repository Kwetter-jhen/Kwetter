package com.jandiehendriks.kwetter.domain;

import java.io.Serializable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Jandie
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="EMOTE_TYPE")
@Table(name="EMOTE")
public abstract class Emote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne
    private KwetterUser kwetterUser;

    public Emote() {
    }

    public Emote(String name, KwetterUser kwetterUser) {
        this.name = name;
        this.kwetterUser = kwetterUser;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KwetterUser getKwetterUser() {
        return kwetterUser;
    }

    public void setKwetterUser(KwetterUser kwetterUser) {
        this.kwetterUser = kwetterUser;
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
        if (!(object instanceof Emote)) {
            return false;
        }
        Emote other = (Emote) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.jandiehendriks.kwetter.domain.Emote[ id=" + id + " ]";
    }
    
}
