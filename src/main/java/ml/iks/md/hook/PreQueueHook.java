
package ml.iks.md.hook;

import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.hook.IPreQueueHook;
import ml.ikslib.gateway.message.OutboundMessage;
import ml.ikslib.gateway.message.OutboundMessage.SentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreQueueHook implements IPreQueueHook {
	static Logger logger = LoggerFactory.getLogger(PreQueueHook.class);

	public boolean process(OutboundMessage message) {
		try {
			BeanLocator.find(GatewayService.class).setMessageStatus(message, SentStatus.Queued);
			return true;
		} catch (Exception e) {
			logger.error("Error!", e);
			return false;
		}
	}
}
