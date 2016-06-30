/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Controller.exceptions.NonexistentEntityException;
import Controller.exceptions.PreexistingEntityException;
import Controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Data.Entity.Category;
import Data.Entity.Event;
import Data.Entity.Favorite;
import Data.Entity.FavoritePK;
import Data.Entity.Mall;
import Data.Entity.Offer;
import Data.Entity.Parking;
import Data.Entity.Person;
import Data.Entity.Store;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class FavoriteJpaController implements Serializable {

    public FavoriteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Favorite favorite) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (favorite.getFavoritePK() == null) {
            favorite.setFavoritePK(new FavoritePK());
        }
        favorite.getFavoritePK().setPersonId(favorite.getPerson().getPersonId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Category categoryId = favorite.getCategoryId();
            if (categoryId != null) {
                categoryId = em.getReference(categoryId.getClass(), categoryId.getCategoryId());
                favorite.setCategoryId(categoryId);
            }
            Event eventId = favorite.getEventId();
            if (eventId != null) {
                eventId = em.getReference(eventId.getClass(), eventId.getEventPK());
                favorite.setEventId(eventId);
            }
            Mall mallId = favorite.getMallId();
            if (mallId != null) {
                mallId = em.getReference(mallId.getClass(), mallId.getMallId());
                favorite.setMallId(mallId);
            }
            Offer offerId = favorite.getOfferId();
            if (offerId != null) {
                offerId = em.getReference(offerId.getClass(), offerId.getOfferPK());
                favorite.setOfferId(offerId);
            }
            Parking parkingId = favorite.getParkingId();
            if (parkingId != null) {
                parkingId = em.getReference(parkingId.getClass(), parkingId.getParkingPK());
                favorite.setParkingId(parkingId);
            }
            Person person = favorite.getPerson();
            if (person != null) {
                person = em.getReference(person.getClass(), person.getPersonId());
                favorite.setPerson(person);
            }
            Store storeStoreId = favorite.getStoreStoreId();
            if (storeStoreId != null) {
                storeStoreId = em.getReference(storeStoreId.getClass(), storeStoreId.getStorePK());
                favorite.setStoreStoreId(storeStoreId);
            }
            em.persist(favorite);
            if (categoryId != null) {
                categoryId.getFavoriteList().add(favorite);
                categoryId = em.merge(categoryId);
            }
            if (eventId != null) {
                eventId.getFavoriteList().add(favorite);
                eventId = em.merge(eventId);
            }
            if (mallId != null) {
                mallId.getFavoriteList().add(favorite);
                mallId = em.merge(mallId);
            }
            if (offerId != null) {
                offerId.getFavoriteList().add(favorite);
                offerId = em.merge(offerId);
            }
            if (parkingId != null) {
                parkingId.getFavoriteList().add(favorite);
                parkingId = em.merge(parkingId);
            }
            if (person != null) {
                person.getFavoriteList().add(favorite);
                person = em.merge(person);
            }
            if (storeStoreId != null) {
                storeStoreId.getFavoriteList().add(favorite);
                storeStoreId = em.merge(storeStoreId);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findFavorite(favorite.getFavoritePK()) != null) {
                throw new PreexistingEntityException("Favorite " + favorite + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Favorite favorite) throws NonexistentEntityException, RollbackFailureException, Exception {
        favorite.getFavoritePK().setPersonId(favorite.getPerson().getPersonId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Favorite persistentFavorite = em.find(Favorite.class, favorite.getFavoritePK());
            Category categoryIdOld = persistentFavorite.getCategoryId();
            Category categoryIdNew = favorite.getCategoryId();
            Event eventIdOld = persistentFavorite.getEventId();
            Event eventIdNew = favorite.getEventId();
            Mall mallIdOld = persistentFavorite.getMallId();
            Mall mallIdNew = favorite.getMallId();
            Offer offerIdOld = persistentFavorite.getOfferId();
            Offer offerIdNew = favorite.getOfferId();
            Parking parkingIdOld = persistentFavorite.getParkingId();
            Parking parkingIdNew = favorite.getParkingId();
            Person personOld = persistentFavorite.getPerson();
            Person personNew = favorite.getPerson();
            Store storeStoreIdOld = persistentFavorite.getStoreStoreId();
            Store storeStoreIdNew = favorite.getStoreStoreId();
            if (categoryIdNew != null) {
                categoryIdNew = em.getReference(categoryIdNew.getClass(), categoryIdNew.getCategoryId());
                favorite.setCategoryId(categoryIdNew);
            }
            if (eventIdNew != null) {
                eventIdNew = em.getReference(eventIdNew.getClass(), eventIdNew.getEventPK());
                favorite.setEventId(eventIdNew);
            }
            if (mallIdNew != null) {
                mallIdNew = em.getReference(mallIdNew.getClass(), mallIdNew.getMallId());
                favorite.setMallId(mallIdNew);
            }
            if (offerIdNew != null) {
                offerIdNew = em.getReference(offerIdNew.getClass(), offerIdNew.getOfferPK());
                favorite.setOfferId(offerIdNew);
            }
            if (parkingIdNew != null) {
                parkingIdNew = em.getReference(parkingIdNew.getClass(), parkingIdNew.getParkingPK());
                favorite.setParkingId(parkingIdNew);
            }
            if (personNew != null) {
                personNew = em.getReference(personNew.getClass(), personNew.getPersonId());
                favorite.setPerson(personNew);
            }
            if (storeStoreIdNew != null) {
                storeStoreIdNew = em.getReference(storeStoreIdNew.getClass(), storeStoreIdNew.getStorePK());
                favorite.setStoreStoreId(storeStoreIdNew);
            }
            favorite = em.merge(favorite);
            if (categoryIdOld != null && !categoryIdOld.equals(categoryIdNew)) {
                categoryIdOld.getFavoriteList().remove(favorite);
                categoryIdOld = em.merge(categoryIdOld);
            }
            if (categoryIdNew != null && !categoryIdNew.equals(categoryIdOld)) {
                categoryIdNew.getFavoriteList().add(favorite);
                categoryIdNew = em.merge(categoryIdNew);
            }
            if (eventIdOld != null && !eventIdOld.equals(eventIdNew)) {
                eventIdOld.getFavoriteList().remove(favorite);
                eventIdOld = em.merge(eventIdOld);
            }
            if (eventIdNew != null && !eventIdNew.equals(eventIdOld)) {
                eventIdNew.getFavoriteList().add(favorite);
                eventIdNew = em.merge(eventIdNew);
            }
            if (mallIdOld != null && !mallIdOld.equals(mallIdNew)) {
                mallIdOld.getFavoriteList().remove(favorite);
                mallIdOld = em.merge(mallIdOld);
            }
            if (mallIdNew != null && !mallIdNew.equals(mallIdOld)) {
                mallIdNew.getFavoriteList().add(favorite);
                mallIdNew = em.merge(mallIdNew);
            }
            if (offerIdOld != null && !offerIdOld.equals(offerIdNew)) {
                offerIdOld.getFavoriteList().remove(favorite);
                offerIdOld = em.merge(offerIdOld);
            }
            if (offerIdNew != null && !offerIdNew.equals(offerIdOld)) {
                offerIdNew.getFavoriteList().add(favorite);
                offerIdNew = em.merge(offerIdNew);
            }
            if (parkingIdOld != null && !parkingIdOld.equals(parkingIdNew)) {
                parkingIdOld.getFavoriteList().remove(favorite);
                parkingIdOld = em.merge(parkingIdOld);
            }
            if (parkingIdNew != null && !parkingIdNew.equals(parkingIdOld)) {
                parkingIdNew.getFavoriteList().add(favorite);
                parkingIdNew = em.merge(parkingIdNew);
            }
            if (personOld != null && !personOld.equals(personNew)) {
                personOld.getFavoriteList().remove(favorite);
                personOld = em.merge(personOld);
            }
            if (personNew != null && !personNew.equals(personOld)) {
                personNew.getFavoriteList().add(favorite);
                personNew = em.merge(personNew);
            }
            if (storeStoreIdOld != null && !storeStoreIdOld.equals(storeStoreIdNew)) {
                storeStoreIdOld.getFavoriteList().remove(favorite);
                storeStoreIdOld = em.merge(storeStoreIdOld);
            }
            if (storeStoreIdNew != null && !storeStoreIdNew.equals(storeStoreIdOld)) {
                storeStoreIdNew.getFavoriteList().add(favorite);
                storeStoreIdNew = em.merge(storeStoreIdNew);
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
                FavoritePK id = favorite.getFavoritePK();
                if (findFavorite(id) == null) {
                    throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(FavoritePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Favorite favorite;
            try {
                favorite = em.getReference(Favorite.class, id);
                favorite.getFavoritePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The favorite with id " + id + " no longer exists.", enfe);
            }
            Category categoryId = favorite.getCategoryId();
            if (categoryId != null) {
                categoryId.getFavoriteList().remove(favorite);
                categoryId = em.merge(categoryId);
            }
            Event eventId = favorite.getEventId();
            if (eventId != null) {
                eventId.getFavoriteList().remove(favorite);
                eventId = em.merge(eventId);
            }
            Mall mallId = favorite.getMallId();
            if (mallId != null) {
                mallId.getFavoriteList().remove(favorite);
                mallId = em.merge(mallId);
            }
            Offer offerId = favorite.getOfferId();
            if (offerId != null) {
                offerId.getFavoriteList().remove(favorite);
                offerId = em.merge(offerId);
            }
            Parking parkingId = favorite.getParkingId();
            if (parkingId != null) {
                parkingId.getFavoriteList().remove(favorite);
                parkingId = em.merge(parkingId);
            }
            Person person = favorite.getPerson();
            if (person != null) {
                person.getFavoriteList().remove(favorite);
                person = em.merge(person);
            }
            Store storeStoreId = favorite.getStoreStoreId();
            if (storeStoreId != null) {
                storeStoreId.getFavoriteList().remove(favorite);
                storeStoreId = em.merge(storeStoreId);
            }
            em.remove(favorite);
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

    public List<Favorite> findFavoriteEntities() {
        return findFavoriteEntities(true, -1, -1);
    }

    public List<Favorite> findFavoriteEntities(int maxResults, int firstResult) {
        return findFavoriteEntities(false, maxResults, firstResult);
    }

    private List<Favorite> findFavoriteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Favorite.class));
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

    public Favorite findFavorite(FavoritePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Favorite.class, id);
        } finally {
            em.close();
        }
    }

    public int getFavoriteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Favorite> rt = cq.from(Favorite.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
