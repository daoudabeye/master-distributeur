
package ml.iks.md.events.callbacks;

import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.callback.IInboundCallCallback;
import ml.ikslib.gateway.callback.events.InboundCallCallbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundCallCallback implements IInboundCallCallback {
	static Logger logger = LoggerFactory.getLogger(InboundCallCallback.class);

	@Override
	public boolean process(InboundCallCallbackEvent event) {
		try {
			BeanLocator.find(GatewayService.class).saveInboundCall(event);
			return true;
		} catch (Exception e) {
			logger.error("Error!", e);
			return false;
		}
	}
}
