package ml.iks.md.events.callbacks;

import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.hook.IPreSendHook;
import ml.ikslib.gateway.message.OutboundMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagePreSendHook implements IPreSendHook {

    static Logger logger = LoggerFactory.getLogger(MessagePreSendHook.class);

    @Override
    public boolean process(OutboundMessage outboundMessage) {
        try{
            BeanLocator.find(GatewayService.class).saveMessage(outboundMessage);
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
