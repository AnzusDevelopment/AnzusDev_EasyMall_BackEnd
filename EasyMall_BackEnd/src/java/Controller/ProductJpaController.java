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
import Data.Entity.Product;
import Data.Entity.ProductPK;
import Data.Entity.Store;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class ProductJpaController implements Serializable {

    public ProductJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Product product) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (product.getProductPK() == null) {
            product.setProductPK(new ProductPK());
        }
        product.getProductPK().setCategoryId(product.getCategory().getCategoryId());
        product.getProductPK().setStoreId(product.getStore().getStorePK().getStoreId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Category category = product.getCategory();
            if (category != null) {
                category = em.getReference(category.getClass(), category.getCategoryId());
                product.setCategory(category);
            }
            Store store = product.getStore();
            if (store != null) {
                store = em.getReference(store.getClass(), store.getStorePK());
                product.setStore(store);
            }
            em.persist(product);
            if (category != null) {
                category.getProductList().add(product);
                category = em.merge(category);
            }
            if (store != null) {
                store.getProductList().add(product);
                store = em.merge(store);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findProduct(product.getProductPK()) != null) {
                throw new PreexistingEntityException("Product " + product + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Product product) throws NonexistentEntityException, RollbackFailureException, Exception {
        product.getProductPK().setCategoryId(product.getCategory().getCategoryId());
        product.getProductPK().setStoreId(product.getStore().getStorePK().getStoreId());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Product persistentProduct = em.find(Product.class, product.getProductPK());
            Category categoryOld = persistentProduct.getCategory();
            Category categoryNew = product.getCategory();
            Store storeOld = persistentProduct.getStore();
            Store storeNew = product.getStore();
            if (categoryNew != null) {
                categoryNew = em.getReference(categoryNew.getClass(), categoryNew.getCategoryId());
                product.setCategory(categoryNew);
            }
            if (storeNew != null) {
                storeNew = em.getReference(storeNew.getClass(), storeNew.getStorePK());
                product.setStore(storeNew);
            }
            product = em.merge(product);
            if (categoryOld != null && !categoryOld.equals(categoryNew)) {
                categoryOld.getProductList().remove(product);
                categoryOld = em.merge(categoryOld);
            }
            if (categoryNew != null && !categoryNew.equals(categoryOld)) {
                categoryNew.getProductList().add(product);
                categoryNew = em.merge(categoryNew);
            }
            if (storeOld != null && !storeOld.equals(storeNew)) {
                storeOld.getProductList().remove(product);
                storeOld = em.merge(storeOld);
            }
            if (storeNew != null && !storeNew.equals(storeOld)) {
                storeNew.getProductList().add(product);
                storeNew = em.merge(storeNew);
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
                ProductPK id = product.getProductPK();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ProductPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getProductPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            Category category = product.getCategory();
            if (category != null) {
                category.getProductList().remove(product);
                category = em.merge(category);
            }
            Store store = product.getStore();
            if (store != null) {
                store.getProductList().remove(product);
                store = em.merge(store);
            }
            em.remove(product);
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

    public List<Product> findProductEntities() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    public Product findProduct(ProductPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
