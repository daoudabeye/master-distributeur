package ml.iks.md.models;

import javax.persistence.Entity;
import java.util.regex.Matcher;

/**
 * @author BEYE
 *
 * Payment Org
 * Le ([0-9]+) a confirme son paiement de ([0-9]+(?:\\.[0-9]{1,2})?) FCFA. Votre nouveau solde est de: ([0-9]+(?:\\.[0-9]{1,2})?) FCFA.
 *
 *
 */
@Entity
public class RechargeOperation extends AbstractOperation {

    private String numberToCredit;
    private double amount;

    public RechargeOperation() {
        super("RECHARGE");
    }

    public RechargeOperation(Matcher mather){
        super("RECHARGE");
    }

    @Override
    protected boolean execute() {
        return false;
    }
}
