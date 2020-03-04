package ml.iks.md.events;

import ml.iks.md.models.InMessage;

public class MessageEvent extends AirtimeAppEvent {

    InMessage in;

    public InMessage getIn() {
        return in;
    }
}
