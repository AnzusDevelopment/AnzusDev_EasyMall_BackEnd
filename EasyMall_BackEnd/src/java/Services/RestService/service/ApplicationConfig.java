/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services.RestService.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Nirox
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(Services.RestService.service.CategoryFacadeREST.class);
        resources.add(Services.RestService.service.EventFacadeREST.class);
        resources.add(Services.RestService.service.FavoriteFacadeREST.class);
        resources.add(Services.RestService.service.MallFacadeREST.class);
        resources.add(Services.RestService.service.OfferFacadeREST.class);
        resources.add(Services.RestService.service.ParkingFacadeREST.class);
        resources.add(Services.RestService.service.PersonFacadeREST.class);
        resources.add(Services.RestService.service.ProductFacadeREST.class);
        resources.add(Services.RestService.service.StoreFacadeREST.class);
    }
    
}
