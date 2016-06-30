/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Controller.exceptions.IllegalOrphanException;
import Controller.exceptions.NonexistentEntityException;
import Controller.exceptions.PreexistingEntityException;
import Controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Data.Entity.Parking;
import java.util.ArrayList;
import java.util.List;
import Data.Entity.Store;
import Data.Entity.Event;
import Data.Entity.Favorite;
import Data.Entity.Mall;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class MallJpaController implements Serializable {

    public MallJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mall mall) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (mall.getParkingList() == null) {
            mall.setParkingList(new ArrayList<Parking>());
        }
        if (mall.getStoreList() == null) {
            mall.setStoreList(new ArrayList<Store>());
        }
        if (mall.getEventList() == null) {
            mall.setEventList(new ArrayList<Event>());
        }
        if (mall.getFavoriteList() == null) {
            mall.setFavoriteList(new ArrayList<Favorite>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Parking> attachedParkingList = new ArrayList<Parking>();
            for (Parking parkingListParkingToAttach : mall.getParkingList()) {
                parkingListParkingToAttach = em.getReference(parkingListParkingToAttach.getClass(), parkingListParkingToAttach.getParkingPK());
                attachedParkingList.add(parkingListParkingToAttach);
            }
            mall.setParkingList(attachedParkingList);
            List<Store> attachedStoreList = new ArrayList<Store>();
            for (Store storeListStoreToAttach : mall.getStoreList()) {
                storeListStoreToAttach = em.getReference(storeListStoreToAttach.getClass(), storeListStoreToAttach.getStorePK());
                attachedStoreList.add(storeListStoreToAttach);
            }
            mall.setStoreList(attachedStoreList);
            List<Event> attachedEventList = new ArrayList<Event>();
            for (Event eventListEventToAttach : mall.getEventList()) {
                eventListEventToAttach = em.getReference(eventListEventToAttach.getClass(), eventListEventToAttach.getEventPK());
                attachedEventList.add(eventListEventToAttach);
            }
            mall.setEventList(attachedEventList);
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : mall.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getFavoritePK());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            mall.setFavoriteList(attachedFavoriteList);
            em.persist(mall);
            for (Parking parkingListParking : mall.getParkingList()) {
                Mall oldMallOfParkingListParking = parkingListParking.getMall();
                parkingListParking.setMall(mall);
                parkingListParking = em.merge(parkingListParking);
                if (oldMallOfParkingListParking != null) {
                    oldMallOfParkingListParking.getParkingList().remove(parkingListParking);
                    oldMallOfParkingListParking = em.merge(oldMallOfParkingListParking);
                }
            }
            for (Store storeListStore : mall.getStoreList()) {
                Mall oldMallOfStoreListStore = storeListStore.getMall();
                storeListStore.setMall(mall);
                storeListStore = em.merge(storeListStore);
                if (oldMallOfStoreListStore != null) {
                    oldMallOfStoreListStore.getStoreList().remove(storeListStore);
                    oldMallOfStoreListStore = em.merge(oldMallOfStoreListStore);
                }
            }
            for (Event eventListEvent : mall.getEventList()) {
                Mall oldMallOfEventListEvent = eventListEvent.getMall();
                eventListEvent.setMall(mall);
                eventListEvent = em.merge(eventListEvent);
                if (oldMallOfEventListEvent != null) {
                    oldMallOfEventListEvent.getEventList().remove(eventListEvent);
                    oldMallOfEventListEvent = em.merge(oldMallOfEventListEvent);
                }
            }
            for (Favorite favoriteListFavorite : mall.getFavoriteList()) {
                Mall oldMallIdOfFavoriteListFavorite = favoriteListFavorite.getMallId();
                favoriteListFavorite.setMallId(mall);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldMallIdOfFavoriteListFavorite != null) {
                    oldMallIdOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldMallIdOfFavoriteListFavorite = em.merge(oldMallIdOfFavoriteListFavorite);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findMall(mall.getMallId()) != null) {
                throw new PreexistingEntityException("Mall " + mall + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mall mall) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mall persistentMall = em.find(Mall.class, mall.getMallId());
            List<Parking> parkingListOld = persistentMall.getParkingList();
            List<Parking> parkingListNew = mall.getParkingList();
            List<Store> storeListOld = persistentMall.getStoreList();
            List<Store> storeListNew = mall.getStoreList();
            List<Event> eventListOld = persistentMall.getEventList();
            List<Event> eventListNew = mall.getEventList();
            List<Favorite> favoriteListOld = persistentMall.getFavoriteList();
            List<Favorite> favoriteListNew = mall.getFavoriteList();
            List<String> illegalOrphanMessages = null;
            for (Parking parkingListOldParking : parkingListOld) {
                if (!parkingListNew.contains(parkingListOldParking)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Parking " + parkingListOldParking + " since its mall field is not nullable.");
                }
            }
            for (Store storeListOldStore : storeListOld) {
                if (!storeListNew.contains(storeListOldStore)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Store " + storeListOldStore + " since its mall field is not nullable.");
                }
            }
            for (Event eventListOldEvent : eventListOld) {
                if (!eventListNew.contains(eventListOldEvent)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Event " + eventListOldEvent + " since its mall field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Parking> attachedParkingListNew = new ArrayList<Parking>();
            for (Parking parkingListNewParkingToAttach : parkingListNew) {
                parkingListNewParkingToAttach = em.getReference(parkingListNewParkingToAttach.getClass(), parkingListNewParkingToAttach.getParkingPK());
                attachedParkingListNew.add(parkingListNewParkingToAttach);
            }
            parkingListNew = attachedParkingListNew;
            mall.setParkingList(parkingListNew);
            List<Store> attachedStoreListNew = new ArrayList<Store>();
            for (Store storeListNewStoreToAttach : storeListNew) {
                storeListNewStoreToAttach = em.getReference(storeListNewStoreToAttach.getClass(), storeListNewStoreToAttach.getStorePK());
                attachedStoreListNew.add(storeListNewStoreToAttach);
            }
            storeListNew = attachedStoreListNew;
            mall.setStoreList(storeListNew);
            List<Event> attachedEventListNew = new ArrayList<Event>();
            for (Event eventListNewEventToAttach : eventListNew) {
                eventListNewEventToAttach = em.getReference(eventListNewEventToAttach.getClass(), eventListNewEventToAttach.getEventPK());
                attachedEventListNew.add(eventListNewEventToAttach);
            }
            eventListNew = attachedEventListNew;
            mall.setEventList(eventListNew);
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getFavoritePK());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            mall.setFavoriteList(favoriteListNew);
            mall = em.merge(mall);
            for (Parking parkingListNewParking : parkingListNew) {
                if (!parkingListOld.contains(parkingListNewParking)) {
                    Mall oldMallOfParkingListNewParking = parkingListNewParking.getMall();
                    parkingListNewParking.setMall(mall);
                    parkingListNewParking = em.merge(parkingListNewParking);
                    if (oldMallOfParkingListNewParking != null && !oldMallOfParkingListNewParking.equals(mall)) {
                        oldMallOfParkingListNewParking.getParkingList().remove(parkingListNewParking);
                        oldMallOfParkingListNewParking = em.merge(oldMallOfParkingListNewParking);
                    }
                }
            }
            for (Store storeListNewStore : storeListNew) {
                if (!storeListOld.contains(storeListNewStore)) {
                    Mall oldMallOfStoreListNewStore = storeListNewStore.getMall();
                    storeListNewStore.setMall(mall);
                    storeListNewStore = em.merge(storeListNewStore);
                    if (oldMallOfStoreListNewStore != null && !oldMallOfStoreListNewStore.equals(mall)) {
                        oldMallOfStoreListNewStore.getStoreList().remove(storeListNewStore);
                        oldMallOfStoreListNewStore = em.merge(oldMallOfStoreListNewStore);
                    }
                }
            }
            for (Event eventListNewEvent : eventListNew) {
                if (!eventListOld.contains(eventListNewEvent)) {
                    Mall oldMallOfEventListNewEvent = eventListNewEvent.getMall();
                    eventListNewEvent.setMall(mall);
                    eventListNewEvent = em.merge(eventListNewEvent);
                    if (oldMallOfEventListNewEvent != null && !oldMallOfEventListNewEvent.equals(mall)) {
                        oldMallOfEventListNewEvent.getEventList().remove(eventListNewEvent);
                        oldMallOfEventListNewEvent = em.merge(oldMallOfEventListNewEvent);
                    }
                }
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    favoriteListOldFavorite.setMallId(null);
                    favoriteListOldFavorite = em.merge(favoriteListOldFavorite);
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    Mall oldMallIdOfFavoriteListNewFavorite = favoriteListNewFavorite.getMallId();
                    favoriteListNewFavorite.setMallId(mall);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldMallIdOfFavoriteListNewFavorite != null && !oldMallIdOfFavoriteListNewFavorite.equals(mall)) {
                        oldMallIdOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldMallIdOfFavoriteListNewFavorite = em.merge(oldMallIdOfFavoriteListNewFavorite);
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
                Integer id = mall.getMallId();
                if (findMall(id) == null) {
                    throw new NonexistentEntityException("The mall with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mall mall;
            try {
                mall = em.getReference(Mall.class, id);
                mall.getMallId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mall with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Parking> parkingListOrphanCheck = mall.getParkingList();
            for (Parking parkingListOrphanCheckParking : parkingListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Mall (" + mall + ") cannot be destroyed since the Parking " + parkingListOrphanCheckParking + " in its parkingList field has a non-nullable mall field.");
            }
            List<Store> storeListOrphanCheck = mall.getStoreList();
            for (Store storeListOrphanCheckStore : storeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Mall (" + mall + ") cannot be destroyed since the Store " + storeListOrphanCheckStore + " in its storeList field has a non-nullable mall field.");
            }
            List<Event> eventListOrphanCheck = mall.getEventList();
            for (Event eventListOrphanCheckEvent : eventListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Mall (" + mall + ") cannot be destroyed since the Event " + eventListOrphanCheckEvent + " in its eventList field has a non-nullable mall field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Favorite> favoriteList = mall.getFavoriteList();
            for (Favorite favoriteListFavorite : favoriteList) {
                favoriteListFavorite.setMallId(null);
                favoriteListFavorite = em.merge(favoriteListFavorite);
            }
            em.remove(mall);
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

    public List<Mall> findMallEntities() {
        return findMallEntities(true, -1, -1);
    }

    public List<Mall> findMallEntities(int maxResults, int firstResult) {
        return findMallEntities(false, maxResults, firstResult);
    }

    private List<Mall> findMallEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mall.class));
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

    public Mall findMall(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mall.class, id);
        } finally {
            em.close();
        }
    }

    public int getMallCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mall> rt = cq.from(Mall.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
