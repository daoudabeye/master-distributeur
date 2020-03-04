package ml.iks.md.service;

import ml.iks.md.events.AppCallbackManager;
import ml.iks.md.models.*;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.CmdMode;
import ml.iks.md.models.data.CmdType;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.*;
import ml.iks.md.util.AppConstants;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.message.MsIsdn;
import ml.ikslib.gateway.message.OutboundMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
//@EnableAsync
@Transactional
public class AirtimeService {

    private static final Logger log = LoggerFactory.getLogger(AirtimeService.class);

    //TODO: SYSTEME SMS OUTBOUNG API CONFIG VIA SETTINGS
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    SimRepository simRepository;

    @Autowired
    private EntityManager em;

    public AirtimeService() {
    }

//    @Async
//    @Scheduled(fixedRate = 1000)
//    public void scheduleFixedRateTaskAsync() throws InterruptedException {
//        System.out.println(
//                "Fixed rate task async - " + System.currentTimeMillis() / 1000);
//        Thread.sleep(2000);
//    }

    public void laoding(boolean isLoading) {
    }

    public void deleteRoute(Long routeId, Long gtwId) {
        Query q = em.createNativeQuery("DELETE FROM route WHERE route_id=" + routeId + " and gateway_id = " + gtwId);
        q.executeUpdate();
    }

    public void deleteUSerRole(Long id, Long id1) {
        Query q = em.createNativeQuery("DELETE FROM user_roles WHERE user_id=" + id + " and role_id=" + id1);
        q.executeUpdate();
    }

    public void checkBalance(String numero) {
        Optional<Client> cl = BeanLocator.find(ClientRepository.class).findByNumero(numero);
        if (cl.isPresent()) {
            double solde = cl.get().getBalance();

            String txt = "Votre solde actuelle est de :" + solde + " CFA.";
            sendNotification(numero, txt);
        }
    }

    public void updateUserMsg(String numero, String option, String text) {
        Optional<Client> cl = BeanLocator.find(ClientRepository.class).findByNumero(numero);
        log.debug("number : " + numero + " option :" + option + " texte = " + text);
        if (cl.isPresent()) {
            Client client = cl.get();
            String[] content = text.replace("#", "").split("\\*");
            switch (option) {
                case "1":
                    //Modifier le nom & prenom
                    String nom = content[2];
                    client.setNom(nom);
                    BeanLocator.find(ClientRepository.class).save(client);
                    break;
                case "2":
                    //Ajouter un numero
                    String num = content[3];
                    Carrier carrier = AppConstants.getOperator(num);

                    NumProfile profile = NumProfile.getFrom(Integer.parseInt(content[2]));

                    MobileNumber mobileNumber = new MobileNumber(profile, carrier, num);
                    mobileNumber.setClient(client);
                    mobileNumber.setDefaultNumber(false);

                    BeanLocator.find(MobileNumberRepository.class).save(mobileNumber);
                    break;
                case "3":
                    String defNum = content[2];
                    List<MobileNumber> numbers = BeanLocator.find(MobileNumberRepository.class).findByClient(client);
                    for (MobileNumber n : numbers) {
                        if (n.getNumber().equals(defNum)) {
                            n.setDefaultNumber(true);
                            BeanLocator.find(MobileNumberRepository.class).save(n);
                        }
                    }
                    break;

                case "4":
                    String deleteNum = content[2];
                    List<MobileNumber> candidates = BeanLocator.find(MobileNumberRepository.class).findByClient(client);
                    for (MobileNumber c : candidates) {
                        if (c.getNumber().equals(deleteNum)) {
                            BeanLocator.find(MobileNumberRepository.class).delete(c);
                        }
                    }
                    break;

                case "5":
                    String pin = content[3];
                    client.setPin(pin);
                    BeanLocator.find(ClientRepository.class).save(client);
                    break;
                case "6":
                    List<MobileNumber> numbersList = BeanLocator.find(MobileNumberRepository.class).findByClient(client);
                    String listNum = numbersList.toString();
                    sendNotification(numero, listNum);
                    break;
                case "7":
                    //Ajouter un numero ISAGO
                    String numIsago = content[2];

                    MobileNumber isago = new MobileNumber(NumProfile.ISAGO, Carrier.NONE, numIsago);
                    isago.setClient(client);
                    isago.setDefaultNumber(false);

                    BeanLocator.find(MobileNumberRepository.class).save(isago);
                    break;
            }
        }
    }

    public void sendNotification(String recp, String texte) {
        log.debug("Sending to :" + recp + " content: " + texte);
        if(StringUtils.isEmpty(texte))
            return;
        MsIsdn recipient = new MsIsdn(recp, MsIsdn.Type.National);
        ml.ikslib.gateway.Service.getInstance().send(new OutboundMessage(recipient, texte));
    }

    /**
     * Ack recharge paiement
     *
     * @param gateway   incoming confirmatoin message gateway
     * @param ackId     transaction id
     * @param recipient payer number
     * @param amount    transaction amount
     * @param soldeSim  sim card balance
     */
    public void ackRecharge(String gateway, String ackId, String recipient, String amount, String soldeSim) {
        List<Payment> p = paymentRepository.findByRecipientAndAmountAndGatewayAndConfirmedOrderByIdAsc(recipient, Double.parseDouble(amount), gateway, false);
        if (p.isEmpty())
            return;
        Payment payment = p.get(0);
        payment.setConfirmed(true);
        paymentRepository.saveAndFlush(payment);

        updateSimSolde(gateway, soldeSim);
    }

    public void updateSimSolde(String gatewayId, String newSolde) {
        Optional<Sim> sim = simRepository.findByGateway(gatewayId);
        if (!sim.isPresent())
            return;
        Sim s = sim.get();
        s.setBalance(Float.parseFloat(newSolde));
        simRepository.save(s);
    }

    public void countPaymentByOp() {
    }

    public boolean relancePayment(String text) throws Exception {
        Optional<Payment> p = paymentRepository.findById(Long.parseLong(text));
        if (!p.isPresent())
            return false;
        Payment payment = p.get();

        return payment.execute();
    }

    public boolean confirmerPayment(String text) throws Exception {
        Optional<Payment> p = paymentRepository.findById(Long.parseLong(text));
        if (!p.isPresent())
            return false;

        Payment payment = p.get();
        payment.setStatus(OutboundMessage.SentStatus.Sent);
        paymentRepository.saveAndFlush(payment);

        return true;
    }

    public void doSmsAction(InMessage msg, String recipient, String sendMontant, String option) {
        Optional<GatewayDefinition> gd = BeanLocator.find(GatewayDefinitionRepository.class).findByGatewayId(msg.getGatewayId());
        if(!gd.isPresent())
            return;
        if(gd.get().getCommand() != null && gd.get().getCommand().getType().equals(CmdType.AUTRUI))
            return;

        String payer = msg.getAddress().replace("223", "");

        try {
            Optional<Client> sender = BeanLocator.find(ClientRepository.class).findByNumero(payer);
            if (!sender.isPresent())
                throw new Exception("Vous devez etre inscris pour effectuer cette operation");
            Client client = sender.get();

            NumProfile recipientProfile = NumProfile.CF;

            if("1".equals(option))
                recipientProfile = NumProfile.RV;
            if("2".equals(option))
                recipientProfile = NumProfile.GR;

            Carrier recipientNetwork = AppConstants.getOperator(recipient);

            Optional<Command> c = BeanLocator.find(CommandRepository.class).
                    findFirstByCarrierAndProfileAndType(recipientNetwork, NumProfile.getParent(recipientProfile), CmdType.RECHARGE);
            Command command = c.get();
            Double amount = Double.parseDouble(sendMontant);

            Payment sendCreditPayment = new Payment(msg.getId(), command, recipientProfile,
                    msg.getGatewayId(), client.getNumero(), recipient, amount);
            sendCreditPayment.setNom("ENVOIS");
            sendCreditPayment.setAgentNetwork(AppConstants.getOperator(client.getNumero()));
            sendCreditPayment.setAgentProfile(NumProfile.RVI);
            sendCreditPayment.setRecipientNetwork(recipientNetwork);
            sendCreditPayment.setClient(client);

            BeanLocator.find(AppCallbackManager.class).registerPaymentMessageEvent(msg, sendCreditPayment);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            String txt = "Echec de l'envois merci d'appeler le service client au 70047171 ou 95151010";
            BeanLocator.find(AirtimeService.class).sendNotification(payer, txt);
        }
    }

    public void buyIsago(InMessage msg, String compteur, String creditIsago) {
        try{
            Optional<Client> cl = BeanLocator.find(ClientRepository.class).findByNumero(msg.getAddress());
            Client client = cl.get();

            List<Command> c = BeanLocator.find(CommandRepository.class).
                    findByProfileAndModeExecution(NumProfile.ISAGO, CmdMode.SMS);
            if (c.isEmpty())
                throw new Exception("Command isago not found");
            Command command = c.get(0);
            Payment sendCreditPayment = new Payment(msg.getId(), command, NumProfile.ISAGO,
                    msg.getGatewayId(), msg.getAddress(), compteur, Double.parseDouble(creditIsago));
            sendCreditPayment.setClient(client);

            BeanLocator.find(AppCallbackManager.class).registerPaymentMessageEvent(msg, sendCreditPayment);

            String txt = "Votre demande est en cours de traitement, merci de consulter les sms ISAGO";
            BeanLocator.find(AirtimeService.class).sendNotification(client.getNumero(), txt);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateIsagoNumber(String nationalAddress, String compteur) {
        Optional<Client> cl = BeanLocator.find(ClientRepository.class).findByNumero(nationalAddress);
        Client client = cl.get();

        List<MobileNumber> numbers = BeanLocator.find(MobileNumberRepository.class).findByClient(client);

        boolean found = false;
        for (MobileNumber number : numbers) {
            if(number.getProfile().equals(NumProfile.ISAGO) && number.isDefaultNumber()){
                number.setNumber(compteur);
                BeanLocator.find(MobileNumberRepository.class).save(number);
                found = true;
            }
        }
        if(!found){
            MobileNumber mobileNumber = new MobileNumber(NumProfile.ISAGO, Carrier.ORANGE, compteur);
            mobileNumber.setDefaultNumber(true);
            mobileNumber.setPrioriter(1);
            mobileNumber.setClient(client);
            BeanLocator.find(MobileNumberRepository.class).save(mobileNumber);
        }
    }
}
