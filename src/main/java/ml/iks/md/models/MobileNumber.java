package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Carrier operator;

//    @Pattern(regexp="^[a-zA-Z0-9]{8}",message="Mobile Number incorrect")
    private String number;

    private Date dateCreation;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    private NumProfile profile;

    private boolean defaultNumber;

    //priority on number selection
    //0 every default gateway
    //1 - n only selected gateway
    private Integer prioriter = 0;

    public MobileNumber(NumProfile profile, Carrier operator, String number) {
        this.profile = profile;
        this.dateCreation = new Date();
        this.operator = operator;
        this.number = number;
        this.prioriter = 0;
    }

    public MobileNumber() {
        this.dateCreation = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrier getOperator() {
        return operator;
    }

    public void setOperator(Carrier operator) {
        this.operator = operator;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isDefaultNumber() {
        return defaultNumber;
    }

    public void setDefaultNumber(boolean defaultNumber) {
        this.defaultNumber = defaultNumber;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getPrioriter() {
        return prioriter;
    }

    public void setPrioriter(Integer priority) {
        this.prioriter = priority;
    }

    public void setPrioriter(String priority) {
        try {
            this.prioriter = Integer.valueOf(priority);
        }catch (Exception e){
            this.prioriter= 0;
        }
        if(prioriter == null)
            prioriter = 0;
    }

    public NumProfile getProfile() {
        return profile;
    }

    public void setProfile(NumProfile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return getNumber() + "["+ operator +"]" + "["+ profile +"]";
    }

}
