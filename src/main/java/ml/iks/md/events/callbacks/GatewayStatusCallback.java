
package ml.iks.md.events.callbacks;

import ml.ikslib.gateway.AbstractGateway;
import ml.ikslib.gateway.callback.IGatewayStatusCallback;
import ml.ikslib.gateway.callback.events.GatewayStatusCallbackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayStatusCallback implements IGatewayStatusCallback {
	static Logger logger = LoggerFactory.getLogger(GatewayStatusCallback.class);

	@Override
	public boolean process(GatewayStatusCallbackEvent event) {
		if(event.getGateway().getStatus() == AbstractGateway.Status.Starting || event.getGateway().getStatus() == AbstractGateway.Status.Stopping){

		}
		if(event.getGateway().getStatus() == AbstractGateway.Status.Started || event.getGateway().getStatus() == AbstractGateway.Status.Stopped){

		}

		return true;
	}
}
