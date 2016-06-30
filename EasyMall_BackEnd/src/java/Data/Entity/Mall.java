/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Nirox
 */
@Entity
@Table(name = "mall", catalog = "easymall", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mall.findAll", query = "SELECT m FROM Mall m"),
    @NamedQuery(name = "Mall.findByMallId", query = "SELECT m FROM Mall m WHERE m.mallId = :mallId"),
    @NamedQuery(name = "Mall.findByMallName", query = "SELECT m FROM Mall m WHERE m.mallName = :mallName"),
    @NamedQuery(name = "Mall.findByMalladdress", query = "SELECT m FROM Mall m WHERE m.malladdress = :malladdress"),
    @NamedQuery(name = "Mall.findByMallOpenHour", query = "SELECT m FROM Mall m WHERE m.mallOpenHour = :mallOpenHour"),
    @NamedQuery(name = "Mall.findByMallCloseHour", query = "SELECT m FROM Mall m WHERE m.mallCloseHour = :mallCloseHour")})
public class Mall implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Mall_Id")
    private Integer mallId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Mall_Name")
    private String mallName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Mall_address")
    private String malladdress;
    @Column(name = "Mall_OpenHour")
    private Integer mallOpenHour;
    @Column(name = "Mall_CloseHour")
    private Integer mallCloseHour;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mall")
    private List<Parking> parkingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mall")
    private List<Store> storeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mall")
    private List<Event> eventList;
    @OneToMany(mappedBy = "mallId")
    private List<Favorite> favoriteList;

    public Mall() {
    }

    public Mall(Integer mallId) {
        this.mallId = mallId;
    }

    public Mall(Integer mallId, String mallName, String malladdress) {
        this.mallId = mallId;
        this.mallName = mallName;
        this.malladdress = malladdress;
    }

    public Integer getMallId() {
        return mallId;
    }

    public void setMallId(Integer mallId) {
        this.mallId = mallId;
    }

    public String getMallName() {
        return mallName;
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    public String getMalladdress() {
        return malladdress;
    }

    public void setMalladdress(String malladdress) {
        this.malladdress = malladdress;
    }

    public Integer getMallOpenHour() {
        return mallOpenHour;
    }

    public void setMallOpenHour(Integer mallOpenHour) {
        this.mallOpenHour = mallOpenHour;
    }

    public Integer getMallCloseHour() {
        return mallCloseHour;
    }

    public void setMallCloseHour(Integer mallCloseHour) {
        this.mallCloseHour = mallCloseHour;
    }

    @XmlTransient
    public List<Parking> getParkingList() {
        return parkingList;
    }

    public void setParkingList(List<Parking> parkingList) {
        this.parkingList = parkingList;
    }

    @XmlTransient
    public List<Store> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<Store> storeList) {
        this.storeList = storeList;
    }

    @XmlTransient
    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
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
        hash += (mallId != null ? mallId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mall)) {
            return false;
        }
        Mall other = (Mall) object;
        if ((this.mallId == null && other.mallId != null) || (this.mallId != null && !this.mallId.equals(other.mallId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.Mall[ mallId=" + mallId + " ]";
    }
    
}
