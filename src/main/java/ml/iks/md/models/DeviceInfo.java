package ml.iks.md.models;

import ml.ikslib.gateway.modem.Modem;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class DeviceInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gateway;
    private String mode = "N/A";
    private String manufacturer = "N/A";
    private String model = "N/A";
    private String swVersion = "N/A";
    private String serialNo = "N/A";
    private String imsi = "N/A";
    private int rssi = 0;
    private String encoding =  "N/A";
    private String smsc =  "N/A";
    private String status = "";

    public DeviceInfo() {
    }

    public DeviceInfo(Modem modem) {
        updateData(modem);
    }

    public void updateData(Modem modem){
        if(modem != null && modem.getDeviceInformation() != null){
            this.gateway = modem.getGatewayId();
            this.mode = modem.getDeviceInformation().getMode() != null ?
                    modem.getDeviceInformation().getMode().toString() : "N/A";
            this.manufacturer = modem.getDeviceInformation().getManufacturer();
            this.model = modem.getDeviceInformation().getModel();
            this.swVersion = modem.getDeviceInformation().getSwVersion();
            this.serialNo = modem.getDeviceInformation().getSerialNo();
            this.imsi = modem.getDeviceInformation().getImsi();
            this.rssi = modem.getDeviceInformation().getRssi();
            this.encoding = modem.getDeviceInformation().getEncoding();
            this.smsc = modem.getSmscNumber() != null ? modem.getSmscNumber().getAddress() : "N/A";
            this.status = modem.getStatus() != null ? modem.getStatus().name() : "N/A";
        }
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSmsc() {
        return smsc;
    }

    public void setSmsc(String smsc) {
        this.smsc = smsc;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
