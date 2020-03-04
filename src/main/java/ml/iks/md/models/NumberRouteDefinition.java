package ml.iks.md.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class NumberRouteDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    String addressRegex;

    String gatewayId;

    String profile;

    public NumberRouteDefinition(){}

    public NumberRouteDefinition(String addressRegex, String gatewayId) {
        this.addressRegex = addressRegex;
        this.gatewayId = gatewayId;
    }

    public String getAddressRegex() {
        return addressRegex;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAddressRegex(String addressRegex) {
        this.addressRegex = addressRegex;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


}
