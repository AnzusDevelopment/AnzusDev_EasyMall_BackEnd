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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "store", catalog = "easymall", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Store.findAll", query = "SELECT s FROM Store s"),
    @NamedQuery(name = "Store.findByStoreId", query = "SELECT s FROM Store s WHERE s.storePK.storeId = :storeId"),
    @NamedQuery(name = "Store.findByMallId", query = "SELECT s FROM Store s WHERE s.storePK.mallId = :mallId"),
    @NamedQuery(name = "Store.findByStoreName", query = "SELECT s FROM Store s WHERE s.storeName = :storeName"),
    @NamedQuery(name = "Store.findByStoreNum", query = "SELECT s FROM Store s WHERE s.storeNum = :storeNum"),
    @NamedQuery(name = "Store.findByStoreSuscription", query = "SELECT s FROM Store s WHERE s.storeSuscription = :storeSuscription")})
public class Store implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StorePK storePK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Store_Name")
    private String storeName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Store_Num")
    private double storeNum;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Store_Suscription")
    private int storeSuscription;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "store")
    private List<Offer> offerList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "store")
    private List<Product> productList;
    @JoinColumn(name = "Mall_Id", referencedColumnName = "Mall_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Mall mall;
    @OneToMany(mappedBy = "storeStoreId")
    private List<Favorite> favoriteList;

    public Store() {
    }

    public Store(StorePK storePK) {
        this.storePK = storePK;
    }

    public Store(StorePK storePK, String storeName, double storeNum, int storeSuscription) {
        this.storePK = storePK;
        this.storeName = storeName;
        this.storeNum = storeNum;
        this.storeSuscription = storeSuscription;
    }

    public Store(int storeId, int mallId) {
        this.storePK = new StorePK(storeId, mallId);
    }

    public StorePK getStorePK() {
        return storePK;
    }

    public void setStorePK(StorePK storePK) {
        this.storePK = storePK;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public double getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(double storeNum) {
        this.storeNum = storeNum;
    }

    public int getStoreSuscription() {
        return storeSuscription;
    }

    public void setStoreSuscription(int storeSuscription) {
        this.storeSuscription = storeSuscription;
    }

    @XmlTransient
    public List<Offer> getOfferList() {
        return offerList;
    }

    public void setOfferList(List<Offer> offerList) {
        this.offerList = offerList;
    }

    @XmlTransient
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
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
        hash += (storePK != null ? storePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Store)) {
            return false;
        }
        Store other = (Store) object;
        if ((this.storePK == null && other.storePK != null) || (this.storePK != null && !this.storePK.equals(other.storePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.Store[ storePK=" + storePK + " ]";
    }
    
}
