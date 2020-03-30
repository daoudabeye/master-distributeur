package ml.iks.md.models;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 200, message = "Name length must be at least 3 and max 200")
    private String nom;

    @Size(min = 8, max = 50, message = "Number length must be at least 8 and max 50")
    @Column(unique = true)
    private String numero;

    @Size(max = 4, message = "Pin length max 4")
    private String pin = "0000";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<MobileNumber> numbers;

    @Size(min = 3, max = 200, message = "Address length must be at least 3 and max 200")
    private String address;

    private double balance = 0.0;
    private boolean blackliste = false;

    private String numeroInscripteur;
    private Date dateInscription;

//    @Formula("(select sum(p.amount) from payment p where p.payer like numero)")
//    private Long totalClient;

    @Formula("(select sum(p.amount) from payment p where p.payer like numero)")
    private Long total = 0L;

    @Formula("(select count(*) from client c where c.numero_inscripteur like numero)")
    private Long inscris;

    public Client(){
        this.dateInscription = new Date();
    }

    public Client(String nom, String numero, String inscripteur) {
        this.nom = nom;
        this.numero = numero;
        this.numeroInscripteur = inscripteur;
        this.dateInscription = new Date();
        this.address = "";
        this.pin = "0000";
        this.balance = 0.0;
        this.blackliste = false;
    }

    public Long getId() {
        return id;
    }

    public void addNumber(MobileNumber number){
        if(this.numbers == null)
            this.numbers = new ArrayList<>();

        this.numbers.add(number);
    }

    public Collection<MobileNumber> getNumbers() {
        return numbers;
    }

    public void setNumbers(Collection<MobileNumber> numbers) {
        this.numbers = numbers;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Boolean getBlackliste() {
        return blackliste;
    }

    public void setBlackliste(Boolean blackliste) {
        this.blackliste = blackliste;
    }

    public String getNumeroInscripteur() {
        return numeroInscripteur;
    }

    public void setNumeroInscripteur(String numeroInscripteur) {
        this.numeroInscripteur = numeroInscripteur;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getInscris() {
        return inscris;
    }

    public void setInscris(Long inscris) {
        this.inscris = inscris;
    }
}
