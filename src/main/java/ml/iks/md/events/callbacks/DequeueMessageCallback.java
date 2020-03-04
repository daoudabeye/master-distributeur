
package ml.iks.md.events.callbacks;

import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.callback.IDequeueMessageCallback;
import ml.ikslib.gateway.callback.events.DequeueMessageCallbackEvent;
import ml.ikslib.gateway.message.OutboundMessage.SentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DequeueMessageCallback implements IDequeueMessageCallback {
	static Logger logger = LoggerFactory.getLogger(DequeueMessageCallback.class);

	public boolean process(DequeueMessageCallbackEvent event) {
		try {
			BeanLocator.find(GatewayService.class).setMessageStatus(event.getMessage(), SentStatus.Unsent);
			return true;
		} catch (Exception e) {
			logger.error("Error!", e);
			return false;
		}
	}
}
