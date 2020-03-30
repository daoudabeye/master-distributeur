package ml.iks.md.bean;

import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.service.ClientService;
import ml.iks.md.service.PaymentService;
import ml.iks.md.service.StatistiqueService;
import ml.iks.md.util.AppUtil;
import ml.ikslib.gateway.Service;
import ml.ikslib.gateway.message.OutboundMessage;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * Created by rmpestano on 12/02/17.
 */
@Named
@ViewScoped
public class StatistiqueMB implements Serializable {

    @Inject
    StatistiqueService statistiqueService;
    @Inject
    ClientService clientService;
    @Inject
    PaymentService paymentService;

    private Date from;
    private Date to;

    private Long montantTotal;
    private Long countTransactions;
    private Long countRechargeSent;
    private Long countRechargeUnsent;
    private Long retour;
    private Long countRetour;
    private Long commission;
    private Long totalOmCfOrange;
    private Long totalOmCfMalitel;
    private Long totalOmCfTelecel;
    private Long totalOmRvOrange;
    private Long totalOmRvMalitel;
    private Long totalOmRvTelecel;
    private Long totalMobiCfOrange;
    private Long totalMobiCfMalitel;
    private Long totalMobiCfTelecel;
    private Long totalMobiRvOrange;
    private Long totalMobiRvMalitel;
    private Long totalMobiRvTelecel;

    public void init() {
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (!has(from))
            startDate = LocalDate.now().atStartOfDay();
        else
            startDate = AppUtil.convertToLocalDateTime(from).toLocalDate().atStartOfDay();
        if(!has(to))
            endDate = LocalDate.now().atTime(LocalTime.MAX);
        else
            endDate = AppUtil.convertToLocalDateTime(to).toLocalDate().atTime(LocalTime.MAX);

        montantTotal = paymentService.sum("PAYMENT", startDate, endDate);
        countTransactions = paymentService.count("PAYMENT", startDate, endDate);

        countRechargeSent = paymentService.count(OutboundMessage.SentStatus.Sent, startDate, endDate);
        countRechargeUnsent = paymentService.count(OutboundMessage.SentStatus.Failed, startDate, endDate);

        totalOmCfOrange = paymentService.sum("PAYMENT", Carrier.ORANGE, Carrier.ORANGE, NumProfile.CF, startDate, endDate);
        totalOmCfMalitel = paymentService.sum("PAYMENT", Carrier.ORANGE, Carrier.MALITEL, NumProfile.CF, startDate, endDate);
        totalOmCfTelecel = paymentService.sum("PAYMENT", Carrier.ORANGE, Carrier.TELECEL, NumProfile.CF, startDate, endDate);
        totalOmRvOrange = paymentService.sum("PAYMENT", Carrier.ORANGE, Carrier.ORANGE, NumProfile.RV, startDate, endDate);
        totalOmRvMalitel = paymentService.sum("PAYMENT", Carrier.ORANGE, Carrier.MALITEL, NumProfile.RV, startDate, endDate);
        totalOmRvTelecel = paymentService.sum("PAYMENT", Carrier.ORANGE, Carrier.TELECEL, NumProfile.RV, startDate, endDate);

        totalMobiCfOrange = paymentService.sum("PAYMENT", Carrier.MALITEL, Carrier.ORANGE, NumProfile.CF, startDate, endDate);
        totalMobiCfMalitel = paymentService.sum("PAYMENT", Carrier.MALITEL, Carrier.MALITEL, NumProfile.CF, startDate, endDate);
        totalMobiCfTelecel =  paymentService.sum("PAYMENT", Carrier.MALITEL, Carrier.TELECEL, NumProfile.CF, startDate, endDate);
        totalMobiRvOrange = paymentService.sum("PAYMENT", Carrier.MALITEL, Carrier.ORANGE, NumProfile.RV, startDate, endDate);
        totalMobiRvMalitel = paymentService.sum("PAYMENT", Carrier.MALITEL, Carrier.MALITEL, NumProfile.RV, startDate, endDate);
        totalMobiRvTelecel = paymentService.sum("PAYMENT", Carrier.MALITEL, Carrier.TELECEL, NumProfile.RV, startDate, endDate);
    }

    //Dashboard

    public Long getCountClient(){
        return clientService.count();
    }

    public Long getCountCf(){
        return clientService.count(NumProfile.CF);
    }
    public Long getCountGr(){
        return clientService.count(NumProfile.GR);
    }

    public Long getCountRv(){
        return clientService.count(NumProfile.RV);
    }
    public Long getCountRvi(){
        return clientService.count(NumProfile.RVI);
    }

    public Integer getActiveGateway(){
        return Service.getInstance().getGatewayIDs().size();
    }

    public Long getCountPaiements(){
        return paymentService.count();
    }

    public Long getCountAgents(){
        return clientService.count(NumProfile.RVI);
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Long getMontantTotal() {
        return montantTotal;
    }

    public Long getCountTransactions() {
        return countTransactions;
    }

    public Long getCountRechargeSent() {
        return countRechargeSent;
    }

    public Long getCountRechargeUnsent() {
        return countRechargeUnsent;
    }

    public Long getRetour() {
        return retour;
    }

    public Long getCountRetour() {
        return countRetour;
    }

    public Long getCommission() {
        return commission;
    }

    public Long getTotalOmCfOrange() {
        return totalOmCfOrange;
    }

    public Long getTotalOmCfMalitel() {
        return totalOmCfMalitel;
    }

    public Long getTotalOmCfTelecel() {
        return totalOmCfTelecel;
    }

    public Long getTotalOmRvOrange() {
        return totalOmRvOrange;
    }

    public Long getTotalOmRvMalitel() {
        return totalOmRvMalitel;
    }

    public Long getTotalOmRvTelecel() {
        return totalOmRvTelecel;
    }

    public Long getTotalMobiCfOrange() {
        return totalMobiCfOrange;
    }

    public Long getTotalMobiCfMalitel() {
        return totalMobiCfMalitel;
    }

    public Long getTotalMobiCfTelecel() {
        return totalMobiCfTelecel;
    }

    public Long getTotalMobiRvOrange() {
        return totalMobiRvOrange;
    }

    public Long getTotalMobiRvMalitel() {
        return totalMobiRvMalitel;
    }

    public Long getTotalMobiRvTelecel() {
        return totalMobiRvTelecel;
    }
}
