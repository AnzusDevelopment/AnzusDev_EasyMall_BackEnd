/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Nirox
 */
@Embeddable
public class FavoritePK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "Favorite_Id")
    private int favoriteId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Person_Id")
    private int personId;

    public FavoritePK() {
    }

    public FavoritePK(int favoriteId, int personId) {
        this.favoriteId = favoriteId;
        this.personId = personId;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) favoriteId;
        hash += (int) personId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FavoritePK)) {
            return false;
        }
        FavoritePK other = (FavoritePK) object;
        if (this.favoriteId != other.favoriteId) {
            return false;
        }
        if (this.personId != other.personId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.FavoritePK[ favoriteId=" + favoriteId + ", personId=" + personId + " ]";
    }
    
}
