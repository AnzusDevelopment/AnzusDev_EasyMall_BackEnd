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
public class EventPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "Event_Id")
    private int eventId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Mall_Id")
    private int mallId;

    public EventPK() {
    }

    public EventPK(int eventId, int mallId) {
        this.eventId = eventId;
        this.mallId = mallId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
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
        hash += (int) eventId;
        hash += (int) mallId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventPK)) {
            return false;
        }
        EventPK other = (EventPK) object;
        if (this.eventId != other.eventId) {
            return false;
        }
        if (this.mallId != other.mallId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Data.Entity.EventPK[ eventId=" + eventId + ", mallId=" + mallId + " ]";
    }
    
}
