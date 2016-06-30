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
import Data.Entity.Category;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Data.Entity.Product;
import java.util.ArrayList;
import java.util.List;
import Data.Entity.Favorite;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Nirox
 */
public class CategoryJpaController implements Serializable {

    public CategoryJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Category category) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (category.getProductList() == null) {
            category.setProductList(new ArrayList<Product>());
        }
        if (category.getFavoriteList() == null) {
            category.setFavoriteList(new ArrayList<Favorite>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Product> attachedProductList = new ArrayList<Product>();
            for (Product productListProductToAttach : category.getProductList()) {
                productListProductToAttach = em.getReference(productListProductToAttach.getClass(), productListProductToAttach.getProductPK());
                attachedProductList.add(productListProductToAttach);
            }
            category.setProductList(attachedProductList);
            List<Favorite> attachedFavoriteList = new ArrayList<Favorite>();
            for (Favorite favoriteListFavoriteToAttach : category.getFavoriteList()) {
                favoriteListFavoriteToAttach = em.getReference(favoriteListFavoriteToAttach.getClass(), favoriteListFavoriteToAttach.getFavoritePK());
                attachedFavoriteList.add(favoriteListFavoriteToAttach);
            }
            category.setFavoriteList(attachedFavoriteList);
            em.persist(category);
            for (Product productListProduct : category.getProductList()) {
                Category oldCategoryOfProductListProduct = productListProduct.getCategory();
                productListProduct.setCategory(category);
                productListProduct = em.merge(productListProduct);
                if (oldCategoryOfProductListProduct != null) {
                    oldCategoryOfProductListProduct.getProductList().remove(productListProduct);
                    oldCategoryOfProductListProduct = em.merge(oldCategoryOfProductListProduct);
                }
            }
            for (Favorite favoriteListFavorite : category.getFavoriteList()) {
                Category oldCategoryIdOfFavoriteListFavorite = favoriteListFavorite.getCategoryId();
                favoriteListFavorite.setCategoryId(category);
                favoriteListFavorite = em.merge(favoriteListFavorite);
                if (oldCategoryIdOfFavoriteListFavorite != null) {
                    oldCategoryIdOfFavoriteListFavorite.getFavoriteList().remove(favoriteListFavorite);
                    oldCategoryIdOfFavoriteListFavorite = em.merge(oldCategoryIdOfFavoriteListFavorite);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCategory(category.getCategoryId()) != null) {
                throw new PreexistingEntityException("Category " + category + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Category category) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Category persistentCategory = em.find(Category.class, category.getCategoryId());
            List<Product> productListOld = persistentCategory.getProductList();
            List<Product> productListNew = category.getProductList();
            List<Favorite> favoriteListOld = persistentCategory.getFavoriteList();
            List<Favorite> favoriteListNew = category.getFavoriteList();
            List<String> illegalOrphanMessages = null;
            for (Product productListOldProduct : productListOld) {
                if (!productListNew.contains(productListOldProduct)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Product " + productListOldProduct + " since its category field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Product> attachedProductListNew = new ArrayList<Product>();
            for (Product productListNewProductToAttach : productListNew) {
                productListNewProductToAttach = em.getReference(productListNewProductToAttach.getClass(), productListNewProductToAttach.getProductPK());
                attachedProductListNew.add(productListNewProductToAttach);
            }
            productListNew = attachedProductListNew;
            category.setProductList(productListNew);
            List<Favorite> attachedFavoriteListNew = new ArrayList<Favorite>();
            for (Favorite favoriteListNewFavoriteToAttach : favoriteListNew) {
                favoriteListNewFavoriteToAttach = em.getReference(favoriteListNewFavoriteToAttach.getClass(), favoriteListNewFavoriteToAttach.getFavoritePK());
                attachedFavoriteListNew.add(favoriteListNewFavoriteToAttach);
            }
            favoriteListNew = attachedFavoriteListNew;
            category.setFavoriteList(favoriteListNew);
            category = em.merge(category);
            for (Product productListNewProduct : productListNew) {
                if (!productListOld.contains(productListNewProduct)) {
                    Category oldCategoryOfProductListNewProduct = productListNewProduct.getCategory();
                    productListNewProduct.setCategory(category);
                    productListNewProduct = em.merge(productListNewProduct);
                    if (oldCategoryOfProductListNewProduct != null && !oldCategoryOfProductListNewProduct.equals(category)) {
                        oldCategoryOfProductListNewProduct.getProductList().remove(productListNewProduct);
                        oldCategoryOfProductListNewProduct = em.merge(oldCategoryOfProductListNewProduct);
                    }
                }
            }
            for (Favorite favoriteListOldFavorite : favoriteListOld) {
                if (!favoriteListNew.contains(favoriteListOldFavorite)) {
                    favoriteListOldFavorite.setCategoryId(null);
                    favoriteListOldFavorite = em.merge(favoriteListOldFavorite);
                }
            }
            for (Favorite favoriteListNewFavorite : favoriteListNew) {
                if (!favoriteListOld.contains(favoriteListNewFavorite)) {
                    Category oldCategoryIdOfFavoriteListNewFavorite = favoriteListNewFavorite.getCategoryId();
                    favoriteListNewFavorite.setCategoryId(category);
                    favoriteListNewFavorite = em.merge(favoriteListNewFavorite);
                    if (oldCategoryIdOfFavoriteListNewFavorite != null && !oldCategoryIdOfFavoriteListNewFavorite.equals(category)) {
                        oldCategoryIdOfFavoriteListNewFavorite.getFavoriteList().remove(favoriteListNewFavorite);
                        oldCategoryIdOfFavoriteListNewFavorite = em.merge(oldCategoryIdOfFavoriteListNewFavorite);
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
                Integer id = category.getCategoryId();
                if (findCategory(id) == null) {
                    throw new NonexistentEntityException("The category with id " + id + " no longer exists.");
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
            Category category;
            try {
                category = em.getReference(Category.class, id);
                category.getCategoryId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The category with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Product> productListOrphanCheck = category.getProductList();
            for (Product productListOrphanCheckProduct : productListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Category (" + category + ") cannot be destroyed since the Product " + productListOrphanCheckProduct + " in its productList field has a non-nullable category field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Favorite> favoriteList = category.getFavoriteList();
            for (Favorite favoriteListFavorite : favoriteList) {
                favoriteListFavorite.setCategoryId(null);
                favoriteListFavorite = em.merge(favoriteListFavorite);
            }
            em.remove(category);
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

    public List<Category> findCategoryEntities() {
        return findCategoryEntities(true, -1, -1);
    }

    public List<Category> findCategoryEntities(int maxResults, int firstResult) {
        return findCategoryEntities(false, maxResults, firstResult);
    }

    private List<Category> findCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Category.class));
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

    public Category findCategory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Category> rt = cq.from(Category.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
