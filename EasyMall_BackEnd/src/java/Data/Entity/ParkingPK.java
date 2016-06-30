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
public class ParkingPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "Parking_Id")
    private int parkingId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Mall_Id")
    private int mallId;

    public ParkingPK() {
    }

    public ParkingPK(int parkingId, int mallId) {
        this.parkingId = parkingId;
        this.mallId = mallId;
    }

    public int getParkingId() {
        return parkingId;
    }

    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
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
        hash += (int) parkingId;
        hash += (int) mallId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParkingPK)) {
            return false;
        }
        ParkingPK other = (ParkingPK) object;
        if (this.parkingId != other.parkingId) {
            return false;
        }
        if (this.mallId != other.mallId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.ParkingPK[ parkingId=" + parkingId + ", mallId=" + mallId + " ]";
    }
    
}
