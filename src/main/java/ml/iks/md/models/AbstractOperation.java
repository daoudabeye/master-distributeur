package ml.iks.md.models;

import ml.iks.md.models.data.Carrier;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class AbstractOperation implements Serializable {
    public enum Declancheur{SMS, USER, TIMER}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Enumerated
    protected Declancheur  declancheur;
    protected Carrier carrier;
    protected Date dateCreation;
    protected Date dateExecution;

    public AbstractOperation(String name){
        this.nom = name;
        this.dateCreation = new Date();
    }

    protected boolean execute() throws Exception {
        return false;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom.toUpperCase();
    }

    public Declancheur getDeclancheur() {
        return declancheur;
    }

    public void setDeclancheur(Declancheur declancheur) {
        this.declancheur = declancheur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateExecution() {
        return dateExecution;
    }

    public void setDateExecution(Date dateExecution) {
        this.dateExecution = dateExecution;
    }

}
