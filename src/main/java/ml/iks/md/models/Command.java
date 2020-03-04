package ml.iks.md.models;


import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.CmdMode;
import ml.iks.md.models.data.CmdType;
import ml.iks.md.models.data.NumProfile;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "command")
public class Command implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Carrier carrier;
    //profile qui execute l'operation
    @Enumerated(EnumType.STRING)
    private NumProfile profile;
//    //profile qui beneficie de l'operation
//    @Enumerated(EnumType.STRING)
//    private NumProfile benef;
    @Enumerated(EnumType.STRING)
    private CmdType type;
    @Enumerated(EnumType.STRING)
    private CmdMode modeExecution;
    private int frais = 0;
    private String recipientAddress;
    private String name;
    private String pattern;

    public Command(){}

    public Command(String name, String pattern, NumProfile profile, CmdType type) {
        this.name = name;
        this.pattern = pattern;
        this.profile = profile;
        this.type = type;
//        this.benef = benef;
    }

    /**
     *
     * @param numero
     * @param montant
     * @param frais
     * @param id
     * @param numeTelClient
     * @return
     */
    public String build(String numero, String montant, String frais, String id, String numeTelClient) {
        String p = pattern;
        p = p.replaceAll("numero", numero);
        p = p.replaceAll("montant", montant);
        p = p.replaceAll("frais", frais);
        p = p.replaceAll("id", id);
        p = p.replaceAll("numClient", numeTelClient);

        return p;
    }

    public Long getId() {
        return id;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public NumProfile getProfile() {
        return profile;
    }

    public void setProfile(NumProfile profile) {
        this.profile = profile;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public CmdType getType() {
        return type;
    }

    public void setType(CmdType type) {
        this.type = type;
    }

    public CmdMode getModeExecution() {
        return modeExecution;
    }

    public void setModeExecution(CmdMode modeExecution) {
        this.modeExecution = modeExecution;
    }

    public int getFrais() {
        return frais;
    }

    public void setFrais(int frais) {
        this.frais = frais;
    }

    @Override
    public String toString() {
        return this.id + "";
    }

    public String toLongString() {
        return name + "["+profile+"]"+ "["+carrier+"]"+ "["+type+"]";
    }
}
