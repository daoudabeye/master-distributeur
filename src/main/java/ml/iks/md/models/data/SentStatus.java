package ml.iks.md.models.data;

public enum SentStatus {
    Sent("S"), Unsent("U"), Queued("Q"), Failed("F");
    private final String shortString;

    private SentStatus(String shortString) {
        this.shortString = shortString;
    }

    public String toShortString() {
        return this.shortString;
    }
}
