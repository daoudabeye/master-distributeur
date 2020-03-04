package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;

import javax.persistence.*;

@Entity
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Carrier carrier;
    private Integer part;

    public Commission() {}

    public Commission(Carrier carrier, Integer value) {
        this.carrier = carrier;
        this.part = value;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public Integer getPart() {
        return part;
    }

    public void setPart(Integer part) {
        this.part = part;
    }
}
