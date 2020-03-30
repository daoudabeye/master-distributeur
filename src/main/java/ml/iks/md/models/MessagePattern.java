package ml.iks.md.models;

import ml.iks.md.events.AppCallbackManager;
import ml.iks.md.models.data.CmdType;
import ml.iks.md.models.data.IncomingMessageType;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.ClientRepository;
import ml.iks.md.repositories.CompteRepository;
import ml.iks.md.repositories.PaymentRepository;
import ml.iks.md.service.AirtimeService;
import ml.iks.md.service.PaymentService;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.message.OutboundMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class MessagePattern implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(MessagePattern.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int priority;

    private boolean forwarding;

    private String regex;

    @Enumerated
    private IncomingMessageType messageType;

    public MessagePattern() {
    }

    public MessagePattern(IncomingMessageType messageType, int priority, boolean forwarding, String regex) {
        //TODO : MessagePattern UI
        this.priority = priority;
        this.forwarding = forwarding;
        this.regex = regex;
        this.messageType = messageType;
    }

    public boolean findAndTrigger(InMessage msg) {
        Pattern pattern = Pattern.compile(regex);
        if (StringUtils.isEmpty(msg.getText()))
            return false;

        Matcher matcher = pattern.matcher(msg.getText().trim());
        while (matcher.find()) {
            switch (messageType) {
                case INSCRIPTION: //demande inscription
                    String numero = matcher.group("p").replace("*", "");
                    String nom = matcher.group("n").replace("*", "");
                    String role = matcher.group("r").replace("*", "");
                    InscriptionOperation ins = new InscriptionOperation(nom, numero, msg.getAddress());
                    try {
                        String defNum1 = matcher.group("d1");
                        ins.addNumber(defNum1.replace("*", ""), NumProfile.getFrom(Integer.parseInt(role)));
                    } catch (Exception e) {
                        log.error(e.getLocalizedMessage());
                    }

                    try {
                        String defNum2 = matcher.group("d2");
                        ins.addNumber(defNum2.replace("*", ""), NumProfile.getFrom(Integer.parseInt(role)));
                    } catch (Exception ignored) {
                    }

                    try {
                        String defNum3 = matcher.group("d3");
                        ins.addNumber(defNum3.replace("*", ""), NumProfile.getFrom(Integer.parseInt(role)));
                    } catch (Exception ignored) {
                    }

                    BeanLocator.find(AppCallbackManager.class).registerInscriptionEvent(msg, ins);

                    return true;

                case PAYMENT: // traitement d'un paiement
                    //TODO : Paiement Org, Malitel, Autrui
                    String payer = matcher.group("n");
                    String amount = matcher.group("m");
                    String solde = matcher.group("s");
                    String frais = "0";
                    String id = "";

                    try {
                        frais = matcher.group("f");
                    } catch (Exception ignored) {
                    }
                    try {
                        id = matcher.group("id");
                    } catch (Exception ignored) {
                    }

                    Payment payment = new Payment(msg.getId(), msg.getGatewayId(), id, payer,
                            Double.parseDouble(amount), Double.parseDouble(frais));
                    BeanLocator.find(AppCallbackManager.class).registerPaymentMessageEvent(msg, payment);

                    BeanLocator.find(AirtimeService.class).updateSimSolde(msg.getGatewayId(), solde);

                    return true;

                case PAYMENT_ACK: //confirmation d'un paiement
                    String ackNum = matcher.group("n");
                    String ackMontant = matcher.group("m");
                    String ackSolde = matcher.group("s");
                    String ackId = "";
                    try {
                        ackId = matcher.group("id");
                    } catch (Exception ignored) {
                    }

                    BeanLocator.find(AirtimeService.class).ackRecharge(msg.getGatewayId(), ackId, ackNum, ackMontant, ackSolde);

                    return true;

                case SEND_CREDIT: //Envois credit
                    String recipient = matcher.group("n");
                    String sendMontant = matcher.group("m");

                    String rviOption = "0";
                    try {
                        rviOption = matcher.group("o");
                    } catch (Exception ignored) {
                    }

                    BeanLocator.find(AirtimeService.class).doSmsAction(msg, recipient, sendMontant, rviOption);

                    return true;
                case RECHARGE_RECEIVED: //Reception d'une recharge
                    String newSolde = matcher.group("s");
                    BeanLocator.find(AirtimeService.class).updateSimSolde(msg.getGatewayId(), newSolde);

                    return true;

                case BALANCE_CHECK:
                    String addr = msg.getNationalAddress();
                    BeanLocator.find(AirtimeService.class).checkBalance(addr);
                    return true;

                case USER_UPDATE:
                    log.info("mtach USER_UPDATE");
                    String option = matcher.group("o");
                    BeanLocator.find(AirtimeService.class).updateUserMsg(msg.getNationalAddress(), option, msg.getText());
                    return true;

                case ISAGO:
                    String compteur = matcher.group("n");
//                    String creditIsago = matcher.group("m");
//                    BeanLocator.find(AirtimeService.class).buyIsago(msg, compteur, creditIsago);

                    BeanLocator.find(AirtimeService.class).updateIsagoNumber(msg.getNationalAddress(), compteur);
                    return true;

                case COMMISSION:
//                    Compte compte =
//                    BeanLocator.find(CompteRepository.class).save()
                    return true;

                case RETOUR:
                    String rpayer = matcher.group("n");
                    String ramount = matcher.group("m");
                    String rsolde = matcher.group("s");
                    Payment rpayment = new Payment(msg.getId(), msg.getGatewayId(), "", rpayer,
                            Double.parseDouble(ramount), 0f);
                    rpayment.setStatus(OutboundMessage.SentStatus.Sent);
                    rpayment.setCommandType(CmdType.RETOUR);
                    Optional<Client> clRetour = BeanLocator.find(ClientRepository.class).findByNumero(rpayer);
                    if (clRetour.isPresent()){
                        rpayment.setClient(clRetour.get());
                    }
                    BeanLocator.find(PaymentRepository.class).save(rpayment);
                    break;

                default:
                    return false;
            }


        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isForwarding() {
        return forwarding;
    }

    public void setForwarding(boolean forwarding) {
        this.forwarding = forwarding;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public IncomingMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(IncomingMessageType messageType) {
        this.messageType = messageType;
    }
}
