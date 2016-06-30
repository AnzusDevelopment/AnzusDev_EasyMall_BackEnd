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
import Data.Entity.Mall;
import Data.Entity.Offer;
import java.util.ArrayList;
import java.util.List;
import Data.Entity.Product;
import Data.Entity.Favorite;
import Data.Entity.Store;
import Data.Entity.StorePK;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class StoreJpaController implements Serializable {

    public StoreJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Store store) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (store.getStorePK() == null) {
            store.setStorePK(new StorePK());
        }
        if (store.getOfferList() == null) {
            store.setOfferList(new ArrayList<Offer>());
        }
        if (store.getProductList() == null) {
            store.setProductList(new ArrayList<Product>());
        }
        if (store.getFavoriteList() == null) {
            store.setFavoriteList(new ArrayList<Favorite>());
        }
        store.getStorePK().setMallId(store.getMall().getMallId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mall mall = store.getMall();
            if (mall != null) {
                mall = em.getReference(mall.getClass(), mall.getMallId());
                store.setMall(mall);
            }
            List<Offer> attachedOfferList = new ArrayList<Offer>();
            for (Offer offerListOfferToAttach : store.getOfferList()) {
                offerListOfferToAttach = em.getReference(offerListOfferToAttach.getClass(), offerListOfferToAttach.getOfferPK());
                attachedOfferList.add(offerListOfferToAttach);
            }
            store.setOfferList(attachedOfferList);
            List<Product> attachedProductList = new ArrayList<Product>();
            for (Product productListProductToAttach : store.getProductList()) {
                productListProductToAttach = em.getReference(productListProductToAttach.getClass(), productListProductToAttach.getProductPK());
                attachedProductList.add(productListProductToAttach);
            }
            store.setProductList(attachedProductList);
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : store.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getFavoritePK());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            store.setFavoriteList(attachedFavoriteList);
            em.persist(store);
            if (mall != null) {
                mall.getStoreList().add(store);
                mall = em.merge(mall);
            }
            for (Offer offerListOffer : store.getOfferList()) {
                Store oldStoreOfOfferListOffer = offerListOffer.getStore();
                offerListOffer.setStore(store);
                offerListOffer = em.merge(offerListOffer);
                if (oldStoreOfOfferListOffer != null) {
                    oldStoreOfOfferListOffer.getOfferList().remove(offerListOffer);
                    oldStoreOfOfferListOffer = em.merge(oldStoreOfOfferListOffer);
                }
            }
            for (Product productListProduct : store.getProductList()) {
                Store oldStoreOfProductListProduct = productListProduct.getStore();
                productListProduct.setStore(store);
                productListProduct = em.merge(productListProduct);
                if (oldStoreOfProductListProduct != null) {
                    oldStoreOfProductListProduct.getProductList().remove(productListProduct);
                    oldStoreOfProductListProduct = em.merge(oldStoreOfProductListProduct);
                }
            }
            for (Favorite favoriteListFavorite : store.getFavoriteList()) {
                Store oldStoreStoreIdOfFavoriteListFavorite = favoriteListFavorite.getStoreStoreId();
                favoriteListFavorite.setStoreStoreId(store);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldStoreStoreIdOfFavoriteListFavorite != null) {
                    oldStoreStoreIdOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldStoreStoreIdOfFavoriteListFavorite = em.merge(oldStoreStoreIdOfFavoriteListFavorite);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findStore(store.getStorePK()) != null) {
                throw new PreexistingEntityException("Store " + store + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Store store) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        store.getStorePK().setMallId(store.getMall().getMallId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Store persistentStore = em.find(Store.class, store.getStorePK());
            Mall mallOld = persistentStore.getMall();
            Mall mallNew = store.getMall();
            List<Offer> offerListOld = persistentStore.getOfferList();
            List<Offer> offerListNew = store.getOfferList();
            List<Product> productListOld = persistentStore.getProductList();
            List<Product> productListNew = store.getProductList();
            List<Favorite> favoriteListOld = persistentStore.getFavoriteList();
            List<Favorite> favoriteListNew = store.getFavoriteList();
            List<String> illegalOrphanMessages = null;
            for (Offer offerListOldOffer : offerListOld) {
                if (!offerListNew.contains(offerListOldOffer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Offer " + offerListOldOffer + " since its store field is not nullable.");
                }
            }
            for (Product productListOldProduct : productListOld) {
                if (!productListNew.contains(productListOldProduct)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Product " + productListOldProduct + " since its store field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (mallNew != null) {
                mallNew = em.getReference(mallNew.getClass(), mallNew.getMallId());
                store.setMall(mallNew);
            }
            List<Offer> attachedOfferListNew = new ArrayList<Offer>();
            for (Offer offerListNewOfferToAttach : offerListNew) {
                offerListNewOfferToAttach = em.getReference(offerListNewOfferToAttach.getClass(), offerListNewOfferToAttach.getOfferPK());
                attachedOfferListNew.add(offerListNewOfferToAttach);
            }
            offerListNew = attachedOfferListNew;
            store.setOfferList(offerListNew);
            List<Product> attachedProductListNew = new ArrayList<Product>();
            for (Product productListNewProductToAttach : productListNew) {
                productListNewProductToAttach = em.getReference(productListNewProductToAttach.getClass(), productListNewProductToAttach.getProductPK());
                attachedProductListNew.add(productListNewProductToAttach);
            }
            productListNew = attachedProductListNew;
            store.setProductList(productListNew);
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getFavoritePK());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            store.setFavoriteList(favoriteListNew);
            store = em.merge(store);
            if (mallOld != null && !mallOld.equals(mallNew)) {
                mallOld.getStoreList().remove(store);
                mallOld = em.merge(mallOld);
            }
            if (mallNew != null && !mallNew.equals(mallOld)) {
                mallNew.getStoreList().add(store);
                mallNew = em.merge(mallNew);
            }
            for (Offer offerListNewOffer : offerListNew) {
                if (!offerListOld.contains(offerListNewOffer)) {
                    Store oldStoreOfOfferListNewOffer = offerListNewOffer.getStore();
                    offerListNewOffer.setStore(store);
                    offerListNewOffer = em.merge(offerListNewOffer);
                    if (oldStoreOfOfferListNewOffer != null && !oldStoreOfOfferListNewOffer.equals(store)) {
                        oldStoreOfOfferListNewOffer.getOfferList().remove(offerListNewOffer);
                        oldStoreOfOfferListNewOffer = em.merge(oldStoreOfOfferListNewOffer);
                    }
                }
            }
            for (Product productListNewProduct : productListNew) {
                if (!productListOld.contains(productListNewProduct)) {
                    Store oldStoreOfProductListNewProduct = productListNewProduct.getStore();
                    productListNewProduct.setStore(store);
                    productListNewProduct = em.merge(productListNewProduct);
                    if (oldStoreOfProductListNewProduct != null && !oldStoreOfProductListNewProduct.equals(store)) {
                        oldStoreOfProductListNewProduct.getProductList().remove(productListNewProduct);
                        oldStoreOfProductListNewProduct = em.merge(oldStoreOfProductListNewProduct);
                    }
                }
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    favoriteListOldFavorite.setStoreStoreId(null);
                    favoriteListOldFavorite = em.merge(favoriteListOldFavorite);
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    Store oldStoreStoreIdOfFavoriteListNewFavorite = favoriteListNewFavorite.getStoreStoreId();
                    favoriteListNewFavorite.setStoreStoreId(store);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldStoreStoreIdOfFavoriteListNewFavorite != null && !oldStoreStoreIdOfFavoriteListNewFavorite.equals(store)) {
                        oldStoreStoreIdOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldStoreStoreIdOfFavoriteListNewFavorite = em.merge(oldStoreStoreIdOfFavoriteListNewFavorite);
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
                StorePK id = store.getStorePK();
                if (findStore(id) == null) {
                    throw new NonexistentEntityException("The store with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(StorePK id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Store store;
            try {
                store = em.getReference(Store.class, id);
                store.getStorePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The store with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Offer> offerListOrphanCheck = store.getOfferList();
            for (Offer offerListOrphanCheckOffer : offerListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Store (" + store + ") cannot be destroyed since the Offer " + offerListOrphanCheckOffer + " in its offerList field has a non-nullable store field.");
            }
            List<Product> productListOrphanCheck = store.getProductList();
            for (Product productListOrphanCheckProduct : productListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Store (" + store + ") cannot be destroyed since the Product " + productListOrphanCheckProduct + " in its productList field has a non-nullable store field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Mall mall = store.getMall();
            if (mall != null) {
                mall.getStoreList().remove(store);
                mall = em.merge(mall);
            }
            List<Favorite> favoriteList = store.getFavoriteList();
            for (Favorite favoriteListFavorite : favoriteList) {
                favoriteListFavorite.setStoreStoreId(null);
                favoriteListFavorite = em.merge(favoriteListFavorite);
            }
            em.remove(store);
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

    public List<Store> findStoreEntities() {
        return findStoreEntities(true, -1, -1);
    }

    public List<Store> findStoreEntities(int maxResults, int firstResult) {
        return findStoreEntities(false, maxResults, firstResult);
    }

    private List<Store> findStoreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Store.class));
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

    public Store findStore(StorePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Store.class, id);
        } finally {
            em.close();
        }
    }

    public int getStoreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Store> rt = cq.from(Store.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
