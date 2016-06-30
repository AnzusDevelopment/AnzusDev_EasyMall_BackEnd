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
import Data.Entity.Store;
import Data.Entity.Favorite;
import Data.Entity.Offer;
import Data.Entity.OfferPK;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class OfferJpaController implements Serializable {

    public OfferJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Offer offer) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (offer.getOfferPK() == null) {
            offer.setOfferPK(new OfferPK());
        }
        if (offer.getFavoriteList() == null) {
            offer.setFavoriteList(new ArrayList<Favorite>());
        }
        offer.getOfferPK().setStoreId(offer.getStore().getStorePK().getStoreId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Store store = offer.getStore();
            if (store != null) {
                store = em.getReference(store.getClass(), store.getStorePK());
                offer.setStore(store);
            }
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : offer.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getFavoritePK());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            offer.setFavoriteList(attachedFavoriteList);
            em.persist(offer);
            if (store != null) {
                store.getOfferList().add(offer);
                store = em.merge(store);
            }
            for (Favorite favoriteListFavorite : offer.getFavoriteList()) {
                Offer oldOfferIdOfFavoriteListFavorite = favoriteListFavorite.getOfferId();
                favoriteListFavorite.setOfferId(offer);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldOfferIdOfFavoriteListFavorite != null) {
                    oldOfferIdOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldOfferIdOfFavoriteListFavorite = em.merge(oldOfferIdOfFavoriteListFavorite);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findOffer(offer.getOfferPK()) != null) {
                throw new PreexistingEntityException("Offer " + offer + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Offer offer) throws NonexistentEntityException, RollbackFailureException, Exception {
        offer.getOfferPK().setStoreId(offer.getStore().getStorePK().getStoreId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Offer persistentOffer = em.find(Offer.class, offer.getOfferPK());
            Store storeOld = persistentOffer.getStore();
            Store storeNew = offer.getStore();
            List<Favorite> favoriteListOld = persistentOffer.getFavoriteList();
            List<Favorite> favoriteListNew = offer.getFavoriteList();
            if (storeNew != null) {
                storeNew = em.getReference(storeNew.getClass(), storeNew.getStorePK());
                offer.setStore(storeNew);
            }
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getFavoritePK());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            offer.setFavoriteList(favoriteListNew);
            offer = em.merge(offer);
            if (storeOld != null && !storeOld.equals(storeNew)) {
                storeOld.getOfferList().remove(offer);
                storeOld = em.merge(storeOld);
            }
            if (storeNew != null && !storeNew.equals(storeOld)) {
                storeNew.getOfferList().add(offer);
                storeNew = em.merge(storeNew);
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    favoriteListOldFavorite.setOfferId(null);
                    favoriteListOldFavorite = em.merge(favoriteListOldFavorite);
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    Offer oldOfferIdOfFavoriteListNewFavorite = favoriteListNewFavorite.getOfferId();
                    favoriteListNewFavorite.setOfferId(offer);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldOfferIdOfFavoriteListNewFavorite != null && !oldOfferIdOfFavoriteListNewFavorite.equals(offer)) {
                        oldOfferIdOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldOfferIdOfFavoriteListNewFavorite = em.merge(oldOfferIdOfFavoriteListNewFavorite);
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
                OfferPK id = offer.getOfferPK();
                if (findOffer(id) == null) {
                    throw new NonexistentEntityException("The offer with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(OfferPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Offer offer;
            try {
                offer = em.getReference(Offer.class, id);
                offer.getOfferPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The offer with id " + id + " no longer exists.", enfe);
            }
            Store store = offer.getStore();
            if (store != null) {
                store.getOfferList().remove(offer);
                store = em.merge(store);
            }
            List<Favorite> favoriteList = offer.getFavoriteList();
            for (Favorite favoriteListFavorite : favoriteList) {
                favoriteListFavorite.setOfferId(null);
                favoriteListFavorite = em.merge(favoriteListFavorite);
            }
            em.remove(offer);
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

    public List<Offer> findOfferEntities() {
        return findOfferEntities(true, -1, -1);
    }

    public List<Offer> findOfferEntities(int maxResults, int firstResult) {
        return findOfferEntities(false, maxResults, firstResult);
    }

    private List<Offer> findOfferEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Offer.class));
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

    public Offer findOffer(OfferPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Offer.class, id);
        } finally {
            em.close();
        }
    }

    public int getOfferCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Offer> rt = cq.from(Offer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
