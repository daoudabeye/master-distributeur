package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class GatewayDefinition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    String className = "ml.ikslib.gateway.modem.Modem";

    String gatewayId;

    String p0, p1, p2, p3, p4, p5;

    String senderId;

    int priority; //priority in starting order

    int maxMessageParts;

    boolean requestDeliveryReport;

    String profile;

    boolean enabled;

    @ManyToMany(mappedBy = "gateways")
    Set<RoutingTable> routes;

    @Enumerated(EnumType.STRING)
    private Carrier network;

    @ManyToOne
    private Command command;

    // order in client number select
    // 0 select client default number
    //1 - n select number from order

    private int ordre = 0;

    public GatewayDefinition(){}

    public GatewayDefinition(String className, String gatewayId, String p0, String p1, String p2, String p3, String p4,
                             String p5, String senderId, int priority, int maxMessageParts, boolean requestDeliveryReport) {
        this.className = className;
        this.gatewayId = gatewayId;
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.senderId = senderId;
        this.priority = priority;
        this.maxMessageParts = maxMessageParts;
        this.requestDeliveryReport = requestDeliveryReport;
    }

    public GatewayDefinition(String className, String gatewayId, String p0, String p1, String p2, String p3, String p4,
                             String p5, String senderId, int priority, int maxMessageParts, boolean requestDeliveryReport,
                             String profile, boolean  enabled) {
        this.className = className;
        this.gatewayId = gatewayId;
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.senderId = senderId;
        this.priority = priority;
        this.maxMessageParts = maxMessageParts;
        this.requestDeliveryReport = requestDeliveryReport;
        this.profile = profile;
        this.enabled = enabled;
    }

    public String getClassName() {
        return className;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getP0() {
        return p0;
    }

    public String getP1() {
        return p1;
    }

    public String getP2() {
        return p2;
    }

    public String getP3() {
        return p3;
    }

    public String getP4() {
        return p4;
    }

    public String getP5() {
        return p5;
    }

    public String getSenderId() {
        return senderId;
    }

    public int getPriority() {
        return priority;
    }

    public int getMaxMessageParts() {
        return maxMessageParts;
    }

    public boolean getRequestDeliveryReport() {
        return requestDeliveryReport;
    }

    public String getProfile() {
        return profile;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public void setP0(String p0) {
        this.p0 = p0;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public void setP3(String p3) {
        this.p3 = p3;
    }

    public void setP4(String p4) {
        this.p4 = p4;
    }

    public void setP5(String p5) {
        this.p5 = p5;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setMaxMessageParts(int maxMessageParts) {
        this.maxMessageParts = maxMessageParts;
    }

    public boolean isRequestDeliveryReport() {
        return requestDeliveryReport;
    }

    public void setRequestDeliveryReport(boolean requestDeliveryReport) {
        this.requestDeliveryReport = requestDeliveryReport;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<RoutingTable> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<RoutingTable> routes) {
        this.routes = routes;
    }

    public Carrier getNetwork() {
        return network;
    }

    public void setNetwork(Carrier network) {
        this.network = network;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public int getOrder() {
        return ordre;
    }

    public void setOrder(int order) {
        this.ordre = order;
    }

    @Override
    public String toString() {
        return gatewayId;
    }
}
