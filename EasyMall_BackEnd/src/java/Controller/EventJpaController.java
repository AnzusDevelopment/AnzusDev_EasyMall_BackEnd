/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Controller.exceptions.NonexistentEntityException;
import Controller.exceptions.PreexistingEntityException;
import Controller.exceptions.RollbackFailureException;
import Data.Entity.Event;
import Data.Entity.EventPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Data.Entity.Mall;
import Data.Entity.Favorite;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class EventJpaController implements Serializable {

    public EventJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Event event) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (event.getEventPK() == null) {
            event.setEventPK(new EventPK());
        }
        if (event.getFavoriteList() == null) {
            event.setFavoriteList(new ArrayList<Favorite>());
        }
        event.getEventPK().setMallId(event.getMall().getMallId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mall mall = event.getMall();
            if (mall != null) {
                mall = em.getReference(mall.getClass(), mall.getMallId());
                event.setMall(mall);
            }
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : event.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getFavoritePK());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            event.setFavoriteList(attachedFavoriteList);
            em.persist(event);
            if (mall != null) {
                mall.getEventList().add(event);
                mall = em.merge(mall);
            }
            for (Favorite favoriteListFavorite : event.getFavoriteList()) {
                Event oldEventIdOfFavoriteListFavorite = favoriteListFavorite.getEventId();
                favoriteListFavorite.setEventId(event);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldEventIdOfFavoriteListFavorite != null) {
                    oldEventIdOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldEventIdOfFavoriteListFavorite = em.merge(oldEventIdOfFavoriteListFavorite);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEvent(event.getEventPK()) != null) {
                throw new PreexistingEntityException("Event " + event + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Event event) throws NonexistentEntityException, RollbackFailureException, Exception {
        event.getEventPK().setMallId(event.getMall().getMallId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Event persistentEvent = em.find(Event.class, event.getEventPK());
            Mall mallOld = persistentEvent.getMall();
            Mall mallNew = event.getMall();
            List<Favorite> favoriteListOld = persistentEvent.getFavoriteList();
            List<Favorite> favoriteListNew = event.getFavoriteList();
            if (mallNew != null) {
                mallNew = em.getReference(mallNew.getClass(), mallNew.getMallId());
                event.setMall(mallNew);
            }
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getFavoritePK());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            event.setFavoriteList(favoriteListNew);
            event = em.merge(event);
            if (mallOld != null && !mallOld.equals(mallNew)) {
                mallOld.getEventList().remove(event);
                mallOld = em.merge(mallOld);
            }
            if (mallNew != null && !mallNew.equals(mallOld)) {
                mallNew.getEventList().add(event);
                mallNew = em.merge(mallNew);
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    favoriteListOldFavorite.setEventId(null);
                    favoriteListOldFavorite = em.merge(favoriteListOldFavorite);
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    Event oldEventIdOfFavoriteListNewFavorite = favoriteListNewFavorite.getEventId();
                    favoriteListNewFavorite.setEventId(event);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldEventIdOfFavoriteListNewFavorite != null && !oldEventIdOfFavoriteListNewFavorite.equals(event)) {
                        oldEventIdOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldEventIdOfFavoriteListNewFavorite = em.merge(oldEventIdOfFavoriteListNewFavorite);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                EventPK id = event.getEventPK();
                if (findEvent(id) == null) {
                    throw new NonexistentEntityException("The event with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(EventPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Event event;
            try {
                event = em.getReference(Event.class, id);
                event.getEventPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The event with id " + id + " no longer exists.", enfe);
            }
            Mall mall = event.getMall();
            if (mall != null) {
                mall.getEventList().remove(event);
                mall = em.merge(mall);
            }
            List<Favorite> favoriteList = event.getFavoriteList();
            for (Favorite favoriteListFavorite : favoriteList) {
                favoriteListFavorite.setEventId(null);
                favoriteListFavorite = em.merge(favoriteListFavorite);
            }
            em.remove(event);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Event> findEventEntities() {
        return findEventEntities(true, -1, -1);
    }

    public List<Event> findEventEntities(int maxResults, int firstResult) {
        return findEventEntities(false, maxResults, firstResult);
    }

    private List<Event> findEventEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Event.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Event findEvent(EventPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Event.class, id);
        } finally {
            em.close();
        }
    }

    public int getEventCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Event> rt = cq.from(Event.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
