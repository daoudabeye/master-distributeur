package ml.iks.md.events;

import ml.ikslib.gateway.message.InboundMessage;

public class CreditSendMessageEvent extends MessageEvent {

    InboundMessage in;

    public CreditSendMessageEvent(InboundMessage in) {
        this.in = in;
    }
}
