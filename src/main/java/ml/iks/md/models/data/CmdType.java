package ml.iks.md.models.data;

public enum CmdType {

    UV, RECHARGE, PAIEMENT, ISAGO, MOBICASH_DIRECT, RVMOBICASH, AUTRUI;

    @Override
    public String toString() {
        return this.name();
    }
}
