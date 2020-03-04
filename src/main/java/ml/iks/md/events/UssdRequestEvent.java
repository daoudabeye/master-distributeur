package ml.iks.md.events;

import ml.ikslib.gateway.ussd.USSDRequest;

public class UssdRequestEvent extends AirtimeAppEvent {
    private USSDRequest request;

    public UssdRequestEvent(USSDRequest request){
        this.request = request;
    }
}
