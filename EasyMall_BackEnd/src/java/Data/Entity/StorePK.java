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
public class StorePK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "Store_Id")
    private int storeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Mall_Id")
    private int mallId;

    public StorePK() {
    }

    public StorePK(int storeId, int mallId) {
        this.storeId = storeId;
        this.mallId = mallId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getMallId() {
        return mallId;
    }

    public void setMallId(int mallId) {
        this.mallId = mallId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) storeId;
        hash += (int) mallId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StorePK)) {
            return false;
        }
        StorePK other = (StorePK) object;
        if (this.storeId != other.storeId) {
            return false;
        }
        if (this.mallId != other.mallId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.StorePK[ storeId=" + storeId + ", mallId=" + mallId + " ]";
    }
    
}
