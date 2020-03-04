package ml.iks.md.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class RoutingTable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String name;

    private String addressRegEx;

    private String profile;

    private Integer priority;

    @ManyToMany
    @JoinTable(
            name = "route",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "gateway_id"))
    private Set<GatewayDefinition> gateways;

    public RoutingTable(){}

    public RoutingTable(String addressRegEx, GatewayDefinition gateway) {
        this.addressRegEx = addressRegEx;
        this.gateways = new HashSet<>();
        this.gateways.add(gateway);
    }

    public RoutingTable(String name, String addressRegEx, Integer priority, String profile) {
        this.addressRegEx = addressRegEx;
        this.gateways = new HashSet<>();
        this.name = name;
        this.priority = priority;
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public String getAddressRegEx() {
        return addressRegEx;
    }

    public void setAddressRegEx(String addressRegEx) {
        this.addressRegEx = addressRegEx;
    }

    public Set<GatewayDefinition> getGateways() {
        return gateways;
    }

    public void setGateways(Set<GatewayDefinition> gateways) {
        this.gateways = gateways;
    }

    public void addGateway(GatewayDefinition gatewayDefinition){
        if(gateways == null)
            this.gateways = new HashSet<>();

        this.gateways.add(gatewayDefinition);
        gatewayDefinition.getRoutes().add(this);
    }

    public void removeGateway(GatewayDefinition gatewayDefinition){
        this.gateways.remove(gatewayDefinition);
        gatewayDefinition.getRoutes().remove(this);
    }

    public List<String> getIds(){
        List<String> ids = new ArrayList<>();
        if(gateways != null){
            gateways.forEach(gatewayDefinition -> {
                ids.add(gatewayDefinition.getGatewayId());
            });
        }
        return ids;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
