
package ml.iks.md.events.callbacks;

import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.callback.IMessageSentCallback;
import ml.ikslib.gateway.callback.events.MessageSentCallbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSentCallback implements IMessageSentCallback {
	static Logger logger = LoggerFactory.getLogger(MessageSentCallback.class);

	@Override
	public boolean process(MessageSentCallbackEvent event) {
		try {
			BeanLocator.find(GatewayService.class).markMessageSent(event);
		} catch (Exception e) {
			logger.error("Error!", e);
			return false;
		} finally {
			return true;
		}
	}
}
