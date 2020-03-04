
package ml.iks.md.events.callbacks;

import ml.iks.md.models.InMessage;
import ml.iks.md.models.MessagePattern;
import ml.iks.md.repositories.MessagePatternRepository;
import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.callback.IInboundMessageCallback;
import ml.ikslib.gateway.callback.events.InboundMessageCallbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InboundMessageCallback implements IInboundMessageCallback {
    static Logger logger = LoggerFactory.getLogger(InboundMessageCallback.class);

    @Override
    public boolean process(InboundMessageCallbackEvent event) {
        try {
            InMessage sms = BeanLocator.find(GatewayService.class).saveInboundMessage(event);
            ml.ikslib.gateway.Service.getInstance().delete(event.getMessage());

            List<MessagePattern> patterns = BeanLocator.find(MessagePatternRepository.class).findAll();
            for (MessagePattern pattern : patterns) {
                boolean triggered = pattern.findAndTrigger(sms);
                if(triggered && !pattern.isForwarding())
                    break;
            }

        } catch (Exception e) {
            logger.error("Error!", e);
        }

        return true;
    }
}
