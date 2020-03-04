package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Sim implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String gateway;

    private boolean status;

    @Enumerated
    private Carrier operator;

    @ManyToOne(optional = true)
    private SimGroup group;

    @ManyToOne
    private Command command;

    private String number;

    private float balance;

    private String smsc;

    private String pin;

    public Sim(String name, String gateway, boolean status, Carrier operator,
               SimGroup group, String number, float balance, String smsc, String pin, Command cmd) {
        this.name = name;
        this.gateway = gateway;
        this.status = status;
        this.operator = operator;
        this.group = group;
        this.number = number;
        this.balance = balance;
        this.smsc = smsc;
        this.pin = pin;
        this.command = cmd;
    }

    public void update(String name, String gateway, boolean status, Carrier operator,
                       SimGroup group, String number, float balance, String smsc, String pin, Command cmd){
        this.name = name;
        this.gateway = gateway;
        this.status = status;
        this.operator = operator;
        this.number = number;
        this.balance = balance;
        this.smsc = smsc;
        this.pin = pin;
        this.group = group;
        this.command = cmd;
    }

    public Sim(){}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public boolean isActive() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Carrier getOperator() {
        return operator;
    }

    public void setOperator(Carrier operator) {
        this.operator = operator;
    }

    public SimGroup getGroup() {
        return group;
    }

    public void setGroup(SimGroup group) {
        this.group = group;
    }

    public String getSmsc() {
        return smsc;
    }

    public void setSmsc(String smsc) {
        this.smsc = smsc;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
