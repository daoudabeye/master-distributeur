/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.iks.md.bean;

import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.service.GatewayService;
import ml.iks.md.service.RouteService;
import ml.iks.md.util.Utils;
import org.omnifaces.util.Faces;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * @author rmpestano
 */
@Named
@ViewScoped
public class GatewayFormMB implements Serializable {


    private Long id;
    private GatewayDefinition gatewayDefinition;

    private Long selectedCmdId;


    @Inject
    GatewayService service;

    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (has(id)) {
            gatewayDefinition = service.findById(id);
        } else {
            gatewayDefinition = new GatewayDefinition();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GatewayDefinition getGatewayDefinition() {
        return gatewayDefinition;
    }

    public void setGatewayDefinition(GatewayDefinition gatewayDefinition) {
        this.gatewayDefinition = gatewayDefinition;
    }

    public Long getSelectedCmdId() {
        return selectedCmdId;
    }

    public void setSelectedCmdId(Long selectedCmdId) {
        this.selectedCmdId = selectedCmdId;
    }

    public void remove() throws IOException {
        if (has(gatewayDefinition) && has(gatewayDefinition.getId())) {
            service.remove(gatewayDefinition);
            Utils.addDetailMessage("La Commade " + gatewayDefinition.getGatewayId()
                    + " supprimer avec suuces");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("parametres.jsf");
        }
    }

    public void save() {
        String msg;
        if (gatewayDefinition.getId() == null) {
            service.insert(gatewayDefinition);
            msg = "route " + gatewayDefinition + " created successfully";
        } else {
            service.update(gatewayDefinition);
            msg = "Commande " + gatewayDefinition.getGatewayId() + " updated successfully";
        }
        Utils.addDetailMessage(msg);
    }

    public void clear() {
        gatewayDefinition = new GatewayDefinition();
        id = null;
    }

    public boolean isNew() {
        return gatewayDefinition == null || gatewayDefinition.getId() == null;
    }
}
