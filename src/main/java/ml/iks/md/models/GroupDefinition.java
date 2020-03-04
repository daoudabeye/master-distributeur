package ml.iks.md.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
public class GroupDefinition implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    String name;

    String description;

    String profile;

    @OneToMany
    Collection<GroupRecipientDefinition> recipients;

    public GroupDefinition() {
    }

    public GroupDefinition(String name, String description, Collection<GroupRecipientDefinition> recipients) {
        this.name = name;
        this.description = description;
        this.recipients = recipients;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<GroupRecipientDefinition> getRecipients() {
        return recipients;
    }

    public void setRecipients(Collection<GroupRecipientDefinition> recipients) {
        this.recipients = recipients;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}

