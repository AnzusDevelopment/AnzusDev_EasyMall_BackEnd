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
public class ProductPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "Product_Id")
    private int productId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Store_Id")
    private int storeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Category_Id")
    private int categoryId;

    public ProductPK() {
    }

    public ProductPK(int productId, int storeId, int categoryId) {
        this.productId = productId;
        this.storeId = storeId;
        this.categoryId = categoryId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) productId;
        hash += (int) storeId;
        hash += (int) categoryId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductPK)) {
            return false;
        }
        ProductPK other = (ProductPK) object;
        if (this.productId != other.productId) {
            return false;
        }
        if (this.storeId != other.storeId) {
            return false;
        }
        if (this.categoryId != other.categoryId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.ProductPK[ productId=" + productId + ", storeId=" + storeId + ", categoryId=" + categoryId + " ]";
    }
    
}
