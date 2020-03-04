package ml.iks.md.events;

public class BalanceRequestEvent extends MessageEvent {
    private String number;

    public BalanceRequestEvent(String number){
        this.number = number;
    }
}
