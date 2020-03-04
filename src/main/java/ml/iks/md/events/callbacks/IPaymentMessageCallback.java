package ml.iks.md.events.callbacks;

import ml.iks.md.events.PaymentMessageEvent;

public interface IPaymentMessageCallback {
    public boolean process(PaymentMessageEvent event);
}
