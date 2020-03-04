
package ml.iks.md.gateway;

import ml.iks.md.service.GatewayService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownThread extends Thread {
	static Logger logger = LoggerFactory.getLogger(ShutdownThread.class);

	@Override
	public void run() {
		logger.info("SMSServer going down, please wait...");
		try {
			GatewayService gtw = BeanLocator.find(GatewayService.class);
			if (gtw.getOutboundServiceThread() != null) {
				gtw.getOutboundServiceThread().shutdown();
				gtw.getOutboundServiceThread().join();
			}
			Service.getInstance().stop();
			Service.getInstance().terminate();
		} catch (Exception e) {
			logger.error("Error while terminating the SMSServer service!", e);
		}
	}
}
