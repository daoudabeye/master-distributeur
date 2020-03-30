package ml.iks.md.models.data;

public enum CmdType {

    UV, RECHARGE, PAIEMENT, ISAGO, MOBICASH_DIRECT, RVMOBICASH, AUTRUI, RETOUR;

    @Override
    public String toString() {
        return this.name();
    }
}
