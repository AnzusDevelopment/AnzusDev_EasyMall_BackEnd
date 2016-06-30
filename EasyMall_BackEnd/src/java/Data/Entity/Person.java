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
@Table(name = "person", catalog = "easymall", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.findByPersonId", query = "SELECT p FROM Person p WHERE p.personId = :personId"),
    @NamedQuery(name = "Person.findByPersonNickName", query = "SELECT p FROM Person p WHERE p.personNickName = :personNickName"),
    @NamedQuery(name = "Person.findByPersonAge", query = "SELECT p FROM Person p WHERE p.personAge = :personAge"),
    @NamedQuery(name = "Person.findByPersonEmail", query = "SELECT p FROM Person p WHERE p.personEmail = :personEmail"),
    @NamedQuery(name = "Person.findByPersonInMall", query = "SELECT p FROM Person p WHERE p.personInMall = :personInMall"),
    @NamedQuery(name = "Person.findByPersonLocation", query = "SELECT p FROM Person p WHERE p.personLocation = :personLocation")})
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Person_Id")
    private Integer personId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Person_NickName")
    private String personNickName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Person_Age")
    private int personAge;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Person_Email")
    private String personEmail;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Person_InMall")
    private boolean personInMall;
    @Size(max = 45)
    @Column(name = "Person_Location")
    private String personLocation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private List<Favorite> favoriteList;

    public Person() {
    }

    public Person(Integer personId) {
        this.personId = personId;
    }

    public Person(Integer personId, String personNickName, int personAge, String personEmail, boolean personInMall) {
        this.personId = personId;
        this.personNickName = personNickName;
        this.personAge = personAge;
        this.personEmail = personEmail;
        this.personInMall = personInMall;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPersonNickName() {
        return personNickName;
    }

    public void setPersonNickName(String personNickName) {
        this.personNickName = personNickName;
    }

    public int getPersonAge() {
        return personAge;
    }

    public void setPersonAge(int personAge) {
        this.personAge = personAge;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public boolean getPersonInMall() {
        return personInMall;
    }

    public void setPersonInMall(boolean personInMall) {
        this.personInMall = personInMall;
    }

    public String getPersonLocation() {
        return personLocation;
    }

    public void setPersonLocation(String personLocation) {
        this.personLocation = personLocation;
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
        hash += (personId != null ? personId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        if ((this.personId == null && other.personId != null) || (this.personId != null && !this.personId.equals(other.personId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.Person[ personId=" + personId + " ]";
    }
    
}
