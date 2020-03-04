package ml.iks.md.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class GroupRecipientDefinition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    String address;

    @ManyToOne
    GroupRecipientDefinition groupRecipientDefinition;

    public GroupRecipientDefinition() {
    }

    public GroupRecipientDefinition(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }

    public GroupRecipientDefinition getGroupRecipientDefinition() {
        return groupRecipientDefinition;
    }

    public void setGroupRecipientDefinition(GroupRecipientDefinition groupRecipientDefinition) {
        this.groupRecipientDefinition = groupRecipientDefinition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
