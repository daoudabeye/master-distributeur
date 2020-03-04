package ml.iks.md.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "incoming")
public class Call implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gatewayId;
    private Date date;
    private String address;

    public Call(){
        super();
    }

    public Call(String gatewayId, Date date, String address) {
        this.gatewayId = gatewayId;
        this.date = date;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
