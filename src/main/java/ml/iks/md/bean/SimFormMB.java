/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.iks.md.bean;

import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.models.Sim;
import ml.iks.md.service.GatewayService;
import ml.iks.md.service.SimService;
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
public class SimFormMB implements Serializable {


    private Long id;
    private Sim sim;

    @Inject
    SimService service;

    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (has(id)) {
            sim = service.findById(id);
        } else {
            sim = new Sim();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sim getSim() {
        return sim;
    }

    public void setSim(Sim sim) {
        this.sim = sim;
    }

    public void remove() throws IOException {
        if (has(sim) && has(sim.getId())) {
            service.remove(sim);
            Utils.addDetailMessage("La Sim " + sim.getName()
                    + " supprimer avec suuces");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("parametres.jsf");
        }
    }

    public void save() {
        String msg;
        if (sim.getId() == null) {
            service.insert(sim);
            msg = "Sim " + sim.getName() + " created successfully";
        } else {
            service.update(sim);
            msg = "Sim " + sim.getName() + " updated successfully";
        }
        Utils.addDetailMessage(msg);
    }

    public void clear() {
        sim = new Sim();
        id = null;
    }

    public boolean isNew() {
        return sim == null || sim.getId() == null;
    }
}
