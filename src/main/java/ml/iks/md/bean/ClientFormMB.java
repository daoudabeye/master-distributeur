/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.iks.md.bean;
import ml.iks.md.models.Client;
import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.service.ClientService;
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
public class ClientFormMB implements Serializable {


    private Long id;
    private Client client;

    @Inject
    ClientService service;

    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (has(id)) {
            client = service.findById(id);
        } else {
            client = new Client();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void remove() throws IOException {
        if (has(client) && has(client.getId())) {
            service.remove(client);
            Utils.addDetailMessage("La Commade " + client.getNom()
                    + " supprimer avec suuces");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("parametres.jsf");
        }
    }

    public void save() {
        String msg;
        if (client.getId() == null) {
            service.insert(client);
            msg = "route " + client + " created successfully";
        } else {
            service.update(client);
            msg = "Commande " + client.getNom() + " updated successfully";
        }
        Utils.addDetailMessage(msg);
    }

    public void clear() {
        client = new Client();
        id = null;
    }

    public boolean isNew() {
        return client == null || client.getId() == null;
    }
}
