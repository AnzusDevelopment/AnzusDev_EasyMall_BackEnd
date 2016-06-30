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
import Data.Entity.Mall;
import Data.Entity.Favorite;
import Data.Entity.Parking;
import Data.Entity.ParkingPK;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class ParkingJpaController implements Serializable {

    public ParkingJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Parking parking) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (parking.getParkingPK() == null) {
            parking.setParkingPK(new ParkingPK());
        }
        if (parking.getFavoriteList() == null) {
            parking.setFavoriteList(new ArrayList<Favorite>());
        }
        parking.getParkingPK().setMallId(parking.getMall().getMallId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mall mall = parking.getMall();
            if (mall != null) {
                mall = em.getReference(mall.getClass(), mall.getMallId());
                parking.setMall(mall);
            }
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : parking.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getFavoritePK());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            parking.setFavoriteList(attachedFavoriteList);
            em.persist(parking);
            if (mall != null) {
                mall.getParkingList().add(parking);
                mall = em.merge(mall);
            }
            for (Favorite favoriteListFavorite : parking.getFavoriteList()) {
                Parking oldParkingIdOfFavoriteListFavorite = favoriteListFavorite.getParkingId();
                favoriteListFavorite.setParkingId(parking);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldParkingIdOfFavoriteListFavorite != null) {
                    oldParkingIdOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldParkingIdOfFavoriteListFavorite = em.merge(oldParkingIdOfFavoriteListFavorite);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findParking(parking.getParkingPK()) != null) {
                throw new PreexistingEntityException("Parking " + parking + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Parking parking) throws NonexistentEntityException, RollbackFailureException, Exception {
        parking.getParkingPK().setMallId(parking.getMall().getMallId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Parking persistentParking = em.find(Parking.class, parking.getParkingPK());
            Mall mallOld = persistentParking.getMall();
            Mall mallNew = parking.getMall();
            List<Favorite> favoriteListOld = persistentParking.getFavoriteList();
            List<Favorite> favoriteListNew = parking.getFavoriteList();
            if (mallNew != null) {
                mallNew = em.getReference(mallNew.getClass(), mallNew.getMallId());
                parking.setMall(mallNew);
            }
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getFavoritePK());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            parking.setFavoriteList(favoriteListNew);
            parking = em.merge(parking);
            if (mallOld != null && !mallOld.equals(mallNew)) {
                mallOld.getParkingList().remove(parking);
                mallOld = em.merge(mallOld);
            }
            if (mallNew != null && !mallNew.equals(mallOld)) {
                mallNew.getParkingList().add(parking);
                mallNew = em.merge(mallNew);
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    favoriteListOldFavorite.setParkingId(null);
                    favoriteListOldFavorite = em.merge(favoriteListOldFavorite);
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    Parking oldParkingIdOfFavoriteListNewFavorite = favoriteListNewFavorite.getParkingId();
                    favoriteListNewFavorite.setParkingId(parking);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldParkingIdOfFavoriteListNewFavorite != null && !oldParkingIdOfFavoriteListNewFavorite.equals(parking)) {
                        oldParkingIdOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldParkingIdOfFavoriteListNewFavorite = em.merge(oldParkingIdOfFavoriteListNewFavorite);
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
                ParkingPK id = parking.getParkingPK();
                if (findParking(id) == null) {
                    throw new NonexistentEntityException("The parking with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ParkingPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Parking parking;
            try {
                parking = em.getReference(Parking.class, id);
                parking.getParkingPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The parking with id " + id + " no longer exists.", enfe);
            }
            Mall mall = parking.getMall();
            if (mall != null) {
                mall.getParkingList().remove(parking);
                mall = em.merge(mall);
            }
            List<Favorite> favoriteList = parking.getFavoriteList();
            for (Favorite favoriteListFavorite : favoriteList) {
                favoriteListFavorite.setParkingId(null);
                favoriteListFavorite = em.merge(favoriteListFavorite);
            }
            em.remove(parking);
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

    public List<Parking> findParkingEntities() {
        return findParkingEntities(true, -1, -1);
    }

    public List<Parking> findParkingEntities(int maxResults, int firstResult) {
        return findParkingEntities(false, maxResults, firstResult);
    }

    private List<Parking> findParkingEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Parking.class));
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

    public Parking findParking(ParkingPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Parking.class, id);
        } finally {
            em.close();
        }
    }

    public int getParkingCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Parking> rt = cq.from(Parking.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
