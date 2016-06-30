/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Nirox
 */
@Entity
@Table(name = "offer", catalog = "easymall", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Offer.findAll", query = "SELECT o FROM Offer o"),
    @NamedQuery(name = "Offer.findByOfferId", query = "SELECT o FROM Offer o WHERE o.offerPK.offerId = :offerId"),
    @NamedQuery(name = "Offer.findByStoreId", query = "SELECT o FROM Offer o WHERE o.offerPK.storeId = :storeId"),
    @NamedQuery(name = "Offer.findByOfferDescription", query = "SELECT o FROM Offer o WHERE o.offerDescription = :offerDescription"),
    @NamedQuery(name = "Offer.findByOfferStartTime", query = "SELECT o FROM Offer o WHERE o.offerStartTime = :offerStartTime"),
    @NamedQuery(name = "Offer.findByOfferEndTime", query = "SELECT o FROM Offer o WHERE o.offerEndTime = :offerEndTime")})
public class Offer implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OfferPK offerPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Offer_Description")
    private String offerDescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Offer_StartTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date offerStartTime;
    @Column(name = "Offer_EndTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date offerEndTime;
    @JoinColumn(name = "Store_Id", referencedColumnName = "Store_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Store store;
    @OneToMany(mappedBy = "offerId")
    private List<Favorite> favoriteList;

    public Offer() {
    }

    public Offer(OfferPK offerPK) {
        this.offerPK = offerPK;
    }

    public Offer(OfferPK offerPK, String offerDescription, Date offerStartTime) {
        this.offerPK = offerPK;
        this.offerDescription = offerDescription;
        this.offerStartTime = offerStartTime;
    }

    public Offer(int offerId, int storeId) {
        this.offerPK = new OfferPK(offerId, storeId);
    }

    public OfferPK getOfferPK() {
        return offerPK;
    }

    public void setOfferPK(OfferPK offerPK) {
        this.offerPK = offerPK;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public Date getOfferStartTime() {
        return offerStartTime;
    }

    public void setOfferStartTime(Date offerStartTime) {
        this.offerStartTime = offerStartTime;
    }

    public Date getOfferEndTime() {
        return offerEndTime;
    }

    public void setOfferEndTime(Date offerEndTime) {
        this.offerEndTime = offerEndTime;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @XmlTransient
    public List<Favorite> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(List<Favorite> favoriteList) {
        this.favoriteList = favoriteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (offerPK != null ? offerPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Offer)) {
            return false;
        }
        Offer other = (Offer) object;
        if ((this.offerPK == null && other.offerPK != null) || (this.offerPK != null && !this.offerPK.equals(other.offerPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.Offer[ offerPK=" + offerPK + " ]";
    }
    
}
