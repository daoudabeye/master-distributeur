package ml.iks.md.events;

import ml.ikslib.gateway.message.InboundMessage;

public class PaymentConfirmationEvent extends MessageEvent {

    InboundMessage in;

    public PaymentConfirmationEvent(InboundMessage in) {
        this.in = in;
    }
}
