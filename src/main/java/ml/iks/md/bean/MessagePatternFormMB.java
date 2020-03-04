/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ml.iks.md.bean;

import ml.iks.md.models.MessagePattern;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.service.MsgPatternService;
import ml.iks.md.service.RouteService;
import ml.iks.md.util.Utils;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

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
public class MessagePatternFormMB implements Serializable {


    private Long id;
    private MessagePattern messagePattern;


    @Inject
    MsgPatternService msgService;

    public void init() {
        if(Faces.isAjaxRequest()){
           return;
        }
        if (has(id)) {
            messagePattern = msgService.findById(id);
        } else {
            messagePattern = new MessagePattern();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessagePattern getMessagePattern() {
        return messagePattern;
    }

    public void setMessagePattern(MessagePattern route) {
        this.messagePattern = route;
    }


    public void remove() throws IOException {
        if (has(messagePattern) && has(messagePattern.getId())) {
            msgService.remove(messagePattern);
            Utils.addDetailMessage("La Commade " + messagePattern.getRegex()
                    + " supprimer avec suuces");
            Faces.getFlash().setKeepMessages(true);
            Faces.redirect("parametres.jsf");
        }
    }

    public void save() {
        String msg;
        if (messagePattern.getId() == null) {
            msgService.insert(messagePattern);
            msg = "route " + messagePattern + " created successfully";
        } else {
            msgService.update(messagePattern);
            msg = "Commande " + messagePattern.getId() + " updated successfully";
        }
        Utils.addDetailMessage(msg);
    }

    public void clear() {
        messagePattern = new MessagePattern();
        id = null;
    }

    public boolean isNew() {
        return messagePattern == null || messagePattern.getId() == null;
    }
}
