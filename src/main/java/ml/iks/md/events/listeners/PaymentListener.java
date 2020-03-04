package ml.iks.md.events.listeners;

import ml.iks.md.events.PaymentMessageEvent;
import ml.iks.md.events.callbacks.IPaymentMessageCallback;
import ml.iks.md.models.Payment;
import ml.ikslib.gateway.message.OutboundMessage;

public class PaymentListener implements IPaymentMessageCallback {

    @Override
    public boolean process(PaymentMessageEvent event) {
        // TODO : processus de recharge client
        try {
            Payment payment = event.getPayment();
            payment.execute();

            if(payment.getStatus().equals(OutboundMessage.SentStatus.Failed) && "GatewayFailure".equals(payment.getFailureCause())){
                Thread.sleep(1000);
                payment.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            return true;
        }
    }
}
