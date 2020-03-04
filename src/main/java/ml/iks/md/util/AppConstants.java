package ml.iks.md.util;

import ml.iks.md.models.data.Carrier;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class AppConstants {
    static final String CSS_PATH = "/styles/bootstrap3.css";
    static final String PROJECT_TITLE = "AIRTIME 1.0";

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final int MAX_PAGE_SIZE = 50;

    static final String NUMBER_PATTERN ="^(([(]?[0-9]{1,3}[)]?)|([(]?[0-9]{4}[)]?))\\s*[)]?[-\\s\\.]?[(]?[0-9]{1,3}[)]?([-\\s\\.]?[0-9]{3})([-\\s\\.]?[0-9]{3,4})$";

    public static final String INVITE_NOTIF = "Faites une inscription gagnante via l'appli boutikini ou par SMS au 82040404: \n" +
            "100*numéro de paiement*numéro à recharger*nom et prénom*1#. Nous vous remercions pour votre confiance";

    public static final String[] PREFIX_ORG = {"7", "82", "90", "93", "83", "91", "94", "92"};
    public static final String[] PREFIX_ML = {"6", "89", "95", "96", "97", "98", "99"};
    public static final String[] PREFIX_TELE = {"5"};

    public static final String PATTERN_AUTRUI = "(\\d{8})\\*([0-9]+(?:\\.[0-9]{1,2})?)\\*(\\d)#";//numRecharge*montant*reseau#
    public static final String PATTERN_AUTRUI_2 = "(\\d{8})\\*(\\d{8})\\*([0-9]+(?:\\.[0-9]{1,2})?)\\*(\\d)#";//numPaiement*numRecharge*montant*reseau#

    public static final Pattern PATTERN_PAIEMENT_AUTRUI = Pattern.compile(AppConstants.PATTERN_AUTRUI);
    public static final Pattern PATTERN_PAIEMENT_AUTRUI_2 = Pattern.compile(AppConstants.PATTERN_AUTRUI_2);

    public static Carrier getOperator(String number) {
        Carrier op = null;
        if(StringUtils.isEmpty(number))
            return null;

        for(String p: PREFIX_ORG) {
            if(number.startsWith(p)) {
                op = Carrier.ORANGE;
                break;
            }
        }

        if(op == null) {
            for(String p: PREFIX_ML) {
                if(number.startsWith(p)) {
                    op = Carrier.MALITEL;
                    break;
                }
            }
        }

        if(op == null) {
            for(String p: PREFIX_TELE) {
                if(number.startsWith(p)) {
                    op = Carrier.TELECEL;
                    break;
                }
            }
        }

        return op;
    }
}
