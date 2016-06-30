/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Entity;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Nirox
 */
@Entity
@Table(name = "favorite", catalog = "easymall", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Favorite.findAll", query = "SELECT f FROM Favorite f"),
    @NamedQuery(name = "Favorite.findByFavoriteId", query = "SELECT f FROM Favorite f WHERE f.favoritePK.favoriteId = :favoriteId"),
    @NamedQuery(name = "Favorite.findByPersonId", query = "SELECT f FROM Favorite f WHERE f.favoritePK.personId = :personId")})
public class Favorite implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FavoritePK favoritePK;
    @JoinColumn(name = "Category_Id", referencedColumnName = "Category_Id")
    @ManyToOne
    private Category categoryId;
    @JoinColumn(name = "Event_Id", referencedColumnName = "Event_Id")
    @ManyToOne
    private Event eventId;
    @JoinColumn(name = "Mall_Id", referencedColumnName = "Mall_Id")
    @ManyToOne
    private Mall mallId;
    @JoinColumn(name = "Offer_Id", referencedColumnName = "Offer_Id")
    @ManyToOne
    private Offer offerId;
    @JoinColumn(name = "Parking_Id", referencedColumnName = "Parking_Id")
    @ManyToOne
    private Parking parkingId;
    @JoinColumn(name = "Person_Id", referencedColumnName = "Person_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Person person;
    @JoinColumn(name = "Store_Store_Id", referencedColumnName = "Store_Id")
    @ManyToOne
    private Store storeStoreId;

    public Favorite() {
    }

    public Favorite(FavoritePK favoritePK) {
        this.favoritePK = favoritePK;
    }

    public Favorite(int favoriteId, int personId) {
        this.favoritePK = new FavoritePK(favoriteId, personId);
    }

    public FavoritePK getFavoritePK() {
        return favoritePK;
    }

    public void setFavoritePK(FavoritePK favoritePK) {
        this.favoritePK = favoritePK;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event eventId) {
        this.eventId = eventId;
    }

    public Mall getMallId() {
        return mallId;
    }

    public void setMallId(Mall mallId) {
        this.mallId = mallId;
    }

    public Offer getOfferId() {
        return offerId;
    }

    public void setOfferId(Offer offerId) {
        this.offerId = offerId;
    }

    public Parking getParkingId() {
        return parkingId;
    }

    public void setParkingId(Parking parkingId) {
        this.parkingId = parkingId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Store getStoreStoreId() {
        return storeStoreId;
    }

    public void setStoreStoreId(Store storeStoreId) {
        this.storeStoreId = storeStoreId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (favoritePK != null ? favoritePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Favorite)) {
            return false;
        }
        Favorite other = (Favorite) object;
        if ((this.favoritePK == null && other.favoritePK != null) || (this.favoritePK != null && !this.favoritePK.equals(other.favoritePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.Favorite[ favoritePK=" + favoritePK + " ]";
    }
    
}
