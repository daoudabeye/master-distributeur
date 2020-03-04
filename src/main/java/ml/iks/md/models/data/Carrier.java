package ml.iks.md.models.data;

public enum Carrier {
    ORANGE, MALITEL, TELECEL, NONE, LOCAL;


    @Override
    public String toString() {
        return name();
    }
}
