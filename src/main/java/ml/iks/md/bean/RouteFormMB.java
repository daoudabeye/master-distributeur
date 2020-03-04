/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.iks.md.bean;

import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.repositories.GatewayDefinitionRepository;
import ml.iks.md.service.RouteService;
import ml.iks.md.util.BeanLocator;
import ml.iks.md.util.Utils;
import org.omnifaces.util.Faces;
import org.primefaces.model.DualListModel;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * @author rmpestano
 */
@Named
@ViewScoped
public class RouteFormMB implements Serializable {


    private Long id;
    private RoutingTable route;


    @Inject
    RouteService routeService;

    GatewayDefinitionRepository gatewayDefinitionRepository;

    private DualListModel<GatewayDefinition> gateways;

    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }

        gatewayDefinitionRepository = BeanLocator.find(GatewayDefinitionRepository.class);
        List<GatewayDefinition> gatewaysDef = gatewayDefinitionRepository.findAll();
        List<GatewayDefinition> source = new ArrayList<GatewayDefinition>(gatewaysDef);
        List<GatewayDefinition> target = new ArrayList<GatewayDefinition>();

        if (has(id)) {
            route = routeService.findById(id);
            target = new ArrayList<GatewayDefinition>(route.getGateways());
            source.removeAll(target);
        } else {
            route = new RoutingTable();
        }
        gateways = new DualListModel<GatewayDefinition>(source, target);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoutingTable getRoute() {
        return route;
    }

    public void setRoute(RoutingTable route) {
        this.route = route;
    }


    public void remove() throws IOException {
        if (has(route) && has(route.getId())) {
            routeService.remove(route);
            Utils.addDetailMessage("La Commade " + route.getName()
                    + " supprimer avec suuces");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("parametres.jsf");
        }
    }

    public void save() {
        String msg;
        List<GatewayDefinition> targedGtw = gateways.getTarget();

        for (int i = 0; i < targedGtw.size(); i++) {
            Optional<GatewayDefinition> gatewayDefinition = gatewayDefinitionRepository.findByGatewayId(targedGtw.get(i) + "");
            gatewayDefinition.ifPresent(definition -> route.addGateway(definition));
        }
        if (route.getId() == null) {
            routeService.insert(route);
            msg = "route " + route + " created successfully";
        } else {
            routeService.update(route);
            msg = "Commande " + route.getName() + " updated successfully";
        }
        Utils.addDetailMessage(msg);
    }

    public void clear() {
        route = new RoutingTable();
        id = null;
    }

    public boolean isNew() {
        return route == null || route.getId() == null;
    }

    public DualListModel<GatewayDefinition> getGateways() {
        return gateways;
    }

    public void setGateways(DualListModel<GatewayDefinition> gateways) {
        this.gateways = gateways;
    }
}
