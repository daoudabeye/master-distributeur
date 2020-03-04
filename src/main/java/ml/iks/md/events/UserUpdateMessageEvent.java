package ml.iks.md.events;

import ml.ikslib.gateway.message.InboundMessage;

public class UserUpdateMessageEvent extends MessageEvent {
    private InboundMessage message;

    public UserUpdateMessageEvent(InboundMessage message){
        this.message = message;
    }
}
