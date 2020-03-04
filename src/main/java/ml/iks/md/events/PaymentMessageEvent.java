package ml.iks.md.events;

import ml.iks.md.models.InMessage;
import ml.iks.md.models.Payment;

public class PaymentMessageEvent extends MessageEvent {

    Payment payment;

    public PaymentMessageEvent(InMessage in, Payment payment) {
        this.in = in;
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }
}
