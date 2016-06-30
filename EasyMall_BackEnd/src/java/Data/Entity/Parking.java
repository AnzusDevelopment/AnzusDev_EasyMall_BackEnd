/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Entity;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Nirox
 */
@Entity
@Table(name = "parking", catalog = "easymall", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Parking.findAll", query = "SELECT p FROM Parking p"),
    @NamedQuery(name = "Parking.findByParkingId", query = "SELECT p FROM Parking p WHERE p.parkingPK.parkingId = :parkingId"),
    @NamedQuery(name = "Parking.findByMallId", query = "SELECT p FROM Parking p WHERE p.parkingPK.mallId = :mallId"),
    @NamedQuery(name = "Parking.findByParkingNumOfPlaces", query = "SELECT p FROM Parking p WHERE p.parkingNumOfPlaces = :parkingNumOfPlaces")})
public class Parking implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ParkingPK parkingPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Parking_NumOfPlaces")
    private double parkingNumOfPlaces;
    @JoinColumn(name = "Mall_Id", referencedColumnName = "Mall_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Mall mall;
    @OneToMany(mappedBy = "parkingId")
    private List<Favorite> favoriteList;

    public Parking() {
    }

    public Parking(ParkingPK parkingPK) {
        this.parkingPK = parkingPK;
    }

    public Parking(ParkingPK parkingPK, double parkingNumOfPlaces) {
        this.parkingPK = parkingPK;
        this.parkingNumOfPlaces = parkingNumOfPlaces;
    }

    public Parking(int parkingId, int mallId) {
        this.parkingPK = new ParkingPK(parkingId, mallId);
    }

    public ParkingPK getParkingPK() {
        return parkingPK;
    }

    public void setParkingPK(ParkingPK parkingPK) {
        this.parkingPK = parkingPK;
    }

    public double getParkingNumOfPlaces() {
        return parkingNumOfPlaces;
    }

    public void setParkingNumOfPlaces(double parkingNumOfPlaces) {
        this.parkingNumOfPlaces = parkingNumOfPlaces;
    }

    public Mall getMall() {
        return mall;
    }

    public void setMall(Mall mall) {
        this.mall = mall;
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
        hash += (parkingPK != null ? parkingPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Parking)) {
            return false;
        }
        Parking other = (Parking) object;
        if ((this.parkingPK == null && other.parkingPK != null) || (this.parkingPK != null && !this.parkingPK.equals(other.parkingPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.Parking[ parkingPK=" + parkingPK + " ]";
    }
    
}
