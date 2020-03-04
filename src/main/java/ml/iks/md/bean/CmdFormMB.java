/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.iks.md.bean;

import ml.iks.md.model.Car;
import ml.iks.md.models.Command;
import ml.iks.md.service.CarService;
import ml.iks.md.service.CmdService;
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
public class CmdFormMB implements Serializable {


    private Long id;
    private Command command;


    @Inject
    CmdService cmdService;

    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (has(id)) {
            command = cmdService.findById(id);
        } else {
            command = new Command();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }


    public void remove() throws IOException {
        if (has(command) && has(command.getId())) {
            cmdService.remove(command);
            Utils.addDetailMessage("La Commade " + command.getName()
                    + " supprimer avec suuces");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("parametres.jsf");
        }
    }

    public void save() {
        String msg;
        if (command.getId() == null) {
            cmdService.insert(command);
            msg = "Command " + command + " created successfully";
        } else {
            cmdService.update(command);
            msg = "Commande " + command.getName() + " updated successfully";
        }
        Utils.addDetailMessage(msg);
    }

    public void clear() {
        command = new Command();
        id = null;
    }

    public boolean isNew() {
        return command == null || command.getId() == null;
    }
}
