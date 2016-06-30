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
public class OfferPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "Offer_Id")
    private int offerId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Store_Id")
    private int storeId;

    public OfferPK() {
    }

    public OfferPK(int offerId, int storeId) {
        this.offerId = offerId;
        this.storeId = storeId;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) offerId;
        hash += (int) storeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OfferPK)) {
            return false;
        }
        OfferPK other = (OfferPK) object;
        if (this.offerId != other.offerId) {
            return false;
        }
        if (this.storeId != other.storeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.OfferPK[ offerId=" + offerId + ", storeId=" + storeId + " ]";
    }
    
}
