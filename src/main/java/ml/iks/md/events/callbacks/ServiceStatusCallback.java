
package ml.iks.md.events.callbacks;

import ml.ikslib.gateway.callback.IServiceStatusCallback;
import ml.ikslib.gateway.callback.events.ServiceStatusCallbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceStatusCallback implements IServiceStatusCallback {
	static Logger logger = LoggerFactory.getLogger(ServiceStatusCallback.class);

	@Override
	public boolean process(ServiceStatusCallbackEvent event) {
		return true;
	}
}
