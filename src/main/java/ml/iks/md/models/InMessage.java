package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.IncomingMessageType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class InMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String encoding;
    private String text;
    private Date messageDate;
    private Date receiveDate;
    private String gatewayId;
    private Carrier carrier;
    private int memIndex;
    private boolean lus = false; // lus par autrui

    @Enumerated
    private IncomingMessageType messageType;

    //Fill when new sms received
    @Transient
    private Sim sim;

    public InMessage() {}

    public InMessage(String address, String encoding, String text, Date messageDate, Date receiveDate, String gatewayId, Carrier carrier, int memIndex) {
        this.address = address;
        this.encoding = encoding;
        this.text = text;
        this.messageDate = messageDate;
        this.receiveDate = receiveDate;
        this.gatewayId = gatewayId;
        this.carrier = carrier;
        this.memIndex = memIndex;
        this.lus = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public String getNationalAddress() {
        return address.replace("223", "");
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Sim getSim() {
        return sim;
    }

    public void setSim(Sim sim) {
        this.sim = sim;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public int getMemIndex() {
        return memIndex;
    }

    public void setMemIndex(int memIndex) {
        this.memIndex = memIndex;
    }

    public boolean isLus() {
        return lus;
    }

    public void setLus(boolean lus) {
        this.lus = lus;
    }

    public IncomingMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(IncomingMessageType messageType) {
        this.messageType = messageType;
    }
}
