package ml.iks.md.models.data;

public enum DeliveryStatus {
    Unknown("U"), Pending("P"), Failed("F"), Delivered("D"), Expired("X"), Error("E");
    private final String shortString;

    private DeliveryStatus(String shortString)
    {
        this.shortString = shortString;
    }

    public String toShortString()
    {
        return this.shortString;
    }
}
