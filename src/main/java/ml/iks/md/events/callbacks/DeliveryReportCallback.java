
package ml.iks.md.events.callbacks;

import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.callback.IDeliveryReportCallback;
import ml.ikslib.gateway.callback.events.DeliveryReportCallbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeliveryReportCallback implements IDeliveryReportCallback {
	static Logger logger = LoggerFactory.getLogger(DeliveryReportCallback.class);

	@Override
	public boolean process(DeliveryReportCallbackEvent event) {
		try {
			BeanLocator.find(GatewayService.class).saveDeliveryReport(event);
			ml.ikslib.gateway.Service.getInstance().delete(event.getMessage());
		} catch (Exception e) {
			logger.error("Error!", e);
		}finally {
			return true;
		}
	}
}
