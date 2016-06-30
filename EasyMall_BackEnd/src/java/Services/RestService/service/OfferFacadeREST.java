/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services.RestService.service;

import Data.Entity.Offer;
import Data.Entity.OfferPK;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;

/**
 *
 * @author Nirox
 */
@Stateless
@Path("data.entity.offer")
public class OfferFacadeREST extends AbstractFacade<Offer> {
    @PersistenceContext(unitName = "EasyMall_BackEndPU")
    private EntityManager em;

    private OfferPK getPrimaryKey(PathSegment pathSegment) {
        /*
         * pathSemgent represents a URI path segment and any associated matrix parameters.
         * URI path part is supposed to be in form of 'somePath;offerId=offerIdValue;storeId=storeIdValue'.
         * Here 'somePath' is a result of getPath() method invocation and
         * it is ignored in the following code.
         * Matrix parameters are used as field names to build a primary key instance.
         */
        Data.Entity.OfferPK key = new Data.Entity.OfferPK();
        javax.ws.rs.core.MultivaluedMap<String, String> map = pathSegment.getMatrixParameters();
        java.util.List<String> offerId = map.get("offerId");
        if (offerId != null && !offerId.isEmpty()) {
            key.setOfferId(new java.lang.Integer(offerId.get(0)));
        }
        java.util.List<String> storeId = map.get("storeId");
        if (storeId != null && !storeId.isEmpty()) {
            key.setStoreId(new java.lang.Integer(storeId.get(0)));
        }
        return key;
    }

    public OfferFacadeREST() {
        super(Offer.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Offer entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") PathSegment id, Offer entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") PathSegment id) {
        Data.Entity.OfferPK key = getPrimaryKey(id);
        super.remove(super.find(key));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Offer find(@PathParam("id") PathSegment id) {
        Data.Entity.OfferPK key = getPrimaryKey(id);
        return super.find(key);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Offer> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Offer> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
