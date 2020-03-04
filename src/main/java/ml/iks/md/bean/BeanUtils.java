package ml.iks.md.bean;

import ml.iks.md.models.Command;
import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.models.data.*;
import ml.iks.md.repositories.CommandRepository;
import ml.iks.md.repositories.GatewayDefinitionRepository;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.message.OutboundMessage;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;

@Named
@SessionScoped
public class BeanUtils implements Serializable {

    public Carrier[] getCarrier() {
        return Carrier.values();
    }

    public NumProfile[] getProfile() {
        return NumProfile.values();
    }

    public CmdMode[] getMode() {
        return CmdMode.values();
    }

    public CmdType[] getType() {
        return CmdType.values();
    }

    public SentStatus[] getStatus() {
        return SentStatus.values();
    }

    public IncomingMessageType[] getInMsgType() {
        return IncomingMessageType.values();
    }

    public Command[] getCmdList() {
        List<Command> cmd =  BeanLocator.find(CommandRepository.class).findAll();
        if(!cmd.isEmpty()){
            Command[] commands = new Command[cmd.size()];
            Object[] cmdArray = cmd.toArray();
            for (int i = 0; i < cmdArray.length; i++) {
                commands[i] = (Command)cmdArray[i];
            }
            return commands;
        }

        return new Command[0];
    }

    public GatewayDefinition[] getGtwList(){
        List<GatewayDefinition> gtws = BeanLocator.find(GatewayDefinitionRepository.class).findAll();
        if(!gtws.isEmpty()){
            GatewayDefinition[] gatewayDefinitions = new GatewayDefinition[gtws.size()];
            Object[] array = gtws.toArray();
            for (int i = 0; i < array.length; i++) {
                gatewayDefinitions[i] = (GatewayDefinition)array[i];
            }
            return gatewayDefinitions;
        }

        return new GatewayDefinition[0];
    }

    public String[] getStatusList(){
        OutboundMessage.SentStatus[] array = OutboundMessage.SentStatus.values();
        String[] status = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            status[i] = array[i].name();
        }
        return status;
    }
}
