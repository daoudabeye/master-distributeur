package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.DeliveryStatus;
import ml.iks.md.util.AppConstants;
import ml.iks.md.util.AppUtil;
import ml.ikslib.gateway.message.OutboundMessage;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class OutMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderAddress;
    private String address;
    private String text;
    private String encoding;
    private Integer priority;
    private Boolean flashSms;
    private String gatewayId;
    private String messageId;
    private String operatorMessageId;
    private Integer parentId;
    private Date sentDate;
    private Date createDate;
    private Date deliveryDate;
    private Boolean requestDeliveryReport;
    private String deliveryStatus;
    private String sentStatus;

    public OutMessage(){}

    public OutMessage(String senderAddress, String address, String text, String encoding, Integer priority, Boolean flashSms, String gatewayId,
                      String messageId, String operatorMessageId, Integer parentId, Date sentDate, Date createDate,
                      Date deliveryDate, Boolean requestDeliveryReport, DeliveryStatus String, String sentStatus) {
        this.senderAddress = senderAddress;
        this.address = address;
        this.text = text;
        this.encoding = encoding;
        this.priority = priority;
        this.flashSms = flashSms;
        this.gatewayId = gatewayId;
        this.messageId = messageId;
        this.operatorMessageId = operatorMessageId;
        this.parentId = parentId;
        this.sentDate = sentDate;
        this.createDate = createDate;
        this.deliveryDate = deliveryDate;
        this.requestDeliveryReport = requestDeliveryReport;
        this.deliveryStatus = deliveryStatus;
        this.sentStatus = sentStatus;
    }

    public OutMessage(OutboundMessage out) {
        this.senderAddress = out.getOriginatorAddress().getAddress();
        this.address = out.getRecipientAddress().getAddress();
        this.text = out.getPayload().getText();
        this.encoding = out.getEncoding().toShortString();
        this.priority = out.getPriority();
        this.flashSms = out.isFlashSms();
        this.gatewayId = out.getGatewayId();
        this.messageId = out.getId();
        try{
            this.operatorMessageId = out.getOperatorMessageId();
        }catch (Exception ignored){}
        this.parentId = 0;
        this.sentDate = out.getSentDate();
        this.createDate = out.getCreationDate();
        this.deliveryDate = null;
        this.requestDeliveryReport = out.getRequestDeliveryReport();
        this.deliveryStatus = null;
        this.sentStatus = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getFlashSms() {
        return flashSms;
    }

    public void setFlashSms(Boolean flashSms) {
        this.flashSms = flashSms;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getOperatorMessageId() {
        return operatorMessageId;
    }

    public void setOperatorMessageId(String operatorMessageId) {
        this.operatorMessageId = operatorMessageId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Boolean getRequestDeliveryReport() {
        return requestDeliveryReport;
    }

    public void setRequestDeliveryReport(Boolean requestDeliveryReport) {
        this.requestDeliveryReport = requestDeliveryReport;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(String sentStatus) {
        this.sentStatus = sentStatus;
    }

    public Carrier getCarrier() {
        return AppConstants.getOperator(getAddress());
    }
}
