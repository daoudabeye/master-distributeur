package ml.iks.md.models;

import ml.iks.md.infra.model.CommandBuilder;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.CmdType;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.*;
import ml.iks.md.service.AirtimeService;
import ml.iks.md.util.AppConstants;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.Service;
import ml.ikslib.gateway.message.MsIsdn;
import ml.ikslib.gateway.message.OutboundMessage;
import ml.ikslib.gateway.ussd.USSDRequest;
import ml.ikslib.gateway.ussd.USSDResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;

import static ml.iks.md.util.AppConstants.PATTERN_PAIEMENT_AUTRUI;
import static ml.iks.md.util.AppConstants.PATTERN_PAIEMENT_AUTRUI_2;

/**
 * @author BEYE
 * Le (?<n>[0-9]+) a confirme son paiement de (?<m>[0-9]+(?:\\.[0-9]{1,2})?) FCFA. Votre nouveau solde est de: (?<s>[0-9]+(?:\\.[0-9]{1,2})?) FCFA.
 */
@Entity
public class Payment extends AbstractOperation {

    private static final Logger log = LoggerFactory.getLogger(Payment.class);

    private Long messageId;
    private String gateway; // Agent Gtw
    private String outGateway; // Out gateway
    private String transactionId;
    private String payer; //Payer number
    @Enumerated(EnumType.STRING)
    private NumProfile agentProfile; // agent profile
    @Enumerated(EnumType.STRING)
    private NumProfile recipientProfile; // receipient profile
    @Enumerated(EnumType.STRING)
    private Carrier agentNetwork; //
    @Enumerated(EnumType.STRING)
    private Carrier recipientNetwork; //
    private String recipient; // recipient number
    private double amount;
    private double frais;
    @Enumerated(EnumType.STRING)
    OutboundMessage.SentStatus status;
    private String failureCause;
    private String response;
    private String payerName;
    private String payerAddress;
    private int relance = 0;
    private boolean confirmed = false;
    @Enumerated(EnumType.STRING)
    private CmdType commandType;

    @Transient
    Client client;
    @Transient
    GatewayDefinition gd;
    @Transient
    Command command;
    @Transient
    int order = 0;

    public Payment() {
        super("PAYMENT");
    }

    /**
     * Constructor for payment operation
     *
     * @param messageId     database message Id
     * @param gatewayId     gateway Id
     * @param transactionId transaction id inside message
     * @param payer         number who send âyment
     * @param amount        payment amount
     * @param frais         payment fee
     */
    public Payment(Long messageId, String gatewayId, String transactionId, String payer, double amount, double frais) {
        super("PAYMENT");
        this.messageId = messageId;
        this.gateway = gatewayId;
        this.transactionId = transactionId;
        this.payer = payer;
        this.amount = amount;
        this.frais = frais;
    }

    public int getRelance() {
        return relance;
    }

    /**
     * Initialize command to execute when gateway receive payment
     *
     * @return command to execute
     * @throws NoSuchElementException command not found exception
     */
    private Command initCommand() throws NoSuchElementException {
        Optional<GatewayDefinition> definition = BeanLocator.find(GatewayDefinitionRepository.class).findByGatewayId(gateway);
        if (!definition.isPresent())
            throw new NoSuchElementException("Gatewaye Incorrect");

        this.gd = definition.get();
        this.carrier = gd.getNetwork();
        this.agentNetwork = gd.getNetwork();

        Command command = gd.getCommand();

        if (command == null)
            throw new NoSuchElementException("Command non configurer");

        this.agentProfile = command.getProfile();
        this.recipientNetwork = command.getCarrier();
        this.recipientProfile = NumProfile.getChild(agentProfile);
        return command;
    }

    @Override
    public boolean execute() throws Exception {
        failureCause = "";
        if (confirmed)
            return true;
        try {

            if (command == null) {
                command = initCommand();
                commandType = command.getType();
            }

            if (StringUtils.isEmpty(getNom()) && command != null)
                this.setNom(command.getType().name());

            assert command != null;

            switch (command.getType()) {
                case UV:
                    Optional<Client> cl = BeanLocator.find(ClientRepository.class).findByNumero(payer);
                    client = cl.get();
                    List<MobileNumber> rviNumber = BeanLocator.find(MobileNumberRepository.class).
                            findByClientAndProfileAndNumber(client, NumProfile.RVI, payer);
                    double commission = 0;
                    if (!rviNumber.isEmpty()) commission = ((amount * 5) / 100);

                    client.setBalance(client.getBalance() + amount + commission);
                    BeanLocator.find(ClientRepository.class).save(client);
                    this.outGateway = "UV";
                    this.status = OutboundMessage.SentStatus.Sent;
                    this.agentNetwork = AppConstants.getOperator(payer);
                    this.recipientNetwork = Carrier.NONE;
                    this.agentProfile = NumProfile.RVI;

                    String txt = "Recharge effectuée, votre nouveau solde est de " + client.getBalance();
                    BeanLocator.find(AirtimeService.class).sendNotification(payer, txt);
                    break;

                case AUTRUI:
                    if (relance < 1) {
                        List<InMessage> inMessages = BeanLocator.find(InMessageRepository.class).findByAddressAndReceiveDate("223" + payer, false, new Date());
                        recipient = "";
                        amount = 0.0;
                        boolean mtached = false;
                        String option = "";
                        for (InMessage inMessage : inMessages) {
                            Matcher matchPaiementAutrui = PATTERN_PAIEMENT_AUTRUI.matcher(inMessage.getText());
                            Matcher matchPaiementAutrui2 = PATTERN_PAIEMENT_AUTRUI_2.matcher(inMessage.getText());

                            if (matchPaiementAutrui.find()) {
                                recipient = matchPaiementAutrui.group(1);
                                amount = Double.parseDouble(matchPaiementAutrui.group(2));
                                option = matchPaiementAutrui.group(3);
                                mtached = true;
                            } else if (matchPaiementAutrui2.find()) {
                                String numeroPaiement = matchPaiementAutrui.group(1);
                                recipient = matchPaiementAutrui.group(2);
                                amount = Double.parseDouble(matchPaiementAutrui.group(3));
                                option = matchPaiementAutrui.group(4);
                                mtached = true;
                            }

                            if (mtached) {
                                inMessage.setLus(true);
                                BeanLocator.find(InMessageRepository.class).save(inMessage);
                            }
                        }
                        if (!mtached) {
                            failureCause = "Aucun sms autrui...";
                            break;
                        }

                        recipientNetwork = AppConstants.getOperator(recipient);
                        recipientProfile = null;

                        if ("1".equals(option))
                            recipientProfile = NumProfile.CF;
                        if ("2".equals(option))
                            recipientProfile = NumProfile.RV;
                        if ("3".equals(option))
                            recipientProfile = NumProfile.GR;

                        Optional<Command> c = BeanLocator.find(CommandRepository.class).
                                findFirstByCarrierAndProfileAndType(recipientNetwork, NumProfile.getParent(recipientProfile), CmdType.RECHARGE);
                        if(!c.isPresent())
                            this.failureCause = "Commande invalide !!";

                        command = c.get();
                        this.setNom("AUTRUI");

                        Optional<Client> sender = BeanLocator.find(ClientRepository.class).findByNumero(payer);
                        try {
                            client = sender.get();
                        } catch (Exception ignored) {
                        }
                    }

                case MOBICASH_DIRECT:
                case RECHARGE:
                    if (command.getFrais() > 0) {
                        frais = (amount * command.getFrais()) / 100;
                        amount = amount - frais;
                    } else
                        frais = 0;

                    if (command.getProfile().equals(NumProfile.MST))
                        amount = amount * 1000;

                    CommandBuilder creditCmd = new CommandBuilder(command)
                            .withAmount(amount)
                            .withFee(frais)
                            .withId(transactionId);

                    int order = gd == null ? 0 : gd.getOrder();
                    if (StringUtils.isEmpty(recipient)){
                        try {
                            recipient = initRecipient(NumProfile.getChild(command.getProfile()), command.getCarrier(), order);
                        } catch (NoSuchElementException noSuchEx){
                            if (StringUtils.isEmpty(recipient))
                                recipient = getNonRegisterRecipient(payer, NumProfile.getChild(command.getProfile()), gd.getNetwork());

                            if (StringUtils.isEmpty(recipient))
                                throw new Exception("Echec de l'operation");
                        }
                    }

                    if (recipientNetwork == null)
                        recipientNetwork = AppConstants.getOperator(recipient);

                    creditCmd.withNumero(recipient);
                    log.debug(creditCmd.build());
                    System.out.println(creditCmd.build());

                    USSDRequest ussdRequest = new USSDRequest(creditCmd.build());

                    if (command.getProfile().equals(NumProfile.GR) && recipientNetwork.equals(Carrier.TELECEL)) {
                        GatewayDefinition tclGtw = findGrTelecel(recipient);
                        if (tclGtw != null)
                            ussdRequest.setGatewayId(tclGtw.getGatewayId());
                        else {
                            this.failureCause = "Plage Grossiste Telecel Inconnue!";
                            break;
                        }
                    }

                    if ("ENVOIS".equals(this.getNom())) {
                        if (client.getBalance() < amount) {
                            this.failureCause = "Solde inssufisant ! Merci de recharger votre compte";
                            break;
                        }
                    }

                    USSDResponse ussdResponse = Service.getInstance().send(ussdRequest);
                    status = ussdRequest.getSentStatus();
                    if (ussdResponse != null)
                        response = ussdResponse.getContent();
                    if (status.equals(OutboundMessage.SentStatus.Sent)) {
                        this.dateExecution = new Date();
                        if ("ENVOIS".equals(this.getNom()))
                            debiteBalance();
                    } else if (status.equals(OutboundMessage.SentStatus.Failed) && ussdRequest.getFailureCause() != null) {
                        this.failureCause = ussdRequest.getFailureCause() + "";
                    }

                    if (ussdResponse != null)
                        this.outGateway = ussdResponse.getGatewayId();
                    break;

                case ISAGO:
                    if (StringUtils.isEmpty(recipient))
                        recipient = initRecipient(NumProfile.ISAGO, null, gd.getOrder());

                    CommandBuilder isagoCmd = new CommandBuilder(command)
                            .withAmount(amount)
                            .withFee(frais)
                            .withId(transactionId)
                            .withNumero(recipient)
                            .withNumClient(payer);

//                    if (client.getBalance() >= amount && !StringUtils.isEmpty(recipient)) {
//                        client.setBalance(client.getBalance() - amount);
//                        BeanLocator.find(ClientRepository.class).saveAndFlush(client);
//                    } else {
//                        this.failureCause = "Solde inssufisant !";
//                        break;
//                    }
                    String isagoaddr = command.getRecipientAddress();
                    OutboundMessage isagosms = new OutboundMessage(new MsIsdn(isagoaddr, MsIsdn.Type.National), isagoCmd.build());
                    Service.getInstance().send(isagosms);
                    status = isagosms.getSentStatus();
                    this.setNom(command.getName());
                    this.outGateway = isagosms.getGatewayId();
                    break;
            }
        } catch (Exception e) {
            if(!OutboundMessage.SentStatus.Queued.equals(status))
                status = OutboundMessage.SentStatus.Failed;
            this.failureCause = e.getLocalizedMessage();

//            AlertMaker.showTrayMessage("Echec Operation", failureCause);
            log.error("Erreur Payment", e);

            String txt = "Echec de la tentative de recharge, Merci de contacter le service client au : 70047171 ou 95151010";
            BeanLocator.find(AirtimeService.class).sendNotification(payer, txt);
        } finally {
            this.relance = relance + 1;
            BeanLocator.find(PaymentRepository.class).saveAndFlush(this);

            if("AUTRUI".equals(this.getNom()) && this.status == OutboundMessage.SentStatus.Sent){
                BeanLocator.find(AirtimeService.class).sendNotification(payer, "Le "+ recipient + " a ete recharger avec succès.");
            }

            if (!StringUtils.isEmpty(failureCause) && failureCause.length() < 100) {
                BeanLocator.find(AirtimeService.class).sendNotification(payer, "Echec de l'operation, Merci de contacter le service client au : 70047171 ou 95151010");
            }
        }

        return true;
    }

    private void debiteBalance() throws Exception {
        if (client.getBalance() >= amount && !StringUtils.isEmpty(recipient)) {
            client.setBalance(client.getBalance() - amount);
            client = BeanLocator.find(ClientRepository.class).saveAndFlush(client);
            double b = client.getBalance();
            if ("ENVOIS".equals(this.getNom())) {
                BeanLocator.find(AirtimeService.class).sendNotification(payer, "Envois de " + (int) amount + "CFA au " + recipient + ", " +
                        "votre nouveau solde : " + (int) b);
            }

        } else {
            throw new Exception("Solde inssufisant ! Merci de rceharger votre compte.");
        }
    }

    /**
     * find candidate number when client is not register
     *
     * @param payerNumber payer mobile number
     * @param numProfile  gateway command num profile for recipient
     * @param carrier     gateway carrier
     * @return selected non register client number
     * @throws NoSuchElementException
     */
    private String getNonRegisterRecipient(String payerNumber, NumProfile numProfile, Carrier carrier) throws NoSuchElementException {
        BeanLocator.find(AirtimeService.class).sendNotification(payerNumber, AppConstants.INVITE_NOTIF);

        if (NumProfile.CF.equals(numProfile)) {
            if (carrier.equals(AppConstants.getOperator(payerNumber))) {
                return payerNumber;
            }
        }
        throw new NoSuchElementException("Impossible d'effectuer cette operation:100");
    }

    /**
     * @param profile gateway command recipient profile
     * @param op      recipient carrier
     * @return candidate number
     * @throws NoSuchElementException number not found
     */
    private String initRecipient(NumProfile profile, Carrier op, int gatewayOrder) throws NoSuchElementException {
        Optional<Client> cl = BeanLocator.find(ClientRepository.class).findByNumero(payer);
        if (!cl.isPresent()){
            this.status = OutboundMessage.SentStatus.Queued;
            throw new NoSuchElementException("Numero de paiement incorrect !!");
        }

        client = cl.get();

        MobileNumber mobileNumber;
        List<MobileNumber> numeros;

        if (gatewayOrder <= 0) {
            if (op != null) {
                numeros = BeanLocator.find(MobileNumberRepository.class).
                        findByClientAndProfileAndOperatorAndDefaultNumberOrderByIdDesc(client, profile, op, true);
            } else {
                numeros = BeanLocator.find(MobileNumberRepository.class).
                        findByClientAndProfileAndDefaultNumberOrderByIdDesc(client, profile, true);
            }
        } else {
            numeros = BeanLocator.find(MobileNumberRepository.class).findByClientAndPrioriterAndProfileOrderByIdDesc(client, gatewayOrder, profile);
        }

        if (numeros.isEmpty())
            throw new NoSuchElementException("No default number for user");
        else
            mobileNumber = numeros.get(0);

        this.payerName = client.getNom();
        this.payerAddress = client.getAddress();

        return mobileNumber.getNumber();
    }

    GatewayDefinition findGrTelecel(String numeroClient) {
        List<GrPlage> p = BeanLocator.find(GrPlageRepository.class).find(Integer.valueOf(numeroClient));
        if (!p.isEmpty()) {
            GrPlage plage = p.get(0);
            GatewayDefinition gd = BeanLocator.find(GatewayDefinitionRepository.class).findBySenderId(plage.getNumero());
            return gd;
        }
        return null;
    }

    public String getPayer() {
        return payer;
    }

    public Long getMessageId() {
        return messageId;
    }

    public double getAmount() {
        return amount;
    }

    public double getFrais() {
        return frais;
    }

    public String getGateway() {
        return gateway;
    }

    public String getFailureCause() {
        return failureCause;
    }

    public String getResponse() {
        return response;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public NumProfile getAgentProfile() {
        return agentProfile;
    }

    public void setAgentProfile(NumProfile agentProfile) {
        this.agentProfile = agentProfile;
    }

    public NumProfile getRecipientProfile() {
        return recipientProfile;
    }

    public void setRecipientProfile(NumProfile recipientProfile) {
        this.recipientProfile = recipientProfile;
    }

    public Carrier getAgentNetwork() {
        return agentNetwork;
    }

    public void setAgentNetwork(Carrier agentNetwork) {
        this.agentNetwork = agentNetwork;
    }

    public Carrier getRecipientNetwork() {
        return recipientNetwork;
    }

    public void setRecipientNetwork(Carrier recipientNetwork) {
        this.recipientNetwork = recipientNetwork;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setFrais(double frais) {
        this.frais = frais;
    }

    public OutboundMessage.SentStatus getStatus() {
        if (status == null)
            return OutboundMessage.SentStatus.Unsent;
        return status;
    }

    public void setStatus(OutboundMessage.SentStatus status) {
        this.status = status;
    }

    public void setFailureCause(String failureCause) {
        this.failureCause = failureCause;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getPayerName() {
        return this.payerName;
    }

    public String getPayerAddress() {
        return this.payerAddress;
    }

    public String getOutGateway() {
        return this.outGateway;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public CmdType getCommandType() {
        return commandType;
    }

    public void setCommandType(CmdType commandType) {
        this.commandType = commandType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
