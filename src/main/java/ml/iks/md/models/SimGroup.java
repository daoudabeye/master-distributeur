package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;

import javax.persistence.*;
import java.util.List;

@Entity
public class SimGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private NumProfile profile;

    @OneToMany
    private List<Sim> sims;

    @Enumerated
    private Carrier target;

    public SimGroup() {}

    public SimGroup(String name, NumProfile profile, Carrier target) {
        this.name = name;
        this.profile = profile;
        this.target = target;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Sim> getSims() {
        return sims;
    }

    public void setSims(List<Sim> sims) {
        this.sims = sims;
    }

    public NumProfile getProfile() {
        return profile;
    }

    public void setProfile(NumProfile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return this.name + "["+ profile + "]";
    }

    public Carrier getTarget() {
        return target;
    }

    public void setTarget(Carrier target) {
        this.target = target;
    }
}
