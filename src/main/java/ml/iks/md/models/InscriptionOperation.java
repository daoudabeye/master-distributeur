package ml.iks.md.models;

import ml.iks.md.events.AppCallbackManager;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.ClientRepository;
import ml.iks.md.repositories.InMessageRepository;
import ml.iks.md.repositories.MobileNumberRepository;
import ml.iks.md.repositories.PaymentRepository;
import ml.iks.md.service.AirtimeService;
import ml.iks.md.service.GatewayService;
import ml.iks.md.util.AppConstants;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.message.OutboundMessage;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author BEYE
 * <p>
 * pattern ^100(\*\d{8})(\*\d{8})?(\*\d{8})?(\*\d{8})?(\*[a-zA-Z\s]+)(\*\d)#$
 * <p>
 * ^100(?<p>\*\d{8})(?<d1>\*\d{8})?(?<d2>\*\d{8})?(?<d3>\*\d{8})?(?<n>\*[a-zA-Z\s]+)(?<r>\*\d)#$
 * <p>
 * Inscription avec un numero de paiment + 3 par defauts
 */

public class InscriptionOperation extends AbstractOperation {

    Client client;

    Collection<MobileNumber> mobileNumbers;

    public InscriptionOperation() {
        super("INSCRIPTION");
    }

    /**
     * Constructor
     * @param nom         nom & prenom du client
     * @param numero      numero de paiement
     * @param inscripteur numero inscripteur
     */
    public InscriptionOperation(String nom, String numero, String inscripteur) {
        super("INSCRIPTION");
        client = new Client();
        client.setNom(nom);
        client.setNumero(numero);
        client.setNumeroInscripteur(inscripteur);
        mobileNumbers = new ArrayList<>();
    }

    public void addNumber(String number, NumProfile profile) {
        Carrier carrier = AppConstants.getOperator(number);

        MobileNumber mobileNumber = new MobileNumber(profile, carrier, number);
        mobileNumber.setClient(client);
        mobileNumber.setDefaultNumber(false);
        mobileNumber.setPrioriter(1);
        mobileNumbers.add(mobileNumber);
    }

    @Override
    public boolean execute() {
        client.setNumbers(mobileNumbers);
        client = BeanLocator.find(ClientRepository.class).save(client);

        for (MobileNumber mobileNumber : mobileNumbers) {
            mobileNumber.setClient(client);
            mobileNumber.setDefaultNumber(true);
            BeanLocator.find(MobileNumberRepository.class).save(mobileNumber);
        }

        String txt = "Bienvenue chez Dream Tech. Votre inscription au service Boutikini a ete prise en compte. Nous vous remercions pour votre confiance";
        BeanLocator.find(AirtimeService.class).sendNotification(client.getNumero(), txt);

        String inscripteur = client.getNumeroInscripteur();
        if(!StringUtils.isEmpty(inscripteur)){
            String txtInscripteur = "L'inscription du " + client.getNumero() + " est reussie. Boutikini.";
            BeanLocator.find(AirtimeService.class).sendNotification(inscripteur.replace("223", ""), txtInscripteur);
        }

        List<Payment> payments = BeanLocator.find(PaymentRepository.class).
                findByPayerAndStatusOrderByDateCreationDesc(client.getNumero(), OutboundMessage.SentStatus.Queued);
        for (Payment payment : payments) {
            InMessage sms = BeanLocator.find(InMessageRepository.class).getOne(payment.getMessageId());
            BeanLocator.find(AppCallbackManager.class).registerPaymentMessageEvent(sms, payment);
        }

        return true;
    }
}
